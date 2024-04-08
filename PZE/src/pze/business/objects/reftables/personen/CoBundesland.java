package pze.business.objects.reftables.personen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Bundesländer
 * 
 * @author Lisiecki
 *
 */
public class CoBundesland extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblbundesland";

	public static final int ID_NRW = 1;
	public static final int ID_BAYERN = 3;
	public static final int ID_BADEN_WUERTTEMBERG = 2;

	private static CoBundesland m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoBundesland() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoBundesland getInstance() throws Exception {
		if (CoBundesland.m_instance == null)
		{
			CoBundesland.m_instance = new CoBundesland();
			CoBundesland.m_instance.loadAll();
		}
		
		return CoBundesland.m_instance;
	}


}
