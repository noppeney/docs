package pze.ui.formulare.messageboard;

import pze.ui.formulare.AbstractAktionCenterMainForm;

/**
 * Formular für das Messageboard für die Projektverfolgung
 * 
 * @author Lisiecki
 *
 */
public abstract class AbstractFormMessageboardProjektverfolgung extends AbstractFormMessageboard { // TODO löschen, wird nicht benötigt
	
	public static String RESID_MESSAGE = "form.meldungen.projektverfolgung";
	public static String RESID_FREIGABE = "form.freigaben.projektverfolgung";

	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param caption 
	 * @throws Exception
	 */
	public AbstractFormMessageboardProjektverfolgung(Object parent, AbstractAktionCenterMainForm formMessageboard, String caption, int messageGruppeID) throws Exception {
		super(parent, formMessageboard, caption, messageGruppeID, formMessageboard instanceof FormMessageboard ? RESID_MESSAGE : RESID_FREIGABE);
	}

}
