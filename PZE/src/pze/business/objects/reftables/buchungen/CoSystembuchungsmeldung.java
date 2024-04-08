package pze.business.objects.reftables.buchungen;

import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Meldungen zu Systembuchungen (aus der Geloc-DB)
 * 
 * @author Lisiecki
 *
 */
public class CoSystembuchungsmeldung extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblsystembuchungsmeldung";


	private static CoSystembuchungsmeldung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoSystembuchungsmeldung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoSystembuchungsmeldung getInstance() throws Exception {
		if (CoSystembuchungsmeldung.m_instance == null)
		{
			CoSystembuchungsmeldung.m_instance = new CoSystembuchungsmeldung();
			CoSystembuchungsmeldung.m_instance.loadAll();
		}
		
		return CoSystembuchungsmeldung.m_instance;
	}


	/**
	 * @return Bezeichnung des Sortierungsfeldes, voreingestellt, zweites Feld...
	 */
	protected String getSortFieldName() {
		return "buchungstypID, meldungNr";
	}
	

	/**
	 * Gehe zu dem Eintrag mit der EventNr
	 * 
	 * @param eventNr
	 * @return ID
	 */
	public int getID(int buchungstypID, int meldungNr) {
		
		if (!moveTo(buchungstypID, "field." + getTableName() + ".buchungstypid") || !moveOnToMeldungNr(meldungNr, buchungstypID))
		{
			return 0;
		}
		
		return getID();
	}
	

	/**
	 * weitergehen bis zur MeldungNr
	 * 
	 * @param meldungNr
	 */
	private boolean moveOnToMeldungNr(int meldungNr, int buchungstypID) {
		while (getMeldungNr() != meldungNr)
		{
			if (!moveNext())
			{
				return false;
			}
		}
		
		return getBuchungstypID() == buchungstypID;
	}


	public int getBuchungstypID() {
		return Format.getIntValue(getField("field." + getTableName() + ".buchungstypid").getValue());
	}


	public int getMeldungNr() {
		return Format.getIntValue(getField("field." + getTableName() + ".meldungnr").getValue());
	}


}
