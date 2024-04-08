package pze.ui.actions;

import framework.business.action.ActionAdapter;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.ui.formulare.FormUebersichtPersonenBenutzergruppe;

/**
 * Klasse zum Anzeigen der Personen mit Benutzergruppen
 * 
 * @author Lisiecki
 *
 */
public class ActionPersonenBenutzergruppen extends ActionAdapter {

	
	@Override
	public void activate(Object sender) throws Exception {
		FormUebersichtPersonenBenutzergruppe.open(getSession(), CoStatusAktivInaktiv.STATUSID_AKTIV);	
	}



}
