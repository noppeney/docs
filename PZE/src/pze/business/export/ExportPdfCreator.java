package pze.business.export;

import java.util.Date;

import org.apache.poi.ss.formula.eval.NotImplementedException;

import framework.Application;
import framework.business.fields.HeaderDescription;
import framework.business.interfaces.FieldType;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.fields.IFieldDescription;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Abstrakte Klasse zum Erstellen einer HTML-Datei für den PDF-Export.<br>
 * Hier sind allgemeine Methoden implementiert.
 * 
 * @author Lisiecki
 */
public abstract class ExportPdfCreator {

	protected StringBuilder m_sb;
	
	protected AbstractCacheObject m_co;
	protected IHeaderDescription m_headerDescription;
	
	
	/**
	 * Gibt eine Tabelle einer Oberfläche in XHTML zurück.
	 * 
	 * @param formPersonMonatseinsatzblatt
	 * @return
	 * @throws Exception 
	 */
	public String createHtml(UniFormWithSaveLogic form) throws Exception{
		throw new NotImplementedException("Methode nicht implementiert. Ggf. createHtml(AbstractCacheObject co) nutzen oder implementieren");
	};

	
	/**
	 * Gibt daten eines CacheObjects in XHTML zurück.
	 * 
	 * @param formPersonMonatseinsatzblatt
	 * @return
	 * @throws Exception 
	 */
	public String createHtml(AbstractCacheObject co) throws Exception{
		throw new NotImplementedException("Methode nicht implementiert. Ggf. createHtml(UniFormWithSaveLogic form) nutzen oder implementieren");
	};


	/**
	 * Open-Tag und Einbindung CSS
	 */
	protected void writeHtmlOpen() {
		m_sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"" 
				+ " \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n "
				+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		
		// Einbindung der CSS-Datei
		m_sb.append("<head>\n");
		m_sb.append("<link rel='stylesheet' href='/" + Application.getWorkingDirectory().replace("\\", "/") + "PdfExport.css' ></link>\n");
		m_sb.append("<link rel='stylesheet' href='/" + Application.getWorkingDirectory().replace("\\", "/") + "FooterStandSeitenzahl.css' ></link>\n");
		if (isQuerformat())
		{
			m_sb.append("<style type='text/css'> @page {size: landscape} </style>");
		}
		m_sb.append("</head>\n");
		m_sb.append("<body>\n");
	}


	/**
	 * Prüfen, ob die Datei im Querformat ausgegeben werden soll
	 * 
	 * @return
	 */
	protected boolean isQuerformat() {
		return false;
	}


	/**
	 * Html-Tag schließen
	 */
	protected void writeHtmlClose() {
		m_sb.append("</body>\n");
		m_sb.append("</html>");
	}


	/**
	 * Eintrag der Projektbeschreibung schreiben
	 * 
	 * @param beschriftung
	 * @param wert
	 */
	protected void writeProjektbeschreibungLinksbuendig(String beschriftung, String wert) {
		writeProjektbeschreibung(beschriftung, wert, true);
	}


	/**
	 * Eintrag der Projektbeschreibung schreiben
	 * 
	 * @param beschriftung
	 * @param wert
	 */
	protected void writeProjektbeschreibungRechtsbuendig(String beschriftung, String wert) {
		writeProjektbeschreibung(beschriftung, wert, false);
	}


	/**
	 * Eintrag der Projektbeschreibung schreiben
	 * 
	 * @param beschriftung
	 * @param wert
	 */
	protected void writeProjektbeschreibung(String beschriftung, String wert, boolean linksbuendig) {
		m_sb.append("<tr><td class='unsichtbar textoben textlinks kein_umbruch'>" + beschriftung + "&nbsp;</td><td class='unsichtbar " 
				+ (linksbuendig ? "textlinks" : "textrechts") + "'>" 
				+ (wert == null ? "" : Format.getConformXml(wert)) 
				+ "</td></tr>\n");
	}


	/**
	 * String mit Table-Tag für das CO
	 * 
	 * @param table
	 * @return
	 * @throws Exception
	 */
	protected String getHtmlStringTable(SortedTableControl table) throws Exception {
		StringBuilder sb;
		
		if (table.getData().getRowCount() == 0)
		{
			return null;
		}
		
		sb = new StringBuilder();
		
		// Cacheobject zum Export vorbeiten
		initCo(table);
		initHeaderDescription(table);
		
		sb.append("<table class='" + getClassTable() + "' style='float: left; " + getStyleTable() + "'>\n");

		sb.append(getHtmlStringTableUeberschriften());
		sb.append(getHtmlStringTableDaten());

		sb.append("</table>\n");

		return sb.toString();
	}
	

	/**
	 * String mit Table-Tag für das CO
	 * 
	 * @param table
	 * @return
	 * @throws Exception
	 */
	protected String getHtmlStringTableCo(AbstractCacheObject co) throws Exception {
		int iField, anzField;
		StringBuilder sb;
		
		if (co.getRowCount() == 0)
		{
			return null;
		}
		
		sb = new StringBuilder();
		
		// Cacheobject zum Export vorbeiten
		m_co = manipuliereCo(co);
		m_headerDescription = new HeaderDescription("");
		
		// Header bestimmen
		anzField = m_co.getColumnCount();
		for (iField=0; iField<anzField; ++iField)
		{
			m_headerDescription.add(m_co.getField(iField).getFieldDescription());
		}

		// Ausgabe
		sb.append("<table class='" + getClassTable() + "' style='float: left; " + getStyleTable() + "'>\n");

		sb.append(getHtmlStringTableUeberschriften());
		sb.append(getHtmlStringTableDaten());

		sb.append("</table>\n");

		return sb.toString();
	}
	
	
	/**
	 * Table-Class für die Datentabelle
	 * 
	 * @return
	 */
	protected String getClassTable(){
		return "";
	}

	
	/**
	 * Table-Style für die Datentabelle
	 * 
	 * @return
	 */
	protected String getStyleTable(){
		return "";
	}


	/**
	 * Row-Tag mit Überschriften des CO
	 * 
	 * @param table
	 * @return 
	 */
	protected String getHtmlStringTableUeberschriften() {
		int iField, anzFields;
		StringBuilder sb;
		IColumnDescription columnDescription;
		IFieldDescription fieldDescription;
		IField coField;
		
		sb = new StringBuilder();
		sb.append("<tr>\n");

		// co durchlaufen
		anzFields = m_headerDescription.getColumnCount();
		for (iField=0; iField<anzFields; ++iField)
		{
			columnDescription = m_headerDescription.getColumnDescription(iField);
			fieldDescription = columnDescription.getFieldDescription();
			coField = m_co.getField(fieldDescription.getResID());
			
			// prüfen, ob das Feld existiert (felder können aus dem co gelöscht werden, damit sie nicht exportiert werden)
			if (coField == null)
			{
				continue;
			}

			sb.append("<th class='" + getClassTdUeberschriften(iField) + "'>");
			sb.append(columnDescription.getCaption() + "</th>\n");
		}
		
		sb.append("</tr>\n");
		
		return sb.toString();
	}


	/**
	 * Row-Tags mit Daten des CO
	 * 
	 * @return
	 * @throws Exception
	 */
	protected String getHtmlStringTableDaten() throws Exception {
		int iRow;
		int anzRows, iField, anzFields;
		String stringValue;
		StringBuilder sb;
		IFieldDescription fieldDescription;
		IField field;
		
		sb = new StringBuilder();
		
		anzRows = m_co.getRowCount();
		anzFields = m_headerDescription.getColumnCount();
		
	
		// co durchlaufen
		m_co.moveFirst();
		for (iRow=0; iRow<anzRows; ++iRow)
		{
			sb.append("<tr>\n");

			for (iField=0; iField<anzFields; ++iField)
			{
				fieldDescription = m_headerDescription.getColumnDescription(iField).getFieldDescription();
				field = m_co.getField(fieldDescription.getResID());
				// prüfen, ob das Feld existiert (felder können aus dem co gelöscht werden, damit sie nicht exportiert werden)
				if (field == null)
				{
					continue;
				}
				stringValue = Format.getConformXml(field.getDisplayValue());

				// ggf. Text formatieren
				stringValue = formatiereText(stringValue, fieldDescription);
				
//				sb.append("<td class='" + getClassTdDaten(iRow, iField) + "' bgcolor='lime'>");
				sb.append("<td class='" + getClassTdDaten(iRow, iField) + "' " + getHtmlAttribute(iRow, iField) + ">");
//				sb.append("<td class='tnlinks" + (iRow > anzRows ? " umrandet" : "") + "'>");
				
				sb.append(stringValue);

				sb.append("</td>\n");
			}
			
			sb.append("</tr>\n");
			
			m_co.moveNext();
		}
		
		return sb.toString();
	}


	/**
	 * Zahlenformate anpassen
	 * 
	 * @param stringValue
	 * @param fieldDescription
	 * @return
	 * @throws Exception 
	 */
	protected String formatiereText(String stringValue, IFieldDescription fieldDescription) throws Exception {
		
		// Zeitfelder abfangen und formatieren
		if (fieldDescription.getFieldType() == FieldType.TEXT && fieldDescription.getDataFormat().equals("0:00"))
		{
			stringValue = getZeitAsText(stringValue);
		}
		
		// Integer abfangen und ggf. formatieren
		if (fieldDescription.getFieldType() == FieldType.INTEGER)
		{
			stringValue = getIntegerAsText(stringValue);
		}
		
		return stringValue;
	}


	/**
	 * Zeit in Minuten (String) in Uhrzeit String umwandeln.<br>
	 * Ist in Funktion ausgelagert, um es in abgeleiteten Klassen zu ändern.
	 * 
	 * @param stringValue
	 * @return
	 */
	protected String getZeitAsText(String stringValue) {
		stringValue = Format.getZeitAsText(Format.getIntValue(stringValue));
		return stringValue;
	}


	/**
	 * Integer-Werte (String) umwandeln.<br>
	 * Ist in Funktion ausgelagert, um es in abgeleiteten Klassen zu ändern.
	 * 
	 * @param stringValue
	 * @return
	 */
	protected String getIntegerAsText(String stringValue) {
		return stringValue;
	}


	protected void initCo(SortedTableControl table) throws Exception {
		m_co = (AbstractCacheObject) table.getData();
		m_co = manipuliereCo(m_co);
	}


	protected void initHeaderDescription(SortedTableControl table) throws Exception {
		m_headerDescription = manipuliereHeaderDescription(table);
	}


	/**
	 * Cacheobjekt vor der Ausgabe ggf. manipulieren, z. B. Spalten löschen
	 * 
	 * @param co
	 * @return
	 * @throws Exception 
	 */
	protected AbstractCacheObject manipuliereCo(AbstractCacheObject co) throws Exception {
		return co;
	}


	/**
	 * HeaderDescription vor der Ausgabe ggf. manipulieren, um eine andere Reihenfolge der Spalten festzulegen
	 * 
	 * @param co
	 * @return
	 * @throws Exception 
	 */
	protected IHeaderDescription manipuliereHeaderDescription(SortedTableControl table) {
		return table.getHeaderDescription();
	}


	/**
	 * HTML-Class-Bezeichnung für Daten-Überschriften
	 * 
	 * @param iField 
	 * @return
	 */
	protected String getClassTdUeberschriften(int iField) {
		return "";
	}


	/**
	 * HTML-Class-Bezeichnung für Daten-Felder
	 * 
	 * @param iRow 
	 * @param iField 
	 * @return
	 */
	protected String getClassTdDaten(int iRow, int iField) {
		return "";
	}


	/**
	 * HTML-Attribute für Daten-Felder
	 * 
	 * @param iRow 
	 * @param iField 
	 * @return
	 */
	protected String getHtmlAttribute(int iRow, int iField) {
		return "";
	}


	/**
	 * Zeile mit dem Stand der Datei und Seitenzahl
	 */
	protected void writeFooter() {
		m_sb.append("<div class='footer'>\n");
		// Wunsch von Fr. Jonas, nach Rücksprache mit Frau Sentis doch nicht mehr gewünscht
		// dann hat Frau Sentis es sich gewünscht
		m_sb.append("<span class='blank'>Stand: " + getStand() + ", erstellt am " + Format.getString(new Date()) + "</span>\n");
//		m_sb.append("<span class='blank'>Stand: " + getStand() + "</span>\n");
		m_sb.append("</div>\n");
	}

	
	/**
	 * Zeilenumbrüche hinzufügen
	 * 
	 * @param anzBr
	 */
	protected void appendBr(int anzBr){
		int iBr;
		
		for (iBr=0; iBr<anzBr; ++iBr)
		{
			m_sb.append("<br />");
		}
		
		m_sb.append("\n");
	}
	
	
	/** 
	 * Es wird eine neue Tabellenzeile erzeugt und die vorherige geschlossen
	 */
	protected void appendTabellenZeilenumbruch()
	{
		appendTabellenZeilenumbruch(m_sb);
	}
	 
	
	/** 
	 * Es wird eine neue Tabellenzeile erzeugt und die vorherige geschlossen
	 */
	protected void appendTabellenZeilenumbruch(StringBuilder sb)
	{
		sb.append("            </tr>\n");
		sb.append("            <tr>\n");
	}
	

	/** 
	 * Es werden das Ende für die letzte Zeile und die Tabelle erzeugt
	 */
	protected void appendTabellenEnde()
	{
		m_sb.append("            </tr>\n");
		m_sb.append("        </table>\n");
	}
	
	
	/**
	 * Stand des Dokuments, wird bei writeFooter verwendet
	 * @return
	 */
	protected abstract String getStand();


}
