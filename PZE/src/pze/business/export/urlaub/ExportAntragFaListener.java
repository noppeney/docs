package pze.business.export.urlaub;


import pze.ui.formulare.person.FormPersonUrlaubsplanung;


/**
 * Listener zur Erstellung einer PDF-Datei mit dem FA-Antrag
 * 
 * @author Lisiecki
 */
public class ExportAntragFaListener extends ExportAntragListener {

	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungUrlaubsplanung
	 */
	public ExportAntragFaListener(FormPersonUrlaubsplanung formAuswertungUrlaubsplanung) {
		super(formAuswertungUrlaubsplanung, "FA");
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAntragFaCreator()).createHtml(m_formPersonUrlaubsplanung);
	}

}
