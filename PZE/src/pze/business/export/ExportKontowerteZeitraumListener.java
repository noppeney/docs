package pze.business.export;


import pze.ui.formulare.auswertung.FormAuswertungKontowerteZeitraum;


/**
 * Listener zur Erstellung einer PDF-Datei der Kontowerte (Zeitraum)
 * 
 * @author Lisiecki
 */
public class ExportKontowerteZeitraumListener extends ExportPdfListener {

	protected FormAuswertungKontowerteZeitraum m_formAuswertungKontowerteZeitraum;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungKontowerteZeitraum
	 */
	public ExportKontowerteZeitraumListener(FormAuswertungKontowerteZeitraum formAuswertungKontowerteZeitraum) {
		m_formAuswertungKontowerteZeitraum = formAuswertungKontowerteZeitraum;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportKontowerteCreator()).createHtml(m_formAuswertungKontowerteZeitraum);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formAuswertungKontowerteZeitraum.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAuswertungKontowerteZeitraum.getProfilePathKey();
	}
	

}
