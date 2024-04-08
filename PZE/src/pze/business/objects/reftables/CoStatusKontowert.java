package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status von Kontowerten (ok, geändert...)
 * 
 * @author Lisiecki
 *
 */
public class CoStatusKontowert extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatuskontowert";

	public static final int STATUSID_OK = 1;
	public static final int STATUSID_GEAENDERT = 2;

	private static CoStatusKontowert m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusKontowert() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusKontowert getInstance() throws Exception {
		if (CoStatusKontowert.m_instance == null)
		{
			CoStatusKontowert.m_instance = new CoStatusKontowert();
			CoStatusKontowert.m_instance.loadAll();
		}
		
		return CoStatusKontowert.m_instance;
	}


}
