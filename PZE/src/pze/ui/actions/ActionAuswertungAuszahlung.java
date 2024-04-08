package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungAuszahlung;

/**
 * Klasse zum Auswerten der Auszahlungen
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungAuszahlung extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungAuszahlung.open(getSession());	
	}

}
