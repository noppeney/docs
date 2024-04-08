package pze.business.export;


import java.util.Calendar;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.Profile;
import pze.ui.formulare.projektverwaltung.FormAuswertungProjekt;


/**
 * Listener zur Erstellung einer PDF-Datei des Tätigkeitsnachweises
 * 
 * @author Lisiecki
 */
public class ExportTaetigkeitsnachweisProjektListener extends ExportPdfListener {

	protected FormAuswertungProjekt m_formAuswertungProjekt;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungProjekt
	 */
	public ExportTaetigkeitsnachweisProjektListener(FormAuswertungProjekt formAuswertungProjekt) {
		m_formAuswertungProjekt = formAuswertungProjekt;
	}

	
	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportTaetigkeitsnachweisProjektCreator()).createHtml(m_formAuswertungProjekt);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(m_formAuswertungProjekt.getDatumExport());

		return "Taetigkeitsnachweis_" 
				+ m_formAuswertungProjekt.getCoProjekt().getProjektNr() + "_" 
				+ gregDatum.get(Calendar.YEAR) + "_" + (gregDatum.get(Calendar.MONTH)+1) + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return Profile.KEY_MONATSEINSATZBLATT;
	}
	

}
