package pze.business.objects.projektverwaltung;

import java.util.Date;

import framework.Application;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattPhasen;

/**
 * CacheObject für die Zuordnung von Personen zu Projekten.<br>
 * 
 * @author Lisiecki
 *
 */
public class CoArbeitsplan extends CoMitarbeiterProjekt {

	public static final String TABLE_NAME = "tblarbeitsplan";
	

	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoArbeitsplan(CoProjekt coProjekt) throws Exception {
		super("table." + TABLE_NAME, coProjekt);
	}


	/**
	 * Laden für ein ausgewähltes Projekt im Monatseinsatzblatt
	 * 
	 * @param virtCoProjekt
	 * @throws Exception
	 */
	public void loadByProjekt(VirtCoProjekt virtCoProjekt) throws Exception {
		int abrufID;
		String whereprojekt;
		
		abrufID = virtCoProjekt.getAbrufID();
		whereprojekt = abrufID > 0 ? " AbrufID=" + abrufID : " AuftragID=" + virtCoProjekt.getAuftragID();
		
		loadByProjekt(whereprojekt);
	}
	

	/**
	 * Laden aller Phasen-Items für das Projekt
	 * 
	 * @throws Exception
	 */
	public void loadItems() throws Exception {
		String whereprojekt;
		
		whereprojekt = (m_coProjekt instanceof CoAuftrag ? " AuftragID=" + m_coProjekt.getID() : " AbrufID=" + m_coProjekt.getID());

		emptyCache();
		Application.getLoaderBase().load(this, whereprojekt + " AND Phase is NOT NULL" , getSortFieldName());

	}


//	/**
//	 * Ist-Zeit für die aktuellen Angaben bestimmen
//	 * 
//	 * @return
//	 * @throws Exception
//	 */
//	private int loadWertIstZeit() throws Exception { // TODO muss noch angepasst werden
//		return loadWertIstZeit((m_coProjekt instanceof CoAuftrag ? " AuftragID=" + m_coProjekt.getID() : " AbrufID=" + m_coProjekt.getID()));
//	}
	

	/**
	 * Ist-Zeit für die aktuellen Angaben bestimmen
	 * 
	 * @param whereprojekt
	 * @return
	 * @throws Exception
	 */
	@Override
	protected int loadWertIstZeit(String whereprojekt) throws Exception {
		CoMonatseinsatzblattPhasen coMonatseinsatzblattPhasen;
		coMonatseinsatzblattPhasen = new CoMonatseinsatzblattPhasen();

		coMonatseinsatzblattPhasen.loadSummeStunden(getID());
		
		return coMonatseinsatzblattPhasen.getWertZeit();
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
//		CoArbeitsplan coMitarbeiterProjekt;
//		
//		coMitarbeiterProjekt = new CoArbeitsplan();
//		Application.getLoaderBase().load(coMitarbeiterProjekt, 
//				(auftragID == 0 ? "" : " AuftragID=" + auftragID)
//				+ (abrufID == 0 ? "" : " AbrufID=" + abrufID ) + " AND PersonID=" + personID, null);
//		
//		return coMitarbeiterProjekt.hasRows();
//	}
	

	/**
	 * Neuen Arbeitsplan (für neues Projekt) erstellen
	 * 
	 * @param coProjekt
	 * @throws Exception
	 */
	public void createNewArbeitsplan() throws Exception { // TODO Arbeitsplan automatisch erzeugen?
		Date datum;
		
		// Phase Bearbeitung erstellen
		createNew();
		setPhase("Bearbeitung");

		// Liefertermin eintragen, wenn vorhanden
		datum = m_coProjekt.getLiefertermin();
		if (datum != null)
		{
			createNew();
			setMeilenstein("Liefertermin");
		}
	}


//	/**
//	 * Neuen Datensatz erstellen
//	 * 
//	 * @param coProjekt
//	 * @throws Exception
//	 */
//	@Override
//	public int createNew() throws Exception {
//		int id;
//		
//		id = super.createNew();
//
//		if (m_coProjekt instanceof CoAuftrag)
//		{
//			setAuftragID(m_coProjekt.getID());
//		}
//
//		if (m_coProjekt instanceof CoAbruf)
//		{
//			setAbrufID(m_coProjekt.getID());
//		}
//
//		return id;
//	}


	private IField getFieldPhase() {
		return getField("field." + getTableName() + ".phase");
	}


	private String getPhase() {
		return Format.getStringValue(getFieldPhase());
	}


	private void setPhase(String phase) {
		getFieldPhase().setValue(phase);
	}


	public boolean isPhase() {
		return checkEingabe(getPhase()) != null;
	}

	
	private IField getFieldMeilenstein() {
		return getField("field." + getTableName() + ".meilenstein");
	}


	private String getMeilenstein() {
		return Format.getStringValue(getFieldMeilenstein());
	}


	private void setMeilenstein(String meilenstein) {
		getFieldMeilenstein().setValue(meilenstein);
	}


	public boolean isMeilenstein() {
		return checkEingabe(getMeilenstein()) != null;
	}


	/**
	 * Prüft einen eingegebenen String, ob er leer ist
	 * 
	 * @param value
	 * @return
	 */
	private String checkEingabe(String value) {
		if (value != null && value.trim().isEmpty())
		{
			return null;
		}
		return value;
	}


	/**
	 * Prüft, ob die Tätigkeit für alle Felder eingegeben wurde
	 */
	public String validate() throws Exception{
		int wertZeit, wertIstZeit, summe, max;
		boolean isPhase, isMeilenstein, hasPhase, hasMeilenstein;
		String phase, meilenstein;
		Date datumHeute, datumVon, datumBis;
		
		
		if (!moveFirst())
		{
			return null;
		}
		
		summe = 0;
		hasPhase = false;
		hasMeilenstein = false;
		datumHeute = Format.getDate0Uhr(new Date());

		// Eingaben durchlaufen
		do
		{
			wertZeit = getWertZeit();
			datumVon = getDatum();
			datumBis = getDatumBis();
			wertIstZeit = loadWertIstZeit();
			
			isPhase = isPhase();
			isMeilenstein = isMeilenstein();
			phase = checkEingabe(getPhase());
			meilenstein = checkEingabe(getMeilenstein());
			
			// Summe der Stunden nur bei Phasen zählen
			if (isPhase())
			{
				summe += wertZeit;
			}

			// Kein Datum in der Vergangenheit
			if ((datumVon != null && datumVon.before(datumHeute) && !datumVon.equals(getFieldDatum().getOriginalValue()))
					|| (datumBis != null && datumBis.before(datumHeute) && !datumBis.equals(getFieldDatumBis().getOriginalValue())))
			{
				return "Das Datum darf nicht in der Vergangenheit liegen.";
			}
			
			// Reihenfolge Datum von/bis
			if (datumVon != null && datumBis != null && datumVon.after(datumBis))
			{
				return "Das Enddatum darf nicht vor dem Beginn liegen.";
			}
			
			// Budget darf nicht kleiner als Ist-Zeit sein
			if (getFieldWertZeit().getValue() != null && wertZeit < wertIstZeit)
			{
				return "Das Budget darf nicht kleiner als die Ist-Stunden sein.";
			}
			
			// Phase/Meilenstein
			if (!isPhase && !isMeilenstein)
			{
				return "Es muss je Eintrag eine Eingabe für eine Phase/Arbeitsaufgabe oder einen Meilenstein gemacht werden.";
			}
			if (isPhase && isMeilenstein)
			{
				return "Es darf je Eintrag nur eine Eingabe für eine Phase/Arbeitsaufgabe oder einen Meilenstein gemacht werden.";
			}
			
			// es muss eine Phase und einen Meilenstein geben
			hasPhase = hasPhase || isPhase;
			hasMeilenstein = hasMeilenstein || isMeilenstein;
			
			// Änderungen dokumentieren
			if (getCurrentRow().getRowState() != IBusinessObject.statusUnchanged)
			{
				updateGeaendertVonAm();
			}
		} while (moveNext());

		
		// Summe der Stunden prüfen
		max = m_coProjekt.getBestellwert() - m_coProjekt.getStartwert();
		if (summe > max)
		{
			return "Die Summe der zugeteilten Stunden übersteigt den maximal möglichen Wert von " + Format.getZeitAsText(max) + ".";
		}
		
		// es muss eine Phase und einen Meilenstein geben
//		if (!hasPhase || !hasMeilenstein)
//		{
//			return "Es muss mindestens eine Phase und einen Meilenstein geben.";
//		}
		
		return null;
	}


	/**
	 * Prüft die Stundeneintragung im Monatseinsatzblatt
	 * 
	 * @param personID
	 * @param datum
	 * @param differenz
	 * @return
	 * @throws Exception
	 */
	public String check(Date datum, int differenz) throws Exception {
		Date datumVon, datumBis;
		
		datumVon = getDatum();
		datumBis = getDatumBis();
		
		// TODO hier noch weitere Zeiträume prüfen, wenn MA mehrfach vorkommt
		if ((datumVon != null && Format.getDate0Uhr(datumVon).after(datum)) 
				|| (datumBis != null && !datum.before(Format.getDate0Uhr(Format.getDateVerschoben(datumBis, 1)))))
		{
			return "Das Projekt " + m_coProjekt.getBezeichnung() + " ist für Sie am " + Format.getString(datum) + " nicht freigegeben.";
		}
		
		if (!isEditing())
		{
			begin();
		}
		
		// eingetragene Stunden summieren
		setWertIstZeit(getWertIstZeit() + differenz);
		
		
		return null;
	}
	

	/**
	 * Prüft, ob auf das Projekt noch Stunden gebucht werden dürfen
	 * @param personID 
	 * 
	 * @return Zeit in Minuten, um die das Kontingent überschritten wurde<br> < 0 bedeutet Kontingent ist nicht überschritten<br>= 0 bedeutet 100 % 
	 */
	public int getBudgetUeberschreitung(int personID) {
		if (moveToPersonID(personID) && getFieldWertZeit().getValue() != null)			// TODO hier noch weitere Zeiträume prüfen, wenn MA mehrfach vorkommt
		{
			return getWertIstZeit() - getWertZeit();
		}
		
		return 0;
	}


}
