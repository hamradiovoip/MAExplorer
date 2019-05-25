/** File: PopupPlugin.java */

package MAEPlugin.popup;


/**
 * This class is used for creating popup plugins.
 *
 * Created on September 5, 2001, 6:21 PM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/03 16:21:37 $ / $Revision: 1.4 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */


public abstract class PopupPlugin extends MAEPlugin.MenuPlugin 
{

  /**
   * PopupPlugin() - Constructor to create new PopupPlugin.
   */
  public PopupPlugin(String label)
  { /* PopupPlugin */
    super(label);
  } /* PopupPlugin */
  
  
  /**
   * PopupPlugin() - Constructor to create new PopupPlugin.
   */
  public PopupPlugin()
  { /* PopupPlugin */
    this("Popup");
  } /* PopupPlugin */
  
  
  /* ---------- ADDITONAL METHODS USER MUST IMPLEMENT  -------------- */
  
  /**
   * updateCurGene() - abstract method end-users must implement to use
   * the API. It is called by the popup Registry for the user to update
   * any dependent data since the current gene has changed.
   * @param mid is the MID (Master Gene ID) that is the new current gene.
   */
  public abstract void updateCurGene(int mid);
  
  
  /**
   * updateFilter() -  abstract method end-users must implement to use
   * the API. It is called by the popup Registry for the user to update
   * any dependent data since the data Filter has changed.
   */
  public abstract void updateFilter();
  
  
  /**
   * updateSlider() -  abstract method end-users must implement to use
   * the API. It is called by the popup Registry for the user to update
   * any dependent data since the threshold sliders have changed.
   * This is invoked from the PopupRegistry.
   */
  public abstract void updateSlider();
  
  
  /**
   * updateLabels() -  abstract method end-users must implement to use
   * the API. It is called by the popup Registry for the user to update
   * any dependent data since some global labels have changed.
   */
  public abstract void updateLabels();
  
} /* end of class PopupPlugin */
