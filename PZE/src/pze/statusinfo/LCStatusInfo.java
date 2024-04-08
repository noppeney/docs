/* updates
 * erst. 30/05/11 vb
 */
package pze.statusinfo;

import java.util.Date;

import framework.business.interfaces.statusinfo.IStatusInfo;

/**
 * für LC derartig abgewandelte Klasse, dass LCStatusInfo-Instanz automatisch Instanz an LCStatusPad weiterreicht
 * (= für LC derart angepasste StatusInfo-Klasse, dass auf Dialog auch AbbrechenButton mitgezeichnet wird)
 * @author veronika
 *
 */
public class LCStatusInfo
{	
	/*
	 * static Members
	 */
	private static LCStatusInfo m_instance = null;
	private static IStatusInfo info = null;
	
	/*
	 * geschützte Konstruktion
	 */
	private LCStatusInfo(boolean mitAbbruch) 
	{
		LCStatusInfo.m_instance = this;
		LCStatusInfo.info = new LCStatusPad(mitAbbruch);
	}
	
	/*
	 * geschützte Konstruktion
	 */
	private LCStatusInfo() 
	{
		LCStatusInfo.m_instance = this;
		LCStatusInfo.info = new LCStatusPad(true);
	}

	/**
	 * fügt eine IStatusInfo implementierende Instanz hinzu
	 * @param info eine IStatusInfo implementierende Instanz
	 */
	public static void setStatusInfo(IStatusInfo info)
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		LCStatusInfo.info = info;
	}
	
	/**
	 * Gibt die zugewiesene Instanz zurück
	 * @return aktuell zugewiesene IStatusInfo Instanz
	 */
	public static IStatusInfo getInstance() {
		return LCStatusInfo.info;
	}
	
	/**
	 * StatusInfo öffnen
	 * @param count
	 */
	public static void openStatusOhneAbbruchButton(int count)
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo(false);

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " StatusInfo.openStatus: " + count);
		else
			LCStatusInfo.info.openStatus(count);
	}
	
	/**
	 * StatusInfo öffnen
	 * @param count
	 */
	public static void openStatusMitAbbruchButton(int count)
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " StatusInfo.openStatus: " + count);
		else
			LCStatusInfo.info.openStatus(count);
	}

	/**
	 * StatusInfo schliessen
	 */
	public static void closeStatus()
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();
		
		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " LCStatusInfo.closeStatus");
		else
			LCStatusInfo.info.closeStatus();
	}
	
	/**
	 * Pane-Text setzen
	 */
	public static void setPaneText(int pane, String message)
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " " + message);
		else
			LCStatusInfo.info.setPaneText(pane, message);
	}

	/**
	 * begin Task
	 */
	public static void beginTask(String name, int count)
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " " + name);
		else
			LCStatusInfo.info.beginTask(name, count);
	}
	
	/**
	 * Maximalwert setzen
	 */
	public static void setMax(int count)
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " " + count);
		else
			LCStatusInfo.info.setMax(count);
	}
	
	/**
	 * gibt den aktuellen Level zurück.
	 * @return Level
	 *//*
	public static int getLevel()
	{
		if(LCStatusInfo.instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " LCStatusInfo.getLevel");
		else
			return ((StatusInfo) LCStatusInfo.info).get.getLevel();

		return 0;
	}*/
	
	/**
	 * set Task name
	 */
	public static void setTaskName(String name)
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " " + name);
		else
			LCStatusInfo.info.setTaskName(name);
	}
	
	/**
	 * set value
	 */
	public static void setValue(int value)
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " " + value);
		else
			LCStatusInfo.info.setValue(value);
	}
	
	/**
	 * advance pgb
	 */
	public static void tick()
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info != null)
			LCStatusInfo.info.tick();
	}
	
	/**
	 * done
	 */
	public static void done()
	{
		if(LCStatusInfo.m_instance == null)
			new LCStatusInfo();

		if(LCStatusInfo.info == null)
			System.out.println(LCStatusInfo.getLogDate() + " LCStatusInfo.done");
		else
			LCStatusInfo.info.done();
	}
	
	/**
	 * Gibt das aktuelle Datum ALS String zurück 
	 * @return date.toString()
	 */
	public static String getLogDate()
	{
		Date date = new Date();
		return date.toString();
	}

}
