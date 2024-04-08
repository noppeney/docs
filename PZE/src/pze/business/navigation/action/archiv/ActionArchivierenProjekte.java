package pze.business.navigation.action.archiv;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderArchiv;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.archiv.CoArchivProjekte;

/**
 * Klasse zum Archivieren der Projekte eines Jahres
 * 
 * @author Lisiecki
 */
public class ActionArchivierenProjekte extends ActionAdapter {

	private NavigationManager m_navigationManager;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param navigationManager
	 */
	public ActionArchivierenProjekte(NavigationManager navigationManager) {
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
		if (coProjekte == null)
		{
			jahr = CoArchivProjekte.getFirstJahrForArchivierung();
		}
		else
		{
			jahr = coProjekte.getFirstNichtArchiviert();
			
			if (jahr == 0)
			{
				Messages.showErrorMessage("Archivierung nicht möglich", "Die Archivierung von Projekten ist für kein weiteres Jahr möglich.");
				return;
			}

			// prüfen, ob das Vorjahr archiviert ist
//			if (!coProjekte.isVorjahrArchiviert())
//			{
//				Messages.showErrorMessage("Archivierung nicht möglich", "Die Projekte von " + jahr + " können nicht archiviert werden, "
//						+ "da die Projekte von " + (jahr-1) + " noch nicht archiviert sind.<br>"
//						+ "Bitte archivieren Sie die Projekte chronologisch.");
//				return;
//			}
		}
		
		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Projekte archivieren", "Möchten Sie die Projekte für " + jahr + " wirklich archivieren?"))
		{
			return;
		}
		
		// 2. Sicherheitsabfrage
		if (!Messages.showYesNoErrorMessage("Projekte archivieren", "Projekte und Projektstunden werden archiviert. "
				+ "Möchten Sie fortfahren und " + jahr + " wirklich archivieren?"))
		{
			return;
		}
		
		
		if (Archivierer.archiviereProjekte(jahr))
		{
			Messages.showInfoMessage("Projekte für " + jahr + " wurden erfolgreich archiviert");
			NavigationManager.getInstance().reloadRootNode(TreeLoaderArchiv.ROOT);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	public boolean getEnabled() {
		Object object;
		CoArchivProjekte coProjekte;
		
		// prüfen, ob die Daten bereits vollständig archiviert sind
		object = m_navigationManager.getSelectedCoObject();
		if (object != null)
		{
			try
			{
				coProjekte = (CoArchivProjekte) object;
				if (coProjekte.hasRows() && !coProjekte.isVollstaendigArchiviert())
				{
					return true;
				}
			} 
			catch (Exception e)
			{
			}
		}
		
		return true;
	}


}
