package pze.business.export;


import pze.ui.formulare.auswertung.FormAuswertungAuszahlung;


/**
 * Listener zur Erstellung einer PDF-Datei der Auswertung Auszahlung
 * 
 * @author Lisiecki
 */
public class ExportAuswertungAuszahlungListener extends ExportPdfListener {

	protected FormAuswertungAuszahlung m_formAuswertungAuszahlung;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungAuszahlung
	 */
	public ExportAuswertungAuszahlungListener(FormAuswertungAuszahlung formAuswertungAuszahlung) {
		m_formAuswertungAuszahlung = formAuswertungAuszahlung;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAuswertungAuszahlungCreator()).createHtml(m_formAuswertungAuszahlung);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formAuswertungAuszahlung.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAuswertungAuszahlung.getProfilePathKey();
	}
	

}
