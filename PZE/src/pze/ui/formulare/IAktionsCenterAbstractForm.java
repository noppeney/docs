package pze.ui.formulare;


/**
 * Interface für Reiter im Messageboard und Freigabecenter
 * 
 * @author lisiecki
 */
public interface IAktionsCenterAbstractForm {

	
	/**
	 * Tabellen neu laden und ggf. TabItem anpassen
	 * 
	 * @return 
	 * @throws Exception
	 */
	public void refreshTableData() throws Exception;


	/**
	 * Caption und Key des Formulars anpassen an den Inhalt
	 * @return 
	 */
	public void refreshCaption();

	/**
	 * Daten für alle Tabellen laden und zuordnen
	 * 
	 * @throws Exception
	 */
	public void reloadTableData() throws Exception;


	/**
	 * Caption und Key des Formulars anpassen an den Inhalt
	 * @return 
	 */
	public int getAnzAntraegeAktiviert();
	
	
	/**
	 * Standard-Bezeichnung ohne Anzahl Anträge/Messages
	 * @return
	 */
	public String getDefaultCaption();
}
