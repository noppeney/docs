package pze.business.export;


import java.util.GregorianCalendar;

import pze.business.Format;
import pze.ui.formulare.auswertung.FormAnwesenheitUebersicht;


/**
 * Listener zur Erstellung einer PDF-Datei der Tagesmeldung
 * 
 * @author Lisiecki
 */
public class ExportTagesmeldungListener extends ExportPdfListener {

	private FormAnwesenheitUebersicht m_formAnwesenheitUebersicht;
	private GregorianCalendar m_gregDatum;
	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAnwesenheitUebersicht
	 */
	public ExportTagesmeldungListener(FormAnwesenheitUebersicht formAnwesenheitUebersicht, GregorianCalendar gregDatum) {
		m_formAnwesenheitUebersicht = formAnwesenheitUebersicht;
		m_gregDatum = gregDatum;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportTagesmeldungCreator()).createHtml(m_gregDatum);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		return "Tagesmeldung_" + Format.getString(m_gregDatum) + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return m_formAnwesenheitUebersicht.getProfilePathKey();
	}
	

}
