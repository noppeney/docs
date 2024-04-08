package pze.business.objects.reftables.projektverwaltung;

import framework.Application;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Standorte von Kunden von Aufträgen
 * 
 * @author Lisiecki
 *
 */
public class CoStandortKunde extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstandortkunde";



	/**
	 * Kontruktor
	 */
	public CoStandortKunde() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Alle Standorte der Kunden laden.
	 * 
	 * @param kundeID
	 * @throws Exception
	 */
	public void loadByKundeID(int kundeID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "kundeID=" + kundeID, getSortFieldName());
	}


}
