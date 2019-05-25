/** File: ExprProfileScrollPane.java */

import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.*;

/**
 * This creates and displays a scrollable pane list of individual expression profile graphic plots. 
 * It will be shown as a Grid of ExprProfilePanel's plots in popup window. 
 * Selecting a particular ExprProfilePanel will set the current gene and call 
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
 * @author P. Lemkin (NCI), G.Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2003/07/07 21:40:41 $   $Revision: 1.7 $ 
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ExprProfilePanel
 * @see ShowPlotPopup
 */
 
class ExprProfileScrollPane extends Panel implements AdjustmentListener
{
  /** link to global MAExplorer instance */  
  private MAExplorer
    mae;       
  
  /** minimum # of rows for the grid */
  final private static int
    MIN_ROWS= 5;                
  /** minimum # of columns for the grid */
  final private static int
    MIN_COLS= 2;
  
  /** Parent window */
  private ShowExprProfilesPopup
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
  /** */
  final int
    N_EP_GENES= 10;
   /** width of scroll bar control */
  private int
    sbSize;                    
  /** i.e. nRows*nCols */
  private int
    nGridElements;             
  /** # of rows  in the Grid */
  private int
    nRows= MIN_ROWS;     
  /** # of columns in the Grid */
  private int
    nCols= MIN_COLS;
  /** .mList[mNbr] in database to display */
  private int
    mNbr; 
  /** # genes to display in popup EP plot */
  private int
    nEPgenes= N_EP_GENES; 
  
  /** list of overlay expr. profiles */
  private ExprProfile
    epOverlayList[];            
  /** Expr Profs [nGridElements] */
  private ExprProfilePanel
    epplList[];                 
  /** [0:nGenes-1] "#Master_ID" */  
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
  
  /** plot style mode: 0=line, 1=circle, 2=curve */
  int
    plotStyleMode;      
  /** 1X, 2X, 5X, 10X pass through to EP panel */
  int
    zoomBarsMag;                  
  /** holds grid of ExprProfilePanel's */
  private Panel
    epPanel;			
  /** vertical scroll bar  */
  private Scrollbar 
    vs;			        
  /** [nGenes*nHP_E] EP overlay coords */
  private float	 
    xList[];                    
  /** [nGenes*nHP_E] EP overlay coords */
  private float	 
    yList[];                   
  /** nGenes*nHP_E */
  private int
    nPoints;                       
  /** [nGenes*nHP_E] EP overlay props */
  private int   
    propList[];                 
  
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
   * ExprProfileScrollPane() - Create a popup grid of ExprProfile plots.
   * Set the  Filter.workingCL if the specified list is null.
   * @param mae is MAExplorer instance
   * @param sepp is the ShowExprProfilesPopup Parent
   * @param exprProfileCL is the gene list to use
   * @param title of window
   * @param preferredPanelWidth is the set by parent Frame if not 0
   * @param showKmeansDataFlag is the report Kmeans data else just CloneID and name
   * @see ExprProfilePanel
   * @see Util#showMsg
   * @see #updateData
   */
  ExprProfileScrollPane(MAExplorer mae, ShowExprProfilesPopup sepp,
  GeneList exprProfileCL, String title,
  int preferredPanelWidth, boolean showKmeansDataFlag )
  { /* ExprProfileScrollPane */
    super();
    
    this.mae= mae;
    this.sepp= sepp;
    this.title= title;
    
    nHP_E= mae.hps.nHP_E;
    nPoints= 0;
    nMnGenes= 0;     /* default is none */
    hasMeanPlotsFlag= false;
    zoomBarsMag= 1;   /* 1X, 2X, 5X, 10X pass through to EP panel */
    
    /* Setup the GeneList to use */
    this.exprProfileCL= exprProfileCL;
    nGenes= exprProfileCL.length;
    if(nGenes==0)
    {
      Util.showMsg("No Filtered genes");
      return;
    }
    
    /* Compute # of grids to use */
    nCols= (nGenes<MIN_COLS)
    ? nGenes : MIN_COLS; /* Math.min(nGenes, MIN_COLS) */
    int n= (nGenes/nCols);
    if(n*nCols<nGenes)
      n++;
    nRows= (n>1) ? n : 1;  /* Math.max(1,n) */
    nRows= Math.min(MIN_ROWS,nRows);
    nGridElements= nRows*nCols;
    
    cTitle= new String[mae.mp.maxGenes];  /* titles for each gene */
    
   /* If Grid of EPpanels, then add a panel in the Center of the
    * panel which contains the grid of EP plots.
    * Add Scroll bars on Vert.
    */
    int
      hgap= 1,                /* gaps between grid elements */
      vgap= 1;
    
    this.setLayout(new BorderLayout(0,0)); /* make scroll bars flush to image*/
    
    epPanel= new Panel();     /* add panel to hold the grid or 2D plot */
    epPanel.setLayout(new GridLayout(nRows,nCols,hgap,vgap));
    this.add("Center",epPanel);
    
    sbSize= 1;
    if(nGenes > nGridElements)
    {
      vs= new Scrollbar(Scrollbar.VERTICAL, 0, sbSize, 0,
      nGenes+sbSize+nRows);
      vs.addAdjustmentListener(this);
      this.add("East", vs);
    }
    else
      vs= null;
    
    /* Add EP plots to each grid element */
    boolean drawLabelsFlag= true;
    Font epLabelFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
    
    /* Create fixed size grid of generic EP plots */
    epplList= new ExprProfilePanel[nGridElements];
    for(int i=0;i<nGridElements;i++)
    { /* add an EP plot for each gene */
      Gene gene= exprProfileCL.mList[i];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      
      /* Create empty Expr Profile Canvas in grid slot */
      epplList[i]= new ExprProfilePanel(mae, -2, "",
                                        epLabelFont,
                                        preferredPanelWidth,
                                        0, /*preferredPanelHeight */
                                        showKmeansDataFlag,
                                        drawLabelsFlag);
      epPanel.add(epplList[i]);
    } /* add an EP plot for grid slot */
    
    updateData(exprProfileCL, title);   /* update and repaint */
  } /* ExprProfileScrollPane */
  
  
  /**
   * updateEPtitles() - generate array of titles for drawing in expression profile plots
   */
  private void updateEPtitles()
  { /* updateEPtitles */
    for(int i=0;i<nGenes;i++)
    { /* get data for each gene */
      Gene gene= exprProfileCL.mList[i];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      
      int mid= gene.mid;
      //cTitle[i]= "MID:"+gene.Master_ID;	   /* depricate prefix */
      cTitle[i]= gene.Master_ID;
    } /* get data for each gene */
  } /* updateEPtitles */
  
  
  /**
   * updateData() - update expression profile scroller with new gene data.
   * This will cause it to repaint as well.
   * @param exprProfileCL is the new list of genes
   * @param title is the title for the canvas label
   * @see #updateEPtitles
   * @see #repaint
   */
  void updateData(GeneList exprProfileCL, String title )
  { /* updateData */
    this.exprProfileCL= exprProfileCL;
    nGenes= exprProfileCL.length;
    this.title= title;
    
    updateEPtitles(); /* generate array of titles for drawing in EP plots */
    repaint();
  } /* updateData */
  
  
  /**
   * setEPcanvasDrawingOptions() - set err-bar & zoom status and then repaint.
   * @param showErrBarsFlag is used if their is duplicate data
   * @param zoomBarsMag magnification factor (1,2,5,10,20) for expression data
   * @param plotStyleMode (line, circle, curve)
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
   * setPlotEPmeansData() - set EP to plot means for each panel
   * @param nMnGenes is the # of averaged genes
   * @param hpDataNbrA is the # samples hpDataXXX[n][]
   * @param hpDataMnA is the mean[0:nMnGenes-1][0:nHP_E-1] expression data
   * @param hpDataSDA is the S.D.[0:nMnGenes-1][0:nHP_E-1] expression data
   * @param doMeanPlotsFlag if plot the mean data
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
    {
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
    }
    
    repaint();
  } /* setPlotEPmeansData */
  
  
  /**
   * paint() - redraw plots near selected Row.
   * @param g is graphics context
   * @see ExprProfilePanel#setPlotEPmeansData
   * @see ExprProfilePanel#updateData
   */
  public void paint(Graphics g)
  { /* paint */
    /* [1] Compute range of mids to display */
    int
      nHP_E= mae.hps.nHP_E,
      cI,
      mid= -1;
    
      /* [2] Draw the list of active EP plots in fixed size
       * grid of generic EP plots.
       */
    sepp.epspStatusLabel.setText("");   /* clear title */
    for(int i=0;i<nGridElements;i++)
    { /* update ExprProfile only for genes in range */
      cI= mNbr+i;
      if(cI>=nGenes)
        mid= -2;           /* clear the panel at the bottom */
      else
      {
        Gene gene= exprProfileCL.mList[cI];
        if(gene==null)
          continue;
        mid= gene.mid;
      }
      
     /* If enabled, set the mean and SD data into each panel
      * when draw it. Assign hpDataXXX[] to nGridElements from
      * selected Row active displayed EP panels.
      */
      if(hasMeanPlotsFlag)
        epplList[i].setPlotEPmeansData(mid, hpDataNbrA[cI],
                                       maxHPdataMn, hpDataMnA[cI],
                                       hpDataSDA[cI], doMeanPlotsFlag);
      /* Copy the flags */
      epplList[i].epc.showErrBarsFlag= showErrBarsFlag;
      epplList[i].epc.zoomBarsMag= zoomBarsMag;
      epplList[i].epc.plotStyleMode= plotStyleMode;
      /*
      if(mae.CONSOLE_FLAG)
          mae.fio.logMsgln("EPSPane-paint i="+i+" cI="+cI+
                           " mid="+mid+ " cTitle[i]='"+cTitle[i]+
                           " mNbr="+mNbr);
      */
      
      /* Cause that panel to update itself with this gene */
      String titleI= (cI>=nGenes) ? "EOL" : "["+(cI+1)+"] "+cTitle[cI];
      epplList[i].updateData(mid,titleI,true /* drawLabelsFlag */);
      if(mid==mae.cdb.objMID)
      {
        Gene gene= mae.mp.midStaticCL.mList[mid];
        if(gene!=null)
          sepp.epspStatusLabel.setText("Current gene "+titleI +
                                       ", "+gene.Gene_Name);
      }
    } /* update ExprProfile only for genes in range */
  } /* paint */
  
  
  /**
   * updateCurGene()- update the current gene in the EP plot list.
   * @param mid is the new Master Gene ID of the current gene
   * @see #repaint
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
        
        /* look at EP's in GRID */
        for(int j=0;j<nGridElements;j++)
          if(epplList[j].epc.ep.mid==mid)
          {
            inEPplotCache= true;
            break;
          }
        
        if(!inEPplotCache)
        { /* refresh the cache */
          mNbr= Math.max(0,(i-MIN_ROWS));
          if(vs!=null)
            setScrollBarPos(mNbr);
        }
        repaint();
        break;
      }
  } /* updateCurGene */
  
  
  /**
   * setScrollBarPos() - set the scroll barPosition for this panel.
   * The arguments are in the range of allowable values
   * otherwise it is a no-op.
   * @param cNumber is data range to be mapped to scroller range
   */
  private void setScrollBarPos(int cNumber)
  { /* setScrollBar Pos*/
    int
      vsMax= vs.getMaximum()-sbSize,
      y= (vsMax*cNumber)/nGenes;
    
    if(y>0 && y<vsMax)
      vs.setValue(y+sbSize);
  } /* setScrollBarPos */
  
  
  /**
   * adjustmentValueChanged() - handle scroll events and cause repaint.
   * @param e is scroller adjustment event
   * @see #repaint
   */
  public void adjustmentValueChanged(AdjustmentEvent e)
  { /* adjustmentValueChanged */
    int val= vs.getValue();
    mNbr= val-sbSize;
    mNbr= Math.max(0,Math.min(mNbr, nGenes-2));
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("EPSPane-AVC val="+val+ " mNbr="+mNbr+ " sbSize="+sbSize);
     */
    repaint();
  } /* adjustmentValueChanged */
  
  
  /**
   * setCurGene() - set current gene if mid is not -1
   * @param mid is the new Master Gene ID of the current gene
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
   * showGeneOfInterest() - show quant and genomic data if click on a point in plot
   * @param mid is the new Master Gene ID of the current gene
   * @param mouseKeyMods is SHIFT or CONTROL key modifiers if not 0
   * @see PopupRegistry#chkOtherCurGeneEffects
   * @see MAExplorer#repaint
   * @see #setCurGene
   * @see #repaint
   */
  private void showGeneOfInterest(int mid, int mouseKeyMods)
  { /* showGeneOfInterest*/
    if(mid==-1)
      return;
    
    setCurGene(mid);               /* set the current gene */
    
    /* When change current gene in ms.objXXX. Then put things here to
     * do when change the gene which are side effects.
     * This includes modifying the 'editable gene list'
     */
    mae.pur.chkOtherCurGeneEffects(mid,mouseKeyMods);
    
    mae.repaint();
    repaint();              /* redraw this plot */
  } /* showGeneOfInterest*/
  
} /* end of ExprProfileScrollPane class */

