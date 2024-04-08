package pze.business.objects.personen;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.CoZeitmodell;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungBuchhaltungDienstreisen;
import pze.business.objects.auswertung.CoAuswertungDienstreisen;
import pze.business.objects.auswertung.CoAuswertungFreigabe;
import pze.business.objects.auswertung.CoAuswertungUrlaub;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.dienstreisen.CoDienstreisezeit;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.CoLandDienstreise;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoBuchungserfassungsart;
import pze.business.objects.reftables.buchungen.CoBuchungstyp;
import pze.business.objects.reftables.buchungen.CoGrundAenderungBuchung;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.business.objects.reftables.buchungen.CoSystembuchungsmeldung;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.business.objects.reftables.personen.CoPosition;
import pze.ui.formulare.freigabecenter.FormFreigabecenterDr;
import pze.ui.formulare.freigabecenter.FormFreigabecenterUrlaub;


/**
 * CacheObject für Buchungen der PZE-Datenbank
 * 
 * @author Lisiecki
 *
 */
public class CoBuchung extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblbuchung";
	
	private static final int PUFFER_URLAUBSTAGE = 5;

//	private CoPerson m_coPerson;

	
	/**
	 * Kontruktor
	 */
	public CoBuchung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Neuen Datensatz mit neuem Primärschlüssel erzeugen
	 * 
	 * @return neue ID
	 * @throws Exception
	 */
	public int createNew() throws Exception	{
		int id = super.createNew();
		
		setErstelltAm(new Date());
		
		return id;
	}


	@Override
	public String getNavigationBitmap() {
		return "book.edit";
	}


	/**
	 * Sortiert nach Datum
	 * 
	 * (non-Javadoc)
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "YEAR(datum), MONTH(datum), DAY(datum), uhrzeitAsInt, ID";
//		return "YEAR(datum), MONTH(datum), DAY(datum), uhrzeitAsInt, bnr, ID";
	}
	
	
	/**
	 * Sortierung der Anträge nach Namen, dann nach Datum
	 * 
	 * @return
	 */
	private String getSortFieldNameAntraege() {
		return "p.Nachname, p.Vorname, YEAR(b.Datum), MONTH(b.Datum), DAY(b.Datum)";
	}
	

	/**
	 * Über Primärschlüssel laden
	 * @param id	Primärschlüssel
	 * @throws Exception 
	 */
	@Override
	public void loadByID(int id) throws Exception {
		load(null, null, id, false);
	}
	

	/**
	 * Alle Buchungen einer Dienstreise laden, unabhängig vom Status 
	 * @param id	Primärschlüssel
	 * @throws Exception 
	 */
	public void loadByDienstreiseID(int id) throws Exception {
		String sql;

		sql = "SELECT * FROM " + TABLE_NAME + " b LEFT OUTER JOIN tblDienstreisezeit z ON (b.ID=z.BuchungID) WHERE DienstreiseID=" + id 
				+ " ORDER BY b.Datum, uhrzeitAsInt, b.ID";

		if (getField(getResIdFieldUhrzeitBis()) == null)
		{
			addFields("table.tbldienstreisezeit");
		}
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
		loadAntragEnde();
	}
	

	/**
	 * Lade alle Buchungen zumm aktuellen Tag
	 * 
	 * @return
	 * @throws Exception
	 */
	public void loadAllFromToday() throws Exception{
		load(null, new Date(), 0, false);
	}


	/**
	 * Lade alle Buchungen für die Person zu dem angegebenen Tag
	 * 
	 * @param coPerson CO der Person
	 * @param datum
	 * @throws Exception
	 */
	public void loadGueltig(CoPerson coPerson, Date datum) throws Exception {
		load(coPerson, datum, 0, true);
	}

	
	/**
	 * Lade alle Buchungen für die Person zu dem angegebenen Tag bzw. die mit der übergebenen ID.<br>
	 * Die Angabe eines Parameters ist ausreichend.
	 * 
	 * @param coPerson CO der Person
	 * @param datum Datum der Buchungen
	 * @param id  ID der Buchung
	 * @param nurGueltig nur gültige Einträge laden (OK und geändert)
	 * @throws Exception
	 */
	public void load(CoPerson coPerson, Date datum, int id, boolean nurGueltig) throws Exception {
		load((coPerson != null ? coPerson.getID() : 0), datum, id, nurGueltig, false);
	}

	
	/**
	 * Lade alle Buchungen für die Person zu dem angegebenen Tag bzw. die mit der übergebenen ID.<br>
	 * Die Angabe eines Parameters ist ausreichend.
	 * 
	 * @param coPerson CO der Person
	 * @param datum Datum der Buchungen
	 * @param id  ID der Buchung
	 * @param nurGueltig nur gültige Einträge laden (OK und geändert)
	 * @throws Exception
	 */
	public void loadNichtGeloescht(int personID, Date datum) throws Exception {
		load(personID, datum, 0, false, true);
	}

	
	/**
	 * Lade alle Buchungen für die Person zu dem angegebenen Tag bzw. die mit der übergebenen ID.<br>
	 * Die Angabe eines Parameters ist ausreichend.
	 * 
	 * @param coPerson CO der Person
	 * @param datum Datum der Buchungen
	 * @param id  ID der Buchung
	 * @param nurGueltig nur gültige Einträge laden (OK und geändert)
	 * @param nichtGeloescht alle nicht gelöscht/als ungültig markierten Eintrage 
	 * @throws Exception
	 */
	public void load(int personID, Date datum, int id, boolean nurGueltig, boolean nichtGeloescht) throws Exception {
		String sql, where;
		GregorianCalendar gregDatum;

		gregDatum = new GregorianCalendar();
		if (datum != null)
		{
			gregDatum.setTime(datum);
		}

		where = "";
		where += (datum != null ? (" AND YEAR(datum)=" + gregDatum.get(Calendar.YEAR)  + " AND MONTH(datum)=" + (gregDatum.get(Calendar.MONTH) + 1) 
				+ " AND DAY(datum)=" + gregDatum.get(Calendar.DAY_OF_MONTH) ) : "")

				+ (personID != 0 ? " AND b.PersonID=" + personID : "")

				+ (id > 0 ? " AND b.ID=" + id : "")

				+ (nurGueltig ? " AND (b.StatusID=" + CoStatusBuchung.STATUSID_OK + " OR b.StatusID=" + CoStatusBuchung.STATUSID_GEAENDERT + ")"
						+ " AND buchungsartID IS NOT NULL" : "")

				+ (nichtGeloescht ? " AND (b.StatusID <> " + CoStatusBuchung.STATUSID_UNGUELTIG + ")" + " AND buchungsartID IS NOT NULL" : "");

		sql = "SELECT b.* FROM " + getTableName() + " b LEFT OUTER JOIN tblPerson p ON (b.PersonID=p.ID)"
				+ " WHERE " + where.substring(4) // erstes "AND" abschneiden
				+ " ORDER BY " + getSortFieldName();

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Nächste Buchung für eine Person ab dem nächsten Tag suchen
	 * 
	 * @param personID
	 * @throws Exception
	 */
	public void loadNext(int personID, Date datum) throws Exception{
		load(personID, datum, 1);
	}
	

	/**
	 * letzte Buchung für eine Person vor dem heutigen Tag suchen
	 * 
	 * @param personID
	 * @param datum 
	 * @throws Exception
	 */
	public void loadLast(int personID, Date datum) throws Exception{
		load(personID, datum, -1);
	}
	

	/**
	 * Buchungen einer Person für einen Arbeitstag laden (z. B. vor oder nach dem heutigen Tag)
	 * 
	 * @param personID
	 * @param datum startdatum
	 * @param datumsShift das Datum wird um diesen Wert verschoben, bis ein Arbeitstag gefunden wurde
	 * @throws Exception
	 */
	public void load(int personID, Date datum, int datumsShift) throws Exception{
		String sql;
		GregorianCalendar gregDatum;
		CoPerson coPerson;
		
		coPerson = new CoPerson();
		coPerson.loadByID(personID);
		
		// Arbeitstag bestimmen
		if (!coPerson.getCoPersonZeitmodell().isEmpty())
		{
			do
			{
				datum = Format.getDateVerschoben(datum, datumsShift);
			} while (!coPerson.isArbeitstag(datum));
		}
		gregDatum = Format.getGregorianCalendar(datum);

		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID + " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + ""
				+ " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH)+1) + " AND DAY(Datum)=" + (gregDatum.get(Calendar.DAY_OF_MONTH))
				+ " AND (StatusID=" + CoStatusBuchung.STATUSID_OK + " OR StatusID=" + CoStatusBuchung.STATUSID_GEAENDERT + ")";
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
	 * 
	 * @param personID ID oder 0 für alle Personen
	 * @param statusGenehmigungID 
	 * @throws Exception
	 */
	public void loadGeplantOfa(int personID) throws Exception{
		removeField("virt.field.buchung.uhrzeitbis");
		removeField(getResIdFreigabeMoeglich());
		emptyCache();

		Application.getLoaderBase().load(this, 
				(personID > 0 ? "PersonID=" + personID + " AND" : "")
				+ " (StatusGenehmigungID=" + CoStatusGenehmigung.STATUSID_GEPLANT
				+  " OR StatusGenehmigungID=" + CoStatusGenehmigung.STATUSID_ABGELEHNT + ")"
				+ " AND StatusID<>" + CoStatusBuchung.STATUSID_UNGUELTIG
				+ " AND BuchungsartID=" + CoBuchungsart.ID_ORTSFLEX_ARBEITEN
//				+ " AND BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeMA=1)"
				, getSortFieldName());

		// End-Zeit der Anträge laden
		loadAntragEnde();
		
		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}

	// diese 3 Methoden zusammenführen

	/**
	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
	 * 
	 * @param personID ID oder 0 für alle Personen
	 * @param statusGenehmigungID 
	 * @throws Exception
	 */
	public void loadAntraegeAktuellOfa(int personID) throws Exception{ // mit oberer Funktion zusammenlegen, hier nur OFA
		removeField("virt.field.buchung.uhrzeitbis");
		removeField(getResIdFreigabeMoeglich());
		emptyCache();

		Application.getLoaderBase().load(this, 
				(personID > 0 ? "PersonID=" + personID + " AND" : "")
				+ " StatusID<>" + CoStatusBuchung.STATUSID_UNGUELTIG
				+ " AND BuchungsartID=" + CoBuchungsart.ID_ORTSFLEX_ARBEITEN
//				+ " AND BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeMA=1)"
				+ " AND StatusGenehmigungID<>" + CoStatusGenehmigung.STATUSID_GEPLANT
				+ " AND StatusGenehmigungID<>" + CoStatusGenehmigung.STATUSID_GENEHMIGT
				, getSortFieldName());

		// End-Zeit der Anträge laden
		loadAntragEnde();
		
		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}

	// TODO 3 löschen, ersetzt durch folgende
//	public void loadAntraegeGeplant(int personID, boolean loadUrlaub) throws Exception{
//		loadAntraege(personID, loadUrlaub, 0, CoStatusBuchung.STATUSID_UNGUELTIG, CoStatusGenehmigung.STATUSID_GEPLANT, 0, 0, 0, 0);
//	}
//	
//	
//	public void loadAntraegeAktuell(int personID, boolean loadUrlaub) throws Exception{
//		loadAntraege(personID, loadUrlaub, 0, 0, 0, 0, CoStatusGenehmigung.STATUSID_GEPLANT, 
//				CoStatusGenehmigung.STATUSID_GENEHMIGT, CoStatusGenehmigung.STATUSID_GELOESCHT);
//	}
//	
//	
//	public void loadAntraegeAbgeschlossen(int personID, Date datum, Date datumBis, boolean loadUrlaub) throws Exception{
//		loadAntraege(personID, datum, datumBis, loadUrlaub, CoStatusBuchung.STATUSID_UNGUELTIG, 0, CoStatusGenehmigung.STATUSID_GENEHMIGT, CoStatusGenehmigung.STATUSID_GELOESCHT, 0, 0, 0);
//	}
	
	
	public void loadAntraegeGeplant(int personID, String buchungsartID, boolean loadUrlaub) throws Exception{
		loadAntraege(personID, buchungsartID, loadUrlaub, 0, CoStatusBuchung.STATUSID_UNGUELTIG, CoStatusGenehmigung.STATUSID_GEPLANT, 0, 0, 0, 0);
	}
	
	
	public void loadAntraegeAktuell(int personID, String buchungsartID, boolean loadUrlaub) throws Exception{
		loadAntraege(personID, buchungsartID, loadUrlaub, 0, 0, 0, 0, CoStatusGenehmigung.STATUSID_GEPLANT, 
				CoStatusGenehmigung.STATUSID_GENEHMIGT, CoStatusGenehmigung.STATUSID_GELOESCHT);
	}
	
	
	public void loadAntraegeAbgeschlossen(int personID, Date datum, Date datumBis, String buchungsartID, boolean loadUrlaub) throws Exception{
		loadAntraege(personID, datum, datumBis, buchungsartID, loadUrlaub, CoStatusBuchung.STATUSID_UNGUELTIG, 0, CoStatusGenehmigung.STATUSID_GENEHMIGT, CoStatusGenehmigung.STATUSID_GELOESCHT, 0, 0, 0);
	}
	
	
//	/**
//	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
//	 * 
//	 * @param personID ID oder 0 für alle Personen
//	 * @param statusGenehmigungID 
//	 * @throws Exception
//	 */
//	private void loadAntraege(int personID, boolean loadUrlaub, int statusID, int notStatusID, int statusGenehmigungID1, int statusGenehmigungID2, 
//			int notStatusGenehmigungID1, int notStatusGenehmigungID2, int notStatusGenehmigungID3) throws Exception{
//		
//		loadAntraege(personID, null, null, loadUrlaub, statusID, notStatusID, statusGenehmigungID1, statusGenehmigungID2, 
//				notStatusGenehmigungID1, notStatusGenehmigungID2, notStatusGenehmigungID3);
//	}
	
	
	/**
	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
	 * 
	 * @param personID ID oder 0 für alle Personen
	 * @param statusGenehmigungID 
	 * @throws Exception
	 */
	private void loadAntraege(int personID, String buchungsartID, boolean loadUrlaub, int statusID, int notStatusID, int statusGenehmigungID1, int statusGenehmigungID2, 
			int notStatusGenehmigungID1, int notStatusGenehmigungID2, int notStatusGenehmigungID3) throws Exception{
		
		loadAntraege(personID, null, null, buchungsartID, loadUrlaub, statusID, notStatusID, statusGenehmigungID1, statusGenehmigungID2, 
				notStatusGenehmigungID1, notStatusGenehmigungID2, notStatusGenehmigungID3);
	}
	
	
//	/**
//	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
//	 * 
//	 * @param personID ID oder 0 für alle Personen
//	 * @param statusGenehmigungID 
//	 * @throws Exception
//	 */
//	private void loadAntraege(int personID, Date datum, Date datumBis, boolean loadUrlaub, int statusID, int notStatusID, 
//			int statusGenehmigungID1, int statusGenehmigungID2, 
//			int notStatusGenehmigungID1, int notStatusGenehmigungID2, int notStatusGenehmigungID3) throws Exception{
//		String sql, whereDatum, teil1, teil2;
//		CoBuchung coBuchungUrlaubsplanung;
//		
//		coBuchungUrlaubsplanung = new CoBuchung();
//
//		whereDatum = CoAuswertung.getWhereDatum(datum, datumBis);
//		
//		// SQL-Statement zusammenbauen
//		sql = " BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeMA=1) "
//				+ "AND BuchungsartID " + (loadUrlaub ? "" : " NOT ") + " IN ("
//				+ CoBuchungsart.ID_URLAUB + ", " + CoBuchungsart.ID_SONDERURLAUB + ", " + CoBuchungsart.ID_FA + ") "
//				+ (personID > 0 ? " AND PersonID=" + personID : "")
//				+ (whereDatum != null ? " AND (" + whereDatum + ")" : "")
//				+ (statusID == 0 ? "" : " AND StatusID=" + statusID)
//				+ (notStatusID == 0 ? "" : " AND StatusID<>" + notStatusID)
//				+ (statusGenehmigungID1 == 0 ? "" : " AND (StatusGenehmigungID=" + statusGenehmigungID1)
//				+ (statusGenehmigungID2 == 0 ? "" : " OR StatusGenehmigungID=" + statusGenehmigungID2)
//				+ (statusGenehmigungID1 == 0 ? "" : ")")
//				+ (notStatusGenehmigungID1 == 0 ? "" : " AND StatusGenehmigungID<>" + notStatusGenehmigungID1)
//				+ (notStatusGenehmigungID2 == 0 ? "" : " AND StatusGenehmigungID<>" + notStatusGenehmigungID2)
//				+ (notStatusGenehmigungID3 == 0 ? "" : " AND StatusGenehmigungID<>" + notStatusGenehmigungID3)
//				;
//
//		// wenn ein Status angegeben wurde müssen alle Buchungen mit dem Status geladen werden
//		if (statusID > 0)
//		{
//			teil1 = sql.substring(0, sql.indexOf("StatusID="));
//			teil2 = sql.substring(sql.indexOf("StatusID="));
//			
//			// alle weiteren Bedingungen zum GenehmigungsStatus sind dann optional
//			if (teil2.contains("AND"))
//			{
//				sql = teil1 + " ( " + teil2.replaceFirst("AND", "OR (") + "))";
//			}
//		}
//
//		
//		// Daten laden
//		removeField("virt.field.buchung.uhrzeitbis");
//		removeField(getResIdFreigabeMoeglich());
//		emptyCache();
//		Application.getLoaderBase().load(loadUrlaub ? coBuchungUrlaubsplanung : this, sql, getSortFieldName());
//
//		
//		// End-Zeit der Anträge laden
//		if (loadUrlaub)
//		{
//			loadUrlaubsuebersicht(coBuchungUrlaubsplanung);
//		}
//		else
//		{
//			loadAntragEnde();
//		}
//		
//		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
//		setModified(false);
//	}
	
	
	/**
	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
	 * 
	 * @param personID
	 * @param datum
	 * @param datumBis
	 * @param buchungsartID String mit allen zulässigen Buchungsarten
	 * @param loadUrlaub
	 * @param statusID
	 * @param notStatusID
	 * @param statusGenehmigungID1
	 * @param statusGenehmigungID2
	 * @param notStatusGenehmigungID1
	 * @param notStatusGenehmigungID2
	 * @param notStatusGenehmigungID3
	 * @throws Exception
	 */
	private void loadAntraege(int personID, Date datum, Date datumBis, String buchungsartID, boolean loadUrlaub, int statusID, int notStatusID, 
			int statusGenehmigungID1, int statusGenehmigungID2, 
			int notStatusGenehmigungID1, int notStatusGenehmigungID2, int notStatusGenehmigungID3) throws Exception{
		String sql, where, whereDatum, teil1, teil2;
		CoBuchung coBuchungTmp;
		
		coBuchungTmp = new CoBuchung();

		whereDatum = CoAuswertung.getWhereDatum(datum, datumBis);
		
		// SQL-Statement zusammenbauen
		where = " BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeMA=1) "
				+ "AND BuchungsartID IN (" + buchungsartID + ") "
				+ (personID > 0 ? " AND PersonID=" + personID : "")
				+ (whereDatum != null ? " AND (" + whereDatum + ")" : "")
				+ (statusID == 0 ? "" : " AND StatusID=" + statusID)
				+ (notStatusID == 0 ? "" : " AND StatusID<>" + notStatusID)
				+ (statusGenehmigungID1 == 0 ? "" : " AND (StatusGenehmigungID=" + statusGenehmigungID1)
				+ (statusGenehmigungID2 == 0 ? "" : " OR StatusGenehmigungID=" + statusGenehmigungID2)
				+ (statusGenehmigungID1 == 0 ? "" : ")")
				+ (notStatusGenehmigungID1 == 0 ? "" : " AND StatusGenehmigungID<>" + notStatusGenehmigungID1)
				+ (notStatusGenehmigungID2 == 0 ? "" : " AND StatusGenehmigungID<>" + notStatusGenehmigungID2)
				+ (notStatusGenehmigungID3 == 0 ? "" : " AND StatusGenehmigungID<>" + notStatusGenehmigungID3)
				;

		// wenn ein Status angegeben wurde müssen alle Buchungen mit dem Status geladen werden
		if (statusID > 0)
		{
			teil1 = where.substring(0, where.indexOf("StatusID="));
			teil2 = where.substring(where.indexOf("StatusID="));
			
			// alle weiteren Bedingungen zum GenehmigungsStatus sind dann optional
			if (teil2.contains("AND"))
			{
				where = teil1 + " ( " + teil2.replaceFirst("AND", "OR (") + "))";
			}
		}

		
		// Dienstreisen mit Zusatzfeldern laden 
		if (buchungsartID.startsWith(CoBuchungsart.ID_DIENSTREISE + ",") || buchungsartID.startsWith(CoBuchungsart.ID_DIENSTGANG + ","))
		{
			coBuchungTmp.addFields("table." + CoDienstreise.TABLE_NAME);
//			coBuchung.addField(CoDienstreise.getResIdZielID());
//			coBuchung.addField(CoDienstreise.getResIdZweckID());
//			coBuchung.addField(CoDienstreise.getResIdThema());

			// zusätzliche Felder für die Anzeige der DR hinzufügen, falls noch nicht vorhanden
			if (getField(CoDienstreise.getResIdZielID()) == null)
			{
				addFields("table." + CoDienstreise.TABLE_NAME);
//				addField(CoDienstreise.getResIdZielID());
//				addField(CoDienstreise.getResIdZweckID());
//				addField(CoDienstreise.getResIdThema());
				addField("virt.field.buchung.datumbis");
				addField("virt.field.buchung.uhrzeitbis");
			}
			
			// SQL-Statement für Zusatzfelder anpassen
			sql = "SELECT d.Bemerkung, b.*, d.* FROM " + getTableName() + " b " // DR-Bemerkung zuerst, damit nicht die der Buchung angezeigt wird
					+ " JOIN tblDienstreise d on (d.ID=b.DienstreiseID) " 
					+ " WHERE " + where.replace("PersonID", "b.PersonID").replace("StatusID", "b.StatusID") 
					+ " ORDER BY d.ID, b.StatusID, b.StatusGenehmigungID, b.datum, b.ID";

			coBuchungTmp.emptyCache();
			Application.getLoaderBase().load(coBuchungTmp, sql);
			
			// aus Tagesbuchungen Zeiträume laden
			loadDrDgUebersicht(coBuchungTmp, false);
			commit();
		}
		else // sonst normales Laden für Urlaub und OFA
		{
			// Daten laden
			removeField("virt.field.buchung.uhrzeitbis");
			removeField(getResIdFreigabeMoeglich());
			emptyCache();

			Application.getLoaderBase().load((loadUrlaub ? coBuchungTmp : this), where, getSortFieldName());

			// End-Zeit der Anträge laden
			if (loadUrlaub)
			{
				loadUrlaubsuebersicht(coBuchungTmp);
				commit();
			}
			else
			{
				loadAntragEnde();
			}
		}
		
		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}
	

	// TODO diese 3 Methoden zusammenführen bzw löschen

	/**
	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
	 * 
	 * @param personID ID oder 0 für alle Personen
	 * @param statusGenehmigungID 
	 * @throws Exception
	 */
	public void loadBeantragtOfa(int personID) throws Exception{
		removeField("virt.field.buchung.uhrzeitbis");
		removeField(getResIdFreigabeMoeglich());
		emptyCache();

		Application.getLoaderBase().load(this, 
				(personID > 0 ? "PersonID=" + personID + " AND" : "")
				+ " StatusGenehmigungID<>" + CoStatusGenehmigung.STATUSID_GEPLANT
				+ " AND BuchungsartID=" + CoBuchungsart.ID_ORTSFLEX_ARBEITEN
//				+ " AND BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeMA=1)"
				, "Datum DESC");

		// End-Zeit der Anträge laden
		loadAntragEnde();
		
		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}


	/**
	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
	 * 
	 * @param personID ID oder 0 für alle Personen
	 * @param statusGenehmigungID 
	 * @param statusGenehmigungID3 
	 * @param statusGenehmigungID4 
	 * @throws Exception
	 */
	public void loadAntraegeAl(Date datum, Date datumBis, int statusGenehmigungID, int statusGenehmigungID2, int statusGenehmigungID3, 
			int statusGenehmigungID4, boolean zeitraum, boolean nurOfa, boolean nurDr) throws Exception{
		int personID;
		String sql, whereDatum, whereFreigabeBerechtigungen;
		CoFreigabeberechtigungen coFreigabeberechtigungen;
		
		personID = UserInformation.getPersonID();
		
		// Einschränkung
		whereDatum = CoAuswertung.getWhereDatum(datum, datumBis);
		coFreigabeberechtigungen = new CoFreigabeberechtigungen();
		whereFreigabeBerechtigungen = coFreigabeberechtigungen.createWhere(personID, !nurOfa && !nurDr);
		
		// Berechtigungen zur Freigabe vorhanden
		if (whereFreigabeBerechtigungen == null)
		{
			emptyCache();
			return;
		}
		
				
		// SQL-Statement zusammensetzen
		sql = 
//				(nurDr && coFreigabeberechtigungen.isDrAuslandFreigabeErlaubt() ? "" : " (" + whereFreigabeBerechtigungen + ") AND ") // Auslands-DR für alle 
//				+
				(whereDatum != null ? " (" + whereDatum + ") AND " : "")
				+ " BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeMA=1)"
				+ " AND PersonID<> " + UserInformation.getPersonID() + " AND ("
				
				// als AL
				+ " ("
				+ " (" + whereFreigabeBerechtigungen + ") AND "
				+ " (StatusGenehmigungID=" + statusGenehmigungID 
				+ (statusGenehmigungID2 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID2 : "")
				+ (statusGenehmigungID3 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID3 : "")
				+ (statusGenehmigungID4 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID4 : "")
				+ ")" 
				+ " AND BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeAL=1)"
				+ ")"
				
				// für Sekretärinnen, außer Abteilung Verwaltung
				// AL dürfen nur einfache Freigabe erteilen
				+ (coFreigabeberechtigungen.isFreigabeVerwaltungErlaubt() ?
						" OR ("
						+ " (StatusGenehmigungID=" + statusGenehmigungID + " OR StatusGenehmigungID=" + CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AL 
						+ (statusGenehmigungID2 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID2 : "")
						+ (statusGenehmigungID3 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID3 : "")
						+ (statusGenehmigungID4 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID4 : "")
						+ ")" 
						+ " AND BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeV=1)"
						+ " AND AbteilungID<>" + CoAbteilung.ID_VERWALTUNG + " AND PositionID=" + CoPosition.ID_SEKRETAERIN
						+ ")"
						: "")

				// Auslands-DR von allen MA
				// AL dürfen nur einfache Freigabe erteilen
				+ (nurDr && coFreigabeberechtigungen.isFreigabeDrAuslandErlaubt() ?
						" OR ("
						+ " (StatusGenehmigungID=" + CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AL 
						+ ")" 
						+ " AND BuchungsartID IN (" + CoBuchungsart.ID_DIENSTREISE + ")"
						+ " AND DienstreiseID IN (SELECT ID FROM tblDienstreise WHERE LandID!=" + CoLandDienstreise.STATUSID_DEUTSCHLAND + ")"
//						+ " AND AbteilungID<>" + CoAbteilung.ID_VERWALTUNG + " AND PositionID=" + CoPosition.ID_SEKRETAERIN
						+ ")"
						: "")

				+ ")";
		
		
		// Anträge laden
		if (nurDr)
		{
			loadAntraegeDrFreigabecenter(sql);
		} 
		else if (zeitraum)
		{
			loadAntraegeUrlaubFreigabecenter(sql);
		}
		else
		{
			loadAntraegeFreigabecentertageweise(sql, nurOfa);
		}
	}


	/**
	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
	 * 
	 * @param personID ID oder 0 für alle Personen
	 * @param statusGenehmigungID 
	 * @throws Exception
	 */
	public void loadAntraegeVertreter(Date datum, Date datumBis, int statusGenehmigungID, int statusGenehmigungID2, int statusGenehmigungID3, 
			boolean zeitraum, boolean nurOfa, boolean nurDr) throws Exception{
		int personID;
		String sql, whereDatum;
		
		personID = UserInformation.getPersonID();
		
		// Einschränkung
		whereDatum = CoAuswertung.getWhereDatum(datum, datumBis);
		
		
		// SQL-Statement zusammensetzen
		sql = " b.PersonID=v.PersonID AND VertreterID=" + personID 
				+ " AND IstFreigegeben= " + (statusGenehmigungID == CoStatusGenehmigung.STATUSID_GENEHMIGT ? 1 : 0)
				+ " AND YEAR(b.Datum)=YEAR(v.Datum) AND MONTH(b.Datum)=MONTH(v.Datum) AND DAY(b.Datum)=DAY(v.Datum) "
				+ (whereDatum != null ? " AND (" + whereDatum.replace("datum", "b.datum") + ")" : "")
//				+ " AND StatusID<>" + CoStatusBuchung.STATUSID_UNGUELTIG
				+ " AND (StatusGenehmigungID=" + statusGenehmigungID 
				+ (statusGenehmigungID2 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID2 : "")
				+ (statusGenehmigungID3 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID3 : "")
				+ ")" 
				+ " AND BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeVertreter=1)";
		
		
		// Anträge laden
		if (nurDr)
		{
//			loadAntraegeDrFreigabecenter(sql);
		} 
		else if (zeitraum)
		{
			loadAntraegeUrlaubFreigabecenter(sql);
		}
		else
		{
			loadAntraegeFreigabecentertageweise(sql, nurOfa);
		}
	}


	/**
	 * Buchungen (einer Person) mit einem bestimmten Genehmigungsstatus laden
	 * 
	 * @param personID ID oder 0 für alle Personen
	 * @param statusGenehmigungID 
	 * @throws Exception
	 */
	public void loadAntraegePb(Date datum, Date datumBis, int statusGenehmigungID, int statusGenehmigungID2, 
			boolean zeitraum, boolean nurOfa, boolean nurDr) throws Exception{

		String sql, whereDatum;
		
		
		// Einschränkung
		whereDatum = CoAuswertung.getWhereDatum(datum, datumBis);
		
		// SQL-Statement zusammensetzen
		sql =  " (StatusGenehmigungID=" + statusGenehmigungID 
				+ (statusGenehmigungID2 > 0 ? " OR StatusGenehmigungID=" + statusGenehmigungID2 : "") + ")" 
				+ (whereDatum != null ? " AND (" + whereDatum.replace("datum", "b.datum") + ")" : "")
				+ " AND BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabePb=1)";
		
		
		// Anträge laden
		if (nurDr)
		{
			loadAntraegeDrFreigabecenter(sql);
		} 
		else if (zeitraum)
		{
			loadAntraegeUrlaubFreigabecenter(sql);
		}
		else
		{
			loadAntraegeFreigabecentertageweise(sql, nurOfa);
		}
	}


	/**
	 * Buchungen zur Freigabe im Freigabecenter laden
	 * 
	 * @param where Teil der SQL-Abfrage, die spezifiert welche Buchungen, z. B. für AL geladen werden
	 * @throws Exception
	 */
	private void loadAntraegeUrlaubFreigabecenter(String where) throws Exception{
		int personID;
		String sql;
		CoBuchung coBuchung;
		
		personID = UserInformation.getPersonID();

		coBuchung = new CoBuchung();
		coBuchung.addField("virt.field.buchung.vertreter");
		coBuchung.addField("virt.field.buchung.datumbis");

		// SQL-Statement zusammensetzen
		sql = "SELECT b.* FROM tblBuchung b JOIN tblPerson p ON (b.PersonID=p.ID)" + (where.contains("(v.") ? ", tblVertreter v" : "")
				+ " WHERE "
				+ " p.ID<>" + personID
				+ " AND BuchungsartID IN(" + FormFreigabecenterUrlaub.BUCHUNGSARTID + ")"
				+ " AND (" + where + ")"
				+ " ORDER BY " + getSortFieldNameAntraege();
		
		
		Application.getLoaderBase().load(coBuchung, sql);

		// End-Zeit der Anträge laden
		loadUrlaubsuebersicht(coBuchung);
		
		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}


	/**
	 * Buchungen zur Freigabe im Freigabecenter laden
	 * 
	 * @param where Teil der SQL-Abfrage, die spezifiert welche Buchungen, z. B. für AL geladen werden
	 * @throws Exception
	 */
	private void loadAntraegeDrFreigabecenter(String where) throws Exception{
		loadAntraegeDrFreigabecenter(where, false);
	}
	

	/**
	 * Buchungen zur Freigabe im Freigabecenter laden
	 * 
	 * @param where Teil der SQL-Abfrage, die spezifiert welche Buchungen, z. B. für AL geladen werden
	 * @param detailAusgabe alle Daten ausgeben oder Zusammenfassung
	 * @throws Exception
	 */
	private void loadAntraegeDrFreigabecenter(String where, boolean detailAusgabe) throws Exception{
		int personID;
		String sql;
		CoBuchung coBuchung;
		
		personID = UserInformation.getPersonID();

		coBuchung = new CoBuchung();
		coBuchung.addFields("table." + CoDienstreise.TABLE_NAME);
//		coBuchung.addField(CoDienstreise.getResIdZielID());
//		coBuchung.addField(CoDienstreise.getResIdZweckID());
//		coBuchung.addField(CoDienstreise.getResIdThema());

		// zusätzliche Felder für die Anzeige der DR hinzufügen, falls noch nicht vorhanden
		if (getField(CoDienstreise.getResIdZielID()) == null)
		{
			addFields("table." + CoDienstreise.TABLE_NAME);
//			addField(CoDienstreise.getResIdZielID());
//			addField(CoDienstreise.getResIdZweckID());
//			addField(CoDienstreise.getResIdThema());
			addField("virt.field.buchung.datumbis");
			addField("virt.field.buchung.uhrzeitbis");
		}

		// SQL-Statement zusammensetzen
		sql = "SELECT d.Bemerkung, b.*, d.* FROM " + getTableName() + " b " // DR-Bemerkung zuerst, damit nicht die der Buchung angezeigt wird
				+ " JOIN tblPerson p ON (b.PersonID=p.ID) JOIN tblDienstreise d on (d.ID=b.DienstreiseID) " 
				+ " WHERE "
				+ " b.ID<>" + personID
				+ " AND BuchungsartID IN (" + FormFreigabecenterDr.BUCHUNGSARTID + ")"
				+ " AND (" + where.replace("PersonID", "b.PersonID").replace("StatusID", "b.StatusID") + ")"
//				+ " ORDER BY " + getSortFieldNameAntraege();
		+ " ORDER BY d.ID, b.StatusID, b.StatusGenehmigungID, b.datum, b.ID";

		Application.getLoaderBase().load(coBuchung, sql);

		
		// aus Tagesbuchungen Zeiträume laden
		loadDrDgUebersicht(coBuchung, detailAusgabe);

		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}


	/**
	 * Buchungen zur Freigabe im Freigabecenter laden
	 * 
	 * @param where Teil der SQL-Abfrage, die spezifiert welche Buchungen, z. B. für AL geladen werden
	 * @param nurOfa nur Ofa-Buchungen laden oder alle
	 * @throws Exception
	 */
	private void loadAntraegeFreigabecentertageweise(String where, boolean nurOfa) throws Exception{
		int personID;
		String sql;
		
		personID = UserInformation.getPersonID();
		
		removeField("virt.field.buchung.uhrzeitbis");
		removeField(getResIdFreigabeMoeglich());
		emptyCache();
		
		// SQL-Statement zusammensetzen
		sql = "SELECT b.* FROM tblBuchung b JOIN tblPerson p ON (b.PersonID=p.ID)" + (where.contains("(v.") ? ", tblVertreter v" : "")
				+ " WHERE "
				+ " p.ID<>" + personID // TODO Gleitzeitkorrekturbeleg, VL Wiebe, alle Stellen markiert
				+ (nurOfa ? " AND (BuchungsartID=" + CoBuchungsart.ID_ORTSFLEX_ARBEITEN + " OR BuchungsartID=" + CoBuchungsart.ID_VORLESUNG + ")": "")
				+ " AND (" + where + ")"
				+ " ORDER BY " + getSortFieldNameAntraege();
		
		
//		System.out.println(sql);
//		long a1 = System.currentTimeMillis();
		Application.getLoaderBase().load(this, sql);
//		System.out.println("SQL:" + Format.getFormat2NksPunkt((System.currentTimeMillis() - a1)/1000.));

		// End-Zeit der Anträge laden
		loadAntragEnde();
		
		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}


	/**
	 * Zur Auswertung genehmigte und abgelehnte Anträge laden
	 * 
	 * @param personID ID oder 0 für alle Personen
	 * @throws Exception
	 */
	public void loadAntraege(CoAuswertungFreigabe coAuswertungFreigabe) throws Exception{
		Date datum;
		String sql, where, whereDatum, wherePerson, wherePosition;
		
		removeField("virt.field.buchung.vertreter");
		removeField("virt.field.buchung.uhrzeitbis");
		removeField(getResIdFreigabeMoeglich());
		emptyCache();
		
		// SQL-Abfrage zusammensetzen
		whereDatum = coAuswertungFreigabe.getWhereDatum();
		wherePerson = coAuswertungFreigabe.getWherePerson();
		wherePosition = coAuswertungFreigabe.getWherePosition();
		where = "";
		where += (whereDatum != null ? " AND (" + whereDatum + ")" : "");
		where += (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");
	
		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}
		
		// nach Datum oder Datum der letzten Änderung (Genehmigung) suchen
		if (coAuswertungFreigabe.getDatumFreigabe())
		{
			where = where.replace("datum", "geaendertAm");
		}

		// SQL-Statement zusammensetzen
		sql = "SELECT b.* FROM tblBuchung b JOIN tblPerson p ON (b.PersonID=p.ID)"
				+ " WHERE " + where
//				+ " AND StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG
				+ " AND (StatusGenehmigungID=" + CoStatusGenehmigung.STATUSID_GENEHMIGT + " OR StatusGenehmigungID=" + CoStatusGenehmigung.STATUSID_ABGELEHNT + ")" 
				+ " AND BuchungsartID IN (SELECT ID FROM rtblBuchungsart WHERE IstFreigabeMA=1)"
				+ " ORDER BY " + getSortFieldNameAntraege();
		
		Application.getLoaderBase().load(this, sql);

		// End-Zeit der Anträge laden
		loadAntragEnde();
		
		// Vertreter laden
		addField("virt.field.buchung.vertreter");
		if (moveFirst())
		{
			do
			{
				if (CoBuchungsart.isUrlaubFA(getBuchungsartID()))
				{
					datum = getDatum();
					loadVertreter(datum, datum);
				}
			} while (moveNext());
		}

		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}


	/**
	 * Zur Auswertung Anträge laden
	 * 
	 * @throws Exception
	 */
	public void loadAntraegeDr(CoAuswertungDienstreisen coAuswertung) throws Exception{
		int kundeID, buchungsartID, statusGenehmigungID;
		String where, whereDatum, wherePerson, wherePosition;
		
		kundeID = coAuswertung.getKundeID();
		buchungsartID = coAuswertung.getBuchungsartID();
		statusGenehmigungID = coAuswertung.getStatusGenehmigungID();
		whereDatum = coAuswertung.getWhereDatum();
		wherePerson = coAuswertung.getWherePerson();
		wherePosition = coAuswertung.getWherePosition();
		
		// SQL-Abfrage zusammensetzen
		where = "";
		where += (whereDatum != null ? " AND (" + whereDatum + ")" : "");
		where += (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");
		where += " AND StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG;
		
		// für die Auswertung der Buchhaltung gibt es weitere Filter
		if (coAuswertung instanceof CoAuswertungBuchhaltungDienstreisen)
		{
			where += (buchungsartID > 0 ? " AND BuchungsartID=" + buchungsartID : "");
			where += (statusGenehmigungID > 0 ? " AND StatusGenehmigungID=" + statusGenehmigungID : "");
			where += (kundeID > 0 ? " AND d.KundeID=" + kundeID : "");
			where += " AND (d.Abgerechnet=" + (coAuswertung.isAusgabeAbgerechnet() ? 1 : 0);
			where += " OR d.Abgerechnet=" + (coAuswertung.isAusgabeNichtAbgerechnet() ? 0 + " OR d.Abgerechnet IS NULL" : 1) + ")";
		}

		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}

		// Anträge laden
		loadAntraegeDrFreigabecenter(where, coAuswertung.isDetailAusgabe());
		sortByDatum(true);
		
		// als nicht geändert markieren, damit keine Speicher-Abfrage kommt
		setModified(false);
	}


	/**
	 * Für alle Anträge im CO das Ende suchen
	 * 
	 * @throws Exception
	 */
	public void loadAntragEnde() throws Exception {
		int zeit;
		CoBuchung coBuchung;
		
		// Buchungen durchlaufen und die nächste Buchung als Ende suchen
		if (moveFirst())
		{
			coBuchung = new CoBuchung();
			addField("virt.field.buchung.uhrzeitbis");
			addField(getResIdFreigabeMoeglich());

			do
			{
				// Buchung laden
				coBuchung.loadAntragEnde(this);
				if (!coBuchung.moveFirst())
				{
					continue;
				}
				
				// Uhrzeit übertragen
				zeit = coBuchung.getUhrzeitAsInt();
				if (zeit > 0)
				{
					setUhrzeitBis(zeit);
				}
			} while (moveNext());
		}
	}


	/**
	 * Nächste Buchung als Ende des übergebenen Antrags laden
	 * 
	 * @param coBuchung das Ende dieser Buchung wird gesucht
	 * @throws Exception
	 */
	public void loadAntragEnde(CoBuchung coBuchung) throws Exception{
		loadAntragEnde(coBuchung.getPersonID(), coBuchung.getDatum(), coBuchung.getUhrzeitAsInt(), coBuchung.getStatusID(), coBuchung.getStatusGenehmigungID());
	}
	

	/**
	 * Nächste Buchung als Ende des Antrags laden
	 * 
	 * @param personID
	 * @param datum 
	 * @param uhrzeit nächste Buchung nach dieser Uhrzeit wird gesucht
	 * @throws Exception
	 */
	private void loadAntragEnde(int personID, Date datum, int uhrzeit, int statusID, int statusGenehmigungID) throws Exception{
		// TODO wenn das so funktioniert int statusGenehmigungID als Parameter löschen
		String sql;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(datum);

		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID + " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + ""
				+ " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH)+1) + " AND DAY(Datum)=" + (gregDatum.get(Calendar.DAY_OF_MONTH))
				+ " AND ((StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG + ")"
				+ " OR (StatusID=" + CoStatusBuchung.STATUSID_OK + " AND PersonID=6079241))" // TODO Gleitzeitkorrekturbeleg, VL Wiebe, alle Stellen markiert
				// bei ungültigen Anträgen bleibt die vorläufige Endbuchung erstmal stehen
//				+ " AND (StatusID=" + (statusID == CoStatusBuchung.STATUSID_UNGUELTIG ? CoStatusBuchung.STATUSID_VORLAEUFIG : statusID) + ")"
//				+ " AND (StatusGenehmigungID=" + statusGenehmigungID + ")"
				// TODO passt so nicht mehr ganz mit delete AntragEnde zusammen, wenn genehemigte Buchung gelöscht wird und abgelehnt
				+ " AND UhrzeitAsInt>" + uhrzeit
				+ " ORDER BY UhrzeitAsInt";
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
		moveFirst();
	}
	

	/**
	 * Buchungen (keine ungültigen) einer Person für einen Tag laden
	 * 
	 * @param personID
	 * @param datum datum
	 * @throws Exception
	 */
	private void loadTag(int personID, Date datum) throws Exception{
		String sql;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(datum);

		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID + " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + ""
				+ " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH)+1) + " AND DAY(Datum)=" + (gregDatum.get(Calendar.DAY_OF_MONTH))
				+ " AND (StatusID<>" + CoStatusBuchung.STATUSID_UNGUELTIG + ") "
				+ " ORDER BY " + getSortFieldName() ;
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * vorläufige Kommen-Buchungen einer Person für einen Tag laden
	 * 
	 * @param personID
	 * @param datum startdatum
	 * @throws Exception
	 */
	private void loadKommenVorlaeufig(int personID, Date datum) throws Exception{
		loadBuchungVorlaeufig(personID, datum, CoBuchungsart.ID_KOMMEN);
	}
	

	/**
	 * vorläufige Buchungen einer Person für einen Tag laden
	 * 
	 * @param personID
	 * @param datum 
	 * @param buchungsartID 
	 * @throws Exception
	 */
	public void loadBuchungVorlaeufig(int personID, Date datum, int buchungsartID) throws Exception{
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(datum);

		loadBuchungVorlaeufig(personID, gregDatum, buchungsartID);
	}
	

	/**
	 * vorläufige Buchungen einer Person für einen Tag laden
	 * 
	 * @param personID
	 * @param gregDatum 
	 * @param buchungsartID 
	 * @throws Exception
	 */
	public void loadBuchungVorlaeufig(int personID, GregorianCalendar gregDatum, int buchungsartID) throws Exception{
		String sql;

		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID + " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + ""
				+ " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH)+1) + " AND DAY(Datum)=" + (gregDatum.get(Calendar.DAY_OF_MONTH))
				+ (buchungsartID == 0 ? "" : " AND (BuchungsartID=" + buchungsartID + ")")
				+ " AND (StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG + ""
				// vorläufige Buchungen oder FA/Krank/Vorlesung mit OK, das ist wie vorläufig
					+ " OR ((StatusID=" + CoStatusBuchung.STATUSID_OK + " OR StatusID=" + CoStatusBuchung.STATUSID_GEAENDERT + ")" 
					+ " AND (BuchungsartID=" + CoBuchungsart.ID_FA + " OR BuchungsartID=" + CoBuchungsart.ID_KRANK 
					+ " OR BuchungsartID=" + CoBuchungsart.ID_VORLESUNG + "))"
							+ ") "
				+ " AND (StatusGenehmigungID IS NULL OR StatusGenehmigungID<>" + CoStatusGenehmigung.STATUSID_ABGELEHNT + ") "
				+ " ORDER BY " + getSortFieldName() ;
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * vorläufige Buchungen einer Person für einen Tag laden
	 * 
	 * @param personID
	 * @param gregDatum 
	 * @param buchungsartID 
	 * @throws Exception
	 */
	public void loadBuchungVorlaeufig(int personID, GregorianCalendar gregDatum, int buchungsartID, int uhrzeitAb) throws Exception{
		String sql;

		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID + " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + ""
				+ " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH)+1) + " AND DAY(Datum)=" + (gregDatum.get(Calendar.DAY_OF_MONTH))
				+ (buchungsartID == 0 ? "" : " AND (BuchungsartID=" + buchungsartID + ")")
				+ (uhrzeitAb == 0 ? "" : " AND (UhrzeitAsInt>" + uhrzeitAb + ")")
				+ " AND (StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG + ") "
				+ " AND (StatusGenehmigungID IS NULL OR StatusGenehmigungID<>" + CoStatusGenehmigung.STATUSID_ABGELEHNT + ") "
				+ " ORDER BY " + getSortFieldName() ;
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * genehmigte Buchungen einer Person für einen Tag laden
	 * 
	 * @param personID
	 * @param datum 
	 * @param buchungsartID 
	 * @throws Exception
	 */
	public void loadBuchungGenehmigt(int personID, Date datum, int buchungsartID) throws Exception{
		String sql;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(datum);

		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID + " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + ""
				+ " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH)+1) + " AND DAY(Datum)=" + (gregDatum.get(Calendar.DAY_OF_MONTH))
				+ (buchungsartID == 0 ? "" : " AND (BuchungsartID=" + buchungsartID + ")")
				+ " AND (StatusGenehmigungID=" + CoStatusGenehmigung.STATUSID_GENEHMIGT + ") "
				+ " ORDER BY " + getSortFieldName() ;
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

//	/**
//	 * Personen mit der Buchungsart für den Tag laden, nur OK/Geändert-Buchungen
//	 * 
//	 * @param gregDatum
//	 * @param buchungsartID
//	 * @param buchungsartID2
//	 * @throws Exception 
//	 */
//	public void loadForTagesmeldungOk(GregorianCalendar gregDatum, int buchungsartID, int buchungsartID2) throws Exception {
//		loadForTagesmeldung(gregDatum, buchungsartID, buchungsartID2, getWhereStatusOK());
//	}
//
//
//	/**
//	 * Personen mit der Buchungsart für den Tag laden, nur OK/Geändert-Buchungen
//	 * 
//	 * @param gregDatum
//	 * @param buchungsartID
//	 * @param buchungsartID2
//	 * @param buchungsartID3
//	 * @throws Exception 
//	 */
//	public void loadForTagesmeldungOk(GregorianCalendar gregDatum, int buchungsartID, int buchungsartID2, int buchungsartID3) throws Exception {
//		loadForTagesmeldung(gregDatum, buchungsartID, buchungsartID2, buchungsartID3, getWhereStatusOK());
//	}
//
//
//	/**
//	 * Personen mit der Buchungsart für den Tag laden, nur vorläufige Buchungen
//	 * 
//	 * @param gregDatum
//	 * @param buchungsartID
//	 * @param buchungsartID2
//	 * @param buchungsartID3
//	 * @throws Exception 
//	 */
//	public void loadForTagesmeldungVorlaeufig(GregorianCalendar gregDatum, int buchungsartID, int buchungsartID2, int buchungsartID3) throws Exception {
//		loadForTagesmeldung(gregDatum, buchungsartID, buchungsartID2, getWhereStatusVorlaeufig());
//	}
//
//
//	/**
//	 * Personen mit der Buchungsart für den Tag laden, nur digital genehmigte Buchungen
//	 * 
//	 * @param gregDatum
//	 * @param buchungsartID
//	 * @param buchungsartID2
//	 * @throws Exception 
//	 */
//	public void loadForTagesmeldungGenehmigt(GregorianCalendar gregDatum, int buchungsartID, int buchungsartID2) throws Exception {
//		loadForTagesmeldung(gregDatum, buchungsartID, buchungsartID2, 
//				" AND (StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG + " AND StatusGenehmigungID=" + CoStatusGenehmigung.STATUSID_GENEHMIGT + ")");
//	}


	/**
	 * Where-Teil einer Abfrage mit Status OK/geändert und nicht abgelehnt, beginnend mit " AND"
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getWhereStatusOK() throws Exception {
		return " AND (StatusID=" + CoStatusBuchung.STATUSID_OK + " OR StatusID=" + CoStatusBuchung.STATUSID_GEAENDERT + ")" + getWhereStatusGenehmigungNichtAbgelehnt();
	}


	/**
	 * Where-Teil einer Abfrage mit Status vorläufig und nicht abgelehnt, beginnend mit " AND"
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getWhereStatusVorlaeufig() throws Exception {
		return " AND StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG + getWhereStatusGenehmigungNichtAbgelehnt();
	}


	/**
	 * Where-Teil einer Abfrage mit Status OK/geändert/vorläufig und nicht abgelehnt, beginnend mit " AND"
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getWhereStatusOkVorlaeufig() throws Exception {
		return " AND (StatusID=" + CoStatusBuchung.STATUSID_OK + " OR StatusID=" + CoStatusBuchung.STATUSID_GEAENDERT 
				+ " OR StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG + ")" + getWhereStatusGenehmigungNichtAbgelehnt();
	}


	/**
	 * Where-Teil einer Abfrage mit StatusGenehmigung NULL oder nicht abgelehnt, beginnend mit " AND"
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String getWhereStatusGenehmigungNichtAbgelehnt() throws Exception {
		return " AND (StatusGenehmigungID IS NULL OR StatusGenehmigungID<>" + CoStatusGenehmigung.STATUSID_ABGELEHNT + ")";
	}


	/**
	 * Personen mit der Buchungsart für den Tag laden
	 * 
	 * @param gregDatum
	 * @param buchungsartID
	 * @param buchungsartID2
	 * @throws Exception 
	 */
//	public void loadForTagesmeldung(GregorianCalendar gregDatum, int buchungsartID, int buchungsartID2, String whereStatus) throws Exception {
//		loadForTagesmeldung(gregDatum, buchungsartID, buchungsartID2, 0, whereStatus);
//	}


	/**
	 * Personen mit der Buchungsart für den Tag laden
	 * 
	 * @param gregDatum
	 * @param buchungsartID
	 * @param buchungsartID2
	 * @param buchungsartID3
	 * @throws Exception 
	 */
	public void loadForTagesmeldung(GregorianCalendar gregDatum, int buchungsartID, int buchungsartID2, int buchungsartID3, String whereStatus) throws Exception {
		String sql;

		sql = "SELECT * FROM " + getTableName() + " WHERE YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + ""
				+ " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH)+1) + " AND DAY(Datum)=" + (gregDatum.get(Calendar.DAY_OF_MONTH))
				+ " AND (BuchungsartID=" + buchungsartID
				+ (buchungsartID2 == 0 ? "" : " OR BuchungsartID=" + buchungsartID2)
				+ (buchungsartID3 == 0 ? "" : " OR BuchungsartID=" + buchungsartID3)
				+ ")"
				+ whereStatus
				+ " ORDER BY " + getSortFieldName() ;
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
		
		// nach namen sortieren
		sortDisplayValue(getFieldPersonID().getFieldDescription().getResID(), false);
	}


	/**
	 * Urlaubsplanung der Person laden (alle Buchungen mit Urlaub, Status egal, auch teilweise FA-Buchungen)
	 * 
	 * @param personID
	 * @throws Exception 
	 */
	public void loadUrlaubsplanung(int personID) throws Exception {
		String sql;
		Date datum;
		
		// erst ab Freigabe der Urlaubsplanung
		datum = CoFirmenparameter.getInstance().getDatumUrlaubsplanungAb();
		
		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID 
				+ (datum == null ? "" : " AND Datum > '" + Format.getStringForDB(datum) + "'")
				+ " AND (BuchungsartID=" + CoBuchungsart.ID_URLAUB + " OR BuchungsartID=" + CoBuchungsart.ID_SONDERURLAUB
				 + " OR BuchungsartID=" + CoBuchungsart.ID_FA + ")"
				+ " ORDER BY StatusID, BuchungsartID, Datum";
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Alle ganztägigen U/SU/FA-Buchungen für die Urlaubsplanung laden
	 * 
	 * @param coAuswertung
	 * @param nur ganztägige Buchungen oder auch teilweise FA
	 * @throws Exception
	 */
	public void loadUrlaubsplanung(CoAuswertung coAuswertung, boolean nurGanzeTage) throws Exception {
		int statusBuchungID;
		String sql, whereDatum, wherePerson, wherePosition, where;
		
		// ggf. Status der Buchungen abfragen
		statusBuchungID = 0;
		if (coAuswertung instanceof CoAuswertungUrlaub)
		{
			statusBuchungID = ((CoAuswertungUrlaub) coAuswertung).getStatusBuchungID();
		}
		
		addField("virt.field.tblkontowert.tag");
		addField("virt.field.tblkontowert.monat");

		// SQL-Abfrage zusammensetzen
		whereDatum = coAuswertung.getWhereDatum();
		wherePerson = coAuswertung.getWherePerson();
		wherePosition = coAuswertung.getWherePosition();
		where = "";
		where += (whereDatum != null ? " AND (" + whereDatum + ")" : "");
		where += (wherePerson != null ? " AND (" + wherePerson + ")" : "");
		where += (wherePosition != null ? wherePosition : "");
		where += (statusBuchungID != 0 ? " AND StatusID=" + statusBuchungID : "");
		where += " AND (EndePze IS NULL OR Datum < CAST(DATEADD(DAY, 1, EndePze) AS DATE))";
		where += " AND (BuchungsartID=" + CoBuchungsart.ID_URLAUB + " OR BuchungsartID=" + CoBuchungsart.ID_SONDERURLAUB 
				+ " OR (BuchungsartID=" + CoBuchungsart.ID_FA + (nurGanzeTage ? " AND UhrzeitAsInt IS NULL" : "") + "))";

		if (where.trim().startsWith("AND"))
		{
			where = where.substring(4);
		}
		
		sql = "SELECT k.*, DAY(Datum) AS TAG, MONTH(Datum) AS Monat, YEAR(Datum) AS Jahr "
				+ " FROM " + getTableName() + " k JOIN tblPerson p ON (k.PersonID=p.ID) "//OUTER APPLY funSumme(0, 0, 0, 0)"
				+ (where.trim().isEmpty() ? "" : " WHERE " + where )
				+  " ORDER BY " + (nurGanzeTage ? 
						" Jahr, Monat, Nachname, Vorname, Tag, StatusID" // für PDF der Urlaubsplanung erst nach Monat, Person, Tag sortieren
						 // für Aufbereitung mit Zeiträumen statt Einzeltagen (Urlaubsübersicht) erst nach Person, Buchungsart, Datum
						: " Nachname, Vorname, StatusID, BuchungsartID, Datum");
//		+ " ORDER BY Jahr, Monat, Tag, Nachname, Vorname";

		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}


	/**
	 * Urlaubsbuchungen der Person laden für den Tag (keine ungültigen; alle Buchungen mit Urlaub, auch teilweise FA-Buchungen)
	 * 
	 * @param personID
	 * @throws Exception 
	 */
	public void loadUrlaubsbuchungenNichtUngueltig(int personID, Date datum) throws Exception {
		String sql;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(datum);
		
		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID
				+ " AND (BuchungsartID=" + CoBuchungsart.ID_URLAUB + " OR BuchungsartID=" + CoBuchungsart.ID_SONDERURLAUB 
				+ " OR BuchungsartID=" + CoBuchungsart.ID_FA + ")"
				+ " AND StatusID<>" + CoStatusBuchung.STATUSID_UNGUELTIG
				+ " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH) + 1)  
				+ " AND DAY(Datum)=" + gregDatum.get(Calendar.DAY_OF_MONTH);
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Urlaubsbuchungen der Person für den Zeitraum laden
	 * 
	 * @param personID
	 * @param datum
	 * @param datumBis
	 * @param buchungsartID
	 * @param statusID
	 * @param statusGenehmigungID 
	 * @throws Exception
	 */
	private void loadUrlaubsbuchungen(int personID, Date datum, Date datumBis, int buchungsartID, int statusID, int statusGenehmigungID) throws Exception {
		String sql;

		sql = "SELECT * FROM " + getTableName() + " WHERE " + "PersonID=" + personID 
				+ " AND DATUM >= '" + Format.getStringForDB(datum) + "' "
				+ " AND DATUM < '" + Format.getStringForDB(Format.getDateVerschoben(Format.getDate0Uhr(datumBis), 1)) + "'"
				+ " AND BuchungsartID IN (" + CoBuchungsart.ID_URLAUB + ", " + CoBuchungsart.ID_SONDERURLAUB + ", " + CoBuchungsart.ID_FA + ")"
				+ " AND BuchungsartID=" + buchungsartID 
				+ " AND StatusID=" + statusID
				+ " AND StatusGenehmigungID=" + statusGenehmigungID
				;
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
	}
	

	/**
	 * Aufbereitete Urlaubsübersicht mit Zeitraum für jede Buchung statt einzelnen Buchungen für jeden Tag
	 * 
	 * @param personID Urlaubsübersicht für die Person erstellen
	 * @throws Exception
	 */
	public void loadUrlaubsuebersicht(int personID) throws Exception {
		CoBuchung coBuchungUrlaubsplanung;

		coBuchungUrlaubsplanung = new CoBuchung();
		coBuchungUrlaubsplanung.loadUrlaubsplanung(personID);
		
		loadUrlaubsuebersicht(coBuchungUrlaubsplanung);
	}
	

	/**
	 * Aufbereitete Urlaubsübersicht mit Zeitraum für jede Buchung statt einzelnen Buchungen für jeden Tag
	 * 
	 * @param coAuswertungUrlaubsplanung Urlaubsübersicht mit diesen Parametern erstellen
	 * @throws Exception
	 */
	public void loadUrlaubsuebersicht(CoAuswertung coAuswertungUrlaubsplanung) throws Exception {
		CoBuchung coBuchungUrlaubsplanung;

		coBuchungUrlaubsplanung = new CoBuchung();
		coBuchungUrlaubsplanung.loadUrlaubsplanung(coAuswertungUrlaubsplanung, false);
		
		loadUrlaubsuebersicht(coBuchungUrlaubsplanung);
		sortByDatum(true);
	}
	

	/**
	 * Aufbereitete Urlaubsübersicht für die übergebenen Buchungen erstellen, 
	 * mit Zeitraum für jede Buchung statt einzelnen Buchungen für jeden Tag
	 * 
	 * @param coAuswertungUrlaubsplanung
	 * @throws Exception
	 */
	private void loadUrlaubsuebersicht(CoBuchung coBuchungUrlaubsplanung) throws Exception {
		int id, personID, lastPersonID, buchungsartID, lastBuchungsartID, statusID, lastStatusID, statusGenehmigungID, lastStatusGenehmigungID;
		int uhrzeit, aktUhrzeit, uhrzeitBis, lastUhrzeitBis, jahr, lastJahr, kernzeitbeginn, geaendertVonID, grundAenderungID;
		Set<String> setBemerkung;
		Date lastDatum, datum, geaendertAm, lastgeaendertAm;
		CoPerson coPerson;
		CoBuchung coBuchung;
		CoFirmenparameter coFirmenparameter = CoFirmenparameter.getInstance();

		coPerson = CoPerson.getInstance();
		coBuchung = new CoBuchung();
		
		lastPersonID = 0;
		lastDatum = null;
		lastJahr = 0;
		uhrzeitBis = 0;
		lastUhrzeitBis = 0;
		lastBuchungsartID = 0;
		lastStatusID = 0;
		lastStatusGenehmigungID = 0;
		lastgeaendertAm = null;
		setBemerkung = new HashSet<String>();

		// Felder für Zeitraum hinzufügen
		emptyCache();
		addField("virt.field.buchung.datumbis");
		addField("virt.field.buchung.uhrzeitbis");
		addField("virt.field.buchung.vertreter");
		begin();

		if (!coBuchungUrlaubsplanung.moveFirst())
		{
			return;
		}
		
		// Urlaubsplanung durchlaufen
		lastDatum = coBuchungUrlaubsplanung.getDatum(); // erstes Datum merken, falls es ein eintägiger Urlaub ist, ist es sonst null
		do
		{
			id = coBuchungUrlaubsplanung.getID();
			personID = coBuchungUrlaubsplanung.getPersonID();
			datum = coBuchungUrlaubsplanung.getDatum();
			jahr = Format.getJahr(datum);
			uhrzeit = coBuchungUrlaubsplanung.getUhrzeitAsInt();
			uhrzeitBis = 0;
			buchungsartID = coBuchungUrlaubsplanung.getBuchungsartID();
			statusID = coBuchungUrlaubsplanung.getStatusID();
			statusGenehmigungID = coBuchungUrlaubsplanung.getStatusGenehmigungID();

			geaendertAm = coBuchungUrlaubsplanung.getGeaendertAm();
			geaendertVonID = coBuchungUrlaubsplanung.getGeaendertVonID();
			grundAenderungID = coBuchungUrlaubsplanung.getGrundAenderungID();

			Format.addStringToSet(coBuchungUrlaubsplanung.getBemerkung(), setBemerkung);
			
			kernzeitbeginn = coFirmenparameter.getKernzeitBeginn(datum);

			// für FA ggf. UhrzeitBis bestimmen
			if (buchungsartID == CoBuchungsart.ID_FA && uhrzeit > 0)
			{
				// vorläufige Kommen-Buchungen für den Tag holen
				coBuchung.loadKommenVorlaeufig(personID, datum);

				// die Uhrzeit der nächsten Buchung ist das Ende der FA-Buchung
				if (coBuchung.moveFirst())
				{
					do
					{
						aktUhrzeit = coBuchung.getUhrzeitAsInt();
						if (aktUhrzeit > uhrzeit)
						{
							uhrzeitBis = aktUhrzeit;
							break;
						}
					} while (coBuchung.moveNext());
				}
			}

			// prüfen, ob Person, Buchungsart und Status übereinstimmen, sonst neuen Eintrag
			if (lastPersonID != personID || lastBuchungsartID != buchungsartID || lastStatusID != statusID || lastStatusGenehmigungID != statusGenehmigungID 
//					|| lastUhrzeit != uhrzeit || lastUhrzeitBis != uhrzeitBis // Uhrzeit von und bis muss gleich sein
					|| (uhrzeit != 0 && uhrzeit != kernzeitbeginn) || lastUhrzeitBis != 0 // FA von 13 bis 10 Uhr oder länger am nächsten Tag möglich
					|| lastJahr != jahr
					|| !checkArbeitstage(coPerson, lastDatum, datum))
			{
				// Vertreter für den alten Datensatz laden
				loadVertreter();
				
				// neuer Datensatz
				add();
				setID(id);
				setPersonID(personID);
				setDatum(datum);
				setUhrzeitAsInt(uhrzeit > 0 && uhrzeit != kernzeitbeginn ? uhrzeit : null);
				//					setUhrzeitBis(uhrzeit > 0 ? uhrzeit+60 : null);
				setUhrzeitBis(uhrzeitBis > 0 ? uhrzeitBis : null);
				setBuchungsartID(buchungsartID);
				setStatusID(statusID);
				if (statusGenehmigungID > 0)
				{
					setStatusGenehmigungID(statusGenehmigungID);
				}

				setGeaendertAm(geaendertAm);
				setGeaendertVonID(geaendertVonID);
				setGrundAenderungID(grundAenderungID);

				lastPersonID = personID;
				lastJahr = jahr;
				lastUhrzeitBis = uhrzeitBis;
				lastBuchungsartID = buchungsartID;
				lastStatusID = statusID;
				lastStatusGenehmigungID = statusGenehmigungID;
				lastgeaendertAm = geaendertAm;

				setBemerkung = new HashSet<String>();
				Format.addStringToSet(coBuchungUrlaubsplanung.getBemerkung(), setBemerkung);

				coPerson.moveToID(personID);
			}

			// Aktualisierungen

			// Uhrzeit bis
			setUhrzeitBis(uhrzeitBis > 0 ? uhrzeitBis : null);

			// letzter Urlaubstag
			setDatumBis(datum);

			// letzte Aktualisierung
			if (geaendertAm.after(lastgeaendertAm))
			{
				setGeaendertAm(geaendertAm);
				setGeaendertVonID(geaendertVonID);
				setGrundAenderungID(grundAenderungID);

				lastgeaendertAm = geaendertAm;
			}

			// Bemerkung erweitern/aktualisieren
			setBemerkung(Format.getStringValue(setBemerkung));

			lastDatum = datum;

		} while (coBuchungUrlaubsplanung.moveNext());
		
		
		// Vertreter für den letzten Datensatz laden
		loadVertreter();


		// Datum bis für den letzten Urlaub noch setzen
//		setDatumBis(lastDatum);
	}
	

	/**
	 * Aufbereitete Übersicht für die übergebenen Buchungen erstellen, 
	 * mit Zeitraum für jede Buchung statt einzelnen Buchungen für jeden Tag
	 * 
	 * @param coBuchungTmp
	 * @param detailAusgabe alle Daten ausgeben oder Zusammenfassung
	 * @throws Exception
	 */
	private void loadDrDgUebersicht(CoBuchung coBuchungTmp, boolean detailAusgabe) throws Exception {
		int id, personID, dienstreiseID, lastDienstreiseID, buchungsartID, statusID, lastStatusID, statusGenehmigungID, lastStatusGenehmigungID;
		int uhrzeit, uhrzeitBis, geaendertVonID, grundAenderungID;
		String resID, stringValue;
		Set<String> setBemerkung;
		Date datum, geaendertAm, lastgeaendertAm;
		IField field;
		CoPerson coPerson;
		CoDienstreise coDienstreise;
		Iterator<IField> iterDrFields;

		coPerson = CoPerson.getInstance();
		coDienstreise = new CoDienstreise();
		
		lastDienstreiseID = 0;
		uhrzeitBis = 0;
		lastStatusID = 0;
		lastStatusGenehmigungID = 0;
		lastgeaendertAm = null;
		setBemerkung = new HashSet<String>();
		
		// End-Buchungen der Tagesbuchungen laden
		coBuchungTmp.loadAntragEnde();

		// Bearbeitung starten
		emptyCache();
		begin();

		if (!coBuchungTmp.moveFirst())
		{
			return;
		}
		
		// Buchungen durchlaufen
		do
		{
			id = coBuchungTmp.getID();
			dienstreiseID = coBuchungTmp.getDienstreiseID();
			personID = coBuchungTmp.getPersonID();
			datum = coBuchungTmp.getDatum();
			uhrzeit = coBuchungTmp.getUhrzeitAsInt();
			uhrzeitBis = coBuchungTmp.getUhrzeitBisAsInt();
			buchungsartID = coBuchungTmp.getBuchungsartID();
			statusID = coBuchungTmp.getStatusID();
			statusGenehmigungID = coBuchungTmp.getStatusGenehmigungID();

			geaendertAm = coBuchungTmp.getGeaendertAm();
			geaendertVonID = coBuchungTmp.getGeaendertVonID();
			grundAenderungID = coBuchungTmp.getGrundAenderungID();

			Format.addStringToSet(coBuchungTmp.getBemerkung(), setBemerkung);
			
			// prüfen, ob Person, Buchungsart und Status übereinstimmen, sonst neuen Eintrag
			if (lastDienstreiseID != dienstreiseID
					|| lastStatusID != statusID || lastStatusGenehmigungID != statusGenehmigungID)
			{
				// neuer Datensatz
				add();
				setID(id);
				setDienstreiseID(dienstreiseID);
				setPersonID(personID);
				setDatum(datum);
				setUhrzeitAsInt(uhrzeit > 0 ? uhrzeit : null);
				setUhrzeitBis(uhrzeitBis > 0 ? uhrzeitBis : null);
				setBuchungsartID(buchungsartID);
				setStatusID(statusID);
				if (statusGenehmigungID > 0)
				{
					setStatusGenehmigungID(statusGenehmigungID);
				}

				// Daten aus dem DR-Antrag
				iterDrFields = coDienstreise.getFields();
				while (iterDrFields.hasNext())
				{
					resID = iterDrFields.next().getFieldDescription().getResID();
					field = getField(resID);
					if (field != null)
					{
						getField(resID).setValue(coBuchungTmp.getField(resID).getValue());
					}
				}
				// bei der Zusammenfassung Ziel-Auswahl und Freitext zusammenführen
				stringValue = Format.getStringValue(getField(CoDienstreise.getResIdZiel()));
				if (!detailAusgabe && (stringValue == null || stringValue.trim().isEmpty())
//						&& coBuchungTmp.getField(CoDienstreise.getResIdZielID()) != null
						)
				{
					getField(CoDienstreise.getResIdZiel()).setValue(coBuchungTmp.getField(CoDienstreise.getResIdZielID()).getDisplayValue());
				}
//				getField(CoDienstreise.getResIdZielID()).setValue(coBuchungTmp.getField(CoDienstreise.getResIdZielID()).getValue());
//				getField(CoDienstreise.getResIdZweckID()).setValue(coBuchungTmp.getField(CoDienstreise.getResIdZweckID()).getValue());
//				getField(CoDienstreise.getResIdThema()).setValue(coBuchungTmp.getField(CoDienstreise.getResIdThema()).getValue());
				
				setGeaendertAm(geaendertAm);
				setGeaendertVonID(geaendertVonID);
				setGrundAenderungID(grundAenderungID);

				lastDienstreiseID = dienstreiseID;
				lastStatusID = statusID;
				lastStatusGenehmigungID = statusGenehmigungID;
				lastgeaendertAm = geaendertAm;

				setBemerkung = new HashSet<String>();
				Format.addStringToSet(coBuchungTmp.getBemerkung(), setBemerkung);
				setBemerkung(coBuchungTmp.getBemerkung());

				coPerson.moveToID(personID);
				
				continue;
			}

			// Aktualisierungen

			// Uhrzeit bis
			setUhrzeitBis(uhrzeitBis > 0 ? uhrzeitBis : null);

			// letzter Urlaubstag
			setDatumBis(datum);

			// letzte Aktualisierung
			if (geaendertAm.after(lastgeaendertAm))
			{
				setGeaendertAm(geaendertAm);
				setGeaendertVonID(geaendertVonID);
				setGrundAenderungID(grundAenderungID);

				lastgeaendertAm = geaendertAm;
			}

			// Bemerkung erweitern/aktualisieren
			setBemerkung(Format.getStringValue(setBemerkung));

		} while (coBuchungTmp.moveNext());
		
	}
	

	public void loadVertreter() throws Exception {
		if (getRowCount() > 0)
		{
			loadVertreter(getDatum(), getDatumBis());
		}
	}


	private void loadVertreter(Date datumUrlaub, Date datumUrlaubBis) throws Exception {
		CoVertreter coVertreter;
		
		if (getRowCount() == 0 || getStatusID() == CoStatusBuchung.STATUSID_UNGUELTIG)
		{
			return;
		}
		
		coVertreter = new CoVertreter();
//		System.out.println(getPerson() + ": " + Format.getString(getDatum()) + " - " + Format.getString(getDatumBis()));
		coVertreter.loadVertreter(getPersonID(), datumUrlaub, datumUrlaubBis);
		
		setVertreter(coVertreter.getMeldungVertreter());
//		System.out.println(coVertreter.getMeldungVertreter());
	}


	/**
	 * Prüft, ob zwischen den beiden übergebenen Datums ein Arbeitstag liegt oder es ein zusammenhängender Zeitraum ist
	 * 
	 * @param lastDatum
	 * @param datum
	 * @return es liegt kein Arbeitstag zwischen den beiden Datums
	 * @throws Exception
	 */
	private boolean checkArbeitstage(CoPerson coPerson, Date lastDatum, Date datum) throws Exception {
		Date aktDatum;
		
		// beide Daten auf die gleiche Uhrzeit setzen
		lastDatum = Format.getDate12Uhr(lastDatum);
		datum = Format.getDate12Uhr(datum);
		
		
		// alle Tage ab lastDatum durchlaufen, bis datum erreicht wurde
		aktDatum = Format.getDateVerschoben(lastDatum, 1);
		while (aktDatum.before(datum))
		{
			// wenn zwischen den beiden übergebenen Datums ein Arbeitstag liegt, ist es ein neuer Urlaub
			if (coPerson.isArbeitstag(aktDatum))
			{
				return false;
			}
			
			aktDatum = Format.getDateVerschoben(aktDatum, 1);
		}
		
		return true;
	}


	/**
	 * Anzahl geplanter Urlaubstage (Status vorläufig) für die Person in dem Jahr ab dem übergebenen Datum
	 * 
	 * @param personID
	 * @param datum
	 * @param jahr
	 * @return
	 * @throws Exception
	 */
	public static int getAnzahlGeplantenUrlaub(int personID, Date datum, int jahr) throws Exception {
		String sql;
		
		sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + "PersonID=" + personID
				+ " AND BuchungsartID=" + CoBuchungsart.ID_URLAUB + " AND StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG
				+ " AND Datum > '" + Format.getStringForDB(Format.getDateVerschoben(new Date(), 1)) + "'"
				+ " AND YEAR(Datum)= " + jahr;
		
		return Format.getIntValue(Application.getLoaderBase().executeScalar(sql));
	}
	

	/**
	 * Anzahl geplanter Urlaubstage (Status vorläufig) für die Person in dem Zeitraum (ab morgen) an denen kein genehmigter Urlaub eingetragen ist
	 * 
	 * @param personID
	 * @param datumVon
	 * @param datumBis
	 * @return
	 * @throws Exception
	 */
	public static int getAnzahlGeplantenUrlaub(int personID, Date datumVon, Date datumBis) throws Exception {
		return getAnzahlGeplanteTage(personID, datumVon, datumBis, CoBuchungsart.ID_URLAUB);
	}
	

	/**
	 * Anzahl geplanter Sonderurlaubstage (Status vorläufig) für die Person in dem Zeitraum (ab morgen) an denen kein genehmigter Sonderurlaub eingetragen ist
	 * 
	 * @param personID
	 * @param datumVon
	 * @param datumBis
	 * @return
	 * @throws Exception
	 */
	public static int getAnzahlGeplantenSonderurlaub(int personID, Date datumVon, Date datumBis) throws Exception {
		return getAnzahlGeplanteTage(personID, datumVon, datumBis, CoBuchungsart.ID_SONDERURLAUB);
	}
	

	/**
	 * Anzahl geplanter Tage mit der übergebenen Buchungsart (Status vorläufig) für die Person in dem Zeitraum (ab morgen) 
	 * an denen kein genehmigter Urlaub eingetragen ist
	 * 
	 * @param personID
	 * @param datumVon
	 * @param datumBis
	 * @return
	 * @throws Exception
	 */
	private static int getAnzahlGeplanteTage(int personID, Date datumVon, Date datumBis, int buchungsartID) throws Exception {
		String sql, where;

		
		// SQL-Abfrage zusammensetzen
		where = CoAuswertung.getWhereDatum(datumVon, datumBis);
		
		where += " AND datum > '" + Format.getStringForDB(Format.getDateVerschoben(Format.getDate0Uhr(new Date()), 1)) + "'";
		where += " AND StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG;
		where += " AND BuchungsartID=" + buchungsartID;
		where += " AND PersonID=" + personID;
		where += " AND NOT EXISTS (SELECT * FROM tblBuchung "
				+ " WHERE (StatusID=" + CoStatusBuchung.STATUSID_OK + " OR StatusID=" + CoStatusBuchung.STATUSID_GEAENDERT + ")"
				+ " AND BuchungsartID=" + buchungsartID + " AND PersonID= " + personID
				+ " AND Year(Datum)=Year(b.Datum) AND MONTH(Datum)=MONTH(b.Datum) AND Day(b.Datum)=Day(Datum))";


		sql = "SELECT COUNT(*) FROM tblBuchung b "
				+ (where.trim().isEmpty() ? "" : " WHERE " + where )
				+ "Group BY Year(Datum) , MONTH(Datum), Day(Datum) ";
		
		// es werden alle Tage mit Buchungen zurückgegeben, deshalb die Länge der Liste
		return Format.getIntValue(Application.getLoaderBase().executeScalarArray(sql).length);
	}
	

	/**
	 * Anzahl noch nicht genehmigter Tage mit der übergebenen Buchungsart (Status vorläufig oder ungültig) für die Person in dem Zeitraum (ab morgen) 
	 * an denen kein genehmigter Urlaub eingetragen ist
	 * 
	 * @param personID
	 * @param datumVon
	 * @param datumBis
	 * @return
	 * @throws Exception
	 */
	public static int getAnzahlbeantragteTage(int personID, int jahr, int buchungsartID) throws Exception {
		String sql, where;
		
		// SQL-Abfrage zusammensetzen
		where = " YEAR(Datum) = " + jahr;
		// Status ungültig, falls Löschen noch nicht genehmigt wurde
		where += " AND (StatusID=" + CoStatusBuchung.STATUSID_VORLAEUFIG + " OR StatusID=" + CoStatusBuchung.STATUSID_UNGUELTIG + ")";
		where += " AND StatusGenehmigungID IN " + CoStatusGenehmigung.getIDsBeantragt();
		where += " AND BuchungsartID=" + buchungsartID;
		where += " AND PersonID=" + personID;
		where += " AND NOT EXISTS (SELECT * FROM tblBuchung "
				+ " WHERE (StatusID=" + CoStatusBuchung.STATUSID_OK + " OR StatusID=" + CoStatusBuchung.STATUSID_GEAENDERT + ")"
				+ " AND BuchungsartID=" + buchungsartID + " AND PersonID= " + personID
				+ " AND Year(Datum)=Year(b.Datum) AND MONTH(Datum)=MONTH(b.Datum) AND Day(b.Datum)=Day(Datum))";


		sql = "SELECT COUNT(*) FROM tblBuchung b "
				+ (where.trim().isEmpty() ? "" : " WHERE " + where )
				+ "Group BY Year(Datum) , MONTH(Datum), Day(Datum) ";
		
		// es werden alle Tage mit Buchungen zurückgegeben, deshalb die Länge der Liste
		return Format.getIntValue(Application.getLoaderBase().executeScalarArray(sql).length);
	}
	

	/**
	 * Anzahl genehmigter Tage mit der übergebenen Buchungsart (Status OK oder geändert) für die Person in dem Zeitraum
	 * 
	 * @param personID
	 * @param datumVon
	 * @param datumBis
	 * @return
	 * @throws Exception
	 */
	public static int getAnzahlGenehmigteTage(int personID, Date datumVon, Date datumBis, int buchungsartID) throws Exception {
		String sql, where;

		
		// SQL-Abfrage zusammensetzen
		where = CoAuswertung.getWhereDatum(datumVon, datumBis);
		
		where += " AND StatusID<>" + CoStatusBuchung.STATUSID_UNGUELTIG;
		where += " AND BuchungsartID=" + buchungsartID;
		where += " AND PersonID=" + personID;

		sql = "SELECT COUNT(*) FROM tblBuchung b "
				+ (where.trim().isEmpty() ? "" : " WHERE " + where )
				+ "Group BY Year(Datum) , MONTH(Datum), Day(Datum) ";
		
		// es werden alle Tage mit Buchungen zurückgegeben, deshalb die Länge der Liste
		return Format.getIntValue(Application.getLoaderBase().executeScalarArray(sql).length);
	}
	

	/**
	 * Anzahl der FA-Tage (nur komplette Tage, genehmigt & geplant) in dem Monat des übergebenen Datums. 
	 * Der Tag des Datums wird nicht berücksichtigt, da für diesen FA eingetragen werden soll
	 * 
	 * @param personID
	 * @param gregDatum
	 * @return
	 * @throws Exception
	 */
	public static int getAnzahlFaTageMonat(int personID, GregorianCalendar gregDatum) throws Exception {
		String sql, where;

		
		// SQL-Abfrage zusammensetzen
		where = " PersonID=" + personID;
		where += " AND StatusID<>" + CoStatusBuchung.STATUSID_UNGUELTIG;
		where += " AND BuchungsartID=" + CoBuchungsart.ID_FA;
		where += " AND UhrzeitAsInt IS NULL";
		where += " AND Year(Datum)=" + gregDatum.get(GregorianCalendar.YEAR) + " AND MONTH(Datum)=" + (gregDatum.get(GregorianCalendar.MONTH)+1)
				+ " AND Day(Datum)<>" + gregDatum.get(GregorianCalendar.DAY_OF_MONTH);


		sql = "SELECT COUNT(*) FROM tblBuchung"
				+ (where.trim().isEmpty() ? "" : " WHERE " + where )
				+ "Group BY Year(Datum), MONTH(Datum), Day(Datum) ";
		
		// es werden alle Tage mit Buchungen zurückgegeben, deshalb die Länge der Liste
		return Format.getIntValue(Application.getLoaderBase().executeScalarArray(sql).length);
	}


	/**
	 * Prüfen, ob die Person an dem Tag OFA gemacht hat
	 * 
	 * @param personID
	 * @param tag
	 * @param monat
	 * @param jahr
	 * @return
	 * @throws Exception 
	 */
	public static boolean isOfa(int personID, int tag, int monat, int jahr) throws Exception {
		String sql, where;

		// SQL-Abfrage zusammensetzen
		where = " PersonID=" + personID;
		where += " AND (StatusID=" + CoStatusBuchung.STATUSID_OK + " OR StatusID=" + CoStatusBuchung.STATUSID_GEAENDERT + ")";
		where += " AND BuchungsartID=" + CoBuchungsart.ID_ORTSFLEX_ARBEITEN;
		where += " AND Year(Datum)=" + jahr + " AND MONTH(Datum)=" + monat + " AND Day(Datum)=" + tag;


		sql = "SELECT * FROM tblBuchung"
				+ (where.trim().isEmpty() ? "" : " WHERE " + where );

		// es werden alle Tage mit Buchungen zurückgegeben, deshalb die Länge der Liste
		return Application.getLoaderBase().executeScalarArray(sql).length > 0;
	}


	/**
	 * Prüfen, ob um die übergebenen Uhrzeiten OFA geplant ist, also z. B. eine neue FA-Buchung innerhalb des OFA-Tags ist
	 * 
	 * @param personID
	 * @param datum
	 * @param uhrzeitVon Beginn der neuen Buchung
	 * @param uhrzeitBis Ende der neuen Buchung
	 * @return
	 * @throws Exception 
	 */
	public static boolean isOfa(int personID, Date datum, int uhrzeitVon, int uhrzeitBis) throws Exception {
		boolean isOfa;
		CoBuchung coBuchung;
		
		
		coBuchung = new CoBuchung();
		coBuchung.loadBuchungVorlaeufig(personID, datum, 0);
		if (coBuchung.moveFirst())
		{
			isOfa = false;
			
			do
			{
				// letzte Buchung vor Uhrzeit der neuen Buchung suchen
				if (coBuchung.getUhrzeitAsInt() < uhrzeitVon)
				{
					isOfa = coBuchung.getBuchungsartID() == CoBuchungsart.ID_ORTSFLEX_ARBEITEN;
				}
				// erste Buchung nach dem Ende der neuen Buchung
				else if (coBuchung.getUhrzeitAsInt() > uhrzeitBis)
				{
					return isOfa && coBuchung.getBuchungsartID() == CoBuchungsart.ID_GEHEN;
				}
				
			} while (coBuchung.moveNext());
		}

		return false;
	}


	/**
	 * Letzte (aktuellsts) Buchungs-Nr laden.<br>
	 * Die BuchungsNr werden durch die Terminal-Buchungen erzeugt.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static int loadLastBuchungsNr() throws Exception{
		int buchungsNr;
		String sql;
		
		sql = "SELECT MAX(bnr) FROM " + TABLE_NAME;
		buchungsNr = Format.getIntValue(Application.getLoaderBase().executeScalar(sql));
		
		return buchungsNr;
	}
	

	public int getBuchungsNr() {
		return Format.getIntValue(getField("field." + getTableName() + ".bnr").getValue());
	}


	public void setBuchungsNr(int buchungsNr) {
		getField("field." + getTableName() + ".bnr").setValue(buchungsNr);
	}


	public Date getZeitpunkt() {
		return getField("field." + getTableName() + ".zeitpunkt").getDateValue();
	}


	public void setZeitpunkt(Date zeitpunkt) {
		getField("field." + getTableName() + ".zeitpunkt").setValue(zeitpunkt);
		
		setDatum(zeitpunkt);
		setUhrzeit(zeitpunkt);
	}


	public int getSystemNr() {
		return Format.getIntValue(getField("field." + getTableName() + ".system").getValue());
	}


	public void setSystemNr(int systemNr) throws Exception {
		getField("field." + getTableName() + ".system").setValue(systemNr);
		
		setBuchungserfassungsartIdBySystemNr(systemNr);
	}


	public int getEventNr() {
		return Format.getIntValue(getField("field." + getTableName() + ".event").getValue());
	}


	public void setEventNr(int eventNr) throws Exception {
		getField("field." + getTableName() + ".event").setValue(eventNr);
		
		setBuchungsTypIdByEventNr(eventNr);
	}


	public int getIdX() {
		return Format.getIntValue(getField("field." + getTableName() + ".idx").getValue());
	}


	public void setIdX(int idX) throws Exception {
		getField("field." + getTableName() + ".idx").setValue(idX);
		
		setSystembuchungsmeldung(idX);
	}


	public String getChipkartenNrHex() {
		return Format.getStringValue(getField("field." + getTableName() + ".knrhex").getValue());
	}


	public void setChipkartenNrHex(String chipkartenNrHex) throws Exception {
		long chipkartenNrAsLong;
		String chipkartenNr;
		

		getField("field." + getTableName() + ".knrhex").setValue(chipkartenNrHex);
		
		// ChipkartenNr bestimmen
		chipkartenNrAsLong = Format.getValueOfHexValue(chipkartenNrHex);
		if (chipkartenNrAsLong > 0)
		{
			chipkartenNr = Format.getStringValue(chipkartenNrAsLong);
			setChipkartenNr(chipkartenNr);
			
			// Person bestimmen
			setPersonID(chipkartenNr);
		}
	}


	/**
	 * ChipkartenNr wird nochmal separat gespeichert, da ggf. keine Person zugeordnet ist und die ChipkartenNr für die Zuordnung benötigt wird.
	 * 
	 * @param chipkartenNr
	 * @throws Exception
	 */
	private void setChipkartenNr(String chipkartenNr) throws Exception {
		getField("field." + getTableName() + ".chipkartennr").setValue(chipkartenNr);
	}


	/**
	 * Buchungsart aus Geloc (param1)
	 * 
	 * @return
	 */
	public String getParam1() {
		return Format.getStringValue(getField("field." + getTableName() + ".param1").getValue());
	}


	/**
	 *  aus Geloc (param2)
	 * 
	 * @return
	 */
	public String getParam2() {
		return Format.getStringValue(getField("field." + getTableName() + ".param2").getValue());
	}


	/**
	 * Buchungsart aus Geloc (param1)
	 * 
	 * @param param1
	 * @throws Exception 
	 */
	public void setParam1(String param1) throws Exception {
		getField("field." + getTableName() + ".param1").setValue(param1);
		
		setBuchungsartIDByBuchungsregelhex(param1);
	}


	/**
	 *  aus Geloc (param2)
	 * 
	 * @return
	 */
	public void setParam2(String param2) throws Exception {
		getField("field." + getTableName() + ".param2").setValue(param2);
	}


	private void setUhrzeit(Date zeitpunkt) {
		getField("field." + getTableName() + ".uhrzeit").setValue(zeitpunkt);
		setUhrzeitAsInt(zeitpunkt);
	}


	/**
	 * Uhrzeit in Minuten als Integer 
	 * 
	 * @param zeitpunkt
	 */
	public void setUhrzeitAsInt(Date zeitpunkt) {
		int zeit;
		GregorianCalendar gregDatum;
		
		gregDatum = (GregorianCalendar) GregorianCalendar.getInstance();
			gregDatum.setTime(zeitpunkt);
		
		zeit = gregDatum.get(GregorianCalendar.HOUR_OF_DAY) * 60 + gregDatum.get(GregorianCalendar.MINUTE);
		
		// alte 30 Sekunden-Rundung
//		if (gregDatum.get(GregorianCalendar.SECOND) >= 30)
//		{
//			zeit += 1;
//		}
		
		setUhrzeitAsInt(zeit);
	}


	public IField getFieldUhrzeitAsInt() {
		return getField("field." + getTableName() + ".uhrzeitasint");
	}


	public int getUhrzeitAsInt() {
		return Format.getIntValue(getFieldUhrzeitAsInt().getValue());
	}


	public void setUhrzeitAsInt(Integer uhrzeitAsInt) {
		getField("field." + getTableName() + ".uhrzeitasint").setValue(uhrzeitAsInt);
	}

	
	public String getResIdFieldUhrzeitBis() {
		return "virt.field.buchung.uhrzeitbis";
	}


	public void addFieldUhrzeitBis() {
		addField(getResIdFieldUhrzeitBis());
	}


	public IField getFieldUhrzeitBis() {
		return getField(getResIdFieldUhrzeitBis());
	}


	public int getUhrzeitBisAsInt() {
		return Format.getIntValue(getFieldUhrzeitBis().getValue());
	}


	public void setUhrzeitBis(Integer uhrzeitAsInt) {
		getFieldUhrzeitBis().setValue(uhrzeitAsInt);
	}


	private int getBuchungsTypID() {
		return Format.getIntValue(getField("field." + getTableName() + ".buchungstypid").getValue());
	}


	private void setBuchungsTypIdByEventNr(int eventNr) throws Exception {
		int buchungstypID;
		
		buchungstypID = CoBuchungstyp.getInstance().getIdFromEventNr(eventNr);
		
		if (buchungstypID != 0)
		{
			setBuchungsTypID(buchungstypID);
		}
	}


	/**
	 * Buchungstypen (aus der Geloc-DB) (Zeitbuchung, Aktivierung Zeitzone...)
	 * 
	 * @param buchungstyp
	 * @throws Exception
	 */
	public void setBuchungsTyp(String buchungstyp) throws Exception {
		setBuchungsTypID(CoBuchungstyp.getInstance().getID(buchungstyp));
	}


	private void setBuchungsTypID(int buchungstypID) throws Exception {
		getField("field." + getTableName() + ".buchungstypid").setValue(buchungstypID);
	}


	/**
	 * Buchungsarten (Kommen, Gehen, Pause...)
	 * 
	 * @param buchungsregelHex
	 * @throws Exception
	 */
	private void setBuchungsartIDByBuchungsregelhex(String buchungsregelHex) throws Exception {
		int buchungsartID;
		
		buchungsartID = CoBuchungsart.getInstance().getIdFromBuchungsregelHex(buchungsregelHex);
		
		if (buchungsartID != 0)
		{
			setBuchungsartID(buchungsartID);
		}
	}


	private void setBuchungserfassungsartIdBySystemNr(int systemNr) throws Exception {
		int buchungserfassungsartID;
		
		buchungserfassungsartID = CoBuchungserfassungsart.getInstance().getIdFromSystemNr(systemNr);
		
		if (buchungserfassungsartID != 0)
		{
			setBuchungserfassungsartID(buchungserfassungsartID);
		}
	}


	private IField getFieldBuchungserfassungsart() {
		return getField("field." + getTableName() + ".buchungserfassungsartid");
	}


	public int getBuchungserfassungsartID() {
		return Format.getIntValue(getFieldBuchungserfassungsart().getValue());
	}


	public void setBuchungserfassungsart(String buchungserfassungsart) throws Exception {
		setBuchungserfassungsartID(CoBuchungserfassungsart.getInstance().getID(buchungserfassungsart));
	}


	public void setBuchungserfassungsartID(int buchungserfassungsartID) throws Exception {
		getFieldBuchungserfassungsart().setValue(buchungserfassungsartID);
	}


	private void setSystembuchungsmeldung(int meldungNr) throws Exception {
		int systembuchungsmeldungid;
		
		systembuchungsmeldungid = CoSystembuchungsmeldung.getInstance().getID(getBuchungsTypID(), meldungNr);
		
		if (systembuchungsmeldungid != 0)
		{
			getField("field." + getTableName() + ".systembuchungsmeldungid").setValue(systembuchungsmeldungid);
		}
	}

	public IField getFieldDienstreiseID() {
		return getField("field." + getTableName() + ".dienstreiseid");
	}


	public void setDienstreiseID(int dienstreiseID) {
		getFieldDienstreiseID().setValue(dienstreiseID);
	}


	public int getDienstreiseID() {
		return Format.getIntValue(getFieldDienstreiseID());
	}

	/**
	 * PersonID anhand der ChipkartenNr bestimmen udn speichern
	 * 
	 * @param chipkartenNr
	 * @throws Exception
	 */
	private void setPersonID(String chipkartenNr) throws Exception {
		int personID;
		
		personID = CoPerson.getInstance().getIdByChipkartenNr(chipkartenNr);
		
		// wenn die Person nicht bestimmt werden konnte, lade die Personen neu (es können neue Personen hinzugefügt worden sein)
		if (personID == 0)
		{
			CoPerson.getInstance().loadAll();
			personID = CoPerson.getInstance().getIdByChipkartenNr(chipkartenNr);
		}
		
		// PersonID speichern, wenn sie bestimmt werden konnte
		if (personID != 0)
		{
			setPersonID(personID);
		}
	}


	public String getStatus() throws Exception {
		return CoStatusBuchung.getInstance().getBezeichnung(getStatusID());
	}


	public void setStatus(String status) throws Exception {
		setStatusID(CoStatusBuchung.getInstance().getID(status));
	}


	public void setStatusOk() throws Exception {
		setStatusID(CoStatusBuchung.STATUSID_OK);
	}


	public void setStatusVorlaeufig() throws Exception {
		setStatusID(CoStatusBuchung.STATUSID_VORLAEUFIG);
	}


	public boolean isStatusOK() throws Exception {
		return getStatusID() == CoStatusBuchung.STATUSID_OK;
	}


	public boolean isStatusUngueltig() throws Exception {
		return getStatusID() == CoStatusBuchung.STATUSID_UNGUELTIG;
	}


	/**
	 * Die Buchung ist gültig, wenn sie den Status OK oder geändert hat.<br>
	 * Vorabbuchungen und gelöschte Buchungen werden (bei der Berechnung der Kontowerte) nicht berücksichtigt.
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isGueltig() throws Exception {
		int statusID;
		
		statusID = getStatusID();
		return statusID == CoStatusBuchung.STATUSID_OK || statusID == CoStatusBuchung.STATUSID_GEAENDERT;
	}


	/**
	 * Die Buchung hat den Status ungültig
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isUngueltig() throws Exception {
		int statusID;
		
		statusID = getStatusID();
		return statusID == CoStatusBuchung.STATUSID_UNGUELTIG;
	}


	/**
	 * Die Buchung hat den Status vorläufig
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isVorlaeufig() throws Exception {
		int statusID;
		
		statusID = getStatusID();
		return statusID == CoStatusBuchung.STATUSID_VORLAEUFIG;
	}

	
	public IField getFieldStatusGenehmigungID() {
		return getField("field." + getTableName() + ".statusgenehmigungid");
	}

	
	public int getStatusGenehmigungID() {
		return Format.getIntValue(getFieldStatusGenehmigungID().getValue());
	}


//	public String getStatusGenehmigung() throws Exception {
//		return CoStatusGenehmigung.getInstance().getBezeichnung(getStatusID());
//	}


	public void setStatusGenehmigungID(int statusID) {
		getFieldStatusGenehmigungID().setValue(statusID);
	}


	public void setStatusGenehmigungGeplant() throws Exception {
		setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_GEPLANT);
	}


	/**
	 * Die Buchung hat den Genehmigung-Status "geplant"
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isGeplant() throws Exception {
		return getStatusGenehmigungID() == CoStatusGenehmigung.STATUSID_GEPLANT;
	}


	/**
	 * Die Buchung hat den Genehmigung-Status "genehmigt"
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isGenehmigt() {
		return getStatusGenehmigungID() == CoStatusGenehmigung.STATUSID_GENEHMIGT;
	}


	/**
	 * Die Buchung hat den Genehmigung-Status "abgelehnt"
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isAbgelehnt() {
		return getStatusGenehmigungID() == CoStatusGenehmigung.STATUSID_ABGELEHNT;
	}


	/**
	 * Die Buchung hat den Genehmigung-Status "gelöscht"
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isGeloescht()  {
		return getStatusGenehmigungID() == CoStatusGenehmigung.STATUSID_GELOESCHT;
	}


//	private void setStatusBeantragt() throws Exception {
//		setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_BEANTRAGT);
//	}
//
//
//	private void setStatusGenehmigt() throws Exception {
//		setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_GENEHMIGT);
//	}


	public IField getFieldBuchungsartID() {
		return getField("field." + getTableName() + ".buchungsartid");
	}


	public void setBuchungsart(String buchungsart) throws Exception {
		setBuchungsartID(CoBuchungsart.getInstance().getID(buchungsart));
	}


	public void setBuchungsartID(int buchungsartID) {
		getFieldBuchungsartID().setValue(buchungsartID);
	}


	public int getBuchungsartID() {
		return Format.getIntValue(getFieldBuchungsartID().getValue());
	}


	public String getBuchungsart() throws Exception {
		return CoBuchungsart.getInstance().getBezeichnung(getBuchungsartID());
	}


	public CoBuchungsart getCoBuchungsart() throws Exception {
		CoBuchungsart coBuchungsart;
		
		coBuchungsart = new CoBuchungsart();
		coBuchungsart.loadByID(getBuchungsartID());
		
		if (coBuchungsart.moveFirst())
		{
			return coBuchungsart;
		}
		
		return null;
	}


	public int getBuchungsartIDOriginal() {
		return Format.getIntValue(getFieldBuchungsartID().getOriginalValue());
	}


	public CoBuchungsart getCoBuchungsartOriginal() throws Exception {
		CoBuchungsart coBuchungsart;
		
		coBuchungsart = new CoBuchungsart();
		coBuchungsart.loadByID(getBuchungsartIDOriginal());
		
		if (coBuchungsart.moveFirst())
		{
			return coBuchungsart;
		}
		
		return null;
	}


	public boolean isDr() throws Exception {
		return getBuchungsartID() == CoBuchungsart.ID_DIENSTREISE;
	}


	public String getResIdFieldDatumBis() {
		return "virt.field.buchung.datumbis";
	}


	public void addFieldDatumBis() {
		addField(getResIdFieldDatumBis());
	}


	public IField getFieldDatumBis() {
		return getField(getResIdFieldDatumBis());
	}


	public Date getDatumBis() {
		return Format.getDateValue(getFieldDatumBis());
	}


	public void setDatumBis(Date datum) {
		if (getFieldDatumBis() == null)
		{
			addFieldDatumBis();
		}
		
		getFieldDatumBis().setValue(datum);
	}


	public IField getFieldErstelltAm() {
		return getField("field." + getTableName() + ".erstelltam");
	}


	public Date getErstelltAm() {
		return Format.getDateValue(getFieldErstelltAm());
	}
	

	public void setErstelltAm(Date datum) {
		getFieldErstelltAm().setValue(Format.getStringMitUhrzeit(Format.getGregorianCalendar(datum)));
	}
	

	public IField getFieldVertreter() {
		return getField("virt.field.buchung.vertreter");
	}


	public void setVertreter(String vertreter) {
		getFieldVertreter().setValue(vertreter);
	}
	

	public String getVertreter() {
		return Format.getStringValue(getFieldVertreter().getValue());
	}
	

	public String getResIdFreigabeMoeglich() {
		return "virt.field.buchung.isfreigabemoeglich";
	}


	public IField getFieldFreigabeMoeglich() {
		return getField(getResIdFreigabeMoeglich());
	}


	public boolean isFreigabeMoeglich() {
		
		return Format.getBooleanValue(getFieldFreigabeMoeglich());
	}


	private void setFreigabeMoeglich(boolean freigabeMoeglich) {
		getFieldFreigabeMoeglich().setValue(freigabeMoeglich);
	}
	

	/**
	 * Die Buchung darf geändert werden, da sie nicht für die angemeldete Person selbst ist 
	 * oder für die angemeldete Person selbst und Änderungen sind zulässig
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isBuchungsaenderungZulaessig() throws Exception {
		return !isSelbstbuchung() || isSelbstbuchungAenderungZulaessig();
	}
	

	/**
	 * Die Buchung ist für die aktuell angemeldete Person selbst
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isSelbstbuchung() throws Exception {
		return UserInformation.isPerson(getPersonID());
	}
	

	/**
	 * Die Buchung ist für die aktuell angemeldete Person selbst und darf geändert werden, da sie vorläufig und eine freigegebene Buchungsart ist
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isSelbstbuchungAenderungZulaessig() throws Exception {
		CoBuchungsart coBuchungsart;
		
		coBuchungsart = getCoBuchungsart();

		return UserInformation.isPerson(getPersonID()) 
				// bei vorläufigen Kommen-Buchungen darf die Uhrzeit geändert werden, auch wenn es keine Selbstbuchung ist
				&& (coBuchungsart == null || coBuchungsart.isSelbstbuchungZulaessig() || getBuchungsartID() == CoBuchungsart.ID_KOMMEN
				|| getBuchungsartID() == CoBuchungsart.ID_VORLESUNG) // TODO Gleitzeitkorrekturbeleg, VL Wiebe, alle Stellen markiert
				&& (CoStatusBuchung.isSelbstbuchungAenderungZulaessig(getStatusID()) || getBuchungsartID() == CoBuchungsart.ID_VORLESUNG || isNew())
				&& CoStatusGenehmigung.isSelbstbuchungAenderungZulaessig(getStatusGenehmigungID())
				;
	}
	
	
	/**
	 * Prüft, ob der übergebene Genehmigungsstatus erteilt werden kann
	 * 
	 * @param nextStatusGenehmigungID
	 * @return
	 * @throws Exception
	 */
	public boolean isFreigabeMoeglich(int nextStatusGenehmigungID) {
		CoVertreter coVertreter;
		
		// Feld zum Speichern der bereits bestimmten Freigabe
		if (getFieldFreigabeMoeglich() == null)
		{
			addField(getResIdFreigabeMoeglich());
		}
		
		try 
		{
			// beim ersten Zugriff Freigabemöglichkeit bestimmen
			if (getFieldFreigabeMoeglich().getValue() == null)
			{
				coVertreter = new CoVertreter();
				coVertreter.loadOhneFreigabe(getPersonID(), getDatum(), getDatum());

				setFreigabeMoeglich(CoBuchungsart.getInstance().isFreigabeMoeglich(getBuchungsartID(), getStatusGenehmigungID(), nextStatusGenehmigungID, 
						getPersonID(), coVertreter.hasNoRows()));
				setModified(false);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return isFreigabeMoeglich();
	}
	
	
	/**
	 * Prüft die Anzahl der aktuell möglichen Freigaben
	 * @param m_nextStatusGenehmigungID 
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getAnzahlFreigabeMoeglich(int m_nextStatusGenehmigungID){
		int anzahl;
		
		if (!moveFirst())
		{
			return 0;
		}
		
		anzahl = 0;
		do
		{
			if (isFreigabeMoeglich(m_nextStatusGenehmigungID))
			{
				++anzahl;
			}
		} while (moveNext());

		return anzahl;
	}
	
	
	/**
	 * Vor dem Speichern müssen die geänderten/neuen Buchungen zwischengespeichert werden, um an diesen Tagen die Kontowerte zu aktualisieren
	 * 
	 * @see framework.business.cacheobject.CacheObject#save()
	 */
	public void save() throws Exception{
		String fehlertext;
		Date datumLetzteAuszahlung;
		CoBuchung coChangedBuchungen;
		CoFreigabe coFreigabe;
		
		
		// alle geänderten Rows holen
		coChangedBuchungen = new CoBuchung();
		getChangedRows(coChangedBuchungen);

		// Datum der letzten Auszahlung laden, da danach keine Änderungen gemacht werden dürfen
		datumLetzteAuszahlung = CoKontowert.getDatumLetzteAuszahlung(getPersonID());

		// Datum der geänderten Rows prüfen
		if (!UserInformation.getInstance().isPersonalverwaltung() && coChangedBuchungen.moveFirst() && datumLetzteAuszahlung != null)
//		if (coChangedBuchungen.moveFirst() && datumLetzteAuszahlung != null)
		{
			do
			{
				// Buchungen in der Vergangenheit können nicht geändert werden
				if (coChangedBuchungen.getDatum().before(datumLetzteAuszahlung))
				{
					// Ausnahme: Löschen von vorläufigen Buchungen
					moveToID(coChangedBuchungen.getID());
					if (Format.getIntValue(getFieldStatusID().getOriginalValue()) == CoStatusBuchung.STATUSID_VORLAEUFIG
							&& Format.getIntValue(getFieldStatusGenehmigungID().getOriginalValue()) != CoStatusGenehmigung.STATUSID_GENEHMIGT
							&& (coChangedBuchungen.getStatusID() == CoStatusBuchung.STATUSID_UNGUELTIG
							|| coChangedBuchungen.getStatusID() == CoStatusBuchung.STATUSID_VORLAEUFIG) // Ende-Buchung
							&& coChangedBuchungen.getStatusGenehmigungID() == CoStatusGenehmigung.STATUSID_GELOESCHT)
					{
						continue;
					}
					
					Messages.showErrorMessage("Speichern abgebrochen", "Es können keine Buchungen vor dem " + Format.getString(datumLetzteAuszahlung)
					+ " (Datum der letzten Auszahlung) gespeichert werden. Bitte Administrator/Buchhaltung kontaktieren.");
					return;
				}

			} while (coChangedBuchungen.moveNext());
		}


		// Datum der Buchung prüfen, für sich selbst nicht in der Vergangenheit
//		fehlertext = validateSelbstbuchung(coChangedBuchungen);
//		if (fehlertext != null)
//		{
//			Messages.showErrorMessage("Speichern abgebrochen", fehlertext);
//			return;
//		}


		// Urlaubsplanung prüfen
		fehlertext = validateUrlaub(coChangedBuchungen);
		if (fehlertext != null)
		{
			Messages.showErrorMessage("Speichern abgebrochen", fehlertext);
			return;
		}
		

		// ggf. Freigabe-Eintrag erstellen
		coFreigabe = createFreigabePlanung();
		if (coFreigabe != null)
		{
			// Status der Buchung setzen
			setStatusGenehmigungGeplant();
		}
		
		// bei Krank-Buchungen alle anderen für den Tag löschen
		checkKrank();

		// speichern
		if (getFieldDatumBis() != null)
		{
			removeField(getResIdFieldDatumBis());
		}
		if (getFieldUhrzeitBis() != null)
		{
			removeField(getResIdFieldUhrzeitBis());
		}
		super.save();

		
		// Freigabe wegen Bezug zur BuchungID erst nach dem Speichern der neuen Buchung speichern
		if (coFreigabe != null)
		{
			coFreigabe.save();
		}
		
		// Kontowerte für geänderte Rows aktualisieren
		coChangedBuchungen.updateKontowerte();
	}


	/**
	 * bei neuen Krank-Buchungen alle anderen Buchungen für den Tag auf ungültig setzen
	 * 
	 * @throws Exception
	 */
	private void checkKrank() throws Exception {
		CoBuchung coBuchung;

		// neue Krank-Buchung
		if (isNew() && getStatusID() != CoStatusBuchung.STATUSID_UNGUELTIG && CoBuchungsart.isKrank(getBuchungsartID()) && getUhrzeitAsInt() == 0)
		{
			coBuchung = new CoBuchung();
			coBuchung.loadTag(getPersonID(), getDatum());
			if (!coBuchung.moveFirst())
			{
				return;
			}
			
			// alle Buchungen durchlaufen und Status setzen
			coBuchung.begin();
			do
			{
				coBuchung.setStatusID(CoStatusBuchung.STATUSID_UNGUELTIG);
				coBuchung.updateGeaendertVonAm();
			} while (coBuchung.moveNext());
			
			coBuchung.save();
		}
	}


	/**
	 * Für neue Buchungen einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	private CoFreigabe createFreigabePlanung() throws Exception {
		int statusID;
		CoBuchungsart coBuchungsart;
		CoFreigabe coFreigabe;
		
		// Freigabe wird für neue vorläufige Buchungen erstellt, die vom MA freigegeben werden
		statusID = getStatusID();
		coBuchungsart = getCoBuchungsart();
		if (isNew()
				&& coBuchungsart != null && coBuchungsart.isFreigabeMa()
				&& (statusID == CoStatusBuchung.STATUSID_VORLAEUFIG
				// oder Vorlesung-OK-Buchung // TODO Gleitzeitkorrekturbeleg, VL Wiebe, alle Stellen markiert
				|| (statusID == CoStatusBuchung.STATUSID_OK && isSelbstbuchung()
						&& (getBuchungsartID() == CoBuchungsart.ID_VORLESUNG || getBuchungsartID() == CoBuchungsart.ID_GEHEN)))
				) 
		{
			// Status-Eintrag erzeugen
			coFreigabe = new CoFreigabe();
			coFreigabe.createNew(getID(), getStatusID(), CoStatusGenehmigung.STATUSID_GEPLANT, UserInformation.getPersonID(), new Date());
			return coFreigabe;
		}

		return null;
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createFreigabeBeantragt() throws Exception {
		createFreigabe(CoStatusGenehmigung.STATUSID_BEANTRAGT);
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createFreigabeVertreter() throws Exception {
		createFreigabe(CoStatusGenehmigung.STATUSID_FREIGEGEBEN_VERTRETER);
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createFreigabeAl() throws Exception {
		createFreigabe(CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AL);
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createFreigabeAusbilder() throws Exception {
		createFreigabe(CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AUSBILDER);
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createFreigabePb() throws Exception {
		createFreigabe(CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB);
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createFreigabeGenehmigt() throws Exception {
		createFreigabe(CoStatusGenehmigung.STATUSID_GENEHMIGT);
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createFreigabeAbgelehnt() throws Exception {
		createFreigabe(CoStatusGenehmigung.STATUSID_ABGELEHNT);
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void createFreigabeGeloescht() throws Exception {
		createFreigabe(CoStatusGenehmigung.STATUSID_GELOESCHT);
	}


	/**
	 * Für eine einzelne Buchung einen Freigabe-Eintrag erstellen und den aktuellen Status der Genehmigung anpassen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	private void createFreigabe(int statusGenehmigungID) throws Exception {
		int buchungsartID, wertZeit;
		Object bookmark;
		CoBuchung coBuchungEnde;
		CoFreigabe coFreigabe;
		CoVertreter coVertreter;
		
		bookmark = getBookmark();
		coBuchungEnde = new CoBuchung();
		coBuchungEnde.loadAntragEnde(this);


		// Status-Eintrag erzeugen
		coFreigabe = new CoFreigabe();
		coFreigabe.createNew(getID(), getStatusID(), statusGenehmigungID, UserInformation.getPersonID(), new Date());
		coFreigabe.save();

		
		// Freigabe Vertreter erst wenn alle Vertreter freigegeben haben
		if (statusGenehmigungID == CoStatusGenehmigung.STATUSID_FREIGEGEBEN_VERTRETER)
		{
			coVertreter = new CoVertreter();
			
			// Freigabe speichern
			coVertreter.load(getPersonID(), UserInformation.getPersonID(), getDatum(), getDatum());
			coVertreter.setIstFreigegebenForAll();
			coVertreter.save();
			
			// prüfen ob es weitere Vertreter gibt die noch nicht freigegeben haben
			coVertreter.loadOhneFreigabe(getPersonID(), getDatum(), getDatum());
			if (coVertreter.hasRows())
			{
				return;
			}
		}


		// Status der Buchung setzen
		if (!isEditing())
		{
			begin();
		}
		setStatusGenehmigungID(statusGenehmigungID);
		
		// Bei Genehmigung Status von vorläufig auf OK ändern
		if (statusGenehmigungID == CoStatusGenehmigung.STATUSID_GENEHMIGT 
				&& CoBuchungsart.isUrlaubFA(getBuchungsartID()) && !isStatusUngueltig())
		{
			setStatusOk();
		}
		
		// wenn ein Lösch-Antrag eines bereits genehmigter Antrags abgelehnt wird, setze den Antrag wieder auf genehmigt
		if (statusGenehmigungID == CoStatusGenehmigung.STATUSID_ABGELEHNT && isStatusUngueltig())
		{
			if (CoBuchungsart.isDrDg(getBuchungsartID()) || getBuchungsartID() == CoBuchungsart.ID_ORTSFLEX_ARBEITEN)
			{
				setStatusVorlaeufig();
			}
			else // Urlaub/FA
			{
				setStatusOk();
			}
			setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_GENEHMIGT);
		}


		// Änderung dokumentieren
		updateGeaendertVonAm();
		save();

		moveTo(bookmark);


		// ggf. Gehen-Buchung anpassen, nur bei Kommen und Gehen, alle anderen Buchungen müssen explizit genehmigt werden
		if (coBuchungEnde.hasRows())
		{
			buchungsartID = coBuchungEnde.getBuchungsartID();
			if (buchungsartID != CoBuchungsart.ID_KOMMEN && buchungsartID != CoBuchungsart.ID_GEHEN)
			{
				// nächste Buchung prüfen, falls es 2 zur gleichen Zeit gibt
				wertZeit = coBuchungEnde.getUhrzeitAsInt();
				if (coBuchungEnde.moveNext())
				{
					buchungsartID = coBuchungEnde.getBuchungsartID();
					if (wertZeit != coBuchungEnde.getUhrzeitAsInt() || (buchungsartID != CoBuchungsart.ID_KOMMEN && buchungsartID != CoBuchungsart.ID_GEHEN))
					{
						movePrev();
						return;
					}
				}
				
				return;
			}
			
			if (!coBuchungEnde.isEditing())
			{
				coBuchungEnde.begin();
			}
			coBuchungEnde.setStatusGenehmigungID(statusGenehmigungID);

			// Änderung dokumentieren
			coBuchungEnde.updateGeaendertVonAm();
			coBuchungEnde.save();
		}
	}


	/**
	 * Einen einzelnen Antrag löschen
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public void deleteAntrag() throws Exception {
		int statusGenehmigungID;
		Object bookmark;
		
		bookmark = getBookmark();
		statusGenehmigungID = getStatusGenehmigungID();
		
		// Status der Buchung und Status Genehmigung setzen
		if (!isEditing())
		{
			begin();
		}
		setStatusID(CoStatusBuchung.STATUSID_UNGUELTIG);
//		getFieldStatusGenehmigungID().setValue(null);
//		setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_GELOESCHT);
		
		// wenn der Antrag bereits genehmigt war, muss das Löschen genehmigt werden
		if (isGenehmigt())
		{
			createFreigabeBeantragt();
		}
		else // sonst die beabsichtigte Freigabe speichern
		{
			createFreigabe(CoStatusGenehmigung.STATUSID_GELOESCHT);
		}

		// Änderungen speichern
//		updateGeaendertVonAm();
//		save();
		
		moveTo(bookmark);
		
		// Endbuchung löschen
		deleteAntragEnde(statusGenehmigungID);
	}

	
	/**
	 * Ende des aktuellen Antrags löschen
	 * 
	 * @param statusGenehmigungID
	 * @throws Exception
	 */
	private void deleteAntragEnde(int statusGenehmigungID) throws Exception {
		int buchungsartID;
		CoBuchung coBuchung;
		
		
		// nächste Buchung laden und ggf. löschen als Ende des Antrags
		coBuchung = new CoBuchung();
		coBuchung.loadAntragEnde(getPersonID(), getDatum(), getUhrzeitAsInt(), getStatusID(), statusGenehmigungID);
		if (!coBuchung.moveFirst())
		{
			return;
		}
		
		// wenn es ein vorläufiges Ende ist löschen
		buchungsartID = coBuchung.getBuchungsartID();
		if ((coBuchung.isVorlaeufig() || (coBuchung.isStatusOK() && coBuchung.getPersonID() == 6079241))// TODO Gleitzeitkorrekturbeleg, VL Wiebe, alle Stellen markiert
				&& (buchungsartID == CoBuchungsart.ID_KOMMEN || buchungsartID == CoBuchungsart.ID_GEHEN || buchungsartID == CoBuchungsart.ID_ENDE_DIENSTGANG_DIENSTREISE))
		{
			coBuchung.begin();
			coBuchung.setStatusID(CoStatusBuchung.STATUSID_UNGUELTIG);
			coBuchung.setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_GELOESCHT);
//			coBuchung.getFieldStatusGenehmigungID().setValue(null);
			coBuchung.updateGeaendertVonAm();
			coBuchung.save();
		}
		// TODO passt so nicht mehr ganz mit loadAntragEnde zusammen, wenn genehmigte Buchung gelöscht wird und abgelehnt

	}


	/**
	 * Buchungen für sich selbst sind nicht für die Vergangenheit möglich
	 * 
	 * @param coChangedBuchungen
	 * @return
	 * @throws Exception
	 */
	public boolean validateSelbstbuchung() throws Exception {
		Date datum, datumHeute;
		
		// nur neue Buchungen betrachten
		if (!isNew())
		{
			return true;
		}

		datum = getDatum();
		datumHeute = Format.getDate0Uhr(new Date());

		// neue Selbstbuchungen nur für die Zukunft
		if (!datum.after(datumHeute) && isSelbstbuchung())
		{
			Messages.showErrorMessage("Speichern abgebrochen", 
					"Buchungen für sich selbst sind nicht für die Vergangenheit möglich. Bitte Personalverwaltung kontaktieren.");
			return false;
		}

		return true;
	}


	/**
	 * Buchungen der Urlaubsplanung (vorläufige Urlaubsbuchungen untersuchen)
	 * 
	 * @param coChangedBuchungen
	 * @return
	 * @throws Exception
	 */
	private String validateUrlaub(CoBuchung coChangedBuchungen) throws Exception {
		int personID, buchungsartID, anzahlFa;
		int anzahlUrlaubNeu, anzahlResturlaub, anzahlGeplantenUrlaub, jahr, jahrUrlaubsplanung, jahrHeute, jahresurlaub, anzahlUrlaubMoeglich;
		Date datum, lastUrlaubstag, datumUrlaubBis, datumHeute;
		GregorianCalendar gregDatum;
		CoBuchung coBuchung;
		CoKontowert coKontowert;
		
		
		if (!coChangedBuchungen.moveFirst())
		{
			return null;
		}
		
		personID = getPersonID();
		anzahlUrlaubNeu = 0;
		jahrUrlaubsplanung = 0;
		datumUrlaubBis = CoFirmenparameter.getInstance().getDatumUrlaubsplanungBis();
		lastUrlaubstag = null;
		datum = null;
		gregDatum = null;
		coBuchung = new CoBuchung();
		
		datumHeute = Format.getDate0Uhr(new Date());
		jahrHeute = Format.getGregorianCalendar(datumHeute).get(GregorianCalendar.YEAR);
		

		// geänderte Buchungen durchlaufen und prüfen
		// Schleife eigentlich unnötig, da auch bei mehrtätigem Urlaub jede Buchung einzeln gespeichert wird
		do
		{
			// nur neue Buchungen betrachten
			if (!coChangedBuchungen.isNew())
			{
				continue;
			}
			
			buchungsartID = coChangedBuchungen.getBuchungsartID();
			
			// vorläufige Urlaubsbuchungen/SU/FA werden geprüft
			if ((buchungsartID == CoBuchungsart.ID_URLAUB || buchungsartID == CoBuchungsart.ID_SONDERURLAUB || buchungsartID == CoBuchungsart.ID_FA)
					&& coChangedBuchungen.getStatusID() == CoStatusBuchung.STATUSID_VORLAEUFIG)
			{
				datum = coChangedBuchungen.getDatum();
				gregDatum = Format.getGregorianCalendar(datum);
				jahr = gregDatum.get(GregorianCalendar.YEAR);

				// für die Urlaubsplanung ist die Begrenzung vorgegeben
				if (datum.after(datumUrlaubBis) && (!UserInformation.getInstance().isPersonalverwaltung() || isSelbstbuchung()))
				{
					return "Die Urlaubsplanung ist nur bis zum " + Format.getString(datumUrlaubBis) + " möglich.";
				}

				// Urlaubsplanung nur für die Zukunft
				if (!datum.after(datumHeute) && (!UserInformation.getInstance().isPersonalverwaltung() && isSelbstbuchung()))
				{
					return "Die Urlaubsplanung ist nicht für die Vergangenheit möglich. Bitte Personalverwaltung kontaktieren.";
				}
				
				// prüfen, ob schon Urlaubsbuchungen für den Tag vorhanden sind
				coBuchung.loadUrlaubsbuchungenNichtUngueltig(personID, datum);
				if (coBuchung.getRowCount() > 0 && isNew())
				{
					if (buchungsartID == CoBuchungsart.ID_URLAUB) // keine doppelte Urlaubsbuchung möglich
					{
						return "Für den " + Format.getString(datum) + " gibt es bereits eine Buchung '" 
								+ CoBuchungsart.getInstance().getBezeichnung(coBuchung.getBuchungsartID()) + "'";
					}
					// Urlaub kann durch SU/FA ersetzt werden
					else if (!Messages.showYesNoMessage("Warnung", "Für den " + Format.getString(datum) + " gibt es bereits eine Buchung '" 
								+ CoBuchungsart.getInstance().getBezeichnung(coBuchung.getBuchungsartID()) + "'. Trotzdem speichern?"))
					{
						return "Buchung abgebrochen";
					}
				}

				// nur Urlaubstage zählen, für SU gibt es keine Einschränkung, 2 FA/Monat wurde bereits abgefragt
				if (buchungsartID == CoBuchungsart.ID_URLAUB)
				{
					// Anzahl der neuen Urlaubstage zählen
					if (isNew())
					{
						++anzahlUrlaubNeu;
					}

					// beim ersten Urlaubstag das Jahr speichern
					if (jahrUrlaubsplanung == 0)
					{
						jahrUrlaubsplanung = jahr;
						lastUrlaubstag = datum;
					}

					// den letzten Urlaubstag speichern, wenn er über die Jahresgrenze geht nur den letzten Tag des ersten Jahres
					if (jahr == jahrUrlaubsplanung && datum.after(lastUrlaubstag))
					{
						lastUrlaubstag = datum;
					}
				}
				else if (buchungsartID == CoBuchungsart.ID_FA)
				{
					// bei ganztägigem FA muss die Anzahl der FA-Tage im Monat abgefragt werden
					if (coChangedBuchungen.getUhrzeitAsInt() == 0)
					{
						anzahlFa = getAnzahlFaTageMonat(personID, gregDatum);
						if (anzahlFa  > 1 && (!UserInformation.getInstance().isPersonalverwaltung() || isSelbstbuchung()))
						{
							return "Für den Monat mit dem " + Format.getString(datum) + " gibt es bereits " + anzahlFa + " FA-Tage.";
						}
					}
				}
			}
	
		} while (coChangedBuchungen.moveNext());

		
		// wenn keine Urlaubsplanung gemacht wurde, ist alles in Ordnung
		if (anzahlUrlaubNeu == 0)
		{
			return null;
		}
		
		
		// Resturlaub am Ende des Jahres prüfen
		gregDatum.set(GregorianCalendar.YEAR, jahrUrlaubsplanung);
		gregDatum.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
		gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 31);
		
		coKontowert = new CoKontowert();
		coKontowert.load(personID, Format.getDateValue(gregDatum));
		
		if (coKontowert.getRowCount() == 0)
		{
			coKontowert.loadLastEintrag(personID);
		}
		
		anzahlResturlaub = coKontowert.getResturlaub();
		anzahlGeplantenUrlaub = getAnzahlGeplantenUrlaub(personID, null, jahrUrlaubsplanung);
		
		
		// Urlaubsplanung dieses Jahr
		if (jahrUrlaubsplanung == jahrHeute)
		{
			// wenn jetzt mehr Urlaub geplant werden soll als Resturlaub am Jahresende abzgl. der noch in diesem Jahr geplanten Urlaubstage
			anzahlUrlaubMoeglich = anzahlResturlaub - anzahlGeplantenUrlaub + PUFFER_URLAUBSTAGE;
			if (anzahlUrlaubNeu > anzahlUrlaubMoeglich)
			{
				// Buchung nicht zulässig
				return "Es dürfen in diesem Jahr nur noch " + anzahlUrlaubMoeglich + " Urlaubstage geplant werden "
						+ "(Fehler am " + Format.getString(datum) + ").";
			}
		}
		// Urlaubsplanung nächstes Jahr
		else
		{
			CoPerson coPerson;
			coPerson = new CoPerson();
			coPerson.loadByID(personID);
			jahresurlaub = coPerson.getJahresurlaub();
			
			// es darf nicht mehr als der Jahresurlaub geplant werden
			anzahlUrlaubMoeglich = jahresurlaub - anzahlGeplantenUrlaub + PUFFER_URLAUBSTAGE;
			if (anzahlUrlaubNeu > anzahlUrlaubMoeglich)
			{
				return "Es dürfen im Jahr " + jahrUrlaubsplanung + " nur noch " + anzahlUrlaubMoeglich 
						+ " Urlaubstage geplant werden " + "(Fehler am " + Format.getString(datum) + ").";
			}
		}
		
		return null;
	}


	/**
	 * Zeiten/Reise- und Projektzeit für DR/DG prüfen
	 * 
	 * @return fehler oder null
	 * @throws Exception
	 */
	public String validateZeitenDrDg() throws Exception {
		int dauer, angegebeneZeiten;
		
		moveFirst();
		do
		{
			dauer = getUhrzeitBisAsInt() - getUhrzeitAsInt();
			angegebeneZeiten = Format.getIntValue(getField(CoDienstreisezeit.getResIdProjektzeit()))
					+ Format.getIntValue(getField(CoDienstreisezeit.getResIdReisezeit()));
			
			// bei mehr als 10 Stunden muss Reise und projektzeit angegeben werden
			if (dauer > 10*60 && angegebeneZeiten == 0)
			{
				return "Bei mehr als 10 Stunden müssen Reise- und Projektzeit angegeben werden.";
			}
		} while(moveNext());

		return null;
	}


	/**
	 * Kontowerte für die Tage der Buchungen aktualisieren.<br>
	 * Ggf. werden die Kontowerte für einen Tag mehrfach aktualisiert. Dies wird aber in Kauf genommen, 
	 * da so sichergestellt ist, dass die Aktualisierung wirklich funktioniert und der Trigger zum Berechnen der Überstunden korrekt aufgerufen wird.
	 * 
	 * @throws Exception
	 */
	public void updateKontowerte() throws Exception {
		int personID;
		CoKontowert coKontowert;
		
		coKontowert = new CoKontowert();
		
		if (!moveFirst())
		{
			return;
		}

		do
		{
			// prüfen, ob die Buchung einer Person zugeordnet ist
			personID = getPersonID();
			
			if (personID > 0)
			{
				if (!coKontowert.updateKontowerte(personID, getDatum()))
				{
					return;
				}
			}
		} while (moveNext());
	}


	/**
	 * Prüft die nächste und letzte Buchung einer Person. <br>
	 * Wenn sie gleich sind bzw. vom gleichen Typ, benutze die letzte Buchung zur Darstellung der Anwesenheit.
	 * 
	 * @param row
	 * @param col
	 * @param setBuchungsartID
	 * @return
	 * @throws Exception
	 */
	public static int checkBuchungsart(int personID) throws Exception {
		int buchungsartID, lastBuchungsartID, nextBuchungsartID;
		Date datum;
		CoBuchung coBuchung;
		
		datum = new Date();
		
		// prüfen ob ein Zeitmodell mit Arbeitszeit vorliegt, da sonst nicht die nächste und letzte Buchung für einen Arbeitstag bestimmt werden kann 
		if (!checkzeitmodell(personID, datum))
		{
			return 0;
		}
		
		// nächste Buchung
		coBuchung = new CoBuchung();
		coBuchung.loadNext(personID, datum);
		if (coBuchung.getRowCount() == 0)
		{
			return 0;
		}
		nextBuchungsartID = coBuchung.getBuchungsartID();

		// letzte Buchung
		coBuchung.loadLast(personID, datum);
		if (coBuchung.getRowCount() == 0)
		{
			return 0;
		}
		lastBuchungsartID = coBuchung.getBuchungsartID();

		
		// Buchungsarten prüfen
		buchungsartID = checkKrank(lastBuchungsartID, nextBuchungsartID);
		if (buchungsartID != 0)
		{
			return buchungsartID;
		}
		
		buchungsartID = checkUrlaub(lastBuchungsartID, nextBuchungsartID);
		if (buchungsartID != 0)
		{
			return buchungsartID;
		}

		buchungsartID = checkElternzeit(lastBuchungsartID, nextBuchungsartID);
		if (buchungsartID != 0)
		{
			return buchungsartID;
		}

		return buchungsartID;
	}


	/**
	 * Prüfen ob ein Zeitmodell mit Arbeitszeit vorliegt, da sonst nicht die nächste und letzte Buchung für einen Arbeitstag bestimmt werden kann
	 *  
	 * @param personID
	 * @param datum
	 * @return 
	 * @throws Exception
	 */
	protected static boolean checkzeitmodell(int personID, Date datum) throws Exception {
		CoPerson coPerson;
		CoZeitmodell coZeitmodell;
		
		coPerson = new CoPerson();
		coPerson.loadByID(personID);
		
		// Wochenarbeitszeit bestimmen
		coZeitmodell = coPerson.getCoZeitmodell(datum);
		if (coZeitmodell == null || coZeitmodell.getAnzWochenstunden() == 0.)
		{
			return false;
		}
		
		return true;
	}
	

	/**
	 * Prüft, ob die Person krank ist.
	 * 
	 * @param row
	 * @param col
	 * @return
	 * @throws Exception
	 */
	private static int checkKrank(int lastBuchungsartID, int nextBuchungsartID) throws Exception {
		Set<Integer> setBuchungsartID;
		
		setBuchungsartID = new HashSet<Integer>();
		setBuchungsartID.add(CoBuchungsart.ID_KRANK);
		setBuchungsartID.add(CoBuchungsart.ID_KRANK_OHNE_LFZ);
		
		return checkBuchungsart(setBuchungsartID, lastBuchungsartID, nextBuchungsartID);
	}
	

	/**
	 * Prüft, ob die Person Urlaub hat.
	 * 
	 * @param row
	 * @param col
	 * @return
	 * @throws Exception
	 */
	private static int checkUrlaub(int lastBuchungsartID, int nextBuchungsartID) throws Exception {
		Set<Integer> setBuchungsartID;
		
		setBuchungsartID = new HashSet<Integer>();
		setBuchungsartID.add(CoBuchungsart.ID_URLAUB);
		setBuchungsartID.add(CoBuchungsart.ID_SONDERURLAUB);
		setBuchungsartID.add(CoBuchungsart.ID_FA);
		
		return checkBuchungsart(setBuchungsartID, lastBuchungsartID, nextBuchungsartID);
	}
	

	/**
	 * Prüft, ob die Person Elternzeit hat.
	 * 
	 * @param row
	 * @param col
	 * @return
	 * @throws Exception
	 */
	private static int checkElternzeit(int lastBuchungsartID, int nextBuchungsartID) throws Exception {
		Set<Integer> setBuchungsartID;
		
		setBuchungsartID = new HashSet<Integer>();
		setBuchungsartID.add(CoBuchungsart.ID_ELTERNZEIT);
		
		return checkBuchungsart(setBuchungsartID, lastBuchungsartID, nextBuchungsartID);
	}
	

	/**
	 * Prüft die nächste und letzte Buchung einer Person. <br>
	 * Wenn sie gleich sind bzw. vom gleichen Typ, benutze die letzte Buchung zur Darstellung der Anwesenheit.
	 * 
	 * @param row
	 * @param col
	 * @param setBuchungsartID
	 * @return
	 * @throws Exception
	 */
	private static int checkBuchungsart(Set<Integer> setBuchungsartID, int lastBuchungsartID, int nextBuchungsartID) throws Exception {
		
		// nächste Buchung
		if (!setBuchungsartID.contains(nextBuchungsartID))
		{
			return 0;
		}

		// letzte Buchung
		if (!setBuchungsartID.contains(lastBuchungsartID))
		{
			return 0;
		}
		
		return lastBuchungsartID;
	}


//	/**
//	 * Prüft die letzten Buchung einer Person um herauszufinden ob sie ortsflexibel oder bei WTI arbeitet.
//	 * Letzte Buchung Kommen oder OFA
//	 * 
//	 * @param personID
//	 * @return
//	 * @throws Exception
//	 */
//	public static boolean isOrtsflexArbeiten(int personID) throws Exception {
//		String sql;
//		CoBuchung coBuchung;
//		
//
//		// die letzte Buchungen laden, die OFA oder Kommen ist
//		sql = "SELECT TOP 1 * FROM " + TABLE_NAME 
//				+ " WHERE PersonID= " + personID
//				+ " AND YEAR(Datum)=YEAR(getdate()) AND DATEPART(dy, Datum) = DATEPART(dy, getdate())"
//				+ " AND (UhrzeitAsInt IS NULL OR UhrzeitAsInt <= (DATEPART(hh, getdate()) * 60 + DATEPART(mi, getdate())))"
//
//				+ " AND (BuchungsartID = " + CoBuchungsart.ID_KOMMEN + " OR BuchungsartID = " + CoBuchungsart.ID_ORTSFLEX_ARBEITEN + ")"
//				+ " AND (StatusID = " + CoStatusBuchung.STATUSID_OK + " OR StatusID =" + CoStatusBuchung.STATUSID_GEAENDERT + ")"
//
//				+ " ORDER BY UhrzeitAsInt DESC, Datum DESC";
//		//		System.out.println(sql);
//		
//		coBuchung = new CoBuchung();
//		Application.getLoaderBase().load(coBuchung, sql);
//		
//		if (coBuchung.moveFirst())
//		{
//			return coBuchung.getBuchungsartID() == CoBuchungsart.ID_ORTSFLEX_ARBEITEN;
//		}
//		
//		return false;
//	}


	/**
	 * für die aktuell geladenen Urlaubsbuchungen (aus der Urlaubsübersicht) den Status ändern
	 * im aktuellen CO wird nichts geändert
	 * 
	 * @param statusID
	 * @return Anzahl geänderter Tage
	 * @throws Exception
	 */
	public int createFreigabeUrlaub(int statusGenehmigungID) throws Exception {
		int buchungsartID, counter;
		Date datum, lastDatum;
		CoBuchung coBuchung;
		

		// alle betroffenen Urlaubsbuchungen laden
		buchungsartID = getBuchungsartID();
		coBuchung = new CoBuchung();
		coBuchung.loadUrlaubsbuchungen(getPersonID(), getDatum(), getDatumBis(), buchungsartID, getStatusID(), getStatusGenehmigungID());
		coBuchung.begin();
		
		if (!coBuchung.moveFirst())
		{
			return 0;
		}
		
		// Initialisierung
		lastDatum = new Date();
		counter = 0;


		// Urlaubstage durchlaufen
		do
		{
			datum = Format.getDate0Uhr(coBuchung.getDatum());

			// wenn es mehrere Buchungen für den gleichen Tag gibt, bearbeite nur die erste
			if (lastDatum.equals(datum))
			{
				continue;
			}

			// bei FA-Buchungen nur die mit der richtigen Uhrzeit oder bei mehrtägigen auch ohne gleiche Uhrzeit anpassen
			if (coBuchung.getBuchungsartID() == CoBuchungsart.ID_FA	&& (getUhrzeitAsInt() != coBuchung.getUhrzeitAsInt() && datum.equals(getDatum())))
			{
				continue;
			}


			// Änderung dokumentieren
			if (!coBuchung.isEditing())
			{
				coBuchung.begin();
			}
			coBuchung.updateGeaendertVonAm();

			// bei Status OK den Grund auf Urlaubsantrag setzen
			if (statusGenehmigungID == CoStatusGenehmigung.STATUSID_BEANTRAGT)
			{
				coBuchung.setGrundAenderungID(buchungsartID == CoBuchungsart.ID_FA ? 
						CoGrundAenderungBuchung.ID_FA_ANTRAG : CoGrundAenderungBuchung.ID_URLAUBSANTRAG);
			}
			
			// beim Löschen Status anpassen
			// evtl. müsste das in createFreigabe() verschoben werden
			if (statusGenehmigungID == CoStatusGenehmigung.STATUSID_GELOESCHT)
			{
				coBuchung.setStatusID(CoStatusBuchung.STATUSID_UNGUELTIG);
				
				// Antrag-Ende von FA-Buchung löschen
				if (buchungsartID == CoBuchungsart.ID_FA)
				{
					deleteAntragEnde(getStatusGenehmigungID());
				}
			}

			// wenn der Urlaub bereits genehmigt war, muss das Löschen genehmigt werden
			if (coBuchung.isGenehmigt() && statusGenehmigungID == CoStatusGenehmigung.STATUSID_GELOESCHT)
			{
				coBuchung.createFreigabeBeantragt();
			}
			else // sonst die beabsichtigte Freigabe speichern
			{
				coBuchung.createFreigabe(statusGenehmigungID);
			}


			// Anzahl geänderter Einträge zählen
			++counter;

			lastDatum = datum;
		} while (coBuchung.moveNext());

		
		// Buchungen mit geändertem Status speichern
		if (!coBuchung.isEditing())
		{
			coBuchung.begin();
		}
		coBuchung.save();


		// für aktuelle Buchung StatusGenehmigung setzen
		// wird über createfreigabe in der DB gespeichert, ist aber erst nach neuem Laden bekannt
		setStatusGenehmigungID(statusGenehmigungID);

		return counter;
	}


	/**
	 * Für alle geladenen Anträge den Status setzen und Freigabe erzeugen
	 * 
	 * @param statusID
	 * @return Anzahl geänderter Tage
	 * @throws Exception
	 */
	public int createFreigabeForAll(int statusGenehmigungID) throws Exception {
		int counter;
		CoBuchung coBuchung;
		
		
		if (!moveFirst())
		{
			return 0;
		}
		
		// Initialisierung
		counter = 0;
		coBuchung = new CoBuchung();


		// Buchungen durchlaufen
		do
		{
			coBuchung.loadByID(getID());
			coBuchung.begin();

			// beim Löschen Status anpassen
			// evtl. müsste das in createFreigabe() verschoben werden
			if (statusGenehmigungID == CoStatusGenehmigung.STATUSID_GELOESCHT)
			{
				coBuchung.setStatusID(CoStatusBuchung.STATUSID_UNGUELTIG);
				
				// Antrag-Ende löschen
				deleteAntragEnde(getStatusGenehmigungID());
			}

			// wenn der Antrag bereits genehmigt war, muss das Löschen genehmigt werden
			if (coBuchung.isGenehmigt() && statusGenehmigungID == CoStatusGenehmigung.STATUSID_GELOESCHT)
			{
				coBuchung.createFreigabeBeantragt();
			}
			else // sonst die beabsichtigte Freigabe speichern
			{
				coBuchung.createFreigabe(statusGenehmigungID);
			}


			// Anzahl geänderter Einträge zählen
			++counter;

		} while (moveNext());

		return counter;
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
