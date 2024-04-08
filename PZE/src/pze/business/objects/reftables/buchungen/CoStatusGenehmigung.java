package pze.business.objects.reftables.buchungen;

import framework.Application;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Status von Genehmigungen
 * 
 * @author Lisiecki
 */
public class CoStatusGenehmigung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblstatusgenehmigung";

	public static final int STATUSID_GEPLANT = 1;
	public static final int STATUSID_BEANTRAGT = 2;
	public static final int STATUSID_FREIGEGEBEN_VERTRETER = 3;
	public static final int STATUSID_FREIGEGEBEN_AL = 5;
	public static final int STATUSID_FREIGEGEBEN_AUSBILDER = 10;
	public static final int STATUSID_FREIGEGEBEN_PB = 9;
	public static final int STATUSID_GENEHMIGT = 6;
	public static final int STATUSID_ABGELEHNT = 7;
	public static final int STATUSID_GELOESCHT = 8;

	private static CoStatusGenehmigung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoStatusGenehmigung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoStatusGenehmigung getInstance() throws Exception {
		if (CoStatusGenehmigung.m_instance == null)
		{
			CoStatusGenehmigung.m_instance = new CoStatusGenehmigung();
			CoStatusGenehmigung.m_instance.loadAll();
		}
		
		return CoStatusGenehmigung.m_instance;
	}

	
	/**
	 * Für Auswertung laden
	 * 
	 * @throws Exception
	 */
	public void loadForAuswertung() throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID IN (" + STATUSID_GEPLANT + ", " + STATUSID_BEANTRAGT + ", " + STATUSID_FREIGEGEBEN_AL + ", " 
		+ STATUSID_GENEHMIGT + ")", getSortFieldName());
	}


	public static boolean isGeplant(int statusID) {
		return statusID == STATUSID_GEPLANT;
	}

	
	public static boolean isGenehmigt(int statusID) {
		return statusID == STATUSID_GENEHMIGT;
	}

	
	public static boolean isUngueltig(int statusID) {
		return statusID == STATUSID_GELOESCHT;
	}

	
	public static boolean isAbgelehnt(int statusID) {
		return statusID == STATUSID_ABGELEHNT;
	}


	/**
	 * Die Buchung mit dem übergebenen Status darf von der angemeldeten Person selbst geändert werden, da sie vorläufig ist
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean isSelbstbuchungAenderungZulaessig(int id) throws Exception {
		return id == 0 || id == STATUSID_GEPLANT || id == STATUSID_ABGELEHNT; 
	}
	

	/**
	 * String mit den IDs für Stati im Zustand der Beantragung
	 * 
	 * @return IDs in (...)
	 */
	public static String getIDsBeantragt() {
		return "(" + STATUSID_BEANTRAGT + ", " + STATUSID_FREIGEGEBEN_VERTRETER + ", " 
				+ STATUSID_FREIGEGEBEN_AL + ", " + STATUSID_FREIGEGEBEN_PB + ")"; 
	}
	

	
}
