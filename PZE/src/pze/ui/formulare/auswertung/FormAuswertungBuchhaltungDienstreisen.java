package pze.ui.formulare.auswertung;

import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.session.ISession;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.interfaces.selection.ISelectionListener;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.UserInformation;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungBuchhaltungDienstreisen;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.reftables.CoMessageGruppe;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.person.DialogDienstreise;

/**
 * Formular für eine Übersicht über die Personen
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungBuchhaltungDienstreisen extends FormAuswertungDienstreisen {
	
	private static String RESID_ZUSAMMENFASSUNG = "form.auswertung.dienstreisenbh";
	private static String RESID_DETAIL = "form.auswertung.dienstreisenbhdetail";

	private ComboControl m_comboKunde;
	private ComboControl m_comboBuchungsart;
	private ComboControl m_comboStatusGenehmigung;
	
	private IButtonControl m_btAlleKunden;
	private IButtonControl m_btAlleBuchungsarten;
	private IButtonControl m_btAlleStatusGenehmigung;


	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	protected FormAuswertungBuchhaltungDienstreisen(Object parent, CoAuswertungBuchhaltungDienstreisen coAuswertung) throws Exception {
		// Zusammenfassung oder detaillierte Ansicht
		super(parent, coAuswertung.isEmpty() || !coAuswertung.isDetailAusgabe() ? RESID_ZUSAMMENFASSUNG : RESID_DETAIL);
		
		// Items anpassen
		refreshItems();
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
		CoAuswertungBuchhaltungDienstreisen coAuswertung;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if(item == null)
		{
			coAuswertung = new CoAuswertungBuchhaltungDienstreisen();
			coAuswertung.loadByUserID(UserInformation.getUserID());

			m_formAuswertung = new FormAuswertungBuchhaltungDienstreisen(editFolder, coAuswertung);
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
		
//		m_HinweisGleitzeitkonto = (BooleanControl) findControl(getResID() + ".hinweisgleitzeitkontoausblenden");
//		m_HinweisGleitzeitkonto = (BooleanControl) findControl(getResID() + ".keinebuchungausblenden");

		m_comboKunde = (ComboControl) findControl(getResID() + ".kundeid");
		m_comboBuchungsart = (ComboControl) findControl(getResID() + ".buchungsartid");
		m_comboStatusGenehmigung = (ComboControl) findControl(getResID() + ".statusgenehmigungid");
		
		m_btAlleKunden = (IButtonControl) findControl(getResID() + ".allekunden");
		m_btAlleKunden.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				m_comboKunde.getField().setValue(null);
				
				refresh(reasonDataChanged, null);
			}
			
			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		m_btAlleStatusGenehmigung = (IButtonControl) findControl(getResID() + ".allestatusgenehmigung");
		m_btAlleStatusGenehmigung.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				m_comboStatusGenehmigung.getField().setValue(null);
				
				refresh(reasonDataChanged, null);
			}
			
			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
		
		m_btAlleBuchungsarten = (IButtonControl) findControl(getResID() + ".allebuchungsarten");
		m_btAlleBuchungsarten.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl control, Object params) {
				m_comboBuchungsart.getField().setValue(null);
				
				refresh(reasonDataChanged, null);
			}
			
			@Override
			public void defaultSelected(IControl control, Object params) {
			}
		});
	}


	@Override
	protected void initTable() throws Exception {

		m_table = new SortedTableControl(findControl(getResID().replace("form", "spread"))){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				if (!isEditable(getSelectedCell()))
				{
					DialogDienstreise.showDialog(getCoBuchungDienstreisen().getDienstreiseID(), CoMessageGruppe.ID_SEKRETAERIN, 0);
				}
			}


			@Override
			public boolean mayEdit(Object bookmark, ISpreadCell cell) throws Exception {
				// einzelne Spalten dürfen geändert werden
				return isEditable(cell);
			}

			
			// Spalte ist editierbar
			private boolean isEditable(ISpreadCell cell) {
				String resID;
				resID = cell.getColumnDescription().getResID();
				
				return resID.equals(CoDienstreise.getResIdFieldAbgerechnet()) || resID.equals(CoDienstreise.getResIdFieldReisekostenabrechnung()) 
						|| resID.equals(CoDienstreise.getResIdFieldBemerkungBH());
			}

		};
	}


	/**
	 * Items für DDR anpassen
	 * 
	 * @throws Exception
	 */
	private void refreshItems() throws Exception {
		CoBuchungsart coBuchungsart;
		CoStatusGenehmigung coStatusGenehmigung;
		
		coBuchungsart = new CoBuchungsart();
		coBuchungsart.loadDrDg();
		refreshItems(m_comboBuchungsart, coBuchungsart, m_comboBuchungsart.getField());
		
		coStatusGenehmigung = new CoStatusGenehmigung();
		coStatusGenehmigung.loadForAuswertung();
		refreshItems(m_comboStatusGenehmigung, coStatusGenehmigung, m_comboStatusGenehmigung.getField());
		
	}


//	@Override
//	protected void loadCo() throws Exception {
//		m_co = new CoBuchung();
//		((CoBuchung) m_co).loadAntraegeDr((CoAuswertungDienstreisen) m_coAuswertung);
//		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
//	}


	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "dienstreisenbh." + id;
	}
	

	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungBuchhaltungDienstreisen();
	}


	@Override
	public boolean mayEdit() {
		return true;
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

			FormAuswertungBuchhaltungDienstreisen.open(session);
		}
		else
		{
			super.loadData();
		}
	}

	
	/**
	 * Prüfe, ob das Formular korrekt ausgefüllt ist und speichere die Daten, wenn sie vollständig sind
	 * Es können nur Daten der DR gespeichert werden
	 * 
	 * @throws Exception Validieren und Speichern
	 * @return Daten waren korrekt und wurden gespeichert
	 */
	public boolean validateAndSave() throws Exception {
		CoBuchung coBuchung;
		CoDienstreise coDienstreise;
		
		// Pflichtfelder prüfen
//		if (!pruefePflichtfelder())
//		{
//			return false;
//		}
//	
//		// weitere Prüfungen
//		if (!validate())
//		{
//			return false;
//		}
			

			// speichern des aktuellen Formulars und der untergeordneten Formulare
//			doBeforeSave();
		
		// Tabelle durchlaufen und geänderte Daten der DR speichern
		coDienstreise = new CoDienstreise();
		coBuchung = getCoBuchungDienstreisen();
		if (coBuchung.isModified() && coBuchung.moveFirst())
		{
			do
			{
				// geänderter Datensatz
				if (coBuchung.getCurrentRow().getRowState() == IBusinessObject.statusChanged)
				{
					// DR laden
					coDienstreise.loadByID(coBuchung.getDienstreiseID());
					coDienstreise.begin();
					
					// änderbare Daten auf DR übertragen
					uebertrageValue(coBuchung, coDienstreise, CoDienstreise.getResIdFieldAbgerechnet());
					uebertrageValue(coBuchung, coDienstreise, CoDienstreise.getResIdFieldReisekostenabrechnung());
					uebertrageValue(coBuchung, coDienstreise, CoDienstreise.getResIdFieldBemerkungBH());
					
					// DR speichern
					coDienstreise.save();
				}
			} while(coBuchung.moveNext());
		}
		
		// Tabelle neu laden
		coBuchung.cancel();
		loadData();
		
//		doAfterSave();
		
		return true;
	}


	/**
	 * Wert von coBuchung auf CoDienstreise übertragen
	 * 
	 * @param coBuchung
	 * @param coDienstreise
	 * @param resID
	 */
	private void uebertrageValue(CoBuchung coBuchung, CoDienstreise coDienstreise, String resID) {
		coDienstreise.getField(resID).setValue(coBuchung.getField(resID).getValue());
	}


}
