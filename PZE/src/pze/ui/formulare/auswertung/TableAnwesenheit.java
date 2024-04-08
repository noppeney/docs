package pze.ui.formulare.auswertung;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.resources.Font;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.auswertung.CoAnwesenheitAlle;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;


/**
 * Klasse für die Tabelle der Anwesenheitsübersicht
 * 
 * @author Lisiecki
 */
public class TableAnwesenheit extends SortedTableControl {

	protected static Font m_fontOfa;
	static int i=0;

	protected AbstractCacheObject m_coAnwesenheit;
	protected CoPerson m_coPersonOfaWti;
	
	protected Map<String, String> m_mapCellColor;
	protected Map<String, Boolean> m_mapCellOfa;

	
	/**
	 * Konstruktor
	 * 
	 * @param tableControl
	 * @throws Exception
	 */
	public TableAnwesenheit(IControl tableControl) throws Exception {
		super(tableControl);
	}


	@Override
	public void tableSelected(IControl arg0, Object arg1){
	}

	/**
	 *  Person öffnen
	 *  
	 * @see pze.ui.controls.SortedTableControl#tableDefaultSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
	 */
	@Override
	public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
		int personID;
		FormPerson formPerson;
		
		personID = getSelectedPersonID();
		if (personID == 0)
		{
			return;
		}
		
		formPerson = FormPerson.open(getSession(), null, personID);
		if (formPerson != null)
		{
			formPerson.showZeiterfassung(new Date());
		}
	}


	/**
	 * Sortieren für diese Tabelle nicht möglich
	 */
	@Override
	protected void sort() throws Exception {
	}


	/**
	 * Farbe setzen
	 * 
	 * @see pze.ui.controls.SortedTableControl#renderCell(framework.ui.interfaces.spread.ISpreadCell)
	 */
	@Override
	protected void renderCell(ISpreadCell cell) throws Exception {
//		System.out.println("render: " + i++);
		if (isCellEmpty(cell))
		{
			return;
		}
		
	
		renderCell2(cell);
	}
	

	/**
	 * ID der in der Tabelle ausgewählten Person herausfinden
	 * 
	 * @return
	 */
	protected int getSelectedPersonID() {
		int row;
		int col;
		String resID;
		IField field;
		
		field = m_table.getSelectedCell().getField();
		if (field == null)
		{
			return 0;
		}
		
		resID = field.getFieldDescription().getResID();
		m_coAnwesenheit.moveTo(m_table.getSelectedBookmark());
		
		row = m_coAnwesenheit.getCurrentRowIndex();
		col = Format.getIntValue(resID.substring(resID.length() - 1));
		
		return ((CoAnwesenheitAlle) m_coAnwesenheit).getPersonID(row, col);
	}
	

	public void setData(IBusinessObject data, CoPerson coPersonOfaWti) throws Exception {
		m_coAnwesenheit = (AbstractCacheObject) data;
		m_coPersonOfaWti = coPersonOfaWti;
		
		m_table.setData(data);
		
		m_mapCellColor = new HashMap<String, String>();
		m_mapCellOfa = new HashMap<String, Boolean>();
	}


	/** 
	 * Farbe für die Zelle festlegen
	 * 
	 * @param cell
	 * @param buchungsartID
	 * @param personID
	 * @param m_coPersonOfaWti 
	 * @throws Exception
	 */
	protected void renderCell2(ISpreadCell cell) throws Exception {
		int row, col, buchungsartID, personID;
		String resID, key, color;

		resID = cell.getColumnDescription().getResID();

		// aktuell markierte Zelle bestimmen
		row = m_coAnwesenheit.getCurrentRowIndex();
		col = Format.getIntValue(resID.substring(resID.length() - 1));
		
		// Schlüssel für Zelle erstellen, cell.equals funktioniert hier nicht
		key = row + "-" + col;

		// prüfen ob die Farbe schon bestimmt wurde
		if (!m_mapCellColor.containsKey(key))
		{
			// Farben anhand der Buchungsart bestimmen
			personID = ((CoAnwesenheitAlle) m_coAnwesenheit).getPersonID(row, col); // TODO hier ein moveTo machen
			buchungsartID = ((CoAnwesenheitAlle) m_coAnwesenheit).getBuchungsartID(row, col);

			// wenn für den Tag keine Buchung existiert
			if (buchungsartID == 0)
			{
				// prüfe ob die nächste und die letzte z. B. Krankbuchungen sind -> wenn ja, markiere die Person als krank
				buchungsartID = CoBuchung.checkBuchungsart(personID);
//				System.out.println("renderDB: " + i++);
			}

			// Farbe bestimmen
			color = CoBuchungsart.getInstance().getColorBuchungsart(buchungsartID);
			m_mapCellColor.put(key, color);
			
			// OFA prüfen
			if (color != null)
			{
				m_mapCellOfa.put(key, (buchungsartID == CoBuchungsart.ID_ORTSFLEX_ARBEITEN || CoBuchungsart.isUnterbrechung(buchungsartID))
					&& m_coPersonOfaWti.moveToID(personID) && m_coPersonOfaWti.getBuchungsartID() == CoBuchungsart.ID_ORTSFLEX_ARBEITEN);
			}
		}
		
		
		// Zelle rendern
		color = m_mapCellColor.get(key);
		if (color != null)
		{
			// Hintergrundfarbe setzen
			cell.setBackColor(color);

			// bei ortsflexiblem Arbeiten und den Unterbrechungen im OFA werden die Zellen besonders markiert
			if (m_mapCellOfa.get(key))
			{
				renderCellOrtsflexibel(cell);
			}
		}
	}


//	/** 
//	 * Farbe für die Zelle festlegen
//	 * 
//	 * @param cell
//	 * @param buchungsartID
//	 * @param personID
//	 * @param m_coPersonOfaWti 
//	 * @throws Exception
//	 */
//	public static void renderCell(ISpreadCell cell, int buchungsartID, int statusID, int personID, CoPerson m_coPersonOfaWti) throws Exception {
//		String color;
//
//		// wenn für den Tag keine Buchung existiert
//		if (buchungsartID == 0)
//		{
//			// prüfe ob die nächste und die letzte z. B. Krankbuchungen sind -> wenn ja, markiere die Person als krank
//			buchungsartID = CoBuchung.checkBuchungsart(personID);
////			System.out.println("renderDB: " + i++);
//		}
//
//		color = CoBuchungsart.getInstance().getColorBuchungsart(buchungsartID);
//		if (color != null)
//		{
//			// Hintergrundfarbe setzen
//			cell.setBackColor(color);
//			
//			// bei ortsflexiblem Arbeiten und den Unterbrechungen im OFA werden die Zellen besonders markiert
//			if ((buchungsartID == CoBuchungsart.ID_ORTSFLEX_ARBEITEN || CoBuchungsart.isUnterbrechung(buchungsartID))
//					&& m_coPersonOfaWti.moveToID(personID) && m_coPersonOfaWti.getBuchungsartID() == CoBuchungsart.ID_ORTSFLEX_ARBEITEN)
//			{
//				renderCellOrtsflexibel(cell);
//			}
//		}
//	}

	
	/**
	 * Aussehen der Zellen bei ortsflexiblem Arbeiten festlegen
	 * 
	 * @param cell
	 * @throws Exception
	 */
	public static void renderCellOrtsflexibel(ISpreadCell cell) throws Exception {
		
		// Schrift und Rahmenfarbe weiß
		cell.setForeColor("##FFFFFF"); // weiss
		cell.setFrameColor("##FFFFFF");

		renderCellFettKursiv(cell);
	}

	
	/**
	 * Aussehen der Zellen bei ortsflexiblem Arbeiten festlegen
	 * 
	 * @param cell
	 * @throws Exception
	 */
	public static void renderCellFettKursiv(ISpreadCell cell) throws Exception {
		
		// kursive/fette Schrift
		if (m_fontOfa == null)
		{
			m_fontOfa = new Font(cell.getFont().getName(), cell.getFont().getSize());
			m_fontOfa.setItalic(true);
			m_fontOfa.setBold(true);
		}
		
		// Schrift setzen
		cell.setFont(m_fontOfa);
	}


}
