package pze.business.objects.personen;

import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;

/**
 * CacheObject für Buchungsfreigaben
 * 
 * @author Lisiecki
 *
 */
public class CoFreigabe extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblfreigabe";
	


	/**
	 * Kontruktor
	 */
	public CoFreigabe() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * CO für die Buchung laden.<br>
	 * 
	 * @param buchungID
	 * @param datum
	 * @throws Exception
	 */
	public void load(int buchungID) throws Exception {
		int zeit;
		GregorianCalendar gregDatum;

		removeField("field.tblbuchung.uhrzeitasint");

		emptyCache();
		Application.getLoaderBase().load(this, "BuchungID=" + buchungID, getSortFieldName());
		
		// Freigaben durchlaufen und die Uhrzeit aus dem Datum filtern
		if (moveFirst())
		{
			addField("field.tblbuchung.uhrzeitasint");

			do
			{
				gregDatum = getGregDatum();
				
				// Uhrzeit übertragen
				zeit = gregDatum.get(GregorianCalendar.HOUR_OF_DAY)*60 + gregDatum.get(GregorianCalendar.MINUTE);
				if (zeit > 0)
				{
					setUhrzeitAsInt(zeit);
				}
			} while (moveNext());
		}
		
		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	
	}
	

	@Override
	public String getNavigationBitmap() {
		return "calendar.edit";
	}


	/**
	 * Nach Datum sortieren
	 * 
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "Datum";
	}
	

	private IField getFieldBuchungID() {
		return getField("field." + getTableName() + ".buchungid");
	}


	private void setBuchungID(int buchungID) {
		getFieldBuchungID().setValue(buchungID);
	}


//	private int getBuchungID() {
//		return Format.getIntValue(getFieldBuchungID());
//	}


	private IField getFieldStatusBuchungID() {
		return getField("field." + getTableName() + ".statusbuchungid");
	}


	private int getStatusBuchungID() throws Exception {
		return Format.getIntValue(getFieldStatusBuchungID().getValue());
	}


	public String getStatusBuchung() throws Exception {
		return CoStatusBuchung.getInstance().getBezeichnung(getStatusBuchungID());
	}


	private void setStatusBuchungID(int statusID) {
		getFieldStatusBuchungID().setValue(statusID);
	}


	private IField getFieldStatusGenehmigungID() {
		return getField("field." + getTableName() + ".statusgenehmigungid");
	}


	private int getStatusGenehmigungID() throws Exception {
		return Format.getIntValue(getFieldStatusGenehmigungID().getValue());
	}


	public String getStatusGenehmigung() throws Exception {
		return CoStatusGenehmigung.getInstance().getBezeichnung(getStatusGenehmigungID());
	}


	private void setStatusGenehmigungID(int statusID) {
		getFieldStatusGenehmigungID().setValue(statusID);
	}


//	private void setStatusGeplant() throws Exception {
//		setStatusID(CoStatusGenehmigung.STATUSID_GEPLANT);
//	}
//
//
//	private void setStatusBeantragt() throws Exception {
//		setStatusID(CoStatusGenehmigung.STATUSID_BEANTRAGT);
//	}
//
//
//	private void setStatusGenehmigt() throws Exception {
//		setStatusID(CoStatusGenehmigung.STATUSID_GENEHMIGT);
//	}


	private IField getFieldUhrzeitAsInt() {
		return getField("field.tblbuchung.uhrzeitasint");
	}


	private void setUhrzeitAsInt(Integer uhrzeitAsInt) {
		getFieldUhrzeitAsInt().setValue(uhrzeitAsInt);
	}


	/**
	 * Neuen Datensatz zur Dokumentierung der Freigabe erzeugen
	 * 
	 * @param datum 
	 * @param personID 
	 */
	public int createNew(int buchungID, int statusBuchungID, int statusGenehmigungID, int personID, Date datum) throws Exception	{
		
		// neuen Datensatz anlegen
		super.createNew();
		
		setBuchungID(buchungID);
		setStatusBuchungID(statusBuchungID);
		setStatusGenehmigungID(statusGenehmigungID);
		
		setPersonID(personID);
		setDatum(new Timestamp(datum.getTime()));
		
		return getID();
	}


}
