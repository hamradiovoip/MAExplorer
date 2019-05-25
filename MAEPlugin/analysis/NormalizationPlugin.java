/** File: NormalizationPlugin.java */

package MAEPlugin.analysis;

import MAEPlugin.*;
import java.util.*;

/**
 * This class extends the analysis MAEPlugin base class to implement the Normalization base class.
 *
 * The "active normalization" is a static reference that all Normalization
 * classes share.  Whichever one is active will be the one used.
 *
 * Created on September 7, 2001, 5:46 PM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/04 15:26:44 $ / $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public abstract class NormalizationPlugin extends AnalysisPlugin 
                                          implements Normalization
{
     
  /** Pointer to the current active Normalization object. Reference to this
   * object allows normalization methods to find the active normalization.
   * This is static since only one normalization method may be active at a time.
   * If the value is null, then there is NO active NormalizationPlugin
   * even if several are loaded.
   */
  protected static Normalization
    activeNormalization= null;
  
  /* gives index access to sample for access to variables */
  protected Object
    sample;
  
  
  /**
   * NormalizationPlugin() - Default Constructor puts default menu name in menu.
   */
  public NormalizationPlugin()
  {
    super("Normalization");
  }
  
  
  /**
   * NormalizationPlugin() - Default Constructor puts menu name in menu.
   * @param menuLabel is the name of the plugin for use as the menu item
   */
  public NormalizationPlugin(String menuLabel)
  {
    super(menuLabel);
  }
  
  
  /**
   * NormalizationPlugin() - Default Constructor puts menu name in menu and file name.
   * @param menuLabel is the name of the plugin for use as the menu item
   * @param pluginFileName name of the plugin without the ".jar"
   */
  public NormalizationPlugin(String menuLabel, String pluginFileName)
  {
    super(menuLabel, pluginFileName);
  }
  
  
  /**
   * pluginMain() is the method activated when end-users check a checkbox API.
   * This is unlike other plugins in that analysis will be done passively.
   */
  public final void pluginMain()
  { /* MenuActivated */
    /* if this plugin is checked, then point normalization
     * pointer to us.
     */
    if (this.getState())
    {
      setActiveNormalization(this);
      //getMAEStub().setNormalizationStateFromPlugin(this.getPluginName());
      
      /* do normalization and refresh */
      getMAEStub().recalcNorms(this.getPluginName(), this.getState());
    }
  } /* MenuActivated */
  
  
  /**
   * disableNormalizationPlugin() - clear activeNormalization state.
   * If the state is null, then there are no active normalization plugins
   * even if one or more is loaded.
   */
  public final static void disableNormalizationPlugin()
  { /* disableNormalizationPlugin */
   activeNormalization= null;
  } /* disableNormalizationPlugin */
  
  
  /**
   * getNormalizationState() - Static method for using testing getActiveNormalization().
   * It is used in MaeJavaAPI.
   * If there is no active NormalizationPlugin instance, then this is false.
   * @return state of normalization plugin. True if active.
   */
  public final static boolean getNormalizationState()
  {  return(activeNormalization != null);  }
  
  
  /**
   * getActiveNormalization() - Bean-style method for getting the activeNormalization.
   * If there is no active NormalizationPlugin instance, then this is null.
   * @return Normalization instance
   */
  public static final synchronized Normalization getActiveNormalization()
  {  return(activeNormalization); }
  
  
  /**
   * setActiveNormalization() - Bean-style method for setting the activeNormalization.
   * This method calls underlying methods in MAExplorer to treat the checkbox
   * as a radio button and make sure that it disables other Normalization
   * methods whether built-in or other normalization plugins.
   * @param obj is Normalization  plugin object
   */
  public final synchronized void setActiveNormalization(Normalization obj)
  { /* setActiveNormalization */
    /* turn off whichever normalization is on now */
    if (activeNormalization != null)
    {
      activeNormalization.setState(false);
    }
    
   /* Change to the parameter and then turn on null value indicates that 
    * we are dumping the Normalization plugin that is current.
    */
    activeNormalization= obj;
    if (activeNormalization != null)
    { /* set this normalization method as active */
      activeNormalization.setState(true);
      
      /* Do a quick double check and set the NormStateFromPLugin */
      if ( obj instanceof NormalizationPlugin)
      { /* notify built-in radio-button code for this normalization */
        /* This is required so that there is only one normalization method. 
         * It then figures out which plugin is active by using 
         * getActiveNormalization() and calling this Normalize method
         * as required.
         */
        String pluginName= ((NormalizationPlugin) obj).getPluginName();
        getMAEStub().setNormalizationStateFromPlugin(pluginName);
      }
      else
        getMAEStub().setNormalizationStateFromPlugin(null);
    } /* set this normalization method as active */
    else
    { /* disable this normalization method */
      getMAEStub().setNormalizationStateFromPlugin(null);
    }
    
  } /* setActiveNormalization */
  
  
  /**
   * pluginHalt() - stop the plugin
   */
  public void pluginHalt()
  { /* pluginHalt */
    if (this == getActiveNormalization())
    { /* disable this normalization in built-in radio button code */
      setActiveNormalization(null);
      getMAEStub().setNormalizationStateFromPlugin(null);
    }
  } /* pluginHalt */
  
  
  /**
   * getSample() - used to Get the primary sample, to get the variables
   * @return sample as Object
   */
  public final Object getSample()
  {  return(this.sample);  }
  
  
  /**
   * setSample() - method used to set primary variables from the sample.
   * @param obj is the NormalizationPlugin sample object
   */
  public final void setSample(Object obj)
  { /* setSample */
    this.sample= obj;   
  } /* setSample */
  
  
  /**
   * resetPipeline() - reset filter at start of data Normalization.
   * We will test for ALL GENES (if required) using the midOK[] boolean
   * created here to later determine if we actually perform the test.
   * This sets up the state that may be used during the pipeline operation.
   * @param optArg - optional argument
   * @return true if succeed
   */
   public abstract boolean resetPipeline(int optArg);
  
  
  /**
   * finishPipeline() - finish filter at end of normalization of all genes (if required).
   * This may be used to clean up the state after the pipeline operation.
   * @param optArg - optional argument
   * @return true if succeed
   */
   public abstract boolean finishPipeline(int optArg);
     
   
  /**
   * calcIntensityScaleExtrema() - compute scaled Intensity upper
   * and lower limits of raw data for each sample. It is called
   * to get extrema for use in scaling displays and other computations
   * where the limits must be known.
   * @param maxRI maximum raw ratio intensity
   * @param minRI minimum raw ratio intensity
   * @param maxRaw maximum raw intensity max(rawF1,rawF2)
   * @param minRaw minimum raw intensity min(rawF1,rawF2)
   * @param sampleNbr for the sample sample number in the range [1:maxSamples]
   * @return an array with the following scaled data:
   *<PRE>
   *   element 0 = maxDataS
   *   element 1 = minDataS
   *   element 2 = maxRawS
   *   element 3 = minRawS
   *</PRE>
   */
  public abstract float[] calcIntensityScaleExtrema(float maxRI, float minRI,
                                                    float maxRaw, float minRaw,
                                                    int sampleNbr);
  
  
  /**
   * scaleIntensityData() - scale raw intensity data as fct of normalization
   * mode. This is called on a per-spot basis.
   * @param rawData is unnormalized data for spot
   * @return scaled normalized spot data
   */
  public abstract float scaleIntensityData(float rawData);
  
  
  /**
   * scaleIntensityData() - scale raw intensity data as fct of normalization mode.
   * There is a (gid,sampleNbr) dependence on the normalization.
   * This is called on a per-spot basis.
   * @param rawData is unnormalized data for spot
   * @param mid is MID of the spot
   * @param gid is GID of the spot
   * @param sampleNbr for the sample array
   * @return normalized spot data
   */
   public abstract float scaleIntensityData(float rawData, int mid, int gid,
                                            int sampleNbr);
   
  
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
   *        if the use Background flag is set.
   * @param mid is the Master Gene ID to test if not -1 and the gene exists
   * @param gid is the spot Geometry Gene Index to test if not -1 and the spot exists
   * @param sampleIdx is the HybridSample index number for single sample HP-X or HP-Y.
   *        For Cy3 vs Cy5 mode, it is 0 for Cy3 (F1), and 1 for Cy5 (F2).
   *        For HP-X 'set' vs HP-Y 'set' mode, 
   *          it is 0 for HP-X 'set', and 1 for HP-Y 'set'
   * @return scaled data if normalization is active, else 0.0F
   */
  public abstract float scaleIntensityData(float rawIntensity, float rawBkgrd,
                                           int mid, int gid, int sampleIdx);
  
  /**
   * recalcNormalizationExtrema() - set the extreama for all samples for this plugin
   */
  public abstract void recalcNormalizationExtrema();
  
  
  /* ---------- ADDITONAL METHODS USER MUST IMPLEMENT  -------------- */
  
  /**
   * updateCurGene() - abstract method end-users must implement to use
   * the API. It is called by the popup Registry for the user to update
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
   * updateSlider() -  abstract method end-users must implement to use the API.
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
  
} /* end of class NormalizationPlugin  */
