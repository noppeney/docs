package pze.business.datentransfer;

import framework.business.action.ActionAdapter;
import pze.business.Profile;
import pze.business.objects.AbstractCacheObject;
import pze.ui.controls.SortedTableControl;


/**
 * ExportListener für Tabellen, zum Export nach Excel.
 * 
 * @author Lisiecki
 *
 */
public class TableExportExcelListener extends ActionAdapter{
	
	private AbstractCacheObject m_co;
	private SortedTableControl m_table;
	
	private String m_dateiname;
	private String m_profilePathKey;
	
	
	/**
	 * Konstruktor
	 * 
	 * @param co Cacheobjekt der Tabelle
	 * @param table Tabelle
	 */
	public TableExportExcelListener(AbstractCacheObject co, SortedTableControl table){
		super();
		
		m_co = co;
		m_table = table;
	}
	
	
	/**
	 * Konstruktor
	 * 
	 * @param co Cacheobjekt der Tabelle
	 * @param table Tabelle
	 * @param dateiname
	 * @param profilePathKey 
	 */
	public TableExportExcelListener(AbstractCacheObject co, SortedTableControl table, String dateiname, String profilePathKey) {
		this(co, table);
		
		m_dateiname = dateiname;
		m_profilePathKey = profilePathKey;
	}


	/**
	 * Export starten.
	 * 
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {
		String filename;
		Excelexporter exporter;
		
		filename = FileHandler.showExcelFileDialog(Profile.getProfileItem(m_profilePathKey) + "\\" + getDefaultFilename());
		if (filename == null)
		{
			return;
		}
		
		exporter = new Excelexporter(false);
		exporter.setFilename(filename);
		exporter.setCo(m_co, m_table);
		exporter.export();

		// Pfad speichern
		Profile.setProfileItem(m_profilePathKey, filename.substring(0, filename.lastIndexOf("\\")));

		super.activate(sender);
	}


	/**
	 * Im Profile gespeicherter Pfad
	 * 
	 * @return
	 */
	protected String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PROJEKTE;
	}
	

	/**
	 * Default-Filename beim Öffnen des Dateiauswahlfensters
	 * 
	 * @return
	 * @throws Exception 
	 */
	protected String getDefaultFilename() throws Exception {
		
		if (m_dateiname == null)
		{
			m_dateiname = "Auswertung";
		}
		
		if (!m_dateiname.endsWith(".xls"))
		{
			m_dateiname += ".xls";
		}
		
		return m_dateiname;
	}
	

	/* (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	@Override
	public boolean getEnabled() {
		return true;
	}

}
