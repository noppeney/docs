package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAmpelliste;

/**
 * Klasse zum Auswerten der Ampelliste
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungAmpelliste extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAmpelliste.open(getSession());	
	}

}
