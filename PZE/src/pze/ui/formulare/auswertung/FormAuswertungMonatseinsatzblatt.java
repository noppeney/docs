package pze.ui.formulare.auswertung;

import framework.business.interfaces.fields.IField;
import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungMonatseinsatzblatt;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für die Auswertung des Monatseinsatzblatts (eingetragene Stunden)
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungMonatseinsatzblatt extends FormAuswertung {
	
	public static String RESID = "form.auswertung.monatseinsatzblatt";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungMonatseinsatzblatt(Object parent) throws Exception {
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
			name = "Auswertung Monatseinsatzblatt";

			m_formAuswertung = new FormAuswertungMonatseinsatzblatt(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap("calendar.edit");
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
		m_table = new SortedTableControl(findControl("spread.auswertung.monatseinsatzblatt")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, ((CoMonatseinsatzblatt) m_co).getPersonID());
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
		m_co = new CoMonatseinsatzblatt();
		((CoMonatseinsatzblatt)m_co).loadAuswertungMonatseinsatzblatt(m_coAuswertung);
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungMonatseinsatzblatt();
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "monatseinsatzblatt." + id;
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

		if (getData() instanceof CoMonatseinsatzblatt)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAuswertungMonatseinsatzblatt.open(session);
		}
		else
		{
			super.loadData();
		}
	}


	@Override
	public String getDefaultExportName() {
		return "Auswertung_MonatseinsatzblattKontowerte" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PERSONEN;
	}
	

}
