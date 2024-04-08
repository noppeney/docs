package pze.business.export;


import pze.ui.formulare.auswertung.FormAuswertungKontowerte;


/**
 * Listener zur Erstellung einer PDF-Datei der Kontowerte
 * 
 * @author Lisiecki
 */
public class ExportKontowerteListener extends ExportPdfListener {

	protected FormAuswertungKontowerte m_formAuswertungKontowerte;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungKontowerte
	 */
	public ExportKontowerteListener(FormAuswertungKontowerte formAuswertungKontowerte) {
		m_formAuswertungKontowerte = formAuswertungKontowerte;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportKontowerteCreator()).createHtml(m_formAuswertungKontowerte);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formAuswertungKontowerte.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAuswertungKontowerte.getProfilePathKey();
	}
	

}
