package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Ersthelfer
 * 
 * @author Lisiecki
 *
 */
public class CoErsthelfer extends AbstractCacheObject {

	public static final String TABLE_NAME = "rtblersthelfer";

	private static CoErsthelfer m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoErsthelfer() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoErsthelfer getInstance() throws Exception {
		if (CoErsthelfer.m_instance == null)
		{
			CoErsthelfer.m_instance = new CoErsthelfer();
			CoErsthelfer.m_instance.loadAll();
			
			// sortieren nach Namen
			CoErsthelfer.m_instance.sortDisplayValue(CoErsthelfer.m_instance.getFieldPersonID().getFieldDescription().getResID(), false);
		}
		
		return CoErsthelfer.m_instance;
	}


}
