package pze.business.export;


import pze.ui.formulare.auswertung.FormAuswertungAnAbwesenheit;


/**
 * Listener zur Erstellung einer PDF-Datei der An-/Abwesenheit
 * 
 * @author Lisiecki
 */
public class ExportAnAbwesenheitListener extends ExportPdfListener {

	protected FormAuswertungAnAbwesenheit m_formAuswertungAnAbwesenheit;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param FormAuswertungAnAbwesenheit
	 */
	public ExportAnAbwesenheitListener(FormAuswertungAnAbwesenheit formAuswertungAnAbwesenheit) {
		m_formAuswertungAnAbwesenheit = formAuswertungAnAbwesenheit;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAnAbwesenheitCreator()).createHtml(m_formAuswertungAnAbwesenheit);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formAuswertungAnAbwesenheit.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAuswertungAnAbwesenheit.getProfilePathKey();
	}
	

}
