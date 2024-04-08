package pze.ui.formulare.auswertung;

import framework.business.interfaces.fields.IField;
import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IColumnDescription;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.export.ExportKontowerteZeitraumListener;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungKontowerteZeitraum;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoKontowertAuswertung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.FormPerson;

/**
 * Formular für die Auswertung der Kontowerte über einen Zeitraum
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungKontowerteZeitraum extends FormAuswertung {
	
	public static String RESID = "form.auswertung.kontowerte.zeitraum";


	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungKontowerteZeitraum(Object parent) throws Exception {
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
			name = "Auswertung Kontowerte (Zeitraum)";

			m_formAuswertung = new FormAuswertungKontowerteZeitraum(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap("calendar.view.month");
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
		m_table = new SortedTableControl(findControl("spread.auswertung.kontowerte.zeitraum")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormPerson formPerson;
				
				formPerson = FormPerson.open(getSession(), null, ((CoKontowertAuswertung) m_co).getPersonID());
				if (formPerson != null)
				{
					formPerson.showZeiterfassung(((CoKontowertAuswertung) m_co).getDatumVon());
				}
			}

			@Override
			protected void tableDataChanged(Object bookmark, IField fld) throws Exception {
			}
		};

	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new CoKontowertAuswertung(getCoAuswertungKontowerteZeitraum());
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungKontowerteZeitraum();
	}


	/**
	 * Krank-Spalten unterschiedlich beschriften
	 * @see pze.ui.formulare.auswertung.FormAuswertung#updateHeaderDescription()
	 */
	@Override
	protected void updateHeaderDescription() {
		CoKontowert coKontowert;
		IHeaderDescription headerDescription;
		IColumnDescription columnDescription;
		
		headerDescription = m_table.getHeaderDescription();
		coKontowert = new CoKontowert();
		
		columnDescription = headerDescription.getColumnDescription(coKontowert.getFieldWertKrank().getFieldDescription().getResID());
		if (columnDescription != null)
		{
			columnDescription.setCaption("Krank (Zeit)");
		}
		
		columnDescription = headerDescription.getColumnDescription(coKontowert.getFieldAnzahlKrank().getFieldDescription().getResID());
		if (columnDescription != null)
		{
			columnDescription.setCaption("Krank (Tage)");
		}

		// Spaltenbreite der Person ändern
		headerDescription.getColumnDescription(coKontowert.getFieldPersonID().getFieldDescription().getResID()).setWidth(130);

		m_table.setHeaderDescription(headerDescription);
//		m_table.enable(false); // TODO seltsamer Fehler (momentan OK) Auswertungstabellen aktiviert (wenn das bei Ausw. Kontowerte nicht gemacht wird ist Tabelle aktiviert)
	}


	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "kontowerte.zeitraum." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addPdfExportListener(new ExportKontowerteZeitraumListener(this));
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

			FormAuswertungKontowerteZeitraum.open(session);
		}
		else
		{
			super.loadData();
		}
	}

	
	private CoAuswertungKontowerteZeitraum getCoAuswertungKontowerteZeitraum(){
		return (CoAuswertungKontowerteZeitraum) m_coAuswertung;
	}
	

	@Override
	public String getDefaultExportName() {
		return "Kontowerte_Zeitraum" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PERSONEN;
	}
	


}
