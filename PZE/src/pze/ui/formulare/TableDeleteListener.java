package pze.ui.formulare;

import framework.business.action.ActionAdapter;
import framework.business.interfaces.refresh.IRefreshable;
import framework.ui.interfaces.controls.ITableControl;
import pze.business.objects.AbstractCacheObject;


/**
 * DeleteListener für Tabellen, der die Einträge nicht löscht, sondern nur als gelöscht markiert.
 * 
 * @author Lisiecki
 *
 */
public class TableDeleteListener extends ActionAdapter{
	
	private AbstractCacheObject m_co;
	private ITableControl m_table;
	private UniFormWithSaveLogic m_form;
	
	
	/**
	 * Konstruktor
	 * 
	 * @param form Formular
	 * @param co Cacheobjekt der Tabelle
	 * @param table Tabelle
	 */
	public TableDeleteListener(UniFormWithSaveLogic form, AbstractCacheObject co, ITableControl table){
		super();
		m_form = form;
		m_co = co;
		m_table = table;
	}
	
	
	/**
	 * Markiert den aktuellen Datensatz als gelöscht. Er wird nicht wirklich gelöscht.
	 * 
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {
		m_co.moveTo(m_table.getSelectedBookmark());
		
		
//		if (m_co.isNew())
		{
			m_co.delete();
		}
//		else
//		{
//			//		Object bm = m_co.getBookmark();
//			m_co.setStatusGeloescht();
//		}
		
//		m_table.refresh(IRefreshable.reasonDataRemoved, bm);
		m_form.refresh(IRefreshable.reasonDataChanged, null);
		super.activate(sender);
	}

	
	/* (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	@Override
	public boolean getEnabled() {
		return m_co.isEditing() && m_table.getSelectedBookmark() != null;
	}

}
