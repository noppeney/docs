package pze.business.objects.projektverwaltung;

import java.util.Date;

import framework.Application;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.reftables.projektverwaltung.CoAktionProjektverfolgung;
import pze.business.objects.reftables.projektverwaltung.CoStatusAenderungProjekt;
import pze.business.objects.reftables.projektverwaltung.CoStatusProjektverfolgung;
import pze.ui.formulare.projektverwaltung.FormProjektProjektverfolgung;

/**
 * CacheObject für die Zuordnung von Personen zu Projekten.<br>
 * 
 * @author Lisiecki
 *
 */
public class CoProjektverfolgung extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblprojektverfolgung";

	protected CoProjekt m_coProjekt;
	private FormProjektProjektverfolgung m_formProjektProjektverfolgung;
	

	/**
	 * Kontruktor
	 * @param formProjektProjektverfolgung 
	 * @throws Exception 
	 */
	public CoProjektverfolgung(CoProjekt coProjekt, FormProjektProjektverfolgung formProjektProjektverfolgung) throws Exception {
		this("table." + TABLE_NAME, coProjekt, formProjektProjektverfolgung);
	}
	

	/**
	 * Kontruktor
	 * @param formProjektProjektverfolgung 
	 * @throws Exception 
	 */
	private CoProjektverfolgung(String tableResID, CoProjekt coProjekt, FormProjektProjektverfolgung formProjektProjektverfolgung) throws Exception {
		super(tableResID);
		
		m_coProjekt = coProjekt;
		m_formProjektProjektverfolgung = formProjektProjektverfolgung;
	}

	
	/**
	 * Laden aller Daten für das Projekt
	 * 
	 * @param checkPl nur alle Daten laden, wenn die Person PL ist, sonst nur die des MA oder für alle Personen laden
	 * @throws Exception
	 */
	public void loadByProjekt() throws Exception {
		int projektleiterID;
		String whereprojekt;
		
		whereprojekt = (m_coProjekt instanceof CoAuftrag ? " AuftragID=" + m_coProjekt.getID() : " AbrufID=" + m_coProjekt.getID());

		// ggf. nur laden, wenn es der PL ist
		if (m_formProjektProjektverfolgung != null && m_formProjektProjektverfolgung.isModusPL())
		{
			projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();
			whereprojekt += 
					// wenn keine allgemeinen Auswertungen erlaubt sind, nur die eigenen Daten laden
					projektleiterID > 0 && (projektleiterID != m_coProjekt.getProjektleiterID() && projektleiterID != m_coProjekt.getProjektleiterID2()) 
							? " AND PersonID=" + projektleiterID : "";
		}

		loadByProjekt(whereprojekt);
	}
	
	
	/**
	 * Laden aller Daten für das Projekt
	 * 
	 * @param whereprojekt
	 * @throws Exception
	 */
	public void loadByProjekt(String whereprojekt) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, whereprojekt, getSortFieldName());

//		addField(getResIdWertIstZeit());

		
		if (!moveFirst())
		{
			return;
		}
		
		
//		begin();
//		do 
//		{
//			setWertIstZeit(loadWertIstZeit(whereprojekt));
//		} while (moveNext());
//		commit();
	}
	
	
	/**
	 * Laden der Änderungen  für die eine Meldung erzeugt wurde
	 * 
	 * @param whereprojekt
	 * @throws Exception
	 */
	public void loadByMeldungID(int meldungID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "MeldungID=" + meldungID, getSortFieldName());
	}
	
//	
//	/**
//	 * Prüft, ob der MA dem Projekt zugeordnet ist
//	 * 
//	 * @param auftragID
//	 * @param abrufID ID oder 0
//	 * @param personID ID oder 0
//	 * @throws Exception
//	 */
//	public static boolean isZugeordnet(int auftragID, int abrufID, int personID) throws Exception {
//		CoProjektverfolgung coMitarbeiterProjekt;
//		
//		coMitarbeiterProjekt = new CoProjektverfolgung(null, null);
//		Application.getLoaderBase().load(coMitarbeiterProjekt, 
//				(auftragID == 0 ? "" : " AuftragID=" + auftragID)
//				+ (abrufID == 0 ? "" : " AbrufID=" + abrufID ) + " AND PersonID=" + personID, null);
//		
//		return coMitarbeiterProjekt.hasRows();
//	}
//	

	private IField getFieldAuftrag() {
		return getField("field." + getTableName() + ".auftragid");
	}


	private void setAuftragID(int auftragID) {
		getFieldAuftrag().setValue(auftragID);
	}


//	private int getAuftragID() {
//		return Format.getIntValue(getFieldAuftrag().getValue());
//	}


	private IField getFieldAbruf() {
		return getField("field." + getTableName() + ".abrufid");
	}


	private void setAbrufID(int abrufID) {
		getFieldAbruf().setValue(abrufID);
	}


//	private int getAbrufID() {
//		return Format.getIntValue(getFieldAbruf().getValue());
//	}


	private IField getFieldKosten() {
		return getField("field." + getTableName() + ".kosten");
	}


	private int getKosten() {
		return Format.getIntValue(getFieldKosten().getValue());
	}


	private void setKosten(int kosten) {
		getFieldKosten().setValue(kosten);
	}


	public IField getFieldTermin() {
		return getField("field." + getTableName() + ".termin");
	}


	public Date getTermin() {
		return Format.getDateValue(getFieldTermin());
	}


	private void setTermin(Date datum) {
		getFieldTermin().setValue(datum);
	}


	private IField getFieldIstStunden() {
		return getField("field." + getTableName() + ".iststunden");
	}


	private int getIstStunden() {
		return Format.getIntValue(getFieldIstStunden());
	}


	private void setIstStunden(int stunden) {
		getFieldIstStunden().setValue(stunden);
	}


	public double getVerbrauch() {
		return getIstStunden()/(1.*getKosten());
	}


	public IField getFieldKostenPL() {
		return getField("field." + getTableName() + ".kostenpl");
	}


	public int getKostenPL() {
		return Format.getIntValue(getFieldKostenPL());
	}


	private void setKostenPL(int kosten) {
		getFieldKostenPL().setValue(kosten);
	}


	public IField getFieldTerminPL() {
		return getField("field." + getTableName() + ".terminpl");
	}


	public Date getTerminPL() {
		return Format.getDateValue(getFieldTerminPL());
	}


	private void setTerminPL(Date datum) {
		getFieldTerminPL().setValue(datum);
	}


	private IField getFieldPLID() {
		return getField("field." + getTableName() + ".plid");
	}


	private void setPLID(int personID) {
		if (personID > 0)
		{
			getFieldPLID().setValue(personID);
		}
	}


	public int getPLID() {
		return Format.getIntValue(getFieldPLID());
	}


	private IField getFieldStatusIDPL() {
		return getField("field." + getTableName() + ".statusprojektverfolgungidpl");
	}


	public int getStatusIDPL() {
		return Format.getIntValue(getFieldStatusIDPL());
	}


	private void setStatusIDPL(int statusID) {
		if (getStatusIDPL() != statusID)
		{
			getFieldStatusIDPL().setValue(statusID);
			
			// wenn der Status geändert wird, Status der Änderung anpassen
			if (statusID == CoStatusProjektverfolgung.STATUSID_OK)
			{
				setStatusAenderungIDPL(CoStatusAenderungProjekt.STATUSID_KEINE_AENDERUNG);
			}
			else // bei Änderungen muss der Status ausgewählt werden
			{
				getFieldStatusAenderungIDPL().setValue(null);
			}
		}
	}

	
	public IField getFieldStatusAenderungIDPL() {
		return getField("field." + getTableName() + ".statusaenderungidpl");
	}


	private int getStatusAenderungIDPL() {
		return Format.getIntValue(getFieldStatusAenderungIDPL());
	}


	private void setStatusAenderungIDPL(int statusID) {
		getFieldStatusAenderungIDPL().setValue(statusID);
	}


	private IField getFieldGeaendertAmPL() {
		return getField("field." + getTableName() + ".geaendertampl");
	}


//	public Date getGeaendertAmPL() {
//		return Format.getDateValue(getFieldGeaendertAmPL().getValue());
//	}


	private void setGeaendertAmPL(String datum) {
		getFieldGeaendertAmPL().setValue(datum);
	}


	public IField getFieldBemerkungPL() {
		return getField("field." + getTableName() + ".bemerkungpl");
	}


	public IField getFieldKostenAL() {
		return getField("field." + getTableName() + ".kostenal");
	}


	public int getKostenAL() {
		return Format.getIntValue(getFieldKostenAL());
	}


	private void setKostenAL(int kosten) {
		getFieldKostenAL().setValue(kosten);
	}


	public IField getFieldTerminAL() {
		return getField("field." + getTableName() + ".terminal");
	}


	public Date getTerminAL() {
		return Format.getDateValue(getFieldTerminAL());
	}

	
	private void setTerminAL(Date datum) {
		getFieldTerminAL().setValue(datum);
	}


	private IField getFieldALID() {
		return getField("field." + getTableName() + ".alid");
	}


	private void setALID(int personID) {
		if (personID > 0)
		{
			getFieldALID().setValue(personID);
		}
	}


	public int getALID() {
		return Format.getIntValue(getFieldALID());
	}


	private IField getFieldStatusIDAL() {
		return getField("field." + getTableName() + ".statusprojektverfolgungidal");
	}


	public int getStatusIDAL() {
		return Format.getIntValue(getFieldStatusIDAL());
	}


	private void setStatusIDAL(int statusID) {
		if (getStatusIDAL() != statusID)
		{
			getFieldStatusIDAL().setValue(statusID);
			
			// wenn der Status geändert wird, Status der Änderung anpassen
			if (statusID == CoStatusProjektverfolgung.STATUSID_OK)
			{
				setStatusAenderungIDAL(CoStatusAenderungProjekt.STATUSID_KEINE_AENDERUNG);
			}
			else // bei Änderungen muss der Status ausgewählt werden
			{
				getFieldStatusAenderungIDAL().setValue(null);
			}
		}
	}


	public IField getFieldStatusAenderungIDAL() {
		return getField("field." + getTableName() + ".statusaenderungidal");
	}


	private int getStatusAenderungIDAL() {
		return Format.getIntValue(getFieldStatusAenderungIDAL());
	}


	private void setStatusAenderungIDAL(int statusID) {
		getFieldStatusAenderungIDAL().setValue(statusID);
	}


	private IField getFieldGeaendertAmAL() {
		return getField("field." + getTableName() + ".geaendertamal");
	}


	private void setGeaendertAmAL(String datum) {
		getFieldGeaendertAmAL().setValue(datum);
	}


	public IField getFieldBemerkungAL() {
		return getField("field." + getTableName() + ".bemerkungal");
	}


	public IField getFieldAktionID() {
		return getField("field." + getTableName() + ".aktionid");
	}


	public int getAktionID() {
		return Format.getIntValue(getFieldAktionID());
	}


	private IField getFieldMeldungID() {
		return getField("field." + getTableName() + ".meldungid");
	}


//	public int getMeldungID() {
//		return Format.getIntValue(getFieldMeldungID());
//	}


	public void setMeldungID(int meldungID) {
		getFieldMeldungID().setValue(meldungID);
	}

	
	/**
	 * Status OK/geändert prüfen
	 * 
	 * @throws Exception
	 */
	public void updateStatusIDPL() throws Exception {
		setStatusIDPL(((getTermin() == null || getTermin().equals(getTerminPL())) && getKosten() == getKostenPL()) 
				? CoStatusProjektverfolgung.STATUSID_OK : CoStatusProjektverfolgung.STATUSID_GEAENDERT);
	}

	
	/**
	 * Status OK/geändert prüfen
	 * 
	 * @throws Exception
	 */
	public void updateStatusIDAL() throws Exception {
		setStatusIDAL(((getTermin() == null || getTermin().equals(getTerminAL())) && getKosten() == getKostenAL()) 
				? CoStatusProjektverfolgung.STATUSID_OK : CoStatusProjektverfolgung.STATUSID_GEAENDERT);
	}

	
	/**
	 * Änderung dokumentieren
	 * 
	 * @throws Exception
	 */
	private void updateGeaendertVonAmPL() throws Exception {
		setPLID(UserInformation.getPersonID());
//		setGeaendertAmPL(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
		setGeaendertAmPL(Format.getString(new Date()));
	}


	/**
	 * Änderung dokumentieren
	 * 
	 * @throws Exception
	 */
	private void updateGeaendertVonAmAL() throws Exception {
		setALID(UserInformation.getPersonID());
//		setGeaendertAmAL(Format.getStringMitUhrzeit(Format.getGregorianCalendar(null)));
		setGeaendertAmAL(Format.getString(new Date()));
	}


	/**
	 * Neuen Datensatz erstellen
	 * 
	 * @param coProjekt
	 * @throws Exception
	 */
	public int createNew() throws Exception {
		int id, personID;
		CoProjekt coProjekt;
		
		id = super.createNew();
		personID = UserInformation.getPersonID();
		coProjekt = null;
		
		// Projekt neu laden, falls sich etwas geändert hat
		if (m_coProjekt instanceof CoAuftrag)
		{
			setAuftragID(m_coProjekt.getID());
			coProjekt = new CoAuftrag();
		}
		else if (m_coProjekt instanceof CoAbruf)
		{
			setAbrufID(m_coProjekt.getID());
			coProjekt = new CoAbruf();
		}
		coProjekt.loadByID(m_coProjekt.getID());
		
		// aktuelle Daten
		setDatum(Format.getDate12Uhr(new Date()));
		setKosten(coProjekt.getBestellwert());
		setTermin(coProjekt.getLiefertermin());
		setIstStunden(coProjekt.getIstStunden());

		// PL oder AL
		if (m_formProjektProjektverfolgung.isModusAL() || personID == coProjekt.getAbteilungsleiterID())
		{
			setTerminAL(getTermin());
			setKostenAL(getKosten());
			updateStatusIDAL();
			updateGeaendertVonAmAL();

			// Modus speichern, falls nicht aus einer Message gestartet wurde
			m_formProjektProjektverfolgung.setModusAL();
		}
		else if (m_formProjektProjektverfolgung.isModusPL() || personID == coProjekt.getProjektleiterID() || personID == coProjekt.getProjektleiterID2())
		{
			setTerminPL(getTermin());
			setKostenPL(getKosten());
			updateStatusIDPL();
			updateGeaendertVonAmPL();
			
			// Modus speichern, falls nicht aus einer Message gestartet wurde
			m_formProjektProjektverfolgung.setModusPL();
		}

		return id;
	}


	/**
	 * Letzten Eintrag für Prüfung durch AL vorbereiten
	 * @return 
	 * 
	 * @throws Exception 
	 */
	public boolean updateAL() throws Exception {
		// wenn es keinen Eintrag durch den PL gibt, beende Methode
		if (!moveLast() || getPLID() == 0 || getALID() > 0)
		{
			return false;
		}
		
		// Angaben vom PL übernehmen
		if (!isEditing())
		{
			begin();
		}
		setTerminAL(getTerminPL());
		setKostenAL(getKostenPL());
		updateStatusIDAL();
		getFieldStatusAenderungIDAL().setValue(getFieldStatusAenderungIDPL().getValue());
		updateGeaendertVonAmAL();
		
		return true;
	}
	

	/**
	 * Prüft, ob die Tätigkeit für alle Felder eingegeben wurde
	 */
	public String validate() throws Exception{
		if (!moveFirst())
		{
			return null;
		}

		// Einträge durchlaufen
		do
		{
			// es werden nur geänderte Einträge geprüft
			if (getCurrentRow().getRowState() == IBusinessObject.statusUnchanged)
			{
				continue;
			}

			// AL
			if (m_formProjektProjektverfolgung.isModusAL())
			{
				// Status Änderung muss ausgwewählt sein
				if (getStatusAenderungIDAL() == 0)
				{
					return "Es muss ein Status der Änderung ausgewählt werden.";
				}

				// Kombination Status und Status Änderung
				if ((getStatusAenderungIDAL() == CoStatusAenderungProjekt.STATUSID_KEINE_AENDERUNG) != (getStatusIDAL() == CoStatusProjektverfolgung.STATUSID_OK))
				{
					return "Status Prognose und Status Änderung passen nicht zusammen.";
				}
				
				// Kombination Status und Aktion
				if (getStatusIDAL() == CoStatusProjektverfolgung.STATUSID_OK && getAktionID() == CoAktionProjektverfolgung.STATUSID_AENDERUNGEN_UEBERNEHMEN)
				{
					return "Status Prognose und Aktion passen nicht zusammen.";
				}
				
				// Aktion muss ausgewählt sein
				if (getAktionID() == 0)
				{
					return "Es muss eine Aktion ausgewählt werden.";
				}


				// Nachfrage bei geänderter Prognose
//				if (getStatusAenderungIDAL() != CoStatusAenderungProjekt.STATUSID_KEINE_AENDERUNG)
				{
					// Aktion an BH weitergeben
					if (getAktionID() != CoAktionProjektverfolgung.STATUSID_KEINE_AKTION)
					{
						if (!Messages.showYesNoMessage("Aktion zur Bearbeitung an Buchhaltung senden", 
								"Soll die Aktion '" + getFieldAktionID().getDisplayValue() + "' zur Freigabe an die Buchhaltung gesendet werden?."))
						{
							return "Bearbeitung abgebrochen";
						}
					}
				}
			}
			// PL
			else if (m_formProjektProjektverfolgung.isModusPL())
			{
				// Status Änderung muss ausgwewählt sein
				if (getStatusAenderungIDPL() == 0)
				{
					return "Es muss ein Status der Änderung ausgewählt werden.";
				}

				// Kombination Status und Status Änderung
				if ((getStatusAenderungIDPL() == CoStatusAenderungProjekt.STATUSID_KEINE_AENDERUNG) != (getStatusIDPL() == CoStatusProjektverfolgung.STATUSID_OK))
				{
					return "Status Prognose und Status Änderung passen nicht zusammen.";
				}

				// Nachfrage bei geänderter Prognose
				if (getStatusAenderungIDPL() != CoStatusAenderungProjekt.STATUSID_KEINE_AENDERUNG)
				{
					if (!Messages.showYesNoMessage("Änderung Prognose", "Soll die geänderte Prognose gespeichert und an den Abteilungsleiter weitergegeben werden?"))
					{
						return "Bearbeitung abgebrochen";
					}
				}
			}
				
			
		} while (moveNext());

		
		return null;
	}


}
