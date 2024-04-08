package pze.business.objects.reftables;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.dienstreisen.CoDienstreise;

/**
 * CacheObject für Ziele einer Dienstreise
 * 
 * @author Lisiecki
 *
 */
public class CoZielDienstreise extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblzieldienstreise";

	
	/**
	 * Kontruktor
	 */
	public CoZielDienstreise() {
		super("table." + TABLE_NAME);
	}
	
	
	/**
	 * Über Buchungsart laden
	 * @param id	Buchungsart
	 * @throws Exception 
	 */
	public void loadByBuchungsartID(int id) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "BuchungsartID=" + id, getSortFieldName());
	}
	

	private IField getFieldOhneFreigabe() {
		return getField("field." + getTableName() + ".ohnefreigabe");
	}


	public boolean isOhneFreigabe() {
		return Format.getBooleanValue(getFieldOhneFreigabe());
	}


	/**
	 * Ziel der übergebenen DR prüfen
	 * 
	 * @param dienstreiseID
	 * @return
	 * @throws Exception
	 */
	public static boolean isOhneFreigabe(int dienstreiseID) throws Exception {
		CoDienstreise coDienstreise;
		CoZielDienstreise coZielDienstreise;
		
		coDienstreise = new CoDienstreise();
		coDienstreise.loadByID(dienstreiseID);
		
		coZielDienstreise = new CoZielDienstreise();
		coZielDienstreise.loadByID(coDienstreise.getZielID());
		
		return coZielDienstreise.hasRows() && coZielDienstreise.isOhneFreigabe();
	}


}
	

