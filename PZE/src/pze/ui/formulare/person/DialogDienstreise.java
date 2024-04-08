package pze.ui.formulare.person;

import framework.business.fields.FieldDescription;
import framework.business.interfaces.FW;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.cui.layout.UniLayout;
import framework.ui.controls.BooleanControl;
import framework.ui.controls.ComboControl;
import framework.ui.controls.TextControl;
import framework.ui.form.UniForm;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.dienstreisen.CoDienstreisezeit;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoKostenstelle;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.business.objects.reftables.CoZielDienstreise;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.business.objects.reftables.projektverwaltung.CoKunde;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;

/**
 * Dialog für Dienstreiseanträge
 * 
 * @author Lisiecki
 */
public class DialogDienstreise extends UniForm {
	
	private final static String RESID_DIENSTREISE = "dialog.dienstreise";

	private static CoDienstreise m_coDienstreise;
	private static CoBuchung m_coBuchung;
	private static boolean m_validateCo;
	private static boolean m_isDG;
	
	private ComboControl m_comboPerson;

	private ComboControl m_comboLand;
	private ComboControl m_comboZiel;
	private ComboControl m_comboZweck;
	private TextControl m_tfZiel;
	private TextControl m_tfThema;

	private ComboControl m_comboKunde;
	private ComboControl m_comboAuftrag;
	private ComboControl m_comboAbruf;
	private ComboControl m_comboKostenstelle;

	/**
	 * ComboControl, dass als erstes (nach dem Kunden) ausgewählt wurde. Die Items für dieses Control werden nicht aktualisiert.
	 */
	private ComboControl m_comboErsteAuswahl;

	private BooleanControl m_checkUebernachtung;
	private BooleanControl m_checkUebernachtungUeberKunde;
	private TextControl m_tfHotel;

	private BooleanControl m_checkDienstwagen;
	private BooleanControl m_checkMietwagen;
	private BooleanControl m_checkPrivatPkw;
	private BooleanControl m_checkBahn;
	private BooleanControl m_checkTaxi;
	private BooleanControl m_checkFlugzeug;
	private BooleanControl m_checkFahrtKunde;
	private TextControl m_tfMitfahrt;

	protected SortedTableControl m_table;

	private TextControl m_tfGeaendertAm;
	private ComboControl m_comboGeaendertVon;

	private BooleanControl m_checkHinweise;
	private TextControl m_tfHinweise;
	private TextControl m_tfBemerkung;
	private TextControl m_tfBemerkungAl;

	private static int m_statusGenehmigungID;
	private static int m_messageGruppeID;

	
	
	/**
	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogDienstreise(String resID) throws Exception {
		super(null, resID);	
		long a = System.currentTimeMillis();
		super.createChilds();
		System.out.println("createChilds: " + (System.currentTimeMillis()-a)/1000.);
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
		// Controls festlegen
		initControls();
		initListener();
		initTable();
		System.out.println("init gesamt: " + (System.currentTimeMillis()-a)/1000.);
	}

	
	/**
	 * Dialog mit der angegebenen Dienstreise öffnen
	 * 
	 * @param dienstreiseID 
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialog(int dienstreiseID) throws Exception {
		return showDialog(dienstreiseID, false, 0, 0);			
	}

	
	/**
	 * Dialog mit der angegebenen Dienstreise öffnen
	 * 
	 * @param dienstreiseID 
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialog(int dienstreiseID, int messageGruppeID, int statusGenehmigungID) throws Exception {
		return showDialog(dienstreiseID, false, messageGruppeID, statusGenehmigungID);
	}

	
	/**
	 * Dialog mit der angegebenen Dienstreise öffnen
	 * 
	 * @param dienstreiseID 
	 * @param validateCo CO auf Vollständigkeit prüfen 
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialog(int dienstreiseID, boolean validateCo, int messageGruppeID, int statusGenehmigungID) throws Exception {
		CoDienstreise coDienstreise;
		
		coDienstreise = new CoDienstreise();
		coDienstreise.loadByID(dienstreiseID);

		return showDialog(coDienstreise, validateCo, messageGruppeID, statusGenehmigungID);
	}

	
	/**
	 * Dialog mit der angegebenen Dienstreise öffnen
	 * 
	 * @param coDienstreise 
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	private static boolean showDialog(CoDienstreise coDienstreise, boolean validateCo, int messageGruppeID, int statusGenehmigungID) throws Exception {
		DialogDienstreise dialog;

		dialog = new DialogDienstreise(RESID_DIENSTREISE);
		dialog.initData(coDienstreise);
		
		m_validateCo = validateCo;
		m_messageGruppeID = messageGruppeID;
		m_statusGenehmigungID = statusGenehmigungID;
		
		// Ziele für DR oder DG
		dialog.refreshItemsComboZiele();
		
		// Datum und Status dürfen nicht bearbeitet werden
		dialog.m_table.enableColumn(m_coBuchung.getFieldDatum().getFieldDescription().getResID(), false);
		dialog.m_table.enableColumn(m_coBuchung.getFieldStatusID().getFieldDescription().getResID(), false);
		dialog.m_table.enableColumn(m_coBuchung.getFieldStatusGenehmigungID().getFieldDescription().getResID(), false);

		return showDialog(dialog);			
	}


	/**
	 * Übergebenen Dialog öffnen
	 * 
	 * @param dialog
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	private static boolean showDialog(DialogDienstreise dialog) throws Exception {
		CoBuchung coBuchung;
		
		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);

		dialog.getDialog().show();

		// TODO nicht gut gelöst, aber Spalten müssen wieder aktiviert werden für andere Formulare
		SortedTableControl.enableField((FieldDescription) m_coBuchung.getFieldDatum().getFieldDescription(), true);
		SortedTableControl.enableField((FieldDescription) m_coBuchung.getFieldStatusID().getFieldDescription(), true);
		SortedTableControl.enableField((FieldDescription) m_coBuchung.getFieldStatusGenehmigungID().getFieldDescription(), true);

		// wenn nicht OK geklickt wurde, keine Aktion
		if (dialog.getDialog().getRetVal() != FW.OK)
		{
			m_coDienstreise.cancel();
			return false;
		}

		// wenn abgelehnte oder genehmigte Buchungen bearbeitet werden, müssen die Tagesbuchungen für eine erneute Freigabe angepasst werden
		if (m_coDienstreise.isModified()
				&& (m_statusGenehmigungID == CoStatusGenehmigung.STATUSID_ABGELEHNT || m_statusGenehmigungID == CoStatusGenehmigung.STATUSID_GENEHMIGT))
		{
			coBuchung = new CoBuchung();
			m_coBuchung.moveFirst();
			do
			{
				// alle Buchungen mit dem ausgewählten Status anpassen
				if (m_coBuchung.getStatusGenehmigungID() == m_statusGenehmigungID)
				{
					// Buchung muss neu geladen werden, da m_coBuchung mit Reise- und Projektzeit nicht gespeichert wird
					coBuchung.loadByID(m_coBuchung.getID());
					coBuchung.begin();
					coBuchung.setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_GEPLANT);
					coBuchung.save();
				}
			} while(m_coBuchung.moveNext());
		}
		
		// DR-Daten speichern
		m_coDienstreise.save();
		
		// Buchungen mit Zeiten der Tage speichern
		saveCoBuchung();
		
		
		// ggf. DR-CO prüfen, z. B. vor Beantragung der Freigabe
		if (m_validateCo)
		{
			// Angaben zur DR
			String fehler = m_coDienstreise.validate();
			if (fehler != null && !fehler.isEmpty())
			{
				Messages.showErrorMessage("Freigabe kann nicht beantragt werden", fehler);
				return showDialog(m_coDienstreise, m_validateCo, m_messageGruppeID, m_statusGenehmigungID);
			}
			
			// Zeitangaben der Einzeltage
			fehler = m_coBuchung.validateZeitenDrDg();
			if (fehler != null && !fehler.isEmpty())
			{
				Messages.showErrorMessage("Freigabe kann nicht beantragt werden", fehler);
				return showDialog(m_coDienstreise, m_validateCo, m_messageGruppeID, m_statusGenehmigungID);
			}
		}
		
		return true;
	}


	private static void saveCoBuchung() throws Exception {
		int status;
		CoDienstreisezeit coDienstreisezeit;
		
		coDienstreisezeit = new CoDienstreisezeit();
		
		
		// Tage durchlaufen
		m_coBuchung.moveFirst();
		do
		{
			status = m_coBuchung.getCurrentRow().getRowState();
			
			// keine Änderungen
			if (status == IBusinessObject.statusUnchanged)
			{
				continue;
			}
			// Änderungen für den Tag
			else if (status == IBusinessObject.statusChanged)
			{
				System.out.println("zeit: " + m_coBuchung.getFieldUhrzeitAsInt().getState());
				System.out.println("zeit bis: " + m_coBuchung.getFieldUhrzeitBis().getState());
				// Uhrzeit geändert
				if (m_coBuchung.getFieldUhrzeitAsInt().getState() == IBusinessObject.statusChanged
						|| m_coBuchung.getFieldUhrzeitBis().getState() == IBusinessObject.statusChanged)
				{
					DialogBuchungAendern.loadAndSave(m_coBuchung);
				}
				
				// Zeiten speichern
				coDienstreisezeit.load(m_coBuchung.getID());
				// Datensatz anlegen, falls noch nicht vorhanden
				if (!coDienstreisezeit.moveFirst())
				{
					coDienstreisezeit.createNew(m_coBuchung.getID());
				}
				
				// Zeiten übertragen und speichern
				if (!coDienstreisezeit.isEditing())
				{
					coDienstreisezeit.begin();
				}
				coDienstreisezeit.setReisezeit(m_coBuchung.getField(CoDienstreisezeit.getResIdReisezeit()).getValue());
				coDienstreisezeit.setProjektzeit(m_coBuchung.getField(CoDienstreisezeit.getResIdProjektzeit()).getValue());
				coDienstreisezeit.save();
				
				continue;
			}
			// neuer Tag
			else if (status == IBusinessObject.statusAdded)
			{
				continue;
			}
		} while (m_coBuchung.moveNext());
	}


	/**
	 * Co setzen
	 * 
	 * @param coDienstreise
	 * @throws Exception
	 */
	private void initData(CoDienstreise coDienstreise) throws Exception {
		
		m_coDienstreise = coDienstreise;
		if (!m_coDienstreise.isEditing())
		{
			m_coDienstreise.begin();
		}
		
		// Daten zu den Einzeltagen über Buchungen laden
		m_coBuchung = new CoBuchung();
		m_coBuchung.loadByDienstreiseID(m_coDienstreise.getID());
		m_coBuchung.begin(); // comitten, um Änderungen prüfen zu können
		m_coBuchung.commit();
		m_coBuchung.begin();
		
		// weitere Initialisierungen
		m_coDienstreise.initHinweisArbeitszeit(m_coBuchung);
		m_isDG = m_coBuchung.getBuchungsartID() == CoBuchungsart.ID_DIENSTGANG;

		// Daten der GUI zuweisen
		setData(m_coDienstreise);
		m_table.setData(m_coBuchung);
	}


	/**
	 * Controls initialisieren
	 * @throws Exception 
	 */
	private void initControls() throws Exception {
		m_comboPerson = (ComboControl) findControl(getResID() + ".personid");

		m_comboLand = (ComboControl) findControl(getResID() + ".landid");
		m_comboZiel = (ComboControl) findControl(getResID() + ".zielid");
		m_comboZweck = (ComboControl) findControl(getResID() + ".zweckid");
		m_tfZiel = (TextControl) findControl(getResID() + ".ziel");
		m_tfThema = (TextControl) findControl(getResID() + ".thema");

		m_comboKunde = (ComboControl) findControl(getResID() + ".kundeid");
		m_comboAuftrag = (ComboControl) findControl(getResID() + ".auftragid");
		m_comboAbruf = (ComboControl) findControl(getResID() + ".abrufid");
		m_comboKostenstelle = (ComboControl) findControl(getResID() + ".kostenstelleid");

		m_checkUebernachtung = (BooleanControl) findControl(getResID() + ".uebernachtung");
		m_checkUebernachtungUeberKunde = (BooleanControl) findControl(getResID() + ".hotelueberkunde");
		m_tfHotel = (TextControl) findControl(getResID() + ".hotel");

		m_checkDienstwagen = (BooleanControl) findControl(getResID() + ".dienstwagen");
		m_checkMietwagen = (BooleanControl) findControl(getResID() + ".mietwagen");
		m_checkPrivatPkw = (BooleanControl) findControl(getResID() + ".privatpkw");
		m_checkBahn = (BooleanControl) findControl(getResID() + ".bahn");
		m_checkTaxi = (BooleanControl) findControl(getResID() + ".taxi");
		m_checkFlugzeug = (BooleanControl) findControl(getResID() + ".flugzeug");
		m_checkFahrtKunde = (BooleanControl) findControl(getResID() + ".fahrtueberkunde");
		m_tfMitfahrt = (TextControl) findControl(getResID() + ".mitfahrt");

		m_checkHinweise = (BooleanControl) findControl(getResID() + ".hinweisegelesen");
		m_tfHinweise = (TextControl) findControl(getResID() + ".hinweise");
		m_tfBemerkung = (TextControl) findControl(getResID() + ".bemerkung");
		m_tfBemerkungAl = (TextControl) findControl(getResID() + ".bemerkungal");

		m_tfGeaendertAm = (TextControl) findControl(getResID() + ".geaendertam");
		m_comboGeaendertVon = (ComboControl) findControl(getResID() + ".geaendertvonid");
	}


	/**
	 * Listener hinzufügen
	 */
	private void initListener() {
		IValueChangeListener valueChangeListener;
		
		
		// ValueChangeListener, um Combos zu aktualisieren und Änderungen zu dokumentieren
		valueChangeListener = new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				DialogDienstreise.this.valueChanged(control);
			}
		};

		m_comboLand.setValueChangeListener(valueChangeListener);
		m_comboZiel.setValueChangeListener(valueChangeListener);
		m_comboZweck.setValueChangeListener(valueChangeListener);
		m_tfZiel.setValueChangeListener(valueChangeListener);
		m_tfThema.setValueChangeListener(valueChangeListener);

		m_comboKunde.setValueChangeListener(valueChangeListener);
		m_comboAuftrag.setValueChangeListener(valueChangeListener);
		m_comboAbruf.setValueChangeListener(valueChangeListener);
		m_comboKostenstelle.setValueChangeListener(valueChangeListener);
		
		m_checkUebernachtung.setValueChangeListener(valueChangeListener);
		m_checkUebernachtungUeberKunde.setValueChangeListener(valueChangeListener);
		m_tfHotel.setValueChangeListener(valueChangeListener);

		m_checkDienstwagen.setValueChangeListener(valueChangeListener);
		m_checkMietwagen.setValueChangeListener(valueChangeListener);
		m_checkPrivatPkw.setValueChangeListener(valueChangeListener);
		m_checkBahn.setValueChangeListener(valueChangeListener);
		m_checkTaxi.setValueChangeListener(valueChangeListener);
		m_checkFlugzeug.setValueChangeListener(valueChangeListener);
		m_checkFahrtKunde.setValueChangeListener(valueChangeListener);
		m_tfMitfahrt.setValueChangeListener(valueChangeListener);
		
		m_checkHinweise.setValueChangeListener(valueChangeListener);
		m_tfHinweise.setValueChangeListener(valueChangeListener);
//		m_tfBemerkung.setValueChangeListener(valueChangeListener);
//		m_tfBemerkungAl.setValueChangeListener(valueChangeListener);
	}


	/**
	 * Tabelle mit den Buchungen initialisieren
	 * 
	 * @throws Exception
	 */
	private void initTable() throws Exception {
		m_table = new SortedTableControl(findControl("spread.dialog.dienstreise.zeiten")) {
			
			@Override
			protected void endEditing(Object bookmark, IField fld) throws Exception {
				super.endEditing(bookmark, fld);
				
				// Arbeitszeit prüfen
				m_coDienstreise.updateHinweisArbeitszeit(m_coBuchung);
				m_tfHinweise.refresh(reasonDataChanged, null);
			}
		};
	}
	

	/**
	 * Bei geänderten Werten ggf. die anderen Combos aktualisieren
	 * @param control 
	 * 
	 * @throws Exception
	 */
	private void valueChanged(IControl control) throws Exception {
		
		// Update der Items
		if (control == null) // Initialisierung
		{
			refreshItemsComboKunde();
			refreshItemsComboAuftrag();
			refreshItemsComboAbruf();
			refreshItemsComboKostenstelle();
			m_comboErsteAuswahl = null;
		}
		else if (control.equals(m_comboKunde))
		{
			// wenn ein Kunde ausgewählt wurde, setze das Kunden-Textfeld auf null
//			m_coDienstreise.setKunde(null);
			
			refreshItemsComboAuftrag();
			refreshItemsComboAbruf();
			refreshItemsComboKostenstelle();
			m_comboErsteAuswahl = null;
		}
		else if (control.equals(m_comboAuftrag))
		{
			// doppelte Auswahl, wenn die Einträge nicht zusammenpassen, werden sie beim ersten mal gelöscht und beim zweiten die neuen gesetzt
			refreshItemsComboAbruf();
			refreshItemsComboKostenstelle();
			refreshItemsComboAbruf();
			refreshItemsComboKostenstelle();
			m_comboErsteAuswahl = (ComboControl) control;
		}
		else if (control.equals(m_comboAbruf))
		{
			// doppelte Auswahl, wenn die Einträge nicht zusammenpassen, werden sie beim ersten mal gelöscht und beim zweiten die neuen gesetzt
			refreshItemsComboAuftrag();
			refreshItemsComboKostenstelle();
			refreshItemsComboAuftrag();
			refreshItemsComboKostenstelle();
			m_comboErsteAuswahl = (ComboControl) control;
		}
		else if (control.equals(m_comboKostenstelle))
		{
			refreshItemsComboAuftrag();
			refreshItemsComboAbruf();
			m_comboErsteAuswahl = (ComboControl) control;
		}
		else if (control.equals(m_comboLand))
		{
			m_coDienstreise.updateHinweisLand();
			m_tfHinweise.refresh(reasonDataChanged, null);
		}
//		else // Combo Stundenart
//		{
////			return;
//		}

		// Kunde auswählen, falls eine andere Comobobox ausgewählt wurde
		updateKunde(m_coDienstreise.getAuftragID());

		m_coDienstreise.updateGeaendertVonAm();
		refresh(reasonDataChanged, null);
	}


	/**
	 * Aktualisieren der Items der Ziele-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboZiele() throws Exception {
		CoZielDienstreise coZielDienstreise;
		
		coZielDienstreise = new CoZielDienstreise();
		coZielDienstreise.loadByBuchungsartID(m_coBuchung.getBuchungsartID());
		
		// refreshItems in dieser Klasse nur für Projekte ausgelegt
		UniFormWithSaveLogic.refreshItems(m_comboZiel, coZielDienstreise, m_coDienstreise.getFieldZielID());
	}
	

	/**
	 * Aktualisieren der Items der Auftrags-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboKunde() throws Exception {
		IField fieldKunde;
		CoKunde coKunde;
		
		coKunde = new CoKunde();
		coKunde.loadAllWithProjekte(CoStatusProjekt.STATUSID_LAUFEND, false); 
		
		fieldKunde = m_coDienstreise.getFieldKundeID();
		
		refreshItems(m_comboKunde, fieldKunde, coKunde);
	}
	

	/**
	 * Aktualisieren der Items der Auftrags-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboAuftrag() throws Exception {
		IField fieldAuftrag;
		CoAuftrag coAuftrag;
		
		coAuftrag = new CoAuftrag();
		coAuftrag.loadItems(m_coDienstreise.getKundeID(), 
				m_coDienstreise.getAbrufID(), m_coDienstreise.getKostenstelleID(), true);
		
		fieldAuftrag = m_coDienstreise.getFieldAuftragID();
		coAuftrag.loadItems(m_coDienstreise.getKundeID(), 
				m_coDienstreise.getAbrufID(), m_coDienstreise.getKostenstelleID(), true);
		

		refreshItems(m_comboAuftrag, fieldAuftrag, coAuftrag);
	}
	

	/**
	 * Aktualisieren der Items der Abruf-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboAbruf() throws Exception {
		IField fieldAbruf;
		CoAbruf coAbruf;
		
		coAbruf = new CoAbruf();
		coAbruf.loadItems(m_coDienstreise.getKundeID(),
			m_coDienstreise.getAuftragID(), m_coDienstreise.getKostenstelleID());
		
		fieldAbruf = m_coDienstreise.getFieldAbrufID();
		
		refreshItems(m_comboAbruf, fieldAbruf, coAbruf);
	}
	

	/**
	 * Aktualisieren der Items der Kostenstellen-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboKostenstelle() throws Exception {
		IField fieldKostenstelle;
		CoKostenstelle coKostenstelle;
		
		coKostenstelle = new CoKostenstelle();
		coKostenstelle.loadItems(m_coDienstreise.getKundeID(),
				m_coDienstreise.getAuftragID(), m_coDienstreise.getAbrufID(), 0);
		
		fieldKostenstelle = m_coDienstreise.getFieldKostenstelleID();
		
		refreshItems(m_comboKostenstelle, fieldKostenstelle, coKostenstelle);
	}


	/**
	 * Items aktualisieren, neue Items setzen wenn noch keine vorhanden sind oder sie ungültig sind
	 * 
	 * @param comboControl
	 * @param field
	 * @param co
	 * @throws Exception
	 */
	private void refreshItems(ComboControl comboControl, IField field, AbstractCacheObject co) throws Exception {
		if (comboControl.equals(m_comboErsteAuswahl))
		{
			return;
		}
		
		if (!checkItems(field, co))
		{
			UniFormWithSaveLogic.refreshItems(comboControl, co, field);
		}
		
		updateItem(field, co);
	}


	/**
	 * Items einer Combobox prüfen.<br>
	 * 
	 * @param field
	 * @param co
	 * @return noch keine Items vorhanden oder sie sind ungültig
	 */
	private boolean checkItems(IField field, AbstractCacheObject co) {
		
		if (field.getValue() == null)
		{
			return false;
		}
		else if (!co.moveToID(Format.getIntValue(field.getValue())))
		{
			return false;
		}
		
		return true;
	}


	/**
	 * Items einer Combobox aktualisieren.<br>
	 * Wenn nur ein Element vorhanden ist, wähle es aus.<br>
	 * Sonst prüfen, ob der ausgewählte Wert noch in der Comobox existiert.
	 * 
	 * @param field
	 * @param co
	 */
	private void updateItem(IField field, AbstractCacheObject co) {
		// Element nicht markieren, wenn es nur eine Kostenstelle für den Kunden gibt und kein zur Kostenstelle gehörender Abruf ausgewählt ist
		if (co.getRowCount() == 1 && (!(co instanceof CoKostenstelle) || m_comboAbruf.getField().getValue() != null))
		{
			field.setValue(co.getID());
		}
		else
		{
			if (!co.moveToID(Format.getIntValue(field.getValue())))
			{
				field.setValue(null);
			}
		}
	}
	

	/**
	 * Auswahl des Kunden aktualisieren
	 * 
	 * @param auftragID
	 * @throws Exception
	 */
	private void updateKunde(int auftragID) throws Exception {
		CoAuftrag coAuftrag;
		
		coAuftrag = new CoAuftrag();
		coAuftrag.loadByID(auftragID);
		
		if (coAuftrag.getRowCount() > 0 && coAuftrag.getKundeID() > 0)
		{
			m_coDienstreise.setKundeID(coAuftrag.getKundeID());
			updateItem(m_coDienstreise.getFieldKundeID(), CoKunde.getInstance());
		}
	}
	

	/**
	 * Nur einzelne Felder sind editierbar.
	 * 
	 * @see framework.cui.controls.base.BaseCompositeControl#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// nur wenn bereits eine Buchung geladen wurde
		if (m_coDienstreise == null)
		{
			return;
		}
		
		// je nach Status darf nichts bearbeitet werden
		if (m_statusGenehmigungID == CoStatusGenehmigung.STATUSID_GELOESCHT)
		{
			super.refresh(reasonDisabled, null);
			m_tfBemerkung.refresh(reasonEnabled, null);
			return;
		}
		
		// der AL darf bei der Genehmigung nur seine Bemerkung eintragen
		if (m_messageGruppeID == CoMessageGruppe.ID_AL)
		{
			super.refresh(reasonDisabled, null);
			m_tfBemerkungAl.refresh(reasonEnabled, null);
			return;
		}
		// Sekretariat darf nur die Bemerkung ändern
		else if (m_messageGruppeID == CoMessageGruppe.ID_SEKRETAERIN)
		{
			super.refresh(reasonDisabled, null);
			m_tfBemerkung.refresh(reasonEnabled, null);
			return;
		}
		else
		{
			// die anderen dürfen die AL-Bemerkung nicht ändern
			m_tfBemerkungAl.refresh(reasonDisabled, null);
			
			// aber die normale Bemerkung jederzeit, auch nach Genehmigung
			m_tfBemerkung.refresh(reasonEnabled, null);
		}

		
		// bei Übernachtung weitere Auswahlmöglichkeit
		if (m_coDienstreise.isUebernachtung())
		{
			m_tfHotel.refresh(reasonEnabled, null);
			m_checkUebernachtungUeberKunde.refresh(reasonEnabled, null);
		}
		else
		{
			m_tfHotel.refresh(reasonDisabled, null);
			m_checkUebernachtungUeberKunde.refresh(reasonDisabled, null);
		}
		
		// bei DG gibt es keine Übernachtung
		if (m_isDG)
		{
			m_checkUebernachtung.refresh(reasonDisabled, null);
			m_tfHotel.refresh(reasonDisabled, null);
			m_checkUebernachtungUeberKunde.refresh(reasonDisabled, null);
			
			m_checkMietwagen.refresh(reasonDisabled, null);
			m_checkBahn.refresh(reasonDisabled, null);
			m_checkTaxi.refresh(reasonDisabled, null);
			m_checkFlugzeug.refresh(reasonDisabled, null);
			m_checkFahrtKunde.refresh(reasonDisabled, null);
		}
		
		// Hinweise nur bei Prüfung aktivieren
		m_checkHinweise.refresh(m_validateCo && m_coDienstreise.hasHinweise() ? reasonEnabled : reasonDisabled, null);

		// nicht änderbare Einträge
		m_tfHinweise.refresh(reasonDisabled, null);
		m_comboPerson.refresh(reasonDisabled, null);
		m_tfGeaendertAm.refresh(reasonDisabled, null);
		m_comboGeaendertVon.refresh(reasonDisabled, null);
		
		// Tabelle mit den Tagen darf geändert werden
		m_table.refresh(reasonEnabled, null);
	}


}


