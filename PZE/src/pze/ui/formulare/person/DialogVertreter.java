package pze.ui.formulare.person;

import java.util.Date;

import framework.business.interfaces.FW;
import framework.business.interfaces.refresh.IRefreshable;
import framework.cui.layout.UniLayout;
import framework.ui.form.UniForm;
import pze.business.Format;
import pze.business.Messages;
import pze.business.UserInformation;
import pze.business.objects.personen.CoPerson;
import pze.business.objects.personen.CoVertreter;
import pze.business.objects.reftables.personen.CoPosition;
import pze.ui.controls.SortedTableControl;


/**
 * Dialog zum Eintragen der Vertreter für Urlaub etc.
 * 
 * @author Lisiecki
 */
public class DialogVertreter extends UniForm {

	private final static String RESID = "dialog.urlaub.vertreter";

	private SortedTableControl m_table;
	
	private CoVertreter m_coVertreter;

	
	/**
	 * Konstruktion
	 * 
	 * @throws Exception
	 */
	private DialogVertreter() throws Exception {
		super(null, RESID);		
		super.createChilds();
		
		UniLayout layout = new UniLayout();
		super.setLayout(layout);
		layout.setControl(this);
		
	}


	/**
	 * Dialog öffnen
	 * 
	 * @return OK geklickt
	 * @throws Exception
	 */
	public static boolean showDialog(int personID, Date  datum, Date datumBis, boolean check) throws Exception {
		DialogVertreter dialog;

		dialog = new DialogVertreter();
		
		// Controls festlegen
		dialog.initControls(personID, datum, datumBis);

		// Dialog anzeigen
		dialog.refresh(reasonDataChanged, null);
		dialog.getDialog().show();

		// wenn nicht OK geklickt wurde beenden
		if (dialog.getDialog().getRetVal() != FW.OK)
		{
			return false;
		}

		// Prüfung, ob ohne Vertreter gespeichert werden darf
		int positionID;
		boolean speichernErfolgreich;
		CoPerson coPerson = CoPerson.getInstance();
		coPerson.moveToID(personID);
		
		positionID = coPerson.getPositionID();

		// Vertreter speichern
		CoVertreter coVertreter;
		coVertreter = dialog.getCoVertreter();
		speichernErfolgreich = coVertreter.save(personID, datum, datumBis);
		
		// wenn keine Prüfung erfolgen soll, Methode beenden
		if (!check)
		{
			return true;
		}
		
		// bis zu 2 Tage können ohne Vertreter gespeichert werden
		if (positionID != CoPosition.ID_KL && positionID != CoPosition.ID_TL // KL/TL haben keinen Vertreter
				&& getAnzahlTageOhneVertreter(personID, datum, datumBis) > 2
				// Personalverwaltung darf für andere auch für mehr Tage ohne Vertreter den Antrag erstellen
				&& (UserInformation.isPerson(personID) || !UserInformation.getInstance().isPersonalverwaltung()))
		{
			Messages.showErrorMessage("Der Antrag kann nicht erstellt werden, da für mehr als 2 Tage kein Vertreter angegeben ist.");
			return showDialog(personID, datum, datumBis, check);
		}

		// wenn kein gültiger Vertreter eingegeben wurde, Abfrage ob trotzdem gespeichert werden soll
		if (!speichernErfolgreich)
		{
			if (!Messages.showYesNoMessage("Kein Vertreter eingegeben", "Sie haben keinen Vertreter eingegeben. Möchten Sie den Antrag trotzdem erstellen?"))
			{
				return showDialog(personID, datum, datumBis, check);
			}
		}

		return true;
	}


	/**
	 * Anzahl der Urlaubstage ohne Vertreter bestimmen
	 * 
	 * @param personID
	 * @param datum
	 * @param datumBis
	 * @return
	 * @throws Exception
	 */
	private static int getAnzahlTageOhneVertreter(int personID, Date datum, Date datumBis) throws Exception {
		int anzTageOhneVertreter;
		Date aktDatum;
		CoPerson coPerson;
		CoVertreter coVertreter;
		
		anzTageOhneVertreter = 0;
		aktDatum = datum;
		coPerson = new CoPerson();
		coPerson.loadByID(personID);
		coVertreter = new CoVertreter();
		
		do
		{
			// prüfen, ob es ein Arbeitstag ist
			if (coPerson.isArbeitstag(aktDatum))
			{
				// Vertreter für den Tag laden
				coVertreter.loadVertreter(personID, aktDatum, aktDatum);
				coVertreter.moveFirst();

				if (coVertreter.getVertreterID() == 0)
				{
					++anzTageOhneVertreter;
				}
			}
			aktDatum = Format.getDateVerschoben(aktDatum, 1);
		} while (!aktDatum.after(datumBis));
		
		return anzTageOhneVertreter;
	}


	/**
	 * Controls initialisieren
	 * @param datumBis 
	 * @param datum 
	 * @param personID 
	 * @throws Exception 
	 */
	private void initControls(int personID, Date datum, Date datumBis) throws Exception {
		
		m_coVertreter = new CoVertreter();
		m_coVertreter.loadVertreter(personID, datum, datumBis);
		
		m_table = new SortedTableControl(findControl("spread.urlaub.vertreter"));
		
		// nur aktive Personen anzeigen
		CoPerson coPerson;
		coPerson = new CoPerson();
		coPerson.loadItemsAktivIntern();
		coPerson.addEmtyItem();
		m_coVertreter.getFieldVertreterID().setItems(coPerson);
		
		m_table.setData(m_coVertreter);
		m_table.refresh(IRefreshable.reasonDataChanged, null);
		m_table.refresh(IRefreshable.reasonItemsChanged, null);
		m_table.refresh(reasonEnabled, null);
	}

	
	private CoVertreter getCoVertreter() {
		return m_coVertreter;
	}

}


