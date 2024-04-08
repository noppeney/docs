package pze.business.objects.auswertung;

import framework.business.interfaces.fields.IField;
import pze.business.Format;

/**
 * CacheObject für die Einstellungen der Auswertung der Überstunden
 * 
 * @author Lisiecki
 *
 */
public class CoAuswertungAuszahlung extends CoAuswertung {

	public static final String TABLE_NAME = "tblauswertungauszahlung";



	/**
	 * Kontruktor
	 */
	public CoAuswertungAuszahlung() {
		super(TABLE_NAME);
	}
	

	public IField getFieldStatusIDAuszahlung() {
		return getField("field." + getTableName() + ".statusidauszahlung");
	}


	public int getStatusIDAuszahlung() {
		return Format.getIntValue(getFieldStatusIDAuszahlung().getValue());
	}


	public void setStatusIDAuszahlung(int statusID) {
		getFieldStatusIDAuszahlung().setValue(statusID);
	}

	
	/**
	 * Where-Teil des SQL-Statement für die Einschränkung des Status der Auszahlung erstellen (mit führendem "AND")
	 * 
	 * @throws Exception 
	 */
	public String getWhereStatusAuszahlung() throws Exception {
		int statusAuszahlungID;
		
		statusAuszahlungID = getStatusIDAuszahlung();

		return (statusAuszahlungID > 0 ? " AND StatusIDAuszahlung = " + statusAuszahlungID : null);
	}


}
