package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.FormBrueckentage;

/**
 * Klasse zum Anzeigen der Brückentage
 * 
 * @author Lisiecki
 *
 */
public class ActionBrueckentage extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormBrueckentage.open(getSession());	
	}



}
