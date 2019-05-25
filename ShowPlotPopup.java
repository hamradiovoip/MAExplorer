/** File: ShowPlotPopup.java  */

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.lang.*;
import java.lang.String;
import java.io.FileReader;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.event.AdjustmentEvent;

/** 
 * The class popups a plot window containing one of several types of plots.
 * The plot type is determined by the plotMode parameter and include 
 * scatter plots, intensity histograms, ratio histograms, clustergrams, etc. 
 * It includes a variable set of control buttons on the bottom depending 
 * on what type of plot is being presented.
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
 
class ShowPlotPopup extends Frame 
       implements AdjustmentListener, ActionListener, MouseListener,
       ItemListener, WindowListener
{
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;                        
    
  /** Threshold button tests --- */
  /** threshold Button tests "= " */
  final static int
    TH_EQ= 1;                   
  /** threshold Button tests "< " */
  final static int
    TH_LT= 2;
  /** threshold Button tests "> " */
  final static int                  
    TH_GT= 3;                   
  /** threshold Button tests "<>" */
  final static int
    TH_IN= 4;                   
  /** threshold Button tests "><" */
  final static int
    TH_OUT= 5;                  
  /** default frame size width of popup */
  final static int
    POPUP_WIDTH= 500;           
  /** default frame size height of popup */
  final static int
    POPUP_HEIGHT= 525;
    
  /** ARG: title of popup plot */
  String
    title;	
    
  /** Plot mode: one of the mae.PLOT_xxxx values */		
  int
    plotMode;
  
  /** working frame size width */		
  int			
    frameWidth= POPUP_WIDTH;    
  /** working frame size neight */	
  int			
    frameHeight= POPUP_HEIGHT;
    
  /** ARG: popup name for PopupRegistry */
  String 
    popupName;
  /** ARG: property bius for PopupRegistry */                  
  int 
    popupPropertyBits;          
  
  /** cell label font */       
  private Font   
    buttonFont;                 
    
  /* --- GUI for popup window --- */
  /** Panel that holds scrollable canvas*/
  private Panel
    sPanel;          
    
  /** vertical LowerBound scrollbar for canvas */           
  private Scrollbar
    vertLBscr;
  /** vertical UpperBound scrollbar for canvas */           
  private Scrollbar
    vertUBscr;
  /** horizontal LowerBound scrollbar for canvas */           
  private Scrollbar
    horizLBscr;
  /** horizontal UpperBound scrollbar for canvas */           
  private Scrollbar
    horizUBscr;
    
  /** Checkbox to toggle Show "Mouse-over info" */
  private Checkbox
    mouseOverCheckBox;          
    
  /* --- optional button list - not all buttons are used for all types
   * of plots.
   */
  /** close window button */ 
  private Button
    closeButton;
  /** histogram (intens/ratio) bin range button */  
  private Button
    histBinRangeButton;
  /** reFilterHist/NoreFilterHist button */ 
  private Button 
    reFilterHistButton;   
  /** [DEPRICATED] Linear/Log button */ 
  private Button    
    scalingButton;
  /** non-Filtered genes button*/ 
  private Button 
    nonFilteredGenesButton;
  /** "SaveAs" GIF image button */ 
  private Button
    saveAsButton; 
    
  /** Histogram (intens/ratio) bin range button. This button will 
   * contain values: "= ", "< ", "> ", "<>", "><" 
   */                 
  String
    histBinRange;               
  /** Histogram Bind Mode using 
   * TH_xx: "= " is 1, "< " is 2, "> " is 3, "<>" is 4, "><" is 5 
   */       
  int
    histBinMode;                
    
  /** number of prewired scroller steps */
  private int
    maxStepsScr= 1000;
  /** scrollbar's 'visible' amount */
  private int
    visibleScr= 1;
  
  /** flag: toggle if "><" hist range */
  boolean
    outsideRangeFlag= false;
  /** flag: toggle with reFilterHist/NoreFilterHist button */
  boolean
    reFilterHistFlag= false; 
  /** flag: toggle with Linear/Log button */ 
  boolean
    logScalingFlag= false;      
  /** flag: toggle non-Filtered genes button*/
  boolean
    showNonFilteredGenesFlag= true; 
   
  /** default output .gif file name */
  String
    defGifFile; 
    
  /* --- Note: in an ShowPlotPopup instance, only 1 of following is not null! --- */  
  /** instance of DrawScatterPlot if drawing a scatter plot */
  DrawScatterPlot
    dwSP;
  /** instance of DrawHistogram if drawing a scatter plot */  
  DrawHistogram
    dwH;
  /** instance of DrawRatioHistogram if drawing a scatter plot */  
  DrawRatioHistogram
    dwRH;
  /** instance of DrawClusterGram if drawing a scatter plot */
  DrawClusterGram
    dwCG;     
 
  
  /**
   * ShowPlotPopup() - constructor to create a spreadsheet from raw data
   * with URL spec. use same name for URL as in the cell.
   * @param mae is the MAExplorer instance
   * @param plotMode is the mae.PLOT_xxxx state
   * @param title is the title of plot
   * @param popupName is the name for the PopupRegistry
   * @param popupPropertyBits are the property bits for the PopupRegistry
   * @param frmOffset is the frame offset in pixels
   * @see PopupRegistry#addPopupWindowToReg
   * @see PopupRegistry#removePopupByKey
   * @see #createPopupPlot
   */
  ShowPlotPopup(MAExplorer mae, int plotMode, String title, String popupName,
                int popupPropertyBits, int frmOffset )
  { /* ShowPlotPopup */
    this.mae= mae;
    
    this.plotMode= plotMode;
    if(title!=null)
      this.title= title;
    else
      this.title="Plot";
    this.popupName= popupName;
    this.popupPropertyBits= popupPropertyBits;
    
    if((popupPropertyBits & mae.pur.UNIQUE)!=0)
      mae.pur.removePopupByKey(popupName);
    mae.pur.addPopupWindowToReg(this, popupName, popupPropertyBits);
    
    createPopupPlot();              /* Go create the plot */
    
    this.setSize(frameWidth,frameHeight);
    this.addWindowListener(this);   /* listener for window events */
    
    this.setTitle(title);
    
    /* Center frame on the screen, PC only */
    Dimension scr= Toolkit.getDefaultToolkit().getScreenSize();
    Point pos= new Point(frmOffset+((scr.width-this.getSize().width)/2),
                         frmOffset+((scr.height-this.getSize().height)/2));
    this.setLocation(pos);
    this.setVisible(true);
  } /* ShowPlotPopup */
  
  
  /**
   * createPopupPlot() - create the popup plot
   * @return false if there is problem.
   * @see DrawClusterGram
   * @see DrawHistogram
   * @see DrawRatioHistogram
   * @see DrawScatterPlot
   * @see SampleSets#setHPxyModStrings
   * @see #updateScaling
   */
  private boolean createPopupPlot()
  { /* createPopupPlot */
    boolean
      allowSaveAsFlag= false,
      allowreFilterHist= false,
      allowScaling= false,
      scatterPlotStuff= false;
    
    /* Setup mae.hps.(sMod, sModX, sModY) based on whether using
     * ratio mode or swapCy5Cy3DataFlags
     */
    mae.hps.setHPxyModStrings();
    
    String
      sf1= mae.cfg.fluoresLbl1,
      sf2= mae.cfg.fluoresLbl2;
    if(mae.useCy5OverCy3Flag)
    { /* flip Cy3/Cy5 to Cy5/Cy3 */
      String sTmp= sf1;
      sf1= sf2;
      sf2= sTmp;
    }
    
    Panel
      titlePanel= new Panel(),
      controlPanel= new Panel();          /* close button only */
    
    /* Add components to frame */
    this.setLayout(new BorderLayout());
    titlePanel.setLayout(new BorderLayout());
    titlePanel.add("North", new Label("    " + title));
    this.add("North", titlePanel);
    
    /* Configure controlPanel layout */
    controlPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
    //controlPanel.setLayout(new GridLayout(4,0,3,4));
    
    /* Create plot panel */
    sPanel= new Panel();            /* panel holding scrollable canvas*/
    sPanel.setLayout(new BorderLayout(1,1)); /* make scrollbars flush to canvas */
    this.add("Center", sPanel);
    
    /* Histogram (intens/ratio) bin range button
     * This button will contain values: "= ", "< ", "> ", "<>", "><"
     */
    histBinRange= "Thr: = ";
    histBinMode= TH_EQ;
    histBinRangeButton= new Button(histBinRange);
    histBinRangeButton.addActionListener(this);
    histBinRangeButton.setFont(buttonFont);
    outsideRangeFlag= false;
    /* possibly add() for this button below for anyone who needs it. */
    
    if(plotMode==mae.PLOT_F1_F2_INTENS || plotMode==mae.PLOT_F1_F2_MVSA ||
       plotMode==mae.PLOT_HP_XY_INTENS)
    { /*Only add Vertical scrollers if particular plot can use them. */
      scatterPlotStuff= true;
      /* Always have the Close button */
      
      Panel vertPanel= new Panel();
      
      vertPanel.setLayout(new GridLayout(2,1, /*R,C*/ 1,1 /*gap*/));
      sPanel.add("West", vertPanel);
      
      vertLBscr= new Scrollbar(Scrollbar.VERTICAL, maxStepsScr+visibleScr, /* val*/
                               visibleScr, 0, maxStepsScr+visibleScr);
      vertUBscr= new Scrollbar(Scrollbar.VERTICAL, 0, /* val*/ visibleScr,
                               0, maxStepsScr+visibleScr);
      vertPanel.add(vertUBscr);
      vertPanel.add(vertLBscr);
      vertLBscr.addAdjustmentListener(this);
      vertUBscr.addAdjustmentListener(this);
    }
    
    /* Histogram horizontal controls */
    if(plotMode==mae.PLOT_F1_F2_INTENS || plotMode==mae.PLOT_F1_F2_MVSA ||
       plotMode==mae.PLOT_HP_XY_INTENS ||
       (mae.LECBdebug==0 && plotMode==mae.PLOT_INTENS_HIST) )
    { /*Only add Horiz scrollers if particular plot can use them. */
      Panel horizPanel= new Panel();
      horizPanel.setLayout(new GridLayout(1,2, /*R,C*/ 1,1 /*gap*/));
      sPanel.add("South", horizPanel);
     
      horizLBscr= new Scrollbar(Scrollbar.HORIZONTAL, 0,
                                visibleScr, 0, maxStepsScr+visibleScr);
      horizUBscr= new Scrollbar(Scrollbar.HORIZONTAL, maxStepsScr+visibleScr,
                                visibleScr, 0, maxStepsScr+visibleScr);
      horizPanel.add(horizLBscr);
      horizPanel.add(horizUBscr);
      horizLBscr.addAdjustmentListener(this);
      horizUBscr.addAdjustmentListener(this);
    }
    
    switch(plotMode)
    { /* do specific plot */
      case MAExplorer.PLOT_F1_F2_INTENS:
      case MAExplorer.PLOT_F1_F2_MVSA:
      case MAExplorer.PLOT_HP_XY_INTENS:
        dwSP= new DrawScatterPlot(mae,this,plotMode);
        /* NOTE: the MouseListener is added in the Draw2Dplot subclass */
        sPanel.add("Center", dwSP);
        allowScaling= true;
        allowSaveAsFlag= true;
        defGifFile= (plotMode==MAExplorer.PLOT_HP_XY_INTENS)
                        ? "maeXYplot.gif" 
                        : (plotMode==MAExplorer.PLOT_F1_F2_MVSA)
                            ? "maeMvsAplot.gif" 
                            : "maeF1F2plot.gif";
        break;
        
      case MAExplorer.PLOT_INTENS_HIST:
        String
          changeStr= (mae.isZscoreFlag)
                        ? "("+sf1+"-"+sf2+") Zscore"
                        : "("+sf1+"/"+sf2+") ratio",
          ss= (mae.useRatioDataFlag) ? changeStr : "intensity",
          subTitle= "Spot data "+ss+" histogram ["+ mae.ms.fullStageText +"]",
          mainTitle= subTitle + ((mae.bkgdCorrectFlag)
                                    ? " less background" : "");
        allowreFilterHist= true;
        allowSaveAsFlag= true;
        defGifFile= "maeIntensHistPlot.gif";
        
        dwH= new DrawHistogram(mae,this,mainTitle, ss, mae.PLOT_INTENS_HIST);
        dwH.addMouseListener(this);
        sPanel.add("Center", dwH);
        break;
        
      case MAExplorer.PLOT_HIST_F1F2_RATIO:
      case MAExplorer.PLOT_HIST_HP_XY_RATIO:
      case MAExplorer.PLOT_HIST_HP_XY_SETS_RATIO:
        allowreFilterHist= true;
        allowSaveAsFlag= true;
        if(plotMode==MAExplorer.PLOT_HIST_F1F2_RATIO)
          defGifFile= "maeXYratioF1F2HistPlot.gif";
        else if(plotMode==MAExplorer.PLOT_HIST_HP_XY_RATIO)
          defGifFile= "maeXYratioHistPlot.gif";
        else if(plotMode==MAExplorer.PLOT_HIST_HP_XY_SETS_RATIO)
          defGifFile= "maeXYratioSetsHistPlot.gif";
        dwRH= new DrawRatioHistogram(mae,this,plotMode);
        dwRH.addMouseListener(this);
        sPanel.add("Center", dwRH);
        break;
        
      case MAExplorer.PLOT_KMEANS_CLUSTERGRAM:
        ClusterGenes.curClusterCL= mae.fc.KmeansNodesCL;
      case MAExplorer.PLOT_CLUSTERGRAM:
        dwCG= new DrawClusterGram(mae, this, titlePanel,
                                  ClusterGenes.hcn,
                                  ClusterGenes.curClusterCL,
                                  title, plotMode, false);
        dwCG.addMouseListener(this);
        sPanel.add("Center", dwCG);
        dwCG.addButtonsToParent(this, controlPanel, buttonFont);
        //Dimension
        //  frameSize= dwCG.getPreferredSize();
        //frameWidth= frameSize.width;
        //frameHeight= frameSize.height;
        frameWidth= mae.PSEUDOIMG_WIDTH+100;   /* was 600 */
        frameHeight=  mae.PSEUDOIMG_WIDTH;  /* was 600 */
        break;
        
      default:
        //mae.fio.logMsgln("SPP-CPP ["+popupName+"] bad plotMode="+plotMode);
        return(false);
    } /* do specific plot */
    
    /* Create special buttons & arrange to handle button clicks */
    if(scatterPlotStuff)
    {
      mouseOverCheckBox= new Checkbox("Mouse-over info",
      mae.useMouseOverFlag);
      mouseOverCheckBox.addItemListener(this);
      controlPanel.add(mouseOverCheckBox);
    }
    
    if(allowScaling)
    { /* Linear/Log button */
      //scalingButton= new Button("Linear");
      //scalingButton.addActionListener(this);
      //scalingButton.setFont(buttonFont);
      //controlPanel.add("Center", scalingButton);
      
      nonFilteredGenesButton= new Button("Filtered genes");
      nonFilteredGenesButton.addActionListener(this);
      nonFilteredGenesButton.setFont(buttonFont);
      controlPanel.add("Center", nonFilteredGenesButton);
    }
    
    if(allowreFilterHist)
    { /* Redraw/Don't Redraw button */
      reFilterHistButton= new Button("Don't re-Filter");
      reFilterHistButton.addActionListener(this);
      reFilterHistButton.setActionCommand("NoReFilterHist");
      reFilterHistButton.setFont(buttonFont);
      controlPanel.add("Center", reFilterHistButton);
    }
    
    if(allowSaveAsFlag && !mae.isAppletFlag)
    { /* only if stand-alone */
      saveAsButton= new Button("SaveAs");
      saveAsButton.addActionListener(this);
      saveAsButton.setFont(buttonFont);
      
      if(! popupName.equals("ALERT-MSG"))
        controlPanel.add("Center", saveAsButton); /* only if NOT alert msg */
    }
    
    if(dwRH!=null || dwH!=null)
      controlPanel.add("Center", histBinRangeButton);
    
    closeButton= new Button("Close");     /* close window button */
    closeButton.addActionListener(this);
    closeButton.setFont(buttonFont);
    controlPanel.add("Center", closeButton);
    
    this.add("South", controlPanel);     /* Seems to work best this way */
    
    /* Realize it */
    updateScaling();         /* force state */
    
    this.pack();
    this.setSize(frameWidth, frameHeight);
    this.setVisible(true);
    
    return(true);
  } /* createPopupPlot */
  
  
  /**
   * updatePlot() - update the popup plot
   * @return false if there is problem.
   * @see DrawClusterGram#updateCGplot
   * @see DrawHistogram#updateHistogramPlot
   * @see DrawRatioHistogram#updateRatioHistogramPlot
   * @see DrawScatterPlot#updateScatterPlot
   */
  private boolean updatePlot()
  { /* updatePlot */
    
    /* Update plot panel */
    switch(plotMode)
    {
      case MAExplorer.PLOT_F1_F2_INTENS: 
      case MAExplorer.PLOT_F1_F2_MVSA:
      case MAExplorer.PLOT_HP_XY_INTENS:
        dwSP.updateScatterPlot();
        break;
        
      case MAExplorer.PLOT_INTENS_HIST:
        dwH.updateHistogramPlot();
        break;
        
      case MAExplorer.PLOT_HIST_F1F2_RATIO:
      case MAExplorer.PLOT_HIST_HP_XY_RATIO:
        dwRH.updateRatioHistogramPlot();
        break;
        
      case MAExplorer.PLOT_KMEANS_CLUSTERGRAM:
      case MAExplorer.PLOT_CLUSTERGRAM:
        dwCG.updateCGplot(ClusterGenes.curClusterCL, ClusterGenes.hcn);
        break;
        
      default:
        return(false);
    }
    
    return(true);
  } /* updatePlot */
  
  
  /**
   * actionPerformed() - Handle Control panel button clicks
   * @param e is ActionEvent for buttons in control panel
   * @see DrawClusterGram#actionPerformed
   * @see EventMenu#promptFileName
   * @see Util#rmvFinalSubDirectory
   * @see Util#saveCmdHistory
   * @see #close
   * @see #saveAsGifFile
   * @see #updateScaling
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    Button item= (Button)e.getSource();
    
    if (cmd.equals("Close"))
    {
      close();
    }
    
    else if (cmd.equals("Linear"))
    { /* DEPRICATED */
      logScalingFlag= true;               /* switch mode */
      scalingButton.setLabel("Log");
      scalingButton.setActionCommand("Log");
      updateScaling();
    }
    
    else if (cmd.equals("Log"))
    { /* DEPRICATED*/
      logScalingFlag= false;              /* switch mode */
      scalingButton.setLabel("Linear");
      scalingButton.setActionCommand("Linear");
      updateScaling();
    }
    
    else if (cmd.equals("ReFilterHist"))
    {
      reFilterHistFlag= false;             /* switch mode */
      Util.saveCmdHistory("Don't re-Filter histogram", false);
      reFilterHistButton.setLabel("Don't re-Filter");
      reFilterHistButton.setActionCommand("NoReFilterHist");
      
      updateScaling();
    }
    
    else if (cmd.equals("NoReFilterHist"))
    {
      reFilterHistFlag= true;              /* switch mode */
      Util.saveCmdHistory("Re-Filter histogram", false);
      reFilterHistButton.setLabel("Re-Filter");
      reFilterHistButton.setActionCommand("ReFilterHist");
      updateScaling();
    }
    
    else if (cmd.equals("Filtered genes"))
    {
      showNonFilteredGenesFlag= false;      /* switch mode */
      Util.saveCmdHistory("Show all genes", false);
      nonFilteredGenesButton.setLabel("Show all genes");
      updateScaling();
    }
    
    else if (cmd.equals("Show all genes"))
    {
      showNonFilteredGenesFlag= true;     /* switch mode */
      Util.saveCmdHistory("Show just Filtered genes", false);
      nonFilteredGenesButton.setLabel("Filtered genes");
      updateScaling();
    }
    
    else if(cmd.equals("SaveAs"))
    { /* Save plot as GIF image */
      String
        defGifDir= Util.rmvFinalSubDirectory(mae.defDir,"Report", true),
        oGifFileName= mae.em.promptFileName("Enter GIF file name",
                                            defGifDir, defGifFile,
                                            null,   /* sub dir */
                                            ".gif",
                                            true,   /* saveMode*/
                                            true    /* useFileDialog */
                                            );
      if(oGifFileName!=null)
      {
        saveAsGifFile(oGifFileName);
        Util.saveCmdHistory("Saved plot as ["+oGifFileName+"]", false);
      }
    }
    
    else if (item==histBinRangeButton)
    { /* Histogram (intens/ratio) bin range button
     * This button will contain values: "= ", "< ", "> ", "<>", "><" */
      outsideRangeFlag= false;
      if(histBinRange.equals("Thr: = "))
      {
        histBinRange= "Thr: < ";
        histBinMode= TH_LT;
      }
      else if(histBinRange.equals("Thr: < "))
      {
        histBinRange= "Thr: > ";
        histBinMode= TH_GT;
      }
      else if(histBinRange.equals("Thr: > "))
      {
        histBinRange= "Thr: <>";
        histBinMode= TH_IN;
      }
      else if(histBinRange.equals("Thr: <>"))
      {
        histBinRange= "Thr: ><";
        histBinMode= TH_OUT;
        outsideRangeFlag= true;
      }
      else if(histBinRange.equals("Thr: ><"))
      {
        histBinRange= "Thr: = ";
        histBinMode= TH_EQ;
      }
      
      histBinRangeButton.setLabel(histBinRange);
      histBinRangeButton.setActionCommand(histBinRange);
      Util.saveCmdHistory("Histogram bin range set to ["+histBinRange+"]", false);
      updateScaling();
    }
    
    else if(dwCG!=null)
    {
     /*
     if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("SPP-AP cmd="+cmd);
      */
      dwCG.actionPerformed(e);    /* do additional button processing */
    }
  } /* actionPerformed */
  
  
  /**
   * updateScaling() - set scaling mode in proper sub-object for plotMode.
   * This may other side effects in terms of repainting.
   * @see DrawHistogram#updateScaling
   * @see DrawRatioHistogram#updateScaling
   * @see DrawScatterPlot#updateScaling
   */
  void updateScaling()
  { /* updateScaling */
    if(dwSP!=null)
      dwSP.updateScaling(logScalingFlag,showNonFilteredGenesFlag);
    else if(dwH!=null)
      dwH.updateScaling(logScalingFlag);
    else if(dwRH!=null)
      dwRH.updateScaling(logScalingFlag);
    else if(dwCG!=null)
    {
    }
  } /* updateScaling */
  
  
  /**
   * saveAsGifFile() - save plot canvas in oGifFileName
   * @param oGifFileName is full path file name of saved GIF file.
   * @see ClusterGramCanvas#drawGifFile
   * @see DrawHistogram#drawGifFile
   * @see DrawRatioHistogram#drawGifFile
   * @see DrawScatterPlot#drawGifFile
   */
  private void saveAsGifFile(String oGifFileName)
  { /* saveAsGifFile */
    if(dwSP!=null)
      dwSP.drawGifFile(oGifFileName);
    else if(dwH!=null)
      dwH.drawGifFile(oGifFileName);
    else if(dwRH!=null)
      dwRH.drawGifFile(oGifFileName);
    else if(dwCG!=null)
      dwCG.cgC.drawGifFile(oGifFileName);
  } /* saveAsGifFile */
  
  
  /**
   * windowClosing() - close down the window.
   * @param e is window closing event
   * @see #close
   */
  public void windowClosing(WindowEvent e)
  {
    close();
  }
  
  
  /*Others not used at this time */
  public void windowOpened(WindowEvent e)  { }
  public void windowActivated(WindowEvent e)  { }
  public void windowClosed(WindowEvent e)  { }
  public void windowDeactivated(WindowEvent e)  { }
  public void windowDeiconified(WindowEvent e)  { }
  public void windowIconified(WindowEvent e)  { }
  
  
  /**
   * mousePressed() - handle mouse Press events
   * @param e is mouse pressed event
   * @see DrawClusterGram#mousePressed
   * @see DrawHistogram#mousePressed
   * @see DrawRatioHistogram#mousePressed
   * @see DrawScatterPlot#mousePressed
   */
  public void mousePressed(MouseEvent e)
  { /* mousePressed */
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("SPP-MP e="+e);
    */
    if(dwSP!=null)
      dwSP.mousePressed(e);
    else if(dwH!=null)
      dwH.mousePressed(e);
    else if(dwRH!=null)
      dwRH.mousePressed(e);
    else if(dwCG!=null)
      dwCG.mousePressed(e);
  } /* mousePressed */
  
  
  /**
   * mouseReleased() - handle mouse Release events
   * @param e is mouse released event
   * @see DrawClusterGram#mouseReleased
   * @see DrawHistogram#mouseReleased
   * @see DrawRatioHistogram#mouseReleased
   * @see DrawScatterPlot#mouseReleased
   */
  public void mouseReleased(MouseEvent e)
  { /* mouseReleased */
    if(dwSP!=null)
      dwSP.mouseReleased(e);
    else if(dwH!=null)
      dwH.mouseReleased(e);
    else if(dwRH!=null)
      dwRH.mouseReleased(e);
    else if(dwCG!=null)
      dwCG.mouseReleased(e);
  } /* mouseReleased */
  
  
  /**
   * mouseClicked() - handle mouse Click events
   * @param e is mouse clicked event
   * @see DrawClusterGram#mouseClicked
   * @see DrawHistogram#mousePressed
   * @see DrawRatioHistogram#mousePressed
   * @see DrawScatterPlot#mousePressed
   */
  public void mouseClicked(MouseEvent e)
  { /* mouseClicked */
    if(dwSP!=null)
      dwSP.mousePressed(e);
    else if(dwH!=null)
      dwH.mousePressed(e);
    else if(dwRH!=null)
      dwRH.mousePressed(e);
    else if(dwCG!=null)
      dwCG.mouseClicked(e);
  } /* mouseClicked */
  
  
  /**
   * mouseMoved() - handle mouse Move events
   * @param e is mouse moved event
   * @see DrawClusterGram#mouseMoved
   * @see DrawHistogram#mouseMoved
   * @see DrawRatioHistogram#mouseMoved
   * @see DrawScatterPlot#mouseMoved
   */
  public void mouseMoved(MouseEvent e)
  { /* mouseMoved */
    if(dwSP!=null)
      dwSP.mouseMoved(e);
    else if(dwH!=null)
      dwH.mouseMoved(e);
    else if(dwRH!=null)
      dwRH.mouseMoved(e);
    else if(dwCG!=null)
      dwCG.mouseMoved(e);
  } /* mouseMoved */
  
  
  /**
   * mouseDragged() - handle mouse Drag events
   * @param e is mouse dragged event
   * @see DrawClusterGram#mouseDragged
   * @see DrawHistogram#mouseDragged
   * @see DrawRatioHistogram#mouseDragged
   * @see DrawScatterPlot#mouseDragged
   */
  public void mouseDragged(MouseEvent e)
  { /* mouseDraggd */
    if(dwSP!=null)
      dwSP.mouseDragged(e);
    else if(dwH!=null)
      dwH.mouseDragged(e);
    else if(dwRH!=null)
      dwRH.mouseDragged(e);
    else if(dwCG!=null)
      dwCG.mouseDragged(e);
  } /* mouseDragged */
  
  
  /**
   * mouseEntered() - handle mouse Enter events
   * @param e is mouse entered event
   * @see DrawClusterGram#mouseEntered
   * @see DrawHistogram#mouseEntered
   * @see DrawRatioHistogram#mouseEntered
   * @see DrawScatterPlot#mouseEntered
   */
  public void mouseEntered(MouseEvent e)
  { /* mouseEntered */
    if(dwSP!=null)
      dwSP.mouseEntered(e);
    else if(dwH!=null)
      dwH.mouseEntered(e);
    else if(dwRH!=null)
      dwRH.mouseEntered(e);
    else if(dwCG!=null)
      dwCG.mouseEntered(e);
  } /* mouseEntered */
  
  
  /**
   * mouseExited() - handle mouse Exit events
   * @param e is mouse exited event
   * @see DrawClusterGram#mouseExited
   * @see DrawHistogram#mouseExited
   * @see DrawRatioHistogram#mouseExited
   * @see DrawScatterPlot#mouseExited
   */
  public void mouseExited(MouseEvent e)
  { /* mouseExited */
    if(dwSP!=null)
      dwSP.mouseExited(e);
    else if(dwH!=null)
      dwH.mouseExited(e);
    else if(dwRH!=null)
      dwRH.mouseExited(e);
    else if(dwCG!=null)
      dwCG.mouseExited(e);
  } /* mouseExited */
  
  
  /**
   * adjustmentValueChanged() - handle scroll events if plot supports scrollers.
   * Use the scroll values to select the part of the plot to be
   * displayed and then repaint it.
   * @param e is scroller adjustment event
   * @see DrawHistogram#updateScrolledRegion
   * @see DrawScatterPlot#updateScrolledRegion
   */
  public void adjustmentValueChanged(AdjustmentEvent e)
  { /* adjustmentValueChanged */
    String arg= e.toString();	/* arg for the event */
    Object source= e.getSource();
    int
      hsValLB= 0,
      hsValUB= 0,
      vsValLB= 0,
      vsValUB= 0;
    float mss= (float)maxStepsScr;  /* to force (float) */
    
    if(horizLBscr!=null)
    {
      hsValLB= horizLBscr.getValue();
      hsValUB= horizUBscr.getValue();
      if(hsValLB>=hsValUB)
      { /* keep LB < UB */
        hsValLB= hsValUB-1;
        horizLBscr.setValue(hsValLB);
      }
    }
    
    if(vertLBscr!=null)
    {
      vsValLB= vertLBscr.getValue();
      vsValUB= vertUBscr.getValue();
      if(vsValLB<=vsValUB)
      { /* keep LB > UB since complement value is vertical */
        vsValLB= vsValUB+1;
        vertLBscr.setValue(vsValLB);
      }
    }
    
    if(dwSP!=null)
    {
      dwSP.updateScrolledRegion((hsValLB/mss), (hsValUB/mss),
      ((maxStepsScr-vsValLB)/mss),
      ((maxStepsScr-vsValUB)/mss));
    }
    
    else if(dwH!=null)
    {
      dwH.updateScrolledRegion((hsValLB/mss), (hsValUB/mss));
    }
  } /* adjustmentValueChanged */
  
  
  /**
   * itemStateChanged() - handle check box item state changed events
   * NOTE: need to implement radio groups here since AWT only
   * implements radio groups for Checkboxes, and not for Checkbox MenuItems.
   * @param e is checkbox toggle event
   * @see Util#saveCmdHistory
   */
  public void itemStateChanged(ItemEvent e)
  { /* itemStateChanged */
    Checkbox item= (Checkbox)e.getSource();
    
    if(mouseOverCheckBox==item)
    {
      mae.useMouseOverFlag= item.getState();
      mae.mbf.miVMuseMouseOver.setState(mae.useMouseOverFlag);
      Util.saveCmdHistory("Mouseover is "+
                          ((mae.useMouseOverFlag) ? "enabled" : "disabled"), 
                          false);
    }
  } /* itemStateChanged */
  
  
  /**
   * updateCurGene() - update plot if current gene changed
   * and this type of plot requires it.
   * @param mid is the new Master Gene ID of the current gene
   * @see DrawClusterGram#updateCurGene
   * @see DrawHistogram#repaint
   * @see DrawRatioHistogram#repaint
   * @see DrawScatterPlot#repaint
   * @see DrawScatterPlot#setCurGene
   */
  void updateCurGene(int mid, Object sObj)
  { /* updateCurGene */
    if(dwSP!=null)
    {
      dwSP.setCurGene(mid);
      dwSP.repaint();
    }
    else if(dwH!=null)
      dwH.repaint();
    else if(dwRH!=null)
      dwRH.repaint();
    else if(dwCG!=null)
      dwCG.updateCurGene(mid);      /* Update ClusterGram */
  } /* updateCurGene */
  
  
  /**
   * updateFilter() - update plot if Filter changed
   * and this type of plot requires it.
   * @param ml is the gene list to update now that filter has changed.
   * @see #updatePlot
   */
  void updateFilter(GeneList ml)
  { /* updateFilter */
    updatePlot();
  } /* updateFilter */
  
  
  /**
   * updateSlider() - update plot if Slider changed
   * and this type of plot requires it.
   * @see DrawScatterPlot#drawScatterPlot
   */
  void updateSlider()
  { /* updateSlider */
    if(dwSP!=null)
    {
      dwSP.drawScatterPlot();
    }
  } /* updateSlider */
  
  
  /**
   * updateLabels() - update string report if labels changed
   * and this type of plot requires it.
   * @see #updatePlot
   */
  void updateLabels()
  { /* updateLabels */
    updatePlot();
  } /* updateLabels */
  
  
  /**
   * close() - close this popup and reset flags if needed
   * @see DrawClusterGram#close
   * @see PopupRegistry#rmvPopupFromReg
   */
  void close()
  { /* close */
    if(dwRH!=null)
      mae.useRatioHistCLflag= false;    /* turn off filter*/
    if(dwH!=null)
      mae.useIntensHistCLflag= false;  /* turn off filter*/
    if(dwCG!=null)
    {
      dwCG.close();  /* clear other data structures as well */
      dwCG= null;
      mae.clg.removePreviousClusterMethod(0);      
    }
    
    mae.pur.rmvPopupFromReg(this);
    this.dispose();          /* close window */
  } /* close */
  
  
} /* class ShowPlotPopup */


