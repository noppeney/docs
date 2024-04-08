package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.person.FormPersonDienstreisenAbrechnung;

/**
 * Klasse zum Auswerten der Dienstreisen-Abrechnung der Mitarbeiter
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungDienstreisenAbrechnung extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormPersonDienstreisenAbrechnung.open(getSession());	
	}

}
