package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.uebersicht.FormUebersichtBuchungen;

/**
 * Klasse zum Anzeigen der aktuellen Buchungen
 * 
 * @author Lisiecki
 *
 */
public class ActionAktuelleBuchungen extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormUebersichtBuchungen.open(getSession());	
	}



}
