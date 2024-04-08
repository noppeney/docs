package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status von Projekten (laufend, ruhen, abgerechnet...)
 * 
 * @author Lisiecki
 *
 */
public class CoStatusProjekt extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusprojekt";

	public static final int STATUSID_LAUFEND = 1;
	public static final int STATUSID_RUHEND = 2;
	
	public static final int STATUSID_ABGESCHLOSSEN= 3;
//	public static final int STATUSID_ABGERECHNET = 4; in Version 4.3 mit Status Fertigmeldung zusammengeführt zu Status abgeschlossen

	public static final int STATUSID_H = 5;

	private static CoStatusProjekt m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoStatusProjekt() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoStatusProjekt getInstance() throws Exception {
		if (CoStatusProjekt.m_instance == null)
		{
			CoStatusProjekt.m_instance = new CoStatusProjekt();
			CoStatusProjekt.m_instance.loadAll();
		}
		
		return CoStatusProjekt.m_instance;
	}

	
	/**
	 * StatusID eines abgeschlossenen Projekts (Fertigmeldung oder abgerechnet) 
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isAbgeschlossen(int id){
		return id == STATUSID_ABGESCHLOSSEN 
//				|| id == STATUSID_ABGERECHNET
				;
	}

}
