package pze.business.objects;

import java.util.Date;

import pze.business.Format;

/**
 * CacheObject für Firmenparameter
 * 
 * @author Lisiecki
 *
 */
public class CoFirmenparameter extends AbstractCacheObject {

	private static final String TABLE_NAME = "tblfirmenparameter";


	private static CoFirmenparameter m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoFirmenparameter() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoFirmenparameter getInstance() throws Exception {
		if (CoFirmenparameter.m_instance == null)
		{
			CoFirmenparameter.m_instance = new CoFirmenparameter();
			CoFirmenparameter.m_instance.loadAll();
		}
		
		return CoFirmenparameter.m_instance;
	}


	@Override
	public String getNavigationBitmap() {
		return "lib.application";
	}

	
	public int getKernzeitBeginn(Date datum) {
		if (Format.isMoBisDo(datum))
		{
			return getKernzeitBeginnMoDo();
		}
		else if (Format.isFreitag(datum))
		{
			return getKernzeitBeginnFr();
		}
		
		return 0;
	}

	
	public int getKernzeitEnde(Date datum) {
		if (Format.isMoBisDo(datum))
		{
			return getKernzeitEndeMoDo();
		}
		else if (Format.isFreitag(datum))
		{
			return getKernzeitEndeFr();
		}
		
		return 0;
	}

	
	public int getKernzeitBeginnMoDo() {
		return Format.getIntValue(getField("field." + getTableName() + ".kernzeitbeginnmodo").getValue());
	}


	public int getKernzeitEndeMoDo() {
		return Format.getIntValue(getField("field." + getTableName() + ".kernzeitendemodo").getValue());
	}


	public int getKernzeitBeginnFr() {
		return Format.getIntValue(getField("field." + getTableName() + ".kernzeitbeginnfr").getValue());
	}


	public int getKernzeitEndeFr() {
		return Format.getIntValue(getField("field." + getTableName() + ".kernzeitendefr").getValue());
	}

	
	public int getRahmenarbeitszeitBeginn(Date datum) {
		if (Format.isMoBisDo(datum))
		{
			return getRahmenarbeitszeitBeginnMoDo();
		}
		else if (Format.isFreitag(datum))
		{
			return getRahmenarbeitszeitBeginnFr();
		}
		
		return 0;
	}

	
	public int getRahmenarbeitszeitEnde(Date datum) {
		if (Format.isMoBisDo(datum))
		{
			return getRahmenarbeitszeitEndeMoDo();
		}
		else if (Format.isFreitag(datum))
		{
			return getRahmenarbeitszeitEndeFr();
		}
		
		return 0;
	}


	public int getRahmenarbeitszeitBeginnMoDo() {
		return Format.getIntValue(getField("field." + getTableName() + ".rahmenarbeitszeitbeginnmodo").getValue());
	}


	public int getRahmenarbeitszeitEndeMoDo() {
		return Format.getIntValue(getField("field." + getTableName() + ".rahmenarbeitszeitendemodo").getValue());
	}


	public int getRahmenarbeitszeitBeginnFr() {
		return Format.getIntValue(getField("field." + getTableName() + ".rahmenarbeitszeitbeginnfr").getValue());
	}


	public int getRahmenarbeitszeitEndeFr() {
		return Format.getIntValue(getField("field." + getTableName() + ".rahmenarbeitszeitendefr").getValue());
	}


	public int getPausenBeginn() {
		return Format.getIntValue(getField("field." + getTableName() + ".pausenbeginn").getValue());
	}


	public int getPausenEnde() {
		return Format.getIntValue(getField("field." + getTableName() + ".pausenende").getValue());
	}


	public int getMaxPausendauer() {
		return Format.getIntValue(getField("field." + getTableName() + ".maxpausendauer").getValue());
	}


	public int getMaxTagesarbeitszeit() {
		return Format.getIntValue(getField("field." + getTableName() + ".maxtagesarbeitszeit").getValue());
	}


	public int getMaxWochenarbeitszeit() {
		return Format.getIntValue(getField("field." + getTableName() + ".maxwochenarbeitszeit").getValue());
	}


	public int getMaxUeberstunden() {
		return Format.getIntValue(getField("field." + getTableName() + ".maxueberstunden").getValue());
	}


	public int getMinUeberstunden() {
		return Format.getIntValue(getField("field." + getTableName() + ".minueberstunden").getValue());
	}


	public Date getDatumMonatseinsatzblatt() {
		return Format.getDateValue(getField("field." + getTableName() + ".datummonatseinsatzblatt").getValue());
	}


	public Date getDatumUrlaubsplanungAb() {
		return Format.getDateValue(getField("field." + getTableName() + ".datumurlaubsplanungab").getValue());
	}


	public Date getDatumUrlaubsplanungBis() {
		return Format.getDateValue(getField("field." + getTableName() + ".datumurlaubsplanungbis").getValue());
	}


	public double getKmPauschale() {
		return Format.getDoubleValue(getField("field." + getTableName() + ".kmpauschale").getValue());
	}


	public double getVerpflegungspauschale8h() {
		return Format.getDoubleValue(getField("field." + getTableName() + ".verpflegungspauschale8h").getValue());
	}


	public double getVerpflegungspauschale24h() {
		return Format.getDoubleValue(getField("field." + getTableName() + ".verpflegungspauschale24h").getValue());
	}


	public double getVerpflegungspauschaleFruestueck() {
		return Format.getDoubleValue(getField("field." + getTableName() + ".verpflegungspauschalefruestueck").getValue());
	}


	public double getVerpflegungspauschaleMittag() {
		return Format.getDoubleValue(getField("field." + getTableName() + ".verpflegungspauschalemittag").getValue());
	}


	public double getVerpflegungspauschaleAbend() {
		return Format.getDoubleValue(getField("field." + getTableName() + ".verpflegungspauschaleabend").getValue());
	}



}
