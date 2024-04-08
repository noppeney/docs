package pze.business.navigation.action.archiv;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoader;
import pze.business.navigation.treeloader.TreeLoaderPersonen;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.personen.CoPerson;

/**
 * Klasse zum Archivieren einer Person
 * 
 * @author Lisiecki
 */
public class ActionArchivierenPerson extends ActionAdapter {

	private NavigationManager m_navigationManager;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param navigationManager
	 */
	public ActionArchivierenPerson(NavigationManager navigationManager) {
		m_navigationManager = navigationManager;
	}


	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {
		String name;
		CoPerson coPerson;

		coPerson = (CoPerson) m_navigationManager.getSelectedCoObject();
		name = coPerson.getName();
		
		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Person archivieren", "Möchten Sie " + name + " wirklich archivieren?"))
		{
			return;
		}
		
		// 2. Sicherheitsabfrage
		if (!Messages.showYesNoErrorMessage("Person archivieren", "Kontowerte, Buchungen, Anträge und Monatseinsatzblätter der Person werden archiviert. "
				+ "Weitere Daten, z. B. Verletzermeldungen werden unwiderruflich gelöscht.<br>"
				+ "Möchten Sie fortfahren und " + name + " wirklich archivieren?"))
		{
			return;
		}
		
		
		if (Archivierer.archivierePerson(coPerson.getID()))
		{
			Messages.showInfoMessage(name + " wurde erfolgreich archiviert");
			NavigationManager.getInstance().reloadRootNode(TreeLoaderPersonen.ROOT);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#getEnabled()
	 */
	public boolean getEnabled() {
		TreeLoader activeLoader;
		Object object;
		CoPerson coPerson;
		boolean ausgeschieden;
		
		// prüfen, ob eine ausgeschiedene Person markiert wurde
		ausgeschieden = false;
		object = m_navigationManager.getSelectedCoObject();
		if (object != null)
		{
			try
			{
				coPerson = (CoPerson) object;
				if (coPerson.hasRows() && coPerson.isAusgeschieden())
				{
					ausgeschieden = true;
				}
			} 
			catch (Exception e)
			{
			}
		}

		activeLoader = m_navigationManager.getActiveLoader();

		return (activeLoader instanceof TreeLoaderPersonen)
				&& ausgeschieden
				;
	}

}
