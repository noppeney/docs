package pze.ui.formulare.person.monatseinsatzblatt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import framework.FW;
import framework.business.interfaces.data.IBusinessObject;
import framework.ui.controls.ComboControl;
import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITableControl;
import framework.ui.interfaces.keys.IKeyListener;
import framework.ui.interfaces.keys.KeyData;
import framework.ui.interfaces.selection.IFocusListener;
import framework.ui.interfaces.selection.ISelectionListener;
import framework.ui.interfaces.selection.IValueChangeListener;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;
import pze.business.Messages;
import pze.business.export.ExportMonatseinsatzblattListener;
import pze.business.export.ExportTaetigkeitsnachweisListener;
import pze.business.navigation.NavigationManager;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattAnzeige;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattPhasen;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattProjekt;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.business.objects.reftables.CoMonatsauswahl;
import pze.ui.controls.IntegerToUhrzeitControl;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.person.FormPerson;
import startup.PZEStartupAdapter;


/**
 * Formular Monatseinsatzblatt
 * 
 * @author Lisiecki
 *
 */
public class FormPersonMonatseinsatzblatt extends UniFormWithSaveLogic {
	public static final String RESID = "form.person.monatseinsatzblatt";

	private CoPerson m_coPerson;
	private CoMonatseinsatzblattAnzeige m_coMonatseinsatzblattAnzeige;
	
	private Map<Integer, CoMonatseinsatzblattPhasen> m_mapMonatseinsatzblattPhasen;

	private CoMonatsauswahl m_coMonatsauswahl;

	private FormPerson m_formPerson;

	private Date m_datum;
	
	private ITableControl m_tableControl;
	private ITableControl m_tableControlPhasen;
	private TableMonatseinsatzblatt m_table;
	private TableMonatseinsatzblattPhasen m_tablePhasen;
	
	private ComboControl m_comboMonatsauswahl;
	private IButtonControl m_btProjektHinzufuegen;
	private IButtonControl m_btProjektEntfernen;

	private IButtonControl m_btExportReisekostenabrechnung;
	private IButtonControl m_btExportMonatseinsatzblatt;
	private IButtonControl m_btExportTaetigkeitsnachweis;

	private TextControl m_tfAuftragsBeschreibung;
	private TextControl m_tfAbrufBeschreibung;
	private TextControl m_tfEdvNr;

	private ComboControl m_comboKunde;
	private TextControl m_tfAuftragsNr;
	private TextControl m_tfAbrufNr;
	private TextControl m_tfKostenstelle;
	private ComboControl m_comboProjektleiter;
	private ComboControl m_comboStundenart;
	
	private TextControl m_tfDatum;
	private IntegerToUhrzeitControl m_tfZeit;
	private TextControl m_tfTaetigkeit;
	private IButtonControl m_btTaetigkeitUebernehmen;
	private TextControl m_tfBemerkung;

	private ComboControl m_comboStatus;
	private ComboControl m_comboGeaendertVon;
	private TextControl m_tfGeaendertAm;

	private AbstractCacheObject m_coMonatseinsatzblattForSave;
	
	private KeyListener m_keyListener;
	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coPerson Co der geloggten Daten
	 * @param formPerson Hauptformular der Person
	 * @param datum 
	 * @throws Exception
	 */
	public FormPersonMonatseinsatzblatt(Object parent, CoPerson coPerson, FormPerson formPerson, Date datum) throws Exception {
		super(parent, RESID, true);
		
		m_formPerson = formPerson;
		m_coPerson = coPerson;
		
		m_datum = datum;
		if (m_datum == null)
		{
			m_datum = new Date();
		}
// TODO test doppelte Einträge im Monatseinsatzblatt		m_datum = Format.getDate12Uhr(m_datum);

		m_keyListener = new KeyListener();
		m_mapMonatseinsatzblattPhasen = new HashMap<Integer, CoMonatseinsatzblattPhasen>();

		initFormular();
		initTable();
		
		// Edit-Modus abfragen, da das Formular neu geladen werden kann
//		if (m_formPerson.getData().isEditing())
//		{
//			refresh(reasonEnabled, null);
//		}
//		else
		{
			refresh(reasonDisabled, null);
		}
	}


	/**
	 * GUI initialisieren und Variablen der Formularfelder initialisieren
	 * 
	 * @throws Exception
	 */
	private void initFormular() throws Exception {
		initComboMonatsauswahl();
		initBtProjekte();
		initBtExport();
		
		m_tfAuftragsBeschreibung = (TextControl) findControl(RESID + ".auftragsbeschreibung");
		m_tfAbrufBeschreibung = (TextControl) findControl(RESID + ".abrufbeschreibung");
		m_tfEdvNr = (TextControl) findControl(RESID + ".edvnr");

		if (!PZEStartupAdapter.MODUS_ARBEITSPLAN)
		{
			m_comboKunde = (ComboControl) findControl(RESID + ".kundeid");
			m_tfAuftragsNr = (TextControl) findControl(RESID + ".auftragsnr");
			m_tfAbrufNr = (TextControl) findControl(RESID + ".abrufnr");
			m_tfKostenstelle = (TextControl) findControl(RESID + ".kostenstelle");
			m_comboProjektleiter = (ComboControl) findControl(RESID + ".projektleiterid");
			m_comboStundenart = (ComboControl) findControl(RESID + ".stundenartid");
		}
		
		m_tfDatum = (TextControl) findControl(RESID + ".datum");
		m_tfZeit = (IntegerToUhrzeitControl) findControl(RESID + ".wertzeit");
		m_tfTaetigkeit = (TextControl) findControl(RESID + ".taetigkeit");
		m_btTaetigkeitUebernehmen = (IButtonControl) findControl(RESID + ".taetigkeituebernehmen");
		m_tfBemerkung = (TextControl) findControl(RESID + ".bemerkung");
		
		m_comboStatus = (ComboControl) findControl(RESID + ".statusid");
		m_comboGeaendertVon = (ComboControl) findControl(RESID + ".geaendertvonid");
		m_tfGeaendertAm = (TextControl) findControl(RESID + ".geaendertam");
		
		
		// Listener
		m_tfTaetigkeit.setKeyListener(m_keyListener);
		m_tfBemerkung.setKeyListener(m_keyListener);
		
		m_tfTaetigkeit.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				// Zellfarbe aktualisieren wegen Prüfung auf eingetragene Tätigkeit
				m_table.deleteCellColor();
			}
			
			@Override
			public void focusGained(IControl control) {
			}
		});
		
		m_btTaetigkeitUebernehmen.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try 
				{
					taetigkeitUebernehmen();
					
					// Zellfarbe aktualisieren wegen Prüfung auf eingetragene Tätigkeit
					m_table.deleteCellColor();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			
			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
	}


	/**
	 * Combo zur Monatsauswahl erstellen und füllen
	 * 
	 * @throws Exception
	 */
	private void initComboMonatsauswahl() throws Exception {
		
		m_coMonatsauswahl = new CoMonatsauswahl(m_coPerson);
		m_coMonatsauswahl.createCo();
		m_coMonatsauswahl.movetoMonat(m_datum);
		
		// Combo erstellen und Wert setzen
		m_comboMonatsauswahl = (ComboControl) findControl(RESID + ".monatsauswahl");
		m_comboMonatsauswahl.getField().setValue(m_coMonatsauswahl.getID());

		// Items setzen
		refreshItems(m_comboMonatsauswahl, m_coMonatsauswahl, m_comboMonatsauswahl.getField());

		// Listener zum wechseln des Monats
		m_comboMonatsauswahl.setValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				try
				{
					if (checkSpeichern())
					{
						reloadFormMonatseinsatzblatt();
					}
					else // Auswahl zurücksetzen
					{
						m_comboMonatsauswahl.getField().setValue(lastValue);
						m_comboMonatsauswahl.refresh(reasonDataChanged, null);
					}
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		
	}
	
	
	/**
	 * Buttons zum Hinzufügen und Entfernen von Projekten mit Listenern initialisieren
	 */
	private void initBtProjekte() {
		
		m_btProjektHinzufuegen = (IButtonControl) findControl("form.person.monatseinsatzblatt.projekthinzufuegen");
		m_btProjektHinzufuegen.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try
				{
					if (projektHinzufuegen())
					{
						reloadFormMonatseinsatzblatt();
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}		
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		m_btProjektEntfernen = (IButtonControl) findControl("form.person.monatseinsatzblatt.projektentfernen");
		m_btProjektEntfernen.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try
				{
					if (projektEntfernen())
					{
						reloadFormMonatseinsatzblatt();
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}		
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
	}

	
	/**
	 * Buttons zum Export mit Listenern initialisieren
	 */
	private void initBtExport() {
		
//		m_btExportReisekostenabrechnung = (IButtonControl) findControl("form.person.monatseinsatzblatt.exportreisekostenabrechnung");
//		m_btExportReisekostenabrechnung.setSelectionListener(new ISelectionListener() {
//			
//			@Override
//			public void selected(IControl control, Object params) {
//				try
//				{
//					(new ExportReisekostenListener(FormPersonMonatseinsatzblatt.this)).activate(null);
//				} 
//				catch (Exception e) 
//				{
//					e.printStackTrace();
//				}		
//			}
//
//			@Override
//			public void defaultSelected(IControl control, Object params) {
//			}
//		});
		
		m_btExportMonatseinsatzblatt = (IButtonControl) findControl("form.person.monatseinsatzblatt.exportmonatseinsatzblatt");
		m_btExportMonatseinsatzblatt.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try
				{
					if (checkSpeichern())
					{
						(new ExportMonatseinsatzblattListener(FormPersonMonatseinsatzblatt.this)).activate(null);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}		
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		m_btExportTaetigkeitsnachweis = (IButtonControl) findControl("form.person.monatseinsatzblatt.exporttaetigkeitsnachweis");
		m_btExportTaetigkeitsnachweis.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				try
				{
					if (checkSpeichern())
					{
						(new ExportTaetigkeitsnachweisListener(FormPersonMonatseinsatzblatt.this)).activate(null);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}		
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
	}

	
	/**
	 * Tabelle initialisieren
	 * 
	 * @throws Exception
	 */
	private void initTable() throws Exception {
		m_coMonatseinsatzblattAnzeige = new CoMonatseinsatzblattAnzeige(m_coPerson.getID(), m_datum);
//		if (m_formPerson.getData().isEditing())
//		{
//			m_coMonatseinsatzblattAnzeige.begin();
//			m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt().begin();
//		}
//		setData(m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt());

		// Tabelle mit Phasen des Arbeitsplans
		if (PZEStartupAdapter.MODUS_ARBEITSPLAN)
		{
			m_tableControlPhasen = (ITableControl) findControl("spread.monatseinsatzblatt.projektdaten.phase");
			m_tablePhasen = new TableMonatseinsatzblattPhasen(m_tableControlPhasen, null, this);
//			m_tablePhasen.enable(false);
//			m_tablePhasen.enableColumn(1, true);
			setDataArbeitsplan(null);
		}

		// erst danach Table Monatseinsatzblatt, da Phasen-Dummy dabei gesetzt wird
		m_tableControl = (ITableControl) findControl("spread.person.monatseinsatzblatt");
		m_table = new TableMonatseinsatzblatt(m_tableControl, m_coMonatseinsatzblattAnzeige, this);
		m_table.setKeyListener(m_keyListener);
		
		m_coPerson.removeChild(m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt().getKey());
		m_coPerson.addChild(m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt());
		
	}


	/**
	 * Projekt zur Tabelle hinzufügen
	 * 
	 * @return Hinzufügen erfolgreich
	 * @throws Exception
	 */
	private boolean projektHinzufuegen() throws Exception {
		if (!checkSpeichern())
		{
			return false;
		}

		if (DialogProjektMonatseinsatzblatt.showDialog(m_coPerson.getID()))
		{
//			if (checkSpeichern())
			{
				return true;
			}
		}
		
		return true;
	}

	
	/**
	 * Projekt aus Tabelle entfernen
	 * 
	 * @return Löschen erfolgreich
	 * @throws Exception
	 */
	private boolean projektEntfernen() throws Exception {
		int projektFieldIndex;
		ISpreadCell selectedCell;
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		CoMonatseinsatzblattProjekt coMonatseinsatzblattProjekt;
		VirtCoProjekt virtCoProjekt;
		
		if (!checkSpeichern())
		{
			return false;
		}
		
		selectedCell = m_table.getSelectedCell();
		if (selectedCell == null)
		{
			return false;
		}
		
		// Projektdaten bestimmen
		projektFieldIndex = m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(selectedCell.getField());
		virtCoProjekt = m_coMonatseinsatzblattAnzeige.getVirtCoProjekt();
		virtCoProjekt.moveTo(projektFieldIndex);

		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Projekt entfernen", 
				"Möchten Sie das Projekt '" + virtCoProjekt.getProjektinfos() + "' wirklich aus der Liste entfernen?<br>"
						+ "Gebuchte Stunden für dieses Projekt werden weiterhin angezeigt."))
		{
			return false;
		}
		
		
		// leere Einträge für das Projekt in diesem Monat aus DB löschen
		coMonatseinsatzblatt = new CoMonatseinsatzblatt();
		virtCoProjekt.moveTo(projektFieldIndex); // durch messagebox geht die Selektion verloren
		coMonatseinsatzblatt.deleteNullValues(m_coPerson.getID(), m_coMonatseinsatzblattAnzeige.getDatum(), virtCoProjekt);
		
		// Projekt aus DB löschen
		coMonatseinsatzblattProjekt = new CoMonatseinsatzblattProjekt();
		coMonatseinsatzblattProjekt.load(m_coPerson.getID());
		coMonatseinsatzblattProjekt.delete(virtCoProjekt);

		return true;
	}


	/**
	 * Prüfe, ob das Formular sich im Edit-Modus befindet und speichere die Daten ggf. (nach Abfrage) ab
	 * 
	 * @return Prüfung erfolgreich
	 * @throws Exception
	 */
	private boolean checkSpeichern() throws Exception {
		int result;
		
		// Modus prüfen
		if (!getData().isModified())
		{
			m_formPerson.cancelEditing();
			{
//				getData().cancel();
//				refreshByEditMode();
			}
			return true;
		}
		
		// Abfrage, ob gespeichert werden soll
		result = Messages.showYesNoCancelMessage("Daten speichern", "Sollen die geänderten Daten gespeichert werden?");
		
		if (result == FW.CANCEL)
		{
			return false;
		}
		else if (result == FW.YES)
		{
			// Speichern und zurückgeben, ob das Speichern funktioniert hat
			m_formPerson.validateAndSave();
			return !getData().isEditing();
		}
		else // Bearbeitung abbrechen
		{
			m_formPerson.cancelEditing();
		}

		return true;
	}


	/**
	 * Formular neu laden
	 * 
	 * @throws Exception
	 */
	private void reloadFormMonatseinsatzblatt() throws Exception {
		m_formPerson.reloadFormMonatseinsatzblatt(true);
		if (m_formPerson.mayEdit())
		{
			m_formPerson.beginEditing();
		}
	}
	

//	/**
//	 * Stundenzuordnung zu Phasen speichern
//	 */
//	@Override
//	public void doAfterSave() throws Exception {
//		if (!PZEStartupAdapter.MODUS_ARBEITSPLAN)
//		{
//			return;
//		}
//
//		CoMonatseinsatzblattPhasen coMonatseinsatzblattPhasen;
//		Iterator<Integer> iter;
//
//		if (m_mapMonatseinsatzblattPhasen.size() == 0)
//		{
//			return;
//		}
//		
//		// über alle Einträge iterieren
//		iter = m_mapMonatseinsatzblattPhasen.keySet().iterator();
//		while (iter.hasNext())
//		{
//			coMonatseinsatzblattPhasen = m_mapMonatseinsatzblattPhasen.get((Integer) iter.next());
//			
//			if (coMonatseinsatzblattPhasen.isEditing())
//			{
//				coMonatseinsatzblattPhasen.save();
//			}
//		}
//		
//	}


	/**
	 * Datum des aktuell ausgewähltes Monats
	 * 
	 * @return
	 */
	public Date getCurrentDatum(){
		return m_coMonatsauswahl.getDatum();
	}


	/**
	 * Bezeichnung des aktuell ausgewählten Monats
	 * 
	 * @return
	 */
	public String getCurrentMonat(){
		return m_coMonatsauswahl.getBezeichnung();
	}
	

	public CoPerson getCoPerson(){
		return m_coPerson;
	}
	

	public CoMonatseinsatzblattAnzeige getCoMonatseinsatzblattAnzeige(){
		return m_coMonatseinsatzblattAnzeige;
	}
	

	/**
	 * letzte Tätigkeit für das Projekt übernehmen
	 * 
	 * @throws Exception
	 */
	private void taetigkeitUebernehmen() throws Exception {
		int tagDesMonats, iTag;
		String taetigkeit;
		ISpreadCell cell;
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		VirtCoProjekt virtCoProjekt;
		
		
		taetigkeit = null;
		
		cell = m_table.getSelectedCell();
		tagDesMonats = m_table.getSelectedTagDesMonats();
		m_coMonatseinsatzblattAnzeige.moveToCell(cell);
		// zum Tag des Monats separat gehen, da durch Aktualisierungen der Oberfläche der Tag verloren gehen kann
		m_coMonatseinsatzblattAnzeige.moveToTag(tagDesMonats);

		virtCoProjekt = m_coMonatseinsatzblattAnzeige.getVirtCoProjekt();
		coMonatseinsatzblatt = m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt();
		
		// letzte Tätigkeit suchen
		while (m_coMonatseinsatzblattAnzeige.movePrev())
		{
			// hier Tag des Monats vom Co holen, da das co durchlaufen wird
			iTag = m_coMonatseinsatzblattAnzeige.getTagDesMonats();
			// wenn alle Tage durchlaufen oder eine Tätigkeit gefunden, beende die Schleife
			if (iTag == 0 || (coMonatseinsatzblatt.moveTo(virtCoProjekt, iTag) && (taetigkeit = coMonatseinsatzblatt.getTaetigkeit()) != null))
			{
				break;
			}
		}
		
		if (taetigkeit == null)
		{
			taetigkeit = CoMonatseinsatzblatt.loadLastTaetigkeit(m_coPerson.getID(), m_coMonatseinsatzblattAnzeige.getDatum(), 		
					m_coMonatseinsatzblattAnzeige.getVirtCoProjekt());
		}
		m_coMonatseinsatzblattAnzeige.moveToTag(tagDesMonats);
		coMonatseinsatzblatt.moveTo(virtCoProjekt, tagDesMonats);
		coMonatseinsatzblatt.setTaetigkeit(taetigkeit);
		
		m_tfTaetigkeit.refresh(reasonDataChanged, null);
	}

	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "personmonatseinsatzblatt" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	/**
	 * Arbeitszeit des aktuellen Tages
	 * 
	 * @return
	 */
	private int getZeit() {
		return Format.getZeitAsInt(Format.getStringValue(m_tfZeit.getField().getValue()));
	}

	
	@Override
	public void activate() {
		
		if (NavigationManager.getSelectedTabItemKey() != null && NavigationManager.getSelectedTabItemKey().equals(getResID()))
		{
			m_formPerson.addSaveHandler();
			refreshByEditMode();
			super.activate();
		}
	}
	
	
	@Override
	public void deactivate() {
		m_formPerson.removeSaveHandler();
		super.deactivate();
	}

	
	/**
	 * Zeit-Formularfeld aktualisieren
	 * 
	 * @see framework.cui.controls.base.BaseCompositeControl#setData(framework.business.interfaces.data.IBusinessObject)
	 */
	@Override
	public void setData(IBusinessObject data) throws Exception {
		super.setData(data);
		
		// das zuletzt ausgewählte CoMonatseinsatzblatt zwischenspeichern, da dieses vor dem Speichern wieder gesetzt werden muss
		// zwischendurch werden Dummy-Co's gesetzt
		AbstractCacheObject co = (AbstractCacheObject) data;
		if (co.getID() > 0)
		{
			m_coMonatseinsatzblattForSave = co;
		}
		
		// refresh, damit das Zeit-Formularfeld aktualisiert wird
		m_tfZeit.refresh(reasonDataChanged, null);

		// Tätigkeit und Bemerkung aktivieren, wenn eine Arbeitszeit für den Tag eingetragen ist
		refreshTaetigkeitBemerkung();
	}
	
	
	/**
	 * Daten für den Arbeitsplan speichern
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void setDataArbeitsplan(int monatseinsatzblattID) throws Exception {
		CoMonatseinsatzblattPhasen data;
		
		if (PZEStartupAdapter.MODUS_ARBEITSPLAN )
		{
			// Daten speichern
			data = m_mapMonatseinsatzblattPhasen.get(monatseinsatzblattID);
			m_tablePhasen.setData(data);
			if (m_coPerson.isEditing() && !data.isEditing())
			{
				data.begin();
			}
		}
	}
	
	
	/**
	 * Daten für den Arbeitsplan speichern
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void setDataArbeitsplan(CoMonatseinsatzblattPhasen data) throws Exception {
		int monatseinsatzblattID;
		
		if (!PZEStartupAdapter.MODUS_ARBEITSPLAN)
		{
			return;
		}
		
		if (data == null)
		{
			m_tablePhasen.setData(null);
			return;
		}
		
		// prüfen, ob bereits Daten vorhanden sind
		monatseinsatzblattID = data.getMonatseinsatzblattID();
		if (!hasDataArbeitsplan(monatseinsatzblattID))
		{
			// Daten speichern
			m_tablePhasen.setData(data);
			m_mapMonatseinsatzblattPhasen.put(monatseinsatzblattID, data);
			
			m_coPerson.removeChild(data.getKey());
			m_coPerson.addChild(data);
			
			if (m_coPerson.isEditing() && !data.isEditing())
			{
				data.begin();
			}
		}
		else
		{
			setDataArbeitsplan(monatseinsatzblattID);
		}
	}
	
	
	/**
	 * prüft, ob für diesen Eintrag im Monatseinsatzblatt bereits Daten hinterlegt sind
	 * 
	 * @param monatseinsatzblattID
	 * @return
	 * @throws Exception
	 */
	public boolean hasDataArbeitsplan(int monatseinsatzblattID) throws Exception {
		return m_mapMonatseinsatzblattPhasen.containsKey(monatseinsatzblattID);
	}
	
	
	/**
	 * Zwischengespeichertes CO zurückgeben, da zwischendurch Dummy-Co's gesetzt werden
	 * 
	 * @see framework.cui.controls.base.BaseCompositeControl#getData()
	 */
	 @Override
	 public IBusinessObject getData() {
		 if (m_coMonatseinsatzblattForSave != null)
		 {
			 return m_coMonatseinsatzblattForSave;
		 }

		 return super.getData();
	 }

	
	/*
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);

		// wenn kein Monat zur Eingabe freigegeben ist, darf nichts aktiviert werden
		if (m_coMonatsauswahl.isEmpty())
		{
			super.refresh(reasonDisabled, element);
			return;
		}
		
		// Monatsauswahl und Buttons zum Hinzufügen und Entfernen von Projekten sowie Export immer aktivieren
		m_comboMonatsauswahl.refresh(reasonEnabled, null);
//		m_btExportReisekostenabrechnung.refresh(reasonEnabled, null);
		m_btExportMonatseinsatzblatt.refresh(reasonEnabled, null);
		m_btExportTaetigkeitsnachweis.refresh(reasonEnabled, null);

		// Buttons zum Hinzufügen und Entfernen bei Berechtigung zum Bearbeiten aktivieren, sonst deaktivieren
		if (m_formPerson.mayEdit())
		{
			m_btProjektHinzufuegen.refresh(reasonEnabled, null);
			m_btProjektEntfernen.refresh(reasonEnabled, null);
		}
		
		// Formularfelder immer deaktivieren
		m_tfAuftragsBeschreibung.refresh(reasonDisabled, null);
		m_tfAbrufBeschreibung.refresh(reasonDisabled, null);
		m_tfEdvNr.refresh(reasonDisabled, null);

		if (!PZEStartupAdapter.MODUS_ARBEITSPLAN)
		{
			m_comboKunde.refresh(reasonDisabled, null);
			m_tfAuftragsNr.refresh(reasonDisabled, null);
			m_tfAbrufNr.refresh(reasonDisabled, null);
			m_tfKostenstelle.refresh(reasonDisabled, null);
			m_comboProjektleiter.refresh(reasonDisabled, null);
			m_comboStundenart.refresh(reasonDisabled, null);
		}
		
		m_tfDatum.refresh(reasonDisabled, null);
		m_tfZeit.refresh(reasonDisabled, null);
		
		m_comboStatus.refresh(reasonDisabled, null);
		m_comboGeaendertVon.refresh(reasonDisabled, null);
		m_tfGeaendertAm.refresh(reasonDisabled, null);

		// Tätigkeit und Bemerkung aktivieren, wenn eine Arbeitszeit für den Tag eingetragen ist
		refreshTaetigkeitBemerkung();
	}


	/**
	 * Tätigkeit und Bemerkung aktivieren, wenn eine Arbeitszeit für den Tag eingetragen ist
	 */
	public void refreshTaetigkeitBemerkung() {

		if (getData() != null && getData().isEditing() && getZeit() > 0)
		{
			m_tfZeit.refresh(reasonDataChanged, null);
			m_tfTaetigkeit.refresh(reasonEnabled, null);
			m_btTaetigkeitUebernehmen.refresh(reasonEnabled, null);
			m_tfBemerkung.refresh(reasonEnabled, null);
		}
		else
		{
			m_tfTaetigkeit.refresh(reasonDisabled, null);
			m_btTaetigkeitUebernehmen.refresh(reasonDisabled, null);
			m_tfBemerkung.refresh(reasonDisabled, null);
		}
	}
	
	
	/**
	 * Listener zum Aktualisieren von Summe und Prüfen auf Bearbeitbarkeit von Datensätzen
	 */
	class KeyListener implements IKeyListener{

		@Override
		public boolean onKeyPressed(IControl control, KeyData data) {

			// Tab-Taste
			if (getData().isEditing() && data.character == '	')
			{
				if (control.equals(m_tfTaetigkeit))
				{
					// wenn der Button deaktiviert ist, gehe zur Bemerkung
					if (!((org.eclipse.swt.widgets.Button) m_btTaetigkeitUebernehmen.getComponent()).setFocus())
					{
						m_tfBemerkung.setFocus();
					}
				}
//				else if (control.equals(m_btTaetigkeitUebernehmen)) // funktioniert nicht bei Buttons
//				{
//					m_tfBemerkung.setFocus();
//				}
				else if (control.equals(m_tfBemerkung))
				{
					m_table.setFocus();
				}
				else if (control.equals(m_tableControl)) // nicht unbedingt notwendig, da es automatisch gemacht wird
				{
					m_tfTaetigkeit.setFocus();
				}
				else
				{
					return true;
				}
				
				return false;
			}
			
			// mit "Entf" und "<-" Eintrag in der Tabelle löschen
			else if (data.keyCode == 8 || data.keyCode == 127)
			{
				if (control.equals(m_tableControl)) 
				{
					try
					{
						// nur im Edit-modus und bei den zu bearbeitenden Zellen
						if (getData().isEditing() && m_table.mayEdit(m_table.getSelectedBookmark(), m_table.getSelectedCell()))
						{
							m_table.setValue(null);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
			return true;
		}

		@Override
		public boolean onKeyReleased(IControl control, KeyData data) {
			return false;
		}
	}


}
