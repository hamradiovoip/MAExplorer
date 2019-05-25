/** File: MaInfoTable.java */

import java.util.*;
import java.lang.*;
import java.lang.String;

/** 
 * This class reads optional extra sample specific information from the MaInfoTable.txt file.
 * @depricated (somewhat)since SamplesTable takes its place. 
 * 
 The extra information file contains
 * descriptive data for all microarrays. In the case of MGAP project
 * data, it is extracted from each of the R.G. Pathways 2.01 data 
 * files and merged into a single table.
 *<P>
 * Note: this is depricated since read the same background data
 * from the new SamplesDB table.
 * If this file does not exist, the menu entry
 *  (Report | Array | Extra Info) does not appear.
 * It reads a subset table from FILE/URL.
 * This whole file is a tab delimited file consisting of the following fields:
 * <P>
 * ALL fields:
 *  Database_File TableName FilterType_Description 
 *  ShortName Header_Description Date Researcher
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
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Table
 * @depricated
 */

class MaInfoTable extends Table
{
  /** link to global MAExplorer instance */
  private MAExplorer
  mae;
  
  /** name of the remap Table */
  final private String
  maeTbl= "InfoTable";
  
  /** --- lookup field indices, -1 if does not exist --- */
  /** lookup field index. -1 if does not exist */
  int
  idxDatabase_File;
  /** lookup field index. -1 if does not exist */
  int
  idxDatabaseFileID;
  /** lookup field index. -1 if does not exist */
  int
  idxTableName;
  /** lookup field index. -1 if does not exist */
  int
  idxFilterType_Descr;
  /** lookup field index. -1 if does not exist */
  int
  idxShortName;
  /** lookup field index. -1 if does not exist */
  int
  idxHeader_Descr;
  /** lookup field index. -1 if does not exist */
  int
  idxDate;
  /** lookup field index. -1 if does not exist */
  int
  idxResearcher;
  /** lookup field index. -1 if does not exist */
  int
  idxBGLow;
  /** lookup field index. -1 if does not exist */
  int
  idxBGAvg;
  /** lookup field index. -1 if does not exist */
  int
  idxBGRms;
  
  /** flag: set if data was read ok */
  boolean
  hasValidDataFlag;
  
  
  /**
   * MaInfoTable() - constructor
   * @param mae is MAExplorer instance
   * @param fileName is name of table file to read
   * @see Table
   * @see #removeMissingEntries
   * @see #setupFieldIndexes
   */
  MaInfoTable(MAExplorer mae, String fileName)
  { /* MaInfoTable */
    /* Read and set up the table from file */
    super(mae, fileName, "Extra Microarray Info");
    this.mae= mae;
    
    /* create idx.... values  or -1 if there is no table.*/
    setupFieldIndexes();
    
    if(tRows==0)
    { /* can't read table */
      hasValidDataFlag= false;
      return;
    }
    
    /* Remove missing entries if not enabled to view. */
    removeMissingEntries();
    
    hasValidDataFlag= true;  /* set ok */
  } /*MaInfoTable */
  
  
  /**
   * setupFieldIndexes() - setup indexes for common fields for speedups
   * @see #lookupFieldIdxAndRemapFieldName
   */
  private void setupFieldIndexes()
  { /* setupFieldIndexes */
    /* Generate index files for table data*/
    idxDatabase_File= lookupFieldIdxAndRemapFieldName(maeTbl,"Database_File");
    idxDatabaseFileID= lookupFieldIdxAndRemapFieldName(maeTbl,"DatabaseFileID");
    idxTableName= lookupFieldIdxAndRemapFieldName(maeTbl,"TableName");
    idxFilterType_Descr= lookupFieldIdxAndRemapFieldName(maeTbl,"FilterType_Description");
    idxShortName= lookupFieldIdxAndRemapFieldName(maeTbl,"ShortName");
    idxHeader_Descr= lookupFieldIdxAndRemapFieldName(maeTbl,"Header_Description");
    idxDate= lookupFieldIdxAndRemapFieldName(maeTbl,"Date");
    idxResearcher= lookupFieldIdxAndRemapFieldName(maeTbl,"Researcher");
    
    idxBGLow= lookupFieldIdxAndRemapFieldName(maeTbl,"BGLow");
    idxBGAvg= lookupFieldIdxAndRemapFieldName(maeTbl,"BGAvg");
    idxBGRms= lookupFieldIdxAndRemapFieldName(maeTbl,"BGRms");
  } /* setupFieldIndexes */
  
  
  
  /**
   * getTableRowByMaInfoId() - get row of MaInfos data by MaInfo_Id
   * @param Database_File field to test
   * @return data else null if failed.
   */
  String[] getTableRowByMaInfoId(String Database_File)
  { /* getTableRowByMaInfoId */
    String sR[]= null;
    
    for(int mRow=0; mRow<tRows; mRow++)
    {
      String dbName=tData[mRow][idxDatabase_File];
      
      if(dbName!=null && dbName.equalsIgnoreCase(Database_File))
        sR= tData[mRow];
    }
    
    return(sR);
  } /* getTableRowByMaInfoId */
  
  
  /**
   * removeMissingEntries() - remove missing entries if not enabled to view
   * @return true if succeed
   * @see SamplesTable#isHPnameIsAccessible
   */
  private boolean removeMissingEntries()
  { /* removeMissingEntries */
    if(!mae.isWorkingMAE)
      return(true);
    int newNbrRows= tRows;
    
    /* Mark all beta rows as null */
    for (int r=0;r<tRows;r++)
    {
      String dbFileName= tData[r][idxDatabase_File];
      if(dbFileName==null || !mae.sampDB.isHPnameIsAccessible(dbFileName))
      { /* mark this row for removal */
        tData[r]= null;
        newNbrRows--;
      }
    }
    
    /* Bubble sort out the NULL entries */
    for(int r1= 1; r1<tRows;r1++)
    {
      if(tData[r1]!=null && tData[r1-1]==null)
        for(int r2= r1;r2>0;r2--)
          if(tData[r2-1]==null)
          { /* move non-null entry into lowest slot */
            tData[r2-1]= tData[r2];  /* move it down in list */
            tData[r2]= null;
          }
    }
    tRows= newNbrRows;  /* update # rows */
    
    return(true);
  } /* removeMissingEntries */
  
} /* end of class MaInfoTable */



