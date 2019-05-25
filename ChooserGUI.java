/** File: ChooserGUI.java */

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.awt.TextField;
import java.awt.TextArea;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.*;

/**
 * The ChooserGUI class is a base class for creating chooser GUIs.
 * It creates Remainder/Selected text area(s) for selecting items 
 * from possibly paired lists. 
 *
 *<PRE>
 * Notes: 
 * 1. TextAreas (ta) include scroll bars and the sizes can be
 *    changed.  Set one size for now. 
 * 2. There is a global font size for all text in text areas and labels. 
 * 3. Data returned: callee gets from remObjList[] & selObjList[] 
 *    with remObjListSize & selObjListSize being the sizes of final 
 *    data arrays.
 * 4. The OK RESET CANCEL buttons are implemented by callee module.
 *
 *  The following is the basic chooserGUI layout
 *
 *                                    (optional)
 *  |-----------|       |----------|  |--------|
 *  | ta1       |   ->  | ta2      |  |  up    |
 *  | REMAINDER |   <-  | SELECTED |  |        |
 *  | list      |   >>  | list     |  |        | 
 *  | LEFT      |   >>  | RIGHT    |  | down   |
 *  |-----------|       |----------|  |--------|
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.10 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see PopupHPChooser
 */


class ChooserGUI extends Panel implements ActionListener
{ /* ChooserGUI */
  /* [TODO]
   *  1. check for duplicates when adding to ta2
   */
    
  /** link to global MAExplorer instance */
  private static MAExplorer
    mae;       
        
  /** increments for each instance */
  private static int
    masterID= 0;                
  /** id # for this instance */
  int
    id;                         
    
  /** if can move SELECTED entry up/down*/
  boolean
    addChangePosFlag;           
  /** true if using other prj data/ta */
  boolean
    remoteFlag;			
    
  /** handle mouse events for text areas for ta1 LEFT */
  private MouseEventHandler   
    meh1;			
  /** handle mouse events for text areas for ta2 RIGHT */
  private MouseEventHandler
    meh2;
  
  /** REMAINDER objs */
  TextArea	
    ta1;
  /** SELECTED obj list */
  TextArea				
    ta2;			
  
  /** mv Remainder entry to Selected */
  private Button			
    addButton;                  
  /** mv Selected entry to Remainder */
  private Button
    delButton;                  
  /** mv ALL Remainder entries to Selected */
  private Button
    addAllButton;               
  /** mv ALL Selected entries to Remainder */
  private Button
    rmvAllButton;               
  /** move SELECTED entry up if possible */
  private Button
    upButton;                   
  /** move SELECTED entry down if possible */
  private Button
    downButton;                 
    
  /* --- Place items within panels for better gui control --- */
  /** control panel for main ok/reset buttons*/
  private Panel      
    controlPanel;               
  /** buttons panel */
  private Panel      
    buttonPanel;               
  /** project prj ta */
  private Panel      
    prjListPanel;               
  /** set prj button */
  private Panel      
    objButtonPanel;             
  /** obj list ta1 */
  private Panel      
    objListTApanel;             
 /** panel with up/down buttons */
  private Panel      
    upDownPanel;                
  /** add,del,all buttons */
  private Panel      
    addDelAllButtonPanel;       
  /** SELECTED list */
  private Panel      
    selectedPanel;              
  /** change up/down position panel */
  private Panel      
    changePosPanel;             
     
  /** label under ta1 */
  private Label
    ta1Label;                   
  /** label  under ta2 */
  private Label
    ta2Label;                   
  /** optional label if not to use ta1 from above */
  private Label
    useAbove;                   
  /** label for title for entire GUI window */ 
  private Label
    lTitle;                     
  
  /** rows/text area */
  int
    taRows;                     
  /** columns/text area */
  int
    taCols;                     
  /** REMAINDER list size */
  int
    origRemObjsSize;	        
  /** SELECTED list size */
  int
    origSelObjsSize;	        
  /** REMAINDER list size */
  int
    remObjListSize;	   	
  /** SELECTED list size */
  int
    selObjListSize;		
    
  /** title for entire GUI window */
  String
    title;                        
  /** label for SELECTED LEFT text area */
  String
    taStr1;                       
  /** label for REMAINDER RIGHT text area */ 
  String
    taStr2;                      
  /** original REMAINDER list */ 
  String
    origRemObjs[];	         
  /** original SELECTED list */
  String
    origSelObjs[];		  
  /** REMAINDER list, returned if needed */ 
  String
    remObjList[];
  /** SELECTED list to be returned */
  String
    selObjList[];		
  
  /** This is either the remote instance or 'this' instance of ChooserGUI 
   * for the instance of REMAINDER to use.
   */
  ChooserGUI
    cGUI;                       
  
  /** font used by title */   
  Font
    titleFont;
  /** font used by label */   
  Font
    labelFont;
  /** font used by data */   
  Font
    dataFont;
    
  /** main GUI panel */
  private Panel
    cGUIpanel;                  
  /** parent window */
  private Object 
    parentWindow;               



  /**
   * ChooserGUI() - constructor for base class of Chooser GUI
   * @param mae is instance of MAExplorer
   * @param remoteC_GUI is the alternate ChooserGUI to use if share REMAINDER
   *        else null
   * @param taRows is the # rows visible in scroller
   * @param taColsis the # columns visible in scroller
   * @param titleFont is the top level title Font
   * @param labelFont is the LEFT and RIGHT titles Font
   * @param dataFont is the Text Areas Font
   * @param parentWindow is the parent window
   * @param addChangePosFlag is flag to move entry position in SELECTED window
   */
  ChooserGUI(MAExplorer mae, ChooserGUI remoteC_GUI, int taRows, int taCols,
             Font titleFont, Font labelFont, Font dataFont,
             Object parentWindow, boolean addChangePosFlag )
  { /* ChooserGUI */
    this.mae= mae;
    
    id= ++masterID;             /* instance ID  for debugging */
    
    remoteFlag= (remoteC_GUI!=null);
    cGUI= (remoteFlag)
             ? remoteC_GUI
             : this;             /* where to get REMAINDER */
    
    this.taRows= taRows;
    this.taCols= taCols;
    this.titleFont= titleFont;
    this.labelFont= labelFont;
    this.dataFont= dataFont;
    this.parentWindow= parentWindow;
    this.addChangePosFlag= addChangePosFlag;
  } /* ChooserGUI */
  
  
  /**
   * setTitles() - set titles for REMAINDER and SELECTED text areas
   * @param taStr1 is the REMAINDER caption
   * @param taStr2 is the SELECTED caption
   */
  void setTitles(String taStr1, String taStr2)
  { /* setTitles */
    this.taStr1= taStr1;
    this.taStr2= taStr2;
  } /* setTitles */
  
  
  /**
   * setListData() - copy original data for REMAINDER and SELECTED lists.
   * Note: can use offset 'off' to copy data from [1:n] arrays into
   * the origXXXObjs[0:n-1] arrays.int
   * @param maxObjs is the max # objects
   * @param offset 1 if [1:n] else 0
   * @param nRem is the # REMAINDER objects
   * @param nSel is # SELECTED objects
   * @param remData is [off:nRem+off-1] REMAINDER
   * @param selData is [off:nRem+off-1] SELECTED
   * @see #copyOrigToWorkingData
   */
  void setListData(int maxObjs, int offset, int nRem, int nSel,
                   String remData[], String selData[] )
  { /* setListData */
    /* [1] Make copy of original data taking array offset into account */
    if(cGUI==this)
    {
      this.origRemObjs= new String[maxObjs];
      for(int i=0; i<nRem; i++)
        this.origRemObjs[i]= remData[i+offset];
      this.origRemObjsSize= nRem;
    }
    
    this.origSelObjs= new String[maxObjs];
    for(int i=0; i<nSel; i++)
      this.origSelObjs[i]= selData[i+offset];
    this.origSelObjsSize= nSel;
    
    /* [2] Copy orig to working data and counters */
    copyOrigToWorkingData();
  } /* setListData */
  
  
  /**
   * buildChooserGUI() - create single instance GUI for Chooser
   * @see MouseEventHandler
   */
  void buildChooserGUI()
  { /* buildChooserGUI */
    String
      add= "Add >",		/* button label */
      del= "< Del",		/* button label */
      addAll= ">>",		/* button labels */
      rmvAll= "<<",	        /* button labels */
      whiteSpace= " ",	/* for space between GUI objs */
      selObjListStr= "",	/* string vers of list of final objs */
      remObjListStr= "";	/* string vers of list of objs */
    
    /* [1] Allocate components */
    addButton= new Button(add); /* buttons  */
    delButton= new Button(del);
    addAllButton= new Button(addAll);
    rmvAllButton= new Button(rmvAll);
    
    /* Place items within panels for better gui control */
    controlPanel= new Panel(new BorderLayout());  /* main ok/reset buttons*/
    buttonPanel= new Panel(new GridBagLayout());  /* buttons */
    prjListPanel= new Panel(new BorderLayout());  /* prj ta */
    objButtonPanel= new Panel(new BorderLayout()); /* set prj button*/
    objListTApanel= new Panel(new BorderLayout()); /* obj list ta1 */
    addDelAllButtonPanel= new Panel(new BorderLayout()); /* add,del,all buttons */
    selectedPanel= new Panel(new BorderLayout());  /* SELECTED list */
    
    ta1Label= new Label(taStr1);              /* under ta1  */
    ta2Label= new Label(taStr2);              /* under ta2 */
    useAbove= new Label("Use above samples"); /* not to use ta1 from above */
    lTitle= new Label(cGUI.title);            /* title for entire GUI window */
    
    /* [1.1] Setup fonts etc */
    lTitle.setFont(titleFont);
    ta1Label.setFont(labelFont);
    ta2Label.setFont(labelFont);
    useAbove.setFont(labelFont);
    this.setLayout(new BorderLayout()); /* main panel is the class */
    
    /* [2] Convert REMAINDER List to string for (LEFT) ta1 */
    if(!remoteFlag)
    { /* create TextArea ta1 only for non-remote cGUIs */
      for(int x=0; x<origRemObjsSize; x++)
        remObjListStr += origRemObjs[x] + "\n";
      ta1= new TextArea(taRows,taCols);
      ta1.setText(remObjListStr);
    }
    
    /* [3] Convert SELECTED List to string for (RIGHT) ta2 */
    for(int x=0; x<origSelObjsSize; x++)
      selObjListStr += origSelObjs[x] + "\n";
    ta2= new TextArea(cGUI.taRows, cGUI.taCols);
    ta2.setText(selObjListStr);
    
    /* [4] Create seperate mouse listeners for TextAreas */
    if(!remoteFlag)
    { /* only listen to non-remote REMAINDER cGUI textArea ta1 */
      meh1= new MouseEventHandler(mae, this, ta1, origRemObjs,
      origRemObjsSize);
      ta1.addMouseListener(meh1);
      ta1.setEditable(false);
    }
    
    meh2= new MouseEventHandler(mae, this, ta2, origSelObjs,
    origSelObjsSize);
    ta2.addMouseListener(meh2);
    ta2.setEditable(false);
    
    /* [5] Add command Buttons and their listeners */
    addButton.setActionCommand("add");
    delButton.setActionCommand("del");
    addAllButton.setActionCommand("addAll");
    rmvAllButton.setActionCommand("rmvAll");
    
    addButton.addActionListener(this);
    delButton.addActionListener(this);
    addAllButton.addActionListener(this);
    rmvAllButton.addActionListener(this);
    
    if(addChangePosFlag)
    { /* add controls to move entrys up or down in selected items*/
      changePosPanel= new Panel(new GridLayout(3,1)); /* up/down */
      upButton= new Button("Up");
      downButton= new Button("Down");
      upButton.addActionListener(this);
      downButton.addActionListener(this);
      changePosPanel.add(upButton);
      changePosPanel.add(downButton);
    }
    
    cGUI.ta1.setBackground(Color.lightGray);
    ta2.setBackground(Color.lightGray);
    
    addButton.setBackground(Color.lightGray);
    delButton.setBackground(Color.lightGray);
    addAllButton.setBackground(Color.lightGray);
    rmvAllButton.setBackground(Color.lightGray);
    
    addButton.setFont(labelFont);
    delButton.setFont(labelFont);
    addAllButton.setFont(labelFont);
    rmvAllButton.setFont(labelFont);
    
    /* [6] For remote REMAINDER fill space normally used by ta1 TextArea */
    if(!remoteFlag)
    {
      ta1.setFont(dataFont);
      objListTApanel.add((new Label(" ")), BorderLayout.NORTH);
      objListTApanel.add(ta1, BorderLayout.CENTER);
      objListTApanel.add(ta1Label, BorderLayout.SOUTH);
    }
    
    /* [7] Put set of three buttons, place into another gridbag */
    addObjGridBagLayout(buttonPanel, addButton ,0,0,1,1,1.0,1);
    addObjGridBagLayout(buttonPanel, delButton ,0,1,1,1,1.0,1);
    addObjGridBagLayout(buttonPanel, addAllButton ,0,2,1,1,1.0,1);
    addObjGridBagLayout(buttonPanel, rmvAllButton ,0,3,1,1,1.0,1);
    addDelAllButtonPanel.add(buttonPanel, BorderLayout.CENTER);
    
    /* [8] Add SELECTED ta2 RIGHT ta2 text area */
    ta2.setFont(dataFont);
    selectedPanel.add((new Label(" ")), BorderLayout.NORTH);
    selectedPanel.add(ta2, BorderLayout.CENTER);
    selectedPanel.add(ta2Label, BorderLayout.SOUTH);
    
    
    /* [9] Place everything into main gridBagLayout panel */
    cGUIpanel= new Panel(new GridBagLayout());  /* main panel */
    
    addObjGridBagLayout(cGUIpanel,whiteSpace,0,0,1,1,1.0,1); /* alignment */
    
    if(!remoteFlag)           /* add LEFT Remainder project list ta1 */
      addObjGridBagLayout(cGUIpanel,prjListPanel,1,0,1,1,1.0,1);
    
    addObjGridBagLayout(cGUIpanel,whiteSpace,2,0,1,1,1.0,3); /* alignment */
    
    if(!remoteFlag)          /* set button */
      addObjGridBagLayout(cGUIpanel,objButtonPanel,3,0,1,1,1.0,1);
    else
      addObjGridBagLayout(cGUIpanel,useAbove,3,0,1,1,1.0,1);
    
    addObjGridBagLayout(cGUIpanel,whiteSpace,4,0,1,1,1.0,1);  /* alignment */
    
    if(!remoteFlag)
      addObjGridBagLayout(cGUIpanel,objListTApanel,5,0,1,1,1.0,1); /* obj list ta */
    
    addObjGridBagLayout(cGUIpanel,whiteSpace,6,0,1,1,1.0,1);  /* alignment */
    addObjGridBagLayout(cGUIpanel,addDelAllButtonPanel,7,0,1,1,1.0,1); /* add,del,>> */
    addObjGridBagLayout(cGUIpanel,whiteSpace,8,0,1,1,1.0,1);  /* alignment */
    
    /* Add the RIGHT selected objs ta */
    if(!addChangePosFlag)
      addObjGridBagLayout(cGUIpanel,selectedPanel,9,0,1,1,1.0,1);
    else
    { /* put selectedPanel and changePosPanel in new panel */
      Panel complexSelPanel= new Panel();
      complexSelPanel.add("West", selectedPanel); /* RIGHT selected objs ta*/
      complexSelPanel.add("Center", changePosPanel);
      addObjGridBagLayout(cGUIpanel,complexSelPanel,9,0,1,1,1.0,1);
    }
    
    addObjGridBagLayout(cGUIpanel,whiteSpace,10,0,1,1,1.0,1); /* alignment */
    
      /* [9.1] [TODO] if addChangePosFlag, then add changePosPanel
       * to right of ta2.
       */
    
    /* [10] now display everything in frame */
    this.add(cGUIpanel,BorderLayout.EAST);     /* add to frame */
    this.add(lTitle,BorderLayout.WEST);
    this.add(controlPanel,BorderLayout.SOUTH); /* add to bottom */
  } /* buildChooserGUI */
  
  
  /**
   * addObjGridBagLayout() - add object to GridBagLayout using x,y grid system.
   * See GridBagConstriants in Patrick Chans book p.738.
   * @param cont is panel, window, etc
   * @param arg is Label/Panel
   * @param x is x coord  location
   * @param y is y coord location
   * @param w is cell width
   * @param h is cell height
   * @param weightX is row weight
   * @param weightY is col weight
   */
  void addObjGridBagLayout(Container cont, Object arg,
                           int x, int y, int w, int h,
                           double weightX, double weightY)
  { /* addObjGridBagLayout */
    GridBagLayout gbl= (GridBagLayout) cont.getLayout();
    GridBagConstraints c= new GridBagConstraints();
    Component comp;
    
    /* [1] set constriants */
    c.fill= GridBagConstraints.HORIZONTAL;
    c.gridx= x;
    c.gridy= y;
    c.gridwidth= w;
    c.gridheight= h;
    c.weightx= weightX;
    c.weighty= weightY;
    
    /* [2] if str then create label else use Component */
    if(arg instanceof String)
      comp= new Label((String)arg);
    else
      comp= (Component)arg;
    
    cont.add(comp);
    gbl.setConstraints(comp, c);
  } /* addObjGridBagLayout */
  
  
  /**
   * addObjs() - move object to (remainder) TA1 from (selected) TA2.
   * @param objName to add
   * @see #updateBothTextAreas
   */
  void addObjs(String objName)
  { /* addObjs */
    int remTmpSize=  0;      /* keep track of size of list */
    String
      isObj,
      newRemObjList[]= new String[mae.MAX_HYB_SAMPLES]; /* temp */
    
    /* [1] Protects against click-happy people if nothing picked. */
    if(objName==null || cGUI.remObjListSize<=0)
      return;	                       /* nothing was selected */
    
    /* [2] Make new REMAINDER list less objName */
    for(int x=0; x<cGUI.remObjListSize; x++)
    { /* add all REMAINDER objs neq to objName */
      isObj= cGUI.remObjList[x];
      if(!objName.equals(isObj))
        newRemObjList[remTmpSize++]= isObj;
    }
    
    /* [3] Move objName from REMAINDER to SELECTED list */
    if(remTmpSize != cGUI.remObjListSize)
    { /* move it */
      /* [3.1] Remove from REMAINDER ta1 (LEFT) list */
      cGUI.remObjListSize= remTmpSize; /* new size */
      cGUI.remObjList= newRemObjList;  /* new list */
      
      /* [3.2] Add to SELECTED ta2 RIGHT list. */
      selObjList[selObjListSize]= objName;
      selObjListSize++;                /* added to SELECTED list */
    } /* move it */
    
    /* [4] Update Text Areas ta1 and ta2 */
    updateBothTextAreas();  /* update both text areas ta1 and ta2 */
  } /* addObjs */
  
  
  /**
   * delObjs() - move SELECTED obj from ta2 (RIGHT) to REMAINDER ta1 (LEFT) list
   * if ta1 is not same prj list.
   * @param objName objName to delete
   * @see #updateBothTextAreas
   */
  void delObjs(String objName)
  { /* delObjs */
    int selTmpSize= 0;
    String
      isObj,
      newSelObjList[]= new String[mae.MAX_HYB_SAMPLES];
    
    /* [1] protects against click-happy people */
    if(objName==null && selObjListSize>0)
      return;
    
    /* [2] Make new SELECTED list less objName. */
    for(int x=0; x<selObjListSize; x++)
    { /* add all SELECTED objs neq to objName */
      isObj= selObjList[x];
      if(! objName.equals(isObj))
        newSelObjList[selTmpSize++]= isObj;
    }
    
    /* [3] Move objName from SELECTED to REMAINDER list */
    if(selTmpSize != selObjListSize)
    { /* move it */
      /* [3.1] Remove from SELECTED ta2 RIGHT list */
      selObjListSize= selTmpSize;     /* new size */
      selObjList= newSelObjList;      /* new list */
      
      /* [3.2] Add to REMAINDER ta1 (LEFT) list */
      cGUI.remObjList[cGUI.remObjListSize]= objName;
      cGUI.remObjListSize++;          /* added to REMAINDER list */
    } /* move it */
    
    /* [4] Update Text Areas ta1 and ta2 */
    updateBothTextAreas();  /* update both text areas ta1 and ta2 */
  } /* delObjs */
  
  
  /**
   * moveObjUp() - move SELECTED obj ta2 (RIGHT) UP one position
   * unless it is at the top of list.
   * @param objName objName to move
   * @see #updateSelTextArea
   */
  void moveObjUp(String objName)
  { /* moveObjUp */
    String isObj;
    
    /* [1] protects against click-happy people */
    if(objName==null && selObjListSize>0)
      return;
    
    /* [2] Make new SELECTED list less objName. */
    for(int x=1; x<selObjListSize; x++)
    { /* move SELECTED obj UP if not first in list */
      isObj= selObjList[x];
      if(objName.equals(isObj))
      { /* move it in place */
        String prev= selObjList[x-1];
        selObjList[x-1]= objName;
        selObjList[x]= prev;
        break;
      }
    } /* move SELECTED obj UP if not first in list */
    
    /* [3] Update Text Areas ta2 */
    updateSelTextArea();  /* update selected text areas ta2 */
  } /* moveObjUp */
  
  
  /**
   * moveObjDown() - move SELECTED obj ta2 (RIGHT) Down one position
   * unless it is at the bottom of list.
   * @param objName objName to move
   * @see #updateSelTextArea
   */
  void moveObjDown(String objName)
  { /* moveObjDown */
    String isObj;
    
    /* [1] protects against click-happy people */
    if(objName==null && selObjListSize>0)
      return;
    
    /* [2] Make new SELECTED list less objName. */
    for(int x=0; x<(selObjListSize-1); x++)
    { /* move SELECTED obj DOWN if not last in list */
      isObj= selObjList[x];
      if(objName.equals(isObj))
      { /* move it in place */
        String next= selObjList[x+1];
        selObjList[x+1]= objName;
        selObjList[x]= next;
        break;
      }
    } /* move SELECTED obj DOWN if not last in list */
    
    /* [3] Update Text Areas ta2 */
    updateSelTextArea();  /* update selected text areas ta2 */
    
  } /* moveObjDown */
  
  
  /**
   * addAllObjs() - move all REMAINDER ta1 objects to SELECTED ta2.
   */
  void addAllObjs()
  { /* addAllObjs */
    /* [1] copy what's left in ta1 to ta2 */
    if(cGUI.remObjListSize==0)		/* nothing there */
      return;
    
    for(int x=0; x<cGUI.remObjListSize; x++)
    { /* copy rest of REMAINING into SELECTED */
      selObjList[selObjListSize++]= cGUI.remObjList[x];
      cGUI.remObjList[x]= " ";
    }
    
    /* [2] create str vers for ta2 display */
    cGUI.remObjListSize= 0;               /* empty */
    
    /* [3] Update Text Areas ta1 and ta2 */
    updateBothTextAreas();  /* update both text areas ta1 and ta2 */
  } /* addAllObjs */
  
  
  /**
   * rmvAllObjs() - move all objects from (SELECTED) ta2 to ta1 (REMAINDER).
   * @see #updateBothTextAreas
   */
  void rmvAllObjs()
  { /* rmvAllObjs */
    /* [1] copy what's left in ta2 to ta1 */
    if(selObjListSize==0)        /* nothing there */
      return;
    
    for(int x=0; x<selObjListSize; x++)
    { /* copy rest of SELECTED into REMAINING */
      cGUI.remObjList[cGUI.remObjListSize++]= selObjList[x];
      selObjList[x]= " ";
    }
    
    /* [2] create str vers for ta2 display */
    selObjListSize= 0;           /* empty */
    
    /* [3] Update Text Areas ta1 and ta2 */
    updateBothTextAreas();       /* update both text areas ta1 and ta2 */
  } /* rmvAllObjs */
  
  
  /**
   * updateSelTextAreas() - update selected text area ta2 (SELECTED)
   * @see #updateBothTextAreas
   * @see MouseEventHandler#resetMouseEventHandler
   */
  private void updateSelTextArea()
  { /* updateSelTextArea */
    String selObjListStr= "";    /* for displaying in text area */
    
    /* [1] Compute new strings */
    for(int x=0; x<selObjListSize; x++)
      selObjListStr += selObjList[x] + "\n";
    
    /* [3] Set RIGHT ta2 text area */
    if(selObjListSize==0)
      selObjListStr= " ";
    meh2.resetMouseEventHandler(selObjList, selObjListSize);
    ta2.setText(selObjListStr);
  } /* updateSelTextArea */
  
  
  /**
   * updateBothTextAreas() - update both text areas ta1 and ta2
   * @see MouseEventHandler#resetMouseEventHandler
   */
  private void updateBothTextAreas()
  { /* updateBothTextAreas */
    String
      remObjListStr= "",    /* for displaying in text area */
      selObjListStr= "";    /* for displaying in text area */
    
    /* [1] Compute new strings */
    for(int x=0; x<cGUI.remObjListSize; x++)
      remObjListStr += cGUI.remObjList[x] + "\n";
    
    for(int x=0; x<selObjListSize; x++)
      selObjListStr += selObjList[x] + "\n";
    
    /* [2] Set LEFT ta1 text area */
    if(cGUI.remObjListSize==0)
      remObjListStr= " ";
    cGUI.meh1.resetMouseEventHandler(cGUI.remObjList,
    cGUI.remObjListSize);
    cGUI.ta1.setText(remObjListStr);
    
    /* [3] Set RIGHT ta2 text area */
    if(selObjListSize==0)
      selObjListStr= " ";
    meh2.resetMouseEventHandler(selObjList, selObjListSize);
    ta2.setText(selObjListStr);
  } /* updateBothTextAreas */
  
  
  /**
   * updateInfo() - update info for index entry in parentWindow if any
   * @param index is index to display in the the information text area
   * @param foundStr is the data
   * @see PopupHPChooser#updateInfo
   */
  void updateInfo(int index, String foundStr)
  { /* updateInfo */
    if(parentWindow==null)
      return;
    else if(parentWindow instanceof PopupHPChooser)
    { /* is HP chooser */
      ((PopupHPChooser)parentWindow).updateInfo(this, index,foundStr);
    }
  } /* updateInfo */
  
  
  /**
   * actionPerformed() - handle action events buttons and text areas
   * @see #addObjs
   * @see #delObjs
   * @see #rmvAllObjs
   * @see #moveObjUp
   * @see #moveObjDown
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    Button item= (Button)e.getSource();
    
    if(cmd.equals("add"))
    {
      if(cGUI.meh1.foundStr!=null)
        addObjs(cGUI.meh1.foundStr);
      cGUI.meh1.foundStr= null;
    }
    
    else if(cmd.equals("del"))
    {
      if(meh2.foundStr!=null)
        delObjs(meh2.foundStr);
      meh2.foundStr= null;
    }
    
    else if(cmd.equals("addAll"))
    {
      addAllObjs();
    }
    
    else if(cmd.equals("rmvAll"))
    {
      rmvAllObjs();
    }
    
    else if(cmd.equals("Up"))
    { /* move SELECTED entry up in list */
      if(meh2.foundStr!=null)
        moveObjUp(meh2.foundStr);
    }
    
    else if(cmd.equals("Down"))
    { /* move SELECTED entry down in list */
      if(meh2.foundStr!=null)
        moveObjDown(meh2.foundStr);
    }
  } /* actionPerformed */
  
  
  /**
   * copyOrigToWorkingData() - copy original data to working data.
   * Note: allocate object lists required.
   */
  void copyOrigToWorkingData()
  { /* copyOrigToWorkingData */
    /* [1] Copy remainder ta1 to working list if not accessed remotely  */
    if(!remoteFlag)
    { /* use own remainder ta1 lists instead of remote one */
      remObjList= new String[mae.MAX_HYB_SAMPLES];
      
      for(int x=0; x<origRemObjsSize; x++)
        remObjList[x]= origRemObjs[x];
      remObjListSize= origRemObjsSize;
    } /* use own remainder ta1 lists instead of remote one */    
    
    /* [2] Copy original selected ta2 list into working list */
    selObjList= new String[mae.MAX_HYB_SAMPLES];
    
    for(int x=0; x<origSelObjsSize; x++)
      selObjList[x]= origSelObjs[x];
    selObjListSize= origSelObjsSize;
  } /* copyOrigToWorkingData */
  
  
  /**
   * resetChooserGUI() - reset original data to working data, reset ta1 and ta2
   * text areas. Note: allocate object lists required.
   * @see MouseEventHandler#resetMouseEventHandler
   * @see #copyOrigToWorkingData
   */
  void resetChooserGUI()
  { /* resetChooserGUI */
    String
      remObjListStr= "",
      selObjListStr= "";
    
    /* [1] Copy original data to working data. */
    copyOrigToWorkingData();
    
    /* [2] Copy remainder ta1 to working list if not accessed remotely. */
    if(!remoteFlag)
    { /* use own remainder ta1 lists instead of remote one */
      for(int x=0; x<origRemObjsSize; x++)
        remObjListStr += origRemObjs[x] + "\n";
      
      meh1.resetMouseEventHandler(origRemObjs,origRemObjsSize);
      ta1.setText(remObjListStr);
    } /* use own remainder ta1 lists instead of remote one */
    
    /* [3] Copy original selected ta2 list into working list */
    for(int x=0; x<origSelObjsSize; x++)
      selObjListStr += origSelObjs[x] + "\n";
    
    meh2.resetMouseEventHandler(origSelObjs, origSelObjsSize);
    ta2.setText(selObjListStr);
  } /* resetChooserGUI */
  
  
  /**
   * windowclosing() - close the window
   * @param e is window closing event
   */
  public void windowClosing(WindowEvent e)
  { /* windowClosing */
    quit();
  } /* windowClosing */
  
  
  /**
   * quit() - kill this window
   */
  void quit()
  { /* quit */
    System.exit(0);
  } /* quit */
  
  
  public void windowActivated(WindowEvent e)  {}
  public void windowClosed(WindowEvent e)  { }
  public void windowDeactivated(WindowEvent e)  {}
  public void windowDeiconified(WindowEvent e)  {}
  public void windowIconified(WindowEvent e)  {}
  public void windowOpened(WindowEvent e)  {}
  
  
  /**
   * keyPressed() - get key Pressed event
   */
  public void keyPressed(KeyEvent e)
  { /* keyPressed */
  } /* keyPressed */
  
  
  /**
   * keyReleased() - get key Released event
   */
  public void keyReleased(KeyEvent e)
  { /* keyReleased */
  } /* keyReleased */
  
  
  /**
   * keyTyped() - get key typed event
   */
  public void keyTyped(KeyEvent e)
  { /* keyTyped */
    char ch= e.getKeyChar();
  } /* keyTyped */
  
  
  /**
   * toString() - pretty-print instance of this ChooserGUI
   * @param msg to display in summary
   * @param allFlag display additional data
   * @return converted string
   */
  String toString(String msg, boolean allFlag)
  { /* toString */
    String  sR= "";
    
    /*
    if(msg==null)
      msg= "";
       
      sR= "------------------\n"+msg+
         "\nChooserGUI.id="+id+
         "\n title='"+title+
         "'\n taStr1='"+taStr1+
         "'\n taStr2='"+taStr2+"'\n"+
         "'\n cGUI.taStr1='"+cGUI.taStr2+"'\n";
       
      if(allFlag)
      {
        sR += " LEFT(ta1): cGUI.origRemObjsSize="+cGUI.origRemObjsSize+"\n";
        for(int i= 0; i<cGUI.origRemObjsSize;i++)
          sR += "   cGUI.origRemObjs["+i+"]= "+cGUI.origRemObjs[i]+"\n";
        
        sR += " RIGHT(ta2: origSelObjsSize="+origSelObjsSize+"\n";
        for(int i= 0; i<origSelObjsSize;i++)
          sR += "   origSelObjs["+i+"]= "+origSelObjs[i]+"\n";
      }
       
      sR += " LEFT(ta1): cGUI.remObjListSize="+cGUI.remObjListSize+"\n";
      for(int i= 0; i<cGUI.remObjListSize;i++)
        sR += "   cGUI.remObjList["+i+"]= "+cGUI.remObjList[i]+"\n";
      
      sR += " FINAL(ta2): selObjListSize="+selObjListSize+"\n";
      for(int i= 0; i<selObjListSize;i++)
        sR += "   selObjList["+i+"]= "+selObjList[i]+"\n";
       */
    
    return(sR);
  }  /* toString */
  
  
  
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /*       #2    CLASS MouseEventHandler                            */
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  
  class MouseEventHandler extends MouseAdapter
  { /* class MouseEventHandler */
    MAExplorer
      mae;
    
    /** size of alphaList */
    int
      alphaListSize;
    /** index of entry in the alphaList */
    int
      index;
    /** TextArea list of best guesses */
    TextArea
      ta;
    /** set text field if match is found */
    String
      foundStr= null;
    /** alpha list  if items in the list*/
    String
      alphaList[];
    /** for event handling */
    ChooserGUI
      cGUI;
    
    
    /**
     * MouseEventHandler() - constructor
     * @param mae is the MAExplorer instance
     * @param cGUI is the chooser gui for event handling
     * @param ta is the text area
     * @param alphaList is the list of items in the list
     * @param size of the alphaList
     */
    public MouseEventHandler(MAExplorer mae, ChooserGUI cGUI, TextArea ta,
                             String alphaList[], int size)
    { /* MouseEventHandler */
      this.mae= mae;
      this.cGUI= cGUI;
      this.ta= ta;
      this.alphaList= alphaList;
      this.alphaListSize= size;
      this.index= 0;
      this.foundStr= null;
    } /* MouseEventHandler */
    
    
    /**
     * MouseEventHandler() - constructor
     * @param mae is the MAExplorer instance
     * @param ta is the text area
     */
    public MouseEventHandler(MAExplorer mae, TextArea ta)
    { /* MouseEventHandler */
      this.mae= mae;
      this.ta= ta;
      this.alphaList= null;
      this.alphaListSize= 0;
      this.foundStr= null;
    } /* MouseEventHandler */
    
    
    /**
     * resetMouseEventHandler() - reset MouseEventHandler for text area
     * @param alphaList to reset it to
     * @param size of list
     */
    void resetMouseEventHandler(String alphaList[], int size)
    { /* resetMouseEventHandler */
      if(alphaList==null || size==0)
      {
        this.alphaList= null;
        this.alphaListSize= 0;
        return;
      }
      
      this.alphaList= alphaList;
      this.alphaListSize= size;
    } /* resetMouseEventHandler */
    
    
    /**
     * MouseReleased() - handle mouse clicks for ta
     * @param e is mouse release event
     */
    public void mouseReleased(MouseEvent evt)
    { /* MouseReleased */
      int
        strSizeTotal= 0,
        caretIdx= 0,        /* for PC, seperate index */
        listSize= 0,		/* number of rows */
        total= 0,		/* keep track of location */
        strSize= 0,
        taLoc= -1;	/* location of mouse clicked in ta */
      String list[]= new String[mae.MAX_HYB_SAMPLES]; /* store list */
      
      /* [1] Check input data */
      if(alphaList==null)
        return;
      if(this.alphaListSize==0)
        return;
      
      if(ta==null)
        return;
      
      /* [2] Check for out of bounds */
      int len= this.alphaListSize;
      for(int r= 0; r < len; r++)
        strSizeTotal += this.alphaList[r].length() + 1;
      
      if(ta.getCaretPosition() >= strSizeTotal)
        return; /* out of bounds */
      
      list= alphaList;
      listSize= alphaList.length;
      index= 0;
      foundStr= null;
      
      /* [3] Get location of mouse click using getCaretPosition() */
      taLoc= ta.getCaretPosition();      /* cursor location */
      
      /* [4] Get str value for that row */
      for(int r= 0; r<listSize; r++)
      { /* search thru rows */
        strSize= list[r].length(); /* get str size */
        
        if(total <= taLoc && taLoc <= (total + strSize))
        {
          foundStr= list[r];
          index= r;
          /* fixes PC counting problem */
          if(mae.isWinPCflag)
            ta.select(caretIdx, (caretIdx + strSize));
          else
            ta.select(total, (total + strSize));
          if(cGUI!=null)
            cGUI.updateInfo(index, foundStr);
          break;
        }
        
        /* fixes Windows PC counting problem*/
        if(mae.isWinPCflag)
        {
          caretIdx += strSize + 1;
          total += strSize + 1;  /* [DEPRICATED] keep count, +2 for \0\nn */
        }
        else
          total += strSize+1;      /* keep count, +1 for \0 */
      } /* search thru rows */
    } /* MouseReleased */
    
  } /* end of class MouseEventHandler */
  
  
  
} /* end of class ChooserGUI */
