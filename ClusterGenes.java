/** File: ClusterGenes.java */

import java.awt.*;
import java.awt.Color;

/**
 * Class to generate gene clusters from working data Filtered gene list.
 * This class represents an individual ClusterGenes entry. The data structure
 * is shared between the different clustering methods. Only one cluster
 * method may be active at any one time.
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.12 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Gene
 * @see GeneList
 * @see HierClustNode
 */

class ClusterGenes
{
    /*
     *<PRE>
     * [TODO] get rid of these static variables if possible!!!
     *
     * 1. [CHECK] currently use static variables. However, since only
     *    one cluster method is allowed at a time, why must it be static?
     * 2. [TODO] Do we keep norm[] in Gene.normalizeData[] or in cgi.xxx[]?
     * 3. [TODO] Need to modify so common variable vectors are based in
     *    Gene.spotFilter[np][gid]. See Gene.java variables
     *    Gene.origDataV[] and Gene.normDataV[].
     * 4. [TODO] Do we make this class Runnable so we can handle the
     *    "STOP!" button as a separate thread?
     *</PRE>
     */
  
  /** link to global MAExplorer instance */
  private static MAExplorer
    mae;
  /** link to global Filter instance */
  private static Filter
    fc;
  /** link to global FileIO instance */
  private static FileIO
    fio;
  /** link to global Config instance */
  private static Config
    cfg;
  /** link to global GeneGeneDist instance */
  static GeneGeneDist
    ccd;
  /** link to global GeneClass instance */
  private static GeneClass
    gct;
  
  /** active instance, GC others */
  static boolean
    activeClusterMethod;
  
  /* --- clustering methods: --- */
  /** Method: SOM - Self-Organizing-Maps, Tamayo (1999)
   * PNAS USA 96:2907-2912 */
  final static int
    METHOD_SOM_CLUSTERING= 1;
  /** Method: find most orthogonal clusters and then subset rest
   * of genes to those. K-means has N Primary Nodes
   */
  final static int
    METHOD_CLUSTER_KMEANS= 2;
  /** Method: Numerical Taxonomy, Sneath&Sokol, 1973, W.H. Freeman Co. */
  final static int
    METHOD_HIERARCHICAL_CLUSTERING= 3;
  /** Method: Numerical Taxonomy, Sneath&Sokol, 1973, W.H. Freeman Co. */
  final static int
    METHOD_SIMILAR_GENES_CLUSTERING= 4;
  /** Method: Numerical Taxonomy, Sneath&Sokol, 1973, W.H. Freeman Co. */
  final static int
    METHOD_SIMILAR_GENE_COUNTS_CLUSTERING= 5;
  
  /** bit property: cause PopupRegistry to force a update of a cluster window */
  final static int
    UPDATE= 100000000;
  
  /** Default # of SOM map nodes */
  final static int
    DEFAULT_N_SOM_NODES1= 3;
  /** Default # of SOM map nodes  */
  final static int
    DEFAULT_N_SOM_NODES2= 2;
  /** Default # of K-means nodes */
  final static int
    DEFAULT_N_KMEANS_NODES= 6;
  
  /** Default number of resampling runs */
  static int
    DEF_NBR_RESAMPLE_RUNS= 10;
  
  /** Current clustering method. Note: there may possibly be
   * multiple cluster methods if for example we used some for
   * the data filter as well as the popup cluster windows.
   */
  int
    method;
  
  /** previous clustering method. We at most remove one previous 
   * cluster method popups.
   */
  static int
    prevMethod= -1;
  
  /** max # filtered genes to do curClusterCL clustering */
  static int
    maxGenesInCurCluster;
  
  /* --- Simple clustering using STATIC method calls w/o constructor --- */
  /** ARG: current genes being clustered */
  static GeneList
    curClusterCL;
  /** current gene being clustered */
  static Gene
    curGene;
  /** Threshold distance for current gene cluster*/
  static float
    curGeneDistThr;
  
  /* --- Complex clustering using Constructor, eg. K-means, etc --- */
  /** ARG: list of genes to be clustered */
  static GeneList
    complexClusterCL;
  /** i.e size of complexClusterCL.length */
  int
    nClist;
  
  /** list of expression profile samples [1:nEPmsList] where genes are to be
   * checked normally this is  mae.msListE[1:mae.nHP_E].
   */
  MaHybridSample
    msEPlist[];
  
  /** size of geneEPvector[][0:nMSList-1] */
  int
    nEPmsList;
  
  /** EP vector for CV1 computations */
  private static float
    cv1Tmp[];
  /** EP vector for CV2 computations */
  private static float
    cv2Tmp[];
  /** EP vector for CV1 computations */
  private static float
    cv1TmpEP[];
  /** EP vector for CV2 computations */
  private static float
    cv2TmpEP[];
  
  /** normalized quantitation vector for all genes
   * of size [0:nClist-1][0:nEPmsList-1] and
   * also [0:nObj-1][0:nDataVect-1]
   */
  float
    geneEPvector[][];
  
  /* --- [TODO]
   * 1. do we keep the norm[] data in Gene.normalizeData[]?
   * 2. Modify so common variable vectors are based on
   *    Gene.spotFilter[np][gid] which means need variable
   *    count when compute distances. Take variable precision
   *    into account. See Gene.origDataV[] and Gene.normDataV[].
   * 3. Do we make this class Runnable so can use [STOP!] button
   *    in BuildGUI? ---
   */
  
  /** <PRE>
   * Gene-Gene distance matrix is a lower-diagonal ccDist[i][j]
   * that is equivalent to ccDist1D[addr1D(i,j)]
   *<PRE>
   * where:
   *     i' = (i>j) ? i : j
   *    j' = (i>j) ? j : i
   *    addr1D(i,j)= j' + i'*(i'+1)/2
   *</PRE>
   * This computes LSQ euclidean distance of the gene pattern vector
   * from another gene using the current distance metric.
   */
  float
    ccDist1D[];
  
  
  /* --- Similar genes and similar gene counts cluster data structures --- */
  /** for updateable String Cluster Report if used */
  static ShowStringPopup
    geneClustersPopup;
  
  
  /* --- Hierarchical cluster data structures --- */
  /** hierarchical clustering tree */
  static HierClustNode
    hcn;
  
  /** plot popup for ClusterGram display for hierarchical clustering */
  static ShowPlotPopup
    hierClusterGramPopup;
  
  
  /* --- [FUTURE] Self-Organizing-Maps data structures --- */
  /** [0:nClist-1] [0:nClist-1] sorted index of nearest genes*/
  int
    nearestGenesIdx[][];
  
  
  /* ---  Cluster K-means data structures  --- */
  /** max # filtered genes to do K-means clustering */
  static int
    maxGenesForKmeansClustering;
  /** max # of clusters found */
  static int
    maxNgeneClusterCnt;
  /** initial seed gene - either 1st or current Obj gene */
  Gene
    initialSeedGene;
  /** [0:nClist-1] best Kmeans node cluster in [1:maxKmeansNodes] for
   * all genes in complexClusterCL
   */
  int
    bestKmeansForGene[];
  
  /** maximum global distance seen for all distances */
  float
    maxGlobalDist;
  /** mean normalized quantitation vector for K-means
   * nodes [1:nKmeansNodes][0:nMSList-1] */
  float
    meanClusterVector[][];
  /** [1:maxKmeansNodes] distance between K-means primary clusters */
  float
    kMeansDist[];
  /** [1:maxKmeansNodes] max distance within each K-means primary clusters */
  float
    kMeansMaxDist[];
  /** [1:maxKmeansNodes] mean within-cluster distance between K-means
   * clusters */
  float
    mnWithinClusterDist[];
  /** [1:maxKmeansNodes] StdDev within-cluster distance between K-means
   * clusters */
  float
    sdWithinClusterDist[];
  /** [1:maxKmeansNodes] index of gene as K-means cluster node */
  int
    kMeansList[];
  
  /** artificial mean K-means gene list */
  GeneList
    mnClustersCL;
  /** opt # genes/K-means-cluster for nEPmsList HP-Es [nKmeansNodes] */
  int
    hpDataNbrA[];
  /** opt Mean HP-E quant data [nKmeansNodes][nEPmsList]*/
  float
    hpDataMnA[][];
  /** opt S.D. HP-E quant data [nKmeansNodes][nEPmsList] */
  float
    hpDataSDA[][];
  /** # of unique K-means nodes found */
  int
    nKmeansNodes;
  /** max # of K-means primary nodes will be set by thresholding slider */
  int
    maxKmeansNodes;
  /* optional list of EP plots if activated */
  static ShowExprProfilesPopup
    KmeansClustersExprProfPopup;
  /** string report exists if not null*/
  static ShowStringPopup
    KmeansClustersPopup;
  
   
  /**
   * ClusterGenes() - constructor to create instance of new cluster method
   * @param mae is the MAExplorer instance
   */
  ClusterGenes(MAExplorer mae)
  { /* ClusterGenes */
    this.mae= mae;
    fio= mae.fio;
    fc= mae.fc;
    cfg= mae.cfg;
    ccd= mae.ccd;
    gct= mae.gct;
    
    initData();
  } /* ClusterGenes */
  
  
  /**
   * ClusterGenes() - constructor to create instance of new cluster method
   * @param complexClusterCL is the list of genes to cluster by sample
   * @param msEPlist is the list of sample HP's to cluster in the expr vector
   * @param nEPmsList is the # HP's to cluster
   */
  ClusterGenes(GeneList complexClusterCL, MaHybridSample msEPlist[],
               int nEPmsList)
  { /* ClusterGenes */   
    this.complexClusterCL= complexClusterCL;
    this.msEPlist= msEPlist;
    this.nEPmsList= nEPmsList; 
      
  } /* ClusterGenes */
    
  
  /**
   * createClusterMethod() - create instance of new cluster method
   * @param complexClusterCL is the list of genes to cluster by sample
   * @param nbrOfClusters is the # of clusters to find for K-means
   * @param msEPlist is the list of sample HP's to cluster in the expr vector
   * @param nEPmsList is the # HP's to cluster
   * @param method is the clusterMETHOD_xxx to use
   * @param resetFlag is true if reset the data structures.
   * @return true if succeed.
   * @see MenuBarFrame#setSTOPbuttonState
   * @see #showReducedFilteredGenesMsg
   * @see #updateHierarchicalClusters
   * @see #updateKmeansClusters
   * @see #updateSOMclusters
   */
  boolean createClusterMethod(GeneList complexClusterCL,
                              int nbrOfClusters, MaHybridSample msEPlist[],
                              int nEPmsList, int method, boolean resetFlag )
  { /* createClusterMethod */    
    this.complexClusterCL= complexClusterCL;
    this.msEPlist= msEPlist;
    this.nEPmsList= nEPmsList;    
    this.method= method;
    
    /* [CHECK] Release resource (e.g., popups) from previous
     * active cluster method.
     */
    /* Remove any popups and data structures from previous cluster method
     * if it was selected. It will not remove the previous method
     * (prevMethod) if it is the same as the new method.
     */
    if(resetFlag)
      removePreviousClusterMethod(method);
    
    activeClusterMethod= true;  /* there is an active cluster method */
    
    /* Create new instance */
    if(nbrOfClusters>0)
      maxKmeansNodes= nbrOfClusters;
    else
      maxKmeansNodes= DEFAULT_N_KMEANS_NODES; /* max # of K-means nodes*/
    
    /* Make tmp EP vectors. */
    cv1Tmp= new float[nEPmsList];
    cv2Tmp= new float[nEPmsList];    
    
    /* Kill off any other clustering methods and statics */
    curClusterCL= null;
    curGene= null;
    
    /* Set default sizes and lists */
    maxGenesForKmeansClustering= mae.mp.maxGenes;
    maxGenesInCurCluster= mae.mp.maxGenes;    
    nClist= complexClusterCL.length;
    
    if((nClist > maxGenesForKmeansClustering) || (nClist<=1))
    { /* show reduced Filter */
      String
        msg2= null,
        msg3= null;
      if(nClist<=1)
      {
        msg2= "Can't cluster - need at least 2 genes passing Filter";
        msg3= "Change Filter settings and try again.";
      }
      showReducedFilteredGenesMsg(msg2,msg3, maxGenesForKmeansClustering);
      return(false);
    }
    
    /* Start clustering for the specified method */
    switch(method)
    { /* startup the specified cluster method */
      case METHOD_SIMILAR_GENES_CLUSTERING:      /* cluster similar genes */
        createSimilarGenedCluster();
        break;
        
      case METHOD_SIMILAR_GENE_COUNTS_CLUSTERING: /* cluster similar gene counts */
        createSimilarGeneCountsClusters();
        break;
        
      case METHOD_CLUSTER_KMEANS:             /* Cluster of N genes to K orthoginal clusters */
        nKmeansNodes= 0;
        updateKmeansClusters();
        break;
        
      case METHOD_HIERARCHICAL_CLUSTERING:    /* hierarchical cluster genes plot */
        mae.mbf.setSTOPbuttonState(false,true); /* enable "STOP!" button */
        updateHierarchicalClusters();
        break;
        
      case METHOD_SOM_CLUSTERING:                        /* Self Organizing Maps */
        updateSOMclusters();       /* [FUTURE] - migrate to MAEPlugin */
        break;
        
      default:
        return(false);
    }/* startup the specified cluster method */
    
    return(true);
  } /* createClusterMethod */
  
  
  /**
   * initData() - init data 
   */
  void initData()
  { /* initData */
    /* There is no active cluster method */
    activeClusterMethod= false; 
    
    prevMethod= 0;
    method= 0;
    
    /* Cluster similar genes or similar gene counts */
    geneClustersPopup= null;
    
    /* Cluster of N genes to K orthoginal clusters */
    nearestGenesIdx= null;
    bestKmeansForGene= null;
    meanClusterVector= null;
    kMeansDist= null;
    kMeansMaxDist= null;
    mnWithinClusterDist= null;
    sdWithinClusterDist= null;
    kMeansList= null;
    initialSeedGene= null;
    KmeansClustersExprProfPopup= null;
    KmeansClustersPopup= null;
    
    /* hierarchical cluster genes plot */
    hcn= null;
    hierClusterGramPopup= null;
    
    /* Self Organizing Maps */
    
    /* Shared variables */
    geneEPvector= null;
    ccDist1D= null;
    
    /* clear the Node names for all genes */
    Gene
      gene,
      mList[]= mae.mp.midStaticCL.mList;
    int maxGenes= mae.mp.midStaticCL.length;
    for(int i=0;i<maxGenes;i++)
    {
      gene= mList[i];
      if(gene!=null || (gene.properties & Gene.C_BAD_SPOT)!=0)
      {
        gene.clusterNodeNbr= 0;
        gene.nGeneClustersCnt= 0;
      }
    }    
  } /* initData */
  
  
  /**
   * removePreviousClusterMethod() - remove any popups and data structures
   * from previous cluster method if it was active. 
   * It will not remove the previous method (prevMethod) if it is the same 
   * as the new method. Note: a prevMethod value of 0 indicates that
   * there was no previous method. The activeClusterMethod flag is
   * set when the previous method is active.
   * @param method is the new method. If it is 0, then just remove
   *       the previous method. 
   * @return true if remove a previous cluster method
   */
  boolean removePreviousClusterMethod(int method)
  { /* removePreviousClusterMethod */
    
    if((!activeClusterMethod && method==0) ||       
       (activeClusterMethod && (prevMethod==method && method!=0)) )
    { /* must be different methods */
      prevMethod= method;
      return(false);        
    }
    
    boolean clearGeneListFlag= false;
    
    switch(prevMethod)
    { /* remove the specified cluster method */
      case METHOD_SIMILAR_GENES_CLUSTERING:  /* cluster similar genes */
        clearGeneListFlag= true;
        if(geneClustersPopup!=null &&
           (method==0 || method!=METHOD_SIMILAR_GENE_COUNTS_CLUSTERING))
        { /* remove the popup */
          activeClusterMethod= false;
          mae.stateScr.updateFilterScrollerUseCounter("Cluster Distance",false);
          mae.stateScr.regenerateScrollers(false);
          mae.em.clearClusterDisplayState(mae.mbf.miCLMfindSimGenesDisp);
          ShowStringPopup gcp= geneClustersPopup;
          geneClustersPopup= null;
          gcp.close(false);       /* MUST BE AT END OF REMOVE! */
        }
        mae.useSimGeneClusterDispFlag= false;
        break;
        
      case METHOD_SIMILAR_GENE_COUNTS_CLUSTERING: /* cluster similar gene counts */
        clearGeneListFlag= true;  
        if(geneClustersPopup!=null &&
           (method==0 || method!=METHOD_SIMILAR_GENES_CLUSTERING))
        { /* remove the popup */
          activeClusterMethod= false;
          mae.stateScr.updateFilterScrollerUseCounter("Cluster Distance",false);
          mae.stateScr.regenerateScrollers(false); 
          mae.em.clearClusterDisplayState(mae.mbf.miCLMsimGeneCountsDisp);
          ShowStringPopup gcp= geneClustersPopup;
          geneClustersPopup= null;
          gcp.close(false);       /* MUST BE AT END OF REMOVE! */
        }
        mae.useClusterCountsDispFlag= false;
        break;
        
      case METHOD_CLUSTER_KMEANS:             /* Cluster of N genes to K orthoginal clusters */
        nearestGenesIdx= null;
        bestKmeansForGene= null;
        meanClusterVector= null;
        kMeansDist= null;
        kMeansMaxDist= null;
        mnWithinClusterDist= null;
        sdWithinClusterDist= null;
        kMeansList= null;
        initialSeedGene= null;
        
        if(KmeansClustersExprProfPopup!=null)
          KmeansClustersExprProfPopup.close(false);
        KmeansClustersExprProfPopup= null;          
        
        mae.stateScr.updateFilterScrollerUseCounter("# of Clusters", false);
        mae.stateScr.regenerateScrollers(false);
        mae.useKmeansClusterCntsDispFlag= false;
        
        if(KmeansClustersPopup!=null)
        { /* remove the popup */
          activeClusterMethod= false;  
          mae.em.clearClusterDisplayState(mae.mbf.miCLMsimGeneCountsDisp);  
          ShowStringPopup kmcp= KmeansClustersPopup;
          KmeansClustersPopup= null;
          kmcp.close(false);       /* MUST BE AT END OF REMOVE! */ 
        }
      
        clearGeneListFlag= true;
        break;
        
      case METHOD_HIERARCHICAL_CLUSTERING:    /* hierarchical cluster genes plot */
        hcn= null;
        mae.useKmeansClusterCntsDispFlag= false;
        if(hierClusterGramPopup!=null)
        { /* remove the popup */  
          activeClusterMethod= false; 
          mae.em.clearClusterDisplayState(mae.mbf.miCLMhierClusterDisp);   
          ShowPlotPopup hcgp= hierClusterGramPopup;
          hierClusterGramPopup= null;               
          hcgp.close();          /* MUST BE AT END OF REMOVE! */ 
        }
        clearGeneListFlag=true;
        break;
        
      case METHOD_SOM_CLUSTERING:             /* Self Organizing Maps */
        clearGeneListFlag= true;
        break;
        
      default:                                /* no prevMethod was requested  */    
        activeClusterMethod= false;
        clearGeneListFlag= false;
        break;
    } /* remove the specified cluster method */
    
    /* NOTE: AFTER cleanup */  
    prevMethod= method;  
    geneEPvector= null;
    ccDist1D= null;
 
    if(clearGeneListFlag)
    { /* remove common data structures */
     /* clear the Node names for all genes */
      Gene
        gene,
        mList[]= mae.mp.midStaticCL.mList;
      int maxGenes= mae.mp.midStaticCL.length;
      for(int i=0;i<maxGenes;i++)
      {
        gene= mList[i];
        if(gene!=null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        {
          gene.clusterNodeNbr= 0;
          gene.nGeneClustersCnt= 0;
        }
      }
    } /* remove common data structures */
    
    /* Clear the cluster method flags and Cluster menu checkboxes */
    mae.repaint();
    
    return(true);
  } /* removePreviousClusterMethod */
  
  
  /**
   * createSimilarGenedCluster() - create cluster of similar genes
   */
  void createSimilarGenedCluster()
  { /* createSimilarGenedCluster *//* Setup popup text window */
    geneClustersPopup=
          new ShowStringPopup(mae,
                              "  Click on a gene in microarray image to find\n"+
                              "  clusters with similar expression profiles.\n"+
                              "\n\n\n\n\n\n\n\n\n",
                              20,41, /* max size*/
                              mae.rptFontSize,
                              "Clusters of specified gene",
                              mae.RPT_TBL_CUR_GENE_CLUSTER,
                              0, "ClustersOfSingleGene",
                              (mae.pur.CUR_GENE | mae.pur.UNIQUE),
                              "maeSimilarGenes.txt");
  } /* createSimilarGenedCluster */

  
  /**
   * createSimilarGeneCountsClusters() - create similar gene counts clusters
   */
  void createSimilarGeneCountsClusters()
  { /* createSimilarGeneCountsClusters */
    /* Setup popup text window */
    geneClustersPopup=
        new ShowStringPopup(mae,
                            "  Plotting counts of clusters for all\n"+
                            "  Filtered genes ("+
                            maxGenesInCurCluster+
                            " maximum).\n\n\n\n"+
                            "\n\n\n\n\n\n\n\n",
                            20,45, /* max size*/
                            mae.rptFontSize,
                            "Counts of all Filtered genes",
                            mae.RPT_TBL_ALL_GENES_CLUSTER,
                            0, "ClusterAllGenes",
                            (mae.pur.CUR_GENE | mae.pur.FILTER |
                            mae.pur.SLIDER | mae.pur.UNIQUE),
                            "maeClusterCounts.txt");
    /* cluster gene counts */
    int objMID= mae.cdb.objMID;
    mae.pur.prevObjMID= -1;   /* force refresh */
    if(objMID==-1)
      objMID= 0;
    mae.pur.updateCurGene(objMID, UPDATE, null);
    //mae.repaint();
  } /*createSimilarGeneCountsClusters */
  
  
  /**
   * updateHierarchicalClusters() - create or update Hierarchical clusters
   * @see EventMenu#setClusterDisplayState
   * @see GeneGeneDist#calcNormGeneVectors
   * @see HierClustNode
   * @see HierClustNode#calcHierCluster
   * @see Util#showMsg3
   * @see #chkIfAbort
   * @see #reportHierClusterTree
   */
  private void updateHierarchicalClusters()
  { /* updateHierarchicalClusters */
    boolean  normByHPXflag= true;  /* by HP data for gene in HP-X
                                     * else by max value in HP */
    int
      iHPnormIdx,            /* normalization sample, msList[] space */
      iEPnormIdx;            /* normalization sample, count from 0 not 1
     * index in dataV[0:nClist-1] space
     * corresponding to msList[1:nHP] space*/
    
    /* [1] Map to dataV[] space */
    if(mae.hps.idxRatioHP!=-1)
      iHPnormIdx= mae.hps.idxRatioHP;   /* <PARAM> overide */
    else
      iHPnormIdx= mae.curHP_X;     /* default to current HP-X */
    iEPnormIdx= Math.max(iHPnormIdx-1,0);  /* count from [0:nHP_E-1] */
    
    if((nClist+2)==mae.mp.maxGenes && mae.normHCbyRatioHPflag)
    { /* DRYROT problem! */
      Util.showMsg3(
           "Can't cluster 'All genes' with HP-X normalization - use gene subset",
                    Color.white, Color.red);
      Util.popupAlertMsg("Can't cluster 'All genes'",
                         "Can't cluster 'All genes' with HP-X normalization\n"+
                         "- use gene subset.\n",
                         4, 60);
      EventMenu.setClusterDisplayState(null,false);  /* clear cluster method */
      return;
    } /* DRYROT problem! */
    
    /* [2] Go cluster it */
    hcn= new HierClustNode(mae,
                           nClist,             /* # of objects */
                           nEPmsList,          /* create empty cluster tree */
                           mae.hierClustMode,  /* mean calc method */
                           mae.useCorrCoeffFlag, /* use CC else Eucl.dist */
                           mae.useClusterDistCacheFlag, /* need more memory
                                                         * if enabled */
                           mae.normHCbyRatioHPflag, /* norm copy of
                                                     * EPmsList data */
                           normByHPXflag, /* by HP data for gene in HP-X
                                           * else by max value in HP */
                           iEPnormIdx     /* HP to norm by */
                           );
    if(hcn.memAllocFailedFlag)
      return;   /* leave error messages in place */

    /* [3] Analyze the data - generate gene vectors from HP data*/
    geneEPvector= ccd.calcNormGeneVectors(complexClusterCL, nClist);
    
    /* [4] Compute the hierarchical clusters building tree in hcn
     * of the
     *   geneEPvector[0:nClist-1][0:nEPvList-1]
     * using:
     *   nodes hierClusters[0:(2*nObj-1)-1]
     */
    String geneNames[]= null;
    int midList[]= null;
    
    if(mae.CONSOLE_FLAG)
    { /* add gene names for debugging */
      geneNames= new String[nClist];
      midList= new int[nClist];
      for(int i= 0;i<nClist;i++)
      { /* save gene name and gene MID index in terminal nodes */
        Gene gene= complexClusterCL.mList[i];
        geneNames[i]= (gene!=null) ? gene.Gene_Name : "";
        midList[i]= (gene!=null) ? gene.mid : -1;
      }
    }
    boolean
      useCompleteClusterFlag= true,
      ok= hcn.calcHierCluster(geneEPvector,geneNames, midList,
                              mae.hierClustMode);
    
    /* [5] Generate report of hcn tree in table, dendrogram
     * or ClusterGram.
     */
    if(!ok)
    {
      Util.showMsg3("Can't do hierarchical clustering with < 2 genes or 1 HP sample",
                     Color.white, Color.red);
      Util.popupAlertMsg("Can't do hierarchical clustering",
                         "Can't do hierarchical cluster with < 2 genes or 1 HP sample.\n",
                         4, 80);
      chkIfAbort();  /* check if abort, then shut down clustering */
    }
    else
      reportHierClusterTree();
    
    /* [6] Help the Garbage Collector */
    geneEPvector= null;
    //hcn= null;
  } /* updateHierarchicalClusters */
  
  
  /**
   * updateSOMclusters() - [FUTURE] create or update Self Organizing Maps clusters.
   * [TODO] migrate SOM code into a MAEPlugin.
   * @see GeneGeneDist#calcGeneGeneDists
   * @see GeneGeneDist#calcNormGeneVectors
   * @see #calcNearestGenes
   */
  private void updateSOMclusters()
  { /* updateSOMclusters */
    maxKmeansNodes= cfg.nbrOfClustersThr;  /* update the count from scroller*/
    
    /* [1] Allocate arrays used in all clustering methods...*/
    ccDist1D= new float[1+((nClist*nClist)/2)+nClist];
    nearestGenesIdx= new int[nClist][nClist];
    
    /* [2] Analyze the data - generate gene vectors from HP data*/
    geneEPvector= ccd.calcNormGeneVectors(complexClusterCL, nClist);
    
   /* [3] Compute the gene-gene distance measure which is the
    * least square distance of the expression profiles with equal
    * weighting for all HPs.
    */
    ccd.calcGeneGeneDists(ccDist1D,nClist,nEPmsList);
    
    /* [4] Cluster of N orthoginal NODES */
    calcNearestGenes();
    
    /* [5] [TODO] compute the S.O.M. clusters */
    
    /* [6] [TODO] report this...*/
    
    /* [7] Help the Garbage Collector */
    geneEPvector= null;
    ccDist1D= null;
    nearestGenesIdx= null;
  } /* updateSOMclusters */
  
  
  /**
   * updateKmeansClusters() - update an existing Kmeans cluster window
   * @see Filter#showNbrFilteredGenes
   * @see GeneGeneDist#calcNormGeneVectors
   * @see GeneGeneDist#calcGeneGeneDists
   * @see MAExplorer#repaint
   * @see Util#showMsg3
   * @see #assignGeneListToBestKmeansNodes
   * @see #calcMeanClusterVectors
   * @see #calcMedianClusterVectors
   * @see #createKmeansNodesPartition
   * @see #reassignKmeansNodes
   * @see #reportKmeansNodes
   * @see #setupKmeansHPstats
   * @see #withinClusterKmeansStatistics
   */
  void updateKmeansClusters()
  { /* updateKmeansClusters */
    /* [1] Allocate arrays used in all clustering methods...*/
    ccDist1D= new float[1+((nClist*nClist)/2)+nClist];
    mnClustersCL= null;
    
    String kMeansMsg= "Computing K-means gene clusters for "+
                      cfg.nbrOfClustersThr + " clusters and "+
                      nClist + " genes being clustered...";
    Util.showMsg3(kMeansMsg, Color.white, Color.red );

    /* [2] Analyze the data - generate gene vectors from HP data*/
    geneEPvector= ccd.calcNormGeneVectors(complexClusterCL, nClist);
    
   /* [3] Compute the gene-gene distance measure which is the
    * least square distance of the expression profiles with equal
    * weighting for all HPs.
    */
    ccd.calcGeneGeneDists(ccDist1D,nClist,nEPmsList);
    
   /* [4] Cluster of N orthoginal NODES . This will also
    * update the count maxKmeansNodes from scroller
    */
    createKmeansNodesPartition(cfg.nbrOfClustersThr);
    
   /* [5] Now assign all genes into sets by closest to
    * one of "K-means" which are orthoginal.
    */
    assignGeneListToBestKmeansNodes(false); /* for all genes */
    
   /* [6] Compute meanClusterVector for initial nodes.
    * We can use either the K-median (if flag is set) or
    * the standard K-means computations.
    * Note: K-median was first discussed by:
    *  David Bickel, Med. College of Georgia,
    *   "Robust Cluster Analysis of DNA Microarray Data:
    *    An Application of Nonparametric Correlation Dissimilarity"
    * Applied Mathematics, Statistics, Modelling and Simulation, July 2001.
    */
    if(mae.useMedianForKmeansClusteringFlag)
      calcMedianClusterVectors();
    else
      calcMeanClusterVectors();
    
    /* [7] Reassign the genes to new mean clusters*/
    reassignKmeansNodes();
    
   /* [8] Now RE-assign all genes into sets by closest to
    * one of "N primary Nodes" mean vectors - which are orthoginal.
    */
    assignGeneListToBestKmeansNodes(true); /* for all genes */
    
    /* [9] Compute mean & StdDev within cluster stats */
    withinClusterKmeansStatistics();
    
    /* [10] Compute summary Kmeans node HP statistics for each HP in
     * each Node and assign mnClustersCL to the nKmeansNodes primary nodes.
     */
    setupKmeansHPstats();
    
    /* [11] Create an expression profile report of Kmeans nodes*/
    reportKmeansNodes();
    
    /* [12] Help the Garbage Collector */
    meanClusterVector= null;
    geneEPvector= null;
    ccDist1D= null;
    nearestGenesIdx= null;
    
    /* Update Msg3 w/# genes passing filter */
    fc.showNbrFilteredGenes();
    
    mae.repaint();
  } /* updateKmeansClusters */
  
  
  /**
   * updateSimilarGenesListCounts() - this updates the cluster counts
   * for this cycle of clustering. Check all genes in the same cluster
   * and add them to the similar genes list for each gene in the cluster.
   * Do this for all clusters.
   */
  void updateSimilarGenesListCounts()
  { /* updateSimilarGenesListCounts */
    Gene
      gene,
      geneI, geneJ,
      mListP[]= fc.KmeansNodesCL.mList,
      clusterKGenes[];
    
    for(int k=0;k<nKmeansNodes;k++)
    { /* update counts for cluster k */
      gene= mListP[k];
      clusterKGenes= new Gene[gene.nGeneClustersCnt];
      int nGenesInClusterK= 0;
      
      for(int i=0;i<nClist;i++)
      { /* Build list of genes in cluster k  */
        gene= mListP[i];
        if(k!=gene.clusterNodeNbr)
          continue;  /* process each cluster separately*/
        clusterKGenes[nGenesInClusterK++]= gene;
      } /* Build list of genes in cluster k */
    } /* update counts for cluster k */
    
  } /* updateSimilarGenesListCounts */
  
  
  /**
   * addr1D() - lookup lower-diagonal addr1D(x,y) [y' + x'*(x'+1)/2]
   * where:
   *<PRE>
   *    x' = (x>y) ? x : y,
   *    y' = (x>y) ? y : x.
   *</PRE>
   * Return the computed 1D address for the 2D (x,y) address.
   */
  final private int addr1D(int x,int y)
  { /* addr1D */
    int
    xP= (y<x) ? x : y,
    yP= (y<x) ? y : x,
    idx= yP + ((xP*(xP+1))/2);  /* idx= yP + (xP*(xP+1))>>1; */
    
    return(idx);
  } /* addr1D */
   
  
  /**
   * showReducedFilteredGenesMsg() - show 'reduce the# of filtered genes' msg
   * @param optMsg2 is cluster message for msg2
   * @param optMsg3 is cluster message for msg2
   * @param maxAllowed is # of genes that can be clustered max (if a problem)
   * @see Filter#showNbrFilteredGenes
   * @see StateScrollers#regenerateScrollers
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see Util#sleepMsec
   */
  static void showReducedFilteredGenesMsg(String optMsg2, String optMsg3,
                                          int maxAllowed)
  { /* showReducedFilteredGenesMsg */
    String
      msg2= (optMsg2!=null)
              ? optMsg2
              : ("Can't cluster. Too many Filtered genes ("+
                 fc.workingCL.length+ ") to compute clusters."),
      msg3= (optMsg3!=null)
               ? optMsg3
               : ("Reduce # Filtered genes to below "+ maxAllowed+
                  " before trying to clustering gene counts again.");
    
    Util.showMsg2(msg2, Color.white, Color.red  );
    Util.showMsg3(msg3,Color.white, Color.red );
    Util.popupAlertMsg("Can't cluster. Too many Filtered genes",
                      msg2+"\n"+msg3,
                      10, 80);

    /* Clean up the GUI */
    mae.useKmeansClusterCntsDispFlag= false;
    mae.mbf.miCLMdispKmeansNodes.setState(false);
    mae.stateScr.regenerateScrollers(false);   /* regenerate */
    
    Util.sleepMsec(5000);   /* give them time to read it*/
    Util.showMsg2("");
    
    /* Update Msg3 w/# genes passing filter */
    fc.showNbrFilteredGenes();
  } /* showReducedFilteredGenesMsg */
  
  
  /**
   * calcMeanClusterClusterDist() - m,n cluster-cluster Euclidean distances.
   * @param n is cluster n
   * @param m is cluster m
   * @return distance between mean cluster vectors for clusters n and m
   */
  private float calcMeanClusterClusterDist(int n, int m)
  { /* calcMeanClusterClusterDist */
    float sumDistSq= 0.0F;
    
    for(int h=0;h<nEPmsList; h++)
    {
      float d= (meanClusterVector[n][h] - meanClusterVector[m][h]);
      sumDistSq += d*d;
    }
    float rmsDist= (float)Math.sqrt((double)(sumDistSq/nEPmsList));
    return(rmsDist);
  } /* calcMeanClusterClusterDist */
  
  
  /**
   * computeGeneToMeanClusterDistance() - gene to Mn-Cluster Euclidean distances
   * @param fk is index of gene
   * @param n is cluster n
   * @return distance between gene fk and mean cluster vector for cluster n.
   */
  private float computeGeneToMeanClusterDistance(int fk, int n)
  { /* computeGeneToMeanClusterDistance */
    float
      d,
      sumDistSq= 0.0F;
    
    for(int h=0;h<nEPmsList; h++)
    {
      d= (geneEPvector[fk][h] - meanClusterVector[n][h]);
      sumDistSq += d*d;
    }
    float rmsDist= (float)Math.sqrt((double)(sumDistSq/nEPmsList));
    return(rmsDist);
  } /* computeGeneToMeanClusterDistance */
  
  
  /**
   * sortNearestGenes() - sort nearest gene list for gene # c by min. distance.
   * The genes are sorted by minimum geneGeneDist[c][] from nearest to
   * furthest. NOTE: This is NOT a symmetric array!
   * @param c is gene number
   * @see #addr1D
   */
  private void sortNearestGenes(int c)
  { /* sortNearestGenes */
    for(int j=0;j<nClist;j++)
    { /* initialize */
      nearestGenesIdx[c][j]= j;
    }
    
    /* Sort by geneGeneDist[c][] */
    int
      nClistM1= (nClist-1),
      idxJ,
      idxJP,
      j;
    float
      ccdJ,
      ccdJP;
    
    for(int k=0;k<nClistM1;k++)
      for(j=0;j<nClistM1;j++)
      { /* bubble sort */
        idxJ= nearestGenesIdx[c][j];
        idxJP= nearestGenesIdx[c][j+1];
        ccdJ= ccDist1D[addr1D(c,idxJ)];
        ccdJP= ccDist1D[addr1D(c,idxJP)];
        
        if(ccdJ >ccdJP)
        { /* swap j and j+1 data */
          nearestGenesIdx[c][j]= idxJP;
          nearestGenesIdx[c][j+1]= idxJ;
        } /* swap j and j+1 data */
      } /* bubble sort */
  } /* sortNearestGenes */
  
  
  /**
   * calcNearestGenes() - compute rank ordered sorted list of nearest genes
   * @see #sortNearestGenes
   */
  private void calcNearestGenes()
  { /* calcNearestGenes */
    for(int cIdx=0;cIdx<nClist;cIdx++)
      sortNearestGenes(cIdx);
  } /* calcNearestGenes */
  
  
  /**
   * getSortedListOfNearestGenesIdx()- get sorted list of nearest genes[] of cIdx.
   * The list should be sorted already.
   * @param cIdx is gene index of getSortedListOfNearestGenes
   * @return list of nearest genes indices
   */
  private int[] getSortedListOfNearestGenesIdx(int cIdx)
  { /* getSortedListOfNearestGenesIdx */
    return(nearestGenesIdx[cIdx]);
  } /* getSortedListOfNearestGenesIdx */
  
  
  /**
   * findAllGeneClusterCounts() - # genes such that geneDist LEQ geneDistThr.
   * The genes being tested are in the fc.workingCL.
   * @param maE is the MAExplorer instance
   * @param clusterDistThr is threshold distance to set
   * @see Filter#showNbrFilteredGenes
   * @see MenuBarFrame#setSTOPbuttonState
   * @see Util#showMsg3
   * @see Gene#setGeneData
   * @see Gene#sortGeneList
   * @see #findClustersOfGene
   * @see #showReducedFilteredGenesMsg
   * @see #updateAllClustersGeneReport
   */
  static void findAllGeneClusterCounts(MAExplorer maE, float clusterDistThr)
  { /* findAllGeneClusterCounts */
    mae= maE;                         /* setup shortcuts */
    fio= mae.fio;
    fc= mae.fc;
    cfg= mae.cfg;
    ccd= mae.ccd;
    gct= mae.gct;
    
    maxNgeneClusterCnt= 0;
    GeneList workingCL= fc.workingCL;
    Gene
    gene,
    mList[]= workingCL.mList;
    int
      pcntDone,
      nTest= workingCL.length,
      cnt;
    
    maxGenesInCurCluster= mae.mp.maxGenes;
    
    if((nTest > maxGenesInCurCluster) || (nTest<=1))
    { /* make sure have valid # of genes to cluster */
      String
      msg2= null,
      msg3= null;
      if(nTest<=1)
      {
        msg2= "Can't cluster - need at least 2 genes passing Filter";
        msg3= "Change Filter settings and try again.";
      }
      showReducedFilteredGenesMsg(msg2,msg3,maxGenesInCurCluster);
      return;
    }
    
    mae.mbf.setSTOPbuttonState(false,true);  /* enable "STOP!" button */
    
    for(int k=0; k<nTest;k++)
    { /* find clusters for EACH gene */
      pcntDone= (100*k)/nTest;
      if((k & 03)==0)
        Util.showMsg3("Computing gene clusters: " + pcntDone + "% done.",
                      Color.white, Color.red );
      gene= mList[k];
      if(gene==null)
        continue;
      cnt= findClustersOfGene(mae,gene,clusterDistThr,false);
      gene.nGeneClustersCnt= cnt;
      maxNgeneClusterCnt= Math.max(maxNgeneClusterCnt,cnt);
      gene.setGeneData((float)cnt);
      
      if(mae.abortFlag)
      {
        chkIfAbort();  /* check if abort, then shut down clustering */
        return;
      }
    } /* find clusters for EACH gene */
    
    Gene.sortGeneList(workingCL.mList, workingCL.length, false /* DESCENDING*/);
    
    /* Update Msg3 w/# genes passing filter */
    fc.showNbrFilteredGenes();
    
    updateAllClustersGeneReport();     /* update string report */
  } /* findAllGeneClusterCounts */
  
  
  /**
   * findClustersOfGene() - find all HP-E gene clusters LT clusterDist.
   * Save results in curClusterCL.
   * Also copy curClusterCL to the E.C.L as a side effect.
   * @param maE is the MAExplorer instance
   * @param geneToTest is the gene to test
   * @param clusterDistThr is threshold distance to set
   * @param doSortFlag
   * @return the number of genes found
   * @see Gene#setGeneDist
   * @see Gene#sortGeneList
   * @see GeneList#copy
   * @see GeneGeneDist#clusterDistance
   * @see GeneList
   * @see GeneList#addGene
   * @see GeneList#copy
   */
  static int findClustersOfGene(MAExplorer maE, Gene geneToTest,
                                float clusterDistThr, boolean doSortFlag)
  { /* findClustersOfGene */
    mae= maE;                          /* setup shortcuts */
    fio= mae.fio;
    fc= mae.fc;
    cfg= mae.cfg;
    ccd= mae.ccd;
    gct= mae.gct;
    
    curGene= geneToTest;              /* save parameters */
    curGeneDistThr= clusterDistThr;   /* save so only call if different*/
    
    if(curClusterCL==null)
    { /* set up cluster report */
      curClusterCL= new GeneList(mae,mae.mp.maxGenes, "curClusterCL", true);
    }
    else
      curClusterCL.clear();
    
    GeneList sourceCL= fc.workingCL;      /* could use mae.gct.allGenesCL */
    Gene
      geneJ,
      mList[]= sourceCL.mList;
    int nTest= sourceCL.length;
    float cDist;
    
    /* Copy genes which have a nonzero ratio */
    for(int midJ=0;midJ<nTest;midJ++)
    { /* test against gene J*/
      geneJ= mList[midJ];
      if(geneJ==null || (geneJ.properties & Gene.C_BAD_SPOT)!=0)
        continue;           /* ignore bogus spots */
      
      cDist= ccd.clusterDistance(geneToTest, geneJ);
      
      if(cDist <= clusterDistThr)
      { /* use it */
        curClusterCL.addGene(geneJ);
        geneJ.setGeneDist(cDist);
        geneJ.setGeneData(cDist);
      }
    } /* test against gene J */
    
    /* Sort the curClusterCL.mList[] by minimum geneDist */
    if(doSortFlag)
      Gene.sortGeneList(curClusterCL.mList, curClusterCL.length,
                        true /* ASCENDING*/);
    
    /* Side-effect! copy cluster into E.G.L. */
    gct.editedCL.copy(gct.editedCL, curClusterCL);
    
    return(curClusterCL.length);
  } /* findClustersOfGene */
  
  
  /**
   * updateGeneClustersOfCurrentGeneReport() - update current gene cluster report
   * @see Gene
   * @see MAExplorer#logDRYROTerr
   * @see ShowStringPopup#updateText
   * @see Util#cvf2s
   * @see Util#cvtValueToStars
   */
  static void updateGeneClustersOfCurrentGeneReport()
  { /* updateGeneClustersOfCurrentGeneReport */
    if(curClusterCL==null || geneClustersPopup==null)
    {
      mae.logDRYROTerr("[CC-UCCOFCR] curClusterCL or geneClustersPopup is null.");
      return;
    }
    int
      n= 0,
      nTest= curClusterCL.length;
    Gene
      mList[]= curClusterCL.mList,
      gene;
    
    String
      s,
      sSimilarity,
      str;
    StringBuffer sBuf= new StringBuffer(60*nTest);
    float maxGeneDist= 0.0F;
    
    for(int k=0;k<nTest;k++)
    {
      gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      
      maxGeneDist= Math.max(gene.geneDist, maxGeneDist);
    }
    
    for(int k=0;k<nTest;k++)
    {
      gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      
      sSimilarity= Util.cvtValueToStars(gene.geneDist,
                                        maxGeneDist,
                                        15, /*maxStars [1:30] */
                                        true /* right fill with spaces */);
      s= ("#" + (++n)) +
          "\t" + gene.Master_ID+
          "\t"+ sSimilarity+
          "\t"+ Util.cvf2s(gene.geneDist,4) +
          "\t"+gene.MasterGeneName+ "\n";
      sBuf.append(s);
    }
    
    s= sBuf.toString();
    sBuf= null;                     /* G.C. it */
    
    String
      title=  n+" genes in cluster for gene ["+curGene.Master_ID+"] "+
              curGene.Gene_Name,
      fields= "\nNbr \t"+mae.masterIDname+"\tSimilarity\tDistance\t"+mae.masterGeneName+
              "\n----\t-------\t----------\t--------\t---------\n";
    
    str= title + fields + s;
    
    geneClustersPopup.updateText(str);
  } /* updateGeneClustersOfCurrentGeneReport */
  
  
  /**
   * updateAllClustersGeneReport() - update all filtered clusters gene report
   * @see Gene
   * @see Gene#setGeneData
   * @see Gene#sortGeneList
   * @see MAExplorer#logDRYROTerr
   * @see ShowStringPopup#updateText
   */
  static void updateAllClustersGeneReport()
  { /* updateAllClustersGeneReport */
    if(fc.workingCL==null || geneClustersPopup==null)
    {
      mae.logDRYROTerr("[CC-UACCR] null workingCL or geneClustersPopup");
      return;
    }
    GeneList useCL= (mae.clusterOnFilteredCLflag)
                      ? fc.workingCL
                      : mae.gct.allGenesCL;
    int
      cnt,
      nTest= useCL.length;
    Gene
      gene,
      mList[]= useCL.mList;
    
    StringBuffer sBuf= new StringBuffer(nTest*40);
    String
      title=  "Number of similar clusters for Filtered genes\n"+
              "There are "+nTest+" genes with at least 1 "+
              "similar gene.\n\n",
      fields= "CloneID\t# similar genes\tGene_Name\n"+
              "-------\t---------------\t---------\n";
    sBuf.append(title+fields);
    
    /* Sort genes by count count in assending order */
    for(int k=0;k<nTest;k++)
    { /* set data field in genes to gene count */
      gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      gene.setGeneData((float)gene.nGeneClustersCnt);
    }
    Gene.sortGeneList(fc.workingCL.mList, fc.workingCL.length,
                      true /* ASCENDING*/);
    
    for(int k=0;k<nTest;k++)
    { /* build string row by row of sorted genes */
      gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      cnt= gene.nGeneClustersCnt;
      if(cnt==0)
        continue;
      sBuf.append(gene.Master_ID + "\t" + "  " + cnt +
                  "\t"+ gene.MasterGeneName + "\n");
    }
    
    String str= sBuf.toString();
    sBuf= null;                 /* G.C. the buffer */
    
    /* update cluster report window */
    geneClustersPopup.updateText(str);
  } /* updateAllClustersGeneReport */
  
  
  /**
   * createKmeansNodesPartition() - setup primary K-means nodes partition.
   * First find most orthogonal clusters and then assign rest of
   * genes to these sets by picking the first gene idx (i.e. current gene)
   * for node [0].
   * Then put next furthest gene idx in [1], etc. until fill up
   * the kMeansDist[maxKmeansNodes]. The actual count is nKmeansNodes.
   * @param maxKmeansNodes is the number of K means nodes to use
   * @return true if successful.
   * @see CompositeDatabase#setObjCoordFromMID
   * @see Gene
   * @see GeneGeneDist#findGeneWithLeastSumDistances
   * @see GeneList
   * @see GeneList#clear
   * @see GeneList#addGene
   * @see Util#showMsg2
   * @see #addr1D
   */
  private boolean createKmeansNodesPartition(int maxKmeansNodes)
  { /* createKmeansNodesPartition */
    nKmeansNodes= 0;
    
    if(kMeansDist==null || this.maxKmeansNodes!=maxKmeansNodes)
    { /* define or redefine structures only if needed else reuse */
      if(maxKmeansNodes<=0 ||
      maxKmeansNodes>cfg.MAX_NBR_CLUSTERS)
        return(false);             /* there is no data to process */
      kMeansList= new int[cfg.MAX_NBR_CLUSTERS+1];
      kMeansDist= new float[cfg.MAX_NBR_CLUSTERS+1];
      kMeansMaxDist= new float[cfg.MAX_NBR_CLUSTERS+1];
      fc.KmeansNodesCL= new GeneList(mae, mae.mp.maxGenes, "KmeansNodesCL", true);
      this.maxKmeansNodes= maxKmeansNodes;
    }
    else
      fc.KmeansNodesCL.clear();  /* reuse the structures */
    
    Gene
      geneM,
      mList[]= complexClusterCL.mList;  /* source of data to cluster */
    
    /* Clear the node # and counts in the genes themselves */
    for(int m=0;m<nClist;m++)
    {
      geneM= mList[m];
      if(geneM==null)
        continue;
      geneM.clusterNodeNbr= 0;       /* save node # */
      geneM.nGeneClustersCnt= 0;
    }
    
      /* Put current gene or first gene if there is no current gene
       * into first primary node if there is no current gene.
       */
    int	curGeneIdx= 0;
    if(mae.cdb.isValidObjFlag)
    { /* use current object if in the gene list */
      Gene curObjGene= mae.mp.midStaticCL.mList[mae.cdb.objMID];
      for(int m=0;m<nClist;m++)
        if(curObjGene==mList[m])
        {
          curGeneIdx= m;
          break;
        }
    } /* use current object if in the gene list */
    else
    { /* find gene with min cluster dist to all other genes */
      curGeneIdx= ccd.findGeneWithLeastSumDistances(ccDist1D, nClist);
      /* Set GLOBAL current gene to this */
      Gene curGene= mList[curGeneIdx];
      if(curGene!=null)
        mae.cdb.setObjCoordFromMID(curGene.mid, null);
    } /* find gene with min cluster dist to all other genes */
    
    /* Push the initial gene curGeneIdx as the first node */
    initialSeedGene= mList[curGeneIdx];
    if(initialSeedGene!=null)
    {
      kMeansList[++nKmeansNodes]= curGeneIdx;  /* i.e. 1st gene is NODE 1 */
      fc.KmeansNodesCL.addGene(initialSeedGene);
      kMeansDist[nKmeansNodes]= 0.0F;
    }
    /*
    if(mae.CONSOLE_FLAG)
      fio.logMsgln("CC-CKNP.2.0 nKmeansNodes="+nKmeansNodes+
                   " kGene="+curGeneIdx+ " n="+0);
     */
    int
      kGene,
      s,
      fk,
      sk,
      pcntDone;
    float
      furthestDist,
      minDistKN,
      dSK;
    Gene gene;
    boolean fkIsKmeansNode;
    
    for(int n=1;n<maxKmeansNodes;n++)
    { /* process node n */
      kGene= -1;     /* k is best gene index of mList[k] */
      furthestDist= 0.0F;
      
      /* show progress */
      pcntDone= (100*(n-1))/maxKmeansNodes;
      Util.showMsg2("Finding Kmeans: " + pcntDone + "% done.",
                    Color.white, Color.red );
      
      /* Find furthest gene that is not in initial Kmeans list */
      for(fk=0;fk<nClist;fk++)
      { /* test if fk is furthest from any of the current Kmeans */
        gene= mList[fk];
        if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
          continue;            /* ignore bogus spots */
        fkIsKmeansNode= false;
        for(s=1;s<=nKmeansNodes;s++)
        { /* test if fk is already a Kmeans node */
          if(kMeansList[s]==fk)
          {
            fkIsKmeansNode= true;
            break;
          }
        }
        if(fkIsKmeansNode)
          continue;    /* ignore genes which are nodes */
        
        /* Look for fk which is furthest from existing nodes*/
        minDistKN= 100000000000.0F;
        for(s=1;s<=nKmeansNodes;s++)
        { /* find min dist of k to some N */
          sk= kMeansList[s];
          dSK= ccDist1D[addr1D(sk,fk)];
          if(dSK>minDistKN)
            minDistKN= dSK;   /* Math.min(dSK,minDistKN) */
        }
        
        if(minDistKN>furthestDist)
        { /* found new gene K further away form any N */
          furthestDist= minDistKN;
          kGene= fk;
        }
      } /* find furthest gene that is not in initial Kmeans list */
      
      if(kGene!=-1)
      { /* save new primary node */
        gene= mList[kGene];
        if(gene!=null)
        {
          gene.clusterNodeNbr= n; /* save node # */
          fc.KmeansNodesCL.addGene(mList[kGene]);
          kMeansList[++nKmeansNodes]= kGene;   /* note: count from 1 */
          kMeansDist[nKmeansNodes]= furthestDist;
        }
      /*
      if(mae.CONSOLE_FLAG)
        fio.logMsgln("CC-CKNP.2 nKmeansNodes="+nKmeansNodes+
                     " kGene="+kGene+ " n="+n+
                     " furthestDist="+(int)furthestDist);
       */
      } /* save new primary node */
      else
      {
        kMeansList[n]= -1;    /* does not exist */
        kMeansDist[n]= 0.0F;  /* does not exist */
      }
    } /* process node n */
    
    Util.showMsg2("");
    
    /*if(mae.CONSOLE_FLAG)
        fio.logMsgln("CC-CKNP.3 finished. nKmeansNodes="+nKmeansNodes);
     */
    
    return(true);
  } /* createKmeansNodesPartition */
  
  
  /**
   * reassignKmeansNodes() - reassign nodes based on closest to new mean clusters.
   * The actual count is nKmeansNodes.
   * @return true if successful.
   * @see #computeGeneToMeanClusterDistance
   */
  private boolean reassignKmeansNodes()
  { /* reassignKmeansNodesn */
    fc.KmeansNodesCL.clear();
    
    Gene
      gene,
      mList[]= complexClusterCL.mList;  /* source of data to cluster */
    
    /* Clear the node # and counts in the genes themselves */
    for(int m=0;m<nClist;m++)
    {
      gene= mList[m];
      if(gene==null)
        continue;
      gene.clusterNodeNbr= 0;       /* save node # */
      gene.nGeneClustersCnt= 0;
    }
    
    /* Find furthest gene that is not in initial Kmeans list */
    int bestFK[]= new int[nKmeansNodes+1];
    float minDistK[]= new float[nKmeansNodes+1];
    
    for(int n=1;n<=nKmeansNodes;n++)
    { /* init */
      bestFK[n]= -1;
      minDistK[n]= 100000000000.0F;
    }
    
    int n;
    float distKN;
    
    for(int fk=0;fk<nClist;fk++)
    { /* find Kmeans closest to fk and put fk into that set */
      for(n=1;n<=nKmeansNodes;n++)
      { /* find best Kmeans node */
        distKN= computeGeneToMeanClusterDistance(fk, n);
        if(minDistK[n]>distKN)
        { /* found new gene K further away form any N */
          minDistK[n]= distKN;
          bestFK[n]= fk;
        }
      } /* find best Kmeans node */
    } /* find Kmeans closest to fk and put fk into that set */
    
    for(n=1;n<=nKmeansNodes;n++)
    { /* assign it */
      kMeansList[n]= bestFK[n];
      kMeansDist[n]= minDistK[n];
      gene= mList[bestFK[n]];
      if(gene!=null)
        gene.clusterNodeNbr= n;    /* save node # */
    /*
    if(mae.CONSOLE_FLAG)
      fio.logMsgln("CC-RAPNP kMeansList["+n+"]="+kMeansList[n]+
                   " kMeansDist["+n+"]="+(int)kMeansDist[n]);
     */
    }
    
    return(true);
  } /* reassignKmeansNodes */
  
  
  /**
   * calcMeanClusterVectors() - compute meanClusterVector Kmeans nodes.
   * Note: the mean vectors are saved in the global variable
   * meanClusterVecto[maxKmeansNodes+1][nEPmsList]
   */
  private void calcMeanClusterVectors()
  { /*  calcMeanClusterVectors */
    int
      bestN,
      n,
      h,
      sk,
      nGenesInCluster;
    Gene geneN;
    
    meanClusterVector= new float[maxKmeansNodes+1][nEPmsList]; /* zero data*/
    
    for(int fk=0;fk<nClist;fk++)
    { /* sum data for each cluster bestN[fk] */
      bestN= bestKmeansForGene[fk];
      for(h=0;h<nEPmsList; h++)
      { /* sum vector elements h for bestN cluster */
        meanClusterVector[bestN][h] += geneEPvector[fk][h];
      }
    } /* sum data for each cluster bestN[fk] */
    
    for(n=1;n<=nKmeansNodes;n++)
    {
      sk= kMeansList[n];
      geneN= complexClusterCL.mList[sk];
      if(geneN==null)
        continue;
      nGenesInCluster= geneN.nGeneClustersCnt;
      
      for(h=0;h<nEPmsList; h++)
      { /* estimate mean vector element h for cluster n */
        meanClusterVector[n][h] /= nGenesInCluster;
      }
      
    /*
    if(mae.CONSOLE_FLAG)
      {
        String s= "K-means Cluster vector["+n+",#"+nGenesInCluster+"]=";
        for(int h=0;h<nMSlist; h++)
           s += (int)meanClusterVector[n][h]+" ";
        fio.logMsgln(s);
      }
     */
    }
  } /*  calcMeanClusterVectors */
  
  
  /**
   * calcMedianClusterVectors() - compute meanClusterVector as K-median nodes.
   * Note: the median vectors are saved in the global variable
   *   meanClusterVecto[maxKmeansNodes+1][nEPmsList]
   * @see Statistics#calcHistStats
   */
  private void calcMedianClusterVectors()
  { /*  calcMedianClusterVectors */
    int
      nBins= 10000,
      nBinsRTN,
      hIdx,
      nSum= 0,
      bestN,
      n,
      h,
      sk,
      nGenesInCluster,
      hist[]= new int[nBins+1];
    float
      ri,
      minDataH,
      maxDataH,
      data[]= new float[nClist+1];
    double sumRI;
    Gene geneN;
    
    meanClusterVector= new float[maxKmeansNodes+1][nEPmsList]; /* zero data*/
    
    /* [1] Compute median vector for cluster n */
    for(n=1;n<=nKmeansNodes;n++)
    { /* Compute median vector for cluster n */
      minDataH= 100000000.0F;
      maxDataH= -100000000.0F;
      sumRI= 0.0F;
      for(int i=0;i<nBins;i++)
        hist[i]= 0;               /* clear data */

     /* [2] For each sample  for genes in cluster n - compute
      * the median value
      */
      for(h=0;h<nEPmsList; h++)
      { /* compute median of element h for cluster n */
        nSum= 0;            /* # genes in this cluster */
        
        /* [2.1] extract data[] for element h for cluster n */
        for(int fk=0;fk<nClist;fk++)
        { /* sum data for each cluster bestN[fk] */
          bestN= bestKmeansForGene[fk];
          if(n!=bestN)
            continue;  /* only use genes in cluster n */
          data[nSum]= geneEPvector[fk][h];
          nSum++;
        } /* sum data for each cluster bestN[fk] */
        
        /* [2.2] compute median of data[] for element h for
         * cluster n.
         */
        if(nSum>0)
        { /* compute statistics so can compute median */
          nBinsRTN= mae.stat.calcHistStats("K-median",nBins,data, nSum,hist);
        }
        else
          nBinsRTN= 0;
        
        if(nBinsRTN==0)
        { /* trouble with median calc */
          meanClusterVector[n][h]= mae.stat.medianH;
        }
        else
          meanClusterVector[n][h]= mae.stat.medianH;
      } /* compute median of element h for cluster n */
      
    /*
    if(mae.CONSOLE_FLAG)
    {
      String s= "K-median Cluster vector["+n+",#"+nGenesInCluster+"]=";
      for(int h=0;h<nMSlist; h++)
        s += (int)meanClusterVector[n][h]+" ";
      fio.logMsgln(s);
    }
     */
    } /* Compute median vector for cluster n */
  } /* calcMedianClusterVectors */
  
  
  /**
   * assignGeneListToBestKmeansNodes() - assign genes for all complexClusterCL
   * genes to the best N primary Nodes clusters.
   * @param useMeanClusterVectors to use mean cluster vectors else
   *        vector of current centroid gene.
   * @see #calcMeanClusterClusterDist
   * @see Gene#clearGeneProperty
   * @see Gene#isGeneProperty
   * @see Gene#setGeneProperty
   * @see #addr1D
   * @see #computeGeneToMeanClusterDistance
   */
  private void assignGeneListToBestKmeansNodes(boolean useMeanClusterVectors)
  { /* assignGeneListToBestKmeansNodes */
    /* [1] Do any (re)allocation required */
    bestKmeansForGene= new int[nClist]; /* for assigning best node to each gene */
    
   /* [2] Clear all cluster counts. Only nodes can have
    * non-zero counts.
    */
    Gene geneK;
    for(int fk=0;fk<nClist;fk++)
    { /* zero the genes cluster counts */
      geneK= complexClusterCL.mList[fk];
      if(geneK==null)
        continue;
      geneK.nGeneClustersCnt= 0;
      geneK.clearGeneProperty(Gene.C_IS_KMEANS);
    }
    
    /* [3] Set cluster counts of Kmeans nodes to 1 */
    int
      m,
      sk,
      sm;
    Gene geneN;
    float distKtoN;
    
    for(int n=1;n<=nKmeansNodes;n++)
    { /* set counts in primary Nodes to 1 and other data*/
      sk= kMeansList[n];
      geneN= complexClusterCL.mList[sk];
      if(geneN==null)
        continue;
      geneN.nGeneClustersCnt= 1;       /* ++ for assigned genes*/
      geneN.clusterNodeNbr= n;         /* save node # to itself */
      geneN.data= kMeansDist[n];       /* dist to nearest Kmeans */
      bestKmeansForGene[sk]= n;        /* point to itself! */
      geneN.setGeneProperty(Gene.C_IS_KMEANS);
      
      /* Set distance to nearest other Kmeans */
      geneN.data= 100000000.0F;        /* dist to nearest Kmeans node */
      for(m=1;m<=nKmeansNodes;m++)
        if(n!=m)
        { /* find nearest dist. */
          sm= kMeansList[m];
          if(useMeanClusterVectors)
            distKtoN= calcMeanClusterClusterDist(n,m);
          else
            distKtoN= ccDist1D[addr1D(sk,sm)];
          geneN.data= Math.min(geneN.data, distKtoN);
        } /* find nearest dist. */
    } /* set counts in primary Nodes to 1 and other data*/
    
    /* [4] Find best Kmeans node for each gene fk which is not
     * a central Kmeans node.
     */
    int bestN;                    /* will set to best node */
    for(int fk=0;fk<nClist;fk++)
    { /* find best Kmeans node for each gene fk*/
      geneK= complexClusterCL.mList[fk];
      if(geneK==null)
        continue;
      distKtoN= 100000000000.0F;  /* best distance from fk to Kmeans node */
      bestN= -1;                  /* will set to best node */
      
      if(!geneK.isGeneProperty(Gene.C_IS_KMEANS))
        for(int n=1;n<=nKmeansNodes;n++)
        { /* find closest Node for all non-NODE genes */
          sk= kMeansList[n];
          float distGeneToN;
          
          if(useMeanClusterVectors)
            distGeneToN= computeGeneToMeanClusterDistance(fk,n);
          else
            distGeneToN= ccDist1D[addr1D(sk,fk)];
          
          if(distGeneToN<distKtoN)
          { /* save assignment for best node with least distance*/
            bestN= n;
            distKtoN= ccDist1D[addr1D(sk,fk)]; /* set to best one*/
            bestKmeansForGene[fk]= bestN;
            geneK.data= distKtoN;        /* save distance */
            geneK.clusterNodeNbr= bestN; /* save node # */
            geneK.nGeneClustersCnt= 0;  /* MUST be 0 which
             * implies it is not a node*/
          }
        } /* find closest Node for all non-NODE genes */
      
      if(bestN!=-1)
      { /* Update the gene cluster counter for the node */
        sk= kMeansList[bestN];
        geneN= complexClusterCL.mList[sk];
        if(geneN!=null)
          geneN.nGeneClustersCnt++;  /* count # genes in cluster */
      }
    } /* find best Kmeans node for each gene fk */
    
  } /* assignGeneListToBestKmeansNodes */
  
  
  /**
   * sortKmeansClusterList() - sort K-means srcCL to dstCL by Node number.
   * Generate the cluster table in dstCL from srcCL.
   * foreach n in Kmeans
   *<PRE>
   *   1. Put Kmeans node first.
   *   2. Sort members of Kmeans n clusters by distance to cluster centroid.
   *   3. Then output members of that Kmeans set.
   * Generate and ordered newCL which will then be used
   * to make the sorted Reports.
   *</PRE>
   * Note: requires that the genes in srcCL be assigned to the
   * best K-means node.
   * @param srcCL is the gene list to sort (source)
   * @param dstCL is the resulting sorted gene list (destination)
   * @see GeneList
   * @see GeneList#addGene
   * @see GeneList#clear
   * @see GeneList#clearNull
   * @see Gene#sortGeneList
   */
  void sortKmeansClusterList(GeneList srcCL,  GeneList dstCL)
  { /* sortKmeansClusterList */
    int
      nClist= srcCL.length,
      n,
      k,
      fkN;             /* index for Kmeans node */
    float maxWithinDist;
    Gene
      gene,
      geneK= null;    /* gene which is the Kmeans node */
    GeneList tmpSortCL= new GeneList(mae, nClist, "tmpSortCL",true);
    /* tmp list for sorting a K-means cluster */
    
    dstCL.clearNull();
    maxGlobalDist= 0.0F;
    
    for(n=1;n<=nKmeansNodes;n++)
    { /* process node n */
      maxWithinDist= 0.0F;           /* max dist within cluster*/
      fkN= kMeansList[n];               /* index for NODE */
      
      /* [1] Get list of genes in Kmeans[n] and put into tmpSortCL */
      tmpSortCL.clear();              /* new list each time */
      for(k=0;k<nClist;k++)
      { /* find best K-means node for each gene fk*/
        if(n!=bestKmeansForGene[k])
          continue;                 /* only genes in cluster n */
        gene= srcCL.mList[k];
        if(gene==null)
          continue;
        if(k==fkN)
          geneK= gene;	         /* save Kmeans node separately */
        else
        {
          tmpSortCL.addGene(gene); /* list of other genes*/
          maxWithinDist= Math.max(maxWithinDist,gene.data);
        }
      } /* find best Kmeans node for each gene fk*/
      
      kMeansMaxDist[n]= maxWithinDist;
      maxGlobalDist= Math.max(maxGlobalDist,maxWithinDist);
            
      /* [2] Sort gene list by distance to Kmeans node */
      Gene.sortGeneList(tmpSortCL.mList, tmpSortCL.length, true /* ascending */);
      
     /* [3] Append tmpSortCL to dstCL to make sorted list
      * for Report.
      */
      dstCL.addGene(geneK);    /* put Kmeans node first in cluster*/
      for(int j=0;j<tmpSortCL.length;j++)
      {
        gene= tmpSortCL.mList[j];
        dstCL.addGene(gene); /* add rest of genes */
      }
    } /* process node n */
  } /* sortKmeansClusterList */
  
  
  /**
   * withinClusterKmeansStatistics() - compute mean & StdDev within cluster stats
   * @return true if successful.
   */
  boolean withinClusterKmeansStatistics()
  { /* withinClusterKmeansStatistics */
    int nNodesInCluster[]= new int[(int)Config.MAX_NBR_CLUSTERS+1];
    float sumDiffSq[]= new float[(int)Config.MAX_NBR_CLUSTERS+1];
    
    mnWithinClusterDist= new float[(int)Config.MAX_NBR_CLUSTERS+1];
    sdWithinClusterDist= new float[(int)Config.MAX_NBR_CLUSTERS+1];
    
    Gene
      geneK,
      mList[]= complexClusterCL.mList;  /* source of data to cluster */
    int n;
    
    for(int fk=0;fk<nClist;fk++)
    { /* Compute sum of distances */
      n= bestKmeansForGene[fk];
      geneK= mList[fk];
      if(geneK==null)
        continue;
      nNodesInCluster[n]++;
      mnWithinClusterDist[n] += geneK.data;
    }
    
    for(n=1;n<=nKmeansNodes;n++)
    { /* compute means */
      mnWithinClusterDist[n] /= nNodesInCluster[n];
    }
    
    float diff;
    for(int fk=0;fk<nClist;fk++)
    { /* Compute sum of (distance-mean)**2 */
      n= bestKmeansForGene[fk];
      geneK= mList[fk];
      if(geneK==null)
        continue;
      diff= (mnWithinClusterDist[n] - geneK.data);
      sumDiffSq[n] += diff*diff;
    }
    
    for(n=1;n<=nKmeansNodes;n++)
    { /* compute StdDev */
      sdWithinClusterDist[n]=
         (float)Math.sqrt((double)(sumDiffSq[n]/nNodesInCluster[n]));
     /*
     if(mae.CONSOLE_FLAG)
       fio.logMsgln("CC-WCNS mnWCD["+n+"]="+ (int)mnWithinClusterDist[n]+
                    " sdWCD["+n+"]="+ (int)sdWithinClusterDist[n]);
      */
    }
    
    return(true);
  } /* withinClusterKmeansStatistics */
  
  
  /**
   * saveKmeansClustersAsGeneSets() - save K-means clusters in named Gene BitSets.
   * The named sets are "Cluster #i" where i is 1 to nKmeansNodes.
   * @return true if succeed.
   * @see GeneBitSet#addItem
   * @see GeneBitSet#clearNull
   * @see GeneBitSet#lookupOrMakeNewGeneBitSet
   */
  boolean saveKmeansClustersAsGeneSets()
  { /* saveKmeansClustersAsGeneSets */
    int
      gbsIdx,
      nB;
    GeneBitSet gbs[]= new GeneBitSet[1+nKmeansNodes];
    
    for(int k=1; k<=nKmeansNodes;k++)
    { /* make new empty Gene Bit Sets */
      gbsIdx= GeneBitSet.lookupOrMakeNewGeneBitSet(("Cluster #"+k),null);
      if(gbsIdx==-1)
        return(false);
      else
      {
        gbs[k]= GeneBitSet.userBS[gbsIdx];
        gbs[k].clearNull();
      }
    } /* make new empty Gene Bit Sets */
    
    Gene
      gene,
      mListP[]= fc.KmeansNodesCL.mList;
    
    for(int i=0;i<nClist;i++)
    { /* sort genes into proper cluster gene bit sets */
      gene= mListP[i];
      nB= gene.clusterNodeNbr;
      gbs[nB].addItem(gene.mid);	  /* add gene to nB'th gene set */
    }
    
    return(true);
  } /* saveKmeansClustersAsGeneSets */
  
  
  /**
   * reportKmeansNodes() - popup expression profile report of K-means nodes
   * @see ArrayScroller#repaint
   * @see Gene#isGeneProperty
   * @see ShowStringPopup
   * @see ShowStringPopup#updateText
   * @see Util#cvtValueToStars
   * @see #sortKmeansClusterList
   */
  private void reportKmeansNodes()
  { /* reportKmeansNodes */
    /* [1] Generate the cluster table in KmeansNodesCL.
     * foreach n in K-means
     *   1. Put K-means node first.
     *   2. Then output members of that K-means set.
     * Generate and ordered newCL which will then be used
     * to make the sorted Reports.
     */
    sortKmeansClusterList(complexClusterCL, fc.KmeansNodesCL);
    
    /* [2] Make print string from newCL */
    String
    sDistKN,
    sSimilarity,
    sMasterID,
    sGene,
    title= "Cluster report for "+nKmeansNodes+" K-means clusters",
    msg= title + " with "+ nClist+
        " genes being clustered.\n"+
        "The seed gene is ["+
        initialSeedGene.Master_ID+"] "+
        initialSeedGene.Gene_Name+ ".\n\n",
    fields=
      mae.masterIDname+"  Similarity      Cluster-#  Distance-to-cluster  Gene-Name\n"+
      "--------  --------------  ---------  -------------------  ----------------\n\n",
    sR= "";
    Gene
      gene,
      mListP[]= fc.KmeansNodesCL.mList;
    int
      sBufSize= 0,
      nB;
    float
      val,
      valMax,
      distKN,
      mnWCD,
      sdWCD,
      cvWCD;
    boolean isKmeansNode;
    StringBuffer sBuf= new StringBuffer(45*nClist);
    
    for(int i=0;i<nClist;i++)
    { /* make report string */
      gene= mListP[i];
      if(gene==null)
        continue;
      isKmeansNode= gene.isGeneProperty(Gene.C_IS_KMEANS);
      sMasterID= gene.Master_ID;
      
      nB= gene.clusterNodeNbr;
      
      distKN= gene.data;
      sDistKN= Util.cvf2s(distKN,3);
      
      /* compute similarity graphic '******'based on distance */
      //valMax= maxGlobalDist;
      valMax= kMeansMaxDist[nB];
      val= (isKmeansNode) ? 0 : distKN;
      sSimilarity= Util.cvtValueToStars(val, valMax, 15, /*maxStars [1:30] */
      true /* right fill with spaces */);
      
      /* Make geneID string be exactly 7 characters */
      int geneIDlen= sMasterID.length();
      if(geneIDlen<7)
        sMasterID += "       ".substring(1,7-geneIDlen);
      
      mnWCD= mnWithinClusterDist[nB];
      sdWCD= sdWithinClusterDist[nB];
      cvWCD= sdWCD/mnWCD;
      
      /* Put it all together in a single printable string. */
      sGene= sMasterID + "   " + sSimilarity + "  " +  nB +
             "          " +
             ((isKmeansNode)
                 ? ("Cluster [" + gene.nGeneClustersCnt +
                    " genes] in cluster [distNext: " +
                    sDistKN + "] wiCdist:mn+-sd="+
                    Util.cvf2s(mnWCD,3) + "+-"+
                    Util.cvf2s(sdWCD,3)+
                    " CV="+Util.cvf2s(cvWCD,3))
                 : sDistKN)+ "  "+gene.Gene_Name+"\n";
      
      sBuf.append(sGene);
    } /* make report string */
    
    /* Convert to concatenated string */
    sR= sBuf.toString();
    sBuf= null;                    /* G.C. it */
    
    /* [3] Now make the report */
    String sReport= msg + fields + sR;
    
    if(KmeansClustersPopup!=null)
      KmeansClustersPopup.updateText(sReport);   /* existing window */
    else
      KmeansClustersPopup=
         new ShowStringPopup(mae, sReport, 24,65, mae.rptFontSize,
                             title, mae.RPT_TBL_KMEANS_CLUSTER,
                             0, "KmeansClustersPopup",
                             (PopupRegistry.CUR_GENE | PopupRegistry.FILTER |
                             PopupRegistry.SLIDER | PopupRegistry.UNIQUE),
                             "maeKmeansClusters.txt");
    
  /* [4] Enable drawing these K-means nodes in the microarray image
   * as magenta circles.
   */
    mae.useKmeansClusterCntsDispFlag= true;
    mae.is.repaint();
  } /* reportKmeansNodes */
  
  
  /**
   * reportHierClusterTree() - popup report of hierarchical clusters tree.
   * This includes the expression profiles.
   * @see GeneList
   * @see GeneList#addGene
   * @see GeneList#clear
   * @see HierClustNode
   * @see HierClustNode#enumerateLeafNodes
   * @see HierClustNode#setEnumerateNodes
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see ShowPlotPopup
   */
  private void reportHierClusterTree()
  { /* reportHierClusterTree */
    
  /* [1] Generate the hierarchical cluster table from genes in
   * complexClusterCL and order by the hcn tree. Then save the
   * order gene list in curClusterCL.
   * Traverse the tree in right-leaf order only pushing leaf nodes
   * into the curClusterCL.
   * Generate an ordered newCL which will then be used
   * to make the sorted Reports.
   */
    if(!hcn.setEnumerateNodes())
    {
      Util.showMsg("No hierarchical clusters to report");
      return;
    }
    
    if(curClusterCL==null)
    { /* set up cluster report */
      curClusterCL= new GeneList(mae, mae.mp.maxGenes, "curClusterCL", true);
    }
    else
      curClusterCL.clear();    /* sorted output gene list */
    
    /* [2] Make print string from newCL */
    String
      sNorm= (mae.normHCbyRatioHPflag)
               ? "HP-X sample data/gene" : "HP max intensities data",
      sWtAvg= (mae.hierClustUnWtAvgFlag)
                ? "unweighted-avg" : "weighted-avg",
      sLinkage= "?";
    
    if(mae.hierClustMode==mae.HIER_CLUST_NEXT_MIN_LNKG)
      sLinkage= "next-min.";
    else if(mae.hierClustMode==mae.HIER_CLUST_PGMA_LNKG)
      sLinkage= "average-arithmetic";
    else if(mae.hierClustMode==mae.HIER_CLUST_PGMC_LNKG)
      sLinkage= "average-centroid";
    
    String title= "Hierarchical ClusterGram for "+ nClist+
                  " genes clustered - normalized by "+
                  sNorm + ", " + sLinkage+"-linkage " + sWtAvg;
    Gene
      mListP[]= complexClusterCL.mList,  /* source of data to cluster */
      gene;
    HierClustNode hcnNode;
    int
      nObj= HierClustNode.nObj,
      pcntDone,
      n= 0,
      nodeID;
    
    while(true)
    { /* Build ordered gene list */
      if((n&07)==0)
      {
        pcntDone= (100*n)/nObj;
        Util.showMsg2("Building Hier-Cluster Report: " +
                      pcntDone + "% done.", Color.white, Color.red );
      }
      hcnNode= hcn.enumerateLeafNodes();
      if(hcnNode==null)
      { /* test if done */
        break;
      }
      
      nodeID= hcnNode.nodeID;
      gene= null;
      
      if(hcnNode.cIdx!=-1)
      { /* valid gene at terminal node */
        gene= mListP[hcnNode.cIdx];
        curClusterCL.addGene(gene);
        n++;                      /* count all valid rows */
      }
      else
        continue;
    } /*  Build ordered gene list */
   /*
   if(mae.CONSOLE_FLAG)
     fio.logMsgln("CC-RHCT.done n="+n+ " hcn.(nObj="+hcn.nObj+
                  " enumCnt="+hcn.enumCnt+
                  " nHierClusters="+hcn.nHierClusters+")");
    */
    Util.showMsg2("");
    
  /* [3] Popup the Dendrogram or ClusterGram.
   * The hierarchical cluster is in the curClusterCL GeneList.
   */
    hierClusterGramPopup= new ShowPlotPopup(mae, mae.PLOT_CLUSTERGRAM,
                                            title, "HierClustersPopup",
                                            (PopupRegistry.CUR_GENE |
                                            // PopupRegistry.FILTER |
                                            PopupRegistry.UNIQUE),
                                            0 /* frame offset in bits*/);
  } /* reportHierClusterTree */
  
  
  /**
   * copyCurClusterToEditedGeneList() - set EGL to K-means curCluster genes
   * for the most similar genes.
   * @return true if succeed
   * @see GeneList#addGene
   * @see GeneList#clear
   */
  static boolean copyCurClusterToEditedGeneList()
  { /* copyCurClusterToEditedGeneList */
    if(!mae.useKmeansClusterCntsDispFlag || !mae.cdb.isValidObjFlag)
      return(false);
    
    Gene
      geneOBJ= mae.mp.midStaticCL.mList[mae.cdb.objMID],
      mListP[]= fc.KmeansNodesCL.mList,
      gene;
    if(geneOBJ==null)
      return(false);        /* No gene selected */
    int
      nClist= fc.KmeansNodesCL.length,
      cNbrOBJ= geneOBJ.clusterNodeNbr,
      cNbr;
    
    gct.editedCL.clear();   /* clear E.C.L */
    
    for(int i=0;i<nClist;i++)
    { /* look for genes which are members of curGene set */
      gene= mListP[i];
      if(gene==null)
        continue;
      cNbr= gene.clusterNodeNbr;
      if(cNbr>0 && cNbr==cNbrOBJ)
        gct.editedCL.addGene(gene);
    }
    
    return(true);
  } /* copyCurClusterToEditedGeneList */
  
  
  /**
   * setupKmeansHPstats() - compute summary K-means node HP statistics
   * for each HP sample in each Node and assign mnClustersCL to the
   * nKmeansNodes primary nodes.
   * @return true if successful.
   * @see Gene
   * @see Gene#isGeneProperty
   * @see GeneList
   * @see GeneList#addGene
   */
  boolean setupKmeansHPstats()
  { /* setupKmeansHPstats */
    if(nKmeansNodes==0)
      return(false);
    
    /* [1] Allocate new data structures */
    hpDataNbrA= new int[nKmeansNodes];
    hpDataMnA= new float[nKmeansNodes][nEPmsList];
    hpDataSDA= new float[nKmeansNodes][nEPmsList];
    
    Gene
      gene,
      mListP[]= complexClusterCL.mList,
      tmpCL[]= new Gene[nKmeansNodes];
    int
      i, j, n,
      nB,
      nBm;
    float
      rawData,
      sdd,
      sumData[][]= new float[nKmeansNodes][nEPmsList],
      sumDataDiffSq[][]= new float[nKmeansNodes][nEPmsList];
    boolean isKmeansNode;
    
    mnClustersCL= new GeneList(mae, nKmeansNodes, "mnClustersCL");
    
    /* [2] Compute vector sums for each node & artificial mn genes*/
    for(i=0;i<nClist;i++)
    { /* compute vector sums for each Node  */
      gene= mListP[i];
      nBm= gene.clusterNodeNbr-1;   /* [0:nKmeansNodes-1] */
      isKmeansNode= gene.isGeneProperty(Gene.C_IS_KMEANS);
      
      if(isKmeansNode)
        tmpCL[nBm]= gene;          /* save K-means nodes */
      hpDataNbrA[nBm]++;            /* compute counts */
      for(j=0;j<nEPmsList;j++)
      { /* compute sums */
        rawData= geneEPvector[i][j]/msEPlist[j+1].normFactor;
        sumData[nBm][j] += rawData;
      }
    } /* compute vector sums for each Node */
    
    /* Make sorted gene list of K-means nodes */
    for(n=0;n<nKmeansNodes;n++)
      mnClustersCL.addGene(tmpCL[n]);
    
    /* [3] Compute means for each vector */
    for(n=0;n<nKmeansNodes;n++)
    { /* compute means for each Node */
      for(j=0;j<nEPmsList;j++)
        hpDataMnA[n][j]= sumData[n][j]/hpDataNbrA[n];
    } /* compute means for each Node */
    sumData= null;                  /* G.C. */
    
    /* [4] compute sums of differences for each sample in each Node */
    for(i=0;i<nClist;i++)
    { /* compute sums of differences for each sample in each Node */
      gene= mListP[i];
      nB= gene.clusterNodeNbr-1;
      for(j=0;j<nEPmsList;j++)
      { /* compute sums */
        rawData= geneEPvector[i][j]/msEPlist[j+1].normFactor;
        sdd= (hpDataMnA[nB][j]-rawData);
        sumDataDiffSq[nB][j] += sdd*sdd;
      }
    } /*compute sums of differences for each sample in each Node  */
    
    /* [5] Compute S.D. for each sample in each Node */
    double sddD;
    for(n=0;n<nKmeansNodes;n++)
    { /* compute S.D. for each sample in each Node */
      for(j=0;j<nEPmsList;j++)
      { /* compute sums */
        sddD= (double)(sumDataDiffSq[n][j]/hpDataNbrA[n]);
        hpDataSDA[n][j]= (float)Math.sqrt(sddD);
      }
    } /* compute S.D. for each sample in each Node */
    
   /*
   if(mae.CONSOLE_FLAG)
     for(n=0;n<nKmeansNodes;n++)
     {
       fio.logMsg("CC-SKMHPS.6 n="+n+" mid="+mnClustersCL.mList[n].mid+ ":");
       for(j=0;j<nEPmsList;j++)
         fio.logMsg(" "+Util.cvf2s(hpDataMnA[n][j],3)+
                    "+-"+(Util.cvf2s(hpDataSDA[n][j],3));
       fio.logMsg("\n");
     }
    */
    
    return(true);
  } /* setupKmeansHPstats */
  
  
  /**
   * chkIfAbort() - check if abort, then shut down clustering.
   * [TODO] buggy - needs to be put on separate thread.
   * @see EventMenu#setClusterDisplayState
   * @see MAExplorer#repaint
   * @see MenuBarFrame#setSTOPbuttonState
   * @see Util#showMsg3
   */
  static void chkIfAbort()
  { /* chkIfAbort */
    if(mae.abortFlag)
    { /* go abort */
      mae.mbf.setSTOPbuttonState(false,false);  /* set to "RUNNING" and
                                                 * clear abort flag */
      mae.em.setClusterDisplayState(null, false); /* clear cluster method */
      Util.showMsg3("Aborting operation");
      mae.repaint();
      return;                                   /* forget it*/
    }
  } /* chkIfAbort */
  
  
  /**
   * cleanup() - close up what needs to be closed and GC all structures.
   * Reset to initial state so can restart it...
   * @see MenuBarFrame#setSTOPbuttonState
   */
  static void cleanup()
  { /* cleanup */
    if(mae!=null && mae.mbf!=null)
      mae.mbf.setSTOPbuttonState(false,false); /* enable "RUNNING" button */
    
    /* G.C. all static class instances */
    curClusterCL= null;
    curGene= null;
    geneClustersPopup= null;
    complexClusterCL= null;
    
    hcn= null;
    hierClusterGramPopup= null;
    
    KmeansClustersExprProfPopup= null;
    KmeansClustersPopup= null;
  } /* cleanup */
  
  
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /*  Class  Cluster                                                */
  /* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
  /**
   * [FUTURE]
   * Class Cluster holds an ordered list of genes for a particular
   * cluster. It also holds other metrics describing that cluster.
   *<P>
   * [TODO] Split out this class into a separate file so can keep
   * many clusters around and manipulate them. Note Clusters are not
   * just GeneBitSets but may be ordered gene lists (or HP lists) where
   * the order is sorted by an associated cluster similarity etc. feature.
   *<P>
   * [NOTE] This class is not currenly used. The cluster methods should
   * be rewritten to use it.
   */
  class Cluster
  {
    /** id # of this cluster */
    int
      id;
    /** id # of nearest cluster */
    int
      nearestClusterID;
    /** # of genes in this cluster */
    int
      nGenes;
    /** # of objects (eg. HPs)  in cluster */
    int
      nEP;
    /** optional cluster name */
    String
      cName;
    /** ordered list of genes in this cluster */
    Gene
      mList[];
    /** optional list of genes in this cluster */
    GeneList
      clusterCL;
    
    /** [0:nGenes-1] distance to meanEP */
    float
      distToMeanDP[];
    /** distance to nearest cluster */
    float
      nearestClusterDist;
    /** coefficient of variation computed as stDist/meanDist */
    float
      covDist;
    /** median distance to medianEP */
    float
      medianDist;
    /** mean distance to meanEP */
    float
      meanDist;
    /** stdDev distance to meanDP */
    float
      sdDist;
    
    /** median expr profile of this cluster */
    ExprProfile
      medianEP;
    /** mean expr profile of this cluster */
    ExprProfile
      meanEP;
    /** stdDev expr profile (to meanEP) of cluster */
    ExprProfile
      sdEP;
    
    
    /**
     * Cluster() - constructor.
     * @param id is the cluster ID
     * @param nGenes is the # of genes in the cluster
     * @param nEP is the # of samples in the expression profile.
     * @param cName is the name of the cluster
     * @param mList is list of genes in the cluster
     */
    Cluster(int id, int nGenes, int nEP, String cName, Gene mList[])
    { /* Cluster */
      this.id= id;
      this.nGenes= nGenes;
      this.nEP= nEP;
      this.cName= cName;
      this.mList= mList;
      
      distToMeanDP= new float[nGenes];
    } /* Cluster */
        
    /* [TODO] add methods to compute features */
        
  }  /* end of class Cluster */
  
  
} /* end of class ClusterGenes */
