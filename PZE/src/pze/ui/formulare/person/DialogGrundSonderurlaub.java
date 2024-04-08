package pze.ui.formulare.person;

import framework.business.interfaces.FW;
import framework.business.interfaces.fields.IField;
import framework.cui.layout.UniLayout;
import framework.ui.controls.ComboControl;
import framework.ui.form.UniForm;
import pze.business.Format;
import pze.business.Messages;


/**
 * Dialog zum Eintragen des Grunds für Sonderurlaub
 * 
 * @author Lisiecki
 */
public class DialogGrundSonderurlaub extends UniForm {

	private final static String RESID = "dialog.grundsonderurlaub";

	private static ComboControl m_comboGrund;
	
	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogGrundSonderurlaub() throws Exception {
		super(null, RESID);		
		super.createChilds();
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
		// Controls festlegen
		initControls();
	}


	/**
	 * Dialog öffnen
	 * 
	 * @return OK geklickt
	 * @throws Exception
	 */
	public static boolean showDialog() throws Exception {
		DialogGrundSonderurlaub dialog;

		dialog = new DialogGrundSonderurlaub();
		
		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);
		dialog.getDialog().show();

		// wenn nicht OK geklickt wurde beenden
		if (dialog.getDialog().getRetVal() != FW.OK)
		{
			return false;
		}
		
		// Abfragen, ob ein Grund angegeben wurde
		if (getGrundID() == 0)
		{
			Messages.showErrorMessage("Der Antrag kann nicht ohne Grund erstellt werden.");
			return showDialog();
		}

		return true;
	}


	/**
	 * Controls initialisieren
	 * @throws Exception 
	 */
	private void initControls() throws Exception {
		m_comboGrund = (ComboControl) getControl(RESID + ".grund");
	}



	private static IField getFieldGrund() {
		return m_comboGrund.getField();
	}


	public static int getGrundID() {
		return Format.getIntValue(getFieldGrund().getValue());
	}


}


