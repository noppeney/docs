package pze.ui.formulare.auswertung;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.UserInformation;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungDienstreisen;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.DialogDienstreise;

/**
 * Formular für eine Übersicht über die Personen
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungDienstreisen extends FormAuswertung {
	
	private static String RESID_ZUSAMMENFASSUNG = "form.auswertung.dienstreisen";
	private static String RESID_DETAIL = "form.auswertung.dienstreisendetail";

//	private ComboControl m_comboStatus;
	
//	private IButtonControl m_btAlleStati;


	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	protected FormAuswertungDienstreisen(Object parent, CoAuswertungDienstreisen coAuswertung) throws Exception {
		// Zusammenfassung oder detaillierte Ansicht
		this(parent, coAuswertung.isEmpty() || !coAuswertung.isDetailAusgabe() ? RESID_ZUSAMMENFASSUNG : RESID_DETAIL);
	}
	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	protected FormAuswertungDienstreisen(Object parent, String resID) throws Exception {
		super(parent, resID);
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @throws Exception
	 */
	public static void open(ISession session) throws Exception {
		String key;
		CoAuswertungDienstreisen coAuswertung;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if(item == null)
		{
			coAuswertung = new CoAuswertungDienstreisen();
			coAuswertung.loadByUserID(UserInformation.getUserID());

			m_formAuswertung = new FormAuswertungDienstreisen(editFolder, coAuswertung);
			item = editFolder.add(m_formAuswertung.getResID(), key, m_formAuswertung, true);
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
		
//		m_comboStatus = (ComboControl) findControl(getResID() + ".statusid");
		
//		m_HinweisGleitzeitkonto = (BooleanControl) findControl(getResID() + ".hinweisgleitzeitkontoausblenden");
//		m_HinweisGleitzeitkonto = (BooleanControl) findControl(getResID() + ".keinebuchungausblenden");

//		m_btAlleStati = (IButtonControl) findControl(getResID() + ".allestati");
//		m_btAlleStati.setSelectionListener(new ISelectionListener() {
//			
//			@Override
//			public void selected(IControl control, Object params) {
//				m_comboStatus.getField().setValue(null);
//				
//				refresh(reasonDataChanged, null);
//			}
//			
//			@Override
//			public void defaultSelected(IControl control, Object params) {
//			}
//		});
	}


	@Override
	protected void initTable() throws Exception {

		m_table = new SortedTableControl(findControl(getResID().replace("form", "spread"))){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				DialogDienstreise.showDialog(getCoBuchungDienstreisen().getDienstreiseID(), CoMessageGruppe.ID_SEKRETAERIN, 0);
			}

		};
		
	}


//	@Override
//	protected void updateHeaderDescription() {
//		IColumnDescription columnDescription;
//		IHeaderDescription headerDescription;
//		CoVerletzerliste coVerletzerliste;
//		
//		headerDescription = m_table.getHeaderDescription();
//		try 
//		{
//			coVerletzerliste = new CoVerletzerliste();
//			
//			// Spalte ID soll nicht ausgegeben werden
//			headerDescription.remove(coVerletzerliste.getFieldID().getFieldDescription().getResID());
//
//			// Spaltenbreite der Meldung ändern
//			headerDescription.getColumnDescription(coVerletzerliste.getFieldMeldungID().getFieldDescription().getResID()).setWidth(300);
//
//			// Spaltenbreite der Person ändern
//			headerDescription.getColumnDescription(coVerletzerliste.getFieldPersonID().getFieldDescription().getResID()).setWidth(130);
//
//			// Spaltenbreite der Bemerkung ändern
//			columnDescription = headerDescription.getColumnDescription(coVerletzerliste.getFieldBemerkung().getFieldDescription().getResID());
//			if (columnDescription != null)
//			{
//				columnDescription.setWidth(300);
//			}
//		} 
//		catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
//	
//		m_table.setHeaderDescription(headerDescription);
//	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new CoBuchung();
		((CoBuchung) m_co).loadAntraegeDr((CoAuswertungDienstreisen) m_coAuswertung);
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungDienstreisen();
	}


	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "dienstreisen." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
//		addPdfExportListener(new ExportVerletzerlisteListener(this));
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
		super.activate();
	}
	

	@Override
	public boolean mayEdit() {
		return false;
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

			FormAuswertungDienstreisen.open(session);
		}
		else
		{
			super.loadData();
		}
	}


	protected CoBuchung getCoBuchungDienstreisen() {
		return (CoBuchung) m_co;
	}


	@Override
	public String getDefaultExportName() {
		return "Dienstreisen" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_DIENSTREISEN;
	}
	

}
