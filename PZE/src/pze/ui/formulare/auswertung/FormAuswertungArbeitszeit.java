package pze.ui.formulare.auswertung;

import framework.business.interfaces.fields.IField;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.Format;
import pze.business.Profile;
import pze.business.export.ExportAuswertungArbeitszeitListener;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungArbeitszeit;
import pze.business.objects.personen.CoArbeitszeit;
import pze.business.objects.reftables.CoHalbjahresauswahl;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für die Auswertung Buchhaltung/Arbeitszeit
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungArbeitszeit extends FormAuswertung {
	
	public static String RESID = "form.auswertung.person.arbeitszeit";

	private CoHalbjahresauswahl m_coHalbjahresauswahl;
	private ComboControl m_comboHalbjahresauswahl;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungArbeitszeit(Object parent) throws Exception {
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
			name = "Auswertung Arbeitszeit";

			m_formAuswertung = new FormAuswertungArbeitszeit(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap("book.error");
		}

		editFolder.setSelection(key);
	}


	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable() throws Exception {
		
		m_table = new SortedTableControl(findControl("spread.auswertung.person.arbeitszeit")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, ((CoArbeitszeit) m_co).getPersonID());
				if (formPerson != null)
				{
					formPerson.showMonatseinsatzblatt();
				}
			}

			@Override
			protected void tableDataChanged(Object bookmark, IField fld) throws Exception {
			}
		};
	}


	@Override
	protected void loadCo() throws Exception {
		long a = System.currentTimeMillis();
		// erst Daten für die Halbjahresauswahl laden, damit der zeitraum für das CO feststeht
		if (m_comboHalbjahresauswahl == null)
		{
			initComboHalbjahresauswahl();
		}
		
		m_co = new CoArbeitszeit(m_coAuswertung, m_coHalbjahresauswahl.getGregDatum());
//		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
		System.out.println("load: " + (System.currentTimeMillis()-a)/1000.);
	}


	/**
	 * Combo zur Halbjahresauswahl erstellen und füllen
	 * 
	 * @throws Exception
	 */
	private void initComboHalbjahresauswahl() throws Exception {
		
		m_coHalbjahresauswahl = new CoHalbjahresauswahl();
		m_coHalbjahresauswahl.createCo();
		
		// zu dem Datensatz mit dem gespeicherten Datum springen
		m_coHalbjahresauswahl.moveTo(Format.getDate0Uhr(m_coAuswertung.getDatumVon()), 
				m_coHalbjahresauswahl.getFieldDatum().getFieldDescription().getResID());
		
		// Combo erstellen und Wert setzen
		m_comboHalbjahresauswahl = (ComboControl) findControl(RESID + ".halbjahresauswahl");
		m_comboHalbjahresauswahl.getField().setValue(m_coHalbjahresauswahl.getID());

		// Items setzen
		refreshItems(m_comboHalbjahresauswahl, m_coHalbjahresauswahl, m_comboHalbjahresauswahl.getField());
		
		// Listener zum Wechseln des Halbjahres, Datum speichern
		m_comboHalbjahresauswahl.setValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				m_coAuswertung.setDatumVon(m_coHalbjahresauswahl.getDatum());
			}
		});
	}
	

	/**
	 * Spaltenbreite der Person ändern
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#updateHeaderDescription()
	 */
	@Override
	protected void updateHeaderDescription() {
		int iSpalten, anzSpalten;
		String resID;
		IHeaderDescription headerDescription;
		IColumnDescription columnDescription;
		
		headerDescription = m_table.getHeaderDescription();
		
		// Spaltenbreite der Person und Saldo ändern
		headerDescription.getColumnDescription(m_co.getFieldPersonID().getFieldDescription().getResID()).setWidth(130);
		headerDescription.getColumnDescription(((CoArbeitszeit) m_co).getFieldSaldo().getFieldDescription().getResID()).setWidth(60);

		// alle weiteren Spalten
		anzSpalten = m_co.getColumnCount();
		for (iSpalten=2; iSpalten<anzSpalten; ++iSpalten)
		{
			columnDescription = headerDescription.getColumnDescription(iSpalten);
			resID = columnDescription.getResID();
			
			// Unterscheidung der Spaltenbreite je nach Spalte
			if (resID.contains(CoArbeitszeit.RESID_WERKTAGE))
			{
				headerDescription.getColumnDescription(iSpalten).setWidth(65);
			}
			else if (resID.contains(CoArbeitszeit.RESID_DIFFERENZ))
			{
				headerDescription.getColumnDescription(iSpalten).setWidth(60);
			}
			else // Max/Ist
			{
				headerDescription.getColumnDescription(iSpalten).setWidth(50);
			}
		}
		
		m_table.setHeaderDescription(headerDescription);
//		m_table.enable(false); // TODO seltsamer Fehler (momentan OK) Auswertungstabellen aktiviert (wenn das bei Ausw. Kontowerte nicht gemacht wird ist Tabelle aktiviert)
	}


	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungArbeitszeit();
	}

	
	/**
	 * Zeitraum als Text
	 * 
	 * @return
	 */
	public String getZeitraum() {
		return m_coHalbjahresauswahl.getBezeichnung();
	}
	
	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "buchhaltung.arbeitszeit." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
		addPdfExportListener(new ExportAuswertungArbeitszeitListener(this));
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

		if (getData() instanceof CoArbeitszeit)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAuswertungArbeitszeit.open(session);
		}
		else
		{
			super.loadData();
		}
	}


	@Override
	public String getDefaultExportName() {
		return "Auswertung_Arbeitszeit" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PERSONEN;
	}
	

}
