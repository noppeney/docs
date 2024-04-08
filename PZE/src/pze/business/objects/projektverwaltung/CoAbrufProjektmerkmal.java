package pze.business.objects.projektverwaltung;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für die Zuordnung von Projektmerkmalen zu Abrufen
 * 
 * @author Lisiecki
 *
 */
public class CoAbrufProjektmerkmal extends AbstractCacheObject {

	public static final String TABLE_NAME = "stblabrufprojektmerkmal";




	/**
	 * Kontruktor
	 */
	public CoAbrufProjektmerkmal() {
		super("table." + TABLE_NAME);
	}


	public void loadByAbrufID(int abrufID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "AbrufID=" + abrufID, getSortFieldName());
	}
	

	/**
	 * Neuen Eintrag für den Auftrag anlegen
	 * 
	 * @param abrufID
	 * @return
	 * @throws Exception
	 */
	public int createNew(int abrufID) throws Exception {
		int id = super.createNew();
		
		setAbrufID(abrufID);
		
		return id;
	}
	
	
	public int getAbrufID(){
		return Format.getIntValue(getField("field." + getTableName() + ".abrufid").getValue());
	}
	
	
	public void setAbrufID(int personID){
		getField("field." + getTableName() + ".abrufid").setValue(personID);
	}
	
	
	public IField getFieldProjektmerkmal(){
		return getField("field." + getTableName() + ".projektmerkmalid");
	}
	
	
	public int getProjektmerkmalID(){
		return getFieldProjektmerkmal().getIntValue();
	}
	

}
