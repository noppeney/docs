package pze.business.navigation.action.archiv;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderArchiv;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.archiv.CoArchivBewegungsdaten;

/**
 * Klasse zum Löschen der Bewegungsdaten eines Jahres
 * 
 * @author Lisiecki
 */
public class ActionLoeschenBewegungsdaten extends ActionAdapter {

	private NavigationManager m_navigationManager;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param navigationManager
	 */
	public ActionLoeschenBewegungsdaten(NavigationManager navigationManager) {
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
		
		// prüfen, ob das Jahr gelöscht werden kann
		if (!coBewegungsdaten.isVollstaendigArchiviert())
		{
			Messages.showErrorMessage("Löschen nicht möglich", "Die Bewegungsdaten von " + jahr + " können nicht gelöscht werden, "
					+ "da sie nicht vollständig archiviert wurden.<br>"
					+ "Nur vollständig archivierte Bewegungsdaten können gelöscht werden.");
			return;
		}
		else if (coBewegungsdaten.getCurrentRowIndex() != 0)
		{
			Messages.showErrorMessage("Löschen nicht möglich", "Die Bewegungsdaten von " + jahr + " können nicht gelöscht werden, "
					+ "da die Bewegungsdaten von " + (jahr-1) + " noch nicht gelöscht wurden.<br>"
					+ "Bitte löschen Sie die Bewegungsdaten chronologisch.");
			return;
		}

		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Bewegungsdaten löschen", "Möchten Sie die Bewegungsdaten für " + jahr + " wirklich löschen?"))
		{
			return;
		}
		
		// 2. Sicherheitsabfrage
		if (!Messages.showYesNoErrorMessage("Bewegungsdaten löschen", "Kontowerte, Buchungen, Anträge und Monatseinsatzblätter werden gelöscht. "
				+ "Weitere Daten, z. B. Verletzermeldungen wurden bei der Archivierung unwiderruflich gelöscht.<br>"
				+ "Möchten Sie fortfahren und die Bewegungsdaten für " + jahr + " wirklich löschen?"))
		{
			return;
		}
		
		
		if (Archivierer.deleteBewegungsdaten(jahr))
		{
			Messages.showInfoMessage("Bewegungsdaten für " + jahr + " wurden erfolgreich gelöscht.");
			NavigationManager.getInstance().reloadRootNode(TreeLoaderArchiv.ROOT);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	public boolean getEnabled() {
		return true;
	}

}
