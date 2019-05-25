/** File: Draw2DPlot.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * This is a base class used for drawing 2D graphics plots. 
 * It contains code to draw axes, titles, captions, scale the data, 
 * overlay graphics, point event handlers, etc. 
 * In particular DrawScatterPlot, ExprProfileOverlay extend this class.
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.14 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see DrawScatterPlot
 * @see ExprProfileOverlay 
 */

class Draw2Dplot extends Canvas implements MouseListener, MouseMotionListener
{
  /** link to global instance */
  MAExplorer 
    mae;                         
  /** link to global MaHybridSample instance */
  MaHybridSample
    ms;                         
  /** link to global Filter instance */
  Filter
    fc;                        
  /** link to global CompositeDatabase instance */
  CompositeDatabase
    cdb;                      
            
  /** if ExprProfileOverlay is the parent, else null */
  ExprProfileOverlay
    epov;                        
  /** if ShowPlotPopup is the parent, else null */
  ShowPlotPopup
    spp;        	
  /** link to scatterplot named overlay maps instance */
  OverlayMap
    olmap;  
  
  /** plot mode for spp */
  int
    plotMode2D;			 
  
  /** default canvas width */ 
  final static int
    MIN_CANVAS_WIDTH=  500; 
  /** default canvas height */ 
  final static int            
    MIN_CANVAS_HEIGHT= 550; 
  /** target Size  for selecting a line */
  final static int
    TARGET_SIZE= 2;	
  
  /** preferred canvas height */ 
  int
    preferredHeight= MIN_CANVAS_HEIGHT;
  /** preferred canvas width */ 
  int
    preferredWidth= MIN_CANVAS_WIDTH;
  /** working canvas width */ 
  private int
    iWidth;
  /** working canvas height */ 
  private int
    iHeight;
    
  /** x Data for computing ratios when clicking */
  float
    xDataScatterPlot[]; 
  /** y Data for computing ratios when clicking */
  float
    yDataScatterPlot[];
  /** x plot coordinate for clicking on point in list */
  int
    xPlotted[];
  /** y plot coordinate for clicking on point in list */
  int
    yPlotted[];
    
  /** viewport x1 for clipping divider */
  float
    vx1;
  /** viewport x2 for clipping divider */
  float
    vx2;
  /** viewport y1 for clipping divider */
  float
    vy1;
  /** viewport y2 for clipping divider */
  float
    vy2;
  /** clipped x1 for clipping divider */
  float
    cx1;
  /** clipped x2 for clipping divider */
  float
    cx2;
  /** clipped y1 for clipping divider */
  float
    cy1;
  /** clipped y2for clipping divider */
  float
    cy2;
    
  /** fraction precision reporting data */
  int
    cvPrecision;                 
  /** # points plotted. # points= #genes X #samples */
  int
    nPointsPlotted;              
    
  /** set to gene list being used */
  GeneList
    eventHandlerCL;              
  /** Graphics context used with displayPlot*/
  Graphics
    gScatterPlot;                
  
 /** flag: set if draw plot to GIF file*/
  boolean
    drawIntoImageFlag= false;  
  /** full Gif file path name */
  String
    oGifFileName;              

	    
  /* --- Arguments from constructor --- */
  /** ARGS: vertical caption */
  private String 
    vertCaption;
  /** ARGS: horizontal caption */
  private String                
    horizCaption;
  /** ARGS: horizontal caption */
  private String
    title;
  /** ARGS: */
  private String
    topTitle;
  /** ARGS: */
  private String
    title1;
  /** ARGS: */
  private String
    title2;
  /** ARGS: */
  private String
    title3;
  /** ARGS: */
  private String
    info1;
  /** ARGS: */
  private String
    info2;
  /** ARGS: */
  private String
    info3;
  /** ARGS: */
  private String
    info4;
  /** ARGS: */
  private String
    aText;
  /** copy of original aText disp below title3 */
  private String
    aTextOrig;                 
  
  /** integer regular font size */
  private int
   iFontSize;
  private int
    iFontHdrSize;
  /** regular font */
  private Font
    fontReg;
  
  /** -- */
  private int
    xText;
   /** -- */
   private int
    yText;
  
  /** scale factor X-axis */
  double 
    scaleX; 
  /** scale factor X-axis */
  double 
    scaleY;
  /** min X range set in initial call */
  double 
    minX;
  /** max X range set in initial call */
  double                      
    maxX;
  /** min Y range set in initial call */
  double
    minY;
  /** max X range set in initial call */
  double
    maxY;
  /** min X range set by ShowPlotPopup scrollers */
  double
    minXscroll;
  /** min Y range set by ShowPlotPopup scrollers */
  double
    minYscroll;
  /** max X range set by ShowPlotPopup scrollers */
  double
    maxXscroll;
  /** max Y range set by ShowPlotPopup scrollers */
  double
    maxYscroll;
    
  /** show grayed out non-filtered genes*/
  boolean 
    showNonFilteredGenesFlag; 
    
  /** flag: Linear(false)/Log(true) */
  private boolean 
    logScalingFlag; 
  /** flag: draw 45 degree line */
  private boolean            
    draw45lineFlag; 
  /** flag: draw 180 degree line */
  private boolean 
    draw180lineFlag; 
  /** flag: label all vert lines w/int # */
  private boolean 
    useXaxisIntNbrFlag;        
  
  /** slope for xList to xPnt */
  private float 
    xScale;   
  /** slope for yList to yPnt */
  private float
    yScale;
  /** baseline for xxList to xPnt */
  private float            
    xBase;
  /** baseline for yList to xyPnt */
  private float
    yBase;              
    
  /** ALL of actual X data points to draw  [0:nList-1] */
  float
    xList[];      
  /** ALL of actual Y data points to draw  [0:nList-1] */
  float
    yList[];
    
  /** X values for only EGL gene points [0:nGroup-1][0:maxGenes-1] */ 
  private int
    xLineList[][];             
  /** Y values for only EGL gene points [0:nGroup-1][0:maxGenes-1] */    
  private int
    yLineList[][];         
  
  /** map point # in [0:nGroup*maxGenes] to [0:maxGenes] */
  private int
    mapPntToMid[];              
    
  /** group # for EP [0:maxGenes-1] */
  private boolean
    jGrpList[];                 
  
  /** xList[] data mapped to int */
  private int 
    xPnt;
  /** yList[] data mapped to int */
  private int 
    yPnt;                
  /** property of data to draw */
  private int 
    propList[];                
  
  /** associated GeneList if not null */
  GeneList
    dispCL;                    
  
  /** max allowed # of EGL genes */
  int 
    maxLineList; 
  /** # elements in x(y)(p)List[0:nList-1] */
  int 
    nList; 
  /** # sequential points to connect group with line for use with EP 2D plot
   * # of lines/line-group 
   */
  int 
    nLineGroup;                
    

  /**
   * Draw2Dplot() - constructor
   * @param mae is the MAExplorer instance
   * @param plotMode is the 2D plot mode
   * @param spp is the ShowPlotPopup instance
   * @param epov is ExprProfileOverlay instance if not null
   * @param title is window title
   * @param nPoints is the # points= #genes X #samples
   * @param nLineGroup # of lines/line-group. 1 for scatter plot
   * @param useXaxisIntNbrFlag to label all vert lines w/int #
   */
  Draw2Dplot(MAExplorer mae, int plotMode, ShowPlotPopup spp,
             ExprProfileOverlay epov, String title, int nPoints,
             int nLineGroup, boolean useXaxisIntNbrFlag )
  { /* Draw2Dplot */
    super();
    
    this.mae= mae;
    this.plotMode2D= plotMode;
    this.spp= spp;
    this.epov= epov;
    this.title= title;
    this.useXaxisIntNbrFlag= useXaxisIntNbrFlag;
    
    ms= mae.ms;
    fc= mae.fc;
    cdb= mae.cdb;
    olmap= mae.olmap;
    
    showNonFilteredGenesFlag= false;
    logScalingFlag= false;          /* Linear(false)/Log(true) */
    cvPrecision= 3;
    
    eventHandlerCL= null;
    nPointsPlotted= 0;              /* # points plotted */
    
    if(plotMode2D==mae.PLOT_F1_F2_INTENS || plotMode2D==mae.PLOT_F1_F2_MVSA ||
       plotMode2D==mae.PLOT_HP_XY_INTENS || nPoints>0)
    { /* allocate it */
      if(nPoints<0)
        nPoints= mae.mp.maxGenes;
      xPlotted= new int[nPoints];
      yPlotted= new int[nPoints];
      xDataScatterPlot= new float[nPoints];
      yDataScatterPlot= new float[nPoints];
    }
    
    nLineGroup= (nLineGroup<=0) ? 1 : nLineGroup;
    this.nLineGroup= nLineGroup;
    if(nLineGroup>0)
    { /* allocate it */
      int maxLineList= mae.mp.maxGenes;
      xLineList= new int[nLineGroup][maxLineList]; /* sorted by gene*/
      yLineList= new int[nLineGroup][maxLineList];
      jGrpList= new boolean[maxLineList];   /* gen'ed sort list flag */
    }
    
    this.addMouseListener(this);
    this.addMouseMotionListener(this);
  } /* Draw2Dplot */
  
  
  /**
   * getPreferredSize() - get the preferred size
   * @return window size
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    iWidth= preferredWidth;
    iHeight= preferredHeight;
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
   * setViewport() - set the viewport (vx1,vx2,vy1,vy2) between two points.
   * @param vx1 is viewport x1 coordinate
   * @param vx2 is viewport x2 coordinate
   * @param vy1 is viewport y1 coordinate
   * @param vy2 is viewport y2 coordinate
   */
  void setViewport(float vx1, float vx2, float vy1, float vy2)
  { /* setViewport */
    this.vx1= vx1;
    this.vx2= vx2;
    this.vy1= vy1;
    this.vy2= vy2;
  } /* setViewport */
  
  
  /**
   * setDataToClip() - set (cx1,cx2,cy1,cy2) data to clip from((x1,x2,y1,y2)
   * @param x1 is coordinate
   * @param x2 is coordinate
   * @param y1 is coordinate
   * @param y2 is coordinate
   */
  void setDataToClip(float x1, float x2, float y1, float y2)
  { /* setDataToClip */
    this.cx1= x1;
    this.cx2= x2;
    this.cy1= y1;
    this.cy2= y2;
  } /* setDataToClip */
  
  
  /**
   * clip_code_viewport(x,y) - Return the clipping code of where (x,y) is
   * w.r.t. the DPORT Viewport clipping window.
   * See Newman & Sproull (1979) pp 65-68 for discussion of algorithm.
   * @param x is coordinate
   * @param y is coordinate
   * @return clipping code
   */
  private int clipCodeViewport(float x, float y)
  { /* clipCodeViewport */
    int code= 0;
    
    if(x<vx1)
      code |= 01;
    else if(x>vx2)
      code |= 02;
    if(y<vy1)
      code |= 04;
    else if(y>vy2)
      code |= 010;
    
    return(code);
  }/* clipCodeViewport */
  
  
  /**
   * clipViewport(x1,x2,y1,y2) - Compute if (x1,x2,y1,y2) is inside of Viewport window.
   * Copy initial data to (x1,x2,y1,y2) to  (cx1,cx2,cy1,cy2).
   * If not recompute (cx1,cx2, cy1, cy2) such that it is.
   * If both points are outside of the window, then it can not
   * be divided so return false else true.
   * See Newman & Sproull (1979) pp 65-68 for discussion of algorithm.
   * @param x1 is coordinate
   * @param x2 is coordinate
   * @param y1 is coordinate
   * @param y2 is coordinate
   * @return true if clipped and data is in (cx1,cx2,cy1,cy2).
   * @see #setDataToClip
   * @see #clipCodeViewport
   */
  boolean clipViewport(float x1, float x2, float y1, float y2)
  { /* clipViewport */
    int
      c1,
      c2,
      c;
    float
      x= 0.0F,
      y= 0.0F;
    
    setDataToClip(x1, x2, y1, y2);    /* copy to (c x1,cx2, cy1, cy2) */
    
    c1= clipCodeViewport(cx1,cy1);
    c2= clipCodeViewport(cx2,cy2);
    
    while ((c1!=0) || (c2!=0))
    { /* Clipping divider */
      if (( c1*c2)!=0)
        return(false);             /* can not divide */
      
      c= c1;
      if (c==0)
        c= c2;
      
      if ((c&01)!=0)
      { /* crosses left edge */
        if (cx1==cx2)
          y= cy1;
        else y= cy1+(cy2-cy1)*(vx1-cx1)/(cx2-cx1);
        x= vx1;
      } /* crosses left edge */
      
      else if ((c&02)!=0)
      { /* crosses right edge */
        if (cx1==cx2)
          y= cy1;
        else y= cy1+(cy2-cy1)*(vx2-cx1)/(cx2-cx1);
        x= vx2;
      } /* crosses right edge */
      
      else if ((c&04)!=0)
      {  /* crosses bottom edge */
        if (cy1==cy2)
          x= cx1;
        else x= cx1+(cx2-cx1)*(vy1-cy1)/(cy2-cy1);
        y= vy1;
      } /* crosses bottom edge */
      
      else if ((c&010)!=0)
      { /* crosses top edge */
        if (cy1==cy2)
          x= cx1;
        else x= cx1+(cx2-cx1)*(vy2-cy1)/(cy2-cy1);
        y= vy2;
      } /* crosses top edge */
      
      if (c==c1)
      { /* check it again */
        cx1= x;
        cy1= y;
        c1= clipCodeViewport(x,y);
      } /* check it again */
      
      if (c==c2)
      { /* check it again */
        cx2= x;
        cy2= y;
        c2= clipCodeViewport(x,y);
      } /* check it again */
    } /* Clipping divider */
    
    return(true);
  } /* clipViewport */
  
  
  /**
   * paint() - draw the 2D plot.
   * @param g is graphics context
   * @see #draw2Dplot
   */
  public void paint(Graphics g)
  { /* paint */
    draw2Dplot(g);
  } /* paint */
  
  
  /**
   * drawPlus() - draw plus sign as overlay graphic in specified color.
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param color is color to draw
   */
  final private void drawPlus(Graphics g, int x, int y, Color color)
  { /* drawPlus */
    int w= (mae.presentViewFlag) ? 3 : 2;  /* i.e. 7x7  else 5x5 */
    
    g.setColor( color );
    g.drawLine( x, y-w, x, y+w );
    g.drawLine( x-w, y, x+w, y );
  } /* drawPlus */
  
  
  /**
   * drawCircle() - draw a circle as overlay graphic in specified color.
   * The color is optional and if null it draws it in green (blue).
   * if mae.useDichromasyFlag then use blue.
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param radius of object
   * @param color is color to draw
   */
  final private void drawCircle(Graphics g, int xC, int yC, int radius,
                                Color color)
  { /* drawCircle */
    if(color==null)
      color= (mae.useDichromasyFlag) ? Color.blue : Color.green;
    if(radius==0)
      radius= 6;
    int thickness= (mae.presentViewFlag) ? 3 : 2;
      
    g.setColor( color);
    for(int t=1;t<=thickness;t++)
      g.drawArc(xC-radius-1, yC-radius-1,
                2*radius+t, 2*radius+t, 0, 360 );
  } /* drawCircle */
  
  
  /**
   * drawSquare() - draw a square as overlay graphic in specified color.
   * The color is optional and if null it draws it in green (blue).
   * if useDichomasyFlag then use blue.
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param radius of object
   * @param color is color to draw
   */
  final private void drawSquare(Graphics g, int xC, int yC,
                                int radius, Color color)
  { /* drawSquare */
    if(color==null)
      color= (mae.useDichromasyFlag) ? Color.blue : Color.green;
    if(radius==0)
      radius= 5;
    int
      height= 2*radius+1,
      width= height;
      
    g.setColor( color);
    g.drawRect( xC-radius, yC-radius, width, height);
    if(mae.presentViewFlag)
      g.drawRect( xC-radius, yC-radius, width+1, height+1);
  } /* drawSquare */
  
  
  /**
   * drawRectangleBin() - draw rectangle (histogram) bin as overlay graphic
   * in specified color.
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param width of object
   * @param height of object
   * @param color is color to draw
   */
  final private void drawRectangleBin(Graphics g, int x, int y,
  int width, int height, Color color)
  { /* drawRectangleBin */
    g.setColor( color );
    g.drawLine( x, y, x+width, y );
    g.drawLine( x, y-height, x+width, y-height );
    g.drawLine( x, y, x, y-height );
    g.drawLine( x+width, y, x+width, y-height );
  } /* drawRectangleBin */
  
  
  /**
   * drawFilledGrayCircle() - draw filled circle of specified grayvalue
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param radius of object
   * @param grayvalue is gray value color to draw
   */
  final private void drawFilledGrayCircle(Graphics g, int x, int y,
                                          int radius, int grayValue)
  { /* drawFilledGrayCircle */
    int
      gray= 255-grayValue,   /* reverse gray */
      r2= radius/2,
      xC= x-r2,
      yC= y-r2;
    //gray= Math.max(0,Math.min(gray,255));
    
    if(gray<0)
      gray= 0;
    else if(gray>255)
      gray= 255;           /* Math.max(0,Math.min(red,255)) */
    
    Color color= new Color(gray,gray,gray);
    
    g.setColor( color );
    g.fillOval( xC, yC, radius, radius);
  } /* drawFilledGrayCircle */
  
  
  /**
   * drawFilledColoredCircle() - draw filled circle of specified  RGB color
   * @param g is graphics context
   * @param x is center of object
   * @param y is center of object
   * @param radius of object
   * @param red is red component of color
   * @param green is green component of color
   * @param blue is blue component of color
   */
  final private void drawFilledColoredCircle(Graphics g, int x, int y,
  int radius,
  int red, int green, int blue)
  { /* drawFilledColoredCircle */
    int
      r2= radius/2,
      xC= x-r2,
      yC= y-r2;
    
    if(red>255)
      red= 255;           /* Math.max(0,Math.min(red,255)) */
    if(green>255)
      green= 255;         /* Math.max(0,Math.min(green,255)) */
    if(blue>255)
      blue= 255;          /* Math.max(0,Math.min(blue,255)) */
    Color color= new Color(red, green, blue);
    
    g.setColor( color );
    g.fillOval( xC, yC, radius, radius);
  } /* drawFilledGrayCircle */
  
  
  /**
   * update2Dplot() - set data for plot and then update the plot.
   * This is generally called through the PopupRegistry through the parent
   * class.
   * @param vertCaption vertical caption
   * @param horizCaption is horizontal caption
   * @param title is window titloe
   * @param topTitle is top title in plot
   * @param title1 is next top title in plot
   * @param title2 is next top title in plot
   * @param title3 is next top title in plot
   * @param aText is bottom title in plot
   * @param xText is bottom title in plot
   * @param yText is bottom title in plot
   * @param scaleX is global scaling
   * @param scaleY is global scaling
   * @param minX is min possible X
   * @param maxX is max possible X range
   * @param minY is min possible Y
   * @param maxY is max possible Y range
   * @param draw45lineFlag
   * @param draw180lineFlag
   * @param xList[] is list of X axis data points
   * @param yList is list of Y axis data points
   * @param propList is gene property list fo points (optional)
   * @param dispCL is the display GeneList if not null
   * @param nList is the # entries in List[]'s
   * @param mapPntToMid is the expression profile overlay nHP*nGenes MIDs mapping
   * @see Draw2Dplot#paint
   * @see #repaint
   */
  void update2Dplot(String vertCaption, String horizCaption, String title,
                    String topTitle,  String title1, String title2,
                    String title3, String aText, int xText, int yText,
                    double scaleX, double scaleY, double minX, double maxX,
                    double minY, double maxY, boolean draw45lineFlag,
                    boolean draw180lineFlag, float xList[], float yList[],
                    int propList[], GeneList dispCL, int nList,
                    int mapPntToMid[])
  { /* update2Dplot */
    this.vertCaption= vertCaption;
    this.horizCaption= horizCaption;
    this.title= title;
    this.topTitle= topTitle;
    this.title1= title1;
    this.title2= title2;
    this.title3= title3;
    this.aText= aText;
    aTextOrig= aText;                /* in case want to redo it */
    this.xText= xText;
    this.yText= yText;
    this.scaleX= scaleX;
    this.scaleY= scaleY;
    this.minX= minX;
    this.maxX= maxX;
    this.minY= minY;
    this.maxY= maxY;
    this.draw45lineFlag= draw45lineFlag;
    this.draw180lineFlag= draw180lineFlag;
    this.xList= xList;
    this.yList= yList;
    this.propList= propList;   /* if it is not null */
    this.dispCL= dispCL;
    this.nList= nList;
    this.mapPntToMid= mapPntToMid;
    
    /* To be used to pick out region to plot */
    minXscroll= minX;
    minYscroll= minY;
    maxXscroll= maxX;
    maxYscroll= maxY;
    
    repaint();
  } /* update2Dplot */
  
  
  /**
   * updateScrolledRegion() - update scrolled region to plot.
   * @param x1Pos in range 0.0:1.0
   * @param x2Pos in range 0.0:1.0
   * @param y1Pos in range 0.0:1.0
   * @param y2Pos in range 0.0:1.0
   * @see Draw2Dplot#paint
   */
  void updateScrolledRegion(float x1Pos, float x2Pos, float y1Pos, float y2Pos )
  { /* updateScrolledRegion */
    /* Map from [0:max(X/Y)] */
    
    /* Map from [min(X/Y):max(X/Y)] */
    minXscroll= (maxX-minX)*x1Pos+minX;
    maxXscroll= (maxX-minX)*x2Pos+minX;
    minYscroll= (maxY-minY)*y1Pos+minY;
    maxYscroll= (maxY-minY)*y2Pos+minY;
   /*
    if(mae.CONSOLE_FLAG)
      System.out.println("D2D-USR XYSR=["+(int)minXscroll+":"+
                         (int)maxXscroll+", "+
                         (int)minYscroll+":"+(int)maxYscroll);
    */
    repaint();
  } /* updateScrolledRegion */
  
  
  /**
   * set2DplotSubtitles() - set info1,2,3,4 subtitles for plot.
   * Do not repaint.
   * @param info1 is extra subtitles
   * @param info2 is extra subtitles
   * @param info3 is extra subtitles
   * @param info4 is extra subtitles
   */
  void set2DplotSubtitles(String info1, String info2, String info3, String info4)
  { /* set2DplotSubtitles */
    this.info1= info1;
    this.info2= info2;
    this.info3= info3;
    this.info4= info4;
  } /* set2DplotSubtitles */
  
  
  /**
   * setFontsFromState() - set the regular fonts from the state variables
   */
  private void setFontsFromState()
  { /* setFontsFromState */
    iFontSize= ((mae.rptFontSize.equals("12pt")
                  ? 12
                  : ((mae.rptFontSize.equals("8pt")
                       ? 8
                       : 10))));
    fontReg= new Font(mae.cfg.fontFamily, Font.PLAIN, iFontSize+2);
  } /* setFontsFromState */
  
  
  /**
   * drawNamedOverlayMaps() try to draw overlay maps if there are any 
   * and they are enabled 
   * @param g is graphics context
   */
  private void drawNamedOverlayMaps(Graphics g,
                                    float scaleXplot, float scaleYplot,
                                    double minXscroll,double minYscroll,
                                    int xOffset, int yOffset)
  { /* drawNamedOverlayMaps */
    if(olmap.nOverlayMaps==0)
      return;
    OverlayMap om;
    float 
      xL, yL,       
      xyMap[];
    char 
      overlayChars[],
      ch;
    String str;
    int 
      i, j,
      x, y, 
      x0= 0,
      y0= 0,
      n,
      nPoints,
      overlayType;       /* 0 for 'o', 1 for continuous line between 
                         * points, 2 for square, 3 for overlayChars[0:nPoints] */
    Color overlayColor;
    for(n= 0; n<olmap.nOverlayMaps;n++)      
    { /* process n'throws overlay */
      om= olmap.overlayMapDB[n];
      if(!om.enableFlag)
        continue;
      xyMap= om.xyMap;
      nPoints= om.nPoints;
      overlayColor= om.overlayColor;
      overlayType= om.overlayType;    
      overlayChars= om.overlayChars;
      j= 0;
      for(i= 0;i<nPoints;i++)
      { /* draw all points in overlay */
        xL= xyMap[j++];
        yL= xyMap[j++];
        /* Now map to pixel space and display it.
         * Compute linear scaled coordinates.
         */
        x= (int)((scaleXplot *(xL-minXscroll)) + xOffset);
        y= yOffset - (int)(scaleYplot * (yL-minYscroll));
        if(i==0)
        { /* beginning point */
          x0= x;
          y0= y;
          g.setColor(overlayColor);
        }
        switch(overlayType)
        {
          case 1:         /* draw piecewise linear lines */
             g.drawLine( x0, y0, x, y );
             x0= x;
             y0= y;
            break;
          case 2:         /* draw small square */
            drawSquare(g, x, y, 3, overlayColor);
            break;
          case 3:         /* draw sn'throws character */
            str= (overlayChars==null) ? (""+n) : (""+overlayChars[i]);
            g.drawString(str, x, y);
            break;
          case 0:         /* draw small circle */
            drawCircle(g, x, y, 3, overlayColor);
          default:
            break;
        }
      } /* draw all points in overlay */
    } /* process n'throws overlay */
  } /* drawNamedOverlayMaps */
  
    
  /**
   * draw2Dplot() - draw a plot in the current graphics context.
   * @param g is graphics context
   * @see GeneList#isMIDinGeneList
   * @see Util#cvd2s
   * @see WriteGifEncoder
   * @see WriteGifEncoder#writeFile
   * @see #draw2Dplot
   * @see #drawCircle
   * @see #drawPlus
   * @see #drawSquare
   * @see #repaint
   */
  void draw2Dplot(Graphics g)
  { /* Draw2dplot */
    
    /* [1] Initialize variables */
    GeneList editedCL= mae.gct.editedCL;
    Gene geneOBJ= (cdb.isValidObjFlag && mae.mp.midStaticCL.mList[cdb.objMID]!=null)
                    ? mae.mp.midStaticCL.mList[cdb.objMID]
                    : null;
    int
      nEGL= editedCL.length,
      cNbrOBJ= (geneOBJ==null) ? 0 : geneOBJ.clusterNodeNbr;
    boolean
      isEGLgeneFlag,
      isCurGeneFlag;
    Color
      color= (mae.useDichromasyFlag) ? Color.orange : Color.red,
      curGeneColor= (mae.useDichromasyFlag) ? Color.blue : Color.green,
      eglColor= (mae.useDichromasyFlag) ? Color.magenta : Color.magenta,
      clusterColor= (mae.useDichromasyFlag) ? Color.blue : Color.green,
      okColor= (mae.useDichromasyFlag) ? Color.orange : Color.red,
      pointColor;         /* color basic point is drawn in */
    
    int
     x1,x2, y1,y2,       /* for drawing line segments */
     yRange0= 256,       /* actual plot area */
     xRange0= 256,       /* actual plot area */
     leftTextX= 20,
     xOffset= 60,
     yOffset= 325,
     scale= 256,
     plotStyleMode= (epov==null) ? 0 : epov.plotStyleMode;
    float
      yRange= (float)(maxYscroll-minYscroll),
      xRange= (float)(maxXscroll-minXscroll),
      scaleXplot= (float)(scaleX*scale/xRange),
      scaleYplot= (float)(scaleY*scale/yRange),
      logScaleX= 0,                      /* only used with Log scaling */
      logScaleY= 0;
    String
      sMinY= Util.cvd2s(minYscroll,cvPrecision), /* default Linear value */
      sMaxY= Util.cvd2s(maxYscroll,cvPrecision),
      sMinX= Util.cvd2s(minXscroll,cvPrecision),
      sMaxX= Util.cvd2s(maxXscroll,cvPrecision);
    double
      lMinY,
      lMaxY,
      lMinX,
      lMaxX;
    Image gifImage= null;
    
    /* Setup params for doing Log transform */
    /* [DEPRECATE] */
    /*
    if(logScalingFlag)
    {
      lMinY= MathMAE.logZero(minYscroll);
      lMaxY= MathMAE.logZero(maxYscroll);
      lMinX= MathMAE.logZero(minXscroll);
      lMaxX= MathMAE.logZero(maxXscroll);
      sMinY= Util.cvd2s(lMinY,2);
      sMaxY= Util.cvd2s(lMaxY,2);
      sMinX= Util.cvd2s(lMinX,2);
      sMaxX= Util.cvd2s(lMaxX,2);
      logScaleX= (float)(scaleXplot * (maxXscroll-minXscroll) /
                         MathMAE.logZero((double)(maxXscroll-minXscroll)));
      logScaleY= (float)(scaleYplot * (maxYscroll-minYscroll) /
                         MathMAE.logZero((double)(maxYscroll-minYscroll)));
    }
       */
    
    /* [1.1] If draw plot into GIF image file, setup new Graphics g */
    if(drawIntoImageFlag)
    { /* draw into GIF file Image instead of canvas */
      int
        w= iWidth,
        h= iHeight;
      gifImage= createImage(w,h);
      g= gifImage.getGraphics();
    }
    
    /* [1.2] Redefine the regular 'fontReg' fonts */
    setFontsFromState();
    g.setFont(fontReg);
    
    /* [2] Draw titles, lables etc. */
    eventHandlerCL= dispCL;   /* if not null */
    gScatterPlot= g;          /* save for later */
    
    if(epov!=null)
      this.setBackground( Color.lightGray );  /* clear screen */
    else
      this.setBackground( Color.white );  /* clear screen */
    g.clearRect(0,0,iWidth,iHeight);
    g.setColor( Color.black );
    
    int
      midY,
      yTop= yOffset-(int)(scaleYplot*yRange);
    
    if(yTop<0)
      yTop= 0;                      /* Math.max(0, yTop)*/
    g.drawLine( xOffset,yTop, xOffset,yOffset+10);      /* Yaxis */
    
    /* Draw tick mark 1/2 down Yaxis */
    midY= (yTop+yOffset+10)/2;
    if(!useXaxisIntNbrFlag)
      g.drawLine( xOffset-7,midY, xOffset,midY);
    
    int
      midX,
      xRight= xOffset+(int)(scaleXplot*xRange);
    xRight= Math.min((iWidth-1), xRight);
    g.drawLine( xOffset-10,yOffset, xRight,yOffset);    /* Xaxis */
    
    /* Draw tick mark 1/2 down Xaxis */
    midX= (xOffset-10+xRight)/2;
    if(!useXaxisIntNbrFlag)
      g.drawLine( midX,yOffset+7,midX,yOffset);
    
    /* [2.1] Draw titles */
    String
      vCapt= (logScalingFlag) ? "ln "+vertCaption : vertCaption,
      hCapt= (logScalingFlag) ? "ln "+horizCaption : horizCaption;
    g.drawString(vCapt, 5, yOffset/2);                 /* vert title */
    g.drawString(hCapt, (int)(xOffset+(3*scaleXplot*xRange)/5),
                 yOffset+25);                          /* horiz title */
    
    if(topTitle==null)
      g.drawString(title, xOffset-30, yOffset+45);     /* main title */
    else
    { /* draw topTitle and 2 lower titles (1,2) */
      if(topTitle!=null && info3==null)
        g.drawString(topTitle, xOffset-30, yTop-30);  /* topTitle */
      if(title1!=null)
        g.drawString(title1, xOffset+10, yOffset+45); /* title1 */
      if(title2!=null)
        g.drawString(title2, xOffset+10, yOffset+60); /* title2 */
      if(title3!=null)
        g.drawString(title3, xOffset+10, yOffset+75); /* title3 */
      if(aTextOrig!=null)
        g.drawString("  "+aTextOrig, xOffset+10, yOffset+90); /* aTextOrig */
    }
    
    /* [2.2] Draw info1, info2, info3, info4 */
    if(info1!=null)
      g.drawString(info1, xOffset-30, yTop-60);         /* info1 */
    if(info2!=null)
      g.drawString(info2, xOffset-30, yTop-45);         /* info2 */
    if(info3!=null)
      g.drawString(info3, xOffset-30, yTop-30);         /* info3 */
    if(info4!=null)
      g.drawString(info4, xOffset-30, yTop-15);         /* info4 */
    
    
    /* [2.3] Draw the scale values. These may be changed by scrollers */
    g.drawString(sMinY, leftTextX, yOffset+5);
    g.drawString(sMaxY, leftTextX, yTop-5);
    if(!useXaxisIntNbrFlag)
    { /* draw range over entire set */
      g.drawString(sMinX, xOffset+5, yOffset+15);
      g.drawString(sMaxX, xRight-5, yOffset+15);
    }
    else
    { /* draw #'s for each X sample */
      for(int k=0;k<nLineGroup;k++)
      { /* draw all sample #s */
        int xS= (int)((k*1.0/(nLineGroup-1)) *
                      (scaleXplot*(maxXscroll-minXscroll)) + xOffset);
        g.drawString((""+(k+1)), xS, yOffset+15);
      }
    } /* draw #'s for each X sample */
    
    nPointsPlotted= nList;
    Gene mList[]= dispCL.mList;
    int
      x,
      y,
      prop,
      nPasses= (!showNonFilteredGenesFlag || nLineGroup>1)
                  ? 1 : 2,        /* draw active on 2nd pass */
      xCurGene= -1,               /* default is NOT to draw it */
      yCurGene= -1;

    /* [3] Draw primary list which is in the visible window */
    float
      xL,
      yL;
    Gene gene;
    int
      mid,
      iMid,               /* set to mid numbered entry */
      iCurMid= -1;        /* set to current gene iMid if defined */
    
    /* [3.1] Compute scale factors so they range over actual
     * drawing region
     */
    xScale= 1.0F;          /* slope for x(y)List to x(y)Pnt */
    yScale= 1.0F;
    xBase= 0.0F;           /* baseline for x(y)List to x(y)Pnt */
    yBase= 0.0F;    
    
    if(nLineGroup>1)
      for(int e=0;e<maxLineList;e++)
        jGrpList[e]= false;     /* reset flags */
    
   /* Note: possibly 2 passes. First pass draws in red, 2nd in
    * overlay color.
    */
    for(int pass=1;pass<=nPasses;pass++)
      for(int i=0;i<nList;i++)
      { /* draw the point list */
        xL= xList[i];
        yL= yList[i];
        
        if(maxYscroll<yL || yL<minYscroll ||
           maxXscroll<xL || xL<minXscroll)
          continue;           /* ignore points outside of the range */
        
        gene= mList[i];
        if(gene==null)
          continue;
        prop= propList[i];  /* get gene properties */
        
        if((prop & Gene.C_BAD_SPOT)!=0)
          continue;         /* ignore bogus spots */
      
        mid= gene.mid;
        isCurGeneFlag= (cdb.isValidObjFlag && cdb.objMID==mid);        
        
        /* [3.1] Test if allowed to draw point and set color. */
        if(!showNonFilteredGenesFlag)
        { /* show Filtered genes from propList[] */
          pointColor= okColor;  /* default is red (orange) '+'*/
          if((prop & Gene.C_IS_NOT_FILTERED)!=0)
          { /* ignore non-filtered genes */
            /* disable clickable point list for event handler */
            xPlotted[i]= -1;
            yPlotted[i]= -1;
            continue;
          }
        } /* show Filtered genes from propList[] */
        
        else
        { /* show non-Filtered genes from propList[] */
          if(pass==2 && (prop & Gene.C_IS_NOT_FILTERED)!=0)
            continue;  /* don't replot non-filtered on 2nd pass */
          
          pointColor= (((prop & Gene.C_IS_FILTERED)!=0)
                         ? okColor  /* red (orange) */
                         : (((prop & Gene.C_IS_NOT_FILTERED)!=0)
                             ? Color.gray
                             : (((prop & Gene.C_BAD_DATA)!=0)
                                  ? Color.cyan
                                  : okColor  /* red (orange) */)));
        } /* show non-Filtered genes from propList[] */
        
        /* [3.1.1] Compute actual scaled coordinates */
        /* [DEPRECATE] - demide if we will NEVER use log()
         * mappings since handle it in Normalization and then
         * remove code, else leave in case we change our mind.
         */
        /*
        if(logScalingFlag)
        {
          x= (int)(logScaleX * MathMAE.logZero((xL-minXscroll)) + xOffset);
          y= yOffset - (int)(logScaleY * MathMAE.logZero((yL-minYscroll)));
        }
        else
         */
        { /* compute linear scaled coordinates */
          x= (int)((scaleXplot *(xL-minXscroll)) + xOffset);
          y= yOffset - (int)(scaleYplot * (yL-minYscroll));
        }
        
       /* [3.2] Handle current gene with green overlays.
       * OK for scatter plot, but not for overlay plot!
       */
        if(isCurGeneFlag && iCurMid==-1)
        { /* current gene - push cur-gene (x,y) coords */
          iCurMid= (mapPntToMid==null) ? i : mapPntToMid[i];
          xCurGene= x;
          yCurGene= y;
        } /* current gene - push cur-gene (x,y) coords */
        
        
       /* [3.3] Capture line coordinates for all genes in
        * EP overlay plot. Map all [i] to iMid for 1st gene
        * in list, after that don't bother drawing it.
        */
        if(pass==1 && nLineGroup>1 && !jGrpList[mapPntToMid[i]])
        { /* push gene (x,y) coords as build xyLineList[] */
          /* Search coord lists for all instances of this gene
           * and push into sorted list. Then mark jGrpList[i]
           * as done so don't recompute later.
           */
          iMid= mapPntToMid[i];
          for(int jG= 0;jG<nLineGroup;jG++)
          { /* Push next nLine points for this gene into list */
            /* compute linear scaled coordinates for each segment */
            int
              k= jG+i,
              xc= (int)((scaleXplot*(xList[k]-minXscroll)) + xOffset),
              yc= yOffset - (int)(scaleYplot*(yList[k]-minYscroll));
            
            xLineList[jG][iMid]= xc;
            yLineList[jG][iMid]= yc;
            
            /*
            if(mae.CONSOLE_FLAG && i<5)
              System.out.println("D2DP-P[3.3] i="+i+" k="+k+
                                 " xc="+xc+" yc="+yc+" mid="+gene.mid+
                                 " iMid="+iMid+" jG="+jG);
            */
            
            /* Draw point in scatter plot or EP overlay plot */
            drawPlus(g, xc, yc, pointColor);
          } /* Push all points with this gene into list */
          
          jGrpList[iMid]= true;
        } /* push gene (x,y) coords as build xyLineList[] */
        
        /* [3.3.1] Handle Kmeans-clustering with special green(blue)
         * numbered overlays.
         */
        if(mae.useKmeansClusterCntsDispFlag && cdb.isValidObjFlag)
        { /* draw Kmeans cluster number in GREEN (BLUE if useDichromasy) */
          int cNbr= gene.clusterNodeNbr;
          if(cNbr>0 && cNbr==cNbrOBJ)
          {
            String sName= (""+cNbr);
            g.setColor(clusterColor);
            g.drawString(sName, x-2, y+3);
          }
          else
            drawPlus(g, x,y, pointColor);     /* default is red '+'*/
        } /* draw Kmeans cluster number in GREEN (BLUE if useDichromasy */
        
        
        /* [3.3.2] Draw point in scatter plot or EP overlay plot */
        else
        { /* Draw point in scatter plot or EP overlay plot */
          drawPlus(g, x, y, pointColor);
        }
        
        /* [3.4] Setup clickable point xy list for event handler */
        if(dispCL!=null)
        { /* setup the clickable point list for the event handler */
          xPlotted[i]= x; /* test of coords in E.H.*/
          yPlotted[i]= y;
          
          /* data for event handler */
          xPnt= (int)(xScale*xList[i]+xBase);
          yPnt= (int)(yScale*yList[i]+yBase);
          xDataScatterPlot[i]= xPnt;   /* data for event handler */
          yDataScatterPlot[i]= yPnt;
        }
        else
        { /* disable clickable point list for the event handler */
          xPlotted[i]= -1;
          yPlotted[i]= -1;
        }
      } /* draw the point list */
    
    /* [4] If we are enabled to draw the 'edited gene list'
     * boxes, then overlay genes from the E.G.L which are ALSO in the
     * point list. If we are using the EGL line EP overlays, then
     * draw magenta +'s but not the boxes.
     */
    if(mae.showEGLflag && nEGL>0)
    { /* display any E.G.L. genes in current list */
      int j;
      
      for(int i=0;i<nList;i++)
      { /* draw the edited gene list point list */
        gene= mList[i];     /* test i'th gene of workingCL
                             * against j'th E.C.L gene */
        if(gene==null)
          continue;
        isEGLgeneFlag= editedCL.isMIDinGeneList(gene.mid);
        if(isEGLgeneFlag && xPlotted[i]!=-1)
        { /* EGL gene - draw magenta square */
          x= xPlotted[i];
          y= yPlotted[i];
          if(epov==null || !epov.sepp.showEGLlinesFlag)
            drawSquare(g, x, y, 5, eglColor);
          drawPlus(g, x, y, eglColor);
        }
      } /* draw the edited gene list point list */
    } /* display any E.G.L. genes in current list */
    
   /* [5] Draw EP overlay lines at end so on top. Color different
    * genes with different properties different colors.
    * This pass, draw all genes which pass filter.
    */
    if(nLineGroup>1 && epov.sepp.showFilteredLinesFlag)
    { /* draw EP overlay lines in overlay plot */
      for(int n=0;n<nList;n++)
      { /* draw all genes that pass test */
        iMid= mapPntToMid[n];
        gene= mList[n];
        if(!jGrpList[iMid] || gene==null)
          continue;
        else
          jGrpList[iMid]= false; /* Mark it as drawn */
        
        prop= gene.properties;
        if(showNonFilteredGenesFlag &&
        (prop & Gene.C_IS_NOT_FILTERED)!=0)
          continue;             /* don't draw non-filtered genes */
        
        pointColor= (((prop & Gene.C_IS_FILTERED)!=0)
                       ? okColor  /* red (orange) */
                       : (((prop & Gene.C_IS_NOT_FILTERED)!=0)
                            ? Color.gray
                            : (((prop & Gene.C_BAD_DATA)!=0)
                                 ? Color.cyan
                                 : okColor  /* red (orange) */)));
        g.setColor(pointColor);
        for(int jc=0;jc<(nLineGroup-1);jc++)
        { /* connect N-1 points to draw line for each gene */
          x1= xLineList[jc][iMid];
          y1= yLineList[jc][iMid];
          x2= xLineList[jc+1][iMid];
          y2= yLineList[jc+1][iMid];
          
          /* Connect the lines: for n points, n-1 lines. */
          if(((jc+1)<nLineGroup) /* && plotStyleMode==2 */ )
            g.drawLine( x1, y1, x2, y2);
        } /* connect N-1 points to draw line for each gene */
      } /* draw all genes that pass test */
    } /* draw EP overlay lines in overlay plot */
    
    /* [5.1] Draw current gene and all EGL genes as
     * EP overlay lines at end so on top.
     */
    if(nLineGroup>1 && (nEGL>0 || iCurMid!=-1))
    { /* draw EGL EP overlay lines in overlay plot */
      g.setColor(eglColor);
      for(int n=0;n<nList;n++)
      { /* draw all genes that pass test */
        iMid= mapPntToMid[n];        
        gene= mList[n];
        if(!jGrpList[iMid] || gene==null)
          continue;
        else
          jGrpList[iMid]= false; /* Mark it as drawn */
        
        prop= gene.properties;
        mid= gene.mid;
        isEGLgeneFlag= editedCL.isMIDinGeneList(mid);
        
       /* If show EGL genes, then draw if they exist and they
        * pass the filter.
        */
        if(!epov.sepp.showEGLlinesFlag || !isEGLgeneFlag ||
           ((prop & Gene.C_IS_NOT_FILTERED)!=0))
          continue;         /* don't draw non-filtered genes */
        
        
        for(int jc=0;jc<(nLineGroup-1);jc++)
        { /* connect N-1 points to draw line for each gene */
          x1= xLineList[jc][iMid];
          y1= yLineList[jc][iMid];
          x2= xLineList[jc+1][iMid];
          y2= yLineList[jc+1][iMid];
          
          /* Connect the lines: for n points, n-1 lines. */
          drawPlus(g, x1, y1, eglColor);
          if(((jc+1)<nLineGroup) /* && plotStyleMode==2 */ )
            g.drawLine( x1, y1, x2, y2);
          else
            drawPlus(g, x2, y2, eglColor);
        } /* connect N-1 points to draw line for each gene */
      } /* draw all genes in EGL */
    } /* draw EGL EP overlay lines in overlay plot */
    
    /* [5.2] Draw green (blue) current gene circle(s) at end so on TOP */
    /* [TODO] epov.plotStyleMode
     *    0= vert lines, 1= circle points, 2= continuous curves.
     */
    if(xCurGene!=-1)
    { /* draw current gene - draw green circle and '+'*/
      if(nLineGroup==1)
      { /* for scatter plot */
        drawPlus(g, xCurGene, yCurGene, curGeneColor);
        drawCircle(g, xCurGene, yCurGene, 5, curGeneColor);
      } /* for scatter plot */
      
      else
      { /* draw cur. gene on each vert line of EP overlay plot */
        for(int jc=0;jc<(nLineGroup-1);jc++)
        { /* for EP overlay plot */
          x1= xLineList[jc][iCurMid];
          y1= yLineList[jc][iCurMid];
          x2= xLineList[jc+1][iCurMid];
          y2= yLineList[jc+1][iCurMid];
          
          /* Connect the lines: for n points, n-1 lines. */
          drawPlus(g, x1, y1, curGeneColor);
          drawCircle(g, x1, y1, 5, curGeneColor);
          if(((jc+1)<nLineGroup) /* && plotStyleMode==2 */ )
            g.drawLine( x1, y1, x2, y2);
          else
          {
            drawPlus(g, x2, y2, curGeneColor);
            drawCircle(g, x2, y2, 5, curGeneColor);
          }
        } /* for EP overlay plot */
      } /* draw cur. gene on each vert line of EP overlay plot */
    } /* draw current gene - draw green circle and '+'*/
    
    /* [6] Draw the on-plot scale lines if required */
    g.setColor( Color.black );
    
    /* [TODO] fix 45 degree line - how should it be drawn?
     * Preserve it as zoom around. Also then will be able
     * to draw the fold-change curves.
     */
    if(mae.NEVER && draw45lineFlag)
    { /* draw 45 degree line in the plot which has slope 1.0 */
      float
        dXscroll= (float)(maxXscroll-minXscroll),
        dYscroll= (float)(maxYscroll-minYscroll),
        scrollSlope= (dYscroll/dXscroll),            /* real scroll space*/
        fullSlope= (float)((maxY-minY)/(maxX-minX)); /* real max space*/
      int
        xp1= xOffset,                           /* viewport pixel space */
        xp2= (xOffset+xRange0),
        yp1= yOffset,
        yp2= (yOffset-yRange0),
        xc1= (int)((scaleXplot*(0-minXscroll)) + xOffset),
        yc1= yOffset - (int)(scaleYplot*(0-minYscroll)),
        xc2= (int)((scaleXplot*dXscroll) + xOffset),
        yc2= yOffset - (int)(scaleYplot*dYscroll);
      /*
      if(mae.CONSOLE_FLAG)
        System.out.println("D2DP-P[6] minX="+Util.cvd2s(minX,3)+
                         " maxX="+Util.cvd2s(maxX,3)+
                         " minY="+Util.cvd2s(minY,3)+
                         " maxY="+Util.cvd2s(maxY,3)+
                         "\n minXscroll="+Util.cvd2s(minXscroll,3)+
                         " minYscroll="+Util.cvd2s(minYscroll,3)+
                         "\n maxXscroll="+Util.cvd2s(maxXscroll,3)+
                         " maxYscroll="+Util.cvd2s(maxYscroll,3)+
                         "\n scrollSlope="+Util.cvd2s(scrollSlope,3)+
                         " fullSlope="+Util.cvd2s(fullSlope,3)+
                         "\n (xc1:xc2, yc1:yc2)=("+xc1+":"+xc2+
                         ", "+yc1+":"+yc2+")");
      */
       
      /* [TODO] Need to do cliping divider to plot in viewport.
       * Use setViewport(... TODO ...)
       * and clipViewport(x1,x2,y1,y2).
       * This computes if (x1,x2,y1,y2) is inside of the Viewport window.
       * Copy initial data to (x1,x2,y1,y2) to  (cx1,cx2,cy1,cy2).
       * If not recompute (cx1,cx2, cy1, cy2) such that it is.
       * If both points are outside of the window, then it can not
       * be divided so return false else true and the clipped data
       * is in (cx1,cx2,cy1,cy2).
       */
      
      /*
      if(xc1<xOffset)
        xc1= xOffset;
      if(yc1>yOffset)
        yc1= yOffset;
     
      if(xc2>(xOffset+xRange0))
        xc2= (xOffset+xRange0);
      if(yc2<(yOffset-yRange0))
        yc2= (yOffset-yRange0);
      */
      
      g.drawLine(xc1,yc1,xc2,yc2);
    } /* draw 45 degree line in the plot which has slope 1.0 */
    
    if(draw180lineFlag)
      g.drawLine((xOffset-1), (yTop+yOffset/2),  xRange0, (yTop+yOffset/2));
    
    /* [7] Draw additional text if required */
    if(aText!=null)
    { /* split into successive lines */
      if(xText==0)
      { /* put to right of graph 1/3 way down plot */
        xText= xRight+5;
        yText= (yTop-5) + (yTop-yOffset)/3;
      }
      g.drawString(aText,xText, yText);
    }
    
    /* [8] Try to draw overlay maps if there are any and they are enabled */
    if(olmap.nOverlayMaps>0)
      drawNamedOverlayMaps(g,scaleXplot, scaleYplot, minXscroll, minYscroll,
                           xOffset, yOffset);
    
    /* [9] If drawing to a GIF file, then cvt Image to Gif stream
     * and write it out.
     */
    if(drawIntoImageFlag && gifImage!=null)
    { /* write it out */
      drawIntoImageFlag= false;
      WriteGifEncoder
      wge= new WriteGifEncoder(gifImage);
      gifImage= null;
      if(wge!=null)
        wge.writeFile(oGifFileName);
      
      repaint();   /* refresh the actual canvas */
    } /* write it out */
  } /* draw2Dplot */
  
  
  
  /**
   * updateScaling() - set scaling mode in proper sub-object.
   * Linear(false)/Log(true) [DEPRICATED]
   * @param logScalingFlag to do log scaling if set
   * @param showNonFilteredGenesFlag if set
   * @see #repaint
   */
  void updateScaling(boolean logScalingFlag,
                     boolean showNonFilteredGenesFlag)
  { /* updateScaling */
    this.logScalingFlag= logScalingFlag;
    this.showNonFilteredGenesFlag= showNonFilteredGenesFlag;
    
    repaint();
  } /* updateScaling */
  
  
  /**
   * mousePressed() - process mouse event, change current gene.
   * @param e is mouse pressed event
   * @see DrawScatterPlot#mouseHandler
   * @see ExprProfileOverlay#mouseHandler
   */
  public void mousePressed(MouseEvent e)
  { /* mousePressed */
    if(spp!=null)
      spp.dwSP.mouseHandler(e.getX(), e.getY(), e.getModifiers(), true);
    else if(epov!=null)
      epov.mouseHandler(e.getX(), e.getY(), e.getModifiers(), true);
  } /* mousePressed */
  
  
  /**
   * mouseMoved() - process mouse event - update MouseOver
   * @param e is mouse moved event
   * @see DrawScatterPlot#mouseHandler
   * @see ExprProfileOverlay#mouseHandler
   */
  public void mouseMoved(MouseEvent e)
  { /* mouseMoved */
    if(!mae.useMouseOverFlag)
      return;
    
    if(spp!=null)
      spp.dwSP.mouseHandler(e.getX(), e.getY(), e.getModifiers(), false);
    else if(epov!=null)
      epov.mouseHandler(e.getX(), e.getY(), e.getModifiers(), false);
  } /* mouseMoved */
  
  
  public void mouseClicked(MouseEvent e)  { }
  public void mouseEntered(MouseEvent e)  { }
  public void mouseExited(MouseEvent e)  { }
  public void mouseReleased(MouseEvent e)  { }
  public void mouseDragged(MouseEvent e)  { }
  
  
} /* end of class Draw2Dplot */



