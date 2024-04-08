package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status von Stundenwerten Monatseinsatzblatt (ok, geändert...)
 * 
 * @author Lisiecki
 *
 */
public class CoStatusStundenwertMonatseinsatzblatt extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusstundenwertmonatseinsatzblatt";

	public static final int STATUSID_OK = 1;
	public static final int STATUSID_GEAENDERT = 2;

	private static CoStatusStundenwertMonatseinsatzblatt m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusStundenwertMonatseinsatzblatt() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusStundenwertMonatseinsatzblatt getInstance() throws Exception {
		if (CoStatusStundenwertMonatseinsatzblatt.m_instance == null)
		{
			CoStatusStundenwertMonatseinsatzblatt.m_instance = new CoStatusStundenwertMonatseinsatzblatt();
			CoStatusStundenwertMonatseinsatzblatt.m_instance.loadAll();
		}
		
		return CoStatusStundenwertMonatseinsatzblatt.m_instance;
	}


}
