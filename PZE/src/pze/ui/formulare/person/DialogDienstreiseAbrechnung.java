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
import pze.business.objects.dienstreisen.CoDienstreise;
import pze.business.objects.dienstreisen.CoDienstreiseAbrechnung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.buchungen.CoStatusAuszahlung;

/**
 * Dialog zum Bearbeiten einer Buchung
 * 
 * @author Lisiecki
 */
public class DialogDienstreiseAbrechnung extends UniForm {
	
	private final static String RESID_DIENSTREISE = "dialog.dienstreiseabrechnung";

	private static CoDienstreiseAbrechnung m_coDienstreiseAbrechnung;
	
	
	private TextControl m_tfZeitWohnortWti;
	private TextControl m_tfKmWohnortWti;
	private TextControl m_tfKm;
	
	private TextControl m_tfAnkunftKunde;
	private TextControl m_tfAbfahrtKunde;
	private TextControl m_tfPauseKunde;
	private TextControl m_tfPauseReise;
	
	private TextControl m_tfGeaendertAm;
//
	private ComboControl m_comboOrtVorDr;
	private ComboControl m_comboOrtNachDr;
	private ComboControl m_comboStatus;
	private ComboControl m_comboGeaendertVon;
//
//	private BooleanControl m_checkUebernachtung;

	
	/**
	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogDienstreiseAbrechnung(String resID) throws Exception {
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
	 * @param coDienstreiseAbrechnung 
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialog(CoDienstreiseAbrechnung coDienstreiseAbrechnung) throws Exception {
		DialogDienstreiseAbrechnung dialog;

		dialog = new DialogDienstreiseAbrechnung(RESID_DIENSTREISE);
		dialog.initData(coDienstreiseAbrechnung);

		return showDialog(dialog);			
	}


	/**
	 * Übergebenen Dialog öffnen
	 * 
	 * @param dialog
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	private static boolean showDialog(DialogDienstreiseAbrechnung dialog) throws Exception { // TODO Fehlerbehandlung inco.validate verschieben
		int statusID;
		
		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);

		dialog.getDialog().show();

		// wenn nicht OK geklickt wurde, keine Aktion
		if (dialog.getDialog().getRetVal() != FW.OK)
		{
			m_coDienstreiseAbrechnung.cancel();
			return false;
		}
		
		// wenn keine Auszahlung gemacht werden soll, Status anpassen
//		if (isKeineAuszahlungAusgewaehlt() && m_coKontowert.getWertAuszahlungUeberstundenProjekt() == 0 && m_coKontowert.getWertAuszahlungUeberstundenReise() == 0)
//		{
//			m_coKontowert.setStatusIDAuszahlung(CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG);
//		}
//		else 
//		{
//			// wenn die an Angaben ungültig sind, beende Prüfung
//			if (!checkAngabenAuszahlung(dialog))
//			{
//				return showDialog(m_coKontowert);
//			}
//			
//			// Auszahlung OK -> Status auf beantragt, wenn er bisher leer oder keineAuszahlung war
//			statusID = m_coKontowert.getStatusIDAuszahlung();
//			if (statusID == 0 || statusID == CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG)
//			{
//				m_coKontowert.setStatusIDAuszahlung(CoStatusAuszahlung.STATUSID_BEANTRAGT);
//			}
//		}
		
		
		// nicht ausgezahlte Stunden in Meldung ausgeben, um Angaben zu bestätigen
//		if (!Messages.showYesNoMessage("Daten bestätigen", Format.getZeitAsText(m_coDienstreise.getWertNichtAusgezahlteUeberstunden()) 
//				+ " Plusstunden verbleiben auf dem Gleitzeitkonto und können zu einem späteren Zeitpunkt nicht mehr ausgezahlt werden.<br>"
//				+ "Möchten Sie die Angaben bestätigen?"))
//		{
//			// wenn die Daten nicht gespeichert werden sollen, Dialog weiter anzeigen
//			return showDialog(m_coDienstreise);
//		}

		// Daten speichern
		m_coDienstreiseAbrechnung.save();
		
		return true;
	}


	/**
	 * Prüft die Angaben zu den auszuzahlenden Überstunden auf Korrektheit.
	 * 
	 * @param dialog
	 * @return
	 * @throws Exception
	 */
	protected static boolean checkAngabenAuszahlung(DialogDienstreiseAbrechnung dialog) throws Exception {
		String errorMessage;
		
//		errorMessage = m_coDienstreise.validateAngabenAuszahlung();
//		if (errorMessage != null)
//		{
//			showErrorMessage(errorMessage, dialog);
//			return false;
//		}
		
		return true;
	}


	/**
	 * Fehlermeldung, wenn die Einträge nicht vollständig sind
	 * 
	 * @param bezeichnung z. B. "keine Person"
	 * @param dialog 
	 * @throws Exception 
	 */
	private static void showErrorMessage(String bezeichnung, DialogDienstreiseAbrechnung dialog) throws Exception {
		Messages.showErrorMessage("Fehler beim Speichern", bezeichnung + "<br>Die Änderungen wurden nicht gespeichert.");
	}


	/**
	 * Controls initialisieren
	 * @throws Exception 
	 */
	private void initControls() throws Exception {
		IFocusListener focusListener;
		IValueChangeListener valueChangeListener;

		m_tfZeitWohnortWti = (TextControl) findControl(getResID() + ".zeitwohnortwti");
		m_tfKmWohnortWti = (TextControl) findControl(getResID() + ".kmwohnortwti");
		m_tfKm = (TextControl) findControl(getResID() + ".km");

		m_tfAnkunftKunde = (TextControl) findControl(getResID() + ".ankunftkunde");
		m_tfAbfahrtKunde = (TextControl) findControl(getResID() + ".abfahrtkunde");
		m_tfPauseKunde = (TextControl) findControl(getResID() + ".pausekunde");
		m_tfPauseReise = (TextControl) findControl(getResID() + ".pausereise");

		m_comboOrtVorDr = (ComboControl) findControl(getResID() + ".ortvorherid");
		m_comboOrtNachDr = (ComboControl) findControl(getResID() + ".ortnachherid");

		m_tfGeaendertAm = (TextControl) findControl(getResID() + ".geaendertam");
		m_comboStatus = (ComboControl) findControl(getResID() + ".statusid");
		m_comboGeaendertVon = (ComboControl) findControl(getResID() + ".geaendertvonid");
		
		
		// Listener um die Zeitberechnung nach Änderung der Textfelder zu aktualisieren
		focusListener = new IFocusListener() {
			
			@Override
			public void focusLost(IControl control) {
				m_coDienstreiseAbrechnung.aktualisiereZeiten();
				refresh(reasonDataChanged, null);
			}
			
			@Override
			public void focusGained(IControl control) {
				// TODO Auto-generated method stub
				
			}
		};
		
		m_tfAnkunftKunde.setFocusListener(focusListener);
		m_tfAbfahrtKunde.setFocusListener(focusListener);
		m_tfPauseKunde.setFocusListener(focusListener);
		m_tfPauseReise.setFocusListener(focusListener);
		
		
		// Listener um die Zeitberechnung nach Änderung der ComboBoxen zu aktualisieren
		valueChangeListener = new IValueChangeListener() {
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				m_coDienstreiseAbrechnung.aktualisiereZeiten();
				refresh(reasonDataChanged, null);
			}
		};
		
		m_comboOrtVorDr.setValueChangeListener(valueChangeListener);
		m_comboOrtNachDr.setValueChangeListener(valueChangeListener);
		
	}


	/**
	 * Co setzen
	 * 
	 * @param coDienstreiseAbrechnung
	 * @throws Exception
	 */
	private void initData(CoDienstreiseAbrechnung coDienstreiseAbrechnung) throws Exception {
		
		m_coDienstreiseAbrechnung = coDienstreiseAbrechnung;
		if (!m_coDienstreiseAbrechnung.isEditing())
		{
			m_coDienstreiseAbrechnung.begin();
		}
		
		m_coDienstreiseAbrechnung.aktualisiereZeiten();

		setData(m_coDienstreiseAbrechnung);
		
		// Checkbox auswählen, wenn Status keineAuszahlung
//		if (m_coKontowert.getStatusIDAuszahlung() == CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG)
//		{
////			m_checkKeineAuszahlung.getField().setValue(true);
//		}
	}


//	/**
//	 * Checkbox keine Auszahlung ist ausgewählt
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	protected static boolean isKeineAuszahlungAusgewaehlt() throws Exception {
//		return m_checkKeineAuszahlung.getField().getBooleanValue() || m_coKontowert.getStatusIDAuszahlung() == CoStatusAuszahlung.STATUSID_KEINE_AUSZAHLUNG;
//	}


	/**
	 * Nur einzelne Felder sind editierbar.
	 * 
	 * @see framework.cui.controls.base.BaseCompositeControl#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		super.refresh(reason, element);
		
		// nur wenn bereits eine Buchung geladen wurde
		if (m_coDienstreiseAbrechnung == null)
		{
			return;
		}
		
		// per default alles aktivieren
		super.refresh(reasonEnabled, null);
		
		
		m_tfZeitWohnortWti.refresh(reasonDisabled, null);
		m_tfKmWohnortWti.refresh(reasonDisabled, null);
		m_tfKm.refresh((m_coDienstreiseAbrechnung.isPrivatPkw() ? reasonEnabled : reasonDisabled), null);

		m_tfGeaendertAm.refresh(reasonDisabled, null);
		m_comboGeaendertVon.refresh(reasonDisabled, null);
//
//		// wenn keine Auszahlung gemacht werden soll, setze die Anzahl auf 0/NULL
//		if (m_coDienstreise.isUebernachtung())
//		{
//			m_tfHotel.refresh(reasonEnabled, null);
//		}
//		else
//		{
//			m_tfHotel.refresh(reasonDisabled, null);
//		}
//
//		
		try
		{
			// nur Benutzer ab Sekretariat dürfen den Status ändern
			// die eigenen Daten dürfen nicht geändert werden
			if (m_coDienstreiseAbrechnung.getPersonID() == CoPerson.getInstance().getIdByUserID(UserInformation.getInstance().getUserID())
					||  (!UserInformation.getInstance().isPersonalverwaltung() && !UserInformation.getInstance().isSekretariat()))
			{
				m_comboStatus.refresh(reasonDisabled, null);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		

	}


}


