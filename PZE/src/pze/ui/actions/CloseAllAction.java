package pze.ui.actions;

import java.util.Iterator;

import framework.business.session.Session;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import framework.ui.mainframe.CloseAllActionListener;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * ActionListener, der beim Klick auf alle schließen ausgelöst wird. 
 * Prüft die Tabs bevor sie geschlossen werden.
 * 
 * @author Lisiecki
 *
 */
public class CloseAllAction extends CloseAllActionListener{

	
	/**
	 * Schließe alle Tabs, wenn sie nicht modified sind
	 * 
	 * (non-Javadoc)
	 * @see framework.ui.mainframe.CloseAllActionListener#activate(java.lang.Object)
	 */
	public void activate(Object sender){
		String key;
		UniFormWithSaveLogic form;
		ITabFolder tf;
		ITabItem tabItem;
		Iterator<ITabItem> iterTabItems;
		IControl control;

		
		// TabItems holen
		tf = Session.getInstance().getMainFrame().getEditFolder();
		iterTabItems = tf.getTabItems();


		// alle Tabs durchlaufen
		while (iterTabItems.hasNext())
		{
			// Tabinformationen auslesen
			tabItem = iterTabItems.next();
			tf.setSelection(tabItem.getKey());
			control = tabItem.getControl();
			key = tabItem.getKey();

			// wenn es ein UniFormWithSaveLogic ist, prüfe ob es geschlossen werden kann
			form = UniFormWithSaveLogic.getUniFormWithSaveLogic(control);
			if (form != null && !form.canclose())
			{
				continue;
			}

			// Tab entfernen
			if (key != null)
			{
				tf.remove(key);
			}
		}
	}

}
