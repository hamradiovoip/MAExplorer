/** File: MJApopupRegistry.java */
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJApopupRegistry methods and data structures. 
 *
 * Access the Popup Registry.
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * addUniquePopupWindowToReg() - register popupWindowInstance window with keyName
 * addPopupWindowToReg() - register popupWindowInstance window with keyName
 * removePopupByKey() - remove popupWindowInstance by keyName
 * doesPopupExistByKey() - does popup Window Instance exist by keyName
 * doesUniquePopupExistByKey() - does unique popupWindow exist by keyName
 * invokeUpdateCurGene() - invoke popup updates with current gene and mouseKeyMods
 * invokeUpdateFilter() - invoke registered popups updates for data filter changed   
 * invokeUpdateSlider() - invoke registered popups updates for slider state data changed
 * invokeUnvokeUpdateLabels() - invoke registered popups updates for sample labels, etc data changed
 * chkOtherCurGeneEffects() - check for other current gene effects when change MID
 * updateCurGeneInImageAndReg() - update current gene in PseudoArray image and registry
 * setWaitCursor() - set wait cursor ON/OFF to clock or standard cursor
 *</PRE>
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), J. Evans (CIT), C. Santos (CIT), 
 *         G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *<P>
 */
 
public class MJApopupRegistry extends MJAbase
{
  
  /**
   * MJApopupRegistry() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJApopupRegistry()
  { /* MJApopupRegistry */
  } /* MJApopupRegistry */
  
  
  /**
   * addUniquePopupWindowToReg() - register popupWindowInstance window with keyName
   * @param popupWindowInstance instance of popup window
   * @param keyName key name associated with popupWindowInstance
   * @param propBits property bits associated with popupWindowInstance
   * @return number of objects of this type, -1 if failed.
   */
  public final int addUniquePopupWindowToReg(MAEUpdateListener popupWindowInstance,
                                             String keyName, int propBits )
  {
    if((propBits & PRPROP_UNIQUE)!=0)
      pur.removePopupByKey(keyName);
    return(pur.addPopupWindowToReg(popupWindowInstance,keyName,propBits));
  }
  
  
  /**
   * addPopupWindowToReg() - register instance of MAEPlugin window with keyName
   * @param maePluginInstance instance of MAEPlugin
   * @param keyName key name associated with maePluginInstance
   * @param propBits property bits associated with maePluginInstance
   * @return number of objects of this type, -1 if failed.
   */
  public final int addPopupWindowToReg(MAEUpdateListener maePluginInstance,
                                       String keyName, int propBits )
  {
    if((propBits & PRPROP_UNIQUE)!=0)
      pur.removePopupByKey(keyName);              /* cleanup first */
    return(pur.addPopupWindowToReg(maePluginInstance,keyName,propBits));
  }
  
  
  /**
   * removePopupByKey() - remove instance of MAEPlugin by keyName
   * @param keyName key name associated with registered instance of MAEPlugin
   * @return true if succeed, else <code>false</code> if problem
   */
  public final boolean removePopupByKey(String keyName)
  { /* removePopupByKey */
    boolean flag= pur.removePopupByKey(keyName);
    return(flag);
  } /* removePopupByKey */
  
  
  /**
   * doesPopupExistByKey() - does instance of registered MAEPlugin exist by keyName
   * @param keyName key name associated with popup Window Instance
   * @return true if popup exists.
   */
  public final boolean doesPopupExistByKey(String keyName)
  { return(pur.doesPopupExist(keyName)); }
  
  
  /**
   * doesUniquePopupExistByKey() - does unique instance of registered MAEPlugin exist by keyName
   * @param keyName key name associated with popup Window Instance
   * @return true if unique popup exists.
   */
  public final boolean doesUniquePopupExistByKey(String keyName)
  { return(pur.doesUniquePopupExist(keyName)); }
  
  
  /**
   * invokeUpdateCurGene() - invoke updates for registered popups and plugins for current gene.
   * The effect of the updates depending on mouseKeyMods specified.
   *<PRE>
   * <B>mouseKeyMods         Effect</B>
   *  none                   set current gene
   *  InputEvent.CTRL_MASK   set current gene and add it to Edited Gene List
   *  InputEvent.SHIFT_MASK  set current gene and remove it from Edited Gene List
   *</PRE>
   * @param maepluginInstance (opt) instance of plugin to avoid calling if not
   *     null (specify 'this' to avoid calling oneself)
   * @param mid current gene if required
   * @param mouseKeyMods mouse key modifier bits associated with mouse press
   * @return true if succeed, false if failed for any reason.
   */
  public final boolean invokeUpdateCurGene(Object maepluginInstance,
                                           int mid, int mouseKeyMods )
  { return(pur.updateCurGene(mid, mouseKeyMods, maepluginInstance)); }
  
  
  /**
   * invokeUpdateFilter() - invoke registered plugins updates for data filter changed
   * @return true if succeed, else <code>false</code> if any error
   */
  public final boolean invokeUpdateFilter()
  { return(pur.updateFilter(fc.workingCL)); }
  
  
  /**
   * invokeUpdateSlider() - invoke registered plugins updates for slider state data changed
   * @return true if succeed, else <code>false</code> if any error
   */
  public final boolean invokeUpdateSlider()
  { return(pur.updateSlider()); }
  
  
  /**
   * invokeUnvokeUpdateLabels() - invoke registered plugins updates for sample labels, etc data changed
   * @return true if succeed, else <code>false</code> if any error
   */
  public final boolean invokeUpdateLabels()
  { return(pur.updateLabels()); }
  
  
  /**
   * chkOtherCurGeneEffects() - check for other current gene effects when change MID
   *<PRE>
   * <B>mouseKeyMods         Effect</B>
   *  none                   set current gene
   *  InputEvent.CTRL_MASK   set current gene and add it to Edited Gene List
   *  InputEvent.SHIFT_MASK  set current gene and remove it from Edited Gene List
   *</PRE>
   * @param mid current gene if required
   * @param mouseKeyMods  bits associated with mouse press
   * @return true if succeed, else <code>false</code> if any error
   */
  public final boolean chkOtherCurGeneEffects(int mid, int mouseKeyMods)
  { return(pur.chkOtherCurGeneEffects(mid, mouseKeyMods)); }
  
  
  /**
   * updateCurGeneInImageAndReg() - update current gene in PseudoArray image and registry
   * If the current gene is defined (i.e. not -1), then check and update the
   * following if they are enabled:
   *<PRE>
   *    1. modifying the 'editable gene list',
   *    2. poping up a web browser on external GenBank data (if enabled),
   *    3. change the gene used in the 'single' expression profile plot,
   *    4. update cluster algorithms and redraw overlays in pseudoarray image.
   *</PRE>
   * @param mid current gene if required (-1 if not)
   * @return true if succeed, else <code>false</code> if any error
   */
  public final boolean updateCurGeneInImageAndReg(int mid)
  { return(pur.updateCurGeneInImageAndReg((""+mid))); }
  
  
  /**
   * setWaitCursor() - set wait cursor ON/OFF to clock or standard cursor
   * @param status turn clock (ON) or standard cursor (OFF)
   * @return <code>false</code> if method was not found or method failed,
   *   otherwise the flag returned by the method.
   */
  public final boolean setWaitCursor(boolean status)
  { /* setWaitCursor */
    pur.setWaitCursor(status);
    return(status);
  } /* setWaitCursor */
  
  
  
} /* end of class MJApopupRegistry */

