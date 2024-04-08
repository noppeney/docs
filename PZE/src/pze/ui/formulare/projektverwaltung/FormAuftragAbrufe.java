package pze.ui.formulare.projektverwaltung;

import framework.ui.interfaces.controls.IControl;
import pze.business.Profile;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.UniFormWithSaveLogic;


/**
 * Formular der Abrufe zu einem Auftrag
 * 
 * @author Lisiecki
 *
 */
public class FormAuftragAbrufe extends UniFormWithSaveLogic {
	public static final String RESID = "form.auftrag.abruf";

	private SortedTableControl m_table;
	
	private CoAbruf m_coAbruf;
			
	

	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param m_coAbruf Co der geloggten Daten
	 * @param formAuftrag Hauptformular des Auftrags
	 * @throws Exception
	 */
	public FormAuftragAbrufe(Object parent, CoAbruf coAbruf, UniFormWithSaveLogic formAuftrag) throws Exception {
		super(parent, RESID, true);
		
		m_coAbruf = coAbruf;
		m_coAbruf.addProjektmerkmalAbruf();
		m_coAbruf.setModified(false);
		
		setData(m_coAbruf);

		m_table = new SortedTableControl(findControl("spread.auftrag.abruf")){

			@Override
			public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception {
				FormAbruf.open(getSession(), m_coAbruf.getID());
			}
		};
		
		m_table.setData(m_coAbruf);
		
//		m_table.setSelectionListener(new ISelectionListener() {
//
//			/**
//			 * Statusanzeige für die aktuelle Unterweisung anpassen
//			 * 
//			 * @see framework.ui.interfaces.selection.ISelectionListener#selected(framework.ui.interfaces.controls.IControl, java.lang.Object)
//			 */
//			@Override
//			public void selected(IControl arg0, Object arg1) {
//				// allgemeine selected-Funktion zum Sortieren aufrufen
//				m_table.tableSelected(arg0, arg1);
//			}
//
//
//			@Override
//			public void defaultSelected(IControl arg0, Object arg1) {
//
//				// Formular mit dem Zeitmodell öffnen
//				try
//				{
//					if (arg1 != null)
//					{
//						FormAbruf.open(getSession(), null, m_coAbruf.getID());
//					}
//				} catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});

		refresh(reasonDisabled, null);
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "auftragabruf" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	
	
	/**
	 * Das Formular darf nicht bearbeitet werden
	 * 
	 * (non-Javadoc)
	 * @see pze.ui.formulare.UniFormWithSaveLogic#mayEdit()
	 */
	@Override
	public boolean mayEdit() {
		return false;
	}
	

	@Override
	public void activate() {

		addExcelExportListener(m_coAbruf, m_table, "Uebersicht_Abrufe", Profile.KEY_AUSWERTUNG_PROJEKTE);
		super.activate();
	}
	

}
