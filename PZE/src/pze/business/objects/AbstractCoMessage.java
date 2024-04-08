package pze.business.objects;

import java.util.Date;

import framework.business.interfaces.fields.IField;
import pze.business.Format;

/**
 * Abstraktes CacheObject für Info-Meldungen
 * 
 * @author Lisiecki
 */
public abstract class AbstractCoMessage extends AbstractCacheObject {

	

	/**
	 * Kontruktor
	 * 
	 * @param tableName
	 * @throws Exception
	 */
	public AbstractCoMessage(String tableName) throws Exception {
		super(tableName);
//		addField("virt.field.freigabe.isausgewaehlt");
	}


	/**
	 * alle offenen Meldungen laden
	 * 
	 * @param messageGruppeID 
	 * @param meldungsTyp 
	 * @throws Exception
	 */
	public abstract void loadByStatusOffen(int messageGruppeID, String meldungsTyp) throws Exception;
	

	/**
	 * alle quittierten Meldungen laden
	 * 
	 * @param messageGruppeID 
	 * @throws Exception
	 */
	public abstract void loadByStatusQuittiert(int messageGruppeID, String meldungsTyp, Date datumVon, Date datumBis) throws Exception;
	

	/**
	 * Quittierung für die aktuelle Message quittieren
	 * 
	 * @return Freigabe-Eintrag erstellt
	 * @throws Exception
	 */
	public abstract void createQuittierung() throws Exception;


	public abstract String getBeschreibung();
	
	
	protected IField getFieldAuftragID() {
		return getField("field." + getTableName() + ".auftragid");
	}


	public int getAuftragID() {
		if (getFieldAuftragID() == null)
		{
			return 0;
		}
		
		return Format.getIntValue(getFieldAuftragID());
	}


	protected IField getFieldAbrufID() {
		return getField("field." + getTableName() + ".abrufid");
	}


	public int getAbrufID() {
		if (getFieldAbrufID() == null)
		{
			return 0;
		}
		return Format.getIntValue(getFieldAbrufID());
	}


	protected IField getFieldDienstreiseID() {
		return getField("field." + getTableName() + ".dienstreiseid");
	}


	public int getDienstreiseID() {
		if (getFieldDienstreiseID() == null)
		{
			return 0;
		}
		return Format.getIntValue(getFieldDienstreiseID());
	}



}
