package pze.ui.formulare.messageboard;

import pze.business.objects.reftables.CoMessageGruppe;

/**
 * Formular für das Messageboard Projektcontrolling
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardProjektcontrolling extends AbstractFormMessageboardProjektzuordnung {
	
	protected static final String CAPTION = "Projektcontrolling";
	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @throws Exception
	 */
	public FormMessageboardProjektcontrolling(Object parent, FormMessageboard formMessageboard) throws Exception {
		super(parent, formMessageboard, CAPTION, CoMessageGruppe.ID_VERWALTUNG);
	}

	
	@Override
	protected String getMeldungsTyp() {
		return "IstMessageProjekt";
	}
	

	public static String getKey(int id) {
		return "messageboard.projektcontrolling." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
