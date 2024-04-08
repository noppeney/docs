package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungUrlaubsbuchungen;

/**
 * Klasse zum Auswerten des Urlaubs
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungUrlaub extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungUrlaubsbuchungen.open(getSession());	
	}

}
