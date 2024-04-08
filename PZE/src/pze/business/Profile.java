package pze.business;

import framework.Application;
import framework.business.interfaces.FieldType;
import framework.business.interfaces.session.ProfileItem;
import framework.business.session.Session;


/**
 * Klasse zur Organisation der Profile-Daten (Export-Pfade)
 * 
 * @author Lisiecki
 */
public class Profile {
	
	public static final String KEY_MONATSEINSATZBLATT = "export.monatseinsatzblatt";
	public static final String KEY_AUSZAHLUNG_UEBERSTUNDEN = "export.auszahlungueberstunden";
	
	public static final String KEY_VERLETZERLISTE = "export.verletzerliste";
	public static final String KEY_DIENSTREISEN = "export.dienstreisen";
	
	public static final String KEY_AUSWERTUNG_PERSONEN = "export.auswertungpersonen";
	public static final String KEY_AUSWERTUNG_BUCHHALTUNG = "export.auswertungbuchhaltung";
	public static final String KEY_AUSWERTUNG_ANABWESENHEIT = "export.auswertunganabwesenheit";
	public static final String KEY_AUSWERTUNG_URLAUBSPLANUNG = "export.urlaubsplanung";
	public static final String KEY_AUSWERTUNG_PROJEKTE = "export.auswertungprojekte";
	
	public static final String KEY_ADMINISTRATION = "export.administration";


	/**
	 * Profile-Eintrag setzen
	 * 
	 * @param path
	 */
	public static void setProfileItem(String key, String path) {
		if(path != null)
		{
			Session.getInstance().getProfile().setItem(key, path, FieldType.TEXT);
		}
	}

	
	/**
	 * Profile-Eintrag laden
	 * 
	 * @param key
	 * @return
	 */
	public static String getProfileItem(String key) {
		 ProfileItem item = Session.getInstance().getProfile().getItem(key);
		 
		 if (item != null)
		 {
			 return (String) item.value;
		 }
		 
		 return null;
	}

	
	/**
	 * Profile-Eintrag für einen Dateipfad laden
	 * 
	 * @param key
	 * @return Pfad oder WorkingDirectory
	 */
	public static String getProfileItemPfad(String key) {
		 String path;
		 
		 path = getProfileItem(key);
		 
		 if (path == null)
		 {
			 return Application.getWorkingDirectory();
		 }
		 
		 return path;
	}


	/**
	 * Profile-Eintrag für den Pfad der Exportdateien Monatseinsatzblatt setzen
	 * 
	 * @param path
	 */
	public static void setProfileItemExportMonatseinsatzblatt(String path) {
		setProfileItem(KEY_MONATSEINSATZBLATT, path);
	}


	/**
	 * Profile-Eintrag für den Pfad der Exportdateien Verletzerliste setzen
	 * 
	 * @param path
	 */
	public static void setProfileItemExportVerletzerliste(String path) {
		setProfileItem(KEY_VERLETZERLISTE, path);
	}


	/**
	 * Profile-Eintrag für den Pfad der Exportdateien AuswertungPersonen setzen
	 * 
	 * @param path
	 */
	public static void setProfileItemExportAuswertungPersonen(String path) {
		setProfileItem(KEY_AUSWERTUNG_PERSONEN, path);
	}


	/**
	 * Profile-Eintrag für den Pfad der Exportdateien AuswertungProjekte setzen
	 * 
	 * @param path
	 */
	public static void setProfileItemExportAuswertungProjekte(String path) {
		setProfileItem(KEY_AUSWERTUNG_PROJEKTE, path);
	}


	/**
	 * Profile-Eintrag für den Pfad der Exportdateien Monatseinsatzblatt laden
	 */
	public static String getProfileItemExportMonatseinsatzblatt() {
		 return getProfileItem(KEY_MONATSEINSATZBLATT);
	}


	/**
	 * Profile-Eintrag für den Pfad der Exportdateien Verletzerliste laden
	 */
	public static String getProfileItemExportVerletzerliste() {
		 return getProfileItem(KEY_VERLETZERLISTE);
	}


	/**
	 * Profile-Eintrag für den Pfad der Exportdateien AuswertungPersonen laden
	 */
	public static String getProfileItemExportAuswertungPersonen() {
		 return getProfileItem(KEY_AUSWERTUNG_PERSONEN);
	}


	/**
	 * Profile-Eintrag für den Pfad der Exportdateien AuswertungProjekte laden
	 */
	public static String getProfileItemExportAuswertungProjekte() {
		 return getProfileItem(KEY_AUSWERTUNG_PROJEKTE);
	}


	

}
