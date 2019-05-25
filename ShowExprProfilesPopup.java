/** File: ShowExprProfilesPopup.java */
 
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.*;

/**
 * The class pops up a window to display a list of
 * expression profiles for the data filtered genes. 
 *<PRE>
 * There are two ways to display the list of genes: 
 *    (1) a scrollable list of Expression Profile plots, 
 *    (2) an overlay expression profile plot.
 * </PRE>
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.9 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
class ShowExprProfilesPopup extends Frame 
	     implements ActionListener, ItemListener, WindowListener 
{
  MAExplorer
    mae;                        /** link to global instance */
  String 
    popupName;                  /** ARG: for PopupRegistry */
  int 
    popupPropertyBits;          /** ARG: for PopupRegistry */
  String
    title;
  
  /** Overlay EP plots */
  ExprProfileOverlay
    epov;   
  /** scrollable panel of ExprProfilePanel's */
  ExprProfileScrollPane
    epsp;                  
  
  /** Scrollable panel */
  ScrollPane
    epPane;			
  /** Grid panel */
  Panel
    gp;				
  /**  Master GeneList being used */
  GeneList 
    mlToUse;	
  /** # of genes in expression profile */
  int
    nGenes;
  /** this frame size computed */
  int
    frameWidth,                 
    frameHeight;
  Label
    epspStatusLabel;
  Checkbox
    mouseOverCheckBox,
    showErrBarsCheckBox;
  Button
    closeButton,
    plotStyleButton,
    zoomBarsButton,
    showEGLButton,
    showFilteredLinesButton,
    saveAsButton;
  /** Flag allowing the plotting of error bars */
  boolean 
    showErrBarsFlag;
  /** Flag indicating that mean values should be plotted */
  boolean 
    hasMeanPlotsFlag;
  /** Mode for plotting points in the expr profile 
   * where:  0=(bar) line, 1=circle, 2=curve */
  int
    plotStyleMode;  
  /** data magnification 1X, 2X, 5X, 10X, 20X */ 
  int
    zoomBarsMag;
  /** opt. # data @ each gene avg */
  int
    hpDataNbrA[];  
  /** opt. Mean data @ each HP sample */
  float
    hpDataMnA[][];   
  /** opt.S.D. @ each HP sample */
  float
    hpDataSDA[][];   
  /**  # of mean nodes */
  int
    nMnGenes;                  		
  boolean
    showEGLlinesFlag= false;
  boolean
    showFilteredLinesFlag= false;
  
  /**
   * ShowExprProfilesPopup() - Constructor. Create popup grid of expression profile plots.
   * Set the  Filter.workingCL if the specified list is null.
   * @param mae is the MAExplorer instance
   * @param mlToUse is the GeneList instance to use. If null, use fc.workingCL
   * @param hpDataNbrA is the  opt. # data @ each gene average
   * @param hpDataMnA is the opt. Mean data @ each HP sample
   * @param hpDataSDA is the opt.S.D. @ each HP sample
   * @param nMnGenes is the # of mean nodes
   * @param hasMeanPlotsFlag to compute the means for each sample
   * @param title of plot
   * @param popupName is the name for PopupRegistry
   * @param popupPropertyBits are the property bits for PopupRegistry
   * @param useEPoverlayFlag to display overlay 2Dplot else scrollable Grid of EPs
   * @param useLargeFrameFlag if need it for more samples
   * @param showKmeansDataFlag /* report Kmeans data else just CloneID and name
   * @see ExprProfileOverlay
   * @see ExprProfileOverlay#repaint
   * @see ExprProfileOverlay#setEPcanvasDrawingOptions
   * @see ExprProfileOverlay#setPlotEPmeansData
   * @see ExprProfileOverlay#updateCurGene
   * @see ExprProfileScrollPane
   * @see ExprProfileScrollPane#repaint
   * @see ExprProfileScrollPane#setEPcanvasDrawingOptions
   * @see ExprProfileScrollPane#setPlotEPmeansData
   * @see ExprProfileScrollPane#updateCurGene
   * @see Filter#showNbrFilteredGenes
   * @see PopupRegistry#addPopupWindowToReg
   * @see PopupRegistry#removePopupByKey
   * @see Util#showMsg
   * @see Util#showMsg3
   */
  ShowExprProfilesPopup(MAExplorer mae, GeneList mlToUse, int hpDataNbrA[],
                        float hpDataMnA[][], float hpDataSDA[][],
                        int nMnGenes, boolean hasMeanPlotsFlag, String title,
                        String popupName, int popupPropertyBits,
                        boolean useEPoverlayFlag, boolean useLargeFrameFlag,
                        boolean showKmeansDataFlag)
  { /* ShowExprProfilesPopup */
    super("ShowExprProfilesPopup");
    
    this.mae= mae;
    this.popupName= popupName;
    this.popupPropertyBits= popupPropertyBits;
    
    /* Register this window with the popup registry */
    if((popupPropertyBits & mae.pur.UNIQUE)!=0)
      mae.pur.removePopupByKey(popupName);
    mae.pur.addPopupWindowToReg(this, popupName, popupPropertyBits);
    
    /* Setup the GeneList to use */
    this.mlToUse= (mlToUse!=null) ? mlToUse : mae.fc.workingCL;
    
    this.hpDataNbrA= hpDataNbrA;
    this.hpDataMnA= hpDataMnA;
    this.hpDataSDA= hpDataSDA;
    this.nMnGenes= nMnGenes;
    
    this.title= title;
    
    this.hasMeanPlotsFlag= hasMeanPlotsFlag;
    showErrBarsFlag= true;
    plotStyleMode= 0;                 /*. ie "line" */
    zoomBarsMag= 1;
    
    nGenes= mlToUse.length;
    if(nGenes==0)
    {
      Util.showMsg("Can't create expression profiles if no Filtered genes.");
      Util.popupAlertMsg("Can't create expression profiles",
                         "You can't create expression profiles if no Filtered genes.",
                         4, 80);
      return;
    }
    
    /* Estimate the width based on # of samples.
     * Should estimate space if using gene labels.
     */
    if(!useLargeFrameFlag && !showKmeansDataFlag)
    {
      int mcw= ExprProfileCanvas.MIN_CANVAS_WIDTH;
      frameWidth= (mcw+25) + ((mcw-25)*mae.hps.nHP_E)/50;
    }
    else
      frameWidth= (int)(2.1*ExprProfileCanvas.MIN_CANVAS_WIDTH);
    frameHeight= 646;   /* was 625 */
    
    if(useEPoverlayFlag)
    {
      frameWidth= 524;
      frameHeight= 465;
    }
    
    int preferredPanelWidth= frameWidth/2;
    
    this.setSize(frameWidth,frameHeight);
    
    /* Create scrollable grid of ExprProfilePanels' */
    epspStatusLabel= new Label(
                 "                                                      ");
    this.add(epspStatusLabel,"North");
    
    /* Create scrollable grid of ExprProfilePanels' */
    if(useEPoverlayFlag)
    { /* overlay plot of EP plots*/
      epov= new ExprProfileOverlay(mae, this, mlToUse, title,
      preferredPanelWidth,
      showKmeansDataFlag);
      this.add(epov,"Center");
    }
    else
    { /* scrollable list of EP plots */
      epsp= new ExprProfileScrollPane(mae, this, mlToUse, title,
      preferredPanelWidth,
      showKmeansDataFlag);
      this.add(epsp,"Center");
    }
    
    /* If enabled, set the mean and SD data into each panel */
    if(epsp!=null)
      epsp.setEPcanvasDrawingOptions(showErrBarsFlag,zoomBarsMag,
                                     plotStyleMode);
    else if(epov!=null)
      epov.setEPcanvasDrawingOptions(showErrBarsFlag,zoomBarsMag,
                                     plotStyleMode);
    
    if(nMnGenes>0)
    {
      if(epsp!=null)
        epsp.setPlotEPmeansData(nMnGenes, hpDataNbrA, hpDataMnA, hpDataSDA,
                                hasMeanPlotsFlag /* doMeanPlotsFlag */);
      else if(epov!=null)
        epov.setPlotEPmeansData(nMnGenes, hpDataNbrA, hpDataMnA, hpDataSDA,
                                hasMeanPlotsFlag /* doMeanPlotsFlag */);
    }
    
    Util.showMsg3("Building the plots...", Color.white, Color.red);
    
    /* Create a bottom panel to hold a couple of buttons  */
    Panel p= new Panel();
    p.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
    this.add(p, "South");
    
    /* Create the buttons and arrange to handle button clicks */
    Font buttonFont= new Font(mae.cfg.fontFamily, Font.PLAIN /*BOLD*/, 14);
    
    /* Always have the Close button */
    if(!useEPoverlayFlag)
    { /* Scrollable list of EP plots */
      showErrBarsCheckBox= new Checkbox("Err", showErrBarsFlag);
      showErrBarsCheckBox.addItemListener(this);
      p.add(showErrBarsCheckBox);
      
      zoomBarsButton= new Button(" 1X");
      zoomBarsButton.addActionListener(this);
      p.add(zoomBarsButton);
      
      plotStyleButton= new Button("Line  ");
      plotStyleButton.addActionListener(this);
      p.add(plotStyleButton);
    }
    
    else
    { /* using EP overlay */
      mouseOverCheckBox= new Checkbox("Mouse info",
      mae.useMouseOverFlag);
      mouseOverCheckBox.addItemListener(this);
      p.add(mouseOverCheckBox);
      
      showEGLButton= new Button("Show EGL       ");
      showEGLButton.addActionListener(this);
      p.add(showEGLButton);
      
      showFilteredLinesButton= new Button("Show Filtered       ");
      showFilteredLinesButton.addActionListener(this);
      p.add(showFilteredLinesButton);
      
      if(epov!=null && !mae.isAppletFlag)
      { /* only if stand-alone */
        saveAsButton= new Button("SaveAs");
        saveAsButton.addActionListener(this);
        saveAsButton.setFont(buttonFont);
        p.add(saveAsButton);
      }
    } /* using EP overlay */
    
    Button showHPnames= new Button("HP names");
    showHPnames.addActionListener(this);
    showHPnames.setFont(buttonFont);
    p.add(showHPnames);
    
    closeButton= new Button("Close");
    closeButton.addActionListener(this);
    closeButton.setFont(buttonFont);
    p.add(closeButton);
    
    this.pack();
    
    this.addWindowListener(this);   /* listener for window events */
    this.setTitle(title);
    
    /* Center frame on the screen, PC only */
    Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
    Point pos= new Point((screen.width - this.getSize().width/2)/2,
                         (screen.height - this.getSize().height)/2);
    this.setLocation(pos);
    this.setVisible(true);
    
    if(epsp!=null)
    {
      epsp.repaint();
      /* update the current gene in the EP plot list */
      if(mlToUse.mList[0]!=null)
        epsp.updateCurGene(mlToUse.mList[0].mid);  /* first in list */
    }
    else if(epov!=null)
    {
      epov.repaint();
      /* update the current gene in the EP plot list */
      if(mlToUse.mList[0]!=null)
       epov.updateCurGene(mlToUse.mList[0].mid);  /* first in list */
    }
    
    /* Update Msg3 w/# genes passing filter */
    mae.fc.showNbrFilteredGenes();
  } /* ShowExprProfilesPopup */
  
  
  /**
   * getPreferredSize() - get the preferred size
   * @return preferred size as a Dimension instance
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(new Dimension(frameWidth, frameHeight));
  } /* getPreferredSize */
  
  
  /**
   * paint() - repaint by causes the ExprProfileScrollPane to repaint.
   * @param g is the Graphics instance
   * @see ExprProfileOverlay#repaint
   * @see ExprProfileScrollPane#repaint
   */
  public void paint(Graphics g)
  { /* paint */
    /* if(mae.CONSOLE_FLAG)
    mae.fio.logMsgln("SEPP-Paint nGenes="+nGenes);
    */
    if(epsp!=null)
      epsp.repaint();
    else if(epov!=null)
      epov.repaint();
  } /* paint */
  
  
  /**
   * setPlotEPclusterMeansData() - set expression profile to plot Cluster means data
   * @param doMeanPlotsFlag compute expression profile means else no-op
   * @see ExprProfileOverlay#setPlotEPmeansData
   * @see ExprProfileScrollPane#setPlotEPmeansData
   */
  void setPlotEPclusterMeansData(boolean doMeanPlotsFlag)
  { /* setPlotEPclusterMeansData */
    if(!hasMeanPlotsFlag)
      return;
    
    ClusterGenes cg= mae.clg;
    if(epsp!=null)
      epsp.setPlotEPmeansData(cg.nKmeansNodes, cg.hpDataNbrA,
                              cg.hpDataMnA, cg.hpDataSDA, doMeanPlotsFlag);
    else if(epov!=null)
      epov.setPlotEPmeansData(cg.nKmeansNodes, cg.hpDataNbrA,
                              cg.hpDataMnA, cg.hpDataSDA, doMeanPlotsFlag);
  } /* setPlotEPclusterMeansData */
  
  
  /**
   * updateEPplots() - update expression profile plots using new GeneList.
   * Use the Filter.workingCL if the specified list is null.
   * @param mlToUse if specified, else use fc.workingCL
   */
  void updateEPplots(GeneList mlToUse)
  { /* updateEPplots */
    this.mlToUse= (mlToUse!=null) ? mlToUse : mae.fc.workingCL;
    nGenes= mlToUse.length;
    
    /* [TODO] redo the list */
  } /* updateEPplots */
  
  
  /**
   * close() - close this popup and reset flags if needed
   * @param keepFlags (ignored for now)
   */
  void close(boolean keepFlags)
  { /* close */
    this.dispose();
  } /* close */
  
  
  /**
   * actionPerformed() - Handle button clicks
   * @param e is ActionEvent for buttons in control panel
   * @see EventMenu#promptFileName
   * @see ExprProfileOverlay#drawGifFile
   * @see ExprProfileOverlay#repaint
   * @see ExprProfileOverlay#setEPcanvasDrawingOptions
   * @see ExprProfileScrollPane#setEPcanvasDrawingOptions
   * @see SampleSets#showHP_E_assignmentsPopup
   * @see Util#nextZoomMag
   * @see Util#rmvFinalSubDirectory
   * @see Util#saveCmdHistory
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    Button item= (Button)e.getSource();
    
    if (cmd.equals("Close")) /* close window */
      close(false);
    
    else if(cmd.equals("HP names"))
      mae.hps.showHP_E_assignmentsPopup();
    
    else if(zoomBarsButton==item)
    { /* zoom lines by 1X, 2X, 5X, 10X, 20X mod */
      zoomBarsMag= Util.nextZoomMag(zoomBarsMag, 20);
      zoomBarsButton.setLabel(zoomBarsMag+"X");
      Util.saveCmdHistory("Expression Profile zoom set to ["+zoomBarsMag+"]",
                          false);
      if(epsp!=null)
        epsp.setEPcanvasDrawingOptions(showErrBarsFlag,zoomBarsMag,plotStyleMode);
      else if(epov!=null)
        epov.setEPcanvasDrawingOptions(showErrBarsFlag,zoomBarsMag,plotStyleMode);
    }
    
    else if(showEGLButton==item)
    { /* toggle showing EGL lines and not for EP overlay plot */
      String label= (showEGLlinesFlag) ? "Show EGL" : "Don't show EGL";
      Util.saveCmdHistory(label+ "genes", false);
      showEGLlinesFlag= !showEGLlinesFlag;
      showEGLButton.setLabel(label);
      epov.repaint();
    }
    
    else if(showFilteredLinesButton==item)
    { /* toggle showing Filtered lines and not for EP overlay plot */
      String label= (showFilteredLinesFlag) ? "Show Filtered" : "Don't show Filtered";
      Util.saveCmdHistory(label+ "genes", false);
      showFilteredLinesFlag= !showFilteredLinesFlag;
      showFilteredLinesButton.setLabel(label);
      epov.repaint();
    }
    
    else if(plotStyleButton==item)
    { /* zoom lines by 1X, 2X, 5X, 10X, 20X mod */
      plotStyleMode= (plotStyleMode+1)%3;
      String label= ((plotStyleMode==0) 
                         ? "Line"  : ((plotStyleMode==1)
                                         ? "Circle" : "Curve"));
      Util.saveCmdHistory("Plot style set to ["+label+"]", false);
      plotStyleButton.setLabel(label);
      if(epsp!=null)
        epsp.setEPcanvasDrawingOptions(showErrBarsFlag,zoomBarsMag, plotStyleMode);
      else if(epov!=null)
        epov.setEPcanvasDrawingOptions(showErrBarsFlag,zoomBarsMag, plotStyleMode);
    } /* zoom lines by 1X, 2X, 5X, 10X, 20X mod */
    
    else if(cmd.equals("SaveAs"))
    { /* Save plot as GIF image */
      String
        defGifDir= Util.rmvFinalSubDirectory(mae.defDir,"Report", true),
        oGifFileName= mae.em.promptFileName("Enter GIF file name", defGifDir,
                                            "maeEPoverlayPlot.gif",
                                            null,   /* sub dir */
                                            ".gif",
                                            true,   /* saveMode*/
                                            true    /* useFileDialog */
                                            );
      if(oGifFileName!=null)
        epov.drawGifFile(oGifFileName);
      Util.saveCmdHistory("Saved Expression Profile plot as "+oGifFileName, false);
    } /* Save plot as GIF image */
  } /* actionPerformed */
  
  
  /*
   * itemStateChanged() - handle check box item state changed events
   * NOTE: need to implement radio groups here since AWT only
   * implements radio groups for Checkboxes, and not for Checkbox MenuItems.
   * @param e is ItemEvent for checkboxes in control panel
   * @see ExprProfileOverlay#setEPcanvasDrawingOptions
   * @see ExprProfileScrollPane#setEPcanvasDrawingOptions
   */
  public void itemStateChanged(ItemEvent e)
  { /* itemStateChanged */
    Checkbox item= (Checkbox)e.getSource();
    
    if(mouseOverCheckBox==item)
    {
      mae.useMouseOverFlag= item.getState();
      mae.mbf.miVMuseMouseOver.setState(mae.useMouseOverFlag);
    }
    
    else if(showErrBarsCheckBox==item)
    {
      showErrBarsFlag= item.getState();
      if(epsp!=null)
        epsp.setEPcanvasDrawingOptions(showErrBarsFlag,zoomBarsMag,
        plotStyleMode);
      else if(epov!=null)
        epov.setEPcanvasDrawingOptions(showErrBarsFlag,zoomBarsMag,
        plotStyleMode);
    }
  } /* itemStateChanged */
  
  
  /**
   * windowClosing() - closing down the window, get rid of the frame.
   * @param e is window closing event
   */
  public void windowClosing(WindowEvent e)
  {
    epsp= null;
    epov= null;
    this.dispose();
  }
  
  
  /*Others not used at this time */
  public void windowOpened(WindowEvent e)  { }
  public void windowActivated(WindowEvent e)  { }
  public void windowClosed(WindowEvent e)  { }
  public void windowDeactivated(WindowEvent e)  { }
  public void windowDeiconified(WindowEvent e)  { }
  public void windowIconified(WindowEvent e)  { }
  
  
  /**
   * updateCurGene() - update expression profile plot list if current gene changed
   * and this type of string report requires it.
   * @param mid is the new Master Gene ID of the current gene
   * @see ExprProfileOverlay#updateCurGene
   * @see ExprProfileScrollPane#updateCurGene
   */
  void updateCurGene(int mid)
  { /* updateCurGene */
    if(epsp!=null)
      epsp.updateCurGene(mid);
    else if(epov!=null)
      epov.updateCurGene(mid);
  } /* updateCurGene */
  
  
  /**
   * updateFilter() - update expression profile plots using new GeneList ml.
   * Use the Filter.workingCL if the specified list is null.
   * @param ml is the gene list to update now that filter has changed.
   * @see ExprProfileOverlay#updateData
   * @see ExprProfileScrollPane#updateData
   */
  void updateFilter(GeneList ml)
  { /* updateFilter */
    mlToUse= (ml!=null) ? ml : mae.fc.workingCL;
    if(epsp!=null)
      epsp.updateData(mlToUse, title);
    else if(epov!=null)
      epov.updateData(mlToUse, title);
  } /* updateFilter */
  
  
  /**
   * updateSlider() - update expression profile plot list if Slider changed
   * and Filter.workingCL is the current EP list.
   * @see ExprProfileOverlay#updateData
   * @see ExprProfileScrollPane#updateData
   */
  void updateSlider()
  { /* updateSlider */
    if(mlToUse==mae.fc.workingCL)
    {
      if(epsp!=null)
        epsp.updateData(mlToUse, title);
      else if(epov!=null)
        epov.updateData(mlToUse, title);
    }
  } /* updateSlider */
  
  
  /**
   * updateLabels() - update string report if labels changed
   * and this type of plot requires it.
   */
  void updateLabels()
  { /* updateLabels */
  } /* updateLabels */
  
} /* end of ShowExprProfilesPopup class */

