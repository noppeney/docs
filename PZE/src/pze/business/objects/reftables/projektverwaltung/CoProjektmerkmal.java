package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Projektmerkmal
 * 
 * @author Lisiecki
 *
 */
public class CoProjektmerkmal extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblprojektmerkmal";

	private static CoProjektmerkmal m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoProjektmerkmal() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoProjektmerkmal getInstance() throws Exception {
		if (CoProjektmerkmal.m_instance == null)
		{
			CoProjektmerkmal.m_instance = new CoProjektmerkmal();
			CoProjektmerkmal.m_instance.loadAll();
		}
		
		return CoProjektmerkmal.m_instance;
	}


}
