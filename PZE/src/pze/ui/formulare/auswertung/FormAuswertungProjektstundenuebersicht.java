package pze.ui.formulare.auswertung;

import framework.business.interfaces.session.ISession;
import framework.business.interfaces.tables.IHeaderDescription;
import framework.ui.interfaces.controls.ITabFolder;
import framework.ui.interfaces.controls.ITabItem;
import pze.business.Profile;
import pze.business.objects.auswertung.CoAuswertung;
import pze.business.objects.auswertung.CoAuswertungProjektstundenauswertung;
import pze.business.objects.projektverwaltung.CoProjektStundenuebersicht;
import pze.business.objects.projektverwaltung.VirtCoProjekt;

/**
 * Formular für die Auswertung der Projektdaten (Ampelliste + Stunden pro ausgewähltem Zeitraum)
 * 
 * @author Lisiecki
 *
 */
public class FormAuswertungProjektstundenuebersicht extends FormAmpelliste {

	public static final String RESID = "form.auswertung.stundenuebersicht";
	
	private static final String RESID_TABLE = "spread.auswertung.stundenuebersicht";

	
	/**
	 * Konstruktor
	 * 
	 * @param parent		visueller Parent
	 * @throws Exception
	 */
	private FormAuswertungProjektstundenuebersicht(Object parent) throws Exception {
		super(parent, RESID);
	}


	/**
	 * Formular öffnen oder selektieren, wenn noch nicht vorhanden
	 * 
	 * @param session Session
	 * @throws Exception
	 */
	public static void open(ISession session) throws Exception {
		String key;
		ITabFolder editFolder;
		ITabItem item;
		
		key = getKey(0);
		
		editFolder = session.getMainFrame().getEditFolder();
		item = editFolder.get(key);

		if (item == null)
		{
			m_formAuswertung = new FormAuswertungProjektstundenuebersicht(editFolder);
			item = editFolder.add(RESID, key, m_formAuswertung, true);
		}

		editFolder.setSelection(key);
	}


	@Override
	protected String getResIdTable(){
		return RESID_TABLE;
	}
	

	/**
	 * Spalten der Tabelle festlegen, da diese dynamisch erzeugt wird.<br>
	 * Die benötigten Spalten werden nochmal in der richtigen Reihenfolge angefügt und anschließend alle alten gelöscht.
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#updateHeaderDescription()
	 */
	@Override
	protected void updateHeaderDescription() {
		int iField, anzFields;
		String resID;
		IHeaderDescription headerDescription;
		VirtCoProjekt virtCoProjekt;
		
		headerDescription = m_table.getHeaderDescription();
		virtCoProjekt = getVirtCoProjekt();
		

		// alle Projektspalten in der richtigen Reihenfolge anhängen
		headerDescription.add(virtCoProjekt.getFieldKundeID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAuftragsNr().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAbrufNr().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldKostenstelle().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldBestellNr().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldBeschreibung().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldStatusID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldLiefertermin().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldBestellwert().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldUvg().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldPuffer().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldSollstunden().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldIstStunden().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldWertZeitVerbleibend().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldVerbrauchBestellwert().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldVerbrauchSollstunden().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldDatumMeldungVersendet().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldProjektleiterID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAbteilungID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAbteilungsleiterID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldFachgebietID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAbteilungKundeID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAnfordererKundeID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldZuordnungID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldPaketID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAbrechnungsartID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAbrechenbareStunden().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldEdvNr().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAuftragID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldAbrufID().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldDatumAbruf().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldDatumBestellung().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldDatumFertigmeldung().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldDatumFreigabeRechnungAG().getFieldDescription());
		headerDescription.add(virtCoProjekt.getFieldDatumBerechnetBis().getFieldDescription());
		

		// Stundenwertspalten nochmal anfügen
		anzFields = m_co.getFieldCount();
		for (iField=0; iField<anzFields; ++iField)
		{
			resID = m_co.getField(iField).getFieldDescription().getResID();
			if (resID.startsWith(CoProjektStundenuebersicht.RESID_FIELD_ZEITRAUM))
			{
				headerDescription.add(m_co.getField(resID).getFieldDescription());
			}
		}
		
		
		// alle alten Spalten löschen
		for (iField=anzFields-1; iField>=0; --iField)
		{
			headerDescription.remove(headerDescription.getColumnDescription(iField).getResID());
		}
		

		// Spaltenbreite anpassen
		headerDescription.getColumnDescription(virtCoProjekt.getFieldKundeID().getFieldDescription().getResID()).setWidth(90);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAuftragsNr().getFieldDescription().getResID()).setWidth(110);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAbrufNr().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldKostenstelle().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldBestellNr().getFieldDescription().getResID()).setWidth(150);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldBeschreibung().getFieldDescription().getResID()).setWidth(350);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldStatusID().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldLiefertermin().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldBestellwert().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldUvg().getFieldDescription().getResID()).setWidth(50);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldPuffer().getFieldDescription().getResID()).setWidth(50);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldSollstunden().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldIstStunden().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldWertZeitVerbleibend().getFieldDescription().getResID()).setWidth(90);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldVerbrauchBestellwert().getFieldDescription().getResID()).setWidth(70);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldVerbrauchSollstunden().getFieldDescription().getResID()).setWidth(70);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldDatumMeldungVersendet().getFieldDescription().getResID()).setWidth(100);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldProjektleiterID().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAbteilungID().getFieldDescription().getResID()).setWidth(130);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAbteilungsleiterID().getFieldDescription().getResID()).setWidth(130);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldFachgebietID().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAbteilungKundeID().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAnfordererKundeID().getFieldDescription().getResID()).setWidth(130);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldZuordnungID().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldPaketID().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAbrechnungsartID().getFieldDescription().getResID()).setWidth(100);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAbrechenbareStunden().getFieldDescription().getResID()).setWidth(100);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldEdvNr().getFieldDescription().getResID()).setWidth(100);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAuftragID().getFieldDescription().getResID()).setWidth(100);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldAbrufID().getFieldDescription().getResID()).setWidth(100);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldDatumAbruf().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldDatumBestellung().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldDatumFertigmeldung().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldDatumFreigabeRechnungAG().getFieldDescription().getResID()).setWidth(80);
		headerDescription.getColumnDescription(virtCoProjekt.getFieldDatumBerechnetBis().getFieldDescription().getResID()).setWidth(80);

		m_table.setHeaderDescription(headerDescription);
	}


	@Override
	protected void loadCo() throws Exception {
		m_co = new VirtCoProjekt();
		((VirtCoProjekt)m_co).loadStundenauswertung(this);
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}

	
	@Override
	protected CoAuswertung createCoAuswertung() throws Exception {
		return new CoAuswertungProjektstundenauswertung();
	}

	
	/**
	 * @param id Primärschlüssel
	 * @return Item Key im Tabfolder
	 */
	public static String getKey(int id) {
		return "projektstundenauswertung." + id;
	}
	
	
	@Override
	public String getKey() {
		return getKey(0);
	}
	

	@Override
	public void activate() {
		addExcelExportListener(m_co, m_table, getDefaultExportName(), getProfilePathKey());
	}
	

	@Override
	public String getDefaultExportName(){
		return "Projektstundenauswertung" + getCoAuswertung().getStringEinschraenkungDatumPerson();
	}
	

	@Override
	public String getProfilePathKey() {
		return Profile.KEY_AUSWERTUNG_PROJEKTE;
	}
	

	/**
	 * Wenn das Formular bereits existiert muss es geschlossen und neu geöffnet werden, da sich die Anzahl der Spalten verändern kann
	 * 
	 * @see pze.ui.formulare.auswertung.FormAuswertung#loadData()
	 */
	protected void loadData() throws Exception {
		ISession session;
		String key;
		ITabFolder editFolder;
		
		if (getData() instanceof VirtCoProjekt)
		{
			session = getSession();
			key = getKey(0);

			editFolder = session.getMainFrame().getEditFolder();
			editFolder.remove(key);

			FormAuswertungProjektstundenuebersicht.open(session);
		}
		else
		{
			super.loadData();
		}
	}

	
	public CoAuswertungProjektstundenauswertung getCoAuswertungProjektstundenauswertung(){
		return (CoAuswertungProjektstundenauswertung) m_coAuswertung;
	}
	
}
