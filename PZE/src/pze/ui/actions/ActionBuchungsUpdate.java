package pze.ui.actions;

import framework.business.action.ActionAdapter;
import framework.business.session.Session;
import pze.ui.formulare.FormBuchungsUpdate;

/**
 * Klasse zum Starten des Buchungsupdate (laden der Terminal-Buchungen)
 * 
 * @author Lisiecki
 *
 */
public class ActionBuchungsUpdate extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormBuchungsUpdate.open(Session.getInstance());
	}



}
