/** File: PopupHPmenuGuesser.java */

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
 * The PopupHPmenuGuesser class popups up a sample name guesser.
 * It is used to specify a sample using a text guesser rather than from
 * a pull-down menu. The alphabetic-sorted data is the same as for the
 * HP Samples menus. The guesser is invoked from the 
 * (Samples | ... | All HP samples list) selection from the various
 * sub menus. The guesser may be useful if there are a large number of
 * samples to easily use the HP sample pull-down menu to select them.
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

class PopupHPmenuGuesser extends Guesser
{ /* class PopupHPmenuGuesser */
  /** Flag that is set once it is sorted */
  static boolean
   strDataIsSortedFlag= false;   
  /** title of guesser */
  final static String
    guesserTitle= "Enter hybridized sample name"; 
  /* default size of text area */
  final static int
    DEF_HP_TA_ROWS= 15; 
  /* default size of text area */
  final static int           
    DEF_HP_TA_COLS= 25;
  
  /* --- Guesser related data - note other arrays live in Guesser.java --- */
  /** unsorted list of HP menu size */
  static int		
    hpMenuItemListSize;	
  /** unsorted HP names list, is G.C.'ed */ 
  static String 
    hpMenuItemList[];
  /** unsorted HP cmd list, is G.C.'ed */ 
  static String 
    hpCmdItemList[];
  /** sorted list of HP menu name */
  static String
    sortedHPmenuItemListStr;    
  
  /** default size of text area */
  int
   taRows= DEF_HP_TA_ROWS;
  /** default size of text area */
  int
   taCols= DEF_HP_TA_COLS;
  
  
  /**
   * PopupHPmenuGuesser() - constructor assumes the HP menu item list is already sorted
   * when it is created during MAExplorer initialization.
   * @param mAE is instance of MAExplorer
   * @param chFirstTyped is first character typed if want to preload the search
   * @param font for text area
   * @see #buildAndRunGuesser
   * @see #setData
   * @see #setupHPmenuItemList
   */
  PopupHPmenuGuesser(MAExplorer mAE, char chFirstTyped, Font font)
  { /* PopupHPmenuGuesser */
    super(mAE, true, /* use since list generic guesser */
    false /* setEGLfromListFlag */  );
    mae= mAE;
    phpmg= this;    /* make active */
    
    /* Setup list of HP menu item list for Guesser by copying
     * the HP menu item data into the working hpMenuItemList[]
     * with sizes hpMenuItemListSize.
     */
    if(hpMenuItemList==null)
      setupHPmenuItemList();
    
    if(font==null)
      font= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
    
    setData(chFirstTyped, sortedHPmenuItemListStr, hpMenuItemList,
    hpMenuItemListSize, taRows, taCols, guesserTitle, font);
    
    buildAndRunGuesser("Hybridization Sample Guesser"); /* create Guesser */
  } /* PopupHPmenuGuesser */
  
  
  /**
   * setupHPmenuItemList() - set lists of HP menu items for Guesser.
   * It copies the data into the working hpMenuItemList[] with size
   * hpMenuItemListSize. It also removes the null entries.
   * @see SortMAE#uniqueInsertionSort
   * @see Util#cvtStrArrayToStr
   */
  private void setupHPmenuItemList()
  { /* setupHPmenuItemList */
    String hpPostfixStr= mbf.hpMenuToUpdateFromGuesser;
    
    /* [1] copy HP data to working guesser lists */
    hpMenuItemListSize= 0;         /* sizes */
    String
      hpMenuItemList0[]= new String[mae.sg.nHPmnuEntries],
      hpCmdItemList0[]= new String[mae.sg.nHPmnuEntries],    /* shorten later */
      hpMenuItemList0UC[]= new String[mae.sg.nHPmnuEntries]; /* upper case 
                                                              * for sorting*/
    
    for(int hpIdx= 0; hpIdx < mae.sg.nHPmnuEntries; hpIdx++)
    { /* setup Stage names */
      StageNames sn= mae.sg.HPmnuEntry[hpIdx];
      String
        cmdLabel= hpPostfixStr + mae.snHPName[sn.hpNbr],
        menuLabel= sn.stageLabel;
      
      if(menuLabel!=null && menuLabel.length() > 0)
      { /* Add only if unique */
        hpMenuItemListSize= SortMAE.uniqueInsertionSort(hpMenuItemList0,
                                                        hpCmdItemList0,
                                                        hpMenuItemList0UC,
                                                        hpMenuItemListSize,
                                                        menuLabel, cmdLabel);
      }
    } /* process HP menu items */    
    
    /* [2] Shorten arrays to the exact length */
    hpMenuItemList0UC= null;             /* G.C. */
    hpMenuItemList= new String[hpMenuItemListSize];
    System.arraycopy(hpMenuItemList0,0,hpMenuItemList, 0,hpMenuItemListSize);
    hpMenuItemList0= null;              /* G.C. */
    
    hpCmdItemList= new String[hpMenuItemListSize];
    System.arraycopy(hpCmdItemList0,0,hpCmdItemList, 0,hpMenuItemListSize);
    hpCmdItemList0= null;               /* G.C. */    
    
    /* [3] Sort lists then convert to string with "\n" delimiters */
    sortedHPmenuItemListStr= Util.cvtStrArrayToStr(hpMenuItemList);
  } /* setupHPmenuItemList */
  
  
  /**
   * cancel() - cancel the HP sample guesser window
   */
  void cancel()
  { /* cancel */
    mbf.hpMenuGuesserIsPoppedUp= false;
    mbf.hpMenuGuesser= null;
    frame.dispose();             /* close guesser window */
  } /* cancel */
  
  
  /**
   * done() - process the selected text and then popdown the guesser
   * @param resultStr is the result string to update the table.
   * @see MAExplorer#repaint
   * @see EventMenu#processSetHP
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see Util#showMsg3
   */
  void done(String resultStr)
  { /* done */
    if(resultStr.length()>0)
    { /* change the sample for the selected HP menu type */
      String actCmd= null;
      for(int i=0;i<hpMenuItemListSize;i++)
        if(resultStr.equals(hpMenuItemList[i]))
        { /* find corresponding action entry */
          actCmd= hpCmdItemList[i];
          break;
        }
      
      /* Check full hybridized sample name "H.P...:..." */
      if(actCmd!=null && EventMenu.processSetHP(actCmd))
      {
        Util.showMsg("Finished loading new current sample");
        Util.showMsg2( " " );
        Util.showMsg3( " " );
        mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        mae.repaint();   /* only repaint if successufl! */
      }
    }
    
    mbf.hpMenuGuesserIsPoppedUp= false;
    mbf.hpMenuGuesser= null;
    frame.dispose();  /* close guesser window */
  } /* done */
  
  
  /**
   * clear() - clear the text field in the main window
   */
  void clear()
  { /* clear */
  } /* clear */
  
  
  /**
   * updateAssocTextField() - update the associated text field
   * @param resultStr is the result string to update the table.
   */
  void updateAssocTextField(String str)
  { /* updateAssocTextField */
  } /* updateAssocTextField */
  
  
  /**
   * updateAssocTextFieldCaretPos() - update assoc text field caret position.
   * @param pos is associated text field caret position
   */
  void updateAssocTextFieldCaretPos(int pos)
  { /* updateAssocTextFieldCaretPos */
  } /* updateAssocTextFieldCaretPos */
  
  
} /* end of class PopupHPmenuGuesser */
