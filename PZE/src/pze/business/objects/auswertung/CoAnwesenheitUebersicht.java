package pze.business.objects.auswertung;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.FeiertagGenerator;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoBrueckentag;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.business.objects.reftables.personen.CoBundesland;

/**
 * Abstraktes CacheObject für die Personen (Ansicht) der Anwesenheitsübersicht
 * 
 * @author Lisiecki
 *
 */
public class CoAnwesenheitUebersicht extends AbstractCacheObject {

	private static final String TABLE_NAME = "tblwochenansicht";
	public static final String FREI = "frei";

	private GregorianCalendar m_gregDatum;
	
	private boolean m_detailsAnzeigen;

	private CoPerson m_coPerson;
	private CoAuswertung m_coAuswertung;

	private FeiertagGenerator m_feiertagGenerator;
	private CoBrueckentag m_coBrueckentag;

	/**
	 * Speichert die Zellennummern der geplanten Buchungen
	 */
	private Set<Integer> m_setIndexGeplanteBuchungen;
	
	
	/**
	 * Default-Konstruktor
	 */
	public CoAnwesenheitUebersicht() {
		m_setIndexGeplanteBuchungen = new HashSet<Integer>();
		
		m_detailsAnzeigen = UserInformation.getInstance().isColorAnwesenheitDetail();
	}


	/**
	 * Kontruktor
	 * @param gregDatum 
	 * @param coAuswertung 
	 * @throws Exception 
	 */
	public CoAnwesenheitUebersicht(CoAuswertung coAuswertung, GregorianCalendar gregDatum) throws Exception {
		super("table." + TABLE_NAME);
		
		m_detailsAnzeigen = UserInformation.getInstance().isColorAnwesenheitDetail();

		m_coAuswertung = coAuswertung;
		m_gregDatum = (GregorianCalendar) gregDatum.clone();
		
		m_feiertagGenerator = FeiertagGenerator.getInstance();
		m_coBrueckentag = CoBrueckentag.getInstance();

		m_setIndexGeplanteBuchungen = new HashSet<Integer>();

		createCo();
	}


	long a, b, c, d, g;
	long a2, b2, c2, d2;

	public void createCo() throws Exception{
		int iTag;
		boolean brueckentage[];
		String eintrag, feiertage[];
		
		
		// Personen laden
		if (!loadCoPerson())
		{
			return;
		}
		g = System.currentTimeMillis();

		// Feier- und Brückentage laden
		brueckentage = new boolean[5];
		feiertage = new String[5];
		init(brueckentage, feiertage);
		
		// Personen durchlaufen
		begin();
		do
		{
			// Zeile für die Person hinzufügen
			add();
			setPersonID(m_coPerson.getID());
			
			
			// Schleife von Montag bis Freitag  
			m_gregDatum.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			for (iTag=0; iTag<5; ++iTag)
			{
				eintrag = null;
				a2 = System.currentTimeMillis();
				
				// Feiertag
				if (feiertage[iTag] != null)
				{
					eintrag = feiertage[iTag];
				}
				// Brückentag
				else if (brueckentage[iTag])
				{
					eintrag = "Brückentag";
				}
				// frei/kein Arbeitstag wg. Zeitmodell
//				else if (!m_coPerson.isArbeitstag(m_gregDatum))
//				{
//					eintrag = "frei";
//				}
				else // Buchungen und Kontowerte für den Tag prüfen
				{
					eintrag = bestimmeEintrag(m_coPerson.getID(), Format.getDateValue(m_gregDatum), iTag);
				}
				c = System.currentTimeMillis();
			
				// Eintrag speichern
				if (eintrag != null)
				{
					getField(1+iTag).setValue(eintrag);
				}

				
				// nächster Tag
				m_gregDatum.add(Calendar.DAY_OF_WEEK, 1);
				d += (System.currentTimeMillis()-c);
				b2 += (System.currentTimeMillis()-a2);
			}

		} while (m_coPerson.moveNext());
		
		
		System.out.println("load Kontowerte: " + b/1000.);
		System.out.println("load Buchungen: " + d2/1000.);
		System.out.println("load for: " + b2/1000.);
		System.out.println("load ...: " + d/1000.);
		System.out.println("Gesamt: " + (System.currentTimeMillis()-g)/1000.);
		
		commit();
	}

	
	/**
	 * Eintrag für die Anwesenheitsübersicht für einen Tag bestimmen
	 * 
	 * @param personID
	 * @param datum
	 * @param iTag
	 * @return
	 * @throws Exception
	 */
	public String bestimmeEintrag(int personID, Date datum, int iTag) throws Exception {
		int buchungsartID, statusGenehmigungID, zeitBeginn, zeitEnde;
		boolean sucheEndzeit;
		String eintrag, bemerkung;
		CoKontowert coKontowert;
		CoBuchung coBuchung;
		CoDienstreise coDienstreise;
		
		zeitBeginn = 0;
		eintrag = null;
		coBuchung = new CoBuchung();
		coKontowert = new CoKontowert();
		coDienstreise = new CoDienstreise();

		
		// Tagesbuchungen
		a = System.currentTimeMillis();
		coKontowert.load(personID, datum, false, false, false);
		b += (System.currentTimeMillis()-a);
		if (coKontowert.hasRows())
		{
			eintrag = coKontowert.getBuchungsartTagesbuchung();
		}
		
		// sonst Buchungen prüfen und Eintrag erzeugen
		if (eintrag == null)
		{
			// freie Tage bei Teilzeit
			if (coKontowert.hasRows() && coKontowert.getWertSollArbeitszeit() == 0)
			{
				eintrag = FREI;
			}
			
			c2 = System.currentTimeMillis();
		
			// sonstige Buchungen, OFA, DR, DG, KGG, Vorlesung
			coBuchung.loadBuchungVorlaeufig(personID, datum, 0);
			if (coBuchung.hasRows())
			{
				eintrag = "";
				sucheEndzeit = false;
				bemerkung = null;
				coBuchung.moveFirst();
				do 
				{
					// ggf. Endzeit eintragen
					if (sucheEndzeit)
					{
						// Endzeit muss nach der Anfangszeit einer Buchung sein
						// (durch mehrere Buchungen an einem Tag, z. B. OFA/FA/OFA, kann es mehrere Buchungen mit gleicher Uhrzeit geben)
						zeitEnde = coBuchung.getUhrzeitAsInt();
						if (zeitEnde == zeitBeginn)
						{
							continue;
						}

						// "-" bei von/bis-Einträgen, "bis " wenn es nur eine Endzeit gibt
						eintrag += (eintrag.endsWith(" ") ? " - " : " bis ") + Format.getZeitAsText(zeitEnde);
						eintrag += bemerkung == null || bemerkung.isEmpty() ? "" : " (" + bemerkung + ")";
						bemerkung = null;
						sucheEndzeit = false;
					}
					
					// bei einer entsprechenden Buchungsart den Eintrag erstellen
					buchungsartID = coBuchung.getBuchungsartID();
					zeitBeginn = coBuchung.getUhrzeitAsInt();
					// Buchungen mit Uhrzeit
					if (
//							(eintrag.equals(FREI) && buchungsartID == CoBuchungsart.ID_KOMMEN) // Kommen nur an arbeitsfreien Tagen
//							||  TODO Abfrage geht so nicht, eintrag wird oben auf "" gesetzt, bei Kommen soll optional ein Antrag erstellt werden, dann nur diese KOmmen-Buchungen laden
							// bei eigenen Kommen-Buchungen automatisch Antrag erstellen, wenn für andere gebucht wird ja/nein-Abfrage
							buchungsartID == CoBuchungsart.ID_ORTSFLEX_ARBEITEN 
							|| buchungsartID == CoBuchungsart.ID_DIENSTREISE || buchungsartID == CoBuchungsart.ID_DIENSTGANG
							|| buchungsartID == CoBuchungsart.ID_KGG || buchungsartID == CoBuchungsart.ID_VORLESUNG
							|| buchungsartID == CoBuchungsart.ID_FA || buchungsartID == CoBuchungsart.ID_KRANK)
					{
						eintrag += " " + coBuchung.getBuchungsart() 
								// FA/Krank ab 6.15 nicht eintragen
								+ ((buchungsartID == CoBuchungsart.ID_FA || buchungsartID == CoBuchungsart.ID_KRANK)
								&& (zeitBeginn == CoFirmenparameter.getInstance().getRahmenarbeitszeitBeginn(coBuchung.getDatum())
								|| zeitBeginn == 0)
								? "" : " " + Format.getZeitAsText(zeitBeginn) + " ");
						sucheEndzeit = true;
						
						// Bemerkung für Ort von DR und DG
						if (CoBuchungsart.isDrDg(buchungsartID))
						{
							coDienstreise.loadByID(coBuchung.getDienstreiseID());
							if (coDienstreise.hasRows())
							{
								bemerkung = coDienstreise.getZiel();
							}
							else // TODO löschen : alte Version vor digitalen Anträgen
							{
								bemerkung = coBuchung.getBemerkung();
							}
						}
					}
					// geplanter Urlaub ohne Uhrzeit
					else if (buchungsartID == CoBuchungsart.ID_URLAUB)
					{
						eintrag = coBuchung.getBuchungsart();
					}
					else // wenn Buchung nicht relevant, keinen Status prüfen
					{
						continue;
					}
//					else if (sucheEndzeit)
//					{
//						// "-" bei von/bis-Einträgen, "bis " wenn es nur eine Endzeit gibt
//						eintrag += (eintrag.endsWith(" ") ? " - " : " bis ") + Format.getZeitAsText(coBuchung.getUhrzeitAsInt());
//						eintrag += bemerkung == null ? "" : " (" + bemerkung + ")";
//						sucheEndzeit = false;
//					}
					
					// Status prüfen, damit vorläufige Buchungen markiert werden
					statusGenehmigungID = coBuchung.getStatusGenehmigungID();
					if (statusGenehmigungID != 0 && statusGenehmigungID != CoStatusGenehmigung.STATUSID_GENEHMIGT)
					{
						m_setIndexGeplanteBuchungen.add(getCurrentRowIndex()*getFieldCount() + iTag + 2);
						
						// den Namen auch markieren
						m_setIndexGeplanteBuchungen.add(getCurrentRowIndex()*getFieldCount() + 1);
					}

				} while (coBuchung.moveNext());
				
				// wenn keine Endzeit angegeben wurde, z. B. DR ab 14 Uhr, Bemerkung eintragen
				if (bemerkung != null)
				{
					eintrag += bemerkung == null || bemerkung.isEmpty() ? "" : " (" + bemerkung + ")";
					bemerkung = null;
				}
			}
			d2 += (System.currentTimeMillis()-c2);

		}

		if (eintrag != null)
		{
			eintrag = formatiereText(eintrag.trim().replace("  ", " "));
		}
		return eintrag;
	}

	
	/**
	 * alle ausgewählten Personen laden
	 * 
	 * @return
	 * @throws Exception
	 */
	private boolean loadCoPerson() throws Exception {
		String where, wherePosition;

		where = m_coAuswertung.getWherePerson();
		wherePosition = m_coAuswertung.getWherePosition();
		where += (wherePosition != null ? wherePosition : "");

		// alle ausgewählten Personen laden
		m_coPerson = new CoPerson();
		m_coPerson.load(where.replace("PersonID", "ID"));

		return m_coPerson.moveFirst();
	}
	
	
	/**
	 * OFA abkürzen
	 * @throws Exception 
	 */
	private String formatiereText(String stringValue) throws Exception {
		CoBuchungsart coBuchungsart;
		
		coBuchungsart = CoBuchungsart.getInstance();

		// OFA
		stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_ORTSFLEX_ARBEITEN), "OFA");
		
		// FA
//		stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_FA), "FA");
		stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_FA), 
				(m_detailsAnzeigen ? "FA" : coBuchungsart.getBezeichnung(CoBuchungsart.ID_URLAUB)));
		
		// Berücksichtigung Datenschutz
		if (!m_detailsAnzeigen)
		{
			stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_KRANK_OHNE_LFZ), "abwesend");
			stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_KRANK), "abwesend");
			stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_ELTERNZEIT), "abwesend");
			stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_BEZ_FREISTELLUNG), "abwesend");
			stringValue = stringValue.replace(FREI, "abwesend");
			
			stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_SONDERURLAUB), coBuchungsart.getBezeichnung(CoBuchungsart.ID_URLAUB));

			// FA ganztags zu Urlaub
//			if (stringValue.equals("FA"))
//			{
//				stringValue = coBuchungsart.getBezeichnung(CoBuchungsart.ID_URLAUB);
//			}
		}
		
		// Krank ohne Lfz. nur für Personalverwaltung
		if (!UserInformation.getInstance().isGruppeAnwesenheitsuebersichtDetail())
		{
			stringValue = stringValue.replace(coBuchungsart.getBezeichnung(CoBuchungsart.ID_KRANK_OHNE_LFZ), coBuchungsart.getBezeichnung(CoBuchungsart.ID_KRANK));
		}

		return stringValue;
	}
	
	
	/**
	 * Prüft, ob es eine geplante (oder genehmigte) Buchung ist
	 * 
	 * @param cell
	 * @return geplant, noch nicht genehmigt
	 */
	public boolean isGeplanteBuchung(ISpreadCell cell) {
		return isGeplanteBuchung(getCurrentRowIndex(), getFieldIndex(cell.getField()));
	}
	
	
	/**
	 * Prüft, ob es eine geplante (oder genehmigte) Buchung ist
	 * 
	 * @param iRow
	 * @param iField
	 * @return geplant, noch nicht genehmigt
	 */
	public boolean isGeplanteBuchung(int iRow, int iField) {
		return m_setIndexGeplanteBuchungen.contains(iRow*getFieldCount() + iField + 1);
	}
	

	/**
	 * Feiertage und Brückentage initialisieren
	 * 
	 * @param brueckentage
	 * @param feiertage
	 */
	private void init(boolean[] brueckentage, String[] feiertage) {
		int iTag;
		
		// Tage durchlaufen
		m_gregDatum.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		for (iTag=0; iTag<5; ++iTag)
		{
			brueckentage[iTag] = m_coBrueckentag.isBrueckentag(m_gregDatum, CoBundesland.ID_NRW);
			feiertage[iTag] = m_feiertagGenerator.getFeiertag(m_gregDatum, CoBundesland.ID_NRW);
			
			// nächster Tag
			m_gregDatum.add(Calendar.DAY_OF_WEEK, 1);
		}
	}


}
