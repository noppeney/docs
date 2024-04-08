package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungUrlaubsplanung;

/**
 * Klasse zum Auswerten der Urlaubsplanung der Mitarbeiter
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungUrlaubsplanung extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungUrlaubsplanung.open(getSession());	
	}

}
