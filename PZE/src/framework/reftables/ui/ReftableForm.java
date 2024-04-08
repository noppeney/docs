/*
 * updates:
 * 08.04.12 ebb, Erstellung
 */
package framework.reftables.ui;

import java.util.Iterator;

import framework.business.action.Action;
import framework.business.action.ActionAdapter;
import framework.business.fields.HeaderDescription;
import framework.business.interfaces.actions.IActionListener;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.business.interfaces.refresh.IRefreshable;
import framework.business.resources.ResourceMapper;
import framework.cui.layout.UniLayout;
import framework.reftables.actions.EditListener;
import framework.reftables.actions.SaveListener;
import framework.ui.form.UniForm;
import pze.business.objects.reftables.ReftableCacheObject;
import pze.ui.controls.SortedTableControl;


/**
 * Formular für die Bearbeitung der Daten einer Referenztabelle
 * 
 * @author G. Ebbers / LP-IT
 * @version 1.1
 */
public class ReftableForm extends UniForm {

	/*
	 * Member
	 */
	private final static String RESID = "form.admin.reftable";
	private SortedTableControl table = null;
	private IActionListener editListener = null;
	private IActionListener saveListener = null;
	private String addActionName;
	private String removeActionName;
	
	/**
	 * Konstruktion
	 * @param parent
	 */
	public ReftableForm(Object parent, String addActionName, String removeActionName) throws Exception {
		
		//------- Konstruktion
		
		super(parent, ReftableForm.RESID);
		this.addActionName = addActionName;
		this.removeActionName = removeActionName;
		
		super.createChilds();
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
	
		//------- Tabelle
		
		this.table = new SortedTableControl(findControl("spread.admin.reftable"));
		
		
		//------- Listener
		
		
	}

	/*
	 * (non-Javadoc)
	 * @see framework.cui.controls.base.BaseCompositeControl#setData(framework.business.interfaces.data.IBusinessObject)
	 */
	@Override
	public void setData(IBusinessObject data) throws Exception {
		super.setData(data);
		this.table.setData(data);
		Iterator<IField> felder = data.getFields();
		felder.next();
		HeaderDescription hdesc = new HeaderDescription(felder);
		this.table.setHeaderDescription(hdesc);
		String caption = ResourceMapper.getInstance().getText(data.getResID());
		super.setCaption(caption);
		this.editListener = new EditListener(data,this);
		this.saveListener = new SaveListener(data,this);

	}

	/*
	 * (non-Javadoc)
	 * @see framework.wui.form.UniForm#activate()
	 */
	@Override
	public void activate() {
		
		Action.get("file.edit").setActionListener(this.editListener);
		Action.get("file.save").setActionListener(this.saveListener);
		
		Action.get(addActionName).setActionListener( new AddListener());
		Action.get(removeActionName).setActionListener( new RemoveListener());
		
		if(super.getData() != null)
			super.getSession().getMainFrame().getToolbar().updateEditMode(super.getData().isEditing());
		
	}

	/*
	 * (non-Javadoc)
	 * @see framework.wui.form.UniForm#deactivate()
	 */
	@Override
	public void deactivate() {
		
		Action.get("file.edit").removeActionListeners();
		Action.get("file.save").removeActionListeners();
		
		Action.get(addActionName).removeActionListeners();
		Action.get(removeActionName).removeActionListeners();
		
		super.getSession().getMainFrame().getToolbar().updateEditMode(false);
		
	}
	
	/**
	 * Hinzufügen eines DS
	 */
	private void onAddRecord() throws Exception {
		
		ReftableCacheObject data = (ReftableCacheObject) this.table.getData();
		data.createNew();
		Object bookmark = data.getBookmark();
		table.refresh(reasonDataAdded, bookmark);
		this.table.setSelectedBookmarks(new Object[] {bookmark});
	}
	
	/**
	 * Entfernen eines DS
	 */
	private void onRemoveRecord() throws Exception {
		
		IBusinessObject data = this.table.getData();
		Object bookmark = this.table.getSelectedBookmark();
		
		if(bookmark != null && data.moveTo(bookmark)) {
			data.delete();
			this.table.refresh(IRefreshable.reasonDataRemoved, bookmark);
			this.table.setSelectedBookmark(null);
		}
		
	}
	
	/**
	 * Listener zum Hinzufügen von Datensätzen
	 */
	class AddListener extends ActionAdapter {

		@Override
		public void activate(Object sender) throws Exception {
			ReftableForm.this.onAddRecord();
		}

		@Override
		public boolean getEnabled() {
			return ReftableForm.this.table.getData().isEditing();
		}

	}
	
	/**
	 * Listener zum Entfernen von Datensätzen
	 */
	class RemoveListener extends ActionAdapter {
		
		@Override
		public void activate(Object sender) throws Exception {
			ReftableForm.this.onRemoveRecord();
		}
		
		@Override
		public boolean getEnabled() {
			return ReftableForm.this.table.getData().isEditing();
		}
		
	}
	
}
