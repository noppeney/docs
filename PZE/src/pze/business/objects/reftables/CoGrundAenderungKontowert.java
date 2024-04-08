package pze.business.objects.reftables;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Gründe für Kontowertänderungen
 * 
 * @author Lisiecki
 *
 */
public class CoGrundAenderungKontowert extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblgrundaenderungkontowert";
	
	public static final int ID_ZEITERFASSUNG_DIENSTREISE = 2;

	private static CoGrundAenderungKontowert m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoGrundAenderungKontowert() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoGrundAenderungKontowert getInstance() throws Exception {
		if (CoGrundAenderungKontowert.m_instance == null)
		{
			CoGrundAenderungKontowert.m_instance = new CoGrundAenderungKontowert();
			CoGrundAenderungKontowert.m_instance.loadAll();
		}
		
		return CoGrundAenderungKontowert.m_instance;
	}


}
