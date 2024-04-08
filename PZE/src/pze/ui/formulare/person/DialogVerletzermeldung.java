package pze.ui.formulare.person;

import framework.business.interfaces.FW;
import framework.cui.layout.UniLayout;
import framework.ui.controls.TextControl;
import framework.ui.form.UniForm;
import pze.business.objects.CoVerletzerliste;

/**
 * Dialog zum Bearbeiten einer Verletzermeldung
 * 
 * @author Lisiecki
 */
public class DialogVerletzermeldung extends UniForm {
	
	private final static String RESID_ANSICHT = "dialog.verletzermeldung";

	private static CoVerletzerliste m_coVerletzerliste;
	
	protected TextControl m_tfBemerkung;

	
	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogVerletzermeldung(String resID) throws Exception {
		super(null, resID);		
		super.createChilds();
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
		// Controls festlegen
		initControls();
	}

	
	/**
	 * Dialog mit der angegebenen Verletzermeldung öffnen
	 * 
	 * @param coVerletzerliste 
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialog(CoVerletzerliste coVerletzerliste) throws Exception {
		DialogVerletzermeldung dialog;

		dialog = new DialogVerletzermeldung(RESID_ANSICHT);
		dialog.initData(coVerletzerliste);

		return showDialog(dialog);			
	}


	/**
	 * Übergebenen Dialog öffnen
	 * 
	 * @param dialog
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	private static boolean showDialog(DialogVerletzermeldung dialog) throws Exception {
		
		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);
		dialog.getDialog().show();

		// wenn nicht OK geklickt wurde, keine Aktion
		if (dialog.getDialog().getRetVal() != FW.OK || !m_coVerletzerliste.isModified())
		{
			m_coVerletzerliste.cancel();
			return false;
		}
		
		
		// nicht ausgezahlte Stunden in Meldung ausgeben, um Angaben zu bestätigen
//		if (!Messages.showYesNoMessage("Daten bestätigen", Format.getZeitAsText(m_coVerletzerliste.getWertNichtAusgezahlteUeberstunden()) 
//				+ " Plusstunden verbleiben auf dem Gleitzeitkonto und können zu einem späteren Zeitpunkt nicht mehr ausgezahlt werden.<br>"
//				+ "Möchten Sie die Angaben bestätigen?"))
//		{
//			// wenn die Daten nicht gespeichert werden sollen, Dialog weiter anzeigen
//			return showDialog(m_coVerletzerliste);
//		}

		// Daten speichern
		m_coVerletzerliste.save();
		
		return true;
	}


	/**
	 * Controls initialisieren
	 * @throws Exception 
	 */
	private void initControls() throws Exception {
		m_tfBemerkung = (TextControl) findControl(getResID() + ".bemerkung");
	}


	/**
	 * Co setzen
	 * 
	 * @param coVerletzerliste
	 * @throws Exception
	 */
	private void initData(CoVerletzerliste coVerletzerliste) throws Exception {
		
		m_coVerletzerliste = coVerletzerliste;
		if (!m_coVerletzerliste.isEditing())
		{
			m_coVerletzerliste.begin();
		}

		setData(m_coVerletzerliste);
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
		if (m_coVerletzerliste == null)
		{
			return;
		}
		
		// per default alles deaktivieren
		super.refresh(reasonDisabled, null);
		
		// Bemerkung aktivieren
		m_tfBemerkung.refresh(reasonEnabled, null);
	}


}


