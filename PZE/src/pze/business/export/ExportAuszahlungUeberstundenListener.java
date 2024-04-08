package pze.business.export;


import java.util.Calendar;
import java.util.GregorianCalendar;

import pze.business.Format;
import pze.business.Profile;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;


/**
 * Listener zur Erstellung einer PDF-Datei Antrag Auszahlung Überstunden
 * 
 * @author Lisiecki
 */
public class ExportAuszahlungUeberstundenListener extends ExportPdfListener {

	protected CoKontowert m_coKontowert;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param coKontowert
	 */
	public ExportAuszahlungUeberstundenListener(CoKontowert coKontowert) {
		m_coKontowert = coKontowert;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportAuszahlungUeberstundenCreator()).createHtml(m_coKontowert);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() throws Exception {
		GregorianCalendar gregDatum;
		CoPerson coPerson;
		
		// Datum und Person bestimmen
		gregDatum = Format.getGregorianCalendar(m_coKontowert.getDatum());

		coPerson = new CoPerson();
		coPerson.loadByID(m_coKontowert.getPersonID());
		
		return "Auszahlung_Ueberstunden_" 
				+ coPerson.getKuerzel() + "_" 
				+ gregDatum.get(Calendar.YEAR) + "_" + (gregDatum.get(Calendar.MONTH)+1) + ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return Profile.KEY_AUSZAHLUNG_UEBERSTUNDEN;
	}
	

}
