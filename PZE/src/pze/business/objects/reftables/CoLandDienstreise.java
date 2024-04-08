package pze.business.objects.reftables;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Länder einer Dienstreise
 * 
 * @author Lisiecki
 *
 */
public class CoLandDienstreise extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtbllanddienstreise";

	public static final int STATUSID_DEUTSCHLAND = 1;

	private static CoLandDienstreise m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoLandDienstreise() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoLandDienstreise getInstance() throws Exception {
		if (CoLandDienstreise.m_instance == null)
		{
			CoLandDienstreise.m_instance = new CoLandDienstreise();
			CoLandDienstreise.m_instance.loadAll();
		}
		
		return CoLandDienstreise.m_instance;
	}

	
	private IField getFieldHinweis() {
		return getField("field." + getTableName() + ".hinweis");
	}


	public String getHinweis() {
		return Format.getStringValue(getFieldHinweis().getValue());
	}


	private IField getFieldA1() {
		return getField("field." + getTableName() + ".a1");
	}


	public boolean isA1() {
		return Format.getBooleanValue(getFieldA1());
	}


	/**
	 * Hinweis zu einem Land zurückgeben
	 * 
	 * @param landID
	 * @return
	 * @throws Exception 
	 */
	public static String getHinweise(int landID) throws Exception {
		// ggf. Instanz erstellen
		if (m_instance == null)
		{
			getInstance();
		}

		if (!m_instance.moveToID(landID))
		{
			return null;
		}
		
		return m_instance.getHinweis();
	}



}
