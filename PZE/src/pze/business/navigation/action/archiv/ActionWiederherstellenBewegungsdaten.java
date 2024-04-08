package pze.business.navigation.action.archiv;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderArchiv;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.archiv.CoArchivBewegungsdaten;

/**
 * Klasse zum Wiederherstellen der Bewegungsdaten eines Jahres
 * 
 * @author Lisiecki
 */
public class ActionWiederherstellenBewegungsdaten extends ActionAdapter {

	private NavigationManager m_navigationManager;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param navigationManager
	 */
	public ActionWiederherstellenBewegungsdaten(NavigationManager navigationManager) {
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
		
		// prüfen, ob das nächste Jahr wiederhergestellt ist
		if (!coBewegungsdaten.isVollstaendigArchiviert())
		{
			Messages.showErrorMessage("Wiederherstellung nicht möglich", "Die Bewegungsdaten von " + jahr + " können nicht wiederhergestellt werden, "
					+ "da sie nicht vollständig archiviert wurden.<br>"
					+ "Nur vollständig archivierte Bewegungsdaten können wiederhergestellt werden.");
			return;
		}
		else if (coBewegungsdaten.isNextJahrArchiviert())
		{
			Messages.showErrorMessage("Wiederherstellung nicht möglich", "Die Bewegungsdaten von " + jahr + " können nicht wiederhergestellt werden, "
					+ "da die Bewegungsdaten von " + (jahr+1) + " noch nicht wiederherstellen sind.<br>"
					+ "Bitte führen Sie die Wiederherstellung der Bewegungsdaten chronologisch durch, angefangen beim letzten Jahr.");
			return;
		}

		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Bewegungsdaten wiederherstellen", "Möchten Sie die Bewegungsdaten für " + jahr + " wirklich wiederherstellen?"))
		{
			return;
		}
		
		// 2. Sicherheitsabfrage
		if (!Messages.showYesNoErrorMessage("Bewegungsdaten wiederherstellen", "Kontowerte, Buchungen, Anträge und Monatseinsatzblätter"
				+ " von nicht archivierten Personen werden wiederhergestellt. "
				+ "Weitere Daten, z. B. Verletzermeldungen wurden bei der Archivierung unwiderruflich gelöscht.<br>"
				+ "Möchten Sie fortfahren und die Bewegungsdaten für " + jahr + " wirklich wiederherstellen?"))
		{
			return;
		}
		
		
		if (Archivierer.restoreBewegungsdaten(jahr))
		{
			Messages.showInfoMessage("Bewegungsdaten für " + jahr + " wurden erfolgreich wiederhergestellt.");
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
