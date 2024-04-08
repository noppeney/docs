package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAnwesenheit;

/**
 * Klasse zum Auswerten der Anwesenheit
 * 
 * @author Lisiecki
 *
 */
public class ActionAnwesenheit extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAnwesenheit.open(getSession());	
	}

}
