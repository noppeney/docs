package pze.ui.formulare.person;

import framework.business.interfaces.FW;
import framework.cui.layout.UniLayout;
import framework.ui.form.UniForm;
import pze.business.objects.personen.CoFreigabe;
import pze.ui.controls.SortedTableControl;


/**
 * Dialog zur Anzeige der Freigabe
 * 
 * @author Lisiecki
 */
public class DialogFreigabe extends UniForm {

	private final static String RESID = "dialog.freigabe";

	private SortedTableControl m_table;
	
	private CoFreigabe m_coFreigabe;

	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogFreigabe() throws Exception {
		super(null, RESID);		
		super.createChilds();
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
	}


	/**
	 * Dialog öffnen
	 * 
	 * @return OK geklickt
	 * @throws Exception
	 */
	public static boolean showDialog(int buchungID) throws Exception {
		DialogFreigabe dialog;

		dialog = new DialogFreigabe();
		
		// Controls festlegen
		dialog.initControls(buchungID);

		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);
		dialog.getDialog().show();

		// wenn nicht OK geklickt wurde beenden
		if (dialog.getDialog().getRetVal() != FW.OK)
		{
			return false;
		}


		return true;
	}


	/**
	 * Controls initialisieren
	 * @param datumBis 
	 * @param datum 
	 * @param personID 
	 * @throws Exception 
	 */
	private void initControls(int buchungID) throws Exception {
		
		m_coFreigabe = new CoFreigabe();
		m_coFreigabe.load(buchungID);
		
		m_table = new SortedTableControl(findControl("spread.freigabe"));
		
		m_table.setData(m_coFreigabe);
		m_table.refresh(reasonDisabled, null);
	}

}


