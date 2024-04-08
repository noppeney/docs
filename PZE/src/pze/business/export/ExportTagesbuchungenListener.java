package pze.business.export;


import pze.business.Format;
import pze.business.Profile;
import pze.business.objects.personen.CoBuchung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.auswertung.FormAuswertungKontowerte;


/**
 * Listener zur Erstellung einer PDF-Datei der Tagesbuchungen
 * 
 * @author Lisiecki
 */
public class ExportTagesbuchungenListener extends ExportPdfListener {

	protected FormAuswertungKontowerte m_formAuswertungKontowerte;

	private SortedTableControl m_tableBuchungen;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param tableBuchungen
	 */
	public ExportTagesbuchungenListener(SortedTableControl tableBuchungen) {
		m_tableBuchungen = tableBuchungen;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportTagesbuchungenCreator()).createHtml(m_tableBuchungen);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		CoBuchung coBuchung;
		
		coBuchung = (CoBuchung) m_tableBuchungen.getData();
		
		return ("Tagesprotokoll_" 
				+ "_" + coBuchung.getFieldPersonID().getDisplayValue()
				+ "_" + Format.getString(coBuchung.getDatum())
				+ ".pdf")
				.replace(",", "").replace(" ", "_");
	}
	

	@Override
	protected String getProfilePathKey() {
		return Profile.KEY_MONATSEINSATZBLATT;
	}
	

}
