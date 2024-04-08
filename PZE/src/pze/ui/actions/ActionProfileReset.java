package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.UserInformation;


/**
 * Klasse zum Zurücksetzen des Profiles, wenn Fenster nicht mehr sichtbar sind
 * 
 * @author Lisiecki
 *
 */
public class ActionProfileReset extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		UserInformation.resetProfileNichtSichtbar();
		Messages.showInfoMessage("Profil zurückgesetzt", "Ihr Profil wurde zurückgesetzt. Alle Fenster sollten jetzt wieder sichtbar sein.");
	}

}
