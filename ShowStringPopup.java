/** File: ShowStringPopup.java */

import java.text.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.*;

/**
 * The class creates and displays a scrollable string text area in a popup window.
 * Various control buttons are also provided at the bottom 
 * of the window that are dependent on the type of text area being displayed.
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.11 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
class ShowStringPopup extends Frame implements ActionListener, WindowListener 
{
  /** link to global MAExplorer nstance */
  private MAExplorer
    mae;                    
  /** link to global instance */
  private MenuBarFrame
    mbf;                    
  
  /** if optimized window with by shrinking # of buttons/row in
   * controlPanel to OPTIMUM_BUTTONS_PER_ROW
   */ 
  private final static boolean
    OPTIMIZE_WINDOW_WIDTH= true; 
  /** if optimize # buttons/row */
  private final static int
    OPTIMUM_BUTTONS_PER_ROW= 4;  
       
  /** ARG: name for PopupRegistry */
  String 
    popupName;              
  /** ARG: property bits of window for PopupRegistry */
  int 
    popupPropertyBits;      
  /** multipurpose button that toggles between:
   * "Go 'Cluster gene counts' " for "allGenes" (cluster counts) and
   * "Go 'Cluster single genes'" for "singleGene" (similar genes).
   */
  private Button
    allGenesPlotButton;
  /** "SaveAs" .txt button */
  private Button 
    saveAsButton;
           
  /** message after Saved Txt into file */
  String
    savedMsg;
  /** copy of text in textArea */
  private String
    textReport;
  private String
  /** default SAVE AS .txt file */  
    defTxtFile;

  /** Text area GUI for main report */             
  private TextArea 
    textarea;
 
  /** Optional Report mode if > 0 */
  private int
    optReportMode;
  /** font size */
  private int
    fntSize= 12;            
  /** # of character rows to show */
  private int
    nRows= 24;              
  /** # of character cols to show */
  private int
    nCols= 80;

  /** Flag: set true if popupName is "ALERT" */
  boolean
   alertOK= false; 
     
  /** mode of operation used in creating this 
   * window. Used in how we close the window 
   */    
  private int
    mode;                   
    
    
  /**
   * ShowStringPopup() - Constructor. Display String in textArea
   * @param mae instance of MAExplorer
   * @param inputStr is initial text string for buffer
   * @param nRows is maximum size of window
   * @param nCols, is maximum size of window
   * @param fontSize is null or  "8pt", "10pt", or "12pt"
   * @param title
   * @param optReportMode if not 0, add extra buttons
   * @param optMode save the mode for closing
   * @param popupName for PopupRegistry
   * @param popupPropertyBits for PopupRegistry
   * @param String defTxtFile is default SAVE AS .txt file
   * @see PopupRegistry#addPopupWindowToReg
   * @see PopupRegistry#removePopupByKey
   * @see Util#cvFontS2I
   */
  ShowStringPopup(MAExplorer mae, String inputStr, int nRows, int nCols,
                  String fontSize, String title, int optReportMode,
                  int optMode, String popupName, int popupPropertyBits,
                  String defTxtFile)
  { /* ShowStringPopup */
    super("ShowStringPopup");
    
    this.defTxtFile= (defTxtFile!=null) ? defTxtFile : "maeResults.txt";
    
    this.mae= mae;
    this.optReportMode= optReportMode;
    this.mode= (optMode>0) ? optMode : optReportMode;
    this.popupName= popupName;
    this.popupPropertyBits= popupPropertyBits;
    this.textReport= inputStr;
    
    String fontFamily= (mae.cfg.fontFamily!=null)
                         ? mae.cfg.fontFamily : "SansSerif";
    
    /* Register this window with the popup registry */
    if(mae.pur!=null)
    { /* only if PopupRegister exists */
      if((popupPropertyBits & mae.pur.UNIQUE)!=0)
        mae.pur.removePopupByKey(popupName);
      mae.pur.addPopupWindowToReg(this, popupName, popupPropertyBits);
    }
    
    if(fontSize!= null)
      fntSize= Util.cvFontS2I(fontSize);
    
    savedMsg= null;       /* changed to message after do "SaveAs" */
    
    /* Count # lines and make size MIN of nRows and nLines */
    if(inputStr==null)
      inputStr= "";
    int
      nButtonsUsed= 0, /* for possibly optimizing control panel layout*/
      nLines= 2,
      lth= inputStr.length();
    for(int i=0;i<lth;i++)
      if(inputStr.charAt(i)=='\n')
        nLines++;                /* count lines */
    nRows= Math.min(nRows,nLines);
    this.nCols= nCols;
    this.nRows= nRows;
    
    /* Create a TextArea to display the contents of the file/string.
     * Should use equi-space font such as courier.
     */
    textarea= new TextArea("", nRows,nCols);
    textarea.setFont(new Font(fontFamily, Font.PLAIN, fntSize));
    textarea.setEditable(false);
    textarea.setBackground(Color.white);
    this.add("Center", textarea);
    
    /* Create a bottom panel to hold a couple of buttons  */
    FlowLayout cpFlowLayout= new FlowLayout(FlowLayout.RIGHT, 10, 5);
    Panel controlPanel= new Panel();
    controlPanel.setBackground(Color.white);
    controlPanel.setLayout(cpFlowLayout);
    this.add(controlPanel, "South");
    
    /* Create the buttons and arrange to handle button clicks */
    Font font= new Font(fontFamily, Font.PLAIN /*BOLD*/, 12);
    Button b;
    
    if(popupName.equals("ALERT"))
    { /* Add 'OK Button' */
      nButtonsUsed++;
      b= new Button("OK");
      b.addActionListener(this);
      b.setFont(font);
      controlPanel.add(b);
      alertOK= false;
    }
    
    else if(optReportMode>0)
    { /* Special case if addition an optional Report and other buttons */
      if(mae.useSimGeneClusterDispFlag)
      { /* Add buttons for clusters closest to gene mode */
        nButtonsUsed++;
        defTxtFile= "maeSimilarGenes.txt";
        allGenesPlotButton= new Button("Go 'Cluster gene counts' ");
        allGenesPlotButton.setActionCommand("allGenes");
        allGenesPlotButton.addActionListener(this);
        allGenesPlotButton.setFont(font);
        controlPanel.add(allGenesPlotButton);
      }
      else if(mae.useClusterCountsDispFlag)
      { /* Add buttons for cluster count mode */
        nButtonsUsed++;
        defTxtFile= "maeClusterCounts.txt";
        allGenesPlotButton= new Button("Go 'Cluster single genes'");
        allGenesPlotButton.setActionCommand("singleGene");
        allGenesPlotButton.addActionListener(this);
        allGenesPlotButton.setFont(font);
        controlPanel.add(allGenesPlotButton);
      }
      
      else if(mae.useKmeansClusterCntsDispFlag)
      { /* Add K-means related buttons - recompute K-means clusters */
        nButtonsUsed++;
        defTxtFile= "maeK-meansClusters.txt";
        b= new Button("Recompute");  /* OLD: "Recompute clusters" */
        b.setActionCommand("redoKmeans");
        b.addActionListener(this);
        b.setFont(font);
        controlPanel.add(b);
      }
      
      if(mae.useSimGeneClusterDispFlag || mae.useClusterCountsDispFlag ||
         mae.useKmeansClusterCntsDispFlag)
      { /* Add Expression Profile Plot Button' */
        nButtonsUsed++;
        b= new Button("EP plot");
        b.addActionListener(this);
        b.setFont(font);
        controlPanel.add(b);
      }
      
      if(mae.useKmeansClusterCntsDispFlag)
      { /* Add Mean Expression Profile Plot Button' */
        nButtonsUsed++;
        b= new Button("Mean EP plot");
        b.setActionCommand("MnEPplot");
        b.addActionListener(this);
        b.setFont(font);
        controlPanel.add(b);
      }
      
      if(mae.useSimGeneClusterDispFlag || mae.useClusterCountsDispFlag ||
         mae.useKmeansClusterCntsDispFlag)
      { /* make cluster report */
        nButtonsUsed++;
        b= new Button("Cluster-Report");
        b.setActionCommand("report");
        b.addActionListener(this);
        b.setFont(font);
        controlPanel.add(b);
      }
      
      if(mae.useKmeansClusterCntsDispFlag)
      { /* make cluster report */
        nButtonsUsed++;
        b= new Button("Mn-Cluster-Report");
        b.setActionCommand("MnKmeansRpt");
        b.addActionListener(this);
        b.setFont(font);
        controlPanel.add(b);
      }
      
      if(mae.useKmeansClusterCntsDispFlag)
      { /* make Kmeans-ClusterGram report */
        nButtonsUsed++;
        b= new Button("ClusterGram");
        b.addActionListener(this);
        b.setFont(font);
        controlPanel.add(b);
        
        nButtonsUsed++;
        b= new Button("SaveAs GeneSets");
        b.addActionListener(this);
        b.setFont(font);
        controlPanel.add("Center", b);
      }
      
    } /* Special case if addition an optional Report & other buttons */
    
    if(!mae.isAppletFlag)
    { /* only if stand-alone */
      nButtonsUsed++;
      saveAsButton= new Button("SaveAs");
      saveAsButton.addActionListener(this);
      saveAsButton.setFont(font);
      controlPanel.add("Center", saveAsButton);
    }
    
    /* Always have the Close button unless an alert*/
    if(!popupName.equals("ALERT"))
    {
      nButtonsUsed++;
      b= new Button("Close");
      b.addActionListener(this);
      b.setFont(font);
      controlPanel.add(b);
    }
    
    /* Optimize Buttons in control panel so can shrink the
     * width of the window.
     * [TODO] rewrite in terms of layout sizes instead of # buttons.
     */
    //Dimension
    //  minCPsize= cpFlowLayout.minimumLayoutSize(controlPanel),
    //  minFrameSize= this.getMinimumSize();
    
    if( /* minCPsize.width > minFrameSize.width */
       OPTIMIZE_WINDOW_WIDTH && nButtonsUsed>=8)
    {
      int
        gCols= OPTIMUM_BUTTONS_PER_ROW,
        gRows= nButtonsUsed/nCols;
      if(gRows*nCols<nButtonsUsed)
        gCols++;
      controlPanel.setLayout(new GridLayout(gRows, gCols, 3, 3));
    }
    
    this.pack();
    textarea.setText(inputStr);
    
    this.addWindowListener(this);  /* listener for window events */
    
    this.setTitle(title);
    
    /* Center frame on the screen, PC only */
    Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
    Point pos= new Point((screen.width - this.getSize().width +50)/2,
                         (screen.height - this.getSize().height)/2);
    this.setLocation(pos);
    
    this.setVisible(true);
  } /* ShowStringPopup */
  
  
  /**
   * updateText() - update text in popup window
   * @param textReport is string to copy into text window
   */
  void updateText(String textReport)
  { /* updateText */
    this.textReport= textReport;
    textarea.setText(textReport);
  } /* updateText */
  
  
  /**
   * updateTitle() - update popup window title
   * @param textMsg is title message
   */
  void updateTitle(String titleMsg)
  { /* updateMsg */
    this.setTitle(titleMsg);
    this.repaint();
  } /* updateMsg */
  
  
  /**
   * changeGeneButton() - change gene label
   * @param newLabel for Gene button
   * @param newCmd to assign to the button
   * @param optReportMode to assign to new command
   */
  void changeGeneButton(String newLabel, String newCmd, int optReportMode)
  { /* changeGeneButton */
    allGenesPlotButton.setLabel(newLabel);
    if(newCmd==null)
      newCmd= newLabel;
    allGenesPlotButton.setActionCommand(newCmd);
    this.optReportMode= optReportMode;
  } /* changeGeneButton */
  
  
  /**
   * close() - close this popup and reset flags if needed
   * @param keepFlags will prevent flags from being reset
   * @see MAExplorer#repaint
   * @see PopupRegistry#removePopupByKey
   */
  void close(boolean keepFlags)
  { /* close */
    if(!keepFlags)
    { /* reset flags */
      if(mae.useSimGeneClusterDispFlag || mae.useClusterCountsDispFlag)
      {
        mae.clg.removePreviousClusterMethod(0);
      }      
      else if(mae.useKmeansClusterCntsDispFlag )
      {        
        mae.clg.removePreviousClusterMethod(0);
      }
      
      if(mae.stateScr.isVisible || mae.autoStateScrPopupFlag)
        mae.stateScr.regenerateScrollers(false);
    } /* reset flags */
    
    /* update the popup registry */
    if(mae.pur!=null)
      mae.pur.rmvPopupFromReg(this);

    mae.repaint();
    if(mae.is!=null && mae.is.siCanvas!=null)
      mae.is.siCanvas.repaint();if(mae.is!=null)
        
    this.dispose();
  } /* close */
  
  
  /**
   * actionPerformed() - Handle button clicks
   * @param e is action evet
   * @see ClusterGenes#saveKmeansClustersAsGeneSets
   * @see ClusterGenes#sortKmeansClusterList
   * @see ClusterGenes#updateKmeansClusters
   * @see GeneBitSet#updateListGeneBitSets
   * @see EventMenu#promptFileName
   * @see EventMenu#setClusterDisplayState
   * @see FileIO#writeFileToDisk
   * @see Filter#computeWorkingGeneList
   * @see GeneGeneDist#calcNormGeneVectors
   * @see HierClustNode
   * @see PopupRegistry#updateCurGene
   * @see Report
   * @see ShowExprProfilesPopup
   * @see ShowExprProfilesPopup#repaint
   * @see ShowPlotPopup
   * @see StateScrollers#regenerateScrollers
   * @see Util#rmvFinalSubDirectory
   * @see Util#saveCmdHistory
   * @see Util#showMsg
   * @see #changeGeneButton
   * @see #saveAsTxtFile
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    mbf= mae.mbf;
    
    if (cmd.equals("Close") || cmd.equals("OK")) /* close window */
    { /* Close or ALERT */
      if(popupName.equals("rloReport"))
        mae.em.rloPopup= null;  /* clear it so don't reuse it */
      
      if(cmd.equals("OK"))
        this.alertOK= true;
      boolean keepFlags= (mode==0);
      this.close(keepFlags);
    }
    
    else if (cmd.equals("report") && optReportMode>0)
    { /* make optional report */
      Util.saveCmdHistory("Creating Report", false);
      Report rpt= new Report(mae, "", /* note- it will make title*/
                             optReportMode, mae.tblFmtMode,
                             popupName+"-Rpt", popupPropertyBits);
    }
    
    else if (cmd.equals("MnKmeansRpt") && optReportMode>0)
    { /* make optional report */
      Util.saveCmdHistory("Creating Report of K-means statistics", false);
      Report rpt= new Report(mae, "", /* note- it will make title*/
                             mae.RPT_TBL_MN_KMEANS_CLUSTER,
                             mae.tblFmtMode,
                             popupName+"-Rpt", popupPropertyBits);
    }
    
    else if (cmd.equals("EP plot") && optReportMode>0 &&
              mae.useSimGeneClusterDispFlag)
    { /* make popup Expr. Profile Plot window */
      Gene curGene= ClusterGenes.curGene;
      int curMID= (curGene!=null) ? curGene.mid : -1;
      String sMsg= mae.gct.editedCL.length+
                   " expr. profiles of genes similar to [" +
                   curGene.Gene_Name + "]";
      Util.saveCmdHistory("Creating list of "+sMsg, false);
      ShowExprProfilesPopup 
        simEPplots= new ShowExprProfilesPopup(mae, mae.gct.editedCL,
                                              null, null, null, 0,
                                              false /* plot means+-SD */,
                                              sMsg,
                                              "EP-report",
                                              (mae.pur.CUR_GENE | mae.pur.FILTER |
                                              mae.pur.SLIDER | mae.pur.UNIQUE),
                                              mae.useEPoverlayFlag,
                                              false, /* useLargeFrameFlag */
                                              false /*showKmeansDataFlag - CloneID & name */
                                              );
      simEPplots.repaint();
      mae.repaint();
    }
    
    else if (cmd.equals("EP plot") && optReportMode>0 &&
             mae.useKmeansClusterCntsDispFlag)
    { /* make popup Expr. Profile Plot window of K-means genes */
      String sMsg= "Expression Profiles of K-means clustered Filtered genes";
      Util.saveCmdHistory("Creating list of "+sMsg, false);
      ClusterGenes.KmeansClustersExprProfPopup=
         new ShowExprProfilesPopup(mae, mae.fc.KmeansNodesCL, null, null, null,
                                   0, false /* plot means+-SD */,
                                   sMsg, "EP-report",
                                   (mae.pur.CUR_GENE | mae.pur.FILTER |
                                   mae.pur.SLIDER | mae.pur.UNIQUE),
                                   mae.useEPoverlayFlag,
                                   false, /* useLargeFrameFlag */
                                   true /*showKmeansDataFlag - CloneID & name */
                                   );
      ClusterGenes.KmeansClustersExprProfPopup.repaint();
    }
    
    else if (cmd.equals("MnEPplot") && optReportMode>0 &&
             mae.useKmeansClusterCntsDispFlag)
    { /* make popup Expr. Profile Plot window of K-means genes statistics */
      String sMsg= "Mean Expression Profiles of K-means clusters";
      Util.saveCmdHistory("Creating list of "+sMsg, false);
      ClusterGenes cg= mae.clg;
      if(cg.mnClustersCL!=null)
      { /* make summary mean EP plot */
        cg.KmeansClustersExprProfPopup=
           new ShowExprProfilesPopup(mae, cg.mnClustersCL, cg.hpDataNbrA,
                                     cg.hpDataMnA, cg.hpDataSDA,
                                     cg.nKmeansNodes, true /* plot means+-SD */,
                                     sMsg, "MnEP-report",
                                     (mae.pur.CUR_GENE | mae.pur.FILTER |
                                     mae.pur.SLIDER | mae.pur.UNIQUE),
                                     mae.useEPoverlayFlag,
                                     true, /* useLargeFrameFlag */
                                     true /*showKmeansDataFlag - CloneID & name */
                                     );
        cg.KmeansClustersExprProfPopup.repaint();
      }
    } /* make popup Expr. Profile Plot window of K-means genes */
    
    else if (cmd.equals("ClusterGram") && optReportMode>0 &&
             mae.useKmeansClusterCntsDispFlag)
    { /* make popup ClusterGram window of K-means genes */
      ClusterGenes cg= mae.clg;
      cg.sortKmeansClusterList(cg.complexClusterCL, mae.fc.KmeansNodesCL);
      float geneEPvect[][]= mae.ccd.calcNormGeneVectors(cg.complexClusterCL,
                                                        cg.nClist);
      cg.hcn= new HierClustNode(mae, cg.nClist, mae.hps.nHP_E,
                                mae.fc.KmeansNodesCL, geneEPvect);
      String title= "K-means ClusterGram for "+ cg.nClist+
                    " genes. Seed gene ["+ cg.initialSeedGene.Master_ID+"] "+
                    cg.initialSeedGene.Gene_Name;
      Util.saveCmdHistory("Creating "+title, false);
      cg.hierClusterGramPopup= 
          new ShowPlotPopup(mae,mae.PLOT_KMEANS_CLUSTERGRAM, title, 
                            "HierClustersPopup",
                            (PopupRegistry.CUR_GENE |
                            // PopupRegistry.FILTER |
                            PopupRegistry.UNIQUE),
                            0 /* frame offset in bits*/);
    } /* make popup ClusterGram window of K-means genes */
    
    else if (cmd.equals("allGenes") && optReportMode>0)
    { /* toggle cluster method: Switch to all Genes mode */
      /* set the state */
      mae.useClusterCountsDispFlag=
         EventMenu.setClusterDisplayState(mbf.miCLMsimGeneCountsDisp, true);
      textarea.setText("  Plotting counts of clusters for all Filtered genes ");
      Util.saveCmdHistory("Display cluster counts of Filtered genes (max of " +
                          ClusterGenes.maxGenesInCurCluster +
                          " genes) by HP-E expr profile.");
      changeGeneButton("Go 'Cluster single gene'", "singleGene",
                       mae.RPT_TBL_ALL_GENES_CLUSTER);
      if(!mae.stateScr.isVisible ||
         (mae.stateScr.sbActive[StateScrollers.idxClusterDist]) ||
          mae.autoStateScrPopupFlag)
        mae.stateScr.regenerateScrollers(false); /* regenerate */
      mae.fc.computeWorkingGeneList();
      
      /* cluster gene counts */
      int mid= mae.cdb.objMID;
      mae.pur.prevObjMID= -1;   /* force refresh */
      mae.pur.updateCurGene(mid, ClusterGenes.UPDATE, null);
    } /* Switch to all Genes mode */
    
    else if (cmd.equals("singleGene") && optReportMode>0)
    { /* Toggle cluster method: Switch to all Genes mode */
      /* set the state */
      mae.useSimGeneClusterDispFlag=
        EventMenu.setClusterDisplayState(mbf.miCLMfindSimGenesDisp, true);
      textarea.setText(" Click on a gene in microarray image to find\n"+
                       "  clusters with similar expression profiles.");
      Util.saveCmdHistory(
        "Display cluster of genes by HP-E expr profile for current gene in image.");
      
      if(!mae.stateScr.isVisible ||
         (mae.stateScr.sbActive[StateScrollers.idxClusterDist]) ||
          mae.autoStateScrPopupFlag)
        mae.stateScr.regenerateScrollers(false);   /* regenerate */
      changeGeneButton("Go 'Cluster gene counts'", "allGenes",
                       mae.RPT_TBL_CUR_GENE_CLUSTER);
      
      /* cluster similar genes from current gene or gene counts */
      int mid= mae.cdb.objMID;
      mae.pur.prevObjMID= -1;   /* force refresh */
      mae.pur.updateCurGene(mid, ClusterGenes.UPDATE, null);
    } /* Switch to all Genes mode */
    
    else if (cmd.equals("redoKmeans") && optReportMode>0)
    { /* recompute the K-means clusters */
      /* Update an existing K-means node cluster window*/
      Util.saveCmdHistory("Recompute the K-means clusters",false);
      mae.clg.updateKmeansClusters();
    }
    
    else if (cmd.equals("SaveAs GeneSets") && optReportMode>0)
    { /* Save all K-means clusters as named Gene sets */
      if(mae.clg.saveKmeansClustersAsGeneSets())
      {
        GeneBitSet
          workingGBS= mae.fc.workingCL.bitSet;
          workingGBS.updateListGeneBitSets();
        Util.saveCmdHistory("Saved all clusters as named Gene sets", false);
        Util.showMsg2("Saved all clusters as named Gene sets.");
      }
    } /* Save all K-means clusters as named Gene sets */
    
    else if(cmd.equals("SaveAs"))
    { /* Save plot as TXT  file */
      String
        defTxtDir= Util.rmvFinalSubDirectory(mae.defDir,"Report", true),
        oTxtFileName;
      
      if(mae.mbf==null)
      { /* not enough GUI exists to popup dialog guesser */
        oTxtFileName= defTxtDir + defTxtFile;  /* DEFAULT */
      }
      else
        oTxtFileName= mae.em.promptFileName("Enter .txt file name",
                                            defTxtDir,
                                            defTxtFile,
                                            null,   /* sub dir */
                                            ".txt",
                                            true,   /* saveMode*/
                                            true    /* useFileDialog */
                                            );
      if(oTxtFileName!=null)
      {
        saveAsTxtFile(oTxtFileName);
        savedMsg= "Saved "+oTxtFileName;
        Util.saveCmdHistory(savedMsg,false);
      }
    } /* Save plot as TXT  file */
  } /* actionPerformed */
  
  
  /**
   * saveAsTxtFile() - save textReport data in .txt file oTxtFileName
   * @param oTxtFileName is the full path of the file to write
   * @return true if succeed
   */
  private boolean saveAsTxtFile(String oTxtFileName)
  { /* saveAsTxtFile */
    /* Write the file */
    boolean flag= mae.fio.writeFileToDisk(oTxtFileName, textReport);
    
    if(flag)
      Util.showMsg("Saved as ["+oTxtFileName+"]");
    return(flag);
  } /* saveAsTxtFile */
  
  
  /**
   * windowClosing() - closing down the window, get rid of the frame.
   * @param e is window closing event
   * @see #close
   */
  public void windowClosing(WindowEvent e)
  { /* windowClosing */
    boolean keepFlags= (mode==0);
    this.close(keepFlags);
    //this.dispose();
  } /* windowClosing */
  
  
  /*Others not used at this time */
  public void windowOpened(WindowEvent e) { }
  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  
  
  
  /**
   * updateCurGene() - update string report if current gene changed
   * and this type of string report requires it.
   * @param mid is the new Master Gene ID of the current gene
   * @see MAExplorer#repaint
   */
  void updateCurGene(int mid)
  { /* updateCurGene */
    mae.repaint();           /* update */
  } /* updateCurGene */
  
  
  /**
   * updateFilter() - update string report if Filter changed
   * and this type of string report requires it.
   * @param ml is the gene list to update now that filter has changed.
   */
  void updateFilter(GeneList ml)
  { /* updateFilter */
  } /* updateFilter */
  
  
  /**
   * updateSlider() - update string report if Slider changed
   * and this type of string report requires it.
   */
  void updateSlider()
  { /* updateSlider */
  } /* updateSlider */
  
  
  /**
   * updateLabels() - update string report if labels changed
   * and this type of string report requires it.
   * @see MAExplorer#repaint
   */
  void updateLabels()
  { /* updateLabels */
    mae.repaint();
  } /* updateLabels */
  
} /* end of ShowStringPopup class */

