package bzh.plealog.bioinfo.ui.util;

import javax.swing.Action;

public interface TableSearcherComponentAction extends Action {
	/**
	 * Sets the search text. This method is used to transfer the search text from the UI
	 * to the class implementing this interface.
	 */
	public void setSearchText(String text);
	
	/**
	 * Sets the caller of this method. This method is used to transfer the caller to the class
	 * implementing this interface; the caller is the UI containing the search system.
	 */
	public void setTableSearcherComponentActionGateway(TableSearcherComponentAPI caller);
}
