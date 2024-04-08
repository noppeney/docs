package pze.business.objects.reftables.personen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Titel
 * 
 * @author Lisiecki
 *
 */
public class CoTitel extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtbltitel";

	private static CoTitel m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoTitel() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoTitel getInstance() throws Exception {
		if (CoTitel.m_instance == null)
		{
			CoTitel.m_instance = new CoTitel();
			CoTitel.m_instance.loadAll();
		}
		
		return CoTitel.m_instance;
	}


}
