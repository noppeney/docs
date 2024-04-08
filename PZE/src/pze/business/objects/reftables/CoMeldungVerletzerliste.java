package pze.business.objects.reftables;

import pze.business.Format;
import pze.business.objects.AbstractCacheObject;

/**
 * CacheObject für die Meldungen auf der Verletzerliste
 * 
 * @author Lisiecki
 *
 */
public class CoMeldungVerletzerliste extends AbstractCacheObject {

	private static final String TABLE_NAME = "rtblmeldungverletzerliste";

	public static final int MELDUNGID_TAGESBEGINN_FEHLT = 1;
	public static final int MELDUNGID_TAGESENDE_FEHLT = 2;
	public static final int MELDUNGID_TAGESENDE_UNZULAESSIG = 3;
	
	public static final int MELDUNGID_PAUSENENDE_FEHLT = 4;
	public static final int MELDUNGID_TAGESBUCHUNG_UNZULAESSIG = 5;
	public static final int MELDUNGID_DOPPELTE_BUCHUNG = 6;
	public static final int MELDUNGID_KEINE_BUCHUNG = 7;
	
	public static final int MELDUNGID_UEBERSCHREITUNG_PAUSE = 8;
	public static final int MELDUNGID_GEARBEITET_ARBEITSFREIER_TAG = 9;
	
	public static final int MELDUNGID_VERLETZUNG_KERNZEIT_BEGINN = 10;
	public static final int MELDUNGID_VERLETZUNG_KERNZEIT_ENDE = 11;
	
	public static final int MELDUNGID_VERLETZUNG_KERNZEIT_PRIV_UNTERBRECHUNG = 12;
	public static final int MELDUNGID_VERLETZUNG_KERNZEIT_ARBEITSUNTERBRECHUNG = 20;

	public static final int MELDUNGID_VERLETZUNG_RAHMENARBEITSZEIT_ENDE = 13;
	public static final int MELDUNGID_UEBERSCHREITUNG_ARBEITSZEIT_TAG = 14;
	
	// diese Meldungen werden per Trigger gesetzt
//	public static final int MELDUNG_UEBERSCHREITUNG_ARBEITSZEIT_WOCHE = 15;
	
//	public static final int MELDUNGID_HINWEIS_GLEITZEITKONTO = 16; // gelöscht für Version 1.4.0

	public static final int MELDUNGID_PAUSENBEGINN= 17;
	public static final int MELDUNGID_PAUSENENDE= 18;

	public static final int MELDUNGID_REISEZEIT= 19;

//	public static final int MELDUNGID_OFA= 21; // gelöscht für Version 4.0
//	public static final int MELDUNGID_OFA_ARBEITSZEIT= 22; // gelöscht für Version 4.0.4

	public static final int MELDUNGID_BUCHUNG_OHNE_ANTRAG= 23;

	private static CoMeldungVerletzerliste m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoMeldungVerletzerliste() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoMeldungVerletzerliste getInstance() throws Exception {
		if (CoMeldungVerletzerliste.m_instance == null)
		{
			CoMeldungVerletzerliste.m_instance = new CoMeldungVerletzerliste();
			CoMeldungVerletzerliste.m_instance.loadAll();
		}
		
		return CoMeldungVerletzerliste.m_instance;
	}


//	public boolean isFreigebenMoeglich(){ // TODO löschen in DB mit neuer Version
//		return Format.getBooleanValue(getField("field." + getTableName() + ".istfreigebenmoeglich").getValue());
//	}
	// diese Spalte und istmeldungarbeitszeit löschen in DB, sind in appTables schon länger raus


	public boolean isMeldungArbeitszeitBeginn(){
		return Format.getBooleanValue(getField("field." + getTableName() + ".istmeldungarbeitszeitbeginn").getValue());
	}


	public boolean isMeldungArbeitszeitEnde(){
		return Format.getBooleanValue(getField("field." + getTableName() + ".istmeldungarbeitszeitende").getValue());
	}


	public boolean isMeldungArbZgKontostand(){
		return Format.getBooleanValue(getField("field." + getTableName() + ".istmeldungarbzgkontostand").getValue());
	}


	public boolean isMeldungArbeitstag(){
		return Format.getBooleanValue(getField("field." + getTableName() + ".istmeldungarbeitstag").getValue());
	}


//	public boolean isFreigabeAL(){
//		return Format.getBooleanValue(getField("field." + getTableName() + ".istfreigabeal").getValue());
//	}
//
//
//	public boolean isFreigabeSekretariat(){
//		return Format.getBooleanValue(getField("field." + getTableName() + ".istfreigabesekretariat").getValue());
//	}
//
//
//	public boolean isAnzeigeSekretariat(){
//		return Format.getBooleanValue(getField("field." + getTableName() + ".istanzeigesekretariat").getValue());
//	}


	public boolean isAnzeigeMessageboardPB(){
		return Format.getBooleanValue(getField("field." + getTableName() + ".istmessageboardpb").getValue());
	}


	public boolean isAnzeigeMessageboardPB(int id){
		if (!moveToID(id))
		{
			return false;
		}
		
		return isAnzeigeMessageboardPB();
	}


	private boolean isBemerkungPflicht(){
		return Format.getBooleanValue(getField("field." + getTableName() + ".istbemerkungpflicht").getValue());
	}


	public boolean isBemerkungPflicht(int id){
		if (!moveToID(id))
		{
			return false;
		}
		
		return isBemerkungPflicht();
	}


}
