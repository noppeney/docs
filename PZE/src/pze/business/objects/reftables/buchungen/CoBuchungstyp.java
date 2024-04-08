package pze.business.objects.reftables.buchungen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Buchungstypen (aus der Geloc-DB) (Zeitbuchung, Aktivierung Zeitzone...)
 * 
 * @author Lisiecki
 *
 */
public class CoBuchungstyp extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblbuchungstyp";

	public static final String ZEITBUCHUNG = "Zeitbuchung";

	private static CoBuchungstyp m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoBuchungstyp() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoBuchungstyp getInstance() throws Exception {
		if (CoBuchungstyp.m_instance == null)
		{
			CoBuchungstyp.m_instance = new CoBuchungstyp();
			CoBuchungstyp.m_instance.loadAll();
		}
		
		return CoBuchungstyp.m_instance;
	}

	
	/**
	 * Gehe zu dem Eintrag mit der EventNr
	 * 
	 * @param eventNr
	 * @return ID
	 */
	public int getIdFromEventNr(int eventNr) {
		
		if (!moveTo(eventNr, "field." + getTableName() + ".eventnr"))
		{
			return 0;
		}
		
		return getID();
	}
	

}
