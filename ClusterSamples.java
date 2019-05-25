/** File: ClusterSamples */

/**
 * This class generates a cluster tree of HP samples from the active gene list. 
 * [FUTURE] The class represents an individual ClusterSamples entry.
 * Model this class after  ClusterGenes.java, use it as a model for this
 * class. *
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
 * @version $Date: 2003/06/04 21:48:20 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

class ClusterSamples 
{
   /** 
    * [TODO] add functionality and reuse HierClustNode class to 
    * cluster HPs as function of Filtered genes..
    */ 
    
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;                 
  
  /** list of genes used in pattern vector*/  
  GeneList
    mList;                  
  /** # of genes in mList */
  int 
    nMlist;              
  /** list of samples H.P.s to cluster */
  MaHybridSample 
    msList[];            
  /** size of msList[] */
  int 
    nMSlist; 
  /** normalized quantitation gene vector for all HPs */
  float
    HPvector[][];        
  /** Euclidean distance or correlation coefficient of sample HP pattern vector 
   * from another gene.
   */
  float
    HPdistances[][];     
  /** index of nearest HP */
  int
    nearestHP[];          


  /**
   * ClusterSamples() - constructor 
   * @param mae is the MAExplorer instance
   * @param mList is the GeneList defining the per sample expression lists
   * @param msList is the list of samples to cluster
   * @param nMSlist is the # of samples
   */
  ClusterSamples(MAExplorer mae, GeneList mList, MaHybridSample msList[],
                 int nMSlist)
  { /* ClusterSamples */
    this.mae= mae;
    this.mList= mList;
    this.nMlist= mList.length;
    this.msList= msList;
    this.nMSlist= nMSlist;
  } /* ClusterSamples */

} /* end of class ClusterSamples */
  


   
