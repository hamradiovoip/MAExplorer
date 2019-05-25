/* File: CompositeDatabase.java */

import java.awt.*;
import java.io.*;

/**
 * Class to operate on composite sets of Hybridized Samples lists HP-X set, HP-Y set and HP-E list. 
 * It computes global statistics and also is used to access list of averaged
 * genes from these lists. 
 *<P>
 * The current gene associated parameters are defined in this class and include:
 * (isValidObjFlag, objMID, objGID, objGIDG, objField, objGrid, objRow, objCol,
 *  objX, objY). 
 *<P>
 * It includes many primary methods used throughout MAExplorer for accessing 
 * this data including:
 *<PRE>
 *<B>List of methods</B>
 * setCurrentGenes() - set current genes to those in the GeneClass database
 * calcRatioHistVal() - compute histogram bin value & increment ratio histogram
 * calcRatioHists() - compute ratio histogram of d1List[]/d2List[] data
 * calcHistOfHP_XYRatios() - compute histogram of HP-X/-Y (or 'sets') spot ratios of filtered data
 * setIntensHistCLfromIntensRange() - set GeneList of (F1+F2)/2 in intensity range
 * setRatioHistCLfromF1F2Ratios() - set GeneList of F1/F2 ratios in ratio range
 * setRatioHistCLfromHP_XYRatios() - set GeneList of X/Y ratios in ratio range
 * setRatioHistCLfromHP_XYsetRatios() - set GeneList of X/Y set ratios in ratio range
 * lookupHPcoords() - lookup closest gene name by (x,y) in sample pseudoarray and set current gene
 * setOBJtoGID() - set the current gene xxxOBJ values to the specified GID
 * setObjCoordFromMaster_ID() - set gene xxxOBJ values from Master_ID name
 * setObjCoordFromMID() - set gene xxxOBJ values from MID for current HP sample
 * getNormHP_XandYdata() - get normalized list spot (HP-X,HP-Y) data passing the data filter
 * getRawHP_XandYdata() - get raw list spot (HP-X,HP-Y) data passing the data filter
 * getHP_XandYsetData() - get list spot (HP-X,HP-Y) sets data passing the data filter
 * calAllSamples() - compute (meanCalDNA, stdDevCalDNA) calibration for all samples
 * recalcNorms() - recompute normalizations for all samples
 * calcHP_HPcorrelations() - compute HP vs. HP samples correlation coefficients table 
 * createHPmeanAndVarTable() - create table (HP,mn,stdDev,min,max,median) of raw intensity data
 * recalcGlobalStats() - recompute global array statistics with all or just good spots
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
 * @author P. Lemkin (NCI), G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2003/11/24 21:18:54 $   $Revision: 1.19 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Filter
 * @see GeneList
 * @see HPxyData
 * @see MaHybridSample
 * @see SampleSets
 */

class CompositeDatabase 
{
  /* [TODO] re-eval all methods and move what does not belong or
   *  make new classes
   */

  /** link to global MAExplorer instance */
  private MAExplorer 
    mae;                        
  /** link to global Maps instance */
  private Maps
    map;                       
  /** link to global Filter instance */
  private Filter
    fc;                       
  /** link to global SamplesSets instance */
  private SampleSets
    hps;                       
      
  /** histogram of g1/g2 ratios [0:nRatioHistBins] in range 0.01:100.0 */
  int
    ratioHist[];              
    
  /** scale of ratio histogram of g1/g2 ratios */
  double
    ratioValue[];             
    
  /** max value found in ratioHist[] */
  int
    maxRatioHistIdx= -1;
  /** min value found in ratioHist[] */
  int    
    minRatioHistIdx= 1000000; 
  /** # of bins in ratioHist[] */
  int
    nRatioHistBins;           
    
  /** generic HP-X/Y set object */
  static HPxyData
    hpXYdata= null;           
  
  /** min intensity HP_X in getNormHP_XandYdata */
  float
    minDataHP_X= 10000000.0F;
  /** min intensity HP_Y in getNormHP_XandYdata */
  float
    minDataHP_Y= 10000000.0F;
  /** max intensity HP_X in getNormHP_XandYdata */
  float
    maxDataHP_X= -10000000.0F; 
  /** max intensity HP_Y in getNormHP_XandYdata */
  float
    maxDataHP_Y= -10000000.0F;
  
  /** temporary GeneList used in local gene list operations */
  private GeneList 
    tempCD_CL;                
     
  /** grand mean HP 'User Filter Gene Set' means for normalization. */
  float
    grandMeanUseGeneSet= 0.0F;     
  /** grand mean HP Calib.DNA means for normalization. */
  float
    grandMeanCalDNA= 0.0F;    
  /** [1:nRows][1:nCols] rSq, HP vs HP correlation coefficient */
  float
    rSqHP_HP[][]= null;       
    		    
  /** Flag to indicate that the current gene (i.e. object) is valid.
   * If MouseReleased selected object matches xy coords within radius, 
   * then the objXXX values defined below are valid.
   */
  static boolean
    isValidObjFlag= false;    
    
				 
  /* --- The following current gene (i.e. object) values are set by 
   * lookupHPcoords() and is called when the user clicks on a spot
   * corresponding to a gene ---
   */
   /** Gene index mid of object if isValidObj is TRUE */
   static int
    objMID;
   /** GridCoords gid of object if isValidObj is TRUE */
   static int
    objGID;                    
   /** Gang f' GridCoords gid of object if isValidObj is TRUE */
   int
    objGIDG;                    
    
   /** object Field that best matches xy coords if isValidObj is TRUE*/
   int
    objField;		         
   /** object Grid that best matches xy coords if isValidObj is TRUE */
   int
    objGrid; 	         
   /** object Grid-Row that best matches xy coords if isValidObj is TRUE */
   int
    objRow;	         
   /** object Grid-Column that best matches xy coords if isValidObj is TRUE */
   int
    objCol;
   /** optimal spot X position if isValidObj is TRUE */ 
   int
    objX; 
   /** optimal spot Y position if isValidObj is TRUE */ 
   int
    objY;
  	 
   /** Common Grid labels for drawing onto the canvas.
    * NOTE: this is specific for each microarray. 
    * Computed as: maxFIELDS*maxGRIDS+2  (includes title)
    * actual # text items to display
    */ 
   int
    maxTextItems;              
    
   int
     nTextItems;               
   /** text HP labels shared colors */
   Color 
     color_textHP[];           
   /** text HP labels shared fonts */
   Font 
     font_textHP[];            
  
   /** flag: track last updated object to avoid recursion problems */
   boolean
     recursionFlag;            
          
      
   /**
    * CompositeDatabase() - constructor to set up data structures.
    * Access is then performed using various methods.
    * @param mae is the MAExplorer instance
    * @see GeneList
    * @see GeneList#clear
    * @see HPxyData
    */
   CompositeDatabase(MAExplorer mae)
   { /* CompositeDatabase */
     this.mae= mae;
     map= mae.mp;
     fc= mae.fc;
     hps= mae.hps;
     
     maxTextItems= (mae.cfg.maxFIELDS*mae.cfg.maxGRIDS+4);
     nTextItems= 0;             /** # of labels to draw in image */
     
     recursionFlag= false;
     nRatioHistBins= MAExplorer.MAX_RATIO_HIST;
     ratioHist= new int[nRatioHistBins+1];
     ratioValue= new double[nRatioHistBins+1];
     
     mae.fc.workingCL.clear();
     tempCD_CL= new GeneList(mae,map.maxGenes,"tempCD_CL", true);
     
     /* The hpXYdata is the master object use everywhere... */
     hpXYdata= new HPxyData(mae, -1, mae.useHPxySetDataFlag 
                            /* use msListX/Y else msX/Y */);
   } /* CompositeDatabase */
   
   
   /**
    * setCurrentGenes() - set current genes to those in the GeneClass database
    * if the curGenesMode is true. Else set all genes as current.
    * @param curGenesMode
    * @see GeneList#addGene
    * @see GeneList#clear
    */
   void setCurrentGenes(int curGenesMode)
   { /*setCurrentGenes */
     mae.fc.gcMemberCL.clear();    /* clear the set */
     Gene gene;
     int maxGenes= map.maxGenes;
     for(int mid=0;mid<maxGenes; mid++)
     { /* test if cloneId entry exists */
       gene= map.midStaticCL.mList[mid];
       if(gene!=null && gene.gid!=-1)
         mae.fc.gcMemberCL.addGene(gene);
     } /* test if cloneId entry exists */
   } /* setCurrentGenes */
   
   
   /**
    * calcRatioHistVal() - compute histogram bin value & increment ratio histogram
    * @param g1 intensity value
    * @param g2 intensity value
    * @return the ratio defined as: g1/g2.
    */
   double calcRatioHistVal(float g1, float g2)
   { /* calcRatioHistVal */
     int idx= 0;
     double r;
     
     if(g1>0 && g2==0)
       r= 1000000.0;           /* i.e. greater than MAX_RATIO */
     else if(g1==0 && g2>0)
       r= 0.0;                 /* i.e. less than MAX_RATIO */
     else
       r= ((double)g1)/((double)g2);
     
     if(r > mae.MAX_RATIO)
       r= mae.MAX_RATIO;       /* Math.min(r,mae.MAX_RATIO)*/
     if(r < mae.MIN_RATIO)
       r= mae.MIN_RATIO;       /* Math.max(r, mae.MIN_RATIO) */
     
     for(int j=0; j<mae.MAX_RATIO_HIST-1;j++)
     { /* find bin */
       if((mae.histRatioBin[j]<=r) && (r<mae.histRatioBin[j+1]))
       {
         idx= j;
         break;                /* found it, put in bin [idx] */
       }
     } /* find bin */
     
     ratioHist[idx]++;            /* count it */
     minRatioHistIdx= Math.min(idx, mae.MAX_RATIO_HIST);
     maxRatioHistIdx= idx;        /* must be > 0!  Math.max(idx, 0); */
     
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("RG-cRHV() idx="+idx+", r=" +
                         Util.cvd2s(r,2)+ ", histRatioBin[idx]="+
                         Util.cvd2s(mae.histRatioBin[idx],2));
       */
     
     return(r);
   } /* calcRatioHistVal */
   
   
   /**
    * calcRatioHists() - compute ratio histogram of d1List[]/d2List[] data.
    * Compute the ratio histogram and set ratio values for genes in ml.
    * @param nList is the size of the data arrays
    * @param d1List ARG: is list of data1 data
    * @param d2List ARG: is list of data2 data
    * @param ml RTN: is GeneList to use in filtering the data
    * @see Gene#setGeneData
    * @see GeneList#clear
    * @see #calcRatioHistVal
    */
   void calcRatioHists(int nList, float d1List[], float d2List[], GeneList ml)
   { /* calcRatioHists */
     Gene 
       gene,
       mList[]= ml.mList;
     float ratio;
     
     ml.clear();  /* clear ratio gene list */
     
     maxRatioHistIdx= -1;
     minRatioHistIdx= 100000000;
     for(int i=0; i<=mae.MAX_RATIO_HIST;i++)
     {
       ratioHist[i]= 0;
       ratioValue[i]= 0.0;
     }
     
     for(int i=0; i<nList; i++)
     { /* compute Ratio histogram  d1/d2 */
       ratio= (float)calcRatioHistVal(d1List[i],d2List[i]);       
       gene= mList[i];      
       gene.setGeneData(ratio);
     }
   } /* calcRatioHists */
   
   
   /**
    * calcHistOfF1F2Ratios() - compute histogram of F1/F2 ratios for spots
    * which are selected in the GeneList ml.
    * @param ml is gene list to use in filtering
    * @param ms is the Sample to use
    * @return true if succeed
    * @see MaHybridSample#getF1F2Data
    * @see #calcRatioHists
    */
   boolean calcHistOfF1F2Ratios(GeneList ml, MaHybridSample ms)
   { /* calcHistOfF1F2Ratios */
     float
     f1List[]= new float[map.maxGenes],     /* data */
     f2List[]= new float[map.maxGenes];
     int
     nList= ms.getF1F2Data(f1List, f2List, null, ml, false, false, false);
     
     /* Compute the ratio histogram and set ratio values in ml */
     calcRatioHists(nList, f1List, f2List, ml);
     
      /*
      if(mae.CONSOLE_FLAG)
   for(int i=1; i<=mae.MAX_RATIO_HIST; i++)
     mae.fio.logMsgln("CD-CHF12R ratioHist[" +i+ "]=" +
          ratioHist[i] +" ratioValue[" +i+ "]=" + ratioValue[i]);
       */
     
     return(true);
   } /* calcHistOfF1F2Ratios */
   
   
   /**
    * calcHistOfHP_XYRatios() - compute histogram of HP-X/-Y (or 'sets') spot ratios of filtered data
    * @param ml is gene list to use in filtering
    * @param ms is the Sample to use
    * @return true if succeed
    * @see #getHP_XandYsetData
    * @see #getNormHP_XandYdata
    * @see #calcRatioHists
    */
   boolean calcHistOfHP_XYratios(GeneList ml, boolean useXYsetsFlag)
   { /* calcHistOfHP_XYRatios */
     if(!useXYsetsFlag && (hps.nHP<2 || (mae.msX==mae.msY)))
       return(false);
     else if(useXYsetsFlag && !mae.useHPxySetDataFlag)
       return(false);
     
     int
     nList;
     float
     xList[]= new float[map.maxGenes],     /* data */
     yList[]= new float[map.maxGenes];
     
     /* Get the xy lists of data */
     if(useXYsetsFlag)
       nList= getHP_XandYsetData(xList, yList, null, ml, false, false);
     else
       nList= getNormHP_XandYdata(xList,yList,null, ml, mae.msX,mae.msY,
                                  false, false);
     
     /* Compute the ratio histogram and set ratio values in ml */
     calcRatioHists(nList, xList, yList, ml);
     
      /*
      if(mae.CONSOLE_FLAG)
        for(int i=1; i<=mae.MAX_RATIO_HIST; i++)
          mae.fio.logMsgln("CD-CHXYR() ratioHist[" + i+
                           "]=" + ratioHist[i] + " ratioValue[" +
                           i+ "]=" + ratioValue[i]);
       */
     
     return(true);
   } /* calcHistOfHP_XYRatios */
   
   
   /**
    * setIntensHistCLfromIntensRange() - set GeneList of (F1+F2)/2 in intensity range
    * @param  resultCL RTN: is the resulting gene list
    * @param ms is the Sample to use
    * @param intensLB is lower bound of intensity
    * @param intensUB is upper bound of intensity
    * @param outsideRangeFlag test if outside of range else inside range
    * @return true if succeed
    * @see Filter#isMIDinWorkingCL
    * @see GeneList#addGene
    * @see GeneList#clear
    * @see MaHybridSample#getDataByGID
    * @see #getNormHP_XandYdata
    * @see #calcRatioHistVal
    */
   boolean setIntensHistCLfromIntenRange(GeneList resultCL, MaHybridSample ms,
                                         float intensLB, float intensUB,
                                         boolean outsideRangeFlag)
   { /* setIntensHistCLfromIntensRange */
     resultCL.clear();               /* clear gene list */
     tempCD_CL.clear();
     Gene
       mList[]= map.midStaticCL.mList,
       gene;
     float intens;
     boolean
       ok,
       isInsideFlag;
     int
       mid,
       gid,
       maxGenes= map.maxGenes,
       type= (mae.useRatioDataFlag)
               ? ms.DATA_RATIO_F1F2TOT
               : ((mae.cfg.maxFIELDS==1)
                   ? ms.DATA_F1TOT
                   : ms.DATA_MEAN_F1F2TOT);
       fc= mae.fc;
     
     for(int k=0; k<maxGenes; k++)
     { /* find intensity in the range */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;
       ok= fc.isMIDinWorkingCL(mid);
       if(!ok)
         continue;
       gid= map.mid2gid[mid];
       intens= ms.getDataByGID(gid,mae.useRatioDataFlag,type);
       isInsideFlag= (intens>=intensLB && intens<=intensUB);
       if((isInsideFlag && !outsideRangeFlag) ||
          (!isInsideFlag && outsideRangeFlag))
       {
         resultCL.addGene(gene);
       }
     } /* find intensity in the range */
     
     return(true);
   } /* setIntensHistCLfromIntensRange */
   
   
   /**
    * setRatioHistCLfromF1F2Ratios() - set GeneList of F1/F2 ratios in ratio range
    * @param resultCL RTN: is the resulting gene list
    * @param ms is the Sample to use
    * @param ratioLB is lower bound of intensity
    * @param ratioUB is upper bound of intensity
    * @param outsideRangeFlag test if outside of range else inside range
    * @return true if succeed
    * @see GeneList#addGene
    * @see GeneList#clear
    * @see MaHybridSample#getF1F2Data
    * @see #calcRatioHistVal
    */
   boolean setRatioHistCLfromF1F2Ratios(GeneList resultCL, MaHybridSample ms,
                                        float ratioLB, float ratioUB,
                                        boolean outsideRangeFlag)
   { /* setRatioHistCLfromF1F2Ratios */
     resultCL.clear();               /* clear ratio gene list */
     tempCD_CL.clear();
     
     int eSize= map.maxGenes+1;
     float
       ratio,
       g1, g2,
       f1List[]= new float[eSize],  /* data */
       f2List[]= new float[eSize];
     int nList= ms.getF1F2Data(f1List, f2List, null, tempCD_CL, false, false,false);
     boolean isInsideFlag;
     Gene 
       gene,
       mList[]= mae.fc.workingCL.mList;
     
     for(int i=0; i<nList; i++)
     { /* find ratios in the range */
       g1= f1List[i];
       g2= f2List[i];
       ratio= (float)calcRatioHistVal(g1,g2);
       
       isInsideFlag= (ratio>=ratioLB && ratio<=ratioUB);
       if((isInsideFlag && !outsideRangeFlag) ||
          (!isInsideFlag && outsideRangeFlag))
       {
         gene= mList[i];
         resultCL.addGene(gene);
       }
     } /* find ratios in the range */
     
     return(true);
   } /* setRatioHistCLfromF1F2Ratios */
   
   
   /**
    * setRatioHistCLfromHP_XYRatios() - set GeneList of X/Y ratios in ratio range
    * @param resultCL RTN: is the resulting gene list
    * @param msX is the X Sample to use
    * @param msY is the Y Sample to use
    * @param ratioLB is lower bound of intensity
    * @param ratioUB is upper bound of intensity
    * @param outsideRangeFlag test if outside of range else inside range
    * @return true if succeed
    * @see GeneList#addGene
    * @see GeneList#clear
    * @see #calcRatioHistVal
    * @see #getNormHP_XandYdata
    */
   boolean setRatioHistCLfromHP_XYratios(GeneList resultCL,
                                         MaHybridSample msX,
                                         MaHybridSample msY,
                                         float ratioLB, float ratioUB,
                                         boolean outsideRangeFlag)
   { /* setRatioHistCLfromHP_XYRatios */
     if(hps.nHP<2 || (msX==msY))
       return(false);
     
     resultCL.clear();              /* clear the set */
     tempCD_CL.clear();
     
     int eSize= map.maxGenes+1;
     float
       ratio,
       g1, g2,
       xList[]= new float[eSize],     /* data */
       yList[]= new float[eSize];
     boolean isInsideFlag;
     int nList= getNormHP_XandYdata(xList, yList, null, tempCD_CL,
                                    mae.msX, mae.msY, false, false);
     Gene gene;
     
     for(int i=0; i<nList; i++)
     { /* find ratios in the range */
       g1= xList[i];
       g2= yList[i];
       ratio= (float)calcRatioHistVal(g1,g2);
       isInsideFlag= (ratio>=ratioLB && ratio<=ratioUB);
       if((isInsideFlag && !outsideRangeFlag) ||
          (!isInsideFlag && outsideRangeFlag))
       {
         gene= tempCD_CL.mList[i];
         resultCL.addGene(gene);
       }
     }  /* find ratios in the range */
     
     return(true);
   } /* setRatioHistCLfromHP_XYRatios */
   
   
   /**
    * setRatioHistCLfromHP_XYsetRatios() - set GeneList of X/Y set ratios in ratio range
    * @param resultCL RTN: is the resulting gene list
    * @param ratioLB is lower bound of intensity
    * @param ratioUB is upper bound of intensity
    * @param outsideRangeFlag test if outside of range else inside range
    * @return true if succeed
    * @see GeneList#addGene
    * @see GeneList#clear
    * @see #calcRatioHistVal
    * @see #getNormHP_XandYdata
    */
   boolean setRatioHistCLfromHP_XYsetRatios(GeneList resultCL,
                                            float ratioLB, float ratioUB,
                                            boolean outsideRangeFlag)
   { /* setRatioHistCLfromHP_XYsetRatios */
     if(!mae.useHPxySetDataFlag)
       return(false);
     
     resultCL.clear();              /* clear the set */
     tempCD_CL.clear();
     
     int eSize= map.maxGenes+1;
     float
       ratio,
       g1, g2,
       xList[]= new float[eSize],     /* data */
       yList[]= new float[eSize];
     boolean isInsideFlag;
     int nList= getHP_XandYsetData(xList,yList,null,tempCD_CL,false, false);
     Gene gene;
     
     for(int i=0; i<nList; i++)
     { /* find ratios in the range */
       g1= xList[i];
       g2= yList[i];
       ratio= (float)calcRatioHistVal(g1,g2);
       
       isInsideFlag= (ratio>=ratioLB && ratio<=ratioUB);
       if((isInsideFlag && !outsideRangeFlag) ||
          (!isInsideFlag && outsideRangeFlag))
       {
         gene= tempCD_CL.mList[i];
         resultCL.addGene(gene);
       }
     } /* find ratios in the range */
     
     return(true);
   } /* setRatioHistCLfromHP_XYsetRatios */
   
   
   /**
    * lookupHPcoords() - lookup closest gene name by (x,y) in sample pseudoarray and set current gene
    * if < distThreshold away, else return "".
    * If it finds it, it also sets the isValidObj flag and updates
    * (objField, objGrid, objRow, objCol) and ( objGID, objGIDG, objMID).
    * It also sets (objX,objY) to (xC[], yC[]) values.
    * It looks up the associated GeneBank,  dbEST data in dbESTid,
    * cloneId, ESTname, GeneBankId (Acc)
    * @param x coordinage
    * @param y coordinates
    * @param distThreshold of distance of (x,y) to nearest spot in sample
    * @param useFieldNameFlag to generate the field name (if > 1 field)
    * @param ms is sample to test coordinates
    * @return string of coordinates data
    * @see GridCoords#cvtGID2str
    * @see ScrollableImageCanvas#repaint
    * @see #setOBJtoGID
    */
   String lookupHPcoords(int x, int y, int distThreshold,
                         boolean useFieldNameFlag, MaHybridSample ms)
   { /* lookupHPcoords */
     int
       maxSpots= map.maxSpots,
       dTsq= (distThreshold*distThreshold),
       dX, dY,
       distSq;
     String maCoords= "";
     Point
       xy,
       xyGid[]= ms.xyCQ;
     
     isValidObjFlag= false;
     
     for(int gid=0; gid<maxSpots; gid++)
     { /* search for closest point */
       if(map.gid2mid[gid]==-1)
         continue;                         /* Bogus GID value */
       xy= xyGid[gid];
       if(xy==null || xy.x<1 || xy.y<1)
         continue;
       dX= (x - xy.x);
       dY= (y - xy.y);
       distSq= (dX*dX + dY*dY);
       
       if(distSq<dTsq)
       { /* found better estimate */
         maCoords= mae.grcd.cvtGID2str(gid, useFieldNameFlag);
         dTsq= distSq;   /* keep best estimate */
         
         setOBJtoGID(gid,ms); /* set the current gene to the specifid GID */
         
         isValidObjFlag= true;
         /* [CHECK] Do we short circuit the search?]*/
       } /* found better estimate */
     } /* search for closest point */
     
     if(isValidObjFlag)
       mae.is.repaint();   /* only if found one */
     
     return(maCoords);
   } /* lookupHPcoords */
   
   
   /**
    * setOBJtoGID() - set the current gene xxxOBJ values to the specified GID.
    * It updates:
    *  (objField, objGrid, objRow, objCol, objGID, objGIDG, objMID, objX, objY).
    */
   void setOBJtoGID(int gid, MaHybridSample ms)
   { /* setOBJtoGID */
     objField= map.gid2fgrc[gid].f;  /* save optimal spot */
     objGrid= map.gid2fgrc[gid].g;
     objRow= map.gid2fgrc[gid].r;
     objCol= map.gid2fgrc[gid].c;
     objGID= gid;
     
     /* Lookup and save the Gang gid and mid */
     objGIDG= map.gidToGangGid[gid];
     objMID= map.gid2mid[gid];
     
     if(ms!=null)
     { /* latch it to PseudoImage coordinante */
       objX= ms.xyCQ[gid].x;
       objY= ms.xyCQ[gid].y;
     }
     
     mae.is.siCanvas.toPoint= new Point(objX,objY);
     mae.is.siCanvas.xyObj= mae.is.siCanvas.toPoint;
   } /* setOBJtoGID */
   
   
   /**
    * setObjCoordFromMaster_ID() - set gene xxxOBJ values from Master_ID name
    * @param masterID is the Master_ID for the current gene
    * @see GipoTable#getMIDfromMaster_IDhashTable
    * @see #setObjCoordFromMID
    */
   void setObjCoordFromMaster_ID(String masterID)
   { /* setObjCoordFromMaster_ID */
     int
     mid= mae.gipo.getMIDfromMaster_IDhashTable(masterID);
     setObjCoordFromMID(mid,null);
   } /* setObjCoordFromMaster_ID */
   
   
   /**
    * setObjCoordFromMID() - set gene xxxOBJ values from MID for current HP sample.
    * Also update the pseudoarray image and any popup plots.
    * @param mid gene MID to set as the current gene (obj)
    * @param sObj to pass onto updateCurGene()
    * @see PopupRegistry#updateCurGene
    * @see ScrollableImageCanvas#setObjPos
    * @see ScrollableImageCanvas#repaint
    */
   void setObjCoordFromMID(int mid, Object sObj)
   { /* setObjCoordFromMID */
     if(mid==-1 || recursionFlag)
       return;
     
     /* look it up and set current object */
     int gid= map.mid2gid[mid];
     MaHybridSample ms= mae.ms;
     
     isValidObjFlag= true;
     objMID= mid;
     objGID= gid;
     objGIDG= map.gidToGangGid[gid];
     objField= map.gid2fgrc[gid].f;  /* save optimal spot */
     objGrid= map.gid2fgrc[gid].g;
     objRow= map.gid2fgrc[gid].r;
     objCol= map.gid2fgrc[gid].c;
     
     /* Latch it to PseudoImage coordinante */
     objX= ms.xyCQ[gid].x;
     objY= ms.xyCQ[gid].y;
     
     mae.is.siCanvas.toPoint= new Point(objX,objY);
     mae.is.siCanvas.xyObj= mae.is.siCanvas.toPoint;
     mae.is.setObjPos(objX, objY);
     
     mae.is.repaint();          /* update current HP in IMAGE plot */
     
     recursionFlag= true;       /* keep from coming back */
     mae.pur.updateCurGene(mid, 0, sObj);
     recursionFlag= false;
   } /* setObjCoordFromMID */
   
   
   /**
    * getNormHP_XandYdata() - get normalized list spot (HP-X,HP-Y) data passing data 
    * filter ml or mae.fc.workingCL. 
    * <P>
    * If msX==msY, then get msX.f1 for msX and msX.f2 for msY
    * else get (msX.f1+msX.f2)/2 for msX and (msY.f1+msY.f2)/2 for msY.
    * The assumption is made that grid structure is same for both msX and msY.
    * Also set the (min/max)(DataHP_X,DataHP_Y) values.
    *
    * Return the number of points nList in the list[0:nList-1].
    * @param xList[max # genes to get] to return HP-X values
    * @param yList[max # genes to get] to return optional HP-Y values
    * @param propList[max # genes to get] gene property values
    * @param ml is the GeneList to save genes or to filter by.
    *        To filter by all genes, set ml to set of all genes.
    * @param msX is HP-X sample to use
    * @param msY is HP-Y sample to use - optional
    * @param setPropsFlag set the propList[] data from fc.workingCL as
    *        IS_FILTERED or IS_NOT_FILTERED. If filterByMLflag, then copy
    *        the gene.prop field.
    * @param filterByMLflag if true, then filter by ml GeneList. To filter
    *         by all genes, set ml to set of all genes mae.gct.allGenesCL
    * @return the number of genes pushed. If the list length is shorter than
    *        the number of genes to be copied, then do not save the remaining
    *        genes, but return the total count so the caller can test if they got
    *        them all if the return size equals the (xList[], yList[]) sizes.
    * @see Filter#isMIDinWorkingCL
    * @see Gene#setGeneData
    * @see GeneList#clearNull
    * @see GeneList#setGene
    * @see GeneBitSet#isItemInGeneBitSet
    * @see MaHybridSample#getDataByGID
    * @see MaHybridSample#getF1F2Data
    * @see #getHP_XandYsetData
    */
   int getNormHP_XandYdata(float xList[], float yList[], int propList[],
                           GeneList ml, MaHybridSample msX, MaHybridSample msY,
                           boolean setPropsFlag, boolean filterByMLflag)
   { /* getNormHP_XandYdata */
     if(msX==null || xList==null)
       return(0);        /* Can't get blood from a stone ... */
     int
       nFiltered= 0,
       nList= 0;
     boolean
       ok,
       getYvaluesFlag= (msY!=null && yList!=null),
       useFilterByMLflag= (filterByMLflag && ml!=null),
       doSetPropsFlag= (setPropsFlag && propList!=null);
     
     if(ml!=null && !filterByMLflag)
       ml.clearNull();
     minDataHP_X= 100000000.0F;
     minDataHP_Y= 100000000.0F;
     maxDataHP_X= -100000000.0F;
     maxDataHP_Y= -100000000.0F;
     
     /* [CHECK] logic if maxFIELDS is 1 */
     // if(maxFIELDS==1)
     //  return(0);                /* no dual field data */
     
     if(mae.useHPxySetDataFlag)
     {
       nList= getHP_XandYsetData(xList, yList, propList, ml,
       setPropsFlag, false);
       return(nList);
     }
     
     if(msX==msY && getYvaluesFlag)
     { /* just compare right & left field data */
       /* [CHECK] if(maxFIELDS==1)
        */
       nList= msX.getF1F2Data(xList, yList, propList, ml, setPropsFlag,
                              mae.useRatioDataFlag, false);
       maxDataHP_X= msX.maxDataF1;
       maxDataHP_Y= msX.maxDataF2;
       return(nList);
     }
     
     float
       ratio,
       gX,
       gY= 0.0F;                       /* totQ - bkgrdQ */
     Gene
       gene,
       mList[]= map.midStaticCL.mList;
     int
       gid,
       mid,
       maxListSize= xList.length, /* xList and yList should be the same */
       maxGenes= map.maxGenes,
       maxFIELDS= mae.cfg.maxFIELDS,
       type= (mae.useRatioDataFlag)
                ? mae.ms.DATA_RATIO_F1F2TOT : mae.ms.DATA_F1TOT;
     Filter fc= mae.fc;
     
     if(filterByMLflag)
     { /* just get genes in ml GeneList */
       mList= ml.mList;
       maxGenes= ml.length;
     }
     
     for(int k=0; k<maxGenes; k++)
     { /* get data for X and Y HPs and F1 and F2 fields */
       gene= mList[k]; 
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
                
       mid= gene.mid;
       ok= (filterByMLflag)
              ? ml.bitSet.isItemInGeneBitSet(mid)
              : fc.isMIDinWorkingCL(mid);
       
       /* If not using propList[], only return spots within Filter.
        * If using propList[], then will set IS_ or IS_NOT_FILTERED
        * there.
        */
       if(!setPropsFlag && !ok)
         continue;                  /* ignore the gene */
       
       gid= map.mid2gid[mid];
       if(gid==-1)
         continue;
       
       if(maxFIELDS>1)
       { /* has replicate spots/gene */
         /* normalized (totQ - bkgrdQ) */
         gX= msX.getDataByGID(gid, mae.useRatioDataFlag,
                              msX.DATA_MEAN_F1F2TOT);
         if(getYvaluesFlag)
           gY= msY.getDataByGID(gid,mae.useRatioDataFlag,
                                msY.DATA_MEAN_F1F2TOT);
       }
       else
       { /* use single spot or ratio data */
         gX= msX.getDataByGID(gid, mae.useRatioDataFlag, type);
         if(getYvaluesFlag)
           gY= msY.getDataByGID(gid, mae.useRatioDataFlag, type);
       }
       
       if(gX<minDataHP_X)
         minDataHP_X= gX;    /* compute min */
       if(gY<minDataHP_Y)
         minDataHP_Y= gY;    /* compute min */
       if(gX>maxDataHP_X)
         maxDataHP_X= gX;    /* compute max */
       if(gY>maxDataHP_Y)
         maxDataHP_Y= gY;    /* compute max */
       
       /* Push into the list */
       if(nList < maxListSize)
       { /* only save up to size of supplied lists */
         xList[nList]= gX;
         if(getYvaluesFlag)
           yList[nList]= gY;
         if(doSetPropsFlag)
         {
           if(filterByMLflag)
             propList[nList]= gene.properties;
           else if(setPropsFlag)
             propList[nList]= (ok)
                               ? Gene.C_IS_FILTERED
                               : Gene.C_IS_NOT_FILTERED;
         }
       } /* only save up to size of supplied lists */
       
       ratio= (gY!=0) ? (float)(gX/gY) : 0.0F;
       
       /* Force it into position so can use with graphics */
       gene.setGeneData(ratio, gX, gY);
       if(ml!=null)
         ml.setGene(gene,nList);
       
       nList++;         /* push counter */
       if(ok)
         nFiltered++;    /* count for debugging, could return in [future] */
     } /* get data for X and Y HPs and F1 and F2 fields */
     
     return(nList);
   } /* getNormHP_XandYdata */   
   
  
   /**
    * getRawHP_XandYdata() - get list raw spot (HP-X,HP-Y) data passing data filter ml or
    * mae.fc.workingCL. If msX==msY, then get msX.f1 for msX and msX.f2 for msY
    * else get (msX.f1+msX.f2)/2 for msX and (msY.f1+msY.f2)/2 for msY.
    * The assumption is made that grid structure is same for both msX and msY.
    * Also set the (min/max)(DataHP_X, DataHP_Y) values. 
    * It will also return the background data values if the xBkgd[] and/or yBkgd[]
    * are not null.
    *<P>
    * NOTE: This method does NOT get 'set' data since it is ambiguous how
    * to "average" raw data (that is why we normalize it :-). So this
    * method ignores mae.useHPxySetDataFlag. Use getNormHP_XandYdata() to
    * set mean set data.
    * <P>
    * Return the number of points nList in the list[0:nList-1].
    * @param xData[max # genes to get] to return HP-X intensity values
    * @param yData[max # genes to get] to return optional HP-Y intensity values
    * @param xBkgd[max # genes to get] to return HP-X background values
    * @param yBkgd[max # genes to get] to return optional HP-Y background values
    * @param propList[max # genes to get] gene property values
    * @param ml is the GeneList to save genes or to filter by.
    *        To filter by all genes, set ml to set of all genes.
    * @param msX is HP-X sample to use
    * @param msY is HP-Y sample to use - optional
    * @param setPropsFlag set the propList[] data from fc.workingCL as
    *        IS_FILTERED or IS_NOT_FILTERED. If filterByMLflag, then copy
    *        the gene.prop field.
    * @param filterByMLflag if true, then filter by ml GeneList. To filter
    *         by all genes, set ml to set of all genes mae.gct.allGenesCL
    * @return the number of genes pushed. If the list length is shorter than
    *        the number of genes to be copied, then do not save the remaining
    *        genes, but return the total count so the caller can test if they got
    *        them all if the return size equals the (xData[], yData[]) sizes.
    * @see Filter#isMIDinWorkingCL
    * @see Gene#setGeneData
    * @see GeneList#clearNull
    * @see GeneList#setGene
    * @see GeneBitSet#isItemInGeneBitSet
    * @see MaHybridSample#getDataByGID
    * @see MaHybridSample#getF1F2Data
    * @see #getHP_XandYsetData
    */
   int getRawHP_XandYdata(float xData[], float yData[], 
                          float xBkgd[], float yBkgd[],
                          int propList[], GeneList ml, 
                          MaHybridSample msX, MaHybridSample msY,
                          boolean setPropsFlag, boolean filterByMLflag)
   { /* getRawHP_XandYdata */
     if(msX==null)
       return(0);        /* Can't get blood from a stone ... */
     boolean
       ok,
       getYvaluesFlag= (msY!=null),
       getXbkgrdFlag= (xBkgd!=null),
       getYbkgrdFlag= (yBkgd!=null),
       useGlobalBkgrdFlag= (msX.bkgrdSpotQ!=null),
       getF1F2Flag= (msX==msY && getYvaluesFlag),
       useFilterByMLflag= (filterByMLflag && ml!=null),
       doSetPropsFlag= (setPropsFlag && propList!=null);
     
     if(ml!=null && !filterByMLflag)
       ml.clearNull();
     minDataHP_X= 100000000.0F;
     minDataHP_Y= 100000000.0F;
     maxDataHP_X= -100000000.0F;
     maxDataHP_Y= -100000000.0F;     
    
     float
       ratio,
       gX= 0.0F,
       gY= 0.0F,
       bX= 0.0F,
       bY= 0.0F;
     Gene
       gene,
       mList[]= map.midStaticCL.mList;
     int
       nList= 0,
       gid,
       gid2,
       mid,
       gidToGangGid[]= map.gidToGangGid,
       maxListSize= xData.length, /* xData and yData should be the same */
       maxGenes= map.maxGenes,
       maxFIELDS= mae.cfg.maxFIELDS,
       type= (mae.useRatioDataFlag)
                ? mae.ms.DATA_RATIO_F1F2TOT : mae.ms.DATA_F1TOT;
     Filter fc= mae.fc;
     
     if(filterByMLflag)
     { /* just get genes in ml GeneList */
       mList= ml.mList;
       maxGenes= ml.length;
     }       
     
     for(int k=0; k<maxGenes; k++)
     { /* get data for X and Y HPs and F1 and F2 fields */
       gene= mList[k];  
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;
       ok= (filterByMLflag)
              ? ml.bitSet.isItemInGeneBitSet(mid)
              : fc.isMIDinWorkingCL(mid);
       
       /* If not using propList[], only return spots within Filter.
        * If using propList[], then will set IS_ or IS_NOT_FILTERED
        * there.
        */
       if(!setPropsFlag && !ok)
         continue;                  /* ignore the gene */
       
       gid= map.mid2gid[mid];
       if(gid==-1)
         continue;
       
       if(getF1F2Flag)
       { /* Get F1 into X and F2 into Y for msX */ 
         gid2= gidToGangGid[gid];
         gX= msX.totQ[gid]; 
         gY= msX.totQ[gid2];
         if(getXbkgrdFlag)
           bX= (useGlobalBkgrdFlag) 
                ? msX.bkgrdQ : msX.bkgrdSpotQ[gid];
         if(getYbkgrdFlag)
           bY= (useGlobalBkgrdFlag) 
                ? msX.bkgrdQ : msX.bkgrdSpotQ[gid2];
       } /* Get F1 into X and F2 into Y for msX */ 
       
       else if(maxFIELDS>1 && getYvaluesFlag)
       { /* get mean of X and Y samples with duplicate F1 F2 */   
         gid2= gidToGangGid[gid];
         gX= (msX.totQ[gid] + msX.totQ[gid2])/2;
         gY= (msY.totQ[gid] + msY.totQ[gid2])/2;
         if(getXbkgrdFlag)
           bX= (useGlobalBkgrdFlag) 
                ? msX.bkgrdQ : (msX.bkgrdSpotQ[gid] + msX.bkgrdSpotQ[gid2])/2;
         if(getYbkgrdFlag)
           bY= (useGlobalBkgrdFlag) 
                ? msY.bkgrdQ : (msY.bkgrdSpotQ[gid] + msY.bkgrdSpotQ[gid2])/2;
       } /* get mean of X and Y samples with duplicate F1 F2 */
       
       else if(maxFIELDS>1 && !getYvaluesFlag)
       { /* get mean of X samples with duplicate F1 F2 */   
         gid2= gidToGangGid[gid];
         gX= (msX.totQ[gid] + msX.totQ[gid2])/2;
         if(getXbkgrdFlag)
           bX= (useGlobalBkgrdFlag) 
                ? msX.bkgrdQ : (msX.bkgrdSpotQ[gid] + msX.bkgrdSpotQ[gid2])/2;
       } /* get mean of X and Y samples with duplicate F1 F2 */ 
       
       else if(maxFIELDS==1 && !mae.useRatioDataFlag)
       { /* get X and Y intensity samples with single spots  */  
         gX= msX.totQ[gid];
         gY= msY.totQ[gid];
         if(getXbkgrdFlag)
           bX= (useGlobalBkgrdFlag) 
                ? msX.bkgrdQ : msX.bkgrdSpotQ[gid];
         if(getYbkgrdFlag)
           bY= (useGlobalBkgrdFlag) 
                ? msY.bkgrdQ : msY.bkgrdSpotQ[gid];
       }  /* get X and Y intensity samples with single spots  */ 
       
       else if(maxFIELDS==1 && getYvaluesFlag && mae.useRatioDataFlag)
       { /* get X and Y ratio data samples with (F1,F2)=(C3/Cy5) */   
         gid2= gidToGangGid[gid];
         try
         {
           gX= (msX.totQ[gid] / msX.totQ[gid2])/2;
           gY= (msY.totQ[gid] / msY.totQ[gid2])/2;
         }
         catch(Exception e)
         {
           gX= 0.0F;
           gY= 0.0F;
         }
         if(getXbkgrdFlag)
           bX= (useGlobalBkgrdFlag) 
                ? msX.bkgrdQ : (msX.bkgrdSpotQ[gid] + msX.bkgrdSpotQ[gid2])/2;
         if(getYbkgrdFlag)
           bY= (useGlobalBkgrdFlag) 
                ? msY.bkgrdQ : (msY.bkgrdSpotQ[gid] + msY.bkgrdSpotQ[gid2])/2;
       }  /* get X and Y ratio data samples with (F1,F2)=(C3/Cy5) */ 
       
       else if(maxFIELDS==1 && !getYvaluesFlag && mae.useRatioDataFlag)
       { /* get X ratio data samples with (F1,F2)=(C3/Cy5) */   
         gid2= gidToGangGid[gid];
         try
         {
           gX= (msX.totQ[gid] / msX.totQ[gid2])/2;
         }
         catch(Exception e)
         {
           gX= 0.0F;
         }
         if(getXbkgrdFlag)
           bX= (useGlobalBkgrdFlag) 
                ? msX.bkgrdQ : (msX.bkgrdSpotQ[gid] + msX.bkgrdSpotQ[gid2])/2;
       }  /* get X and Y ratio data samples with (F1,F2)=(C3/Cy5) */  
              
       /* Push into the list */
       if(nList < maxListSize)
       { /* only save up to size of supplied lists */
         xData[nList]= gX;
         if(getXbkgrdFlag)
           xBkgd[nList]= bX;
         if(getYvaluesFlag)
         {
           yData[nList]= gY;           
           if(getXbkgrdFlag)
             yBkgd[nList]= bY;
         }
         if(doSetPropsFlag)
         {
           if(filterByMLflag)
             propList[nList]= gene.properties;
           else if(setPropsFlag)
             propList[nList]= (ok)
                               ? Gene.C_IS_FILTERED
                               : Gene.C_IS_NOT_FILTERED;
         }
       } /* only save up to size of supplied lists */
       
       nList++;         /* push counter */
     } /* get data for X and Y HPs and F1 and F2 fields */
     
     return(nList);
   } /* getRawHP_XandYdata */
   
   
   /**
    * getHP_XandYsetData() - get list spot (HP-X,HP-Y) sets data passing the data filter
    * mae.fc.workingCL. If msX==msY, then get msX.f1 for msX and msX.f2 for msY
    * else get (msX.f1+msX.f2)/2 for msX and
    * (msY.f1+msY.f2)/2 for msY.
    * The assumption is made that grid structure is same for both
    * msX and msY.  It computes and returns the number of points nList
    * in the list[1:nList].
    * @param xList[max # genes to get] HP-X values
    * @param yList[max # genes to get] return optional HP-Y values
    * @param propList[max # genes to get] optional property values
    * @param ml is the GeneList to save genes in or filter by.To filter
    *         by all genes, set ml to set of all genes mae.gct.allGenesCL
    * @param setPropsFlag set the propList[] data from fc.workingCL as
    *        IS_FILTERED or IS_NOT_FILTERED
    * @param filterByMLflag if true, then filter by ml
    * @return the number of genes pushed. If the list length is shorter than
    *         the number of genes to be copied, then do not save the remaining
    *         genes, but return the total count so the caller can test if they got
    *         the all if them return size equals the (xList[], yList[]) sizes.
    * @see Filter#isMIDinWorkingCL
    * @see Gene#setGeneData
    * @see GeneList#clear
    * @see GeneList#clearNull
    * @see GeneList#setGene
    * @see GeneBitSet#isItemInGeneBitSet
    * @see HPxyData#setupDataStruct
    * @see HPxyData#updateDataAndStat
    */
   int getHP_XandYsetData(float xList[], float yList[], int propList[],
                          GeneList ml, boolean setPropsFlag,
                          boolean filterByMLflag )
   { /*getHP_XandYsetData*/
     if(!mae.useHPxySetDataFlag || xList==null)
       return(0);        /* Can't get blood from a stone ... */
     
     hpXYdata.setupDataStruct(mae.useHPxySetDataFlag);
     
     int
       nList= 0,
       nX= hpXYdata.nX,
       nY= hpXYdata.nY;
     boolean
       getYvaluesFlag= (yList!=null && nY>0),
       doFilterByMLflag= (filterByMLflag && ml!=null),
       doSetPropsFlag= (setPropsFlag && propList!=null);
     
     if(nX==0 || nY==0 && getYvaluesFlag)
     { /* no HP_XYsets*/
       nList= 0;
       return(nList);
     }
     
     if(ml!=null && !filterByMLflag)
       ml.clearNull();
     minDataHP_X= 10000000.0F;
     minDataHP_Y= 10000000.0F;
     maxDataHP_X= -10000000.0F;
     maxDataHP_Y= -10000000.0F;
     
     boolean
       ok,
       inGeneListFlag= true;
     Gene
       gene,
       mList[]= map.midStaticCL.mList;
     int
       maxGenes= map.maxGenes,
       gid1,
       gid2,
       mid,
       maxListSize= xList.length; /* xList and yList should be the same */
     float
       gX,
       gY= 0.0F,
       ratio;
     Filter fc= mae.fc;
     
     if(filterByMLflag)
     { /* just get genes in ml GeneList */
       mList= ml.mList;
       maxGenes= ml.length;
     }
     
     for(int k=0;k<maxGenes; k++)
     { /* get data HP-X/-Y sets */
       gene= mList[k];
       if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
         continue;            /* ignore bogus spots */
       
       mid= gene.mid;
       ok= (filterByMLflag)
             ? ml.bitSet.isItemInGeneBitSet(mid)
             : fc.isMIDinWorkingCL(mid);
       
      /* If not using propList[], only return spots within Filter.
       * If using propList[], then will set IS_ or IS_NOT_FILTERED
       * there.
       */
       if(!setPropsFlag && !ok)
         continue;
       
       if(!hpXYdata.updateDataAndStat(mid))
         continue;
       
       gX= hpXYdata.mnXdata;
       if(getYvaluesFlag)
         gY= hpXYdata.mnYdata;
       
       /* Push into the list */
       if(nList < maxListSize)
       { /* only save up to size of supplied lists */
         xList[nList]= gX;
         if(getYvaluesFlag)
           yList[nList]= gY;
         if(doSetPropsFlag)
         {
           if(filterByMLflag)
             propList[nList]= gene.properties;
           else if(setPropsFlag)
             propList[nList]= (ok)
                                ? Gene.C_IS_FILTERED
                                : Gene.C_IS_NOT_FILTERED;
         }
       } /* only save up to size of supplied lists */
       
       if(gX<minDataHP_X)
         minDataHP_X= gX;    /* compute min */
       if(gY<minDataHP_Y)
         minDataHP_Y= gY;     /* compute min */
       if(gX>maxDataHP_X)
         maxDataHP_X= gX;    /* compute max */
       if(gY>maxDataHP_Y)
         maxDataHP_Y= gY;    /* compute max */
       
       ratio= (gY!=0) ? (float)(gX/gY) : 0.0F;
       
       /* force it into position so can use with graphics */
       gene.setGeneData(ratio, gX, gY);
       if(ml!=null)
         ml.setGene(gene,nList);
       
       nList++;               /* push counter */
     } /* get data HP-X/-Y sets */
     
     return(nList);
   } /* getHP_XandYsetData */
   
   
   /**
    * calAllSamples() - compute (meanCalDNA, stdDevCalDNA) calibration for all samples
    * @return true if successful.
    * @see MaHybridSample#calcMeanCalDNA
    * @see MaHybridSample#calcMeanUseNormGeneSet
    */
   boolean calAllSamples()
   { /* calAllSamples */
     float
       sumMeanUseGeneSet= 0.0F,
       sumMeanCalDNA= 0.0F;
     int
       hpIndex[]= new int[hps.nHP],
       nUsed= 0;
     MaHybridSample ms;
     
    /* [1] Compute calibration means,std-dev, # used and index array
     * to access them.
     */
     for(int i=1;i<=hps.nHP;i++)
     {
       ms= hps.msList[i];
       if(ms==null || !ms.dataAvailFromQuantFileFlag ||
          !ms.hasValidDataFlag)
         continue;                   /* ignore it */
       
       if(mae.normByCalDNAflag)
       {
         ms.calcMeanCalDNA();      /* compute mean and std-dev */
         sumMeanCalDNA += ms.meanCalDNA;
         ms.scaleCalDNA= 1.0F;
       }
       if(mae.normByGeneSetFlag)
       {
         ms.calcMeanUseNormGeneSet();  /* compute mean and std-dev */
         sumMeanUseGeneSet += ms.meanUseGeneSet;
         ms.scaleUseGeneSet= 1.0F;
       }
       
       hpIndex[nUsed]= i;
       nUsed++;
     }
     
     /* [2] Compute grand mean and scale to that for basic scale
      * factor.
     */
     grandMeanCalDNA= 0.0F;
     grandMeanUseGeneSet= 0.0F;
     if(nUsed==0)
       return(false);
     
     grandMeanCalDNA= sumMeanCalDNA/nUsed;
     grandMeanUseGeneSet= sumMeanUseGeneSet/nUsed;
     
     /* [3] Estimate calibration coefficient for each sample */
     float
       msmRI,
       maxScaledMaxRI= 0.0F;  /* max value of maxRI scaled by
                               * ms.scaleCalDNA */
     int
       r, j;
     
     for(r=0;r<nUsed;r++)
     { /* compute scale factor */
       j= hpIndex[r];
       ms= hps.msList[j];
       
     /* Compute scale factors for scaling by CalDNA.
      * If scaling to 65K, then only the HP with highest
      * but max for all HPs is 65K */
       ms.scaleCalDNA= grandMeanCalDNA/ms.meanCalDNA;
       ms.scaleUseGeneSet= grandMeanUseGeneSet/ms.meanUseGeneSet;
       
       if(mae.normByGeneSetFlag)
         msmRI= ms.scaleUseGeneSet*ms.maxRI;
       else
         msmRI= ms.scaleCalDNA*ms.maxRI;	/*global max*/
       
       if(maxScaledMaxRI<msmRI)
         maxScaledMaxRI= msmRI;  /* Math.max(msmRI, maxScaledMaxRI) */
      /*
      if(mae.CONSOLE_FLAG)
      {
        mae.fio.logMsgln("CDB-CAHP.1 r="+r+
                         " scaleCalDNA="+ms.scaleCalDNA+
                         " scaleUsetGeneSet="+ms.scaleUsetGeneSet+
                         " maxRI="+ms.maQ.maxRI+
                         " msmRI="+msmRI+
                         " maxScaledMaxRI="+maxScaledMaxRI);
       }
       */
     } /* compute scale factor */
     
    /* [3.1] Estimate scaleCalDNAto65K for each sample. This
     * rescales all of the factors so the new scale factor
     * times the raw intensity value comes out to be 65K.
     */
     float reScaleFactor= mae.MAX_INTENSITY/maxScaledMaxRI;
     /*
     if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("CDB-CAHP.2 reScaleFactor="+reScaleFactor);
     */
     
     for(r=0;r<nUsed;r++)
     { /* compute rescaled factor */
       j= hpIndex[r];
       ms= hps.msList[j];
       
       if(mae.normByGeneSetFlag)
         ms.scaleUseGeneSetTo65K= (reScaleFactor*ms.scaleUseGeneSet);
       else
         ms.scaleCalDNAto65K= (reScaleFactor*ms.scaleCalDNA);
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("CDB-CAHP.13 r="+r+" scaleCalDNAto65K="+ms.scaleCalDNAto65K);        
      */
     } /* compute rescaled factor */
     
     return(true);
   } /* calAllSamples */
   
   
   /**
    * recalcNorms() - recompute normalizations for all samples in the hps.msList[].
    * Recalculate intensity scale factor extrema depending on
    * normalization mode.
    * @param sMethod is the name of the current normalization method
    * @param recomputeNormalizationExtremaFlag if set
    * @see MAExplorer#repaint
    * @see MaHybridSample#calcIntensScaleExtrema
    * @see PopupRegistry#updateFilter
    * @see StateScrollers#regenerateScrollers
    * @see StateScrollers#setRangeMaps
    * @see Util#showMsg
    * @see #calAllSamples
    */
   void recalcNorms(String sMethod, boolean recomputeNormalizationExtremaFlag)
   { /* recalcNorms */
     /*[TODO] check if working correctly with PopupRegistry* callbacks. */
     
     MenuBarFrame mbf= mae.mbf;
     SampleSets hps= mae.hps;
     
     /* [1] Get plugin info if active */
     String normPluginName= null;  /* null means, not active */
     MAEPlugin.Normalization normPluginObj= null;
     if(mae.mja.getNormalizationState())
     { /* NormalizationPlugin support - keep norm specific vars. in plugin */
       normPluginObj= mae.mja.getActiveNormalization();
       normPluginName= 
           ((MAEPlugin.analysis.NormalizationPlugin) normPluginObj).getPluginName();
      if(mae.DBUG_NORM_PLUGIN)
        System.out.println("CD-RCN.1 normPluginName="+normPluginName);
     }    
    
     /* [2] Set global isZscore flag for doing different Zdiff instead of 
      * ratio calculations. 
      *[DEPRICATED] moved to EventMenu.
     */
     if(mae.NEVER)
     { /* reset Zscore flag */
       boolean
         newZscoreFlag= (mae.normByZscoreFlag ||
                         mae.normByLogMedianFlag ||
                         mae.normByZscoreMeanStdDevLogFlag ||
                         mae.normByZscoreMeanAbsDevLogFlag);
       if(mae.isZscoreFlag!=newZscoreFlag)
       { /* If changed, adjust any sliders that depend on being in Zscore mode */
         mae.isZscoreFlag= newZscoreFlag;
         //mae.stateScr.adjustRatioOrZscoreCounters();
       }
     } /* reset Zscore flag */
    
     /* [3] Tell user which Normalization method we are using */
     mae.normName= (normPluginName!=null)
                      ? normPluginName
                      : sMethod;                  /* saved in state */
     mae.normNameDisp= "Norm.: " +
                       ((recomputeNormalizationExtremaFlag)
                          ? (sMethod+" intensity")
                          : "none");
     Util.showMsg(mae.normNameDisp);
     
     /* [4] Recompute normalization extrema and perform any specific 
      * updates for the method.
      */
     if(recomputeNormalizationExtremaFlag)
       for(int h=1;h<=hps.nHP;h++)
       { /* process each sample */
         ///ms= hps.msList[h];
         
         if(mae.normByZscoreFlag)
         {
         }
         
         else if(mae.normByMedianFlag)
         {
         }
         
         else if(mae.normByLogMedianFlag)
         {
         }
         
         else if(mae.normByZscoreMeanAbsDevLogFlag)
         {
         }
         
         else if(mae.normByZscoreMeanAbsDevLogFlag)
         {
         }
         
         else if(h==1 && (mae.normByGeneSetFlag || mae.normByCalDNAflag ||
                          mae.normByHousekeepGenesFlag))
         { /* process sets of genes */
           if(h==1)
           {
             calAllSamples();
             /* compute (meanCalDNA, stdDevCalDNA) for all Samples. */
             //String
             //  sMsg3= "Mean calib. intensity= "+
             //         (Util.cvf2s(mae.ms.meanCalDNA,3 +
             //         "+-" +
             //         Util.cvf2s(mae.ms.stdDevCalDNA,3);
             //Util.showMsg2(sMsg3);
           }
         }
         
         else if(mae.scaleDataToMaxIntensFlag)
         {
         }
         
         else if (h==1 && normPluginObj!=null)
         { /* NormalizationPlugin support - keep norm specific vars. in plugin */
           /* This is called - even if it is a no-op */
           if(mae.DBUG_NORM_PLUGIN)
             System.out.println("CD-RCN.2 normPluginObj");
           normPluginObj.recalcNormalizationExtrema();
         }          
         
         else if(mae.DBUG_GENERIC_NORM_PLUGIN && mae.testGenericNormPluginFlag)
         { /* if debugging GenNormalizationPlugin using direct class */
           if(mae.DBUG_GENERIC_NORM_PLUGIN)
             System.out.println("CD-RCN.3 gnp");
           mae.gnp.recalcNormalizationExtrema();
         }
         
         else
         {  /* if switch background on or off */
         }
         
         /* [TODO] [2.1] possibly recalc other parameters */
         hps.msList[h].calcIntensScaleExtrema();
       }
     
     /* [5] Set normalization dependent scrollers based normalization
      * method and regenerate the scrollers if need be.
      */
     mae.stateScr.setRangeMaps();
     mae.stateScr.regenerateScrollers(false);
     
     /* [6] Update filter and repost new data. */
     mae.updatePseudoImgFlag= true;
     mae.pur.updateFilter(mae.fc.workingCL);
     mae.repaint();
   } /* recalcNorms */
   
   
   /**
    * calDNAhybSamplesTable() - calc. (meanCalDNA, stdDevCalDNA) Table for all samples
    * @return SimplleTable if successful else null.
    * @see MaHybridSample
    * @see PopupRegistry#updateFilter
    * @see SimpleTable
    * @see Util#cvf2s
    * @see #calAllSamples
    */
   SimpleTable calDNAhybSamplesTable()
   { /* calDNAhybSamplesTable */
     int
       hpIndex[]= new int[hps.nHP+1],
       nUsed= 0;
     String fieldList[]= {"Sample_ID",
                         "Mean CalDNA Intensity", "StdDev CalDNA Intensity",
                         "CalDNA scale factor",
                         "Min RawIntensity", "Max RawIntensity",
                         "Min Scaled Intensity", "Max Scaled Intensity"
                         };
     
    /* [1] Compute # used and index array to access active HPs.*/
     MaHybridSample ms;
     for(int i=1;i<=hps.nHP;i++)
     {
       ms= hps.msList[i];
       if(ms==null || !ms.dataAvailFromQuantFileFlag ||
       !ms.hasValidDataFlag)
         continue;    /* ignore it */
       hpIndex[nUsed]= i;
       nUsed++;
     }
     
     if(nUsed==0)
       return(null);
     int
       nRowsNeeded= Math.max(nUsed, ShowSpreadsheetPopup.MAX_ROWS_TO_SHOW);
     nRowsNeeded++;                   /* add 1 */
     int
       nRows= nRowsNeeded,
       nCols= fieldList.length;
     
     /* [2] generate the calibrations */
     calAllSamples();
     
     /* [3] Create calibration Table for reports */
     String  title= "'Calibration DNA' (" + mae.cfg.calDNAname +
             ") statistics summary for active samples - global mean = " +
             Util.cvf2s(grandMeanCalDNA,2) +
             ", #spots=" + mae.gct.calDNACL.length;
     
     SimpleTable calDNAtable= new SimpleTable(title, "", null, nRows, nCols);
     
     for(int c=0;c<nCols;c++)
       calDNAtable.tFields[c]= fieldList[c];	 /* stuff fields */
     String
       rd[],
       tData[][]= calDNAtable.tData;          /* local ptr */     
     
     /* [3.1] Generate the Table for future possible use. */
     String d[]= new String[nCols];
     int
       c,
       j;
     for(int r=0;r<nRows;r++)
     { /* fill all rows */
       rd= tData[r];
       for(c=0;c<nCols;c++)
         rd[c]= "";   /* default data for this row */
       
       if(r<nUsed)
       { /* fill with data else "" */
         j= hpIndex[r];
         ms= hps.msList[j];
         if(ms==null || !ms.dataAvailFromQuantFileFlag ||
         !ms.hasValidDataFlag)
           continue;    /* ignore it */
         
         rd[0]= ms.hpName;                     /* Sample_ID */
         rd[1]= Util.cvf2s(ms.meanCalDNA,4);   /* Mean Intensity */
         rd[2]= Util.cvf2s(ms.stdDevCalDNA,4); /* S.D. Intensity */
         rd[3]= Util.cvf2s(ms.scaleCalDNA,4);  /* CalDNA Scale factor */
         rd[4]= Util.cvf2s(ms.minRI,4);        /* min raw intensity */
         rd[5]= Util.cvf2s(ms.maxRI,4);        /* max raw intensity */
         rd[6]= Util.cvf2s(ms.minDataS,4);     /* min scaled intensity */
         rd[7]= Util.cvf2s(ms.maxDataS,4);     /* max scaled intensity */
       } /* fill with data else "" */
     } /* fill all rows */
     
     return(calDNAtable);
   } /* calDNAhybSamplesTable */
   
   
   /**
    * calcHP_HPcorrelations() - compute HP vs. HP samples correlation coefficients table
    * for all loaded HPs. It is computed only on the Filtered data.
    * @return SimpleTable if successful else null.
    * @see MathMAE#calcXYstat
    * @see SimpleTable
    * @see Util#showMsg3
    * @see #getNormHP_XandYdata
    */
   SimpleTable calcHP_HPcorrelations()
   { /* calcHP_HPcorrelations */
     String fieldName[]= new String[hps.nHP+1];  /* max size */
     int
       hpIndex[]= new int[hps.nHP+1],     /* max size */
       nUsed= 0;
     
     fieldName[0]= "";                        /* nothing in corner */
     
     /* [1] Compute index of data and HP names for active HPs */
     MaHybridSample ms;
     for(int i=1;i<=hps.nHP;i++)
     { /* find out # of gels loaded */
       ms= hps.msList[i];
       if(ms==null || !ms.dataAvailFromQuantFileFlag ||
          !ms.hasValidDataFlag)
         continue;    /* ignore it */
       nUsed++;
       hpIndex[nUsed]= i;   /* save for future computation */
       fieldName[nUsed]= ms.hpName;  /* Sample_ID */
     }
     
     /* [2] Create calibration Table for reports */
     int nRowsNeeded= Math.max(nUsed, ShowSpreadsheetPopup.MAX_ROWS_TO_SHOW);
     nRowsNeeded++;             /* add 1 */
     int
       nList,
       nRows= nRowsNeeded,      /* Col 1 is sample name */
       nCols= nUsed+1;          /* Col 1 is sample name */
     float
       xList[]= new float[map.maxSpots],
       yList[]= new float[map.maxSpots];
     int propList[]= new int[map.maxSpots];
     SimpleTable hphpCorrTable= new SimpleTable(
                                   "HP vs. HP Samples Correlation Coefficients Table",
                                                "", null, nRows, nCols);
     for(int c=0;c<nCols;c++)
       hphpCorrTable.tFields[c]= fieldName[c];
     
     rSqHP_HP= new float[nRows][nCols];
     
     String
       sR= "",
       tData[][]= hphpCorrTable.tData;
     
     /* [3] Estimate correlation coefficient for each row,col samples
      * and stuff the tables.
      */
     int
       r, c,
       row, col;
     MaHybridSample
       msRow, 
       msCol;
     String msg;
     
     for(row=0;row<nRows;row++)
     { /* process rows */
       for(c=0;c<nCols;c++)
         tData[row][c]= "";       
       if(row<nUsed)
       { /* add real data to array */
         rSqHP_HP[row][row]= 1.0F;
         r= hpIndex[row+1];
         msRow= hps.msList[r];
         tData[row][0]= msRow.hpName;
         
         msg= ""+((100*(row+1))/nUsed)+
              "% done computing HP vs HP correlation coefficients";
         Util.showMsg3(msg, Color.white, Color.red);
         
         for(col=row+1;col<nUsed;col++)
         {
           c= hpIndex[col+1];
           if(c==0)
             continue;
           msCol= hps.msList[c];
           
           /* Get data meeting the Filter */
           nList= getNormHP_XandYdata(xList,yList, propList, null /* no GeneList */,
                                      msRow, msCol, true /* set setPropList */,
                                      false);
           sR= MathMAE.calcXYstat(nList, xList, yList, propList,
                                  ("HP:"+(row+1)), ("HP:"+(col+1)) );
           
           /* Stuff tables */
           rSqHP_HP[row][col]= (float)MathMAE.rSq; /* symmetric */
           rSqHP_HP[col][row]= (float)MathMAE.rSq;
           tData[row][col+1]= sR;
         }
       } /* add real data to array */
     } /* process rows */
     
     Util.showMsg3("");           /* clear counter */
     
     return(hphpCorrTable);
   } /* calcHP_HPcorrelations */
   
   
   /**
    * createHPmeanAndVarTable() - create table (HP,mn,stdDev,min,max,median) of raw intensity data.
    * @return table if successful, else null.
    * @see Table
    * @see Util#cvf2s
    */
   Table createHPmeanAndVarTable()
   { /* createHPmeanAndVarTable */
     int
       hpIndex[]= new int[hps.nHP],
       nUsed= 0;
     String
       sf1= mae.cfg.fluoresLbl1,
       sf2= mae.cfg.fluoresLbl2,
       fieldList[]= {"Sample_ID",
                     "Mean Raw Intensity",
                     "Median RI", "Mode RI",
                     "StdDev RI", "Mean Abs Dev RI",
                     "Mean Log RI", "StdDev Log RI",
                     "Mean Abs Dev log RI",
                     "Min RI", "MaxRI",
                     "Min Scaled Intensity", "Max Scaled Intensity",
                     "Mean RI / Max RI", "65K scale factor",
                     "Ratio Median Scale", "Ratio Median Bkgrd Scale",
                     "Median "+sf1, "Median "+sf2,
                     "Median "+sf1+" Bkgd", "Median "+sf2+" Bkgd"
                     };
     
     /* [1] Compute list of active samples */
     MaHybridSample ms;
     for(int i=1;i<=hps.nHP;i++)
     {
       ms= hps.msList[i];
       if(ms==null || !ms.dataAvailFromQuantFileFlag ||
          !ms.hasValidDataFlag)
         continue;    /* ignore it */
       hpIndex[nUsed]= i;
       nUsed++;
     }
     
     if(nUsed<1)
       return(null);
     int
       nRowsNeeded= Math.max(nUsed,ShowSpreadsheetPopup.MAX_ROWS_TO_SHOW);
     nRowsNeeded++;                   /* add 1 */
     int
       nRows= nRowsNeeded,
       nCols= fieldList.length;  /* note: depends on what we put in list*/
     if(!mae.useRatioDataFlag)
       nCols = (nCols-6);        /* Don't add ratio median scaling values */
     
     /* [2] Create Table for reports */
     String title= "Samples mean & stdDev statistics summary for active HPs";
     
     Table hpMnVar= new Table(mae, nRows, nCols, title, "");
     
     for(int c=0;c<nCols;c++)
       hpMnVar.tFields[c]= fieldList[c];	 /* stuff fields */
     String
       rd[],
       tData[][]= hpMnVar.tData;              /* local ptr */
     
     /* [3] Generate the Table for future possible use. */
     int
       c,
       j;
     for(int r=0;r<nRows;r++)
     { /* fill all rows */
       rd= tData[r];
       for(c=0;c<nCols;c++)
         rd[c]= "";   /* default data for this row */
       
       if(r<nUsed)
       { /* process HP */
         /* fill with data else "" */
         j= hpIndex[r];
         ms= hps.msList[j];
         if(ms!=null && ms.dataAvailFromQuantFileFlag &&
         ms.hasValidDataFlag)
         {
           rd[0]= ms.hpName;     /* Sample_ID */
           rd[1]= Util.cvf2s(ms.mnHPri,4);
           rd[2]= Util.cvf2s(ms.medianRI,4);
           rd[3]= Util.cvf2s(ms.modeRI,4);
           rd[4]= Util.cvf2s(ms.sdHPri,4);
           rd[5]= Util.cvf2s(ms.madHPri,4);
           rd[6]= Util.cvf2s(ms.logMeanRI,4);
           rd[7]= Util.cvf2s(ms.logStdDevRI,4);
           rd[8]= Util.cvf2s(ms.logMeanAbsDevRI,4);
           rd[9]= Util.cvf2s(ms.minRI,4);
           rd[10]= Util.cvf2s(ms.maxRI,4);
           rd[11]= Util.cvf2s(ms.minDataS,4);
           rd[12]= Util.cvf2s(ms.maxDataS,4);
           rd[13]= Util.cvf2s((ms.mnHPri/ms.maxRI),4);
           rd[14]= Util.cvf2s(ms.scaleToMaxIV,4);
           if(mae.useRatioDataFlag)
           {
             rd[15]= Util.cvf2s(ms.ratioMedianScale,4);
             rd[16]= Util.cvf2s(ms.ratioMedianBkgdScale,4);
             rd[17]= Util.cvf2s(ms.medianF1,4);
             rd[18]= Util.cvf2s(ms.medianF2,4);
             rd[19]= Util.cvf2s(ms.medianF1Bkgd,4);
             rd[20]= Util.cvf2s(ms.medianF2Bkgd,4);
           }
         }
       } /* process HP */
     } /* fill all rows */
     
     return(hpMnVar);
   } /* createHPmeanAndVarTable */
   
   
   /**
    * recalcGlobalStats() - recompute global array statistics with all or just good spots.
    * This recalculates the statistics for each array in the database using its
    * local good spot data status. This also includes a call to
    * ms.cvtRawQdataToMAEdata() to compute additional derived statistics.
    * It then updates the PopupRegistry current gene, data filter and display.
    * @param useGoodSpotsForGlobalStatsFlag if true
    * @see MaHybridSample#recalcGlobalStats
    * @see PopupRegistry#updateCurGene
    * @see PopupRegistry#updateFilter
    */
   void recalcGlobalStats(boolean useGoodSpotsForGlobalStatsFlag)
   { /* recalcGlobalStats */
     for(int i=1;i<hps.nHP;i++)
     {
       MaHybridSample ms= hps.msList[i];
       ms.recalcGlobalStats(useGoodSpotsForGlobalStatsFlag);
     }
     
     mae.updatePseudoImgFlag= true;
     mae.pur.updateCurGene(objMID, 0, null);
     mae.pur.updateFilter(mae.fc.workingCL);
   } /* recalcGlobalStats */
   
   
   /**
    * cleanup() - cleanup global static allocated variables in this class.
    * If statics are added later to this class, then set them to
    * null here.
    */
   void cleanup()
   { /* cleanup */
     hpXYdata= null;
   } /* cleanup */
   
} /* end of class CompositeDatabase */
