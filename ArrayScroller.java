/** File: ArrayScroller.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.util.*;
import java.util.EventListener;
import java.lang.*;
import java.io.*;

/**
 * This class supports the scrollable clickable pseudoarray image.
 * The image is contained in the ScrollableImageCanvas which uses 
 * DrawPseudoImage to generate the image to draw on the canvas. 
 * It also has a text-title, ScrollableImageCanvas, 
 * horizontal and vertical scroll bars used to scrolling the canvas. The 
 * ArrayScroller is created as part of the GUI before the pseudoarray images 
 * has been generated. Therefore, we need to do several setXXXX() method 
 * calls after the image has been generated to have the the ArrayScroller 
 * "stuff" it as needed. 
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
 * @version $Date: 2006/05/16 20:53:30 $   $Revision: 1.11 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see DrawPseudoImage
 * @see ScrollableImageCanvas
 */

class ArrayScroller extends Panel implements AdjustmentListener,
      MouseListener, MouseMotionListener
{
  /** link to global MAExplorer instance */
  MAExplorer
    mae;                        
  /** link to global MaHybridSample instance */  
  MaHybridSample
    msR;                        
  
 /** instance of the scrollable image canvas */
  ScrollableImageCanvas 
    siCanvas;    
  /** opt. holds mae.pImage, vs & hs scroll box in SOUTH*/			
  Panel
    southPanel;			
  /** opt. vertical scroll bar */
  Scrollbar 
    vs;
  /** opt. horizontal scroll bar  */
  Scrollbar 	       
    hs;			        
  /** image title placed above canvas  */
  TextField
    txtField1;						        
  /** genomic info placed above canvas */
  TextField
    txtField2;						        
  /** additional genomic info placed above canvas */
  TextField
    txtField3;			
  /** HP sample name */
  String
    name;		        			
  /** current title for window */
  String
    title;	
    		
  /** last foreground colors cached for showMsg1 */
  private Color
    lastFG1; 
  /** last background colors cached for showMsg1 */
  private Color                 
    lastBG1;
  /** last foreground colors cached for showMsg2 */
  private Color
    lastFG2; 
  /** last background colors cached for showMsg2 */
  private Color                 
    lastBG2;/** last foreground colors cached for showMsg3 */
  private Color
    lastFG3; 
  /** last background colors cached for showMsg3 */
  private Color                 
    lastBG3;
    
  /** magnification */
  int 
    magnification;
  /** width of panel  */
  public int 
    preferredWidth;
  /** height of panel  */
  public int 
    preferredHeight;		
  /** prewired scroller steps */
  int 
    steps= 100;			
  /** scrollbar thickness */
  int 
    pagestep= 15;		
  
  /** flag: set if xObj,yObj is set */
  boolean
    img_selectedFlag;	       
  
    
  /**
   * ArrayScroller() - constructor. Resize to the preferred size.
   * @param mae is the MAExplorer instance
   * @param name of the current microarray sample
   * @param title of the window
   * @param iWidth of scrollableImageCanvas
   * @param iHeight of scrollableImageCanvas
   * @param preferredWidth is the size of mae.is main window
   * @param preferredHeight is the size of mae.is
   * @param magnification of array
   * @see ScrollableImageCanvas
   * @see #syncScrollerAccess
   */
  ArrayScroller(MAExplorer mae, String name, String title,
                int iWidth, int iHeight, 
                int preferredWidth, int preferredHeight,  
                int magnification )
  { /* ArrayScroller */
    this.mae= mae;
    this.name= name;
    this.title= (title==null) ? "" : title;
    this.preferredWidth= preferredWidth;
    this.preferredHeight= preferredHeight;
    this.magnification= magnification;
    
    img_selectedFlag= false;
    msR= mae.ms;
    
    setLayout(new BorderLayout(0,0));	/* make scroll bars flush to image*/
    
    /* Always add scrollbars */
    southPanel= new Panel();
    southPanel.setLayout(new GridLayout(2,1, /*R,C*/ 1,1 /*gap*/));
    add("South", southPanel);
    
    vs= new Scrollbar(Scrollbar.VERTICAL, 0, pagestep, 0, steps);
    hs= new Scrollbar(Scrollbar.HORIZONTAL, 0, pagestep, 0, steps);
    vs.addAdjustmentListener(this);
    hs.addAdjustmentListener(this);
    southPanel.add(hs);
    add("East", vs);
    
    /* Add three text fields msg1, msg2, msg3 */
    txtField1= new TextField(title,25);
    txtField2= new TextField(title,25);
    txtField3= new TextField(title,25);
    Panel txtPanel= new Panel();
    txtPanel.setLayout(new GridLayout(3,1, /*R,C*/ 1,1 /*gap*/));
    
    txtPanel.add(txtField1);
    txtPanel.add(txtField2);
    txtPanel.add(txtField3);
    
    txtField1.setText(""); /* Note: set all text fields to read-only */
    txtField2.setText("");
    txtField3.setText("");
    
    txtField1.setEditable(false);
    txtField2.setEditable(false);
    txtField3.setEditable(false);
    
    txtField1.setForeground(Color.black);
    txtField2.setForeground(Color.black);
    txtField3.setForeground(Color.black);
    
    txtField1.setBackground(Color.white);
    txtField2.setBackground(Color.white);
    txtField3.setBackground(Color.white);
    
    add("North",txtPanel);
    
    /* Create canvas of preferred size (iWidth, iHeight) */
    siCanvas= new ScrollableImageCanvas(mae.ms, iWidth, iHeight, this);
    siCanvas.addMouseListener(this);
    siCanvas.addMouseMotionListener(this);
    add("Center", siCanvas);
    
    lastFG1= Color.black;
    lastBG1= Color.white;
    lastFG2= Color.black;
    lastBG2= Color.white;
    lastFG3= Color.black;
    lastBG3= Color.white;
    
    siCanvas.setBackground(Color.white);
    
    syncScrollerAccess();         /* forces update ms for canvas */
  } /* ArrayScroller */
  
  
  /**
   * recreateScollableCanvas() - Resize canvas with new size by making a new ScrollableImageCanvas.
   * Note: have to setup other variables as needed.
   * @param iWidth of scrollableImageCanvas
   * @param iHeight of scrollableImageCanvas
   * @see ScrollableImageCanvas
   * @see #syncScrollerAccess
   */
  void recreateScollableCanvas(int iWidth, int iHeight)
  { /* recreateScollableCanvas */
    /* [1] Remove the old canvas from ArrayScroller and listeners */
    if(siCanvas!=null)
    {
      remove(siCanvas);
      siCanvas.removeKeyListener(mae.mbf); /* will listen for ESC key */
    }
    
    /* [2] Create canvas of preferred size */
    siCanvas= new ScrollableImageCanvas(mae.ms, iWidth, iHeight, this);
    siCanvas.addMouseListener(this);
    siCanvas.addMouseMotionListener(this);
    add("Center", siCanvas);
    
    siCanvas.setBackground(Color.white);
    
    /* [3] Add Global references and event handlers */
    //mae.sic= siCanvas;               /* fix Global references */
    siCanvas.addKeyListener(mae.mbf); /* will listen for ESC key */
    
    syncScrollerAccess();         /* forces update ms for canvas */
  } /* recreateScollableCanvas */
  
  
  /**
   * getPreferredSize() - get the preferred window size
   * @return size of window
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(new Dimension(preferredHeight, preferredWidth));
  } /* getPreferredSize */
  
  
  /**
   * syncScrollerAccess() - update the image scroller and sync canvas access
   * to current HP's pseudoarray image, ms, msX, msY
   * @see ScrollableImageCanvas#syncCanvasAccess
   */
  void syncScrollerAccess()
  { /* syncScrollerAccess */
    msR= mae.ms;
    repaint();
    siCanvas.syncCanvasAccess();
  } /* syncScrollerAccess */
  
  
  /**
   * setText1() - change the text1 msg  for the panel.
   * Force foreground to BLACK and background to WHITE.
   * @param text to save in text field
   * @param saveTitleFlag to save text of last message
   */
  void setText1(String msg, boolean saveTitleFlag)
  { /* setText1 */
    setText1(msg, saveTitleFlag, Color.black, Color.white);
  } /* setText1 */
  
  
  /**
   * setText1() - change the text1 field in the panel with fg, bg colors
   * You can save the msg as the title.
   * @param msg to save in text field
   * @param saveTitleFlag to save text of last message
   * @param fg is foreground color
   * @param bg is background color
   */
  void setText1(String msg, boolean saveTitleFlag, Color fg, Color bg)
  { /* setText1 */
    if(saveTitleFlag)
      this.title= msg;
    if(fg==null)
      fg= Color.black;
    if(bg==null)
      bg= Color.white;
    if(!lastFG1.equals(fg))
      txtField1.setForeground(fg);
    if(!lastBG1.equals(bg))
      txtField1.setBackground(bg);
    txtField1.setText(msg);
    lastFG1= fg;
    lastBG1= bg;
  } /* setText1 */
  
  
  /**
   * setText2() - change the text2 field in the panel.
   * Force foreground to BLACK and background to WHITE.
   * @param msg to save in text field
   */
  void setText2(String msg)
  { /* setText2 */
    setText2(msg, Color.black, Color.white);
  } /* setText2 */
  
  
  /**
   * setText2() - change the text2 field in the panel with fg, bg colors
   * @param msg to save in text field
   * @param fg is foreground color
   * @param bg is background color
   */
  void setText2(String msg, Color fg, Color bg)
  { /* setText2 */
    if(fg==null)
      fg= Color.black;
    if(bg==null)
      bg= Color.white;
    if(!lastFG2.equals(fg))
      txtField2.setForeground(fg);
    if(!lastBG2.equals(bg))
      txtField2.setBackground(bg);
    lastFG2= fg;
    lastBG2= bg;
    txtField2.setText(msg);
  } /* setText2 */
  
  
  /**
   * setText3() - change the text3 field in the panel.
   * Force foreground to BLACK and background to WHITE.
   * @param msg to save in text field
   * @see #setText3
   */
  void setText3(String msg)
  { /* setText3 */
    setText3(msg, Color.black, Color.white);
  } /* setText3 */
  
  
  /**
   * setText3() - change the text3 field in the panel with fg, bg colors
   * @param msg to save in text field
   * @param fg is foreground color
   * @param bg is background color
   */
  void setText3(String msg, Color fg, Color bg)
  { /* setText3 */
    if(fg==null)
      fg= Color.black;
    if(bg==null)
      bg= Color.white;
    if(!lastFG3.equals(fg))
      txtField3.setForeground(fg);
    if(!lastBG3.equals(bg))
      txtField3.setBackground(bg);
    lastFG3= fg;
    lastBG3= bg;
    txtField3.setText(msg);
  } /* setText3 */
  
  
  /**
   * setImgPos() - set the Image position Point xyImg. Don't repaint here
   * @param xyImg is the image position
   * @see ScrollableImageCanvas#setImgPos
   * @see #syncScrollerAccess
   */
  void setImgPos(Point xyImg)
  { /* setImgPos */
    siCanvas.setImgPos(mae.ms,xyImg.x, xyImg.y);
    syncScrollerAccess();
  } /* setImgPos */
  
  
  /**
   * setImgPos() - set the Image position (x,y). Don't repaint here...
   * @param xImg is the image position
   * @param yImg is the image position
   * @see ScrollableImageCanvas#setImgPos
   * @see #syncScrollerAccess
   */
  void setImgPos(int xImg, int yImg)
  { /* setImgPos */
    syncScrollerAccess();
    siCanvas.setImgPos(mae.ms,xImg, yImg);
  } /* setImgPos */
  
  
  /**
   * getImgPos() - get the Image position
   * @return the image position
   * @see ScrollableImageCanvas#getImgPos
   * @see #syncScrollerAccess
   */
  Point getImgPos()
  { /* getImgPos */
    syncScrollerAccess();
    Point xyImg= siCanvas.getImgPos();
    return(xyImg);
  } /* getImgPos */
  
  
  /**
   * setObjPos() - set the object position xyObj. Don't repaint here
   * @param xyObj is the object (i.e. gene) position
   * @see ScrollableImageCanvas#setObjPos
   * @see #syncScrollerAccess
   */
  void setObjPos(Point xyObj)
  { /* setObjPos */
    syncScrollerAccess();
    siCanvas.setObjPos(xyObj.x, xyObj.y);
  } /* setObjPos */
  
  
  /**
   * setObjPos) - set the object position (x,y). Don't repaint here
   * @param xObj is the object (i.e. gene) position
   * @param yObj is the object (i.e. gene) position
   * @see ScrollableImageCanvas#setObjPos
   * @see #syncScrollerAccess
   */
  void setObjPos(int xObj, int yObj)
  { /* setObjPos */
    syncScrollerAccess();
    siCanvas.setObjPos(xObj, yObj);
  } /* setObjPos */
  
  
  /**
   * getObjPos() - get the object position
   * @return the object (gene) position
   * @see ScrollableImageCanvas#getObjPos
   * @see #syncScrollerAccess
   */
  Point getObjPos()
  { /* getObjPos */
    syncScrollerAccess();
    Point xyObj= siCanvas.getObjPos();
    return(xyObj);
  } /* getObjPos */
  
  
  /**
   * setScrollPageSteps() - set scroll bar pagestep and steps
   * @param pagestep is the scroll bar paging size
   * @param steps is the scroll bar # of steps
   * @see ScrollableImageCanvas#repaint
   * @see #syncScrollerAccess
   */
  void setScrollPageSteps(int pagestep, int steps)
  { /* setScrollPageSteps */
    if(hs!=null && vs!=null)
    {
      syncScrollerAccess();
      this.pagestep= pagestep;
      this.steps= steps;
      hs.setUnitIncrement(steps);
      hs.setBlockIncrement(pagestep);
      vs.setUnitIncrement(steps);
      vs.setBlockIncrement(pagestep);
      siCanvas.repaint();
    }
  } /* setScrollPageSteps */
  
  
  /**
   * setArrayScrollerSize() - set the Image Scroller size
   * @param hSize is the scroll bar horizontal size
   * @param vSize is the scroll bar vertical size
   * @see ScrollableImageCanvas
   */
  void setArrayScrollerSize(int hSize, int vSize)
  { /* setArrayScrollerSize */
    this.preferredWidth= hSize;
    this.preferredHeight= vSize;
    siCanvas.setSize(preferredWidth, preferredHeight);
    siCanvas.repaint();
  }  /* setArrayScrollerSize */
  
  
  /**
   * drawImageTitle() - draw title in image.
   * This uses the mouse position in the canvas to get the
   * [x,y,g(x,y)/od(x,y)] values and set it to: 'title' + (x,y,g) + "[use]"
   * @see ScrollableImageCanvas#drawImageTitle
   */
  void drawImageTitle()
  { /* drawImageTitle */
    siCanvas.drawImageTitle();
  } /* drawImageTitle */
  
  
  /**
   * getImgCursor() - get the (x,y) scrollable cursor for this image
   * @return cursor, else if there is no cursor, then return (0,0).
   */
  Point getImgCursor()
  { /*getImgCursor */
    Point cursor;
    if(hs!=null && vs!=null)
      cursor= new Point(hs.getValue(),vs.getValue());
    else
      cursor= new Point(0,0); /* NO-OP */
    return(cursor);
  } /*getImgCursor */
  
  
  /**
   * getImgCursorMax() - get the (x,y) scrollable image cursor maxima.
   * @return (x,y) Point maxima. If there is no cursor, then return (0,0).
   */
  Point getImgCursorMax()
  { /* getImgCursorMax */
    Point curMax;
    if(hs!=null && vs!=null)
      curMax= new Point(hs.getMaximum(),vs.getMaximum());
    else curMax= new Point(0,0);
    return(curMax);
  } /* getImgCursorMax */
  
  
  /**
   * setScrollBar() - set the scroll bars (x,y) position for this image.
   * The arguments are in the range of allowable values
   * else it is a no-op.
   * @param x is scrollabar position to set
   * @param y is scrollabar position to set
   * @see ScrollableImageCanvas#repaint
   */
  void setScrollBar(int x, int y)
  { /* setScrollBar */
    if(hs!=null && vs!=null)
    { /* Only try to set them if they exist */
      if(x>0 && x<hs.getMaximum())
        hs.setValue(x);
      if(y>0 && y<vs.getMaximum())
        vs.setValue(y);
      siCanvas.repaint();	/* [BUG] does not repaint...  */
    }
  } /* setScrollBar */
  
  
  /**
   * adjustmentValueChanged() - handle ArrayScroller scroll events.
   * Handle ArrayScroller scroll events and cause a repaint of the
   * canvas. If we are using the scroll bar for this canvas, then set
   * the obj position for use by other canvas for the same image.
   * @param e is scroller adjustment event
   * @see ScrollableImageCanvas#repaint
   * @see ScrollableImageCanvas#setImgPos
   * @see ScrollableImageCanvas#setObjPos
   */
  public void adjustmentValueChanged(AdjustmentEvent e)
  { /* adjustmentValueChanged */
    String arg= e.toString();	/* arg for the event */
    Object source= e.getSource();
    
    /* Test if they were fooling with the image canvas scroll bars */
      /* If NOT using the scroll bar, then set the obj position
       * using the scroll bars cursors in the rough position.
       */
    if((source==hs || source==vs) && mae.pImage!=null)
    {
      int xScrCursor= - (mae.pImage.getWidth(this) - getSize().width) /
      hs.getMaximum() * hs.getValue();
      int yScrCursor= - (mae.pImage.getHeight(this) - getSize().height) /
      vs.getMaximum() * vs.getValue();
      siCanvas.setObjPos(xScrCursor, yScrCursor);
      siCanvas.setImgPos(mae.ms, xScrCursor, yScrCursor);
    }
    
    siCanvas.repaint();
  } /* adjustmentValueChanged */
  
  
  /**
   * mousePressed() - handle mouse pressed event
   * by passing it to ScrollableImageCanvas to handle.
   * @param e is mouse press event
   * @see ScrollableImageCanvas#mousePressed
   */
  public void mousePressed(MouseEvent e)
  { /* mousePressed */
    siCanvas.mousePressed(e); /* Pass down to ScrollableCanvas */
  } /* mousePressed */
  
  
  /**
   * mouseReleased() - handle mouse released event
   * by passing it to ScrollableImageCanvas to handle.
   * @param e is mouse released event
   * @see ScrollableImageCanvas#mouseReleased
   */
  public void mouseReleased(MouseEvent e)
  { /* mouseReleased */
    siCanvas.mouseReleased(e); /* Pass down to ScrollableCanvas */
  } /* mouseReleased */
  
  
  /**
   * mouseClicked() - handle mouse Click event
   * by passing it to ScrollableImageCanvas to handle.
   * @param e is mouse clicked event
   * @see ScrollableImageCanvas#mouseClicked
   */
  public void mouseClicked(MouseEvent e)
  { /* mouseClicked */
    siCanvas.mouseClicked(e); /* Pass down to ScrollableCanvas */
  } /* mouseClicked */
  
  
  /**
   * mouseMoved() - handle mouse Move event
   * by passing it to ScrollableImageCanvas to handle.
   * @param e is mouse moved event
   * @see ScrollableImageCanvas#mouseMoved
   */
  public void mouseMoved(MouseEvent e)
  { /* mouseMoved */
    siCanvas.mouseMoved(e);   /* Pass down to ScrollableCanvas */
  } /* mouseMoved */
  
  
  /**
   * mouseDragged() - handle mouse Drag event
   * by passing it to ScrollableImageCanvas to handle.
   * @param e is mousedragged event
   * @see ScrollableImageCanvas#mouseDragged
   */
  public void mouseDragged(MouseEvent e)
  { /* mouseDraggd */
    siCanvas.mouseDragged(e); /* Pass down to ScrollableCanvas */
  } /* mouseDragged */
  
  
  /**
   * mouseEntered() - handle mouse Enter events
   * by passing it to ScrollableImageCanvas to handle.
   * @param e is mouse entered event
   * @see ScrollableImageCanvas#mouseEntered
   */
  public void mouseEntered(MouseEvent e)
  { /* mouseEntered */
    siCanvas.mouseEntered(e); /* Pass down to ScrollableCanvas */
  } /* mouseEntered */
  
  
  /**
   * mouseExited() - handle mouse Exit events
   * by passing it to ScrollableImageCanvas to handle.
   * @param e is mouse left event
   * @see ScrollableImageCanvas#mouseExited
   */
  public void mouseExited(MouseEvent e)
  { /* mouseExited */
    siCanvas.mouseExited(e); /* Pass down to ScrollableCanvas */
  } /* mouseExited */
  
} /* end class:  ArrayScroller */

