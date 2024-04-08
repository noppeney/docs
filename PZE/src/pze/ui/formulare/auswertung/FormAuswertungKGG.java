package pze.ui.formulare.auswertung;

import framework.business.interfaces.session.ISession;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Profile;
import pze.business.UserInformation;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungKGG;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoKostenstelle;
import pze.ui.controls.SortedTableControl;

/**
 * Formular für die Auswertung der KGG-Stunden
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungKGG extends FormAuswertung {

	public static final String RESID = "form.auswertung.kgg";
	
	private static final String RESID_TABLE = "spread.auswertung.kgg";
	
	
	protected ComboControl m_comboAuftrag;
	protected ComboControl m_comboAbruf;
	protected ComboControl m_comboKostenstelle;
//	protected ComboControl m_comboKostenstelle2;
	protected ComboControl m_comboBerichtsNr;

	protected ComboControl m_comboPersonen;
	protected ComboControl m_comboStundenart;

	//	protected ComboControl m_comboKunde;
//
//	protected ComboControl m_comboProjektleiter;
//	protected ComboControl m_comboAbteilungsleiter;
//	protected ComboControl m_comboFachgebiet;
//
//	protected ComboControl m_comboAbrechnungsart;
//	protected ComboControl m_comboStatus;
	
	protected IButtonControl m_btAlleAuftraege;
	protected IButtonControl m_btAlleAbrufe;
	protected IButtonControl m_btAlleKostenstellen;
//	protected IButtonControl m_btAlleKostenstellen2;
	protected IButtonControl m_btAlleBerichtsNr;
//	protected IButtonControl m_btAlleKunde;
	protected IButtonControl m_btAllePersonen;
	protected IButtonControl m_btAlleStundenarten;
//	protected IButtonControl m_btAlleStatus;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungKGG(Object parent) throws Exception {
		this(parent, RESID);
	}


	/**
	 * Konstruktor
	 * 
	 * @param parent
	 * @param resID
	 * @throws Exception
	 */
	public FormAuswertungKGG(Object parent, String resID) throws Exception {
		super(parent, resID);
		
		initFormular();
		
		// für die Auswahlliste nur die für KGG relevanten Items laden
		CoAuftrag coAuftrag;
		coAuftrag = new CoAuftrag();
		coAuftrag.loadItemsKGG();
		refreshItems(m_comboAuftrag, coAuftrag, m_comboAuftrag.getField());

		CoAbruf coAbruf;
		coAbruf = new CoAbruf();
		coAbruf.loadItemsKGG();
		refreshItems(m_comboAbruf, coAbruf, m_comboAbruf.getField());

		CoKostenstelle coKostenstelle;
		coKostenstelle = new CoKostenstelle();
		coKostenstelle.loadItemsKGG();
		refreshItems(m_comboKostenstelle, coKostenstelle, m_comboKostenstelle.getField());
		
		// laden funktioniert so nicht, da PSP nicht eindeutig ist, ggf. Auswahl als Text speichern
//		coKostenstelle.removeField(coKostenstelle.getFieldBezeichnung().getFieldDescription());
//		refreshItems(m_comboKostenstelle2, coKostenstelle, m_comboKostenstelle2.getField());

		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @throws Exception
	 */
	public static void open(ISession session) throws Exception {
		String key;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if (item == null)
		{
			m_formAuswertung = new FormAuswertungKGG(editFolder);
			item = editFolder.add(RESID, key, m_formAuswertung, true);
		}

		editFolder.setSelection(key);
	}


	protected void initFormular() throws Exception {
		super.initFormular();
		
//		CoPerson coPerson;
//		IValueChangeListener valueChangeListenerKunde, valueChangeListenerWti;
		

		
		
		m_comboAuftrag = (ComboControl) findControl(getResID() + ".auftragid");
		m_comboAbruf = (ComboControl) findControl(getResID() + ".abrufid");
		m_comboKostenstelle = (ComboControl) findControl(getResID() + ".kostenstelleid");
//		m_comboKostenstelle2 = (ComboControl) findControl(getResID() + ".kostenstelleid2");
		m_comboBerichtsNr = (ComboControl) findControl(getResID() + ".berichtsnrid");
		
		m_comboPersonen = (ComboControl) findControl(getResID() + ".personid");
		m_comboStundenart = (ComboControl) findControl(getResID() + ".stundenartid");

		
//		m_comboKunde = (ComboControl) findControl(getResID() + ".kundeid");
//		
//		m_comboProjektleiter = (ComboControl) findControl(getResID() + ".projektleiterid");
//		m_comboAbteilungsleiter = (ComboControl) findControl(getResID() + ".abteilungsleiterid");
//		m_comboFachgebiet = (ComboControl) findControl(getResID() + ".fachgebietid");
//		
//		m_comboAbrechnungsart = (ComboControl) findControl(getResID() + ".abrechnungsartid");
//		m_comboStatus = (ComboControl) findControl(getResID() + ".statusid");
//
//
//		// für die Auswahlliste der Projektleiter nur die Personen laden, die als Projektleiter angegeben sind
//		coPerson = new CoPerson();
//		coPerson.loadItemsProjektleiter();
//		refreshItems(m_comboProjektleiter, coPerson, m_comboProjektleiter.getField());

		
//		valueChangeListenerKunde = new IValueChangeListener() {
//
//			@Override
//			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
//				resetCombosEinschraenkungKunde(control, currentValue);
//			}
//		};
//
//		m_comboKunde.setValueChangeListener(valueChangeListenerKunde);
//		m_comboAbteilungKunde.setValueChangeListener(valueChangeListenerKunde);
//		m_comboAnfordererKunde.setValueChangeListener(valueChangeListenerKunde);
//				
//
//		valueChangeListenerWti = new IValueChangeListener() {
//
//			@Override
//			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
//				resetCombosEinschraenkungWti(control, currentValue);
//			}
//		};
//
//		m_comboProjektleiter.setValueChangeListener(valueChangeListenerWti);
//		m_comboAbteilung.setValueChangeListener(valueChangeListenerWti);
//		m_comboAbteilungsleiter.setValueChangeListener(valueChangeListenerWti);
//		m_comboFachgebiet.setValueChangeListener(valueChangeListenerWti);
				

//		valueChangeListenerAbrechnung = new IValueChangeListener() {
//
//			@Override
//			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
//				resetCombosEinschraenkungAbrechnung(control, currentValue);
//			}
//		};
//
//		m_comboAbrechnungsart.setValueChangeListener(valueChangeListenerAbrechnung);
//		m_comboStatus.setValueChangeListener(valueChangeListenerAbrechnung);
				

		
		m_btAlleAuftraege = (IButtonControl) findControl(getResID() + ".alleauftrag");
		m_btAlleAuftraege.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_comboAuftrag.getField().setValue(null);

				refresh(reasonDataChanged, null);
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btAlleAbrufe = (IButtonControl) findControl(getResID() + ".alleabruf");
		m_btAlleAbrufe.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_comboAbruf.getField().setValue(null);

				refresh(reasonDataChanged, null);
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btAlleKostenstellen = (IButtonControl) findControl(getResID() + ".allekostenstelle");
		m_btAlleKostenstellen.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_comboKostenstelle.getField().setValue(null);

				refresh(reasonDataChanged, null);
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
//		m_btAlleKostenstellen2 = (IButtonControl) findControl(getResID() + ".allekostenstelle2");
//		m_btAlleKostenstellen2.setSelectionListener(new ISelectionListener() {
//
//			@Override
//			public void selected(IControl control, Object params) {
//				m_comboKostenstelle2.getField().setValue(null);
//
//				refresh(reasonDataChanged, null);
//			}
//
//			@Override
//			public void defaultSelected(IControl control, Object params) {
//			}
//		});
		
		
		m_btAlleBerichtsNr = (IButtonControl) findControl(getResID() + ".alleberichtsnr");
		m_btAlleBerichtsNr.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_comboBerichtsNr.getField().setValue(null);

				refresh(reasonDataChanged, null);
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
//		m_btAlleKunde = (IButtonControl) findControl(getResID() + ".allekunde");
//		m_btAlleKunde.setSelectionListener(new ISelectionListener() {
//
//			@Override
//			public void selected(IControl control, Object params) {
//				resetCombosKunde();
//			}
//
//			@Override
//			public void defaultSelected(IControl control, Object params) {
//			}
//		});
//		
		
		m_btAllePersonen = (IButtonControl) findControl(getResID() + ".allepersonen");
		m_btAllePersonen.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_comboPersonen.getField().setValue(null);
				refresh(reasonDataChanged, null);
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btAlleStundenarten = (IButtonControl) findControl(getResID() + ".allestundenarten");
		m_btAlleStundenarten.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_comboStundenart.getField().setValue(null);
				refresh(reasonDataChanged, null);
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
//		
//		m_btAlleStatus = (IButtonControl) findControl(getResID() + ".allestatus");
//		m_btAlleStatus.setSelectionListener(new ISelectionListener() {
//
//			@Override
//			public void selected(IControl control, Object params) {
//				m_comboStatus.getField().setValue(null);
//				refresh(reasonDataChanged, null);
//			}
//
//			@Override
//			public void defaultSelected(IControl control, Object params) {
//			}
//		});
//		
	}

//
//	/**
//	 * Items nicht neu laden, da alle Abteilungen ausgewählt werden dürfen
//	 * 
//	 * @see pze.ui.formulare.auswertung.FormAuswertung#loadItemsComboAbteilung()
//	 */
//	protected void loadItemsComboAbteilung() throws Exception {
//	}
//
//
//	/**
//	 * Werte der Combos für die Einschränkung der Kunden auf NULL setzen und den übergebenen Wert setzen
//	 * 
//	 * @param currentValue 
//	 * @param control 
//	 */
//	private void resetCombosEinschraenkungKunde(IControl control, Object currentValue) {
//		resetCombosKunde();
//		
//		((ComboControl) control).getField().setValue(currentValue);
//		
//		refresh(reasonDataChanged, null);
//	}
//
//
//	/**
//	 * Werte der Combos für die Einschränkung der Kunden auf NULL setzen
//	 */
//	private void resetCombosKunde() {
//		m_comboKunde.getField().setValue(null);
//		m_comboAbteilungKunde.getField().setValue(null);
//		m_comboAnfordererKunde.getField().setValue(null);
//		
//		refresh(reasonDataChanged, null);
//	}
//
//
//	/**
//	 * Werte der Combos für die Einschränkung der WTI-Parameter auf NULL setzen und den übergebenen Wert setzen
//	 * 
//	 * @param currentValue 
//	 * @param control 
//	 */
//	private void resetCombosEinschraenkungWti(IControl control, Object currentValue) {
//		resetCombosWti();
//		
//		((ComboControl) control).getField().setValue(currentValue);
//		
//		refresh(reasonDataChanged, null);
//	}
//
//
//	/**
//	 * Werte der Combos für die Einschränkung der WTI-Parameter auf NULL setzen
//	 */
//	private void resetCombosWti() {
//		m_comboProjektleiter.getField().setValue(null);
//		m_comboAbteilung.getField().setValue(null);
//		m_comboAbteilungsleiter.getField().setValue(null);
//		m_comboFachgebiet.getField().setValue(null);
//		
//		refresh(reasonDataChanged, null);
//	}


	/**
	 * Werte der Combos für die Einschränkung der Abrechnung auf NULL setzen und den übergebenen Wert setzen
	 * 
	 * @param currentValue 
	 * @param control 
	 */
//	private void resetCombosEinschraenkungAbrechnung(IControl control, Object currentValue) {
//		resetCombosAbrechnung();
//		
//		((ComboControl) control).getField().setValue(currentValue);
//		
//		refresh(reasonDataChanged, null);
//	}


	/**
	 * Werte der Combos für die Einschränkung der Abrechnung auf NULL setzen
	 */
//	private void resetCombosAbrechnung() {
//		m_comboAbrechnungsart.getField().setValue(null);
//		m_comboStatus.getField().setValue(null);
//		
//		refresh(reasonDataChanged, null);
//	}


//
//	private void initTablesAuswertung() throws Exception {
//		m_coAuswertungAmpellisteAbteilung = new CoAuswertungAmpellisteAbteilung();
//		m_coAuswertungAmpellisteAbteilung.loadByAuswertungID(m_coAuswertung.getID());
//		
//		m_tableAbteilungen = new SortedTableControl(findControl("spread.auswertung.ampelliste.abteilungen"));
//		m_tableAbteilungen.setData(m_coAuswertungAmpellisteAbteilung);
//		m_coAuswertung.addChild(m_coAuswertungAmpellisteAbteilung);
//		
//		
//		
//		m_coAuswertungAmpellisteProjektleiter = new CoAuswertungAmpellisteProjektleiter();
//		m_coAuswertungAmpellisteProjektleiter.loadByAuswertungID(m_coAuswertung.getID());
//		
//		m_tableAbteilungsleiter = new SortedTableControl(findControl("spread.auswertung.ampelliste.projektleiter"));
//		m_tableAbteilungsleiter.setData(m_coAuswertungAmpellisteProjektleiter);
//		m_coAuswertung.addChild(m_coAuswertungAmpellisteProjektleiter);
//	}


	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable() throws Exception {
		m_table = new SortedTableControl(findControl(getResIdTable())){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
//				int id;
//				
//				// Abruf öffnen, wenn es einen gibt
//				id = getVirtCoProjekt().getAbrufID();
//				if (id > 0)
//				{
//					FormAbruf.open(getSession(), id);
//					return;
//				}
//				
//				// sonst Auftrag öffnen
//				id = getVirtCoProjekt().getAuftragID();
//				FormAuftrag.open(getSession(), id);
			}

		};

	}

	
	/**
	 * ResID der Tabelle
	 * 
	 * @return
	 */
	private String getResIdTable(){
		return RESID_TABLE;
	}
	

	@Override
	protected void loadCo() throws Exception {
		m_co = new CoMonatseinsatzblatt();
		((CoMonatseinsatzblatt)m_co).loadAuswertungKGG(getCoAuswertungKGG());
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
//		int personID;
		CoAuswertungKGG coAuswertung;
		
		coAuswertung = new CoAuswertungKGG();
		
		// Projektleiter dürfen nur ihre eigenen Projekte sehen, wenn sie nicht auch Projektauswerter sind
//		personID = UserInformation.getInstance().getPersonIDAlsProjektleiter();
//		if (personID > 0)
//		{
//			coAuswertungAmpelliste.setProjektleiterID(personID);
//		}
		
		return coAuswertung;
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "kggauswertung." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
//		addPdfExportListener(new ExportAmpellisteListener(this));
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}
	

	@Override
	public String getDefaultExportName(){
		return "KGG_Auswertung" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}
	

	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PROJEKTE;
	}
	

	/**
	 * Wenn das Formular bereits existiert muss es geschlossen und neu geöffnet werden, da sich die Anzahl der Spalten verändern kann
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#loadData()
	 */
	protected void loadData() throws Exception {
		ISession session;
		String key;
		ITabFolder editFolder;

		// TODO löschen, wenn man nie an diese Stelle kommt
		if (getData() instanceof CoMonatseinsatzblatt)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAuswertungKGG.open(session);
		}
		else
		{
			super.loadData();
		}
	}

	
	public CoAuswertungKGG getCoAuswertungKGG(){
		return (CoAuswertungKGG) m_coAuswertung;
	}
	

//	protected VirtCoProjekt getVirtCoProjekt() {
//		return (VirtCoProjekt) m_co;
//	}

	
	/**
	 * Auswahltabellen aktivieren
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#refresh(int, java.lang.Object)
	 */
	public void refresh(int reason, Object element){
		int personID = 0;

		super.refresh(reason, element);

		m_table.refresh(reasonDisabled, null);

		// Projektleiter dürfen nur ihre eigenen Projekte sehen, wenn sie nicht auch Projektauswerter sind
		try 
		{
			personID = UserInformation.getInstance().getPersonIDAlsProjektleiter();
			if (personID > 0)
			{
//				getCoAuswertungAmpelliste().setProjektleiterID(personID);// wird hier gemacht, falls sich die Berechtigungen der Person ändern
//				m_comboProjektleiter.refresh(reasonDisabled, null);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}
}
