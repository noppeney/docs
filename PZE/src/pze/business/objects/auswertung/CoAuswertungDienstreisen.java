package pze.business.objects.auswertung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;

/**
 * CacheObject für die Einstellungen der Auswertung der Dienstreisen
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungDienstreisen extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungdienstreisen";



	/**
	 * Kontruktor
	 */
	public CoAuswertungDienstreisen() {
		this(TABLE_NAME);
	}
	

	/**
	 * Kontruktor
	 */
	public CoAuswertungDienstreisen(String tableName) {
		super(tableName);
	}
	

	/**
	 * bei neuen Einträgen nur aktive MA
	 * 
	 * @see pze.business.objects.AbstractCacheObject#createNew()
	 */
	@Override
	public int createNew(int userID) throws Exception	{
		int id;
		
		id = super.createNew(userID);
		
		// aktive Personen
		setStatusAktivInaktiv(CoStatusAktivInaktiv.STATUSID_AKTIV);
		
		// alle DR
		setAusgabeAbgerechnet(true);
		setAusgabeNichtAbgerechnet(true);
		
		return id;
	}
	

	private IField getFieldDetailAusgabe() {
		return getField("field." + getTableName() + ".detailausgabe");
	}


	public boolean isDetailAusgabe() {
		return Format.getBooleanValue(getFieldDetailAusgabe());
	}


	private IField getFieldAusgabeAbgerechnet() {
		return getField("field." + getTableName() + ".ausgabeabgerechnet");
	}


	public boolean isAusgabeAbgerechnet() {
		return Format.getBooleanValue(getFieldAusgabeAbgerechnet());
	}


	private void setAusgabeAbgerechnet(boolean isAktiv) {
		getFieldAusgabeAbgerechnet().setValue(isAktiv);
	}


	private IField getFieldAusgabeNichtAbgerechnet() {
		return getField("field." + getTableName() + ".ausgabenichtabgerechnet");
	}


	public boolean isAusgabeNichtAbgerechnet() {
		return Format.getBooleanValue(getFieldAusgabeNichtAbgerechnet());
	}


	private void setAusgabeNichtAbgerechnet(boolean isAktiv) {
		getFieldAusgabeNichtAbgerechnet().setValue(isAktiv);
	}


	private IField getFieldBuchungsartID() {
		return getField("field." + getTableName() + ".buchungsartid");
	}


	public int getBuchungsartID() {
		return Format.getIntValue(getFieldBuchungsartID());
	}


	private IField getFieldKundeID() {
		return getField("field." + getTableName() + ".kundeid");
	}


	public int getKundeID() {
		return Format.getIntValue(getFieldKundeID());
	}


	private IField getFieldStatusGenehmigungID() {
		return getField("field." + getTableName() + ".statusgenehmigungid");
	}


	public int getStatusGenehmigungID() {
		return Format.getIntValue(getFieldStatusGenehmigungID());
	}


}
