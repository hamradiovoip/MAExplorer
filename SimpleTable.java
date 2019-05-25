/** File: SimpleTable.java - for use only with MAEPlugins... */
 

/**
 * The SimpleTable class creates an empty table of size tRows and tCols. 
 * It is up to the caller to populate the table. It contains several methods for
 * accessing data in the table.
 *<PRE>
 *<B>List of access methods</B>
 * SimpleTable() - constructor for creating empty table
 * SimpleTable() - constructor for creating a table popupated with fields and data
 * freeTable() - free Table tData[][] and tFields so can garbage collect it
 * trimWhitespace() - trim Table tData[][] and tFields[] data
 * setFieldData() - set new field[] list for table
 * setRowData() - set new row data[] for specific row in the table
 * lookupFieldIdx() - lookup column index of field if exists
 * getTableRowFieldByRowIdx() - get field in row by Table row index
 * getTableRowFieldIdxByRowIdx() - get data by field idx with row index
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.9 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Table
 */

 public class SimpleTable                                          
 {
  /** # of columns/row in the table */
  public int
    tCols;	
  /** # of rows/Table */		
  public int
    tRows;
  /** names of table fields */			
  public String
    tFields[];		        
  /** row vectors of data [0:tRows-1][0:tCols-1] */
  public String
    tData[][];		        
  /** Optional title of table */
  public String 
    title;		        
  /** Optional message associated with table */
  public String 
    msg;	        
  /** Optional file name associated with table */
  public String 
    fileName;
    

  /**
   * SimpleTable() - constructor for creating empty table.
   * @return instance of EMPTY SimpleTable
   */
  public SimpleTable()
  { /* SimpleTable */
    tRows= 0;                    /* empty table */
    tCols= 0;
  } /* SimpleTable *
     
     
  /**
   * SimpleTable() - constructor for creating empty table.
   * The table is similar to a spreadsheet of size (tRows,tCols).
   * If tCols > 0, then tFields[] is allocated.
   * If tCols > 0 AND tRows> 0, then tFields[] is allocated.
   * @param title opt. title of the table
   * @param msg opt. message assoc. w/table
   * @param fileName opt. file name assoc. w/table
   * @param tRows # of rows in table
   * @param tCols # of Field columns in table.
   * @return instance of SimpleTable
   */
  public SimpleTable(String title, String msg, String fileName,
                     int tRows,  int tCols )
  { /* SimpleTable */
    this.tRows= tRows;
    this.tCols= tCols;
    this.title= title;
    this.msg= msg;
    this.fileName= fileName;
    
    /* Allocate the Table field names [0:tCols-1] */
    tFields= (tCols>0) ? new String[tCols] : null;
    
    /* Allocate the Table data [0:tRows-1][0:tCols-1] */
    tData= (tRows>0 && tCols>0) ? new String[tRows][tCols] : null;
  } /* Table */
  
  
  /**
   * SimpleTable() - constructor for creating a table popupated with fields and data.
   * The table is similar to a spreadsheet of size (tRows,tCols).
   * If tCols > 0, then tFields[] is allocated.
   * If tCols > 0 AND tRows> 0, then tFields[] is allocated.
   * @param title opt. title of the table
   * @param msg opt. message assoc. w/table
   * @param fileName opt. file name assoc. w/table
   * @param tRows # of rows in table
   * @param tCols # of Field columns in table.
   * @param tFields field names of the table
   * @param tData rows of data
   * @return instance of SimpleTable
   */
  public SimpleTable(String title, String msg, String fileName,
                     int tRows, int tCols, String tFields[], String tData[][] )
  { /* SimpleTable */
    this.tRows= tRows;
    this.tCols= tCols;
    this.title= title;
    this.msg= msg;
    this.fileName= fileName;
    this.tFields= tFields;
    this.tData= tData;
  } /* Table */
  
  
  /**
   * freeTable() - free Table tData[][] and tFields so can garbage collect it
   * Also zero tRows, tCols
   */
  public void freeTable()
  { /* freeTable */
    tRows= 0;
    tCols= 0;
    tFields= null;
    tData= null;
  } /* freeTable */
  
  
  /**
   * trimWhitespace() - trim Table tData[][] and tFields[] data.
   * This removes leading and trailing white space.
   */
  public void trimWhitespace()
  { /* trimWhitespace */
    if(tCols==0 || tRows==0 || tFields==null || tData==null)
      return;
    
    for(int c=0;c<tCols;c++)
    {
      tFields[c]= tFields[c].trim();
      for(int r=0;r<tRows;r++)
      {
        String
        s= tData[r][c];
        if(s!=null)
          tData[r][c]= s.trim();
        //tData[r][c]= tData[r][c].trim();
      }
    }
  } /* trimWhitespace */
  
  
  /**
   * setFieldData() - set new field[] list for table
   * @param newFields is list of new fields
   * @return nNewFields is # of new fields to use
   * @return true if succeed
   */
  public boolean setFieldData(String newFields[], int nNewFields)
  { /* setFieldList */
    if(tCols<nNewFields)
      return(false);
    for(int i=0;i<nNewFields;i++)
      tFields[i]= newFields[i];
    return(true);
  } /* setFieldData */
  
  
  /**
   * setRowData() - set new row data[] for specific row in the table
   * @param newRowData is list of new data for row rowNbr
   * @return nFields is # of new fields to use
   * @param rowNbr is the row to change
   * @return true if succeed
   */
  public boolean setRowData(String newRowData[], int nFields, int rowNbr)
  { /* setRowData */
    if(tCols<nFields || rowNbr>=tRows)
      return(false);
    for(int i=0;i<nFields;i++)
      tData[rowNbr][i]= newRowData[i];
    return(true);
  } /* setRowData */
  
  
  /**
   * lookupFieldIdx() - lookup column index of field if exists.
   * @param fieldName to lookup
   * @return index if found, else if it does not exist return -1
   */
  public int lookupFieldIdx(String fieldName)
  { /* lookupFieldIdx */
    int
    idx= -1;          /* failure default */
    
    for(int i=0; i<tCols; i++)
      if(fieldName.equalsIgnoreCase(tFields[i]))
      {
        idx= i;
        break;
      }
    return(idx);
  } /* lookupFieldIdx */
  
  
  /**
   * getTableRowFieldByRowIdx() - get field in row by Table row index
   * @param idx is field (i.e. column) to use
   * @param fieldName to lookup
   * @return field if found, else null
   * @see #lookupFieldIdx
   */
  public String getTableRowFieldByRowIdx(int idx, String fieldName)
  { /* getTableRowFieldByRowIdx */
    int fIdx= lookupFieldIdx(fieldName);
    
    if(fIdx<0 || idx<1)
      return(null);
    
    String sR= tData[idx][fIdx];
    
    return(sR);
  } /* getTableRowFieldByRowIdx */
  
  
  /**
   * getTableRowFieldIdxByRowIdx() - get data by field idx with row index
   * @param rowIdx is field (i.e. column) to use
   * @param fieldIdx of field to to lookup
   * @return data if found, else null
   */
  public String getTableRowFieldIdxByRowIdx(int rowIdx, int fieldIdx)
  { /* getTableRowFielddxByRowIdx */
    if(fieldIdx<0 || rowIdx<0)
      return(null);
    
    String sR= tData[rowIdx][fieldIdx];
    
    return(sR);
  } /* getTableRowFieldIdxByRowIdx */
  
  
 } /* end of class SimpleTable */
 
 
 
