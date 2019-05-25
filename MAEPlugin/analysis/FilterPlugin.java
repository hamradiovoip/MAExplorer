/** File: FilterPlugin.java */ 

package MAEPlugin.analysis;

import java.util.*;
import MAEPlugin.*;

/**
 * This class is the filter MAEPlugin base class and is be used to 
 * implement a filter base class:
 * <P>
 * Users must implement pluginInit() and geneOperation().
 * Users MAY overide resetPipeline() and finishPipeline().
 *<P>
 * Created on September 5, 2001, 6:13 PM, Jai Evans
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/04 15:26:43 $ / $Revision: 1.10 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public abstract class FilterPlugin extends AnalysisPlugin
{
  /** pointer to the active FilterPlugin object. Reference to this
   * object allows data filter methods to find the active
   * data filter.
   */
  protected static Vector
    activeFilterPluginList = null;
  
  /* static block */
  static
  {
    activeFilterPluginList= new Vector();
  }
  
  
  /**
   * FilterPlugin() - Constructor for new FilterPlugin called when
   * load the plugin. This specifies the plugin name.
   */
  public FilterPlugin()
  { /* FilterPlugin */
    super("Filter Plugin");
  } /* FilterPlugin */
  
  
  /**
   * FilterPlugin() - Constructor for new FilterPlugin
   * @param label is the overide name of the plugin
   */
  public FilterPlugin(String label)
  { /* FilterPlugin */
    super(label);
  } /* FilterPlugin */
  
  
  /**
   * pluginMain() is the method activated when end-users check a checkbox API.
   * This is unlike other plugins in that analysis will be done passively.
   * @see #setActiveFilter
   * @see #removeActiveFilter
   */
  public final void pluginMain()
  { /* MenuActivated */
    /* if this plugin is checked, then point data filter
     * pointer to us.
     */
    if( this.getState() )
    { /* Notify built-in code that this filter plugin is active */
      setActiveFilter(this);
    }
    else
      removeActiveFilter(this);
    
    getMAEStub().refreshDataFilter();  /* do what need to do to refresh the filter
                                        * both when present and when removed */
  } /* MenuActivated */
  
  
  /**
   * getFilterState() - Static method for using getActiveFilter().
   * It is used in MaeJavaAPI
   * @return state of data filter plugin. True if active.
   */
  public final static boolean getFilterState()
  { return(!activeFilterPluginList.isEmpty()); }
  
  
  /**
   * getActiveFilterPluginList() - Bean-style method for getting activeFilterPluginList.
   * @return FilterPlugin instance
   */
  public static final synchronized Vector getActiveFilterPluginList()
  { return(activeFilterPluginList); }
  
  
  /**
   * removeActiveFilter() - Bean-style method for removing from activeFilterPluginList.
   */
  public final synchronized void removeActiveFilter(FilterPlugin o)
  { /* removeActiveFilter */
    if (activeFilterPluginList != null)
    {
      activeFilterPluginList.remove(o);
    }
  } /* removeActiveFilter */
  
  
  /**
   * setActiveFilter() - Bean-style method for setting the activeFilterPluginList.
   */
  public final synchronized void setActiveFilter(FilterPlugin o)
  { /* setActiveFilter */
    if (activeFilterPluginList != null)
    {
      activeFilterPluginList.add(o);
    }
  } /* setActiveFilter */
  
  
  /**
   * setOperation() - used in Filter intersection chain to process additional MAEPlugin filter tests.
   * It is passed the MID list and then calls geneOperation() for each mid
   * and then returns the mids that did pass the test in the same midList[].
   *<PRE>
   * NOTE:
   * (1)Override this method only if you need to compare different elements
   *    of the genelist simultaneously.<BR>
   * (2) Otherwise implement geneOperation() @see #geneOperation
   *</PRE>
   * @param midList Master Gene ID list
   * @return number of genes that did pass the test. Genes passing
   * the first are in the first part of the midList[]; -1 means no
   * genes past filter. If the state of the filter is off, then just
   * return the original gene list.
   * @see #geneOperation
   */
  public int setOperation(int midList[], int length)
  { /* setOperation */
    int j= -1;
    boolean isActiveFlag= this.getState();
    if(!isActiveFlag)
      return(length);   /* No-op */
    
    for (int i=0; i< length ; i++)
    { /* process each MID in midList by calling the user's geneOperation on it */
      if (geneOperation( i ))
      {
        j++;
        midList[j]= midList[i];
      }
    }
    return(j);
  } /* setOperation */
  
  
  /**
   * resetPipeline() - reset filter at start of test of all genes (if required).
   * This sets up the state that may be used during the pipeline operation.
   * @param optArg - optional argument
   * @return true if succeed
   */
  public boolean resetPipeline(int optArg)
  { /* resetPipeline */
    return(true);
  } /* resetPipeline */
  
  
  /**
   * finishPipeline() - finish filter at end of test of all genes (if required).
   * This cleans up the state that may be after the pipeline operation.
   * @param optArg - optional argument
   * @return true if succeed
   */
  public boolean finishPipeline(int optArg) 
  { /* finishPipeline */
    return(true);
  } /* finishPipeline */
    
  
  /**
   * geneOperation() - implemented by plugin writer.
   * NOTE: use MJAsmapleList, MJAGene and MJAGeneList to access data
   */
  protected abstract boolean geneOperation(int mid);
  
  
  /**
   * pluginInit() - Plugin init method.
   * Code to initialize the plugin data structures etc. at load time
   * goes here.
   */
  public abstract void pluginInit();
  
  
  /**
   * pluginHalt() - stop the plugin
   */
  public void pluginHalt()
  { /* pluginHalt */
    removeActiveFilter(this);
  } /* pluginHalt */
  
  
} /* end of class FilterPlugin */

