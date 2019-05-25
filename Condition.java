/* File: Condition.java */

/**
 * This class represents an individual Condition list of hybridized sample entries.
 * It holds the static list database of all active Conditions 
 * as well as data structures for defining ordered lists of Condition lists. 
 * This is useful for creating ordered lists of replicate samples. There are
 * methods for creating, removing, testing, etc. these data structures.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2003/04/28 18:45:16 $   $Revision: 1.30 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see MaHybridSample
 */

class Condition
{
  /** link to global MAExplorer instance */
  private static MAExplorer
    mae;                   

  /* -- meta database of condition lists */
  
  /** maximum number of # of condition lists slots available. */
  final static int
    MAX_COND_LISTS= MAExplorer.MAX_HYB_SAMPLES; 
  /** maximum number of # of ordered condition lists slots available. */
  final static int
    MAX_ORDERED_COND_LISTS= MAExplorer.MAX_ORDERED_COND_LISTS;
  /** # of condition lists */
  static int
    nCondLists= 0;             
  /** [0:nCondLists-1] list of condLists' */
  static Condition
    condList[]= null;          
  /** [0:nCondLists-1] list of names of the conditions */
  static String
    condNames[]= null;     
  
  /** [0:cfNparams-1] list of condition parameter names for CondForm */
  static String
    cfParamNames[];  
  /** [0:nCondLists-1][0:cfNparams-1] list of condition parameter values for CondForm*/
  static String
    cfParamValues[][]; 
  /** # of condition parameters */
  static int
    cfNparams; 

  /** Current condition which was the last condition selected by the
   * PopupConditionChooser. If it is undefined, then it is set to -1.
   * Note: this index is into the condList[] and CondNames[] etc DB.
   */
  static int
    curCondIdx;
  
  /** Current OCL which was the last OCL selected by the
   * PopupOrderedCondChooser. If it is undefined, then it is set to -1.
   * Note: this index is into the orderedCondList[] and orderedCondListName[] etc DB.
   */
  static int
    curOCLidx;
  
  /* -------------------------- */
  /* --- Condition instance --- */  
  /* -------------------------- */
  
  /** name of condition list */
  String
    cName= null;               
  /** [0:nMScond-1] elements of the list */
  MaHybridSample
    msCond[]= null;            
  /** # of entries in the list */
  int
    nMScond= 0;                
 
    
  /* -------------- Lists of ordered lists of conditions ------------ */
  /** Database orderedCondList (OCL) list of all order sample condition lists 
   *     [0:maxOrderedCondLists][1:nConditions] for all active samples. 
   *<PRE>
   *   E.g., if ck are Condition instances, then
   *  OCL[0] = (c1,c2,c3,c4)
   *  OCL[1] = (c5,c6,c7,c8)
   *     . . .
   *  OCL[maxOrderedCondLists-1] = (c9,c10)
   *
   * E.g., the jth instance of orderedCondList[j] is an array instance of 
   *  size nOrderedCondList[j] conditions i.e. 
   *      OCL[j]= new Condition[ nOrderedCondList[j] ];
   *
   * The name of thejth OCL ius  orderedCondListName[j].
   *
   * The initial OCL DB is allocated as an empty 2D array in the Constructor   
   *     orderedCondList= new Condition[MAX_ORDERED_COND_LISTS][];
   *
   * Then, OCL instances are added using createNewNamedOrderedCondList().
   *</PRE>
   */
  static Condition
    orderedCondList[][];    
  /** Names of ordered condition lists [0:maxOrderedCondLists-1] */
  static String
    orderedCondListName[];    
  /** # of conditions in each of the ordered condition lists 
   * [0:maxOrderedCondLists-1]  
   */
  static int
    nOrderedCondList[]; 
  /** max # of ordered condition lists that have been created. */
  static int
    maxOrderedCondLists;     
  
  /** [0:oclNparams-1] list of OCL parameter names for CondForm */
  static String
    oclParamNames[];  
  /** [0:maxOrderedCondLists-1][0:oclNparams-1] list of OCL parameter values for CondForm*/
  static String
    oclParamValues[][]; 
  /** # of ordered condition list parameters */
  static int
    oclNparams; 
    
 
  /**
   * Condition() - constructor to create a new named sample Condition list.
   * Also setup the database the first time it is called.
   * @param maE is the MAExplorer instance
   * @param condName of new condition
   * @see #initCondition
   */
  Condition(MAExplorer maE, String condName, boolean intitalParamFlag)
  { /* Condition */
    mae= maE;
    
    if(condList==null)
    { /* create initial list of conditions */
      /* NOTE: curCondIdx and curOCLidx are -1 is undefined.
       * They are set by Config
       */
      
      condList= new Condition[MAX_COND_LISTS]; 
      condNames= new String[MAX_COND_LISTS]; 
      for(int i=0;i<MAX_COND_LISTS;i++)
        condList[i]= null;         /* make sure it has null entries */
      nCondLists= 0;	             /* # of condition lists  */
     
      /* [NOTE] curOCLidx= -1 indicating NOT-DEFINED is set by Config */
      
      /* Setup the named ordered lists of condition lists. */
      maxOrderedCondLists= 0;
      orderedCondList= new Condition[MAX_ORDERED_COND_LISTS][];
      orderedCondListName= new String[MAX_ORDERED_COND_LISTS];
      nOrderedCondList= new int[MAX_ORDERED_COND_LISTS];
      
      /* The actual parameter names should be read from the Config or .hps
       * state data files. Should read first from the and over write the defaults below. 
       * One problems could be if the file is an older version.
       */
      if(intitalParamFlag) 
      { /* initialize param only once */
       
        if(cfParamNames==null)
        {
          String defParamNames[]= {"Name", "Disease state", "Stage", "Time", "Dose"};
          cfParamNames= defParamNames;
          cfNparams= cfParamNames.length;
          cfParamValues= new String[MAX_COND_LISTS][cfNparams];
        }
        
        if(oclParamNames==null)
        {
          String defOclParamNames[]= {"Name", "Disease state", "Stage", "Time", "Dose"};
          oclParamNames= defOclParamNames;
          oclNparams= oclParamNames.length;
          oclParamValues= new String[MAX_ORDERED_COND_LISTS][oclNparams];
        }
        
      } /* initialize param only once */
      
      
     } /* create initial list of conditions */
    
    initCondition(condName);
  } /* Condition */
  
  
  /**
   * Condition() - constructor to create a new empty named Condition list.
   * @param condName of new condition
   * @see #initCondition
   */
  Condition(String condName)
  { /* Condition */
    initCondition(condName);
  } /* Condition */
  
  
  /**
   * Condition() - constructor to create a new condition with list of samples.
   * @param condName of new condition
   * @param msList is list of samples
   * @param msListLength is # of samples
   * @param offset if the msList starts at [0] or [1], so is 0 or 1.
   * It is 0 if cond.msCond[] 1 if mae.hps.msList[].
   * @see #addCondList
   */
  Condition(String condName, MaHybridSample msList[], int msListLength,
            int offset)
  { /* Condition */
    addCondList(condName, msList, msListLength, offset );
  } /* Condition */
  
  
  /**
   * initCondition() - initialize Condition list to empty - i.e. no samples.
   * @param condName of new condition
   * @see MaHybridSample
   */
  void initCondition(String condName)
  { /* initCondition */    
    cName= null;
    msCond= null;
    nMScond= 0;                 /* Failed */
    
    if(condName==null || condName.length()==0)
      return;                   /* no-op */
    
    /* create a new condition list */
    cName= condName;
    msCond= new MaHybridSample[mae.MAX_HYB_SAMPLES];
    nMScond= 0;
  } /* initCondition */
  
  
  /**
   * isInCondList() - tests if this is the name of a condition list
   * @param condName of condition
   * @return true if it is a condition
   * @see #lookupCondListIdxByName
   */
  static boolean isInCondList(String condListName)
  { /* isInCondList */
    boolean flag= (lookupCondListIdxByName(condListName,false)!=-1);
    return(flag);
  } /* isInCondList */
  
  
  /**
   * getConditionByName() - lookup Condition entry by name
   * @param condListName of condition
   * @return condition corresponding to this name, else null if not a condition
   * @see #lookupCondListIdxByName
   */
  static Condition getConditionByName(String condListName)
  { /* getConditionByName */
    int idx= lookupCondListIdxByName(condListName,false);
    Condition clR= (idx==-1) ? null : condList[idx];
    return(clR);
  } /* getConditionByName */
  
  
  /**
   * lookupCondListIdxByName() - lookup a Condition by name and return the index.
   * if it is a numeric string, then return the number, else
   * search the condList's cName's for a match. If exactFlag is set, then
   * do matching ignoring case.
   * @param userListName is partial name of condition that is unique else number
   * @param exactFlag do exact match else startsWith()
   * @return index of Condition in condList[0:nConditions-1] else -1 if fail
   */
  static int lookupCondListIdxByName(String userListName, boolean exactFlag )
  { /* lookupCondListIdxByName */
    if(userListName==null)
      return(-1);
    
    /* test if it is a number in the proper range */
    int i;
    
    try
    { /* they specified the set by the number of the set */
      i= java.lang.Integer.parseInt(userListName);
      if(i>=1 && i<=nCondLists)
        return(i-1);
      else
        return(-1);
    }
    catch(NumberFormatException e)
    { /* then look up by name */
      int
        foundIt= -1,
        count= 0;
      boolean isExactFlag= false;
      String
        lcUSname= userListName.toLowerCase(),
        clName;
      
      for(i=0;i<nCondLists;i++)
        if(condList[i]!=null)
        { /* check non-null condList entries for name match */
          clName= condList[i].cName.toLowerCase();
          isExactFlag= clName.equals(lcUSname);
          if(isExactFlag || (!exactFlag && clName.startsWith(lcUSname)))
          { /* found it */
            count++;
            foundIt= i;
            if(isExactFlag)
              break;  /* stop search */
          }
        } /* check non-null condList entries for name match */
      
      if(count==1 || isExactFlag)
        return(foundIt);         /* it is unique */
    } /* then look up by name */
    
    return(-1);             /* failed because either not found
     * or it was not unique. */
  } /* lookupCondListIdxByName */
  
  
  /**
   * listCondition() - popup a window listing of all samples in the named Condition lists
   * @param userName name of condition to access
   * @return true if succeed
   * @see ShowStringPopup
   * @see #getConditionByName
   * @see #lookupCondListIdxByName
   */
  boolean listCondition(String userName)
  { /* listCondition*/
    Condition
    cond= getConditionByName(userName);
    if(cond==null)
      return(false);
    
    int idx= lookupCondListIdxByName(userName,false);
    String
      sTitle= "Condition List #"+(idx+1)+" ["+cond.cName+"]",
      header= sTitle+"\n====================================\n",
      db= "Database: "+mae.cfg.dbSubset+"\n",
      date= "Date: "+Util.dateStr()+"\n",
      sR= header+db+date+"\n";      
    
    for(int i=0;i<cond.nMScond;i++)
    {
      MaHybridSample ms= cond.msCond[i];
      sR += "HP sample["+(i+1)+"] "+ms.fullStageText+"\n";
    }
    
    /* Add the Parameter list if it exists */
    if(cfNparams>0)
    { /* add the list of parameter (n,v) pairs */
      sR += "\n";
      for(int j=0;j<cfNparams;j++)
      {
        String sValue= cfParamValues[idx][j];
        if(sValue==null) 
          sValue= "";
        sR += "Parameter["+(j+1)+" "+cfParamNames[j]+
               "] = '"+sValue+"'\n";
      }
    }/* add the list of parameter (n,v) pairs */
    
    ShowStringPopup t= new ShowStringPopup(mae, sR,30,60, 
                                           mae.rptFontSize, sTitle,
                                           0, 0, "ListUserCondition",
                                           PopupRegistry.UNIQUE,
                                           "maeConditions-"+idx+".txt");
    return(true);
  } /* listCondition */
  
  
  /**
   * listCondLists() - popup a window listing of all Condition lists in database
   * @see #getListCondListsStr
   * @see ShowStringPopup
   */
  void listCondLists()
  { /* listCondListst */
    String sR= getListCondListsStr();    
    
    ShowStringPopup t= new ShowStringPopup(mae, sR,25,60, 
                                           mae.rptFontSize,
                                           "User Condition Lists",
                                           0, 0, "UserConditionLists",
                                           PopupRegistry.UNIQUE, 
                                           "maeConditions.txt");
  } /* listCondLists */
  
  
  /**
   * updateListCondLists() - update existing Condition list window, else nop
   * @see PopupRegistry#lookupShowStringPopupInstance
   * @see ShowStringPopup#updateText
   * @see #getListCondListsStr
   */
  void updateListCondLists()
  { /* updateListCondListst */
    ShowStringPopup
      ssp= mae.pur.lookupShowStringPopupInstance("UserConditionLists");
    if(ssp==null)
      return;                /* does not exist, so no-op */
    String sR= getListCondListsStr();
    
    ssp.updateText(sR);
  } /* getListCondLists */
  
  
  /**
   * getListCondListsStr() - get a list of the names of the Condition lists
   * @return string report of list of conditions
   */
  String getListCondListsStr()
  { /* getListCondListsStr */
    String 
      header= "Condition Lists\n"+
              "===============\n",
      db= "Database: "+mae.cfg.dbSubset+"\n",
      date= "Date: "+Util.dateStr()+"\n",
      sR= header+db+date+"\n";      
    
    for(int i=0;i<nCondLists;i++)
      if(condList[i]!=null)
      {
        Condition cond= condList[i];        
        sR += "Condition #"+(i+1)+"   Nbr-samples="+cond.nMScond+
              ", Name ["+cond.cName+"]\n";
      }
      
    /* Add the Parameter list if it exists */
    if(cfNparams>0)
    { /* add the list of parameter names */      
      sR += "\n";
      for(int j=0;j<cfNparams;j++)
      {
        sR += "Parameter #"+(j+1)+" '"+cfParamNames[j]+"'\n";
      }
    }/* add the list of parameter names */
      
    return(sR);
  } /* getListCondListsStr */
  
  
  /**
   * addCondList() - add conditionName Condition list with msList[] data
   * @param conditionName is condition list to add
   * @param msList is the list of samples specify by a MaHybridSample list
   * @param msListLength is the number of samples in the list
   * @param offset 0 if cond.msCond[] 1 if mae.hps.msList[]
   * @return idx !=-1 if added the list.
   * @see #lookupCondListIdxByName
   */
  int addCondList(String conditionName, MaHybridSample msList[],
                  int msListLength, int offset )
  { /* addCondList */
    if(conditionName==null || conditionName.length()==0 ||
       (msList==null && msListLength>0))
      return(-1);
    
    int
      idx= lookupCondListIdxByName(conditionName, true);
    Condition cond= null;
    
    if(idx!=-1)
      cond= condList[idx];
    else if(nCondLists<MAX_COND_LISTS)
    {
      idx= nCondLists;
      cond= new Condition(conditionName);
      condNames[nCondLists]= conditionName;     
      
      condList[nCondLists++]= cond;    /* [NOTE] incrementing # of Condition instances */
    }
    
    if(cond!=null)
    { /* found it - go add it */
      cond.msCond= new MaHybridSample[mae.MAX_HYB_SAMPLES];
      for(int i=0;i<msListLength;i++)
        cond.msCond[i]= msList[i+offset];  /* note: counts [1:n], not [0:n-1] */
      cond.nMScond= msListLength;
    } /* found it */
    
    return(idx);
  } /* addCondList */
  
  
  /**
   * rmvCondList() - remove conditionName from the list of Condition lists if it exists. Also
   * update the parameters to keep both synced.
   * @param conditionName is name of Condition to remove
   * @param useExactMatch of user name else use startsWith()
   * @param rmvCorrespondingParamFlag may not want to rmv them here since kept 
   *         locally in popupchooser
   * @return true if deleted the list.
   * @see #lookupCondListIdxByName
   */
  boolean rmvCondList(String conditionName, boolean useExactMatch,
                      boolean rmvCorrespondingParamFlag)
  { /* rmvCondList */
    int idx= lookupCondListIdxByName(conditionName, useExactMatch);    
    if(idx==-1)
      return(false);
    
    /* found it - go delete it */
    for(int x=idx; x<nCondLists; x++)
    { /* shift list down by 1 from [idx] to [nCondList-1] */
      condList[x]= condList[x+1];
      condNames[x]= condNames[x+1];
      cfParamValues[x]= cfParamValues[x+1];
    }
    
    nCondLists--;
    condList[nCondLists]= null;
    condNames[nCondLists]= null;
    cfParamValues[nCondLists]= null;

    return(true);
  } /* rmvCondList */
  
  
  /**
   * renameCondList() - rename conditionName if it exists to newCondName
   * @param conditionName is old condition name  to rename
   * @param newCondName is the new Condition name to change it to
   * @param useExacMatch of user name else use startsWith()
   * @return true if successful.
   * @see #lookupCondListIdxByName
   */
  boolean renameCondList(String conditionName, String newCondName,
                         boolean useExactMatch)
  { /* renameCondList */
    int idx= lookupCondListIdxByName(conditionName, useExactMatch);    
    if(idx==-1 || newCondName==null)
      return(false);
    
    condList[idx].cName= newCondName;
    condNames[idx]= newCondName;
    
    return(true);
  } /* renameCondList */
  
  
  /**
   * addHPtoCondList() - add Sample to condition list.
   * Only add it to the list if it is not already present in the list.
   * @param ms is the sample to add to this condition list
   * @return true if successful.
   */
  boolean addHPtoCondList(MaHybridSample ms)
  { /* addHPtoCondList */
    for(int i=1;i<=nMScond;i++)
      if(msCond[i]==ms)
        return(false);		/* already in the list */
    
    msCond[nMScond++]= ms;	
    return(true);
  } /* addHPtoCondList */
  
  
  /**
   * rmvHPfromCondList() - remove Sample from condition list
   * @param ms is the sample to remove from this condition list
   * @return true if successful.
   */
  boolean rmvHPfromCondList(MaHybridSample ms)
  { /* rmvHPfromCondList */
    for(int i=1;i<=nMScond;i++)
      if(msCond[i]==ms)
      { /* found it, shorten the list after remove it */
        for(int j=i;j<nMScond;j++)
          msCond[j]= msCond[j+1];
        msCond[nMScond]= null;
        nMScond--;
        return(true);
      }    
    return(false);
  } /* rmvHPfromCondList */
  
  
  /**
   * isHPinCondList() - is Sample in condition list
   * @param ms is the sample to test to see if it is in this condition list
   * @return true if successful.
   */
  boolean isHPinCondList(MaHybridSample ms)
  { /* isHPinCondList */
    for(int i=1;i<=nMScond;i++)
      if(msCond[i]==ms)
        return(true);
    
    return(false);
  } /* isHPinCondList */
  
  
  /**
   * getCondListName() - get Condition list name
   * @return name of this condition list
   */
  String getCondListName()
  { /* getCondListName */
    return(cName);
  } /* getCondListName */
  
  
  /**
   * getCondListLength() - get Condition list length
   * @return length of  this condition list
   */
  int getCondListLength()
  { /* getCondListLength */
    return(nMScond);
  } /* getCondListLength */
  
  
  /**
   * getHPlist() - get sample list for condition [1:nMScond]
   * @return list of MaHybridSample instances in this condition
   */
  MaHybridSample[] getHPlist()
  { /* getHPlist */
    MaHybridSample msListRtn[]= new MaHybridSample[mae.MAX_HYB_SAMPLES];
    
    for(int i=0;i<nMScond;i++)
      msListRtn[i+1]= msCond[i];  /* counts [1:n], not [0:n-1] */
    
    return(msListRtn);
  } /* getHPlist */
  
  
  /**
   * createNewNamedOrderedCondList() - create new named ordered condition list
   * if it does not already exist - in which case do nothing.
   * @param oCondListName is the name of the ordered condition list
   * @return true if created new ordered condList, false if list already exists.
   */
  boolean createNewNamedOrderedCondList(String oCondListName)
  { /* createNewNamedOrderedCondList */    
    int idx= lookupNamedOrderedCondListIdx(oCondListName);
    if(idx!=-1|| oCondListName==null)
      return(false);                     /* already exists or no name */
    
    if(maxOrderedCondLists >= (MAX_ORDERED_COND_LISTS-1))
      return(false);                     /* bad data or exists */ 
    
    /* Make a new named list at [freeNbr] */
    orderedCondList[maxOrderedCondLists]= new Condition[MAX_ORDERED_COND_LISTS];
    orderedCondListName[maxOrderedCondLists]= oCondListName;
    nOrderedCondList[maxOrderedCondLists]= 0;
    maxOrderedCondLists++;        /* added at the end of the list */
    
    return(true);
  } /* createNewNamedOrderedCondList */
  
  
  /**
   * removeNamedOrderedCondList() - remove existing named ordered list of conditions
   * if it exists.
   * @param oCondListName is the name of the ordered condition list
   * @return true if removed named condList, false if does not exist.
   */
  boolean removeNamedOrderedCondList(String oCondListName)
  { /* removeNamedOrderedCondList */
    int idx= lookupNamedOrderedCondListIdx(oCondListName);
    if(idx==-1)
      return(false);                    /* bad data */
    
    /* found it - remove from the list */
    for(int k=idx;k<maxOrderedCondLists;k++)
    {
      orderedCondList[k]= orderedCondList[k+1];
      orderedCondListName[k]= orderedCondListName[k+1];
      nOrderedCondList[k]= nOrderedCondList[k+1];
    }
    
    maxOrderedCondLists--;      /* removed at the end of the list */
    orderedCondList[maxOrderedCondLists]= null;
    orderedCondListName[maxOrderedCondLists]= null;
    return(true);
  } /* removeNamedOrderedCondList */
  
  
  /**
   * isConditionInOrderedCondList() - test if condition is in named ordered condition list.
   * @param cd is condition to test
   * @param oCondListName is the name of the ordered condition list
   * @return true if the condition is in the ordered condition list
   */
  boolean isConditionInOrderedCondList(Condition cd, String oCondListName)
  { /* isConditionInOrderedCondList */  
    int oclNbr= lookupNamedOrderedCondListIdx(oCondListName);
    if(cd==null || oclNbr==-1)
      return(false);       /* bad data */
    
    boolean inListFlag= false;   /* is in the X list already */
    
    Condition ocl[]= orderedCondList[oclNbr];  /* ordered condition list */
        
    int nOCL= nOrderedCondList[oclNbr];
    for(int i=1;i<=nOCL;i++)
      if(ocl[i]==cd)
      { /* test if it is already in the ordered condition list */
        inListFlag= true;
        break;
      }
    
    return(inListFlag);
  } /* isConditionInOrderedCondList */
  
  
  /**
   * isOrderedCondList() - test if is named ordered condition list.
   * @param cd is condition to test
   * @param oCondListName is the name of the ordered condition list
   * @return true if the condition is in the ordered condition list
   */
  boolean isOrderedCondList(String oCondListName)
  { /* isOrderedCondList */  
    int oclNbr= lookupNamedOrderedCondListIdx(oCondListName);
    if(oclNbr==-1)
      return(false);       /* bad data or does not exist */
    else
      return(true);
  } /* isOrderedCondList */
  
  
  /**
   * getConditionIdxInOrderedCondList() - get condition index in named ordered condition list.
   * @param cd is condition to test
   * @param oCondListName is the name of the ordered condition list
   * @return index if found, else -1
   */
  int getConditionIdxInOrderedCondList(Condition cd, String oCondListName)
  { /* getConditionIdxInOrderedCondList */  
    int oclNbr= lookupNamedOrderedCondListIdx(oCondListName);
    if(cd==null || oclNbr==-1)
      return(-1);       /* bad data */
    
    Condition ocl[]= orderedCondList[oclNbr];  /* ordered condition list */        
    int nOCL= nOrderedCondList[oclNbr];
    
    for(int i=1;i<=nOCL;i++)
      if(ocl[i]==cd)
      { /* test if it is already in the ordered condition list */
        return(i);
      }
    
    return(-1);
  } /* getConditionIdxInOrderedCondList */
  
  
  /**
   * addConditionToOrderedCondList() - add condition to named ordered condition list.
   * The ordered condition list is a list of unique conditions.
   * If the ordered condition list or condition does not exist, then fail. 
   * The list must be created (or removed) separately.
   * @param cd is condition to add
   * @param oCondListName is the name of the ordered condition list
   * @return true if successful
   * @see #createNewNamedOrderedCondList
   */
  boolean addConditionToOrderedCondList(Condition cd, String oCondListName)
  { /* addConditionToOrderedCondList */  
    int oclNbr= lookupNamedOrderedCondListIdx(oCondListName);
    if(cd==null || oclNbr==-1)
      return(false);       /* bad data */
    
    Condition ocl[]= orderedCondList[oclNbr];  /* ordered condition list */        
    int nOCL= nOrderedCondList[oclNbr];
    
    boolean inListFlag= isConditionInOrderedCondList(cd, oCondListName);    
    if(!inListFlag)
    { /* add it to list */
      ocl[nOrderedCondList[oclNbr]++]= cd;
      Util.showMsg("Adding '"+ cd.cName + "' to ordered condition list ["+
                   oCondListName+"]");
    }
    
    return(true);
  } /* addConditionToOrderedCondList */
  
  
  /**
   * rmvConditionFromOrderedCondList() - remove condition from named ordered condition list.
   * The ordered condition list is a list of conditions. 
   * If the ordered condition list or condition does not exist, then fail. 
   * The list must be created (or removed) separately.
   * @param cd is condition list to remove
   * @param oCondListName is the name of the ordered condition list
   * @return true if condition list removed from the ordered condition list.
   */
  boolean rmvConditionFromOrderedCondList(Condition cd, String oCondListName)
  { /* rmvConditionFromOrderedCondList */ 
    int oclNbr= lookupNamedOrderedCondListIdx(oCondListName);
    if(cd==null || oclNbr==-1)
      return(false);       /* bad data */
            
    int nOCL= nOrderedCondList[oclNbr];  
    if(nOCL<=0)
      return(false);
    
    boolean inListFlag= isConditionInOrderedCondList(cd, oCondListName);    
    if(!inListFlag)
      return(false);    
    
    Condition ocl[]= orderedCondList[oclNbr];  /* ordered condition list */  
    int cdIdx= getConditionIdxInOrderedCondList(cd, oCondListName);
        
    for(int i=cdIdx;i<(nOCL-1);i++)
    {
      ocl[i]= ocl[i+1];
    }
    nOrderedCondList[oclNbr]--;
    
    Util.showMsg("Removing '"+ cd.cName + "' from ordered condition list ["+
                   oCondListName+"]");
    return(true);
  } /* rmvConditionFromOrderedCondList */
  
  
  /**
   * lookupNamedOrderedCondList() - lookup named ordered condition list
   * @param oCondListName is the name of the ordered condition list
   * @return Condition list else null
   */
  Condition[] lookupNamedOrderedCondList(String oCondListName)
  { /* lookupNamedOrderedCondListIdx */
    int idx= lookupNamedOrderedCondListIdx(oCondListName);
    if(idx!=-1)
      return(orderedCondList[idx]);
    
    return(null);
  } /* lookupNamedOrderedCondList */
  
  
  /**
   * lookupNamedOrderedCondListIdx() - lookup named ordered condition list index
   * @param oCondListName is the name of the ordered condition list
   * @return index if found, else -1
   */
  int lookupNamedOrderedCondListIdx(String oCondListName)
  { /* lookupNamedOrderedCondListIdx */
    if(oCondListName==null)
      return(-1);
    
    for(int i= 0; i<maxOrderedCondLists; i++)
    {
      if(orderedCondListName[i]!=null)
      {
        String str= orderedCondListName[i];
        if(str.equals(oCondListName))
          return(i);
      }
    }
    
    return(-1);
  } /* lookupNamedOrderedCondListIdx */
  
  
   /**
    * returnListOfCondsForOCL() - return list of conditions
    * @param oclName Ordered Condition List name
    * @return list of conditions
    * @see #lookupNamedOrderedCondListIdxByName
    */
  
  String[] returnListOfCondsForOCL(String oclName)
  {
     int idx= lookupNamedOrderedCondListIdx(oclName);
     if(idx==-1)
       return(null);
     
     int oclSize= nOrderedCondList[idx];
     String condList[]= new String[oclSize];
     //Condition condList[] = new Condition[oclSize];
     
     for(int i=0; i<oclSize; i++)
        
       condList[i]= orderedCondList[idx][i].cName;   
     
     return(condList);
  }
  
  
  /**
   * listOrderedCondition() - popup a window listing of all Conditions 
   * in the Ordered Condition list
   * @param userName name of condition to access
   * @return true if succeed
   * @see ShowStringPopup
   * @see #getConditionByName
   * @see #lookupCondListIdxByName
   */
  boolean listOrderedCondition(String oclName)
  { /* listOrderedCondition */  
    int idx= lookupNamedOrderedCondListIdx(oclName);
    if(idx==-1)
       return(false);
    String condList[]= returnListOfCondsForOCL(oclName);
    int oclSize= nOrderedCondList[idx];
   
    String
      sTitle= "Ordered Condition List #"+(idx)+" [" + oclName + "]",
      header= sTitle + "\n====================================\n",
      db= "Database: " + mae.cfg.dbSubset + "\n",
      date= "Date: " + Util.dateStr() + "\n",
      sR= header + db + date + "\n";      
    
    for(int i=0; i < oclSize; i++)
      sR += "Condition["+(i+1)+"] " + condList[i] + "\n";

    /* Add the Parameter list if it exists */
    if(cfNparams>0)
    { /* add the list of parameter (n,v) pairs */
      sR += "\n";
      for(int j=0;j<oclNparams;j++)
      {
        String sValue= oclParamValues[idx][j];
        if(sValue==null) 
          sValue= "";
        sR += "OCL parameter["+(j+1)+" "+oclParamNames[j]+
               "] = '"+sValue+"'\n";
      }
    }/* add the list of parameter (n,v) pairs */
    
    ShowStringPopup t= new ShowStringPopup(mae, sR, 30, 60, 
                                           mae.rptFontSize, sTitle,
                                           0, 0, "OCLcontents",
                                           PopupRegistry.UNIQUE,
                                           "OCL-"+idx+".txt");
    return(true);
  } /* listOrderedCondition */
  
  
  /**
   * listAllOrderedConditions() - popup a report showing list of all OCLs
   * @return true if succeed
   */
  public final boolean listAllOrderedConditions()
  { /* listAllOrderedConditions */
    String
      sTitle= "List of all Ordered Condition Lists",
      header= sTitle + "\n===================================\n",
      db= "Database: " + mae.cfg.dbSubset + "\n",
      date= "Date: " + Util.dateStr() + "\n",
      sR= header + db + date + "\n";      
    
    for(int i=0; i < maxOrderedCondLists; i++)
      sR += "OCL["+(i+1)+"] " + orderedCondListName[i] +
            " with "+nOrderedCondList[i]+" conditions\n";

    /* Add the Parameter list if it exists */
    if(cfNparams>0)
    { /* add the list of OCL parameter (n,v) pairs */
      sR += "\n";
      for(int j=0;j<oclNparams;j++)
      {
        sR += "OCL parameter["+(j+1)+" "+oclParamNames[j]+ "]'\n";
      }
    }/* add the list of parameter (n,v) pairs */
    
    ShowStringPopup t= new ShowStringPopup(mae, sR, 30, 60, 
                                           mae.rptFontSize, sTitle,
                                           0, 0, "ListAllOCLs",
                                           PopupRegistry.UNIQUE,
                                           "allOCLs.txt");
    return(true);
  } /* listAllOrderedConditions */
  
   
  /**
   * union() - compute union of two conditions cond1 and cond2.
   * @param cond1 is the name of the first condition list
   * @param cond2 is the name of the second condition list
   * @return computed condition with newName
   * @see #addCondList
   */
  Condition union(Condition cond1, Condition cond2, String newName)
  { /* union */
    if(cond1==null || cond2==null)
      return(null);
    
    Condition cR= new Condition(mae, newName, false);
    MaHybridSample
      msR,
      ms2,
      msCond1[]= cond1.msCond,
      msCond2[]= cond2.msCond;
    int
      nMScond1= cond1.nMScond,
      nMScond2= cond2.nMScond;
    
    /* First copy msCond1 to cR.msCond */
    cR.nMScond= 0;
    for(int i=0;i<nMScond1;i++)
      cR.msCond[cR.nMScond++]= cond1.msCond[i];
    
    /* Then add any msCond2 elements not in msCond1 */
    for(int i=0;i<nMScond2;i++)
    { /* compute the union */
      boolean foundAlreadyInList= false;
      ms2= msCond2[i];
      
      for(int j=0;j<cR.nMScond;j++)
      {
        msR=cR. msCond[j];
        if(ms2==msR)
        {
          foundAlreadyInList= true;
          break;
        }
      }
      
      if(!foundAlreadyInList)
        cR.msCond[cR.nMScond++]= ms2;
    } /* compute the union */
    
    addCondList(newName,cR.msCond, cR.nMScond, 0);
    
    return(cR);
  } /* union */
  
  
  /**
   * intersection() - compute intersection of two conditions cond1 and cond2.
   * @param cond1 is the name of the first condition list
   * @param cond2 is the name of the second condition list
   * @return computed condition with newName
   * @see #addCondList
   */
  Condition intersection(Condition cond1, Condition cond2, String newName)
  { /* intersection */
    if(cond1==null || cond2==null)
      return(null);
    
    Condition cR= new Condition(mae, newName, false);
    MaHybridSample
      msR,
      ms1,
      ms2,
      msCond1[]= cond1.msCond,
      msCond2[]= cond2.msCond;
    int
      nMScond1= cond1.nMScond,
      nMScond2= cond2.nMScond;
    
    cR.nMScond= 0;
    for(int i=0;i<nMScond1;i++)
    { /* compute the intersection */
      ms1= msCond1[i];
      
      for(int j=0;j<nMScond2;j++)
      {
        ms2= msCond2[j];
        if(ms2==ms1)
        {
          cR.msCond[cR.nMScond++]= ms2;  /* push if in BOTH */
          break;
        }
      }
    } /* compute the intersection */
    
    addCondList(newName,cR.msCond, cR.nMScond, 0);
    
    return(cR);
  } /* intersection */
  
  
  /**
   * difference() - compute difference of two conditions cond1 and cond2.
   * @param cond1 is the name of the first condition list
   * @param cond2 is the name of the second condition list
   * @return computed condition with newName
   * @see #addCondList
   */
  Condition difference(Condition cond1, Condition cond2, String newName)
  { /* difference */
    if(cond1==null || cond2==null)
      return(null);
    
    Condition cR= new Condition(mae, newName, false);
    MaHybridSample
      ms1,
      ms2= null,
      msCond1[]= cond1.msCond,
      msCond2[]= cond2.msCond;
    int
      nMScond1= cond1.nMScond,
      nMScond2= cond2.nMScond;
    
    cR.nMScond= 0;
    for(int i=0;i<nMScond1;i++)
    { /* compute the difference */
      boolean foundItInBothLists= false;
      ms1= msCond1[i];
      
      for(int j=0;j<nMScond2;j++)
      { /* make sure NOT in list 2 */
        ms2= msCond2[j];
        if(ms2==ms1)
        {
          foundItInBothLists= true;
          break;
        }
      }
      
      if(!foundItInBothLists)
        cR.msCond[cR.nMScond++]= ms1;  /* push if in 1 but not 2 */
    } /* compute the union */
    
    addCondList(newName,cR.msCond, cR.nMScond, 0);
    
    return(cR);
  } /* difference */
  
  
  /**
   * cleanup() - cleanup global static allocated variables in this class.
   * If statics are added later to this class, then set them to null here.
   */
  void cleanup()
  { /* cleanup */
    nCondLists= 0;
    condList= null;
    condNames= null;
    maxOrderedCondLists= 0;
    orderedCondList= null;
    orderedCondListName= null;
    nOrderedCondList= null;
  } /* cleanup */
  
} /* end of class Condition */




