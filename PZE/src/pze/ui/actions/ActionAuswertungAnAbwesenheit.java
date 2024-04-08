package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungAnAbwesenheit;

/**
 * Klasse zum Auswerten der An-/Abwesenheit der Mitarbeiter
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungAnAbwesenheit extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungAnAbwesenheit.open(getSession());	
	}

}
