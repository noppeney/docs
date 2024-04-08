package pze.business.objects.archiv;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.personen.CoKontowert;

/**
 * Klasse zum Laden der Archiv-Einträge für Bewegungsdaten
 * 
 * @author lisiecki
 */
public class CoArchivBewegungsdaten extends AbstractCacheObject {

	/**
	 * für Jahre mit noch nicht archivierten Daten
	 */
	CoArchivBewegungsdaten m_coBewegungsdaten;

	
	
	/**
	 * Kontruktor
	 */
	public CoArchivBewegungsdaten() {
		super("table." + CoKontowert.TABLE_NAME);
	}
	
	
	/**
	 * Alle Jahre laden
	 * 
	 * @throws Exception
	 */
	public void loadArchiv() throws Exception {
		
		// archivierte Jahre
		loadArchiv(this, true);
		
		// nicht archivierte Jahre
		m_coBewegungsdaten = new CoArchivBewegungsdaten();
		loadArchiv(m_coBewegungsdaten, false);
	}

	
	/**
	 * Alle Jahre laden
	 * 
	 * @param co
	 * @param loadArchiv
	 * @throws Exception
	 */
	private void loadArchiv(CoArchivBewegungsdaten co, boolean loadArchiv) throws Exception {
		
		// Feld für Jahreszahl
		co.removeField(getResIdJahr());
		co.addField(getResIdJahr());

		// Daten laden aus Kontowerten oder aus dem Archiv
		co.emptyCache();
		Application.getLoaderBase().load(co, "SELECT DISTINCT YEAR(Datum) AS Jahr FROM " + (loadArchiv ? "ARCHIV" : "") + CoKontowert.TABLE_NAME + " ORDER BY Jahr");
	}

	
	private String getResIdJahr() {
		return "virt.field.monatseinsatzblatt.jahr";
	}


	private IField getFieldJahr() {
		return getField(getResIdJahr());
	}


	@Override
	public int getJahr() {
		return Format.getIntValue(getFieldJahr());
	}
	

	@Override
	public IField getFieldBezeichnung() {
		return getFieldJahr();
	}

	
	/**
	 * Zu der Zeile mit dem Jahr wechseln
	 * 
	 * @param tag
	 * @return
	 */
	private boolean moveToJahr(int jahr) {
		return moveTo(jahr, getResIdJahr());
	}


	/**
	 * Jahr bereits vollständig archiviert oder nur teilweise durch Personen
	 * 
	 * @return
	 */
	public boolean isVollstaendigArchiviert() {
		return !m_coBewegungsdaten.moveToJahr(getJahr());
	}


	/**
	 * Das Vorjahr ist bereits vollständig archiviert oder es ist das erste Jahr
	 * 
	 * @return
	 */
	public boolean isVorjahrArchiviert() {
		// es ist das erste Jahr oder das Vorjahr ist archiviert
		return !movePrev() || isVollstaendigArchiviert();
	}


	/**
	 * Das nächste Jahr ist bereits vollständig archiviert
	 * 
	 * @return
	 */
	public boolean isNextJahrArchiviert() {
		// es ist das letzte Jahr oder das nächste Jahr ist archiviert
		return moveNext() && isVollstaendigArchiviert();
	}


	@Override
	public String getNavigationBitmap() {
		// Bitmap in Abhängigkeit davon, ob bereits alle Daten für das Jahr archiviert wurden
		if (isVollstaendigArchiviert())
		{
			return "brick.go";
		}
		else
		{
			return "brick.edit";
		}
	}

}
