package pze.business.objects.reftables.personen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status aktiv/inaktiv
 * 
 * @author Lisiecki
 *
 */
public class CoStatusAktivInaktiv extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusaktivinaktiv";
	
	public static final int STATUSID_ALLE = 0; // virtuell, für einfache Unterscheidung bei Auswertungen
	public static final int STATUSID_AKTIV = 1;
	public static final int STATUSID_INAKTIV = 2;
	public static final int STATUSID_AUSGESCHIEDEN = 3;
	
	private static CoStatusAktivInaktiv m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusAktivInaktiv() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusAktivInaktiv getInstance() throws Exception {
		if (CoStatusAktivInaktiv.m_instance == null)
		{
			CoStatusAktivInaktiv.m_instance = new CoStatusAktivInaktiv();
			CoStatusAktivInaktiv.m_instance.loadAll();
		}
		
		return CoStatusAktivInaktiv.m_instance;
	}

}
