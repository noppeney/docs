package pze.business.objects.personen.monatseinsatzblatt;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.projektverwaltung.CoAbruf;
import pze.business.objects.projektverwaltung.CoKostenstelle;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.business.objects.reftables.CoStundenart;

/**
 * CacheObject für die Zuordnung von Projekten zu Monatseinatzblättern.<br>
 * Auswahl der anzuzeigenden Projekte wird vom Mitarbeiter erstellt.
 * 
 * @author Lisiecki
 *
 */
public class CoMonatseinsatzblattProjekt extends AbstractCacheObject {

	public static final String TABLE_NAME = "stblmonatseinsatzblattprojekt";



	/**
	 * Kontruktor
	 */
	public CoMonatseinsatzblattProjekt() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Laden von Abrufen in Abhängigkeit von ihrem Status und dem Auftrag
	 * 
	 * @param isAktiv aktiv=Status != abgerechnet
	 * @param AuftragID AuftragID oder 0
	 * @throws Exception
	 */
	public void load(int personID) throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, " PersonID=" + personID, getSortFieldName());
	}


	/**
	 * Projekts für die Person im Monatseinsatzblatt hinzufügen
	 * 
	 * @param personID
	 * @param coProjekt
	 * @throws Exception 
	 */
	public boolean addProjekt(int personID, CoProjekt coProjekt) throws Exception {
		int id;
		boolean isAbruf;
		CoKostenstelle coKostenstelle;

		id = coProjekt.getID();
		isAbruf = (coProjekt instanceof CoAbruf ? true : false);

		// Daten laden
		emptyCache();
		Application.getLoaderBase().load(this, " PersonID=" + personID + " AND " + (isAbruf ? "AbrufID" : "AuftragID") + "=" + id, getSortFieldName());
		
		// wenn das Projekt schon existiert, beende Methode
		if (hasRows())
		{
			return false;
		}
		
		// sonst füge es hinzu
		createNew();
		setPersonID(personID);
		setAuftragID(coProjekt.getAuftragID());
		if (isAbruf)
		{
			setAbrufID(id);
		}
		setKundeID(coProjekt.getKundeID());
		setStundenartID(CoStundenart.STATUSID_INGENIEURSTUNDEN);
		
		// Kostenstelle prüfen
		coKostenstelle = new CoKostenstelle();
		coKostenstelle.loadItems(coProjekt.getKundeID(), isAbruf ? 0 : id, isAbruf ? id : 0, 0);
		
		// wenn es mehr als eine Kostenstelle für die Auswahl gibt, kann das Projekt nicht hinzugefügt werden
		if (coKostenstelle.getRowCount() > 1)
		{
			return false;
		}
		if (coKostenstelle.getRowCount() == 1)
		{
			setKostenstelleID(coKostenstelle.getID());
		}
		
		// speichern
		save();
		return true;
	}


	public IField getFieldKundeID() {
		return getField("field." + getTableName() + ".kundeid");
	}


	public int getKundeID() {
		return Format.getIntValue(getFieldKundeID().getValue());
	}


	public void setKundeID(int kundeID) {
		getFieldKundeID().setValue(kundeID);
	}


	public IField getFieldAuftragID() {
		return getField("field." + getTableName() + ".auftragid");
	}


	public void setAuftragID(int auftragID) {
		getFieldAuftragID().setValue(auftragID);
	}


	public int getAuftragID() {
		return Format.getIntValue(getFieldAuftragID().getValue());
	}


	public IField getFieldAbruf() {
		return getField("field." + getTableName() + ".abrufid");
	}


	public void setAbrufID(int abrufID) {
		getFieldAbruf().setValue(abrufID);
	}


	public int getAbrufID() {
		return Format.getIntValue(getFieldAbruf().getValue());
	}


	public IField getFieldKostenstelle() {
		return getField("field." + getTableName() + ".kostenstelleid");
	}


	public void setKostenstelleID(int kostenstelleID) {
		getFieldKostenstelle().setValue(kostenstelleID);
	}


	public int getKostenstelleID() {
		return Format.getIntValue(getFieldKostenstelle().getValue());
	}


	public IField getFieldBerichtsNrID() {
		return getField("field." + getTableName() + ".berichtsnrid");
	}


	public void setBerichtsNrID(int berichtsNrID) {
		getFieldBerichtsNrID().setValue(berichtsNrID);
	}


	public int getBerichtsNrID() {
		return Format.getIntValue(getFieldBerichtsNrID());
	}


	public IField getFieldStundenart() {
		return getField("field." + getTableName() + ".stundenartid");
	}


	public void setStundenartID(int stundenartID) {
		getFieldStundenart().setValue(stundenartID);
	}


	public int getStundenartID() {
		return Format.getIntValue(getFieldStundenart().getValue());
	}


	public IField getFieldBemerkung() {
		return getField("field." + getTableName() + ".bemerkung");
	}


	public String getBemerkung() {
		return Format.getStringValue(getFieldBemerkung().getValue());
	}


	public void setBemerkung(String bemerkung) {
		getFieldBemerkung().setValue(bemerkung);
	}


	/**
	 * Zum Datensatz mit den übergebenen Projekt springen
	 * 
	 * @param virtCoProjekt
	 * @return Datensatz vorhanden
	 * @throws Exception 
	 */
	public boolean moveTo(VirtCoProjekt virtCoProjekt) throws Exception {
		return moveTo(virtCoProjekt.getAuftragID(), virtCoProjekt.getAbrufID(), virtCoProjekt.getKostenstelleID(), virtCoProjekt.getBerichtsNrID(),
				virtCoProjekt.getStundenartID());
	}


	/**
	 * Zum Datensatz mit den übergebenen Werten springen
	 * 
	 * @param auftragID
	 * @param abrufID
	 * @param kostenstelleID
	 * @param stundenartID 
	 * @return Datensatz vorhanden
	 * @throws Exception 
	 */
	private boolean moveTo(int auftragID, int abrufID, int kostenstelleID, int berichtsNrID, int stundenartID) throws Exception {
	
		if (moveFirst())
		{
			do
			{
				if (getAuftragID() != auftragID)
				{
					continue;
				}

				if (getAbrufID() != abrufID)
				{
					continue;
				}

				if (getKostenstelleID() != kostenstelleID)
				{
					continue;
				}

				if (getBerichtsNrID() != berichtsNrID)
				{
					continue;
				}

				if (getStundenartID()!= stundenartID)
				{
					continue;
				}

				return true;
			} while (moveNext());
		}

		return false;
	}


	/**
	 * Datensatz mit den übergebenen Projekt löschen
	 * 
	 * @param virtCoProjekt
	 * @throws Exception 
	 */
	public void delete(VirtCoProjekt virtCoProjekt) throws Exception {
		// es können mehrere Einträge für das Projekt existieren, daher while-Schleife
		while (moveTo(virtCoProjekt))
		{
			if (!isEditing())
			{
				begin();
			}
			
			delete();
			save();
		}
		
	}



}
