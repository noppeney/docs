package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungVerletzerliste;

/**
 * Klasse zum Auswerten der Verletzerliste
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungVerletzerliste extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungVerletzerliste.open(getSession());	
	}

}
