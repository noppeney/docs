package pze.business.export;


import pze.ui.formulare.projektverwaltung.FormAuswertungProjekt;


/**
 * Listener zur Erstellung einer PDF-Datei der Stundenübersicht von Projekten
 * 
 * @author Lisiecki
 */
public class ExportStundenuebersichtProjekteListener extends ExportPdfListener {

	protected FormAuswertungProjekt m_formAuswertungProjekt;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungprojekt
	 */
	public ExportStundenuebersichtProjekteListener(FormAuswertungProjekt formAuswertungprojekt) {
		m_formAuswertungProjekt = formAuswertungprojekt;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportStundenuebersichtProjekteCreator()).createHtml(m_formAuswertungProjekt);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() throws Exception {
		return m_formAuswertungProjekt.getDefaultExportName();
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAuswertungProjekt.getProfilePathKey();
	}
	

}
