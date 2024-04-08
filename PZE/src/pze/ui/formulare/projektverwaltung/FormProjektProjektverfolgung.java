package pze.ui.formulare.projektverwaltung;

import java.util.Date;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.actions.IActionListener;
import pze.business.UserInformation;
import pze.business.navigation.NavigationManager;
import pze.business.objects.CoMessage;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.projektverwaltung.CoProjektverfolgung;
import pze.business.objects.reftables.CoMessageQuittierung;
import pze.business.objects.reftables.CoStatusMessage;
import pze.business.objects.reftables.projektverwaltung.CoAktionProjektverfolgung;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular der Projektverfolgung für ein Projekt
 * 
 * @author Lisiecki
 *
 */
public class FormProjektProjektverfolgung extends UniFormWithSaveLogic {
	public static final String RESID = "form.projekt.projektverfolgung";

	private FormProjekt m_formProjekt;
	
	protected TableProjektProjektverfolgung m_table;
	
	protected CoProjekt m_coProjekt;
	protected CoProjektverfolgung m_coProjektverfolgung;
	
	private IActionListener m_addlistener;
//	private IActionListener m_deletelistener;
	
	private int m_meldungID;

	

	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param m_coProjektverfolgung Co der geloggten Daten
	 * @param formProjekt Hauptformular des Projekts
	 * @throws Exception
	 */
	public FormProjektProjektverfolgung(Object parent, CoProjekt coProjekt, UniFormWithSaveLogic formProjekt) throws Exception {
		this(parent, coProjekt, formProjekt, RESID);
	}


	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param m_coProjektverfolgung Co der geloggten Daten
	 * @param formProjekt Hauptformular des Projekts
	 * @param resid resid des Formulars
	 * @throws Exception
	 */
	public FormProjektProjektverfolgung(Object parent, CoProjekt coProjekt, UniFormWithSaveLogic formProjekt, String resid) throws Exception {
		super(parent, resid, true);

		m_formProjekt = (FormProjekt) formProjekt;
		m_coProjekt = coProjekt;
		
		// CO anlegen und laden
		initCo();
		loadData();
		if (m_coProjekt.isEditing())
		{
			m_coProjektverfolgung.begin();
		}
		
		// CO als Child dem Projekt zuordnen, damit das speichern funktioniert
		m_coProjekt.addChild(m_coProjektverfolgung);

		// Tabelle anlegen
		initTable();
		m_addlistener = new AddListener();
//		m_deletelistener = new TableDeleteListener(this, m_coProjektverfolgung, m_table);

		refresh(reasonDisabled, null);
	}


	/**
	 * CO anlegen
	 * 
	 * @throws Exception
	 */
	protected void initCo() throws Exception {
		m_coProjektverfolgung = new CoProjektverfolgung(m_coProjekt, this);
	}


	/**
	 * Tabelle anlegen
	 * 
	 * @throws Exception
	 */
	protected void initTable() throws Exception {
//		m_table = new SortedTableControl(findControl("spread.projekt.projektverfolgung"));
//		m_table.setData(m_coProjektverfolgung);
		m_table = new TableProjektProjektverfolgung(findControl("spread.projekt.projektverfolgung"), m_coProjektverfolgung, this);
	}


	/**
	 * Daten laden
	 * 
	 * @throws Exception
	 */
	protected void loadData() throws Exception {
		m_coProjektverfolgung.loadByProjekt();
		
		setData(m_coProjektverfolgung);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "projekt.projektverfolgung." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	/**
	 * Modus setzen und zur Aktivierung der Spalten an Tabelle übergeben
	 * 
	 * @param meldungID
	 */
	public void setModus(int meldungID) {
		m_meldungID = meldungID;
		m_table.enableColumns();
	}
	

	/**
	 * Formular befindet sich in einem Modus der Projektverfolgung
	 * 
	 * @return
	 */
	public boolean hasModus() {
		return m_meldungID > 0;
	}
	

	public boolean isModusPL() {
		return m_meldungID == CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN;
	}


	public void setModusPL() {
		m_meldungID = CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN;
	}


	public boolean isModusAL() {
		return m_meldungID == CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_PRUEFEN;
	}


	public void setModusAL() {
		m_meldungID = CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_PRUEFEN;
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
					if ((UserInformation.getInstance().isPersonalverwaltung() || m_formProjekt.isProjektbearbeiter()))
					{
						Action.get("file.new").addActionListener(m_addlistener);
//						Action.get("edit.delete").addActionListener(m_deletelistener);
					}
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				m_formProjekt.addSaveHandler();
				refreshByEditMode();
				updateEditToolbarButton();
			
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
			if ((UserInformation.getInstance().isPersonalverwaltung() || m_formProjekt.isProjektbearbeiter()))
			{
				Action.get("file.new").removeActionListener(m_addlistener);
//				Action.get("edit.delete").removeActionListener(m_deletelistener);
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
	 * Aufruf der Add-Funktion zum Hinzufügen eines neuen Datensatzes
	 * 
	 * @throws Exception
	 */
	public void add() throws Exception {
		m_addlistener.activate(null);
	}
	
	
	/**
	 * neuen Datensatz hinzufügen
	 *
	 */
	class AddListener extends ActionAdapter
	{
		@Override
		public void activate(Object sender) throws Exception {
			m_coProjektverfolgung.createNew();
			m_table.refresh(reasonDataAdded, m_coProjektverfolgung.getBookmark());
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coProjektverfolgung.isEditing();
		}
	}


	/**
	 * ggf. Meldungen generieren
	 * 
	 */
	@Override
	public void doBeforeSave() throws Exception {
//		int rowState;
//		boolean projektNeu;
//		CoMonatseinsatzblattProjekt coMonatseinsatzblattProjekt;
//		
//		if (!m_coProjektverfolgung.moveFirst() || m_coProjektverfolgung.hasNoRows())
//		{
//			return;
//		}
		
		// Personen durchlaufen
//		coMonatseinsatzblattProjekt = new CoMonatseinsatzblattProjekt();
//		m_coMessage = new CoMessage();
//		do
//		{
//			rowState = m_coProjektverfolgung.getCurrentRow().getRowState();
//			
//			// neue Person zugeordnet
//			if (rowState == IBusinessObject.statusAdded)
//			{
//				// Projekt der Person im Monatseinsatzblatt zuteilen
//				projektNeu = coMonatseinsatzblattProjekt.addProjekt(m_coProjektverfolgung.getPersonID(), m_coProjekt);
//				
//				m_coMessage.createMessageProjektZugeteilt(m_coProjektverfolgung.getPersonID(), m_coProjekt, projektNeu);
//			}
//			// Daten geändert
//			else if (rowState == IBusinessObject.statusChanged)
//			{
//				m_coMessage.createMessageProjektzuteilungGeaendert(m_coProjektverfolgung.getPersonID(), m_coProjekt, m_coProjektverfolgung);
//			}
//			
//		} while(m_coProjektverfolgung.moveNext());
		
	}


	/**
	 * Meldungen zur Projektverfolgung erstellen oder löschen
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	@Override
	public void doAfterSave() throws Exception {
		int aktionID;
		Date terminAL, terminPL;
		
		// nach Eingabe AL
		if (isModusAL())
		{
			// Meldungen AL auf quittiert setzen
			quittiereMeldungen(CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_PRUEFEN);
			
			// Meldungen PL auf quittiert setzen, falls er keine Eingabe gemacht hat
			quittiereMeldungen(CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN);
			
			// Meldung an PL, wenn seine Prognose nicht vom AL übernommen wurde
			terminAL = m_coProjektverfolgung.getTerminAL();
			terminPL = m_coProjektverfolgung.getTerminPL();
			if (m_coProjektverfolgung.getPLID() != 0
					&& ( (terminAL != null && terminPL != null && !terminAL.equals(m_coProjektverfolgung.getTerminPL()))
							|| (m_coProjektverfolgung.getKostenAL() != m_coProjektverfolgung.getKostenPL())
							))
			{
				new CoMessage().createMessageProjektTerminKostenAenderungUnterschied(m_coProjekt, m_coProjektverfolgung);
			}

			// Meldung für die Buchhaltung erzeugen, falls eine Aktion ausgewählt wurde
			aktionID = m_coProjektverfolgung.getAktionID();
			if (aktionID == CoAktionProjektverfolgung.STATUSID_AENDERUNGEN_UEBERNEHMEN)
			{
				new CoMessage().createMessageProjektTerminKostenAenderungUebernehmen(m_coProjekt, m_coProjektverfolgung);
			}
			else if (aktionID == CoAktionProjektverfolgung.STATUSID_PROJEKT_SCHLIESSEN)
			{
				new CoMessage().createMessageProjektSchliessen(m_coProjekt, m_coProjektverfolgung);
			}
		}
		else if (isModusPL()) // nach Eingabe PL
		{
			// Meldungen PL auf quittiert setzen
			quittiereMeldungen(CoMessageQuittierung.ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN);
			
			// Projekt zur Prüfung an den AL weiterleiten
			new CoMessage().createMessageProjektverfolgungPrognosePruefen(m_coProjekt, true);
		}
		
		loadData();
		
		m_table.refresh(reasonDataChanged, null);
	}


	/**
	 * Meldungen auf Status quittiert setzen
	 * 
	 * @param meldungID Art der Meldungen
	 * @throws Exception
	 */
	private void quittiereMeldungen(int meldungID) throws Exception {
		CoMessage coMessage;
		
		// Meldungen laden
		coMessage = new CoMessage();
		coMessage.loadByProjekt(m_coProjekt, meldungID);
		if (!coMessage.moveFirst())
		{
			return;
		}
		
		// Meldungen auf quittiert setzen
		coMessage.begin();
		do
		{
			// Prüfung, ob Status bereits auf quittiert steht
			if (coMessage.getStatusID() == CoStatusMessage.STATUSID_QUITTIERT)
			{
				continue;
			}
			
			// sonst Status anpassen
			coMessage.setStatusQuittiert();
			coMessage.updateGeaendertVonAm();
		} while (coMessage.moveNext());
		coMessage.save();
	}


}
