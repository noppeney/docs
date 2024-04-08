package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status von Info-Meldungen
 * 
 * @author Lisiecki
 */
public class CoStatusMessage extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusmessage";

	public static final int STATUSID_OFFEN = 1;
	public static final int STATUSID_QUITTIERT = 2;

	private static CoStatusMessage m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusMessage() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoStatusMessage getInstance() throws Exception {
		if (CoStatusMessage.m_instance == null)
		{
			CoStatusMessage.m_instance = new CoStatusMessage();
			CoStatusMessage.m_instance.loadAll();
		}
		
		return CoStatusMessage.m_instance;
	}

	
	public static boolean isOffen(int statusID) {
		return statusID == STATUSID_OFFEN;
	}

	
	public static boolean isQuittiert(int statusID) {
		return statusID == STATUSID_QUITTIERT;
	}


	
}
