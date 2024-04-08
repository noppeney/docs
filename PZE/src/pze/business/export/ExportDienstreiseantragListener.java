package pze.business.export;


import java.util.Date;

import pze.business.Format;
import pze.business.Profile;
import pze.business.objects.AbstractCacheObject;
import pze.ui.formulare.person.FormPersonDienstreisen;


/**
 * Listener zur Erstellung einer PDF-Datei Dienstreiseantrag
 * 
 * @author Lisiecki
 */
public class ExportDienstreiseantragListener extends ExportPdfListener {

	protected FormPersonDienstreisen m_formAuswertungDienstreisen;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param formAuswertungAuszahlung
	 */
	public ExportDienstreiseantragListener(FormPersonDienstreisen formAuswertungAuszahlung) {
		m_formAuswertungDienstreisen = formAuswertungAuszahlung;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#createHtmlCode()
	 */
	@Override
	protected String createHtmlCode() throws Exception {
		return (new ExportDienstreiseantragCreator()).createHtml(m_formAuswertungDienstreisen);
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportPdfListener#getDefaultFilename()
	 */
	@Override
	protected String getDefaultFilename() {
		Date datumAnfang, datumEnde;
		AbstractCacheObject co = m_formAuswertungDienstreisen.getCoDienstreiseAntrag();
		
		co.moveFirst();
		datumAnfang = co.getDatum();
		
		co.moveLast();
		datumEnde = co.getDatum();
		
		return "Dienstreiseantrag_" 
				+ m_formAuswertungDienstreisen.getCoPerson().getKuerzel() + "_" 
				+ Format.getReverseUnterstrichString(datumAnfang) 
				// Ende bei mehrtägigen Dienstreisen
				+ (datumAnfang.equals(datumEnde) ? "" : ("_" + Format.getReverseUnterstrichString(datumEnde)))
				+ ".pdf";
	}
	

	@Override
	protected String getProfilePathKey() {
		return Profile.KEY_MONATSEINSATZBLATT;
	}
	

}
