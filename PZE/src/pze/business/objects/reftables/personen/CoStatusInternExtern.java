package pze.business.objects.reftables.personen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status intern/extern
 * 
 * @author Lisiecki
 *
 */
public class CoStatusInternExtern extends AbstractCacheObject {

	public static final int STATUSID_INTERN = 1;
	public static final int STATUSID_EXTERN = 2;
	public static final int STATUSID_EXTERN_WTI = 3;
	
	private static final String TABLE_NAME = "rtblstatusinternextern";

	private static CoStatusInternExtern m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusInternExtern() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusInternExtern getInstance() throws Exception {
		if (CoStatusInternExtern.m_instance == null)
		{
			CoStatusInternExtern.m_instance = new CoStatusInternExtern();
			CoStatusInternExtern.m_instance.loadAll();
		}
		
		return CoStatusInternExtern.m_instance;
	}


}
