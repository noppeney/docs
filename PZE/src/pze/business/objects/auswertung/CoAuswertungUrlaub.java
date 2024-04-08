package pze.business.objects.auswertung;

import java.util.Calendar;
import java.util.GregorianCalendar;

import framework.business.interfaces.fields.IField;
import pze.business.Format;

/**
 * CacheObject für die Einstellungen der Auswertung der Urlaubsbuchungen
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungUrlaub extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungurlaub";



	/**
	 * Kontruktor
	 */
	public CoAuswertungUrlaub() {
		super(TABLE_NAME);
	}
	
	
	/**
	 * bei neuen Einträgen aktuelles Jahr eintragen
	 * 
	 * @see pze.business.objects.AbstractCacheObject#createNew()
	 */
	@Override
	public int createNew(int userID) throws Exception	{
		int id;
		GregorianCalendar gregDatum;
		
		id = super.createNew();
		
		setUserID(userID);
		
		// Auswertungszeitraum das aktuelle Jahr
		gregDatum = Format.getGregorianCalendar(null);
		gregDatum.set(GregorianCalendar.MONTH, Calendar.JANUARY);
		gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 1);
		setDatumVon(Format.getDateValue(gregDatum));
		
		gregDatum.set(GregorianCalendar.MONTH, Calendar.DECEMBER);
		gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 31);
		setDatumBis(Format.getDateValue(gregDatum));
		
		// aktive Personen
//		setStatusAktivInaktiv(CoStatusAktivInaktiv.STATUSID_AKTIV);
		
		return id;
	}
	

	public IField getFieldStatusBuchungID() {
		return getField("field." + getTableName() + ".statusbuchungid");
	}


	public int getStatusBuchungID() {
		return Format.getIntValue(getFieldStatusBuchungID());
	}


	public String getStatusBuchung() {
		return getFieldStatusBuchungID().getDisplayValue();
	}


}
