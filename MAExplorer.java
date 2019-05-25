/* File: MAExplorer.java */

import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.lang.*;
import java.net.*;
import java.util.*;

/**
 * MicroArray Explorer program for DNA expression array data
 * mining. This is the main() class. 
 * See the MAExplorer Reference Manual http://maexplorer.sourceforge.net/
 * for the details.
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
 * @version $Date: 2006/02/27 14:50:58 $   $Revision: 1.132 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Gene 
 * @see GeneClass
 * @see GeneGeneDist
 * @see GeneList
 * @see GipoTable
 * @see GetParams
 * @see MaeJavaAPI
 * @see MaHybridSample
 * @see MaInfoTable
 * @see Maps
 * @see MenuBarFrame
 * @see PopupRegistry
 * @see SampleSets
 * @see SamplesTable
 * @see ScrollableImageCanvas
 * @see SpotData
 * @see SpotFeatures
 * @see StageNames
 * @see StateScrollers
 * @see Statistics
 * @see UserState
 * @see Util
*/

public class MAExplorer extends Applet implements Runnable 
{
  /** Compiled Version constant for use in version comparison etc. */
  final static String
    VERSION= "V.0.96.34.01";
  /** Compiled MAExplorer title with version number */
  final static String
    maeTitleVer= "MicroArray Explorer - "+VERSION+"-Beta"; 
  /** Compiled Date/Revision constants */
  final static String
    verStr= "Nov 24, 2003";       
           
  /* ----------- START OF GLOBAL DEBUGGING FLAGS ---------------- */
     
  /** Enable testing MAEPlugin from the intial menu without dynamic MAEPlugin
   * loading of the class. <B>THIS IS NEVER ENABLED IN OFFICIAL RELEASES...</B>
   * The class that the Plugin invokes should be linked with MAExplorer
   * and invoked by a new() in TestPlugin menu that then invokes the
   * class. This means that you need to edit the TestPluginCode.java code 
   * to invoke the code you want to debug with MAExplorer.
   */
  static boolean
    DBUG_TEST_PLUGIN_FLAG= false;
  
  /** Test the code to let the user switch between R or S-Plus as the
   * statistics evaluation engine.
   */
  final static boolean
    DBUG_TEST_R_OR_SPLUS_CHECKBOX= false; 
  
  /** If debugging MAERlibr.zip file update. This switches the way we
   * update the MAERlibr from the maexplorer.sourceforge.net server.
   */
  final static boolean
    DBUG_MAERlibr_ZIP_UPDATE= false; 
  
  /** Default empty .mae startup database */
  final static String
    MAE_STARTUP_FILE= "MAEstartupDefault.mae";  
  
  /** Default microarray database server. This is Web server 
   * where "/MAExplorer/" lives, but should be changed via the
   * Config file to other servers. It will not hurt though
   * if it is not changed.
   */
  final static String
    DEF_BASESERVER= "http://maexplorer.sourceforge.net/"; 
				
  /** flag to prevent multiple instances*/
  private boolean
    onceOnlyFlag= false;  
  
  /* --- Debugging stuff... for printing out a program trace to
   * the Java console. Note: if running with a web browser,
   * popup the java console BEFORE starting MAExplorer.
   * NOTE: do not "final static" since javac will barf on its usage. ---
   */
  /** Debugging flag - Always TRUE */
  static boolean
    CONSOLE_FLAG= true;    
  /** Debugging flag - Always FALSE */
  static boolean
    NEVER= false;     
  /** General purpose debugging flag. This flag also sets LECBdebug used
   * to enable ALL menus while debugging
  */ 
  final static boolean
    DEBUG= false;  
  /** Debugging variablen - debugging CGI login locally */
  final static boolean                 
    DBUG_LOGIN= false;    
  /** debugging HierClustNode lists */
  final static boolean
    DBUG_HCN= false;      
  /** debugging Stand-Alone code */
  final static boolean
    DBUG_STANDALONE= false;   
  /** debugging Genomic Menu/URLs */
  /* final */  static boolean
    DBUG_GENOMIC_URLS_FLAG= false;       
  /** debugging QualCheck */ 
  final static boolean
    DBUG_QUALCHK_QUANT_FLAG= false;      
  /** debugging MedMiner Menu/URLs */
  final static boolean
    DBUG_MEDMINER_FLAG= false; 
  /** print memory times at key places */
  final static boolean
    PRINT_MEM_TIMES= true;   
  /** if debugging setting EGL set from subtree of HCN dendrogram */
  final static boolean
    DBUG_SET_EGL_FROM_DENDROGRAM= false;  
  /** if debugging CANVAS_WRAPAROUND_BUG */
  final static boolean
    CANVAS_WRAPAROUND_BUG= false;
      
  /** if debugging NormalizationPlugins using the PluginLoader */
  final static boolean
    DBUG_NORM_PLUGIN= false; 
  
  /** [DEBUGGING] If debugging a plugin extending NormalizationPlugin, 
   * you may debug it in the MAExplorer core image by temporarily including
   * in in the code.  <B>THIS IS NEVER ENABLED IN OFFICIAL RELEASES...</B>
   * We use a wrapper class GenNormalizationPlugin instance gnp that in turn
   * generates a GenericNormalizationPlugin and calls it as required. Only
   * the gnp is visible to the MAExplorer code.
   * We add this dummy menu item in the code to make it easier to debug 
   * "pipeline" types of MAEPlugin.Analysis.xxxx plugins.
   * This:
   * 1. creates gnp and testGenericNormPluginFlag instances in MAExplorer
   * 2. creates miNRMtestGenericNormPlugin menu item instance in MenuBarFrame
   * 3. handles miNRMtestGenericNormPlugin event in EventMenu to toggle
   *    testGenericNormPluginFlag
   * 4. tests testGenericNormPluginFlag instance in CompositeDatabase and
   *    call gnp.XXXX() methods
   * 5. tests testGenericNormPluginFlag instance in MaHybridSample and
   *    call gnp.XXXX() methods
   */
  final static boolean
    DBUG_GENERIC_NORM_PLUGIN= false;
  /** Only while debugging: if DBUG_GENERIC_NORM_PLUGIN is set for use with
   * TestPlugin.
   */
  GenNormalizationPlugin
    gnp= null;
  
  /** if debugging display the CV of HP-EP samples  as colors [0:1.0] lie
   * for p-value display.
   */
  final static boolean
    DBUG_CV_DISPLAY_EPLIST= true;    
    
  /* ----------- END OF GLOBAL DEBUGGING FLAGS ---------------- */
  
    
  /** Developer debug flag. This is 0 if debugging, -1 otherwise and is
   * used for building menus while developing code. We set it to 0 either
   * explicitly or by the DEBUG flag.
   */
  int
    LECBdebug;
    
  /** "this" applet instance for MAExplorer main class */
  Applet
    applet;
    
  /* The following are fixed behavior flags */
  /** (600x900) size of pseudoarray image canvas */    
  final static int
    PSEUDOIMG_WIDTH= 600;
  /** (600x900) size of pseudoarray image canvas */
  final static int
    PSEUDOIMG_HEIGHT= 900;
    
  /** Code for assigning current HP to X-axis */
  final static int
    X_AXIS= 1;
  /** Code for assigning current HP to X-axis */
  final static int
    Y_AXIS= 2; 
    
  /** Maximum # of projects for project lists */
  final static int
    MAX_PROJECTS= 25;
  /** Max # of hybridized array samples to load at one time */
  final static int
    MAX_HYB_SAMPLES= 400;  
  /** Max # of named sample condition lists. Worst case is
   * one sample/condition list which defaults to an
   * expression profile.
   */
  final static int
    MAX_CONDITION_LISTS= MAX_HYB_SAMPLES;  
  /** maximum number of Condition parameters */
  final public static int 
    MAX_COND_PARAM= 50;  
   /** maximum number of # of ordered condition lists slots available. */
  final static int
    MAX_ORDERED_COND_LISTS= 30;
  /** default est # .quant files to load*/
  final static int
    N_EST_QUANT_FILES= 4;  
        
  /** # of files always read:
   *    MaExplorerConfig.txt
   *    GIPO-xxx.txt
   *    Samples.txt
   *    MaExplorerInfo.txt [DEPRICATED]	 
  */      
  final static int
    N_CONST_FILES= 4;             			 
			  
  /** Flag that indicates if the database startup .mae file exists */
  static boolean 
    startupFileExistsFlag= false;		  
  /** Flag that indicates if the startup .mae file exists in PREVIOUS database */
  boolean 
    previousStartupState;
  
  /** Default Configuration file: "MaExplorerConfig.txt" */
  String
    configFile;
          
  /* --- Instances of major classes for global data access --- */
  
  /** Global instance of ArrayScroller obj to hold pseudoarray image 
   * scrollable canvas 
   */
  ArrayScroller
    is;
  /** Global instance of Cluster Genes object */
  ClusterGenes
    clg;
  /** Global instance of composite database  object*/	
  CompositeDatabase
    cdb;
  /** Global instance of condition list tables */
  Condition
    cdList;
  /** Global instance of Configuration and state variables including top level 
   * file, URL configs, etc.
   */      
  Config
    cfg;
  /** Global instance of configuration data from MaExplorerConfig.txt file*/
  ConfigTable
    cfgDB;
  /** Global instance of share grid coordinate mapper */   
  GridCoords 
    grcd;  
  /** Global instance of draw pseudo image */
  DrawPseudoImage
    dwPI;
  /** Global instance of edit editedCL gene (clone) list */
  EditedGeneList 
    egl;  
  /** Global instance of handle MenuItem and MenuItemCheckbox events */
  EventMenu
    em;     
  /** Global instance of file and URL I/O */
  FileIO
    fio; 
  /** Global instance of data Filter gene operations */
  Filter 
    fc;
 /** Global instance of Gene */
  Gene
    gene;
  /** Global instance of Gene Class table  */
  GeneClass
    gct;	
  /** Global instance of Gene-Gene distances */           
  GeneGeneDist
    ccd;                 
  /** Global instance of GeneList contains gene (clone) list tables */
  GeneList
    clLst;
 /** Global instance of GIPO table mapping genes to other data */
  GipoTable
    gipo;	
  /** Global instance of contains set of applet/configuration PARAM parsers */
  GetParams    
    gp;  
  /** Global instance of MaeJavaAPI Gather/Scatter API for MAEPlugins */
  public static MaeJavaAPI
    mja;
  /** Global instance of depricated "MaExplorerInfo.txt" DB list of .info data 
   * for entire DB. 
   */
  MaInfoTable
    infoDB;
  /** Global instance of lookup table maps populated elsewhere*/
  Maps
    mp;    
  /** Global instance of Frame based MenuBar */ 
  MenuBarFrame
    mbf;	
  /** Global instance of scatterplot named overlay maps */
  OverlayMap
    olmap;
  /** Global instance of holds popup registry of all active popup windows and 
   * update handlers.
   */
  PopupRegistry
    pur; 
  /** Global instance of HP-X, HP-Y and HP-E sets and lists */
  SampleSets
    hps;
  /** The "Samples.txt" database list of the H.P.s in the entire database. */
  SamplesTable
    sampDB;     
  /** Global instance of spot data object */
  SpotData
    sd;   
  /** Global instance of spot feature info */
  SpotFeatures
    sf;  
  /** Global instance of Staging names for menus etc */
  StageNames
    sg;	     
  /** holds popup state scrollers - note: created in MenuBarFrame! */
  StateScrollers
    stateScr; 
  /** Global instance of parametric & non-parametric stats */
  Statistics
    stat;  
  /** Global instance of User State methods */
  UserState
    us;   
  /** Global instance of Utility class */
  Util
    util; 
  
  /* -- Future classes included here to force compile -- */
   				 
  /** Global instance of ClusterSamples class */
  ClusterSamples
    clstrSamp; 
  /** Global instance of DryRotException class */
  DryRotException
    dryRotExcept; 
				 
  /* -- active HP samples. The lists of HPs are maintained in
   * class SampleSets ---
   */
  /** Current hybridized sample */
  static MaHybridSample
    ms;
  /** Previous (last current) hybridized sample for garbage collection */
  static MaHybridSample                        
    msPrev;
  /** Current HP-X hybridized sample */
  static MaHybridSample
    msX;
  /** Current HP-Y hybridized sample */
  static MaHybridSample
    msY;  
  /** Current hybridized sample index for hps.msList[]  */
  static int
    curHP;  
  /** Current HP-X sample index for hps.msList[]  */
  static int 
    curHP_X;  
  /** Current HP-Y sample index for hps.msList[]  */
  static int 
    curHP_Y;
    
  /** "HP-X 'set'" name for msListX[] HPs */
  String
    classNameX;
  /** "HP-Y 'set'" name for msListY[] HPs */
  String               
    classNameY;
    
  /** Horizontal screen size */
  int
    scrWidth; 
  /** Horizontal screen size */
  int                
    scrHeight;	
 
  /* --- current system names and paths --- */
  /** Unique Database ID ("DIDxxxxxx") - used as prefix for State/ files */
  static String
    dbID;
  /** operating system Windows, SUNOS, etc */
  /** startup date */
  static String
    date;
  /** operating system Windows, SUNOS, etc */
  static String
    osName;
  /** full codebase path for /mae/ on MAE svr */
  static String
    codeBase;
  /** Stand-alone PARAM "saCodeBase" overrides default*/
  static String
    saCodeBase;
  /** base addr PARAM "proxyServer" */
  String                
    proxyServer;
  /** top level codebase for MAE server */  
  String  	       
    maeServerBase;
  /** either Applet or file I/O or standalone */ 
  static String 
    fileBasePath;             
  /** codebase where the documentation Reference Manual is kept */
  String
    docCodeBase; 
  /** paths, "/" or "|", depends on OS */
  String
    fileSeparator;
  /** dynamic paths separator, "/" or "|", depends on OS and if using 
   * URL from Stand-Alone
   */
  String            
    dynFileSeparator;
   
  /* --- Standalone variables --- */
  /** Standalone current project DB name */
  String
    defSAprjName;
  /** Default standalone directory (=null JWS hack) */
  static String
    defDir;	
  /** Default standalone .mae startup file */
  static String               
    defStartupFile;
  /** Default standalone assoc. DB web address */ 
  static String                          
    defSAwebAddr;
  /** Standalone startup Directory where startupFile is found */
  static String                                        
    startupDir;
  /** Standalone startup DB .mae file */
  static String                                      
    startupFile; 
  /** Standalone Sample DB project list DB file */ 
  static String                                  
    prjListFile; 
  /** Standalone /Cache directory for saving data downloaded for array
   * Web server.
   */ 
  String                                  
    cacheDir;
  /** Standalone /Plugins directory where optional MAEPlugins may be found */ 
  static String
    pluginsDir; 
  /** Standalone getProperties("java.home") */ 
  static String                                     
    javaHome;                  
  
  /** Flag: set if reuse main GUI frame */
  boolean 
    reuseGUIframeFlag; 
  /** Flag: set to TRUE if any Windows PC */
  boolean         
    isWinPCflag;             
       
  /* Info for user state and [FUTURE] groupware */
  /** browser title */
  static String
    browserTitle;
  
  /** access other user name from Dialog */
  String
    otherUserName;
  /** Dialog: current user login name */
  String
    userName;
  /** Dialog: current user password */
  String
    userPasswd;  
  /** current project being requested */
  static String
    curPrjName; 
  /** [0:n-1] login verified projects */
  String
    validPrj[];    
  /** # of login verified projects*/
  int
    nValidPrj;                 
  
  /** set if valid login has been performed */
  boolean
    isLoggedInFlag;            
  
  /** current user state name */
  String
    userStateName;	
  /** current user state index */
  int
    userStateIdx;
       
  /** pseudo grayscale microarray image based on .quantspot intensity data.
   * This image is shared by ALL HPs and is recompute if current HP
   * changes. 
   */
  Image 
    pImage;  
  /** working size of pseudoarray image */
  int
    pWidth; 
   /** working size of pseudoarray image */	
  int               
    pHeight;	     
         
  /* --- Various constants --- */
  /** maximum # intensity values of .quant data */
  int
    MAX_INTENSITY; 
    
  /* --- Ratio Histogram preloads --- */     
  /** Minimum ratio for histogram of ratios histRatioBin[] */    
  final static float
    MIN_RATIO= 0.01F;    
  /** maximum ratio for histogram of ratios histRatioBin[] */
  final static float         
    MAX_RATIO= 100.0F;     
  /** preloaded set of legal ratios in [1/100 : 100] */
  final static double
    histRatioBin[]= {0.0,0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09,
                     .1,.15,.2,.25,.3, .35,.4,.45,.5,.55,.6,.65,.7,.75,.8,.85,.9,
                     1.0,1.25,1.5,1.75,
                     2.0,2.5,3.0,3.5,4.0,4.5,6.0,6.5,7,7.5,8,8.5,9,9.5,10.0,
                     11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,
                     20.0,25.0,30.0,40.0,50.0,60.0,70.0,80.0,90.0,100.0,1000.0
                    };
  /** # of bins in histRatioBin[] */
  final static int
    MAX_RATIO_HIST= histRatioBin.length; 
  
  /* --- Marker bits to use when drawing gene overlays on the image --- */
  /** pseudoarray canvas - No overlay */
  final static int
    MARKER_NONE= 1; 
  /** pseudoarray canvas - draw 5x5 plus */
  final static int
    MARKER_PLUS= 2; 
  /** pseudoarray canvas - draw circle of size radius */
  final static int
    MARKER_CIRCLE= 4;
  /** pseudoarray canvas - draw square of size radius */
  final static int
    MARKER_SQUARE= 8; 
  /** pseudoarray canvas - draw all spots in gene list*/
  final static int
    MARKER_GENES= 16;
  /** pseudoarray canvas - draw Current object */
  final static int
    MARKER_CURRENT= 32; 
  /** pseudoarray canvas - draw K-means cluster # */
  final static int
    MARKER_KMEANS_CLUSTER= 64; 
      
  /* --- Master Gene Name and ID values which the various 
   * xxxxMode variables can take ---
   */
  /** Master masterNameMode Mode - gene name*/
  final static int  
    MASTER_GENE_NAME= 1; 
  /** Master masterNameMode Mode - UniGene name*/  
  final static int
    MASTER_UG_NAME= 2;       
    
  /** Master masterIDmode Mode - clone ID */  
  final static int
    MASTER_CLONE_ID= 1;    
  /** Master masterIDmode Mode - GenBank ID*/
  final static int
    MASTER_GENBANK= 2;       
  /** Master masterIDmode Mode - GenBank 5' ID */
  final static int
    MASTER_GENBANK5= 3;      
  /** Master masterIDmode Mode - GenBank 3' ID */
  final static int
    MASTER_GENBANK3= 4;      
  /** Master masterIDmode Mode - UniGene ID */
  final static int
    MASTER_UG_ID= 5;      
  /** Master masterIDmode Mode - dbEST 5' ID */
  final static int
    MASTER_DBEST5= 6;      
  /** Master masterIDmode Mode - dbEST 3' ID */
  final static int
    MASTER_DBEST3= 7;      
  /** Master masterIDmode Mode - SwissProt ID */
  final static int
    MASTER_SWISS_PROT= 8;      
  /** Master masterIDmode Mode - LocusLink ID */
  final static int
    MASTER_LOCUSLINK= 9;      
  /** Master masterIDmode Mode - OMIM ID */
  final static int
    MASTER_OMIM= 10;      
  /** Master masterIDmode Mode - generic ID */
  final static int
    MASTER_GENERIC_ID= 11;
    
  /* --- QualCheck type used in Good Spot filters of .quant data ---- */ 
  /** QualCheck type: Alphabetic codes */
  final static int
    QUALTYPE_ALPHA= 1; 
  /** QualCheck type: uses MAE Gene.xxxx prop QualCheck codes */
  final static int
    QUALTYPE_PROP_CODE= 2;
  /** QualCheck type: Uses [qualThr] QualCheck continuous data */
  final static int
    QUALTYPE_THR= 3;            
    
  /* --- plot types used in various plots ---- */
  /** Plotting types - draw plus symbol in scatter plot */
  final static int
    DRAW_PLUS= 1;
  /** Plotting types - draw circle symbol in scatter plot */
  final static int
    DRAW_CIRCLE= 2;     
  /** Plotting types - draw box symbol in scatter plot */
  final static int
    DRAW_BOX= 3;      
  /** Plotting types - draw histogram bin */
  final static int
    DRAW_BIN= 4;	        
    
    
  /* ---- Mode values used in a variety of filters ---- */
  /** rangeMode: Inside [v1:v2] for testing */
  final static int
    RANGE_INSIDE= 1;  
  /** rangeMode: Outsiide [v1:v2] for testing */     
  final static int
    RANGE_OUTSIDE= 2; 
  
  /** range-compare mode: spot intensity of samples - ALL samples must comply */
  final static int
    COMPARE_ALL= 1;     
  /** range-compare mode: spot intensity of samples - ANY samples must comply */
  final static int
    COMPARE_ANY= 2;    
  /** range-compare mode: spot intensity of samples - PRODUCT of samples must comply */
  final static int
    COMPARE_PRODUCT= 3;    
  /** range-compare mode: spot intensity of samples - SUM samples must comply */
  final static int 
    COMPARE_SUM= 4;    
  /** range-compare mode: spot intensity of samples - AT MOST n samples must comply */
  final static int 
    COMPARE_AT_MOST= 5;       
  /** range-compare mode: spot intensity of samples - AT LEAST n samples must comply */
  final static int 
    COMPARE_AT_LEAST= 6;
    
  /* --- Select subset of HP's to be used in particular data Filter --- */
  /** HP data Filter subset mode:  F1F2 current HP - mae.ms */
  final static int 
    SS_MODE_MS= 1;
  /** HP data Filter subset mode: F1F2 HP-X and HP-Y - mae.msX and mae.msY */
  final static int 
    SS_MODE_XY= 2; 
  /** HP data Filter subset mode: F1F2 HP-X sets - hps.msListX[] */
  final static int 
    SS_MODE_XSET= 3; 
  /** HP data Filter subset mode: F1F2 HP-Y sets - hps.msListY[] */
  final static int 
    SS_MODE_YSET= 4; 
  /** HP data Filter subset mode: F1F2 HP-X or HP-Y sets - hps.msListX(Y)[] */
  final static int 
    SS_MODE_XORY_SETS= 5; 
  /** HP data Filter subset mode: F1F2 HP-X AND HP-Y sets - hps.msListX(Y)[] */
  final static int 
    SS_MODE_XANDY_SETS= 6; 
  /** HP data Filter subset mode: F1F2 HP-E list - hps.msListE[] */
  final static int 
    SS_MODE_ELIST= 7;  
    
  /* --- Edited Gene list mouse mode --- */
  /** Edited Gene List mouse click action mode: NO-OP */
  final static int 
    EDIT_NOP= 1;    
  /** Edited Gene List mouse click action mode: ADD gene on click */
  final static int        
    EDIT_ADD= 2;    
  /** Edited Gene List mouse click action mode: REMOVE gene on click */
  final static int 
    EDIT_RMV= 3;
   
  /* --- Pseudoarray image plot modes --- */
  /** Pseudoarray image plot mode: Grayscale display current HP intensity */
  final static int  
    PLOT_PSEUDOIMG= 1;
  /** Pseudoarray image plot mode: display HP-X/HP-Y ratios as [green:yellow:red]*/
  final static int  
    PLOT_PSEUDO_HP_XY_RYG_IMG= 2;  
  /** Pseudoarray image plot mode: display HP-X/HP-Y ratios as [green:yellow:red] */
  final static int  
    PLOT_PSEUDO_F1F2_RYG_IMG= 3;
  /** Pseudoarray image plot mode: display current F1/F2 (Cy3/Cy5) 
   * ratios as [green:black:red]
   */
  final static int  
    PLOT_PSEUDO_F1F2_IMG= 4;
  /** Pseudoarray image plot mode: display HP-X/HP-Y ratios as [green:black:red]*/
  final static int  
    PLOT_PSEUDO_HP_XY_IMG= 5;
  /** Pseudoarray image plot mode: display X,Y 'sets' p-Value [0.0:1.0] 
   * as [black:white] */
  final static int  
    PLOT_PSEUDO_HP_XY_P_VALUE_IMG= 6;
  /** Pseudoarray image plot mode: display HP_EP 'list' CV coefficient of variation
   * [0.0:1.0] as [black:white]*/
  final static int  
    PLOT_PSEUDO_HP_EP_CV_VALUE_IMG= 7;
     
  /* --- Popup plot modes --- */
  /** Popup plot mode: current F1 vs F2, or Cy3 vs Cy5 scatter plot */
  final static int  
    PLOT_F1_F2_INTENS= 11;    
  /** Popup plot mode: HP-X vs HP-Y, or HP-X 'set' vs HP-Y 'set' scatter plot */
  final static int  
    PLOT_HP_XY_INTENS= 12;
  /** Popup plot mode: M vs A scatter plot of current sample */
  final static int  
    PLOT_F1_F2_MVSA= 13;
        
  /** Popup plot mode: current HP intensity histogram plot */
  final static int     
    PLOT_INTENS_HIST= 21;
  /** Popup plot mode: current HP F1/F2 ratio histogram plot */
  final static int  
    PLOT_HIST_F1F2_RATIO= 22;    
  /** Popup plot mode: HP-X/HP-Y ratio histogram plot */
  final static int 
    PLOT_HIST_HP_XY_RATIO= 23;        
  /** Popup plot mode: HP-X 'set' / HP-Y 'set' ratio histogram plot */
  final static int 
    PLOT_HIST_HP_XY_SETS_RATIO= 24; 
           
  /** Popup plot mode: expression profile plot */
  final static int 
    PLOT_EXPR_PROFILE= 31;     
       
  /** Popup plot mode: cluster similar genes plot */
  final static int 
    PLOT_CLUSTER_GENES= 41;            
  /** Popup plot mode: hierarchical cluster genes plot */
  final static int 
    PLOT_CLUSTER_HIER= 42;           
  /** Popup plot mode:  cluster HP-samples plot */
  final static int 
    PLOT_CLUSTER_HYBSAMPLES= 43;           
  /** Popup plot mode:  clustergram genes plot */
  final static int 
    PLOT_CLUSTERGRAM= 44;           
  /** Popup plot mode: k-means cluster genes plot */
  final static int 
    PLOT_KMEANS_CLUSTERGRAM= 45;
    
              
  /** Hierarchical clustering mode: Average-arithmetic linkage */
  final static int 
    HIER_CLUST_PGMA_LNKG= 1;
  /** Hierarchical clustering mode: Average-centroid linkage */
  final static int 
    HIER_CLUST_PGMC_LNKG= 2;    
  /** Hierarchical clustering mode: next-minimum cluster linkage */
  final static int 
    HIER_CLUST_NEXT_MIN_LNKG= 3;
    
  /* --- Report modes --- */
  /** Report mode: no report */
  final static int 
    RPT_NONE= 20;
  /** Report mode: highest N HP-X/HP-Y ratio genes */
  final static int 
    RPT_TBL_HIGH_RATIO= 21;
  /** Report mode: lowest N HP-X/HP-Y ratio genes */
  final static int 
    RPT_TBL_LOW_RATIO= 22;    
  /** Report mode: highest N current HP F1/F2 ratio genes */
  final static int 
    RPT_TBL_HIGH_F1F2= 23;    
  /** Report mode: lowest N current HP F1/F2 ratio genes */
  final static int 
    RPT_TBL_LOW_F1F2= 24;        
  /** Report mode: data Filtered genes */
  final static int 
    RPT_TBL_FILTERED_GENES= 25;       
  /** Report mode: genes in current GencClass */
  final static int 
    RPT_TBL_GENE_CLASS= 26;       
  /** Report mode: all named genes */
  final static int 
    RPT_TBL_NAMED_GENES= 27;       
  /** Report mode: array report of SamplesDB */
  final static int 
    RPT_TBL_SAMPLES_DB_INFO= 28;       
  /** Report mode: array report of extra Info samples data (DEPRICATED) */
  final static int 
    RPT_TBL_HP_DB_INFO= 29;       
  /** Report mode: array report with active Web links */
  final static int 
    RPT_TBL_SAMPLES_WEB_LINKS= 30;       
  /** Report mode: expression profiles of filtered genes [CHECK] */
  final static int 
    RPT_TBL_EXPR_PROFILE= 31;       
  /** Report mode: array samples set statistics */
  final static int 
    RPT_TBL_HP_XY_SET_STAT= 32;
  /** Report mode: current gene clustering statistics */
  final static int 
    RPT_TBL_CUR_GENE_CLUSTER= 33;       
  /** Report mode: all genes clustering statistics */
  final static int 
    RPT_TBL_ALL_GENES_CLUSTER= 34;       
  /** Report mode: K-means clustering statistics */
  final static int 
    RPT_TBL_KMEANS_CLUSTER= 35;        
  /** Report mode: mean clusters for K-means clusters statistics */
  final static int    
    RPT_TBL_MN_KMEANS_CLUSTER= 36;       
  /** Report mode: Edited Gene List */
  final static int     
    RPT_TBL_EDITED_GENE_LIST= 37;       
  /** Report mode:Normalization gene list statistics */
  final static int 
    RPT_TBL_NORMALIZATION_GENE_LIST= 38;       
  /** Report mode: HP vs HP correlation coefficient for only filtered genes */
  final static int 
    RPT_TBL_HP_HP_CORR= 39;       
  /** Report mode: array samples Calibration DNA statistics */
  final static int 
    RPT_TBL_CALIB_DNA_STAT= 40;       
  /** Report mode: array samples mean and variance statistics */
  final static int 
    RPT_TBL_HP_MN_VAR_STAT= 41;       
  /** Report mode: hierarchical clusters gene statistics */
  final static int 
    RPT_TBL_HIER_CLUSTER= 42;       
  /** Report mode: project database */
  final static int 
    RPT_TBL_MAE_PRJ_DB= 43;      
  /** Report mode: Ordered Condition List gene statistics */
  final static int 
    RPT_TBL_OCL_STAT= 44;
              
  /** Report format mode: tab-delimited report as TextArea */
  final static int      
    RPT_FMT_TAB_DELIM= 1;
  /** Report format mode: dynamic spreadsheet report */
  final static int      
    RPT_FMT_DYN= 2; 
  
  /* --- List of image and associated files to read for each
   * hybridized sample mae.hps.msList[1:nHP]. ---
   */   
  /** PARAM max # images to preload set from "maxPreloadImages" */
  int
    maxPreloadImgs; 
  /** HP name from "image*" [1:iHPnbr] allocated [MAX_HYB_SAMPLES+1] */
  String
    iSampleName[];
  /** image files to read allocated [MAX_HYB_SAMPLES+1] */  
  String 
    iImageFile[]; 
  /** # of entries in "image#" PARAM list.  The mae.hps.msList[1:mae.hps.nHP]
   * is derived from this and modified from snHPName[] list.
   */
  int
    iHPnbr;
    
  /* --- Stage Name lists --- */
  /** # of entries in the Stage name list */
  int 
    snHPnbr;    
  /** index of snHPName[] corresponding to iSampleName[] entry 
   * allocated [MAX_HYB_SAMPLES+1].
   */  
  int
    mapHPtoMenuName[]; 
  /* Parameters used for bulding the menus. These stage HP name [1:snHPnbr]
   * are initialized to size [MAX_HYB_SAMPLES+1]
   */  
  /** slist of tage HP name [1:snHPnbr] */
  String
    snHPName[];    
  /** list of free form sample names [1:snHPnbr]  */
  String
    snHPSampleID[]; 
  /** (opt) list of DB file ID [1:snHPnbr] */ 
  String
    snHPDatabaseFileID[];
  /** list of image files to read [1:snHPnbr] */  
  String 
    snImageFile[];
  /** list for building menus [1:snHPnbr] */  
  String
    snHPMenuText[];
  /** list for buildingn menus [1:snHPnbr] */ 
  String
    snHPFullStageText[];
  /** list of Project names [1:snHPnbr] */ 
  String
    snPrjName[];
  /** (opt) sample requires login */
  boolean
    snNeedLogin[];
  /** swap (Cy3,Cy5) field data for (Cy5,Cy3) for this HP */
  boolean      
    snSwapCy5Cy3Data[];
       
  /* Used to compute startup progress */          
  /** true if alphaList[] geneNames and numericList[] gene xxxx_IDs are sorted */
  boolean
    sortedGeneAndCloneIDListsFlag; 
  
  /** # of files read so far */  
  int
    nFilesRead;    
  /** # files always read + # .quant files. i.e. = N_CONST_FILES + 
   * N_EST_QUANT_FILES.
   */  
  int
    nFilesTotal;               
    
  /** Flicker delay for run() loop delay also used for flicker */
  int
    runLoopDelayMsec;    
  /** main thread for run() */
  Thread
    runT;
   
  /** PARAM "SpotRadius" */     
  int
    spotRad;    
  /** PARAM "CanvasHorSize" */
  static int
    canvasHSize;     
  /** PARAM "CanvasVertSize" */ 
  static int              
    canvasVSize;  
    
    
  /* --- Data Filter Range sliders --- */
  /** PARAM "SI1" 0 or MIN_INTENSITY (spot Intens.) */
  float
    sit1;                      
  /** PARAM "SI2" MAX_INTENSITY */
  float
    sit2;
  /** PARAM "I1" 0 or MIN_INTENSITY (gene "intens") */
  float
    t1;
  /** PARAM "I2" MAX_INTENSITY */
  float
    t2;                        
  /** PARAM "R1" MIN_RATIO range (HP-X/HP-Y) */
  float
    r1;                        
  /** PARAM "R2" MAX_RATIO range */
  float
    r2;                        
  /** PARAM "CR1" MIN_RATIO range (i.e. Cy3/Cy5)*/
  float
    cr1;                       
  /** PARAM "CR2" MAX_RATIO range */
  float
    cr2;                       
  
  /** Report font size: "8pt", "10pt", or "12pt" */
  String
    rptFontSize; 
 
  /** List of dryrot messages  if any occur. Should be null. */
  String
    dryrotLogStr;             
  
   /** Display Flag: show edited gene list */ 
  boolean
    showEGLflag;
    
  /** Flag set when done initialization and ready to analyze */
  boolean             
    mReady;
    
  /** Flag indicating whether using the Working server (mae/) or not */
  boolean	               
    isWorkingMAE;    
             	 
    
  /* -- Data filter modes. NOTE: the ACTUAL settings are set during init() by
   * the resetDefaultstate() method. ---
   */
  
  /** Pseudo array image plot mode: PLOT_xxxx_IMAGE */
  int
    plotImageMode;    
  /** EGL mode for 'edited gene list': EDIT_xxxx */
  int
    editMode;    
  /** Hier.Clustering linkage mode: HIER_CLUST_xxxxx */
  int
    hierClustMode;   
  
  /** spot quality type mode: QUALTYPE_xxxx */    
  int
    qualTypeMode;     
  /** positive Quant data test mode: SS_MODE_xxxx */
  int
    posQuantTestMode;    
  /** Good Spot (QualChk) test mode: SS_MODE_xxxx */
  int
    goodSpotTestMode;       
  /**  Spot (Detection Value) test mode: SS_MODE_xxxx */
  int
    detValueSpotTestMode;
  /** Report type mode: RPT_xxxx. Not saved in state */
  int
    reportMode;    
  /** Report table format mode: RPT_FMT_xxxx */
  int
    tblFmtMode;    
     
  /** spot intensity test mode: SS_MODE_xxxx */
  int
    spotIntensTestMode; 
  /** Spot intensity range mode for thresholds RANGE_OUTSIDE, RANGE_INSIDE */
  int
    spotIntensRangeMode;				
  /** Spot Intensity compare mode for threshold: COMPARE_xxxx */
  int
    spotIntensCompareMode;    
  /** Sample intensity range mode for threshold: RANGE_INSIDE, RANGE_OUTSIDE */
  int
    sampleIntensityRangeMode; 
  /** Ratio range restriction for threshold mode:  RANGE_INSIDE, RANGE_OUTSIDE */ 
  int
    ratioRangeMode;    
  /** Cy3/Cy5 range restriction for threshold mode: RANGE_INSIDE, RANGE_OUTSIDE */
  int
    ratioCy3Cy5RangeMode;    
  /** CV test mode: SS_MODE_xxxx */
  int
    cvTestMode; 
    			     
   /** Flag: cleared if enter through standalone main() */
   boolean
     isAppletFlag= true;
   /** Flag: stand-alone PARAM "useWebDB" try WebDB before local directories */
   boolean    
     useWebDBflag;
   /** Flag: stand-alone PARAM "enableFIOcaching" */
   boolean    
     enableFIOcachingFlag;
   /** Flag: if enabled, it first checks the /Cache directory for all http://
    * codebase files and if not in the cache, it saves the file in the
    * cache after reading it from the Web.
   */
   boolean    
     cacheFIOflag; 		
				
   /** made changes to DB so may want to SAVE DB*/
   boolean
     madeChangesFlag;
   /** PARAM "useCy5/Cy3" else Cy3/Cy5 */
   boolean
     useCy5OverCy3Flag;    
   /** PARAM "useMouseOver" for popup tracking balloons */
   boolean
     useMouseOverFlag;    
   /** Flicker HP-X and HP-Y in run() loop */
   boolean
     flickerXYflag;        
   /** Scale pseudoarray image by 1/100 to zoom low-range values */
   boolean
     lowRangeZoomFlag;    
   /** Flag: is using one of the Zscore normalizations*/
   boolean
     isZscoreFlag;    
   /** Flag: Plot EP list as overlay 2Dplot else scrollable Grid of EPs */
   boolean
     useEPoverlayFlag;    
   /** PARAM flip array rows & columns */
   boolean
     swapRowsColsFlag;    
   /** PARAM "useRatioData" Cy3/Cy5 else Intensity */
   boolean
     useRatioDataFlag;    
   /** use HP-X,Y 'sets msListX/Y else HP-X,HP-Y msX/Y */
   boolean
     useHPxySetDataFlag;      
  /** [TODO] use mean HP-E 'list' data else single current HP sample data */
   boolean 
     useMeanHPeListDataFlag;
   /** PARAM "allowNegQuantDataFlag" */
   boolean
     allowNegQuantDataFlag;    
   /** update pseudo image - not saved in the State */
   boolean
     updatePseudoImgFlag;    
   /** PARAM use Pseudo XY coords else actual */
   boolean
     usePseudoXYcoordsFlag;    
   /** Flag: plot mean HP-X in F1, HP-Y in F2 pseudoimg */
   boolean
     dualXYpseudoPlotFlag;    
   /** Flag: reuse HP[1] Quant xy coords for speedup*/
   boolean
     reuseXYcoordsFlag;    
   /** PARAM "noMsgReporting" */        
   boolean
     noMsgReportFlag;    
   /** Flag: call quit() if flag set to exit run(). Not saved in state */
   boolean
     dieFlag;    
   /** Flag: set true by the STOP button. Not saved in state */
   boolean
     abortFlag;    
   /** Flag: run the Garbage Collector. Not saved in state  */
   boolean
     doGCflag;    
   /** Flag: set if do "Quit". Not saved in state  */
   boolean
     killAppletFlag;    
				 
   /** Flag: show Filtered spots */
   boolean
     viewFilteredSpotsFlag;     
   /** PARAM "useDichromasy" alt. colors */
   boolean
     useDichromasyFlag;    
   /** Flag: report f1 or f1&f2 (ganged) */
   boolean
     gangSpotFlag;    
   /** Flag: use larger fonts, thicker lines, circles, plus, etc. for 
    * presentation viewing */
   boolean
     presentViewFlag;
   /** Flag: Use S-PLUS, else R, as computing engine */
   boolean
     useSPLUSasComputingEngineFlag;
   /** Flag: use time-stamped RLO logging files folders, else just Report/ */
   boolean
     useRLOloggingFlag;
				    
   /* --- These master names are set by analyzing the GIPO file or possibly on 
    * restoring the state.---
    */
  /** either "GeneName", "UGclusterName, etc" */
  String
    masterGeneName;           
  /** either "CloneID", "UniGeneID, GenBank, etc" */
  String
    masterIDname;              
  /** either MASTER_GENE_NAME or MASTER_UG_NAME */
  int
    masterNameMode;  
  /** either MASTER_CLONE_ID or MASTER_GENBANK or MASTER_GENBANK5 or
   * MASTER_GENBANK3 or MASTER_UG_ID, MASTER_SWISS_PROT, MASTER_GENERIC_ID */
  int            
    masterIDmode;                             
    
  /* --- reporting names --- */
  /** either "F1/F2", "Cy3/Cy5", or "Cy5/Cy3" */
  String
    reportRatioStr;
  /** name of norm. method in Status display */
  String         
    normNameDisp;              
  /** name of normalization method*/
  String         
    normName;                  
  
  /* --- Normalization method state flags --- */
  /** use Good Spots data for computing global array statistics for
   * normalization purposes on a per array basis,
   * otherwise use all spot data.
   */
  boolean
    useGoodSpotsForGlobalStatsFlag;
  /** calc: I'= Intensity - background */
  boolean
    bkgdCorrectFlag;
  /** enable cy3/cy5 ratio median correction*/
  boolean
    ratioMedianCorrectionFlag;
  /** normalize by housekeeping genes*/
  boolean
    normByHousekeepGenesFlag;
  /** normalize by 'user gene set' */
  boolean
    normByGeneSetFlag;
  /** normalize by Calibration DNA set*/
  boolean
    normByCalDNAflag;
  /** normalize by HP Zscore */
  boolean
    normByZscoreFlag;
  /** normalize by HP median */
  boolean
    normByMedianFlag;
  /** normalize by HP log of median */
  boolean
    normByLogMedianFlag;
  /** norm by Zscore log intens,stdDev.*/
  boolean
    normByZscoreMeanStdDevLogFlag;
  /** norm. by Zscore log intens,meanAbsDev*/
  boolean
    normByZscoreMeanAbsDevLogFlag;
  /** normalize to maximum intensity */
  boolean
    scaleDataToMaxIntensFlag;
  
  /** If DBUG_GENERIC_NORM_PLUGIN, then debug GenericNormalizationPlugin
   * within the code. If the DBUG_GENERIC_NORM_PLUGIN is enabled, then
   * the GenNormalizationPlugin) gnp will exist and point to the instance.
   * The menu item appears in the menu:
   * (Analysis | Normalization | "Test Generic Norm Plugin [DBUG]")
   */
  boolean
    testGenericNormPluginFlag= false;
    
  /** use correlation coeffieient instead of Euclidian distance when clustering */
  boolean
    useCorrCoeffFlag;
  /** use UPGMA or WPGMA for computing the average node vector when hierarchical
   * clustering.
   */
  boolean
    hierClustUnWtAvgFlag;
  /** set true if normalize Hier-Clustering dataV[h] by dataV[iDataV] 
   * else norm by HP[h] msListE[h].maQ.maxRI
   */
  boolean
    normHCbyRatioHPflag;
  /** set true if use median instead of means of clusters in K-means clustering. */
  boolean
    useMedianForKmeansClusteringFlag;
  
  /* --- Genomic DB Web browser state flags - select which DB to use --- */
  /** GenBank popup browser Mode */
  boolean
    genBankViewerFlag;         
  /** dbEST popup browser Mode */
  boolean
    dbESTviewerFlag;
  /** UniGene popup browser Mode */
  boolean
    uniGeneViewerFlag;
  /** OMIM popup browser Mode */
  boolean
    omimViewerFlag;
  /** mAdb Clone page popup browser Mode */
  boolean
    mAdbViewerFlag;
  /** LocusLink LocusID page popup browser Mode */
  boolean
    locusLinkViewerFlag;
  /** MedMiner gene page popup browser Mode */
  boolean
    medMinerViewerFlag;
  /** Swiss-Prot page popup browser Mode */
  boolean
    swissProtViewerFlag;
  /** PIR page popup browser Mode */
  boolean
    pirViewerFlag;
  /** holds sGenomicMenu[cfg.nGenomicMenus] menu flags */
  boolean
    genomicViewerFlag[];      
	
  
  /* --- Report modifier state flags --- */   
  /** use Raw intensity for EP ratios in gene REPORTS*/
  boolean
    useEPrawIntensValFlag;     
  /** set for EP ratios in gene REPORTS*/
  boolean
    addExprProfileFlag;       
  /** set for HP_XY statistics in gene REPORTS*/
  boolean
    addHP_XYstatFlag;       
  /** set for OCL statics statistics in gene REPORTS*/
  boolean
    addOCLstatFlag;
     
  /* --- Data Filter state flags --- */
  /** Auto state-scroller popup windown assoc w/Filters */
  boolean
    autoStateScrPopupFlag;
  /** "Use lowest X/Y ratios filter" */
  boolean
    useLowRatiosFilterFlag;    
  /** "Use highest X/Y ratios filter" */
  boolean
    useHighRatiosFilterFlag;   
  /** filter genes by spot Intensity threshold*/
  boolean
    spotIntensFilterFlag;      
  /** filter genes by intensity threshold*/
  boolean
    intensFilterFlag;          
  /** filter genes by ratio threshold*/
  boolean
    ratioFilterFlag;           
  /** filter genes by Cy3/Cy5 ratio threshold*/
  boolean
    ratioCy3Cy5FilterFlag;     
  /** filter genes by GeneClass mbrshp*/
  boolean
    geneClassMbrFilterFlag;    
  /** filter genes by useGeneSet mbrship*/
  boolean
    useGeneSetFilterFlag;      
  /** filter genes on Ratio histogram bin*/
  boolean
    useRatioHistCLflag;        
  /** filter genes on Intensity hist bin*/  
  boolean
    useIntensHistCLflag;       
  /** filter genes on Edited Gene List */
  boolean
    useEditedCLflag;           
  /** filter by genes in Good Genes List */
  boolean
    useGoodGeneCLflag;         
  /** filter by genes with at least 1 replicate */
  boolean
    useReplicateGenesFlag;     
  /** use mean of CV else use max  in CV filter*/
  boolean
    useCVmeanElseMaxFlag; 
  /** filter genes passing HP-X/-Y t-test*/
  boolean
    tTestXYfilterFlag;              
  /** filter genes passing HP-X/-Y sets t-test*/
  boolean
    tTestXYsetsFilterFlag;               
  /** filter genes passing HP-X/-Y sets KS-test*/
  boolean
    KS_TestXYsetsFilterFlag;              
  /** filter genes passing current OCL (Ordered Condition List) F-test*/
  boolean
    F_TestOCLFilterFlag;
  /** filter by Spot CV filter is enabled */
  boolean
    useSpotCVfilterFlag;       
  /** filter genes by cluster HP-E */
  boolean
    clusterHP_EfilterFlag;     
  /** filter genes by abs diff(HP-X,HP-Y) */
  boolean
    useDiffFilterFlag;         
  /** filter genes with positive quant data */
  boolean
    usePosQuantDataFlag;       
  /** filter genes with Good Spot (QualChk) data */
  boolean
    useGoodSpotDataFlag;      
  /** filter genes with Spot (Detection value) data */
  boolean
    useDetValueSpotDataFlag;
  /** filter by genes with non-zero intensity - problem when do log() transform */
  boolean
    useOnlyGenesWithNonZeroDensityFlag;
    
  /* --- Cluster method modification state flags --- */
  /** cluster on Filtered/All genes*/
  boolean
    clusterOnFilteredCLflag;
  /** cluster distance thresholding*/
  boolean
    useClusterDistFlag;        
  /** show a cluster of similar genes for current gene */
  boolean
    useSimGeneClusterDispFlag;    
  /** show gene clusters count */
  boolean
    useClusterCountsDispFlag;  
  /** show hierarchical clusters */
  boolean
    useHierClusterDispFlag;    
  /** show K-means clusters */
  boolean
    useKmeansClusterCntsDispFlag;
  /** norm geneEPvect[] to 1.0 for clustering */ 
  boolean
    useLSQmagNormFlag;         
  /** use cluster cache else recompute each time */
  boolean
    useClusterDistCacheFlag;   
  /** use short else float cluster cache, can save memory if really needed */
  boolean
    useShortClusterDistCacheFlag; 
   
 
  /**
   * main() - for MAExplorer started by standalone application.
   * @param args is the command line arg list
   */
  public static void main(String args[])
  { /* main */
    /* Top level class */
    MAExplorer mae= new MAExplorer();   /* if come in again, kills old one... */
    
    mae.onceOnlyFlag= false;   /* keep additional instances away */
    mae.isAppletFlag= false;   /* clear since enter through main() */
    
    /* Setup the default installation directory. w/o jws, via cmd line */
    String
      argV,
      fileS,
      fileSep= System.getProperty("file.separator"),
      userDir= System.getProperty("user.dir")+fileSep,
      defDir= userDir+"MAE"+fileSep;
    
    mae.defDir= defDir;
    mae.defSAprjName= "Empty database";
    mae.defSAwebAddr= "";
    
    /* Setup projects DB file where list of last .MAE used lives */
    mae.prjListFile= userDir+"MAEprojects.txt";
    mae.pluginsDir= userDir+"Plugins"+fileSep; /* Standalone /Plugins path */
    
    
    /* For standAlone using JWS, directory from args[]  */
    int
      idx,
      i= 0;
    
    /* Add the following to JWS StandAlone 'Program Arguments" in Run:
     *   "file://home/<user>/jws/MAExplorer/"
     */
    mae.defStartupFile= MAE_STARTUP_FILE;
    while(i<args.length)  /* NOTE: could have other args in future*/
    { /* parse command line args */
      if(args[i].equals("-jws") || args[i].equals("-dir"))
      { /* must be set within JWS - DEPRICATED... */
        mae.defDir= args[++i]; /* must be set within JWS */
      }
      else
      { /* overide with file spec. The only non-switch arg */
        argV= args[i];
        mae.defStartupFile= argV;
        idx= argV.lastIndexOf(fileSep);
        if(idx!=-1)
        { /* get just file name - not full path of file name */
          mae.defStartupFile= argV.substring(idx+1);
          /* extract path directory from default startup file */
          mae.defDir= argV.substring(0,idx+1);
        }
      } /* overide with file spec. The only non-switch arg */
      i++;
    } /* parse command line args */
    
      /*
      if(CONSOLE_FLAG)
         System.out.println("MAE-main defDir='"+mae.defDir+
                            "'\n  defStartupFile='"+mae.defStartupFile+"'"+
                            "'\n  pluginsDir='"+mae.pluginsDir+"'");
       */
    
    mae.init();             /* get parameters */
    mae.start();            /* Create GUI, restart the bkgd thread */
    
    System.runFinalization();  /* Why not! */
    System.gc();
  } /* main */
  
  
  /**
   * clearDynamicState() - reset dynamic state variables in MAE instance.
   */
  private void clearDynamicState()
  { /* clearDynamicState */
      /* [TODO] check if we need to null these variables out if running
       * stand-alone. We probably do for Applets. But may be able to
       * simplify if do not need to do this.
       */
    
    /* Note: the lists of HPs are maintained in class SampleSets */
    ms= null;                 /* cur. hybridized sample */
    msPrev= null;             /* previous ms hybridized sample - for GC */
    msX= null;                /* cur. X-axis single hybridized sample */
    msY= null;                /* cur. Y-axis single hybridized sample */
    
    curHP= 0;	                /* cur. hybridized sample being displayed */
    curHP_X= 0;	              /* cur. HP-X hybridized sample being displayed*/
    curHP_Y= 0;	              /* cur. HP-Y hybridized sample being displayed*/
    
    /* Status on the system we are running on */
    scrWidth= 0;              /* screen size */
    scrHeight= 0;
    
    pImage= null;	            /* pseudo grayscale microarray image */
    
    date= Util.dateStr();     /* startup date */
    osName= null;             /* operating system Windows, SUNOS, etc */
    codeBase= null;           /* full codebase path for /mae/ on MAE svr*/
    maeServerBase= null;      /* top level codebase for MAE server*/
    fileBasePath= null;
    isWinPCflag= false;       /* set to TRUE if any Windows PC */
    
    /* Info for user state and [FUTURE] groupware */
    browserTitle= maeTitleVer;  /* browser title */
    otherUserName= null;       /* access other user name from Dialog */
    userName= "";              /* Dialog: current user login name */
    userPasswd= "";		         /* Dialog: current user password */
    curPrjName= "";		         /* current project being requested */
    nValidPrj= 0;              /* # of login verified projects*/
    isLoggedInFlag= false;     /* set if valid login */
    userStateName= null;	     /* current user state name */
    userStateIdx= -1;	         /* current user state index */
    madeChangesFlag= false;    /* made changes to DB so may want to SAVE DB*/
    
    /* Clear the [MAX_HYB_SAMPLES+1] lists */
    iSampleName= null;         /* Sample (HP) name from "image*" [1:iHPnbr] */
    iImageFile= null;          /* image files to read */
    mapHPtoMenuName= null;     /* map snHPName[] to iHPname[] */
    snHPName= null;            /* stage HP name [1:snHPnbr]*/
    snHPSampleID= null;        /* English name of sample */
    snHPDatabaseFileID= null;  /* (opt) DB file ID */
    snImageFile= null;         /* image files to read */
    snHPMenuText= null;        /* for building menus */
    snHPFullStageText= null;   /* for building menus */
    snPrjName= null;           /* Project name */
    snNeedLogin= null;         /* requires login */
    
    /* Used to compute startup progress */
    sortedGeneAndCloneIDListsFlag= false;  /* if alphaList[] and
                                            * numericList[] are sorted */
    
    nFilesRead= 0;             /* # of files read so far */
    
    /* # files always read + # .quant files */
    nFilesTotal= N_CONST_FILES + N_EST_QUANT_FILES;
       
    runT= null;		 /* main thread for run() */
    
    normName= "median";        /* default Normalization method */
    normNameDisp= normName;
    rptFontSize= "10pt";       /* "8pt", "10pt", or "12pt" */
    LECBdebug= -1;             /* 0 if debugging, -1 otherwise */
    dryrotLogStr= null;        /* Build list of dryrot messages */
    mReady= false;	           /* ready to analyze */
  } /* clearDynamicState */

  
  /**
   * destroyStaticState() - reset static state variables in MAE instance
   * @see ClusterGenes#cleanup
   * @see CompositeDatabase#cleanup
   * @see Condition#cleanup
   * @see EventMenu#cleanup
   * @see GeneGeneDist#cleanup
   * @see GeneList#cleanup
   * @see HierClustNode#cleanup
   * @see Util#cleanup
   */
  private void destroyStaticState()
  { /* destroyStaticState */
    /* Clear selected counters */
    mReady= false;	            /* ready to analyze */
    nFilesRead= 0;              /* # of files read so far */    
    
    /* # files always read + # .quant files */
    nFilesTotal= N_CONST_FILES + N_EST_QUANT_FILES;
    
    /* Remove static objects */
    if(startupFileExistsFlag)
    {
      ccd.cleanup();            /* Gene-Gene distances */
      clLst.cleanup();          /* contains gene (clone) list tables */
      ClusterGenes.cleanup();   /* cluster genes */
      cdb.cleanup();            /* composite database object*/
      cdList.cleanup();         /* contains condition list tables */
    }
    HierClustNode.cleanup();    /* hierarchical clustering */
    util.cleanup();             /* Utility instance */
    em.cleanup();               /* handle event menu events */
    
    /* Kill object instances since will regenerate */
    gene= null;                 /* Gene instances */
    ccd= null;                  /* Gene-Gene distances */
    clLst= null;                /* contains gene (clone) list tables */
    cdb= null;                  /* composite database  object*/
    cdList= null;               /* contains condition list tables */
    cfg= null;                  /* top level file and URL configs */
    cfgDB= null;                /* data from MaExplorerConfig.txt DB*/
    dwPI= null;                 /* draw pseudo image */
    egl= null;                  /* edit the editedCL gene list */
    em= null;                   /* handle EventMenu Item events */
    fio= null;                  /* file and URL I/O */
    fc= null;                   /* Filter gene operations */
    gct= null;		              /* Gene Class table */
    gipo= null;		              /* GIPO table mapping spots to gene data*/
    grcd= null;                 /* GridCoord mapper */
    gp= null;                   /* contains set of PARAM parsers */
    hps= null;                  /* maintains HP-X, HP-Y and HP-E sets*/
    infoDB= null;               /* The "MaExplorerInfo.txt" database */
    mp= null;                   /* lookup table maps populated elsewhere*/
    sampDB= null;               /* The "Samples.txt" database */
    sd= null;                   /* spot data object */
    sf= null;                   /* spot feature info */
    sg= null;			              /* Staging names for menus etc */
    us= null;                   /* user State */
    util= null;                 /* Utility instance */
    
    System.runFinalization();  /* Why not! */
    System.gc();
  } /* destroyStaticState */
  
  
  /**
   * resetDefaultstate() - Reinitialize global user state.
   * Anything in this method should be able to be set during
   * runtime from the "Reset" menu.
   * If called from the MENU, call Config again to reset from
   * PARAM values.
   * @see Config#getStateParamValues
   */
  void resetDefaultstate()
  { /* resetDefaultstate */
    /* [1] set default modes */
    plotImageMode= PLOT_PSEUDOIMG;
    editMode= EDIT_NOP;
    reportMode= RPT_NONE;
    tblFmtMode= RPT_FMT_DYN;
    spotIntensRangeMode= RANGE_INSIDE;
    spotIntensCompareMode= COMPARE_ALL;
    sampleIntensityRangeMode= RANGE_INSIDE;
    ratioRangeMode= RANGE_OUTSIDE;
    ratioCy3Cy5RangeMode= RANGE_OUTSIDE;
    rptFontSize= "10pt";
    spotIntensTestMode= SS_MODE_MS;
    cvTestMode= SS_MODE_MS;
    posQuantTestMode= SS_MODE_MS;
    hierClustMode= HIER_CLUST_PGMC_LNKG;
    goodSpotTestMode= SS_MODE_MS;
    detValueSpotTestMode= SS_MODE_MS;
    qualTypeMode= QUALTYPE_PROP_CODE;
    
    /* [2] set other default switches */
    madeChangesFlag= false;       /* made DB changes, so may want to SAVE DB*/
    useCy5OverCy3Flag= false;     /* PARAM "useCy5/Cy3" else Cy3/Cy5 */
    useMouseOverFlag= true;       /* PARAM "useMouseOver" for popup tracking balloons */
    flickerXYflag= false;         /* Flicker HP-X and HP-Y in run() loop */
    lowRangeZoomFlag= false;      /* Scale pseudoarray image zoom low-range values */
    isZscoreFlag= false;          /* is using one of the Zscore normalizations*/
    useEPoverlayFlag= false;      /* Plot EP list as 2D overlay or Grid */
    useRatioDataFlag= false;      /* PARAM "useRatioData" */
    dualXYpseudoPlotFlag= false;  /* plot mean HP-X in F1, HP-Y in F2 pseudoimg */
    reuseXYcoordsFlag= false;     /* reuse Quant xy coords*/
    usePseudoXYcoordsFlag= false; /* use Pseudo XY coordinates else actual*/
    updatePseudoImgFlag= false;   /* update pseudo image */
    killAppletFlag= false;        /* set if do "Quit" */
    doGCflag= true;               /* run the Garbage Collector */
    viewFilteredSpotsFlag= true;  /* show Filtered spots */
    gangSpotFlag= false;          /* report f1 or f1&f2 (ganged) */
    genBankViewerFlag= false;     /* GenBank popup browser Mode */
    dbESTviewerFlag= false;       /* dbEST popup browser Mode */
    uniGeneViewerFlag= false;     /* UniGene popup browser Mode */
    locusLinkViewerFlag= false;   /* LocusLink LocusID page popup browser Mode */
    omimViewerFlag= false;        /* OMIM page popup browser mdoe */
    medMinerViewerFlag= false;    /* MedMiner Clone page popup browser Mode */
    swissProtViewerFlag= false;   /* Swiss-Prot page popup browser Mode */
    pirViewerFlag= false;         /* PIR page popup browser Mode */
    presentViewFlag= false;       /* use larger fonts, thicker lines, spots,
                                   * plus, etc. for presentation viewing */    
    useSPLUSasComputingEngineFlag= false; /* Use S-PLUS, else R, as computing engine */
    useRLOloggingFlag= false;     /* use time-stamped RLO logging files Report/ folders */
    useGoodSpotsForGlobalStatsFlag= false; /* use Good Spots data for global array stats*/
    useDetValueSpotDataFlag= false; /* use spot detection value */   
    useOnlyGenesWithNonZeroDensityFlag= false; /* filter by genes w/non-zero intensity */
    bkgdCorrectFlag= false;       /* I'= Intensity - bkgrd */
    ratioMedianCorrectionFlag= false; /** enable cy3/cy5 ratio median correction*/
    scaleDataToMaxIntensFlag= false; /* normalize to maximum intensity */
    normByHousekeepGenesFlag= false; /* normalize by housekeeping genes*/
    normByGeneSetFlag= false;      /* normalize by gene set */
    normByCalDNAflag= false;       /* normalize by Calibration DNA */
    normByZscoreFlag= false;       /* normalize by HP Zscore */
    normByMedianFlag= true;        /* normalize by HP median */
    normByLogMedianFlag= false;    /* normalize by HP log of median */
    normByZscoreMeanStdDevLogFlag= false; /* norm w/Zscore log intens,stdDev*/
    normByZscoreMeanAbsDevLogFlag= false; /* norm w/Zscore log intens,mnAbsDev*/
    useCorrCoeffFlag= false;       /* use corr. coeff instead of Eucl.dist */
    hierClustUnWtAvgFlag= false;   /* use UPGMA or WPGMA hier Clust avging*/
    normHCbyRatioHPflag= true;     /* set true if normalize Hier-Clustering
                                    * dataV[h] by dataV[iDataV]
                                    * else norm by HP[h] msListE[h].maxRI */
    useMedianForKmeansClusteringFlag= false;  /* use median instead of means of
                                               * clusters in K-means clustering. */
    useHighRatiosFilterFlag= false; /* "Use highest ratios filter"*/
    useLowRatiosFilterFlag= false; /* "Use highest ratios filter"*/
    spotIntensFilterFlag= false;   /* filter genes by spotIntensity threshold*/
    intensFilterFlag= false;       /* filter genes by intensity threshold*/
    ratioFilterFlag= false;        /* filter genes by ratio threshold*/
    ratioCy3Cy5FilterFlag= false;  /* filter genes by Cy3/Cy5 ratio threshold*/
    geneClassMbrFilterFlag= true;  /* filter genes by GeneClass mbrshp*/
    tTestXYfilterFlag= false;      /* filter genes by HP-X,HP-Y t-test*/    
    tTestXYsetsFilterFlag= false;  /* filter genes by HP_XY sets t-test */
    KS_TestXYsetsFilterFlag= false; /* filter genes by HP_XY sets KS-test */
    F_TestOCLFilterFlag= false;    /* filter genes by cur. OCL (Ordered Cond List) F-test */
    useSpotCVfilterFlag= false;    /* filter genes by Spot CV */
    clusterHP_EfilterFlag= false;  /* cluster HP-E filter */
    useDiffFilterFlag= false;      /* filter genes by Diff(HP-X,HP-Y)*/
    useEPrawIntensValFlag= false;  /* use RI EP ratios in GENE REPORTS*/
    addExprProfileFlag= false;     /* set if want EP data in GENE RPTS*/
    addHP_XYstatFlag= false;       /* set for HP_XY stats in GENE RPTS*/
    addOCLstatFlag= false;         /* set for OCL statistics in GENE RPTS */
    usePosQuantDataFlag= false;    /* filter genes with intensity>0.0 */
    useGoodSpotDataFlag= false;    /* filter genes with Good Spot (QualChk) data */
    useOnlyGenesWithNonZeroDensityFlag= false; /* filter by genes with non-zero intensity */
    useDetValueSpotDataFlag= false; /* use spot detection value */
    
    showEGLflag= false;	           /* show edited gene list */
    useHPxySetDataFlag= false;     /* use mean msListX/Y data else msX/Y data */         
    useMeanHPeListDataFlag= false; /* use mean HP-E 'list' else cur HP sample data */
    
    autoStateScrPopupFlag= true;   /* Auto state-scroller popup window*/
    clusterOnFilteredCLflag= true; /* cluster on Filtered/All genes*/
    
    useClusterDistFlag= false;     /* cluster distance thresholding*/
    useSimGeneClusterDispFlag= false; /* show cluster of similar genes to current gene */
    useClusterCountsDispFlag= false; /* show gene clusters count */
    useHierClusterDispFlag= false; /* draw hierarchical clusters */
    useKmeansClusterCntsDispFlag= false; /* draw K-means Nodes */
    useLSQmagNormFlag= false;      /* norm geneEPvect[] to 1 for clustering */
    useClusterDistCacheFlag= true; /* can save memory if really needed */
    useShortClusterDistCacheFlag= false; /* use short else float cluster cache*/
    useRatioHistCLflag= false;     /* filter genes on Ratio hist. bin*/
    useIntensHistCLflag= false;    /* filter genes on intens. hist bin*/
    useEditedCLflag= false;        /* filter genes on edited list */
    useGoodGeneCLflag= false;      /* filter by genes in good genes */
    useReplicateGenesFlag= false;  /* filter by genes with replicates */
    useCVmeanElseMaxFlag= false;   /* use mean of CV else use max */
    
    /* [3] Overide the parameter values when doing Menu driven RESET */
    if(cfgDB!=null)
      cfg.getStateParamValues(); 
  } /* resetDefaultstate */
  
  
  /**
   * init() - allocate,clear state, read PARAMs, build GUI and start things off.
   * Clear state, read PARAMs, build GUI and start things off.
   * This code can be called repeatedly to restart MAExplorer
   * on a difference saCodeBase.
   * @see ArrayScroller#repaint
   * @see CompositeDatabase
   * @see CompositeDatabase#recalcNorms
   * @see CompositeDatabase#setOBJtoGID
   * @see Config#getStateParamValues
   * @see Config#setupFullpathFilenames
   * @see Config#setupPathBase
   * @see Condition
   * @see DrawPseudoImage
   * @see EditedGeneList
   * @see EventMenu
   * @see EventMenu#setPlotState
   * @see Filter
   * @see Filter#computeWorkingGeneList
   * @see FileIO
   * @see Gene
   * @see GeneClass
   * @see GeneClass#assignGeneNames2StaticGeneList
   * @see GeneClass#setCurrentGeneClassName
   * @see GeneClass#setupSpecialGeneLists
   * @see GeneClass#updateStdGenePartitions
   * @see GeneGeneDist
   * @see GeneList
   * @see GetParams
   * @see GipoTable
   * @see GipoTable#copyGBCTdataToStaticGeneDB
   * @see GipoTable#copyGeneNamesTableFile
   * @see GridCoords
   * @see MaeJavaAPI
   * @see MaeJavaAPI#initStateAndMJAchildren
   * @see Maps
   * @see MaInfoTable
   * @see MenuBarFrame
   * @see MenuBarFrame#callAllStartupPlugins
   * @see MenuBarFrame#enableMenus
   * @see MenuBarFrame#redoMenuBar
   * @see MenuBarFrame#setTitle
   * @see PopupRegistry
   * @see SamplesTable
   * @see SampleSets
   * @see SamplesTable#readListOfSampleNames
   * @see ScrollableImageCanvas#repaint
   * @see StageNames
   * @see StageNames#getMenuTreeFromSamplesTable
   * @see SpotData
   * @see SpotFeatures
   * @see Statistics
   * @see Table#freeTable
   * @see UserState
   * @see Util
   * @see Util#chkDirFinalSeparator
   * @see Util#getRatioStr
   * @see Util#popupDryrotMsgsAndQuit
   * @see Util#popupViewer
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see #doAnyLoginsRequired
   * @see #clearDynamicState
   * @see #destroy2
   * @see #destroyStaticState
   * @see #getParameter
   * @see #readListOfSampleDataFiles
   * @see #repaint
   * @see #resetDefaultstate
   */
  public void init()
  { /* init */    
    /* [1] Reset and allocate variables */
    previousStartupState= startupFileExistsFlag;
    reuseGUIframeFlag= (mbf!=null); /* make new GUI FIRST TIME ONLY */
    
    if(!reuseGUIframeFlag)
    { /* INITIAL GUI: clear for initial database */
      clearDynamicState();       /* set all vars to initial values */
      resetDefaultstate();       /* Reinitialize global user state */
    } 
    else
    { /* REUSE GUI: reset specific items for new database */
      destroyStaticState();      /* destroy old classes */
    }
    /* [1.1] Allocate HP lists */
    iSampleName= new String[MAX_HYB_SAMPLES+1];  /* HP name from
                                                  * "image*" [1:iHPnbr] */
    iImageFile= new String[MAX_HYB_SAMPLES+1];   /* GIF image files */
    mapHPtoMenuName= new int[MAX_HYB_SAMPLES+1]; /* map snHPName[]
                                                  * to iHPname[] */
    snHPName= new String[MAX_HYB_SAMPLES+1];     /* HP stage name [1:snHPnbr]*/
    snHPSampleID= new String[MAX_HYB_SAMPLES+1]; /* free text sample name*/
    snHPDatabaseFileID= new String[MAX_HYB_SAMPLES+1]; /* (opt) DB file ID */
    snImageFile= new String[MAX_HYB_SAMPLES+1];  /* image files to read */
    snHPMenuText= new String[MAX_HYB_SAMPLES+1]; /* for building menus */
    snHPFullStageText= new String[MAX_HYB_SAMPLES+1]; /* for build. menus*/
    snPrjName= new String[MAX_HYB_SAMPLES+1];    /* Project name */
    snNeedLogin= new boolean[MAX_HYB_SAMPLES+1]; /* requires login */
    snSwapCy5Cy3Data= new boolean[MAX_HYB_SAMPLES+1]; /* swap (Cy3,Cy5)*/
    
    mReady= false;               /* disable event handlers until finished*/
    
    /* Default Configuration file may overwrite with PARAM or .mae param. */
    configFile= "MaExplorerConfig.txt"; 
    
    /* [1.2] setup files since need them right away. */
    fio= new FileIO(this);      /* for file and URL I/O */
    util= new Util(this);       /* for I/O etc */
    scrWidth= Toolkit.getDefaultToolkit().getScreenSize().width;
    scrHeight= Toolkit.getDefaultToolkit().getScreenSize().height;
    
    /* [1.3] Get codebase and file base addresses */
    javaHome= (!isAppletFlag) ? System.getProperty("java.home") : "";
    osName= System.getProperty("os.name");
    if(!isAppletFlag)
      fileSeparator= System.getProperty("file.separator");
    else
      fileSeparator= "/";
    dynFileSeparator= fileSeparator; /* default. overide if Stand-alone
                                      * access to "http://" server */
    
    if(!isAppletFlag)
    { /* get the stand-alone application path */
      /* NOTE: must set the path for JWS on PC directly from an
       * additional -jws <mae-dir>. This sets the bin dir instead of
       * the dir where it is run, runs fine via MS-DOS prompt.
       * So must set path in prj:edit settings within jws.
       */
      if(this.mja!=null)
        this.mja= new MaeJavaAPI(this);  /* [JE 12-17-01]
                                          * Must be here - even
                                          * if plugins not active */
      
      if(defDir!=null)
        codeBase= defDir; /* must be set within JWS prj:edit menu*/
      else  
        codeBase= new String(new File("").getAbsolutePath()); /* vis OS */
      
      if(!codeBase.startsWith("file:/"))
        codeBase= "file:/" + codeBase;   /* force it to local file*/
      
      /* Note: convention is that the defDir is
       * <someDBpath>/MAE/theMaeFile.mae.
       * Check to see if defDir ends with "/MAE/" variants. If it
       * does, then remove it to compute the actual defDir.
       */
      if(codeBase.toUpperCase().endsWith(fileSeparator+"MAE"+
         fileSeparator))
      {
        int
        last= codeBase.length() - 4;
        codeBase= codeBase.substring(0,last);
      }
      /*
       if(CONSOLE_FLAG)
         System.out.println("MAE-s.a.0 defDir='"+defDir+
                            "'\n codeBase='"+codeBase+
                            "'\n defStartupFile='"+defStartupFile+"'");
      */
    } /* get the stand-alone application path */
    else
      codeBase= this.getCodeBase().toString();      /* Applet way */
    
    /* [1.4] Get the PARAMs and parse them */
    /* Read the startup DB file and invoke reading new HPs */
    us= new UserState(this, userName);   /* create user state */
    if(!isAppletFlag)
    { /* get the stand-alone application path */
      /* NOTE: overide by analyzing the argv[] list */
      /* [1.4.1] Set up the mae.(fileBasePath, isWorkingMAE) */
      Config.setupPathBase(this);
      
      codeBase= fileBasePath;       /* NOTE: redefining the codebase! */
      startupFile= defStartupFile;
      startupDir= fileBasePath;
      
     /* [1.4.2] Clean up the directories so that they all end
      * with fileSeparators.
      * Note: Mac Bug found and fixed by Jai Evans 10/12/00.
      */
      defDir= util.chkDirFinalSeparator(defDir,fileSeparator);
      fileBasePath= util.chkDirFinalSeparator(fileBasePath,fileSeparator);
      codeBase= util.chkDirFinalSeparator(codeBase,fileSeparator);
      startupDir= util.chkDirFinalSeparator(startupDir,fileSeparator);
      
      /* [1.4.3] Start the Error log */
      fio.logMsg("\n--MAE error log ["+date+"]--\n",
                 startupDir+"MAEerr.log",
                 true /* newFileFlag */, true /* appendFlag */);
      int fullPathIdx= startupFile.indexOf(":");
      if(fullPathIdx!=-1)
      { /* redefine the startupDir and startupFile */
        int
          lastSlash= startupFile.lastIndexOf(fileSeparator);
          startupDir= startupFile.substring(0,lastSlash+1);
          startupFile= startupFile.substring(lastSlash+1);
      }
      cacheDir= startupDir+"Cache"+fileSeparator; /* Standalone /Cache path */
      /*
      if(CONSOLE_FLAG)
        System.out.println("MAE-s.a.1 startupDir='"+startupDir+
                           "'\n startupFile='"+startupFile+
                           "'\n defDir='"+defDir+
                           "'\n prjListFile="+mae.prjListFile+
                           "'\n codeBase='"+codeBase+
                           "'\n cacheDir='"+cacheDir+"'");
       */
      
     /* Disable FIO caching for stand-alone. If enabled, it first
      * checks the /Cache directory for all http:// codebase files
      * and if not in the cache, it saves the file in the
      * cache after reading it from the Web.
      * Set default stand-alone .mae params.
      */
      saCodeBase= codeBase;         /* default is where we are */
      enableFIOcachingFlag= false;
      useWebDBflag= false;          /* using disable Web DB */
      
      /* Try to change these stand-alone .mae params */
      if(!us.readMAEstartupFile(defDir, startupFile))
      { /* There is no startup file */
        startupFileExistsFlag= false;
        //return;          /* dead! */
      }
      else
        startupFileExistsFlag= true;
      chkIfCache();     /* check if set up to cache from Web DB. */
      
      /*
      if(CONSOLE_FLAG)
        System.out.println("MAE-s.a.2 cacheFIOflag="+cacheFIOflag+
                           " enableFIOcachingFlag="+ enableFIOcachingFlag+
                          " fileSeparator='"+fileSeparator+"'"+
                           " dynFileSeparator='"+dynFileSeparator+"'"+
                           "\n  codeBase="+codeBase+
                           "\n  saCodeBase="+saCodeBase);
       */
      if(useWebDBflag && saCodeBase.startsWith("http://"))
      { /* overide codebase */
        codeBase= saCodeBase;   /* Overide it */
        
        /* If enabled caching, first try to read all files from
         * cache BEFORE getting from Web server.
         */
        Config.setupPathBase(this);  /* set to new codebase */
        /*
          if(CONSOLE_FLAG)
            System.out.println("MAE-s.a.3 cacheFIOflag="+cacheFIOflag+
                               " cacheDir="+cacheDir+
                               "\n  codeBase="+codeBase+
                               "\n  fileBasePath="+fileBasePath+
                               "\n  maeServerBase="+maeServerBase
                              );
         */
      } /* overide codebase */
    } /* stand-alone application way */
    else
    { /* Applet way */
      String overideConfig= getParameter("configFile");
      if(overideConfig!=null)
        configFile= overideConfig;
      
      /* Set up the mae.(fileBasePath, isWorkingMAE) */
      defSAprjName= "";
      defSAwebAddr= "";
      Config.setupPathBase(this);
      startupDir= codeBase;
    } /* Applet way */
    
    /* [2] Finish initialization */
    mReady= false;            /* disable event handlers until finished*/
    if(runT!=null)
      runT.stop();
    gp= new GetParams(this);
    
    /* [2.1] Set to 0 if debugging, -1 otherwise for building menus
     * while developing code.
     */
    if(DEBUG || (DBUG_STANDALONE && !isAppletFlag))
      LECBdebug= 0;             /* show menu item */
    else
      LECBdebug= -1;            /* DON'T show menu item */
    //if(CONSOLE_FLAG)
    //  fio.logMsgln("MAE: LECBdebug="+LECBdebug);
    
    /* Get new documentation if on file system or any server */
    docCodeBase= "http://maexplorer.sourceforge.net/MaeRefMan/";
    
    /* [2.2] If enabled, notify the little "Msg" frame window
     * that we are reading.
     */
    noMsgReportFlag= gp.setDefParam("noMsgReporting",false);
    if(isAppletFlag && !noMsgReportFlag)
      util.popupViewer(this.getCodeBase(),
      "maeAppMsgReadingDB.html", "Msg");
    
    /* [2.3]  Analyze codeBase and setup fileBasePath */
    cfg= new Config(this, configFile); /* get file&URL name configs*/
    
    /* Overides MACROS but not Applet PARAMs */
    String
    fullConfigPath= fileBasePath+"Config"+dynFileSeparator+
    configFile;
    
    cfgDB= new ConfigTable(this,fullConfigPath);
    if(cfgDB.tRows==0 && startupFileExistsFlag)
    { /* problem with file system */
      System.out.println("Can't read Config file ["+fullConfigPath+"]");
      destroy2();
      return;
    }
    
    /* [2.3.1] Set state PARAM parameters */
    if(startupFileExistsFlag)
      cfg.getStateParamValues();
    browserTitle= maeTitleVer + " - " + cfg.dbSubset;
    
    /* [2.3.2] Set global isZscore flag for doing different
     * Zdiff instead of ratio calcs.
     */
    isZscoreFlag= (normByZscoreFlag || normByLogMedianFlag ||
                   normByZscoreMeanStdDevLogFlag ||
                   normByZscoreMeanAbsDevLogFlag);
     
    /* [2.3.3] Can't do gang scrolling if only 1 field */
    gangSpotFlag= (cfg.maxFIELDS>1 && !useRatioDataFlag);
    
    /* [2.3.4] Reset flags based on # or initial HPs */
    if(maxPreloadImgs<1)
    { /* set options when no samples */
      useMouseOverFlag= false;   /* so easier to see msgs */
    } /* set options when no samples */
    
   /* [2.3.4] Analyze useRatioDataFlag and useCy5OverCy3Flag
    * to set print string as "F1/F2", "Cy3/Cy5" or "Cy5/Cy3", etc.
    * string.
    */
    reportRatioStr= Util.getRatioStr();
    
    /* [2.4] Allocate mapping tables which are populated elsewhere. */
    mp= new Maps(this);
    gene= new Gene(this);   /* setup here so get mae instance to get map */
    
    /* [2.5] Set up grid coordinate mapper */
    grcd= new GridCoords(this);
    
    /* [2.6] Set up named scatterplot overlay maps DB */
    olmap= new OverlayMap(this,25); 
    
    //if(NEVER)
    // isWorkingMAE= true;  /* *** DEBUG to emulate working server *** */
    
    /* [2.7] Add the path to file names.
     * Setup the full file paths by adding the base path
     * the to file names.
     * If the codeBase starts with "http://" then use codeBase
     * else use the fileBasePath.
     * The file names are defined as follows:
     *  1. use DEF_xxxx in Config class.
     *  2. Overide the filenames if any in the MaExlorer.cfg file
     *     using the ConfigTable class.
     *  3. Overide the filenames using PARAM values if any.
     */
    if(startupFileExistsFlag)
      cfg.setupFullpathFilenames();
    
    if(CONSOLE_FLAG)
    {
      fio.logMsgln("");
      fio.logMsgln("---------------------------");
      fio.logMsgln("*** STARTING MAExplorer ***");
      fio.logMsgln("---------------------------");
      fio.logMsgln("browserTitle="+browserTitle);
      fio.logMsgln("verStr="+verStr);
      fio.logMsgln("date="+date);
      fio.logMsgln("Screen (widthXheight)=(" +
                   scrWidth + " X " + scrHeight + ")");
      fio.logMsgln("osName="+osName);
      fio.logMsgln("codeBase="+codeBase);
      fio.logMsgln("prjListFile="+prjListFile);
      fio.logMsgln("defSAprjName=" + defSAprjName);
      fio.logMsgln("defDir=" + defDir);
      fio.logMsgln("defStartupFile="+defStartupFile);
      fio.logMsgln("defSAwebAddr=" + defSAwebAddr);
      fio.logMsgln("startupDir=" + startupDir);
      fio.logMsgln("fileBasePath=" + fileBasePath);
      fio.logMsgln("maeServerBase="+maeServerBase);
      fio.logMsgln("isWorkingMAE=" + isWorkingMAE);
      fio.logMsgln("fileBasePath="+fileBasePath);
      fio.logMsgln("sampDBfile="+cfg.sampDBfile);
      if(cfg.maInfoFile!=null)
        fio.logMsgln("maInfoFile="+cfg.maInfoFile);
      fio.logMsgln("gipoFile="+cfg.gipoFile);
      fio.logMsgln("maxPreloadImgs="+maxPreloadImgs);
      
      //System.getProperties().list(System.out);
    }
    
    /* [3] Start reading database files and build structures. */
    pur= new PopupRegistry(this);       /* holds popup registry */
    
    /* [3.1] Read the list of hybrdizations for the entire database.
     * This contains the Database_File name (no .gif extension).
     */
    if(startupFileExistsFlag)
      sampDB= new SamplesTable(this, cfg.sampDBfile);
    
    /* [3.2] Get a list of all hybridized sample images.
     * 1. get all images specified in the PARAM and these are
     *    candidates for loading the .quant DB file. They are named
     *    "image1", "image2", ..., "imageMAX_HYB_SAMPLES".
     * 2. Then, add names to the list (primarily for loading later
     *    from the menus). These names will come from the
     *    Samples.txt database (column Database_File).
     *    Don't add a name if it is already in the list.
     */
    if(startupFileExistsFlag)
    {
      hps= new SampleSets(this);       /* maintains HP-X, HP-Y & HP-E sets*/
      sampDB.readListOfSampleNames();  /* get names for Sample menuand setup the
                                        * mae.sn????[1:mae.snHPnbr] arrays */
    }
    
    /* [3.2.1] If they did not specify the # or HPs to preload,
     * but did specify a set of {image1,image2,...), then
     * use that count as the number or preload.
     */
    if(maxPreloadImgs==0 && iHPnbr>0)
      maxPreloadImgs= iHPnbr;
    
    /* [3.3] Check if any samples require a Login and then
     * do it now before waste more time loading other stuff
     * which is not used since the Login failed.
     */
    if(!doAnyLoginsRequired())
    {
      killAppletFlag= true;
      Util.popupAlertMsgLine("Failed - aborting MAExplorer.");
      return;
    }
    
    /* [3.3.1] Don't bother reading if a fatal error */
    if(Util.popupDryrotMsgsAndQuit())
      return;
    
    /* [3.4] Setup data arrays for (DevClass,Source,Stage)
     * submenu system. If it can't find the entries, it sets
     * up default values.
     */
    if(startupFileExistsFlag)
    {
      sg= new StageNames(this);	   /* for setting up menu HP staging tree*/
      sg.getMenuTreeFromSamplesTable(this);
    }
    
    /* [4] Create other objects required */
    if(startupFileExistsFlag)
      gct= new GeneClass(this);  /* Read Gene Class table for sets
                                 * of genes including Gene_Names
                                 * which have to install in static
                                 * gene list AFTER read GeneTable.
                                 * We must read this now to help setup
                                 * menus with Gene_Class names */

    /* [4.1] Get the extra .info data for each HP in the database
     * which is kept in "MaExplorerInfo.txt".
     * Not: this may be null in which case, no report is made.
     */
    infoDB= (cfg.maInfoFile!=null && cfg.maInfoFile.length()>0)
               ? new MaInfoTable(this, cfg.maInfoFile)
               : null;
    
    /* [5] Read GIPO Table mapping FGRC to Master ID data, etc.
     * Create the STATIC gene lists and additional Maps class
     * mapping tables.
     */
    //Util.printCurrentMemoryUsage("Before new GipoTable");
    if(startupFileExistsFlag)
      gipo= new GipoTable(this, cfg.gipoFile);
    //Util.printCurrentMemoryUsage("After new GipoTable - before freeTable");
                       
     
    /* [6] Now build the GUI and initialize the state */
    if(pWidth<=0)
      pWidth= PSEUDOIMG_WIDTH;
    if(pHeight<=0)
      pHeight= PSEUDOIMG_HEIGHT;
    
    if(!reuseGUIframeFlag || !previousStartupState)
    { /* make new GUI FIRST TIME ONLY or if there was no data in previous one! */
      mbf= new MenuBarFrame(this);
    }
    else
    { /* delete and GC old one, then make new menu bar for the new database */
      mbf.redoMenuBar();             /* [DEBUG] redoing it makes it hang! */
      mbf.setTitle(browserTitle);
    }
    
    /* [6.1] Add other classes and init the state */
    sd= new SpotData(this);            /* spot data object */
    stat= new Statistics(this);	 /* (non)parametric stats */
    fc= new Filter(this,mp.maxGenes);  /* Filter gene operations */
    egl= new EditedGeneList(this);     /* make editedCL gene list */
    
    /* [6.2] Setup the current gene class. Default if necessary */
    String
    defGCname= (GeneClass.curGeneClassName==null)
                 ? "ALL GENES"
                 : GeneClass.curGeneClassName;
    if(startupFileExistsFlag)
      gct.setCurrentGeneClassName(defGCname); /* sets default and
                                               * mae.fc.gcMemberCL*/
    
    /* [6.3] Add GUI event handlers */
    em= new EventMenu(this);
    
    /* [7] Final initialization of other objects we will need. */
    dwPI= new DrawPseudoImage(this);          /* create pseudo image canvas */
    
    /* [7.1] Assign Gene Class "Gene_Name" to STATIC gene list
     * now that the gene lists exist in the Maps.midStaticCL list.
     * Note that GeneClass will read the table locally - it is NOT
     * an extension of Table because it reads the table sometime AFTER
     * it is created.
     */
    if(startupFileExistsFlag)
    {
      gct.assignGeneNames2StaticGeneList();
      if(gct.gincDB!=null)
        gct.gincDB.freeTable();               /* so can garbage collect it */
    }
    
    /* [7.2] Copy Gene associated data from GenBank
     * eg dbESTs, GenBankAccs, etc. into fields of existing
     * static gene structures
     */
    if(startupFileExistsFlag)
    {
      gipo.copyGBCTdataToStaticGeneDB();
      gipo.copyGeneNamesTableFile(); /* use data to update static Genes */
    }
    
    /* [7.3] Copy the Gene names associated with Master IDs
     * (i.e. use the "Unigene_cluster_name" as the Gene Name),
     * the "Unigene_Cluster_ID", and "Gene_Class" to
     *  update static Gene database.
     */
    if(startupFileExistsFlag)
      gct.updateStdGenePartitions(); /* update builtin default gene classes*/
    
    /* [8] Create gene and cnondition (HP) sets and lists */
    clLst= new GeneList(this,mp.maxGenes,"clLst",
                        true);         /* setup Gene sets w/no data */
    cdList= new Condition(this,null,true);  /* set up H.P. condition sets */
    
    /* [9] Create multi-HP class for analyzing research genetics
     * tables, ratio histograms, etc.
     */
    if(startupFileExistsFlag)
      cdb= new CompositeDatabase(this);
    //Util.printCurrentMemoryUsage("After new CompositeDatabase");
    if(Util.popupDryrotMsgsAndQuit())
      return;
    
    /* [10] Setup for spot features calls */
    if(startupFileExistsFlag)
      sf= new SpotFeatures(this);          /* Spot feature info */
    
    /* [11] Create MaHybridSample objects.
     * Read in maxPreloadImgs image to populate the data structures.
     * Read in the others on demand.
     */
    if(startupFileExistsFlag)
    { /*read data samples data */
      //Util.printCurrentMemoryUsage("Before readListOfSampleDataFiles()");
      boolean readOKflag= readListOfSampleDataFiles();
      //Util.printCurrentMemoryUsage("After readListOfSampleDataFiles()");
      if(!readOKflag)
        return;
    }
    
    //if(CONSOLE_FLAG)
    // return;          /* -------------[DEBUG]----------- */
    
    /* [11.1] set default hybridized sample to ms, and set ArrayScroller
     * to first ms object which will be the one displayed.
     */
    /* Reset the default GeneClasses */
    if(gct!=null)
      gct.setupSpecialGeneLists();
    if(ms!=null)
      updatePseudoImgFlag= true;
    
    /* [11.2] check on default array image and mode to draw it. */
    if(ms!=null)
    {
      if(iHPnbr==1)
      {
        plotImageMode= (useRatioDataFlag)
                         ? PLOT_PSEUDO_F1F2_RYG_IMG : PLOT_PSEUDOIMG;
        EventMenu.setPlotState((useRatioDataFlag)  
                                   ? mbf.miPMplotPseudoHP_F1F2_RYGImg
                                   : mbf.miPMplotPseudoImg);
      }
      else if(iHPnbr>1)
      { /* switch to Ratio HP-X/HP-Y  display */
        plotImageMode= PLOT_PSEUDO_HP_XY_IMG;
        EventMenu.setPlotState(mbf.miPMplotPseudoHP_XYImg);
      }
      updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
    }
    if(Util.popupDryrotMsgsAndQuit())
      return;
    
    /* [12] Create sorted GeneName and Master_ID lists for Guesser */
    Util.showMsg3("Sorting gene names and IDs ...", Color.white, Color.red );
    if(startupFileExistsFlag)
      PopupGeneGuesser.sortGeneNameAndCloneIDLists(this);
    
    /* [13] Default to Normalization method */
    if(startupFileExistsFlag)
      cdb.recalcNorms(normName, true);
    
    /* [14] Create default GeneBitSets' in userBS[] list. */
    if(startupFileExistsFlag)
      gct.setupDefaultGeneClass_GeneBitSets();
    if(Util.popupDryrotMsgsAndQuit())
      return;
    updatePseudoImgFlag= true;
    if(startupFileExistsFlag)
      fc.computeWorkingGeneList();
    
    /* [15] Garbage Collect whatever we can. */
    //Util.printCurrentMemoryUsage("Before G.C. before enable menus");
    System.gc();
    //Util.printCurrentMemoryUsage("After G.C. before enable menus");
    
    /* [16] Set up Gene-Gene Distance object in case need it for clustering.*/
    if(startupFileExistsFlag)
      ccd= new GeneGeneDist(this);  /* Gene-Gene distances */
    
    /* [17] Force default current gene to ULHC if any samples exist */
    if(startupFileExistsFlag)
      cdb.setOBJtoGID(0,ms); /* set the current gene to the specifid GID */
    
    /* [17.1] Initialize the scroller use counters based on the State flags.
     * NOTE: plugins will modify the counters separated through other
     * calls.
     */
    stateScr.initUserFilterScrollerCounters();
    stateScr.regenerateScrollers(false);
    
    /* [17.2] Create cluster genes instance */    
    clg= new ClusterGenes(this);
    
    /* [18] If there are any plugins that must be called at MAExplorer
     * Startup, then do it now.
     * [TODO] These are indicated by cfg.sPluginCallAtStartup[] being "TRUE".
     * first init the children classes of the MaeJavaAPI.
     */
    if(mja!=null)
      mja.initStateAndMJAchildren(this);
    mbf.callAllStartupPlugins();         /* [TODO] invoke startup plugins */
        
    /* [18.1] if debugging NormalizationPlugins using direct class
     * then create GenNormalizationPlugin instance 
     */
    if(DBUG_GENERIC_NORM_PLUGIN)
      gnp= new GenNormalizationPlugin(mja, false);
    else
      gnp= null;   /* NOT debugging generic normalization plugin */  
     
    /* [19] Add RLO R scripts (Plugins | RLO methods) submenu tree to if RLOs exist. */
    mbf.addRLOsubmenuTree();
    
    /* [20] All DONE, enable the world! */
    Util.showMsg("");
    Util.showMsg3("Enabling menus...", Color.white,Color.red );
    mbf.enableMenus((maxPreloadImgs==0)
                       ? "basic" : "all"); /* enable all menus - this takes
                                            * longer on PCs than on UNIX */
    
    if(Util.popupDryrotMsgsAndQuit())
      return;
    
    /* [21] Notify the user that it is ok to click on array image. */
    madeChangesFlag= false;  /* reset so no DB changes were made */
    mReady= true;                /* enable event handlers */
    String
    sMsg1= (maxPreloadImgs>=1)
               ? "click on a gene to query database."
               : "select a startup database.";
    Util.showMsg1( "Ready - "+ sMsg1,  true,
                  Color.green,   /* foreground */
                  Color.white);  /* background */
    if(maxPreloadImgs>=1)
    { /* data exists */
      Util.showMsg2( " " );
      Util.showMsg3( " " );
    }
    else
    { /* make them get data */
      Util.showMsg2( "   First select database 'File' menu | 'Open ... DB',");
      Util.showMsg3( "   Then select one of the .mae startup files.");
    }
    
    /* [22] Notify the little "Msg" frame window that we are Ready */
    if(isAppletFlag && !noMsgReportFlag)
      util.popupViewer(this.getCodeBase(), "maeAppMsgReady.html", "Msg");
    
    updatePseudoImgFlag= true;   /* update pseudo image */
    repaint();
    is.repaint();
    is.siCanvas.repaint();
  } /* init */
  
  
  /**
   * start() - (re)create GUI if needed, read PARAMs & start run thread.
   */
  public void start()
  { /* start */
    if(runT==null)
    { /* start initialization thread */
      runT= new Thread(this);
      runT.start();
    }
  } /* start */
  
  
  /**
   * stop() - stop the thread.
   */
  public void stop()
  { /* stop */
    if (runT != null)
      runT.stop();
    runT= null;
  } /* stop */

  
  /**
   * run() - main loop which does checking for various things
   * [1] Check if want to repaint based on spot list change.
   * [2] Check to see if need to garbage collect
   * @see FileIO#logMsgln
   * @see PopupRegistry#checkStringPopupTimeouts
   * @see ScrollableImageCanvas#repaint
   * @see Util#sleepMsec
   * @see #destroy
   * @see #stop
   */
  public void run()
  { /* run */
    Runtime rt= Runtime.getRuntime();
    long lastPctFreeMem= 101;    /* keep lst memory size so minimize G.C. */
   
    dieFlag= false;
    runLoopDelayMsec= 1000;
    
    while( runT != null && !dieFlag)
    { /* infinite run loop */
      /* [1] take a short nap...for 1 second */
      Util.sleepMsec(runLoopDelayMsec);
      
      /* [2] Check timeouts for all active popups and kill any that
       * exceed the time. We increment the times here.
       */
      if(pur!=null)
        pur.checkStringPopupTimeouts(runLoopDelayMsec);
      
      /* [2.1] If flickering, then switch the name of the sample
       * each time around the loop.
       */
      if(flickerXYflag)
      {
        ms= (ms==msX) ? msY : msX;
        updatePseudoImgFlag= true;
      }
      
      /* [2.2] Check if need to do a repaint() */
      if(updatePseudoImgFlag && is.siCanvas!=null)
        is.siCanvas.repaint();
      
      /* [3] Check if need to garbage collect after an operation. */
      if(doGCflag)
      { /* see if need to GC */
        System.runFinalization();
        long totMem= rt.totalMemory();
        long freeMem= rt.freeMemory();
        int pctFreeMem= (int)(100*freeMem/totMem);
        
        if(fio!=null &&
          (lastPctFreeMem!=pctFreeMem && pctFreeMem <30)&&
          (lastPctFreeMem<(pctFreeMem+10)))
        { /* go GC to free some up */
          fio.logMsgln("**** GC: pctFree="+pctFreeMem+ "%, totMem="+
                       Util.cvd2s((totMem/1000000.0),2) + "Mbytes");
          System.runFinalization();
          System.gc();
          doGCflag= false;
          
          /* Recompute last pct free AFTER GC so don't GC
           * too often.
           */
          totMem= rt.totalMemory();
          freeMem= rt.freeMemory();
          pctFreeMem= (int)(100*freeMem/totMem);
          lastPctFreeMem= pctFreeMem;
        } /* go GC to free some up */
      } /* see if need to GC */
      
      
      /* [4] Kill MAExplorer if anyone asks us to. */
      if(killAppletFlag)
        break;            /* kill run Thread to kill applet */
    } /* infinite run loop */
    
    if(CONSOLE_FLAG && fio!=null)
      fio.logMsgln("Quiting run()");
    
    destroy2();           /* kills this applet */
    stop();
  } /* run */
  
  
  /**
   * paint() - generate viewable scrollable pseudoarray image with overlays.
   * @param g is graphics context
   * @see ArrayScroller#syncScrollerAccess
   */
  public void paint(Graphics g)
  { /* paint */
    if(!mReady)
      return;
    
    if(is!=null)
      is.syncScrollerAccess();
  } /* paint */
  
  
  /**
   * update() - redraw graphics
   * @param g is graphics context
   */
  public void update(Graphics g)
  { /* update */
    if(!mReady)
      return;
    paint(g);
  } /* update */
  
  
  /**
   * doAnyLoginsRequired() - do any Web server logins required for PARAM images
   * that access a Web server database of primary MAExplorer data.
   * @return true if ok.
   * @see Util#popupDryrotMsgsAndQuit
   * @see Util#showMsg
   * @see UserState#validateUserProject
   * @see #logDRYROTerr
   */
  private boolean doAnyLoginsRequired()
  { /* doAnyLoginsRequired */
    int maxToRead= Math.min(maxPreloadImgs, iHPnbr);
    
    /* [1] Create mapping index from image# names to Samples
     * SN names lists.
     */
    for(int i= 1; i<=maxToRead; i++)
    { /* create H.P. objects */
      boolean foundIt= false;
      String imgHPname= iSampleName[i];
      
      for(int s= snHPnbr; s>=1; s--)
      { /* create map of HP names by index */
        String snHPname= snHPName[s];
        
        if(snHPname.equals(imgHPname))
        { /* map it */
          mapHPtoMenuName[i]= s;
          foundIt= true;
          break;
        }
      }
      if(!foundIt)
      {
        logDRYROTerr("[MAE-DALR] - Bad PARAM 'image" +i + "='" +
                     imgHPname+"'\nnot in SamplesDB ["+
                     cfg.sampDBfile+"]");
        Util.showMsg("HP["+imgHPname+"] not in SamplesDB ["+
                     cfg.sampDBfile+"]");
        mapHPtoMenuName[i]= -1;  /* mark as NOT FOUND */
      }
    } /* create H.P. objects */
    
    /* Don't bother reading if a fatal error */
    if(Util.popupDryrotMsgsAndQuit())
      return(false);
    
    /* [2] Check if logins required and do it for all requested HPs */
    for(int i= 1; i<=maxToRead; i++)
    { /* check any logins required */
      int hpNbr= mapHPtoMenuName[i];
      /* index of snHPName[] corresponding to iHPname[] entry */
      String snHPprojectName= snPrjName[hpNbr];
      boolean snHPneedLogin= snNeedLogin[hpNbr];
      /*
      if(CONSOLE_FLAG)
        fio.logMsgln("RLHPDF iHPname[i="+i+"]="+ iHPname[i]+
                     "\n    snHPprojectName="+snHPprojectName+
                     ", snHPneedLogin="+snHPneedLogin);
      */
      if(snHPneedLogin)
      {
        boolean loginOK= us.validateUserProject(userName, userPasswd,
                                                snHPprojectName);
        if(loginOK)
          snNeedLogin[hpNbr]= false;   /* so don't do this again*/
        else
        { /* can't login */
          isLoggedInFlag= false;
          return(false);
        }
      }
    } /* check any logins required */
    
    isLoggedInFlag= true;
    return(true);
  } /* doAnyLoginsRequired */
  
  
  /**
   * readListOfSampleDataFiles() - populate msList[1:nHP] data structures
   * by reading the data files up to maxPreloadImgs. If the user wants more,
   * then they will load them manually.
   * @return true if successful in reading ALL of the samples.
   * @see Condition#addCondList
   * @see GetParams#setDefParam
   * @see MenuBarFrame#setHP_XYlabels
   * @see MaHybridSample
   * @see SampleSets#addHPtoDB
   * @see SampleSets#setHPlistsFromPARAM
   * @see Util#popupDryrotMsgsAndQuit
   * @see UserState#restoreCondLists
   * @see #createSample
   */
  boolean readListOfSampleDataFiles()
  { /* readListOfSampleDataFiles */
    /* [1] Initialize */
    int maxToRead= Math.min(maxPreloadImgs, iHPnbr);
    
    // maxToRead= Math.min(maxPreloadImgs, hps.nHP);
    // maxToRead= hps.nHP;    /* Override if going to cache all images */
    
    nFilesTotal= maxToRead+N_CONST_FILES;   /* # of files always read +
     * # .quant files */
    
    /* Set some global parameters used by all samples */
    cfg.minQualCheck= 100000000.0F;  /* when calc min QualCheck Value */
    cfg.maxQualCheck= -100000000.0F; /* when calc max QualCheck Value */
    
    /* [2] Create MaHybridSample objects. Read in maxPreloadImgs .quant
     * files. Then, read in the others on demand.
     */
    for(int idxHP= 1; idxHP<=maxToRead; idxHP++)
    { /* create H.P. objects */
      int hpNbr= mapHPtoMenuName[idxHP];
                              /* index of snHPName[] corresponding
                               * to iSampleName[] entry */
      
      if(hpNbr==-1)
        continue;         /* marked as NOT FOUND. Note just because
                           * it is in the startup list of samples does
                           * not mean that it is in the SamplesDB. */
      
      String
        snHPname= snHPName[hpNbr],
        snHPsampleID= snHPSampleID[hpNbr],
        snHPdatabaseFileID= snHPDatabaseFileID[hpNbr],
        snHPimageFile= snImageFile[hpNbr],
        snHPmenuText= snHPMenuText[hpNbr],
        snHPfullStageText= snHPFullStageText[hpNbr],
        snHPprojectName= snPrjName[hpNbr],
        sHPcy53Token= ("HPcy53Flag-"+hpNbr);
      boolean
        snHPneedLogin= snNeedLogin[hpNbr],
        snHPswapCy5Cy3Data= gp.setDefParam(sHPcy53Token,false),
        readImageFlag= false,
        setDefaultSpacingFlag= true;
      
      snSwapCy5Cy3Data[hpNbr]= snHPswapCy5Cy3Data;
                 /* If specified "HPcy53Flag" in .mae file,
                  * then override flag from the SamplesDB.txt file
                  */
      /*
      if(CONSOLE_FLAG)
        fio.logMsgln("RLSDF iSampleName[idxHP="+idxHP+"]="+
                     iSampleName[idxHP]+
                     ", snHPname[hpNbr="+hpNbr+"]="+ snHPname+
                     ", snHPsampleID="+snHPsampleID+
                     ", snHPdatabaseFileID="+snHPdatabaseFileID+
                     ", snHPimageFile="+snHPimageFile+
                     ", snHPmenuText="+snHPmenuText+
                     ", snHPfullStageText="+snHPfullStageText+
                     ", snHPprojectName="+snHPprojectName+
                     ", snHPneedLogin="+snHPneedLogin+
                     ", snHPswapCy5Cy3Data="+snHPswapCy5Cy3Data);
      */
      
      ms= createSample(idxHP,               /* i.e idxHP */
      snHPname, snHPsampleID, snHPdatabaseFileID,
      snHPimageFile, snHPmenuText, snHPfullStageText,
      snHPprojectName, snHPneedLogin,
      snHPswapCy5Cy3Data);
      
      if(ms!=null && !ms.needLoginFlag)
      { /* only add if was successful */
        hps.addHPtoDB(ms,idxHP);  /* add to master msList[0:nHP] DB */
      }
    } /* create H.P. objects */
    
    /* Dont; bother setting names if fatal error */
    if(Util.popupDryrotMsgsAndQuit())
      return(false);
    
    /* [3] Setup the HP map msList(X|Y|E)[] and idxList(X|Y|E)[]
     * arrays from the PARAM cfg.X(Y|E)lists.
     * Set default HP-X and HP-Y to the first entries
     * on the Xlist and Ylist PARAM (if any) using the map entries
     * msListX[0] for curHP_X and msListY[0] for curHP_Y
     * This deals with the case where there is NO intial data!
     */
    hps.setHPlistsFromPARAM();
    
    /* Set default sample to X sample if it exists */
    curHP= curHP_X;
    ms= msX;
    
    mbf.setHP_XYlabels();         /* default to single HP-X and HP-Y */
    
    /* [4] Setup default HP condition lists */
    if(hps.nHP_X>0)
      cdList.addCondList("Initial HP-X: "+classNameX,
                         hps.msListX, hps.nHP_X, 1);
    if(hps.nHP_Y>0)
      cdList.addCondList("Initial HP-Y: "+classNameY,
                         hps.msListY, hps.nHP_Y, 1);
    if(hps.nHP_E>0)
      cdList.addCondList("Initial HP-E expression list",
                         hps.msListE, hps.nHP_E, 1);
    
    /* [5] Read user HP condition lists from stand-alone file. */
    us.restoreCondLists();
    
    return(true);
  } /* readListOfSampleDataFiles */
  
  
  /**
   * createSample() - create and return a MaHybridSample hybridized sample object.
   * For all hpNbr > 1, read the .quant filedata.
   * Then when we need the image in the future, eg. when we switch
   * the mae.ms (current displayed HP) from the Samples menu,
   * the it sets mae.ms= mae.msList[npNbr]  to install new current ms.
   * @param hpNbr is the sample number
   * @param hpName is the sample file name
   * @param sampleID is the free-form name of the sample
   * @param databaseFileID is the optional database file ID (used with mAdb)
   * @param oImageName is the optional image file name
   * @param oMenuName is the name of this sample as it appears in the menu
   * @param fullStageText is the full text specification of this sample
   * @param projectName project this database belongs to
   * @param needLoginFlag requires login to access data from Web server if needed.
   * @param swapCy5Cy3DataFlag set if swap Cy3 and Cy5 channels.
   * @return a new MaHybridSample object otherwise null if failed.
   * @see MaHybridSample
   */
  MaHybridSample createSample(int hpNbr, String hpName,String sampleID,
                              String databaseFileID, String oImageName,
                              String oMenuName, String fullStageText,
                              String projectName, boolean needLoginFlag,
  boolean swapCy5Cy3DataFlag)
  { /* createSample */
    /* Now make a MaHybridSample Sample object and initialize what need to do */
    MaHybridSample
      msR= new MaHybridSample(this, hpName, sampleID, databaseFileID,
                              oImageName, oMenuName, fullStageText,
                              projectName, needLoginFlag,
                              swapCy5Cy3DataFlag,
                              hpNbr        /* i.e. idxHybridSample */
                              );
    if(msR.needLoginFlag)
      return(null);                        /* failed */
    
    return(msR);
  } /* createSample */
  
  
  /**
   * chkIfCache() - check if set up to cache from Web DB.
   * Make sure /Cache directory exists and if not, it creates it.
   */
  void chkIfCache()
  { /* chkIfCache */
    if(!isAppletFlag && useWebDBflag && saCodeBase.startsWith("http://"))
    { /* Stand-alone application enable caching from the Web server */
      cacheFIOflag= enableFIOcachingFlag;
      codeBase= saCodeBase;       /* Overide it */
      dynFileSeparator= "/";
    }
    else
    { /* no caching */
      cacheFIOflag= false;
    }
  } /* chkIfCache */
  
  
  /**
   * resetImageAndState() - Re initialize whenever restart
   * @see #resetDefaultstate
   * @see GeneClass#setupSpecialGeneLists
   * @see #repaint
   */
  void resetImageAndState()
  { /*resetImageAndState */
    resetDefaultstate();  /* Reinitialize global switches state */
    
    /* [TODO] call the GUI and put the menu switches in a std state */
    
    /* Reset the default GeneClasses */
    if(gct!=null)
      gct.setupSpecialGeneLists();
    
      /* [TODO] what do we do with initial msY and msE? -
       * Reset to copy of initial list.
       */
    
    /* [TODO] Reset Filter state */
    
    if(ms!=null)
      updatePseudoImgFlag= true;
    
    repaint();
  } /* resetImageAndState */
  
  
  /**
   * quit() - kill this application or Applet
   * @see MenuBarFrame#setVisible
   * @see MenuBarFrame#dispose
   */
  void quit()
  { /* quit */
    if(isAppletFlag)
      killAppletFlag= true;
    if(mbf!=null)
    {
      mbf.setVisible(false);
      mbf.dispose();
    }
    dieFlag= true;       /* will cause the run() to exit,
                          * then call stop() and destroy() */
    if(!isAppletFlag)
      System.exit(0);
  } /* quit */
  
  
  /**
   * percentDone() - estimate percent of files done loading
   * @return a percentage in the range of [0 to 100]
   * @see Util#showMsg3
   */
  int percentDone()
  { /*percentDone */
    int pcntDone= (100*nFilesRead)/nFilesTotal;
    String sMsg;
    
    Util.showMsg3("Loading database: " + pcntDone + "% done.",
                  Color.white, Color.red);
    return(pcntDone);
  } /* percentDone */
  
  
  /**
   * cleanup() - close up what needs to be closed and GC all structures
   * Reset to initial state so can restart it...
   * @see #destroyStaticState
   * @see FileIO#logMsgln
   */
  void cleanup()
  { /* cleanup */
    
    /* Cleanup statics in other Classes - for use when
     * loading a new database.
     */
    destroyStaticState();
    
    if(fio!=null)
      fio.logMsgln("MAE-start gc()");
    System.runFinalization();
    System.gc();
    if(fio!=null)
      fio.logMsgln("MAE-done gc()");
  } /* cleanup */
  
  
  /**
   * destroy() - destroy this class
   * @see #destroy2
   */
  public void destroy()
  { /* destroy */
    destroy2();
  } /* destroy */
  
  
  /**
   * destroy2() - destroy this class, but clean up the mess
   * of statics first.
   * @see FileIO#closeLogFile
   * @see FileIO#logMsgln
   * @see #cleanup
   */
  void destroy2()
  { /* destroy2 */
    if(fio!=null)
      fio.logMsgln("MAE-destroy2()");
    
    onceOnlyFlag= false;  /* allow next instance. */
    
    if(isAppletFlag)
      cleanup();          /* clean up THIS main class for applet */
    else
    { /* exit stand-alone application */
      if(fio!=null)
        fio.closeLogFile();
      System.exit(0);	/* only for non-Applet */
    } /* exit stand-alone application */
  } /* destroy2 */
  
  
  /**
   * logDRYROTerr() - add the dryrot error to the dryrotLogStr and log it
   * by printing it to the system log.
   * @param msg is the dryrot message to print
   * @see FileIO#logMsg
   */
  void logDRYROTerr(String msg)
  { /* logDRYROTerr */
    if(msg!=null)
    {
      String sMsg= "'DRYROT' - " + msg + "\n";
      if(dryrotLogStr==null)
        dryrotLogStr= sMsg;
      else
        dryrotLogStr += sMsg;
      fio.logMsg(sMsg);
    }
  } /* logDRYROTerr */
  
  
} /* end of class MAExplorer */
