/** File: PopupProjDirGuesser.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.TextField;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.*;

/**
 * The PopupProjDirGuesser class creates a popup project directory entry guesser.
 * This is an alphabetic-sorted guesser for all project directory items
 * which is invoked from the (File | Set project) menu selection.
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
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Guesser
 */

class PopupProjDirGuesser extends Guesser
{ /* class PopupProjDirGuesser */
  /** flag: set once data is sorted */
  static boolean
    strDataIsSortedFlag= false;
     
  /** fixed prompt */
  final static String
    guesserTitle= "Enter project name"; 
    
  /** default row size of text area */ 
  final static int
    DEF_PD_TA_ROWS= 10;
  /** default col size of text area */   
  final static int
    DEF_PD_TA_COLS= 40;
  
  /* -- Guesser related data - note other arrays live in Guesser.java -- */
  /** size of unsorted list of proj Dir entries */
  static int		
    projDirListSize;		
  /** unsorted projDir names list, is G.C.'ed */
  static String 
    projDirItemList[]; 
  /** unsorted projDir cmd list, is G.C.'ed */
  static String 
    projDirPathItemList[];	 
  /** sorted list of HP menu name */
  static String
    sortedProjDirItemListStr;   
  
  /** working row size of text area */
  int
   taRows= DEF_PD_TA_ROWS;
  /** working column size of text area */
  int
   taCols= DEF_PD_TA_COLS;
  
  /** "Project-Name Project-Dir" table */
  Table
    prjTbl;  
        
  /** # of non-null Table rows */             
  int
    nRows;                                
  /** Table field: "Project-Name" */ 
  int
    idxPrjName;                          
  /** Table field: "Project-Directory" */
  int
    idxPrjDir;                
      
      
  /**
   * PopupProjDirGuesser() - constructor for project director guesser
   * which assumes the HP menu item list is already sorted when it is created
   * during MAExplorer initialization.
   * @param maE is instance of MAExplorer
   * @param chFirstTyped is first character typed if want to preload the search
   * @param font for text area
   * @param prjTbl is the project table to use
   * @see Table#lookupFieldIdx
   * @see SortMAE#uniqueInsertionSort
   * @see Util#cvtStrArrayToStr
   * @see #buildAndRunGuesser
   * @see #setData
   * @see #setupProjDirItemList
   */
  PopupProjDirGuesser(MAExplorer maE, char chFirstTyped, Font font, Table prjTbl)
  { /* PopupProjDirGuesser */
    super(maE, true, /* use since list generic guesser */
          false /* setEGLfromListFlag */  );
    mae= maE;
    this.prjTbl= prjTbl;
    pPDg= this;                    /* make Guesser active */
    
    idxPrjName= prjTbl.lookupFieldIdx("Project-Name");
    idxPrjDir= prjTbl.lookupFieldIdx("Project-Directory");
    if(idxPrjName==-1 || idxPrjDir==-1)
      return;  /* failed */
    else
    { /* Last row is null */
      nRows= --prjTbl.tRows;
    }
    
    /* Setup list of project name items for Guesser by copying
     * the Project-Name item data into the working projDirItemList[]
     * with sizes projDirItemListSize.
     */
    if(projDirItemList==null)
      setupProjDirItemList();
    
    if(font==null)
      font= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
    
    setData(chFirstTyped, sortedProjDirItemListStr, projDirItemList,
    projDirListSize, taRows, taCols, guesserTitle, font);
    
    buildAndRunGuesser("Project Guesser");   /* create Guesser */
  } /* PopupProjDirGuesser */
  
  
  /**
   * setupProjDirItemList() - set lists of project Dir items for Guesser.
   * It copies the data into the working projDirItemList[] with size
   * projDirItemListSize. It also removes the null entries.
   */
  private void setupProjDirItemList()
  { /* setupProjDirItemList */
    String pdPostfixStr= mbf.projDirToUpdateFromGuesser;
    
    /* [1] copy HP data to working guesser lists */
    projDirListSize= 0;         /* sizes */
    String
      projDirItemList0[]= new String[nRows+1],     /* tmp arrays*/
      projDirPathItemList0[]= new String[nRows+1], /* shorten later */
      projDirItemList0UC[]= new String[nRows+1];   /* upper case for sorting*/
    
    for(int p= 0; p < nRows; p++)
    { /* setup Stage names */
      String
        pName= prjTbl.tData[p][idxPrjName],
        pDir= prjTbl.tData[p][idxPrjDir];
      
      /* Add only if unique */
      projDirListSize= SortMAE.uniqueInsertionSort(projDirItemList0,
                                                   projDirPathItemList0,
                                                   projDirItemList0UC,
                                                   projDirListSize,
                                                   pName, pDir);
    } /* process HP menu items */
    
    /* [2] Shorten arrays to the exact length needed by guesser */
    projDirItemList0UC= null;           /* G.C. */
    projDirItemList= new String[projDirListSize];
    System.arraycopy(projDirItemList0,0,projDirItemList, 0,projDirListSize);
    projDirItemList0= null;             /* G.C. */
    
    projDirPathItemList= new String[projDirListSize];
    System.arraycopy(projDirPathItemList0,0,projDirPathItemList, 0,projDirListSize);
    projDirPathItemList0= null;         /* G.C. */
    
    /* [3] Sort lists then convert to string with "\n" delimiters */
    sortedProjDirItemListStr= Util.cvtStrArrayToStr(projDirItemList);
  } /* setupProjDirItemList */
  
  
  /**
   * cancel() - cancel the guesser window
   */
  void cancel()
  { /* cancel */
    mbf.projDirGuesserIsPoppedUp= false;
    mbf.projDirGuesser= null;
    frame.dispose();             /* close guesser window */
  } /* cancel */
  
  
  /**
   * done() - process the selected text and then popdown the guesser
   * @param resultStr is the result string to update the table.
   * @see EventMenu#changeProjDir
   * @see MAExplorer#repaint
   * @see Table#lookupFieldIdx
   * @see #cancel
   */
  void done(String resultStr)
  { /* done */
    int
      idxPrjName= prjTbl.lookupFieldIdx("Project-Name"),
      idxPrjDir= prjTbl.lookupFieldIdx("Project-Directory");
    String
      pdbName,
      pName= null,      /* assume failed */
      pDir= null;
    
    for(int i=0;i<nRows;i++)
    { /* search for matching project */
      pdbName= prjTbl.tData[i][idxPrjName];
      if(pdbName.equals(resultStr))
      { /* found it - change directory*/
        pName= pdbName;
        pDir= prjTbl.tData[i][idxPrjDir];
        break;
      } /* found it - change directory*/
    } /* search for matching project */
    
    prjTbl= null;
    mae.em.changeProjDir(pName, pDir);
    mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    mae.repaint();
    cancel();          /* close guesser window */
  } /* done */
  
  
  /**
   * clear() - clear the text field in the main window
   */
  void clear()
  { /* clear */
  } /* clear */
  
  
  /**
   * updateAssocTextField() - update the associated text field
   * @param str is string to update the associated text field
   */
  void updateAssocTextField(String str)
  { /* updateAssocTextField */
  } /* updateAssocTextField */
  
  
  /**
   * updateAssocTextFieldCaretPos() - update assoc text field caret position
   * @param pos is associated text field caret position
   */
  void updateAssocTextFieldCaretPos(int pos)
  { /* updateAssocTextFieldCaretPos */
  } /* updateAssocTextFieldCaretPos */
  
  
} /* end of class PopupProjDirGuesser */
