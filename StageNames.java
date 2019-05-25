/** File: StageNames.java */

import java.text.*;

/**
 * The class sets up staging names for building menus and sample labels.
 * These sample labels are used in variousplots and reports. 
 * The stage names reflects the structure of 
 *    SamplesDB.txt:Menu_Stage_Name entries which are saved in 
 * menuNames[] and are used later to build the Samples menu trees.
 * We build the mae.hps.topSubMenuNames[] list from these entries if any.
 * The staging data is contained in the SamplesDB.txt Table and the
 * snXXXX[sample#] results are in the mae.snXXX[] arrays.
 *<P>
 * The use of the PopupHPChooser is the preferred method as it is easier to
 * navigate than the menus.
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.8 $ 
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

class StageNames
{
  
  /* [TODO] note this mess needs a major cleanup, reorganization,
   * and generalization since:
   *  1. StageNames are actually developmental stage names, time series,
   *     drug-dose response, etc.
   *  2. Change the name of this class to reflect the new orientation
   *     since all databases are not StageName oriented.
   */

  /* --- lists of data from Samples DB for use in menu building and
   * other operations
   */
  /** List unique menu name entries for various entities in Samples tbl*/
  String 
    menuNames[];                
  /** List unique source name entries for various entities in Samples tbl*/
  String 
    sourceNames[];	
  /** List unique strain name entries for various entities in Samples tbl*/
  String 
    strainNames[]; 	
  /** List unique stage name entries for various entities in Samples tbl*/
  String  
   stageNames[];
  /** List unique probe name entries for various entities in Samples tbl*/
  String 
    probeNames[];

  /** # unique menu name entries in Samples tbl*/
  int	
    nmenuNames; 	        
  /** # unique source name entries in Samples tbl*/
  int	
    nsourceNames; 	
  /** # unique strain name entries in Samples tbl*/
  int	
    nstrainNames;	
  /** # unique stage name entries in Samples tbl*/
  int	
    nstageNames;
  /** # unique probe name entries in Samples tbl*/
  int	
    nprobeNames;
         
  /* --- The following captures the INSTANCEs of the samples table --- */    
  /** list of menus that the entry belongs to */
  StageNames
    HPmnuEntry[];       
  /** # of menu entries */          
  int 
    nHPmnuEntries;              
    
  /* --- The following is a menu INSTANCE --- */  
  /** submenu # correspndg to Source*/
  int
    subMenuNbr;
  /** idx of mae.snHPNames[] */
  int
    hpNbr;
  /** row of membrane table */
  int
    sampleDBrow;
  /** index for menuNames[] */
  int
    menuJ;
  /** index for sourceNames[] */
  int
    sourceJ;
  /** index for strainNames[] */
  int
    strainJ;
  /** index for stageNames[] */
  int
    stageJ;
  /** index for probeNames[] */
  int
    probeJ;
  /** itemLabel to be used in menu */
  String
    stageLabel;                 
  
    
  /**
   * StageNames() - initial global constructor with no data
   * @param mae is MAExplorer instance
   */
  StageNames(MAExplorer mae)
  { /* StageNames */
    /* We will setup the menu list from SamplesDB.txt later. */
    mae.hps.nTopSubMenus= 0;
  } /* StageNames */
  
  
  /**
   * StageNames() - constructor with data
   * @param subMenuNbr is the submenu # correspndg to Source
   * @param hpNbris the idx of mae.snHPNames[]
   * @param sampleDBrow is the row of sample DB table
   * @param menuJ is the index for menuNames[]
   * @param sourceJ is the index for sourceNames[]
   * @param strainJ is the index for strainNames[]
   * @param stageJ is the index for stageNames[]
   * @param probeJ is the index for probeNames[]
   * @param stageLabel is the item label for menu
   */
  StageNames(int subMenuNbr, int hpNbr, int sampleDBrow,
             int menuJ, int sourceJ, int strainJ, int stageJ, int probeJ,
             String stageLabel)
  { /* StageNames */
    this.subMenuNbr= subMenuNbr;
    this.hpNbr= hpNbr;
    this.sampleDBrow= sampleDBrow;
    this.menuJ= menuJ;
    this.sourceJ= sourceJ;
    this.strainJ= strainJ;
    this.stageJ= stageJ;
    this.stageLabel= stageLabel;
  } /* StageNames */
  
  
  /**
   * getMenuTreeFromSamplesTable() - build tree from Samples.txt Table
   *<PRE>
   * The Pull-down menus should look like:
   *  Menu_Stage_Name
   *    Source
   *      Strain
   *        Stage
   *</PRE>
   * @param mae is MAExplorer instance
   * @return true if succeed
   * @see SamplesTable#getUniqueTableFieldData
   * @see StageNames
   * @see Util#findIndexForKey
   * @see #sortMenuEntriesByStage
   */
  boolean getMenuTreeFromSamplesTable(MAExplorer mae)
  { /* getMenuTreeFromSamplesTable */
    SampleSets hps= mae.hps;
    FileIO fio= mae.fio;
    SamplesTable mt= mae.sampDB;
    
    /* [1] Get lists of unique fields from the DB file. */
    if(mt.idxMenu_Stage_Name==-1)
      return(false);  /* There are no menu entries. FORGETABOUTIT! */
    
    menuNames= mt.getUniqueTableFieldData(mt.idxMenu_Stage_Name);
    sourceNames= mt.getUniqueTableFieldData(mt.idxSource);
    strainNames= mt.getUniqueTableFieldData(mt.idxStrain);
    stageNames= mt.getUniqueTableFieldData(mt.idxStage);
    probeNames= mt.getUniqueTableFieldData(mt.idxProbe);
    nmenuNames= menuNames.length;
    
    /* Test if menu tree names exist */
    if(sourceNames==null || strainNames==null ||
    stageNames==null || probeNames==null)
      return(false);
    
    /* Menu tree exists, so use it */
    nsourceNames= sourceNames.length;
    nstrainNames= strainNames.length;
    nstageNames= stageNames.length;
    nprobeNames= probeNames.length;
    
    /* Setup working menu list of what is actually in SamplesDB */
    mae.hps.topSubMenuNames= menuNames;
    if(menuNames!=null)
      mae.hps.nTopSubMenus= menuNames.length;
    else
      mae.hps.nTopSubMenus= 0;
    
    /* [2] Creat entries from the rows of DB table */
    HPmnuEntry= new StageNames[mt.tRows];  /* worst case size */
    
    for (int r=0; r<mt.tRows;r++)
    { /* build the list */
      String
        rowData[]= mt.tData[r],
        fSampleID= rowData[mt.idxSample_ID],
        fStrain= rowData[mt.idxStrain],
        fSource= rowData[mt.idxSource],
        fStage= rowData[mt.idxStage],
        fProbe= rowData[mt.idxProbe],
        fExposure= rowData[mt.idxExposure],
        fhpName= rowData[mt.idxDatabase_File],
        fhpFileID= (mt.idxDatabaseFileID==-1)
                      ? "" : rowData[mt.idxDatabaseFileID],
        fmsName= rowData[mt.idxMenu_Stage_Name],
        fLogin= (mt.idxLogin==-1) ? null : rowData[mt.idxLogin];
      if(fhpName==null)
        break;
      
      /* Lookup the entry indices in the unique names lists */
      int
        hpNbr= Util.findIndexForKey(mae.snHPName, fhpName),
        menuJ= Util.findIndexForKey(menuNames, fmsName),
        sourceJ= Util.findIndexForKey(sourceNames, fSource),
        strainJ= Util.findIndexForKey(strainNames, fStrain),
        stageJ= Util.findIndexForKey(stageNames, fStage),
        probeJ= Util.findIndexForKey(probeNames, fProbe),
        subMenuNbr= 0;     /* default to the first menu */
      
      String stageLabel= "";
      
      /* Lookup the submenu to put the entry in */
      for(int m=0; m<hps.nTopSubMenus; m++)
        if(fmsName.equalsIgnoreCase(hps.topSubMenuNames[m]))
        {
          subMenuNbr= m;
          break;
        }
      
    /* NOTE: sourceIdx and subMenuNbr are different even
     * though they have the same String name!
     * Note(1): only add entries where the .quant file exists
     * Note(2): if the codebase is "http://.../mae/" and
     *          the HP entry "Login" is TRUE do NOT include it.
     * Note(3): Create a stage label which may include the
     *          exposure.
     */
     /*
      if(mae.CONSOLE_FLAG)
      {
        fio.logMsgln("SN-GMTMT MT["+r+"]=" +
                     " fhpName="+ fhpName +
                     " hpNbr=" + hpNbr +
                     " subMenuNbr=" + subMenuNbr);
        
        fio.logMsgln("         MT["+r+"]=" +
                     " menuJ="+menuJ +
                     " sourceJ="+sourceJ +
                     " strainJ="+strainJ +
                     " stageJ="+stageJ +
                     " probeJ="+probeJ);
      }
     */
      
      if(hpNbr!=-1 && menuJ!=-1
         // && sourceJ!=-1 && strainJ!=-1
         // && (!mae.isWorkingMAE ||
         //     (mae.isWorkingMAE && !fLogin.equalsIgnoreCase("TRUE")))
        )
      { /* add entry since all fields exist */
        /* If hpNbr!=-1, then update snHPFullStageText[] */
        stageLabel= (fStage.length()>0) ? (fStage+" ") : "";
        if(fExposure.length()>0 && !fExposure.startsWith("?"))
          stageLabel += " (" + fExposure  +") ";
        if(fhpFileID.length()>0 && fhpName.equalsIgnoreCase(fhpFileID))
          stageLabel += "[" + fSampleID + "-" + fhpName +"]";
        else
          stageLabel += "[" + fhpName + "]";
        
        // mae.snHPName[hpNbr]= fhpName;
        // mae.snImageFile[hpNbr]= "Gifs"+mae.dynFileSeparator+ fhpName + ".gif";
        mae.snHPFullStageText[hpNbr]= stageLabel;
       /*
       if(mae.CONSOLE_FLAG)
         fio.logMsgln("SN-GMTMT.1 MT["+r+"]=" +
                      " hpNbr=" + hpNbr +
                      " snHPFullStageText["+hpNbr+"]="+
                      mae.snHPFullStageText[hpNbr] +
                      " subMenuNbr=" + subMenuNbr);
        */
        StageNames sn= new StageNames(subMenuNbr, hpNbr, sampleDBrow,
                                      menuJ, sourceJ, strainJ,
                                      stageJ, probeJ,
                                      stageLabel);
        HPmnuEntry[nHPmnuEntries++]= sn;
        /*
        if(mae.CONSOLE_FLAG)
          fio.logMsgln("SN-GMTMT.2 sn["+ (nHPmnuEntries-1)+"]=" +
                       ", stageLabel="+stageLabel+
                       ", subMenuNbr=" + subMenuNbr +
                       ", sampleDBrow="+ sn.sampleDBrow +
                       ", menuJ="+sn.menuJ +
                       ", sourceJ="+sn.sourceJ +
                       ", strainJ="+sn.strainJ +
                       ", stageJ="+sn.stageJ +
                       ", probeJ="+sn.probeJ);
        if(mae.CONSOLE_FLAG)
          fio.logMsgln("SN-GMTMT.3 r="+r+", sn["+ (nHPmnuEntries-1)+"]=" +
                       rowData[mt.idxDatabase_File] + ", Stage="+stageLabel);
        */
      } /* add entry */
    } /* build the list */
    
    if(nHPmnuEntries==0)
      return(false);
    
    /* Sort HPmnuEntry[0:nHPmnuEntries-1] by Stage
     * So menus work out right.
     */
    sortMenuEntriesByStage(HPmnuEntry,nHPmnuEntries);
    
    return(true);
  } /* getMenuTreeFromSamplesTable */
  
  
  /**
   * sortMenuEntriesByStage() - sort HPmnuEntry[] by Stage.
   * @param data is list of stagenames
   * @int lth is the number of stage names
   */
  private void sortMenuEntriesByStage(StageNames data[], int lth)
  { /* sortMenuEntriesByStage */
    /* [TODO] extend this to deal with trailing numbers
     * eg. A1, A10, A2 should be sorted as A1, A2, A10.
     * Should be able to use a RuleBasedCollator...
     * [TODO] REWRITE - THIS CODE IS A HOG!
     */
    int
     j,
     n,
     jMin= 0;
    Collator col= Collator.getInstance();
    
    for(int i= 0; i<lth-1; i++)
    { /* bubble sort */
      n= jMin;
      for(j= (lth-2); j>=n; j--)
      { /* compare Source[j] with Source[j+1] & swap if out of order*/
        if(col.compare(data[j].stageLabel, data[j+1].stageLabel) >0)
        { /* swap j and j+1 index */
          StageNames snTmp= data[j];
          data[j]= data[j+1];
          data[j+1]= snTmp;
          jMin= j;
        } /* swap j and j+1 index */
      }
    } /* bubble sort */
  } /* sortMenuEntriesByStage */
  
  
} /* end of class StageNames */

