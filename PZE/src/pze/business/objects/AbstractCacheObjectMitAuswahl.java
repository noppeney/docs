package pze.business.objects;

import framework.business.interfaces.data.IBusinessObject;
import framework.business.interfaces.fields.IField;
import pze.business.Format;

/**
 * Abstraktes CacheObject für Table-Objekt bei der Auswahl von Einträgen aus Referenztabellen
 * 
 * @author Lisiecki
 */
public abstract class AbstractCacheObjectMitAuswahl extends AbstractCacheObject {


	
	/**
	 * Kontruktor
	 * 
	 * @throws Exception 
	 */
	public AbstractCacheObjectMitAuswahl(String tableResID) throws Exception {
		super(tableResID);
	}


//	public void add(int auswertungAktivitaetID, int objectID) throws Exception {
//		if (!isEditing())
//		{
//			begin();
//		}
//		
//		add();
//		setPersonID(auswertungAktivitaetID);
//		setRefTableObjectID(objectID);
//	}


	/**
	 * neues Cacheobject initialisieren
	 * 
	 * @throws Exception 
	 */
	protected void init(int auswertungID) throws Exception {
		begin();
		
		
		// keine Einträge vorhanden
		if (!moveFirst())
		{
			return;
		}
		
		// Co durchlaufen und IDs setzen
		do 
		{
			if (getID() == 0)
			{
				setID(nextID());
				setAuswertungID(auswertungID);
				setIstAusgewaehlt(false);

				getCurrentRow().setRowState(IBusinessObject.statusAdded);
			}
		} while (moveNext());
		
		setModified(false);
	}


	private IField getFieldAuswertungID() {
		return getField("field." + getTableName() + ".auswertungid");
	}

	
//	private int getAuswertungID() {
//		return Format.getIntValue(getFieldAuswertungID().getValue());
//	}

	
	private void setAuswertungID(int auswertungID) {
		getFieldAuswertungID().setValue(auswertungID);
	}


	protected abstract IField getFieldRefTableObjectID();

	
	private int getRefTableObjectID() {
		return Format.getIntValue(getFieldRefTableObjectID().getValue());
	}

	
//	private void setRefTableObjectID(int refTableObjectID) {
//		getFieldRefTableObjectID().setValue(refTableObjectID);
//	}


	private IField getFieldIstAusgewaehlt() {
		return getField("field." + getTableName() + ".istausgewaehlt");
	}


	private boolean istAusgewaehlt() {
		return Format.getBooleanValue(getFieldIstAusgewaehlt().getValue());
	}

	
	/**
	 * aktuellen Eintrag auswählen
	 * 
	 * @param istAusgewaehlt Eintrag auswählen
	 */
	private void setIstAusgewaehlt(boolean istAusgewaehlt) {
		getFieldIstAusgewaehlt().setValue(istAusgewaehlt);
	}
	

	/**
	 * alle oder keinen Eintrag markieren, je nachdem wie die aktuelle Auswahl ist
	 */
	public void selectAll() {
		selectAll(!allSelected());
	}


	/**
	 * alle oder keinen Eintrag markieren
	 * 
	 * @param auswaehlen Einträge auswählen
	 */
	public void selectAll(boolean auswaehlen) {
		if (moveFirst())
		{
			do 
			{
				setIstAusgewaehlt(auswaehlen);
			} while (moveNext());
		}
	}
	

	/**
	 * alle Felder ausgewählt
	 * 
	 * @return true/false
	 */
	public boolean allSelected() {
		if (moveFirst())
		{
			do 
			{
				if (!istAusgewaehlt())
				{
					return false;
				}
			} while (moveNext());
		}
		
		return true;
	}

	
	/**
	 * Ausgewählte Werte als Komma-getrennter String
	 * 
	 * @return ausgewählte Werte
	 */
//	public String getSelectedValues() {
//		String values = null;
//		
//		if (moveFirst())
//		{
//			values = "";
//			
//			do 
//			{
//				if (isAusgewaehlt())
//				{
//					values += "'" + getField(1).getDisplayValue() + "', ";
//				}
//			} while (moveNext());
//			
//			if (!values.isEmpty())
//			{
//				values = values.substring(0, values.length()-2);
//			}
//			else
//			{
//				values = "''";
//			}
//		}
//		
//		return values;
//	}

	
	/**
	 * Ausgewählte IDs als Komma-getrennter String
	 * 
	 * @return ausgewählte IDs oder null
	 * @throws Exception 
	 */
	public String getSelectedIDs() throws Exception {
		String values = null;
		
		if (moveFirst() && getFieldIstAusgewaehlt() != null)
		{
			values = "";
			
			do 
			{
				if (istAusgewaehlt())
				{
					values += getRefTableObjectID() + ", ";
				}
			} while (moveNext());
			
			values = listetoString(values);
		}
		
		return values;
	}

	
//	/**
//	 * alle IDs als Komma-getrennter String
//	 * 
//	 * @return 
//	 * @throws Exception 
//	 */
//	@Override
//	public String getIDs() throws Exception {
//		String values = null;
//		
//		if (moveFirst())
//		{
//			values = "";
//			
//			do 
//			{
//				values += getRefTableObjectID() + ", ";
//			} while (moveNext());
//			
//			values = listetoString(values);
//		}
//		
//		return values;
//	}


}
