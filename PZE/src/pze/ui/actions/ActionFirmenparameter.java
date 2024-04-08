package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.FormFirmenparameter;

/**
 * Klasse zum Anzeigen der Firmenparameter
 * 
 * @author Lisiecki
 *
 */
public class ActionFirmenparameter extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormFirmenparameter.open(getSession());	
	}



}
