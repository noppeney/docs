package pze.ui.formulare.messageboard;

import pze.business.objects.reftables.CoMessageGruppe;

/**
 * Formular für das Messageboard der Abteilungsleiter
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardAL extends AbstractFormMessageboard {
	
	protected static final String CAPTION = "Abteilungsleiter";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @throws Exception
	 */
	public FormMessageboardAL(Object parent, FormMessageboard formMessageboard) throws Exception {
		super(parent, formMessageboard, CAPTION, CoMessageGruppe.ID_AL);
	}



	public static String getKey(int id) {
		return "messageboard.abteilungsleiter." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
