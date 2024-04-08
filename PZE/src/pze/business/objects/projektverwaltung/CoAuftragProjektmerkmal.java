package pze.business.objects.projektverwaltung;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für die Zuordnung von Projektmerkmalen zu Aufträgen
 * 
 * @author Lisiecki
 *
 */
public class CoAuftragProjektmerkmal extends AbstractCacheObject {

	public static final String TABLE_NAME = "stblauftragprojektmerkmal";



	/**
	 * Kontruktor
	 */
	public CoAuftragProjektmerkmal() {
		super("table." + TABLE_NAME);
	}


	public void loadByAuftragID(int auftragID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "AuftragID=" + auftragID, getSortFieldName());
	}
	

	/**
	 * Neuen Eintrag für den Auftrag anlegen
	 * 
	 * @param auftragID
	 * @return
	 * @throws Exception
	 */
	public int createNew(int auftragID) throws Exception {
		int id = super.createNew();
		
		setAuftragID(auftragID);
		
		return id;
	}
	
	
	public int getAuftragID(){
		return Format.getIntValue(getField("field." + getTableName() + ".auftragid").getValue());
	}
	
	
	public void setAuftragID(int personID){
		getField("field." + getTableName() + ".auftragid").setValue(personID);
	}
	
	
	public IField getFieldProjektmerkmal(){
		return getField("field." + getTableName() + ".projektmerkmalid");
	}
	
	
	public int getProjektmerkmalID(){
		return getFieldProjektmerkmal().getIntValue();
	}
	

}
