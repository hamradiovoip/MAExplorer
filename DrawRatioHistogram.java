/** File: DrawRatioHistogram.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * This class draws a ratio histogram on an extended Canvas.
 * It is updated by the PopupRegistry when the normalization,
 * Filter.workingCL, or other state values change.
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.10 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ShowPlotPopup
 */

class DrawRatioHistogram extends Canvas implements MouseListener
{
  /** link to global instance */
  private MAExplorer 
    mae;                         
  /** link to global instance */
  private CompositeDatabase
    cdb;                       
  /** link to global instance */
  private ShowPlotPopup
    spp;                         
  
  /** plot mode */
  private int
    plotMode;	
    		 
  /** canvas used to draw ratio histogram */
  private Canvas 
    pCanvas;
 
  /* default canvas width */ 	
  final static int
    MIN_CANVAS_WIDTH=  450;
  /* default canvas height */  	
  final static int            
    MIN_CANVAS_HEIGHT= 350;
  /** target Size  for selecting a line */
  final static int
    TARGET_SIZE= 2;	
  
  /* Preferred canvas height */  	 
  public int
    preferredHeight= MIN_CANVAS_HEIGHT; 
  /* Preferred canvas width */  	
  public int
    preferredWidth= MIN_CANVAS_WIDTH;
    
  /** X2 [1:nHist] for clicking on point in list */
  private int
    x1[];  
  /** X2 [1:nHist] for clicking on point in list */
  private int
    x2[];			  
  /** corresponding hist bin index */
  private int
    binIndex[];                  

  	 
  /* working canvas width */  
  private int
    iWidth= MIN_CANVAS_WIDTH; 	 
  /* working canvas height */  
  private int
    iHeight= MIN_CANVAS_HEIGHT;
    
  /** full Gif file path name */
  private String
    oGifFileName;
 /** Info on genes in the current bin */
  private String               
    ncStr= "";                  
  /** vertical caption */
  private String
    vertCaption;                  
  /** horizontal caption */
  private String
    horizCaption;                  
  /** horizontal variable caption */
  private String
    horizVariableCaption;
  /** title */
  private String
    title;
  /** X title */
  private String
    titleX;			
  /** Y title */
  private String
    titleY;	
    
  /** min value of range of values found */
  private double
    minXfound;
  /** min value of range of values found */
  private double
    maxXfound;
  /** ARG: minimum possible value range set by scrollers */
  private double
    minX;
  /** ARG: maximum possible value range set by scrollers */
  private double
    maxX;			/** maximum possible value range*/
    
  /** point type: DRAW _PLUS, _CIRCLE, _BOX*/
  private int
    pointType;
  /** freq bin hist[0:nHist-1].*/
  private int
    histRatio[];	
  /** value bin hist[0:nHist-1].*/
  private double
    histValue[];
  /** index of X axis for data arrays*/
  private int
    curXbinIdx= -1;
  /** active bin # [0:nHist] else -1 */
  private int
    activeBin= -1;             
  /** hist[0:npoints-1]*/
  private int
    nHist;			
  /** lower range of histogram values */
  private float
    ratioLower;                  
  /** upper range of histogram values */
  private float
    ratioUpper;
  /** set if draw plot to GIF file*/
  private boolean
    drawIntoImageFlag= false;
  /** for testing range */
  private boolean
    outsideRangeFlag= false;
  /** Linear(false)/Log(true) */	
  private boolean
    logScalingFlag= false;      
    
  /** font used for lableing */			   				   
  private Font
   smallFont;
  

  /**
   * DrawRatioHistogram() - constructor to draw ratio histogram
   * @param mae is the MAExplorer instance
   * @param spp is the ShowPlotPopup instance
   * @param plotMode is the specific plot mode to implement 
   * @see #updateRatioHistogramPlot
   */
  DrawRatioHistogram(MAExplorer mae, ShowPlotPopup spp, int plotMode)
    { /* DrawRatioHistogram */
      super();           /* Create ScrollPane to display the canvas */
      this.mae= mae;
      this.spp= spp;
      this.plotMode= plotMode;
      
      smallFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
      
      cdb= mae.cdb;
      
      activeBin= -1;              /* active bin # [0:nHist] else -1 */
    
      updateRatioHistogramPlot(); /* set the state and cause a repaint to draw it*/
    } /* DrawRatioHistogram */


  /**
   * drawGifFile() - draw plot into Gif image file if in stand-alone mode.
   * This sets it up and lets paint() to the heavy lifting...
   * @param oGifFileName is the full path GIF output file
   * @return true if successful
   * @return false if unable to generate the image file.
   * @see #repaint
   */
  boolean drawGifFile(String oGifFileName /* GIF output file */)
    { /* drawGifFile */
      if(mae.isAppletFlag || oGifFileName==null)
	return(false);
	
      drawIntoImageFlag= true; 
      this.oGifFileName= oGifFileName;
      repaint();          /* will start the process */
      
      return(true);
    } /* drawGifFile */
   
    
  /**
   * getPreferredSize() - get the preferred size.
   * @return window size
   */
  public Dimension getPreferredSize()
    { /*getPreferredSize*/
      return(new Dimension(preferredWidth, preferredHeight));
    } /* getPreferredSize */
  
  
  /**
   * getMinimumSize() - get the minimum preferred size
   * @return window size
   */
  public Dimension getMinimumSize()
    { /* getMinimumSize */
      return(new Dimension(MIN_CANVAS_WIDTH, MIN_CANVAS_HEIGHT));
    } /* getMinimumSize */
  
  
  /**
   * updateRatioHistogramPlot() - set ratioHist state & do repaint to draw it.
   * Does {PLOT_HIST_F1F2_RATIO, PLOT_HIST_HP_XY_RATIO, 
   *       PLOT_HIST_HP_XY_SETS_RATIO }.
   * [CHECK] compute hist of SETS of X and Y microarray samples.
   * @see CompositeDatabase#calcHistOfF1F2Ratios
   * @see CompositeDatabase#calcHistOfHP_XYratios
   * @see #setHistogramRatiosData
   */
  void updateRatioHistogramPlot()
    { /* updateRatioHistogramPlot */
      int
        maxSpots= mae.mp.maxSpots,
        pointType= mae.DRAW_PLUS;
      String
        changeStr= null;
	   
      if(plotMode==mae.PLOT_HIST_F1F2_RATIO)
      { /* ratio histogram of F1/F2 */
        String
          sf1= mae.cfg.fluoresLbl1,
          sf2= mae.cfg.fluoresLbl2;
        if(mae.useCy5OverCy3Flag)
        { /* Flip Cy3/Cy5 to Cy5/Cy3 */
          String sTmp= sf1;
          sf1= sf2;
          sf2= sTmp;
        }
        changeStr= (mae.isZscoreFlag)
                      ? "Zscore ("+sf1+"-"+sf2+")"
                      : "Ratio ("+sf1+"/"+sf2+")";
        String
          mainTitle1= "Histogram of " + changeStr +" spot data",
          mainTitle2= "HP: "+ mae.ms.fullStageText + mae.hps.sModX,
          mainTitle= mainTitle1+", "+mainTitle2;
        if(!mae.useRatioHistCLflag)
          cdb.calcHistOfF1F2Ratios(mae.fc.ratioThrCL, mae.ms);
        setHistogramRatiosData("Freq", changeStr,
                               mainTitle,  mainTitle1, mainTitle2,
                               mae.MIN_RATIO, mae.MAX_RATIO, pointType,
                               cdb.ratioHist,  
                               mae.histRatioBin,
                                /* i.e.  CompositeDatabase.ratioValue */
                               mae.MAX_RATIO_HIST);
      } /* ratio histogram of F1/F2 */
      
      else if(plotMode==mae.PLOT_HIST_HP_XY_RATIO ||
              plotMode==mae.PLOT_HIST_HP_XY_SETS_RATIO)
      { /* ratio histogram of HP-X/HP-Y */
        String
          horizCaption= null,
          mainTitle1= "HP-X: " + cdb.hpXYdata.hpNameX+mae.hps.sModX,
          mainTitle2= "HP-Y: " + cdb.hpXYdata.hpNameY+mae.hps.sModY,
          mainTitle= mainTitle1+", "+mainTitle2;
        
        if(plotMode==mae.PLOT_HIST_HP_XY_RATIO)
        {
          changeStr= (mae.isZscoreFlag)
                        ? "Zscore (HP-X - HP-Y)"
                        : "Ratio (HP-X/HP-Y)";
          if(!mae.useRatioHistCLflag)
            cdb.calcHistOfHP_XYratios(mae.fc.ratioThrCL,
                                      false /* no HP sets */);
        }
        else if(plotMode==mae.PLOT_HIST_HP_XY_SETS_RATIO)
        {
          changeStr= ((mae.isZscoreFlag)
                         ? "Zscore (HP-X - HP-Y)"
                         : "Ratio (HP-X/HP-Y)") + " 'sets'";
          if(!mae.useRatioHistCLflag)
            cdb.calcHistOfHP_XYratios(mae.fc.ratioThrCL,
                                      true  /* use HP sets */);
        }
        
        horizCaption= changeStr + " mean spot data";
        setHistogramRatiosData("Freq",  horizCaption,
                               mainTitle, mainTitle1, mainTitle2,
                               mae.MIN_RATIO, mae.MAX_RATIO, pointType,
                               cdb.ratioHist,
                               mae.histRatioBin, /* i.e. CompositeDatabase.ratioValue */
                               mae.MAX_RATIO_HIST);
      } /* ratio histogram of HP-X/HP-Y  */
  } /* updateRatioHistogramPlot */
 

  /**
   * paint() - draw the histogram
   * @param g is graphics context
   * @see #drawHistogramRatios
   */
  public void paint(Graphics g)
    { /* paint */
      drawHistogramRatios(g);
    } /* paint */

  
  /**
   * drawPlus() - draw plus sign in the specified color.
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param color is color to draw 
   */
  final static void drawPlus(Graphics g, int x, int y, Color color)
  { /* drawPlus */
    int w= 2;
    g.setColor( color );
    g.drawLine( x, y-w, x, y+w );
    g.drawLine( x-w, y, x+w, y );
  } /* drawPlus */
  
  
  /**
   * drawRectangleBin() - draw histogram bin
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param width of object
   * @param height of object
   * @param color is color to draw 
   * @param fillit to draw a solid object
   */
  final void drawRectangleBin(Graphics g, int x, int y, int width, int height, 
                              Color color, int binIdx, boolean fillIt)
    { /* drawRectangleBin */
      g.setColor( color );
      if(!fillIt)
      {
        g.drawLine( x, y, x+width, y );
        g.drawLine( x, y-height, x+width, y-height );
        g.drawLine( x, y, x, y-height );
        g.drawLine( x+width, y, x+width, y-height );
      }
      else
        g.fillRect( x, y-height, width, height );
      
      x1[binIdx]= x;
      x2[binIdx]= (x+width-1);
  } /* drawRectangleBin */
     
  
  /**
   * setHistogramRatiosData() - draw histogram of rg.ratioHist[].
   * Rescale it to 300x400 canvas.
   * @param vertCaption is the vertical caption
   * @param horizCaption
   * @param title is the window title 
   * @param titleX is the X axis title
   * @param titleY is the Y axis title
   * @param minX is lowest X axis value
   * @param maxX is highest X axis value
   * @param pointType is either DRAW _PLUS, _CIRCLE, _BOX
   * @param histRatio is the ratios of the ratio histogram
   * @param histValue is the bin values frequencies of the ratio histogram
   * @param nHist  is the number of points in hist[0:npoints-1]
   * @see Util#showMsg
   * @see #drawRectangleBin
   * @see #repaint
   */
  void setHistogramRatiosData(String vertCaption, String horizCaption,
                              String title, String titleX, String titleY,
                              double minX, double maxX, int pointType,
                              int histRatio[], double histValue[], int nHist)
  { /* setHistogramRatiosData */
    this.vertCaption= vertCaption;
    this.horizCaption= horizCaption;
    this.title= title;
    this.titleX= titleX;
    this.titleY= titleY;
    this.minX= minX;
    this.maxX= maxX;
    this.pointType= pointType;
    this.histRatio= histRatio;
    this.histValue= histValue;
    
    minXfound= 10000000;
    maxXfound= -1;
    double  value;
    
    for(int i= 1; i<nHist; i++)
      if(histRatio[i]>0)
      { /* find actual range */
        value= histValue[i];
        if(minXfound>value)
          minXfound= value;      /* Math.min(minXfound,value) */
        if(maxXfound<value)
          maxXfound= value;      /* Math.max(maxXfound,value) */
      }
    
    if(minX<0 || maxX<=0)
    {
      Util.showMsg("No ratio histogram range - can't plot");
      Util.popupAlertMsg("No ratio histogram range",
                         "No ratio histogram range - can't plot",
                         4, 80);
      return;
    }
    
    if(x1==null || this.nHist!=nHist)
    { /* don't regenerate if doing a real update */
      x1= new int[nHist+1];	/* for clicking on point in list */
      x2= new int[nHist+1];
      binIndex= new int[nHist+1];
    }
    this.nHist= nHist;
    
    repaint();
  } /* setHistogramRatiosData */
  
  
  /**
   * drawHistogramRatios() - draw histogram of rg.ratioHist[].
   * Rescale it to 300x400 canvas.
   * @param g is graphics context
   * @see Util#showMsg
   * @see WriteGifEncoder
   * @see WriteGifEncoder#writeFile
   * @see #drawRectangleBin
   */
  void drawHistogramRatios(Graphics g)
  { /* drawHistogramRatios */
    int
      nReplicates= 2,         /* since count genes not spots */
      hSize= 450,
      vSize= 325;
    int
      xRange= (int)(maxX-minX),
      xOffset= 50,
      yOffset= (int)vSize;
    int
      hMax= 0,		/* max histogram value for scaling */
      iMinX= (int)minX,
      iMaxX= (int)maxX,
      binWidth= (int)((hSize-xOffset)/nHist);
    
    for(int i= 0;i<nHist; i++)
      hMax= Math.max(hMax,histRatio[i]);
    
    if(hMax==0)
    {
      Util.showMsg("No Ratio Histogram data");
      return;
    }
    
    double
      minY= 0.0,
      maxY= hMax/nReplicates,         /* count genes not spots */
      scaleX= hSize/maxX,
      scaleY= 0.90*(vSize/hMax),
      yRange= (int)(hSize/mae.MAX_RATIO_HIST);  /* [CHECK] rounding error */
    int yTop= yOffset-vSize+40;
    
    /* If draw plot into GIF image file, setup new Graphics g. */
    Image gifImage= null;
    if(drawIntoImageFlag)
    { /* draw into GIF file Image instead of canvas */
      int
        w= preferredWidth,
        h= preferredHeight;
      gifImage= createImage(w,h);
      g= gifImage.getGraphics();
    }
    
    /* Draw the axes and labels */
    this.setBackground( Color.white );             /* clear screen */
    g.clearRect(0,0,iWidth,iHeight);
    g.setColor( Color.black );
    
    if(yTop<0)
      yTop= 0;                                        /* max(0,yTop) */
    g.drawLine( xOffset,yTop, xOffset,yOffset+10);    /* Yaxis */
    
    int xRight= xOffset+(int)(scaleX*xRange);
    xRight= Math.min((iWidth-1), xRight);
    g.drawLine( xOffset-10,yOffset, xRight,yOffset);  /* Xaxis */
    
    g.drawString(vertCaption, 5, yOffset/2);          /* vert title */
    g.drawString(horizCaption, xOffset+(4*xRange)/5,
    yOffset+50);                         /* horiz title */
    //g.drawString(title, xOffset+20, yOffset+70);    /* title */
    g.drawString(titleX, xOffset+20, yOffset+70);     /* title */
    g.drawString(titleY, xOffset+20, yOffset+90);    /* title */
    
    /* Draw the scale values */
    g.drawString(Util.cvd2s(minY,0), 20, yOffset+5);
    g.drawString(Util.cvd2s(maxY,0), 20, yTop-5);
    /* g.drawString(Util.cvd2s(mae.MIN_RATIO,2), xOffset+5, yOffset+15);  */
    g.drawString(Util.cvd2s(mae.MAX_RATIO,2), xRight-5, yOffset+15);
    
    String rangeText= "Ratio range[" +
    Util.cvd2s(minXfound,3) + ":" +
    Util.cvd2s(maxXfound,3) + "]";
    int
      xRtext= xOffset+20,     /* (int)(maxX-40) */
      yRtext= yTop+20;        /* yOffset+70 */
    g.drawString(rangeText, xRtext, yRtext);
    g.setColor( (mae.useDichromasyFlag) ? Color.orange : Color.red );
    g.drawString(ncStr, xRtext, yRtext+20);
    g.setColor( Color.black );
    Font stdFont= g.getFont();
    
    /* Draw the histogram bins */
    int
      x, y,
      width,
      height,
      xDoc;
    Color binColor;
    boolean
      inRangeFlag,
      fillIt;
    double
      histValI;
    
    for(int i= 1; i<nHist; i++)
    { /* plot each bin entry and draw horizontal scale values */
      x= i*binWidth+xOffset;
      y= yOffset;
      width= binWidth;
      histValI= histValue[i];
      height= (int)(0.95*vSize*histRatio[i])/hMax;
      
      inRangeFlag= (histValI>=ratioLower &&
      histValI<ratioUpper);
      if(outsideRangeFlag)
        inRangeFlag= !inRangeFlag;
      fillIt= (activeBin==i);
      
      binColor= (inRangeFlag || fillIt)
                  ? ((mae.useDichromasyFlag)
                        ? Color.orange : Color.red)
                  : Color.black;
      /* active bin # [0:nHist] else -1 */
      
      drawRectangleBin(g,x,y,width,height, binColor, i, fillIt);
      
      binIndex[i]= i;
      if((i & 03)==1)
      {
        xDoc= xOffset+(i-1)*binWidth+5;
        g.setFont(smallFont);
        g.drawString(Util.cvd2s(histValI,2), xDoc, yOffset+25);
        g.setFont(stdFont);
        g.drawLine( xDoc,yOffset, xDoc,yOffset+8);
      }
    } /* plot each bin entry and draw horizontal scale values */
    
    /* If drawing to a GIF file, then cvt Image to Gif stream
     * and write it out.
     */
    if(drawIntoImageFlag && gifImage!=null)
    { /* write it out */
      drawIntoImageFlag= false;
      WriteGifEncoder wge= new WriteGifEncoder(gifImage);
      gifImage= null;
      if(wge!=null)
        wge.writeFile(oGifFileName);
      
      repaint();   /* refresh the actual canvas */
    } /* write it out */
  } /* drawHistogramRatios */
    
        
  /**
   * filterByBin() - filter genes by current activeBin histogram bin.
   * Use the spp.histBinRange range to set the desired thresholds.
   * Both nHist>0 and activeBin>=0 to do filtering.
   * If get a hit, then display data for the gene.
   * @see CompositeDatabase#setRatioHistCLfromF1F2Ratios
   * @see CompositeDatabase#setRatioHistCLfromHP_XYratios
   * @see CompositeDatabase#setRatioHistCLfromHP_XYsetRatios
   * @see Filter#computeWorkingGeneList
   * @see MAExplorer#repaint
   * @see Util#cvf2s
   * @see Util#saveCmdHistory
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see #repaint
   */
  void filterByBin()
  { /* filterByBin */
    mae.useRatioHistCLflag= false;
    activeBin= -1;
    if(nHist==0)
      return;               /* no histogram... */
    
    ncStr= "";              /* default is none */
    
    if(curXbinIdx!=-1)
    { /* filter by active bin */
      activeBin= binIndex[curXbinIdx];
      int freq= 0;
      float
        rBinLower= (float)histValue[curXbinIdx],
        rBinUpper= 100.0F;  /* handles boundary condition */
      int j;
      String multRngStr= null;
      
      if(curXbinIdx<nHist)
        rBinUpper= (float)histValue[curXbinIdx+1];
      ratioLower= rBinLower;
      ratioUpper= rBinUpper;
      
     /* Use spp.binHistRange which contain values:
      * "= ", "< ", "> ", "<>", "><"
     */
      outsideRangeFlag= false;
      if(spp.histBinMode==spp.TH_EQ)
      { /* In BIN */
        ratioLower= rBinLower;
        ratioUpper= rBinUpper;
        freq= histRatio[activeBin];
      }
      else if(spp.histBinMode==spp.TH_LT)
      { /* LESS than */
        ratioLower= (float)histValue[0];
        ratioUpper= rBinUpper;
        for(j=0;j<=activeBin;j++)
          freq += histRatio[j];   /* sum of freq's <= thr */
      }
      else if(spp.histBinMode==spp.TH_GT)
      { /* GREATER than */
        if(curXbinIdx<nHist)
          ratioLower= (float)histValue[curXbinIdx];
        else ratioLower= rBinUpper;
        ratioUpper= 1000.0F;
        for(j=activeBin;j<=nHist;j++)
          freq += histRatio[j];   /* sum of freq's >= thr */
      }
      else if(spp.histBinMode==spp.TH_IN ||
              spp.histBinMode==spp.TH_OUT)
      { /* INSIDE or OUTSIDE RANGE */
        ratioLower= 1.0F/rBinUpper;
        ratioUpper= rBinUpper;
        if(ratioUpper<ratioLower)
        {
          ratioUpper= 1.0F/rBinUpper;
          ratioLower= rBinUpper;
        }
        outsideRangeFlag= spp.outsideRangeFlag;
        if(outsideRangeFlag)
          multRngStr= "0.0 :" + Util.cvf2s(ratioLower,4) +
                      ", " + Util.cvf2s(ratioUpper,4)+" : 1000";
        float hvJ;
        boolean dataInRangeFlag;
        for(j=0;j<nHist;j++)
        { /* sum the freq in the range */
          hvJ= (float)histValue[j];
          dataInRangeFlag= (hvJ>=ratioLower && hvJ<ratioUpper);
          if((dataInRangeFlag && !outsideRangeFlag) ||
             (!dataInRangeFlag && outsideRangeFlag))
            freq += histRatio[j];   /* sum of freq's in thr
             * either IN or OUT range */
        }
      } /* INSIDE or OUTSIDE RANGE */
      
      freq /= 2;  /* since counting genes not spots */
      if(multRngStr==null)
        multRngStr= Util.cvf2s(ratioLower,4) + " : " +
                               Util.cvf2s(ratioUpper,4);
      ncStr= "(" + spp.histBinRange + ") Ratio [" +
             multRngStr + "] has "+ freq + " genes";
      
      /* compute genes in this bin range*/
      if(spp.reFilterHistFlag)
      { /* ReCompute genes in this bin range*/
        Util.saveCmdHistory("Filtering by ratio histogram bin "+ncStr, false);
        mae.useRatioHistCLflag= false;  /* compute hist on all genes*/
        mae.fc.computeWorkingGeneList(); /* use full list*/
        
        if(plotMode==mae.PLOT_HIST_F1F2_RATIO)
          cdb.setRatioHistCLfromF1F2Ratios(mae.fc.ratioHistCL,
                                           mae.ms, ratioLower, ratioUpper,
                                           outsideRangeFlag);
        else if(plotMode==mae.PLOT_HIST_HP_XY_RATIO)
          cdb.setRatioHistCLfromHP_XYratios(mae.fc.ratioHistCL,
                                            mae.msX,mae.msY,
                                            ratioLower, ratioUpper,
                                            outsideRangeFlag);
        else if(plotMode==mae.PLOT_HIST_HP_XY_SETS_RATIO)
          cdb.setRatioHistCLfromHP_XYsetRatios(mae.fc.ratioHistCL,
                                               ratioLower, ratioUpper,
                                               outsideRangeFlag);
        mae.useRatioHistCLflag= true;
        mae.fc.computeWorkingGeneList();   /* now is partial list*/
        mae.updatePseudoImgFlag= true;
        mae.repaint();
      } /* ReCompute genes in this bin range*/
    } /* filter by active bin */
    
    /* Found it!! - Go process */
    mae.mbf.miFRMhistRatioRngFilter.setState(mae.useRatioHistCLflag);
    if(mae.useRatioHistCLflag==false)
    { /* restore filter to not use ratio hist gene list */
      mae.fc.computeWorkingGeneList();
      mae.repaint();
    }
    else
      Util.showMsg2("Click on 'Freq' to reset histogram & Filter");
    Util.showMsg3(ncStr);
    repaint();
    
    return;
  } /* filterByBin */
     
       
  /**
   * mouseHandler() - interact with current plot if a graphic plot.
   * If get a hit, then display data for the gene.
   * @param x mouse position
   * @param y mouse position
   * @see #filterByBin
   */
  public void mouseHandler(int x, int y)
  { /* mouseHandler */
    if(nHist==0)
      return;
    
    curXbinIdx= -1;
    for(int i=0;i<nHist-1;i++)
    { /* find closest point */
      if(x>=x1[i] && x<=x2[i])
      {
        curXbinIdx= i;
        break;
      }
    }
    
    filterByBin();                 /* go process it */
  } /* mouseHandler */

    
  /**
   * mousePressed() - process mouse event
   * @param e is mouse pressed event
   * @see #mouseHandler
   */
  public void mousePressed(MouseEvent e)
    { 
      mouseHandler(e.getX(), e.getY());
    }  
 
     
  /* others not used at this time */       
  public void mouseClicked(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }    
  public void mouseExited(MouseEvent e) { }    
  public void mouseReleased(MouseEvent e) { }      
  public void mouseMoved(MouseEvent e) { }      
  public void mouseDragged(MouseEvent e) { }      

  
  /**
   * updateScaling() - set scaling mode in proper sub-object.
   * Linear(false)/Log(true) [DEPRICATED]
   * @param logScalingFlag if using log scaling
   * @see #filterByBin
   */
  void updateScaling(boolean logScalingFlag)
  { /* updateScaling */
    this.logScalingFlag= logScalingFlag;
    filterByBin();    /* filter by current activeBin histogram bin.
                       * use the spp.histBinRange range to set
                       * desired thresholds. */
  } /* updateScaling */
   
   
} /* end of class DrawRatioHistogram */



