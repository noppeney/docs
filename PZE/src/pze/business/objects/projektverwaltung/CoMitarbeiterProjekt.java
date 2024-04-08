package pze.business.objects.projektverwaltung;

import java.util.Date;

import framework.Application;
import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblatt;
import startup.PZEStartupAdapter;

/**
 * CacheObject für die Zuordnung von Personen zu Projekten.<br>
 * 
 * @author Lisiecki
 *
 */
public class CoMitarbeiterProjekt extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblmitarbeiterprojekt";

	protected CoProjekt m_coProjekt;
	

	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoMitarbeiterProjekt(CoProjekt coProjekt) throws Exception {
		this("table." + TABLE_NAME, coProjekt);
	}
	

	/**
	 * Kontruktor
	 * @throws Exception 
	 */
	public CoMitarbeiterProjekt(String tableResID, CoProjekt coProjekt) throws Exception {
		super(tableResID);
		
		m_coProjekt = coProjekt;
		
		// TODO nur aktive Personen anzeigen
			
		// Items aktualisieren
		updateItemsPhasen();
	}


	/**
	 * Items für Phasen des Arbeitsplans aktualisieren
	 * 
	 * @throws Exception
	 */
	public void updateItemsPhasen() throws Exception {
		IField field;
		CoArbeitsplan coArbeitsplan;
		
		// prüfen, ob das Feld vorhanden ist
		field = getFieldArbeitsplan();
		if (field == null
				|| !PZEStartupAdapter.MODUS_ARBEITSPLAN
				)
		{
			return;
		}

		// Items für das Projekt laden
		coArbeitsplan = new CoArbeitsplan(m_coProjekt);
		coArbeitsplan.loadItems();
		coArbeitsplan.addEmtyItem();
		
		// Items setzen
		field.setItems(coArbeitsplan);
	}
	
	
	/**
	 * Laden aller Daten für das Projekt
	 * 
	 * @param checkPl nur alle Daten laden, wenn die Person PL ist, sonst nur die des MA oder für alle Personen laden
	 * @throws Exception
	 */
	public void loadByProjekt(boolean checkPl) throws Exception {
		int projektleiterID;
		String whereprojekt;
		
		whereprojekt = (m_coProjekt instanceof CoAuftrag ? " AuftragID=" + m_coProjekt.getID() : " AbrufID=" + m_coProjekt.getID());

		// ggf. nur laden, wenn es der PL ist
		if (checkPl)
		{
			projektleiterID = UserInformation.getInstance().getPersonIDAlsProjektleiter();
			whereprojekt += 
					// wenn keine allgemeinen Auswertungen erlaubt sind, nur die eigenen daten laden
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
		removeField(getResIdWertIstZeit());
		emptyCache();
		Application.getLoaderBase().load(this, whereprojekt, getSortFieldName());

		addField(getResIdWertIstZeit());

		
		if (!moveFirst())
		{
			return;
		}
		
		
		begin();
		do 
		{
			setWertIstZeit(loadWertIstZeit(whereprojekt));
		} while (moveNext());
		commit();
	}


	/**
	 * Ist-Zeit für die aktuellen Angaben bestimmen
	 * 
	 * @return
	 * @throws Exception
	 */
	protected int loadWertIstZeit() throws Exception {
		return loadWertIstZeit((m_coProjekt instanceof CoAuftrag ? " AuftragID=" + m_coProjekt.getID() : " AbrufID=" + m_coProjekt.getID()));
	}
	

	/**
	 * Ist-Zeit für die aktuellen Angaben bestimmen
	 * 
	 * @param whereprojekt
	 * @return
	 * @throws Exception
	 */
	protected int loadWertIstZeit(String whereprojekt) throws Exception {
		CoMonatseinsatzblatt coMonatseinsatzblatt;
		coMonatseinsatzblatt = new CoMonatseinsatzblatt();

		coMonatseinsatzblatt.loadSummeStunden(getPersonID(), getDatum(), getDatumBis(), whereprojekt);
		
		return coMonatseinsatzblatt.getWertZeit();
	}
	

	/**
	 * Prüft, ob der MA dem Projekt zugeordnet ist
	 * 
	 * @param auftragID
	 * @param abrufID ID oder 0
	 * @param personID ID oder 0
	 * @throws Exception
	 */
	public static boolean isZugeordnet(int auftragID, int abrufID, int personID) throws Exception {
		CoMitarbeiterProjekt coMitarbeiterProjekt;
		
		coMitarbeiterProjekt = new CoMitarbeiterProjekt(null);
		Application.getLoaderBase().load(coMitarbeiterProjekt, 
				(auftragID == 0 ? "" : " AuftragID=" + auftragID)
				+ (abrufID == 0 ? "" : " AbrufID=" + abrufID ) + " AND PersonID=" + personID, null);
		
		return coMitarbeiterProjekt.hasRows();
	}
	
	
	@Override
	public String getKey(){
		return getTableName();
	}
	
	
	public CoProjekt getCoProjekt(){
		return m_coProjekt;
	}
	

	private IField getFieldArbeitsplan() {
		return getField("field." + getTableName() + ".arbeitsplanid");
	}


	private String getResIdFieldDatumBis() {
		return "field." + getTableName() + ".datumbis";
	}


	protected IField getFieldDatumBis() {
		return getField(getResIdFieldDatumBis());
	}


	public Date getDatumBis() {
		return Format.getDateValue(getFieldDatumBis());
	}


	private IField getFieldAuftrag() {
		return getField("field." + getTableName() + ".auftragid");
	}


	protected void setAuftragID(int auftragID) {
		getFieldAuftrag().setValue(auftragID);
	}


	private int getAuftragID() {
		return Format.getIntValue(getFieldAuftrag().getValue());
	}


	private IField getFieldAbruf() {
		return getField("field." + getTableName() + ".abrufid");
	}


	protected void setAbrufID(int abrufID) {
		getFieldAbruf().setValue(abrufID);
	}


	private int getAbrufID() {
		return Format.getIntValue(getFieldAbruf().getValue());
	}


	protected IField getFieldWertZeit() {
		return getField("field." + getTableName() + ".wertzeit");
	}


	public int getWertZeit() {
		return Format.getIntValue(getFieldWertZeit().getValue());
	}


	private String getResIdWertIstZeit() {
		return "virt.field." + getTableName() + ".wertistzeit";
	}


	public IField getFieldWertIstZeit() {
		return getField(getResIdWertIstZeit());
	}


	public int getWertIstZeit() {
		return Format.getIntValue(getFieldWertIstZeit().getValue());
	}


	protected void setWertIstZeit(Integer zeit) {
		getFieldWertIstZeit().setValue(zeit);
	}


	private IField getFieldTaetigkeit() {
		return getField("field." + getTableName() + ".taetigkeit");
	}


	private String getTaetigkeit() {
		return Format.getStringValue(getFieldTaetigkeit().getValue());
	}


	/**
	 * Neuen Datensatz erstellen
	 * 
	 * @param coProjekt
	 * @throws Exception
	 */
	public int createNew() throws Exception {
		int id;
		
		id = super.createNew();

		if (m_coProjekt instanceof CoAuftrag)
		{
			setAuftragID(m_coProjekt.getID());
		}

		if (m_coProjekt instanceof CoAbruf)
		{
			setAbrufID(m_coProjekt.getID());
		}

		return id;
	}


	
	/**
	 * Prüft, ob die Tätigkeit für alle Felder eingegeben wurde
	 */
	public String validate() throws Exception{
		int wertZeit, wertIstZeit, summe, max;
		Date datumHeute, datumVon, datumBis;
		
		
		if (!moveFirst())
		{
			return null;
		}
		
		datumHeute = Format.getDate0Uhr(new Date());
		summe = 0;
		do
		{
			wertZeit = getWertZeit();
			datumVon = getDatum();
			datumBis = getDatumBis();
			wertIstZeit = loadWertIstZeit();
			
			summe += wertZeit;

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
			
			// wenn der Stundenwert geändert wurde muss geprüft werden, ob genügend Stunden auf dem Projekt zur Verfügung stehen
			if (getCurrentRow().getRowState() != IBusinessObject.statusAdded && getFieldPersonID().getState() == IBusinessObject.statusChanged)
			{
				return "Die Person darf nicht geändert werden. Bitte erstellen Sie einen neuen Eintrag.";
			}
			
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
		
		
		return null;
	}


	/**
	 * Prüft die Stundeneintragung für die Person im Monatseinsatzblatt
	 * 
	 * @param personID
	 * @param datum
	 * @param differenz
	 * @return
	 * @throws Exception
	 */
	public String check(int personID, Date datum, int differenz) throws Exception {
		Date datumVon, datumBis;
		
		if (!moveToPersonID(personID))
		{
			return "Das Projekt " + m_coProjekt.getBezeichnung() + " ist für Sie nicht freigegeben.";
		}
		
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
