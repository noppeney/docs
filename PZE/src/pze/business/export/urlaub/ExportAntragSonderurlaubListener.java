package pze.business.export.urlaub;


import pze.ui.formulare.person.FormPersonUrlaubsplanung;


/**
 * Listener zur Erstellung einer PDF-Datei mit dem Antrag auf Sonderurlaub
 * 
 * @author Lisiecki
 */
public class ExportAntragSonderurlaubListener extends ExportAntragListener {


	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungUrlaubsplanung
	 * @param i 
	 */
	public ExportAntragSonderurlaubListener(FormPersonUrlaubsplanung formAuswertungUrlaubsplanung) {
		super(formAuswertungUrlaubsplanung, "Sonderurlaub");
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAntragSonderurlaubCreator()).createHtml(m_formPersonUrlaubsplanung);
	}

}
