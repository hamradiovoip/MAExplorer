/** File: MAEStub.java */


package MAEPlugin;
import java.util.*;

/**
 * This interface Enforces MAE compliance with Plugin requirements. 
 * It provides hooks into scattered parts of MAE.
 *
 * Created on September 6, 2001, 8:28 AM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/04 15:20:30 $ / $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see MaeJavaAPI
 * @see MAEPlugin
 */


public interface MAEStub 
 {
     
   /* NOTE: These methods are implemented in MaeJavaAPI */
   
   /* fields */
   
   /** menu where plugins should hook themselves.
    * alias to: mbar.pluginsMenu
    * used by: MAEPlugins and children
    */   
   public java.awt.Menu
     menuHook= null;
   
   
   /**
    *  refreshDataFilter() - refresh the datsa filter
    */
   public void refreshDataFilter();  
   
   
   /* [0.0] ****** UTILITY methods *******************/
   
   /**
    * getFrame() - provides Frame for Dialogs and other AWT components
    * that need frames.
    */
   public java.awt.Frame getFrame();
   
   
   /* [1.0] ****** GENERAL UI methods ****************/   
   
   /**
    * showMsg() - alias to: Util.showMsg(String);
    * used by: (all). Draws on top text line of main window.
    * @param str message to draw in top text line of main window.
    */
   public void showMsg(String str);
   
   
   /* [2.2] ****** EDIT pull down menu ************* */
   /* empty */
   
   /* [2.1] ****** NORMALIZATION pull down menu *******/
   
   /**
    * recalcNorms() - alias to: mae.cdb.recalcNorms(String, boolean);
    * used by: NormalizationPlugin
    * @param msg name of plugin message
    * @param flag -
    */
   public void recalcNorms(String msg, boolean flag);
   
    
  /**
   * disableNormalizationPlugin() - clear activeNormalization state.
   * If the state is null, then there are no active normalization plugins
   * even if one or more is loaded.
   */
  public void disableNormalizationPlugin();
  
  
   /**
    * getNormalizationState() - alias to: EventMenu.getNormalizationState
    * used by: NormalizationPlugin
    */
   public boolean getNormalizationState();
   
   
   /**
    * setNormalizationState() -
    * alias to: EventMenu.setNormalizationState
    * used by: NormalizationPlugin
    */
   public void setNormalizationState(boolean flag);
   
   
   /**
    * setNormalizationStateFromPlugin() - used by: NormalizationPlugin
    */
   void setNormalizationStateFromPlugin(String methodNmae);
   
   
   /**
    * loadPluginsStartup() - method to load plugins from file via startup
    */
   public void loadPluginsStartup();
   
   
   /**
    * insertPluginMenu() - method to insert plugin into menu
    */
   public void insertPluginMenu(java.awt.MenuItem mi);
   
   
   /**
    * remove plugin menu
    */
   public void removePluginMenu(java.awt.MenuItem mi);
   
   
   /**
    * setMenuHook() - method to set access root menu
    */
   public void setMenuHook(java.awt.Menu menu);
   
   
   /**
    * getMenuHook() - method to retrieve the root menu
    */
   public java.awt.Menu getMenuHook();
   
   
   /**
    * registerPopupWindow() - changes the label on the mennItem
    * @param mja is instance of MaejavaAPI
    * @param popupName is the name for the PopupRegistry
    * @param popupPropBits are the property bits for the PopupRegistry
    * @param obj is the popup instance to be registered.
    */
   // public void registerPopupWindow(String popupName, int popupPropBits, Object obj);
   
   
   /* Normalization support follows */
   
   /** Bean-style method for setting the activeNormalization MAEStub */
   //public void setActiveNormalization(Normalization o);
   
   
   /** Bean-style method for getting the activeNormalization MAEStub*/
   //public Normalization getActiveNormalization();
   
   /* Data Methods*/
   
   
   /**
    * getSampleTotHistStats() - get Hashtable list of a sample's total histogram
    * statistics. This data is currently computed on non-data Filtered
    * (i.e. all genes) list of data.
    *
    * <PRE>
    * The Hashtable list returned is defined as:
    * <B>name          - Value</B>
    * "minHistData"     - float total array: min hist data value
    * "maxHistData"     - float total array: max hist data value
    * "mnHPri"          - float total array: rawIntens mean, all genes
    *                     in HP sample without background
    * "sdHPri"          - float total array: rawIntens StdDev, all genes
    *                     in HP sample without background
    * "madHPri"         - float total array: rawIntens meanAbsDev, all
    *                     genes in HP sample without background
    * "mnHPri1"         - float total array: rawIntens f1 mean, all
    *                     genes in HP sample without background
    * "sdHPri1"         - float total array: rawIntens f1 StdDev, all
    *                     genes in HP sample without background
    * "mnHPri2"         - float total array: rawIntens f2 mean, all genes
    *                     in HP sample without background
    * "sdHPri2"         - float total array: rawIntens f2 StdDev, all
    *                     genes in HP sample without background
    * "scaleToMaxIV"    - float total array: maxFV/maxRI scale factor to
    *                     compute the maximum range
    * "maxFV"           - float maximum and fractions of dynamic range
    * "logMeanRI"       - float total array: std dev of log of means
    * "logStdDevRI"     - float total array: std dev of log of means
    * "logMeanAbsDevRI" - float total array: Filter hist: log of mean
    *                     absolute deviation raw intensity
    * "logMaxRI"        - float total array: log of maxRI
    * "logMinRI"        - float total array: log of minRI
    *  . . .
    * </PRE>
    *<P>
    *
    * @param sampleNbr sample number data to access
    * @return Hashtable else <code>null</code> if not found or error.
    */
   public Hashtable getSampleTotHistStats(Object o);
   
   
   /**
    * logZero() - compute log10((x==0.0 ? 0.0 : x) - avoid log(0.0)!
    * This defaults log10(0.0) to log(1.0).
    * @param x argument for log
    * @return log10((x==0.0 ? 0.0 : x) - avoid log(0.0)!
    */
   public double logZero(double x);
   
   
} /* end interface MAEStub */





