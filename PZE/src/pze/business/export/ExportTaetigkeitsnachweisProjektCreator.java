package pze.business.export;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import pze.business.Format;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.monatseinsatzblatt.CoMonatseinsatzblattAnzeige;
import pze.business.objects.projektverwaltung.CoProjekt;
import pze.business.objects.projektverwaltung.VirtCoProjekt;
import pze.ui.formulare.UniFormWithSaveLogic;
import pze.ui.formulare.projektverwaltung.FormAuswertungProjekt;


/**
 * Klasse zum Erstellen einer HTML-Datei für den PDF-Export des Tätigkeitsnachweises für ein einzelnes Projekt
 * 
 * @author Lisiecki
 */
public class ExportTaetigkeitsnachweisProjektCreator extends ExportTaetigkeitsnachweisCreator {

	private FormAuswertungProjekt m_formAuswertungProjekt;



	@Override
	protected String getMonat() {
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(m_formAuswertungProjekt.getDatumExport());

		return Format.getMonat(gregDatum) + " " + gregDatum.get(Calendar.YEAR);
	}


	@Override
	protected String getName() {
		return m_coPerson.getBezeichnung();
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportTaetigkeitsnachweisCreator#writeSeiten(pze.ui.formulare.UniFormWithSaveLogic)
	 */
	@Override
	protected int writeSeiten(UniFormWithSaveLogic formAuswertungProjekt) throws Exception {
		int iProjekt, anzProjekte, anzProjekteMitDaten;
		int personID, auftragID, abrufID;
		Date datum;
		CoProjekt coProjekt;
		VirtCoProjekt virtCoProjekt;


		auftragID = 0;
		abrufID = 0;
		anzProjekteMitDaten = 0;
		
		m_formAuswertungProjekt = (FormAuswertungProjekt) formAuswertungProjekt;
		datum = m_formAuswertungProjekt.getDatumExport();

		// Projektdaten laden
		coProjekt = m_formAuswertungProjekt.getCoProjekt();
		if (coProjekt.isAuftrag())
		{
			auftragID = coProjekt.getID();
		}
		else if (coProjekt.isAbruf())
		{
			abrufID = coProjekt.getID();
		}
		
		// alle Personen mit Tätigkeiten laden
		m_coPerson = new CoPerson();
		m_coPerson.loadByProjekt(coProjekt, datum);
		if (!m_coPerson.moveFirst())
		{
			return 0;
		}
		
		// Personen durchlaufen
		do 
		{
			// Monatseinsatzblatt für die Person laden
			personID = m_coPerson.getID();
			m_coMonatseinsatzblattAnzeige = new CoMonatseinsatzblattAnzeige(personID, datum);
			virtCoProjekt = getVirtCoProjekt();

			// Anzahl Projekte prüfen
			anzProjekte = getAnzProjekte();
			if (anzProjekte == 0 || !virtCoProjekt.moveFirst())
			{
				return 0;
			}

			// Projekte durchlaufen
			iProjekt = 0;
			m_kggProjektNr = 0;
			m_mapKggZeit = new HashMap<Integer, Integer>();
			m_mapKggTaetigkeiten = new HashMap<Integer, String>();
			do 
			{
				// das richtige Projekt herausfiltern
				if (virtCoProjekt.getAuftragID() == auftragID || (auftragID == 0 && virtCoProjekt.getAbrufID() == abrufID))
				{
					// Seite des Tätigkeitsnachweis erstellen
					if (writeSeite(iProjekt, anzProjekte))
					{
						++anzProjekteMitDaten;
					}
					
					// ggf. die Nr. der KGG-Projekte speichern
					if (m_kggProjektNr == 0 && m_mapKggTaetigkeiten.size() > 0)
					{
						m_kggProjektNr = iProjekt;
					}

				}
				++iProjekt;
			} while (virtCoProjekt.moveNext());

			// KGG-Tätigkeitsnachweis
			if (m_kggProjektNr > 0)
			{
				++anzProjekteMitDaten;
				writeSeite(m_kggProjektNr, anzProjekte, getStringTabellenDatenKGG());
			}

		} while (m_coPerson.moveNext());

		return anzProjekteMitDaten;
	}


	/*
	 * (non-Javadoc)
	 * @see pze.business.export.ExportTaetigkeitsnachweisCreator#getVirtCoProjekt()
	 */
	@Override
	protected VirtCoProjekt getVirtCoProjekt() {
		VirtCoProjekt virtCoProjekt;
		
		virtCoProjekt = m_coMonatseinsatzblattAnzeige.getVirtCoProjekt();
		virtCoProjekt.moveFirst();
		
		return virtCoProjekt;
	}

}
