package pze.ui.formulare.auswertung;

import framework.business.interfaces.fields.IField;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.UserInformation;
import pze.business.export.ExportKontowerteListener;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungKontowerte;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoKontowertAuswertung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für die Auswertung der Kontowerte (Stand Gleitzeitkonto und Resturlaub)
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungKontowerte extends FormAuswertung {
	
	public static String RESID = "form.auswertung.kontowerte";

	private IControl m_checkResturlaubDetailsAusgaben;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungKontowerte(Object parent) throws Exception {
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
			name = "Auswertung Kontowerte";

			m_formAuswertung = new FormAuswertungKontowerte(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap("calendar.view.week");
		}

		editFolder.setSelection(key);
	}


	@Override
	protected void initFormular() throws Exception {
		super.initFormular();

		m_checkResturlaubDetailsAusgaben = findControl(getResID() + ".standresturlaubdetailsausgeben");
	}


	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable() throws Exception {
		m_table = new SortedTableControl(findControl("spread.auswertung.kontowerte")){

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

		m_table.setHeaderDescription(headerDescription);
//		m_table.enable(false); // TODO seltsamer Fehler (momentan OK) Auswertungstabellen aktiviert (wenn das bei Ausw. Kontowerte nicht gemacht wird ist Tabelle aktiviert)
	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new CoKontowertAuswertung(getCoAuswertungKontowerte());
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungKontowerte();
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "kontowerte." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addPdfExportListener(new ExportKontowerteListener(this));
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

		if (getData() instanceof CoKontowertAuswertung)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAuswertungKontowerte.open(session);
		}
		else
		{
			super.loadData();
		}
	}

	
	private CoAuswertungKontowerte getCoAuswertungKontowerte(){
		return (CoAuswertungKontowerte) m_coAuswertung;
	}


	@Override
	public String getDefaultExportName() {
		return "Kontowerte" + getCoAuswertungKontowerte().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PERSONEN;
	}


	@Override
	public void refresh(int reason, Object element){
		super.refresh(reason, element);

		// für alle mit Rechten unterhalb der Personalabteilung ist die Option zum Ausgeben von Details zum Resturlaub deaktiviert
		if (!UserInformation.getInstance().isPersonalverwaltung())
		{
			m_checkResturlaubDetailsAusgaben.refresh(reasonDisabled, null);
		}

	}


}
