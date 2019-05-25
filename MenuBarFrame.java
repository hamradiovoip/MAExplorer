/** File: MenuBarFrame.java */

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

/** 
 * The class builds the MAExplorer GUI including a menu bar in a new Frame. 
 * We have to use a new Frame so we can get a MenuBar for the
 * standalone GUI. It also has the Action (MenuItem) and Item (CheckBoxMenuItem)
 * event listeners for the menu and passes the event commands
 * back to the @see EventMenu classe to handle.
 *<P>
 * The builtin checkbox menu items are given (currently) individual variables.
 * We track all all menu entries in order to be able to invoke commands from
 * the program (in addition to user interaction with the GUI):
 * 1. Menu Items are tracked in
 *     (menuActionLabelList[0:nCmds-1], and menuActionCmdList[0:nCmds-1]). 
 * 2.Checkbox menu items are tracked in
 *     (chkBoxMenuLabelList[0:nCBcmds-1] and chkBoxMenuItemList[0:nCBcmds-1])
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), J. Evans(CIT), G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.48 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see EventMenu
*/
  
class MenuBarFrame extends Frame implements ActionListener, ItemListener, 
	                                    WindowListener, KeyListener
{
  /* 
   * [NOTE] This combines the old BuildGUI and mnuBarFrame of Version 0.91.01 
   * where the previous version where they were separate classes was 0.90.08.
   * It was restructured at version 0.90.06 by Jai Evans and merged into
   * 0.90.08 by Pete Lemkin.
   */
    
  /** link to global instance */ 
  private MAExplorer
    mae;                       
  /** link to global Config instance */   
  private Config
    cfg;                            
  
  /** title for frame */
  String
    title; 
  /**  menu bar  */
  static MenuBar
    mbar;			
     
  /** max number of menu items allowed */
  static final int
    MAX_CMDS= 3001;
  /** max number of menu checkbox items allowed */
  static final int
    MAX_CHKBOX_CMDS= 1003;  
               
  /** # of menu item commands [0:nCmds-1] */
  static int        
    nCmds= 0;	  
  /** [MAX_CMDS] list of menuItem commands labels */
  static String
    menuActionLabelList[];  	        
  /** [MAX_CMDS] list of menuItem (short-form) commands for event handler */
  static String
    menuActionCmdList[]; 
  
  /** # of menu CheckBoxMenuItem item cmds [0:nCBcmds-1] */
  static int
    nCBcmds= 0;
  /** [MAX_CHKBOX_CMDS] list of checkboxItem cmds menu labels */
  static String
    chkBoxMenuLabelList[];              
  /** [MAX_CHKBOX_CMDS] list of checkboxItem cmds menu items corresponding to names */ 
  static CheckboxMenuItem  
    chkBoxMenuItemList[];    
  
  /* --- data stucture to point to end-nodes of menu subtrees for adding
   * possible MAEPlugins at a later time.
   */
  static final int
    MAX_MENU_STUBS= 100;
  /** # of menu stubs in the list */
  int
    nMenuStubs= 0;   
  /** list of Names of Stubs in Menu tree */
  String
    stubNames[];
  /** list of Menu Stubs in Menu tree */
  Menu
    stubMenus[];
  
  /* --- data stuctures to support RLO R script menus in the (Plugin | RLO methods)
   * menu tree.
   */               
  /** Menu stub to add/modify R(Plugin | RLO methods (submenu)) menu items */
  Menu     
    rloSmnu;
  /** # of RLO entries in the menu */
  int
    nRLOs= 0; 
  /** RLO menu names [0:nRLOs-1] in Plugin | RLO methods) menu tree */ 
  String
    rloMenuNames[]; 
  /** RLO menu names [0:nRLOs-1] in Plugin | RLO methods) menu tree */ 
  String
    rloRscriptNames[];  

  /* --- Variables in building the top level frame --- */    
  /** i.e. mae.browserTitle */
  String
    aTitle;                       
  
  /** ULHC X of MenuBarFrame window */
  int
    x0= 20;
  /** ULHC X of MenuBarFrame window */
  int                     
    y0= 20; 
	   
  /** report label must be global variable*/
  Label
    reportLabel;		
  
  /** current global small font */
  Font 
    smallFontLbl;               
  /** current global medium font */
  Font 
    mediumFontLbl;              
  /** current global font to use font */
  Font 
    useFontLbl;                       
  /** command pops it up */
  ExprProfilePopup
    exprProfilePopup;           
                      
  /** dialog frame created but not displayed until needed */
  Frame
    dialogFrame;               
     
  /** holds popup dialog query */ 
  PopupDialogQuery
    pdq;                        
  /** holds popup BinOpr dialog query */ 
  PopupBinOprDialogQuery
    pBOdq;                      
  /** holds HP-X & -Y labels, STOP button*/ 
  Panel 
    HPlabelPanel;		                     
   /** holds above chkboxes & scrollbar panels */
   Panel 
    ps;                              
  /** If hasStopButtonFlag is set (i.e.  button exists, then set if want to abort
   * a computation. The event handler sets the mae.abortFlag 
   */
  Button
    stopButton;                 
  /** Show Mouse-over info */
  Checkbox
    mouseOverCheckBox;           
  /** enter cur. gene name in popup guesser */
  Button
    curGeneTextButton;          
  /** if guesser was popped up*/ 
  boolean
    masterIDguesserIsPoppedUp;  
  /** create when needed */ 
  PopupGeneGuesser 
    gnMidGuesser;		  
  
  /** set if stopButton has "STOP!" label*/
  boolean
    hasStopButtonFlag;          
  /** if chooser was popped up*/
  boolean
    hpMenuChooserIsPoppedUp;     
  /** if guesser was popped up*/ 
  boolean
    hpMenuGuesserIsPoppedUp;    
  
  /** either "STOP!" or "     " */
  String
    stopLblName;  
                  
  /** Set in event handler to one of: ":HP" ":HP_X" ":HP_Y" ":HP_X+" ":HP_X-"
    * ":HP_Y+" ":HP_Y-" ":HP_E+" ":HP_E-"
    */ 
  String
    hpMenuToUpdateFromGuesser;  
  
  /** Set in event handler to one of: ":HP" ":HP_X" ":HP_Y" ":HP_X+" ":HP_X-"
   * ":HP_Y+" ":HP_Y-" ":HP_E+" ":HP_E-"
   */ 
  String
    hpMenuToUpdateFromChooser;  
  
  /** HP menu guesser: create when needed */ 
  PopupHPmenuGuesser 
    hpMenuGuesser;	         
  
  
  /** Project Guesser: create when needed */ 
  PopupProjDirGuesser
    projDirGuesser;               
  /** if guesser was popped up*/ 
  boolean
    projDirGuesserIsPoppedUp;     
  /** prj dir result set in event handler */ 
  String
    projDirToUpdateFromGuesser;       
      
  /** "Analysis" menu */
  Menu 
    analysisMenu;     
  /** "File"  menu */ 
  Menu                 
    fileMenu;                       
  /** "Sample" command  menu */
  Menu 
    sampleMenu;                 
  /** "GeneClass"  menu */
  Menu 
    geneClassMenu;    
  /** "Normalization"  menu */ 
  Menu 
    normMenu;   
  /** "Edit"  menu */
  Menu 
    editMenu;    
  /** "Filter"  menu */
  Menu 
    filterMenu;                 
  /** "View"  menu */    
  Menu 
    viewMenu;                   
  /** "Plot"  menu */    
  Menu 
    plotMenu;                    
  /** "Cluster"  menu */    
  Menu 
    clusterMenu;   
  /** "Report" menu */  
  Menu 
    reportMenu;                    
  /** "Plugins" menu */   
  Menu 
    pluginsMenu;                   
  /** "Help"  menu */
  Menu 
    helpMenu;                    
    
  /* -- The following are Checkbox menu items used in various menus. -- */  
  
  /** Menu: (Edit | Preferences) access data from Web database */
  CheckboxMenuItem
    miFEMuseWebDB;
  /** Menu: (Edit | Preferences) enable using cache for Web database */
  CheckboxMenuItem
    miFEMenableFIOcache;    
  
  /** Menu: (Samples) use HP-X and HP-Y 'sets' of  multiple samples 
   * else use individual HP-X and HP-Y samples. */
  CheckboxMenuItem
    miHPMuseHPxySets;
  
  /** (Analysis | Normalization | Housekeeping genes) */
  CheckboxMenuItem
    miNRMhousekeepNorm;
  /** (Analysis | Normalization | User defined gene set genes) */
  CheckboxMenuItem
    miNRMgeneSetNorm;
  /** (Analysis | Normalization | Calibration DNA genes) */
  CheckboxMenuItem
    miNRMCalDNAnorm;
  /** (Analysis | Normalization | Zscore )*/
  CheckboxMenuItem
    miNRMZscoreNorm;
  /** (Analysis | Normalization | Median) */
  CheckboxMenuItem
    miNRMmedianNorm;
  /** (Analysis | Normalization | Normalize by logMedian )*/
  CheckboxMenuItem
    miNRMlogMedianNorm;
  /** (Analysis | Normalization | Normalize by Zscore of log (StdDev) ) */
  CheckboxMenuItem
    miNRMZscoreMeanStdDevLogNorm;
  /** (Analysis | Normalization | Normalize by Zscore of log (AbsDev) ) */
  CheckboxMenuItem
    miNRMZscoreMeanAbsDevLogNorm;
  /** (Analysis | Normalization | Normalize to maximum intensity (65K) */
  CheckboxMenuItem
    miNRMscaleToMaxIntens;
  /** (Analysis | Normalization } No normalization)*/
  CheckboxMenuItem
    miNRMnoNorm;
  
  /** only if mae.DBUG_GENERIC_NORM_PLUGIN  is set, then
   * (Analysis | Normalization | Test Generic Norm Plugin) 
   */
  CheckboxMenuItem
    miNRMtestGenericNormPlugin;
  
  /** (Analysis | Normalization) use per-sample Good Spots data for 
   * global array statistics */
  CheckboxMenuItem
    miNRMgoodSpotsStats;
  /** (Analysis | Normalization) use background correction */
  CheckboxMenuItem
    miNRMbkgrdCorr;
  /** (Analysis | Normalization) use ratio median correction 
   * for the Cy3 and Cy5 channels */
  CheckboxMenuItem
    miNRMratioMedianCorrection;
    
  /** Menu: (Edit | User Edited Gene List') Show 'Edited Gene List' */
  CheckboxMenuItem
    miEMshowEditedGenes;
  /** Menu: (Edit | User Edited Gene List') don't edit 'Edited Gene List' */
  CheckboxMenuItem
    miEMeditNop;
  /** Menu: (Edit | User Edited Gene List') add gene to 'Edited Gene List' */
  CheckboxMenuItem
    miEMeditAdd;
  /** Menu: (Edit | User Edited Gene List') delete gene to 'Edited Gene List' */
  CheckboxMenuItem
    miEMeditRmv;
  /** Menu: (Edit | User Edited Gene List') [DEPRICATED] */
  CheckboxMenuItem
    miEMautoScrollerPopup;
  /** Menu: (Edit | Preferences) Cluster Filtered Genes, else all genes */
  CheckboxMenuItem
    miEMclusterWorkingCL;

  /** Menu:(View) show EGL genes */
  CheckboxMenuItem
    miVMshowEditedGenes;
  /** Menu:(View) show Filtered genes and spots */
  CheckboxMenuItem
    miVMshowFilteredSpots;
  /** Menu:(View) enable Presentation view that increases font sizes */
  CheckboxMenuItem
    miVMPresentView;
  /** Menu:(View) show gang scrolling for duplicated grids of spots */
  CheckboxMenuItem
    miVMGang;
  /** Menu:(View) enable mouse-over information for mouse over selected genes */
  CheckboxMenuItem
    miVMuseMouseOver;
  /** Menu:(View) enable popup GenBank browser for new current gene */
  CheckboxMenuItem
    miVMgenBankViewer;
  /** Menu:(View) enable popup GenBank browser for new current gene */
  CheckboxMenuItem
    miVMdbESTviewer;
  /** Menu:(View) enable popup UniGene browser for new current gene */
  CheckboxMenuItem
    miVMuniGeneViewer;
  /** Menu:(View) enable popup OMIM browser for new current gene */
  CheckboxMenuItem
    miVMomimViewer;
  /** Menu:(View) enable popup mAdb gene browser for new current gene */
  CheckboxMenuItem
    miVMmAdbViewer;
  /** Menu:(View) enable popup LocusLink gene browser for new current gene */
  CheckboxMenuItem
    miVMlocusLinkViewer;
  /** Menu:(View) enable popup MedMiner browser for new current gene */
  CheckboxMenuItem
    miVMmedMinerViewer;
  /** Menu:(View) enable popup SwissProt browser for new current gene */
  CheckboxMenuItem
    miVMswissProtViewer;
  /** Menu:(View) enable popup PIRs browser for new current gene */
  CheckboxMenuItem
    miVMpirViewer;
  /** Menu:(View) enable Dichromasy colors that may be useful for some
   * colorblind researchers instead of default color schemes. */
  CheckboxMenuItem
    miVMuseDichromasy;
  /** Menu:(View) enable popup message history log */
  CheckboxMenuItem
    miVMshowMsgLog;
  /** Menu:(View) enable popup command history log */
  CheckboxMenuItem
    miVMshowHistoryLog;
     
  /** sGenomicMenu[cfg.nGenomicMenus] menu items as determined from Config DB */
  CheckboxMenuItem
    miVMgenomicViewer[];          
	
  /** Menu: (Analysis | Filter) Filter by GeneClass membership */
  CheckboxMenuItem
    miFRMgeneClassMbrFilter;
  /** Menu: (Analysis | Filter) Filter by user gene set membership */
  CheckboxMenuItem
    miFRMuseGeneSetFilter;
  /** Menu: (Analysis | Filter) Filter by good genes membership */
  CheckboxMenuItem
    miFRMgoodGeneListFilter;
  /** Menu: (Analysis | Filter) Filter by replicate genes membership */
  CheckboxMenuItem
    miFRMreplicateGenesFilter;
  
  /** Menu: (Analysis | Filter) Filter by ratio histogram bin */
  CheckboxMenuItem    
    miFRMhistRatioRngFilter;
  /** Menu: (Analysis | Filter) Filter by intensity histogram bin */
  CheckboxMenuItem
    miFRMhistIntensRngFilter;

  /** Menu: (Analysis | Filter) Filter by EGL gene membership */
  CheckboxMenuItem    
    miFRM_EGLfilter;
  /** Menu: (Analysis | Filter) Filter by spot CV test */
  CheckboxMenuItem
    miFRMspotCVfilter;
  /** Menu: (Analysis | Filter) Filter by Cluster test */
  CheckboxMenuItem
    miFRMclusterHP_Efilter;
  /** Menu: (Analysis | Filter) Filter by difference test */
  CheckboxMenuItem
    miFRMuseDiffFilter;
  /** Menu: (Analysis | Filter) Filter by HP-X and HP-Y F1+F2 t-Test */
  CheckboxMenuItem
    miFRMtTestXYfilter;
  /** Menu: (Analysis | Filter) Filter by HP-X and HP-Y 'sets' t-Test  */
  CheckboxMenuItem
    miFRMtTestXYsetsFilter;
  /** Menu: (Analysis | Filter) Filter by HP-X and HP-Y 'sets' KS-Test  */
  CheckboxMenuItem
    miFRMksTestXYsetsFilter;
  /** Menu: (Analysis | Filter) Filter by cur. OCL F-Test)  */
  CheckboxMenuItem
    miFRMfTestOCLFilter;

  /** Menu: (Analysis | Filter) Filter by high ratio range test */
  CheckboxMenuItem    
    miFRMhighXYratioFilter;
  /** Menu: (Analysis | Filter) Filter by low ratio range test */
  CheckboxMenuItem
    miFRMlowXYratioFilter;  
  
  /** Menu: (Analysis | Filter | Filter by spot intensity [SI1:SI2] sliders) 
   * Filter by spot intensity range test */
  CheckboxMenuItem
    miFRMthrSpotIntens;

  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * within range test */
  CheckboxMenuItem    
    miFRMspotIntensRngIn;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * outside range test */
  CheckboxMenuItem
    miFRMspotIntensRngOut;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * test current HP sample */
  CheckboxMenuItem
    miFRMspotIntensModeMS;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * test current HP-X and HP-Y samples */
  CheckboxMenuItem
    miFRMspotIntensModeXY;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * test current HP-X 'sets' samples */
  CheckboxMenuItem
    miFRMspotIntensModeXsets;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * test current HP-Y 'sets' samples */
  CheckboxMenuItem
    miFRMspotIntensModeYsets;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * test current HP-X or HP-Y 'sets' samples */
  CheckboxMenuItem
    miFRMspotIntensModeXORYsets;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * test current HP-X and HP-Y 'sets' samples */
  CheckboxMenuItem
    miFRMspotIntensModeXANDYsets;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * test current HP-E 'list' samples */
  CheckboxMenuItem
    miFRMspotIntensModeE;

  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * must have ALL selected samples */
  CheckboxMenuItem    
    miFRMspotIntensCompareModeALL;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * must have ANY selected samples */
  CheckboxMenuItem
    miFRMspotIntensCompareModeANY;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * must have AT MOST (n) selected samples */
  CheckboxMenuItem
    miFRMspotIntensCompareModeAT_MOST;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * must have AT LEAST (n) selected samples */
  CheckboxMenuItem
    miFRMspotIntensCompareModeAT_LEAST;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * must have PRODUCT selected samples less than threshold */
  CheckboxMenuItem
    miFRMspotIntensCompareModePRODUCT;
  /** Menu: (Analysis | Filter | spot intensity [SI1:SI2] sliders) 
   * must have SUM selected samples less than threshold */
  CheckboxMenuItem
    miFRMspotIntensCompareModeSUM;

  /** Menu: (Analysis | Filter | by intensity range test)
   * enabled */
  CheckboxMenuItem     
    miFRMthrGray;
  /** Menu: (Analysis | Filter | by intensity range test) 
   * within range */
  CheckboxMenuItem
    miFRMgrayRngIn;
  /** Menu: (Analysis | Filter | by intensity range test) 
   * outside range */
  CheckboxMenuItem
    miFRMgrayRngOut;
  
  /** Menu: (Analysis | Filter | by ratio range test) enabled */
  CheckboxMenuItem    
    miFRMthrRatio;
  /** Menu: (Analysis | Filter | by ratio range test) in range */
  CheckboxMenuItem
    miFRMratioRngIn;
  /** Menu: (Analysis | Filter | Filter by ratio range test)
   * outside of range */
  CheckboxMenuItem
    miFRMratioRngOut;
  
  /** Menu: (Analysis | Filter | Filter by Cy3/Cy5 ratio range test)
   * enabled */
  CheckboxMenuItem
    miFRMthrCy3Cy5Ratio;
  /** Menu: (Analysis | Filter | Filter by Cy3/Cy5 ratio range test)
   * in range */
  CheckboxMenuItem    
    miFRMCy3Cy5RatioRngIn;
  /** Menu: (Analysis | Filter | Filter by Cy3/Cy5 ratio range test) 
   * outside of range */
  CheckboxMenuItem
    miFRMCy3Cy5RatioRngOut;  
  
  /** Menu: (Analysis | Filter | CV threshold sliders) 
   * test current HP sample */
  CheckboxMenuItem    
    miFRMcvModeMS;
  /** Menu: (Analysis | Filter | CV threshold sliders) 
   * test current HP sample */
  CheckboxMenuItem
    miFRMcvModeXY;
  /** Menu: (Analysis | Filter | CV threshold sliders) 
   * test current HP-X 'set' samples */
  CheckboxMenuItem
    miFRMcvModeXset;
  /** Menu: (Analysis | Filter | CV threshold sliders) 
   * test current HP-Y 'set' samples */
  CheckboxMenuItem
    miFRMcvModeYset;
  /** Menu: (Analysis | Filter | CV threshold sliders) 
   * test current HP-X or HP-Y 'sets' samples */
  CheckboxMenuItem
    miFRMcvModeXORYsets;
  /** Menu: (Analysis | Filter | CV threshold sliders) 
   * test current HP-X and HP-Y 'sets' samples */
  CheckboxMenuItem
    miFRMcvModeXANDYsets;
  /** Menu: (Analysis | Filter | CV threshold sliders) 
   * test current HP sample */
  CheckboxMenuItem
    miFRMcvModeE;
  
  /** Menu: (Analysis | Filter | CV threshold sliders) 
   * use the CV mean else the CV max in computation */
  CheckboxMenuItem    
    miFRMuseCVmeanElseMax;
  
  /** Menu: (Analysis | Filter | use positive quantified data) 
   */
  CheckboxMenuItem    
    miFRMusePosQuantDataFlag;
  /** Menu: (Analysis | Filter | use positive quantified data) 
   * test current HP sample */  
  CheckboxMenuItem
    miFRMposQuantModeMS;
  /** Menu: (Analysis | Filter | use positive quantified data) 
   * test current HP-X and HP-Y  samples */
  CheckboxMenuItem
    miFRMposQuantModeXY;
  /** Menu: (Analysis | Filter | use positive quantified data) 
   * test current HP-X and HP-Y 'set' samples */
  CheckboxMenuItem
    miFRMposQuantModeXYsets;
  /** Menu: (Analysis | Filter | use positive quantified data) 
   * test current HP-E 'list' samples */
  CheckboxMenuItem
    miFRMposQuantModeE;
  
  /** Menu: (Analysis | Filter | use Good Spot data) 
   */  
  CheckboxMenuItem    
    miFRMuseGoodSpotDataFlag;
  /** Menu: (Analysis | Filter | use Good Spot data) 
   * test current HP sample */ 
  CheckboxMenuItem
    miFRMgoodSpotModeMS;
  /** Menu: (Analysis | Filter | use Good Spot data) 
   * test current HP-X and HP-Y  samples */
  CheckboxMenuItem
    miFRMgoodSpotModeXY;
  /** Menu: (Analysis | Filter | use Good Spot data) 
   */ 
  CheckboxMenuItem
    miFRMgoodSpotModeXORYsets;
  /** Menu: (Analysis | Filter | use Good Spot data) 
   * test current HP-X and HP-Y 'set' samples */
  CheckboxMenuItem
    miFRMgoodSpotModeXANDYsets;
  /** Menu: (Analysis | Filter | use Good Spot data) 
   * test current HP-E 'list' samples */
  CheckboxMenuItem
    miFRMgoodSpotModeE;
  
  /** Menu: (Analysis | Filter | use Spot Detection Value data) 
   */  
  CheckboxMenuItem    
    miFRMuseDetValueSpotDataFlag;
  /** Menu: (Analysis | Filter | use only genes with non-zero intensity) 
   */  
  CheckboxMenuItem    
    miFRMOnlyGenesWithNonZeroDensityFlag;    
  /** Menu: (Analysis | Filter | use Spot Detection Valuedata) 
   * test current HP sample */ 
  CheckboxMenuItem
    miFRMdetValueSpotModeMS;
  /** Menu: (Analysis | Filter | use Spot Detection Value data) 
   * test current HP-X and HP-Y  samples */
  CheckboxMenuItem
    miFRMdetValueSpotModeXY;
  /** Menu: (Analysis | Filter | use Spot  Detection Value data) 
   * test current HP-X and HP-Y 'set' samples */
  CheckboxMenuItem
    miFRMdetValueSpotModeXANDYsets;
  /** Menu: (Analysis | Filter | use Spot  Detection Value data) 
   * test current HP-E 'list' samples */
  CheckboxMenuItem
    miFRMdetValueSpotModeE;

  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudograyscale intensity */
  CheckboxMenuItem
    miPMplotPseudoImg;
  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudocolor Red(X)-Yellow-Green(Y) HP-X/Y ratio or Zdiff */
  CheckboxMenuItem
    miPMplotPseudoHP_XY_RYGImg;
  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudocolor Red(Cy5)-Yellow-Green(Cy3) Cy3/Cy5 ratio or Zdiff */
  CheckboxMenuItem
    miPMplotPseudoHP_F1F2_RYGImg;
  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudocolor HP-X/Y ratio or Zdiff */
  CheckboxMenuItem
    miPMplotPseudoHP_XYImg;
  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudocolor Cy3/Cy5 ratio or Zdiff */
  CheckboxMenuItem
    miPMplotPseudoF1F2Img;
  /** Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudocolor (HP-X,HP-Y) 'sets' p-value */
  CheckboxMenuItem
    miPMplotPseudoHP_XY_pValueImg; 
  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudocolor HP-EP 'list' CV-value */
  CheckboxMenuItem
    miPMplotPseudoHP_EP_CV_valueImg= null;
  
  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudocolor dual HP-X and HP-Y as Field 1 and Field 2 grids*/
  CheckboxMenuItem   
    miPMdualXYpseudoImg;
  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Flicker of HP-X and HP-Y samples in intensity mode */
  CheckboxMenuItem
    miPMflickerXY;
  /**  Menu:(Analysis | Plot | Show Microarray) 
   * use Pseudocolor low range zoom */
  CheckboxMenuItem
    miPMlowRangeZoom;   
  
  /** (Analysis | Plot | Expression Profile) Menu
   * Expression Profile list type (overlay or grid).
   */
  CheckboxMenuItem
    miPEPlistType; 

  /** Menu: (Analysis | Cluster) cluster by finding similar genes */
  CheckboxMenuItem 
    miCLMfindSimGenesDisp;
  /** Menu: (Analysis | Cluster) cluster by finding similar gene counts */
  CheckboxMenuItem
    miCLMsimGeneCountsDisp;
  /** Menu: (Analysis | Cluster) cluster by hierarchical clustering */
  CheckboxMenuItem
    miCLMhierClusterDisp;
  /** Menu: (Analysis | Cluster) cluster by K-means */
  CheckboxMenuItem    
    miCLMdispKmeansNodes;

  /** Menu: (Analysis | Cluster | cluster by K-means)
   * cluster using K-medians else use K-means */
  CheckboxMenuItem    
    miCLMuseMedianKmeans;
  /** Menu: (Analysis | Cluster)
   * cluster using correlation coefficient else Euclidean distance */
  CheckboxMenuItem
    miCLMuseCorrCoeffDist;
  /** Menu: (Analysis | Cluster | hierarchical )
   * cluster using LSQ magnitude normalization else ... */
  CheckboxMenuItem
    miCLMuseLSQmagNorm;
   
   /** Menu: (Analysis | Cluster | Hierarchical) use 
    * average-arithmetic linkage for hier.clustering  */
   CheckboxMenuItem
     miCLHCMavgPGMALnk;               
   /** Menu: (Analysis | Cluster | Hierarchical)  
    * average-centroid linkage for hier.clustering  */
   CheckboxMenuItem
     miCLHCMavgPGMCLnk;               
   /** Menu: (Analysis | Cluster | Hierarchical)  
    * next-min for linkage hier.clustering */
   CheckboxMenuItem
     miCLHCMnextMinLnk;               
   /** Menu: (Analysis | Cluster | Hierarchical)  
    * unweighted/weighted average for hier.clustering */
   CheckboxMenuItem
     miCLHCMunWtAvg;                  
   /** Menu: (Analysis | Cluster | Hierarchical)  
    * normalize EP by HP sample for hier.clustering */
   CheckboxMenuItem
     miCLMnormHP;                   
  /** Menu: (Analysis | Cluster | Hierarchical)  
   * use Cluster dist-dist cache matrix for  hier.clustering */
  CheckboxMenuItem
    miCLHCMuseClusterDistCache;      
  /** Menu: (Analysis | Cluster | Hierarchical)  
   * use 16-bit short vs 32-bit float for hier.clustering */
  CheckboxMenuItem
    miCLHCMuseShortClusterDistCache; 
    
  /** Menu: (Analysis | Report) font size is 8 pt */
  CheckboxMenuItem
    miTMtblFontSize8pt; 
  /** Menu: (Analysis | Report) font size is 10 pt */
  CheckboxMenuItem
     miTMtblFontSize10pt;
  /** Menu: (Analysis | Report) font size is 12 pt */
  CheckboxMenuItem
     miTMtblFontSize12pt;
    
  /** Menu: (Analysis | Report) font size is 8 pt */
  CheckboxMenuItem
    miPRtblFontSize8pt; 
  /** Menu: (Analysis | Report) font size is 10 pt */
  CheckboxMenuItem
     miPRtblFontSize10pt;
  /** Menu: (Analysis | Report) font size is 12 pt */
  CheckboxMenuItem
     miPRtblFontSize12pt;

  /** Menu: (Analysis | Report) use Tab-Delimited table format */
  CheckboxMenuItem
     miTMtblFmtTabDelim;
  /** Menu: (Analysis | Report) use Dynamic clickable table format */
  CheckboxMenuItem
     miTMtblFmtClickable;
  /** Menu: (Analysis | Report) add Expr-Profile data to Table */
  CheckboxMenuItem
     miTMtblFmtAddEPdata;
  /** Menu: (Analysis | Report) use raw Expr-Profile data in Table */
  CheckboxMenuItem
     miTMtblFmtUseRawEPdata;
  /** Menu: (Analysis | Report) add HP-XY set statistics data to Table */
  CheckboxMenuItem
     miTMtblFmtAddHP_XYsetStat;
  /** Menu: (Analysis | Report) add OCL statistics data to Table */
  CheckboxMenuItem
     miTMtblFmtAddOCLstatistics; 
  
  /** Menu: (Plugins | Use S-PLUS, else R, as computing engine [CB]) */
  CheckboxMenuItem
    miPLGMuseSPLUSasComputingEngine;
  
  /** Menu: (Plugins | Save SRLO reports in time-stampled folder [CB]) */
  CheckboxMenuItem
    miPLGMuseTimeStampReports;
  
  /** GUI label for  current HP-X */
  Label
    HP_Xlabel;
  /** GUI label for  current HP-Y */ 
  Label               
    HP_Ylabel;                   
  
 
  /**
   * MenuBarFrame() - constructor for main GUI window and menubar.
   * @param mae is instance of MAExplorer
   * @param title is the name of the frame
   * @param x0 is ULHC X position of frame
   * @param y0 is ULHC Y position of frame
   * @see #addMenuBar
   * @see #buildMenuGUI
   * @see #insertPluginMenuEntries
   * @see #setTitle
   */
  MenuBarFrame(MAExplorer mae, String title, int x0, int y0)
  { /* MenuBarFrame */
    this.mae= mae;
    cfg= mae.cfg;
    this.x0= x0;            /* placement & size of MnuBarFrame */
    this.y0= y0;
    
    /* Set the Frame window title string - overide with our title */
    //this.title= title;
    this.title= mae.cfg.database +" - "+ mae.browserTitle;
    String aTitle= mae.browserTitle;
    
    /* Set the size of the main window */
    int
      //width= (mae.presentViewFlag) ? 710 : 680, /* main window */
      width= (mae.presentViewFlag) ? 660 : 630,   /* main window */
      height= 500;                                /* main window */
    
    menuActionLabelList= new String[MAX_CMDS];    /* list of menuItem labels */
    menuActionCmdList= new String[MAX_CMDS];     /* list of menuItem commands */
    chkBoxMenuLabelList= new String[MAX_CHKBOX_CMDS]; /* checkboxItem names */
    chkBoxMenuItemList= new CheckboxMenuItem[MAX_CHKBOX_CMDS]; /* checkboxItem items */
    
    /* setup menu stub lists */
    nMenuStubs= 0;
    stubNames= new String[MAX_MENU_STUBS];
    stubMenus= new Menu[MAX_MENU_STUBS];
    
    smallFontLbl= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
    mediumFontLbl= new Font(mae.cfg.fontFamily, Font.PLAIN, 12);
    useFontLbl= (mae.presentViewFlag) ? mediumFontLbl : smallFontLbl;
    
    exprProfilePopup= null;
    masterIDguesserIsPoppedUp= false;
    gnMidGuesser= null;
    hasStopButtonFlag= false;
    hpMenuGuesserIsPoppedUp= false;
    hpMenuChooserIsPoppedUp= false;
    hpMenuToUpdateFromGuesser= null;
    hpMenuGuesser= null;
    
    /* Adding the menubar will modify nCmds.
     *
     * [TODO] rewrite and move out of MenuBarFrame back into
     * MAExplorer proper and invoke it at the very end of init
     * at [19] when notify user that everything is ready.
     * This lets us use the status of .quant file data to
     * determine which menus should be active.
     */
    addMenuBar();
    
    /* Go actually build the GUI */
    buildMenuGUI(x0, y0, width, height);
    
    /* Insert Plugin menus if any at proper stubs */
    insertPluginMenuEntries();
    
    setTitle(aTitle);
    
    /* Save the size parameters */
    width= Math.min(width, (int)(mae.scrWidth*0.90));
    height= Math.min(height, (int)(mae.scrHeight*0.90));
    setBounds(x0, y0, width, height);     /* set the size of the frame */
    setResizable(true);
    
    show();
    requestFocus();
    
    this.addWindowListener(this); /* listener for window events */
  } /* MenuBarFrame */
  
  
  /**
   * MnuBarFrame() - constructor used with the Applet.
   * @param mae is instance of MAExplorer
   */
  MenuBarFrame(MAExplorer mae)
  { /* MenuBarFrame */
    this(mae,"MAE",20,20);
  } /* MenuBarFrame */
  
  
  /**
   * redoMenuBar() - redo the menubar if restarted a new database with different entries.
   * @see #addMenuBar
   * @see #insertPluginMenuEntries
   * @see #setTitle
   */
  void redoMenuBar()
  { /* redoMenuBar */
    /* Set the Frame window title string - overide with our title */
    this.title= mae.cfg.database +" - "+ mae.browserTitle;
    String aTitle= mae.browserTitle;    
    
    /* re-setup menu stub lists */
    nMenuStubs= 0;
    useFontLbl= (mae.presentViewFlag) ? mediumFontLbl : smallFontLbl;
    
    /* Adding the menubar will modify nCmds and the list of commands.
     *
     * [TODO] rewrite and move out of MenuBarFrame back into
     * MAExplorer proper and invoke it at the very end of init
     * at [19] when notify user that everything is ready.
     * This lets us use the status of .quant file data to
     * determine which menus should be active.
     */
    addMenuBar();
    
    /* Insert Plugin menus if any at proper stubs */
    //removeAllPluginMenus();  /* [TODO] ny datastructures required */
    insertPluginMenuEntries();
    
    setTitle(aTitle);
    
    System.gc();
    
    show();
    requestFocus();
  } /* redoMenuBar */
  
  
  /**
   * addMenuStubToMenuStubList() - add menu position to Menu Stub List.
   * This list can be used when loading plugins to associate the plugin
   * with a particular menu stub in the menu tree.
   *<BR>
   * The data for this is found in the Config DB as four paired numbered
   * PARAM entries and is loaded when the Config DB is loaded:
   * <PRE>
   *    "PluginMenuName"+i.         E.g. "New sample report"
   *    "PluginMenuStubName"+i.     E.g. "Report:sample"
   *    "PluginClassFile"+i.        E.g. "NewReport.class"
   *    "PluginCallAtStartup"+i.    E.g. "TRUE"
   *</PRE>
   * @param name is the name of the menu stub
   * @param menuStub is the Menu stub associated with the name
   */
  private void addMenuStubToMenuStubList(String name, Menu menuStub)
  { /* addMenuStubToMenuStubList */
    if(nMenuStubs>=MAX_MENU_STUBS-1)
      return;               /* can't save it */
    
    stubNames[nMenuStubs]= name;
    stubMenus[nMenuStubs++]= menuStub;
  } /* addMenuStubToMenuStubList */
  
  
  /**
   * findMenuStubByName() - find menu stub associated with name in Menu Stub List.
   *
   * The data for this is found in the Config DB as four paired numbered
   * PARAM entries and is loaded when the Config DB is loaded:
   * <PRE>
   *    "PluginMenuName"+i.         E.g. "New sample report"
   *    "PluginMenuStubName"+i.     E.g. "Report:sample"
   *    "PluginClassFile"+i.        E.g. "NewReport.class"
   *    "PluginCallAtStartup"+i.    E.g. "TRUE"
   *</PRE>
   * @param name is the name of the menu stub to look for
   * @return  Menu stub associated with the name
   */
  private Menu findMenuStubByName(String name)
  { /* findMenuStubByName */
    Menu menu= null;
    
    for(int i=0;i<nMenuStubs;i++)
      if(stubNames[nMenuStubs].equals(name))
      {
        menu= stubMenus[i];
        break;
      }
    return(menu);
  } /* findMenuStubByName */
  
  
  /**
   * insertPluginMenuEntries() - insert Plugin menus if any at proper stubs.
   * Search the config sPluginXXX[] database for plugins to install
   * in the menu and to load. Note: this is called AFTER the initial
   * menu tree is built.
   *
   * The data for this is found in the Config DB as three paired numbered
   * PARAM entries and is loaded when the Config DB is loaded:
   * <PRE>
   *    "PluginMenuName"+i.         E.g. "New sample report"
   *    "PluginMenuStubName"+i.     E.g. "Report:sample"
   *                         (from legal list of Menu stub names)
   *    "PluginClassFile"+i.        E.g. "NewReport.class"
   *    "PluginCallAtStartup"+i.    E.g. "InstallInMenu"
   *                                 (1 of: "InstallInMenu", "RunOnStartup", "NoInstall")
   *</PRE>
   * @see #findMenuStubByName
   */
  private void insertPluginMenuEntries()
  { /* insertPluginMenuEntries */
    //MaeJavaAPI.setMenuHook(menuStub);
    for(int i=0;i<cfg.nPluginMenus;i++)
    { /* add all plugins into the menu and load plugin class */
      Menu menuStub= findMenuStubByName(cfg.sPluginMenuStubName[i]);
      if(menuStub!=null)
      {
        //loadClass(cfg.sPluginClassFile[i]);
        /* If class loaded ok, then add the entry to the menu stub */
        menuStub.add(cfg.sPluginMenuName[i]);
        
        /* [TODO] JAI: we need to hook this in somehow to the code
         * you wrote in MaeJavaAPI.
         * [JE Code already exists this is not an issue at present
         */
        //MaeJavaAPI.setMenuHook(menuStub);
      }
    } /* add all plugins into the menu and load plugin class */
  } /* insertPluginMenuEntries */
  
  
  /**
   * callAllStartupPlugins() - call all Plugins initialized at Startup.
   * These are indicated by sPluginCallAtStartup[] being "TRUE".
   * Search the config sPluginXXX[] database for Startup plugins to call.
   * Note: these classes were previously installed.
   *
   * The data for this is found in the Config DB as four paired numbered
   * PARAM entries and is loaded when the Config DB is loaded:
   * <PRE>
   *    "PluginMenuName"+i.         E.g. "New sample report"
   *    "PluginMenuStubName"+i.     E.g. "Report:sample"
   *    "PluginClassFile"+i.        E.g. "NewReport.class"
   *    "PluginCallAtStartup"+i.    E.g. "TRUE"
   *</PRE>
   */
  void callAllStartupPlugins()
  { /* callAllStartupPlugins */
    for(int i=0;i<cfg.nPluginMenus;i++)
    { /* call all Startup plugins */
      String sStartupFlag= cfg.sPluginCallAtStartup[i];
      boolean startupFlag= (sStartupFlag.equals("TRUE") ||
                            sStartupFlag.equals("true"));
      if(!startupFlag)
        continue;
      
      /* [TODO] (Jai) Call the plugin */
    }
  } /* callAllStartupPlugins */
  
  
  /**
   * makeMenuItem() - make menuItem entry in menu list.
   * Setup action command and listener call back.
   * If the command name is null, set the command name to label name.
   * if shortCut is <0, then gray-out the item. i.e. not available and
   * do not add to event handler.
   * @param pm is the menu to install it
   * @param sLabel is the visible label
   * @param sCmd is the opt Cmd name (uses sLabel if null)
   * @param shortcut is the opt short cut
   */
  private void makeMenuItem(Menu pm, String sLabel, String sCmd, int shortcut )
  { /*makeMenuItem*/
    MenuItem mi= new MenuItem(sLabel);
    if(sCmd==null)
      sCmd= sLabel;		     /* use same string for both */
    mi.setActionCommand(sCmd);     /* separate string for CMD & LABEL*/
    
    if(shortcut>=0)
      mi.addActionListener(this);   /* actionListener in this class */
    else
      mi.setEnabled(false);
    
    /* Push sCmd into menuActionNameList[] in case need to look it up */
    if(nCmds<this.MAX_CMDS)
    {
      menuActionLabelList[nCmds]= sLabel;   /* Save for event handler */
      menuActionCmdList[nCmds++]= sCmd;     /* Save for event handler */
    }
    
    if(shortcut>0)
    {
      MenuShortcut
      msc= new MenuShortcut(shortcut);
      mi.setShortcut(msc);	                /* optional shortcut */
    }
    pm.add(mi);
  } /* makeMenuItem*/
  
  
  /**
   * makeSubMenu() - make submenu entry in menu list.
   * Setup action command and listener call back.
   * If the command name is null, set the command name to label name.
   * @param pm is the menu to install it
   * @param sLabel is the visible label
   * @param sCmd is the opt Cmd name (uses sLabel if null)
   * @param shortcut is the opt short cut
   */
  private Menu makeSubMenu(Menu pm, String sLabel, String sCmd, int shortcut )
  { /* makeSubMenu*/
    Menu mi= new Menu(sLabel);
    if(shortcut!=0)
    {
      MenuShortcut msc= new MenuShortcut(shortcut);
      mi.setShortcut(msc);	       /* optional shortcut */
    }
    pm.add(mi);
    return(mi);
  } /*makeSubMenu*/
  
  
  /**
   * makeChkBoxMenuItem() - make CheckboxMenuItem entry in popup menu list.
   * Setup action command and listener call back.
   * If the command name is null, set the command name to label name.
   * if shortCut is <0, then gray-out the item. i.e. not available and
   * do not add to event handler.
   * @param pm is the menu to install it
   * @param sLabel is the visible label
   * @param sCmd is the opt Cmd name (uses sLabel if null)
   * @param shortcut is the opt short cut
   * @param value is the initial value of the checkbox
   */
  private CheckboxMenuItem makeChkBoxMenuItem(Menu pm, String sLabel, 
                                              String sCmd, int shortcut, 
                                              boolean value )
  { /* makeChkBoxMenuItem */
    CheckboxMenuItem mi= new CheckboxMenuItem(sLabel,value);
    
    mi.setState(value);
    if(sCmd==null)
      sCmd= sLabel;		     /* use same string for both */
    mi.setActionCommand(sCmd);     /* separate string for CMD and  LABEL */
    if(shortcut>=0)
      mi.addItemListener(this);    /* actionListener in this class */
    else
      mi.setEnabled(false);
    
    if(shortcut>0)
    {
      MenuShortcut  msc= new MenuShortcut(shortcut);
      mi.setShortcut(msc);	      /* optional shortcut */
    }
    
    /* Push sCmd and item into chkBoxMenuLabelList[] in case need to look it up */
    if(nCBcmds<this.MAX_CHKBOX_CMDS)
    { /* Save for event handler */
      chkBoxMenuLabelList[nCBcmds]= sCmd;
      chkBoxMenuItemList[nCBcmds++]= mi;
    }
    pm.add(mi);
    return(mi);
  } /* makeChkBoxMenuItem*/
  
  
  /**
   * addSetHP_XYEsubmenuTree() - add HP sample submenu tree to submenu if submenus exist.
   * The hpPostfixStr : "_X" or "_Y" or "_E" or ""
   * @param subMenu to add to the tree
   * @param hpPostfixStr command string to associate with the submenu entries.
   * @see #makeMenuItem
   * @see #makeSubMenu
   */
  void addSetHP_XYEsubmenuTree(Menu subMenu, String hpPostfixStr)
  { /* addSetHP_XYEsubmenus */
    /* [1] Add H.P. (mae.msDTT) selected using the  stage
     * DevelopentalStage  subsetting of H.P. list.
     * The entries for that menu will use
     * the "HP:..." prefix so they are serviced normally.
     * The "HP_X:..." prefix sets the msX instance
     * The "HP_Y:..." prefix sets the msY instance
     * The "HP_E:..." prefix sets the msE instance
     */
    if(mae.hps!=null && mae.hps.nTopSubMenus>0)
    { /* add submenu tree */
      Menu
        devStageMenu= makeSubMenu(subMenu, "By Source", null,0),
        devStageSubMenu[]= new Menu[mae.hps.nTopSubMenus];
      StageNames sn;
      String
       cmdLabel,
       Strain,
       Stage,
       Probe,
       viewerLabel;
      int j;
      
      for(int i=0; i<mae.hps.nTopSubMenus; i++)
      { /* build devel. stage menu tree */
        devStageSubMenu[i]= makeSubMenu(devStageMenu,
                                        mae.hps.topSubMenuNames[i],
                                        null,0);
        
        for(j=0; j<mae.sg.nHPmnuEntries; j++)
        { /* build subtree for stage(i) */
          sn= mae.sg.HPmnuEntry[j];
          
          if(sn.subMenuNbr!=i)
            continue;  /* not for this submenu(i) i.e. 'Source' */
          cmdLabel= "HP" + hpPostfixStr + ":" + mae.snHPName[sn.hpNbr];
          /*
          Strain= mae.sg.strainNames[sn.strainIdx];
          Stage= (sn.stageIdx==-1) ? "" : mae.sg.stageNames[sn.stageIdx];
          Probe= (sn.probeIdx==-1) ? "" : mae.sg.probeNames[sn.probeIdx];
          */
          viewerLabel= sn.stageLabel;
          makeMenuItem(devStageSubMenu[i],  viewerLabel, cmdLabel,0);
        } /* build subtree for stage(i) */
      } /* build devel. stage menu tree */
      
      subMenu.addSeparator();
    } /* add submenu tree */
    
    /* [2] Make list of ALL hybridized samples in the database
     * using Guesser.
     */
    String cmdStr= "HpGuesser:HP" + hpPostfixStr;
    makeMenuItem(subMenu, "From list of all samples", cmdStr, 0);
  } /* addSetHP_XYEsubmenus */
  
    
  /**
   * addRLOsubmenuTree() - add RLO R scripts submenu tree to rloSmnu if RLOs exist.
   * @see #makeMenuItem
   * @see #makeSubMenu
   */
  void addRLOsubmenuTree()
  { /* addRLOsubmenuTree */
    /* Get the RLO entries and add the menu items */
    MJAReval mr= mae.mja.mjaReval;
    
    /* [1] Load the RLO/*.rlo database entries into memory DB. */ 
    if(!mr.setupAllBasePaths(mae.fileBasePath))
      return;             /* R was not installed, DO NOT INSTALL the menu entries */
    
    mr.readPermanentRLOdatabase(true /* loadDemoRLOsFlag */);
    
    /* [2] Get list of RLO menu names */
    nRLOs= 0;
    String
      rloMN[]= mr.getListofRLOmenuNames(),
      rloRSN[]= mr.getListofRLOscriptNames();
    
    /* [3] Build menu entries to add to rloSmnu menu stub */
    if(rloMN!=null)
    { /* build the submenu items */
      int nAllRLOs= rloMN.length;
      if(nAllRLOs>0)
      { /* add RLO methods */
        /* Sort alphabetically */
        int sortIdx[]= SortMAE.bubbleSortIndex(rloRSN, nAllRLOs, true);
        rloMenuNames= new String[nAllRLOs];
        rloRscriptNames= new String[nAllRLOs];
        
        for(int i=0;i<nAllRLOs;i++)
        {
          int idx=  sortIdx[i];
          if(mae.NEVER && rloRSN[idx].startsWith("DemoRLO"))
            continue;               /* ignore DEMO RLOs */
          
          rloMenuNames[nRLOs]= rloMN[idx];
          rloRscriptNames[nRLOs]= rloRSN[idx];
          makeMenuItem(rloSmnu, rloMN[idx], "RLO:"+rloRSN[idx],0);
          nRLOs++;
        }
      } /* add RLO methods */
    } /* build the submenu items */
  } /* addRLOsubmenuTree */
  
  
  /**
   * addMenuBar() - add menubar with menus to the frame
   * @return the number of menu and commands created
   * @see MaeJavaAPI
   * @see MaeJavaAPI#setMenuHook
   * @see #addSetHP_XYEsubmenuTree
   * @see #addMenuStubToMenuStubList
   * @see #makeChkBoxMenuItem
   * @see #makeMenuItem
   * @see #makeSubMenu
   */
  int addMenuBar()
  { /* addMenuBar */
    nCBcmds= 0;   	        /* # of menu chkbox item cmds [0:nCBcmds-1]*/
    nCmds= 0;		/* # of menu item commands [0:nCmds-1]*/
    
    int
      LECBdebug= mae.LECBdebug,
      isSAvisible= (!mae.isAppletFlag) ? 0 : -1,
      isDEBUG= (mae.DEBUG) ? 0 : -1,
      isSAvisibleDEBUG= (!mae.isAppletFlag && mae.DEBUG) ? 0 : -1;
    String
      sf1= mae.cfg.fluoresLbl1,
      sf2= mae.cfg.fluoresLbl2;
    
    if(mae.useCy5OverCy3Flag)
    { /* flip Cy3/Cy5 to Cy5/Cy3 */
      String sTmp= sf1;
      sf1= sf2;
      sf2= sTmp;
    }
    String changeStr= (mae.useRatioDataFlag)
                          ? "("+sf1+"/"+sf2+")" : "intensity";    
    
    /* [1] Create menu bar and install it */
    mbar= new MenuBar();
    this.setMenuBar(mbar); /* activate menu bar even if menus are not active*/
    
    Font newFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 12);
    mbar.setFont(newFont);         /* make it a larger font */
    this.mbar= mbar;
    
    /* [1.1] Create a set of popup menus to add to the Buttons
     * or which are then added to the menu bar panel. The event
     * handler will do a show() to bring these up.
     */
    this.analysisMenu= new Menu("Analysis");
    
    this.fileMenu= new Menu("File");
    this.sampleMenu= new Menu("Samples");
    this.geneClassMenu= new Menu("GeneClass");
    this.normMenu= new Menu("Normalization");
    this.editMenu= new Menu("Edit");
    this.filterMenu= new Menu("Filter");
    this.viewMenu= new Menu("View");
    this.plotMenu= new Menu("Plot");
    this.clusterMenu= new Menu("Cluster");
    this.reportMenu= new Menu("Report");
    this.pluginsMenu= new Menu("Plugins");
    this.helpMenu= new Menu("Help");
    
    /* Add menus to the menu bar in this order */
    mbar.add(this.fileMenu);         /* Always put FILES on left*/
    mbar.add(this.sampleMenu);
    /* use "Analysis menu" of main menus.
     * Puts several top level menus in
     * an "Analysis" pull-down menu
     * E.g.  Analysis
     *       ========
     *       GeneClasses
     *       Normalization
     *       Filter
     *       Plot
     *       Cluster
     *       Report
     */
    this.analysisMenu.add(this.geneClassMenu);
    this.analysisMenu.add(this.normMenu);
    this.analysisMenu.add(this.filterMenu);
    this.analysisMenu.add(this.plotMenu);
    this.analysisMenu.add(this.clusterMenu);
    this.analysisMenu.add(this.reportMenu);
    
    mbar.add(this.editMenu);
    mbar.add(this.analysisMenu);
    mbar.add(this.viewMenu);
    mbar.add(this.pluginsMenu);
    mbar.add(this.helpMenu);
    
    /* Activate MAEPlugins. Put a plugin loader into the menu bar and
     * make sure the Menu Hook is set.
     */
    if (mae.mja!=null)
      mae.mja.setMenuHook(this.pluginsMenu); /* just use it */
    else
    { /* get around chicken and egg MaeJavaAPI generation problem */
      mae.mja= new MaeJavaAPI(mae);       /* create it here */
      mae.mja.setMenuHook(this.pluginsMenu);
    }
    
    /* Disable menus so users keep their cotten picken mouse
     * off of commands.
     */
    this.enableMenus("none");
    
    /* [2.1] Build the "FILE" menu */
    /* Only if doing an application!!!!! */
    Menu
      smnu,   /* tmp submenu variables used while building the tree */
      smnu2,
      smnu3;
               
    /* Menu stub to add/modify R(Plugin | RLO methods (submenu)) menu items */
    rloSmnu= null;
    nRLOs= 0; 
    
    smnu= makeSubMenu(this.fileMenu, "Databases", null,0);
    makeMenuItem(smnu, "New project", null, isSAvisibleDEBUG);
    makeMenuItem(smnu, "Set project", null, isSAvisibleDEBUG);
    makeMenuItem(smnu, "Open disk DB", null, isSAvisible);
    makeMenuItem(smnu, "Open Web DB", null, isSAvisibleDEBUG);
    makeMenuItem(smnu, "Save disk DB", null, isSAvisible);
    makeMenuItem(smnu, "Save as disk DB", null, isSAvisible);
    
    addMenuStubToMenuStubList("FileMenu:Databases",smnu);
    
    smnu= makeSubMenu(this.fileMenu, "Exploratory 'state'", null,0);
    makeMenuItem(smnu, "Register", "State:reg",isDEBUG);
    makeMenuItem(smnu, "Login", "State:login",0);
    makeMenuItem(smnu, "Open user's state", "State:open",isDEBUG);
    makeMenuItem(smnu, "Save user's state", "State:save",isDEBUG);
    makeMenuItem(smnu, "Directory of user's states", "State:dir",isDEBUG);
    makeMenuItem(smnu, "Delete user state", "State:del",isDEBUG);
    
    addMenuStubToMenuStubList("FileMenu:State",smnu);
    
    smnu= makeSubMenu(this.fileMenu, "Groupware", null,0);
    makeMenuItem(smnu, "Open another user's state","State:openOther",isDEBUG);
    makeMenuItem(smnu, "Share user state", "State:shr",isDEBUG);
    makeMenuItem(smnu, "Unshare user state", "State:unshr",isDEBUG);
    
    addMenuStubToMenuStubList("FileMenu:Groupware",smnu);
    
    //if(mae.NEVER)
    //{ /* reset is optional */
    //  smnu= makeSubMenu(this.fileMenu, "Reset", null,0);
    //  makeMenuItem(smnu, "Sample-X","Reset:HP-X", -1);
    //  makeMenuItem(smnu, "Sample-Y", "Reset:HP-Y", -1);
    //  makeMenuItem(smnu, "Sample-E", "Reset:HP-E", -1);
    //  makeMenuItem(smnu, "GeneClass", "Reset:GeneClass", -1);
    //  makeMenuItem(smnu, "Normalization", "Reset:norm", -1);
    //  makeMenuItem(smnu, "Reset all", "Reset:all", 0 /*KeyEvent.VK_T*/);
    //} /* reset is optional */
       
    makeMenuItem(this.fileMenu, "Update MAExplorer from maexplorer.sourceforge.net",
                 "UpdateMAE",0);         /* only for standalone*/
    makeMenuItem(this.fileMenu, "Update Plugins from maexplorer.sourceforge.net",
                 "UpdateMAEPlugins",0);  /* only for standalone*/    
    makeMenuItem(this.fileMenu, "Update RLO methods from maexplorer.sourceforge.net",
                 "UpdateRLOmethods",0);  /* only for standalone*/
    if(mae.DBUG_MAERlibr_ZIP_UPDATE)
      makeMenuItem(this.fileMenu, "Update MAERlibr.zip from maexplorer.sourceforge.net",
                   "UpdateMAERLIB",0);  /* only for standalone*/
    
    makeMenuItem(this.fileMenu, "Quit", null,0);  /* only for standalone*/
    addMenuStubToMenuStubList("FileMenu",this.fileMenu);
    
    /* [2.2] Build the "HybridSample" menu and submenus*/
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */
      makeMenuItem(this.sampleMenu, "Choose HP-X, HP-Y and HP-E samples", 
                   "HpChooser",0);
      makeMenuItem(this.sampleMenu, "Choose named condition lists of samples", 
                   "CondChooser",0);
      makeMenuItem(this.sampleMenu, "Choose ordered lists of conditions", 
                   "OrderedCondChooser",0);
      smnu= makeSubMenu(this.sampleMenu, "Set Samples from lists", null,0);
      smnu2= makeSubMenu(smnu, "Set current HP-X sample",  null,0);
      addSetHP_XYEsubmenuTree(smnu2, "_X");
      smnu2= makeSubMenu(smnu, "Set current HP-Y sample",  null,0);
      addSetHP_XYEsubmenuTree(smnu2, "_Y");
      
      /* edit HP-X and HP-Y sets using menu */
      smnu2= makeSubMenu(smnu, "Edit HP-X & HP-Y 'sets' of samples by source",
                         null,0);
      smnu3= makeSubMenu(smnu2, "Add sample to HP-X set", null,0);
      addSetHP_XYEsubmenuTree(smnu3, "_X+");
      
      smnu3= makeSubMenu(smnu2, "Add sample to HP-Y set", null,0);
      addSetHP_XYEsubmenuTree(smnu3, "_Y+");
      
      smnu3= makeSubMenu(smnu2, "Remove sample from HP-X set", null,0);
      addSetHP_XYEsubmenuTree(smnu3, "_X-");
      
      smnu3= makeSubMenu(smnu2, "Remove sample from HP-Y set", null,0);
      addSetHP_XYEsubmenuTree(smnu3, "_Y-");
      
      /* edit HP-E using menu */
      smnu2= makeSubMenu(smnu, "Edit HP-E expr. profile 'list' by source", 
                         null,0);
      smnu3= makeSubMenu(smnu2, "Add sample to HP-E list", null,0);
      addSetHP_XYEsubmenuTree(smnu3, "_E+");
      
      smnu3= makeSubMenu(smnu2, "Remove sample from HP-E list", null,0);
      addSetHP_XYEsubmenuTree(smnu3, "_E-");
      
      if(mae.useRatioDataFlag)
      { /* add checkbox to dynamically compute Cy5/Cy3 else Cy3/Cy5 */
        String sMsg= "Edit use "+
                     (mae.cfg.fluoresLbl2+"/"+ mae.cfg.fluoresLbl1)+
                     " else "+
                     (mae.cfg.fluoresLbl1+"/"+ mae.cfg.fluoresLbl2)+
                     " for each sample";
        this.sampleMenu.addSeparator();
        makeMenuItem(this.sampleMenu, sMsg, "HPswapCy53",0);
      }
      
      this.sampleMenu.addSeparator();
      
      makeMenuItem(this.sampleMenu, "List HP-X & HP-Y sample 'sets'", null,0);
      makeMenuItem(this.sampleMenu, "List HP-E sample list", null,0);
      
      makeMenuItem(this.sampleMenu, "Define HP-X 'set' class (condition) name",
                   "HP-X:setClass", 0);
      makeMenuItem(this.sampleMenu, "Define HP-Y 'set' class (condition) name",
                   "HP-Y:setClass", 0);
      this.miHPMuseHPxySets=
        makeChkBoxMenuItem(this.sampleMenu,"Use HP-X & HP-Y 'sets' else single samples [CB]",
                           null, 0, mae.useHPxySetDataFlag);
      
      addMenuStubToMenuStubList("HPmenu",this.sampleMenu);
      
    } /* no menu subtree if no data */
    
    /* [2.3] Build the "GeneClass" menu */
    /* NOTE: the event handler will try to match the name
     * to the right of "GC:" with the gene class name in
     * the mae.gct.geneClassName[] list. So make sure the name
     * is the same.
     */
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */
      makeMenuItem(this.geneClassMenu, "All genes", "GC:ALL GENES",0);
      makeMenuItem(this.geneClassMenu, "All named genes", "GC:ALL NAMED GENES",0);
      makeMenuItem(this.geneClassMenu, "ESTs similar to genes",
                   "GC:ESTs similar to genes",0);
      makeMenuItem(this.geneClassMenu, "ESTs", "GC:ESTs",0);
      makeMenuItem(this.geneClassMenu, "All genes and ESTs",
      "GC:All genes and ESTs",0);
      makeMenuItem(this.geneClassMenu, "Good genes", "GC:Good genes", 0);
      makeMenuItem(this.geneClassMenu, "Replicate genes", "GC:Replicate genes", 0);
      
         /* [TODO] Only show menus if gene sets exist in GIPO.
          * How do we test for existance of these data? add flags?.
          */
      makeMenuItem(this.geneClassMenu,"Housekeeping genes","GC:Housekeeping genes",-1);
      if(mae.cfg.calDNAname.length()>0)
        makeMenuItem(this.geneClassMenu, "Calibration DNA","GC:Calibration DNA",0);
      if(mae.cfg.yourPlateName.length()>0)
        makeMenuItem(this.geneClassMenu, "Your plates", "GC:Your plates",0);
      makeMenuItem(this.geneClassMenu, "Empty wells", "GC:Empty wells",0);
      
      if(mae.gct.nbrGC>mae.gct.nSpecialGC)
      { /* add gene class submenu */
        smnu= makeSubMenu(this.geneClassMenu, "Set Gene Class subset", null,0);
        for(int i=0; i<mae.gct.nbrGC; i++)
          makeMenuItem(smnu, mae.gct.geneClassName[i],
                       "GC:" + mae.gct.geneClassName[i], 0);
      } /* add gene class submenu */
      
      this.geneClassMenu.addSeparator();
      makeMenuItem(this.geneClassMenu, "List current Gene Class", "ListCurGC",0);
      
      addMenuStubToMenuStubList("GeneClassMenu",this.geneClassMenu);
    } /* no menu subtree if no data */
    
    /* [2.4] Build the "NORMALIZATION" menu */
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */
      this.miNRMZscoreNorm= 
         makeChkBoxMenuItem(this.normMenu, "Zscore of intensity [RB]", null, 0, 
                            mae.normByZscoreFlag);
      this.miNRMmedianNorm=
         makeChkBoxMenuItem(this.normMenu, "Median intensity [RB]", null, 0,
                            mae.normByMedianFlag);
      this.miNRMlogMedianNorm=
         makeChkBoxMenuItem(this.normMenu, "Log median intensity [RB]", null, 0, 
                            mae.normByLogMedianFlag);
      this.miNRMZscoreMeanStdDevLogNorm=
         makeChkBoxMenuItem(this.normMenu, "Zscore of log intensity, stdDev [RB]",
                            null, 0, mae.normByZscoreMeanStdDevLogFlag);
      this.miNRMZscoreMeanAbsDevLogNorm=
         makeChkBoxMenuItem(this.normMenu, "Zscore of log intensity, mnAbsDev [RB]",
                            null, 0, mae.normByZscoreMeanAbsDevLogFlag);
      
      /* Only show menu if there is calibration DNA. */
      this.miNRMCalDNAnorm= (mae.cfg.calDNAname.length()==0)
                              ? null
                              : makeChkBoxMenuItem(this.normMenu,
                                                   "By calibration DNA gene set [RB]",
                                                   null,0, mae.normByCalDNAflag);
      
      this.miNRMgeneSetNorm=
         makeChkBoxMenuItem(this.normMenu, "By 'User Normalization Gene Set' [RB]",
                            null, 0, mae.normByGeneSetFlag);
      this.miNRMhousekeepNorm=
         makeChkBoxMenuItem(this.normMenu, "By housekeeping gene set [RB]",
                            null, -1, mae.normByHousekeepGenesFlag);
      
      this.miNRMscaleToMaxIntens=
         makeChkBoxMenuItem(this.normMenu, "Scale Intensity data to 65K [RB]",
                            null,0, mae.scaleDataToMaxIntensFlag);
      this.miNRMnoNorm=
         makeChkBoxMenuItem(this.normMenu, "Unnormalized [RB]", null, 0, false);      
      
      if(mae.DBUG_GENERIC_NORM_PLUGIN)
      { /* (Analysis | Normalization | Test Generic Norm Plugin) */
        /* We add this dummy menu item here to make it easier to
         * debug "pipeline" types of MAEPlugin.Analysis.xxxx plugins.
         */
        this.normMenu.addSeparator();
        this.miNRMtestGenericNormPlugin=
           makeChkBoxMenuItem(this.normMenu, "Test Generic Norm Plugin [DBUG]",
                              null,0, mae.testGenericNormPluginFlag);
      }
      else this.miNRMtestGenericNormPlugin= null;
      
      this.normMenu.addSeparator();
      this.miNRMbkgrdCorr=
         makeChkBoxMenuItem(this.normMenu, "Use background intensity correction [CB]",
                            null,0, mae.bkgdCorrectFlag);
      
      if(!mae.useRatioDataFlag)
        this.miNRMratioMedianCorrection= null;
      else
        this.miNRMratioMedianCorrection=
           makeChkBoxMenuItem(this.normMenu, "Use ratio median correction [CB]",
                              null,0, mae.ratioMedianCorrectionFlag);
      
      this.miNRMgoodSpotsStats=
         makeChkBoxMenuItem(this.normMenu,
                            "Use per-sample Good Spots data for global array statistics [CB]",
                            null, LECBdebug, mae.useGoodSpotsForGlobalStatsFlag);
      
      addMenuStubToMenuStubList("NormMenu",this.normMenu);
    } /* no menu subtree if no data */
    
    /* [2.5] Build the "EDIT" menu */
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */
      smnu= makeSubMenu(this.editMenu, "User 'Edited Gene List'", null,0);
      
      miEMshowEditedGenes=
         makeChkBoxMenuItem(smnu, "Show 'Edited Gene List' [CB]", null,0, mae.showEGLflag);
      miEMeditNop=
         makeChkBoxMenuItem(smnu, "Don't edit [RB]", "Edit Mode: nop",
                            0, (mae.editMode==mae.EDIT_NOP));
      miEMeditAdd=
         makeChkBoxMenuItem(smnu, "Click to add gene to E.G.L. (CTRL/click) [RB]",
                           "Edit Mode: add gene", 0,(mae.editMode==mae.EDIT_ADD));
      miEMeditRmv=
         makeChkBoxMenuItem(smnu, "Click to remove gene from E.G.L. (SHIFT/click) [RB]",
                            "Edit Mode: rmv gene", 0,(mae.editMode==mae.EDIT_RMV));
      
      makeMenuItem(smnu, "Set 'Edited Gene List' to Filtered genes",
                   "Edit:CopyW2EGL", 0);
      makeMenuItem(smnu, "Clear 'Edited Gene List'", "Edit:ClearEGL",0);
      addMenuStubToMenuStubList("EditMenu:EGL",smnu);
      
      smnu= makeSubMenu(this.editMenu, "Sets of Genes", null,0);
      makeMenuItem(smnu, "List saved gene sets", "GeneSet:list", 0);
      makeMenuItem(smnu, "Save Filtered genes as gene set ","GeneSet:assWorkCL", 0);
      makeMenuItem(smnu, "Save 'Edited Gene List' as gene set","GeneSet:assEGL", 0);
      makeMenuItem(smnu, "Assign 'User Filter Gene Set'", "GeneSet:useCSfilter", 0);
      makeMenuItem(smnu, "Assign 'User Normalization Gene Set'", "GeneSet:useCSnorm", 0);
      makeMenuItem(smnu, "OR (Union) of 2 gene sets", "GeneSet:union", 0);
      makeMenuItem(smnu, "AND (Intersection) of 2 gene sets", "GeneSet:inter", 0);
      makeMenuItem(smnu, "Difference of 2 gene sets", "GeneSet:diff", 0);
      makeMenuItem(smnu, "Rename gene set ", "GeneSet:reName", 0);
      makeMenuItem(smnu, "Load gene set from disk file", "GeneSet:Load", isSAvisibleDEBUG);
      makeMenuItem(smnu, "Remove gene set ", "GeneSet:rmv", 0);
      
      addMenuStubToMenuStubList("EditMenu:GeneSet",smnu);
      
      smnu= makeSubMenu(this.editMenu, "Sets of Conditions (samples)", null,0);
      makeMenuItem(smnu, "List saved HP condition lists", "CndLst:list", 0);
      makeMenuItem(smnu, "List contents of saved HP condition list", "CndLst:listOne", 0);
      makeMenuItem(smnu, "Save HP-X 'set' as condition list", "CndLst:saveX", 0);
      makeMenuItem(smnu, "Save HP-Y 'set' as condition list", "CndLst:saveY", 0);
      makeMenuItem(smnu, "Save HP-E 'list' as condition list", "CndLst:saveE", 0);
      makeMenuItem(smnu, "Assign saved condition list to HP-X set", "CndLst:useX", 0);
      makeMenuItem(smnu, "Assign saved condition list to HP-Y set", "CndLst:useY", 0);
      makeMenuItem(smnu, "Assign saved condition list to HP-E list", "CndLst:useE", 0);
      makeMenuItem(smnu, "OR (Union) of 2 condition lists", "CndLst:union", 0);
      makeMenuItem(smnu, "AND (Intersection) of 2 condition lists", "CndLst:inter", 0);
      makeMenuItem(smnu, "Difference of 2 condition lists", "CndLst:diff", 0);
      makeMenuItem(smnu, "Rename condition list ", "CndLst:reName", 0);
      makeMenuItem(smnu, "Load HP condition list from disk file", "CndLst:Load", isSAvisibleDEBUG);
      makeMenuItem(smnu, "Remove HP condition list ", "CndLst:rmv", 0);
      
      addMenuStubToMenuStubList("EditMenu:CondList",smnu);
      
      /* Note: items in Preferences may be duplicated in other menus! */
      smnu= makeSubMenu(this.editMenu, "Preferences", null,0);
      
      /*
      makeMenuItem(smnu, "Auto scrollers", "State:adjStateScr", 0);
      smnu.addSeparator();
      */
      
      if(!mae.isAppletFlag)
      { /* only add if Stand-Alone */
        this.miFEMuseWebDB= 
           makeChkBoxMenuItem(smnu, "Use Web DB [CB]", null, 0, mae.useWebDBflag);
        makeMenuItem(smnu, "Define Web DB", "setWebDB", 0);
        this.miFEMenableFIOcache=
           makeChkBoxMenuItem(smnu, "Web DB data caching [CB]", null, 0, 
                              mae.enableFIOcachingFlag);
      }
      
      makeMenuItem(smnu, "Define HP-X 'set' class (condition) name",
                   "HP-X:setClass", 0);
      makeMenuItem(smnu, "Define HP-Y 'set' class (condition) name",
                   "HP-Y:setClass", 0);
      
      if(!mae.isAppletFlag)
      { /* only add if Stand-Alone */
        makeMenuItem(smnu, "Define DB name", "setDBname", 0);
        makeMenuItem(smnu, "Define DB title", "setDBtitle", 0);
        makeMenuItem(smnu, "Define GEO Platform ID", "setGeoPlatformID", 0);
      }
      
      addMenuStubToMenuStubList("EditMenu:Preferences",smnu);
      
      smnu.addSeparator();
      
      makeMenuItem(smnu, "Adjust all Filter threshold scrollers", "Scr:adjAll", 0);
      
      makeMenuItem(smnu, "Set max # genes in highest/lowest report or filter",
                   "RPT:maxGenesDialog", 0);
      smnu.addSeparator();
      smnu2= makeSubMenu(smnu, "Font family", null,0);
      makeMenuItem(smnu2, "Ariel", "FontFamily:Ariel", 0);
      makeMenuItem(smnu2, "Courier", "FontFamily:Courier", 0);
      makeMenuItem(smnu2, "Helvetica", "FontFamily:Helvetica", 0);
      makeMenuItem(smnu2, "MonoSpaced", "FontFamily:MonoSpaced", 0);
      makeMenuItem(smnu2, "Sans Serif", "FontFamily:SansSerif", 0);
      makeMenuItem(smnu2, "TimesRoman", "FontFamily:TimesRoman", 0);
      
      smnu2= makeSubMenu(smnu, "Font size", null,0);
      this.miPRtblFontSize12pt=
          makeChkBoxMenuItem(smnu2, "12 pt [RB]", null,0,(mae.rptFontSize.equals("12pt")));
      this.miPRtblFontSize10pt=
          makeChkBoxMenuItem(smnu2, "10 pt [RB]", null,0,(mae.rptFontSize.equals("10pt")));
      this.miPRtblFontSize8pt=
         makeChkBoxMenuItem(smnu2, "8 pt [RB]", null,0,(mae.rptFontSize.equals("8pt")));
      
      smnu.addSeparator();
      this.miEMclusterWorkingCL=
          makeChkBoxMenuItem(smnu, "Cluster Filtered Genes, else all genes [CB]",
                             null, LECBdebug,
      mae.clusterOnFilteredCLflag);      
          
      smnu.addSeparator();
      makeMenuItem(smnu, "Resize MAExplorer memory limits for the next time it is run",
                   "ResizeMem",0);    /* only for standalone*/
      
      addMenuStubToMenuStubList("EditMenu",this.editMenu);
    } /* no menu subtree if no data */
    
    
    /* [2.6] Build the "FILTER" menu */
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */
      this.miFRMgeneClassMbrFilter=
         makeChkBoxMenuItem(this.filterMenu, "Filter by GeneClass membership [CB]",
                            null, 0, mae.geneClassMbrFilterFlag);
      
      this.miFRMuseGeneSetFilter=
         makeChkBoxMenuItem(this.filterMenu,"Filter by 'User Filter Gene Set' membership [CB]",
                            null, 0, mae.useGeneSetFilterFlag);
      
      this.miFRM_EGLfilter=
         makeChkBoxMenuItem(this.filterMenu,"Filter by 'Edited Gene List' membership [CB]",
                            null, 0, mae.useEditedCLflag);
      
      this.miFRMgoodGeneListFilter=
         makeChkBoxMenuItem(this.filterMenu,"Filter by global 'good genes list' membership [CB]",
                            null, 0, mae.useGoodGeneCLflag);
      
      this.miFRMreplicateGenesFilter=
         makeChkBoxMenuItem(this.filterMenu,"Filter by genes with replicates [CB]",
                            null, 0, mae.useReplicateGenesFlag);
      
      this.filterMenu.addSeparator();
      this.miFRMhistRatioRngFilter=
         makeChkBoxMenuItem(this.filterMenu,"Filter by ratio histogram bin [CB]",
                            null, 0, mae.useRatioHistCLflag);
      
      this.miFRMhistIntensRngFilter=
         makeChkBoxMenuItem(this.filterMenu,"Filter by "+changeStr+" histogram bin [CB]",
                            null, 0, mae.useIntensHistCLflag);
      
      /* filter by disallowing negative data */
      smnu= makeSubMenu(this.filterMenu,"Filter by positive intensity data",null,0);
      this.miFRMusePosQuantDataFlag=
         makeChkBoxMenuItem(smnu, "Filter by spots with positive intensity values [CB]",
                            null, 0, mae.usePosQuantDataFlag /* mae.allowNegQuantDataFlag*/ );
      smnu2= makeSubMenu(smnu, "Check spots for positive values mode", null,0);
      this.miFRMposQuantModeMS= 
         makeChkBoxMenuItem(smnu2, "Current HP [RB]", null,0,
                            (mae.posQuantTestMode==mae.SS_MODE_MS));
      
      this.miFRMposQuantModeXY= 
         makeChkBoxMenuItem(smnu2, "HP-X & HP-Y [RB]", null,0,
                            (mae.posQuantTestMode==mae.SS_MODE_XY));
      this.miFRMposQuantModeXYsets=
         makeChkBoxMenuItem(smnu2, "HP-X & HP-Y 'sets' [RB]", null,0,
                            (mae.posQuantTestMode==mae.SS_MODE_XANDY_SETS));
      this.miFRMposQuantModeE= 
         makeChkBoxMenuItem(smnu2, "HP-E [RB]", null,0,
                            (mae.posQuantTestMode==mae.SS_MODE_ELIST));      
      
      this.miFRMOnlyGenesWithNonZeroDensityFlag=
         makeChkBoxMenuItem(this.filterMenu,"Filter by genes with non-zero intensity [CB]",
                            null, 0, mae.useOnlyGenesWithNonZeroDensityFlag);
        
      /* filter by disallowing negative data */
      smnu= makeSubMenu(this.filterMenu, "Filter by per-sample Good Spot data", null,0);
      this.miFRMuseGoodSpotDataFlag= 
         makeChkBoxMenuItem(smnu, "Filter by spots with Good Spot values [RB]",
                            null, 0, mae.useGoodSpotDataFlag);
      smnu2= makeSubMenu(smnu, "Check spots for Good Spot mode", null,0);
      this.miFRMgoodSpotModeMS=
         makeChkBoxMenuItem(smnu2, "Current HP [RB]", null,0,
                            (mae.goodSpotTestMode==mae.SS_MODE_MS));
      this.miFRMgoodSpotModeXY=
         makeChkBoxMenuItem(smnu2, "HP-X & HP-Y [RB]", null,0,
                            (mae.goodSpotTestMode==mae.SS_MODE_XY));
      this.miFRMgoodSpotModeXORYsets=
         makeChkBoxMenuItem(smnu2, "HP-X or HP-Y 'sets' [RB]", null,0,
                            (mae.goodSpotTestMode==mae.SS_MODE_XORY_SETS));
      this.miFRMgoodSpotModeXANDYsets=
         makeChkBoxMenuItem(smnu2, "HP-X and HP-Y 'sets' [RB]",null,0,
                            (mae.goodSpotTestMode==mae.SS_MODE_XANDY_SETS));
      this.miFRMgoodSpotModeE= 
         makeChkBoxMenuItem(smnu2, "HP-E [RB]", null,0,
                           (mae.goodSpotTestMode==mae.SS_MODE_ELIST));
      
      /* [TODO] Problem. Need to test data BEFORE it is actually read in! So
       * force it on for now!
       */
      if(mae.CONSOLE_FLAG /* mae.ms!=null && mae.ms.hasDetValueSpotDataFlag */ )
      { /* filter by disallowing spots with bad correlation coeff data */
        smnu= makeSubMenu(this.filterMenu, 
                          "Filter by per-sample Spot Detection Value data",
                          null,0);
        this.miFRMuseDetValueSpotDataFlag=
           makeChkBoxMenuItem(smnu,"Filter by spots by per-sample Spot Detection Value data [CB]",
                              null, 0, mae.useDetValueSpotDataFlag);
        smnu2= makeSubMenu(smnu,"Check spots for Spot Detection Data mode [CB]",null,0);
        this.miFRMdetValueSpotModeMS=
           makeChkBoxMenuItem(smnu2, "Current HP [RB]", null,0,
                              (mae.detValueSpotTestMode==mae.SS_MODE_MS));
        this.miFRMdetValueSpotModeXY=
           makeChkBoxMenuItem(smnu2, "HP-X & HP-Y [RB]", null,0,
                              (mae.detValueSpotTestMode==mae.SS_MODE_XY));
        this.miFRMdetValueSpotModeXANDYsets=
           makeChkBoxMenuItem(smnu2, "HP-X and HP-Y 'sets' [RB]", null,0,
                              (mae.detValueSpotTestMode==mae.SS_MODE_XANDY_SETS));
        this.miFRMdetValueSpotModeE=
           makeChkBoxMenuItem(smnu2, "HP-E [RB]", null,0,
                              (mae.detValueSpotTestMode==mae.SS_MODE_ELIST));
      } /* filter by disallowing spots with bad correlation coeff data */
      //else
      //this.miFRMuseDetValueSpotDataFlag= null;
      this.filterMenu.addSeparator();
      
      /* The "spot intensity [SI1:SI2] sliders" menu tree */
      /* only enable if computing (min/maxRawVal) correctly */
      smnu= makeSubMenu(this.filterMenu,"Filter by spot intensity [SI1:SI2] sliders",
                        null,0);
      this.miFRMthrSpotIntens=
         makeChkBoxMenuItem(smnu, "Use spot intensity [SI1:SI2] sliders [CB]",
                            null, 0, mae.spotIntensFilterFlag);
      smnu.addSeparator();
      this.miFRMspotIntensRngIn=
         makeChkBoxMenuItem(smnu, "Inside range [RB]", null,0,
                            (mae.spotIntensRangeMode==mae.RANGE_INSIDE));
      this.miFRMspotIntensRngOut=
         makeChkBoxMenuItem(smnu, "Outside range [RB]", null,0,
                            (mae.spotIntensRangeMode==mae.RANGE_OUTSIDE));
      smnu.addSeparator();
      smnu2= makeSubMenu(smnu, "Use data mode", null,0);
      this.miFRMspotIntensModeMS=
         makeChkBoxMenuItem(smnu2, "Current HP [RB]", null,0,
                            (mae.spotIntensTestMode==mae.SS_MODE_MS));
      
      this.miFRMspotIntensModeXY=
         makeChkBoxMenuItem(smnu2, "HP-X or HP-Y [RB]", null,0,
                            (mae.spotIntensTestMode==mae.SS_MODE_XY));
      this.miFRMspotIntensModeXsets=
         makeChkBoxMenuItem(smnu2, "HP-X 'sets' [RB]", null,0,
                            (mae.spotIntensTestMode==mae.SS_MODE_XSET));
      this.miFRMspotIntensModeYsets=
         makeChkBoxMenuItem(smnu2, "HP-Y 'sets'", null,0,
                            (mae.spotIntensTestMode==mae.SS_MODE_YSET));
      this.miFRMspotIntensModeXORYsets=
         makeChkBoxMenuItem(smnu2, "HP-X or HP-Y 'sets' [RB]", null,0,
                            (mae.spotIntensTestMode==mae.SS_MODE_XORY_SETS));
      this.miFRMspotIntensModeXANDYsets=
         makeChkBoxMenuItem(smnu2, "HP-X and HP-Y 'sets' [RB]", null,0,
                            (mae.spotIntensTestMode==mae.SS_MODE_XANDY_SETS));
      this.miFRMspotIntensModeE=
         makeChkBoxMenuItem(smnu2, "HP-E [RB]", null,0,
                            (mae.spotIntensTestMode==mae.SS_MODE_ELIST));
      
      smnu2= makeSubMenu(smnu, "Compare channels meeting range", null,0);
      this.miFRMspotIntensCompareModeALL=
         makeChkBoxMenuItem(smnu2, "ALL channels [RB]", null,0,
                            (mae.spotIntensCompareMode==mae.COMPARE_ALL));
      this.miFRMspotIntensCompareModeANY=
         makeChkBoxMenuItem(smnu2, "ANY channels [RB]", null,0,
                            (mae.spotIntensCompareMode==mae.COMPARE_ANY));
      this.miFRMspotIntensCompareModeAT_MOST=
         makeChkBoxMenuItem(smnu2, "AT MOST channels [RB]", null, 0,
                            (mae.spotIntensCompareMode==mae.COMPARE_AT_MOST));
      this.miFRMspotIntensCompareModeAT_LEAST=
         makeChkBoxMenuItem(smnu2, "AT LEAST channels [RB]", null, 0,
                            (mae.spotIntensCompareMode==mae.COMPARE_AT_LEAST));
      this.miFRMspotIntensCompareModePRODUCT=
         makeChkBoxMenuItem(smnu2, "PRODUCT of channels [RB]", null,0,
                            (mae.spotIntensCompareMode==mae.COMPARE_PRODUCT));
      this.miFRMspotIntensCompareModeSUM=
         makeChkBoxMenuItem(smnu2, "SUM of channels [RB]", null,0,
                            (mae.spotIntensCompareMode==mae.COMPARE_SUM));
      
      /* The "spot intensity/(Cy3/Cy5) [I1:I2] sliders" menu tree */
      smnu= makeSubMenu(this.filterMenu,"Filter by "+changeStr+" [I1:I2] sliders", 
                        null,0);
      this.miFRMthrGray=
         makeChkBoxMenuItem(smnu,"Use "+changeStr+" [I1:I2] sliders [CB]",
                            null, 0, mae.intensFilterFlag);
      smnu.addSeparator();
      this.miFRMgrayRngIn=
         makeChkBoxMenuItem(smnu, "Inside range [RB]", null,0,
                           (mae.sampleIntensityRangeMode==mae.RANGE_INSIDE));
      this.miFRMgrayRngOut=
         makeChkBoxMenuItem(smnu, "Outside range [RB]", null,0,
                            (mae.sampleIntensityRangeMode==mae.RANGE_OUTSIDE));
      
      /* The "spot ratio/Zscore [R1:R2] sliders" menu tree */
      smnu= makeSubMenu(this.filterMenu,"Filter by ratio or Zdiff sliders", null,0);
      this.miFRMthrRatio=
         makeChkBoxMenuItem(smnu,"Use ratio [R1:R2] or Zdiff [Z1:Z2] sliders [CB]",
                            null, 0,
      mae.ratioFilterFlag);
      smnu.addSeparator();
      this.miFRMratioRngIn=
         makeChkBoxMenuItem(smnu, "Inside range [RB]", null,0,
                            (mae.ratioRangeMode==mae.RANGE_INSIDE));
      this.miFRMratioRngOut=
         makeChkBoxMenuItem(smnu, "Outside range [RB]", null,0,
                            (mae.ratioRangeMode==mae.RANGE_OUTSIDE));
      
      /* If Cy3/Cy5 data, then add "Cy3/Cy5 HP-X ratio/Zscore [CR1:CR2] sliders" menu tree */
      if(mae.useRatioDataFlag)
      { /* only if Cy3/Cy5 data */
        smnu= makeSubMenu(this.filterMenu,
                          "Filter by Cy3/Cy5 HP-X ratio or Zdiff sliders [CB]", 
                          null,0);
        this.miFRMthrCy3Cy5Ratio=
           makeChkBoxMenuItem(smnu, "Use ratio [CR1:CR2] or Zdiff [CZ1:CZ2] sliders [CB]",
                              null, 0, mae.ratioCy3Cy5FilterFlag);
        smnu.addSeparator();
        this.miFRMCy3Cy5RatioRngIn=
           makeChkBoxMenuItem(smnu, "Inside range [RB]", null,0,
                              (mae.ratioCy3Cy5RangeMode==mae.RANGE_INSIDE));
        this.miFRMCy3Cy5RatioRngOut=
           makeChkBoxMenuItem(smnu, "Outside range [RB]", null,0,
                              (mae.ratioCy3Cy5RangeMode==mae.RANGE_OUTSIDE));
      } /* only if Cy3/Cy5 data */
      
      smnu= makeSubMenu(this.filterMenu, "Filter by Spot CV", null,0);
      this.miFRMspotCVfilter=
         makeChkBoxMenuItem(smnu, "Use spot [CV] slider [CB]", null, 0,
                            mae.useSpotCVfilterFlag);
      
      smnu2= makeSubMenu(smnu, "Spot CV filter mode", null,0);
      if(mae.gangSpotFlag && !mae.useRatioDataFlag)
        this.miFRMcvModeMS=
           makeChkBoxMenuItem(smnu2, "Current HP [RB]", null,0,
                              (mae.cvTestMode==mae.SS_MODE_MS));
      
      if(mae.gangSpotFlag && !mae.useRatioDataFlag)
        this.miFRMcvModeXY=
           makeChkBoxMenuItem(smnu2, "HP-X or HP-Y [RB]", null,0,
                              (mae.cvTestMode==mae.SS_MODE_XY));
      this.miFRMcvModeXset=
         makeChkBoxMenuItem(smnu2, "HP-X 'set' [RB]", null,0,
                            (mae.cvTestMode==mae.SS_MODE_XSET));
      this.miFRMcvModeYset=
      makeChkBoxMenuItem(smnu2, "HP-Y 'set' [RB]", null,0,
                         (mae.cvTestMode==mae.SS_MODE_YSET));
      this.miFRMcvModeXORYsets=
      makeChkBoxMenuItem(smnu2, "HP-X or HP-Y 'sets' [RB]", null,0,
                         (mae.cvTestMode==mae.SS_MODE_XORY_SETS));
      this.miFRMcvModeXANDYsets=
      makeChkBoxMenuItem(smnu2, "HP-X and HP-Y 'sets' [RB]", null,0,
                         (mae.cvTestMode==mae.SS_MODE_XANDY_SETS));
      this.miFRMcvModeE=
      makeChkBoxMenuItem(smnu2, "HP-E 'list' [RB]", null,0,
                         (mae.cvTestMode==mae.SS_MODE_ELIST));
      
      this.miFRMuseCVmeanElseMax=
      makeChkBoxMenuItem(smnu, "Use Mean else Max of HP CVs [CB]", null,0,
                         mae.useCVmeanElseMaxFlag);
      
      this.filterMenu.addSeparator();
      
      if(mae.gangSpotFlag && !mae.useRatioDataFlag)
        this.miFRMtTestXYfilter=
           makeChkBoxMenuItem(this.filterMenu,
                              "Filter by HP-X,HP-Y t-Test [p-Value] slider [RB]",
                              null, 0, mae.tTestXYfilterFlag);
      
      this.miFRMtTestXYsetsFilter=
         makeChkBoxMenuItem(this.filterMenu,
                            "Filter by HP-X,HP-Y 'sets' t-Test [p-Value] slider [RB]",
                            null, 0, mae.tTestXYsetsFilterFlag);
      
      this.miFRMksTestXYsetsFilter=
         makeChkBoxMenuItem(this.filterMenu,
                            "Filter by HP-X,HP-Y 'sets' Kolmogorov-Smirnov test [p-Value] slider [RB]",
                            null, 0, mae.KS_TestXYsetsFilterFlag);
      
      this.miFRMfTestOCLFilter=
         makeChkBoxMenuItem(this.filterMenu,
                            "Filter by current Ordered Cond. List (OCL) F-Test [p-Value] slider [RB]",
                            null, 0, mae.F_TestOCLFilterFlag);    
      
      this.filterMenu.addSeparator();
      this.miFRMclusterHP_Efilter=
         makeChkBoxMenuItem(this.filterMenu,
                            "Filter by HP-E clustering [Cluster Dist] slider [CB]",
                            null, 0, mae.clusterHP_EfilterFlag);
      
      this.miFRMuseDiffFilter=
         makeChkBoxMenuItem(this.filterMenu,
                            "Filter by Diff(HP-X,HP-Y) [Abs.Diff.] slider [CB]",
                            null, 0, mae.useDiffFilterFlag);
      
      this.filterMenu.addSeparator();
      this.miFRMhighXYratioFilter=
         makeChkBoxMenuItem(this.filterMenu,
                            "Filter genes with highest X/Y ratio or X-Y Zdiff [CB]",
                            null, 0, mae.useHighRatiosFilterFlag);
      
      this.miFRMlowXYratioFilter=
         makeChkBoxMenuItem(this.filterMenu,
                            "Filter genes with lowest X/Y ratio or X-Y Zdiff [CB]",
                            null, 0, mae.useLowRatiosFilterFlag);
      
      addMenuStubToMenuStubList("FilterMenu",this.filterMenu);
    } /* no menu subtree if no data */
    
    /* [2.7] Build the "PLOT" menu. */
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */
      smnu= makeSubMenu(this.plotMenu, "Show Microarray", null,0);
      this.miPMplotPseudoImg=
         makeChkBoxMenuItem(smnu, "Pseudograyscale intensity [RB]",
                            null, 0, (mae.plotImageMode==mae.PLOT_PSEUDOIMG));
      this.miPMplotPseudoHP_XY_RYGImg=
         makeChkBoxMenuItem(smnu,
                            "Pseudocolor Red(X)-Yellow-Green(Y) HP-X/Y ratio or Zdiff [RB]",
                            null, 0, 
                            (mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_RYG_IMG));
      this.miPMplotPseudoHP_F1F2_RYGImg=
         makeChkBoxMenuItem(smnu,
                            "Pseudocolor Red("+sf2+")-Yellow-Green("+sf1+") "+
                            sf1+"/"+sf2+" ratio or Zdiff [RB]",
                            null, 0,(mae.plotImageMode==mae.PLOT_PSEUDO_F1F2_RYG_IMG));
      this.miPMplotPseudoHP_XYImg=
         makeChkBoxMenuItem(smnu, "Pseudocolor HP-X/Y ratio or Zdiff [RB]",
                            null, 0, (mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_IMG));
      if(mae.gangSpotFlag)
        this.miPMplotPseudoF1F2Img=
           makeChkBoxMenuItem(smnu,"Pseudocolor "+sf1+"/"+sf2+" ratio or Zdiff [RB]",
                              null, 0, (mae.plotImageMode==mae.PLOT_PSEUDO_F1F2_IMG));
      this.miPMplotPseudoHP_XY_pValueImg=
         makeChkBoxMenuItem(smnu, "Pseudocolor (HP-X,HP-Y) 'sets' p-value [RB]",
                            null, 0,
                            (mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_P_VALUE_IMG));
      if(mae.DBUG_CV_DISPLAY_EPLIST)
      this.miPMplotPseudoHP_EP_CV_valueImg=
         makeChkBoxMenuItem(smnu, 
                            "Pseudocolor  HP-EP 'list' CV (Coefficient of Variation) [RB]",
                            null, 0, 
                            (mae.plotImageMode==mae.PLOT_PSEUDO_HP_EP_CV_VALUE_IMG));
        
      this.miPMflickerXY=
         makeChkBoxMenuItem(smnu, "Flicker HP-X & HP-Y Pseudograyscale images [RB]",
                            null, 0, mae.flickerXYflag);
      makeMenuItem(smnu, "Original image", "Plot:origImg", 0);
      
      smnu.addSeparator();
      
      this.miPMdualXYpseudoImg=
      makeChkBoxMenuItem(smnu, "Use dual HP-X & HP-Y Pseudoimage [CB]", null, 0,
                         mae.dualXYpseudoPlotFlag);
      
      this.miPMlowRangeZoom=
         makeChkBoxMenuItem(smnu,
                            "Scale pseudoarray image by 1/100 to zoom low-range values [CB]",
                            null, 0, mae.lowRangeZoomFlag);
      
      if(!mae.isAppletFlag)
        makeMenuItem(smnu, "SaveAs GIF", "Plot:SaveAs",0);
      
      addMenuStubToMenuStubList("PlotMenu:PseudoArray",smnu);
      
      smnu= makeSubMenu(this.plotMenu, "Scatter Plots", null, 0);
      makeMenuItem(smnu, "HP-X vs HP-Y "+changeStr, "Plot:HP_XYscatPlot", 0);
      if(mae.gangSpotFlag || mae.useRatioDataFlag)
      {
        makeMenuItem(smnu, "HP "+sf1+" vs "+sf2+" intensity",
                     "Plot:F1F2scatterPlot", 0);
        makeMenuItem(smnu, "M vs A plot of current sample",
                     "Plot:MvsA-F1F2ScatrPlot", mae.LECBdebug);        
      }
      
      if(mae.useRatioDataFlag /* && mae.LECBdebug==0*/ )
      { /* compare Cy3, Cy5 channels across arrays */
        smnu.addSeparator();
        makeMenuItem(smnu, "HP-X("+sf1+") vs HP-Y("+sf1+") intensity",
                     "Plot:Xcy3VsYcy3ScatrPlot",0);
        makeMenuItem(smnu, "HP-X("+sf2+") vs HP-Y("+sf2+") intensity",
                     "Plot:Xcy5VsYcy5ScatrPlot",0);
        makeMenuItem(smnu, "HP-X("+sf1+") vs HP-Y("+sf2+") intensity",
                     "Plot:Xcy3VsYcy5ScatrPlot",0);
        makeMenuItem(smnu, "HP-X("+sf2+") vs HP-Y("+sf1+") intensity",
                     "Plot:Xcy5VsYcy3ScatrPlot",0);
      } /* compare Cy3, Cy5 channels across arrays */
      
      addMenuStubToMenuStubList("PlotMenu:ScatterPlots",smnu);
      
      smnu= makeSubMenu(this.plotMenu, "Histograms", null, 0);
      makeMenuItem(smnu, "HP-XY ratio or Zdiffs", "Plot:HP_XYratioHist", 0);
      makeMenuItem(smnu, "HP-XY 'sets' ratio or Zdiff","Plot:HP_XYsetsRatioHist",0);
      if(mae.gangSpotFlag /* || mae.useRatioDataFlag */)
        makeMenuItem(smnu, sf1+sf2+" ratio or Zdiff", "Plot:F1F2ratioHist", 0);
      makeMenuItem(smnu, "HP "+changeStr, "Plot:intensHist", 0);
      
      addMenuStubToMenuStubList("PlotMenu:Histogram",smnu);
      
      smnu= makeSubMenu(this.plotMenu, "Expression profile plots", null, 0);
      makeMenuItem(smnu, "Display a gene's expr. profile for HP-E","Plot:geneEP",0);
      makeMenuItem(smnu, "Display Filtered genes' expr. profiles for HP-E",
                   "Plot:FltrGeneEP", 0);
      smnu.addSeparator();
      this.miPEPlistType=
         makeChkBoxMenuItem(smnu, "Use EP overlay else EP list [CB]", null, 0 /* LECBdebug*/,
                            mae.useEPoverlayFlag);
      
      addMenuStubToMenuStubList("PlotMenu:EPplots",smnu);
      
      addMenuStubToMenuStubList("PlotMenu",this.plotMenu);
      
      this.miCLMfindSimGenesDisp=
         makeChkBoxMenuItem(this.clusterMenu,
                            "Cluster genes with expression profiles similar to current gene [RB]",
                            null /* "Plot:findSimilarGenesDisplay" */, 0,
                            mae.useSimGeneClusterDispFlag);
      this.miCLMsimGeneCountsDisp=
         makeChkBoxMenuItem(this.clusterMenu,
                            "Cluster counts of similar Filtered genes by expression profiles [RB]",
                            null /* "Plot:similarGeneCountsDisplay" */, 0,
                            mae.useClusterCountsDispFlag);
      
      this.miCLMdispKmeansNodes=
         makeChkBoxMenuItem(this.clusterMenu,
                            "K-means clustering of gene expression profiles [RB]",
                            null, 0, mae.useKmeansClusterCntsDispFlag);
      
      smnu= makeSubMenu(this.clusterMenu,
                        "Hierarchical clustering of expression profiles",null, 0);
      this.miCLMhierClusterDisp=
         makeChkBoxMenuItem(smnu,"Display ClusterGram of gene expr profiles [RB]",
                            null, 0, mae.useHierClusterDispFlag);
      smnu.addSeparator();
      this.miCLHCMavgPGMALnk=
         makeChkBoxMenuItem(smnu, "Use avg-arithmetic-linkage [RB]", null, LECBdebug,
                            (mae.hierClustMode==mae.HIER_CLUST_PGMA_LNKG));
      
      this.miCLHCMavgPGMCLnk=
         makeChkBoxMenuItem(smnu, "Use avg-centroid-linkage [RB]", null, 0,
                            (mae.hierClustMode==mae.HIER_CLUST_PGMC_LNKG));
      
      this.miCLHCMnextMinLnk=
         makeChkBoxMenuItem(smnu, "Use next-min-linkage [RB]", null, 0,
                           (mae.hierClustMode==mae.HIER_CLUST_NEXT_MIN_LNKG));
      
      this.miCLHCMuseClusterDistCache=
         makeChkBoxMenuItem(smnu, "Use cluster-distance matrix cache [CB]",
                            null, 0, mae.useClusterDistCacheFlag);
      this.miCLHCMuseShortClusterDistCache=
         makeChkBoxMenuItem(smnu, "Use short else float cluster-distance matrix cache [CB]",
                            null, 0, mae.useShortClusterDistCacheFlag);
      
      this.miCLHCMunWtAvg=
         makeChkBoxMenuItem(smnu, "Use unweighted else weighted average [CB]", null, 0,
                            mae.hierClustUnWtAvgFlag);
      
      /* DEPRICATED in code (as of V.0.96.15) - add these as MAEPlugins ...
      makeMenuItem(this.clusterMenu, "S.O.M. gene clusters by expr profiles [RB]",
                   "Plot:dispSOMclstr", -1);
      makeMenuItem(this.clusterMenu, 
                   "Principal Component Analysis of genes by expr profiles [RB]",
                   "Plot:dispPCAclstr", -1);
      makeMenuItem(this.clusterMenu, 
                   "Multi-Dimensional Scaling of genes by expr profiles [RB]",
                   "Plot:dispMDSclstr", -1);
      makeMenuItem(this.clusterMenu, 
                   "Clusters of (HP-E) samples as fct of Filtered genes [RB]",
                   "Plot:dispHPclstr", -1);
       */
      
      addMenuStubToMenuStubList("PlotMenu:Cluster",this.clusterMenu);
      
      this.clusterMenu.addSeparator();
      
      this.miCLMuseCorrCoeffDist=
         makeChkBoxMenuItem(this.clusterMenu,
                            "Use correlation-coefficient else Euclidian-distance [CB]",
                            null, 0, mae.useCorrCoeffFlag);
      this.miCLMuseLSQmagNorm=
         makeChkBoxMenuItem(this.clusterMenu,
                            "Scale EP vector by max magnitude prior to clustering [CB]",
                            null, 0, mae.useLSQmagNormFlag);
      this.miCLMnormHP=
         makeChkBoxMenuItem(this.clusterMenu,
                            "Normalize by HP-X sample else HP max intensities [CB]",
                            null, 0, mae.normHCbyRatioHPflag);
      this.miCLMuseMedianKmeans=
         makeChkBoxMenuItem(this.clusterMenu,
                            "Use median instead of mean for K-means clustering [CB]",
                            null, 0, mae.useMedianForKmeansClusteringFlag);
      
      addMenuStubToMenuStubList("PlotMenu:ClusterFlags",this.clusterMenu);
    } /* no menu subtree if no data */
    
    /* [2.8] Build the "Report" menu */
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */
      smnu= makeSubMenu(this.reportMenu, "Array reports", null, 0);
      makeMenuItem(smnu, "Hybridized samples info", "RPT:HPinfo", 0);
      makeMenuItem(smnu, "Hybridized samples Web links", "RPT:HPwebLinks",0);
      if(mae.infoDB!=null)
        makeMenuItem(smnu, "Extra Info on Samples", "RPT:xtraHPinfo",0);
      if(isSAvisible==0)
        makeMenuItem(smnu, "MAE Projects DB", "RPT:MAEprjDB",0);
      makeMenuItem(smnu, "Samples vs Samples correlation coefficients",
                   "RPT:HP_HPcorr", 0);
      makeMenuItem(smnu, "'Calibration DNA' summary", "RPT:CalDNAstat", 0);
      makeMenuItem(smnu,"Samples mean & variance summary", "RPT:HPmeanVarStat",0);
      
      addMenuStubToMenuStubList("ReportMenu:Samples",smnu);
      
      smnu= makeSubMenu(this.reportMenu, "Gene reports", null, 0);
      makeMenuItem(smnu, "All named genes", "RPT:allNamedC",0);
      makeMenuItem(smnu, "Genes in 'Edited Gene List'", "RPT:EGLgenes",0);
      makeMenuItem(smnu, "Genes in 'Normalization Gene List'", "RPT:NormGeneSet",0);
      makeMenuItem(smnu, "Genes in GeneClass", "RPT:GeneClassC",0);
      
      smnu2= makeSubMenu(smnu, "Filtered gene reports", null,0);
      makeMenuItem(smnu2, "Genes passing the Filter","RPT:filterC",0);
      makeMenuItem(smnu2, "Highest HP-XY ratios or Zdiffs", "RPT:highHP_XY",0);
      makeMenuItem(smnu2, "Lowest HP-XY ratios or Zdiffs", "RPT:lowHP_XY",0);
      if(mae.gangSpotFlag || mae.useRatioDataFlag)
      {
        makeMenuItem(smnu2, "Highest "+sf1+sf2+" ratios or Zdiffs",
                     "RPT:highHP_F1F2",0);
        makeMenuItem(smnu2, "Lowest "+sf1+sf2+" ratios or Zdiffs",
                     "RPT:lowHP_F1F2",0);
      }
      makeMenuItem(smnu2, "Expression profiles of Filtered genes",
                   "RPT:EPFilter",0);
      makeMenuItem(smnu2, "HP-XY 'set' statistics of Filtered genes",
                  "RPT:HP-XY-setStat",0);
      makeMenuItem(smnu2, "Ordered Condition List statistics of Filtered genes",
                  "RPT:OCLstat",0);
      
      addMenuStubToMenuStubList("ReportMenu:Genes",smnu);
      
      this.reportMenu.addSeparator();
      smnu= makeSubMenu(this.reportMenu, "Table format", null,0);
      this.miTMtblFmtClickable=
         makeChkBoxMenuItem(smnu, "Spreadsheet [RB]", null, 0,
                           (mae.tblFmtMode==mae.RPT_FMT_DYN));
      this.miTMtblFmtTabDelim=
         makeChkBoxMenuItem(smnu, "Tab delimited - suitable for export [RB]",
                            null,0, (mae.tblFmtMode==mae.RPT_FMT_TAB_DELIM));
      smnu.addSeparator();
      
      makeMenuItem(smnu, "Set max # genes in highest/lowest report or filter",
                   "RPT:maxGenesDialog", 0);
      
      this.miTMtblFmtAddEPdata=
         makeChkBoxMenuItem(smnu, "Add EP data to Gene-Reports [CB]", null, 0, 
                            mae.addExprProfileFlag);
      this.miTMtblFmtUseRawEPdata=
         makeChkBoxMenuItem(smnu, "Use EP data in Gene-Reports [CB]", null, 0,
                            mae.useEPrawIntensValFlag);
      this.miTMtblFmtAddHP_XYsetStat=
         makeChkBoxMenuItem(smnu,"Add HP-X/-Y 'set' statistics data to Gene-Reports [CB]",
                            null, 0, mae.addHP_XYstatFlag);
      this.miTMtblFmtAddOCLstatistics=
        makeChkBoxMenuItem(smnu,"Add Ordered Condition List statistics data to Gene-Reports [CB]",
                           null, 0, mae.addOCLstatFlag);
      
      smnu= makeSubMenu(this.reportMenu, "Table font size", null,0);
      this.miTMtblFontSize12pt=
         makeChkBoxMenuItem(smnu, "12 pt [RB]", null,0,(mae.rptFontSize.equals("12pt")));
      this.miTMtblFontSize10pt=
         makeChkBoxMenuItem(smnu, "10 pt [RB]", null,0,(mae.rptFontSize.equals("10pt")));
      this.miTMtblFontSize8pt=
         makeChkBoxMenuItem(smnu, "8 pt [RB]", null,0,(mae.rptFontSize.equals("8pt")));
      
      addMenuStubToMenuStubList("ReportMenu",this.reportMenu);
    } /* no menu subtree if no data */
    
    /* [2.9] Build the "VIEW" menu */
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */
      this.miVMshowEditedGenes=
         makeChkBoxMenuItem(this.viewMenu, "Show 'Edited Gene List' [CB]", null,0,
                            mae.showEGLflag);
      
      this.viewMenu.addSeparator();
      boolean
        hasCloneID= (mae.gipo.cloneIdIdx!=-1),
        hasLocusID= (mae.gipo.LocusLinkIdIdx!=-1),
        hasSwissProt= (mae.gipo.SwissProtIdx!=-1),
        has_dbEST= (mae.gipo.dbESTid3Idx!=-1 || mae.gipo.dbESTid5Idx!=-1),
        hasUniGeneID= (mae.gipo.Unigene_cluster_IDidx!=-1),
        hasOmimID= (mae.gipo.OmimIdIdx!=-1),
        hasGenBank= (mae.gipo.GenBankAccIdx!=-1 ||
                     mae.gipo.GenBankAcc3Idx!=-1 || 
                     mae.gipo.GenBankAcc5Idx!=-1);
      
      if(/* hasCloneID  || */ hasGenBank)
        this.miVMgenBankViewer=
           makeChkBoxMenuItem(this.viewMenu,
                              "Enable display current gene in popup GenBank Web Browser [CB]",
                              null,0, mae.genBankViewerFlag);
      
      if(has_dbEST)
        this.miVMdbESTviewer=
           makeChkBoxMenuItem(this.viewMenu,
                              "Enable display current gene in popup dbEst Web Browser [CB]",
                               null, 0, mae.dbESTviewerFlag);
      
      if(hasCloneID /* || hasGenBank */ || hasUniGeneID)
        this.miVMuniGeneViewer=
           makeChkBoxMenuItem(this.viewMenu,
                              "Enable display current gene in popup UniGene Web Browser [CB]",
                              null,0, mae.uniGeneViewerFlag);
      
      if(hasCloneID)
        this.miVMmAdbViewer=
           makeChkBoxMenuItem(this.viewMenu,
                              "Enable display current gene in popup mAdb Web Browser [CB]",
                              null,0, mae.mAdbViewerFlag);
      
      if(hasLocusID || hasGenBank)
        this.miVMlocusLinkViewer=
            makeChkBoxMenuItem(this.viewMenu,
                               "Enable display current gene in popup LocusLink Web Browser [CB]",
                               null,0, mae.locusLinkViewerFlag);
      if(hasOmimID)
        this.miVMomimViewer=
           makeChkBoxMenuItem(this.viewMenu,
                              "Enable display current gene in popup OMIM Web Browser [CB]",
                               null, 0, mae.omimViewerFlag);
      
      if(mae.DBUG_MEDMINER_FLAG && hasCloneID)
        this.miVMmedMinerViewer=
           makeChkBoxMenuItem(this.viewMenu,
                              "Enable display current gene in popup MedMiner Web Browser [CB]",
                              null,0, mae.medMinerViewerFlag);
      
      if(hasSwissProt)
        this.miVMswissProtViewer=
           makeChkBoxMenuItem(this.viewMenu,
                              "Enable display current gene in popup Swiss-Prot Web Browser [CB]",
                              null,0, mae.swissProtViewerFlag);
      
      if(hasSwissProt)
        this.miVMpirViewer=
           makeChkBoxMenuItem(this.viewMenu,
                              "Enable display current gene in popup PIR Web Browser [CB]",
                              null,0, mae.pirViewerFlag);
      
      /* [TODO] Add menu entries for GenomicMenu[i] entries and setup
       * state and event handler. NOTE: we allocate the
       * CheckboxMenuItem array and state flag arrays here as well.
       */
      if(mae.DBUG_GENOMIC_URLS_FLAG && cfg.nGenomicMenus>0)
      { /* add generic Genomic menu entries */
        this.miVMgenomicViewer= new CheckboxMenuItem[cfg.nGenomicMenus];
        mae.genomicViewerFlag= new boolean[cfg.nGenomicMenus];
        for(int i=0;i<cfg.nGenomicMenus;i++)
        { /* add entry for PARAM menu entries "GenomicMenu"+i */
          String sMenuStr= "Enable display current gene in popup "+
                           cfg.sGenomicMenu[i]+" Web Browser [CB]";
          this.miVMgenomicViewer[i]=
             makeChkBoxMenuItem(this.viewMenu, sMenuStr, null,0,
                                mae.genomicViewerFlag[i]);
        } /* add entry for PARAM menu entries "GenomicMenu"+i */
      } /* add generic Genomic menu entries */
      
      this.viewMenu.addSeparator();
      
      this.miVMshowFilteredSpots=
         makeChkBoxMenuItem(this.viewMenu, "Show Filtered spots in array [CB]",
                            null, 0, mae.viewFilteredSpotsFlag);
      
      if(mae.cfg.maxFIELDS>1)
        this.miVMGang=
           makeChkBoxMenuItem(this.viewMenu, "Gang F1-F2 scrolling [CB]",
                              null,0, mae.gangSpotFlag);
      
      this.miVMuseMouseOver=
         makeChkBoxMenuItem(this.viewMenu, "Show mouse-over info [CB]",
                            null,0, mae.useMouseOverFlag);
      
      this.miVMPresentView=
         makeChkBoxMenuItem(this.viewMenu, "Presentation view mode [CB]",
                            null,0, mae.presentViewFlag);
      
      this.miVMuseDichromasy=
         makeChkBoxMenuItem(this.viewMenu, "Color scheme (red-green) or dichromasy [CB]",
                            null,0, mae.useDichromasyFlag);
      
      this.miVMshowMsgLog=
         makeChkBoxMenuItem(this.viewMenu, "Show log of messages [CB]",
                            null, 0,
                            ((Util.historyTextFrame==null)
                              ? false : Util.historyTextFrame.isVisibleFlag));
      
      this.miVMshowHistoryLog=
         makeChkBoxMenuItem(this.viewMenu, "Show log of command history [CB]",
                            null, 0,
                            ((Util.msgTextFrame==null)
                              ? false : Util.msgTextFrame.isVisibleFlag));
      
      addMenuStubToMenuStubList("ViewMenu",this.viewMenu);
    } /* no menu subtree if no data */
    
    /* [2.10] Build the "Plugins" menu extras */
    /* Load the RLO/*.rlo database entries so we can then add
     * them to the  (Plugin | RLO methods). We will add the list
     * of RLO entries to this menu at the end of MAExplorer initialization.
     */
    String srloName= (mae.DBUG_TEST_R_OR_SPLUS_CHECKBOX) ? "SRLO" : "RLO";
    rloSmnu= makeSubMenu(this.pluginsMenu, (srloName+" methods"), null,0);
    addMenuStubToMenuStubList("Plugins:RLOmethods",rloSmnu);   
    
    if(mae.DBUG_TEST_R_OR_SPLUS_CHECKBOX)
      this.miPLGMuseSPLUSasComputingEngine= 
         makeChkBoxMenuItem(this.pluginsMenu, 
                            "Use S-PLUS, else R, as computing engine [CB]",
                            null, 0, 
                            mae.useSPLUSasComputingEngineFlag);
    
    this.miPLGMuseTimeStampReports= 
         makeChkBoxMenuItem(this.pluginsMenu, 
                            "Save "+srloName+
                            " reports in time-stamped Report/ folder [CB]",
                            null, 0, 
                            mae.useRLOloggingFlag);
    
    /* Set RLO Report logging flag for creating subdirectory in 
     * {project}/Report/{RLOname}-yymmdd.hhmmss/ to store the RLO output 
     * files copied to Report/
     */        
    /* If R program exists, then add the RtestPlugin */
    String sRfoundStr= Reval.simpleLookupRprogramPath();
    if(mae.NEVER && sRfoundStr!=null)
    { /* silently tests and loads good "RtestPlugin.jar" from install dir */      
      String
        userDir= System.getProperty("user.dir")+mae.fileSeparator,
        pluginJarPath= userDir + "Plugins" + mae.fileSeparator+"RtestPlugin.jar";
      try
      { /* try to load it */        
        MAEPlugin.popup.PluginLoader
          piLdr= new MAEPlugin.popup.PluginLoader(mae.mja, pluginJarPath);      
        addMenuStubToMenuStubList("RtestPlugin.jar", this.pluginsMenu);
      }
      catch (Exception elp)
      {
        System.out.println("MBF- can't load RtestPlugin.jar");
      }
    }
    this.pluginsMenu.addSeparator();
    
    if(mae.startupFileExistsFlag)
    { /* no menu subtree if no data */  
      if(mae.DBUG_TEST_PLUGIN_FLAG && pluginsMenu!=null)
      { /* build Plugin code testing command */
        /* The mae.DBUG_TEST_PLUGIN_FLAG enables testing MAEPlugin code
         * from the initial menu without doing a dynamic load class.
         * The class that the Plugin invokes should be linked
         * with MAExplorer and invoked by TestPlugin menu that
         * then invokes the class. This means that you need to
         * edit TestPluginCode.java to invoke the code you want to debug
         * with MAExplorer.
         */
        makeMenuItem(this.pluginsMenu, "Test Plugin Code", "Plugin:TestCode",0);
      } /* build Plugin code testing command */
    } /* no menu subtree if no data */
    
    /* [2.11] Build the "HELP" menu */
    makeMenuItem(this.helpMenu, "Home page", "DOC:Home", 0);
    makeMenuItem(this.helpMenu, "Introduction", "DOC:Intro", 0);
    makeMenuItem(this.helpMenu, "Overview", "DOC:Ovr", 0);
    makeMenuItem(this.helpMenu, "PDF documents", "DOC:PDFs", 0);
    makeMenuItem(this.helpMenu, "Short tutorial", "DOC:ShortTut", 0);
    makeMenuItem(this.helpMenu, "Advanced Tutorial", "DOC:AdvTut", 0);
    makeMenuItem(this.helpMenu, "Menu summary", "DOC:Mnu", 0);
    makeMenuItem(this.helpMenu, "Reference manual", "DOC:RefMan", 0);
    smnu= makeSubMenu(this.helpMenu, "Menus", null,0);
    makeMenuItem(smnu, "File", "DOC:RefMan-file", 0);
    makeMenuItem(smnu, "Samples ", "DOC:RefMan-samples", 0);
    makeMenuItem(smnu, "Edit", "DOC:RefMan-edit", 0);
    makeMenuItem(smnu, "Analysis - GeneClass", "DOC:RefMan-GeneClass", 0);
    makeMenuItem(smnu, "Analysis - Normalization", "DOC:RefMan-Normalization", 0);
    makeMenuItem(smnu, "Analysis - Filter", "DOC:RefMan-Filter", 0);
    makeMenuItem(smnu, "Analysis - Plot", "DOC:RefMan-Plot", 0);
    makeMenuItem(smnu, "Analysis - Cluster", "DOC:RefMan-Cluster", 0);
    makeMenuItem(smnu, "Analysis - Report", "DOC:RefMan-Report", 0);
    makeMenuItem(smnu, "View", "DOC:RefMan-view", 0);
    makeMenuItem(smnu, "Plugins", "DOC:RefMan-plugins", 0);
    makeMenuItem(smnu, "Help", "DOC:RefMan-help", 0);
    makeMenuItem(this.helpMenu, "MAEPlugins", "DOC:MAEPlugins", 0);
    makeMenuItem(this.helpMenu, "R plugins", "DOC:RtestPlugins", 0);
    makeMenuItem(this.helpMenu, "Intro to data exploration", "DOC:dataExpl", 0);
    makeMenuItem(this.helpMenu, "Glossary", "DOC:Gloss", 0);
    makeMenuItem(this.helpMenu, "Index", "DOC:Index", 0);
    
    if(mae.cfg.sHelpMenu!=null)
    { /* PARAM: "HelpMenu"[i] entries */
      this.helpMenu.addSeparator();
      for(int h= 1;h<=cfg.nHelpMenus;h++)
      {
        String name= "DOC:HelpMenu"+h;
        makeMenuItem(this.helpMenu, mae.cfg.sHelpMenu[h-1], name, 0);
      }
      this.helpMenu.addSeparator();
    }
    makeMenuItem(this.helpMenu, "About", "DOC:About", 0);
    
    addMenuStubToMenuStubList("HelpMenu",this.helpMenu);
    
    return(nCmds);    /* total # of menu entries created */
  } /* addMenuBar */
  
  
  /**
   * buildMenuGUI() - build the GUI with pop up menus
   * @param x0 is X ULHC position of window
   * @param y0 is Y ULHC position of window
   * @param width of window
   * @param height of window
   * @see ArrayScroller
   * @see StateScrollers
   * @see #createDialogQueryFrames
   * @see #setSTOPbuttonState
   */
  void buildMenuGUI(int x0, int y0, int width, int height)
  { /* buildMenuGUI */
    Panel pMain= (Panel)mae;
    
   /* [1] If redoing the "menu bar", then need to remove OLD pull-down menus
    * and UN-register them from the event handlers before creating them
    * again.
    */
    
    /* [2] Create panel to hold checkboxes and scrollbars */
    Panel pgrid= new Panel();     /* holds chkboxes & scrollbars */
    pgrid.setLayout(new GridLayout(1,2)); /* was (2,2) */
    //this.add(pgrid);		    /* add menu bar frame to ctrl panel */
    
    ps= new Panel();              /* just holds chkboxes & scrollbars */
    FlowLayout flowLeft= new FlowLayout(FlowLayout.LEFT);
    ps.setLayout(flowLeft);
    pgrid.add(ps);
    
    HPlabelPanel= new Panel();    /* holds HP-X and -Y labels and possibly
                                   * other controls */
    //HPlabelPanel.setLayout(flowLeft);
    HPlabelPanel.setLayout(new GridLayout(2,2));
    ps.add(HPlabelPanel);
    this.add(pgrid,"North");	    /* add checkboxes and scrollers */
    
    /* [2.1] Setup hidden popup dialogs */
    createDialogQueryFrames();
    
    /* [2.2] Holds popup state scrollers */
    mae.stateScr= new StateScrollers(mae,"State scrollers");
    
    /* [3] Add labels in panel with HP names */
    Color bgColor= ((mae.osName.startsWith("W")) ? Color.lightGray : Color.white);
    HP_Xlabel= new Label(
      " HP-X:                                                                                                    ");
    HP_Ylabel=	new Label(
      " HP-Y:                                                                                                    ");
    HP_Xlabel.setFont(useFontLbl);
    HP_Ylabel.setFont(useFontLbl);
    HP_Xlabel.setBackground(bgColor);
    HP_Ylabel.setBackground(bgColor);
    
   /* [3.1] Create a STOP button to be used if want to abort a
    * computation. The event handler sets the mae.abortFlag
    * which is then tested by the particular process.
    */
    stopButton= null;
    mae.abortFlag= false;             /* disable */
    this.addKeyListener(this);        /* will listen for ESC key */
    this.addKeyListener(this);
    
    /* Create special buttons & arrange to handle button clicks */
    mouseOverCheckBox= new Checkbox("Mouse-over info",
    mae.useMouseOverFlag);
    mouseOverCheckBox.addItemListener(this);
    if(miVMuseMouseOver!=null)
      miVMuseMouseOver.setEnabled(false);
    
   /* Create Stop Button that will only appear when needed
    * and is set to either "STOP!" or "RUNNING"
    * [TODO] problems with event handler - disabled for now.
    */
    stopButton= new Button(stopLblName);
    stopButton.setFont(useFontLbl);
    stopButton.setActionCommand("STOP!");
    stopButton.addActionListener(this);
    setSTOPbuttonState(false,false); /* set to "RUNNING" */
    
   /* [3.2] Create a Current Gene Name Button for popuping up the
    *  typein Guesser for the clone MasterGeneName/Master_ID.
    * This uses the poup guesser when activated.
    */
    curGeneTextButton= new Button(PopupGeneGuesser.guesserTitle);
    curGeneTextButton.setBackground(Color.cyan);
    curGeneTextButton.setFont(useFontLbl);
    curGeneTextButton.setActionCommand("PopupGeneGuesser");
    curGeneTextButton.addActionListener(this);
    curGeneTextButton.setEnabled(false);
    curGeneTextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
   /* [3.3] Load up the HP label control sub panel.
    *    [GeneGuesser | [x] mouseover]  ==  HP-X label
    *    [STOP!]                        ==  HP-Y label
    * 2x2 but pack commands in ULPC.
    * Put the STOP! button in the LLHC
    */
    Label dummyLbl= new Label("");
    Panel subCtrlP= new Panel();   /* holds curGeneTxt & mouseover */
    subCtrlP.setLayout(flowLeft);
    
    HPlabelPanel.setBackground(bgColor);
    subCtrlP.add(curGeneTextButton);     /* 1st cell, Left */
    subCtrlP.add(mouseOverCheckBox);      /* 1st cell, middle */
    HPlabelPanel.add(subCtrlP);           /* 1st row, col 1 */
    HPlabelPanel.add(HP_Xlabel);          /* 1st row, col 2 */
    
    if(mae.LECBdebug==0)
    { /* add Stop buttom */
      Panel subCtrlP2= new Panel();         /* holds curGeneTxt & mouseover*/
      subCtrlP2.setLayout(flowLeft);
      subCtrlP2.add(stopButton);        /* need to add 3rd row */
      subCtrlP2.add(dummyLbl);
      HPlabelPanel.add(subCtrlP2);      /* 2nd row, col 1 */
    }
    else
    { /* don't add STOP button */
      HPlabelPanel.add(dummyLbl);       /* 2nd row, col 1 */
    }
    
    HPlabelPanel.add(HP_Ylabel);          /* 2nd row, col 2 */
    
    /* [4] Put the image in ArrayScroller object & add data separately */
    int sNbrX= mae.mapHPtoMenuName[mae.curHP_X];
    mae.is= new ArrayScroller(mae,
                              mae.snImageFile[sNbrX], /* name */
                              mae.snHPName[sNbrX],
                              mae.canvasHSize,  /* mae.is.siCanvas size*/
                              mae.canvasVSize,
                              mae.pWidth,       /* mae.is main window size */
                              mae.pHeight,
                              1                 /* magnification */
                              );   
    mae.is.siCanvas.addKeyListener(this);   /* will listen for ESC key */
    
    //mae.is.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    this.add(mae.is,"Center");     /* install in popup menubar frame */
    
    this.setVisible(true);
  } /* buildMenuGUI */
  
  
  /**
   * createDialogQueryFrames() - create all popup dialog frames
   * @see PopupDialogQuery
   * @see PopupBinOprDialogQuery
   */
  void createDialogQueryFrames()
  { /* createDialogQueryFrames */
    if(dialogFrame==null)
    {
      dialogFrame= new Frame("Dialog");
      pdq= new PopupDialogQuery(mae,dialogFrame,2);
      pBOdq= new PopupBinOprDialogQuery(mae,dialogFrame);
    }
  } /* createDialogQueryFrames */
  
  
  /**
   * setSTOPbuttonState() - enable/disable STOP button & clear abortFlag.
   * If either flag is true, then repaint the button or force it to appear.
   * [TODO] need to make this handler a separate thread.
   * Need to put all EventMenu processes on a separate thread so can abort it.
   * For now, the button is disabled...
   * @param flag to abort command process if true.
   * @param isEnabledFlag sets stop label to "STOP!" else "RUNNING"
   * @see #repaint
   */
  synchronized void setSTOPbuttonState(boolean flag, boolean isEnabledFlag)
  { /* setSTOPbuttonState */
    if(stopButton==null)
      return;
    
    hasStopButtonFlag= isEnabledFlag;
    stopLblName= (isEnabledFlag) ? "STOP!" : "RUNNING";
    Color stopButtonColor= (isEnabledFlag) ? Color.red : Color.green; /* Color.gray */
    stopButton.setLabel(stopLblName);
    stopButton.setBackground(stopButtonColor);
    //stopButton.setEnabled(isEnabledFlag);
    stopButton.repaint();
    repaint();
    
    //if(flag || mae.abortFlag)
    // {
    //   mae.abortFlag= flag;   /* clear flag in BOth cases */
    //   mae.repaint();
    // }
    // else
    mae.abortFlag= flag;   /* clear flag in BOth cases */
  } /* setSTOPbuttonState */
  
  
  /**
   * setHP_XYlabels() - set the GUI HP-X: and HP-Y: labels
   * as singles or multiple (Class names)
   * @see SampleSets#setHPxyModStrings
   */
  void setHP_XYlabels()
  { /* setHP_XYlabels */
    HPxyData hpd= mae.cdb.hpXYdata;
    
    if(mae.useHPxySetDataFlag)
    { /* use HP-X and HP-y 'set' names */
      hpd.hpNameX= mae.classNameX;
      hpd.hpNameY= mae.classNameY;
    }
    else
    { /* use HP-X and HP-y msX and msY names */
      hpd.hpNameX= (mae.msX==null) ? "-none-" : mae.msX.fullStageText;
      hpd.hpNameY= (mae.msY==null) ? "-none-" : mae.msY.fullStageText;
    }
    
    /* Setup mae.hps.(sMod, sModX, sModY) based on whether using
     * ratio mode or swapCy5Cy3DataFlags
       */
    mae.hps.setHPxyModStrings();
    
    HP_Xlabel.setText(" HP-X: "+hpd.hpNameX + mae.hps.sModX);
    HP_Ylabel.setText(" HP-Y: "+hpd.hpNameY + mae.hps.sModY);
  } /* setHP_XYlabels */
  
  
  /**
   * enableMenus() - enable/disable all menus, checkboxes, typein fields etc.
   * @param opr is "none", "all", or "basic"
   */
  synchronized void enableMenus(String opr)
  { /* enableMenus */
    boolean flag= opr.equals("all");
    
    analysisMenu.setEnabled(flag);
    sampleMenu.setEnabled(flag);
    geneClassMenu.setEnabled(flag);
    normMenu.setEnabled(flag);
    editMenu.setEnabled(flag);
    filterMenu.setEnabled(flag);
    viewMenu.setEnabled(flag);
    plotMenu.setEnabled(flag);
    clusterMenu.setEnabled(flag);
    reportMenu.setEnabled(flag);
    
    if(mae.mja!=null && pluginsMenu!=null)
      pluginsMenu.setEnabled(flag);
    if(curGeneTextButton!=null)
      curGeneTextButton.setEnabled(flag);
    if(miVMuseMouseOver!=null)
      miVMuseMouseOver.setEnabled(flag);
    
    boolean
    basicFlag= (flag || opr.equals("basic"));
    fileMenu.setEnabled(basicFlag);
    if(mae.mja!=null && pluginsMenu!=null)
      pluginsMenu.setEnabled(basicFlag);
    helpMenu.setEnabled(basicFlag);
  } /* enableMenus */
  
  
  /**
   * actionPerformed() - handle action events for Buttons and menu commands.
   * @param e is action event for buttons and menu commands
   * @see EventMenu#handleActions
   * @see FileIO#logMsgln
   * @see PopupGeneGuesser
   * @see #setSTOPbuttonState
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    
    if(cmd.equals("STOP!"))
    { /* Special case for the STOP! button */
     /* Set the mae.abortFlag to true and reset to "RUNNING"
      * Ignore button press if already in RUNNING mode.
      */
      if(hasStopButtonFlag)
        setSTOPbuttonState(true, false);
      return;
    }
    
    else if(cmd.equals("PopupGeneGuesser"))
    { /* enter current gene name in popup guesser */
      if(masterIDguesserIsPoppedUp)
        return;                     /* no-op */
      masterIDguesserIsPoppedUp= true;
      
      /* Create guesser (separate window) */
      gnMidGuesser= new PopupGeneGuesser(mae, '\0', useFontLbl);
      return;
    }
    else
    { /* process menu commands */
      for(int i=0;i<nCmds;i++)
        if(cmd.equals(menuActionCmdList[i]))
        {
          EventMenu.handleActions(e);
          return;
        }
    }
    
    mae.fio.logMsgln("MBF-AP Illegal command [" + cmd + "] - ignored");
  } /* actionPerformed */
  
  
  /**
   * itemStateChanged() - handle item state changed events.
   * NOTE: need to implement radio groups here since AWT only
   * implements radio groups for Checkboxes, and ot for Checkbox MenuItems.
   * @param e is checkbox event Checkbox and menu checkbox commands
   * @see EventMenu#handleItemStateChanged
   */
  public void itemStateChanged(ItemEvent e)
  { /* itemStateChanged */
    if(mouseOverCheckBox==e.getSource())
    {
      mae.useMouseOverFlag= ((Checkbox)e.getSource()).getState();
      miVMuseMouseOver.setState(mae.useMouseOverFlag);
      mae.repaint();
      return;
    }
    
    EventMenu.handleItemStateChanged(e);    /* process it THERE */
  } /* itemStateChanged */
  
  
  /**
   * windowClosing() - close the window
   * @param e is window closing event
   * @see MAExplorer#quit
   */
  public void windowClosing(WindowEvent e)
  { /* windowClosing */
    mae.quit();
  } /* windowClosing */
  
  
  public void windowActivated(WindowEvent e) {}
  public void windowClosed(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowOpened(WindowEvent e) {}
  
  
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
    mae.abortFlag= (ch==KeyEvent.VK_ESCAPE);
       /* fio.msgLogln("GB-KT ch='"+ch+"'="+((int)ch)+
          " abortFlag="+mae.abortFlag);
        */
  } /* keyTyped */
  
  
} /* end of class BuildGUI */

