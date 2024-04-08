package pze.business.objects.auswertung;

import java.util.Calendar;
import java.util.GregorianCalendar;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.reftables.projektverwaltung.CoAusgabezeitraum;

/**
 * CacheObject für die Einstellungen der Auswertung der Projekte 
 * 
 * @author Lisiecki
 */
public class CoAuswertungProjekt extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungprojekte";



	/**
	 * Kontruktor
	 */
	public CoAuswertungProjekt() {
		super(TABLE_NAME);
	}
	
	
	/**
	 * Kontruktor
	 */
	public CoAuswertungProjekt(String tableName) {
		super(tableName);
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
		
		// monatliche Auswertung setzen
		setAusgabezeitraumID(CoAusgabezeitraum.ID_MONATLICH);
		
		// speichern, falls ein neues Objekt angelegt wird (kann sonst zu Fehlern führen, 15.01.2019)
		save();
		begin();

		return id;
	}
	

	public IField getFieldAusgabezeitraumID() {
		return getField("field." + getTableName() + ".ausgabezeitraumid");
	}


	public int getAusgabezeitraumID() {
		return Format.getIntValue(getFieldAusgabezeitraumID().getValue());
	}


	public void setAusgabezeitraumID(int id) {
		getFieldAusgabezeitraumID().setValue(id);
	}


	public IField getFieldLetztenMonatPruefen() {
		return getField("field." + getTableName() + ".letztenmonatpruefen");
	}


	public boolean isLetztenMonatPruefenAktiv() {
		return Format.getBooleanValue(getFieldLetztenMonatPruefen().getValue());
	}

}
