package pze.business.objects.personen;

import java.util.Date;

import framework.Application;
import framework.business.interfaces.fields.IField;
import pze.business.Format;
import pze.business.objects.AbstractCacheObject;
import pze.business.objects.CoZeitmodell;

/**
 * CacheObject für die Zuordnung der Pausenmodelle zu Personen
 * 
 * @author Lisiecki
 *
 */
public class CoPersonZeitmodell extends AbstractCacheObject {

	public static final String TABLE_NAME = "stblpersonzeitmodell";




	/**
	 * Kontruktor
	 */
	public CoPersonZeitmodell() {
		super("table." + TABLE_NAME);
	}
	

	public void loadByPersonID(int personID) throws Exception {
		Application.getLoaderBase().load(this, "PersonID=" + personID, "gueltigVon");
	}

	
	public IField getFieldZeitmodellID(){
		return getField("field." + getTableName() + ".zeitmodellid");
	}
	
	
	public int getZeitmodellID(){
		return Format.getIntValue(getFieldZeitmodellID().getValue());
	}
	
	
	/**
	 * Bestimmt das an dem übergebenen Datum gültige Zeitmodell
	 * 
	 * @param datum
	 * @return
	 * @throws Exception 
	 */
	public CoZeitmodell getCoZeitmodell(Date datum) throws Exception{
		CoZeitmodell coZeitmodell;
		
		// rückwärts suchen, bis ein gültiges Zeitmodell gefunden wurde
		if (!moveLast() || datum == null)
		{
			return null;
		}
		
		do
		{
			if (datum.after(Format.getDate0Uhr(getDateGueltigVon())))
			{
				coZeitmodell = new CoZeitmodell();
				coZeitmodell.loadByID(getZeitmodellID());
				
				return coZeitmodell;
			}
		} while (movePrev());
		
		return null;
	}
	

	/**
	 * Beginn des Zeitmodells
	 * 
	 * @return
	 */
	public Date getDateGueltigVon() {
		return Format.getDateValue(getField("field." + getTableName() + ".gueltigvon").getValue());
	}
	
	
	/**
	 * Ende des Zeitmodells
	 * 
	 * @return
	 */
	public Date getDateGueltigBis() {
		return Format.getDateValue(getField("field." + getTableName() + ".gueltigbis").getValue());
	}
	

	/**
	 * Neuen Eintrag für die Person anlegen
	 * 
	 * @param personID
	 * @return
	 * @throws Exception
	 */
	public int createNew(int personID) throws Exception {
		int id = super.createNew();
		
		setPersonID(personID);
		
		return id;
	}
	
	 
	 /**
	  * Es darf nur ein Zeitmodell ohne Enddatum geben.
	  * 
	  * @see pze.business.objects.AbstractCacheObject#validate()
	  */
	@Override
	public String validate() throws Exception {
		boolean isOhneEnde, isBeginnPzeGeprueft;
		String result;
		Date gueltigVon, gueltigBis, lastAustritt, beginnPze, endePze, naechsterTag;
		CoPerson coPerson;
		

		// überschriebene Methode aufrufen
		result = super.validate();
		if (result != null || getRowCount() == 0)
		{
			return result;
		}

		lastAustritt = null;
		isOhneEnde = false;
		isBeginnPzeGeprueft = false;
		
		coPerson = new CoPerson();
		coPerson.loadByID(getPersonID());
		beginnPze = Format.getDate12Uhr(coPerson.getBeginnPze());
		endePze = Format.getDate12Uhr(coPerson.getEndePze());

		// sortieren, um Überlappungen herauszufiltern
		sort("field." + getTableName() + ".gueltigvon", false);

		// CO durchlaufen und prüfen, ob nur ein Austrittsdatum fehlt 
		if (moveFirst())
		{
			do 
			{
//				if (isGeloescht())
//				{
//					continue;
//				}
				
				// Zeitmodell muss ausgewählt sein
				if (getZeitmodellID() == 0)
				{
					return "Zeitmodell fehlt.";
				}
				
				gueltigVon = Format.getDate12Uhr(getDateGueltigVon());
				gueltigBis = Format.getDate12Uhr(getDateGueltigBis());

				// ohne Ein- und Austritt ist nur bei einer Firma erlaubt
				if (gueltigVon == null)
				{
					return "Für jedes Zeitmodell muss angegeben werden, ab wann es gültig ist.";
				}
				
				// ohne Enddatum ist nur bei dem letzten Zeitmodell erlaubt
				if (isOhneEnde)
				{
					return "Nur das letzte Zeitmodell darf ohne Enddatum sein.";
				}
				
				// Beginn PZE prüfen
				if (!isBeginnPzeGeprueft && beginnPze != null)
				{
					if (gueltigVon.after(beginnPze))
					{
						return getFehlertextZeitraumOhneZeitmodell(gueltigVon, beginnPze);
					}
					
					isBeginnPzeGeprueft = true;
				}
				
				// merken, ob ein Datensatz ohne Ende existiert 
				if (gueltigBis == null)
				{
					isOhneEnde = true;
				}
				
				// Eintrittsdatum muss vor dem Austrittsdatum liegen
				if (gueltigBis != null && gueltigVon != null && gueltigVon.after(gueltigBis))
				{
					return "Das Anfangsdatum muss vor dem Enddatum liegen.";
				}
				
				// Eintrittsdatum muss nach dem letzten Austrittsdatum liegen
				if (lastAustritt != null && !gueltigVon.after(lastAustritt))
				{
					return "Das Anfangsdatum muss nach dem vorherigen Enddatum liegen.";
				}
				
				// Es darf kein Zeitraum ohne Zeitmodell existieren
				naechsterTag = Format.getDateVerschoben(lastAustritt, 1);
				if (lastAustritt != null && naechsterTag.before(gueltigVon))
				{
					return getFehlertextZeitraumOhneZeitmodell(naechsterTag, gueltigVon);
				}
				
				lastAustritt = gueltigBis;
			} while (moveNext());
			
			
			// Ende PZE prüfen, wenn ein Enddatum für das letzte Zeitmodell angegeben wurde
			lastAustritt = Format.getDateVerschoben(lastAustritt, 1);
			if (!isOhneEnde && (endePze == null || lastAustritt.before(endePze)))
			{
				return getFehlertextZeitraumOhneZeitmodell(lastAustritt, endePze);
			}
		}

		return null;
	}


	
	/**
	 * fehlertext, wenn für einen Zeitraum kein Zeitmodell hinterlegt wurde.
	 * 
	 * @param beginn
	 * @param ende
	 * @return
	 */
	private String getFehlertextZeitraumOhneZeitmodell(Date beginn, Date ende) {
		return "Für den Bereich vom " + Format.getString(beginn) + (ende != null ? " bis " + Format.getString(ende) : "") + " ist kein Zeitmodell hinterlegt.";
	}


}
