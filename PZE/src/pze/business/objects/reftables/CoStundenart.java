package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status von Stundenwerten Monatseinsatzblatt (ok, geändert...)
 * 
 * @author Lisiecki
 *
 */
public class CoStundenart extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstundenart";

	public static final int STATUSID_INGENIEURSTUNDEN = 1;
	public static final int STATUSID_REISEZEIT = 2;
	public static final int STATUSID_ERSTELLUNG = 3; // TODO ZAVAS: In FormAuswertungProjekt werden diese Stunden nicht berücksichtigt
	public static final int STATUSID_QS = 4;

	private static CoStundenart m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStundenart() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStundenart getInstance() throws Exception {
		if (CoStundenart.m_instance == null)
		{
			CoStundenart.m_instance = new CoStundenart();
			CoStundenart.m_instance.loadAll();
		}
		
		return CoStundenart.m_instance;
	}

	
	public static boolean isStundenartKgg(int id) {
		return id == STATUSID_ERSTELLUNG || id == STATUSID_QS;
	}


}
