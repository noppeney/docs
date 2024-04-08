package pze.business.objects.reftables.projektverwaltung;

import framework.Application;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für die P-Nr.
 * 
 * @author Lisiecki
 *
 */
public class CoPNummer extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblpnummer";



	/**
	 * Kontruktor
	 */
	public CoPNummer() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Alle P-Nummern der Abteilung laden.
	 * 
	 * @param abteilungKundeID
	 * @throws Exception
	 */
	public void loadByAbteilungKundeID(int abteilungKundeID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "AbteilungKundeID=" + abteilungKundeID, getSortFieldName());
	}


}
