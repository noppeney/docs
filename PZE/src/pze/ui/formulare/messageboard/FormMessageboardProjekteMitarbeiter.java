package pze.ui.formulare.messageboard;

import pze.business.objects.reftables.CoMessageGruppe;

/**
 * Formular für das Messageboard der Projektzuordnung für Mitarbeiter
 * 
 * @author Lisiecki
 *
 */
public class FormMessageboardProjekteMitarbeiter extends AbstractFormMessageboardProjektzuordnung {
	
	protected static final String CAPTION = "Projekte";
//	protected static final String CAPTION = FormMessageboard.MESSAGEBOARD_PROJEKTE_NEU ? "Projektzuordnung" : "Projekte";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @param formMessageboard 
	 * @throws Exception
	 */
	public FormMessageboardProjekteMitarbeiter(Object parent, FormMessageboard formMessageboard) throws Exception {
		super(parent, formMessageboard, CAPTION, CoMessageGruppe.ID_MITARBEITER);
	}

	
	@Override
	protected String getMeldungsTyp() {
		return "IstMessageProjekt";
	}
	

	public static String getKey(int id) {
		return "messageboard.mitarbeiter.projektzuordnung." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	


}
