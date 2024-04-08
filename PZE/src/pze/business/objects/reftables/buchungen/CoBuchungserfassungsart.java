package pze.business.objects.reftables.buchungen;

import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Buchungserfassungsarten (Vorder-/Hintertür, PC)
 * 
 * @author Lisiecki
 *
 */
public class CoBuchungserfassungsart extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblbuchungserfassungsart";

	public static final int ID_VORDERTUER = 1;
	public static final int ID_HINTERTUER = 2;
	public static final int ID_PC = 3;

	private static CoBuchungserfassungsart m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoBuchungserfassungsart() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoBuchungserfassungsart getInstance() throws Exception {
		if (CoBuchungserfassungsart.m_instance == null)
		{
			CoBuchungserfassungsart.m_instance = new CoBuchungserfassungsart();
			CoBuchungserfassungsart.m_instance.loadAll();
		}
		
		return CoBuchungserfassungsart.m_instance;
	}
	

	public int getSystemNr() {
		return Format.getIntValue(getField("field." + getTableName() + ".systemnr"));
	}


	/**
	 * Gehe zu dem Eintrag mit der SystemNr
	 * 
	 * @param ID
	 * @return systemNr aus Geloc
	 */
	public int getSystemNr(int id) {
		
		if (!moveToID(id))
		{
			return 0;
		}
		
		return getSystemNr();
	}
	

	/**
	 * Gehe zu dem Eintrag mit der SystemNr
	 * 
	 * @param bezeichnung
	 * @return ID
	 */
	public int getIdFromSystemNr(int systemNr) {
		
		if (!moveTo(systemNr, "field." + getTableName() + ".systemnr"))
		{
			return 0;
		}
		
		return getID();
	}
	

}
