package pze.ui.formulare.person;

import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.IFocusListener;
import pze.business.UserInformation;
import pze.business.navigation.NavigationManager;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoUser;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular der verwaltungstechnischen Daten einer Person
 * 
 * @author Lisiecki
 *
 */
public class FormPersonVerwaltung extends UniFormWithSaveLogic {
	public static final String RESID = "form.person.verwaltung";

	private CoPerson m_coPerson;
			
	private UniFormWithSaveLogic m_formPerson;
	
//	private ComboControl m_comboAbteilung;
	private ComboControl m_comboBenutzer;


	
	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coPerson Co der geloggten Daten
	 * @param formPerson Hauptformular der Person
	 * @throws Exception
	 */
	public FormPersonVerwaltung(Object parent, CoPerson coPerson, UniFormWithSaveLogic formPerson) throws Exception {
		super(parent, RESID, true);
		
		m_formPerson = formPerson;
		m_coPerson = coPerson;

//		m_comboAbteilung = (ComboControl) findControl("form.person.abteilungid");
		m_comboBenutzer = (ComboControl) findControl("form.person.userid");

		initListener();
		
		setData(m_coPerson);
		
		refresh(reasonDisabled, null);
	}


	/**
	 * Listener für Buttons etc. initialisieren
	 */
	private void initListener() {
		
		// beim Focus auf die Combobox werden alle nicht-zugeordneten User geladen
		m_comboBenutzer.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try
				{
					// beim Verlust des Fovus wieder alle Benutzer laden
					resetItemsComboBenutzer();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void focusGained(IControl control) {
				try
				{
					refreshItemsComboBenutzerByUserID();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "personverwaltung" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	@Override
	public void activate() {
		
		if (NavigationManager.getSelectedTabItemKey() != null && NavigationManager.getSelectedTabItemKey().equals(getResID()))
		{
			m_formPerson.addSaveHandler();
			refreshByEditMode();
			super.activate();
		}
	}
	
	
	@Override
	public void deactivate() {
		m_formPerson.removeSaveHandler();
		super.deactivate();
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
	

	/**
	 * Aktualisieren der Items der Benutzer-Combo, damit nur die nicht-zugeordneten Benutzer angezeigt werden
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboBenutzerByUserID() throws Exception {
		int userID;
		CoUser coUser;

		// alle nicht-zugeordneten Benutzer laden
		userID = m_coPerson.getUserID();
		coUser = new CoUser();
		coUser.loadAllUnused(userID);
		
		refreshItemsComboBenutzer(coUser);
	}
	

	/**
	 * Zurücksetzen der Items der Benutzer-Combo
	 * 
	 * @throws Exception
	 */
	private void resetItemsComboBenutzer() throws Exception {
		CoUser coUser;

		// alle Benutzer laden
		coUser = new CoUser();
		coUser.loadAll();
		
		refreshItemsComboBenutzer(coUser);
	}


	/**
	 * Setzen der Items der Benutzer-Combo
	 * 
	 * @param coUser Items
	 * @throws Exception
	 */
	private void refreshItemsComboBenutzer(CoUser coUser) throws Exception {
		int userID;

		userID = m_coPerson.getUserID();
		refreshItems(m_comboBenutzer, coUser, userID);
		
		// ausgewählte Person wieder setzen
		if (userID != 0)
		{
			m_coPerson.setUserID(userID);
			refresh(reasonDataChanged, null);
		}
	}
	

	/**
	 * Aktualisieren der Items der Combo
	 * 
	 * @param combo zu aktualisierendes Combo-Control
	 * @param coItems CO mit den neuen Items
	 * @param currentID aktuell ausgewählte ID im ComboControl
	 * @throws Exception
	 */
	private void refreshItems(ComboControl combo, AbstractCacheObject coItems, int currentID) throws Exception {
		IField field;
		IBusinessObject items;
		
		// TODO setitems über m_coPerson.getField.setItems bzw uniformwithsave.refreshItems probieren, wenn so noch fehler auftreten beim aktualisieren
		
		field = combo.getField();
		items = field.getItems();
		
		if (!items.isEditing())
		{
			items.begin();
		}
		
		// alte Items löschen
		while (items.moveFirst())
		{
			items.delete();
		}
		
		// neue Items hinzufügen
		if (coItems.moveFirst())
		{
			do
			{
				items.add();
				items.getField(0).setValue(coItems.getField(0).getValue());
				items.getField(1).setValue(coItems.getField(1).getValue());

			} while (coItems.moveNext());
		}

		field.setItems(items);
		
		// wenn die Gruppe nicht in der neuen Liste vorkommt, lösche sie
		if (!coItems.moveToID(currentID))
		{
			field.setValue(null);
		}
		
		combo.setField(field);		
		combo.refresh(reasonDataChanged, null);
		combo.refresh(reasonItemsChanged, null);
	}
	

}
