package pze.ui.formulare.person;

import framework.business.interfaces.FW;
import framework.cui.layout.UniLayout;
import framework.ui.form.UniForm;
import pze.business.objects.personen.CoBuchung;
import pze.ui.controls.SortedTableControl;


/**
 * Dialog zur Anzeige der Freigabe
 * 
 * @author Lisiecki
 */
public class DialogBuchungenBeantragt extends UniForm { // TODO Klassen verallgemeinern mit Dialogfreigabe, nur Tabellen drin, keine Aktionen

	private final static String RESID = "dialog.buchungen.beantragt";

	private SortedTableControl m_table;
	
	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogBuchungenBeantragt() throws Exception {
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
	public static boolean showDialog(int personID) throws Exception {
		DialogBuchungenBeantragt dialog;

		dialog = new DialogBuchungenBeantragt();
		
		// Controls festlegen
		dialog.initControls(personID);

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
	 * @param personID 
	 * @throws Exception 
	 */
	private void initControls(int personID) throws Exception {
		CoBuchung coBuchung;
		
		coBuchung = new CoBuchung();
		coBuchung.loadBeantragtOfa(personID);
		
		m_table = new SortedTableControl(findControl("spread.buchungen.beantragt"));
		
		m_table.setData(coBuchung);
		m_table.refresh(reasonDisabled, null);
	}

}


