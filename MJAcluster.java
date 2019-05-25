/** File: MJAcluster.java */
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJAcluster methods and data structures. 
 *
 * Access cluster data structures and support methods.
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * get_useCorrelationCoefficientFlag() - get gene-gene distance metric flag.
 * set_hierClusterUnWeightedAvgFlag() - hier-clustering averaging method flag
 * get_clusterOnFilteredGenesFlag() - to show cluster on Filtered genes 
 * get_useSimilarGeneClusterDisplayFlag() - if similar genes clustering method is active
 * get_useClusterCountsDisplayFlag() - if gene cluster counts of # similar genes is active
 * get_normHierClusterByRatioHPFlag() - to norm hierarchical cluster by ratio HP 
 * get_useMedianForKmeansClusteringFlag() - use K-median else default K-means 
 * get_useHierClusterDisplayFlag() - if hierarchical clustering method is active  
 * get_useKmeansClusterCountsDispFlag() - show K-means clusters counts
 * get_useLSQmagnitudeNormalizationFlag() - to normalize cluster expression 
 * get_useClusterDistanceCacheFlag() - to use cluster distance cache 
 * get_useShortClusterDistanceCacheFlag() - use short[] cluster dist cache
 * getKmeansClusters() -  list of K-means clustering data.
 * getClusterOfSimilarGenes() - list of gene cluster similar to seed gene.
 * getHierClusterOfGenes() - get Hashtable list of hierarchical gene cluster 
 * addr1D() - lookup lower-diagonal addr1D(x,y) [y' + x'*(x'+1)/2]
 * computeGeneGeneDistanceMatri() - return gene-gene cluster distance matrix.
 * calcNormGeneVectors() - calc HP-E intensity vector for genes to normalize
 * findGeneWithLeastSumDistances() - find gene with minimum cluster distance
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
 * @author P. Lemkin (NCI), J. Evans (CIT), C. Santos (CIT), 
 *         G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.9 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
public class MJAcluster extends MJAbase
{  

  /**
   * MJAcluster() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAcluster()
  { /* MJAcluster */
  } /* MJAcluster */
  
  
  /**
   * get_useCorrelationCoefficientFlag() - get gene-gene distance metric
   * flag. It is true if use correlation coefficient for computing the
   * gene-gene distance metric, false if use Euclidean distance.
   * @return value of flag
   */
  public final boolean get_useCorrelationCoefficientFlag()
  { return(mae.useCorrCoeffFlag); }
  
  
  /**
   * set_hierClusterUnWeightedAvgFlag() - set hier-clustering averaging
   * method flag.
   * If true useUPGMA else WPGMA hierarchical Clustering average.
   * gene-gene distance metric, false if use Euclidean distance.
   * @param value to set flag
   */
  public final void set_hierClusterUnWeightedAvgFlag(boolean value)
  { mae.hierClustUnWtAvgFlag= value; }
  
  
  /**
   * get_clusterOnFilteredGenesFlag() - get flag to cluster on Filtered genes
   * else All genes.
   * @return value of flag
   */
  public final boolean get_clusterOnFilteredGenesFlag()
  { return(mae.clusterOnFilteredCLflag); }
  
  
  /**
   * get_useSimilarGeneClusterDisplayFlag() - get flag to show if similar
   * genes cluster method is active.
   * @return value of flag
   */
  public final boolean get_useSimilarGeneClusterDisplayFlag()
  { return(mae.useSimGeneClusterDispFlag); }
  
  
  /**
   * get_useClusterCountsDisplayFlag() - get flag to show if gene cluster 
   * counts cluster method is active.
   * @return value of flag
   */
  public final boolean get_useClusterCountsDisplayFlag()
  { return(mae.useClusterCountsDispFlag); }
  
  
  /**
   * get_normHierClusterByRatioHPFlag() - get flag to that normalizing
   * each HP-E gene expression vector dataV[1:nHP] by the ratio of
   * HP-X dataV[HP-X]. Otherwise, normalize each element by the maximum
   * value for each sample.
   * @return value of flag
   */
  public final boolean get_normHierClusterByRatioHPFlag()
  { return(mae.normHCbyRatioHPflag); }
  
  
  /**
   * get_useMedianForKmeansClusteringFlag() - get flag for K-median
   * else K-means clustering
   * @return value of flag
   */
  public final boolean get_useMedianForKmeansClusteringFlag()
  { return(mae.useMedianForKmeansClusteringFlag); }
  
  
  /**
   * get_useHierClusterDisplayFlag() - get flag show hierarchical clusters
   * in the pseudoarray image for All Filtered genes.
   * @return value of flag
   */
  public final boolean get_useHierClusterDisplayFlag()
  { return(mae.useHierClusterDispFlag); }
  
  
  /**
   * get_useKmeansClusterCountsDispFlag() - get flag show K-means
   * cluster counts for all Filtered genes in the pseudoarray image.
   * @return value of flag
   */
  public final boolean get_useKmeansClusterCountsDispFlag()
  { return(mae.useKmeansClusterCntsDispFlag); }
  
  
  /**
   * get_useLSQmagnitudeNormalizationFlag() - get flag to normalize cluster
   * expression
   * vector geneEPvect[] to 1.0 for clustering
   * @return value of flag
   */
  public final boolean get_useLSQmagnitudeNormalizationFlag()
  { return(mae.useLSQmagNormFlag); }
  
  
  /**
   * get_useClusterDistanceCacheFlag() - get flag to use cluster distance
   * cache to speed up computations.
   * @return value of flag
   */
  public final boolean get_useClusterDistanceCacheFlag()
  { return(mae.useClusterDistCacheFlag); }
  
  
  /**
   * get_useShortClusterDistanceCacheFlag() - get flag t ouse short[]
   * cluster distance
   * cache to save memory
   * @return value of flag
   */
  public final boolean get_useShortClusterDistanceCacheFlag()
  { return(mae.useShortClusterDistCacheFlag); }
  
  
  /**
   * getKmeansClusters() - get Hashtable list of K-means clustering data.
   * Cluster genes passing the data Filter before getting the data.
   * Only genes passing the data Filter are used in computing the data.
   *
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>nAme                   - Value</B>
   * "NbrClusters"            - int number of clusters
   * "NbrSamples"             - int number of HP-E samples
   * "NbrGenes"               - int number of Genes clustered
   * "NameGeneListToCluster"  - String name of GeneList set to cluster
   * "InitialSeedGeneMID"     - int initial seed gene MID to start cluster 1
   * "maxGlobalDist"          - float max Global cluster distance for
   *                            all genes
   * "kMeansDist"             - float[1:NbrClusters] distance between K-means
   *                            clusters
   * "kMeansMaxDist"          - float[1:NbrClusters] max distance within each
   *                            K-means cluster
   * "mnWithinClusterDist"    - float[1:NbrClusters] mean within-cluster
   *                            distance between K-means clusters
   * "sdWithinClusterDist"    - float[1:NbrClusters] StdDev within-cluster
   *                            distance between K-means clusters
   * "kMeansList"             - int[1:NbrClusters] index of gene as K-means
   *                            cluster center
   * "hpDataNbrA"             - int [0:NbrClusters-1] opt # genes/K-means
   *                            cluster for HP-E samples
   * "hpDataMnA"              - float[0:NbrClusters-1][0:NbrSamples-1] opt
   *                            Mean HP-E quant data
   * "hpDataSDA"              - float[0:NbrClusters-1][0:NbrSamples-1] opt.
   *                            StdDev HP-E quant data
   * "nClustersFound"         - int # of unique K-means clusters
   *                            actually found
   * "maxKmeansNodes"         - int max # of K-means clusters set by
   *                            thresholding slider
   * "geneEPvector"           - float[0:NbrGenes-1][0:NbrSamples-1]
   *                            normalized quantified gene vector
   * "ClusterMeansGeneList"   - Hashtable list GeneList of means of clusters
   * "CurClusterGeneList"      - Hashtable list GeneList of genes in current
   *                            cluster
   * </PRE>
   *<P>
   * @param geneListToCluster name of gene list with genes to cluster
   * @param nbrOfClusters # of clusters to generate
   * @param initialSeedGeneMID initial seed gene specified by MID
   * @return Hashtable list else <code>null</code> if error.
   */
  public final Hashtable getKmeansClusters(String geneListToCluster,
                                           int nbrOfClusters,
                                           int initialSeedGeneMID)
  { /* getKmeansClusters */
    GeneList complexClusterCL=  mlLst.getGeneListByName(geneListToCluster);
    
    if(complexClusterCL==null || initialSeedGeneMID<0 ||
    initialSeedGeneMID>map.maxGenes)
      return(null);
    
    pur.updateCurGene(initialSeedGeneMID,0,null);
    ClusterGenes clg= mae.clg;
    clg.createClusterMethod(complexClusterCL, nbrOfClusters,
                            hps.msListE, hps.nHP_E,
                            ClusterGenes.METHOD_CLUSTER_KMEANS,
                            true /* resetFlag */);
    
    Hashtable ht= new Hashtable(25);
    ht.put("NbrClusters", new Integer(clg.maxNgeneClusterCnt));
    ht.put("NbrSamples", new Integer(clg.nEPmsList));
    ht.put("NbrGenes", new Integer(clg.nClist));
    ht.put("NameGeneListToCluster", geneListToCluster);
    ht.put("InitialSeedGeneMID", new Integer(clg.initialSeedGene.mid));
    ht.put("maxGlobalDist", new Float(clg.maxGlobalDist));
    ht.put("kMeansDist", clg.kMeansDist);
    ht.put("kMeansMaxDist", clg.kMeansMaxDist);
    ht.put("mnWithinClusterDist", clg.mnWithinClusterDist);
    ht.put("sdWithinClusterDist", clg.sdWithinClusterDist);
    ht.put("kMeansList", clg.kMeansList);
    ht.put("hpDataNbrA", clg.hpDataNbrA);
    ht.put("hpDataMnA", clg.hpDataMnA);
    ht.put("hpDataSDA", clg.hpDataSDA);
    ht.put("nClustersFound", new Integer(clg.nKmeansNodes));
    ht.put("maxKmeansNodes", new Integer(clg.maxKmeansNodes));
    ht.put("geneEPvector", clg.geneEPvector);
    ht.put("ClusterMeansGeneList",
    cvtGeneList2GeneMIDlist(clg.mnClustersCL));
    ht.put("CurClusterGeneList",
    cvtGeneList2GeneMIDlist(clg.curClusterCL));
    
    return(ht);
  } /* getKmeansClusters */
  
  
  /**
   * getClusterOfSimilarGenes() - get Hashtable list of gene cluster similar
   * to seed gene.
   * Cluster genes passing the data Filter before getting the data.
   * Only genes passing the data Filter are used in computing the data.
   * The similar genes are also copied to the Edited Gene list.
   *
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name                   - Value</B>
   * "NbrSamples"               - int number of HP-E samples
   * "NbrGenes"                 - int number of Genes clustered
   * "NameGeneListToCluster"    - String name of GeneList set to cluster
   * "InitialSeedGeneMID"       - int initial seed gene MID to start cluster
   * "maxGlobalDist"            - float max Global cluster distance for all
   *                              genes
   * "curGeneDistanceThr"       - float threshold distance for similar genes
   * "NbrSimilarGenesInCluster" - int # genes in cluster simiar to seed gene
   * "CurClusterGeneList"       - Hashtable of GeneList of genes in current
   *                              cluster
   * </PRE>
   *<P>
   * @param geneListToCluster name of gene list with genes to cluster
   * @param curGeneDistanceThr threshold distance for similar genes
   * @param initialSeedGeneMID initial seed gene specified by MID
   * @return <code>null</code> if not found or error,
   *          else return Hashtable list.
   */
  public final Hashtable getClusterOfSimilarGenes(String geneListToCluster,
                                                  float curGeneDistanceThr,
                                                  int initialSeedGeneMID)
  { /* getClusterOfSimilarGenes */
    GeneList complexClusterCL=  mlLst.getGeneListByName(geneListToCluster);
    
    if(complexClusterCL==null || initialSeedGeneMID<0 ||
    initialSeedGeneMID>map.maxGenes)
      return(null);
    
    pur.updateCurGene(initialSeedGeneMID,0,null);
    ClusterGenes clg= mae.clg;
    clg.createClusterMethod(complexClusterCL, 0,
                            hps.msListE, hps.nHP_E, 
                            mae.clg.METHOD_SIMILAR_GENES_CLUSTERING,
                            true /* resetFlag */);
    Gene geneToTest= map.midStaticCL.mList[initialSeedGeneMID];
    
    clg.curGeneDistThr= curGeneDistanceThr;
    clg.findClustersOfGene(mae, geneToTest, cfg.clusterDistThr, true);
    
    Hashtable ht= new Hashtable(12);
    ht.put("NbrSamples", new Integer(clg.nEPmsList));
    ht.put("NbrGenes", new Integer(clg.nClist));
    ht.put("NameGeneListToCluster", geneListToCluster);
    ht.put("InitialSeedGeneMID", new Integer(clg.initialSeedGene.mid));
    ht.put("maxGlobalDist", new Float(clg.maxGlobalDist));
    ht.put("curGeneDistanceThr", new Float(clg.curGeneDistThr));
    ht.put("NbrSimilarGenesInCluster", new Integer(clg.curClusterCL.length));
    ht.put("CurClusterGeneList",cvtGeneList2GeneMIDlist(clg.curClusterCL));
    
    return(ht);
  } /* getClusterOfSimilarGenes */
  
  
  /**
   * getHierClusterOfGenes() - get Hashtable list of hierarchical gene cluster
   * of genes. Data is clustered as a function of expression profile.
   *
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name                   - Value</B>
   * "NbrSamples"               - int number of HP-E samples
   * "NbrGenes"                 - int number of Genes clustered
   * "NameGeneListToCluster"    - String name of GeneList set to cluster
   * "NormByRatioHPflag"        - boolean flag: normalize EP data before
   *                              cluster by "HP-X Sample data/gene"
   *                              else "HP max intensities data"
   * "HierClusterUnWtAvgFlag"   - boolean flag: "unweighted-avg"
   *                              else "weighted-avg"
   * "HierClusterMode"          - int cluster linkage mode. Either
   *                              HIER_CLUST_NEXT_MIN_LNKG,
   *                              HIER_CLUST_PGMA_LNKG or HIER_CLUST_PGMC_LNKG
   * "CurClusterGeneList"       - Hashtable list GeneList of genes in current
   *                              cluster in order of hierarchical cluster.
   * "NbrNodes"                 - int number of nodes in the cluster
   *                              (2*NbrGenes-1)
   * "MaxDistLR"                - float max distance (squared) between any
   *                              Left and Right found between all nodes
   * "TreeEnumeration"          - Hashtable enumeration of the hierarchical
   *                              cluster
   *
   * The <B>TreeEnumeration Hashtable list</B> is returned an enumeration
   * of the cluster tree and is defined as:
   * <B>Name                   - Value</B>
   * "GeneMID"                  - int Gene MID index if it is a terminal
   *                              node else -1
   * "NodeID"                   - int unique cluster node #
   * "EnumOrder"                - float enumeration order in range
   *                              [0:NbrNodes-1].
   *                              Intermediate nodes may be fractional eg.
   *                              (LeftChildNode.enumOrder +
   *                               RightChildNode.enumOrder)/2.0
   * "ParentNodeID"             - int if not -1, then parent node
   * "LeftChildNodeID"          - int if not -1, then left node
   * "RightChildNodeID"         - int distance between left and right
   *                              children nodes
   * "MeanEPdataForNode"        - mean vector float [0:NbrSamples-1] of left
   *                              and right
   * "NbrChildren"              - int # of children of Node
   * </PRE>
   *<P>
   * @param geneListToCluster name of gene list with genes to cluster
   * @return <code>null</code> if not found or error,
   *         else return Hashtable list.
   */
  public final Hashtable getHierClusterOfGenes(String geneListToCluster)
  { /* getHierClusterOfGenes */
    GeneList complexClusterCL=  mlLst.getGeneListByName(geneListToCluster);
    
    if(complexClusterCL==null)
      return(null);
    
    ClusterGenes clg= mae.clg;
    clg.createClusterMethod(complexClusterCL, 0, mae.hps.msListE, 
                            mae.hps.nHP_E, clg.METHOD_HIERARCHICAL_CLUSTERING,
                            true /* resetFlag */);
    ClusterGenes cg= mae.clg;
    HierClustNode
      hcnNode,
      hcn= clg.hcn;
    if(!hcn.setEnumerateNodes())
      return(null);             /* no hier cluster to report */
    
    Hashtable htTree[]= new Hashtable[2*clg.curClusterCL.length+1];
    
    while(true)
    { /* Build ordered gene list */
      hcnNode= hcn.enumerateLeafNodes();
      if(hcnNode==null)
      { /* test if done */
        break;
      }
      
      if(hcnNode.cIdx!=-1)
      { /* valid gene at terminal node */
        Gene gene= map.midStaticCL.mList[hcnNode.cIdx];
        if(gene==null)
          continue;
        Hashtable htNode= new Hashtable(12);
        htNode.put("GeneMID", new Integer((int)gene.mid));
        htNode.put("NodeID", new Integer(hcnNode.nodeID));
        htNode.put("EnumOrder", new Float(hcnNode.enumOrder));
        htNode.put("ParentNodeID", new Integer(hcnNode.hcParent.nodeID));
        htNode.put("LeftChildNodeID", new Integer(hcnNode.hcLeft.nodeID));
        htNode.put("RightChildNodeID", new Integer(hcnNode.hcRight.nodeID));
        htNode.put("DistLR", new Float(hcnNode.distLR));
        htNode.put("MeanEPdataForNode", hcnNode.dataV);
        htNode.put("NbrChildren", new Integer(hcnNode.nbrChildren));
        htTree[hcnNode.cIdx]= htNode;
      }  /* valid gene at terminal node */
      else
        continue;
    } /*  Build ordered gene list */
    
    Hashtable ht= new Hashtable(15);
    ht.put("NbrSamples", new Integer(cg.nEPmsList));
    ht.put("NbrGenes", new Integer(cg.nClist));
    ht.put("NameGeneListToCluster", geneListToCluster);
    ht.put("NormByRatioHPflag", new Boolean(mae.normHCbyRatioHPflag));
    ht.put("HierClusterUnWtAvgFlag", new Boolean(mae.hierClustUnWtAvgFlag));
    ht.put("HierClusterMode", new Integer(mae.hierClustMode));
    ht.put("CurClusterGeneList",cvtGeneList2GeneMIDlist(cg.curClusterCL));
    ht.put("NbrNodes",new Integer(hcn.nObj));
    ht.put("MaxDistLR",new Float(hcn.maxDistLR));
    ht.put("TreeEnumeration", htTree);
    
    return(ht);
  } /* getHierClusterOfGenes */
  
  
  /**
   * addr1D() - lookup lower-diagonal addr1D(x,y) [y' + x'*(x'+1)/2]
   *<PRE>
   * where:
   *    x' = (x>y) ? x : y,
   *    y' = (x>y) ? y : x.
   *</PRE>
   * @param x coordinate
   * @param y coordinate
   * @return the computed 1D address for the 2D (x,y) address.
   */
  public final int addr1D(int x,int y)
  { /* addr1D */
    int
      xP= (y<x) ? x : y,
      yP= (y<x) ? y : x,
      idx= yP + ((xP*(xP+1))/2);  /* idx= yP + (xP*(xP+1))>>1; */
    
    return(idx);
  } /* addr1D */
  
  
  /**
   * computeGeneGeneDistanceMatrix() - return gene-gene cluster distance matrix.
   * This is encoded as an lower diagonal 1D array using  for genes x and
   * y using addr1D(x,y)to access the data.
   *<PRE>
   *     addr1D(x,y) = [y' + x'*(x'+1)/2]
   * where:
   *    x' = (x>y) ? x : y,
   *    y' = (x>y) ? y : x.
   *</PRE>
   * @param geneListToUse is the name of the genelist to filter by
   * @return matrix if it exists, else return null.
   */
  public final float[] computeGeneGeneDistanceMatrix(String geneListToUse)
  { /* computeGeneGeneDistanceMatri */
    
    GeneList dataCL= mlLst.getGeneListByName(geneListToUse);
    int
      nClist= dataCL.length,
      nEP= hps.nHP_E;
    ClusterGenes cg= new ClusterGenes(dataCL, hps.msListE, hps.nHP_E);
    
    float ccDist1D[]= new float[1+((nClist*nClist)/2)+nClist];    
    cg.ccDist1D= ccDist1D;
    
    mae.ccd.calcNormGeneVectors(dataCL, nClist);
    cg.ccd.calcGeneGeneDists(ccDist1D, nClist, nEP);
    cg.ccDist1D= null; /* this will let it garbage collect the struct */
    
    return(ccDist1D);
  } /* computeGeneGeneDistanceMatri */
  
  
  /**
   * calcNormGeneVectors() - compute HP-E intensity vector for geneList To
   * Normalize. Normalize by the first entry of each vector.
   * [TODO] add notes on normalization options set in the menus
   * and readable using flag query methods here.
   * @param geneListToNormalize gene list to normalize.
   * @return normalized [0:nbrGenesInGeneList-1][0:nbrSamples-1].
   */
  public final float[][] calcNormGeneVectors(String geneListToNormalize)
  { /* calcNormGeneVectors */
    GeneList dataCL= mlLst.getGeneListByName(geneListToNormalize);
    int
      nClist= dataCL.length,
      nEP= hps.nHP_E;    
    ClusterGenes cg= new ClusterGenes(dataCL, hps.msListE, hps.nHP_E);
    
    float geneEPvector[][]= cg.ccd.calcNormGeneVectors(dataCL, nClist);
    
    return(geneEPvector);
  } /* calcNormGeneVectors */
  
  
  
  /**
   * findGeneWithLeastSumDistances() - find gene with minimum cluster distance
   * to all other genes.
   * @param ccDist1D gene-gene distance computed with
   *        computeGeneGeneDistanceMatrix()
   * @param nGenes is the # of genes in gene-gene matrix.
   * @return the MID gene index if it exists, else return -1 if errors.
   * @see #computeGeneGeneDistanceMatrix
   */
  public final int findGeneWithLeastSumDistances(float ccDist1D[], int nGenes)
  { /* findGeneWithLeastSumDistances */
    if(ccDist1D==null || nGenes<=0)
      return(-1);
    int bestIdx= GeneGeneDist.findGeneWithLeastSumDistances(ccDist1D,nGenes);
    return(bestIdx);
  } /* findGeneWithLeastSumDistances */
  
  
} /* end of class MJAcluster */

