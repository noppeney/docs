package pze.ui.formulare.messageboard;

import pze.business.objects.reftables.CoMessageGruppe;

/**
 * Formular für das Messageboard der Verwaltung
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardVerwaltung extends AbstractFormMessageboard {
	
	protected static final String CAPTION = "Personalverwaltung";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @throws Exception
	 */
	public FormMessageboardVerwaltung(Object parent, FormMessageboard formMessageboard) throws Exception {
		super(parent, formMessageboard, CAPTION, CoMessageGruppe.ID_VERWALTUNG);
	}



	public static String getKey(int id) {
		return "messageboard.verwaltung." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
