package pze.business.objects;

import java.util.Date;
import java.util.GregorianCalendar;

import pze.business.Format;

/**
 * CacheObject für Ferien
 * 
 * @author Lisiecki
 *
 */
public class CoFerien extends AbstractCacheObject {

	public static final String TABLE_NAME = "tblferien";


	private static CoFerien m_instance = null;
	


	/**
	 * Kontruktor
	 */
	private CoFerien() {
		super("table." + TABLE_NAME);
	}
	

	/**
	 * Getter Instanz der Klasse
	 * 
	 * @return m_instance
	 */
	public static CoFerien getInstance() throws Exception {
		if (CoFerien.m_instance == null)
		{
			CoFerien.m_instance = new CoFerien();
			CoFerien.m_instance.loadAll();
		}
		
		return CoFerien.m_instance;
	}


	/**
	 * Nach Datum, Bundesland sortieren
	 * @return 
	 */
	protected String getSortFieldName() {
		return "DatumVon";
	}
	

	@Override
	public String getNavigationBitmap() {
		return "flag.blue";
	}


	/**
	 * Datum, auf 12 Uhr normiert
	 * 
	 * @return
	 */
	@Override
	public Date getDatum() {
		return Format.getDate12Uhr(Format.getDateValue(getField("field." + getTableName() + ".datumvon").getValue()));
	}


	/**
	 * Datum, auf 12 Uhr normiert
	 * 
	 * @return
	 */
	public Date getDatumBis() {
		return Format.getDate12Uhr(Format.getDateValue(getField("field." + getTableName() + ".datumbis").getValue()));
	}


	/**
	 * sucht das übergebene Datum in den Ferien
	 * 
	 * @param datum
	 * @return Datum ist Ferientag
	 */
	public boolean isFerientag(GregorianCalendar datum) {
		return isFerientag(Format.getDateValue(datum));
	}


	/**
	 * sucht das übergebene Datum in den Ferien
	 * 
	 * @param datum
	 * @return Datum ist Ferientag
	 */
	public boolean isFerientag(Date datum) {
		Date aktDatum, datumVon, datumBis;
		
		aktDatum = Format.getDate12Uhr(datum);
		
		if (!moveFirst())
		{
			return false;
		}
		
		do 
		{
			datumVon = getDatum();
			datumBis = getDatumBis();
			
			// vor Ferienbeginn, dann ist es kein Ferientag
			if (aktDatum.before(datumVon))
			{
				return false;
			}
			
			// nach den aktuellen Ferien, dann betrachte die nächsten Ferien
			if (aktDatum.after(datumBis))
			{
				continue;
			}
			else // sonst liegt das Datum in den aktuellen Ferien
			{
				return true;
			}
		} while (moveNext());
		
		return false;
	}

}
