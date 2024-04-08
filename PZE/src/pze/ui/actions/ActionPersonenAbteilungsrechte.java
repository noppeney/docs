package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.ui.formulare.FormUebersichtPersonenAbteilungsrechte;

/**
 * Klasse zum Anzeigen der Personen mit Abteilungsrechten
 * 
 * @author Lisiecki
 *
 */
public class ActionPersonenAbteilungsrechte extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormUebersichtPersonenAbteilungsrechte.open(getSession(), CoStatusAktivInaktiv.STATUSID_AKTIV);	
	}

}
