/** File: DrawClusterGram.java */

import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.*;

/**
 * This class creates and displays a ClusterGram canvas window.
 * It contains a scrollable ClusterGram canvas of ordered genes in a popup window.
 * When performing clustering, it is updated by the PopupRegistry when 
 * the current gene, Filter.workingCL, or other state values change. 
 * If hierarchical clustering is used, it uses dendrogram data structures 
 * in HierClustNode - otherwise data may be] passed an ordered 
 * GeneList (i.e. mList[]).
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), G.g Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.10 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ClusterGenes
 * @see ClusterGramCanvas
 * @see HierClustNode
 */

class DrawClusterGram extends ScrollPane 
	     implements ActionListener, ItemListener, WindowListener, 
	     MouseListener, MouseMotionListener
{
  /** link to global instance */
  private MAExplorer
    mae;                        
    
  /** Parent popup window instance */
  ShowPlotPopup
    spp;                        
  /** cluster tree */
  private HierClustNode
    tree;                       
  /** title for window */
  private String
    title;                      
  /** titlePanel of parent  */
  private Panel
    titlePanel;		        
  /** Clustergram canvas */
  ClusterGramCanvas
    cgC;			
  /** GeneList being used */
  private GeneList 
    orderedCL;			
  
  /** # genes in orderedCL */
  private int
    nGenes;                     
  /** default  # of genes to display in EP list */
  final int
    N_EP_GENES= 8;
  /** plot mode */
  int
    plotMode;
  /** # of rows in clustergram */ 
  private int
    nRows;                     
 /** # of columns in clustergram */ 
  private int
    nCols;
  /** current selected row of ClusterGram */
  private int
    curCGrow;                   
  /** current mid of selected ClusterGram row */
  private int
    curCGmid;
  /** # of genes to display in popup EP plot */
  private int
    nEPgenes= N_EP_GENES;       
    
  /** height size of status canvas */
  int
    statusCanvasHeight;         
  /** set if draw dendrogram as well */
  boolean
    drawDendroGramFlag;         
 /** mode of operation used in creating thiswindow for use in closing the window */
  private boolean
    keepFlagsOnClosing;         
      
  /** status canvas */
  StatusCanvas
    statusCanvas;               
  /** draw status at top of Gif image */
  Graphics
    gGif= null;                 
  /** toggle drawing the dendrogram */
  private Checkbox
    dGramCheckBox;              
  /** Save display as GIF file */
  private Button
    saveAsGIF;                                
  /** popup Report */ 
  private Button
    custGramReport;                              
  /** zoom in on dendrogram */
  private Button
    zoomDgramButton;                          
  /** show list of Samples */ 
  private Button
    showHPnames;                             
  /** show expression profiles for nEPgenes at current ClusterGram 
   * position curCGrow
   */
  private Button
    showEPsubset;               
    			 
  /** exists if create EP plot subset popup */
  ShowExprProfilesPopup
    hierClustersExprProfPopup;  



  /**
   * DrawClusterGram() - Create popup grid of ClusterGram plots.
   * Set the mae.fc.workingCL if the specified list is null.
   * @param mae is the MAExplorer instance
   * @param spp is the ShowPlotPopup instance
   * @param titlePanel is where to put extra stuff
   * @param tree is the HierClustNode cluster tree
   * @param orderedCL is thegene list to use
   * @param title for window
   * @param plotMode is the specific plot mode to implement
   * @param keepFlagsOnClosing to save flags on closing
   * @see ClusterGramCanvas
   * @see Filter#showNbrFilteredGenes
   * @see StatusCanvas
   * @see Util#showMsg
   * @see #repaint
   */
  DrawClusterGram(MAExplorer mae, ShowPlotPopup spp,
                  Panel titlePanel, HierClustNode tree,
                  GeneList orderedCL, String title,
                  int plotMode, boolean keepFlagsOnClosing)
  { /* DrawClusterGram */
    super();
    
    this.mae= mae;
    this.spp= spp;
    this.titlePanel= titlePanel;
    this.tree= tree;
    this.title= title;
    this.plotMode= plotMode;
    this.keepFlagsOnClosing= keepFlagsOnClosing;
    
    /* Setup the GeneList to use */
    this.orderedCL= orderedCL;
    nGenes= orderedCL.length;
    if(nGenes==0)
    {
      Util.showMsg("Can't create ClusterGram since no Filtered data");
      Util.popupAlertMsg("Can't create ClusterGram ",
                         "Can't create ClusterGram since no Filtered data.\n",
                         4, 60);
      return;
    }
    
    /* Compute # of boxes to draw in the array */
    nCols= tree.nDataV;        /* # of Samples */
    nRows= nGenes;
    
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("SCGP nGenes="+nGenes+
       " nCols="+nCols+" nRows="+nRows);
       */
    
      /* Add a panel in the scrollable pane which contains the canvas
       * which contains the plots of cluster gram rows for all of
       * the genes.
       */
    
    /* Note: tree contains all lists needed for drawing dGram */
    cgC= new ClusterGramCanvas(mae, this, orderedCL, tree);
    cgC.addMouseListener(this);
    //cgC.addMouseMotionListener(this);
    this.add(cgC,"Center");
    
    /* Add a status canvas to the center of title panel if it exists */
    if(titlePanel!=null)
    {
      statusCanvasHeight= 100;
      statusCanvas= new StatusCanvas(this, 100,  /* [TODO] make dynamic */
      cgC.MIN_CANVAS_WIDTH);
      titlePanel.add("Center", statusCanvas);
    }
    
    /* Center frame on the screen, PC only */
    Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
    Point pos= new Point((screen.width - this.getSize().width)/2,
                         (screen.height - this.getSize().height)/2);
    this.setLocation(pos);
    
    /* Update Msg3 w/# genes passing filter */
    mae.fc.showNbrFilteredGenes();
    
    repaint();
  } /* DrawClusterGram */
  
  
  /**
   * addButtonsToParent() - add buttons for this plot to parents's panel
   * @param spp is the ShowPlotPopup instance
   * @param p is panel where to put extra stuff
   * @param bFont is font to use
   */
  void addButtonsToParent(ShowPlotPopup spp, Panel p, Font bFont)
  { /* addButtonsToParent */
    
    /* Create the buttons and arrange to handle button clicks */
    Font buttonFont= bFont;
    if(bFont==null)
      buttonFont= new Font(mae.cfg.fontFamily, Font.PLAIN /*BOLD*/, 14);
    
    /* Always have the Close button */
    showEPsubset= new Button("EP plot");
    showEPsubset.addActionListener(this);
    showEPsubset.setFont(buttonFont);
    p.add(showEPsubset);
    
    custGramReport= new Button("ClustGram Report");
    custGramReport.addActionListener(this);
    custGramReport.setFont(buttonFont);
    p.add(custGramReport);
    
    showHPnames= new Button("Show HP names");
    showHPnames.addActionListener(this);
    showHPnames.setFont(buttonFont);
    p.add(showHPnames);
    
    drawDendroGramFlag= false;
    if(plotMode==mae.PLOT_CLUSTERGRAM)
    {
      /* Add box for toggling drawing the dendrogram */
      dGramCheckBox= new Checkbox("DendroGram", drawDendroGramFlag);
      dGramCheckBox.setBackground(Color.white);
      dGramCheckBox.addItemListener(this);
      
      /* Add box for toggling zooming the dendrogram */
      cgC.dGramZoomMag= 1;
      zoomDgramButton= new Button("   1X DG");
      zoomDgramButton.addActionListener(this);
      zoomDgramButton.setFont(buttonFont);
      p.add(dGramCheckBox);
      p.add(zoomDgramButton);
    }
    
    if(!mae.isAppletFlag)
    { /* only if stand-alone */
      saveAsGIF= new Button("SaveAs");
      saveAsGIF.addActionListener(this);
      saveAsGIF.setFont(buttonFont);
      p.add(saveAsGIF);
    }
    
    /* NOTE: Use Parent's close button */
      /*
      Button closeButton= new Button("Close");
      closeButton.addActionListener(this);
      closeButton.setFont(buttonFont);
      p.add(closeButton);
       */
  } /* addButtonsToParent */
  
  
  /**
   * getPreferredSize() - get the preferred size
   * @return window size
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(cgC.getPreferredSize());
  } /* getPreferredSize */
  
  
  /**
   * getMinimumSize() - get the minimum preferred size
   * @return window size
   */
  public Dimension getMinimumSize()
  { /* getMinimumSize */
    return(cgC.getMinimumSize());
  } /* getMinimumSize */
  
  
  /**
   * paint() - redraw clustergram
   * @param g is graphics context
   * @see ClusterGramCanvas#repaint
   * @see #updateCGplot
   */
  public void paint()
  { /* paint */
    updateCGplot(orderedCL,tree);
    cgC.repaint();
  } /* paint */
  
  
  /**
   * updateCurGene()- update the current gene in the ClusterGram
   * @param mid is the Master Gene Index if not -1
   * @see ClusterGramCanvas#updateCurGene
   * @see #repaint
   */
  void updateCurGene(int mid)
  { /* updateCurGene */
    cgC.updateCurGene(mid);
    repaint();
  } /* updateCurGene */
  
  
  /**
   * updateCGplot() - update ClusterGram plots using new GeneList.
   * Set the  mae.fc.workingCL if the specified list is null.
   * @param orderedCL is thegene list to use
   * @param tree is the HierClustNode cluster tree
   * @see ClusterGramCanvas#updateData
   */
  void updateCGplot(GeneList orderedCL, HierClustNode tree)
  { /* updateCGplot */
    this.orderedCL= orderedCL;
    nGenes= orderedCL.length;
    this.tree= tree;
    
    cgC.updateData(orderedCL, tree);
  } /* updateCGplot */
  
  
  /**
   * close() - close this popup and reset flags if keepFlagsOnClosing is false
   * @see EventMenu#setClusterDisplayState
   */
  void close()
  { /* close */
    if(!keepFlagsOnClosing)
    { /* reset flags */
      ClusterGenes.hierClusterGramPopup= null;
      ClusterGenes.hcn= null;
      EventMenu.setClusterDisplayState(null,false);  /* disable all cluster methods */
    } /* reset flags */
  } /* close */
  
  
  /**
   * actionPerformed() - Handle button clicks
   * @param e is button pressed event
   * @see ClusterGramCanvas#drawGifFile
   * @see ClusterGramCanvas#repaint
   * @see EventMenu#promptFileName
   * @see MAExplorer#repaint
   * @see Report
   * @see SampleSets#showHP_E_assignmentsPopup
   * @see ShowExprProfilesPopup
   * @see Util#rmvFinalSubDirectory
   * @see Util#nextZoomMag
   * @see Util#saveCmdHistory
   * @see ClusterGramCanvas#repaint
   * @see #close
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    Button item= (Button)e.getSource();
    
    if (cmd.equals("EP plot"))
    { /* popup EP plots ordered gene list */
      String
      sMsg= "Expression Profiles of 'hierarchical clustered' genes";
      Util.saveCmdHistory("Show list of "+sMsg, false);
      hierClustersExprProfPopup=
      new ShowExprProfilesPopup(mae, orderedCL,
                                null, null, null, 0, false,
                                sMsg,
                                "DG-EP",
                                (PopupRegistry.CUR_GENE | PopupRegistry.FILTER |
                                PopupRegistry.SLIDER | PopupRegistry.UNIQUE),
                                mae.useEPoverlayFlag,
                                false, /* useLargeFrameFlag */
                                false /* just CloneID & name */ );
      mae.repaint();
    }
    
    else if (cmd.equals("ClustGram Report"))
    {
      boolean
        old_addExprProfileFlag= mae.addExprProfileFlag;
        mae.addExprProfileFlag= true;          /* force EP data in TABLE*/
      Util.saveCmdHistory("Create ClusterGram report", false);
      Report rpt= new Report(mae, "ClusterGram report", /* note- it will make title*/
                             mae.RPT_TBL_HIER_CLUSTER,
                             mae.tblFmtMode,
                             "HierClustTbl",
                             (PopupRegistry.CUR_GENE | PopupRegistry.FILTER |
                             PopupRegistry.SLIDER | PopupRegistry.UNIQUE));
      mae.addExprProfileFlag= old_addExprProfileFlag; /* restore it */
    }
    
    else if(cmd.equals("Show HP names"))
    {
      Util.saveCmdHistory("Show list of HP names", false);
      mae.hps.showHP_E_assignmentsPopup();
    }
    
    else if(cmd.equals("SaveAs"))
    { /* Save Canvas as GIF image */
      String
        defGifDir= Util.rmvFinalSubDirectory(mae.defDir,"Report",true),
        oGifFileName= mae.em.promptFileName("Enter GIF file name",
                                            defGifDir,
                                            "maeClusterGramPlot.gif",
                                            null,   /* sub dir */
                                            ".gif",
                                            true,   /* saveMode*/
                                            true    /* useFileDialog */
                                            );
      if(oGifFileName!=null)
      {
        cgC.drawGifFile(oGifFileName);
        Util.saveCmdHistory("Saved ClusterGram as "+oGifFileName, false);
      }
    } /* Save Canvas as GIF image */
    
    else if(zoomDgramButton==item)
    { /* "nX DG" button - zoom in on Dendrogram */
     /* Can magnify lines by 1X, 2X, 5X, 10X, 20X, 50X,
      * 100X, 200X, 500X, 1000X mod.
      * Set range to max of 10X.
      */
      cgC.dGramZoomMag= Util.nextZoomMag(cgC.dGramZoomMag, 100);
      Util.saveCmdHistory("Changed Dendrogram zoom to "+
      cgC.dGramZoomMag+"X", false);
      zoomDgramButton.setLabel(cgC.dGramZoomMag+"X DG");
      /* zoom in on dendrogram */
      cgC.repaint();
    }
    
    else if (cmd.equals("close")) /* close window */
    {
      close();
    }
  } /* actionPerformed */
  
  
  /*
   * itemStateChanged() - handle item state changed events
   * NOTE: need to implement radio groups here since AWT only
   * implements radio groups for Checkboxes, and not for Checkbox MenuItems.
   * @param e is checkbox pressed event
   * @see ClusterGramCanvas#repaint
   * @see Util#saveCmdHistory
   */
  public void itemStateChanged(ItemEvent e)
  { /* itemStateChanged */
    Checkbox item= (Checkbox)e.getSource();
    
    if(dGramCheckBox==item)
    {
      drawDendroGramFlag= dGramCheckBox.getState();
      String
      sMsg= ((drawDendroGramFlag) ? "Display" : "Don't display")+
            " dendrogram";
      Util.saveCmdHistory(sMsg, false);
      cgC.repaint();
    }
  } /* itemStateChanged */
  
  
  /**
   * windowClosing() - closing down the window, get rid of the frame.
   * @param e is window closing event
   * @see ShowPlotPopup#dispose
   */
  public void windowClosing(WindowEvent e)
  { /* windowClosing */
    spp.dispose();
  } /* windowClosing */
  
  
  /**
   * Pass all mouse events down to the Canvas.
   */
  public void mousePressed(MouseEvent e)  { }
  public void mouseDragged( MouseEvent e )  { }
  
  
  /**
   * mouseReleased() - Pass all mouse events down to the Canvas.
   * @param e is button released event
   * @see ClusterGramCanvas#setCurrentGene
   */
  public void mouseReleased(MouseEvent e)
  { /* mouseReleased */
    cgC.setCurrentGene(e.getX(), e.getY(), e.getModifiers(), true);
  } /* mouseReleased */
  
  public void mouseClicked(MouseEvent e)  { }
  public void mouseEntered(MouseEvent e)  { }
  public void mouseExited(MouseEvent e)  { }
  
  
  /**
   * mouseMoved() - process mouse event
   * @param e is button moved event
   */
  public void mouseMoved(MouseEvent e)
  { /* mouseMoved */
    //if(!mae.MouseOver)
    //return;
    // /* Update MouseOver */
    //cgC.setCurrentGene(e.getX(), e.getY(), e.getModifiers(), false);
  } /* mouseMoved */
  
  
  /*Others not used at this time */
  public void windowOpened(WindowEvent e)  { }
  public void windowActivated(WindowEvent e)  { }
  public void windowClosed(WindowEvent e)  { }
  public void windowDeactivated(WindowEvent e)  { }
  public void windowDeiconified(WindowEvent e)  { }
  public void windowIconified(WindowEvent e)  { }
  
  
  /**
   * drawScaleMapAndVerticalLabels() - draw scale map & vert lbls i
   * in the title panel status area
   * @param gGif is graphics context
   */
  void drawScaleMapAndVerticalLabels(Graphics gGif)
  { /* drawScaleMapAndVerticalLabels */
    this.gGif= gGif;            /* save for when paint is called */
    statusCanvas.repaint();
  } /* drawScaleMapAndVerticalLabels */
  
} /* end of DrawClusterGram class */



/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*   #2 class StatusCanvas                                          */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/* This class draws the status window for the ClusterGram
 */

class StatusCanvas extends Canvas
{
  private DrawClusterGram
    dcg;
  public int
    preferredHeight,
    preferredWidth;
  
  /**
   * StatusCanvas () - constructor
   * @param cdg is DrawClusterGram instance
   * @param preferredHeight is preferred height
   * @param preferredWidth is preferred width
   */
  StatusCanvas(DrawClusterGram dcg, int preferredHeight, int preferredWidth)
  { /* StatusCanvas */
    this.dcg= dcg;
    this.preferredHeight= preferredHeight;
    this.preferredWidth= preferredWidth;
  } /* StatusCanvas */
  
  
  /**
   * getPreferredSize() - get the preferred size
   * @return window size
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(new Dimension(preferredWidth, preferredHeight));
  } /* getPreferredSize */
  
  
  /**
   * getMinimumSize() - get the minimumpreferred size
   * @return window size
   */
  public Dimension getMinimumSize()
  { /* getMinimumSize */
    return(new Dimension(preferredWidth, preferredHeight));
  } /* getMinimumSize */
  
  
  /**
   * paint() - draw the ClusterGram status area.
   * @param g is graphics context
   * @see ClusterGramCanvas#drawScaleMap
   * @see ClusterGramCanvas#drawVerticalLabels
   * @see ClusterGramCanvas#setBoxSizes
   */
  public void paint(Graphics g)
  { /* paint */
    /* [1] Clear the backbround */
    g.setColor(Color.white);
    g.fillRect(0,0, preferredWidth, preferredHeight);
    
    /* Set box sizes based on font metrics */
    dcg.cgC.setBoxSizes(g);
    
      /* [2] Draw the X/Y color scale map
       *  Green                 BLACK             RED
       *  <1/8X 1/6X 1/4X 1/2X  1X    2X  4X  6X  >8X
       */
    dcg.cgC.drawScaleMap(g);
    
    /* [3] Draw the vertical labels */
    dcg.cgC.drawVerticalLabels(g);
  } /* paint */
  
  
  
}  /* end of StatusCanvas */


