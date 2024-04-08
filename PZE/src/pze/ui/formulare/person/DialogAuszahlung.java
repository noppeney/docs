package pze.ui.formulare.person;

import framework.business.interfaces.FW;
import framework.cui.layout.UniLayout;
import framework.ui.controls.BooleanControl;
import framework.ui.controls.ComboControl;
import framework.ui.controls.TextControl;
import framework.ui.form.UniForm;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.IFocusListener;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.CoMessage;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.reftables.buchungen.CoStatusAuszahlung;

/**
 * Dialog zum Bearbeiten einer Buchung
 * 
 * @author Lisiecki
 */
public class DialogAuszahlung extends UniForm {
	
	private final static String RESID_ANSICHT = "dialog.auszahlung";

	private static CoKontowert m_coKontowert;
	
	
	private TextControl m_tfWertAuszahlungUeberstundenProjekt;
	private TextControl m_tfWertAuszahlungUeberstundenReise;

	private ComboControl m_comboStatus;
	private static BooleanControl m_checkKeineAuszahlung;

	
	/**
	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogAuszahlung(String resID) throws Exception {
		super(null, resID);		
		super.createChilds();
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
		// Controls festlegen
		initControls();
	}

	
	/**
	 * Dialog mit der angegebenen Auszahlung öffnen
	 * 
	 * @param coAuszahlungen 
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialog(CoKontowert coAuszahlungen) throws Exception {
		DialogAuszahlung dialog;

		dialog = new DialogAuszahlung(RESID_ANSICHT);
		dialog.initData(coAuszahlungen);

		return showDialog(dialog);			
	}


	/**
	 * Übergebenen Dialog öffnen
	 * 
	 * @param dialog
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	private static boolean showDialog(DialogAuszahlung dialog) throws Exception { // TODO Fehlerbehandlung inco.validate verschieben
		int statusID;
		
		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);
		dialog.getDialog().show();

		// wenn nicht OK geklickt wurde, keine Aktion
		if (dialog.getDialog().getRetVal() != FW.OK)
		{
			m_coKontowert.cancel();
			return false;
		}
		
		// wenn keine Auszahlung gemacht werden soll, Status anpassen
		if (isKeineAuszahlungAusgewaehlt() && m_coKontowert.getWertAuszahlungUeberstundenProjekt() == 0 && m_coKontowert.getWertAuszahlungUeberstundenReise() == 0)
		{
			m_coKontowert.setStatusIDAuszahlung(CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG);
		}
		else 
		{
			// wenn die an Angaben ungültig sind, beende Prüfung
			if (!checkAngabenAuszahlung(dialog))
			{
				return showDialog(m_coKontowert);
			}
			
			// Auszahlung OK -> Status auf beantragt, wenn er bisher leer oder keineAuszahlung war
			statusID = m_coKontowert.getStatusIDAuszahlung();
			if (statusID == 0 || statusID == CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG)
			{
				m_coKontowert.setStatusIDAuszahlung(CoStatusAuszahlung.STATUSID_BEANTRAGT);
			}
		}
		
		
		// nicht ausgezahlte Stunden in Meldung ausgeben, um Angaben zu bestätigen
		if (!Messages.showYesNoMessage("Daten bestätigen", Format.getZeitAsText(m_coKontowert.getWertNichtAusgezahlteUeberstunden()) 
				+ " Plusstunden verbleiben auf dem Gleitzeitkonto und können zu einem späteren Zeitpunkt nicht mehr ausgezahlt werden.<br>"
				+ "Möchten Sie die Angaben bestätigen?"))
		{
			// wenn die Daten nicht gespeichert werden sollen, Dialog weiter anzeigen
			return showDialog(m_coKontowert);
		}

		// Message erzeugen, wenn noch zu viele Stunden auf dem Gleitzeitkonto sind
		// vor dem Speichern, damit im CO die richtige Zeile markiert ist
		new CoMessage().createMessageUeberstunden(m_coKontowert);

		// Daten speichern
		m_coKontowert.save();
		
		return true;
	}


	/**
	 * Prüft die Angaben zu den auszuzahlenden Überstunden auf Korrektheit.
	 * 
	 * @param dialog
	 * @return
	 * @throws Exception
	 */
	protected static boolean checkAngabenAuszahlung(DialogAuszahlung dialog) throws Exception {
		String errorMessage;
		
		errorMessage = m_coKontowert.validateAngabenAuszahlung();
		if (errorMessage != null)
		{
			showErrorMessage(errorMessage, dialog);
			return false;
		}
		
		return true;
	}


	/**
	 * Fehlermeldung, wenn die Einträge nicht vollständig sind
	 * 
	 * @param bezeichnung z. B. "keine Person"
	 * @param dialog 
	 * @throws Exception 
	 */
	private static void showErrorMessage(String bezeichnung, DialogAuszahlung dialog) throws Exception {
		Messages.showErrorMessage("Fehler beim Speichern", bezeichnung + "<br>Die Änderungen wurden nicht gespeichert.");
	}


	/**
	 * Controls initialisieren
	 * @throws Exception 
	 */
	private void initControls() throws Exception {

		// Textfelder zur Eintragung von Stunden
		m_tfWertAuszahlungUeberstundenProjekt = (TextControl) getControl(getResID() + ".wertauszahlungueberstundenprojekt");
		m_tfWertAuszahlungUeberstundenProjekt.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				if (m_coKontowert.getWertAuszahlungUeberstundenProjekt() > 0)
				{
					m_checkKeineAuszahlung.getField().setValue(false);
					refresh(reasonDataChanged, null);
				}
			}
			
			@Override
			public void focusGained(IControl control) {
			}
		});
		
		m_tfWertAuszahlungUeberstundenReise = (TextControl) getControl(getResID() + ".wertauszahlungueberstundenreise");
		m_tfWertAuszahlungUeberstundenReise.setFocusListener(new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				if (m_coKontowert.getWertAuszahlungUeberstundenReise() > 0)
				{
					m_checkKeineAuszahlung.getField().setValue(false);
					refresh(reasonDataChanged, null);
				}
			}
			
			@Override
			public void focusGained(IControl control) {
			}
		});
		
		
		// Checkbox mit Listener um die Auszahlungs-Felder zu leeren
		m_checkKeineAuszahlung = (BooleanControl) getControl("dialog.auszahlung.keineauszahlung");
		m_checkKeineAuszahlung.setValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				
				// wenn keine Auszahlung gemacht werden soll, setze die Anzahl auf 0/NULL
				if ((boolean) currentValue)
				{
					m_coKontowert.setWertAuszahlungUeberstundenProjekt(null);
					m_coKontowert.setWertAuszahlungUeberstundenReise(null);
					
//					refresh(reasonDataChanged, null);
					// beim refresh wird das Datum von 12 auf 2 Uhr geändert (ich weiss nicht warum), daher nur die einzelnen Felder aktualisieren
					m_tfWertAuszahlungUeberstundenProjekt.refresh(reasonDataChanged, null);
					m_tfWertAuszahlungUeberstundenReise.refresh(reasonDataChanged, null);
				}
				System.out.println();
			}
		});

		
		// ComboBox mit Listener um die Auszahlungs-Felder zu leeren
		m_comboStatus = (ComboControl) getControl(getResID() + ".statusidauszahlung");
		m_comboStatus.setValueChangeListener(new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				
				// wenn keine Auszahlung gemacht werden soll, setze die Anzahl auf 0/NULL
				if ((int)currentValue == CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG)
				{
					m_coKontowert.setWertAuszahlungUeberstundenProjekt(null);
					m_coKontowert.setWertAuszahlungUeberstundenReise(null);
					m_checkKeineAuszahlung.getField().setValue(true);
				}
				else
				{
					m_checkKeineAuszahlung.getField().setValue(false);
				}
				
				refresh(reasonDataChanged, null);
			}
		});
	}


	/**
	 * Co setzen
	 * 
	 * @param coAuszahlungen
	 * @throws Exception
	 */
	private void initData(CoKontowert coAuszahlungen) throws Exception {
		
		m_coKontowert = coAuszahlungen;
		if (!m_coKontowert.isEditing())
		{
			m_coKontowert.begin();
		}

		setData(m_coKontowert);
		
		// Checkbox auswählen, wenn Status keineAuszahlung
		if (m_coKontowert.getStatusIDAuszahlung() == CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG)
		{
			m_checkKeineAuszahlung.getField().setValue(true);
		}
	}


	/**
	 * Checkbox keine Auszahlung ist ausgewählt
	 * 
	 * @return
	 * @throws Exception
	 */
	protected static boolean isKeineAuszahlungAusgewaehlt() throws Exception {
		return m_checkKeineAuszahlung.getField().getBooleanValue() || m_coKontowert.getStatusIDAuszahlung() == CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG;
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
		if (m_coKontowert == null)
		{
			return;
		}
		
		// per default alles deaktivieren
		super.refresh(reasonDisabled, null);
		
		// Auszahlungsfelder aktivieren
		m_checkKeineAuszahlung.refresh(reasonEnabled, null);
		m_tfWertAuszahlungUeberstundenProjekt.refresh(reasonEnabled, null);
		m_tfWertAuszahlungUeberstundenReise.refresh(reasonEnabled, null);
		
		// Personalabteilung darf den Status ändern, nur für sich selbst nicht
		try
		{
			if (UserInformation.getInstance().isPersonalverwaltung() && UserInformation.getPersonID() != m_coKontowert.getPersonID())
			{
				m_comboStatus.refresh(reasonEnabled, null);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}


}


