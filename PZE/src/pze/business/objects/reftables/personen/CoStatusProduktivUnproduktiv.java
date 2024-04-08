package pze.business.objects.reftables.personen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status produktiv/unproduktiv
 * 
 * @author Lisiecki
 *
 */
public class CoStatusProduktivUnproduktiv extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusproduktivunproduktiv";

	private static CoStatusProduktivUnproduktiv m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusProduktivUnproduktiv() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusProduktivUnproduktiv getInstance() throws Exception {
		if (CoStatusProduktivUnproduktiv.m_instance == null)
		{
			CoStatusProduktivUnproduktiv.m_instance = new CoStatusProduktivUnproduktiv();
			CoStatusProduktivUnproduktiv.m_instance.loadAll();
		}
		
		return CoStatusProduktivUnproduktiv.m_instance;
	}


}
