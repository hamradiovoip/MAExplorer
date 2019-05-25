/** File: ExprProfileOverlay.java */

import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.*;

/** 
 * This class displays a scrollable expression profile overlay.
 * It extends the Draw2Dplot class and consists of an overlay plot of the 
 * expression profiles of genes in the specifimed gene list. 
 * Selecting a particular profile sets the current gene and calls 
 * the PopupRegistry to update other windows. 
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
 * @version $Date: 2003/07/07 21:40:41 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ExprProfile
 * @see ShowPlotPopup
 */


 
class ExprProfileOverlay extends Panel implements AdjustmentListener
{
    
/*
 * [TODO] 
 *</PRE>
 * 1. Debug why only some points are active and caught by
 *    the mouse handler. Need to redo it so can interact better.
 *
 * 2. Finish implementing scrolling so can see the regions better
 *
 * 3. Add multiple color plots for multiple sets of genes and a popup
 *    scrollable panel list of gene sets checkboxes with matching colors 
 *    to filter which gene sets get displayed. Because they may be many 
 *    sets the panel should also cycle through a variety of symbols as
 *    well as colors.
 *    Colors: [red, yel, org, green, cyan, blue, purple, (black?)]
 *    Filled shapes: [Circle, Square, Triangle, Diamond, X, etc.]
 *    e.g.
 *
 *    -------------------------------
 *     [x] gene set 1 (red circle)
 *     [ ] gene set 2 (orange circle)
 *        . . .
 *     [x] gene set p (green square)
 *        . . .
 *     [ ] gene set N (blue diamond)
 *    -------------------------------
 </PRE>
*/
  /** links to global MAExplorer instance */
  private MAExplorer
    mae;      
                      
  /** Parent window */
  ShowExprProfilesPopup
    sepp;                       
  /** title for entire EP. panel */
  private String
    title;                      
  /** GeneList being used */
  private GeneList 
    exprProfileCL;		
  /** # genes in exprProfileCL */
  private int
    nGenes;                    
  /** width of scroll bar control */
  private int
    sbSize;                     
  /** save previous MID found with mouse */
  private int
    prevBestMID;                
  /** .mList[mNbr] in database to display */
  private int
    mNbr;                       
  /** list of overlay expr. profiles */
  private ExprProfile
    epOverlayList[];            
  /** draw 2D overlay of EP plots */
  Draw2Dplot
    d2dp;                                           
  /** Expr Profs [nGridElements] */
  private ExprProfilePanel
    epplList[];                 
  /** titles [0:nGenes-1] "#Master_ID" */
  private String
    cTitle[];                      
  /** # of HPS in an Expr Profile */
  private int
    nHP_E;                                           
  /** # of averaged genes ~ nGenes */
  private int
    nMnGenes;                                       
  /** # of samples for hpDataXXX[n][] */
  private int
    hpDataNbrA[];               
  /** max of hpDataMnA[][] data */
  private float
    maxHPdataMn;                               
  /** mean[0:nMnGenes-1][0:nHP_E-1] */  
  private float
    hpDataMnA[][];                           
  /** S.D.[0:nMnGenes-1][0:nHP_E-1] */
  private float
    hpDataSDA[][];              
  /** pass through to EP panel */ 
  private boolean 
    showErrBarsFlag;                         
  /** if plot means and SD */ 
  private boolean 
    doMeanPlotsFlag;                         
  /** if hpDataXXX[] exists */
  private boolean 
    hasMeanPlotsFlag;           
  /**plot style mode:  0=line, 1=circle, 2=curve */ 
  int
    plotStyleMode;              
  /** zoom factor: 1X, 2X, 5X, 10X pass through to EP panel */
  int
    zoomBarsMag;                
      
  /** holds g2D overlay plot */
  private Panel
    epPanel;			
  
  /** lower bound horizontal scroll bar  */
  private Scrollbar 
    hsL;		        
  /** upper bound horizontal scroll bar  */
  private Scrollbar
    hsU;		        
  /** lower bound vertical scroll bar  */
  private Scrollbar
    vsL;		        
  /** upper bound vertical scroll bar  */
  private Scrollbar
    vsU;	  
  /** # of steps for hsL hsU */
  private int
    maxHsteps;                
  /** # of steps for vsL vsU */
  private int
    maxVsteps;                  
  /** lower HP sample # to display */
  private float
    hLB;                        
  /** upper HP sample # to display */
  private float
    hUB;                        
  /** lower expression value to display */
  private float
    vLB;                        
  /** upper expression value to display */
  private float
    vUB;                        
  
  /** [nGenes*nHP_E] EP overlay X coords */
  private float	 
    xList[];                    
  /** [nGenes*nHP_E] EP overlay Y coords */
  private float	
    yList[];                    
  /** nGenes*nHP_E */  
  private int
    nPoints;    
  /** [nGenes*nHP_E] EP overlay props */
  private int
    propList[];                 
  /** [nGenes*nHP_E] mid mapping */ 
  private int
    mapPointToMid[]; 
                 
  /** special [nGenes*nHP_E] CL */
  private GeneList
    epOvrLayCL;  
  
  /** gene if mouse event */
  private Gene
    bestGene;                  
  /** HP if mouse event */ 
  private int
    bestHP;                 
  /** gene clone ID if mouse event */
  private int
    bestMid;                    
  /** intensity if mouse event */
  private float
    bestIntens;                 
    
        

  /**
   * ExprProfileOverlay() - Create popup overlay plot of ExprProfiles.
   * Set the Filter.workingCL if the specified list is null.
   * @param mae is the MAExplorer instance
   * @param sepp is the ShowExprProfilesPopup Parent
   * @param GeneList exprProfileCL is thege ne list to use
   * @param title is the window title
   * @param preferredPanelWidth is set by parent Frame if not 0
   * @param showKmeansDataFlag to report Kmeans data else just Master_ID and name
   * @see Draw2Dplot
   * @see ExprProfile
   * @see Util#showMsg
   * @see #updateData
   */
  ExprProfileOverlay(MAExplorer mae,ShowExprProfilesPopup sepp,
                     GeneList exprProfileCL, String title,
                     int preferredPanelWidth,  boolean showKmeansDataFlag)
  { /* ExprProfileOverlay */
    super();
    
    this.mae= mae;
    this.sepp= sepp;
    this.title= title;
    
    nHP_E= mae.hps.nHP_E;
    nPoints= 0;
    nMnGenes= 0;       /* default is none */
    hasMeanPlotsFlag= false;
    zoomBarsMag= 1;     /* 1X, 2X, 5X, 10X pass through to EP panel */
    prevBestMID= -2;    /* starts things up */
    
    /* Setup the GeneList to use */
    this.exprProfileCL= exprProfileCL;
    nGenes= exprProfileCL.length;
    if(nGenes==0)
    {
      Util.showMsg("No Filtered genes");
      return;
    }
    
    cTitle= new String[mae.mp.maxGenes];  /* titles for each gene */
    
   /* If Grid of EPpanels, then add a panel in the Center of the
    * panel which contains the grid of EP plots.
    * Add Scroll bars on Vert.
    */
    int
      hgap= 1,                /* gaps between grid elements */
      vgap= 1;
    
    this.setLayout(new BorderLayout(0,0)); /* make scroll bars flush to image*/
    
    epPanel= new Panel();     /* add panel to hold 2D plot */
    epPanel.setLayout(new FlowLayout()); /* 2D plot */
    this.add("Center",epPanel);
    
    /* Add scroll bar ranges to scroll sample range (horizontal)
     * and intensity expression range (vertical)
     */
    sbSize= 1;
    hsU= null;                 /* scroll the sample range */
    hsL= null;
    vsU= null;                 /* scroll the intensity range */
    vsL= null;
    
    Panel
      hPanel= new Panel(),     /* panel to hold horiz sliders */
      vPanel= new Panel();     /* panel to hold vertical sliders */
    
    hPanel.setLayout(new GridLayout(1,2, /*R,C*/ 1,1 /*gap*/));
    vPanel.setLayout(new GridLayout(2,1, /*R,C*/ 1,1 /*gap*/));
    if(mae.LECBdebug==0)
    { /* add scroll bars */
      this.add("South",hPanel);
      this.add("West",vPanel);
    }
    
    /* Create horizontal scrollbar range */
    maxHsteps= 200;
    hsL= new Scrollbar(Scrollbar.HORIZONTAL, 0, sbSize, 0, maxHsteps+sbSize);
    hsL.addAdjustmentListener(this);
    hPanel.add(hsL);
    hsU= new Scrollbar(Scrollbar.HORIZONTAL, maxHsteps+sbSize, sbSize,
                       0, maxHsteps+sbSize);
    hsU.addAdjustmentListener(this);
    hPanel.add(hsU);
    
    /* Create vertical scrollbar range */
    maxVsteps= 1000;
    vsU= new Scrollbar(Scrollbar.VERTICAL, 0, sbSize, 0, maxVsteps+sbSize);
    vsU.addAdjustmentListener(this);
    vPanel.add(vsU);
    vsL= new Scrollbar(Scrollbar.VERTICAL, maxVsteps+sbSize, sbSize,
                       0, maxVsteps+sbSize);
    vsL.addAdjustmentListener(this);
    vPanel.add(vsL);
    
    /* Create 2D plot of EP overlay data */
    epOverlayList= new ExprProfile[nGenes];    
    Gene
      gene,
      mList[]= exprProfileCL.mList;
    
    for(int i=0;i<nGenes;i++)
    { /* create EP data */
      gene= mList[i];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      
      /* Create Expr Profile data for gene gene.mid */
      epOverlayList[i]= new ExprProfile(mae, gene.mid, false);
    }
    
    d2dp= new Draw2Dplot(mae, -1, /* plotMode2D */
                         (ShowPlotPopup)null,   /* ShowPlotPopup */
                         this, title,
                         nGenes*mae.hps.nHP_E,  /* nPoints */
                         mae.hps.nHP_E,         /* points in the line group */
                         true                   /* useXaxisIntNbrFlag - label
                                                 * all vert lines w/int # */
                         );
    epPanel.add(d2dp);
    
    updateData(exprProfileCL, title);   /* update and repaint */
  } /* ExprProfileOverlay */
  
  
  /**
   * drawGifFile() - draw plot into Gif image file if in standalone mode.
   * This sets it up and lets paint() to the heavy lifting...
   * @param oGifFileName is the full path GIF output file
   * @return false if unable to generate the image file.
   * @see #repaint
   */
  boolean drawGifFile(String oGifFileName)
  { /* drawGifFile */
    if(mae.isAppletFlag || oGifFileName==null || d2dp==null)
      return(false);
    
    d2dp.drawIntoImageFlag= true;
    d2dp.oGifFileName= oGifFileName;
    repaint();          /* will start the process */
    
    return(true);
  } /* drawGifFile */
  
  
  /**
   * updateData() - update exprression profile scroller with new gene data.
   * This will cause it to repaint as well.
   * @param exprProfileCL is the new list of genes
   * @param title for canvas label
   * @see #repaint
   */
  void updateData(GeneList exprProfileCL, String title )
  { /* updateData */
    this.exprProfileCL= exprProfileCL;
    nGenes= exprProfileCL.length;
    this.title= title;
    
    repaint();
  } /* updateData */
  
  
  /**
   * setEPcanvasDrawingOptions() - set err-bar and zoom status and then repaint.
   * @param showErrBarsFlag to show error bars in the plot
   * @param zoomBarsMag is the zoom magnification
   * @param plotStyleMode is the style mode as line, circle, curve
   * @see #repaint
   */
  void setEPcanvasDrawingOptions(boolean showErrBarsFlag, int zoomBarsMag,
                                 int plotStyleMode)
  { /* setEPcanvasDrawingOptions */
    this.showErrBarsFlag= showErrBarsFlag;
    this.zoomBarsMag= zoomBarsMag;
    this.plotStyleMode= plotStyleMode;
    repaint();
  } /* setEPcanvasDrawingOptions */
  
  
  /**
   * setPlotEPmeansData() - set expression profile to plot mean data for each panel
   * @param nMnGenes is the # of averaged genes
   * @param hpDataNbrA is a list of the # samples hpDataXXX[n][]
   * @param hpDataMnA is a list of the mean[0:nMnGenes-1][0:nHP_E-1] data
   * @param hpDataSDA is a list ofthe  S.D.[0:nMnGenes-1][0:nHP_E-1] data
   * @param doMeanPlotsFlag if true
   * @see #repaint
   */
  void setPlotEPmeansData(int nMnGenes, int hpDataNbrA[], float hpDataMnA[][],
                          float hpDataSDA[][], boolean doMeanPlotsFlag)
  { /* setPlotEPmeansData */
    this.nMnGenes= nMnGenes;
    this.hpDataNbrA= hpDataNbrA;
    this.hpDataMnA= hpDataMnA;
    this.hpDataSDA= hpDataSDA;
    this.doMeanPlotsFlag= doMeanPlotsFlag;
    hasMeanPlotsFlag= (nMnGenes>0);
    
    /* Compute dynamic range of means  [0:-]*/
    if(hasMeanPlotsFlag)
    { /* has mean plots */
      maxHPdataMn= 0.0F;
      float dnh;
      int
        h,
        nHP_E= mae.hps.nHP_E;
      for(int n=0;n<nMnGenes;n++)
        for(h=0;h<nHP_E;h++)
        {
          dnh= hpDataMnA[n][h];
          if(dnh > maxHPdataMn)
            maxHPdataMn= dnh;
        }
    } /* has mean plots */
    
    repaint();
  } /* setPlotEPmeansData */
  
  
  /**
   * paint() - redraw plots near selected row.
   * @param g is graphics context
   * @see Draw2Dplot#update2Dplot
   * @see GeneBitSet#setItem
   * @see GeneList
   * @see GeneList#clear
   */
  public void paint(Graphics g)
  { /* paint */
    /* [1] Compute range of mids to display */
    int
      nHP_E= mae.hps.nHP_E,
      cI,
      mid,
      nList= 0;
    
    /* [2] Draw the list of active EP plots in d2dp as
     * 2D plot of EP overlay data.
     */
    ExprProfile ep;
    float
      maxY= -100000000.0F,
      y;
    Gene mList[]= exprProfileCL.mList;
    
    if(nPoints!=nGenes*nHP_E)
    { /* reallocate */
      nPoints= nGenes*nHP_E;
      xList= new float[nPoints];    /* coordinates */
      yList= new float[nPoints];
      propList= new int[nPoints];
      mapPointToMid= new int[nPoints];
      epOvrLayCL= new GeneList(mae,nPoints,"EPovrLay",true);
    }
    epOvrLayCL.clear();   /* special list */
    
    for(int c=0;c<nGenes;c++)
    { /* update lists for all genes */
      ep= epOverlayList[c];
      for(int j=0;j<nHP_E;j++)
      { /* push points in plot */
        xList[nList]= j+1;
        y= (hasMeanPlotsFlag) ? ep.hpDataMn[j] : ep.hpData[j];
        if(maxY<y)
          maxY= y;
        yList[nList]= y;
        
        //epOvrLayCL.addGene(mList[c]);   /* can NOT use SETs */
        epOvrLayCL.mList[nList]= mList[c]; /* set LIST directly*/
        if(mList[c]!=null)
        {
          propList[nList]= mList[c].properties;        
          epOvrLayCL.bitSet.setItem(mList[c].mid);
          mapPointToMid[nList]= mList[c].mid;
        }
       /*
       if(mae.CONSOLE_FLAG && mae.cdb!=null && mae.cdb.objMID==mList[c].mid)
         System.out.println("EPSP c="+c+" nList="+nList+
                            " j="+j+" x="+(j+1)+" y="+y+
                            " cdb.objMID="+mae.cdb.objMID);
        */
        nList++;
      } /* push points in plot */
    } /* update lists for all genes */
    
    d2dp.update2Dplot("Expr", "HP samples",
                      title, "Expression Profile Overlays",
                      "", /* title1*/ "", /* title2 */
                      " ["+mae.normNameDisp+"]",
                      "", /* aText */ 0, /* xText */ 0, /* yText */
                      1.0, 1.0,
                      1.0, (double)nHP_E, /* X-axis */
                      0.0, (double)maxY,  /* Y-axis */
                      false,              /* draw 45 line */
                      false,              /* draw 180 line */
                      xList, yList, propList,
                      epOvrLayCL, nList,
                      mapPointToMid);
  } /* paint */
  
  
  /**
   * updateCurGene() - update the current gene in the expression profile plot list
   * @param mid is the Master Gene Index if not -1
   * @see PopupRegistry
   */
  void updateCurGene(int mid)
  { /* updateCurGene */
    Gene mListEP[]= exprProfileCL.mList;
    
    for(int i=0;i<nGenes;i++)
      if(mListEP[i].mid==mid)
      { /* found it */
      /* See if mid is in the visible EP plot window of
       * nGridElements in which case just repaint. Otherwise,
       * move the scroll bar.
       */
        boolean inEPplotCache= false;
        
        /* look at EP's in epOverlayList */
        for(int j=0;j<nGenes;j++)
          if(epOverlayList[i].mid==mid)
          {
            inEPplotCache= true;
            break;
          }
        
        repaint();
        break;
      } /* found it */
  } /* updateCurGene */
  
  
  /**
   * adjustmentValueChanged() - handle scroll events and cause a repaint.
   * @param e is scroller adjustment event
   * @see Draw2Dplot#updateScrolledRegion
   * @see #repaint
   */
  public void adjustmentValueChanged(AdjustmentEvent e)
  { /* adjustmentValueChanged */
    int
      hsValLB= 0,
      hsValUB= 0,
      vsValLB= 0,
      vsValUB= 0;
    
    if(hsL!=null)
    { /* set the sample range to be displayed */
      hsValLB= hsL.getValue();
      hsValUB= hsU.getValue();
      if(hsValLB>=hsValUB)
      { /* keep LB < UB */
        hsValLB= hsValUB-1;
        hsL.setValue(hsValLB);
      }
    }
    
    if(vsL!=null)
    { /* Set the intensity (expression) range to be displayed */
      vsValLB= vsL.getValue();
      vsValUB= vsU.getValue();
      if(vsValLB<=vsValUB)
      { /* keep LB > UB since complement value is vertical */
        vsValLB= vsValUB+1;
        vsL.setValue(vsValLB);
      }
    }
    
    /* [TODO] Update Scrolled Region:*/
    hLB= (float)(hsValLB/maxHsteps);
    hUB= (float)(hsValUB/maxHsteps);
    vLB= (float)((sbSize-vsValLB)/maxVsteps);
    vUB= (float)((sbSize-vsValUB)/maxVsteps);
    
    d2dp.updateScrolledRegion(hLB,hUB,vLB,vUB);
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("EPOV-AVC hLB="+hLB+" hUB="+hUB+
                         " vLB="+vLB+" vUB="+vUB);
       */
    repaint();
  } /* adjustmentValueChanged */
  
  
  
  /**
   * mouseHandler() - interact with current 2D plot if a graphic plot.
   * If get a hit, then display data for the gene.
   * Note: because the plot lives in the ScrollerImageCanvas, there
   * is only ONE plot so can't get into trouble with multiple plots.
   * @param x is x coordinate at mouse event
   * @param y is y coordinate at mouse event
   * @param mouseKeyMods is keyboard status at mouse event
   * @param setCurGeneFlag if true
   * @see Draw2Dplot#set2DplotSubtitles
   * @see #showGeneOfInterest
   * @see #repaint
   */
  public void mouseHandler(int x, int y, int mouseKeyMods,
  boolean setCurGeneFlag)
  { /* mouseHandler */
    if(d2dp.eventHandlerCL==null || d2dp.nPointsPlotted==0)
      return;
    
    int
      threshold= 3,                /* # pixels max can click away */
      thrSQ= threshold*threshold,
      bestI= -1,
      bestLSQerr= 100000000;
    Gene mList[]= d2dp.eventHandlerCL.mList;
    
    bestGene= null;                   /* init */
    bestMid= -1;
    bestIntens= 0.0F;
    bestHP= 0;
    
    for(int i=0;i<d2dp.nPointsPlotted;i++)
    { /* find closest point */
      int
        dx= (d2dp.xPlotted[i]-x),
        dy= (d2dp.yPlotted[i]-y),
        lsqErr= (dx*dx + dy*dy);
      
      if(bestLSQerr>lsqErr && mList[i]!=null && mList[i].mid!=-1)
      { /* latch onto better one */
        bestLSQerr= lsqErr;
        bestI= i;
        bestMid= mList[i].mid;
        bestGene= mList[i];
        bestIntens= d2dp.yList[i];
        bestHP= (int)d2dp.xList[i];
      }
    } /* find closest point */
      /*
      if(mae.CONSOLE_FLAG)
        System.out.println("D2DP-MH foundIt="+(bestLSQerr<=thrSQ)+
                           " bestI="+bestI+" bestMid="+bestMid+
                           " bestIntens="+bestIntens+
                           " d2dp.nPointsPlotted="+d2dp.nPointsPlotted);
       */
    
    if(bestLSQerr<=thrSQ)
    { /* Found it!! - Go process */
      showGeneOfInterest(bestMid, mouseKeyMods,setCurGeneFlag);
      prevBestMID= bestMid;
    }
    else if(prevBestMID!=-1)
    { /* clear it out */
      d2dp.set2DplotSubtitles(null,null,null,null);
      repaint();
      prevBestMID= -1;
    }
    return;
  } /* mouseHandler */
  
  
  /**
   * setCurGene() - set current gene from mid if it is not -1.
   * @param mid is the Master Gene Index if not -1
   * @see CompositeDatabase#setObjCoordFromMID
   */
  private void setCurGene(int mid)
  { /* stCurGene */
    if(mid==-1)
      return;
    int gid1= mae.mp.mid2gid[mid];
    if(gid1==-1)
      return;
    
    /* If Expression Profile mode is on, then draw it */
    mae.cdb.setObjCoordFromMID(mid,this);
  } /* setCurGene */
  
  
  /**
   * showGeneMsgsAndPlot() - show quant genomic data messages in MSG and in plot
   * @see Draw2Dplot#set2DplotSubtitles
   * @see GridCoords#cvtGID2str
   * @see SpotFeatures#getSpotFeatures
   * @see SpotFeatures#getSpotGenomicData
   * @see Util#showMsg
   * @see Util#showFeatures
   */
  private void showGeneMsgsAndPlot()
  { /* showGeneMsgsAndPlot */
    if(bestMid==-1)
      return;
    int gid1= mae.mp.mid2gid[bestMid];
    MaHybridSample ms= mae.hps.msListE[bestHP];
    
    ms.getSpotDataStatic(gid1,mae.useRatioDataFlag);
    /* lookup the XY coords of spot */
    Point xyGid1= SpotData.xyS;
    
    String
      sIntensity= (!mae.bkgdCorrectFlag) ? "intens" : "intens'",
      maCoords= mae.grcd.cvtGID2str(gid1,false),
      xygStr= maCoords + "#"+bestHP+" ["+ mae.hps.msListE[bestHP].hpName+
              "] "+sIntensity+"="+Util.cvf2s(bestIntens,4),
      featuresStr= mae.sf.getSpotFeatures(xyGid1, mae.ms),
      genomicDataStr= mae.sf.getSpotGenomicData(xyGid1, mae.ms),
      numericData= xygStr + ", ("+mae.normNameDisp+")";
    
    Util.showMsg(numericData);
    Util.showFeatures(featuresStr, genomicDataStr);
    
    d2dp.set2DplotSubtitles(numericData, featuresStr, genomicDataStr,null);
  } /* showGeneMsgsAndPlot */
  
  
  /**
   * showGeneOfInterest() - show quant and genomic data if click on point in plot
   * @param mid is the Master Gene Index if not -1
   * @param mouseKeyMods is keyboard status at mouse press event
   * @param setCurGeneFlag if true
   * @see MAExplorer#repaint
   * @see PopupRegistry#chkOtherCurGeneEffects
   * @see #setCurGene
   * @see #showGeneMsgsAndPlot
   * @see #repaint
   */
  private void showGeneOfInterest(int mid, int mouseKeyMods,
  boolean setCurGeneFlag)
  { /* showGeneOfInterest*/
    if(mid==-1)
      return;
    
    if(setCurGeneFlag)
      setCurGene(mid);      /* set the current gene */
    
    showGeneMsgsAndPlot();  /* show quant&NCBI data messages in MSGs
     * and in the plot */
    
      /* when change current gene in ms.objXXX. Then put things here to
       * do when change the gene which are side effects.
       * This includes modifying the 'editable gene list'
       */
    if(setCurGeneFlag)
      mae.pur.chkOtherCurGeneEffects(mid,mouseKeyMods);
    
    mae.repaint();
    repaint();              /* redraw this plot */
  } /* showGeneOfInterest*/
  
} /* end of ExprProfileOverlay class */

