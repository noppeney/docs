package pze.business.navigation.action.archiv;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderArchiv;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.archiv.CoArchivProjekte;

/**
 * Klasse zum Wiederherstellen der Projekte eines Jahres
 * 
 * @author Lisiecki
 */
public class ActionWiederherstellenProjekte extends ActionAdapter {

	private NavigationManager m_navigationManager;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param navigationManager
	 */
	public ActionWiederherstellenProjekte(NavigationManager navigationManager) {
		m_navigationManager = navigationManager;
	}


	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {
		int jahr;
		CoArchivProjekte coProjekte;

		coProjekte = (CoArchivProjekte) m_navigationManager.getSelectedCoObject();
		jahr = coProjekte.getJahr();
		
		// prüfen, ob das nächste Jahr wiederhergestellt ist
		if (!coProjekte.isVollstaendigArchiviert())
		{
			Messages.showErrorMessage("Wiederherstellung nicht möglich", "Die Projekte von " + jahr + " können nicht wiederhergestellt werden, "
					+ "da sie nicht vollständig archiviert wurden.<br>"
					+ "Nur vollständig archivierte Projekte können wiederhergestellt werden.");
			return;
		}
		else if (coProjekte.isNextJahrArchiviert())
		{
			Messages.showErrorMessage("Wiederherstellung nicht möglich", "Die Projekte von " + jahr + " können nicht wiederhergestellt werden, "
					+ "da die Projekte von " + (jahr+1) + " noch nicht wiederherstellen sind.<br>"
					+ "Bitte führen Sie die Wiederherstellung der Projekte chronologisch durch, angefangen beim letzten Jahr.");
			return;
		}

		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Projekte wiederherstellen", "Möchten Sie die Projekte für " + jahr + " wirklich wiederherstellen?"))
		{
			return;
		}
		
		// 2. Sicherheitsabfrage
		if (!Messages.showYesNoErrorMessage("Projekte wiederherstellen", "Projekte und Projektdaten werden wiederhergestellt. "
				+ "Möchten Sie fortfahren und die Projekte für " + jahr + " wirklich wiederherstellen?"))
		{
			return;
		}
		
		
		if (Archivierer.restoreProjekte(jahr))
		{
			Messages.showInfoMessage("Projekte für " + jahr + " wurden erfolgreich wiederhergestellt.");
			NavigationManager.getInstance().reloadRootNode(TreeLoaderArchiv.ROOT);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	public boolean getEnabled() {
		return m_navigationManager.getSelectedCoObject() != null;
	}

}
