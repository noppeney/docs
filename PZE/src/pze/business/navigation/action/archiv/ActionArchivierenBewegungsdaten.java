package pze.business.navigation.action.archiv;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderArchiv;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.archiv.CoArchivBewegungsdaten;

/**
 * Klasse zum Archivieren der Bewegungsdaten eines Jahres
 * 
 * @author Lisiecki
 */
public class ActionArchivierenBewegungsdaten extends ActionAdapter {

	private NavigationManager m_navigationManager;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param navigationManager
	 */
	public ActionArchivierenBewegungsdaten(NavigationManager navigationManager) {
		m_navigationManager = navigationManager;
	}


	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {
		int jahr;
		CoArchivBewegungsdaten coBewegungsdaten;

		coBewegungsdaten = (CoArchivBewegungsdaten) m_navigationManager.getSelectedCoObject();
		jahr = coBewegungsdaten.getJahr();
		
		// prüfen, ob das Vorjahr archiviert ist
		if (!coBewegungsdaten.isVorjahrArchiviert())
		{
			Messages.showErrorMessage("Archivierung nicht möglich", "Die Bewegungsdaten von " + jahr + " können nicht archiviert werden, "
					+ "da die Bewegungsdaten von " + (jahr-1) + " noch nicht archiviert sind.<br>"
					+ "Bitte archivieren Sie die Bewegungsdaten chronologisch.");
			return;
		}
		
		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Bewegungsdaten archivieren", "Möchten Sie die Bewegungsdaten für " + jahr + " wirklich archivieren?"))
		{
			return;
		}
		
		// 2. Sicherheitsabfrage
		if (!Messages.showYesNoErrorMessage("Bewegungsdaten archivieren", "Kontowerte, Buchungen, Anträge und Monatseinsatzblätter werden archiviert. "
				+ "Weitere Daten, z. B. Verletzermeldungen werden unwiderruflich gelöscht.<br>"
				+ "Möchten Sie fortfahren und " + jahr + " wirklich archivieren?"))
		{
			return;
		}
		
		
		if (Archivierer.archiviereBewegungsdaten(jahr))
		{
			Messages.showInfoMessage("Bewegungsdaten für " + jahr + " wurden erfolgreich archiviert");
			NavigationManager.getInstance().reloadRootNode(TreeLoaderArchiv.ROOT);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	public boolean getEnabled() {
		Object object;
		CoArchivBewegungsdaten coBewegungsdaten;
		
		// prüfen, ob die Daten bereits vollständig archiviert sind
		object = m_navigationManager.getSelectedCoObject();
		if (object != null)
		{
			try
			{
				coBewegungsdaten = (CoArchivBewegungsdaten) object;
				if (coBewegungsdaten.hasRows() && !coBewegungsdaten.isVollstaendigArchiviert())
				{
					return true;
				}
			} 
			catch (Exception e)
			{
			}
		}
		
		return false;
	}


}
