package pze.business.objects.personen.monatseinsatzblatt;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import framework.Application;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungKGG;
import pze.business.objects.auswertung.CoAuswertungProjekt;
import pze.business.objects.auswertung.CoAuswertungProjektstundenauswertung;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAbrufProjektmerkmal;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoAuftragProjektmerkmal;
import pze.business.objects.projektverwaltung.CoKostenstelle;
import pze.business.objects.projektverwaltung.CoMitarbeiterProjekt;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.projektverwaltung.Projektstunden;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.business.objects.projektverwaltung.Zeitraum;
import pze.business.objects.reftables.CoStatusStundenwertMonatseinsatzblatt;
import pze.business.objects.reftables.CoStundenart;
import pze.business.objects.reftables.projektverwaltung.CoAusgabezeitraum;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;

/**
 * CacheObject für die Daten (Stunden und Tätigkeiten) zu Monatseinatzblättern.<br>
 * 
 * @author Lisiecki
 *
 */
public class CoMonatseinsatzblatt extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblmonatseinsatzblatt";

	private int m_personID;
	private Date m_datum;
	private GregorianCalendar m_gregDatum;
	
	private Map<Integer, Projektstunden> m_mapProjektstundenAuftrag;
	private Map<Integer, Projektstunden> m_mapProjektstundenAbruf;

	private Map<String, CoMitarbeiterProjekt> m_mapProjektMitarbeiterZuordnung;

	

	/**
	 * Kontruktor
	 */
	public CoMonatseinsatzblatt() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Laden aller Daten für das Monatseinsatzblatt des Mitarbeiters im aktuellen Monat
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public void load(int personID, Date datum) throws Exception {
		
		m_personID = personID;
		m_datum = datum;
		
		m_gregDatum = new GregorianCalendar();
		m_gregDatum.setTime(m_datum);
		
		emptyCache();
		Application.getLoaderBase().load(this, " personID=" + personID 
				+ " AND YEAR(Datum)=" + m_gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)=" + (m_gregDatum.get(Calendar.MONTH)+1), getSortFieldName());
	}
	

	/**
	 * Laden aller Daten für das Monatseinsatzblatt des Mitarbeiters im aktuellen Monat
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public void loadAuftraegeAuszahlung(int personID, Date datum) throws Exception {
		String sql;
		
		m_personID = personID;
		m_datum = datum;
		
		m_gregDatum = new GregorianCalendar();
		m_gregDatum.setTime(m_datum);
		
		addField("virt.field.tblmonatseinsatzblatt.wertprojektzeit");
		addField("virt.field.tblmonatseinsatzblatt.wertreisezeit");
		
		sql = "SELECT DISTINCT m.AuftragID, (p.WertZeit) AS WertProjektZeit,(r.WertZeit) AS WertReiseZeit, a.AuftragsNr"
				+ " FROM " + getTableName() + " m JOIN tblAuftrag a ON (m.AuftragID=a.ID)"
				+ getUnterabfrageAuftraegeAuszahlung("p", 1) + getUnterabfrageAuftraegeAuszahlung("r", 2)
				+ " WHERE personID=" + m_personID 
				+ " AND YEAR(Datum)=" + m_gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)=" + (m_gregDatum.get(Calendar.MONTH)+1)
				+ " AND (p.WertZeit > 0 OR r.WertZeit > 0)  ORDER BY a.AuftragsNr;";
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * WHERE-Teil der SQL-Abfrage für Aufträge zur Auszahlung
	 * 
	 * @return
	 */
	protected String getWhereAuftraegeAuszahlung() {
		return " WHERE personID=" + m_personID 
				+ " AND YEAR(Datum)=" + m_gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)=" + (m_gregDatum.get(Calendar.MONTH)+1)
				+ " AND WertZeit > 0 ";
	}
	

	/**
	 * Unterabfrage der SQL-Abfrage für Aufträge zur Auszahlung
	 * 
	 * @return
	 */
	protected String getUnterabfrageAuftraegeAuszahlung(String kuerzel, int stundenartID) {
		return "LEFT OUTER JOIN (SELECT AuftragID, SUM(WertZeit) AS WertZeit, AuftragsNr FROM tblmonatseinsatzblatt m"
				+ " JOIN tblAuftrag a ON (m.AuftragID=a.ID) "
				+ getWhereAuftraegeAuszahlung()
				+ " AND StundenartID=" + stundenartID + " GROUP BY AuftragID, AuftragsNr ) AS " + kuerzel + " ON (m.AuftragID=" + kuerzel + ".AuftragID)";
	}


	/**
	 * Laden der Einträge für das Monatseinsatzblatt des Mitarbeiters im aktuellen Monat, 
	 * die für das übergebene Projekt keine gültigen Werte enthalten
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public void loadNullValues(int personID, Date datum, VirtCoProjekt virtCoProjekt) throws Exception {
		loadNullValues(personID, datum, virtCoProjekt.getAuftragID(), virtCoProjekt.getAbrufID(), virtCoProjekt.getKostenstelleID(), virtCoProjekt.getBerichtsNrID(), 
				virtCoProjekt.getStundenartID());
	}
	
	
	/**
	 * Laden der Einträge für das Monatseinsatzblatt des Mitarbeiters im aktuellen Monat, 
	 * die für das übergebene Projekt keine gültigen Werte enthalten
	 * 
	 * @param datum
	 * @param auftragID
	 * @param abrufID
	 * @param kostenstelleID
	 * @param stundenartID
	 * @throws Exception
	 */
	private void loadNullValues(int personID, Date datum, int auftragID, int abrufID, int kostenstelleID, int berichtsNrID, int stundenartID) throws Exception {
		
		m_personID = personID;
		m_datum = datum;
		
		m_gregDatum = new GregorianCalendar();
		m_gregDatum.setTime(m_datum);
		
		emptyCache();
		Application.getLoaderBase().load(this, " personID=" + personID 
				+ " AND YEAR(Datum)=" + m_gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)=" + (m_gregDatum.get(Calendar.MONTH)+1)
				+ " AND auftragID " + (auftragID == 0 ? " IS NULL " : "=" + auftragID) 
				+ " AND abrufID " + (abrufID == 0 ? " IS NULL " : "=" + abrufID) 
				+ " AND kostenstelleID " + (kostenstelleID == 0 ? " IS NULL " : "=" + kostenstelleID) 
				+ " AND BerichtsNrID " + (berichtsNrID == 0 ? " IS NULL " : "=" + berichtsNrID) 
				+ " AND stundenartID =" + stundenartID
				+ " AND (WertZeit IS NULL OR WertZeit=0) "
				, getSortFieldName());
	}
	
	
	/**
	 * Laden der Einträge für das Monatseinsatzblatt des Mitarbeiters für den aktuellen Tag, 
	 * z.B. um zu prüfen ob Daten vorhanden sind
	 * 
	 * @param datum
	 * @param auftragID
	 * @param abrufID
	 * @param kostenstelleID
	 * @param stundenartID
	 * @throws Exception
	 */
	private void load(int personID, Date datum, int auftragID, int abrufID, int kostenstelleID, int berichtsNrID, int stundenartID) throws Exception {
		
		m_gregDatum = new GregorianCalendar();
		m_gregDatum.setTime(datum);
		
		emptyCache();
		Application.getLoaderBase().load(this, " personID=" + personID 
				+ " AND YEAR(Datum)=" + m_gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)=" + (m_gregDatum.get(Calendar.MONTH)+1)
				+ " AND Day(Datum)=" + (m_gregDatum.get(Calendar.DAY_OF_MONTH))
				+ " AND auftragID " + (auftragID == 0 ? " IS NULL " : "=" + auftragID) 
				+ " AND abrufID " + (abrufID == 0 ? " IS NULL " : "=" + abrufID) 
				+ " AND kostenstelleID " + (kostenstelleID == 0 ? " IS NULL " : "=" + kostenstelleID) 
				+ " AND BerichtsNrID " + (berichtsNrID == 0 ? " IS NULL " : "=" + berichtsNrID) 
				+ " AND stundenartID =" + stundenartID
				, getSortFieldName());
	}
	

	/**
	 * Laden der letzten Tätigkeit für das übergebene Projekt
	 * 
	 * @param personID
	 * @param datum
	 * @return Tätigkeit
	 * @throws Exception
	 */
	public static String loadLastTaetigkeit(int personID, Date datum, VirtCoProjekt virtCoProjekt) throws Exception {
		return loadLastTaetigkeit(personID, datum, virtCoProjekt.getAuftragID(), virtCoProjekt.getAbrufID(), 
				virtCoProjekt.getKostenstelleID(), virtCoProjekt.getBerichtsNrID(), virtCoProjekt.getStundenartID());
	}
	
	
	/**
	 * Laden der letzten Tätigkeit für das übergebene Projekt
	 * 
	 * @param datum
	 * @param auftragID
	 * @param abrufID
	 * @param kostenstelleID
	 * @param stundenartID
	 * @return Tätigkeit
	 * @throws Exception
	 */
	private static String loadLastTaetigkeit(int personID, Date datum, int auftragID, int abrufID, int kostenstelleID, int berichtsNrID, 
			int stundenartID) throws Exception {
		String sql;
		
		sql = "SELECT TOP(1) Taetigkeit FROM " + TABLE_NAME + " WHERE PersonID=" + personID + " AND Datum < '" + Format.getStringForDB(datum) + "'"
				+ " AND auftragID " + (auftragID == 0 ? " IS NULL " : "=" + auftragID) 
				+ " AND abrufID " + (abrufID == 0 ? " IS NULL " : "=" + abrufID) 
				+ " AND kostenstelleID " + (kostenstelleID == 0 ? " IS NULL " : "=" + kostenstelleID) 
				+ " AND BerichtsNrID " + (berichtsNrID == 0 ? " IS NULL " : "=" + berichtsNrID) 
				+ " AND stundenartID =" + stundenartID 
				+ " AND Taetigkeit IS NOT NULL"
				+ " ORDER BY Datum DESC";
		
		return Format.getStringValue(Application.getLoaderBase().executeScalar(sql));
	}
	

	/**
	 * Laden der bereits gebuchten Stunden für einen Auftrag
	 * 
	 * @param auftragID
	 * @return
	 * @throws Exception
	 */
	private static int loadStundenAuftrag(int auftragID) throws Exception {
		String sql;
		
		sql = "SELECT SUM(WertZeit) FROM " + TABLE_NAME + " WHERE auftragID=" + auftragID + " AND abrufID IS NULL";
		
		return Format.getIntValue(Application.getLoaderBase().executeScalar(sql));
	}
	

	/**
	 * Laden der bereits gebuchten Stunden für einen Abruf
	 * 
	 * @param auftragID
	 * @param abrufID
	 * @return
	 * @throws Exception
	 */
	private static int loadStundenAbruf(int abrufID) throws Exception {
		String sql;
		
		sql = "SELECT SUM(WertZeit) FROM " + TABLE_NAME + " WHERE abrufID=" + abrufID;
		
		return Format.getIntValue(Application.getLoaderBase().executeScalar(sql));
	}

	
	/**
	 * Anzahl Stunden der Person in einem Zeitraum für das Projekt
	 * 
	 * @param personID
	 * @param datumVon
	 * @param datumBis
	 * @param whereprojekt AbrufID der AustragID
	 * @throws Exception
	 */
	public void loadSummeStunden(int personID, Date datumVon, Date datumBis, String whereprojekt) throws Exception {
		String sql, whereDatum, where;
		
		// SQL-Abfrage zusammensetzen
		whereDatum = CoAuswertung.getWhereDatum(datumVon, datumBis);
		where = "PersonID=" + personID + " AND " + whereprojekt + (whereDatum == null ? "" : " AND " + whereDatum);
		
		// Bezeichnung für die Spalte mit dem Zeitraum
		sql = "SELECT SUM(WertZeit) AS WertZeit"
				+ " FROM " + getTableName() + ""
				+ " WHERE " + where;

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Daten für die Projektauswertung laden (Anzahl Stunden pro Person in einem Zeitraum )
	 * 
	 * @param coAuswertungProjekt
	 * @param auftragID 
	 * @param stundenartID 
	 * @throws Exception
	 */
	public void loadAuswertungAuftrag(int auftragID, CoAuswertungProjekt coAuswertungProjekt, String stundenartID) throws Exception {
		String where;
		
		where = " AuftragID=" + auftragID;
		
		loadAuswertungProjekt(coAuswertungProjekt, where, stundenartID);
	}
	

	/**
	 * Daten für die Projektauswertung laden (Anzahl Stunden pro Person in einem Zeitraum )
	 * 
	 * @param coAuswertungProjekt
	 * @param abrufID 
	 * @param stundenartID 
	 * @throws Exception
	 */
	public void loadAuswertungAbruf(int abrufID, CoAuswertungProjekt coAuswertungProjekt, String stundenartID) throws Exception {
		String where;
		
		where = " AbrufID=" + abrufID;
		
		loadAuswertungProjekt(coAuswertungProjekt, where, stundenartID);
	}
	

	/**
	 * Daten für die Projektauswertung laden (Anzahl Stunden pro Person in einem Zeitraum )
	 * 
	 * @param coAuswertungProjekt
	 * @param where Spezifizierung  auf Auftrag oder Abruf
	 * @throws Exception
	 */
	private void loadAuswertungProjekt(CoAuswertungProjekt coAuswertungProjekt, String where, String stundenartID) throws Exception {
		int ausgabezeitraumID;
		String sql, stringZeitraum, stringJahr, whereDatum;
		
		// Feld für Zeitraum-Nr
		addField("virt.field.monatseinsatzblatt.jahr");
		addField("virt.field.monatseinsatzblatt.zeitraum");
		
		// SQL-Abfrage zusammensetzen
		whereDatum = coAuswertungProjekt.getWhereDatum();
		where = where + (whereDatum == null ? "" : " AND " + whereDatum) + (stundenartID == null ? "" : " AND StundenartID IN (" + stundenartID + ")")
				+ " AND WertZeit > 0";
		
		// Bezeichnung für die Spalte mit dem Zeitraum
		ausgabezeitraumID = coAuswertungProjekt.getAusgabezeitraumID();
		CoAusgabezeitraum.getInstance().moveToID(ausgabezeitraumID);
		stringZeitraum = "(MONTH(Datum)-1)/" + CoAusgabezeitraum.getInstance().getAnzahlMonate();
		stringJahr = "YEAR(Datum)";
		
		sql = "SELECT PersonID, SUM(WertZeit) AS WertZeit, " + stringZeitraum + " AS Zeitraum, " + stringJahr + " AS Jahr"
				+ " FROM " + getTableName() + " k JOIN tblPerson p ON (k.PersonID=p.ID) "
				+ " WHERE " + where 
				+ " GROUP BY PersonID, Nachname, Vorname, " + stringJahr + ", " + stringZeitraum
				+ " ORDER BY Nachname, Vorname, " + stringJahr + ", " + stringZeitraum;

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Daten für die Projektauswertung laden (Anzahl Stunden pro Person in einem Zeitraum )
	 * 
	 * @param coAuswertungProjektstundenauswertung
	 * @param where Spezifizierung  auf Auftrag oder Abruf
	 * @throws Exception
	 */
	public void loadAuswertungProjekt(CoAuswertungProjektstundenauswertung coAuswertungProjektstundenauswertung, String where) throws Exception {
		int ausgabezeitraumID, kostenstelleID;
		String sql, stringZeitraum, stringJahr, whereDatum;
		
		// Feld für Zeitraum-Nr
		addField("virt.field.monatseinsatzblatt.jahr");
		addField("virt.field.monatseinsatzblatt.zeitraum");
		
		// SQL-Abfrage zusammensetzen
		whereDatum = coAuswertungProjektstundenauswertung.getWhereDatum();
		where = where + (whereDatum == null ? "" : " AND " + whereDatum) + " AND WertZeit > 0";
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}
		
		// Kostenstelle
		kostenstelleID = coAuswertungProjektstundenauswertung.getKostenstelleID();
		if (kostenstelleID > 0)
		{
			where += " AND KostenstelleID=" + kostenstelleID;
		}
		
		// Bezeichnung für die Spalte mit dem Zeitraum
		ausgabezeitraumID = coAuswertungProjektstundenauswertung.getAusgabezeitraumID();
		CoAusgabezeitraum.getInstance().moveToID(ausgabezeitraumID);
		stringZeitraum = "(MONTH(Datum)-1)/" + CoAusgabezeitraum.getInstance().getAnzahlMonate();
		stringJahr = "YEAR(Datum)";
		
		sql = "SELECT SUM(WertZeit) AS WertZeit, " + stringZeitraum + " AS Zeitraum, " + stringJahr + " AS Jahr"
				+ " FROM " + getTableName() + ""
				+ " WHERE " + where 
				+ " GROUP BY " + stringJahr + ", " + stringZeitraum
				+ " ORDER BY " + stringJahr + ", " + stringZeitraum;

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Daten für die Buchhaltungs-Auswertung "Stundenübersicht" laden (Stunden pro Mitarbeiter/Monat auf ein Projekt)
	 * 
	 * @param coAuswertung
	 * @param where Spezifizierung  auf Auftrag oder Abruf
	 * @throws Exception
	 */
	public void loadAuswertungBuchhaltungStundenuebersicht(CoAuswertung coAuswertung) throws Exception {
		String sql, whereDatum, wherePerson, wherePosition, where;
		
		// zusätzliche Felder
		addField("field.tblperson.statusinternexternid");
		addField("field.tblperson.standortid");
		addField("field.tblperson.abteilungid");
		addField("field.tblperson.positionid");
		
		addField("virt.field.monatseinsatzblatt.monat");
		addField("virt.field.monatseinsatzblatt.jahr");
		
		addField("virt.field.projekt.kundeid");
		addField("virt.field.projekt.auftragid");
		addField("virt.field.projekt.abrufid");
		
		addField("virt.field.projekt.auftragprojektmerkmalid");
		addField("virt.field.projekt.abrufprojektmerkmalid");
		
		addField("virt.field.projekt.auftragedvnr");
		addField("virt.field.projekt.abrufedvnr");
		
		addField("virt.field.projekt.abrufabteilungkundeid");
		addField("virt.field.projekt.auftragabteilungkundeid");
		
//		addField("virt.field.projekt.projektmerkmal1id");
//		addField("virt.field.projekt.projektmerkmal2id");
//		addField("virt.field.projekt.projektmerkmal3id");
		
		
		// SQL-Abfrage zusammensetzen
		wherePerson = coAuswertung.getWherePerson(true);
		if (wherePerson != null)
		{
			wherePerson = wherePerson.replace("PersonID", "p.ID");
		}
		wherePosition = coAuswertung.getWherePosition();
		whereDatum = coAuswertung.getWhereDatum();
		where = (whereDatum != null ? " AND (" + whereDatum + ")" : "");
		where += (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");

		sql = "SELECT "
				+ "PersonID, StatusInternExternID, StandortID, AbteilungID, PositionID, YEAR(Datum) AS Jahr, MONTH(Datum) AS Monat, "
				+ " KundeID, AuftragID, AbrufID, AuftragAbteilungKundeID, AbrufAbteilungKundeID, "
				+ " AuftragEdvNr, AbrufEdvNr, KostenstelleID, StundenartID, SUM(WertZeit) AS WertZeit"
				
				+ " FROM "
				+ " (SELECT p.ID AS PersonID, Nachname, Vorname, StatusInternExternID, StandortID, p.AbteilungID, p.PositionID, Datum, "
				+ " WertZeit AS WertZeit,"
				+ " au.KundeID AS KundeID, m.AuftragID AS AuftragID, m.AbrufID AS AbrufID, "
				+ " au.AbteilungKundeID AS AuftragAbteilungKundeID, ab.AbteilungKundeID AS AbrufAbteilungKundeID, "
				+ " au.EdvNr AS AuftragEdvNr, ab.EdvNr AS AbrufEdvNr, m.KostenstelleID AS KostenstelleID, StundenartID"
				+ " FROM " + getTableName() + " m JOIN tblPerson p ON (m.PersonID=p.ID) "
				
				+ " JOIN tblAuftrag au ON (m.AuftragID = au.ID)"
				+ " LEFT OUTER JOIN tblAbruf ab ON (m.AbrufID = ab.ID)"
				
				+ " WHERE WertZeit > 0 " + where 
				
				+ " UNION ALL "
				+ getSqlUnterabfrageStundenuebersichtDummyKostenstellen(where, "AnzahlUrlaub", 
						CoAuftrag.ID_URLAUB_PRODUKTIV , CoAuftrag.ID_URLAUB_UNPRODUKTIV)
				
				+ " UNION ALL "
				+ getSqlUnterabfrageStundenuebersichtDummyKostenstellen(where, "AnzahlSonderurlaub", 
						CoAuftrag.ID_SONDERURLAUB_PRODUKTIV , CoAuftrag.ID_SONDERURLAUB_UNPRODUKTIV)
				
				+ " UNION ALL "
				+ getSqlUnterabfrageStundenuebersichtDummyKostenstellen(where, "AnzahlElternzeit", 
						CoAuftrag.ID_ELTERNZEIT_PRODUKTIV , CoAuftrag.ID_ELTERNZEIT_UNPRODUKTIV)
				
				+ " UNION ALL "
				+ getSqlUnterabfrageStundenuebersichtDummyKostenstellen(where, "AnzahlKrank", 
						CoAuftrag.ID_KRANK_PRODUKTIV , CoAuftrag.ID_KRANK_UNPRODUKTIV)
				
				+ " UNION ALL "
				+ getSqlUnterabfrageStundenuebersichtDummyKostenstellen(where, "AnzahlKrankOhneLfz", 
						CoAuftrag.ID_KRANK_OHNE_LFZ_PRODUKTIV , CoAuftrag.ID_KRANK_OHNE_LFZ_UNPRODUKTIV)
				
				+ ") AS db" 

				+ " GROUP BY PersonID, Nachname, Vorname, StatusInternExternID, StandortID, AbteilungID, PositionID, YEAR(Datum), MONTH(Datum), "
				+ " KundeID, AuftragID, AbrufID, AuftragAbteilungKundeID, AbrufAbteilungKundeID, "
				+ " AuftragEdvNr, AbrufEdvNr, KostenstelleID, StundenartID"
				
				+ " ORDER BY Nachname, Vorname";

		emptyCache();
		Application.getLoaderBase().load(this, sql);
		
		// Projektmerkmale hinzufügen
		addProjektmerkmalAuftrag();
		addProjektmerkmalAbruf();
	}

	
	public void loadAuswertungKGG(CoAuswertungKGG coAuswertung) throws Exception {
		int auftragID, abrufID, kostenstelleID, berichtsNrID, personID, stundenartID;
		String whereDatum, where, sql;

		// Feld für PSP-Element hinzufügen
		if (getField(CoKostenstelle.getResIdPSP()) == null)
		{
			addField(CoKostenstelle.getResIdPSP());
		}
		
		// SQL-Abfrage zusammensetzen
		auftragID = coAuswertung.getAuftragID();
		abrufID = coAuswertung.getAbrufID();
		kostenstelleID = coAuswertung.getKostenstelleID();
//		kostenstelleID2 = coAuswertung.getKostenstelleID2();
		berichtsNrID = coAuswertung.getBerichtsNrID();
		personID = coAuswertung.getPersonID();
		stundenartID = coAuswertung.getStundenartID();
		whereDatum = coAuswertung.getWhereDatum();

		where = (whereDatum == null ? "" : " AND " + whereDatum)
		+ (auftragID == 0 ? "" : " AND AuftragID=" + auftragID) 
		+ (abrufID == 0 ? "" : " AND AbrufID=" + abrufID) 
		+ (kostenstelleID == 0 ? "" : " AND KostenstelleID=" + kostenstelleID) 
//		+ (kostenstelleID2 == 0 ? "" : " AND KostenstelleID=" + kostenstelleID2) 
		+ (berichtsNrID == 0 ? "" : " AND BerichtsNrID=" + berichtsNrID) 
		+ (personID == 0 ? "" : " AND PersonID=" + personID) 
		+ (stundenartID == 0 ? "" : " AND StundenartID=" + stundenartID) 
		+ " AND StundenartID IN (" + CoStundenart.STATUSID_ERSTELLUNG + ", " + CoStundenart.STATUSID_QS + ")"
		+ " AND WertZeit>0";
		
		// Statement zusammensetzen
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}
		
		sql = "SELECT m.*, k.PSP FROM " + getTableName() + " m JOIN tblKostenstelle k on (m.KostenstelleID=k.ID) WHERE " + where 
				+ " ORDER BY " + getSortFieldName();
		
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	
	
	/**
	 * Summe der Stunden pro Person im aktuellen und vorherigen Monat laden.<br>
	 * Nur Zeiten in der Vergangenheit werden berücksichtigt.
	 * 
	 * @throws Exception
	 */
	public void loadSumme() throws Exception {
		String sql;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(null);
		
		sql = " SELECT PersonID, SUM(WertZeit) AS WertZeit FROM tblMonatseinsatzblatt WHERE " + 
				" YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)>" + (gregDatum.get(Calendar.MONTH)-1) + 
				" AND Datum<'" + Format.getString(Format.getDate0Uhr(new Date())) + "' " + 
				" Group By PersonID";

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Summe der Stunden pro Person auf sonstige-Abteilungs-Aufträge laden. <br>
	 * Wenn der letzte Monat noch nicht gesperrt ist, wird er auch berücksichtigt.
	 * 
	 * @throws Exception
	 */
	public void loadWsonstiges() throws Exception {
		String sql;
		GregorianCalendar gregDatum;
		
		// Datum Monatseinsatzblatt neu laden, sonst wird die Schließung des Monats nicht erkannt
		CoFirmenparameter.getInstance().loadAll();
		gregDatum = Format.getGregorianCalendar(CoFirmenparameter.getInstance().getDatumMonatseinsatzblatt());
		
		sql = " SELECT PersonID, MAX(Datum) AS Datum, SUM(WertZeit) AS WertZeit, AuftragID FROM tblMonatseinsatzblatt WHERE " + 
				" YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)>" + (gregDatum.get(Calendar.MONTH)) + 
				" AND Datum<'" + Format.getString(Format.getDate0Uhr(new Date())) + "' " + 
				" AND AuftragID IN (SELECT ID FROM tblAuftrag WHERE Message8Stunden=1) " + 
				" Group By PersonID, AuftragID, MONTH(Datum)"
				+ " HAVING SUM(WertZeit) > " + (8*60-1)
				+ " ORDER BY PersonID";

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Projektmerkmal für den Auftrag laden und in das Cacheobjekt eintragen
	 * 
	 * @throws Exception
	 */
	protected void addProjektmerkmalAuftrag() throws Exception {
//		int iProjektmerkmal;
		CoAuftragProjektmerkmal coAuftragProjektmerkmal = new CoAuftragProjektmerkmal();
		
		if (moveFirst())
		{
			do
			{
				coAuftragProjektmerkmal.loadByAuftragID(getAuftragID());

				if (coAuftragProjektmerkmal.moveFirst())
				{
//					iProjektmerkmal = 1;
					do
					{
						getField("virt.field.projekt.auftragprojektmerkmalid" /*+ iProjektmerkmal++ + "id"*/).
						setValue(coAuftragProjektmerkmal.getProjektmerkmalID());
					} while (coAuftragProjektmerkmal.moveNext());
				}
			} while (moveNext());
		}
	}
	

	/**
	 * Projektmerkmal für den Abruf laden und in das Cacheobjekt eintragen
	 * 
	 * @throws Exception
	 */
	protected void addProjektmerkmalAbruf() throws Exception {
//		int iProjektmerkmal;
		CoAbrufProjektmerkmal coAbrufProjektmerkmal = new CoAbrufProjektmerkmal();
		
		if (moveFirst())
		{
			do
			{
				coAbrufProjektmerkmal.loadByAbrufID(getAbrufID());

				if (coAbrufProjektmerkmal.moveFirst())
				{
//					iProjektmerkmal = 1;
					do
					{
						getField("virt.field.projekt.abrufprojektmerkmalid" /*+ iProjektmerkmal++ + "id"*/).
						setValue(coAbrufProjektmerkmal.getProjektmerkmalID());
					} while (coAbrufProjektmerkmal.moveNext());
				}
			} while (moveNext());
		}
	}
	
	
	/**
	 * SQL-Unterabfrage generieren für die Auswertung Buchhaltung Stundenübersicht
	 * 
	 * @param where vorgefertigter where-Teil der Abfrage
	 * @param spalteKontowert zu prüfender Wert in tblKontowert
	 * @param id_produktiv AuftragID
	 * @param id_unproduktiv AuftragID
	 * @return
	 */
	private String getSqlUnterabfrageStundenuebersichtDummyKostenstellen(String where, String spalteKontowert, int id_produktiv, int id_unproduktiv){
		
		// Sonderfall Krank, Zeiten und ganze Tage sind möglich
		where += " AND (ISNULL(" + spalteKontowert + ", 0) > 0 " +  (id_produktiv ==  CoAuftrag.ID_KRANK_PRODUKTIV ? " OR ISNULL(WertKrank, 0) > 0 " : "") + ")";
		
		// richtiges Zeitmodell für Sollarbeitszeit
		where += " AND ( z.ID IS NULL " + " OR (GueltigVon <= Datum AND Datum <= GueltigBis) OR (GueltigVon <= Datum AND GueltigBis IS NULL) ) ";
		
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}
		
		return " SELECT p.ID AS PersonID, Nachname, Vorname, StatusInternExternID, StandortID, AbteilungID, PositionID, Datum, "
				
				// Sonderfall Krank, Zeiten und ganze Tage sind möglich
				+ (id_produktiv ==  CoAuftrag.ID_KRANK_PRODUKTIV ? " ISNULL(WertKrank, 0) + " : "")
				
				// Sollarbeitszeit über das Zeitmodell bestimmen, da bei Elternzeit und KrankOhneLfz das Tagessoll auf 0 gesetzt wird
				+ " CASE WHEN DATEPART(dw, Datum) = 1 THEN TagessollMontag "
				+ " WHEN DATEPART(dw, Datum) = 2 THEN TagessollDienstag "
				+ " WHEN DATEPART(dw, Datum) = 3 THEN TagessollMittwoch "
				+ " WHEN DATEPART(dw, Datum) = 4 THEN TagessollDonnerstag "
				+ " WHEN DATEPART(dw, Datum) = 5 THEN TagessollFreitag "
				+ " WHEN DATEPART(dw, Datum) = 6 THEN TagessollSamstag "
				+ " WHEN DATEPART(dw, Datum) = 7 THEN TagessollSonntag END"
				
				+ " * ISNULL(" + spalteKontowert + ", 0) AS WertZeit, NULL AS KundeID, " 
				+ " CASE WHEN IstProduktiv = 'TRUE' THEN " + id_produktiv + " ELSE " + id_unproduktiv + " END AS AuftragID, "
				+ " NULL AS AbrufID, NULL AS AuftragEdvNr, NULL AS AbrufEdvNr, NULL AS AuftragAbteilungKunde, NULL AS AbrufAbteilungKunde, "
				+ " NULL AS KostenstelleID, NULL AS StundenartID "
				+ " FROM tblKontowert k JOIN tblPerson p ON (k.PersonID=p.ID) JOIN rtblPosition pos ON (p.PositionID=pos.ID)"
				+ " LEFT OUTER JOIN stblPersonZeitmodell spz ON (p.ID = spz.personID) JOIN tblZeitmodell z ON  (spz.ZeitmodellID=z.ID)"
				+ " WHERE " + where;
	}
	

	/**
	 * Daten für die Projektauswertung laden (Anzahl Stunden pro Person in einem Zeitraum )
	 * 
	 * @param coAuswertung
	 * @param where Spezifizierung  auf Auftrag oder Abruf
	 * @throws Exception
	 */
	public void loadAuswertungMonatseinsatzblatt(CoAuswertung coAuswertung) throws Exception {
		String sql, whereDatum, wherePerson, wherePosition, where;
		
		// zusätzliche Felder
		addField("field.tblkontowert.wertbezahltearbeitszeit");
		addField("virt.field.tblmonatseinsatzblatt.differenz");
		
		// SQL-Abfrage zusammensetzen
		wherePerson = coAuswertung.getWherePerson().replace("PersonID", "p.ID");
		wherePosition = coAuswertung.getWherePosition();
		whereDatum = coAuswertung.getWhereDatum();
		where = (whereDatum != null ? " AND (" + whereDatum + ")" : "");
		where += (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}

		sql = "SELECT t.PersonID, SUM(t.WertZeit) AS WertZeit, SUM(t.WertBezahlteArbeitszeit) AS WertBezahlteArbeitszeit,"
				+ " SUM(t.WertZeit) - SUM(t.WertBezahlteArbeitszeit) AS Differenz FROM "
				+ " (SELECT p.ID AS PersonID, Nachname, Vorname, WertZeit AS WertZeit, 0 AS WertBezahlteArbeitszeit"
				+ " FROM " + getTableName() + " m JOIN tblPerson p ON (m.PersonID=p.ID) "
				+ " WHERE " + where
				
				+ " UNION ALL "
				
				+ " SELECT p.ID AS PersonID, Nachname, Vorname, 0 AS WertZeit, WertBezahlteArbeitszeit-ISNULL(WertKrank, 0) AS WertBezahlteArbeitszeit"
				+ " FROM tblKontowert k JOIN tblPerson p ON (k.PersonID=p.ID) "
				+ " WHERE " + where + " AND ISNULL(AnzahlKrank, 0)<>1 AND ISNULL(AnzahlUrlaub, 0)<>1 AND ISNULL(AnzahlSonderurlaub, 0)<>1) AS t" 
				+ " GROUP BY PersonID, Nachname, Vorname "
				+ " ORDER BY Nachname, Vorname";

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Löschen der Einträge für das Monatseinsatzblatt des Mitarbeiters im aktuellen Monat, 
	 * die für das übergebene Projekt keine gültigen Werte enthalten
	 * 
	 * @param personID
	 * @param datum
	 * @param virtCoProjekt
	 * @throws Exception
	 */
	public void deleteNullValues(int personID, Date datum, VirtCoProjekt virtCoProjekt) throws Exception {
		loadNullValues(personID, datum, virtCoProjekt);
		
		if (getRowCount() == 0)
		{
			return;
		}
		
		// alle Daten löschen
		begin();
		deleteAll();
		save();
	}

	
	public String getKey(){
		return getTableName();
	}
	

	public void setTagDesMonatsDatum(int tagDesMonats) {
		m_gregDatum.set(Calendar.DAY_OF_MONTH, tagDesMonats);
		m_datum = new Timestamp(m_gregDatum.getTimeInMillis());
	}


	public void setDatum(Date datum) {
		getFieldDatum().setValue(datum);
	}


	/**
	 * Tag des Monats vom aktuellen Datum
	 * 
	 * @return
	 */
	public int getTagDesMonats() {
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(getDatum());
		
		return gregDatum.get(Calendar.DAY_OF_MONTH);
	}


	public int getKundeID() {
		return Format.getIntValue(getField("field." + getTableName() + ".kundeid").getValue());
	}


	public IField getFieldAuftrag() {
		return getField("field." + getTableName() + ".auftragid");
	}


	public void setAuftragID(int auftragID) {
		getFieldAuftrag().setValue(auftragID);
	}


	public int getAuftragID() {
		return Format.getIntValue(getFieldAuftrag().getValue());
	}


	public IField getFieldAbruf() {
		return getField("field." + getTableName() + ".abrufid");
	}


	public void setAbrufID(int abrufID) {
		getFieldAbruf().setValue(abrufID);
	}


	public int getAbrufID() {
		return Format.getIntValue(getFieldAbruf().getValue());
	}


	public IField getFieldKostenstelleID() {
		return getField("field." + getTableName() + ".kostenstelleid");
	}


	public void setKostenstelleID(int kostenstelleID) {
		getFieldKostenstelleID().setValue(kostenstelleID);
	}


	public int getKostenstelleID() {
		return Format.getIntValue(getFieldKostenstelleID().getValue());
	}


	public IField getFieldBerichtsNrID() {
		return getField("field." + getTableName() + ".berichtsnrid");
	}


	public void setBerichtsNrID(int berichtsNrID) {
		getFieldBerichtsNrID().setValue(berichtsNrID);
	}


	public int getBerichtsNrID() {
		return Format.getIntValue(getFieldBerichtsNrID());
	}


	public IField getFieldStundenartID() {
		return getField("field." + getTableName() + ".stundenartid");
	}


	public void setStundenartID(int stundenartID) {
		getFieldStundenartID().setValue(stundenartID);
	}


	public int getStundenartID() {
		return Format.getIntValue(getFieldStundenartID().getValue());
	}


	public IField getFieldWertZeit() {
		return getField("field." + getTableName() + ".wertzeit");
	}


	public int getWertZeit() {
		return Format.getIntValue(getFieldWertZeit().getValue());
	}


	public void setWertZeit(Integer zeit) {
		getFieldWertZeit().setValue(zeit);
	}


	public IField getFieldWertProjektZeit() {
		return getField("virt.field." + getTableName() + ".wertprojektzeit");
	}


	public int getWertProjektZeit() {
		return Format.getIntValue(getFieldWertProjektZeit().getValue());
	}


	public IField getFieldWertReiseZeit() {
		return getField("virt.field." + getTableName() + ".wertreisezeit");
	}


	public int getWertReiseZeit() {
		return Format.getIntValue(getFieldWertReiseZeit().getValue());
	}


	public IField getFieldTaetigkeit() {
		return getField("field." + getTableName() + ".taetigkeit");
	}


	public String getTaetigkeit() {
		return Format.getStringValue(getFieldTaetigkeit().getValue());
	}


	public void setTaetigkeit(String taetigkeit) {
		getFieldTaetigkeit().setValue(taetigkeit);
	}


	public IField getFieldBemerkung() {
		return getField("field." + getTableName() + ".bemerkung");
	}


	public String getBemerkung() {
		return Format.getStringValue(getFieldBemerkung().getValue());
	}


	public void setBemerkung(String bemerkung) {
		getFieldBemerkung().setValue(bemerkung);
	}


	/**
	 * laufende Nr. des Zeitraums bei Auswertungen (ab 0)
	 * 
	 * @return
	 */
	public int getZeitraumNr() {
		return Format.getIntValue(getField("virt.field.monatseinsatzblatt.zeitraum").getValue());
	}


	/**
	 * Jahr bei Auswertungen
	 * 
	 * @return
	 */
	public int getJahr() {
		return Format.getIntValue(getField("virt.field.monatseinsatzblatt.jahr").getValue());
	}


	/**
	 * Ersten Zeitraum der Auswertung bestimmen
	 * 
	 * @return Zeitraum
	 */
	public Zeitraum getFirstZeitraum() {
		int firstZeitraumNr, firstJahr, zeitraumNr, jahr;
		Zeitraum zeitraum;
		
		if (!moveFirst())
		{
			return null;
		}

		firstZeitraumNr = 12;
		firstJahr = Integer.MAX_VALUE;
		zeitraum = new Zeitraum();

		do
		{
			jahr = getJahr();
			zeitraumNr = getZeitraumNr();

			if (jahr < firstJahr)
			{
				firstJahr = jahr;
				firstZeitraumNr = zeitraumNr;
			}
			else if (jahr == firstJahr)
			{
				firstZeitraumNr = Math.min(firstZeitraumNr, zeitraumNr);
			}
		} while (moveNext());


		zeitraum.setZeitraumNr(firstZeitraumNr);
		zeitraum.setJahr(firstJahr);

		return zeitraum;
	}


	/**
	 * letzten Zeitraum der Auswertung bestimmen
	 * 
	 * @return Zeitraum
	 */
	public Zeitraum getLastZeitraum() {
		int lastZeitraumNr, lastJahr, zeitraumNr, jahr;
		Zeitraum zeitraum;
		
		if (!moveFirst())
		{
			return null;
		}

		lastZeitraumNr = 0;
		lastJahr = Integer.MIN_VALUE;
		zeitraum = new Zeitraum();

		do
		{
			jahr = getJahr();
			zeitraumNr = getZeitraumNr();

			if (jahr > lastJahr)
			{
				lastJahr = jahr;
				lastZeitraumNr = zeitraumNr;
			}
			else if (jahr == lastJahr)
			{
				lastZeitraumNr = Math.max(lastZeitraumNr, zeitraumNr);
			}
		} while (moveNext());


		zeitraum.setZeitraumNr(lastZeitraumNr);
		zeitraum.setJahr(lastJahr);

		return zeitraum;
	}


	/**
	 * Zum Datensatz mit den übergebenen Werten springen. <br>
	 * Wenn er nicht existiert, lege ihn an.
	 * 
	 * @param virtCoProjekt
	 * @param tagDesMonats
	 * @return datensatz vorhanden
	 * @throws Exception 
	 */
	public boolean moveTo(VirtCoProjekt virtCoProjekt, int tagDesMonats) throws Exception {
		return moveTo(virtCoProjekt.getAuftragID(), virtCoProjekt.getAbrufID(), virtCoProjekt.getKostenstelleID(), virtCoProjekt.getBerichtsNrID(),  
				virtCoProjekt.getStundenartID(), tagDesMonats);
	}


	/**
	 * Zum Datensatz mit den übergebenen Werten springen. <br>
	 * Wenn er nicht existiert, lege ihn an.
	 * 
	 * @param auftragID
	 * @param abrufID
	 * @param kostenstelleID
	 * @param tagDesMonats
	 * @param stundenartID 
	 * @return datensatz vorhanden
	 * @throws Exception 
	 */
	private boolean moveTo(int auftragID, int abrufID, int kostenstelleID, int berichtsNrID, int stundenartID, int tagDesMonats) throws Exception {
		boolean isModified;
	
		if (moveFirst())
		{
			do
			{
				if (getAuftragID() != auftragID)
				{
					continue;
				}

				if (getAbrufID() != abrufID)
				{
					continue;
				}

				if (getKostenstelleID() != kostenstelleID)
				{
					continue;
				}

				if (getBerichtsNrID() != berichtsNrID)
				{
					continue;
				}

				if (getStundenartID()!= stundenartID)
				{
					continue;
				}

				if (getTagDesMonats() != tagDesMonats)
				{
					continue;
				}

				return true;
			} while (moveNext());
		}

		// wenn der Datensatz nicht existiert, lege ihne im Editmodus an
		if (!isEditing())
		{
			return false;
		}
		
//		isEditing = isEditing();
		isModified = isModified();
		createNew(auftragID, abrufID, kostenstelleID, berichtsNrID, stundenartID, tagDesMonats);
		
//		if (!isEditing)
//		{
//			commit();
//		}
		
		if (!isModified)
		{
			setModified(false);
		}

		return true;
	}


	/**
	 * Neuen Datensatz erstellen
	 * 
	 * @param auftragID
	 * @param abrufID
	 * @param kostenstelleID
	 * @param stundenartID
	 * @param tagDesMonats
	 * @throws Exception
	 */
	private void createNew(int auftragID, int abrufID, int kostenstelleID, int berichtsNrID, int stundenartID, int tagDesMonats) throws Exception {
		super.createNew();

		setPersonID(m_personID);
		
		setTagDesMonatsDatum(tagDesMonats);
		setDatum(m_datum);
		setWertZeit(0);

		setAuftragID(auftragID);
		
		// Abruf, falls einer ausgewählt wurde
		if (abrufID > 0)
		{
			setAbrufID(abrufID);
		}
		
		// Kostenstelle, falls eine ausgewählt wurde
		if (kostenstelleID > 0)
		{
			setKostenstelleID(kostenstelleID);
		}
		
		// BerichtsNr, falls eine ausgewählt wurde
		if (berichtsNrID > 0)
		{
			setBerichtsNrID(berichtsNrID);
		}
		
		setStundenartID(stundenartID);
		
		setStatusID(CoStatusStundenwertMonatseinsatzblatt.STATUSID_OK);
	}


	/**
	 * Abfragen, ob es ein Dummy-Datensatz ist, da dieser nicht gespeichert wird
	 * 
	 * @see pze.business.objects.AbstractCacheObject#appendPflichtfelderFehler(java.lang.String, java.util.HashSet)
	 */
	@Override
	public String appendPflichtfelderFehler(String felder, HashSet<IField> schonGeprueft) {
		try
		{
			if (isDummyCo())
			{
				return felder;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return super.appendPflichtfelderFehler(felder, schonGeprueft);
	}

	
	/**
	 * Prüft, ob die Tätigkeit für alle Felder eingegeben wurde
	 */
	public String validate() throws Exception{
		int wertZeit, differenz, auftragID, abrufID;
		String taetigkeit, fehler;
		IField fieldWertZeit;
		
		
		if (!moveFirst())
		{
			return null;
		}

		
		m_mapProjektstundenAuftrag = new HashMap<Integer, Projektstunden>();
		m_mapProjektstundenAbruf = new HashMap<Integer, Projektstunden>();
		m_mapProjektMitarbeiterZuordnung = new HashMap<String, CoMitarbeiterProjekt>();

		do
		{
			fieldWertZeit = getFieldWertZeit();
			wertZeit = getWertZeit();
			taetigkeit = getTaetigkeit();

			// Tätigkeit muss eingegeben werden, wenn eine Zeit eingegeben wurde
			if (wertZeit > 0 && (taetigkeit == null || taetigkeit.trim().isEmpty()))
			{
				return "Für alle Projektzeiten muss eine Tätigkeit angegeben werden. "
						+ "Ihre Eingabe kann nicht gespeichert werden.";
			}
			
			// wenn der Stundenwert geändert wurde muss geprüft werden, ob genügend Stunden auf dem Projekt zur Verfügung stehen
			if (fieldWertZeit.getState() == IBusinessObject.statusChanged)
			{
				differenz = wertZeit - Format.getIntValue(fieldWertZeit.getOriginalValue());
				if (differenz == 0)
				{
					continue;
				}
				
				// prüfen, ob das aktuelle Projekt ein Abruf ist
				auftragID = getAuftragID();
				abrufID = getAbrufID();
				if (abrufID > 0)
				{
					fehler = updateAktStundenAbruf(auftragID, abrufID, differenz);
					if (fehler != null)
					{
						return fehler;
					}
				}
				// sonst ist es ein Auftrag
				else
				{
					fehler = updateAktStundenAuftrag(auftragID, differenz);
					if (fehler != null)
					{
						return fehler;
					}
				}
			}
			
		} while (moveNext());

		
		// Stunden der Projekte prüfen
		fehler = checkAuftragsstunden();
		if (fehler != null)
		{
			return fehler;
		}
		
		fehler = checkAbrufstunden();
		if (fehler != null)
		{
			return fehler;
		}
		
		fehler = checkStundenMitarbeiterZuordnung();
		if (fehler != null)
		{
			return fehler;
		}
		
		return null;
	}


	/**
	 * Temporären Stand der Projektstunden für einen Auftrag aktualisieren
	 * 
	 * @param auftragID
	 * @param differenz
	 * @return Fehlermeldung oder null
	 * @throws Exception
	 */
	private String updateAktStundenAuftrag(int auftragID, int differenz) throws Exception {
		String fehler;
		CoAuftrag coAuftrag;
		Projektstunden projektstunden;
		
		coAuftrag = null;
		coAuftrag = new CoAuftrag();
		coAuftrag.loadByID(auftragID);
		
		if (!m_mapProjektstundenAuftrag.containsKey(auftragID))
		{
			if (coAuftrag.getStatusID() != CoStatusProjekt.STATUSID_LAUFEND)
			{
				return "Der Auftrag " + coAuftrag.getBezeichnung() + " ist für Buchungen gesperrt.";
			}

			projektstunden = new Projektstunden(coAuftrag);
			projektstunden.setAktStunden(loadStundenAuftrag(auftragID));
//			projektstunden.setProjektID(auftragID);
//			projektstunden.setSollstunden(coAuftrag.getSollstunden());
//			projektstunden.setUeberbuchung(coAuftrag.getUeberbuchung());
			
			m_mapProjektstundenAuftrag.put(auftragID, projektstunden);
		}
		
		projektstunden = m_mapProjektstundenAuftrag.get(auftragID);
		projektstunden.setAktStunden(projektstunden.getAktStunden() + differenz);
		
		// Prüfung der Zuordnung von Mitarbeitern zum Projekt
		fehler = checkMitarbeiterZuordnung(coAuftrag, differenz);
		if (fehler != null)
		{
			return fehler;
		}
		
		return null;
	}
	

	/**
	 * Temporären Stand der Projektstunden für einen Abruf aktualisieren
	 * 
	 * @param auftragID
	 * @param abrufID
	 * @param differenz
	 * @return Fehlermeldung oder null
	 * @throws Exception
	 */
	private String updateAktStundenAbruf(int auftragID, int abrufID, int differenz) throws Exception {
		String fehler;
		CoAbruf coAbruf;
		Projektstunden projektstunden;
		
		coAbruf = null;
		coAbruf = new CoAbruf();
		coAbruf.loadByID(abrufID);
		
		if (!m_mapProjektstundenAbruf.containsKey(abrufID))
		{
			if (coAbruf.getStatusID() != CoStatusProjekt.STATUSID_LAUFEND)
			{
				return "Der Abruf " + coAbruf.getBezeichnung() + " ist für Buchungen gesperrt.";
			}
			
			projektstunden = new Projektstunden(coAbruf);
			projektstunden.setAktStunden(loadStundenAbruf(abrufID));
//			projektstunden.setProjektID(abrufID);
//			projektstunden.setSollstunden(coAbruf.getSollstunden());
//			projektstunden.setUeberbuchung(coAbruf.getUeberbuchung());
			
			m_mapProjektstundenAbruf.put(abrufID, projektstunden);
		}
		
		projektstunden = m_mapProjektstundenAbruf.get(abrufID);
		projektstunden.setAktStunden(projektstunden.getAktStunden() + differenz);
		
		// Prüfung der Zuordnung von Mitarbeitern zum Projekt
		fehler = checkMitarbeiterZuordnung(coAbruf, differenz);
		if (fehler != null)
		{
			return fehler;
		}

		return null;
	}
	

	private String checkMitarbeiterZuordnung(CoProjekt coProjekt, int differenz) throws Exception {
		int personID;
		String fehler, key;
		CoMitarbeiterProjekt coMitarbeiterProjekt;

		// Zuordnung der Mitarbeiter zu dem Projekt laden
		key = coProjekt.getKey();
		if (!m_mapProjektMitarbeiterZuordnung.containsKey(key))
		{
			coMitarbeiterProjekt = new CoMitarbeiterProjekt(coProjekt);
			coMitarbeiterProjekt.loadByProjekt(false);
			
			m_mapProjektMitarbeiterZuordnung.put(key, coMitarbeiterProjekt);
		}
		coMitarbeiterProjekt = m_mapProjektMitarbeiterZuordnung.get(key);
		
		// prüfen ob der Mitarbeiter zugeordnet ist
		if (coMitarbeiterProjekt.hasNoRows())
		{
			return null;
		}
		
		personID = getPersonID();
		fehler = coMitarbeiterProjekt.check(personID, getDatum(), differenz);
		if (fehler != null)
		{
			return fehler;
		}
		
		return null;

	}


	/**
	 * Stunden der Aufträge prüfen
	 * 
	 * @return Fehlermeldung oder null
	 */
	private String checkAuftragsstunden() {
		int iKey, anzKeys, budgetUeberschreitung;
		Projektstunden projektstunden;
		Object[] keys;
		
		keys = m_mapProjektstundenAuftrag.keySet().toArray();
		anzKeys = keys.length;
		for (iKey=0; iKey<anzKeys; ++iKey)
		{
			projektstunden = m_mapProjektstundenAuftrag.get(keys[iKey]);
			budgetUeberschreitung = projektstunden.getBudgetUeberschreitung();
			if (budgetUeberschreitung > 0)
			{
				return "Auf den Auftrag " + projektstunden.getProjektbezeichnung() + " können nicht alle Stunden gebucht werden.<br>"
						+ "Das Stundenbudget ist aktuell um " + Format.getZeitAsText(budgetUeberschreitung) + " Stunden überschritten.";
			}
		}
		
		return null;
	}


	/**
	 * Stunden der Abrufe prüfen
	 * 
	 * @return Fehlermeldung oder null
	 */
	private String checkAbrufstunden() {
		int iKey, anzKeys, budgetUeberschreitung;
		Projektstunden projektstunden;
		Object[] keys;
		
		keys = m_mapProjektstundenAbruf.keySet().toArray();
		anzKeys = keys.length;
		for (iKey=0; iKey<anzKeys; ++iKey)
		{
			projektstunden = m_mapProjektstundenAbruf.get(keys[iKey]);
			budgetUeberschreitung = projektstunden.getBudgetUeberschreitung();
			if (budgetUeberschreitung > 0)
			{
				return "Auf den Abruf " + projektstunden.getProjektbezeichnung() + " können nicht alle Stunden gebucht werden.<br>"
						+ "Das Stundenbudget ist aktuell um " + Format.getZeitAsText(budgetUeberschreitung) + " Stunden überschritten.";
			}
		}
		
		return null;
	}


	/**
	 * Stunden der zugeordneten Projekte prüfen
	 * 
	 * @return Fehlermeldung oder null
	 */
	private String checkStundenMitarbeiterZuordnung() {
		int iKey, anzKeys, budgetUeberschreitung;
		CoMitarbeiterProjekt coMitarbeiterProjekt;
		Object[] keys;
		
		keys = m_mapProjektMitarbeiterZuordnung.keySet().toArray();
		anzKeys = keys.length;
		for (iKey=0; iKey<anzKeys; ++iKey)
		{
			coMitarbeiterProjekt = m_mapProjektMitarbeiterZuordnung.get(keys[iKey]);
			budgetUeberschreitung = coMitarbeiterProjekt.getBudgetUeberschreitung(getPersonID());
			if (budgetUeberschreitung > 0)
			{
				return "Auf das Projekt " + coMitarbeiterProjekt.getCoProjekt().getProjektNr() + " können nicht alle Stunden gebucht werden.<br>"
						+ "Das Ihnen zugeordnete Stundenbudget ist aktuell um " + Format.getZeitAsText(budgetUeberschreitung) 
						+ " Stunden überschritten.";
			}
		}
		
		return null;
	}


	/**
	 * Abfragen, ob es ein Dummy-Datensatz ist, da dieser nicht gespeichert wird
	 * 
	 * @see framework.business.cacheobject.CacheObject#save()
	 */
	public void save() throws Exception{
		if (!isDummyCo() && isEditing())
		{
			try
			{
				// prüfen ob identische Eintrag schon existiert
				checkRowVorhanden();
				
				super.save();
			} 
			catch (Exception e)
			{
				Messages.showErrorMessage("Fehler beim Speichern", "Die Daten konnten nicht gespeichert werden. "
						+ "Ggf. wurden zwischenzeit bereits Daten eingegeben oder ein unerwarteter Fehler ist aufgetreten.<br>"
						+ "Bitte das Formular schließen und erneut öffnen.");
				e.printStackTrace();
				throw e;
			}
		}
	}


	/**
	 * prüfen ob ein identischer Eintrag schon existiert, z. B. durch eine weitere PZE-Instanz
	 * 
	 * @throws Exception
	 */
	private void checkRowVorhanden() throws Exception {
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		coMonatseinsatzblatt = new CoMonatseinsatzblatt();

		// alle Datensätze durchlaufen
		if (moveFirst())
		{
			do
			{
				// prüfe neue Datensätze
				if (isNew())
				{
					// Datensatz für das Projekt für den Tag laden, es darf eigentlich keinen geben
					coMonatseinsatzblatt.load(getPersonID(), getDatum(), getAuftragID(), getAbrufID(), getKostenstelleID(), getBerichtsNrID(), getStundenartID());
					
					if (coMonatseinsatzblatt.hasRows())
					{
						Messages.showErrorMessage("Fehler beim Speichern", "Die Daten konnten nicht gespeichert werden. "
								+ "Ggf. ist das Formular bereits in einer anderen PZE-Instanz geöffnet "
								+ "oder ein unerwarteter Fehler ist aufgetreten.<br>"
								+ "Bitte das Formular schließen und erneut öffnen.");
						throw new IllegalAccessException();
					}
				}
				
			} while (moveNext());
		}
	}

	
	/**
	 * Abfragen, ob es ein Dummy-Datensatz ist, da dieser nicht gespeichert wird
	 * 
	 */
	public boolean isDummyCo() throws Exception{
		return getRowCount() > 0 && getID() == 0;
	}


	/**
	 * Gibt die auf den Abruf in dem Monat des übergebenen Datums gebuchte Zeit zurück
	 * 
	 * @param abrufID
	 * @param datum
	 * @return
	 * @throws Exception
	 */
	public static int getWertZeitMonat(int abrufID, Date datum) throws Exception {
		String sql;
		GregorianCalendar gregCalendar;
		
		gregCalendar = Format.getGregorianCalendar(datum);

		sql = "SELECT SUM(WertZeit) FROM " + TABLE_NAME + " WHERE AbrufID=" + abrufID 
				+ " AND YEAR(Datum)=" + gregCalendar.get(Calendar.YEAR)
				+ " AND MONTH(Datum)=" + (gregCalendar.get(Calendar.MONTH)+1);

		return Format.getIntValue(Application.getLoaderBase().executeScalar(sql));
	}

}
