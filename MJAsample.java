/** File: MJAsample.java */
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJAsample methods and data structures. 
 *
 * Access get and put single sample top-level data.
 *
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * setSampleNameMethod() - sets sample name method to  name domain to be used.
 * lookupSampleName() - Return sample number by SampleID (free-text) name
 * lookupSampleNameByDBfileName() - Return sample number by sample DB file name
 * lookupSampleNameByDBfileID() - lookup sample number by sample databaseFileID
 * lookupSampleByFreeTextName() - lookup sample number by SampleID (free-text) name.
 *
 * getSampleIDbySampleNbr() - lookup the sample ID name by sample number
 * getProjectNameBySampleNbr() - lookup the sample project name by sample number
 * getFullSampleTextBySampleNbr() - lookup full sample text description by sample number
 * 
 * getRawIntensityQuantDataList() - get raw spot intensity quant data list 
 * getRawBkgrdQuantDataList() - get raw spot background quant data 
 * getQualCheckQuantDataList() - get spot QualCheck good spot data list 
 * getCorrCoefQuantDataList() - get spot intensity correlation coeff. data
 * getXYdataList() - get list of XY coordinates data for sample
 * getQualCheckList() - get list of QualCheck data for sample
 * getSampleScaleFactors() - get list of a sample's scale factors
 * getSampleF1F2Extrema() - get list of sample's F1F2 intensity extrema
 * setSampleF1F2Extrema() - set normalized list of sample's F1F2 intensity extrema
 * setSampleCurScaledExtrema() - set sample's current normalized scaled intensity extrema. 
 * getSampleTypeFlags() - get list of a sample's property flags
 * getSampleTotHistStats() - get list of a sample's total histogram statistics
 * getSampleCalibDNAdata() - get list of a Sample calibration DNA data
 * calcIntensityScaleExtrema() - compute intensity scale extrema for Sample.
 * calcMeanCalibrationDNA() - compute (mean,StdDev) gene for Sample
 * calcMeanUseNormGeneSet() - compute (mean,StdDev) normalization gene set
 * swapCy5Cy3AndReCalcStats() - swap Cy3 and Cy5, recalculate statistics 
 * getSamplesDataVector() - get sample data for sample indexes for gene mid.
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
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.15 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see MJAhistogram for other Sample Histogram methods
 *<P>
 */
 
public class MJAsample extends MJAbase
{
  /** nameMethod defines the type of name to use in looking up a sample.
  * The legal names are:
  * 1 for DBfileName, 2 for DBfileID, 3 for free-text name.
  */
 static int 
   nameMethod= 1;  /* default to DBfileName */
 /** Lookup method to use when finding sample by DB file Name */
 final static int
   NAME_METHOD_DB_FILE_NAME= 1;
 /** Lookup method to use when finding sample by DB file ID. */
 final static int
   NAME_METHOD_DB_FILE_ID= 2;
 /** Lookup method to use when finding sample by free-text Sample_ID. */
 final static int
   NAME_METHOD_DB_SAMPLE_ID= 3;
  
  /**
   * MJAsample() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAsample()
  { /* MJAsample */
  } /* MJAsample */
  
  
    /**
   * setSampleNameMethod() - sets the sample name method to the name domain to be used.
   * @param nameMethodToUse may be NAME_METHOD_DB_FILE_NAME, NAME_METHOD_DB_FILE_ID, 
   *   or NAME_METHOD_DB_SAMPLE_ID
   * @return true if the nameMethod is legal.
   */
  public final static boolean setSampleNameMethod(int nameMethodToUse)
  { /* setSampleNameMethod */
    if(nameMethodToUse==NAME_METHOD_DB_FILE_NAME || 
       nameMethodToUse==NAME_METHOD_DB_FILE_ID || 
       nameMethodToUse==NAME_METHOD_DB_SAMPLE_ID)
     {
       nameMethod= nameMethodToUse;
       return(true);
     }
     else
       return(false);
  } /* setSampleNameMethod */
  
    
  /**
   * lookupSampleName() - lookup sample number by sample databaseFileID. 
   * Sample numbers are in the range of [1:maxSamples].
   * @param databaseFileID sample file name to search for. Note: don't include
   *        the file extension in the name.
   * @param condName name of condition
   * @return sample number or -1 if failed
   */
  public final int lookupSampleName(String databaseFileID, String condName)
  { /* lookupSampleName */
    int idx= -1;
    
    if(nameMethod==NAME_METHOD_DB_FILE_NAME)
      idx=lookupSampleNameByDBfileName(databaseFileID, condName); /* hpName */
    else if(nameMethod==NAME_METHOD_DB_FILE_ID)
      idx= lookupSampleNameByDBfileID(databaseFileID, condName); /* databaseFileID */
    else if(nameMethod==NAME_METHOD_DB_SAMPLE_ID)
      idx= lookupSampleByFreeTextName(databaseFileID, condName);  /* sampleID */
    
    return(idx); /* not found */
  } /* lookupSampleName */ 
  
  
  /**
   * lookupSampleNameByDBfileName() - lookup sample number by sample DB file name. 
   * @param sampleDBfileName sample file name to search for. Note: don't include
   *  the file extension in the name.
   * @param condName name of condition
   * @return sample number or -1 if failed
   */
  public final int lookupSampleNameByDBfileName(String sampleDBfileName, 
                                                String condName)
  { /* lookupSampleNameByDBfileName */
    /* Check the SampleFileName against ms.hpName values. */
    if(sampleDBfileName == null || condName == null)
      return(-1); /* error */
    
    for(int x=1; x<=hps.nHP; x++)
    { /* look for sampleDBfileName that is same as ms.hpName */
      if(hps.msList[x]!=null &&
         hps.msList[x].hpName.equalsIgnoreCase(sampleDBfileName))
      { /* found it */
        return(x);
      }
    } /* look for sampleDBfileName that is same as ms.hpName */   
    
    return(-1); /* not found */
  } /* lookupSampleNameByDBfileName */ 
  
  
  /**
   * lookupSampleNameByDBfileID() - lookup sample number by sample databaseFileID.
   * Sample numbers are in the range of [1:maxSamples]. 
   * @param databaseFileID sample file name to search for. Note: don't include
   *  the file extension in the name.
   * @param condName name of condition
   * @return sample number or -1 if failed
   */
  public final int lookupSampleNameByDBfileID(String databaseFileID, String condName)
  { /* lookupSampleNameByDBfileID */
    /* Check the SampleFileName against ms.hpName values. */
    if(databaseFileID == null || condName == null)
      return(-1); /* error */
    
    for(int x=1; x<=hps.nHP; x++)
    { /* look for databaseFileID that is same as ms.hpName */
      if(hps.msList[x]!=null &&
         hps.msList[x].databaseFileID.equalsIgnoreCase(databaseFileID))
      { /* found it */
        return(x);
      }
    } /* look for sampleDBfileName that is same as ms.hpName */   
    
    return(-1); /* not found */
  } /* lookupSampleNameByDBfileID */ 
 
  
  /**
   * lookupSampleByFreeTextName() - lookup sample number by SampleID (free-text) name. 
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleFreeTextName sample free-text name to search for sample
   * @param condName name of condition
   * @return sample number or -1 if failed
   */
  public final int lookupSampleByFreeTextName(String sampleFreeTextName,
                                              String condName)
  { /* lookupSampleByFreeTextName */      
    /* Check the sampleFreeTextName against ms.hpName values. */
    if(sampleFreeTextName == null || condName == null)
      return(-1); /* error */
    
    for(int x=1; x<=hps.nHP; x++)
    { /* look for sampleFreeTextName that is same as ms.hpName */
      if(hps.msList[x]!=null && 
         hps.msList[x].sampleID.equalsIgnoreCase(sampleFreeTextName))
        return(x); 
    } /* look for sampleFreeTextName that is same as ms.hpName */
    
    return(-1); /* not found */
  } /* lookupSampleByFreeTextName */
  
  
 /**
  * getSampleIDbySampleNbr() - lookup the sample ID name by sample number.
  * Sample numbers are in the range of [1:maxSamples].
  * @param sampleNbr is the number of the sample to lookup
  * @return the result if found, else null
  */
  public final String getSampleIDbySampleNbr(int sampleNbr)
  {
     MaHybridSample ms= chkGetHP(sampleNbr);
     if(ms==null)
       return(null);
     return(ms.sampleID);
  }
  
  
 /**
  * getProjectNameBySampleNbr() - lookup the sample project name by sample number.
  * Sample numbers are in the range of [1:maxSamples].
  * @param sampleNbr is the number of the sample to lookup
  * @return the result if found, else null
  */
  public final String getProjectNameBySampleNbr(int sampleNbr)
  {
     MaHybridSample ms= chkGetHP(sampleNbr);
     if(ms==null)
       return(null);
     return(ms.prjName);
  }
  
  
 /**
  * getFullSampleTextBySampleNbr() - lookup the full sample text description by sample number.
  * Sample numbers are in the range of [1:maxSamples].
  * @param sampleNbr is the number of the sample to lookup
  * @return the result if found, else null
  */
  public final String getFullSampleTextBySampleNbr(int sampleNbr)
  {
     MaHybridSample ms= chkGetHP(sampleNbr);  
     if(ms==null)
       return(null);  
     return(ms.fullStageText);
  }
  
   
  /**
   * getRawIntensityQuantDataList() - get raw spot intensity quant data list
   * for the given sample.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return data array if sampleNbr is found,
   *   otherwise the data array for the current sample is returned.
   */
  public final float[] getRawIntensityQuantDataList(int sampleNbr)
  { /* getRawIntensityQuantDataList */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    float data[]= msW.totQ;
    
    return(data);
  } /* getRawIntensityQuantDataList */
  
  
  /**
   * getRawBkgrdQuantDataList() - get raw spot background quant data
   * for the given sample.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return data array if sampleNbr is found,
   *   otherwise the data array for the current sample is returned.
   */
  public final float[] getRawBkgrdQuantDataList(int sampleNbr)
  { /* getRawBkgrdQuantDataList */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    float data[];
    
    if(msW.bkgrdSpotQ==null)
    {
      data= new float[1];
      data[0]= msW.bkgrdQ;
    }
    else
      data= msW.bkgrdSpotQ;
    
    return(data);
  } /* getRawBkgrdQuantDataList */
  
  
  /**
   * getQualCheckQuantDataList() - get spot QualCheck good spot data list
   * for the given sample.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return data array if sampleNbr is found,
   *   otherwise the data array for the current sample is returned.
   */
  public final float[] getQualCheckQuantDataList(int sampleNbr)
  { /* getQualCheckQuantDataList */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      return(null);
    float data[]= msW.qualChkQ;
    
    return(data);
  } /* getQualCheckQuantDataList */
  
  
  /**
   * getCorrCoefQuantDataList() - get spot intensity correlation coefficient
   * data for the given sample.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return data array if sampleNbr is found,
   *   otherwise the data array for the current sample is returned.
   */
  public final float[] getCorrCoefQuantDataList(int sampleNbr)
  { /* getCorrCoefQuantDataList */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    float data[]= msW.totQ;
    
    return(data);
  } /* getCorrCoefQuantDataList */
  
  
  /**
   * getXYdataList() - get list of XY coordinates data for sample.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return data array if sampleNbr is found,
   *   otherwise the data array for the current sample is returned.
   */
  public final Point[] getXYdataList(int sampleNbr)
  { /* getXYdataList */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    Point data[]= msW.xyCQ;
    
    return(data);
  } /* getXYdataList */
  
  
  /**
   * getQualCheckList() - get list of QualCheck data for sample number.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return data array if sampleNbr is found,
   *   otherwise the data array for the current sample is returned.
   */
  public final float[] getQualCheckList(int sampleNbr)
  { /* getQualCheckList */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    return(msW.qualChkQ);
  } /* getQualCheckList */
  
  
  /**
   * getSampleScaleFactors() - get list of a sample's scale factors.
   * Sample numbers are in the range of [1:maxSamples].
   * These are used in various built-in normalization modes.
   * Call calcIntensityScaleExtrema()to refresh the data.
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name                - Value</B>
   * "ratioMedianScale"     - float may be set if Cy3/Cy5 median scale
   *                          correction
   * "ratioMedianBkgdScale" - float may be set if Cy3/Cy5 median background
   *                          scale correction
   * "stdDevUseGeneSet"     - float StdDev 'User Filter Gene Set' intensity
   * "meanUseGeneSet"       - float mean 'User Filter Gene Set' intensity
   * "scaleUseGeneSet"      - float scale factor for 'User Filter Gene set'
   * "scaleUseGeneSetTo65K" - float scale factor for 'User Filter Gene Set'
   *                          but max for all HPs is 65K
   * "stdDevCalDNA"         - float StdDev 'calibration DNA' intensity
   * "meanCalDNA"           - float mean 'calibration DNA' intensity
   * "scaleCalDNA"          - float scale factor for scaling by 'calibration
   *                          DNA' intensity
   * "scaleCalDNAto65K"     - float scale factor for scaling by 'calibration
   *                          DNA' intensity, but max for all HPs is 65K
   * "normFactor"           - float normalization factor for all intensity
   *                          data so can compare intensity between HPs
   *</PRE>
   *
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return hashtable list else <code>null</code> if not found or error.
   * @see #calcIntensityScaleExtrema
   */
  public final Hashtable getScaleFactors(int sampleNbr)
  { /* ScaleFactors */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    Hashtable ht= new Hashtable();
    ht.put("ratioMedianScale",new Float(msW.ratioMedianScale));
    ht.put("ratioMedianBkgdScale",new Float(msW.ratioMedianBkgdScale));
    ht.put("stdDevUseGeneSet",new Float(msW.stdDevUseGeneSet));
    ht.put("meanUseGeneSet",new Float(msW.meanUseGeneSet));
    ht.put("scaleUseGeneSet",new Float(msW.scaleUseGeneSet));
    ht.put("scaleUseGeneSetTo65K",new Float(msW.scaleUseGeneSetTo65K));
    ht.put("stdDevCalDNA",new Float(msW.stdDevCalDNA));
    ht.put("meanCalDNA",new Float(msW.meanCalDNA));
    ht.put("scaleCalDNA",new Float(msW.scaleCalDNA));
    ht.put("scaleCalDNAto65K",new Float(msW.scaleCalDNAto65K));
    ht.put("normFactor",new Float(msW.normFactor));
    
    return(ht);
  } /* ScaleFactors */
  
  
  /**
   * getSampleF1F2Extrema() - get list of sample's normalized F1F2
   * intensity extrema and 1st order histogram statistics. Also
   * get the raw intensity extrema.
   * Sample numbers are in the range of [1:maxSamples].
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name        - Value</B>
   *     --- normalized intensity data -----
   * "minDataF1"     - float minimum intensity data for F1 channel
   * "minDataF2"     - float minimum intensity data for F2 channel
   * "maxDataF1"     - float maximum intensity data for F1 channel
   * "maxDataF2"     - float maximum intensity data for F2 channel
   * "medianF1"      - float median intensity of F1 channel data
   * "medianF2"      - float median intensity of F2 channel data
   * "medianF1Bkgd"  - float median background intensity of F1 channel data
   * "medianF2Bkgd"  - float median background intensity of F2 channel data
   *     --- Raw intensity data -----
   * "minRI"         - float min raw intensity
   * "maxRI"         - float max raw intensity
   * "minRI1"        - float min raw intensity F1 (Cy3)
   * "maxRI1"        - float max raw intensity F1 (Cy3)
   * "minRI2"        - float min raw intensity F2 (Cy5)
   * "maxRI2"        - float max raw intensity F2 (Cy5)
   * "minRatioRI"    - float min raw intensity ratio F1/F2
   * "maxRatioRI"    - float max raw intensity ratio F1/F2
   * </PRE>
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return hash table else <code>null</code> if not found or error.
   */
  public final Hashtable getSampleF1F2Extrema(int sampleNbr)
  { /* getSampleF1F2Extrema */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    Hashtable ht= new Hashtable();
    ht.put("minDataF1",new Float(msW.minDataF1)); /* normalized */
    ht.put("minDataF2",new Float(msW.minDataF2));
    ht.put("maxDataF1",new Float(msW.maxDataF1));
    ht.put("maxDataF2",new Float(msW.maxDataF2));
    ht.put("medianF1",new Float(msW.medianF1));
    ht.put("medianF2",new Float(msW.medianF2));
    ht.put("medianF1Bkgd",new Float(msW.medianF1Bkgd));
    ht.put("medianF2Bkgd",new Float(msW.medianF2Bkgd));
    
    ht.put("minRI", new Float(msW.minRI));        /* raw intensity */
    ht.put("maxRI", new Float(msW.maxRI));
    ht.put("minRI1", new Float(msW.minRI1));
    ht.put("maxRI1", new Float(msW.maxRI1));
    ht.put("minRI2", new Float(msW.minRI2));
    ht.put("maxRI2", new Float(msW.maxRI2));
    
    ht.put("minRatioRI", new Float(msW.minRatioRI));
    ht.put("maxRatioRI", new Float(msW.maxRatioRI));
    
    return(ht);
  } /* getSampleF1F2Extrema */
  
  
  /**
   * setSampleF1F2Extrema() - set list of sample's normalized F1F2 intensity extrema.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @param minDataF1 is minimum normalized data for F1 channel
   * @param minDataF2 is minimum normalized data for F1 channel
   * @param maxDataF1 is maximum normalized data for F1 channel
   * @param maxDataF2 is maximum normalized data for F2 channel
   */
  public final void setSampleF1F2Extrema(int sampleNbr,
                                         float minDataF1, float minDataF2,
                                         float maxDataF1, float maxDataF2)
  { /* setSampleF1F2Extrema */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    msW.minDataF1= minDataF1;
    msW.minDataF2= minDataF2;
    msW.maxDataF1= maxDataF1;
    msW.maxDataF2= maxDataF2;
  } /* setSampleF1F2Extrema */
  
  
  /**
   * setSampleCurScaledExtrema() - set sample's current normalized scaled intensity extrema.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @param maxDataS is current norm: scaled max data
   * @param minDataS is current norm: scaled min data
   * @param maxRawS is current norm: scaled maxRaw data
   * @param minRawS is current norm: scaled minRaw data
   */
  public final void setSampleCurScaledExtrema(int sampleNbr,
                                              float maxDataS, float minDataS,
                                              float maxRawS, float minRawS)
  { /* setSampleCurScaledExtrema */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    msW.maxDataS= maxDataS;
    msW.minDataS= minDataS;
    msW.maxRawS= maxRawS;
    msW.minRawS= minRawS;
  } /* setSampleCurScaledExtrema
     
     
  /**
   * getSampleTypeFlags() - get list of a sample's property flags by sample number.
   * Sample numbers are in the range of [1:maxSamples].
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name                    - Value</B>
   * "hasBkgrdPerSpotFlag" - boolean flag: each spot has a background
   *                         measurement value
   * "hasQualChkFlag"      - boolean flag: each spot has a QualCheck value
   * "implicitFieldsFlag"  - boolean flag: Fields are defined implicitly in
   *                         the .quant file
   * "flipRowsColsFlag"    - boolean flag: flip rows and columns
   *                         (not normally done)
   * "hasValidDataFlag"    - boolean flag: data has been successfully read
   *                         for this sample
   * "doRatioFlag"         - boolean flag: the two channels are handled as a
   *                         ratio rather and separate intensities
   * "swapCy3Cy5Flag"      - boolean swap (Cy3,Cy5) data for (Cy5,Cy3)
   * </PRE>
   *
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return hash table else <code>null</code> if not found or error.
   */
  public final Hashtable getSampleTypeFlags(int sampleNbr)
  { /* getSampleTypeFlags */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    Hashtable ht= new Hashtable(10);
    ht.put("hasBkgrdPerSpotFlag",new Boolean(msW.hasBkgrdPerSpotFlag));
    ht.put("hasQualChkFlag",new Boolean(msW.hasQualChkFlag));
    ht.put("implicitFieldsFlag",new Boolean(msW.implicitFieldsFlag));
    ht.put("flipRowsColsFlag",new Boolean(msW.flipRowsColsFlag));
    ht.put("hasValidDataFlag",new Boolean(msW.hasValidDataFlag));
    ht.put("doRatioFlag",new Boolean(msW.doRatioFlag));
    ht.put("swapCy3Cy5Flag",new Boolean(mae.snSwapCy5Cy3Data[sampleNbr]));
    
    return(ht);
  } /* getSampleTypeFlags */
  
  
  /**
   * getRawSampleData() - get list of a sample's total histogram statistics.
   * Sample numbers are in the range of [1:maxSamples].
   * This data is currently computed on non-data Filtered (i.e. all genes)
   * list of data.
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name          - Value</B>
   * "scaleToMaxIV"    - float total array: maxFV/maxRI scale factor to
   *                     compute the maximum range
   * "maxFV"           - float maximum and fraction
   * "minRI"           - float min raw intensity
   * "maxRI"           - float max raw intensity
   * "minRI1"          - float min raw intensity F1 (Cy3)
   * "maxRI1"          - float max raw intensity F1 (Cy3)
   * "minRI2"          - float min raw intensity F2 (Cy5)
   * "maxRI2"          - float max raw intensity F2 (Cy5)
   * "minRaw"          - float min raw intensity (@)
   * "maxRaw"          - float max raw intensity (@)
   * "minRatioRI"      - float min Ratio of raw intensity
   * "maxRatioRI"      - float max Ratio of raw intensity
   * "xCmin"           - int min X coord mapped to MAE coord
   * "yCmin"           - int min Y coord mapped to MAE coord
   * "xCmax"           - int max X coord mapped to MAE coord
   * "yCmax"           - int max Y coord mapped to MAE coord
   * </PRE>
   *<P>
   *
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return <code>null</code> if not found or error, else return list.
   */
  public final Hashtable getRawSampleData(int sampleNbr)
  { /* getRawSampleData */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    Hashtable ht= new Hashtable();
    ht.put("scaleToMaxIV", new Float(msW.scaleToMaxIV));
    ht.put("maxFV", new Float(msW.maxFV));
    
    ht.put("minRI", new Float(msW.minRI));
    ht.put("maxRI", new Float(msW.maxRI));
    ht.put("minRI1", new Float(msW.minRI1));
    ht.put("maxRI2", new Float(msW.maxRI2));
    ht.put("minRI2", new Float(msW.minRI2));
    ht.put("maxRI2", new Float(msW.maxRI2));
    ht.put("minRaw", new Float(msW.minRaw));
    ht.put("maxRaw", new Float(msW.maxRaw));
    
    ht.put("maxRatioRI", new Float(msW.maxRatioRI));
    ht.put("minRatioRI", new Float(msW.minRatioRI));
    
    ht.put("xCmax", new Integer(msW.xCmax));
    ht.put("yCmax", new Integer(msW.yCmax));
    ht.put("xCmin", new Integer(msW.xCmin));
    ht.put("yCmin", new Integer(msW.yCmin));
    
    return(ht);
  } /* getRawSampleData */
  
  
  /**
   * getSampleTotHistStats() - get list of a sample's total histogram statistics.
   * Sample numbers are in the range of [1:maxSamples].
   * This data is currently computed on non-data Filtered (i.e. all genes)
   * list of data.
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name          - Value</B>
   * "minHistData"     - float total array: min hist data value
   * "maxHistData"     - float total array: max hist data value
   * "mnHPri"          - float total array: rawIntens mean, all genes in HP
   *                     sample without background
   * "sdHPri"          - float total array: rawIntens StdDev, all genes in
   *                     HP sample without background
   * "madHPri"         - float total array: rawIntens meanAbsDev,
   *                     all genes in HP sample without background
   * "mnHPri1"         - float total array: rawIntens f1 mean, all
   *                     genes in HP sample without background
   * "sdHPri1"         - float total array: rawIntens f1 StdDev, all
   *                     genes in HP sample without background
   * "mnHPri2"         - float total array: rawIntens f2 mean, all
   *                     genes in HP sample without background
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
   * "medianRI"        - float median raw intensity
   * "modeRI"          - float mode raw intensity
   * "minRI"           - float min raw intensity
   * "maxRI"           - float max raw intensity
   * "minRI1"          - float min raw intensity F1 (Cy3)
   * "maxRI1"          - float max raw intensity F1 (Cy3)
   * "minRI2"          - float min raw intensity F2 (Cy5)
   * "maxRI2"          - float max raw intensity F2 (Cy5)
   * "minRaw"          - float min raw intensity (@)
   * "maxRaw"          - float max raw intensity (@)
   * "deltaHistBin"    - float delta Hist Bin (bin width)
   * "minRatioRI"      - float min Ratio of raw intensity
   * "maxRatioRI"      - float max Ratio of raw intensity
   * "logMinRatioRI"   - float log minRatioRI
   * "logMaxRatioRI"   - float log maxRatioRI
   * "logRange"        - float log ratio range [Rmin:Rmax]
   * "logDeltaBin"     - float (MAX_INTENSITY/logRange)
   * "logMiddle"       - float -logMinRatioRI for finding RI==1.0
   * "xCmin"           - int min X coord mapped to MAE coord
   * "yCmin"           - int min Y coord mapped to MAE coord
   * "xCmax"           - int max X coord mapped to MAE coord
   * "yCmax"           - int max Y coord mapped to MAE coord
   * </PRE>
   *<P>
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return <code>null</code> if not found or error, else return list.
   */
  public final Hashtable getSampleTotHistStats(int sampleNbr)
  { /* getSampleTotHistStats */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    Hashtable ht= new Hashtable();
    ht.put("minHistData", new Float(msW.minHistData));
    ht.put("maxHistData", new Float(msW. maxHistData));
    ht.put("mnHPri", new Float(msW.mnHPri));
    ht.put("sdHPri", new Float(msW.sdHPri));
    ht.put("madHPri", new Float(msW.madHPri));
    ht.put("mnHPri1", new Float(msW.mnHPri1));
    ht.put("sdHPri1", new Float(msW.sdHPri1));
    ht.put("mnHPri2", new Float(msW.mnHPri2));
    ht.put("sdHPri2", new Float(msW.sdHPri2));
    ht.put("scaleToMaxIV", new Float(msW.scaleToMaxIV));
    ht.put("maxFV", new Float(msW.maxFV));
    ht.put("logMeanRI", new Float(msW.logMeanRI));
    ht.put("logStdDevRI,", new Float(msW.logStdDevRI));
    ht.put("logMeanAbsDevRI", new Float(msW.logMeanAbsDevRI));
    ht.put("logMaxRI", new Float(msW.logMaxRI));
    ht.put("logMinRI", new Float(msW.logMinRI));
    
    ht.put("medianRI", new Float(msW.medianRI));
    ht.put("modeRI", new Float(msW.modeRI));
    ht.put("minRI", new Float(msW.minRI));
    ht.put("maxRI", new Float(msW.maxRI));
    ht.put("minRI1", new Float(msW.minRI1));
    ht.put("maxRI2", new Float(msW.maxRI2));
    ht.put("minRI2", new Float(msW.minRI2));
    ht.put("maxRI2", new Float(msW.maxRI2));
    ht.put("minRaw", new Float(msW.minRaw));
    ht.put("maxRaw", new Float(msW.maxRaw));
    
    ht.put("deltaHistBin", new Float(msW.deltaHistBin));
    ht.put("maxRatioRI", new Float(msW.maxRatioRI));
    ht.put("minRatioRI", new Float(msW.minRatioRI));
    ht.put("logMaxRatioRI", new Float(msW.logMaxRatioRI));
    ht.put("logMinRatioRI", new Float(msW.logMinRatioRI));
    ht.put("logRange", new Float(msW.logRange));
    ht.put("logDeltaBin", new Float(msW.logDeltaBin));
    ht.put("logMiddle", new Float(msW.logMiddle));
    
    ht.put("xCmax", new Integer(msW.xCmax));
    ht.put("yCmax", new Integer(msW.yCmax));
    ht.put("xCmin", new Integer(msW.xCmin));
    ht.put("yCmin", new Integer(msW.yCmin));
    
    return(ht);
  } /* getSampleTotHistStats */
  
  
  /**
   * getSampleCalibDNAdata() - get list of a Sample calibration DNA data
   * this sample. If recalcCalDNAcalibFlag is set, then recompute it.
   * Sample numbers are in the range of [1:maxSamples].
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name           - Value</B>
   * "sampleNbr"        - int sample number
   * "meanCalDNA"       - float mean of calibration DNA
   * "stdDevCalDNA"     - float Std-Dev of calibration DNA
   * "scaleCalDNA"      - float scale factor for calibration DNA
   * "scaleUseGeneSet"  - float scale factor for 'Use Gene Set'
   * "scaleCalDNAto65K" - float scale factor for calibration DNA to 65K
   * </PRE>
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @param recalcCalDNAcalibFlag recompute calibration before get data
   * @return <code>null</code> if not found or error, else return list.
   */
  public final Hashtable getSampleCalibDNAdata(int sampleNbr,
                                               boolean recalcCalDNAcalibFlag)
  { /* getSampleCalibDNAdata */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    if(recalcCalDNAcalibFlag)
      msW.calcMeanCalDNA();
    
    Hashtable ht= new Hashtable();
    ht.put("sampleNbr",new Integer(sampleNbr));
    ht.put("meanCalDNA", new Float(msW.meanCalDNA));
    ht.put("stdDevCalDNA", new Float(msW.stdDevCalDNA));
    ht.put("scaleCalDNA", new Float(msW.scaleCalDNA));
    ht.put("scaleUseGeneSet", new Float(msW.scaleUseGeneSet));
    ht.put("scaleCalDNAto65K", new Float(msW.scaleCalDNAto65K));
    
    return(ht);
  } /* getSampleCalibDNAdata */
  
  
  /**
   * calcIntensityScaleExtrema() - compute intensity scale extrema for Sample.
   * Find intensity scale extrema for current normalization.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return true if succeed, else <code>false</code> if problems.
   */
  public final boolean calcIntensityScaleExtrema(int sampleNbr)
  { /* calcIntensityScaleExtrema */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    return(msW.calcIntensScaleExtrema());
  } /* calcIntensityScaleExtrema */
  
  
  /**
   * calcMeanCalibrationDNA() - compute (mean,StdDev) gene for Sample
   * for calibration cDNA for the current normalization.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return true if succeed else <code>false</code> if problems.
   */
  public final boolean calcMeanCalibrationDNA(int sampleNbr)
  { /* calcMeanCalibrationDNA */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    return(msW.calcMeanCalDNA());
  } /* calcMeanCalibrationDNA */
  
  
  /**
   * calcMeanUseNormGeneSet() - compute (mean,StdDev) normalization gene set
   * for Sample.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return <code>false</code> if method was not found or method failed,
   *   otherwise the flag returned by the method.
   */
  public final boolean calcMeanUseNormGeneSet(int sampleNbr)
  { /* calcMeanUseNormGeneSet */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    return(msW.calcMeanUseNormGeneSet());
  } /* calcMeanUseNormGeneSet */
  
  
  /**
   * swapCy5Cy3AndReCalcStats() - swap Cy3 and Cy5, recalculate statistics
   * for sample. Toggle swapCy5Cy3DataFlag for this sample.
   * Sample numbers are in the range of [1:maxSamples].
   * @param sampleNbr sample number to use, else current HP sample if 0
   * @return true if succeed else <code>false</code> if problems.
   */
  public final boolean swapCy5Cy3AndReCalcStats(int sampleNbr)
  { /* swapCy5Cy3AndReCalcStats */
    MaHybridSample msW= chkGetHP(sampleNbr);
    if(msW==null)
      msW= mae.ms;
    
    return(msW.swapCy5Cy3AndReCalcStats());
  } /* swapCy5Cy3AndReCalcStats */
  
       
  /** 
   * getSamplesDataVector() - get sample data for sample indexes for gene mid.
   * Save the data in precomputed sampleData[] array of 1D vectors.
   * @param sampleData - array to return samples data
   * @param samplesIndex - list of sample indices [1:N] (not [0:N-1] to lookup data
   * @param mid gene to lookup
   * @return true if succeed
   */
  public final boolean getSamplesDataVector(float sampleData[],
                                            int samplesIndex[], int mid)
  { /* getSamplesDataVector */
    if(sampleData==null || samplesIndex==null || mid<0)
      return(false);
    
    int
      s,
      nSamples= sampleData.length,
      nIndex= samplesIndex.length;    
    if(nIndex==0 || nSamples<nIndex)
      return(false);
    
    int gid= map.mid2gid[mid];    
    MaHybridSample ms;
    float
      dataBcRI,
      dataI;
    
    for(int i=0;i<nIndex;i++)
    { /* get normalized data vector */
      s= samplesIndex[i];
      ms= hps.msList[s];
      dataBcRI= ms.getRawIntens(gid, mae.useRatioDataFlag);
      dataI= ms.scaleIntensData(dataBcRI,gid);
      sampleData[i]= dataI;
    }
    
    return(true);
  } /* getSamplesDataVector */
  
  
} /* end of class MJAsample */

