package pze.business.objects;

import java.util.Date;
import java.util.GregorianCalendar;

import pze.business.Format;

/**
 * CacheObject für Brückentage
 * 
 * @author Lisiecki
 *
 */
public class CoBrueckentag extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblbrueckentag";


	private static CoBrueckentag m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoBrueckentag() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoBrueckentag getInstance() throws Exception {
		if (CoBrueckentag.m_instance == null)
		{
			CoBrueckentag.m_instance = new CoBrueckentag();
			CoBrueckentag.m_instance.loadAll();
		}
		
		return CoBrueckentag.m_instance;
	}


	/**
	 * Nach Datum, Bundesland sortieren
	 * @return 
	 */
	protected String getSortFieldName() {
		return "Datum, BundeslandID";
	}
	

	@Override
	public String getNavigationBitmap() {
		return "flag.green";
	}


	/**
	 * Datum, auf 12 Uhr normiert
	 * 
	 * @return
	 */
	public Date getDatum() {
		return Format.getDate12Uhr(Format.getDateValue(getField("field." + getTableName() + ".datum").getValue()));
	}


	public int getBundeslandID() {
		return Format.getIntValue(getField("field." + getTableName() + ".bundeslandid").getValue());
	}


	/**
	 * sucht das übergebene Datum in den Brückentagen
	 * 
	 * @param datum
	 * @return Datum ist Brückentag
	 */
	public boolean isBrueckentag(GregorianCalendar datum, int bundeslandID) {
		return isBrueckentag(Format.getDateValue(datum), bundeslandID);
	}


	/**
	 * sucht das übergebene Datum in den Brückentagen
	 * 
	 * @param datum
	 * @return Datum ist Brückentag
	 */
	public boolean isBrueckentag(Date datum, int bundeslandID) {
		Date datum12Uhr, datumBrueckentag;
		
		datum12Uhr = Format.getDate12Uhr(datum);
		
		if (!moveFirst())
		{
			return false;
		}
		
		do 
		{
			datumBrueckentag = getDatum();
			
			if (datum12Uhr.equals(datumBrueckentag) && bundeslandID == getBundeslandID())
			{
				return true;
			}
			
			if (datumBrueckentag.after(datum12Uhr))
			{
				return false;
			}
		} while (moveNext());
		
		return false;
	}


}
