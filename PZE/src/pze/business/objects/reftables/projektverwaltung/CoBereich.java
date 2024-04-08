package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Bereiche von Aufträgen
 * 
 * @author Lisiecki
 *
 */
public class CoBereich extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblbereich";

	private static CoBereich m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoBereich() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoBereich getInstance() throws Exception {
		if (CoBereich.m_instance == null)
		{
			CoBereich.m_instance = new CoBereich();
			CoBereich.m_instance.loadAll();
		}
		
		return CoBereich.m_instance;
	}


}
