package pze.business.datentransfer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import framework.Application;
import framework.business.cacheobject.CacheObject;
import framework.business.interfaces.FieldType;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.auswertung.CoAnwesenheitUebersicht;
import pze.ui.controls.SortedTableControl;
import startup.PZEStartupAdapter;


// TODO Excelexporter Vergleich mit  framework.documents.excel.ExcelExporter;


/**
 * Exporter um CO in Excel-Dateien zu exportieren
 * 
 * @author Lisiecki
 */ 
public class Excelexporter {

	private static final String SHEETNAME_DEFAULT = "Tabelle";
	private static final HSSFRichTextString EMPTY_CELL_SIGN = new HSSFRichTextString("x");
	
	private static final String CELLSTYLE_NORMAL = "cell_normal";
	private static final String CELLSTYLE_FETTKURSIV = "cell_fettkursiv";
	private static final String CELLSTYLE_DOUBLE = "cell_double";
	private static final String CELLSTYLE_DOUBLE_PROZENT = "cell_double_prozent";
	private static final String CELLSTYLE_DATUM = "cell_date_short";


	private HSSFWorkbook m_wb;
	private HSSFSheet m_sheet;

	private String m_filename;

	private Map<String, HSSFCellStyle> m_styles;

	private CacheObject m_co;
	/**
	 * TableControl mit den auszugebenen Feldern
	 */
	private SortedTableControl m_table;
	
	/**
	 * für leere Zellinhalte "x" eintragen
	 */
	private boolean m_withEmptyCellSign;


	/**
	 * Konstruktion
	 * 
	 * @param withEmptySign für leere Zellinhalte "-" eintragen
	 * @throws Exception
	 */
	public Excelexporter(boolean withEmptySign) throws Exception {
		m_withEmptyCellSign = withEmptySign;
		
		init();
	}

	
	/**
	 * Initialisierung
	 */
	private void init(){
		m_wb = new HSSFWorkbook();
		
		m_styles = new HashMap<String, HSSFCellStyle>();
		
		// Zellen-Styles definieren
		createStyles();
	}
	
	
	/**
	 * Initialisierung der Vorlage
	 * 
	 * @throws IOException 
	 */
	public void initVorlageAuswertungAktivitaet() throws IOException{
		InputStream inp = PZEStartupAdapter.class.getResourceAsStream("/templates/VorlageAuswertungAktivitaet.xls");
		
		m_wb = new HSSFWorkbook(inp);
		m_sheet = m_wb.getSheetAt(0);
	}
	

	/**
	 * Setter für Filename
	 * 
	 * @param filename Filename
	 */
	public void setFilename(String filename) {
		m_filename = filename;		
	}


	/**
	 * Setter für Cacheobject
	 * 
	 * @param co auszugebenes Cacheobject
	 */
	public void setCo(CacheObject co) {
		setCo(co, null);
	}


	/**
	 * Setter für Cacheobject
	 * 
	 * @param co auszugebenes Cacheobject
	 * @param SortedTableControl table, die die auszugebenen Felder und deren Reihenfolge bestimmt 
	 */
	public void setCo(CacheObject co, SortedTableControl table) {
		m_co = co;
		m_table = table;
	}


	/**
	 * Setter für Sheetname
	 * 
	 * @param sheetname Sheetname oder null
	 */
	public void createSheet(String sheetname) {
		
		// wenn kein Sheetname übergeben wurde, generiere einen über die Anzahl der bereits vorhandenen Sheets
		if (sheetname == null)
		{
			sheetname = SHEETNAME_DEFAULT + (m_wb.getNumberOfSheets() + 1);
		}
		
		m_sheet = m_wb.createSheet(sheetname);
	}


	/**
	 *  Export starten
	 *  
	 * @throws Exception
	 */
	public boolean export() throws Exception {
		String fehlertext;
		
		// Prüfung, ob Daten vorhanden sind
		fehlertext = validate();
		if(fehlertext != null)
		{
			Messages.showErrorMessage("Fehler beim Export der Excel-Datei", fehlertext);
			return false;
		}

		// setze Printlayout-Einstellung
		setPrintLayout();

		// schreibe Header
		writeHeader();		

		// schreibe Daten
		writeCoData();

		// Spaltenbreiten setzen
		setColumnWidth();

		// Programmversion nach dem Einstellen der Spaltenbreiten machen, damit die Länge des Infotextes nicht berücksichtigt wird
		writeProgrammversion();

		// Datei speichern
		saveFile();		

		// Datei öffnen
		FileHandler.openFile(m_filename);
		
		return true;
	}

	
	/**
	 * Prüft, ob alle notwendigen Angaben für den Export gemacht wurden
	 * 
	 * @return Fehlermeldung oder null wenn alles ok ist
	 */
	private String validate(){
		String result;
		
		result = null;
		
		if (m_filename.length() == 0)
		{
			result = "Dateiname wurde nicht gesetzt.";
		}
		
		if (m_sheet == null)
		{
			createSheet(null);
		}
		
		if (m_co == null)
		{
			result = "Daten wurden nicht gesetzt.";
		}
		
		if (m_co.getRowCount() == 0)
		{
			result = "Keine Daten gefunden.";
		}
		
		if (m_co.getRowCount() > 65500)
		{
			result = "Zu viele Daten gefunden.";
		}
				
		return result;
	}
	

	/**
	 * stelle das Drucklayout ein
	 * ( ist nötig für HSSFWorksheets)
	 */
	private void setPrintLayout() {
		m_sheet.setDisplayGridlines(false);
		m_sheet.setFitToPage(true);
		m_sheet.setPrintGridlines(false);
		m_sheet.setHorizontallyCenter(true);		
		HSSFPrintSetup printSetup = m_sheet.getPrintSetup();		
		printSetup.setFitHeight((short) 1);
		printSetup.setFitWidth((short) 1);
		m_sheet.setAutobreaks(true);
	}


	/**
	 * Header in die Datei schreiben
	 */
	private void writeHeader() throws Exception {
		int iCol, anzCols;
		String caption;
		HSSFRow row;
		HSSFCell cell;
		IHeaderDescription headerDescription;
		IColumnDescription columnDescription;

		row = m_sheet.createRow(0);
		headerDescription = m_table.getHeaderDescription();

		anzCols = getAnzCol();
		for(iCol=0; iCol<anzCols; iCol++)
		{
//			field = getField(iCol);
//			caption = field.getFieldDescription().getCaption();
			
			columnDescription = headerDescription.getColumnDescription(iCol);
			caption = columnDescription.getCaption();

			cell = row.createCell(iCol);
			cell.setCellValue(caption);
			cell.setCellStyle(m_styles.get("header"));			
		}
	}


	/**
	 * Daten des CO in die Datei schreiben
	 * 
	 */
	private void writeCoData() {
		int iRow, iCol, anzCol;
		String displayValue;
		Object value;
		IField field;
		FieldType fieldType;
		HSSFRow row;
		HSSFCell cell;

		if (!m_co.moveFirst())
		{
			return;
		}

		anzCol = getAnzCol();

		do
		{
			iRow = m_co.getCurrentRowIndex();
			row = m_sheet.createRow(iRow+1);

			for(iCol=0; iCol<anzCol; iCol++)
			{
				cell = row.createCell(iCol);
				field = getField(iCol);

				value = field.getValue();
				displayValue = field.getDisplayValue();
				
				fieldType = field.getFieldDescription().getFieldType();


				// leere Zellen abfangen
				if(value == null)
				{
					writeEmptyCell(cell);
					continue;
				}

				// Bei Fremdschlüssel von Referenztabellen den angezeigten Wert verwenden
				if (fieldType.equals(FieldType.INTEGER) && Format.getIntValue(value) != Format.getIntValue(displayValue))
				{
					fieldType = FieldType.TEXT;
					value = displayValue;
				}

				// Datentyp-spezifische Formatierung
				switch(fieldType)
				{		

				case BOOLEAN:
					setCellStyle(cell, CELLSTYLE_NORMAL);
					cell.setCellValue(Format.getBooleanValue(value));
					break;

				case TEXT:
					if (m_table.isZeitField(field))
					{
						setCellStyle(cell, CELLSTYLE_DOUBLE);
						value = Format.getIntValue(value) / 60.;
						cell.setCellValue(Format.getDoubleValue(value));
						break;
					}

					if (m_co instanceof CoAnwesenheitUebersicht && ((CoAnwesenheitUebersicht)m_co).isGeplanteBuchung(iRow, iCol))
					{
						setCellStyle(cell, CELLSTYLE_FETTKURSIV);
					}
					else
					{
						setCellStyle(cell, CELLSTYLE_NORMAL);
					}
					cell.setCellValue(Format.getStringValue(value));
					break;

				case NONE:
					setCellStyle(cell, CELLSTYLE_NORMAL);
					cell.setCellValue(Format.getStringValue(value));
					break;

				case DATE:
					setCellStyle(cell, CELLSTYLE_DATUM);
					cell.setCellValue(Format.getGregorianCalendar(Format.getDateValue(value)));									
					break;

				case INTEGER:
					setCellStyle(cell, CELLSTYLE_NORMAL);
					cell.setCellValue(Format.getIntValue(value));
					break;

				case LONG:
					setCellStyle(cell, CELLSTYLE_DOUBLE);
					cell.setCellValue(Format.getLongValue(value));
					break;

				case DOUBLE:
					
					// Angaben in Prozent abfangen, z. B. Verbrauch
					if (m_table.isProzentField(field))
					{
						setCellStyle(cell, CELLSTYLE_DOUBLE_PROZENT);
					}
					else
					{
						setCellStyle(cell, CELLSTYLE_DOUBLE);
					}

					cell.setCellValue(Format.getDoubleValue(value));
					break;

				}
				
			}
		}while(m_co.moveNext());

	}


	/**
	 * Feld des CO mit der übergebenen Nr.<br>
	 * Wenn eine tabelle übergeben wurde, das Feld mit der entsprechenden Tabellenspalte.
	 * 
	 * @param iCol
	 * @return
	 */
	private IField getField(int iCol) {
		String resID;
		IField field;

		if (m_table == null)
		{
			field = m_co.getField(iCol);
		}
		else
		{
			resID = m_table.getHeaderDescription().getColumnDescription(iCol).getResID();
			field = m_co.getField(resID);
		}
		
		return field;
	}


	private void setCellStyle(HSSFCell cell, String stylename) {
		cell.setCellStyle(m_styles.get(stylename));
	}


	/**
	 * Leere Zelle schreiben. Entweder bleibt sie leer, oder das EMTY_CELL_SIGN wird eingetragen.
	 * 
	 * @param cell
	 */
	private void writeEmptyCell(HSSFCell cell) {

		// Rahmen setzen, auch wenn es keinen Inhalt gibt
		setCellStyle(cell, "cell_normal");

		if(m_withEmptyCellSign)
		{
			cell.setCellValue(EMPTY_CELL_SIGN);
		}
	}


	/**
	 * Spaltenbreite anpassen an Inhalt/Header
	 */
	private void setColumnWidth() {
		int iCol, anzCols;

		anzCols = getAnzCol();
		for(iCol=0; iCol<anzCols; iCol++)
		{
			m_sheet.autoSizeColumn(iCol);
		}
	}


	/**
	 * Anzahl Spalten
	 * 
	 * @return
	 */
	private int getAnzCol() {
		
		if (m_table != null)
		{
			return m_table.getHeaderDescription().getColumnCount();
		}
		else
		{
			return m_co.getColumnCount();
		}
	}


	/**
	 * Programmversion ausgeben
	 */
	private void writeProgrammversion() {
		HSSFRow row;
		HSSFCell cell;
		
		row	= m_sheet.createRow(m_co.getRowCount() + 2);
		cell = row.createCell(0);
		cell.setCellValue("Diese Datei wurde erstellt mit PZE " + Application.getVersionString() + ".");
	}


	/**
	 * interne Speicherprozedur zum abschliessenden Speichern
	 */
	private void saveFile() throws Exception {
		FileOutputStream fos;

		fos = new FileOutputStream(m_filename);
		
		m_wb.write(fos);
		
		fos.close();
	}


	/**
	 * Setter FieldTypesReport
	 * @param co 
	 * @param fieldTypes Hashtable<Integer,FieldType>
	 */
//	private void setFieldTypes(CacheObject co) {
//		int i;
//		Hashtable<Integer, FieldType> fieldTypes;
//
//		i = 0;
//		fieldTypes = new Hashtable<Integer, FieldType>();
//
//		IField field;
//		Iterator<IField> fieldIterator = co.getFields();
//
//		while (fieldIterator.hasNext())
//		{
//			field = fieldIterator.next();
//			fieldTypes.put(i++, field.getFieldDescription().getFieldType());
//		} 
//
//
//		m_fieldTypes = fieldTypes;
//	}

	
	/**
	 * Rahmen wird um einzelne HSSFCell gebildet
	 * hier: dünner, schwarzer Rahmen um alle Zellen (oben,unten,links,rechts)
	 * wird von createStyles pro Style aufgerufen
	 * letztendlich wird dann dieser Style für die Gestaltung der Zelle, bevor eigentliche
	 * Daten in Zellen im HSSFWorksheet geschrieben werden von Hauptmethode startExcelExport
	 * aufgerufen
	 * 
	 * @param wb Workbook
	 * @return
	 */
	private HSSFCellStyle createBorderedStyle(HSSFWorkbook wb) {
		HSSFCellStyle style = wb.createCellStyle();
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);
		style.setTopBorderColor(HSSFColor.BLACK.index);

		return style;
	}

	
	/**
	 * Styles für die Zellen der Spaltenüberschrift und der Daten werden erstellt
	 */
	private void createStyles() {
		HSSFDataFormat df = m_wb.createDataFormat();
		HSSFCellStyle style;

		//Header
		//Zentriert, Fettschrift,Farbverlauf hellblau mit dunkler farbl. Hervorhebung
		HSSFFont headerFont = m_wb.createFont();
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style = createBorderedStyle(m_wb);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setFont(headerFont);
		m_styles.put("header", style);

		//Styles für Zellen
		//Datumsformat
		//DateFormat "dd.MM.yyyy"
		//Font: Grösse 12 Points
		//rechts ausgerichtet
		//gewrappter Text
		HSSFFont cellFont = m_wb.createFont();

		//Short Datumsformat ( d.h. ohne Stunden, Minuten und Sek.)
		//wird abhängig von Einstellung im Directionary herangezogen zur Formatierung des
		//entspr. Datums
		style = createBorderedStyle(m_wb);
		style.setDataFormat(df.getFormat("dd.mm.yyyy"));
		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		style.setWrapText(true);
		style.setFont(cellFont);
		m_styles.put("cell_date_short",style);

		//Zahlenformate
		//Double ... Long
		//Dateformat "0.00"
		//rechte Ausrichtung
		style = createBorderedStyle(m_wb);
		style.setDataFormat(df.getFormat("0.0"));
		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		style.setFont(cellFont);
		m_styles.put("cell_double", style);

		//Zahlenformate
		//Double ... Long
		//Dateformat "0.00"
		//rechte Ausrichtung
		style = createBorderedStyle(m_wb);
		style.setDataFormat(df.getFormat("0.000E+00"));
		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		style.setFont(cellFont);
		m_styles.put("cell_double_3nks", style);

		//Zahlenformate in %
		//Double ... Long
		//Dateformat "0.00"
		//rechte Ausrichtung
		style = createBorderedStyle(m_wb);
		style.setDataFormat(df.getFormat("0%"));
		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		style.setFont(cellFont);
		m_styles.put("cell_double_prozent", style);

		//Integer
		//Ausrichtung: rechts
		//Dateformat: "#0"
		style = createBorderedStyle(m_wb);
		style.setDataFormat(df.getFormat("0"));
		style.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		style.setFont(cellFont);
		m_styles.put("cell_int_normal",style);

		//Normale Zelle (Text)
		//gewrappter Text
		//Fontgrösse : s.o.
		//Ausrichtung: links
		style = createBorderedStyle(m_wb);
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style.setWrapText(true);
		style.setFont(cellFont);
		m_styles.put("cell_normal",style);

		//fett & kursiv Zelle (Text)
		//gewrappter Text
		//Fontgrösse : s.o.
		//Ausrichtung: links
		HSSFFont cellFontFettKursiv = m_wb.createFont();
		cellFontFettKursiv.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellFontFettKursiv.setItalic(true);
		style = createBorderedStyle(m_wb);
		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style.setWrapText(true);
		style.setFont(cellFontFettKursiv);
		m_styles.put(CELLSTYLE_FETTKURSIV, style);
	}


	/**
	 * Export des Dosisleistungsprotokolls
	 * 
	 * @param jahr Jahr
	 * @param monat Monat
	 * @param coPerson CO der Person
	 * @throws Exception
	 */
//	public void exportDosisprotokoll(CoPerson coPerson, int monat, int jahr) throws Exception {
//		int tag, lastTag, dosis;
//		String value, bemerkung;
//		CoZugangPerson coZugangPerson;
//		InputStream inp = StartupAdapter.class.getResourceAsStream("/templates/VorlageDosisprotokoll.xls");
//		
//		m_wb = new HSSFWorkbook(inp);
//		m_sheet = m_wb.getSheetAt(0);
//		coZugangPerson = new CoZugangPerson();
//
//		setTextLeft(coPerson.getName(), 7, 1);
//		setTextLeft(String.format("%2d/%4d", monat, jahr), 7, 4);
//		setTextLeft(Format.getString(Format.getGregorianCalendar(null)), 42, 1);
//
//		// Dosimeter
//		coZugangPerson.emptyCache();
//		coZugangPerson.loadDosimeterByPersonZeitraum(coPerson.getID(), monat, jahr);
//		value = coZugangPerson.getAllDosimeter();
//		if (value != null)
//		{
//			setTextLeft(value, 6, 2);
//		}
//		
//		// Dosis
//		coZugangPerson.emptyCache();
//		coZugangPerson.loadDosisByPersonZeitraum(coPerson.getID(), monat, jahr);
//		if (coZugangPerson.moveFirst())
//		{
//			lastTag = 0;
//			dosis = 0;
//			bemerkung = "";
//
//			do
//			{
//				// Werte müssen bei mehreren Zugängen an einem Tag summiert werden
//				tag = coZugangPerson.getTag();
//				if (tag != lastTag)
//				{
//					dosis = 0;
//					bemerkung = "";
//				}
//				lastTag = tag;
//				
//				dosis += coZugangPerson.getDosis();
//				value = coZugangPerson.getBemerkung();
//				if (value != null)
//				{
//					bemerkung += ", " + value;
//				}
//				
//				setInteger(dosis, 9 + tag, 1);
//				if (bemerkung.length() > 0)
//				{
//					if (bemerkung.startsWith(", "))
//					{
//						bemerkung = bemerkung.substring(2);
//					}
//					setTextCenter(bemerkung, 9 + tag, 5);
//				}
//
//			} while (coZugangPerson.moveNext());
//		}
//
////		setTextLeft(coPerson.getName(), 7, 1);
//		saveFile();
//	}


	/**
	 * Text in die Datei einfügen
	 * 
	 * @param value Text
	 * @param iRow Zeile
	 * @param iCol Spalte
	 */
//	private void setTextLeft(String value, int iRow, int iCol) {
//		HSSFRow row;
//		HSSFCell cell;
//		HSSFRichTextString text;
//		HSSFFont font;
//		HSSFCellStyle style;
//
//		text = new HSSFRichTextString(value);
//		row = m_sheet.getRow(iRow);
//		cell = row.createCell(iCol);
//		
//		font = m_wb.createFont();
//		font.setFontName(HSSFFont.FONT_ARIAL);
//		font.setFontHeightInPoints((short) 11);
//
//		style = m_wb.createCellStyle();
//		style.setFont(font);
//		style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
//		style.setAlignment(HSSFCellStyle.ALIGN_LEFT);
//		
//		cell.setCellStyle(style);
//		cell.setCellValue(text);
//	}	


	/**
	 * Text in die Datei einfügen, mittig zentriert
	 * 
	 * @param value Text
	 * @param iRow Zeile
	 * @param iCol Spalte
	 */
//	private void setTextCenter(String value, int iRow, int iCol) {
//		HSSFRow row;
//		HSSFCell cell;
//		HSSFRichTextString text;
//		HSSFFont font;
//		HSSFCellStyle style;
//
//		text = new HSSFRichTextString(value);
//		row = m_sheet.getRow(iRow);
//		cell = row.createCell(iCol);
//		
//		font = m_wb.createFont();
//		font.setFontName(HSSFFont.FONT_ARIAL);
//		font.setFontHeightInPoints((short) 11);
//
//		style = createBorderedStyle(m_wb);
//		style.setFont(font);
//		style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
//		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//		
//		cell.setCellStyle(style);
//		cell.setCellValue(text);
//	}	


	/**
	 * Integer in die Datei einfügen
	 * 
	 * @param value Text
	 * @param iRow Zeile
	 * @param iCol Spalte
	 */
//	private void setInteger(int value, int iRow, int iCol) {
//		HSSFRow row;
//		HSSFCell cell;
//		HSSFFont font;
//		HSSFCellStyle style;
//		
//		row = m_sheet.getRow(iRow);
//		cell = row.createCell(iCol);
//		
//		font = m_wb.createFont();
//		font.setFontName(HSSFFont.FONT_ARIAL);
//		font.setFontHeightInPoints((short) 11);
//		
//		style = createBorderedStyle(m_wb);
//		style.setFont(font);
//		style.setVerticalAlignment(HSSFCellStyle.ALIGN_CENTER);
//		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//		
//		cell.setCellStyle(style);
//		cell.setCellValue(value);
//	}	

}
