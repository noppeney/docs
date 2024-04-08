package pze.ui.formulare.person.monatseinsatzblatt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import framework.business.fields.HeaderDescription;
import framework.business.interfaces.CaptionType;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.resources.Font;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattAnzeige;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattPhasen;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattProjekt;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;
import pze.ui.formulare.projektverwaltung.FormAbruf;
import pze.ui.formulare.projektverwaltung.FormAuftrag;

/**
 * Klasse für die Tabelle des Monatseinsatzblattes
 * 
 * @author Lisiecki
 */
public class TableMonatseinsatzblatt extends SortedTableControl {

	private static final String COLOR_HEADER = "##99CCCC";
	public static final String COLOR_ARBEITSFREI = "##DDDDDD";
//	private static final String COLOR_EINTRAG_GUELTIG = "##00FF00";
	private static final String COLOR_EINTRAG_GUELTIG = "##88FF88";
//	private static final String COLOR_EINTRAG_UNGUELTIG = "##FF0000";
	private static final String COLOR_EINTRAG_UNGUELTIG = "##FF6666";
	private static final String COLOR_TAETIGKEIT_FEHLT = "##FFFF00";

	public static final int TOLERANZ_MINUTEN = 30;
	public static final int TOLERANZ_MONAT_POSITIV = 30;
	public static final int TOLERANZ_MONAT_NEGATIV = -15;

	private CoMonatseinsatzblattAnzeige m_coMonatseinsatzblattAnzeige;

	private FormPersonMonatseinsatzblatt m_formPersonMonatseinsatzblatt;
	
	private CoPerson m_coPerson;
	
	/**
	 * Dummy-CO, dass gesetzt wird wenn für die aktuell ausgewählte Zelle keine Daten existieren
	 */
	private VirtCoProjekt m_virtCoProjektDummy;

	/**
	 * Dummy-CO, dass gesetzt wird wenn für die aktuell ausgewählte Zelle keine Daten existieren
	 */
	private CoMonatseinsatzblatt m_coMonatseinsatzblattDummy;

	/**
	 * Dummy-CO, dass gesetzt wird wenn für die aktuell ausgewählte Zelle keine Daten existieren
	 */
	private CoMonatseinsatzblattPhasen m_coMonatseinsatzblattPhasenDummy;

	/**
	 * für jeden Tag speichern, ob es ein Arbeitstag ist
	 */
	private Map<Date, Boolean> m_mapArbeitstage;

	/**
	 * Color speichern um sie nicht immer neu zu laden
	 */
	private Map<String, String> m_mapCellColor;

	/**
	 * Tag des Monats der aktuell selektierten Zelle wegen Aktualisierungen der Oberfläche zwischenspeichern
	 */
	private int m_selectedTagDesMonats;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param tableControl
	 * @param coMonatseinsatzblattAnzeige
	 * @param formPersonMonatseinsatzblatt
	 * @throws Exception
	 */
	public TableMonatseinsatzblatt(IControl tableControl, CoMonatseinsatzblattAnzeige coMonatseinsatzblattAnzeige, 
			FormPersonMonatseinsatzblatt formPersonMonatseinsatzblatt) throws Exception{
		super(tableControl);
		
		setCoMonatseinsatzblattAnzeige(coMonatseinsatzblattAnzeige);
		m_formPersonMonatseinsatzblatt = formPersonMonatseinsatzblatt;
		
		m_coPerson = new CoPerson();
		m_coPerson.loadByID(m_coMonatseinsatzblattAnzeige.getPersonID());
		
		m_mapArbeitstage = new HashMap<Date, Boolean>();
		m_mapCellColor = new HashMap<String, String>();

		// Daten des Formulars initialisieren
		initCoDummies();
		m_formPersonMonatseinsatzblatt.setData(m_virtCoProjektDummy);
		m_formPersonMonatseinsatzblatt.setData(m_coMonatseinsatzblattDummy);
		m_formPersonMonatseinsatzblatt.setDataArbeitsplan(m_coMonatseinsatzblattPhasenDummy);

		// Daten der Tabelle initialisieren
		setData(m_coMonatseinsatzblattAnzeige);
		
		// Formatierung
		formatTable();
	}


	/**
	 * Dummy-Datensätz2 anlegen
	 * 
	 * @throws Exception
	 */
	private void initCoDummies() throws Exception {
		m_virtCoProjektDummy = new VirtCoProjekt();
		m_virtCoProjektDummy.begin();
		m_virtCoProjektDummy.add();
		m_virtCoProjektDummy.commit();
		m_virtCoProjektDummy.setModified(false);

		m_coMonatseinsatzblattDummy = new CoMonatseinsatzblatt();
		m_coMonatseinsatzblattDummy.begin();
		m_coMonatseinsatzblattDummy.add();
		m_coMonatseinsatzblattDummy.setWertZeit(0);
		m_coMonatseinsatzblattDummy.commit();
		m_coMonatseinsatzblattDummy.setModified(false);
		
		m_coMonatseinsatzblattPhasenDummy = new CoMonatseinsatzblattPhasen();
	}


	/**
	 * Tabelle formatieren.<br>
	 * Spaltenbreite, Alignment...
	 * 
	 */
	private void formatTable() {
		int iCol, anzCols;
		IHeaderDescription headerDescription;
		IColumnDescription columnDescription;
		
		
		headerDescription = getHeaderDescription();
		anzCols = getHeaderDescription().getColumnCount();
		
		// alle Spalten mit AlignCenter
		for (iCol=0; iCol<anzCols; ++iCol)
		{
			columnDescription = headerDescription.getColumnDescription(iCol);

			columnDescription.setAlignment(CaptionType.ALIGNCENTER);

			// Spalten etwas breiter, wenn eine BerichtsNr eingetragen ist
			columnDescription.setWidth(iCol > 1 && getVirtCoProjekt(iCol-2).getBerichtsNrID() > 0 ? 120 : 80); // 80
		}
		
		// Beschriftungs-Spalte breiter
		columnDescription = headerDescription.getColumnDescription(0);
		columnDescription.setWidth(110);

		// Änderungen anwenden
		setHeaderDescription(headerDescription);
		
		// 1. Spalte fixiert
		setFixedCols(m_coMonatseinsatzblattAnzeige.getStartindexProjektspalten());
	}
	

	/**
	 * Prüft, ob die Zelle bearbeitet werden darf
	 * 
	 * @param bookmark
	 * @param cell
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean mayEdit(Object bookmark, ISpreadCell cell) throws Exception {
		int currentRowIndex, projektFieldIndex, arbeitszeit;
		
		// aktuell markierte Zelle bestimmen
		currentRowIndex = getCurrentRowIndex(bookmark);
		
		// Projektzeilen dürfen nicht bearbeitet werden
		projektFieldIndex = m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(cell.getColumnDescription().getResID());
		if (currentRowIndex < m_coMonatseinsatzblattAnzeige.getAnzProjektzeilen())
		{
			// Bemerkungsfelder der Projekte sind eine Ausnahme
			return isBemerkungsfeld(projektFieldIndex);
		}
		
		// Summenzeile darf nicht bearbeitet werden
		if (isSummenzeile(currentRowIndex))
		{
			return false;
		}
		
		// nur Projektspalten dürfen bearbeitet werden
		if (projektFieldIndex < 0)
		{
			return false;
		}
		
		// nur an Arbeitstagen bearbeiten
		if (!isArbeitstag())
		{
			return false;
		}
		
		
		// Personalabteilung darf für alle eintragen, außer für sich selbst, auch bei krank oder Urlaub
		if (UserInformation.getInstance().isPersonalverwaltung() && !UserInformation.isPerson(m_coMonatseinsatzblattAnzeige.getPersonID()))
		{
			return true;
		}
		
		// AL dürfen nur bis zum geprüften Datum bearbeiten
		if (UserInformation.getInstance().isAL() && m_coMonatseinsatzblattAnzeige.getDatum().before(CoFirmenparameter.getInstance().getDatumMonatseinsatzblatt()))
		{
			return false;
		}

		// an Urlaub und Krankheitstagen darf nicht eingetragen werden
		arbeitszeit = m_coMonatseinsatzblattAnzeige.getArbeitszeit();
		// TODO Ausnahme für einzelne Person beim Arbeiten trotz Krank 
//		if (m_coPerson.getID() == 46 && m_coMonatseinsatzblattAnzeige.getWertKrank() > 0)
//		{
//			
//		}
//		else 
			if (arbeitszeit > 0 && m_coMonatseinsatzblattAnzeige.getWertKrank() + m_coMonatseinsatzblattAnzeige.getWertUrlaub() == arbeitszeit)
		{
			return false;
		}
		
		// FA, Krank ohne Lfz.
		if (m_coMonatseinsatzblattAnzeige.getDisplayValueKrank().equals(CoMonatseinsatzblattAnzeige.EINTRAG_KRANK_OHNE_LFZ)
				|| m_coMonatseinsatzblattAnzeige.getDisplayValueUrlaub().equals(CoMonatseinsatzblattAnzeige.EINTRAG_FA)
				|| m_coMonatseinsatzblattAnzeige.getDisplayValueUrlaub().equals(CoMonatseinsatzblattAnzeige.EINTRAG_FREIGESTELLT))
		{
			return false;
		}
		
		return true;
	}


	/**
	 * Feld ist Bemerkungsfeld eines Projektes
	 * 
	 * @param projektFieldIndex
	 * @return
	 */
	public boolean isBemerkungsfeld(int projektFieldIndex){
		if (projektFieldIndex < 0 
				|| !m_coMonatseinsatzblattAnzeige.getFieldTagDesMonats().getValue().equals(
						m_coMonatseinsatzblattAnzeige.getVirtCoProjekt().getCaptionBemerkung()))
		{
			return false;
		}
		
		return true;
	}
	
	
	public void setCoMonatseinsatzblattAnzeige(CoMonatseinsatzblattAnzeige coMonatseinsatzblattAnzeige) throws Exception {
		m_coMonatseinsatzblattAnzeige = coMonatseinsatzblattAnzeige;
		setData(m_coMonatseinsatzblattAnzeige);
		setHeaderDescription(new HeaderDescription(m_coMonatseinsatzblattAnzeige.getFields()));
	}


	/**
	 * Zeile des Bookmarks
	 * 
	 * @param bookmark
	 * @return
	 */
	private int getCurrentRowIndex(Object bookmark) {
		m_coMonatseinsatzblattAnzeige.moveTo(bookmark);
		return m_coMonatseinsatzblattAnzeige.getCurrentRowIndex();
	}


	/**
	 * aktuell markierte Zeile
	 * 
	 * @return
	 */
	private int getCurrentRowIndex() {
		return m_coMonatseinsatzblattAnzeige.getCurrentRowIndex();
	}


	/**
	 * Zuletzt ausgewählter Tag des Monats (als Integer gespeichert)
	 * 
	 * @return
	 */
	public int getSelectedTagDesMonats(){
		return m_selectedTagDesMonats;
	}
	
	
	/**
	 * Hintergrund anpassen für die Zellen, die nicht bearbeitet werden dürfen
	 * 
	 * @throws Exception 
	 * @see pze.ui.controls.SortedTableControl#renderCell(framework.ui.interfaces.spread.ISpreadCell)
	 */
	@Override
	protected void renderCell(ISpreadCell cell) throws Exception {
		int currentRowIndex;
		String key, color;
		Object bookmark;
		Font font;
		CoMonatseinsatzblatt coMonatseinsatzblatt;

		if (cell.getField() == null)
		{
			return;
		}

		// aktuell markierte Zelle bestimmen
		currentRowIndex = getCurrentRowIndex();
		key = currentRowIndex + "-" + cell.getField().getFieldDescription();
		
		// Bookmark merken, um in nach dem rendern wieder zurückzusetzen
		coMonatseinsatzblatt = m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt();
		bookmark = coMonatseinsatzblatt.getBookmark();

		// Farbe bestimmen, wenn noch nicht vorhanden
		// Tabellenfarben siehe framework.cui.multispread.BaseMultiSpread
		if (!m_mapCellColor.containsKey(key))
		{
			m_mapCellColor.put(key, getColor(cell));
		}

		// Farbe speichern
		color = m_mapCellColor.get(key);
		if (color != null)
		{
			cell.setBackColor(color);
		}

		// Header mit fetter Schrift
		font = cell.getFont();
		font.setBold(currentRowIndex < m_coMonatseinsatzblattAnzeige.getAnzProjektzeilen());
		
		// Bookmark zurücksetzen
		coMonatseinsatzblatt.moveTo(bookmark);

		// Um Status des Projekts anzuzeigen, muss der Stand separat geladen werden, da in virtCoProjekt nur die Stunden des Benutzers stehen
		// -> neues CO, prüfen wann es neu geladen werden kann/muss; beim öffnen/speichern, ggf. bei Focus oder es ist nur beim öffnen/speichern aktuell
		// einfacher wäre nur den Status des Projektes zu laden, dann rot wenn nicht mehr aktiv; das ginge in virtCoProjekt
		// außerdem ist bei 80% die Frage wieviel STunden noch drauf sind; Könnte auch zur Reservierung von Stunden führen
		
	}


	/**
	 * Farbe für die übergebene Zelle bestimmen
	 * Tabellenfarben siehe framework.cui.multispread.BaseMultiSpread
	 * 
	 * @param cell
	 * @return
	 * @throws Exception
	 */
	private String getColor(ISpreadCell cell) throws Exception {
		int currentRowIndex, arbeitszeit;
		int tagDesMonats;
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		VirtCoProjekt virtCoProjekt;
		
		
		// aktuell markierte Zelle bestimmen
		currentRowIndex = getCurrentRowIndex();

		// Header je nach Status des Projektes einfärben
		if (currentRowIndex < m_coMonatseinsatzblattAnzeige.getAnzProjektzeilen())
		{
			// Projektdaten
			virtCoProjekt = getVirtCoProjekt(cell);
			
			// laufende Projekte mit normaler Headerfarbe
			if (virtCoProjekt == null || virtCoProjekt.getStatusID() == CoStatusProjekt.STATUSID_LAUFEND)
			{
				return COLOR_HEADER;
			}
			else // alle anderen rot
			{
				return COLOR_EINTRAG_UNGUELTIG;
			}
		}

		
		// Summenzeile
		if (isSummenzeile(currentRowIndex))
		{
			if (m_coMonatseinsatzblattAnzeige.checkSumme())
			{
				return COLOR_EINTRAG_GUELTIG;
			}
			else
			{
				return COLOR_EINTRAG_UNGUELTIG;
			}
		}
		

		// Projektstunden
		m_coMonatseinsatzblattAnzeige.moveTo(currentRowIndex);
		arbeitszeit = m_coMonatseinsatzblattAnzeige.getArbeitszeit();

		if (!isArbeitstag())
		{
			return COLOR_ARBEITSFREI;
		}
		else if (mayEdit(m_coMonatseinsatzblattAnzeige.getBookmark(), cell))
		{
			// prüfen, ob es ein Projektfield ist
			tagDesMonats = m_coMonatseinsatzblattAnzeige.getTagDesMonats();
			
			// Projektdaten
			virtCoProjekt = getVirtCoProjekt(cell);
	
			// Daten Monatseinsatzblatt
			coMonatseinsatzblatt = m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt();
			// beim 1. Öffnen des Monats sind noch keine Daten vorhanden
			if (coMonatseinsatzblatt.getRowCount() == 0 && arbeitszeit > 0)
			{
				return COLOR_EINTRAG_UNGUELTIG;
			}
			
			if (virtCoProjekt == null || !coMonatseinsatzblatt.moveTo(virtCoProjekt, tagDesMonats))
			{
				return null;
			}
				
//			m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt().moveTo(m_coMonatseinsatzblattAnzeige.getVirtCoProjekt(), m_coMonatseinsatzblattAnzeige.getTagDesMonats());
//			System.out.println(m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt().getWertZeit());
//			System.out.println(m_formPersonMonatseinsatzblatt.getTaetigkeit() + " - " + m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt().getTaetigkeit());
//			System.out.println(m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(cell.getField()));
			if (coMonatseinsatzblatt.getWertZeit() > 0 && coMonatseinsatzblatt.getTaetigkeit() == null)
			{
				return COLOR_TAETIGKEIT_FEHLT;
			}
			else if (arbeitszeit == 0) // noch keine Arbeitszeit für die Zukunft
			{
				
			}
			// ganztägig krank oder Urlaub
			else if (m_coMonatseinsatzblattAnzeige.getWertKrank() + m_coMonatseinsatzblattAnzeige.getWertUrlaub() == arbeitszeit)
			{
				
			}
			// teilweise krank an einem Tag
			else if (Math.abs(arbeitszeit - (m_coMonatseinsatzblattAnzeige.getSummeStunden() + m_coMonatseinsatzblattAnzeige.getWertKrank())) < TOLERANZ_MINUTEN)
			{
				return COLOR_EINTRAG_GUELTIG;
			}
			else
			{
				return COLOR_EINTRAG_UNGUELTIG;
			}
		}

		return null;
	}


	/**
	 * projekt für die Spalte der übergebenen Zelle laden
	 * 
	 * @param cell
	 * @return
	 */
	private VirtCoProjekt getVirtCoProjekt(ISpreadCell cell) {
		int projektFieldIndex;
		
		projektFieldIndex = m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(cell.getField());
		
		if (projektFieldIndex < 0)
		{
			return null;
		}
		
		return getVirtCoProjekt(projektFieldIndex);
	}


	/**
	 * Projekt für die Spalte des übergebenen Index laden
	 * 
	 * @param projektFieldIndex
	 * @return
	 */
	private VirtCoProjekt getVirtCoProjekt(int projektFieldIndex) {
		VirtCoProjekt virtCoProjekt;

		virtCoProjekt = m_coMonatseinsatzblattAnzeige.getVirtCoProjekt();
		virtCoProjekt.moveTo(projektFieldIndex);
		
		return virtCoProjekt;
	}


	/**
	 * Aktueller Tag ist ein Arbeitstag bzw. die Person hat an dem Tag gearbeitet
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean isArbeitstag() throws Exception {
		Date datum;
		
		// keine Datumszeile
		if (m_coMonatseinsatzblattAnzeige.getTagDesMonats() == 0)
		{
			return false;
		}
		
		// Datum prüfen
		datum = m_coMonatseinsatzblattAnzeige.getDatum();
		if (!m_mapArbeitstage.containsKey(datum))
		{
			m_mapArbeitstage.put(datum, m_coPerson.isArbeitstag(datum) || m_coMonatseinsatzblattAnzeige.getArbeitszeit() > 0);
		}
		
		return Format.getBooleanValue(m_mapArbeitstage.get(datum));
	}


	/**
	 * Übergebene Zeilennummer ist die Summenzeile (letzte Zeile)
	 * 
	 * @param rowIndex
	 * @return
	 */
	private boolean isSummenzeile(int rowIndex) {
		return rowIndex + 1 == m_coMonatseinsatzblattAnzeige.getRowCount();
	}


	/**
	 * Formular für den Tag anpassen
	 * @throws Exception 
	 * 
	 * @see pze.ui.controls.SortedTableControl#tableSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
	 */
	@Override
	public void tableSelected(IControl arg0, Object arg1) {
		super.tableSelected(arg0, arg1);
		
		try
		{
			int tagDesMonats;
			int projektFieldIndex, monatseinsatzblattID;
			CoMonatseinsatzblatt coMonatseinsatzblatt;
			VirtCoProjekt virtCoProjekt;
			CoMonatseinsatzblattPhasen coMonatseinsatzblattPhasen;
			
			
			// prüfen, ob es ein Projektfield ist
//			m_coMonatseinsatzblattAnzeige.moveToTag(getCurrentRowIndex() - m_coMonatseinsatzblattAnzeige.getAnzProjektzeilen());
			projektFieldIndex = m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(getSelectedCell().getField());

			// Tag des Monats wegen Aktualisierungen zwischenspeichern
			tagDesMonats = m_coMonatseinsatzblattAnzeige.getTagDesMonats();
			m_selectedTagDesMonats = tagDesMonats;
			
			if (projektFieldIndex < 0)
			{
				// erst Projektdaten, dann Einsatzdaten setzen (Oberfläche enthält Formularfelder aus beiden CO's)
				m_formPersonMonatseinsatzblatt.setData(m_virtCoProjektDummy);
				m_formPersonMonatseinsatzblatt.setData(m_coMonatseinsatzblattDummy);
				m_formPersonMonatseinsatzblatt.setDataArbeitsplan(m_coMonatseinsatzblattPhasenDummy);
				return;
			}

			// Projektdaten
			virtCoProjekt = getVirtCoProjekt(projektFieldIndex);
			m_formPersonMonatseinsatzblatt.setData(virtCoProjekt);


			// Einsatzdaten
			if (tagDesMonats < 1)
			{
				m_formPersonMonatseinsatzblatt.setData(m_coMonatseinsatzblattDummy);
				m_formPersonMonatseinsatzblatt.setDataArbeitsplan(m_coMonatseinsatzblattPhasenDummy);
			}
			else
			{
				coMonatseinsatzblatt = m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt();
				if (coMonatseinsatzblatt.moveTo(virtCoProjekt, tagDesMonats))
				{
					// erst Projektdaten, dann Einsatzdaten setzen (Oberfläche enthält Formularfelder aus beiden CO's)
					m_formPersonMonatseinsatzblatt.setData(coMonatseinsatzblatt);
					
					// Zuordnung der Stunden zu Projektphasen
					monatseinsatzblattID = coMonatseinsatzblatt.getID();
					if (m_formPersonMonatseinsatzblatt.hasDataArbeitsplan(monatseinsatzblattID))
					{
						m_formPersonMonatseinsatzblatt.setDataArbeitsplan(monatseinsatzblattID);
					}
					else
					{
						coMonatseinsatzblattPhasen = new CoMonatseinsatzblattPhasen();
						coMonatseinsatzblattPhasen.load(monatseinsatzblattID, virtCoProjekt);
						m_formPersonMonatseinsatzblatt.setDataArbeitsplan(coMonatseinsatzblattPhasen);
					}
				}
				else
				{
					m_formPersonMonatseinsatzblatt.setData(m_coMonatseinsatzblattDummy);
					m_formPersonMonatseinsatzblatt.setDataArbeitsplan(m_coMonatseinsatzblattPhasenDummy);
				}
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Beim Doppelklick ggf. das Propjekt oder den Tag öffnen
	 * 
	 * @see pze.ui.controls.SortedTableControl#tableDefaultSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
	 */
	public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
		int id;
		int tagDesMonats;
		int projektFieldIndex;
		IField field;
		VirtCoProjekt virtCoProjekt;


		// prüfen, ob es ein Projektfield ist
		field = getSelectedCell().getField();
		projektFieldIndex = m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(field);

		// Tag des Monats
		tagDesMonats = m_coMonatseinsatzblattAnzeige.getTagDesMonats();

		
		// Projekt bei entsprechender Berechtigung öffnen
		if (tagDesMonats < 1 && projektFieldIndex >= 0 && !isBemerkungsfeld(projektFieldIndex))
		{
			// Projektdaten
			virtCoProjekt = getVirtCoProjekt(projektFieldIndex);

			// Abruf öffnen
			id = virtCoProjekt.getAbrufID();
			if (id > 0)
			{
				FormAbruf.open(getSession(), id);
				return;
			}

			// Auftrag öffnen
			id = virtCoProjekt.getAuftragID();
			if (id > 0)
			{
				FormAuftrag.open(getSession(), id);
			}
		}

		// Buchungen des Tages öffnen
		if (tagDesMonats > 0 && field.equals(m_coMonatseinsatzblattAnzeige.getFieldTagDesMonats()))
		{
			FormPerson.open(getSession(), null, m_coPerson.getID()).showZeiterfassung(Format.getDatum(m_coMonatseinsatzblattAnzeige.getDatum(), tagDesMonats));
		}
	}

	
	/**
	 * Sortieren für diese Tabelle nicht möglich
	 */
	protected void sort() throws Exception {
		
	}
	
	
	/**
	 * Wert in die aktuelle Zelle setzen
	 * 
	 * @param value
	 * @throws Exception
	 */
	public void setValue(Object value) throws Exception {
		IField field;
		
		field = getSelectedCell().getField();
		
		field.setValue(value);
		endEditing(null, field);
	}

	
	/**
	 * Bearbeiten des Feldes beenden und ggf. Daten aktualisieren
	 * 
	 * @param fld
	 * @throws Exception
	 */
	@Override
	public void endEditing(Object bookmark, IField fld) throws Exception {
		CoMonatseinsatzblattProjekt coMonatseinsatzblattProjekt;
		VirtCoProjekt virtCoProjekt;
		
		// Bemerkung
		if (isBemerkungsfeld(0))
		{
			// Projekt bestimmen
			virtCoProjekt = m_coMonatseinsatzblattAnzeige.getVirtCoProjekt();
			virtCoProjekt.moveTo(m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(fld));
			
			// Projektdaten laden
			coMonatseinsatzblattProjekt = new CoMonatseinsatzblattProjekt();
			coMonatseinsatzblattProjekt.load(m_coPerson.getID());
			
			// Bemerkung speichern
			if (coMonatseinsatzblattProjekt.moveTo(virtCoProjekt))
			{
				coMonatseinsatzblattProjekt.begin();
				coMonatseinsatzblattProjekt.setBemerkung(fld.getDisplayValue());
				coMonatseinsatzblattProjekt.save();
			}
			else // Meldung, wenn die Bemerkung nicht gespeichert wird
			{
				Messages.showInfoMessage("Bemerkung nicht gespeichert", 
						"Die Bemerkung wurde nicht gespeichert, da Sie das Projekt bereits aus Ihrer persönlichen Projektliste entfernt haben.");
			}
		}
		// Stundenwerte
		else
		{
			// Stundenwert aktualisieren
			m_coMonatseinsatzblattAnzeige.update(fld);
		
			// Farbe der Zeile löschen, damit sie mit Berücksichtigung der Eingabe neu bestimmt wird
			deleteCellColor();

			// kein komplettes Refresh der Oberfläche, sonst geht die Selektion verloren
			m_formPersonMonatseinsatzblatt.refreshTaetigkeitBemerkung();
		}
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * alle Color-Eintragungen für die aktuelle Zeile löschen
	 * 
	 */
	public void deleteCellColor() {
		deleteRowColor(getCurrentRowIndex());
		
		// Farbe für die Summenzeile auch löschen
		deleteRowColor(m_coMonatseinsatzblattAnzeige.getRowCount()-1);
	}


	/**
	 * alle Color-Eintragungen für die aktuelle Zeile löschen
	 * 
	 */
	private void deleteRowColor(int rowIndex) {
		int iKey, anzKeys;
		
		Object[] keyList = m_mapCellColor.keySet().toArray();
		
		// alle zellen durchlaufen
		anzKeys = keyList.length;
		for (iKey=0; iKey<anzKeys; ++iKey) 
		{
			String key = (String) keyList[iKey];
			
			// Eintragungen für die Zeile löschen
			if (key.startsWith("" + rowIndex))
			{
				m_mapCellColor.remove(key);
			}
		}
	}

	
	@Override
	public void refresh(int reason, Object element) {
		int row;
		
		// zuletzt markierte Zeile nach Refresh wieder markieren
		row = getCurrentRowIndex();
		super.refresh(reason, element);
//		System.out.println("Test: " + row + " - " + getCurrentRowIndex());
		m_coMonatseinsatzblattAnzeige.moveTo(row);
//		System.out.println("Test NEU: " + row + " - " + getCurrentRowIndex());
	}
}
