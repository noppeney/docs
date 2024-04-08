package pze.business.objects.reftables.projektverwaltung;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Aktionen im Rahmen der Projektverfolgung (Änderungen übernehmen, Projekt schließen...)
 * 
 * @author Lisiecki
 *
 */
public class CoAktionProjektverfolgung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblaktionprojektverfolgung";

	public static final int STATUSID_PROJEKT_SCHLIESSEN = 1;
	public static final int STATUSID_AENDERUNGEN_UEBERNEHMEN = 2;
	public static final int STATUSID_KEINE_AKTION = 3;

	private static CoAktionProjektverfolgung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoAktionProjektverfolgung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoAktionProjektverfolgung getInstance() throws Exception {
		if (CoAktionProjektverfolgung.m_instance == null)
		{
			CoAktionProjektverfolgung.m_instance = new CoAktionProjektverfolgung();
			CoAktionProjektverfolgung.m_instance.loadAll();
		}
		
		return CoAktionProjektverfolgung.m_instance;
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
