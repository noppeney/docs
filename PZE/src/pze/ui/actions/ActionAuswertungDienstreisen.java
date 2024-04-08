package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungDienstreisen;

/**
 * Klasse zum Auswerten der Dienstreisen der Mitarbeiter
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungDienstreisen extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungDienstreisen.open(getSession());	
	}

}
