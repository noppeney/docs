package pze.ui.formulare.messageboard;

import pze.business.objects.reftables.CoMessageGruppe;

/**
 * Formular für das Messageboard des Sekretariats
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardSekretariat extends AbstractFormMessageboard {
	
	protected static final String CAPTION = "Sekretariat";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @throws Exception
	 */
	public FormMessageboardSekretariat(Object parent, FormMessageboard formMessageboard) throws Exception {
		super(parent, formMessageboard, CAPTION, CoMessageGruppe.ID_SEKRETARIAT, "form.meldungen.dr");
	}



	public static String getKey(int id) {
		return "messageboard.sekretariat." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
