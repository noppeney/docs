package pze.ui.formulare.projektverwaltung;

import framework.Application;
import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.actions.IActionListener;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.refresh.IRefreshable;
import framework.business.interfaces.session.ISession;
import framework.ui.controls.ComboControl;
import framework.ui.controls.TextControl;
import framework.ui.form.Scroller;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.IFocusListener;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.navigation.NavigationBaseNode;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderProjekte;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAbrufKostenstelle;
import pze.business.objects.projektverwaltung.CoAbrufProjektmerkmal;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoKostenstelle;
import pze.business.objects.projektverwaltung.CoMitarbeiterProjekt;
import pze.business.objects.reftables.projektverwaltung.CoAbteilungKunde;
import pze.business.objects.reftables.projektverwaltung.CoAnfordererKunde;
import pze.business.objects.reftables.projektverwaltung.CoFachgebiet;
import pze.business.objects.reftables.projektverwaltung.CoProjektmerkmal;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.ComboRefreshItemsListener;
import pze.ui.formulare.UniFormWithSaveLogic;

/**
 * Formular für Abrufe
 * 
 * @author Lisiecki
 *
 */
public class FormAbruf extends FormProjekt {

	public static String RESID_ALLGEMEIN = "form.abruf.allgemein";

	// Formulare
	private static FormAbruf m_formAbruf;
//	private static FormDokumente m_formDokumente;

	private SortedTableControl m_tableProjektmerkmale;
	private SortedTableControl m_tableKostenstellen;
	private SortedTableControl m_selectedTable;
	
	private TextControl m_tfAbrufNr;
	private TextControl m_tfRevision;
	
	private ComboControl m_comboAbteilungKunde;
	private ComboControl m_comboAnfordererKunde;
//	private ComboControl m_comboPNr;
	private ComboControl m_comboZuordnung;
	private ComboControl m_comboPaket;

	private ComboControl m_comboFachgebiet;
	
	private TextControl m_tfDatumAbruf;
	private TextControl m_tfDatumTermin;
	private TextControl m_tfDatumFertigmeldung;
	private TextControl m_tfDatumFreigabeRechnungAG;
	private TextControl m_tfDatumSchlussrechnung;
	
	private TextControl m_tfBestellwert;
	private TextControl m_tfUvg;
	private TextControl m_tfSollstunden;
	private TextControl m_tfStartwert;
	private TextControl m_tfUeberbuchung;
	private ComboControl m_comboAbrechnungsart;
	
//	private TextControl m_tfMeldungXProzentIntern;
//	private TextControl m_tfMeldungXProzentKunde;
	private TextControl m_tfDatumMeldungVersendet;
	
	private TextControl m_tfDatumBerechnetBis;
	
	private ComboControl m_comboStatus;
	
	private CoAbruf m_coAbruf;
	private CoAbrufProjektmerkmal m_coAbrufProjektmerkmal;
	private CoAbrufKostenstelle m_coAbrufKostenstelle;

	private IActionListener m_addlistener;
	private IActionListener m_deletelistener;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param coAbruf	 zu editierendes CacheObject
	 * @throws Exception
	 */
	public FormAbruf(Object parent, CoAbruf coAbruf) throws Exception {
		super(parent, RESID_ALLGEMEIN);
		init(coAbruf);
	}


	protected void init(CoAbruf coAbruf) throws Exception {
		m_coAbruf = coAbruf;

		// Kostenstellen laden, falls neue eingegeben wurden
		loadKostenstellen();

		setData(m_coAbruf);
		
		initControls();
		initListener();

		initTableProjektmerkmale();
		initTableKostenstellen();

		if (UserInformation.getInstance().isProjektverwaltung() 
				|| (UserInformation.getInstance().isKGG() && m_coAbruf.isKGG()))
		{
			m_addlistener = new AddListener();
			m_deletelistener = new DeleteListener();
		}
		
		refresh(reasonDisabled, null);
	}


	/**
	 * Controls initialisieren
	 * @throws Exception 
	 */
	private void initControls() throws Exception {
		String resID;
		
		resID = "form.abruf";
		super.initControls(resID);
		
		m_tfAbrufNr = (TextControl) findControl(resID + ".abrufnr");
		m_tfEdvNr = (TextControl) findControl(resID + ".edvnr");
		m_tfRevision = (TextControl) findControl(resID + ".revision");
		m_tfBeschreibung = (TextControl) findControl(resID + ".beschreibung");
		
		m_comboAbteilungKunde = (ComboControl) findControl(resID + ".abteilungkundeid");
		m_comboAnfordererKunde = (ComboControl) findControl(resID + ".anfordererkundeid");
//		m_comboPNr = (ComboControl) findControl(resID + ".pnummerid");
		m_comboZuordnung = (ComboControl) findControl(resID + ".zuordnungid");
		m_comboPaket = (ComboControl) findControl(resID + ".paketid");
		
		m_comboAbteilungsleiter = (ComboControl) findControl(resID + ".abteilungsleiterid");
		m_comboProjektleiter = (ComboControl) findControl(resID + ".projektleiterid");
		m_comboProjektleiter2 = (ComboControl) findControl(resID + ".projektleiterid2");
		m_comboFachgebiet = (ComboControl) findControl(resID + ".fachgebietid");
		m_comboFachgebiet.setFocusListener(new ComboRefreshItemsListener(m_comboFachgebiet, m_coAbruf.getFieldFachgebiet(), new CoFachgebiet()));
		
		m_tfDatumAbruf = (TextControl) findControl(resID + ".datumabruf");
		m_tfDatumTermin = (TextControl) findControl(resID + ".datumtermin");
		m_tfDatumFertigmeldung = (TextControl) findControl(resID + ".datumfertigmeldung");
		m_tfDatumFreigabeRechnungAG = (TextControl) findControl(resID + ".datumfreigaberechnungag");
		m_tfDatumSchlussrechnung = (TextControl) findControl(resID + ".datumschlussrechnung");
		
		m_tfBestellwert = (TextControl) findControl(resID + ".bestellwert");
		m_tfUvg = (TextControl) findControl(resID + ".uvg");
		m_tfPuffer = (TextControl) findControl(resID + ".puffer");
		m_tfSollstunden = (TextControl) findControl(resID + ".sollstunden");
		m_tfStartwert = (TextControl) findControl(resID + ".startwert");
		m_tfUeberbuchung = (TextControl) findControl(resID + ".ueberbuchung");
		m_comboAbrechnungsart = (ComboControl) findControl(resID + ".abrechnungsartid");
		
//		m_tfMeldungXProzentIntern = (TextControl) findControl(resID + ".meldungxprozentintern");
//		m_tfMeldungXProzentKunde = (TextControl) findControl(resID + ".meldungxprozentkunde");
		m_tfDatumMeldungVersendet = (TextControl) findControl(resID + ".datummeldungversendet");
		m_tfDatumBerechnetBis = (TextControl) findControl(resID + ".datumberechnetbis");
		
		m_comboStatus = (ComboControl) findControl(resID + ".statusid");
		
		m_tfBemerkung = (TextControl) findControl(resID + ".bemerkung");
	}


	/**
	 * Listener hinzufügen
	 */
	private void initListener() {
		IValueChangeListener valueChangeListener;
		IFocusListener focusListener;

		
		// ValueChangeListener, um Änderungen zu protokollieren
		valueChangeListener = new IValueChangeListener() {

			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				FormAbruf.this.valueChanged();
			}
		};

		focusListener = new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try 
				{
					FormAbruf.this.valueChanged();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void focusGained(IControl control) {
				try 
				{
					FormAbruf.this.focusGained(control);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		
		// normale ValueChangeListener für Protokollierung
		m_tfAbrufNr.setValueChangeListener(valueChangeListener);
		m_tfEdvNr.setValueChangeListener(valueChangeListener);
		m_tfRevision.setValueChangeListener(valueChangeListener);
		m_tfBeschreibung.setValueChangeListener(valueChangeListener);
		
		m_comboAbteilungKunde.setValueChangeListener(valueChangeListener);
		m_comboAnfordererKunde.setValueChangeListener(valueChangeListener);
//		m_comboPNr.setValueChangeListener(valueChangeListener);
		m_comboZuordnung.setValueChangeListener(valueChangeListener);
		m_comboPaket.setValueChangeListener(valueChangeListener);
		
		m_comboAbteilungsleiter.setValueChangeListener(valueChangeListener);
		m_comboProjektleiter.setValueChangeListener(valueChangeListener);
		m_comboProjektleiter2.setValueChangeListener(valueChangeListener);
		m_comboFachgebiet.setValueChangeListener(valueChangeListener);
		
		m_tfDatumAbruf.setValueChangeListener(valueChangeListener);
		m_tfDatumTermin.setValueChangeListener(valueChangeListener);
		m_tfDatumFertigmeldung.setValueChangeListener(valueChangeListener);
		m_tfDatumFreigabeRechnungAG.setValueChangeListener(valueChangeListener);
		m_tfDatumSchlussrechnung.setValueChangeListener(valueChangeListener);
		
		m_tfBestellwert.setFocusListener(focusListener);
		m_tfUvg.setFocusListener(focusListener);
		m_tfPuffer.setFocusListener(focusListener);
		m_tfSollstunden.setFocusListener(focusListener);
		m_tfStartwert.setFocusListener(focusListener);
		m_tfUeberbuchung.setFocusListener(focusListener);
		m_comboAbrechnungsart.setValueChangeListener(valueChangeListener);
		
//		m_tfMeldungXProzentIntern.setValueChangeListener(valueChangeListener);
//		m_tfMeldungXProzentKunde.setValueChangeListener(valueChangeListener);
		m_tfDatumMeldungVersendet.setValueChangeListener(valueChangeListener);
		m_tfDatumBerechnetBis.setValueChangeListener(valueChangeListener);
		
		m_comboProzentmeldung.setValueChangeListener(valueChangeListener);
		m_comboProzentmeldung2.setValueChangeListener(valueChangeListener);
		m_comboIntervallProjektverfolgung.setValueChangeListener(valueChangeListener);
		m_comboIntervallProjektbericht.setValueChangeListener(valueChangeListener);
		m_isProjektverfolgungAktiv.setValueChangeListener(valueChangeListener);
		m_isProjektbericht.setValueChangeListener(valueChangeListener);
		m_isMessage8Stunden.setValueChangeListener(valueChangeListener);

		m_tfBestellwertOriginal.setValueChangeListener(valueChangeListener);
		m_tfDatumTerminOriginal.setValueChangeListener(valueChangeListener);
		
		m_comboStatus.setValueChangeListener(valueChangeListener);
		
		m_tfBemerkung.setValueChangeListener(valueChangeListener);
		m_tfBemerkung.setFocusListener(focusListener);
	}


	private void initTableProjektmerkmale() throws Exception {
		m_coAbrufProjektmerkmal = new CoAbrufProjektmerkmal();
		// Daten nur laden, wenn die Berechtigungen stimmen
		if (UserInformation.getInstance().isProjektmerkmalAnsicht())
		{
			m_coAbrufProjektmerkmal.loadByAbrufID(m_coAbruf.getID());
		}
		
		m_coAbruf.addChild(m_coAbrufProjektmerkmal);
		if (m_coAbruf.isEditing() && !m_coAbrufProjektmerkmal.isEditing())
		{
			m_coAbrufProjektmerkmal.begin();
		}
		
		m_tableProjektmerkmale = new SortedTableControl(findControl("spread.abruf.allgemein.projektmerkmal"));
		m_tableProjektmerkmale.setData(m_coAbrufProjektmerkmal);
		
		// Listener zum Zwischenspeichern der aktuellen Tabelle für add-/delete-Listener und zur Aktualisierung der ComboBox
		m_tableProjektmerkmale.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
			}
			
			@Override
			public void focusGained(IControl control) {
				m_selectedTable = m_tableProjektmerkmale;
				
				try
				{
					// Daten der ComboBox aktualisieren
					IField field;
					CoProjektmerkmal coProjektmerkmal;

					coProjektmerkmal = CoProjektmerkmal.getInstance();
					coProjektmerkmal.loadAll();

					field = m_coAbrufProjektmerkmal.getFieldProjektmerkmal();
					
					refreshItems(m_tableProjektmerkmale, m_coAbrufProjektmerkmal, field, coProjektmerkmal);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		});
	}


	private void initTableKostenstellen() throws Exception {
		m_coAbrufKostenstelle = new CoAbrufKostenstelle();
		m_coAbrufKostenstelle.loadByAbrufID(m_coAbruf.getID());
		m_coAbruf.addChild(m_coAbrufKostenstelle);
		
		if (m_coAbruf.isEditing() && !m_coAbrufKostenstelle.isEditing())
		{
			m_coAbrufKostenstelle.begin();
		}
		
		m_tableKostenstellen = new SortedTableControl(findControl("spread.abruf.allgemein.kostenstelle"));
		m_tableKostenstellen.setData(m_coAbrufKostenstelle);
		
		// Listener zum Zwischenspeichern der aktuellen Tabelle für add-/delete-Listener und zur Aktualisierung der ComboBox
		m_tableKostenstellen.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
			}
			
			@Override
			public void focusGained(IControl control) {
				m_selectedTable = m_tableKostenstellen;
				
				try
				{
					// Daten der ComboBox aktualisieren
					IField field;
					CoKostenstelle coKostenstelle;

					coKostenstelle = new CoKostenstelle();
					coKostenstelle.loadByKundeID(m_coAbruf.getKundeID(), m_coAbruf.getCoKostenstelle().getIDs());

					field = m_coAbrufKostenstelle.getFieldKostenstelle();
					
					// Kostenstellen aktualisieren
					loadKostenstellen();
					refreshItems(m_tableKostenstellen, m_coAbrufKostenstelle, field, coKostenstelle);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * 
	 */
	@Override
	public void setData(IBusinessObject data) throws Exception {
		super.setData(data);
		
		refreshItemsComboAlByAbteilung();
	}
	
	
	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param node Knoten in Navigation
	 * @throws Exception
	 */
	public static void open(ISession session, NavigationBaseNode node) throws Exception {
		int id = (node==null ? -1 : node.getID());
		
		open(session, id);
	}

	
	/**
	 * neues (leeres) Formular öffnen (oder selektieren) und Auftrag auswählen
	 * 
	 * @param session
	 * @param node
	 * @param auftragID 
	 * @throws Exception
	 */
	public static void openNew(ISession session, NavigationBaseNode node, int auftragID) throws Exception {
		CoAuftrag coAuftrag;
		CoAbruf coAbruf;
		
		open(session, node);
		
		coAuftrag = new CoAuftrag();
		coAuftrag.loadByID(auftragID);
		
		coAbruf = (CoAbruf) m_formAbruf.getCo();
		coAbruf.setAuftragID(auftragID);
		coAbruf.setAbteilungsleiterID(coAuftrag.getAbteilungsleiterID());
		
		m_formAbruf.setData(coAbruf);
		m_formAbruf.refresh(reasonDataChanged, null);
	}
	
	
	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @param node Knoten in Navigation
	 * @param id 
	 * @return dieses Formular
	 * @throws Exception
	 */
	public static FormProjekt open(ISession session, int id) throws Exception {
		int projektleiterID;
		ITabFolder editFolder;
		ITabFolder sub;
		String key;
		Scroller scroller;
		ITabItem item;
		
		editFolder = session.getMainFrame().getEditFolder();
		key = getKey(id);
		item = editFolder.get(key);
		sub = null;
		
		if (item == null)
		{
			CoAbruf coAbruf = new CoAbruf();
			String name;

			if (id == -1)
			{
				coAbruf.createNew();
				key = getKey(coAbruf.getID());
				name = "<neuer Abruf>";
			}
			else
			{
				coAbruf.loadByID(id);
				name = coAbruf.getBezeichnung();
			}
						
			// prüfen, ob der Abruf geöffnet werden darf
			projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();
			if (projektleiterID > 0 && projektleiterID != coAbruf.getProjektleiterID() && projektleiterID != coAbruf.getProjektleiterID2()
					&& !CoMitarbeiterProjekt.isZugeordnet(0, id, projektleiterID))
			{
				return null;
			}

			
			item = editFolder.add(name, key, null,true);
			item.setBitmap(coAbruf.getNavigationBitmap());
			sub = item.getSubFolder();
			editFolder.setSelection(key);	

			// Abruf
			scroller = new Scroller(sub, "scroller.abruf.allgemein");
			m_formAbruf = new FormAbruf(scroller, coAbruf);
            scroller.setControl(m_formAbruf);
			sub.add("Allgemeine Daten", FormAbruf.RESID_ALLGEMEIN, scroller, false);
			m_formAbruf.setSubTabFolder(sub);

			// TODO neue Funktion Dokumente
			// Dokumente
//			m_formDokumente = new FormDokumente(sub, coAabruf, m_formAbruf);
//			sub.add("Dokumente", FormDokumente.RESID, m_formDokumente, false);			
//			m_formAbruf.addAdditionalForm(m_formDokumente);

			// Arbeitsplan
			m_formAbruf.addFormArbeitsplan(coAbruf);

			// Mitarbeiter
			m_formAbruf.addFormMitarbeiter(coAbruf);

			// Auswertung und Projektverfolgung für PL und Auswerter
			if (projektleiterID == 0 || (projektleiterID == coAbruf.getProjektleiterID() || projektleiterID == coAbruf.getProjektleiterID2()))
			{
				m_formAbruf.addFormProjektverfolgung(coAbruf);
				m_formAbruf.addFormAuswertung();
			}
			
			sub.setActivateSubFolder(true);
			sub.setSelection(RESID_ALLGEMEIN);
			
			item.setBitmap(coAbruf.getNavigationBitmap());
		}

		editFolder.setSelection(key);
		return m_formAbruf;
	}


	/**
	 * Kostenstellen laden, falls neue eingegeben wurden
	 */
	private void loadKostenstellen() throws Exception {
		Application.getRefTableLoader().updateRefItems("table." + CoKostenstelle.TABLE_NAME);
	}


	@Override
	protected void reloadCo() throws Exception {
		m_coAbruf.loadByID(m_coAbruf.getID());
	}


	@Override
	public void activate() {
		Action.get("file.new").addActionListener(m_addlistener);
		Action.get("edit.delete").addActionListener(m_deletelistener);

		super.activate();
	}
	
	
	@Override
	public void deactivate() {
		Action.get("file.new").removeActionListener(m_addlistener);
		Action.get("edit.delete").removeActionListener(m_deletelistener);

		super.deactivate();
	}


	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "abruf." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	
	
	/**
	 * Bei geänderten Werten speichern, wer wann die Änderungen gemacht hat
	 * 
	 * @throws Exception
	 */
	private void valueChanged() throws Exception {

		// Speichern von wem die Änderungen gemacht wurden
		m_coAbruf.setGeaendertVonID(UserInformation.getPersonID());
		m_coAbruf.setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Tab-Caption anpassen, Baum und RefTable-Items neu laden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	@Override
	public void doAfterSave() throws Exception {
		refreshTabItem();
		
		// Co neu laden wegen Berechnung Sollstunden
		reloadCo();
		refresh(reasonDataChanged, null);
		
		Application.getRefTableLoader().updateRefItems(getData().getResID());
		
		NavigationManager.getInstance().reloadRootNode(TreeLoaderProjekte.ROOT);
		
		// auf untergeordneten Formularen ggf. Daten neu laden, z. B. Verletzermeldungen
		for (UniFormWithSaveLogic addForm : additionalForms)
		{
			addForm.doAfterSave();
		}
	}


	/**
	 * Berechtigungen zur Bearbeitung prüfen
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// zusätzlich prüfen, ob Kostenstellen-Tabelle für KGG-Projekte aktiviert werden soll
		try 
		{
			// wenn bearbeitet werden soll
			if (reason == reasonEnabled)
			{
				if (UserInformation.getInstance().isKGG() && m_coAbruf.isKGG())
				{
					m_tableKostenstellen.refresh(reasonEnabled, null);
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}


	/**
	 * Aktualisieren der Items der Combos mit Kunden-Bezug 
	 * 
	 * @throws Exception
	 */
	protected void refreshItemsCombosByKunde() throws Exception {
		int kundeID;
		IField field;
		CoAbteilungKunde coAbteilungKunde;
		CoAnfordererKunde coAnfordererKunde;
		
		kundeID = m_coAbruf.getKundeID();
		
		coAbteilungKunde = new CoAbteilungKunde();
		coAbteilungKunde.loadByKundeID(kundeID);
		
		coAnfordererKunde = new CoAnfordererKunde();
		coAnfordererKunde.loadByKundeID(kundeID, m_coAbruf.getAnfordererKundeID());
		
		
		// Felder aktualisieren
		field = m_coAbruf.getFieldAbteilungKundeID();
		refreshItems(m_comboAbteilungKunde, coAbteilungKunde, field);

		field = m_coAbruf.getFieldAnfordererKundeID();
		refreshItems(m_comboAnfordererKunde, coAnfordererKunde, field);
	}
	

	@Override
	protected void refreshItemsComboAlByAbteilung() throws Exception {
		IField fieldAl;
		CoPerson coPerson;
		
		if (m_comboAbteilungsleiter == null)
		{
			return;
		}
		
		coPerson = new CoPerson();
		coPerson.loadItemsAbteilungsleiterByAuftrag(0);
		
		fieldAl = m_coAbruf.getFieldAbteilungsleiterID();
		
		refreshItems(m_comboAbteilungsleiter, coPerson, fieldAl);
	}


	/**
	 * Aktualisieren der Items der Combos mit Kunden-Bezug 
	 * 
	 * @throws Exception
	 */
	protected void refreshItemsCombosByAbteilungKunde() throws Exception {
//		int abteilungKundeID;
//		IField field;
//		CoPNummer coPNummer;
//		
//		abteilungKundeID = m_coAbruf.getAbteilungKundeID();
//		
//		coPNummer = new CoPNummer();
//		coPNummer.loadByAbteilungKundeID(abteilungKundeID);
//		
//		// Felder aktualisieren
//		field = m_coAbruf.getFieldPNr();
//		refreshItems(m_comboPNr, coPNummer, field);
	}
	

	/**
	 * Bezeichnungs-Datensatz hinzufügen
	 *
	 */
	class AddListener extends ActionAdapter
	{
		@Override
		public void activate(Object sender) throws Exception {
			
			if (m_selectedTable.equals(m_tableProjektmerkmale))
			{
				// max. 1 Projektmerkmal darf eingetragen werden
				if (m_coAbrufProjektmerkmal.getRowCount() == 1)
				{
					Messages.showErrorMessage("Kein weiteres Projektmerkmal möglich", "Es darf nur 1 Projektmerkmal angegeben werden.");
					return;
				}
				
				m_coAbrufProjektmerkmal.createNew(m_coAbruf.getID());
				m_tableProjektmerkmale.refresh(reasonDataAdded, m_coAbrufProjektmerkmal.getBookmark());
			}
			else if (m_selectedTable.equals(m_tableKostenstellen))
			{
				m_coAbrufKostenstelle.createNew(m_coAbruf.getID());
				m_tableKostenstellen.refresh(reasonDataAdded, m_coAbrufKostenstelle.getBookmark());
			}
			
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coAbruf.isEditing() && m_selectedTable != null
					// Projektverwaltung oder KGG-User für Kostenstellen
					&& (UserInformation.getInstance().isProjektverwaltung() || m_selectedTable.equals(m_tableKostenstellen));
		}
	}


	/**
	 * Bezeichnungs-Datensatz hinzufügen
	 *
	 */
	class DeleteListener extends ActionAdapter
	{
		@Override
		public void activate(Object sender) throws Exception {
			
			if (m_selectedTable.equals(m_tableProjektmerkmale))
			{
				m_coAbrufProjektmerkmal.moveTo(m_tableProjektmerkmale.getSelectedBookmark());
				m_coAbrufProjektmerkmal.delete();
			}
			else if (m_selectedTable.equals(m_tableKostenstellen))
			{
				m_coAbrufKostenstelle.moveTo(m_tableKostenstellen.getSelectedBookmark());
				m_coAbrufKostenstelle.delete();
			}

			refresh(IRefreshable.reasonDataChanged, null);
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coAbruf.isEditing() && m_selectedTable != null && m_selectedTable.getSelectedBookmark() != null;
		}
	}


}
