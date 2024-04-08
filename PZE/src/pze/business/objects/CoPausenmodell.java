package pze.business.objects;

import pze.business.Format;

/**
 * CacheObject für Pausenmodelle
 * 
 * @author Lisiecki
 *
 */
public class CoPausenmodell extends AbstractCacheObject {

	private static final String TABLE_NAME = "tblpausenmodell";


	private static CoPausenmodell m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoPausenmodell() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoPausenmodell getInstance() throws Exception {
		if (CoPausenmodell.m_instance == null)
		{
			CoPausenmodell.m_instance = new CoPausenmodell();
			CoPausenmodell.m_instance.loadAll();
		}
		
		return CoPausenmodell.m_instance;
	}


	@Override
	public String getNavigationBitmap() {
		return "cup";
	}

	
	private int getZeitBisPause1() {
		return Format.getIntValue(getField("field." + getTableName() + ".zeitbispause1").getValue());
	}
	
	
	private int getZeitBisPause2() {
		return Format.getIntValue(getField("field." + getTableName() + ".zeitbispause2").getValue());
	}
	
	
	private int getDauerPause1() {
		return Format.getIntValue(getField("field." + getTableName() + ".dauerpause1").getValue());
	}
	
	
	private int getDauerPause2() {
		return Format.getIntValue(getField("field." + getTableName() + ".dauerpause2").getValue());
	}
	
	
//	public int getDauerPauseMax() {
//		return Format.getIntValue(getField("field." + getTableName() + ".maximalzeit").getValue());
//	}
	
	
	/**
	 * Minimale Pausendauer für die übergebene Arbeitszeit (inkl. Pause) berechnen.
	 * 
	 * @param arbeitszeitMitPause
	 * @return
	 */
	public int getDauerPauseMin(int arbeitszeitMitPause) {
		int pause, zeitBisPause, dauerPause;
		
		zeitBisPause = getZeitBisPause1();
		dauerPause = getDauerPause1();
		pause = getPause(arbeitszeitMitPause, zeitBisPause, dauerPause);

		if (pause > 0)
		{
			arbeitszeitMitPause -= pause;
			
			zeitBisPause = getZeitBisPause2();
			dauerPause = getDauerPause2();
			pause += getPause(arbeitszeitMitPause, zeitBisPause, dauerPause);
		}

		return pause;
	}


	/**
	 * Mindestpause für die übergebene Arbeitszeit und die Pause.<br>
	 * Die Zeit bis zu Beginn der Pause darf nach Abzug der Pause nicht unterschritten werden.<br>
	 * Beispiel: bei 6:15 werden nur 15 statt 30 Minuten Pause abgezigen.    
	 * 
	 * @param arbeitszeit
	 * @param zeitBisPause
	 * @param dauerPause
	 * @return
	 */
	private int getPause(int arbeitszeit, int zeitBisPause, int dauerPause) {

		if (arbeitszeit > zeitBisPause)
		{
			return Math.min(arbeitszeit - zeitBisPause, dauerPause);
		}
		
		return 0;
	}
	

}
