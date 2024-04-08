package pze.business.objects.projektverwaltung;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.auswertung.CoAuswertungAmpelliste;
import pze.business.objects.auswertung.CoAuswertungProjektstundenauswertung;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattProjekt;
import pze.business.objects.reftables.projektverwaltung.CoAusgabezeitraum;
import pze.ui.formulare.auswertung.FormAuswertungProjektstundenuebersicht;

/**
 * CacheObject für die Zuordnung von Projekten zu Monatseinatzblättern.<br>
 * Auswahl der anzuzeigenden Projekte wird vom Mitarbeiter erstellt.
 * 
 * @author Lisiecki
 *
 */
public class VirtCoProjekt extends AbstractCacheObject {

	public static final String TABLE_NAME = "virt.table.projekt";
	private static final String FIELD_RESID = "virt.field.projekt.";

	private int m_personID;
	
	/**
	 * Speichern der Beschriftung, da diese beim rendern der Tabelle benötigt wird
	 */
	private String m_captionBemerkung;

	

	/**
	 * Kontruktor
	 */
	public VirtCoProjekt() {
		super(TABLE_NAME);
	}
	

	/**
	 * Laden der Projekte für die Person und den Monat.<br>
	 * Alle Projekte mit Stunden für den Monat und die von der Person ausgewählten Projekte werden geladen.
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	public void load(int personID, Date datum) throws Exception {
		String select, sql;
		GregorianCalendar gregDatum;
		
		m_personID = personID;
		
		gregDatum = Format.getGregorianCalendar(datum);
		
		select = " au.ID AS AuftragID, au.KundeID AS KundeID, au.KundeID AS KundeKuerzelID, au.AuftragsNr AS AuftragsNr, "
				+ " au.Beschreibung AS AuftragsBeschreibung,  au.EdvNr AS AuftragEdvNr, au.ProjektleiterID AS AuftragProjektleiterID, "
				+ " au.StatusID AS AuftragStatusID, "
				+ " ab.ID AS AbrufID, ab.AbrufNr AS AbrufNr, ab.Beschreibung AS AbrufBeschreibung, ab.EdvNr AS AbrufEdvNr, "
				+ " ab.ProjektleiterID AS AbrufProjektleiterID, "
				+ " ab.StatusID AS AbrufStatusID, "
				+ " k.ID AS KostenstelleID, k.Bezeichnung AS Kostenstelle,"
				+ " b.ID AS BerichtsNrID, b.Bezeichnung AS BerichtsNr,"
				+ " ku.bezeichnung AS Kunde"; 

		sql = "SELECT " + select + ", m.StundenartID AS StundenartID"
				+ " FROM tblMonatseinsatzblatt m "
				+ " LEFT OUTER JOIN tblAuftrag au ON (m.AuftragID = au.ID)"
				+ " LEFT OUTER JOIN tblAbruf ab ON (m.AbrufID = ab.ID)"
				+ " LEFT OUTER JOIN tblKostenstelle k ON (m.KostenstelleID = k.ID)"
				+ " LEFT OUTER JOIN tblBerichtsNr b ON (m.BerichtsNrID = b.ID)"
				+ " LEFT OUTER JOIN rtblKunde ku ON (au.KundeID = ku.ID)"
				+ " WHERE m.PersonID = " + personID + " AND YEAR(Datum)=" + gregDatum.get(Calendar.YEAR) + " AND MONTH(Datum)=" + (gregDatum.get(Calendar.MONTH)+1)
				
				+ " UNION "
				
				+ " SELECT " + select + ", s.StundenartID AS StundenartID"
				+ " FROM stblMonatseinsatzblattProjekt s "
				+ " LEFT OUTER JOIN tblAuftrag au ON (s.AuftragID = au.ID)"
				+ " LEFT OUTER JOIN tblAbruf ab ON (s.AbrufID = ab.ID)"
				+ " LEFT OUTER JOIN tblKostenstelle k ON (s.KostenstelleID = k.ID)"
				+ " LEFT OUTER JOIN tblBerichtsNr b ON (s.BerichtsNrID = b.ID)"
				+ " LEFT OUTER JOIN rtblKunde ku ON (au.KundeID = ku.ID)"
				+ " WHERE s.PersonID = " + personID 
				
				+ " ORDER BY " + getSortFieldName();
		
		
		emptyCache();
		Application.getLoaderBase().load(this, sql);
		
		doAfterLoad();
	}
	

	/**
	 * Ampelliste gemäß den ausgewählten Kriterien laden.
	 * 
	 * @param formAmpelliste 
	 * @throws Exception
	 */
	public void loadAmpelliste(CoAuswertungAmpelliste coAuswertungAmpelliste) throws Exception {
		boolean auftraegeAusgeben, abrufeAusgeben;
		String select, selectAuftrag, selectAbruf, joinOhneProjekt, groupBy, sql, datumBisAsString;
		String where, whereProjekt, whereAbruf, whereAuftrag, whereDatum;
//		String selectedprojektleiterIDs;
		
		auftraegeAusgeben = coAuswertungAmpelliste.isAuftraegeAusgebenAktiv();
		abrufeAusgeben = coAuswertungAmpelliste.isAbrufeAusgebenAktiv();
		
		// Änderungen müssen für die Ampelliste in app_Gui 
		// und für die Projektauswertung in FormauswertungProjektstundenübersicht (Headerdescription) angepasst werden
		
		selectAuftrag = " au.ID AS AuftragID, au.KundeID AS KundeID, au.KundeID AS KundeKuerzelID, au.AbteilungID, "
				+ " au.AbteilungsleiterID  AS AuftragAbteilungsleiterID, "
				+ " au.AuftragsNr AS AuftragsNr, au.BestellNr AS BestellNr, "
				+ " au.Beschreibung AS AuftragsBeschreibung,  au.EdvNr AS AuftragEdvNr,  au.ProjektleiterID AS AuftragProjektleiterID,"
				+ " au.AbteilungKundeID AS AuftragAbteilungKundeID,  au.AnfordererKundeID AS AuftragAnfordererKundeID,  "
				+ " au.AbrechnungsartID AS AuftragAbrechnungsartID, au.StatusID AS AuftragStatusID, au.DatumTermin AS AuftragLiefertermin, "
				+ " au.DatumFertigmeldung AS AuftragDatumFertigmeldung, au.DatumBestellung AS DatumBestellung, "
				+ " au.DatumMeldungVersendet AS AuftragDatumMeldungVersendet, au.DatumBerechnetBis AS AuftragDatumBerechnetBis, "
				+ " au.Startwert AS AuftragStartwert, "
				+ " auBudget.Sollstunden AS AuftragSollstunden, au.UVG AS AuftragUVG, au.Puffer AS AuftragPuffer, au.Bestellwert AS AuftragBestellwert,"
				+ " auBudget.IstStunden AS AuftragIstStunden,"
				+ " auBudget.AbrechenbareStunden AS AuftragAbrechenbareStunden, "
				+ " auBudget.WertZeitVerbleibend AS AuftragWertZeitVerbleibend, "
				+ " auBudget.VerbrauchBestellwert AS AuftragVerbrauchBestellwert, auBudget.VerbrauchSollstunden AS AuftragVerbrauchSollstunden, "
				+ " auk.ID AS AuftragKostenstelleID, auk.Bezeichnung AS AuftragKostenstelle ";
				
		selectAbruf = " ab.ID AS AbrufID, ab.FachgebietID, ab.PNummerID, ab.ZuordnungID, ab.PaketID,"
				+ " ab.AbrufNr AS AbrufNr, ab.DatumAbruf, "
				+ " ab.Beschreibung AS AbrufBeschreibung, ab.EdvNr AS AbrufEdvNr,  ab.ProjektleiterID AS AbrufProjektleiterID,"
				+ " ab.AbteilungsleiterAbrufID AS AbrufAbteilungsleiterID, "
				+ " ab.AbteilungKundeID AS AbrufAbteilungKundeID,  ab.AnfordererKundeID AS AbrufAnfordererKundeID,  "
				+ " ab.AbrechnungsartID AS AbrufAbrechnungsartID, ab.StatusID AS AbrufStatusID, ab.DatumTermin AS AbrufLiefertermin, "
				+ " ab.DatumFertigmeldung AS AbrufDatumFertigmeldung, ab.DatumFreigabeRechnungAG AS DatumFreigabeRechnungAG, "
				+ " ab.DatumMeldungVersendet AS AbrufDatumMeldungVersendet, ab.DatumBerechnetBis AS AbrufDatumBerechnetBis, "
				+ " ab.Startwert AS AbrufStartwert, "
				+ " abBudget.Sollstunden AS AbrufSollstunden, ab.UVG AS AbrufUVG, ab.Puffer AS AbrufPuffer, ab.Bestellwert AS AbrufBestellwert, "
				+ " abBudget.IstStunden AS AbrufIstStunden, "
				+ " abBudget.AbrechenbareStunden AS AbrufAbrechenbareStunden, "
				+ " abBudget.WertZeitVerbleibend AS AbrufWertZeitVerbleibend,"
				+ " abBudget.VerbrauchBestellwert AS AbrufVerbrauchBestellwert, abBudget.VerbrauchSollstunden AS AbrufVerbrauchSollstunden, "
				+ " abk.ID AS AbrufKostenstelleID, abk.Bezeichnung AS AbrufKostenstelle ";

		// WertZeit kann man hier nicht bestimmen, da Aufträge und Abrufe separat geladen werden. 
		// Bei Aufträgen mit Abrufen würden nur die Stunden berücksichtigt werden, die ohne Abruf gebucht wurden.
		// Bei Bedarf muss daher die DB-Funktion funBudget angepasst werden
		select = "SELECT " + selectAuftrag + ", " + selectAbruf 
//	TODO Stundenart			+ ", m.StundenartID AS StundenartID"
				+ ", ku.bezeichnung AS Kunde";

		datumBisAsString = coAuswertungAmpelliste.getStringDatumBisForSql();
		joinOhneProjekt = " JOIN rtblKunde ku ON (au.KundeID = ku.ID)"
				+ " LEFT OUTER JOIN stblAuftragKostenstelle sauk ON (au.ID = sauk.AuftragID) LEFT OUTER JOIN tblKostenstelle auk ON (sauk.KostenstelleID = auk.ID)"
				+ " LEFT OUTER JOIN stblAbrufKostenstelle sabk ON (ab.ID = sabk.AbrufID) LEFT OUTER JOIN tblKostenstelle abk ON (sabk.KostenstelleID = abk.ID)"
				+ " OUTER APPLY funBudgetAuftragDatum(au.ID, '" + datumBisAsString + "') auBudget "
				+ " OUTER APPLY funBudgetAbrufDatum(ab.ID, '" + datumBisAsString + "') abBudget ";
	
		
		// WHERE-Teile der SQL-Statementszusammenbauen
		whereDatum = coAuswertungAmpelliste.getWhereDatum();
		whereDatum = (whereDatum == null ? "" : " AND " + whereDatum);
		
		// Einschränkungen nur für den Auftrag
		where = coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getAuftragID(), "au.ID");
		where += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getKundeID(), "au.KundeID");
		where += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getAbteilungID(), "au.AbteilungID");
		where += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getAbteilungsleiterID(), "au.AbteilungsleiterID");
		where += coAuswertungAmpelliste.getBestellNr() == null ? "" : " AND au.BestellNr LIKE '%" + coAuswertungAmpelliste.getBestellNr() + "%'";

		// Einschränkungen für Aufträge und Abrufe
		whereProjekt = coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getAbteilungKundeID(), "projekt.AbteilungKundeID");
		whereProjekt += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getAnfordererKundeID(), "projekt.AnfordererKundeID");
		whereProjekt += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getProjektleiterID(), "projekt.ProjektleiterID");
		whereProjekt += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getAbrechnungsartID(), "projekt.AbrechnungsartID");
		whereProjekt += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getStatusID(), "projekt.StatusID");
		
		// Einschränkungen nur für den Abruf
		whereAbruf = coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getFachgebietID(), "ab.FachgebietID");
		whereAbruf += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getAbrufID(), "ab.ID");
		
		// wenn es Einschränkungen für den Abruf (Abruf-Felder) gibt, können keine Aufträge ausgegeben werden
		if (!whereAbruf.isEmpty())
		{
			auftraegeAusgeben = false;
		}
		
		// Einschränkungen AL für den Abruf
		whereAbruf += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getAbteilungsleiterID(), "ab.AbteilungsleiterAbrufID");

		whereAbruf = where + whereProjekt.replace("projekt.", "ab.") + whereAbruf;
		whereAuftrag = where + whereProjekt.replace("projekt.", "au.");

		// Einschränkungen nur für die Kostenstelle
		whereAuftrag += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getKostenstelleID(), "auk.ID");
		whereAbruf += coAuswertungAmpelliste.getWhereID(coAuswertungAmpelliste.getKostenstelleID(), "abk.ID");
//		dann noch einzelne projektauswertung auftrag mit kostenstelle

//		selectedprojektleiterIDs = formAmpelliste.getSelectedProjektleiterIDs();
		
		
		// GROUP BY muss jedes Feld enthalten, damit die Stunden summiert werden können
		// TODO GROUP BY auf SELECT FROM Monatseinsatzblatt beziehen, dann ist dieses unnötig
		groupBy = " GROUP BY "
				+ " au.ID, au.KundeID, au.AuftragsNr, au.BestellNr, au.Beschreibung, AbteilungID, au.AbteilungsleiterID, au.EdvNr, au.ProjektleiterID, "
				+ " au.AbteilungKundeID, au.AnfordererKundeID, au.AbrechnungsartID, au.StatusID, au.DatumTermin, au.DatumFertigmeldung, "
				+ " au.DatumBestellung, au.DatumMeldungVersendet, au.DatumBerechnetBis, "
				+ " au.Startwert, auBudget.Sollstunden, au.UVG, au.Puffer, au.Bestellwert, auBudget.IstStunden, auBudget.AbrechenbareStunden, auBudget.WertZeitVerbleibend, "
				+ " auBudget.VerbrauchBestellwert, auBudget.VerbrauchSollstunden, "
				+ " auk.ID, auk.Bezeichnung, "

				+ " ab.ID, ab.AbrufNr, ab.DatumAbruf, ab.Beschreibung, ab.FachgebietID, ab.PNummerID, ab.ZuordnungID, ab.PaketID, ab.EdvNr,"
				+ " ab.ProjektleiterID, ab.AbteilungsleiterAbrufID,"
				+ " ab.AbteilungKundeID, ab.AnfordererKundeID, ab.AbrechnungsartID, ab.StatusID, ab.DatumTermin, "
				+ " ab.DatumFertigmeldung, ab.DatumFreigabeRechnungAG, ab.DatumMeldungVersendet, ab.DatumBerechnetBis, "
				+ " ab.Startwert, abBudget.Sollstunden, ab.UVG, ab.Puffer, ab.Bestellwert, abBudget.IstStunden, abBudget.AbrechenbareStunden, abBudget.WertZeitVerbleibend,"
				+ " abBudget.VerbrauchBestellwert, abBudget.VerbrauchSollstunden, "
				+ " abk.ID, abk.Bezeichnung, "
				
				+ " ku.bezeichnung "
//	TODO STundenart			+ ", m.StundenartID "
				;
	
		
		// SQL-Statement
		sql = "";
			
		if (auftraegeAusgeben)
		{
			sql += select
			+ " FROM (SELECT * FROM tblMonatseinsatzblatt m WHERE AbrufID IS NULL " + whereDatum + ") AS m "
			+ " RIGHT OUTER JOIN tblAuftrag au ON (m.AuftragID = au.ID)"
			+ " LEFT OUTER JOIN tblAbruf ab ON (0 = ab.ID)"
			+ joinOhneProjekt
			+ (whereAuftrag.isEmpty() ? "" : " WHERE " + whereAuftrag.substring(4))
//			+ " AND (au.StatusID=" + CoStatusProjekt.STATUSID_LAUFEND + " OR au.StatusID=" + CoStatusProjekt.STATUSID_RUHEND + ")" 
//			+ (selectedprojektleiterIDs != null ? "au.ProjektleiterID IN (" + selectedprojektleiterIDs + ")" : "")
			+ groupBy;
			
			if (abrufeAusgeben)
			{
				sql += " UNION ";
			}
		}
		System.out.println(sql);

		if (abrufeAusgeben)
		{
			sql += select
					+ " FROM (SELECT * FROM tblMonatseinsatzblatt m WHERE AbrufID IS NOT NULL " + whereDatum + ") AS m "
					+ " RIGHT OUTER JOIN tblAbruf ab ON (m.AbrufID = ab.ID)"
					+ " JOIN tblAuftrag au ON (ab.AuftragID = au.ID)"
					+ joinOhneProjekt
					+ (whereAbruf.isEmpty() ? "" : " WHERE " + whereAbruf.substring(4))
//					+ " AND (ab.StatusID=" + CoStatusProjekt.STATUSID_LAUFEND + " OR ab.StatusID=" + CoStatusProjekt.STATUSID_RUHEND + ")" 
//					+ (selectedprojektleiterIDs != null ? "ab.ProjektleiterID IN (" + selectedprojektleiterIDs + ")" : "")
					+ groupBy;
//			System.out.println(select);
		}

		long a = System.currentTimeMillis();
		// Daten laden, wenn ein SQL-Statement generiert wurde
		emptyCache();
		
		if (!sql.isEmpty())
		{
			sql += " ORDER BY Kunde, AuftragsNr, AbrufNr";
			System.out.println(sql);
			Application.getLoaderBase().load(this, sql);
			
			// Daten aus Aufträgen und Abrufen zusammenführen
			doAfterLoadAmpelliste();
		}
		
		System.out.println("Zeit loading Ampelliste: " + (System.currentTimeMillis() - a)/1000);
	}
	

	/**
	 * Projektstundenauswertung gemäß den ausgewählten Kriterien laden.<br>
	 * Besteht aus Ampelliste und Stunden pro ausgewähltem Zeitraum
	 * 
	 * @param formAuswertungProjektstundenuebersicht 
	 * @throws Exception
	 */
	public void loadStundenauswertung(FormAuswertungProjektstundenuebersicht formAuswertungProjektstundenuebersicht) throws Exception {
		String where;
		CoAuswertungProjektstundenauswertung coAuswertungProjektstundenauswertung;
		CoMonatseinsatzblatt coMonatseinsatzblatt;

		coAuswertungProjektstundenauswertung = formAuswertungProjektstundenuebersicht.getCoAuswertungProjektstundenauswertung();
		
		
		// Ampelliste laden
		loadAmpelliste(coAuswertungProjektstundenauswertung);

		if (!moveFirst())
		{
			return;
		}

		

		// Zeiträume laden und entsprechende Spalten hinzufügen
		coMonatseinsatzblatt = new CoMonatseinsatzblatt();
		coMonatseinsatzblatt.loadAuswertungProjekt(coAuswertungProjektstundenauswertung, "");
		addFields(coMonatseinsatzblatt, coAuswertungProjektstundenauswertung);

		
		// Projekte durchlaufen und Stunden laden
		do
		{
			if (isAbruf())
			{
				where = " AbrufID=" + getAbrufID();
			}
			else
			{
				where = " AuftragID=" + getAuftragID();
			}

			// Daten laden
			coMonatseinsatzblatt.loadAuswertungProjekt(coAuswertungProjektstundenauswertung, where);

			// Zeiträume mit Stunden vorhanden?
			if (!coMonatseinsatzblatt.moveFirst())
			{
				continue;
			}

			// Zeiträume durchlaufen und Stunden den Zeiträumen zuordnen
			do
			{
				getField(getFieldResID(coMonatseinsatzblatt.getJahr(), coMonatseinsatzblatt.getZeitraumNr())).setValue(coMonatseinsatzblatt.getWertZeit());
			} while(coMonatseinsatzblatt.moveNext());

		} while (moveNext());
	}


	/**
	 * Fields gemäß Stundenwerten hinzufügen
	 * 
	 * @param coAuswertungProjekt
	 * @param coMonatseinsatzblatt
	 * @param coAuswertungProjektstundenauswertung 
	 * @throws Exception 
	 */
	private void addFields(CoMonatseinsatzblatt coMonatseinsatzblatt, CoAuswertungProjektstundenauswertung coAuswertungProjektstundenauswertung) throws Exception {
		int iZeitraum, ausgabezeitraumID, jahr, anzZeitraeumeJahr;
		String resID, columnName, columnLabel;
		Zeitraum firstZeitraum, lastZeitraum;

		
		ausgabezeitraumID = coAuswertungProjektstundenauswertung.getAusgabezeitraumID();
		CoAusgabezeitraum.getInstance().moveToID(ausgabezeitraumID);
		anzZeitraeumeJahr = CoAusgabezeitraum.getInstance().getAnzahlZeitraeume();
		
		firstZeitraum = coMonatseinsatzblatt.getFirstZeitraum();
		lastZeitraum = coMonatseinsatzblatt.getLastZeitraum();
		
		if (firstZeitraum == null)
		{
			return;
		}

		
		// für jeden Zeitraum eine Spalte
		for (iZeitraum=firstZeitraum.getZeitraumNr(), jahr=firstZeitraum.getJahr(); 
				jahr<lastZeitraum.getJahr() || (jahr==lastZeitraum.getJahr() && iZeitraum<=lastZeitraum.getZeitraumNr()); ++iZeitraum)
		{
			resID = getFieldResID(jahr, iZeitraum);

			// Caption bestimmen
			columnName = getFieldCaption(ausgabezeitraumID, jahr, iZeitraum);
			if (columnName == null)
			{
				return;
			}

			// Feld hinzufügen
			columnLabel = columnName;
			addField(resID, columnName, columnLabel, false);
			setZeitFormat(resID);
			
			// nächstes Jahr
			if (iZeitraum == anzZeitraeumeJahr-1)
			{
				++jahr;
				iZeitraum = -1; // wird in for-Schleife auf 1 gesetzt
			}
		}
	}
	

	/**
	 * Caption bestimmen 
	 * 
	 * @param iZeitraum
	 * @param ausgabezeitraumID
	 * @param jahr
	 * @return
	 */
	private String getFieldCaption(int ausgabezeitraumID, int jahr, int iZeitraum) {
		switch (ausgabezeitraumID) 
		{
		case CoAusgabezeitraum.ID_JAEHRLICH:
			return "" + jahr;

		case CoAusgabezeitraum.ID_HALBJAEHRLICH:
			return (iZeitraum+1) + ". HJ " + jahr;

		case CoAusgabezeitraum.ID_VIERTELJAEHRLICH:
			return (iZeitraum+1) + ". VJ " + jahr;

		case CoAusgabezeitraum.ID_MONATLICH:
			return Format.getMonatAbkuerzung(iZeitraum) + " " + jahr;

		default:
			return null;
		}
	}


	/**
	 * ResID des Field bestimmen
	 * 
	 * @param iZeitraum
	 * @param jahr
	 * @return
	 */
	private String getFieldResID(int jahr, int iZeitraum) {
		return "field.zeitraum." + jahr + "." + iZeitraum;
	}
	

	/**
	 * Aufbereitung des CO's nach dem Laden.<br>
	 * Der Projektleiter, die EDV-Nr etc. müssen aus dem von Auftrag und Abruf generiert werden.
	 * 
	 * @throws Exception 
	 */
	private void doAfterLoad() throws Exception {
		CoMonatseinsatzblattProjekt coMonatseinsatzblattProjekt;
		
		
		if (!moveFirst())
		{
			return;
		}
		
		coMonatseinsatzblattProjekt = new CoMonatseinsatzblattProjekt();
		coMonatseinsatzblattProjekt.load(m_personID);
		
		if (!isEditing())
		{
			begin();
		}

		do
		{
			// Daten auf Auftrag und Abruf zusammenführen
			checkBeschreibung();
			checkEdvNr();
			checkProjektleiter();
			checkAbteilungsleiter();
			checkAbteilungKunde();
			checkAnfordererKunde();
			checkAbrechnungsart();
			checkStatus();
			checkLiefertermin();
			checkDatumMeldungVersendet();
			checkDatumFertigmeldung();
			checkDatumBerechnetBis();
			checkStartwert();
			checkBestellwert();
			checkUvg();
			checkPuffer();
			checkSollstunden();
			checkIstStunden();
			checkAbrechenbareStunden();
			checkWertZeitVerbleibend();
			checkWertVerbrauchBestellwert();
			checkWertVerbrauchSollstunden();
			checkKostenstelle();
			
			// Bemerkung laden
			if (coMonatseinsatzblattProjekt.moveTo(this))
			{
				setBemerkung(coMonatseinsatzblattProjekt.getBemerkung());
			}
		} while (moveNext());
	}


	/**
	 * Aufbereitung des CO's nach dem Laden.<br>
	 * Der Projektleiter, die EDV-Nr etc. müssen aus dem von Auftrag und Abruf generiert werden.<br>
	 * Abrufe mit mehreren Kostenstellen zusammenführen. Dazu werden die Kostenstellen als Komma-getrennte Liste unter dem ersten Eintrag zusammengefasst.
	 * Die weiteren Einträge werden gelöscht.
	 * 
	 * @throws Exception 
	 */
	private void doAfterLoadAmpelliste() throws Exception {
		int auftragID, lastAuftragID;
		int abrufID, lastAbrufID;
		String kostenstelle;
		CoMonatseinsatzblattProjekt coMonatseinsatzblattProjekt;
		
		
		
		// Projektleiter, die EDV-Nr etc. müssen aus dem von Auftrag und Abruf generiert werden
		doAfterLoad();
		
		// Aufträge und Abrufe mit doppelten Kostenstellen zusammenführen
		lastAuftragID = 0;
		lastAbrufID = 0;
		coMonatseinsatzblattProjekt = new CoMonatseinsatzblattProjekt();
		coMonatseinsatzblattProjekt.load(m_personID);
		
		if (!moveFirst())
		{
			return;
		}
		
		if (!isEditing())
		{
			begin();
		}
		
		do
		{
			// Aufträge und Abrufe können doppelt vorkommen, wenn es mehrere Kostenstellen gibt
			auftragID = getAuftragID();
			abrufID = getAbrufID();
			
			// Aufträge ohne Abrufe
			if (abrufID == 0 && lastAbrufID == 0 && auftragID == lastAuftragID)
			{
				// Eintrag löschen und Kostenstelle beim vorherigen Eintrag anhängen
				kostenstelle = getKostenstelle();
				delete();

				movePrev();
				getFieldKostenstelle().setValue(getKostenstelle() + ", " + kostenstelle);
				continue;
			}
			
			// Abrufe
			if (abrufID > 0 && abrufID == lastAbrufID)
			{
				// Eintrag löschen und Kostenstelle beim vorherigen Eintrag anhängen
				kostenstelle = getKostenstelle();
				delete();

				movePrev();
				getFieldKostenstelle().setValue(getKostenstelle() + ", " + kostenstelle);
				continue;
			}
			
			lastAuftragID = auftragID;
			lastAbrufID = abrufID;
			
		} while (moveNext());
	}


	private void checkBeschreibung() {
		String beschreibung;

		if (isAbruf())
		{
			beschreibung = getAbrufBeschreibung();
		}
		else
		{
			beschreibung = getAuftragsBeschreibung();
		}
		
		getFieldBeschreibung().setValue(beschreibung);
	}


	private void checkEdvNr() {
		String edvNr;

		if (isAbruf())
		{
			edvNr = getAbrufEdvNr();
		}
		else
		{
			edvNr = getAuftragEdvNr();
		}
		
		getFieldEdvNr().setValue(edvNr);
	}


	private void checkAbteilungsleiter() {
		int abteilungsleiterID;

		if (isAbruf())
		{
			abteilungsleiterID = getAbrufAbteilungsleiterID();
		}
		else
		{
			abteilungsleiterID = getAuftragAbteilungsleiterID();
		}
		
		if (abteilungsleiterID > 0)
		{
			getFieldAbteilungsleiterID().setValue(abteilungsleiterID);
		}
	}


	private void checkProjektleiter() {
		int projektleiterID;

		if (isAbruf())
		{
			projektleiterID = getAbrufProjektleiterID();
		}
		else
		{
			projektleiterID = getAuftragProjektleiterID();
		}
		
		if (projektleiterID > 0)
		{
			getFieldProjektleiterID().setValue(projektleiterID);
		}
	}


	private void checkAbteilungKunde() {
		int abteilungKundeID;

		if (isAbruf())
		{
			abteilungKundeID = getAbrufAbteilungKundeID();
		}
		else
		{
			abteilungKundeID = getAuftragAbteilungKundeID();
		}
		
		if (abteilungKundeID > 0)
		{
			getFieldAbteilungKundeID().setValue(abteilungKundeID);
		}
	}


	private void checkAnfordererKunde() {
		int anfordererKundeID;

		if (isAbruf())
		{
			anfordererKundeID = getAbrufAnfordererKundeID();
		}
		else
		{
			anfordererKundeID = getAuftragAnfordererKundeID();
		}
		
		if (anfordererKundeID > 0)
		{
			getFieldAnfordererKundeID().setValue(anfordererKundeID);
		}
	}


	private void checkAbrechnungsart() {
		int abrechnungsartID;

		if (isAbruf())
		{
			abrechnungsartID = getAbrufAbrechnungsartID();
		}
		else
		{
			abrechnungsartID = getAuftragAbrechnungsartID();
		}
		
		if (abrechnungsartID > 0)
		{
			getFieldAbrechnungsartID().setValue(abrechnungsartID);
		}
	}


	private void checkStatus() {
		int statusID;

		if (isAbruf())
		{
			statusID = getAbrufStatusID();
		}
		else
		{
			statusID = getAuftragStatusID();
		}
	
		if (statusID > 0)
		{
			getFieldStatusID().setValue(statusID);
		}
	}


	private void checkLiefertermin() {
		Date liefertermin;

		if (isAbruf())
		{
			liefertermin = getAbrufLiefertermin();
		}
		else
		{
			liefertermin = getAuftragLiefertermin();
		}
	
		getFieldLiefertermin().setValue(liefertermin);
	}


	private void checkDatumBerechnetBis() {
		Date datumBerechnetBis;

		if (isAbruf())
		{
			datumBerechnetBis = getAbrufDatumBerechnetBis();
		}
		else
		{
			datumBerechnetBis = getAuftragDatumBerechnetBis();
		}
	
		getFieldDatumBerechnetBis().setValue(datumBerechnetBis);
	}


	private void checkDatumFertigmeldung() {
		Date datumFertigMeldung;

		if (isAbruf())
		{
			datumFertigMeldung = getAbrufDatumFertigmeldung();
		}
		else
		{
			datumFertigMeldung = getAuftragDatumFertigmeldung();
		}
	
		getFieldDatumFertigmeldung().setValue(datumFertigMeldung);
	}


	private void checkDatumMeldungVersendet() {
		Date datumMeldungVersendet;

		if (isAbruf())
		{
			datumMeldungVersendet = getAbrufDatumMeldungVersendet();
		}
		else
		{
			datumMeldungVersendet = getAuftragDatumMeldungVersendet();
		}
	
		getFieldDatumMeldungVersendet().setValue(datumMeldungVersendet);
	}


	private void checkStartwert() {
		int startwert;

		if (isAbruf())
		{
			startwert = getAbrufStartwert();
		}
		else
		{
			startwert = getAuftragStartwert();
		}
	
		getFieldStartwert().setValue(startwert);
	}


	private void checkBestellwert() {
		int bestellwert;

		if (isAbruf())
		{
			bestellwert = getAbrufBestellwert();
		}
		else
		{
			bestellwert = getAuftragBestellwert();
		}
	
		getFieldBestellwert().setValue(bestellwert);
	}


	private void checkUvg() {
		int uvg;

		if (isAbruf())
		{
			uvg = getAbrufUvg();
		}
		else
		{
			uvg = getAuftragUvg();
		}
	
		getFieldUvg().setValue(uvg);
	}


	private void checkPuffer() {
		int puffer;

		if (isAbruf())
		{
			puffer = getAbrufPuffer();
		}
		else
		{
			puffer = getAuftragPuffer();
		}
	
		getFieldPuffer().setValue(puffer);
	}


	private void checkSollstunden() {
		int sollstunden;

		if (isAbruf())
		{
			sollstunden = getAbrufSollstunden();
		}
		else
		{
			sollstunden = getAuftragSollstunden();
		}
	
		getFieldSollstunden().setValue(sollstunden);
	}


	private void checkIstStunden() {
		int istStunden;

		if (isAbruf())
		{
			istStunden = getAbrufIstStunden();
		}
		else
		{
			istStunden = getAuftragIstStunden();
		}
	
		getFieldIstStunden().setValue(istStunden);
	}


	private void checkAbrechenbareStunden() {
		int abrechenbareStunden;

		if (isAbruf())
		{
			abrechenbareStunden = getAbrufAbrechenbareStunden();
		}
		else
		{
			abrechenbareStunden = getAuftragAbrechenbareStunden();
		}
	
		getFieldAbrechenbareStunden().setValue(abrechenbareStunden);
	}


	private void checkWertZeitVerbleibend() {
		int wertZeitVerbleibend;

		if (isAbruf())
		{
			wertZeitVerbleibend = getAbrufWertZeitVerbleibend();
		}
		else
		{
			wertZeitVerbleibend = getAuftragWertZeitVerbleibend();
		}
		
		getFieldWertZeitVerbleibend().setValue(wertZeitVerbleibend);
	}


	private void checkWertVerbrauchBestellwert() {
		double verbrauchBestellwert;

		if (isAbruf())
		{
			verbrauchBestellwert = getAbrufVerbrauchBestellwert();
		}
		else
		{
			verbrauchBestellwert = getAuftragVerbrauchBestellwert();
		}
		
		getFieldVerbrauchBestellwert().setValue(verbrauchBestellwert);
	}


	private void checkWertVerbrauchSollstunden() {
		double verbrauchSollstunden;

		if (isAbruf())
		{
			verbrauchSollstunden = getAbrufVerbrauchSollstunden();
		}
		else
		{
			verbrauchSollstunden = getAuftragVerbrauchSollstunden();
		}
		
		getFieldVerbrauchSollstunden().setValue(verbrauchSollstunden);
	}


	private void checkKostenstelle() {
		int kostenstelleID;
		String kostenstelle;

		if (isAbruf())
		{
			kostenstelleID = getAbrufKostenstelleID();
			kostenstelle = getAbrufKostenstelle();
		}
		else
		{
			kostenstelleID = getAuftragKostenstelleID();
			kostenstelle = getAuftragKostenstelle();
		}
		
		if (kostenstelleID > 0)
		{
			getFieldKostenstelleID().setValue(kostenstelleID);
			getFieldKostenstelle().setValue(kostenstelle);
		}
	}


	/**
	 * Kombination der Nummern
	 * 
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	protected String getSortFieldName() {
		return "Kunde, AuftragsNr, AbrufNr, Kostenstelle, StundenartID";
	}
	

	public IField getFieldAuftragID() {
		return getField(FIELD_RESID + "auftragid");
	}


	public int getAuftragID() {
		return Format.getIntValue(getFieldAuftragID().getValue());
	}


	public IField getFieldAuftragsNr() {
		return getField(FIELD_RESID + "auftragsnr");
	}


	public String getAuftragsNr() {
		return Format.getStringValue(getFieldAuftragsNr().getValue());
	}


	public IField getFieldAuftragsBeschreibung() {
		return getField(FIELD_RESID + "auftragsbeschreibung");
	}


	public String getAuftragsBeschreibung() {
		return Format.getStringValue(getFieldAuftragsBeschreibung().getValue());
	}


	public IField getFieldKundeID() {
		return getField(FIELD_RESID + "kundeid");
	}


	public String getKunde() {
		return getFieldKundeID().getDisplayValue();
	}


	public IField getFieldKundeKuerzelID() {
		return getField(FIELD_RESID + "kundekuerzelid");
	}


	public String getKundeKuerzel() {
		return Format.getStringValue(getFieldKundeKuerzelID().getDisplayValue());
	}


	public IField getFieldAuftragEdvNr() {
		return getField(FIELD_RESID + "auftragedvnr");
	}


	public String getAuftragEdvNr() {
		return Format.getStringValue(getFieldAuftragEdvNr().getValue());
	}


	public IField getFieldAuftragAbteilungsleiterID() {
		return getField(FIELD_RESID + "auftragabteilungsleiterid");
	}


	public int getAuftragAbteilungsleiterID() {
		return Format.getIntValue(getFieldAuftragAbteilungsleiterID().getValue());
	}


	public IField getFieldAuftragProjektleiterID() {
		return getField(FIELD_RESID + "auftragprojektleiterid");
	}


	public int getAuftragProjektleiterID() {
		return Format.getIntValue(getFieldAuftragProjektleiterID().getValue());
	}


	public IField getFieldAuftragAbteilungKundeID() {
		return getField(FIELD_RESID + "auftragabteilungkundeid");
	}


	public int getAuftragAbteilungKundeID() {
		return Format.getIntValue(getFieldAuftragAbteilungKundeID().getValue());
	}


	public IField getFieldAuftragAnfordererKundeID() {
		return getField(FIELD_RESID + "auftraganfordererkundeid");
	}


	public int getAuftragAnfordererKundeID() {
		return Format.getIntValue(getFieldAuftragAnfordererKundeID().getValue());
	}


	public IField getFieldAuftragAbrechnungsartID() {
		return getField(FIELD_RESID + "auftragabrechnungsartid");
	}


	public int getAuftragAbrechnungsartID() {
		return Format.getIntValue(getFieldAuftragAbrechnungsartID().getValue());
	}


	public IField getFieldAuftragStatusID() {
		return getField(FIELD_RESID + "auftragstatusid");
	}


	public int getAuftragStatusID() {
		return Format.getIntValue(getFieldAuftragStatusID().getValue());
	}


	public IField getFieldAuftragLiefertermin() {
		return getField(FIELD_RESID + "auftragliefertermin");
	}


	public Date getAuftragLiefertermin() {
		return Format.getDateValue(getFieldAuftragLiefertermin().getValue());
	}


	public IField getFieldAuftragDatumMeldungVersendet() {
		return getField(FIELD_RESID + "auftragdatummeldungversendet");
	}


	public Date getAuftragDatumMeldungVersendet() {
		return Format.getDateValue(getFieldAuftragDatumMeldungVersendet().getValue());
	}


	public IField getFieldAuftragDatumFertigmeldung() {
		return getField(FIELD_RESID + "auftragdatumfertigmeldung");
	}


	public Date getAuftragDatumFertigmeldung() {
		return Format.getDateValue(getFieldAuftragDatumFertigmeldung().getValue());
	}


	public IField getFieldAuftragDatumBerechnetBis() {
		return getField(FIELD_RESID + "auftragdatumberechnetbis");
	}


	public Date getAuftragDatumBerechnetBis() {
		return Format.getDateValue(getFieldAuftragDatumBerechnetBis().getValue());
	}


	public IField getFieldAuftragStartwert() {
		return getField(FIELD_RESID + "auftragstartwert");
	}


	public int getAuftragStartwert() {
		return Format.getIntValue(getFieldAuftragStartwert().getValue());
	}


	public IField getFieldAuftragBestellwert() {
		return getField(FIELD_RESID + "auftragbestellwert");
	}


	public int getAuftragBestellwert() {
		return Format.getIntValue(getFieldAuftragBestellwert().getValue());
	}


	public IField getFieldAuftragUvg() {
		return getField(FIELD_RESID + "auftraguvg");
	}


	public int getAuftragUvg() {
		return Format.getIntValue(getFieldAuftragUvg().getValue());
	}


	public IField getFieldAuftragPuffer() {
		return getField(FIELD_RESID + "auftragpuffer");
	}


	public int getAuftragPuffer() {
		return Format.getIntValue(getFieldAuftragPuffer().getValue());
	}


	public IField getFieldAuftragSollstunden() {
		return getField(FIELD_RESID + "auftragsollstunden");
	}


	public int getAuftragSollstunden() {
		return Format.getIntValue(getFieldAuftragSollstunden().getValue());
	}


	public IField getFieldAuftragIstStunden() {
		return getField(FIELD_RESID + "auftragiststunden");
	}


	public int getAuftragIstStunden() {
		return Format.getIntValue(getFieldAuftragIstStunden().getValue());
	}


	public IField getFieldAuftragAbrechenbareStunden() {
		return getField(FIELD_RESID + "auftragabrechenbarestunden");
	}


	public int getAuftragAbrechenbareStunden() {
		return Format.getIntValue(getFieldAuftragAbrechenbareStunden().getValue());
	}


	public IField getFieldAuftragWertZeitVerbleibend() {
		return getField(FIELD_RESID + "auftragwertzeitverbleibend");
	}


	public int getAuftragWertZeitVerbleibend() {
		return Format.getIntValue(getFieldAuftragWertZeitVerbleibend().getValue());
	}


	public IField getFieldAuftragVerbrauchBestellwert() {
		return getField(FIELD_RESID + "auftragverbrauchbestellwert");
	}


	public double getAuftragVerbrauchBestellwert() {
		return Format.getDoubleValue(getFieldAuftragVerbrauchBestellwert().getValue());
	}


	public IField getFieldAuftragVerbrauchSollstunden() {
		return getField(FIELD_RESID + "auftragverbrauchsollstunden");
	}


	public double getAuftragVerbrauchSollstunden() {
		return Format.getDoubleValue(getFieldAuftragVerbrauchSollstunden().getValue());
	}


	public IField getFieldAuftragKostenstelleID() {
		return getField(FIELD_RESID + "auftragkostenstelleid");
	}


	public int getAuftragKostenstelleID() {
		return Format.getIntValue(getFieldAuftragKostenstelleID().getValue());
	}

	
	public IField getFieldAuftragKostenstelle() {
		return getField(FIELD_RESID + "auftragkostenstelle");
	}


	public String getAuftragKostenstelle() {
		return Format.getStringValue(getFieldAuftragKostenstelleID().getDisplayValue());
	}


	public IField getFieldAbrufID() {
		return getField(FIELD_RESID + "abrufid");
	}


	public int getAbrufID() {
		return Format.getIntValue(getFieldAbrufID().getValue());
	}


	public IField getFieldAbrufNr() {
		return getField(FIELD_RESID + "abrufnr");
	}


	public String getAbrufNr() {
		return Format.getStringValue(getFieldAbrufNr().getValue());
	}


	public IField getFieldAbrufBeschreibung() {
		return getField(FIELD_RESID + "abrufbeschreibung");
	}


	public String getAbrufBeschreibung() {
		return Format.getStringValue(getFieldAbrufBeschreibung().getValue());
	}


	public IField getFieldAbrufEdvNr() {
		return getField(FIELD_RESID + "abrufedvnr");
	}


	public String getAbrufEdvNr() {
		return Format.getStringValue(getFieldAbrufEdvNr().getValue());
	}


	public IField getFieldAbrufAbteilungsleiterID() {
		return getField(FIELD_RESID + "abrufabteilungsleiterid");
	}


	public int getAbrufAbteilungsleiterID() {
		return Format.getIntValue(getFieldAbrufAbteilungsleiterID().getValue());
	}


	public IField getFieldAbrufProjektleiterID() {
		return getField(FIELD_RESID + "abrufprojektleiterid");
	}


	public int getAbrufProjektleiterID() {
		return Format.getIntValue(getFieldAbrufProjektleiterID().getValue());
	}


	public IField getFieldAbrufAbteilungKundeID() {
		return getField(FIELD_RESID + "abrufabteilungkundeid");
	}


	public int getAbrufAbteilungKundeID() {
		return Format.getIntValue(getFieldAbrufAbteilungKundeID().getValue());
	}


	public IField getFieldAbrufAnfordererKundeID() {
		return getField(FIELD_RESID + "abrufanfordererkundeid");
	}


	public int getAbrufAnfordererKundeID() {
		return Format.getIntValue(getFieldAbrufAnfordererKundeID().getValue());
	}


	public IField getFieldAbrufAbrechnungsartID() {
		return getField(FIELD_RESID + "abrufabrechnungsartid");
	}


	public int getAbrufAbrechnungsartID() {
		return Format.getIntValue(getFieldAbrufAbrechnungsartID().getValue());
	}


	public IField getFieldAbrufStatusID() {
		return getField(FIELD_RESID + "abrufstatusid");
	}


	public int getAbrufStatusID() {
		return Format.getIntValue(getFieldAbrufStatusID().getValue());
	}


	public IField getFieldAbrufLiefertermin() {
		return getField(FIELD_RESID + "abrufliefertermin");
	}


	public Date getAbrufLiefertermin() {
		return Format.getDateValue(getFieldAbrufLiefertermin().getValue());
	}


	public IField getFieldAbrufDatumMeldungVersendet() {
		return getField(FIELD_RESID + "abrufdatummeldungversendet");
	}


	public Date getAbrufDatumMeldungVersendet() {
		return Format.getDateValue(getFieldAbrufDatumMeldungVersendet().getValue());
	}


	public IField getFieldAbrufDatumFertigmeldung() {
		return getField(FIELD_RESID + "abrufdatumfertigmeldung");
	}


	public Date getAbrufDatumFertigmeldung() {
		return Format.getDateValue(getFieldAbrufDatumFertigmeldung().getValue());
	}


	public IField getFieldAbrufDatumBerechnetBis() {
		return getField(FIELD_RESID + "abrufdatumberechnetbis");
	}


	public Date getAbrufDatumBerechnetBis() {
		return Format.getDateValue(getFieldAbrufDatumBerechnetBis().getValue());
	}


	public IField getFieldAbrufStartwert() {
		return getField(FIELD_RESID + "abrufstartwert");
	}


	public int getAbrufStartwert() {
		return Format.getIntValue(getFieldAbrufStartwert().getValue());
	}


	public IField getFieldAbrufBestellwert() {
		return getField(FIELD_RESID + "abrufbestellwert");
	}


	public int getAbrufBestellwert() {
		return Format.getIntValue(getFieldAbrufBestellwert().getValue());
	}


	public IField getFieldAbrufUvg() {
		return getField(FIELD_RESID + "abrufuvg");
	}


	public int getAbrufUvg() {
		return Format.getIntValue(getFieldAbrufUvg().getValue());
	}


	public IField getFieldAbrufPuffer() {
		return getField(FIELD_RESID + "abrufpuffer");
	}


	public int getAbrufPuffer() {
		return Format.getIntValue(getFieldAbrufPuffer().getValue());
	}


	public IField getFieldAbrufSollstunden() {
		return getField(FIELD_RESID + "abrufsollstunden");
	}


	public int getAbrufSollstunden() {
		return Format.getIntValue(getFieldAbrufSollstunden().getValue());
	}


	public IField getFieldAbrufIstStunden() {
		return getField(FIELD_RESID + "abrufiststunden");
	}


	public int getAbrufIstStunden() {
		return Format.getIntValue(getFieldAbrufIstStunden().getValue());
	}


	public IField getFieldAbrufAbrechenbareStunden() {
		return getField(FIELD_RESID + "abrufabrechenbarestunden");
	}


	public int getAbrufAbrechenbareStunden() {
		return Format.getIntValue(getFieldAbrufAbrechenbareStunden().getValue());
	}


	public IField getFieldAbrufWertZeitVerbleibend() {
		return getField(FIELD_RESID + "abrufwertzeitverbleibend");
	}


	public int getAbrufWertZeitVerbleibend() {
		return Format.getIntValue(getFieldAbrufWertZeitVerbleibend().getValue());
	}


	public IField getFieldAbrufVerbrauchBestellwert() {
		return getField(FIELD_RESID + "abrufverbrauchbestellwert");
	}


	public double getAbrufVerbrauchBestellwert() {
		return Format.getDoubleValue(getFieldAbrufVerbrauchBestellwert().getValue());
	}


	public IField getFieldAbrufVerbrauchSollstunden() {
		return getField(FIELD_RESID + "abrufverbrauchsollstunden");
	}


	public double getAbrufVerbrauchSollstunden() {
		return Format.getDoubleValue(getFieldAbrufVerbrauchSollstunden().getValue());
	}


	public IField getFieldAbrufKostenstelleID() {
		return getField(FIELD_RESID + "abrufkostenstelleid");
	}


	public int getAbrufKostenstelleID() {
		return Format.getIntValue(getFieldAbrufKostenstelleID().getValue());
	}

	
	public IField getFieldAbrufKostenstelle() {
		return getField(FIELD_RESID + "abrufkostenstelle");
	}


	public String getAbrufKostenstelle() {
		return Format.getStringValue(getFieldAbrufKostenstelleID().getDisplayValue());
	}


	public IField getFieldKostenstelleID() {
		return getField(FIELD_RESID + "kostenstelleid");
	}


	public int getKostenstelleID() {
		return Format.getIntValue(getFieldKostenstelleID().getValue());
	}

	
	public IField getFieldKostenstelle() {
		return getField(FIELD_RESID + "kostenstelle");
	}


	public String getKostenstelle() {
		return Format.getStringValue(getFieldKostenstelleID().getDisplayValue());
	}


	public IField getFieldBerichtsNrID() {
		return getField(FIELD_RESID + "berichtsnrid");
	}


	public int getBerichtsNrID() {
		return Format.getIntValue(getFieldBerichtsNrID());
	}

	
	public IField getFieldBerichtsNr() {
		return getField(FIELD_RESID + "berichtsnr");
	}


	public String getBerichtsNr() {
		return Format.getStringValue(getFieldBerichtsNrID().getDisplayValue());
	}


	public IField getFieldBeschreibung() {
		return getField(FIELD_RESID + "beschreibung");
	}


	public String getBeschreibung() {
		return Format.getStringValue(getFieldBeschreibung().getValue());
	}


	public String getProjektNr() {
		if (isAbruf())
		{
			return getAbrufNr();
		}
		else
		{
			return getAuftragsNr();
		}
	}


	public IField getFieldEdvNr() {
		return getField(FIELD_RESID + "edvnr");
	}


	public String getEdvNr() {
		return Format.getStringValue(getFieldEdvNr().getValue());
	}


	public IField getFieldBestellNr() {
		return getField(FIELD_RESID + "bestellnr");
	}


	public IField getFieldAbteilungID() {
		return getField(FIELD_RESID + "abteilungid");
	}


	public IField getFieldAbteilungsleiterID() {
		return getField(FIELD_RESID + "abteilungsleiterid");
	}


	public IField getFieldFachgebietID() {
		return getField(FIELD_RESID + "fachgebietid");
	}


	public IField getFieldProjektleiterID() {
		return getField(FIELD_RESID + "projektleiterid");
	}


	public int getProjektleiterID() {
		return Format.getIntValue(getFieldProjektleiterID().getValue());
	}


	public String getProjektleiter() {
		return getFieldProjektleiterID().getDisplayValue();
	}


	public IField getFieldAbteilungKundeID() {
		return getField(FIELD_RESID + "abteilungkundeid");
	}


	public int getAbteilungKundeID() {
		return Format.getIntValue(getFieldAbteilungKundeID().getValue());
	}


	public IField getFieldAnfordererKundeID() {
		return getField(FIELD_RESID + "anfordererkundeid");
	}


	public int getAnfordererKundeID() {
		return Format.getIntValue(getFieldAnfordererKundeID().getValue());
	}

	
	public IField getFieldAbrechnungsartID() {
		return getField(FIELD_RESID + "abrechnungsartid");
	}


	public int getAbrechnungsartID() {
		return Format.getIntValue(getFieldAbrechnungsartID().getValue());
	}

	
	public IField getFieldPNummerID() {
		return getField(FIELD_RESID + "pnummerid");
	}

	
	public IField getFieldZuordnungID() {
		return getField(FIELD_RESID + "zuordnungid");
	}

	
	public IField getFieldPaketID() {
		return getField(FIELD_RESID + "paketid");
	}


	public IField getFieldStatusID() {
		return getField(FIELD_RESID + "statusid");
	}


	public int getStatusID() {
		return Format.getIntValue(getFieldStatusID().getValue());
	}


	public IField getFieldLiefertermin() {
		return getField(FIELD_RESID + "liefertermin");
	}


	public Date getLiefertermin() {
		return Format.getDateValue(getFieldLiefertermin().getValue());
	}


	public IField getFieldDatumMeldungVersendet() {
		return getField(FIELD_RESID + "datummeldungversendet");
	}


	public Date getDatumMeldungVersendet() {
		return Format.getDateValue(getFieldDatumMeldungVersendet().getValue());
	}


	public IField getFieldDatumAbruf() {
		return getField(FIELD_RESID + "datumabruf");
	}


	public Date getDatumAbruf() {
		return Format.getDateValue(getFieldDatumAbruf().getValue());
	}


	public IField getFieldDatumBestellung() {
		return getField(FIELD_RESID + "datumbestellung");
	}


	public Date getDatumBestellung() {
		return Format.getDateValue(getFieldDatumBestellung().getValue());
	}


	public IField getFieldDatumFertigmeldung() {
		return getField(FIELD_RESID + "datumfertigmeldung");
	}


	public Date getDatumFertigmeldung() {
		return Format.getDateValue(getFieldDatumFertigmeldung().getValue());
	}


	public IField getFieldDatumFreigabeRechnungAG() {
		return getField(FIELD_RESID + "datumfreigaberechnungag");
	}


	public Date getDatumFreigabeRechnungAG() {
		return Format.getDateValue(getFieldDatumFreigabeRechnungAG().getValue());
	}


	public IField getFieldDatumBerechnetBis() {
		return getField(FIELD_RESID + "datumberechnetbis");
	}


	public Date getDatumDatumBerechnetBis() {
		return Format.getDateValue(getFieldDatumBerechnetBis().getValue());
	}


	public IField getFieldStartwert() {
		return getField(FIELD_RESID + "startwert");
	}


	public int getStartwert() {
		return Format.getIntValue(getFieldStartwert().getValue());
	}


	public IField getFieldBestellwert() {
		return getField(FIELD_RESID + "bestellwert");
	}


	public int getBestellwert() {
		return Format.getIntValue(getFieldBestellwert().getValue());
	}


	public IField getFieldUvg() {
		return getField(FIELD_RESID + "uvg");
	}


	public int getUvg() {
		return Format.getIntValue(getFieldUvg().getValue());
	}


	public IField getFieldPuffer() {
		return getField(FIELD_RESID + "puffer");
	}


	public int getPuffer() {
		return Format.getIntValue(getFieldPuffer().getValue());
	}


	public IField getFieldSollstunden() {
		return getField(FIELD_RESID + "sollstunden");
	}


	public int getSollstunden() {
		return Format.getIntValue(getFieldSollstunden().getValue());
	}


	public IField getFieldWertZeit() {
		return getField(FIELD_RESID + "wertzeit");
	}


	public int getWertZeit() {
		return Format.getIntValue(getFieldWertZeit().getValue());
	}


	public IField getFieldIstStunden() {
		return getField(FIELD_RESID + "iststunden");
	}


	public int getIstStunden() {
		return Format.getIntValue(getFieldIstStunden().getValue());
	}


	public IField getFieldAbrechenbareStunden() {
		return getField(FIELD_RESID + "abrechenbarestunden");
	}


	public int getAbrechenbareStunden() {
		return Format.getIntValue(getFieldAbrechenbareStunden().getValue());
	}


	public IField getFieldWertZeitVerbleibend() {
		return getField(FIELD_RESID + "wertzeitverbleibend");
	}


	public int getWertZeitVerbleibend() {
		return Format.getIntValue(getFieldWertZeitVerbleibend().getValue());
	}


	public IField getFieldWertZeitVerbleibend2() {
		return getField(FIELD_RESID + "wertzeitverbleibend2");
	}


	public int getWertZeitVerbleibend2() {
		return Format.getIntValue(getFieldWertZeitVerbleibend2().getValue());
	}


	public IField getFieldVerbrauchBestellwert() {
		return getField(FIELD_RESID + "verbrauchbestellwert");
	}


	public double getVerbrauchBestellwert() {
		return Format.getDoubleValue(getFieldVerbrauchBestellwert().getValue());
	}


	public IField getFieldVerbrauchSollstunden() {
		return getField(FIELD_RESID + "verbrauchsollstunden");
	}


	public double getVerbrauchSollstunden() {
		return Format.getDoubleValue(getFieldVerbrauchSollstunden().getValue());
	}


	public IField getFieldStundenartID() {
		return getField(FIELD_RESID + "stundenartid");
	}


	public int getStundenartID() {
		return Format.getIntValue(getFieldStundenartID().getValue());
	}


	public String getStundenart() {
		return getFieldStundenartID().getDisplayValue();
	}


	public IField getFieldBemerkung() {
		return getField(FIELD_RESID + "bemerkung");
	}


	public String getBemerkung() {
		return Format.getStringValue(getFieldBemerkung().getValue());
	}


	public void setBemerkung(String bemerkung) {
		getFieldBemerkung().setValue(bemerkung);
	}


	/**
	 * Caption des Bemerkungsfeldes
	 * 
	 * @return
	 */
	public String getCaptionBemerkung() {
		if (m_captionBemerkung == null)
		{
			m_captionBemerkung = getFieldBemerkung().getFieldDescription().getCaption();
		}
		
		return m_captionBemerkung;
	}

	
	public boolean isAbruf(){
		return getAbrufID() > 0;
	}
	

	/**
	 * Kunde, Auftrags-Nr und Stundenart
	 * 
	 */
	public String getProjektinfos(){
		String abrufNr, kostenstelle;
		
		abrufNr = getAbrufNr();
		kostenstelle = getKostenstelle();
		
		return getFieldKundeID().getDisplayValue() + " - " + getAuftragsNr()
				+ (abrufNr == null ? "" : " - " + abrufNr)
				+ (kostenstelle == null ? "" : " - " + kostenstelle) 
				+ " (" + getFieldStundenartID().getDisplayValue() + ")";
	}
	

	/**
	 * Zeile im CO suchen, die den übergebenen Projektdaten entsprechen
	 * 
	 * @param auftragsID
	 * @param abrufID
	 * @param kostenstelleID
	 * @param berichtsNrID
	 * @param stundenartID 
	 * @return Index des Projektes im CO
	 * @throws Exception 
	 */
	public int getRowIndex(int auftragsID, int abrufID, int kostenstelleID, int berichtsNrID, int stundenartID) throws Exception{
		
		if (!moveTo(auftragsID, abrufID, kostenstelleID, berichtsNrID, stundenartID))
		{
			throw new Exception();
		}
		
		return getCurrentRowIndex();
	}
	

	/**
	 * Zeile im CO suchen, die den übergebenen Projektdaten entsprechen
	 * 
	 * @param auftragsID
	 * @param abrufID
	 * @param kostenstelleID
	 * @param berichtsNrID
	 * @param stundenartID 
	 * @return Datensatz gefunden
	 * @throws Exception 
	 */
	private boolean moveTo(int auftragsID, int abrufID, int kostenstelleID, int berichtsNrID, int stundenartID) throws Exception{
		
		if (!moveFirst())
		{
			return false;
		}
		
		do
		{
			if (getAuftragID() != auftragsID)
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
			
			if (getStundenartID() != stundenartID)
			{
				continue;
			}
			
			return true;
		} while (moveNext());
		
		return false;
	}
	
}
