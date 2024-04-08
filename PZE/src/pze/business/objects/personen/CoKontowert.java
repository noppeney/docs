package pze.business.objects.personen;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import framework.Application;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import pze.business.FeiertagGenerator;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoBrueckentag;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.CoPausenmodell;
import pze.business.objects.CoVerletzerliste;
import pze.business.objects.CoZeitmodell;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungAuszahlung;
import pze.business.objects.auswertung.CoAuswertungKontowerte;
import pze.business.objects.auswertung.CoAuswertungKontowerteZeitraum;
import pze.business.objects.reftables.CoMeldungVerletzerliste;
import pze.business.objects.reftables.CoStatusKontowert;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusAuszahlung;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.personen.CoPosition;
import pze.business.objects.reftables.personen.CoStandort;
import pze.business.objects.reftables.personen.CoStatusAktivInaktiv;
import pze.business.objects.reftables.personen.CoStatusInternExtern;

/**
 * CacheObject für Kontowerte
 * 
 * @author Lisiecki
 *
 */
public class CoKontowert extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblkontowert";
	
	private static final int MAX_ARBEITSUNTERBRECHUNG = 30;
	private static final int MAX_AUSZAHLUNG_STUNDEN = 30 * 60;

	private CoPerson m_coPerson;
	
	private CoVerletzerliste m_coVerletzerliste;
	
	private boolean m_isKrankLetzteBuchung;
	
	private int m_anzWerktageBerechnet;
	private int m_summeArbeitszeitBerechnet;
	
//	private int m_wertOfaGeplant;
	


	/**
	 * Kontruktor
	 */
	public CoKontowert() {
		super("table." + TABLE_NAME);
		
		addField("virt.field.kontowert.wertplusstunden");
		addField("virt.field.kontowert.wertplusstundenprojekt");
		addField("virt.field.kontowert.wertplusstundenreise");
		addField("virt.field.kontowert.wertminusstunden");

		addField("virt.field.kontowert.wertbezahltearbeitszeitmonat");
		addField("virt.field.kontowert.wertsollarbeitszeitmonat");
		addField("virt.field.kontowert.wertueberstundenmonat");

		addField("virt.field.kontowert.wertbezahltearbeitszeitwoche");
		addField("virt.field.kontowert.wertsollarbeitszeitwoche");
		addField("virt.field.kontowert.wertueberstundenwoche");
		
		addField("virt.field.kontowert.wertplusstundenmonat");
		addField("virt.field.kontowert.wertplusstundenprojektmonat");
		addField("virt.field.kontowert.wertplusstundenreisemonat");
		addField("virt.field.kontowert.wertminusstundenmonat");
		
		addField("virt.field.kontowert.wertauszahlbareueberstunden");
		addField("virt.field.kontowert.wertauszahlbareueberstundenprojekt");
		addField("virt.field.kontowert.wertauszahlbareueberstundenreise");
		addField("field.tblkontowert.wertauszahlungueberstunden");
		addField("field.tblkontowert.wertauszahlungueberstundenprojekt");
		addField("field.tblkontowert.wertauszahlungueberstundenreise");
	}
	

	/**
	 * CO für die Person und den Tag laden.<br>
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public void load(int personID, Date datum) throws Exception { // TODO Verletzerliste prüfen, Verletzerliste wird wohl meistens nicht benötigt
		load(personID, datum, false, true);
	}
	

	/**
	 * CO für die Person und den Tag laden, nur einfache Kontowerte.<br>
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public void loadEinfach(int personID, Date datum) throws Exception { // TODO einfach Verletzerliste prüfen, Verletzerliste wird wohl meistens nicht benötigt
		load(personID, datum, false, false);
	}
	

	/**
	 * CO für die Person und den Monat laden.<br>
	 * 
	 * @param personID
	 * @param datum
	 * @param detailliert Daten detailliert mit Summen und Plus-/Minusstunden laden
	 * @throws Exception
	 */
	public void loadMonat(int personID, Date datum, boolean detailliert) throws Exception {
		load(personID, datum, true, detailliert);
	}
	

	/**
	 * CO für die Person und den Tag/Monat laden.<br>
	 * 
	 * @param personID
	 * @param datum
	 * @param monatLaden Daten für den gesamten Monat oder den Tag laden
	 * @param detailliert Daten detailliert mit Summen und Plus-/Minusstunden laden
	 * @throws Exception
	 */
	public void load(int personID, Date datum, boolean monatLaden, boolean detailliert) throws Exception {
		load(personID, datum, monatLaden, detailliert, true);
	}
	

	/**
	 * CO für die Person und den Tag/Monat laden.<br>
	 * 
	 * @param personID
	 * @param datum
	 * @param monatLaden Daten für den gesamten Monat oder den Tag laden
	 * @param detailliert Daten detailliert mit Summen und Plus-/Minusstunden laden
	 * @throws Exception
	 */
	public void load(int personID, Date datum, boolean monatLaden, boolean detailliert, boolean verletzerliste) throws Exception {
		int jahr, monat, tag;
		String sql;
		GregorianCalendar gregDatum;
		
		loadCoPerson(personID);
		
		
		gregDatum = Format.getGregorianCalendar(datum);
		jahr = gregDatum.get(Calendar.YEAR);
		monat = (gregDatum.get(Calendar.MONTH) + 1);
		tag = gregDatum.get(Calendar.DAY_OF_MONTH);
		
		sql = "SELECT * FROM " + getTableName() 
		+ (detailliert ?
				" OUTER APPLY funPlusMinusStunden(" + personID + ", YEAR(Datum), MONTH(Datum), DAY(Datum))"
				+ " OUTER APPLY funSumme(" + personID + ", YEAR(Datum), MONTH(Datum), DAY(Datum))" 
				+ " OUTER APPLY funSummeWoche(" + personID + ", YEAR(Datum), DATEPART(ww, Datum), DATEPART(dw, Datum))" 
				: "")
		+ " WHERE PersonID=" + personID + " AND YEAR(Datum)=" + jahr + ""
		+ " AND MONTH(Datum)=" + monat 
		+ (monatLaden ? "" : " AND DAY(Datum)=" + tag)
		+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
		

		// Verletzerliste laden
		if (verletzerliste)
		{
			m_coVerletzerliste = new CoVerletzerliste();
			if (getRowCount() > 0)
			{
				m_coVerletzerliste.load(m_coPerson, getDatum());

			}
		}
	}


	/**
	 * CO für die Person ab dem übergebenen Datum laden.<br>
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	private void loadAbDatum(int personID, Date datum) throws Exception {
		String where;
		
		loadCoPerson(personID);
		
		where = " PersonID=" + personID + " AND Datum > '" + Format.getStringForDB(datum) + "' AND StatusID=" + CoStatusKontowert.STATUSID_OK;

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}
	

	/**
	 * CO für die Person und den Zeitraum laden
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public void load(int personID, Date datumVon, Date datumBis) throws Exception {
		String where;
		
//		loadCoPerson(personID);
		
		where = "PersonID=" + personID 
				+ " AND DATUM >= '" + Format.getStringForDB(datumVon) + "' "
				+ " AND DATUM < '" + Format.getStringForDB(Format.getDateVerschoben(Format.getDate0Uhr(datumBis), 1)) + "'";

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}
	

	/**
	 * Eintrag mit dem spätesten Datum für die Person laden
	 * 
	 * @param personID
	 * @throws Exception
	 */
	public void loadLastEintrag(int personID) throws Exception {
		String where;
		
		loadCoPerson(personID);
		
		where = " PersonID=" + personID + " AND Datum = (SELECT MAX(Datum) FROM " + TABLE_NAME + " WHERE PersonID=" + personID + ")";

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}
	
	
	/**
	 * Summe der Arbeitszeit pro Person im aktuellen und vorherigen Monat laden.<br>
	 * Nur Zeiten in der Vergangenheit werden berücksichtigt.
	 * 
	 * @throws Exception
	 */
	public void loadSummeArbeitszeit() throws Exception {
		String sql;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(null);
		
		sql = " SELECT PersonID, SUM(WertBezahlteArbeitszeit-ISNULL(WertKrank, 0)) AS WertBezahlteArbeitszeit FROM tblKontowert WHERE " + 
				"ISNULL(AnzahlUrlaub, 0)+" + 
				"ISNULL(AnzahlSonderurlaub, 0)+" + 
				"ISNULL(AnzahlFA, 0)+" + 
				"ISNULL(AnzahlElternzeit, 0)+" + 
				"ISNULL(AnzahlUnbezahlteFreistellung, 0)+" + 
				"ISNULL(AnzahlKrank, 0)+" + 
				"ISNULL(AnzahlKrankOhneLfz, 0)+" + 
				"ISNULL(AnzahlBezFreistellung, 0)=0" + 
//				"AND WertBezahlteArbeitszeit IS NOT NULL" + 
//				" AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)>" + (gregDatum.get(Calendar.MONTH)-1) + 
				" AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)>" + (gregDatum.get(Calendar.MONTH)-1) + 
				" AND Datum<'" + Format.getString(Format.getDate0Uhr(new Date())) + "' " + 
				" Group By PersonID";

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	
	
	static long time;
	
	
	/**
	 * CO mit den Auszahlungen der Person für das aktuelle und das letzte Jahr laden.<br>
	 * 
	 * @param personID
	 * @throws Exception
	 */
	public void loadAuszahlungen(int personID) throws Exception {
		String sql;
		time = System.currentTimeMillis();
		
		// SQL-Abfrage zusammensetzen
		sql = getSqlAuszahlung("PersonID=" + personID);
		sql = sql.replace("WHERE", "WHERE YEAR(Datum) > (YEAR(GETDATE())-2) AND ");

		emptyCache();
		Application.getLoaderBase().load(this, sql);
		System.out.println("LOAD Auszahlung: " + (System.currentTimeMillis() - time));
	}
	

	/**
	 * Datum der letzten Auszahlung bestimmen ("keine Auszahlung wir nicht berücksichtigt")
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	public static Date getDatumLetzteAuszahlung(int personID) throws Exception {
		String sql;
		Date datumLetzteAuszahlung;
		time = System.currentTimeMillis();
		
		// SQL-Abfrage zusammensetzen
		sql = getSqlAuszahlung("PersonID=" + personID);
		
		sql = sql.replace("SELECT *", "SELECT MAX(Datum)");
		sql = sql.replace("WHERE", "WHERE StatusIdAuszahlung IS NOT NULL AND StatusIdAuszahlung <> " + CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG + " AND ");
		sql = sql.substring(0, sql.indexOf("ORDER")-1);

		datumLetzteAuszahlung = (Date) Application.getLoaderBase().executeScalarArray(sql)[0];
		if (datumLetzteAuszahlung != null)
		{
			datumLetzteAuszahlung = Format.getDate0Uhr(Format.getDateVerschoben(datumLetzteAuszahlung, 1));
		}
		System.out.println("LOAD Auszahlung neu: " + (System.currentTimeMillis() - time));
		
		return datumLetzteAuszahlung;
	}


//	/**
//	 * Datum der letzten Auszahlung bestimmen ("keine Auszahlung wir nicht berücksichtigt")
//	 * 
//	 * @param personID
//	 * @return
//	 * @throws Exception
//	 */
//	public static Date getDatumLetzteAuszahlung(int personID) throws Exception {
//		int statusIDAuszahlung;
//		Date datumLetzteAuszahlung;
//		CoKontowert coKontowert;
//		
//		coKontowert = new CoKontowert();
//		coKontowert.loadAuszahlungen(personID);
//		datumLetzteAuszahlung = null;
//		
//		if (coKontowert.moveFirst())
//		{
//			do
//			{
//				statusIDAuszahlung = coKontowert.getStatusIDAuszahlung();
//
//				if (statusIDAuszahlung != 0 && statusIDAuszahlung != CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG)
//				{
//					// 0 Uhr des Folgetages wegen Uhrzeit der Buchungen
//					datumLetzteAuszahlung = Format.getDate0Uhr(Format.getDateVerschoben(coKontowert.getDatum(), 1));
//					break;
//				}
//			} while(coKontowert.moveNext());
//		}
//		
//		return datumLetzteAuszahlung;
//	}


	/**
	 * CO in Abhängigkeit der gewählten Parameter laden
	 * 
	 * @param coAuswertungKontowerte
	 * @throws Exception
	 */
	public void load(CoAuswertungKontowerte coAuswertungKontowerte) throws Exception {
		String whereDatum, wherePerson, wherePosition, where;
		Date datumGeplantBis;
		
		
		// SQL-Abfrage zusammensetzen
		whereDatum = coAuswertungKontowerte.getWhereDatum();
		wherePerson = coAuswertungKontowerte.getWherePerson();
		wherePosition = coAuswertungKontowerte.getWherePosition();
		
		// Sicherheitsabfrage: keine Daten laden, wenn kein (Start-)Datum angegeben ist (sonst wird alles geladen -> Laufzeitproblem)
		if (whereDatum == null || coAuswertungKontowerte.getDatumVon() == null)
		{
			return;
		}
		where = (whereDatum != null ? " AND (" + whereDatum + ")" : "");
		where += (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}

		// ggf. Bezugsdatum für Resturlaub bis berücksichtigen
		datumGeplantBis = coAuswertungKontowerte.getDatumGeplantBis();

		
		// Daten laden
		loadMitResturlaub(where, datumGeplantBis, false);
	}


	/**
	 * CO mit Resturlaub zum übergebenen Datum laden
	 * 
	 * @param datumGeplantBis 
	 * @param where 
	 * @param refDatumHeute 
	 * @throws Exception
	 */
	public void loadMitResturlaub(String where, Date datumGeplantBis, boolean refDatumHeute) throws Exception {
		int resturlaub, resturlaubGenehmigt, resturlaubRest, resturlaubVerplant, resturlaubOffen;
		String sql;
		
		// Felder hinzufügen (kann nicht immer gemacht werden, weil sonst im Personenreiter der Resturlaub eingetragen und der Datensatz als geändert markiert wird)
		addField("virt.field.kontowert.resturlaubgenehmigt");
		addField("virt.field.kontowert.rest");
		addField("virt.field.kontowert.resturlaubverplant");
		addField("virt.field.kontowert.resturlauboffen");

		
		// Daten laden
		sql = "SELECT * FROM " + getTableName() + " k JOIN tblPerson p ON (k.PersonID=p.ID) OUTER APPLY funSumme(0, 0, 0, 0)"
				+ (where.trim().isEmpty() ? "" : " WHERE " + where )
				+ " ORDER BY Nachname, Vorname, Datum";

		emptyCache();
		Application.getLoaderBase().load(this, sql);

		
		// genehmigten und geplanten Resturlaub berechnen
		if (moveFirst())
		{
			do
			{
				// Personendaten laden
				loadCoPerson(getPersonID());
				
				// Resturlaub
				resturlaub = getResturlaub();
				resturlaubGenehmigt = getResturlaubGenehmigt(datumGeplantBis);
				resturlaubRest = resturlaub - resturlaubGenehmigt;
				resturlaubVerplant = CoBuchung.getAnzahlGeplantenUrlaub(getPersonID(), refDatumHeute ? new Date() : getDatum(), datumGeplantBis);
				resturlaubOffen = resturlaubRest - resturlaubVerplant;
						
				// genehmigten Resturlaub
				getFieldResturlaubGenehmigt().setValue(resturlaubGenehmigt);
				getFieldRest().setValue(resturlaubRest);
				
				// noch nicht genehmigter, aber geplanter Urlaub
				getFieldResturlaubVerplant().setValue(resturlaubVerplant);
				
				// offen/nicht verplant
				getFieldResturlaubOffen().setValue(resturlaubOffen);

			} while (moveNext());
		}
	}


	/**
	 * Bereits verplanten Resturlaub aus dem Intranet bestimmen
	 * 
	 * @param datumGeplantBis 
	 * @return
	 * @throws Exception
	 */
//	protected int getResturlaubVerplant(Date datumGeplantBis) throws Exception {
//		int intraPersonID, resturlaubVerplant;
//		String datumAsString, sql;
//		
//		
//		// DB-Connection zum Intranet öffnen
//		PZEStartupAdapter.openIntranetDbConnection(); 
//		
//
//		// ID der Person im Intranet bestimmen
//		sql = "SELECT intra_id FROM pze_intranet_usermap WHERE pze_id=" + getPersonID();
//		intraPersonID = Format.getIntValue(Application.getLoaderBase().executeScalar(sql));
//			
//		
//		// Anzahl geplanter Tage aus der Urlaubsplanung, an denen kein genehmigter Urlaub in der Anwesenheit eingetragen ist	
//		datumAsString = Format.getStringForDB(getDatum());
//		sql = "SELECT COUNT(tag) FROM "
//				+ " ((SELECT tag FROM urlaubsplanung WHERE nutzer = " + intraPersonID
//				+ " AND tag > '" + datumAsString + "' "
//				// Jahresende oder ein alternatives Bezugsdatum
//				+ (datumAsString == null ? " AND YEAR(tag) = " + getGregDatum().get(Calendar.YEAR) + "" 
//						:  " AND tag < '" + Format.getDateVerschoben(datumGeplantBis, 1) + "'")
//				+ " AND eintrag LIKE 'Urlaub%') Planung)"
//				+ " WHERE tag NOT IN "
//				+ " (SELECT tag FROM anwesenheit WHERE nutzer = " + intraPersonID + " AND tag > '" + datumAsString 
//				+ "' AND (eintrag LIKE 'Urlaub%' OR eintrag LIKE 'FA%'))";
//		
//		// SQL-Statement ausführen
//		resturlaubVerplant = Format.getIntValue(Application.getLoaderBase().executeScalar(sql));
//
//		// DB-Connection zum Intranet schließen
//		PZEStartupAdapter.openDefaultDbConnection();
//		
//		return resturlaubVerplant;
//	}
	

	/**
	 * CO in Abhängigkeit der gewählten Parameter laden
	 * 
	 * @param coAuswertungKontowerteZeitraum
	 * @throws Exception 
	 */
	public void load(CoAuswertungKontowerteZeitraum coAuswertungKontowerteZeitraum) throws Exception {
		String sql, wherePerson, wherePosition, where;
		
		addField("field.tblkontowert.datumvon");
		addField("field.tblkontowert.datumbis");
		addField("field.tblkontowert.anzahlarbeitstage");
		addField("virt.field.kontowert.aenderunggleitzeitkonto");

		// SQL-Abfrage zusammensetzen
		wherePerson = coAuswertungKontowerteZeitraum.getWherePerson();
		wherePosition = coAuswertungKontowerteZeitraum.getWherePosition();
		where = coAuswertungKontowerteZeitraum.getWhereDatumBisHeute() + (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}

		sql = "SELECT PersonID, MIN(Datum) AS DatumVon, MAX(Datum) AS DatumBis, "
				+ " COUNT(WertSollArbeitszeit) AS AnzahlArbeitstage,"
				+ " SUM(WertSollArbeitszeit) AS WertSollArbeitszeit,"
				+ " SUM(WertBezahlteArbeitszeit) AS WertBezahlteArbeitszeit, "
				+ " SUM(WertBezahlteArbeitszeit) - SUM(WertSollArbeitszeit) AS WertUeberstunden,"
				+ " SUM(WertUeberstunden) AS AenderungGleitzeitkonto,"
				
				+ " SUM(WertPlusstunden) AS WertPlusstunden,"
				+ " SUM(WertPlusstundenProjekt) AS WertPlusstundenProjekt, "
				+ " SUM(WertPlusstundenReise) AS WertPlusstundenReise,"
				+ " SUM(WertMinusstunden) AS WertMinusstunden,"

				+ " SUM(WertAuszahlungUeberstundenProjekt) AS WertAuszahlungUeberstundenProjekt,"
				+ " SUM(WertAuszahlungUeberstundenReise) AS WertAuszahlungUeberstundenReise,"

				+ " SUM(WertAnwesend) AS WertAnwesend,"
				+ " SUM(WertDienstreise) AS WertDienstreise,"
				+ " SUM(WertDienstgang) AS WertDienstgang,"
				+ " SUM(WertReisezeit) AS WertReisezeit,"
				+ " SUM(WertVorlesung) AS WertVorlesung,"
				+ " SUM(WertBerufsschule) AS WertBerufsschule,"
				
				+ " SUM(WertPause) AS WertPause,"
				+ " SUM(WertPausenAenderung) AS WertPausenAenderung,"
				+ " SUM(WertArbeitsunterbrechung) AS WertArbeitsunterbrechung,"
				+ " SUM(WertPrivateUnterbrechung) AS WertPrivateUnterbrechung,"
				+ " SUM(WertKrank) AS WertKrank,"
				
				+ " SUM(AnzahlUrlaub) AS AnzahlUrlaub,"
				+ " SUM(AnzahlSonderurlaub) AS AnzahlSonderurlaub,"
				+ " SUM(AnzahlFA) AS AnzahlFA,"
				+ " SUM(AnzahlElternzeit) AS AnzahlElternzeit,"
				+ " SUM(AnzahlKrank) AS AnzahlKrank,"
				+ " SUM(AnzahlKrankOhneLfz) AS AnzahlKrankOhneLfz"
				+ " FROM " + getTableName() + " k JOIN tblPerson p ON (k.PersonID=p.ID) "
				+ " OUTER APPLY funPlusMinusStunden(PersonID, YEAR(Datum), MONTH(Datum), DAY(Datum))"
				+ " OUTER APPLY funSumme(PersonID, YEAR(Datum), MONTH(Datum), DAY(Datum))"
				+ (where.trim().isEmpty() ? "" : " WHERE " + where )
				+ " GROUP BY PersonID, Nachname, Vorname "
				+ " ORDER BY Nachname, Vorname, DatumVon";

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * CO in Abhängigkeit der gewählten Parameter laden
	 * 
	 * @param coAuswertungAuszahlung
	 * @throws Exception
	 */
	public void load(CoAuswertungAuszahlung coAuswertungAuszahlung) throws Exception {
		String sql, whereDatum, wherePerson, wherePosition, whereStatusAuszahlung, where;
		time = System.currentTimeMillis();


		// Einschränkungen laden
		whereDatum = coAuswertungAuszahlung.getWhereDatum();
		wherePerson = coAuswertungAuszahlung.getWherePerson();
		wherePosition = coAuswertungAuszahlung.getWherePosition();
		whereStatusAuszahlung = coAuswertungAuszahlung.getWhereStatusAuszahlung();
		
		where = (whereDatum != null ? " AND (" + whereDatum + ")" : "");
		where += (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");
		where += (whereStatusAuszahlung != null ? whereStatusAuszahlung : "");

		// SQL-Abfrage zusammensetzen
		sql = getSqlAuszahlung(where);

		emptyCache();
		Application.getLoaderBase().load(this, sql);
		System.out.println("LOAD Auszahlung: " + (System.currentTimeMillis() - time));
	}


	public void loadAnAbwesenheit(CoAuswertung coAuswertungAnAbwesenheit) throws Exception {
		String sql, whereDatum, wherePerson, wherePosition, where;
		
		addField("virt.field.tblkontowert.tag");
		addField("virt.field.tblkontowert.monat");

		// SQL-Abfrage zusammensetzen
		whereDatum = coAuswertungAnAbwesenheit.getWhereDatum();
		wherePerson = coAuswertungAnAbwesenheit.getWherePerson();
		wherePosition = coAuswertungAnAbwesenheit.getWherePosition();
		where = "";
		where += (whereDatum != null ? " AND (" + whereDatum + ")" : "");
		where += (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");
		where += " AND (EndePze IS NULL OR Datum < CAST(DATEADD(DAY, 1, EndePze) AS DATE))";

		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}
		
		sql = "SELECT k.*, DAY(Datum) AS TAG, MONTH(Datum) AS Monat, YEAR(Datum) AS Jahr "
				+ " FROM " + getTableName() + " k JOIN tblPerson p ON (k.PersonID=p.ID) "//OUTER APPLY funSumme(0, 0, 0, 0)"
				+ (where.trim().isEmpty() ? "" : " WHERE " + where )
				+ " ORDER BY Jahr, Monat, Nachname, Vorname, Tag";
//		+ " ORDER BY Jahr, Monat, Tag, Nachname, Vorname";

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Alle Personen mit Sollarbeitszeit=0 (Teilzeitkräfte mit freiem Tag) laden<br>
	 * 
	 * @param gregDatum
	 * @throws Exception
	 */
	public void loadFrei(GregorianCalendar gregDatum) throws Exception {
		loadFrei(0, gregDatum);
	}
	
	
	/**
	 * Alle Personen mit Sollarbeitszeit=0 (Teilzeitkräfte mit freiem Tag) laden<br>
	 * 
	 * @param personID 
	 * @param gregDatum
	 * @throws Exception
	 */
	public void loadFrei(int personID, GregorianCalendar gregDatum) throws Exception {
		int jahr, monat, tag;
		String sql;
		
		jahr = gregDatum.get(Calendar.YEAR);
		monat = (gregDatum.get(Calendar.MONTH) + 1);
		tag = gregDatum.get(Calendar.DAY_OF_MONTH);
		
		// alle Personen mit Sollarbeitszeit=0 laden, aktiv, intern
		sql = "SELECT * FROM " + getTableName() 
		+ " WHERE "
		+ (personID != 0 ? " PersonID=" + personID + " AND " : "")
		+ "PersonID IN (SELECT ID FROM tblPerson WHERE StatusAktivInaktivID=" + CoStatusAktivInaktiv.STATUSID_AKTIV + ""
		+ " AND StatusInternExternID=" + CoStatusInternExtern.STATUSID_INTERN + ")"
		+ " AND YEAR(Datum)=" + jahr + " AND MONTH(Datum)=" + monat + " AND DAY(Datum)=" + tag
		+ " AND WertSollarbeitszeit=0 "
		+ " AND ISNULL(AnzahlKrank, 0)=0  AND ISNULL(AnzahlKrankOhneLfz, 0)=0 AND ISNULL(AnzahlElternzeit, 0)=0 " // Krank und Elternzeit rausfiltern
		+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * SQL-Statement mit allen Auszahlungen
	 * 
	 * @param where zusätzlich zu berücksichtigender WHERE-Teil der Abfrage, z. B. für Personen oder Zeitraum
	 * @return
	 */
	private static String getSqlAuszahlung(String where) {
		String sql;

		// Auszahlung sind immer am letzten Tag des Monats oder Auszahlung explizit eingetragen
		
		// Monatsende nur für vergangene Monate anzeigen
		where += " AND (YEAR(Datum) < YEAR(GETDATE()) OR (YEAR(Datum) = YEAR(GETDATE()) AND DATEPART(DAYOFYEAR, Datum) < DATEPART(DAYOFYEAR, GETDATE()))) "
				// 8 weil erst ab Einführung der Regelung, im September 2018 Stand Gleitzeitkonto (siehe DB, Funktion funSumme), danach normal
				+ " AND ((YEAR(Datum)=2018 AND MONTH(Datum) > 8) OR YEAR(Datum)>2018)" 
				+ " AND (DAY(Datum) = DATEPART(dd, DATEADD(dd, -DATEPART(dd, DATEADD(MM, 1, Datum)), DATEADD(MM, 1, datum))) "// letzter Tag des Monats
				// nur von Auszahlung betroffener Personenkreis (ohne AL) oder explizit eingetragene Auszahlungen
				+ getWhereIsPersonWithAuszahlungPlusstunden() + " OR StatusIDAuszahlung IS NOT NULL)  ";

		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}

		sql = "SELECT * FROM " + TABLE_NAME  + " k JOIN tblPerson p ON (k.PersonID=p.ID) "
		+ " OUTER APPLY funPlusMinusStunden(PersonID, YEAR(Datum), MONTH(Datum), DAY(Datum))"
		+ " OUTER APPLY funSumme(PersonID, YEAR(Datum), MONTH(Datum), DAY(Datum))"
		+ " WHERE " + where
		+ " ORDER BY Nachname, Vorname, Datum DESC"; // neuesten Eintrag zuerst";
		
		return sql;
	}


	/**
	 * Where-Teil einer Abfrage (mit AND) mit Prüfung ob die Person die normale Überstunden-Regelung hat (keine AL, externe, geringfügig Beschäftigte...). 
	 * 
	 * @param where
	 * @return
	 */
	private static String getWhereIsPersonWithAuszahlungPlusstunden() {
		return " AND p.StandortID =" + CoStandort.ID_JUELICH + " AND (positionID = " + CoPosition.ID_INGENIEUR 
				+ " OR positionID = " + CoPosition.ID_TECHNIKER + " OR positionID = " + CoPosition.ID_SACHBEARBEITER  + " OR positionID = " 
				+ CoPosition.ID_SEKRETAERIN  + " OR positionID = " + CoPosition.ID_AZUBI  + " OR positionID = " + CoPosition.ID_SYSTEMADMINISTRATOR  
				+ " OR positionID = " + CoPosition.ID_SE + ")";
	}


	private void loadCoPerson(int personID) throws Exception {
		m_coPerson = new CoPerson();
		m_coPerson.loadByID(personID);
	}


	@Override
	public String getNavigationBitmap() {
		return "calendar.edit";
	}


	/**
	 * Nach Datum sortieren
	 * 
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "Datum";
	}
	

	public IField getFieldArbeitszeit() {
		return getField("field." + getTableName() + ".wertbezahltearbeitszeit");
	}


	public void setWertArbeitszeit(int wertArbeitszeit) {
		getFieldArbeitszeit().setValue(wertArbeitszeit);
	}


	public int getWertArbeitszeit() {
		return Format.getIntValue(getFieldArbeitszeit().getValue());
	}


	/**
	 * Berechnet die Summe der bezahlten Arbeitszeit für die aktuelle Woche
	 * 
	 * @return
	 * @throws Exception 
	 */
//	private int getWertArbeitszeitWoche() throws Exception {
//		String datum, sql;
//		
//		datum = Format.getString(getDatum());
//		
//		sql = "SELECT SUM(WertBezahlteArbeitszeit) FROM tblKontowert WHERE PersonID=" + m_coPerson.getID() 
//		+ " AND Datum > CAST(DATEADD(DAY, -DATEPART(WEEKDAY, '" + datum + "') + 1, '" + datum + "') AS DATE)"
//		+ " AND Datum < CAST(DATEADD(DAY, -DATEPART(WEEKDAY, '" + datum + "') + 8, '" + datum + "') AS DATE)";
//
//		return Format.getIntValue(Application.getLoaderBase().executeScalar(sql));
//	}


	private IField getFieldArbeitszeitMonat() {
		return getField("virt.field.kontowert.wertbezahltearbeitszeitmonat");
	}


	private void setWertArbeitszeitMonat(int wertZeit) {
		getFieldArbeitszeitMonat().setValue(wertZeit);
	}


	public int getWertArbeitszeitMonat() {
		return Format.getIntValue(getFieldArbeitszeitMonat().getValue());
	}


	private IField getFieldArbeitszeitWoche() {
		return getField("virt.field.kontowert.wertbezahltearbeitszeitwoche");
	}


	private void setWertArbeitszeitWoche(int wertZeit) {
		getFieldArbeitszeitWoche().setValue(wertZeit);
	}


	public IField getFieldSollArbeitszeit() {
		return getField("field." + getTableName() + ".wertsollarbeitszeit");
	}


	private void setWertSollArbeitszeit(int wertSollArbeitszeit) {
		getFieldSollArbeitszeit().setValue(wertSollArbeitszeit);
	}


	public int getWertSollArbeitszeit() {
		return Format.getIntValue(getFieldSollArbeitszeit().getValue());
	}


	private IField getFieldSollArbeitszeitMonat() {
		return getField("virt.field.kontowert.wertsollarbeitszeitmonat");
	}


//	private void setWertSollArbeitszeitMonat(int wertSollArbeitszeit) {
//		getFieldSollArbeitszeitMonat().setValue(wertSollArbeitszeit);
//	}


	public int getWertSollArbeitszeitMonat() {
		return Format.getIntValue(getFieldSollArbeitszeitMonat().getValue());
	}


	public IField getFieldUeberstunden() {
		return getField("field." + getTableName() + ".wertueberstunden");
	}


	private void setWertUeberstunden(int wertUeberstunden) {
		getFieldUeberstunden().setValue(wertUeberstunden);
	}


	public int getWertUeberstunden() {
		return Format.getIntValue(getFieldUeberstunden().getValue());
	}


	private IField getFieldUeberstundenMonat() {
		return getField("virt.field.kontowert.wertueberstundenmonat");
	}


	private void setWertUeberstundenMonat(int wertUeberstunden) {
		getFieldUeberstundenMonat().setValue(wertUeberstunden);
	}


	private IField getFieldUeberstundenWoche() {
		return getField("virt.field.kontowert.wertueberstundenwoche");
	}


	private void setWertUeberstundenWoche(int wertUeberstunden) {
		getFieldUeberstundenWoche().setValue(wertUeberstunden);
	}


//	public int getWertUeberstundenMonat() {
//		return Format.getIntValue(getFieldUeberstundenMonat().getValue());
//	}


	public IField getFieldPlusstunden() {
		return getField("virt.field.kontowert.wertplusstunden");
	}


	private void setWertPlusstunden(Integer wertPlusstunden) {
		getFieldPlusstunden().setValue(wertPlusstunden);
	}


	public int getWertPlusstunden() {
		return Format.getIntValue(getFieldPlusstunden().getValue());
	}


	public IField getFieldPlusstundenMonat() {
		return getField("virt.field.kontowert.wertplusstundenmonat");
	}


	private void setWertPlusstundenMonat(int wertPlusstunden) {
		getFieldPlusstundenMonat().setValue(wertPlusstunden);
	}


	public int getWertPlusstundenMonat() {
		return Format.getIntValue(getFieldPlusstundenMonat().getValue());
	}


	public IField getFieldPlusstundenProjekt() {
		return getField("virt.field.kontowert.wertplusstundenprojekt");
	}


	private void setWertPlusstundenProjekt(Integer wertPlusstundenProjekt) {
		getFieldPlusstundenProjekt().setValue(wertPlusstundenProjekt);
	}


	public int getWertPlusstundenProjekt() {
		return Format.getIntValue(getFieldPlusstundenProjekt().getValue());
	}


	public IField getFieldPlusstundenProjektMonat() {
		return getField("virt.field.kontowert.wertplusstundenprojektmonat");
	}


	private void setWertPlusstundenProjektMonat(int wertPlusstunden) {
		getFieldPlusstundenProjektMonat().setValue(wertPlusstunden);
	}


	public int getWertPlusstundenProjektMonat() {
		return Format.getIntValue(getFieldPlusstundenProjektMonat().getValue());
	}


	public IField getFieldPlusstundenReise() {
		return getField("virt.field.kontowert.wertplusstundenreise");
	}


	private void setWertPlusstundenReise(Integer wertPlusstundenReise) {
		getFieldPlusstundenReise().setValue(wertPlusstundenReise);
	}


	public int getWertPlusstundenReise() {
		return Format.getIntValue(getFieldPlusstundenReise().getValue());
	}


	public IField getFieldPlusstundenReiseMonat() {
		return getField("virt.field.kontowert.wertplusstundenreisemonat");
	}


	private void setWertPlusstundenReiseMonat(int wertPlusstunden) {
		getFieldPlusstundenReiseMonat().setValue(wertPlusstunden);
	}


	public int getWertPlusstundenReiseMonat() {
		return Format.getIntValue(getFieldPlusstundenReiseMonat().getValue());
	}


	public IField getFieldMinusstunden() {
		return getField("virt.field.kontowert.wertminusstunden");
	}


	private void setWertMinusstunden(Integer wertMinusstunden) {
		getFieldMinusstunden().setValue(wertMinusstunden);
	}


	public int getWertMinusstunden() {
		return Format.getIntValue(getFieldMinusstunden().getValue());
	}


	public IField getFieldMinusstundenMonat() {
		return getField("virt.field.kontowert.wertminusstundenmonat");
	}


	private void setWertMinusstundenMonat(int wertMinusstunden) {
		getFieldMinusstundenMonat().setValue(wertMinusstunden);
	}


	public int getWertMinusstundenMonat() {
		return Format.getIntValue(getFieldMinusstundenMonat().getValue());
	}


	private IField getFieldAuszahlbareUeberstunden() {
		return getField("virt.field.kontowert.wertauszahlbareueberstunden");
	}


	private void setWertAuszahlbareUeberstunden(int auszahlbareUeberstunden) {
		getFieldAuszahlbareUeberstunden().setValue(auszahlbareUeberstunden);
	}


	public int getWertAuszahlbareUeberstunden() {
		return Format.getIntValue(getFieldAuszahlbareUeberstunden().getValue());
	}


	public IField getFieldAuszahlbareUeberstundenProjekt() {
		return getField("virt.field.kontowert.wertauszahlbareueberstundenprojekt");
	}


	private void setWertAuszahlbareUeberstundenProjekt(int auszahlbareUeberstundenProjekt) {
		getFieldAuszahlbareUeberstundenProjekt().setValue(auszahlbareUeberstundenProjekt);
	}


	public int getWertAuszahlbareUeberstundenProjekt() {
		return Format.getIntValue(getFieldAuszahlbareUeberstundenProjekt().getValue());
	}


	public IField getFieldAuszahlbareUeberstundenReise() {
		return getField("virt.field.kontowert.wertauszahlbareueberstundenreise");
	}


	private void setWertAuszahlbareUeberstundenReise(int auszahlbareReiseUeberstunden) {
		getFieldAuszahlbareUeberstundenReise().setValue(auszahlbareReiseUeberstunden);
	}


	public int getWertAuszahlbareUeberstundenReise() {
		return Format.getIntValue(getFieldAuszahlbareUeberstundenReise().getValue());
	}


	public IField getFieldAuszahlungUeberstunden() {
		return getField("field.tblkontowert.wertauszahlungueberstunden");
	}


	public void setWertAuszahlungUeberstunden(Integer auszahlbareUeberstunden) {
		getFieldAuszahlungUeberstunden().setValue(auszahlbareUeberstunden);
	}


	public int getWertAuszahlungUeberstunden() {
		return Format.getIntValue(getFieldAuszahlungUeberstunden().getValue());
	}


	public IField getFieldAuszahlungUeberstundenProjekt() {
		return getField("field.tblkontowert.wertauszahlungueberstundenprojekt");
	}


	public void setWertAuszahlungUeberstundenProjekt(Integer auszahlbareUeberstundenProjekt) {
		getFieldAuszahlungUeberstundenProjekt().setValue(auszahlbareUeberstundenProjekt);
	}


	public int getWertAuszahlungUeberstundenProjekt() {
		return Format.getIntValue(getFieldAuszahlungUeberstundenProjekt().getValue());
	}


	public IField getFieldAuszahlungUeberstundenReise() {
		return getField("field.tblkontowert.wertauszahlungueberstundenreise");
	}


	public void setWertAuszahlungUeberstundenReise(Integer auszahlbareUeberstunden) {
		getFieldAuszahlungUeberstundenReise().setValue(auszahlbareUeberstunden);
	}


	public int getWertAuszahlungUeberstundenReise() {
		return Format.getIntValue(getFieldAuszahlungUeberstundenReise().getValue());
	}


	public int getWertNichtAusgezahlteUeberstunden() {
		return getWertAuszahlbareUeberstundenProjekt() + getWertAuszahlbareUeberstundenReise() 
		- getWertAuszahlungUeberstundenProjekt() - getWertAuszahlungUeberstundenReise();
	}


	public IField getFieldStandGleitzeitkonto() {
		return getField("field." + getTableName() + ".wertueberstundengesamt");
	}


	public int getWertUeberstundenGesamt() {
		return Format.getIntValue(getFieldStandGleitzeitkonto().getValue());
	}


	private void setWertUeberstundenGesamt(int wertUeberstunden) {
		getFieldStandGleitzeitkonto().setValue(wertUeberstunden);
	}


	public IField getFieldAenderungGleitzeitkonto() {
		return getField("virt.field.kontowert.aenderunggleitzeitkonto");
	}


//	private void setWertAenderungGleitzeitkonto(int wertUeberstunden) {
//		getFieldAenderungGleitzeitkonto().setValue(wertUeberstunden);
//	}


	public int getWertAenderungGleitzeitkonto() {
		return Format.getIntValue(getFieldAenderungGleitzeitkonto().getValue());
	}


	public IField getFieldAnwesend() {
		return getField("field." + getTableName() + ".wertanwesend");
	}


//	private void setWertAnwesend(int wertAnwesend) {
//		getFieldAnwesend().setValue(wertAnwesend);
//	}


	public int getWertAnwesend() {
		return Format.getIntValue(getFieldAnwesend().getValue());
	}


	public IField getFieldDienstreise() {
		return getField("field." + getTableName() + ".wertdienstreise");
	}


//	private void setWertDienstreise(int wertDienstreise) {
//		getFieldDienstreise().setValue(wertDienstreise);
//	}


	public int getWertDienstreise() {
		return Format.getIntValue(getFieldDienstreise().getValue());
	}


	public IField getFieldReisezeit() {
		return getField("field." + getTableName() + ".wertreisezeit");
	}


	public void setWertReisezeit(int wertReisezeit) {
		getFieldReisezeit().setValue(wertReisezeit);
	}


	public int getWertReisezeit() {
		return Format.getIntValue(getFieldReisezeit().getValue());
	}


	public IField getFieldDienstgang() {
		return getField("field." + getTableName() + ".wertdienstgang");
	}


//	private void setWertDienstgang(int wertDienstgang) {
//		getFieldDienstgang().setValue(wertDienstgang);
//	}


	public int getWertDienstgang() {
		return Format.getIntValue(getFieldDienstgang().getValue());
	}


	public IField getFieldVorlesung() {
		return getField("field." + getTableName() + ".wertvorlesung");
	}


//	private void setWertVorlesung(int wertVorlesung) {
//		getFieldVorlesung().setValue(wertVorlesung);
//	}


	public int getWertVorlesung() {
		return Format.getIntValue(getFieldVorlesung().getValue());
	}


	private IField getFieldBerufsschule() {
		return getField("field." + getTableName() + ".wertberufsschule");
	}


//	private void setWertBerufsschule(int wertBerufsschule) {
//		getFieldBerufsschule().setValue(wertBerufsschule);
//	}


	private int getWertBerufsschule() {
		return Format.getIntValue(getFieldBerufsschule().getValue());
	}


	public IField getFieldPause() {
		return getField("field." + getTableName() + ".wertpause");
	}


//	private void setWertPause(int wertPause) {
//		getFieldPause().setValue(wertPause);
//	}


	private int getWertPause() {
		return Format.getIntValue(getFieldPause().getValue());
	}


	public IField getFieldPausenaenderung() {
		return getField("field." + getTableName() + ".wertpausenaenderung");
	}


	private void setWertPausenaenderung(int wertPausenaenderung) {
		getFieldPausenaenderung().setValue(wertPausenaenderung);
	}


	private int getWertPausenaenderung() {
		return Format.getIntValue(getFieldPausenaenderung().getValue());
	}


	public IField getFieldArbeitsunterbrechung() {
		return getField("field." + getTableName() + ".wertarbeitsunterbrechung");
	}


	public IField getFieldPrivateUnterbrechung() {
		return getField("field." + getTableName() + ".wertprivateunterbrechung");
	}


//	private void setWertPrivateUnterbrechung(int wertPrivateUnterbrechung) {
//		getFieldPrivateUnterbrechung().setValue(wertPrivateUnterbrechung);
//	}
//
//
//	private int getWertPrivateUnterbrechung() {
//		return Format.getIntValue(getFieldPrivateUnterbrechung().getValue());
//	}


	public IField getFieldWertKrank() {
		return getField("field." + getTableName() + ".wertkrank");
	}


	private void setWertKrank(int wertKrank) {
		getFieldWertKrank().setValue(wertKrank);
	}


	public int getWertKrank() {
		return Format.getIntValue(getFieldWertKrank().getValue());
	}


	private int getWert(IField field) {
		return Format.getIntValue(field.getValue());
	}


//	private void setWert(IField field, int wert) {
//		field.setValue(wert);
//	}


	private void addWert(IField field, int wert) throws Exception {
		field.setValue(getWert(field) + wert);
	}


	public IField getFieldAnzahlUrlaub() {
		return getField("field." + getTableName() + ".anzahlurlaub");
	}


//	private void setAnzahlUrlaub(int anzahlUrlaub) {
//		getFieldAnzahlUrlaub().setValue(anzahlUrlaub);
//	}


	public int getAnzahlUrlaub() {
		return Format.getIntValue(getFieldAnzahlUrlaub().getValue());
	}


	public IField getFieldResturlaub() {
		return getField("field." + getTableName() + ".resturlaub");
	}


	public int getResturlaub() {
		return Format.getIntValue(getFieldResturlaub().getValue());
	}


	/**
	 * Anzahl der bereits genehmigten Urlaubstage, ab dem aktuellen Datum.<br>
	 * Wenn der Urlaub im nächsten Jahr genommen wird, wird er auch abgezogen.
	 * 
	 * @param datumGeplantBis 
	 * @return
	 * @throws Exception
	 */
	public int getResturlaubGenehmigt(Date datumGeplantBis) throws Exception {
		String sql;

		sql = "SELECT COUNT(*) FROM tblBuchung WHERE PersonID= " + m_coPerson.getID() + " AND BuchungsartID=" + CoBuchungsart.ID_URLAUB 
				+ " AND " + "Datum > '" + Format.getString(Format.getDateVerschoben(getDatum(), 1)) + "'"
				+ (datumGeplantBis == null ? "" : " AND " + "Datum < '" + Format.getString(Format.getDateVerschoben(datumGeplantBis, 1)) + "'")
				+ " AND (StatusID = " + CoStatusBuchung.STATUSID_OK + " OR StatusID = " + CoStatusBuchung.STATUSID_GEAENDERT + ")";

		return Math.min(Format.getIntValue(Application.getLoaderBase().executeScalar(sql)), getResturlaub());
	}


	public IField getFieldResturlaubGenehmigt() {
		return getField("virt.field.kontowert.resturlaubgenehmigt");
	}


	public IField getFieldRest() {
		return getField("virt.field.kontowert.rest");
	}


	public IField getFieldResturlaubVerplant() {
		return getField("virt.field.kontowert.resturlaubverplant");
	}


	public IField getFieldResturlaubOffen() {
		return getField("virt.field.kontowert.resturlauboffen");
	}


	public int getResturlaubOffen() {
		return Format.getIntValue(getFieldResturlaubOffen());
	}


	public IField getFieldAnzahlSonderurlaub() {
		return getField("field." + getTableName() + ".anzahlsonderurlaub");
	}


//	private void setAnzahlSonderurlaub(int anzahlSonderurlaub) {
//		getFieldAnzahlSonderurlaub().setValue(anzahlSonderurlaub);
//	}


	public int getAnzahlSonderurlaub() {
		return Format.getIntValue(getFieldAnzahlSonderurlaub().getValue());
	}


	public IField getFieldAnzahlFa() {
		return getField("field." + getTableName() + ".anzahlfa");
	}


//	private void setAnzahlFa(int anzahlFa) {
//		getFieldAnzahlFa().setValue(anzahlFa);
//	}


	public int getAnzahlFa() {
		return Format.getIntValue(getFieldAnzahlFa().getValue());
	}


	public IField getFieldAnzahlElternzeit() {
		return getField("field." + getTableName() + ".anzahlelternzeit");
	}


//	private void setAnzahlElternzeit(int anzahlElternzeit) {
//		getFieldAnzahlElternzeit().setValue(anzahlElternzeit);
//	}


	public int getAnzahlElternzeit() {
		return Format.getIntValue(getFieldAnzahlElternzeit().getValue());
	}


	public IField getFieldAnzahlKrank() {
		return getField("field." + getTableName() + ".anzahlkrank");
	}


//	private void setAnzahlKrank(int anzahlKrank) {
//		getFieldAnzahlKrank().setValue(anzahlKrank);
//	}


	public int getAnzahlKrank() {
		return Format.getIntValue(getFieldAnzahlKrank().getValue());
	}


	public IField getFieldAnzahlKrankOhneLfz() {
		return getField("field." + getTableName() + ".anzahlkrankohnelfz");
	}


//	private void setAnzahlKrankOhneLfz(int anzahlKrankOhneLfz) {
//		getFieldAnzahlKrankOhneLfz().setValue(anzahlKrankOhneLfz);
//	}


	public int getAnzahlKrankOhneLfz() {
		return Format.getIntValue(getFieldAnzahlKrankOhneLfz());
	}


	public IField getFieldAnzahlBezFreistellung() {
		return getField("field." + getTableName() + ".anzahlbezfreistellung");
	}


	public int getAnzahlBezFreistellung() {
		return Format.getIntValue(getFieldAnzahlBezFreistellung());
	}


	public void setStatus(String status) throws Exception {
		setStatusID(CoStatusKontowert.getInstance().getID(status));
	}


	public String getStatus() throws Exception {
		return CoStatusKontowert.getInstance().getBezeichnung(getStatusID());
	}


	public void setStatusOk() throws Exception {
		setStatusID(CoStatusKontowert.STATUSID_OK);
	}


	public IField getFieldStatusIDAuszahlung() {
		return getField("field." + getTableName() + ".statusidauszahlung");
	}


	public int getStatusIDAuszahlung() throws Exception {
		return Format.getIntValue(getFieldStatusIDAuszahlung().getValue());
	}


	public void setStatusIDAuszahlung(int statusID) throws Exception {
		getFieldStatusIDAuszahlung().setValue(statusID);
	}


	/**
	 * Berechnet die Werktage (ohne Urlaub, Krank etc.) und tatsächlich gearbeitete Zeit für den geladenen Zeitraum
	 * 
	 * @return
	 * @throws Exception 
	 */
	public void berechneWerktageArbeitszeit() throws Exception{
		int arbeitszeit, bundeslandID;
		boolean isFreierTag;
//		boolean isLetzterTagWerktag; 
		Date datum, datumHeute;
		CoBrueckentag coBrueckentag;
		FeiertagGenerator feiertagGenerator;
		
		datumHeute = Format.getDate0Uhr(new Date());
		bundeslandID = m_coPerson.getBundeslandID();
		feiertagGenerator = FeiertagGenerator.getInstance();
		coBrueckentag = CoBrueckentag.getInstance();
		
		m_anzWerktageBerechnet = 0;
		m_summeArbeitszeitBerechnet = 0;
//		isLetzterTagWerktag = false;
		if (moveFirst())
		{
			do
			{
				// nur bis zum heutigen Tag berechnen
				datum = getDatum();
				if (datum.after(datumHeute))
				{
					return;
				}
				
				arbeitszeit = getWertArbeitszeit();
				isFreierTag = feiertagGenerator.isFeiertag(datum, bundeslandID) || coBrueckentag.isBrueckentag(datum, bundeslandID);
				
				// Montag bis Freitag
				if (Format.isMoBisFr(datum))
				{
//					isLetzterTagWerktag = false;

					// Prüfung ob Urlaub, Krank oder Elternzeit
					if (getAnzahlUrlaub() + getAnzahlSonderurlaub() + getAnzahlKrank() + getAnzahlKrankOhneLfz() + getAnzahlElternzeit() == 0)
					{
						// wenn nicht, Arbeitszeit summieren
						m_summeArbeitszeitBerechnet += arbeitszeit;
						
						// Werktag nur wenn es kein Feiertag/Brückentag ist
						if (!isFreierTag)
						{
							++m_anzWerktageBerechnet;
//							isLetzterTagWerktag = true;
						}
					}
				}
				else // Wochenende
				{
					// Arbeitszeit summieren
					m_summeArbeitszeitBerechnet += arbeitszeit;

					// Samstag zählt als Werktag wenn es kein Feiertag ist und der Freitag davor war ein Arbeitstag
					if (Format.isSamstag(datum) && !isFreierTag /*&& isLetzterTagWerktag*/)
					{
						++m_anzWerktageBerechnet;
//						isLetzterTagWerktag = true;
					}
//					else // momentan nicht notwendig
//					{
//						isLetzterTagWerktag = false;
//					}
				}
				
			} while (moveNext());
		}
	}
	
	
	/**
	 * Die für den Monat bestimmte Anzahl Werktage abzgl. Urlaub etc.
	 * 
	 * @return
	 */
	public int getAnzWerktageBerechnet(){
		return m_anzWerktageBerechnet;
	}
	
	
	
	/**
	 * Die für den Monat bestimmte tatsächlich gearbeitete Zeit
	 * 
	 * @return
	 */
	public int getSummeArbeitszeitBerechnet(){
		return m_summeArbeitszeitBerechnet;
	}
	
	
	/**
	 * Prüft, ob es ein gültiger Datensatz mit Kontowerten oder ein leerer Dummy-Datensatz ist
	 * 
	 * @return
	 */
	public boolean isGueltig(){
		return getPersonID() > 0;
	}
	

	/**
	 * Neuen Datensatz erstellen für die Person und den Tag.<br>
	 * Die Kontowerte für diesen Tag werden durch die Buchungen berechnet.
	 * 
	 * @param datum 
	 * @param personID 
	 */
	public int createNew(int personID, Date datum) throws Exception	{
		
		createVortagIfNotExists(personID, datum);
		
		// wenn kein Zeitmodell existiert, können keine Kontodaten erstellt werden
		if (m_coPerson.getCoZeitmodell(datum) == null)
		{
			return 0;
		}

		// neuen Datensatz anlegen
		super.createNew();
		
		setPersonID(personID);
		setDatum(Format.getDate12Uhr(datum));
		setStatusOk();
		
		// Kontowerte aktualisieren
		updateKontowerte(true);
		
		return getID();
	}


	/**
	 * Datensatz für den Vortag von Datum erstellen, falls er noch nicht existiert
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public void createVortagIfNotExists(int personID, Date datum) throws Exception {
		Date datumVortag, beginnPze, endePze;
		CoKontowert coKontowert;
		
		if (m_coPerson == null)
		{
			loadCoPerson(personID);
		}
		
		// wenn die Kontowerte für den Vortag noch nicht existieren, lege diese auch an
		datumVortag = Format.getDate12Uhr(Format.getDateVerschoben(datum, -1));
		
		// keine Kontowerte vor Beginn PZE anlegen
		beginnPze = Format.getDate12Uhr(m_coPerson.getBeginnPze());
		endePze = Format.getDate12Uhr(m_coPerson.getEndePze());
		if (beginnPze == null || datumVortag.before(beginnPze))
		{
			return;
		}

		// wenn die Person die Firma verlässt, berechne das letzte Datum
		if (endePze != null && datumVortag.after(endePze))
		{
			datumVortag = endePze;
		}

		coKontowert = new CoKontowert();
//		coKontowert.load(personID, datumVortag);
		coKontowert.loadEinfach(personID, datumVortag);

		if (!coKontowert.moveFirst())
		{
			coKontowert.createNew(personID, datumVortag);
		}
	}


	/**
	 * Nach dem Speichern auch die Verletzerliste speichern.<br>
	 * Funktioniert nicht, wenn mehrere Datensätze geladen sind.
	 * 
	 * @see framework.business.cacheobject.CacheObject#save()
	 */
	public void save() throws Exception{

		// Daten speichern
		super.save();
		
		// Verletzerliste speichern, wenn es nur einen Datensatz gibt
		// (wenn es mehrere gibt, sollte auch nicht gespeichert werden, sonst funktionieren auch andere Funktionen nicht)
		if (getRowCount() == 1)
		{
			// Wochenarbeitszeit prüfen, kann erst nach dem Speichern der Kontowerte gemacht werden
//			m_coVerletzerliste.checkArbeitszeitWoche(getWertArbeitszeitWoche());
			
			// bei Dienstreisen muss die Reisezeit angegeben sein, prüfen falls die Reisezeit eingegeben wurde
			updateVerletzerlisteReisezeit();

			// Verletzerliste speichern
			if (m_coVerletzerliste != null && m_coVerletzerliste.isEditing())
			{
				m_coVerletzerliste.save();
			}
		}
	}


	/**
	 *  Datum der letzten Auszahlung prüfen, da danach keine Änderungen gemacht werden dürfen.<br>
	 *  
	 *  
	 * @return Datum oder null wenn kein Datum vorhanden bzw. aufgrund der Berechtigung (Personalverwaltung/Admin) nicht relevant
	 * @throws Exception
	 */
	protected Date checkDatumLetzteAuszahlung() throws Exception {
		Date datumLetzteAuszahlung;
		
		if (getRowCount() == 1)
		{
			// Datum der letzten Auszahlung laden, da danach keine Änderungen gemacht werden dürfen
			datumLetzteAuszahlung = CoKontowert.getDatumLetzteAuszahlung(getPersonID());

			// Datum der geänderten Rows prüfen
			if (!UserInformation.getInstance().isPersonalverwaltung() && datumLetzteAuszahlung != null && getDatum().before(datumLetzteAuszahlung))
			{
				return datumLetzteAuszahlung;
			}
		}
		
		return null;
	}

	
	/**
	 * Kontowerte für die Person und den Tag aktualisieren.<br>
	 * 
	 * @param datum 
	 * @param personID 
	 * @throws Exception 
	 */
	public boolean updateKontowerte(int personID, Date datum) throws Exception {
		String message;
		CoPerson coPerson;
		
		load(personID, datum);
		
		/////////////////////////////////////////////////// ggf. löschen und createNew in load-Funktion, wenn die alten Kontowerte beibehalten werden sollen
		if (moveFirst())
		{
			// Meldung, wenn geänderte Kontowerte neu berechnet werden
			if (getStatusID() == CoStatusKontowert.STATUSID_GEAENDERT)
			{
				coPerson = CoPerson.getInstance();
				coPerson.moveToID(getPersonID());
				message = "Die geänderten Kontowerte von " + coPerson.getName() + " für den " + Format.getString(getDatum());
				
				// wer hat die Änderungen gemacht
				coPerson.moveToID(getGeaendertVonID());
				message += " (geändert durch " + coPerson.getName() + " am " + Format.getString(getGeaendertAm()) + ")";
				
				message += " werden verworfen und neu berechnet. Möchten Sie fortfahren?";
				
				// Abfragen, ob Kontowerte überschrieben werden sollen
				if (!Messages.showYesNoMessage("Kontowerte überschreiben?", message))
				{
					Messages.showInfoMessage("Buchung abgebrochen", "Bitte prüfen Sie die Buchungen von " + coPerson.getName() + " für den " 
							+ Format.getString(getDatum()) + ". Ggf. müssen gerade gespeicherte Buchungen wieder ungültig gemacht werden.");
					return false;
				}
			}
			
			// Verletzerliste löschen
			m_coVerletzerliste.load(m_coPerson, getDatum());
			m_coVerletzerliste.deleteAll();
			m_coVerletzerliste.save();
			
			// alte Kontowerte löschen
			begin();
			deleteAll(); // eigentlich darf es nur einen Datensatz geben
			save();
		}
		
		// neuen Datensatz erstellen
		createNew(personID, datum);
		
		return true;
	}
	

	/**
	 * Kontowerte für den Vortag des übergebenen Datums für alle Personen prüfen und ggf. einfügen.
	 * 
	 * @param datum 
	 * @throws Exception
	 */
	public void updateKontowerteVortag(Date datum) throws Exception {
		int sollArbeitszeit;
		GregorianCalendar gregDatumVortag;
		CoPerson coPerson;

		
		// Datum für den Vortag
		gregDatumVortag = Format.getGregorianCalendar(datum);
		gregDatumVortag.add(Calendar.DAY_OF_MONTH, -1);

		// alle Personen mit Kontowerte für den Vortag laden
		coPerson = new CoPerson();
		coPerson.loadAllWithKontowerte(gregDatumVortag);
		
		if (!coPerson.moveFirst())
		{
			return;
		}
	
		// Kontowerte (Soll-Arbeitszeit) für die Personen prüfen
		do
		{
			load(coPerson.getID(), gregDatumVortag.getTime());
			
			// Wert merken und ggf. neu berechnen
			sollArbeitszeit = getWertSollArbeitszeit();
			berechneSollArbeitszeit();
			
			if (sollArbeitszeit != getWertSollArbeitszeit())
			{
				// Kontowerte aktualisieren
				updateKontowerte(coPerson.getID(), gregDatumVortag.getTime());
//				MessageBox.show("test", "Fehlerhafte Kontowerte korrigiert für " + coPerson.getName() + " am " + Format.getString(gregDatumVortag));
			}
		} while (coPerson.moveNext());
		
		
		// Kontowerte für die Personen anlegen, für die noch keine existieren
		createKontowerteVortagIfNotExists(datum);
	}


	/**
	 * Kontowerte für den Vortag des übergebenen Datums für alle Personen einfügen, wenn noch keine Kontowerte vorhanden sind.<br>
	 * Wird vermutlich nur benötigt, wenn keine Buchung für den Tag vorhanden ist.
	 * 
	 * @param datum 
	 * @throws Exception
	 */
	public void createKontowerteVortagIfNotExists(Date datum) throws Exception {
		GregorianCalendar gregDatumVortag;
		
		
		// Datum für den Vortag
		gregDatumVortag = Format.getGregorianCalendar(datum);
		gregDatumVortag.add(Calendar.DAY_OF_MONTH, -1);

		// alle Personen ohne Kontowerte für den Vortag laden
		m_coPerson = new CoPerson();
		m_coPerson.loadAllWithoutKontowerte(gregDatumVortag);
		
		if (!m_coPerson.moveFirst())
		{
			return;
		}
	
		// Kontowerte für die Personen anlegen
		do
		{
			createVortagIfNotExists(m_coPerson.getID(), datum);
		} while (m_coPerson.moveNext());
	}

	
	/**
	 * Kontowerte für die Person und den Tag aktualisieren.<br>
	 * 
	 * @param datum 
	 * @param personID 
	 * @throws Exception 
	 */
	public void updateKontowerteAbDatum(int personID, Date datum) throws Exception {

		m_coVerletzerliste = new CoVerletzerliste();

		loadAbDatum(personID, datum);

		if (!moveFirst())
		{
			return;
		}
		
		do
		{
			// Verletzerliste löschen
			m_coVerletzerliste.load(m_coPerson, getDatum());
			m_coVerletzerliste.deleteAll();
			m_coVerletzerliste.save();
			
			// Kontowerte aktualisieren
			updateKontowerte(false);
			
			// prüfen, ob etwas geändert wurde (wird sonst aus irgendeinem Grund nicht erkannt)
			getCurrentRow().setRowState(hasModifiedRows() ? IBusinessObject.statusChanged : IBusinessObject.statusUnchanged);

			// neue Verletzerliste speichern
			m_coVerletzerliste.save();
			
			// alte Kontowerte löschen
//			begin();
//			deleteAll(); // eigentlich darf es nur einen Datensatz geben
//			save();
		} while (moveNext());
		
		// neuen Datensatz erstellen
//		createNew(personID, datum);

		// Daten speichern
		super.save();
	}
	

	/**
	 * Kontowerte für den aktuell geladenen Tag aktualisieren.<br>
	 * 
	 * @throws Exception 
	 */
	private void updateKontowerte(boolean save) throws Exception {

		if (getRowCount() == 0)
		{
			return;
		}
		
		if (!isEditing())
		{
			begin();
		}
		
		// Verletzerliste anlegen
		m_coVerletzerliste = new CoVerletzerliste(m_coPerson, getDatum());
		
		// Stundenverteilung auf die Konten Anwesend, Dienstreise etc. berechnen
		berechneStundenverteilung();

		// Soll-Arbeitszeit berechnen
		berechneSollArbeitszeit();
		
		// Arbeitszeit (und Pausenänderung) berechnen aus den verschiedenen Kontowerten (Anwesend, Dienstgang/-reise...)
		berechneArbeitszeit(true);
	
		// Überstunden berechnen
		updateUeberstunden();

		// Arbeitszeit OFA prüfen, 60 Minuten Toleranz, sonst Meldung zu wenig gearbeitet
//		m_coVerletzerliste.check(getWertArbeitszeit() > m_wertOfaGeplant-61, CoMeldungVerletzerliste.MELDUNGID_OFA_ARBEITSZEIT, m_wertOfaGeplant);

		// Kontowerte speichern
		if (save)
		{
			save();
		}
	}


	/**
	 * Bestimme die Arbeitszeit aus Anwesend, Dienstgang, Dienstreise, Vorlesung und Berufsschule.<br>
	 * Pausenänderung wird ebenfalls berechnetund die Arbeitszeit entsprechend angepasst.
	 * 
	 * @param pausenaenderungBerechnen Pausenänderung berechnen oder nicht (wg. manuell geänderter Pausenänderung)
	 * @return
	 * @throws Exception 
	 */
	private void berechneArbeitszeit(boolean pausenaenderungBerechnen) throws Exception {
		int arbeitszeit, maxTagesarbeitszeit, differenz;

		
		// Arbeitszeit berechnen aus den verschiedenen Kontowerten (Anwesend, Dienstgang/-reise...)
		arbeitszeit = getWertAnwesend() + getWertDienstgang() + getWertDienstreise() + getWertVorlesung() + getWertBerufsschule() + getWertKrank();
		
		// Pausenzeiten berechnen und Arbeitszeit anpassen
		if (pausenaenderungBerechnen)
		{
			berechnePausenaenderung(arbeitszeit);
		}
		arbeitszeit = arbeitszeit - getWertPausenaenderung();
		
		// max. Tagesarbeitszeit prüfen
		maxTagesarbeitszeit = CoFirmenparameter.getInstance().getMaxTagesarbeitszeit();
		m_coVerletzerliste.check(arbeitszeit <= maxTagesarbeitszeit, CoMeldungVerletzerliste.MELDUNGID_UEBERSCHREITUNG_ARBEITSZEIT_TAG, arbeitszeit);

		// max. 10 Stunden am Tag anrechnen
		arbeitszeit = Math.min(arbeitszeit, maxTagesarbeitszeit); 

		// Verletzerliste Gearbeitet an arbeitsfreiem Tag
		m_coVerletzerliste.check(m_coPerson.isArbeitstag(getDatum()) || arbeitszeit == 0, CoMeldungVerletzerliste.MELDUNGID_GEARBEITET_ARBEITSFREIER_TAG, arbeitszeit);
		
		// bei Tagesbuchungen Krank und Urlaub Sollarbeitszeit gutschreiben
		if (getAnzahlKrank() + getAnzahlUrlaub() + getAnzahlSonderurlaub() == 1)
		{
			arbeitszeit = getWertSollArbeitszeit();
		}

		// bei Krank als letzter Buchung wird auch die Sollarbeitszeit gutgeschrieben
		if (m_isKrankLetzteBuchung)
		{
			differenz = Math.max(0, getWertSollArbeitszeit() - arbeitszeit);
			setWertKrank(getWertKrank() + differenz);
			arbeitszeit += differenz;
		}

		setWertArbeitszeit(arbeitszeit);
	}


	/**
	 * Sollarbeitszeit über das Zeitmodell und die Abwesenheitsbuchung berechnen.<br>
	 * Dafür müssen die Kontowerte anhand der Buchungen vorher berechnet werden.
	 * 
	 * @throws Exception
	 */
	private void berechneSollArbeitszeit() throws Exception {
		int sollArbeitszeit;
		Date datum;
		
		sollArbeitszeit = 0;
		datum = getDatum();
		
		// SollArbeitszeit berechnen, wenn es ein Arbeitstag ist 
		if (m_coPerson.isArbeitstag(datum))
		{
			// Sollarbeitszeit aus dem Zeitmodell bestimmen
			sollArbeitszeit = m_coPerson.getSollArbeitszeitZeitmodell(datum);
			
			// bei Tagesbuchungen Elternzeit und Krank ohne Lfz. gibt es keine SollArbeitszeit gutschreiben
			if (getAnzahlElternzeit() + getAnzahlKrankOhneLfz() + getAnzahlBezFreistellung() == 1)
			{
				sollArbeitszeit = 0;
			}
		}

		setWertSollArbeitszeit(sollArbeitszeit);
	}


	/**
	 * Stundenverteilung auf die Konten Anwesend, Dienstreise etc. für den aktuell geladenen Tag berechnen auf Basis der Buchungen für den Tag.<br>
	 * 
	 * @param coBuchung
	 * @throws Exception
	 */
	private void berechneStundenverteilung() throws Exception {
		int uhrzeit, uhrzeitLetzteBuchung, wertMinuten, anzBuchungen, buchungsartID;
		int beginnKernzeit, endeKernzeit, sollArbeitszeit, beginnPause, endePause;
		boolean isEingehalten, isOfaAnfangGeprueft;
		Date datum;
		IField currentField;
		CoBuchung coBuchung, coBuchungTmp;
		CoBuchungsart coBuchungsart, coBuchungsartLetzteBuchung;
		
		datum = getDatum();
		coBuchungTmp = new CoBuchung();
		
		isOfaAnfangGeprueft = false;
//		m_wertOfaGeplant = 0;
		
		// Buchungen laden
		coBuchung = new CoBuchung();
		coBuchung.loadGueltig(m_coPerson, datum);
		anzBuchungen = coBuchung.getRowCount();

		// prüfen, ob Buchungen für einen Arbeitstag vorhanden sind
		isEingehalten = !m_coPerson.isArbeitstag(datum) || anzBuchungen > 0;
		m_coVerletzerliste.check(isEingehalten, CoMeldungVerletzerliste.MELDUNGID_KEINE_BUCHUNG);
		
		// wenn keine Buchungen vorhanden sind, muss nicht weiter berechnet werden
		if (anzBuchungen == 0)
		{
			return;
		}

		if (!isEditing())
		{
			begin();
		}
		
		coBuchung.moveFirst();
		beginnKernzeit = CoFirmenparameter.getInstance().getKernzeitBeginn(datum);
		endeKernzeit = CoFirmenparameter.getInstance().getKernzeitEnde(datum);
		
		// erste Buchung prüfen, ob es eine Tagesbeginnbuchung ist
		coBuchungsart = coBuchung.getCoBuchungsart();
		m_coVerletzerliste.check(coBuchungsart.isTagesbeginnbuchung(), CoMeldungVerletzerliste.MELDUNGID_TAGESBEGINN_FEHLT);
		
		// erste Buchung prüfen, ob sie nach Kernzeitbeginn liegt
		uhrzeit = coBuchung.getUhrzeitAsInt();
		m_coVerletzerliste.check(uhrzeit <= beginnKernzeit, CoMeldungVerletzerliste.MELDUNGID_VERLETZUNG_KERNZEIT_BEGINN, uhrzeit);

		// Buchungen durchlaufen 
		currentField = null;
		coBuchungsart = null;
		do
		{
			// FA als nicht-Tagesbuchung wird ignoriert, es muss eine Gehen-Buchung gemacht werden, 
			// sonst funktioniert das genehmigen des FA-Antrags nicht
			if (coBuchung.getCoBuchungsart().getID() == CoBuchungsart.ID_FA)
			{
				if (anzBuchungen > 1)
				{
					continue;
				}
				
				coBuchung.loadAntragEnde();
				if (coBuchung.getUhrzeitBisAsInt() > 0)
				{
					coBuchungsart = coBuchung.getCoBuchungsart();
					continue;
				}
			}

			// Buchungsart bestimmen
			coBuchungsartLetzteBuchung = coBuchungsart;
			coBuchungsart = coBuchung.getCoBuchungsart();
			buchungsartID = coBuchung.getBuchungsartID();

			// Zeit berechnen von der letzten bis zur aktuellen Buchung
			uhrzeitLetzteBuchung = uhrzeit;
			uhrzeit = coBuchung.getUhrzeitAsInt();
			uhrzeit = rundeUhrzeit(uhrzeit, coBuchung, coBuchungsart);
					
			wertMinuten = uhrzeit - uhrzeitLetzteBuchung;

			// Minuten dem Stundenkonto zuweisen, bei FA und Krank ohne Lfz. keine Zeiten speichern, sondern nur ganze Tage
			if (currentField != null && !currentField.equals(getFieldAnzahlFa()) && !currentField.equals(getFieldAnzahlKrankOhneLfz()))
			{
				// bei privater Unterbrechung innerhalb der Pausenzeit, wird dies als Pause gutgeschrieben
				if (currentField.equals(getFieldPrivateUnterbrechung()))
				{
					beginnPause = CoFirmenparameter.getInstance().getPausenBeginn();
					endePause = CoFirmenparameter.getInstance().getPausenEnde();
					
					// priv. Unterbrechung vor und nach der Pausenzeit
					wertMinuten = Math.max(0, Math.min(beginnPause, uhrzeit) - uhrzeitLetzteBuchung) 
							+ Math.max(0, uhrzeit - Math.max(endePause, uhrzeitLetzteBuchung));
					addWert(currentField, wertMinuten);
					
					// priv. Unterbrechung innerhalb der Pausenzeit ist der Rest der priv. Unterbrechung
					wertMinuten = uhrzeit - uhrzeitLetzteBuchung - wertMinuten;
					addWert(getFieldPause(), wertMinuten);
				}
				else // Standardfall
				{
					addWert(currentField, wertMinuten);
				}
			}
			// Sonderfall Krank ohne Lfz.und zusätzlich gearbeitet
			else if (currentField != null && currentField.equals(getFieldAnzahlKrankOhneLfz()))
			{
				addWert(currentField, 1);
			}
			
			// Field bestimmen, auf das ab der aktuellen Buchung gebucht wird
			currentField = getCurrentField(buchungsartID);
			
			// OFA-Buchungen prüfen
//			if (buchungsartID == CoBuchungsart.ID_ORTSFLEX_ARBEITEN && !ofaAnfangGeprueft) // erste OFA-Buchung für Beginn der Arbeit
//			{
//				coBuchungTmp.loadBuchungVorlaeufig(getPersonID(), datum, buchungsartID);
//				m_coVerletzerliste.check(coBuchungTmp != null &&Math.abs(uhrzeit - coBuchungTmp.getUhrzeitAsInt()) <= 60, 
//						CoMeldungVerletzerliste.MELDUNGID_OFA, uhrzeit);
//				
//				// nur erste Buchung prüfen, nach Pause kann es weitere OFA-Buchungen geben
//				ofaAnfangGeprueft = true;
//			}
//			
//			// Gehen-Buchung im OFA, bei DR/DG wird sowieso ein Korrekturbeleg erstellt
//			if (ofaAnfangGeprueft && buchungsartID == CoBuchungsart.ID_GEHEN)
//			{
//				coBuchungTmp.loadBuchungVorlaeufig(getPersonID(), datum, buchungsartID); // mit der passenden Vorläufigen Uhrzeit vergleichen
//				m_coVerletzerliste.check(coBuchungTmp != null && Math.abs(uhrzeit - coBuchungTmp.getUhrzeitAsInt()) <= 60, 
//						CoMeldungVerletzerliste.MELDUNGID_OFA, uhrzeit);
//			}
			

			// Buchungen in Abhängigkeit der letzten Buchung
			if (coBuchungsartLetzteBuchung != null)
			{
				// wenn auf Pause gebucht wurde, darf die nächste Buchung keine Tagesendbuchung sein
				m_coVerletzerliste.check(coBuchungsartLetzteBuchung.getID() != CoBuchungsart.ID_PAUSE || !coBuchungsart.isTagesendbuchung(), 
						CoMeldungVerletzerliste.MELDUNGID_PAUSENENDE_FEHLT);


				// wenn auf Pause gebucht wurde, darf die nächste Buchung nicht nach dem Pausenende sein
				m_coVerletzerliste.check(coBuchungsartLetzteBuchung.getID() != CoBuchungsart.ID_PAUSE 
						|| (uhrzeit >= CoFirmenparameter.getInstance().getPausenBeginn() && uhrzeit <= CoFirmenparameter.getInstance().getPausenEnde()), 
						CoMeldungVerletzerliste.MELDUNGID_PAUSENENDE, uhrzeit);

				
				// doppelte Buchung
				m_coVerletzerliste.check(coBuchungsart.getID() != coBuchungsartLetzteBuchung.getID(), CoMeldungVerletzerliste.MELDUNGID_DOPPELTE_BUCHUNG, uhrzeit);
				
				// 
//				if (coBuchungsartLetzteBuchung.getID() == CoBuchungsart.ID_KRANK_OHNE_LFZ)
//				{
//					addWert(currentField, 1);
//				}
			}

			
			// Pausenbeginn
			m_coVerletzerliste.check(coBuchungsart.getID() != CoBuchungsart.ID_PAUSE 
					|| (uhrzeit >= CoFirmenparameter.getInstance().getPausenBeginn() && uhrzeit <= CoFirmenparameter.getInstance().getPausenEnde()), 
					CoMeldungVerletzerliste.MELDUNGID_PAUSENBEGINN, uhrzeit);

			
			// Tagesendbuchung darf nur am Ende sein, Krank und FA dürfen aber auch am Anfang sein
			isEingehalten = !coBuchungsart.isTagesendbuchung() || (coBuchung.getCurrentRowIndex() == anzBuchungen-1)
					|| coBuchungsart.getID() == CoBuchungsart.ID_KRANK || coBuchungsart.getID() == CoBuchungsart.ID_FA;
			// nach der Tagesendbuchungen darf noch ein FA kommen, da FA ignoriert wird und man bei FA ab 14 Uhr auch kurz vorher gehen kann
			if (!isEingehalten && coBuchung.getCurrentRowIndex() == anzBuchungen-2)
			{
				coBuchung.moveNext();
				isEingehalten = (coBuchung.getBuchungsartID() == CoBuchungsart.ID_FA);
				coBuchung.movePrev();
			}
			m_coVerletzerliste.check(isEingehalten, CoMeldungVerletzerliste.MELDUNGID_TAGESENDE_UNZULAESSIG, uhrzeit);

			
			// bei Tagesbuchungen prüfen, ob es die einzige Buchung des Tages ist
			m_coVerletzerliste.check(!coBuchungsart.isTagesbuchungPflicht() || anzBuchungen == 1, CoMeldungVerletzerliste.MELDUNGID_TAGESBUCHUNG_UNZULAESSIG, uhrzeit);

			
			// private Unterbrechung während der Kernarbeitszeit (auch letzte Buchung prüfen, wegen Kommen in der Kernzeit)
			m_coVerletzerliste.check(coBuchungsartLetzteBuchung == null || coBuchungsartLetzteBuchung.getID() !=  CoBuchungsart.ID_PRIVATE_UNTERBRECHUNG 
					|| uhrzeit < beginnKernzeit || uhrzeit > endeKernzeit, 
					CoMeldungVerletzerliste.MELDUNGID_VERLETZUNG_KERNZEIT_PRIV_UNTERBRECHUNG, uhrzeit);
			m_coVerletzerliste.check(coBuchungsart.getID() !=  CoBuchungsart.ID_PRIVATE_UNTERBRECHUNG || uhrzeit < beginnKernzeit || uhrzeit > endeKernzeit, 
					CoMeldungVerletzerliste.MELDUNGID_VERLETZUNG_KERNZEIT_PRIV_UNTERBRECHUNG, uhrzeit);
			
			
			// Arbeitszeitunterbrechung > 30 Minuten während der Kernarbeitszeit
			// letzte Buchung prüfen, da erst beim beenden der Unterbrechung auf 30 Minuten geprüft werden kann
			m_coVerletzerliste.check(coBuchungsartLetzteBuchung == null || coBuchungsartLetzteBuchung.getID() !=  CoBuchungsart.ID_ARBEITSUNTERBRECHUNG 
					|| wertMinuten <= MAX_ARBEITSUNTERBRECHUNG  || (uhrzeit < beginnKernzeit && uhrzeitLetzteBuchung < beginnKernzeit) 
					|| (uhrzeit > endeKernzeit && uhrzeitLetzteBuchung > endeKernzeit), 
					CoMeldungVerletzerliste.MELDUNGID_VERLETZUNG_KERNZEIT_ARBEITSUNTERBRECHUNG, uhrzeit);
			
			
			// Buchungsart für die ein Antrag erstellt werden muss
			if (coBuchungsart.isFreigabeMa())
			{
				// OFA-Arbeitszeit prüfen
				if (buchungsartID == CoBuchungsart.ID_ORTSFLEX_ARBEITEN)
				{
					// nur erste Buchung prüfen, nach Pause kann es weitere OFA-Buchungen geben
					if (isOfaAnfangGeprueft)
					{
						continue;
					}
					isOfaAnfangGeprueft = true;

					// vorläufiges Gehen laden
					coBuchungTmp.loadBuchungVorlaeufig(getPersonID(), datum, CoBuchungsart.ID_GEHEN);
					if (coBuchungTmp.hasRows())
					{
//						m_wertOfaGeplant = coBuchungTmp.getUhrzeitAsInt();
						
						// vorläufiges OFA abziehen
						coBuchungTmp.loadBuchungVorlaeufig(getPersonID(), datum, buchungsartID);
						if (coBuchungTmp.hasRows())
						{
//							m_wertOfaGeplant -= coBuchungTmp.getUhrzeitAsInt();
						}
					}
				}
				
				coBuchungTmp.loadBuchungGenehmigt(getPersonID(), datum, buchungsartID);
				m_coVerletzerliste.check(coBuchungTmp.hasRows(), CoMeldungVerletzerliste.MELDUNGID_BUCHUNG_OHNE_ANTRAG, uhrzeit);
			}
			
		} while (coBuchung.moveNext());
		
		
		// Tagesbuchung
		if (currentField != null && coBuchungsart.isTagesbuchungZulaessig() && anzBuchungen == 1)
		{
			// Krank-Feld auf Anzahl Krank ändern
			if (currentField.equals(getFieldWertKrank()))
			{
				currentField = getFieldAnzahlKrank();
			}
			
			// Sonderfall FA zeitweise und noch keine weitere Buchungen vorhanden -> kein FA eintragen
			if (currentField.equals(getFieldAnzahlFa()) && uhrzeit > CoFirmenparameter.getInstance().getRahmenarbeitszeitBeginnMoDo())
			{
				
			}
			else
			{
				currentField.setValue(1);
			}
			
			// Vorlesung braucht kein Ende als Einzelbuchung -> Sollarbeitszeit
			if (currentField.equals(getFieldVorlesung()) || currentField.equals(getFieldBerufsschule()))
			{
				sollArbeitszeit = m_coPerson.getSollArbeitszeitZeitmodell(datum);
				currentField.setValue(sollArbeitszeit + getDauerPauseMin(sollArbeitszeit));
			}
		}
		

		// wenn keine zu prüfende Buchung gefunden wurde, kann die Prüfung beendet werden
		if (coBuchungsart == null)
		{
			return;
		}

		// letzte Buchung muss Endbuchung oder zulässige Tagesbuchung sein
		m_coVerletzerliste.check(coBuchungsart.isTagesendbuchung() 
				|| (coBuchungsart.isTagesbuchungZulaessig() && anzBuchungen == 1), CoMeldungVerletzerliste.MELDUNGID_TAGESENDE_FEHLT);


		// letzte Buchung prüfen, ob vor Kernzeitende oder nach Rahmenarbeitszeitende liegt
		if (uhrzeit > 0)
		{
			m_coVerletzerliste.check(anzBuchungen == 1 || !coBuchungsart.isTagesendbuchung() || uhrzeit >= endeKernzeit, 
					CoMeldungVerletzerliste.MELDUNGID_VERLETZUNG_KERNZEIT_ENDE, uhrzeit);

			m_coVerletzerliste.check(!coBuchungsart.isTagesendbuchung() || uhrzeit <= CoFirmenparameter.getInstance().getRahmenarbeitszeitEnde(datum), 
					CoMeldungVerletzerliste.MELDUNGID_VERLETZUNG_RAHMENARBEITSZEIT_ENDE, uhrzeit);
		}
		
		
		// Arbeitszeit OFA prüfen
//		if (m_wertOfaGeplant > 0)
//		{
//			if (m_wertOfaGeplant > 9*60)
//			{
//				m_wertOfaGeplant -= 45;
//			}
//			else if (m_wertOfaGeplant > 6*60)
//			{
//				m_wertOfaGeplant -= 30;
//			}
//		}

		
		// bei Dienstreisen muss die Reisezeit angegeben sein
//		updateVerletzerlisteReisezeit();

		
		// wenn krank die letzte Buchung, aber keine Tagesbuchung ist, muss die Arbeitszeit nachträglich auf die Sollarbeitszeit angepasst werden
		m_isKrankLetzteBuchung = anzBuchungen > 1 && coBuchungsart.getID() == CoBuchungsart.ID_KRANK;
	}


	/**
	 * Eintrag wegen fehlender Reisezeit bei Dienstreisen prüfen
	 * 
	 * @throws Exception
	 */
	public void updateVerletzerlisteReisezeit() throws Exception {
		// alten Eintrag löschen und ggf. neuen erstellen
		if (m_coVerletzerliste != null)
		{
			// TODO nur eine Krücke weil sonst die Meldung bei Reisezeit = 0 (weil Weg zur Arbeit länger) immer wieder neu erzeugt wird
//			if (m_coVerletzerliste.moveToMeldungID(CoMeldungVerletzerliste.MELDUNGID_REISEZEIT) 
//					&& m_coVerletzerliste.getStatusID() == CoStatusVerletzung.STATUSID_FREIGEGEBEN)
//			{
//				return;
//			}
			while (m_coVerletzerliste.deleteMeldung(CoMeldungVerletzerliste.MELDUNGID_REISEZEIT))
			{
				// normal sollte es nur eine Meldung geben, falls es mehr gib werden die so gelöscht
			}
			m_coVerletzerliste.check(getWertDienstreise() == 0 || getWertReisezeit() > 0, CoMeldungVerletzerliste.MELDUNGID_REISEZEIT);
		}
	}


	/**
	 * Runde die Uhrzeit bei Tagesbeginn vor Beginn der Rahmenarbeitszeit
	 * 
	 * @param uhrzeit
	 * @param coBuchung
	 * @param coBuchungsart 
	 * @return
	 * @throws Exception
	 */
	private int rundeUhrzeit(int uhrzeit, CoBuchung coBuchung, CoBuchungsart coBuchungsart) throws Exception {
		
		// ab 6.30 (Beginn Rahmenarbeitszeit) Uhr wird erst gezählt, wenn es eine Tagesbeginnbuchung ist
		uhrzeit = Math.max(uhrzeit, CoFirmenparameter.getInstance().getRahmenarbeitszeitBeginn(getDatum()));
		
		// Ab Juli 2019 gibt es die 5 Minuten Rundung nicht mehr
//		// bei der ersten Buchung auf die nächsten 5 Minuten runden
//		if (coBuchung.getCurrentRowIndex() == 0 && coBuchungsart.isTagesbeginnbuchung())
//		{
//			uhrzeit += 4;
//			uhrzeit = uhrzeit - (uhrzeit % 5);
//		}
//		
//		// bei der letzten Buchung auf die letzen 5 Minuten runden, wenn es eine Tagesendbuchung ist
//		if (coBuchung.getCurrentRowIndex() == coBuchung.getRowCount()-1 && coBuchungsart.isTagesendbuchung())
//		{
//			uhrzeit = uhrzeit - (uhrzeit % 5);
//		}
		
		return uhrzeit;
	}


	/**
	 * Bestimme die Pausenänderung auf Basis der Arbeitszeit
	 * 
	 * @param arbeitszeit Arbeitszeit aus Anwesend, Dienstgang, Dienstreise, Vorlesung und Berufsschule
	 * @throws Exception 
	 */
	private void berechnePausenaenderung(int arbeitszeit) throws Exception {
		int pause, pausenAenderung, dauerPauseMin;
		CoZeitmodell coZeitmodell;
		
		coZeitmodell = m_coPerson.getCoZeitmodell(getDatum());
		if (coZeitmodell == null)
		{
			return;
		}
		
		pause = getWertPause();
		dauerPauseMin = getDauerPauseMin(arbeitszeit + pause);

		// Pausenänderung berechnen
		pausenAenderung = Math.max(0, dauerPauseMin - pause); // Pausenänderung darf nicht negativ werden, bei mehr als 30/45 Minuten Pause
		if (pausenAenderung > 0)
		{
			setWertPausenaenderung(pausenAenderung);
		}
		
		// Verletzung bei zu langer Pause
		m_coVerletzerliste.check(pause <= CoFirmenparameter.getInstance().getMaxPausendauer(), CoMeldungVerletzerliste.MELDUNGID_UEBERSCHREITUNG_PAUSE, pause);
	}

	
	 /**
	 * Minimale Pausendauer für die übergebene Arbeitszeit (inkl. Pause) berechnen.
	  * 
	  * @param arbeitszeitMitPause
	  * @throws Exception
	  */
	private int getDauerPauseMin(int arbeitszeitMitPause) throws Exception {
		CoZeitmodell coZeitmodell;
		CoPausenmodell coPausenmodell;
		
		coZeitmodell = m_coPerson.getCoZeitmodell(getDatum());
		if (coZeitmodell == null)
		{
			return 0;
		}
		
		coPausenmodell = coZeitmodell.getCoPausenmodell();
		return coPausenmodell.getDauerPauseMin(arbeitszeitMitPause);
	 }
	 
	 
	/**
	 * Arbeitszeit wegen manuell geänderter Pausenzeit neu berechnen.<br>
	 * Werte werden neu berechnet, da die Arbeitszeit ggf. vorher auf 10 gekürzt wurde.
	 * 
	 * @throws Exception 
	 */
	public void updateArbeitszeitWgPausenaenderung() throws Exception {
//		int differenz, arbeitszeit, maxTagesarbeitszeit;
//		
//		differenz = getWertPausenaenderung() - Format.getIntValue(getFieldPausenaenderung().getOriginalValue());
//		arbeitszeit = Format.getIntValue(getFieldArbeitszeit().getOriginalValue()) - differenz;
//		maxTagesarbeitszeit = CoFirmenparameter.getInstance().getMaxTagesarbeitszeit();
//
//		setWertArbeitszeit(arbeitszeit);
		
		m_coVerletzerliste.deleteMeldung(CoMeldungVerletzerliste.MELDUNGID_UEBERSCHREITUNG_ARBEITSZEIT_TAG);
		berechneArbeitszeit(false);
		
//		m_coVerletzerliste.check(arbeitszeit <= maxTagesarbeitszeit, CoMeldungVerletzerliste.MELDUNG_UEBERSCHREITUNG_ARBEITSZEIT_TAG, arbeitszeit);
	}
	
	 
	/**
	 * Arbeitszeit/Monat wegen manuell geänderter Arbeitszeit/Tag aktualisieren (ist eigentlich ein in der DB berechneter Wert und wird nicht gespeichert)
	 * Berechnung hier nur für die aktuelle Anzeige, um die daten nicht neu laden zu müssen
	 * 
	 * @throws Exception 
	 */
	public void updateArbeitszeitMonat() throws Exception {
		int differenz;
		
		differenz = getWertArbeitszeit() - Format.getIntValue(getFieldArbeitszeit().getOriginalValue());
		
		setWertArbeitszeitMonat(Format.getIntValue(getFieldArbeitszeitMonat().getOriginalValue()) + differenz);
		setWertArbeitszeitWoche(Format.getIntValue(getFieldArbeitszeitWoche().getOriginalValue()) + differenz);
	}
	
	
	/**
	 * Überstunden aus Arbeitszeit und Sollarbeitszeit neu berechnen
	 * 
	 * @throws Exception 
	 */
	public void updateUeberstunden() throws Exception {
		int ueberstunden;
		
		ueberstunden = getWertArbeitszeit() - getWertSollArbeitszeit();
		setWertUeberstunden(ueberstunden);

		// Plus-, Minus- und ReisePlusStunden
		updatePlusMinusStunden();
	}


	/**
	 * Felder für Plus-, Minus-, und ReisePlusStunden aktualisieren
	 */
	public void updatePlusMinusStunden() {
		int reisezeit;
		int plusstundenReise, ueberstunden;
		
		// zuerst alle auf NULL setzen
		setWertPlusstunden(null);
		setWertPlusstundenProjekt(null);
		setWertPlusstundenReise(null);
		setWertMinusstunden(null);

		
		// Plusstunden oder Minusstunden
		ueberstunden = getWertUeberstunden();
		if (ueberstunden > 0)
		{
			setWertPlusstunden(ueberstunden);
			
			// ggf. PlusstundenReise
			plusstundenReise = 0;
			reisezeit = getWertReisezeit();
			if (reisezeit > 0)
			{
				// prüfen, ob es zu Plusstunden gekommen ist
				plusstundenReise = Math.min(ueberstunden, reisezeit);
				if (plusstundenReise > 0)
				{
					setWertPlusstundenReise(plusstundenReise);
				}
			}
			
			// PlusstundenProjekt
			setWertPlusstundenProjekt(ueberstunden - plusstundenReise);
		}
		else if (ueberstunden < 0) // Minusstunden
		{
			setWertMinusstunden(ueberstunden);
		}
	}


	/**
	 * Überstunden Summen Monat/Gesamt wegen geänderten manuell geänderter Überstunden/Tag aktualisieren
	 *  (ist eigentlich ein in der DB berechneter Wert und wird nicht gespeichert)
	 * 
	 * @throws Exception 
	 */
	public void updateUeberstundenSummen() throws Exception {
		int ueberstundenAlt, ueberstundenNeu, differenz, differenzProjekt, differenzReise, plusDifferenz, minusDifferenz;
		int plusstunden, plusstundenProjekt, plusstundenReise;
		int standGleitzeitkonto;
		
		// Differenz zwischen alten und neuen Überstunden berechnen, um diesen Wert auch bei Überstunden/Monat und gesamt zu berücksichtigen
		ueberstundenAlt = Format.getIntValue(getFieldUeberstunden().getOriginalValue());
		ueberstundenNeu = getWertUeberstunden();
		differenz = ueberstundenNeu - ueberstundenAlt;
		plusDifferenz = Math.max(ueberstundenNeu, 0) - Math.max(ueberstundenAlt, 0);
		minusDifferenz = plusDifferenz - differenz;
		differenzProjekt = getWertPlusstundenProjekt() - Format.getIntValue(getFieldPlusstundenProjekt().getOriginalValue());
		differenzReise = getWertPlusstundenReise() - Format.getIntValue(getFieldPlusstundenReise().getOriginalValue());
		
		
		// Überstunden Monat/Gesamt
		setWertUeberstundenMonat(Format.getIntValue(getFieldUeberstundenMonat().getOriginalValue()) + differenz);
		setWertUeberstundenWoche(Format.getIntValue(getFieldUeberstundenWoche().getOriginalValue()) + differenz);
		setWertUeberstundenGesamt(Format.getIntValue(getFieldStandGleitzeitkonto().getOriginalValue()) + differenz);
		
		// Plus-/MinusStunden Monat
		plusstunden = Format.getIntValue(getFieldPlusstundenMonat().getOriginalValue()) + plusDifferenz;
		plusstundenProjekt = Format.getIntValue(getFieldPlusstundenProjektMonat().getOriginalValue()) + differenzProjekt;
		plusstundenReise = Format.getIntValue(getFieldPlusstundenReiseMonat().getOriginalValue()) + differenzReise;
		setWertPlusstundenMonat(plusstunden);
		setWertPlusstundenProjektMonat(plusstundenProjekt);
		setWertPlusstundenReiseMonat(plusstundenReise);
		setWertMinusstundenMonat(Format.getIntValue(getFieldMinusstundenMonat().getOriginalValue()) - minusDifferenz);
		
		// auszahlbare Überstunden
		standGleitzeitkonto = Math.max(getWertUeberstundenGesamt(), 0);
		setWertAuszahlbareUeberstunden(Math.min(standGleitzeitkonto, plusstunden));

		// auszahlbare ÜberstundenProjekt
		setWertAuszahlbareUeberstundenProjekt(Math.min(standGleitzeitkonto, plusstundenProjekt));
		
		// auszahlbare ÜberstundenReise: StandGleitzeitkonto - (Differenz zwischen Reise und normalen PlusStunden -> erst normale PlusStunden auszahlen, ReisePlusStunden wurden abgefeiert)
		setWertAuszahlbareUeberstundenReise(Math.min(Math.max(0, standGleitzeitkonto - plusstunden + plusstundenReise), plusstundenReise));
	}


	private IField getCurrentField(int buchungsartID) {

		switch (buchungsartID)
		{

		case CoBuchungsart.ID_KOMMEN:
			return getFieldAnwesend();

		case CoBuchungsart.ID_ORTSFLEX_ARBEITEN:
			return getFieldAnwesend();

		case CoBuchungsart.ID_DIENSTGANG:
			return getFieldDienstgang();

		case CoBuchungsart.ID_DIENSTREISE:
			return getFieldDienstreise();

		case CoBuchungsart.ID_KGG:
			return getFieldAnwesend();

		case CoBuchungsart.ID_VORLESUNG:
			return getFieldVorlesung();

		case CoBuchungsart.ID_BERUFSSCHULE:
			return getFieldBerufsschule();

		case CoBuchungsart.ID_PAUSE:
			return getFieldPause();

		case CoBuchungsart.ID_PAUSENENDE:
			return getFieldAnwesend();

		case CoBuchungsart.ID_ARBEITSUNTERBRECHUNG:
			return getFieldArbeitsunterbrechung();

		case CoBuchungsart.ID_PRIVATE_UNTERBRECHUNG:
			return getFieldPrivateUnterbrechung();

		case CoBuchungsart.ID_KRANK:
			return getFieldWertKrank();


		// Tagesbuchungen
		case CoBuchungsart.ID_URLAUB:
			return getFieldAnzahlUrlaub();

		case CoBuchungsart.ID_SONDERURLAUB:
			return getFieldAnzahlSonderurlaub();

		case CoBuchungsart.ID_ELTERNZEIT:
			return getFieldAnzahlElternzeit();

		case CoBuchungsart.ID_FA:
			return getFieldAnzahlFa();

		case CoBuchungsart.ID_KRANK_OHNE_LFZ:
			return getFieldAnzahlKrankOhneLfz();

		case CoBuchungsart.ID_BEZ_FREISTELLUNG:
			return getFieldAnzahlBezFreistellung();

		
		// Gehe-Buchung
		case CoBuchungsart.ID_GEHEN:
			return null;

		case CoBuchungsart.ID_ENDE_DIENSTGANG_DIENSTREISE:
			return null;

		}

		return null;
	}


	/**
	 * Bezeichnung der Tagesbuchung, wenn es eine ist für den aktuellen Tag
	 * 
	 * @return Bezeichnung der Buchungsart oder null, wenn es keine Tagesbuchung ist
	 * @throws Exception
	 */
	public String getBuchungsartTagesbuchung() throws Exception {
		int buchungsartID;
		String buchungsart;
		
		buchungsartID = 0;
		buchungsart = null;
		
		
		if (getAnzahlUrlaub() > 0)
		{
			buchungsartID = CoBuchungsart.ID_URLAUB;
		}

		if (getAnzahlSonderurlaub() > 0)
		{
			buchungsartID = CoBuchungsart.ID_SONDERURLAUB;
		}

		if (getAnzahlElternzeit() > 0)
		{
			buchungsartID = CoBuchungsart.ID_ELTERNZEIT;
		}

		if (getAnzahlFa() > 0)
		{
			buchungsartID = CoBuchungsart.ID_FA;
		}

		if (getAnzahlKrank() > 0)
		{
			buchungsartID = CoBuchungsart.ID_KRANK;
		}

		if (getAnzahlKrankOhneLfz() > 0)
		{
			buchungsartID = CoBuchungsart.ID_KRANK_OHNE_LFZ;
		}

		if (getAnzahlBezFreistellung() > 0)
		{
			buchungsartID = CoBuchungsart.ID_BEZ_FREISTELLUNG;
		}

		
		if (buchungsartID > 0)
		{
			buchungsart = CoBuchungsart.getInstance().getBezeichnung(buchungsartID);
		}
		
		return buchungsart;
	}


	/**
	 * Anzahl Urlaubstage für die Person zum übergebenen Datum suchen
	 * 
	 * @param personID
	 * @param datum
	 * @param datumBeruecksichtigen  Urlaub an aktuellem Tag berücksichtigen
	 * @return 
	 * @throws Exception 
	 */
	public static int getAnzahlUrlaubstage(int personID, Date datum, boolean datumBeruecksichtigen) throws Exception {
		CoKontowert coKontowert;
		
		coKontowert = new CoKontowert();
		coKontowert.load(personID, datum);
		
		// wenn das aktuelle Datum berücksichtigt werden soll, muss geprüft werden ob es ein Urlaubstag is (AnzahlUrlaub)
		return coKontowert.getResturlaub() + (datumBeruecksichtigen ? coKontowert.getAnzahlUrlaub() : 0);
	}


	/**
	 * Es muss ein Grund für die Änderung angegeben werden
	 * 
	 * @see pze.business.objects.AbstractCacheObject#validate()
	 */
	@Override
	public String validate() throws Exception{
		int iField, anzFields;
		Date datumLetzteAuszahlung;

		if (!isModified())
		{
			return null;
		}

		// Datum der letzten Auszahlung prüfen, da danach keine Änderungen gemacht werden dürfen
		datumLetzteAuszahlung = checkDatumLetzteAuszahlung();
		if (datumLetzteAuszahlung != null)
		{
			return "Es können keine Buchungen vor dem " + Format.getString(datumLetzteAuszahlung)
			+ " (Datum der letzten Auszahlung) gespeichert werden. Bitte Administrator/Buchhaltung kontaktieren.";
		}
		
		
		// Kein Grund für die Änderung angegeben
		if (getGrundAenderungID() == 0)
		{
			// prüfen, ob ein Feld geändert wurde oder nur ein child (Verletzerliste)
			anzFields = getFieldCount();
			for (iField=0; iField<anzFields; ++iField)
			{
				if (getField(iField).getState() == IBusinessObject.statusChanged)
				{
					return "Es wurde kein Grund für die Bearbeitung angegeben.";
				}
			}
		}
		
		// wenn ein Datensatz in der Zukunft (oder heute) gespeichert werden soll, gebe eine Warnung aus
		if (Format.getDate12Uhr(getDatum()).after(Format.getDate0Uhr(new Date())))
		{
			Messages.showWarningMessage("Die geänderten Kontowerte liegen in der Zukunft. "
					+ "Durch Buchungen für diesen Tag werden die Kontodaten ggf. überschrieben.");
		}

		return null;
	}


	/**
	 * Prüft die Angaben zu den auszuzahlenden Überstunden auf Korrektheit.
	 * 
	 * @param dialog
	 * @return
	 * @throws Exception
	 */
	public String validateAngabenAuszahlung() throws Exception {
		int auszahlungProjektstunden;
		int auszahlungReisestunden;
		int wertAuszahlbareUeberstundenProjekt;
		int wertAuszahlbareUeberstundenReise;
		
		auszahlungProjektstunden = getWertAuszahlungUeberstundenProjekt();
		auszahlungReisestunden = getWertAuszahlungUeberstundenReise();
		wertAuszahlbareUeberstundenProjekt = getWertAuszahlbareUeberstundenProjekt();
		wertAuszahlbareUeberstundenReise = getWertAuszahlbareUeberstundenReise();
		
		
		// Stunden > 0
		if (auszahlungProjektstunden == 0 && auszahlungReisestunden == 0)
		{
			return "Bitte eine Anzahl Stunden > 0 zur Auszahlung eintragen oder die Option \"keine Auszahlung\" wählen.";
		}

		// nur ganze Stunden
		if (!checkAuszahlungGanzzahl(auszahlungProjektstunden) || !checkAuszahlungGanzzahl(auszahlungReisestunden))
		{
			return "Es können nur volle Stunden ausgezahlt werden.";
		}

		// maximal 30 Stunden
		if (auszahlungProjektstunden + auszahlungReisestunden > MAX_AUSZAHLUNG_STUNDEN)
		{
			return "Es können maximal 30 Stunden pro Monat ausgezahlt werden.";
		}

		// nicht mehr als die auszahlbaren Projektstunden
		if (auszahlungProjektstunden > wertAuszahlbareUeberstundenProjekt)
		{
			return "Es können nicht mehr als die auszahlbaren Projektstunden ausgezahlt werden.";
		}
		
		// nicht mehr als die auszahlbaren Reisestunden
		if (auszahlungReisestunden > wertAuszahlbareUeberstundenReise)
		{
			return "Es können nicht mehr als die auszahlbaren Reisestunden ausgezahlt werden.";
		}
		
		return null;
	}


	/**
	 * Prüft, ob die Auszahlung eine Ganzzahl und nicht negativ ist
	 * 
	 * @param auszahlung
	 * @return
	 */
	private boolean checkAuszahlungGanzzahl(int auszahlung) {
		return auszahlung >= 0 && auszahlung % 60 == 0;
	}


	/**
	 * Erweitert die übergebene Liste der fehlenden Felder um die im akt. Cacheobjekt fehlenden
	 * 
	 * @param felder	Felder, die aus anderen COs Fehler haben
	 * @param schonGeprueft Liste der bereits geprüften Felder
	 * @return Felder, die als Pflichtfelder nicht ausgefüllt sind.
	 */
	@Override
	public String appendPflichtfelderFehler(String felder, HashSet<IField> schonGeprueft) {
		if (getDatum() == null)
		{
			return felder;
		}

		return super.appendPflichtfelderFehler(felder, schonGeprueft);
	}


	/**
	 * Zu der Zeile mit dem Tag wechseln
	 * 
	 * @param tag
	 * @return
	 */
	public boolean moveToTag(int tag) {
		return moveTo(getDatum(tag), getFieldDatum().getFieldDescription().getResID());
	}


	/**
	 * Datum für den übergebenen Tag des Monats bestimmen
	 * 
	 * @param tagDesMonats
	 * @return
	 */
	private Date getDatum(int tagDesMonats) {
		Date datum;
		GregorianCalendar gregDatum;
		
		datum = getDatum();
		if (datum == null)
		{
			return null;
		}
		
		gregDatum = new GregorianCalendar();
		gregDatum.setTime(datum);
		gregDatum.set(Calendar.DAY_OF_MONTH, tagDesMonats);
		
		return new Timestamp(gregDatum.getTimeInMillis());
	}


	private IField getVirtFieldTag() {
		return getField("virt.field.tblkontowert.tag");
	}


	public int getVirtTag() {
		return Format.getIntValue(getVirtFieldTag().getValue());
	}


	private IField getVirtFieldMonat() {
		return getField("virt.field.tblkontowert.monat");
	}


	public int getVirtMonat() {
		return Format.getIntValue(getVirtFieldMonat().getValue());
	}

}
