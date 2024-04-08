package pze.business.export;


import pze.ui.formulare.auswertung.FormAuswertungVerletzerliste;


/**
 * Listener zur Erstellung einer PDF-Datei der Verletzerliste
 * 
 * @author Lisiecki
 */
public class ExportVerletzerlisteListener extends ExportPdfListener {

	protected FormAuswertungVerletzerliste m_formVerletzerliste;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param FormAuswertungVerletzerliste
	 */
	public ExportVerletzerlisteListener(FormAuswertungVerletzerliste formVerletzerliste) {
		m_formVerletzerliste = formVerletzerliste;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportVerletzerlisteCreator()).createHtml(m_formVerletzerliste);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formVerletzerliste.getDefaultExportName();
	}

	@Override
	protected String getProfilePathKey() {
		return m_formVerletzerliste.getProfilePathKey();
	}
	

}
