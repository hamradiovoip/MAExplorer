/** File: ConnectionPlugin.java */

package MAEPlugin.connection;

/**
 * This class is the connection MAEPlugin base class and is be used to implement a connection base class.
 *
 * Created on September 5, 2001, 6:16 PM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/02/21 16:30:15 $ / $Revision: 1.4 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

abstract public class ConnectionPlugin extends MAEPlugin.MenuPlugin
{
  
  /**
   * ConnectionPlugin() - Constructor for creating new ConnectionPlugin
   */
  public ConnectionPlugin()
  { /* ConnectionPlugin */
  } /* ConnectionPlugin */
  
  
  /* ---------- ADDITONAL METHODS USER MUST IMPLEMENT  -------------- */
  
  /**
   * updateCurGene() - abstract method end-users must implement to use the API.
   * It is called by the popup Registry for the user to update
   * any dependent data since the current gene has changed.
   * @param mid is the MID (Master Gene ID) that is the new current gene.
   */
  public abstract void updateCurGene(int mid);
  
  
  /**
   * updateFilter() -  abstract method end-users must implement to use the API.
   * It is called by the popup Registry for the user to update
   * any dependent data since the data Filter has changed.
   */
  public abstract void updateFilter();
  
  
  /**
   * updateSlider() -  abstract method end-users must implement to use  the API.
   * It is called by the popup Registry for the user to update
   * any dependent data since the threshold sliders have changed.
   * This is invoked from the PopupRegistry.
   */
  public abstract void updateSlider();
  
  
  /**
   * updateLabels() -  abstract method end-users must implement to use the API.
   * It is called by the popup Registry for the user to update
   * any dependent data since some global labels have changed.
   */
  public abstract void updateLabels();
  
  
  /**
   * close() - close the plugin
   * @param preserveDataStructuresFlag to save data structures
   */
  public abstract void close(boolean preserveDataStructuresFlag);
  
} /* end of ConnectionPlugin */
