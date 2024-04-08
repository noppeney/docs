package pze.ui.formulare.messageboard;

import pze.business.objects.reftables.CoMessageGruppe;

/**
 * Formular für das Messageboard der Mitarbeiter
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardMitarbeiter extends AbstractFormMessageboard {
	
	protected static final String CAPTION = "Mitarbeiter";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @throws Exception
	 */
	public FormMessageboardMitarbeiter(Object parent, FormMessageboard formMessageboard) throws Exception {
		super(parent, formMessageboard, CAPTION, CoMessageGruppe.ID_MITARBEITER);
	}



	public static String getKey(int id) {
		return "messageboard.mitarbeiter." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
