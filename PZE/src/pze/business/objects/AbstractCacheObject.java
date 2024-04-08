package pze.business.objects;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;

import framework.Application;
import framework.business.cacheobject.CacheObject;
import framework.business.fields.FieldDescription;
import framework.business.interfaces.FieldType;
import framework.business.interfaces.PersistanceType;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.data.IRow;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.fields.IFieldDescription;
import framework.business.interfaces.loader.ILoaderBase;
import framework.database.cacheobject.LoaderBase;
import pze.business.Format;
import pze.business.UserInformation;


/**
 * allgemeines Cacheobject für das PZE-Projekt
 * 
 * @author Lisiecki
 */
public abstract class AbstractCacheObject extends CacheObject {

	
	/**
	 * Konstruktor
	 */
	public AbstractCacheObject() {
	}

	
	/**
	 * Konstruktor
	 * @param tableResID	RESID der Tabelle
	 */
	public AbstractCacheObject(String tableResID) {
		super(tableResID);
	}

	
	/**
	 * Alle Datensätze laden
	 * @throws Exception aus Loaderbase
	 */
	public void loadAll() throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "", getSortFieldName());
	}
	
	
//	/**
//	 * Alle nicht-gelöschten Daten laden
//	 * 
//	 * @throws Exception
//	 */
//	public void loadAllNotDeleted() throws Exception {
//		emptyCache();
//		Application.getLoaderBase().load(this, "statusID=" + PZEStartupAdapter.STATUS_NICHT_GELOESCHT, getSortFieldName());
//	}

	
	/**
	 * Über Primärschlüssel laden
	 * @param id	Primärschlüssel
	 * @throws Exception 
	 */
	public void loadByID(int id) throws Exception {
		// voreingestellt ist das erste Feld der Primärschlüssel, wenn nicht, Methode überschreiben!
		emptyCache();
		Application.getLoaderBase().load(this, getField(0).getFieldDescription().getName() + "=" + id,"");
	}
	
	
	/**
	 * alle Daten für die Person laden
	 * 
	 * @param personID
	 * @throws Exception
	 */
	public void loadByPersonID(int personID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, " PersonID= " + personID, getSortFieldName());
	}


	/**
	 * Feld hinzufügen
	 * 
	 * @param resid
	 * @param columnName
	 * @param columnLabel
	 */
	protected void addField(String resid, String columnName, String columnLabel) {
		FieldDescription fieldDescription;
		
		fieldDescription = new FieldDescription(resid);
		fieldDescription.setTableName(getTableName());
		fieldDescription.setName(columnName);
		fieldDescription.setCaption(columnLabel);
		fieldDescription.setFieldType(FieldType.TEXT);
		
		addField(fieldDescription);
	}


	/**
	 * Feld hinzufügen
	 * 
	 * @param resid
	 * @param columnName
	 * @param columnLabel
	 * @param enabled
	 */
	protected void addField(String resid, String columnName, String columnLabel, boolean enabled) {
		FieldDescription fieldDescription;
		
		fieldDescription = new FieldDescription(resid);
		fieldDescription.setTableName(getTableName());
		fieldDescription.setName(columnName);
		fieldDescription.setCaption(columnLabel);
		fieldDescription.setFieldType(FieldType.TEXT);
		fieldDescription.setEnabled(enabled);
		
		addField(fieldDescription);
	}


	/**
	 * Feld hinzufügen
	 * 
	 * @param resid
	 * @param columnName
	 * @param columnLabel
	 */
	protected void addFieldInteger(String resid, String columnName, String columnLabel) {
		FieldDescription fieldDescription;
		
		fieldDescription = new FieldDescription(resid);
		fieldDescription.setTableName(getTableName());
		fieldDescription.setName(columnName);
		fieldDescription.setCaption(columnLabel);
		fieldDescription.setFieldType(FieldType.INTEGER);
		fieldDescription.setDataFormat("0");

		addField(fieldDescription);
	}


	/**
	 * Format des Feldes auf das Zeitformat 0:00 ändern
	 * 
	 * @param resid
	 * @param columnName
	 * @param columnLabel
	 * @param enabled
	 */
	protected void setZeitFormat(String resID) {
		IField field;
		FieldDescription fieldDescription;

		field = getField(resID);
		fieldDescription = (FieldDescription) field.getFieldDescription();

		fieldDescription.setDataFormat("0:00");
	}


	/**
	 * Dieses Cacheobject mit Daten aus dem übergebenen CO füllen.<br>
	 * Die Fields des aktuellen Cacheobjektes werden dabei in dem übergebenen gesucht.
	 * 
	 * @param coVerletzerliste
	 * @throws Exception
	 */
	protected void addValues(AbstractCacheObject co) throws Exception {
		int iField;
		int anzFields;
		IField field;
		begin();
		
		if (!co.moveFirst())
		{
			return;
		}
		
		// co durchlaufen und Daten übertragen
		anzFields = getColumnCount();
		do
		{
			add();
			getCurrentRow().setRowState(IBusinessObject.statusUnchanged);
			for (iField=0; iField<anzFields; ++iField)
			{
				field = getField(iField);
				field.setValue(co.getField(field.getFieldDescription().getResID()).getValue());
			}
		} while (co.moveNext());
		
	}


	/**
	 * Gibt ein Cacheobjekt mit den Dokumenten des aktuellen Cacheobjektes zurück.
	 * Dokumente können für verschiedene Cacheobjekte angelegt, daher die Funktion in dieser Klasse.
	 * 
	 * @return Dokumente zu Objekt
	 * @throws Exception
	 */
	public CoDokumente getDokumente() throws Exception {
		CoDokumente coDokumente = new CoDokumente();
		
		// in tblDokumente wird allgemein ein FK gespeichert, es gibt aber kein constraint zu einer bestimmten Tabelle
		coDokumente.loadByObjekt(getID());
		coDokumente.setKey("dokumente");
		
//		addChild(coDokumente);

		return coDokumente;
	}
	
	
	/**
	 * Cacheobjekt mit Logging-Daten
	 * 
	 * @return CoLogging
	 * @throws Exception 
	 */
	public CoLogging getLogging() throws Exception {
		CoLogging coLogging;
		
		// Logging laden
		coLogging = new CoLogging();		
		
		return coLogging;
	}
	
	
	/**
	 * PK ist voreingestellt das erste Feld...
	 * @return Primärschlüssel
	 */
	public int getID() {
		return getFieldID().getIntValue();
	}
	
	
	/**
	 * ID der übergebenen Bezeichnung
	 * 
	 * @return
	 */
	public int getID(String bezeichnung) {
		moveToBezeichnung(bezeichnung);
		return getID();
	}


	/**
	 * PK ist voreingestellt das erste Feld...
	 * @param id Primärschlüssel
	 */
	public void setID(int id) {
		getFieldID().setValue(id);		
	}


	/**
	 * @return PK-Field ist voreingestellt das erste Feld...
	 */
	public IField getFieldID() {
		return getField(0);
	}
	
	
	/**
	 * Bezeichnung ist voreingestellt das zweite Feld...
	 * 
	 * @return Bezeichnung
	 */
	public String getBezeichnung() {
		return getFieldBezeichnung().getDisplayValue();
	}

	
	/**
	 * Bezeichnung der übergebenen ID
	 * 
	 * @return Bezeichnung oder null
	 */
	public String getBezeichnung(int id) {
		if (!moveToID(id))
		{
			return null;
		}
		
		return getBezeichnung();
	}


	/**
	 * Bezeichnung ist voreingestellt das zweite Feld...
	 * @param bezeichnung Primärschlüssel
	 */
	public void setBezeichnung(String bezeichnung) {
		getFieldBezeichnung().setValue(bezeichnung);
	}
	

	/**
	 * @return Bezeichnung-Field, voreingestellt das zweite Feld...
	 */
	public IField getFieldBezeichnung() {
		return getField(1);
	}
	

	/**
	 * @return Bezeichnung des Sortierungsfeldes, voreingestellt, zweites Feld...
	 */
	protected String getSortFieldName() {
		return getFieldBezeichnung().getFieldDescription().getName();
	}
	

	public IField getFieldPersonID() {
		return getField("field." + getTableName() + ".personid");
	}


	public int getPersonID() {
		IField field;
		
		field = getFieldPersonID();
		
		if (field == null)
		{
			return 0;
		}
		
		return Format.getIntValue(field.getValue());
	}


	public String getPerson() {
		return Format.getStringValue(getFieldPersonID().getDisplayValue());
	}


	public void setPersonID(int personID) {
		getFieldPersonID().setValue(personID);
	}


	public IField getFieldDatum() {
		return getField("field." + getTableName() + ".datum");
	}


	public Date getDatum() {
		return Format.getDateValue(getFieldDatum());
	}


	public GregorianCalendar getGregDatum() {
		return Format.getGregorianCalendar(getDatum());
	}


	public int getJahr() {
		return getGregDatum().get(GregorianCalendar.YEAR);
	}


	public void setDatum(Date datum) {
		getFieldDatum().setValue(datum);
	}


	public IField getFieldStatusID() {
		return getField("field." + getTableName() + ".statusid");
	}


	/**
	 * StatusID, nicht in jeder Tabelle vorhanden
	 * 
	 * @return StatusID oder 0
	 */
	public int getStatusID() {
		return Format.getIntValue(getFieldStatusID().getValue());
	}


	public IField getFieldGeaendertVon() {
		return getField("field." + getTableName() + ".geaendertvonid");
	}


	public int getGeaendertVonID() {
		return Format.getIntValue(getFieldGeaendertVon().getValue());
	}


	public void setGeaendertVonID(int personID) {
		if (personID > 0)
		{
			getFieldGeaendertVon().setValue(personID);
		}
	}


	public IField getFieldGeaendertAm() {
		return getField("field." + getTableName() + ".geaendertam");
	}


	public Date getGeaendertAm() {
		return Format.getDateValue(getFieldGeaendertAm().getValue());
	}


	/**
	 * Funktioniert nur mit String, da das Dataformat auf String gesetzt ist
	 * 
	 * @param datum Datum mit Uhrzeit
	 */
	public void setGeaendertAm(String datum) {
		getFieldGeaendertAm().setValue(datum);
	}


	public void setGeaendertAm(Date datum) {
//		setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(datum)));
		getFieldGeaendertAm().setValue(datum);
	}


	public IField getFieldGrundAenderungID() {
		return getField("field." + getTableName() + ".grundaenderungid");
	}


	public int getGrundAenderungID() {
		return Format.getIntValue(getFieldGrundAenderungID());
	}


	public void setGrundAenderungID(int grundAenderungID) {
		getFieldGrundAenderungID().setValue(grundAenderungID);
	}


	/**
	 * StatusID, nicht in jeder Tabelle vorhanden
	 * 
	 * @param statusID
	 */
	public void setStatusID(int statusID) {
		IField field;
		
		field = getFieldStatusID();
		
		if (field != null)
		{
			field.setValue(statusID);
		}
	}


	public IField getFieldBemerkung() {
		return getField("field." + getTableName() + ".bemerkung");
	}


	public String getBemerkung() {
		return Format.getStringValue(getFieldBemerkung().getValue());
	}


	public void setBemerkung(String bemerkung) {
		getFieldBemerkung().setValue(bemerkung);
	}


	/**
	 * Änderung dokumentieren
	 * 
	 * @throws Exception
	 */
	public void updateGeaendertVonAm() throws Exception {
		setGeaendertVonID(UserInformation.getPersonID());
		setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
	}


	/**
	 * Neuen Datensatz mit neuem Primärschlüssel erzeugen
	 * 
	 * @return neue ID
	 * @throws Exception
	 */
	public int createNew() throws Exception	{
		if(!isEditing())
		{
			begin();
		}
		
		int id = nextID();
		
		add();
		setID(id);
		return id;
	}


	/**
	 * neue ID generieren
	 * 
	 * @return neue ID
	 * @throws Exception
	 */
	public int nextID() throws Exception	{		
		int nk = 0;
		int ar = 0;
		String name = "SEQ_GEN";
		
		// Schleife wegen optimistischer Konkurrenz. Ich versuche solange defensiv meinen neuen
		// Key auszurechnen und zurückzuschreiben, bis nachweislich keiner dazwischen gegangen ist.
		do
		{
			ILoaderBase loaderbase = Application.getLoaderBase();
			String sql = "SELECT seq_count from sequence where seq_name='"+name+"'";
			Object lk = loaderbase.executeScalar(sql);

			if(lk != null) 
			{
				if (lk instanceof BigDecimal)
				{
					lk = ((BigDecimal)lk).intValue();
				}

				nk = (Integer)lk + 1;
			}
			else
			{
				loaderbase.execute("INSERT INTO sequence (seq_name,seq_count) VALUES ('"+name+"',0)");
				nk = 1;
			}

			String sql3 = "update sequence SET seq_count="+nk+" WHERE seq_name='"+name+"' AND (seq_count="+lk+" OR COALESCE(seq_count,0)=0);";
			loaderbase.execute(sql3);
			ar = LoaderBase.getUpdateCount();

		} while ( ar == 0);
		
		return (int)nk;	
	}
	

	/**
	 * Erweitert die übergebene Liste der fehlenden Felder um die im akt. Cacheobjekt fehlenden
	 * 
	 * @param felder	Felder, die aus anderen COs Fehler haben
	 * @param schonGeprueft Liste der bereits geprüften Felder
	 * @return Felder, die als Pflichtfelder nicht ausgefüllt sind.
	 */
	public String appendPflichtfelderFehler(String felder, HashSet<IField> schonGeprueft) {
		if (getRowCount() == 0 )
			return felder;
		
		// durchlaufe alle Felder
		Iterator<IField> fields = getFields();
		while(fields.hasNext())
		{
			// alle benötigten Felder überprüfen
			IField field = fields.next();
			if(!schonGeprueft.contains(field) && field.getFieldDescription().getRequired())
			{
				felder = checkPflichfeld(felder, schonGeprueft, field);
			}
		}
		return felder;
	}


	/**
	 * Prüft das übergebene feld und fügt es ggf. der Fehlerliste hinzu
	 * 
	 * @param felder
	 * @param schonGeprueft
	 * @param field
	 * @return
	 */
	protected String checkPflichfeld(String felder, HashSet<IField> schonGeprueft, IField field) {
		schonGeprueft.add(field);

		// alle leeren Felder hinzufügen
		if(field.getValue() == null) 
		{
			if(!felder.isEmpty()) 
			{
				felder += ", ";
			}
			felder += field.getFieldDescription().getCaption();
		}

		// alle String-Felder mit leerem Inhalt hinzufügen
		if(field.getValue() instanceof String)
		{
			if(field.getStringValue().trim().isEmpty()) 
			{
				if(!felder.isEmpty())
				{
					felder += ", ";
				}
				felder += field.getFieldDescription().getCaption();
			}
		}

		return felder;
	}


	/**
	 * Leeres Item hinzufügen, um die Auswahl einer Person zu löschen
	 * 
	 * @param coPerson
	 * @throws Exception
	 */
	public void addEmtyItem() throws Exception {
		begin();
		add();
		setID(0);
		setBezeichnung("");
	}



	/**
	 * Erstellt ein Cacheobject der aktuellen Daten mit ID und Bezeichnung
	 * 
	 * @return Items als Referenztabelle
	 * @throws Exception
	 */
	public CacheObject createRefItemsFromRows() throws Exception {
		CacheObject obj = new CacheObject();
		obj.addField(getField(0).getFieldDescription());
		obj.addField(getFieldBezeichnung().getFieldDescription());
		
		if(moveFirst()) 
		{
			obj.begin();
			do
			{
				obj.add();
				obj.getField(0).setValue(getID());
				obj.getField(1).setValue(getBezeichnung());
			} while(moveNext());
		}
		
		return obj;
	}
	
	
	/**
	 * @return Bitmap für die Navigation
	 */
	public String getNavigationBitmap() {
		return null;
	}

	
	/**
	 * bereinigt Dateinamen, indem der Pfad nicht mit angezeigt wird
	 * 
	 * @param value unbereinigter Dateiname
	 * @return bereinigter Dateiname
	 */
	public static String getDisplayDateiName(String value) {
		if (value == null )
			return null;
		int idx = value.indexOf("\\");
		if (idx == -1 )
			return value;
		else
			return value.substring(idx+1);
	}

	
	/**
	 * Prüft die Daten des akt. Objektes.
	 * 
	 * @return spezielle Fehlermeldungen oder null
	 */
	public String validate() throws Exception{
		return null;
	}
	

	/**
	 * Gibt den Tabellennamen zurück.
	 * 
	 * @return
	 */
	public String getTableName() {
		return getResID().substring(getResID().indexOf(".") + 1);
	}
	
	
	/**
	 * Index des übergebenen Fields
	 * 
	 * @param field
	 * @return
	 */
	public int getFieldIndex(IField field) {
		int iField;
		int anzFields;
		
		anzFields = getFieldCount();
		for (iField=0; iField<anzFields; ++iField)
		{
			if (getField(iField).equals(field))
			{
				return iField;
			}
		}
		
		return -1;
	}

//	/**
//	 * Setzt den Status auf gelöscht.
//	 * 
//	 */
//	public void setStatusGeloescht() {
//		getField("field." + getTableName() + ".statusid").setValue(PZEStartupAdapter.STATUS_GELOESCHT);
//	}
//	
//
//	/**
//	 * Setzt den Status auf nicht gelöscht.
//	 * 
//	 */
//	public void setStatusNichtGeloescht() {
//		getField("field." + getTableName() + ".statusid").setValue(PZEStartupAdapter.STATUS_NICHT_GELOESCHT);
//	}
//	
//
//	/**
//	 * Prüft auf Status gelöscht
//	 * 
//	 * @return Person gelöscht
//	 */
//	public boolean isGeloescht() {
//		return getField("field." + getTableName() + ".statusid").getValue().equals(PZEStartupAdapter.STATUS_GELOESCHT);
//	}

	
	/**
	 * Prüft ob der aktuelle Datensatz leer is
	 * 
	 * @return Datensatz leer
	 */
	public boolean isEmpty() {
		IField field;
		Iterator<IField> iter = getFields();
		while(iter.hasNext())
		{
			field = iter.next();
			if (!field.getFieldDescription().getResID().endsWith("statusid") && field.getValue() != null)
				return false;
		}
		
		return true;
	}
	
	
	/**
	 * Prüft ob Datensätze vorhanden sind
	 * 
	 * @return Datensätze vorhanden
	 */
	public boolean hasRows() {
		return getRowCount() > 0;
	}
	
	
	/**
	 * Prüft ob Datensätze vorhanden sind
	 * 
	 * @return keine Datensätze vorhanden
	 */
	public boolean hasNoRows() {
		return getRowCount() == 0;
	}
	
	
	public void setIsNew() {
		getCurrentRow().setRowState(IBusinessObject.statusAdded);
	}

	protected void addChild(AbstractCacheObject co) throws Exception {
		super.addChild(co);
		
		if (isEditing() && !co.isEditing())
		{
			co.begin();
		}
	}
	

	/**
	 * Alle Einträge löschen<br>
	 * Ohne anschließendes Speichern werden die Daten nur aus dem co gelöscht.
	 * 
	 */
	public void deleteAll() {
		deleteAll(false);
	}
	

	/**
	 * Alle Einträge löschen<br>
	 * 
	 * @param save CO speichern
	 */
	public void deleteAll(boolean save) {
		try 
		{
			if (!isEditing())
			{
				begin();
			}
			
			while (moveFirst())
			{
				delete();
			}
			
			if (save)
			{
				save();
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	

	/**
	 * Gehe zu dem Eintrag mit der ID
	 * 
	 * @param id
	 */
	public boolean moveToID(int id) {
		return moveTo(id, "field." + getTableName() + ".id");
	}
	

	/**
	 * Gehe zu dem Eintrag mit der Bezeichnung
	 * 
	 * @param bezeichnung
	 */
	public boolean moveToBezeichnung(String bezeichnung) {
		return moveTo(bezeichnung, "field." + getTableName() + ".bezeichnung");
	}
	

	/**
	 * Gehe zu dem Eintrag mit der PersonID
	 * 
	 * @param personId
	 */
	public boolean moveToPersonID(int personId) {
		return moveTo(personId, "field." + getTableName() + ".personid");
	}
	

	/**
	 * CO nach Datum sortieren
	 * 
	 * @param chronologisch das älteste Datum als erstes
	 * @throws Exception
	 */
	public void sortByDatum(boolean chronologisch) throws Exception{
		sort(getFieldDatum().getFieldDescription().getResID(), !chronologisch);
	}
	
	
	/**
	 * Kopiert das aktuelle CO auf das übergebene und kopiert/verdoppelt die Zeile des Bookmarks 
	 * 
	 * @param co CO auf das die Daten kopiert werden sollen
	 * @param bookmark
	 * @return
	 */
	public void createCoWithCopiedRow(AbstractCacheObject co, int currentRowIndex) {
		
		try
		{
			Object newBookmark;
			
			newBookmark = null;
			
			if (currentRowIndex < 0)
			{
				return;
			}

			if (!co.isEditing())
			{
				co.begin();
			}
			
			// aktuellen und zu kopierenden bookmark
			moveTo(currentRowIndex);

			// Cacheobjekt durchlaufen und die Daten kopieren
			if (moveFirst())
			{				
				do 
				{
					// Zeile in temporäres Cacheobject hinzufügen
					copyCurrentRowDataToCo(co);
					
					// wenn die zu kopierende Zeile erreicht wurde
					if (getCurrentRowIndex() == currentRowIndex)
					{
						// füge sie ein weiteres Mal hinzu
						copyCurrentRowDataToCo(co);
						
						// der Primary-Kex darf nicht kopiert werden
						if (co.getField(0).getFieldDescription().getIsPrimaryKey())
						{
							co.getField(0).setValue(null);
						}
						co.getCurrentRow().setRowState(IBusinessObject.statusAdded);
						newBookmark = co.getBookmark();
					}

				} while (moveNext());
			}
			
			co.moveTo(newBookmark);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return;
	}


	/**
	 *  kopiert die aktuelle Zeile in das übergebene Cacheobject
	 * 
	 * @param co
	 * @throws Exception
	 */
	private void copyCurrentRowDataToCo(AbstractCacheObject co) throws Exception {
		int iCol, anzCols;
		

		// Zeile hinzufügen
		co.add();
		
		// Spalten durchlaufen und Werte kopieren
		anzCols = getColumnCount();
		for (iCol=0; iCol<anzCols; ++iCol)
		{
			co.getField(iCol).setValue(getField(iCol).getValue());
		}
		
		return;
	}

	
	/**
	 * Alle geänderten Rows des CO bestimmen
	 * 
	 * @param coChangedRows CO, in dem die geänderte Rows gespeichert werden
	 * @throws Exception
	 */
	public void getChangedRows(AbstractCacheObject coChangedRows) throws Exception{
		int rowState;
		IRow row;

		
		if (!moveFirst())
		{
			return;
		}

		// prüfen, ob co im Edit-mode ist
		if (!coChangedRows.isEditing())
		{
			coChangedRows.begin();
		}
		
		// alle Datensätze merken, die geändert wurden
		do
		{
			row = getCurrentRow();
			rowState = row.getRowState();
			
			if (rowState != IBusinessObject.statusUnchanged)
			{
				// neue Zeile anlegen und Werte kopieren
				coChangedRows.add();
				copyRow(coChangedRows);
			}
			
		} while (moveNext());
		
	}

	
	/**
	 * alle IDs als Komma-getrennter String
	 * 
	 * @return 
	 * @throws Exception 
	 */
	public String getIDs() throws Exception {
		String values = null;
		
		if (moveFirst())
		{
			values = "";
			
			do 
			{
				values += getID() + ", ";
			} while (moveNext());
			
			values = listetoString(values);
		}
		
		return values;
	}

	
	/**
	 * alle bezeichnungen als Komma-getrennter String
	 * 
	 * @return 
	 * @throws Exception 
	 */
	public String getBezeichnungen() throws Exception {
		String values = null;
		
		if (moveFirst())
		{
			values = "";
			
			do 
			{
				values += getBezeichnung() + ", ";
			} while (moveNext());
			
			values = listetoString(values);
		}
		
		return values;
	}


	/**
	 * Macht aus dem übergebenen String eine mit Komma getrennte Liste
	 * 
	 * @param values
	 * @return Liste der IDs oder null
	 */
	protected String listetoString(String values) {
		if (!values.isEmpty())
		{
			values = values.substring(0, values.length()-2);
		}
		else
		{
			values = null;
		}
		
		return values;
	}


	/**
	 * Summenzeile hinzufügen und Summe berechnen
	 * 
	 * @throws Exception
	 */
	protected void addRowSumme() throws Exception {
		int summe, iField, anzFields;
		IField field;
		FieldType fieldType;
		
		// Summe in erste Spalte schreiben
		add();
		getField(0).setValue("Summe");
		

		// Fields durchlaufen
		anzFields = getColumnCount();
		for (iField=1; iField<anzFields; ++iField)
		{
			field = getField(iField);
			fieldType = field.getFieldDescription().getFieldType();
			if (fieldType == FieldType.DATE || !field.getFieldDescription().getRefTableName().isEmpty())
			{
				continue;
			}
			
			summe = 0;
			moveFirst();
			do
			{
				summe += Format.getIntValue(field.getValue());

			} while (moveNext());
			
			// Summe speichern
			field.setValue(summe);
		}
		
//		setModified(false);
	}

	
	/**
	 * Prüft, ob ein Feld geändert wurde
	 * 
	 * @return
	 */
	protected boolean hasModifiedRows() {
		Iterator<IField> it = getFields();
		
		//------- Schleife über alle Felder
		
		while(it.hasNext())
		{
			IField fld = (IField) it.next();
			IFieldDescription fd = fld.getFieldDescription();
			PersistanceType pt = fd.getPersistanceType();
			
			if(pt != PersistanceType.ReadWrite)
				continue;
			
			if(fld.getState() == IBusinessObject.statusChanged)
			{
				return true;
			}
			
		}
		
		return false;
	}


	/**
	 * prüft, ob am Anfang des where-Statements ein AND steht
	 * 
	 * @param where Statement ohne AND am Anfang
	 * @return
	 */
	public static String checkAndAtBeginning(String where) {
		if (where == null)
		{
			return null;
		}
		if (where.trim().startsWith("AND"))
		{
			return where.substring(4);
		}

		return where;
	}


}
