package pze.business.objects.reftables.buchungen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status von Auszahlungen von Überstunden
 * 
 * @author Lisiecki
 */
public class CoStatusAuszahlung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusauszahlung";

	public static final int STATUSID_BEANTRAGT = 1;
	public static final int STATUSID_IN_BEARBEITUNG = 2;
	public static final int STATUSID_AUSZAHLUNG_FOLGEMONAT = 3;
	public static final int STATUSID_AUSZAHLUNGSKONTO = 4;
	public static final int STATUSID_AUSGEZAHLT = 5;
	public static final int STATUSID_KEINE_AUSZAHLUNG = 6;

	private static CoStatusAuszahlung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusAuszahlung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoStatusAuszahlung getInstance() throws Exception {
		if (CoStatusAuszahlung.m_instance == null)
		{
			CoStatusAuszahlung.m_instance = new CoStatusAuszahlung();
			CoStatusAuszahlung.m_instance.loadAll();
		}
		
		return CoStatusAuszahlung.m_instance;
	}


}
