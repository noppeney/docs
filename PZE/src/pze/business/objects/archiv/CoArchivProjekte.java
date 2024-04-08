package pze.business.objects.archiv;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;

/**
 * Klasse zum Laden der Archiv-Einträge für Projekte
 * 
 * @author lisiecki
 */
public class CoArchivProjekte extends AbstractCacheObject { // TODO ggf. mit CoArchivBewegungsdaten vereinen

	/**
	 * für Jahre mit noch nicht archivierten Daten
	 */
	CoArchivProjekte m_coMonatseinsatzblatt;

	
	
	/**
	 * Kontruktor
	 */
	public CoArchivProjekte() {
		super("table." + CoAuftrag.TABLE_NAME);
	}
	
	
	/**
	 * Alle Jahre laden
	 * 
	 * @throws Exception
	 */
	public void loadArchivJahre() throws Exception {
		
		// archivierte Jahre
		loadArchivJahre(this, true);
		
		// nicht archivierte Jahre
		m_coMonatseinsatzblatt = new CoArchivProjekte();
		loadArchivJahre(m_coMonatseinsatzblatt, false);
	}

	
	/**
	 * Alle Jahre laden
	 * 
	 * @param co
	 * @param loadArchiv
	 * @throws Exception
	 */
	private void loadArchivJahre(CoArchivProjekte co, boolean loadArchiv) throws Exception {
		
		// Feld für Jahreszahl
		co.removeField(getResIdJahr());
		co.addField(getResIdJahr());

		// Daten laden aus Monatseinsatzblatt oder aus dem Archiv
		co.emptyCache();
		Application.getLoaderBase().load(co, "SELECT DISTINCT data.Jahr FROM "
				+ (loadArchiv ? getDatabase(loadArchiv, false, false) : getDatabase(loadArchiv, true, true))
				+ " ORDER BY Jahr");
	}
	
	
	/**
	 * Datenbasis mit Projekten und dem Jahr der letzten eingetragenen Stunden laden
	 * 
	 * @param loadArchiv Daten aus produktiv oder Archiv laden
	 * @param checkStatusAuftrag Status auf abgeschlossen prüfen
	 * @param checkStatusAbruf Status auf abgeschlossen prüfen
	 * @return
	 */
	public static String getDatabase(boolean loadArchiv, boolean checkStatusAuftrag, boolean checkStatusAbruf) {
		String praefix, statusIDs;
		
		praefix = Archivierer.getPraefixMoveFromTbl(!loadArchiv);
		statusIDs = getStatusIDsArchivierung();
		
		return "(SELECT m.AuftragID" + (checkStatusAbruf ? ", m.AbrufID" : "") + ", MAX(YEAR(Datum)) AS Jahr FROM " + praefix + CoMonatseinsatzblatt.TABLE_NAME + " m "
		+ (checkStatusAuftrag ? " LEFT OUTER JOIN " 
		+ praefix 
		+ "tblAuftrag au on (m.AuftragID=au.ID) " : "")
		+ (checkStatusAbruf ? " LEFT OUTER JOIN " 
		+ praefix 
		+ "tblAbruf ab on (m.AbrufID=ab.ID) " : "")
		+ " WHERE WertZeit > 0 "
//		+ (checkStatusAuftrag ? " AND au.StatusID IN " + statusIDs + " AND AbrufID IS NULL ": "")
		+ (checkStatusAuftrag ? " AND au.StatusID IN " + statusIDs + (checkStatusAbruf ? "" : " AND AbrufID IS NULL ") : "")
//		+ (checkStatusAuftrag ? " AND au.StatusID IN " + statusIDs : "")
		+ (checkStatusAbruf ? " AND (ab.StatusID IN " + statusIDs + " OR ab.StatusID IS NULL)": "")
		+ " GROUP BY m.AuftragID" + (checkStatusAbruf ? ", m.AbrufID" : "") + ") data";
	}


	/**
	 * Alle Stati als Liste, die für die Archvierung von Projekten zulässig sind
	 * 
	 * @return
	 */
	public static String getStatusIDsArchivierung() {
		String statusIDs;
		statusIDs = " (" 
//		+ CoStatusProjekt.STATUSID_ABGERECHNET + ", "
				+ CoStatusProjekt.STATUSID_ABGESCHLOSSEN 
//				+ ", " + CoStatusProjekt.STATUSID_H 
				// in CoArchivprojekte
				+ ") ";
		return statusIDs;
	}


	/**
	 * Laden des ersten Datums
	 * @return 
	 * @throws Exception
	 */
	public static int getFirstJahrForArchivierung() throws Exception {
		return Format.getIntValue(Application.getLoaderBase().executeScalar("SELECT MIN(Jahr) FROM " + getDatabase(false, true, true)));
	}
	

	private String getResIdJahr() {
		return "virt.field.monatseinsatzblatt.jahr";
	}


	private IField getFieldJahr() {
		return getField(getResIdJahr());
	}


	@Override
	public int getJahr() {
		return Format.getIntValue(getFieldJahr());
	}
	

	@Override
	public IField getFieldBezeichnung() {
		return getFieldJahr();
	}

	
	/**
	 * Zu der Zeile mit dem Jahr wechseln
	 * 
	 * @param tag
	 * @return
	 */
	private boolean moveToJahr(int jahr) {
		return moveTo(jahr, getResIdJahr());
	}


	/**
	 * Jahr bereits vollständig archiviert oder nur teilweise durch Personen
	 * 
	 * @return
	 */
	public boolean isVollstaendigArchiviert() {
		return !m_coMonatseinsatzblatt.moveToJahr(getJahr());
	}


	/**
	 * Das Vorjahr ist bereits vollständig archiviert oder es ist das erste Jahr
	 * 
	 * @return
	 */
	public boolean isVorjahrArchiviert() {
		// es ist das erste Jahr oder das Vorjahr ist archiviert
		return !movePrev() || isVollstaendigArchiviert();
	}


	/**
	 * Das nächste Jahr ist bereits vollständig archiviert
	 * 
	 * @return
	 */
	public boolean isNextJahrArchiviert() {
		// es ist das letzte Jahr oder das nächste Jahr ist archiviert
		return moveNext() && isVollstaendigArchiviert();
	}


	/**
	 * Das nächste Jahr ist bereits vollständig archiviert
	 * 
	 * @return
	 */
	public int getFirstNichtArchiviert() {
		m_coMonatseinsatzblatt.moveFirst();
		return m_coMonatseinsatzblatt.getJahr();
	}


	@Override
	public String getNavigationBitmap() {
		// Bitmap in Abhängigkeit davon, ob bereits alle Daten für das Jahr archiviert wurden
		if (isVollstaendigArchiviert())
		{
			return "brick.go";
		}
		else
		{
			return "brick.edit";
		}
	}

}
