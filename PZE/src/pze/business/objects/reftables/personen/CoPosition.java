package pze.business.objects.reftables.personen;

import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Position
 * 
 * @author Lisiecki
 *
 */
public class CoPosition extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblposition";

	public static final int ID_KL = 1;
	public static final int ID_TL = 2;
	public static final int ID_AL = 3;
//	public static final int ID_GL = 4;
	public static final int ID_INGENIEUR = 5;
	public static final int ID_TECHNIKER = 6;
	public static final int ID_SACHBEARBEITER = 7;
	public static final int ID_SEKRETAERIN = 8;
	public static final int ID_REINIGUNGSKRAFT = 9;
	public static final int ID_BÜROHILFSKRAFT = 10;
	public static final int ID_AZUBI = 11;
	public static final int ID_AUSHILFE = 12;
	public static final int ID_FREMDLEISTER = 13;
	public static final int ID_STUDENTISCHE_HILFSKRAFT = 14;
//	public static final int ID_GESCHAEFTSFUEHRUNG = 15;
	public static final int ID_SYSTEMADMINISTRATOR = 16;
	public static final int ID_SE = 17;
	public static final int ID_GFV = 18;
	public static final int ID_GFT = 19;

	private static CoPosition m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoPosition() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoPosition getInstance() throws Exception {
		if (CoPosition.m_instance == null)
		{
			CoPosition.m_instance = new CoPosition();
			CoPosition.m_instance.loadAll();
		}
		
		return CoPosition.m_instance;
	}


}
