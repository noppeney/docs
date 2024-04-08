package pze.business.export.urlaub;


import pze.business.export.ExportPdfListener;
import pze.ui.formulare.auswertung.FormAuswertungUrlaubsplanung;


/**
 * Listener zur Erstellung einer PDF-Datei der Urlaubsplanung
 * 
 * @author Lisiecki
 */
public class ExportUrlaubsplanungListener extends ExportPdfListener {

	protected FormAuswertungUrlaubsplanung m_formAuswertungUrlaubsplanung;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungUrlaubsplanung
	 */
	public ExportUrlaubsplanungListener(FormAuswertungUrlaubsplanung formAuswertungUrlaubsplanung) {
		m_formAuswertungUrlaubsplanung = formAuswertungUrlaubsplanung;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		String htmlString;
		
		long a = System.currentTimeMillis();
		htmlString = (new ExportUrlaubsplanungCreator()).createHtml(m_formAuswertungUrlaubsplanung);
		System.out.println("export: " + (System.currentTimeMillis()-a)/1000.);
		
		return htmlString;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return m_formAuswertungUrlaubsplanung.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAuswertungUrlaubsplanung.getProfilePathKey();
	}
	

}
