package pze.business.export.urlaub;


import pze.business.export.ExportPdfListener;
import pze.ui.formulare.person.FormPersonUrlaubsplanung;


/**
 * Listener zur Erstellung einer PDF-Datei mit dem Urlaubsantrag, Sonderurlaub und FA
 * 
 * @author Lisiecki
 */
public abstract class ExportAntragListener extends ExportPdfListener {

	protected FormPersonUrlaubsplanung m_formPersonUrlaubsplanung;
	private String m_name;
	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungUrlaubsplanung
	 * @param string 
	 */
	public ExportAntragListener(FormPersonUrlaubsplanung formAuswertungUrlaubsplanung, String name) {
		m_formPersonUrlaubsplanung = formAuswertungUrlaubsplanung;
		m_name = name;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return "Antrag_" + m_name + "_" + m_formPersonUrlaubsplanung.getDefaultExportName() + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formPersonUrlaubsplanung.getProfilePathKey();
	}
	

}
