package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.auswertung.FormAuswertungMonatseinsatzblatt;

/**
 * Klasse zum Auswerten der Monatseinsatzblatt
 * 
 * @author Lisiecki
 *
 */
public class ActionAuswertungMonatseinsatzblatt extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormAuswertungMonatseinsatzblatt.open(getSession());	
	}

}
