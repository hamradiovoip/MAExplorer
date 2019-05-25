/** File: MJAsampleList.java */
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer GatherScatter API class to access MJAsampleList methods and data structures. 
 *
 * Access lists of samples top-level data
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * ----------------- global sample set data and sizes of lists ---
 * getNbrHPsamples() - get # of ALL HP samples currently loaded in DB
 * getSizeOf_HP_X_set() - get # of HP-X 'set' samples
 * getSizeOf_HP_Y_set() - get # of HP-Y 'set' samples
 * getSizeOf_HP_E_set() - get # of HP-E 'set' samples
 * getCurrent_HP_index() - get Current HP sample index
 * getCurrent_HP_X_index() - get Current HP-X sample index
 * getCurrent_HP_Y_index() - get Current HP-Y sample index
 * getCurrent_HP_name() - get Current HP sample name
 * getCurrent_HP_X_name() - get Current HP-X sample name
 * getCurrent_HP_Y_name() - get Current HP-Y sample name
 * get_HP_SampleName() - get HP sample name for sample number
 * getClassXname() - get the current Class X name.
 * getClassYname() - get the current Class Y name
 * ----------------- Lists of samples description data ----------
 * getAllSampleNames()- get list of all active sample names used in DB
 * getHP_Xset_SampleNames()- get list of HP-X 'set' sample names used in DB
 * getHP_Yset_SampleNames()- get list of HP-Y 'set' sample names used in DB
 * getHP_Elist_SampleNames()- get list of HP-E 'set' sample names used in DB
 * getHP_Xset_SampleIDs()- get list of HP-X 'set' sample IDs used in DB
 * getHP_Yset_SampleIDs()- get list of HP-Y 'set' sample IDs used in DB
 * getHP_Elist_SampleIDs()- get list of HP-E 'set' sample IDs used in DB
 * getHP_Xset_SampleNbrs()- get list of HP-X 'set' sample numbers used in DB
 * getHP_Yset_SampleNbrs()- get list of HP-Y 'set' sample numbers used in DB
 * getHP_Elist_SampleNbrs()- get list of HP-E 'list' sample numbers used in DB
 * getListOfAllActiveHPsampleIndices() - get list of all active sample indices
 * getListOfHPXsampleIndices() - get list of sample indices in HP-X set
 * getListOfHPXYampleIndices() - get list of sample indices in HP-Y set
 * getListOfHPEsampleIndices() - get list of sample indices in HP-E list
 * getHP_XYsetsStatistics() - get HP-X and HP-Y 'set statistics for gene.
 * getListOfSampleMenuEntries() - get list of sample menu names in in menu.
 * getListOfSampleProjectEntries() - get list of sample project names. 
 * getListOfSampleStageNames() - get list of sample full stage text names. 
 * getListOfSampleNames() - get list sample database file name (no .quant). 
 * getGrandMeanCalDNA() - get compute grand mean of calibration DNA
 * getGrandMeanUseGeneSet() - get compute grand mean of 'Use Gene Set'
 * getSampleVsSampleCorrelationsTable()- list of Sample Vs Sample corre tbl
 * getSampleStatisticsTable() - get list of all Sample statistics table.
 * ---------------- Set Sample sets and lists ------------------     
 * setListOfXsamples() - set int[] list of sample indices in HP-X 'set'.    
 * setListOfYsamples() - set int[] list of sample indices in HP-Y 'set'. 
 * setListOfEsamples() - set int[] list of sample indices in HP-E 'list'. 
 * ----- Lists of HP, HP-X, HP-Y, HP-X and HP-Y 'sets' intensity data ------
 * getRawF1F2DataAndBkgdForSample() - get filtered spot list raw intensity, raw bkgrd, props data
 * getAllRawF1F2DataAndBkgdForSample() - get all spot list raw intensity, raw bkgrd, props data
 * getF1F2dataForSample() - get filtered (f1,f2,prop) lists of Field (F1,F2) intensity data
 * getAllF1F2dataForSample() - get all (f1,f2,prop) lists of Field (F1,F2) intensity data
 * getHPdataForSample() - get filtered (data,prop) list single sample's intensity data
 * getAllHPdataForSample() - get all normalized (data,prop) list single sample's intensity data
 * getAllRawHPdataForSample() - get all raw (data,prop) list single sample's intensity data
 * getHP_XandYsamplesData() - get filtered (X,Y,prop) lists of single X and Y samples normalized data
 * getAllHP_XandYsamplesData() - get all (X,Y,prop) lists of single X and Y samples normalized data
 * getHP_XandYsetDataForSample() - get filtered (X,Y,prop) lists of HP-X/-Y 'sets' mean normalized data
 * getAllHP_XandYsetDataForSample() - get filtered (X,Y,prop) lists of HP-X/-Y 'sets' mean normalized data
 * getMeanDataGeneListDataForCondition() - get filtered (mn,SD,prop) list Condition samples' normalized data
 * getAllMeanDataGeneListDataForCondition() - get all (mn,SD,prop) list Condition samples' normalized data
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
 * @version $Date: 2003/11/24 21:18:54 $   $Revision: 1.20 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
public class MJAsampleList extends MJAbase
{
  
  /**
   * MJAsampleList() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAsampleList()
  { /* MJAsampleList */
  } /* MJAsampleList */
  
  
  /* ----------- global sample set data and sizes of lists ----- */
  
  /**
   * getNbrHPsamples() - get # of ALL HP samples currently loaded in DB
   * @return value else <code>null</code> if not found.
   */
  public final int getNbrHPsamples()
  { return(mae.iHPnbr); }
  
  
  /**
   * getSizeOf_HP_X_set() - get # of HP-X 'set' samples
   * @return value else <code>null</code> if not found.
   */
  public final int getSizeOf_HP_X_set()
  { return(hps.nHP_X); }
  
  
  /**
   * getSizeOf_HP_Y_set() - get # of HP-Y 'set' samples
   * @return value else <code>null</code> if not found.
   */
  public final int getSizeOf_HP_Y_set()
  { return(hps.nHP_Y); }
  
  
  /**
   * getSizeOf_HP_E_set() - get # of HP-E 'set' samples.
   * @return value else <code>null</code> if not found.
   */
  public final int getSizeOf_HP_E_set()
  { return(hps.nHP_E); }
  
  
  /**
   * getCurrent_HP_index() - get Current HP sample index.
   * Sample index numbers are in the range of [1:maxSamples].
   * @return value else <code>null</code> if not found.
   */
  public final int getCurrent_HP_index()
  { return(mae.curHP); }
  
  
  /**
   * getCurrent_HP_X_index() - get Current HP-X sample index.
   * Sample index numbers are in the range of [1:maxSamples].
   * @return value else <code>-1</code> if not found.
   */
  public final int getCurrent_HP_X_index()
  { return(mae.curHP_X); }
  
  
  /**
   * getCurrent_HP_Y_index() - get Current HP-Y sample index.
   * Sample index numbers are in the range of [1:maxSamples].
   * @return value else <code>-1</code> if not found.
   */
  public final int getCurrent_HP_Y_index()
  { return(mae.curHP_Y); }
  
  
  /**
   * getCurrent_HP_name() - get Current HP sample name
   * @param getFullNameFlag to get fullStageText sample name else sample name
   * @return name else <code>null</code> if not found.
   */
  public final String getCurrent_HP_name(boolean getFullNameFlag)
  { /* getCurrent_HP_name */
    String name= (mae.curHP==-1) 
                    ? null
                    : ((getFullNameFlag) 
                          ? hps.msList[mae.curHP].hpName
                          : hps.msList[mae.curHP].fullStageText);
    return(name);
  } /* getCurrent_HP_name */
  
  
  /**
   * getCurrent_HP_X_name() - get Current HP-X sample name
   * @param getFullNameFlag to get fullStageText sample name else sample name
   * @return name else <code>null</code> if not found.
   */
  public final String getCurrent_HP_X_name(boolean getFullNameFlag)
  { /* getCurrent_HP_X_name */
    String name= (mae.curHP_X==-1) 
                    ? null
                    : ((getFullNameFlag) 
                          ? hps.msList[mae.curHP_X].hpName
                          : hps.msList[mae.curHP_X].fullStageText);
    return(name);
  } /* getCurrent_HP_X_name */
  
  
  /**
   * getCurrent_HP_Y_name() - get Current HP-Y sample name
   * @param getFullNameFlag to get fullStageText sample name else sample name
   * @return name else <code>null</code> if not found.
   */
  public final String getCurrent_HP_Y_name(boolean getFullNameFlag)
  { /* getCurrent_HP_Y_name */
    String name= (mae.curHP_Y==-1) 
                    ? null
                    : ((getFullNameFlag) 
                          ? hps.msList[mae.curHP_Y].hpName
                          : hps.msList[mae.curHP_Y].fullStageText);
    return(name);
  } /* getCurrent_HP_Y_name */
  
  
  /**
   * get_HP_SampleName() - get HP sample name for sample number
   * @param sampleNbr is the number of the sample to look up
   * @param getFullNameFlag to get fullStageText sample name else sample name
   * @return name else <code>null</code> if not found.
   */
  public final String get_HP_SampleName(int sampleNbr, boolean getFullNameFlag)
  { /* get_HP_SampleName */
    String name= (sampleNbr==-1) 
                    ? null
                    : ((getFullNameFlag) 
                          ? hps.msList[sampleNbr].hpName
                          : hps.msList[sampleNbr].fullStageText);
    return(name);
  } /* get_HP_SampleName */  
  
  
  /**
   * getClassXname() - get the current Class X name.
   * @return class X name
   */
  public final String getClassXname()
  { /* getClassXname */
    return(mae.classNameX);
  } /* getClassXname */
  
  
  /**
   * getClassYname() - get the current Class Y name.
   * @return class Y name
   */
  public final String getClassYname()
  { /* getClassYname */
    return(mae.classNameY);
  } /* getClassYname */
  
  
  /* -------------- Lists of samples description data ---------- */
  
  /**
   * getAllSampleNames()- get list of all active sample names used in DB
   * @return values else <code>null</code> if error
   */
  public final String[] getAllSampleNames()
  { /* getAllSampleNames */
    if(mae.iHPnbr<=0)
      return(null);
    String sR[]= new String[mae.iHPnbr];
    for(int j=0;j<mae.iHPnbr;j++)
      sR[j]= mae.iSampleName[j+1];
    return(sR);
  } /* getAllSampleNames */
  
  
  /**
   * getHP_Xset_SampleNames()- get list of HP-X 'set' sample names used in DB
   * @return values else <code>null</code> if error
   */
  public String[] getHP_Xset_SampleNames()
  { /* getHP_Xset_SampleNames */
    if(hps.nHP_X<=0)
      return(null);
    String sR[]= new String[hps.nHP_X];
    for(int j=0;j<hps.nHP_X;j++)
      sR[j]= hps.msListX[j+1].hpName;
    return(sR);
  } /* getHP_Xset_SampleNames */
  
  
  /**
   * getHP_Yset_SampleNames()- get list of HP-Y 'set' sample names used in DB
   * @return values else <code>null</code> if error
   */
  public final String[] getHP_Yset_SampleNames()
  { /* getHP_Yset_SampleNames */
    if(hps.nHP_Y<=0)
      return(null);
    String sR[]= new String[hps.nHP_Y];
    for(int j=0;j<hps.nHP_Y;j++)
      sR[j]= hps.msListY[j+1].hpName;
    return(sR);
  } /* getHP_Yset_SampleNames */
  
  
  /**
   * getHP_Elist_SampleNames()- get list of HP-E 'list' sample IDs used in DB
   * @return values else <code>null</code> if error
   */
  public final String[] getHP_Elist_SampleNames()
  { /* getHP_Elist_SampleNames */
    if(hps.nHP_E<=0)
      return(null);
    String sR[]= new String[hps.nHP_E];
    for(int j=0;j<hps.nHP_E;j++)
      sR[j]= hps.msListE[j+1].hpName;
    return(sR);
  } /* getHP_Elist_SampleNames */
  
  
  /**
   * getHP_Xset_SampleIDs()- get list of HP-X 'set' sample IDs used in DB
   * @return values else <code>null</code> if error
   */
  public final String[] getHP_Xset_SampleIDs()
  { /* getHP_Xset_SampleIDs */
    if(hps.nHP_X<=0)
      return(null);
    String sR[]= new String[hps.nHP_X];
    for(int j=0;j<hps.nHP_X;j++)
      sR[j]= hps.msListX[j+1].sampleID;
    return(sR);
  } /* getHP_Xset_SampleIDs */
  
  
  /**
   * getHP_Yset_SampleIDs()- get list of HP-Y 'set' sample IDs used in DB
   * @return values else <code>null</code> if error
   */
  public final String[] getHP_Yset_SampleIDs()
  { /* getHP_Yset_SampleIDs */
    if(hps.nHP_Y<=0)
      return(null);
    String sR[]= new String[hps.nHP_Y];
    for(int j=0;j<hps.nHP_Y;j++)
      sR[j]= hps.msListY[j+1].hpName;
    return(sR);
  } /* getHP_Yset_SampleIDs */
  
  
  /**
   * getHP_Elist_SampleIDs()- get list of HP-E 'list' sample IDs used in DB
   * @return values else <code>null</code> if error
   */
  public final String[] getHP_Elist_SampleIDs()
  { /* getHP_Elist_SampleIDs */
    if(hps.nHP_E<=0)
      return(null);
    String sR[]= new String[hps.nHP_E];
    for(int j=0;j<hps.nHP_E;j++)
      sR[j]= hps.msListE[j+1].sampleID;
    return(sR);
  } /* getHP_Elist_SampleIDs */
  
  
  /**
   * getHP_Xset_SampleNbrs()- get list of HP-X 'set' sample numbers used in DB
   * @return values else <code>null</code> if error
   */
  public final int[] getHP_Xset_SampleNbrs()
  { /* getHP_Xset_SampleNbrs */
    if(hps.nHP_X<=0)
      return(null);
    int idxR[]= new int[hps.nHP_X];
    for(int j=0;j<hps.nHP_X;j++)
      idxR[j]= hps.msListX[j+1].idxHP;
    return(idxR);
  } /* getHP_Xset_SampleNbrs */
  
  
  /**
   * getHP_Yset_SampleNbrs()- get list of HP-Y 'set' sample numbers used in DB
   * @return values else <code>null</code> if error
   */
  public final int[] getHP_Yset_SampleNbrs()
  { /* getHP_Yset_SampleNbrs */
    if(hps.nHP_Y<=0)
      return(null);
    int idxR[]= new int[hps.nHP_Y];
    for(int j=0;j<hps.nHP_Y;j++)
      idxR[j]= hps.msListY[j+1].idxHP;
    return(idxR);
  } /* getHP_Yset_SampleNbrs */
  
  
  /**
   * getHP_Elist_SampleNbrs()- get list of HP-E 'list' sample numbers used in DB
   * @return values else <code>null</code> if error
   */
  public final int[] getHP_Elist_SampleNbrs()
  { /* getHP_Elist_SampleNbrs */
    if(hps.nHP_E<=0)
      return(null);
    int idxR[]= new int[hps.nHP_E];
    for(int j=0;j<hps.nHP_E;j++)
      idxR[j]= hps.msListE[j+1].idxHP;
    return(idxR);
  } /* getHP_Elist_SampleNbrs */
  
  
  /**
   * getListOfAllActiveHPsampleIndices() - get list of all active sample HP indices.
   * Sample index numbers are in the range of [1:maxSamples].
   * @return list of indices else <code>null</code> if the list is empty.
   */
  public final int[] getListOfAllActiveHPsampleIndices()
  { /* getListOfAllActiveHPsampleIndices */
    int
      n= hps.nHP,
      listIdx[];
    MaHybridSample 
      msW,
      msLst[]= hps.msList;
    if(n==0)
      return(null);
    
    /* Save int linear data structure */
    int
      j= 0,
      data[]= new int[n];
    
    for(int i=1;i<=n;i++)
    {
      msW= msLst[i];
      data[j++]= msW.idxHP;
    }
    
    return(data);
  } /* getListOfAllActiveHPsampleIndices */
  
  
  /**
   * getListOfHPXsampleIndices() - get list of sample indices in HP-X set.
   * Sample index numbers are in the range of [1:maxSamples].
   * @return list of indices else <code>null</code> if the list is empty.
   */
  public final int[] getListOfHPXsampleIndices()
  { /* getListOfHPXsampleIndices */
    int
      n= hps.nHP_X,
      listIdx[];
    MaHybridSample
      msW,
      msLst[]= hps.msListX;
    if(n==0)
      return(null);
    
    /* Save int linear data structure */
    int
      j= 0,
      data[]= new int[n];
    
    for(int i=1;i<=n;i++)
    {
      msW= msLst[i];
      data[j++]= msW.idxHP;
    }
    
    return(data);
  } /* getListOfHPXsampleIndices */
  
  
  /**
   * getListOfHPYsampleIndices() - get list of sample indices in HP-Y set.
   * Sample index numbers are in the range of [1:maxSamples].
   * @return list of indices else <code>null</code> if the list is empty.
   */
  public final int[] getListOfHPYsampleIndices()
  { /* getListOfHPYsampleIndices */
    int
      n= hps.nHP_Y,
      listIdx[];
    MaHybridSample
      msW,
      msLst[]= hps.msListY;
    if(n==0)
      return(null);
    
    /* Save int linear data structure */
    int
      j= 0,
      data[]= new int[n];
    
    for(int i=1;i<=n;i++)
    {
      msW= msLst[i];
      data[j++]= msW.idxHP;
    }
    
    return(data);
  } /* getListOfHPYsampleIndices */
  
  
  /**
   * getListOfHPEsampleIndices() - get list of sample indices in HP-E list.
   * Sample index numbers are in the range of [1:maxSamples].
   * @return list of indices else <code>null</code> if the list is empty.
   */
  public final int[] getListOfHPEsampleIndices()
  { /* getListOfHPEsampleIndices */
    int
      n= hps.nHP_E,
      listIdx[];
    MaHybridSample
      msW,
      msLst[]= hps.msListE;
    if(n==0)
      return(null);
    
    /* Save int linear data structure */
    int
      j= 0,
      data[]= new int[n];
    
    for(int i=1;i<=n;i++)
    {
      msW= msLst[i];
      data[j++]= msW.idxHP;
    }
    
    return(data);
  } /* getListOfHPEsampleIndices */
  
  
  /**
   * getHP_XYsetsStatistics() - get HP-X and HP-Y 'set statistics for gene.
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Property name           - Value</B>
   * "HPXsetData"           - float[0:sizeofHPXset-1] array of normalized HP-X
   *                          set data
   * "HPYsetData"           - float[0:sizeofHPYset-1] array of normalized HP-Y
   *                          set data
   * "CVF1F2eachHPX"        - float[0:sizeofHPXset-1] array of (F1,F2) CV for
   *                          each HP sample in HP-X set
   * "CVF1F2eachHPY"        - float[0:sizeofHPYset-1] array of (F1,F2) CV for
   *                          each HP sample in HP-Y set
   * "sizeofHPXset"         - int number of samples in HP-X set
   * "sizeofHPYset"         - int number of samples in HP-Y set
   * "MeanHPXset"           - float mean of HP-X set
   * "MeanHPYset"           - float mean of HP-Y set
   * "StdDevHPXset"         - float StdDev of HP-X set
   * "StdDevHPYset"         - float StdDev of HP-Y set
   * "CoeffVariationHPXset" - float CoeffVariation of HP-X set
   * "CoeffVariationHPYset" - float CoeffVariation of HP-Y set
   * "LabelForHPXset"       - String Plot label for HP-X set
   * "LabelForHPYset"       - String Plot label for HP-Y set
   * "ClassNameHPXset"      - String ClassName for HP-X set
   * "ClassNameHPYset"      - String ClassName for HP-Y set
   * </PRE>
   *
   * @param mid MID gene data to access
   * @return value <code>null</code> if mid was not found,
   *   otherwise the current values are returned in a Hashtable.
   */
  public final Hashtable getHP_XYsetsStatistics(int mid)
  { /* getHP_XYsetsStatistics */
    hpXYdata.updateData(mid, false);
    
    Hashtable ht= new Hashtable(25);
    
    ht.put("HPXsetData",hpXYdata.hpDataX);
    ht.put("HPYsetData",hpXYdata.hpDataY);
    ht.put("HPEsetData",hpXYdata.hpDataE);
    ht.put("CVF1F2eachHPX",hpXYdata.f1f2CV_X);
    ht.put("CVF1F2eachHPY",hpXYdata.f1f2CV_Y);
    ht.put("CVF1F2eachHPE",hpXYdata.f1f2CV_E);
    ht.put("nX",new Integer(hpXYdata.nX));
    ht.put("nY",new Integer(hpXYdata.nY));
    ht.put("nE",new Integer(hpXYdata.nE));
    ht.put("MeanHPXdata",new Float(hpXYdata.mnXdata));
    ht.put("MeanHPYdata",new Float(hpXYdata.mnYdata));
    ht.put("MeanHPEdata",new Float(hpXYdata.mnEdata));
    ht.put("StdDevHPXdata",new Float(hpXYdata.stdDevXdata));
    ht.put("StdDevHPYdata",new Float(hpXYdata.stdDevYdata));
    ht.put("StdDevHPEdata",new Float(hpXYdata.stdDevEdata));
    ht.put("CVHPXdata",new Float(hpXYdata.cvXdata));
    ht.put("CVHPYdata",new Float(hpXYdata.cvYdata));
    ht.put("CVHPEdata",new Float(hpXYdata.cvEdata));
    ht.put("LabelX",hpXYdata.hpLabelX);
    ht.put("LabelY",hpXYdata.hpLabelY);
    ht.put("LabelE",hpXYdata.hpLabelY);
    ht.put("ClassNameHPXset",hpXYdata.hpNameX);
    ht.put("ClassNameHPYset",hpXYdata.hpNameY);
    
    return(ht);
  } /* getHP_XYsetsStatistics */
  
  
  /**
   * getListOfSampleMenuEntries() - get list of sample menu names as appears
   * in the MAExplorer Samples menu.
   * @return list[0:nHP-1] if found,
   *  else null <code>null</code> if getName was not found.
   */
  public final String[] getListOfSampleMenuEntries()
  { /* getListOfSampleMenuEntries */
    if(hps.nHP<=0)
      return(null);
    int j= 0;
    String
      s,
      data[]= new String[hps.nHP];
    MaHybridSample msW;
    for(int i=1;i<=hps.nHP;i++)
    {
      msW= hps.msList[i];
      if(msW==null)
        continue;
      data[j++]= msW.menuTitle;
    }
    
    return(data);
  } /* getListOfSampleMenuEntries */
  
  
  /**
   * getListOfSampleProjectEntries() - get list of sample project names.
   * @return list[0:nHP-1] if found,
   *  else null <code>null</code> if getName was not found.
   */
  public final String[] getListOfSampleProjectEntries()
  { /* getListOfSampleProjectEntries */
    if(hps.nHP<=0)
      return(null);
    int j= 0;
    String
      s,
     data[]= new String[hps.nHP];
    MaHybridSample msW;
    for(int i=1;i<=hps.nHP;i++)
    {
      msW= hps.msList[i];
      if(msW==null)
        continue;
      data[j++]= msW.prjName;
    }
    
    return(data);
  } /* getListOfSampleProjectEntries */
  
  
  /**
   * getListOfSampleStageNames() - get list of sample full stage text names.
   * @return list[0:nHP-1] if found,
   *  else null <code>null</code> if getName was not found.
   */
  public final String[] getListOfSampleStageNames()
  { /* getListOfSampleStageNames */
    if(hps.nHP<=0)
      return(null);
    int j= 0;
    String
      s,
      data[]= new String[hps.nHP];
    MaHybridSample msW;
    
    for(int i=1;i<=hps.nHP;i++)
    {
      msW= hps.msList[i];
      if(msW==null)
        continue;
      data[j++]= msW.fullStageText;
    }
    
    return(data);
  } /* getListOfSampleStageNames */
  
  
  /**
   * getListOfSampleNames() - get list sample database file name (no .quant).
   * @return list[0:nHP-1] if found,
   *  else null <code>null</code> if getName was not found.
   */
  public final String[] getListOfSampleNames()
  { /* getListOfSampleNames */
    if(hps.nHP<=0)
      return(null);
    int j= 0;
    String
      s,
      data[]= new String[hps.nHP];
    MaHybridSample msW;
    
    for(int i=1;i<=hps.nHP;i++)
    {
      msW= hps.msList[i];
      if(msW==null)
        continue;
      data[j++]= msW.hpName;
    }
    
    return(data);
  } /* getListOfSampleNames */
  
  
  /**
   * getGrandMeanCalDNA() - get compute grand mean of calibration DNA
   * @param doRecalcCalibFlag recompute calibration before get data
   * @return value
   */
  public final float getGrandMeanCalDNA(boolean doRecalcCalibFlag)
  { /* getGrandMeanCalDNA */
    if(doRecalcCalibFlag)
    {
      cdb.calAllSamples();
      cdb.calDNAhybSamplesTable();
    }
    return(cdb.grandMeanCalDNA);
  } /* getGrandMeanCalDNA */
  
  
  /**
   * getGrandMeanUseGeneSet() - get compute grand mean of 'Use Gene Set'
   * @param doRecalcCalibFlag recompute calibration before get data
   * @return value
   */
  public final float getGrandMeanUseGeneSet(boolean doRecalcCalibFlag)
  { /* getGrandMeanUseGeneSet */
    if(doRecalcCalibFlag)
    {
      cdb.calAllSamples();
      cdb.calDNAhybSamplesTable();
    }
    return(cdb.grandMeanUseGeneSet);
  } /* getGrandMeanUseGeneSet */
  
  
  /**
   * getSampleVsSampleCorrelationsTable() - get list of
   * Sample Vs Sample correlations table.
   * This is computed for the current data Filter data.
   *
   * <PRE>
   * The Hashtable returned is defined as:
   * <B>Name          - Value</B>
   * "TitleOfTable"     - String title of sample vs sample correlation table
   * "NbrCols"          - int number of columns in the table
   * "NbrRows"          - int number of rows in the table
   * "FieldNames"       - String[0:NbrCols-1] list of field names
   * "TableRowsData"    - String[0:NbrRows-1][0:NbrCols-1] rows of data
   *                      in the table
   * </PRE>
   * @return <code>null</code> if not found or error, else return list.
   */
  public final Hashtable getSampleVsSampleCorrelationsTable()
  { /* getSampleVsSampleCorrelationsTable */
    SimpleTable ccTbl= cdb.calcHP_HPcorrelations();
    Hashtable ht= cvtTable2Hashtable(ccTbl);
    return(ht);
  } /* getSampleVsSampleCorrelationsTable */
  
  
  /**
   * getSampleStatisticsTable() - get list of all Sample statistics table.
   * This is computed for the current data Filter data.
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name          - Value</B>
   * "TitleOfTable"     - String title of the sample statistics table
   * "NbrCols"          - int number of columns in the table
   * "NbrRows"          - int number of rows in the table
   * "FieldNames"       - String[0:NbrCols-1] list of field names
   * "TableRowsData"    - String[0:NbrRows-1][0:NbrCols-1] rows of data in tbl
   * </PRE>
   * @return <code>null</code> if not found or error, else return list.
   */
  public final Hashtable getSampleStatisticsTable()
  { /* getSampleStatisticsTable */
    SimpleTable sampleStatTbl= cdb.createHPmeanAndVarTable();
    Hashtable ht= cvtTable2Hashtable(sampleStatTbl);
    return(ht);
  } /* getSampleStatisticsTable */
  
  
  /* ---------------- Set Sample sets and lists ------------------ */
  
  /**
   * setListOfXsamples() - set int[] list of samples in HP-X 'set' list.
   * The number of samples is the size of the list.
   * Sample index numbers are in the range of [1:maxSamples].
   * @param sampleIdxList list of sample number indices to save in the list.
   * @return value <code>true</code> if succeed.
   */
  public final boolean setListOfXsamples(int sampleIdxList[])
  { /* setListOfXsamples */
    int
      n= sampleIdxList.length,
      j= 0,
      k;
    MaHybridSample
      msW,
      msLst[]= new MaHybridSample[n+1];
    
    for(int i=1; i<=n;i++)
    { /* generate list of Sample objects */
      k= sampleIdxList[i-1];
      msW= hps.msList[i];
      if(msW!=null)
        msLst[++j]= msW;
    }
    
    /* Scatter back into MAExplorer state */
    hps.msListX= msLst;
    hps.nHP_X= j;
    
    return(true);
  } /* setListOfXsamples */
  
  
  /**
   * setListOfYsamples() - set int[] list of samples in HP-Y 'set' list.
   * The number of samples is the size of the list.
   * Sample index numbers are in the range of [1:maxSamples].
   * @param sampleIdxList list of sample number indices to save in the list.
   * @return value <code>true</code> if succeed.
   */
  public final boolean setListOfYsamples(int sampleIdxList[])
  { /* setListOfYsamples */
    int
      n= sampleIdxList.length,
      j= 0,
      k;
    MaHybridSample
      msW,
      msLst[]= new MaHybridSample[n+1];
    
    for(int i=1; i<=n;i++)
    { /* generate list of Sample objects */
      k= sampleIdxList[i-1];
      msW= hps.msList[i];
      if(msW!=null)
        msLst[++j]= msW;
    }
    
    /* Scatter back into MAExplorer state */
    hps.msListY= msLst;
    hps.nHP_Y= j;
    
    return(true);
  } /* setListOfYsamples */
  
  
  /**
   * setListOfEsamples() - set int[] list of samples in HP-E 'list' list.
   * The number of samples is the size of the list.
   * Sample index numbers are in the range of [1:maxSamples].
   * @param sampleIdxList list of sample HP number indices to save in the list.
   * @return value <code>true</code> if succeed.
   */
  public final boolean setListOfEsamples(int sampleIdxList[])
  { /* setListOfEsamples */
    int
      n= sampleIdxList.length,
      j= 0,
      k;
    MaHybridSample
      msW,
      msLst[]= new MaHybridSample[n+1];
    
    for(int i=1; i<=n;i++)
    { /* generate list of Sample objects */
      k= sampleIdxList[i-1];
      msW= hps.msList[i];
      if(msW!=null)
        msLst[++j]= msW;
    }
    
    /* Scatter back into MAExplorer state */
    hps.msListE= msLst;
    hps.nHP_E= j;
    
    return(true);
  } /* setListOfEsamples */
  
  
  /* --- Lists of HP, HP-X, HP-Y, HP-X & HP-Y 'sets' intensity data --- */
 

  /**
   * getRawF1F2DataAndBkgdForSample() - get filtered spot list raw intensity and background data
   * for genes and return it in the arrays pre-specified by the user.
   * The preallocated array size should be the maximum # of genes for
   * filtered data by the specified gene list.
   * If you are using ratio data, then F1 is Cy3 and F2 is Cy5 - unless
   * you have swapped channels (see Swap (Cy3,Cy5) in Samples menu).
   * The will get the current intensity data filtered list of nGene
   * genes into (rawF1data, rawF2data, rawF1bkgd, rawF2bkgd, propList) lists.
   * Sample index numbers are in the range of [1:maxSamples].
   *<P>
   * @param rawF1data[] return raw F1 intensity values
   * @param rawF2data[] return raw F2 intensity values
   * @param rawF1bkgd[] return raw F1 background values
   * @param rawF2bkgd[] return raw F2 background values
   * @param properties[] return property bits for each gene if array not null.
   *   See mjaBase.GENE_IS_xxxxx properties for the definitions.
   * @param midList[] return MIDs for corresponding data if not null
   * @param sampleNbr specifies the HP sample number data to retrieve
   * @param geneListName name of gene list to copy data filter genes into
   *        if it is not null
   * @return the number of genes pushed. If the list length is shorter than
   * the number of genes to be copied, then do not save the remaining
   * genes, but return the total count so the caller can test if they got
   * them all if the return size equals the (nList[] size.
   */
   public final int getRawF1F2DataAndBkgdForSample(float rawF1data[], float rawF2data[],
                                                   float rawF1bkgd[], float rawF2bkgd[],
                                                   int properties[],  int midList[], 
                                                   int sampleNbr, String geneListName)
   { /* getRawF1F2DataAndBkgdForSample */

     if(mae.cfg.maxFIELDS==1 || rawF1data==null || rawF2data==null ||
        rawF1bkgd==null || rawF2bkgd==null)
       return(0);        /* Can't get blood from a stone ... */
 
     MaHybridSample msW= (sampleNbr==0) ? mae.msX : chkGetHP(sampleNbr);
     if(msW==null)
       return(0);
     
     Gene
       mList[]= map.midStaticCL.mList,
       gene;
     float
       rawData1,
       rawData2,
       rawBkgrd1,
       rawBkgrd2,
       bkgrdQ1= msW.bkgrdQ,
       bkgrdQ2= msW.bkgrdQ,
       bkgrdSpotQ[]= msW.bkgrdSpotQ,       
       totQ[]= msW.totQ;
     int
       gid1,
       gid2,
       mid,
       maxListSize= rawF1data.length,  /* assume all lists same size */
       maxGenes= map.maxGenes,
       nList= 0;
     boolean
       doSetPropsFlag= (properties!=null),
       doMidListFlag= (midList!=null),
       hasBkgrdArrayFlag= (bkgrdSpotQ!=null);
 
     for(int k=0;k<maxGenes;k++)
     { /* process all spots */
       if(nList >= (maxListSize-1))
         break;   /* no more room in RTN arrays */

       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;                         /* ignore bogus spots */
       
       mid= gene.mid;
       gid1= map.mid2gid[mid];             /* map mid to gid */
       gid2= map.gidToGangGid[gid1];       /* get the f2 spot gid */ 
        
       if(bkgrdSpotQ==null)
       { /* scalar background value for entire array*/
         rawF1bkgd[nList]= bkgrdQ1;
         rawF2bkgd[nList]= bkgrdQ2;
       }
       else
       { /* background for each spot */
         rawF1bkgd[nList]= bkgrdSpotQ[gid1];
         rawF2bkgd[nList]= bkgrdSpotQ[gid2];
       }

      rawF1data[nList]= totQ[gid1];
      rawF2data[nList]= totQ[gid2];

      if(doSetPropsFlag)
        properties[nList]= gene.properties;  /* see mjaBase.GENE_IS_xxxxx properties */
      if(doMidListFlag)
        midList[nList]= mid;
      
       nList++;                   /* push counter for all genes */
     } /* process all spots */
 
     return(nList);
   } /* getRawF1F2DataAndBkgdForSample */   
   
   
   /**
    * getAllawF1F2DataAndBkgdForSample() - get all spot list raw intensity and background data
    * for genes and return it in the arrays pre-specified by the user.
    * The preallocated array size should be the maximum # of genes in the database.
    * If you are using ratio data, then F1 is Cy3 and F2 is Cy5 - unless
    * you have swapped channels (see Swap (Cy3,Cy5) in Samples menu).
    * The will get the current intensity data filtered list of nGene
    * genes into (rawF1data, rawF2data, rawF1bkgd, rawF2bkgd, properties) lists.
    * Sample index numbers are in the range of [1:maxSamples].
    *<P>
    * @param rawF1data[] return raw F1 intensity values
    * @param rawF2data[] return raw F2 intensity values
    * @param rawF1bkgd[] return raw F1 background values
    * @param rawF2bkgd[] return raw F2 background values
    * @param properties[] return property bits for each gene if array not null. 
    *   See mjaBase.GENE_IS_xxxxx properties for the definitions.
    * @param midList[] return MIDs for corresponding data if not null
    * @param sampleNbr specifies the HP sample number data to retrieve
    * @return the number of genes pushed. If the list length is shorter than
    *   the number of genes to be copied, then do not save the remaining
    *   genes, but return the total count so the caller can test if they got
    *   them all if the return size equals the (nList[] size.
    */
   public final int getAllRawF1F2DataAndBkgdForSample(float rawF1data[], float rawF2data[],
                                                      float rawF1bkgd[], float rawF2bkgd[],
                                                      int properties[], int midList[], 
                                                      int sampleNbr)
   { /* getAllRawF1F2DataAndBkgdForSample */

     if(mae.cfg.maxFIELDS==1 || rawF1data==null || rawF2data==null ||
        rawF1bkgd==null || rawF2bkgd==null)
       return(0);        /* Can't get blood from a stone ... */
 
     MaHybridSample msW= (sampleNbr==0) ? mae.msX : chkGetHP(sampleNbr);
     if(msW==null)
       return(0);
     
     Gene
       mList[]= map.midStaticCL.mList,
       gene;
     float
       rawData1,
       rawData2,
       rawBkgrd1,
       rawBkgrd2,
       bkgrdQ1= msW.bkgrdQ,
       bkgrdQ2= msW.bkgrdQ,
       bkgrdSpotQ[]= msW.bkgrdSpotQ,       
       totQ[]= msW.totQ;
     int
       gid1,
       gid2,
       mid,
       maxListSize= rawF1data.length,  /* assume all lists same size */
       maxGenes= map.maxGenes,
       nList= 0;
     boolean
       doSetPropsFlag= (properties!=null),
       doMidListFlag= (midList!=null),
       hasBkgrdArrayFlag= (bkgrdSpotQ!=null);
 
     for(int k=0;k<maxGenes;k++)
     { /* process all spots */
       if(nList >= (maxListSize-1))
         break;   /* no more room in RTN arrays */

       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;                         /* ignore bogus spots */
       
       mid= gene.mid;
       gid1= map.mid2gid[mid];             /* map mid to gid */
       gid2= map.gidToGangGid[gid1];       /* get the f2 spot gid */ 
        
       if(bkgrdSpotQ==null)
       { /* scalar background value for entire array*/
         rawF1bkgd[nList]= bkgrdQ1;
         rawF2bkgd[nList]= bkgrdQ2;
       }
       else
       { /* background for each spot */
         rawF1bkgd[nList]= bkgrdSpotQ[gid1];
         rawF2bkgd[nList]= bkgrdSpotQ[gid2];
       }

      rawF1data[nList]= totQ[gid1];
      rawF2data[nList]= totQ[gid2];

      if(doSetPropsFlag)
        properties[nList]= gene.properties;  /* see mjaBase.GENE_IS_xxxxx properties */
      if(doMidListFlag)
        midList[nList]= mid;
      
       nList++;                   /* push counter for all genes */
     } /* process all spots */
 
     return(nList);
   } /* getAllRawF1F2DataAndBkgdForSample */
  
  
  /**
   * getF1F2dataForSample() - get filtered (f1,f2,prop) lists of Field (F1,F2) data
   * for sample in (f1List[], f2List[], propList[]) for <B>FILTERED</B> data by
   * the specified gene list. The f1List[] and f2List[] are intensity data.
   * If you are using ratio data, then F1 is Cy3 and F2 is Cy5 - unless
   * you have swapped channels (see Swap (Cy3,Cy5) in Samples menu).
   * The will get the current intensity data filtered list of nGene
   * genes into (f1List,f2Llist,PropList,GeneList) lists.
   * Note: preallocate maximum size arrays before call.
   * Sample index numbers are in the range of [1:maxSamples].
   *<P>
   * @param f1List RTN: intensity data[0:nGene-1] for F1 sample
   * @param f2List RTN: intensity data[0:nGene-1] for F2 sample
   * @param propList RTN: gene property list[0:nGene-1],
   *        see GENE_xxxx properties. If using propList[], then will set bits
   *        to MJAbase.GENE_IS_FILTERED or MJAbase.GENE_IS_NOT_FILTERED.
   * @param sampleNbr specifies the HP sample number data to retrieve
   * @param geneListName name of gene list to copy data filter genes into 
   *        if it is not null
   * @return nGenes # of genes in f1List[], f2List[], propList[],
   *      else 0 if error or there is no dual channel data available!
   */
  public final int getF1F2dataForSample(float f1List[], float f2List[],
                                        int propList[], int sampleNbr,
                                        String geneListName)
  { /* getF1F2dataForSample */
    if(mae.cfg.maxFIELDS<=1 && !mae.useRatioDataFlag)
      return(0);              /* no dual channel data available! */
    
    int nGenes= 0;
    GeneList cl= lookupGeneListByName(geneListName);
    if(cl==null)
      cl= fc.workingCL;
    
    /* Get HP-X F1, F2 data */
    MaHybridSample ms= (sampleNbr==0) ? mae.msX : chkGetHP(sampleNbr);
    if(ms==null)
      return(0);
    nGenes= ms.getF1F2Data(f1List,f2List, propList, cl, true,
                           false /* useF1F2ratioFlag */, 
                           true /* filterByMLflag */);
    return(nGenes);
  } /* getF1F2dataForSample */  
  
  
  /**
   * getAllF1F2dataForSample() - get all (f1,f2,prop) lists of Field (F1,F2) data
   * for sample in (f1List[], f2List[]) for <B>ALL</B> gene data. 
   * The f1List[] and f2List[] are intensity data.
   * If you are using ratio data, then F1 is Cy3 and F2 is Cy5 - unless
   * you have swapped channels (see Swap (Cy3,Cy5) in Samples menu).
   * The will get the current intensity data filtered list of nGene
   * genes into (f1List,f2Llist) lists.
   * Sample index numbers are in the range of [1:maxSamples].
   *<P>
   * Note: preallocate maximum size arrays before call.
   * @param f1List RTN: intensity data[0:nGene-1] for F1 sample
   * @param f2List RTN: intensity data[0:nGene-1] for F2 sample
   * @param sampleNbr specifies the HP sample number data to retrieve
   * @return nGenes # of genes in f1List[], f2List[], 
   *      else 0 if error or there is no dual channel data available!
   */
  public final int getAllF1F2dataForSample(float f1List[], float f2List[],
                                           int sampleNbr)
  { /* getAllF1F2dataForSample */
    if(mae.cfg.maxFIELDS<=1 && !mae.useRatioDataFlag)
      return(0);              /* no dual channel data available! */
    int nGenes= 0;
    GeneList cl= mae.gct.allGenesCL;    
    
    /* Get HP-X F1, F2 data */
    MaHybridSample ms= (sampleNbr==0) ? mae.msX : chkGetHP(sampleNbr);
    if(ms==null)
      return(0);
    
    /* Get the data with no properties and no filtering. We need to
     * set the props flag to force no filtering, but since the propList[]
     * is null, there is no propList saved.
     */
    nGenes= ms.getF1F2Data(f1List,f2List,
                           null,  /* propList */
                           cl,  /* GeneList */
                           true,  /* setPropsFlag */
                           false, /* useF1F2ratioFlag */
                           false  /* filterByMLflag */ );
    return(nGenes);
  } /* getAllF1F2dataForSample */

  
  /**
   * getHPdataForSample() - get normalized filtered (data,prop) list single sample's data
   * for sample in (dataList[], propList[]) for <B>FILTERED</B> data by the
   * specified gene list. The dataList[] is intensity data.
   * If you are using ratio data, this is the ratio data for each sample
   * i.e. (F1/F2) or (Cy3/Cy5) for the sample. Otherwise is the F1
   * data. Data is corrected for background, and median Ratio correction
   * if applicable and the mode is set in the MAExplorer menus.
   * The will get the current intensity data filtered list of nGene
   * genes into (datalist,PropList,GeneList) lists.
   * Note: preallocate maximum size arrays before call.
   * Sample index numbers are in the range of [1:maxSamples].
   *<P>
   * @param dataList RTN: intensity data[0:nGene-1] for sample if succeed
   * @param propList RTN: gene property list[0:nGene-1] if succeed.
   *        See GENE_xxxx properties. If using propList[], then will set bits
   *        to MJAbase.GENE_IS_FILTERED or MJAbase.GENE_IS_NOT_FILTERED.
   * @param sampleNbr specifies the HP sample number data to retrieve
   * @param geneListName name of gene list to copy data filter genes into.
   *        If the name is null, use the the set of ALL GENES gene list.
   * @return nGenes # of genes in dataList[] and propList[],
   *        if the geneListName is not found, use the data filter,
   *        else 0 if error. If the list length is shorter than
   *        the # of genes to be copied, then do not save the remaining
   *        genes, but return the total count so the caller can test if
   *        they got them all if the return size equals the dataList[])
   *        size.
   */
  public final int getHPdataForSample(float dataList[], int propList[],
                                      int sampleNbr, String geneListName)
  { /* getHPdataForSample */
    int nGenes= 0;
    GeneList cl= lookupGeneListByName(geneListName);
    if(cl==null)
      cl= mae.gct.allGenesCL;
    
    /* Get HP-X F1, F2 data */
    MaHybridSample msW= (sampleNbr==0) ? mae.msX : chkGetHP(sampleNbr);
    if(msW==null)
      return(0);
    boolean old_useHPxySetDataFlag= mae.useHPxySetDataFlag;
    mae.useHPxySetDataFlag= false; /* disable so get single sample data not set*/
    nGenes= mae.cdb.getNormHP_XandYdata(dataList, null /* listY[] */, 
                                        propList, cl,
                                        msW, null /* msY */,
                                        true, /* setPropsFlag */
                                        true  /* filterByMLflag */);
    mae.useHPxySetDataFlag= old_useHPxySetDataFlag; /* restore it */
    return(nGenes);
  } /* getHPdataForSample */  
  
  
  /**
   * getAllHPdataForSample() - get all normalized (data,prop) list single sample's data for
   * sample in (dataList[]) for <B>ALL</B> genes. The dataList[] is intensity data.
   * If you are using ratio data, this is the ratio data for each sample
   * i.e. (F1/F2) or (Cy3/Cy5) for the sample. Otherwise is the F1
   * data. Data is corrected for background, and median Ratio correction
   * if applicable and the mode is set in the MAExplorer menus.
   * The will get the current intensity data filtered list of nGene
   * genes into (datalist) lists.
   * Sample index numbers are in the range of [1:maxSamples].
   *<P>
   * Note: preallocate maximum size arrays before call.
   * @param dataList RTN: intensity data[0:nGene-1] for sample if succeed
   * @param sampleNbr specifies the HP sample number data to retrieve
   * @return nGenes # of genes in dataList[]. If the list length is shorter than
   *   the # of genes to be copied, then do not save the remaining
   *   genes, but return the total count so the caller can test if
   *   they got them all if the return size equals the dataList[])
   *   size.
   */
  public final int getAllHPdataForSample(float dataList[], int sampleNbr)
  { /* getAllHPdataForSample */
    int nGenes= 0;
    
    /* Get HP-X F1, F2 data */
    MaHybridSample msW= (sampleNbr==0) ? mae.msX : chkGetHP(sampleNbr);
    if(msW==null)
      return(0);   

    GeneList cl= mae.gct.allGenesCL;    
    
    /* Get the data with no properties and no filtering. We need to
     * set the props flag to force no filtering, but since the propList[]
     * is null, there is no propList saved.
     */
    if(mae.NEVER)
      nGenes= msW.getF1F2Data(dataList, null,
                              null,  /* propList */
                              cl,  /* GeneList */
                              true,  /* setPropsFlag */
                              mae.useRatioDataFlag, 
                              false  /* filterByMLflag */);
    nGenes= mae.cdb.getNormHP_XandYdata(dataList, null /* listY[] */, 
                                        null,  /* propList */
                                        cl,  /* GeneList */
                                        msW, null /* msY */,
                                        true, /* setPropsFlag */
                                        false  /* filterByMLflag */);
    return(nGenes);
  } /* getAllHPdataForSample */
   
  
  /**
   * getHP_XandYsamplesData() - get filtered (X,Y,prop) lists of X and Y samples data
   * for single samples in (Xdata[],Ydata[], propList[]) for <B>FILTERED</B> data
   * by the specified gene list. The Xdata[] and Ydata[] are normalized intensity data.
   * If you are using ratio data, then sample intensity is the ratio data
   * for each sample i.e. (Cy3X/Cy5X) for HP-X and (Cy3Y/Cy5Y) for HP-Y.
   * The will get the current data filtered list of nGene genes into
   * (Xlist,Ylist,PropList).<P>
   * If your are using (F1,F2) or (Cy3,Cy5) data and sampleNbrX==sampleNbrY,
   * then it will get the F1 (Cy3) channel in xData[] and the F2 (Cy5) data
   * in the yData[] arrays.
   *<P>
   * Note: preallocate maximum size arrays before call.
   * Sample index numbers are in the range of [1:maxSamples].
   *<P>
   * @param xList RTN: data[0:nGene-1] for X sample if succeed
   * @param yList RTN: data[0:nGene-1] for Y sample if succeed
   * @param propList RTN: gene property list[0:nGene-1] if succeed.
   *        See GENE_xxxx properties. If using propList[], then will set bits
   *        to MJAbase.GENE_IS_FILTERED or MJAbase.GENE_IS_NOT_FILTERED.
   * @param sampleNbrX specifies the HP-X sample number data to retrieve
   * @param sampleNbrY specifies the HP-Y sample number data to retrieve
   * @param geneListName name of gene list to copy data filter genes into.
   * @return nGenes # of genes in xList[], yList[], and propList[],
   *         else 0 if error. If the list length is shorter than
   *        the # of genes to be copied, then do not save the remaining
   *        genes, but return the total count so the caller can test if
   *        they got them all if the return size equals the (xList[],
   *        yList[]) sizes.
   */
  public final int getHP_XandYsamplesData(float xList[], float yList[],
                                          int propList[], int sampleNbrX, 
                                          int sampleNbrY, String geneListName)
  { /* getHP_XandYsamplesData */
    int nGenes= 0;
    GeneList cl= lookupGeneListByName(geneListName);
    if(cl==null)
      cl= mae.gct.allGenesCL;
      //cl= fc.workingCL;
    
    /* get HP-X and HP-Y data */
    MaHybridSample
      msX= (sampleNbrX==0) ? mae.msX : chkGetHP(sampleNbrX),
      msY= (sampleNbrY==0) ? mae.msY : chkGetHP(sampleNbrY);
    if(msX==null || msY==null)
      return(0);
    
    nGenes= cdb.getNormHP_XandYdata(xList, yList, propList, cl,
                                    msX, msY,
                                    true, /* setPropsFlag */
                                    true  /* filterByMLflag */ );
    
    return(nGenes);
  } /* getHP_XandYsamplesData */
  
 
  /**
   * getAllRawHPdataForSample() - get all raw (data,prop) list single sample's 
   * intensity data for single samples in (Xdata[],Ydata[], propList[]) for <B>FILTERED</B> data
   * by the specified gene list. The Xdata[] and Ydata[] are raw intensity data.
   * If you are using ratio data, then sample intensity is the raw ratio data
   * for each sample i.e. (Cy3X/Cy5X) for HP-X and (Cy3Y/Cy5Y) for HP-Y.
   * The will get the current data filtered list of nGene genes into
   * (xData, yData, xBkgr, yBkgd,PropList). <P>
   * If sampleNbrY==-1, then get (Cy3 into xData, Cy5 into yData).<P>
   * If your are using (F1,F2) or (Cy3,Cy5) data and sampleNbrX==sampleNbrY,
   * then it will get the F1 (Cy3) channel in xData[] and the F2 (Cy5) data
   * in the yData[] arrays.
   *<P>
   * Note: preallocate maximum size arrays before call.
   * Sample index numbers are in the range of [1:maxSamples].
   *<P>
   * @param xData RTN: X data[0:nGene-1] for X sample if succeed
   * @param yData RTN: opt. Y data[0:nGene-1] for Y sample if succeed
   * @param xBkgd RTN: opt. X bkgrd data[0:nGene-1] for X sample if succeed 
   * @param yBkgd RTN: opt. Y bkgrd data[0:nGene-1] for Y sample if succeed
   * @param propList RTN: gene property list[0:nGene-1] if succeed.
   *        See GENE_xxxx properties. If using propList[], then will set bits
   *        to MJAbase.GENE_IS_FILTERED or MJAbase.GENE_IS_NOT_FILTERED.
   * @param sampleNbrX specifies the HP-X sample number data to retrieve
   * @param sampleNbrY specifies the HP-Y sample number data to retrieve
   * @param geneListName name of gene list to copy data filter genes into.
   * @return nGenes # of genes in xData[], yData[], xBkgd[], yBkgd[], and propList[],
   *         else 0 if error. If the list length is shorter than
   *        the # of genes to be copied, then do not save the remaining
   *        genes, but return the total count so the caller can test if
   *        they got them all if the return size equals the (xList[],
   *        yList[]) sizes.
   */
  public final int getRawHP_XandYsamplesData(float xData[], float yData[], 
                                             float xBkgd[], float yBkgd[],
                                             int propList[], int sampleNbrX, 
                                             int sampleNbrY, String geneListName)
  { /* getRawHP_XandYsamplesData */
    int nGenes= 0;
    GeneList cl= lookupGeneListByName(geneListName);
    if(cl==null)
      cl= mae.gct.allGenesCL;
      //cl= fc.workingCL;
    
    /* get HP-X and HP-Y data */
    MaHybridSample
      msX= (sampleNbrX==0) ? mae.msX : chkGetHP(sampleNbrX),
      msY= (sampleNbrY==0) ? mae.msY : chkGetHP(sampleNbrY);
    if(sampleNbrY==-1)
      msY= null;  /* get F1 F2 channels into xData and yData */
      
    if(msX==null)
      return(0);
    
    nGenes= cdb.getRawHP_XandYdata(xData, yData, xBkgd, yBkgd, propList, cl,
                                   msX, msY,
                                   true, /* setPropsFlag */
                                   true  /* filterByMLflag */ );
    
    return(nGenes);
  } /* getRawHP_XandYsamplesData */  
   
  
  /**
   * getAllHP_XandYsamplesData() - get all (X,Y,prop) lists of X and Y samples data
   * for single samples in (Xdata[],Ydata[]) for <B>ALL</B> genes. 
   * The Xdata[] and Ydata[] are intensity data.
   * If you are using ratio data, then sample intensity is the ratio data
   * for each sample i.e. (Cy3X/Cy5X) for HP-X and (Cy3Y/Cy5Y) for HP-Y.
   * The will get the current data filtered list of nGene genes into
   * the (Xlist,Ylist) arrays.
   * Sample index numbers are in the range of [1:maxSamples].
   *<P>
   * Note: preallocate maximum size arrays before call.
   * @param xList RTN: data[0:nGene-1] for X sample if succeed
   * @param yList RTN: data[0:nGene-1] for Y sample if succeed
   * @param sampleNbrX specifies the HP-X sample number data to retrieve
   * @param sampleNbrY specifies the HP-Y sample number data to retrieve
   * @return nGenes # of genes in xList[], yList[], else 0 if error. 
   *        If the list length is shorter than the # of genes to be copied,
   *        then do not save the remaining genes, but return the total count 
   *        so the caller can test if they got them all if the return size
   *        equals the (xList[], yList[]) sizes.
   */
  public final int getAllHP_XandYsamplesData(float xList[], float yList[],
                                             int sampleNbrX, int sampleNbrY)
  { /* getAllHP_XandYsamplesData */
    int nGenes= 0;
    
    /* get HP-X and HP-Y data */
    MaHybridSample
      msX= (sampleNbrX==0) ? mae.msX : chkGetHP(sampleNbrX),
      msY= (sampleNbrY==0) ? mae.msY : chkGetHP(sampleNbrY);
    if(msX==null || msY==null)
      return(0);

    GeneList cl= mae.gct.allGenesCL;   
    
    /* Get the data with no properties and no filtering. We need to
     * set the props flag to force no filtering, but since the propList[]
     * is null, there is no propList saved.
     */
    nGenes= cdb.getNormHP_XandYdata(xList, yList, 
                                    null, /* propList*/
                                    cl, /* GeneList */
                                    msX, msY,
                                    true, /* setPropsFlag */
                                    false /* filterByMLflag */);
    
    return(nGenes);
  } /* getAllHP_XandYsamplesData */  
  
  
  /**
   * getHP_XandYsetDataForSample() - get normalized HP-X, -Y 'sets' (X,Y,prop) 
   * data lists for sample in (Xdata[],Ydata[], propList[] for <B>FILTERED</B>
   * data by the specified gene list.
   * The will get the current data filtered list of nGene genes
   * mean 'set' intensity data into (Xlist,Ylist,PropList).
   * Note: preallocate maximum size arrays before call.
   * @param xList RTN: mean intensity data[0:nGene-1] for X sample
   * @param yList RTN: mean intensity data[0:nGene-1] for Y sample
   * @param propList RTN: gene property list[0:nGene-1]
   * @param geneListName name of gene list specifying genes to filter by.
   *        If you specify null, it will use the set of ALL GENES gene list.
   * @return nGenes # of genes in xList[], yList[], propList,
   *        else 0 if error. If the list length is shorter than
   *        the # of genes to be copied, then do not save the remaining
   *        genes, but return the total count so the caller can test if
   *        they got them all if the return size equals the (xList[],
   *        yList[]) sizes.
   */
  public final int getHP_XandYsetDataForSample(float xList[], float yList[],
                                               int propList[], 
                                               String geneListName)
  { /* getHP_XandYsetDataForSample */
    int nGenes= 0;
    GeneList cl= lookupGeneListByName(geneListName);
    if(cl==null)
      cl= mae.gct.allGenesCL;
      //cl= fc.workingCL;
    
    boolean old_useHPxySetDataFlag= mae.useHPxySetDataFlag;
    mae.useHPxySetDataFlag= true;          /* set while get the data */
    
    /* get HP-X 'set' and HP-Y ;'set' data */
    nGenes= cdb.getHP_XandYsetData(xList, yList, propList, cl,  
                                true, /* setPropsFlag */
                                true  /* filterByMLflag */ );
    
    mae.useHPxySetDataFlag= old_useHPxySetDataFlag;  /* restore flag */
    return(nGenes);
  } /* getHP_XandYsetDataForSample */
   
  
  /**
   * getAllHP_XandYsetDataForSample() - get all HP-X, -Y 'sets' (X,Y) 
   * normalized data lists for sample in (Xdata[],Ydata[] for <B>ALL</B> genes.
   * The will get the current data list ofall nGene genes
   * mean 'set' intensity data into (Xlist,Ylist).
   * Note: preallocate maximum size arrays before call.
   * @param xList RTN: mean intensity data[0:nGene-1] for X sample
   * @param yList RTN: mean intensity data[0:nGene-1] for Y sample
   * @return nGenes # of genes in xList[], yList[] else 0 if error. 
   *         If the list length is shorter than
   *         the # of genes to be copied, then do not save the remaining
   *         genes, but return the total count so the caller can test if
   *         they got them all if the return size equals the (xList[],
   *         yList[]) sizes.
   */
  public final int getAllHP_XandYsetDataForSample(float xList[], float yList[])
  { /* getAllHP_XandYsetDataForSample */
    int nGenes= 0;
    boolean old_useHPxySetDataFlag= mae.useHPxySetDataFlag;
    mae.useHPxySetDataFlag= true;          /* set while get the data */
    
    /* get HP-X 'set' and HP-Y 'set' data */
    nGenes= cdb.getHP_XandYsetData(xList, yList, 
                                   null, /* propList */
                                   null, /* GeneList */
                                   true, /* setPropsFlag */
                                   false /* filterByMLflag */ );
    
    mae.useHPxySetDataFlag= old_useHPxySetDataFlag;  /* restore flag */
    return(nGenes);
  } /* getAllHP_XandYsetDataForSample */
  
  
  /**
   * getMeanDataGeneListDataForCondition() - get filtered (mn,SD,prop) list Condition samples' 
   * normalized data for genes in the specified gene list and samples in the condition
   * list and save in (meanData[], stdDevData[], propList[])
   * for data filtered by the specified gene list. The dataList[] is intensity data.
   * If you are using ratio data, this is the ratio data for each sample
   * i.e. (F1/F2) or (Cy3/Cy5) for the sample. Otherwise is the F1
   * data. Data is corrected for background, and median Ratio correction
   * if applicable and the mode is set in the MAExplorer menus.
   * The will get the current intensity data filtered list of nGene
   * genes into (datalist,PropList,GeneList) lists.
   * Note: preallocate maximum size arrays before call.
   * @param meanData RTN: mean intensity data[0:nGene-1] for samples if succeed
   * @param stdDevData RTN: StdDev intensity data[0:nGene-1] for samples if succeed.
   *        If this is null, don't return the StdDev data.
   * @param propList RTN: gene property list[0:nGene-1] if succeed
   *        see GENE_xxxx properties.
   *        If this is null, don't return the properties data.
   * @param condListName specifies the condition list of samples data to retrieve
   * @param geneListName name of gene list to copy data filter genes into.
   *        If the name is null, use the set of ALL GENES gene list.
   * @return nGenes # of genes in dataList[] and propList[],
   *        if the geneListName is not found, use the data filter,
   *        else 0 if error. If the list length is shorter than
   *        the # of genes to be copied, then do not save the remaining
   *        genes, but return the total count so the caller can test if
   *        they got them all if the return size equals the dataList[])
   *        size.
   */
  public final int getMeanDataForCondition(float meanData[], float stdDevData[],
                                           int propList[], String condListName,
                                           String geneListName)
  { /* getMeanDataForCondition */
    GeneList cl= lookupGeneListByName(geneListName);
    if(cl==null)
      cl= mae.gct.allGenesCL;
      //cl= fc.workingCL;
    
    int
      idxCond= cdList.lookupCondListIdxByName(condListName,true /* exact match*/),
      nGenes= cl.length;
    
    if(idxCond==-1)
      return(0);
    Condition cd= cdList.condList[idxCond];
    int nMScond= cd.nMScond;
    boolean setPropsFlag= (propList!=null);
    float
      data,
      sd,
      sumData[]= new float[nGenes],
      sumDataSq[]= new float[nGenes];
    MaHybridSample msW;
    
    for(int i=0;i<nMScond;i++)
    { /* process each sample in condition list */
      /* Get HP-X F1, F2 data */
      msW= cd.msCond[i];
      int nGenesI= msW.getF1F2Data(meanData, null, propList, cl, 
                                   setPropsFlag, /* setPropsFlag */
                                   mae.useRatioDataFlag,
                                   true /* filterByMLflag */ );
      for(int j=0;j<nGenes;j++)
      {
        data= meanData[j];
        sumData[j] += data;
        sumDataSq[j] += data*data;
      }
    } /* process each sample in condition list */
    
    /* Now compute meanData[] */
    if(nMScond>=1)
      for(int j=0;j<nGenes;j++)
        meanData[j]= sumData[j]/nGenes;
    
    /* Now compute stdDevData[] */
    if(nMScond>=2)
      for(int j=0;j<nGenes;j++)
      {
        data= meanData[j];
        sd= (float)Math.sqrt((sumDataSq[j] - nMScond*data*data)/nMScond);
        if(stdDevData!=null)
          stdDevData[j]= sd;
      }
    
    return(nGenes);
  } /* getMeanDataForCondition */
  
  
  /**
   * getAllMeanDataGeneListDataForCondition() - get all (mn,SD,prop) list Condition samples' 
   * normalized data for genes in the specified gene list and samples in the condition 
   * list and save in in (meanData[], stdDevData[])
   * for data filtered by the specified gene list. The dataList[] is intensity data.
   * If you are using ratio data, this is the ratio data for each sample
   * i.e. (F1/F2) or (Cy3/Cy5) for the sample. Otherwise is the F1
   * data. Data is corrected for background, and median Ratio correction
   * if applicable and the mode is set in the MAExplorer menus.
   * The will get the current intensity data filtered list of nGene
   * genes into (datalist) lists.
   * Note: preallocate maximum size arrays before call.
   * @param meanData RTN: mean intensity data[0:nGene-1] for samples if succeed
   * @param stdDevData RTN: StdDev intensity data[0:nGene-1] for samples if succeed
   * @param condListName specifies the condition list of samples data to retrieve
   * @return nGenes # of genes in dataList[] else 0 if error. 
   *        If the list length is shorter than
   *        the # of genes to be copied, then do not save the remaining
   *        genes, but return the total count so the caller can test if
   *        they got them all if the return size equals the dataList[])
   *        size.
   */
  public final int getAllMeanDataForCondition(float meanData[], float stdDevData[],
                                              String condListName)
  { /* getAllMeanDataForCondition */    
    int
      idxCond= cdList.lookupCondListIdxByName(condListName,true /* exact match*/),
      nGenes= 0;
    
    if(idxCond==-1)
      return(0);
    Condition cd= cdList.condList[idxCond];
    int nMScond= cd.nMScond;
    float
      data,
      sumData[]= new float[nGenes],
      sumDataSq[]= new float[nGenes];
    MaHybridSample msW;
    
    for(int i=0;i<nMScond;i++)
    { /* process each sample in condition list */
      /* Get HP-X F1, F2 data */
      msW= cd.msCond[i];
      nGenes= msW.getF1F2Data(meanData, null, 
                              null, /* propList */
                              null, /* filterByMLflag */ 
                              true, /* setPropsFlag */
                              mae.useRatioDataFlag,
                              false /* filterByMLflag */ );
      for(int j=0;j<nGenes;j++)
      {
        data= meanData[j];
        sumData[j] += data;
        sumDataSq[j] += data*data;
      }
    } /* process each sample in condition list */
    
    /* Now compute meanData[] */
    if(nMScond>=1)
      for(int j=0;j<nGenes;j++)
        meanData[j]= sumData[j]/nGenes;
    
    /* Now compute stdDevData[] */
    if(nMScond>=2)
      for(int j=0;j<nGenes;j++)
      {
        data= meanData[j];
        stdDevData[j]= (float)Math.sqrt((sumDataSq[j] - nMScond*data*data)/nMScond);
      }
    
    return(nGenes);
  } /* getAllMeanDataForCondition */
  
  
  
} /* end of class MJAsampleList */

