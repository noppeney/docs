package pze.business.objects.reftables.projektverwaltung;

import framework.Application;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Abteilungen von Kunden von Aufträgen
 * 
 * @author Lisiecki
 *
 */
public class CoAbteilungKunde extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblabteilungkunde";



	/**
	 * Kontruktor
	 */
	public CoAbteilungKunde() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Alle Abteilungen der Kunden laden.
	 * 
	 * @param kundeID
	 * @throws Exception
	 */
	public void loadByKundeID(int kundeID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "kundeID=" + kundeID, getSortFieldName());
	}


}
