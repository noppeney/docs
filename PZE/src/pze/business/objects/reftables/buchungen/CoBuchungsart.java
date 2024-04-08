package pze.business.objects.reftables.buchungen;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.reftables.CoFreigabeberechtigungen;
import pze.business.objects.reftables.personen.CoAbteilung;
import pze.business.objects.reftables.personen.CoPosition;

/**
 * CacheObject für Buchungsarten (Kommen, Gehen, Pause...)
 * 
 * @author Lisiecki
 *
 */
public class CoBuchungsart extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblbuchungsart";

	public static final int ID_KOMMEN = 1;
	public static final int ID_GEHEN = 6;
	
	public static final int ID_DIENSTGANG = 2;
	public static final int ID_DIENSTREISE = 3;
	public static final int ID_ENDE_DIENSTGANG_DIENSTREISE = 17;
	public static final int ID_KGG = 18;
	
	public static final int ID_BERUFSSCHULE = 4;
	public static final int ID_VORLESUNG = 5;
	
	public static final int ID_PAUSE = 7;
	public static final int ID_PAUSENENDE = 19;
	public static final int ID_ARBEITSUNTERBRECHUNG = 20;
	public static final int ID_PRIVATE_UNTERBRECHUNG = 15;
	
	public static final int ID_URLAUB = 8;
	public static final int ID_SONDERURLAUB = 9;
	public static final int ID_ELTERNZEIT = 10;
	public static final int ID_FA = 11;

	public static final int ID_KRANK = 13;
	public static final int ID_KRANK_OHNE_LFZ = 14;
	
	public static final int ID_ORTSFLEX_ARBEITEN = 21;

	public static final int ID_BEZ_FREISTELLUNG = 22;

	
	
	public static final String COLOR_ANWESEND = "##88FF88";
	public static final String COLOR_ABWESEND = "##DDDDDD";
	
	public static final String COLOR_PAUSE = "##FF6666";
	public static final String COLOR_ARBEITSUNTERBRECHUNG = UserInformation.getInstance().isColorAnwesenheitDetail() ? "##FF60B7" : COLOR_PAUSE;
	public static final String COLOR_PRIVATE_UNTERBRECHUNG = UserInformation.getInstance().isColorAnwesenheitDetail() ? "##FE2EF7" : COLOR_PAUSE;

	public static final String COLOR_URLAUB = "##2E9AFE";
	public static final String COLOR_SONDERURLAUB= "##2E9AFE";
	public static final String COLOR_FA = UserInformation.getInstance().isColorAnwesenheitDetail() ? "##00BFFF" : COLOR_URLAUB;

	public static final String COLOR_ELTERNZEIT = UserInformation.getInstance().isColorAnwesenheitDetail() ? "##DEB887" : COLOR_ABWESEND; // alt B45F04
	
	public static final String COLOR_DIENSTREISE = "##FFBF00";
	public static final String COLOR_DIENSTGANG = "##FAAC58";
	public static final String COLOR_KGG = "##DDDDDD";
	public static final String COLOR_VORLESUNG = "##F7D358";
	
	public static final String COLOR_KRANK = UserInformation.getInstance().isColorAnwesenheitDetail() ? "##FFFF00" : COLOR_ABWESEND;
	public static final String COLOR_KRANK_OHNE_LFZ = UserInformation.getInstance().isColorAnwesenheitDetail() ? "##FFFF00" : COLOR_ABWESEND;

	public static final String COLOR_ORTSFLEX_ARBEITEN = "##88CC88";

	// beige, ocker, hellbraun FFEB8D FFEBCD
	// lila C850FF, etwas heller E066FF
	// hellblau F0F8FF

	public static CoBuchungsart m_instance = null;
	


	/**
	 * Kontruktor
	 */
	public CoBuchungsart() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz 
	 * 
	 * @return Instanz der Klasse 
	 */
	public static CoBuchungsart getInstance() throws Exception {
		if (CoBuchungsart.m_instance == null)
		{
			CoBuchungsart.m_instance = new CoBuchungsart();
			CoBuchungsart.m_instance.loadAll();
		}
		
		return CoBuchungsart.m_instance;
	}

	
	/**
	 * Alle Buchungsarten laden, zu denen man eine Berechtigung hat (Personalverwaltung hat zusätzliche Berechtigungen) 
	 * und die mit der übergebenen ID, damit die Comboboxen korrekt gefüllt werden.
	 * 
	 * @throws Exception
	 */
	public void loadAllWithBerechtigung(int id, boolean isSelbstbuchung) throws Exception {
		String where;
		
		where = "";
		
		// für sich selbst nur die buchbaren Buchungsarten und die aktuelle laden
		if (isSelbstbuchung)
		{
			where += "ID = " + id;
			where += " OR IstSelbstbuchungZulaessig = 1 ";
		}
		// für andere je nach Berechtigung, personalverwaltung kann alle laden
		else if (!UserInformation.getInstance().isPersonalverwaltung())
		{
			where += "ID = " + id;

			if (UserInformation.getInstance().isSekretariat())
			{
				where += " OR IstBuchungPersonalverwaltung = 0 ";
			}
			else
			{
				where += " OR IstSelbstbuchungZulaessig = 1 ";
			}
		}
		
		emptyCache();
		Application.getLoaderBase().load(this, where, getSortFieldName());
	}

	
	/**
	 * Buchungsarten DR/DG laden
	 * 
	 * @throws Exception
	 */
	public void loadDrDg() throws Exception {
		emptyCache();
		Application.getLoaderBase().load(this, "ID IN (" + ID_DIENSTGANG + ", " + ID_DIENSTREISE + ")", getSortFieldName());
	}


	/**
	 * Gehe zu dem Eintrag mit der Buchungsregel (Hexadezimal-Format)
	 * 
	 * @param bezeichnung
	 * @return ID
	 */
	public int getIdFromBuchungsregelHex(String buchungsregelHex) {
		int buchungsregel;
		
		buchungsregel = (int) Format.getValueOfHexValue(buchungsregelHex);
		
		if (!moveTo(buchungsregel, "field." + getTableName() + ".buchungsregel"))
		{
			return 0;
		}
		
		return getID();
	}
	

	private IField getFieldTagesbeginnbuchung() {
		return getField("field." + getTableName() + ".isttagesbeginnbuchung");
	}


	public boolean isTagesbeginnbuchung() {
		return Format.getBooleanValue(getFieldTagesbeginnbuchung().getValue());
	}


	private IField getFieldTagesendbuchung() {
		return getField("field." + getTableName() + ".isttagesendbuchung");
	}


	public boolean isTagesendbuchung() {
		return Format.getBooleanValue(getFieldTagesendbuchung().getValue());
	}


	private IField getFieldTagesbuchungZulaessig() {
		return getField("field." + getTableName() + ".isttagesbuchungzulaessig");
	}


	public boolean isTagesbuchungZulaessig() {
		return Format.getBooleanValue(getFieldTagesbuchungZulaessig().getValue());
	}

	
//	public boolean isTagesbuchungZulaessig(int id) {
//		if (!moveToID(id))
//		{
//			return false;
//		}
//		
//		return isTagesbuchungZulaessig();
//	}


	private IField getFieldTagesbuchungPflicht() {
		return getField("field." + getTableName() + ".isttagesbuchungpflicht");
	}


	public boolean isTagesbuchungPflicht() {
		return Format.getBooleanValue(getFieldTagesbuchungPflicht().getValue());
	}

	
//	public boolean isTagesbuchungPflicht(int id) {
//		if (!moveToID(id))
//		{
//			return false;
//		}
//		
//		return isTagesbuchungPflicht();
//	}


	private IField getFieldBuchungPersonalverwaltung() {
		return getField("field." + getTableName() + ".istbuchungpersonalverwaltung");
	}


	public boolean isBuchungPersonalverwaltung() {
		return Format.getBooleanValue(getFieldBuchungPersonalverwaltung().getValue());
	}


	private IField getFieldSelbstbuchungZulaessig() {
		return getField("field." + getTableName() + ".istselbstbuchungzulaessig");
	}


	public boolean isSelbstbuchungZulaessig() {
		return Format.getBooleanValue(getFieldSelbstbuchungZulaessig().getValue());
	}


	public boolean isSelbstbuchungZulaessig(int id) {
		if (!moveToID(id))
		{
			return false;
		}
		
		return isSelbstbuchungZulaessig();
	}


	private IField getFieldFreigabeMa() {
		return getField("field." + getTableName() + ".istfreigabema");
	}


	public boolean isFreigabeMa() {
		return Format.getBooleanValue(getFieldFreigabeMa());
	}


	public boolean isFreigabeMa(int id) {
		if (!moveToID(id))
		{
			return false;
		}
		
		return isFreigabeMa();
	}


	private IField getFieldFreigabeAl() {
		return getField("field." + getTableName() + ".istfreigabeal");
	}


	public boolean isFreigabeAl() {
		return Format.getBooleanValue(getFieldFreigabeAl());
	}


	private IField getFieldFreigabeVertreter() {
		return getField("field." + getTableName() + ".istfreigabevertreter");
	}


	public boolean isFreigabeVertreter() {
		return Format.getBooleanValue(getFieldFreigabeVertreter());
	}


	private IField getFieldFreigabePb() {
		return getField("field." + getTableName() + ".istfreigabepb");
	}


	public boolean isFreigabePb() {
		return Format.getBooleanValue(getFieldFreigabePb());
	}


	private IField getFieldFreigabeV() {
		return getField("field." + getTableName() + ".istfreigabev");
	}


	public boolean isFreigabeV() {
		return Format.getBooleanValue(getFieldFreigabeV());
	}


	/**
	 * Pause, priv. Unterbrechung, Arbeitsunterbrechung
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isUnterbrechung(int id) {
		return id == ID_PAUSE || id == ID_PRIVATE_UNTERBRECHUNG || id == ID_ARBEITSUNTERBRECHUNG;
	}


	/**
	 * Urlaub, Sonderurlaub, FA
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isUrlaub(int id) {
		return id == ID_URLAUB || id == ID_SONDERURLAUB;
	}


	/**
	 * Urlaub, Sonderurlaub, FA
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isUrlaubFA(int id) {
		return id == ID_URLAUB || id == ID_SONDERURLAUB || id == ID_FA;
	}


	/**
	 * Krank, Krank ohne LFZ
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isKrank(int id) {
		return id == ID_KRANK || id == ID_KRANK_OHNE_LFZ;
	}


	/**
	 * Dienstreise oder Dienstgang
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isDrDg(int id) {
		return id == ID_DIENSTGANG || id == ID_DIENSTREISE;
	}


	/**
	 * Buchungsarten, bei denen die Uhrzeit geändert werden darf
	 * 
	 * @param id
	 * @return
	 */
	public static boolean isUhrzeitAendernErlaubt(int id) {
		return id == ID_ORTSFLEX_ARBEITEN || id == ID_FA || isDrDg(id);
	}


	/**
	 * BuchungsartID für das Ende der Buchung bestimmen, wenn sie existiert
	 * 
	 * @param uhrzeitBis 
	 * @return BuchungsartID oder 0
	 */
	public int getBuchungsartTagesendeID(Integer uhrzeitBis) {
		int buchungsartID;
		
		buchungsartID = getID();
		
		
		if (buchungsartID == ID_DIENSTGANG || buchungsartID == ID_DIENSTREISE || buchungsartID == ID_VORLESUNG|| buchungsartID == ID_BERUFSSCHULE)
		{
//			return ID_ENDE_DIENSTGANG_DIENSTREISE;
//		}
//		else if (buchungsartID == ID_VORLESUNG|| buchungsartID == ID_BERUFSSCHULE)
//		{
//			return ID_GEHEN;
			// bis 15 Uhr Kommen, danach Gehen
			try 
			{
				if (Format.getIntValue(uhrzeitBis) > 0 && uhrzeitBis < CoFirmenparameter.getInstance().getKernzeitEndeMoDo())
				{
					return ID_KOMMEN;
				}
			} 
			catch (Exception e)
			{
			}
			
			return ID_ENDE_DIENSTGANG_DIENSTREISE;
		}
		else if (buchungsartID == ID_KGG)
		{
			return ID_GEHEN;
		}
		else if (buchungsartID == ID_PAUSE || buchungsartID == ID_FA 
				 || buchungsartID == ID_ARBEITSUNTERBRECHUNG|| buchungsartID == ID_PRIVATE_UNTERBRECHUNG)
		{
			return ID_KOMMEN;
		}
		else if (buchungsartID == ID_KOMMEN || buchungsartID == ID_ORTSFLEX_ARBEITEN)
		{
			return ID_GEHEN;
		}

		return 0;
	}

	
	/**
	 * Prüft, ob für die Buchungsart mit dem aktuellen Genehmigungsstatus der nächste Genehmigungsstatus erteilt werden kann
	 * 
	 * @param coBuchungsartID
	 * @param aktStatusGenehmigungID aktueller Genehmigungsstatus
	 * @param nextStatusGenehmigungID nächster Genehmigungsstatus
	 * @param vertretungFreigegeben 
	 * @return
	 * @throws Exception 
	 */
	public boolean isFreigabeMoeglich(int coBuchungsartID, int aktStatusGenehmigungID, int nextStatusGenehmigungID, int personID,
			boolean vertretungFreigegeben) throws Exception {
		CoPerson coPerson;
		CoFreigabeberechtigungen coFreigabeberechtigungen;
		
		// zur aktuellen Buchungsart gehen
		moveToID(coBuchungsartID);
		
		
		// Freigabe vom AL möglich?
		if (nextStatusGenehmigungID == CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AL)
		{
			// wenn Freigabe vom V für Sekretärinnen erforderlich
			if (isFreigabeV())
			{
				coFreigabeberechtigungen = new CoFreigabeberechtigungen(UserInformation.getPersonID());

				coPerson = CoPerson.getInstance();
				coPerson.moveToID(personID);
				
				// Freigeber V darf nicht freigeben, wenn der AL noch nicht freigegeben hat
				if (coFreigabeberechtigungen.isFreigabeVerwaltungErlaubt() && aktStatusGenehmigungID != CoStatusGenehmigung.STATUSID_FREIGEGEBEN_AL
						&& coPerson.getAbteilungID() != CoAbteilung.ID_VERWALTUNG && coPerson.getPositionID() == CoPosition.ID_SEKRETAERIN)
				{
					return false;
				}
			}
			
			// wenn Freigabe von PB erforderlich und noch nicht erteilt
			if (isFreigabePb() && aktStatusGenehmigungID == CoStatusGenehmigung.STATUSID_BEANTRAGT)
			{
				return false;
				// prüfe ob Freigabe PB bereits erteilt
//				return aktStatusGenehmigungID == CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB;
			}

			// wenn Freigabe vom Vertreter erforderlich
			if (isFreigabeVertreter())
			{
				// prüfe ob Freigabe Vertreter bereits erteilt
				return vertretungFreigegeben || aktStatusGenehmigungID == CoStatusGenehmigung.STATUSID_FREIGEGEBEN_VERTRETER;
			}
			
			return true;
		}
		
		
		// Freigabe vom Vertreter möglich?
		if (nextStatusGenehmigungID == CoStatusGenehmigung.STATUSID_FREIGEGEBEN_VERTRETER)
		{
			// wenn Freigabe von PB erforderlich
			if (isFreigabePb())
			{
				// prüfe ob Freigabe PB bereits erteilt
				return aktStatusGenehmigungID == CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB;
			}
			
			return true;
		}
		
		
		// Freigabe von PB möglich?
		if (nextStatusGenehmigungID == CoStatusGenehmigung.STATUSID_FREIGEGEBEN_PB)
		{
			return true;
		}

		
		return false;
	}
	

	/**
	 * Farbe für Buchungsarten
	 * 
	 * @param buchungsartID
	 * @return
	 */
	public String getColorBuchungsart(int buchungsartID){
		
		switch (buchungsartID) 
		{
		case CoBuchungsart.ID_KOMMEN:
			return COLOR_ANWESEND;

		case CoBuchungsart.ID_GEHEN:
			return COLOR_ABWESEND;

		case CoBuchungsart.ID_DIENSTGANG:
			return COLOR_DIENSTGANG;

		case CoBuchungsart.ID_DIENSTREISE:
			return COLOR_DIENSTREISE;

		case CoBuchungsart.ID_KGG:
			return COLOR_KGG;

		case CoBuchungsart.ID_ENDE_DIENSTGANG_DIENSTREISE:
			return COLOR_ABWESEND;

		case CoBuchungsart.ID_BERUFSSCHULE:
			return COLOR_VORLESUNG;

		case CoBuchungsart.ID_VORLESUNG:
			return COLOR_VORLESUNG;

		case CoBuchungsart.ID_PAUSE:
			return COLOR_PAUSE;

		case CoBuchungsart.ID_PAUSENENDE:
			return COLOR_ANWESEND;

		case CoBuchungsart.ID_ARBEITSUNTERBRECHUNG:
			return COLOR_ARBEITSUNTERBRECHUNG;

		case CoBuchungsart.ID_PRIVATE_UNTERBRECHUNG:
			return COLOR_PRIVATE_UNTERBRECHUNG;

		case CoBuchungsart.ID_URLAUB:
			return COLOR_URLAUB;

		case CoBuchungsart.ID_SONDERURLAUB:
			return COLOR_SONDERURLAUB;

		case CoBuchungsart.ID_FA:
			return COLOR_FA;

		case CoBuchungsart.ID_ELTERNZEIT:
			return COLOR_ELTERNZEIT;

		case CoBuchungsart.ID_KRANK:
			return COLOR_KRANK;

		case CoBuchungsart.ID_KRANK_OHNE_LFZ:
			return COLOR_KRANK_OHNE_LFZ;

		case CoBuchungsart.ID_ORTSFLEX_ARBEITEN:
			return COLOR_ORTSFLEX_ARBEITEN;

		default:
			return COLOR_ABWESEND;
		}
	}

}
