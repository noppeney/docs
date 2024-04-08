package pze.ui.formulare.freigabecenter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.ISelectionListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.export.urlaub.ExportUrlaubsplanungListener;
import pze.business.objects.CoMessage;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.CoVertreter;
import pze.business.objects.reftables.CoGrundSonderurlaub;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.ui.controls.SortedTableControl;
import pze.ui.formulare.auswertung.FormAuswertungUrlaubsplanung;
import pze.ui.formulare.person.DialogBuchungAendern;
import pze.ui.formulare.person.DialogGrundSonderurlaub;
import pze.ui.formulare.person.DialogVertreter;



/**
 * Formular für das Freigabecenter für eigenen Urlaub
 * 
 * @author Lisiecki
 *
 */
public class FormFreigabecenterUrlaub extends AbstractFormFreigabecenterMitarbeiter {
	
	protected static String RESID = "form.freigabenurlaubneu";
	protected static final String CAPTION = "Urlaub/FA";
	public static final String BUCHUNGSARTID = CoBuchungsart.ID_URLAUB + ", " + CoBuchungsart.ID_SONDERURLAUB + ", " + CoBuchungsart.ID_FA;

	private IButtonControl m_btUrlaubsplanungAktuell;
	private IButtonControl m_btUrlaubsplanungNaechstesJahr;
	private IButtonControl m_btVertreterAendern;

	private TextControl m_tfResturlaub;
	private TextControl m_tfResturlaubGeplant;

	
	
	/**
	 * Konstruktor
	 * @param parent		visueller Parent
	 * @param formFreigabecenter 
	 * @throws Exception
	 */
	public FormFreigabecenterUrlaub(Object parent, FormFreigabecenter formFreigabecenter) throws Exception {
		super(parent, formFreigabecenter, CAPTION, RESID);
		
		initFormular();
	}

	
	@Override
	protected void loadAntraegeGeplant(boolean zeitraum) throws Exception {
		m_coBuchungGeplant.loadAntraegeGeplant(m_personID, BUCHUNGSARTID, true);
	}

	
	@Override
	protected void loadAntraegeAktuell(boolean zeitraum) throws Exception {
		m_coBuchungAktuell.loadAntraegeAktuell(m_personID, BUCHUNGSARTID, true);
	}


	@Override
	protected void loadAntraegeAbgeschlossen() throws Exception {
		m_coBuchungAbgeschlossen.loadAntraegeAbgeschlossen(m_personID, m_tfDatumVon.getField().getDateValue(), m_tfDatumBis.getField().getDateValue(), 
				BUCHUNGSARTID, true);
	}


	public static String getKey(int id) {
		return "freigaben.mitarbeiter.urlaub." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(getID());
	}
	

	/**
	 * Formularfelder und Listener initialisieren
	 * @throws Exception 
	 */
	private void initFormular() throws Exception {

		m_tfResturlaub = (TextControl) findControl(getResID() + ".resturlaub");
		m_tfResturlaubGeplant = (TextControl) findControl(getResID() + ".resturlaubgeplant");

		m_btUrlaubsplanungAktuell = (IButtonControl) findControl(getResID() + ".aktuellesjahr");
		if (m_btUrlaubsplanungAktuell != null)
		{
			m_btUrlaubsplanungAktuell.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedUrlaubsplanung(m_personID, 0);
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		m_btUrlaubsplanungNaechstesJahr = (IButtonControl) findControl(getResID() + ".naechstesjahr");
		if (m_btUrlaubsplanungNaechstesJahr != null)
		{
			m_btUrlaubsplanungNaechstesJahr.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedUrlaubsplanung(m_personID, 1);
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}

		m_btVertreterAendern = (IButtonControl) findControl(getResID() + ".aktuell.vertreter.aendern");
		if (m_btVertreterAendern != null)
		{
			m_btVertreterAendern.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						clickedVertreterAendern(m_coBuchungAktuell);
					} 
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				@Override
				public void defaultSelected(IControl control, Object params) {
				}
			});
		}
	}


	@Override
	protected void clickedBuchungBeantragen(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception {
		if (clickedBeantragen(coBuchung, tableBuchungen))
		{
			clickedAktualisieren();
		}
	}


	@Override
	protected void clickedBuchungAendern(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception {
		DialogBuchungAendern.showDialogWithBuchung(coBuchung);
		clickedAktualisieren();
	}


	@Override
	protected void clickedBuchungLoeschen(CoBuchung coBuchung, TableAntraege tableBuchungen) throws Exception {
		clickedLoeschen(coBuchung, tableBuchungen);
		clickedAktualisieren();
	}


	private void clickedVertreterAendern(CoBuchung coBuchung) throws Exception {
		// Vertreter ändern
		DialogVertreter.showDialog(coBuchung.getPersonID(), coBuchung.getDatum(), coBuchung.getDatumBis(), false);
		
		// Status zurücksetzen
		coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_GEPLANT);
		
		clickedAktualisieren();
	}


	/**
	 * Status OK für alle Urlaubstage der aktuellen Auswahl setzen
	 * 
	 * @param table 
	 * @return 
	 * @throws Exception
	 */
	public static boolean clickedBeantragen(CoBuchung coBuchung, SortedTableControl table) throws Exception {
		int buchungsartID;
		Date datumBis;
		
		// prüfen, ob ein Eintrag ausgewählt ist
		if (!coBuchung.moveTo(table.getSelectedBookmark()))
		{
			return false;
		}
		
		// Sicherheitsabfrage
		datumBis = coBuchung.getDatumBis();
		if (!Messages.showYesNoMessage("Freigabe beantragen", "Möchten Sie die Freigabe des Antrags '" + coBuchung.getBuchungsart() + "' vom "
				+ Format.getString(coBuchung.getDatum()) + (datumBis == null ? "" : "-" + Format.getString(datumBis))
				+ " wirklich beantragen?"))
		{
			return false;
		}

		// Vertreter eingeben
		if (!DialogVertreter.showDialog(coBuchung.getPersonID(), coBuchung.getDatum(), coBuchung.getDatumBis(), true))
		{
			return false;
		}

		
		// Urlaub, Sonderurlaub, FA unterscheiden
		buchungsartID = coBuchung.getBuchungsartID();
		switch (buchungsartID)
		{
		case CoBuchungsart.ID_URLAUB:
			int personID, resturlaub, anzahlUrlaubAntrag, anzahlUrlaubBeantragt;
			Date datum;
			GregorianCalendar gregDatum;
			CoKontowert coKontowert;
			
			coKontowert = new CoKontowert();

			// Personendaten
			personID = coBuchung.getPersonID();
			
			// Datum des Urlaubs
			datum = coBuchung.getDatum();
			gregDatum = Format.getGregorianCalendar(datum);

			// Resturlaub aktuellen Jahres (Jahr des Urlaubs) setzen
			gregDatum.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
			gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 31);
			coKontowert.load(personID, Format.getDateValue(gregDatum));
			// wenn der Eintrag nicht existiert, lade den letzten
			if (coKontowert.getRowCount() == 0)
			{
				coKontowert.loadLastEintrag(personID);
			}
			resturlaub = coKontowert.getResturlaub();
			
			// Urlaubstage dieses Antrags
			anzahlUrlaubAntrag = CoBuchung.getAnzahlGeplantenUrlaub(personID, datum, coBuchung.getDatumBis());

			// Anzahl bereits beantragter Urlaubstage
			anzahlUrlaubBeantragt = CoBuchung.getAnzahlbeantragteTage(personID, gregDatum.get(Calendar.YEAR), buchungsartID);
			
			// Anzahl Urlaubstage prüfen
			if (anzahlUrlaubAntrag > resturlaub - anzahlUrlaubBeantragt)
			{
				Messages.showErrorMessage("Die Anzahl der mit diesem Antrag geplanten Urlaubstage (" + anzahlUrlaubAntrag 
						+ ") ist größer als die Anzahl der Resturlaubstage (" + resturlaub 
						+ (anzahlUrlaubBeantragt > 0 ? ", " + anzahlUrlaubBeantragt + " bereits beantragt" : "")
						+ ").");
				return false;
			}
			
			break;
		case CoBuchungsart.ID_SONDERURLAUB:
			int anzahlTageGeplant, anzahlTageZulaessig;
			
			// Grund für den Sonderurlaub abfragen
			if (!DialogGrundSonderurlaub.showDialog())
			{
				return false;
			}
			
			// Anzahl der Sonderurlaubstage prüfen
			anzahlTageGeplant = CoBuchung.getAnzahlGeplantenSonderurlaub(coBuchung.getPersonID(), coBuchung.getDatum(), coBuchung.getDatumBis());
			anzahlTageZulaessig = CoGrundSonderurlaub.getInstance().getAnzahlTage(DialogGrundSonderurlaub.getGrundID());
			if (anzahlTageGeplant > anzahlTageZulaessig)
			{
				Messages.showErrorMessage("Die Anzahl der geplanten Sonderurlaubstage (" + anzahlTageGeplant 
						+ ") ist größer als die Anzahl der für diesen Grund zulässigen Sonderurlaubstage (" + anzahlTageZulaessig + ").");
				return false;
			}
			
			// Grund als Kommentar speichern
			saveGrundSonderurlaub(coBuchung);
			Messages.showInfoMessage("Freigabe Personalverwaltung erforderlich", 
					"Bitte weisen Sie den Grund für den Sonderurlaub bei der Personalverwaltung nach.");
			
			break;
		case CoBuchungsart.ID_FA:
			
			// Zeitkonto prüfen
			if (!checkZeitkontoFa(coBuchung))
			{
				return false;
			}
			
			break;
		}

		
		// Status setzen
		coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_BEANTRAGT);
		
		// ggf. Zusatzinfo speichern
		saveZusatzinfo(coBuchung);
		
		// GF brauchen bei Urlaubsbuchungen < 3 Tagen (ohne Vertreter) keine Genehmigung
		CoPerson coPerson;
		coPerson = CoPerson.getInstance();
		coPerson.moveToID(coBuchung.getPersonID());

		coBuchung.loadVertreter();

		if (coPerson.getAbteilungID() == CoAbteilung.ID_GESCHAEFTSFUEHRUNG 
				&& (coBuchung.getVertreter() == null || coBuchung.getVertreter().isEmpty()))
		{
			coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_GENEHMIGT);
		}
		
		return true;
	}

	
	/**
	 * Status ungültig für alles Urlaubstage der aktuellen Auswahl setzen
	 * 
	 * @throws Exception
	 */
	public static void clickedLoeschen(CoBuchung coBuchung, SortedTableControl table) throws Exception {
		int statusID, anzahlTage;
		Date datum, datumBis;  
		
		statusID = coBuchung.getStatusID();

		// prüfen, ob ein Eintrag ausgewählt ist
		if (!coBuchung.moveTo(table.getSelectedBookmark()))
		{
			return;
		}
		
		// Sicherheitsabfrage
		datum = Format.getDate12Uhr(coBuchung.getDatum());
		datumBis = Format.getDate12Uhr(coBuchung.getDatumBis());
		if (!Messages.showYesNoMessage("Buchung löschen", "Möchten Sie die Buchung '" + coBuchung.getBuchungsart() + "' vom "
				+ Format.getString(datum) + (datumBis == null ? "" : "-" + Format.getString(datumBis))
				+ " wirklich löschen?")) 
		{
			return;
		}

		// 

		// Status setzen
		anzahlTage = coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_GELOESCHT);

		// Vertreter-Eintragungen löschen
		deleteVertreter(coBuchung); 

		// ggf. Meldung an die Personalverwaltung
		new CoMessage().createMessageUrlaubGeloescht(coBuchung.getPersonID(), datum, datumBis, coBuchung.getBuchungsart(), statusID, anzahlTage);
	}


	/**
	 * Bei ganztägigen FA-Buchungen das Zeitkonto prüfen
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean checkZeitkontoFa(CoBuchung coBuchung) throws Exception {
		return checkZeitkontoFa(coBuchung.getPersonID(), coBuchung.getDatum(), coBuchung.getDatumBis(), 
				coBuchung.getUhrzeitAsInt(), coBuchung.getUhrzeitBisAsInt());
	}


	/**
	 * Bei ganztägigen FA-Buchungen das Zeitkonto prüfen
	 * 
	 * @return
	 * @throws Exception
	 */
	private static boolean checkZeitkontoFa(int personID, Date datumVon, Date datumBis, int uhrzeitAsInt, int uhrzeitBisAsInt) throws Exception {
		int zeit, zeitkonto;
		CoKontowert coKontowert;
		
		
		// wenn der erste Tag nur teilweise ist, muss erst ab dem nächsten Tag geprüft werden
		if (uhrzeitAsInt > 0)
		{
			datumVon = Format.getDateVerschoben(datumVon, 1);
		}

		// wenn der letzte Tag nur teilweise ist, muss nur bis zum vorletzten Tag geprüft werden
		if (uhrzeitBisAsInt > 0)
		{
			datumBis = Format.getDateVerschoben(datumBis, -1);
		}

		// benötigte Zeit über Sollarbeitszeit berechnen
		coKontowert = new CoKontowert();
		coKontowert.load(personID, datumVon, datumBis);
		zeit = 0;
		
		if (coKontowert.moveFirst())
		{
			// Kontowerte durchlaufen
			do
			{
				zeit += coKontowert.getWertSollArbeitszeit();
			} while (coKontowert.moveNext());
		}

		// aktuelles Zeitguthaben
		coKontowert.load(personID, Format.getDateVerschoben(new Date(), -1));
		zeitkonto = coKontowert.getWertUeberstundenGesamt();
		
		// Prüfung
		if (zeitkonto < zeit)
		{
			Messages.showErrorMessage("Abbau von Zeitguthaben nicht möglich", "Ihr Zeitkonto (" + Format.getZeitAsText(zeitkonto) 
			+ ") muss mindestens einen Wert von " + Format.getZeitAsText(zeit) + " aufweisen.");
			return false;
		}
		
		return true;
	}


	/**
	 * Grund des Sonderurlaubs speichern
	 * 
	 * @throws Exception
	 */
	private static void saveGrundSonderurlaub(CoBuchung coBuchung) throws Exception {
		CoBuchung coBuchungGrund;
		
		// Buchung laden
		coBuchungGrund = new CoBuchung();
		coBuchungGrund.loadByID(coBuchung.getID());
		coBuchungGrund.begin();
		
		// Bemerkung um Grund Sonderurlaub ersetzen
		coBuchungGrund.setBemerkung((coBuchung.getBemerkung() == null ? "" : coBuchung.getBemerkung() + " ") 
				+ ("Grund: " + CoGrundSonderurlaub.getInstance().getBezeichnung(DialogGrundSonderurlaub.getGrundID())));
		
		coBuchungGrund.save();
	}


	/**
	 * Zusätzliche Infos ob Anträge ersetzt oder verlängert werden
	 * 
	 * @return
	 * @throws Exception 
	 */
	private static void saveZusatzinfo(CoBuchung coBuchung) throws Exception {
		String meldung, zusatzinfo;
		Date datum;
		CoBuchung coBuchungBemerkung;
		CoKontowert coKontowert;

		zusatzinfo = "";
		
		// wenn MA in dem Zeitraum bereits Vertreter ist
		if ((meldung = getZusatzinfoVertretung(coBuchung)) != null && !meldung.isEmpty())
		{
			zusatzinfo += meldung + " ";
		}

		
		// wenn Buchungen von den Vortagen verlängert werden 
		if ((meldung = getZusatzinfoZeitraumVorher(coBuchung)) != null)
		{
			zusatzinfo += meldung + " ";
		}
		
		// wenn Buchungen von den nachfolgenden Tagen verlängert werden 
		if ((meldung = getZusatzinfoZeitraumNachher(coBuchung)) != null)
		{
			zusatzinfo += meldung + " ";
		}
		
		// Zeitkonto bei FA
		if (coBuchung.getBuchungsartID() == CoBuchungsart.ID_FA)
		{
			datum = Format.getDateVerschoben(new Date(), -1);
			coKontowert = new CoKontowert();
			coKontowert.load(coBuchung.getPersonID(), datum);

			zusatzinfo += "Arbeitszeitkonto am " + Format.getString(datum) + ": " + Format.getZeitAsText(coKontowert.getWertUeberstundenGesamt());
		}
		
		// Zusatzinfo vorhanden?
		if (zusatzinfo.isEmpty())
		{
			return;
		}
		
		// Buchung laden
		coBuchungBemerkung = new CoBuchung();
		coBuchungBemerkung.loadByID(coBuchung.getID());
		coBuchungBemerkung.begin();
		
		// Bemerkung um Grund Sonderurlaub ersetzen
		coBuchungBemerkung.setBemerkung((coBuchung.getBemerkung() == null ? "" : (coBuchung.getBemerkung() + " ") 
				+ ("Info: " + zusatzinfo)).trim());
		
		coBuchungBemerkung.save();
	}


	/**
	 * Zusätzliche Infos ob Buchungen an den vorherigen Tagen verlängert werden
	 * 
	 * @return
	 * @throws Exception 
	 */
	private static String getZusatzinfoZeitraumVorher(CoBuchung coBuchung) throws Exception {
		int personID;
		String tagesbuchung, aktTagesbuchung;
		Date datum, datumVon, datumBis;
		CoKontowert coKontowert;
		
		personID = coBuchung.getPersonID();
		datum = coBuchung.getDatum();
		datumVon = null;
		datumBis = null;

		coKontowert = new CoKontowert();
		
		
		// Kontowerte durchlaufen
		tagesbuchung = null;
		do
		{
			// Kontowerte für den nächsten tag laden
			datum = Format.getDateVerschoben(datum, -1);
			coKontowert.load(personID, datum);
			if (!coKontowert.moveFirst())
			{
				break;
			}
			
			// wenn es ein Arbeitstag war, prüfe ob schon Buchungen vorhanden sind
			if (coKontowert.getWertSollArbeitszeit() > 0)
			{
				aktTagesbuchung = coKontowert.getBuchungsartTagesbuchung();
				// wenn es keine Tagesbuchung ist oder krank, ist die Prüfung beendet
				if (aktTagesbuchung == null || aktTagesbuchung.equals(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_KRANK))
						|| aktTagesbuchung.equals(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_KRANK_OHNE_LFZ)))
				{
//					if (tagesbuchung != null)
					{
						break;
					}
				}
				else // wenn es eine Tagesbuchung ist
				{
					// wenn es die erste ist, speichere sie
					if (tagesbuchung == null)
					{
						tagesbuchung = aktTagesbuchung;
						datumVon = coKontowert.getDatum();
						datumBis = datumVon;
					}
					else if (tagesbuchung == aktTagesbuchung) // wenn es die gleiche wie vorher ist, Startdatum anpassen
					{
						datumVon = coKontowert.getDatum();
					}
					else // wenn es eine neue ist, beende die Prüfung
					{
						break;
					}
				}
			}
			
		} while (true);
		
		
		return (tagesbuchung == null ? null : tagesbuchung + " (" + Format.getString(datumVon) + " - " + Format.getString(datumBis) + ")");
	}


	/**
	 * Zusätzliche Infos ob Buchungen an den nachfolgenden Tagen verlängert werden
	 * 
	 * @return
	 * @throws Exception 
	 */
	private static String getZusatzinfoZeitraumNachher(CoBuchung coBuchung) throws Exception {
		int personID;
		String tagesbuchung, aktTagesbuchung;
		Date datum, datumVon, datumBis;
		CoKontowert coKontowert;
		
		personID = coBuchung.getPersonID();
		datum = coBuchung.getDatumBis();
		datumVon = null;
		datumBis = null;

		coKontowert = new CoKontowert();
		
		// Kontowerte durchlaufen
		tagesbuchung = null;
		do
		{
			// Kontowerte für den nächsten tag laden
			datum = Format.getDateVerschoben(datum, 1);
			coKontowert.load(personID, datum);
			if (!coKontowert.moveFirst())
			{
				break;
			}
			
			// wenn es ein Arbeitstag war, prüfe ob schon Buchungen vorhanden sind
			if (coKontowert.getWertSollArbeitszeit() > 0)
			{
				aktTagesbuchung = coKontowert.getBuchungsartTagesbuchung();
				// wenn es keine Tagesbuchung ist oder krank, ist die Prüfung beendet
				if (aktTagesbuchung == null || aktTagesbuchung.equals(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_KRANK))
						|| aktTagesbuchung.equals(CoBuchungsart.getInstance().getBezeichnung(CoBuchungsart.ID_KRANK_OHNE_LFZ)))
				{
//					if (tagesbuchung != null)
					{
						break;
					}
				}
				else // wenn es eine Tagesbuchung ist
				{
					// wenn es die erste ist, speichere sie
					if (tagesbuchung == null)
					{
						tagesbuchung = aktTagesbuchung;
						datumVon = coKontowert.getDatum();
						datumBis = datumVon;
					}
					else if (tagesbuchung == aktTagesbuchung) // wenn es die gleiche wie vorher ist, Enddatum anpassen
					{
						datumBis = coKontowert.getDatum();
					}
					else // wenn es eine neue ist, beende die Prüfung
					{
						break;
					}
				}
			}
			
		} while (true);
		
		
		return (tagesbuchung == null ? null : tagesbuchung + " (" + Format.getString(datumVon) + " - " + Format.getString(datumBis) + ")");
	}


	/**
	 * Info, ob die Person irgendwo als Vertretung eingetragen wurde
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String getZusatzinfoVertretung(CoBuchung coBuchung) throws Exception {
		CoVertreter coVertreter;
		coVertreter = new CoVertreter();
		coVertreter.loadForVertreter(coBuchung.getPersonID(), coBuchung.getDatum(), coBuchung.getDatumBis());

		return coVertreter.getMeldungVertretungFuer();
	}


	/**
	 * Alle Vertreter zu den Buchungen der Person in dem übergebenen Zeitraum löschen 
	 * 
	 * @throws Exception
	 */
	private static void deleteVertreter(CoBuchung coBuchung) throws Exception {
		CoVertreter coVertreter;

		// prüfen, ob ein Eintrag ausgewählt ist
//		if (!coBuchung.moveTo(table.getSelectedBookmark()))
//		{
//			return;
//		}
		
		coVertreter = new CoVertreter();
		coVertreter.deleteVertreter(coBuchung.getPersonID(), coBuchung.getDatum(), coBuchung.getDatumBis());
	}

	
	/**
	 * PDF-Urlaubsplanung ausgeben
	 * 
	 * @param diffJahre Differenz in Jahren, wenn nicht für das aktuelle Jahr. 1 => nächstes Jahr
	 */
	public static void clickedUrlaubsplanung(int personID, int diffJahre) {
		GregorianCalendar gregDatum;
		CoAuswertung coAuswertung;
		FormAuswertungUrlaubsplanung formAuswertungUrlaubsplanung;
		
		// Jahr festlegen
		gregDatum = Format.getGregorianCalendar12Uhr(null);
		gregDatum.set(Calendar.YEAR, gregDatum.get(Calendar.YEAR) + diffJahre);

		try
		{
			// Auswertung der Urlaubsplanung nutzen
			formAuswertungUrlaubsplanung = new FormAuswertungUrlaubsplanung(null);
			coAuswertung = formAuswertungUrlaubsplanung.getCoAuswertung();
			
			// Einschränkung nur aktuelle Person
			formAuswertungUrlaubsplanung.resetCombosEinschraenkungPerson();
			formAuswertungUrlaubsplanung.resetCombosEinschraenkungPosition();
			coAuswertung.setPersonID(personID);
			
			// Anfangs- und Enddatum für das Jahr
			gregDatum.set(Calendar.DAY_OF_MONTH, 1);
			gregDatum.set(Calendar.MONTH, Calendar.JANUARY);
			coAuswertung.setDatumVon(Format.getDateValue(gregDatum));
			
			gregDatum.set(Calendar.DAY_OF_MONTH, 31);
			gregDatum.set(Calendar.MONTH, Calendar.DECEMBER);
			coAuswertung.setDatumBis(Format.getDateValue(gregDatum));
			
			// Daten laden und ausgeben
			formAuswertungUrlaubsplanung.checkData();
			formAuswertungUrlaubsplanung.loadCoExtern();
			(new ExportUrlaubsplanungListener(formAuswertungUrlaubsplanung)).activate(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Messages.showErrorMessage("Fehler beim Erstellen der Urlaubsplanung", "Fehler beim Erstellen der Datei.<br>Bitte Administrator kontaktieren.");
		}

	}

	
	/**
	 * Vor dem Laden den Resturlaub aktualisieren
	 */
	@Override
	public void reloadTableData() throws Exception {
		loadResturlaub();
		
		super.reloadTableData();
	}
	
	
	/**
	 * Resturlaub laden und anzeigen 
	 * 
	 * @throws Exception
	 */
	private void loadResturlaub() throws Exception {
		int resturlaub;
		GregorianCalendar gregDatum;
		CoKontowert coKontowert;

		gregDatum = Format.getGregorianCalendar(null);
		gregDatum.set(GregorianCalendar.MONTH, GregorianCalendar.DECEMBER);
		gregDatum.set(GregorianCalendar.DAY_OF_MONTH, 31);
		
		coKontowert = new CoKontowert();
		coKontowert.load(m_personID, Format.getDateValue(gregDatum));
		resturlaub = coKontowert.getResturlaub();

		m_tfResturlaub.getField().setValue(resturlaub);
		m_tfResturlaubGeplant.getField().setValue(CoBuchung.getAnzahlGeplantenUrlaub(m_personID, new Date(), Format.getDateValue(gregDatum)));
	}


	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// Urlaubsplanung ist immer möglich
		m_btUrlaubsplanungAktuell.refresh(reasonEnabled, null);
		m_btUrlaubsplanungNaechstesJahr.refresh(reasonEnabled, null);
	}

	
	@Override
	protected void refreshBtAntraegeAktuell() {
		try 
		{
			m_btVertreterAendern.refresh(reasonDisabled, null);
			m_btBuchungAktuellAendern.refresh(reasonDisabled, null);
			m_btBuchungAktuellLoeschen.refresh(reasonDisabled, null);

			// wenn keine Buchung ausgewählt ist, alle Buttons deaktivieren
			if (!m_coBuchungAktuell.moveTo(m_tableBuchungenAktuell.getSelectedBookmark()))
			{
				return;
			}
			
			// nur abgelehnte Anträge können bearbeitet werden
			if (!m_coBuchungAktuell.isAbgelehnt())
			{
				return;
			}
			
			m_btVertreterAendern.refresh(reasonEnabled, null);
			m_btBuchungAktuellAendern.refresh(reasonEnabled, null);
			m_btBuchungAktuellLoeschen.refresh(reasonEnabled, null);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}



}
