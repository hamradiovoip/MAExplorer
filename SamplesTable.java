/** File: SamplesTable.java */
import java.util.*;
import java.lang.*;
import java.lang.String;

/**
 * The SamplesTable class is database for the Samples DB Table. 
 * It reads the tab-delimited SamplesDB.txt file from the Config/.
 * It also contains methods to help handle queries on samples in the database.
 * The Samples DB contains descriptive data for all samples - whether they
 * are loaded or not. This lets us then lookup file names for samples that
 * were not initially loaded and then load them. This data also includes
 * (Login, Project) security information as well as Sample_ID and
 * Database_File, and other optional fields.
 *<P> 
 * It reads a subset of the actual table data from FILE/URL.
 * This Samples DB file may consist of the following fields:
 *<PRE>
 *   Sample_ID Source Strain Stage Probe Login Project Contributor
 *   Contrib_Institute Submission_Date Exposure Membrane_Nbr 
 *   Database_File GeneCard_URL Histology_URL Model_URL Comments 
 *   Menu_Stage_Name Membrane_Layout 
 * <P>
 * NOTE: substitute - ("Membrane" is depricated for "Sample" -
 * for now parse it both ways 4-18-00.)
 *         Sample_ID for Membrane_ID  
 *         Sample_Nbr for Membrane_Nbr   
 *         Sample_Layout for Membrane_Layout 
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
 * @author P. Lemkin (NCI), G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Table
 */

class SamplesTable extends Table
{
  /** remap table name */ 
  final private String
    maeTbl= "SamplesTable"; 
  
  /** link to global instance of MAExplorer */
  private MAExplorer 
    mae;                    
   
  /**
   * The following fields were from the original Membranes.txt file.
   * These lookup field indices are used to access the Samples DB.
  */
  int
    idxSample_ID,         
    idxStrain,
    idxSource,
    idxProbe,
    idxStage,
    idxLogin,             /* could be depricated if not used */
    idxProject,
    idxContributor,
    idxContrib_Institute,
    idxSubmission_Date,     
    idxExposure,
    idxSample_Nbr,
    idxDatabase_File,
    idxDatabaseFileID,    /** opt. - for mAdb */
    idxGeneCard_URL,      /** opt. */
    idxHistology_URL,     /** opt. */
    idxModel_URL,         /** opt. */
    idxComments,          /** Extra comments */
    idxMenu_Stage_Name;
    
  /* The following index fields were merged here from MaInfoTable */
  int
    idxFilterType,        /** [DEPRICATE] id of array filter */
    idxFilterType_Descr,  /** more info on array filter */
    idxBGLow,             /** global background - for Res.Gen. data */
    idxBGAvg,    
    idxBGRms;    

  /** set if data was read ok */
  boolean 
    hasValidDataFlag;       
  
    
  /**
   * SamplesTable() - constructor to read Samples DB and set up table.
   * @param mae is instance of MAExplorer
   * @param fileName is file name of the Samples DB
   * @see MAExplorer#logDRYROTerr
   * @see #setupFieldIndexes
   */
  SamplesTable(MAExplorer mae, String fileName)
  { /* SamplesTable */
    /* Read and set up the table from file */
    super(mae, fileName, "Samples Info");
    
    this.trimWhitespace();   /* remove leading & trailing whitespace */
    
    this.mae= mae;
    hasValidDataFlag= false;
    
    /* [1] Test if file exists */
    if(tRows==0)
    { /* can't read table */
      mae.logDRYROTerr("can't read "+fileName);
      return;
    }
    
    /* [2] Parse fields and get idx values */
    setupFieldIndexes();	/* create idx.... values */
    
    /* [2.1] Make sure minimum fields required exist which is the
     * case if the indices are not -1.
     */
    if(idxSample_ID==-1 || idxProject==-1 || idxDatabase_File==-1)
    { /* missing fields in the table - report fields as DRYROT */
      mae.logDRYROTerr("ST.2.1: missing one or more SampleDB file ["+
                       fileName+"] fields\n"+
                       " (Sample_ID, Project, Database_File, Menu_Stage_Name)\n"+
                       " sample_ID="+idxSample_ID+
                       " Project="+idxProject+
                       " Database_File="+idxDatabase_File);
    } /* missing fields in the table - report fields as DRYROT */
    
    hasValidDataFlag= true;     /* set ok */
  } /* SamplesTable */
  
  
  /**
   * setupFieldIndexes() - set up indexes for common fields for speedups
   * @see #lookupFieldIdxAndRemapFieldName
   */
  void setupFieldIndexes()
  { /* setupFieldIndexes */
    /* See if did any table field name mappings */
    /* Generate index files for table data*/
    
    /* Check Alternate names */
    idxSample_ID= lookupFieldIdxAndRemapFieldName(maeTbl,"Membrane_ID");  /* DEPRICATED */
    if(idxSample_ID==-1)
      idxSample_ID= lookupFieldIdxAndRemapFieldName(maeTbl,"Sample_ID");
    
    idxStrain= lookupFieldIdxAndRemapFieldName(maeTbl,"Strain");
    idxSource= lookupFieldIdxAndRemapFieldName(maeTbl,"Source");
    idxProbe= lookupFieldIdxAndRemapFieldName(maeTbl,"Probe");
    idxStage= lookupFieldIdxAndRemapFieldName(maeTbl,"Stage");
    idxLogin= lookupFieldIdxAndRemapFieldName(maeTbl,"Login");
    idxProject= lookupFieldIdxAndRemapFieldName(maeTbl,"Project");
    idxContributor= lookupFieldIdxAndRemapFieldName(maeTbl,"Contributor");
    idxContrib_Institute= lookupFieldIdxAndRemapFieldName(maeTbl,"Contrib_Institute");
    idxSubmission_Date= lookupFieldIdxAndRemapFieldName(maeTbl,"Submission_Date");
    idxExposure= lookupFieldIdxAndRemapFieldName(maeTbl,"Exposure");
    
    /* Check Alternate names */
    idxSample_Nbr= lookupFieldIdxAndRemapFieldName(maeTbl,"Membrane_Nbr"); /* DEPRICATED */
    if(idxSample_Nbr==-1)
      idxSample_Nbr= lookupFieldIdxAndRemapFieldName(maeTbl,"Sample_Nbr");
    
    idxDatabase_File= lookupFieldIdxAndRemapFieldName(maeTbl,"Database_File");
    idxDatabaseFileID= lookupFieldIdxAndRemapFieldName(maeTbl,"DatabaseFileID");
    idxGeneCard_URL= lookupFieldIdxAndRemapFieldName(maeTbl,"GeneCard_URL");
    idxHistology_URL= lookupFieldIdxAndRemapFieldName(maeTbl,"Histology_URL");
    idxModel_URL= lookupFieldIdxAndRemapFieldName(maeTbl,"Model_URL");
    //idxComments= lookupFieldIdxAndRemapFieldName(maeTbl,"Comments");
    
    /* Check Alternate names */
    idxMenu_Stage_Name= lookupFieldIdxAndRemapFieldName(maeTbl,"Menu_Stage_Name");
    if(idxMenu_Stage_Name==-1)
      idxMenu_Stage_Name= lookupFieldIdxAndRemapFieldName(maeTbl,"Menu_Source_Name");
    
    /* Generate index files for additional table data*/
    idxFilterType= lookupFieldIdxAndRemapFieldName(maeTbl,"FilterType");
    idxFilterType_Descr= lookupFieldIdxAndRemapFieldName(maeTbl,"FilterType_Description");
    idxComments= lookupFieldIdxAndRemapFieldName(maeTbl,"Comments");
    //idxResearcher= lookupFieldIdxAndRemapFieldName(maeTbl,"Researcher");
    
    idxBGLow= lookupFieldIdxAndRemapFieldName(maeTbl,"BGLow");
    idxBGAvg= lookupFieldIdxAndRemapFieldName(maeTbl,"BGAvg");
    idxBGRms= lookupFieldIdxAndRemapFieldName(maeTbl,"BGRms");
  } /* setupFieldIndexes */
  
  
  /**
   * getTableFieldDataByHPname() - get data of fieldName of particular HP sample
   * @param fieldName is the name of the field
   * @param hpName is the name of the sample
   * @return data, else null if failed.
   * @see #lookupFieldIdxAndRemapFieldName
   */
  String getTableFieldDataByHPname(String fieldName, String hpName)
  { /* getTableFieldDataByHPname */
    String sR= null;
    int idxField;
    for(int mRow=0; mRow<tRows; mRow++)
      if((tData[mRow][idxDatabase_File]).equals(hpName))
      {
        idxField= lookupFieldIdxAndRemapFieldName(maeTbl,fieldName);
        if(idxField==-1)
          return(null);
        sR= tData[mRow][idxField];
        break;
      }
    
    return(sR);
  } /* getTableFieldDataByHPname */
  
  
  /**
   * getUniqueTableFieldData() - get list from SamplesDB.txt database
   * Sort the array in ASCENDING order.
   * It is indexed from [0 : rtnStr.length()-1].
   * @param fieldIdx is the index of the field (column) to retrieve
   * @return data array, else null if a problem or there is no data.
   * @see SortMAE#sortArray
   */
  String[] getUniqueTableFieldData(int fieldIdx)
  { /* getUniqueTableFieldData */
    if(tRows==0 || fieldIdx==-1)
      return(null);
    
    String msn[]= new String[tRows];
    int
      i,
      nMSN= 0;
    String s;
    boolean foundIt;
    
    for(int r=0;r<tRows;r++)
    { /* read in all rows of data */
      s= tData[r][fieldIdx];
      
      if(s!=null && s.length()>0)
      { /* push if unique */
        foundIt= false;
        for(i=0;i<nMSN;i++)
          if(msn[i].equals(s))
          { /* found it */
            foundIt= true;
            break;
          }
        if(!foundIt)
          msn[nMSN++]= s;  /* only push if unique */
      }
    }
    
    /* Shortened the array so the size is what it actually is */
    String msnR[]= new String[nMSN];
    for(i=0;i<nMSN;i++)
      msnR[i]= msn[i];
    
    /* Sort the array ASCENDING order. */
    msnR= SortMAE.sortArray(msnR, true /* ASCENDING order */);
    
    return(msnR);
  } /* getUniqueTableFieldData */
  
  
  /**
   * readListOfSampleNames() - get list of Sample (i.e. HP) names from Config database.
   * These names are setup with the .mae startup file for the stand-alone
   * application or PARAMs for an Applet.
   *<PRE>
   * Samples are defined by the keyword "image" following by the relative
   * sample number (e.g. "image4").
   * 1. First, get all images specified in the PARAM and these are
   *    candidates for loading the samples. They are named
   *    "image1", "image2", ..., "imageMAX_HYBRID_SAMPLES".
   * This pushes names into:
   *       mae.iSampleName[++mae.iHPnbr]= baseFile;
   *       mae.iImageFile[mae.iHPnbr].
   *    Don't add a name if it is already in the list.
   *
   * 2. Then, add names to the list (primarily for loading later
   *    from the menus). These names will come from the Image_File field
   *    in the Samples.txt database.
   *    These names will be pushed into
   *        snHPName[++mae.snHPNbr] and snImageFile[].
   *    Don't add a name if it is already in the list.
   *</PRE>
   * @see GetParams#setDefParam
   * @see MAExplorer#logDRYROTerr
   * @see Util#showMsg
   */
  void readListOfSampleNames()
  { /*readListOfSampleNames */
    mae.hps.nHP= 0;
    
    /* [1] get all images specified in the PARAM and these are
     *    candidates for loading the image. They are named
     *    "image1", "image2", ..., "imageMAX_HYBRID_SAMPLES".
     * They are pushed into
     *     mae.iSampleName[++mae.iHPnbr]
     *     mae.iImageFile[mae.iHPnbr]
     */
    mae.iHPnbr= 0;
    String
      maName,
      databaseFile,      /* Database_File also name of .quant file */
      SampleID,          /* free text name of Database_File must be present */
      DatabaseFileID,    /* optional */
      Login,             /* optional */
      Project,
      SwapCy5Cy3,        /* optional */
      tDataRow[];        /* current row in the table */
    int
      r,
      k;
    boolean unique;
    
    for(int i=1;i<=mae.MAX_HYB_SAMPLES;i++)
    { /* look for image files image1, image2, etc */
      maName= "image"+i;  /* Synthesize "image1", "image2", etc. */
      databaseFile= mae.gp.setDefParam( maName, (String)null);
      if(databaseFile!=null)
      { /* found one  - only push those which exist*/
        unique= true;
        for(k= 1;k<=mae.iHPnbr;k++)
          if(mae.iSampleName[k]==databaseFile)
          { /* dupl. spec. */
            unique= false;
            break;
          }
        if(unique)
        { /* push image# names */
          mae.iSampleName[++mae.iHPnbr]= databaseFile;
          mae.iImageFile[mae.iHPnbr]= "Gifs" +
          mae.dynFileSeparator +
          databaseFile + ".gif";
          if(mae.CONSOLE_FLAG)
            mae.fio.logMsgln("MT-RLSN maName='"+maName+
                             "' databaseFile="+databaseFile);
        }
      }
      else break;
    } /* look for image files image1, image2, etc */
    
    /* [2] Then, add names to the list (primarily for loading later
     * from the menus). These names will come from the
     *    Samples.txt database.
     * Don't add a name if it is already in the list.
     */
    mae.snHPnbr= 0;
    for(int i=1;i<=tRows;i++)
    { /* look for image files image1, image2, etc */
      r= i-1;    /* NOTE: tData[][] counts from 0, iSampleName[] and
                  * snHPName[] count from 1. */
      
      tDataRow= tData[r];
      databaseFile= tDataRow[idxDatabase_File];
      SampleID= tDataRow[idxSample_ID];
      DatabaseFileID= (idxDatabaseFileID!=-1) 
                         ? tDataRow[idxDatabaseFileID] : null;
      Login= (idxLogin==-1) 
                ? null : tDataRow[idxLogin];
      Project= tDataRow[idxProject];
      
      if(databaseFile==null)
        break;         /* end of the list */
      
      else if(databaseFile.length()==0)
        continue;      /* ignore empty names */
      
      else
      { /* Push entry from the SamplesDB.txt associated DB file */
        /* It must meet the visibility requirement
         * or [TODO] are Login and the login-password (.htaccess)
         * has been verified if the Login is required.
         */
        /* Push into local DB list */
        mae.snHPName[++mae.snHPnbr]= databaseFile;
        mae.snHPSampleID[mae.snHPnbr]= SampleID;
        mae.snHPDatabaseFileID[mae.snHPnbr]= DatabaseFileID;
        mae.snImageFile[mae.snHPnbr]= "Gifs" + mae.dynFileSeparator+
                                      databaseFile + ".gif";
        
        String  mnuText= tData[r][idxSample_ID];
        if(mnuText==null || mnuText.length()==0)
          mnuText= databaseFile;                          /* overide it */
        
        /* set default names */
        mae.snHPMenuText[mae.snHPnbr]= mnuText;
        mae.snPrjName[mae.snHPnbr]= Project;
        String defFullName= (DatabaseFileID!=null && DatabaseFileID.length()>0 &&
                             DatabaseFileID.equals(databaseFile))
                              ? SampleID : databaseFile;
        mae.snHPFullStageText[mae.snHPnbr]= defFullName; /* default */
        
        boolean
          requireLoginFlag= false,      /* default is not ratio data */
          swapCy5Cy3DataFlag= false;
        
        if(Login!=null && Login.equals("TRUE"))
        { /* force user to log in */
          requireLoginFlag= mae.isWorkingMAE;   /* MGAP server*/
        /*
        if(mae.CONSOLE_FLAG)
          mae.fio.logMsgln("MT-RLSN.2 needLoginFlag-set"+" Project='"+Project+
                           "' baseFile="+baseFile);
         */
        } /* force user to log in */
        
        mae.snNeedLogin[mae.snHPnbr]= requireLoginFlag;
        mae.snSwapCy5Cy3Data[mae.snHPnbr]= false; /* overide by .mae */
      } /* Push entry from the SamplesDB.txt associated DB file */
    } /* look for image files image1, image2, etc */
    
    /* [CHECK]  mae.hps.nHP= mae.iHPnbr; or mae.snHPnbr */
    
    /* [3] Default at least one image if none specified */
    if(mae.iHPnbr==0)
    { /* no images specified... */
      /* tell user to use Samples menu to pick entries */
      Util.showMsg("NOTE: use Samples menu to pick sample entries");
    } /* no images specified... */
    
  } /* readListOfSampleNames */
  
  
  /**
   * makeHPrestrictedTable() - make a Table of HPs restricted by login required.
   * This test is only performed if running as an Applet.
   * Only include HP rows that are visible in the database
   * i.e. loaded.
   * If the table does not contain the keyword "Database_File",
   * Then simply return the entire table.
   * Otherwise, it creates a new subset table and copies rows
   * of valid data (i.e. pointers).
   * @param srcTbl super table to restrict by login access
   * @return subtable else null if there is a problem.
   * @see SimpleTable
   * @see Table#lookupFieldIdx
   * @see #chkIfHPvisible
   */
  SimpleTable makeHPrestrictedTable(Table srcTbl)
  { /* makeHPrestrictedTable */
    if(!mae.isAppletFlag)
      return(srcTbl);      /* for stand-alone, return same table*/
    
    int idxSrcDBfile= srcTbl.lookupFieldIdx("Database_File");
    if(idxSrcDBfile==-1)
      return(srcTbl);            /* no restriction */
    
    int
      sRows= srcTbl.tRows,
      sCols= srcTbl.tCols,
      dRows= 0,
      dCols= sCols,
      r,c;
    String sData[][]= srcTbl.tData;

    for(r=0; r<sRows; r++)
      if(sData[r]!=null && chkIfHPvisible(sData[r][idxSrcDBfile]))
        dRows++;                      /* will use this row */
    
    SimpleTable dstTbl= new SimpleTable(srcTbl.title+".1","",null,dRows,dCols);
    
    dstTbl.tFields= srcTbl.tFields;   /* just use same field names*/
    
    /* Copy valid source rows to destination rows */
    int dr= 0;                        /* index of dstTbl rows */
    for(r=0; r<sRows; r++)
      if(sData[r]!=null && chkIfHPvisible(sData[r][idxSrcDBfile]))
        dstTbl.tData[dr++]= srcTbl.tData[r];
    
    return(dstTbl);
  } /* makeHPrestrictedTable */
  
  
  /**
   * chkIfHPvisible() - check if allowed to view HP sample.
   * NOTE: called after all logins so can check verified projects.
   * @param hpNameToChk is the sample to check
   * @return true if ok.
   */
  boolean chkIfHPvisible(String hpNameToChk)
  { /* chkIfHPvisible */
    String
      hpName,
      Login,
      Project,
      tDataRow[];
    boolean
      inProjectList,
      isVisible;
    int p;
    
    for(int r= 0; r<tRows; r++)
    { /* check each entry in Samples table */
      tDataRow= tData[r];
      hpName= tDataRow[idxDatabase_File];
      if(hpName==null || hpName.length()==0)
        continue;      /* ignore empty names */
      
      if(hpName.equals(hpNameToChk))
      { /* check if matching name is ok to use */
        Login= (idxLogin==-1) ? "TRUE" : tDataRow[idxLogin];
        Project= tDataRow[idxProject];
        inProjectList= false;
        if(Project==null || Project.length()==0)
          inProjectList= true;
        //else if(mae.nValidPrj==0)
        //  inProjectList= true;
        else
          if(mae.nValidPrj==0)
            inProjectList= true;
          else
            for(p=0;p<=mae.nValidPrj;p++)
              if(mae.validPrj[p].equals(Project))
              {
                inProjectList= true;
                break;
              }
        
        isVisible= (!mae.isWorkingMAE ||
        (mae.isWorkingMAE && inProjectList &&
        Login.equals("TRUE")));
        
        return(isVisible);
      } /* check if matching name is ok to use */
    } /* check each entry in Samples table */
    
    return(false);
  } /* chkIfHPvisible */
  
  
  /**
   * isHPnameIsAccessible() - test if HP database name is accessible.
   * If we are not in workingMAE mode, everything is accessible
   * else look for exact match.
   * @param hpName is the sample to check
   * @return true if sample is accessible.
   */
  boolean isHPnameIsAccessible(String hpName)
  { /* isHPnameIsAccessible */
    if(!mae.isWorkingMAE)
      return(true);
    
    for (int r=0;r<tRows;r++)
    {
      String rfDBname= tData[r][idxDatabase_File];
      if(rfDBname!=null && rfDBname.equals(hpName))
        return(true);
    }
    return(false);        /* no found when require it */
  } /* isHPnameIsAccessible */
  
} /* end of class SamplesTable */
