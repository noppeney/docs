package pze.ui.formulare.person.monatseinsatzblatt;

import framework.business.interfaces.FW;
import framework.business.interfaces.fields.IField;
import framework.cui.layout.UniLayout;
import framework.ui.controls.ComboControl;
import framework.ui.form.UniForm;
import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.selection.IValueChangeListener;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattProjekt;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoAuftrag;
import pze.business.objects.projektverwaltung.CoBerichtsNr;
import pze.business.objects.projektverwaltung.CoKostenstelle;
import pze.business.objects.reftables.CoStundenart;
import pze.business.objects.reftables.projektverwaltung.CoKunde;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjekt;
import pze.ui.formulare.UniFormWithSaveLogic;



/**
 * Dialog zum Bearbeiten einer Buchung
 * 
 * @author Lisiecki
 */
public class DialogProjektMonatseinsatzblatt extends UniForm {
	
	private final static String RESID_ANSICHT = "dialog.monatseinsatzblatt.projekt";

	private CoMonatseinsatzblattProjekt m_coMonatseinsatzblattProjekt;
	
	private ComboControl m_comboKunde;
	private ComboControl m_comboAuftrag;
	private ComboControl m_comboAbruf;
	private ComboControl m_comboKostenstelle;
	private ComboControl m_comboBerichtsNr;
	private ComboControl m_comboStundenart;

	/**
	 * ComboControl, dass als erstes (nach dem Kunden) ausgewählt wurde. Die Items für dieses Control werden nicht aktualisiert.
	 */
	private ComboControl m_comboErsteAuswahl;

	
	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogProjektMonatseinsatzblatt(int personID) throws Exception {
		super(null, RESID_ANSICHT);		
		super.createChilds();
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
		// Controls festlegen
		initControls();
		initListener();
		
		// CO anlegen
		initData(personID);

		// Items für Combos anpassen
		valueChanged(null);
		
		// Default für Stundenart
		m_coMonatseinsatzblattProjekt.setStundenartID(CoStundenart.STATUSID_INGENIEURSTUNDEN);
	}

	
	/**
	 * Dialog öffnen
	 * 
	 * @param personID
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialog(int personID) throws Exception {
		DialogProjektMonatseinsatzblatt dialog;

		dialog = new DialogProjektMonatseinsatzblatt(personID);

		return showDialog(dialog);			
	}


	/**
	 * Übergebenen Dialog öffnen
	 * 
	 * @param dialog
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	private static boolean showDialog(DialogProjektMonatseinsatzblatt dialog) throws Exception {
		
		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);
		dialog.getDialog().show();

		// wenn OK geklickt wurde
		if (dialog.getDialog().getRetVal() != FW.OK)
		{
			return false;
		}

		// wenn Daten geändert wurden und kein Grund für die Bearbeitung angegeben wurde, öffne Dialog erneut
		if (dialog.getCoMonatseinsatzblattProjekt().isModified())
		{
			// speichern
			return save(dialog);
		}
		
		return false;
	}




	/**
	 * Buchung speichern.<br>
	 * Beim Erstellen einer neuen Buchung werden ggf. mehrere Buchungen erzeugt.
	 * 
	 * @param dialog
	 * @return Daten gespeichert
	 * @throws Exception
	 */
	private static boolean save(DialogProjektMonatseinsatzblatt dialog) throws Exception {
		CoMonatseinsatzblattProjekt coMonatseinsatzblattProjekt;
		
		// Eingaben prüfen
		if (!dialog.validate())
		{
			return false;
		}
		
		// Auswahl speichern
		coMonatseinsatzblattProjekt = dialog.getCoMonatseinsatzblattProjekt();
		if (!coMonatseinsatzblattProjekt.isEditing())
		{
			coMonatseinsatzblattProjekt.begin();
		}
		
		coMonatseinsatzblattProjekt.save();
		
		return true;
	}


	/**
	 * Eingaben prüfen
	 * 
	 * @return Eingaben zulässig
	 * @throws Exception 
	 */
	private boolean validate() throws Exception {
		CoAbruf coAbruf;

		// Kostenstelle ohne Abruf nicht zulässig
//		if (m_coMonatseinsatzblattProjekt.getKostenstelleID() > 0 && m_coMonatseinsatzblattProjekt.getAbrufID() == 0)
//		{
//			Messages.showErrorMessage("Eine Kostenstelle darf nicht ohne Abruf ausgewählt werden.");
//			return false;
//		}
		
		// Auftrag/Abruf ohne Kostenstelle nicht zulässig, wenn es Kostenstelle gibt
		if (m_coMonatseinsatzblattProjekt.getKostenstelleID() == 0 && m_comboKostenstelle.getField().getItems().getRowCount() > 0)
		{
			Messages.showErrorMessage("Ein Auftrag/Abruf für den es Kostenstellen gibt darf nicht ohne Kostenstelle ausgewählt werden.");
			return false;
		}
		
		// Auftrag ohne Abruf nicht zulässig, wenn es Abrufe gibt
//		if (m_coMonatseinsatzblattProjekt.getAbrufID() == 0 && m_comboAbruf.getField().getItems().getRowCount() > 0)
//		{
//			Messages.showErrorMessage("Ein Auftrag für den es Abrufe gibt darf nicht ohne Abruf ausgewählt werden.");
//			return false;
//		}
//		// neu, da wegen einer bereis ausgewählten Kostenstelle ggf. die Abruf-Auswahlliste leer ist
		// berücksichtigt so auch abgeschlossene Abrufe
		coAbruf = new CoAbruf();
		coAbruf.loadByAuftragID(m_coMonatseinsatzblattProjekt.getAuftragID(), true);
		if (m_coMonatseinsatzblattProjekt.getAbrufID() == 0 && coAbruf.hasRows())
		{
			Messages.showErrorMessage("Ein Auftrag für den es Abrufe gibt darf nicht ohne Abruf ausgewählt werden.");
			return false;
		}
		
		// Kostenstelle ohne BerichtsNr nicht zulässig, wenn es BerichtsNr gibt
		if (m_coMonatseinsatzblattProjekt.getBerichtsNrID() == 0 && m_comboBerichtsNr.getField().getItems().getRowCount() > 0)
		{
			Messages.showErrorMessage("Eine Kostenstelle für die es Berichts-Nrn. gibt darf nicht ohne Berichts-Nr. ausgewählt werden.");
			return false;
		}

		// Stundenart prüfen
		System.out.println(m_coMonatseinsatzblattProjekt.getBerichtsNrID());
		System.out.println(CoStundenart.isStundenartKgg(m_coMonatseinsatzblattProjekt.getStundenartID()));
		if ((m_coMonatseinsatzblattProjekt.getBerichtsNrID() > 0) != (CoStundenart.isStundenartKgg(m_coMonatseinsatzblattProjekt.getStundenartID())))
		{
			Messages.showErrorMessage("Für diese Auswahl ist die Stundenart '" 
					+ CoStundenart.getInstance().getBezeichnung(m_coMonatseinsatzblattProjekt.getStundenartID()) + "' nicht zulässig.");
			return false;
		}

		
		
		return true;
	}


	/**
	 * Controls initialisieren
	 * @throws Exception 
	 */
	private void initControls() throws Exception {
		m_comboKunde = (ComboControl) getControl(getResID() + ".kundeid");
		m_comboAuftrag = (ComboControl) getControl(getResID() + ".auftragid");
		m_comboAbruf = (ComboControl) getControl(getResID() + ".abrufid");
		m_comboKostenstelle = (ComboControl) getControl(getResID() + ".kostenstelleid");
		m_comboBerichtsNr = (ComboControl) getControl(getResID() + ".berichtsnrid");
		m_comboStundenart = (ComboControl) getControl(getResID() + ".stundenartid");
	}


	/**
	 * Listener hinzufügen
	 */
	private void initListener() {
		IValueChangeListener m_valueChangeListener;
		
		
		// ValueChangeListener, um Combos zu aktualisieren
		m_valueChangeListener = new IValueChangeListener() {
			
			@Override
			public void valueChanged(IControl control, Object originalValue, Object lastValue, Object currentValue) throws Exception {
				DialogProjektMonatseinsatzblatt.this.valueChanged(control);
			}
		};

		m_comboKunde.setValueChangeListener(m_valueChangeListener);
		m_comboAuftrag.setValueChangeListener(m_valueChangeListener);
		m_comboAbruf.setValueChangeListener(m_valueChangeListener);
		m_comboKostenstelle.setValueChangeListener(m_valueChangeListener);
		m_comboBerichtsNr.setValueChangeListener(m_valueChangeListener);
	}


	/**
	 * Cacheobject mit neuem Datensatz anlegen
	 * 
	 * @param personID
	 * @throws Exception
	 */
	private void initData(int personID) throws Exception {
		m_coMonatseinsatzblattProjekt = new CoMonatseinsatzblattProjekt();
		m_coMonatseinsatzblattProjekt.createNew();
		m_coMonatseinsatzblattProjekt.setPersonID(personID);
		setData(m_coMonatseinsatzblattProjekt);
	}

	
	/**
	 * Bei geänderten Werten ggf. die anderen Combos aktualisieren
	 * @param control 
	 * 
	 * @throws Exception
	 */
	private void valueChanged(IControl control) throws Exception {
		
		// erstes/führendes ComboControl merken
		if (control == null || control.equals(m_comboKunde))
		{
			m_comboErsteAuswahl = null;
		}
		else if (m_comboErsteAuswahl == null && !control.equals(m_comboStundenart))
		{
			m_comboErsteAuswahl = (ComboControl) control;
		}
		

		// Update der Items
		if (control == null) // Initialisierung
		{
			refreshItemsComboKunde();
			refreshItemsComboAuftrag();
			refreshItemsComboAbruf();
			refreshItemsComboKostenstelle();
			refreshItemsComboBerichtsNr();
		}
		else if (control.equals(m_comboKunde))
		{

			refreshItemsComboAuftrag();
			refreshItemsComboAbruf();
			refreshItemsComboKostenstelle();
			refreshItemsComboBerichtsNr();
		}
		else if (control.equals(m_comboAuftrag))
		{
			// doppelte Auswahl, wenn die Einträge nicht zusammenpassen, werden sie beim ersten mal gelöscht und beim zweiten die neuen gesetzt
			refreshItemsComboAbruf();
			refreshItemsComboKostenstelle();
			refreshItemsComboAbruf();
			refreshItemsComboKostenstelle();
			refreshItemsComboBerichtsNr();
		}
		else if (control.equals(m_comboAbruf))
		{
			// doppelte Auswahl, wenn die Einträge nicht zusammenpassen, werden sie beim ersten mal gelöscht und beim zweiten die neuen gesetzt
			refreshItemsComboAuftrag();
			refreshItemsComboKostenstelle();
			refreshItemsComboAuftrag();
			refreshItemsComboKostenstelle();
			refreshItemsComboBerichtsNr();
		}
		else if (control.equals(m_comboKostenstelle))
		{
			refreshItemsComboAuftrag();
			refreshItemsComboAbruf();
			refreshItemsComboBerichtsNr();
		}
		else if (control.equals(m_comboBerichtsNr))
		{
			refreshItemsComboKostenstelle();
			refreshItemsComboAuftrag();
			refreshItemsComboAbruf();
//			refreshItemsComboAuftrag();
//			refreshItemsComboAbruf();
//			refreshItemsComboKostenstelle();
		}
		else // Combo Stundenart
		{
			return;
		}

		// Kunde auswählen, falls eine andere Comobobox ausgewählt wurde
		updateKunde(m_coMonatseinsatzblattProjekt.getAuftragID());

		refresh(reasonDataChanged, null);
	}


	/**
	 * Aktualisieren der Items der Auftrags-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboKunde() throws Exception {
		IField fieldKunde;
		CoKunde coKunde;
		
		coKunde = new CoKunde();
		coKunde.loadAllWithProjekte(CoStatusProjekt.STATUSID_LAUFEND, false); 
		
		fieldKunde = m_coMonatseinsatzblattProjekt.getFieldKundeID();
		
		refreshItems(m_comboKunde, fieldKunde, coKunde);
	}
	

	/**
	 * Aktualisieren der Items der Auftrags-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboAuftrag() throws Exception {
		IField fieldAuftrag;
		CoAuftrag coAuftrag;
		
		coAuftrag = new CoAuftrag();
		coAuftrag.loadItems(m_coMonatseinsatzblattProjekt.getKundeID(), 
				m_coMonatseinsatzblattProjekt.getAbrufID(), m_coMonatseinsatzblattProjekt.getKostenstelleID(), true);
		
		fieldAuftrag = m_coMonatseinsatzblattProjekt.getFieldAuftragID();
		
		refreshItems(m_comboAuftrag, fieldAuftrag, coAuftrag);
	}
	

	/**
	 * Aktualisieren der Items der Abruf-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboAbruf() throws Exception {
		IField fieldAbruf;
		CoAbruf coAbruf;
		
		coAbruf = new CoAbruf();
		coAbruf.loadItems(m_coMonatseinsatzblattProjekt.getKundeID(),
			m_coMonatseinsatzblattProjekt.getAuftragID(), m_coMonatseinsatzblattProjekt.getKostenstelleID());
		
		fieldAbruf = m_coMonatseinsatzblattProjekt.getFieldAbruf();
		
		refreshItems(m_comboAbruf, fieldAbruf, coAbruf);
	}
	

	/**
	 * Aktualisieren der Items der Kostenstellen-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboKostenstelle() throws Exception {
		IField fieldKostenstelle;
		CoKostenstelle coKostenstelle;
		
		coKostenstelle = new CoKostenstelle();
		coKostenstelle.loadItems(m_coMonatseinsatzblattProjekt.getKundeID(),
				m_coMonatseinsatzblattProjekt.getAuftragID(), m_coMonatseinsatzblattProjekt.getAbrufID(), m_coMonatseinsatzblattProjekt.getBerichtsNrID());
		
		fieldKostenstelle = m_coMonatseinsatzblattProjekt.getFieldKostenstelle();
		
		refreshItems(m_comboKostenstelle, fieldKostenstelle, coKostenstelle);
	}


	/**
	 * Aktualisieren der Items der Kostenstellen-Combo
	 * 
	 * @throws Exception
	 */
	private void refreshItemsComboBerichtsNr() throws Exception {
		IField fieldBerichtsNr;
		CoBerichtsNr coBerichtsNr;
		
		coBerichtsNr = new CoBerichtsNr();
		coBerichtsNr.loadItems(m_coMonatseinsatzblattProjekt.getKundeID(),
				m_coMonatseinsatzblattProjekt.getAuftragID(), m_coMonatseinsatzblattProjekt.getAbrufID(), m_coMonatseinsatzblattProjekt.getKostenstelleID());
		
		fieldBerichtsNr = m_coMonatseinsatzblattProjekt.getFieldBerichtsNrID();
		
		refreshItems(m_comboBerichtsNr, fieldBerichtsNr, coBerichtsNr);
	}


	/**
	 * Items aktualisieren, neue Items setzen wenn noch keine vorhanden sind oder sie ungültig sind
	 * 
	 * @param comboControl
	 * @param field
	 * @param co
	 * @throws Exception
	 */
	private void refreshItems(ComboControl comboControl, IField field, AbstractCacheObject co) throws Exception {
		if (comboControl.equals(m_comboErsteAuswahl))
		{
			return;
		}
		
		if (!checkItems(field, co))
		{
			UniFormWithSaveLogic.refreshItems(comboControl, co, field);
		}
		
		updateItem(field, co);
	}


	/**
	 * Items einer Combobox prüfen.<br>
	 * 
	 * @param field
	 * @param co
	 * @return noch keine Items vorhanden oder sie sind ungültig
	 */
	private boolean checkItems(IField field, AbstractCacheObject co) {
		
		if (field.getValue() == null)
		{
			return false;
		}
		else if (!co.moveToID(Format.getIntValue(field.getValue())))
		{
			return false;
		}
		
		return true;
	}


	/**
	 * Items einer Combobox aktualisieren.<br>
	 * Wenn nur ein Element vorhanden ist, wähle es aus.<br>
	 * Sonst prüfen, ob der ausgewählte Wert noch in der Comobox existiert.
	 * 
	 * @param field
	 * @param co
	 */
	private void updateItem(IField field, AbstractCacheObject co) {
		// Element nicht markieren, wenn es nur eine Kostenstelle für den Kunden gibt und kein zur Kostenstelle gehörender Abruf ausgewählt ist
		if (co.getRowCount() == 1 
				&& (!(co instanceof CoKostenstelle) || m_comboAbruf.getField().getValue() != null || m_comboBerichtsNr.getField().getValue() != null))
		{
			field.setValue(co.getID());
		}
		else
		{
			if (!co.moveToID(Format.getIntValue(field.getValue())))
			{
				field.setValue(null);
			}
		}
	}
	

	/**
	 * Auswahl des Kunden aktualisieren
	 * 
	 * @param auftragID
	 * @throws Exception
	 */
	private void updateKunde(int auftragID) throws Exception {
		CoAuftrag coAuftrag;
		
		coAuftrag = new CoAuftrag();
		coAuftrag.loadByID(auftragID);
		
		if (coAuftrag.getRowCount() > 0 && coAuftrag.getKundeID() > 0)
		{
			m_coMonatseinsatzblattProjekt.setKundeID(coAuftrag.getKundeID());
			updateItem(m_coMonatseinsatzblattProjekt.getFieldKundeID(), CoKunde.getInstance());
		}
	}
	
	
	public CoMonatseinsatzblattProjekt getCoMonatseinsatzblattProjekt(){
		return m_coMonatseinsatzblattProjekt;
	}
	
	

//	/**
//	 * Nur einzelne Felder sind editierbar.
//	 * 
//	 * @see framework.cui.controls.base.BaseCompositeControl#refresh(int, java.lang.Object)
//	 */
//	@Override
//	public void refresh(int reason, Object element) {
//		super.refresh(reason, element);
//		
//		// per default alles deaktivieren
////		super.refresh(reasonEnabled, null);
//	}


}


