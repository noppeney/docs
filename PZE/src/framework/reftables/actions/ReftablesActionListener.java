/*
 * updates:
 * 07.04.12 ebb, Erstellung
 */
package framework.reftables.actions;

import framework.Application;
import framework.business.action.ActionAdapter;
import framework.reftables.ui.ReftablesNavigationForm;
import framework.ui.interfaces.controls.ITabFolder;

/**
 * Listener für das Editieren von Referenztabellen
 * 
 * @author Gerrit Ebbers
 * @version 1.1
 */
public class ReftablesActionListener extends ActionAdapter {
	private static final String FORM_REFNAVIGATION = "form.refnavigation";
	private String addActionName;
	private String removeActionName;
	
	public ReftablesActionListener(String addActionName, String removeActionName) {
		super();
		this.addActionName = addActionName;
		this.removeActionName = removeActionName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {
		
		//------- ist das Formular bereits aktiv?
		
		ITabFolder tfNavigation = Application.getMainFrame().getNavFolder();
		
		
		//------- Navigationsformular erzeugen
		if (tfNavigation.get(FORM_REFNAVIGATION)== null)
		{
			ReftablesNavigationForm form = new ReftablesNavigationForm(getSession(),tfNavigation, addActionName, removeActionName);
			tfNavigation.add("Referenztabellen",FORM_REFNAVIGATION,  form);
		}
		tfNavigation.setSelection(FORM_REFNAVIGATION);
		
	}

}
