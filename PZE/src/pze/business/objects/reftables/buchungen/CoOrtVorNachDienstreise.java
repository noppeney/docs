package pze.business.objects.reftables.buchungen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für den Ort vor und nach Dienstreisen
 * 
 * @author Lisiecki
 */
public class CoOrtVorNachDienstreise extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblortvornachdienstreise";

	public static final int STATUSID_WOHNORT = 1;
	public static final int STATUSID_PRIVAT = 2;
	public static final int STATUSID_HOTEL = 3;
	public static final int STATUSID_WTI = 4;

	private static CoOrtVorNachDienstreise m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoOrtVorNachDienstreise() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoOrtVorNachDienstreise getInstance() throws Exception {
		if (CoOrtVorNachDienstreise.m_instance == null)
		{
			CoOrtVorNachDienstreise.m_instance = new CoOrtVorNachDienstreise();
			CoOrtVorNachDienstreise.m_instance.loadAll();
		}
		
		return CoOrtVorNachDienstreise.m_instance;
	}

	
	public static boolean isIdWti(int id) {
		return id == STATUSID_WTI;
	}

	
	public static boolean isIdWohnort(int id) {
		return id == STATUSID_WOHNORT;
	}

}
