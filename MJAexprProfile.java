/** File: MJAexprProfile.java */
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access expression profiles methods and data structures. 
 *<P>
 * This contains methods for manipulating expression profile data for particular
 * genes across a list of samples. If there are duplicate spots/sample 
 * (i.e. F1, F2), then it will also compute the (mean, StdDev, CoeffOvVar)
 * for each sample.  
 *
 *<PRE>
 *<B>List of methods available to Plugin-writers</B>
 * ----------------- HP-E Expression Profile Modifier Parameters ----------------
 * getNormToMaxMeanFlag() -  normalize expression profile to 1.0 using max Mean
 * getDoMeanPlotsFlag() - generate mean plots (of clusters) else individual plots
 * getEPmaxMeanSampleData() - max mean normalized sample data scale factor
 * getComputedMaxMeanEP() - max mean of the current EP sample data
 * getNormToMaxMeanFlag() -  normalize expression profile to 1.0 using max Mean
 * setDoMeanPlotsFlag() - generate mean plots (of clusters) else individual plots
 * setEPmaxMeanSampleData() - max mean normalized sample data scale factor
 * ---------------- Access HP-E Expression Profile Data For Single Gene ------------- 
 * calcExprProfile() - compute expression profile of HP-E samples for MID
 * getEPsamplesData() - list of normalized HP-E samples data for MID 
 * getEPsamplesMean() - opt. list of normalized HP-E samples means data if dups for MID
 * getEPsamplesStdDev() - opt. list of normalized HP-E samples StdDev if dups for MID
 * getEPsamplesCV() - opt. list of normalized HP-E samples CoeffOfVar if dups for MID 
 * ---------------- Access HP-E Expression Profile Data For List of Genes -------------
 * getListsOfEPsamplesData() - lists of normalized HP-E samples data EPs for list of MIDs 
 * getListsOfEPsamplesMean() - lists of normalized HP-E samples Means EPs for list of MIDs 
 * getListsOfEPsamplesStdDev() - lists of normalized HP-E samples StdDev EPs for list of MIDs 
 * getListsOfEPsamplesCOF() - lists of normalized HP-E samples CoefOfVar EPs for list of MIDs 
 * ------ Access Ordered Condition List Expression Profile Data For Single Gene ------
 * calcOrderedCondListExprProfile() - computes the data for mid and ordered condition list
 * getLastorderedCondListName() - name of last ordered condition list computation
 * getListsOfOrderedCondListMeans() - last means from ordered condition list computation 
 * getListsOfOrderedCondListStdDevs() - last StdDevs from ordered condition list computation
 * getListsOfOrderedCondListCVs() - last CVs from ordered condition list computation
 * getListsOfOrderedCondListNbrs() - last cond sizes from ordered condition list computation
 *  getListsOfOrderedCondListNames() - last names from ordered condition list computation
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
 *         G. Thornwall (SAIC), NCI-Frederick, Frederick, M
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
public class MJAexprProfile extends MJAbase
{  
  /** Expression profile used for accessing data */
  private static ExprProfile
    ep;
  
  /** Ordered Condition List name */
  private static String
    oclName;
  
  /** Ordered Condition List names for each condition */
  private static String
    oclCondNames[];
  
  /** Ordered Condition List # conditions in list */
  private static int
    oclNbrConds;
  
  /** Ordered Condition List maximum mean seen in any condition */
  private static float
    maxOCLmean;
  
  /** Ordered Condition List # samples/condition */
  private static int
    oclNbr[];
  
  /** Ordered Condition List Means of each condition */
  private static float
    oclMeans[];
  
  /** Ordered Condition List StdDevs of each condition */
  private static float
    oclStdDev[];
  
  /** Ordered Condition List Coefficient of Variation of each condition  */
  private static float
    oclCV[];
  

  /**
   * MJAexprProfile() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAexprProfile()
  { /* MJAexprProfile */
    ep= new ExprProfile(mae,-1, true /* default normToOneFlag */);
    
    oclName= null;
    oclNbrConds= 0;
    oclCondNames= null;
    oclNbr= null;
    oclMeans= null;
    oclStdDev= null;
    oclCV= null;
  } /* MJAexprProfile */
  
  
  /* ----------------- HP-E Expression Profile Modifier Parameters ---------------- */
  
  /**
   * getNormToOneFlag() - flag of normalize expression profile to 1.0 flag
   * @return flag
   */
  public final boolean getNormToOneFlag()
  { return(ep.normToOneFlag); }
  
  
  /**
   * getDoMeanPlotsFlag() - flag to generate mean plots (of the clusters) else
   * individual plots
   * @return flag
   */
  public final boolean getDoMeanPlotsFlag()
  { return(ep.doMeanPlotsFlag); }
  
  
  /**
   * getEPmaxMeanSampleData() - max mean normalized sample data scale factor
   * computed over all samples and all genes being used in a list of expression
   * profiles. This is an externally specified scale factor.
   * @return value
   */
  public final float getEPmaxMeanSampleData()
  { return(ep.maxHPdataMn); }
  
  
  /**
   * getComputedMaxMeanEP() - max mean of the current EP sample data
   * @return value
   */
  public final float getComputedMaxMeanEP()
  { return(ep.maxMean); }
  
  
  /**
   * setNormToOneFlag() - flag of normalize expression profile to 1.0 flag
   * @return flag
   */
  public final void setNormToOneFlag(boolean normToOneFlag)
  { ep.normToOneFlag= normToOneFlag; }
  
  
  /**
   * setDoMeanPlotsFlag() - flag to generate mean plots (of the clusters) else
   * individual plots
   * @param doMeanPlotsFlag value to set flag
   * @return flag
   */
  public final void setDoMeanPlotsFlag(boolean doMeanPlotsFlag)
  { ep.doMeanPlotsFlag= doMeanPlotsFlag; }
  
  
  /**
   * setEPmaxMeanSampleData() - max mean normalized sample data scale factor
   * computed over all samples and all genes being used in a list of expression
   * profiles. This is an externally specified scale factor.
   * @param maxMeanSampleData value to set
   * @return value
   */
  public final void setEPmaxMeanSampleData(float maxMeanSampleData)
  { ep.maxHPdataMn= maxMeanSampleData; }
  
  
  /* ----------------- Access HP-E Expression Profile Data For Single Gene ---------------- */
  
  /**
   * calcExprProfile() - compute expression profile of HP-E samples for MID
   * The data is then returned using other methods.
   * @param mid is the Master Gene Index to use in compouting expression profile
   * @return true if succeed, else there was a problem
   * @see #getEPsamplesData
   * @see #getEPsamplesMean
   * @see #getEPsamplesStdDev
   * @see #getEPsamplesCV
   */
  public final boolean calcExprProfile(int mid)
  { return(ep.updateData(mid)); }
  
  
  /**
   * getEPsamplesData() - list of normalized HP-E samples data for MID.
   * If mid is -1, then use values computed with calcExprProfile(), else
   * recompute it for the new MID.
   * @param mid (optionl) Master Gene Index to use in compouting expression profile
   * @return list of data if succeed, else null there was a problem
   * @see #getEPsamplesMean
   * @see #getEPsamplesStdDev
   * @see #getEPsamplesCV
   */
  public final float[] getEPsamplesData(int mid)
  {
    if(mid!=-1)
      ep.updateData(mid);
    return(ep.hpData);
  }
  
  
  /**
   * getEPsamplesMean() - opt. list of normalized HP-E samples means data if duplicates for MID.
   * If mid is -1, then use values computed with calcExprProfile(), else
   * recompute it for the new MID.
   * @param mid (optionl) Master Gene Index to use in compouting expression profile
   * @return list of data if succeed, else null there was a problem
   * @see #getEPsamplesData
   * @see #getEPsamplesStdDev
   * @see #getEPsamplesCV
   */
  public final float[] getEPsamplesMean(int mid)
  {
    if(mid!=-1)
      ep.updateData(mid);
    return(ep.hpDataMn);
  }
  
  
  /**
   * getEPsamplesStdDev() - opt. list of normalized HP-E samples StdDev if duplicates for MID.
   * If mid is -1, then use values computed with calcExprProfile(), else
   * recompute it for the new MID.
   * @param mid (optionl) Master Gene Index to use in compouting expression profile
   * @return list of data if succeed, else null there was a problem
   * @see #getEPsamplesData
   * @see #getEPsamplesMean
   * @see #getEPsamplesCV
   */
  public final float[] getEPsamplesStdDev(int mid)
  {
    if(mid!=-1)
      ep.updateData(mid);
    return(ep.hpDataSD);
  }
  
  
  /**
   * getEPsamplesCV() - opt. list of normalized HP-E samples CoefOfVar if duplicates for MID.
   * If mid is -1, then use values computed with calcExprProfile(), else
   * recompute it for the new MID.
   * @param mid (optionl) Master Gene Index to use in compouting expression profile
   * @return list of data if succeed, else null there was a problem
   * @see #getEPsamplesData
   * @see #getEPsamplesMean
   * @see #getEPsamplesStdDev
   */
  public final float[] getEPsamplesCV(int mid)
  {
    if(mid!=-1)
      ep.updateData(mid);
    return(ep.cvData);
  }
  
  /* ----------------- Access HP-E samples Expression Profile Data For Multiple Genes ---------------- */
  
  /**
   * getListsOfEPsamplesData() - lists of normalized HP-E samples data EPs for list of MIDs.
   * Compute expression profiles for all of the mid's in the midList[] that are legal
   * (i.e. no -1). If there are holes (i.e. -1's ) in the list, the data is 0.0 for those
   * entries.
   * @param midList list of Master Gene Indices to compute expression profiles
   * @return list of EPs of data if succeed, else null there was a problem
   */
  public final float[][] getListsOfEPsamplesData(int midList[])
  { /* getListsOfEPsamplesData */
    if(midList==null || midList.length==0)
      return(null);
    int
      mid,
      nHP_E= mae.hps.nHP_E,
      nGenes= midList.length;
    float listOfEPs[][]= new float[nGenes][nHP_E];
    for(int n= 0;n<nGenes;n++)
    {
      mid= midList[n];
      if(mid==-1)
        continue;
      ep.updateData(mid);
      for(int i=0;i<nHP_E;i++)
        listOfEPs[n][i]=  ep.hpData[i];
    }
    return(listOfEPs);
  } /* getListsOfEPsamplesData */
  
  
  /**
   * getListsOfEPsamplesMean() - lists of normalized HP-E samples Means EPs for list of MIDs
   * Compute expression profiles for all of the mid's in the midList[] that are legal
   * (i.e. no -1). If there are holes (i.e. -1's ) in the list, the data is 0.0 for those
   * entries.
   * @param midList list of Master Gene Indices to compute expression profiles
   * @return list of EPs of data if succeed, else null there was a problem
   */
  public final float[][] getListsOfEPsamplesMean(int midList[])
  { /* getListsOfEPsamplesMean */
    if(midList==null || midList.length==0)
      return(null);
    int
      mid,
      nHP_E= mae.hps.nHP_E,
      nGenes= midList.length;
    float listOfEPs[][]= new float[nGenes][nHP_E];
    for(int n= 0;n<nGenes;n++)
    {
      mid= midList[n];
      if(mid==-1)
        continue;
      ep.updateData(mid);
      for(int i=0;i<nHP_E;i++)
        listOfEPs[n][i]=  ep.hpDataMn[i];
    }
    return(listOfEPs);
  } /* getListsOfEPsamplesMean */
  
  
  /**
   * getListsOfEPsamplesStdDev() - lists of normalized HP-E samples StdDev EPs for list of MIDs.
   * Compute expression profiles for all of the mid's in the midList[] that are legal
   * (i.e. no -1). If there are holes (i.e. -1's ) in the list, the data is 0.0 for those
   * entries.
   * @param midList list of Master Gene Indices to compute expression profiles
   * @return list of EPs of data if succeed, else null there was a problem
   */
  public final float[][] getListsOfEPsamplesStdDev(int midList[])
  { /* getListsOfEPsamplesStdDev */
    if(midList==null || midList.length==0)
      return(null);
    int
      mid,
      nHP_E= mae.hps.nHP_E,
      nGenes= midList.length;
    float listOfEPs[][]= new float[nGenes][nHP_E];
    for(int n= 0;n<nGenes;n++)
    {
      mid= midList[n];
      if(mid==-1)
        continue;
      ep.updateData(mid);
      for(int i=0;i<nHP_E;i++)
        listOfEPs[n][i]=  ep.hpDataSD[i];
    }
    return(listOfEPs);
  } /* getListsOfEPsamplesStdDev */
  
  
  /**
   * getListsOfEPsamplesCV() - lists of normalized HP-E samples CoefOfVar EPs for list of MIDs.
   * Compute expression profiles for all of the mid's in the midList[] that are legal
   * (i.e. no -1). If there are holes (i.e. -1's ) in the list, the data is 0.0 for those
   * entries.
   * @param midList list of Master Gene Indices to compute expression profiles
   * @return list of EPs of data if succeed, else null there was a problem
   */
  public final float[][] getListsOfEPsamplesCV(int midList[])
  { /* getListsOfEPsamplesCV */
    if(midList==null || midList.length==0)
      return(null);
    int
      mid,
      nHP_E= mae.hps.nHP_E,
      nGenes= midList.length;
    float listOfEPs[][]= new float[nGenes][nHP_E];
    for(int n= 0;n<nGenes;n++)
    {
      mid= midList[n];
      if(mid==-1)
        continue;
      ep.updateData(mid);
      for(int i=0;i<nHP_E;i++)
        listOfEPs[nGenes][i]=  ep.cvData[i];
    }
    return(listOfEPs);
  } /* getListsOfEPsamplesCV */
  
  
  /* --------- Access Ordered Condition List Expression Profile Data For Single Gene --------- */
  
  /**
   * calcOrderedCondListExprProfile() - compute normalized ordered condition list EPs for MID.
   * Compute expression profiles for all of the mid if it is legal (i.e. no -1).
   * Each entry of the expression list returned is the average of the data
   * for the corresponding condition in the ordered condition list. The size of
   * the expression profile returned is the size of the ordered condition list.
   *<P>
   * @param orderedCondListName name of ordered condition list of condition lists
   * @param mid (optionl) Master Gene Index to use in compouting expression profile
   * @return true if succeed, else false there was a problem
   *<BR>
   * The data is accessed with the following additional methods.
   * @see #getListsOfOrderedCondListMeans
   * @see #getListsOfOrderedCondListStdDevs
   * @see #getListsOfOrderedCondListCVs
   * @see #getListsOfOrderedCondListNbrs
   * @see #getListsOfOrderedCondListNames
   */
  public final boolean calcOrderedCondListExprProfile(String orderedCondListName,
  int mid)
  { /* calcOrderedCondListExprProfile */
    if(mid!=-1)
      return(false);
    Condition
      cond,
      orderedCondList[]= cdList.lookupNamedOrderedCondList(orderedCondListName);
    if(orderedCondList==null)
      return(false);
    
    oclName= orderedCondListName;                /* save it */
    int
      gid1= map.mid2gid[mid],
      gid2= map.gidToGangGid[gid1],
      DATA_F1TOT= mae.ms.DATA_F1TOT,
      DATA_F2TOT= mae.ms.DATA_F2TOT,
      DATA_RATIO_F1F2TOT= mae.ms.DATA_RATIO_F1F2TOT,
      nOConds= orderedCondList.length,
      maxFIELDS= mae.cfg.maxFIELDS,
      type= (mae.useRatioDataFlag) ? DATA_RATIO_F1F2TOT : DATA_F1TOT;
    boolean useRatioDataFlag= mae.useRatioDataFlag;
    float g1, g2;
    double
      sampleData,
      sumData,
      sumDataSq,
      diff, mean,
      stdDev= 0.0F,
      cv= 0.0F;
    MaHybridSample ms;
    
    oclNbrConds= nOConds;    /* save state */
    oclNbr= new int[nOConds];
    oclCondNames= new String[nOConds];
    oclMeans= new float[nOConds];
    oclStdDev= new float[nOConds];
    oclCV= new float[nOConds];
    
    maxOCLmean= 0.0F;
    for(int n= 0;n<nOConds;n++)
    { /* compute the mean expression for the samples in each condition */
      cond= orderedCondList[n];
      int nMScond= cond.nMScond;
      sumData= 0.0;
      sumDataSq= 0.0F;
      for(int i= 0;i<nMScond;i++)
      { /* compute mean and cv from normalized condition samples data */
        ms= cond.msCond[i];
        if(maxFIELDS>1)
        { /* has replicate spots/gene */
          /* normalized (totQ - bkgrdQ) */
          g1= ms.getDataByGID(gid1, useRatioDataFlag, DATA_F1TOT);
          g2= ms.getDataByGID(gid1, useRatioDataFlag, DATA_F2TOT);
        }
        else
        { /* use single spot or ratio data */
          g1= ms.getDataByGID(gid1, useRatioDataFlag, type);
          g2= g1;                  /* so CV= 0.0 */
        }
        
        sampleData= (g1+g2)/2.0F;
        
        /* Compute coefficient of variation per sample - not used... */
        //diff= (float)(g1-g2);
        //cv= (sampleData==0.0F) ? 0.0F : Math.abs(diff)/sampleData;
        
        sumData += sampleData/2.0F;
        sumDataSq += sampleData*sampleData;
      } /* compute mean and cv from normalized condition samples data */
      
      mean= (nMScond>0) ? sumData/nMScond : 0;
      oclNbr[n]= nMScond;
      oclCondNames[n]= cond.cName;
      oclMeans[n]= (float)mean;
      maxOCLmean= (float)Math.max(maxOCLmean,mean);
      
      /* Now compute the StdDev */
      stdDev= (nMScond>=2) 
                 ? Math.sqrt((sumDataSq - nMScond*mean*mean)/nMScond)
                 : 0.0;
      oclStdDev[n]= (float)stdDev;
      
      cv= (stdDev!=0.0) ? mean/stdDev : 0.0;
      oclCV[n]= (float)cv;
    } /* compute the mean expression for the samples in each condition */
    
    return(false);
  } /* calcOrderedCondListExprProfile */
  
  
  /**
   * getLastorderedCondListName() - get name of last ordered condition list computation
   * computed using calcOrderedCondListExprProfile().
   * @return true if succeed, else there was a problem
   */
  public final String getLastorderedCondListName()
  { return(oclName); }
  
  
  /**
   * getListsOfOrderedCondListMeans() is last conditions means[] from ordered condition
   * list computation computed using calcOrderedCondListExprProfile().
   * @return true if succeed, else there was a problem
   */
  public final float[] getListsOfOrderedCondListMeans()
  { return(oclMeans); }
  
  
  /**
   * getListsOfOrderedCondListStdDevs() is last conditions StdDev[] from ordered condition
   * list computation computed using calcOrderedCondListExprProfile().
   * @return true if succeed, else there was a problem
   */
  public final float[] getListsOfOrderedCondListStdDevs()
  { return(oclStdDev); }
  
  
  /**
   * getListsOfOrderedCondListCVs() is last conditions Coeff of Variation[] from
   * ordered condition list computation computed using calcOrderedCondListExprProfile().
   * @return true if succeed, else there was a problem
   */
  public final float[] getListsOfOrderedCondListCVs()
  { return(oclCV); }
  
  
  /**
   * getListsOfOrderedCondListNbrs() is last conditions sizes [] from ordered condition
   * list computation computed using calcOrderedCondListExprProfile().
   * @return true if succeed, else there was a problem
   */
  public final int[] getListsOfOrderedCondListNbrs()
  { return(oclNbr); }
  
  
  /**
   * getListsOfOrderedCondListNames() is last conditions means[] from ordered condition
   * list computation computed using calcOrderedCondListExprProfile().
   * @return true if succeed, else there was a problem
   */
  public final String[] getListsOfOrderedCondListNames()
  { return(oclCondNames); }
  
  
} /* end of class MJAexprProfile */

