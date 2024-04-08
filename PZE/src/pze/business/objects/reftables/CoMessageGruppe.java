package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Zuordnung wer welche Message zur Quittierung bekommt
 * 
 * @author Lisiecki
 *
 */
public class CoMessageGruppe extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblmessagegruppe";

	public static final int ID_MITARBEITER = 1;
	public static final int ID_AL = 3;
	public static final int ID_VERWALTUNG = 4;
	public static final int ID_SEKRETAERIN = 5; // Meldungen über Sekretärinnen
	public static final int ID_PL = 6;
	public static final int ID_SEKRETARIAT = 7; // Meldungen für das Sekretariat
	public static final int ID_DR_INFO = 8;

	private static CoMessageGruppe m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoMessageGruppe() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoMessageGruppe getInstance() throws Exception {
		if (CoMessageGruppe.m_instance == null)
		{
			CoMessageGruppe.m_instance = new CoMessageGruppe();
			CoMessageGruppe.m_instance.loadAll();
		}
		
		return CoMessageGruppe.m_instance;
	}


}
