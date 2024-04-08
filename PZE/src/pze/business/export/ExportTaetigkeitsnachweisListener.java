package pze.business.export;


import java.util.Calendar;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.Profile;
import pze.ui.formulare.person.monatseinsatzblatt.FormPersonMonatseinsatzblatt;


/**
 * Listener zur Erstellung einer PDF-Datei des Tätigkeitsnachweises
 * 
 * @author Lisiecki
 */
public class ExportTaetigkeitsnachweisListener extends ExportPdfListener {

	protected FormPersonMonatseinsatzblatt m_formPersonMonatseinsatzblatt;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formPersonMonatseinsatzblatt
	 */
	public ExportTaetigkeitsnachweisListener(FormPersonMonatseinsatzblatt formPersonMonatseinsatzblatt) {
		m_formPersonMonatseinsatzblatt = formPersonMonatseinsatzblatt;
	}

	
	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportTaetigkeitsnachweisCreator()).createHtml(m_formPersonMonatseinsatzblatt);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(m_formPersonMonatseinsatzblatt.getCurrentDatum());

		return "Taetigkeitsnachweis_" 
				+ m_formPersonMonatseinsatzblatt.getCoPerson().getKuerzel() + "_" 
				+ gregDatum.get(Calendar.YEAR) + "_" + (gregDatum.get(Calendar.MONTH)+1) + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return Profile.KEY_MONATSEINSATZBLATT;
	}
	

}
