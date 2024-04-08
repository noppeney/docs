package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungBuchhaltungStundenuebersicht;

/**
 * Klasse zum Auswerten Stundenübersicht für die Buchhaltung
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungBuchhaltungStundenuebersicht extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungBuchhaltungStundenuebersicht.open(getSession());	
	}

}
