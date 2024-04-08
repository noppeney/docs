package pze.ui.formulare.projektverwaltung;

import java.util.Date;

import framework.business.interfaces.fields.IField;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;
import pze.business.objects.projektverwaltung.CoProjektverfolgung;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjektverfolgung;
import pze.ui.controls.DateControlPze;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.auswertung.FormAmpelliste;


/**
 * Tabelle für die Projektverfolgung
 * 
 * 
 * @author lisiecki
 */
public class TableProjektProjektverfolgung extends SortedTableControl{

	private CoProjektverfolgung m_coProjektverfolgung;
	private FormProjektProjektverfolgung m_formProjektProjektverfolgung;
	 

	/**
	 * Konstruktor
	 * 
	 * @param tableControl
	 * @param formProjektProjektverfolgung 
	 * @param m_coProjektverfolgung
	 * @throws Exception
	 */
	public TableProjektProjektverfolgung(IControl tableControl, CoProjektverfolgung coProjektverfolgung, FormProjektProjektverfolgung formProjektProjektverfolgung) throws Exception {
		super(tableControl);
		
		m_formProjektProjektverfolgung = formProjektProjektverfolgung;
		m_coProjektverfolgung = coProjektverfolgung;
		setData(m_coProjektverfolgung);
		
//		enableColumns();
		
		// sortieren ist in dieser Tabelle nicht erlaubt, da Chronologie wichtig ist und nur der letzte Eintrag bearbeitet werden darf
		disableSort();
	}


	/**
	 * Spalten in Abhängigkeit vom Modus aktivieren
	 */
	public void enableColumns() {
		boolean isModusAL;
		
		isModusAL = m_formProjektProjektverfolgung.isModusAL();
		// Felder für AL oder PL aktivieren
//		if ()
		{
			enableColumn(m_coProjektverfolgung.getFieldTerminPL().getFieldDescription().getResID(), !isModusAL);
			enableColumn(m_coProjektverfolgung.getFieldKostenPL().getFieldDescription().getResID(), !isModusAL);
			enableColumn(m_coProjektverfolgung.getFieldStatusAenderungIDPL().getFieldDescription().getResID(), !isModusAL);
			enableColumn(m_coProjektverfolgung.getFieldBemerkungPL().getFieldDescription().getResID(), !isModusAL);
		}
//		else
		{
			enableColumn(m_coProjektverfolgung.getFieldTerminAL().getFieldDescription().getResID(), isModusAL);
			enableColumn(m_coProjektverfolgung.getFieldKostenAL().getFieldDescription().getResID(), isModusAL);
			enableColumn(m_coProjektverfolgung.getFieldStatusAenderungIDAL().getFieldDescription().getResID(), isModusAL);
			enableColumn(m_coProjektverfolgung.getFieldBemerkungAL().getFieldDescription().getResID(), isModusAL);
		}
	}

	
	/**
	 * nur die letzte Zeile darf bearbeitet werden
	 */
	@Override
	public boolean mayEdit(Object bookmark, ISpreadCell cell) throws Exception {
		int currentRowIndex;
		
		// aktuell markierte Zelle bestimmen
		currentRowIndex = m_coProjektverfolgung.getCurrentRowIndex();
		
		// nur die letzte Zeile darf bearbeitet werden
		return m_coProjektverfolgung.isModified() && currentRowIndex == m_coProjektverfolgung.getRowCount()-1;
	}
	
	
	@Override
	public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
//		FormAbruf.open(getSession(), null, m_coMitarbeiterProjekt.getID());
	}
	

	/**
	 * Farbe als Ampelliste anpassen
	 */
	protected void renderCell(ISpreadCell cell) throws Exception {
		super.renderCell(cell);
		
		String color;
		
		// Header hat keine Farbe
		if (cell.getField() == null)
		{
			return;
		}
		
		// Farbe bestimmen und speichern
		color = getColor(cell);
		if (color != null)
		{
			cell.setBackColor(color);
		}
	}


	/**
	 * Farbe für die Zelle bestimmen
	 * 
	 * @param cell
	 * @return
	 */
	private String getColor(ISpreadCell cell) {
		int statusID;
		double verbrauch;
		String color;
		String resID, fieldBezeichnung;
		Date datum;
		IField field;
		
		field = cell.getField();
		resID = field.getFieldDescription().getResID();
		fieldBezeichnung = resID.substring(resID.lastIndexOf(".")+1);
		color = null;
		
		// Felder für PL-Angaben
		if (resID.endsWith("pl") || fieldBezeichnung.startsWith("pl"))
		{
			statusID = m_coProjektverfolgung.getStatusIDPL();
			color = getColor(statusID);
		}
		else if (resID.endsWith("al") || fieldBezeichnung.startsWith("al"))	// Felder für AL-Angaben
		{
			statusID = m_coProjektverfolgung.getStatusIDAL();
			color = getColor(statusID);
		}
		else // aktueller Projektstand
		{
			verbrauch = m_coProjektverfolgung.getVerbrauch();

			if (verbrauch < 0.6)
			{
				color = FormAmpelliste.COLOR_GRUEN;
			}
			else if (verbrauch < 0.8)
			{
				color = FormAmpelliste.COLOR_GELB;
			}
			else
			{
				color = FormAmpelliste.COLOR_ROT;
			}
			
			// Lieferdatum nochmal separat abfragen
			if (field.equals(m_coProjektverfolgung.getFieldTermin()))
			{
				// wenn das Lieferdatum überschritten ist, wird es rot angezeigt
				datum = m_coProjektverfolgung.getTermin();
				if (datum != null && datum.before(Format.getDate0Uhr(new Date())))
				{
					color = FormAmpelliste.COLOR_ROT;
				}
			}
			
			// Aktion ohne Farbe
			if (field.equals(m_coProjektverfolgung.getFieldAktionID()))
			{
				color = null;
			}
		}
		
		
		return color;
	}


	/**
	 * Farbe für die Zelle in Abhängigkeit vom Status bestimmen
	 * 
	 * @param statusID
	 * @return
	 */
	private String getColor(int statusID) {
		if (statusID == CoStatusProjektverfolgung.STATUSID_OK)
		{
			return FormAmpelliste.COLOR_GRUEN;
		}
		else if (statusID == CoStatusProjektverfolgung.STATUSID_GEAENDERT)
		{
			return FormAmpelliste.COLOR_GELB;
		}
		
		return null;
	}


	// TODO ähnlich zu SortedTableControl (Monatseinsatzblatt, Zuordnung MA-Zeitmodell), sollte man nochmal bei Gelegenheit vergleichen
	@Override
	protected boolean beginEditing(Object bookmark, ISpreadCell cell) throws Exception {
		// Editor für Date-Fields anpassen
		if (isDateField(cell.getField()))
		{
			// Inhalt der Zelle merken und nach Änderung der Fielddescription wieder zurücksetzen
			String stringValue = cell.getField().getDisplayValue();

			setDateFieldescriptionToText(cell);
			
			cell.setText(stringValue);
			cell.getField().setValue(stringValue);
		}

		else if (isZeitField(cell.getField()))
		{
			int i = Format.getIntValue(cell.getField().getValue());
			// angezeigt wird die Zeit als Text
			cell.setText(Format.getZeitAsText(i));
			
			// der Wert ist die Zeit als Integer
			cell.getField().setValue(Format.getZeitAsText(i));
		}
		
		return mayEdit(bookmark, cell);
		
	}
	
	
	/**
	 * Aktionen nach Abschluss der Bearbeitung
	 */
	@Override
	public void endEditing(Object bookmark, IField fld) throws Exception {
		String text;
		if (isZeitField(fld))
		{
			Integer zeit;
			String zeitString;
			
			// Zeit bestimmen
			zeitString = fld.getDisplayValue();
			if (zeitString.isEmpty()) // keine Zeit eingetragen bzw. Zeit gelöscht
			{
				fld.setValue(null);
				m_table.getSelectedCell().setText(null);
				return;
			}
			zeit = Format.getZeitAsInt(zeitString);
			
//			fld.setValue(Format.getZeitAsText(zeit));
			fld.setValue(zeit);
			m_table.getSelectedCell().setText(Format.getZeitAsText(zeit));
		}
		else if (isDateField(fld)) // das Datum kann in verschiedenen Formaten eingegeben werden
		{
			// Datum formatieren
			text = fld.getDisplayValue();
			if (!text.isEmpty())
			{
				text = DateControlPze.formatDatum(fld.getDisplayValue()); // falls keine Punkte eingetragen wurden
				text = Format.getDateText(text); // Jahr vierstellig
			}
			
			setDateFieldescriptionToDate();

			// Datum in die Tabelle und das Feld setzen (mit Uhrzeit um es DB-konform zu machen)
			m_table.getSelectedCell().setText(text);
			if (!text.isEmpty())
			{
				fld.setValue(Format.getDate12Uhr(Format.getDateValue(text)));
			}
			else
			{
				fld.setValue(null);
			}
		}
		
		// Status OK/geändert prüfen
		updateStatusField(fld);
	}


	/**
	 * Status OK/geändert prüfen
	 * 
	 * @param fld
	 * @throws Exception
	 */
	private void updateStatusField(IField fld) throws Exception {
		if (fld.equals(m_coProjektverfolgung.getFieldKostenAL()) || fld.equals(m_coProjektverfolgung.getFieldTerminAL()))
		{
			m_coProjektverfolgung.updateStatusIDAL();
		}
		else if (fld.equals(m_coProjektverfolgung.getFieldKostenPL()) || fld.equals(m_coProjektverfolgung.getFieldTerminPL()))
		{
			m_coProjektverfolgung.updateStatusIDPL();
		}
	}

}
