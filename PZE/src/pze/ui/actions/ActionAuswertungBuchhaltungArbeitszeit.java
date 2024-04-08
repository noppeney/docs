package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungArbeitszeit;

/**
 * Klasse zum Auswerten der Arbeitszeit für die Buchhaltung
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungBuchhaltungArbeitszeit extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungArbeitszeit.open(getSession());	
	}

}
