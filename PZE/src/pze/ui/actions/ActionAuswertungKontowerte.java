package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungKontowerte;

/**
 * Klasse zum Auswerten der Kontowerte
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungKontowerte extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungKontowerte.open(getSession());	
	}

}
