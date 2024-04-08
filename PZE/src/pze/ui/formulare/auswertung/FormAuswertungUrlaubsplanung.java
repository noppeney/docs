package pze.ui.formulare.auswertung;

import java.util.Date;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Format;
import pze.business.Messages;
import pze.business.Profile;
import pze.business.export.urlaub.ExportUrlaubsplanungListener;
import pze.business.objects.CoFirmenparameter;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungUrlaubsplanung;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;

/**
 * Formular für die Auswertung der Urlaubsplanung (Erstellung der PDF für Personen, Gruppen...)
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungUrlaubsplanung extends FormAuswertung {
	
	public static String RESID = "form.auswertung.urlaubsplanung";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	public FormAuswertungUrlaubsplanung(Object parent) throws Exception {
		super(parent, RESID);
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @throws Exception
	 */
	public static void open(ISession session) throws Exception {
		String key, name;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if (item == null)
		{
			name = "Urlaubsplanung";

			m_formAuswertung = new FormAuswertungUrlaubsplanung(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap("weather.sun");
		}

		editFolder.setSelection(key);
	}


	@Override
	protected void initTable() throws Exception {
	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new CoBuchung();
		
		((CoBuchung) m_co).loadUrlaubsplanung(m_coAuswertung, true);
	}


	public void loadCoExtern() throws Exception {
		loadCo();
	}


	/**
	 * CO mit den Daten
	 * AnAbwesenheit wird für das leichetere Erstellen der Ausgabe geladen
	 * 
	 * @return
	 * @throws Exception 
	 */
	public CoKontowert getCoKontowerte() throws Exception {
		CoKontowert coKontowert = new CoKontowert();
		
		coKontowert.loadAnAbwesenheit(m_coAuswertung);
		return coKontowert;
	}


	/**
	 * CO mit den Daten
	 * 
	 * @return
	 * @throws Exception 
	 */
	public CoBuchung getCoBuchung() throws Exception {
		return (CoBuchung) m_co;
	}


	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungUrlaubsplanung();
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "urlaubsplanung." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
	}
	
	
	/**
	 * PDF-Datei erstellen.<br>
	 * Vorher müssen die Kontowerte ggf. noch berechnet werden.
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#clickedAktualisieren()
	 */
	@Override
	protected void clickedAktualisieren() throws Exception {
		ExportUrlaubsplanungListener exportUrlaubsplanungListener;
		
		long a = System.currentTimeMillis();
		// Daten prüfen bzw. laden
		checkData();
		System.out.println("checkData: " + (System.currentTimeMillis()-a)/1000.);
		a = System.currentTimeMillis();

		// Daten laden
		super.clickedAktualisieren();
		System.out.println("clickedAktualisieren: " + (System.currentTimeMillis()-a)/1000.);

		// PDF erstellen
		exportUrlaubsplanungListener = new ExportUrlaubsplanungListener(this);
		exportUrlaubsplanungListener.activate(null);
	}


	/**
	 * Daten laden und ggf. anpassen
	 * 
	 * @throws Exception
	 */
	public void checkData() throws Exception {
		boolean zeitraumGeaendert;
		Date datumVon, datumBis;
		Date vorgabeDatumVon, vorgabeDatumBis;
		CoPerson coPerson;
		CoKontowert coKontowert;
		CoFirmenparameter coFirmenparameter;
		
		coKontowert = new CoKontowert();
		coFirmenparameter = CoFirmenparameter.getInstance();
		
		// Datum bestimmen, bis zu dem die Daten ausgegeben werden sollen
		datumVon = Format.getDate12Uhr(m_coAuswertung.getDatumVon());
		datumBis = Format.getDate12Uhr(m_coAuswertung.getDatumBis());
		if (datumVon == null || datumBis == null)
		{
			Messages.showErrorMessage("Sie haben keinen Zeitraum für die Auswertung ausgewählt!");
			return;
		}

		// Urlaubsplanung kann nur im freigegebenen Zeitraum abgerufen werden
		zeitraumGeaendert = false;
		vorgabeDatumVon = Format.getDate12Uhr(coFirmenparameter.getDatumUrlaubsplanungAb());
		vorgabeDatumBis = Format.getDate12Uhr(coFirmenparameter.getDatumUrlaubsplanungBis());
		if (datumVon.before(vorgabeDatumVon))
		{
			m_coAuswertung.setDatumVon(vorgabeDatumVon);
			datumVon = vorgabeDatumVon;
			zeitraumGeaendert = true;
		}
		if (datumBis.after(vorgabeDatumBis))
		{
			m_coAuswertung.setDatumBis(vorgabeDatumBis);
			datumBis = vorgabeDatumBis;
			zeitraumGeaendert = true;
		}
		if (zeitraumGeaendert)
		{
			refresh(reasonDataChanged, null);
			Messages.showWarningMessage("Die Urlaubsplanung ist nur für den Zeitraum " + Format.getString(vorgabeDatumVon)
			+ " - " + Format.getString(vorgabeDatumBis) + " möglich. Die Angaben wurden entsprechend angepasst.");
		}
		
		// Datum um 1 Tag verschieben, um die Funktion zum Prüfen des Vortages zu verwenden
		datumBis = Format.getDateVerschoben(datumBis, 1);
		
		
		// ausgewählte Personen durchlaufen und Kontowerte erstellen, wenn sie noch nicht existieren
		coPerson = m_coAuswertung.getCoPersonByAuswahl();
		if (!coPerson.moveFirst())
		{
			// wenn es keine Einschränkung der Personen gibt, lade alle
			coPerson.loadAll();
			coPerson.moveFirst();
		}

		do
		{
			// nur für aktive Personen Kontowerte berechnen
			if (!coPerson.isAktiv())
			{
				continue;
			}

			coKontowert.createVortagIfNotExists(coPerson.getID(), datumBis);
		} while(coPerson.moveNext());
	}


	@Override
	public String getDefaultExportName() {
		return "Urlaubsplanung" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_URLAUBSPLANUNG;
	}
	

}
