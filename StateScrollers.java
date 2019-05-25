/** File: StateScrollers.java */

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

/**
 * The class builds a popup window dynamic grid of threshold parameter scrollers.
 * This constitutes a set of state scrollers popup "control panel". 
 * Scrollers are associated with data filters which use the threshold. 
 * If a filter becomes active or inactive, then the scroller panel is 
 * regenerated and the panel will added or dropped to reflect the changes. 
 * The regenerated is invoked by EventMenu when the data filter is enabled
 * or disabled.
 *<P>
 * There is no DONE button handler since users do not kill the window,
 * but rather when it is killed when the associated Filter is disabled.
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.20 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see EventMenu
 * @see Filter
 */

class StateScrollers extends Frame implements
             AdjustmentListener, ActionListener, WindowListener, 
	     MouseListener
{ 
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;                        
  /** link to global Config instance */
  private Config
    cfg;                        
  /** link to global Util instance */
  private Util
    util;                       

  /** size of mouseover TextArea */
  final static int
    TA_ROWS= 3;   
  /** size of mouseover TextArea */
  final static int
    TA_COLS= 45;
    
  /** default width size of state scroller panel */
  private final int
    DEFAULT_PANEL_WIDTH= 400;   
  /** default height size of state scroller panel */
  private final int
    DEFAULT_PANEL_HEIGHT= 100;  
  
  /** max width size of state scroller panel */
  private final int
    PREFERRED_PANEL_HEIGHT= 599;   
  /** max height size of state scroller panel */
  private final int
    PREFERRED_PANEL_WIDTH= 100;
  
  /** size of grid for panel  */
  private final int
    GRIDCOL= 3;
	
  /** Normal position for showing selected state. If showing 
   * all states, then move Vert position to near top
   */
  private Point
    scrPos;
   	        
  /** list of state scroller label names */
  static String
    sbName[]= {"Spot SI1", "Spot SI2",    /* # 1 and # 2 */
	             "I1", "I2",                /* # 3 and # 4 */
	             "Ratio R1", "Ratio R2",    /* # 5 and # 6 */
	             "Zdiff Z1", "Zdiff Z2",    /* # 7 and # 8 */
	             "Ratio CR1", "Ratio CR2",  /* # 9 and # 10 */
               "Zdiff CZ1", "Zdiff CZ2",  /* # 11 and # 12 */
               "p-Value",                 /* # 13 */
               "Spot CV",                 /* # 14 */
               "Spot Radius",             /* # 15 */
               "Cluster Distance",        /* # 16 */
               "# of Clusters",           /* # 17 */
               "Diff HP-XY",              /* # 18 */
               "Percent SI OK",           /* # 19 */
               "Spot Quality",            /* # 20 */
               "Spot Detection Value"     /* # 21 */
              };
  
  /** # of entries in sbName[] tables */
  final static int
    nSB= sbName.length; 
  
  /** messages for mouseover of label */
  static String
    sbMouseMsg[]= 
       {"Threshold: Spot SI1\n"+
	      "HP-sample spot intensity lower bound for data Filter."+
        "\nIf ratio data, then Cy3 and Cy5 channels separately.",   /* # 1 */
        
        "Threshold: Spot SI2\n"+
        "HP-sample spot intensity upper bound for data Filter."+
        "\nIf ratio data, then Cy3 and Cy5 channels separately.",   /* # 2 */
        
        "Threshold: I1\n"+
        "HP-sample gene intensity lower bound for data Filter."+
        "\nIf ratio data, then (Cy3/Cy5) else intensity of spot",   /* # 3 */
        
        "Threshold: I2\n"+
        "HP-sample gene intensity upper bound for data Filter."+
        "\nIf ratio data, then (Cy3/Cy5) else intensity of spot",   /* # 4 */
        
        "Threshold: Ratio R1\n"+
        "ratio (HP-X / HP-Y) lower bound used in data Filter",      /* # 5 */
        
        "Threshold: Ratio R2\n"+
        "ratio (HP-X / HP-Y) upper bound used in data Filter",      /* # 6 */
        
        "Threshold: Zdiff Z1\n"+
        "Zdiff (HP-X - HP-Y) ratio lower bound used in data Filter", /* # 7 */
        
        "Threshold: Zdiff Z2\n"+
        "Zdiff (HP-X - HP-Y) ratio upper bound used in data Filter", /* # 8 */
        
        "Threshold: Ratio CR1\n"+
        "ratio (Cy3 / Cy5) lower bound used in data Filter",        /* # 9 */
        
        "Threshold: Ratio CR2\n"+
        "ratio (Cy3 / Cy5) lower bound used in data Filter",        /* # 10 */
        
        "Threshold: Zdiff CZ1\n"+
        " Zdiff (Cy3 - Cy5) ratio lower bound used in data Filter", /* # 11 */
        
        "Threshold: Zdiff CZ2\n"+
        "Zdiff (Cy3 - Cy5) ratio upper bound used in data Filter",  /* # 12 */
        
        "Threshold: p-Value\n"+
        "p-value threshold used in (HP-X, HP-Y) t-Test, F-test and\n"+
        "other data Filters",                                       /* # 13 */
        
        "Threshold: Spot CV\n"+
        "spot coefficient of variation threshold used in data filter", /* # 14 */
        
        "Threshold: Spot Radius\n"+
        "radius of spots drawn in pseudo-array image",              /* # 15 */
        
        "Threshold: Cluster Distance\n"+
        "cluster distance used in similar clusters, cluster counts"+
        "\nand K-means clustering",                                 /* # 16 */
        
        "Threshold: # of Clusters\n"+
        "number of clusters desired in K-means clustering",         /* # 17 */
        
        "Threshold: Diff HP-XY\n"+
        "absolute difference between HP-X and HP-Y "+
        "\nused in data filtering",                                 /* # 18 */
        
        "Threshold: Percent SI OK\n"+
        "percent of samples meeting [SI1:SI2] spot intensity "+
        "range",                                                    /* # 19 */
        
        "Threshold: Spot Quality\n"+
        "spot quality threshold if have continuous range of "+
        "\nQualCheck spot 'goodness' values used in data filter",   /* # 20 */
        
        "Threshold: Spot Detection Value\n"+
        "spot detection value threshold if have continuous range of "+
        "\nDetection Value 'quality' values used in data filter."+
        "\nFor example, this could be the Affymetrix 'Detection p-Value'." /* # 21 */
      };
        
  /* NOTE: these values start a 0 not 1! */
  /** index for Spot SI1 intensity value lower bound */
  final static int
    idxSI1= 0;		       
  /** index for Spot SI2 intensity value upper bound */
  final static int
    idxSI2= 1;
  /** index for I1 (T1) HP intensity value lower bound */
  final static int
    idxT1= 2;
  /** index for I2 (T2) HP intensity value upper bound */
  final static int
    idxT2= 3;
  /** index for R1 ratio HP-X/HP-Y value lower bound */
  final static int
    idxR1= 4;
  /** index for R2 ratio HP-X/HP-Y value upper bound */
  final static int
    idxR2= 5;
  /** index for Z1 zscore value of (HP-X - HP-Y) lower bound */
  final static int
    idxZ1= 6;
  /** index for Z2 zscore value of (HP-X - HP-Y) upper bound */
  final static int
    idxZ2= 7;
  /** index for CR1 Cy(3/5) ratio value lower bound */
  final static int
    idxCR1= 8;
  /** index for CR2 Cy(3/5) ratio value upper bound */
  final static int
    idxCR2= 9;
  /** index for CZ1 Cy(3/5) zscore value lower bound */
  final static int
    idxCZ1= 10;
  /** index for CZ2 Cy(3/5) zscore value upper bound */
  final static int
    idxCZ2= 11;
  /** index for p-value threshold value*/
  final static int
    idxPvalue= 12;
  /** index for spot Coefficient of Variation threshold value*/
  final static int
    idxSpot_CV= 13;
  /** index for Radius for drawing spots in the pseudo array */
  final static int
    idxRadius= 14;
  /** index for clusterDist threshold for cluster distance clustering */
  final static int
    idxClusterDist= 15;
  /** index for # of clusters threshold for K-means clustering */
  final static int
    idxNbrClusters= 16;
  /** index for Diff(HP-X,HP-Y) intensity differerence threshold */
  final static int
    idxDiffXY= 17;
  /** index for Percent SI OK (SI AT_LEAST/AT_MOST) QualCheck threshold */
  final static int
    idxPctOK= 18;
  /** index for qualType continuous threshold of QualCheck values */
  final static int
    idxQualThr= 19;
  /** index for continuous threshold of Spot Detection values */
  final static int
    idxdetValueSpotThr= 20;
    
  /** Flag: allow popdown if orig. window closing */ 
  boolean
    allowPopdownOnWindowClosing;
  /* Flag set for special post-processing */
  boolean
   doRepaint;
  /* Flag set for special post-processing */
  boolean
   runImageOptimizer;  
  /* Flag set for special post-processing */
  boolean
   runClusterAnalysis;
  /* Flag set for special post-processing */
  boolean
   runKmeansClusterAnalysis;
  /* Flag set for special post-processing */
  boolean
   runFilter;
  
  
  /* --- 
   * Scroller scale multiplicative factor to scale from STATE domain
   * to Scrollbar domain pixel size of bar domain.
   * The event handler will scale DOWN the int scroller value by dividing 
   * the it by the scale factor and saving it in sbCurValue[],
   * the STATE variable value. ---
  */
  /** size of scale in Scroller space */      
  int 
    sbSize[];            
  
  /** Note: upper bounds in STATE space */
  float
    sbUpperBound[];      
  /** Note: lower bounds in STATE space */
  float
    sbLowerBound[];
  /** current & initial (STATE) values of scrollbar */
  float
    sbCurValue[];        
		   	     	
  /* The following index maps reflects the index of the corresponding 
   * scroller that bounds this value.
   */		        
  /** corresponding LT scroller integer bounds */  	     
  int
    sbLTidx[];          
  /** corresponding GT scroller integer bounds */
  int
    sbGTidx[];
  /** # of digits in scroller value labels */
  int
    sbPrecision[];      
       
  /** Flags for using a nonlinear vernier in the SCROLLER CONTROLs */ 
  boolean
    sbNonLinearScale[];      
       
  /** # of filters dependent on each slider. Each filter
   * that requires that slider will increment the counter
   * when it is enabled and decrement the counter when
   * it is disabled.
   */ 
  int
    useFilterCounters[]; 
	
  /** holds title string for state scroller GUI*/         	
  String
    title;

  /* --- GUI components for state scroller --- */
  /** holds buttons, textAreas, etc for state scroller GUI*/         
  Panel 
    lowerControlPanel;
    		
  /** pop down (hide) the box - only in adjust all scrollers mode. 
   * Normally, can only pop it down if there are NO associated data Filters.
  */
  Button
    doneButton;   
  /** Apply the parameters to the data filters. Only in adjust all scrollers 
   * mode. Normally, automatically apply it by calling the data filters if not
   * adjusting all scrollers mode.
  */
  Button
    applyButton;
  /** pop down current mode and popup adjust all sliders */
  Button
    adjustAllButton;       
  
  /** mouseover TextArea */
  TextArea
    mouseoverTA;	        
  	
  /** Label for title */	
  Label
    titleLabel;
  /** label of Name to left of scrollbar */
  Label
    sbLblName[];	  
   /** label of Values to right of scrollbar */
  Label
    sbLblValue[];	  
  /** list of scroll bars */
  Scrollbar
    sbBar[];		
  /** Flags: true if scroll bars are active */	
  boolean 
    sbActive[];			
  /** holds the Scrollbar (SB) panels */
  Panel
    pgrid;			
  /** optional background color ... */
  Color 
    bgColor;			
  /** font type for labels */
  Font 
    newFontLbl;		
  /** Flag: force all scrollers to appear even if not used... */	
  boolean 
    useAllScrollersFlag;
  /** Flag: if StateScroller GUI is visible */
  boolean 
    isVisible;			
  /** panel size Width  */
  private int
    pnlWidth;
  /** panel size height  */
  private int
    pnlHeight;
  /** size of grid for panel  */
  private int
    gridCol;
  /** size of grid for panel  */
  private int
    gridRow;			     

  /**
   * StateScrollers() - constructor to build the GUI with pop up menus
   * @param mae is MAExplorer instance
   * @param title is the title for the state scroller popup window
   * @see #initPreloads
   */
  StateScrollers(MAExplorer mae, String title )
  { /* StateScrollers */
    super("Preference sliders");
    
    this.title= title;
    this.mae= mae;
    cfg= mae.cfg;
    util= mae.util;
    
    /* [1] Create arrays */
    initPreloads();             /* setup all of the preloaded arrays. */
    sbLblName= new Label[nSB];  /* dynamic label of names to left of scrollbar
                                 * which holds 'sbName[]>'*/
    sbLblValue= new Label[nSB]; /* dynamic label to left of scrollbar
                                 * which holds '<cur-value[i]>'*/
    sbBar= new Scrollbar[nSB];  /* scroll bars */
    sbBar= new Scrollbar[nSB];  /* scroll bars */
    sbActive= new boolean[nSB]; /* true if a scroll bar is active */
    
    newFontLbl= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
    isVisible= false;
    useAllScrollersFlag= false;
    allowPopdownOnWindowClosing= false; /* allow popdown if orig. window closing */
  } /* StateScrollers */
 
  
  /**
   * getPreferredSize() - get the preferred size
   * @return window size
   */
  public Dimension getPreferredSize_DEPRICATED()
  { /* getPreferredSize */
    Dimension
      d= (useAllScrollersFlag)
           ? new Dimension(PREFERRED_PANEL_HEIGHT, PREFERRED_PANEL_HEIGHT)
           : new Dimension(DEFAULT_PANEL_HEIGHT, DEFAULT_PANEL_HEIGHT);
    return(d);
  } /* getPreferredSize */  
  
  
  /**
   * initPreloads() - setup the preloaded arrays of filter names, values and range gang-scroll option.
   * [TODO] Note: the ranges may be gotten from the .mae state file.
   * In particular, the following are normalization and state dependent:
   *    sbUpperBound[],  sbLowerBound[], sbCurValue[]
   * The solution is to save these bounds in the state as well (what
   * a pain!) or to recalculate them (also a pain...).
   * @see #setRangeMaps
   */
  private void initPreloads()
  { /* initPreloads */
    /* change "I1, I2" to "(Cy3/Cy5) I1", "(Cy3/Cy5) I2"
     * or "Intensity I1", "Intensity I2"
     */
    String
      sf1= mae.cfg.fluoresLbl1,
      sf2= mae.cfg.fluoresLbl2;
    if(mae.useCy5OverCy3Flag)
    { /* flip Cy3/Cy5 to Cy5/Cy3 */
      String sTmp= sf1;
      sf1= sf2;
      sf2= sTmp;
    }
    String
    changeStr= (mae.useRatioDataFlag)
                  ? "("+sf1+"/"+sf2+")"
                  : "Intensity";
    sbName[idxT1]= changeStr + " I1";
    sbName[idxT2]= changeStr + " I2";
    
    /* Note: the scroller range in SCROLLER space */
    int iTmp1[]= {mae.MAX_INTENSITY,         /* #1 tmp. scale for SI1 value*/
                  mae.MAX_INTENSITY,         /* #2 tmp. scale for SI2 value*/
                  mae.MAX_INTENSITY,         /* #3 scale for I1 (T1) value*/
                  mae.MAX_INTENSITY,         /* #4 cale for I2 (T2) value*/
                  mae.MAX_RATIO_HIST,        /* #5 scale for R1 value*/
                  mae.MAX_RATIO_HIST,        /* #6 scale for R2 value*/
                  10000,                     /* #7 scale for Z1 value*/
                  10000,                     /* #8 scale for Z2 value*/
                  mae.MAX_RATIO_HIST,        /* #9 scale for CR1 value*/
                  mae.MAX_RATIO_HIST,        /* #10 scale for CR2 value*/
                  1000,                      /* #11 scale for CZ1 value*/
                  1000,                      /* #12 scale for CZ2 value*/
                  1000000,	                 /* #13 scale for p-value thr value*/
                  1000,	                     /* #14 scale for CV thr value*/
                  cfg.MAX_RADIUS,            /* #15 scale for Radius*/
                  50000,                     /* #16 scale clusterDistance */
                  (int)cfg.MAX_NBR_CLUSTERS, /* #17 scale # clusters */
                  10000,                     /* #18 scale for DiffXY */
                  100,                       /* #19 scale for Percent SI OK */
                  100,                       /* #20 scale for Spot Quality */
                  10000000                   /* #21 scale for Spot Detection Value */
                 };
    sbSize= iTmp1;
    
    /* Note: the upper and lower bounds are in the STATE space */
    float fTmp1[]= {(float)mae.MAX_INTENSITY, /* #1 U.B. for SI1 value*/
                    (float)mae.MAX_INTENSITY, /* #2 U.B. for SI2 value*/
                    (float)mae.MAX_INTENSITY, /* #3 U.B. for I1 (T1) value*/
                    (float)mae.MAX_INTENSITY, /* #4 U.B. for I2 (T2) value*/
                    mae.MAX_RATIO,            /* #5 U.B. for R1 value*/
                    mae.MAX_RATIO,            /* #6 U.B. for R2 value*/
                    4.0F,                     /* #7 U.B. for Z1 value*/
                    4.0F,                     /* #8 U.B. for Z2 value*/
                    mae.MAX_RATIO,            /* #9 U.B. for CR1 value*/
                    mae.MAX_RATIO,            /* #10 U.B. for CR2 value*/
                    4.0F,                     /* #11 U.B. for CZ1 value*/
                    4.0F,                     /* #12 U.B. for CZ2 value*/
                    1.0F,	                    /* #13 U.B. for p-value thr value*/
                    1.0F,	                    /* #14 U.B. for CV thr value*/
                    (float)cfg.MAX_RADIUS,    /* #15 U.B. for Radius*/
                    cfg.MAX_CLUSTER_DIST,     /* #16 U.B. for clusterDistance */
                    cfg.MAX_NBR_CLUSTERS,     /* #17 U.B. for # clusters */
                    4.0F,                     /* #18 (was 0.4F??) U.B. for DiffXY */
                    100.0F,                   /* #19 U.B. for Percent SI OK */
                    cfg.maxQualCheck,         /* #20 U.B. for Spot Quality */
                    1.0F                      /* #21 U.B. for Spot Detection Value */
                   };
    sbUpperBound= fTmp1;
    
    float fTmp2[]= {0.0F,	                   /* #1 L.B. for SI1 value*/
                    0.0F,                    /* #2 L.B. for SI2 value*/
                    0.0F,	                   /* #3 L.B. for I1 (T1) value*/
                    0.0F,                    /* #4 L.B. for I2 (T2) value*/
                    mae.MIN_RATIO,           /* #5 L.B. for R1 value*/
                    mae.MIN_RATIO,           /* #6 L.B. for R2 value*/
                    -4.0F,                   /* #7 L.B. for Z1 value*/
                    -4.0F,                   /* #8 L.B. for Z2 value*/
                    mae.MIN_RATIO,           /* #9 L.B. for CR1 value*/
                    mae.MIN_RATIO,           /* #10 L.B. for CR2 value*/
                    -4.0F,                   /* #11 L.B. for CZ1 value*/
                    -4.0F,                   /* #12 L.B. for CZ2 value*/
                    0.0F /*0.0000001F */,	   /* #13 L.B. for p-value thr value*/
                    0.0000001F,	             /* #14 L.B. for CV thr value*/
                    (float)cfg.MIN_RADIUS,   /* #15 L.B. for Radius*/
                    cfg.MIN_CLUSTER_DIST,    /* #16 L.B. for clusterDistance */
                    cfg.MIN_NBR_CLUSTERS,    /* #17 L.B. for # clusters */
                    0.0000001F,              /* #18 L.B. for DiffXY */
                    0.0F,                    /* #19 L.B. for Percent SI OK */
                    cfg.minQualCheck,        /* #20 L.B. for Spot Quality */
                    0.0F                     /* #21 L.B. for Spot Detection Value */
                   };
    sbLowerBound= fTmp2;
    
    /* Current & initial (STATE) scaled value of scrollbar */
    
    /* Current & initial (STATE) scaled value of scrollbar */
    
    /* State read from Config.txt file */
    float fTmp3FromState[]= {mae.sit1,	               /* #1 SI1 value*/
                             mae.sit2,                 /* #2 SI2 value*/
                             mae.t1,	                 /* #3 I1 (T1) value*/
                             mae.t2,                   /* #4 I2 (T2) value*/
                             (mae.isZscoreFlag)
                               ? mae.MIN_RATIO : mae.r1, /* #5 R1 value*/
                             (mae.isZscoreFlag)
                               ? mae.MAX_RATIO : mae.r2, /* #6 R2 value*/
                             (!mae.isZscoreFlag)
                               ? -4.0F : mae.r1,       /* #7 Z1 value*/
                             (!mae.isZscoreFlag)
                               ? 4.0F : mae.r2,        /* #8 Z2 value*/
                             (mae.isZscoreFlag)
                               ? mae.MIN_RATIO : mae.r1, /* #9 CR1 value*/
                             (mae.isZscoreFlag)
                               ? mae.MAX_RATIO : mae.r2, /* #10 CR2 value*/
                             (!mae.isZscoreFlag)
                               ? -4.0F : mae.cr1,      /* #11 CZ1 value*/
                             (!mae.isZscoreFlag)
                               ? 4.0F : mae.cr2,       /* #12 CZ2 value*/
                             cfg.pValueThr,            /* #13 p-value thr value*/
                             cfg.spotCVthr,            /* #14 CV thr value*/
                             (float)mae.spotRad,       /* #15 Radius value */
                             cfg.clusterDistThr,       /* #16 clusterDistance value */
                             (float)cfg.nbrOfClustersThr, /* #17 # clusters */
                             cfg.diffThr,              /* #18 DiffXY thr value */
                             cfg.pctOKthr,             /* #19 Percent SI OK thr value */
                             cfg.qualThr,              /* #20 Spot Quality */
                             cfg.detValueSpotThr       /* #21 Spot Detection Value */
                            };    
    sbCurValue= fTmp3FromState;     /* State read from Config.txt file */
    
     /* The following index maps reflects the index of the
      * corresponding scroller that bounds this value.
      */
    int iTmp2[]= {idxSI2,        /* SI1 <= SI2 */
                  -1,	           /* SI2 use default L.B. */
                  idxT2,         /* I1 (T1) <= G2 */
                  -1,	           /* I2 (T2) use default L.B. */
                  idxR2,         /* R1 <= R2 */
                  -1,            /* R2 use default L.B. */
                  idxZ2,         /* Z1 <= Z2 */
                  -1,            /* Z2 use default L.B. */
                  idxCR2,        /* CR1 <= CR2 */
                  -1,            /* CR2 use default L.B. */
                  idxCZ2,        /* CZ1 <= CZ2 */
                  -1,            /* CZ2 use default L.B. */
                  -1,	           /* p-value use default L.B.*/
                  -1, 	         /* f1f2 CV use default L.B.*/
                  -1,	           /* Radius use default L.B.*/
                  -1,            /* clusterDistance */
                  -1,            /* # clusters */
                  -1,            /* diff Thr */
                  -1,            /* Percent SI OK Thr */
                  -1,            /* Spot Quality Thr */
                  -1             /* Spot Detection Value */
                 };
    sbLTidx= iTmp2;           /* to make sure, LB < UB */
    
    int iTmp3[]= {-1,	      /* SI1 use default U.B..*/
                  idxSI1,   /* SI2 >= SI1 */
                  -1,	      /* I1 (T1) use default U.B..*/
                  idxT1,    /* I2 (T2) >= I1 */
                  -1,	      /* R1 use default U.B.. */
                  idxR1,    /* R2 >= R1 */
                  -1,	      /* Z1 use default U.B.. */
                  idxZ1,    /* Z2 >= Z1 */
                  -1,	      /* CR1 use default U.B.. */
                  idxCR1,   /* CR2 >= CR1 */
                  -1,	      /* CZ1 use default U.B.. */
                  idxCZ1,   /* CZ2 >= CZ1 */
                  -1,       /* p-value use default U.B..*/
                  -1, 	    /* f1f2 CV use default U.B..*/
                  -1,	      /* Radius use default U.B..*/
                  -1,       /* clusterDistance */
                  -1,       /* # clusters */
                  -1,       /* diff Thr */
                  -1,       /* Percent SI OK thr */
                  -1,       /* Spot Quality thr */
                  -1        /* Spot Detection Value thr */
                 };
    sbGTidx= iTmp3;
    
    /* # of digits in scroller value labels */
    int iTmp4[]= {3,	    /* SI1 int.*/
                  3,	    /* SI2 int */
                  3,	    /* I1 (T1) int.*/
                  3,	    /* I2 (T2) int */
                  3,	    /* R1 %.3f */
                  3,      /* R2 %.3f */
                  3,	    /* Z1 %.3f */
                  3,      /* Z2 %.3f */
                  3,	    /* CR1 %.3f */
                  3,      /* CR2 %.3f */
                  3,	    /* CZ1 %.3f */
                  3,      /* CZ2 %.3f */
                  7,      /* p-value use default U.B. %.5f thr */
                  5, 	    /* f1f2 CV use default U.B. %.5f thr */
                  0,	    /* Radius use default U.B. int */
                  4,      /* clusterDistance thr */
                  0,      /* # clusters */
                  5,      /* Diff thr */
                  2,      /* Percent SI OK  thr */
                  2,      /* Spot Quality thr */
                  7       /* Spot Detection Value  (like p-value) */
                 };
    sbPrecision= iTmp4;   /* to make sure, LB < UB */
    
    
    /* Note: use a non-linear vernier in the SCROLLER CONTROL.
     * This should only be used for non-ganged controls.
     */
    boolean bTmp1[]= {false,         /* tmp. scale for SI1 value*/
                      false,         /* tmp. scale for SI2 value*/
                      false,         /* scale for I1 (T1) value*/
                      false,         /* scale for I2 (T2) value*/
                      false,         /* scale for R1 value*/
                      false,         /* scale for R2 value*/
                      false,         /* scale for Z1 value*/
                      false,         /* scale for Z2 value*/
                      false,         /* scale for CR1 value*/
                      false,         /* scale for CR2 value*/
                      false,         /* scale for CZ1 value*/
                      false,         /* scale for CZ2 value*/
                      true,          /* scale for p-value thr value */
                      true,          /* scale for CV thr value */
                      false,         /* scale for Radius thr */
                      true,          /* scale clusterDistance thr */
                      false,         /* scale # clusters thr */
                      true,          /* scale for DiffXY thr */
                      false,         /* scale for Percent SI OK thr */
                      true,          /* Spot Quality thr */
                      true           /* Spot Detection Value thr */
                    };
    sbNonLinearScale= bTmp1;
    
      /* # of filters dependent on each slider */
    int iTmp5[]= {0,	    /* SI1 */
                  0,	    /* SI2  */
                  0,	    /* I1 (T1) */
                  0,	    /* I2 (T2)  */
                  0,	    /* R1 */
                  0,      /* R2 */
                  0,	    /* Z1 */
                  0,      /* Z2 */
                  0,	    /* CR1 */
                  0,      /* CR2  */
                  0,	    /* CZ1  */
                  0,      /* CZ2  */
                  0,      /* p-value u*/
                  0, 	    /* f1f2 CV use default U.B. */
                  0,	    /* Radius use default U.B. */
                  0,      /* clusterDistance thr */
                  0,      /* # clusters */
                  0,      /* Diff thr */
                  0,      /* Percent SI OK thr */
                  0,      /* Spot Quality thr */
                  0       /* Spot Detection Value (like p-value) */
                 };
    useFilterCounters= iTmp5;   /* # of filters dependent on each slider */
    
       
    /* Set norm dependent scrollers based normalization method. */
    setRangeMaps();
  } /* initPreloads */
  
  
  /**
   * setUseAllScrollersFlag() - set the state to display all scrollers by flag.
   * @param newFlag is the value to set the all scrollers flag to.
   * @return the previous value
   */
  boolean setUseAllScrollersFlag(boolean newFlag)
  { /* setUseAllScrollersFlag */
    boolean oldValue= useAllScrollersFlag;
    useAllScrollersFlag= newFlag;
    return(oldValue);
  } /* setUseAllScrollersFlag */
  
  
  /**
   * createStateScrollers() - build the grid panel of popup scrollers
   * @see Util#cvf2s
   * @see #cvStateToScrollValue
   */
  void createStateScrollers()
  { /* createStateScrollers */
    /* [1] Add the title label at the top and setsize*/
    this.setLayout(new BorderLayout()); /* main frame will be borderlayout */
    pnlWidth= DEFAULT_PANEL_WIDTH;
    pnlHeight= DEFAULT_PANEL_HEIGHT;
    
    this.addWindowListener(this); /* listener for window events */
    
    /* [2] Create button panel and add "done" button*/
    lowerControlPanel= new Panel();
    lowerControlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    this.add(lowerControlPanel,"South");
    
    /* Setup text area. If strings are going to be large then need
     * to resize, (adding SB will not work since the cursor is in use!)
    */
    mouseoverTA= new TextArea(" ", TA_ROWS, TA_COLS, TextArea.SCROLLBARS_NONE);
    mouseoverTA.setEditable(false);
    lowerControlPanel.add(mouseoverTA);
    
    /* The buttons are only active if adjusting all scrollers */
    Panel buttonPanel= new Panel();
    applyButton= new Button("Apply");
    if(allowPopdownOnWindowClosing)      
      adjustAllButton= new Button("Adjust active");
    else
      adjustAllButton= new Button("Adjust all   ");
    doneButton= new Button("Done");
    applyButton.addActionListener(this);
    adjustAllButton.addActionListener(this);
    doneButton.addActionListener(this);
    buttonPanel.add(applyButton);
    buttonPanel.add(adjustAllButton);
    buttonPanel.add(doneButton);
    applyButton.setEnabled(allowPopdownOnWindowClosing);
    adjustAllButton.setEnabled(true);
    doneButton.setEnabled(allowPopdownOnWindowClosing);
    lowerControlPanel.add(buttonPanel);
   
    /* [3] Create panel to hold Scrollers */
    pgrid= new Panel();	              /* holds activated scrollbars */
    gridRow=0;	      	              /* will add as needed */
    gridCol=GRIDCOL;
    GridLayout gl= new GridLayout(0,gridCol,nSB,5); /* create grid (was 5 before nSB)*/
    pgrid.setLayout(gl);
    
    /* [4] Create scroll bars in rows */
    pgrid.add(new Label("                  ")); /* force extra spaces, */
    pgrid.add(new Label("                  ")); /* because of gridlayout */
    pgrid.add(new Label("                  "));
    
    int
      curValue,
      minValue,
      maxValue,
      visibleVal= 1;
    String
      ivStr,
      sLbl;
    for(int i=0; i<nSB; i++)
    { /* create and add i'th scroll bar panel to grid */
      if(sbActive[i])
      {
        sbLblName[i]= new Label(" " + sbName[i]); /* move from edge */
        
        curValue= cvStateToScrollValue(i, sbCurValue[i]);
        minValue= 0;
        maxValue= sbSize[i];
        ivStr= ""+ (int) sbCurValue[i];
        if(sbPrecision[i]>0)
          ivStr= Util.cvf2s(sbCurValue[i], sbPrecision[i]);
        sLbl= ivStr;
        sbLblValue[i]= new Label(sLbl);
        sbLblValue[i].addMouseListener(this);   /* handle mouseover events */
        
        sbBar[i]= new Scrollbar(Scrollbar.HORIZONTAL, curValue, visibleVal,
                                minValue, (maxValue+visibleVal));
        sbBar[i].addAdjustmentListener(this);
        
        pgrid.add(sbLblName[i]);
        pgrid.add(sbBar[i]);
        pgrid.add(sbLblValue[i]);               /* dynamic value */
        
        /* handle mouseover events */
        sbLblName[i].addMouseListener(this);
        sbBar[i].addMouseListener(this);
        sbLblValue[i].addMouseListener(this);
        
        sbBar[i].setValue(curValue);          /* set it to current value position */
      }
    } /* create and add i'th scroll bar panel to grid */
    
    /* [5] add components to frame */
    pgrid.add(new Label("                  ")); /* force extra spaces, */
    pgrid.add(new Label("                  ")); /* because of gridlayout */
    pgrid.add(new Label("                  "));
    
    if(title!=null)
    {  /* add title to top */
      this.add(new Label(title, Label.CENTER), BorderLayout.NORTH);
    }
    this.add(pgrid, BorderLayout.CENTER);      /* scrollbars */
    this.add(lowerControlPanel, BorderLayout.SOUTH); /* lower Controls */
    this.setSize(pnlWidth,pnlHeight);
    this.pack();
    isVisible= true;
    
    /* Center frame on the screen, PC only */
    Dimension scr= Toolkit.getDefaultToolkit().getScreenSize();
    if(allowPopdownOnWindowClosing)
      scrPos= new Point((int)(0.75*scr.width-this.getSize().width/2), 20);
    else
      scrPos= new Point((int)(0.75*scr.width-this.getSize().width/2),
                        (int)(0.75*scr.height-this.getSize().height/2));
    this.setLocation(scrPos);
    
    this.setVisible(true);
  } /* createStateScrollers */
  
  
  /**
   * clearActiveScrollers() - clear list of all active scrollers
   */
  void clearActiveScrollers()
  { /* clearActiveScrollers */
    for(int i=0;i<nSB;i++)
    {
      if(sbActive[i] && sbBar[i]!=null)
        pgrid.remove(sbBar[i]);
      sbActive[i]= false;
    }
  } /* clearActiveScrollers */
  
  
  /**
   * lookupScrollerIdx() - lookup a particular scroller index
   * @param scrollBarName is the name of the scroller to lookup by name
   * @return -1 if failed, else an index >= 0 if successful;
   */
  int lookupScrollerIdx(String scrollBarName)
  { /* lookupScrollerIdx */
    for(int i=0;i<nSB;i++)
      if(scrollBarName.equalsIgnoreCase(sbName[i]))
      {
        return(i);
      }
    return(-1);
  } /* lookupScrollerIdx */
  
  
  /**
   * addScroller() - add a particular scroller by scroller name
   * @param scrollBarName is the name of the scroller to add by name
   * @return true if successful;
   */
  boolean addScroller(String scrollBarName)
  { /* addScroller */
    for(int i=0;i<nSB;i++)
      if(scrollBarName.equalsIgnoreCase(sbName[i]))
      {
        sbActive[i]= true;
        return(true);
      }
    return(false);
  } /* addScroller */
  
  
  /**
   * rmvScroller() - remove a particular scroller by scroller name
   * @param scrollBarName is the name of the scroller to remove by name
   * @return true if successful
   */
  boolean rmvScroller(String scrollBarName)
  { /* rmvScroller */
    for(int i=0;i<nSB;i++)
      if(scrollBarName.equalsIgnoreCase(sbName[i]))
      {
        if(sbActive[i] && sbBar[i]!=null)
          pgrid.remove(sbBar[i]);
        sbActive[i]= false;
        return(true);
      }
    return(false);
  } /* rmvScroller */
  
  
  /**
   * updateFilterScrollerUseCounter() - "add" or "remove" use of a particular scroller by scroller name.
   * This method is called both by the EventMenu handlers when
   * the Filter flags change and by plugins via the MJA.
   * @param scrollBarName is the name of the scroller to update by name in sbName[0:nSB-1]
   * @param addFlag to add a counter if true else remove a counter if false
   * @return true if successful
   */
  boolean updateFilterScrollerUseCounter(String scrollBarName, 
                                         boolean addFlag)
  { /* updateFilterScrollerUseCounter */
    for(int i=0;i<nSB;i++)
      if(scrollBarName.equalsIgnoreCase(sbName[i]))
      {
        if(addFlag)
          useFilterCounters[i]= Math.max(useFilterCounters[i]+1, 0);
        else if(! addFlag)
          useFilterCounters[i]= Math.max(useFilterCounters[i]-1, 0);
        return(true);
      }
    return(false);
  } /* updateFilterScrollerUseCounter */
  
  
  /**
   * adjustRatioOrZscoreCounters() - adjust any sliders that depend on being in Zscore mode
   * This is ONLY called when mae.isZscoreFlag is changed.
   * This is called if Zscore mode is CHANGED while ratio mode is still active.
   */
  void adjustRatioOrZscoreCounters()
  { /* adjustRatioOrZscoreCounters */
    if(mae.ratioFilterFlag)
    { /* Ratio or Zdiff filter */
      /* ratio data */
      updateFilterScrollerUseCounter("Ratio R1", ! mae.isZscoreFlag);
      updateFilterScrollerUseCounter("Ratio R2", ! mae.isZscoreFlag);
      
      /* Zdiff data */
      updateFilterScrollerUseCounter("Zdiff Z1", mae.isZscoreFlag);
      updateFilterScrollerUseCounter("Zdiff Z2", mae.isZscoreFlag);
    }
    
    if(mae.ratioCy3Cy5FilterFlag)
    { /* Cy3/Cy5 Ratio or Zdiff filter */
      /* Ratio data */
      updateFilterScrollerUseCounter("Ratio CZ1", ! mae.isZscoreFlag);
      updateFilterScrollerUseCounter("Ratio CZ2", ! mae.isZscoreFlag);
    
      /* Zdiff data */
      updateFilterScrollerUseCounter("Ratio CR1", mae.isZscoreFlag);
      updateFilterScrollerUseCounter("Ratio CR2", mae.isZscoreFlag);
    }
  } /* adjustRatioOrZscoreCounters */
  
  
  /**
   * initUserFilterScrollerCounters() - initialize scroller use counters 
   * based on the State flags.
   *<PRE>
   * 1. called from EventMenu handlers when change the flags
   * 2. plugins will modify counters separately through other calls.
   *</PRE> 
   */
   void initUserFilterScrollerCounters()
   { /* initUserFilterScrollerCounters */
     if(mae.spotIntensFilterFlag)
     {
       updateFilterScrollerUseCounter(sbName[idxSI1], true);
       updateFilterScrollerUseCounter(sbName[idxSI2], true);
     }
     
     if(mae.intensFilterFlag)
     {
       updateFilterScrollerUseCounter(sbName[idxT1], true);
       updateFilterScrollerUseCounter(sbName[idxT2], true);
     }
     
     if(mae.ratioFilterFlag)
     {
       if(mae.isZscoreFlag)
       { /* Zscore scrollers */
         updateFilterScrollerUseCounter(sbName[idxZ1], true);
         updateFilterScrollerUseCounter(sbName[idxZ2], true);
       }
       else
       { /* Ratio scrollers */
         updateFilterScrollerUseCounter(sbName[idxR1], true);
         updateFilterScrollerUseCounter(sbName[idxR2], true);
       }
     }     
     
     if(mae.ratioCy3Cy5FilterFlag)
     {
       if(mae.isZscoreFlag)
       { /* Zscore scrollers */
         updateFilterScrollerUseCounter(sbName[idxCZ1], true);
         updateFilterScrollerUseCounter(sbName[idxCZ2], true);
       }
       else
       { /* Ratio scrollers */
         updateFilterScrollerUseCounter(sbName[idxCR1], true);
         updateFilterScrollerUseCounter(sbName[idxCR2], true);
       }
     }
     
     if(mae.tTestXYsetsFilterFlag || mae.tTestXYfilterFlag ||
        mae.KS_TestXYsetsFilterFlag || mae.F_TestOCLFilterFlag)
     {
       updateFilterScrollerUseCounter(sbName[idxPvalue], true);
     }
     
     if(mae.useSpotCVfilterFlag )
     {
       updateFilterScrollerUseCounter(sbName[idxSpot_CV], true);
     }
     
     if(mae.useClusterDistFlag)
     {
       updateFilterScrollerUseCounter(sbName[idxClusterDist], true);
     }
     
     if(mae.useKmeansClusterCntsDispFlag)
     {
       updateFilterScrollerUseCounter(sbName[idxNbrClusters], true);
     }
     
     if(mae.useDiffFilterFlag)
     {
       updateFilterScrollerUseCounter(sbName[idxDiffXY], true);
     }
     
     if(mae.spotIntensFilterFlag &&
       (mae.spotIntensCompareMode==MAExplorer.COMPARE_AT_LEAST ||
        mae.spotIntensCompareMode==MAExplorer.COMPARE_AT_MOST))
     {
       updateFilterScrollerUseCounter(sbName[idxPctOK], true);
     }
     
     if(mae.useGoodSpotDataFlag && mae.qualTypeMode==mae.QUALTYPE_THR)
     {
       updateFilterScrollerUseCounter(sbName[idxQualThr], true);
     }
     
     if(mae.useDetValueSpotDataFlag)
     {
       updateFilterScrollerUseCounter(sbName[idxdetValueSpotThr], true);
     }     
     
   } /* initUserFilterScrollerCounters */
    
  
  /**
   * setEnablePopupScrollbars() - hide or show popup all scrollbars
   * @param showIt flag to hide or show popup state scrollers
   */
  void setEnablePopupScrollbars(boolean showIt)
  { /* setEnablePopupScrollbars */
    pgrid.setVisible(showIt);
  } /* setEnablePopupScrollbars */
  
  
  /**
   * cvScrollToStateValue() - cvt scrollbar value to (float) STATE value
   *<PRE>
   * If using a log scale, then
   *        stVal = alpha*(sbValue**2)+beta,
   * where: alpha = (ub-lb)/(sbSize**2-1),
   *        beta  = ( (ub+lb)-alpha*(sbSize**2+1) )/2.
   *</PRE>
   * @param idx is the index of the scroller to convert
   * @param sbVal is the scroller space value of the scroller to convert
   * @return value in state space
   * @see #cvScrollValueToRatio
   */
  float cvScrollToStateValue(int idx, int sbVal)
  { /* cvScrollToStateValue */
    float
      ub, lb,
      stVal= 0.0F;
    int sbMax= sbSize[idx];
    boolean useNonLinearScaleFlag= sbNonLinearScale[idx];
    
    if(idx==idxR1 || idx==idxR2 || idx==idxCR1 || idx==idxCR2)
      stVal= (float)cvScrollValueToRatio(sbVal, sbMax);
    else
    { /* interpolate from scroll var */
      ub= sbUpperBound[idx];                /* state space values */
      lb= sbLowerBound[idx];
      if(useNonLinearScaleFlag)
      { /* interpolate on log scale */
        try
        { /* use non-linear solution */
          double
            sbValXform= Math.log((double)(sbMax-sbVal+1.0)),
            sbMaxXform= Math.log((double)(sbMax+1.0)),
            r= sbValXform/sbMaxXform;            /* scrollbar ratio */
          stVal= (float)((1-r)*(ub - lb) + lb);
          /*
          if(mae.CONSOLE_FLAG)
           System.out.println("SS-CVSc2StV ("+sbName[idx]+
                              ") ub="+util.cvf2s(ub,2)+
                              " lb="+util.cvf2s(lb,2)+
                              " sbMax="+sbMax+
                              " sbMaxXform="+util.cvd2s(sbMaxXform,6)+
                              "\n sbVal="+sbVal+
                              " sbValXform="+util.cvd2s(sbValXform,6)+                              
                              " r="+r+
                              " : stVal="+util.cvf2s(stVal,5));         
         */
        } /* use non-linear solution */
        catch(Exception e)
        { /* fallback to interpolate on linear scale */
          useNonLinearScaleFlag= false;
        }
      } /* interpolate on log scale */
      
      if(!useNonLinearScaleFlag)
      { /* interpolate on linear scale */
        float r= (float)sbVal/(float)sbMax; /* scrollbar ratio */
        stVal= r*(ub - lb) + lb;
      }
    } /* interpolate from scrollvar */
    
    return(stVal);
  } /* cvScrollToStateValue */
  
  
  /**
   * cvStateToScrollValue() - convert STATE value to scrollbar current value.
   *<PRE>
   * If using a log scale, then
   *      ubMlbRange= (ub-lb),
   *      stMlbVal= (sbMax -stVal -lb),
   *      r= (stMlbVal / ubMlbRange),
   *      rLog= r*log(sbMax+1.0),
   *      sbVal= sbMax - (exp(rLog) - 1.0).
   *</PRE>
   * @param idx is the index of the scroller to convert
   * @param stVal is the space space value of the scroller to convert
   * @return value in scroller space
   * @see #cvRatioToScrollValue
   */
  int cvStateToScrollValue(int idx, float stVal)
  { /* cvStateToScrollValue */
    int
      sbVal= 0,
      sbMax= sbSize[idx]+1;
    float ub, lb;
    double
     ubMlb,
     stMlb,
     r,
     rLog;  
          
    boolean useNonLinearScaleFlag= sbNonLinearScale[idx];
    
    if(idx==idxR1 || idx==idxR2 || idx==idxCR1 || idx==idxCR2)
      sbVal= cvRatioToScrollValue(stVal /*, sbMax*/);
    else
    { /* check if non-linear scaling */
      ub= sbUpperBound[idx];               /* state space values */
      lb= sbLowerBound[idx];
      if(useNonLinearScaleFlag)
      { /* interpolate on log scale */
        try
        { /* use non-linear solution */          
          ubMlb= (ub-lb);
          stMlb= (ub-stVal-lb);
          r= (stMlb/ubMlb);
          rLog= r*Math.log((double)(sbMax+1.0));
          sbVal= sbMax - (int)(Math.exp(rLog) - 1.0);
          /*
          if(mae.CONSOLE_FLAG)
           System.out.println("SS-CVSt2ScV ("+sbName[idx]+
                              ") ub="+util.cvf2s(ub,2)+
                              " lb="+util.cvf2s(lb,2)+
                              " sbMax="+sbMax+
                              " ubMlb="+util.cvd2s(ubMlb,6)+
                              " stMlb="+util.cvd2s(stMlb,6)+
                              "\n r="+util.cvd2s(r,6)+
                              " rLog="+rLog+
                              " stVal="+util.cvd2s(stVal,6)+
                              " : sbVal="+sbVal);
          */
        } /* use non-linear solution */
        catch (Exception e)
        { /* error - fall back to linear soln */
          useNonLinearScaleFlag= false;
        } /* error - fall back to linear soln */
      } /* interpolate on log scale */
      
      if(!useNonLinearScaleFlag)
      { /* interpolate on linear scale */
        r= (stVal-lb)/(ub-lb);
        sbVal= (int)(r*sbMax);
      }
    } /*check if non-linear scaling */
    
    return(sbVal);
  } /* cvStateToScrollValue */
  
  
  /**
   * cvRatioToScrollValue() - convert state ratio to ratio-scroller value
   * @param rValF is the ratio scroller value
   * @return value
   */
  int cvRatioToScrollValue(float rValF)
  { /* cvRatioToScrollValue */
    double rVal= (double)rValF;
    int
      lth= (mae.MAX_RATIO_HIST-1),
      hrbIdx= 0;
    
    if(rVal < mae.histRatioBin[0])
      hrbIdx= 0;
    else if(rVal > mae.histRatioBin[lth])
      hrbIdx= lth;
    else
      for(int i=1;i<=lth;i++)
        if(rVal>= mae.histRatioBin[i-1] &&
        rVal<= mae.histRatioBin[i])
        {
          hrbIdx= i;
          break;
        }
    
    return(hrbIdx);
  } /* cvRatioToScrollValue */
  
  
  /**
   * cvScrollValueToRatio() - convert ratio-scroller value to ratio STATE value
   * @param val is the ratio scroller value
   * @param size is the max size of the val range.
   * @return value
   */
  double cvScrollValueToRatio(int val, int size)
  { /* cvScrollValueToRatio */
    float r= (float)val/(float)size;
    int
      scaledValue= (int)(r*(mae.MAX_RATIO_HIST-1)),
      idx= (int)(r*size);
    
    idx= Math.max(0, Math.min(idx, (mae.MAX_RATIO_HIST-1)));
    
    double
    rVal= mae.histRatioBin[idx];
    return(rVal);
  } /* cvScrollValueToRatio */
  
  
  /**
   * clearPostProcessingFlags() - clear special post-processing flags
   * that are then set by particular scroller changes
   */
  void clearPostProcessingFlags()
  { /* clearPostProcessingFlags */
    doRepaint= false;
    runImageOptimizer= false;
    runClusterAnalysis= false;
    runKmeansClusterAnalysis= false;
    runFilter= false;
  } /* clearPostProcessingFlags */
  
  
  /**
   * reRunImageOptimizer() - rerun pseudoarray image optimization after changed state.
   * If it succeeds, the caller needs to do a global repaint.
   * @return true if succeed, in which case the caller needs to repaint.
   */
  boolean reRunImageOptimizer()
  { /* reRunImageOptimizer */
    boolean flag= false;
    return(flag);
  } /* reRunImageOptimizer */
  
  
  /**
   * reRunClusterAnalysis() - rerun cluster analysis after changed state.
   * If it succeeds, the caller needs to do a global repaint.
   * @return true if succeed, in which case the caller needs to repaint.
   * @see ClusterGenes
   * @see ClusterGenes#updateKmeansClusters
   * @see PopupRegistry#updateCurGene
   */
  boolean reRunClusterAnalysis()
  { /* reRunClusterAnalysis */
    boolean flag= false;
    
    /* Do the cluster analysis */
    if(mae.useKmeansClusterCntsDispFlag)
    { /* cluster K-means genes */
      /* Recompute the clusters */
      if(ClusterGenes.activeClusterMethod)
      { /* update an existing K-means node cluster window */
        mae.clg.updateKmeansClusters();
        flag= true;
      }
      else
      { /* remove old Kmeans and regenerate it */
        mae.clg.removePreviousClusterMethod(0);
        mae.clg.createClusterMethod(mae.fc.workingCL, cfg.nbrOfClustersThr,
                                    mae.hps.msListE, mae.hps.nHP_E,
                                    mae.clg.METHOD_CLUSTER_KMEANS,
                                    false /* resetFlag */);
      }
      flag= true;
    } /* cluster K-means genes */
    
    else if((mae.cdb.isValidObjFlag && mae.useSimGeneClusterDispFlag) ||
             mae.useClusterCountsDispFlag)
    { /* cluster similar genes from current gene or gene counts */
      int mid= mae.cdb.objMID;
      mae.pur.updateCurGene(mid, ClusterGenes.UPDATE, null);
      flag= true;
    } /* cluster similar genes from current gene or gene counts */
    
    return(flag);
  } /* reRunClusterAnalysis */
  
  
  /**
   * adjustmentValueChanged() - handle scroller state changed events
   * @see Filter#computeWorkingGeneList
   * @see MAExplorer#repaint
   * @see Util#cvf2s
   * @see #clearPostProcessingFlags
   * @see #cvScrollToStateValue
   * @see #cvStateToScrollValue
   * @see #reRunImageOptimizer
   * @see #reRunClusterAnalysis
   * @see #setThresholdValueBySliderIdx
   */
  public void adjustmentValueChanged(AdjustmentEvent e)
  { /* adjustmentValueChanged */
    
    /* [1] initialize  */
    /* Clear special post-processing flags that are then set by
     * particular scroller changes
     */
    clearPostProcessingFlags();
    
    if(!mae.mReady)
      return;           /* not ready to accept events */
    
    Scrollbar sb= (Scrollbar)e.getSource();
    
    /* [2] Find the scroll bar that changed */
    int idx= -1;
    
    for(int i= 0;i<nSB;i++)
      if(sbBar[i]==sb)
      { /* found sb */
        idx= i;
        break;
      }
    
    if(idx==-1)
      return;                  /* not found, bogus event */
    
    int sbValue= sb.getValue();  /* value from adjustment of SB */
    float stateVal= cvScrollToStateValue(idx, sbValue); /* scaled val */
    
    if(stateVal==sbCurValue[idx])
      return;                  /* no change is a no-op */
    
    /* [2.1] Check if violating Gang scroller bound. If so, then
     * force LB <=UB.
     */
    boolean doGangBoundCheckingFlag= true;
    
    if(doGangBoundCheckingFlag)
    { /* force Gang scroller bound checking and correction */
      int
        ltIdx= sbLTidx[idx],  /* Gang UB index of Lower bound */
        gtIdx= sbGTidx[idx];  /* Gang LB index of Upper bound */
      
      if(ltIdx!=-1)
      { /* check UB gang scroller value against this Lower Bound */
        float gtStateVal= sbCurValue[ltIdx]; /* gang UB scroller value */
        /*
        if(mae.DBUG_SCROLLERS_FLAG)
          System.out.println("SSCR-AVC.2.1.LB: sb[idx:"+idx+":("+sbName[idx]+
                             ")] sbValue="+ sbValue+" stateVal="+stateVal+
                             "\n   sbCurV[idx]="+Util.cvf2s(sbCurValue[idx],3)+
                             " sbLB["+idx+"]="+Util.cvf2s(sbLowerBound[idx],3)+
                             " sbUB["+idx+"]="+Util.cvf2s(sbUpperBound[idx],3)+
                             " sbSize["+idx+"]="+Util.cvf2s(sbSize[idx],3)+
                             "\n   gang sb[ltIdx:"+ltIdx+":("+sbName[ltIdx]+
                             ")] sbCurV[ltIdx]="+Util.cvf2s(sbCurValue[ltIdx],3)+
                             "\n   sbLB[lt:"+ltIdx+"]="+Util.cvf2s(sbLowerBound[ltIdx],3)+
                             " sbUB[lt:"+ltIdx+"]="+Util.cvf2s(sbUpperBound[ltIdx],3)+
                             " sbSize[lt:"+ltIdx+"]="+Util.cvf2s(sbSize[ltIdx],3));
        */
        if(stateVal > gtStateVal)
        { /* (potential UB) > LB, set new UB to the old LB value */
          stateVal= gtStateVal;
          sbValue= cvStateToScrollValue(ltIdx,gtStateVal);
          sb.setValue(sbValue);
        }
      }
      
      if(gtIdx!=-1)
      { /* check LB gang scroller value against this Upper Bound */
        float
        ltStateVal= sbCurValue[gtIdx]; /* gang LB scroller value */
        /*
        if(mae.DBUG_SCROLLERS_FLAG)
          System.out.println("SSCR-AVC.2.2.GB: sb[idx:"+idx+":("+sbName[idx]+
                             ")] sbValue="+ sbValue+" stateVal="+stateVal+
                             "\n   sbCurV[idx]="+Util.cvf2s(sbCurValue[idx],3)+
                             " sbLB["+idx+"]="+Util.cvf2s(sbLowerBound[idx],3)+
                             " sbUB["+idx+"]="+Util.cvf2s(sbUpperBound[idx],3)+
                             " sbSize["+idx+"]="+Util.cvf2s(sbSize[idx],3)+
                             "\n   gang sb[gtIdx:"+gtIdx+":("+sbName[gtIdx]+
                             ")] sbCurV[gtIdx]="+Util.cvf2s(sbCurValue[gtIdx],3)+
                             "\n   sbLB[lt:"+gtIdx+"]="+Util.cvf2s(sbLowerBound[gtIdx],3)+
                             " sbUB[lt:"+gtIdx+"]="+Util.cvf2s(sbUpperBound[gtIdx],3)+
                             " sbSize[lt:"+gtIdx+"]="+Util.cvf2s(sbSize[gtIdx],3));
        */
        if(stateVal < ltStateVal)
        { /* (potential LB) > UB, set new LB to the old UB value */
          stateVal= ltStateVal;
          sbValue= cvStateToScrollValue(gtIdx,ltStateVal);
          sb.setValue(sbValue);
        }
      }
    } /* force Gang scroller bound checking and correction */
    
    /* [2.2] Save current scroller STATE value */
    sbCurValue[idx]= cvScrollToStateValue(idx, sbValue);    
    
    /* [3] Scale scroller value to state value. */
    /*
    if(mae.CONSOLE_FLAG)
    {
      mae.fio.logMsgln("SSCR-AVC.3: sb[#"+idx+":("+sbName[idx]+
                       ")] sbValue="+ sbValue+
                       " sbCurV[idx]="+Util.cvf2s(sbCurValue[idx],3)+
                       "\n          sbLB["+idx+"]="+Util.cvf2s(sbLowerBound[idx],3)+
                       " sbUB["+idx+"]="+Util.cvf2s(sbUpperBound[idx],3)+
                       " sbSize["+idx+"]="+Util.cvf2s(sbSize[idx],3));
      int
        ltIdx= sbLTidx[idx],  // Gang UB index of Lower bound
        gtIdx= sbGTidx[idx];  // Gang LB index of Upper bound
     String
       ltGangName= (ltIdx==-1) ? "*" : sbName[ltIdx],
       gtGangName= (gtIdx==-1) ? "*" : sbName[gtIdx],
       ltCurVal= (ltIdx==-1) ? "*" : Util.cvf2s(sbCurValue[ltIdx],3),
       gtCurVal= (gtIdx==-1) ? "*" : Util.cvf2s(sbCurValue[gtIdx],3);
      
      mae.fio.logMsgln("     gang sbLTidx["+idx+"]="+ltIdx+
                       "=("+ltGangName+
                       "), sbGTidx["+idx+"]=["+gtIdx+
                       ":("+gtGangName+")]"+
                       "\n          sbCurV[ltGang:"+ltIdx+"]="+ltCurVal+
                       ", sbCurV[gtGang:"+gtIdx+"]=("+gtCurVal+")");
     }
    */
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("  SB: Min="+sb.getMinimum()+
                       " Max="+sb.getMaximum()+
                       " LineI="+sb.getLineIncrement()+
                       " BlockI="+sb.getBlockIncrement()+
                       " UnitI="+sb.getUnitIncrement()+
                       " PageI="+sb.getPageIncrement()+
                       " Vis="+sb.getVisible()+
                       " VisA="+sb.getVisibleAmount());
      */
    
     /* [4] Set slider threshold value by slider index.It returns true if changed a
      * slider and this has the side effect of changing one or more flags may be set
      * including: runFilter, runImageOptimizer, runClusterAnalysis,
      *            runKmeansClusterAnalysis.
      */
    if(! setThresholdValueBySliderIdx(idx, sbCurValue[idx]))
      return;
    
    /* [4.1] Update the sbLblValue[i] */
    String ivStr= "" +(int)sbCurValue[idx];
    if(sbPrecision[idx]>0)
      ivStr= Util.cvf2s(sbCurValue[idx],sbPrecision[idx]);
    String sLbl=  ivStr;
    sbLblValue[idx].setText(sLbl);
    
    /* [5] Do cluster analysis if required. Rerun cluster analysis after
     * changed state. If it succeeds, the caller needs to do a global repaint.
     */
    if(runClusterAnalysis || runKmeansClusterAnalysis)
    { /* do the cluster analysis and then repaint */
      if(reRunClusterAnalysis())
        doRepaint= true;
    }
    
    /* [5.1] rerun pseudoarray image optimization after changed state.
     * If it succeeds, the caller needs to do a global repaint.
     */
    if(runImageOptimizer)
    { /* do the pseudoarray image optimization and then repaint */
      if(reRunImageOptimizer())
        doRepaint= true;
    }
    
    /* [6] try to run filter and repaint */
    if(runFilter)
    {
      mae.fc.computeWorkingGeneList();
      doRepaint= true;
    }
    
    /* [7] just repaint */
    if(doRepaint)
    {
      mae.repaint();
      //mae.is.repaint();
    }
  } /* adjustmentValueChanged */
  
  
  /**
   * setThresholdValueBySliderIdx() - set slider threshold value by slider index
   * @param idx is the index of the slider value in the sbName[]
   * @return false if invalid index, true if valid index. Need to check
   * additional flags that may be set here.
   *<BR>
   * Side effects: one or more flags may be set including:
   *  runFilter, runImageOptimizer, runClusterAnalysis, runKmeansClusterAnalysis
   *<BR>
   * Note the flags should be cleared BEFORE calling this method.
   * @see #clearPostProcessingFlags()
   */
  boolean setThresholdValueBySliderIdx(int idx, float value)
  { /* setThresholdValueBySliderIdx */
    switch(idx)
    { /* stuff scaled value into state variable */
      case idxSI1:               /* SI1 value*/
        mae.sit1= value;
        runFilter= (useFilterCounters[idxSI1] > 0);
        break;
        
      case idxSI2:		          /* SI2 value*/
        mae.sit2= value;
        runFilter= (useFilterCounters[idxSI2] > 0);
        break;
        
      case idxT1:               /* I1 (T1) value*/
        mae.t1= value;
        runFilter= (useFilterCounters[idxT1] > 0);
        break;
        
      case idxT2:		            /* I2 (T2) value*/
        mae.t2= value;
        runFilter= (useFilterCounters[idxT2] > 0);
        break;
        
      case idxR1:		            /* R1 value*/
        mae.r1= value;
        runFilter= (useFilterCounters[idxR1] > 0);
        break;
        
      case idxR2:		            /* R2 value*/
        mae.r2= value;
        runFilter= (useFilterCounters[idxR2] > 0);
        break;
        
      case idxZ1:		            /* Z1 value*/
        mae.r1= value;
        runFilter= (useFilterCounters[idxZ1] > 0);
        break;
        
      case idxZ2:		            /* Z2 value*/
        mae.r2= value;
        runFilter= (useFilterCounters[idxZ2] > 0);
        break;
        
      case idxCR1:		          /* CR1 value*/
        mae.cr1= value;
        runFilter= (useFilterCounters[idxCR1] > 0);
        break;
        
      case idxCR2:		          /* CR2 value*/
        mae.cr2= value;
        runFilter= (useFilterCounters[idxCR2] > 0);
        break;
        
      case idxCZ1:		        /* CZ1 value*/
        mae.cr1= value;
        runFilter= (useFilterCounters[idxCZ1] > 0);
        break;
        
      case idxCZ2:		        /* CZ2 value*/
        mae.cr2= value;
        runFilter= (useFilterCounters[idxCZ2] > 0);
        break;
        
      case idxPvalue:		        /* p-value thr value*/
        cfg.pValueThr= value;
        runFilter= (useFilterCounters[idxPvalue] > 0);
        break;
        
      case idxSpot_CV:		        /* Spot CV thr value*/
        cfg.spotCVthr= value;
        runFilter= (useFilterCounters[idxSpot_CV] > 0);
        break;
        
      case idxRadius:	                 /* Radius*/
        mae.spotRad= (int)value;
        runImageOptimizer= true;
        break;
        
      case idxClusterDist:                /* clusterDist threshold */
        cfg.clusterDistThr= value;
        runClusterAnalysis= true;         /* has different graphic*/
        break;
       
      case idxNbrClusters:                /* # clusters threshold */
        cfg.nbrOfClustersThr= (int)value;
        runKmeansClusterAnalysis= true;   /* has different graphic*/
        break;
        
      case idxDiffXY:		          /* DiffXY threshold*/
        cfg.diffThr= value;
        runFilter= (useFilterCounters[idxDiffXY] > 0);
        break;
        
      case idxPctOK:		          /* Percent SI OK threshold*/
        cfg.pctOKthr= value;
        runFilter= (useFilterCounters[idxPctOK] > 0);
        break;
        
      case idxdetValueSpotThr:	 /* Spot Detection Valuef thr value*/
        cfg.detValueSpotThr= value;
        runFilter= (useFilterCounters[idxdetValueSpotThr] > 0);
        break;
        
      default:
        return(false);
    } /* stuff scaled value into state variable */
    
    return(true);    
  } /* setThresholdValueBySliderIdx */
  
  
  /**
   * actionPerformed() - Handle button clicks
   * @see #clearActiveScrollers
   * @see #popdownScrollers
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    
    if (cmd.equals("Done")) 
    { /* close window */
      clearActiveScrollers();
      popdownScrollers();
      /* allow popdown if original window closing */
      allowPopdownOnWindowClosing= false;
      
      /* [NOTE] This does NOT clear the corresponding Filter or Plot method */
      
      int nActive= nbrActiveScrollers();
      if(nActive>0)
      { /* we were showing selected filters, go regenerate it */
        regenerateScrollers(false);
      }
    }
    
    else if(cmd.equals("Apply"))
    { /* apply it NOW to the data filter */      
      mae.fc.computeWorkingGeneList();
      doRepaint= true;
    }
    
    else if(cmd.startsWith("Adjust all"))
    { /* adjust all filters */
      clearActiveScrollers();
      popdownScrollers();
      allowPopdownOnWindowClosing= false;
      regenerateScrollers(true);
    }
    
    else if(cmd.startsWith("Adjust active"))
    { /* adjust all filters */
      clearActiveScrollers();
      popdownScrollers();
      allowPopdownOnWindowClosing= false;
      regenerateScrollers(false);
    }    
  } /* actionPerformed */
  
  
  /**
   * popdownScrollers() - close down the window using dispose
   */
  void popdownScrollers()
  { /* popdownScrollers*/
    if(pgrid != null)
      this.remove(pgrid);
    if(lowerControlPanel != null)
      this.remove(lowerControlPanel);
    isVisible= false;
    this.dispose();		/* get rid of scroller window */
  } /* popdownScrollers */
  
  
  /**
   * nbrActiveScrollers() - return the number of active scrollers
   * and copy scaled scroller data to the state variables.
   * @return # of active scrollers
   */
  int nbrActiveScrollers()
  { /* nbrActiveScrollers */
    return(nbrActiveScrollersUseCounters());
  } /* nbrActiveScrollers */
  
    
  /**
   * nbrActiveScrollersUseCounters() - return the number of active scrollers
   * and copy scaled scroller data to the state variables.
   * This is based on the user counters being > 0.
   * @return # of active scrollers
   */
  int nbrActiveScrollersUseCounters()
  { /* nbrActiveScrollersUseCounters*/
    for (int i=0;i<nSB;i++)
      sbActive[i]= false;
    
    if(useFilterCounters[idxSI1]>0 || useAllScrollersFlag)
    {
      sbActive[idxSI1]= true;
      mae.sit1= sbCurValue[idxSI1];
    }
    
    if(useFilterCounters[idxSI2]>0 || useAllScrollersFlag)
    {
      sbActive[idxSI2]= true;
      mae.sit2= sbCurValue[idxSI2];
    }
    
    if(useFilterCounters[idxT1]>0 || useAllScrollersFlag)
    {
      sbActive[idxT1]= true;
      mae.t1= sbCurValue[idxT1];
    }
    
    if(useFilterCounters[idxT2]>0 || useAllScrollersFlag)
    {
      sbActive[idxT2]= true;
      mae.t2= sbCurValue[idxT2];
    }
    
    if(useFilterCounters[idxZ1]>0 || useAllScrollersFlag)
    {
      sbActive[idxZ1]= true;
      mae.r1= sbCurValue[idxZ1];
    }
    
    if(useFilterCounters[idxZ2]>0 || useAllScrollersFlag)
    {
      sbActive[idxZ2]= true;
      mae.r2= sbCurValue[idxZ2];
    }
    
    if(useFilterCounters[idxR1]>0 || useAllScrollersFlag)
    {
      sbActive[idxR1]= true;
      mae.r1= sbCurValue[idxR1];
    }
    
    if(useFilterCounters[idxR2]>0 || useAllScrollersFlag)
    {
      sbActive[idxR2]= true;
      mae.r2= sbCurValue[idxR2];
    }

    if(useFilterCounters[idxCZ1]>0 || useAllScrollersFlag)
    {
      sbActive[idxCZ1]= true;
      mae.cr1= sbCurValue[idxCZ1];
    }

    if(useFilterCounters[idxCZ2]>0 || useAllScrollersFlag)
    {
      sbActive[idxCZ2]= true;
      mae.cr2= sbCurValue[idxCZ2];
    }
    
    if(useFilterCounters[idxCR1]>0 || useAllScrollersFlag)
    {
      sbActive[idxCR1]= true;
      mae.cr1= sbCurValue[idxCR1];    
    }
    
    if(useFilterCounters[idxCR2]>0 || useAllScrollersFlag)
    {
      sbActive[idxCR2]= true;
      mae.cr2= sbCurValue[idxCR2];      
    }
    
    if(useFilterCounters[idxPvalue]>0 || useAllScrollersFlag)
    {
      sbActive[idxPvalue]= true;
      cfg.pValueThr= sbCurValue[idxPvalue];
    }
    
    if(useFilterCounters[idxSpot_CV]>0 || useAllScrollersFlag)
    {
      sbActive[idxSpot_CV]= true;
      cfg.spotCVthr= sbCurValue[idxSpot_CV];
    }
    
    if(useAllScrollersFlag)
    {
      sbActive[idxRadius]= true;
      mae.spotRad= (int)sbCurValue[idxRadius];
    }
    
    if(useFilterCounters[idxClusterDist]>0 || useAllScrollersFlag)
    {
      sbActive[idxClusterDist]= true;
      cfg.clusterDistThr= sbCurValue[idxClusterDist];
    }
    
    if(useFilterCounters[idxNbrClusters]>0 || useAllScrollersFlag)
    {
      sbActive[idxNbrClusters]= true;
      cfg.nbrOfClustersThr= (int)sbCurValue[idxNbrClusters];
    }
    
    if(useFilterCounters[idxDiffXY]>0 || useAllScrollersFlag)
    {
      sbActive[idxDiffXY]= true;
      cfg.diffThr= (int)sbCurValue[idxDiffXY];
    }
    
    if(useFilterCounters[idxPctOK]>0 || useAllScrollersFlag)
    {
      sbActive[idxPctOK]= true;
      cfg.pctOKthr= sbCurValue[idxPctOK];
    }
    
    if(useFilterCounters[idxQualThr]>0 || useAllScrollersFlag)
    {
      sbActive[idxQualThr]= true;
      cfg.qualThr= (int)sbCurValue[idxQualThr];
    }
    
    if(useFilterCounters[idxdetValueSpotThr]>0 || useAllScrollersFlag)
    {
      sbActive[idxdetValueSpotThr]= true;
      cfg.detValueSpotThr= (int)sbCurValue[idxdetValueSpotThr];
    }
    
    int nActive= 0;
    for (int i=0;i<nSB;i++)
      if(sbActive[i])
        nActive++;
    
    return(nActive);
  } /* nbrActiveScrollersUseCounters */
  
  
  /**
   * regenerateScrollers() - regenerate scroller window
   * @param popupAllScrollersFlag will popup a window with ALL scrollers active
   * @see #addScroller
   * @see #clearActiveScrollers
   * @see #createStateScrollers
   * @see #nbrActiveScrollers
   * @see #popdownScrollers
   */
  void regenerateScrollers(boolean popupAllScrollersFlag)
  { /* regenerateScrollers*/
    allowPopdownOnWindowClosing= popupAllScrollersFlag;
                         /* allow popdown if original window closing */
    if(mae.stateScr.isVisible)
      popdownScrollers();             /* remove old scroller since contents
                                       * may have changed. */
    /* Create new scrollers window */
    clearActiveScrollers();
    
    /* Reset the sbActive[] from global switches and reset flags */
    int nActive= nbrActiveScrollers();
    if(nActive==0 && !popupAllScrollersFlag)
      return;                         /* nothing to show, don't pop it up */
    
    /* Add scrollers which are active. If popup All, then do that. */
    for(int idx= 0;idx<nSB;idx++)
      if(sbActive[idx] || popupAllScrollersFlag)
        addScroller(sbName[idx]);
    
    createStateScrollers();
  } /* regenerateScrollers */
  
  
  /**
   * setRangeMaps() - set norm dependent scrollers based normalization method.
   * @see #setRangeMapsFromState
   */
  void setRangeMaps()
  { /* setRangeMaps */
    /* [TODO] may want to decrease precision if switch to large #s. */
    
    if(mae.CONSOLE_FLAG)
    { /* set the range map from the .mae state data */
      setRangeMapsFromState();
      return;
    }
    
    /* Otherwise, compute range of data for ALL samples with current 
     * normalization and then reset the scroller maps
     */
    float
      minVal= 10000000000.0F,
      maxVal= -10000000000.0F,
      minRawVal= 10000000000.0F,
      maxRawVal= -10000000000.0F;
    MaHybridSample ms;
    
    if(mae.hps!=null)
      for(int h=1;h<=mae.hps.nHP;h++)
      { /* process each sample */
        ms= mae.hps.msList[h];
        minVal= Math.min(minVal,ms.minDataS);
        maxVal= Math.max(maxVal,ms.maxDataS);
        minRawVal= Math.min(minRawVal,ms.minRawS);
        maxRawVal= Math.max(maxRawVal,ms.maxRawS);
      }
    float maxDiff= (float)Math.abs(maxVal-minVal);
    
    if(!mae.useRatioDataFlag)
    { /* Hack to fix bug with reversed ...RawVals for non-ratio data */
      minRawVal= minVal;
      maxRawVal= maxVal;
    }
    
    sbCurValue[idxSI1]= minRawVal;
    sbLowerBound[idxSI1]= minRawVal;
    sbUpperBound[idxSI1]= maxRawVal;
    
    sbCurValue[idxSI2]= maxRawVal;
    sbLowerBound[idxSI2]= minRawVal;
    sbUpperBound[idxSI2]= maxRawVal;
    
    sbCurValue[idxT1]= minVal;
    sbLowerBound[idxT1]= minVal;
    sbUpperBound[idxT1]= maxVal;
    
    sbCurValue[idxT2]= maxVal;
    sbLowerBound[idxT2]= minVal;
    sbUpperBound[idxT2]= maxVal;
    
    sbCurValue[idxClusterDist]= maxDiff/10;
    sbLowerBound[idxClusterDist]= 0;
    sbUpperBound[idxClusterDist]= maxDiff;
    
    sbCurValue[idxDiffXY]= maxDiff/10;
    sbLowerBound[idxDiffXY]= 0;
    sbUpperBound[idxDiffXY]= maxDiff;
    
    sbCurValue[idxPctOK]= mae.cfg.pctOKthr;
    sbLowerBound[idxPctOK]= 0.0F;
    sbUpperBound[idxPctOK]= 100.0F;
  } /* setRangeMaps */
  
  
  /**
   * setRangeMapsFromState() - set normalization dependent scrollers based on norm. method.
   * @see MaHybridSample#scaleIntensData
   */
  void setRangeMapsFromState()
  { /* setRangeMapsFromState */
    /* [TODO] 1. may want to decrease precision if switch to large #s.
     * [TODO] 2. NOTE: these ranges depend on the normalization mode, so we need
     * to take that into account.
     */
    /* Compute range of data for ALL samples with current normalization
     * and then reset the scroller maps
     */
    float
      minVal= 10000000000.0F,
      maxVal= -10000000000.0F,
      minRawVal= 10000000000.0F,
      maxRawVal= -10000000000.0F,
      minDataS,
      maxDataS,
      minRawS,
      maxRawS,
      hpMinVal,
      hpMaxVal,
      hpMinRawVal,
      hpMaxRawVal,
      maxDiff;
    MaHybridSample ms;
    MAEPlugin.Normalization obj= null;
      
    /* [1] Setup normalization plugin if needed */
    if(mae.mja.getNormalizationState())
    { /* Scale using the MAEPlugin Normalization plugin */      
      obj= mae.mja.getActiveNormalization();      
      obj.resetPipeline(0);
    }    
  
    /* [2] Find the global extrema over all samples in HP-E */
    for(int h=1;h<=mae.hps.nHP;h++)
    { /* process each sample */
      ms= mae.hps.msList[h];
      
      /* If active Normalization plugin, then scale extrema */
      if(obj!=null)
      { /* scale the extrema and save back into the sample */
        float extrema[]= obj.calcIntensityScaleExtrema(ms.maxRI, ms.minRI,
                                                       ms.maxRaw, ms.minRaw,
                                                       h);
         ms.minDataS= extrema[0];
         ms.maxDataS= extrema[1];
         ms.minRawS= extrema[2];
         ms.maxRawS= extrema[3];
      } /* scale the extrema and save back into the sample */
      
      /* remap it */
      minDataS= ms.minDataS;
      maxDataS= ms.maxDataS;
      minRawS= ms.minRawS;
      maxRawS= ms.maxRawS;
      
      /* [TODO] Need to give it the gidMAX and gidMIN values in case
       * need them for scaling with Normalization Plugins.
       */
      hpMinVal= ms.scaleIntensData(minDataS);
      hpMaxVal= ms.scaleIntensData(maxDataS);
      hpMinRawVal= ms.scaleIntensData(minRawS);
      hpMaxRawVal= ms.scaleIntensData(maxRawS);
      
      minVal= Math.min(minVal,hpMinVal);
      maxVal= Math.max(maxVal,hpMaxVal);
      minRawVal= Math.min(minRawVal,hpMinRawVal);
      maxRawVal= Math.max(maxRawVal,hpMaxRawVal);
    } /* process each sample */
    
    maxDiff= (float)Math.abs(maxVal-minVal);
    
    if(!mae.useRatioDataFlag)
    { /* Hack to fix bug with reversed ...RawVals for non-ratio data */
      minRawVal= minVal;
      maxRawVal= maxVal;
    }
    
    /* [3] Only change the values affected by normalization changes.
     * And then, only change current value if outside of new limits.
     */
    /* Spot intensity */
    sbCurValue[idxSI1]= mae.sit1;
    if(sbCurValue[idxSI1]<minRawVal || sbCurValue[idxSI1]>maxRawVal)
      sbCurValue[idxSI1]= minRawVal;
    sbLowerBound[idxSI1]= minRawVal;
    sbUpperBound[idxSI1]= maxRawVal;
    
    sbCurValue[idxSI2]= mae.sit2;
    if(sbCurValue[idxSI2]>maxRawVal || sbCurValue[idxSI2]<sbCurValue[idxSI1])
      sbCurValue[idxSI2]= maxRawVal;
    sbLowerBound[idxSI2]= minRawVal;
    sbUpperBound[idxSI2]= maxRawVal;
    
    /* Sample intensity */
    sbCurValue[idxT1]=  mae.t1;
    if(sbCurValue[idxT1]<minVal || sbCurValue[idxT1]>minVal)
      sbCurValue[idxT1]= minVal;
    sbLowerBound[idxT1]= minVal;
    sbUpperBound[idxT1]= maxVal;
    
    sbCurValue[idxT2]= mae.t2;
    if(sbCurValue[idxT2]>maxVal || sbCurValue[idxT2]<sbCurValue[idxT1])
      sbCurValue[idxT2]= maxVal;
    sbLowerBound[idxT2]= minVal;
    sbUpperBound[idxT2]= maxVal;
    
    //sbCurValue[idxClusterDist]= maxDiff/10;
    //sbLowerBound[idxClusterDist]= 0;
    //sbUpperBound[idxClusterDist]= maxDiff;
    
    sbCurValue[idxDiffXY]= cfg.diffThr;
    if(sbCurValue[idxDiffXY]<0 || sbCurValue[idxDiffXY]>maxDiff)
      sbCurValue[idxDiffXY]= maxDiff/10;
    sbLowerBound[idxDiffXY]= 0;
    sbUpperBound[idxDiffXY]= maxDiff;
    
    /* [4] Finish the MAEPlugin Normalization plugin if it is active*/      
    if(obj!=null) 
      obj.finishPipeline(0);
  } /* setRangeMapsFromState */
  
  
  /**
   * windowClosing() - close the window - don't allow unless doing ALL scrollers.
   * @see Util#cvf2s
   * @see Util#saveCmdHistory
   * @see Util#showMsg1
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see #clearActiveScrollers
   * @see #popdownScrollers
   */
  public void windowClosing(WindowEvent e)
  { /* windowClosing */
    if(allowPopdownOnWindowClosing)
    { /* allow popdown if original window closing */
      if(util.historyIsVisibleFlag || util.msgIsVisibleFlag)
      { /* dump Scroller state to message logging - save pseudo command */
        String stateStr= "";
        for(int i=0;i<nSB;i++)
          stateStr += "Threshold "+sbName[i]+" = "+
                      util.cvf2s(sbCurValue[i],4)+"\n";
        util.saveCmdHistory("Show state of all data Filter threshold scrollers",
                            false);
        util.saveMsgHistory(stateStr, false) ; /* only save in messages history */
      }
      
      clearActiveScrollers();
      popdownScrollers();
      allowPopdownOnWindowClosing= false;
    } /* allow popdown if original window closing */
    else
    {
      util.showMsg1("You can't popup down the state scroller window while using",
                    Color.red,Color.white);
      util.showMsg2("thresholds for particular Filters or Clustering. It will be removed",
                    Color.red,Color.white);
      util.showMsg3("automatically when you finish clustering or using those filters.",
                    Color.red,Color.white);
      
      Util.popupAlertMsg("You can't popup down the state scroller window",
                         "You can't popup down the state scroller window while using thresholds\n"+
                         "for particular Filters or Clustering. It will be removed automatically\n"+
                         "when you finish clustering or using those filters.",
                         10, 80);
    }
  } /* windowClosing */
  
  
  public void windowActivated(WindowEvent e)  {}
  public void windowClosed(WindowEvent e)  { }
  public void windowDeactivated(WindowEvent e)  {}
  public void windowDeiconified(WindowEvent e)  {}
  public void windowIconified(WindowEvent e)  {}
  public void windowOpened(WindowEvent e)  {}
  
  
  /**
   * mouseEntered() - put mouse over data in MouseOver text area
   */
  public void mouseEntered(MouseEvent me)
  { /* mouseMoved */
    Object meObj= me.getSource();
    Label lbl= null;
    Scrollbar sb= null;
    
    if((meObj instanceof Label))
      lbl= (Label)meObj;
    if((meObj instanceof Scrollbar))
      sb= (Scrollbar)meObj;
    int idx= -1;
    
    for (int i=0;i<nSB;i++)
      if((lbl!=null && (lbl==sbLblName[i] || lbl==sbLblValue[i])) ||
         (sb!=null && sb==sbBar[i]))
      {
        idx= i;
        break;
      }
    if(idx==-1)
      return;                     /* ForGetAboutIt! */
    
    String data= sbMouseMsg[idx];
    if(data!=null && data.length()>0)
      mouseoverTA.setText(data);
    //else
    //.mouseoverTA.setText("");
  } /* mouseMoved */
  
  
  /**
   * mouseExited() - clear mouseover text area.
   */
  public void mouseExited(MouseEvent me)
  { /* mouse */
    mouseoverTA.setText("");
  } /* mouse */
  
  
  public void mouseMoved(MouseEvent me) { }
  public void mouseClicked(MouseEvent me)  { }
  public void mousePressed(MouseEvent me)  { }
  public void mouseReleased(MouseEvent me)  { }
  
  
} /* end of class StateScrollers */

