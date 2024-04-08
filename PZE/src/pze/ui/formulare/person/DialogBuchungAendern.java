package pze.ui.formulare.person;

import java.util.Date;

import framework.cui.layout.UniLayout;
import pze.business.Format;
import pze.business.Messages;
import pze.business.objects.CoMessage;
import pze.business.objects.personen.CoBuchung;
import pze.business.objects.personen.CoVertreter;
import pze.business.objects.reftables.buchungen.CoBuchungsart;
import pze.business.objects.reftables.buchungen.CoStatusBuchung;
import pze.business.objects.reftables.buchungen.CoStatusGenehmigung;

/**
 * Dialog zum Ändern einer Buchung, z. B. bei der Beantragung einer Freigabe
 * 
 * @author Lisiecki
 */
public class DialogBuchungAendern extends DialogBuchung {
	

	private Date m_datumOriginal;  
	private Date m_datumBisOriginal;  
	private int m_uhrzeitOriginal;  
	private int m_uhrzeitBisOriginal;  

	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogBuchungAendern(String resID) throws Exception {
		super(resID);		
		super.createChilds();
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
		// Controls festlegen
		initControls();
	}

	
	/**
	 * Dialog mit der angegebenen Buchung öffnen
	 * 
	 * @param buchungID
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static boolean showDialogWithBuchung(CoBuchung coBuchung) throws Exception {
		DialogBuchungAendern dialog;

		dialog = new DialogBuchungAendern(RESID_EINGABE);
		dialog.loadBuchung(coBuchung);
		
		return showDialog(dialog);			
	}
	
	
	/**
	 * Dialog mit der angegebenen Buchung laden und ggf. geänderte Uhrzeiten speichern
	 * 
	 * @param buchungID
	 * @return Daten wurden geändert
	 * @throws Exception
	 */
	public static void loadAndSave(CoBuchung coBuchung) throws Exception {
		DialogBuchungAendern dialog;

		// Daten laden
		dialog = new DialogBuchungAendern(RESID_EINGABE);
		dialog.loadBuchung(coBuchung);
		
		// Zeiten übertragen, da CO neu aus der DB geladen wird
		m_coBuchung.setUhrzeitAsInt(coBuchung.getUhrzeitAsInt());
		m_coBuchung.setUhrzeitBis(coBuchung.getUhrzeitBisAsInt());
		dialog.m_uhrzeitBisOriginal = Format.getIntValue(coBuchung.getFieldUhrzeitBis().getOriginalValue());

		// speichern
		dialog.save();	
	}
	

	/**
	 * Buchung laden
	 * 
	 * @param id ID der Buchung
	 * @throws Exception
	 */
	private void loadBuchung(CoBuchung coBuchung) throws Exception {
		int uhrzeit;
		
		// Buchung erstellen
		m_coBuchung = new CoBuchung();
		m_coBuchung.loadByID(coBuchung.getID());
		m_coBuchung.begin();
		
		// ggf. Datum und Uhrzeit bis anpassen
//		m_coBuchung.addFieldDatumBis();
		m_coBuchung.addFieldUhrzeitBis();
		m_coBuchung.setDatumBis(coBuchung.getDatumBis());
		uhrzeit = coBuchung.getUhrzeitBisAsInt();
		if (uhrzeit > 0)
		{
			m_coBuchung.setUhrzeitBis(uhrzeit);
		}

		// Daten der Oberfläche zuweisen
		setData(m_coBuchung);
		checkDatumKalener();
		checkDatumKalener(coBuchung.getDatumBis(), m_kalenderBis);

		// CO kopieren für Vergleich der Änderung (OriginalValue funktioniert nicht weil "bis"-Daten nachgeladen werden)
		m_datumOriginal = Format.getDate12Uhr(m_coBuchung.getDatum());
		m_datumBisOriginal = Format.getDate12Uhr(m_coBuchung.getDatumBis());
		m_uhrzeitOriginal = m_coBuchung.getUhrzeitAsInt();
		m_uhrzeitBisOriginal = uhrzeit;
	}



	/**
	 * Buchung speichern.<br>
	 * Beim Erstellen einer neuen Buchung werden ggf. mehrere Buchungen erzeugt.
	 * 
	 * @param dialog
	 * @return 
	 * @throws Exception
	 */
	@Override
	protected int save() throws Exception {
		int statusGenehmigungID, counter, uhrzeit, uhrzeitBis, buchungsartID;
		boolean statusAendern;
		CoBuchung coBuchung;
		
		statusGenehmigungID = m_coBuchung.getStatusGenehmigungID();
		uhrzeit = m_coBuchung.getUhrzeitAsInt();
		uhrzeitBis = m_coBuchung.getUhrzeitBisAsInt();
		buchungsartID = m_coBuchung.getBuchungsartID();
		counter = 0;
		
		
		
		// bei genehmigten und abgelehnten Buchungen muss der Status geändert werden
		statusAendern = CoStatusGenehmigung.isGenehmigt(statusGenehmigungID) || CoStatusGenehmigung.isAbgelehnt(statusGenehmigungID);
		
		// je nach Buchungsart unterscheiden
		if (CoBuchungsart.isUrlaub(buchungsartID))
		{
			// damit die Buchung nicht geöffnet wird
			m_nurBemerkungBearbeiten = true;

			return saveUrlaub();
		}
		else if (CoBuchungsart.isUhrzeitAendernErlaubt(m_coBuchung.getBuchungsartID()))
		{
			// damit die Buchung nicht geöffnet wird
			m_nurBemerkungBearbeiten = true;

			// bei bereits genehmigten Anträgen Kommentar anpassen, alte Zeiten reinschreiben
			if (CoStatusGenehmigung.isGenehmigt(statusGenehmigungID))
			{
				m_coBuchung.setBemerkung("bisher " + (m_uhrzeitOriginal > 0 ? Format.getZeitAsText(m_uhrzeitOriginal) + " ": "")
						+ (m_uhrzeitBisOriginal > 0 ? "bis " + Format.getZeitAsText(m_uhrzeitBisOriginal) + " ": "") 
						+ (m_coBuchung.getBemerkung() == null ? "" : m_coBuchung.getBemerkung()));
			}
			
			// Uhrzeit wurde geändert
			if (uhrzeit != m_uhrzeitOriginal)
			{
				if (statusAendern)
				{
					m_coBuchung.setStatusVorlaeufig();
					m_coBuchung.setStatusGenehmigungGeplant();
				}
				m_coBuchung.updateGeaendertVonAm();
				m_coBuchung.save();
			}
			
			// Uhrzeit Bis wurde geändert
			if (uhrzeitBis != m_uhrzeitBisOriginal)
			{
				// Endbuchung laden
				coBuchung = new CoBuchung();
				coBuchung.loadAntragEnde(m_coBuchung);

				if (coBuchung.getBuchungsartID() == CoBuchungsart.ID_KOMMEN || coBuchung.getBuchungsartID() == CoBuchungsart.ID_GEHEN
						|| coBuchung.getBuchungsartID() == CoBuchungsart.ID_ENDE_DIENSTGANG_DIENSTREISE)
				{
					coBuchung.begin();
					coBuchung.setUhrzeitAsInt(uhrzeitBis);
					
					if (statusAendern)
					{
						coBuchung.setStatusVorlaeufig();
						coBuchung.setStatusGenehmigungGeplant();
						
						// Anfangsbuchung auch aktualisieren, damit der Antrag aktualisiert wird
						if (!m_coBuchung.isEditing())
						{
							m_coBuchung.begin();
						}
						m_coBuchung.setStatusVorlaeufig();
						m_coBuchung.setStatusGenehmigungGeplant();
						m_coBuchung.updateGeaendertVonAm();
						m_coBuchung.save();
					}
					coBuchung.updateGeaendertVonAm();
					coBuchung.save();
				}
				else
				{
					Messages.showErrorMessage("Endzeit kann nicht geändert werden", 
							"Die Endzeit ist nicht die Uhrzeit einer Kommen-, Gehen- oder Ende DR/DG-Buchung."
							+ " Bitte ändern Sie die entsprechende Buchung '" + coBuchung.getBuchungsart() + "'.");
				}
			}
		}
		
		return counter;
	}


	/**
	 * Geänderte Urlaubs-Buchungen speichern
	 * 
	 * @return
	 * @throws Exception
	 */
	private int saveUrlaub() throws Exception {
		int statusID, counter;
		Date datumGeaendert, datumBisGeaendert; 
		
		statusID = m_coBuchung.getStatusID();
		datumGeaendert = Format.getDate12Uhr(m_coBuchung.getDatum());
		datumBisGeaendert = Format.getDate12Uhr(m_coBuchung.getDatumBis());
		counter = 0;
		
		
		// bei abgelehntem Urlaub, der hier geändert wird, wird der Status wieder auf vorläufig gesetzt
		if (m_coBuchung.isAbgelehnt())
		{
			m_coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_GEPLANT);
		}
		
		// zum Löschen Methoden aus FormUrlaubsplanung, ggf. dort mit diesen vereinigen (Vertreter auch in coBuchung löschen)
		
		// Buchungen am Anfang der Original-Buchung löschen
		if (datumGeaendert.after(m_datumOriginal))
		{
			m_coBuchung.setDatum(m_datumOriginal);
			m_coBuchung.setDatumBis(!datumGeaendert.after(m_datumBisOriginal) ? Format.getDateVerschoben(datumGeaendert, -1) : m_datumBisOriginal);
			
			// Anzahl geänderter Einträge zählen
			counter += m_coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_GELOESCHT);

			CoVertreter coVertreter;
			coVertreter = new CoVertreter();
			coVertreter.deleteVertreter(m_coBuchung.getPersonID(), m_coBuchung.getDatum(), m_coBuchung.getDatumBis());
		}
		
		// Buchungen am Ende der Original-Buchung löschen
		if (datumBisGeaendert.before(m_datumBisOriginal))
		{
			m_coBuchung.setDatum(!datumBisGeaendert.before(m_datumOriginal) ? Format.getDateVerschoben(datumBisGeaendert, 1) : m_datumOriginal);
			m_coBuchung.setDatumBis(m_datumBisOriginal);

			// Anzahl geänderter Einträge zählen
			counter += m_coBuchung.createFreigabeUrlaub(CoStatusGenehmigung.STATUSID_GELOESCHT);

			CoVertreter coVertreter;
			coVertreter = new CoVertreter();
			coVertreter.deleteVertreter(m_coBuchung.getPersonID(), m_coBuchung.getDatum(), m_coBuchung.getDatumBis());
		}
		
		
		// hier für neue Buchungen Datum anpassen und Methoden zum speichern nutzen
		
		// neue Buchung vor der Original-Buchung
		if (datumGeaendert.before(m_datumOriginal))
		{
			m_coBuchung.setIsNew();
			m_coBuchung.setID(m_coBuchung.nextID());
			m_coBuchung.setStatusID(CoStatusBuchung.STATUSID_VORLAEUFIG);
			m_coBuchung.setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_GEPLANT);
			m_coBuchung.updateGeaendertVonAm();
			m_coBuchung.setErstelltAm(new Date());
			m_coBuchung.setDatum(datumGeaendert);
			m_coBuchung.setDatumBis(!datumBisGeaendert.before(m_datumOriginal) ? Format.getDateVerschoben(m_datumOriginal, -1) : datumBisGeaendert);
			
			// Anzahl geänderter Einträge zählen
			berechneAnzahlTage();
			counter += super.save();
		}
		
		
		// neue Buchung nach der Original-Buchung
		if (datumBisGeaendert.after(m_datumBisOriginal))
		{
			m_coBuchung.setIsNew();
			m_coBuchung.setID(m_coBuchung.nextID());
			m_coBuchung.setStatusID(CoStatusBuchung.STATUSID_VORLAEUFIG);
			m_coBuchung.setStatusGenehmigungID(CoStatusGenehmigung.STATUSID_GEPLANT);
			m_coBuchung.updateGeaendertVonAm();
			m_coBuchung.setErstelltAm(new Date());
			m_coBuchung.setDatum(!datumGeaendert.after(m_datumBisOriginal) ? Format.getDateVerschoben(m_datumBisOriginal, +1) : datumGeaendert);
			m_coBuchung.setDatumBis(datumBisGeaendert);
			
			// Anzahl geänderter Einträge zählen
			berechneAnzahlTage();
			counter += super.save();
		}
		
		
		// ggf. Info an die Buchhaltung, wenn bereits genehmigte Buchungen geändert werden
		new CoMessage().createMessageUrlaubGeaendert(m_coBuchung.getPersonID(), m_datumOriginal, m_datumBisOriginal, datumGeaendert, datumBisGeaendert,
				m_coBuchung.getBuchungsart(), statusID, counter);
		
		return counter;
	}


	/**
	 * Nur einzelne Felder sind editierbar.
	 * 
	 * @see framework.cui.controls.base.BaseCompositeControl#refresh(int, java.lang.Object)
	 */
	@Override
	public void refresh(int reason, Object element) {
		
		// nur wenn bereits eine Buchung geladen wurde
		if (m_coBuchung == null)
		{
			return;
		}
		
		// per default alles deaktivieren
		super.refresh(reasonDisabled);

		
		// bei OFA, FA... Uhrzeit ändern
		if (CoBuchungsart.isUhrzeitAendernErlaubt(m_coBuchung.getBuchungsartID()))
		{
			m_tfUhrzeit.refresh(reasonEnabled, null);
			m_tfUhrzeitBis.refresh(reasonEnabled, null);
		}
		else if (CoBuchungsart.isUrlaub(m_coBuchung.getBuchungsartID())) // bei Urlaub Datum bis bearbeiten 
		{
			m_tfDatum.refresh(reasonEnabled, null);
			m_tfDatumBis.refresh(reasonEnabled, null);
		}
	
		
		// Kalender aktualisieren
		m_kalender.setEnabled(m_tfDatum.isEnabled()); 
		m_kalenderBis.setEnabled(m_tfDatumBis.isEnabled()); 

		// Grund der Änderung und Bemerkung können immer geändert werden
		m_comboGrundAenderung.refresh(reasonEnabled, null);
		m_tfBemerkung.refresh(reasonEnabled, null);

		// Freigabe kann angezeigt werden, sofern vorhanden
		if (m_btFreigabeAnzeigen != null && m_coBuchung.getStatusGenehmigungID() != 0)
		{
			m_btFreigabeAnzeigen.refresh(reasonEnabled, null);
		}
	}

}


