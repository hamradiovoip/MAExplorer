/** File: Filter.java */

import java.awt.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;

 /**
  * The class computes the intersection of selected data filter gene list.
  * This implements gene data filtering. The result is the working 
  * gene list workingCL that is then used through MAExplorer. The computation
  * uses the private gene list tmpCL in these calculations.
  *
  * <PRE>
  *	FILTER NAME                           Gene list name
  *   	===========                           ===============
  *  "The data filtered working gene list"    workingCL  
  *  "Filter by GeneClass membership"         gcMemberCL   
  *  "Filter by HP-X, HP-Y t-Test"            HP_XY_t_TestCL  
  *  "Filter by HP-XY sets t-Test"            HP_XYsets_t_TestCL 
  *  "Filter by HP-XY sets KS-Test"           HP_XYsets_KS_TestCL 
  *  "Filter by current OCL F-Test"           OCL_F_TestCL  
  *  "Filter by Edited Gene List"             gct.editedCL
  *  "Filter by userFilterGeneSet membership" gct.userFilterGeneSetCL
  *  "Use Spot CV filter"                     spotCVCL  
  *  "Threshold: spot intensity [SI1:SI2]"    spotIntensThrCL  
  *  "Threshold: intensity [I1:I2]"           intensityThrCL  
  *  "Threshold: ratio [R1:R2]"               ratioThrCL
  *  "Threshold: ratio [CR1:CR2]"             ratioCy3Cy5ThrCL
  *  "Filter by HP-E clusters"                HP_EclustersCL
  *  "Filter by Diff(HP-X,HP-Y)"              DiffHP_XYCL 
  *  "Filter by Positive Quant Data"          posQuantDataCL 
  *  "Filter by Good Spot(QualChk) Data"      goodSpotQualChkCL  
  *  "Filter by Spot(Detection value) Data"   detValueSpotDataCL  
  *  "Filter by non-zero Spot Data"           nonZeroSpotDataCL  
  *
  *  Additional Filters which are not in the normal Filter pipeline
  *  ==============================================================
  *  "Highest ratio genes"                    highestRatiosCL
  *  "lowest ratio genes"                     lowestRatiosCL
  *  "Tightly clustered genes"                clusteredGenesCL
  *  "Spots in the display list"              displayCL
  *  "Genes in K-means clusters"              KmeansNodesCL
  *  "Ratio histogram selected genes"         ratioHistCL
  *  "Intensity histogram selected genes"     intensHistCL
  *  "good genes"                             gct.goodGenesCL
  *  "replicate genes"                        gct.replicateGenesCL
  *  "Normalization genes"                    gct.normCL
  * </PRE>
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
  * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.22 $
  * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
  * @see GeneClass
  * @see GeneList
  * @see PopupRegistry
  */
 
class Filter 
{
  /** link to global MAExplorer instance */
  private MAExplorer 
    mae;                        
  /** link to global instance */
  //private Maps
    //map;                        
  /** link to global SampleSets instance */
  private SampleSets
    hps;                       
  /** link to globalFileIO instance */
  private FileIO
    fio;                       
  /** link to global Statistics instance */
  private Statistics
    stat;                        
  /** link to global GeneClass instance */
  private GeneClass
    gct;                     
      
  /** temporary gene list for intersections computations */
  private GeneList
    tmpCL;			
     
  /** --- properties if used --- */
  /** property: is filtered */
  final static int
    junk_IS_FILTERED= 1;  
  /** property: is not filtered */
  final static int             
    junk_NOT_FILTERED= 2;  
  /** property: has bad data */
  final static int
    junk_BAD_DATA= 4;  
 
  /* --- Following filters ARE part of the normal Filter pipeline ---  */
  /** Working gene list after run filter */
  GeneList
    workingCL;
  /** "Filter by GeneClass membership" */
  GeneList
    gcMemberCL;
  /** "Filter genes by HP-X,HP-Y t-Test"  i.e. mae.msListX(Y) */
  GeneList
    HP_XY_t_TestCL;
  /** "Filter genes by HP-X,HP-Y sets t-Test" i.e. mae.msX(Y) */
  GeneList
    HP_XYsets_t_TestCL;
  /** "Filter genes by HP-X,HP-Y sets Kolmagorove-Smirnov -Test" i.e. mae.msListX(Y)  */
  GeneList
    HP_XYsets_KS_TestCL;
  /** "Filter genes by current Ordered Condition List F-Test" i.e. current OCL */
  GeneList
    OCL_F_TestCL;
    /** "Use Spot CV filter" */
  GeneList
    spotCVCL;
  /** "Threshold: spot intensity [RI1:RI2]" */
  GeneList
    spotIntensThrCL;
    /** "Threshold: intensity [I1:I2]" */
  GeneList
    intensityThrCL;
    /** "Threshold: ratio [R1:R2]" */
  GeneList
    ratioThrCL;
  /** "Threshold: ratio [CR1:CR2]" */
  GeneList
    ratioCy3Cy5ThrCL;
    /** "Filter by HP-E clusters" */ 
  GeneList
    HP_EclustersCL;
    /** "Filter by Diff(HP-X,HP-Y)" */        
  GeneList
    DiffHP_XYCL;
  /** "Filter by Positive Quant Data"  */
  GeneList
    posQuantDataCL;
  /** "Filter by Good Spot(QualChk) Data" */    
  GeneList
    goodSpotQualChkCL;
  /** "Filter by Spot (Detection Value) Data" */    
  GeneList
    detValueSpotDataCL;
  /** "Filter by non-zero Spot Data" */    
  GeneList
    nonZeroSpotDataCL;
    
   /* --- Following filters ARE NLOT part of the normal Filter pipeline ---  */
   /** "Highest ratios" */ 
  GeneList
    highestRatiosCL;
    /** "Lowest ratios" */
  GeneList
    lowestRatiosCL;
  /** "Tightly clustered genes" */
  GeneList
    clusteredGenesCL;
    /** "Spots in the display list" */
  GeneList
    displayCL;
  /** "Genes in primary K-means nodes" */           
  GeneList
    KmeansNodesCL;
    /** "Ratio histogram selected genes" */
  GeneList
    ratioHistCL;
  /** "Intensity histogram selected genes" */
  GeneList
    intensHistCL;
   
  /** max # genes/genelist */
  int
    maxGenes; 
                 
  /** table lookup for gene membership */
  boolean
    geneInWorkingGeneList[];  
 
  /** names of active filters for drawing in pseudo array image [0:nActiveFilters-1] */
  String
    activeFilterNames[];       
  /** # of active filters */
  int      
    nActiveFilters;     
  /** list of Mids used in passing data back and forth to FilterPlugins */
  int
    midList[];
           
    
	      
  /**
   * Filter() - constructor to initialize data filter and initial gene lists.
   * @param mae is the MAExplorer instance
   * @param maxGenes is the max # of possible genes
   * @see #allocateGeneLists
   */
  Filter(MAExplorer mae, int maxGenes )
  { /* main */
    this.mae= mae;            /* setup global links */
    this.maxGenes= maxGenes;
    hps= mae.hps;
    fio= mae.fio;
    stat= mae.stat;
    gct= mae.gct;
    
    if(maxGenes==0)
      return;
    
    /* Create GeneLists */
    if(workingCL==null)
      allocateGeneLists(); /* create initial gene lists */
  } /*Filter */

    
  /**
   * Filter() - constructor to initialize data filter but not initial gene lists
   * @param mae is the MAExplorer instance
   */
  Filter(MAExplorer mae)
  { /* main */
    this.mae= mae;
    this.maxGenes= mae.mp.maxGenes;        /* local copy of master count */
    
    /* Gene lists used in the filter */
    workingCL= null;		          /* G.C. them */
    gcMemberCL= null;
    HP_XY_t_TestCL= null;
    HP_XYsets_t_TestCL= null;
    HP_XYsets_KS_TestCL= null;
    OCL_F_TestCL= null;
    spotCVCL= null;
    spotIntensThrCL= null;
    intensityThrCL= null;
    ratioThrCL= null;
    ratioCy3Cy5ThrCL= null;
    HP_EclustersCL= null;
    DiffHP_XYCL= null;
    posQuantDataCL= null;
    goodSpotQualChkCL= null;
    detValueSpotDataCL= null;
    nonZeroSpotDataCL= null;
    
    /* secondary gene lists */
    highestRatiosCL= null;
    lowestRatiosCL= null;
    clusteredGenesCL= null;
    displayCL= null;
    KmeansNodesCL= null;            /* allocated in ClusterGene */
    ratioHistCL= null;
    intensHistCL= null;
  } /*Filter */


  /**
   * allocateGeneLists() - create all of the gene lists if they were not previously created
   * @see GeneList
   */
  private void allocateGeneLists()
  { /* allocateGeneLists */
    activeFilterNames= new String[30];  /* max # active filters */
    nActiveFilters= 0;
    
    if(maxGenes==0)
      maxGenes= mae.mp.maxGenes;       /* Local copy of master count */
    
    /* Create GeneLists */
    if(workingCL==null)
    { /* create initial gene lists */
      midList= new int[maxGenes];  /* used in FilterPlugins */   
      
      tmpCL=  new GeneList(mae,maxGenes,"tmpCL", true);      
      workingCL= new GeneList(mae,maxGenes,"workingCL", true); /*i.e. filtered*/
      
      gcMemberCL= new GeneList(mae,maxGenes,"gcMemberCL", false);
      HP_XY_t_TestCL= new GeneList(mae,maxGenes, "HP_XY_t_TestCL", true);
      HP_XYsets_t_TestCL= new GeneList(mae,maxGenes,"HP_XYsets_t_TestCL",true);
      HP_XYsets_KS_TestCL= new GeneList(mae,maxGenes,"HP_XYsets_KS_TestCL",true);
      OCL_F_TestCL= new GeneList(mae,maxGenes,"OCL_F_TestCL",true);
      spotCVCL= new GeneList(mae,maxGenes,"spotCVCL",true);
      intensityThrCL= new GeneList(mae,maxGenes,"intensityThrCL", true);
      ratioThrCL= new GeneList(mae,maxGenes,"ratioThrCL",true);
      ratioCy3Cy5ThrCL= new GeneList(mae,maxGenes,"ratioCy3Cy5ThrCL",true);
      HP_EclustersCL= new GeneList(mae,maxGenes, "HP_EclustersCL", true);
      DiffHP_XYCL= new GeneList(mae,maxGenes, "DiffHP_XYCL", true);
      posQuantDataCL= new GeneList(mae,maxGenes,"posQuantDataCL", false);
      goodSpotQualChkCL= new GeneList(mae,maxGenes,"goodSpotQualChkCL", false);
      
      detValueSpotDataCL= new GeneList(mae,maxGenes,"detValueSpotDataCL", false);
      nonZeroSpotDataCL= new GeneList(mae,maxGenes,"nonZeroSpotDataCL", false);
      
      highestRatiosCL= new GeneList(mae,maxGenes,"highestRatiosCL",true);
      lowestRatiosCL= new GeneList(mae,maxGenes,"lowestRatiosCL",true);
      clusteredGenesCL= new GeneList(mae,maxGenes,"clusteredGenesCL",true);
      
      displayCL= new GeneList(mae,maxGenes, "displayCL",true);
      ratioHistCL= new GeneList(mae,maxGenes, "ratioHistCL",true);
      intensHistCL= new GeneList(mae,maxGenes, "intensHistCL", true);
      spotIntensThrCL= new GeneList(mae,maxGenes, "spotIntensHistCL", true);
      
       /* NOTE: additional GeneLists are setup in GeneClass when
        * it is initialized. These include:
        *   gct.userFilterGeneSetCL, gct.editedCL, gct.normCL
        */
    } /* create initial gene lists */
    
  } /* allocateGeneLists */
       
      
  /** 
   * showNbrFilteredGenes() - update Msg3 status line with the number of genes passing filter
   * @see Util#showMsg3
   */
  void showNbrFilteredGenes()
  { /* showNbrFilteredGenes */
    Util.showMsg3("There are "+ workingCL.length + " genes passing the Filter.");
  } /* showNbrFilteredGenes */     
  
  
  /**
   * doFilterPluginPipelineInitOpr() - do pipeline Init operation on all active
   * FilterPlugin's.
   * @param pipelineInitOpr - is either "resetPipeline" or "finishPipeline"
   * @return true if succeed
   */
  boolean doFilterPluginPipelineInitOpr(String pipelineInitOpr, int argVal)
  { /* doFilterPluginPipelineInitOpr */
    if(!pipelineInitOpr.equals("resetPipeline") &&
       !pipelineInitOpr.equals("finishPipeline"))
      return(false);
    
    if(MAEPlugin.analysis.FilterPlugin.getFilterState())
    { /* operate on all active MAEPlugin.analysis.FilterPlugins */
      Object argList[]= new Object[] {new Integer(argVal)};
      Vector filters= MAEPlugin.analysis.FilterPlugin.getActiveFilterPluginList();
      int nFilters= (filters!=null) ? filters.size() : 0;
      if(nFilters==0)
        return(false);
      boolean didOprFlag;
      
      for(int i= 0; i < nFilters; i++)
      { /* process the nFilters */
        Object cObj= filters.elementAt(i);
        /* fp is the MAEPlugin.analysis.FilterPlugin instance */
        MAEPlugin.analysis.FilterPlugin fp= null;
        try
        { /* cast it but check for bogus class type */
          fp= (MAEPlugin.analysis.FilterPlugin)cObj;
        }
        catch(Exception em)
        {
          Util.showMsg("Can't invoke Filter Plugin - it is not a FilterPlugin class.");          
          Util.showMsg2("Exception: "+em.toString(),Color.red,Color.white);
          em.printStackTrace();
          Util.popupAlertMsg("Can't invoke Filter Plugin",
                             "Can't invoke Filter Plugin - it is not a FilterPlugin class.\n"+
                             "Exception: "+em.toString(),
                             4, 60);
          continue;
        }
        
        try
        { /* invoke method instance of FitlerPlugin.setOperation() */
          Class classTypes[]= new Class[] { int.class };
          Method m= fp.getClass().getMethod(pipelineInitOpr, classTypes);
          Object result= m.invoke((Object)fp, argList);
          didOprFlag= ((Boolean)result).booleanValue();
          /* [TODO] could use the returned value */
        }
        catch(Exception em)
        {
          System.out.println("F-CWGL "+pipelineInitOpr+
                             " FilterPlugin invoke() Exception \n em='"+em+"'");          
          em.printStackTrace();
          continue;
        }
      } /* process the nFilters */
    } /* operate on all active MAEPlugin.analysis.FilterPlugins */
    
    return(true);
  } /* doFilterPluginPipelineInit */
  
  
  /**
   * doFilterPluginPipelineOpr() - do pipeline operation on all active FilterPlugin's.
   * @return true if succeed
   */
  boolean doFilterPluginPipelineOpr()
  { /* doFilterPluginPipelineOpr */
    if (!MAEPlugin.analysis.FilterPlugin.getFilterState())
      return(true);
    
    boolean flag= true;
    
    /* Call all MAEPlugin.analysis.FilterPlugins */
    Vector filters= MAEPlugin.analysis.FilterPlugin.getActiveFilterPluginList();
    int nFilters= (filters!=null) ? filters.size() : 0;
    for(int i= 0; i < nFilters; i++)
    { /* process the nFilters */
      int
        j= 0,                /* # of active genes in midList[] */
        nFiltered= 0;        /* # of genes computed by filter */
      
      /* NOTE: work with MID list not clone bitset */
      flag &= tmpCL.bitSet.copyBStoCL(tmpCL,tmpCL.bitSet);
      Gene geneFP;
      for(int n=0;n<tmpCL.length;n++)
      {
        geneFP= tmpCL.mList[n];
        if(geneFP!=null)
          midList[j++]= geneFP.mid;
      }
      
      Object 
        argList[]= new Object[] {midList, new Integer(j)},
        cObj= filters.elementAt(i);
      
      /* fp is the MAEPlugin.analysis.FilterPlugin instance */
      MAEPlugin.analysis.FilterPlugin  fp= null;
      try
      { /* cast it but check for bogus class type */
        fp= (MAEPlugin.analysis.FilterPlugin)cObj;
      }
      catch(Exception em)
      {
        Util.showMsg("Can't invoke Filter Plugin - it is not a FilterPlugin class.");        
        Util.showMsg2("Exception: "+em.toString(),Color.red,Color.white);
        Util.popupAlertMsg("Can't invoke Filter Plugin",
                           "Can't invoke Filter Plugin - it is not a FilterPlugin class.\n"+
                           "Exception: "+em.toString(),
                           4, 60);
        continue;
      }
      
      try
      { /* invoke method instance of FitlerPlugin.setOperation() */
        Class classTypes[]= new Class[] { int[].class, int.class };
        Method m= fp.getClass().getMethod("setOperation", classTypes);
        Object result= m.invoke((Object)fp, argList);
        nFiltered= ((Integer)result).intValue();
      }
      catch(Exception em)
      {
        System.out.println("F-DFPPO FilterPlugin invoke() Exception em='"+em+"'");        
        em.printStackTrace();
        continue;
      }
      
      //nFiltered= filters.elementAt(i).setOperation(midList, j);
      
      if(nFiltered>0)
      { /* rebuild the tmpCL gene list */
        tmpCL.clear();
        int mid;
        Gene gene;
        for(int n=0; n<nFiltered;n++)
        { /* add gene to tmpCL gene list */
          mid= midList[n];
          gene= mae.mp.midStaticCL.mList[mid];
          tmpCL.addGene(gene);
        }
      } /* rebuild the tmpCL gene list */
      else
      { /* abort processing since a filter failed[ CHECK] */
        flag= false;
        break;
      }
    } /* process the nFilters */
                                   
    return(flag);                             
  } /* doFilterPluginPipelineOpr */

     
   /**
    * computeWorkingGeneList() - run the data filter gene to compute the workingCL gene list.
    * This data filter computes the successive intersections of all genes
    * with GeneLists from all active restrictive filters.
    * The restrictive filters are recomputed before the intersection
    * is done.
    * NOTE: this is expensive, so don't call every repaint() cycle!!!
    *
    * @return FALSE if there is no filter setting workingCL to all genes.
    * Otherwise, the filter is in workingCL.
    * If we return true, we may want to mae.repaint() since we may need
    * to update the display from workingCL.
    * @see GeneBitSet#copyBStoCL
    * @see GeneClass#getGeneListOfCurrentGeneClass
    * @see GeneList#addGene
    * @see GeneList#clear
    * @see GeneList#copy
    * @see GeneList#intersection
    * @see PopupRegistry#updateFilter
    * @see Util#showMsg
    * @see Util#showMsg2
    * @see Util#showMsg3
    * @see #allocateGeneLists
    * @see #doFilterPluginPipelineOpr
    * @see #setCy3Cy5RatiosCL
    * @see #setGeneListBySpotCV
    * @see #setGoodSpotQuantData
    * @see #setHP_XYsets_t_TestCL
    * @see #setHP_XY_t_TestCL
    * @see #setHP_XY_KS_TestCL
    * @see #setOCL_F_TestCL
    * @see #setPostiveQuantData
    * @see #setRatiosGeneList
    * @see #setSIthresholdsCL
    * @see #setThresholdsCL
    * @see #showNbrFilteredGenes
    *
    */
   boolean computeWorkingGeneList()
   { /* computeWorkingGeneList */
     boolean
       useHP_XYratio= true, /* set false if want to do F1/F2 ratio */
       flagGen= false,      /* set when do a compultation to gene set */
       flag= true;          /* if there is any problems with computing
                             * the chain of filters */
     
      /*
      if(mae.CONSOLE_FLAG)
         fio.logMsgln("FILTER-CWCL.1 StartTime="+ Util.dateStr());
       */
     
      /* [1] Do NOT run the filter if there is no data in the database
       * which would be the case if no samples were loaded when you
       * run it from the Start menu rather than clicking on a .mae file.
       */
     if(mae.hps.nHP<=0)
       return(false);        /* can't run the filter if there is no data */
     
     /* [1.1] Create GeneLists if never done before... */
     if(workingCL==null)
       allocateGeneLists();    /* create initial gene lists if needed */
     nActiveFilters= 0;
     
     /* [2] if any GeneList.operation() fails, it returns false!
      * Start with tmpCL with all genes set.
      */
     flagGen= tmpCL.copy(tmpCL, mae.gct.allGenesCL);
     flag &= workingCL.clear();
     
     if(tmpCL.length==0 || gcMemberCL==null || HP_XY_t_TestCL==null ||
        HP_XYsets_t_TestCL==null || HP_XYsets_KS_TestCL==null || OCL_F_TestCL==null ||
        spotCVCL==null || ratioThrCL==null ||
        spotIntensThrCL==null || intensityThrCL==null ||
        ratioThrCL==null || ratioCy3Cy5ThrCL==null ||
        HP_EclustersCL==null || ratioHistCL==null || intensHistCL==null)
     { /* failed, just return a list of all genes */
       workingCL.copy(workingCL, mae.gct.allGenesCL);
       return(false);
     }     
     
     /* [2.1] Reset any active plugin pipelines */
     doFilterPluginPipelineInitOpr("resetPipeline", 0);
     
     /* [3] Update Filters and do Gene list intersections. If there
      * is any problems, then abort the computations an just return the
      * set of all genes.
      */
     if(tmpCL.length>0 && mae.geneClassMbrFilterFlag)
     { /* "Filter by GeneClass membership" */
       activeFilterNames[nActiveFilters++]= "Gene Class";
       /* recompute gcMemberCL */
       gcMemberCL.copy(gcMemberCL,
       mae.gct.getGeneListOfCurrentGeneClass() );
       flag &= tmpCL.intersection(tmpCL, gcMemberCL, /* ARGS */
                                  tmpCL              /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter by GeneClass membership" */
     
     if(flag && mae.useGeneSetFilterFlag)
     { /*"Filter by useGeneSet membership"*/
       activeFilterNames[nActiveFilters++]= "Use Filter Gene Set";
       flag= tmpCL.intersection(tmpCL, gct.userFilterGeneSetCL, /* ARGS */
                                tmpCL                    /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /*"Filter by useGeneSet membership"*/
     
     if(flag && mae.useGoodGeneCLflag)
     { /* "Filter by goodGenesSet membership" */
       activeFilterNames[nActiveFilters++]= "Good Genes";
       flag= tmpCL.intersection(tmpCL, gct.goodGenesCL, /* ARGS */
                                tmpCL            /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter by goodGenesSet membership" */
     
     if(flag && mae.useReplicateGenesFlag)
     { /* "Filter by genes with replicates" */
       activeFilterNames[nActiveFilters++]= "Replicate Genes";
       flag= tmpCL.intersection(tmpCL, gct.replicateGenesCL, /* ARGS */
                                tmpCL             /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter by genes with replicates" */
     
     if(flag && mae.useRatioHistCLflag)
     { /* "Ratio histogram selected genes" */
       activeFilterNames[nActiveFilters++]= "Ratio hist bin";
       /* This is set by clicking on a bin in a ratio histogram */
       flag= tmpCL.intersection(tmpCL, ratioHistCL, /* ARGS */
                                tmpCL         /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Ratio histogram selected genes" */
     
     if(flag && mae.useIntensHistCLflag)
     { /* "Intensity histogram selected genes" */
       String
         sf1= mae.cfg.fluoresLbl1,
         sf2= mae.cfg.fluoresLbl2;
       if(mae.useCy5OverCy3Flag)
       { /* flip Cy3/Cy5 to Cy5/Cy3 */
         String
           sTmp= sf1;
         sf1= sf2;
         sf2= sTmp;
       }
       
       String
         changeStr= (mae.isZscoreFlag)
                      ? "("+sf1+"-"+sf2+") Zscore"
                      : "("+sf1+"/"+sf2+") ratio",
         ss= (mae.useRatioDataFlag) ? "Ratio" : "Intens.";
       activeFilterNames[nActiveFilters++]= ss+" hist bin";
       /* This is set by clicking on a bin in a intensity histogram */
       flag= tmpCL.intersection(tmpCL, intensHistCL, /* ARGS */
                                tmpCL         /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Intensity histogram selected genes" */
     
     if(flag && mae.useEditedCLflag)
     { /* "edited selected genes" */
       activeFilterNames[nActiveFilters++]= "E.G.L.";
       flag= tmpCL.intersection(tmpCL, gct.editedCL, /* ARGS */
                                tmpCL         /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "edited genes" */
     
     if(flag && mae.useSpotCVfilterFlag)
     { /* "Spot CV filter" */
       activeFilterNames[nActiveFilters++]= "CV of spots";
       /* [TODO] resolve how we handle multiple HPs.
        *
        * Maybe call spotCV test depending on what is
        * being compared. i.e.
        *     mae.ms                    "Filter F1F2 current HP"
        *    (mae.msX, mae.msY)         "Filter F1F2 HP-X and HP-Y"
        *    (mae.msListX,mae.msListY)  "Filter F1F2 HP-X-set and HP-Y-set"
        *     mae.ListE                 "Filter F1F2 all HP-E"
        */
       /* could do it with Threshold filtering with wide open limits */
       flagGen= setGeneListBySpotCV(spotCVCL, tmpCL,
                                    mae.cfg.spotCVthr, mae.cvTestMode);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, spotCVCL,   /* ARGS */
                                  tmpCL       /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Spot CV filter" */
     
     if(flag && mae.spotIntensFilterFlag)
     { /* "Threshold: spotIntensity [SI1:SI2]" */
       activeFilterNames[nActiveFilters++]= "[SI1:SI2] Thr.";
       /* Set GeneList of thresholded F1,F2
        * where the threshold for rangeType [T1:T2] and rangeMode
        * RANGE_INSIDE, RANGE_OUTSIDE.
        * If msy is null, then use msx.F1 and msy.F2
        * else use msx.(F1+F2)/2 and  msy.(F1+F2)/2.
        * The dataType is mae.DATA_TOT, DATA_AVG.
        * The rangeTypeOveride is mae.RANGE_TYPE_INTENSITY.
        */
       flagGen= setSIthresholdsCL(tmpCL, /* ARGS */
                                  spotIntensThrCL /* RTN */ );
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, spotIntensThrCL, /* ARGS */
                                  tmpCL            /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Threshold: rawIntensity [RI1:RI2]" */
     
     if(flag && mae.intensFilterFlag)
     { /* "Threshold: intensity [I1:I2]" */
       activeFilterNames[nActiveFilters++]= "[I1:I2] Thr.";
       /* Set GeneList of thresholded F1,F2 or HP_X,HP_Y
        * where the threshold for rangeType [T1:T2] and rangeMode
        * RANGE_INSIDE, RANGE_OUTSIDE.
        * If msy is null, then use msx.F1 and msy.F2
        * else use msx.(F1+F2)/2 and  msy.(F1+F2)/2.
        * The dataType is mae.DATA_TOT, DATA_AVG.
        * The rangeTypeOveride is mae.RANGE_TYPE_INTENSITY.
        */
       flagGen= setThresholdsCL(tmpCL, /* ARGS */
                                intensityThrCL,   /* RTN */
                                mae.msX, mae.msY,
                                true  /* test gray range */);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, intensityThrCL, /* ARGS */
                                  tmpCL           /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Threshold: intensity [I1:I2]" */
     
     if(flag && mae.ratioFilterFlag)
     { /* "Threshold: ratio [R1:R2]" */
       activeFilterNames[nActiveFilters++]= "[R1:R2] Thr.";
       /* Set GeneList of thresholded F1,F2 or HP_X,HP_Y
        * where the threshold for rangeType [R1:R2] and rangeMode
        * RANGE_INSIDE, RANGE_OUTSIDE.
        * If msy is null, then use msx.F1 and msy.F2
        * else use msx.(F1+F2)/2 and  msy.(F1+F2)/2.
        * The dataType is mae.DATA_TOT, DATA_AVG.
        * The rangeTypeOveride is RANGE_TYPE_RATIO
        */
       flagGen= setThresholdsCL(tmpCL, /* ARG */
                                ratioThrCL,   /* RTN */
                                mae.msX, mae.msY,
                                false /* tests Ratio range */);       
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, ratioThrCL, /* ARGS */
                                  tmpCL       /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Threshold: ratio [R1:R2]" */
     
     if(flag && mae.ratioCy3Cy5FilterFlag)
     { /* "Threshold: ratio [CR1:CR2]" */
       activeFilterNames[nActiveFilters++]= "[CR1:CR2] Thr.";
       /* Set GeneList of thresholded genes
        * where the threshold for rangeType [CR1:CR2] and rangeMode
        * RANGE_INSIDE, RANGE_OUTSIDE.
        * Use msX.F1 (Cy3) and msX.F2 (Cy5).
        * [TODO] pick whether use HP-X, HP-Y, or HP-E for calcs.
        */
       flagGen= setCy3Cy5RatiosCL(tmpCL, /* ARG */
                                  ratioCy3Cy5ThrCL, /* RTN */
                                  mae.msX, mae.useCy5OverCy3Flag);       
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, ratioCy3Cy5ThrCL, /* ARGS */
                                  tmpCL       /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Threshold: ratio [CR1:CR2]" */
     if(flag && mae.tTestXYfilterFlag)
     { /* "Filter genes by HP-X,HP-Y t-Test" mae.msX(Y) */
       activeFilterNames[nActiveFilters++]= "t-Test X,Y";
       /* Generate t-test set.  */
       flagGen= setHP_XY_t_TestCL(HP_XY_t_TestCL, /* RTN */
                                  tmpCL,      /* only test these*/
                                  mae.msX, mae.msY,
                                  mae.cfg.pValueThr);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, HP_XY_t_TestCL, /* ARGS */
                                  tmpCL           /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter genes by HP-X,HP-Y t-Test" mae.msX(Y) */     
     
     if(flag && mae.tTestXYsetsFilterFlag)
     { /* "Filter genes by HP-X,HP-Y sets t-Test" mae.msListX(Y) */
       activeFilterNames[nActiveFilters++]= "t-Test X,Y 'sets'";
       /* Generate t-test set. */
       flagGen= setHP_XYsets_t_TestCL(HP_XYsets_t_TestCL,
                                      tmpCL,   /* only test these*/
                                      mae.cfg.pValueThr);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, HP_XYsets_t_TestCL, /* ARGS */
                                  tmpCL               /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter genes by HP-X,HP-Y sets t-Test" mae.msListX(Y) */
          
     if(flag && mae.KS_TestXYsetsFilterFlag)
     { /* "Filter genes by HP-X,HP-Y sets t-Test" mae.msListX(Y) */
       activeFilterNames[nActiveFilters++]= "KS-Test X,Y 'sets'";
       /* Generate KS-test set. */
       flagGen= setHP_XYsets_KS_TestCL(HP_XYsets_KS_TestCL,
                                      tmpCL,   /* only test these*/
                                      mae.cfg.pValueThr);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, HP_XYsets_KS_TestCL, /* ARGS */
                                  tmpCL               /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter genes by HP-X,HP-Y sets KS-Test" mae.msListX(Y) */
               
     if(flag && mae.F_TestOCLFilterFlag)
     { /* "Filter genes current Ordered Condition List F-Test" cdList.curOCL */
       activeFilterNames[nActiveFilters++]= "F-Test cur OCL";
       /* Generate KS-test set. */
       flagGen= setOCL_F_TestCL(OCL_F_TestCL,
                                      tmpCL,   /* only test these*/
                                      mae.cfg.pValueThr);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, OCL_F_TestCL, /* ARGS */
                                  tmpCL               /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter genes current Ordered Condition List F-Test" cdList.curOCL */
     
     if(flag && mae.clusterHP_EfilterFlag)
     { /*  "Filter by HP-E clusters"  */
       activeFilterNames[nActiveFilters++]= "HP-E cluster";
       flagGen= setHP_EclustersCL(HP_EclustersCL, /* RTN */
                                  tmpCL,      /* only test these*/
                                  mae.cfg.clusterDistThr);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, HP_EclustersCL, /* ARGS */
                                  tmpCL           /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter by HP-E clusters"  */
     
     if(flag && mae.useDiffFilterFlag)
     { /* "Filter by Diff(HP-X,HP-Y)" */
       activeFilterNames[nActiveFilters++]= "Diff(HP-X,HP-Y)";
       flagGen= setDiffHP_XYCL(DiffHP_XYCL, /* RTN */
                               tmpCL,        /* only test these*/
                               mae.cfg.diffThr);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, DiffHP_XYCL, /* ARGS */
                                  tmpCL        /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Filter by Diff(HP-X,HP-Y)" */
     
     if(flag && mae.useHighRatiosFilterFlag)
     { /* "Use highest ratios filter" for each array involved... */
       activeFilterNames[nActiveFilters++]= "Highest HP-XY" +
                                            ((mae.isZscoreFlag)
                                                ? "Zdiff" : "ratio");
       /* [TODO] resolve how we handle multiple HPs - don't enable until
        * Could just sort tmpCL... in place.
        */
       flagGen= setRatiosGeneList(highestRatiosCL, tmpCL,
                                  mae.cfg.maxGenesToRpt,
                                  useHP_XYratio, true /*sortBy Highest*/);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, highestRatiosCL, /* ARGS */
                                  tmpCL            /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Use highest ratios filter" for each array involved... */
     
     if(flag && mae.useLowRatiosFilterFlag)
     { /* "Use lowest ratios filter" for each array involved... */
       activeFilterNames[nActiveFilters++]= "Lowest HP-XY" +
                                            ((mae.isZscoreFlag)
                                               ? "Zdiff" : "ratio");
       /* [TODO] resolve how we handle multiple HPs - don't enable until
        * Could just sort tmpCL... in place
       */
       flagGen= setRatiosGeneList(lowestRatiosCL, tmpCL,
                                  mae.cfg.maxGenesToRpt,
                                  useHP_XYratio, false /*sort By highest */);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, lowestRatiosCL,  /* ARGS */
                                  tmpCL            /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     } /* "Use lowest ratios filter" for each array involved... */
     
     if(flag && mae.usePosQuantDataFlag)
     { /* "Filter out genes with negative quant values" */
       activeFilterNames[nActiveFilters++]= "Only quant. data > 0";
       flagGen= setPostiveQuantData(posQuantDataCL, tmpCL,
                                    mae.posQuantTestMode);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, posQuantDataCL, /* ARGS */
                                  tmpCL           /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     }
     
     if(flag && mae.useGoodSpotDataFlag)
     { /* "Filter by Good Spot(QualChk) Data" */
       activeFilterNames[nActiveFilters++]= "Good Spot data";
       flagGen= setGoodSpotQuantData(goodSpotQualChkCL, tmpCL,
                                     mae.goodSpotTestMode);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, goodSpotQualChkCL, /* ARGS */
                                  tmpCL           /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     }
     
     if(flag && mae.useDetValueSpotDataFlag)
     { /* "Filter by Spot (Detection Value) Data" */
       /* Note: only filter if the data exists */
       activeFilterNames[nActiveFilters++]= "Spot Detect Value data";
       flagGen= setDetValueQuantData(detValueSpotDataCL, tmpCL,
                                     mae.detValueSpotTestMode);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, detValueSpotDataCL, /* ARGS */
                                  tmpCL           /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     }
     
     if(flag && mae.useOnlyGenesWithNonZeroDensityFlag)
     { /* "Filter use only genes with non-zero intensity"" */
       /* Note: only filter if the non-zero data exists */
       activeFilterNames[nActiveFilters++]= "Use only non-zero data";
       flagGen= setNonZeroQuantData(nonZeroSpotDataCL, tmpCL);
       if(flagGen)
         flag= tmpCL.intersection(tmpCL, nonZeroSpotDataCL, /* ARGS */
                                  tmpCL           /* RTN */);
       flag &= (tmpCL.bitSet.count>0);
     }
     
     /* [3.1] Do pipeline operation on all active FilterPlugin's. */
     flag= flag && doFilterPluginPipelineOpr();     
     
     /* [3.2] Finish any active plugin pipelines */     
     doFilterPluginPipelineInitOpr("finishPipeline",0);
     
     /* [4] Copy the final result from tmpCL to workingCL */
     /*
     if(mae.CONSOLE_FLAG)
       fio.logMsgln("F-CWCL.2     tmpCL.(length, bitSet.(count, highestMID))="+
                      tmpCL.length+
                      " ("+ tmpCL.bitSet.count+
                      ","+ tmpCL.bitSet.highestMID+"))");
                      GeneBitSet.cbsDbug= (editedCL.length > 0);
     */
     
     /* [4.1] Convert bitset to GeneList workingCL both bitSet and mList[] */
     flag &= tmpCL.bitSet.copyBStoCL(workingCL, tmpCL.bitSet);
     
      /*
      if(mae.CONSOLE_FLAG)
        {
          GeneBitSet.cbsDbug= false;
          fio.logMsgln("F-CWCL.2 workingCL.(length, bitSet.(count, highestMID))="+
             workingCL.length+
             " ("+ workingCL.bitSet.count+
             ","+ workingCL.bitSet.highestMID+"))");
          fio.logMsgln("F-CWCL.2     tmpCL.(length, bitSet.(count, highestMID))="+
              tmpCL.length+
              " ("+ tmpCL.bitSet.count+
              ","+ tmpCL.bitSet.highestMID+"))");
          fio.logMsgln("F-CWCL.2  editedCL.(length, bitSet.(count, highestMID))="+
              editedCL.length+
              " ("+ editedCL.bitSet.count+
              ","+ editedCL.bitSet.highestMID+"))");
       }
       */
     
     /* [NOTE] If failed, just return a list of ALL genes */
     
     /* [4.2] update the # of genes found by the filter in Msg3 */
     showNbrFilteredGenes(); /* update Msg3 w/# genes passing filter */
     
     /* [4.3] Setup boolean table lookup for gene membership in workingCL */
     geneInWorkingGeneList= new boolean[mae.mp.maxGenes]; /* clears all to false */
     Gene
       gene,
       mList[]= workingCL.mList;
     int
       nGenes= workingCL.length,
       idxMID;
     for(int i=0;i<nGenes;i++)
     { /* mark genes in boolean[] working gene list - for speedup */
       gene= mList[i];
       idxMID= (gene==null) ? -1 : gene.mid;
       /*
       if(mae.CONSOLE)
         fio.logMsgln("F-CWCL i="+i+ " gene="+gene+ " idxMID="+idxMID);
       */
       if(idxMID>=0)
         geneInWorkingGeneList[idxMID]= true;
     } /* mark genes in boolean[] working gene list - for speedup */
     
     /* [4.4] update other processes that need to see the new gene list */
     if(flag)
       mae.pur.updateFilter(workingCL);             
     
     return(flag);
   } /* computeWorkingGeneList */
 
        
   /**
    * setPostiveQuantData() - filter out genes with negative quant values.
    * If any spot is < 0.0, then kill the gene depending on what is
    * being tested. <BR>
    * [NOTE] for now this assumes Data in one (AFFY) channel.
    * If we are dealing with Ratio or 2 Channel data, then
    * compare both channels.
    *<PRE>
    * Do posQuantTestMode test depending on what is being compared. i.e.
    *   SS_MODE_MS     mae.ms                   "Filter PosQuant current HP"
    *   SS_MODE_XY    (mae.msX, mae.msY)        "Filter PosQuant HP-X and HP-Y"
    *   SS_MODE_XYSET (mae.msListX,mae.msListY) "Filter PosQuant HP-X-set and HP-Y-set"
    *   SS_MODE_E      mae.ListE                "Filter PosQuant all HP-E"
    *</PRE>
    * @param posSpotDataCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param posQuantTestMode is the SS_MODE_xxxx specified above
    * @return true if succeed and results in posSpotDataCL, else
    * false if a problem.
    * @see ExprProfile
    * @see GeneList#clearNull
    * @see GeneList#addGene
    * @see MaHybridSample#getSpotDataStatic
    */
   boolean setPostiveQuantData(GeneList posQuantDataCL, GeneList genesToTestCL,
                               int posQuantTestMode)
   { /* setPostiveQuantData */
     if(posQuantDataCL==null)
       return(false);
     
     MaHybridSample
       ms= mae.ms,
       msX= mae.msX,
       msY= mae.msY,
       msListX[]= hps.msListX,
       msListY[]= hps.msListY,
       msListE[]= hps.msListE;
     int
       nTest=genesToTestCL.length,
       nX= hps.nHP_X,
       nY= hps.nHP_Y,
       nE= hps.nHP_E,
       maxFIELDS= mae.cfg.maxFIELDS;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     int
       mid,
       gid1,
       gid2= -1;
     SpotData sd;
     boolean
       isPosFlag,
       has2ChannelsFlag= (mae.cfg.maxFIELDS>1 || mae.useRatioDataFlag);
     
     posQuantDataCL.clearNull();
     
     /* Copy genes which do NOT have any values < 0.0 */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;
       /* test appropriate data for clone */
       isPosFlag= true;
       gid1= gene.gid;
       if(has2ChannelsFlag)
         gid2= mae.mp.gid2mid[gid1];
       
       if(posQuantTestMode==mae.SS_MODE_MS)
       { /* test posQuant data for current HP */
         sd= ms.getSpotDataStatic(gid1, false);
         if(sd.totS<0.0F)
           isPosFlag= false;
         if(isPosFlag && has2ChannelsFlag)
         {
           sd= ms.getSpotDataStatic(gid2, false);
           if(sd.totS<0.0F)
             isPosFlag= false;
         }
       } /* test posQuant data for current HP */
       
       else if(posQuantTestMode==mae.SS_MODE_XY)
       { /* (mae.msX, mae.msY) "test posQuant HP-X and HP-Y" mult spots */
         sd= msX.getSpotDataStatic(gid1, false);
         if(sd.totS<0.0F)
           isPosFlag= false;
         if(isPosFlag && has2ChannelsFlag)
         {
           sd= msX.getSpotDataStatic(gid2, false);
           if(sd.totS<0.0F)
             isPosFlag= false;
         }
         
         if(isPosFlag)
         {
           sd= msY.getSpotDataStatic(gid1,  false);
           if(sd.totS<0.0F)
             isPosFlag= false;
         }
         if(isPosFlag && has2ChannelsFlag)
         {
           sd= msY.getSpotDataStatic(gid2, false);
           if(sd.totS<0.0F)
             isPosFlag= false;
         }
       } /* (mae.msX, mae.msY) "test posQuant HP-X and HP-Y" mult spots */
       
       else if(posQuantTestMode==mae.SS_MODE_XANDY_SETS)
       { /* (mae.msListX, mae.msListY) "Filter posQuant HP-X-set & HP-Y-set"*/
         for(int i=1;i<=nX;i++)
         { /* check HP-X 'set' */
           sd= msListX[i].getSpotDataStatic(gid1, false);
           if(sd.totS<0.0F)
           {
             isPosFlag= false;
             break;
           }
           if(isPosFlag && has2ChannelsFlag)
           {
             sd= msListX[i].getSpotDataStatic(gid2, false);
             if(sd.totS<0.0F)
             {
               isPosFlag= false; 
               break;
             }
           }
         } /* check HP-X 'set' */
         
         if(isPosFlag)
           for(int i=1;i<=nY;i++)
           { /* check HP-Y 'set' */
             sd= msListY[i].getSpotDataStatic(gid1, false);
             if(sd.totS<0.0F)
             {
               isPosFlag= false;
               break;
             }
             if(isPosFlag && has2ChannelsFlag)
             {
               sd= msListY[i].getSpotDataStatic(gid2, false);
               if(sd.totS<0.0F)
               {
                 isPosFlag= false;
                 break;
               }
             }
           } /* check HP-Y 'set' */
       } /* (mae.msListX, mae.msListY) "Filter posQuant HP-X-set & HP-Y-set"*/
       
       else if(mae.posQuantTestMode==mae.SS_MODE_ELIST)
       { /* mae.ListE "Filter posQuant all HP-E" mult spots */
         for(int i=1;i<=nE;i++)
         {
           sd= msListE[i].getSpotDataStatic(gid1, false);
           if(sd.totS<0.0F)
           {
             isPosFlag= false;
             break;
           }
           if(isPosFlag && has2ChannelsFlag)
           {
             sd= msListE[i].getSpotDataStatic(gid2, false);
             if(sd.totS<0.0F)
             {
               isPosFlag= false;
               break;
             }
           }
         }
       } /* mae.ListE "Filter posQuant all HP-E" mult spots */
       
       else
         isPosFlag= false;
       
       if(isPosFlag)
       { /* use it */
         posQuantDataCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setPostiveQuantData */   
	
        
   /**
    * setGoodSpotQuantData() - filter out genes failing QualChk quant values.
    * Only valid it QualChk data exists! Test first
    *<PRE>
    * Do goodSpotTestMode test depending on what is being compared. i.e.
    *   SS_MODE_MS     mae.ms                   "Filter current HP"
    *   SS_MODE_XY    (mae.msX, mae.msY)        "Filter HP-X and HP-Y"
    *   SS_MODE_XYSET (mae.msListX,mae.msListY) "Filter HP-X-set and HP-Y-set"
    *   SS_MODE_E      mae.ListE                "Filter all HP-E"
    *
    * [TODO] Also, mae.qualityTypeMode selects various tests:
    *   QUALTYPE_ALPHA= 1,         - QualCheck types (Alphabetic codes)
    *   QUALTYPE_MAE_PROP_CODES    - uses Gene.xxxx prop codes
    *   QUALTYPE_THRESHOLD         - Uses qualThr threshold
    * </PRE>
    * @param goodSpotDataCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param goodSpotTestMode is the SS_MODE_xxxx specified above
    * @return true if succeed and results in goodSpotDataCL, else
    * false if a problem.
    * @see GeneList#clearNull
    * @see GeneList#addGene
    */
   boolean setGoodSpotQuantData(GeneList goodSpotDataCL, GeneList genesToTestCL,
                                int goodSpotTestMode)
   { /* setGoodSpotQuantData */
     if(goodSpotDataCL==null)
       return(false);
     
     MaHybridSample
       ms= mae.ms,
       msX= mae.msX,
       msY= mae.msY,
       msListX[]= hps.msListX,
       msListY[]= hps.msListY,
       msListE[]= hps.msListE;
     
     float
       qualChk,                        /* full QualCheck value */
       qualThr= mae.cfg.qualThr;  /* [TODO] use real float data */
     int
       mid,
       gid1,
       nTest=genesToTestCL.length,
       nX= hps.nHP_X,
       nY= hps.nHP_Y,
       nE= hps.nHP_E,
       maxFIELDS= mae.cfg.maxFIELDS;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     boolean
       isQualTypeThr= (mae.qualTypeMode==mae.QUALTYPE_THR),
       isOKflag;
     
     goodSpotDataCL.clearNull();
     
     /* Copy genes which do NOT have any values < 0.0 */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;
       /* test appropriate data for clone */
       isOKflag= true;
       gid1= gene.gid;
       
       if(goodSpotTestMode==mae.SS_MODE_MS)
       { /* testGood Spot data for current HP */
         qualChk= ms.qualChkQ[gid1];
         if((isQualTypeThr && qualChk<qualThr) ||
         (qualChk>0 && qualChk!=gene.C_GOOD_MID))
           isOKflag= false;
       }
       
       else if(goodSpotTestMode==mae.SS_MODE_XY)
       { /* (mae.msX, mae.msY) "test posQuant HP-X and HP-Y" mult spots */
         qualChk= msX.qualChkQ[gid1];
         if((isQualTypeThr && qualChk<qualThr) ||
         (qualChk>0 && qualChk!=gene.C_GOOD_MID))
           isOKflag= false;
         
         qualChk= msY.qualChkQ[gid1];
         if((isQualTypeThr && qualChk<qualThr) ||
         (qualChk>0 && qualChk!=gene.C_GOOD_MID))
           isOKflag= false;
       }
       
       else if(goodSpotTestMode==mae.SS_MODE_XANDY_SETS)
       { /* (mae.msListX, mae.msListY) "Filter posQuant HP-X-set & HP-Y-set"*/
         for(int i=1;i<=nX;i++)
         {
           qualChk= msListX[i].qualChkQ[gid1];
           if((isQualTypeThr && qualChk<qualThr) ||
           (qualChk>0 && qualChk!=gene.C_GOOD_MID))
           {
             isOKflag= false;
             break;
           }
         }
         if(isOKflag)
           for(int i=1;i<=nY;i++)
           {
             qualChk= msListY[i].qualChkQ[gid1];
             if((isQualTypeThr && qualChk<qualThr) ||
             (qualChk>0 && qualChk!=gene.C_GOOD_MID))
               
             {
               isOKflag= false;
               break;
             }
           }
       } /* (mae.msListX, mae.msListY) "Filter posQuant HP-X-set & HP-Y-set"*/
       
       else if(mae.goodSpotTestMode==mae.SS_MODE_ELIST)
       { /* mae.ListE "Filter good spot all HP-E" mult spots */
         for(int i=1;i<=nE;i++)
         {
           qualChk= msListE[i].qualChkQ[gid1];
           if((isQualTypeThr && qualChk<qualThr) ||
           (qualChk>0 && qualChk!=gene.C_GOOD_MID))
           {
             isOKflag= false;
             break;
           }
         }
       } /* mae.ListE "Filter good spot all HP-E" mult spots */
       
       else
         isOKflag= false;
       
       if(isOKflag)
       { /* use it */
         goodSpotDataCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setGoodSpotQuantData */
  
        
   /**
    * setDetValueQuantData() - filter out genes failing Detection Value quant values.
    * Only valid it Detection Value data exists! Test ms.hasDetValueDataFlag first.
    * It will threshold using the detValueSpotThr state threshold.
    *<PRE>
    * Do detValueSpotTestMode test depending on what is being compared. i.e.
    *   SS_MODE_MS     mae.ms                   "Filter current HP"
    *   SS_MODE_XY    (mae.msX, mae.msY)        "Filter HP-X and HP-Y"
    *   SS_MODE_XYSET (mae.msListX,mae.msListY) "Filter HP-X-set and HP-Y-set"
    *   SS_MODE_E      mae.ListE                "Filter all HP-E"
    * </PRE>
    * @param detValueSpotDataCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param detValueSpotTestMode is the SS_MODE_xxxx specified above
    * @return true if succeed and results in detValueSpotDataCL, else
    * false if a problem.
    * @see GeneList#clearNull
    * @see GeneList#addGene
    */
   boolean setDetValueQuantData(GeneList detValueSpotDataCL, GeneList genesToTestCL,
                                int detValueSpotTestMode)
   { /* setDetValueQuantData */
     MaHybridSample
       ms= mae.ms,
       msX= mae.msX,
       msY= mae.msY,
       msListX[]= hps.msListX,
       msListY[]= hps.msListY,
       msListE[]= hps.msListE;
     
     if(detValueSpotDataCL==null && !ms.hasDetValueSpotDataFlag)
       return(false);
     
     float
      detValue,                        /* full QualCheck value */
       detValueSpotThr= mae.cfg.detValueSpotThr;  /* [TODO] use real float data */
     int
       nTest=genesToTestCL.length,
       nX= hps.nHP_X,
       nY= hps.nHP_Y,
       nE= hps.nHP_E,
       maxFIELDS= mae.cfg.maxFIELDS,
       mid,
       gid1;
     Gene 
       gene,
       mList[]= genesToTestCL.mList;
     boolean isOKflag;
     
     detValueSpotDataCL.clearNull();
     
     /* Copy genes which do NOT have any values < 0.0 */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;       
       /* test appropriate data for clone */
       isOKflag= true;
       gid1= gene.gid;
       
       if(detValueSpotTestMode==mae.SS_MODE_MS)
       { /* test Spot data for current HP */
         detValue= ms.detValueQ[gid1];
         if(detValue>=detValueSpotThr || detValue<0.0F || detValue>1.0F)
           isOKflag= false;
       }
       
       else if(detValueSpotTestMode==mae.SS_MODE_XY)
       { /* (mae.msX, mae.msY) "test posQuant HP-X and HP-Y" mult spots */
         detValue= msX.detValueQ[gid1];
         if(detValue>=detValueSpotThr || detValue<0.0F || detValue>1.0F)
           isOKflag= false;
         
         detValue= msY.detValueQ[gid1];
         if(detValue>=detValueSpotThr || detValue<0.0F || detValue>1.0F)
           isOKflag= false;
       }
       
       else if(detValueSpotTestMode==mae.SS_MODE_XANDY_SETS)
       { /* (mae.msListX, mae.msListY) "Filter posQuant HP-X-set & HP-Y-set"*/
         for(int i=1;i<=nX;i++)
         {
           detValue= msListX[i].detValueQ[gid1];
           if(detValue>=detValueSpotThr || detValue<0.0F || detValue>1.0F)
           {
             isOKflag= false;
             break;
           }
         }
         if(isOKflag)
           for(int i=1;i<=nY;i++)
           {
             detValue= msListX[i].detValueQ[gid1];
             if(detValue>=detValueSpotThr || detValue<0.0F || detValue>1.0F)
             {
               isOKflag= false;
               break;
             }
           }
       } /* (mae.msListX, mae.msListY) "Filter posQuant HP-X-set & HP-Y-set"*/
       
       else if(mae.detValueSpotTestMode==mae.SS_MODE_ELIST)
       { /* mae.ListE "Filter good spot all HP-E" mult spots */
         for(int i=1;i<=nE;i++)
         {
           detValue= msListE[i].detValueQ[gid1];
           if(detValue>=detValueSpotThr || detValue<0.0F || detValue>1.0F)
           {
             isOKflag= false;
             break;
           }
         }
       } /* mae.ListE "Filter good spot all HP-E" mult spots */
       
       else
         isOKflag= false;
       
       if(isOKflag)
       { /* use it */
         detValueSpotDataCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setDetValueQuantData */
     
         	  
   /**
    * setNonZeroQuantData() - filter out genes with 0.0 quant values in any HP-EP samples.
    * If any spot is == 0.0, then kill the gene depending on what is
    * being tested. <BR>
    * @param nonZeroSpotDataCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @return true if succeed and results in nonZeroSpotDataCL, else
    * false if a problem.
    * @see ExprProfile
    * @see GeneList#clearNull
    * @see GeneList#addGene
    * @see MaHybridSample#getSpotDataStatic
    */
   boolean setNonZeroQuantData(GeneList nonZeroSpotDataCL, GeneList genesToTestCL)
   { /* setNonZeroQuantData */
     if(nonZeroSpotDataCL==null)
       return(false);     
    
     MaHybridSample msListE[]= hps.msListE;
     int
       nTest=genesToTestCL.length,
       nE= hps.nHP_E,
       maxFIELDS= mae.cfg.maxFIELDS;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     int
       mid,
       gid1,
       gid2= -1;
     SpotData sd;
     boolean
       isNonZeroFlag,
       has2ChannelsFlag= (mae.cfg.maxFIELDS>1 || mae.useRatioDataFlag);
     
     nonZeroSpotDataCL.clearNull();
     
     /* Copy genes which do NOT have any values < 0.0 */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;
       /* test appropriate data for clone */
       isNonZeroFlag= true;
       gid1= gene.gid;
       if(has2ChannelsFlag)
         gid2= mae.mp.gid2mid[gid1];
       
       /* mae.ListE "Filter posQuant all HP-E" mult spots */
       for(int i=1;i<=nE;i++)
       { /* check all spots in all EP arrays */
         sd= msListE[i].getSpotDataStatic(gid1, false);
         if(sd.totS==0.0F)
         {
           isNonZeroFlag= false;
           break;
         }
         if(has2ChannelsFlag)
         {
           sd= msListE[i].getSpotDataStatic(gid2, false);
           if(sd.totS==0.0F)
           {
             isNonZeroFlag= false;
             break;
           }
         }
       } /* check all spots in all EP arrays */
              
       if(isNonZeroFlag)
       { /* use it */
         nonZeroSpotDataCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setNonZeroQuantData */
   
	 
  /**     
   * setRatiosGeneList() set ratiosCL by reportMode and sort by specified direction.
   * @param ratiosCL is the gene list returned with a new set of genes
   * @param genesToTestCL list of genes to test   
   * @param maxGenesToRpt is the maximum number of genes to test in report 
   * @param useHP_XYratio is flag to use HP X/Y ratio else use F1/F2 ratio
   * @param sortDirection as either ASSENDING or DESCENDING
   * @return true if succeed and results in ratiosCL, else
   * false if a problem.
   * @see Gene#setGeneData
   * @see Gene#sortGeneList
   * @see GeneList#clearNull
   * @see GeneList#addGene
   * @see GeneList#compressGeneList
   * @see HPxyData#updateDataAndStat
   * @see MaHybridSample#getDataByGID
   * @see Util#showMsg3
   */
   boolean setRatiosGeneList(GeneList ratiosCL, GeneList genesToTestCL,
                             int maxGenesToRpt, boolean useHP_XYratio,
   boolean sortDirection )
   { /* setRatiosGeneList */
     int nTest= genesToTestCL.length;
     float
       g1= 0.0F,
       g2= 0.0F,
       r= 0.0F;
     MaHybridSample
       ms= mae.ms,
       msX= mae.msX,
       msY= mae.msY;
     HPxyData hpXYdata= mae.cdb.hpXYdata;  /* generic HP-X/Y set object*/
     int
       nX= hpXYdata.nX,
       nY= hpXYdata.nY,
       mid,
       gid;
     ratiosCL.clearNull();
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     
     /* Copy genes which have a non-zero ratio */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid; 
       gid= gene.gid;
       
       if(useHP_XYratio && mae.useHPxySetDataFlag)
       { /* set data to H.P. msX/msY */
         if(!hpXYdata.updateDataAndStat(mid))
           continue;
         else
         { /* only use genes within range constraints*/
           g1= hpXYdata.mnXdata;
           g2= hpXYdata.mnYdata;
         }
       }
       else if(useHP_XYratio)
       { /* set data to msX.(F1+F2)/msY.(F1+F2) */
         int
         method= (mae.useRatioDataFlag)
         ? ms.DATA_RATIO_F1F2TOT
         : ms.DATA_MEAN_F1F2TOT;
         g1= msX.getDataByGID(gid, mae.useRatioDataFlag, method);
         g2= msY.getDataByGID(gid, mae.useRatioDataFlag, method);
       }
       else
       { /* set data to ms.F1/ms.F2 */
         g1= ms.getDataByGID(gid, mae.useRatioDataFlag, ms.DATA_F1TOT);
         g2= ms.getDataByGID(gid, mae.useRatioDataFlag, ms.DATA_F2TOT);
       }
       
       /* Compute Zdiff or ratio */
       r= (mae.isZscoreFlag) ? (g1-g2) : g1/g2;
       
       gene.setGeneData(r,g1,g2);
       ratiosCL.addGene(gene);
     } /* test each gene */
     
       /*
       if(mae.CONSOLE_FLAG)
    fio.logMsgln("Filter:SRCL (msX==msY)="+ (msX==msY) +
         ", msX["+msX.hpName+"]"+
         ", msY["+msY.hpName+"]");
      if(mae.CONSOLE_FLAG)
    fio.logMsgln("Filter:SRCL nTest="+nTest);
        */
     
     if(nTest>maxGenesToRpt)
     { /* sort by ratio and then pick the maxGenesToRpt */
       Util.showMsg3("Sorting genes...");
       Gene.sortGeneList(ratiosCL.mList, ratiosCL.length,
       sortDirection);
    /* Then shorten the list by deleting [maxGenesToRpt:nTest]
     * and changing the length.
     */
       for(int j=maxGenesToRpt-1; j<nTest; j++)
         ratiosCL.mList[j]= null;
       
       ratiosCL.compressGeneList();
     }
     
     return(true);
   } /* setRatiosGeneList */
        
     
   /**
    * setGeneListBySpotCV() - do test of coefficient of variation against threshold.
    * The mae.useCVmaxElseMeanFlag determines whether we use max 
    * or mean of the CVs.
    * If maxFIELDS==1, then can't do F1F2 for single HP.
    *<PRE>
    * Call F1F2CV test depending on what is being compared. i.e.
    *   SS_MODE_MS     mae.ms                    "Filter F1F2 current HP"
    *   SS_MODE_XY    (mae.msX, mae.msY)         "Filter F1F2 HP-X and HP-Y"
    *   SS_MODE_XYSET (mae.msListX)              "Filter F1F2 HP-X-set"   
    *   SS_MODE_XYSET (mae.mae.msListY)          "Filter F1F2 HP-Y-set"     
    *   SS_MODE_XYSET (mae.msListX,mae.msListY)  "Filter F1F2 HP-X-set and HP-Y-set"
    *   SS_MODE_ELIST  mae.ListE                 "Filter F1F2 all HP-E"
    *</PRE>
    * @param cvResultCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param spotCVthr is the coefficient of variantion threshold to use
    * @param cvMode is the way to compute the CV (see SS_MODE_xxxx above)
    * @return true if succeed and results in cvResultsCL, else
    * false if a problem.
    * @see ExprProfile#updateData
    * @see GeneList#clearNull
    * @see GeneList#addGene
    * @see HPxyData#updateDataAndStat
    * @see MaHybridSample#getSpotData12Static
    * @see #calcF1F2CV
    */ 
   boolean setGeneListBySpotCV(GeneList cvResultCL, GeneList genesToTestCL,
                               float spotCVthr, int cvTestMode)
   { /* setGeneListBySpotCV */
     if(cvResultCL==null)
       return(false);
     
     ExprProfile ep= null;
     if(mae.cvTestMode==mae.SS_MODE_ELIST)
       ep= new ExprProfile(mae,-1, false);
     HPxyData hpxy= mae.cdb.hpXYdata;
     boolean
      testXYflag= false,
      singleFlag= false,
      doubleFlag= false;
     MaHybridSample
       ms= mae.ms,
       msX= mae.msX,
       msY= mae.msY,
       msListX[]= hps.msListX,
       msListY[]= hps.msListY;
     int
       nTest=genesToTestCL.length,
       nX= hps.nHP_X,
       nY= hps.nHP_Y,
       nE= hps.nHP_E;
     
     cvResultCL.clearNull();
     SpotData sd;
     float
       sumCV,        /* compute both and then pick one want */
       cv,           /* computes Max but may overide with Mean */
       cv1= 0.0F,
       cv2= 0.0F,
       cvI;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     int
       mid,
       gid1, gid2,
       maxFIELDS= mae.cfg.maxFIELDS,
       type= (mae.useRatioDataFlag)
                ? mae.ms.DATA_RATIO_F1F2TOT : mae.ms.DATA_F1TOT;
     float
       g1, g2;
     
     /* Copy genes which pass the t-test */
     for(int k=0;k<nTest;k++)
     { /* process mid */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid; 
       gid1= gene.gid;
       gid2= (maxFIELDS>1) ? mae.mp.gidToGangGid[gid1] : -1;
       
       sumCV= 0.0F;
       cv= 0.0F;
       
       if(cvTestMode==mae.SS_MODE_MS)
       { /* Get F1F2 CV data for current HP */
         if(gid2==-1)
           continue;
         sd= ms.getSpotData12Static(gid1, gid2, false);
         g1= sd.tot1S;
         g2= sd.tot2S;
         cv= calcF1F2CV(g1, g2);
         testXYflag= false;
       }
       
       else if(cvTestMode==mae.SS_MODE_XY)
       { /* (mae.msX, mae.msY) "Filter F1F2 HP-X and HP-Y" mult spots */
         sd= msX.getSpotData12Static(gid1, gid2,mae.useRatioDataFlag);
         cv1= calcF1F2CV(sd.tot1S, sd.tot2S);
         sd= msY.getSpotData12Static(gid1, gid2, mae.useRatioDataFlag);
         cv2= calcF1F2CV(sd.tot1S, sd.tot2S);
         cv= Math.max(cv1,cv2);   /* default to max which may overide */
         testXYflag= true;
       }
       
       else if(cvTestMode==mae.SS_MODE_XSET)
       { /* (mae.msListX) "Filter F1F2 HP-X-set"*/
         /* get (means, std-dev, n) data to compute p-value */
         if(!hpxy.updateDataAndStat(mid))
           continue;
         /* default to max which may overide */
         cv= hpxy.cvXdata;
         testXYflag= false;
       }
       
       else if(cvTestMode==mae.SS_MODE_YSET)
       { /* (mae.msListX) "Filter F1F2 HP-Y-set"*/
         /* get (means, std-dev, n) data to compute p-value */
         if(!hpxy.updateDataAndStat(mid))
           continue;
         /* default to max which may overide */
         cv= hpxy.cvYdata;
         testXYflag= false;
       }
       
       else if(cvTestMode==mae.SS_MODE_XORY_SETS ||
       cvTestMode==mae.SS_MODE_XANDY_SETS)
       { /* (mae.msListX, mae.msListY) "Filter F1F2 HP-X-set and HP-Y-set"*/
         /* get (means, std-dev, n) data to compute p-value */
         if(!hpxy.updateDataAndStat(mid))
           continue;
         /* default to max which may overide */
         cv1= hpxy.cvXdata;      /* for nX samples */
         cv2= hpxy.cvYdata;      /* for nY samples */
         testXYflag= true;
       }
       
       else if(mae.cvTestMode==mae.SS_MODE_ELIST)
       { /* mae.ListE "Filter F1F2 all HP-E" mult spots */
         if(nE==0)
           continue;
         testXYflag= false;
         if(maxFIELDS==1)
         { /* compute CV over all samples */
           float
             sumE= 0.0F,
             meanE,
             sumDiffSqE= 0.0F,
             varE,
             stdDevEdata;
           for(int i=0;i<nE;i++)
             sumE += ep.hpData[i];
           meanE= sumE/nE;
           for(int i=0;i<nE;i++)
           {
             g1= ep.hpData[i];
             sumDiffSqE= (g1-meanE)*(g1-meanE);
           }
           varE= sumDiffSqE/nE;      /* [TODO] sample mean (n-1)? */
           stdDevEdata= (float)Math.sqrt(varE);
           cv= stdDevEdata/meanE;    /* Use Mean of HP CVs instead of Max */
         } /* compute CV over all samples */
         else
         { /* mae.ListE "Filter F1F2 all HP-E" mult spots */
           ep.updateData(mid);
           for(int i=0;i<nE;i++)
           {
             cvI= ep.cvData[i];
             if(cvI>cv)
               cv= cvI;          /* Math.max(cv, cvI) */
             sumCV += cvI;
           }
           cv= sumCV/nE;          /* Use Mean of HP CVs instead of Max */
         } /* mae.ListE "Filter F1F2 all HP-E" mult spots */
       } /* mae.ListE "Filter F1F2 all HP-E" mult spots */
       
       else
         return(false);
       
       /* Do the test */
       if(testXYflag)
       {
         if(cvTestMode==mae.SS_MODE_XANDY_SETS)
           doubleFlag= (cv1 <= spotCVthr && cv2 <= spotCVthr);
         else
           doubleFlag= (cv1 <= spotCVthr || cv2 <= spotCVthr);
       }
       else
         singleFlag= (cv <= spotCVthr);
       
       if(singleFlag || doubleFlag)
       { /* add it to result list */
         cvResultCL.addGene(gene);
       }
     } /* process mid */
     
     return(true);
   } /* setGeneListBySpotCV */     
    
    
   /**
    * setHP_XY_t_TestCL() - set GeneList of genes passing HP-X,HP-Y t-Test
    * with a pValue better than pValueThr.<BR>
    * Compute the means and std deviations using the f1,f2 values for each
    * sample. Put the pValue in the gene object.
    * @param t_TestResultCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param msX is the X sample with F1 and F2 duplicates
    * @param msY is the Y sample with F1 and F2 duplicates
    * @param pValueThr is the p-value threshold to use
    * @return true if succeed and results in t_TestResultsCL, else
    * false if a problem.
    * @see Gene#setGeneData
    * @see GeneList#clearNull
    * @see GeneList#addGene
    * @see MaHybridSample#getDataByGID
    * @see Statistics#calcTandPvalues
    */
   boolean setHP_XY_t_TestCL(GeneList t_TestResultCL, GeneList genesToTestCL,
                             MaHybridSample msX, MaHybridSample msY,
                             float pValueThr)
   { /* setHP_XY_t_TestCL */
     int
       mid,
       gid,
       nTest= genesToTestCL.length;
     float
       g11, g12,
       g21, g22,
       mn1, mn2, sd1, sd2,
       sumSq1, sumSq2,
       pValue;
     Gene 
       gene,
       mList[]= genesToTestCL.mList;
     
     /* [CHECK] do we add the following? Probably do NOT need
      * since MnuBarFrame menu entry is should be disabled and
      * we should never get here.
      */
     //if(cfg.maxFIELDS==1)
     //  return(false);
     
     t_TestResultCL.clearNull();
     
     /* Copy genes which have a nonzero ratio */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid; 
       gid= gene.gid;
       
       /* set data to ms.X/ms.Y*/
       g11= msX.getDataByGID(gid, mae.useRatioDataFlag, msX.DATA_F1TOT);
       g12= msX.getDataByGID(gid, mae.useRatioDataFlag, msX.DATA_F2TOT);
       g21= msY.getDataByGID(gid, mae.useRatioDataFlag, msY.DATA_F1TOT);
       g22= msY.getDataByGID(gid, mae.useRatioDataFlag, msY.DATA_F2TOT);
       
       mn1= (g11+g12)/2.0F;
       mn2= (g21+g22)/2.0F;
       
       //if(mn1==0 || mn2==0)
       //  continue;            /* [TODO] what about "Missing Spots" ? */
       
       /* Note: degrees of freedom = 1,  since 2 samples */
       sumSq1= (mn1-g11)*(mn1-g11) + (mn1-g12)*(mn1-g12);
       sumSq2= (mn2-g21)*(mn2-g21) + (mn2-g22)*(mn2-g22);
       sd1= (float)Math.sqrt(sumSq1);
       sd2= (float)Math.sqrt(sumSq2);
       
      /* Calc t and p-values given (n1,m1,s1) and (n2,m2,s2),
       * calc f, t, p, dF.
       * It computes:
       *    f - calculated f statistic
       *    t - t or t' statistic
       *    pT - t-test p-value w/NULL hypoth
       *    pF - f-test p-value w/NULL hypoth
       *    dF - degrees of freedom
       *
       * Returns false if any of the data is invalid or the beta fct fails.
       */
       boolean ok= stat.calcTandPvalues(2, 2, mn1, mn2, sd1, sd2);
       
       pValue= (float)stat.pT;
       
       if(ok && pValue<=pValueThr)
       { /* use it */
         float ratio= 0.0F;
         if(mn2>0.0F)
           ratio= mn1/mn2;
        /*
         if(mae.CONSOLE_FLAG)
         {
           float t= (float)stat.t;
           float f= (float)stat.f;
           float fStat= (float)stat.fStat;
           float dF= (float)stat.dF;
           float pT= (float)stat.pT;
           float pF= (float)stat.pF;
           fio.logMsgln("F-CTPV mid="+mid +
                        " mn12=("+Util.cvd2s(mn1,1)+
                        ","+Util.cvd2s(mn2,1)+")"+
                        " sd12=("+Util.cvd2s(sd12,1)+
                        ","+Util.cvd2s(sd2,1)+")"+
                        " t="+Util.cvd2s(t,3)+
                        " f="+Util.cvd2s(f,3)+
                        " pT="+Util.cvd2s(pT,3)+
                        " pValueThr="+Util.cvd2s(pValueThr,3)+
                        " pF="+Util.cvd2s(pF,3)+
                        " dF="+Util.cvd2s(dF,1)+
                        " fStat="+ Util.cvd2s(fStat,3));
         }
        */
  
         gene.setGeneData(ratio,mn1,mn2,pValue);
         t_TestResultCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setHP_XY_t_TestCL */  
    
    
   /**
    * setHP_XYsets_t_TestCL() - set GeneList of genes passing HP-X,HP-Y sets t-Test
    * with a pValue better than pValueThr.<BR>
    * [CHECK] how do we handle this if not in HP-XY 'set' mode?GeneList
    * @param t_TestResultCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param pValueThr is the p-value threshold to use
    * @return true if succeed and results in t_TestResultsCL, else
    * false if a problem.
    * @see Gene#setGeneData
    * @see GeneList#clearNull
    * @see GeneList#addGene
    * @see HPxyData#setupDataStruct
    * @see HPxyData#updateDataAndStat
    * @see Statistics#calcTandPvalues
    */
   boolean setHP_XYsets_t_TestCL(GeneList t_TestResultCL, GeneList genesToTestCL,
                                 float pValueThr)
   { /* setHP_XYsets_t_TestCL */
     int
       mid,
       nTest= genesToTestCL.length;
     float pValue= 0.0F;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     HPxyData hpxy= mae.cdb.hpXYdata;
     hpxy.setupDataStruct(mae.useHPxySetDataFlag);
     
     t_TestResultCL.clearNull();
     
     if(hpxy.nX<2 || hpxy.nY<2)
       return(false);
     
     /* Copy genes which pass the Sets t-test */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;   
       /* get (means, std-dev, n) data to compute p-value */
       if(!hpxy.updateDataAndStat(mid))
         continue;
       
    /* Calc t and p-values given (n1,m1,s1) and (n2,m2,s2),
     * calc f, t, p, dF.
     * It computes:
     *    f - calculated f statistic
     *    t - t or t' statistic
     *    pT - t-test p-value w/NULL hypoth
     *    pF - f-test p-value w/NULL hypoth
     *    dF - degrees of freedom
     *
     * Returns false if any of the data is invalid or the beta fct fails.
     */
       double
         mn1= (double)hpxy.mnXdata,
         mn2= (double)hpxy.mnYdata,
         sd1= (double)hpxy.stdDevXdata,
         sd2= (double)hpxy.stdDevYdata;
       boolean ok= stat.calcTandPvalues(hpxy.nX, hpxy.nY, mn1,mn2,sd1,sd2);
       
       pValue= (float)stat.pT;
       
       if(ok && (pValue<=pValueThr))
       { /* use it */
         float ratio= 0.0F;
         if(hpxy.mnYdata>0.0F)
           ratio= hpxy.mnXdata/hpxy.mnYdata;
        /*
         if(mae.CONSOLE_FLAG)
         {
           float t= (float)stat.t;
           float f= (float)stat.f;
           float fStat= (float)stat.fStat;
           float dF= (float)stat.dF;
           float pT= (float)stat.pT;
           float pF= (float)stat.pF;
           fio.logMsgln("F-CTPV mid="+mid +
                        " mn12=("+Util.cvd2s(mn1,1)+
                        ","+Util.cvd2s(mn2,1)+")"+
                        " sd12=("+Util.cvd2s(sd12,1)+
                        " (sd1,sd2)=("+Util.cvd2s(sd1,1)+
                        ","+Util.cvd2s(sd2,1)+")"+
                        " t="+Util.cvd2s(t,3)+
                        " f="+Util.cvd2s(f,3)+
                        " pT="+Util.cvd2s(pT,3)+
                        " pF="+Util.cvd2s(pF,3)+
                        " dF="+Util.cvd2s(dF,1)+
                        " fStat="+ Util.cvd2s(fStat,3));
         }
        */
         
         gene.setGeneData(ratio, hpxy.mnXdata, hpxy.mnYdata, pValue);
         t_TestResultCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setHP_XYsets_t_TestCL */      
    
   /**
    * setHP_XYsets_KS_TestCL() - set GeneList of genes passing HP-X,HP-Y sets KS-Test
    * (Kolmogorov-Smirnov) with a pValue better than pValueThr.<BR>
    * [CHECK] how do we handle this if not in HP-XY 'set' mode?GeneList
    * @param KS_TestResultCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param pValueThr is the p-value threshold to use
    * @return true if succeed and results in KS_TestResultsCL, else
    * false if a problem.
    * @see Gene#setGeneData
    * @see GeneList#clearNull
    * @see GeneList#addGene
    * @see HPxyData#setupDataStruct
    * @see Statistics#calcKStestStat
    */
   boolean setHP_XYsets_KS_TestCL(GeneList KS_TestResultCL, GeneList genesToTestCL,
                                  float pValueThr)
   { /* setHP_XYsets_KS_TestCL */
     HPxyData hpxy= mae.cdb.hpXYdata;
     int
       mid,
       s,
       nX= hpxy.nX,
       nY= hpxy.nY,
       nTest= genesToTestCL.length;
     float
       ratio,
       pValue;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     boolean ok;
     
     hpxy.setupDataStruct(mae.useHPxySetDataFlag);
     
     KS_TestResultCL.clearNull();
     
     if(nX<2 || nY<2)
       return(false);
     
     double
       dataX[]= new double[nX],
       dataY[]= new double[nY];
     
     /* Copy genes which pass the KS-test */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;  
       
       /* get (means, std-dev, n) data to compute p-value */
       if(!hpxy.updateDataAndStat(mid))
         continue;
       
       /* Copy data from float to double */
       for(s=0;s<nX;s++)
         dataX[s]= (double)hpxy.hpDataX[s];
       for(s=0;s<nY;s++)
         dataY[s]= (double)hpxy.hpDataY[s];
              
      /* Calc KS statistics given (data1[],n1) (data2[],n2),
       * It computes:
       *    ksD - D statistic
       *    pKS - KS test p-value w/NULL hypoth
       *    dFks - degrees of freedom
       *
       * Returns false if any of the data is invalid or the beta fct fails.
       */
       ok= stat.calcKStestStat(dataX, nX, dataY, nY);       
       pValue= (float)stat.pKS;
       
       if(ok && (pValue<=pValueThr))
       { /* use it */
         ratio= (hpxy.mnYdata>0.0F) ? (hpxy.mnXdata/hpxy.mnYdata) :  0.0F;
        /*
         if(mae.CONSOLE_FLAG)
         {
           float ksD= (float)stat.ksD;
           float dFks= (float)stat.dFks;
           float pKS= (float)stat.pKS;
           fio.logMsgln("F-CTPV mid="+mid +
                        " pKS="+Util.cvd2s(pKS,3)+
                        " ksD="+Util.cvd2s(ksD,3)+
                        " dFks="+Util.cvd2s(dFks,3));
         }
        */
         
         gene.setGeneData(ratio, hpxy.mnXdata, hpxy.mnYdata, pValue);
         KS_TestResultCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setHP_XYsets_KS_TestCL */ 
    
    
   /**
    * setOCL_F_TestCL() - set GeneList of genes passing F-test on current OCL
    * (Ordered Condition List) with a pValue better than pValueThr.
    * The current OCL MUST be defined else return true.
    * @param OCL_F_TestResultCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param pValueThr is the p-value threshold to use
    * @return true if succeed and results in OCL_F_TestResultsCL, else
    * false if a problem.
    * @see Gene#setGeneData
    * @see GeneList#clearNull
    * @see GeneList#addGene
    * @see Condition#getSampleNamesInCondList
    * @see Condition#getConditionsInOrderedCondList    
    * @see Condition#getSamplesInCondList
    * @see Statistics#calcNCondFtestStat
    */
   boolean setOCL_F_TestCL(GeneList OCL_F_TestResultCL,
                                  GeneList genesToTestCL,
                                  float pValueThr)
   { /* setOCL_F_TestCL */
     Condition cdList= mae.cdList;
     MJAsample mjaSample= mae.mja.mjaSample;
     HPxyData hpxy= mae.cdb.hpXYdata;
     int
       mid,
       nTest= genesToTestCL.length;
     float pValue= 0.0F;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
  
     /* [1] Get lists of samples from the current Ordered Condition List */
     int curOCLidx= cdList.curOCLidx;
     if(cdList.curOCLidx==-1)
       return(false);    /* There is no current OCL */
     
     /* [1.1] Get the list of conditions in the OCL */
     Condition 
       cd,               /* working condition */
       ocl[]= cdList.orderedCondList[cdList.curOCLidx];
     int
       nConditions= cdList.nOrderedCondList[curOCLidx],
       nCondData[]= new int[nConditions]; /* # of samples in each condition */ 
       
     /* [1.2] Get lists of samples from the current Ordered Condition List.
      * Current set of sample indexes for samples in the Conditions in the
      * Ordered Condition List to test. [nConditions][nsamples per condition]
      */
     int  
       allSamplesIndex[][]= new int[nConditions][],
       nCondSamples,        /* # of samples/condition */
       nSamples,            /* working # of samples */ 
       samplesIndex[],      /* Working sample indexes in working condition */  
       sampleNbrList[];     /* working list of samples/condition */ 
     MaHybridSample msList[];
     
     /* Working current set of data for gene mid for samples in the 
      * Conditions in the Ordered Condition List to test.
      * [nConditions][nsamples per condition]
      */
     float condData[][]= new float[nConditions][];
     
     for(int c=0;c<nConditions;c++)
     { /* get sample indices for all conditions */
       cd= ocl[c];                /* get the working condition */
       msList= cd.getHPlist();    /* get list of samples */
       if(msList==null)
         return(false);           /* fatal error */
       nSamples= cd.nMScond;
       nCondData[c]= nSamples;
       sampleNbrList= new int[cd.nMScond];  /* make a new list */
       for(int i=1;i<=nSamples;i++)
         sampleNbrList[i-1]= MJAbase.lookupHPsampleNbr(msList[i]);
       allSamplesIndex[c]= sampleNbrList;
       condData[c]= new float[nSamples];
     } /* get sample indices for all conditions */
      
     /* Working normalized sample data for gene mid for working 
      * data [nCondSamples]
      */
     float
       dataS[]; 
     
     /* [1.3] Clear the results set */
     OCL_F_TestResultCL.clearNull();
     
     /* [2] Generate the set of genes passing the F-test on OCL data. */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;  
       
       /* [2.1] Get the condData sample data[c][nCondData[c]] for
        * conditions c in [0:nCondition-1] for the mid and the
        * current normalization and ratio mode.
        */
       for(int c=0;c<nConditions;c++)
       { /* get the data for each set of samples in condition c */
         samplesIndex= allSamplesIndex[c];
         /* Build the working set of condition data for gene mid.
         * Lookup the data for samples in the list and save it in
         * the precomputed condData[] array of 1D vectors.
         */
         mjaSample.getSamplesDataVector(condData[c], samplesIndex, mid);
       } /* get the data for each set of samples in condition c */
           
       /* [2.2] Compute the F-test on the OCL data computes:
        *    pFnConds - p value
        *    fStatNconds - f statistic
        *    mnSqWithin - mean within class variance
        *    mnSqBetween - mean between class variance
        *    dfWithin - degrees of freedom Within conditions
        *    dfBetween - degrees of freedom Between conditions
        *
        * Returns false if any of the data is invalid or the beta fct fails.
        */
       boolean ok= stat.calcNCondFtestStat(condData, nCondData, nConditions);
    
      /* [3] Test the p-value computed in the F-test against the pValue 
       * slider threshold. This tests the Null-hypothesis that
       * there is no difference between the means as there is 
       * significant different variance between the conditions
       */    
       pValue= (float)stat.pFnConds;
       
       if(ok && (pValue<=pValueThr))
       { /* use it */
         /* get (means, std-dev, n) data to compute p-value */
         if(!hpxy.updateDataAndStat(mid))
           continue;
         float ratio= 0.0F;
         if(hpxy.mnYdata>0.0F)
           ratio= hpxy.mnXdata/hpxy.mnYdata;
         /*
         if(mae.CONSOLE_FLAG)
         {
           float dfWithin= (float)stat.dfWithin;
           float dfBetween= (float)stat.dfBetween;
           float mnSqWithin= = (float)stat.mnSqWithin;
           float mnSqBetween= = (float)stat.mnSqBetween;
           float p= (float)stat.pFnConds;
           float f= (float)stat.fStatNconds;
           fio.logMsgln("F-CTPV mid="+mid +
                        " pT="+Util.cvd2s(pT,4)+
                        " f="+ Util.cvd2s(f,4)+
                        " mnSqWithin="+ Util.cvd2s(mnSqWithin,4)+
                        " mnSqBetween="+ Util.cvd2s(mnSqBetween,4)+
                        " dfWithin="+Util.cvd2s(dfWithin,1)+
                        " dfBetween="+Util.cvd2s(dfBetween,1));
          }
         */
         
         gene.setGeneData(ratio, hpxy.mnXdata, hpxy.mnYdata, pValue);
         OCL_F_TestResultCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setOCL_F_TestCL */ 
     
     
   /**
    * setHP_EclustersCL() - Filter genes by HP-E clusters LEQ clusterDist
    * @param passedCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param clusterDistThr is the cluster distance threshold to use
    * @return true if succeed and results in passedCL, else
    * false if a problem.
    */
   boolean setHP_EclustersCL(GeneList passedCL, GeneList genesToTestCL,
                             float clusterDistThr)
   { /* setHP_EclustersCL */
     GeneGeneDist  ccd= mae.ccd;
     int
       midI,
       midJ,
       nTest= genesToTestCL.length;
     
     passedCL.clearNull();
     Gene
       geneI,
       geneJ,
       mList[]= genesToTestCL.mList;
     float
       cDist,
       distIJ;
     
     /* Copy genes which have a nonzero ratio */
     for(midI=0;midI<nTest;midI++)
     { /* test each gene I */
       geneI= mList[midI];
       if(geneI==null)
         continue;           /* ignore bogus data */
       cDist= 10000000000.0F;
       
       for(midJ=0;midJ<nTest;midJ++)
         if(midI!=midJ)
         { /* test against gene J */
           geneJ= mList[midJ];
           if(geneJ==null)
             continue;           /* ignore bogus data */
           distIJ= ccd.clusterDistance(geneI, geneJ);
           if(cDist>distIJ)
             cDist= distIJ;      /* Math.min(cDist,distIJ) */
         } /* test against gene J*/
       
       if(cDist <= clusterDistThr)
       { /* use it */
         passedCL.addGene(geneI);
       }
     } /* test each gene */
     
     return(true);
   } /* setHP_EclustersCL */		  
    
     
   /**
    * setDiffHP_XYCL() - Filter genes by diff(HP-X,HP-Y) GEQ diffThr.
    * @param passedCL is the gene list returned with a new set of genes
    * @param genesToTestCL list of genes to test
    * @param diffThr is the difference threshold to use
    * @return true if succeed and results in passedCL, else
    * false if a problem.
    */
   boolean setDiffHP_XYCL(GeneList passedCL, GeneList genesToTestCL,
   float diffThr)
   { /* setDiffHP_XYCL */
     MaHybridSample
       msX= mae.msX,
       msY= mae.msY;
     int
       mid,
       gid1,
       nTest= genesToTestCL.length,
       maxFIELDS= mae.cfg.maxFIELDS,
       type= (mae.useRatioDataFlag)
                ? mae.ms.DATA_RATIO_F1F2TOT : mae.ms.DATA_F1TOT;
     
     passedCL.clearNull();
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     float
       gX, gY,
       absDiff;
     
     /* Copy genes which have a Diff(HP-X,HP-Y) >= zdiffThr */
     for(int k=0;k<nTest;k++)
     { /* test each gene */
       gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */      
       
       mid= gene.mid;
       gid1= mae.mp.mid2gid[mid];
       
       if(maxFIELDS>1)
       { /* has replicate spots/gene */
         /* normalized (totQ - bkgrdQ) */
         gX= msX.getDataByGID(gid1, mae.useRatioDataFlag,
         msX.DATA_MEAN_F1F2TOT);
         gY= msY.getDataByGID(gid1, mae.useRatioDataFlag,
         msY.DATA_MEAN_F1F2TOT);
       }
       else
       { /* use single spot or ratio data */
         gX= msX.getDataByGID(gid1, mae.useRatioDataFlag, type);
         gY= msY.getDataByGID(gid1, mae.useRatioDataFlag, type);
       }
       absDiff= (gX-gY);
       if(absDiff<0.0F)
         absDiff= -absDiff;
       
       if(absDiff >= diffThr)
       { /* use it */
         passedCL.addGene(gene);
       }
     } /* test each gene */
     
     return(true);
   } /* setDiffHP_XYCL */

	  
   /**
    * setSIthresholdsCL() - set GeneList of Spot Iintensity thresholded range
    * where the threshold for rangeType [ST1:ST2]
    * and rangeMode is RANGE_INSIDE, RANGE_OUTSIDE.
    * All spots being tested must be within the range.
    * It tests the gray range if testGray is true, else it tests ratios.
    *<PRE>
    * Call spot intensity for spots in HPs
    *  depending on what is being compared. i.e.
    *   SS_MODE_MS         mae.ms                "Filter F1F2 current HP"
    *   SS_MODE_XY        (mae.msX, mae.msY)     "Filter F1F2 HP-X or HP-Y"
    *   SS_MODE_XORY_SET  (mae.msListX,.msListY) "Filter F1F2 HP-X-set or HP-Y-set"
    *   SS_MODE_XANDY_SET (mae.msListX,.msListY) "Filter F1F2 HP-X-set and HP-Y-set"
    *   SS_MODE_E         mae.ListE              "Filter F1F2 all HP-E"
    * </PRE>
    * This is modified by spotIntensCompareMode
    *  COMPARE_ALL        all g1, g2 values meet range
    *  COMPARE_ANY        any g1, g2 values meet range
    *  COMPARE_PRODUCT    the product of all g1, g2 values meet range
    *  COMPARE_SUM        the sum of all g1, g2 values meet range
    *
    *  COMPARE_AT_MOST    at most nOKthr of all values meet range
    *  COMPARE_AT_LEAST   at least nOKthr of all values meet range
    * @param genesToTestCL list of genes to test
    * @param resultCL is the gene list returned with a new set of genes
    * @return true if succeed and results in passedCL, else
    * false if a problem.
    * @see GeneList#clearNull
    * @see GeneList#addGene
    * @see MaHybridSample#getSpotDataStatic
    * @see #checkSpotIntensityRange
    */
   boolean setSIthresholdsCL(GeneList genesToTestCL, GeneList resultCL )
   
   { /* setSIthresholdsCL */
     if(resultCL==null)
       return(false);
     
     MaHybridSample
      ms= mae.ms,
      msX= mae.msX,
      msY= mae.msY,
      msListX[]= hps.msListX,
      msListY[]= hps.msListY,
      msListE[]= hps.msListE;
     int
       nTest=genesToTestCL.length,
       nX= hps.nHP_X,
       nY= hps.nHP_Y,
       nE= hps.nHP_E,
       nHP= hps.nHP;
     SpotData sd;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     int
       nOK1,                 /* size of ok1[] */
       nOK2,                 /* size of ok2[] */
       spotIntensTestMode= mae.spotIntensTestMode,
       maxFIELDS= mae.cfg.maxFIELDS,
       mid,
       gid1, gid2;
     float
       g1, g2,                     /* individual spot intensities */
       gProduct1,                  /* product of all spot intensities */
       gProduct2,                  /* product of all spot intensities */
       gSum1,                      /* sum of all spot intensities */
       gSum2;                      /* sum of all spot intensities */
     boolean
       ok,                         /* spot logic f(ok1,ok2,g1,g2) is ok */
       ok1[]= new boolean[nHP+1],  /* channel 1 for sample i is ok */
       ok2[]= new boolean[nHP+1];  /* channel 2 for sample i is ok */
     
     resultCL.clearNull();
     
     /* [1] Compute lists of samples passing SI range test */
     for(int k=0;k<nTest;k++)
     { /* process mid */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;  
       gid1= gene.gid;
       
       gProduct1= 1.0F;
       gProduct2= 1.0F;
       gSum1= 0.0F;
       gSum2= 0.0F;
       nOK1= 0;
       nOK2= 0;
       ok= true;               /* assume matches */
       
      /* Note: since looking at spot intensity, not ratios,
       * we need to look at the Cy3 and Cy5 channels separately.
       * The value of -1 indicates there is no separate channel
       */
       gid2= (maxFIELDS>1 || mae.useRatioDataFlag)
               ? mae.mp.gidToGangGid[gid1] : -1;
       
       if(spotIntensTestMode==mae.SS_MODE_MS)
       { /* mae.ms "Filter all current HP F1 F2 spots" */
         sd= ms.getSpotDataStatic(gid1,false);
         g1= sd.totS;
         gProduct1 *= g1;      /* compute running product and sum */
         gSum1 += g1;
         ok1[0]= checkSpotIntensityRange(g1);
         if(gid2!=-1)
         { /* if F1, F2 data, both channels must be in range else false */
           sd= ms.getSpotDataStatic(gid2,false);
           g2= sd.totS;
           gProduct1 *= g2;  /* compute running product and sum */
           gSum1 += g2;
           ok2[0]= (ok1[0] && checkSpotIntensityRange(g2));
         }
         nOK1= 1;
       } /* mae.ms "Filter all current HP F1 F2 spots" */
       
       else if(spotIntensTestMode==mae.SS_MODE_XY)
       {  /* (mae.msX, mae.msY) "Filter all HP-X or Y (F1 F2) or (Cy3 Cy5) spots" */
         /* First get HP-X data */
         sd= msX.getSpotDataStatic(gid1, false);
         g1= sd.totS;
         gProduct1 *= g1;   /* compute running product and sum */
         gSum1 +=g1;
         ok1[0]= checkSpotIntensityRange(g1);
         if(gid2!=-1)
         { /* if F1, F2 data, both channels must be in range else false */
           sd= msX.getSpotDataStatic(gid2, false);
           g2= sd.totS;
           gProduct1 *= g2;   /* compute running product and sum */
           gSum1 += g2;
           ok1[0]= (ok1[0] && checkSpotIntensityRange(g2));
         }
         nOK1= 1;
         
         /* Then get HP Y data now */
         sd= msY.getSpotDataStatic(gid1, false);
         g1= sd.totS;
         gProduct2 *= g1;      /* compute running product and sum */
         gSum2 += g1;
         ok2[0]= checkSpotIntensityRange(g1);
         if(gid2!=-1)
         { /* if F1, F2 data, both channels must be in range else false */
           sd= msY.getSpotDataStatic(gid2, false);
           g2= sd.totS;
           gProduct2 *= g2;  /* compute running product and sum */
           gSum2 += g2;
           ok2[0]= (ok2[0] && checkSpotIntensityRange(g2));
         }
         nOK2= 1;
       } /* (mae.msX, mae.msY) "Filter all HP-X&Y (F1 F2) or (Cy3 Cy5) spots" */
       
       else if(spotIntensTestMode==mae.SS_MODE_XSET)
       { /* mae.msListX "Filter all HP-X sets spots" */
         for(int i=1;i<=nX;i++)
         { /* chk HP-X set samples */
           sd= msListX[i].getSpotDataStatic(gid1, false);
           g1= sd.totS;
           gProduct1 *= g1;     /* compute running product and sum */
           gSum1 += g1;
           ok1[nOK1]= checkSpotIntensityRange(g1);
           if(gid2!=-1)
           { /* if F1, F2 data, both channels must be in range else false */
             sd= msListX[i].getSpotDataStatic(gid2, false);
             ok= checkSpotIntensityRange(sd.totS);
             g2= sd.totS;
             gProduct2 *= g2;  /* compute running product and sum */
             gSum2 += g2;
             ok1[nOK1]= ok1[nOK1] && checkSpotIntensityRange(g2);
           }
           nOK1++;
         } /* chk HP-X set samples */
       } /* mae.msListX "Filter all HP-X sets spots" */
       
       else if(spotIntensTestMode==mae.SS_MODE_YSET)
       { /* mae.msListY "Filter all HP-Y sets spots" */
         for(int i=1;i<=nY;i++)
         { /* chk HP-Y set samples */
           sd= msListY[i].getSpotDataStatic(gid1, false);
           g1= sd.totS;
           gProduct1 *= g1;     /* compute running product and sum */
           gSum1 += g1;
           ok1[nOK1]= checkSpotIntensityRange(g1);
           if(gid2!=-1)
           { /* if F1, F2 data, both channels must be in range else false */
             sd= msListY[i].getSpotDataStatic(gid2, false);
             ok= checkSpotIntensityRange(sd.totS);
             g2= sd.totS;
             gProduct2 *= g2;  /* compute running product and sum */
             gSum2 += g2;
             ok1[nOK1]= ok1[nOK1] && checkSpotIntensityRange(g2);
           }
           nOK1++;
         } /* chk HP-Y set samples */
       } /* mae.msListY "Filter all HP-Y sets spots" */
       
       else if(spotIntensTestMode==mae.SS_MODE_XORY_SETS)
       { /* mae.msListX&Y "Filter all HP-X&Y sets spots" */
         for(int i=1;i<=nX;i++)
         { /* chk HP-X samples */
           sd= msListX[i].getSpotDataStatic(gid1, false);
           g1= sd.totS;
           gProduct1 *= g1;     /* compute running product and sum */
           gSum1 += g1;
           ok1[nOK1]= checkSpotIntensityRange(g1);
           if(gid2!=-1)
           { /* if F1, F2 data, both channels must be in range else false */
             sd= msListX[i].getSpotDataStatic(gid2, false);
             ok= checkSpotIntensityRange(sd.totS);
             g2= sd.totS;
             gProduct2 *= g2;  /* compute running product and sum */
             gSum2 += g2;
             ok1[nOK1]= ok1[nOK1] && checkSpotIntensityRange(g2);
           }
           nOK1++;
         } /* chk HP-X samples */
         
         for(int i=1;i<=nY;i++)
         { /* chk HP-Y samples */
           sd= msListY[i].getSpotDataStatic(gid1,false);
           g1= sd.totS;
           gProduct1 *= g1;     /* compute running product and sum */
           gSum1 += g1;
           ok1[nOK1]= checkSpotIntensityRange(g1);
           if(gid2!=-1)
           { /* if F1, F2 data, both channels must be in range else false */
             sd= msListY[i].getSpotDataStatic(gid2,false);
             g2= sd.totS;
             gProduct1 *= g2;  /* compute running product and sum */
             gSum1 += g2;
             ok1[nOK1]= (ok2[nOK2] && checkSpotIntensityRange(g2));
           }
           nOK1++;
         } /* chk HP-YX samples */
       } /* mae.msListX&Y "Filter all HP-X&Y sets spots" */
       
       else if(spotIntensTestMode==mae.SS_MODE_XANDY_SETS)
       { /* mae.msListX&Y "Filter all HP-X&Y sets spots" */
         for(int i=1;i<=nX;i++)
         { /* chk HP-X samples */
           sd= msListX[i].getSpotDataStatic(gid1, false);
           g1= sd.totS;
           gProduct1 *= g1;     /* compute running product and sum */
           gSum1 += g1;
           ok1[nOK1]= checkSpotIntensityRange(g1);
           if(gid2!=-1)
           { /* if F1, F2 data, both channels must be in range else false */
             sd= msListX[i].getSpotDataStatic(gid2, false);
             ok= checkSpotIntensityRange(sd.totS);
             g2= sd.totS;
             gProduct2 *= g2;  /* compute running product and sum */
             gSum2 += g2;
             ok1[nOK1]= ok1[nOK1] && checkSpotIntensityRange(g2);
           }
           nOK1++;
         } /* chk HP-X samples */
         
         for(int i=1;i<=nY;i++)
         { /* chk HP-Y samples */
           sd= msListY[i].getSpotDataStatic(gid1,false);
           g1= sd.totS;
           gProduct2 *= g1;     /* compute running product and sum */
           gSum2 += g1;
           ok2[nOK2]= checkSpotIntensityRange(g1);
           if(gid2!=-1)
           { /* if F1, F2 data, both channels must be in range else false */
             sd= msListY[i].getSpotDataStatic(gid2,false);
             g2= sd.totS;
             gProduct2 *= g2;  /* compute running product and sum */
             gSum2 += g2;
             ok2[nOK2]= (ok2[nOK2] && checkSpotIntensityRange(g2));
           }
           nOK2++;
         } /* chk HP-YX samples */
       } /* mae.msListX&Y "Filter all HP-X&Y sets spots" */
       
       else if(spotIntensTestMode==mae.SS_MODE_ELIST)
       { /* mae.ListE "Filter all HP-E F1 F2 spots" */
         for(int i=1;i<=nE;i++)
         { /* chk HP-E samples */
           sd= msListE[i].getSpotDataStatic(gid1,false);
           g1= sd.totS;
           gProduct1 *= g1;   /* compute running product and sum */
           gSum1 += g1;
           ok1[nOK1]= checkSpotIntensityRange(g1);
           if(gid2!=-1)
           { /* if F1, F2 data, both channels must be in range else false */
             sd= msListE[i].getSpotDataStatic(gid2,false);
             g2= sd.totS;
             gProduct1 *= g2;  /* compute running product and sum */
             gSum1 += g2;
             ok1[nOK1]= (ok1[nOK1] && checkSpotIntensityRange(g2));
           }
           nOK1++;
         } /* chk HP-E samples */
       } /* mae.ListE "Filter all HP-E F1 F2 spots" */
       
       /* Now analyze ok1[] and ok[2] lists */
       //if(maxFIELDS==1 || !mae.useRatioDataFlag)
       //  ok= checkSpotIntensityCompareRange(nOK1, 0, ok1, null,
       //				gProduct1, gProduct2,
       //				gSum1, gSum2);
       //else
       ok= checkSpotIntensityCompareRange(nOK1, nOK2, ok1, ok2,
       gProduct1, gProduct2,
       gSum1, gSum2);
       
       /* Finally, add gene if we get through this mess!!!! */
       if(ok)
       { /* add it to result list */
         resultCL.addGene(gene);
       }
     } /* process mid */
     
     return(true);
   } /* setSIthresholdsCL */   
      
       
   /**
    * checkSpotIntensityRange() - test spot intensity in [ST1:ST2] for specified rangeMode
    * RANGE_INSIDE, RANGE_OUTSIDE.
    * @param g1 value to test
    * @return true if pass the range test
    */
   boolean checkSpotIntensityRange(float g1)
   { /* checkSpotIntensityRange */
     boolean flag= false;
     
     switch(mae.spotIntensRangeMode)
     {
       case MAExplorer.RANGE_INSIDE:
         flag= (g1>=mae.sit1 && g1<=mae.sit2);
         break;
         
       case MAExplorer.RANGE_OUTSIDE:
         flag= (g1<mae.sit1 || g1>mae.sit2);
         break;
         
       default:
         return(false);
     }
     
     return(flag);
   } /* checkSpotIntensityRange */
    
          
   /**
    * checkSpotIntensityCompareRange() - for testing spot intensity in [ST1:ST2] with compareMode
    * COMPARE_ALL, COMPARE_ANY, COMPARE_PRODUCT, COMPARE_SUM.
    *<BR>
    * [CHECK] check logic for all combinations of rangeMode and
    * spotIntensTestMode.
    * @param nOK1 is the size of variable 2 lists
    * @param nOK2 is the size of variable 2 lists
    * @param ok1 is list of flagged variable for variable 1 list
    * @param ok2 is list of flagged variable for variable 2 list
    * @param gProduct1 is product of variables for variable 1 list
    * @param gProduct2 is product of variables for variable 2 list
    * @param gSum1 is sum of variables for variable 1 list
    * @param gSum2 is sum of variables for variable 2 list
    * @return true if succeed in the test.
    * @see #checkSpotIntensityRange
    */
   boolean checkSpotIntensityCompareRange(int nOK1, int nOK2,
                                          boolean ok1[], boolean ok2[],
                                          float gProduct1, float gProduct2,
                                          float gSum1, float gSum2)
   { /* checkSpotIntensityCompareRange */
     boolean flag= false;
     int
       nPassed1,
       nPassed2;
     float
       pctPassed1,
       pctPassed2;
     
     switch(mae.spotIntensCompareMode)
     {
       case MAExplorer.COMPARE_ALL:
         flag= true;
         for(int i=0;i<nOK1;i++)
           if(!ok1[i])
           {
             flag= false;    /* failed ALL test */
             break;
           }
         if(flag && ok2!=null)
           for(int i=0;i<nOK2;i++)
             if(!ok2[i])
             {
               flag= false;  /* failed ALL test */
               break;
             }
         break;
         
       case MAExplorer.COMPARE_ANY:
         flag= false;
         for(int i=0;i<nOK1;i++)
           if(ok1[i])
           {
             flag= true;    /* passed ANY test */
             break;
           }
         if(!flag && ok2!=null)
           for(int i=0;i<nOK2;i++)
             if(ok2[i])
             {
               flag= true;  /* passed ANY test */
               break;
             }
         break;
         
       case MAExplorer.COMPARE_AT_MOST:
       case MAExplorer.COMPARE_AT_LEAST:
         flag= false;
         nPassed1= 0;
         nPassed2= 0;
         for(int i=0;i<nOK1;i++)
           if(ok1[i])
             nPassed1++;
         if(ok2!=null)
         {
           for(int i=0;i<nOK2;i++)
             if(ok2[i])
               nPassed2++;
         }
         
         pctPassed1= (100.0F*nPassed1)/nOK1;
         pctPassed2= (nOK2>0) ? (100.0F*nPassed2)/nOK2 : 0.0F;
         
         if(mae.spotIntensCompareMode==MAExplorer.COMPARE_AT_LEAST)
         { /* AT LEAST */
           flag= (pctPassed1>=mae.cfg.pctOKthr);
           if(nOK2>0 && !flag)
             flag= (pctPassed2>=mae.cfg.pctOKthr);
         }
         else
         { /* AT MOST */
           flag= (pctPassed1<=mae.cfg.pctOKthr);
           if(nOK2>0 && !flag)
             flag= (pctPassed2<=mae.cfg.pctOKthr);
         }
         break;
         
       case MAExplorer.COMPARE_PRODUCT:
         flag= checkSpotIntensityRange(gProduct1);
         if(nOK2>0 && !flag)
           flag= checkSpotIntensityRange(gProduct2);
         break;
         
       case MAExplorer.COMPARE_SUM:
         flag= checkSpotIntensityRange(gSum1);
         if(nOK2>0 && !flag)
           flag= checkSpotIntensityRange(gSum2);
         break;
         
       default:
         return(false);
     }
     
     return(flag);
   } /* checkSpotIntensityCompareRange */   
   
       
   /**
   * setThresholdsCL() - set GeneList of thresholded F1,F2 or HP_X,HP_Y intensity or ratio range
   * where the threshold for rangeType [T1:T2] or [R1:R2] 
   * and rangeMode is RANGE_INSIDE, RANGE_OUTSIDE.
   * If msY is not null, then use msX.F1 and msX.F2 for Y,
   * else use msX.(F1+F2)/2 and  msY.(F1+F2)/2.
   * The dataType is mae.DATA_TOT, DATA_AVG.
   * It tests the gray range if testGray is true, else it tests ratios.
   *
   * @param genesToTestCL list of genes to test   
   * @param passedCL is the gene list returned with a new set of genes
   * @param msX is the X sample with F1 and F2 duplicates
   * @param msY is the Y sample with F1 and F2 duplicates
   * @return true if succeed and results in passedCL, else
   * false if a problem.
   * @see Gene#setGeneData
   * @see GeneList#clearNull
   * @see GeneList#addGene
   * @see HPxyData#updateDataAndStat
   * @see MaHybridSample#getDataByGID
   * @see #checkSamplesRange
   */
   boolean setThresholdsCL(GeneList genesToTestCL, GeneList passedCL,
                           MaHybridSample msX, MaHybridSample msY,
   boolean testGrayFlag )
   { /* setThresholdsCL */
     if(msX==null)
       return(false);
     
     passedCL.clearNull();
     
     boolean useF1F2data= (msY==null);
     if(useF1F2data)
       msY= msX;
     int
       mid,
       gid1,
       gid2,
       nGenes= genesToTestCL.length,
       maxFIELDS= mae.cfg.maxFIELDS,
       type= (mae.useRatioDataFlag)
                ? msX.DATA_RATIO_F1F2TOT : msX.DATA_F1TOT;
     HPxyData  hpXYdata= mae.cdb.hpXYdata;  /* generic HP-X/Y set object*/
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     SpotData sd;
     float
       tot1X,
       tot2X,
       tot1Y,
       tot2Y,
       gX, gY;		            /* estimated gray values */
     
     for(int k=0; k<nGenes; k++)
     { /* get data for F1 side only */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;
       gid1= gene.gid;
       gid2= (maxFIELDS>1) ? mae.mp.gidToGangGid[gid1] : -1;
       
       /* Get data for msx */
       if(mae.useHPxySetDataFlag)
       { /* set data to H.P. msX/msY */
         if(!hpXYdata.updateDataAndStat(mid))
           continue;
         else
         { /* only use genes within range constraints*/
           gX= hpXYdata.mnXdata;
           gY= hpXYdata.mnYdata;
         }
       }
       else if(maxFIELDS>1)
       { /* has replicate spots/gene */
         /* normalized (totQ - bkgrdQ) */
         tot1X= msX.getDataByGID(gid1, mae.useRatioDataFlag,msX.DATA_F1TOT);
         tot2X= msX.getDataByGID(gid1, mae.useRatioDataFlag,msY.DATA_F2TOT);
         if(useF1F2data)
         { /* multiple spots in same sample */
           gX= tot1X;
           gY= tot2X;
         }
         else
         { /* separate samples */
           tot1Y= msY.getDataByGID(gid1, mae.useRatioDataFlag,msY.DATA_F1TOT);
           tot2Y= msY.getDataByGID(gid1, mae.useRatioDataFlag,msY.DATA_F2TOT);
           gX= (tot1X + tot2X)/2;
           gY= (tot1Y + tot2Y)/2;
         }
       }
       else
       { /* use single spot or ratio data */
         gX= msX.getDataByGID(gid1, mae.useRatioDataFlag,type);
         gY= msY.getDataByGID(gid1, mae.useRatioDataFlag,type);
       }
       
       /* clip to stay out of trouble */
       if(gX<0)
         gX= 0;                    /* Math.max(0,gX) */
       if(gY<0)
         gY= 0;                    /* Math.max(0,gY) */
       
       boolean ok= checkSamplesRange(gX, gY, !testGrayFlag);
       
       if(ok)
       { /* only return spots within range constraints*/
         float ratio= (gX>0) ? ((float)gX)/((float)gY) : 0.0F;
         
         /* force it into position so can use with graphics */
         gene.setGeneData(ratio, (float)gX, (float)gY);
         passedCL.addGene(gene);
       } /* only return spots within range constraints*/
     } /* get data for F1 side only */
     
     return(true);
   } /* setThresholdsCL */
   
                      
  /**
   * setCy3Cy5RatiosCL() - Set GeneList of thresholded genes for Cy3/Cy5 ratio range 
   * where the threshold for rangeType [CR1:CR2] and rangeMode
   * RANGE_INSIDE, RANGE_OUTSIDE.
   * Use msX.F1 (Cy3) and msX.F2 (Cy5).
   * We must be in Ratio mode.
   * @param genesToTestCL list of genes to test   
   * @param passedCL is the gene list returned with a new set of genes
   * @param msX is the X sample with F1 and F2 duplicates
   * @param useCy5Cy3flag if swapped Cy3 and Cy5 data
   * @return true if succeed and results in passedCL, else
   * false if a problem.
   * @see GeneList#clearNull
   * @see GeneList#addGene
   * @see MaHybridSample#getDataByGID
   * @see #checkCy3Cy5SamplesRange
   */
   boolean setCy3Cy5RatiosCL(GeneList genesToTestCL, GeneList passedCL,
                             MaHybridSample msX, boolean useCy5Cy3flag)
   { /* setCy3Cy5RatiosCL */
     if(!mae.useRatioDataFlag)
       return(false);
     
     passedCL.clearNull();
     
     int
       mid,
       gid1,
       nGenes= genesToTestCL.length;
     Gene
       gene,
       mList[]= genesToTestCL.mList;
     float
       tmpF,
       cy3X,
       cy5X;
     
     for(int k=0; k<nGenes; k++)
     { /* get data for F1 side only */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;
       gid1= gene.gid;
       cy3X= 0.0F;
       cy5X= 0.0F;
       
       /* Get data for msx */
       /* [TODO] get average Cy3 and Cy5 for HP-X 'set' */
       if(mae.useHPxySetDataFlag)
       { /* get mean Cy3 and Cy5 in HP-X 'set' */
         MaHybridSample msList[]= hps.msListX;
         int nHP= hps.nHP_X;
         for(int i= 1;i<=nHP;i++)
         { /* compute sum across arrays */
           cy3X += msList[i].getDataByGID(gid1, false, msX.DATA_F1TOT);
           cy5X += msList[i].getDataByGID(gid1, false, msX.DATA_F2TOT);
         }
         cy3X /= nHP;       /* compute averages */
         cy5X /= nHP;
       } /* get mean Cy3 and Cy5 in HP-X 'set' */
       
       else
       { /* get Cy3, Cy5 from single HP-X sample */
         /* normalized (totQ - bkgrdQ) */
         cy3X= msX.getDataByGID(gid1, false, msX.DATA_F1TOT);
         cy5X= msX.getDataByGID(gid1, false, msX.DATA_F2TOT);
       }
       
       if(useCy5Cy3flag)
       { /* swap Cy3 and Cy5 so compute Cy5/Cy3 */
         tmpF= cy3X;
         cy3X= cy5X;
         cy5X= tmpF;
       }
       
       /* Test if Cy3/Cy5 inside or outside range */
       boolean ok= checkCy3Cy5SamplesRange(cy3X, cy5X);
       
       if(ok)
       { /* only return spots within range constraints*/
         // float ratio= (gX>0) ? ((float)gX)/((float)gY) : 0.0F;
         // /* force it into position so can use with graphics */
         // gene.setGeneData(ratio, (float)gX, (float)gY);
         passedCL.addGene(gene);
       } /* only return spots within range constraints*/
     } /* get data for F1 side only */
     
     return(true);
   } /* setCy3Cy5RatiosCL */
     

   /**
    * calcF1F2CV() - compute 2 sample estimate the coefficient of variation as
    * where CV is estimated as       2*|g1-g2|/(g1+g2).
    * If (g1+g2)==0, then return 0.0.
    * @param g1 value
    * @param g2 value
    * @return computed value
    */
   float calcF1F2CV(float g1, float g2)
   { /*calcF1F2CV*/
     float
       fsum= (g1+g2),
       cv;
     
     if(fsum==0.0F)
       cv= 0.0F;    /* divide by zero! */
     else
     {
       float diff= (g1-g2);
       cv= 2.0F*((diff>0) ? diff : -diff)/fsum;  /* 2.0F*Math.abs(g1-g2)/fsum) */
     }
     return(cv);
   } /*calcF1F2CV*/
        
   
  /**
   * checkSamplesRange() - for testing intensity[I1:I2] or ratio [R1:R2] for current rangeMode
   * RANGE_INSIDE, RANGE_OUTSIDE.  If doing ratio test and normalization is
   * set to Zscore, compute (g1-g2) else g1/g2.
   * @param g1 value
   * @param g2 value
   * @param testRatioFlag test ratio data[R1:R2], else intensity data against
   *        [I1:I2] range.
   * @return true if the ratio meets the range test
   */
  boolean checkSamplesRange(float g1, float g2, boolean testRatioFlag)
  { /*checkSamplesRange*/    
    double r;
    int rangeMode= (testRatioFlag)
                      ? mae.ratioRangeMode : mae.sampleIntensityRangeMode;
    
    if(testRatioFlag)
    { /* test ratio */
      if(g1==0.0F || g2==0.0F)
        return(false);
      else
      {
        r=((float)g1/(float)g2);
      }
    } /* test ratio */
    else
      r= 0.0;    /* placeholder to keep compiler happy */
    
    switch(rangeMode)
    {
      case MAExplorer.RANGE_INSIDE:
        if(! testRatioFlag &&
           (g1>=mae.t1 && g1<=mae.t2 && g2>=mae.t1 && g2<=mae.t2))
          return(true);
        
        else if(testRatioFlag && (r>=mae.r1 && r<=mae.r2))
          return(true);
        break;
        
      case MAExplorer.RANGE_OUTSIDE:
        if(! testRatioFlag &&
           ((g1<mae.t1 || g1>mae.t2) && (g2<mae.t1 || g2>mae.t2)))
          return(true);
        
        else if(testRatioFlag && (r<mae.r1 || r>mae.r2))
          return(true);
        break;
        
      default:
        return(false);
    }
    return(false);
  } /* checkSamplesRange*/
         
   
  /**
   * checkCy3Cy5SamplesRange() - test cy3/cy5 [CR1:CR2] for current rangeMode
   * RANGE_INSIDE, RANGE_OUTSIDE. If doing Zscore, compute (cy3-cy5) else cy3/cy5.
   * @param cy3 value of cy3 (F1) channel
   * @param cy5 value of cy5 (F2) channel
   * @return true if the ratio meets the range test
   */
  boolean checkCy3Cy5SamplesRange(float cy3, float cy5)
  { /* checkCy3Cy5SamplesRange */
    float r;
    if(mae.isZscoreFlag)
    {
      r= (cy3-cy5);
    }
    else
    {
      if(cy5==0.0)
        return(false);
      r= (cy3/cy5);
    }
    
    switch(mae.ratioCy3Cy5RangeMode)
    {
      case MAExplorer.RANGE_INSIDE:
        if(r>=mae.cr1 && r<=mae.cr2)
          return(true);
        break;
        
      case MAExplorer.RANGE_OUTSIDE:
        if(r<mae.cr1 || r>mae.cr2)
          return(true);
        break;
        
      default:
        return(false);
    }
    
    return(false);
  } /* checkCy3Cy5SamplesRange*/

   
  /**
   * isGIDinWorkingCL() - return true if GridCoords gid is in the workingCL of genes
   * @param gid is Gene Index GID to test
   * @return true if gid is in working gene set
   * @see GeneList#isGIDinGeneList
   */
  boolean isGIDinWorkingCL(int gid)
  { /* isGIDinWorkingCL */
    if(geneInWorkingGeneList==null)
      return(workingCL.isGIDinGeneList(gid));
    else return(geneInWorkingGeneList[mae.mp.gid2mid[gid]]);  /* shortcut */
  } /* isGIDinWorkingCL */
  
  
  /**
   * isMIDinWorkingCL() - return true if Master Gene Id (mid) is in workingCL
   * @param mid is Master Gene Index MID to test
   * @return true if mid is in working gene set
   * @see GeneList#isMIDinGeneList
   */
  boolean isMIDinWorkingCL(int mid)
  { /* isMIDinWorkingCL */
    if(geneInWorkingGeneList==null)
      return(workingCL.isMIDinGeneList(mid));
    else return(geneInWorkingGeneList[mid]);               /* shortcut */
  } /* isMIDinWorkingCL */


} /* End of class Filter */
