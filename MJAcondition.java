/** File: MJAcondition.java */
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJAcondition methods and data structures. 
 *
 * This contains methods for manipulating condition lists of samples and 
 * ordered lists of condition lists. Conditions are sets of samples. 
 * You may also have ordered lists of conditions. The latter would be the 
 * case in an ordered list of conditions (eg. cell cycle points) with
 * replicate samples in each condition.
 *<PRE>
 *<B>List of methods available to Plugin-writers</B>
 * --------------- Lists of Condition Lists --------------- 
 * getMaxCondListSize() - get maximum # of samples condition list can hold
 * getMaxNbrSamplesInDB() - get maximum # of samples database list can hold
 * getMaxNbrCondParams() - get maximum # of condition parameters DB may hold
 * getNbrCondLists() - get # of active condition lists 
 * getCondListsNames() - get list of active condition lists names
 * getCondListsSizes() - get list of active condition lists sizes
 * createNewCondList() - create new named condition list
 * isCondList() - tests if this is a name of a condition list
 * removeCondList() - remove named condition list if it exists
 * renameCondList() - rename userListName if it exists to newListName.
 * addSampleToCondList() - add sample to named condition list
 * removeSampleFromCondList() - remove sample from named condition list
 * removeAllSamplesFromCondList() - Remove all samples from cond list.
 * isSampleInCondList() - is sample in named condition list?
 * getCondListLength() - get # of samples in the condition list
 * getSamplesInCondList() - get list of sample numbers in named condition 
 * getSampleNamesInCondList() - get list of sample names for named condition 
 * getAllSampleNamesInDB() - get list of all sample names in the database
 * getSampleNbrByCondNameAndSampleName() - get sample number by sample name & condition name.
 * getListCondListsStr() - get list of Conditions pretty-print string
 * updateListCondLists() - update existing Condition list window if active.
 * popupAllConditionsReport() - popup a report showing list of all conditions
 * popupListConditionReport() - popup a report showing list of samples for condition
 *
 * --------------- get Lists of Ordered Condition Lists ---------------
 * getMaxOrderedCondListSize() - get max # condition lists an ordered cond. list can hold
 * getNbrOrderedCondLists() - get # of active ordered condition lists
 * getOrderedCondListsNames() - get list of active ordered condition lists names
 * getOrderedCondListsSizes() - get list of active ordered condition lists sizes
 * getOrderedCondNamesInCondList() - get a list of all conditions in order cond list
 *
 * --------------- manipulate Lists of Ordered Condition Lists ---------------
 * createNewNamedOrderedCondList() - create new named ordered list of conditions
 * removeNamedOrderedCondList() - remove named ordered list of conditions
 * isConditionInOrderedCondList() - test if condition is in named ordered condition list.
 * getConditionIdxInOrderedCondList() - get condition index in named ordered condition list.   
 * addConditionToOrderedCondList() - add condition list to named ordered condition list.
 * rmvConditionFromOrderedCondList() - remove condition list from named ordered condition list.
 * getNamedOrderedCondListIdx() - lookup named ordered condition list index
 * getConditionsInOrderedCondList() - get list of names conditions in OCL
 * getAllCondNames()- get a list of all condition names.
 * isOrderedCondList() - tests if this is a name of an ordered condition list
 * removeAllCondsFromOrderedCondList() - Remove all conditions from condList
 * setCondInOrderedCondList() - replace existing conditions with new list
 * popupListOCLReport() - popup a report showing list of conds for OCL
 * popupAllOCLsReport() - popup a report showing list of all OCLs
 * getCurOCL() - return the name of the current OCL if it exists
 * setCurOCL() - set the name of the current OCL
 *
 * --------------- ordered conditions parameters -----------------------------
 * getMaxNbrOCLParams() - return max number of Ordered Condition List parameters.
 * getOrderedCondParamNames() - return list of Ordered condition paramter names. 
 * setOclParamNames() - set list of ocl parameter names for all conditions
 * setOclParamValues() - set list of ocl parameter values for specific condition
 *
 * ------------------------- Set Lists of Conditions ----------------
 * setNbrCondLists() - Set # of active condition lists 
 * setCondListsNames() - Set list of active condition lists names
 * setCondListsSizes() - Set list of active condition lists sizes
 * setCondListLength() - Set # of samples in the condition list 
 * setSamplesInCondList() - Set list of sample numbers in named condition.
 * setSampleNamesInCondList() - Set list of sample names in named condition.
 * getCurCondition() - return the name of the current condition if it exists
 * setCurCondition() - set the name of the current condition
 *
 * ------------------------- set-theoretic methods ----------------------------
 * union() - compute resultCond= Union (Condition 1, Condition 2)
 * intersection() - compute resultCond= Intersection (Condition 1, Condition 2)
 * difference() - compute resultCond = difference (Condition 1 - Condition 2)
 *
 * ------------------------- get condition parameter methods ------------------
 * getNbrCondParams() - get the number of condition parameters
 * getCondParamNames() - get list of condition parameter names for all conditions
 * getOrderedCondParamValues() - return list of Ordered condition paramter values. 
 *
 * ------------------------- set condition parameter methods ------------------ 
 * setCondParamNames() - set list of condition parameter names for all conditions
 * setCondParamValues() - set list of parameter values for specific condition
 * 
 *<PRE> 
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
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.57 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *<P>
 */
 
public class MJAcondition extends MJAbase
{
  private final boolean DBUG_FLAG= true;
 
  /**
   * MJAcondition() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAcondition()
  { /* MJAcondition */
     MJAsample.setSampleNameMethod(MJAsample.NAME_METHOD_DB_FILE_NAME);
  } /* MJAcondition */
  
  
  /* --------------- Lists of Condition Lists --------------- */
  
  /**
   * getMaxCondListSize() - get maximum # of samples condition list can hold
   * @return value
   */
  public final int getMaxCondListSize()
  { return(mae.MAX_CONDITION_LISTS); }
  
  
  /**
   * getMaxNbrSamplesInDB() - get maximum # of samples database list can hold
   * @return value
   */
  public final int getMaxNbrSamplesInDB()
  { return(mae.MAX_HYB_SAMPLES); } 
  
   
  /**
   * getMaxNbrCondParams() - get maximum # of condition parameters DB may hold
   * @return value
   */
  public final int getMaxNbrCondParams()
  { return(mae.MAX_COND_PARAM); } 
   
  
  /**
   * getNbrCondLists() - get # of active condition lists
   * @return value
   */
  public final int getNbrCondLists()
  { return(cdList.nCondLists); }
  
  
  /**
   * getCondListsNames() - get list of active condition lists names
   * @return value, null if no lists.
   */
  public final String[] getCondListsNames()
  {
    if(cdList.nCondLists==0)
      return(null);
   
    String sR[]= new String[cdList.nCondLists];
    for(int i= 0;i<cdList.nCondLists;i++)
      sR[i]= cdList.condNames[i];
    return(sR);
  }
  
  
  /**
   * getCondListsSizes() - get list of active condition lists sizes
   * @return value, null if no lists.
   */
  public final int[] getCondListsSizes()
  {
    if(cdList.nCondLists==0)
      return(null);
    int iR[]= new int[cdList.nCondLists];
    for(int i= 0;i<cdList.nCondLists;i++)
      iR[i]= cdList.condList[i].nMScond;
    return(iR);
  }
  
  
  /**
   * createNewCondList() - create new named condition list
   * @param condName is the name of the new condition list
   * @return false if failed. It returns true if it succeeds
   *         or the condition list already exists.
   */
  public final boolean createNewCondList(String condName)
  { /* createNewCondList */
    MaHybridSample msList[]= null;
    int
      msListLength= 0,
      offset= 0; /* 0 if cond.msCond[] 1 if mae.hps.msList[] */
    Condition cd= new Condition(condName, msList,0,offset);
    if (cd.nMScond==-1)
      return(false);
    else
      return(true);
  } /* createNewCondList */
  
  
  /**
   * isCondList() - tests if this is a name of a condition list
   * @param condName is the name of the condition list
   * @return true if it is.
   */
  public final boolean isCondList(String condName)
  { return(cdList.isInCondList(condName)); } 
  
  
  /**
   * removeCondList() - remove named condition list if it exists
   * and also remove it from any ordered condition lists it
   * may appear in.
   *<P>
   * [TODO]
   *<P>
   * @param condName is the name of the condition list to delete
   * @return false if failed. It returns true if it succeeds
   *         or the condition list already exists.
   */
  public final boolean removeCondList(String condName)
  { return(cdList.rmvCondList(condName, true/* useExactMatch*/, false)); }
  
  
  /**
   * addSampleToCondList() - add sample to named condition list
   * @param sampleNbr is the sample to add to the condition list
   * @param condName is the name of the condition list
   * @return true if succeed, false if problem or the condition does not exist
   */
  public final boolean addSampleToCondList(int sampleNbr, String condName)
  { /* addSampleToCondList */
    int idx= cdList.lookupCondListIdxByName(condName, true /* exact match*/);
    if(idx==-1)
      return(false);
    Condition cd= cdList.condList[idx];
    MaHybridSample ms= chkGetHP(sampleNbr);
    if(ms==null)
      return(false);
    return(cd.addHPtoCondList(ms));
  } /* addSampleToCondList */
  
  
  /**
   * removeSampleFromCondList() - remove sample from named condition list
   * @param sampleNbr is the sample to remove from the condition list
   * @param condName is the name of the condition list
   * @return true if succeed
   */
  public final boolean removeSampleFromCondList(int sampleNbr, String condName)
  { /* removeSampleFromCondList */
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(false);
    Condition cd= cdList.condList[idx];
    MaHybridSample ms= chkGetHP(sampleNbr);
    if(ms==null)
      return(false);
    boolean flag= cd.rmvHPfromCondList(ms);
    return(flag);
  } /* removeSampleFromCondList */
  
    
  /**
   * removeAllSamplesFromCondList() - Remove all samples from cond list.
   * @param condListName name of condition list to remove all samples.
   * @return true if ok, false if not found or problem
   */
  public boolean removeAllSamplesFromCondList(String condListName)                                              
  { /* removeAllSamplesFromCondList */    
    int idx= cdList.lookupCondListIdxByName(condListName,true /* exact match*/);
    if(idx==-1)
      return(false);
    
     /* Remove all samples from the condition list by setting all entries to
      * null and reseting the total count.
      */
    Condition cd= cdList.condList[idx];
    cd.msCond= new MaHybridSample[mae.MAX_HYB_SAMPLES];
    cd.nMScond= 0;
    
    return(true);
  } /* removeAllSamplesFromCondList */
  
  
  /**
   * renameCondList() - rename userListName if it exists to newListName.
   * @param oldConditionName name of existing condition to be renamed
   * @param newConditionName is the new name to call that condition
   * @param useExactMatchOnOldNameFlag will match just first part of old name
   * @return true if successful.
   */
  public final boolean renameCondList(String oldConditionName,
                                      String newConditionName,
  boolean useExactMatchOnOldNameFlag)
  {
    return(cdList.renameCondList(oldConditionName, newConditionName,
    useExactMatchOnOldNameFlag));
  }
  
  
  /**
   * isSampleInCondList() - is sample in named condition list?
   * @param sampleNbr is the sample to test in the condition list
   * @param condName is the name of the condition list
   * @return true if it is
   */
  public final boolean isSampleInCondList(int sampleNbr, String condName)
  { /* isSampleInCondList */
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(false);
    Condition cd= cdList.condList[idx];
    MaHybridSample ms= chkGetHP(sampleNbr);
    if(ms==null)
      return(false);
    return(cd.isHPinCondList(ms));
  } /* isSampleInCondList */
  
  
  /**
   * getCondListLength() - get # of samples in the condition list
   * @param condName is the name of the condition list
   * @return # of samples in condition list, -1 if failed.
   */
  public final int getCondListLength(String condName)
  { /* getCondListLength */
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(-1);
    Condition cd= cdList.condList[idx];
    return(cd.nMScond);
  } /* getCondListLength */
  
  
  /**
   * getSamplesInCondList() - get list of sample numbers in named condition.
   * NOTE: for N samples, the values are in the range of [1:N], not [0:N-1].
   * @param condName is the name of the condition list
   * @return list of sample numbers if succeed, else null
   */
  public final int[] getSamplesInCondList(String condName)
  { /* getSamplesInCondList */
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(null);
    Condition cd= cdList.condList[idx];
    MaHybridSample msList[]= cd.getHPlist();
    if(msList==null)
      return(null);
    int
      nSamples= cd.nMScond,
      sampleNbrList[]= new int[nSamples];
    for(int i=1;i<=nSamples;i++)
      sampleNbrList[i-1]= lookupHPsampleNbr(msList[i]);
    
    return(sampleNbrList);
  } /* getSamplesInCondList */
  
  
  /**
   * getSampleNamesInCondList() - get list of sample names in named condition.
   * @param condName is the name of the condition list
   * @return list of sample names if succeed, else null
   */
  public final String[] getSampleNamesInCondList(String condName)
  { /* getSampleNamesInCondList */
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(null);
    Condition cd= cdList.condList[idx];
    MaHybridSample msList[]= cd.getHPlist();
    if(msList==null)
      return(null);
    int nSamples= cd.nMScond;
    String sampleNbrList[]= new String[nSamples];
    for(int i=1;i<=nSamples;i++)
    {
      if(msList[i]==null)// TODO GREG Debugging here
        sampleNbrList[i-1]="NULL value found in getSampleNamesInCondList()";
      else
        sampleNbrList[i-1]= msList[i].hpName;
    }
    return(sampleNbrList);
  } /* getSampleNamesInCondList */
  
  
  /**
   * getAllSampleNamesInDB() - get list of all sample names in the database
   * @return list of sample names if succeed, else null
   */
  public final String[] getAllSampleNamesInDB()
  { /* getAllSampleNamesInDB */
    MaHybridSample msList[]= mae.hps.msList;
    if(msList==null)
      return(null);
    int nSamples= mae.hps.nHP;
    String sampleNbrList[]= new String[nSamples];
    for(int i=1;i<=nSamples;i++)
      sampleNbrList[i-1]= msList[i].hpName;
    
    return(sampleNbrList);
  } /* getAllSampleNamesInDB */
  
  
  /**
   * getSampleNbrByCondNameAndSampleName() - get sample number by sample name and condition name.
   * @param condName is the name of the condition list
   * @param sampleName is the name of the sample
   * @return sample # if found, else -1
   */
  public final int getSampleNbrByCondNameAndSampleName(String condName, String sampleName)
  { /* getSampleNbrByCondNameAndSampleName */
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(-1);
    Condition cd= cdList.condList[idx];
    MaHybridSample msList[]= cd.getHPlist();
    if(msList==null)
      return(-2);
    int nSamples= cd.nMScond;
    String sampleNbrList[]= new String[nSamples];
    for(int i=1;i<=nSamples;i++)
    {
      if(msList[i]!=null && sampleName.equals(msList[i].hpName))
        return(i);
    }
    return(-1);
  } /* getSampleNbrByCondNameAndSampleName */
  
  
  /**
   * getListCondListsStr() - get list of Conditions pretty-print string
   */
  public final String getListCondListsStr()
  { return(cdList.getListCondListsStr()); }
  
  
  /**
   * updateListCondLists() - update existing Condition list window if active.
   */
  public final void updateListCondLists()
  { cdList.updateListCondLists(); }
  
  
  /**
   * popupAllConditionsReport() - popup a report showing list of all conditions
   */
  public final void popupAllConditionsReport()
  { cdList.listCondLists(); }
  
  
  /**
   * popupListConditionReport() - popup a report showing list of samples for condition
   * @param condName is the condition that will be used to generate a report
   */
  public final void popupListConditionReport(String condName)
  { cdList.listCondition(condName); }
   
  
  /* --------------- Lists of Ordered Condition Lists --------------- */
  
  /**
   * getMaxOrderedCondListSize() - get maximum # of condition lists that an
   * ordered condition list can hold
   * @return value
   */
  public final int getMaxOrderedCondListSize()
  { return(cdList.MAX_ORDERED_COND_LISTS); }
  
  
  /**
   * getNbrOrderedCondLists() - get # of active ordered condition lists
   * @return value
   */
  public final int getNbrOrderedCondLists()
  { return(cdList.maxOrderedCondLists); }
  
  
  /**
   * getOrderedCondListsNames() - get list of active ordered condition lists names
   * @return value, null if no lists.
   */
  public final String[] getOrderedCondListsNames()
  {
    if(cdList.maxOrderedCondLists==0)
      return(null);
    String sR[]= new String[cdList.maxOrderedCondLists];
    for(int i= 0;i<cdList.maxOrderedCondLists;i++)
      sR[i]= cdList.orderedCondListName[i];
    return(sR);
  }
  
  
  /**
   * getOrderedCondListsSizes() - get list of active ordered condition lists sizes
   * @return value, null if no lists.
   */
  public final int[] getOrderedCondListsSizes()
  {
    if(cdList.maxOrderedCondLists==0)
      return(null);
    int iR[]= new int[cdList.maxOrderedCondLists];
    for(int i= 0;i<cdList.maxOrderedCondLists;i++)
      iR[i]= cdList.nOrderedCondList[i];
    return(iR);
  }
  
  
  /**
   * getOrderedCondNamesInCondList() - get a list of all condition names in ordered cond list
   * @return value if found, else null
   */
  public final String[] getOrderedCondNamesInCondList(String orderedCondListName)
  { /* getOrderedCondNamesInCondList */
    String condNames[]= null;
    for(int i= 0;i<cdList.maxOrderedCondLists;i++)
    {
      if(cdList.orderedCondListName[i].equals(orderedCondListName))
      {
        Condition cListI[]= cdList.orderedCondList[i];
        int nClistIlth= cdList.nOrderedCondList[i]; 
        condNames= new String[nClistIlth];
        for(int j=0;j<nClistIlth;j++)
          condNames[j]= cListI[j].cName;
        break;
      }
    }
    return(condNames);
  } /* getOrderedCondNamesInCondList */
  
  
  /**
   * createNewNamedOrderedCondList() - create new named ordered condition list
   * of conditions if it does not already exist - in which case do nothing.
   * @param oCondListName is the name of the ordered condition list
   * @return true if created new ordered condList, false if list already exists.
   */
  public final boolean createNewNamedOrderedCondList(String oCondListName)
  { return(cdList.createNewNamedOrderedCondList(oCondListName)); }
  
  
  /**
   * removeNamedOrderedCondList() - remove existing named ordered list of
   * conditions the ordered list exists.
   * @param oCondListName is the name of the ordered condition list
   * @return true if removed named condList, false if does not exist.
   */
  public final boolean removeNamedOrderedCondList(String oCondListName)
  { return(cdList.removeNamedOrderedCondList(oCondListName)); }
  
  
   /**
   * isConditionInOrderedCondList() - test if condition is in named ordered condition list.
   * @param cdName is condition to test
   * @param oCondListName is the name of the ordered condition list
   * @return true if the condition is in the ordered condition list
   */
  public final boolean isConditionInOrderedCondList(String condName, String oCondListName)
  { /* isConditionInOrderedCondList */  
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(false);
    Condition cd= cdList.condList[idx];
    return(cdList.isConditionInOrderedCondList(cd, oCondListName));    
  } /* isConditionInOrderedCondList */
  
  
  /**
   * getConditionIdxInOrderedCondList() - get condition index in named ordered condition list.
   * @param cdName is condition to test
   * @param oCondListName is the name of the ordered condition list
   * @return index if found, else -1
   */
   public final int getConditionIdxInOrderedCondList(String cdName, String oCondListName)
  { /* getConditionIdxInOrderedCondList */
    int idx= cdList.lookupCondListIdxByName(cdName,true /* exact match*/);
    if(idx==-1)
      return(-1);
    Condition cd= cdList.condList[idx];
    return(cdList.getConditionIdxInOrderedCondList(cd, oCondListName));    
  } /* getConditionIdxInOrderedCondList */ 
   
   
  /**
   * addConditionToOrderedCondList() - add condition to named ordered condition list.
   * The ordered condition list is a list of unique conditions.
   * If the ordered condition list or condition does not exist, then fail. 
   * The list must be created (or removed) separately.
   * @param cdName is condition to add
   * @param oCondListName is the name of the ordered condition list
   * @return true if successful
   * @see #addConditionToOrderedCondList
   */
  public final boolean addConditionToOrderedCondList(String condName,
                                                     String oCondListName)
  { /* addConditionToOrderedCondList */
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(false);
    Condition cd= cdList.condList[idx];
    boolean flag= cdList.addConditionToOrderedCondList(cd,oCondListName);
    return(flag);
  }  /* addConditionToOrderedCondList */
  
  
  /**
   * rmvConditionFromOrderedCondList() -  remove condition from named ordered condition list.
   * The ordered condition list is a list of conditions. 
   * If the ordered condition list or condition does not exist, then fail. 
   * The list must be created (or removed) separately.
   * @param cdName is condition list to remove
   * @param oCondListName is the name of the ordered condition list
   * @return true if condition list removed from the ordered condition list.
   * @see Condition#lookupCondListIdxByName
   * @see Condition#rmvConditionFromOrderedCondList
   */
  public final boolean rmvConditionFromOrderedCondList(String condName,
                                                       String oCondListName)
  { 
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(false);
    Condition cd= cdList.condList[idx];
    boolean flag= cdList.rmvConditionFromOrderedCondList(cd,oCondListName);
    return(flag);
  }
  
  
  /**
   * getNamedOrderedCondListIdx() - get named ordered condition list index
   * @param oCondListName is the name of the ordered condition list
   * @return index if found, else -1
   */
  public final int getNamedOrderedCondListIdx(String oCondListName)
  { return(cdList.lookupNamedOrderedCondListIdx(oCondListName)); }
  
  
  /**
   * getConditionsInOrderedCondList() - get list of names conditions in OCL
   * @param oCondListName is the name of the ordered condition list
   * @return list of conditions in OCL if found, else null
   */
  public final String[] getConditionsInOrderedCondList(String oCondListName)
  { /* getConditionsInOrderedCondList */
    int oclIdx= cdList.lookupNamedOrderedCondListIdx(oCondListName); 
    if(oclIdx==-1)
      return(null);
    
    Condition cList[]= cdList.orderedCondList[oclIdx]; 
    int nConds= cdList.nOrderedCondList[oclIdx];
    String condNames[]= new String[nConds];
    for(int i=0;i<nConds;i++)
      condNames[i]= cList[i].cName;
      
    return(condNames);
  } /* getConditionsInOrderedCondList */  
  
  
  /**
   * getAllCondNames()- get a list of all condition names.
   * @return List of all condition names
   */
  public final String[] getAllCondNames()
  { /* getAllCondNames */
    
    /* [1] find true size, just do not use max size since it is made extralarge */
    int condTrueSize= 0;
    for(int i=0; i<cdList.MAX_ORDERED_COND_LISTS; i++)
    {
      if(cdList.condNames[i] != null && cdList.condNames[i] != "") 
       condTrueSize++;
    }
    
    /* [2] Save the conds */
    String newCondList[]= new String[condTrueSize];
    for(int i=0; i<condTrueSize; i++)
      newCondList[i]= cdList.condNames[i];
       
    return(newCondList);
   
  } /* getAllCondNames */
  
  
  /**
   * isOrderedCondList() - tests if this is a name of an ordered condition list
   * @param oCondName is the name of the ordered condition list
   * @return true if it is.
   * @see Condition#isOrderedCondList
   */
  public final boolean isOrderedCondList(String condName)
  { return(cdList.isOrderedCondList(condName)); }
  

  /**
   * removeAllCondsFromOrderedCondList() - Remove all conditions from condList
   * @param condListName name of condion list
   * @return true if success, false if problem
   * @see #getOrderedCondNamesInCondList
   * @see #isOrderedCondList
   * @see #rmvConditionFromOrderedCondList
   */
  public final boolean removeAllCondsFromOrderedCondList(String oCondListName)
  {/* removeAllCondsFromOrderedCondList */
    /* [1] Check if ordered cond name valid */
    boolean condListFlag= isOrderedCondList(oCondListName);
    if(!condListFlag)
      return(false);
    
    String condNames[]= getOrderedCondNamesInCondList(oCondListName);
    int nConds= condNames.length;
    
    /* [2] rmv each cond */
    for(int i=0; i < nConds; i++)
      rmvConditionFromOrderedCondList(condNames[i], oCondListName);
    return(true);
  }/* removeAllCondsFromOrderedCondList */
  
  
  /**
   * setCondInOrderedCondList() - replace existing conditions with new list
   * @param oCondListName ordered condition list name
   * @param newConds ordered list of conds 
   * @param nNewCond number of conditions in newConds
   * @return true if success, false if problem
   * @see #addConditionToOrderedCondList
   * @see #isOrderedCondList
   * @see #removeAllCondsFromOrderedCondList
   */
  public final boolean setCondInOrderedCondList(String oCondListName,
                                                String[] newConds, 
                                                int nNewCond)
  {/* setCondInOrderedCondList */    
    /* [1] Check if ordered condition list name valid */
    boolean condListFlag= isOrderedCondList(oCondListName);
    if(!condListFlag)
      return(false);
    
    /* [2] Remove all conditions from ordered condition list */
    removeAllCondsFromOrderedCondList(oCondListName);
    
    /* [3] Add all new conditions to ordered condition list */
    for(int i=0; i < nNewCond; i++) 
    {
      int idx= cdList.lookupCondListIdxByName(newConds[i], true /* exact match */);
      if(idx==-1)
        return(false);
     
     if(addConditionToOrderedCondList(newConds[i], oCondListName)==false)
       return(false);
    }    
    return(true);
  }/* setCondInOrderedCondList */
  
  
  /*
   * popupListOCLReport() - popup a report showing list of conds for ordered cond list
   * @param orderedCondListName name of ocl
   */
    public final void popupListOCLReport(String orderedCondListName)
   {cdList.listOrderedCondition(orderedCondListName);}  
   
  
   /**
    * popupAllOCLsReport() - popup a report showing list of all OCLs
    */
    public final void popupAllOCLsReport()
   {cdList.listAllOrderedConditions();}  
 
 
  /*
   * getCurOCL() - return the name of the current OCL if it exists
   * @return name of current Ordered Condition List if exists, else null
   */
    public final String getCurOCL()
   { /* getCurOCL */
     int idx= cdList.curOCLidx;
     if(idx<0 || idx>(cdList.maxOrderedCondLists-1))
       return(null);
     else 
       return(cdList.orderedCondListName[idx]);
   } /* getCurOCL */
  
  
  /*
   * setCurOCL() - set the name of the current OCL if it exists
   * @param newCurOCLname - name of new current OCL list
   * @return true if succeed, else false if name is not a legal
   *        Ordered Condition List if exists, else null
   */
    public final boolean setCurOCL(String newCurOCLname)
   { /* setCurOCL */
     int idx= cdList.lookupNamedOrderedCondListIdx(newCurOCLname);
     if(idx<0)
       return(false);
     else 
     {
       cdList.curOCLidx= idx;
     }
      
     return(true);
   } /* setCurOCL */
    
  
  /* --------------- ordered conditions parameters ----------------------------- */
  /**
   * getMaxNbrOCLParams() - return max number of Ordered Condition List parameters.
   * @return number of ocl parameters
   */
  public final int getMaxNbrOCLParams()
  { return( mae.cdList.oclNparams); }
  
  
  /**
   * getOrderedCondParamNames() - return list of Ordered condition paramter names. 
   */
  public final String[] getOrderedCondParamNames()
  { return(mae.cdList.oclParamNames); }
  
  
  /**
   * getOrderedCondParamValues() - return list of Ordered condition paramter values. 
   * @param oclName Ordered Condition List name
   * @return return list of Ordered Condition List paramter values
   */
  public final String[] getOrderedCondParamValues(String oclName)
  { /* getOrderedParamValues */
    int idx= cdList.lookupNamedOrderedCondListIdx(oclName);
    if(idx==-1)
      return(null);
    String oclParamValues[];
    if(mae.cdList.oclParamValues==null)
    {
      oclParamValues= new String[mae.cdList.oclNparams];
      for(int i=0; i<mae.cdList.oclNparams;i++)
        oclParamValues[i]= "";/* blank */
    }    
    else
    {
      oclParamValues= new String[mae.cdList.oclNparams];
      for(int x=0; x<mae.cdList.oclNparams; x++)
      {
       oclParamValues[x]= mae.cdList.oclParamValues[idx][x];
      }
    }
    return(oclParamValues);
  } /* getOrderedParamValues */
  
  
  /**
   * setOclParamNames() - set list of ocl parameter names for all conditions.
   * Note: this method will not check whether you have changed the order of names.
   * However, you could change the name if the meaning does not change. 
   * E.g., "State" could be changed to "Disease state".
   * Also, new parameter names may be added and the condition parameter names are
   * extended in the MAExplorer state.<BR>
   * The number of parameters is the size of the newParamNames list.
   * NOTE: the # of parameter values must match the number of parameter names.
   * The names must be set prior to setting the values.
   * @param newParamNames - is the list of new names to save.
   * @return true if succeed in changing the parameter names 
   */
  public final boolean setOclParamNames(String newParamNames[])
  { /* setCondParamNames */
    if(newParamNames==null || newParamNames.length==0)
      return(false);   
    mae.cdList.oclParamNames= newParamNames;
    mae.cdList.oclNparams= newParamNames.length;  /* set the length here */
    return(true);
  } /* setOclParamNames */
 
  
  /**
   * setOclParamValue() - set list of ocl parameter values for specific condition
   * NOTE: the # of parameter values must match the number of parameter names.
   * The names must be set prior to setting the values.
   * @param condName - name of the condition to update its values
   * @param newParamValues - is the list of new parameter values to save.
   * @return true if succeed in changing the parameter values, else failed
   * if the condition was not found or the # of parameter values was different
   * than the previously set parameter names.
   */
  public final boolean setOclParamValue(String oclName, String newParamValues[])
  { /* setOclParamValue */
    if(newParamValues==null || newParamValues.length==0)
      return(false);
    int 
      idx= cdList.lookupNamedOrderedCondListIdx(oclName);
    if(idx==-1 || mae.cdList.oclNparams!=newParamValues.length)
      return(false);
    mae.cdList.oclParamValues[idx]= newParamValues;    
    return(true);
  } /* setOclParamValue */
  
  
  
  /* ------------------------ SET Conditions ------------------------ */
  
  /**
   * setNbrCondLists() - Set # of active condition lists 
   * @return boolean
   */
  public final boolean setNbrCondLists(int nCondListsNew)
    { 
      cdList.nCondLists= nCondListsNew;

     /* TODO, need to check if ok. Not over max */
     return(true);
    }    
    
      
  /**
   * setCondListsNames() - Set list of active condition lists names
   * @return value, null if no lists.
   */
  public final boolean setCondListsNames(String[] activeCondList, int size)
    { 
      if(size == 0 || activeCondList == null)
        return(false);
      cdList.condNames= new String[size];
      cdList.nCondLists= size;
      for(int i= 0; i<size; i++)
         cdList.condNames[i]= activeCondList[i];      
      return(true); 
    }  
           
    
  /**
   * setCondListsSizes() - Set list of active condition lists sizes
   * @return boolean, false if problem
   * @param newListSizes new size of cond list
   */
  public final boolean setCondListsSizes(int[] newListSizes)
    { 
      if(newListSizes == null)
        return(false);
      for(int i= 0; i<cdList.nCondLists; i++)
        cdList.condList[i].nMScond= newListSizes[i];
      return(true); 
    }
       
    
  /**
   * setCondListLength() - Set # of samples in the condition list 
   * @return boolean, false if problem
   * @param condName is the name of the condition list
   */
  public final boolean setCondListLength(int nSamples, String condName)
    { /* setCondListLength */
       int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
       if(idx==-1)
         return(false);
       Condition cd= cdList.condList[idx];
       cd.nMScond= nSamples;
       return(true);
    } /* setCondListLength */

  
  /**
   * setSamplesInCondList() - Set list of sample numbers in named condition.
   * @param condListName is the name of the condition list
   * @param sampleNames is list of sample names to be assigned to the condition
   * @param nNewSamples number of new samples to be set
   * @return boolean, false if problem or the condition does not exist
   */
  public final boolean setSamplesInCondList(String condListName, 
                                            String sampleNames[],
                                            int nNewSamples)
    { /* setSamplesInCondList */ 
      /* [1] Check if condListValid */
      boolean condListFlag= isCondList(condListName);
      if(!condListFlag)
        return(false);
      
      removeAllSamplesFromCondList(condListName);
     
      for(int i=0; i<=nNewSamples; i++) 
      {
        int sampleNbr= mae.mja.mjaSample.lookupSampleName(sampleNames[i],
                                                         condListName);
        if(sampleNbr!=-1)
          addSampleToCondList(sampleNbr, condListName);
      }
     
      return(true);
    } /* setSamplesInCondList */
  
  
   /*
    * getCurCondition() - return the name of the current condition if it exists
    * @return name of current Condition if exists, else null
   */
   public final String getCurCondition()
   { /* getCurCondition */
     int idx= cdList.curCondIdx;
     if(idx<0 || idx>(cdList.nCondLists-1))
       return(null);
     else 
       return(cdList.condNames[idx]);
   } /* getCurCondition */
  
  
  /*
   * setCurCondition() - set the name of the current condition
   * @param newCurCondName - name of new current condition list
   * @return true if succeed, else false if name is not a legal
   *        Condition if exists, else null
   */
   public final boolean setCurCondition(String newCurCondName)
   { /* setCurCondition */
     int idx= cdList.lookupCondListIdxByName(newCurCondName,true);
     if(idx<0)
       return(false);
     else 
     {
       cdList.curCondIdx= idx;
     }
      
     return(true);
   } /* setCurCondition */
  
  
  /* ------------------------- set-theoretic methods -------------------------------------*/

  /**
   * union() - compute resultCond= Union (Condition 1, Condition 2)
   * @param cond1Name name of condition 1
   * @param cond2 name of condition 2
   * @param resultCondName name of computed Union (Conditon 1, Condition 2)
   * @return true if succeed.
   */
   public final boolean union(String cond1Name, String cond2Name, 
                              String resultCondName)
   { /* union */
     int
       idx1= cdList.lookupCondListIdxByName(cond1Name,true /* exact match*/),
       idx2= cdList.lookupCondListIdxByName(cond2Name,true /* exact match*/);
     if(idx1==-1 || idx2==-1)
       return(false);
     Condition
       cond1= cdList.condList[idx1],
       cond2= cdList.condList[idx2],
       resultCond= cdList.union(cond1, cond2, resultCondName);
     return((resultCond!=null));
   } /* union */
    

  /**
   * intersection() - compute resultCond= Intersection (Condition 1, Condition 2)
   * @param cond1Name name of condition 1
   * @param cond2Name name of condition 2
   * @param resultCondName name of computed Union (Conditon 1, Condition 2)
   * @return true if succeed.
   */
  public final boolean intersection(String cond1Name, String cond2Name,
                                    String resultCondName)
  { /* intersection */
    int
      idx1= cdList.lookupCondListIdxByName(cond1Name,true /* exact match*/),
      idx2= cdList.lookupCondListIdxByName(cond2Name,true /* exact match*/);
    if(idx1==-1 || idx2==-1)
      return(false);
    Condition
      cond1= cdList.condList[idx1],
      cond2= cdList.condList[idx2],
      resultCond= cdList.intersection(cond1, cond2, resultCondName);
    return((resultCond!=null));
  } /* intersection */
  
  
  /**
   * difference() - compute resultCond = difference (Condition 1 - Condition 2)
   * @param cond1Name name of condition 1
   * @param cond2Name name of condition 2
   * @param resultCondName name of computed Union (Conditon 1, Condition 2)
   * @return true if succeed.
   */
  public final boolean difference(String cond1Name, String cond2Name,
                                  String resultCondName)
  { /* difference */
    int
      idx1= cdList.lookupCondListIdxByName(cond1Name,true /* exact match*/),
      idx2= cdList.lookupCondListIdxByName(cond2Name,true /* exact match*/);
    if(idx1==-1 || idx2==-1)
      return(false);
    Condition
      cond1= cdList.condList[idx1],
      cond2= cdList.condList[idx2],
      resultCond= cdList.difference(cond1, cond2, resultCondName);
    return((resultCond!=null));
  } /* difference */
  
  
 /* ------------------------- get condition parameter methods ---------------- */
  
  /**
   * getNbrCondParams() - get the number of condition parameters
   * @return true if succeed.
   */
  public final int getNbrCondParams()
  { return( mae.cdList.cfParamNames.length ); }
  
  
  /**
   * getCondParamNames() - get list of condition parameter names for all conditions
   * @return a list of names if succeed, else null.
   */
  public final String[] getCondParamNames()
  { return(mae.cdList.cfParamNames); }
  
  
  /**
   * getCondParamValues() - get list of parameter values for specific condition
   * @param condName name of condition 
   * @return a list of names if succeed, else null.
   */
  public final String[] getCondParamValues(String condName)
  { /* getCondParamValues */
    int idx= cdList.lookupCondListIdxByName(condName,true /* exact match*/);
    if(idx==-1)
      return(null);
     
    String cfParamValues[]= mae.cdList.cfParamValues[idx];
    if(cfParamValues==null)
    {
      cfParamValues= new String[mae.cdList.cfNparams];
      
      for(int i=0; i<mae.cdList.cfNparams;i++)
        cfParamValues[i]= "";
    }
      
    return(cfParamValues);
  } /* getCondParamValues */
    
  
 /* ------------------------- set condition parameter methods ---------------- */  
   
  /**
   * setCondParamNames() - set list of condition parameter names for all conditions.
   * Note: this method will not check whether you have changed the order of names.
   * However, you could change the name if the meaning does not change. 
   * E.g., "State" could be changed to "Disease state".
   * Also, new parameter names may be added and the condition parameter names are
   * extended in the MAExplorer state.<BR>
   * The number of parameters is the size of the newParamNames list.
   * NOTE: the # of parameter values must match the number of parameter names.
   * The names must be set prior to setting the values.
   * @param newParamNames - is the list of new names to save.
   * @return true if succeed in changing the parameter names 
   * @see #setCondParamValues
  */
  public final boolean setCondParamNames(String newParamNames[])
  { /* setCondParamNames */
    if(newParamNames==null || newParamNames.length==0)
      return(false);   
    mae.cdList.cfParamNames= newParamNames;
    mae.cdList.cfNparams= newParamNames.length;  /* set the length here */
    return(true);
  } /* setCondParamNames */
 
  
  /**
   * setCondParamValue() - set list of parameter values for specific condition
   * NOTE: the # of parameter values must match the number of parameter names.
   * The names must be set prior to setting the values.
   * @param condName - name of the condition to update its values
   * @param newParamValues - is the list of new parameter values to save.
   * @return true if succeed in changing the parameter values, else failed
   * if the condition was not found or the # of parameter values was different
   * than the previously set parameter names.
   * @see #setCondParamNames
  */
  public final boolean setCondParamValue(String condName, String newParamValues[])
  { /* setCondParamValue */
    if(newParamValues==null || newParamValues.length==0)
      return(false);
    int 
      idx= cdList.lookupCondListIdxByName(condName, true /* exact match*/);
    if(idx==-1 || mae.cdList.cfNparams!=newParamValues.length)
      return(false);
    mae.cdList.cfParamValues[idx]= newParamValues;    
    return(true);
  } /* setCondParamValue */
  
} /* end of class MJAcondition */

