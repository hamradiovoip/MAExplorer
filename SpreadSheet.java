/** File: SpreadSheet.java */

import java.awt.*;
import java.awt.Color;
import java.io.*;
import java.awt.TextField;
import java.util.*;
import java.lang.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.*;
import java.awt.Cursor;
/** 
 * This SpreadSheet class implements a dynamic spread sheet data structures.
 * This includs underlying URL mappings, column and row headings etc.  It has 
 * been extensively edited by the MAExplorer group to integrate it better
 * with MAExplorer. 
 *<P>
 *<PRE>
 * This class creates a spread sheet with 2 fixed rows on top and on
 * the Left.  The user data is modified using
 * ShowSpreadSheetPopup.cvtTableToprepDataOrig(). 
 *
 * This new size is the size of prepDataOrig[0:rows][0:cols] w/ 2
 * extra col & 2 extra rows.  The size is (tRows+2)*(tCols+2) but is
 * addressed in [1:tRows+1][1:tCols+1].
 *
 * So if the user prepDataOrig is
 *     prepDataOrig[0:uRows-1][uCols], 
 * then the virtual array is
 *     prepDataEdit[uRows+2][uCols+1]
 * and rows 0 and column 0 are fixed.
 *
 * Col 0 vector and Row 0 vector are NULLs.
 * Col 1 vector is a fixed list of quoted numbers "1", "2", "3"  ...
 * Row 1 vector is a fixed list of field names F1, F2, F3,...
 *			   
 * and rows 0 and column 0 are fixed.
 *  
 * row\col    0    1     2      3      5      6            tCols+1 (index)
 *    0      null null  null   null   null   null ...
 *    1      null ""    F1(A)  F2(B)  F3(C)  F4(D) ...     F[tCols-1]
 *    2      null "1"     *      *      *      *  . .  
 *    3      null "2"     *      *      *      *  . . .    ...
 *    4      null "3"     *      *      *      *  . . .
 *           . . .
 *  tRows+1 null "tRows" . . .
 *  (index)
 * This means that when we scroll, the contents of these fixed
 * cells are not changed. We only copy a subregion from 
 * prepDataOrig[2:prepRows-1][2:prepCols-1] (???CHECK???)
 *
 * CLASSES
 * =======
 *  #1 SpreadSheet    -  creates spreadsheet, title, and control buttons
 *  #2 SSspreadPanel  -  creates a panel of cells and scrollbars, create 
 *                       prepDataEdit[][].
 *  #3 SSstatusBar    -  panel w/ textfield displaying current grid value.
 *  #4 SStextCell     -  binary tree cells of TextFields with a String value 
 *  #5 SStextArray    -  array of SStextCell 
 *  #6 SSscrollHorizontal - a horizontal scrollbar for the SpreadSheet
 *  #7 SSscrollVertical - a vertical scrollbar for the SpreadSheet 
 *</PRE>
 *
 * @author Torsten Hothorn
 * Originally written by Torsten Hothorn, 19.04.1998, 
 * Gnu General Licence
 *<P>
 * Heavily modified by the MAExplorer group.
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Report
 * @see ShowSpreadsheetPopup
 *
 */

class SpreadSheet extends Panel
{ /* class SpreadSheet */
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
 /*    #1             CLASS SpreadSheet                            */
 /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

 /** link to global MAExplorer instance */
  MAExplorer 
     mae;                  
  
  /* --- Spreadsheet Cell modes */
  /** Spreadsheet Cell mode: Cell is just data */
  final static int    
    CELL_NOP= 1;            
  /** Spreadsheet Cell mode: Cell is a URL hypertext link */
  final static int    
    CELL_URL= 2;
  /** Spreadsheet Cell mode: Cell is an expression profile */
  final static int    
    CELL_EXPR_PROFILE= 3;

  /** link to local SSspreadPanel instance */
  private SSspreadPanel
    sp;                     
  /** vertical spreadsheet scroller instance */
  private SSscrollVertical 
    sv;                     
  /** horizontal spreadsheet scroller instance */
  private SSscrollHorizontal 
    sh;                     

  /** font size number for cell entries */
  private int 
    fontSize;               
  /** original # prep rows for prepDataOrig[0:prepRows-1][0:prepCols-1]
   * sizes w/ 2 extra col & 2 extra rows.
   * Col 0 and Row 0 are NULLs.
   * Col 1 rows [1:prepRows] is 1.2.3.4.5. prepRows..
   * Row 1 cols [1:prepCols] is F1, F2, F3,...
   */ 
  private int              
    prepRows;               
  /** original # prep columns for prepDataOrig[0:prepRows-1][0:prepCols-1]
   * sizes w/ 2 extra col & 2 extra rows.
   * Col 0 and Row 0 are NULLs.
   * Col 1 rows [1:prepRows] is 1.2.3.4.5. prepRows..
   * Row 1 cols [1:prepCols] is F1, F2, F3,...
   */
  private int
    prepCols;
  /** virtual row size of prepDataEdit[0:rows-1][0:cols-1]
   * This also inclues the field names in the top row
   * and the row # in the left column */
  private int
    virRows;
  /** virtual column sizes of prepDataEdit[0:rows-1][0:cols-1]
   * This also inclues the field names in the top row
   * and the row # in the left column */
  private int
    virCols;

  /** ARG:orig. prepDataOrig[0:prepRows-1][0:prepCols-1] */
  String
    prepDataOrig[][];
  /** ARG: opt. URL to use if not null 
   * then sizeof [prepRows][prepCols](??? CHECK???)
   */
  String
    dataURL[][];
  /** ARG: user title */
  String
    title;                 
  /** ARG: cell URL font */
  Font   
    cellUrlFont;           
  /** ARG: cell data font */
  Font   
    cellDataFont;
  /** ARG: cell label font */  
  Font
    buttonFont;
  /** ARG: cell type [cols] fields is CELL_xxxx */
  int 
    cellFormat[];
  /** popup browser flag */          
  boolean
    urlPopupFlag;	
  /** link to global instance of SpreadsheetPopup */  
  ShowSpreadsheetPopup
    ssp= null;         
  /** title panel for of spreadsheet */    
  Panel 
    titlePanel;            
  


  /**
   * Spreadsheet() - creates a new Panel with BoderLayout and adds a new SSspreadPanel
   * (the grid itself) w/ horizontal and vertical scrollbars. Specify the
   * fonts as well.
   * @param ssp is instance of ShowSpreadsheetPopup
   * @param mae is instance of MAExplorer
   * @param virRows is # of virtual rows i.e. size of prepDataEdit[][]
   * @param virCols is # of virtual columns i.e. size of prepDataEdit[][]
   * @param prepDataOrig is user data[0:usrRawData][]
   * @param prepRows is the size of prepDataOrig
   * @param prepCols is the size of prepDataOrig
   * @param fontSize is 8, 10, 12, 14
   * @param title
   * @param cellUrlFont is the cell URL font
   * @param cellDataFont is the cell data font
   * @param buttonFont is the cell label font
   * @param cellFormat[] is the cell type fields
   * @param dataURL[][] is the data for URLs if not null
   * @see SSspreadPanel#initValues
   * @see #doSpreadSheet
   */
  SpreadSheet(ShowSpreadsheetPopup ssp, MAExplorer mae, int virRows, int virCols,
              String[][] prepDataOrig, int prepRows, int prepCols, int fontSize,
              String title, Font cellUrlFont, Font cellDataFont, Font buttonFont,
              int cellFormat[], String dataURL[][])
  { /* SpreadSheet constructor */
    this.ssp= ssp;
    this.mae= mae;
    this.virRows= virRows;
    this.virCols= virCols;
    this.prepDataOrig= prepDataOrig;
    this.fontSize= fontSize;
    this.title= title;
    this.cellUrlFont= cellUrlFont;
    this.cellDataFont= cellDataFont;
    this.buttonFont=buttonFont;
    this.cellFormat= cellFormat;
    this.dataURL= dataURL;
    this.urlPopupFlag= ssp.urlPopupFlag;
    
    this.prepRows= prepRows;
    this.prepCols= prepCols;
    
    doSpreadSheet();           /* create the spreadsheet */
    
    sp.initValues(prepDataOrig);    /* add data to spreadsheet */
  } /* SpreadSheet constructor */
  
  
  /**
   * doSpreadSheet() - create the spreadsheet panel, title, and control buttons
   * @see SSspreadPanel
   */
  private void doSpreadSheet()
  { /* doSpreadSheet */
    this.setLayout(new BorderLayout());
    
    /*
    if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("doSpreadSheet():   virRows= "+ virRows +
                        " virCols= "+ virCols +
                        " prepRows= "+ prepRows +
                        " prepCols= "+ prepCols+
                        " last-col= "+prepDataOrig[1][prepCols-1] +
                        " last-row= "+prepDataOrig[prepRows-1][1]);
    */
    
    sp= new SSspreadPanel(this.prepDataOrig, ssp, mae, virRows, virCols,
                          prepRows, prepCols, fontSize,
                          cellUrlFont, cellDataFont, buttonFont,
                          cellFormat, dataURL);
    
    sp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
    add("Center", sp);
    add("East", sp.sv);
    add("South", sp.sh);
    
    /* Make a panel with both title and status panel */
    titlePanel= new Panel();
    
    titlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
    titlePanel.setLayout(new GridLayout(2,1));
    Label
    titleLabel= new Label(title, Label.CENTER);
    if(buttonFont!=null)
      titleLabel.setFont(buttonFont);
    titlePanel.add(titleLabel);
    titlePanel.add(sp.status);
    add("North", titlePanel);
  } /* doSpreadSheet */
  
  
  /**
   * initValues() - initialize data values for spreadsheet data structures
   * @param data is the initial data values
   * @see SSspreadPanel#initValues
   */
  void initValues(String[][] data)
  { /* initValues */
    sp.initValues(data);
  } /* initValues */
  
  
   /*
    * setValue() - inserts a String into a cell in the grid.
    * We start with (2,2), because (1,x) and (x,1) are used as labels.
    * You can set the labels using this method and (1,x), (x,1)
    */
  /*
  void setValue(int i, int j, String arg)
    { sp.setValue_NOTUSED(i,j,arg);  }
  */
  
  
  /**
   * getValue() - get the String from the cell at grid position (i,j)
   * @param i is grid position i
   * @param j is grid position j
   * @return value at (i,j)
   * @see SSspreadPanel#getValue
   */
  String getValue(int i, int j)
  { return(sp.getValue(i,j)); }
  
  
  /**
   * getValues() - gets the whole grid as String[][] incl. labels
   * @return entire grid
   * @see SSspreadPanel#getValue
   */
  String[][] getValues()
  { return(sp.getValues()); }
  
  
  /**
   * insertColumn() - insert a column at column k
   * @param k number of new row to insert
   * @see SSspreadPanel#insertColumn
   */
  void insertColumn(int k)
  { sp.insertColumn(k); }
  
  
  /**
   * deleteColumn() - delete row at column k
   * @param k number of new row to delete
   * @see SSspreadPanel#deleteColumn
   */
  void deleteColumn(int k)
  { sp.deleteColumn(k); }
  
} /* class SpreadSheet */





/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*   #2              CLASS  SSspreadPanel                         */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
class SSspreadPanel extends Panel implements MouseListener
{ /* class SSspreadPanel */
  MAExplorer 
    mae;

  /** the array of SStextCell cells */
  SStextArray 
    cells;		        
  int 
    currentValueV,
    currentValueH;
  /** ULHC base address starting at (1,1) */
  private int 
    positionX= 1,	        
    positionY= 1;
  /** scroll bars */
  SSscrollVertical       
    sv;
  SSscrollHorizontal
    sh;
  /** [RC] & full cell contents displayed @top*/
  SSstatusBar 
    status;			
  /** Calculated default or overidden */
  Font   
    dataFontSize;
  /** Calculated default or overidden */
  Font
     headerFontSize;             
   int
     headerClickedCol=0;
   /** for sorting */
   int
     index[];
   /** ARG: "virtual screen" size that is visible */
   int			
     virRows;	                
   /** ARG: "virtual screen" size */
   int
     virCols;	                
   /** ARG: user data size*/
   int
     prepRows;                   
   /** ARG: user data size*/
   int
     prepCols;                   
   /** ARG: only if no fonts specified */
   int
     fontSize;                   
   /** opt. data for URLs if not null [0:prepRows-1][0:prepCols-1] */ 
   String
     dataURL[][];   
   /** "virt. screen" [1:prepRows-1][1:prepCols-1] */
   String
     prepDataEdit[][];    
   /** ARG orig. data[1:prepRows-1][1:prepCols-1] */
   String
     prepDataOrig[][];           
  /** ARG: cell URL font */
  Font   
    cellUrlFont;		
  /** ARG: cell data font */
  Font   
    cellDataFont;		
  /** ARG: cell label font */
  Font   
    buttonFont;			
  int
    locRow=0,
    locCol=0;
  /** keep track of col last clicked */
  int
    oldPosition;		
  int
    offSet;  		
  /** ARG: cell type fields[0:cols-1] one of CELL_NOP,CELL_URL,
   * CELL_EXPR_PROFILE in user data space...
   */
  int
    cellFormat[];
  /** pass down globals  */
  ShowSpreadsheetPopup
    ssp= null;			
  /** popup browser flag  */
  boolean
    urlPopupFlag;		
  /** keep track of desend/ascending  */
  boolean
    sortAsendDesendFlag;		

  GridLayout 
    lay;
 

  /**
   * SSspreadPanel() - constructor, panel holding (row,col) matrix of TextFields
   * with the URL font and fields.
   * @param prepDataOrig is user data[0:usrRawData][]
   * @param ssp is instance of ShowSpreadsheetPopup
   * @param mae is instance of MAExplorer
   * @param virRows is # of virtual rows i.e. size of prepDataEdit[][]
   * @param virCols is # of virtual columns i.e. size of prepDataEdit[][]
   * @param prepRows is the size of prepDataOrig
   * @param prepCols is the size of prepDataOrig
   * @param fontSize is 8, 10, 12, 14
   * @param cellUrlFont is the cell URL font
   * @param cellDataFont is the cell data font
   * @param buttonFont is the cell label font
   * @param cellFormat[] is the cell type fields
   * @param dataURL[][] is the data for URLs if not null
   * @see #doSSspreadPanel
   */
  SSspreadPanel(String prepDataOrig[][], ShowSpreadsheetPopup ssp,
                MAExplorer mae, int virRows, int virCols,
                int prepRows, int prepCols, int fontSize, Font cellUrlFont,
                Font cellDataFont, Font buttonFont, int cellFormat[],
                String dataURL[][])
  { /* SSspreadPanel */
    this.mae= mae;
    this.ssp= ssp;
    this.virRows= virRows;
    this.virCols= virCols;
    this.prepRows= prepRows;
    this.prepCols= prepCols;
    this.prepDataOrig= prepDataOrig;
    this.fontSize= fontSize;
    this.cellUrlFont= cellUrlFont;
    this.cellDataFont= cellDataFont;
    this.buttonFont=buttonFont;
    this.cellFormat= cellFormat;
    this.dataURL= dataURL;
    this.offSet= 0;
    this.prepDataEdit= null;
    this.urlPopupFlag= ssp.urlPopupFlag;
    this.sortAsendDesendFlag=true;
    this.oldPosition=0;
    
    doSSspreadPanel();
  } /* SSspreadPanel */
  
  
  /**
   * SSspreadPanel() - constructor, inits the grid with values
   * without the URL font and fields.
   * @param mae is instance of MAExplorer
   * @param virRows is # of virtual rows i.e. size of prepDataEdit[][]
   * @param virCols is # of virtual columns i.e. size of prepDataEdit[][]
   * @param prepDataOrig is user data[0:usrRawData][]
   * @param prepRows is the size of prepDataOrig
   * @param prepCols is the size of prepDataOrig
   * @param fontSize is 8, 10, 12, 14
   * @param cellDataFont is the cell data font
   * @param buttonFont is the cell label font
   * @see #doSSspreadPanel
   * @see #initValues
   */
  SSspreadPanel(MAExplorer mae, int virRows, int virCols,
                String prepDataOrig[][], int prepRows, int prepCols,
                int fontSize, Font cellDataFont, Font buttonFont)
  { /* SSspreadPanel */
    this.mae= mae;
    this.virRows= virRows;
    this.virCols= virCols;
    this.prepRows= prepRows;
    this.prepCols= prepCols;
    this.prepDataOrig= prepDataOrig;
    this.fontSize= fontSize;
    this.cellUrlFont= null;
    this.cellDataFont= cellDataFont;
    this.buttonFont=buttonFont;
    this.cellFormat= null;
    this.dataURL= null;
    this.offSet= 0;
    this.prepDataEdit= null;
    this.sortAsendDesendFlag=true;
    this.oldPosition=0;
    
    doSSspreadPanel();
    
    initValues();
  } /* SSspreadPanel */
  
  
  /**
   * doSSspreadPanel() - actually build the spreadsheet panel
   * @see SSscrollHorizontal
   * @see SSscrollVertical
   * @see SSstatusBar
   * @see SStextArray
   * @see SStextArray#getTextField
   */
  private void doSSspreadPanel()
  { /* doSSspreadPanel */
    int size=0;
    
    /* [1] Set font size and type */
    if(fontSize == 8 || fontSize == 10 || fontSize == 12 || fontSize == 14)
      size= fontSize;
    else
      size= 12;
    if(buttonFont!=null)
      headerFontSize= buttonFont;
    else
      headerFontSize= new Font(mae.cfg.fontFamily, Font.BOLD, size);
    if(cellDataFont!=null)
      dataFontSize= cellDataFont;
    else dataFontSize= new Font(mae.cfg.fontFamily, Font.PLAIN, size);
    
    currentValueV= 0;
    currentValueH= 0;
    
    /* [2] cells which are visible and total data*/
    offSet= 0;
    ssp.virtualHdrPos= -1;
    ssp.prepHdrPos= -1;
    
    /* [3] The prepDataEdit[][] holds all the values
     * of user as Strings (prepCols,prepRows). If we scroll, we "move" the
     * cells over this array. The cells only display data from
     * prepDataEdit - not the original user prepDataOrig. Note
     * That we do special things with Col 1 (#s) and row 1 (field names).
     */
    prepDataEdit= new String[prepCols][prepRows];
    prepDataEdit[1][1]= " ";
    lay= new GridLayout(virRows,virCols);
    
    /* [4] Create the scrollbar, NOTE: do not add here,
     * add it to the panel in SpreadSheet
     */
    lay.setHgap(0);
    lay.setVgap(0);
    setLayout(lay);
    cells= new SStextArray(virRows,virCols);
    sv= new SSscrollVertical(this);
    sh= new SSscrollHorizontal(this);
    
    
    /* [5] Create status bar */
    status= new SSstatusBar(ssp,mae,virRows,virCols);
    
    
    /* [6] Add data to visible window of the SSspreadPanel */
    for (int r=1; r<= virRows; r++)
    {
      for (int c= 1; c<= virCols; c++)
      { /* setup visible text field areas */
        this.add(cells.getTextField(r,c));
        cells.getTextField(r,c).addMouseListener(this);
        cells.getTextField(r,c).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        /* we use all (1,x) and (x,1) fields as labels */
        cells.getTextField(r,c).setEditable(false);
        
        if ((r == 1 ) || (c == 1)) /* headers */
        { /* left column or top row*/
          cells.getTextField(r,c).setFont(headerFontSize);
        }
        
        setCellFontAndColor(r,c);
        
        /* force headers to black */
        cells.getTextField(r,1).setForeground(Color.black);
        cells.getTextField(1,c).setForeground(Color.black);
      } /* setup visible text field areas */
    } /* add data  */
  } /* doSSspreadPanel */
  
  
  /**
   * setCellFontAndColor() - use dataFont or cellUrlFont depending on cell(r,c)
   * @param r is row of cell
   * @param c is column of cell
   * @see SStextArray#getTextField
   */
  private void setCellFontAndColor(int r, int c)
  { /* setCellFontAndColor */
    int
      formatCol= c-2,
      localOffset= (formatCol + offSet);
    
    if(localOffset >= 0 && localOffset <= virCols && (r>=1 && c>=1))
    {
      if(r>=1 && c>=1 &&  cellUrlFont!=null && cellFormat!=null &&
         cellFormat[localOffset] == SpreadSheet.CELL_URL)
      { /* special handling for URLs */
        cells.getTextField(r,c).setFont(cellUrlFont);
        cells.getTextField(r,c).setForeground(Color.blue);
      }
      else
      { /* regular data cells */
        cells.getTextField(r,c).setFont(dataFontSize);
        cells.getTextField(r,c).setForeground(Color.black);
      }
    }
    else
    { /* regular data cells */
      cells.getTextField(r,c).setFont(dataFontSize);
      cells.getTextField(r,c).setForeground(Color.black);
    }
  } /* setCellFontAndColor */
  
  
  /**
   * sortSpreadsheet() - sort entire spread sheet based on column clicked.
   * @param oldPosition
   * @param colToSort column to sort
   * @param prepCols is # actual data cols
   * @param prepRows is # actual data cols
   * @param unsortedData is unsorted data to be stroed
   * @return sorted data
   * @see SortMAE#bubbleSortIndex
   * @see Util#cvs2f
   */
  String[][] sortSpreadsheet(int oldPosition, int colToSort,
                             int prepCols, int prepRows,
                             String unsortedData[][])
  { /* sortSpreadsheet */
    if(prepCols > 1 && prepRows > 1 && prepDataOrig != null)
    { /* sort rows by data in particular column */
      String tempData[][]= new String[prepRows+1][prepCols+1];
      
      /* [1] Test whether sort by string of float value */
      boolean isNumber= false;    /* set to true if data is int or float*/
      
      /* fails it not a number */
      try
      {
        String sampleValue= null;
        isNumber=true;
        
        for(int r= 2; r < prepRows; r++)
          if(prepDataOrig[r][colToSort]!=null)
          {
            sampleValue= prepDataOrig[r][colToSort];
            Float F= new Float(sampleValue);
            float fTest= F.floatValue();
          }
      }
      catch(NumberFormatException e)
      {
        isNumber= false;  /* fails if any element is not a # */
      }
      
      int index[];	            /* [0:rows-1] used to sort rows by natural
                                 * sort of selected column. */
      String dataUCstr[]= null; /* copy of column data to sort as string */
      float dataUCnbr[]= null;  /* copy of column data to sort as float */
      
      /* [2] get proper type of data to sort */
      if(isNumber)
        dataUCnbr= new float[prepRows+1];  /* default [] of 0.0F */
      else
        dataUCstr= new String[prepRows+1];
      
     /* [3] convert temp array [0:rows-2] of col data from
      * rows [2:rows] to be sorted to upper case.
      * Note: data starts in row 2 not 0 or 1!
      */
      for(int r= 2; r <=prepRows; r++)
      {
        if(prepDataOrig[r][colToSort]!=null)
        {
          String value= prepDataOrig[r][colToSort].toUpperCase();
          if(isNumber)
            dataUCnbr[r-2]= Util.cvs2f(value);
          else dataUCstr[r-2]= value; /* save only row data */
        }
        else if(!isNumber)
          dataUCstr[r-2]= "";
      }
      
      /* [4] toggle to desending/assending switch each time sort it */
      if(oldPosition == colToSort)
      { /* toggle to desending/assending */
        if(sortAsendDesendFlag)
          sortAsendDesendFlag= false;
        else
          sortAsendDesendFlag= true;
      }
      else
        sortAsendDesendFlag= true;
      
      oldPosition= colToSort;       /* new old position */
      
     /* [5] create sorted index array based on str array.
      * Data in dataUCxxx[] and index[] is in [0:rows-2] NOT [2:rows]
      */
      if(isNumber)
        index= SortMAE.bubbleSortIndex(dataUCnbr, prepRows-1,
        sortAsendDesendFlag);
      else
        index= SortMAE.bubbleSortIndex(dataUCstr, prepRows-1,
        sortAsendDesendFlag);
      
      /* [6] place labels at top */
      for(int c= 1; c < prepCols; c++)
        tempData[1][c]= unsortedData[1][c];
      
      
      /* [7] copy data, sorting via index array */
      for(int r= 2; r <= prepRows; r++)
        for(int c= 1; c < prepCols; c++)
        {
          if(c==1)
            tempData[r][c]= prepDataOrig[r][c];
          else
          {
            int
            idx= 2+index[r-2]; /* cvt index value to table data[]*/
            tempData[r][c]= prepDataOrig[idx][c];
          }
        }
      
      dataUCstr= null;
      dataUCnbr= null;
      
      return(tempData);
    } /* sort rows by data in particular column */
    
    return(null);
  } /* sortSpreadsheet */
  
  
  /**
   * initValues() - show prepDataEdit inside the grid in U.L.H.C.
   * @see SStextArray#getTextField
   */
  void initValues()
  { /* initValues */
    String sv;
    for (int r=1; r<= virRows; r++)
      for (int c= 1; c<= virCols; c++)
      {
        if(r<= prepRows && c<= prepCols)
          sv= prepDataEdit[r][c];
        else
          sv= "";
        cells.getTextField(r,c).setText(sv);
      }
    positionX= 1;
    positionY= 1;
  } /* initValues */
  
  
  /**
   * initValues() - show prepDataEdit inside grid in U.L.H.C. with new data
   * @param data to use
   * @see #initValues
   */
  void initValues(String[][] data)
  { /* initValues */
    prepDataEdit= data;
    initValues();
  } /* initValues */
  
  
  /**
   * setValue_NOTUSED() - set a single value into prepDataEdit area
   */
  /*
   void setValue(int i, int j, String arg)
   {
     cells.getTextField(i,j).setText(arg);
     prepDataEdit[positionX + i -1][positionY + j -1]= arg;
   }
  */
  
  
  /**
   * getValue() - get a single value from prepDataEdit cell(i,j)
   * @param i coordinate of cell
   * @param j coordinate of cell
   * @return cell(i,j) value
   */
  String getValue(int i, int j)
  { return prepDataEdit[i][j]; }
  
  
  /**
   * getValues() - get all values in a String[][] array
   * @return all values of array
   */
  String[][] getValues()
  { return(prepDataEdit); }
  
  
  /* --- methods are used for Scroll bar movements --- */
  
  /**
   * moveColHeader() - move the head of cols use negative ints for moving left.
   * Used with horzontal scrollbar.
   * @param diff is amount to move the column header
   * @return false if out of bounds.
   * @see SStextArray#getTextField
   */
  private boolean moveColHeader(int diff)
  { /* moveColHeader */
    int
      headerIndex= -1,
      c= 0,			/** cols counter in for loop */
      location= 0,		/** index for cols */
      startIndex= 2;
    
    offSet += diff;       /* for url cellFormat array */
    
    for (c= startIndex; c <= virCols; c++)
    { /* get the cell to move  */
      SStextCell dummy= cells.getTextField(1,c);
      
      /* relative position. */
      location= positionY - 1 + c + diff;
      if(location<0)
        continue;  /* protection added PFL 2-22-00*/
      
      if(ssp.virtualHdrPos == location) /* for gray color */
        headerIndex= location - diff;   /* save proper loc for header clicked*/
      
      String newText= prepDataEdit[1][location];
      dummy.setText(newText);
      
      /* force header to black */
      cells.getTextField(1,location).setForeground(Color.black);
      cells.getTextField(1,location).setBackground(ssp.cellReg);
    } /* get the cell to move  */
    
    /* if clicked cell header is within virtual space then display it */
    if(ssp.prepHdrPos>0 && ssp.headerName!=null)
    {
      for(int x=1; x<=virCols; x++)
      {
        if(prepDataOrig[1][ssp.prepHdrPos].compareTo(cells.getTextField(1,x).getText()) == 0)
        {
          cells.getTextField(1,x).setBackground(ssp.headerClicked);
          ssp.virtualHdrPos= x;
        }
        else
          cells.getTextField(1,x).setBackground(ssp.cellReg);
      }
    }
    
    cells.getTextField(1,1).setBackground(ssp.cellReg);
    
    return(true);
  } /* moveColHeader */
  
  
  /**
   * moveRowHeader() - move first cell of rows use negative ints for moving down.
   * @param diff is amount to move the row header
   * @return false if out of bounds.
   */
  private boolean moveRowHeader(int diff)
  { /* moveRowHeader */
    int
      r= 0,
      location= 0;
    
    for (r= 2; r<= virRows;r++)
    { /* relative position X - virRows + difference -1 */
      location= positionX + r + diff - 1;
      if(location<0)
        continue;  /* protection added PFL 2-22-00*/
      
      SStextCell dummy= cells.getTextField(r,1);
      dummy.setText(prepDataEdit[location][1]);
      
      /* force header to black */
      cells.getTextField(location,1).setForeground(Color.black);
    }
    
    return(true);
  } /* moveRowHeader */
  
  
  
  /**
   * moveUp() - move diff cells up
   * @param diff is amount to move the cells up
   * @see SStextArray#getTextField
   * @see #moveRowHeader
   */
  void moveUp(int diff)
  { /* moveUp */
    if (positionX < (prepRows - 1))
    {
      int tempTagRow=0;	/* place holder for old location */
      
      if(!moveRowHeader(diff))	/* move headers */
        return;
      
      tempTagRow= ssp.tagRow;
      ssp.oldTagRow= ssp.tagRow;
      
      status.txtOld= cells.getTextField(ssp.tagRow,ssp.tagCol);
      status.txtOld.setBackground(ssp.cellReg);
      
      ssp.tagRow= ssp.tagRow - diff;
      
      if(ssp.tagRow > 1 && ssp.tagCol > 1)
        cells.getTextField(ssp.tagRow,ssp.tagCol).setBackground(ssp.cellClicked);
      
      cells.getTextField(tempTagRow,ssp.tagCol).setBackground(ssp.cellReg);
      
      /* move data */
      for (int i=2; i<= virRows; i++)
      {
        for (int j= 2; j<= virCols;j++)
        {
          locRow= positionX + i + diff - 1;
          locCol= positionY - 1 + j;
          
          SStextCell dummy= cells.getTextField(i,j);
          dummy.setText(prepDataEdit[locRow][locCol]);
          setCellFontAndColor(locRow,locCol);
          
          /* force header cols black */
          cells.getTextField(locRow,1).setForeground(Color.black);
          cells.getTextField(1,locCol).setForeground(Color.black);
        }
      }
      
      positionX += diff;
    }
  } /* moveUp */
  
  
  /**
   * moveDown() - move diff cells down
   * @param diff is amount to move the cells down
   * @see SStextArray#getTextField
   * @see #moveRowHeader
   */
  void moveDown(int diff)
  { /* moveDown */
    
    int
      tempTagRow= 0,		/* place holder for old location */
      tempTagCol= 0,
      i,			/* for loops */
      j;
    
    if (positionX > 1)
    { /* pos X is not negative  */      
      tempTagRow= ssp.tagRow;
      ssp.oldTagRow= ssp.tagRow;
      
      status.txtOld= cells.getTextField(ssp.tagRow,ssp.tagCol);
      status.txtOld.setBackground(ssp.cellReg);
      
      ssp.tagRow= ssp.tagRow + diff;
      
      if(ssp.tagRow > 1 && ssp.tagCol > 1)
        cells.getTextField(ssp.tagRow,ssp.tagCol).setBackground(ssp.cellClicked);
      cells.getTextField(tempTagRow,ssp.tagCol).setBackground(ssp.cellReg);
      
      /* [1] move headers down */
      if(moveRowHeader(-diff) == true)
      {
        for (i=2; i<= virRows; i++)
          for (j= 2; j<= virCols;j++)
          {
            locRow= positionX + i - diff - 1;
            locCol= positionY - 1 + j;
            
            SStextCell dummy= cells.getTextField(i,j);
            dummy.setText(prepDataEdit[locRow][locCol]);
            setCellFontAndColor(locRow,locCol);
            
            /* force header text to black */
            cells.getTextField(locRow,1).setForeground(Color.black);
            cells.getTextField(1,locCol).setForeground(Color.black);
          }
        positionX -= diff;
      }
      
    } /* pos X is not negative  */
  } /* moveDown */
  
  
  /**
   * moveRight() - move diff cells to the right
   * @param diff is amount to move the cells right
   * @see SStextArray#getTextField
   * @see #moveColHeader
   * @see #setCellFontAndColor
   */
  void moveRight(int diff)
  { /* moveRight */
    int tempTagCol=0;		        /* place holder for old location */
    
    if (positionY <= prepRows)	/* within data fields */
    {
      if(! moveColHeader(diff))	        /* move headers first */
        return;
      
      tempTagCol= ssp.tagCol;
      ssp.oldTagCol= ssp.tagCol;
      
      status.txtOld= cells.getTextField(ssp.tagRow,ssp.tagCol);
      status.txtOld.setBackground(ssp.cellReg);
      
      ssp.tagCol= ssp.tagCol - diff;
      
      if(ssp.tagCol > 1 && ssp.tagRow > 1)
        cells.getTextField(ssp.tagRow,ssp.tagCol).setBackground(ssp.cellClicked);
      cells.getTextField(ssp.tagRow,tempTagCol).setBackground(ssp.cellReg);
      
      
      for (int i=2; i<= virRows; i++)
      {
        for (int j= 2; j<= virCols;j++)
        {
          setCellFontAndColor(i,j);
          locRow= positionX -1 + i;
          locCol= positionY + j + diff-1;
          
          setCellFontAndColor(locRow,locCol);
          SStextCell
          dummy= cells.getTextField(i,j);
          dummy.setText(prepDataEdit[locRow][locCol]);
          
          setCellFontAndColor(locRow,locCol);
          
          /* force header text to black */
          cells.getTextField(locRow,1).setForeground(Color.black);
          cells.getTextField(1,locCol).setForeground(Color.black);
        }
      }
      
      positionY += diff;
    }
  } /* moveRight */
  
  
  /**
   * moveLeft() - move diff cells to the left
   * @param diff is amount to move the cells left
   * @see SStextArray#getTextField
   * @see #moveColHeader
   * @see #setCellFontAndColor
   */
  void moveLeft(int diff)
  {	/* moveLeft */
    int tempTagCol= 0;		/* place holder for old location */
    
    if (positionY > 1)
    {
      if(! moveColHeader(-diff))
        return;
      
      tempTagCol= ssp.tagCol;
      ssp.oldTagCol= ssp.tagCol;
      
      status.txtOld= cells.getTextField(ssp.tagRow,ssp.tagCol);
      status.txtOld.setBackground(ssp.cellReg);
      
      ssp.tagCol= ssp.tagCol + diff;
      
      if(ssp.tagCol > 1 && ssp.tagRow > 1)
        cells.getTextField(ssp.tagRow,ssp.tagCol).setBackground(ssp.cellClicked);
      cells.getTextField(ssp.tagRow,tempTagCol).setBackground(ssp.cellReg);
      
      for (int i=2; i<= virRows; i++)
      {
        for (int j= 2; j<= virCols;j++)
        {
          setCellFontAndColor(i,j);
          locRow= positionX - 1 + i;
          locCol= positionY + j - diff - 1;
          
          if(locCol<0 || locRow<0)
            continue;  /* protection added PFL 2-22-00*/
          
          SStextCell dummy= cells.getTextField(i,j);
          dummy.setText(prepDataEdit[locRow][locCol]);
          setCellFontAndColor(locRow,locCol);
          
          /* force header text to black */
          cells.getTextField(locRow,1).setForeground(Color.black);
          cells.getTextField(1,locCol).setForeground(Color.black);
        }
      }
      
      positionY -= diff;
    }
  } /* moveLeft */
  
  
  /**
   * deleteColumn() - delete the k-th col
   * @param k is column to delete
   * @see #initValues
   */
  void deleteColumn(int k)
  { /* deleteColumn */
    for (int i=1; i< virCols; i++)
    {
      for (int j=k; j < (virRows-1); j++)
      {
        prepDataEdit[i][j]= prepDataEdit[i][j+1];
      }
      prepDataEdit[i][virRows-1]= " ";
    }
    initValues();
  } /* deleteColumn */
  
  
  
  /**
   * insertColumn() - insert a row at k'th row
   * @param k is column to insert
   * @see #initValues
   */
  void insertColumn(int k)
  { /* insertColumn */
    for (int i=1; i<virCols; i++)
    {
      for (int j=2; j <= (virRows-k); j++)
      {
        prepDataEdit[i][virRows-j + 1]= prepDataEdit[i][virRows-j];
      }
      prepDataEdit[i][k]= " " ;
    }
    initValues();
  } /* insertColumn */
  
    
  /**
   * cellAdjust() - take action for mouse clicks on individual cells
   * @param e is MouseEvent
   * @see FileIO#logMsgln
   * @see SStextArray#getTextField
   * @see #moveUp
   * @see #repaint
   * @see #sortSpreadsheet
   */
  void cellAdjust(MouseEvent e)
  { /* cellAdjust */
    boolean cellClickedFlag= false;
    int
      positionI,             /* prepDataEdit[][] coordinate space */
      positionJ;
    String sortedData[][]=null;    /* sort user data */
    SStextCell cell= (SStextCell) e.getSource();
    
    /* [0] initialize  */
    positionI= cell.i;	/* save position of cells */
    positionJ= cell.j;
    cellClickedFlag= true;
    
    /* [1] sort if clicked on upper fields */
    if(positionI == 1 && positionJ != 1)
    { /* sorting */
      String headerName= cell.getText();
      
      for(int x= 1; x< prepCols;x++)
      {
        if(this.prepDataEdit[1][x]!=null)
          if(this.prepDataEdit[1][x].compareTo(headerName)==0)
            headerClickedCol= x;
      }
      
      /* [1.1] set cursor to wait cursor*/
      cell.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      status.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      
      for (int r= 1; r <= virRows; r++)
        for (int c= 1; c <= virCols; c++)
          cells.getTextField(r,c).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      
      /* [1.2] sort the data */
      sortedData= sortSpreadsheet(oldPosition,
                                  headerClickedCol,
                                  prepCols,
                                  prepRows,
                                  this.prepDataEdit);
      /* keep track of old position for asend/desending sorting */
      oldPosition= headerClickedCol;
      
      ssp.virtualHdrPos= positionJ;	    /* shown on spreadsheet */
      ssp.headerName= headerName;      /* header */
      
      /* search for clicked header in prepData and save location */
      for(int x= 1; x< prepCols;x++)
      {
        if(prepDataOrig[1][x].compareTo(headerName)==0)
          ssp.prepHdrPos= x;
      }
      
      if(sortedData!=null)
      {
        /* [1.1] add sorted data to spreadsheet */
        prepDataEdit= sortedData;
        moveUp(0);	/* sort of a repaint so it will display sorted data */
        
        /* [1.2] reset cursors */
        for (int r= 1; r <= virRows; r++)
          for (int c= 1; c <= virCols; c++)
          {
            cells.getTextField(r,c).setBackground(ssp.cellReg);
            cells.getTextField(r,c).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          }
        
        /* set header cell color to gray */
        cells.getTextField(1,positionJ).setBackground(ssp.headerClicked);
        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cell.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        status.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        repaint();
      }
      else
        mae.fio.logMsgln("SSSP-CA: sort Err");
      
    } /* sorting */
    
    /* [2] update status bar with the particular cell in focus */
    status.setText(cell, cells, positionX, positionY, positionI, positionJ,
                   prepDataEdit, cellFormat, dataURL, cellClickedFlag);
    
  } /* cellAdjust */
  
  
  /**
   * mousePressed() - handle mouse Press events
   * @see #cellAdjust
   */
  public void mousePressed(MouseEvent e)
  { cellAdjust(e); }
  
  public void mouseReleased(MouseEvent e)  {}
  public void mouseClicked(MouseEvent e)  {}
  public void mouseMoved(MouseEvent e)  {}
  public void mouseDragged(MouseEvent e)  {}
  public void mouseEntered(MouseEvent e)  {}
  public void mouseExited(MouseEvent e)  {}
  
  
} /* class SSspreadPanel */




/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*      #3           CLASS SSstatusBar                            */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
class SSstatusBar extends Panel
{ /* class SSstatusBar */
  /* panel holding a textfield that displays the value of the grid
   * which has the focus.
   */
  final static String
    gName[]= {null, "A", "B", "C", "D", "E", "F", "G", "H",
              "I","J","K","L","M","N","O","P","Q", "R", "S",
              "T", "U", "V", "W" ,"X", "Y", "Z",
              "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH",
              "AI","AJ","AK","AL","AM","AN","AO","AP","AQ", "AR", "AS",
              "AT", "AU", "AV", "AW" ,"AX", "AY", "AZ"};
   int
     EXCELMAXLABEL= gName.length-1;  /** max number col with alphabet */
   MAExplorer
     mae;
   Label
     coordsNumber;
   SStextCell
     content;
   String
     previousOldContent= null,   /** to prevent double clicking on URLs */
     oldContent,
     prepDataEdit[][];
   int
     virRows,                    /** size of visible window */
     virCols,
     positionX,
     positionY;
   boolean
     urlPopupFlag;		/** popup browser flag  */
   ShowSpreadsheetPopup
     ssp= null;
   SStextCell
     cell= null,
     txt= null,
     txtOld= null;
   
   
   /**
    * SSstatusBar() - constructor, add the components for statusbar set here
    * @param prepDataOrig is user data[0:usrRawData][]
    * @param ssp is instance of ShowSpreadsheetPopup
    * @param mae is instance of MAExplorer
    * @param virRows is # of virtual rows i.e. size of prepDataEdit[][]
    * @param virCols is # of virtual columns i.e. size of prepDataEdit[][]
    * @see SStextCell
    */
   SSstatusBar(ShowSpreadsheetPopup ssp, MAExplorer mae, int virRows, int virCols)
   { /* SSstatusBar */
     super();
     this.mae= mae;
     this.ssp= ssp;
     setLayout(new BorderLayout()); /* BorderLayout works best */
     
     coordsNumber= new Label(" A1  "); /* excel like cell location ie "A1" */
     content= new SStextCell(128);  /* display cell that cursor is on */
     content.setText("       ");
     
     this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
     
     this.virRows= virRows;
     this.virCols= virCols;
     add(coordsNumber, BorderLayout.WEST);
     add(content, BorderLayout.CENTER);
   } /* SSstatusBar */
   
   
   /**
    * cvtCol2Alpha() - set the cells values (int) to alphabet ie A,B,C ... AAA.
    * @param i is column number to convert to letters
    * @return letter or "OVER" if more than EXCELMAXLABEL
    */
   String cvtCol2Alpha(int i)
   { /* cvtCol2Alpha */
     if(i < EXCELMAXLABEL && i > 0)
       return(gName[i]);
     else
       return("OVER");
   } /* cvtCol2Alpha */
   
   
   /**
    * setText() - set the cells coordinate position in the display.statusBar.
    * Also popup browser for URL if enabled.
    * @param txt is instance of SStextCell
    * @param cells is instance of SStextArray
    * @param x is PositionX relative addr within vir. window
    * @param y is PositionY relative addr within vir. window
    * @param i is PositionI ULHC base adr within vir. window
    * @param j is PositionJ ULHC base adr within vir. window
    * @param prepDataEdit
    * @param cellFormat is cell format for field[0:tCols-1]
    * @param dataURL is opt. data for URLs if ! null[0:prepRows-1][0:prepCols-1]
    * @param alterCellColorFlag for changing cell color
    * @see CompositeDatabase#setObjCoordFromMaster_ID
    * @see SStextArray#getTextField
    * @see Util#popupViewer
    * @see #cvtCol2Alpha
    */
   void setText(SStextCell txt, SStextArray cells, int x, int y, int i, int j,
               String[][] prepDataEdit, int cellFormat[],
               String dataURL[][], boolean alterCellColorFlag)
   { /* setText */
     /* [1] Get data for analysis */
     this.prepDataEdit= prepDataEdit;
     
     positionX= txt.dataRow - 1;  /* calc rows */
     positionY= txt.dataCol - 1;  /* calc cols */
     
     cell= txt;
     oldContent= txt.getText(); /* update content of last cell we click on */
     content.setText(oldContent);
     
     int
       dR= x+i-3,   /* in user data space starting from (0,0) */
       dC= y+j-3;   /* data column index */
     
     /* Do bounds checking here */
     if(dC >= 0 && dR >= 0)
     {
       String urlS= oldContent;           /* use the text label */
       
       if(dataURL!=null)
         urlS= dataURL[dR][dC];      /* use the URL */
       
       cell= cells.getTextField(i,j); /* new location */
       
       /* [2] set background color of cell */
       if(alterCellColorFlag)
       { /* background color */
         for (int r= 1; r <= virRows; r++)
           for (int c= 1; c <= virCols; c++)
             cells.getTextField(r,c).setBackground(ssp.cellReg);
         
         if(txtOld!=null)
           txtOld.setBackground(ssp.cellReg);
         txtOld= cell;	/* save next old location */
         
         ssp.oldTagRow= ssp.tagRow;
         ssp.oldTagCol= ssp.tagCol;
         ssp.tagRow= i;
         ssp.tagCol= j;
         
         /* do not change headers */
         if(i == 1 || j == 1)
         {
           cell.setBackground(ssp.cellReg);
           ssp.clickedCellRow= -1;
           ssp.clickedCellCol= -1;
         }
         else
         {
           cell.setBackground(ssp.cellClicked);
           ssp.clickedCellRow= ssp.tagRow;
           ssp.clickedCellCol= ssp.tagCol;
         }
         
       } /* background color */
       
       /* [3] URL address */
       boolean
         isExprProfile= (cellFormat!=null &&
         cellFormat[dC]==SpreadSheet.CELL_EXPR_PROFILE),
         isURL= urlS.startsWith("http://"),
         doPopup= (cellFormat!=null &&
         cellFormat[dC] == SpreadSheet.CELL_URL);
       
       /* [4] Test and do URL popup to prevent double clicking on URLs */
       if(previousOldContent==null || !previousOldContent.equals(oldContent))
       { /* test if popup URL */
         previousOldContent= oldContent;
         
         urlPopupFlag= ssp.urlPopupFlag;   /* popup browser flag  */
         if(isURL && urlPopupFlag)
           mae.util.popupViewer(null, urlS, "MaeAux");
         
        /* If valid mae.masterIDname, then update current xxxOBJ in
         * current ms image.
         */
         int
           colIndx= dR+2,	       /* data index not virtual index */
           rowIndx= dC+2;
         String
           field= prepDataEdit[1][dC+2],  /* field name of column */
           val= prepDataEdit[dR+2][dC+2];

         
         /* set green (blue) circle in MAExplorer window */
         if(field.equals(mae.masterIDname))
           mae.cdb.setObjCoordFromMaster_ID(val);
       } /* test if popup URL */
       
       /* [3] Update status, Excel like display */
       if(positionX > 0 && positionY > 0)
       { 
         /* ignore the headers, start origin (A1)
          * at data instead of headers. Note leading space...
         */
         String
           coordStr= " " + cvtCol2Alpha(dC+1) + (dR+1);
         coordsNumber.setText(coordStr);     /* Cols+rows */
       }
     }
   } /* setText */
   
   
} /* class SSstatusBar */



/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*             #4    CLASS  SStextCell                         */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

class SStextCell extends TextField
{ /* class SStextCell: binary tree cells of TextFields with an String value */
  
  /** virtual coordinate for status bar */
  int
    dataRow,
    dataCol;
  /** grid index  */
  int
    i,
    j;
  SStextCell
    nextRow,
    nextCol;
  
  
  /**
   * SStextCell() - Constructor, inits a TextField of length for all
   * other values 0
   */
  SStextCell()
  { /* SStextCell */
    super();
    this.setText(" ");
    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    nextRow= null;
    nextCol= null;
    dataRow= -1;
    dataCol= -1;
    i= -1;
    j= -1;
  } /* SStextCell */
  
  
  /**
   * SStextCell() - Constructor, inits a TextField of length,
   * all other values 0
   */
  SStextCell(int length)
  { /* SStextCell */
    super(length);
    this.setText(" ");
    this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    nextRow= null;
    nextCol= null;
    dataRow= -1;
    dataCol= -1;
    i= -1;
    j= -1;
  } /* SStextCell */
  
  
} /* class SStextCell */




/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*             #5  CLASS SStextArray                              */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

class SStextArray
{ /* class SStextArray */
  /* class SStextArray: organizing SStextCell classes as a binary tree,
     access in array way */
  
  SStextCell
    root,
    rootDummy,
    merkRow;
  /** size of visible grid */
  int
    virRows,
    virCols;
  SStextCell
    return_it;
  
  
  /**
   * SStextArray() - constructor, creates an (r,c) "array" of SStextCell
   * @param virRows is # of virtual rows i.e. size of prepDataEdit[][]
   * @param virCols is # of virtual columns i.e. size of prepDataEdit[][]
   * @see SStextCell
   */
  SStextArray(int virRows, int virCols)
  {  /* SStextArray */
    this.virRows= virRows;
    this.virCols= virCols;
    root= new SStextCell();
    root.i= 1;
    root.j= 1;
    root.dataRow= 1;
    root.dataCol= 1;
    
    /* rootDummy always holds the root-element of the tree,
     * never ever overwrite!
     */
    rootDummy= root;
    for (int r= 1; r<= virRows; r++)
    {
      merkRow= root;
      for (int c= 2; c <=virCols+1; c++)
      {
        root.nextCol= new SStextCell();
        root.nextCol.i= r;
        root.nextCol.j= c;
        root.dataRow= r;
        root.dataCol= c;
        root= root.nextCol;
      }
      root= merkRow;
      root.nextRow= new SStextCell();
      root.nextRow.i= r;
      root.nextRow.j= 1;
      root= root.nextRow;
    }
    
    /* ok, back to the roots */
    root= rootDummy;
  } /* SStextArray */
  
  
  /**
   * getTextField() - returns the (row,col) SStextCell
   * @param row to access
   * @param col to access
   * @return SStextCell
   */
  SStextCell getTextField(int row, int col)
  { /* SStextCell */
    if (row <= virRows)
      for (int r= 1; r < row; r++)
        root= root.nextRow;
    if (col <= virCols)
      for (int c= 1; c < col; c++)
        root= root.nextCol;
    
    return_it= root;
    root= rootDummy;
    return(return_it);
  } /* SStextCell */
  
} /* class SStextArray */





/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*             #6  CLASS SSscrollHorizontal                       */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
class SSscrollHorizontal extends Scrollbar implements AdjustmentListener
{
  /* a horizontal scrollbar for the SpreadSheet */
  
  SSspreadPanel
    spreadPanel;
  /** upper bounds on scroller */
  int
    upperHorzBounds=0,
    oldHorzValue,
    currentHorzValue;
  
  
  /**
   * SSscrollHorizontal() - a horizontal scrollbar for the SpreadSheet
   * @param spreadPanel instance of horizontal SSspreadPanel
   */
  SSscrollHorizontal(SSspreadPanel spreadPanel)
  {
    super(HORIZONTAL,1,5,1, spreadPanel.prepCols - 2);
    this.spreadPanel= spreadPanel;
    oldHorzValue=0;
    currentHorzValue= this.getValue();
    this.addAdjustmentListener(this);
  }
  
  
  /**
   * setScrollBarPos() - reset scroller bar, not used now
   * @param value to reset horizontal scrollbar
   */
  void setScrollBarPos(int value)
  {
    oldHorzValue= this.getValue();
    this.setValue(value);
  }
  
  
  /**
   * adjustmentValueChanged() - change spreadsheet cell positions via scrollers
   * @param e is scroller adjustment event
   */
  public void adjustmentValueChanged(AdjustmentEvent e)
  { /* adjustmentValueChanged */
    int position= this.getValue(); /* current value of sb */
    
    /*
    mae.fio.logMsg("scrollerH pos ="+position+
                   " virCols="+spreadPanel.virCols+
                   " prepCols= "+ spreadPanel.prepCols);
    mae.fio.logMsgln(" last-col-header= "+
                     spreadPanel.prepDataOrig[1][spreadPanel.prepCols-1]);       
    */
    if(position > currentHorzValue && position < spreadPanel.prepCols)
       spreadPanel.moveRight(position - currentHorzValue);
    else
      if(position < currentHorzValue && position < spreadPanel.prepCols)
        spreadPanel.moveLeft(currentHorzValue - position);
    
    
    if(position >= spreadPanel.prepCols)
      this.setValue(currentHorzValue);         /* same - do nothing */
    else
    {
      currentHorzValue= position;
      this.setValue(position);
    }
  } /* adjustmentValueChanged */
  
  
} /* class SSscrollHorizontal */





/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*          #7     CLASS  SSscrollVertical                        */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
class SSscrollVertical extends Scrollbar implements AdjustmentListener
{ /*  a vertical scrollbar for the SpreadSheet */
  SSspreadPanel
    spreadPanel;
  /** upper bounds on scroller */
  private int
    upperVertBounds=0;
  /** keep track of oldvalue */
  private int
    oldVertValue=0,
    curVertValue;
  
  
  /**
   * SSscrollVertical() - a vertical scrollbar for the SpreadSheet
   * @param spreadPanel instance of vertical SSspreadPanel
   */
  SSscrollVertical(SSspreadPanel spreadPanel)
  {
    super(VERTICAL,1,10,1,spreadPanel.prepRows+1);
    this.spreadPanel= spreadPanel;
    curVertValue= this.getValue();
    oldVertValue=0;
    this.addAdjustmentListener(this);
  }
  
  
  /**
   * setScrollBarPosition() - reset scroller bar, not used now
   * @param value to reset horizontal scrollbar
   */
  void setScrollBarPosition(int value)
  {
    oldVertValue= this.getValue();
    this.setValue(value);
  }
  
  
  /**
   * adjustmentValueChanged() - change spreadsheet cell positions via scrollers
   * @param e is adjustment event
   * @see SSspreadPanel#moveDown
   * @see SSspreadPanel#moveUp
   */
  public void adjustmentValueChanged(AdjustmentEvent e)
  {
    int position= this.getValue();
    
    /*
    mae.fio.logMsg("scrollerV pos ="+position +
                   " virRows="+spreadPanel.virRows+
                   " prepRows= "+ spreadPanel.prepRows);       
    mae.fio.logMsgln(" last row header= "+
                     spreadPanel.prepDataOrig[spreadPanel.prepRows-1][1]);
    */
    
    /* determine if going up or going down */
    if ((position > curVertValue) && (position < spreadPanel.prepRows))
      spreadPanel.moveUp(position - curVertValue);
    else
      if ((position < curVertValue) && (position < spreadPanel.prepRows))
        spreadPanel.moveDown(curVertValue - position);
    
    if(position >= spreadPanel.prepRows)
      this.setValue(curVertValue);
    else
    {
      curVertValue= position;
      this.setValue(position);
    }
  } /* adjustmentValueChanged */
  
} /* class SSscrollVertical */


/* end of file SpreadSheet.java */

