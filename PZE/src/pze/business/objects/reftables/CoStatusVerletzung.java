package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status von Verletzungen (ok, geändert...)
 * 
 * @author Lisiecki
 *
 */
public class CoStatusVerletzung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusverletzung";

	public static final int STATUSID_OFFEN = 1;
	public static final int STATUSID_FREIGEGEBEN= 2;

	private static CoStatusVerletzung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusVerletzung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusVerletzung getInstance() throws Exception {
		if (CoStatusVerletzung.m_instance == null)
		{
			CoStatusVerletzung.m_instance = new CoStatusVerletzung();
			CoStatusVerletzung.m_instance.loadAll();
		}
		
		return CoStatusVerletzung.m_instance;
	}


}
