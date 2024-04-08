package pze.ui.formulare;

import framework.business.interfaces.fields.IField;
import framework.ui.controls.ComboControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.IFocusListener;
import pze.business.objects.AbstractCacheObject;


/**
 * Listener zum Aktualisieren von ComboControls bei ihrer Aktivierung. <br>
 * Es werden alle Einträge der Referenztabelle geladen.
 * 
 * @author Lisiecki
 */
public class ComboRefreshItemsListener implements IFocusListener {

	ComboControl m_comboControl;
	IField m_field;
	AbstractCacheObject m_co;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param comboControl
	 * @param field
	 * @param co
	 */
	public ComboRefreshItemsListener(ComboControl comboControl, IField field, AbstractCacheObject co) {
		m_comboControl = comboControl;
		m_field = field;
		m_co = co;
	}
	
	
	@Override
	public void focusGained(IControl control) {
		
	}

	@Override
	public void focusLost(IControl control) {
		try
		{
			m_co.loadAll();
			UniFormWithSaveLogic.refreshItems(m_comboControl, m_co, m_field);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

}
