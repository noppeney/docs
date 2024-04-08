package pze.ui.formulare.freigabecenter;

import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.ui.formulare.person.DialogBuchungAendern;



/**
 * Formular für das Freigabecenter für AL
 * 
 * @author Lisiecki
 *
 */
public class FormFreigabecenterOfa extends AbstractFormFreigabecenterMitarbeiter {
	
	protected static String RESID = "form.freigabenneu";
	protected static final String CAPTION = "OFA";
	private static final String BUCHUNGSARTID = CoBuchungsart.ID_ORTSFLEX_ARBEITEN + ", " + CoBuchungsart.ID_VORLESUNG;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param formFreigabecenter 
	 * @throws Exception
	 */
	public FormFreigabecenterOfa(Object parent, FormFreigabecenter formFreigabecenter) throws Exception {
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
		if (Messages.showYesNoMessage("Freigabe beantragen", "Möchten Sie die Freigabe des Antrags '" + coBuchung.getBuchungsart() + "' am "
				+ Format.getString(coBuchung.getDatum()) + " wirklich beantragen?"))
		{
			// AL dürfen direkt die Freigabe für sich selbst erstellen 
			if (UserInformation.getInstance().isGruppeOfaOhneGenehmigung()) // TODO auch für Dienstreisen etc ?
			{
				coBuchung.createFreigabeGenehmigt();
				Messages.showInfoMessage("Buchung genehmigt", "Ihre Buchung wurde automatisch genehmigt.");
			}
			else
			{
				coBuchung.createFreigabeBeantragt();
			}
			
			clickedAktualisieren();
		}

	}

	
	@Override
	protected void clickedBuchungAendern(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception {
		DialogBuchungAendern.showDialogWithBuchung(coBuchung);
		clickedAktualisieren();
	}


	@Override
	protected void clickedBuchungLoeschen(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception {
		if (Messages.showYesNoMessage("Antrag löschen", "Möchten Sie den Antrag '" + coBuchung.getBuchungsart() + "' am "
				+ Format.getString(coBuchung.getDatum()) + " wirklich löschen?"))
		{
			coBuchung.deleteAntrag();
			
			// AL dürfen direkt die Freigabe für sich selbst erstellen 
			if (UserInformation.getInstance().isGruppeOfaOhneGenehmigung()) // TODO auch für Dienstreisen etc ?
			{
				coBuchung.createFreigabeGenehmigt();
				Messages.showInfoMessage("Buchung gelöscht", "Ihre Buchung wurde gelöscht.");
			}

			clickedAktualisieren();
		}
	}


	public static String getKey(int id) {
		return "freigaben.mitarbeiter.ofa." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}



}
