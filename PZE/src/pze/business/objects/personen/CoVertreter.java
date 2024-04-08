package pze.business.objects.personen;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoMessage;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;


/**
 * CacheObject für Vertreter von Urlaub etc.
 * 
 * @author Lisiecki
 *
 */
public class CoVertreter extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblvertreter";
	
	private static final int ANZAHL_EINGABEZEILEN = 10;

	
	/**
	 * Kontruktor
	 */
	public CoVertreter() {
		super("table." + TABLE_NAME);
	}
	


	@Override
	public String getNavigationBitmap() {
		return "book.edit";
	}


	/**
	 * Sortiert nach Buchungsnr, die neueste zuerst
	 * 
	 * (non-Javadoc)
	 * @see pze.business.objects.AbstractCacheObject#getSortFieldName()
	 */
	@Override
	protected String getSortFieldName() {
		return "PersonID, VertreterID, Datum";
	}
	
	

	/**
	 * Virtuelles Field hinzufügen
	 * 
	 * @return
	 * @throws Exception
	 */
	private void addFieldDatumBis() throws Exception{
		addField(getResIdDatumBis());
	}


	/**
	 * CO für die Person und den Zeitraum laden
	 * 
	 * @param personID
	 * @param datum
	 * @throws Exception
	 */
	private void load(int personID, Date datumVon, Date datumBis) throws Exception {
		String where;
		
		where = "PersonID=" + personID 
				+ " AND DATUM >= '" + Format.getStringForDB(datumVon) + "' "
				+ " AND DATUM < '" + Format.getStringForDB(Format.getDateVerschoben(Format.getDate0Uhr(datumBis), 1)) + "'";

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}
	

	/**
	 * CO für die Person und den Zeitraum laden
	 * 
	 * @param personID
	 * @param vertreterID
	 * @param datum
	 * @throws Exception
	 */
	public void load(int personID, int vertreterID, Date datumVon, Date datumBis) throws Exception {
		String where;
		
		where = "PersonID=" + personID + " AND VertreterID=" + vertreterID
				+ " AND DATUM >= '" + Format.getStringForDB(datumVon) + "' "
				+ " AND DATUM < '" + Format.getStringForDB(Format.getDateVerschoben(Format.getDate0Uhr(datumBis), 1)) + "'";

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}
	

	/**
	 * Eintragungen ohne Freigabe der Vertratung laden
	 * 
	 * @param personID
	 * @param datumVon
	 * @param datumBis
	 * @throws Exception
	 */
	public void loadOhneFreigabe(int personID, Date datumVon, Date datumBis) throws Exception {
		String where;
		
		where = "PersonID=" + personID 
				+ " AND DATUM >= '" + Format.getStringForDB(datumVon) + "' "
				+ " AND DATUM < '" + Format.getStringForDB(Format.getDateVerschoben(Format.getDate0Uhr(datumBis), 1)) + "'"
				+ " AND IstFreigegeben=0";

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}


	/**
	 * CO für die Person als Vertreter in dem Zeitraum laden (nur bei genehmigtem Urlaub)
	 * 
	 * @param personID
	 * @param datum
	 * @return Set mit allen daten, an denen die person Vertreter ist
	 * @throws Exception
	 */
	public Set<Date> loadDateSetForVertreter(int vertreterID, Date datumVon, Date datumBis) throws Exception {
		loadForVertreter(vertreterID, datumVon, datumBis);

		Set<Date> set = new HashSet<Date>();
		if (moveFirst())
		{
			do
			{
				set.add(Format.getDate12Uhr(getFieldDatum().getDateValue()));
			} while (moveNext());
		}
		
		return set;
	}

	
	/**
	 * CO für die Person als Vertreter und den Zeitraum laden (nur bei genehmigtem Urlaub)
	 * 
	 * @param vertreterID
	 * @param datum
	 * @throws Exception
	 */
	public void loadForVertreter(int vertreterID, Date datumVon, Date datumBis) throws Exception {
		String where;
		
		where = "VertreterID=" + vertreterID 
				+ " AND DATUM >= '" + Format.getStringForDB(datumVon) + "' "
				+ " AND DATUM < '" + Format.getStringForDB(Format.getDateVerschoben(Format.getDate0Uhr(datumBis), 1)) + "'"
				+ " AND PersonID IN (SELECT PersonID FROM tblBuchung WHERE (BuchungsartID=" + CoBuchungsart.ID_URLAUB 
				+ " OR BuchungsartID=" + CoBuchungsart.ID_SONDERURLAUB + " OR BuchungsartID=" + CoBuchungsart.ID_FA + ")"
				+ " AND ( (StatusID = " + CoStatusBuchung.STATUSID_OK + " OR StatusID = " + CoStatusBuchung.STATUSID_GEAENDERT + ")"
				+ " OR (StatusID = " + CoStatusBuchung.STATUSID_VORLAEUFIG + " AND StatusGenehmigungID <> " + CoStatusGenehmigung.STATUSID_GEPLANT + ") )"
				+ " AND YEAR(Datum)=YEAR(tblVertreter.Datum) AND MONTH(Datum)=MONTH(tblVertreter.Datum) AND DAY(Datum)=DAY(tblVertreter.Datum)"
				+ ")"; 

		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}
	

	/**
	 * Vertreter für einen Urlaub laden und für die Anzeige aufbereiten
	 * 
	 * @param personID
	 * @param datumUrlaub
	 * @param datumUrlaubBis
	 * @throws Exception
	 */
	public void loadVertreter(int personID, Date datumUrlaub, Date datumUrlaubBis) throws Exception {
		CoVertreter coVertreter;
		
		// Vertreter laden
		coVertreter = new CoVertreter();
		coVertreter.load(personID, datumUrlaub, datumUrlaubBis);
		
		loadVertreter(coVertreter);
	}

//
//	/**
//	 * Den übergebenen Vertreter für einen Urlaub laden und für die Anzeige aufbereiten
//	 * 
//	 * @param vertreterID
//	 * @param datumUrlaub
//	 * @param datumUrlaubBis
//	 * @throws Exception
//	 */
//	public void loadVertretung(int vertreterID, Date datumUrlaub, Date datumUrlaubBis) throws Exception {
//		CoVertreter coVertreter;
//		
//		// Vertreter laden
//		coVertreter = new CoVertreter();
//		coVertreter.loadVertretung(vertreterID, datumUrlaub, datumUrlaubBis);
//		
//		loadVertreter(coVertreter);
//	}


	/**
	 * Vertreter für die Anzeige aufbereiten
	 * 
	 * @param personID
	 * @param datumUrlaub
	 * @param datumUrlaubBis
	 * @throws Exception
	 */
	public void loadVertreter(CoVertreter coVertreter) throws Exception {
		int vertreterID, lastVertreterID, iZeile;
		Date datum, lastDatum;
		
	
		// virtuelles Feld für Datum bis hinzufügen
		emptyCache();
		addFieldDatumBis();
		begin();

		if (coVertreter.moveFirst())
		{
			// Vertreter durchlaufen und aufbereitet in diesem CO speichern
			lastVertreterID = 0;
			lastDatum = null;
			do
			{
				datum = coVertreter.getDatum();
				vertreterID = coVertreter.getVertreterID();

				// bei neuem Vertreter neuen Eintrag erzeugen
				// Vertreter muss nicht den ganzen Zeitraum abedecken, abfangen fals mittendrin Tage fehlen 
				if (lastVertreterID != vertreterID || !Format.getDateVerschoben(lastDatum, 1).equals(datum))
				{
					add();
					setVertreterID(vertreterID);
					setDatum(datum);
					setDatumBis(datum);
				} 
				else
				{
					setDatumBis(datum);
				}

				lastVertreterID = vertreterID;
				lastDatum = datum;
			} while (coVertreter.moveNext());
		}
		
		// ggf. mit Leerzeilen zu Eingabe auffüllen
		iZeile = getRowCount();
		while (iZeile < ANZAHL_EINGABEZEILEN)
		{
			add();
			++iZeile;
		}
	}

	
	/**
	 * Aktuell eingetragene Vertreter für die übergeben Person speichern, Urlaubszeitraum beachten
	 * 
	 * @param personID
	 * @param datumUrlaub
	 * @param datumUrlaubBis
	 * @return gültige Vertreter gespeichert
	 * @throws Exception
	 */
	public boolean save(int personID, Date datumUrlaub, Date datumUrlaubBis) throws Exception{
		int vertreterID, anzahlUngueltigeEintraege;
		Date datum, datumBis;
		CoVertreter coVertreter;
		CoKontowert coKontowert;
		
		coKontowert = new CoKontowert();

		// einheitliche Uhrzeit setzen, sonst kann es zu Anzeigefehlern kommen 
		datumUrlaub = Format.getDate12Uhr(datumUrlaub);
		datumUrlaubBis = Format.getDate12Uhr(datumUrlaubBis);
	
		// alte Vertreter löschen, es werden immer alle Einträge neu angelegt, das ist einfacher als zu bearbeiten
		coVertreter = new CoVertreter();
		coVertreter.load(personID, datumUrlaub, datumUrlaubBis);
		coVertreter.deleteAll();
		coVertreter.save();
		
		
		// wenn kein Vertreter angegeben muss nichts gemacht werden
		if (!moveFirst())
		{
			return false;
		}
	
		// aktuelles CO mit den Vertretern durchlaufen und in neuem CO speichern
		anzahlUngueltigeEintraege = 0;
		coVertreter = new CoVertreter();
		coVertreter.begin();
		do
		{
			vertreterID = getVertreterID();
			// einheitliche Uhrzeit setzen, sonst kann es zu Anzeigefehlern kommen 
			datum = Format.getDate12Uhr(getDatum());
			datumBis = Format.getDate12Uhr(getDatumBis());
			
			// Urlaubszeitraum beachten
			if (datum == null || datum.before(datumUrlaub))
			{
				datum = datumUrlaub;
			}
			if (datumBis == null || datumBis.after(datumUrlaubBis))
			{
				datumBis = datumUrlaubBis;
			}

			// Fehler der Eingabe abfangen
			// kein Vertreter oder ungültiges Datum
			if (vertreterID == 0 || datum.after(datumBis))
			{
				continue;
			}
			
//			datum = Format.getDate12Uhr(datum);
//			datumBis = Format.getDate12Uhr(datumBis);

			// für jeden Tag des Zeitraums einen Datensatz erzeugen
			do
			{
				// prüfen, ob der Vertreter bereits eine Tagesbuchung für den Tag hat
				coKontowert.load(vertreterID, datum);
				if (coKontowert.getBuchungsartTagesbuchung() != null)
				{
					++anzahlUngueltigeEintraege;
				}
				else
				{
					// neuen Eintrag zum Speichern eines Vertreters erzeugen
					coVertreter.createNew();
					coVertreter.setPersonID(personID);
					coVertreter.setVertreterID(vertreterID);
					coVertreter.setDatum(datum);
					coVertreter.setIstFreigegeben(false);
				}
				
				// nächster Tag
				datum = Format.getDateVerschoben(datum, 1);
			} while (!datum.after(datumBis));
			

		} while (moveNext());


		// Vertreter speichern
		coVertreter.save();
		
		// Info bei ungültigen Einträgen
		if (anzahlUngueltigeEintraege > 0)
		{
			Messages.showWarningMessage("Es wurde(n) " + anzahlUngueltigeEintraege + " ungültige Vertretungs-Eintragung(en) gelöscht.");
		}
		
		// Rückgabe, ob gültige Vertreter gespeichert wurden
		return coVertreter.getRowCount() > 0;
	}


	/**
	 * Alle Vertreter zu den Buchungen der Person in dem übergebenen Zeitraum löschen 
	 * 
	 * @param personID
	 * @param datum
	 * @param datumBis
	 * @throws Exception
	 */
	public void deleteVertreter(int personID, Date datum, Date datumBis) throws Exception {
		load(personID, datum, datumBis);
		
		// Messages für das Messageboard erstellen
		createMessageVertretungFuer();
		
		// Eintragungen löschen
		deleteAll();
		save();
	}
	

	public IField getFieldVertreterID() {
		return getField("field." + getTableName() + ".vertreterid");
	}


	public int getVertreterID() {
		return Format.getIntValue(getFieldVertreterID());
	}


	public String getVertreter() {
		return getFieldVertreterID().getDisplayValue();
	}


	private void setVertreterID(int personID) {
		getFieldVertreterID().setValue(personID);
	}


	private String getResIdDatumBis() {
		return "virt.field.buchung.datumbis";
	}


	private IField getFieldDatumBis() {
		return getField(getResIdDatumBis());
	}


	public Date getDatumBis() {
		return Format.getDateValue(getFieldDatumBis());
	}


	private void setDatumBis(Date datum) {
		getFieldDatumBis().setValue(datum);
	}


	private IField getFieldIstFreigegeben() {
		return getField("field." + getTableName() + ".istfreigegeben");
	}


	private void setIstFreigegeben() {
		setIstFreigegeben(true);
	}


	private void setIstFreigegeben(boolean istFreigegeben) {
		getFieldIstFreigegeben().setValue(istFreigegeben);
	}


	/**
	 * Freigabe für alle aktuellen Datensätze speichern
	 * @throws Exception 
	 */
	public void setIstFreigegebenForAll() throws Exception {
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
			setIstFreigegeben();
		} while (moveNext());
		
	}


	/**
	 * "Vertretung für xxx (Zeitraum)"
	 */
	public String getMeldungVertretungFuer() {
		String meldung;
		
		if (!moveFirst())
		{
			return "";
		}
		
		// Vertretung für & Beginn
		meldung = "Vertretung für " + getPerson() + " (" + Format.getString(getDatum());
		
		// Ende, falls mehrtägig
		if (getRowCount() > 1)
		{
			moveLast();
			meldung += " - " + Format.getString(getDatum());
		}
		
		meldung += ")";
		
		return meldung;
	}


	/**
	 * Meldungen für das Messageboard erzeugen "Vertretung für xxx (Zeitraum)"
	 * @throws Exception 
	 */
	private void createMessageVertretungFuer() throws Exception {
		int vertreterID, lastVertreterID;
		String person;
		Date datum, datumBis;
		
		if (!moveFirst())
		{
			return;
		}
		
		person = null;
		lastVertreterID = 0;
		datum = null;
		datumBis = null;
		
		// Vertreter-Eintragungen durchlaufen
		do 
		{
			vertreterID = getVertreterID();
			
			// neuer vertreter
			if(vertreterID != lastVertreterID)
			{
				// wenn es nicht der erste Eintrag ist, erzeuge eine Meldung
				if (person != null)
				{
					new CoMessage().createMessageVertretungGeloescht(person, lastVertreterID, datum, datumBis);
				}
				
				person = getPerson();
				datum = getDatum();
				datumBis = null;
			}
			else // wenn es die gleiche Person ist, speicher das Enddatum
			{
				datumBis = getDatum();
			}
			
			lastVertreterID = vertreterID;
		} while (moveNext());

		
		// nach dem letzten Eintrag noch eine Meldung erzeugen
		new CoMessage().createMessageVertretungGeloescht(person, lastVertreterID, datum, datumBis);
	}


	/**
	 * Auflistung aller Vertreter mit Zeitraum für den geladenen Urlaub
	 */
	public String getMeldungVertreter() {
		String vertreter, meldung;
		Date datum, datumBis;
		
		if (!moveFirst())
		{
			return "";
		}
		
		meldung = "";

		do
		{
			vertreter = getVertreter();
			if (vertreter == null || vertreter.isEmpty())
			{
				continue;
			}
			
			datum = getDatum();
			datumBis = getDatumBis();
			
			meldung += "; " + vertreter + " (" + Format.getString(datum) + (datum.equals(datumBis) ? "" : (" - " + Format.getString(datumBis))) + ") ";
		} while (moveNext());
		
		if (meldung.length() > 2)
		{
			meldung = meldung.substring(2);
		}
		
		return meldung;
	}



}
