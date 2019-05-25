/** File: GenNormalizationPlugin.java */

import java.math.*;
import java.util.*;

/**
 * This class implements a <B>wrapper</B> for a general purpose
 * class GenericNormalizationPlugin MAExplorer plugin.
 * This lets it exist all of the time in MAExplorer and is activated with a
 * developer wants to debug a new normalization plugin as a installed class
 * prior to deploying and testing it as a PluginLoader .jar file.
 * <P>
 * See mae.DBUG_GENERIC_NORM_PLUGIN flag to enable this.
 * <P>
 * <B>[NOTE]</B> if not debugging then comment out all "gnpp." references
 * so it will compile if you remove the actual GenericNormalizationPlugin
 * (i.e., to put it in its own maePlugins/ folder. 
 * These are flagged with a "[GNPP] so you can grep for them as "//gnpp."
 * and either add or remove the "//".
 *<P>
 * Note: It is designed so that it may be used as either as a MAEPlugin or called,
 * when debugging, from the MAExplorer menu if enabled in the main program.
 * NOTE: The debugging menu item is NOT included in official releases 
 * and is disabled in the MAExplorer.java main program.
 * <P>
 * This example can serve as a model of using various MJA API methods for 
 * writing a range of NormalizationPlugin implementations. It includes
 * more code than is necessary for most normalization plugins. However,
 * after it is adapted to a particular method, the additional code can 
 * be removed to "slim" it down. See (4) below for discussion on the types
 * of data available for writing normalization plugins.
 *<P>
 * <B>(1) Required methods you must implement for NormalizationPlugins'</B>
 * There are several methods that require you to overide them for this
 * extended type of FilterPlugin. 
 *<PRE>
 *  resetPipeline() - is called by the main MAExplorer data Filter for you to
 *                    initialize your data prior to processing each gene
 *                    with geneOperation(mid) calls. This sets the 
 *                    sampleDataType so that the plugin could use it for
 *                    computing advanced normalizations.
 *  finalizePipeline() - is called at the end of the main MAExplorer data
 *                    Filter for you post-process your data structures if
 *                    you need to.
 *  calcIntensityScaleExtrema() - is called to compute Intensity Scale upper
 *                    and lower limits extrema of raw data for each sample
                      when the normalization method is changed.
 *  recalcNormalizationExtrema() - compute the intensity extreama for each 
 *                    sample and save back in the sample.
 *  scaleIntensityData() - is called to scale a data point.
 *</PRE>
 *<P>
 * <B>(2) Data structure "SampleStats" can be used for saving sample specific data</B>
 * You can optionally use the local SampleStats class to hold statistics and other
 * data on a per-sample basis and accessed via ssList[0:maxSamples]. You must
 * popupulate this structure if you need it.
 *<P>
 * <B>(3) Notes on required calls to MJAclasses for extended NormalizationPlugins'</B>
 * We get the state of MAExplorer through various calls to MJAxxxx methods in
 * the initState() method.
 *<P>
 * <B>(4) Types of data that may be accessed in this generic normalization plugin</B>
 * There are two types of data typically used: a) global (i.e. normalized by a median,
 * Zscore, etc.); and b) data dependent in which all relevant data must be retrieved
 * and used to precompute the analysis method prior to doing the actual normalization.
 * This may be done in the resetPipeline() method which is called at the start of
 * a pipeline processing cycle. Note that there may occasionally be other calls
 * to scaleIntensityData() to scale the data (e.g. when you click on the current 
 * spot and it will report the data). It is critical that the resetPipeline() was 
 * previously called prior to using scaleIntensityData().
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-Frederick)
 * @version $Date: 2004/01/13 16:45:02 $ / $Revision: 1.5 $
 */

public class GenNormalizationPlugin extends MAEPlugin.analysis.NormalizationPlugin
       implements MAEUpdateListener
{
        
  /** only report mids analysis in this range if DBUG_FLAG is on */
  public static int
    DBUG_MIN_MIDS= 20,
    DBUG_MAX_MIDS= 20;  /* to get more than 1 spot set to higher number... */
  
  /** Add the following if debugging a new GenericNormalizationPlugin.java plugin.
   * [GNPP] you may comment out if not debugging.
   */
  //public GenericNormalizationPlugin gnpp= null;
  
  /**
   * GenNormalizationPlugin() - constructor ONLY for use with debugging
   * using the mae.DBUG_GENERIC_NORM_PLUGIN flag to add
   *   (Analysis | Normalization | Test Generic Norm Plugin [DBUG]) 
   * to the normalization menu.
   * It is not used otherwise.
   * @param MaeJavaAPI mJA is instance of MaeJavaAPI
   * @param doTestDbugFlag to invoke the dummy testDbug() method.
   */
  public GenNormalizationPlugin(MaeJavaAPI mJA, boolean doTestDbugFlag)
  { /* ExampleXYdataFilterPlugin */
    /* [GNPP] you may comment out if not debugging. */
    //gnpp= new GenericNormalizationPlugin(mJA, doTestDbugFlag); 
    //DBUG_MIN_MIDS= gnpp.DBUG_MIN_MIDS;
    //DBUG_MAX_MIDS= gnpp.DBUG_MAX_MIDS; 
  } /* GenNormalizationPlugin */ 
  
  
  /**
   * resetPipeline() - reset filter at start of data Normalization.
   * We will test for ALL GENES (if required) using the midOK[] boolean
   * created here to later determine if we actually perform the test.
   * This sets up the state that may be used during the pipeline operation.
   * @param optArg - optional argument
   * @return true if succeed
   */
  public boolean resetPipeline(int optArg)
  { /* resetPipeline */
    boolean flag= false;
    
    /* [GNPP] you may comment out if not debugging. */
    //flag= gnpp.resetPipeline(optArg);
    
    return(flag);
  } /* resetPipeline */


  /**
   * finishPipeline() - overide finish filter at end of normalization of all genes (if required).
   * This may be used to clean up the state after the pipeline operation.
   * @param optArg - optional argument
   * @return true if succeed
   */
  public boolean finishPipeline(int optArg)
  { /* finishPipeline */ 
    boolean flag= false;
    
    /* [GNPP] you may comment out if not debugging. */
    //flag= gnpp.finishPipeline(optArg);
    
    return(flag);
  } /* finishPipeline */


  /**
   * scaleIntensityData() - scale raw intensity data as fct of normalization mode.
   * This is called on a per-spot basis from the normalization.
   * @param rawIntensity is unnormalized data for the spot
   * @return scaled normalized spot data
   */
  public float scaleIntensityData(float rawIntensityPoint)
  { /* scaleIntensityData */
    float val= 0.0F;
    
    /* [GNPP] you may comment out if not debugging. */
    //val= gnpp.scaleIntensityData(rawIntensityPoint);
    
    return(val);
  } /* scaleIntensityData */
    
      
 /**
  * scaleIntensityData() - scale raw intensity data as fct of normalization mode.
  * This assumes that either the rawIntensity was background corrected, or
  * we ignore the background by supplying 0.0 for additional corrections.
  * This is called on a per-spot basis.
  * There is a (gid,sampleNbr) dependence on the normalization.
  * @param rawIntensity is unnormalized data for the spot
  * @param mid is MID of the spot
  * @param gid is GID of point
  * @param sampleNbr for the sample
  * @return scaled normalized spot data
  */
  public float scaleIntensityData(float rawIntensity, int mid, int gid, int sampleNbr)
  { /* scaleIntensityData */
    float val= 0.0F;
    
    /* [GNPP] you may comment out if not debugging. */
    //val= gnpp.scaleIntensityData(rawIntensity, mid, gid, sampleNbr);
    
    return(val);
  } /* scaleIntensityData */
	
  
  /**
   * scaleIntensityData() - the gene normalization operating on gene mid
   * for sampleIdx where the data is ether extracted here or from the resetPipeline
   * pre-analysis.
   * If the normalization is inoperative, then just return 0.0.
   * The mid must be >=0 since -1 indicates an illegal gene.
   * The midOK[mid] must be true, else it indicates an illegal gene.
   *
   * [NOTE] The plugin writer should try to avoid doing special analyses here that 
   * need to be computed DURING the processing and instead do the computations 
   * during the resetPipeline() operation if possible. That may be able to 
   * optimized and the data cached to avoid constant recomputation.
   * @param rawIntensity is intensity value to be normalized
   * @param rawBkgrd is background intensity to be used in normalization
   *        if the use Background flag is set. If there is no background,
   *        then supply 0.0F as the arg.
   * @param mid is the Master Gene ID to test if not -1 and the gene exists
   * @param gid is the spot Geometry Gene Index to test if not -1 and the spot exists
   * @param sampleNbr is the HybridSample index number for single sample.
   *        For Cy3 vs Cy5 mode, it is 0 for Cy3 (F1), and 1 for Cy5 (F2).
   *        For HP-X 'set' vs HP-Y 'set' mode, 
   *          it is 0 for HP-X 'set', and 1 for HP-Y 'set'
   * @return scaled data if normalization is active, else 0.0F
   */
  public float scaleIntensityData(float rawIntensity, float rawBkgrd,
                                  int mid, int gid, int sampleNbr)
  { /* scaleIntensityData */ 
    float val= 0.0F;
    
    /* [GNPP] you may comment out if not debugging. */
    //val= gnpp.scaleIntensityData(rawIntensity, rawBkgrd, mid, gid, sampleNbr);
    
    return(val);
  } /* scaleIntensityData */
  
  
  /**
   * calcIntensityScaleExtrema() - compute scaled intensity extrema.
   * This includes the upper and lower limits of raw data for each sample.
   * It is called to get the extrema for use in scaling displays and other 
   * computations where the limits must be known.
   * @param maxRI maximum raw ratio intensity
   * @param minRI minimum raw ratio intensity
   * @param maxRaw maximum raw intensity max(rawF1,rawF2)
   * @param minRaw minimum raw intensity min(rawF1,rawF2)
   * @param sampleNbr for the sample sample number in the range [1:maxSamples]
   * @return an array with the following scaled data:
   *<PRE>
   *   element 0= maxDataS
   *   element 1= minDataS
   *   element 2= maxRawS
   *   element 3= minRawS
   *</PRE>
   */
  public float[] calcIntensityScaleExtrema(float maxRI, float minRI,
                                           float maxRaw, float minRaw,
                                           int sampleNbr)
  { /* calcIntensityScaleExtrema */ 
    float f[]= null;
    
    /* [GNPP] you may comment out if not debugging. */
    //f= gnpp.calcIntensityScaleExtrema(maxRI, minRI, maxRaw, minRaw, sampleNbr);
    
    return(f);
  } /* calcIntensityScaleExtrema */
  
  
  /**
   * recalcNormalizationExtrema() - set the extrema for all samples for this plugin.
   * [NOTE] This method is used if we want to compute all extrema for all samples 
   * simultaneously. Otherwise, since calcIntensityScaleExtrema() is called 
   * repeatedly for all samples, that may be adequate.
   */
  public void recalcNormalizationExtrema()
  { /* recalcNormalizationExtrema */
    /* [GNPP] you may comment out if not debugging. */
    //gnpp.recalcNormalizationExtrema();
  } /* recalcNormalizationExtrema */
  
             
  /**
   * updateCurGene() - update any data since current gene has changed.
   * This is invoked from the PopupRegistry
   * @param mid is the MID (Master Gene ID) that is the new current gene.
   */
  public void updateCurGene(int mid)
  { 
    /* [GNPP] you may comment out if not debugging. */
    //gnpp.updateCurGene(mid);
  }
  
 
  /**
   * updateFilter() - update any dependent data since Filter has changed.
   * This is invoked from the PopupRegistry
   */
  public void updateFilter() 
  { 
    /* [GNPP] you may comment out if not debugging. */
    //gnpp.updateFilter();
  }

 
  /**
   * updateSlider() - update any dependent data sincea threshold slider has changed.
   * This is invoked from the PopupRegistry.
   */
  public void updateSlider() 
  { 
    /* [GNPP] you may comment out if not debugging. */
    //gnpp.updateSlider();
  }

 
  /**
   * updateLabels() - update any dependent data since global labels have changed.
   * This is invoked from the PopupRegistry.
   */
  public void updateLabels()
  {
    /* [GNPP] you may comment out if not debugging. */
    //gnpp.updateLabels();
  }
  
  
  /**
   * close() - close the plugin
   * @param preserveDataStructuresFlag to save data structures
   */
  public void close(boolean preserveDataStructuresFlag)
  {
    /* [GNPP] you may comment out if not debugging. */
    //gnpp.close(preserveDataStructuresFlag);
  }

} /* end of GenNormalizationPlugin */



