package pze.ui.formulare;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.interfaces.actions.IActionListener;
import framework.business.interfaces.refresh.IRefreshable;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITableControl;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.UserInformation;
import pze.business.datentransfer.DateiHandler;
import pze.business.navigation.NavigationManager;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoDokumente;


/**
 * Dokumentenformular
 * 
 * @author Lisiecki
 *
 */
public class FormDokumente extends UniFormWithSaveLogic {
	public static final String RESID="form.dokumente";

	private ITableControl m_tableDokumente;
	private IControl m_uploadControl;
	private IControl m_openControl;
	
	private AbstractCacheObject m_coObjekt;
	private CoDokumente m_coDokumente;
	
	private UniFormWithSaveLogic m_allgemeineDatenForm;

	private IActionListener m_addlistener;
	private IActionListener m_deletelistener;
	
	private DateiHandler m_dateiHandler;

	

	/**
	 * 
	 * @param parent
	 * @param objekt 
	 * @throws Exception
	 */
	public FormDokumente(Object parent, AbstractCacheObject objekt, UniFormWithSaveLogic allgemeineDatenForm) throws Exception {
		super(parent,RESID,true);
		m_allgemeineDatenForm = allgemeineDatenForm;

		m_coObjekt = objekt;
		m_coDokumente = m_coObjekt.getDokumente();
		if (m_coObjekt.isEditing())
		{
			m_coDokumente.begin();
		}
		
		setData(m_coDokumente);
		m_coObjekt.addChild(m_coDokumente);

		
		m_dateiHandler = new DateiHandler(this, m_coDokumente.getField("field.tbldokumente.dateiname"), "dokumente");

		m_uploadControl = findControl("form.dokumente.upload");
		m_openControl = findControl("form.dokumente.open");

		m_tableDokumente = (ITableControl) findControl("spread.dokumente");
		setFileNames();
		m_tableDokumente.setData(m_coDokumente);
		m_tableDokumente.setSelectionListener(new ISelectionListener() {
			
			@Override
			public void selected(IControl arg0, Object arg1) {
				
				// wenn ein Eintrag ausgewählt wurde, springe im Cacheobjekt zu diesem Feld
				if (arg1 != null)
				{
					m_coDokumente.moveTo(m_tableDokumente.getSelectedBookmark());
				}
				
				// Buttons aktualisieren
				refreshButtons(reasonDataChanged);
			}
			
			@Override
			public void defaultSelected(IControl arg0, Object arg1) {
				
			}
		});

		// beim Anlegen einer neuen Unterkomponente wird das Formular aktiviert, sonst deaktiviert
		refresh(m_coDokumente.isEditing() ? reasonEnabled : reasonDisabled, null);		

		m_addlistener = new AddListener();
		m_deletelistener = new TableDeleteListener(this, m_coDokumente, m_tableDokumente);
	}

	
	/**
	 * setzt alle Dateinamen in eine darstellbare Form (ohne Pfad)
	 */
	private void setFileNames() {
		
		// merken, ob das Cacheobjekt modified ist, da es beim Eintragen der Dateinamen ohne Pfad immer auf modified gesetzt wird
		boolean isModified = m_coDokumente.isModified();
		
		if ( m_coDokumente.moveFirst())
		{
			do
			{
				m_coDokumente.getField("field.tbldokumente.dateiname.pure").setValue(
						AbstractCacheObject.getDisplayDateiName(m_coDokumente.getDateiname()));
			} while ( m_coDokumente.moveNext());
		}
		
		// Status zurücksetzen
		m_coDokumente.setModified(isModified);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "dokumente" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	/* (non-Javadoc)
	 * @see rdg.ui.formulare.UniFromWithSaveLogic#activate()
	 */
	@Override
	public void activate() {

		if (NavigationManager.getSelectedTabItemKey() != null && NavigationManager.getSelectedTabItemKey().equals(getResID()))
		{
			try
			{
				if (UserInformation.getInstance().isProjektverwaltung())
				{
					Action.get("file.new").addActionListener(m_addlistener);
					Action.get("edit.delete").addActionListener(m_deletelistener);
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			m_dateiHandler.activate();
			
			m_allgemeineDatenForm.addSaveHandler();
			refreshByEditMode();
			updateEditToolbarButton();
			
			super.activate();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see rdg.ui.formulare.UniFromWithSaveLogic#deactivate()
	 */
	@Override
	public void deactivate() {
		
		try
		{
			if (UserInformation.getInstance().isProjektverwaltung())
			{
				Action.get("file.new").removeActionListener(m_addlistener);
				Action.get("edit.delete").removeActionListener(m_deletelistener);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		m_dateiHandler.deactivate();
		m_allgemeineDatenForm.removeSaveHandler();
		super.deactivate();
	}

	
	/* (non-Javadoc)
	 * @see framework.cui.controls.base.BaseCompositeControl#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		Object bm;
		
		// Dateinamen ggf. anpassen
		if ( reason == IRefreshable.reasonDataChanged)
		{
			setFileNames();
		}
		
		// refresh und Auswahl wiederherstellen
		bm = m_tableDokumente.getSelectedBookmark();	
		super.refresh(reason, element);
		m_coDokumente.moveTo(bm);
		
		refreshButtons(reason);
	}
	
	
	/**
	 * Buttons in Abhängigkeit von der Auswahl in der Tabelle aktivieren
	 * 
	 * @param reason Grund für refresh
	 */
	private void refreshButtons(int reason){
		
		// wenn kein Tabelleneintrag ausgewählt ist, deaktiviere Buttons
		if (m_tableDokumente.getSelectedBookmark() == null)
		{
			m_uploadControl.refresh(reasonDisabled, null);
			m_openControl.refresh(reasonDisabled, null);
		}
		// bei ausgewähltem Tabelleneintrag
		else
		{
			// upload ist im edit-Modus möglich
			m_uploadControl.refresh(getData().isEditing() ? reasonEnabled : reasonDisabled, null);

			// OpenButton wird aktiviert, wenn eine Datei angegeben ist 
			if (m_coDokumente.getDateiname() != null)
			{
				m_openControl.refresh(reasonEnabled, null);
			}
			else
			{
				m_openControl.refresh(reasonDisabled, null);
			}
		}
	}


	/**
	 * Bezeichnungs-Datensatz hinzufügen
	 *
	 */
	class AddListener extends ActionAdapter
	{
		@Override
		public void activate(Object sender) throws Exception {
			m_coDokumente.createNew(m_coObjekt.getID());
			m_tableDokumente.refresh(reasonDataAdded, m_coDokumente.getBookmark());
			super.activate(sender);
		}
		
		/* (non-Javadoc)
		 * @see framework.business.action.ActionAdapter#getEnabled()
		 */
		@Override
		public boolean getEnabled() {
			return m_coDokumente.isEditing();
		}
	}
	
	
//	/**
//	 * Bezeichnungs-Datensatz löschen
//	 *
//	 */
//	class DeleteListener extends ActionAdapter
//	{
//		@Override
//		/*
//		 * (non-Javadoc)
//		 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
//		 */
//		public void activate(Object sender) throws Exception {
//			m_coDokumente.moveTo(m_tableDokumente.getSelectedBookmark());
//
//			Object bm = m_coDokumente.getBookmark();
//			m_coDokumente.delete();
//			m_tableDokumente.refresh(reasonDataRemoved, bm);
//			super.activate(sender);
//		}
//		
//		/* (non-Javadoc)
//		 * @see framework.business.action.ActionAdapter#getEnabled()
//		 */
//		@Override
//		public boolean getEnabled() {
//			return m_coDokumente.isEditing() && m_tableDokumente.getSelectedBookmark() != null;
//		}
//	}
}
