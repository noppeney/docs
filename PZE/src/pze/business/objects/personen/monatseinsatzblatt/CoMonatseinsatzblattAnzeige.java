package pze.business.objects.personen.monatseinsatzblatt;

import java.util.Date;

import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.business.objects.reftables.CoStatusStundenwertMonatseinsatzblatt;
import pze.ui.formulare.person.monatseinsatzblatt.TableMonatseinsatzblatt;


/**
 * Cacheobject mit den Daten der Tabelle Monatseinsatzblatt, die in der Oberfläche angezeigt wird.
 * 
 * @author Lisiecki
 */
public class CoMonatseinsatzblattAnzeige extends AbstractCacheObject {
	
	private static final int INTERVALL_STUNDENWERT = 30;
	
	public static final String EINTRAG_FA = "FA";
	public static final String EINTRAG_KRANK_OHNE_LFZ = "ohne Lfz.";
	public static final String EINTRAG_FREIGESTELLT = "freigestellt";

	private static final String RESID_TAG = "field." + "tag";
	private static final String RESID_ARBEITSZEIT = "field." + "arbeitszeit";
	private static final String RESID_SUMME = "field." + "summe";
	private static final String RESID_KRANK = "field." + "krank";
	private static final String RESID_URLAUB = "field." + "urlaub";

	private IField m_fieldArbeitszeit;
	private IField m_fieldSumme;
	private IField m_fieldKrank;
	private IField m_fieldUrlaub;
	
	private VirtCoProjekt m_virtCoProjekt;
	private CoMonatseinsatzblatt m_coMonatseinsatzblatt;
	
	private CoKontowert m_coKontowert;

	private int m_personID;
	private Date m_datum;
	
	/**
	 * Anzahl Zeilen mit Projektdaten
	 */
	private int m_anzProjektZeilen;
	
	/**
	 * Index der ersten Projektspalte
	 */
	private int m_startindexProjektspalten;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public CoMonatseinsatzblattAnzeige(int personID, Date datum) throws Exception {
		super();
		
		m_personID = personID;
		m_datum = datum;
		
		m_virtCoProjekt = new VirtCoProjekt();
		m_virtCoProjekt.load(personID, datum);

		m_coMonatseinsatzblatt = new CoMonatseinsatzblatt();
		m_coMonatseinsatzblatt.load(personID, datum);
		
		m_coKontowert = new CoKontowert();
		m_coKontowert.loadMonat(personID, datum, true);
		
		createCo();
	}
	
	
	/**
	 * Cacheobject erstellen.<br>
	 * Dazu wird der Rahmen aus den Projekten und den Tagen des Monats erstellt und anschließend das CO mit den Stundenwerten gefüllt.
	 * 
	 * @throws Exception
	 */
	public void createCo() throws Exception{
		
		setResID(m_coMonatseinsatzblatt.getResID());

		begin();
		
		// Felder für Projekte erstellen
		addFields();
		
		// Zeilen für Projektdaten und Tage des Monats erstellen
		addRows();
		
		// Stundenwerte eintragen
		setData();

		// Monatssummen berechnen
		updateSummeMonat(m_fieldArbeitszeit);
		updateSummeMonat(m_fieldUrlaub);
		updateSummeMonat(m_fieldKrank);
		updateSummeMonat(m_fieldSumme); // Summe zum Schluss, weil Krank und Arbeitszeit berücksichtogt werden

		commit();
	}


	/**
	 * Alle Felder für die Anzeige des Monatseinsatzblattes erzeugen (Spalten mit Tagen des Monats und Projekten)
	 * 
	 * @throws Exception
	 */
	private void addFields() throws Exception {
		addFieldTagDesMonats();
		
		addFieldArbeitszeit();

		addFieldsProjekte();
		
		addFieldSumme();
		addFieldKrank();
		addFieldUrlaub();
	}


	/**
	 * Feld zur Anzeige des Tages hinzufügen
	 */
	private void addFieldTagDesMonats() {
		String resid, columnName, columnLabel;

		resid = RESID_TAG;
		columnName = "Tag";
		columnLabel = columnName;

		addField(resid, columnName, columnLabel);
	}


	/**
	 * Felder für die Projekte des Stundenzettels hinzufügen
	 */
	private void addFieldsProjekte() {
		int iProjekt;
		String resid, columnName, columnLabel;
		
		// Startindex der Projektspalten
		m_startindexProjektspalten = getFieldCount();
		
		// prüfen, ob Projekte vorhanden sind
		if (!m_virtCoProjekt.moveFirst())
		{
			return;
		}
		
		iProjekt = 0;
		do
		{
			++iProjekt;
			resid = "field.projekt." + iProjekt + "";

			columnName = "projekt" + iProjekt;
			columnLabel = columnName;

			addField(resid, columnName, columnLabel);
		} while (m_virtCoProjekt.moveNext());
		
	}


	/**
	 * Feld zur Anzeige der Summe der eingetragenen Stunden hinzufügen
	 */
	private void addFieldArbeitszeit() {
		String resid, columnName, columnLabel;

		resid = RESID_ARBEITSZEIT;
		columnName = "Arbeitszeit";
		columnLabel = columnName;

		addField(resid, columnName, columnLabel);
		m_fieldArbeitszeit = getField(getColumnCount() - 1);
	}


	/**
	 * Feld zur Anzeige der Summe der eingetragenen Stunden hinzufügen
	 */
	private void addFieldSumme() {
		String resid, columnName, columnLabel;

		resid = RESID_SUMME;
		columnName = "Summe";
		columnLabel = columnName;

		addField(resid, columnName, columnLabel);
		m_fieldSumme = getField(getColumnCount() - 1);
	}


	/**
	 * Feld zur Anzeige der Krankheitstage hinzufügen
	 */
	private void addFieldKrank() {
		String resid, columnName, columnLabel;

		resid = RESID_KRANK;
		columnName = "Krank";
		columnLabel = columnName;

		addField(resid, columnName, columnLabel);
		m_fieldKrank = getField(getColumnCount() - 1);
	}


	/**
	 * Feld zur Anzeige der Urlaubstage hinzufügen
	 */
	private void addFieldUrlaub() {
		String resid, columnName, columnLabel;

		resid = RESID_URLAUB;
		columnName = "Urlaub/FA";
		columnLabel = columnName;

		addField(resid, columnName, columnLabel);
		m_fieldUrlaub = getField(getColumnCount() - 1);
	}


	/**
	 * Alle Zeilen zur Anzeige des Monatseinsatzblattes hinzufügen (Projektdaten und Tage des Monats)
	 * 
	 * @throws Exception
	 */
	private void addRows() throws Exception {
		addRowsProjektdaten();
		
		// Beschriftung der Spalten für Summe, Krank, Urlaub...
		addBeschriftungSonderspalten();
		
		addRowsTageDesMonats();
		
		addRowSumme();
	}


	/**
	 * Zeilen mit den Projektdaten hinzufügen und füllen
	 * 
	 * @throws Exception
	 */
	private void addRowsProjektdaten() throws Exception {

		addRowProjektdaten(m_virtCoProjekt.getFieldKundeKuerzelID());
		addRowProjektdaten(m_virtCoProjekt.getFieldAuftragsNr());
		addRowProjektdaten(m_virtCoProjekt.getFieldAbrufNr());
		addRowProjektdaten(m_virtCoProjekt.getFieldKostenstelleID());
		addRowProjektdaten(m_virtCoProjekt.getFieldBerichtsNrID());
		addRowProjektdaten(m_virtCoProjekt.getFieldProjektleiterID());
		addRowProjektdaten(m_virtCoProjekt.getFieldBemerkung());
		addRowProjektdaten(m_virtCoProjekt.getFieldStundenartID());
		
		m_anzProjektZeilen = getRowCount();
	}


	/**
	 * Zeile mit den Projektdaten (aller Projekte) hinzufügen und füllen
	 * 
	 * @param field Feld mit den Projektdaten, die in der Zeile angezeigt werden sollen
	 * @throws Exception
	 */
	private void addRowProjektdaten(IField field) throws Exception {
		int iField;
		
		add();
		
		// Beschriftung
		iField = 0;
		getField(iField).setValue(field.getFieldDescription().getCaption());
		
		// Daten der Projekte
		if (!m_virtCoProjekt.moveFirst())
		{
			return;
		}
		
		iField = m_startindexProjektspalten;
		do
		{
			getField(iField).setValue(field.getDisplayValue());
			++iField;
		} while (m_virtCoProjekt.moveNext());
	}


	/**
	 * Beschriftung der Spalten für Summe, Krank, Urlaub...
	 */
	private void addBeschriftungSonderspalten() {
		int iField, anzFields;
		IField field;
		
		// vor den Projektspalten
		anzFields = m_startindexProjektspalten;
		for (iField=1; iField<anzFields; ++iField)
		{
			field = getField(iField);
			field.setValue(field.getFieldDescription().getCaption());
		}
		
		// nach den Projektspalten
		anzFields = getColumnCount();
		for (iField=m_virtCoProjekt.getRowCount() + m_startindexProjektspalten; iField<anzFields; ++iField)
		{
			field = getField(iField);
			field.setValue(field.getFieldDescription().getCaption());
		}
	}


	/**
	 * Für jeden Tag des Monats eine Zeile erstellen und ggf. Sonderspalten füllen
	 * 
	 * @throws Exception
	 */
	private void addRowsTageDesMonats() throws Exception {
		int iTag, anzTage;

		anzTage = Format.getAnzTageDesMonats(m_datum);
		for (iTag=1; iTag<=anzTage; ++iTag)
		{
			add();
			setTagDesMonats(iTag);

			if (m_coKontowert.hasRows())
			{
				checkArbeitszeit();
				checkKrank();
				checkUrlaub();
			}
		}
	}


	/**
	 * Für jeden Tag des Monats eine Zeile erstellen und ggf. Sonderspalten füllen
	 * 
	 * @throws Exception
	 */
	@Override
	protected void addRowSumme() throws Exception {
		add();
		getFieldTagDesMonats().setValue("Summe");
	}


	/**
	 * Field mit Tag des Monat ist die 1. Spalte
	 * @return 
	 */
	public IField getFieldTagDesMonats() {
		return getField(0);
	}


	/**
	 * Field mit Arbeitszeit
	 * @return 
	 */
	public IField getFieldArbeitszeit() {
		return m_fieldArbeitszeit;
	}


	/**
	 * Field mit Summe Projektstunden 
	 * @return 
	 */
	public IField getFieldSumme() {
		return m_fieldSumme;
	}


	/**
	 * Nr. des Tages im Monat in die 1. Spalte eintragen
	 * 
	 * @param iTag
	 */
	private void setTagDesMonats(int iTag) {
		getFieldTagDesMonats().setValue(iTag);
	}


	/**
	 * Nr. des Tages im Monat oder 0
	 * 
	 * @return 
	 */
	public int getTagDesMonats() {
		return Format.getIntValue(getFieldTagDesMonats().getValue());
	}


	/**
	 * Aktuelles Datum (wenn der Tag des Monats == 0 (keine Datumszeile) wird der 1. bzw. irgendein Tag des Monats zurückgegeben)
	 * 
	 * @param tag
	 * @return
	 */
	public Date getDatum() {
		return Format.getDatum(m_datum, getTagDesMonats());
	}


	/**
	 * Arbeitszeit eintragen
	 */
	private void checkArbeitszeit() {
		// Fehler in der Uhrzeit 2 Uhr statt 12 Uhr, weshalb die Arbeitszeit nicht angezeigt wurde (09.04.2021)
		// Fehler nicht reproduzierbar, aber in der DB korrigiert
//		select * from tblKontowert where ID > 2000000 and datepart(hour, Datum)=2
//		update tblKontowert set Datum = '2021-03-31T12:00:00.000' where ID = 4214612
		String value;

		if (!m_coKontowert.moveToTag(getTagDesMonats()))
		{
			return;
		}
		
		if (m_coKontowert.getWertArbeitszeit() > 0)
		{
			value = Format.getZeitAsText(m_coKontowert.getWertArbeitszeit());
		}
		else
		{
			value = null;
		}
		
		m_fieldArbeitszeit.setValue(value);
	}


	/**
	 * Prüfen, ob die Person an dem Tag krank war
	 */
	private void checkKrank() {
		String value;
		
		if (!m_coKontowert.moveToTag(getTagDesMonats()))
		{
			return;
		}
		
		if (m_coKontowert.getAnzahlKrank() > 0)
		{
			value = Format.getZeitAsText(m_coKontowert.getWertArbeitszeit());
		}
		else if (m_coKontowert.getAnzahlKrankOhneLfz() > 0)
		{
			value = EINTRAG_KRANK_OHNE_LFZ;
		}
		else if (m_coKontowert.getWertKrank() > 0)
		{
			value = Format.getZeitAsText(m_coKontowert.getWertKrank());
		}
		else
		{
			value = null;
		}
		
		m_fieldKrank.setValue(value);
	}


	/**
	 * Prüfen, ob die Person an dem Tag Urlaub hatte
	 */
	private void checkUrlaub() {
		String value;
		
		if (!m_coKontowert.moveToTag(getTagDesMonats()))
		{
			return;
		}
		
		if (m_coKontowert.getAnzahlUrlaub() + m_coKontowert.getAnzahlSonderurlaub() > 0)
		{
			value = Format.getZeitAsText(m_coKontowert.getWertArbeitszeit());
		}
		else if (m_coKontowert.getAnzahlFa() > 0)
		{
			value = EINTRAG_FA;
		}
		else if (m_coKontowert.getAnzahlBezFreistellung() > 0)
		{
			value = EINTRAG_FREIGESTELLT;
		}
		else
		{
			value = null;
		}
		
		m_fieldUrlaub.setValue(value);
	}


	/**
	 * Daten des Basis-CO´s transponieren
	 * 
	 * @throws Exception
	 */
	private void setData() throws Exception {
		int wertZeit;
		IField field;

		
		if (!m_coMonatseinsatzblatt.moveFirst())
		{
			return;
		}
		
		do
		{
			// Field des Projektes
			field = getField(m_coMonatseinsatzblatt.getAuftragID(), m_coMonatseinsatzblatt.getAbrufID(), 
					m_coMonatseinsatzblatt.getKostenstelleID(), m_coMonatseinsatzblatt.getBerichtsNrID(), m_coMonatseinsatzblatt.getStundenartID());
			
			// in die Zeile des Tages wechseln
			if (!moveToTag(m_coMonatseinsatzblatt.getTagDesMonats()))
			{
				throw new Exception();
			}
			
			// Stundenwert setzen
			wertZeit = m_coMonatseinsatzblatt.getWertZeit();
			if (wertZeit > 0)
			{
				field.setValue(Format.getZeitAsText(wertZeit));
				updateSummeTag();
				updateSummeMonat(field);
			}
			
		} while (m_coMonatseinsatzblatt.moveNext());
		
	}


	/**
	 * Field für das Projekt bestimmen
	 * 
	 * @param auftragID 
	 * @param abrufID 
	 * @param kostenstelleID 
	 * @param berichtsNrID 
	 * @param stundenartID 
	 * @return
	 * @throws Exception
	 */
	private IField getField(int auftragID, int abrufID, int kostenstelleID, int berichtsNrID, int stundenartID) throws Exception {
		int iProjekt;
		
		// Index des Projektes, um die Spalte/Field zu bestimmen
		iProjekt = m_virtCoProjekt.getRowIndex(auftragID, abrufID, kostenstelleID, berichtsNrID, stundenartID);
		
		return getField(iProjekt + m_startindexProjektspalten);
	}

	
	/**
	 * In der aktuellen Zeile zur übergebenen Zelle wechseln.<br>
	 * Bedeutet: In virtCoProjekt und coMonatseinsatzblatt entsprechend navigieren.
	 * 
	 * @param cell
	 * @return
	 * @throws Exception
	 */
	public boolean moveToCell(ISpreadCell cell) throws Exception{
		int tagDesMonats;
		int projektFieldIndex;
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		VirtCoProjekt virtCoProjekt;

		// prüfen, ob es ein Projektfield ist
		projektFieldIndex = getProjektFieldIndex(cell.getField());
		tagDesMonats = getTagDesMonats();
		
		if (projektFieldIndex < 0)
		{
			return false;
		}

		// Projektdaten
		virtCoProjekt = getVirtCoProjekt();
		virtCoProjekt.moveTo(projektFieldIndex);

		// Einsatzdaten
		if (tagDesMonats < 1)
		{
			return false;
		}

		coMonatseinsatzblatt = getCoMonatseinsatzblatt();
		if (!coMonatseinsatzblatt.moveTo(virtCoProjekt, tagDesMonats))
		{
			return false;
		}

		return true;
	}
	
	
	/**
	 * In die angegeebene Zeile zur übergebenen Zelle wechseln.<br>
	 * Bedeutet: In virtCoProjekt und coMonatseinsatzblatt entsprechend navigieren.
	 * 
	 * @param cell
	 * @param rowIndex
	 * @return
	 * @throws Exception
	 */
	public boolean moveToCell(ISpreadCell cell, int rowIndex) throws Exception{
		int tagDesMonats;
		int projektFieldIndex;
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		VirtCoProjekt virtCoProjekt;

		tagDesMonats = rowIndex - getAnzProjektzeilen();
		
		// prüfen, ob es ein Projektfield ist
		projektFieldIndex = getProjektFieldIndex(cell.getField());
		
		if (projektFieldIndex < 0)
		{
			return false;
		}

		// Projektdaten
		virtCoProjekt = getVirtCoProjekt();
		virtCoProjekt.moveTo(projektFieldIndex);

		// Einsatzdaten
		if (tagDesMonats < 1)
		{
			return false;
		}

		coMonatseinsatzblatt = getCoMonatseinsatzblatt();
		if (!coMonatseinsatzblatt.moveTo(virtCoProjekt, tagDesMonats))
		{
			return false;
		}

		return true;
	}
	

	/**
	 * Zu der Zeile mit dem Tag wechseln
	 * 
	 * @param tag
	 * @return
	 */
	public boolean moveToTag(int tag) {
		return moveTo(tag, RESID_TAG);
	}


	/**
	 * Zu der Zeile mit der Summe wechseln
	 * 
	 * @return
	 */
	public boolean moveToSummenzeile() {
		return moveToTag(Format.getAnzTageDesMonats(getDatum())) && moveNext();
	}


	/**
	 * Feldwert aktualisieren und daraus resultierende Berechnungen durchführen
	 * 
	 * @param selectedField
	 * @throws Exception
	 */
	public void update(IField selectedField) throws Exception {
		Integer zeit, differenz;
		String zeitString;
		
		if (selectedField.getState() != IBusinessObject.statusChanged)
		{
			return;
		}

		// Zeit bestimmen
		zeitString = selectedField.getDisplayValue();
		zeit = Format.getZeitAsInt(zeitString);

		// ungültige Eingaben abfangen
		if (zeit > 0)
		{
			// auf 30 Minuten runden
			differenz = zeit % INTERVALL_STUNDENWERT;
			if (differenz < INTERVALL_STUNDENWERT/2)
			{
				zeit -= differenz;
			}
			else
			{
				zeit += INTERVALL_STUNDENWERT - differenz;
			}
			
			// Wert in Stundenformat formatieren
			selectedField.setValue(Format.getZeitAsText(zeit));
		}
		else
		{
			zeit = null;
			selectedField.setValue(zeit);
		}

		// moveTo im Co ist nicht notwendig, weil dies schon über tableSelect gemacht wird 
//		m_virtCoProjekt.moveTo(getProjektFieldIndex(selectedField));
//		m_coMonatseinsatzblatt.moveTo(m_virtCoProjekt, getTagDesMonats());

		// Wert im richtigen CO eintragen
//		int projektFieldIndex = getProjektFieldIndex(selectedField);
//		int tagDesMonats = getTagDesMonats();
//		
//		System.out.println(m_coMonatseinsatzblatt.getDatum() + " -update " + tagDesMonats);
		m_coMonatseinsatzblatt.setWertZeit(zeit);
		
		// Speichern von wem die Änderungen gemacht wurden
		m_coMonatseinsatzblatt.setGeaendertVonID(UserInformation.getPersonID());
		m_coMonatseinsatzblatt.setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
		
		// Status anpassen: OK = neuer Eintrag oder er war bisher leer
		if (m_coMonatseinsatzblatt.isNew() 
				|| ( m_coMonatseinsatzblatt.getStatusID() == CoStatusStundenwertMonatseinsatzblatt.STATUSID_OK) 
					&& Format.getIntValue(m_coMonatseinsatzblatt.getFieldWertZeit().getOriginalValue()) == 0 )
		{
			m_coMonatseinsatzblatt.setStatusID(CoStatusStundenwertMonatseinsatzblatt.STATUSID_OK);
		}
		else
		{
			m_coMonatseinsatzblatt.setStatusID(CoStatusStundenwertMonatseinsatzblatt.STATUSID_GEAENDERT);
		}

		// Summe der eingetragenen Stunden für den Tag neu berechnen
		updateSummeTag();
		updateSummeMonat(selectedField);
	}


	/**
	 * Index (ab 0) der aktuellen Spalte in den Projektspalten.<br>
	 * Entspricht der Spalte in m_virtCoProjekt
	 * 
	 * @param field Table-Field (kein Cell-Field)
	 * @return Index oder -1, wenn es keine Projektspalte ist
	 */
	public int getProjektFieldIndex(IField field) {
		int iField;
		int anzFields;
		
		anzFields = getAnzProjektspalten();
		for (iField=0; iField<anzFields; ++iField)
		{
			if (getField(iField + m_startindexProjektspalten).equals(field))
			{
				return iField;
			}
		}
		
		return -1;
	}


	/**
	 * Index (ab 0) der Spalte mit der resID in den Projektspalten<br>
	 * Entspricht der Spalte in m_virtCoProjekt
	 * 
	 * @param resID
	 * @return Index oder -1, wenn es keine Projektspalte ist
	 */
	public int getProjektFieldIndex(String resID) {
		int iField;
		int anzFields;
		
		anzFields = getAnzProjektspalten();
		for (iField=0; iField<anzFields; ++iField)
		{
			if (getField(iField + m_startindexProjektspalten).getFieldDescription().getResID().equals(resID))
			{
				return iField;
			}
		}
		
		return -1;
	}


	/**
	 * Summe in der aktuellen Zeile berechnen
	 */
	private void updateSummeTag() {
		int iField, iStartfield, anzFields, summe;
		
		// bei Feld 1 beginnen zu zählen, wegen Tages-Spalte
		iStartfield = m_startindexProjektspalten;
		
		summe = 0;
		anzFields = m_virtCoProjekt.getRowCount();
		for (iField=iStartfield; iField<anzFields+iStartfield; ++iField)
		{
			summe += Format.getZeitAsInt(Format.getStringValue(getField(iField).getValue()));
		}
		
		m_fieldSumme.setValue(Format.getZeitAsText(summe));

		// Monatssumme der Summenspalte anpassen
		updateSummeMonat(m_fieldSumme);
	}


	/**
	 * Summe in der aktuellen Spalte berechnen
	 */
	private void updateSummeMonat(IField selectedField) {
		int summe;
		Object bookmark;
		
		// bookmark merken
		bookmark = getBookmark();

		moveTo(m_anzProjektZeilen);
		
		summe = 0;
		do
		{
			summe += Format.getZeitAsInt(Format.getStringValue(selectedField.getValue()));

		} while (moveNext() && getTagDesMonats() > 0);
		
		// Summe setzen
		selectedField.setValue(Format.getZeitAsText(summe));
		
		// bei der Monatssumme auch Krank- und Urlaubsstunden berücksichtigen
		if (selectedField.equals(m_fieldSumme))
		{
			selectedField.setValue(Format.getZeitAsText(summe) + "/" + Format.getZeitAsText(getSummeInklKrankUrlaub()));
		}
		
		// bookmark wieder setzen
		moveTo(bookmark);
	}

	
	/**
	 * Summe der eingetragenen Stunden prüfen (dazu muss der Bookmark in der Summenzeile liegen)
	 * 
	 * @return
	 */
	public boolean checkSumme(){
		int summe, differenz;
		
		summe = getSummeInklKrankUrlaub();
		differenz = summe - Format.getZeitAsInt(Format.getStringValue(m_fieldArbeitszeit.getValue()));

		return differenz >= TableMonatseinsatzblatt.TOLERANZ_MONAT_NEGATIV && differenz < TableMonatseinsatzblatt.TOLERANZ_MONAT_POSITIV;
	}


	/**
	 * Summe aus Summe, Krank und Urlaub in der Zeile berechnen
	 * 
	 * @return
	 */
	public int getSummeInklKrankUrlaub() {
		int summe;
		summe = getSummeStunden();
		summe += Format.getZeitAsInt(Format.getStringValue(m_fieldUrlaub.getValue()));
		summe += Format.getZeitAsInt(Format.getStringValue(m_fieldKrank.getValue()));
		return summe;
	}
	

	public int getArbeitszeit(){
		return Format.getZeitAsInt(m_fieldArbeitszeit.getDisplayValue());
	}
	

	/**
	 * "/" rausfiltern, bei Monatssumme
	 * 
	 * @return
	 */
	public int getSummeStunden(){
		String stringValue;
		
		stringValue = m_fieldSumme.getDisplayValue();
		if (stringValue.contains("/"))
		{
			stringValue = stringValue.substring(0, stringValue.indexOf("/"));
		}
			
		return Format.getZeitAsInt(stringValue);
	}
	

	/**
	 * Wert des Feldes als Integer
	 * 
	 * @return
	 */
	public int getWertKrank(){
		return Format.getZeitAsInt(m_fieldKrank.getDisplayValue());
	}
	

	/**
	 * Eintrag des Feldes als String
	 * 
	 * @return
	 */
	public String getDisplayValueKrank(){
		return m_fieldKrank.getDisplayValue();
	}
	

	/**
	 * Wert des Feldes als Integer
	 * 
	 * @return
	 */
	public int getWertUrlaub(){
		return Format.getZeitAsInt(m_fieldUrlaub.getDisplayValue());
	}
	

	/**
	 * Eintrag des Feldes als String
	 * 
	 * @return
	 */
	public String getDisplayValueUrlaub(){
		return m_fieldUrlaub.getDisplayValue();
	}
	
	
	public int getPersonID() {
		return m_personID;
	}


	public int getStartindexProjektspalten(){
		return m_startindexProjektspalten;
	}

	
	public int getAnzProjektzeilen(){
		return m_anzProjektZeilen;
	}

	
	public int getAnzProjektspalten(){
		return m_virtCoProjekt.getRowCount();
	}

	
	public CoMonatseinsatzblatt getCoMonatseinsatzblatt(){
		return m_coMonatseinsatzblatt;
	}

	
	public VirtCoProjekt getVirtCoProjekt(){
		return m_virtCoProjekt;
	}
	
	
}
