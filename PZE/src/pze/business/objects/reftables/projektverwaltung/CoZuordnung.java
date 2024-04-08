package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für die Zuordnung (Projektinfo)
 * 
 * @author Lisiecki
 *
 */
public class CoZuordnung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblzuordnung";



	/**
	 * Kontruktor
	 */
	public CoZuordnung() {
		super("table." + TABLE_NAME);
	}
	

}
