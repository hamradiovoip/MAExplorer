/** File: MJAnormalization.java */
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJAnormalization methods and data structures. 
 *
 * Access normalization data and methods.
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * getNormMethod() - name of current normalization method
 * getNormMethodDisplayName() - display name of current normalization method
 * recalcNorms() - recalculate normalizations of all samples  
 * computeWorkingGeneList() - run data Filter and recompute working gene list
 * madeChanges() - set the madeChangesFlag since state changed. 
 * setPosQuantModeState() - Positive Quantified Intensity state 
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
 
public class MJAnormalization extends MJAbase
{
  
  /**
   * MJAnormalization() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer
   * Plugins.
   */
  MJAnormalization()
  { /* MJAnormalization */
  } /* MJAnormalization */
  
  
  /**
   * getNormMethod() - get name of current normalization method
   * @return value else <code>null</code> if not found.
   */
  public final String getNormMethod()
  { return(mae.normName); }
  
  
  /**
   * getNormMethodDisplayName() - get display name of current normalization
   * method
   * @return value else <code>null</code> if not found.
   */
  public final String getNormMethodDisplayName()
  { return(mae.normNameDisp); }
  
  
  /**
   * recalcNorms() - recalculate normalizations of all samples
   * @param msg  title string
   * @param flag flag passed to recalcNorms (????)...
   */
  public final void recalcNorms(String msg, boolean flag)
  { /* recalcNorms */
    /* [MAEPlugin] forced implementation via MAEStub
     * alias to: cdb.recalcNorms(String, boolean);
     * used by: NormalizationPlugin
     */
    cdb.recalcNorms(msg, flag);
  }  /* recalcNorms */
  
  
  /**
   * computeWorkingGeneList() - run the data Filter and compute the
   * working gene list.
   * [MAEPlugin] forced implementation via MAEStub.
   */
  public final void computeWorkingGeneList()
  { /* computeWorkingGeneList */
    /* [MAEPlugin] forced implementation via MAEStub
     * alias to: fc.computeWorkingGeneList()
     * used by: FilterPlugin
     */
    fc.computeWorkingGeneList();
  }  /* computeWorkingGeneList */
  
  
  /**
   * madeChanges() - set the madeChangesFlag since state changed.
   * [MAEPlugin] forced implementation via MAEStub.
   */
  public final void madeChanges()
  { /* madeChanges */
    /* [MAEPlugin] forced implementation via MAEStub
     * alias to: mae.madeChangesFlag= boolean
     * used by: FilterPlugin
     */
    mae.madeChangesFlag= true;
  } /* madeChanges */
  
  
  /**
   * setPosQuantModeState() - set Positive Quantified Intensity state
   * [MAEPlugin] forced implementation via MAEStub.
   */
  public final void setPosQuantModeState(boolean flag)
  { /* setPosQuantModeState */
    /* [MAEPlugin] forced implementation via MAEStub
     * alias to: em.setPosQuantModeState(boolean)
     * used by: FilterPlugin
     */
    // [CHECK] em.setPosQuantModeState(flag);
  } /* setPosQuantModeState */
  
  
} /* end of class MJAnormalization */


