/** File: ConfigTable.java */

import java.util.*;
import java.lang.*;
import java.lang.String;

/**
 * This reads the Config file as a tab-delimited table to define the initial Configi state. 
 * Later, these values may be overiden by GetParams or
 * by user interaction via the menu. It reads the MaExplorerConfig.txt
 * table from FILE/URL.  This whole file is a tab-delimited file
 * consisting of the following fields: (Parameter, Value, DataType,
 * Comments).
 *<PRE>
 * Table to map User Table and Field to MAExplorer Table and Field
 * Config file entries of the form:
 *   Parameter= "mapTF"
 *   Value= "maeTname,maeFname,usrTname,usrFname"
 *
 * Eg.  "GenomicIDdata,Clone_ID,GIPOdata,Clone Id"
 *</PRE>
 * This table is set up here and invoked immediately after
 * a table is read to remap the names so the MAE names can
 * be used within MAExplorer.
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.9 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Config
 * @see Table
 */

class ConfigTable extends Table
{
  /* --- Lookup field indices --- */
  /** parameter string name */
  int              
    idxParameter;        
  /** value of parameter */
  int              
    idxValue;            
  /** i.e "int", "boolean" or "float" */
  int              
    idxDataType;         
  /** optional comments */
  int              
    idxComments;         
  
  /** max # of (maeTable, maeField) to (userTable, userField) map entries */
  final private static int
    MAX_MU_MAP= 50;
  /** map [0:nMUmap-1] names of MAE Table */
  private String
    mapMTname[];         
  /** [map 0:nMUmap-1] names of MAE Field */
  private String
    mapMFname[];         
  /** map [0:nMUmap-1] names of User Table */
  private String
    mapUTname[];         
  /** map 0:nMUmap-1] names of User Field */
  private String
    mapUFname[];         
  /** # of map entries */
  int 
    nMUmap;              

  /** flag: set if data was read ok */
  boolean 
    hasValidData;        
  
    
  /**
   * ConfigTable() - constructor to read the configuration table file.
   * @param mae is the MAExplorer instance
   * @param fileName is the full path filename for configuation file
   * @see #setupFieldIndexes
   * @see #setupTableFieldNameMap
   */
  ConfigTable(MAExplorer mae, String fileName)
  { /* ConfigTable */
    /* Read and set up the table from file */
    super(mae, fileName, "Configuration Parameters");
    
    this.trimWhitespace();    /* remove leading & trailing whitespace */
    
    hasValidData= false;      /* Default: set if data was read ok */
    if(tRows==0)
      return;                 /* can't read table */
    
    setupFieldIndexes();	/* create idx.... values */
    setupTableFieldNameMap();  /* map user to MAE field names */
    
    hasValidData= true;       /* set ok */
  } /*ConfigTable */
  
  
  /**
   * setupFieldIndexes() - setup indexes for commonly found fields.
   * We use indexes to speed up the table access.
   * @see #lookupFieldIdx
   */
  void setupFieldIndexes()
  { /* setupFieldIndexes */
    /* Generate index files for table data*/
    idxParameter= lookupFieldIdx("Parameter");
    idxValue= lookupFieldIdx("Value");
    idxDataType= lookupFieldIdx("DataType");
    idxComments= lookupFieldIdx("Comments");
  } /* setupFieldIndexes */
  
  
  /**
   * setupTableFieldNameMap() - map user to MAE field names
   *<PRE>
   *   Parameter= "mapTF"
   *   Value= "maeTname,maeFname,usrTname,usrFname"
   *</PRE>
   * @see Util#cvs2Array
   * @see #getNthValueByName
   */
  private void setupTableFieldNameMap()
  { /* setupTableFieldNameMap */
    String
      muData[],  /* contains mT, mF, uT, uF data strings */
      s;
    
    mapMTname= new String[MAX_MU_MAP];
    mapMFname= new String[MAX_MU_MAP];
    mapUTname= new String[MAX_MU_MAP];
    mapUFname= new String[MAX_MU_MAP];
    nMUmap= 0;
    
    for(int i=0;i<MAX_MU_MAP;i++)
    { /* get as many entries as they sent us */
      s= getNthValueByName("mapTF", i);
      if(s!=null)
      { /* found one */
        /* Parse the string s into mT,mF,uT,uF */
        muData= Util.cvs2Array(s,4,",");
        mapMTname[nMUmap]= muData[0];
        mapMFname[nMUmap]= muData[1];
        mapUTname[nMUmap]= muData[2];
        mapUFname[nMUmap]= muData[3];
        ++nMUmap;              /** # of map entries */
      } /* found one */
    } /* get as many entries as they sent us */
  } /* setupTableFieldNameMap */
  
  
  /**
   * mapUsrFieldToMaeField() - map user (Tbl,Field) name to MAE Field name
   * @param usrTbl is the user's name for the table
   * @param usrField is the user's name for the field
   * @return usrField if can't find it
   */
  String mapUsrFieldToMaeField(String usrTbl, String usrField)
  { /* mapUsrFieldToMaeField */
    String
      mapMT,
      mapMF,
      sR= usrField;             /* default to same name */
    
    /* Look for matching user table and field */
    for(int i=0;i<nMUmap;i++)
    { /* search map table for a match of usrTbl and usrField */
      mapMT= mapMTname[i];
      mapMF= mapMFname[i];
      if(mapMT.equalsIgnoreCase(usrTbl) && mapMF.equalsIgnoreCase(usrField))
      { /* found it in the table - remap the field name */
        sR= mapUFname[i];
        break;
      }
    } /* search map table for a match of usrTbl and usrField */
    
    return(sR);
  } /* mapUsrFieldToMaeField */
  
  
  /**
   * getConfigRowByRowIdx() - get row of Config data table Row
   * @param tableRow to get data
   * @return row of data else null if can't find it
   */
  String[] getConfigRowByRowIdx(int tableRow)
  { /* getConfigRowByRowIdx */
    if(tableRow<0 || tableRow>=tRows)
      return(null);
    return(tData[tableRow]);
  } /* getConfigRowByRowIdx */
  
  
  /**
   * getConfigRowFieldByName() - get row field data by array coords
   * @param tableRow to get data
   * @param fieldName to get data
   * @return field data else null if can't find it
   * @see #lookupFieldIdx
   */
  String getConfigRowFieldByName(int tableRow, String fieldName)
  { /* getConfigRowFieldByName */
    int fIdx= lookupFieldIdx(fieldName);
    if(fIdx<0 || tableRow<0 || tableRow>=tRows)
      return(null);
    String sR= tData[tableRow][fIdx];
    return(sR);
  } /* getConfigRowFieldByName */
  
  
  /**
   * getConfigRowFieldByRowIdxName() - get row field by row index and name
   * @param idx is row index to get data
   * @param fieldName to get data
   * @return field data else null if can't find it
   * @see #getTableRowFieldByRowIdx
   */
  String getConfigRowFieldByRowNameIdx(int idx, String fieldName)
  { /* getConfigRowFieldByRowIdxName */
    return(getTableRowFieldByRowIdx(idx,fieldName));
  } /* getConfigRowFieldByRowIdxName */
  
  
  /**
   * getConfigRowFieldByRowFieldIdxses() - get row field by row and field indices
   * @param rowIdx is row index to get data
   * @param fieldIdx is field index to get data
   * @return field data else null if can't find it
   */
  String getConfigRowFieldByRowFieldIdxes(int rowIdx, int fieldIdx)
  { /* getConfigRowFieldByRowFieldIdxes */
    if(fieldIdx<0 || rowIdx<0)
      return(null);
    String sR= tData[rowIdx][fieldIdx];
    return(sR);
  } /* getConfigRowFieldByRowFieldIdxes */
  
  
  /**
   * getTableRowByParameter() - get row of Configuration table data by Parameter
   * @param param to use to search DB
   * @return data else null if failed.
   */
  String[] getTableRowByParameter(String param)
  { /* getTableRowByParameter */
    String sR[]= null;
    for(int mRow=0; mRow<tRows; mRow++)
      if((tData[mRow][idxParameter]).equalsIgnoreCase(param))
      {
        sR= tData[mRow];
        break;
      }
    return(sR);
  } /* getTableRowByParameter */
  
  
  /**
   * getValueByName() - get Configuration table Value by Name
   * @param name of parameter to get data
   * @return data else null if failed.
   */
  String getValueByName(String name)
  { /* getValueByName */
    String sR= null;
    for(int mRow=0; mRow<tRows; mRow++)
    { /* look for name in row Parameter instance and return Value */
      String sTable= tData[mRow][idxParameter];
      if(sTable==null)
        return(null);
      else if(sTable.equalsIgnoreCase(name))
      { /* found it */
        sR= tData[mRow][idxValue];
        break;
      }
    }
    return(sR);
  } /* getValueByName*/
  
  
  /**
   * getNthValueByName() - get n'th Configuration table Value by Name
   * @param name of parameter to get data
   * @param n is the n'th instance of the name to get
   * @return data else null if failed.
   * @see #getValueByName
   */
  String getNthValueByName(String name, int n)
  { /* getNthValueByName */
    String sR= null;
    int cnt= 0;
    
    if(n==0)
      return(getValueByName(name));
    
    for(int mRow=0; mRow<tRows; mRow++)
    { /* look for name in nth row Parameter instance & return Value*/
      String sTable= tData[mRow][idxParameter];
      if(sTable==null)
        return(null);
      else if(sTable.equalsIgnoreCase(name))
      { /* found an instance - check if right one */
        String s= tData[mRow][idxValue];
        if(++cnt>=n)
        { /* use nth instance */
          sR= s;
          break;
        }
      } /* found an instance - check if right one */
    } /* look for name in nth row Parameter instance & return Value*/
    
    return(sR);
  } /* getNthValueByName*/
  
  
} /* end of class ConfigTable */



