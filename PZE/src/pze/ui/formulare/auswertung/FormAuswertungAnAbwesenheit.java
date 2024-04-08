package pze.ui.formulare.auswertung;

import java.util.Date;

import framework.business.interfaces.session.ISession;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Format;
import pze.business.Messages;
import pze.business.Profile;
import pze.business.export.ExportAnAbwesenheitListener;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungAnAbwesenheit;
import pze.business.objects.personen.CoKontowert;
import pze.business.objects.personen.CoPerson;

/**
 * Formular für die Auswertung der An-/Abwesenheit (Kalender)
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungAnAbwesenheit extends FormAuswertung {
	
	public static String RESID = "form.auswertung.anabwesenheit";

	
	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungAnAbwesenheit(Object parent) throws Exception {
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
			name = "Auswertung An-/Abwesenheit";

			m_formAuswertung = new FormAuswertungAnAbwesenheit(editFolder);
			item = editFolder.add(name, key, m_formAuswertung, true);
			item.setBitmap("calendar");
		}

		editFolder.setSelection(key);
	}


	/**
	 * Person beim Doppelklick öffnen
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#initTable()
	 */
	@Override
	protected void initTable() throws Exception {
	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new CoKontowert();
		
		((CoKontowert) m_co).loadAnAbwesenheit(m_coAuswertung);
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungAnAbwesenheit();
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "anabwesenheit." + id;
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
		Date datumVon, datumBis;
		CoPerson coPerson;
		CoKontowert coKontowert;
		CoAuswertung coAuswertung;
		ExportAnAbwesenheitListener exportAnAbwesenheitListener;
		
		coKontowert = new CoKontowert();
		coAuswertung = getCoAuswertung();
		
		// Datum bestimmen, bis zu dem die Daten ausgegeben werden sollen
		datumVon = coAuswertung.getDatumVon();
		datumBis = coAuswertung.getDatumBis();
		if (datumVon == null || datumBis == null)
		{
			Messages.showErrorMessage("Sie haben keinen Zeitraum für die Auswertung ausgewählt!");
			return;
		}

		// Datum um 1 Tag verschieben, um die Funktion zum Prüfen des Vortages zu verwenden
		datumBis = Format.getDateVerschoben(datumBis, 1);
		
		
		// ausgewählte Personen durchlaufen und Kontowerte erstellen, wenn sie noch nicht existieren
		coPerson = coAuswertung.getCoPersonByAuswahl();
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

		// Daten laden
		super.clickedAktualisieren();

		// PDF erstellen
		exportAnAbwesenheitListener = new ExportAnAbwesenheitListener(this);
		exportAnAbwesenheitListener.activate(null);
	}


	@Override
	public String getDefaultExportName() {
		return "Anwesenheitskalender" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}


	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_ANABWESENHEIT;
	}
	

}
