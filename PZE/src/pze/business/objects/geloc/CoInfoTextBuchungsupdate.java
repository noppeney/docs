package pze.business.objects.geloc;

import pze.business.Format;
import pze.business.objects.AbstractCacheObject;


/**
 * CacheObject für Infotext Buchungsupdate
 * 
 * @author Lisiecki
 *
 */
public class CoInfoTextBuchungsupdate extends AbstractCacheObject {


	/**
	 * Kontruktor
	 */
	public CoInfoTextBuchungsupdate() {
		super("virt.table.buchungsupdate");
	}
	
	
	public String getInfoText() {
		return Format.getStringValue(getField("virt.field.buchungsupdate.infotext").getValue());
	}
	
	
	public void setInfoText(String infoText) {
		getField("virt.field.buchungsupdate.infotext").setValue(infoText);
	}
	

}
