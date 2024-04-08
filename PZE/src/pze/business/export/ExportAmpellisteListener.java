package pze.business.export;


import pze.ui.formulare.auswertung.FormAmpelliste;


/**
 * Listener zur Erstellung einer PDF-Datei der Ampelliste
 * 
 * @author Lisiecki
 */
public class ExportAmpellisteListener extends ExportPdfListener {

	protected FormAmpelliste m_formAmpelliste;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAmpelliste
	 */
	public ExportAmpellisteListener(FormAmpelliste formAmpelliste) {
		m_formAmpelliste = formAmpelliste;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAmpellisteCreator()).createHtml(m_formAmpelliste);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formAmpelliste.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAmpelliste.getProfilePathKey();
	}
	

}
