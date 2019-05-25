/** File: PopupCondChooser.java */

import java.awt.*;
import java.awt.List;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.awt.TextField;
import java.awt.TextArea;
import java.awt.Button;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.awt.TextField;
import java.lang.*;
import java.text.*;
import java.lang.Object;

/**
 * Class PopupCondChooser - GUI to allow users to associate conditions with samples,
 * define parameters of conditions and save within MAExplorer to be used in
 * reports/plots etc. This lets the user create, and edit conditions and properties.
 * A condition is an ordered list of HP samples with associated properties. The
 * properties are shared across all conditions but each condition has a different
 * set of values.
 *<P>
 *<PRE>
 * Description:
 * The user is given a list of condition names. They then select one which causes
 * the listed sample data for that condition to be displayed in the 
 * REMAINDER and SELECTED scrollable lists that are assigned to the 
 * PopupCondChooserGUI (upper part of the window). The REMAINDER list of samples
 * is the list of all HP samples less the SELECTED samples.
 *
 * Each condition has a corresponding (right & left) list of parameters which
 * are placed into the  * NAME and VALUE paired entries in a scroll pane in the
 * lower right.
 *
 * Saving a condition, copies the SELECTED samples to the named condition list
 * using the mjaCondition methods. It also saves the associated parameter
 * (name[],value[]) list for that condition using the mjaCondition methods.
 *
 * New conditions may be defined and the SELECTED list is set to empty and the
 * associated parameter lists are set to empty.
 *
 * New parameters may be defined and are added to the end of the list. Since ALL
 * conditions, use the same list of parameter names, then ALL condition parameter
 * lists must be extended and saved when the conditions are saved.
 *
 * Condition Parameter data is read in (UserStata.java) or uses default values.
 *
 *</PRE>
 *<PRE>
 * It contains the following classes:
 * 1. CondData contains the active remainder and selected samples data for
 *    a condition instance.
 * 2. CondParamEditForm creates a ScrollPane of the (name,value) parameters
 *    for the condition instance.
 * 3. CondDialogBox is a dialog box with with two buttons yesMsg and noMsg that 
 *    will return true or false when pressed.
 * 4. PopupTextField is a simple "editor" for adding a string value. 
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
 * @author G. Thornwall (SAIC), P. Lemkin (NCI), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.18 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
public class PopupCondChooser extends Frame 
      implements ActionListener, WindowListener, ItemListener
{
  final public static boolean
    DBUG_FLAG= false; 
  
  final public static String
    DATE= "$Date: 2004/01/13 16:45:59 $",
    REVISION= "$Revision: 1.18 $",
    VERSION= DATE + " / " + REVISION,
    BANNER= "PopupCondChooser - Version 0.14 ("+ DATE+")";    
  final public static boolean
    CONSOLE_FLAG= true,      /* NEVER CHANGE */
    NEVER= false;            /* NEVER CHANGE */
  /** Error codes for reportMsg() */
  final public static int
    FATAL_MSG= 2,
    WARNING_MSG= 1,
    LOG_MSG= 0;   
  /** MIN size of scrollable Canvas */
  final public static int
    MIN_WINDOW_WIDTH= 530;
  final public static int 
    MIN_WINDOW_HEIGHT= 600; 
  
  /** maximum possible number of conditions */
  private static int 
    maxPossibleCond;
  /** maximum possible number of Condition parameters */
  private static int 
    maxPossibleCondParam;
  /** maximum possible number of hybridized saaples expected */
  public static int 
    maxPossibleSamples;
  
  /** previous instance if not null so can close it 
   * before starting new instance.
   */
  private static PopupCondChooser
     oldPCC= null;
   
  /** MaeJavaAPI classes */
  private MJAcondition
    mjaCondition;
  private MJAfilter
    mjaFilter;
  private MJAproperty
    mjaProperty;
  private MJAreport
    mjaReport;
  private MJAutil
    mjaUtil;
  private MJAsample
    mjaSample;
   
  /* --------------------------------------------------- */
  /* GUI objects etc.                                    */
  /* --------------------------------------------------- */
  /** Add condition */
  private Button
    addCondButton;
  /** remove condition */
  private Button
    removeCondButton;
  /** list condition */
  private Button
    listCondButton;
  /** list all condition */
  private Button
    listAllCondButton;
  /** add condition parameter */
  private Button
    addParamButton;
  /** remove condition parameter */
  private Button
    rmvParamButton;
  /** save */
  private Button
    saveButton;
  /** done */
  private Button
    doneButton;
  /** cancel */
  private Button
    cancelButton;
  /** size for PopupCondChooserGUI */
  private int
    pccgWidth;
  /** size for PopupCondChooserGUI */
  private int
    pccgHeight;          
  /** preferred height size of frame*/
  private int
    preferredHeight= MIN_WINDOW_HEIGHT;                
  /** preferred width size of frame */
  private int 
    preferredWidth= MIN_WINDOW_WIDTH;
  
  /** Font family from MAExplorer - using mjaProperty.getReportFontFamily() */
  private String 
    fontFamily;
  
  /** font for data used in the Chooser GUI */
  private Font
   dataFont;
  /** font for buttons */
  private Font
   buttonFont;
  /** font for labels */
  private Font
    labelFont;
  /** font for labels */
  private Font
    titleFont;
  /** font for title at the top of the main window */
  private Font
    msgFont;
  /** font for parameter Name and Value */
  private Font
    paramFont;
  /** font for conditions */
  private Font
    condFont;
  
  /** Text message report line */
  private TextField
    msgTF;
  /** Text message foreground color that may be changed */
  private Color
    msgColor= Color.black;
  /** Text message background color that may be changed */
  private Color
    msgBkgrd= Color.white;
  /** Text data report foreground color that may be changed */
  private Color
    dataColor= Color.black;
  /** Text data report background color that may be changed */
  private Color
    dataBkgrd= Color.white;
  
  /** The working list of conditions for GUI */
  private List
    condList; 
  
  /** Panel for condition data */
  private Panel
    chooserPanel;
  /** Panel to hold conditions list (LEFT) and Param Editor (RIGHT) */
  private Panel
    condPanel;
  
  /** msg display 1 */
  private TextField
    pccMsgTxtField1;
   /** msg display 2 */
  private TextField
    pccMsgTxtField2;
   /** msg display 3 */
  private TextField
    pccMsgTxtField3;
   /** error msg display */
  private TextField
    condErrorMsgTf;
  
  /* ------------------------------------------------------ */
  /*  Data structure                                        */
  /* ------------------------------------------------------ */ 
  
  /** Title banner for the main window */
  private String
    banner;
  
  /** List [0:maxPossibleCond-1] of working condition 
   * data: name, selected and remainder samples, 
   * param form (name,value).
   */
  private CondData
    cd[];
  /** number of condition lists */  
  private int
    nbrCondLists;
  /** CondParamEditForm instance */
  private CondParamEditForm
    cpef;
  
  /** The current condition name */
  private String
    curCond;
  
  /** list [0:nbrCondLists-1] of condition parameter names for
   * CondParamEditForm.
   */
  private String
    paramNames[];
  /** # of condition parameters */
  private int
    nbrParams; 
  
  /** Flag enabled if doing "SaveAs" */
  private boolean
    saveReportAsTextFileFlag; 
  /** see if saved ok*/
  private boolean
    saveFlag=false;
  /** if something is changed, need to save it so keep track */
  private boolean
    valueChangedFlag= false;
  
  /** title of remainder samples TextArea for PopupCondChooserGUI */
  private String
    remTitle;
  /** title of selected samples TextArea for PopupCondChooserGUI */
  private String
    selTitle;
  
  /** previous CondParamEditForm index */
  private int
    prevCPEFidx= 0;
  /** current cond index */
  private int
    curCondIdx= 0; 
  
  /** list of condition names */  
  private String
    condListNames[];
  /** show current condition name */
  private Label
    curCondNameLabel;    
  /** show current condition name */
  private String
    curCondListName;
  
  /** Maximum # of samples currently in the database. This is the largest
   * size (in # samples) a condition list can be.
   */
  private int
    maxNbrSamples;
  /** list of ALL samples in the database */
  private String
    allSampleNames[];
  
  /** remaining sample names of condition before edit */
  private String
    remSamplesTmp[];
  /** selected sample names of condition before edit */
  private String
    selSamplesTmp[];
  
  /** chooser GUI for sample data */
  private PopupCondChooserGUI
    pccgSample;
  /** previous chooser GUI for sample data */
  private PopupCondChooserGUI
    oldPccgSample;
  
  
  /**
   * PopupCondChooser() - Constructor
   * @param mja interface with MAExplorer via API.
   * @param oldPCC is previous instance if not null 
   * @see initDataAndGUI
   */
  public PopupCondChooser(MaeJavaAPI mja, PopupCondChooser oldPCC)
  { /* PopupCondChooser */ 
    /* [1] Since can only have 1 copy, pop-down old copy first */
     if(oldPCC!=null)
     {
       oldPCC.close();   /* previous instance if not null */  
     }
     oldPCC= this;        /* save this instance */
  
    /* [1.1] Access the parts of the Open Java API required through
     * MaeJavaAPI instance.
     */
    mjaCondition= mja.mjaCondition;
    mjaFilter= mja.mjaFilter;
    mjaProperty= mja.mjaProperty;
    mjaReport= mja.mjaReport;
    mjaSample= mja.mjaSample;
    mjaUtil= mja.mjaUtil;
    
    banner= "Condition Chooser - define and edit condition lists of samples";
    
    /* [2] Notify user that creating the Chooser. */
    mjaUtil.showMsg1("Condition Chooser enables defining and editing lists of samples");
    
    /* [3] Build the specific data structures and GUI  */
    initData();
    
    /* [4] Note: everything below this needs active Graphical objects
     * so buildGUI(), MUST be here. 
     */
    buildGUI();    
    repaint();
  } /* PopupCondChooser */
  
  
  /**
   * initData() - initialize the database.
   * @see MJAcondition#getCondListsNames
   * @see MJAcondition#getCondListsSizes
   * @see MJAcondition#getMaxCondListSize
   * @see MJAcondition#getNbrCondLists
   * @see buildGUI
   * @see CondParamEditForm
   * @see setupCondData
   */
  void initData()
  { /* initDataAndGUI */    
    /* [1] Default sizes defined in MAExplorer */
    maxPossibleCond= mjaCondition.getMaxCondListSize();
    maxPossibleCondParam= mjaCondition.getMaxNbrCondParams();
    maxPossibleSamples= mjaCondition.getMaxNbrSamplesInDB();
    
    /* [1.1] condition list of names */   
    nbrCondLists= mjaCondition.getNbrCondLists();    
    condListNames= mjaCondition.getCondListsNames();  
    
    /* [1.2] Get a list of all samples in the database */
    allSampleNames= mjaCondition.getAllSampleNamesInDB();      
    maxNbrSamples= allSampleNames.length;
    
    /* [1.3] Get list of parameter names which are the same for
     * all conditions.
     */
    paramNames= mjaCondition.getCondParamNames(); 
    nbrParams= (paramNames!=null) ? paramNames.length : 0; 

    /* [2] Setup condtions, get condition list, etc */
    cd= new CondData[maxPossibleCond];
    
    setupCondData();       
  } /* initData */

  
  /**
   * setupCondData() -  get remainder and selected condition data, & parameters
   * from MJA (indirectly from mae) and set up the cd[] array of conditions data.
   * @see CondParamEditForm#buildGUI
   * @see MJAcondition#getCondParamNames
   * s@ee MJAcondition#getCondParamValues
   * @see MJAcondition#getSampleNamesInCondList
   * @see MJAcondition#getNbrCondLists
   * @see calcRemSamples
   * @see CondData
   * @see CondParamEditForm
   */
  public void setupCondData()
  { /* setupCondData */
    /* [1] setup some defaults */
    int
      selLength= 0,
      remLength= 0;
       
    /* [2] setup max nbr of conditions and samples for data arrays,
     * get condition data from MJA.
     */     
    for(int x=0; x < nbrCondLists; x++)
    { /* create parameters for each condition list */    
      /* [2.1] setup condition parameter forms */       
      String 
        condName= condListNames[x],
        condParamValues[]= mjaCondition.getCondParamValues(condName);
      /* NOTE: empty parameter values should be "", NOT null so make sure! */
      for(int k=0;k<nbrParams;k++)
        if(condParamValues[k]==null)
          condParamValues[k]= "";
      
      cd[x]= new CondData(condListNames[x], maxPossibleSamples, 
                          nbrParams, paramNames, condParamValues);  
      
      /* [2.2] Get Sample Names in Condition List */
      selSamplesTmp=  mjaCondition.getSampleNamesInCondList(condName); 
      selLength= (selSamplesTmp==null) ? 0 : selSamplesTmp.length;
    
      /* [2.3] Calculate the remainder; R = DB - Ci (DB is mList[]) */
      remSamplesTmp= calcRemSamples(condName, -1);
      remLength= (remSamplesTmp == null) ? 0 : remSamplesTmp.length;
      
      /* [2.4] Remember the selected samples length for GUI */      
      cd[x].nSelSamples= selLength;
      cd[x].nRemSamples= remLength;
      
      /* [2.5] save samples */
      for(int y=0; y < selLength; y++)
        cd[x].selSamples[y]= selSamplesTmp[y];

      for(int y=0; y < remLength; y++)
        cd[x].remSamples[y]= remSamplesTmp[y];  
    } /* create parameters for each condition list */
  } /* setupCondData */
  
  
  /**
   * calcRemSamples() - compute remainder list of sample names (R = DB - Ci)
   * where DB is all of the samples in the database, and Ci are the samples
   * in condition Ci.
   * @param condListName is the condition list Ci name
   * @param curIndex is the index of the current condition to use if not -1
   * @return list of remainder sample names, else null if failed.
   * @see MJAcondition#getSampleNamesInCondList
   *
   */
  private String[] calcRemSamples(String condListName, int curIndex)                                       
  { /* calcRemSamples */    
    /* Get data */
    String 
      tempAllSampleNames[]= allSampleNames,
      curSampleNames[];
    
    if(curIndex==-1)
      curSampleNames= mjaCondition.getSampleNamesInCondList(condListName);
    else
      curSampleNames= cd[curIndex].selSamples;     
    
    if(tempAllSampleNames==null || curSampleNames==null)
      return(null);
    
    int
      maxSamples= tempAllSampleNames.length,
      nCurSamples= curSampleNames.length,
      nRemSamples= 0;
    String
      curName,
      allName,
      rList[]= new String[maxSamples];
 
    for(int j=0;j<maxSamples;j++)
    { /* convolve the two lists */
      boolean inListFlag= false;
      allName= tempAllSampleNames[j];     
      
      for(int i=0;i<nCurSamples;i++)
        if(allName.equals(curSampleNames[i]))
        { /* yes, it current list so do NOT save in remainder list */
          inListFlag= true;
          break;
        }
      if(!inListFlag)
        rList[nRemSamples++]= allName;
    }/* convolve the two lists */      
    
    String remSampleNames[]= new String[nRemSamples];
    for(int i=0;i<nRemSamples;i++)
      remSampleNames[i]= rList[i];
    
    return(remSampleNames);
  } /* calcRemSamples */
        
  
  /**
   * buildGUI() - build the graphical user interface for PopupCondChooser.
   * @see MJAcondition#PopupCondChooserGUI
   * @see #getCondIdxFromName
   * @see MJAcondition#getCondListsNames
   * @see MJAcondition#getCurCondition
   * @see MJAcondition#getMaxCondListSize
   * @see MJAcondition#getNbrCondLists
   * @see MJAcondition#getSampleNamesInCondList
   * @see buildPopupCondChooserGUI
   * @see CondParamEditForm
   */
  public void buildGUI()
  { /* buildGUI */
    int
      selLength= 0,
      remLength= 0;
    
    pccgWidth= 30;
    pccgHeight= 15;
    
    /* [1] Setup the layouts */
    FlowLayout fl= new FlowLayout();
    BorderLayout bl= new BorderLayout();
    GridLayout condParamEditFormGl= new GridLayout();
    
    /* [2] Fonts - use the same Font family from MAExplorer and
     * use it for ALL fonts for consistency.
     */
    fontFamily= mjaProperty.getReportFontFamily();
  
    titleFont= new Font(fontFamily, Font.PLAIN, 12);
    msgFont= new Font(fontFamily, Font.PLAIN, 12);
    buttonFont= new Font(fontFamily, Font.PLAIN,12);  
    labelFont= new Font(fontFamily, Font.PLAIN, 12); 
    
    dataFont= new Font(fontFamily, Font.PLAIN, 10);  
    paramFont= new Font(fontFamily, Font.PLAIN, 10);  
    condFont= new Font(fontFamily, Font.PLAIN, 10);  
    
    /* [3] Create panels */
    chooserPanel= new Panel(new FlowLayout());
    condPanel= new Panel();
    
    Panel
      samplesPanel= new Panel(),
      miscPanel= new Panel(),
      mainCondPanel= new Panel(),
      bottomPanel= new Panel(),
      centerPanel= new Panel(),
      topPanel= new Panel(),
      buttonPanel= new Panel(),
      msgPanel= new Panel();
    
    /* [4] Set background colors & fonts*/
    samplesPanel.setBackground(Color.white);
    condPanel.setBackground(Color.white);
    mainCondPanel.setBackground(Color.white);
    miscPanel.setBackground(Color.white);
    centerPanel.setBackground(Color.white);
    buttonPanel.setBackground(Color.white);
    bottomPanel.setBackground(Color.white);
    this.setBackground(Color.white);   
    
    /* [5] Setup panel layouts */
    condParamEditFormGl.setColumns(2);
    condParamEditFormGl.setRows(4);
    samplesPanel.setLayout(new BorderLayout());
    condPanel.setLayout(new GridLayout(1,2));
    mainCondPanel.setLayout(new BorderLayout());
    buttonPanel.setLayout(fl);
    msgPanel.setLayout(new GridLayout(3,1));
    
    /* [6] Get basic data/info on conditions */
    condList= new List(nbrCondLists, false);
    condList.addItemListener(this);
    
    /* [7] Get current ocl */
    
    curCondListName= mjaCondition.getCurCondition();  /* get last current ocl from prev state, if exists */
    if(curCondListName != null)
      curCondIdx= getCondIdxFromName(curCondListName);
    else
      curCondIdx= 0;                 /* set current condition to first condition */
    
    prevCPEFidx= 0;                /* the previous condition captured when
                                    * you change the current condition */
    
    /* [8] Setup the shared Parameter Editor panel. This builds the GUI for 
     * current condition create parameters for each condition list.
     * Since this is the first instance, there is no previousl instance
     * data to save.
     */        
    cpef= new CondParamEditForm(this, cd[curCondIdx], null);
    
    /* [9] Get list of samples of current condition (default) for GUI */
    curCondListName= condListNames[curCondIdx]; 
    curCondNameLabel= new Label(curCondListName);
    
    oldPccgSample= null;
    pccgSample= new PopupCondChooserGUI(this,null, maxPossibleSamples,
                                       pccgHeight, pccgWidth, titleFont, 
                                       labelFont, dataFont, buttonFont,
                                       this, true);    
    
    remTitle= "Remainder Samples"; /* titles */
    selTitle= "Selected Samples in current condition";
    
    pccgSample.setTitles(remTitle, selTitle);
    remLength= (remSamplesTmp == null) 
                  ? 0 : remSamplesTmp.length;
    
    pccgSample.setListData(maxNbrSamples,                    /* max # objs */
                          0,                                /* offset */
                          cd[curCondIdx].nRemSamples, /* # remainder objs */
                          cd[curCondIdx].nSelSamples, /* # selected  objs */
                          cd[curCondIdx].remSamples,  /* remainder data */
                          cd[curCondIdx].selSamples); /* selected data */
    pccgSample.buildPopupCondChooserGUI();
    chooserPanel.add(pccgSample);
    condList= new java.awt.List(nbrCondLists, false);
    condList.addItemListener(this);
 
    for(int z=0 ; z< nbrCondLists; z++)
      condList.addItem(condListNames[z]);
    
    if(curCondIdx >= 0)
      condList.select(curCondIdx);
    else
      condList.select(0);
    
    /* [10] Add everthing to main GUI frame */
    int pccMsgTxtFieldSize= 60;
    pccMsgTxtField1= new TextField(pccMsgTxtFieldSize);
    pccMsgTxtField2= new TextField(pccMsgTxtFieldSize);
    pccMsgTxtField3= new TextField(pccMsgTxtFieldSize);
    
    pccMsgTxtField1.setFont(msgFont);
    pccMsgTxtField2.setFont(msgFont);
    pccMsgTxtField3.setFont(msgFont);
    
    condList.setFont(condFont);    
    
    miscPanel.add(new Label("     "));
    miscPanel.add(curCondNameLabel);
  
    msgPanel.add(pccMsgTxtField1);
    msgPanel.add(pccMsgTxtField2);
    msgPanel.add(pccMsgTxtField3);
    
    /* [NOTE] redo this using a GridBagLayout to shrink the top label for banner
     * which needs to be added.
     */
    /*
    Panel titleAndchooserPanel= new Panel(); 
    titleAndchooserPanel.setLayout(new GridLayout(2,1));
    titleAndchooserPanel.add(new Label(banner));    
    titleAndchooserPanel.add(chooserPanel);    
    samplesPanel.add(titleAndchooserPanel, BorderLayout.NORTH);    
    */
    
    samplesPanel.add(chooserPanel, BorderLayout.NORTH);
    
    samplesPanel.add(miscPanel, BorderLayout.CENTER);
    samplesPanel.add(msgPanel, BorderLayout.SOUTH);
      
    condPanel.add(condList);  
    condPanel.add(cpef);    
    mainCondPanel.add(condPanel,BorderLayout.SOUTH);
    
    /* NOTE: The white space is necessary to force the grid layout 
     * to line up the titles in the GUI for the (left) list of conditions
     * and the (right) current condition annotation windows..
     */
    String mainCondTitle= 
           "    List of Conditions                                                      "+
           "            Current Condition Annotation";
    Label tmpLabel= new Label(mainCondTitle);
    mainCondPanel.add(tmpLabel, BorderLayout.NORTH);
  
    topPanel.add(samplesPanel);               /* North */
    
    centerPanel.add(mainCondPanel);      /* Center */
    
    addCondButton= new Button("Add Cond");
    removeCondButton= new Button("Remove Cond");
    listCondButton= new Button("List Cond");
    listAllCondButton= new Button("List All");
    addParamButton= new Button("Add Ann");
    rmvParamButton= new Button("Remove Ann");
    saveButton= new Button("Save");
    doneButton= new Button("Done");
    cancelButton= new Button("Cancel");

    addCondButton.setFont(buttonFont);
    removeCondButton.setFont(buttonFont);
    listCondButton.setFont(buttonFont);
    listAllCondButton.setFont(buttonFont);
    addParamButton.setFont(buttonFont);
    rmvParamButton.setFont(buttonFont);
    saveButton.setFont(buttonFont);
    doneButton.setFont(buttonFont);
    cancelButton.setFont(buttonFont);
        
    addCondButton.addActionListener(this); 
    removeCondButton.addActionListener(this);
    listCondButton.addActionListener(this);
    listAllCondButton.addActionListener(this);
    addParamButton.addActionListener(this);
    rmvParamButton.addActionListener(this);
    saveButton.addActionListener(this);
    doneButton.addActionListener(this);
    cancelButton.addActionListener(this);
    
    buttonPanel.add(addCondButton);
    buttonPanel.add(removeCondButton);
    buttonPanel.add(listCondButton);
    buttonPanel.add(listAllCondButton);
    buttonPanel.add(addParamButton);
    buttonPanel.add(rmvParamButton);
    buttonPanel.add(saveButton);
    buttonPanel.add(doneButton);
    buttonPanel.add(cancelButton);
    this.addWindowListener(this);    
    
    this.setLayout(new BorderLayout());
    this.add(buttonPanel, BorderLayout.SOUTH);
    this.add(centerPanel, BorderLayout.CENTER);
    this.add(topPanel, BorderLayout.NORTH);
    this.add(new Label(" "),BorderLayout.EAST);
    this.add(new Label(" "),BorderLayout.WEST);
    
    this.setTitle(banner);
    
    Dimension d= getPreferredSize();
    this.setSize(d);
    
    this.pack();
    this.setVisible(true);
  } /* buildGUI */
  
  
  /**
   * addAllSamplesForCond() - Place samples data associated with new condition into ChooserGUI.
   * Save old samples data, remove samples data and then add new data.
   * @param cond Condition set to add samples
   * @param newCurIndex index for data
   * @see PopupCondChooserGUI#buildPopupCondChooserGUI
   * @see MJAcondition#getAllSampleNamesInDB
   * @see getRemSamplesData
   * @see getSelSamplesData
   * @see PopupCondChooserGUI
   */
  private void addAllSamplesForCond(String cond, int newCurIndex)
  { /* addAllSamplesForCond */     
    int
      selLength= 0,
      remLength= 0;
    CondData curCD= cd[curCondIdx];
    
    curCD.remSamples= new String[maxPossibleSamples];
    curCD.selSamples= new String[maxPossibleSamples];
    
    /* [1] save remainder data from chooser GUI */
    for(int x=0;x < pccgSample.remObjListSize; x++)
      curCD.remSamples[x]= pccgSample.remObjList[x];
    
    curCD.nRemSamples= pccgSample.remObjListSize;
    
    /* [2] save select data from chooser GUI */
    for(int x=0;x < pccgSample.selObjListSize; x++)
      curCD.selSamples[x]= pccgSample.selObjList[x];
    
    curCD.nSelSamples= pccgSample.selObjListSize;
    
    /* [3] Get name AFTER set the new index */
    curCondIdx= newCurIndex;    
    String condListName= condListNames[curCondIdx]; 
    
    /* [4] clear old chooserGUI */
    chooserPanel.remove(pccgSample);
    
    /* [5] Get new list of samples for condition and create 
     * new PopupCondChooserGUI.
     */
    selSamplesTmp= getSelSamplesData(newCurIndex);    
    selLength= (selSamplesTmp == null) ? 0 : selSamplesTmp.length;
    
    remSamplesTmp= getRemSamplesData(newCurIndex); /* get locally */
    remLength= (remSamplesTmp == null) 
                   ? 0 : remSamplesTmp.length;
   
    pccgSample= new PopupCondChooserGUI(this,null, maxNbrSamples,
                                       pccgHeight, pccgWidth, titleFont,
                                       labelFont, dataFont, buttonFont, 
                                       this, true);
    pccgSample.setTitles(remTitle, selTitle);
    
    pccgSample.setListData(maxNbrSamples,            /* max # objs */
                          0,                        /* offset */
                          remLength,                /* # remainder objs */
                          selLength,                /* # selected  objs */
                          remSamplesTmp,            /* remainder data */
                          selSamplesTmp);           /* selected data */
    
    if(oldPccgSample!=null)
      chooserPanel.remove(oldPccgSample);
    
    pccgSample.buildPopupCondChooserGUI();
    chooserPanel.add(pccgSample);
    
    oldPccgSample= pccgSample;
    
    condListName= condListNames[newCurIndex];
    curCondNameLabel.setText(condListName);
   
    pack();
    repaint();
  } /* addAllSamplesForCond */
  
  
  /**
   * getRemSamplesDataFromChooserGUI() - get remainder data from chooserGUI
   * @return list of remainder data
   * @param index which  
   */
  private String[] getRemSamplesDataFromChooserGUI(int index)
  { /* getRemSamplesDataFromChooserGUI */
    int len= pccgSample.remObjListSize;
    String tmpStr[]= new String[len];
    for(int x=0;x < len; x++)
      cd[index].remSamples[x]= pccgSample.remObjList[x];
    cd[index].nRemSamples= len;
    return(tmpStr);
  } /* getRemSamplesDataFromChooserGUI */
  
  
  /**
   * getSelSamplesDataFromChooserGUI() -  get selected data from chooserGUI
   * @return list of selected data
   * @param index which condition index
   */
  private String[] getSelSamplesDataFromChooserGUI(int index)
  { /* getSelSamplesDataFromChooserGUI */
    int len= pccgSample.selObjListSize;
    String tmpStr[]= new String[len];
    for(int x=0;x < len; x++)
      cd[index].selSamples[x]= pccgSample.selObjList[x];
    cd[index].nSelSamples= len;
    return(tmpStr);
  } /* getSelSamplesDataFromChooserGUI */
  
  
  /**
   * getSelSamplesData() -  get Select data, locally.
   * @return list of selected data
   * @param index which condition index
   */
  private String[] getSelSamplesData(int index)
  { /* getSelSamplesDataFromChooserGUI*/
    int len= cd[index].nSelSamples;
    String tmpStr[]= new String[len];
    for(int x=0; x < len; x++)
      tmpStr[x]= cd[index].selSamples[x];
    return(tmpStr);
  } /* getSelSamplesDataFromChooserGUI */
  
  
  /**
   * getRemSamplesData() -  get remainder data, locally.
   * @return list of remainder data
   * @param index which condition index
   */
  private String[] getRemSamplesData(int index)
  { /* getRemSamplesData */
    int len= cd[index].nRemSamples;
    String tmpStr[]= new String[len];
    for(int x=0; x < len; x++)
      tmpStr[x]= cd[index].remSamples[x];
    return(tmpStr);
  } /* getRemSamplesData */
    
  
  /**
   * getCondIdxFromName() - Look up and return index of current cond.
   * @return index or -1 for none.
   * @param condName name of condition list
   */
  int getCondIdxFromName(String condName)
  {/* getCondIdxFromName */
    if(condName!=null)
    {
      for(int x=0; x<nbrCondLists; x++)
        if(condName.equals(condListNames[x]))
          return(x);
    }
   return(-1); /* not found or error */
  }/* getCondIdxFromName */
  
  
  /**
   * getCondNameFromIdx() - Look up and return name of current ocl.
   * @return name or null for none.
   * @param oclIdx of ordered condition list
   */
  String getCondNameFromIdx(int condIdx)
  {/* getCondNameFromIdx */
    if(condIdx >= 0 && condIdx <nbrCondLists)
      return(condListNames[condIdx]);
    return(null); /* not found or error */
  }/* getCondNameFromIdx */
  
  
  /**
   * paint() - repaint full scrolled text area.
   */
  public void paint(Graphics g)
  { /* paint */
    
  } /* paint */
  
  
  /**
   * update() - update window
   * @param g is graphics context
   */
  public void update(Graphics g)
  { /* update */
    paint(g);
  } /* update */  
  
  
  /**
   * getSampleNbrBySampleName() - lookup the sample # by its name
   * @param sampleName is the sample number tolookup 
   * @return the index if found, else return -1
   */
  private int getSampleNbrBySampleName(String sampleName)
  { /* getSampleNbrBySampleName */
    for(int i=0; i<allSampleNames.length;i++)
      if(allSampleNames[i].equals(sampleName))
        return(i);
    return(-1); /* failed */
  } /* getSampleNbrBySampleName */  
  
  
  /**
   * updateInfo() - update the info text fields for selected HP.
   * If the sample does not exist in the database, then it is a NO-OP.
   * @param sampleName is the sample number to be displayed
   * @see #repaint
   */
  void updateInfo(String sampleName)
  { /* updateInfo */ 
    /* Lookup current selected sample that was selected from CondChooserGUI */
    int sampleNbr= getSampleNbrBySampleName(sampleName);
    if(sampleNbr==-1)
      return; 
    
    String
      sampleID= mjaSample.getSampleIDbySampleNbr(sampleNbr),
      prjName= mjaSample.getProjectNameBySampleNbr(sampleNbr),
      fullSampleText= mjaSample.getFullSampleTextBySampleNbr(sampleNbr);
    if(sampleID==null || prjName==null || fullSampleText==null)
      return;
    
    String
      s1= "HP: "+ sampleID,
      s2= "Project: " + prjName, 
      s3= "Title: " + fullSampleText;
    
    /* Stuff them in the 3 centered text fields*/
    pccMsgTxtField1.setText(s1);
    pccMsgTxtField2.setText(s2);
    pccMsgTxtField3.setText(s3);
    this.repaint();
  } /* updateInfo */
  
  
  /** 
   * saveAllCondsToMaeState() - save all conditions to MAExplorer state
   */
  private void saveAllCondsToMaeState()
  { /* saveAllCondsToMaeState */
    /* [1] Save condition parameter into current cd */
    cpef.saveData();
    
    /* [2] Set/Save each cond list data into MAE via MJA,
     * save remainder data from chooser GUI.
     */
    cd[curCondIdx].remSamples= new String[maxPossibleSamples];
    cd[curCondIdx].selSamples= new String[maxPossibleSamples];
    
    for(int x=0; x < pccgSample.remObjListSize; x++)
      cd[curCondIdx].remSamples[x]= pccgSample.remObjList[x];
    
    cd[curCondIdx].nRemSamples= pccgSample.remObjListSize;
    
    /* [2.2] save select data from chooser GUI */
    for(int x=0;x < pccgSample.selObjListSize; x++)
      cd[curCondIdx].selSamples[x]= pccgSample.selObjList[x];
    
    cd[curCondIdx].nSelSamples= pccgSample.selObjListSize;
    
    saveFlag= saveCondState(cd, nbrCondLists);
    
    pccgSample.valueChangedFlag= false;
    valueChangedFlag= false;          /* reset the flag */
  } /* saveAllCondsToMaeState */
  
  
  /**
   * addNewCondition() - add new condition to local chooser database
   * and leave it as the selected condition
   */
  private void addNewCondition()
  { /* addNewCondition */
    /* [1] Prompt user for new condName */
    PopupTextField ptf= new PopupTextField(this, "Add condition ",
                                           this, "Enter new condion name");
    String newCondName= ptf.newString;
    
    /* [2] Check valid name */
    if(ptf.stringValidFlag)
    { /* valid name */
      
      /* [2.1] Make sure the name is not already on the list - else error */
      for(int x= 0; x < nbrCondLists; x++)
        if(newCondName.equals(cd[x].condName))
        {
          mjaUtil.showMsg1("Can't add the same condition name twice.");
          Util.popupAlertMsg("Can't add the same condition name twice",
                            "Can't add the same condition name twice",
                            4, 60);
          return;
        }
    } /* valid name */
    
    /* [3] Create new condList */
    if(! mjaCondition.createNewCondList(newCondName))
    { /* trouble - possible DRYROT error */
      if(DBUG_FLAG)
        System.out.println("PCC-AP: DRYROT - problem creating condition list");
    } /* trouble - possible DRYROT error */
    
    else
    {/* Create new CondData */
      /* [4] Create new CondData */
      String emptyParamValues[]= new String[nbrParams];
      for(int i=0;i<nbrParams;i++)
        emptyParamValues[i]= "";  /* must not be null! */
      CondData newCd= new CondData(newCondName, maxPossibleSamples,
                                   nbrParams, paramNames, emptyParamValues);
      
      /* [5] Place all samples in remainder since user will have to select them */
      newCd.remSamples= allSampleNames;
      newCd.nRemSamples= maxNbrSamples;
      newCd.nSelSamples= 0;
      
      /* [6] Update the editor panel to new Parameter values and repaint */
      condPanel.remove(cpef);
      cpef= new CondParamEditForm(this, newCd, cpef);
      /* Add the rebuilt new current condition */
      condPanel.add(cpef);
      this.pack();
     
      /* [7] Save the new condition */
      cd[nbrCondLists]= newCd;   /* push new condition onto list of conds */
      if(nbrCondLists==0)
        removeCondButton.setEnabled(true);/* added one */
      nbrCondLists++;
      
      /* [8] update condListNames from local source & display the new list */
      condListNames= new String[nbrCondLists];
      for(int z=0; z<nbrCondLists; z++) 
        condListNames[z]= cd[z].condName;
      
      condList.addItem(newCondName); 
      
      this.pack();
      this.setVisible(true);
      valueChangedFlag= true;  /* set the flag since things have changed and
                                * will need to save */
    } /* Create new CondData */
  }  /* addNewCondition */
    
  
  /**
   * removeConditionLocally() - remove user specified condition locally.
   * @see #getCondNameFromIdx
   * @see  MJAcondition#setCurCondition
   **/
  public void removeConditionLocally()
  { /* removeConditionLocally */
    /* [1] Let user pick condition to remove via special GUI */
    ChoiceList cl= new ChoiceList(this, "Choose a Condition to remove",
                                  condListNames, nbrCondLists, 
                                  "Condition", this);
    
    /* [2] Find it locally and remove it, rebuild GUI.
     * [NOTE]: must save it via save button or will not be saved
     * into MAExplorer */
    if(cl.removeFlag)
    { /* try to remove it */
      String condNameToRemove= cl.returnItem; /* get cond to be removed */
      int
        tmpIndex=0,
        condFoundIndex= -1;
      
      CondData cdTmp[]= new CondData[maxPossibleCond];
      
      for(int z=0; z<nbrCondLists; z++)
      {/* search for cond to be removed, build new array of cd w/o cond */
        
        if(cd[z].condName.equals(condNameToRemove))
          condFoundIndex= z;  /* found local in cd, skip placing it into cdTmp */
        else
          cdTmp[tmpIndex++]= cd[z];    /* save */
      }/* search for cond to be removed, build new array of cd w/o cond */
      
      if(condFoundIndex != -1)
      { /* remove the condition they specified */
        /* [2.1] Reset certain variables */
        nbrCondLists--;        /* one less */
        if(nbrCondLists==0)
          removeCondButton.setEnabled(false); /* if none left, no need to try to remove */
        cd= cdTmp;             /* restore new list w/o the one removed */
        
        valueChangedFlag= true;/* reset the flag */
               
        /* [2.2] Remove cond params & list of conditions */
        condPanel.remove(cpef);
        condPanel.remove(condList);
        
        /* [2.3] Create new list of conditions and parameters */
        condList= new List(nbrCondLists, false);
        condList.addItemListener(this);
        
        /* [2.4] Save the new current cond to the state */ 
        if(condFoundIndex == curCondIdx)
        {
          condList.select(0);/* rmv same cond as curCond */
          curCondIdx= 0;
        }
        else
          condList.select(curCondIdx);
        
        String curConditionname= getCondNameFromIdx(curCondIdx);
        mjaCondition.setCurCondition(curConditionname);
        
        condList.setFont(condFont);
        
        condListNames = new String[nbrCondLists]; /* redue list of names */
        for(int z=0; z< nbrCondLists; z++)
        {
          condListNames[z]= cd[z].condName;
          condList.addItem(cd[z].condName);
        }
        cpef= new CondParamEditForm(this, cd[curCondIdx], null);
        
        /* [2.5] Add them back */
        condPanel.add(condList);
        condPanel.add(cpef);
        this.pack();
      } /* remove the condition they specified */
      
      /* [TODO] handle problem where no more samples and what do we select next? */
      
    }  /* try to remove it */
  } /* removeConditionLocally */    
    

  /**
   * addNewParameter() - add a new parameter locally. Popup window
   * for new condition parameter name.
   **/
  public void addNewParameter()
  { /* addNewParameter */
    /* [1] popup window to prompt window for new cond param name */
    PopupTextField ptf= new PopupTextField(this, 
                                           "Add condition parameter name",
                                           this, "Enter new parameter name");
    
    /* [2] Save new param name data */
    if(ptf.stringValidFlag)
    { /* valid name */
      /* Make sure the name is not already on the list - else error */
      for(int x= 0; x<nbrParams; x++)
        if(ptf.newString.equals(paramNames[x]))
        {
          mjaUtil.showMsg1("Can't add the same parameter name twice.");
          Util.popupAlertMsg("Can't add the same parameter name twice",
                            "Can't add the same parameter name twice",
                            4, 60);
          return;
        }
      
      /* [2.1] Save current condition parameters into current cd if needed */
      cpef.saveData();
      
      /* [2.2] Add to list of condition parameter names. Extend
       * the list length.
       */
      String tmpNames[]= new String[nbrParams+1];
      for(int x= 0; x<nbrParams; x++)
        tmpNames[x]= paramNames[x];         /* copy old values */
      
      tmpNames[nbrParams]= ptf.newString;    /* add new value at end */
      paramNames= tmpNames;                  /* replace the string */
      
      /* [2.3] Need to allocate more space for for EACH condition
       * value list cd[].value[] and copy its contents to the
       * new lists.
       */
      for(int x=0; x < nbrCondLists; x++)
      { /* extend value list of condition x */
        String tmpValues[]= new String[nbrParams+1]; /* make new val list */
        
        for(int i=0; i < nbrParams; i++)
          tmpValues[i]= cd[x].value[i]; /* save old values */
        
        tmpValues[nbrParams]= ""; /* array starts at 0, initially set to "" */
        
        cd[x].value= tmpValues;
        cd[x].name= paramNames;     /* Note: all lists see same list & size */
        cd[x].nParams= nbrParams+1;
      } /* extend value list of condition x */
      
      if(nbrParams==0)
        rmvParamButton.setEnabled(true);/* now something to remove since added one */      
      nbrParams++;                           /* update global counter */
      
      /* [2.4] Update the editor panel to new Parameter values and repaint */
      condPanel.remove(cpef);
      cpef= new CondParamEditForm(this, cd[curCondIdx], null);
      /* Add the rebuilt new current condition */
      condPanel.add(cpef);
      this.pack();
    } /* valid name */
    
    valueChangedFlag= true;          /* set the flag */
  }  /* addNewParameter */   
      
    
  /**
   * removeLocalParameter() - remove parameter locally for all conditions
   */
  public void removeLocalParameter() 
  { /* removeLocalParameter */
      /* [TODO] what are the implications since there is ONE list
       * of parameter names. So deleting a parameter in one condition
       * will make it out of sync with the master list. We could
       * either (1) make it empty for this condition, or (2) ask if
       * we want to remove it for ALL CONDITIONS. Then ask if they
       * REALLY want to do that and then have them spell out EXACTLY
       * "YES" to make sure.
       */
 
    ChoiceList cl= new ChoiceList(this, "Choose a parameter to remove",
                                  paramNames, nbrParams,
                                  "parameter", this);
    String curParamName= "***";
    
    if(cl.removeFlag) 
    { /* try to remove it */
      
      String
        paramValuesTmp[]= new String[nbrParams-1],
        paramNamesTmp[]= new String[nbrParams-1],
        paramNameToRemove= cl.returnItem; /* get parameter name to be removed */
      int
        paramFoundIndex=0,
        tmpIndex=0;
      
      for(int z=0; z< nbrParams; z++) 
      {/* search for parameter name to be removed, build new array of cd w/o param */
        if(paramNames[z].equals(paramNameToRemove))
          paramFoundIndex= z;  /* found local, skip placing it into paramTmp */
        else 
          paramNamesTmp[tmpIndex++]= paramNames[z]; /* save to a tmp */
         
      }/* search for parameter name to be removed, build new array of cd w/o param */
 
      /* [2] If found remove it */
      if(paramFoundIndex != -1) 
      { /* remove the parameter name they specified */
        /* [2.1] Reset certain variables */
        nbrParams--;        /* one less */
        if(nbrParams==0)
          rmvParamButton.setEnabled(false);/* since none left, disable rmv button */
        paramNames= paramNamesTmp;             /* restore new lists w/o the one removed */
        
        valueChangedFlag= true;/* reset the flag */
        
        /* [2.2] Remove cond params & list of conditions */
        condPanel.remove(cpef);
        condPanel.remove(condList);
        cpef.saveData();                     /* save current scrollPane */
        
        /* [2.3] Remove the user deleted parameterName value for each condition &
         * regenerate condData */
        for(int x=0; x < nbrCondLists; x++)
        {/* regenerate condData */
          tmpIndex= 0;
          
          String
            condParamValues[] = new String[nbrParams];
          
          for(int y=0 ; y < nbrParams; y++)
          {
            condParamValues[y]= "";
            
            if(y != paramFoundIndex)
              condParamValues[tmpIndex++]= cd[x].value[y];/* save but skip the deleted one */
          }
          
          cd[x]= new CondData(condListNames[x], maxPossibleSamples,
                              nbrParams, paramNames, condParamValues);
          
        }/* regenerate condData */
        
        cpef= new CondParamEditForm(this, cd[curCondIdx], null);
        
        /* [2.4] Add them back */
        condPanel.add(condList);
        condPanel.add(cpef);
        this.pack();
        
      
      }/* remove the parameter name they specified */
    }/* try to remove it */
  }  /* removeLocalParameter */

    
  /**
   * clearInfo() - clear info for the 3 msg areas in the PopupCondChooser
   */
  void clearInfo()
  { /* clearInfo */
    pccMsgTxtField1.setText(" ");
    pccMsgTxtField2.setText(" ");
    pccMsgTxtField3.setText(" ");
    this.repaint();    
  } /* clearInfo */
  
  
  /**
   * itemStateChanged() - user clicked on condtion in the condition list, 
   * must change CondParamEditForm data, remainder and selected data,
   * save condForm data.
   * @param ie ItemEvent
   * @see addAllSamplesForCond
   * @see CondParamEditForm#saveData
   */
  public void itemStateChanged(ItemEvent ie)
  {/* itemStateChanged */    
    /* [1] Get some data */
    Object obj= ie.getSource();
    java.awt.List itemC= (java.awt.List) obj;
    String choiceStr= itemC.getSelectedItem();
    int selectedIndex= itemC.getSelectedIndex();    
  
    /* [2] Save old samples data from GUI to data structures then put the 
     * new data into GUI.
     */
    addAllSamplesForCond(choiceStr, selectedIndex);    
       
    /* [3] Update the editor panel to new Parameter values and repaint.
     * This will save the old data and then create an instance of the new
     * CondData instance.
     */ 
    condPanel.remove(cpef);  
    
    cpef= new CondParamEditForm(this, cd[selectedIndex], cpef);
    /* Add the rebuilt new current condition */ 
    condPanel.add(cpef);
    this.pack();        
    
    /* [5] Keep track of previous index */
    prevCPEFidx= curCondIdx; 
    curCondIdx= selectedIndex;   
    
    /* [6] Save the new current cond to the state */
    String curConditionname= getCondNameFromIdx(curCondIdx);
    mjaCondition.setCurCondition(curConditionname);
    
  } /* itemStateChanged */
  
  
  /**
   * handleEvent() - process special events
   * @param evt is special events
   */
  public boolean handleEvent(Event evt)
  { /* handleEvent */
    return(super.handleEvent(evt)); /* Let superclass handle other events */
  } /* handleEvent */
  
  
  /**
   * actionPerformed() - Handle Control panel button clicks
   * @param e is ActionEvent
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    
    if(cmd.equals("Add Cond"))
    { /* add new condition and leave it as the selected condition */ 
      addNewCondition();
    }
     
    else if(cmd.equals("Remove Cond"))
    { /* "Remove Cond" button to remove and existing condition */          
      removeConditionLocally();
    }
  
    else if(cmd.equals("List Cond"))
    { /* "List Cond" button to list cur. condition & its parameter(N,V) data */      
      /* [NOTE]: Must save first, gets globle data from mae */
      if(valueChangedFlag)
      { /* ask if save state before list */
        /* [NOTE] Because the the PopupCondChooser state is NEQ MAexplorer,
         * give the user the option to SAVE the state so they are the same
         * before listing it.
         */
        CondDialogBox
          db= new CondDialogBox(this,"You must save any changes you have made to list them.  "+
              "Do you want to Save your edit before listing the MAExplorer conditions?",
                                 "Yes", "No");
        if(db.okFlag) 
          saveAllCondsToMaeState(); /* save data first back into MAExplorer */
      }      
      curCondListName= condListNames[curCondIdx]; 
      mjaCondition.popupListConditionReport(curCondListName);
    }
  
    else if(cmd.equals("List All"))
    { /*  "List All" button to list a summary of all conditions in DB */      
      if(valueChangedFlag)
      { /* ask if save state before list */
        /* [NOTE] Because the the PopupCondChooser state is NEQ MAexplorer,
         * give the user the option to SAVE the state so they are the same
         * before listing it.
         */ 
        CondDialogBox
          db= new CondDialogBox(this,"You must save any changes you have made to list them.  "+
                                "Do you want to Save your edit before listing the MAExplorer conditions?",
                                 "Yes", "No");
        if(db.okFlag) 
          saveAllCondsToMaeState(); /* save data first back into MAExplorer */
      }      
      mjaCondition.popupAllConditionsReport();
    }
  
    else if(cmd.equals("Add Ann"))
    { /* add new annotation parameter to condition */      
      addNewParameter();
    }     
    
    else if(cmd.equals("Remove Ann"))
    { /* remove annotation parameter for condition after asking ARE YOU SURE? */ 
      removeLocalParameter();
    }
    
    else if(cmd.equals("Save"))
    { /* save the condition lists back into the Condition state */ 
       saveAllCondsToMaeState(); /* save data first back into MAExplorer */
    } /* save the condition lists back into the Condition state */
    
    else if(cmd.equals("Cancel"))
    { /* cancel - but ask if save first */
      System.gc();
      if(valueChangedFlag)
      { /* verify that want to save it - else do save if respond Yes */
        CondDialogBox
          db= new CondDialogBox(this,"You must save any changes you have made to list them.  "+
                                     "Are you sure you want to exit without saving?",
                                     "Yes", "No");
        /* If no, then exit without saving */
        if(db.okFlag) 
          saveAllCondsToMaeState(); /* save data first back into MAExplorer */
      }
      close();       
    } /* cancel - but ask if save first */
    
    else if(cmd.equals("Done"))
    { /* done - verify if want to save changes */
 
      /* Check if changed, and if changed, ask if save and then save it */  
      if(valueChangedFlag || pccgSample.valueChangedFlag)
      { /* ask if want to save it */       
        CondDialogBox
          db= new CondDialogBox(this, 
                                "You havn't saved the data, you may save the data before you exit.",
                                "Save & Exit", "Exit");
         /* If no, then exit without saving */
        if(db.okFlag) 
          saveAllCondsToMaeState(); /* save data first back into MAExplorer */
      }/* ask if want to save it */
      
      System.gc();
      close();                        /* always close the window */
    } /* done - verify if want to save changes */
    
  } /* actionPerformed */
  
  
  /**
   * saveCondState() - saves all condition sample and param (N,V) data to MAE via MJAcondition.
   * This replaces the old MAExplorer condition lists with the new ones.
   * @param cdList is list of all conditions
   * @param condLen size of cdList
   * @see MJAcondition#createNewCondList
   * @see MJAcondition#setCondParamNames
   * @see MJAcondition#setCondParamValue
   * @see MJAcondition#setSamplesInCondList
   */
  private boolean saveCondState(CondData cdList[], int nCondLists)
  { /* saveCondState */     
   
    /* [1] remove all conditions first */
    int nbrGlobalCondList= mjaCondition.getNbrCondLists();
    String[] listOfGlobalCond= mjaCondition.getCondListsNames();
    
    if(listOfGlobalCond!=null && nbrGlobalCondList>0) 
    {
      for(int i= 0; i< nbrGlobalCondList; i++) 
      {
         boolean removedWorkedFlag= mjaCondition.removeCondList(listOfGlobalCond[i]);
        
       if(DBUG_FLAG)
         if(removedWorkedFlag)
           System.out.println("PCC-SCS1.1.1: removed: "+listOfGlobalCond[i]);
         else
           System.out.println("PCC-SCS1.1.1: problem removing: "+listOfGlobalCond[i]);
      }
    }
    
    /* [2] Add back local conditions */
    for(int x=0; x<nCondLists; x++) 
    { /* process each cond list */
      CondData cdX= cdList[x];
      String
        condNameX= cdX.condName,
        selSamples[]= cdX.selSamples;
      
      /* [2.1] Check to see if condListName is present */
      if(! mjaCondition.isCondList(condNameX))
      { /* condition name is not in the MAE database, so make first then save it */
        if(! mjaCondition.createNewCondList(condNameX))
        { /* trouble - possible DRYROT error */
          if(DBUG_FLAG)
           System.out.println("PCC-SCS1.1.1: DRYROT - problem creating cond list");
          return(false);
        }
      } /* condition name is not in the MAE database, so make first then save it */
      
      /* [3] Save samples to CondList*/
      mjaCondition.setSamplesInCondList(condNameX,             /* cond name*/
                                        cdX.selSamples,   /* list of samples*/
                                        cdX.nSelSamples); /* size of list of samples*/   
      
      /* [4] Save cond parameter values- the size of the arrays are the # params */
      mjaCondition.setCondParamNames(cdX.name); /* save all parameter names */
      boolean saveFlag= mjaCondition.setCondParamValue(condNameX, cdX.value);   
      if(!saveFlag)
      { /* bad data */
        if(cdX.name.length!=nbrParams || cdX.value.length!=nbrParams)
        {
          if(DBUG_FLAG)
            System.out.println("PCC-SCS dryrot cdX.name.length="+cdX.name.length+
                               " cdX.value.length="+cdX.value.length+
                               " NEQ nbrParams="+nbrParams);
        }      
        return(false);
      }/* bad data */
    } /* process each cond list */
   
    valueChangedFlag= false;
    return(true);
  } /* saveCondState */
  
  
  /**
   * close() - close this popup 
   */
  public void close()
  { /* close */
    oldPCC= null;        /* clear this instance */
      
    /* Need to remove all large data structures to let G.C. to do its thing */
    paramNames= null;
    remTitle= null;
    selTitle= null;
   
    cd= null;
    cpef= null;
    
    condListNames= null;
    allSampleNames= null;
    condList= null;
    pccgSample= null;
    
    chooserPanel= null;
    
    pccMsgTxtField1= null;
    pccMsgTxtField2= null;
    pccMsgTxtField3= null;    
       
    titleFont= null;
    msgFont= null;    
    buttonFont= null;
    labelFont= null;
    condFont= null;
    paramFont= null;
    
    /* Now should be able to Garbage Collect most of this class. */
    System.gc();       
    
    this.dispose();
  } /* close */
  
  
  /**
   * windowClosing() - close down the window.
   * @param e Window Event
   */
  public void windowClosing(WindowEvent e)
  { /* close */
    /* Check if changed, and if changed, ask if save and then save it */
    if(valueChangedFlag)
    { /* ask if want to save it */
      CondDialogBox db= new CondDialogBox(this,
           "You havn't saved the data, you may save the data before you exit.",
           "Save & exit", "Exit");
      if(db.okFlag)
        saveFlag= saveCondState(cd, nbrCondLists);
    }
    close();                        /* always close the window */
  } /* close */  
  
  
  /* Others not used at this time */
  public void windowOpened(WindowEvent e)  { }
  public void windowActivated(WindowEvent e)  { }
  public void windowClosed(WindowEvent e)  {}
  public void windowDeactivated(WindowEvent e)  { }
  public void windowDeiconified(WindowEvent e)  { }
  public void windowIconified(WindowEvent e)  { }
  
  
  /**
   * updateCurGene() - update any data since current gene has changed.
   * This is invoked from the PopupRegistry
   * @param mid is the MID (Master Gene ID) that is the new current gene.
   */
  public void updateCurGene(int mid) { }
  
  
  /**
   * updateFilter() - update any dependent data since Filter has changed.
   * This is invoked from the PopupRegistry
   */
  public void updateFilter() { }
  
  
  /**
   * updateSlider() - update any dependent data sincea threshold slider has changed.
   * This is invoked from the PopupRegistry.
   */
  public void updateSlider() { }
  
  
  /**
   * updateLabels() - update any dependent data since global labels have changed.
   * This is invoked from the PopupRegistry.
   */
  public void updateLabels() { }
  
  
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /*              1.       CLASS CondData                               */
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /**
   * Class CondData contains the active remainder and selected samples data
   * for a condition instance.
   */
  class CondData
  {/* CondData */
    /** Condition name */
    String 
      condName;
    /** list of remainder samples */
    String
      remSamples[];
    /** list of selected samples for current condition being edited */
    String
      selSamples[];
    /** number of selected samples */
    int
      nSelSamples;
    /** number of remainder samples */
    int
      nRemSamples;
    /** A value has changed, data will need to be saved */
    boolean
      valueChangedFlag;
    /** list of condition values */
    String
      value[];
    /** list of condition names */
    String
      name[];
    /** number of rows of (name,value) parameter pairs */
    int
      nParams;
    
    
    /**
     * CondData() - Constructor     
     * @param condName - name of the condition 
     * @param maxSamples - size of selected and remainder arrays
     * @param nParams number of parameters
     * @param paramNames is array [0:nParams-1] of parameter names
     * @param paramValues is array [0:nParams-1] of parameter values
     * @param 
     */
    public CondData(String condName, int maxSamples, int nParams, 
                    String paramNames[], String paramValues[])
    {
      this.condName= condName;
      this.nParams= nParams;
      this.name= paramNames;
      this.value= paramValues;
      
      remSamples= new String[maxSamples];
      selSamples= new String[maxSamples];
      
      nSelSamples= 0;
      nRemSamples= 0;
      valueChangedFlag= false;     
    }/* Constructor */
    
  }/* end class CondData */
  
   
  
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /*                2.       CLASS CondParamEditForm                    */
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /**
   * Class CondParamEditForm creates a ScrollPane of the (name,value)
   * parameters for the current condition instance.
   */
  
  class CondParamEditForm extends ScrollPane implements TextListener
  {
    /** default preferred size used prior to mainPanel being constructed */
    final private static  int
      DEF_SP_WIDTH= 300;
    /** default preferred size used prior to mainPanel being constructed */
    final private static int
      DEF_SP_HEIGHT= 100;
    
    /** parent class */
    PopupCondChooser
      pcc;        
    /** instance of condData */
    CondData
      cd;    
    /** max width of textfields */
    final public static int
      MAX_POSSIBLE_PARAMS= 100;
    
    /** main layout */
    BorderLayout
      bl;
    /** labels GridLayout to create condition form */
    GridLayout
      labelGL;
    /** TextFields GridLayout to create condition form */
    GridLayout
      tfGL;    
    /** labels go here */
    Panel
      labelPanel;
    /** TextFields go here */
    Panel
      tfPanel;
    /** place both labelPanel & tfPanel to create form */
    Panel
      mainPanel;
    /** parameter names fields */
    TextField
      nameTF[];
    /** a value was changed for THIS condition in one or more parametes, so
     * data will need to be saved.
     */
    boolean
      valueChangedFlag;
    int
      nParams=0;
    
    
   /**
    * CondParamEditForm() - Constructor for parameter (name[],value[]) data. Save data
    * if not null otherwise it will be lost.
    * @param pcc instance of PopupCondChooser
    * @param cd is instance of CondData to use in NEW instance
    * @param oldCPEF is instance of OLD cpef to check if need to save its data.
    */
    CondParamEditForm(PopupCondChooser pcc, CondData cd, CondParamEditForm oldCPEF)
    { /* CondParamEditForm */  
      super(ScrollPane.SCROLLBARS_AS_NEEDED);      
       
      this.pcc= pcc;        /* new instance */
      
      /* Save old CondData instance in case need to save */
      if(oldCPEF!=null && oldCPEF.valueChangedFlag)
        oldCPEF.saveData(); 
      
      valueChangedFlag= false;         
      this.cd= cd;  
      
      /* Create GUI for the condition form */
      buildParamEditGUI();
    } /* CondParamEditForm */
    
  
  /**
   * getPreferredSize() - get the preferred size
   * @return window size
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    Dimension d= new Dimension(DEF_SP_WIDTH, DEF_SP_HEIGHT);
    
    //if(mainPanel!=null)
    //  d= mainPanel.getPreferredSize();
    
    return(d);
  } /* getPreferredSize */
  
    
   /**
    * buildParamEditGUI() - create GUI for the condition form. 
    * Update the parameter data structures and rpaint it   
    * This updates the editor panel to new Parameter values and repaint.
    * @param instance of CondData
    */
    private void buildParamEditGUI()
    { /* buildParamEditGUI */ 
      /* [1] Create main panel */            
      mainPanel= new Panel();
      mainPanel.setLayout(new FlowLayout());  
      this.add(mainPanel);                    /* add to scroller */  
     
      /* [2] Setup the new cd */
      this.cd= cd; 
      nParams= cd.nParams; 
      nameTF= new TextField[nParams];      
      
      /* [3] Now create the new GUI elements for the mainPanel */
      labelGL= new GridLayout();  /* NEW instances!!! */
      tfGL= new GridLayout();
      
      labelGL.setColumns(1);
      labelGL.setRows(nParams);
      
      tfGL.setColumns(1);
      tfGL.setRows(nParams);
      
      labelPanel= new Panel();    /* NEW instances!!! */
      tfPanel= new Panel();
      
      labelPanel.setLayout(labelGL);
      tfPanel.setLayout(tfGL);
      
      int 
        tfSize,
        maxTFSize= 60;            /* was 15 */
      
      /* [4] Find max sizes of param value textfields */
      for(int i=0; i<nParams; i++)
      { /* find max sizes */        
        /* [4.1] Check if value[i] is null before checking length,
         * prevents null error bug.
         */
        if(cd.value[i] != null)
          tfSize= 20 + cd.value[i].length(); 
        else
          tfSize= 10;
        
        /* [4.2] Check if name[i] is null before checking length */       
        if(tfSize > maxTFSize)
          maxTFSize= tfSize;                   
      } /* find max sizes */
      
      /* [5] Create CondParamEditForm grid */
      for(int i=0; i<nParams; i++)
      { /* create CondParamEditForm grid */
        /* [5.1] Keep track of largest size so we can use it to
         * set the GUI size to save space.
         */
       
        /* [5.2] Add label and textfield to their panels */
        Label label= new Label(cd.name[i]);
        label.setFont(pcc.paramFont);
        labelPanel.add(label);
        nameTF[i]= new TextField(cd.value[i], maxTFSize);
        nameTF[i].setFont(pcc.paramFont);
        nameTF[i].addTextListener(this);
        tfPanel.add(nameTF[i]);
      } /* create CondParamEditForm grid */
      
      /* [6] add to GUI */
      mainPanel.add(labelPanel);
      mainPanel.add(tfPanel); 
      
      mainPanel.repaint();
      pcc.repaint();
    } /* buildParamEditGUI */
     
    
    /**
     * saveData() - Save value data from textFields to cd.value[] if changed.
     */
    public boolean saveData()
    { /* saveData */
      if(this.valueChangedFlag)
      {
        for(int i=0; i<nParams; i++)
        {
          cd.value[i]= nameTF[i].getText();
        }
        this.valueChangedFlag= false;      /* since saved to cd.value */
      }
      
      return(true); /* success */
    } /* saveData */
            
       
    /**
     * textValueChanged() - service text changed by setting the value changed flag.
     * @param textEvent if user typed in the text field
     */
    public void textValueChanged(java.awt.event.TextEvent textEvent)
    { /* textValueChanged */
      this.valueChangedFlag= true;
      pcc.valueChangedFlag= true; /* set parent for ANY cond which changes */
    }/* textValueChanged */
      
    
  }/* End Class CondParamEditForm */
   
  
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /*               3.          CLASS CondDialogBox                      */
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /**
   * Class CondDialogBox is a dialog box with with two buttons yesMsg and
   * noMsg that will return true or false when pressed.
   */
  
  class CondDialogBox extends Dialog 
                             implements ActionListener, WindowListener
  {    
    /** Frame where dialog box is added */
    private Frame
      f;/** Message for dialog box */
    private String
      msg;
    /** Yes label for dialog box */
     private String
      yesMsg;
    /** No label for dialog box */
     private String
      noMsg;
    
    /** flag from OK button */
    boolean
      okFlag;
    
    /**
     * CondDialogBox() - Constructor
     * @param f is frame of dialog box
     * @param msg for dialog box
     * @param yesMsg for dialog box
     * @param noMsg for dialog box
     */
    CondDialogBox(Frame f, String msg, String yesMsg, String noMsg)
    { /* CondDialogBox */
      super(f, "Message", true);
      
      okFlag= true;
      this.msg= msg;
      this.f= f;
      this.yesMsg= yesMsg;
      this.noMsg= noMsg;
      
      buildGUI();
    } /* CondDialogBox */
    
    
    /**
     * buidlGUI() - Build GUI
     */
    void buildGUI()
    { /* buildGUI */
      String displayMsg;
      Button
        yesButton= new Button(yesMsg),
        noButton= new Button(noMsg);
      Label label;
     
      yesButton.addActionListener(this);
      noButton.addActionListener(this);
      
      displayMsg= (msg!=null) ? msg : "Choose "+yesMsg+" or "+noMsg;
      label= new Label(displayMsg);
      
      Panel
        buttonPanel= new Panel(),
        mainPanel= new Panel(new BorderLayout());
      
      buttonPanel.add(yesButton);
      buttonPanel.add(noButton);
      mainPanel.add(label,BorderLayout.NORTH);
      mainPanel.add(buttonPanel,BorderLayout.SOUTH);
      
      this.add(mainPanel);
      this.setSize(250,300);
      this.setTitle(displayMsg);
      this.pack();
      
      /* put the Dialog box in the middle of the frame */
      Dimension
        myDim= getSize(),
        frameDim= f.getSize(),
        screenSize= getToolkit().getScreenSize();
      Point loc= f.getLocation();
      
      loc.translate((frameDim.width - myDim.width)/2,
                    (frameDim.height - myDim.height)/2);
      loc.x= Math.max(0,Math.min(loc.x,screenSize.width - getSize().width));
      loc.y= Math.max(0,Math.min(loc.y, screenSize.height - getSize().height));
      
      setLocation(loc.x,loc.y);
      this.setVisible(true);
    } /* buildGUI */
    
    
    /**
     * actionPerformed() - handle action events
     * @param ae is the ActionEvent 
     */
    public void actionPerformed(java.awt.event.ActionEvent ae)
    {  /* actionPerformed */
      String cmd= ae.getActionCommand();
      Button item= (Button) ae.getSource();
      
      if(cmd.equals(yesMsg))
      {
        okFlag= true;
        close();
      }
      
      else if(cmd.equals(noMsg))
      {
        okFlag= false;
        close();
      }
    }  /* actionPerformed */
    
    
    /**
     * close() - close this popup
     */
    private void close()
    { /* close */
      this.dispose();
    } /* close */
    
    
    /**
     * windowClosing() - close down the window - assume false.
     */
    public void windowClosing(WindowEvent e)
    { /* close */
      okFlag= false;
      close();
    } /* close */
    
    
    /* Others not used at this time */
    public void windowOpened(WindowEvent e)  { }
    public void windowActivated(WindowEvent e)  { }
    public void windowClosed(WindowEvent e)  {}
    public void windowDeactivated(WindowEvent e)  { }
    public void windowDeiconified(WindowEvent e)  { }
    public void windowIconified(WindowEvent e)  { }
    
  }/* End Class CondDialogBox */
   
  
  
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /*               4.          CLASS  PopupTextField                    */
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /**
   * Class PopupTextField is a simple "editor" for entering a string value.  
   */  
class PopupTextField extends Dialog implements ActionListener, WindowListener
  { /* PopupTextField */
   
    /** instance of PopupCondChooser */
    private PopupCondChooser
      pcc;
    /** textfield for typing in new name */    
    private TextField
      newNameTF;
    /** cancel button */    
    private Button
      cancelButton;
    /** done button */     
    private Button
      doneButton;
    
    /** This is set if the name the user types is valid */
    boolean 
      stringValidFlag= false; 
    /** used to return the string the user types in */
    String
      newString= "";
    
     
    /**
     * PopupTextField() - Constructor
     * @param f is Frame to place this in
     * @param title of the frame
     * @param pcc is the PopupCondChooser instance
     * @param labelName is the name label
     */
    PopupTextField(Frame f, String title, PopupCondChooser pcc, String labelName)
    {
      super(f, " ", true);
      this.pcc= pcc;
      
      Label newNameLabel= new Label(labelName);
      Panel
        buttonPanel= new Panel(),
        newNamePanel= new Panel();
    
      /* popup window to input data */      
      cancelButton= new Button("Cancel");
      doneButton= new Button("Done");
       
      newNameTF= new TextField();
      this.setLayout(new BorderLayout());
      newNamePanel.setLayout(new BorderLayout());
      buttonPanel.setLayout(new FlowLayout());
      
      buttonPanel.add(doneButton);
      buttonPanel.add(cancelButton);
      
      newNamePanel.add(newNameLabel, BorderLayout.NORTH);
      newNamePanel.add(newNameTF, BorderLayout.SOUTH);
      
      doneButton.addActionListener(this); /* Buttons */
      cancelButton.addActionListener(this); 
      
      this.addWindowListener(this);    
      this.add(newNamePanel, BorderLayout.NORTH);
      this.add(buttonPanel, BorderLayout.SOUTH);
      Dimension d= getPreferredSize();
      this.setSize(d);
      this.setTitle(title);
       
      /* Center frame on the screen, PC only */
      Dimension pccSize= pcc.getSize();
      int
        xPos= pccSize.width - (this.getSize().width)-50,
        yPos= (pccSize.height - this.getSize().height)/2;
      Point pos= new Point(xPos, yPos);
      this.setLocation(pos);
    
      this.pack();
      this.setVisible(true); 
    } /* PopupTextField */
  
    
    /**
     * actionPerformed() - handle action events
     * @param ae is the ActionEvent 
     */
    public void actionPerformed(java.awt.event.ActionEvent ae)
    { /* actionPerformed */
      String cmd= ae.getActionCommand();
      Button item= (Button) ae.getSource();
      
      if(cmd.equals("Done"))  
      {
        newString= newNameTF.getText();/* save users typed in text */
        if(newString!=null && newString.length() >= 1)
          stringValidFlag= true;
        close();
      }
      else if(cmd.equals("Cancel"))
      {
        stringValidFlag= false;
        newString= null;
        close(); /* stop without saving */
      }
    } /* actionPerformed */
    
   
    /**
     * close() - close this popup
     */
    private void close()
    { /* close */
      this.dispose();
    } /* close */
    
    
    /**
     * windowClosing() - close down the window - assume false.
     */
    public void windowClosing(WindowEvent e)
    { /* close */ 
      close();
    } /* close */
    
    /* Others not used at this time */
    public void windowOpened(WindowEvent e)  { }
    public void windowActivated(WindowEvent e)  { }
    public void windowClosed(WindowEvent e)  {}
    public void windowDeactivated(WindowEvent e)  { }
    public void windowDeiconified(WindowEvent e)  { }
    public void windowIconified(WindowEvent e)  { }
    
  }/* end of Class PopupTextField */

  
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /*               5.          CLASS  ChoiceList                        */
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /**
   * Class ChoiceList Allows user to pick an item in a list of items
   */  
class ChoiceList extends Dialog implements ItemListener, ActionListener,
                                           WindowListener
  { /* ChoiceList */   
    /** instance of PopupCondChooser */
    private PopupCondChooser
      pcc;  
    /** pass down frame for dialog */
    Frame
      f;
    /** index of which item is selected. Note: the first [0] item is used for initalMsg on selection */
    int
      selectedIndex= 0;
    /** return selected item when done button is pressed after remove button is pressed */
    String
      returnItem= "";
    /** specific msg to be displayed about what is being removed interface parameter or condition */
    String
      removeMsg;
    /** in the list at position 0, first thing the user will see */
    String
      initialMsg;
    /** item in list that was clicked on by user */
    String
      choiceStr;
    /** list of items to select from*/
    Choice
      choiceList;
    /** if selection went ok */
    boolean
      removeFlag= false;
    /** cancel w/o removing */
    Button
     cancelButton;
    /** remove */
    Button
     removeButton;
    /** Done button */
    Button
     doneButton;

    
    /**
     * ChoiceList() - Constructor
     * @param f is Frame to put this in
     * @param title of the frame
     * @param itemList is array of items to use in the Choice 
     * @param listSize is the # of items use use in itemList[]
     */
    ChoiceList(Frame f, String title, String itemList[], int listSize, 
               String buttonMsg, PopupCondChooser pcc)
    { /* ChoiceList */
      super(f, " ", true);
    
      this.f= f;
      this.pcc= pcc;
      removeMsg= "Remove" + buttonMsg;
      choiceList= new Choice();     
      Panel
        mainPanel= new Panel(),
        buttonPanel= new Panel();  
     
      cancelButton= new Button("Cancel");
      removeButton= new Button(removeMsg);
      doneButton= new Button("Done");
      
      returnItem= "";
      initialMsg= "Choose Item from list";
      
      BorderLayout bl= new BorderLayout();
      this.setLayout(bl);      
      
      choiceList.addItemListener(this);
      doneButton.addActionListener(this); 
      cancelButton.addActionListener(this); 
      removeButton.addActionListener(this); 
      removeButton.setEnabled(false); /* disable until item is choosen */
      
      buttonPanel.add(removeButton);
      buttonPanel.add(doneButton);
      buttonPanel.add(cancelButton);
       
      mainPanel.add(choiceList);
      
      this.addWindowListener(this);    
      
      choiceList.addItem(initialMsg);
      
      for(int x= 0;x<listSize ; x++)
        choiceList.addItem(itemList[x]);
               
      this.add(mainPanel, BorderLayout.NORTH);
      this.add(buttonPanel, BorderLayout.SOUTH);
      Dimension d= getPreferredSize();
      this.setSize(d);
      this.setTitle(title);
      this.pack();
      this.setVisible(true);  
    } /* ChoiceList */
    
   
    /**
     * close() - close this popup
     */
    private void close()
    { /* close */
      this.dispose();
    } /* close */
    
    
    /**
     * windowClosing() - close down the window - assume false.
     */
    public void windowClosing(WindowEvent e)
    { /* close */ 
      close();
    } /* close */
    
    
    /**
     * actionPerformed() - button handler 
     */
    public void actionPerformed(ActionEvent ae) 
    { /* actionPerformed */    
     String cmd= ae.getActionCommand();
      
      if(cmd.equals("Done"))  
      {        
        /* close window and go do the actual remove, global variables */
        close();  
      }
      else if(cmd.equals("Cancel"))
      {     
        returnItem= "";
        removeFlag= false;         
        close(); /* stop without saving */           
      }
      else if(cmd.equals(removeMsg))
      { /* remove */
        if(!returnItem.equals(initialMsg))
        {
          PopupTextField ptf= new PopupTextField(f,
                              "Are you sure you want to remove ["+returnItem+"]?",
                              pcc,
                              "Enter 'YES' if you want to remove it.");
          
          if(ptf.newString.equalsIgnoreCase("yes"))
          { /* remove from GUI, local */
                       
            if(selectedIndex!=0)
            {
              choiceList.remove(selectedIndex);
              removeFlag= true;
              removeButton.setEnabled(false);
             
              this.pack();/* redraw */
              this.setVisible(true);
            }
          } /* remove from GUI, local */
        }
      } /* remove */  
     
    } /* actionPerformed */
    
    
    public void itemStateChanged(java.awt.event.ItemEvent ie) 
    {
      Object obj= ie.getSource();
      Choice itemC= (Choice) obj;  
      choiceStr= itemC.getSelectedItem();
      
      returnItem= choiceStr;
      selectedIndex= itemC.getSelectedIndex();  
      removeButton.setEnabled(true); 
    }
    public void windowActivated(java.awt.event.WindowEvent windowEvent) {}
    public void windowClosed(java.awt.event.WindowEvent windowEvent) {}
    public void windowDeactivated(java.awt.event.WindowEvent windowEvent) {}
    public void windowDeiconified(java.awt.event.WindowEvent windowEvent) {}
    public void windowIconified(java.awt.event.WindowEvent windowEvent) {}
    public void windowOpened(java.awt.event.WindowEvent windowEvent) {}
    
}/* end of class ChoiceList */


} /* end of class PopupCondChooser */


