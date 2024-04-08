package pze.business;

import framework.Application;
import framework.business.session.Session;
import framework.business.useradmin.model.GroupModel;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.personen.CoPosition;


/**
 * Klasse zur Speicherung von Benutzerinformationen<br>
 * Z. B. die Gruppenzugehörigkeit kann geprüft werden.
 * 
 * @author Lisiecki
 */
public class UserInformation {
	
	private static final String GRUPPE_ENTWICKLER = "Entwickler";
	private static final String GRUPPE_ADMINISTRATOREN = "Administratoren";
	
	private static final String GRUPPE_PERSONALVERWALTUNG = "Personalverwaltung";
	private static final String GRUPPE_PERSONALANSICHT = "Personalansicht";
	
	private static final String GRUPPE_SEKRETARIAT = "Sekretariat";
	private static final String GRUPPE_TL_AL = "TL/AL";
	
	private static final String GRUPPE_MITARBEITER = "Mitarbeiter";
	
	private static final String GRUPPE_PROJEKTVERWALTUNG = "Projektverwaltung";
	private static final String GRUPPE_PROJEKTAUSWERTUNG = "Projektauswertung";
	private static final String GRUPPE_PROJEKTCONTROLLING = "Projektcontrolling";

	private static final String GRUPPE_KGG = "KGG";
	
	private static final String GRUPPE_DR_INFO = "DR-Info";

	private static UserInformation m_instance = null;
	
	private GroupModel m_coGroups;
	
	

	/**
	 * Konstruktor
	 * @throws Exception 
	 */
	private UserInformation(){
		try 
		{
			m_coGroups = new GroupModel();
			m_coGroups.load();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			Messages.showErrorMessage("Fehler beim Laden der Benutzergruppen.");
		}
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static UserInformation getInstance() {
		if (UserInformation.m_instance == null)
		{
			UserInformation.m_instance = new UserInformation();
		}
		
		return UserInformation.m_instance;
	}

	
	/**
	 * UserID des aktuellen Users
	 * 
	 * @return
	 */
	public static int getUserID() throws Exception {
		return Session.getInstance().getUserInfo().getUserID();
	}

	
	/**
	 * LoginName des aktuellen Users
	 * 
	 * @return
	 */
	public static String getLoginName() throws Exception {
		return Session.getInstance().getUserInfo().getLoginName();
	}

	
	/**
	 * Prüft ob der Benutzer Entwickler ist.
	 * 
	 * @return ja/nein
	 */
	public boolean isEntwickler() {
		return isUserInGroup(GRUPPE_ENTWICKLER);
	}


	/**
	 * Prüft ob der Benutzer Admin ist.<br>
	 * Durch die hierarchische Struktur sind Entwickler auch Administratoren.
	 * 
	 * @return ja/nein
	 */
	public boolean isAdmin() {
		return isUserInGroup(GRUPPE_ADMINISTRATOREN) || isEntwickler();
	}


	/**
	 * Prüft ob der Benutzer in der Gruppe Personalverwaltung ist.<br>
	 * Durch die hierarchische Struktur sind Administratoren auch in der Personalverwaltung.
	 * 
	 * @return ja/nein
	 */
	public boolean isPersonalverwaltung() {
		return isUserInGroup(GRUPPE_PERSONALVERWALTUNG) || isAdmin();
	}


	/**
	 * Prüft ob der Benutzer in der Gruppe Personalverwaltung ist.<br>
	 * Die hierarchische Struktur wird nicht berücksichtigt (keine Administratoren).
	 * 
	 * @return ja/nein
	 */
	public boolean isPersonalverwaltungOhneAdmin() {
		return isUserInGroup(GRUPPE_PERSONALVERWALTUNG);
	}


	/**
	 * Prüft ob der Benutzer in der Gruppe Buchungsverwaltung ist.<br>
	 * Durch die hierarchische Struktur sind Administratoren auch in der Buchungsverwaltung.
	 * 
	 * @return ja/nein
	 */
//	public boolean isZeitbuchung() {
//		return isUserInGroup(GRUPPE_ZEITBUCHUNG) || isAdmin();
//	}


	/**
	 * Prüft ob der Benutzer in der Gruppe Personalansicht ist.<br>
	 * Durch die hierarchische Struktur ist Personalverwaltung auch in der Personalansicht.
	 * 
	 * @return ja/nein
	 */
	public boolean isPersonalansicht() {
		return isUserInGroup(GRUPPE_PERSONALANSICHT) || isPersonalverwaltung();// || isZeitbuchung();
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe Sekretariat ist.
	 * Durch die hierarchische Struktur ist Personalverwaltung auch in der Gruppe Sekretariat.
	 * 
	 * @return ja/nein
	 */
	public boolean isSekretariat() {
		return isUserInGroup(GRUPPE_SEKRETARIAT) || isPersonalverwaltung();
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe Sekretariat ist.
	 * Die hierarchische Struktur wird nicht berücksichtigt (keine Personalverwaltung).
	 * 
	 * @return ja/nein
	 */
	public boolean isSekretariatOhneVerwaltung() {
		return isUserInGroup(GRUPPE_SEKRETARIAT);
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe DR-Info ist.
	 * 
	 * @return ja/nein
	 */
	public boolean isDrInfo() {
		return isUserInGroup(GRUPPE_DR_INFO);
	}


	/**
	 * Prüft ob der Benutzer in der Gruppe AL (TL) ist.<br>
	 * Durch die hierarchische Struktur sind Personen der Gruppe Sekretariat auch in der Leitung.
	 * 
	 * @return ja/nein
	 */
	public boolean isAL() {
		try 
		{
			return isUserInGroup(GRUPPE_TL_AL) || isSekretariat()
					|| CoFreigabeberechtigungen.hasBerechtigungen(getPersonID());
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe AL (TL) ist.<br>
	 * 
	 * @return ja/nein
	 */
	public boolean isNurAL() {
		return isUserInGroup(GRUPPE_TL_AL);
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe AL (TL) ist.<br>
	 * 
	 * @return ja/nein
	 */
	public boolean isSE() {
		// TODO Kleemann
		// SE als Position, um die auch auswerten zu können?
		// AL-berechtigungen sind über die Benutzergruppe definiert
		// 
		// -> Berechtigungen sollten über die Gruppe laufen, Position nur wenn sie ausgewertet werden sollen
		return isUserInGroup(GRUPPE_TL_AL);
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe AL (TL) ist + Se/Va.<br>
	 * 
	 * @return ja/nein
	 */
	public boolean isGruppeOfaOhneGenehmigung() {
		try
		{
			CoPerson coPerson = CoPerson.getInstance();
			coPerson.moveToID(getPersonID());
			int positionID = coPerson.getPositionID();
			
			return isNurAL() || positionID == CoPosition.ID_KL || positionID == CoPosition.ID_GFT || positionID == CoPosition.ID_GFV;
		} 
		catch (Exception e) 
		{
			return false;
		}
	}

	
	/**
	 * Prüft ob der Benutzer einfacher Mitarbeiter ist.
	 * 
	 * @return ja/nein
	 */
	public boolean isMitarbeiter() {
		return isUserInGroup(GRUPPE_MITARBEITER);
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe Projektverwaltung ist.
	 * Durch die hierarchische Struktur sind Administratoren auch in der Projektverwaltung.
	 * 
	 * @return ja/nein
	 */
	public boolean isProjektverwaltung() {
		return isUserInGroup(GRUPPE_PROJEKTVERWALTUNG) || isAdmin();
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe Projektauswertung ist.<br>
	 * Durch die hierarchische Struktur sind Personen der Gruppe Projektverwaltung auch in der Gruppe Projektauswertung.
	 * 
	 * @return ja/nein
	 */
	public boolean isProjektauswertung() {
		return isUserInGroup(GRUPPE_PROJEKTAUSWERTUNG) || isProjektverwaltung();
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe Projektcontrolling ist.<br>
	 * 
	 * @return ja/nein
	 */
	public boolean isProjektcontrolling() {
		return isUserInGroup(GRUPPE_PROJEKTCONTROLLING);
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe KGG ist.<br>
	 * 
	 * @return ja/nein
	 */
	public boolean isKGG() {
		return isUserInGroup(GRUPPE_KGG);
	}

	
	/**
	 * Prüft ob der Benutzer in der Gruppe berechtigt ist ein Projekt zu bearbeiten.<br>
	 * AL dürfen dies.
	 * 
	 * @return ja/nein
	 */
	public boolean isProjektbearbeiter() {
		return isUserInGroup(GRUPPE_TL_AL); // TODO || isSE();
	}


	/**
	 * Gibt die PersonID der aktuellen Person zurück, wenn diese als ProjektleiterID relevant ist.
	 * Also dann, wenn die Person in der Gruppe Projektleiter ist, aber nicht in Projektansicht. 
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getPersonIDAlsProjektleiter() throws Exception {
		int projektleiterID;

		projektleiterID = 0;
		if (!isProjektauswertung())
		{
			projektleiterID = getPersonID();
		}
		
		return projektleiterID;
	}
	
	
	/**
	 * Prüft ob der Benutzer die detaillierten Farben für die Anwesenheit sehen darf.<br>
	 * AL, Sekretärinnen... und Personalansicht.
	 * 
	 * @return ja/nein
	 */
	public boolean isColorAnwesenheitDetail() {
		return isAL() || isPersonalansicht();
	}

	
	/**
	 * Prüft ob der Benutzer die detaillierten Angaben zu Krank/Krank ohne Lfz. in der Anwesenheitsübersicht sehen darf.<br>
	 * Nur Personalverwaltung, nicht alle Admins 
	 * 
	 * @return ja/nein
	 */
	public boolean isGruppeAnwesenheitsuebersichtDetail() {
		return isPersonalverwaltungOhneAdmin() || isEntwickler();
	}

	
	/**
	 * Prüft ob der Benutzer die Projektmerkmale einsehen darf.
	 * 
	 * @return ja/nein
	 */
	public boolean isProjektmerkmalAnsicht() {
		return isProjektverwaltung();
	}


	/**
	 * Prüft ob der Benutzer in der Gruppe ist
	 * 
	 * @param groupName Gruppenname
	 * @return ja/nein
	 */
	private boolean isUserInGroup(String groupName) {
		int groupID;
		
		if (moveToGroup(groupName))
		{
			groupID = m_coGroups.getGroupID();
		
			return Session.getInstance().getUserInfo().isUserInGroup(groupID);
		}
		
		return false;
	}


	/**
	 * Gehe im Group-CO zu der Gruppe
	 * 
	 * @param groupName
	 * @return Gruppe vorhanden
	 */
	private boolean moveToGroup(String groupName) {
		return m_coGroups.moveTo(groupName, "field.rtblgroups.groupname");
	}

	
	/**
	 * PersonID des aktuellen Users
	 * 
	 * @return
	 */
	public static int getPersonID() throws Exception {
		return CoPerson.getInstance().getIdByUserID(getUserID());
	}

	
	/**
	 * PersonID des aktuellen Users ist die übergebene
	 * 
	 * @return
	 */
	public static boolean isPerson(int personID) throws Exception {
		return CoPerson.getInstance().getIdByUserID(getUserID()) == personID;
	}


	/**
	 * Alle ggf. nicht sichtbaren Profile-Koordinaten löschen<br>
	 * Durch verschiedene Auflösungen und Bildschirme (z. B. im OFA) können Dialogfenster in den nicht sichtbaren Bereich verschoben werden.
	 * 
	 * @throws Exception
	 */
	public static void resetProfileNichtSichtbar() throws Exception {

		// alle Profile-Angaben mit negativen Werten löschen, da diese im unsichtbaren Bereich liegen könnten
		Application.getLoaderBase().execute("DELETE FROM tblProfiles WHERE  UserID = '" 
				+ Session.getInstance().getUserInfo().getLoginName() + "' and value LIKE '-%';");
		
		// alle Profile-Angaben zum Monatseinsatzblatt-Projekt und Buchungsdialog löschen, die liegen oft im positiven, unsichtbaren Bereich
		Application.getLoaderBase().execute("DELETE FROM tblProfiles WHERE  UserID = '" 
				+ Session.getInstance().getUserInfo().getLoginName() + "' and (KeyPath LIKE '%buchung.%' OR KeyPath LIKE '%.projekt');");

		
		// aktuell geladene Profile-Angaben löschen und neu laden
		Session.getInstance().getProfile().clear();
		Session.getInstance().getProfile().load(Session.getInstance().getUserInfo().getLoginName());
	}

}
