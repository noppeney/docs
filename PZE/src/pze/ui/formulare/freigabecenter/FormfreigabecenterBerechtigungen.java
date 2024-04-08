package pze.ui.formulare.freigabecenter;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.FW;
import framework.business.interfaces.actions.IActionListener;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.TableDeleteListener;
import pze.ui.formulare.UniFormWithSaveLogic;

/**
 * Formular für Freigabeberechtigungen
 * 
 * @author Lisiecki
 *
 */
public class FormfreigabecenterBerechtigungen extends UniFormWithSaveLogic {
	
	public static String RESID = "form.berechtigungen.freigaben";

	private CoFreigabeberechtigungen m_coFreigabeberechtigungen;
	private CoFreigabeberechtigungen m_coFreigabeberechtigungenUser;

	private SortedTableControl m_tableBerechtigungen;
	
	private IActionListener addlistener, deletelistener;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param statusAktivInaktiv 
	 * @throws Exception
	 */
	public FormfreigabecenterBerechtigungen(Object parent) throws Exception {
		super(parent, RESID);
		
		m_coFreigabeberechtigungen = new CoFreigabeberechtigungen();
		m_coFreigabeberechtigungen.loadForFreigabecenter(UserInformation.getPersonID());
		setData(m_coFreigabeberechtigungen);

		m_coFreigabeberechtigungenUser = new CoFreigabeberechtigungen();
		m_coFreigabeberechtigungenUser.loadByPersonID(UserInformation.getPersonID());

		initTable();
		
		addlistener = new AddListener();
		deletelistener = new TableDeleteListener(this, m_coFreigabeberechtigungen, m_tableBerechtigungen);
	}


	private void initTable() throws Exception {

		m_tableBerechtigungen = new SortedTableControl(findControl("spread.berechtigungen.freigaben")) {

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
//				FormPerson.open(getSession(), null, m_coFreigabeberechtigungen.getPersonID());
			}
			
			
			@Override
			public boolean mayEdit(Object bookmark, ISpreadCell cell) throws Exception {
				Object value;
				String resID;
				
				resID = cell.getColumnDescription().getResID();

				// die eigenen Berechtigungen dürfen nicht bearbeitet werden
				if (m_coFreigabeberechtigungen.getPersonID() == UserInformation.getPersonID())
				{
					return false;
				}
				
				// "Vertreter für" darf nicht bearbeitet werden
				if (resID.contains("vertreter"))
				{
					return false;
				}

				// Person & Datum darf bearbeitet werden
				if (resID.contains("datum") || resID.contains("person"))
				{
					return true;
				}

				// nicht mehr Berechtigungen vergeben als man selbst hat
				value = m_coFreigabeberechtigungenUser.getField(resID).getValue();
				if (value != null && value instanceof Boolean)
				{
					return Format.getBooleanValue(value);
				}

				return false;
			}

		};
		

		// nur aktive Personen anzeigen
		CoPerson coPerson;
		coPerson = new CoPerson();
		coPerson.loadItemsAktivIntern();
		m_coFreigabeberechtigungen.getFieldPersonID().setItems(coPerson);

		
		m_tableBerechtigungen.setData(m_coFreigabeberechtigungen);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "freigabeberechtigungen." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	
	
	/**
	 * Das Formular darf nicht bearbeitet werden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#mayEdit()
	 */
	@Override
	public boolean mayEdit() {
		return true;
	}
	
	
	/**
	 * Überschreibt die übergeordnete Funktion, da hier ein additionalForm gespeichert werden soll
	 * 
	 */
	@Override
	public boolean canclose() {
		int messageAntwort;

		try
		{
			// bei Formularen mit Änderungen erfolgt eine Sicherheitsabfrage, um die Änderungen nicht direkt zu verwerfen
			if (isModified())
			{
				// Focus aus der Tabelle nehmen, falls diese zuletzt bearbeitet wurde (sonst gibt es eine fehlermeldung)
				setFocus();

				messageAntwort = Messages.showYesNoCancelMessage("Formular schließen?", 
						"Alle nicht gespeicherten Änderungen gehen verloren.<br/>Sollen die Änderungen vor dem Schließen gespeichert werden?");

				// Formular speichern und schließen, wenn das Speichern erfolgreich war
				if (messageAntwort == FW.YES)
				{
					return validateAndSave();
				}
				else if (messageAntwort == FW.NO) // nicht speichern, Formular schließen
				{
					return true;
				}
				else // bei Abbrechen bleibt das Formular offen
				{
					return false;
				}
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			Messages.showErrorMessage("Die Daten konnten nicht gespeichert werden. "
					+ "Möglicherweise sind nicht alle Daten des Formulars vollständig gefüllt.");
			return false;
		}

		return super.canclose();
	}

	
	@Override
	public void activate() {
		Action.get("file.new").addActionListener(addlistener);
		Action.get("edit.delete").addActionListener(deletelistener);
		super.activate();
	}


	@Override
	public void deactivate() {
		Action.get("file.new").removeActionListener(addlistener);
		Action.get("edit.delete").removeActionListener(deletelistener);
		super.deactivate();
	}


	/**
	 * Vertreter-Datensatz hinzufügen
	 *
	 */
	class AddListener extends ActionAdapter
	{
		@Override
		public void activate(Object sender) throws Exception {
			m_coFreigabeberechtigungen.createNew();
			m_tableBerechtigungen.refresh(reasonDataAdded, m_coFreigabeberechtigungen.getBookmark());
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coFreigabeberechtigungen.isEditing();
		}
	}


}
