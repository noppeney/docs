package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.ui.formulare.freigabecenter.FormFreigabecenter;

/**
 * Klasse zum Öffnen der Freigaben
 * 
 * @author Lisiecki
 *
 */
public class ActionFreigabecenter extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormFreigabecenter.open(getSession());	
	}

}
