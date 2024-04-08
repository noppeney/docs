package pze.ui.formulare.auswertung;

import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;
import pze.business.objects.auswertung.CoAnwesenheitAbteilung;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.reftables.buchungen.CoBuchungsart;


/**
 * Klasse für die Tabelle der Anwesenheitsübersicht pro Abteilung
 * 
 * @author Lisiecki
 *
 */
public class TableAnwesenheitAbteilung extends TableAnwesenheit {

	
	/**
	 * Konstruktor
	 * 
	 * @param tableControl
	 * @throws Exception
	 */
	public TableAnwesenheitAbteilung(IControl tableControl) throws Exception {
		super(tableControl);
	}


	/**
	 * ID der in der Tabelle ausgewählten Person herausfinden
	 * 
	 * @return
	 */
	@Override
	protected int getSelectedPersonID() {
		int row, col;
		String resID;
		
		resID = getSelectedCell().getField().getFieldDescription().getResID();
		
		row = m_coAnwesenheit.getCurrentRowIndex();
		col = Format.getIntValue(resID.substring(resID.length() - 1));
		
		return ((CoAnwesenheitAbteilung)m_coAnwesenheit).getPersonID(row, col);
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
	@Override
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
			personID = ((CoAnwesenheitAbteilung)m_coAnwesenheit).getPersonID(row, col);
			buchungsartID = ((CoAnwesenheitAbteilung)m_coAnwesenheit).getBuchungsartID(row, col);

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

}
