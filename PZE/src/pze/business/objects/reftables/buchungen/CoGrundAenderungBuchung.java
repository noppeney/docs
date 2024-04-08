package pze.business.objects.reftables.buchungen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Gründe für Buchungsänderungen
 * 
 * @author Lisiecki
 *
 */
public class CoGrundAenderungBuchung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblgrundaenderungbuchung";

	public static final int ID_DIENSTREISEANTRAG = 2;
	public static final int ID_URLAUBSANTRAG = 3;
	public static final int ID_FA_ANTRAG = 4;
	public static final int ID_KRANKMELDUNG = 5;
	public static final int ID_DIENSTGANGANTRAG = 10;
	public static final int ID_WOCHENPLAN = 14;
	public static final int ID_KGG = 15;
	public static final int ID_OFA_ANTRAG = 16;
	public static final int ID_URLAUBSPLANUNG = 17;

	private static CoGrundAenderungBuchung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoGrundAenderungBuchung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoGrundAenderungBuchung getInstance() throws Exception {
		if (CoGrundAenderungBuchung.m_instance == null)
		{
			CoGrundAenderungBuchung.m_instance = new CoGrundAenderungBuchung();
			CoGrundAenderungBuchung.m_instance.loadAll();
		}
		
		return CoGrundAenderungBuchung.m_instance;
	}


}
