package pze.ui.formulare.projektverwaltung;

import framework.Application;
import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.actions.IActionListener;
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
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoAuftragKostenstelle;
import pze.business.objects.projektverwaltung.CoAuftragProjektmerkmal;
import pze.business.objects.projektverwaltung.CoKostenstelle;
import pze.business.objects.projektverwaltung.CoMitarbeiterProjekt;
import pze.business.objects.reftables.projektverwaltung.CoAbteilungKunde;
import pze.business.objects.reftables.projektverwaltung.CoAnfordererKunde;
import pze.business.objects.reftables.projektverwaltung.CoKunde;
import pze.business.objects.reftables.projektverwaltung.CoProjektmerkmal;
import pze.business.objects.reftables.projektverwaltung.CoStandortKunde;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.ComboRefreshItemsListener;
import pze.ui.formulare.UniFormWithSaveLogic;

/**
 * Formular für Aufträge
 * 
 * @author Lisiecki
 *
 */
public class FormAuftrag extends FormProjekt {

	public static String RESID_ALLGEMEIN = "form.auftrag.allgemein";
	public static String RESID_KOSTENSTELLE = "form.auftrag.kostenstelle";
	public static String RESID_ABRUF = "form.auftrag.abruf";

	// Formulare
//	private FormAuftrag m_formAuftrag;
//	private FormAuftragKostenstellen m_formAuftragKostenstellen;
	private FormAuftragAbrufe m_formAuftragAbrufe;
//	private FormDokumente m_formDokumente;
	
	private SortedTableControl m_tableProjektmerkmale;
	private SortedTableControl m_tableKostenstellen;
	private SortedTableControl m_selectedTable;

	private TextControl m_tfAuftragsNr;
	private TextControl m_tfBestellNr;
	private TextControl m_tfAngebotsNr;
	
	private ComboControl m_comboKunde;
	private ComboControl m_comboStandortKunde;
	private ComboControl m_comboAbteilungKunde;
	private ComboControl m_comboAnfordererKunde;
	
	private ComboControl m_comboAbteilung;
	
	private TextControl m_tfDatumbestellung;
	private TextControl m_tfDatumTermin;
	private TextControl m_tfDatumFertigmeldung;
	private TextControl m_tfDatumSchlussrechnung;
	private TextControl m_tfDatumAngebot;
	private TextControl m_tfDatumAuftragsbestaetigung;
	
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
	
	private CoAuftrag m_coAuftrag;
	private CoAuftragProjektmerkmal m_coAuftragProjektmerkmal;
	private CoAuftragKostenstelle m_coAuftragKostenstelle;

	private IActionListener m_addlistener;
	private IActionListener m_deletelistener;

	
	
	/**
	 * Konstruktor
	 * @param node			Navigationskonten
	 * @param parent		visueller Parent
	 * @param coAuftrag	 zu editierendes CacheObject
	 * @throws Exception
	 */
	public FormAuftrag(Object parent, CoAuftrag coAuftrag) throws Exception {
		super(parent, RESID_ALLGEMEIN);
		m_coAuftrag = coAuftrag;

		// Kostenstellen laden, falls neue eingegeben wurden
		loadKostenstellen();

		setData(m_coAuftrag);
		
		initControls();
		initListener();

		initTableProjektmerkmale();
		initTableKostenstellen();

		if (UserInformation.getInstance().isProjektverwaltung())
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
		
		resID = "form.auftrag";
		super.initControls(resID);

		m_tfAuftragsNr = (TextControl) findControl(resID + ".auftragsnr");
		m_tfBestellNr = (TextControl) findControl(resID + ".bestellnr");
		m_tfAngebotsNr = (TextControl) findControl(resID + ".angebotsnr");
		m_tfEdvNr = (TextControl) findControl(resID + ".edvnr");
		m_tfBeschreibung = (TextControl) findControl(resID + ".beschreibung");
		
		m_comboKunde = (ComboControl) findControl(resID + ".kundeid");
		m_comboKunde.setFocusListener(new ComboRefreshItemsListener(m_comboKunde, m_coAuftrag.getFieldKundeID(), new CoKunde()));
		
		m_comboStandortKunde = (ComboControl) findControl(resID + ".standortkundeid");
		m_comboAbteilungKunde = (ComboControl) findControl(resID + ".abteilungkundeid");
		m_comboAnfordererKunde = (ComboControl) findControl(resID + ".anfordererkundeid");

		m_comboProjektleiter = (ComboControl) findControl(resID + ".projektleiterid");
		m_comboProjektleiter2 = (ComboControl) findControl(resID + ".projektleiterid2");
		m_comboAbteilung = (ComboControl) findControl(resID + ".abteilungid");
		m_comboAbteilungsleiter = (ComboControl) findControl(resID + ".abteilungsleiterid");
		
		m_tfDatumAngebot = (TextControl) findControl(resID + ".datumangebot");
		m_tfDatumAuftragsbestaetigung= (TextControl) findControl(resID + ".datumauftragsbestaetigung");
		m_tfDatumbestellung = (TextControl) findControl(resID + ".datumbestellung");
		m_tfDatumTermin = (TextControl) findControl(resID + ".datumtermin");
		m_tfDatumFertigmeldung = (TextControl) findControl(resID + ".datumfertigmeldung");
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
				FormAuftrag.this.valueChanged();
			}
		};

		focusListener = new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try 
				{
					FormAuftrag.this.valueChanged();
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
					FormAuftrag.this.focusGained(control);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		

		// normale ValueChangeListener für Protokollierung
		m_tfAuftragsNr.setValueChangeListener(valueChangeListener);
		m_tfBestellNr.setValueChangeListener(valueChangeListener);
		m_tfAngebotsNr.setValueChangeListener(valueChangeListener);
		m_tfEdvNr.setValueChangeListener(valueChangeListener);
		m_tfBeschreibung.setValueChangeListener(valueChangeListener);
		
		m_comboKunde.setValueChangeListener(valueChangeListener);
		m_comboStandortKunde.setValueChangeListener(valueChangeListener);
		m_comboAbteilungKunde.setValueChangeListener(valueChangeListener);
		m_comboAnfordererKunde.setValueChangeListener(valueChangeListener);
		
		m_comboProjektleiter.setValueChangeListener(valueChangeListener);
		m_comboProjektleiter2.setValueChangeListener(valueChangeListener);
		m_comboAbteilung.setValueChangeListener(valueChangeListener);
		m_comboAbteilungsleiter.setValueChangeListener(valueChangeListener);
		
		m_tfDatumbestellung.setValueChangeListener(valueChangeListener);
		m_tfDatumTermin.setValueChangeListener(valueChangeListener);
		m_tfDatumFertigmeldung.setValueChangeListener(valueChangeListener);
		m_tfDatumSchlussrechnung.setValueChangeListener(valueChangeListener);
		m_tfDatumAngebot.setValueChangeListener(valueChangeListener);
		m_tfDatumAuftragsbestaetigung.setValueChangeListener(valueChangeListener);
		
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
		m_coAuftragProjektmerkmal = new CoAuftragProjektmerkmal();
		// Daten nur laden, wenn die Berechtigungen stimmen
		if (UserInformation.getInstance().isProjektmerkmalAnsicht())
		{
			m_coAuftragProjektmerkmal.loadByAuftragID(m_coAuftrag.getID());
		}

		m_coAuftrag.addChild(m_coAuftragProjektmerkmal);
		if (m_coAuftrag.isEditing() && !m_coAuftragProjektmerkmal.isEditing())
		{
			m_coAuftragProjektmerkmal.begin();
		}
		
		m_tableProjektmerkmale = new SortedTableControl(findControl("spread.auftrag.allgemein.projektmerkmal"));
		m_tableProjektmerkmale.setData(m_coAuftragProjektmerkmal);
		
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

					field = m_coAuftragProjektmerkmal.getFieldProjektmerkmal();
					
					refreshItems(m_tableProjektmerkmale, m_coAuftragProjektmerkmal, field, coProjektmerkmal);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		});
	}


	private void initTableKostenstellen() throws Exception {
		m_coAuftragKostenstelle = new CoAuftragKostenstelle();
		m_coAuftragKostenstelle.loadByAuftragID(m_coAuftrag.getID());
		m_coAuftrag.addChild(m_coAuftragKostenstelle);
		
		if (m_coAuftrag.isEditing() && !m_coAuftragKostenstelle.isEditing())
		{
			m_coAuftragKostenstelle.begin();
		}
		
		m_tableKostenstellen = new SortedTableControl(findControl("spread.auftrag.allgemein.kostenstelle"));
		m_tableKostenstellen.setData(m_coAuftragKostenstelle);
		
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
					coKostenstelle.loadByKundeID(m_coAuftrag.getKundeID(), m_coAuftrag.getCoKostenstelle().getIDs());

					field = m_coAuftragKostenstelle.getFieldKostenstelle();
					
					// Kostenstellen aktualisieren
					loadKostenstellen();
					refreshItems(m_tableKostenstellen, m_coAuftragKostenstelle, field, coKostenstelle);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
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
		String key;
		Scroller scroller;
		ITabItem item;
		FormAuftrag formAuftrag;
		
		editFolder = session.getMainFrame().getEditFolder();
		key = getKey(id);
		item = editFolder.get(key);
		formAuftrag = null;
		
		if (item == null)
		{
			CoAuftrag coAuftrag = new CoAuftrag();
			String name;

			if (id == -1 )
			{
				coAuftrag.createNew();
				key = getKey(coAuftrag.getID());
				name = "<neuer Auftrag>";
			}
			else
			{
				coAuftrag.loadByID(id);
				name = coAuftrag.getBezeichnung();
			}
			
			// prüfen, ob der Auftrag geöffnet werden darf
			projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();
			if (projektleiterID > 0 && projektleiterID != coAuftrag.getProjektleiterID() && projektleiterID != coAuftrag.getProjektleiterID2()
					&& !CoMitarbeiterProjekt.isZugeordnet(id, 0, projektleiterID))
			{
				return null;
			}
			
						
			item = editFolder.add(name, key, null, true);
			item.setBitmap(coAuftrag.getNavigationBitmap());
			ITabFolder subTabFolder = item.getSubFolder();
			editFolder.setSelection(key);	

			// Auftrag
			scroller = new Scroller(subTabFolder, "scroller.auftrag.allgemein");
          	formAuftrag = new FormAuftrag(scroller, coAuftrag);
            scroller.setControl(formAuftrag);
            subTabFolder.add("Allgemeine Daten", FormAuftrag.RESID_ALLGEMEIN, scroller, false);
            formAuftrag.setSubTabFolder(subTabFolder);

			// Kostenstellen
//			formAuftrag.addFormKostenstellen(coAuftrag);

			// Abrufe
			formAuftrag.addFormAbrufe(coAuftrag);

			// Arbeitsplan
			formAuftrag.addFormArbeitsplan(coAuftrag);

			// Mitarbeiter
			formAuftrag.addFormMitarbeiter(coAuftrag);

			// TODO neue Funktion Dokumente
			// Dokumente
//			m_formDokumente = new FormDokumente(sub, coAuftrag, m_formAuftrag);
//			sub.add("Dokumente", FormDokumente.RESID, m_formDokumente, false);			
//			m_formAuftrag.addAdditionalForm(m_formDokumente);

			// Projektverfolgung und Auswertung für PL und Auswerter
			if (projektleiterID == 0 || (projektleiterID == coAuftrag.getProjektleiterID() || projektleiterID == coAuftrag.getProjektleiterID2()))
			{
				formAuftrag.addFormProjektverfolgung(coAuftrag);
				formAuftrag.addFormAuswertung();
			}

			
			subTabFolder.setActivateSubFolder(true);
			subTabFolder.setSelection(RESID_ALLGEMEIN);
			
			item.setBitmap(coAuftrag.getNavigationBitmap());
		}

		editFolder.setSelection(key);
		return formAuftrag;
	}


//	/**
//	 * Formular Kostenstellen hinzufügen
//	 * 
//	 * @throws Exception
//	 */
//	private void addFormKostenstellen(CoAuftrag coAuftrag) throws Exception {
//		m_formAuftragKostenstellen = new FormAuftragKostenstellen(m_subTabFolder, coAuftrag, this);
//		m_subTabFolder.add("Kostenstellen", FormAuftragKostenstellen.RESID, m_formAuftragKostenstellen, false);			
//		addAdditionalForm(m_formAuftragKostenstellen);
//	}


	/**
	 * Formular Abrufe hinzufügen
	 * 
	 * @throws Exception
	 */
	private void addFormAbrufe(CoAuftrag coAuftrag) throws Exception {
		CoAbruf coAbruf;
		
		// Abrufe laden
		coAbruf = new CoAbruf();
		coAbruf.loadByAuftragID(coAuftrag.getIDs(), false);
		
		m_formAuftragAbrufe = new FormAuftragAbrufe(m_subTabFolder, coAbruf, this);
		m_subTabFolder.add("Abrufe", FormAuftragAbrufe.RESID, m_formAuftragAbrufe, false);			
		addAdditionalForm(m_formAuftragAbrufe);
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
		return "auftrag." + id;
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
		m_coAuftrag.setGeaendertVonID(UserInformation.getPersonID());
		m_coAuftrag.setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Tab-Caption anpassen, Co, Baum und RefTable-Items neu laden
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
	 * Aktualisieren der Items der Gruppen-Combo
	 * 
	 * @throws Exception
	 */
	protected void refreshItemsComboAlByAbteilung() throws Exception {
		IField fieldAl;
		CoPerson coPerson;
		
		coPerson = new CoPerson();
		coPerson.loadItemsAbteilungsleiter(m_coAuftrag.getAbteilungID());
		
		fieldAl = m_coAuftrag.getFieldAbteilungsleiterID();
		
		refreshItems(m_comboAbteilungsleiter, coPerson, fieldAl);
	}
	

	/**
	 * Aktualisieren der Items der Combos mit Kunden-Bezug 
	 * 
	 * @throws Exception
	 */
	protected void refreshItemsCombosByKunde() throws Exception {
		int kundeID;
		IField field;
		CoStandortKunde coStandortKunde;
		CoAbteilungKunde coAbteilungKunde;
		CoAnfordererKunde coAnfordererKunde;
		
		kundeID = m_coAuftrag.getKundeID();
		
		coStandortKunde = new CoStandortKunde();
		coStandortKunde.loadByKundeID(kundeID);
		
		coAbteilungKunde = new CoAbteilungKunde();
		coAbteilungKunde.loadByKundeID(kundeID);
		
		coAnfordererKunde = new CoAnfordererKunde();
		coAnfordererKunde.loadByKundeID(kundeID, m_coAuftrag.getAnfordererKundeID());
		
		// Felder aktualisieren
		field = m_coAuftrag.getFieldStandortKundeID();
		refreshItems(m_comboStandortKunde, coStandortKunde, field);

		field = m_coAuftrag.getFieldAbteilungKundeID();
		refreshItems(m_comboAbteilungKunde, coAbteilungKunde, field);

		field = m_coAuftrag.getFieldAnfordererKundeID();
		refreshItems(m_comboAnfordererKunde, coAnfordererKunde, field);
	}
	

	/**
	 * Kostenstellen laden, falls neue eingegeben wurden
	 */
	private void loadKostenstellen() throws Exception {
		Application.getRefTableLoader().updateRefItems("table." + CoKostenstelle.TABLE_NAME);
	}


	@Override
	protected void reloadCo() throws Exception {
		m_coAuftrag.loadByID(m_coAuftrag.getID());
	}


//	/**
//	 * Bezeichnungs-Datensatz hinzufügen
//	 *
//	 */
//	class AddListener extends ActionAdapter
//	{
//		@Override
//		public void activate(Object sender) throws Exception {
//			
//			// max. 1 Projektmerkmal darf eingetragen werden
//			if (m_coAuftragProjektmerkmal.getRowCount() == 1)
//			{
//				Messages.showErrorMessage("Kein weiteres Projektmerkmal möglich", "Es darf nur 1 Projektmerkmal angegeben werden.");
//				return;
//			}
//			
//			m_coAuftragProjektmerkmal.createNew(m_coAuftrag.getID());
////			m_coFilteredPersonFirma.filter();
//			m_tableProjektmerkmale.refresh(reasonDataAdded, m_coAuftragProjektmerkmal.getBookmark());
//			super.activate(sender);
//		}
//		
//		/* (non-Javadoc)
//		 * @see framework.business.action.ActionAdapter#getEnabled()
//		 */
//		@Override
//		public boolean getEnabled() {
//			return m_coAuftrag.isEditing();
//		}
//	}


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
				if (m_coAuftragProjektmerkmal.getRowCount() == 1)
				{
					Messages.showErrorMessage("Kein weiteres Projektmerkmal möglich", "Es darf nur 1 Projektmerkmal angegeben werden.");
					return;
				}
				
				m_coAuftragProjektmerkmal.createNew(m_coAuftrag.getID());
				m_tableProjektmerkmale.refresh(reasonDataAdded, m_coAuftragProjektmerkmal.getBookmark());
			}
			else if (m_selectedTable.equals(m_tableKostenstellen))
			{
				m_coAuftragKostenstelle.createNew(m_coAuftrag.getID());
				m_tableKostenstellen.refresh(reasonDataAdded, m_coAuftragKostenstelle.getBookmark());
			}
			
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coAuftrag.isEditing() && m_selectedTable != null;
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
				m_coAuftragProjektmerkmal.moveTo(m_tableProjektmerkmale.getSelectedBookmark());
				m_coAuftragProjektmerkmal.delete();
			}
			else if (m_selectedTable.equals(m_tableKostenstellen))
			{
				m_coAuftragKostenstelle.moveTo(m_tableKostenstellen.getSelectedBookmark());
				m_coAuftragKostenstelle.delete();
			}

			refresh(IRefreshable.reasonDataChanged, null);
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coAuftrag.isEditing() && m_selectedTable != null && m_selectedTable.getSelectedBookmark() != null;
		}
	}


}
