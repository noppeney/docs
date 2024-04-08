package pze.ui.formulare.projektverwaltung;

import java.util.Date;

import framework.ui.controls.BooleanControl;
import framework.ui.controls.ComboControl;
import framework.ui.controls.TextControl;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.controls.ITabFolder;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.projektverwaltung.CoProjektverfolgung;
import pze.business.objects.reftables.projektverwaltung.CoAktionProjektverfolgung;
import pze.business.objects.reftables.projektverwaltung.CoAusgabezeitraum;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;
import pze.ui.formulare.UniFormWithSaveLogic;
import startup.PZEStartupAdapter;

/**
 * Abstrakte Klasse für Projekte (Aufträge und Abrufe) mit einigen allgemeinen Funktionen.<br>
 * Zum. Beispiel das Formular zur Auswertung.
 * 
 * @author Lisiecki
 */
public abstract class FormProjekt extends UniFormWithSaveLogic {
	protected ITabFolder m_subTabFolder;

	protected FormProjektArbeitsplan m_formProjektArbeitsplan;
	protected FormProjektMitarbeiter m_formProjektMitarbeiter;
	protected FormProjektProjektverfolgung m_formProjektverfolgung;
	protected FormAuswertungProjekt m_formAuswertungProjekt;

	protected TextControl m_tfEdvNr;
	protected TextControl m_tfBeschreibung;
	
	protected TextControl m_tfDatumTerminOriginal;
	protected TextControl m_tfBestellwertOriginal;
	
	protected BooleanControl m_isProjektverfolgungAktiv;
	protected BooleanControl m_isProjektbericht;
	protected BooleanControl m_isMessage8Stunden;
	protected ComboControl m_comboProzentmeldung;
	protected ComboControl m_comboProzentmeldung2;
	protected ComboControl m_comboIntervallProjektverfolgung;
	protected ComboControl m_comboIntervallProjektbericht;

	protected ComboControl m_comboAbteilungsleiter;
	protected ComboControl m_comboProjektleiter;
	protected ComboControl m_comboProjektleiter2;

	protected TextControl m_tfPuffer;
	
	protected TextControl m_tfBemerkung;
	
//	private int m_messageIdToSave;


	
	/**
	 * Konstruktor
	 * 
	 * @param parent
	 * @param resid
	 * @throws Exception
	 */
	public FormProjekt(Object parent, String resid) throws Exception {
		super(parent, resid);
	}


	protected void setSubTabFolder(ITabFolder subTabFolder) {
		m_subTabFolder = subTabFolder;
	}


	/**
	 * Formular Arbeitsplan hinzufügen
	 * 
	 * @throws Exception
	 */
	protected void addFormArbeitsplan(CoProjekt coProjekt) throws Exception {
		if (PZEStartupAdapter.MODUS_ARBEITSPLAN)
		{
			m_formProjektArbeitsplan = new FormProjektArbeitsplan(m_subTabFolder, coProjekt, this);
			m_subTabFolder.add(FormProjektArbeitsplan.RESID, FormProjektArbeitsplan.getKey(0), m_formProjektArbeitsplan, false);			
			addAdditionalForm(m_formProjektArbeitsplan);
		}
	}


	/**
	 * Formular Mitarbeiter hinzufügen
	 * 
	 * @throws Exception
	 */
	protected void addFormMitarbeiter(CoProjekt coProjekt) throws Exception {
		m_formProjektMitarbeiter = new FormProjektMitarbeiter(m_subTabFolder, coProjekt, this);
		m_subTabFolder.add(FormProjektMitarbeiter.RESID, FormProjektMitarbeiter.getKey(0), m_formProjektMitarbeiter, false);			
		addAdditionalForm(m_formProjektMitarbeiter);
	}


	/**
	 * Formular Projektverfolgung hinzufügen
	 * 
	 * @throws Exception
	 */
	protected void addFormProjektverfolgung(CoProjekt coProjekt) throws Exception {
		m_formProjektverfolgung = new FormProjektProjektverfolgung(m_subTabFolder, coProjekt, this);
		m_subTabFolder.add(FormProjektProjektverfolgung.RESID, FormProjektProjektverfolgung.getKey(0), m_formProjektverfolgung, false);		
		addAdditionalForm(m_formProjektverfolgung);
	}


	/**
	 * Formular Auswertung hinzufügen
	 * 
	 * @throws Exception
	 */
	protected void addFormAuswertung() throws Exception {
		m_formAuswertungProjekt = new FormAuswertungProjekt(m_subTabFolder, this);
		m_subTabFolder.add(FormAuswertungProjekt.RESID, FormAuswertungProjekt.getKey(0), m_formAuswertungProjekt, false);			
		addAdditionalForm(m_formAuswertungProjekt);
	}


	/**
	 * Formular Auswertung entfernen
	 * 
	 * @throws Exception
	 */
	private void removeFormAuswertung() throws Exception {
		m_subTabFolder.remove(FormAuswertungProjekt.getKey(0));			
		removeAdditionalForm(m_formAuswertungProjekt);
	}

	
	/**
	 * Öffnet das Formular zur Projektverfolgung
	 * 
	 * @throws Exception
	 */
	private void showFormProjektverfolgung() throws Exception {
		m_subTabFolder.setSelection(FormProjektProjektverfolgung.getKey(0));
		m_formProjektverfolgung.activate();
	}


	/**
	 * Öffnet das Formular zur Projektverfolgung und fügt einen neuen Datensatz hinzu
	 * 
	 * @param meldungID 
	 * @throws Exception
	 */
	public void showFormProjektverfolgung(int meldungID) throws Exception {
		m_formProjektverfolgung.setModus(meldungID);
		showFormProjektverfolgung();
		editlistener.activate(null);

		if (m_formProjektverfolgung.isModusPL())
		{
			m_formProjektverfolgung.add();
		}
		else if (m_formProjektverfolgung.isModusAL())
		{
			if (!((CoProjektverfolgung) m_formProjektverfolgung.getCo()).updateAL())
			{			
				m_formProjektverfolgung.add();
			}
		}
//		m_formProjektverfolgung.refreshByEditMode();
//		updateEditToolbarButton();// das muss ggf. am besten auf dem Reiter aufgerufen werden
	}


//	/**
//	 * Öffnet das Formular zur Projektverfolgung und fügt einen neuen Datensatz hinzu
//	 * 
//	 * @param meldungID 
//	 * @throws Exception
//	 */
//	public void showFormProjektverfolgungAdd(int meldungID) throws Exception {
//		if (MODUS_PROJEKTVERFOLGUNG)
//		{
//			showFormProjektverfolgung();
//			editlistener.activate(null);
//			m_formProjektverfolgung.setModus(meldungID);
//			m_formProjektverfolgung.add();
//		}
//	}
//
//
//	/**
//	 * Öffnet das Formular zur Projektverfolgung und passt den aktuellen Datensatz für den AL an
//	 * 
//	 * @param meldungID 
//	 * @throws Exception
//	 */
//	public void showFormProjektverfolgungAL(int meldungID) throws Exception {
//		if (MODUS_PROJEKTVERFOLGUNG)
//		{
//			showFormProjektverfolgung();
//			editlistener.activate(null);
//			m_formProjektverfolgung.setModus(meldungID);
//			if (!((CoProjektverfolgung) m_formProjektverfolgung.getCo()).updateAL())
//			{			
//				m_formProjektverfolgung.add();
//			}
//		}
//	}


	/**
	 * Formular Auswertung neu laden wegen geänderter Einstellungen.<br>
	 * Aktualisieren funktioniert nicht, da sich die Anzahl der Spalten ändern kann, deshalb wir das Formular neu erstellt.
	 * 
	 * @param datum Datum in dem Monat
	 * @throws Exception
	 */
	public void reloadFormAuswertung() throws Exception {
		
		// geht nur, wenn man nicht im Edit-Modus ist, da auch das Projekt-CO neu geladen werden muss (falls jemand zwischenzeitlich STunden eingetragen hat)
		if (getData().isEditing())
		{
			Messages.showErrorMessage("Aktualisierung nicht möglich", "Die Auswertung kann nicht aktualisiert werden, da sich das Formular im Edit-Modus befindet.<br>"
					+ "Bitte speichern oder verwerfen Sie die Änderungen und führen anschließend die Auswertung erneut durch.");
			return;
		}
		
		reloadCo();
		
		removeFormAuswertung();	
		addFormAuswertung();
			
		m_subTabFolder.setSelection(FormAuswertungProjekt.getKey(0));
		
//		activate();
	}


	/**
	 * Controls initialisieren
	 * 
	 * @param resID 
	 * @throws Exception 
	 */
	protected void initControls(String resID) throws Exception {
		m_tfDatumTerminOriginal = (TextControl) findControl(resID + ".datumterminoriginal");
		m_tfBestellwertOriginal = (TextControl) findControl(resID + ".bestellwertoriginal");

		m_isProjektverfolgungAktiv = (BooleanControl) findControl(resID + ".projektverfolgungaktiv");
		m_isProjektbericht = (BooleanControl) findControl(resID + ".projektbericht");
		m_isMessage8Stunden = (BooleanControl) findControl(resID + ".message8stunden");

		m_comboProzentmeldung = (ComboControl) findControl(resID + ".prozentmeldungid");
		m_comboProzentmeldung2 = (ComboControl) findControl(resID + ".prozentmeldungid2");
		m_comboIntervallProjektverfolgung = (ComboControl) findControl(resID + ".intervallprojektverfolgungid");
		m_comboIntervallProjektbericht = (ComboControl) findControl(resID + ".intervallprojektberichtid");
		
		// Intervall ist erstmal nur monatlich möglich
		CoAusgabezeitraum coAusgabezeitraum;
		coAusgabezeitraum = new CoAusgabezeitraum();
		coAusgabezeitraum.loadByID(CoAusgabezeitraum.ID_MONATLICH);
		refreshItems(m_comboIntervallProjektverfolgung, coAusgabezeitraum, ((CoProjekt) getCo()).getFieldIntervallProjektverfolgungID());
		
		coAusgabezeitraum = new CoAusgabezeitraum();
		coAusgabezeitraum.loadForProjektbericht();
		refreshItems(m_comboIntervallProjektbericht, coAusgabezeitraum, ((CoProjekt) getCo()).getFieldIntervallProjektberichtID());
	}
	

	/**
	 * Wird vom FocusListener auf einzelne Objekte gestartet
	 * 
	 * @param control
	 * @throws Exception 
	 */
	protected void focusGained(IControl control) throws Exception {
		if (!getCo().isEditing())
		{
			return;
		}
		
		// wenn das Bemerkungsfeld geändert wird, soll dies mit Datum/Kürzel dokumentiert werden
		if (control.equals(m_tfBemerkung))
		{
			CoPerson.getInstance().moveToID(UserInformation.getPersonID());
			CoProjekt coProjekt;
			
			coProjekt = ((CoProjekt) getCo());
			coProjekt.setBemerkung(Format.getString(new Date()) + " " + CoPerson.getInstance().getKuerzel() + ": \n" 
					+ (coProjekt.getBemerkung() == null ? "" : coProjekt.getBemerkung()));
			
			// Feld aktualisieren um Änderung anzuzeigen
			coProjekt.updateGeaendertVonAm();
			refresh(reasonDataChanged, null);
		}
	}


	/**
	 * Aktualisiert das CO des Formulars
	 * @throws Exception 
	 */
	protected abstract void reloadCo() throws Exception;


	/**
	 * Nur Projektverwaltung/Administratoren und Projektbearbeiter (AL, PL) dürfen Projekte bearbeiten
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#mayEdit()
	 */
	@Override
	public boolean mayEdit() {
		UserInformation userInformation;
		
		userInformation = UserInformation.getInstance();
		
		return userInformation.isProjektverwaltung() || isProjektbearbeiter() 
				|| (m_formProjektverfolgung != null && m_formProjektverfolgung.hasModus());
	}
	
	
	@Override
	public void doBeforeSave() throws Exception {
		
		// auf untergeordneten Formularen ggf. Aktionen durchführen
		for (UniFormWithSaveLogic addForm : additionalForms)
		{
			addForm.doBeforeSave();
		}
	}

	
	@Override
	public void doAfterSave() throws Exception {
		// TODO Message aus Projektverfolgung speichern
		//
		// vergleichen oder nur ob Änderungen gemacht wurden?
		
	}

//
//	/**
//	 * ID der Meldung merken, die beim Speichern des Projekts quittiert werden soll
//	 * 
//	 * @param messageID
//	 */
//	private void setMessageToSave(int messageID) {
//		m_messageIdToSave = messageID;
//	}


	/**
	 * geänderte Daten aus der Projektverfolgung in das Projekt eintragen
	 * 
	 * @param messageID
	 * @throws Exception
	 */
	public void setDatenProjektverfolgung(int messageID) throws Exception {
		CoProjekt coProjekt;
		CoProjektverfolgung coProjektverfolgung;

		// Daten laden
		coProjekt = (CoProjekt) getCo();
		coProjektverfolgung = new CoProjektverfolgung(null, null);
		coProjektverfolgung.loadByMeldungID(messageID); // man könnte das auch aus dem Form holen
		
		// Bearbeitung beginnen
		editlistener.activate(null);
		
		// Termin/Kosten aus der Projektverfolgung übernehmen
		if (coProjektverfolgung.getAktionID() == CoAktionProjektverfolgung.STATUSID_AENDERUNGEN_UEBERNEHMEN)
		{
			coProjekt.setLiefertermin(coProjektverfolgung.getTerminAL());
			coProjekt.setBestellwert(coProjektverfolgung.getKostenAL());
		}
		else
		{
			coProjekt.setStatusID(CoStatusProjekt.STATUSID_ABGESCHLOSSEN);
		}
		
		
		coProjekt.updateGeaendertVonAm();
		refresh(reasonDataChanged, null);
		
//		setMessageToSave(messageID);
	}


	/**
	 * Berechtigungen zur Bearbeitung prüfen
	 * 
	 * @see pze.ui.formulare.UniFormWithSaveLogic#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		UserInformation userInformation;
		
		// prüfen, ob Änderungen gemacht werden dürfen
		if (!UserInformation.getInstance().isProjektverwaltung() && !isProjektbearbeiter() )
		{
			super.refresh(reasonDisabled, null);
		}
		else
		{
			super.refresh(reason, element);
		}

		
		try 
		{
			// wenn bearbeitet werden soll
			if (reason == reasonEnabled)
			{
				userInformation = UserInformation.getInstance();

				// Personen der Projektverwaltung dürfen alles bearbeiten
				// muss abgefragt werden, weil bei Administratoren, die auch Projektleiter sind, einige Felder sonst deaktiviert werden
				if (userInformation.isProjektverwaltung())
				{
					if (!userInformation.isProjektcontrolling())
					{
						m_tfBestellwertOriginal.refresh(reasonDisabled, null);
						m_tfDatumTerminOriginal.refresh(reasonDisabled, null);
					}

					return;
				}

				// wenn es nur ein Bearbeiter (statt Projektverwaltung) ist
				if (isProjektbearbeiter())
				{
					// alles deaktivieren und nur einzelne Felder freigeben
					super.refresh(reasonDisabled, element);

					// die Beschreibung, EDV-Nr. und Puffer dürfen alle Bearbeiter ändern
					m_tfBeschreibung.refresh(reasonEnabled, null);
					m_tfBemerkung.refresh(reasonEnabled, null);
					m_tfEdvNr.refresh(reasonEnabled, null);
					m_tfPuffer.refresh(reasonEnabled, null);
					m_comboProzentmeldung2.refresh(reasonEnabled, null);

					// AL dürfen den Abteilungs- und Projektleiter bestimmen
					if (userInformation.isNurAL())
					{
						m_comboAbteilungsleiter.refresh(reasonEnabled, null);
						m_comboProjektleiter.refresh(reasonEnabled, null);
						m_comboProjektleiter2.refresh(reasonEnabled, null);
						
						// Projektverfolgung
						m_isProjektverfolgungAktiv.refresh(reasonEnabled, null);
						m_isProjektbericht.refresh(reasonEnabled, null);
						m_comboIntervallProjektverfolgung.refresh(reasonEnabled, null);
						m_comboIntervallProjektbericht.refresh(reasonEnabled, null);
						m_isMessage8Stunden.refresh(reasonEnabled, null);
					}

					// PL1 darf PL2 anpassen
					if (UserInformation.getPersonID() == ((CoProjekt) getCo()).getProjektleiterID())
					{
						m_comboProjektleiter2.refresh(reasonEnabled, null);
					}
				}
			}
			
			refreshItemsComboAlByAbteilung();
			refreshItemsCombosByKunde();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	
	/**
	 * Projektbearbeiter sind AL und PL1/2
	 * 
	 * @return
	 */
	protected boolean isProjektbearbeiter() {
		int personID;
		CoProjekt coProjekt;
		
		try 
		{
			personID = UserInformation.getPersonID();
			coProjekt = ((CoProjekt) getCo());
			
			return UserInformation.getInstance().isProjektbearbeiter() 
					|| coProjekt.getProjektleiterID() == personID || coProjekt.getProjektleiterID2() == personID;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	

	protected abstract void refreshItemsComboAlByAbteilung() throws Exception;


	protected abstract void refreshItemsCombosByKunde() throws Exception;

}
