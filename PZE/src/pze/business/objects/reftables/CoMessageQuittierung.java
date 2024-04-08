package pze.business.objects.reftables;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für Zuordnung wer welche Message zur Quittierung bekommt
 * 
 * @author Lisiecki
 *
 */
public class CoMessageQuittierung extends AbstractCacheObject {

	public static final int ID_AENDERUNG_URLAUB_GENEHMIGT = 1;
	public static final int ID_AENDERUNG_URLAUB_GEPLANT = 2;
	public static final int ID_RESTURLAUB = 3;
	public static final int ID_KEIN_ERSTHELFER= 4;
//	private static final int ID_URLAUB_GEPLANT_ANSTEHEND = 5;
//	private static final int ID_OFA_GEPLANT_ANSTEHEND = 6;
	public static final int ID_MONATSEINSATZBLATT_EINTRAGEN = 7;
	public static final int ID_VERTRETUNG_GELOESCHT = 8;
	public static final int ID_ANTRAG_ABGELEHNT = 9;
	public static final int ID_ANTRAG_GENEHMIGT = 10;
	public static final int ID_PROJEKT_ZUGETEILT= 11;
	public static final int ID_PROJEKTZUTEILUNG_GEAENDERT = 12;
	public static final int ID_GLEITZEITKONTOSTAND = 13; // zum Jahresende auf 20 Stunden prüfen
	public static final int ID_W_SONSTIGES8STUNDEN = 14; // ab 8 Stunden auf Abteilung-sonstiges
	public static final int ID_PROJEKTVERFOLGUNG_PROGNOSE_ERSTELLEN = 15;
	public static final int ID_PROJEKTVERFOLGUNG_PROGNOSE_PRUEFEN = 16;
	public static final int ID_PROJEKTAENDERUNG_UEBERNEHMEN = 17;
	public static final int ID_PROJEKT_SCHLIESSEN = 18;
	public static final int ID_PROJEKT_PROZENTMELDUNG = 19;
	public static final int ID_A1_BESCHEINIGUNG = 20;
	public static final int ID_PROJEKTAENDERUNG_NICHT_UEBERNOMMEN= 21;
	public static final int ID_PLINFO_PROJEKTAENDERUNG_UEBERNEHMEN = 22;
	public static final int ID_PLINFO_PROJEKT_SCHLIESSEN = 23;
	public static final int ID_DR_INFO = 24;

	private static final String TABLE_NAME = "rtblmessage";

	private static CoMessageQuittierung m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoMessageQuittierung() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoMessageQuittierung getInstance() throws Exception {
		if (CoMessageQuittierung.m_instance == null)
		{
			CoMessageQuittierung.m_instance = new CoMessageQuittierung();
			CoMessageQuittierung.m_instance.loadAll();
		}
		
		return CoMessageQuittierung.m_instance;
	}


	private IField getFieldMitarbeiter() {
		return getField("field." + getTableName() + ".mitarbeiter");
	}


	public boolean isQuittierungMitarbeiter() {
		return Format.getBooleanValue(getFieldMitarbeiter());
	}


	private IField getFieldGl() {
		return getField("field." + getTableName() + ".gl");
	}


	public boolean isQuittierungGL() {
		return Format.getBooleanValue(getFieldGl());
	}


	private IField getFieldAl() {
		return getField("field." + getTableName() + ".al");
	}


	public boolean isQuittierungAl() {
		return Format.getBooleanValue(getFieldAl());
	}


	private IField getFieldVerwaltung() {
		return getField("field." + getTableName() + ".verwaltung");
	}


	public boolean isQuittierungVerwaltung() {
		return Format.getBooleanValue(getFieldVerwaltung());
	}


	private IField getFieldSekretariat() {
		return getField("field." + getTableName() + ".sekretariat");
	}


	public boolean isQuittierungSekretariat() {
		return Format.getBooleanValue(getFieldSekretariat());
	}


	private IField getFieldDrInfo() {
		return getField("field." + getTableName() + ".drinfo");
	}


	public boolean isDrInfo() {
		return Format.getBooleanValue(getFieldDrInfo());
	}


	private IField getFieldMessageProjekt() {
		return getField("field." + getTableName() + ".istmessageprojekt");
	}


	private boolean isMessageProjekt() {
		return Format.getBooleanValue(getFieldMessageProjekt());
	}


	public static boolean isMessageProjekt(int id) {
		m_instance.moveToID(id);
		return m_instance.isMessageProjekt();
	}


	private IField getFieldMessageProjektverfolgung() {
		return getField("field." + getTableName() + ".istmessageprojektverfolgung");
	}


	private boolean isMessageProjektverfolgung() {
		return Format.getBooleanValue(getFieldMessageProjektverfolgung());
	}


	public static boolean isMessageProjektverfolgung(int id) {
		m_instance.moveToID(id);
		return m_instance.isMessageProjektverfolgung();
	}


}
