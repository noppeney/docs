package pze.ui.formulare.person;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;

import framework.business.interfaces.FW;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import framework.cui.layout.UniLayout;
import framework.ui.controls.BooleanControl;
import framework.ui.controls.ComboControl;
import framework.ui.controls.TextControl;
import framework.ui.form.UniForm;
import framework.ui.interfaces.controls.IButtonControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.IFocusListener;
import framework.ui.interfaces.selection.ISelectionListener;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.CoZeitmodell;
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.dienstreisen.CoDienstreiseAbrechnung;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoBuchungserfassungsart;
import pze.business.objects.reftables.buchungen.CoBuchungstyp;
import pze.business.objects.reftables.buchungen.CoGrundAenderungBuchung;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;

/**
 * Dialog zum Bearbeiten einer Buchung
 * 
 * @author Lisiecki
 */
public class DialogBuchung extends UniForm {
	
	private final static String RESID_ANSICHT = "dialog.buchung.ansicht";
	public final static String RESID_EINGABE = "dialog.buchung.eingabe";

	protected static CoBuchung m_coBuchung;
	
	
	protected static TextControl m_tfDatum; // static, um nach dem Schließen das Datum abzufragen
	protected TextControl m_tfDatumBis;

	protected DateTime m_kalender;
	protected DateTime m_kalenderBis;

	
	private TextControl m_tfAnzahlTage;
	protected TextControl m_tfUhrzeit;
	protected TextControl m_tfUhrzeitBis;
	
	private ComboControl m_comboPerson;
	private TextControl m_tfChipkartenNr;
	
	private ComboControl m_comboBuchungsart;
//	private ComboControl m_comboBuchungerfassungsart;
//	private ComboControl m_comboBuchungstyp;
//	private ComboControl m_comboZusatzinfo;
	
	private ComboControl m_comboStatus;
//	private ComboControl m_comboStatusGenehmigung;
//	private ComboControl m_comboGeaendertVon;
//	private DateControl m_tfGeaendertAm;
	protected ComboControl m_comboGrundAenderung;
	
	private BooleanControl m_checkIsVorlage;
	
	protected IButtonControl m_btFreigabeAnzeigen;

	
	/**
	 * Dienstreise zum Zwischenspeichern, falls die Eingaben als Vorlage für weitere Buchungen genutzt werden sollen
	 */
	private static int m_dienstreiseIdVorlage;
	
	protected TextControl m_tfBemerkung;

	private IFocusListener m_focusLostListener;
	
	/**
	 * eingegebenes Datum zum Zwischenspeichern
	 */
	private static Date m_datum;
	
	/**
	 * eingegebene Uhrzeit zum Zwischenspeichern
	 */
	private static Integer m_uhrzeit;
	
	/**
	 * eingegebene BuchungsartID zum Zwischenspeichern
	 */
	private static int m_buchungsartID;

	protected static boolean m_nurBemerkungBearbeiten;
	
	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	protected DialogBuchung(String resID) throws Exception {
		super(null, resID);		
		super.createChilds();
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
		// Controls festlegen
		initControls();
		initListener();
		
		m_nurBemerkungBearbeiten = false;
	}

	
	/**
	 * Dialog mit der angegebenen Buchung öffnen
	 * 
	 * @param buchungID
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialogWithBuchung(int buchungID) throws Exception {
		return showDialogWithBuchung(buchungID, false);	
	}

	
	/**
	 * Dialog mit der angegebenen Buchung öffnen
	 * 
	 * @param buchungID
	 * @param nurBemerkungBearbeiten Eine Bemerkung muss eingetragen werden, sonst kann nichts geändert werden
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialogWithBuchung(int buchungID, boolean nurBemerkungBearbeiten) throws Exception {
		DialogBuchung dialog;

		dialog = new DialogBuchung(RESID_ANSICHT);
		dialog.loadBuchung(buchungID);
		
		DialogBuchung.m_nurBemerkungBearbeiten = nurBemerkungBearbeiten;
		
		return showDialog(dialog);			
	}

	
	/**
	 * Dialog für eine neue Buchung öffnen
	 * 
	 * @param personID Person schon auswählen
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialogNewBuchung(int personID) throws Exception {
		DialogBuchung dialog;

		dialog = new DialogBuchung(RESID_EINGABE);
		dialog.loadBuchung(-1, personID);

		return showDialog(dialog);			
	}


	/**
	 * Übergebenen Dialog öffnen
	 * 
	 * @param dialog
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	protected static boolean showDialog(DialogBuchung dialog) throws Exception { // TODO Fehlerbehandlung in co.validate verschieben
		int personID;
//		CoBuchungsart coBuchungsart, coBuchungsartOriginal;
		
		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);
		dialog.getDialog().show();

		// wenn nicht OK geklickt wurde beenden, außer es muss eine Bemerkung eingetragen werden
		if (dialog.getDialog().getRetVal() != FW.OK)
		{
			m_dienstreiseIdVorlage = 0;
			return false;
		}

		// TODO bei Urlaub oder ggf. allgemein prüfen, ob schon ein Antrag vorliegt
		
		// Sicherheitsabfrage bei mehrtägigen Buchungen
		if (dialog.getDatumBis() != null && Format.getDate12Uhr(m_coBuchung.getDatum()).before(Format.getDate12Uhr(dialog.getDatumBis())))
		{
			if (!Messages.showYesNoMessage("Mehrtägige Buchung", "Sie sind dabei eine mehrtägige Buchung zu erstellen"
					+ " (" + Format.getString(m_coBuchung.getDatum()) + " bis " + Format.getString(dialog.getDatumBis()) + "). Möchten Sie fortfahren?"))
			{
				return reshowDialog(dialog);
			}
		}
		
		
		// wenn Daten geändert wurden und kein Grund für die Bearbeitung angegeben wurde, öffne Dialog erneut
		if (m_coBuchung.isModified())
		{
			personID = m_coBuchung.getPersonID();
//			coBuchungsart = m_coBuchung.getCoBuchungsart();
//			coBuchungsartOriginal = m_coBuchung.getCoBuchungsartOriginal();
			
			// Vollständigkeit der Eingabe prüfen
			if (m_coBuchung.getDatum() == null)
			{
				return showErrorMessage("kein Datum", dialog);
			}
			else if (personID == 0)
			{
				return showErrorMessage("keine Person", dialog);
			}
			else if (m_coBuchung.getBuchungsartID() == 0)
			{
				return showErrorMessage("keine Buchungsart", dialog);
			}
			else if (m_coBuchung.getStatusID() == 0)
			{
				return showErrorMessage("kein Status der Buchung", dialog);
			}
			else if (m_coBuchung.getGrundAenderungID() == 0)
			{
				return showErrorMessage("kein Grund für die Bearbeitung", dialog);
			}
			
			// Buchungsart prüfen, da einige Buchungen (Abwesenheitbuchungen) nur von der Personalverwaltung gemacht werden dürfen
//			else if (coBuchungsart.isBuchungPersonalverwaltung() && !UserInformation.getInstance().isPersonalverwaltung()
//					&& !m_coBuchung.isSelbstbuchungAenderungZulaessig()) // teilweise können Buchungen auch selbst durchgeführt werden
//			{
//				return showErrorMessage("eine Buchungsart, zu deren Auswahl Sie nicht berechtigt sind,", dialog);
//			}
			
			// Buchungsart prüfen, da einige Buchungen nicht geändert werden dürfen
//			else if (coBuchungsartOriginal != null && coBuchungsartOriginal.isBuchungPersonalverwaltung() && !UserInformation.getInstance().isPersonalverwaltung()
//					&& !m_coBuchung.isSelbstbuchungAenderungZulaessig()) // teilweise können Buchungen auch selbst durchgeführt werden
//			{
//				return showErrorMessage("für eine Buchungsart, zu deren Änderung Sie nicht berechtigt sind, eine Änderung", dialog);
//			}
			
			// eine Buchung darf nicht auf Dienstreise geändert werden
//			else if (!dialog.isVorlage() && !checkAenderungDienstreise()) 
//			{
//				return showErrorMessage("für eine gespeicherte Buchung eine Dienstreise (nur als neue Buchung zulässig) ", dialog);
//			}
			
			// eine vorläufige Buchung zu einem Antrag darf nicht auf OK geändert werden
			else if (m_coBuchung.isGueltig() && Format.getIntValue(m_coBuchung.getFieldStatusID().getOriginalValue()) == CoStatusBuchung.STATUSID_VORLAEUFIG
					&& m_coBuchung.getStatusGenehmigungID() != 0)
			{
				Messages.showErrorMessage("Fehler beim Speichern", "Der Status einer Buchung zu einem Antrag kann nicht geändert werden. "
						+ "Die Änderungen wurden nicht gespeichert.");
				return false;
			}
			
			// FA darf nur 2 Monate im Vorraus gebucht werden 
			else if (m_coBuchung.getBuchungsartID() == CoBuchungsart.ID_FA)
			{
				if (Format.getDate0Uhr(m_coBuchung.getDatum()).after(Format.getDateVerschobenMonate(Format.getDate0Uhr(new Date()), 2)))
				{
					return showErrorMessage("für eine FA-Buchung ein Datum mehr als 2 Monate im Voraus", dialog);
				}
			}

			// OFA prüfen
			else if (m_coBuchung.getBuchungsartID() == CoBuchungsart.ID_ORTSFLEX_ARBEITEN)
			{
				// Uhrzeit muss angegeben werden
				if (m_coBuchung.getUhrzeitAsInt() == 0)
				{
					return showErrorMessage("eine OFA-Buchung ohne Uhrzeit", dialog);
				}
				
				// OFA darf nur 2 Wochen im Vorraus gebucht werden 2 oder 4???
				if (Format.getDate0Uhr(m_coBuchung.getDatum()).after(Format.getDateVerschobenWochen(Format.getDate0Uhr(new Date()), 4))
						|| (dialog.getDatumBis() != null 
						&& Format.getDate0Uhr(dialog.getDatumBis()).after(Format.getDateVerschobenWochen(Format.getDate0Uhr(new Date()), 4))))
				{
					return showErrorMessage("für eine OFA-Buchung ein Datum mehr als 4 Wochen im Voraus", dialog);
				}
			}

			// Bemerkung ggf. Pflicht
			if (m_nurBemerkungBearbeiten)
			{
				if (m_coBuchung.getBemerkung() == null || m_coBuchung.getBemerkung().trim().isEmpty())
				{
					Messages.showErrorMessage("Fehler beim Speichern", "Es wurde kein Grund für die Ablehnung des Antrags angegeben. "
							+ "Die Ablehnung wurde nicht gespeichert.");
					return false;
				}
				else
				{
					// Bemerkung mit Kürzel des Bearbeiters versehen
					CoPerson.getInstance().moveToID(UserInformation.getPersonID());
					m_coBuchung.setBemerkung(CoPerson.getInstance().getKuerzel() + ": " + m_coBuchung.getBemerkung());
				}
			}
			
			// Sicherheitsabfrage Buchungsart bei Selbstbuchung
//			else if (m_coBuchung.isSelbstbuchung())
//			{
//				if (!CoStatusBuchung.isSelbstbuchungAenderungZulaessig(m_coBuchung.getStatusID()))
//				{
//					return showErrorMessage("ein Status, zu deren Auswahl Sie nicht berechtigt sind,", dialog);
//				}
//			}

			// speichern
			dialog.save();

			if (!m_nurBemerkungBearbeiten)
			{
				FormPerson.open(dialog.getSession(), null, personID).showZeiterfassung(m_coBuchung.getDatum());
			}

			// wenn die Buchung als Vorlage weiterverwendet werden soll, öffne sie erneut
			if (dialog.isVorlage())
			{
				dialog.setDatum(m_datum);
				if (m_uhrzeit != null)
				{
					dialog.setUhrzeit(m_uhrzeit);
				}
				dialog.setBuchungsartID(m_buchungsartID);
				dialog.setIsVorlage(true);
				m_coBuchung.setID(m_coBuchung.nextID());
				m_coBuchung.getCurrentRow().setRowState(IBusinessObject.statusAdded);
				return reshowDialog(dialog);
			}
			
			return true;
		}
		
		// Bemerkung ggf. Pflicht
		if (m_nurBemerkungBearbeiten)
		{
			// hierhin kommt man nur, wenn die Buchung nicht bearbeitet wurde
			
			// Prüfung ob eine Bemerkung einegtragen wurde oder nicht
//			if (m_coBuchung.getBemerkung() == null || m_coBuchung.getBemerkung().trim().isEmpty())
			{
				Messages.showErrorMessage("Fehler beim Speichern", "Es wurde kein Grund für die Ablehnung des Antrags angegeben. "
						+ "Die Ablehnung wurde nicht gespeichert.");
				return false;
			}
			
//			return true;
		}
		
		return false;
	}


	/**
	 * Prüfung, dass keine Dienstreise in eine andere Buchungsart geändert wurde
	 * 
	 * @return Änderungen zulässig (bzw. keine gemacht) oder nicht
	 */
//	private static boolean checkAenderungDienstreise() throws Exception { //  nur vorläufige sollten nicht geändert werden, z. B. am terminal falsch gebuchte Änderungen schon
//		int buchungsartID, rowState;
//		IField field;
//		Object originalValue;
//
//		buchungsartID = m_coBuchung.getBuchungsartID();
//		field = m_coBuchung.getFieldBuchungsartID();
//		originalValue = field.getOriginalValue();
//		rowState = m_coBuchung.getCurrentRow().getRowState();
//			
//		// keine DR, neue Buchung, vorher keine Buchungsart angegeben oder nicht geändert
//		return CoBuchungsart.isDrDg(buchungsartID) || rowState == IBusinessObject.statusAdded 
//				|| originalValue == null || field.getValue().equals(originalValue);
//	}


	/**
	 * Fehlermeldung, wenn die Einträge nicht vollständig sind
	 * 
	 * @param bezeichnung z. B. "keine Person"
	 * @param dialog 
	 * @throws Exception 
	 */
	private static boolean showErrorMessage(String bezeichnung, DialogBuchung dialog) throws Exception {
		
		Messages.showErrorMessage("Fehler beim Speichern", "Es wurde " + bezeichnung + " angegeben. Die Änderungen wurden nicht gespeichert.");
		
		return reshowDialog(dialog);
	}

	
	/**
	 * Dialog mit den gleichen Daten erneut öffnen
	 * 
	 * @param dialog
	 * @return
	 * @throws Exception
	 */
	private static boolean reshowDialog(DialogBuchung dialog) throws Exception {
		int uhrzeitBis;
		boolean isVorlage;
		Date datumBis;
		
		
		// virtuelle Felder zwischenspeichern
		datumBis = dialog.getDatumBis();
		uhrzeitBis = dialog.getUhrzeitBis();
		isVorlage = dialog.isVorlage();
		
		// neuen Dialog erstellen
		dialog = new DialogBuchung(dialog.getResID());
		dialog.setData(m_coBuchung);
		
		// virtuelle Felder wieder setzen
		dialog.setDatumBis(datumBis);
		if (uhrzeitBis > 0)
		{
			dialog.setUhrzeitBis(uhrzeitBis);
		}

		// Kalender aktualisieren
		dialog.checkDatumKalener(m_coBuchung.getDatum(), dialog.getKalender());
		dialog.checkDatumKalener(datumBis, dialog.getKalenderBis());

		dialog.setIsVorlage(isVorlage);
		
		return showDialog(dialog);
	}


	/**
	 * Buchung speichern.<br>
	 * Beim Erstellen einer neuen Buchung werden ggf. mehrere Buchungen erzeugt.
	 * 
	 * @param dialog
	 * @return 
	 * @throws Exception
	 */
	protected int save() throws Exception {
		int buchungsartTagesendeID, dienstreiseID;
		boolean isNeu;
		Integer uhrzeitBis;
		Date datum, datumBis;
		CoPerson coPerson;

		// die eigenen Daten dürfen nicht geändert werden, ist mittlerweile möglich
//		if (m_coBuchung.getPersonID() == CoPerson.getInstance().getIdByUserID(UserInformation.getInstance().getUserID()))
//		{
//			Messages.showErrorMessage("Speichern nicht möglich.", "Sie dürfen nur Buchungen für andere Personen speichern.");
//			return;
//		}
		
		coPerson = new CoPerson();
		coPerson.loadByID(m_coBuchung.getPersonID());

		datum = Format.getDate12Uhr(m_coBuchung.getDatum());
		m_datum = datum;
		datumBis = Format.getDate12Uhr(getDatumBis());
		
		// 0 Uhr wird nicht eingetragen
		m_uhrzeit = m_coBuchung.getUhrzeitAsInt();
		if (m_uhrzeit == 0)
		{
			m_uhrzeit = null;
			m_coBuchung.setUhrzeitAsInt(m_uhrzeit);
		}
		
		uhrzeitBis = getUhrzeitBis();
		if (uhrzeitBis == 0)
		{
			uhrzeitBis = null;
		}
	
		dienstreiseID = 0;
		m_buchungsartID = m_coBuchung.getBuchungsartID();
		buchungsartTagesendeID = m_coBuchung.getCoBuchungsart().getBuchungsartTagesendeID(uhrzeitBis);

		// bei Urlaub werden keine Zeiten gespeichert
		if (m_buchungsartID == CoBuchungsart.ID_URLAUB || m_buchungsartID == CoBuchungsart.ID_SONDERURLAUB)
		{
			m_uhrzeit = null;
			m_coBuchung.setUhrzeitAsInt(m_uhrzeit);
			uhrzeitBis = null;
		}
		
		// Sonderfälle FA
		if (m_buchungsartID == CoBuchungsart.ID_FA)
		{
			// bei zeitw. FA bis ohne Anfangszeit wird der Beginn der Rahmenarbeitszeit angegeben
			if (m_uhrzeit == null && uhrzeitBis != null)
			{
				m_uhrzeit = CoFirmenparameter.getInstance().getRahmenarbeitszeitBeginn(m_datum);
				m_coBuchung.setUhrzeitAsInt(m_uhrzeit);
			}

			// TODO Sonderfall FA am OFA-Tag
			// an einem OFA-Tag wird die Endbuchung für die neue Buchung auch OFA statt Kommen
//			if (m_uhrzeit != null && uhrzeitBis != null && CoBuchung.isOfa(m_coBuchung.getPersonID(), datum, m_uhrzeit, uhrzeitBis))
//			{
//				buchungsartTagesendeID = CoBuchungsart.ID_ORTSFLEX_ARBEITEN;
//			}
			// Unterscheidung OFA bereits genehmigt, dann neue OFA auch genehmigt? oder 2. Antrag? GgF. Select-Statements in Tabellen anpassen
			// wenn noch nicht genehmigt, sollte ein 2. OFA-Antrag daraus werden -> es müssen 2 genehmigt werden?
			// nochmal drüber nachdenken, ggf. mit Sven Ablauf besprechen
		}

		// Buchung speichern, vorher Daten speichern, weil z. B. Tagesendbuchung gemacht werden kann
		isNeu = m_coBuchung.isNew();
		if (!m_coBuchung.isEditing())
		{
			m_coBuchung.begin();
		}
		
		// Buchungen für dich selbst dürfen nicht in der Vergangenheit gemacht werden
		if (!m_coBuchung.validateSelbstbuchung())
		{
			return 0;
		}
		
		// an arbeitsfreien Tagen keine Tagesbuchung speichern
		if (checkBuchungSpeichern(datum, coPerson))
		{
			// ggf. Einträge für Dienstreiseantrag und Abrechnung erzeugen
			dienstreiseID = createDienstreise(isNeu, m_dienstreiseIdVorlage);
			if (dienstreiseID > 0)
			{
				m_coBuchung.setDienstreiseID(dienstreiseID);
			}

			// Buchung speichern
			m_coBuchung.save();
			
		}

		// wenn ein Uhrzeit-Ende angegeben, wird dafür eine Tagesende-Buchung erzeugt
		saveTagesendbuchung(uhrzeitBis, buchungsartTagesendeID);

		
		// mehrtägige Buchung
		if (datumBis != null)
		{
			// Buchung ggf. für mehrere Tage speichern
			while (datum.before(datumBis))
			{
				// Datum für den nächsten Tag
				datum = Format.getDateVerschoben(datum, 1);

				initNextBuchung();
				
				m_coBuchung.setDatum(datum);
				m_coBuchung.setUhrzeitAsInt(m_uhrzeit);
				m_coBuchung.setBuchungsartID(m_buchungsartID);

				// kein Urlaubstag an arbeitsfreien Tagen speichern
				if (!checkBuchungSpeichern(datum, coPerson))
				{
					continue;
				}

				// ggf. Einträge für Dienstreiseantrag und Abrechnung erzeugen
				if (dienstreiseID > 0)
				{
					m_coBuchung.setDienstreiseID(dienstreiseID);
				}

				m_coBuchung.save();

				// wenn ein Uhrzeit-Ende angegeben, wird dafür eine Gehen-Buchung erzeugt
				saveTagesendbuchung(uhrzeitBis, buchungsartTagesendeID);
			}
		}

		// DR-Formular anzeigen, erst hier weil Buchungen gespeichert sein müssen
		if (dienstreiseID > 0)
		{
			DialogDienstreise.showDialog(dienstreiseID);
		}

		if (isVorlage())
		{
			m_dienstreiseIdVorlage = dienstreiseID;
		}
		else
		{
			m_dienstreiseIdVorlage = 0;
		}

		return getAnzahlTage();
	}


	/**
	 * An arbeitsfreien Tagen keine Tagesbuchung speichern (Urlaub, Krank...) und keine Vorlesung, außer für Aushilfen <br>
	 * Alles andere darf gebucht werden, falls doch gearbeitet wird.<br>
	 * Alle Buchungen dürfen aber auf ungültig gesetzt werden.
	 * 
	 * @param datum
	 * @param coPerson
	 * @return
	 * @throws Exception
	 */
	private static boolean checkBuchungSpeichern(Date datum, CoPerson coPerson) throws Exception {
		return coPerson.isArbeitstag(datum) || coPerson.getCoZeitmodell(datum).getID() == CoZeitmodell.ID_AUSHILFE
				|| (!m_coBuchung.getCoBuchungsart().isTagesbuchungPflicht() && m_coBuchung.getBuchungsartID() != CoBuchungsart.ID_VORLESUNG
						&& m_coBuchung.getBuchungsartID() != CoBuchungsart.ID_KRANK && m_coBuchung.getBuchungsartID() != CoBuchungsart.ID_FA)
				|| m_coBuchung.getStatusID() == CoStatusBuchung.STATUSID_UNGUELTIG;
	}


	/**
	 * ggf. Einträge für Dienstreiseantrag und Abrechnung erzeugen
	 * 
	 * @param isNeu Buchung wurde neu erstellt, sonst wird keine Dienstreise erzeugt
	 * @param dienstreiseID ID der Dienstreise, die als Vorlage dienen soll (bei mehrtägigen Dienstreisen)
	 * @param dialogAnzeigen Dialog anzeigen oder Daten direkt speichern
	 * @return 
	 * @throws Exception
	 */
	private static int createDienstreise(boolean isNeu, int dienstreiseID) throws Exception {
		CoDienstreise coDienstreise, coDienstreiseVorlage;
		CoDienstreiseAbrechnung coDienstreiseAbrechnung;
	
		// wenn es keine neue Dienstreise (vorläufige Buchung für den Antrag) ist muss nichts gemacht werden
		if (!m_coBuchung.isVorlaeufig() || !CoBuchungsart.isDrDg(m_buchungsartID) || !isNeu)
		{
			return 0;
		}

		coDienstreise = new CoDienstreise();
		coDienstreise.createNew(m_coBuchung.getPersonID()); // auch erzeugen wenn später von Vorlage kopiert wird, da copyRow in bestehende Row kopiert

		// wenn keine Vorlage für die Dienstreise übergeben wurde wird eine neue erstellt, sonst die alte kopieren
		if (dienstreiseID == 0) // neue, leere Dienstreise
		{
//			DialogDienstreise.showDialog(coDienstreise);
		}
		else
		{
			// Vorlage laden
			coDienstreiseVorlage = new CoDienstreise();
			coDienstreiseVorlage.loadByID(dienstreiseID);
			
			// in neue Dienstreise übertragen und neue IDs anpassen
			coDienstreiseVorlage.copyRow(coDienstreise);
			coDienstreise.setID(coDienstreise.nextID());
			coDienstreise.save();
			
			// ggf. Dialog zum Bearbeiten anzeigen (z. B. wenn Buchung als Vorlage verwendet wurde)
//			DialogDienstreise.showDialog(coDienstreise);
		}

		
		// Eintrag für die Abrechnung der Dienstreise erstellen
		coDienstreiseAbrechnung = new CoDienstreiseAbrechnung();
		coDienstreiseAbrechnung.createNew(coDienstreise.getID());

		return coDienstreise.getID();
	}


	/**
	 * Tagesendbuchung zur aktuellen Buchung erstellen
	 * 
	 * @param uhrzeitBis
	 * @param buchungsartEndeID
	 * @throws Exception
	 */
	private static void saveTagesendbuchung(Integer uhrzeitBis, int buchungsartEndeID) throws Exception {
		if (uhrzeitBis != null && uhrzeitBis > 0 && buchungsartEndeID > 0)
		{
			initNextBuchung();
			
			m_coBuchung.setUhrzeitAsInt(uhrzeitBis);
			m_coBuchung.setBuchungsartID(buchungsartEndeID);
			m_coBuchung.getFieldDienstreiseID().setValue(null);
			
			m_coBuchung.save();
		}
	}


	/**
	 * m_coBuchung zum Erstellen der nächsten Buchung initialisieren
	 * 
	 * @throws Exception
	 */
	private static void initNextBuchung() throws Exception {
		if (!m_coBuchung.isEditing())
		{
			m_coBuchung.begin();
		}
		m_coBuchung.getCurrentRow().setRowState(IBusinessObject.statusAdded);
		m_coBuchung.setID(m_coBuchung.nextID());
		// StatusGenehmigung sollte null oder geplant sein, dann sollte auch das Ende diesen Status bekommen
//		m_coBuchung.getFieldStatusGenehmigungID().setValue(null);
	}


	/**
	 * Controls initialisieren
	 * @throws Exception 
	 */
	protected void initControls() throws Exception {

		m_tfDatum = (TextControl) findControl(getResID() + ".datum");
		m_tfDatumBis = (TextControl) findControl(getResID() + ".datumbis");
		m_tfAnzahlTage = (TextControl) findControl(getResID() + ".anzahltage");
		
		m_tfUhrzeit = (TextControl) findControl(getResID() + ".uhrzeitasint");
		m_tfUhrzeitBis = (TextControl) findControl(getResID() + ".uhrzeitbis");
		
		m_comboPerson = (ComboControl) findControl(getResID() + ".personid");
		m_tfChipkartenNr = (TextControl) findControl(getResID() + ".chipkartennr");
		
		m_comboBuchungsart = (ComboControl) findControl(getResID() + ".buchungsartid");
//		m_comboBuchungerfassungsart = (ComboControl) findControl(getResID() + ".buchungserfassungsartid");
//		m_comboBuchungstyp = (ComboControl) findControl(getResID() + ".buchungstypid");
//		m_comboZusatzinfo = (ComboControl) findControl(getResID() + ".systembuchungsmeldungid");
		
		m_comboStatus = (ComboControl) findControl(getResID() + ".statusid");
//		m_comboStatusGenehmigung = (ComboControl) findControl(getResID() + ".statusgenehmigungid");
//		m_comboGeaendertVon = (ComboControl) findControl(getResID() + ".geaendertvonid");
//		m_tfGeaendertAm = (DateControl) findControl("dialog.buchung.geaendertam");
		m_comboGrundAenderung = (ComboControl) findControl(getResID() + ".grundaenderungid");
		
		m_checkIsVorlage = (BooleanControl) findControl(getResID() + ".isvorlage");
		
		m_tfBemerkung = (TextControl) findControl(getResID() + ".bemerkung");
		
		initKalender();
		initKalenderBis();
//		m_kalender.setEnabled(false);
		
		initBtFreigabeAnzeigen();
	}


	/**
	 * Kalender zur Datumsauswahl
	 */
	private void initKalender() {
		if (m_tfDatum == null)
		{
			return;
		}
		
		m_kalender = new DateTime ((Composite) findControl("group." + getResID() + ".datum").getComponent(), SWT.CALENDAR);
		m_kalender.setBackground(new Color(Display.getCurrent(), new RGB(207, 235, 249)));
		m_kalender.setLocation(m_tfDatumBis == null ? 135 : 5, 30);
		m_kalender.setSize(185, 150);
		m_kalender.pack();
		m_kalender.setBackground(new Color(Display.getCurrent(), new RGB(207, 235, 249)));
		
		// Test, ob es so funktioniert, wenn zuerst der heutige Tag ausgewählt wird
		// beim SelectionListener wird nur ein geänderter Tag erkannt
		m_kalender.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
				Date datum = new Date();
				String tmpDatum =  m_kalender.getDay () + "." + (m_kalender.getMonth () + 1) + "." + m_kalender.getYear ();
				SimpleDateFormat f = new SimpleDateFormat("d.MM.yyyy");
				
				try 
				{
					datum = f.parse(tmpDatum);
					setDatum(Format.getDate12Uhr(datum));
					m_tfDatum.refresh(reasonDataChanged, null);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});

		// evtl. unnötig wegen MouseListener
//		m_kalender.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent arg0) {
////				System.out.println ("Calendar date selected (DD.MM.YYYY) = " + (calendar.getMonth () + 1) + "/" + calendar.getDay () + "/" + calendar.getYear ());				
//			}
//
//			@Override
//			public void widgetSelected(SelectionEvent arg0) {
//				Date datum = new Date();
//				String tmpDatum =  m_kalender.getDay () + "." + (m_kalender.getMonth () + 1) + "." + m_kalender.getYear ();
//				SimpleDateFormat f = new SimpleDateFormat("d.MM.yyyy");
//				
//				try 
//				{
//					datum = f.parse(tmpDatum);
//					setDatum(Format.getDate12Uhr(datum));
//					m_tfDatum.refresh(reasonDataChanged, null);
//				} 
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
	}


	/**
	 * Kalender zur Datumsauswahl
	 */
	private void initKalenderBis() {
		if (m_tfDatumBis == null)
		{
			return;
		}

		m_kalenderBis = new DateTime ((Composite) findControl("group." + getResID() + ".datum").getComponent(), SWT.CALENDAR);
		m_kalenderBis.setBackground(new Color(Display.getCurrent(), new RGB(207, 235, 249)));
		m_kalenderBis.setLocation(190, 30);
		m_kalenderBis.setSize(185, 150);
		m_kalenderBis.pack();
		m_kalenderBis.setBackground(new Color(Display.getCurrent(), new RGB(207, 235, 249)));
		
		m_kalenderBis.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
//				System.out.println ("Calendar date selected (DD.MM.YYYY) = " + (calendar.getMonth () + 1) + "/" + calendar.getDay () + "/" + calendar.getYear ());				
			}

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				Date datum = new Date();
				String tmpDatum =  m_kalenderBis.getDay () + "." + (m_kalenderBis.getMonth () + 1) + "." + m_kalenderBis.getYear ();
				SimpleDateFormat f = new SimpleDateFormat("d.MM.yyyy");
				
				try 
				{
					datum = f.parse(tmpDatum);
					setDatumBis(Format.getDate12Uhr(datum));
					m_tfDatumBis.refresh(reasonDataChanged, null);
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Datum des Haupt-Kalenders prüfen und ggf. aktualisieren
	 */
	protected void checkDatumKalener() {
		checkDatumKalener(getDatum(), m_kalender);
	}
	

	/**
	 * Datum des Kalenders prüfen und ggf. aktualisieren
	 * @param kalender 
	 * @param datum 
	 */
	protected void checkDatumKalener(Date datum, DateTime kalender) {
		int jahr, monat, tag;
		Date datumBis;
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(datum);
		jahr = gregDatum.get(Calendar.YEAR);
		monat = gregDatum.get(Calendar.MONTH);
		tag = gregDatum.get(Calendar.DAY_OF_MONTH);
		
		// Kalender kann disposed sein, wenn Buchung als Vorlage weiter genutzt wird
		if (kalender != null && !kalender.isDisposed())
		{
			// Kalender bei Abweichungen anpassen
			if (kalender.getYear() != jahr || kalender.getMonth() != monat || kalender.getDay() != tag)
			{
				kalender.setDate(jahr, monat, tag);
			}
			
			// wenn Datum gesetzt wird und DatumBis ist früher, passe auch DatumBis auf Datum an
			datumBis = getDatumBis();
			if (kalender.equals(getKalender()) && datum != null && (datumBis == null || datum.after(getDatumBis())))
			{
				setDatumBis(datum);
				
				if (m_tfDatumBis != null)
				{
					m_tfDatumBis.refresh(reasonDataChanged, null);
				}
			}
		}
	}


	private void initBtFreigabeAnzeigen() {
		m_btFreigabeAnzeigen = (IButtonControl) findControl("dialog.buchung.ansicht.genehmigunganzeigen");
		if (m_btFreigabeAnzeigen != null)
		{
			m_btFreigabeAnzeigen.setSelectionListener(new ISelectionListener() {

				@Override
				public void selected(IControl control, Object params) {
					try 
					{
						DialogFreigabe.showDialog(m_coBuchung.getID());
//						CoFreigabe coFreigabe;
//						
//						coFreigabe = new CoFreigabe();
//						coFreigabe.load(m_coBuchung.getID());
//						Messages.showInfoMessage(coFreigabe.toString());
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


	/**
	 * Listener hinzufügen
	 */
	private void initListener() {
		IValueChangeListener m_valueChangeListener;
		
		// Kalender bei Datumsänderung anpassen
		m_tfDatum.setValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				setDatum(getDatum());
			}
		});

		// Kalender bei Datumsänderung anpassen
		if (m_tfDatumBis != null)
		{
			m_tfDatumBis.setValueChangeListener(new IValueChangeListener() {

				@Override
				public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
					setDatumBis(getDatumBis());
				}
			});
		}

		
		// ValueChangeListener, um Änderungen zu protokollieren
		m_valueChangeListener = new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				DialogBuchung.this.valueChanged();
			}
		};
		
		m_focusLostListener = new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				try 
				{
					DialogBuchung.this.valueChanged();
				} 
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			@Override
			public void focusGained(IControl control) {
				
			}
		};
		
		setFocusListener(m_tfDatum);
		setFocusListener(m_tfDatumBis);
		setFocusListener(m_tfUhrzeit);
		setFocusListener(m_tfChipkartenNr);
		setFocusListener(m_tfBemerkung);

		m_comboStatus.setValueChangeListener(m_valueChangeListener);
//		m_comboStatusGenehmigung.setValueChangeListener(m_valueChangeListener);
		m_comboGrundAenderung.setValueChangeListener(m_valueChangeListener);
		
		
		// bei einer neuen Buchungsart kann ggf. schon der Grund der Änderung eingetragen werden
		m_comboBuchungsart.setValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				int buchungsartID;

				buchungsartID = getBuchungsartID();

				switch (buchungsartID)
				{
				case CoBuchungsart.ID_URLAUB:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_URLAUBSPLANUNG);
					break;

				case CoBuchungsart.ID_SONDERURLAUB:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_URLAUBSPLANUNG);
					break;

				case CoBuchungsart.ID_FA:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_FA_ANTRAG);
					break;

				case CoBuchungsart.ID_DIENSTREISE:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_DIENSTREISEANTRAG);
					break;

				case CoBuchungsart.ID_DIENSTGANG:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_DIENSTGANGANTRAG);
					break;

				case CoBuchungsart.ID_ORTSFLEX_ARBEITEN:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_OFA_ANTRAG);
					break;

				case CoBuchungsart.ID_KRANK:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_KRANKMELDUNG);
					break;

				case CoBuchungsart.ID_KRANK_OHNE_LFZ:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_KRANKMELDUNG);
					break;

				case CoBuchungsart.ID_KGG:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_KGG);
					break;

				case CoBuchungsart.ID_VORLESUNG:
					m_coBuchung.setGrundAenderungID(CoGrundAenderungBuchung.ID_WOCHENPLAN);
					break;

				default:
					DialogBuchung.this.valueChanged();
					break;
				}

				refresh(reasonDataChanged, null);
			}
		});
	}


	/**
	 * FocusListener auf ein Control setzen, falls es existiert (es gibt verschiedene Ansichten für das Formular)
	 * 
	 * @param control
	 */
	private void setFocusListener(TextControl control) {
		if (control != null)
		{
			control.setFocusListener(m_focusLostListener);
		}
	}


	/**
	 * Buchung laden
	 * 
	 * @param id ID der Buchung
	 * @throws Exception
	 */
	private void loadBuchung(int id) throws Exception {
		loadBuchung(id, 0);
	}


	/**
	 * Buchung laden
	 * 
	 * @param id ID der Buchung
	 * @param id personID PersonID, falls eine neue Buchung erstellt wird
	 * @throws Exception
	 */
	protected void loadBuchung(int id, int personID) throws Exception {
		
		m_coBuchung = new CoBuchung();
		if (id > 0)
		{
			m_coBuchung.loadByID(id);
		}
		m_coBuchung.begin();

		// wenn noch keine Buchung existiert, lege eine neue an
		if (m_coBuchung.getRowCount() == 0)
		{
			m_coBuchung.createNew();
			
			// Person per Default auswählen
			if (personID > 0)
			{
				m_coBuchung.setPersonID(personID);
			}

			// es werden nur Zeitbuchungen am PC gemacht
			m_coBuchung.setBuchungserfassungsartID(CoBuchungserfassungsart.ID_PC);
			m_coBuchung.setBuchungsTyp(CoBuchungstyp.ZEITBUCHUNG);
		}
		
		// Items für comboBuchungsart und comboPersonen anpassen an Berechtigung
		setItemsBuchungsart();
		setItemsPerson();
		setItemsStatus();
		
		setData(m_coBuchung);
		checkDatumKalener(getDatum(), m_kalender);
	}


	/**
	 * Items der Combo Buchungsart anpassen.<br>
	 * Nur die Buchungsarten laden, zu denen man eine Berechtigung hat (Personalverwaltung darf mehr als Sekretariat).
	 * 
	 * @throws Exception
	 */
	private void setItemsBuchungsart() throws Exception {
		CoBuchungsart coBuchungsart = new CoBuchungsart();
		
		coBuchungsart.loadAllWithBerechtigung(m_coBuchung.getBuchungsartID(), m_coBuchung.isSelbstbuchung());
		m_coBuchung.getFieldBuchungsartID().setItems(coBuchungsart);
	}


	/**
	 * Items der Combo Person anpassen.<br>
	 * Nur die Personen laden, zu denen man eine Berechtigung hat
	 * 
	 * @throws Exception
	 */
	private void setItemsPerson() throws Exception {
		CoPerson coPerson;
		
		// muss nur für Sekretariat angepasst werden, da alle anderen keinen Zugriff auf das Feld oder Zugriff auf alle Positionen haben
		if (UserInformation.getInstance().isSekretariat() && !UserInformation.getInstance().isPersonalverwaltung())
		{
			coPerson = new CoPerson();
			coPerson.loadItemsOfCurrentUser();
			m_coBuchung.getFieldPersonID().setItems(coPerson);
		}
	}


	/**
	 * Items der Combo Status anpassen.<br>
	 * Nur die Stati laden, zu denen man eine Berechtigung hat
	 * 
	 * @throws Exception
	 */
	private void setItemsStatus() throws Exception {
		CoStatusBuchung coStatusBuchung;
		
		coStatusBuchung = new CoStatusBuchung();

		// bei Selbstbuchungen nur die zulässigen Stati laden
		if (m_coBuchung.isSelbstbuchung())
		{
			coStatusBuchung.loadSelbstbuchungAenderungZulaessig(m_coBuchung.getStatusID());
		}
		else
		{
			coStatusBuchung.loadAll();
		}
		
		m_coBuchung.getFieldStatusID().setItems(coStatusBuchung);

		// wenn nur ein Status möglich ist, wähle diesen aus
		if (coStatusBuchung.getRowCount() == 1)
		{
			m_coBuchung.setStatusID(coStatusBuchung.getID());
		}
	}

	
	/**
	 * Bei geänderten Werten speichern, wer wann die Änderungen gemacht hat
	 * 
	 * @throws Exception
	 */
	private void valueChanged() throws Exception {
		int statusID;
		
		if (!m_coBuchung.isModified())
		{
			return;
		}

		// Speichern von wem die Änderungen gemacht wurden
		m_coBuchung.setGeaendertVonID(UserInformation.getPersonID());
		m_coBuchung.setGeaendertAm(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
		
		statusID = m_coBuchung.getStatusID();
		if (!m_coBuchung.isNew() && statusID == CoStatusBuchung.STATUSID_OK)
		{
			m_coBuchung.setStatusID(CoStatusBuchung.STATUSID_GEAENDERT);
		}
		
		// Anzahl Tage aktualisieren
		berechneAnzahlTage();
		
		// ChipkartenNr darf nicht geändert werden
		checkChipkartenNr();
		
		refresh(reasonDataChanged, null);
	}


	/**
	 * Anzahl Arbeitstage berechnen und anzeigen
	 * 
	 * @throws Exception 
	 */
	protected void berechneAnzahlTage() throws Exception {
		int anzTage;
		Date datum;
		Date datumBis;
		CoPerson coPerson;
		
		// Anzahl Tage berechnen
		anzTage = 0;
		datum = m_coBuchung.getDatum();
		datumBis = getDatumBis();
		coPerson = new CoPerson();
		coPerson.loadByID(m_coBuchung.getPersonID());
		
		if (datum != null && datumBis != null)
		{
			do
			{
				if (coPerson.isArbeitstag(datum))
				{
					++anzTage;
				}
				datum = Format.getDateVerschoben(datum, 1);
			} while (!datum.after(datumBis));
		}
		
		if (anzTage > 0)
		{
			setAnzahlTage(anzTage);
		}
		else
		{
			setAnzahlTage(null);
		}
	}


	/**
	 * prüfen, ob die ChipkartenNr geändert wurde und ggf. zurücksetzen, da keine Änderung zulässig ist
	 */
	private void checkChipkartenNr() {
		
		if (m_tfChipkartenNr == null)
		{
			return;
		}
		
		if (m_tfChipkartenNr.getField().getOriginalValue() != null 
				&& !m_tfChipkartenNr.getField().getOriginalValue().equals(m_tfChipkartenNr.getField().getValue()))
		{
			m_tfChipkartenNr.getField().setValue(m_tfChipkartenNr.getField().getOriginalValue());
			Messages.showErrorMessage("Die ChipkartenNr darf nicht geändert werden.");
		}
	}


	public static Date getDatum() {
		if (m_tfDatum == null)
		{
			return null;
		}
		
		return Format.getDateValue(m_tfDatum.getField().getValue());
	}


	private void setDatum(Date datum) {
		if (m_tfDatum != null)
		{
			m_tfDatum.getField().setValue(datum);
			checkDatumKalener(datum, m_kalender);
		}
	}


	private Date getDatumBis() {
		if (m_tfDatumBis != null)
		{
			return Format.getDateValue(m_tfDatumBis.getField().getValue());
		}
		
		return null;
	}


	private void setDatumBis(Date datumBis) {
		if (m_tfDatumBis != null)
		{
			m_tfDatumBis.getField().setValue(datumBis);
			checkDatumKalener(datumBis, m_kalenderBis);
		}
	}


	private void setAnzahlTage(Object anzTage) {
		if (m_tfAnzahlTage != null)
		{
			m_tfAnzahlTage.getField().setValue(anzTage);
		}
	}


	private int getAnzahlTage() {
		if (m_tfAnzahlTage != null)
		{
			return Format.getIntValue(m_tfAnzahlTage.getField());
		}
		
		return 0;
	}


	private DateTime getKalender() {
		return m_kalender;
	}


	private DateTime getKalenderBis() {
		return m_kalenderBis;
	}


	private void setUhrzeit(int uhrzeitAsInt) {
		m_tfUhrzeit.getField().setValue(uhrzeitAsInt);
	}


//	private int getUhrzeit() {
//		return Format.getIntValue(m_tfUhrzeit.getField().getValue());
//	}


	private int getUhrzeitBis() {
		if (m_tfUhrzeitBis != null)
		{
			return Format.getIntValue(m_tfUhrzeitBis.getField().getValue());
		}
		
		return 0;
	}


	private void setUhrzeitBis(int uhrzeitBis) {
		m_tfUhrzeitBis.getField().setValue(uhrzeitBis);
	}


	private IField getFieldBuchungsartID() {
		return m_comboBuchungsart.getField();
	}


	private int getBuchungsartID() {
		return Format.getIntValue(getFieldBuchungsartID().getValue());
	}


	private void setBuchungsartID(int buchungsartID) {
		getFieldBuchungsartID().setValue(buchungsartID);
	}


	private IField getFieldIsVorlage() {
		return m_checkIsVorlage.getField();
	}


	private boolean isVorlage() {
		if (m_checkIsVorlage != null)
		{
			return getFieldIsVorlage().getBooleanValue();
		}
		
		return false;
	}


	private void setIsVorlage(boolean value) {
		if (m_checkIsVorlage != null)
		{
			getFieldIsVorlage().setValue(value);
		}
	}


	/**
	 * allgemeine Refresh-Funktion für alle Felder
	 * 
	 * @param reason
	 */
	public void refresh(int reason) {
		super.refresh(reason, null);
	}
	
	
	/**
	 * Nur einzelne Felder sind editierbar.
	 * 
	 * @see framework.cui.controls.base.BaseCompositeControl#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// nur wenn bereits eine Buchung geladen wurde
		if (m_coBuchung == null)
		{
			return;
		}
		
		// per default alles deaktivieren
		super.refresh(reasonDisabled, null);
		
		// es gibt einen Modus bei dem nur die Bemerkung bearbeitet werden darf (beim Ablehnen von Anträgen)
		if (m_nurBemerkungBearbeiten)
		{
			m_tfBemerkung.refresh(reasonEnabled, null);
			m_kalender.setEnabled(false);
			return;
		}

		try 
		{
			int buchungsartIdAktuell, buchungsartIdOriginal;
			boolean selbstBuchung, selbstbuchungZulaessig;
			UserInformation userInformation;
			
			userInformation = UserInformation.getInstance();
			buchungsartIdAktuell = m_coBuchung.getBuchungsartID();
			buchungsartIdOriginal = Format.getIntValue(m_coBuchung.getFieldBuchungsartID().getOriginalValue());
			selbstBuchung = m_coBuchung.isSelbstbuchung(); // eigene Buchung wird bearbeitet
			selbstbuchungZulaessig = m_coBuchung.isSelbstbuchungAenderungZulaessig(); // eigene Buchung wird bearbeitet und Änderungen erlaubt
			
			// die eigenen Daten dürfen zum Teil geändert werden
			// Benutzer der Gruppe Personalverwaltung dürfen andere Daten ändern
			if (selbstbuchungZulaessig 
					// Sekretariat darf nur ändern, wenn man es selbst noch darf
					|| (!selbstBuchung && userInformation.isSekretariat() && CoStatusGenehmigung.isSelbstbuchungAenderungZulaessig(m_coBuchung.getStatusGenehmigungID()))
					// Personalverwaltung darf auch bei Anträgen noch Änderungen machen
					|| (!selbstBuchung && userInformation.isPersonalverwaltung())
					)
			{
				m_tfUhrzeit.refresh(reasonEnabled, null);

				// für sich selbst darf die Buchungsart nicht geändert werden
				if ((!selbstBuchung 
//						&& buchungsartIdOriginal != CoBuchungsart.ID_DIENSTREISE
						// Buchungsarten für die eine Freigabe notwendig ist, dürfen nicht geändert werden; außer es sind OK-Buchungen
						&& ( (!CoBuchungsart.getInstance().isFreigabeMa(buchungsartIdOriginal) && !CoBuchungsart.getInstance().isFreigabeMa(buchungsartIdAktuell))
								|| (m_coBuchung.isStatusOK() && Format.getIntValue(m_coBuchung.getFieldStatusID().getOriginalValue()) == CoStatusBuchung.STATUSID_OK))
						)
					|| m_coBuchung.isNew()) // bei neuer Buchung darf geändert werden
				{
					m_comboBuchungsart.refresh(reasonEnabled, null);
				}
				
				if (!selbstbuchungZulaessig
//						|| buchungsartIdAktuell == CoBuchungsart.ID_VORLESUNG
						|| m_coBuchung.getStatusID() == 0
						)
				{
					m_comboStatus.refresh(reasonEnabled, null);
//					m_comboStatusGenehmigung.refresh(reasonEnabled, null);
				}
				
				m_comboGrundAenderung.refresh(reasonEnabled, null);
				
				m_tfBemerkung.refresh(reasonEnabled, null);

				// neue Buchung
				if (m_coBuchung.isNew())
				{
					m_tfDatum.refresh(reasonEnabled, null);
					m_tfDatumBis.refresh(reasonEnabled, null);
					m_tfUhrzeitBis.refresh(reasonEnabled, null);
					
					if (!selbstbuchungZulaessig)
					{
						m_comboPerson.refresh(reasonEnabled, null);
					}
					m_checkIsVorlage.refresh(reasonEnabled, null);
				}
				// Buchung bearbeiten
				else
				{
					// Admins können die ChipkartenNr auslesen
					if (!selbstBuchung && userInformation.isAdmin())
					{
						m_tfChipkartenNr.refresh(reasonEnabled, null);
					}
				}
			}
			
			m_kalender.setEnabled(m_tfDatum.isEnabled()); 
			if (m_tfDatumBis != null)
			{
				m_kalenderBis.setEnabled(m_tfDatumBis.isEnabled()); 
			}

			
			// Freigabe kann angezeigt werden, sofern vorhanden
			if (m_btFreigabeAnzeigen != null && m_coBuchung.getStatusGenehmigungID() != 0)
			{
				m_btFreigabeAnzeigen.refresh(reasonEnabled, null);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}


}


