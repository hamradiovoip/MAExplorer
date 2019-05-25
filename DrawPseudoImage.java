/** File: DrawPseudoImage.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * Class to draw microarray data (ratio, intensity, p-value, etc) as a pseudoarray image. 
 * The image is eventually  displayed using
 * ScrollableImageCanvas. Note the Pseudoarray image size is fixed and is
 * mae.(pWidth,pHeight). The pseudoarray image is updated when
 * mae.updatePseudoImgFlag is set by many different processes including the
 * PopupRegistry when the normalization, Filter.workingCL, or 
 * other state values change.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), G.b Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2003/11/24 21:18:54 $  / $Revision: 1.14 $ 
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @ScrollableImageCanvas
 */
 
class DrawPseudoImage 
{
  /** link to global MAExplorer instance */
  private MAExplorer 
    mae;                     
                     
    
  /** [Dark green:dark red] - MUST BE ODD #!!!*/
  final static int
    MAX_COLORS= 9;             
  
  /** flag: color for ratio data. use continuous Red-Yellow-Green color range 
   * from [min:max] else use discrete Red-Black-Green.
   */
  private boolean
    useRYGcolorsFlag;      
   
  /** color table used for PseudoArray image color mapping */
  Color
    ratioColor[];                     
  /** PseudoArray image color range increment */ 
  float
    rangeInc;                              
  /** ratio range for values used in PseudoArray image color mapping */
  float
    ratioRange[];
        
  /** color table used for PseudoArray image color spectrum color mapping */
  Color
    spectrumColor[];                          
  /** range of values for values used in PseudoArray image color spectrum mapping */
  float
    spectrumRange[];           
    
  /** max # pixels in left labels */
  private int
    maxLeftLabelPixelsWidth;
  /** add to orig data coords x_textQ[] */
  private int
    xSpotOffset;              
  /** add to orig data coords y_textQ[] */
  private int
    ySpotOffset;
  /** # pixels in target image on each edge */
  private int
    guardRegion;               
  
  /** integer regular font size */
  private int
   iFontSize;  
  /** integer header font size is 2+ regular size unless 
   * the latter is 12pt in which case the header font size
   * is also 12 pt.
   */
  private int
    iFontHdrSize;
  /** regular font */
  private Font
    font;
  /** Header font */
  private Font
    fontHdr; 
	             
  /** set HP-X (HP-Y) if click on sample name*/
  boolean     
    setHPXflag;                 
  /** list of HP names [mae.hps.nHP+1] */ 
  Point
    hpXYpseudoImgList[];      
    
    
  /**
   * DrawPseudoImage() - constructor
   * @param mae is the MAExplorer instance
   * @see #setPseudoColorRange
   */
  DrawPseudoImage(MAExplorer mae)
  { /* DrawPseudoImage */
    this.mae= mae;
    
   /* Set method for coloring ratios. Use discrete Red-Black-Green
    * else use continuous Red-Yellow-Green color range from [min:max].
    */
    useRYGcolorsFlag= false;
    
    xSpotOffset= MaHPquantTable.X_SPOTQ_OFFSET;
    ySpotOffset= MaHPquantTable.Y_SPOTQ_OFFSET;
    guardRegion= MaHPquantTable.GUARD_REGION;
    
    maxLeftLabelPixelsWidth= 2*guardRegion+xSpotOffset;
    /* move it more to right */
    
    setHPXflag= true;       /* set HP-X (HP-Y) if click on sample name*/
    rangeInc= 0.75F;        /* pseudo color range increment */
    
    setPseudoColorRange();  /* set isZscoreLog flag & pseudocolor range */
  } /* DrawPseudoImage */
  
  
  /**
   * setPseudoColorRange() - set isZscoreLog flag and pseudocolor range.
   * @see Util#setupColorRatioRange
   * @see Util#setupColorSpectrumRange
   */
  private void setPseudoColorRange()
  { /* setPseudoColorRange */
    boolean isZscoreLog= (mae.normByZscoreMeanStdDevLogFlag ||
                          mae.normByZscoreMeanAbsDevLogFlag);   
    
    /* Setup color chart mapping for ratios */
    ratioColor= new Color[MAX_COLORS+1];
    ratioRange= new float[MAX_COLORS];
    
    /* rangeInc: 0.25F : setup color map -0.5   to +2.0 */
    /* rangeInc: 0.50F : setup color map -0.333 to +3.0 */
    /* rangeInc: 0.75F : setup color map -0.25  to +4.0 */
    float changeInc= (isZscoreLog) ? 0.33F*rangeInc : rangeInc;
    Util.setupColorRatioRange(changeInc, ratioColor, ratioRange,
                              MAX_COLORS, mae.isZscoreFlag);
    
    /* color spectrum. E.g. for pValue 0.0 is RED, 1.0 is BLUE. */
    spectrumColor= new Color[MAX_COLORS+1];
    spectrumRange= new float[MAX_COLORS];
    Util.setupColorSpectrumRange(spectrumColor, spectrumRange,
                                 MAX_COLORS, true);
  } /* setPseudoColorRange */
  
  
  /**
   * drawPlus() - draw plus sign using specified color.
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param color is color to draw
   */
  private void drawPlus(Graphics g, int x, int y, Color color)
  { /* drawPlus */
    int w= 2;
    g.setColor( color );
    g.drawLine( x, y-w, x, y+w );
    g.drawLine( x-w, y, x+w, y );
  } /* drawPlus */
  
  
  /**
   * drawFilledGrayCircle() - draw filled circle of specified grayvalue [0:255].
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param radius of object
   * @param grayValue is the gray value color to draw
   */
  private void drawFilledGrayCircle(Graphics g, int x, int y, int radius,
  int grayValue)
  { /* drawFilledGrayCircle */
    int
      gray= 255-grayValue,      /* reverse gray */
      r2= radius/2,
      xC= x-r2,
      yC= y-r2;
    
    if(gray<0)
      gray= 0;
    if(gray>255)
      gray= 255;                /* Math.max(0,Math.min(gray,255)) */
    
    Color color= new Color(gray,gray,gray);
    
    g.setColor( color );
    g.fillOval( xC, yC, radius, radius);
  } /* drawFilledGrayCircle */
  
  
  /**
   * drawFilledColoredCircle() - draw filled circle of specified color
   * Clip colors to [0:255].
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param radius of object
   * @param red is the red color component
   * @param green is the green color component
   * @param blue is the blue color component
   */
  private void drawFilledColoredCircle(Graphics g, int x, int y, int radius,
                                       int red, int green, int blue)
  { /* drawFilledColoredCircle */
    int
      r2= radius/2,
      xC= x-r2,
      yC= y-r2;
    
    /* Clip colors to [0:255] */
    if(red<0)
      red= 0;            /* Math.max(0,red) */
    if(green<0)
      green= 0;          /* Math.max(0,green) */
    if(blue<0)
      blue= 0;           /* Math.max(0,blue) */
    if(red>255)
      red= 255;          /* Math.min(red,255) */
    if(green>255)
      green= 255;        /* Math.min(green,255) */
    if(blue>255)
      blue= 255;         /* Math.min(blue,255) */
    
    Color color= new Color(red, green, blue);
    
    g.setColor( color );
    g.fillOval( xC, yC, radius, radius);
  } /* drawFilledGrayCircle */
  
  
  /**
   * drawFilledColoredCircle() - draw filled circle of specified color.
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param radius of object
   * @param color is the color to draw
   */
  private void drawFilledColoredCircle(Graphics g, int x, int y,
  int radius, Color color)
  { /* drawFilledColoredCircle */
    int
      r2= radius/2,
      xC= x-r2,
      yC= y-r2;
    
    g.setColor( color );
    g.fillOval( xC, yC, radius, radius);
  } /* drawFilledGrayCircle */
  
  
  /**
   * clipStrToWidth() - clip string to maxPixelWidth and font char width.
   * If the string will not fit in the maxPixelWidth, then remove
   * characters that exceed that length.
   * @param str is the string to possibly clip
   * @param maxPixelWidth is the maximum pixel width
   * @param maxSubStrLen is the max substring length
   * @param g is graphics context
   * @param font is the current font being used
   * @return possibly clipped string
   */
  String clipStrToWidth(String str, int  maxPixelWidth, int maxSubStrLen,
  Graphics g, Font font)
  { /* clipStrToWidth */
    String sR= str;
    if(str==null)
      return(null);
    
    int
      sLen= str.length(),
      nSRchars= sLen;
    
    if(mae.CONSOLE_FLAG && sLen>0)
    {
      if(maxSubStrLen==0)
        maxSubStrLen= 18;            /* worst case */
      nSRchars= Math.min(maxSubStrLen,sLen);
      sR= str.substring(0,nSRchars);  /* max # of characters */
      return(sR);
    }
    
    if(sLen>0)
    { /* clip it */
      FontMetrics fm= g.getFontMetrics(font);
      int
        strWidth= fm.stringWidth(str),
        sRwidth= strWidth;
      
      if(sRwidth>maxPixelWidth)
      { /* clip it iteratively */
        while(sRwidth>maxPixelWidth && nSRchars>0)
        { /* shrink it until fits */
          nSRchars--;
          sR= str.substring(0,nSRchars);
          sRwidth= fm.stringWidth(sR);
          /*
          if(mae.CONSOLE_FLAG)
            System.out.println("DP-CSTW sLen="+sLen+
                               " nSRchars="+nSRchars+
                               " sRwidth="+sRwidth+
                               " strWidth="+strWidth+
                               " maxPixelWidth="+maxPixelWidth+
                               " sR='"+sR+"' str='"+str+"'");
          */
        } /* shrink it until fits */
      } /* clip it iteratively */
    } /* clip it */
    /*
    if(mae.CONSOLE_FLAG)
      System.out.println("DP-CSTW.2 sLen="+sLen+" nSRchars="+nSRchars+
                         " sR='"+sR+"' str='"+str+"'");
    */
    return(sR);
  } /* clipStrToWidth */
  
  
  /**
   * setFontsFromState() - set the regular and header fonts from the state variables
   */
  private void setFontsFromState()
  { /* setFontsFromState */
    iFontSize= ((mae.rptFontSize.equals("12pt")
                  ? 12
                  : ((mae.rptFontSize.equals("8pt")
                        ? 8
                        : 10))));
    iFontHdrSize= Math.min((iFontSize+2), 12);
    font= new Font(mae.cfg.fontFamily, Font.PLAIN, iFontSize);
    fontHdr= new Font(mae.cfg.fontFamily, Font.PLAIN, iFontHdrSize);
  } /* setFontsFromState */
  
  
  /**
   * drawScaleMap() - draw color scale map on the left of the canvas
   * @param gi is graphics context
   * @param isRatioPlot indicates doing a ratio rather than intensity plot
   * @param useRYGcolorsFlag using additive colors rather than lookup map
   * @param minI minimum intensity
   * @param maxI maximum intensity
   * @see #clipStrToWidth
   */
  private void drawScaleMap(Graphics gi, boolean isRatioPlot,
                            boolean useRYGcolorsFlag,
                            float minI, float maxI)
  { /* drawScaleMap */
    int
      gray,
      precision= 3,
      width= 15,          /* pixels for box */
      height= 15,
      yTitle= 50,
      x= 5,               /* left margin */
      y= yTitle+12+height,
      yLabel,
      xLabel= x+width+5;  /* space between box and label */
    float val;
    Color
      color,
      colorRYG[][]= new Color[MAX_COLORS][MAX_COLORS], /* [red][geeen] */
      colorTxt= (isRatioPlot) ? Color.white : Color.black,
      colorTxtHdr= (isRatioPlot)
                      ? new Color(180,180,255) /* was Color(100,100,255) */
                      : Color.blue;
    String
      s,         /* actual clipped string drawn */
      sLbl,
      sRange,
      changeStr= (mae.isZscoreFlag) ? "Zdiff" : "ratio",
      sF1F2= mae.reportRatioStr;	 /* [OLD] mae.cfg.fluoresLbl1+mae.cfg.fluoresLbl2, */
    
    if(mae.plotImageMode==mae.PLOT_PSEUDO_F1F2_RYG_IMG)
      sF1F2=((mae.useCy5OverCy3Flag)
               ? (mae.cfg.fluoresLbl1+"+"+ mae.cfg.fluoresLbl2)
               : (mae.cfg.fluoresLbl2+"+"+ mae.cfg.fluoresLbl1));
      
    String
      msg= (mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_IMG ||
            mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_RYG_IMG)
              ? ((mae.useHPxySetDataFlag)
                   ? "HP-XY 'set' " : "HP-XY ")+changeStr
                   : ((mae.plotImageMode==mae.PLOT_PSEUDO_F1F2_IMG ||
                       mae.plotImageMode==mae.PLOT_PSEUDO_F1F2_RYG_IMG)
                         ? sF1F2+" "+changeStr
                         : ((mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_P_VALUE_IMG)
                              ?  "HP-X vs HP-Y 'sets' p-Value"
                              : ((mae.plotImageMode==mae.PLOT_PSEUDO_HP_EP_CV_VALUE_IMG)
                                  ?  "HP-EP 'list' CV-Value"                                
                                  : ((mae.dualXYpseudoPlotFlag)
                                      ?  "HP-X (left) & HP-Y (right)"
                                      : "Intensity Current Sample"))));
      
      /* Redefine the regular 'font' and 'fontHdr' fonts */
      setFontsFromState();
      gi.setFont(fontHdr);
      gi.setColor(colorTxtHdr);
      
      /* [1] Setup SUM Red+Green color map */
      if(useRYGcolorsFlag)
      { /* special case of X+Y = Red+green */
        int
          maxColor= 255/(MAX_COLORS-1),
          halfMAX_COLORS= (MAX_COLORS-1)/2;
        for(int i=MAX_COLORS-1;i>=0;i--)
          for(int j=0;j<MAX_COLORS;j++)
          {
            int
              k= (MAX_COLORS-1)-j,
              mc= i*maxColor,
              r= (j<=halfMAX_COLORS) ? mc : (k*mc)/halfMAX_COLORS,
              g= (j>=halfMAX_COLORS) ? mc : (j*mc)/halfMAX_COLORS;
              colorRYG[i][j]= new Color(r,g,0);
            /*
            if(mae.CONSOLE_FLAG)
             System.out.println(" [i,j]=["+i+","+j+"] k="+k+
                                " [r,g]=["+r+","+g+"]");
            */
          }
      } /* special case of X+Y = Red+green */
      
      /* [1.1] Draw the normalization method into the plot state. */
      gi.drawString(mae.normNameDisp,x,yTitle);
      
     /* [2] Draw the type of pseudocolor plot:
      *           Intensity, X/Y, X/Y sets, or F1/F2
      */
      gi.drawString(msg,x,yTitle+15);
      
      /* [2.1] Vertical table of colors and corresponding values */
      gi.setFont(font);
      gi.setColor(colorTxt);
      for(int i=MAX_COLORS-1;i>=0;i--)
      { /* draw ith bin */
        yLabel= y+height-3;
        if(mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_P_VALUE_IMG ||
           mae.plotImageMode==mae.PLOT_PSEUDO_HP_EP_CV_VALUE_IMG)
        { /* color range spectrum Red, orange, yellow, Green, ... */
          val= spectrumRange[i];
          color= spectrumColor[i];
        }
        else if(isRatioPlot)
        { /* color range Red - Black - Green */
          val= ratioRange[i];
          color= ratioColor[i];
        }
        else
        { /* Grayscale range White throuh Black */
          val= ((i*(maxI-minI)/MAX_COLORS)+minI)*mae.cfg.lowRangeScaleFactor;
          gray= (255*(MAX_COLORS-1-i))/MAX_COLORS;
          color= new Color(gray,gray,gray);
          if(maxI>1000.0F)
            precision= 1;
          else if(maxI<10.0F)
            precision= 4;
        }
        sRange= (i==0)
                  ? "<" 
                  : ( (i==MAX_COLORS-1)
                          ? ">"  : "");
        if(useRYGcolorsFlag)
        { /* special case of SUM of X+Y = Red+Green */
          int w= 2*width/MAX_COLORS;
          for(int j=0;j<MAX_COLORS;j++)
          {
            gi.setColor(colorRYG[i][j]);
            gi.fillRect((x+j*w),y, w,height);
          }
          val= i/(MAX_COLORS-1.0F)*mae.cfg.lowRangeScaleFactor;
          sLbl= Util.cvf2s(val,precision);
          
          gi.setColor(colorTxt);
          gi.drawString(sLbl, xLabel+width,yLabel);
          if(i==0)
          {
            gi.setColor(colorTxt);
            gi.drawString("X  Y", x,yLabel);
          }
        }
        else
        { /* single color */
          gi.setColor(color);
          gi.fillRect(x,y, width,height);
          if(mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_P_VALUE_IMG)
          { /* p-values no prefix */
            float pValue= val;
            sLbl= Util.cvf2s(pValue,precision);
          }
          else if(mae.plotImageMode==mae.PLOT_PSEUDO_HP_EP_CV_VALUE_IMG)
          { /* CV-values no prefix */
            float cvValue= val;
            sLbl= Util.cvf2s(cvValue,precision);
          }
          else
            sLbl= sRange+Util.cvf2s(val,precision);
          gi.setColor(colorTxt);
          gi.drawString(sLbl, xLabel,yLabel);
        }
        
        y += height+2;        /* space between this and next box */
      } /* draw ith bin */
      
       /* [3] Draw list of HP-E samples and save (x,y) list for possible
        * use with mae.is.siCanvas event handler to pick new current sample.
        */
      if(mae.viewFilteredSpotsFlag)
      { /* Show additional state information */
        /* [3.1] show HP-E list */
        hpXYpseudoImgList= new Point[mae.hps.nHP+1];
        y += height+8;    /* space between color map and HP-E list */
        gi.setFont(fontHdr);
        gi.setColor(Color.magenta);
        gi.drawString(((setHPXflag) ? "[X]" : "[Y]"), x, y);
        gi.setColor(colorTxtHdr);
        gi.drawString("Current Sample",x+15,y);
        gi.setColor(colorTxt);
        hpXYpseudoImgList[0]= new Point(x+2,y-2);
        y += height+2;    /* space between HP-E entries */
        
        gi.setFont(font);
        for(int i=1;i<=mae.hps.nHP;i++)
        {
          gi.setColor(Color.magenta);
          hpXYpseudoImgList[i]= new Point(x+2,y-2);
          gi.drawString("*",x,y);
          if(mae.hps.msList[i]!=mae.ms)
            gi.setColor(colorTxt);
          String hpName= mae.hps.msList[i].hpName;
          hpName= clipStrToWidth(hpName, maxLeftLabelPixelsWidth,
                                 14, gi, font);
          //if(hpName.length()>15)
          //  hpName= hpName.substring(0,15);
          gi.drawString(hpName,x+5,y);
          y += height;   /* space between HP-E entries */
        }
        
        /* [3.2] Show list of active filters */
        y += height+8;    /* space between color map and HP-E list */
        gi.setFont(fontHdr);
        gi.setColor(colorTxtHdr);
        gi.drawString(" Active Filters",x,y);
        gi.setColor(colorTxt);
        y += height+2;        /* space between HP-E entries */
        gi.setFont(font);
        for(int i=0;i<mae.fc.nActiveFilters;i++)
        {
          s= clipStrToWidth(mae.fc.activeFilterNames[i],
          maxLeftLabelPixelsWidth, 18,
          gi, font);
          gi.drawString(s,x+5,y);
          y += height;   /* space between Filter entries */
        }
        
        /* [3.3] Show active GeneClass */
        y += height+8;   /* space between color map and HP-E list */
        gi.setFont(fontHdr);
        gi.setColor(colorTxtHdr);
        gi.drawString(" Active GeneClass",x,y);
        gi.setColor(colorTxt);
        y += height+2;        /* space between HP-E entries */
        gi.setFont(font);
        s= clipStrToWidth(mae.gct.geneClassName[mae.gct.curGeneClass],
                          maxLeftLabelPixelsWidth, 18, gi, font);
        gi.drawString(s, x,y);
        
        
        /* [3.4] Show current FontFamily */
        y += 2*height+8;   /* space between color map and HP-E list */
        gi.setFont(fontHdr);
        gi.setColor(colorTxtHdr);
        gi.drawString(" Font Family",x,y);
        gi.setColor(colorTxt);
        y += height+2;        /* space between HP-E entries */
        gi.setFont(font);
        s= clipStrToWidth(mae.cfg.fontFamily,
        maxLeftLabelPixelsWidth, 18, gi, font);
        gi.drawString(s, x,y);
        
      } /* Show additional state information */
  } /* drawScaleMap */
  
  
  /**
   * drawGridLabelsInImage() - draw ms.textQ[] in the image at (x,y)[] locations.
   * NOTE: the data is setup in the Sample object ms.
   * @param ms is sample being used
   * @param g is graphics context
   * @param isRatioPlot is ratio plot rather than intensity plot
   * @param isDualPseudoImageFlag shows two arrays side by side
   * @see SampleSets#setHPxyModStrings
   */
  private void drawGridLabelsInImage(MaHybridSample ms, Graphics g,
                                     boolean isRatioPlot,
                                     boolean isDualPseudoImageFlag)
  { /* drawGridLabelsInImage */
    if(!ms.dataAvailFromQuantFileFlag)
      return;                 /* don't draw it!!! */
    
    boolean
      isXY_pValue= (mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_P_VALUE_IMG),
      isEP_CVvalue= (mae.plotImageMode==mae.PLOT_PSEUDO_HP_EP_CV_VALUE_IMG);
    int
      deltaX= -5,		/* Offset so draw label at that point */
      deltaY= +5,
      xC, yC;
    CompositeDatabase cdb= mae.cdb;
    String
      sLbl1,
      sLbl2,
      gLbl;

    /* Setup mae.hps.(sMod, sModX, sModY) based on whether using
     * ratio mode or swapCy5Cy3DataFlags
     */
    mae.hps.setHPxyModStrings();
    
    g.setFont(cdb.font_textHP[0]);    /* default is Label font */
    for(int i=1;i<=cdb.nTextItems; i++)
    {
      /* draw over the pseudo image */
      xC= ms.x_textQ[i];
      yC= ms.y_textQ[i];
      
      /* Draw it only if in clipped canvas and text is not null */
      if(xC>0 && yC>0 && ms.textHP[i].length()>0)
      { /* draw it */
        if(i==0 ||
           (i>0 && cdb.color_textHP[i]!=null &&
           cdb.color_textHP[i]!=cdb.color_textHP[i-1]))
          g.setColor(cdb.color_textHP[i]);
        if(i==0 ||
           (i>0 && cdb.font_textHP[i]!=null &&
           cdb.font_textHP[i]!=cdb.font_textHP[i-1]))
          g.setFont(cdb.font_textHP[i]);
        
        if(isRatioPlot)
          g.setColor(Color.white);
        
        if(i==1 && (isRatioPlot || isDualPseudoImageFlag || isXY_pValue))
        { /* special HP-X and HP-Y or F1 F2 labels */
          if(mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_IMG ||
             mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_RYG_IMG ||
             mae.dualXYpseudoPlotFlag || isXY_pValue)
          { /* Label HP-X and HP-Y */
            if(!mae.useHPxySetDataFlag)
            { /* Label single Samples HP-X and HP-Y */
              sLbl1= "HP-X: "+mae.msX.textHP[i]+mae.hps.sModX;
              sLbl2= "HP-Y: "+mae.msY.textHP[i]+mae.hps.sModY;
            }
            else
            { /* Label multiple Samples ClassX & classY names */
              sLbl1= "HP-X: "+mae.classNameX+mae.hps.sModX;
              sLbl2= "HP-Y: "+mae.classNameY+mae.hps.sModY;
            }
          } /* Label HP-X and HP-Y */
          else
          { /* Label F1 and F2 */
            sLbl1= mae.cfg.fluoresLbl1 + " " + ms.textHP[i];
            sLbl2= mae.cfg.fluoresLbl2 + " " + ms.textHP[i];
            if(mae.useCy5OverCy3Flag)
            { /* flip Cy3/Cy5 to Cy5/Cy3 */
              String sTmp= sLbl1;
              sLbl1= sLbl2;
              sLbl2= sTmp;
            }
          }
          g.drawString(sLbl1, 40, yC+deltaY);
          g.drawString(sLbl2, 40, yC+deltaY+16);
        } /* special HP-X and HP-Y or F1 F2 labels */
        
        else if(i==1 && mae.useHPxySetDataFlag)
        { /* Label multiple Samples ClassX & classY names */
          sLbl1= (setHPXflag)
                   ? "HP-X: "+mae.classNameX
                   : "HP-Y: "+mae.classNameY;
          g.drawString(sLbl1, 40, yC+deltaY);
        }
        else
        { /* draw grid labels */
          gLbl= ms.textHP[i];
          if(isDualPseudoImageFlag)
          { /* overide with 'set' HP-X and HP-Y data */
            gLbl= ((i<=mae.cfg.maxGRIDS) ? "X" : "Y") +
            gLbl.substring(1);
          }
          int
            gridX= ((i==1) ? 40 : xC+deltaX), 
            gridY= yC+deltaY;
          g.drawString(gLbl, gridX, gridY);
           
          if(mae.CANVAS_WRAPAROUND_BUG)
            System.out.println("DPI-DGLII gLbl="+gLbl+" gridX="+gridX+" gridY="+gridY);
        }
      } /* draw it */
    }
  } /* drawGridLabelsInImage */
  
  
  /**
   * drawPseudoImage() - draw pseudoarray image of filled grayscale circles
   * using the totC[] density values.
   * Depending on the value of plotModePseudImage, it implements:
   *<PRE>
   *   PLOT_PSEUDOIMG                  -  gray value white to black
   *   PLOT_PSEUDO_HP_XY_P_VALUE_IMG   -  pValue[0.0:1.0] spectrum value white to black
   *   PLOT_PSEUDO_HP_EP_CV_VALUE_IMG  -  pValue[0.0:1.0] spectrum value white to black
   *   PLOT_PSEUDOCOLOR_HP_XY_RYG_IMG  -  sum Red(X)+Green(Y) is Red,yellow,green
   *   PLOT_PSEUDO_F1F2_RYG_IMG        -  sum Red(F2)+Green(F1) is Red,yellow,green
   *   PLOT_PSEUDOCOLOR_HP_XY_IMG      -  ratio X/Y is Red,black,green
   *   PLOT_PSEUDOCOLOR_F1F2_IMG       -  ratio F1/F2 is Red,black,green
   *</PRE>
   * NOTE: currently it maps data to either RGB or 0:255 grayscale.
   * @param gi is graphics context
   * @param piWidth is width of image
   * @param piHeight is height of image
   * @param plotMode is type of image
   * @param ms is sample being plotted
   * @see HPxyData#updateDataAndStat
   * @see MaHybridSample#computeMinMaxF1F2Data
   * @see MaHybridSample#getDataByGID
   * @see MaHybridSample#getSpotDataStatic
   * @see MaHybridSample#getSpotData12Static
   * @see Statistics#calcTandPvalues
   * @see Util#setRatioColor
   * @see #drawFilledColoredCircle
   * @see #drawGridLabelsInImage
   * @see #drawScaleMap
   * @see #setPseudoColorRange
   */
  void drawPseudoImage(Graphics gi, int piWidth, int piHeight,
                       int plotMode, MaHybridSample ms)
  { /* drawPseudoImage */
    MaHybridSample
      msX= mae.msX,
      msY= mae.msY;
    Statistics stat= mae.stat;
    HPxyData hpXYdata= mae.cdb.hpXYdata;
    SpotData sd;
    int
      maxGenes= mae.mp.maxGenes,
      radius= mae.spotRad+1,
      maxFIELDS= mae.cfg.maxFIELDS,
      type= (mae.useRatioDataFlag)
               ? ms.DATA_RATIO_F1F2TOT : ms.DATA_F1TOT;
    boolean
      isRBGratioXY= (plotMode==mae.PLOT_PSEUDO_HP_XY_IMG),
      isXY_pValue= (plotMode==mae.PLOT_PSEUDO_HP_XY_P_VALUE_IMG),
      isEP_CVvalue= (mae.plotImageMode==mae.PLOT_PSEUDO_HP_EP_CV_VALUE_IMG),
      isRYGratioXY= (plotMode==mae.PLOT_PSEUDO_HP_XY_RYG_IMG),
      isRYGratioF1F2= (plotMode==mae.PLOT_PSEUDO_F1F2_RYG_IMG),
      isRatioPlot= (isRBGratioXY || isRYGratioXY || isRYGratioF1F2 ||
      plotMode==mae.PLOT_PSEUDO_F1F2_IMG),
      isDualPseudoImageFlag= (mae.dualXYpseudoPlotFlag &&
                              plotMode==mae.PLOT_PSEUDOIMG);
    float
      minI= ms.minDataS,
      maxI= ms.maxDataS,
      minIX= minI,
      maxIX= maxI,
      minIY= minI,
      maxIY= maxI,
      range= (maxI-minI),
      rangeX= range,
      rangeY= range,
      scaleRng= 255.0F/range,        /* Auto-ranging */
      scaleRngX= scaleRng,
      scaleRngY= scaleRng,
      lowRangeScaleFactor=mae.cfg.lowRangeScaleFactor;  /* local copy for speed */
    int
      mid2gid[]= mae.mp.mid2gid,
      gidToGangGid[]= mae.mp.gidToGangGid,
      mid,
      gid,
      gidG,
      red,
      green,
      intens;
    Gene
      mList[]= mae.mp.midStaticCL.mList,
      gene;
    float
      xF= 0.0F,                      /* X and Y normalized data */
      yF= 0.0F,
      gData= 0.0F,
      changeData;
    Point p;
    Color color;
    
    if(isRYGratioXY || isXY_pValue)
    { /* HP-X and HP-Y */
      if(mae.useHPxySetDataFlag)
      { /* HP-X 'set' and HP-Y 'set' */
        minIX= mae.cdb.minDataHP_X;
        maxIX= mae.cdb.maxDataHP_X;
        minIY= mae.cdb.minDataHP_Y;
        maxIY= mae.cdb.maxDataHP_Y;
        rangeX= (maxIX-minIX);
        rangeY= (maxIY-minIY);
      }
      else
      { /* individual HP-X and HP-Y samples */
        minIX= msX.minDataS;
        maxIX= msX.maxDataS;
        minIY= msY.minDataS;
        maxIY= msY.maxDataS;
        rangeX= (maxIX-minIX);
        rangeY= (maxIY-minIY);
      }
    } /* HP-X and HP-Y */
    
    else if(isRYGratioF1F2)
    {/* F1 and F2 of current HP */
      ms.computeMinMaxF1F2Data(false, /*useF1F2ratioFlag */ true   /* useAllGenesFlag */);
      minIX= ms.minDataF1;
      maxIX= ms.maxDataF1;
      minIY= ms.minDataF2;
      maxIY= ms.maxDataF2;
      
     /* [TODO] Need to give it the gidMAX and gidMIN values in case
      * need them for scaling with Normalization Plugins.
      */
      /*
      minIX= ms.scaleIntensData(ms.minDataF1,ms.gidMinF1);
      maxIX= ms.scaleIntensData(ms.maxDataF1,ms.gidMaxF1);
      minIY= ms.scaleIntensData(ms.minDataF2,ms.gidMinF2);
      maxIY= ms.scaleIntensData(ms.maxDataF2,ms.gidMaxF2);
      */
      rangeX= (maxIX-minIX);
      rangeY= (maxIY-minIY);
    }
    
    /* [NOTE] a problem with taking the extrema is that it will have a minimum
     * scale range for most data if there are a few very large or very small
     * outliers. Rescale the range of the by lowRangeScaleFactor
     * as the dynamic range and then outliers would be bright colors with more
     * sensitivity to the values toward the middle.
     * If we do that then we need to clip the color to [0:255] or we will blow
     * the dynamic range.
     */
    /* compute range for separate channels */
    scaleRng /=lowRangeScaleFactor;
    scaleRngX= 255.0F/(lowRangeScaleFactor*rangeX);
    scaleRngY= 255.0F/(lowRangeScaleFactor*rangeY);
    
    useRYGcolorsFlag= (isRYGratioXY  || isRYGratioF1F2); /* use [Red-yellow-green]
                                                          * if false */
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("\nDPI plotMode="+plotMode+
                       " useRYGcolorsFlag="+useRYGcolorsFlag+
                       "\n minI="+Util.cvf2s(minI,1)+
                       " maxI="+Util.cvf2s(maxI,1)+
                       " scaleRng="+Util.cvf2s(scaleRng,5)+
                       "\n minIX="+Util.cvf2s(minIX,1)+
                       " maxIX="+Util.cvf2s(maxIX,1)+
                       " scaleRngX="+Util.cvf2s(scaleRngX,5)+
                       "\n minIY="+Util.cvf2s(minIY,1)+
                       " maxIY="+Util.cvf2s(maxIY,1)+
                       " scaleRngY="+Util.cvf2s(scaleRngY,5));
    */
    
    setPseudoColorRange();     /* set isZscoreFlag & pseudocolor range used
                                * for drawing legend and for ratio spot color */
    
    color= (isRatioPlot)
              ? Color.black  /* better contrast for pseudocolor */
              : ((isXY_pValue || isEP_CVvalue)
                   ? new Color(240,240,240)   /* very light gray */
                   : new Color(200,255,255)); /* Color.cyan,
                                               * #C6EFF7= light cyan=(186,239,247)
                                               * or Color.white */
    
    /* Color the background before draw */
    gi.setColor( color );
    gi.fillRect(0,0,piWidth,piHeight);   /* draw the background */
    
   /* Draw grayscale or color scale legend map on the left of the canvas.
    * This also draws active HP lists, Filter State, GeneClass State, etc.
    */
    drawScaleMap(gi, isRatioPlot, useRYGcolorsFlag, minI, maxI);
    
    for(int f=1;f<=maxFIELDS;f++)
      for(int k=0;k<maxGenes;k++)
      { /* draw spot in current mode */
        gene= mList[k];
        if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
          continue;            /* ignore bogus spots */
      
        mid= gene.mid;
        gid= mid2gid[mid];
        if(f==2)
          gid= gidToGangGid[gid];
        
        if(mae.CANVAS_WRAPAROUND_BUG)
        { // debug code to look at (x,y) data from last grid
          GridCoords grc= mae.mp.gid2fgrc[gid];            
          p= SpotData.xyS; 
          int
            grcG= grc.g,
            grcR= grc.r,
            grcC= grc.c;  
          if((grcG==(mae.cfg.maxGRIDS/2) || grcG>=(mae.cfg.maxGRIDS-1)) && grcR==1)
            System.out.println("DPI-DPI pi(Width,Height)=("+piWidth+","+piHeight+
                               ") p[G,R,C])["+grcG+","+grcR+","+grcC+
                               "]=("+ p.x+","+p.y+")");
        }
      
        if(plotMode==mae.PLOT_PSEUDOIMG || isXY_pValue || isEP_CVvalue)
        { /* draw pseudograyscale current HP image */
          /* plot each single spot or ratio data */
          gData= ms.getDataByGID(gid, mae.useRatioDataFlag,
                                 ms.DATA_F1TOT);
          /* this gets intensity and XY coords */
          p= SpotData.xyS; 
          if(mae.dualXYpseudoPlotFlag && !mae.useHPxySetDataFlag)
          { /* overide intensity  with HP-X in f1 & HP-Y in F2*/
            if(f==1)
              gData= msX.getDataByGID(gid, mae.useRatioDataFlag,
                                      ms.DATA_MEAN_F1F2TOT);
            else
              gData= msY.getDataByGID(gid, mae.useRatioDataFlag,
                                      ms.DATA_MEAN_F1F2TOT);
          }
          if(mae.useHPxySetDataFlag)
          { /* may overide with 'set' HP-X and HP-Y data */
            if(!hpXYdata.updateDataAndStat(mid))
              continue;
            if(mae.dualXYpseudoPlotFlag)
              gData= (f==1) ? hpXYdata.mnXdata : hpXYdata.mnYdata;
            else
              gData= (setHPXflag) ? hpXYdata.mnXdata : hpXYdata.mnYdata;
          }
          
          /* Scale it to 8-bits for drawing depending on the mode */
          if(isXY_pValue)
          { /* scale the pseudocolorgray value from the p-Value */
            double
              mn1= (double)hpXYdata.mnXdata,
              mn2= (double)hpXYdata.mnYdata,
              sd1= (double)hpXYdata.stdDevXdata,
              sd2= (double)hpXYdata.stdDevYdata;
            boolean ok= stat.calcTandPvalues(hpXYdata.nX, hpXYdata.nY,
                                             mn1,mn2,sd1,sd2);
            gene.pValue= (float)stat.pT;
            int ipv= MAX_COLORS-1;
            float pValue= gene.pValue;
            
            for(int i= MAX_COLORS-1; i>=0;i--)
              if(pValue >= spectrumRange[i])
              { /* find first color that value >= range value */
                ipv= i;
                break;
              }
            color= spectrumColor[ipv];
            drawFilledColoredCircle(gi, p.x, p.y, radius, color);
          } /* scale the pseudocolorgray value from the p-Value */
          
          else if(isEP_CVvalue)
          { /* scale the pseudocolor gray value from the HP_EP CV Value */
            mae.cdb.hpXYdata.updateEPdataAndStat(mid);
            float
              mnE= hpXYdata.mnEdata,
              sdE=hpXYdata.stdDevEdata,
              cvE= (mnE>0.0) ? sdE/mnE : 0.0F;
            gene.cv= cvE;
            int ipv= MAX_COLORS-1;
            
            for(int i= MAX_COLORS-1; i>=0;i--)
              if(cvE >= spectrumRange[i])
              { /* find first color that value >= range value */
                ipv= i;
                break;
              }
            color= spectrumColor[ipv];
            drawFilledColoredCircle(gi, p.x, p.y, radius, color);
          } /* scale the pseudocolor gray value from the HP_EP CV Value  */
          else
          { /* just scale to gray value */
            intens= (int)(scaleRng*(gData-minI));
            intens= (intens>255) ? 255: ((intens<0) ? 0 : intens);
            drawFilledGrayCircle(gi,p.x,p.y,radius,intens);
          }
         /*
         if(mae.CONSOLE_FLAG & gid==1775)
            mae.fio.logMsgln("DPI gid="+gid+" f="+f+
                        " p(x,y)=("+p.x+","+p.y+")");
          */
        } /* draw pseudograyscale current HP image */
        
        else if((plotMode==mae.PLOT_PSEUDO_HP_XY_IMG) ||
                (plotMode==mae.PLOT_PSEUDO_HP_XY_RYG_IMG))
        { /* draw (red,yellow,green) pseudo color HP-X/HP-Y image */
          /* Subract minimum to bring minimum to 0 */
          if(mae.useHPxySetDataFlag)
          { /* get 'set' HP-X and HP-Y data */
            if(!hpXYdata.updateDataAndStat(mid))
              continue;
            xF= hpXYdata.mnXdata - minIX;
            yF= hpXYdata.mnYdata - minIY;
          }
          else
          { /* get single sample HP-X and HP-Y data */
            xF= msX.getDataByGID(gid,mae.useRatioDataFlag,type) - minIX;
            yF= msY.getDataByGID(gid,mae.useRatioDataFlag,type) - minIY;
          }
          
          mae.ms.getSpotDataStatic(gid,mae.useRatioDataFlag);
          /* use only for getting xyCoords */
          p= SpotData.xyS;
          
          if(!useRYGcolorsFlag)
          { /* use RATIO X/Y to range over RED,BLACK,GREEN by lookup */
            changeData= (mae.isZscoreFlag)
                          ? (xF-yF)
                          : ((yF==0.0F) ? 1.0F : xF/yF);
            
            color= Util.setRatioColor(changeData, ratioColor,
            ratioRange, MAX_COLORS);
            drawFilledColoredCircle(gi, p.x, p.y, radius, color);
          } /* use RATIO X/Y to range over RED,BLACK,GREEN by lookup */
          
          else
          { /* use SUM of colors RED to YELLOW to GREEN */
            red= (int)(scaleRng*xF);
            green= (int)(scaleRng*yF);
            red= (red>255) ? 255: ((red<0) ? 0 : red);
            green= (green>255) ? 255: ((green<0) ? 0 : green);
            drawFilledColoredCircle(gi, p.x, p.y, radius, red, green, 0);
          }
        } /* draw pseudo color HP-X/HP-Y image */
        
        else if(plotMode==mae.PLOT_PSEUDO_F1F2_IMG ||
        plotMode==mae.PLOT_PSEUDO_F1F2_RYG_IMG)
        { /* draw pseudo color F1/F2 ratio or sum of current HP image */
          /* NOTE: the ratio is the same on both sides! */
          gidG= mae.mp.gidToGangGid[gid];
          if(f==1)
            sd= ms.getSpotData12Static(gid, gidG, false);
          else
            sd= ms.getSpotData12Static(gidG, gid, false);
          xF= sd.tot1S - minIX;
          yF= sd.tot2S - minIY;
          if(mae.useCy5OverCy3Flag)
          { /* GLOBAL Flip Cy3/Cy5 to Cy5/Cy3 */
            float fTmp= xF;
            xF= yF;
            yF= fTmp;
          }
          if(plotMode==mae.PLOT_PSEUDO_F1F2_RYG_IMG)
          { /* GLOBAL Flip Cy5(red) and Cy3(green) */
            float fTmp= xF;
            xF= yF;
            yF= fTmp;
          }
          p= SpotData.xy1S;
          
          if(!useRYGcolorsFlag)
          { /* use RATIO colors RED to BLACK to GREEN */
            changeData= (mae.isZscoreFlag)
                          ? (xF-yF)
                          : ((yF==0.0F)
                               ? 1.0F : xF/yF);
            color= Util.setRatioColor(changeData,
                                      ratioColor, ratioRange,
                                      MAX_COLORS);
            drawFilledColoredCircle(gi, p.x, p.y, radius, color);
          } /* use RATIO colors RED to BLACK to GREEN */
          else
          { /* use SUM colors RED(F1) to YELLOW to GREEN(F2) */
            red= (int)(scaleRng*xF);
            green= (int)(scaleRng*yF);
            red= (red>255) ? 255: ((red<0) ? 0 : red);
            green= (green>255) ? 255: ((green<0) ? 0 : green);
          /*
          if(mae.CONSOLE_FLAG && gid>20 && gid < 30)
            mae.fio.logMsgln("DPI-F1+F2 gid="+gid+
                            " xF="+Util.cvf2s(xF,3)+
                            " yF="+Util.cvf2s(yF,3)+
                            " sRngX="+Util.cvf2s(scaleRngX,3)+
                            " sRngY="+Util.cvf2s(scaleRngY,3)+
                            " red="+red+" green="+green+
                            " minIX="+Util.cvf2s(minIX,1)+
                            " minIY="+Util.cvf2s(minIY,1)+
                            " maxIX="+Util.cvf2s(maxIX,1)+
                            " maxIY="+Util.cvf2s(maxIY,1));
            */
            drawFilledColoredCircle(gi, p.x, p.y, radius, red, green, 0);
          } /* use SUM colors RED(F1) to YELLOW to GREEN(F2) */
        } /* draw pseudo color F1/F2 ratio or sum of current HP image */
        
      } /* draw spot in current mode */
    
    /* Draw ms.textQ[] in the image at (x,y)[] locs.
     * NOTE: the data is setup in the Sample object ms.
     */
    drawGridLabelsInImage(ms, gi, isRatioPlot, isDualPseudoImageFlag);
  } /* drawPseudoImage */
  
  
} /* end of class DrawPseudoImage */



