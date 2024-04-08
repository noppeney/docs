package pze.ui.formulare;

import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoLogging;
import pze.ui.controls.SortedTableControl;


/**
 * Loggingformular
 * 
 * @author Lisiecki
 *
 */
public class LoggingForm extends UniFormWithSaveLogic {
	public static final String RESID="form.logging";

	private SortedTableControl m_tableLogging;
	
	private AbstractCacheObject m_coParent;
	private CoLogging m_coLogging;
	


	/**
	 * Konstruktor
	 * 
	 * @param parent visueller Parent
	 * @param coParent Co der geloggten Daten
	 * @param allgemeineDatenForm Hauptformular der Person
	 * @throws Exception
	 */
	public LoggingForm(Object parent, AbstractCacheObject coParent, UniFormWithSaveLogic allgemeineDatenForm) throws Exception {
		super(parent, RESID, true);

		m_coParent = coParent;
		m_coLogging = m_coParent.getLogging();
		setData(m_coLogging);

		m_tableLogging = new SortedTableControl(findControl("spread.logging"));
		m_tableLogging.setData(m_coLogging);

		// Tabelle darf nicht bearbeitet werden
		refresh(reasonDisabled, null);		
	}

	
	/**
	 * Formular ist nicht zu bearbeiten, daher nie aktivieren
	 * 
	 * @see framework.cui.controls.base.BaseCompositeControl#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		try
		{
			if (reason == reasonDataChanged)
			{
				m_coLogging = m_coParent.getLogging();
				setData(m_coLogging);

				m_tableLogging.setData(m_coLogging);

				// Tabelle darf nicht bearbeitet werden
				refresh(reasonDisabled, null);		
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "logging" + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

}
