package pze.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import framework.business.interfaces.fields.IField;

public class Format {

	private static boolean SCIENTIEFIC_ROUNDING = false;
	
	private final static String MONTAG = "Montag";
	private final static String DIENSTAG = "Dienstag";
	private final static String MITTWOCH = "Mittwoch";
	private final static String DONNERSTAG = "Donnerstag";
	private final static String FREITAG = "Freitag";
	private final static String SAMSTAG = "Samstag";
	private final static String SONNTAG = "Sonntag";

	private final static String MONTAG_ABKUERZUNG = "Mo";
	private final static String DIENSTAG_ABKUERZUNG = "Di";
	private final static String MITTWOCH_ABKUERZUNG = "Mi";
	private final static String DONNERSTAG_ABKUERZUNG = "Do";
	private final static String FREITAG_ABKUERZUNG = "Fr";
	private final static String SAMSTAG_ABKUERZUNG = "Sa";
	private final static String SONNTAG_ABKUERZUNG = "So";

	private final static String JANUAR = "Januar";
	private final static String FEBRUAR = "Februar";
	private final static String MAERZ = "März";
	private final static String APRIL = "April";
	private final static String MAI = "Mai";
	private final static String JUNI = "Juni";
	private final static String JULI = "Juli";
	private final static String AUGUST = "August";
	private final static String SEPTEMBER = "September";
	private final static String OKTOBER = "Oktober";
	private final static String NOVEMBER = "November";
	private final static String DEZEMBER = "Dezember";

	private final static String JANUAR_ABKUERZUNG = "Jan";
	private final static String FEBRUAR_ABKUERZUNG = "Feb";
	private final static String MAERZ_ABKUERZUNG = "März";
	private final static String APRIL_ABKUERZUNG = "Apr";
	private final static String MAI_ABKUERZUNG = "Mai";
	private final static String JUNI_ABKUERZUNG = "Juni";
	private final static String JULI_ABKUERZUNG = "Juli";
	private final static String AUGUST_ABKUERZUNG = "Aug";
	private final static String SEPTEMBER_ABKUERZUNG = "Sep";
	private final static String OKTOBER_ABKUERZUNG = "Okt";
	private final static String NOVEMBER_ABKUERZUNG = "Nov";
	private final static String DEZEMBER_ABKUERZUNG = "Dez";

	public static int AAA=0;
	public static int BBB=0;
	public static int CCC=0;


	/**
	 * Gibt eine Integer-Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static int getRunden0Nks(double value) {
		return (int) Math.round(value);
	}


	/**
	 * Gibt eine auf die gewünschte Anzahl Nachkommastellen gerundete Zahl zurück
	 * 
	 * @param value
	 * @param anzNks
	 * @return
	 */
	private static double getRunden(double value, int anzNks) {
		BigDecimal bd;

		try 
		{
			// doppeltes Runden, um Ungenauigkeiten durch Darstellung (z. B. 1.499999999999999) zu verhindern
			bd = BigDecimal.valueOf(value);
			value = bd.setScale(anzNks + 3, RoundingMode.HALF_UP).doubleValue();

			bd = BigDecimal.valueOf(value);
			value = bd.setScale(anzNks, RoundingMode.HALF_UP).doubleValue();

			return value;
		} 
		catch (NumberFormatException e) {
			return 0;
		}
	}


	/**
	 * Gibt eine auf 1 Nachkommastelle gerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getRunden1Nks(double value) {
		return getRunden(value, 1);
	}


	/**
	 * Gibt eine auf 1 Nachkommastelle abgerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getAbrunden1Nks(double value) {
		// 0.0005 subtrahieren, um abzurunden
		value -= 0.05;

		return getRunden1Nks(value);
	}


	/**
	 * Gibt eine auf 2 Nachkommastellen gerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getRunden2Nks(double value) {
		return getRunden(value, 2);
	}


	/**
	 * Gibt eine auf 3 Nachkommastellen gerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getRunden3Nks(double value) {
		return getRunden(value, 3);
	}


	/**
	 * Gibt eine auf 3 Nachkommastellen aufgerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getAufrunden3Nks(double value) {
		return getRunden3Nks(getAufrunden(value, 1/1000.));
	}


	/**
	 * Gibt eine auf 4 Nachkommastellen gerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getRunden4Nks(double value) {
		// doppeltes Runden, da z.B. 1.4999999999999999 (ungenau) sonst abgerundet wird
		value = Math.round(value * 1000000000)/1000000000.;
		value = Math.round(value * 10000)/10000.;
		return value;
	}


	/**
	 * Gibt eine im E-Format auf die gewünschte Anzahl Nachkommastellen gerundete Zahl zurück
	 * 
	 * @param value
	 * @param anzNks
	 * @return
	 */
	private static double getRundenE(double value, int anzNks) {

		try
		{
			// doppeltes Runden, um Ungenauigkeiten durch Darstellung (z. B. 1.499999999999999) zu verhindern
			BigDecimal bd = BigDecimal.valueOf(value);
			BigDecimal bc = new BigDecimal(bd.unscaledValue(),bd.precision()-1);
			value = ((bc.setScale(8, RoundingMode.HALF_UP)).scaleByPowerOfTen(bc.scale()-bd.scale())).doubleValue();

			bd = BigDecimal.valueOf(value);
			bc = new BigDecimal(bd.unscaledValue(),bd.precision()-1);		
			return ((bc.setScale(anzNks, RoundingMode.HALF_UP)).scaleByPowerOfTen(bc.scale()-bd.scale())).doubleValue();
		} 
		catch (NumberFormatException e) {
			return 0;
		}
	}


	/**
	 * Gibt eine im E-Format auf 1 Nachkommastellen gerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getRundenE1Nks(double value) {
		return getRundenE(value, 1);
	}


	/**
	 * Gibt eine im E-Format auf 1 Nachkommastellen aufgerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getAufrundenE1Nks(double value) {
		return getRundenE1Nks(getAufrunden(value, getZehnerpotenz(value)/10));
	}


	/**
	 * Gibt eine im E-Format auf 2 Nachkommastellen gerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getRundenE2Nks(double value) {
		++AAA;
		return getRundenE(value, 2);
	}


	/**
	 * Gibt je nach aktuellem Rundungsmodus eine im E-Format auf 2 Nachkommastellen gerundete Zahl zurÃ¼ck
	 * oder die Zahl selbst.
	 * 
	 * @param value
	 * @return
	 */
	public static double getRundenE2NksOption(double value) {
		if (SCIENTIEFIC_ROUNDING)
		{
			++BBB;
			return getRundenE2Nks(value);
		}
		else
		{
			++CCC;
			return value;			
		}
	}


	/**
	 * Aktiviert das Runden fÃ¼r die Funktion getRundenE2NksOption
	 * 
	 * @param enabled Runden aktivieren
	 */
	public static void enabledScientificRounding(boolean enabled){
		SCIENTIEFIC_ROUNDING = enabled;
	}


	/**
	 * Ist das Runden fÃ¼r die Funktion getRundenE2NksOption aktiviert?
	 * 
	 * @return Runden aktiviert
	 */
	public static boolean isScientificRoundingEnabled(){
		return SCIENTIEFIC_ROUNDING;
	}


	/**
	 * Gibt eine im E-Format auf 3 Nachkommastellen gerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getRundenE3Nks(double value) {
		return getRundenE(value, 3);
	}


	/**
	 * Gibt eine im E-Format auf 2 Nachkommastellen aufgerundete Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	public static double getAufrundenE2Nks(double value) {
		return getRundenE2Nks(getAufrunden(value, getZehnerpotenz(value)/100));
	}


	/**
	 * Gibt eine im aufgerundete Zahl zurück. Die Anzahl der NKS wird durch die Potenz bestimmt
	 * 
	 * @param value
	 * @return
	 */
	private static double getAufrunden(double value, double potenz) {
		//			System.out.println(value);
		//			System.out.println(value/potenz);
		//			System.out.println(Math.ceil(value/potenz));
		//			System.out.println(Math.ceil(value/potenz) * potenz);
		if (potenz == 0.)
		{
			return 0;
		}

		return Math.ceil(value/potenz) * potenz;
	}


	/**
	 * Gibt einen String mit der Zahl als Integer zurück
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat0Nks(double value) {
		return String.format("%d", getRunden0Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl als Integer zurück
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat0Nks(long value) {
		return String.format("%d", value);
	}


	/**
	 * Gibt einen String mit der gewünschten Anzahl Stellen und führenden Nullen der Zahl als Integer zurück
	 * 
	 * @param value
	 * @param anzStellen
	 * @return
	 */
	public static String getFormat0Nks(double value, int anzStellen) {
		String format = "%0" + anzStellen + "d";
		return String.format(format, getRunden0Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 1 Nachkommastelle zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat1NksPunkt(double value) {
		return String.format("%.01f", getRunden1Nks(value)).replace(",", ".");
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 1 Nachkommastelle zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat1NksKomma(double value) {
		return String.format("%.01f", getRunden1Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 2 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat2NksPunkt(double value) {
		return String.format("%.02f", getRunden2Nks(value)).replace(",", ".");
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 2 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat2NksKomma(double value) {
		return String.format("%.02f", getRunden2Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 3 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat3NksPunkt(double value) {
		return String.format("%.03f", getRunden3Nks(value)).replace(",", ".");
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 3 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat3NksKomma(double value) {
		return String.format("%.03f", getRunden3Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 4 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat4NksPunkt(double value) {
		return String.format("%.04f", getRunden4Nks(value)).replace(",", ".");
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 3 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat4NksKomma(double value) {
		return String.format("%.04f", getRunden4Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 3 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat3Nks_kg(double value) {
		return String.format("%.03f_kg", getRunden3Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl mit genau 3 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormat3Nks_GewProzent(double value) {
		return String.format("%.03f", getRunden3Nks(value)) + "_Gew.-%";
	}


	/**
	 * Gibt einen String mit der Zahl im E-Format mit genau 1 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormatE1NksPunkt(double value) {
		return getFormatE1NksKomma(value).replace(",", ".");
	}


	/**
	 * Gibt einen String mit der Zahl im E-Format mit genau 2 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormatE1NksKomma(double value) {
		return String.format("%.01E", getRundenE1Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl im E-Format mit genau 2 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormatE2NksPunkt(double value) {
		return getFormatE2NksKomma(value).replace(",", ".");
	}


	/**
	 * Gibt einen String mit der Zahl im E-Format mit genau 2 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormatE2NksKomma(double value) {
		return String.format("%.02E", getRundenE2Nks(value));
	}


	/**
	 * Gibt einen String mit der Zahl im E-Format mit genau 3 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormatE3NksPunkt(double value) {
		return getFormatE3NksKomma(value).replace(",", ".");
	}


	/**
	 * Gibt einen String mit der Zahl im E-Format mit genau 3 Nachkommastellen zurück
	 * (bei weniger Nachkommastellen wird mit 0 aufgefüllt) 
	 * 
	 * @param value
	 * @return
	 */
	public static String getFormatE3NksKomma(double value) {
		return String.format("%.03E", getRundenE3Nks(value));
	}


	/**
	 * Gibt den boolean-Wert als String zurück
	 * 
	 * @param value
	 * @return
	 */
	public static String getString(boolean value) {
		return String.valueOf(value);
	}


	/**
	 * Gibt das Datum als String zurück
	 * DD.MM.YYYY
	 * 
	 * @param date
	 * @return
	 */
	public static String getString(Date date) {
		GregorianCalendar gregDatum;
		
		if (date == null)
		{
			return null;
		}
		
		gregDatum= new GregorianCalendar();
		gregDatum.setTime(date);
		
		return getString(gregDatum);
	}


	/**
	 * Gibt das Datum als String zurück
	 * DD.MM.YYYY hh:mm
	 * 
	 * @param date
	 * @return
	 */
	public static String getStringMitUhrzeit(Date date) {
		GregorianCalendar gregDatum;
		
		if (date == null)
		{
			return null;
		}
		
		gregDatum= new GregorianCalendar();
		gregDatum.setTime(date);
		
		return getString(gregDatum) + String.format(" %02d:%02d", gregDatum.get(Calendar.HOUR_OF_DAY), gregDatum.get(Calendar.MINUTE));
	}


	/**
	 * Gibt das Datum als String zurück
	 * DD.MM.YYYY
	 * 
	 * @param gregDatum
	 * @return
	 */
	public static String getString(GregorianCalendar gregDatum) {
		if (gregDatum == null)
			return "nicht eingelesen";

		return String.format("%02d.%02d.%d", gregDatum.get(Calendar.DAY_OF_MONTH), 
				gregDatum.get(Calendar.MONTH)+1, gregDatum.get(Calendar.YEAR));
	}


	/**
	 * Gibt das Datum als String zurück
	 * YYYYMMDD
	 * 
	 * @param value
	 * @return
	 */
	public static String getReverseString(GregorianCalendar value) {
		if (value == null)
			return "nicht eingelesen";

		String datum = getString(value);

		return datum.substring(6) + datum.substring(datum.indexOf(".")+1, datum.lastIndexOf(".")) + datum.substring(0, 2);
	}


	/**
	 * Gibt das Datum als String zurück
	 * YYYY_MM_DD
	 * 
	 * @param value
	 * @return
	 */
	public static String getReverseUnterstrichString(Date datum) {
		GregorianCalendar gregDatum;
		
		gregDatum = Format.getGregorianCalendar(datum);

		return gregDatum.get(Calendar.YEAR) + "_" + (gregDatum.get(Calendar.MONTH)+1) + "_" + gregDatum.get(Calendar.DAY_OF_MONTH);
	}


	/**
	 * Gibt das Datum als String zurück
	 * DD.MM.YY
	 * 
	 * @param value
	 * @return
	 */
	public static String getString2Jahreszahl(GregorianCalendar value) {
		if (value == null)
			return "nicht eingelesen";

		return String.format("%02d.%02d.%d", value.get(Calendar.DAY_OF_MONTH), 
				value.get(Calendar.MONTH)+1, value.get(Calendar.YEAR)%100);
	}


	/**
	 * Gibt das Datum als String zum einfügen in die DB zurück
	 * YYYY-MM-DDT00:00:00
	 * 
	 * @param value
	 * @return
	 */
	public static String getStringForDB(GregorianCalendar value) {
		if (value == null)
			return "nicht eingelesen";

		return String.format("%d-%02d-%02dT00:00:00", value.get(Calendar.YEAR), 
				value.get(Calendar.MONTH)+1, value.get(Calendar.DAY_OF_MONTH));
	}


	/**
	 * Gibt das Datum als String zum einfügen in die DB zurück
	 * YYYY-MM-DDT00:00:00
	 * 
	 * @param value
	 * @return
	 */
	public static String getStringForDBmitUhrzeit(GregorianCalendar value) {
		if (value == null)
			return "nicht eingelesen";

		return String.format("%d-%02d-%02dT%02d:%02d:%02d", value.get(Calendar.YEAR), 
				value.get(Calendar.MONTH)+1, value.get(Calendar.DAY_OF_MONTH), 
				value.get(Calendar.HOUR) + (value.get(Calendar.AM_PM) == Calendar.AM ? 0 : 12), value.get(Calendar.MINUTE), value.get(Calendar.SECOND));
	}


	/**
	 * Gibt das Datum als String zum einfügen in die DB zurück
	 * YYYY-MM-DDT00:00:00
	 * 
	 * @param value
	 * @return
	 */
	public static String getStringForDB(Date value) {
		GregorianCalendar gregdatum;
		
		gregdatum = new GregorianCalendar();
		gregdatum.setTime(value);

		return getStringForDB(gregdatum);
	}


	/**
	 * Gibt das Datum als String zum einfügen in die DB zurück
	 * YYYY-MM-DDT00:00:00
	 * 
	 * @param value
	 * @return Datum oder null
	 */
	public static String getStringForDBmitUhrzeit(Date value) {
		GregorianCalendar gregdatum;
		
		gregdatum = new GregorianCalendar();
		gregdatum.setTime(value);

		return getStringForDBmitUhrzeit(gregdatum);
	}


	/**
	 * Gibt das Datum als String zum einfügen in die DB zurück
	 * YYYY-MM-DDT00:00:00
	 * 
	 * @param value
	 * @return
	 */
	public static String getStringMitUhrzeit(GregorianCalendar value) {
		if (value == null)
			return "nicht eingelesen";

		return String.format("%02d.%02d.%d %02d:%02d:%02d", 
				value.get(Calendar.DAY_OF_MONTH), value.get(Calendar.MONTH)+1, value.get(Calendar.YEAR),
				value.get(Calendar.HOUR_OF_DAY), value.get(Calendar.MINUTE), value.get(Calendar.SECOND));
	}


	/**
	 * Gibt das Datum als String zum einfügen in die DB zurück
	 * YYYY-MM-DDT00:00:00
	 * 
	 * @param value
	 * @return
	 */
	public static String getStringForDB_100a(GregorianCalendar value) {
		int year, month, day;

		if (value == null)
			return "nicht eingelesen";

		year = value.get(Calendar.YEAR);
		month = value.get(Calendar.MONTH)+1;
		day = value.get(Calendar.DAY_OF_MONTH);

		if (month == 12 && day == 31)
		{
			year += 1;
			month = 1;
			day = 1;
		}

		return String.format("%d-%02d-%02dT00:00:00", year, month, day);
	}


	/**
	 * Gibt einen xml-konformen String zurück
	 * Ä, Ü... und Sonderzeichen werden ersetzt
	 * 
	 * @param value
	 * @return
	 */
	public static String getConformXml(String value) {

		if (value != null)
		{
			value = value.replaceAll("&", "&amp;");
			value = value.replaceAll("'", "&apos;");
			value = value.replaceAll("<", "&lt;");
			value = value.replaceAll(">", "&gt;");
			value = value.replaceAll("\"", "&quot;");
			value = value.replaceAll("Ä", "&#196;");
			value = value.replaceAll("Ö", "&#214;");
			value = value.replaceAll("Ü", "&#220;");
			value = value.replaceAll("ä", "&#228;");
			value = value.replaceAll("ö", "&#246;");
			value = value.replaceAll("ü", "&#252;");
			value = value.replaceAll("ß", "&#223;");
			
			// &nbsp; soll nicht umformatiert werden -> rückgängig machen
			value = value.replaceAll("&amp;nbsp;", "&nbsp;");
			
			// <br/> soll nicht umformatiert werden -> rückgängig machen
			value = value.replaceAll("&lt;br/&gt;", "<br/>");
			value = value.replaceAll("&lt;br /&gt;", "<br/>");
			
			// <b> soll nicht umformatiert werden -> rückgängig machen
			value = value.replaceAll("&lt;b&gt;", "<b>");
			value = value.replaceAll("&lt;/b&gt;", "</b>");
			
			// <i> soll nicht umformatiert werden -> rückgängig machen
			value = value.replaceAll("&lt;i&gt;", "<i>");
			value = value.replaceAll("&lt;/i&gt;", "</i>");
		}

		return value;
	}

	
	/**
	 * GregorianCalendar des übergebenen Datums auf 0 Uhr normiert 
	 * 
	 * @param datum Datum oder null für heute
	 * @return
	 */
	public static GregorianCalendar getGregorianCalendar0Uhr(Date datum){
		GregorianCalendar gregDatum;
		
		// übergebenes Datum oder heute
		gregDatum = getGregorianCalendar(datum);
		
		// Uhrzeit auf 0 Uhr normieren
		gregDatum.set(GregorianCalendar.HOUR, 0);
		gregDatum.set(GregorianCalendar.MINUTE, 0);
		gregDatum.set(GregorianCalendar.SECOND, 0);
		gregDatum.set(GregorianCalendar.MILLISECOND, 0);
		gregDatum.set(GregorianCalendar.AM_PM, 0);

		return gregDatum;
	}

	
	/**
	 * GregorianCalendar des übergebenen Datums auf 12 Uhr normiert 
	 * 
	 * @param datum Datum oder null für heute
	 * @return
	 */
	public static GregorianCalendar getGregorianCalendar12Uhr(Date datum){
		GregorianCalendar gregDatum;
		
		gregDatum = getGregorianCalendar0Uhr(datum);
		gregDatum.set(GregorianCalendar.HOUR, 12);

		return gregDatum;
	}

	
	/**
	 * GregorianCalendar der übergebenen Datums mit Uhrzeit
	 * 
	 * @param datum Datum oder null für heute/aktuelle Uhrzeit
	 * @return
	 */
	public static GregorianCalendar getGregorianCalendar(Date datum){
		GregorianCalendar gregDatum;
		
		// übergebenes Datum oder heute
		gregDatum = (GregorianCalendar) GregorianCalendar.getInstance();
		if (datum != null)
		{
			gregDatum.setTime(datum);
		}

		return gregDatum;
	}

	
	public static long getDateDiffInMillis(GregorianCalendar datum1, GregorianCalendar datum2) {
		return datum2.getTimeInMillis() - datum1.getTimeInMillis();
	}


	public static long getDateDiffInSec(GregorianCalendar datum1, GregorianCalendar datum2) {
		return getDateDiffInMillis(datum1, datum2) / 1000;
	}


	/**
	 * Berechnet die Differenz zwischen zwei Daten in Monaten (AKZ)
	 * 
	 * @param datum1
	 * @param datum2
	 * @return
	 */
	public static double getAkzInMon(GregorianCalendar datum1, GregorianCalendar datum2) {
		double akz;
		GregorianCalendar datumTmp;

		// prüfen, ob datum1 vor datum2 liegt, sonst tauschen
		if (datum1.after(datum2))
		{
			datumTmp = datum1;
			datum1 = datum2;
			datum2 = datumTmp;
		}


		// AKZ der ganzen jahre in Monaten
		akz = (datum2.get(Calendar.YEAR) - datum1.get(Calendar.YEAR)) * 12;

		// AKZ der Monate (negativer Wert ist ok, Monate werden dann wieder abgezogen)
		akz += datum2.get(Calendar.MONTH) - datum1.get(Calendar.MONTH);

		// AKZ der Tage (negativer Wert ist ok, Tage werden dann wieder abgezogen)
		akz += (datum2.get(Calendar.DAY_OF_MONTH) - datum1.get(Calendar.DAY_OF_MONTH)) / (365.25 / 12.);


		// abrunden auf eine NKS
		akz = getAbrunden1Nks(akz);

		return akz;
	}


	/**
	 * Berechnet die Differenz zwischen zwei Daten in Monaten (AKZ)
	 * 
	 * @param datum1
	 * @param datum2
	 * @return
	 */
	public static double getAkzInMon(Date datum1, Date datum2) {
		GregorianCalendar gregDatum1, gregDatum2;

		gregDatum1 = new GregorianCalendar();
		gregDatum1.setTime(datum1);

		gregDatum2 = new GregorianCalendar();
		gregDatum2.setTime(datum2);

		return getAkzInMon(gregDatum1, gregDatum2);
	}


	/**
	 * Jahr bestimmen
	 * 
	 * @param datum
	 * @return 
	 */
	public static int getJahr(Date datum) {
		return getGregorianCalendar(datum).get(GregorianCalendar.YEAR);
	}
		
		
	/**
	 * Wochentag bestimmen
	 * 
	 * @param datum
	 * @return deutscher Wochentag ausgeschrieben
	 */
	public static String getWochentag(Date datum) {

		return getWochentag(getGregorianCalendar12Uhr(datum));
	}


	/**
	 * Wochentag bestimmen
	 * 
	 * @param datum
	 * @return deutscher Wochentag ausgeschrieben
	 */
	public static String getWochentag(GregorianCalendar gregDatum) {
		int tagDerWoche;

		tagDerWoche = gregDatum.get(GregorianCalendar.DAY_OF_WEEK);

		switch (tagDerWoche)
		{
		case GregorianCalendar.MONDAY:
			return MONTAG;
		case GregorianCalendar.TUESDAY:
			return DIENSTAG;
		case GregorianCalendar.WEDNESDAY:
			return MITTWOCH;
		case GregorianCalendar.THURSDAY:
			return DONNERSTAG;
		case GregorianCalendar.FRIDAY:
			return FREITAG;
		case GregorianCalendar.SATURDAY:
			return SAMSTAG;
		case GregorianCalendar.SUNDAY:
			return SONNTAG;
		}

		return null;
	}


	/**
	 * Wochentag berechnen
	 * 
	 * @param datum
	 * @return deutscher Wochentag ausgeschrieben
	 */
	public static String getWochentagAbkuerzung(Date datum) {
		return getWochentagAbkuerzung(getGregorianCalendar12Uhr(datum));
	}


	/**
	 * Wochentag berechnen
	 * 
	 * @param gregDatum
	 * @return deutscher Wochentag abgekürzt
	 */
	public static String getWochentagAbkuerzung(GregorianCalendar gregDatum) {
		int tagDerWoche;
		
		tagDerWoche = gregDatum.get(GregorianCalendar.DAY_OF_WEEK);

		switch (tagDerWoche)
		{
		case GregorianCalendar.MONDAY:
			return MONTAG_ABKUERZUNG;
		case GregorianCalendar.TUESDAY:
			return DIENSTAG_ABKUERZUNG;
		case GregorianCalendar.WEDNESDAY:
			return MITTWOCH_ABKUERZUNG;
		case GregorianCalendar.THURSDAY:
			return DONNERSTAG_ABKUERZUNG;
		case GregorianCalendar.FRIDAY:
			return FREITAG_ABKUERZUNG;
		case GregorianCalendar.SATURDAY:
			return SAMSTAG_ABKUERZUNG;
		case GregorianCalendar.SUNDAY:
			return SONNTAG_ABKUERZUNG;
		}

		return null;
	}


	/**
	 * Datum ist ein Tag zwischen Montag und Donnerstag
	 * 
	 * @param datum
	 * @return
	 */
	public static boolean isMoBisDo(Date datum) {
		String wochentag;
		
		wochentag = getWochentag(datum);
		
		return wochentag.equals(MONTAG) || wochentag.equals(DIENSTAG) || wochentag.equals(MITTWOCH) || wochentag.equals(DONNERSTAG);
	}
	

	/**
	 * Datum ist ein Tag zwischen Montag und Freitag
	 * 
	 * @param datum
	 * @return
	 */
	public static boolean isMoBisFr(Date datum) {
		return isMoBisDo(datum) || isFreitag(datum);
	}
	

	/**
	 * Datum ist ein Tag zwischen Montag und Freitag
	 * 
	 * @param datum
	 * @return
	 */
	public static boolean isMoBisFr(GregorianCalendar datum) {
		String wochentag;
		
		wochentag = getWochentag(datum);
		
		return wochentag.equals(MONTAG) || wochentag.equals(DIENSTAG) || wochentag.equals(MITTWOCH) 
				|| wochentag.equals(DONNERSTAG) || wochentag.equals(FREITAG);
	}
	

	/**
	 * Datum ist am Wochenende
	 * 
	 * @param datum
	 * @return
	 */
	public static boolean isWochenende(GregorianCalendar datum) {
		String wochentag;
		
		wochentag = getWochentag(datum);
		
		return wochentag.equals(SAMSTAG) || wochentag.equals(SONNTAG);
	}
	

	/**
	 * Datum ist ein Freitag
	 * 
	 * @param datum
	 * @return
	 */
	public static boolean isFreitag(Date datum) {
		String wochentag;
		
		wochentag = getWochentag(datum);
		
		return wochentag.equals(FREITAG);
	}
	

	/**
	 * Datum ist ein Samstag
	 * 
	 * @param datum
	 * @return
	 */
	public static boolean isSamstag(Date datum) {
		String wochentag;
		
		wochentag = getWochentag(datum);
		
		return wochentag.equals(SAMSTAG);
	}
	

	/**
	 * Datum ist ein Sonntag
	 * 
	 * @param datum
	 * @return
	 */
	public static boolean isSonntag(Date datum) {
		String wochentag;
		
		wochentag = getWochentag(datum);
		
		return wochentag.equals(SONNTAG);
	}
	

	/**
	 * Monat berechnen
	 * 
	 * @param gregDatum
	 * @return deutscher Monat ausgeschrieben
	 */
	public static String getMonat(GregorianCalendar gregDatum) {
		return getMonat(gregDatum.get(GregorianCalendar.MONTH));
	}


	/**
	 * Monat berechnen, ABkürzung zurückgeben
	 * 
	 * @param gregDatum
	 * @return deutscher Monat ausgeschrieben
	 */
	public static String getMonatAbkuerzung(GregorianCalendar gregDatum) {
		return getMonatAbkuerzung(gregDatum.get(GregorianCalendar.MONTH));
	}


	/**
	 * Monat berechnen
	 * 
	 * @param Monat (aus GregDatum, also ab 0)
	 * @return deutscher Monat ausgeschrieben
	 */
	public static String getMonat(int monat) {
		
		switch (monat)
		{
		case GregorianCalendar.JANUARY:
			return JANUAR;
		case GregorianCalendar.FEBRUARY:
			return FEBRUAR;
		case GregorianCalendar.MARCH:
			return MAERZ;
		case GregorianCalendar.APRIL:
			return APRIL;
		case GregorianCalendar.MAY:
			return MAI;
		case GregorianCalendar.JUNE:
			return JUNI;
		case GregorianCalendar.JULY:
			return JULI;
		case GregorianCalendar.AUGUST:
			return AUGUST;
		case GregorianCalendar.SEPTEMBER:
			return SEPTEMBER;
		case GregorianCalendar.OCTOBER:
			return OKTOBER;
		case GregorianCalendar.NOVEMBER:
			return NOVEMBER;
		case GregorianCalendar.DECEMBER:
			return DEZEMBER;
		}

		return null;
	}


	/**
	 * Monat berechnen
	 * 
	 * @param Monat (aus GregDatum, also ab 0)
	 * @return deutscher Monat abgekürzt
	 */
	public static String getMonatAbkuerzung(int monat) {
		
		switch (monat)
		{
		case GregorianCalendar.JANUARY:
			return JANUAR_ABKUERZUNG;
		case GregorianCalendar.FEBRUARY:
			return FEBRUAR_ABKUERZUNG;
		case GregorianCalendar.MARCH:
			return MAERZ_ABKUERZUNG;
		case GregorianCalendar.APRIL:
			return APRIL_ABKUERZUNG;
		case GregorianCalendar.MAY:
			return MAI_ABKUERZUNG;
		case GregorianCalendar.JUNE:
			return JUNI_ABKUERZUNG;
		case GregorianCalendar.JULY:
			return JULI_ABKUERZUNG;
		case GregorianCalendar.AUGUST:
			return AUGUST_ABKUERZUNG;
		case GregorianCalendar.SEPTEMBER:
			return SEPTEMBER_ABKUERZUNG;
		case GregorianCalendar.OCTOBER:
			return OKTOBER_ABKUERZUNG;
		case GregorianCalendar.NOVEMBER:
			return NOVEMBER_ABKUERZUNG;
		case GregorianCalendar.DECEMBER:
			return DEZEMBER_ABKUERZUNG;
		}

		return null;
	}


	/**
	 * Gibt die Zehnerpotenz der Zahl zurück
	 * 
	 * @param value
	 * @return
	 */
	private static double getZehnerpotenz(double value) {
		return Math.pow(10, Math.floor(Math.log10(value)));
	}


	/**
	 * Wandelt das übergebene Object in ein double-Object um
	 * 
	 * @param value
	 * @return
	 */
	public static double getDoubleValue(Object value) {
		if (value == null)
		{
			return 0;
		}
		else if (value.getClass().equals(String.class))
		{
			// Bei Strings muss geprüft werden, ob es sich um eine Zahl handelt
			try 
			{
				return Double.valueOf(((String) value).replace(",", "."));				
			} 
			catch (Exception e)
			{
				return 0;
			}
		}
		else if (value.getClass().equals(Integer.class))
			return (Integer) value;
		else if (value.getClass().equals(Long.class))
			return (Long) value;
		else if (value.getClass().equals(Float.class))
			return (Float) value;
		else if (value.getClass().equals(BigDecimal.class))
			return ((BigDecimal) value).doubleValue();
		else if (value instanceof IField)
			return getDoubleValue(((IField) value).getValue());
		else
			return (Double) value;
	}


	/**
	 * Wandelt das übergebene Object in ein int-Object um
	 * 
	 * @param value
	 * @return
	 */
	public static int getIntValue(Object value) {
		return getRunden0Nks(getDoubleValue(value));
	}


	/**
	 * Wandelt das übergebene Object in ein long-Object um
	 * 
	 * @param value
	 * @return
	 */
	public static long getLongValue(Object value) {
		return Math.round(getDoubleValue(value));
	}


	/**
	 * Wandelt das übergebene Object in ein String-Object um
	 * 
	 * @param value String oder null
	 * @return
	 */
	public static String getStringValue(Object value) {
		if (value == null)
		{
			return null;
		}

		if (value instanceof String)
		{
			return (String) value;
		}

		if (value instanceof Date)
		{
			return getString((Date) value);
		}
		
		if (value instanceof IField)
		{
			return getStringValue(((IField) value).getValue());
		}

		
		return String.valueOf(value);
	}


	/**
	 * Gibt einen String mit einer formatierten Angabe in Minuten im Format mm:ss zurück
	 * 
	 * @param minuten
	 * @return
	 */
	public static String getStringOfTime(int sekunden) {
		return String.format("%d:%02d", sekunden/60, sekunden%60);
	}


	/**
	 * Wandelt das übergebene Object in ein boolean-Object um
	 * 
	 * @param value
	 * @return
	 */
	public static boolean getBooleanValue(Object value) {
		
		if (value == null)
		{
			return false;
		}
		
		if (value instanceof IField)
		{
			return getBooleanValue(((IField) value).getValue());
		}
		
		return (Boolean) value;
	}


	/**
	 * Prüft Boolean-Wert auf TRUE
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isTrueValue(Object value) {
		return value != null && (Boolean) value;
	}


	/**
	 * Prüft Boolean-Wert auf FALSE
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isFalseValue(Object value) {
		return value != null && !((Boolean) value);
	}
	
	
	/**
	 * Dezimalzahl für eine Zahl in Hexadezimalschreibweise
	 * 
	 * @param hexValue
	 * @return Dezimalzahl als long oder 0
	 */
	public static long getValueOfHexValue(String hexValue){
		
		if (hexValue == null)
		{
			return 0;
		}
		
		// führende Nullen entfernen
		hexValue = hexValue.trim();
		while (hexValue.startsWith("0"))
		{
			hexValue = hexValue.substring(1);
		}

		// leere Einträge abfangen
		if (hexValue.isEmpty())
		{
			return 0;
		}

		return Long.parseLong(hexValue, 16);
	}

	
	/**
	 * Hexadezimalwert für eine Dezimalzahl
	 * 
	 * @param hexValue
	 * @return String mit Hexadezimalwert
	 */
	public static String geHexValue(long value){
		return Long.toHexString(value);
	}

	
	/**
	 * Date-Value oder null
	 * 
	 * @param date null, Date, String oder IField
	 * @return
	 */
	public static Date getDateValue(Object date) {

		if (date == null)
		{
			return null;
		}
		else if (date instanceof Date)
		{
			return (Date) date;
		}
		else if (date instanceof String)
		{
			GregorianCalendar gregDatum = getGregorianCalendar((String) date);
			return getDateValue(gregDatum);
		}
		else if (date instanceof IField)
		{
			return getDateValue(((IField) date).getValue());
		}
		else
		{
			return null;
		}
	}


	/**
	 * GregorianCalendar-Value oder null
	 * 
	 * @param String mit Datum
	 * @return
	 */
	private static GregorianCalendar getGregorianCalendar(String datum) {
		int index1, index2, jahr;
		GregorianCalendar gregDatum;

		// Datum als jjjj-mm-tt hh:mm:...
		if (datum.contains("-"))
		{
			gregDatum = new GregorianCalendar();
			index1 = datum.indexOf("-");
			index2 = datum.lastIndexOf("-");
			
			if (index1 != index2)
			{
				gregDatum.set(GregorianCalendar.YEAR, getIntValue(datum.substring(0, index1)));
				gregDatum.set(GregorianCalendar.MONTH, (getIntValue(datum.substring(index1+1, index2))-1));
				gregDatum.set(GregorianCalendar.DAY_OF_MONTH, (getIntValue(datum.substring(index2+1, datum.indexOf(" ")))));
			}
		}
		else // Datum als tt.mm.jj/jjjj
		{
			gregDatum = new GregorianCalendar();
			index1 = datum.indexOf(".");
			index2 = datum.lastIndexOf(".");

			if (index1 != index2)
			{
				gregDatum.set(GregorianCalendar.DAY_OF_MONTH, getIntValue(datum.substring(0, index1)));
				gregDatum.set(GregorianCalendar.MONTH, (getIntValue(datum.substring(index1+1, index2))-1));
				jahr = getIntValue(datum.substring(index2+1));
				if (jahr < 1000)
				{
					jahr += 2000;
				}
				gregDatum.set(GregorianCalendar.YEAR, getIntValue(jahr));
			}
		}

		return gregDatum;
	}

	
	/**
	 * Zeit aus einem String als Datetime-Objekt lesen
	 * 
	 * @param datum
	 * @return
	 */
	public static String getZeitAusDatetime(String datum) {
		int index1, index2;

		// Datum als jjjj-mm-tt hh:mm:...
		index1 = datum.indexOf(" ");
		index2 = datum.lastIndexOf(":");

		try
		{
			return datum.substring(index1+1, index2);
		}
		catch (Exception e) 
		{
		}

		return null;
	}

	
	/**
	 * Macht aus einem Datum mit zweistelliger Jahreszahl ein zulässiges mit vierstelliger Jahreszahl
	 * 
	 * @param datum
	 * @return
	 */
	public static String getDateText(String datum) {
		int punkt, lastPunkt, jahr;
		GregorianCalendar gregDatum;

		gregDatum = new GregorianCalendar();
		punkt = datum.indexOf(".");
		lastPunkt = datum.lastIndexOf(".");
		
		if (punkt != lastPunkt)
		{
			gregDatum.set(GregorianCalendar.DAY_OF_MONTH, getIntValue(datum.substring(0, punkt)));
			gregDatum.set(GregorianCalendar.MONTH, (getIntValue(datum.substring(punkt+1, lastPunkt))-1));
			jahr = getIntValue(datum.substring(lastPunkt+1));
			if (jahr < 1000)
			{
				jahr += 2000;
			}
			gregDatum.set(GregorianCalendar.YEAR, getIntValue(jahr));
		}

		return getString(getDateValue(gregDatum));
	}


	/**
	 * Date-Value oder null
	 * 
	 * @param datum
	 * @return neues Datum oder null
	 */
	public static Date getDate0Uhr(Date datum) {
		Date newDatum;
		
		if (datum != null)
		{
			// muss so gemacht werden, um das Format beizubehalten
			newDatum = (Date) datum.clone();
			newDatum.setTime(getGregorianCalendar0Uhr(datum).getTimeInMillis());
			return new Timestamp(newDatum.getTime());
		}
		else
		{
			return null;
		}
	}

	
	/**
	 * Date-Value oder null
	 * 
	 * @param datum
	 * @return neues Datum oder null
	 */
	public static Date getDate12Uhr(Date datum) {
		Date newDatum;
		
		if (datum != null)
		{
			// muss so gemacht werden, um das Format beizubehalten
			newDatum = (Date) datum.clone();
			newDatum.setTime(getGregorianCalendar12Uhr(datum).getTimeInMillis());
			return new Timestamp(newDatum.getTime());
		}
		else
		{
			return null;
		}
	}

	
	/**
	 * Date-Value oder null
	 * 
	 * @param gregDatum
	 * @return
	 */
	public static Date getDateValue(GregorianCalendar gregDatum) {
		
		if (gregDatum != null)
		{
			return new Timestamp(gregDatum.getTimeInMillis());
		}
		else
		{
			return null;
		}
	}


	/**
	 * Gibt ein neues Datum um die übergebene Anzahl Tage verschoben zurück
	 * 
	 * @param datum neues Date-Object
	 * @param anzTage
	 * @return null (wenn datum = null) oder verschobenes Datum
	 */
	public static Date getDateVerschoben(Date datum, int anzTage) {
		return getDateVerschoben(datum, anzTage, GregorianCalendar.DAY_OF_MONTH);
	}


	/**
	 * Gibt ein neues Datum um die übergebene Anzahl Monate verschoben zurück
	 * 
	 * @param datum neues Date-Object
	 * @param anzWochen
	 * @return null (wenn datum = null) oder verschobenes Datum
	 */
	public static Date getDateVerschobenWochen(Date datum, int anzWochen) {
		return getDateVerschoben(datum, anzWochen, GregorianCalendar.WEEK_OF_MONTH);
	}


	/**
	 * Gibt ein neues Datum um die übergebene Anzahl Monate verschoben zurück
	 * 
	 * @param datum neues Date-Object
	 * @param anzMonate
	 * @return null (wenn datum = null) oder verschobenes Datum
	 */
	public static Date getDateVerschobenMonate(Date datum, int anzMonate) {
		return getDateVerschoben(datum, anzMonate, GregorianCalendar.MONTH);
	}


	/**
	 * Gibt ein neues Datum um die übergebene Anzahl Monate verschoben zurück
	 * 
	 * @param datum neues Date-Object
	 * @param anz
	 * @param gregEinheit Tage, Wochen, Monate... z. B. GregorianCalendar.MONTH
	 * @return null (wenn datum = null) oder verschobenes Datum
	 */
	private static Date getDateVerschoben(Date datum, int anz, int gregEinheit) {
		Date newDatum;
		GregorianCalendar gregdatum;
		
		if (datum == null)
		{
			return null;
		}
		
		gregdatum = Format.getGregorianCalendar(datum);
		gregdatum.add(gregEinheit, anz);
		
		// muss so gemacht werden, um das Format beizubehalten
		newDatum = (Date) datum.clone();
		newDatum.setTime(gregdatum.getTimeInMillis());
		
		return new Timestamp(newDatum.getTime());
	}


	/**
	 * Datum, bei dem der Tag des Monats angepasst wurde
	 * 
	 * @param datum 
	 * @param tagDesMonats
	 * @return
	 */
	public static Date getDatum(Date datum, int tagDesMonats) {
		GregorianCalendar gregDatum;
		
		gregDatum = new GregorianCalendar();
		gregDatum.setTime(datum);
		
		if (tagDesMonats > 0)
		{
			gregDatum.set(Calendar.DAY_OF_MONTH, tagDesMonats);
		}
		
		return new Timestamp(gregDatum.getTimeInMillis());
	}


	/**
	 * Datum für den letzten Tag im aktuellen Monat bestimmen
	 * 
	 * @param datum
	 * @return
	 */
	public static Date getDatumLetzterTagdesMonats(Date datum) {
		return getDatum(datum, getAnzTageDesMonats(datum));
	}


	/**
	 * Parsen eines beliebigen Strings mit ":" oder "./," als Trennzeichen in das Format h:mm
	 * 
	 * @param text
	 * @return
	 */
	public static String getZeitAsText(String text) {
		int zeit;
		
		zeit = getZeitAsInt(text);
		
		return getZeitAsText(zeit);
	}

	
	/**
	 * Parsen der Minutenanzahl in das Format h:mm (Stunden mit 1000er-Trennzeichen)
	 * 
	 * @param text
	 * @return
	 */
	public static String getZeitAsText(int minuten) {
		int stunden, stundenInTausend;
		String vorzeichen;
		
		// negative Werte abfangen
		vorzeichen = "";
		if (minuten < 0)
		{
			vorzeichen = "-";
			minuten = -minuten;
		}
		
		// 1000er Trennzeichen
		stunden = (minuten / 60);
		stundenInTausend = stunden / 1000;
		stunden -= stundenInTausend * 1000;

		return  vorzeichen + (stundenInTausend > 0 ? stundenInTausend + "." + Format.getFormat0Nks(stunden, 3) : stunden) + ":" + Format.getFormat0Nks(minuten % 60, 2);
	}

	
	/**
	 * Parsen eines beliebigen Strings mit ":" oder "./," als Trennzeichen in Anzahl der Minuten als Integer 
	 * 
	 * @param text
	 * @return Zeit in Minuten
	 */
	public static int getZeitAsInt(String text) {
		int zeit;
//		int minuten, stunden;
//		String stringStunden, stringMinuten; 
		String vorzeichen;
		
		if (text == null)
		{
			return 0;
		}
		
		// negative Werte abfangen
		vorzeichen = "";
		if (text.startsWith("-"))
		{
			vorzeichen = "-";
			text = text.substring(1);
		}
		
		// Eingabe im Format hh:mm
		if (text.contains(":"))
		{
			zeit = getMinutenFromUhrzeit(text);
		}
		else // Eingabe im Format hh,hh (0,5 = 30 Minuten)
		{
			// Stundenangaben mit mehr als 2 NKS werden gerundet
			zeit = Format.getIntValue(Format.getRunden2Nks(Format.getDoubleValue(text)) * 60); 
		}
		// Zeit 0800 in 08:30 formatieren funktioniert nicht, weil auch 180 h für Fremdleister bzw Projektbudget 1000 Stunden eingetragen wird
//		else if (text.contains(".") || text.contains(",")) // Eingabe im Format hh,hh (0,5 = 30 Minuten)
//		{
//			// Stundenangaben mit mehr als 1 NKS werden gerundet
//			zeit = Format.getIntValue(Format.getRunden1Nks(Format.getDoubleValue(text)) * 60); 
//		}
//		else if (text.length() > 2) // Eingabe im Format hhhh (ohne :)
//		{
//			text = text.substring(0, text.length()-2) + ":" + text.substring(text.length()-2);
//			
//			zeit = getMinutenFromUhrzeit(text);
//		}
//		else // Eingabe im ganzen Stunden
//		{
//			// Stundenangaben mit mehr als 1 NKS werden gerundet
//			zeit = Format.getIntValue(Format.getRunden1Nks(Format.getDoubleValue(text)) * 60); 
//		}
		
		// negative Werte abfangen
		if (vorzeichen.equals("-"))
		{
			zeit = -zeit;
		}

		return zeit;
	}


	/**
	 * Anzahl Minuten aus Uhrzeit im Format hh:mm
	 * 
	 * @param text
	 * @return
	 */
	private static int getMinutenFromUhrzeit(String text) {
		int minuten;
		int stunden;
		int zeit;
		String stringStunden;
		String stringMinuten;
		stringStunden = text.substring(0, text.indexOf(":")).replace(".", "");
		stringMinuten = text.substring(text.indexOf(":") + 1);
		
		stunden = Format.getIntValue(stringStunden);
		minuten = Format.getIntValue(stringMinuten);
		
		zeit = 60 * stunden + minuten;
		return zeit;
	}


	/**
	 * Anzahl Tage des Monats
	 * 
	 * @param datum
	 * @return
	 */
	public static int getAnzTageDesMonats(Date datum){
		GregorianCalendar gregDatum;
		
		gregDatum = getGregorianCalendar(datum);
		
		return gregDatum.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	

	/**
	 * String einem Set hinzufügen, wenn es nicht null ist
	 * 
	 * @param string
	 * @param set
	 */
	public static void addStringToSet(String string, Set<String> set) {
		
		if (string == null || string.isEmpty())
		{
			return;
		}
		
		set.add(string);
	}
	

	/**
	 * Inhalt des Sets zu einem String mit Komma-sparierten Einträgen machen
	 * 
	 * @param set
	 * @return
	 */
	public static String getStringValue(Set<String> set) {
		String wert;
		
		wert = set.toString();
		wert = wert.replace("[", "");
		wert = wert.replace("]", "");
		
		return wert;
	}
	

	/**
	 * aktuelle Speicherauslastung ausgeben
	 */
	public static void printMemory() {
		Runtime rt;

		rt = Runtime.getRuntime();

		System.out.println("max. " + rt.maxMemory()/1.e6 + "MB     total: " + rt.totalMemory()/1.e6 + "MB     free: " +rt.freeMemory()/1.e6+ "MB");
	}


}
