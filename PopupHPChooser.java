/** File: PopupHPChooser.java */

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
 * The class creates a GUI for selecting HP-X and HP-Y 'sets', HP-E 'list'.
 * It lets users select subsets of samples from the set of all samples loaded
 * in the database. Since HP-X and HP-Y sets are mutually exclusive, the data put
 * into HP-X 'set' is not available to put into the HP-Y 'set'. The HP-E list' 
 * may be defined as an ordered list from a subset of the set of all samples.
 *<P>
 * The GUI uses 3 instances of the ChooserGUI tied together as described above
 * and illustrated in the figure below. It makes copies of the HP sample lists
 * in SampleSets hps.(msList, msListX, msListY, msListE) into
 *<PRE>
 * The user selected results are saved in local arrays:
 *   msListXo[], msListYo[],  msListEo[], msListoXY[], msListoE[].
 * Then generate the corresponding string arrays
 *   sListXo[], sListYo[],  sListEo[], sListoXY[], sListoE[].
 *
 * The SELECTED lists are sListXo[], sListYo[], sListEo[].
 * The REMAINING lists are: sListoXY[] and sListoE[].
 *</PRE>
 * If the user finally presses "Done", the results are saveed back in
 * SampleSets hps.(msListX, msListY, msListE).
 *<P> 
 *<PRE>
 *  The following is the GUI Layout for HP chooser
 *
 *   REMAINDERs     SELECTEDs
 *  
 *  |------|       |------|
 *  |  HP  |   ->  | HP-X | Up
 *  | rem  |   <-  |      | Down
 *  |------|       |------| 
 *                 
 *    use      ->  |------| 
 *    above    <-  | HP-Y | Up
 *                 |      | Down
 *                 |------| 
 *	  
 *  |------|       |------| 
 *  | HP   |   ->  | HP-E | Up
 *  | rem  |   <-  |      | Down
 *  |------|       |------| 
 *            
 * --------------------------------
 * |  info area for selected HP   |
 * --------------------------------
 *  
 *   OK   CANCEL  RESET  (control buttons)
 * </PRE>
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author P. Lemkin (NCI), G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.9 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ChooserGUI
 */

class PopupHPChooser extends Frame implements ActionListener, WindowListener
{ /* PopupHPChooser */  
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;                   
  /** link to global SampleSets instance */
  private SampleSets 
    hps;     
   
  /*Global id counter. It increments for each instance */
  private static int
    masterID= 0;           
  /** id # for this instance */
  int
    id;                        
                 
 /** Height of frame holding popup HP chooser. was 500 */  
 final static int
    POPUP_HEIGHT= 675; 
 /** Width of of frame holding popup HP chooser. Old 650 (30 cols), 
  * (old 550 before Up/Down).
  */                 
  final static int
    POPUP_WIDTH= 780;     
  
  /** frame to install choosers */  
  private Frame
    f;  
  /** default frame size - must be here! */
  int
    frameWidth= POPUP_WIDTH; 
  /** default frame size - must be here! */
  int 
    frameHeight= POPUP_HEIGHT;
  
  /** index of chooser instance in cgList[] */ 
  int 
    cgIdx;  
  /** TextArea rows dimensions */
  int              
    taRows;	 
  /** TextArea cols dimensions */
  int 
    taCols;		 
  /** number of ChooseGUI instances */
  int 
    nChooser;            
  
  /** [0:nChooser-1] list of chooserGUIs */
  private ChooserGUI
    cgList[];	         
  
  /** # of original HP-X selected samples */
  private int	
    nHPXo;  
  /** # of original HP-Y selected samples */
  private int	            
    nHPYo;               
  /** # of original HP-E selected samples */
  private int	
    nHPEo;               
  /** # all probes in the database */
  private int	
    nHP;                 
  /** # rem(X+Y) = mxListo - msListXo - msListYo */
  private int	
    nHPrXY;	         
  /** # rem(E) = mxListo - msListEo */
  private int	   
    nHPrE;		 
  /** # of final selected HP-X samples */
  private int	
    nHPXf;               
  /** # of final selected HP-Y samples */
  private int	
    nHPYf;
  /** # of final selected HP-E samples */
  private int	
    nHPEf;
  
  /** sorted list of HP menu size */
  private int			
    hpMenuItemListSize;	 
    
  /** initial HP-X sample list on starting chooser */
  private MaHybridSample
    msListXo[];          
  /** initial HP-Y sample list on starting chooser */
  private MaHybridSample
    msListYo[];          
  /** initial HP-E sample list on starting chooser */
  private MaHybridSample
    msListEo[];          
  /** List of ALL initial samples in DB */
  private MaHybridSample
    msList[];            
  /** rem(X+Y) = mxListo - msListXo - msListYo */
  private MaHybridSample
    msListrXY[];         
  /** rem(E) = mxListo - msListEo */ 
  private MaHybridSample
    msListrE[];           
      
  /** initial HP-X sample list on starting chooser */
  private String
    sListXo[];           
  /** initial HP-Y sample list on starting chooser */
  private String
    sListYo[];          
  /** initial HP-E list on starting chooser */
  private String
    sListEo[];		 
  /** List of ALL initial samples in DB */
  private String
    sList[];             
  /** rem(X+Y) = msListo - msListXo - msListYo */
  private String
    sListrXY[];          
  /** rem(E) = msListo - msListEo */
  private String
    sListrE[];           
  
  /** title of popup chooser window */
  private String
    title;               
  
  /** font for title of popup chooser window */
  private Font
    titleFont;
  /** font for labels of popup chooser window */
  private Font
    labelFont;
  /** font for data of popup chooser window */
  private Font
    dataFont;

  /** first line of info about selected HP */
  private TextField
    infoTA1;
  /** second line of info about selected HP */
  private TextField	         
    infoTA2;
  /** third line of info about selected HP */
  private TextField
    infoTA3;
      

  /**
   * PopupHPChooser() - Constructor.
   * @param mae is instance of MAExplorer
   * @param titleFont is the font for the title
   * @param labelFont is the font for the labels
   * @param dataFont is the font for the data
   * @param taRows is the TextArea rows dimension
   * @param taCols is the TextArea cols dimension
   * @see #buildHPchooserGUI
   */
  PopupHPChooser(MAExplorer mae, Font titleFont, Font labelFont,
                 Font dataFont, int taRows, int taCols )
  { /* PopupHPChooser */
    this.f= this;       /* use THIS frame */
    this.mae= mae;
    
    id= ++masterID;     /* instance ID  for debugging */
    /*
    if(mae.CONSOLE_FLAG)
      System.out.println("PHPC.new id="+id);
     */
    
    hps= mae.hps;
    this.taRows= taRows;
    this.taCols= taCols;
    
    nChooser= 3;        /* HP-X, HP-Y and HP-E ChooseGUI instances */
    
    if(titleFont==null)
      titleFont= new Font(mae.cfg.fontFamily, Font.BOLD, 14);
    if(labelFont==null)
      labelFont= new Font(mae.cfg.fontFamily, Font.BOLD, 12);
    if(dataFont==null)
      dataFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 12);
    
    this.titleFont= titleFont;
    this.labelFont= labelFont;
    this.dataFont= dataFont;
    
    /* Local definitions */
    cgIdx= 3;        /* # of ChooserGUI instances */
    title= "      Choose HP-X and HP-Y sets, and HP-E list of hybridized samples";
    
    buildHPchooserGUI(title);
  } /* PopupHPChooser */
  
  
  /**
   * buildHPchooserGUI() - create GUI for PopupHPChooser.
   * @param title for the chooser
   * @see #addObj
   * @see #buildDataStructures
   */
  void buildHPchooserGUI(String title)
  { /* buildHPchooserGUI */
    String whiteSpace= " ";                     	/* for space between ChooserGUIs */
    
    /* place items within panels for better gGUI control */
    Panel
     p= new Panel(new GridBagLayout()),           /* main panel */
     buttonPanel= new Panel(),                    /* sub ok/reset buttons */
     controlPanel= new Panel(new BorderLayout()), /* holds buttonPanel */
     infoPanel= new Panel(new GridLayout(3,1));   /* panel w/info on selected HP */
    Label lTitle= new Label(title);               /* title */
    Button
      resetButton= new Button(" Reset "),         /* reset text areas */
      okButton= new Button(" OK "),               /* submit */
      cancelButton= new Button(" Cancel ");       /* kill Chooser w/o submit */    
    
    /* [1] setup fonts etc */
    setLayout(new BorderLayout()); /* main frame */
    lTitle.setFont(titleFont);
    
    okButton.setBackground(Color.lightGray);
    cancelButton.setBackground(Color.lightGray);
    resetButton.setBackground(Color.lightGray);
    
    okButton.setFont(labelFont);
    cancelButton.setFont(labelFont);
    resetButton.setFont(labelFont);
    
    okButton.addActionListener(this);	/* button listeners */
    cancelButton.addActionListener(this);
    resetButton.addActionListener(this);
    
    /* [2] add buttons to 2 nested panels, for alignment */
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    buttonPanel.add(resetButton);
    controlPanel.add(buttonPanel,BorderLayout.SOUTH);
    
    /* [2.1] Add Info text area */
    infoTA1= new TextField("",80);
    infoTA2= new TextField("",80);
    infoTA3= new TextField("",80);
    infoPanel.add(infoTA1);
    infoPanel.add(infoTA2);
    infoPanel.add(infoTA3);
    controlPanel.add(infoPanel,BorderLayout.NORTH);
    
    /* [3] Stuff data into data structures */
    buildDataStructures();
    
    /* [4] add to panel to create */
    for(int x=0; x<cgIdx; x++)
      addObj(p,cgList[x],0,x,1,1,1.0,1);    
    
    /* [5] now display everything in frame */
    f.add(p,BorderLayout.CENTER);           /* add to frame */
    f.add(lTitle,BorderLayout.NORTH);
    f.add(controlPanel,BorderLayout.SOUTH); /* add to bottom */
    f.setBackground(Color.white);
    f.addWindowListener(this);
    
    f.pack();
    Dimension
    d = f.getPreferredSize();
    f.setSize(d);
    f.setVisible(true);
  } /* buildHPchooserGUI */
  
  
  /**
   * addObj() - add object to GridBagLayout. Uses x,y grid system.
   * See GridBagConstriants in Patrick Chan's book on Java classes II,
   * AWT classes, Revision 2, p.738.
   * @param cont is panel, window, or scrollpane
   * @param arg is the Label/Panel
   * @param x is the x coord  location
   * @param y is the y coord location
   * @param w is the cell width
   * @param h is the cell height
   * @param weightX is the row weight
   * @param weightY is the col weight
   */
  void addObj(Container cont, Object arg, int x, int y,
              int w, int h, double weightX, double weightY)
  { /* addObj */
    GridBagLayout gbl= (GridBagLayout) cont.getLayout();
    GridBagConstraints c= new GridBagConstraints();
    Component comp;
    
    /* set constriants */
    c.fill= GridBagConstraints.HORIZONTAL;
    c.gridx= x;
    c.gridy= y;
    c.gridwidth= w;
    c.gridheight= h;
    c.weightx= weightX;
    c.weighty= weightY;
    
    /* if str then create label else use Component */
    if(arg instanceof String)
      comp= new Label((String) arg);
    else
      comp= (Component) arg;
    
    cont.add(comp);
    gbl.setConstraints(comp, c);
  } /* addObj */
  
  
  /**
   * buildDataStructures() - build the data structures used by the popup chooser.
   * It uses data in the scroller windows from
   *  SampleSets.msListX|Y|E|*[0:nHPX|Y|E|* -1]
   * @see ChooserGUI
   * @see ChooserGUI#buildChooserGUI
   * @see ChooserGUI#setListData
   * @see ChooserGUI#setTitles
   * @see #printChooserState
   * @see #setupHP_XYEchooserLists
   */
  void buildDataStructures()
  { /* buildDataStructures */
    /* [1] Map HP data to working lists will use for creating the
     * choosers.
     * Setup original ms lists. Save the results in:
     *   msListXo[], msListYo[],  msListEo[], msListoXY[], msListoE[].
     * Then generate the corresponding string arrays
     *   sListXo[], sListYo[],  sListEo[], sListoXY[], sListoE[].
     *
     * The SELECTED lists are sListXo[], sListYo[], sListEo[].
     * The REMAINING lists are: sListoXY[] and sListoE[].
     */
    setupHP_XYEchooserLists();
    
    /* [2] Create nChooser ChooseGUI instances */
    cgList= new ChooserGUI[nChooser];      /* array of chooserGUI's */
    boolean addChangePosFlag= true;
    cgList[0]= new ChooserGUI(mae, null, taRows, taCols,
                              titleFont, labelFont, dataFont,
                              this, addChangePosFlag);
    cgList[1]= new ChooserGUI(mae, cgList[0], taRows, taCols,
                              titleFont, labelFont, dataFont,
                              this, addChangePosFlag);
    cgList[2]= new ChooserGUI(mae, null, taRows, taCols,
                              titleFont, labelFont, dataFont,
                              this, addChangePosFlag);
    
    /* [3] Set GUI titles */
    cgList[0].setTitles("Remaining hybridized samples", "HP-X set selected");
    cgList[1].setTitles("" /* Y has no label */, "HP-Y set selected");
    cgList[2].setTitles("Remaining hybridized samples",
                        "HP-E ordered list selected");
    
    /* [4] Set up the HP-X REMAINDER and SELECTED obj lists */
    cgList[0].setListData(mae.MAX_HYB_SAMPLES, 1, nHPrXY, nHPXo,
                          sListrXY, sListXo);
    
    /* [5] Set up the HP-Y REMAINDER and SELECTED obj lists */
    cgList[1].setListData(mae.MAX_HYB_SAMPLES, 1, nHPrXY, nHPYo,
                          sListrXY, sListYo);
    
    /* [6] Set up the HP-E  REMAINDER and SELECTED obj lists */
    cgList[2].setListData(mae.MAX_HYB_SAMPLES, 1, nHPrE, nHPEo,
                          sListrE, sListEo);
    
    /*[7] Build Chooser data structures */
    for(int i=0;i<nChooser;i++)
      cgList[i].buildChooserGUI();
    
    if(mae.CONSOLE_FLAG)
      printChooserState("PHPC id="+id+" - after buildDataStructures()");
  } /* buildDataStructures */
  
  
  /**
   * setupHP_XYEchooserLists() - setup working HP-(X,Y,E) chooser lists.
   * Setup original ms lists. Save the results in:
   *   msListXo[], msListYo[],  msListEo[], msListoXY[], msListoE[].
   * Then generate the corresponding string arrays
   *   sListXo[], sListYo[],  sListEo[], sListoXY[], sListoE[].
   *
   * NOTE: data is specified as [1:n] rather than [0:n-1].
   * @return true if successful.
   */
  private boolean setupHP_XYEchooserLists()
  { /* setupHP_XYEchooserLists */
    /* [1] Compute local ms Lists and difference lists */
    nHPXo= hps.nHP_X;   /* original ('o') HP-X, Y and E samples */
    nHPYo= hps.nHP_Y;
    nHPEo= hps.nHP_E;
    nHP= hps.nHP;
    
    /* NOTE: the only data that we have when we start this chooser is
     * the original (i.e. 'o') HP lists. We compute the remainder lists
     * from these lists.
     */
    msListXo= hps.msListX;
    msListYo= hps.msListY;
    msListEo= hps.msListE;
    msList= hps.msList;
    
    /* [1.1] Compute difference lists/arrays.
     * Compute: msListrXY= msListo - msListXo - msListYo
     * Compute: msListrE= msListo - msListEo
     */
    nHPrXY= 0;
    nHPrE= 0;
    msListrXY= new MaHybridSample[mae.MAX_HYB_SAMPLES+1];
    msListrE= new MaHybridSample[mae.MAX_HYB_SAMPLES+1];
    
    /* output arrays */
    nHPXf= 0;
    nHPYf= 0;
    nHPEf= 0;
    
    boolean foundIt;
    MaHybridSample ms;
    
    /* Compute remainder lists by subtracting (X+Y) from all and E from all. */
    for(int i=1; i<=nHP; i++)
    { /* compute msListoXY and msListoE */
      ms= msList[i];
      
      /* Compute: msListrXY= msListo - msListXo - msListYo */
      foundIt= false;
      
      for(int j=1;j<=nHPXo;j++)
        if(ms==msListXo[j])
        {
          foundIt= true;
          break;
        }
      
      if(!foundIt)
        for(int j=1;j<=nHPYo;j++)
          if(ms==msListYo[j])
          {
            foundIt= true;
            break;
          }
      
      if(!foundIt)
        msListrXY[++nHPrXY]= ms;
      
      /* Compute: msListrE= msListo - msListEo */
      foundIt= false;
      for(int j=1;j<=nHPEo;j++)
        if(ms==msListEo[j])
        {
          foundIt= true;
          break;
        }
      if(!foundIt)
        msListrE[++nHPrE]= ms;
    } /* compute msListoXY and msListoE */
    
    /* [2] Generate corresponding string arrays using the full stage names*/
    sListXo= new String[mae.MAX_HYB_SAMPLES+1];
    sListYo= new String[mae.MAX_HYB_SAMPLES+1];
    sListEo= new String[mae.MAX_HYB_SAMPLES+1];
    sList= new String[mae.MAX_HYB_SAMPLES+1];
    sListrXY= new String[mae.MAX_HYB_SAMPLES+1];
    sListrE= new String[mae.MAX_HYB_SAMPLES+1];
    
    /* [NOTE] could use ms.fullStageText instead of ms.sampleID */
    /* Set up the selected lists */
    for(int i=1;i<=nHPXo;i++)
      sListXo[i]= msListXo[i].sampleID+" ["+
      msListXo[i].hpName+"]";      /* selected*/
    
    for(int i=1;i<=nHPYo;i++)
      sListYo[i]= msListYo[i].sampleID+" ["+
      msListYo[i].hpName+"]";      /* selected*/
    
    for(int i=1;i<=nHPEo;i++)
      sListEo[i]= msListEo[i].sampleID+" ["+
      msListEo[i].hpName+"]";      /* selected*/
    
    for(int i=1;i<=nHP;i++)
      sList[i]= msList[i].sampleID+" ["+
      msList[i].hpName+"]";        /* entire list */
    
    for(int i=1;i<=nHPrXY;i++)
      sListrXY[i]= msListrXY[i].sampleID+" ["+
      msListrXY[i].hpName+"]";     /* remainder */
    
    for(int i=1;i<=nHPrE;i++)
      sListrE[i]= msListrE[i].sampleID+" ["+
      msListrE[i].hpName+"]";      /* remainder */
    
    return(true);
  } /* setupHP_XYEchooserLists */
  
  
  /**
   * setMAEstateFromChooserData() - restore MAE state from chooser data.
   * @see MAExplorer#repaint
   */
  private void setMAEstateFromChooserData()
  { /* setMAEstateFromChooserData  */
    int
      i,
      j;
    String
      oObj,
      fList[];
    
    nHPXf= cgList[0].selObjListSize;
    nHPYf= cgList[1].selObjListSize;
    nHPEf= cgList[2].selObjListSize;
    
    /* [1] Map string result arrays to MaHybridSample arrays.*/
    hps.nHP_X= 0;
    hps.nHP_Y= 0;
    hps.nHP_E= 0;
    
    for(i=1;i<=nHP;i++)
    { /* scan original list for instance of final X list */
      fList= cgList[0].selObjList; /* counts [0:nHPXf] */
      for(j= 0;j<nHPXf;j++)
        if(fList[j].equals(sList[i]))
        { /* found sample in both lists */
          hps.msListX[++hps.nHP_X]= msList[i];
          break;
        }
    } /* scan original list for instance of final X list */
    
    for(i=1;i<=nHP;i++)
    { /* scan original list for instance of final Y list */
      fList= cgList[1].selObjList;
      for(j= 0;j<nHPYf;j++)
        if(fList[j].equals(sList[i]))
        { /* found sample in both lists */
          hps.msListY[++hps.nHP_Y]= msList[i];
          break;
        }
    } /* scan original list for instance of final Y list */
    
    for(i=1;i<=nHP;i++)
    { /* scan original list for instance of final E list */
      fList= cgList[2].selObjList;
      for(j= 0;j<nHPEf;j++)
        if(fList[j].equals(sList[i]))
        { /* found sample in both lists */
          hps.msListE[++hps.nHP_E]= msList[i];
          break;
        }
    } /* scan original list for instance of final E list */
    
   /*
   if(mae.CONSOLE_FLAG)
     printChooserState("setMaeStateFromChooserData - after");
   */
    
    mae.repaint();
  } /* setMAEstateFromChooserData */
  
  
  /**
   * printChooserState() -  print chooser state data.
   * @param msg optional message
   */
  void printChooserState(String msg)
  { /* printChooserState */
    /*
    String
      fListX[]= cgList[0].selObjList,
      fListY[]= cgList[1].selObjList,
      fListE[]= cgList[2].selObjList;
    int
      fListXsize= cgList[0].selObjListSize,
      fListYsize= cgList[1].selObjListSize,
      fListEsize= cgList[2].selObjListSize;
       
    System.out.println("\n-------------\n"+ "PHPC.id="+id+", "+msg);
       
    System.out.println("----\n Final List: fListXsize="+fListXsize);
    for(int j= 0;j<fListXsize;j++)
      System.out.println("  fListX["+j+"]="+fListX[j]);
       
    System.out.println("----\n Final List: fListYsize="+fListYsize);
      for(int j= 0;j<fListYsize;j++)
        System.out.println("  fListY["+j+"]="+fListY[j]);
       
    System.out.println("----\n Final List: fListEsize="+fListXsize);
      for(int j= 0;j<fListEsize;j++)
        System.out.println("  fListE["+j+"]="+fListE[j]);
       
    for(int y=0; y<cgIdx; y++)
      System.out.println(cgList[y].toString("PHPC.id="+id+
                         "\n PHPC.cgList["+y+"] "+msg, true));
       
    System.out.println(" ----- ");
    System.out.println("  hps.nHP="+hps.nHP);
    for(int i=1;i<=hps.nHP;i++)
      System.out.println(" hps.msList["+i+"]="+ hps.msList[i].fullStageText);
       
    System.out.println(" ----- ");
    System.out.println("  hps.nHP_X="+hps.nHP_X);
    for(int i=1;i<=hps.nHP_X;i++)
      System.out.println(" hps.msListX["+i+"]="+ hps.msListX[i].fullStageText);
       
    System.out.println("  hps.nHP_Y="+hps.nHP_Y);
      for(int i=1;i<=hps.nHP_Y;i++)
        System.out.println(" hps.msListY["+i+"]="+ hps.msListY[i].fullStageText);
       
    System.out.println("  nHPrXY="+nHPrXY);
    for(int i=1;i<=nHPrXY;i++)
       System.out.println(" msListrXY["+i+"]="+msListrXY[i].fullStageText);
       
    System.out.println("  hps.nHP_E="+hps.nHP_E);
    for(int i=1;i<=hps.nHP_E;i++)
      System.out.println(" hps.msListE["+i+"]="+hps.msListE[i].fullStageText);
       
    System.out.println("");
    */
  } /* printChooserState */
  
  
  /**
   * updateInfo() - update the info text fields for selected HP
   * Note: this should be called from ChooseGUI when an entry is picked.
   * @param cGUI is instance of ChooserGUI to use
   * @param idx is the field to select
   * @param sEntry is the corresponding entry.
   * @see #repaint
   */
  void updateInfo(ChooserGUI cGUI, int idx, String sEntry)
  { /* updateInfo */
    MaHybridSample
      msTst,
      ms= null;
    String sMStest= "";
    /*
    if(mae.CONSOLE_FLAG)
      System.out.println("PHPC-UI idx="+idx+" sEntry="+sEntry);
    */
    for(int i=1;i<=nHP; i++)
    {
      msTst= msList[i];
      sMStest= msTst.sampleID+" ["+msTst.hpName+"]";
      if(msTst!=null && sEntry.equals(sMStest))
      { /* found it */
        ms= msTst;
        break;
      }
    }
    if(ms==null)
      return;
    
    String
      s1= "HP: "+ ms.sampleID,
      s2= "Project: "+ms.prjName,
      s3= "Title: "+ms.fullStageText;
    /*
    if(mae.CONSOLE_FLAG)
      System.out.println("PHPC-UI. s1="+s1+"\n  s2="+s2+"\n  s3="+s3);
    */
    infoTA1.setText(s1);
    infoTA2.setText(s2);
    infoTA3.setText(s3);
    this.repaint();
  } /* updateInfo */
  
  
  /**
   * actionPerformed() - handle action events
   * buttons and text areas.
   * @param e is action event for button press
   * @see ChooserGUI#resetChooserGUI
   * @see #printChooserState
   * @see #setMAEstateFromChooserData
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    Button item= (Button) e.getSource();
    
    if(cmd.equals(" Cancel "))
      close();
    else if(cmd.equals(" Reset "))
    {
      for(int x=0; x<cgIdx; x++)
        cgList[x].resetChooserGUI(); /* reset data to orig. */
    }
    else if(cmd.equals(" OK "))
    {
      setMAEstateFromChooserData(); /* restore MAE state from final lists */
      
      if(mae.CONSOLE_FLAG)
        printChooserState("PHPC.id="+id+" final Chooser State - OK");
      close();
    }
  } /* actionPerformed */
  
  
  /**
   * windowClosing() - close the window
   * @param e is window closing event
   */
  public void windowClosing(WindowEvent e)
  { /* windowClosing */
    close();
  } /* windowClosing */
  
  
  /**
   * close() - close the window.
   */
  void close()
  { /* close */
    mae.mbf.hpMenuChooserIsPoppedUp= false;
   /*
   if(mae.CONSOLE_FLAG)
     System.out.println("PHPC.quit id="+id);
    */
    dispose();
  } /* close */
  
  
  public void windowActivated(WindowEvent e)  {}
  public void windowClosed(WindowEvent e)  { }
  public void windowDeactivated(WindowEvent e)  {}
  public void windowDeiconified(WindowEvent e)  {}
  public void windowIconified(WindowEvent e)  {}
  public void windowOpened(WindowEvent e)  {}
  
  
  /**
   * keyPressed() - get key Pressed event - look for ESCAPE key to set STOP flag
   * @param e is key pressed event
   */
  public void keyPressed(KeyEvent e)
  { /* keyPressed */
    //System.out.println("GB-KP e="+e);
  } /* keyPressed */
  
  
  /**
   * keyReleased() - get key Released event
   * @param e is key released event
   */
  public void keyReleased(KeyEvent e)
  { /* keyReleased */
    //System.out.println("GB-KR e="+e);
  } /* keyReleased */
  
  
  /**
   * keyTyped() - get key typed event - look for ESCAPE key to set STOP flag
   * @param e is key typed event
   */
  public void keyTyped(KeyEvent e)
  { /* keyTyped */
    char ch= e.getKeyChar();
    // mae.abortFlag= (ch==KeyEvent.VK_ESCAPE);
    /* fio.msgLogln("GB-KT ch='"+ch+"'="+((int)ch)+" abortFlag="+mae.abortFlag);
    */
  } /* keyTyped */
  
  
} /* class PopupHPChooser */
