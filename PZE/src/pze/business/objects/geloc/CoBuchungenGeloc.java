package pze.business.objects.geloc;

import java.util.Date;

import framework.Application;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoBuchung;
import startup.PZEStartupAdapter;


/**
 * CacheObject für Buchungen aus der Geloc-Datenbank
 * 
 * @author Lisiecki
 *
 */
public class CoBuchungenGeloc extends AbstractCacheObject {

	private static final String TABLE_NAME = "buchungengeloc";

	/**
	 * Kontruktor
	 */
	public CoBuchungenGeloc() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Sortiert nach Buchungsnr, die neueste zuerst
	 * 
	 * (non-Javadoc)
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "bnr";
	}
	

	/**
	 * Alle Buchungen aus der Geloc-DB laden, die noch nicht in der PZE-DB sind.
	 * 
	 * @param buchungsNr letzte bereits gespeicherte BuchungsNr
	 * @throws Exception
	 */
	public void loadNewBuchungen() throws Exception{
		loadNewBuchungen(CoBuchung.loadLastBuchungsNr());
	}


	/**
	 * Alle Terminal-Buchungen (keine PC-Buchungen) laden, die neuer als die übergebene BuchungsNr sind.
	 * 
	 * @param buchungsNr letzte bereits gespeicherte BuchungsNr
	 * @throws Exception
	 */
	private void loadNewBuchungen(int buchungsNr) throws Exception{

		
		PZEStartupAdapter.openGelocDbConnection();
		
		Application.getLoaderBase().load(this, "bnr > " + buchungsNr + " AND system <> 0", getSortFieldName());

		PZEStartupAdapter.openDefaultDbConnection();
	}


	public int getBuchungsNr() {
		return Format.getIntValue(getField("field." + getTableName() + ".bnr").getValue());
	}


	public Date getZeitpunkt() {
		return getField("field." + getTableName() + ".zeitpunkt").getDateValue();
	}


	public int getSystemNr() {
		return Format.getIntValue(getField("field." + getTableName() + ".system").getValue());
	}


	public int getEventNr() {
		return Format.getIntValue(getField("field." + getTableName() + ".event").getValue());
	}


	public int getIdX() {
		return Format.getIntValue(getField("field." + getTableName() + ".idx").getValue());
	}


	public String getChipkartenNrHex() {
		return Format.getStringValue(getField("field." + getTableName() + ".knrhex").getValue());
	}


	/**
	 * Buchungsart aus Geloc (param1)
	 * 
	 * @return
	 */
	public String getParam1() {
		return Format.getStringValue(getField("field." + getTableName() + ".param1").getValue());
	}

	
	public String getParam2() {
		return Format.getStringValue(getField("field." + getTableName() + ".param2").getValue());
	}

}
