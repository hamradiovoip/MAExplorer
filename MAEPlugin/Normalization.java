/** File: Normalization.java */

package MAEPlugin;


/*
 * This interface is used with Normalizationplugins.
 *
 * Created on December 27, 2001, 5:00 PM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P> 
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/03 16:21:27 $  $Revision: 1.6 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public interface Normalization
 {    

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
   public float[] calcIntensityScaleExtrema(float maxRI, float minRI,
                                            float maxRaw, float minRaw,
                                            int sampleNbr);   
   
  
  
  /**
   * resetPipeline() - reset filter at start of data Normalization.
   * We will test for ALL GENES (if required) using the midOK[] boolean
   * created here to later determine if we actually perform the test.
   * This sets up the state that may be used during the pipeline operation.
   * @param optArg - optional argument
   * @return true if succeed
   */
   public boolean resetPipeline(int optArg);
  
  
  /**
   * finishPipeline() - finish filter at end of normalization of all genes (if required).
   * This may be used to clean up the state after the pipeline operation.
   * @param optArg - optional argument
   * @return true if succeed
   */
   public boolean finishPipeline(int optArg);
   
  
   /**
    * scaleIntensityData() - scale raw intensity data as fct of normalization
    * mode. This method is called on a per-spot basis.
    * @param rawData is unnormalized data of spot
    * @return normalized spot data
    */
   public float scaleIntensityData(float rawData);
   
   
   /**
    * scaleIntensityData() - scale raw intensity data as fct of normalization mode.
    * This method passes the (gid,sampleNbr) in case it is needed.
    * This method is called on a per-spot basis.
    * @param rawData is unnormalized data of spot
    * @param mid is MID of the spot
    * @param gid is GID of the spot
    * @param sampleNbr for the sample array
    * @return normalized spot data
    */
   public float scaleIntensityData(float rawData, int mid, int gid, int sampleNbr);
   
   
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
   public float scaleIntensityData(float rawIntensity, float rawBkgrd,
                                   int mid, int gid, int sampleIdx);
  
   /**
    * getState() - get the state of the checkbox
    */
   public boolean getState();
   
   
   /**
    * setState() - set the state of the checkbox
    */
   public void setState(boolean flag);
   
   
   /**
    * recalcNormalizationExtrema() - set the extreama for all samples for this plugin
    */
   public void recalcNormalizationExtrema();
   
      
   /**
    * setSample() - set the sample object
    */
   public void setSample(Object o);
   
   
   /**
    * getSample() - get the sample object
    */
   public Object getSample();
   
      
} /* end of class Normalization */



