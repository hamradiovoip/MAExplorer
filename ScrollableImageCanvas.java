/** File: ScrollableImageCanvas.java */


import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.util.*;
import java.util.EventListener;
import java.lang.*;
import java.io.*;

/**
 * This class supports a scrollable image canvas for the pseudoarray image. 
 * The scrollbars live in ArrayScroller whose event
 * handler calls the ScrollableImageCanvas. In additionl, the mouse event 
 * handler finds the current gene if the xy coords are close to any gene 
 * and then invokes updates of other windows through the PopupRegistry.
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
 * @version $Date: 2003/11/24 21:18:54 $   $Revision: 1.21 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ArrayScroller
 * @see DrawPseudoImage
 */
 
class ScrollableImageCanvas extends Canvas implements
     MouseListener, MouseMotionListener
{
  /** link to global instance of MAExplorer */
  private MAExplorer 
    mae;                        
  /** link to global instance of Maps */
  private Maps
    map;                        
  /** link to global instance of CompositeDatabase */
  private CompositeDatabase
    cdb;                        
  /** link to global instance of Config */
  private Config
    cfg;                        
  /** link to global instance of Filter  */
  private Filter
    fc;                         
  /** link to current sample */                    
  MaHybridSample
    msR;  
  /** link to global instance of GeneClass */                       
  private GeneClass
    gct; 
  
  /** Parent class */ 
  ArrayScroller
    is;	
  /** name of ArrayScroller parent */
  private String
    isName;		                     
  
  /** link to ms.xyCQ[] coordinates of spots */
  private Point
    xyCQ[]; 
  /** target Size */
  final static int
    tSize= 7;			
  /** max # of text items  */  
  final static int
    MAX_TEXT= 52;		
  /** either MARKER_PLUS or MARKER_CIRCLE */ 
  private int
    markerShape;                
  /** extra pixels for circles */
  private int
    addPixels= 1;
  
  /** + base address of ULHC of clipped image in 
   * image canvas used in last paint */
  private int
    xBase= 0,			
    yBase= 0;
  private int
  /** + image address (x,y)+(xBase,yBase) */
    xImg= 0,			
    yImg= 0;
  /** eventHndlr set (x,y) object position*/
  private int
    xObj= 0,			
    yObj= 0;
  private int
    xRelMap= 0,
    yRelMap= 0;
  
  public int
    preferredHeight= MAExplorer.canvasVSize,
    preferredWidth= MAExplorer.canvasHSize;   
  
  /** size of array area within canvas */
  int
    iWidth= 0,                 
    iHeight= 0;
  
   /** status area MaHPquantTable.X_SPOTQ_OFFSET */ 
  int
    extraWidth;               
  /** status area MaHPquantTable.Y_SPOTQ_OFFSET */ 
  int
    extraHeight;      
  
  /** pImage: iWidth+extraWidth of the actual canvas */
  private int	
    scWidth;
  /** pImage: iHeight+extraHeight of the actual canvas */
  private int	                
    scHeight;                  
	   
  /** displayed HP MouseOver */
  private boolean
    showedHPmouseOverFlag;
  /** displayed Gene MouseOver */
  private boolean
    showedGeneMouseOverFlag;   
  /** CTRL/Mouse */
  private boolean
    ctrlMod;
  /** SHIFT/Mouse */
  private boolean
    shiftMod;	
  
  /** set if draw clustergram to GIF file */
  private boolean
    drawIntoImageFlag;     
  /** GIF output file if used */
  private String
    oGifFileName;
  
  /** object color  */
  Color
    objColor= Color.yellow;
  /** cross-hairs color  */
  Color
    targetColor= Color.cyan;   
 
  /** small char font - 12pt */
  Font 
    smallFont;
  /** small char font - 12pt */
  Font
    mediumFont; 
    
  /** current point (xObj,yObj) on event*/
  Point
    xyObj; 
  /** mousePressed record */
  Point                
    fromPoint;
  /** mouseDragged record */
  Point          
    toPoint;                    
		 
			 
				 
  /**
   * ScrollableImageCanvas() - constructor for scrollable pseudoarray image canvas.
   * @param ms is the MaHybridSample instance
   * @param preferredWidth is the mae.canvasHSize
   * @param preferredHeight is the mae.canvasVSize
   * @param is is the ArrayScroller instance
   */
  ScrollableImageCanvas(MaHybridSample ms, int preferredWidth,
                        int preferredHeight, ArrayScroller is)
  { /* ScrollableImageCanvas */
    this.msR= ms;
    this.preferredWidth= preferredWidth;
    this.preferredHeight= preferredHeight;
    this.is= is;
    
    isName= is.name;
    mae= is.mae;
    map= mae.mp;
    fc= mae.fc;
    cdb= mae.cdb;
    cfg= mae.cfg;
    gct= mae.gct;
    
    smallFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 12); /* was 10*/
    mediumFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 14);
    
    /* Set the pseudo image the size.*/
    iHeight= preferredHeight;  /* mae.pWidth or mae.canvasHSize */
    iWidth= preferredWidth;    /* mae.pHeight or mae.canvasVSize */
    
    /* Compute pseudo image canvas size as (array area) + (status area) */
    extraWidth= MaHPquantTable.X_SPOTQ_OFFSET;  /* LEFT status area */
    extraHeight= MaHPquantTable.Y_SPOTQ_OFFSET; /* RIGHT status area */
    scWidth= iWidth + extraWidth;
    scHeight= iHeight + extraHeight;
    
    //markerShape= mae.MARKER_PLUS;
    markerShape= mae.MARKER_CIRCLE;
    
    xyObj= new Point(0,0);
    fromPoint= new Point(0,0);     /* for future rubber-banding*/
    toPoint= new Point(0,0);
    showedHPmouseOverFlag= false;      /* displayed HP MouseOver */
    showedGeneMouseOverFlag= false;   /* displayed Gene MouseOver */
  } /* ScrollableImageCanvas */
  
  
  /**
   * drawGifFile() - draw pseudoimage into Gif image file if in standalone mode.
   * This sets it up and lets paint() to the heavy lifting...
   * @param oGifFileName is the GIF output file full path for doing SaveAs GIF
   * @return false if unable to generate the image file.
   * @see #repaint
   */
  boolean drawGifFile(String oGifFileName)
  { /* drawGifFile */
    if(mae.isAppletFlag || oGifFileName==null)
      return(false);
    
    drawIntoImageFlag= true;
    this.oGifFileName= oGifFileName;
    repaint();                          /* will start the process */
    
    return(true);
  } /* drawGifFile */
  
  
  /**
   * syncCanvasAccess() - update canvas access
   * @see #repaint
   */
  void syncCanvasAccess()
  { /* syncCanvasAccess */
    msR= mae.ms;
    if(mae.mReady)
      repaint();
  } /* syncCanvasAccess */
  
  
  /**
   * getPreferredSize() - get the preferred size
   * @return size of the canvas
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(new Dimension(preferredHeight, preferredWidth));
  } /* getPreferredSize */
  
  
  /**
   * getMinimumSize() - get the minimum current size of the canvas size
   * @return size of the canvas
   */
  public Dimension getMinimumSize()
  { /* getMinimumSize */
    return(new Dimension(MAExplorer.canvasVSize, MAExplorer.canvasHSize));
  } /* getMinimumSize */
  
  
  /**
   * setObjState() - change the object position
   * @param ms is the sample to use for current Obj
   * @param xObj is the current X coordinate of the object
   * @param yObj is the current Y coordinate of the object
   */
  void setObjState(MaHybridSample ms, int xObj, int yObj)
  {
    msR= ms;
    this.xObj= xObj;
    this.yObj= yObj;
  }
  
  
  /**
   * setImgPos() - set the Image position.
   * Don't repaint here.
   * Note: used by ArrayScroller when move scroll bars.
   * @param ms is the sample to use for current Obj
   * @param xObj is the current X coordinate of the object
   * @param yObj is the current Y coordinate of the object
   * @see ArrayScroller#setScrollBar
   * @see #repaint
   */
  void setImgPos(MaHybridSample ms, int xImg, int yImg)
  { /* setImgPos */
    msR= ms;
    this.xImg= xImg;
    this.yImg= yImg;
    
    /* Map (xImg,yImg) to the scrollbar cursors positions.
     * Then force it to recenter the image cursor at the  (xImg, yImg)
     * by repainting it.
     */
    /* [BUG] recalculate correctly */
    int
      cWidth= getSize().width,         /* canvas size */
      cHeight= getSize().height,
      xB= -(xImg - cWidth/2),	         /* MUST BE < 0!!! */
      yB= -(yImg - cHeight/2),
      dWidth= - (scWidth - cWidth),
      dHeight= - (scHeight - cHeight),
      hMax= is.hs.getMaximum(),
      vMax= is.vs.getMaximum(),
      hsVal= (xB * hMax) / dWidth,     /* will be 0-100% */
      vsVal= (yB * vMax) / dHeight;

    /* Move scrollbars to the mapped (xImg,yImg) and thus the image
     * viewed in the canvas.
     */
    is.setScrollBar(hsVal,vsVal);
    
    repaint();		/* move image */
  } /* setImgPos*/
  
  
  /**
   * getImgPos() - get the Image position (xImg,yImg).
   * @return (xImg,yImg) point
   */
  Point getImgPos()
  { /* getImgPos */
    Point xyImg= new Point(xImg, yImg);
    
    return(xyImg);
  } /* getImgPos */
  
  
  /**
   * setObjPos() - set the object position (xObj,yObj).
   * Don't repaint here...
   * @param xObj is the current X coordinate of the object
   * @param yObj is the current Y coordinate of the object
   */
  void setObjPos(int xObj, int yObj)
  { /* setObjPos */
    this.xObj= xObj;
    this.yObj= yObj;
  } /* setObjPos */
  
  
  /**
   * getObjPos() - get the object position (xObj,yObj).
   * @return (xObj,yObj) point
   */
  Point getObjPos()
  { /* getObjPos */
    Point xyObj= new Point(xObj, yObj);
    return(xyObj);
  } /* getObjPos */
  
  
  /**
   * drawSquareAroundSpot() - draw a square around spot if visible.
   * The color is optional and if null it draws it in Blue.
   * @param xyo point to draw
   * @param g is graphics context
   * @param optColor to use, else Blue
   * @param useRadius is radius to use. If <0, use mae.spotRad/2
   * @see #drawGeneList
   */
  private void drawSquareAroundSpot(Point xyo, Graphics g, Color optColor,
  int useRadius)
  { /* drawSquareAroundSpot */
    Color color;
    if(optColor==null)
      color= Color.blue;
    else color= optColor;
    
    if(xyo.x>0 && xyo.y>0)
    { /* draw current object */
      int
       radius= (useRadius>0) ? useRadius : mae.spotRad/2,
       height= 2*radius+1,
       width= height,
       xC= xyo.x - xBase -radius,
       yC= xyo.y - yBase -radius,
       xS= getSize().width,
       yS= getSize().height;

      /* Draw it only if in clipped canvas and text is not null  */
      if(xC>0 && xC<xS && yC>0 && yC<yS)
      { /* only draw if inside of canvas */
        g.setColor( color);
        g.drawRect( xC, yC, width, height);
        if(mae.presentViewFlag)
          g.drawRect( xC, yC, width+1, height+1);  /* thickness of 2 */
      }
    } /* draw current object */
  } /* drawSquareAroundSpot */
  
  
  /**
   * drawCircleAroundSpot() - draw a circle around spot if visible.
   * The color is optional and if null it draws it in green.
   * If the thickness is 0, it is set to 1.
   * @param xyo point to draw
   * @param g is graphics context
   * @param optColor to use, else Blue
   * @param extraPixels to extend the default radius (mae.spotRad/2)
   * @param thickness (0,1,2) of the line
   * @see #drawGeneList
   */
  private void drawCircleAroundSpot(Point xyo, Graphics g, Color optColor,
  int extraPixels, int thickness)
  { /* drawCircleAroundSpot */
    Color color;
    if(optColor==null)
      color= Color.green;
    else color= optColor;
    if(thickness==0)
      thickness= 1;
    
    if(xyo.x>0 && xyo.y>0)
    { /* draw current object */
      int
        radius= extraPixels + mae.spotRad/2,
        xC= xyo.x - xBase,
        yC= xyo.y - yBase,
        xS= getSize().width,
        yS= getSize().height;
      
      /* Draw it only if in clipped canvas and text is not null  */
      if(xC>0 && xC<xS && yC>0 && yC<yS)
      { /* only draw if inside of canvas */
        g.setColor( color);
        for(int t=1;t<=thickness;t++)
          g.drawArc( xC-radius-1, yC-radius-1, 2*radius+t, 2*radius+t, 0, 360 );
      }
    } /* draw current object */
  } /* drawCircleAroundSpot */
  
  
  /**
   * drawCircleAroundSpot() - draw circle & title around spot if visible.
   * Draw the title to the right of the circle
   * The color is optional and if null it draws it in green (blue).
   * If the thickness is 0, it is set to 1.
   * @param xyo point to draw
   * @param g is graphics context
   * @param optColor to use, else Blue
   * @param extraPixels to extend the default radius (mae.spotRad/2)
   * @param thickness (0,1,2) of the line
   * @param sTitle is title to label circle
   * @see #drawGeneList
   */
  private void drawCircleAroundSpot(Point xyo, Graphics g, Color optColor,
  int extraPixels, int thickness,
  String sTitle )
  { /* drawCircleAroundSpot */
    Color color;
    if(optColor==null)
      color= (mae.useDichromasyFlag) ? Color.blue : Color.green;
    else
      color= optColor;
    if(thickness==0)
      thickness= 1;
    
    if(xyo.x>0 && xyo.y>0)
    { /* draw current object */
      int
        radius= extraPixels + mae.spotRad/2,
        xC= xyo.x - xBase,
        yC= xyo.y - yBase,
        xS= getSize().width,
        yS= getSize().height;
      
      /* Draw it only if in clipped canvas and text is not null  */
      if(xC>0 && xC<xS && yC>0 && yC<yS)
      { /* only draw if inside of canvas */
        g.setColor( color);
        for(int t=1;t<=thickness;t++)
          g.drawArc( xC-radius-1, yC-radius-1, 2*radius+t, 2*radius+t, 0, 360 );
        if(sTitle!=null)
          g.drawString(sTitle, xC+radius+5, yC+10);
      }
    } /* draw current object */
  } /* drawCircleAroundSpot */
  
  
  /**
   * drawPlustAtSpot() - draw a 7x7 + around spot if visible.
   * If presentationViewFlag is set then draw it larger 9 x 9.
   * Default to red
   * @param xyo point to draw
   * @param g is graphics context
   * @param optColor to use, else Red
   * @see #drawGeneList
   */
  private void drawPlustAtSpot(Point xyo, Graphics g, Color optColor)
  { /* drawPlustAtSpot */
    Color color= (optColor==null)
                    ? ((mae.useDichromasyFlag) ? Color.orange : Color.red)
                    : optColor;    
    int
      xC= xyo.x - xBase,
      yC= xyo.y - yBase,
      xS= getSize().width,
      yS= getSize().height;
    
    /* Draw it only if in clipped canvas and text is not null  */
    int w= (mae.presentViewFlag) ? 4 : 3;  /* i.e. 9x9 else 7x7 */
    
    if(xC>0 && xC<xS && yC>0 && yC<yS)
    { /* only draw if inside of canvas */
      int
      xDraw= xC,
      yDraw= yC;
      
      g.setColor( color );
      g.drawLine( xDraw, yDraw-w, xDraw, yDraw+w );
      g.drawLine( xDraw-w, yDraw, xDraw+w, yDraw );
    }
  } /* drawPlustAtSpot */
  
  
  /**
   * showImageMousePos() - show the mouse positions in the image.
   * Only report if debugging...
   * @param e is mouse event so we can get the data if needed
   * @param x is mouse X position
   * @param y is mouse Y position
   */
  private void showImageMousePos(MouseEvent e, String msg, int x, int y)
  { /* showImageMousePos */
    /*
    if(mae.CONSOLE_FLAG)
    {
    if(msg==null)
      msg= "";
    Util.showMsg("[" + msg + "] " + " x=" + x + ", y=" + y +
                (ctrlMod)
                  ? " CTRL" : (shiftMod ? " SHIFT" : "")) +
                " (x,y)Img= (" + xImg + "," + yImg + ")" +
                " (x,y)Base= (" + xBase + "," + yBase + ")" +
                " (x,y)Obj= (" + xObj + "," + yObj + ")");
    }
    */
  } /* showImageMousePos */
  
  
  /**
   * showMouseCoords() - show the mouse coordinates for DEBUGGING
   * @param ms is message
   * @param x is mouse X position
   * @param y is mouse Y position
   * @param imgNbr is the HP sample number
   */
  private void showMouseCoords(String msg, int x, int y, int imgNbr)
  { /* showMouseCoords */
    /*
    if(mae.CONSOLE_FLAG)
      Util.showMsg("[" + msg + "] (x,y)= (" + x+ "," + y +
                   ") (xRelMap,yRelMap)= (" + xRelMap + "," + yRelMap + ")");
    */
  } /* showMouseCoords */
  
  
  /**
   * mapRelXYtoImage() - map (x,y) Mouse in current canvas to (xImg,yImg)
   * as well as updating the parent (xImg,yImg) values.
   * This sets (ctrlMode, shiftMod, xImg, yImg and is.img_selectedFlag)
   * and (xObj, yObj, xyObj).
   * @param e is mouse event so we can get the key modfifiers
   * @param x is mouse X position
   * @param y is mouse Y position
   * @see #getHPnbrInImage
   */
  final private void mapRelXYtoImage(MouseEvent e, int x, int y)
  { /* mapRelXYtoImage */
    int modifiers= e.getModifiers();
    
    ctrlMod= ((modifiers & InputEvent.CTRL_MASK) != 0);
    shiftMod= ((modifiers & InputEvent.SHIFT_MASK) != 0);
    
    xImg= x + xBase;          /* absolute position in the image */
    yImg= y + yBase;
    
    is.img_selectedFlag= true; /* NOW force it to be defined. */
    xObj= xImg;
    yObj= yImg;
    xyObj.x= xObj;
    xyObj.y= yObj;
  } /* mapRelXYtoImage */
  
  
  /**
   * drawImageTitle() - draw title in image.
   */
  void drawImageTitle()
  { /* drawImageTitle */
    String
      oldTitle= is.title,
      sT= is.title + "  (" + xObj + ", " + yObj + ")" ;
    
    is.setText1(sT, true);
    is.title= oldTitle;
  } /* drawImageTitle */
  
  
  /**
   * getHPnbrInImage() - return HP # if within threshold distance
   * @param e is MouseEvent so can get modifiers and (x,y) event data
   * @return -1 if fail, 0 if [X] or [Y], and >=1 if entry.
   */
  private int getHPnbrInImage(MouseEvent e)
  { /* getHPnbrInImage */
    int
      iR= -1,                     /* return value */
      modifiers= e.getModifiers(),
      x= e.getX(),
      y= e.getY(),
      xImg= x + xBase,          /* absolute position in the image */
      yImg= y + yBase,    
      xHPdiff, yHPdiff,
      hpXdistThr= MaHPquantTable.X_SPOTQ_OFFSET-5,
      hpYdistThr= 5;
    
    Point xyHPlist[]= mae.dwPI.hpXYpseudoImgList;
    if(xyHPlist==null)
      return(-1);                /* there is no data */
    
    //mapRelXYtoImage(e,x,y);
    
    /* Test if over HP[i] on HP-E list in left of image */
    for(int i=0;i<=mae.hps.nHP;i++)
    { /* look for left end of HP-E[i] sample name */
      xHPdiff= Math.abs(xyHPlist[i].x - xImg);
      yHPdiff= Math.abs(xyHPlist[i].y - yImg);
      if(xHPdiff<=hpXdistThr && yHPdiff<=hpYdistThr)
      { /* found it, change the current HP-X */
        iR= i;
        break;
      }
    } /* look for left end of HP-E[i] sample name */
    
    return(iR);
  } /* getHPnbrInImage */
  
  
  /**
   * mousePressed() - handle mouse dragged. No-op for now.
   * @param e is MouseEvent so can get modifiers and (x,y) event data
   */
  public void mousePressed(MouseEvent e)
  { /* mousePressed */
    if(!mae.mReady)
      return;
    
    int
    x= e.getX(),
    y= e.getY();
    return;
  } /* mousePressed */
  
  
  /**
   * mouseDragged() - handle mouse dragged. No-op for now.
   * @param e is MouseEvent so can get modifiers and (x,y) event data
   */
  public void mouseDragged( MouseEvent e )
  { /* mouseDragged */
    if(!mae.mReady)
      return;
    
    int
    x= e.getX(),
    y= e.getY();
    
    return;
  } /* mouseDragged */
  
  
  /**
   * mouseReleased() - process mouse events for pseudoarray image.
   * It attempts to match the (x,y) position to active regions to change
   * the current HP-X or HP-Y  sample (also toggling the [X] and [Y]).
   * If is is successful, it redraws the Pseudoarray Image and reruns
   * the data Filter.
   *<P>
   * Otherwise, it attempts to latch onto a spot's (x,y) (+-spotRadius)
   * to redefine the current gene. If it matches a spot, it redefines
   * the current gene, updates gene information in status lines, runs the
   * data filter, updates (xImg,yImg) with the relative position in canvas
   * by adding (x,y) to paint ULHC base address (xBase,yBase) and repaints
   * the canvas.
   * @param e is MouseEvent so can get modifiers and (x,y) event data
   * @see CompositeDatabase#lookupHPcoords
   * @see Filter#computeWorkingGeneList
   * @see PopupRegistry#updateCurGene
   * @see SampleSets#setHPxyModStrings
   * @see SpotFeatures#showValidSpotInfo
   * @see #getHPnbrInImage
   * @see #repaint
   */
  public void mouseReleased(MouseEvent e)
  { /* mouseReleased */
    if(!mae.mReady ||  mae.maxPreloadImgs==0)
      return;
    
    int
    modifiers= e.getModifiers(),
    x= e.getX(),
    y= e.getY(),
    hpNbr;
    Point
    xyHPlist[]= mae.dwPI.hpXYpseudoImgList;
    DrawPseudoImage
    dwPI= mae.dwPI;
    
     /* Save the previous object state in case we do not change it
      * so that it may be restored.
      **/
    int
    oldXobj= xyObj.x, /* save real object if any */
    oldYobj= xyObj.y;
    boolean
    oldIsValidObjectFlag= cdb.isValidObjFlag; /* save real object status */
    mapRelXYtoImage(e,x,y);
    
    /* [1] Test if clicked on HP[i] on HP-E list in left of image */
    hpNbr= getHPnbrInImage(e);
    if(hpNbr==0)
    { /* toggle setting HP-X and HP-Y  active sample */
      dwPI.setHPXflag= !dwPI.setHPXflag;
      Util.showMsg("Set sample to HP-"+
      ((dwPI.setHPXflag) ? "X" : "Y"));
      Util.showFeatures("","");
      mae.updatePseudoImgFlag= true;
    }
    else if(hpNbr>=1)
    { /* change to sample hpNbr */
      mae.curHP= hpNbr;
      mae.ms= mae.hps.msList[hpNbr]; /* set the new one */
      mae.is.syncScrollerAccess();   /* update scroller & canvas
       * access to rg,po,ms */
      /* change HP-X sample to current sample */
      String
      sXY;
      if(dwPI.setHPXflag)
      {
        mae.msX= mae.ms;
        mae.curHP_X= hpNbr;
        mae.curHP= hpNbr;
        sXY= "X";
      }
      else
      {
        mae.msY= mae.ms;
        mae.curHP_Y= hpNbr;
        mae.curHP= hpNbr;
        sXY= "Y";
      }
       /* Setup mae.hps.(sMod, sModX, sModY) based on whether
        * using ratio mode or swapCy5Cy3DataFlags
        */
      mae.hps.setHPxyModStrings();
      
      mae.updatePseudoImgFlag= true;
      Util.showMsg("Changed HP-"+sXY+" to ["+mae.ms.hpName+"]"+
      mae.hps.sModXY);
    } /* change to sample hpNbr */
    
    if(mae.updatePseudoImgFlag)
    { /* update pseudo image */
      
      /* restore Object to previous object before clicked */
      xyObj.x= oldXobj; /* restore real object if any */
      xyObj.y= oldYobj;
      cdb.isValidObjFlag= oldIsValidObjectFlag;
      
      //mae.cdb.hpXYdata.setupDataStruct(mae.useHPxySetDataFlag);
      fc.computeWorkingGeneList();
      mae.repaint();
      repaint();
      
      return;
    } /* found it, change the current HP-X */
    
    /*
    if(mae.CONSOLE_FLAG)
      showImageMousePos(e, "is.mouseReleased", x, y);
    */
    
    /* [2] Test if clicked on spot in image */
    toPoint= new Point(xObj,yObj); /* save coordinates [FUTURE]*/
    xyObj= toPoint;
    
    /* Optimize spot position only for original image */
    String s= cdb.lookupHPcoords(xObj,yObj,mae.spotRad,true, mae.ms);
    
    if(cdb.isValidObjFlag)
    { /* only update other interested parties if data is valid */
      xObj= cdb.objX;    /* data is valid */
      yObj= cdb.objY;
    }
    else
    { /* restore Object to previous object before clicked */
      xyObj.x= oldXobj; /* restore real object if any */
      xyObj.y= oldYobj;
      cdb.isValidObjFlag= oldIsValidObjectFlag;
    }
    
    if(cdb.isValidObjFlag)
    { /* if still valid, then refresh it */
      /* Note: popup registry will repaint this window AFTER
       * it recomputes the working gene list with the new data values.
       */
      mae.pur.updateCurGene(cdb.objMID, modifiers, null);
      mae.sf.showValidSpotInfo(xyObj,mae.ms);
    }
    
    repaint();
  } /* mouseReleased */
  
  
  /**
   * mouseClicked() - handle mouse Click events
   * @param e is MouseEvent so can get modifiers and (x,y) event data
   */
  public void mouseClicked(MouseEvent e)
  { /* mouseClicked */
  } /* mouseClicked */
  
  
  /**
   * mouseMoved() - handle mouse Move events to update the mouseover display
   * of the gene that is near the mouse.
   * @param e is MouseEvent so can get modifiers and (x,y) event data
   * @see CompositeDatabase#lookupHPcoords
   * @see SampleSets#setHPxyModStrings
   * @see SpotFeatures#showValidSpotInfo
   * @see #getHPnbrInImage
   */
  public void mouseMoved(MouseEvent e)
  { /* mouseMoved */
    if(!mae.mReady || mae.maxPreloadImgs==0 || cdb==null)
      return;
    
    int
      oldXobj= xyObj.x, /* save real object if any */
      oldYobj= xyObj.y,
      oldGID = cdb.objGID,
      x= e.getX(),
      y= e.getY();
    mapRelXYtoImage(e,x,y); /* get xyObj for mouseover only! */
    
    if(mae.useMouseOverFlag)
    { /* test if move mouse over HP entry or spot */
      int hpNbr= getHPnbrInImage(e); /* Test if clicked on HP[] entry
                                      * HP-E list in left of image */
      /* Setup mae.hps.(sMod, sModX, sModY) based on whether
       * using ratio mode or swapCy5Cy3DataFlags
       */
      mae.hps.setHPxyModStrings();
      if(hpNbr>0)
      { /* display full data for sample */
        MaHybridSample  msP= mae.hps.msList[hpNbr];
        Util.showFeatures("Sample #"+hpNbr+" "+msP.sampleID,
        msP.fullStageText+mae.hps.sModXY);
        showedHPmouseOverFlag= true;
        xyObj.x= oldXobj;  /* restore real object if any */
        xyObj.y= oldYobj;
        cdb.objGID = oldGID;
        return;
      } /* display full data for Sample  */
      
      /* Test if clicked on spot in image */
      Point  xyMouseOver= new Point(xObj,yObj); /* save coordinates [FUTURE]*/
      /* lookup spot position */
      String s= cdb.lookupHPcoords(xObj,yObj,mae.spotRad,true, mae.ms);
      if(s.length()>0)
      { /* display Gene MouseOver */
        mae.sf.showValidSpotInfo(xyMouseOver,mae.ms);
        showedGeneMouseOverFlag= true;
      }
    } /* test if move mouse over HP entry or spot */
    
    xyObj.x= oldXobj;  /* restore real object if any */
    xyObj.y= oldYobj;
    cdb.objGID = oldGID;
  } /* mouseMoved */
  
  
  /**
   * mouseEntered() - handle mouse Enter events
   * @param e is MouseEvent so can get modifiers and (x,y) event data
   */
  public void mouseEntered(MouseEvent e)
  { /* mouseEntered */
  } /* mouseEntered */
  
  
  /**
   * mouseExited() - handle mouse Exit events
   * @param e is MouseEvent so can get modifiers and (x,y) event data
   */
  public void mouseExited(MouseEvent e)
  { /* mouseExited */
    if(mae.useMouseOverFlag &&
    (showedHPmouseOverFlag || showedGeneMouseOverFlag))
    {
      Util.showFeatures("","");
      showedHPmouseOverFlag= false;     /* displayed HP balloon */
      showedGeneMouseOverFlag= false;  /* displayed Gene balloon */
    }
  } /* mouseExited */
  
  
  /**
   * update() - update, without background, the scrollable canvas
   * @param g is graphics to update if ready to draw.
   * @see #repaint
   */
  public void update(Graphics g)
  { /* update */
    if(!mae.mReady)
      return;
    paint(g);
  } /* update */
  
  
  /**
   * zoomImage() - zoom the image in the canvas.
   * [DISABLED] Fake a zoom by drawing zoomed image into restricted canvas
   * @param g is graphics to update if ready to draw.
   * @param im  is the image to draw into
   * @param xS center of point to zoom into or out of
   * @param yS center of point to zoom into or out of
   * @param cWidth of region to zoom
   * @param cHeight of region to zoom
   */
  private void zoomImage(Graphics g, Image im, int xS, int yS,
                         int cWidth, int cHeight)
  { /* zoomImage */
    if(false /* OLD WAY */)
    {
      int
        zWidth= scWidth*is.magnification,
        zHeight= scHeight*is.magnification;
      g.drawImage(im, xS, yS, zWidth, zHeight, this);
    }
    else
    { /* NEW WAY */
      int
        halfIwidth= (cWidth/2)/is.magnification,
        halfIheight= (cHeight/2)/is.magnification,
        sx1= xObj-halfIwidth,
        sx2= xObj+halfIwidth,
        sy1= yObj-halfIheight,
        sy2= yObj+halfIheight,
        dx1= 0,
        dx2= cWidth,
        dy1= 0,
        dy2= cHeight;
      g.drawImage(im,                 /* image to draw in */
                  dx1, dy1, dx2, dy2, /* DESTINATION */
                  sx1, sy1, sx2, sy2, /* SOURCE */
                  Color.white,        /* background color */
                  this);
    } /* NEW WAY */
  } /* zoomImage */
  
  
  /**
   * drawGeneList() - draw a gene list on either SaveAsGif or Pseudoarray Image.
   * The marker bits determine what to draw.
   *<BR>
   * If MARKER_GENES is set, then draw each spot in the gene
   * list in Red(white) MARKER_PLUS, MARKER_CIRCLE or MARKER_SQUARE,
   * MARKER_KMEANS_CLUSTER, or NONE if 0 or MARKER_NONE.
   *<BR>
   * If using PLOT_PSEUDO_F1F2_IMAGE || PLOT_PSEUDO_HP_XY_IMAGE
   * then draw it in white since the background is black.
   *<BR>
   * If the MARKER_CURRENT is set then draw it as a Green circle.
   * if Gang mode is enabled, draw it on both spots.
   * @param g is graphics context
   * @param ml is the gene list to use for overlay graphics
   * @param markerBits is the MARKER_xxxx symbol(s) to use
   * @see #drawCircleAroundSpot
   * @see #drawPlustAtSpot
   * @see #drawSquareAroundSpot
   */
  private void drawGeneList(Graphics g, GeneList ml, int markerBits )
  { /* drawGeneList */
    int
      maxFields= cfg.maxFIELDS,
      thickness= (mae.presentViewFlag) ? 3 : 2,
      extraPixels= 0,        /* used for special pValue display */
      xLblOffset= (mae.presentViewFlag) ? 3 : 2,
      yLblOffset= (mae.presentViewFlag) ? 5 : 3,
      shapeBits= ((markerBits & (mae.MARKER_PLUS | mae.MARKER_CIRCLE |
                                 mae.MARKER_SQUARE |
                                 mae.MARKER_KMEANS_CLUSTER))==0)
                     ? mae.MARKER_PLUS : markerBits,
      nGenes= ml.length;
    Font useFont= (mae.presentViewFlag) ? mediumFont : smallFont;
    Color
      filteredColor,        /* Filtered spot color */
      curGeneColor,         /* current gene color */
      curClustColor;        /* current cluster color */
    
    if(mae.plotImageMode==mae.PLOT_PSEUDOIMG)
    { /* overlay a grayscale image on light cyan bkgrd */
      filteredColor= (mae.useDichromasyFlag) ? Color.orange : Color.red;
      curGeneColor= (mae.useDichromasyFlag) ? Color.blue : Color.green;
      curClustColor= (mae.useDichromasyFlag) ? Color.blue : Color.green;
    }
    else if(mae.plotImageMode==mae.PLOT_PSEUDO_HP_XY_P_VALUE_IMG)
    { /* overlay a spectrum image image on light cyan bkgrd */
      filteredColor= Color.black;
      curGeneColor= Color.black;
      curClustColor= Color.magenta;
      extraPixels= 1;
    }
    else if(mae.plotImageMode==mae.PLOT_PSEUDO_HP_EP_CV_VALUE_IMG)
    { /* overlay a spectrum image on light cyan bkgrd */
      filteredColor= Color.black;
      curGeneColor= Color.black;
      curClustColor= Color.magenta;
      extraPixels= 1;
    }
    else
    { /* overlay a pseudocolor ratio image on black bkgrd */
      filteredColor= Color.white;
      curGeneColor= Color.yellow;
      curClustColor= Color.yellow;
    }
    
    Point xyList[]= xyCQ;             /* index by gid */
    
    if((markerBits & mae.MARKER_NONE)!=0 || markerBits==0)
      return;                     /* No overlay */
    
    /* [1] HERE is where we map the genes to gid  and
     * then to xy and draw it as  filteredColor !
     */
    Gene mList[]= ml.mList;
    if((markerBits & mae.MARKER_GENES)!=0)
      for(int i=0; i<nGenes; i++)
      { /* NOW get the (x,y) coords and draw points */
        Gene geneI= mList[i];
        if(geneI==null)
          continue;
        int gid= geneI.gid;
        if(gid==-1)
          continue;
        
        for(int j=1;j<=maxFields;j++)
        { /* do f1, or f1 and f2 */
          if(j==2)
          { /* remap F1 gid to F2 gid */
            GridCoords
            gc= map.gid2fgrc[gid];
            gid= map.fgrc2gid[2][gc.g][gc.r][gc.c];
          }
          Point p= xyList[gid];
          if(p==null)
            continue;
          
          if(mae.CANVAS_WRAPAROUND_BUG)
          { // debug code to look at (x,y) data from last grid
            GridCoords grc= mae.mp.gid2fgrc[gid];
            int
              grcG= grc.g,
              grcR= grc.r,
              grcC= grc.c;
            if((grcG==(mae.cfg.maxGRIDS/2) || grcG>=(mae.cfg.maxGRIDS-1)) && grcC==1)
              System.out.println("SIC-DEGL sc(Wth,Hght)=("+
                                 scWidth+","+scHeight+
                                 ") pref.(Wdth,Hght)=("+
                                 preferredWidth+","+preferredHeight+
                                 ") p[G,R,C])["+grcG+","+grcR+","+grcC+
                                 "]=("+ p.x+","+p.y+")");
          }
          
          if((shapeBits & mae.MARKER_PLUS)!=0)
            drawPlustAtSpot(p, g, filteredColor);
          if((shapeBits & mae.MARKER_CIRCLE)!=0)
            drawCircleAroundSpot(p, g, filteredColor, 1+extraPixels, 1);
          if((shapeBits & mae.MARKER_SQUARE)!=0)
            drawSquareAroundSpot(p, g, filteredColor, 0);
          
          if(mae.useKmeansClusterCntsDispFlag &&
          (shapeBits & mae.MARKER_KMEANS_CLUSTER)!=0)
          { /* draw K-means cluster number in curClustColor */
            int mid= map.gid2mid[gid];
            Gene
              geneOBJ= map.midStaticCL.mList[cdb.objMID],
              gene= map.midStaticCL.mList[mid];
            if(gene==null)
            {
              drawPlustAtSpot(p, g, filteredColor);
            }
            else
            { /* test the current gene */
              int
                cNbrOBJ= geneOBJ.clusterNodeNbr,
                cNbr= gene.clusterNodeNbr;
              if(cdb.isValidObjFlag && cNbr>0 && cNbr==cNbrOBJ)
              {
                String sName= (""+cNbr);
                g.setColor(curClustColor);
                
                g.setFont(useFont);   /* change to proper font */
                g.drawString(sName, p.x-xLblOffset, p.y+yLblOffset);
                /* change back to larger font? */
              }
              else
                drawPlustAtSpot(p, g, filteredColor);
            } /* test the current gene */
          } /* draw K-means cluster number in curClustColor */
        } /* do f1, or f1 and f2 */
      } /* draw points */
    
    /* [2] Draw circle around current Gene spot (spots if Gang) as
     * curGeneColor.
     */
    if(cdb.isValidObjFlag && (markerBits & mae.MARKER_CURRENT)!=0 &&
       (xyObj.x!=0 || xyObj.y!=0))
    { /* draw circle around current gene */
      drawCircleAroundSpot(xyCQ[cdb.objGID], g, curGeneColor,
                           addPixels+extraPixels, thickness+1);
      if(mae.gangSpotFlag)
      {
        Point pGang= xyCQ[map.gidToGangGid[cdb.objGID]];
        drawCircleAroundSpot(pGang, g, curGeneColor, addPixels+extraPixels,
                             thickness+1);
      }
    } /* draw circle around current gene */
  } /* drawGeneList */
  
  
  /**
   * drawGeneClustersOfCurrentGene() - draw genes in clusters as colored circles or squares
   * @see #drawCircleAroundSpot
   * @see #drawSquareAroundSpot
   */
  private void drawGeneClustersOfCurrentGene(Graphics g)
  { /* drawGeneClustersOfCurrentGene */
    if(//g.clusterDistThr==0 ||
       ClusterGenes.curClusterCL==null)
      return;
    int
      mid,
      thickness= (mae.presentViewFlag) ? 3 : 2,
      nTest= ClusterGenes.curClusterCL.length;
    Gene mList[]=ClusterGenes.curClusterCL.mList;
    Color
      seedGeneColor,
      simClustColor;
    
    if(mae.plotImageMode==mae.PLOT_PSEUDOIMG)
    { /* overlay a grayscale image on light cyan bkgrd */
      seedGeneColor= (mae.useDichromasyFlag) ? Color.blue : Color.green;
      simClustColor= (mae.useDichromasyFlag) ? Color.black : Color.blue;
    }
    else
    { /* overlay a pseudocolor ratio image on black bkgrd */
      seedGeneColor= Color.yellow;
      simClustColor= Color.cyan;
    }
    
    for(int k=0;k<nTest;k++)
    { /* test each gene */
      Gene gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      
      mid= gene.mid;
      int
        gid= gene.gid,
        gid2= (cfg.maxFIELDS>1) ? map.gidToGangGid[gid] : -1;
      if(gene.geneDist==0.0)
      { /* gene being tested */
        Point p= xyCQ[gid];
        drawCircleAroundSpot(p, g, seedGeneColor,addPixels,thickness);
        if(gid2!=-1)
        {
          Point pGang= xyCQ[gid2];
          drawCircleAroundSpot(pGang, g, seedGeneColor,addPixels,
          thickness);
        }
      } /* gene being tested */
      else
      { /* other genes */
        /* make the size 1/LSQerr */
        float
          dist= gene.geneDist,
          pctR= 1.0F-(dist/cfg.clusterDistThr);
        int radius= (int)((1.2*pctR+0.5F)*mae.spotRad);
        Point p= xyCQ[gid];
        drawSquareAroundSpot(p, g, simClustColor,radius);
        if(gid2!=-1)
        {
          Point pGang= xyCQ[gid2];
          drawSquareAroundSpot(pGang, g, simClustColor,radius);
        }
      } /* other genes */
    } /* test each gene */
  } /* drawGeneClustersOfCurrentGene */
  
  
  /**
   * drawCountsOfGeneClusters() - draw colored circles as nGeneClusterCnt's.
   * If the Filter count has changed, then re-run the clustering of
   * all filtered genes.
   * This draws circles proportional to # clusters/gene
   * @param g is graphics context
   * @see #drawCircleAroundSpot
   */
  private void drawCountsOfGeneClusters(Graphics g)
  { /* drawCountsOfGeneClusters */
    Gene mList[]= fc.workingCL.mList;
    int
      mid,
      thickness= (mae.presentViewFlag) ? 3 : 1,
      nTest= fc.workingCL.length;
    float maxCnt= (float)ClusterGenes.maxNgeneClusterCnt;
    Color clustCntsColor;
    
    if(mae.plotImageMode==mae.PLOT_PSEUDOIMG)
    { /* overlay a grayscale image on light cyan bkgrd */
      clustCntsColor= Color.blue;
    }
    else
    { /* overlay a pseudocolor ratio image on black bkgrd */
      clustCntsColor= Color.cyan;
    }
    
    for(int k=0; k<nTest;k++)
    { /* test and draw all genes in mList[] */
      Gene gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      
      mid= gene.mid;
      int cnt= gene.nGeneClustersCnt;
      if(cnt>0)
      { /* draw circle proportional to # clusters/gene */
        float pctR= (float)cnt/maxCnt;
        int
          addPixels= (int)(14*pctR),
          gid= gene.gid;
        Point p= xyCQ[gid];
        drawCircleAroundSpot(p, g, clustCntsColor, addPixels,thickness);
        if(cfg.maxFIELDS>1)
        {
          int gid2= map.gidToGangGid[gid];
          Point pGang= xyCQ[gid2];
          drawCircleAroundSpot(pGang, g, clustCntsColor, addPixels,thickness);
        }
      } /* draw circle proportional to # clusters/gene */
    } /* test and draw all genes in mList[] */
  } /* drawCountsOfGeneClusters */
  
  
  /**
   * drawNprimaryNodesGenes() - draw magenta circles as nGeneClusterCnt's.
   * This draws circles proportional to the K-means # clusters/Node-gene.
   * @param g is graphics context
   * @return true if succeed.
   * @see #drawCircleAroundSpot
   */
  private boolean drawNprimaryNodesGenes(Graphics g)
  { /* drawNprimaryNodesGenes */
    if(!mae.useKmeansClusterCntsDispFlag ||
    fc.KmeansNodesCL==null)
      return(false);
    
    Gene
      gene,
      mList[]= fc.KmeansNodesCL.mList;
    int
      cnt,
      nKmeansGenes= fc.KmeansNodesCL.length,
      thickness= (mae.presentViewFlag) ? 3 : 1,
      maxCnt= 0;
    Color kMeansClusterColor;    /* cluster center circles */
    
    if(mae.plotImageMode==mae.PLOT_PSEUDOIMG)
    { /* overlay a grayscale image on light cyan bkgrd */
      kMeansClusterColor= Color.magenta;
    }
    else
    { /* overlay a pseudocolor ratio image on black bkgrd */
      kMeansClusterColor= Color.magenta;
    }
    
    for(int k=0; k<nKmeansGenes;k++)
    {
      gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      cnt= gene.nGeneClustersCnt;
      if(maxCnt<cnt)
        maxCnt= cnt;             /* get Math.max(maxCnt,cnt) */
    }
    if(maxCnt==0)
      return(false);
    
    for(int k=0; k<nKmeansGenes;k++)
    { /* process each K-means gene */
      gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      
      cnt= gene.nGeneClustersCnt;
      if(cnt>0)
      { /* draw circle proportional to # clusters/Node-gene */
        float pctR= (float)cnt/maxCnt;
        int
          addPixels= (int)(14*pctR),
          gid= gene.gid,
          gid2= map.gidToGangGid[gid];
        String sName= (""+gene.clusterNodeNbr);
        Point p= xyCQ[gid];
        drawCircleAroundSpot(p, g, kMeansClusterColor,addPixels,thickness,sName);
        if(cfg.maxFIELDS>1)
        {
          Point pGang= xyCQ[gid2];
          drawCircleAroundSpot(pGang, g, kMeansClusterColor,
          addPixels,thickness,sName);
        }
      } /* draw circle proportional to # clusters/Node-gene */
    } /* process each K-means gene */
    
    return(true);
  } /* drawNprimaryNodesGenes */
  
  
  /**
   * drawEditedGeneList() - draw magenta squares of Filter.editedCL genes
   * @param g is graphics context
   * @see EditedGeneList
   * @see Filter
   * @see #drawSquareAroundSpot
   */
  private void drawEditedGeneList(Graphics g)
  { /* drawEditedGeneList */
    if(!mae.showEGLflag)
      return;
    
    Gene mList[]= gct.editedCL.mList;
    int
      radius= mae.spotRad+1,
      nGenes= gct.editedCL.length;
    
    for(int k=0; k<nGenes;k++)
    { /* draw only genes in the E.G.L. */
      Gene gene= mList[k];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      int gid= gene.gid;
      Point p= xyCQ[gid];     
    
      drawSquareAroundSpot(p, g, Color.magenta,radius);
      if(cfg.maxFIELDS>1)
      { /* if gang spots exist, then draw them as well */
        int gid2= map.gidToGangGid[gid];
        Point pGang= xyCQ[gid2];
        drawSquareAroundSpot(pGang, g, Color.magenta, radius);
      }
      
    } /* draw only genes in the E.G.L. */
  } /* drawEditedGeneList */
  
  
  /**
   * paint() - repaint scrollable canvas in region defined by the scroll bars.
   * This includes the pseudoarray image which is then followed by various overlays
   * include: the genes passing the data filter, clustered genes, K-means clustered
   * genes, genes in the EGL, etc. If they requested a "SaveAs GIF", then it
   * creates WriteGifEncoder image to draw in and then save and then it repaints
   * one more time to refresh the visible canvas.
   * @param g is graphics context
   * @see DrawPseudoImage#drawPseudoImage
   * @see WriteGifEncoder
   * @see WriteGifEncoder#writeFile
   * @see #drawCountsOfGeneClusters
   * @see #drawEditedGeneList
   * @see #drawGeneClustersOfCurrentGene
   * @see #drawGeneList
   * @see #drawNprimaryNodesGenes
   * @see #repaint
   * @see #zoomImage
   */
  public void paint(Graphics g)
  { /* paint */
    if(!mae.mReady || mae.ms==null || mae.maxPreloadImgs==0)
      return;             /* There is no Sample instance at this time */
    
    fc= mae.fc;           /* update ptrs for speedup */
    cfg= mae.cfg;
    map= mae.mp;
    cdb= mae.cdb;
    msR= mae.ms;
    xyCQ= msR.xyCQ;
    
    /*Reset the fonts in case they changed */
    smallFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 12); /* was 10*/
    mediumFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 14);
    
    /* [1] get window sizes */
    int
      cWidth= getSize().width,   /* canvas size */
      cHeight= getSize().height,
      xS, yS;			               /* Subwindow to draw. Must be < 0
                                  * since it draws the lower right
                                  * rectangle from there. */
    
    /* [1.1] Compute pseudo image canvas size as
     *   (array area) + (status area).
     */
    //iHeight= preferredHeight;  /* mae.pWidth or mae.canvasHSize */
    //iWidth= preferredWidth;    /* mae.pHeight or mae.canvasVSize */
    scWidth= iWidth+extraWidth;
    scHeight= iHeight+extraHeight;
    
    /* [1.2] If draw plot into GIF image file, setup new Graphics g. */
    Image gifImage= null;
    
    if(drawIntoImageFlag)
    { /* draw into GIF file Image instead of canvas */
      int
        //w= cWidth,
        //h= cHeight;
        w= scWidth,
        h= scHeight;
        gifImage= createImage(w,h);
        g= gifImage.getGraphics();
    } /* draw into GIF file Image instead of canvas */
    
    /* [1.3] Clear background to background color */
    g.setColor(((mae.plotImageMode!=mae.PLOT_PSEUDOIMG))
                ? Color.black  /* better contrast for pseudocolor */
                : new Color(200,255,255) /* Color.cyan,
                                          * #C6EFF7= light cyan=(186,239,247)
                                          * or Color.white */
              );
    g.fillRect(0,0,scWidth,scHeight);   /* draw the background */
    
    /* [2] Get the subwindow to draw into */
    if(is.hs!=null && is.vs!=null)
    { /* get position of canvas from scroll bars */
      int
        dWidth= - (scWidth - cWidth),  /* - offsets */
        dHeight= - (scHeight - cHeight);
      int
        hVal= is.hs.getValue(),   /* actual scroll positions */
        vVal= is.vs.getValue();
      int
        hMax= is.hs.getMaximum(), /* max range of scroll bars */
        vMax= is.vs.getMaximum();
      
      xS= (hVal * dWidth)/hMax;   /* compute new ULHC base subwindow */
      yS= (vVal * dHeight)/vMax;
      /* 
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("SIC-P.2 (pWidth,pHeight)=("+
                        mae.pWidth+","+mae.pHeight+
                        ") mae.canvas(H,V)size=("+
                        mae.canvasHSize+","+mae.canvasVSize+
                        ")\n  (Width,Height) c=("+cWidth+","+cHeight+
                        ") i=("+iWidth+","+iHeight+
                        ") sc=("+scWidth+","+scHeight+
                        ") d=("+dWidth+","+dHeight+
                        ")\n  (xS,yS)=("+yS+","+xS+
                        ") (hVal,vVal)=("+hVal+","+vVal+
                        ") (hMax,vMax)=("+hMax+","+vMax+
                        ")\n  is.preferred(W,H)=("+
                        is.preferredWidth+","+ is.preferredHeight+")"+
                        ") sic.preferred(W,H)=("+
                        preferredWidth+","+ preferredHeight+")"
                        );
     */
    } /* get position of canvas from scroll bars */
    else
    { /* get position of canvas from elsewhere */
      xS= xObj;
      yS= yObj;
    }
    
    /* [2.1] Clip subwindow for safety. Must be < 0 */
    xS= (xS>0) ? 0 : xS;
    yS= (yS>0) ? 0 : yS;
    
    /* [2.2]  Save the ULHC window base coordinates. Note: [xyBase= -xyS]. */
    xBase= -xS;
    yBase= -yS;
    
    /* [2.3] Draw image starting at (xS,yS) of image.
     * Note: draw at a negative position which causes the image
     * to be drawn starting at the lower right hand rectangle
     * starting at (-xS, -yS). See TYJava pg 198 on Zoom trick
     * which unfortunately does not get entire image.
     * [TODO] - get better algorithm which is faster and covers
     * entire image.
     */
    if(mae.pImage==null)
    { /* if pImage does not exist, create it now */
      /* Need to create here because of wierd bug where create before do
       * update. This is described in
       *    http://forum2.java.sun.com/forum?14@@.ee82679
       *
       * Create pseudo image - regardless of !mae.isAppletFlag value.
       * NOTE[1] we SHARE this between HPs and save a LOT of memory!!!
       * This means if we switch HPs we need to redraw it.
       * NOTE[2] also the coordinate system is different from that
       * of the .quant files (pImage is smaller) so we need to
       * always map the coords when drawing into pImage.
       */
      mae.pImage= createImage(scWidth,scHeight);
      
      if(mae.CANVAS_WRAPAROUND_BUG)
      /*  System.out.println("SIC-P.2.3 (cWidth,cHeight)=("+cWidth+","+cHeight+
                           ") mae.(pWidth,pHeight)=("+ mae.pWidth+","+mae.pHeight+
                           ")\n  mae.canvas(H,V)size=("+mae.canvasHSize+","+mae.canvasVSize+
                           ")\n  (iWidth,iHeight)=("+iWidth+","+iHeight+
                           ")  (cWidth,cHeight)=("+ cWidth+","+cHeight+
                           ")\n  (scWidth,scHeight)=("+ scWidth+","+scHeight+")");
      */
      
      //if(mae.CONSOLE_FLAG)
        System.out.println("SIC-P.2.3 (cWidth,cHeight)=("+cWidth+","+cHeight+
                           ") mae.(pWidth,pHeight)=("+ mae.pWidth+","+mae.pHeight+
                           ")\n  mae.canvas(H,V)size=("+mae.canvasHSize+","+mae.canvasVSize+
                           ")\n  (iWidth,iHeight)=("+iWidth+","+iHeight+
                           ")  (cWidth,cHeight)=("+ cWidth+","+cHeight+
                           ")\n  (scWidth,scHeight)=("+ scWidth+","+scHeight+
                           ")  extra(Width,Height)=("+ extraWidth+","+extraHeight+
                           ")\n  is.preferred(W,H)=("+is.preferredWidth+","+ is.preferredHeight+")"+
                           ") sic.preferred(W,H)=("+ preferredWidth+","+ preferredHeight+")"
                           );
      
    } /* if pImage does not exist, create it now */
    
    if(mae.updatePseudoImgFlag)
    { /* draw pseudo grayscale micro array image */
      /* NOTE: not sure why it does not work from update(), it is null sometimes */
      Graphics gp= mae.pImage.getGraphics();
      mae.dwPI.drawPseudoImage(gp, scWidth, scHeight,
      mae.plotImageMode, mae.ms);
      mae.updatePseudoImgFlag= false;  /* so don't refresh every repaint()
                                        * only when new data */
    } /* draw pseudo grayscale micro array image */
    
    /* [2.4] If there is no database, the don't bother drawing the rest */
    if(!mae.startupFileExistsFlag)
      return;
    
    /* [3] If magnification>1, draw zoomed region else 1:1 */
    if(is.magnification>1)
      zoomImage(g, mae.pImage,  xS, yS, scWidth, scHeight);
    else
      g.drawImage(mae.pImage, xS, yS, this);    /* just draw 1:1 */
    
    /* [4] Draw list of WorkingCL gene list of points.
     * Then draw the current gene spot or spots if Gang mode
     * (i.e. xxxOBJ) as overlays.
     */
    if(mae.viewFilteredSpotsFlag)
    { /* show Filtered spots */
      if(mae.useKmeansClusterCntsDispFlag)
        drawGeneList(g, fc.workingCL, 
                    (mae.MARKER_GENES | mae.MARKER_KMEANS_CLUSTER | 
                     mae.MARKER_CURRENT));
      else
        drawGeneList(g, fc.workingCL,
                     (mae.MARKER_GENES | markerShape | mae.MARKER_CURRENT));
    } /* show Filtered spots */
    
    /* [5] If Expression Profile mode is on, then draw it */
    if(cdb.isValidObjFlag && mae.mbf.exprProfilePopup!=null)
    { /* draw expression profile for the gene */
      int
        f= cdb.objField,
        grid= cdb.objGrid,
        r= cdb.objRow,
        c= cdb.objCol,
        gid= map.fgrc2gid[f][grid][r][c],
        mid= map.gid2mid[gid];
      Gene gene= map.midStaticCL.mList[mid];
      String
        msg= (gene==null)
                ? "-no gene-"
                : mae.masterIDname + " [" + gene.Master_ID + "]";
      mae.mbf.exprProfilePopup.updateData(mid, msg, true /* drawLabelsFlag */);
    } /* draw expression profile for the gene */
    
    /* [6] If drawing cluster of similar genes, then draw them as blue squares */
    if(cdb.isValidObjFlag && mae.useSimGeneClusterDispFlag)
      drawGeneClustersOfCurrentGene(g);
    
    /* [7] If drawing gene cluster counts, then draw them as blue circles. */
    if(mae.useClusterCountsDispFlag)
      drawCountsOfGeneClusters(g);
    
    /* [8] If drawing K-means Nodes, then draw as magenta cirles */
    if(mae.useKmeansClusterCntsDispFlag)
      drawNprimaryNodesGenes(g);
    
    /* [9] If showing edited gene list, editedCL genes,
     * then draw them as magenta squares.
     */
    if(mae.showEGLflag)
      drawEditedGeneList(g);
    
    /* [10] If drawing to a GIF file, then cvt Image to Gif stream
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
  } /* paint */
  
  
} /* End class: ScrollableImageCanvas */


