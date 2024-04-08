package pze.ui.formulare.freigabecenter;

import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.CoMessage;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.CoLandDienstreise;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.business.objects.reftables.personen.CoPosition;
import pze.ui.formulare.person.DialogDienstreise;



/**
 * Formular für das Freigabecenter für AL
 * 
 * @author Lisiecki
 *
 */
public class FormFreigabecenterAl extends AbstractFormFreigabecenter {
	
	protected static final String CAPTION = "Abteilungsleiter"; // TODO wo speichern?

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param formFreigabecenter 
	 * @throws Exception
	 */
	public FormFreigabecenterAl(Object parent, FormFreigabecenter formFreigabecenter) throws Exception {
		super(parent, formFreigabecenter, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AL, CAPTION);
	}

	
	@Override
	protected void loadAntraegeOfa(boolean zeitraum) throws Exception {
		m_coBuchung.loadAntraegeAl(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AUSBILDER, 
				CoStatusGenehmigung.STATUSID_FREIGEGEBEN_VERTRETER, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB, zeitraum, true, false);
	}

	
	@Override
	protected void loadAntraegeUrlaub(boolean zeitraum) throws Exception {
		m_coBuchungUrlaub.loadAntraegeAl(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AUSBILDER, 
				CoStatusGenehmigung.STATUSID_FREIGEGEBEN_VERTRETER, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB, zeitraum, false, false);
	}


	@Override
	protected void loadAntraegeDr(boolean zeitraum) throws Exception {
//		CoFreigabeberechtigungen coFreigabeberechtigungen;
//		
//		coFreigabeberechtigungen = CoFreigabeberechtigungen.getInstance();
//		coFreigabeberechtigungen.moveToPersonID(UserInformation.getPersonID());

		m_coBuchungDr.loadAntraegeAl(null, null, CoStatusGenehmigung.STATUSID_BEANTRAGT, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AUSBILDER, 
//				coFreigabeberechtigungen.isDrAuslandFreigabeErlaubt() ? CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AL : 0,
				CoStatusGenehmigung.STATUSID_FREIGEGEBEN_VERTRETER, CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB, zeitraum, false, true);
	}
	

	@Override
	protected void loadAntraegeBearbeitet() throws Exception {
		m_coBuchungBearbeitet.loadAntraegeAl(m_tfDatumVon.getField().getDateValue(), m_tfDatumBis.getField().getDateValue(), 
				CoStatusGenehmigung.STATUSID_GENEHMIGT, CoStatusGenehmigung.STATUSID_ABGELEHNT, 0, 0, false, false, false);
	}


	/**
	 * Bei der 
	 * @throws Exception 
	 */
	@Override
	protected boolean checkBeforeFreigabe(CoBuchung coBuchung) throws Exception {
		String bemerkung;
		CoDienstreise coDienstreise;
		
		// nur bei DR gibt es eine Prüfung
		if (!CoBuchungsart.isDrDg(coBuchung.getBuchungsartID()))
		{
			return true;
		}
		
		// DR laden
		coDienstreise = new CoDienstreise();
		coDienstreise.loadByID(coBuchung.getDienstreiseID());
		
		// wenn es Hinweise gibt, müssen diese kommentiert werden
		bemerkung = coDienstreise.getBemerkungAl();
		if (coDienstreise.hasHinweise() && (bemerkung == null || bemerkung.isEmpty()))
		{
			Messages.showErrorMessage("Freigabe nicht möglich", "Vor der Freigabe muss eine Bemerkung zu den Hinweisen eingetragen werden.");
			if (DialogDienstreise.showDialog(coBuchung.getDienstreiseID(), CoMessageGruppe.ID_AL, 0))
			{
				return checkBeforeFreigabe(coBuchung);
			}
			else
			{
				return false;
			}
		}
		
		return true;
	}
	
	
	@Override
	protected void createFreigabe(CoBuchung coBuchung) throws Exception {
		int positionID, statusGenehmigungID;
		CoPerson coPerson;
		CoFreigabeberechtigungen coFreigabeberechtigungen;
		CoDienstreise coDienstreise;

		coPerson = CoPerson.getInstance();
		coPerson.moveToID(coBuchung.getPersonID());
		positionID = coPerson.getPositionID();
		
		coFreigabeberechtigungen = new CoFreigabeberechtigungen(UserInformation.getPersonID());
		
		// wenn Freigeber V erforderlich ist (Sekretärinnen)
		if (coBuchung.getCoBuchungsart().isFreigabeV() && positionID == CoPosition.ID_SEKRETAERIN)
		{
			// AL dürfen nur einfache Freigabe erteilen
			if (!coFreigabeberechtigungen.isFreigabeVerwaltungErlaubt())
			{
				if (coBuchung.equals(m_coBuchung))
				{
					coBuchung.createFreigabeAl();
				}
				else
				{
					coBuchung.createFreigabeUrlaub(m_nextStatusGenehmigungID);
				}

				return;
			}
		}
		
		// vollständige Azubi-Freigabe nur mit Abteilungsrechten
		if (positionID == CoPosition.ID_AZUBI 
				&& coFreigabeberechtigungen.isFreigabeAzubiErlaubt() && !coFreigabeberechtigungen.isFreigabeNukBerechnungenErlaubt())
		{
			if (coBuchung.equals(m_coBuchung))
			{
				coBuchung.createFreigabeAusbilder();
			}
			else
			{
				coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AUSBILDER);
			}

			return;
		}
		
		
		// sonst wird die vollständige Freigabe erteilt
		if (coBuchung.equals(m_coBuchung))
		{
			coBuchung.createFreigabeGenehmigt();
		}
		else if (coBuchung.equals(m_coBuchungDr))
		{
			coDienstreise = new CoDienstreise();
			coDienstreise.loadByID(coBuchung.getDienstreiseID());
			CoLandDienstreise.getInstance().moveToID(coDienstreise.getLandID());

			// Auslands-DR müssen noch von ... freigegeben werden
			statusGenehmigungID = CoStatusGenehmigung.STATUSID_GENEHMIGT;
			if (coDienstreise.getLandID() != CoLandDienstreise.STATUSID_DEUTSCHLAND && !coFreigabeberechtigungen.isFreigabeDrAuslandErlaubt())
			{
				statusGenehmigungID = CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AL;
			}

			// Genehmigung für alle Tage
			CoBuchung coBuchungTage;
			coBuchungTage = new CoBuchung();
			coBuchungTage.loadByDienstreiseID(coBuchung.getDienstreiseID());
			coBuchungTage.createFreigabeForAll(statusGenehmigungID);
			
			// Buchung als genehmigt markieren, um die Message zu erzeugen
			coBuchung.setStatusGenehmigungID(statusGenehmigungID);
			
			// A1-Bescheinigung prüfen
			if (coBuchung.isVorlaeufig()) // nur beim Beantragen, nicht beim Zurückziehen
			{
				if (CoLandDienstreise.getInstance().isA1())
				{
					new CoMessage().createMessageA1Bescheinigung(coBuchung.getPersonID(), coDienstreise, m_coBuchungDr);
				}
			}
		}
		else if (coBuchung.equals(m_coBuchungUrlaub))
		{
			coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_GENEHMIGT);
		}

	}


	public static String getKey(int id) {
		return "freigaben.al." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}


}
