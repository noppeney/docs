package pze.ui.formulare.projektverwaltung;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.actions.IActionListener;
import framework.business.interfaces.data.IBusinessObject;
import pze.business.UserInformation;
import pze.business.navigation.NavigationManager;
import pze.business.objects.CoMessage;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattProjekt;
import pze.business.objects.projektverwaltung.CoMitarbeiterProjekt;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.TableDeleteListener;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular der Mitarbeiter für ein Projekt
 * 
 * @author Lisiecki
 *
 */
public class FormProjektMitarbeiter extends UniFormWithSaveLogic {
	public static final String RESID = "form.projekt.mitarbeiter";

	private FormProjekt m_formProjekt;
	
	protected SortedTableControl m_table;
	
	protected CoProjekt m_coProjekt;
	protected CoMitarbeiterProjekt m_coMitarbeiterProjekt;
	protected CoMessage m_coMessage;
			
	
	private IActionListener m_addlistener;
	private IActionListener m_deletelistener;

	

	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coProjekt Co der geloggten Daten
	 * @param formProjekt Hauptformular des Projekts
	 * @throws Exception
	 */
	public FormProjektMitarbeiter(Object parent, CoProjekt coProjekt, UniFormWithSaveLogic formProjekt) throws Exception {
		this(parent, coProjekt, formProjekt, RESID);
	}


	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coProjekt Co der geloggten Daten
	 * @param formProjekt Hauptformular des Projekts
	 * @param resid resid des Formulars
	 * @throws Exception
	 */
	public FormProjektMitarbeiter(Object parent, CoProjekt coProjekt, UniFormWithSaveLogic formProjekt, String resid) throws Exception {
		super(parent, resid, true);

		m_formProjekt = (FormProjekt) formProjekt;
		m_coProjekt = coProjekt;
		
		// CO anlegen und laden
		initCo();
		loadData();
		if (m_coProjekt.isEditing())
		{
			m_coMitarbeiterProjekt.begin();
		}
		
		// CO als Child dem Projekt zuordnen, damit das speichern funktioniert
		m_coProjekt.addChild(m_coMitarbeiterProjekt);

		// Tabelle anlegen
		initTable();
		m_addlistener = new AddListener();
		m_deletelistener = new TableDeleteListener(this, m_coMitarbeiterProjekt, m_table);

		refresh(reasonDisabled, null);
	}


	/**
	 * CO anlegen
	 * 
	 * @throws Exception
	 */
	protected void initCo() throws Exception {
		m_coMitarbeiterProjekt = new CoMitarbeiterProjekt(m_coProjekt);
	}


	/**
	 * Tabelle anlegen
	 * 
	 * @throws Exception
	 */
	protected void initTable() throws Exception {
		m_table = new TableProjektMitarbeiter(findControl("spread.projekt.mitarbeiter"), m_coMitarbeiterProjekt);
	}


	/**
	 * Daten laden
	 * 
	 * @throws Exception
	 */
	protected void loadData() throws Exception {
		m_coMitarbeiterProjekt.loadByProjekt(true);
		
		setData(m_coMitarbeiterProjekt);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "mitarbeiter.projekt." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	
	

	@Override
	public void activate() {
//		addExcelExportListener(m_coAbruf, m_table, "Uebersicht_Abrufe", Profile.KEY_AUSWERTUNG_PROJEKTE);

		try 
		{
			if (NavigationManager.getSelectedTabItemKey() != null && NavigationManager.getSelectedTabItemKey().equals(getKey(0)))
			{
				try
				{
					if (UserInformation.getInstance().isPersonalverwaltung() || m_formProjekt.isProjektbearbeiter())
					{
						Action.get("file.new").addActionListener(m_addlistener);
						Action.get("edit.delete").addActionListener(m_deletelistener);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				m_formProjekt.addSaveHandler();
				refreshByEditMode();
				updateEditToolbarButton();
				
				// Items der Phasen aktualisieren
				m_coMitarbeiterProjekt.updateItemsPhasen();
			
				super.activate();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void deactivate() {
		try
		{
			if (UserInformation.getInstance().isPersonalverwaltung() || m_formProjekt.isProjektbearbeiter())
			{
				Action.get("file.new").removeActionListener(m_addlistener);
				Action.get("edit.delete").removeActionListener(m_deletelistener);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		m_formProjekt.removeSaveHandler();
		super.deactivate();
	}


	/**
	 * neuen Datensatz hinzufügen
	 *
	 */
	class AddListener extends ActionAdapter
	{
		@Override
		public void activate(Object sender) throws Exception {
			m_coMitarbeiterProjekt.createNew();
//			m_coFilteredPersonFirma.filter();
			m_table.refresh(reasonDataAdded, m_coMitarbeiterProjekt.getBookmark());
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coMitarbeiterProjekt.isEditing();
		}
	}


	/**
	 * ggf. Meldungen generieren
	 * 
	 */
	@Override
	public void doBeforeSave() throws Exception {
		int rowState;
		boolean projektNeu;
		CoMonatseinsatzblattProjekt coMonatseinsatzblattProjekt;
		
		if (!m_coMitarbeiterProjekt.moveFirst() || m_coMitarbeiterProjekt.hasNoRows())
		{
			return;
		}
		
		// Personen durchlaufen
		coMonatseinsatzblattProjekt = new CoMonatseinsatzblattProjekt();
		m_coMessage = new CoMessage();
		do
		{
			rowState = m_coMitarbeiterProjekt.getCurrentRow().getRowState();
			
			// neue Person zugeordnet
			if (rowState == IBusinessObject.statusAdded)
			{
				// Projekt der Person im Monatseinsatzblatt zuteilen
				projektNeu = coMonatseinsatzblattProjekt.addProjekt(m_coMitarbeiterProjekt.getPersonID(), m_coProjekt);
				
				m_coMessage.createMessageProjektZugeteilt(m_coMitarbeiterProjekt.getPersonID(), m_coProjekt, projektNeu);
			}
			// Daten geändert
			else if (rowState == IBusinessObject.statusChanged)
			{
				m_coMessage.createMessageProjektzuteilungGeaendert(m_coMitarbeiterProjekt.getPersonID(), m_coProjekt, m_coMitarbeiterProjekt);
			}
			
		} while(m_coMitarbeiterProjekt.moveNext());
		
	}


	/**
	 * Meldungen speichern, Daten neu laden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	@Override
	public void doAfterSave() throws Exception {
		// Meldungen speichern
		if (m_coMessage != null && m_coMessage.isEditing())
		{
			m_coMessage.save();
		}
		
		loadData();
		
		m_table.refresh(reasonDataChanged, null);
	}


}
