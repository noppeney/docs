package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Fachgebiete von Abrufen
 * 
 * @author Lisiecki
 *
 */
public class CoFachgebiet extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblfachgebiet";

	private static CoFachgebiet m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoFachgebiet() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoFachgebiet getInstance() throws Exception {
		if (CoFachgebiet.m_instance == null)
		{
			CoFachgebiet.m_instance = new CoFachgebiet();
			CoFachgebiet.m_instance.loadAll();
		}
		
		return CoFachgebiet.m_instance;
	}


}
