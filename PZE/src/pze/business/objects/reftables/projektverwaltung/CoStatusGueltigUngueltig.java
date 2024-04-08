package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status gültig/ungültig
 * 
 * @author Lisiecki
 *
 */
public class CoStatusGueltigUngueltig extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusgueltigungueltig";

	public static final int STATUSID_GUELTIG = 1;
	public static final int STATUSID_UNGUELTIG = 2;
	
	private static CoStatusGueltigUngueltig m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusGueltigUngueltig() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusGueltigUngueltig getInstance() throws Exception {
		if (CoStatusGueltigUngueltig.m_instance == null)
		{
			CoStatusGueltigUngueltig.m_instance = new CoStatusGueltigUngueltig();
			CoStatusGueltigUngueltig.m_instance.loadAll();
		}
		
		return CoStatusGueltigUngueltig.m_instance;
	}

	
	/**
	 * StatusID prüfen
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isGueltig(int id){
		return id == STATUSID_GUELTIG;
	}

}
