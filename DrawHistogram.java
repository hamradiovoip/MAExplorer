/** File: DrawHistogram.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * Class to draw an intensity histogram in popup frame.
 * It is updated by the PopupRegistry when the normalization,
 * Filter.workingCL, or other state values change.
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.8 $ 
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ShowPlotPopup
*/

class DrawHistogram extends Canvas implements MouseListener
{
  /** link to global MAExplorer instance */
  private MAExplorer 
    mae;                        
  /** link to global MaHybridSample instance */
  private MaHybridSample
    ms;                         
  
  /** ARG: parent window */
  private ShowPlotPopup
    spp;                        
 
  /** ARG: plot mode */
  private int
    plotMode;			
  /** ARG: opt. title else generate one */
  private String
    mTitle;                     
    
  /** Break histogram into bins */
  final static int
    MAXBINS= 64;
  /** default canvas width */
  final static int                
    MIN_CANVAS_WIDTH=  450;
  /** default canvas height */
  final static int          
    MIN_CANVAS_HEIGHT= 360;
  /** target Size  for selecting a line */
  final static int
    TARGET_SIZE= 2;		
    
  /** preferred canvas height */
  public int
    preferredHeight= MIN_CANVAS_HEIGHT;
  /** preferred canvas width */
  public int
    preferredWidth= MIN_CANVAS_WIDTH;
    
  /** [0:MAXBINS] bins frequency for display */
  private int
    binFreq[];
  /** [0:MAXBINS] bins actual value @start of bin for display */                  
  private float
    binDataValue[];             
  /** x1 [0:MAXBINS-1] for clicking on point in list */
  private int
    x1[];           
  /** x2 [0:MAXBINS-1] for clicking on point in list */
  private int
    x2[];		        
    
  /** precision in intensity data report */
  private int
    cvPrecision;                
  /** working canvas width */
  private int
    iWidth= MIN_CANVAS_WIDTH;
  /** working canvas height */
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
  
  /** idx lower intensity range binDataValue[] space*/
  private int
    intensL;                    
  /** idx upper intensity range binDataValue[] space*/
  private int
    intensU;
  /** minimum range of values found in binFreq[] */
  private int
    minFreq;                    
  /** maximum range of values found in binFreq[] */
  private int
    maxFreq;
  /** mean binFreq[] */
  private int
    meanFreq;                   
  /** median binFreq[] */
  private int
    medianFreq;                 
  /** mean bin intensity value for medianFreq */
  private int
    meanBin;                    
  /** median bin intensity value for medianFreq */
  private int
    medianBin;                  
  /** ARG: minimum possible value range set by scrollers */
  private int
    minBinX;
  /** ARG: maximum possible value range set by scrollers */
  private int
    maxBinX;	        
  
  /** value of hist[] bin in [minX:maxX] */
  private float
    deltaBin;                   
  /** mean binFreq of weighted binFreq[] */
  private float
    meanValue;
  /** median binFreq of binDataValue[] */
  private float
    medianValue;
  /** set from ms hist[] statistics */
  private float
    modeH;                      
  /** set from ms hist[] statistics */
  private float
    medianH;                     			 
  /** ARG: minimum possible value range set by scrollers */
  private float
    minX;
  /** ARG: maximum possible value range set by scrollers */
  private float
    maxX;			
  
  /** point type: DRAW _PLUS, _CIRCLE, _BOX*/
  private int
    pointType;			
  /** freq hist[0:nHist-1].*/
  private int
    hist[];		        
  /** active bin idx [0:MAXBINS-1] else -1 */
  int
    activeBin;                  	        
  /** hist[0:nHist-1] of raw data. nHist should be MAExplorer.MAX_INTENSITY */
  int
    nHist;			
    
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
   * DrawHistogram() - constructor to create an intensity histogram.
   * @param mae is the MAExplorer instance
   * @param spp is the ShowPlotPopup instance
   * @param mTitle is window title
   * @param horizVariableCaption is horizontal caption
   * @param plotMode is the specific plot mode to implement
   * @see MaHybridSample#calcIntensHist
   * @see #updateHistogramPlot
   */
  DrawHistogram(MAExplorer mae, ShowPlotPopup spp, String mTitle,
                String horizVariableCaption, int plotMode)
  { /* DrawHistogram */
    super();           /* Create ScrollPane to display the canvas */
    
    this.mae= mae;
    this.spp= spp;
    this.horizVariableCaption= (horizVariableCaption==null)
                                 ? "Intensity"
                                 : horizVariableCaption;
    this.plotMode= plotMode;
    this.mTitle= mTitle;
    
    smallFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
    
    ms= mae.ms;
    
    activeBin= -1;              /* active bin # [0:MAXBINS-1] else -1 */
    cvPrecision= 3;             /* precision in intensity data report */
    
    minBinX= 0;
    maxBinX= mae.MAX_INTENSITY-1;
    
    binFreq= new int[MAXBINS+1];
    binDataValue= new float[MAXBINS+1];
    
    ms.calcIntensHist();        /* recompute the histogram */
    
    updateHistogramPlot();   /* set state & cause a repaint to draw it*/
  } /* DrawHistogram */
  
  
  /**
   * drawGifFile() - draw plot into Gif image file if in stand-alone mode.
   * This sets it up and lets paint() to the heavy lifting...
   * @param oGifFileName is the full path GIF output file
   * @return true if successful
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
   * getPreferredSize() - get the preferred size
   * @return window size
   */
  public Dimension getPreferredSize()
  { /* getPreferredSize*/
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
   * updateHistogramPlot() - set hist state and do repaint to draw it.
   * @see MaHybridSample#calcIntensHist
   * @see #setHistogramData
   */
  void updateHistogramPlot()
  { /* updateHistogramPlot */
    int pointType= mae.DRAW_PLUS;    
    String  mainTitle= "Spot data Intensity Histogram [" +
                       ms.fullStageText +"]";
    title= (mTitle==null) ? mainTitle : mTitle;
    
    if(spp.reFilterHistFlag)
      ms.calcIntensHist();        /* recompute the histogram */
    
    setHistogramData("Freq", horizVariableCaption, title,
    ms.minIntens, ms.maxIntens, ms.deltaBin,
    ms.modeIntens, ms.medianIntens,
    pointType, ms.hist, ms.nBins);
  } /* updateHistogramPlot */
  
  
  /**
   * updateScrolledRegion() - update scrolled region to plot
   * @param x1Pos is x1 position in the range 0.0:1.0
   * @param x2Pos is x2 position in the range 0.0:1.0
   * @see #setHistogramData
   */
  void updateScrolledRegion(float x1Pos, float x2Pos)
  { /* updateScrolledRegion */
    /* Interpolate from [0:1] to [0:max(X/Y)] */
    minX= x1Pos*(ms.maxIntens-ms.minIntens)+ms.minIntens;
    maxX= x2Pos*(ms.maxIntens-ms.minIntens)+ms.minIntens;
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("DH-USR (minX,maxX)=("+
                       Util.cvf2s(minX,2)+
                       ","+Util.cvf2s(maxX,2)+
                       ") (x1Pos,x2Pos)=("+
                       Util.cvf2s(x1Pos,2)+
                       ","+Util.cvf2s(x2Pos,2)+")");
     */
    setHistogramData("Freq", horizVariableCaption, title,
                     minX, maxX, ms.deltaBin,
                     ms.modeIntens, ms.medianIntens,
                     pointType, ms.hist, ms.nBins);
  } /* updateScrolledRegion */
  
  
  /**
   * setHistogramData() - draw histogram of hist[0:nHist-1].
   * Rescale it to 300x400 canvas.
   * @param vertCaption is vertical caption
   * @param horizCaption is horizontal caption
   * @param title for window
   * @param minX is min range to display
   * @param maxX is max range to display
   * @param deltaBin is width of a histogram bin
   * @param modeH the mode computed from histogram
   * @param medianH the median computed from histogram
   * @param pointType (type of symbol)
   * @param hist is the intensity freqency hist[0:MAXINTENS]
   * @param nHist is the number of bins in hist[0:npoints-1]
   * @see #repaint
   * @see #updateSampleBins
   */
  private void setHistogramData(String vertCaption, String horizCaption,
                                String title, float minX, float maxX,
                                float deltaBin, float modeH, float medianH,
                                int pointType, int hist[], int nHist)
  { /* setHistogramData */
    this.vertCaption= vertCaption;
    this.horizCaption= horizCaption;
    this.title= title;
    this.minX= minX;         /* real value of scaled data */
    this.maxX= maxX;
    this.deltaBin= deltaBin;
    this.modeH= modeH;
    this.medianH= medianH;
    this.pointType= pointType;
    this.hist= hist;
    
    minBinX= (int)(((nHist-1)*minX)/(ms.maxIntens-ms.minIntens));
    maxBinX= (int)(((nHist-1)*maxX)/(ms.maxIntens-ms.minIntens));
    
    if(x1==null || this.nHist!=nHist)
    { /* don't regenerate if doing a real update */
      x1= new int[MAXBINS+1];	/* for clicking on point in list */
      x2= new int[MAXBINS+1];
    }
    this.nHist= nHist;
    
      /* Given the value range [minX:maxX], map the scaled
       * hist[0:nHist-1] to binXXX[0:MAXBINS-1].
       */
    updateSampleBins();
    
    repaint();
  } /* setHistogramData */
  
  
  /**
   * updateSampleBins() - map hist[0:nHist-1] to binXXX[0:MAXBINS-1].
   * Where binXXX[] is binFreq[], binDataValue[].
   * Given the data value range [minX:maxX], map hist[0:nHist-1] to
   * binXXX[0:MAXBINS-1].
   * Also compute minFreq, maxFreq, meanValue, medianValue
   */
  private void updateSampleBins()
  { /* updateSampleBins */
    float
    dX= ((maxX-minX)/MAXBINS),       /* mapped to scaled space */
    dBin= ((float)nHist)/MAXBINS;    /* mapped to hist bin space */
  /*
  if(mae.CONSOLE_FLAG)
    mae.fio.logMsgln("DH-USB dX="+ Util.cvf2s(dX,2)+
         " dBin="+Util.cvf2s(dBin,2)+
         " (min,max)BinX=("+
         minBinX+","+maxBinX+
         ")\n deltaBin="+Util.cvf2s(deltaBin,4)+
         " (minX,maxX)=("+ Util.cvf2s(minX,2)+
         ","+Util.cvf2s(maxX,2)+")");
   */
    for(int i= 0; i<MAXBINS;i++)
    { /* compute the bin subsets */
      int
      bfTot= 0,                     /* zero counter for each bin */
      iOrig= (int)(i*dBin),         /* bin start value in hist[] space */
      iEnd= (int)(iOrig+dBin);      /* bin end value in hist[] space */
      float
      bdVal= (i*dX+minX);           /* intens value in hist[] space */
      
      if(iEnd>=nHist)
        iEnd= nHist-1;                /* Clip it */
      binDataValue[i]= bdVal;         /* intensity value in center
       * of hist[iOrig:iEnd]*/
      
      /* Set binFreq[j] to sum of bin freq in hist[] range j */
      /* Zero out rest of histogram */
      for(int j=iOrig;j<=iEnd;j++)
        bfTot += hist[j];            /* sum of bin frequencies */
      binFreq[i]= bfTot;
      /*
      if(mae.CONSOLE_FLAG && i<6)
        mae.fio.logMsgln("DH-USB.1 i="+i+
             " iOrig="+iOrig+" iEnd="+iEnd+
             " bdVal="+Util.cvf2s(bdVal,3)+
             " bfTot="+bfTot);
       */
    } /* compute the bin subsets */
    
    int
     binFreqI,
     wtSumFreq= 0,
     sumFreq= 0;
    minFreq= 10000000;
    maxFreq= -10000000;
    
    for(int i= 0; i<MAXBINS; i++)
    { /* find actual range */
      binFreqI= binFreq[i];
      wtSumFreq += i*binFreqI;
      sumFreq += binFreqI;
      if(minFreq>binFreqI)
        minFreq= binFreqI;   /* Math.min(minFreq,binFreqI) */
      if(maxFreq<binFreqI)
        maxFreq= binFreqI;   /* Math.max(maxFreq,binFreqI) */
    }
    
    /* Compute meanValue and medianValue */
    meanFreq= sumFreq/MAXBINS;
    meanBin= (sumFreq==0) ? 0 : wtSumFreq/sumFreq;
    meanValue= binDataValue[meanBin];
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("DH-USB.2 minFreq="+minFreq+
                       " maxFreq="+maxFreq+
                       "\n  meanFreq="+meanFreq+
                       " meanValue="+Util.cvd2s(meanValue,3)+
                       " sumFreq="+sumFreq);
    */
    
    /* Compute median */
    int
      sum2= 0,              /* incremental total */
      halfSum= sumFreq/2;
    for(int i=0;i<MAXBINS;i++)
    {
      sum2 += binFreq[i];
      if(sum2>=halfSum)
      {
        medianFreq= binFreq[i];
        medianValue= binDataValue[i];
        medianBin= i;
        break;
      }
    }
  } /* updateSampleBins */
  
  
  /**
   * paint() - draw the histogram
   * @param g is graphics context
   * @see #drawHistogram
   */
  public void paint(Graphics g)
  { /* paint */
    drawHistogram(g);
  } /* paint */
  
  
  /**
   * drawPlus() - draw plus sign at the specified color.
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param color is color to draw
   */
  final private static void drawPlus(Graphics g, int x, int y, Color color)
  { /* drawPlus */
    int w= 2;
    g.setColor( color );
    g.drawLine( x, y-w, x, y+w );
    g.drawLine( x-w, y, x+w, y );
  } /* drawPlus */
  
  
  /**
   * drawRectangleBin() - draw histogram bin at the specified color
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param width of object
   * @param height of object
   * @param color is color to draw
   * @param fillit to draw a solid object
   */
  final private void drawRectangleBin(Graphics g, int x, int y,
                                      int width, int height, Color color,
                                      int bIdx, boolean fillIt)
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
    
    x1[bIdx]= x;
    x2[bIdx]= (x+width-1);
  } /* drawRectangleBin */
  
  
  /**
   * drawHistogram() - draw histogram of hist[].
   * Rescale it to 300x400 size canvas.
   * @param g is graphics context
   * @see Util#showMsg
   * @see WriteGifEncoder
   * @see WriteGifEncoder#writeFile
   * @see #drawHistogram
   * @see #drawRectangleBin
   * @see #repaint
   */
  void drawHistogram(Graphics g)
  { /* drawHistogram */
    int
      hSize= 450,
      vSize= 325;
    int
      //xRange= (int)(maxX-minX),
      xRange= (int)(maxBinX-minBinX),
      xOffset= 50,
      yOffset= (int)vSize,
      hMax= 0,		/* max histogram value for scaling */
      iMinX= (int)minX,
      iMaxX= (int)maxX,
      binWidth= (int)((hSize-xOffset)/MAXBINS);
    
    for(int i= 0;i<MAXBINS; i++)
      hMax= Math.max(hMax,binFreq[i]);
    
    if(hMax==0)
    {
      Util.showMsg("No histogram");
      return;
    }
    
    double
      minY= 0.0,
      maxY= hMax,
      scaleX= ((double)hSize)/maxBinX,
      scaleY= 0.90*(vSize/hMax),
      yRange= (int)(hSize/MAXBINS);  /* [CHECK] rounding error */
    int
      yTop= yOffset-vSize+40;
    /*
    if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("DH-DH xRange="+xRange+
                        " iMinX="+iMinX+" iMaxX="+iMaxX+
                        " scaleX="+Util.cvd2s(scaleX,3));
    */
    
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
    this.setBackground( Color.white );  /* clear screen */
    g.clearRect(0,0,iWidth,iHeight);
    g.setColor( Color.black );
    
    if(yTop<0)
      yTop= 0;                               /* Math.max(0, yTop) */
    g.drawLine( xOffset,yTop, xOffset,yOffset+10);   /* Yaxis */
    
    int
      xRight= xOffset+(int)(scaleX*xRange);
    xRight= Math.min((iWidth-1), xRight);
    g.drawLine( xOffset-10,yOffset, xRight,yOffset); /* Xaxis */
    
    /* Draw lower title and horiz and vertical captions */
    g.drawString(title, xOffset+20, yOffset+58); /* lower title */
    g.drawString(vertCaption, 5, yOffset/2);     /* vert caption */
    g.drawString(horizCaption, xOffset+180,
    yOffset+40);                    /* horiz caption */
    
    /* Draw the color keys at the bottom */
    g.drawString("Keys:", xOffset+20, yOffset+75);
    g.setColor( (mae.useDichromasyFlag) ? Color.cyan : Color.green );
    g.drawString("mean", xOffset+55, yOffset+75); /* color key */
    g.setColor( Color.blue );
    g.drawString("median", xOffset+93, yOffset+75); /* color key */
    if(ncStr.length()>0)
    {
      g.setColor( (mae.useDichromasyFlag) ? Color.orange : Color.red );
      g.drawString("selected", xOffset+140, yOffset+75); /* color key */
    }
    g.setColor( Color.black );
    
    /* Draw the Vertical axis scale values */
    g.drawString(Util.cvd2s(minY,cvPrecision), 20, yOffset+5);
    g.drawString(Util.cvd2s(maxY,cvPrecision), 20, yTop-5);
    
    String
      rangeText= "Frequency range[" +
                 Util.cvf2s(minX,cvPrecision) + " : " +
                 Util.cvf2s(maxX,cvPrecision) +
                 "] (mean,median) Freq=("+meanFreq+","+medianFreq+")",
      statItext= "(mean,median) "+horizVariableCaption+"=("+
                 Util.cvf2s(meanValue,cvPrecision)+
                 ", "+Util.cvf2s(medianValue,cvPrecision)+")";
    int
      xRtext= xOffset+40,        /* (int)(maxX-40) */
      yRtext= yTop-20;           /* yOffset+70 */
    
    /* Draw summary statistics text at the top */
    g.drawString(rangeText, xRtext, yRtext);
    g.drawString(statItext, xRtext, yRtext+17);
    g.setColor( (mae.useDichromasyFlag) ? Color.orange : Color.red );
    g.drawString(ncStr, xRtext, yRtext+34);
    g.setColor( Color.black);
    
    Font stdFont= g.getFont();
    
    /* Draw the histogram bins */
    for(int i= 0; i<MAXBINS; i++)
    { /* plot each bin entry and draw horizontal scale values */
      int
        x= i*binWidth+xOffset,
        y= yOffset,
        width= binWidth,
        height= (int)(0.95*vSize*binFreq[i])/hMax;      
      boolean
        fillIt= ((activeBin==i) || (medianBin==i) || (meanBin==i)),
        inRangeFlag= (i>=intensL && i<intensU);
      
      if(outsideRangeFlag)
        inRangeFlag= !inRangeFlag;
      
      Color binColor= (medianBin==i)
                       ? Color.blue
                       : ((meanBin==i)
                            ? ((mae.useDichromasyFlag)
                                 ? Color.cyan : Color.green)
                                 : ((inRangeFlag || activeBin==i)
                                       ? ((mae.useDichromasyFlag)
                                           ? Color.orange : Color.red)
                                       : Color.black));
      /* active bin # [0:MAXBINS-1] else -1 */
      
      drawRectangleBin(g,x,y,width,height, binColor, i, fillIt);
      
      if((i & 07)==1 || i==(MAXBINS-1))
      { /* draw numeric X axis values */
        int xDoc= xOffset+(i-1)*binWidth+5;
        g.setFont(smallFont);
        g.drawString(Util.cvf2s(binDataValue[i],cvPrecision),
        xDoc, yOffset+25);
        g.setFont(stdFont);
        g.drawLine( xDoc, yOffset, xDoc,yOffset+8);
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
  } /* drawHistogram */
  
  
  /**
   * filterByBin() - filter genes by current activeBin histogram bin(s).
   * Use the spp.histBinRange range to set the desired thresholds.
   * If get a hit, then display data for the gene. If do not get a hit,
   * then clear the filter.
   * Note: because there is only ONE plot so can't get into trouble
   * with multiple plots.
   * @see Filter#computeWorkingGeneList
   * @see CompositeDatabase#setIntensHistCLfromIntenRange
   * @see MAExplorer#repaint
   * @see Util#cvf2s
   * @see Util#saveCmdHistory
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see #repaint
   */
  void filterByBin()
  { /* filterByBin */
    /* [CHECK] test if working correctly - not sure, and DEBUG!!! */    
    mae.useIntensHistCLflag= false;
    if(nHist==0)
      return;    
    ncStr= "";              /* default is none */
    
    if(activeBin!=-1)
    { /* filter by active bin */
      int
        freq= binFreq[activeBin],
        j;
      String multRngStr= null;
      
   /* Use spp.binHistRange which contain values:
    * "= ", "< ", "> ", "<>", "><"
    */
      outsideRangeFlag= false;
      if(spp.histBinMode==spp.TH_EQ)
      { /* In BIN */
        intensL= activeBin;
        intensU= activeBin+1;
        freq= binFreq[activeBin];
      }
      else if(spp.histBinMode==spp.TH_LT)
      { /* LESS than */
        intensL= 0;
        intensU= activeBin;
        for(j=0;j<=activeBin;j++)
          freq += binFreq[j];
      }
      else if(spp.histBinMode==spp.TH_GT)
      { /* GREATER than "> " */
        intensL= activeBin;
        intensU= MAXBINS-1;
        for(j=activeBin;j<MAXBINS;j++)
          freq += binFreq[j];
      }
      else if(spp.histBinMode==spp.TH_IN ||
      spp.histBinMode==spp.TH_OUT)
      { /* INSIDE ("<>") or OUTSIDE RANGE ("><") */
        intensL= (MAXBINS-1 -activeBin);
        intensU= activeBin;
        if(intensU<intensL)
        {  /* SWAP */
          int oldU= intensU;
          intensU= intensL;
          intensL= oldU;
        }
        outsideRangeFlag= spp.outsideRangeFlag;
        if(outsideRangeFlag)
          multRngStr= "0 : " + Util.cvf2s(binDataValue[intensL],3)+
                       ", " + Util.cvf2s(binDataValue[intensU],3)+
                       " : "+Util.cvf2s(maxX,3);
        boolean dataInRangeFlag;
        
        for(j=0;j<MAXBINS;j++)
        { /* sum the freq in the range */
          dataInRangeFlag= (j>=intensL && j<intensU);
          if((dataInRangeFlag && !outsideRangeFlag) ||
             (!dataInRangeFlag && outsideRangeFlag))
            freq += binFreq[j];
        }
      } /* INSIDE or OUTSIDE RANGE */
      
      float
        minIntens= binDataValue[intensL],
        maxIntens= binDataValue[intensU];
      if(multRngStr==null)
        multRngStr= Util.cvf2s(minIntens,cvPrecision) +
                    " : " + Util.cvf2s(maxIntens,cvPrecision);
      ncStr= "(" + spp.histBinRange + ") Intensity [" +
             multRngStr + "] has "+ freq + " genes";
      
    /*
     if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("DH-FBB.1 intensL="+intensL+
                        " intensU="+intensU+
                        " (min,max)Intens=("+
                        Util.cvd2s(minIntens,3)+
                        ","+Util.cvd2s(maxIntens,3)+
                        ")\n  spp.reFilterHistFlag="+
                        spp.reFilterHistFlag+
                        " mae.useIntensHistCLflag="+
                        mae.useIntensHistCLflag);
    */
      
      /* Compute genes in this bin range*/
      //if(spp.reFilterHistFlag)
      if(activeBin!=-1)
      { /* ReCompute genes in this bin range*/
        Util.saveCmdHistory("Filtering by histogram bin "+ncStr, false);
        mae.useIntensHistCLflag= false;
        mae.fc.computeWorkingGeneList(); /* use full list */
        if(plotMode==mae.PLOT_INTENS_HIST)
          mae.cdb.setIntensHistCLfromIntenRange(mae.fc.intensHistCL,
                                                ms, minIntens, maxIntens,
                                                spp.outsideRangeFlag);
        mae.useIntensHistCLflag= true;  /* set flag !!! */
        mae.fc.computeWorkingGeneList();
      /* 
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("DH-FBB.2 intensL="+intensL+
                         " intensU="+intensU+
                         " (min,max)Intens=("+
                         Util.cvd2s(minIntens,3)+
                         ","+Util.cvd2s(maxIntens,3)+
                         ")\n  spp.reFilterHistFlag="+
                         spp.reFilterHistFlag+
                         " mae.useIntensHistCLflag="+
                         mae.useIntensHistCLflag+
                         "\n fc.intensHistCL.length="+
                         mae.fc.workingCL.length+
                         " fc.workingCL.length="+
                         mae.fc.intensHistCL.length);
       */
        mae.updatePseudoImgFlag= true;
        mae.repaint();
      } /* ReCompute genes in this bin range*/
    } /* filter by active bin */
    
    /* Found it!! - Go process */
    mae.mbf.miFRMhistIntensRngFilter.setState(mae.useIntensHistCLflag);
    if(!mae.useIntensHistCLflag)
    { /* restore filter to not use ratio hist gene list */
      mae.fc.computeWorkingGeneList();
      mae.updatePseudoImgFlag= true;
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
   * Note: because there is only ONE plot so can't get into trouble
   * with multiple plots.
   * @param x mouse position
   * @param y mouse position
   * @see #filterByBin
   */
  public void mouseHandler(int x, int y)
  { /* mouseHandler */
    if(nHist==0)
      return;
    
    activeBin= -1;
    for(int i=0;i<MAXBINS-1;i++)
      if(x>=x1[i] && x<=x2[i])
      { /* find closest point */
        activeBin= i;
        break;
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
  public void mouseClicked(MouseEvent e)  { }
  public void mouseEntered(MouseEvent e)  { }
  public void mouseExited(MouseEvent e)  { }
  public void mouseReleased(MouseEvent e)  { }
  public void mouseMoved(MouseEvent e)  { }
  public void mouseDragged(MouseEvent e)  { }
  
  
  /**
   * updateScaling() - set scaling mode in proper sub-object.
   * Linear(false)/Log(true)
   * @param logScalingFlag if using log scaling
   * @see #filterByBin
   */
  void updateScaling(boolean logScalingFlag)
  { /* updateScaling */
    this.logScalingFlag= logScalingFlag;
    filterByBin();                 /* go process it */
  } /* updateScaling */
  
  
} /* end of class DrawHistogram */



