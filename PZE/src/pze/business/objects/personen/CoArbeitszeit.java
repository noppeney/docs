package pze.business.objects.personen;

import java.util.Calendar;
import java.util.GregorianCalendar;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.auswertung.CoAuswertung;


/**
 * Cacheobject mit den Daten für die Auswertung Buchhaltung-Arbeitszeit, so wie sie in der Oberfläche angezeigt wird.
 * 
 * @author Lisiecki
 */
public class CoArbeitszeit extends AbstractCacheObject {
	
	private static final int ANZAHL_MONATE = 6;

	private static final String TABLENAME = "arbeitszeit";
	
	public static final String RESID_WERKTAGE = "field." + "werktage";
	public static final String RESID_MAX = "field." + "max";
	public static final String RESID_IST = "field." + "ist";
	public static final String RESID_DIFFERENZ = "field." + "differenz";
	private static final String RESID_SALDO = "field." + "saldo";

	private static final String BESCHRIFTUNG_WERKTAGE = "Werktage";
	private static final String BESCHRIFTUNG_MAX = "Max";
	private static final String BESCHRIFTUNG_IST = "Ist";
	private static final String BESCHRIFTUNG_DIFFERENZ = "Differenz";
	private static final String BESCHRIFTUNG_SALDO = "Saldo";

	private IField m_fieldSaldo;
	private IField m_fieldPerson;
	
	/**
	 * Liste der Personen für die aktuelle Auswertung
	 */
	private CoPerson m_coPerson;

	private GregorianCalendar m_gregDatum;
	
	
	/**
	 * Konstruktor
	 * 
	 * @param coAuswertung
	 * @param gregorianCalendar Datum des 1. Monats für die Auswertung
	 * @throws Exception
	 */
	public CoArbeitszeit(CoAuswertung coAuswertung, GregorianCalendar gregorianCalendar) throws Exception {
		super();
		
		// alle Personen für die Auswertung laden
		m_coPerson = new CoPerson();
		m_coPerson.load(coAuswertung.getWherePerson().replace("PersonID", "ID"));
		
		m_gregDatum = gregorianCalendar;
		
		createCo();
	}
	
	
	/**
	 * Cacheobject erstellen.<br>
	 * Dazu wird der Rahmen aus den Spalten für die 6 Monate erstellt und anschließend das CO mit den Stundenwerten gefüllt.
	 * 
	 * @throws Exception
	 */
	public void createCo() throws Exception{
		
		setResID(TABLENAME);

		begin();
		
		// Felder für Zeitraum erstellen
		addFields();

		// Zeilen für alle Personen erstellen und mit Daten füllen
		addRows();

		commit();
	}


	/**
	 * Alle Felder für die Anzeige erzeugen
	 * 
	 * @throws Exception
	 */
	private void addFields() throws Exception {
		
		// Feld Person
		addField("field.tblkontowert.personid");
		m_fieldPerson = getField(getColumnCount() - 1);
		
		// Summe und Einzelwerte der Monate
		addFieldSaldo();
		addFieldsEinzelwerte();
	}


	/**
	 * Feld zur Anzeige der Summe der Differenzen -> aktueller Wert
	 */
	private void addFieldSaldo() {
		String resid, columnName, columnLabel;

		resid = RESID_SALDO;
		columnName = BESCHRIFTUNG_SALDO;
		columnLabel = columnName;

		addField(resid, columnName, columnLabel);
		setZeitFormat(resid);
		m_fieldSaldo = getField(getColumnCount() - 1);
	}


	/**
	 * Felder für die 6 Monate hinzufügen
	 */
	private void addFieldsEinzelwerte() {
		int iMonat;
		String monat;
		
		
		// Felder für 6 Monate nach dem Startdatum 
		for (iMonat=0; iMonat<ANZAHL_MONATE; ++iMonat)
		{
			// Monat aus Abkürzung und der verkürzten Jahreszahl zusammenbauen
			monat = Format.getMonatAbkuerzung(m_gregDatum) + " " + (m_gregDatum.get(Calendar.YEAR)-2000);
			
			addFieldWerktage(monat);
			addFieldMax(monat);
			addFieldIst(monat);
			addFieldDifferenz(monat);
			
			// nächster Monat
			m_gregDatum.add(Calendar.MONTH, 1);
		}

		// Datum wieder zurücksetzen
		m_gregDatum.add(Calendar.MONTH, -ANZAHL_MONATE);
	}


	private void addFieldWerktage(String monat) {
		addFieldEinzelwert(RESID_WERKTAGE, BESCHRIFTUNG_WERKTAGE, monat, false);
	}


	private void addFieldMax(String monat) {
		addFieldEinzelwert(RESID_MAX, BESCHRIFTUNG_MAX, monat, true);
	}


	private void addFieldIst(String monat) {
		addFieldEinzelwert(RESID_IST, BESCHRIFTUNG_IST, monat, true);
	}


	private void addFieldDifferenz(String monat) {
		addFieldEinzelwert(RESID_DIFFERENZ, BESCHRIFTUNG_DIFFERENZ, monat, true);
	}


	/**
	 * Feld für den übergebenen Monat hinnzufügen
	 * 
	 * @param resID
	 * @param beschriftung
	 * @param monat
	 * @param zeitFormat ZeitFormat oder Integer
	 */
	private void addFieldEinzelwert(String resID, String beschriftung, String monat, boolean zeitFormat) {
		String columnName, columnLabel;

		resID = resID + monat.replace(" ", "_");
		columnName = beschriftung + " " + monat;
		columnLabel = columnName;

		// alle Spalten für diese CO sind im Zeitformat 0:00 oder Integer für Anzahl Tage
		if (zeitFormat)
		{
			addField(resID, columnName, columnLabel);
			setZeitFormat(resID);
		}
		else
		{
			addFieldInteger(resID, columnName, columnLabel);
		}
	}

	long a;

	/**
	 * Für jede Person eine Zeile hinzufügen und Daten laden
	 * 
	 * @throws Exception
	 */
	private void addRows() throws Exception {
		int personID, iMonat, iField, anzWerktage, arbeitszeit, maxZeit, differenz, saldo;
		CoKontowert coKontowert;
		
		long tmp;
		a=0;
		
		if (!m_coPerson.moveFirst())
		{
			return;
		}

		do
		{
			// neue Zeile für die Person
			add();
			personID = m_coPerson.getID();
			m_fieldPerson.setValue(personID);


			// Felder für 6 Monate nach dem Startdatum 
			iField = 2;
			saldo = 0;
			for (iMonat=0; iMonat<ANZAHL_MONATE; ++iMonat)
			{
				// Daten für den Monat laden und Kennzahlen berechnen
				coKontowert = new CoKontowert();
				tmp = System.currentTimeMillis();
				coKontowert.loadMonat(personID, m_gregDatum.getTime(), false);
				a += System.currentTimeMillis()-tmp;
				coKontowert.berechneWerktageArbeitszeit();
				anzWerktage = coKontowert.getAnzWerktageBerechnet();
				arbeitszeit = coKontowert.getSummeArbeitszeitBerechnet();

				// Anzahl Werktage
				getField(iField++).setValue(anzWerktage);

				// Max-Zeit
				maxZeit = anzWerktage * 8 * 60; // 8 Stunden pro Werktag
				getField(iField++).setValue(maxZeit); 

				// Ist-Zeit
				getField(iField++).setValue(arbeitszeit);

				// Differenz
				differenz = arbeitszeit - maxZeit;
				getField(iField++).setValue(differenz);
				saldo += differenz; 

				// nächster Monat
				m_gregDatum.add(Calendar.MONTH, 1);
			}

			// Saldo eintragen
			getFieldSaldo().setValue(saldo);

			// Datum wieder zurücksetzen
			m_gregDatum.add(Calendar.MONTH, -ANZAHL_MONATE);

		} while (m_coPerson.moveNext());
		System.out.println("person load: " + a/1000.);
	}


	@Override
	public IField getFieldPersonID() {
		return m_fieldPerson;
	}


	/**
	 * Field mit Saldo
	 * @return 
	 */
	public IField getFieldSaldo() {
		return m_fieldSaldo;
	}

}
