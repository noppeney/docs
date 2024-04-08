package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungKontowerteZeitraum;

/**
 * Klasse zum Auswerten der Kontowerte (Zeitraum)
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungKontowerteZeitraum extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungKontowerteZeitraum.open(getSession());	
	}

}
