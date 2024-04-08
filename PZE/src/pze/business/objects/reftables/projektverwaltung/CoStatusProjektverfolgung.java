package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status bei der Projektverfolgung (OK, geändert...)
 * 
 * @author Lisiecki
 *
 */
public class CoStatusProjektverfolgung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusprojektverfolgung";

	public static final int STATUSID_OK = 1;
	public static final int STATUSID_GEAENDERT = 2;

	private static CoStatusProjektverfolgung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusProjektverfolgung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusProjektverfolgung getInstance() throws Exception {
		if (CoStatusProjektverfolgung.m_instance == null)
		{
			CoStatusProjektverfolgung.m_instance = new CoStatusProjektverfolgung();
			CoStatusProjektverfolgung.m_instance.loadAll();
		}
		
		return CoStatusProjektverfolgung.m_instance;
	}

	
//	/**
//	 * StatusID eines abgeschlossenen Projekts (Fertigmeldung oder abgerechnet) 
//	 * 
//	 * @param id
//	 * @return
//	 */
//	public static boolean isAbgeschlossen(int id){
//		return id == STATUSID_FERTIGMELDUNG || id == STATUSID_ABGERECHNET;
//	}

}
