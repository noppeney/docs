package pze.ui.formulare;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.FW;
import framework.business.interfaces.actions.IActionListener;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.refresh.IRefreshable;
import framework.business.interfaces.session.ISession;
import framework.business.protokoll.FieldLogger;
import framework.business.resources.ResourceMapper;
import framework.business.session.Session;
import framework.cui.layout.UniLayout;
import framework.ui.controls.ComboControl;
import framework.ui.form.UniForm;
import framework.ui.group.UniGroup;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.tabcontrol.TabFolder;
import framework.ui.tabcontrol.TabItem;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.datentransfer.TableExportExcelListener;
import pze.business.export.ExportPdfListener;
import pze.business.navigation.NavigationManager;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoPerson;
import pze.ui.controls.IntegerToUhrzeitControl;
import pze.ui.controls.SortedTableControl;


/**
 * Klasse für die Bearbeitungs- und Speicherlogik der Oberflächen
 * 
 * @author Lisiecki
 *
 */
public abstract class UniFormWithSaveLogic extends UniForm {

	protected IActionListener editlistener;
	protected IActionListener savelistener;
	
	/**
	 * Weitere Formulare, die bei der Bearbeitungs- und Speicherlogik berücksichtigt werden müssen
	 */
	protected ArrayList<UniFormWithSaveLogic> additionalForms = new ArrayList<UniFormWithSaveLogic>();
	protected boolean isAdditionalForm = false;

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		Parentformular
	 * @param resid			DD-Resource
	 * @throws Exception
	 */
	public UniFormWithSaveLogic(Object parent, String resid) throws Exception {
		super(parent, resid);
		
		this.createChilds();
		updateLayout();
		
		editlistener = new EditListener();
		savelistener = new SaveListener();
	}


	/**
	 * Konstruktor
	 * 
	 * @param parent		Parentformular
	 * @param resid			DD-Resource
	 * @throws Exception
	 */
	public UniFormWithSaveLogic(Object parent, String resid, boolean isAdditionalForm) throws Exception {
		this(parent, resid);
		this.isAdditionalForm = isAdditionalForm;
	}


	/**
	 * Layout aktualisieren
	 */
	private void updateLayout() {
		UniLayout layout = new UniLayout();
		this.setLayout(layout);
		layout.setControl(this);
	}
	

	/**
	 * weiteres Formular hinzufügen
	 * @param form weiteres Formular
	 */
	public void addAdditionalForm(UniFormWithSaveLogic form)
	{
		if (!additionalForms.contains(form))
			additionalForms.add(form);
	}

	
	/**
	 * Formular löschen
	 * @param form weiteres Formular
	 */
	public void removeAdditionalForm(UniFormWithSaveLogic form)
	{
		if (additionalForms.contains(form))
			additionalForms.remove(form);
	}

	
	/* (non-Javadoc)
	 * @see framework.ui.form.UniForm#activate()
	 */
	@Override
	public void activate() {
			
		// Handler müssen für untergeordnete Formulare nicht hinzugefügt werden
		if (!isAdditionalForm )
		{
			addSaveHandler();
			refreshByEditMode();
	
			super.activate();
		}
		
		updateEditToolbarButton();
	}


	/**
	 * Oberfläche in Abhängigkeit vom aktuellen Edit-Mode aktualisieren
	 */
	public void refreshByEditMode() {
		if (getData().isEditing())
		{
			refresh(IRefreshable.reasonEnabled, null);
		}
		else
		{
			refresh(IRefreshable.reasonDisabled, null);
		}
		
		refresh(IRefreshable.reasonDataChanged, null);
	}
	

	/**
	 * Save/Edit-Handler hinzufügen 
	 */
	public void addSaveHandler() {
		if (mayEdit())
		{
			Action.get("file.edit").addActionListener(editlistener);
			Action.get("file.save").addActionListener(savelistener);
		}
	}

	
	/* (non-Javadoc)
	 * @see framework.ui.form.UniForm#deactivate()
	 */
	@Override
	public void deactivate() {
	
		// Handler gibt es für untergeordnete Formulare nicht
		if (!isAdditionalForm)
		{
			removeSaveHandler();
			super.deactivate();
		}
		
		removePdfExportListener();
		removeExcelExportListener();

		updateEditToolbarButton(false);
	}

	
	/**
	 * Edit/Save Hander löschen
	 */
	public void removeSaveHandler() {
		Action.get("file.edit").removeActionListener(editlistener);
		Action.get("file.save").removeActionListener(savelistener);
	}
	

	/**
	 * Aktion zum Excel-Export der übergebenen Tabelle aktivieren
	 * 
	 * @param co
	 * @param table
	 * @param dateiname 
	 * @param profilePathKey 
	 */
	public void addExcelExportListener(AbstractCacheObject co, SortedTableControl table, String dateiname, String profilePathKey){
		removeExcelExportListener();
		Action.get("export.excel").addActionListener(new TableExportExcelListener(co, table, dateiname, profilePathKey));
	}
	
	
	/**
	 * Aktion zum Excel-Export der übergebenen Tabelle aktivieren
	 * 
	 */
	public void removeExcelExportListener(){
		Action.get("export.excel").removeActionListeners();
	}
	

	/**
	 * Aktion zum PDF-Export der übergebenen Tabelle aktivieren
	 * 
	 * @param co
	 * @param table
	 */
	public void addPdfExportListener(ExportPdfListener exportPdfListener){
		Action.get("export.pdf").addActionListener(exportPdfListener);
	}
	
	
	/**
	 * Aktion zum PDF-Export der übergebenen Tabelle aktivieren
	 * 
	 */
	public void removePdfExportListener(){
		Action.get("export.pdf").removeActionListeners();
	}
	

	@Override
	public void refresh(int reason, Object element) {
		
		// prüfen, ob ein Feld bearbeitet werden darf
		boolean mayedit = mayEdit();
		if (reason == reasonEnabled && !mayedit)
		{
			reason = IRefreshable.reasonDisabled;
		}
		
		super.refresh(reason, element);
	}


	/**
	 * Aktualisieren der Items einer Combo
	 * 
	 * @throws Exception
	 */
	public static void refreshItems(ComboControl comboControl, AbstractCacheObject co, IField field) throws Exception {
		int id;
		
		field.setItems(co);
		
		id = Format.getIntValue(field.getValue());
		if (!co.moveToID(id))
		{
			field.setValue(null);
		}
		
		comboControl.setField(field);
		comboControl.refresh(IRefreshable.reasonDataChanged, null);
		comboControl.refresh(IRefreshable.reasonItemsChanged, null);
	}
	

	/**
	 * Aktualisieren der Items einer Tabelle
	 * 
	 * @throws Exception
	 */
	public static void refreshItems(SortedTableControl table, AbstractCacheObject coTable, IField field, AbstractCacheObject coField) throws Exception {
		
		field.setItems(coField);
		
		table.setData(coTable);
		table.refresh(IRefreshable.reasonDataChanged, null);
		table.refresh(IRefreshable.reasonItemsChanged, null);
	}
	

	/**
	 * @return hat der Benutzer die Berechtigung zum editieren?
	 */
	public boolean mayEdit() {// TODO berechtigungen über Ressourcemapper anschauen
		int [] groups = getSession().getUserInfo().getGroups();			
		boolean mayedit = ResourceMapper.getInstance().isEnabled(getResID(), groups);
		return mayedit;
	}
	

	/**
	 * wird vor dem Speichern aufgerufen
	 */
	public void doBeforeSave() throws Exception {
		
	}


	/**
	 * wird nach dem Speichern aufgerufen
	 */
	public void doAfterSave() throws Exception {

	}


	/**
	 * Caption und Key des Formulars anpassen an den Inhalt
	 */
	protected void refreshTabItem() {
		AbstractCacheObject co;
		TabItem tabItem;
		
		co = getCo();
		tabItem = NavigationManager.getSelectedTabItem();
		
		// Tab nur anpassen, wenn es noch geöffnet ist. Wenn beim Schließen gespeichert wird, erfolgt keine Anpassung
		if (co != null && tabItem != null && tabItem.getKey().equals(getKey()))
		{
			tabItem.setCaption(co.getBezeichnung());
			tabItem.setKey(getKey());
		}
	}


	/**
	 * Wenn ein neues CO auf ein Formular gesetzt wird, müssen die Uhrzeiten zurückgesetzt werden, da sonst der angezeigte (alte) Wert übernommen wird.<br>
	 * Word rekursiv für Groups aufgerufen
	 */
	protected void resetIntegerToUhrzeitControls() {
		Iterator<IControl> iterControls;
		
		iterControls = getControls();
		
		resetIntegerToUhrzeitControls(iterControls);
		
	}


	/**
	 * Wenn ein neues CO auf ein Formular gesetzt wird, müssen die Uhrzeiten zurückgesetzt werden, da sonst der angezeigte (alte) Wert übernommen wird.<br>
	 * Word rekursiv für Groups aufgerufen
	 * 
	 * @param iterControls
	 */
	private void resetIntegerToUhrzeitControls(Iterator<IControl> iterControls) {
		IControl control;
		while (iterControls.hasNext())
		{
			control = iterControls.next();

			if (control instanceof IntegerToUhrzeitControl)
			{
				((IntegerToUhrzeitControl) control).resetValue();
			}
			
			// Funktion rekursiv ausrufen
			else if (control instanceof UniGroup)
			{
				resetIntegerToUhrzeitControls(((UniGroup) control).getControls());
			}
		}
	}


	/**
	 * CO mit den Daten
	 * 
	 * @return
	 */
	public AbstractCacheObject getCo() {
		return (AbstractCacheObject) getData();
	}


	/**
	 * ID des CO mit den Daten
	 * 
	 * @return
	 */
	protected int getID() {
		return getCo().getID();
	}

	
	/**
	 * Eindeutiger Key für das Formular.<br>
	 * Funktion muss für die Formulare der obersten Reiterebene überschrieben werden.
	 * 
	 * @param id
	 * @return
	 */
	public abstract String getKey();
	

	/**
	 * @return wurden Daten verändert?
	 */
	protected boolean isModified(){
		return getData().isModified();
	}
	
	
	@Override
	public boolean canclose() {
		int messageAntwort;
		String caption;
		
		// untergeordnete Formulare können nicht geschlossen werden
		if (isAdditionalForm)
		{
			return true;
		}

		try
		{
			// TODO Fehlerüberprüfung ist unterschiedlich, wenn auf Speichern geklickt oder mit x geschlossen wird
			// z. B. wenn Tabelen nicht vollständig gefüllt sind
			
			// bei Formularen mit Änderungen erfolgt eine Sicherheitsabfrage, um die Änderungen nicht direkt zu verwerfen
			if (isModified())
			{
				// Focus aus der Tabelle nehmen, falls diese zuletzt bearbeitet wurde (sonst gibt es eine fehlermeldung)
				setFocus();

				caption = NavigationManager.getTabItem(getKey()).getCaption();
	
				messageAntwort = Messages.showYesNoCancelMessage("Formular schließen?", 
						"Alle nicht gespeicherten Änderungen gehen verloren.<br/>Sollen die Änderungen am Formular "
								+ (caption == null || caption.isEmpty() ? "" : ("\"" + caption.replace(">", "").replace("<", "") + "\"")) 
								+ " vor dem Schließen gespeichert werden?");

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
//			e.printStackTrace();
			Messages.showErrorMessage("Die Daten konnten nicht gespeichert werden. "
					+ "Möglicherweise sind nicht alle Tabellen des Formulars vollständig gefüllt oder die WTI-Auftrags-Nr. ist bereits vergeben.");
			return false;
		}

		return super.canclose();
	}

	
	/**
	 * 
	 * @return ist das ein zusätzliches Formular?
	 */
	public boolean isAdditionalForm() {
		return isAdditionalForm;
	}
	
	
	/**
	 * Prüfe, ob das Formular korrekt ausgefüllt ist und speichere die Daten, wenn sie vollständig sind
	 * 
	 * @throws Exception Validieren und Speichern
	 * @return Daten waren korrekt und wurden gespeichert
	 */
	public boolean validateAndSave() throws Exception {
		
		// Pflichtfelder prüfen
		if (!pruefePflichtfelder())
		{
			return false;
		}
	
		// weitere Prüfungen
		if (!validate())
		{
			return false;
		}
			

//		try {
			// speichern des aktuellen Formulars und der untergeordneten Formulare
			doBeforeSave();
			FieldLogger.getInstance().log(getData());
			getData().save();
			for (UniFormWithSaveLogic addForm : additionalForms)
			{
				if  (addForm.getData().isModified())
				{
					addForm.getData().save();
				}

				addForm.refresh(IRefreshable.reasonDisabled, null);
			}
//		}
//		catch (Exception e) 
//		{
//			Messages.showErrorMessage("Die Daten konnten nicht gespeichert werden. Möglicherweise ist die WTI-Auftrags-Nr. bereits vergeben.");
//			return false;
//		}
			
		refresh(IRefreshable.reasonDisabled, null);
		doAfterSave();

		updateEditToolbarButton();
		
		return true;
	}


	/**
	 * Überprüfe die eingegebenen Daten auf Korrektheit.
	 * 
	 * @return
	 * @throws Exception
	 */
	protected boolean validate() throws Exception {
		
		// prüfe Cacheobjekt des aktuellen Formulars
		String fehler = ((AbstractCacheObject)getData()).validate();
		if (fehler != null)
		{
			Messages.showErrorMessage(fehler);
			return false;
		}
		
		
		// prüfe Cacheobjekt der untergeordneten Formulare
		for (UniFormWithSaveLogic addForm : additionalForms)
		{
			fehler = ((AbstractCacheObject)addForm.getData()).validate();
			if (fehler != null)
			{
				Messages.showErrorMessage(fehler);
				return false;
			}
		}
		
		return true;
	}


	/**
	 * Prüfe, ob alle Pflichtfelder ausgefüllt sind.
	 * Wenn nicht, gebe eine Fehlermeldung mit den nicht-ausgefüllten Feldern aus.
	 * 
	 * @return alle Pflichtfelder ausgefüllt
	 */
	protected boolean pruefePflichtfelder() {
		HashSet<IField> schonGeprueft = new HashSet<IField>();
		String felder = ((AbstractCacheObject)getData()).appendPflichtfelderFehler("", schonGeprueft);
		
		for (UniFormWithSaveLogic addForm : additionalForms)
		{
			felder = ((AbstractCacheObject)addForm.getData()).appendPflichtfelderFehler(felder, schonGeprueft);
		}
		if(!felder.isEmpty())
		{
			showMessageFehlerPflichfelder(felder);
			return false;
		}
		
		return true;
	}


	/**
	 * Message wegen fehlender Pflichfelder ausgeben
	 * 
	 * @param felder
	 */
	public static void showMessageFehlerPflichfelder(String felder) {
		Messages.showWarningMessage(
				"Fehlende Pflichtfelder!", 
				"Bitte füllen Sie alle Pflichtfelder aus.<br/> " +
				"Folgende Pflichtfelder fehlen noch:<br/>" + felder);
	}

	
	/**
	 * @param edit Editiermoduls
	 */
	protected void updateEditToolbarButton(boolean edit) {
		super.getSession().getMainFrame().getToolbar().updateEditMode(edit);
	}
	
	
	/**
	 * boolean Toolbarbutton upaten
	 */
	protected void updateEditToolbarButton() {
		if (getData () != null)
		{
			updateEditToolbarButton(getData().isEditing());
		}
	}

	
	/**
	 * Editlistener der Formulare
	 * 
	 * @author Lisiecki
	 *
	 */
	class EditListener extends ActionAdapter {
		/*
		 * (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
		 */
		@Override
		public void activate(Object sender) throws Exception {
			//------- Modus umschalten
			if(getData().isEditing())
			{
				if (sender != null && getData().isModified() && !Messages.showYesNoMessage("Bearbeitung abbrechen",
						"Wollen Sie die Bearbeitung abbrechen?<br>Nicht gespeicherte Änderungen gehen dadurch verloren."))
				{
					return;
				}
				
				getData().cancel();
				for (UniFormWithSaveLogic addForm : additionalForms)
				{
					IBusinessObject data = addForm.getData();
					if (data.isEditing())
						data.cancel();
					addForm.refresh(IRefreshable.reasonDataChanged, null);
					addForm.refresh(IRefreshable.reasonDisabled, null);
				}
				refresh(IRefreshable.reasonDataChanged, null);
				refresh(IRefreshable.reasonDisabled, null);
				
				doAfterCancelEditing();
			}
			else
			{
				getData().begin();
				for (UniFormWithSaveLogic addForm : additionalForms)
				{
					IBusinessObject data = addForm.getData();
					if (!data.isEditing())
						data.begin();
					addForm.refresh(IRefreshable.reasonEnabled, null);
				}
				refresh(IRefreshable.reasonEnabled, null);
			}
			
			//------- Toolbar-Button umschalten
			updateEditToolbarButton();
		}
 
		@Override
		public boolean getEnabled() {
			if (getData().isNew())
			{
				return false;
			}
			else
			{
				return true;
			}
		}
	}
	
	
	/**
	 * Savelistener der Formulare
	 * 
	 * @author Lisiecki
	 *
	 */
	private class SaveListener extends ActionAdapter {
		@Override
		
		public void activate(Object sender) throws Exception {
			validateAndSave();
		}
		
		@Override
		public boolean getEnabled() {		
			boolean ismodified = getData().isModified();
			for (UniFormWithSaveLogic addForm : additionalForms)
			{
				IBusinessObject data = addForm.getData();
				ismodified |= data.isModified();
			}
			return ismodified;
		}
	}

	
	/**
	 * Aktuelles Objekt löschen.
	 * 
	 * @param session Session
	 * @param co Cacheobject mit dem zu löschenden Object
	 * @param bezeichnung Bezeichnung des Objektes mit Artikel (z. B. die Person)
	 * @param key Key des zugehörigen Formulars
	 * @throws Exception
	 */
	public static void deleteObject(ISession session, AbstractCacheObject co, String bezeichnung, String key) throws Exception {

		if (!Messages.showYesNoMessage("Daten löschen?", "Soll " + bezeichnung + " \"" + co.getBezeichnung() + "\" wirklich gelöscht werden?"))
		{
			return;
		}
		
		// Datensatz löschen
		if (!co.isEditing())
		{
			co.begin();
		}
//		co.setStatusGeloescht();
		co.delete();
		co.save();
		
		// Oberfläche aktualisieren
		ITabFolder editFolder = session.getMainFrame().getEditFolder();
		editFolder.remove(key);
		NavigationManager.getInstance().reloadRootNode();
		
		
		refreshAllForms();
	}


	/**
	 * Aktionen nach dem Abbrechen der Bearbeitung
	 * @throws Exception 
	 */
	public void doAfterCancelEditing() throws Exception {
		
	}


	public static void refreshAllForms() {
		// TabItems holen
		ITabFolder tf = Session.getInstance().getMainFrame().getEditFolder();
		Iterator<ITabItem> iterTabItems = tf.getTabItems();

		// alle Tabs durchlaufen
		while (iterTabItems.hasNext())
		{
			// Tabinformationen auslesen
			ITabItem tabItem = iterTabItems.next();
			IControl control = tabItem.getControl();

			// wenn es ein UniFormWithSaveLogic ist, prüfe ob es geschlossen werden kann
			UniFormWithSaveLogic form = getUniFormWithSaveLogic(control);
			if (form != null)
			{
				form.refresh(reasonDataChanged, null);
			}
		}
	}


	/**
	 * Prüft, ob die angemeldete Person die Daten zur übergebenen PersonID sehen darf
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	public static boolean maySeePerson(int personID) throws Exception {
		CoPerson coPerson;
		
		// die Daten der eigenen Person dürfen immer geöffnet werden
		if (UserInformation.getPersonID() == personID)
		{
			return true;
		}

		// Personen der Personalansicht dürfen alle personen sehen
		if (UserInformation.getInstance().isPersonalansicht())
		{
			return true;
		}
		
		// um Personendaten von anderen sehen zu können, muss man mindestens AL sein
		if (!UserInformation.getInstance().isAL())
		{
			return false;
		}
		
		// Berechtigungen laden
		coPerson = new CoPerson();
		coPerson.loadItemsOfCurrentUser();
		
		return coPerson.moveToID(personID);
	}


	public static UniFormWithSaveLogic getUniFormWithSaveLogic(IControl control){
		
		try 
		{
			// wenn das übergebene Control ein Tabfolder ist, hole dir das Control des Tabfolders
			if (control instanceof TabFolder)
			{
				Iterator<ITabItem> tabItems = ((TabFolder) control).getTabItems();
				while ( tabItems.hasNext())
				{
					ITabItem next = tabItems.next();
					if (next.getControl() instanceof UniFormWithSaveLogic)
					{
						UniFormWithSaveLogic form = (UniFormWithSaveLogic) next.getControl();
						if (!form.isAdditionalForm() )
							control = form;
					}
				}
			}
			
			// versuche das UniFormWithSaveLogic-Objekt des Controls zurückzugeben
			return (UniFormWithSaveLogic) control;
		}
		catch (Exception e) 
		{
			// im Fehlerfall, gebe null zurück
			return null;
		}
	}

}
