package pze.ui.formulare.freigabecenter;

import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.CoMessage;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.business.objects.reftables.CoZielDienstreise;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.ui.formulare.person.DialogDienstreise;



/**
 * Formular für das Freigabecenter DR/DG für Mitarbeiter
 * 
 * @author Lisiecki
 */
public class FormFreigabecenterDr extends AbstractFormFreigabecenterMitarbeiter {
	
	protected static String RESID = "form.freigabendr";
	protected static final String CAPTION = "DR/DG";
	public static final String BUCHUNGSARTID = CoBuchungsart.ID_DIENSTREISE + ", " + CoBuchungsart.ID_DIENSTGANG;
	
	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param formFreigabecenter 
	 * @throws Exception
	 */
	public FormFreigabecenterDr(Object parent, FormFreigabecenter formFreigabecenter) throws Exception {
		super(parent, formFreigabecenter, CAPTION, RESID);
	}

	
	@Override
	protected void loadAntraegeGeplant(boolean zeitraum) throws Exception {
		m_coBuchungGeplant.loadAntraegeGeplant(m_personID, BUCHUNGSARTID, false);
	}

	
	@Override
	protected void loadAntraegeAktuell(boolean zeitraum) throws Exception {
		m_coBuchungAktuell.loadAntraegeAktuell(m_personID, BUCHUNGSARTID, false);
	}


	@Override
	protected void loadAntraegeAbgeschlossen() throws Exception {
		m_coBuchungAbgeschlossen.loadAntraegeAbgeschlossen(m_personID, m_tfDatumVon.getField().getDateValue(), m_tfDatumBis.getField().getDateValue(), 
				BUCHUNGSARTID, false);
	}


	@Override
	protected void clickedBuchungBeantragen(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception {
		CoBuchung coBuchungTage;

		
		// Sicherheitsabfrage
		if (Messages.showYesNoMessage("Freigabe beantragen", "Möchten Sie die Freigabe des Antrags '" + coBuchung.getBuchungsart() + "' vom "
				+ Format.getString(coBuchung.getDatum()) 
				+ (coBuchung.getDatumBis() == null ? "" : "-" + Format.getString(coBuchung.getDatumBis())) + " wirklich beantragen?"))
		{
			// prüfen, ob alle Angaben für die Dienstreise vorliegen
			if (!DialogDienstreise.showDialog(coBuchung.getDienstreiseID(), true, CoMessageGruppe.ID_MITARBEITER, 0))
			{
				return;
			}
			
			// alle Buchungen der DR laden
			coBuchungTage = new CoBuchung();
			coBuchungTage.loadByDienstreiseID(coBuchung.getDienstreiseID());

			// AL dürfen ggf. direkt die Freigabe für sich selbst erstellen 
			if (UserInformation.getInstance().isGruppeOfaOhneGenehmigung())
			{
				CoPerson coPerson;
				coPerson = CoPerson.getInstance();
				coPerson.moveToID(coBuchung.getPersonID());

				// GF braucht keine Genehmigung, AL je nach DR-Ziel
				if (coPerson.getAbteilungID() == CoAbteilung.ID_GESCHAEFTSFUEHRUNG || CoZielDienstreise.isOhneFreigabe(coBuchung.getDienstreiseID()))
				{
					coBuchungTage.createFreigabeForAll(CoStatusGenehmigung.STATUSID_GENEHMIGT);
					Messages.showInfoMessage("Buchung genehmigt", "Ihre Buchung wurde automatisch genehmigt.");
				}
				else // DR ohne automatische Genehmigung
				{
					coBuchungTage.createFreigabeForAll(CoStatusGenehmigung.STATUSID_BEANTRAGT);
				}
			}
			else // Beantragung normaler MA
			{
				coBuchungTage.createFreigabeForAll(CoStatusGenehmigung.STATUSID_BEANTRAGT);
			}
			
			// Message erzeugen
			new CoMessage().createMessageDrAntrag(coBuchung);

			clickedAktualisieren();
		}

	}

	
	@Override
	protected void clickedBuchungAendern(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception {
		int statusGenehmigung;
		
		// Status prüfen
		statusGenehmigung = coBuchung.getStatusGenehmigungID();
		
		// per Doppelklick können nur vorläufige Anträge bearbeitet werden
		if (statusGenehmigung != CoStatusGenehmigung.STATUSID_GEPLANT && tableBuchungen == null)
		{
			statusGenehmigung = CoStatusGenehmigung.STATUSID_GELOESCHT;
		}
		
		// Dienstreise öffnen
		DialogDienstreise.showDialog(coBuchung.getDienstreiseID(), 0, statusGenehmigung);
//		DialogBuchungAendern.showDialogWithBuchung(coBuchung);
		clickedAktualisieren();
	}


	@Override
	protected void clickedBuchungLoeschen(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception {
		CoBuchung coBuchungTage;
		
		// Sicherheitsabfrage
		if (Messages.showYesNoMessage("Antrag löschen", "Möchten Sie den Antrag '" + coBuchung.getBuchungsart() + "' vom "
				+ Format.getString(coBuchung.getDatum()) 
				+ (coBuchung.getDatumBis() == null ? "" : "-" + Format.getString(coBuchung.getDatumBis())) + " wirklich löschen?"))
		{
			// alle Buchungen der DR laden und löschen
			coBuchungTage = new CoBuchung();
			coBuchungTage.loadByDienstreiseID(coBuchung.getDienstreiseID());
			coBuchungTage.createFreigabeForAll(CoStatusGenehmigung.STATUSID_GELOESCHT);
			clickedAktualisieren();
		}
	}


	public static String getKey(int id) {
		return "freigaben.mitarbeiter.dr." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}



}
