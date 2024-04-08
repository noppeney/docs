package pze.business.objects.personen;

import java.util.Date;

import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.UserInformation;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.auswertung.CoAuswertungAuszahlung;
import pze.business.objects.auswertung.CoAuswertungKontowerte;
import pze.business.objects.auswertung.CoAuswertungKontowerteZeitraum;


/**
 * CO mit den Daten zur Auswertung der Kontowerte.<br>
 * In dem CO sind nur die Fields aus CoKontowerte enthalten, die in dem Auswertungs-CO ausgewählt wurden.
 * 
 * 
 * @author Lisiecki
 */
public class CoKontowertAuswertung extends AbstractCacheObject {


	public CoKontowertAuswertung() {
	}


	/**
	 * Konstruktor
	 * 
	 * @param coAuswertungKontowerte
	 * @throws Exception
	 */
	public CoKontowertAuswertung(CoAuswertungKontowerte coAuswertungKontowerte) throws Exception{
		super();

		load(coAuswertungKontowerte);
	}
	

	/**
	 * Konstruktor
	 * 
	 * @param coAuswertungKontowerteZeitraum
	 * @throws Exception 
	 */
	public CoKontowertAuswertung(CoAuswertungKontowerteZeitraum coAuswertungKontowerteZeitraum) throws Exception {
		super();

		load(coAuswertungKontowerteZeitraum);
	}


	/**
	 * Konstruktor
	 * 
	 * @param coAuswertungAuszahlung
	 * @throws Exception 
	 */
	public CoKontowertAuswertung(CoAuswertungAuszahlung coAuswertungAuszahlung) throws Exception {
		super();

		load(coAuswertungAuszahlung);
		addRowSumme();
	}


	/**
	 * Daten gemäß der Auswertung laden
	 * 
	 * @param coAuswertungKontowerte
	 * @throws Exception
	 */
	private void load(CoAuswertungKontowerte coAuswertungKontowerte) throws Exception{
		CoKontowert coKontowert;
		
		coKontowert = new CoKontowert();
		coKontowert.load(coAuswertungKontowerte);

		setResID(coKontowert.getResID());

		addFields(coAuswertungKontowerte, coKontowert);
		addValues(coKontowert);
		
		setModified(false);
	}


	/**
	 * Daten gemäß der Auswertung laden
	 * 
	 * @param coAuswertungKontowerteZeitraum
	 * @throws Exception
	 */
	private void load(CoAuswertungKontowerteZeitraum coAuswertungKontowerteZeitraum) throws Exception {
		CoKontowert coKontowert;
		
		coKontowert = new CoKontowert();
		coKontowert.load(coAuswertungKontowerteZeitraum);

		setResID(coKontowert.getResID());

		addFields(coAuswertungKontowerteZeitraum, coKontowert);
		addValues(coKontowert);
		
		setModified(false);
	}


	/**
	 * Daten gemäß der Auswertung laden
	 * 
	 * @param coAuswertungAuszahlung
	 * @throws Exception
	 */
	private void load(CoAuswertungAuszahlung coAuswertungAuszahlung) throws Exception {
		CoKontowert coKontowert;
		
		coKontowert = new CoKontowert();
		coKontowert.load(coAuswertungAuszahlung);

		setResID(coKontowert.getResID());

		addFields(coAuswertungAuszahlung, coKontowert);
		addValues(coKontowert);
		
		setModified(false);
	}


	/**
	 * Fields gemäß der Auswertung hinzufügen
	 * 
	 * @param coAuswertungKontowerte
	 * @param coKontowert
	 */
	private void addFields(CoAuswertungKontowerte coAuswertungKontowerte, CoKontowert coKontowert) {

		// Person und Datum
		addField(coKontowert.getFieldPersonID().getFieldDescription());
		addField(coKontowert.getFieldDatum().getFieldDescription());
		
		// Stand Gleitzeitkonto
		if (coAuswertungKontowerte.isStandGleitzeitkontoAusgebenAktiv())
		{
			addField(coKontowert.getFieldStandGleitzeitkonto().getFieldDescription());
		}

		// Resturlaub
		if (coAuswertungKontowerte.isStandResturlaubAusgebenAktiv())
		{
			addField(coKontowert.getFieldResturlaub().getFieldDescription());
			
			
			if (UserInformation.getInstance().isPersonalverwaltung() && coAuswertungKontowerte.isStandResturlaubDetailsAusgebenAktiv())
			{
				addField(coKontowert.getFieldResturlaubGenehmigt().getFieldDescription());
				addField(coKontowert.getFieldRest().getFieldDescription());
				addField(coKontowert.getFieldResturlaubVerplant().getFieldDescription());
				addField(coKontowert.getFieldResturlaubOffen().getFieldDescription());
			}
		}
	}


	/**
	 * Fields gemäß der Auswertung hinzufügen
	 * 
	 * @param coAuswertungKontowerteZeitraum
	 * @param coKontowert
	 */
	private void addFields(CoAuswertungKontowerteZeitraum coAuswertungKontowerteZeitraum, CoKontowert coKontowert) {

		// Person und Datum
		addField(coKontowert.getFieldPersonID().getFieldDescription());
		addField("field.tblkontowert.datumvon");
		addField("field.tblkontowert.datumbis");
	
		// Arbeitstage
		if (coAuswertungKontowerteZeitraum.isAnzahlArbeitstageAusgebenAktiv())
		{
			addField("field.tblkontowert.anzahlarbeitstage");
		}

		// Arbeitszeit
		if (coAuswertungKontowerteZeitraum.isWertArbeitszeitAusgebenAktiv())
		{
			addField(coKontowert.getFieldArbeitszeit().getFieldDescription());
		}

		// Sollarbeitszeit
		if (coAuswertungKontowerteZeitraum.isWertSollarbeitszeitAusgebenAktiv())
		{
			addField(coKontowert.getFieldSollArbeitszeit().getFieldDescription());
		}

		// Überstunden
		if (coAuswertungKontowerteZeitraum.isWertUeberstundenAusgebenAktiv())
		{
			addField(coKontowert.getFieldUeberstunden().getFieldDescription());
		}

		// Änderung Gleitzeitkonto
		if (coAuswertungKontowerteZeitraum.isAenderungGleitzeitkontoAusgebenAktiv())
		{
			addField(coKontowert.getFieldAenderungGleitzeitkonto().getFieldDescription());
		}

		// Plusstunden
		if (coAuswertungKontowerteZeitraum.isWertPlusstundenAusgebenAktiv())
		{
			addField(coKontowert.getFieldPlusstunden().getFieldDescription());
		}

		// PlusstundenProjekt
		if (coAuswertungKontowerteZeitraum.isWertPlusstundenProjektAusgebenAktiv())
		{
			addField(coKontowert.getFieldPlusstundenProjekt().getFieldDescription());
		}

		// PlusstundenReise
		if (coAuswertungKontowerteZeitraum.isWertPlusstundenReiseAusgebenAktiv())
		{
			addField(coKontowert.getFieldPlusstundenReise().getFieldDescription());
		}

		// Minusstunden
		if (coAuswertungKontowerteZeitraum.isWertMinusstundenAusgebenAktiv())
		{
			addField(coKontowert.getFieldMinusstunden().getFieldDescription());
		}

		// Auszahlung Projektstunden
		if (coAuswertungKontowerteZeitraum.isWertAuszahlungProjektstundenAusgebenAktiv())
		{
			addField(coKontowert.getFieldAuszahlungUeberstundenProjekt().getFieldDescription());
		}

		// Auszahlung Reisestunden
		if (coAuswertungKontowerteZeitraum.isWertAuszahlungReisestundenAusgebenAktiv())
		{
			addField(coKontowert.getFieldAuszahlungUeberstundenReise().getFieldDescription());
		}

		// Anwesend
		if (coAuswertungKontowerteZeitraum.isWertAnwesendAusgebenAktiv())
		{
			addField(coKontowert.getFieldAnwesend().getFieldDescription());
		}

		// Dienstgang
		if (coAuswertungKontowerteZeitraum.isWertDienstgangAusgebenAktiv())
		{
			addField(coKontowert.getFieldDienstgang().getFieldDescription());
		}

		// Dienstreise
		if (coAuswertungKontowerteZeitraum.isWertDienstreiseAusgebenAktiv())
		{
			addField(coKontowert.getFieldDienstreise().getFieldDescription());
		}

		// Reisezeit
		if (coAuswertungKontowerteZeitraum.isWertReisezeitAusgebenAktiv())
		{
			addField(coKontowert.getFieldReisezeit().getFieldDescription());
		}

		// Vorlesung
		if (coAuswertungKontowerteZeitraum.isWertVorlesungAusgebenAktiv())
		{
			addField(coKontowert.getFieldVorlesung().getFieldDescription());
		}

		// Pause
		if (coAuswertungKontowerteZeitraum.isWertPauseAusgebenAktiv())
		{
			addField(coKontowert.getFieldPause().getFieldDescription());
		}

		// Pausenänderung
		if (coAuswertungKontowerteZeitraum.isWertPausenAenderungAusgebenAktiv())
		{
			addField(coKontowert.getFieldPausenaenderung().getFieldDescription());
		}

		// Arbeitsunterbrechung
		if (coAuswertungKontowerteZeitraum.isWertArbeitsunterbrechungAusgebenAktiv())
		{
			addField(coKontowert.getFieldArbeitsunterbrechung().getFieldDescription());
		}

		// priv. Unterbrechung
		if (coAuswertungKontowerteZeitraum.isWertPrivateUnterbrechungAusgebenAktiv())
		{
			addField(coKontowert.getFieldPrivateUnterbrechung().getFieldDescription());
		}

		// Wert Krank
		if (coAuswertungKontowerteZeitraum.isWertKrankAusgebenAktiv())
		{
			addField(coKontowert.getFieldWertKrank().getFieldDescription());
		}

		// Anzahl Krank
		if (coAuswertungKontowerteZeitraum.isAnzahlKrankAusgebenAktiv())
		{
			addField(coKontowert.getFieldAnzahlKrank().getFieldDescription());
		}

		// Krank ohne Lfz
		if (coAuswertungKontowerteZeitraum.isAnzahlKrankOhneLfzAusgebenAktiv())
		{
			addField(coKontowert.getFieldAnzahlKrankOhneLfz().getFieldDescription());
		}

		// Urlaub
		if (coAuswertungKontowerteZeitraum.isAnzahlUrlaubAusgebenAktiv())
		{
			addField(coKontowert.getFieldAnzahlUrlaub().getFieldDescription());
		}

		// Sonderurlaub
		if (coAuswertungKontowerteZeitraum.isAnzahlSonderurlaubAusgebenAktiv())
		{
			addField(coKontowert.getFieldAnzahlSonderurlaub().getFieldDescription());
		}

		// FA
		if (coAuswertungKontowerteZeitraum.isAnzahlFaAusgebenAktiv())
		{
			addField(coKontowert.getFieldAnzahlFa().getFieldDescription());
		}

		// Elternzeit
		if (coAuswertungKontowerteZeitraum.isAnzahlElternzeitAusgebenAktiv())
		{
			addField(coKontowert.getFieldAnzahlElternzeit().getFieldDescription());
		}
		
	}


	/**
	 * Fields gemäß der Auswertung hinzufügen
	 * 
	 * @param coAuswertungAuszahlung
	 * @param coKontowert
	 */
	private void addFields(CoAuswertungAuszahlung coAuswertungAuszahlung, CoKontowert coKontowert) {

		// Person, Datum, Status
		addField(coKontowert.getFieldPersonID().getFieldDescription());
		addField(coKontowert.getFieldDatum().getFieldDescription());
		addField(coKontowert.getFieldStatusIDAuszahlung().getFieldDescription());
		
		// Stundenwerte
		addField(coKontowert.getFieldAuszahlungUeberstundenProjekt().getFieldDescription());
// TODO Bearbeitung der Auszahlungen (Status) in Auswertung aktivieren		((FieldDescription) coKontowert.getFieldAuszahlungUeberstundenProjekt().getFieldDescription()).setEnabled(false);
		// irgendwo (evtl. beim deactive()) wieder aktivieren, da dies sonst immer für das Field gibt, auch beim Bearbeiten von Kontowerten
		addField(coKontowert.getFieldAuszahlungUeberstundenReise().getFieldDescription());
		addField(coKontowert.getFieldAuszahlbareUeberstundenProjekt().getFieldDescription());
		addField(coKontowert.getFieldAuszahlbareUeberstundenReise().getFieldDescription());
		addField(coKontowert.getFieldStandGleitzeitkonto().getFieldDescription());
	}


	/**
	 * Co mit Daten füllen
	 * 
	 * @param coKontowert
	 * @throws Exception
	 */
	private void addValues(CoKontowert coKontowert) throws Exception {
		int iField;
		int anzFields;
		IField field;
		begin();
		
		if (!coKontowert.moveFirst())
		{
			return;
		}
		
		// coKontowert durchlaufen und Daten übertragen
		anzFields = getColumnCount();
		do
		{
			add();
			for (iField=0; iField<anzFields; ++iField)
			{
				field = getField(iField);
				field.setValue(coKontowert.getField(field.getFieldDescription().getResID()).getValue());
			}
		} while (coKontowert.moveNext());
		
	}


	public Date getDatumVon() {
		return Format.getDateValue(getField("field.tblkontowert.datumvon").getValue());
	}


}
