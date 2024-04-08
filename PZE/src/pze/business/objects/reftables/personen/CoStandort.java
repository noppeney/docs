package pze.business.objects.reftables.personen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Standorte von Personen
 * 
 * @author Lisiecki
 *
 */
public class CoStandort extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstandort";

	public static final int ID_JUELICH = 1;


	/**
	 * Kontruktor
	 */
	public CoStandort() {
		super("table." + TABLE_NAME);
	}
	
}
