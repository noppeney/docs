package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungBuchhaltungDienstreisen;

/**
 * Klasse zum Auswerten der Dienstreisen der Mitarbeiter für die Buchhaltung
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungBuchhaltungDienstreisen extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungBuchhaltungDienstreisen.open(getSession());	
	}

}
