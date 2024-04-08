package pze.ui.formulare.messageboard;

import pze.business.objects.reftables.CoMessageGruppe;

/**
 * Formular für das Messageboard mit DR-Informationen
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardDrInfo extends AbstractFormMessageboard {
	
	protected static final String CAPTION = "Dienstreisen";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @throws Exception
	 */
	public FormMessageboardDrInfo(Object parent, FormMessageboard formMessageboard) throws Exception {
		super(parent, formMessageboard, CAPTION, CoMessageGruppe.ID_DR_INFO, "form.meldungen.dr");
	}



	public static String getKey(int id) {
		return "messageboard.drinfo." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
