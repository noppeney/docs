package pze.business.objects.auswertung;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;


/**
 * Abstraktes CacheObject für die Personen (Ansicht) der Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class CoAnwesenheitTagesmeldung extends AbstractCacheObject {

	private static final String TABLE_NAME = "tbltagesmeldung";

	private boolean m_detailsAnzeigen;
	
	private GregorianCalendar m_gregDatum;
	
	private Map<GregorianCalendar, CoBuchung> m_mapDatumCoBuchung;
	
	private Map<String, String> m_mapNameEintragDatenschutz;
	
	// Personen mit OK- und vorläufigen Buchungen speichern
	private Set<String> m_setPersonBuchungOk;
	private Set<String> m_setPersonBuchungVorlaeufig;
	
	

	
	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoAnwesenheitTagesmeldung() throws Exception {
		super("table." + TABLE_NAME);
	}
	
	
	/**
	 * Kontruktor
	 * @param gregDatum 
	 * @param coAuswertung 
	 * @throws Exception 
	 */
	public CoAnwesenheitTagesmeldung(CoAuswertung coAuswertung, GregorianCalendar gregDatum) throws Exception {
		super("table." + TABLE_NAME);
		
		m_detailsAnzeigen = UserInformation.getInstance().isColorAnwesenheitDetail();
		
		m_gregDatum = (GregorianCalendar) gregDatum.clone();
		
		m_mapNameEintragDatenschutz = new HashMap<String, String>();
		m_setPersonBuchungOk = new HashSet<String>();
		m_setPersonBuchungVorlaeufig = new HashSet<String>();
		
		createCo();
	}

	long a, b, c=0, d=0, g;
	long a2, b2, c2=0, d2=0, c3, c4, c5;

	/**
	 * CO mir Daten füllen
	 * 
	 * @throws Exception
	 */
	private void createCo() throws Exception{
		CoAnwesenheitTagesmeldung coTmp;
		
		 c2=c3=c4=c5=0;
		 d2=0;
		begin();
		
		// Buchungsarten durchlaufen
		a = System.currentTimeMillis();

		// Zustände mit OK-Buchungen (Tagesbuchungen Abwesenheit) zuerst erstellen, da für die Personen keine vorläufigen Buchungen ausgegeben werden
		// z. B. kein gepl. OFA wenn MA krank
		addZustaendeOK();
		// temporär kopieren, um sie unten einzufügen
		coTmp = new CoAnwesenheitTagesmeldung();
		copy_indexed(coTmp);

		// vorläufige Zeit-Buchungen oben ausgeben
		emptyCache();
		begin();
		addZustaendeVorlaeufig();
		commit();
		
		// OK-Buchungen wieder anfügen
		coTmp.copy_indexed(this);
		if (!isEditing())
		{
			begin();
		}

		// Frei wegen Teilzeit
		addFrei();
		
		// abwesend wenn Datenschutz relevant
		addAbwesend();
		
		// Ausgabe zur Optimierung
		System.out.println("load je zustand: " + b2/1000.);
		System.out.println("Datum Ende: " + c2/1000.);
		System.out.println("Datum Ende-buchung: " + c4/1000.);
		System.out.println("Datum Ende-buchung/kontowert: " + c5/1000.);
		System.out.println("Uhrzeit Ende: " + d2/1000.);
		System.out.println("Gesamt Tagesmeldung: " + (System.currentTimeMillis()-a)/1000.);
		
		if (isEditing())
		{
			commit();
		}
	}


	/**
	 * Vorläufige Buchungen mit Zeiten
	 * 
	 * @throws Exception
	 */
	private void addZustaendeVorlaeufig() throws Exception {
		addZustand("OFA", CoBuchungsart.ID_ORTSFLEX_ARBEITEN, 0, 0, false, false, false);

		addZustand("Dienstgang", CoBuchungsart.ID_DIENSTGANG, 0, 0, false, true, false);
		
		addZustand("Dienstreise", CoBuchungsart.ID_DIENSTREISE, 0, 0, false, true, false);
		
		// Vorlesung erstmal so über PC-Buchungen, besser nachher über Genehmigung, dann müsste vom Azubi Status vorläufig oder OK eingetragen werden
		// aktuell lohnt sich der Aufwand aber nicht
		addZustand("Vorlesung FZJ", CoBuchungsart.ID_VORLESUNG, 0, 0, false, false, false);
//				, " AND BuchungserfassungsartID=" + CoBuchungserfassungsart.ID_PC
//				+ CoBuchung.getWhereStatusOkVorlaeufig());

		addZustand("KGG", CoBuchungsart.ID_KGG, 0, 0, false, false, false);

		// Kommen wird nicht ausgegeben, nur um arbeiten am arbeitsfreien Tag abzufangen
		addZustand("Kommen", CoBuchungsart.ID_KOMMEN, 0, 0, false, false, false);
	}


	/**
	 * OK-Buchungen füge tageweise Abwesenheiten
	 * 
	 * @throws Exception
	 */
	private void addZustaendeOK() throws Exception {
		addZustand("Krank", CoBuchungsart.ID_KRANK, CoBuchungsart.ID_KRANK_OHNE_LFZ, 0, true, false, true);
		
		addZustand("Urlaub", CoBuchungsart.ID_URLAUB, CoBuchungsart.ID_SONDERURLAUB, CoBuchungsart.ID_FA, true, false, false,
				CoBuchung.getWhereStatusOkVorlaeufig());
		
		addZustand("Elternzeit", CoBuchungsart.ID_ELTERNZEIT, 0, 0, true, false, true);
		
		// bez. Freistellung
		addZustand("freigestellt", CoBuchungsart.ID_BEZ_FREISTELLUNG, 0, 0, true, false, true);
	}


	/**
	 * Eine Zeile für einen Zustand in der Tagesmeldung erzeugen
	 * 
	 * @param bezeichnung bezeichnung des Zustands
	 * @param buchungsartID1 zu berücksichtigende Buchungsarten
	 * @param buchungsartID2 zu berücksichtigende Buchungsarten
	 * @param buchungsartID3 zu berücksichtigende Buchungsarten
	 * @param statusOkVorlaeufig true-OK, false-vorläufig
	 * @param bemerkungEintragen Bemerkung der Buchung eintragen, z. B. Ort der DR/DG
	 * @param datenschutz Zustad unterliegt dem Datenschutz
	 * @throws Exception
	 */
	private void addZustand(String bezeichnung, int buchungsartID1, int buchungsartID2, int buchungsartID3, 
			boolean statusOkVorlaeufig, boolean bemerkungEintragen, boolean datenschutz) throws Exception {
		addZustand(bezeichnung, buchungsartID1, buchungsartID2, buchungsartID3, statusOkVorlaeufig, bemerkungEintragen, datenschutz, null);
	}


	/**
	 * Eine Zeile für einen Zustand in der Tagesmeldung erzeugen
	 * 
	 * @param bezeichnung bezeichnung des Zustands
	 * @param buchungsartID1 zu berücksichtigende Buchungsarten
	 * @param buchungsartID2 zu berücksichtigende Buchungsarten
	 * @param buchungsartID3 zu berücksichtigende Buchungsarten
	 * @param statusOkVorlaeufig true-OK, false-vorläufig
	 * @param bemerkungEintragen Bemerkung der Buchung eintragen, z. B. Ort der DR/DG
	 * @param datenschutz Zustad unterliegt dem Datenschutz
	 * @param where alternativ zu statusOkVorlaeufig kann auch ein anderer Where-Teil angegeben werden
	 * @throws Exception
	 */
	private void addZustand(String bezeichnung, int buchungsartID1, int buchungsartID2, int buchungsartID3, 
			boolean statusOkVorlaeufig, boolean bemerkungEintragen, boolean datenschutz, String where) throws Exception {
		int statusGenehmigungID;
		boolean nichtGenehmigt;
		String name, eintrag, whereStatus;
		GregorianCalendar gregDatum;
		CoBuchung coBuchung;
		CoDienstreise coDienstreise;

		coDienstreise = new CoDienstreise();
		
		
		// where-Teil der SQL-Abfragen
		if (where == null)
		{
			whereStatus = statusOkVorlaeufig ? CoBuchung.getWhereStatusOK() : CoBuchung.getWhereStatusVorlaeufig();
		}
		else
		{
			whereStatus = where;
		}
		
		// Buchungen laden
		a2 = System.currentTimeMillis();
		coBuchung = new CoBuchung();
		coBuchung.loadForTagesmeldung(m_gregDatum, buchungsartID1, buchungsartID2, buchungsartID3, whereStatus);
		b2 += (System.currentTimeMillis()-a2);

		// Daten vorhanden?
		if (coBuchung.hasRows())
		{
			// für jeden zu prüfenden Tag werden die Buchungen gespeichert, damit sie nicht für jede Petrson neu geladen werden müssen
			m_mapDatumCoBuchung = new HashMap<GregorianCalendar, CoBuchung>();
			
			// Zeile hinzufügen
			add();
			setZustand(bezeichnung);
			
			// Buchungen durchlaufen
			coBuchung.moveFirst();
			do
			{
				// Status prüfen, damit vorläufige Buchungen markiert werden
				statusGenehmigungID = coBuchung.getStatusGenehmigungID();
				nichtGenehmigt = statusGenehmigungID != 0 && statusGenehmigungID != CoStatusGenehmigung.STATUSID_GENEHMIGT;

				// Person und ggf. Bemerkung eintragen
				name = coBuchung.getPerson();
				
				// bei ganztägigen OK-Buchungen eingetragene Personen speichern
				if (statusOkVorlaeufig && coBuchung.getUhrzeitAsInt() == 0)
				{
					m_setPersonBuchungOk.add(name);
				}
				else if (!statusOkVorlaeufig) // bei vorläufigen Buchungen nur die Personen ohne OK-Buchung ausgeben (z. B. kein OFA wenn krank)
				{
					if (m_setPersonBuchungOk.contains(name))
					{
						continue;
					}
					m_setPersonBuchungVorlaeufig.add(name);
				}
				
				// Kommen wird nicht ausgegeben, nur um arbeiten am arbeitsfreien Tag abzufangen
				if (buchungsartID1 == CoBuchungsart.ID_KOMMEN)
				{
					continue;
				}
				
				// Namen eintragen
				if (CoBuchungsart.isDrDg(buchungsartID1)) // bei DR das Ziel hinzufügen
				{
					coDienstreise.loadByID(coBuchung.getDienstreiseID());
					name += " (" + coDienstreise.getZiel() + ")";
				}
				name = formatiereGenehmigung(name, nichtGenehmigt);
//				name = formatiereGenehmigung(name 
//						+ (bemerkungEintragen && coBuchung.getBemerkung() != null ? " (" + coBuchung.getBemerkung() + ")" : ""), nichtGenehmigt);
				addName(name, datenschutz);

				// wenn es eine Uhrzeit gibt, trage sie ein
				eintrag = createEintragUhrzeit(coBuchung);
				
				// wenn es ein Enddatum gibt, trage es ein
				gregDatum = loadDatumEnde(coBuchung.getPersonID(), buchungsartID1, buchungsartID2, buchungsartID3, whereStatus);
				eintrag += (gregDatum == null ? "" : (eintrag.isEmpty() ? "" : "; ") + "bis " + Format.getString(gregDatum));
				
				addEintrag(formatiereGenehmigung(eintrag, nichtGenehmigt), name, datenschutz);
			} while (coBuchung.moveNext());
			
			// wenn kein Eintrag erstellt wurde, lösche die Zeile wieder
			if (getEintrag() == null)
			{
				delete();
			}
		}
	}


	/**
	 * Zustand "frei" hinzufügen für Teilzeitkräfte
	 * 
	 * @throws Exception
	 */
	private void addFrei() throws Exception {
		String name, eintrag;
		GregorianCalendar gregDatum;
		CoKontowert coKontowert;

		
		// Personen laden
		coKontowert = new CoKontowert();
		coKontowert.loadFrei(m_gregDatum);

		if (coKontowert.hasNoRows())
		{
			return;
		}
		
		// Zeile hinzufügen
		if (m_detailsAnzeigen)
		{		
			add();
			setZustand(CoAnwesenheitUebersicht.FREI);
		}

		// Personen durchlaufen
		coKontowert.moveFirst();
		do
		{
			// Person und ggf. Bemerkung eintragen
			name = coKontowert.getPerson();
			
			// Personen, die am freien Tag arbeiten nicht eintragen
			if (m_setPersonBuchungOk.contains(name) || m_setPersonBuchungVorlaeufig.contains(name))
			{
				continue;
			}
			
			// Namen eintragen
			addName(name, true);

			// wenn es ein Enddatum gibt, trage es ein
			gregDatum = loadFreiDatumEnde(coKontowert.getPersonID());
			eintrag = (gregDatum == null ? "" : "bis " + Format.getString(gregDatum));
			
			addEintrag(eintrag, name, true);
		} while (coKontowert.moveNext());
		
		// wenn eine Zeile erstellt wurde, aber kein Eintrag weil die die Person arbeitet am arbeitsfreien Tag, lösche den Eintrag Frei wieder
		if (m_detailsAnzeigen && getName() == null)
		{		
			delete();
		}
	}


	/**
	 * Zustand "abwesend" hinzufügen wegen Datenschutz
	 * 
	 * @throws Exception
	 */
	private void addAbwesend() throws Exception {
		TreeSet<String> setNamen;
		
		if (m_mapNameEintragDatenschutz.size() == 0)
		{		
			return;
		}

		// Zeile hinzufügen
		add();
		setZustand("abwesend");

		// Einträge durchlaufen
		setNamen = new TreeSet<>(m_mapNameEintragDatenschutz.keySet());
		for (String name : setNamen)
		{
			addName(name, false);
			addEintrag(m_mapNameEintragDatenschutz.get(name), name, false);
		}
	}


	/**
	 * Datum bis für den Eintrag einer Person suchen
	 * 
	 * @param personID
	 * @param buchungsartID
	 * @param buchungsartID2
	 * @param buchungsartID3
	 * @param where
	 * @return
	 * @throws Exception
	 */
	private GregorianCalendar loadDatumEnde(int personID, int buchungsartID, int buchungsartID2, int buchungsartID3, String where) throws Exception {
		GregorianCalendar gregDatum, gregDatumEnde;
		CoBuchung coBuchung;
		CoKontowert coKontowert;
		c = System.currentTimeMillis();
		
		gregDatum = (GregorianCalendar) m_gregDatum.clone();
		gregDatumEnde = null;
		coBuchung = new CoBuchung();
		coKontowert = new CoKontowert();
		
		
//		CoPerson.getInstance().moveToID(personID);
//		System.out.println(CoPerson.getInstance().getName());
		// Folgetage durchlaufen
		while (true)
		{
			// nächster Tag
			gregDatum.add(Calendar.DAY_OF_MONTH, 1);
			
			c3 = System.currentTimeMillis();
			// Buchungen laden, wenn es wieder eine Buchung gibt, gehe zum nächsten Tag
			if (!m_mapDatumCoBuchung.containsKey(gregDatum))
			{
//				System.out.println("load " + Format.getStringMitUhrzeit(gregDatum));
				m_mapDatumCoBuchung.put((GregorianCalendar) gregDatum.clone(), new CoBuchung());
				m_mapDatumCoBuchung.get(gregDatum).loadForTagesmeldung(gregDatum, buchungsartID, buchungsartID2, buchungsartID3, where);
			}
			coBuchung = m_mapDatumCoBuchung.get(gregDatum);
			c4 += (System.currentTimeMillis()-c3);
			if (coBuchung.moveToPersonID(personID))
			{
				// Tag mit der gleichen Buchung speichern
				gregDatumEnde = (GregorianCalendar) gregDatum.clone();
				continue;
			}

			// freie Tage bei Teilzeit prüfen, beenden bei Arbeitstagen
			coKontowert.load(personID, gregDatum.getTime(), false, false, false);
			c5 += (System.currentTimeMillis()-c3);
			if (coKontowert.hasRows() && coKontowert.getWertSollArbeitszeit() > 0)
			{
				break;
			}
		}
		c2 += (System.currentTimeMillis()-c);

		// letzten gültigen Tag zurückgeben
		return gregDatumEnde;
	}


	/**
	 * Datum bis für den Eintrag einer Person suchen
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	private GregorianCalendar loadFreiDatumEnde(int personID) throws Exception {
		GregorianCalendar gregDatum, gregDatumEnde;
		CoKontowert coKontowert;
		
		gregDatum = (GregorianCalendar) m_gregDatum.clone();
		gregDatumEnde = null;
		coKontowert = new CoKontowert();
		
		// Folgetage durchlaufen
		while (true)
		{
			// nächster Tag
			gregDatum.add(Calendar.DAY_OF_MONTH, 1);
			
			// freie Tage bei Teilzeit prüfen, beenden bei Arbeitstagen
			coKontowert.loadFrei(personID, gregDatum);
			if (coKontowert.hasNoRows() || !coKontowert.moveToPersonID(personID) || Format.isWochenende(gregDatum))
			{
				break;
			}

			gregDatumEnde = (GregorianCalendar) gregDatum.clone();
		}

		// letzten gültigen Tag zurückgeben
		return gregDatumEnde;
	}


	/**
	 * Uhrzeit für einen Eintrag suchen
	 * 
	 * @param coBuchung
	 * @return
	 * @throws Exception
	 */
	private String createEintragUhrzeit(CoBuchung coBuchung) throws Exception {
		int uhrZeitAsInt;
		int uhrBisZeitAsInt;
		String eintrag;
		d = System.currentTimeMillis();
		
		eintrag = "";
		uhrZeitAsInt = coBuchung.getUhrzeitAsInt();
		if (uhrZeitAsInt > 0)
		{
			// Uhrzeit bis bestimmen
			uhrBisZeitAsInt = loadUhrzeitEnde(coBuchung.getPersonID(), uhrZeitAsInt);
			
			// Startuhrzeit nicht bei FA nicht, wenn es Beginn der Arbeitszeit ist
			uhrZeitAsInt = coBuchung.getUhrzeitAsInt();
			if (coBuchung.getBuchungsartID() == CoBuchungsart.ID_FA 
					&& uhrZeitAsInt == CoFirmenparameter.getInstance().getRahmenarbeitszeitBeginn(coBuchung.getDatum()))
			{
				uhrZeitAsInt = 0;
			}

			// Eintrag generieren
			if (uhrBisZeitAsInt > 0)
			{
				eintrag = (uhrZeitAsInt > 0 ? Format.getZeitAsText(uhrZeitAsInt) + " - " : "bis ") + Format.getZeitAsText(uhrBisZeitAsInt);
			}
			else
			{
				eintrag = "ab " + Format.getZeitAsText(uhrZeitAsInt);
			}
		}
		d2 += (System.currentTimeMillis()-d);

		return eintrag;
	}


	private int loadUhrzeitEnde(int personID, int uhrzeitAb) throws Exception {
		CoBuchung coBuchung;
				
		coBuchung = new CoBuchung();
		coBuchung.loadBuchungVorlaeufig(personID, m_gregDatum, 0, uhrzeitAb);
		
		if (coBuchung.moveFirst())
		{
			return coBuchung.getUhrzeitAsInt();
		}
		
		return 0;
	}

//	wenn benötigt mit CoAnwesenheitsübersicht einheitlich machen
//	/**
//	 * OFA abkürzen
//	 * @throws Exception 
//	 */
//	private String formatiereText(String stringValue) throws Exception {
//
//		// OFA
//		stringValue = stringValue.replace(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_ORTSFLEX_ARBEITEN), "OFA");
//		
//		// FA
//		stringValue = stringValue.replace(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_FA), "FA");
//		
//		return stringValue;
//	}
//	

	/**
	 * nicht genehmigte Einträge fett/kursiv darstellen
	 * 
	 * @param eintrag
	 * @param nichtGenehmigt
	 * @return
	 */
	private String formatiereGenehmigung(String eintrag, boolean nichtGenehmigt) {
		if (nichtGenehmigt && !eintrag.isEmpty())
		{
			return "<b><i>" + eintrag + "</i></b>";
		}
		
		return eintrag;
	}


	private IField getFieldZustand() {
		return getField("field." + getTableName() + ".zustand");
	}


	private void setZustand(String zustand) {
		getFieldZustand().setValue(zustand);
	}

	
	private IField getFieldName() {
		return getField("field." + getTableName() + ".name");
	}

	
	private String getName() {
		return Format.getStringValue(getFieldName());
	}


	private void setName(String name) {
		getFieldName().setValue(name);
	}


	private void addName(String name,  boolean datenschutz) {
		
		// prüfen, ob der Eintrag aus Datenschutzgründen ausgegeben werden darf
		if (datenschutz && !m_detailsAnzeigen)
		{
//			m_mapNameEintragDatenschutz.put(name, null);
			return;
		}
		
		// Name ggf. anfügen
		if (getName() == null)
		{
			setName(name);
		}
		else
		{
			setName(getName() + "<br/>" + name);
		}
	}

	
	private IField getFieldEintrag() {
		return getField("field." + getTableName() + ".eintrag");
	}

	
	private String getEintrag() {
		return Format.getStringValue(getFieldEintrag());
	}


	private void setEintrag(String eintrag) {
		getFieldEintrag().setValue(eintrag);
	}


	private void addEintrag(String eintrag, String name, boolean datenschutz) {

		// geschütztes Leerzeichen wenn nichts eingetragen werden soll
		if (eintrag == null || eintrag.isEmpty())
		{
			eintrag = "&nbsp;";
		}
		
		// prüfen, ob der Eintrag aus Datenschutzgründen ausgegeben werden darf
		if (datenschutz && !m_detailsAnzeigen)
		{
			m_mapNameEintragDatenschutz.put(name, eintrag);
			return;
		}

		// Eintrag ggf. anfügen
		if (getEintrag() == null)
		{
			setEintrag(eintrag);
		}
		else
		{
			setEintrag(getEintrag() + "<br/>" + eintrag);
		}
	}


//	/**
//	 * Prüft, ob das Datum ein Feiertag ist
//	 * 
//	 * @param datum
//	 * @return
//	 */
//	private boolean isFeiertag(GregorianCalendar datum)
//	{
//		return m_feiertagGenerator.isFeiertag(datum, CoBundesland.ID_NRW);
//	}
//
//
//	/**
//	 * Prüft, ob das Datum ein Brückentag ist
//	 * 
//	 * @param datum
//	 * @return
//	 */
//	private boolean isBrueckentag(GregorianCalendar datum)
//	{
//		return m_coBrueckentag.isBrueckentag(datum, CoBundesland.ID_NRW);
//	}
//

}
