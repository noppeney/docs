package pze.ui.formulare.auswertung;

import framework.business.interfaces.fields.IField;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Profile;
import pze.business.export.ExportAuswertungAuszahlungListener;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungAuszahlung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoKontowertAuswertung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für die Auswertung der Auszahlung von Plusstunden
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungAuszahlung extends FormAuswertung {
	
	public static String RESID = "form.auswertung.auszahlung";

	private ComboControl m_comboStatusAuszahlung;
	private IButtonControl m_btAlleStatusAuszahlung;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungAuszahlung(Object parent) throws Exception {
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
			name = "Auswertung Auszahlung";

			m_formAuswertung = new FormAuswertungAuszahlung(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap("calendar.add");
		}

		editFolder.setSelection(key);
	}


	@Override
	protected void initFormular() throws Exception {
		super.initFormular();

		m_comboStatusAuszahlung = (ComboControl) findControl(getResID() + ".statusauszahlung");
		
		m_btAlleStatusAuszahlung = (IButtonControl) findControl(getResID() + ".allestatusauszahlung");
		if (m_btAlleStatusAuszahlung != null)
		{
			m_btAlleStatusAuszahlung.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					m_comboStatusAuszahlung.getField().setValue(null);

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
		m_table = new SortedTableControl(findControl("spread.auswertung.auszahlung")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;

				formPerson = FormPerson.open(getSession(), null, ((CoKontowertAuswertung) m_co).getPersonID());
				if (formPerson != null)
				{
					formPerson.showZeiterfassung(((CoKontowertAuswertung) m_co).getDatum());
				}
			}

			@Override
			protected void tableDataChanged(Object bookmark, IField fld) throws Exception {
			}
		};

	}


	/**
	 * Spaltenbreite der Person ändern
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#updateHeaderDescription()
	 */
	@Override
	protected void updateHeaderDescription() {
		CoKontowert coKontowert;
		IHeaderDescription headerDescription;
		
		headerDescription = m_table.getHeaderDescription();
		coKontowert = new CoKontowert();
		
		// Spaltenbreite der Person ändern
		headerDescription.getColumnDescription(coKontowert.getFieldPersonID().getFieldDescription().getResID()).setWidth(130);

		// Spaltenbreite Status ändern
		headerDescription.getColumnDescription(coKontowert.getFieldStatusIDAuszahlung().getFieldDescription().getResID()).setWidth(130);

		m_table.setHeaderDescription(headerDescription);
//		m_table.enable(false); // TODO seltsamer Fehler (momentan OK) Auswertungstabellen aktiviert (wenn das bei Ausw. Kontowerte nicht gemacht wird ist Tabelle aktiviert)
	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new CoKontowertAuswertung(getCoAuswertungAuszahlung());
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungAuszahlung();
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "auswertung.auszahlung." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addPdfExportListener(new ExportAuswertungAuszahlungListener(this));
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
		// TODO Bearbeitung der Auszahlungen (Status) in Auswertung aktivieren		super.activate();
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

		if (getData() instanceof CoKontowertAuswertung)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAuswertungAuszahlung.open(session);
		}
		else
		{
			super.loadData();
		}
	}

	
	private CoAuswertungAuszahlung getCoAuswertungAuszahlung(){
		return (CoAuswertungAuszahlung) m_coAuswertung;
	}


	@Override
	public String getDefaultExportName() {
		return "AuswertungAuszahlung" + getCoAuswertungAuszahlung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PERSONEN;
	}

}
