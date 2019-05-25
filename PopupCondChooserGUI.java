/** File: PopupCondChooserGUI.java */


import java.awt.*;
import java.awt.Button;
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
 * The PopupCondChooserGUI class is a popup class for creating condition chooser GUIs.
 * It provides a panel implementing a (Remainder, Selection) GUI.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author G. Thornwall (SAIC), P. Lemkin (NCI), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.5 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public class PopupCondChooserGUI extends Panel implements 
                                         ActionListener, WindowListener
 {
  /** instance of PopupCondChooser */
  private PopupCondChooser
    cc;
  /** Total number of instances of this class. It increments for each instance */
  private static int
    masterID= 0;                
  /** id # for this instance */
  int
    id;          

  /** the MAX # of samples is specified by MAExplorer */
  private int 
    maxHybSamples;
  
  /** if can move SELECTED entry up/down*/
  boolean
    addChangePosFlag;           
  /** true if using other prj data/ta */
  boolean
    remoteFlag;			
  /** The system is a windows PC */
  boolean
    isWinPCflag= false;
  /** value changed */
  boolean
    valueChangedFlag= false;

  /** handle mouse events for text areas for remTextArea LEFT */
  private MouseEventHandler   
    remainderMeh;			
  /** handle mouse events for text areas for selTextArea RIGHT */
  private MouseEventHandler
    selectMeh;
  
  /** REMAINDER objs */
  private TextArea	
    remTextArea;
  /** SELECTED obj list */
  private TextArea				
    selTextArea;			
  
  /** Move the Remainder entry to Selected */
  private Button			
    addButton;                  
  /** Nove the Selected entry to Remainder */
  private Button
    delButton;                  
  /** Move ALL Remainder entries to Selected */
  private Button
    addAllButton;               
  /** Move ALL Selected entries to Remainder */
  private Button
    rmvAllButton;               
  /** Move SELECTED entry up if possible */
  private Button
    upButton;                   
  /** Move SELECTED entry down if possible */
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
  /** obj list remTextArea */
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
     
  /** label under remTextArea */
  private Label
    remTextAreaLabel;                   
  /** label  under selTextArea */
  private Label
    selTextAreaLabel;                   
  /** optional label if not to use remTextArea from above */
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
    remLabelName;                       
  /** label for REMAINDER RIGHT text area */ 
  String
    selLabelName;    
  
  /** original REMAINDER list */ 
  String
    origRemObjs[];	         
  /** original SELECTED list */
  String
    origSelObjs[];		  
  /** REMAINDER list of sample objects, returned if needed */ 
  String
    remObjList[];
  /** SELECTED list of sample object names to be returned */
  String
    selObjList[];	
  
  /** This is either the remote instance or 'this' instance of PopupCondChooserGUI 
   * for the instance of REMAINDER to use.
   */
  PopupCondChooserGUI
    ccGUI;            
  
  /** font used by title */   
  private Font
    titleFont;
  /** font used by label */   
  private Font
    labelFont;
  /** font used by data */   
  private Font
    dataFont;
  /** button font */
  Font
    buttonFont;
  
  /** main GUI panel */
  private Panel
    ccGUIpanel;                  
  /** parent window */
  private Object 
    parentWindow; 

  
  /**
   * PopupCondChooserGUI() - constructor for base class of Chooser GUI
   * @param cc is instance of PopupCondChooser
   * @param remoteC_GUI is the alternate PopupCondChooserGUI to use if share REMAINDER
   *        else null
   * @param maxHybSamples is MAX # of samples is specified by MAExplorer 
   * @param taRows is the # rows visible in scroller
   * @param taColsis the # columns visible in scroller
   * @param titleFont is the top level title Font
   * @param labelFont is the LEFT and RIGHT titles Font
   * @param dataFont is the Text Areas Font
   * @param buttonFont is the buttons Font
   * @param parentWindow is the parent window
   * @param addChangePosFlag is flag to move entry position in SELECTED window
   */
  PopupCondChooserGUI(PopupCondChooser cc, PopupCondChooserGUI remoteCC_GUI, 
                      int maxHybSamples, int taRows, int taCols,
                      Font titleFont, Font labelFont, Font dataFont, Font buttonFont,
                      Object parentWindow, boolean addChangePosFlag )
  { /* PopupCondChooserGUI */    
    id= ++masterID;                             /* instance ID  for debugging */
    
    this.cc= cc;
    remoteFlag= (remoteCC_GUI!=null);
    ccGUI= (remoteFlag) ? remoteCC_GUI : this;  /* where to get REMAINDER */
    
    this.maxHybSamples= maxHybSamples;
    this.taRows= taRows;
    this.taCols= taCols;
    this.titleFont= titleFont;
    this.labelFont= labelFont;
    this.dataFont= dataFont;
    this.buttonFont= buttonFont;
    this.parentWindow= parentWindow;
    this.addChangePosFlag= addChangePosFlag;
  } /* PopupCondChooserGUI */
  
  
  /**
   * setTitles() - set titles for REMAINDER and SELECTED text areas
   * @param remLabelName is the REMAINDER caption
   * @param selLabelName is the SELECTED caption
   */
  void setTitles(String remLabelName, String selLabelName )
  { /* setTitles */
    this.remLabelName= remLabelName;
    this.selLabelName= selLabelName;
  } /* setTitles */
  
  
  /**
   * setListData() - copy original data for REMAINDER and SELECTED lists.
   * Note: can use offset 'off' to copy data from [1:n] arrays into
   * the origXXXObjs[0:n-1] arrays.int
   * @param maxObjs is the max # objects
   * @param offset 1 if [1:n] else 0, starting point of array
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
    if(ccGUI == this && remData != null)
    {
      this.origRemObjs= new String[maxObjs];
      for(int i=0; i<nRem; i++)
        this.origRemObjs[i]= remData[i+offset];
      this.origRemObjsSize= nRem;
    }
    
    if(selData!=null)
    {
      this.origSelObjs= new String[maxObjs];
      for(int i=0; i<nSel; i++)
        this.origSelObjs[i]= selData[i+offset];
      this.origSelObjsSize= nSel;
    }
    
    /* [2] Copy orig to working data and counters */
    copyOrigToWorkingData();
  } /* setListData */
  
  
  /**
   * buildPopupCondChooserGUI() - create single instance GUI for Chooser.
   * @see MouseEventHandler
   */
  void buildPopupCondChooserGUI()
  { /* buildPopupCondChooserGUI */
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
    controlPanel= new Panel(new BorderLayout());     /* main ok/reset buttons*/
    buttonPanel= new Panel(new GridBagLayout());     /* buttons */
    prjListPanel= new Panel(new BorderLayout());     /* prj ta */
    objButtonPanel= new Panel(new BorderLayout());   /* set prj button*/
    objListTApanel= new Panel(new BorderLayout());   /* obj list remTextArea */
    addDelAllButtonPanel= new Panel(new BorderLayout()); /* add,del,all buttons */
    selectedPanel= new Panel(new BorderLayout());    /* SELECTED list */
    
    remTextAreaLabel= new Label(remLabelName);                     /* under remTextArea  */
    selTextAreaLabel= new Label(selLabelName);                     /* under selTextArea */
    useAbove= new Label("Use above samples");        /* not to use remTextArea from above */
    lTitle= new Label(ccGUI.title);                  /* title for entire GUI window */
    
    /* [1.1] Setup fonts etc */
    addButton.setFont(buttonFont);
    delButton.setFont(buttonFont);
    addAllButton.setFont(buttonFont);
    rmvAllButton.setFont(buttonFont);
    
    lTitle.setFont(titleFont);
    remTextAreaLabel.setFont(labelFont);
    selTextAreaLabel.setFont(labelFont);
    useAbove.setFont(labelFont);
    this.setLayout(new BorderLayout()); /* main panel is the class */
    
    /* [2] Convert REMAINDER List to string for (LEFT) remTextArea */
    if(!remoteFlag)
    { /* create TextArea remTextArea only for non-remote ccGUIs */
      for(int x=0; x<origRemObjsSize; x++)
        remObjListStr += origRemObjs[x] + "\n";
      remTextArea= new TextArea(taRows,taCols);
      remTextArea.setText(remObjListStr);
    }
    
    /* [3] Convert SELECTED List to string for (RIGHT) selTextArea */
    for(int x=0; x<origSelObjsSize; x++)
      selObjListStr += origSelObjs[x] + "\n";
    selTextArea= new TextArea(ccGUI.taRows, ccGUI.taCols);
    selTextArea.setText(selObjListStr);
    
    /* [4] Create seperate mouse listeners for TextAreas */
    if(!remoteFlag)
    { /* only listen to non-remote REMAINDER ccGUI textArea remTextArea */
      remainderMeh= new MouseEventHandler( this, remTextArea, origRemObjs,
      origRemObjsSize);
      remTextArea.addMouseListener(remainderMeh);
      remTextArea.setEditable(false);
    }
    
    selectMeh= new MouseEventHandler( this, selTextArea, origSelObjs,
    origSelObjsSize);
    selTextArea.addMouseListener(selectMeh);
    selTextArea.setEditable(false);
    
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
      upButton.setFont(buttonFont);
      downButton.setFont(buttonFont);
      changePosPanel.add(upButton);
      changePosPanel.add(downButton);
    }
    
    ccGUI.remTextArea.setBackground(Color.lightGray);
    selTextArea.setBackground(Color.lightGray);
    
    addButton.setBackground(Color.lightGray);
    delButton.setBackground(Color.lightGray);
    addAllButton.setBackground(Color.lightGray);
    rmvAllButton.setBackground(Color.lightGray);
    
    addButton.setFont(buttonFont);
    delButton.setFont(buttonFont);
    addAllButton.setFont(buttonFont);
    rmvAllButton.setFont(buttonFont);
    
    /* [6] For remote REMAINDER fill space normally used by remTextArea TextArea */
    if(!remoteFlag)
    {
      remTextArea.setFont(dataFont);
      objListTApanel.add((new Label(" ")), BorderLayout.NORTH);
      objListTApanel.add(remTextArea, BorderLayout.SOUTH);
      objListTApanel.add(remTextAreaLabel, BorderLayout.CENTER);
    }
    
    /* [7] Put set of three buttons, place into another gridbag */
    addObjGridBagLayout(buttonPanel, addButton ,0,0,1,1,1.0,1);
    addObjGridBagLayout(buttonPanel, delButton ,0,1,1,1,1.0,1);
    addObjGridBagLayout(buttonPanel, addAllButton ,0,2,1,1,1.0,1);
    addObjGridBagLayout(buttonPanel, rmvAllButton ,0,3,1,1,1.0,1);
    addDelAllButtonPanel.add(buttonPanel, BorderLayout.CENTER);
    
    /* [8] Add SELECTED selTextArea RIGHT selTextArea text area */
    selTextArea.setFont(dataFont);
    selectedPanel.add((new Label(" ")), BorderLayout.NORTH);
    selectedPanel.add(selTextArea, BorderLayout.SOUTH);
    selectedPanel.add(selTextAreaLabel, BorderLayout.CENTER);    
    
    /* [9] Place everything into main gridBagLayout panel */
    ccGUIpanel= new Panel(new GridBagLayout());  /* main panel */
    
    addObjGridBagLayout(ccGUIpanel,whiteSpace,0,0,1,1,1.0,1); /* alignment */
    
    if(!remoteFlag)           /* add LEFT Remainder project list remTextArea */
      addObjGridBagLayout(ccGUIpanel,prjListPanel,1,0,1,1,1.0,1);
    
    addObjGridBagLayout(ccGUIpanel,whiteSpace,2,0,1,1,1.0,3); /* alignment */
    
    if(!remoteFlag)          /* set button */
      addObjGridBagLayout(ccGUIpanel,objButtonPanel,3,0,1,1,1.0,1);
    else
      addObjGridBagLayout(ccGUIpanel,useAbove,3,0,1,1,1.0,1);
    
    addObjGridBagLayout(ccGUIpanel,whiteSpace,4,0,1,1,1.0,1);  /* alignment */
    
    if(!remoteFlag)
      addObjGridBagLayout(ccGUIpanel,objListTApanel,5,0,1,1,1.0,1); /* obj list ta */
    
    addObjGridBagLayout(ccGUIpanel,whiteSpace,6,0,1,1,1.0,1);  /* alignment */
    addObjGridBagLayout(ccGUIpanel,addDelAllButtonPanel,7,0,1,1,1.0,1); /* add,del,>> */
    addObjGridBagLayout(ccGUIpanel,whiteSpace,8,0,1,1,1.0,1);  /* alignment */
    
    /* Add the RIGHT selected objs ta */
    if(!addChangePosFlag)
      addObjGridBagLayout(ccGUIpanel,selectedPanel,9,0,1,1,1.0,1);
    else
    { /* put selectedPanel and changePosPanel in new panel */
      Panel
      complexSelPanel= new Panel();
      complexSelPanel.add("West", selectedPanel); /* RIGHT selected objs ta*/
      complexSelPanel.add("Center", changePosPanel);
      addObjGridBagLayout(ccGUIpanel,complexSelPanel,9,0,1,1,1.0,1);
    }	/* put selectedPanel and changePosPanel in new panel */
    
    addObjGridBagLayout(ccGUIpanel,whiteSpace,10,0,1,1,1.0,1); /* alignment */
    
    /* [9.1] [TODO] if addChangePosFlag, then add changePosPanel to right of selTextArea. */
    
    /* [10] now display everything in frame */
    this.add(ccGUIpanel,BorderLayout.EAST);     /* add to frame */
    this.add(lTitle,BorderLayout.WEST);
    this.add(controlPanel,BorderLayout.SOUTH); /* add to bottom */
  } /* buildPopupCondChooserGUI */
  
  
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
   * addObjs() - move object to (remainder) remTextArea from (selected) selTextArea.
   * @param objName to add
   * @see #updateBothTextAreas
   */
  void addObjs(String objName)
  { /* addObjs */
    int remTmpSize=  0;              /* keep track of size of list */
    String
      isObj,
      newRemObjList[]= new String[maxHybSamples]; /* temp */
    
    /* [1] Protects against click-happy people if nothing picked. */
    if(objName==null || ccGUI.remObjListSize<=0)
      return;	                       /* nothing was selected */
    
    /* [2] Make new REMAINDER list less objName */
    for(int x=0; x<ccGUI.remObjListSize; x++)
    { /* add all REMAINDER objs neq to objName */
      isObj= ccGUI.remObjList[x];
      if(!objName.equals(isObj))
        newRemObjList[remTmpSize++]= isObj;
    }/* add all REMAINDER objs neq to objName */
    
    /* [3] Move objName from REMAINDER to SELECTED list */
    if(remTmpSize != ccGUI.remObjListSize)
    { /* move it */
      /* [3.1] Remove from REMAINDER remTextArea (LEFT) list */
      ccGUI.remObjListSize= remTmpSize;   /* new size */
      ccGUI.remObjList= newRemObjList;    /* new list */
      
      /* [3.2] Add to SELECTED selTextArea RIGHT list. */
      selObjList[selObjListSize]= objName;
      selObjListSize++;                   /* added to SELECTED list */
    } /* move it */
    
    /* [4] Update Text Areas remTextArea and selTextArea */
    updateBothTextAreas();  /* update both text areas remTextArea and selTextArea */
  } /* addObjs */
  
  
  /**
   * delObjs() - move SELECTED obj from selTextArea (RIGHT) to REMAINDER remTextArea (LEFT) list
   * if remTextArea is not same prj list.
   * @param objName objName to delete
   * @see #updateBothTextAreas
   */
  void delObjs(String objName)
  { /* delObjs */
    int selTmpSize= 0;
    String
      isObj,
      newSelObjList[]= new String[maxHybSamples];
    
    /* [1] protects against click-happy people */
    if(objName==null && selObjListSize>0)
      return;
    
    /* [2] Make new SELECTED list less objName. */
    for(int x=0; x<selObjListSize; x++)
    { /* add all SELECTED objs neq to objName */
      isObj= selObjList[x];
      if(!objName.equals(isObj))
        newSelObjList[selTmpSize++]= isObj;
    } /* add all SELECTED objs neq to objName */
    
    /* [3] Move objName from SELECTED to REMAINDER list */
    if(selTmpSize != selObjListSize)
    { /* move it */
      /* [3.1] Remove from SELECTED selTextArea RIGHT list */
      selObjListSize= selTmpSize;     /* new size */
      selObjList= newSelObjList;      /* new list */
      
      /* [3.2] Add to REMAINDER remTextArea (LEFT) list */
      ccGUI.remObjList[ccGUI.remObjListSize]= objName;
      ccGUI.remObjListSize++;          /* added to REMAINDER list */
    } /* move it */
    
    /* [4] Update Text Areas remTextArea and selTextArea */
    updateBothTextAreas();  /* update both text areas remTextArea and selTextArea */
  } /* delObjs */
  
  
  /**
   * moveObjUp() - move SELECTED obj selTextArea (RIGHT) UP one position
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
    
    /* [3] Update Text Areas selTextArea */
    updateSelTextArea();  /* update selected text areas selTextArea */
  } /* moveObjUp */
  
  
  /**
   * moveObjDown() - move SELECTED obj selTextArea (RIGHT) Down one position
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
      }/* move it in place */
    } /* move SELECTED obj DOWN if not last in list */
    
    /* [3] Update Text Areas selTextArea */
    updateSelTextArea();  /* update selected text areas selTextArea */
    
  } /* moveObjDown */
  
  
  /**
   * addAllObjs() - move all REMAINDER remTextArea objects to SELECTED selTextArea.
   */
  void addAllObjs()
  { /* addAllObjs */
    /* [1] copy what's left in remTextArea to selTextArea */
    if(ccGUI.remObjListSize==0)		/* nothing there */
      return;
    
    for(int x=0; x<ccGUI.remObjListSize; x++)
    { /* copy rest of REMAINING into SELECTED */
      selObjList[selObjListSize++]= ccGUI.remObjList[x];
      ccGUI.remObjList[x]= " ";
    } /* copy rest of REMAINING into SELECTED */
    
    /* [2] create str vers for selTextArea display */
    ccGUI.remObjListSize= 0;               /* empty */
    
    /* [3] Update Text Areas remTextArea and selTextArea */
    updateBothTextAreas();  /* update both text areas remTextArea and selTextArea */
  } /* addAllObjs */
  
  
  /**
   * rmvAllObjs() - move all objects from (SELECTED) selTextArea to remTextArea (REMAINDER).
   * @see #updateBothTextAreas
   */
  void rmvAllObjs()
  { /* rmvAllObjs */
    /* [1] copy what's left in selTextArea to remTextArea */
    if(selObjListSize==0)        /* nothing there */
      return;
    
    for(int x=0; x<selObjListSize; x++)
    { /* copy rest of SELECTED into REMAINING */
      ccGUI.remObjList[ccGUI.remObjListSize++]= selObjList[x];
      selObjList[x]= " ";
    } /* copy rest of SELECTED into REMAINING */
    
    /* [2] create str vers for selTextArea display */
    selObjListSize= 0;           /* empty */
    
    /* [3] Update Text Areas remTextArea and selTextArea */
    updateBothTextAreas();       /* update both text areas remTextArea and selTextArea */
  } /* rmvAllObjs */
  
  
  /**
   * updateSelTextAreas() - update selected text area selTextArea (SELECTED)
   * @see #updateBothTextAreas
   * @see MouseEventHandler#resetMouseEventHandler
   */
  private void updateSelTextArea()
  { /* updateSelTextArea */
    String selObjListStr= "";    /* for displaying in text area */
    
    /* [1] Compute new strings */
    for(int x=0; x<selObjListSize; x++)
      selObjListStr += selObjList[x] + "\n";
    
    /* [3] Set RIGHT selTextArea text area */
    if(selObjListSize==0)
      selObjListStr= " ";
    selectMeh.resetMouseEventHandler(selObjList, selObjListSize);
    selTextArea.setText(selObjListStr);
  } /* updateSelTextArea */
  
  
  /**
   * updateBothTextAreas() - update both text areas remTextArea and selTextArea
   * @see MouseEventHandler#resetMouseEventHandler
   */
  private void updateBothTextAreas()
  { /* updateBothTextAreas */
    String
      remObjListStr= "",    /* for displaying in text area */
      selObjListStr= "";    /* for displaying in text area */
    
    /* [1] Compute new strings */
    for(int x=0; x<ccGUI.remObjListSize; x++)
      remObjListStr += ccGUI.remObjList[x] + "\n";
    
    for(int x=0; x<selObjListSize; x++)
      selObjListStr += selObjList[x] + "\n";
    
    /* [2] Set LEFT remTextArea text area */
    if(ccGUI.remObjListSize==0)
      remObjListStr= " ";
    ccGUI.remainderMeh.resetMouseEventHandler(ccGUI.remObjList,
    ccGUI.remObjListSize);
    ccGUI.remTextArea.setText(remObjListStr);
    
    /* [3] Set RIGHT selTextArea text area */
    if(selObjListSize==0)
      selObjListStr= " ";
    selectMeh.resetMouseEventHandler(selObjList, selObjListSize);
    selTextArea.setText(selObjListStr);
  } /* updateBothTextAreas */
  
  
  /**
   * updateInfo() - update info for index entry in parentWindow if any
   * @param index is index to display in the the information text area
   * @param foundStr is the data
   * @see PopupHPChooser#updateInfo
   */
  void updateInfo(int index, String foundStr)
  { /* updateInfo */
    if(foundStr!=null)
      cc.updateInfo(foundStr);
  } /* updateInfo */
  
  
  /**
   * clearInfo() - clear info for the 3 msg areas in the PopupCondChooser
   */
  void clearInfo()
  { /* clearInfo */
    cc.clearInfo();    
  } /* clearInfo */
  
  
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
    valueChangedFlag= true;
    
    if(cmd.equals("add"))
    {
      if(ccGUI.remainderMeh.foundStr!=null)
        addObjs(ccGUI.remainderMeh.foundStr);
      ccGUI.remainderMeh.foundStr= null;
      clearInfo();
    }
    else if(cmd.equals("del"))
    {
      if(selectMeh.foundStr!=null)
        delObjs(selectMeh.foundStr);
      selectMeh.foundStr= null;
      cc.clearInfo();
    }
    else if(cmd.equals("addAll"))
    {
      addAllObjs();
      clearInfo();
    }
    else if(cmd.equals("rmvAll"))
    {
      rmvAllObjs();
      clearInfo();
    }
    else if(cmd.equals("Up"))
    { /* move SELECTED entry up in list */
      if(selectMeh.foundStr!=null)
        moveObjUp(selectMeh.foundStr);
      clearInfo();
    }
    else if(cmd.equals("Down"))
    { /* move SELECTED entry down in list */
      if(selectMeh.foundStr!=null)
        moveObjDown(selectMeh.foundStr);
      clearInfo();
    }
  } /* actionPerformed */
  
  
  /**
   * copyOrigToWorkingData() - copy original data to working data.
   * Note: allocate object lists required.
   */
  void copyOrigToWorkingData()
  { /* copyOrigToWorkingData */
    /* [1] Copy remainder remTextArea to working list if not accessed
     * remotely .
     */
    if(!remoteFlag)
    { /* use own remainder remTextArea lists instead of remote one */
      remObjList= new String[maxHybSamples];
      
      for(int x=0; x<origRemObjsSize; x++)
        remObjList[x]= origRemObjs[x];
      remObjListSize= origRemObjsSize;
    } /* use own remainder remTextArea lists instead of remote one */    
    
    /* [2] Copy original selected selTextArea list into working list */
    selObjList= new String[maxHybSamples];
    
    for(int x=0; x<origSelObjsSize; x++)
      selObjList[x]= origSelObjs[x];
    selObjListSize= origSelObjsSize;
  } /* copyOrigToWorkingData */
  
  
  /**
   * resetPopupCondChooserGUI() - reset original data to working data, reset remTextArea and selTextArea
   * text areas. Note: allocate object lists required.
   * @see MouseEventHandler#resetMouseEventHandler
   * @see #copyOrigToWorkingData
   */
  void resetPopupCondChooserGUI()
  { /* resetPopupCondChooserGUI */
    String
      remObjListStr= "",
      selObjListStr= "";
    
    /* [1] Copy original data to working data. */
    copyOrigToWorkingData();
    
    /* [2] Copy remainder remTextArea to working list if not accessed remotely. */
    if(!remoteFlag)
    { /* use own remainder remTextArea lists instead of remote one */
      for(int x=0; x<origRemObjsSize; x++)
        remObjListStr += origRemObjs[x] + "\n";
      
      remainderMeh.resetMouseEventHandler(origRemObjs,origRemObjsSize);
      remTextArea.setText(remObjListStr);
    } /* use own remainder remTextArea lists instead of remote one */
    
    /* [3] Copy original selected selTextArea list into working list */
    for(int x=0; x<origSelObjsSize; x++)
      selObjListStr += origSelObjs[x] + "\n";
    
    selectMeh.resetMouseEventHandler(origSelObjs, origSelObjsSize);
    selTextArea.setText(selObjListStr);
  } /* resetPopupCondChooserGUI */
  
  
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
   * toString() - pretty-print instance of this PopupCondChooserGUI
   * @param msg to display in summary
   * @param allFlag display additional data
   * @return converted string
   */
  String toString(String msg, boolean allFlag)
  { /* toString */
    String sR= "";
     
    if(msg==null)
      msg= "";
    /*   
    sR= "------------------\n"+msg+
       "\nPopupCondChooserGUI.id="+id+
       "\n title='"+title+
       "'\n remLabelName='"+remLabelName+
       "'\n selLabelName='"+selLabelName+"'\n"+
       "'\n ccGUI.remLabelName='"+ccGUI.selLabelName+"'\n";
       
      if(allFlag)
      {
        sR += " LEFT(remTextArea): ccGUI.origRemObjsSize="+ccGUI.origRemObjsSize+"\n";
        for(int i= 0; i<ccGUI.origRemObjsSize;i++)
          sR += "   ccGUI.origRemObjs["+i+"]= "+ccGUI.origRemObjs[i]+"\n";
        
        sR += " RIGHT(selTextArea: origSelObjsSize="+origSelObjsSize+"\n";
        for(int i= 0; i<origSelObjsSize;i++)
          sR += "   origSelObjs["+i+"]= "+origSelObjs[i]+"\n";
      }
    
    sR += " LEFT(remTextArea): ccGUI.remObjListSize="+ccGUI.remObjListSize+"\n";
    for(int i= 0; i<ccGUI.remObjListSize;i++)
      sR += "   ccGUI.remObjList["+i+"]= "+ccGUI.remObjList[i]+"\n";
    
    sR += " FINAL(selTextArea): selObjListSize="+selObjListSize+"\n";
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
    PopupCondChooserGUI
      ccGUI;
    
    
    /**
     * MouseEventHandler() - constructor
     * @param mae is the MAExplorer instance
     * @param ccGUI is the chooser gui for event handling
     * @param ta is the text area
     * @param alphaList is the list of items in the list
     * @param size of the alphaList
     */
    public MouseEventHandler( PopupCondChooserGUI ccGUI, TextArea ta,
                              String alphaList[], int size)
    { /* MouseEventHandler */
      this.ccGUI= ccGUI;
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
    public MouseEventHandler(TextArea ta)
    { /* MouseEventHandler */
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
        caretIdx= 0,                 /* for PC, seperate index */
        listSize= 0,	               /* number of rows */
        total= 0,		                 /* keep track of location */
        strSize= 0,
        taLoc= -1;	                 /* location of mouse clicked in ta */
      String
        list[]= new String[maxHybSamples]; /* store list */
      
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
          if(isWinPCflag)
            ta.select(caretIdx, (caretIdx + strSize));
          else
            ta.select(total, (total + strSize));
          if(ccGUI!=null)
            ccGUI.updateInfo(index, foundStr);
          break;
        }
        
        /* fixes Windows PC counting problem*/
        if(isWinPCflag)
        {
          caretIdx += strSize + 1;
          total += strSize + 1;  /* [DEPRICATED] keep count, +2 for \0\nn */
        }
        else
          total += strSize+1;      /* keep count, +1 for \0 */
      } /* search thru rows */
    } /* MouseReleased */
  } /* end of class MouseEventHandler */
    
    
}/* end of class Condition Chooser  */

