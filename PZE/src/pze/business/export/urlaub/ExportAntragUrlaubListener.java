package pze.business.export.urlaub;


import pze.ui.formulare.person.FormPersonUrlaubsplanung;


/**
 * Listener zur Erstellung einer PDF-Datei mit dem Urlaubsantrag
 * 
 * @author Lisiecki
 */
public class ExportAntragUrlaubListener extends ExportAntragListener {

	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungUrlaubsplanung
	 */
	public ExportAntragUrlaubListener(FormPersonUrlaubsplanung formAuswertungUrlaubsplanung) {
		super(formAuswertungUrlaubsplanung, "Urlaub");
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAntragUrlaubCreator()).createHtml(m_formPersonUrlaubsplanung);
	}

}
