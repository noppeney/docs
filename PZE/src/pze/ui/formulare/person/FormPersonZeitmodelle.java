package pze.ui.formulare.person;

import java.util.Date;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.actions.IActionListener;
import framework.ui.interfaces.controls.IControl;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.navigation.NavigationManager;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.CoPersonZeitmodell;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.FormZeitmodell;
import pze.ui.formulare.TableDeleteListener;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular der Zeitmodelle einer Person
 * 
 * @author Lisiecki
 *
 */
public class FormPersonZeitmodelle extends UniFormWithSaveLogic {
	public static final String RESID = "form.person.zeitmodell";

	private SortedTableControl m_table;
	
	private CoPerson m_coPerson;
	private CoPersonZeitmodell m_coPersonZeitmodell;
			
	private UniFormWithSaveLogic m_formPerson;
	
	private IActionListener m_addlistener;
	private IActionListener m_deletelistener;

	private boolean m_isModified;
	

	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coPerson Co der geloggten Daten
	 * @param formPerson Hauptformular der Person
	 * @throws Exception
	 */
	public FormPersonZeitmodelle(Object parent, CoPerson coPerson, UniFormWithSaveLogic formPerson) throws Exception {
		super(parent, RESID, true);
		
		m_formPerson = formPerson;

		m_coPerson = coPerson;
		m_coPersonZeitmodell = m_coPerson.getCoPersonZeitmodell();
		if (m_coPerson.isEditing())
		{
			m_coPersonZeitmodell.begin();
		}
		
		setData(m_coPersonZeitmodell);
		m_coPerson.addChild(m_coPersonZeitmodell);

		m_table = new SortedTableControl(findControl("spread.person.zeitmodell")) {

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {

				// im Edit-Modus nicht zu der Meldung wechseln
				if (UserInformation.getInstance().isAdmin() && !FormPersonZeitmodelle.this.getData().isEditing())
				{
					FormZeitmodell.open(getSession(), m_coPersonZeitmodell.getZeitmodellID());
				}
			}
		};
		m_table.setData(m_coPersonZeitmodell);

		m_addlistener = new AddListener();
		m_deletelistener = new TableDeleteListener(this, m_coPersonZeitmodell, m_table);
		
		refresh(reasonDisabled, null);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "personzeitmodell" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	@Override
	public void activate() {
		
		if (NavigationManager.getSelectedTabItemKey() != null && NavigationManager.getSelectedTabItemKey().equals(getResID()))
		{
			try
			{
				if (UserInformation.getInstance().isPersonalverwaltung())
				{
					Action.get("file.new").addActionListener(m_addlistener);
					Action.get("edit.delete").addActionListener(m_deletelistener);
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			m_formPerson.addSaveHandler();
			refreshByEditMode();
			updateEditToolbarButton();
		
			super.activate();
		}
	}
	
	
	@Override
	public void deactivate() {
		try
		{
			if (UserInformation.getInstance().isPersonalverwaltung())
			{
				Action.get("file.new").removeActionListener(m_addlistener);
				Action.get("edit.delete").removeActionListener(m_deletelistener);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		m_formPerson.removeSaveHandler();
		super.deactivate();
	}

	
	/**
	 * Bezeichnungs-Datensatz hinzufügen
	 *
	 */
	class AddListener extends ActionAdapter
	{
		@Override
		public void activate(Object sender) throws Exception {
			m_coPersonZeitmodell.createNew(m_coPerson.getID());
//			m_coFilteredPersonFirma.filter();
			m_table.refresh(reasonDataAdded, m_coPersonZeitmodell.getBookmark());
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coPersonZeitmodell.isEditing();
		}
	}

	
	/**
	 * Zwischenspeichern, ob an den Zeitmodellen etwas geändert wurde. Dann müssen nach dem Speichern die Kontodaten aktualisiert werden. 
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doBeforeSave()
	 */
	public void doBeforeSave() throws Exception {
		m_isModified = m_coPersonZeitmodell.isModified();
	}
	
	
	/**
	 * Aktualisieren aller Kontodaten ab den aktualisierten Eintrag
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#doAfterSave()
	 */
	@Override
	public void doAfterSave() throws Exception {
//		int rowState;
		Date datum, firstModifiedDatum;
		CoKontowert coKontowert;
		
		if (!m_isModified)
		{
			return;
		}
		
		firstModifiedDatum = null;
		
//		// erstes Datum suchen, bei dem es Änderungen gibt
//		m_coPersonZeitmodell.moveFirst();
//		do
//		{
//			rowState = m_coPersonZeitmodell.getCurrentRow().getRowState();
//			if (rowState == CacheObject.statusAdded || rowState == CacheObject.statusChanged)
//			{
//				datum = m_coPersonZeitmodell.getDateGueltigVon();
//				
//				if (firstModifiedDatum == null || firstModifiedDatum.after(datum))
//				{
//					firstModifiedDatum = datum;
//				}
//			}
//		} while (m_coPersonZeitmodell.moveNext());
		
		
		// nur Datum in der Zukunft beachten
		datum = Format.getGregorianCalendar12Uhr(null).getTime();
		if (firstModifiedDatum == null || firstModifiedDatum.before(datum))
		{
			firstModifiedDatum = datum;
		}
		
		
		// Kontowerte aktualisieren
		coKontowert = new CoKontowert();
		coKontowert.updateKontowerteAbDatum(m_coPerson.getID(), firstModifiedDatum);
	}


	/**
	 * Verletzermeldungen dürfen nicht bearbeitet werden
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#refresh(int, java.lang.Object)
	 */
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		if (!UserInformation.getInstance().isPersonalverwaltung())
		{
			super.refresh(reasonDisabled, null);
		}
	}
	

}
