package pze.business.objects.auswertung;

import java.util.Calendar;
import java.util.GregorianCalendar;

import framework.business.interfaces.fields.IField;
import pze.business.Format;

/**
 * CacheObject für die Einstellungen der Auswertung der Ampelliste
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungKGG extends CoAuswertungAmpelliste {

	public static final String TABLE_NAME = "tblauswertungkgg";



	/**
	 * Kontruktor
	 */
	public CoAuswertungKGG() {
		super(TABLE_NAME);
	}
	
	
	/**
	 * Neuen Datensatz für die aktuell übergebene Person anlegen.<br>
	 * Default-Werte für Datum dieses Jahr und Ausgabe in Monaten
	 * 
	 * @see pze.business.objects.AbstractCacheObject#createNew()
	 */
	public int createNew(int userID) throws Exception	{
		int id;
		GregorianCalendar gregDatum;
		
		id = super.createNew(userID);
		
		// 1.1. des Jahres setzen
		gregDatum = Format.getGregorianCalendar12Uhr(null);
		gregDatum.set(Calendar.MONTH, Calendar.JANUARY);
		gregDatum.set(Calendar.DAY_OF_MONTH, 1);
		setDatumVon(Format.getDateValue(gregDatum));
		
		// bis heute
		gregDatum.set(Calendar.MONTH, Calendar.DECEMBER);
		gregDatum.set(Calendar.DAY_OF_MONTH, 31);
		setDatumBis(Format.getDateValue(gregDatum));
		
		return id;
	}
	

//	private IField getFieldKostenstelleID2() {
//		return getField("field." + getTableName() + ".kostenstelleid2");
//	}
//
//
//	public int getKostenstelleID2() {
//		return Format.getIntValue(getFieldKostenstelleID2().getValue());
//	}


	private IField getFieldBerichtsNrID() {
		return getField("field." + getTableName() + ".berichtsnrid");
	}


	public int getBerichtsNrID() {
		return Format.getIntValue(getFieldBerichtsNrID());
	}


	private IField getFieldStundenartID() {
		return getField("field." + getTableName() + ".stundenartid");
	}


	public int getStundenartID() {
		return Format.getIntValue(getFieldStundenartID());
	}




//	public IField getFieldAusgabezeitraumID() {
//		return getField("field." + getTableName() + ".ausgabezeitraumid");
//	}
//
//
//	public int getAusgabezeitraumID() {
//		return Format.getIntValue(getFieldAusgabezeitraumID().getValue());
//	}
//
//
//	public void setAusgabezeitraumID(int id) {
//		getFieldAusgabezeitraumID().setValue(id);
//	}


}
