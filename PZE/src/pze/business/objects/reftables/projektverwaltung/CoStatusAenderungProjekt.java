package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status der Änderung bei der Projektverfolgung 
 * 
 * @author Lisiecki
 *
 */
public class CoStatusAenderungProjekt extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusaenderungprojekt";

	public static final int STATUSID_KUNDE_INFORMIERT = 1;
	public static final int STATUSID_KUNDE_NICHT_INFORMIERT = 2;
	public static final int STATUSID_ZUGESTIMMT = 3;
	public static final int STATUSID_KEINE_AENDERUNG = 4;

	private static CoStatusAenderungProjekt m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusAenderungProjekt() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusAenderungProjekt getInstance() throws Exception {
		if (CoStatusAenderungProjekt.m_instance == null)
		{
			CoStatusAenderungProjekt.m_instance = new CoStatusAenderungProjekt();
			CoStatusAenderungProjekt.m_instance.loadAll();
		}
		
		return CoStatusAenderungProjekt.m_instance;
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
