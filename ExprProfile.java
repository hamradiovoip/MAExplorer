/** File: ExprProfile.java */

import java.util.*;
import java.lang.*;

/**
 * The class creates an nHP_E sample expression profile object for a gene.
 * The gene is specified by its mid for the HPs in HP-E condition list of
 * samples. Note these arrays are indexed [0:nHP_E-1]. This contains data, but
 * no graphics.
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
 * @version $Date: 2003/07/07 21:40:41 $   $Revision: 1.10 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ExprProfileCanvas
 * @see ExprProfileOverlay
 * @see Filter#setGeneListBySpotCV
 * @see Filter#setPostiveQuantData
 * @see Report#createTableOfExprProfiles
 */

class ExprProfile
{
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;                   
  /** link to global Maps instance */
  private Maps
    map;
    
  /** flag: normalize hpData[] & cvData[] to 1.0 */ 
  boolean
    normToOneFlag; 
  /** flag: generate mean plots (of the clusters) else individual plots */
  boolean
    doMeanPlotsFlag;  
  /** flag: set by setupData() if successful */
  boolean
    didDataSetupFlag;      
  
  /** ARG: Master Gene ID (MID) for gene we are creating if != -1 */ 
  int
    mid;                       
  /** GID derived from mid */    
  int
    gid1;                   
  /** (opt) ganged GID derived from mid */    
  int
    gid2;     
  /** # clustered genes to this mid */
  int
    nGeneClustersCnt;     
  /** # of HP's to draw after setup*/
  int
    nHP_E;                  
  /** (hpName,hpData,cvData) HP-E samples */  
  String
    hpLabel[];                            
  /** names for all HP-E samples */
  String
    hpName[];              

   /* [TODO] long term we need to decide where to centralize
    * keeping normalized expression vector data. Possibly
    * keep in Gene instance? This will make sure that all methods
    * EVERYWHERE use the same data source.
    *
    * [NOTE] currently some methods and classes use hpData[] and some
    * do not... Make this consistent!!!!
   */
   
  /** opt. # quant data for all HP-E samples */
  int
    hpDataNbr;             
    
  /** maximum of the hpDataMn[] values */ 
  float
    maxMean;                 
  
  /** normalized quantified data for all HP-E samples */ 
  float
    hpData[];                
  /** opt. max mean normalized quantified data for all samples and genes */ 
  float
    maxHPdataMn;            
  /** opt. Mean quant data for all HP-E samples */ 
  float
    hpDataMn[];              
  /** opt. S.D. quant data for all HP-E samples */ 
  float
    hpDataSD[];              
  /** Coefficient of variation all HP-E samples */
  float
    cvData[];              
  
  /** Y coord for horizontal axis for vertical line, else is label for 
   * setting current gene
   */  
  int
    y1Coord;    
  /** X coords for all HP-E vert lines in canvas for event handler... */
  int
    xCoord[];              


  /**
   * ExprProfile() - Constructor to create expresion profile object
   * @param mae is the MAExplorer instance
   * @param mid is the Master Gene Index if not -1
   * @param normToOneFlag normalize data to 1.0 if set
   * @see #setupData
   */
  ExprProfile(MAExplorer mae, int mid, boolean normToOneFlag )
  { /* ExprProfile */
    this.mae= mae;
    this.mid= mid;
    map= mae.mp;
    
    /* Setup here to save space if multiple instances */
    didDataSetupFlag= false;
    doMeanPlotsFlag= false;
    if(mid==-1)
      return;
    
    setupData(mid);
  } /* ExprProfile */
  
  
  /**
   * updateData() - update expression profile with new gene data
   * @param mid is the Master Gene Index to use in computing expression profile
   * @see #setupData
   */
  boolean updateData(int mid)
  { /* updateData */
    this.mid= mid;
    doMeanPlotsFlag= false;
    
    return(setupData(mid));
  } /* updateData */
  
  
  /**
   * updateData() - update expression profile with new gene and mean data
   * @param mid is the Master Gene Index to use in compouting expression profile
   * @param doMeanPlotsFlag generate mean plots (of the clusters) else individual plots
   * @param hpDataNbr is the number of samples/Expr profile
   * @param maxHPdataMn opt. max mean normalized quantified data for all samples and genes
   * @param hpDataMn is Returned list of mean data if doing mean plots
   * @param hpDataSD is Returned list of StdDev data if doing mean plots
   * @see #setupData
   */
  boolean updateData(int mid, boolean doMeanPlotsFlag, int hpDataNbr,
                     float maxHPdataMn, float hpDataMn[], float hpDataSD[] )
  { /* updateData */
    this.mid= mid;
    this.doMeanPlotsFlag= doMeanPlotsFlag;
    this.hpDataNbr= hpDataNbr;
    this.maxHPdataMn= maxHPdataMn;
    this.hpDataMn= hpDataMn;
    this.hpDataSD= hpDataSD;
    
    return(setupData(mid));
  } /* updateData */
  
  
  /**
   * setupData() - setup the data arrays for the other computations
   * @param mid is the Master Gene Index to use in compouting expression profile
   * @return false if an illegal mid
   * @see MaHybridSample#getDataByGID
   */
  boolean setupData(int mid)
  { /* setupData */
    if(mid<0 || mid> map.maxGenes || map.midStaticCL.mList[mid]==null)
      return(false);
    
    gid1= map.mid2gid[mid];
    if(gid1==-1)
      return(false);
    gid2= map.gidToGangGid[gid1];
    nGeneClustersCnt= map.midStaticCL.mList[mid].nGeneClustersCnt;
    
    /* [1] setup structures */
    if(hpData==null || nHP_E!=mae.hps.nHP_E)
    { /* allocate arrays to hold data */
      nHP_E= mae.hps.nHP_E; /* may have changed so check */
      hpData= new float[nHP_E];
      cvData= new float[nHP_E];
      hpName= new String[nHP_E];
      hpLabel= new String[nHP_E];
      xCoord= new int[nHP_E];
    }
    
    /* [2] get list of quant data for this gene for each of the
     * HPs in the HP-E list.
     */
    MaHybridSample
      msListE[]= mae.hps.msListE,
      ms;
    float
      g1,g2,
      mean,
      diff,
      cv;
    String cvStr= (doMeanPlotsFlag) ? " +-" : " CV:";
    int
      i,
      precision= (maxHPdataMn>1000.0F) ? 0 : 3,
      maxFIELDS= mae.cfg.maxFIELDS,
      type= (mae.useRatioDataFlag) ? mae.ms.DATA_RATIO_F1F2TOT : mae.ms.DATA_F1TOT;
      maxMean= 0.0F;
    
    for(i=0;i<nHP_E;i++)
    { /* get the data vector */
      ms= msListE[i+1];
      if(ms!=null)
      { /* process HP[i] */
        hpName[i]= ms.hpName;
        if(doMeanPlotsFlag)
        { /* use mean plot from hpDataMn[i] & hpDataSD[i] */
          /* [CHECK] if used this for all EP calculations
           * in HierClustering etc. What about speed?
           */
          mean= hpDataMn[i];
          cv= hpDataSD[i];
        }
        else
        { /* compute mean and cv from normalized data */
          if(maxFIELDS>1)
          { /* has replicate spots/gene */
            /* normalized (totQ - bkgrdQ) */
            g1= ms.getDataByGID(gid1, mae.useRatioDataFlag, ms.DATA_F1TOT);
            g2= ms.getDataByGID(gid1, mae.useRatioDataFlag, ms.DATA_F2TOT);
          }
          else
          { /* use single spot or ratio data */
            g1= ms.getDataByGID(gid1, mae.useRatioDataFlag,
            type);
            g2= g1;                  /* so CV= 0.0 */
          }
          
          mean= (g1+g2)/2.0F;
          maxMean= Math.max(maxMean,mean);
          
          /* Compute coefficient of variation */
          diff= (float)(g1-g2);
          cv= (mean==0.0F) ? 0.0F : Math.abs(diff)/mean;
        } /* compute mean and cv from normalized data */
        
        /* Save the data */
        hpData[i]= mean;
        cvData[i]= cv;
      } /* process HP[i] */
    } /* get the data vector */
    
    if(normToOneFlag)
      for(i=0;i<nHP_E;i++)
      { /* scale to 1.0 for maxMean */
        
        /* [CHECK]
         * 1. do we use this for all EP calculations in
         * all EP metric computations? Eg. hierClustNode,
         * Kmeans clustering, Similar Genes, etc.
         *
         * 2. Need to add goodSpot[hp][mid] in EP data and
         * to scale by variable nHP[mid] depending on how many
         * spots are good for each mid.
         */
        hpData[i] /= maxMean;
        cvData[i] /= maxMean;
      }
    
    for(i=0;i<nHP_E;i++)
      hpLabel[i]= "#" + (i+1) + " [" + hpName[i] +
                  ((mae.bkgdCorrectFlag) ? "] I':" : "] I:") +
                  Util.cvf2s(hpData[i],precision) +
                  cvStr + Util.cvf2s(cvData[i],precision);
      
    didDataSetupFlag= true;  /* enable painting */
    return(true);
  } /* setupData */
  
  
  
} /* end of ExprProfile class */

