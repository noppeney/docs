package pze.business.objects.dienstreisen;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Dienstreisezeiten
 * 
 * @author Lisiecki
 *
 */
public class CoDienstreisezeit extends AbstractCacheObject {

	public static final String TABLE_NAME = "tbldienstreisezeit";
	


	/**
	 * Kontruktor
	 */
	public CoDienstreisezeit() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * CO für die Buchung laden.<br>
	 * 
	 * @param buchungID
	 * @param datum
	 * @throws Exception
	 */
	public void load(int buchungID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "BuchungID=" + buchungID, getSortFieldName());
	}
	

	private IField getFieldBuchungID() {
		return getField("field." + getTableName() + ".buchungid");
	}


	private void setBuchungID(Integer buchungID) {
		getFieldBuchungID().setValue(buchungID);
	}


//	private int getBuchungID() {
//		return Format.getIntValue(getFieldBuchungID());
//	}


	public static String getResIdReisezeit() {
		return "field." + TABLE_NAME + ".reisezeit";
	}


	public IField getFieldReisezeit() {
		return getField(getResIdReisezeit());
	}


	private int getReisezeit() throws Exception {
		return Format.getIntValue(getFieldReisezeit());
	}


	public void setReisezeit(Object zeit) {
		getFieldReisezeit().setValue(zeit);
	}


	public static String getResIdProjektzeit() {
		return "field." + TABLE_NAME + ".projektzeit";
	}

	
	public IField getFieldProjektzeit() {
		return getField(getResIdProjektzeit());
	}


	private int getProjektzeit() throws Exception {
		return Format.getIntValue(getFieldProjektzeit());
	}


	public void setProjektzeit(Object zeit) {
		getFieldProjektzeit().setValue(zeit);
	}


	/**
	 * Neuen Datensatz anlegen
	 * 
	 * @param datum 
	 * @param personID 
	 */
	public int createNew(int buchungID) throws Exception	{
		
		// neuen Datensatz anlegen
		super.createNew();
		
		setBuchungID(buchungID);
		
		return getID();
	}


}
