package pze.business.navigation.action.archiv;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderArchiv;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.archiv.CoArchivProjekte;

/**
 * Klasse zum Löschen der Projekte eines Jahres
 * 
 * @author Lisiecki
 */
public class ActionLoeschenProjekte extends ActionAdapter {

	private NavigationManager m_navigationManager;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param navigationManager
	 */
	public ActionLoeschenProjekte(NavigationManager navigationManager) {
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
		
		// prüfen, ob das Jahr gelöscht werden kann
		if (!coProjekte.isVollstaendigArchiviert())
		{
			Messages.showErrorMessage("Löschen nicht möglich", "Die Projekte von " + jahr + " können nicht gelöscht werden, "
					+ "da sie nicht vollständig archiviert wurden.<br>"
					+ "Nur vollständig archivierte Projekte können gelöscht werden.");
			return;
		}
		else if (coProjekte.getCurrentRowIndex() != 0)
		{
			Messages.showErrorMessage("Löschen nicht möglich", "Die Projekte von " + jahr + " können nicht gelöscht werden, "
					+ "da die Projekte von " + (jahr-1) + " noch nicht gelöscht wurden.<br>"
					+ "Bitte löschen Sie die Projekte chronologisch.");
			return;
		}

		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Projekte löschen", "Möchten Sie die Projekte für " + jahr + " wirklich löschen?"))
		{
			return;
		}
		
		// 2. Sicherheitsabfrage
		if (!Messages.showYesNoErrorMessage("Projekte löschen", "Projekte und Projektstunden werden gelöscht. "
				+ "Möchten Sie fortfahren und die Projekte für " + jahr + " wirklich löschen?"))
		{
			return;
		}
		
		
		if (Archivierer.deleteProjekte(jahr))
		{
			Messages.showInfoMessage("Projekte für " + jahr + " wurden erfolgreich gelöscht.");
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
