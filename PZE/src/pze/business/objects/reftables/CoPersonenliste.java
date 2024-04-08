package pze.business.objects.reftables;

import framework.Application;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Personenlisten
 * 
 * @author Lisiecki
 *
 */
public class CoPersonenliste extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblpersonenliste";

	private static CoPersonenliste m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoPersonenliste() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoPersonenliste getInstance() throws Exception {
		if (CoPersonenliste.m_instance == null)
		{
			CoPersonenliste.m_instance = new CoPersonenliste();
			CoPersonenliste.m_instance.loadAll();
		}
		
		return CoPersonenliste.m_instance;
	}


	/**
	 * CO mit den Items für die Personen laden, für die der aktuelle User Berechtigungen besitzt
	 * 
	 * @throws Exception
	 */
	public void loadItemsOfCurrentUser() throws Exception {
		String where;
		
		where = "";
		
		// Personalansicht und AL darf alle sehen
		if (!UserInformation.getInstance().isPersonalansicht() && !UserInformation.getInstance().isNurAL())
		{
			// Alle Gruppen in denen er selbst drin ist
			where = " Oeffentlich=1 AND ID IN (SELECT PersonenlisteID FROM stblPersonenPersonenliste WHERE PersonID=" 
					+ UserInformation.getPersonID() + ")";
		}
			 
		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}


}
