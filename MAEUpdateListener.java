/** File: MAEUpdateListener.java */
 
/**
 * The MAEUpdateListener class interface is used with MAEPlugins to specify
 * the update interface for the PopupRegistry event handlers. These are called
 * by MAEPlugins that have registered themselves with the popup registry
 * when they were created and the have specified the particular events including:
 *<PRE>
 * <LI> updateCurGene() - the current gene has changed
 * <LI> updateFilter() - the data filter has changed
 * <LI> updateSlider() - a threshold state slider changed
 * <LI> updateLabel() - the labels (including HP samples, class names etc) have changed
 * <LI> close() - go close the MAEPlugin
 *</PRE>
 *
 * [JE Created on December 19, 2001, 4:00 PM, Version 1.0]
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author Jai Evans (DECA/CIT), P. Lemkin (NCI), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public interface MAEUpdateListener //extends java.util.EventListener
{
  
  
  /**
   * updateCurGene() - update (plot,report,string) popups w/current
   * gene. The popup objects will do whatever else is needed and also
   * handle side-effects.
   *
   */
  public void updateCurGene(int mid);
  
  
  /**
   * updateFilter() - update popups using Filter workingCL changes
   * for (plot,report,string,EPOverlayPlot) popups. The popup objects
   * will do whatever else is needed.
   */
  public void updateFilter();
  
  
  /**
   * updateSlider() - update popups using Slider changes
   * for (plot,report,string) popups. The popup objects  will do
   * whatever else is needed.
   * [TODO] should this be deprecated? - only case where may need
   * it is in Cluster threhold and count scrollers...
   */
  public void updateSlider();
  
  
  /**
   * updateLabels() - update popups using Label changes
   * for (plot,report,string) popups. The popup objects  will do
   * whatever else is needed.
   * [TODO] should this be deprecated? - only case where may need
   * it is in Cluster threhold and count scrollers...
   */
  public void updateLabels();
  
  
  /**
   * close() - close the plugin
   * @param preserveDataStructuresFlag to save data structures
   */
  public void close(boolean preserveDataStructuresFlag);
  
} /* end of class MAEUpdateListener */

