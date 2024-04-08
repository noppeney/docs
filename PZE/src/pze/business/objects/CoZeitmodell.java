package pze.business.objects;

import java.util.Date;

import framework.Application;
import pze.business.Format;

/**
 * CacheObject für Zeitmodelle
 * 
 * @author Lisiecki
 *
 */
public class CoZeitmodell extends AbstractCacheObject {

	private static final String TABLE_NAME = "tblzeitmodell";
	public static final int ID_AUSHILFE = 1903352;

	private static CoZeitmodell m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoZeitmodell() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoZeitmodell getInstance() throws Exception {
		if (CoZeitmodell.m_instance == null)
		{
			CoZeitmodell.m_instance = new CoZeitmodell();
			CoZeitmodell.m_instance.loadAll();
		}
		
		return CoZeitmodell.m_instance;
	}


	@Override
	public String getNavigationBitmap() {
		return isInUse() ? "clock" : "clock.delete";
	}

	
	/**
	 * Tagessoll anhand des Wochentags bestimmen
	 * 
	 * @param datum
	 * @return
	 */
	public int getTagessoll(Date datum) {
		return Format.getIntValue(getField("field." + getTableName() + ".tagessoll" + Format.getWochentag(datum).toLowerCase()).getValue());
	}
	
	
	public boolean isMeldungArbeitszeitBeginnAktiv() {
		return Format.getBooleanValue(getField("field." + getTableName() + ".meldungarbeitszeitverletzungbeginn").getValue());
	}
	
	
	public boolean isMeldungArbeitszeitEndeAktiv() {
		return Format.getBooleanValue(getField("field." + getTableName() + ".meldungarbeitszeitverletzungende").getValue());
	}
	
	
	public boolean isMeldungArbZgKontostandAktiv() {
		return Format.getBooleanValue(getField("field." + getTableName() + ".meldungarbeitszeitgesetz").getValue());
	}
	
	
	public boolean isMeldungArbeitstagAktiv() {
		return Format.getBooleanValue(getField("field." + getTableName() + ".meldungarbeitstag").getValue());
	}
	
	
	public int getPausenmodellID() {
		return Format.getIntValue(getField("field." + getTableName() + ".pausenmodellid").getValue());
	}
	
	
	/**
	 * Bestimmt das Pausenmodell
	 * 
	 * @return
	 * @throws Exception 
	 */
	public CoPausenmodell getCoPausenmodell() throws Exception{
		CoPausenmodell coZeitmodell;

		coZeitmodell = new CoPausenmodell();
		coZeitmodell.loadByID(getPausenmodellID());

		return coZeitmodell;
	}
	

	/**
	 * Gibt die Wochenarbeitsstunden eines Zeitmodells zurück
	 * 
	 * @return Wochenarbeitsstunden als double
	 */
	public double getAnzWochenstunden(){
		return (Format.getIntValue(getField("field." + getTableName() + ".tagessollmontag").getValue())
				+ Format.getIntValue(getField("field." + getTableName() + ".tagessolldienstag").getValue())
				+ Format.getIntValue(getField("field." + getTableName() + ".tagessollmittwoch").getValue())
				+ Format.getIntValue(getField("field." + getTableName() + ".tagessolldonnerstag").getValue())
				+ Format.getIntValue(getField("field." + getTableName() + ".tagessollfreitag").getValue())
				+ Format.getIntValue(getField("field." + getTableName() + ".tagessollsamstag").getValue())
				+ Format.getIntValue(getField("field." + getTableName() + ".tagessollsonntag").getValue()))
				/ 60.;
	}
	
	
	/**
	 * Zeitmodell ist in Verwendung und darf damit nicht gelöscht werden
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isInUse() {
		String sql;
		
		try
		{
			sql = "SELECT COUNT(*) FROM stblPersonzeitmodell WHERE ZeitmodellID= " + getID();
			return Format.getIntValue(Application.getLoaderBase().executeScalar(sql)) > 0;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return true;
		}
	}
	
}
