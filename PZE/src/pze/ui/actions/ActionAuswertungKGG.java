package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungKGG;

/**
 * Klasse zum Auswerten der KGG-Stunden
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungKGG extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungKGG.open(getSession());	
	}

}
