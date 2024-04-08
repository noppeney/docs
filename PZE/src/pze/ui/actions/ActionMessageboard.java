package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.messageboard.FormMessageboard;

/**
 * Klasse zum Öffnen des Messageboards
 * 
 * @author Lisiecki
 *
 */
public class ActionMessageboard extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormMessageboard.open(getSession());	
	}

}
