/* File: Config.java */

import java.awt.*;
import java.util.*;

/**
 * The class holds MAExplorer configuration variables that constitute par of the global state.
 * Data is read from the MaConfiguration.txt (or whatever it is called) 
 * input file when MAExplorer is first started. Then (navem,value) data read 
 * from the .mae startup file (in the case of the stand-alone system) or 
 * from <PARAM>s (in the case of APPLETS) may overide this.
 * <P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.27 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ConfigTable
 * @see GetParams
 */

class Config
{
  /** link to global instance of MAExplorer */
  private MAExplorer
    mae;
  /** link to global instance of GetParams */                    
  private GetParams 
    gp;                     
  
  /** Flag: once only code for single call to getStateParamValues() */ 
  private boolean
    didGetParms;            
    
       
  /* --- Default PARAM values and ranges ---
   * Note: need following statics for preloaded Scrollers data. 
   * Note: these are also set in the Config .txt file.
   */
  /** default uArray canvas horizontal size */
  final static int
    DEF_CANVAS_HSIZE= 1100;
  /** default uArray canvas vertical size */
  final static int
    DEF_CANVAS_VSIZE= 2000;  
  /** default maximum intensity. This is replaced by actual maximum... */
  final static int
    DEF_MAX_INTENSITY= 65535;    
  
  /** Default spot radius (-1) */  
  final static int
    DEF_RADIUS= 7;
  /** Minimum spot radius allowed */  
  final static int            
    MIN_RADIUS= 3;
  /** MAximum spot radius allowed */  
  final static int
    MAX_RADIUS= 20;
  
  /** Default cluster distance */ 
  final static float 
    DEF_CLUSTER_DIST= 10.0F;
  /** Minimum cluster distance */ 
  final static float 
    MIN_CLUSTER_DIST= 0.0F;
  /** Maximum cluster distance */ 
  final static float 
    MAX_CLUSTER_DIST= 400.0F;
     
  /** Default # of clusters */
  final static int 
    DEF_NBR_CLUSTERS= 6;
  /** Minimum # of clusters */
  final static int
    MIN_NBR_CLUSTERS= 2;
  /** Maximum # of clusters */
  final static int
    MAX_NBR_CLUSTERS= 100;
   
  /** DEFAULT: t-test threshold */
  final static float
    DEF_P_VALUE_THR= 0.20F;
  /** DEFAULT: SPOT CV threshold */ 
  final static float     
    DEF_SPOT_CV_THR= 0.25F; 
  /** DEFAULT Diff(HP-X,HP-Y) in [minNorm:maxNorm] as fct. Norm method */    
  final static float      
    DEF_DIFF_THR= 1.96F;    
    
   
  /* --- State Variables set from Applet <PARAM> --- */
  /** PARAM "databaseName" */
  String
    database;               
  /** PARAM "dbSubset" database subset*/
  String
    dbSubset;              
  /** PARAM default M.A. analysis pgm */
  String
    maAnalysisProg;         
  /** PARAM "species" UniGene Species code */
  String
    unigeneSpecies;         
				  
  /** base addr PARAM "writeFileCGIurl" */ 
  String  
    writeFileCGIurl;
      
  /** base addr PARAM "genBankCloneURL" */
  String
    genBankCloneURL;
  /** base addr PARAM "genBankCloneURLepilogue" */
  String
    genBankCloneURLepilogue;
  /** base addr PARAM "uniGeneURL" */
  String
    uniGeneURL;
  /** base addr PARAM "uniGeneClusterIdURL" */
  String
    uniGeneClusterIdURL;
  /** base addr PARAM "omimURL" */
  String
    omimURL;
  /** base addr PARAM "dbEstURL" */
  String
    dbEstURL;
  /** base addr PARAM "mAdbURL" */
  String
    mAdbURL;
  /** base addr PARAM "gbid2LocusLinkURL" */
  String
    gbid2LocusLinkURL;
  /** base addr PARAM "locusLinkURL" */
  String
    locusLinkURL;
  /** base addr PARAM "jaxURL" */
  String
    jaxURL;
  /** base addr PARAM "genBankAccURL" */
  String
    genBankAccURL;
  /** base addr PARAM "medMinerURL" */
  String
    medMinerURL;
  /** base addr PARAM "medMinerURLepilogue" */
  String
    medMinerURLepilogue;
  /** base addr PARAM "swissProtURL" */
  String
    swissProtURL;
  /** base addr PARAM "pirURL" */    
  String
    pirURL;
  /** base addr PARAM "IMAGE2unigeneURL" */
  String
    IMAGE2unigeneURL;
  /** base addr PARAM "IMAGE2GenBankURL" */
  String
    IMAGE2GenBankURL; 
  
  /** Get HTML list of www.ncbi.nlm.nih.gov/geo Gene Express Omnibus 
   * GEO microarray platforms numbered GPL4, GPL5, ..." 
   */
  String  
      geoListOfPlatformsURL;
  /** Get full HTML list of a specific GEO microarray platform. Append the platoform.
   *  e.g. "GPL80" 
   */
  String  
      geoFullDataTableURL; 
  /** GEO platform Identifier of the form "GPL"+<digits>. E.g. GPL80 */
  String
    geoPlatformID;
  
  /** base addr PARAM "geneCardURL" */ 
  String
    geneCardURL;
  /** base addr PARAM "histologyURL" */ 
  String
    histologyURL;
  /** base addr PARAM "modelsURL" */ 
  String
    modelsURL;

  /* --- Generic arrays of Help menu, Genomic URL menu, Plugin Menu data ---- */
 
  /** size of sHelpMenu[] */
  int
    nHelpMenus;
 /** PARAM menu entries "HelpMenu"+i */
  String
    sHelpMenu[];           
  /** PARAM URL entries "HelpURL"+i */
  String
    sHelpURL[];
    
  /* The following is a way to specify Genomic IDs and URLs
   * to associated Web databases. These can be used to overide
   * the internal IDs' (e.g. GenBankAcc, etc.) or to define new
   * databases and ID's (e.g. FlyBase, etc.).
   */
  /** size of sGenomicMenu[] */
  int
    nGenomicMenus;
  /** PARAM menu entries "GenomicMenu"+i. An example might be "FlyBase" */
  String
    sGenomicMenu[];         
  /** PARAM URL entries "GenomicURL"+i */    
  String
    sGenomicURL[];          
  /** PARAM: URL entries "GenomicURLepilogue"+i */
  String
    sGenomicURLepilogue[]= null;
  /** PARAM ID required entries ("GenomicIDreq"+i). 
   * These should be within the legal list of identifiers. 
   * Eg Clone_ID, GenBankAcc, etc. An example of a new
   * GIPO id might be "FlyBaseID".
   */
  String
    sGenomicIDreq[];
        
  /** size of sPluginMenus[] */
  int
    nPluginMenus;
  /** PARAM menu entries "PluginMenuName"+i.       E.g. "New sample report" */
  String
    sPluginMenuName[]; 
  /** PARAM menu entries "PluginMenuStubName"+i.   E.g. "ReportMenu:sample"  */
  String
    sPluginMenuStubName[]; 
  /** PARAM menu entries "PluginClassFile"+i.      E.g. "NewReport.class" */
  String
    sPluginClassFile[];  
  /** PARAM menu entries "PluginCallAtStartup"+i.  E.g. "TRUE", "FALSE" or "BOTH" 
   * of "InstallInMenu", "RunOnStartup", "NoInstall"
  */
  String
    sPluginCallAtStartup[];  
  
  /* --- various parameters --- */  
  /** Configuration file where we got the config data ... */
  String
    configFile; 
  /** PARAM "quantFileExt" i.e. ".quant" file extension default */                 
  String 
    quantFileExt;            
  /** PARAM "samplesDBfile" */
  String                
    sampDBfile;		                         
  /** PARAM "maInfoFile" or URL (opt) */
  String 
    maInfoFile;           
  /** PARAM "gipoFile" or URL */
  String 
    gipoFile;
  
  /* --- [FUTURE] add functionality to support mAdb-ATC server --- */
  /** PARAM "baseCgiURLquant" */
  String
    baseCgiURLquant;        
  /** PARAM "baseCgiURLsamples" */ 
  String
    baseCgiURLsamples;		 
  /** PARAM "baseCgiURLgipo" */
  String
    baseCgiURLgipo;         
  /** PARAM "baseCgiURLgeneClassNames" */
  String
    baseCgiURLgeneClassNames;    
  /** PARAM "baseCgiURLgeneIdNameClass" */
  String
    baseCgiURLgeneIdNameClass;   

  /* --- Names for initial startup database --- */
  /** PARAM "calDNAname" (opt) calibration DNA name */            
  String
    calDNAname;		         
  /** PARAM "yourPlateName" (opt) your plate name */
  String
    yourPlateName;		 
  /** PARAM "emptyWellName" (opt) what you call "EmptyWell" */
  String
    emptyWellName;		 
  /** HP-X set <PARAM NAME=Xlist VALUE=1,3,5> */
  String
    Xlist;                  
  /** HP-Y set <PARAM NAME=Ylist VALUE=2,4,6> */
  String
    Ylist;                  
  /** HP-E list <PARAM NAME=Elist VALUE=1,2,3,4,5,6> */
  String
    Elist;           
  /** HP to use for normalization <PARAM NAME=RatioHP VALUE=4> */
  String
    RatioHP;                
  
  /** PARAM "fontFamily" 'SansSerif' */
  String
    fontFamily= "SansSerif";             
  /** PARAM "fluorescentLbl1" (Cy3) or F1*/
  String
    fluoresLbl1;            
  /** PARAM "fluorescentLbl2" (Cy5) or F2 */
  String
    fluoresLbl2;            
  
  /** PARAM Max # of genes to report in highest or lowest # genes */   
  int
    maxGenesToRpt;         
  /** PARAM specified # of genes */
  int
    nbrGENES;
  /** PARAM specified # of spots */
  int
    nbrSPOTS;
  /** PARAM fields left and right */
  int
    maxFIELDS;
  /** PARAM grids labeled A, B, ..., H */
  int
    maxGRIDS;
  /** PARAM grid rows */
  int
    maxGCOLS;
  /** PARAM grid cols */
  int
    maxGROWS;
   
  /** Threshold PARAM: # of clusters threshold */ 
  int
    nbrOfClustersThr;       
  
  /** Set from .quant file data: Minimum QualCheck Value */ 
  float
    minQualCheck;
  /** Set from .quant file data: Maximum QualCheck Value */ 
  float            
    maxQualCheck; 
                    
  /** Threshold PARAM: "qualThr" continuous QualCheck threshold */
  float  
    qualThr;     
  /** Threshold PARAM: "detValueSpotThr" continuous spot Detection Value threshold */
  float  
    detValueSpotThr;
  
  /** Threshold PARAM: "pctOKthr" % spot intensity OK threshold */
  float  
    pctOKthr;               
  /** Threshold PARAM: diff (HP-X,HP-Y) Filter threshold */
  float  
    diffThr;                
  /** Threshold PARAM: cluster distance Filter threshold */
  float  
    clusterDistThr;         
  /** Threshold PARAM: p-value threshold for t-test Filter */  
  float  
    pValueThr;
  /** Threshold PARAM:  Spot CV threshold 2*|f1-f2|/(f1+f2) for Filter */  
  float  
    spotCVthr;              
    
  /** Scale factor for pseudoarray image. The low-range scale 
   * factor for scaling the low portion of the dynmaic
   * range.
   */  
  float  
    lowRangeScaleFactor;              
    
     
  /** Flag: generate grid coords from nbrGENES */
  static boolean
    genGridsFromNbrGenes;
  
  /** Flag: ignore extra fields > maxFIELDS. This means that you can treat
   * multi-field data as if it only had one field. 
   */
  static boolean
    ignoreExtraFIELDS; 
   
  /** Computed maximum pseudoarray canvas size, null if not using 
   * pseudoarrays as (width,height) required given the array geometry.
   */
  Dimension
    maxCalcPseudoArraySize;    
     

  /**
   * Config() - constructor to make it accessible.
   * @param mae is the MAExplorer instance
   * @param configFile is the full path filename for configuation file
   */
  Config(MAExplorer mae, String configFile)
  { /* Config */
    this.mae= mae;
    this.configFile= configFile;
    gp= mae.gp;
    didGetParms= false;
  } /* Config */
  
  
  /**
   * setDefaultURLs() - set default URLs based on species
   * @param species name (UniGene 2 letter codes)
   * @return true if succeed
   */
  boolean setDefaultURLs(String species)
  { /* setDefaultURLs */
    if(species==null)
      return(false);          /* failed */
    
    dbEstURL= "http://www.ncbi.nlm.nih.gov/irx/cgi-bin/birx_doc?dbest+";
    //genBankAccURL= "http://www.ncbi.nlm.nih.gov/htbin-post/Entrez/query?db=2&form=1&term=";
    genBankAccURL= "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Search&db=Nucleotide&term=";
    
    genBankCloneURL= "http://www.ncbi.nlm.nih.gov/irx/cgi-bin/"+
                     "submit_form_query?TITLE=dbEST+Retrieval+Output&"+
                     "INPUTS=1&BRACKETS=NONE&ADDFLAGS=-b&DB=dbest&NDOCS=10&Q1=";
    genBankCloneURLepilogue= "[clin]";
    
    IMAGE2GenBankURL= "http://nciarray.nci.nih.gov/cgi-bin/UG_query.cgi?"+
                      "ORG="+species+"&ACC=IMAGE:";
    //IMAGE2GidURL= "http://nciarray.nci.nih.gov/cgi-bin/UG_query.cgi?"+
    //	  "ORG="+species+"&GID=IMAGE";  /* [TODO] get correct URL */
    IMAGE2unigeneURL= "http://nciarray.nci.nih.gov/cgi-bin/UG_query.cgi?"+
                       "ORG="+species+"&CLONE=IMAGE:";
    mAdbURL= "http://nciarray.nci.nih.gov/cgi-bin/clone_report.cgi?"+
             "CRITERIA=clone&PARAMETER=IMAGE:";
   
    //uniGeneURL= "http://www.ncbi.nlm.nih.gov/UniGene/query.cgi?ORG="+species+"&TEXT=";
    uniGeneURL= "http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG="+
                species+"&CID=";
       
    //uniGeneClusterIdURL= "http://www.ncbi.nlm.nih.gov/cgi-bin/UniGene/clust?"+
    //                      "ORG="+species+"&CID=";
    
    uniGeneClusterIdURL= "http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=Hs&CID=";
    omimURL= "http://www.ncbi.nlm.nih.gov:80/entrez/dispomim.cgi?id=";
    
    geneCardURL= "http://bioinfo.weizmann.ac.il/cards-bin/carddisp?";
    gbid2LocusLinkURL= "http://www.ncbi.nlm.nih.gov/LocusLink/list.cgi?ORG=&V=0&Q=+";
    locusLinkURL= "http://www.ncbi.nlm.nih.gov/LocusLink/list.cgi?SITE=104&V=1&"+
                  "ORG=Hs&ORG=Mm&ORG=Rn&ORG=Dr&ORG=Dm&Q=";
    swissProtURL= "http://www.expasy.ch/cgi-bin/get-sprot-entry?";
    //GENBANK_BASEURL= "http://www.ncbi.nlm.nih.gov/htbin-post/Entrez/query?db=n&form=6&Dopt=g&uid=",
    pirURL= "http://pir.georgetown.edu/cgi-bin/iproclass/iproclass?choice=entry&id=";
    
    medMinerURL= "http://discover.nci.nih.gov/textmining/cgi-bin/ngg-query.cgi?q=";
    medMinerURLepilogue= "&list=MGAP-1.7Kchip";     /* [TODO] fix this */
    
      /* [FUTURE] use this to get Affy probe set data:
       * (1) download the platform for the Affy Array type in the geo database.
       *     The geoListOfPlatformsURL lists all platforms.
       * (2) In a specific platform, look for "full table view...". This corresponds
       *     to geoFullDataTableURL+"GPL"+i.
       * (3) look for "Data table" and then pick out the data that follows in a HTML format
       *   It will include fields like the following from GPL80:
       *   ID      UNIGENE GB_ACC  TIGR_ID GENE_SYM        MAP     GO_BIO_PROCESS  GO_CELL_COMPONENT
       *   AB000220_at     Hs.171921       AB000220                SEMA3C  7q21-q31        GO:8151  cell growth and maintenance(predicted/computed)  GO:6955  immune response(predicted/computed)  GO:9315  drug resistance(experimental evidence)                                 Anti-pathogen response(predicted/computed)                              IPR003659PSIdomainIPR003006ImmunoglobulinandmajorhistocompatibilitycomplexdomainIPR001627SemaphorinCD100antigenIPR003599Immunoglobulinsubtype
       *   AB000381_s_at   Hs.86161        AB000381                GML     8q24.3  GO:8285   negative control of cell proliferation(experimental  evidence)  GO:6977   DNA damage response, induction  of cell arrest by p53(predicted/computed)  GO:74     cell cycle control(experimental  evidence)  GO:6915   apoptosis(experimental evidence)         GO:5886   plasma membrane(predicted/computed)   GO:15025  GPI-anchored membrane-bound receptor(experimental  evidence)  Integralmembranepredictedcomputed       Cell death/Apoptosis(not recorded)      Unspecified membrane(predicted/computed)                        IPR001526CD59antigen
       */
    geoPlatformID= "";
    geoListOfPlatformsURL= "http://www.ncbi.nlm.nih.gov/geo/query/browse.cgi?view=platforms";
    geoFullDataTableURL= "http://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?targ=&view=full&acc=";
    /* add platform e.g. "GPL80" */
    
    return(true);
  } /* setDefaultURLs */
  
  
  /**
   * getStateParamValues() - get non-file state PARAMs
   * @see GetParams#getNumberedParamList
   * @see GetParams#getParamVal
   * @see GetParams#setDefParam
   * @see MAExplorer#logDRYROTerr
   * @see Util#cvs2f
   * @see Util#cvs2i
   * @see Util#popupDryrotMsgsAndQuit
   * @see #getAdditionalParams
   * @see #setDefaultURLs
   */
  void getStateParamValues()
  { /* getStateParamValues*/
    String sTmp;
    float iTmp;
    
    if(didGetParms)
      return;
    didGetParms= true;    /* set onceonly flag */
    
    /* [1] Parameters which configure the size of the array */
      /* Optional fields [FUTURE - for array data (eg. Incyte, Affymetrix, etc)
       * that does not have (grid,row,col) data and may have missing
       * "Locations"].
       */
    nbrGENES= gp.setDefParam("NBR_CLONES", 0);
    if(nbrGENES==0)
      nbrGENES= gp.setDefParam("NBR_GENES", 0);
    nbrSPOTS= gp.setDefParam("NBR_SPOTS", 0);
    genGridsFromNbrGenes= gp.setDefParam("genGridsFromNbrClones",false);
    if(!genGridsFromNbrGenes)
      genGridsFromNbrGenes= gp.setDefParam("genGridsFromNbrGenes",false);
    
    /* Required fields */
    maxFIELDS= gp.setDefParam("MAX_FIELDS", -1);
    maxGRIDS= gp.setDefParam("MAX_GRIDS", -1);
    maxGCOLS= gp.setDefParam("MAX_GRID_COLS", -1);
    maxGROWS= gp.setDefParam("MAX_GRID_ROWS", -1);
    
    /*
    if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("CFG-GP maxFIELDS="+maxFIELDS+
                        " maxGRIDS="+maxGRIDS+
                        " maxGCOLS="+maxGCOLS+
                        " maxGROWS="+maxGROWS);
     */
    
    /* Test for illegal configurations */
    if(maxFIELDS==-1 || maxGRIDS==-1 || maxGCOLS==-1 || maxGROWS==-1 )
    {
      mae.logDRYROTerr("CFG.1: bad size(s) of #fields("+maxFIELDS+
                       ") or #grids("+maxGRIDS+
                       ") or #rows("+maxGROWS+
                       ") or #cols("+maxGCOLS+") in config file");
    }
    if(maxFIELDS!=1 && maxFIELDS!=2)
    {
      mae.logDRYROTerr("CFG.1: illegal maxFIELDS("+
                       maxFIELDS+"), must be 1 or 2, in config file.");
      Util.popupDryrotMsgsAndQuit();
    }
    
    mae.spotRad= gp.setDefParam("SpotRadius",
                                DEF_RADIUS, MIN_RADIUS, MAX_RADIUS);
    
    /* Recompute  (canvasHSize,canvasVSize) based on
     * (maxFIELDS, maxGRIDS, maxGROWS, maxGCOLS) and spotRad.
     * The objective is to make the spot spacing look good and
     * fit entirely on the DrawPseudoImage image that will
     * be drawn in the ScrollableImageCanvas.
     */
    
    /* Get size of the pseudo array canvas */
    mae.canvasHSize= gp.setDefParam("CanvasHorSize",DEF_CANVAS_HSIZE);
    mae.canvasVSize= gp.setDefParam("CanvasVertSize",DEF_CANVAS_VSIZE);
    if(mae.canvasHSize<=0 || mae.canvasVSize<=0)
    {
      mae.logDRYROTerr("CFG.1: bad pseudoarray canvas (HxV) size("+
                       mae.canvasHSize+" X "+mae.canvasVSize+
                       ") in config file");
    }
    
    /* Compute the canvasSize based on (F,G,R,C) */
    mae.spotRad= DEF_RADIUS;   /* Hardwire the spot radius */
    //int spaceBetweenSpots= MaHPquantTable.SPACING_BETWEEN_SPOTS,
    //spotRegionX= mae.spotRad+spaceBetweenSpots,
    //spotRegionY= mae.spotRad+spaceBetweenSpots;
    int
      totSpots= maxFIELDS*maxGRIDS*maxGCOLS*maxGROWS,
      spotRegionX= MaHPquantTable.DELTA_X_SPACING,
      spotRegionY= MaHPquantTable.DELTA_Y_SPACING,
      guardRegion= MaHPquantTable.GUARD_REGION,
      guardLeft= MaHPquantTable.X_SPOTQ_OFFSET,
      guardTop=  MaHPquantTable.X_SPOTQ_OFFSET,
      gridHeight= (int)(maxGROWS*spotRegionY+1.6*guardRegion),
      gridWidth= (int)(maxGCOLS*spotRegionX+1.5*guardRegion),
      vSize= guardTop+(maxGRIDS*gridHeight)+4*guardRegion,
      hSize= guardLeft+(maxFIELDS*gridWidth);
    if(totSpots>7000)
      vSize += 2*guardRegion;
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("CFG hSize="+hSize+" vSize="+vSize+
                      "\n maxFIELDS="+maxFIELDS+
                      " maxGRIDS="+maxGRIDS+
                      " maxGCOLS="+maxGCOLS+
                      " maxGROWS="+maxGROWS+
                      "\n spaceBetweenSpots="+spaceBetweenSpots+
                      "\n mae.spotRad="+mae.spotRad+
                      "\n spotRegionX="+spotRegionX+
                      "\n spotRegionY="+spotRegionY+
                      "\n guardRegion="+guardRegion+
                      "\n guardLeft="+guardLeft+
                      "\n guardTop="+guardTop+
                      "\n gridHeight="+gridHeight+
                      "\n gridWidth="+gridWidth
                      );
        */
    
      /* Use fixed spot pseudoarray image spacing with dynamic canvasH[V]Size
       * else scale to fixed size canvas.
       */
    mae.canvasHSize= Math.max(hSize,mae.PSEUDOIMG_WIDTH);
    mae.canvasVSize= vSize;
    
    /* [2] Optional parameters which configure the threshold sliders */
    /* Get default MAX_INTENSITY if want a different dynamic range. */
    mae.MAX_INTENSITY= gp.setDefParam("MAX_INTENSITY",DEF_MAX_INTENSITY);
    
    /* Get spot intensity range */
    mae.sit1= gp.setDefParam("SI1", 0, 0, (float)mae.MAX_INTENSITY);
    mae.sit2= gp.setDefParam("SI2", (float)mae.MAX_INTENSITY,
                             mae.sit1, (float)mae.MAX_INTENSITY);
    
      /* Get the [t1:t2] range.
       * Note: could have [I1:I2] range that is < 0.0 if Zscore.
       * Overide with I1, I2 intensity sliders if they exist.
       * Check for G1:G2 for historical depricated reasons.
       */
    sTmp= gp.getParamVal("I1");
    if(sTmp==null)
      sTmp= gp.getParamVal("G1");
    mae.t1= (sTmp!=null) ? Util.cvs2f(sTmp) : 0.0F;
    
    sTmp= gp.getParamVal("I2");
    if(sTmp==null)
      sTmp= gp.getParamVal("G2");
    mae.t2= (sTmp!=null) ? Util.cvs2f(sTmp) : (float)mae.MAX_INTENSITY;
    if(mae.t2<mae.t1)
      mae.t1= mae.t2;      /* t1 can never be larger than t2 */
    
    mae.r1= gp.setDefParam("R1", mae.MIN_RATIO, mae.MIN_RATIO, mae.MAX_RATIO);
    mae.r2= gp.setDefParam("R2", mae.MAX_RATIO, mae.r1, mae.MAX_RATIO);
    
    mae.cr1= gp.setDefParam("CR1", mae.MIN_RATIO, mae.MIN_RATIO, mae.MAX_RATIO);
    mae.cr2= gp.setDefParam("CR2", mae.MAX_RATIO, mae.MIN_RATIO, mae.MAX_RATIO);
    
    diffThr= gp.setDefParam("diffThr", DEF_DIFF_THR, 0.0F, 4.0F);
    /* default set for Zscore 4 StdDev's */
    clusterDistThr= gp.setDefParam("clusterDistThr", DEF_CLUSTER_DIST,
                                   MIN_CLUSTER_DIST,MAX_CLUSTER_DIST);
    nbrOfClustersThr= gp.setDefParam("nbrOfClustersThr",
                                     DEF_NBR_CLUSTERS, MIN_NBR_CLUSTERS,
                                     MAX_NBR_CLUSTERS);
    pValueThr= gp.setDefParam("pValueThr", DEF_P_VALUE_THR,0.0F,1.0F);
    spotCVthr= gp.setDefParam("spotCVthr", DEF_SPOT_CV_THR,0.0F,1.0F);
    pctOKthr= gp.setDefParam("pctOKthr", 50.0F,0.0F,100.0F);
    qualThr= gp.setDefParam("qualThr",100, 0,100);
    detValueSpotThr= gp.setDefParam("detValueSpotThr",1.0F, 0.0F,1.0F);
    
    mae.lowRangeZoomFlag= gp.setDefParam("lowRangeZoomFlag", mae.lowRangeZoomFlag);
    lowRangeScaleFactor= (mae.lowRangeZoomFlag) ? 0.01F : 1.0F;
    
    if(mae.MAX_INTENSITY==0 ||
      mae.spotRad<=0 ||
      clusterDistThr<=0.0F ||
      nbrOfClustersThr<=0 ||
      pValueThr<=0.0F || pValueThr>1.0F ||
      spotCVthr==0.0F || spotCVthr>1.0F ||
      diffThr<0.0F || diffThr>4.0F)
    {
      mae.logDRYROTerr("CFG.2: bad State thresholds in config file");
    }
    
    /* [3] Parameters which define URLs */
    unigeneSpecies= gp.getParamVal("unigeneSpecies");   /* Hs, Mm, etc */
    if(unigeneSpecies==null)
      unigeneSpecies= "Mm";                             /* THIS IS NOT A GOOD THING!!! */
    setDefaultURLs(unigeneSpecies);                     /* make the defaults non-null */
    
    mae.proxyServer= gp.getParamVal("proxyServer");       /* only for applet */
    writeFileCGIurl= gp.getParamVal("writeFileCGIurl");   /* [DEPRICATE] */
    
    /* Handle both spellings for GenBank (i.e. GeneBank) */
    sTmp= gp.getParamVal("genBankCloneURL");
    if(sTmp==null)
      sTmp= gp.getParamVal("geneBankCloneURL");
    if(sTmp!=null)
      genBankCloneURL= sTmp;
    
    sTmp= gp.getParamVal("genBankCloneURLepilogue");
    if(sTmp==null)
      sTmp= gp.getParamVal("geneBankCloneURLepilogue");
    if(sTmp!=null)
      genBankCloneURLepilogue= sTmp;
    
    dbEstURL= gp.setDefParam("dbEstURL",dbEstURL);
    mAdbURL= gp.setDefParam("mAdbURL",mAdbURL);
    gbid2LocusLinkURL= gp.setDefParam("gbid2LocusLinkURL",
                                      gbid2LocusLinkURL);
    
    uniGeneURL= gp.setDefParam("uniGeneURL",uniGeneURL);
    uniGeneClusterIdURL= gp.setDefParam("uniGeneClusterIdURL",
                                        uniGeneClusterIdURL);
    
    omimURL= gp.setDefParam("omimURL", omimURL);
    
    locusLinkURL= gp.setDefParam("locusLinkURL", locusLinkURL);
    jaxURL= gp.setDefParam("setDefParam", jaxURL);
    
    sTmp= gp.getParamVal("genBankAccURL");
    if(sTmp==null)
      sTmp= gp.getParamVal("geneBankAccURL");
    if(sTmp!=null)
      genBankAccURL= sTmp;
    
    IMAGE2unigeneURL= gp.setDefParam("IMAGE2unigeneURL",
                                     IMAGE2unigeneURL);
    
    /* Handle multiple spellings - if neither use default */
    sTmp= gp.getParamVal("IMAGE2GenBankURL");
    if(sTmp!=null)
      IMAGE2GenBankURL= sTmp;
    else if(IMAGE2GenBankURL!=null)
    {
      sTmp= gp.getParamVal("IMAGE2GeneBankURL");
      if(sTmp!=null)
        IMAGE2GenBankURL= sTmp;
    }
    
    medMinerURL= gp.setDefParam("medMinerURL", medMinerURL);
    medMinerURLepilogue= gp.setDefParam("medMinerURLepilogue",
                                        medMinerURLepilogue);
    
    /* Fill it in if not found */
    gbid2LocusLinkURL= gp.setDefParam("gbid2LocusLinkURL",
                                      gbid2LocusLinkURL);
    locusLinkURL= gp.setDefParam("locusLinkURL", locusLinkURL);    
    
    geoListOfPlatformsURL= gp.setDefParam("geoListOfPlatformsURL", geoListOfPlatformsURL);
    geoFullDataTableURL= gp.setDefParam("geoFullDataTableURL", geoFullDataTableURL);
    geoPlatformID= gp.setDefParam("geoPlatformID", geoPlatformID);
    
    if(mae.DBUG_MEDMINER_FLAG)
    {
      medMinerURL= "http://discover.nci.nih.gov/textmining/cgi-bin/ngg-query.cgi?q=";
      medMinerURLepilogue= "&list=MGAP-1.7Kchip";
      //medMinerURLepilogue= "F:\jws\MAExplorer\Txt\MGAP-1.7Kchip"
      //medMinerURLepilogue= "&list=human_oncochip";
      //http://discover.nci.nih.gov/textmining/cgi-bin/ngg-query.cgi?q=108837&list=human_oncochip
    }
    
    swissProtURL= gp.setDefParam("swissProtURL", swissProtURL);
    pirURL= gp.setDefParam("pirURL", pirURL);
    
    geneCardURL= gp.setDefParam("GeneCardURL", geneCardURL);
    histologyURL= gp.getParamVal("histologyURL");
    modelsURL= gp.getParamVal("modelsURL");
    
      /* Get paired (HelpMenu1,HelpURL1), (HelpMenu2,HelpURL2),  ...
       * lists of entries if not null.
       */
    sHelpMenu= mae.gp.getNumberedParamList("HelpMenu");
    sHelpURL= mae.gp.getNumberedParamList("HelpURL");
    nHelpMenus= (sHelpMenu==null) ? 0 : sHelpMenu.length;
    int
    nHelpURL= (sHelpURL==null) ? 0 : sHelpURL.length;
    if(nHelpMenus!=nHelpURL)
    { /* DRYROT - Menu not synced with URL entries */
      mae.logDRYROTerr("CFG.2.1: HelpMenu[] not synced with HelpURL[] entries");
    }
    
    /* Get triples
     *   (GenomicMenu1, GenomicURL1, GenomicURLepilogue1, GenomicIDreq1),
     *   (GenomicMenu2, GenomicURL2, GenomicURLepilogue2, GenomicIDreq2), ...
     * lists of entries if not null.
     */
    if(mae.DBUG_GENOMIC_URLS_FLAG)
    { /* get Genomic data from config file */
      sGenomicMenu= mae.gp.getNumberedParamList("GenomicMenu");
      sGenomicURL= mae.gp.getNumberedParamList("GenomicURL");
      sGenomicURLepilogue= mae.gp.getNumberedParamList("GenomicURLepilogue");
      sGenomicIDreq= mae.gp.getNumberedParamList("GenomicIDreq");
      nGenomicMenus= (sGenomicMenu==null) ? 0 : sGenomicMenu.length;
      int
        nGenomicURL= (sGenomicURL==null) ? 0 : sGenomicURL.length,
        nGenomicURLepilogue= (sGenomicURLepilogue==null) ? 0 : sGenomicURLepilogue.length,
        nGenomicIDreq= (sGenomicIDreq==null) ? 0 : sGenomicIDreq.length;
      if(nGenomicMenus!=nGenomicURL ||
         nGenomicMenus!=nGenomicURLepilogue ||
         nGenomicMenus!=nGenomicIDreq)
      { /* DRYROT - Menu not synced with URL entries */
        mae.logDRYROTerr("CFG.2.2: (sGenomicMenu[], sGenomicURL[], sGenomicIDreq[],"+
                         "\n sGenomicURLepilogue[]) entries not synced");
      } /* DRYROT - Menu not synced with URL entries */
    } /* get Genomic data from config file */
    else
    { /* Overide this data if not allowed to use it */
      nGenomicMenus= 0;
    }
    
    /* Get paired ("PluginMenuName1","PluginMenuStubName1","sPluginClassFile1"),
     * ("PluginMenuName2","PluginMenuStubName2","sPluginClassFile2"),  ...
     * lists of entries if not null. This is used for installing preloaded
     * plugins.
     */
    sPluginMenuName= gp.getNumberedParamList("PluginMenuName");
    sPluginMenuStubName= gp.getNumberedParamList("PluginMenuStubName");
    sPluginClassFile= gp.getNumberedParamList("PluginClassFile");
    sPluginCallAtStartup= gp.getNumberedParamList("PluginCallAtStartup");
    nPluginMenus= (sPluginMenuName==null) ? 0 : sPluginMenuName.length;
    int
      nPluginMenuStubName= (sPluginMenuStubName==null) ? 0 : sPluginMenuStubName.length,
      nPluginClassFile= (sPluginClassFile==null) ? 0 : sPluginClassFile.length,
      nPluginCallAtStartup= (sPluginCallAtStartup==null) ? 0 : sPluginCallAtStartup.length;
    if(nPluginMenus!=nPluginMenuStubName ||
       nPluginMenus!=nPluginClassFile ||
       nPluginMenus!=nPluginCallAtStartup	)
    { /* DRYROT - Menu entires not synced with URL entries */
      mae.logDRYROTerr("CFG.2.3: entries (PluginMenu[], PluginMenuStubName[], PluginClassFile[],"+
                       "\nPluginCallAtStartup[]) are not all present or the same size");
    }
    
   /* [TODO] add dynamic list of URLs that feed off of either
    * Clone ID, Genbank ID, other ID. Specify name field as:
    *    URL.CloneID.<propName>.<menuName>
    * Then could add to various menus, etc. based on <propName>.
    * [TODO] fix logic so use Master_ID
    */
    
    /* Don't test for jaxURL */
    boolean  appletProxyServerFlag= (mae.proxyServer==null);
    
    if(mae.isAppletFlag && appletProxyServerFlag)
    {
      mae.logDRYROTerr("CFG.3: null proxy server for applet in config file");
    }
    
   /* [3.1] [NOTE] If a standalone application, then we need to
    * possibly modify mae.saCodeBase so it points to a Web server
    * (eg. "http://www.lecb.ncifcrf.gov/") rather than
    * "file://".  Because of the "chicken and the egg" problem
    * this is handled in us.readMAEstartupFile() which
    * uses the "saCodeBase" parameter rather than here.
    */
    
    /* [4] Get parameters of names of things */
    database= gp.setDefParam( "dataBase","");
    dbSubset= gp.setDefParam( "dbSubset","");
    maAnalysisProg= gp.setDefParam( "maAnalysisProgram","");    
    
   /* Get Unique Database ID ("DIDxxxxxx") - used as prefix for State/ files.
    * If it does not exist, then create it when save the database.
    */  
    mae.dbID= gp.setDefParam( "dbID","");
    
    mae.userName= gp.setDefParam("userName","");
    mae.classNameX= gp.setDefParam("classNameX","HP-X 'set'");
    mae.classNameY= gp.setDefParam("classNameY","HP-Y 'set'");
    calDNAname= gp.setDefParam("calibDNAname","");
    yourPlateName= gp.setDefParam("yourPlateName","");
    emptyWellName= gp.setDefParam("emptyWellName","EmptyWell");
    
    /* [5] Get scalar and boolean parameters values */
    mae.useCy5OverCy3Flag= gp.setDefParam("useCy5/Cy3", false);
    mae.useMouseOverFlag= gp.setDefParam("useMouseOver",true);
    
   /* The following is a hack for the original ResGen MGAP data only!
    * We normally do not want to swap Rows and Columns.
    */
    boolean specialHackFlag= (database.equals("MGAP DB") &&
                              maAnalysisProg.equals("Pathways 2.01"));
    mae.swapRowsColsFlag= gp.setDefParam("swapRowsColumns", specialHackFlag);
    
    mae.maxPreloadImgs= gp.setDefParam("maxPreloadImages",0);
    mae.useRatioDataFlag= gp.setDefParam("useRatioData",false);
    mae.bkgdCorrectFlag= gp.setDefParam("useBackgroundCorrection", 
                                        mae.bkgdCorrectFlag);
    mae.ratioMedianCorrectionFlag= gp.setDefParam("useRatioMedianCorrection",
                                                  mae.useRatioDataFlag);
    mae.allowNegQuantDataFlag= gp.setDefParam("allowNegQuantDataFlag", false);
    mae.useDichromasyFlag= gp.setDefParam("useDichromasy",false);
    mae.usePseudoXYcoordsFlag= gp.setDefParam("usePseudoXYcoords",false);
    mae.reuseXYcoordsFlag= gp.setDefParam("reuseXYcoords",
                                          mae.usePseudoXYcoordsFlag);
    maxGenesToRpt= gp.setDefParam("maxClonesReported",0);
    if(maxGenesToRpt==0)
      maxGenesToRpt= gp.setDefParam("maxGenesReported",50);
    mae.presentViewFlag= gp.setDefParam("presentationViewFlag",false);
    mae.useSPLUSasComputingEngineFlag=
                  gp.setDefParam("useSPLUSasComputingEngineFlag",false);
    mae.useRLOloggingFlag= gp.setDefParam("useRLOloggingFlag",false);
    
    /* [6] Get optional PARAMs */
    fontFamily= gp.setDefParam("fontFamily","SansSerif");
    if(!mae.useRatioDataFlag)
    {
      fluoresLbl1= gp.setDefParam("fluorescentLbl1","F1");
      fluoresLbl2= gp.setDefParam("fluorescentLbl2","F2");
    }
    else
    {
      fluoresLbl1= gp.setDefParam("fluorescentLbl1","Cy3");
      fluoresLbl2= gp.setDefParam("fluorescentLbl2","Cy5");
    }
    ignoreExtraFIELDS= gp.setDefParam("ignoreExtraFields",true);
    Xlist= gp.setDefParam("Xlist","");
    Ylist= gp.setDefParam("Ylist","");
    Elist= gp.setDefParam("Elist","");
    if(mae.maxPreloadImgs>0)
    { /* check and set default lists */
      if(Xlist==null)
        Xlist= "1";
      if(Ylist==null)
        Ylist= (mae.maxPreloadImgs>1) ? "2" : "1";
        if(Elist==null)
        { /* pick all samples */
          Elist= "1";
          for(int i=2;i<mae.maxPreloadImgs;i++)
            Elist += (","+i);
        } /* pick all samples */
    } /* check and set default lists */  
    
    RatioHP= gp.setDefParam("RatioHP","");
    
    /* Read the current Condition and current OCL */
    Condition.curCondIdx= gp.setDefParam("curCondIdx", -1); 
    Condition.curOCLidx= gp.setDefParam("curOCLidx", -1); 
    
    /* [7] Get additional parameters from .mae file etc.
     * put there by (File | SaveAsDB).
     */
    getAdditionalParams();
    
    /* [8] Compute the maximum pseudoarray canvas size, null if not using
     * pseudoarrays as (width,height) required given the array geometry.
     */
    maxCalcPseudoArraySize= computeMaxCanvasSize();
    
    /* [8.1] Replace specified canvas size with required canvas size! */
    mae.canvasHSize= Math.max(mae.canvasHSize,maxCalcPseudoArraySize.width);
    mae.canvasVSize= Math.max(mae.canvasVSize,maxCalcPseudoArraySize.height);
    
    Util.popupDryrotMsgsAndQuit();  /* get out if any DRYROT trouble */
  } /* getStateParamValues */
  
  
  /**
   * getAdditionalParams() - get additional params from .mae file or Applet etc.
   * put there by (File | SaveAsDB).
   * @see GetParams#setDefParam
   */
  void getAdditionalParams()
  { /* getAdditionalParams */
    /* [1] Get additional View state flags put there by (File | SaveAsDB) */
    mae.showEGLflag= gp.setDefParam("showEGLflag",mae.showEGLflag);
    mae.genBankViewerFlag= gp.setDefParam("genBankViewerFlag", mae.genBankViewerFlag);
    mae.dbESTviewerFlag= gp.setDefParam("dbESTviewerFlag", mae.dbESTviewerFlag);
    mae.uniGeneViewerFlag= gp.setDefParam("uniGeneViewerFlag", mae.uniGeneViewerFlag);
    mae.omimViewerFlag= gp.setDefParam("omimViewerFlag", mae.omimViewerFlag);
    mae.mAdbViewerFlag= gp.setDefParam("mAdbViewerFlag", mae.mAdbViewerFlag);
    mae.locusLinkViewerFlag= gp.setDefParam("locusLinkViewerFlag", mae.locusLinkViewerFlag);
    mae.medMinerViewerFlag= gp.setDefParam("medMinerViewerFlag", mae.medMinerViewerFlag);
    mae.swissProtViewerFlag= gp.setDefParam("swissProtViewerFlag", mae.swissProtViewerFlag);
    mae.pirViewerFlag= gp.setDefParam("pirViewerFlag",mae.pirViewerFlag);
    mae.viewFilteredSpotsFlag= gp.setDefParam("viewFilteredSpotsFlag", mae.viewFilteredSpotsFlag);
    mae.gangSpotFlag= gp.setDefParam("gangSpotFlag",mae.gangSpotFlag);
    mae.flickerXYflag= gp.setDefParam("flickerXYflag",mae.flickerXYflag);
    
    /* [2] Get active Filters substate put there by (File | SaveAsDB) */
    GeneClass.curGeneClassName= gp.setDefParam("curGeneClassName", (String)null);
    
    mae.geneClassMbrFilterFlag= gp.setDefParam("geneClassMbrFilterFlag", mae.geneClassMbrFilterFlag);
    mae.geneClassMbrFilterFlag= gp.setDefParam("geneClassMbrFilterFlag", mae.geneClassMbrFilterFlag);
    mae.useGeneSetFilterFlag= gp.setDefParam("useGeneSetFilterFlag", mae.useGeneSetFilterFlag);
    mae.useEditedCLflag= gp.setDefParam("useEditedCLflag", mae.useEditedCLflag);
    mae.useGoodGeneCLflag= gp.setDefParam("useGoodGeneCLflag", mae.useGoodGeneCLflag);
    mae.useReplicateGenesFlag= gp.setDefParam("mae.useReplicateGeneFlag", mae.useReplicateGenesFlag);
    mae.useRatioHistCLflag= gp.setDefParam("useRatioHistCLflag", mae.useRatioHistCLflag);
    mae.useIntensHistCLflag= gp.setDefParam("useIntensHistCLflag", mae.useIntensHistCLflag);
    
    mae.spotIntensFilterFlag= gp.setDefParam("spotIntensFilterFlag", mae.spotIntensFilterFlag);
    mae.spotIntensRangeMode= gp.setDefParam("spotIntensRangeMode", mae.spotIntensRangeMode);
    mae.spotIntensTestMode= gp.setDefParam("spotIntensTestMode", mae.spotIntensTestMode);
    mae.spotIntensCompareMode= gp.setDefParam("spotIntensCompareMode", mae.spotIntensCompareMode);
    
    mae.intensFilterFlag= gp.setDefParam("intensFilterFlag", mae.intensFilterFlag);
    mae.sampleIntensityRangeMode= gp.setDefParam("sampleIntensityRangeMode", mae.sampleIntensityRangeMode);
    
    mae.ratioFilterFlag= gp.setDefParam("ratioFilterFlag", mae.ratioFilterFlag);
    mae.ratioRangeMode= gp.setDefParam("ratioRangeMode", mae.ratioRangeMode);
    
    mae.ratioCy3Cy5FilterFlag= gp.setDefParam("ratioCy3Cy5FilterFlag", mae.ratioCy3Cy5FilterFlag);
    mae.ratioCy3Cy5RangeMode= gp.setDefParam("ratioCy3Cy5RangeMode", mae.ratioCy3Cy5RangeMode);
    
    mae.useSpotCVfilterFlag= gp.setDefParam("useSpotCVfilterFlag", mae.useSpotCVfilterFlag);
    mae.cvTestMode= gp.setDefParam("cvTestMode", mae.cvTestMode);
    
    mae.usePosQuantDataFlag= gp.setDefParam("usePosQuantDataFlag", mae.usePosQuantDataFlag);
    mae.posQuantTestMode= gp.setDefParam("posQuantTestMode", mae.posQuantTestMode);
    
    mae.useGoodSpotDataFlag= gp.setDefParam("useGoodSpotDataFlag", mae.useGoodSpotDataFlag);
    mae.goodSpotTestMode= gp.setDefParam("goodSpotTestMode", mae.goodSpotTestMode);
    
    mae.useDetValueSpotDataFlag= gp.setDefParam("useDetValueSpotDataFlag", mae.useDetValueSpotDataFlag);
    mae.useOnlyGenesWithNonZeroDensityFlag= gp.setDefParam("useOnlyGenesWithNonZeroDensityFlag", 
                                                           mae.useOnlyGenesWithNonZeroDensityFlag);
    mae.detValueSpotTestMode= gp.setDefParam("detValueSpotTestMode", mae.detValueSpotTestMode);
    
    mae.qualTypeMode= gp.setDefParam("qualTypeMode", mae.qualTypeMode);
    mae.useGoodSpotsForGlobalStatsFlag= gp.setDefParam("useGoodSpotsForGlobalStatsFlag",
    mae.useGoodSpotsForGlobalStatsFlag);
    
    mae.useCVmeanElseMaxFlag= gp.setDefParam("useCVmeanElseMaxFlag", mae.useCVmeanElseMaxFlag);
    mae.tTestXYfilterFlag= gp.setDefParam("tTestXYfilterFlag", mae.tTestXYfilterFlag);
    mae.tTestXYsetsFilterFlag= gp.setDefParam("tTestXYsetsFilterFlag", mae.tTestXYsetsFilterFlag);
    mae.KS_TestXYsetsFilterFlag= gp.setDefParam("KS_TestXYsetsFilterFlag", mae.KS_TestXYsetsFilterFlag);
    mae.F_TestOCLFilterFlag= gp.setDefParam("F_TestOCLFilterFlag", mae.F_TestOCLFilterFlag);
    mae.clusterHP_EfilterFlag= gp.setDefParam("clusterHP_EfilterFlag", mae.clusterHP_EfilterFlag);
    mae.useDiffFilterFlag= gp.setDefParam("useDiffFilterFlag", mae.useDiffFilterFlag);
    mae.useHighRatiosFilterFlag= gp.setDefParam("useHighRatiosFilterFlag", mae.useHighRatiosFilterFlag);
    mae.useLowRatiosFilterFlag= gp.setDefParam("useLowRatiosFilterFlag", mae.useLowRatiosFilterFlag);
    
    /* [3] Get additional Plot state flags put there by (File | SaveAsDB) */
    mae.plotImageMode= gp.setDefParam("plotModeImage", -1);       /* DEPRICATED name */
    if(mae.plotImageMode==-1)
      mae.plotImageMode= gp.setDefParam("plotImageMode", mae.plotImageMode);
    
    mae.dualXYpseudoPlotFlag= gp.setDefParam("dualXYpseudoPlotFlag", mae.dualXYpseudoPlotFlag);
    mae.useEPoverlayFlag= gp.setDefParam("useEPoverlayFlag", mae.useEPoverlayFlag);
    mae.hierClustMode= gp.setDefParam("hierClustMode", mae.hierClustMode);
    mae.hierClustUnWtAvgFlag= gp.setDefParam("hierClustUnWtAvgFlag", mae.hierClustUnWtAvgFlag);
    mae.useHierClusterDispFlag= gp.setDefParam("useHierClusterDispFlag", mae.useHierClusterDispFlag);
    mae.useCorrCoeffFlag= gp.setDefParam("useCorrCoeffFlag", mae.useCorrCoeffFlag);
    mae.useClusterDistFlag= gp.setDefParam("useClusterDistFlag", mae.useClusterDistFlag);
    mae.useSimGeneClusterDispFlag= gp.setDefParam("useSimGeneClusterDispFlag", mae.useSimGeneClusterDispFlag);
    mae.useClusterCountsDispFlag= gp.setDefParam("useClusterCountsDispFlag", mae.useClusterCountsDispFlag);
    mae.useKmeansClusterCntsDispFlag= gp.setDefParam("useKmeansClusterCntsDispFlag", mae.useKmeansClusterCntsDispFlag);
    mae.useMedianForKmeansClusteringFlag= gp.setDefParam("useMedianForKmeansClusteringFlag", mae.useMedianForKmeansClusteringFlag);
    mae.useLSQmagNormFlag= gp.setDefParam("useLSQmagNormFlag", mae.useLSQmagNormFlag);
    mae.normHCbyRatioHPflag= gp.setDefParam("normHCbyRatioHPflag", mae.normHCbyRatioHPflag);
    mae.useClusterDistCacheFlag= gp.setDefParam("useClusterDistCacheFlag", mae.useClusterDistCacheFlag);
    mae.useShortClusterDistCacheFlag= gp.setDefParam("useShortClusterDistCacheFlag", mae.useShortClusterDistCacheFlag);
    
    /* [4] Get additional Table state flags put there by (File | SaveAsDB) */
    mae.tblFmtMode= gp.setDefParam("tblFmtMode", mae.tblFmtMode);
    mae.rptFontSize= gp.setDefParam("rptFontSize", mae.rptFontSize);
    mae.addExprProfileFlag= gp.setDefParam("addExprProfileFlag", mae.addExprProfileFlag);
    mae.useEPrawIntensValFlag= gp.setDefParam("useEPrawIntensValFlag", mae.useEPrawIntensValFlag);
    mae.addHP_XYstatFlag= gp.setDefParam("addHP_XYstatFlag", mae.addHP_XYstatFlag);
    mae.addOCLstatFlag= gp.setDefParam("addOCLstatFlag",mae.addOCLstatFlag);
    
    /* [5] Get additional Preferences state flags put there by (File | SaveAsDB) */
    mae.autoStateScrPopupFlag= gp.setDefParam("autoStateScrPopupFlag", mae.autoStateScrPopupFlag);
    mae.clusterOnFilteredCLflag= gp.setDefParam("clusterOnFilteredCLflag", mae.clusterOnFilteredCLflag);
    
    /* [6] Get additional Edit state flags put there by (File | SaveAsDB) */
    mae.editMode= gp.setDefParam("editMode",mae.editMode);
    GeneBitSet.nUserBS= gp.setDefParam("nUserBS",GeneBitSet.nUserBS);
    
    /* [7] Get additional Normalization state flags put there by (File | SaveAsDB) */
    mae.normName= gp.setDefParam("normName",mae.normName);
    mae.isZscoreFlag= gp.setDefParam("isZscoreFlag",mae.isZscoreFlag);
    mae.normByZscoreFlag= gp.setDefParam("normByZscoreFlag", mae.normByZscoreFlag);
    mae.normByMedianFlag= gp.setDefParam("normByMedianFlag", mae.normByMedianFlag);
    mae.normByLogMedianFlag= gp.setDefParam("normByLogMedianFlag", mae.normByLogMedianFlag);
    mae.normByZscoreMeanStdDevLogFlag= gp.setDefParam("normByZscoreMeanStdDevLogFlag",
                                                      mae.normByZscoreMeanStdDevLogFlag);
    mae.normByZscoreMeanAbsDevLogFlag= gp.setDefParam("normByZscoreMeanAbsDevLogFlag",
                                                      mae.normByZscoreMeanAbsDevLogFlag);
    mae.normByCalDNAflag= gp.setDefParam("normByCalDNAflag", mae.normByCalDNAflag);
    mae.normByGeneSetFlag= gp.setDefParam("normByGeneSetFlag", mae.normByGeneSetFlag);
    mae.normByHousekeepGenesFlag= gp.setDefParam("normByHousekeepGenesFlag",
                                                 mae.normByHousekeepGenesFlag);
    mae.scaleDataToMaxIntensFlag= gp.setDefParam("scaleDataToMaxIntensFlag",
                                                 mae.scaleDataToMaxIntensFlag);
    
   /* [8] Get additional Samples state flags put there by (File | SaveAsDB).
    * Note: "useHPxySetDataFlag" has [DEPRICATION-COMPATIBILITY PROBLEM]
    */
    mae.useHPxySetDataFlag= gp.setDefParam("useHPxySetDataFlag", 
                                           mae.useHPxySetDataFlag);
    mae.useMeanHPeListDataFlag= gp.setDefParam("useMeanHPeListDataFlag", 
                                               mae.useMeanHPeListDataFlag);
  } /* getAdditionalParams */
  
  
  /**
   * readPARAMvalues() - read applet PARAM values overiding configuration file.
   * @param path of configuration file
   * @see GetParams#getParamVal
   * @see MAExplorer#logDRYROTerr
   * @see #getStateParamValues
   */
  void readPARAMvalues(String path)
  { /* readPARAMvalues */
    GetParams gp= mae.gp;
    
    /* [1] Get non-file parameters */
    getStateParamValues();
    
    /* [2] get files */
    sampDBfile= gp.getParamVal("membranesDBfile");  /* DEPRICATED */
    if(sampDBfile==null)
      sampDBfile= gp.getParamVal("samplesDBfile");
    maInfoFile= gp.getParamVal("maInfoFile");
    gipoFile= gp.getParamVal("gipoFile");
    
    quantFileExt= gp.setDefParam("quantFileExt", ".quant");
    
    /* add base URL address for CGI */
    baseCgiURLquant= gp.getParamVal("baseCgiURLquant");
    baseCgiURLsamples= gp.getParamVal("baseCgiURLsamples");
    baseCgiURLgipo= gp.getParamVal( "baseCgiURLgipo");
    baseCgiURLgeneClassNames= gp.getParamVal("baseCgiURLgeneIdNameClass");
    baseCgiURLgeneIdNameClass= gp.getParamVal("baseCgiURLgeneIdNameClass");
    
    if(sampDBfile==null || gipoFile==null)
      mae.logDRYROTerr("CFG: bad file name(s)");
    
    /* [3] Add prefix with the path */
    sampDBfile= path + sampDBfile;
    gipoFile= path + gipoFile;
    
    /* optional files are null if don't exist */
    maInfoFile= (maInfoFile==null) ? null : (path + maInfoFile);
  } /* readPARAMvalues */
  
  
  /**
   * setupPathBase() - set mae.(fileBasePath, isWorkingMAE) from codeBase.
   * @param mae is the MAExplorer instance
   */
  static void setupPathBase(MAExplorer mae)
  { /* setupPathBase */
    String osName= mae.osName;
    
    if(!MAExplorer.codeBase.startsWith("http://"))
    { /* setup local files */
      /* Get rid of "file:" prefix (Solaris) or (SunOS)
       * "file:/" prefix for Windows,
       * "file:/" prefix for MacOS
       */
      mae.isWinPCflag= false;
      
      if(osName.equals("Solaris") || osName.equals("SunOS")||
         osName.equals("Linux") || osName.equals("MacOS-X"))
      { /* UNIX */
        if(!mae.isAppletFlag)     /* "file:" prefix */
          mae.fileBasePath= mae.codeBase.substring(6);
        else                      /* "file:" prefix */
          mae.fileBasePath= mae.codeBase.substring(5);
      } /* UNIX */
      
      else if(osName.startsWith("Windows"))
      { /* special handling for Windows - UGH!!! Fix Bill'isms */
        /* should handle Windows 95/98/NT/2000/XP */
        mae.isWinPCflag= true;
        mae.fileBasePath= mae.codeBase.substring(6); /* "file:/" */
        int len= mae.fileBasePath.length();
        StringBuffer fbO= new StringBuffer(len);
        
        for(int i= 0; i<=(len-1); i++)
        { /* convert "C|/..." to "C:/..." */
          char
            chI= mae.fileBasePath.charAt(i),
            chO= ((chI=='|') ? ':' :chI);
          fbO.append(chO);
        } /* convert "C|/..." to "C:/..." */
        mae.fileBasePath= new String(fbO);
      } /* special handling for Windows - UGH!!! Fix Bill'isms */
      
      else if(osName.equals("MacOS"))
        mae.fileBasePath= mae.codeBase.substring(6); /* "file:/" prefix */
      
      else
        mae.fileBasePath= mae.codeBase.substring(6); /* "file:/" prefix */
    } /* setup local files */
    
    else
    { /* setup up URL's */
      mae.fileBasePath= mae.codeBase;   /* [CHECK] all cases of how
       * the codeBase is used as
       * fileBasePath!!!! */
      int endOffset= mae.codeBase.indexOf('/',8);
      mae.maeServerBase= mae.codeBase.substring(0,endOffset+1);
    } /* setup up URL's */
    
      /* Determine whether we are on the Working Web server (mae/)
       * or not.
       */
    mae.isWorkingMAE= (mae.codeBase.startsWith("http://") &&
                       (mae.codeBase.endsWith("/mae/") ||
                        mae.codeBase.endsWith("/mae")
                        // || mae.codeBase.endsWith("/maeTest/") ||
                        // mae.codeBase.endsWith("/maeTest")
                       ));
      /*
      if(mae.CONSOLE_FLAG)
          mae.fio.logMsgln("CFG mae.isWorkingMAE="+mae.isWorkingMAE+
                           " mae.codeBase="+mae.codeBase+
                           "\n  mae.fileBasePath="+mae.fileBasePath+
                           "\n  mae.maeServerBase="+mae.maeServerBase);
       */
  } /* setupPathBase */
  
  
  /**
   * setupFullpathFilenames() - add the full path to the file names
   * @see #readPARAMvalues
   */
  void setupFullpathFilenames()
  { /* setupFullpathFilenames */    
   /* Setup the full file paths by adding the base path
    * the to file names. The file names are defined
    * as follows:
    *  1. use DEF_xxxx in Config class.
    *  2. Overide the filenames if any in the MaExlorer.cfg file
    *     using the ConfigTable class.
    *  3. Overide the filenames using PARAM values if any.
    */
    //if(mae.codeBase.startsWith("http://") ||
    // mae.codeBase.startsWith("file://"))
    //readPARAMvalues(mae.codeBase + "Config/");
    //else
    
    readPARAMvalues(mae.fileBasePath+"Config"+mae.dynFileSeparator);
  } /* setupFullpathFilenames */
  
  
  /**
   * computeMaxCanvasSize() - compute the maximum pseudoArray Canvas size
   * @return maximum pseudoarray canvas size, null if not using pseudoarrays
   * as (width,height) required given the array geometry.
   */
  Dimension computeMaxCanvasSize()
  { /* computeMaxCanvasSize */
    int
      maxHsize= mae.canvasHSize,
      maxVsize= mae.canvasVSize;
    
    if(mae.usePseudoXYcoordsFlag)
    { /* generate using pseudoarray from (F,G,R,C) */
      /* Setup the default guard region around target image */
      int
        guardLeftC= MaHPquantTable.GUARD_REGION,
        guardTopC= MaHPquantTable.GUARD_REGION,
        guardBottomC= MaHPquantTable.GUARD_REGION,
        guardRightC= MaHPquantTable.GUARD_REGION,
        guardXpixelsC= (guardLeftC + guardRightC),
        guardYpixelsC= (guardTopC + guardBottomC);
      
      /* [1] Compute pseudo coord deltas in case needed for pImage drawing. */
      float
        fmaxFIELDS= (float)maxFIELDS,
        fmaxGRIDS= (float)maxGRIDS+1,  /* Add 1 because of 0/1 counting problem */
        fmaxGROWS= (float)maxGROWS,
        fmaxGCOLS= (float)maxGCOLS;
      
      /* Use fixed spot PseudoArray spacing with dynamic canvasH[V]Size. */
      float
        scaleY= 1.0F,  /* 0.4F */
        scaleX= 1.5F,
        pseudoGRowSize= (fmaxGROWS*MaHPquantTable.DELTA_Y_SPACING + scaleY*guardTopC),
        pseudoGColSize= (fmaxGCOLS*MaHPquantTable.DELTA_X_SPACING + scaleX*guardLeftC);
      int
        xMax= (int)((fmaxFIELDS-1)*pseudoGColSize +
                    (guardLeftC + fmaxGCOLS*MaHPquantTable.DELTA_X_SPACING)),
        yMax= (int)((fmaxGRIDS-1)*pseudoGRowSize +
                    (guardTopC + fmaxGROWS*MaHPquantTable.DELTA_Y_SPACING));
      
      /* Save the computed data. Flip X and Y */
      maxHsize= xMax + MaHPquantTable.X_SPOTQ_OFFSET;
      maxVsize= yMax + MaHPquantTable.Y_SPOTQ_OFFSET;
    } /* generate using pseudoarray from (F,G,R,C) */
    
    /* Capture the state*/
    maxCalcPseudoArraySize= new Dimension(maxHsize, maxVsize);
    
    if(mae.CANVAS_WRAPAROUND_BUG)
      System.out.println("CFG-CMCS maxCalcPseudoArraySize.(H,W)=("+
                         maxHsize+", "+maxVsize+")"+
                         " (F,G,R,C)=("+maxFIELDS+","+maxGRIDS+","+
                         maxGROWS+","+maxGCOLS+")" );
    
    return(new Dimension((int)maxHsize, (int)maxVsize));
  } /* computeMaxCanvasSize */
  
  
} /* end of class Config */




