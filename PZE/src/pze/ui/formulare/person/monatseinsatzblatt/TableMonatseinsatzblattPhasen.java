package pze.ui.formulare.person.monatseinsatzblatt;

import framework.ui.interfaces.controls.IControl;
import framework.ui.interfaces.spread.ISpreadCell;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattPhasen;
import pze.ui.controls.SortedTableControl;

/**
 * Klasse für die Tabelle des Monatseinsatzblattes
 * 
 * @author Lisiecki
 */
public class TableMonatseinsatzblattPhasen extends SortedTableControl {


	private CoMonatseinsatzblattPhasen m_coMonatseinsatzblattPhasen;

	private FormPersonMonatseinsatzblatt m_formPersonMonatseinsatzblatt;
	
	
	/**
	 * Konstruktor
	 * 
	 * @param tableControl
	 * @param coMonatseinsatzblattPhasen
	 * @param formPersonMonatseinsatzblatt
	 * @throws Exception
	 */
	public TableMonatseinsatzblattPhasen(IControl tableControl, CoMonatseinsatzblattPhasen coMonatseinsatzblattPhasen, 
			FormPersonMonatseinsatzblatt formPersonMonatseinsatzblatt) throws Exception{
		super(tableControl);
		
		m_formPersonMonatseinsatzblatt = formPersonMonatseinsatzblatt;
		
		// Spalte Arbeitsplan darf nicht geändert werden
		enableColumn(CoMonatseinsatzblattPhasen.getResIdArbeitsplanID(), false);
		// wenn das nicht funktioniert über mayEdit wie im Monatseinsatzblatt

	}

//
//	/**
//	 * Prüft, ob die Zelle bearbeitet werden darf
//	 * 
//	 * @param bookmark
//	 * @param cell
//	 * @return
//	 * @throws Exception 
//	 */
//	@Override
//	public boolean mayEdit(Object bookmark, ISpreadCell cell) throws Exception {
//		int currentRowIndex, projektFieldIndex, arbeitszeit;
//		
//		if (cell.getField().getFieldDescription().get)
//		
//		// nur Projektspalten dürfen bearbeitet werden
//		if (projektFieldIndex < 0)
//		{
//			return false;
//		}
//		
//		// Personalabteilung darf für alle eintragen, außer für sich selbst, auch bei krank oder Urlaub
//		if (UserInformation.getInstance().isPersonalverwaltung() && !UserInformation.isPerson(m_coMonatseinsatzblattAnzeige.getPersonID()))
//		{
//			return true;
//		}
//		
//		
//		return true;
//	}



//	/**
//	 * Formular für den Tag anpassen
//	 * @throws Exception 
//	 * 
//	 * @see pze.ui.controls.SortedTableControl#tableSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
//	 */
//	@Override
//	public void tableSelected(IControl arg0, Object arg1) {
//		super.tableSelected(arg0, arg1);
//		
//		try
//		{
//			int tagDesMonats;
//			int projektFieldIndex, monatseinsatzblattID;
//			CoMonatseinsatzblatt coMonatseinsatzblatt;
//			VirtCoProjekt virtCoProjekt;
//			CoMonatseinsatzblattPhasen coMonatseinsatzblattPhasen;
//			
//			
//			// prüfen, ob es ein Projektfield ist
////			m_coMonatseinsatzblattAnzeige.moveToTag(getCurrentRowIndex() - m_coMonatseinsatzblattAnzeige.getAnzProjektzeilen());
//			projektFieldIndex = m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(getSelectedCell().getField());
//
//			// Tag des Monats wegen Aktualisierungen zwischenspeichern
//			tagDesMonats = m_coMonatseinsatzblattAnzeige.getTagDesMonats();
//			m_selectedTagDesMonats = tagDesMonats;
//			
//			if (projektFieldIndex < 0)
//			{
//				// erst Projektdaten, dann Einsatzdaten setzen (Oberfläche enthält Formularfelder aus beiden CO's)
//				m_formPersonMonatseinsatzblatt.setData(m_virtCoProjektDummy);
//				m_formPersonMonatseinsatzblatt.setData(m_coMonatseinsatzblattDummy);
//				m_formPersonMonatseinsatzblatt.setDataArbeitsplan(m_coMonatseinsatzblattPhasenDummy);
//				return;
//			}
//
//			// Projektdaten
//			virtCoProjekt = getVirtCoProjekt(projektFieldIndex);
//			m_formPersonMonatseinsatzblatt.setData(virtCoProjekt);
//
//
//			// Einsatzdaten
//			if (tagDesMonats < 1)
//			{
//				m_formPersonMonatseinsatzblatt.setData(m_coMonatseinsatzblattDummy);
//				m_formPersonMonatseinsatzblatt.setDataArbeitsplan(m_coMonatseinsatzblattPhasenDummy);
//			}
//			else
//			{
//				coMonatseinsatzblatt = m_coMonatseinsatzblattAnzeige.getCoMonatseinsatzblatt();
//				if (coMonatseinsatzblatt.moveTo(virtCoProjekt, tagDesMonats))
//				{
//					// erst Projektdaten, dann Einsatzdaten setzen (Oberfläche enthält Formularfelder aus beiden CO's)
//					m_formPersonMonatseinsatzblatt.setData(coMonatseinsatzblatt);
//					
//					// Zuordnung der Stunden zu Projektphasen
//					monatseinsatzblattID = coMonatseinsatzblatt.getID();
//					if (m_formPersonMonatseinsatzblatt.hasDataArbeitsplan(monatseinsatzblattID))
//					{
//						m_formPersonMonatseinsatzblatt.setDataArbeitsplan(monatseinsatzblattID);
//					}
//					else
//					{
//						coMonatseinsatzblattPhasen = new CoMonatseinsatzblattPhasen();
//						coMonatseinsatzblattPhasen.load(monatseinsatzblattID, virtCoProjekt);
//						m_formPersonMonatseinsatzblatt.setDataArbeitsplan(coMonatseinsatzblattPhasen);
//					}
//				}
//				else
//				{
//					m_formPersonMonatseinsatzblatt.setData(m_coMonatseinsatzblattDummy);
//					m_formPersonMonatseinsatzblatt.setDataArbeitsplan(m_coMonatseinsatzblattPhasenDummy);
//				}
//			}
//		} 
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
//	
//	/**
//	 * Beim Doppelklick ggf. das Propjekt oder den Tag öffnen
//	 * 
//	 * @see pze.ui.controls.SortedTableControl#tableDefaultSelected(framework.ui.interfaces.controls.IControl, java.lang.Object)
//	 */
//	public void tableDefaultSelected(IControl arg0, Object arg1) throws Exception{
//		int id;
//		int tagDesMonats;
//		int projektFieldIndex;
//		IField field;
//		VirtCoProjekt virtCoProjekt;
//
//
//		// prüfen, ob es ein Projektfield ist
//		field = getSelectedCell().getField();
//		projektFieldIndex = m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(field);
//
//		// Tag des Monats
//		tagDesMonats = m_coMonatseinsatzblattAnzeige.getTagDesMonats();
//
//		
//		// Projekt bei entsprechender Berechtigung öffnen
//		// Projektleiter brauchen hier nur Zugriff für die Berechnung der Stundenauswertung
//		if (UserInformation.getInstance().isProjektleiter() && tagDesMonats < 1 && projektFieldIndex >= 0 && !isBemerkungsfeld(projektFieldIndex))
//		{
//			// Projektdaten
//			virtCoProjekt = getVirtCoProjekt(projektFieldIndex);
//
//			// Abruf öffnen
//			id = virtCoProjekt.getAbrufID();
//			if (id > 0)
//			{
//				FormAbruf.open(getSession(), null, id);
//				return;
//			}
//
//			// Auftrag öffnen
//			id = virtCoProjekt.getAuftragID();
//			if (id > 0)
//			{
//				FormAuftrag.open(getSession(), null, id);
//			}
//		}
//
//		// Buchungen des Tages öffnen
//		if (tagDesMonats > 0 && field.equals(m_coMonatseinsatzblattAnzeige.getFieldTagDesMonats()))
//		{
//			FormPerson.open(getSession(), null, m_coPerson.getID()).showZeiterfassung(Format.getDatum(m_coMonatseinsatzblattAnzeige.getDatum(), tagDesMonats));
//		}
//	}

	
//	/**
//	 * Sortieren für diese Tabelle nicht möglich
//	 */
//	protected void sort() throws Exception {
//		
//	}
//	
	
//	/**
//	 * Bearbeiten des Feldes beenden und ggf. Daten aktualisieren
//	 * 
//	 * @param fld
//	 * @throws Exception
//	 */
//	@Override
//	public void endEditing(Object bookmark, IField fld) throws Exception { // TODO
//		CoMonatseinsatzblattProjekt coMonatseinsatzblattProjekt;
//		VirtCoProjekt virtCoProjekt;
//		
//		// Bemerkung
//		if (isBemerkungsfeld(0))
//		{
//			// Projekt bestimmen
//			virtCoProjekt = m_coMonatseinsatzblattAnzeige.getVirtCoProjekt();
//			virtCoProjekt.moveTo(m_coMonatseinsatzblattAnzeige.getProjektFieldIndex(fld));
//			
//			// Projektdaten laden
//			coMonatseinsatzblattProjekt = new CoMonatseinsatzblattProjekt();
//			coMonatseinsatzblattProjekt.load(m_coPerson.getID());
//			
//			// Bemerkung speichern
//			if (coMonatseinsatzblattProjekt.moveTo(virtCoProjekt))
//			{
//				coMonatseinsatzblattProjekt.begin();
//				coMonatseinsatzblattProjekt.setBemerkung(fld.getDisplayValue());
//				coMonatseinsatzblattProjekt.save();
//			}
//			else // Meldung, wenn die Bemerkung nicht gespeichert wird
//			{
//				Messages.showInfoMessage("Bemerkung nicht gespeichert", 
//						"Die Bemerkung wurde nicht gespeichert, da Sie das Projekt bereits aus Ihrer persönlichen Projektliste entfernt haben.");
//			}
//		}
//		// Stundenwerte
//		else
//		{
//			// Stundenwert aktualisieren
//			m_coMonatseinsatzblattAnzeige.update(fld);
//
//			// kein komplettes Refresh der Oberfläche, sonst geht die Selektion verloren
//			m_formPersonMonatseinsatzblatt.refreshTaetigkeitBemerkung();
//		}
//		
//		refresh(reasonDataChanged, null);
//	}

//	protected boolean beginEditing(Object bookmark, ISpreadCell cell) throws Exception {
//		if (!getData().isEditing())
//		{
//			getData().begin();
//		}
//		
//		return super.beginEditing(bookmark, cell);
//	}
	
}
