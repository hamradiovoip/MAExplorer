/** File: ExprProfilePanel.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import java.util.EventListener;
import java.lang.*;

/**
 * This class creates and displays a window containing an ExprProfileCanvas.
 * The expression profile is drawin in the ExprProfileCanvas. It also 
 * contains mouse handlers for clicking on points in the canvas.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), Gb. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ExprProfileScrollPane
 * @see ShowExprProfilesPopup
 */
 

class ExprProfilePanel extends Panel implements MouseListener, MouseMotionListener
{
  /** link to global MAExplorer instance */
  MAExplorer
    mae;               
    
  /** expression profile canvas we put into panel */  
  ExprProfileCanvas
    epc;               
       
	     
  /**
   * ExprProfilePanel() - Constructor to display expression profile in canvas.
   *<PRE>
   * If mid >=0, then display gene info
   * If mid==-1, then display "request to click on a gene"
   * If mid==-2, then don't draw anything.
   *</PRE>
   * @param mae is the MAExplorer instance
   * @param mid is the Master Gene Index if not -1
   * @param title is the title for canvas label
   * @param font is the overide labels font if not null
   * @param preferredWidth is the canvas size, 0 to use defaults
   * @param preferredHeight
   * @param showKmeansDataFlag to draw K-means data else use gene data
   * @param drawLabelsFlag if true
   * @see ExprProfileCanvas
   */
  ExprProfilePanel(MAExplorer mae, int mid, String title, Font font,
                   int preferredWidth, int preferredHeight,
                   boolean showKmeansDataFlag, boolean drawLabelsFlag )
  { /* ExprProfilePanel */
    this.mae= mae;
    
    epc= new ExprProfileCanvas(mae, mid, title, font,
    preferredWidth, preferredHeight,
    showKmeansDataFlag, drawLabelsFlag);
    this.add(epc);
    
    epc.addMouseListener(this);
    epc.addMouseMotionListener(this);
  } /* ExprProfilePanel */
  
  
  /**
   * getPreferredSize() - get the preferred size
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(epc.getPreferredSize());
  } /* getPreferredSize */
  
  
  /**
   * getMinimumSize() - get the minimumpreferred size
   */
  public Dimension getMinimumSize()
  { /* getMinimumSize */
    return(epc.getMinimumSize());
  } /* getMinimumSize */
  
  
  /**
   * updateData() - update expression profile with new gene data if a legal gene MID.
   * @param mid is the Master Gene Index if not -1
   * @param title is the title for canvas label
   * @param drawLabelsFlag if true
   * @see ExprProfileCanvas#updateData
   */
  boolean updateData(int mid, String title, boolean drawLabelsFlag )
  { /* updateData */
    return(epc.updateData(mid,title,drawLabelsFlag));
  } /* updateData */
  
  
  /**
   * setPlotEPmeansData() - set expression profile to plot means data.
   * @param mid is the Master Gene Index if not -1
   * @param hpDataNbr is the # HP-E members
   * @param maxHPdataMn is the max value in vector
   * @param hpDataMn is the Mean HP member quant data
   * @param hpDataSD is the S.D. HP member quant data
   * @param doMeanPlotsFlag if true
   * @see ExprProfileCanvas#setPlotEPmeansData
   */
  void setPlotEPmeansData(int mid, int hpDataNbr, float maxHPdataMn,
                          float hpDataMn[], float hpDataSD[],
                          boolean doMeanPlotsFlag)
  { /* setPlotEPmeansData */
    epc.setPlotEPmeansData(mid, hpDataNbr, maxHPdataMn, hpDataMn,
    hpDataSD, doMeanPlotsFlag);
  } /* setPlotEPmeansData */
  
  
  /**
   * paint() - draw the expression profile plot
   * only repaint if a legal gene MID.
   * @param g is graphics context
   * @see ExprProfileCanvas#repaint
   */
  public void paint(Graphics g)
  { /* paint */
    epc.repaint();
  } /* paint */
  
  
  /**
   * mousePressed() - handle mouse Pressed events
   * @param is mouse pressed event
   * @see #repaint
   */
  public void mousePressed(MouseEvent e)
  { /* mousePressed */
    epc.xCursor= e.getX();
    epc.yCursor= e.getY();
    epc.drawAdditionalDataFlag= true;
    repaint();
  } /* mousePressed */
  
  
  /**
   * mouseDragged() - just report (HP, Quant) of nearest HP sample near where mouse is
   * @param is mouse dragged event
   * @see #repaint
   */
  public void mouseDragged( MouseEvent e )
  { /* mouseDragged */
    epc.xCursor= e.getX();
    epc.yCursor= e.getY();
    epc.drawAdditionalDataFlag= true;
    repaint();
  } /* mouseDragged */
  
  
  /**
   * mouseReleased() - handle mouse Released events
   * @param is mouse released event
   * @see #repaint
   */
  public void mouseReleased(MouseEvent e)
  { /* mouseReleased */
    epc.xCursor= e.getX();
    epc.yCursor= e.getY();
    epc.drawAdditionalDataFlag= true;
    repaint();
  } /* mouseReleased */
  
  
  public void mouseClicked(MouseEvent e) { }
  public void mouseMoved(MouseEvent e) { }
  public void mouseEntered(MouseEvent e) { }
  public void mouseExited(MouseEvent e) { }
  
} /* end of ExprProfilePanel class */

