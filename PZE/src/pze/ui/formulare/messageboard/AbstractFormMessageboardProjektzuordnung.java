package pze.ui.formulare.messageboard;

/**
 * Formular für das Messageboard für Projekte
 * 
 * @author Lisiecki
 *
 */
public abstract class AbstractFormMessageboardProjektzuordnung extends AbstractFormMessageboard {// TODO löschen, wird nicht benötigt
	
	public static String RESID = "form.meldungen.projektzuordnung";

	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param caption 
	 * @throws Exception
	 */
	public AbstractFormMessageboardProjektzuordnung(Object parent, FormMessageboard formMessageboard, String caption, int messageGruppeID) throws Exception {
		super(parent, formMessageboard, caption, messageGruppeID, RESID);
	}

}
