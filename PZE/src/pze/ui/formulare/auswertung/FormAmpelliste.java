package pze.ui.formulare.auswertung;

import framework.business.interfaces.session.ISession;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import framework.ui.interfaces.selection.IValueChangeListener;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Profile;
import pze.business.UserInformation;
import pze.business.export.ExportAmpellisteListener;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungAmpelliste;
import pze.business.objects.personen.CoKontowertAuswertung;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.projektverwaltung.FormAbruf;
import pze.ui.formulare.projektverwaltung.FormAuftrag;

/**
 * Formular für die Auswertung der Projektdaten (Ampelliste)
 * 
 * @author Lisiecki
 *
 */
public class FormAmpelliste extends FormAuswertung {

	public static final String COLOR_GRUEN= "##88FF88";
	public static final String COLOR_ROT = "##FF6666";
	public static final String COLOR_GELB = "##FFFF00";

	public static final String RESID = "form.auswertung.ampelliste";
	
	private static final String RESID_TABLE = "spread.auswertung.ampelliste";
	
//	private CoAuswertungAmpellisteAbteilung m_coAuswertungAmpellisteAbteilung;
//	private CoAuswertungAmpellisteProjektleiter m_coAuswertungAmpellisteProjektleiter;

//	private SortedTableControl m_tableAbteilungen;
//	private SortedTableControl m_tableAbteilungsleiter;
	
	protected ComboControl m_comboAuftrag;
	protected ComboControl m_comboAbruf;
	protected ComboControl m_comboKostenstelle;

	protected ComboControl m_comboKunde;
	protected ComboControl m_comboAbteilungKunde;
	protected ComboControl m_comboAnfordererKunde;

	protected ComboControl m_comboProjektleiter;
	protected ComboControl m_comboAbteilungsleiter;
	protected ComboControl m_comboFachgebiet;

	protected ComboControl m_comboAbrechnungsart;
	protected ComboControl m_comboStatus;
	
	protected IButtonControl m_btAlleAuftraege;
	protected IButtonControl m_btAlleAbrufe;
	protected IButtonControl m_btAlleKostenstellen;
	protected IButtonControl m_btAlleKunde;
	protected IButtonControl m_btAlleWti;
	protected IButtonControl m_btAlleAbrechnungsart;
	protected IButtonControl m_btAlleStatus;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAmpelliste(Object parent) throws Exception {
		this(parent, RESID);
	}


	/**
	 * Konstruktor
	 * 
	 * @param parent
	 * @param resID
	 * @throws Exception
	 */
	public FormAmpelliste(Object parent, String resID) throws Exception {
		super(parent, resID);
		
		initFormular();
		
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
			m_formAuswertung = new FormAmpelliste(editFolder);
			item = editFolder.add(RESID, key, m_formAuswertung, true);
		}

		editFolder.setSelection(key);
	}


	protected void initFormular() throws Exception {
		super.initFormular();
		
		CoPerson coPerson;
		IValueChangeListener valueChangeListenerKunde, valueChangeListenerWti;
		
		
		m_comboAuftrag = (ComboControl) findControl(getResID() + ".auftragid");
		m_comboAbruf = (ComboControl) findControl(getResID() + ".abrufid");
		m_comboKostenstelle = (ComboControl) findControl(getResID() + ".kostenstelleid");
		
		m_comboKunde = (ComboControl) findControl(getResID() + ".kundeid");
		m_comboAbteilungKunde = (ComboControl) findControl(getResID() + ".abteilungkundeid");
		m_comboAnfordererKunde = (ComboControl) findControl(getResID() + ".anfordererkundeid");
		
		m_comboProjektleiter = (ComboControl) findControl(getResID() + ".projektleiterid");
		m_comboAbteilungsleiter = (ComboControl) findControl(getResID() + ".abteilungsleiterid");
		m_comboFachgebiet = (ComboControl) findControl(getResID() + ".fachgebietid");
		
		m_comboAbrechnungsart = (ComboControl) findControl(getResID() + ".abrechnungsartid");
		m_comboStatus = (ComboControl) findControl(getResID() + ".statusid");


		// für die Auswahlliste der Projektleiter nur die Personen laden, die als Projektleiter angegeben sind
		coPerson = new CoPerson();
		coPerson.loadItemsProjektleiter();
		refreshItems(m_comboProjektleiter, coPerson, m_comboProjektleiter.getField());

		
		valueChangeListenerKunde = new IValueChangeListener() {

			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				resetCombosEinschraenkungKunde(control, currentValue);
			}
		};

		m_comboKunde.setValueChangeListener(valueChangeListenerKunde);
		m_comboAbteilungKunde.setValueChangeListener(valueChangeListenerKunde);
		m_comboAnfordererKunde.setValueChangeListener(valueChangeListenerKunde);
				

		valueChangeListenerWti = new IValueChangeListener() {

			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				resetCombosEinschraenkungWti(control, currentValue);
			}
		};

		m_comboProjektleiter.setValueChangeListener(valueChangeListenerWti);
		m_comboAbteilung.setValueChangeListener(valueChangeListenerWti);
		m_comboAbteilungsleiter.setValueChangeListener(valueChangeListenerWti);
		m_comboFachgebiet.setValueChangeListener(valueChangeListenerWti);
				

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
		
		
		m_btAlleKunde = (IButtonControl) findControl(getResID() + ".allekunde");
		m_btAlleKunde.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				resetCombosKunde();
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btAlleWti = (IButtonControl) findControl(getResID() + ".allewti");
		m_btAlleWti.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				resetCombosWti();
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btAlleAbrechnungsart = (IButtonControl) findControl(getResID() + ".alleabrechnungsart");
		m_btAlleAbrechnungsart.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_comboAbrechnungsart.getField().setValue(null);
				refresh(reasonDataChanged, null);
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		
		m_btAlleStatus = (IButtonControl) findControl(getResID() + ".allestatus");
		m_btAlleStatus.setSelectionListener(new ISelectionListener() {

			@Override
			public void selected(IControl control, Object params) {
				m_comboStatus.getField().setValue(null);
				refresh(reasonDataChanged, null);
			}

			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
	}


	/**
	 * Items nicht neu laden, da alle Abteilungen ausgewählt werden dürfen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#loadItemsComboAbteilung()
	 */
	protected void loadItemsComboAbteilung() throws Exception {
	}


	/**
	 * Werte der Combos für die Einschränkung der Kunden auf NULL setzen und den übergebenen Wert setzen
	 * 
	 * @param currentValue 
	 * @param control 
	 */
	private void resetCombosEinschraenkungKunde(IControl control, Object currentValue) {
		resetCombosKunde();
		
		((ComboControl) control).getField().setValue(currentValue);
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Werte der Combos für die Einschränkung der Kunden auf NULL setzen
	 */
	private void resetCombosKunde() {
		m_comboKunde.getField().setValue(null);
		m_comboAbteilungKunde.getField().setValue(null);
		m_comboAnfordererKunde.getField().setValue(null);
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Werte der Combos für die Einschränkung der WTI-Parameter auf NULL setzen und den übergebenen Wert setzen
	 * 
	 * @param currentValue 
	 * @param control 
	 */
	private void resetCombosEinschraenkungWti(IControl control, Object currentValue) {
		resetCombosWti();
		
		((ComboControl) control).getField().setValue(currentValue);
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Werte der Combos für die Einschränkung der WTI-Parameter auf NULL setzen
	 */
	private void resetCombosWti() {
		m_comboProjektleiter.getField().setValue(null);
		m_comboAbteilung.getField().setValue(null);
		m_comboAbteilungsleiter.getField().setValue(null);
		m_comboFachgebiet.getField().setValue(null);
		
		refresh(reasonDataChanged, null);
	}


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
				int id;
				
				// Abruf öffnen, wenn es einen gibt
				id = getVirtCoProjekt().getAbrufID();
				if (id > 0)
				{
					FormAbruf.open(getSession(), id);
					return;
				}
				
				// sonst Auftrag öffnen
				id = getVirtCoProjekt().getAuftragID();
				FormAuftrag.open(getSession(), id);
			}

			@Override
			protected void renderCell(ISpreadCell cell) throws Exception {
				super.renderCell(cell);
				
				double verbrauch;
				String color;
				
				// Header hat keine Farbe
				if (cell.getField() == null)
				{
					return;
				}
				
				verbrauch = getVirtCoProjekt().getVerbrauchSollstunden();

				if (verbrauch < 0.6)
				{
					color = COLOR_GRUEN;
				}
				else if (verbrauch < 0.8)
				{
					color = COLOR_GELB;
				}
				else
				{
					color = COLOR_ROT;
				}

				cell.setBackColor(color);
			}
		};

	}

	
	/**
	 * ResID der Tabelle
	 * 
	 * @return
	 */
	protected String getResIdTable(){
		return RESID_TABLE;
	}
	

	/*
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#updateHeaderDescription()
	 */
	@Override
	protected void updateHeaderDescription() {
//		CoKontowert coKontowert;
//		IHeaderDescription headerDescription;
		
//		headerDescription = m_table.getHeaderDescription();
//		coKontowert = new CoKontowert();
//		
//		// Spaltenbreite der Person ändern
////		headerDescription.getColumnDescription(coKontowert.getFieldPersonID().getFieldDescription().getResID()).setWidth(130);
//
//		m_table.setHeaderDescription(headerDescription);
	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new VirtCoProjekt();
		((VirtCoProjekt)m_co).loadAmpelliste(getCoAuswertungAmpelliste());
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		int personID;
		CoAuswertungAmpelliste coAuswertungAmpelliste;
		
		coAuswertungAmpelliste = new CoAuswertungAmpelliste();
		
		// Projektleiter dürfen nur ihre eigenen Projekte sehen, wenn sie nicht auch Projektauswerter sind
		personID = UserInformation.getInstance().getPersonIDAlsProjektleiter();
		if (personID > 0)
		{
			coAuswertungAmpelliste.setProjektleiterID(personID);
		}
		
		return coAuswertungAmpelliste;
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "ampelliste." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addPdfExportListener(new ExportAmpellisteListener(this));
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}
	

	@Override
	public String getDefaultExportName(){
		return "Ampelliste" + getCoAuswertung().getStringEinschraenkungDatumPerson();
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
		if (getData() instanceof CoKontowertAuswertung)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAmpelliste.open(session);
		}
		else
		{
			super.loadData();
		}
	}

	
	public CoAuswertungAmpelliste getCoAuswertungAmpelliste(){
		return (CoAuswertungAmpelliste) m_coAuswertung;
	}
	

	protected VirtCoProjekt getVirtCoProjekt() {
		return (VirtCoProjekt) m_co;
	}

	
//	public String getSelectedProjektleiterIDs() throws Exception{
//		if (m_coAuswertungAmpellisteProjektleiter == null)
//		{
//			return null;
//		}
//		
//		return m_coAuswertungAmpellisteProjektleiter.getSelectedIDs();
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
				getCoAuswertungAmpelliste().setProjektleiterID(personID);// wird hier gemacht, falls sich die Berechtigungen der Person ändern
				m_comboProjektleiter.refresh(reasonDisabled, null);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

//		m_tableAbteilungen.refresh(reasonEnabled, null);
//		m_tableGruppen.refresh(reasonEnabled, null);
//		m_tableAbteilungsleiter.refresh(reasonEnabled, null);
	}
}
