package pze.business.objects.reftables;

import framework.Application;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Benutzer aus der Systemtabelle
 * 
 * @author Lisiecki
 *
 */
public class CoUser extends AbstractCacheObject {

	private static final String TABLE_NAME = "tblextusers";

//	private static CoUser m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoUser() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
//	public static CoUser getInstance() throws Exception {
//		if (CoUser.instance == null)
//		{
//			CoUser.instance = new CoUser();
//			CoUser.instance.loadAll();
//		}
//		
//		return CoUser.instance;
//	}


	/**
	 * Alle UserIDs, die keinem Benutzer zugeordnet sind
	 * 
	 * @param userID aktuelle userID, da die auch geladen werden muss
	 * @throws Exception
	 */
	public void loadAllUnused(int userID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "userID=" + userID + " OR userID NOT IN (SELECT userID FROM tblPerson WHERE userID IS NOT NULL)", getSortFieldName());
	}


}
