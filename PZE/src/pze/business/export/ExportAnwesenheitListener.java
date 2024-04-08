package pze.business.export;


import pze.ui.formulare.auswertung.FormAnwesenheitUebersicht;


/**
 * Listener zur Erstellung einer PDF-Datei der Anwesenheit
 * 
 * @author Lisiecki
 */
public class ExportAnwesenheitListener extends ExportPdfListener {

	protected FormAnwesenheitUebersicht m_formAnwesenheitUebersicht;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAnwesenheitUebersicht
	 */
	public ExportAnwesenheitListener(FormAnwesenheitUebersicht formAnwesenheitUebersicht) {
		m_formAnwesenheitUebersicht = formAnwesenheitUebersicht;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAnwesenheitCreator()).createHtml(m_formAnwesenheitUebersicht);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formAnwesenheitUebersicht.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAnwesenheitUebersicht.getProfilePathKey();
	}
	

}
