package pze.business.objects.projektverwaltung;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für die Zuordnung von Kostenstellen zu Abrufen
 * 
 * @author Lisiecki
 *
 */
public class CoAuftragKostenstelle extends AbstractCacheObject {

	public static final String TABLE_NAME = "stblauftragkostenstelle";




	/**
	 * Kontruktor
	 */
	public CoAuftragKostenstelle() {
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
		
		setAbrufID(auftragID);
		
		return id;
	}
	

	public IField getFieldAuftragID() {
		return getField("field." + getTableName() + ".auftragid");
	}
	

	public int getAbrufID(){
		return Format.getIntValue(getFieldAuftragID());
	}
	
	
	public void setAbrufID(int auftragID){
		getFieldAuftragID().setValue(auftragID);
	}


	public IField getFieldKostenstelle() {
		return getField("field." + getTableName() + ".kostenstelleid");
	}
	


}
