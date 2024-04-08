package pze.business.export;


import pze.ui.formulare.auswertung.FormAuswertungArbeitszeit;


/**
 * Listener zur Erstellung einer PDF-Datei der Auswertung Arbeitszeit
 * 
 * @author Lisiecki
 */
public class ExportAuswertungArbeitszeitListener extends ExportPdfListener {

	protected FormAuswertungArbeitszeit m_formAuswertungArbeitszeit;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungArbeitszeit
	 */
	public ExportAuswertungArbeitszeitListener(FormAuswertungArbeitszeit formAuswertungArbeitszeit) {
		m_formAuswertungArbeitszeit = formAuswertungArbeitszeit;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAuswertungArbeitszeitCreator()).createHtml(m_formAuswertungArbeitszeit);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formAuswertungArbeitszeit.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAuswertungArbeitszeit.getProfilePathKey();
	}
	

}
