package pze.business.navigation.action.archiv;

import framework.business.action.ActionAdapter;
import pze.business.Messages;
import pze.business.navigation.NavigationManager;
import pze.business.navigation.treeloader.TreeLoaderArchiv;
import pze.business.objects.archiv.Archivierer;
import pze.business.objects.archiv.CoArchivPerson;

/**
 * Klasse zum Löschen einer Person
 * 
 * @author Lisiecki
 */
public class ActionLoeschenPerson extends ActionAdapter {

	private NavigationManager m_navigationManager;
	
	
	
	/**
	 * Konstruktor
	 * 
	 * @param navigationManager
	 */
	public ActionLoeschenPerson(NavigationManager navigationManager) {
		m_navigationManager = navigationManager;
	}


	/*
	 * (non-Javadoc)
	 * @see framework.business.action.ActionAdapter#activate(java.lang.Object)
	 */
	@Override
	public void activate(Object sender) throws Exception {
		String name;
		CoArchivPerson coArchivPerson;

		coArchivPerson = (CoArchivPerson) m_navigationManager.getSelectedCoObject();
		name = coArchivPerson.getName();
		
		// Sicherheitsabfrage
		if (!Messages.showYesNoMessage("Person löschen", "Möchten Sie " + name + " wirklich löschen?"))
		{
			return;
		}
		
		// 2. Sicherheitsabfrage
		if (!Messages.showYesNoErrorMessage("Person löschen", "Kontowerte, Buchungen, Anträge und Monatseinsatzblätter der Person werden unwiderruflich gelöscht.<br>"
				+ "Möchten Sie fortfahren und " + name + " wirklich löschen?"))
		{
			return;
		}
		
		
		if (Archivierer.deletePerson(coArchivPerson.getID()))
		{
			Messages.showInfoMessage(name + " wurde erfolgreich gelöscht");
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
