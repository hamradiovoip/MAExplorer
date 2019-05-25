/** File: ClusterGramCanvas.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.util.*;
import java.util.EventListener;
import java.lang.*;
import java.io.*;
import java.io.OutputStream;

/**
 * Create a ClusterGram canvas and display the colored green/red similarity table in a popup window.
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.11 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see DrawClusterGram
 * @see HierClustNode
 */ 

class ClusterGramCanvas extends Canvas 
{
  /** link to global MAExplorer instance*/
  static MAExplorer
    mae;           
                
  /** default canvas width */
  final static int
    MIN_CANVAS_WIDTH=  1024;              
  /** default canvas height */
  final static int           
    MIN_CANVAS_HEIGHT= 5000;              
  /** maximum colors used - Dark green to dark red */
  final static int
    MAX_COLORS= 9;             
		       
  /** preferred height size of entire canvas */
  int
    preferredHeight;     	       
  /** preferred width size of entire canvas */
  int 
    preferredWidth;	       
  /** offset of status canvas if drawing it at top of GIF image canvas, else 0 */ 
  int
    statVertOffset;            
    
  
  /** hierarchical cluster tree */
  private HierClustNode
    tree;                      
  /** parent class */
  private DrawClusterGram 
    dcg;                       
  /** [0:maxGenes-1][0:nCols-1] normalized data for each object */ 
  private float
    objDataV[][];              
       
  /** max of dGramDistS[]  */
  private float
    maxDgramDist;                  
  /** [0:nObj-2] log dist. between children */
  private float
    dGramDistS[];                  
  /** [0:nObj-2] log dist. between Right's children*/
  private float
    dGramDistR[];                  
  /** [0:nObj-2] log dist. between Left's children*/
  private float
    dGramDistL[];             
  /** [0:nObj-2] enumeration order */ 
  private float
    dGramEnumOrder[];             
  /** [0:nObj-2] right child enum order */
  private float
    dGramEOright[];                
  /** [0:nObj-2] left child enum order */
  private float
    dGramEOleft[];            
  
  /** [0:nObj-2] Node Number */
  private int
    dGramNodeNbr[];
  /** [0:nObj-2]  Right Node Number */ 
  private int        
    dGramRightNNbr[];
  /** [0:nObj-2] Left Node Number */
  private int
    dGramLeftNNbr[];
  
  /** used for color mapping */
  private Color
    ratioColor[];              
  
  /** used for color mapping */
  private float
    ratioRange[];              
     
  /** flag: plot narrower boxes */
  private boolean
    shortWidthFlag;            
  /** flag: if cdg.plotMode==mae.PLOT_KMEANS_CLUSTERGRAM) */
  private boolean
    addKmeansDataFlag;            
  /** flag: 1, 2, 3, ...  for COL name */
  private boolean
    useColNbrFlag;             
  /** flag: use full HP stage name for COL name */
  private boolean
    useFullHPStageNameFlag;
  /** flag: set by setupData() if successful */
  private boolean  
    didDataSetupFlag;          
    
  /** found object row when clicked on it */
  boolean
    foundObjFlag;              
  /** zoom in on dendrogram - set by DrawClusterGram checkbox */
  int
    dGramZoomMag;        
    
  /** drawing font */
  private Font
    font;                      
  /** font metrics for font */
  private FontMetrics
    fm;    
  private int
    fontHeight,
    fontWidth,
    fontLeading;
    
  /** # of extra rows so cover the viewport */
  private int
    guardRows;                 
  /** # of rows i.e. genes */
  private int
    nRows;                     
  /** # of columns i.e. HPs */
  private int
    nCols;                     
    
  /** gene list for rows */
  private GeneList
    orderedCL;  
                     
  /** left horizontal size of dendrogram region */
  private int
    dGramX1;
  /** right horizontal size of dendrogram region */
  private int
    dGramX2;
  /** position of scale map X */
  private int
    scaleMapX;                
  /** position of scale map Y */
  private int
    scaleMapY;
  /** vertical label ULHC */
  private int
    vertLabelX;                
  /** top y for vert label */
  private int
    vertLabelY1;               
  /** bottom y for vert label */
  private int
    vertLabelY2;               
  /** left edge of Left label */
  private int
    leftLabel;                 
  /** left edge of box array */
  private int
    leftEdge;                  
  /** right edge of box array */
  private int
    rightEdge;                 
  /** top edge of box array */
  private int
    topEdge;                   
  private int
    botEdge;                   
  /** bottom edge of box array */
  private int
    boxWidth;                  
  /** size of a data box in the clustergram */
  private int
    boxHeight;
  
  /** set by event handler mouse on click */
  private boolean
    isBoxSelectedFlag;         
  
  /** selected row set by event handler mouse on click */
  int
    selRow;
  /** selected column set by event handler mouse on click */
  int
    selCol;
  /** col to norm by in [0:nDataV-1] */
  int
    iDataV;                    
  
  /** set by event handler mouse coords on click */
  private int
    mouseModifiers;            
  /** x cursor set by event handler mouse coords on click */
  private int
    xCursor;   
  /** y cursor set by event handler mouse coords on click */
  private int   
    yCursor;
  /** set by event handler on click */
  int
    selMID;     
  /** distance selected by clicking on dendrogram */
  private float
    selDist;  
                     
  /** current gene feature data */   
  private String 
    featuresStr;                   
  /** current gene genomic data */   
  private String 
    genomicDataStr;
    
  /** ULHC event hlr [0:nCols-1] */
  private int
    boxX[]; 
  /** ULHC event hlr [0:nRows-1] */
  private int                   
    boxY[];                    
  
  /** opt. column labels */
  private String
    colLabel[];                   
  /** row labels (Master_ID + MasterGeneName) */
  private String           
    rowLabel[];                      
  /** row MID index */
  private int           
    rowMID[];          
	
  /** set if draw clustergram to GIF file*/
  private boolean
    drawIntoImageFlag;         
  /** GIF output file if used */
  private String
    oGifFileName;              	    
							    	    	    
	    
  /**
   * ClusterGramCanvas() - Constructor to display expresion profile in canvas
   * @param mae is the instance of MAExplorer
   * @param dcg is the parent window
   * @param orderedCL is the gene list for rows
   * @param tree is the hierarchical cluster tree
   * @see #unpackTreeDgramLists
   * @see #updateData
   */
  ClusterGramCanvas(MAExplorer mae, DrawClusterGram dcg,
                    GeneList orderedCL, HierClustNode tree )
  { /* ClusterGramCanvas */
    this.mae= mae;
    if(orderedCL==null || orderedCL.length==0 || tree==null)
      return;
    
    this.dcg= dcg;
    
    this.orderedCL= orderedCL;   /* gene list ordered by rows */
    this.tree= tree;
    unpackTreeDgramLists(tree);  /* extract dGram arrays for
                                  * drawing dendrograms */
    
    preferredHeight= MIN_CANVAS_HEIGHT;
    preferredWidth= MIN_CANVAS_WIDTH;
    
    font= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
    
    /* Estimate Size of a data box in the clustergram */
    boxHeight= 13;
    shortWidthFlag= false;       /* plot narrower boxes */
    
    preferredHeight= (nRows*boxHeight)+200;
    
    /* Do any data setup required */
    addKmeansDataFlag= (dcg.plotMode==mae.PLOT_KMEANS_CLUSTERGRAM);
    
    drawIntoImageFlag= false;
    
    didDataSetupFlag= false;
    dGramZoomMag= 1;    /* zoom in on dendrogram - set by
                         * DrawClusterGram 1X, 2X, ..., 20X mag button */
    updateData(orderedCL, tree);
  } /* ClusterGramCanvas */
  
  
  /**
   * drawGifFile() - draw clustergram into Gif image file if in stand-alone mode.
   * This sets it up and lets paint() to the heavy lifting...   *
   * @param oGifFileName is the name of the GIF output file.
   * @return false if unable to generate the image file.
   * @see #repaint
   */
  boolean drawGifFile(String oGifFileName)
  { /* drawGifFile */
    if(mae.isAppletFlag || oGifFileName==null)
      return(false);
    
    drawIntoImageFlag= true;
    this.oGifFileName= oGifFileName;
    repaint();          /* will start the process */
    
    return(true);
  } /* drawGifFile */
  
  
  /**
   * updateData() - update expression profile with new gene data
   * @param orderedCL is the ordered gene list for rows
   * @param tree  hierarchical cluster tree
   * @return true if succeed
   * @see GeneList
   * @see HierClustNode
   * @see #repaint
   * @see #setupData
   * @see #unpackTreeDgramLists
   */
  boolean updateData(GeneList orderedCL, HierClustNode tree )
  { /* updateData */
    this.orderedCL= orderedCL;   /* gene list ordered by rows */
    this.tree= tree;
    
    unpackTreeDgramLists(tree);  /* extract dGram arrays for
     * drawing dendrograms */
    
    didDataSetupFlag= false;
    setupData();                 /* allocate data structures */
    repaint();
    
    return(true);
  } /* updateData */
  
  
  /**
   * unpackTreeDgramLists() - extract dGram arrays for drawing dendrograms
   * @param tree  hierarchical cluster tree
   * @see HierClustNode
   */
  private void unpackTreeDgramLists(HierClustNode tree)
  { /* unpackTreeDgramLists */
    this.tree= tree;
    
    /* Make local ptrs for speed */
    nRows= tree.nObj;                 /* number of objects, i.e. # genes */
    nCols= tree.nDataV;               /* size of expr. profile, i.e. # HPs */
    iDataV= tree.iDataV;              /* normalization object index if not -1 */
    objDataV= tree.objDataV;          /* [0:nRows-1][0:nCols-1]
                                       * norm. dataV[0:nRows] */
    maxDgramDist= tree.maxDgramDist;  /* max dGramDistXXX[]
                                       * (distance not dist**2).
                                       * Log of max distance found */
    
    dGramDistS= tree.dGramDist;          /* [0:nRows-2] dist. between children */
    dGramDistR= tree.dGramDistR;         /* [0:nRows-2] dist. right children */
    dGramDistL= tree.dGramDistL;         /* [0:nRows-2] dist. left children */
    dGramEnumOrder= tree.dGramEnumOrder; /* [0:nRows-2] enumeration order */
    dGramEOright= tree.dGramEOright;     /* [0:nRows-2] right child enum order */
    dGramEOleft= tree.dGramEOleft;       /* [0:nRows-2] left child enum order */
    dGramNodeNbr= tree.dGramNodeNbr;     /* [0:nRows-2] Node Number */
    dGramRightNNbr= tree.dGramRightNNbr; /* [0:nRows-2] Right Node Nbr */
    dGramLeftNNbr= tree.dGramLeftNNbr;   /* [0:nRows-2] Left Node Nbr */
  } /* unpackTreeDgramLists */
  
  
  /**
   * setupData() - setup additional data arrays for ClusterGram
   * @see Gene#isGeneProperty
   * @see GeneList
   * @see Util#cvtValueToStars
   */
  private void setupData()
  { /* setupData */
    if(didDataSetupFlag)
      return;
    
    Gene gene;
    String 
      geneName,
      masterID,
      s;
    
    isBoxSelectedFlag= false;
    selRow= -1;
    selCol= -1;
    selMID= -1;
    selDist= 0.0F;
    featuresStr= "";
    genomicDataStr= "";
    
    boxX= new int[nCols];            /* ULHC event locations */
    boxY= new int[nRows+1];          /* extra for last row */
    
    colLabel= new String[nCols];     /* opt. column labels */
    rowLabel= new String[nRows];     /* row labels (Master_ID + MasterGeneName) */
    rowMID= new int[nRows];          /* MID #  for gene */
    
    MaHybridSample ms;
    for (int h=0;h<nCols;h++)
    {
      ms= mae.hps.msListE[h];
      s= (useColNbrFlag || ms==null)
            ? (""+h+1)
            : ((useFullHPStageNameFlag)
                  ? ms.fullStageText
                  : ms.hpName);
      colLabel[h]= s;
    }
    
    /* The following variables are only used for K-means clustergram */
    int
      mid= -1,
      nB;
    float
      val,
      valMax,
      distKN;
    boolean isKmeansNode;
    String sSimilarity;
    
    for(int r=0;r<nRows;r++)
    { /* make list of Master IDs and gene names */
      gene= orderedCL.mList[r];
      
      /* [NOTE] if((gene.properties & Gene.C_BAD_SPOT)!=0)
       * could ignore bogus spots
       */      
      if(gene!=null)
      { /* use the gene */
        geneName= gene.Gene_Name;
        masterID= gene.Master_ID;
        mid= gene.mid;
        if(addKmeansDataFlag)
        { /* use ClusterGram for Kmeans-clustering */
          /* [CHECK] verify that this is working correctly */
          nB= gene.clusterNodeNbr;
          distKN= gene.data;
          isKmeansNode= gene.isGeneProperty(Gene.C_IS_KMEANS);
          /* compute similarity graphic '******'based on distance */
          //valMax= maxGlobalDist;
          valMax= mae.clg.kMeansMaxDist[nB];
          val= (isKmeansNode) ? 0 : distKN;
          sSimilarity= Util.cvtValueToStars(val, valMax, 15, /*maxStars [1:30] */
                                            true /* right fill with spaces*/);
          s= masterID + "  Cluster#" + nB + "  " + sSimilarity + "   " + geneName;
        }/* use ClusterGram for Kmeans-clustering */
        else
          s= masterID + "  " + geneName;
      } /* use the gene */
      else
        s= mae.masterIDname+"#"+ mid;      /* NOTE: this is a DRYROT BUG!!! */
      rowLabel[r]= s;
      rowMID[r]= mid;
    } /* make list of Master IDs and gene names */
    
   /* Setup color chart mapping for ratios.
    * [TODO] move this specialized color mapping code
    * to the Util. package and integrated with other colormap code.
    */
    ratioColor= new Color[MAX_COLORS+1];
    ratioRange= new float[MAX_COLORS];
    
    /* Testing X/Y, but want Y/X, so flip Red and Green so
     *     X/Y > 1 is Y/X < 1 is RED
     *     X/Y < 1 is Y/X > 1 is GREEN
     */
    /* set the colors */
    ratioColor[8]= new Color(255,0,0);     /* RED */
    ratioColor[7]= new Color(210,0,0);
    ratioColor[6]= new Color(160,0,0);
    ratioColor[5]= new Color(110,0,0);
    ratioColor[4]= new Color(0,0,0);       /* BLACK */
    ratioColor[3]= new Color(0,110,0);
    ratioColor[2]= new Color(0,160,0);
    ratioColor[1]= new Color(0,210,0);
    ratioColor[0]= new Color(0,255,0);     /* GREEN */
    ratioColor[9]= Color.cyan;             /* Illegal color */
    
    /* range the ratios */
    ratioRange[0]= 0.125F;
    ratioRange[1]= 0.165F;
    ratioRange[2]= 0.25F;
    ratioRange[3]= 0.50F;
    ratioRange[4]= 1.0F;
    ratioRange[5]= 2.0F;
    ratioRange[6]= 4.0F;
    ratioRange[7]= 6.0F;
    ratioRange[8]= 8.0F;
    
    didDataSetupFlag= true;
  } /* setupData */
  
  
  /**
   * getPreferredSize() - get the preferred size
   * @return size of the window
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(new Dimension(preferredWidth, preferredHeight));
  } /* getPreferredSize */
  
  
  /**
   * getMinimumSize() - get the minimum preferred size
   * @return size of the window
   */
  public Dimension getMinimumSize()
  { /* getMinimumSize */
    return(new Dimension(MIN_CANVAS_WIDTH, MIN_CANVAS_HEIGHT));
  } /* getMinimumSize */
  
  
  /**
   * selectObject() - test mouse cursor to select row or box if within any box
   * @param xC is the x coordinate selected in the window
   * @param yC is the y coordinate selected in the window
   * @return true if successful and set the appropriate values.
   */
  private boolean selectObject(int xC, int yC)
  { /* selectObject */
    /* Default is failed */
    isBoxSelectedFlag= false;
    selRow= -1;
    selCol= -1;
    selMID= -1;
    selDist= -1.0F;
    
    /* Determine which row and column is selected and set */
    if(xC>=leftEdge && xC<=rightEdge && yC>=topEdge && yC<=botEdge)
    { /* test specific boxes */
      for(int c= nCols-1; c>=0;c--)
        if(xC > boxX[c])
        {
          selCol= c;
          break;
        }
    } /* test specific boxes */
    
    /* Get selected Row and gene MID */
    for(int r= 0; r<=nRows; r++)
      if(yC < boxY[r])
      {
        selRow= r-1;
        break;
      }
    if(selRow>=0)
    {
      isBoxSelectedFlag= true;
      Gene gene= orderedCL.mList[selRow];
      if(gene!=null)
        selMID= gene.mid;
    }
    
    /* Get selected dendrogram distance taking Zoom into account */
    int dGwidth= (dGramX2-dGramX1);
    if(dcg.drawDendroGramFlag && xC<dGwidth)
      selDist= (maxDgramDist*(dGwidth-xC))/(dGwidth*dGramZoomMag);
    
    return(isBoxSelectedFlag);
  } /* selectObject */
  
  
  /**
   * showGeneMsgsAndPlot() - show quant & genomic data messages in MSG & in plot
   * @param mid is the master gene index
   * @param selRow is the row selected if not -1
   * @see MaHybridSample#getSpotDataStatic
   * @see SpotFeatures#getSpotFeatures
   * @see SpotFeatures#getSpotGenomicData
   * @see Util#cvf2s
   * @see Util#showFeatures
   * @see Util#showMsg
   */
  void showGeneMsgsAndPlot(int mid, int selRow)
  { /* showGeneMsgsAndPlot */
    int gid1= mae.mp.mid2gid[mid];
    
    mae.ms.getSpotDataStatic(gid1, mae.useRatioDataFlag);  /* just using the XY coords */
    Point xyGid1= SpotData.xyS;
    if(xyGid1==null)
      return;
    
    boolean saveGangFlag= mae.gangSpotFlag;
    
    /* If possible, force it to be true since need both data  */
    mae.gangSpotFlag= (mae.cfg.maxFIELDS>1);
    
    String normIntensStr= "";
    if(selCol!=-1)
    {
      float normIntensity= objDataV[selRow][selCol];
      normIntensStr= "norm. intensity="+ Util.cvf2s(normIntensity,4);
    }
    
    String
      maCoords= mae.grcd.cvtGID2str(gid1,false),
      xygStr= maCoords + normIntensStr,
      featuresStr= mae.sf.getSpotFeatures(xyGid1, mae.ms),
      genomicDataStr= mae.sf.getSpotGenomicData(xyGid1, mae.ms);
    
    mae.gangSpotFlag= saveGangFlag; /* restore flag */
    
    Util.showMsg(xygStr);
    Util.showFeatures(featuresStr, genomicDataStr);
  } /* showGeneMsgsAndPlot */
  
  
  /**
   * updateCurGene() - set current gene by MID from PopupRegistry
   * @param mid is the master gene index
   * @see DrawClusterGram#getScrollPosition
   * @see DrawClusterGram#getViewportSize
   * @see DrawClusterGram#setScrollPosition
   * @see #showGeneMsgsAndPlot
   */
  void updateCurGene(int mid)
  { /* updateCurGene */
    selMID= -1;
    selRow= -1;
    selCol= -1;
    selDist= -1.0F;
    
    /* Look up the gene by mid */
    Gene mList[]= orderedCL.mList;
    for(int r=0;r<nRows;r++)
      if(mList[r]!=null && mList[r].mid==mid)
      {
        selMID= mid;
        selRow= r;           /* Note: do not change  selCol */
        foundObjFlag= true;
        selDist= -1.0F;
        break;
      }
    
    if(selRow>=0)
    { /* Set the current gene */
      showGeneMsgsAndPlot(selMID, selRow);
      
      /* Scroll canvas with the row centered in the canvas. */
      Point canvasPos= dcg.getScrollPosition();
      Dimension viewPortSize= dcg.getViewportSize();
      int
        halfViewPort= viewPortSize.height/2,
        y= ((preferredHeight*selRow)/nRows) - halfViewPort;
      if(y<0)
        y= 0;
      try
      {
        dcg.setScrollPosition(canvasPos.x, y);
        repaint();        /* update the clusterGram */
      }
      catch (Exception IllegalArgumentException)
      {
      }
    } /* Set the current gene */
  } /* updateCurGene */
  
  
  /**
   * setCurrentGene() - set current gene from (xCursor,yCursor) in ClusterGram.
   * @param xCursor of current gene
   * @param yCursor of current gene
   * @param mouseModifiers when selected the current gene
   * @param changeCurGeneFlag is on if we should call the popup registry
   * @see PopupRegistry#updateCurGene
   * @see #repaint
   * @see #selectObject
   * @see #showGeneMsgsAndPlot
   */
  void setCurrentGene(int xCursor, int yCursor, int mouseModifiers,
                      boolean changeCurGeneFlag)
  { /* setCurrentGene */
    this.xCursor= xCursor;
    this.yCursor= yCursor;
    this.mouseModifiers= mouseModifiers;
    
    /* Look up the gene at the selRow */
    foundObjFlag= selectObject(xCursor,yCursor);
    
    if(selRow>=0)
    { /* Set the current gene */
      //CompositeDatabase.setObjCoordFromMID(selMID);
      
      showGeneMsgsAndPlot(selMID, selRow);
      
    /* when change current gene in ms.objXXX. Then put things here
     * to do when change the gene which are side effects.
     * This includes modifying the 'editable gene list'
     */
      //mae.pur.chkOtherCurGeneEffects(selMID,
      //			  ClusterGenes.UPDATE
      //			  //0 /* modifiers */
      //			  );
      
      if(changeCurGeneFlag)
      {
        mae.pur.updateCurGene(selMID,
        mouseModifiers, /*ClusterGenes.UPDATE */
        dcg.spp);
      }
      
      repaint();
      //mae.repaint();
    } /* Set the current gene */
  } /* setCurrentGene */
  
  
  /**
   * setBoxSizes() - set box sizes based on font metrics.
   * If the dgc.drawDendroGramFlag is set, then add space to
   * left of clustergram for the dendrogram to be drawn.
   * @param g is graphics context
   */
  void setBoxSizes(Graphics g)
  { /* setBoxSizes */
    /* Get the font sizes */
    fm= g.getFontMetrics(font);
    fontHeight= fm.getHeight();
    fontWidth= (fm.getMaxAdvance()==-1) ? 5 : fm.getMaxAdvance();
    fontLeading= fm.getLeading();
    
    /* Estimate Size of a data box in the clustergram */
    boxHeight= fontHeight+fontLeading;
    boxWidth= fontWidth;
    
    if(mae.hps.nHP_E>15)
    { /* plot narrower boxes */
      shortWidthFlag= true;
      boxWidth= 5;          /* OVERIDE */
    }
    
    dGramX1= 0;          /* left edge set if drawing dendrogram */
    dGramX2= 0;          /* right edge set if drawing dendrogram */
    
    if(dcg.drawDendroGramFlag)
    { /* setup region for drawing dendrogram */
      dGramX1= 4;
      dGramX2= dGramX1+100;
    }
    
    /* Setup other regions to draw into */
    scaleMapX= 50;             /* position of scale map */
    scaleMapY= 0;              /* 10 */
    
    /* region to draw label above cluster gram */
    vertLabelX= 40+dGramX2;    /* vertical label ULHC */
    vertLabelY1= scaleMapY+boxHeight+5;   /* top y for vert label */
    vertLabelY2= vertLabelY1+5*boxHeight; /* bottom y for vert label */
    
    leftLabel= 4+dGramX2;      /* left edge of label for each gene row */
    leftEdge= vertLabelX;      /* left edge of box array */
    rightEdge= leftEdge + (nCols+1)*boxWidth; /* right edge of box array */
    
    topEdge= 2;                /* top edge of box array */
    botEdge= topEdge + (nRows+2)*boxHeight; /* bottom edge of box array */
  } /* setBoxSizes */
  
  
  /**
   * setRatioColor() - map data ratio to color (used to color a box).
   * Color is in (ratioMin : ratioMax].
   * i.e. ratioMin < ratio <= ratioMax for ratio <1.0
   * and  ratioMin <= ratio < ratioMax for ratio >1.0
   * @param ratio is the ratio to use in looking up the color
   * @param n is the number of colors in map
   */
  private Color setRatioColor(float ratio, int n)
  { /* setRatioColor */
    int iColor= 9;                /* Illegal Color  CYAN */
    
    for(int i=0;i<9;i++)
      if(ratio < ratioRange[i])
      {
        iColor= i-1;
        if(iColor<0)
          iColor=0;
        break;
      }
    /*
    if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("CGC-SRC n="+n+" iColor="+iColor+
                        " ratio="+Util.cvf2s(ratio,3));
    */
    if(iColor==9)
      iColor= 8;    /* latch onto last color */
    
    return(ratioColor[iColor]);
  } /* setRatioColor */
  
  
  /**
   * drawRow() - draw row of boxes, left & right label, save the coords.
   * Use this.font as the font size.
   * @param g is graphics context
   * @param r is the row in the cluster gram
   * @param dataV is the data vector
   * @see GeneList#isMIDinGeneList
   * @see #setRatioColor
   */
  private void drawRow(Graphics g, int r, float dataV[])
  { /* drawRow */
    int
      x1= leftEdge,
      x2,
      y1= topEdge + boxHeight*r+statVertOffset,
      y2= y1 + boxHeight,
      rightEdgeEGLmark= rightEdge-5,
      rightEdgeLabel= rightEdge;
    
    /* [1] Memorize the row canvas address */
    boxY[r]= y1;
    boxY[r+1]= (y1+boxHeight);     /* for catching boxY[nRows-1] */
    
    /* [2] Draw the row of boxes */
    for(int c=0;c<nCols;c++)
    { /* draw a row of boxes */
      x2= x1+boxWidth;
      g.setColor(setRatioColor(dataV[c], r));
      g.fillRect(x1,y1,boxWidth,boxHeight);
      boxX[c]= x1;     /* save canvas addr for event handler */
      x1 += boxWidth;         /* bump for next one to draw */
    } /* draw a row of boxes */
    
    /* [3] Draw the right row label */
    g.setFont(font);
    /* Draw a '*' if the gene is in the EGL and the view EGL flag is set. */
    int mid= rowMID[r];
    boolean geneIsInEGLflag= mae.gct.editedCL.isMIDinGeneList(mid);
    
    if(geneIsInEGLflag && mae.showEGLflag)
    {
      g.setColor(Color.magenta);
      g.drawString("*", rightEdgeEGLmark, y2-3);
    }
    
    /* Draw the gene name label on the right */
    if(r==selRow)
      g.setColor((mae.useDichromasyFlag) ? Color.blue : Color.green);
    else
      g.setColor((mae.useDichromasyFlag) ? Color.black : Color.blue);
      g.drawString(rowLabel[r], rightEdgeLabel, y2-3);
      
    /* [4] Draw the left row # */
    if(r==selRow)
      g.setColor((mae.useDichromasyFlag) ? Color.blue : Color.green);
    else
      g.setColor( Color.black );
      
    g.drawString((""+(r+1)), leftLabel, y2-3);
  } /* drawRow */
  
  
  /**
   * copyGenesInSubtreeToEGL() - Copy terminal genes in dendrogram subtree
   * to E.G.L for box that was selected at (selRow,selCol) such that terminal
   * genes LEQ selDist GT 0.0F and dcg.drawDendroGramFlag.
   * [TODO] - needs to be debugged. Not picking up correct subset of the
   * tree.
   * @see HierClustNode#findSubtreeOfNodeNbrs
   * @see #setEGLtoSubTreeGenes
   */
  private void copyGenesInSubtreeToEGL()
  { /* copyGenesInSubtreeToEGL */
    if(!dcg.drawDendroGramFlag || selDist<=0.0F)
      return;         /* ForGetAboutIt - not selecting subtree */
    if(!mae.DBUG_SET_EGL_FROM_DENDROGRAM)
      return;
    
    /* [1] Backup the tree from this terminal node until find
     * non-terminal node <= selDist. This then is the terminal node
     */
    int
      nodeNbr= dGramNodeNbr[selRow],
      topNodeNbrOfSubtree= nodeNbr;
    
    /* [TODO] need to traverse the tree. */
    
    /* [2] Find Genes in dendrogram tree that belong to this branch */
    int
      subtreeNbrs[]= null;
    subtreeNbrs= tree.findSubtreeOfNodeNbrs(topNodeNbrOfSubtree);
    if(subtreeNbrs==null)
      return;
    
    /* [3] Copy genes to E.G.L. */
    setEGLtoSubTreeGenes(subtreeNbrs);
  } /* copyGenesInSubtreeToEGL */
  
  
  /**
   * setEGLtoSubTreeGenes() - Copy genes to E.G.L. (Edited Gene List)
   * @param subtreeNbrs is the list of genes to be copied to the E.G.L.
   * @see EditedGeneList#addGeneToEditedCL
   */
  void setEGLtoSubTreeGenes(int subtreeNbrs[])
  { /* setEGLtoSubTreeGenes */
    int lth= subtreeNbrs.length;
    
    mae.gct.editedCL.clear();
    for(int i=0;i<lth;i++)
    {
      int
        midNbr= subtreeNbrs[i],
        mid= (tree.hierClusters[midNbr].oFeature);
      
      mae.egl.addGeneToEditedCL(mid);
    }
  } /* setEGLtoSubTreeGenes */
  
  
  /**
   * drawSelectedHP() - draw selected box as white circle
   * if the selected row is within [r1:r2].
   * @param g is the graphic context
   * @param r1 starting row that is visible
   * @param r2 ending row that is visible
   */
  private void drawSelectedHP(Graphics g, int r1, int r2)
  { /* drawSelectedHP */
    if(r1>selRow || selRow>r2)
      return;                   /* make sure legal to draw */
    
    int
      x1= leftEdge+selCol*boxWidth,
      x2= x1+boxWidth,
      y1= topEdge + boxHeight*selRow+statVertOffset,
      y2= y1 + boxHeight,
      radius= (boxHeight/2)-3,  /* was boxWidth */
      radius2,
      xC= x1+(boxWidth/2),
      yC= y1+(boxHeight/2),
      x0= xC-radius,
      y0= yC-radius;
    
    /* [2] Draw the white circle over the selected box */
    if(radius<2)
      radius= 2;            /* MUST NEVER BE < 1 !!!! */
    radius2= 2*radius;
    
    g.setColor( Color.white );
    g.drawArc( x0, y0, radius2, radius2, 0, 360 );
    
    /*
    if(mae.CONSOLE_FLAG)
      System.out.println("CGC-DSHP nCols="+nCols+
                         " selRow="+selRow+" selCol="+selCol+
                         "\n  boxWidth="+boxWidth+" radius="+radius+
                         " 2*radius="+radius2+
                         "\n  (xC,yC)=("+xC+","+yC+
                         ") (x0,y0)=("+x0+","+y0+")");
    */
  } /* drawSelectedHP */
  
  
  /**
   * drawVerticalLabels() - Draw the vertical labels of samples
   * @param g is the graphic context
   */
  void drawVerticalLabels(Graphics g)
  { /* drawVerticalLabels */
    int
      y1= vertLabelY2,
      x1= vertLabelX; /* note: start on left of box, since need space
                       * when have 2 digits or more */
    
    g.setColor(Color.black);
    for(int c=0;c<nCols;c++)
    { /* draw each heading label */
      if(!shortWidthFlag || c==0 || (c+1)%5==0)
      { /* draw it */
        g.setFont(font);
        if(useColNbrFlag)
        { /* just draw numbers */
          g.drawString(""+(c+1),x1,y1);
        }
        else
        { /* draw the letters of the name vertically */
          g.drawString(""+(c+1),x1,y1);
        }
      } /* draw it */
      
      x1 += boxWidth;
    } /* draw each heading label */
  } /* drawVerticalLabels */
  
  
  /**
   * drawScaleMap() - draw the X/Y color scale map.
   *<PRE>
   *  RED                 BLACK             GREEN
   *  <1/8X 1/6X 1/4X 1/2X  1X    2X  4X  6X  >8X
   *
   *   Selected Row[#row] Master_ID (if Y selected)
   *   Selected Col[#col] Sample name (if X & Y selected)
   *   Normalization Col[#iDataV] Norm Sample name (if X selected)
   *   <featureStr> (if X selected)
   *   <genomicDataStr> (if X selected)
   *</PRE>
   * @param g is the graphic context
   * @see Util#cvf2s
   */
  void drawScaleMap(Graphics g)
  { /* drawScaleMap */
    int
      x1= scaleMapX,
      width= 2*boxWidth,
      y1= scaleMapY;
    
    g.setFont(font);
    g.drawString("  HP[i] > HP[X]                          HP[i] < HP[X]",
    scaleMapX, y1);
    
    y1 += boxHeight+3;
    for(int i=0;i<MAX_COLORS;i++)
    {
      g.setColor( ratioColor[i] );
      g.fillRect(x1,y1, width,boxHeight);
      x1 += width;        /* bump for next one to draw */
    }
    
    y1 += boxHeight+10;
    g.setColor( Color.black );
    g.drawString("<1/8X  1/6X  1/4X  1/2X    1X    2X    4X    6X   >8X",
    scaleMapX, y1);
    
    if(selRow!=-1)
    { /* add gene spcific data */
      int
        selRow1= selRow+1,
        selCol1= selCol+1,
        normCol= iDataV+1;
      MaHybridSample msListE[]= mae.hps.msListE;
      
      x1= scaleMapX+20;
      y1 += boxHeight+5;
      g.setColor( Color.black );
      String
        hpNameN= msListE[normCol].hpName,
        masterID= (selMID==-1 || orderedCL.mList[selRow]==null)
                    ? ""
                    : orderedCL.mList[selRow].Master_ID;
      String
        sMsg= null,
        sRowData= "Selected row ["+selRow1+"] "+mae.masterIDname+" ["+
                  masterID+"]";
      
      if(mae.normHCbyRatioHPflag)
        sRowData += ",  Normalization column ["+normCol+
                    "] HP: "+ hpNameN;
      g.drawString(sRowData, x1, y1);
      
      if(selCol1>0)
      { /* add data on specific HP[col] */
        String
          normIntensStr= "",
          hpNameY= msListE[selCol1].hpName;
        float
          normColVal= objDataV[selRow][selCol];
        sMsg= "Selected col["+selCol1+"] HP: "+hpNameY+
              ", Normalized intensity="+Util.cvf2s(normColVal,4);
      } /* add data on specific HP[col]  */
      
      if(dcg.drawDendroGramFlag && selDist>0.0F)
      {
        if(sMsg!=null)
          sMsg += ", ";
        sMsg= "Distance between clusters="+ Util.cvf2s(selDist,4);
      }
      if(sMsg!=null)
      {
        y1 += boxHeight;
        g.drawString(sMsg,x1, y1);
      }
      y1 += boxHeight;
      g.drawString("  "+featuresStr, x1, y1);
      y1 += boxHeight;
      g.drawString("  "+genomicDataStr, x1, y1);
    }  /* add gene spcific data */
  } /* drawScaleMap */
  
  
  /**
   * drawDendrogram() - draw dendrogram from row r1 to r2.
   *<PRE>
   * The spacing between terminal nodes is boxHeight.
   * The region for drawing dendrogram is dGramX1 to dGramX2.
   * The 'enum order' is the spacing on particular ordered rows
   * and is fractional for non-terminal nodes.
   * We used the ordered hierarchical cluster data for nodes in [r1:r2].
   *   dGramDist[0:nObj-1] distance between children.
   *   dGramDistR[0:nObj-1] distance between Right children.
   *   dGramDistL[0:nObj-1] distance between Left children.
   *   dGramEnumOrder[0:nObj-1] enumeration order.
   *   dGramEOright[0:nObj-1] right child enum order.
   *   dGramEOleft[0:nObj-1] left child enum order.
   *   dGramNodeNbr[0:nObj-1] - Node Number
   *   dGramEOrightNNbr[0:nObj-1] - Right Node Number
   *   dGramLeftNNbr[0:nObj-1] - Left Node Number
   *</PRE>
   * @param g is the graphic context
   * @param r1 starting row that is visible
   * @param r1 ending row that is visible
   */
  private void drawDendrogram(Graphics g, int r1, int r2)
  { /* drawDendrogram */
    if(dGramDistS==null)
      return;                 /* no data */
    
    /* Draw nodes which are in the [r1:r2] range */
    int
      DBUG_N= 11,             /* # of nodes - ONLY DEBUG if allowed */
      x1, x2, y1, y2,
      dGoff= 5,                         /* left start of dendrogram */
      dGwidth= (dGramX2-dGramX1)-dGoff, /* max width of dendrogram */
      ntNodes= nRows-1;
    float
      distS,
      distR,
      distL,
      eoS,
      eoR,
      eoL,
      r1F= (float)Math.max(0,(r1-guardRows)),
      r2F= (float)Math.min(ntNodes,(r2+guardRows)),
      dGscale= (dGramZoomMag*dGwidth)/maxDgramDist;
                              /* scale log(1+dist) to X coord
                               * space & scale by magnification */
    int
      dxS,
      dxL,
      dxR,
      nNbr,                   /* node number */
      rNNbr,
      lNNbr;
    
    /*
    if(mae.DBUG_HCN && nRows<20)
      mae.fio.logMsgln("CGC-DD dGramZoomMag="+dGramZoomMag+
                       " selDist="+selDist+ " dGscale="+dGscale);
    */
    
    /* 
    if(mae.DBUG_HCN && mae.LECBdebug==0 && nRows<20)
      mae.fio.logMsgln("\nCGC-DD r1:r2["+r1+":"+r2+
                       "], r1F:r2F["+((int)r1F)+":"+((int)r2F)+
                       "] ntNodes="+ntNodes+
                       " nRows="+nRows+
                       "\n    guardRows="+guardRows+
                       " topEdge="+topEdge +
                       " boxHeight="+boxHeight+
                       "\n    dGramX1="+dGramX1+
                       " dGramX2="+dGramX2+
                       " dGwidth="+dGwidth+
                       " dGscale="+Util.cvf2s(dGscale,2)+
                       " maxDgramDist="+Util.cvf2s(maxDgramDist,2));
       */
    
    for(int n= 0;n<ntNodes;n++)
    { /* test if node in [r1:r2] range and then draw it */
      eoS= dGramEnumOrder[n];
      if(r1F<=eoS && eoS<=r2F)
      { /* draw it */
        distS= dGramDistS[n];
        distR= dGramDistR[n];
        distL= dGramDistL[n];
        eoR= dGramEOright[n];
        eoL= dGramEOleft[n];
        dxS= (int)(dGwidth - dGscale*distS)+dGoff;  /* Horiz pos.
                                                    * current node */
        dxR= (int)(dGwidth - dGscale*distR)+dGoff;
        dxL= (int)(dGwidth - dGscale*distL)+dGoff;
        nNbr= dGramNodeNbr[n];
        rNNbr= dGramRightNNbr[n];
        lNNbr= dGramLeftNNbr[n];
        
        dxS= Math.max(0,dxS);        /* clip in case zooming */
        dxR= Math.max(0,dxR);
        dxL= Math.max(0,dxL);
       /*
        if(mae.DBUG_HCN && mae.LECBdebug==0 && n<=DBUG_N && nRows<20)
         mae.fio.logMsgln("\n CGC-DD.1 n="+n+
                          " eo(S,L,R)=("+Util.cvf2s(eoS,2)+
                          ", "+Util.cvf2s(eoR,2)+", "+
                          Util.cvf2s(eoL,2)+
                          "), #(S,R,L)=("+nNbr+","+rNNbr+","+lNNbr+
                          ")\n dist(S,R,L)=("+Util.cvf2s(distS,2)+
                          ", "+Util.cvf2s(distR,2)+
                          ", "+Util.cvf2s(distL,2)+
                          ") (dxS,dxR,dxL)=("+dxS+", "+dxR+", "+dxL+")");
        */
        
        /*  Draw a non-terminal node as three lines:
         * 1. draw vert line (eoR,distS) to (eoL,distS)
         * 2. draw horiz line (eoR,distS) to (eoR,distR)
         * 3. draw horiz line (eoL,distS) to (eoL,distL)
         */
        if(distS<selDist && selDist>0.0F)
          g.setColor((mae.useDichromasyFlag) ? Color.orange : Color.red);
        else g.setColor(Color.black);
        
        /* 1. Estimate vert line coords between left and right children
         * (eoR,distS) to (eoL,distS)
         */
        x1= dxS;
        x2= dxS;
        y1= (int)(topEdge + (boxHeight*(eoR+0.5F)))+statVertOffset;
        y2= (int)(topEdge + (boxHeight*(eoL+0.5F)))+statVertOffset;
        g.drawLine(x1,y1,x2,y2);
        /*
        if(mae.DBUG_HCN && n<=DBUG_N && nRows<20)
          mae.fio.logMsgln("  CGC-DD.2.1 VERT-LINE [x1:x2, y1:y2]=["+
         x1+":"+x2+","+y1+":"+y2+ "]");
         */
        
       /* 2. Estimate horizontal line coords from self to Right children.
        * (eoR,distS) to (eoR,distR).
        */
        x1= dxS;
        x2= dxR;
        y1= (int)(topEdge + (boxHeight*(eoR+0.5F)))+statVertOffset;
        y2= y1;
        g.drawLine(x1,y1,x2,y2);
        /*
        if(mae.DBUG_HCN && n<=DBUG_N && nRows<20)
          mae.fio.logMsgln("  CGC-DD.2.2 Right HOR-LINE [x1:x2, y1:y2]=["+
           x1+":"+x2+","+y1+":"+y2+ "]");
         */
        
       /* 3. Estimate horizontal line coords from self to Right children
        *  (eoL,distS) to (eoL,distL).
        */
        x1= dxS;
        x2= dxL;
        y1= (int)(topEdge + (boxHeight*(eoL+0.5F)))+statVertOffset;
        y2= y1;
        g.drawLine(x1,y1,x2,y2);
        /*
        if(mae.DBUG_HCN && n<=DBUG_N && nRows<20)
          mae.fio.logMsgln("  CGC-DD.2.3 Left HOR-LINE [x1:x2, y1:y2]=["+
         x1+":"+x2+","+y1+":"+y2+ "]");
         */
        g.setColor(Color.black);
      } /* draw it */
    } /* test if node in [r1:r2] range and then draw it */
  } /* drawDendrogram */
  
  
  /**
   * paint() - draw ClusterGram [r1:r2] from scroller percentage and canvas height.
   * Redraw only what is visible...
   * @param g is the graphic context
   * @see DrawClusterGram#drawScaleMapAndVerticalLabels
   * @see DrawClusterGram#getHScrollbarHeight
   * @see DrawClusterGram#getVAdjustable
   * @see DrawClusterGram#getViewportSize
   * @see WriteGifEncoder
   * @see WriteGifEncoder#writeFile
   * @see #copyGenesInSubtreeToEGL
   * @see #drawDendrogram
   * @see #drawRow
   * @see #drawScaleMap
   * @see #drawSelectedHP
   * @see #drawVerticalLabels
   * @see #repaint
   * @see #setBoxSizes
   */
  public void paint(Graphics g)
  { /* paint */
    Image gifImage= null;
    Graphics gGif= null;           /** draw status at top of Gif image */
    
    statVertOffset= 0;  /** non-zero if writing to GIF image */
    
    /* If draw plot into GIF image file, setup new Graphics g. */
    if(drawIntoImageFlag)
    { /* draw into GIF file Image instead of canvas */
      statVertOffset= dcg.statusCanvasHeight;
      int
        w= 600+ ((dcg.drawDendroGramFlag) ? dGramX2 /* i.e. 100 */ : 0),
        h= boxHeight*nRows+statVertOffset;
      
      gifImage= createImage(w,h);
      g= gifImage.getGraphics();
    }
    
    /* [1] Clear the backbround and set the distances...*/
    g.setColor(Color.white);
    g.fillRect(0,0, preferredWidth, preferredHeight);
    setBoxSizes(g);    /* set box sizes based on font metrics */
    
    /* [2] Draw the status area */
    if(drawIntoImageFlag)
    { /*draw the ClusterGram status area into GIF image */
      dcg.cgC.drawScaleMap(g); /*  Draw the X/Y color scale map */
      drawVerticalLabels(g);   /* Draw the vertical labels */
    }
    else
    { /* Have the parent draw it in the separate title area canvas */
      dcg.drawScaleMapAndVerticalLabels(gGif);
    }
    
    /* [3] Get size and position of viewport so can draw only those
     * rows that are needed.
     */
    Dimension viewPortSize= dcg.getViewportSize();
    Adjustable VAdj= dcg.getVAdjustable();
    int
      vPortHeight= viewPortSize.height,
      vPortWidth= viewPortSize.width,
      HScrbarHeight= dcg.getHScrollbarHeight(),
      minVA= VAdj.getMinimum(),             /* get scroller range */
      maxVA= VAdj.getMaximum(),
      valueVA= VAdj.getValue();             /* get current position */
    
    float pct= ((float)valueVA)/((float)(maxVA-minVA-HScrbarHeight));
    int rowsPerVP= (vPortHeight/boxHeight+1); /* rows per viewport height*/
    
    guardRows= rowsPerVP/2;
    
    int
      estR1= (int)(pct*nRows),              /* rows equiv to viewport */
      estR2= (estR1+rowsPerVP),
      r1= Math.max((estR1-guardRows),0),    /* rows with guard region */
      r2= Math.min((estR2+guardRows),(nRows-1));
    
    if(drawIntoImageFlag)
    { /* set limits to entire image */
      r1= 0;    /* rows with guard region */
      r2= nRows-1;
    }
    
   /*
    if(mae.DBUG_HCN && nRows<20)
    {
      mae.fio.logMsgln("CGC-P row["+(selRow+1)+
                       "], viewPortSize="+viewPortSize+
                       ", viewPort[HxW]=["+vPortHeight+
                       "x="+vPortWidth+"]");
      mae.fio.logMsgln("   VA[min:max]=["+minVA+":"+maxVA+
                       "], val[VA]=["+valueVA+"]");
      mae.fio.logMsgln("   pct="+ Util.cvf2s(100.0F*pct,3)+
                       ", estR1="+estR1+
                       ", estR2="+estR2+
                       ", rowsPerVP="+rowsPerVP+
                       "  guardRows="+guardRows+
                       ", [r1:r2]=["+r1+":"+r2+"]");
      }
     */
    
    /* [4] Draw the rows of EP boxes and labels in a continuous array
     * but only in viewport + guard region.
     */    
    float dataV[];
    for(int r=r1;r<=r2;r++)
    { /* add a row of boxes only in viewport + guard region */
      dataV= objDataV[r];
      drawRow(g, r, dataV);
    }
    
    /* [4.1] Add the white circle on top of box if box was selected
     * at (selRow,selCol).
     */
    if(r1<=selRow && selRow<=r2)
    { /* add a circle at selected box */
      drawSelectedHP(g,r1,r2);
    }
    
    /* [4.2] Copy genes in dendrogram subtree if box was selected
     * at (selRow,selCol).
     */
    if(r1<=selRow && selRow<=r2)
    { /* copy genes in SubTree to EGL */
      copyGenesInSubtreeToEGL();
    }
    
    /* [5] Draw the dendrogram if required or rows r1 to r2  */
    if(dcg.drawDendroGramFlag)
      drawDendrogram(g,r1,r2);
    
    /* [6] If drawing to a GIF file, then cvt Image to Gif stream
     * and write it out. Then repaint without the gifImage which
     * will then redraw the image in the window.
     */
    if(drawIntoImageFlag && gifImage!=null)
    { /* write it out */
      drawIntoImageFlag= false;
      WriteGifEncoder  wge= new WriteGifEncoder(gifImage);
      gifImage= null;
      if(wge!=null)
        wge.writeFile(oGifFileName);
      
      repaint();   /* refresh the actual canvas */
    } /* write it out */    
  } /* paint */
    
  
} /* end of ClusterGramCanvas class */

