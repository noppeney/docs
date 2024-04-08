package pze.ui.formulare.auswertung;

import framework.business.interfaces.fields.IField;
import framework.business.interfaces.session.ISession;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Profile;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungUrlaub;
import pze.business.objects.personen.CoBuchung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für die Auswertung der Urlaubsbuchungen
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungUrlaubsbuchungen extends FormAuswertung {
	
	public static String RESID = "form.auswertung.urlaub";

	private ComboControl m_comboStatusBuchung;
	private IButtonControl m_btAlleStatusBuchung;

	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungUrlaubsbuchungen(Object parent) throws Exception {
		super(parent, RESID);
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @throws Exception
	 */
	public static void open(ISession session) throws Exception {
		String key, name;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if (item == null)
		{
			name = "Auswertung Urlaubsbuchungen";

			m_formAuswertung = new FormAuswertungUrlaubsbuchungen(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap("weather.sun"); // Flugzeug lib.liner
		}

		editFolder.setSelection(key);
	}


	/**
	 * Formularfelder und Listener initialisieren
	 * @throws Exception 
	 */
	@Override
	protected void initFormular() throws Exception {
		super.initFormular();

		m_comboStatusBuchung = (ComboControl) findControl(getResID() + ".statusbuchungid");

		m_btAlleStatusBuchung = (IButtonControl) findControl(getResID() + ".allestatusbuchung");
		if (m_btAlleStatusBuchung != null)
		{
			m_btAlleStatusBuchung.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					m_comboStatusBuchung.getField().setValue(null);

					refresh(reasonDataChanged, null);
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}
	}


	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable() throws Exception {
		m_table = new SortedTableControl(findControl("spread.auswertung.urlaub")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, (m_co).getPersonID());
				if (formPerson != null)
				{
					formPerson.showZeiterfassung(m_co.getDatum());
				}
			}

			@Override
			protected void tableDataChanged(Object bookmark, IField fld) throws Exception {
			}
		};
	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new CoBuchung();
		((CoBuchung) m_co).loadUrlaubsuebersicht(m_coAuswertung);

		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}


//	/**
//	 * CO mit den Daten
//	 * AnAbwesenheit wird für das leichetere Erstellen der Ausgabe geladen
//	 * 
//	 * @return
//	 * @throws Exception 
//	 */
//	public CoKontowert getCoKontowerte() throws Exception {
//		CoKontowert coKontowert = new CoKontowert();
//		
//		coKontowert.loadAnAbwesenheit(m_coAuswertung);
//		return coKontowert;
//	}
//
//
//	/**
//	 * CO mit den Daten
//	 * 
//	 * @return
//	 * @throws Exception 
//	 */
//	public CoBuchung getCoBuchung() throws Exception {
//		CoBuchung coBuchung = new CoBuchung();
//		coBuchung.loadUrlaubsbuchungen(m_coAuswertung);
//		
//		return coBuchung;
//	}


	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungUrlaub();
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "auswertung.urlaub." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
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

		if (getData() instanceof CoBuchung)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAuswertungUrlaubsbuchungen.open(session);
		}
		else
		{
			super.loadData();
		}
	}


	@Override
	public String getDefaultExportName() {
		return "Urlaub" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_URLAUBSPLANUNG;
	}


}
