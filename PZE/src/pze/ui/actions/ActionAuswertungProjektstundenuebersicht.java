package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungProjektstundenuebersicht;

/**
 * Klasse zum Auswerten der Projektstundenübersicht
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungProjektstundenuebersicht extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungProjektstundenuebersicht.open(getSession());	
	}

}
