/** File: EventMenu.java */

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.*;
import java.io.FilenameFilter;

/**
 * Event handler class to process menubar Menu selection and menu checkbox events.
 * It contains various other methods used to support the interpretation
 * of these events and support the data structures. Some of these methods will
 * ensure radio-button behavior of the ganged checkbox menu items.
 * It updates global state flags and invokes various methods to perform the
 * operation.
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
 * @version $Date: 2003/11/24 21:18:54 $   $Revision: 1.55 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see MenuBarFrame
 */

class EventMenu implements FilenameFilter
{
  /** link to global MAExplorer instance */
  private static MAExplorer
    mae;
  /** link to global MenuBarFrame instance */
  private static MenuBarFrame
    mbf;
  /** link to global Filter instance */
  private static Filter
    fc;
  /** link to global GeneClass instance */
  private static GeneClass
    gct;
  /** link to global Condition instance */
  private static Condition
    cdList;
  /** link to global Config instance */
  private static Config
    cfg;
  /** link to global PopupRegistry instance */
  private static PopupRegistry
    pur;  
  /** link to global StateScrollers instance */
  private static StateScrollers
    stateScr;
  /** link to global Util instance */
  private static Util
    util;
  
  /** new project directory if any if not null */
  private static String
    newDir= null;
  
  /** last prompted file for popup dialog */
  private static String
    promptFile;
  /** last prompted directory  for popup dialog*/
  private static String
    promptDir;
  /** last prompted project for popup dialog */
  private static String
    promptProject;
  /** for FilenameFilter file extension for popup dialog */
  private static String
    promptExt;
  
  /** link to global instance of working GeneList */
  private static GeneList
    workingCL;
  /** link to global instanceof normalization GeneBitSet */
  private static GeneBitSet
    normCLbitSet;
  /** link to global instance of working GeneBitSet */
  private static GeneBitSet
    wkCLbitSet;
  
  /** save old value of mae.plotModeImage */
  private static int
    oldPlotMode;
  /** save old value of MaHybridSample mae.ms */
  private static MaHybridSample
    oldMS;
  /** previous array display mode */
  private static CheckboxMenuItem
    oldItem;
  
  /** processing by eval method not GUI. Use the argList supplied by eval */
  private static boolean
    useArgListFlag;
  
  /** old PopupCondChooser  to be closed if open up a new one without closing 
   * the first.
   **/
  private static PopupCondChooser 
    oldPCC;
  /** old PopupOrderedCondChooser  to be closed if open up a new one without closing 
   * the first.
   **/
  private static PopupOrderedCondChooser 
    oldPOCC;
  
  /** RLO report popup if it exists */
  static ShowStringPopup 
    rloPopup= null;
  
  
  
  /**
   * EventMenu() - constructor for event menu instance
   * @param mae is the MAExplorer instance
   */
  EventMenu(MAExplorer mae)
  { /* EventMenu */
    this.mae= mae;
    this.mbf= mae.mbf;
    this.fc= mae.fc;
    this.gct= mae.gct;
    this.cdList= mae.cdList;
    this.stateScr= mae.stateScr;
    this.util= mae.util;
    
    cfg= mae.cfg;
    pur= mae.pur;
    
    useArgListFlag= false;
    oldPCC= null;
  } /* EventMenu */
  
  
  /**
   * setPlotState() - set the Plot checkbox state.
   * NOTE may force the plotMode from outside.
   * EG. to pseduo image if no original or vice versa.
   * @param item is a CheckboxMenuItem item for this radio group
   */
  static void setPlotState(CheckboxMenuItem item)
  { /* setPlotState */
    /* [1] turn off all states */
    mbf.miPMplotPseudoImg.setState(false);
    if(mbf.miPMplotPseudoHP_XY_RYGImg!=null)
      mbf.miPMplotPseudoHP_XY_RYGImg.setState(false);
    if(mbf.miPMplotPseudoHP_F1F2_RYGImg!=null)
      mbf.miPMplotPseudoHP_F1F2_RYGImg.setState(false);
    mbf.miPMplotPseudoHP_XYImg.setState(false);
    if(mae.gangSpotFlag)
      mbf.miPMplotPseudoF1F2Img.setState(false);
    if(mbf.miPMplotPseudoHP_XY_pValueImg!=null)
      mbf.miPMplotPseudoHP_XY_pValueImg.setState(false);
    
    if(mbf.miPMplotPseudoHP_EP_CV_valueImg!=null)
      mbf.miPMplotPseudoHP_EP_CV_valueImg.setState(false);
        
    mbf.miPMflickerXY.setState(false);
    
    mae.cdb.hpXYdata.setCalcEPdataFlag(false); /* set it by specific event hlr */
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    mae.flickerXYflag= false;
    mae.runLoopDelayMsec= 1000;
    mae.updatePseudoImgFlag= true;  /* force pseudo array to be recomputed*/
    
    mae.madeChangesFlag= true;      /* made DB changes, so should SAVE DB*/
  } /* setPlotState */
  
  
  /**
   * setEditState() - set the edit checkbox state.
   * @param item is a CheckboxMenuItem item for this radio group
   */
  private static void setEditState(CheckboxMenuItem item)
  { /* setEditState */
    /* [1] turn off all states */
    mbf.miEMeditNop.setState(false);
    mbf.miEMeditAdd.setState(false);
    mbf.miEMeditRmv.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    mae.madeChangesFlag= true; /* made DB changes, so should SAVE DB*/
  } /* setEditState */
  
  
  /**
   * setSIrangeState() - set the spot intensity Range checkbox state.
   * Also revalidate the selected genes since the state is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setSIrangeState(CheckboxMenuItem item)
  { /* setSIrangeState */
    /* [1] turn off all states */
    mbf.miFRMspotIntensRngIn.setState(false);
    mbf.miFRMspotIntensRngOut.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setSIrangeState */
  
  
  /**
   * setIntensityRangeState() - set the Intensity Range checkbox state.
   * Also revalidate the selected genes since the state is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setIntensityRangeState(CheckboxMenuItem item)
  { /* setIntensityRangeState */
    /* [1] turn off all states */
    mbf.miFRMgrayRngIn.setState(false);
    mbf.miFRMgrayRngOut.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setIntensityRangeState */
  
  
  /**
   * setRatioRangeState() - set the Ratio Range checkbox state.
   * Also revalidate the selected genes since the state is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setRatioRangeState(CheckboxMenuItem item)
  { /* setRatioRangeState */
    /* [1] turn off all states */
    mbf.miFRMratioRngIn.setState(false);
    mbf.miFRMratioRngOut.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setRatioRangeState */
  
  
  /**
   * setCy3Cy5RatioRangeState() - set the Cy3/Cy5 Ratio Range checkbox state.
   * Also revalidate the selected genes since the state is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setCy3Cy5RatioRangeState(CheckboxMenuItem item)
  { /* setCy3Cy5RatioRangeState */
    /* [1] turn off all states */
    mbf.miFRMCy3Cy5RatioRngIn.setState(false);
    mbf.miFRMCy3Cy5RatioRngOut.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setCy3Cy5RatioRangeState */
  
  
  /**
   * setSImodeState() - set the spot intensity mode checkbox state.
   * Also revalidate the selected genes if CV mode checking is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setSImodeState(CheckboxMenuItem item)
  { /* setSImodeState */
    /* [1] turn off all states */
    mbf.miFRMspotIntensModeMS.setState(false);
    mbf.miFRMspotIntensModeXY.setState(false);
    mbf.miFRMspotIntensModeXsets.setState(false);
    mbf.miFRMspotIntensModeYsets.setState(false);
    mbf.miFRMspotIntensModeXORYsets.setState(false);
    mbf.miFRMspotIntensModeXANDYsets.setState(false);
    mbf.miFRMspotIntensModeE.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setSImodeState */
  
  
  /**
   * setSIcompareModeState() - set the spot intensity compare mode checkbox state.
   * Also revalidate the selected genes since the state is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   * @see StateScrollers#regenerateScrollers
   */
  private static void setSIcompareModeState(CheckboxMenuItem item)
  { /* setSIcompareModeState */
    /* [1] turn off all states */
    mbf.miFRMspotIntensCompareModeALL.setState(false);
    mbf.miFRMspotIntensCompareModeANY.setState(false);
    mbf.miFRMspotIntensCompareModeAT_MOST.setState(false);
    mbf.miFRMspotIntensCompareModeAT_LEAST.setState(false);
    mbf.miFRMspotIntensCompareModePRODUCT.setState(false);
    mbf.miFRMspotIntensCompareModeSUM.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    if(stateScr.isVisible || mae.autoStateScrPopupFlag)
      stateScr.regenerateScrollers(false);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setSIcompareModeState */
  
  
  /**
   * setCVmodeState() - set theCV mode checkbox state.
   * Also revalidate the selected genes if CV data checking is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setCVmodeState(CheckboxMenuItem item)
  { /* setCVmodeState */
    /* [1] turn off all states */
    if(mae.gangSpotFlag)
    {
      mbf.miFRMcvModeMS.setState(false);
      mbf.miFRMcvModeXY.setState(false);
    }
    mbf.miFRMcvModeXset.setState(false);
    mbf.miFRMcvModeYset.setState(false);
    mbf.miFRMcvModeXORYsets.setState(false);
    mbf.miFRMcvModeXANDYsets.setState(false);
    mbf.miFRMcvModeE.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setCVmodeState */
  
  
  /**
   * setPosQuantModeState() - set the Postive Quant data mode checkbox state.
   * Also revalidate the selected genes if Positive Quant data checking
   * is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setPosQuantModeState(CheckboxMenuItem item)
  { /* setPosQuantModeState */
    /* [1] turn off all states */
    mbf.miFRMposQuantModeMS.setState(false);
    mbf.miFRMposQuantModeXY.setState(false);
    mbf.miFRMposQuantModeXYsets.setState(false);
    mbf.miFRMposQuantModeE.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setPosQuantModeState */
  
  
  /**
   * setGoodSpotModeState() - set the Good Spot data mode checkbox state.
   * Also revalidate the selected genes if the mode data checking
   * is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setGoodSpotModeState(CheckboxMenuItem item)
  { /* setGoodSpotModeState */
    /* [1] turn off all states */
    mbf.miFRMgoodSpotModeMS.setState(false);
    mbf.miFRMgoodSpotModeXY.setState(false);
    mbf.miFRMgoodSpotModeXORYsets.setState(false);
    mbf.miFRMgoodSpotModeXANDYsets.setState(false);
    mbf.miFRMgoodSpotModeE.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setGoodSpotModeState */
  
  
  /**
   * setDetValueSpotModeState() - set the Spot detection value data mode checkbox state.
   * Also revalidate the selected genes if the mode data checking
   * is changed.
   * @param item is a CheckboxMenuItem item for this radio group
   * @see Filter#computeWorkingGeneList
   */
  private static void setDetValueSpotModeState(CheckboxMenuItem item)
  { /* setDetValueSpotModeState */
    /* [1] turn off all states */
    mbf.miFRMdetValueSpotModeMS.setState(false);
    mbf.miFRMdetValueSpotModeXY.setState(false);
    mbf.miFRMdetValueSpotModeXANDYsets.setState(false);
    mbf.miFRMdetValueSpotModeE.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    fc.computeWorkingGeneList();    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setDetValueSpotModeState */
  
  
  /**
   * setReportFormatState() - set the Report Format checkbox state.
   * @param item is a CheckboxMenuItem item for this radio group
   */
  private static void setReportFormatState(CheckboxMenuItem item)
  { /* setReportFormatState */
    /* [1] turn off all states */
    mbf.miTMtblFmtTabDelim.setState(false);
    mbf.miTMtblFmtClickable.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(true);
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setReportFormatState */
  
  
  
  /**
   * setReportFontSizeState() - set the Report Font Size checkbox state.
   * @param item is a CheckboxMenuItem item for this radio group
   */
  private static void setReportFontSizeState(CheckboxMenuItem item)
  { /* setReportFontSizeState */
    /* [1] turn off all states */
    mbf.miTMtblFontSize8pt.setState(false);
    mbf.miTMtblFontSize10pt.setState(false);
    mbf.miTMtblFontSize12pt.setState(false);
    
    mbf.miPRtblFontSize8pt.setState(false);
    mbf.miPRtblFontSize10pt.setState(false);
    mbf.miPRtblFontSize12pt.setState(false);
    
    /* [2] Turn on selected state*/
    if(item!=null && 
       (mbf.miTMtblFontSize8pt==item || mbf.miPRtblFontSize8pt==item))
    {
      mbf.miTMtblFontSize8pt.setState(true);
      mbf.miPRtblFontSize8pt.setState(true);
    }
    else if(item!=null &&
            (mbf.miTMtblFontSize10pt==item || mbf.miPRtblFontSize10pt==item))
    {
      mbf.miTMtblFontSize10pt.setState(true);
      mbf.miPRtblFontSize10pt.setState(true);
    }
    else if(item!=null && 
            (mbf.miTMtblFontSize12pt==item || mbf.miPRtblFontSize12pt==item))
    {
      mbf.miTMtblFontSize12pt.setState(true);
      mbf.miPRtblFontSize12pt.setState(true);
    }
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* setReportFontSizeState */
  
  
  /**
   * setViewerDisplayState() - set the viewer display checkbox state.
   * @param item is a CheckboxMenuItem item for this radio group
   * @param genomicDB is name of the genomic DB for use in msg
   * @see Util#saveCmdHistory
   */
  static boolean setViewerDisplayState(CheckboxMenuItem item, String genomicDB)
  { /* setViewerDisplayState */
    boolean flag= (item!=null) ? item.getState() : false;
    
    /* [1] turn off all states */
    if(mbf.miVMgenBankViewer!=null)
      mbf.miVMgenBankViewer.setState(false);
    if(mbf.miVMdbESTviewer!=null)
      mbf.miVMdbESTviewer.setState(false);
    if(mbf.miVMuniGeneViewer!=null)
      mbf.miVMuniGeneViewer.setState(false);
    if(mbf.miVMmAdbViewer!=null)
      mbf.miVMmAdbViewer.setState(false);
    if(mbf.miVMlocusLinkViewer!=null)
      mbf.miVMlocusLinkViewer.setState(false);
    if(mbf.miVMomimViewer!=null)
      mbf.miVMomimViewer.setState(false);
    if(mbf.miVMmedMinerViewer!=null)
      mbf.miVMmedMinerViewer.setState(false);
    if(mbf.miVMswissProtViewer!=null)
      mbf.miVMswissProtViewer.setState(false);
    if(mbf.miVMpirViewer!=null)
      mbf.miVMpirViewer.setState(false);
    
    if(cfg.nGenomicMenus>0)
      for(int i=0;i<cfg.nGenomicMenus;i++)
      { /* disable generic Genomic menu entries */
        /* Note: these will be enabled later */
        mbf.miVMgenomicViewer[i].setState(false);
        mae.genomicViewerFlag[i]= false;
      } /* disable generic Genomic menu entries */
    
    /* First shut other cluster methods off! */
    mae.genBankViewerFlag= false;
    mae.dbESTviewerFlag= false;
    mae.uniGeneViewerFlag= false;
    mae.mAdbViewerFlag= false;
    mae.locusLinkViewerFlag= false;
    mae.omimViewerFlag= false;
    mae.medMinerViewerFlag= false;
    mae.swissProtViewerFlag= false;
    mae.pirViewerFlag= false;
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(flag);
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
    
    /* [3] Update Cluster state */
    Util.saveCmdHistory(((flag) 
                          ? "Display " : "Don't display ") +
                            "current gene in popup "+genomicDB+" Web browser.");
    
    return(flag);
  } /* setViewerDisplayState */
  
  
  /**
   * setClusterDisplayState() - set the cluster display checkbox state.
   * NOTE: it first clears ALL clustering flags and checkboxes.
   * Also revalidate the cluster method state by calling
   * removePreviousClusterMethod() to close down the current
   * cluster method if it is active.
   * The checkbox is toggled and if if false, it returns
   * the default flag.
   * @param item is the checkbox menu item to set if not null.
   * @param defaultFlag is value to set if checkbox was not selected.
   * @return the (value of the checkbox OR the defaultFlag value). 
   * @see ClusterGenes#removePreviousClusterMethod
   */
  static boolean setClusterDisplayState(CheckboxMenuItem item,
                                        boolean defaultFlag)
  { /* setClusterDisplayState */
    boolean flag= (item!=null) ? item.getState() : false;
    if(!flag)
      flag= defaultFlag;
    
    /* [1] turn off all states */
    mbf.miCLMfindSimGenesDisp.setState(false);
    mbf.miCLMsimGeneCountsDisp.setState(false);
    mbf.miCLMhierClusterDisp.setState(false);
    mbf.miCLMdispKmeansNodes.setState(false);
    
    /* First shut other cluster methods off! */
    mae.useSimGeneClusterDispFlag= false;
    mae.useClusterCountsDispFlag= false;
    mae.useHierClusterDispFlag= false;
    mae.useKmeansClusterCntsDispFlag= false;
    
    /* [2] Turn on selected state*/
    if(item!=null)
      item.setState(flag);
    
    /* [3] Update Cluster state */
    int method= 0;       /* default to no method */
    if(item==mbf.miCLMfindSimGenesDisp)
     method= mae.clg.METHOD_SIMILAR_GENES_CLUSTERING;
    else if(item==mbf.miCLMsimGeneCountsDisp)
     method= mae.clg.METHOD_SIMILAR_GENE_COUNTS_CLUSTERING;
    else if(item==mbf.miCLMhierClusterDisp)
     method= mae.clg.METHOD_HIERARCHICAL_CLUSTERING;
    else if(item==mbf.miCLMdispKmeansNodes)
     method= mae.clg.METHOD_CLUSTER_KMEANS;
    mae.clg.removePreviousClusterMethod(method);  /* kill old cluster method */
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
    
    return(flag);
  } /* setClusterDisplayState */
    
  
  /**
   * clearClusterDisplayState() - clear cluster display checkbox state.
   * @param item is the cluster checkbox menu item to set if not null.
   * @return true if succeed
   */
  static boolean clearClusterDisplayState(CheckboxMenuItem item)
  { /* clearClusterDisplayState */
    boolean flag= false;
    
    /* [1] turn off all states */
    if(item==mbf.miCLMfindSimGenesDisp)
    {
      mbf.miCLMfindSimGenesDisp.setState(false);
      mae.useSimGeneClusterDispFlag= false;
    }
    else if(item==mbf.miCLMsimGeneCountsDisp)
    {
      mbf.miCLMsimGeneCountsDisp.setState(false);
      mae.useClusterCountsDispFlag= false;
    }
    else if(item==mbf.miCLMhierClusterDisp)
    {
      mbf.miCLMhierClusterDisp.setState(false);
      mae.useHierClusterDispFlag= false;
    }
    else if(item==mbf.miCLMdispKmeansNodes)
    {
      mbf.miCLMdispKmeansNodes.setState(false);
      mae.useKmeansClusterCntsDispFlag= false;
    }
    else return(false);
    
    return(true);
  } /* clearClusterDisplayState */
  
     
  /**
   * setIntensityCompareModeSlider() - set intensity compare mode Percent SI slider
   * @param newCompareMode
   */
  static void setIntensityCompareModeSlider(int newCompareMode)
  { /* setIntensityCompareModeSlider */
     boolean 
       oldComparePctModeFlag= 
         (mae.spotIntensCompareMode==MAExplorer.COMPARE_AT_LEAST ||
          mae.spotIntensCompareMode==MAExplorer.COMPARE_AT_MOST),
       newComparePctModeFlag= 
         (newCompareMode==MAExplorer.COMPARE_AT_LEAST ||
          newCompareMode==MAExplorer.COMPARE_AT_MOST);
      
     if(newComparePctModeFlag!=oldComparePctModeFlag)
     { /* If changed, adjust any sliders that depend on Percent SI */     
        stateScr.updateFilterScrollerUseCounter("Percent SI OK", 
                                                newComparePctModeFlag);
     }
  } /* setIntensityCompareModeSlider */
  
     
  /**
   * setZscoreNormalizationState() - set Zscore normalization state and
   * adjust sliders if required.
   */
  static void setZscoreNormalizationState()
  { /* setZscoreNormalizationState */
     boolean oldZscoreFlag= mae.isZscoreFlag;
     mae.isZscoreFlag= (mae.normByZscoreFlag ||
                        mae.normByLogMedianFlag ||
                        mae.normByZscoreMeanStdDevLogFlag ||
                        mae.normByZscoreMeanAbsDevLogFlag);
     if(mae.isZscoreFlag!=oldZscoreFlag)
     { /* If changed, adjust any sliders that depend on being in Zscore mode */
       mae.stateScr.adjustRatioOrZscoreCounters(); 
     }
  } /* setZscoreNormalizationState */
    
     
  /**
   * setNormalizationStateFromPlugin() - set normalization state from MAEPlugin
   * @param normMethodName to be specified
   * @see #setNormalizationState
   */
  static void setNormalizationStateFromPlugin(String normMethodName)
  { /* setNormalizationStateFromPlugin */
    setNormalizationState(null, normMethodName);
    if(mae.DBUG_NORM_PLUGIN)
      System.out.println("EM-SNSFP normMethodName="+normMethodName);    
  } /* setNormalizationStateFromPlugin */
  
  
  /**
   * setNormalizationState() - set the normalization checkbox state.
   * and toggle the checkbox item in the menu.
   * Also set flag that made change in the state.
   * @param item is a CheckboxMenuItem item for this radio group
   * @param methodName is name of new normalization method
   * @see MaeJavaAPI#getNormalizationState
   * @see MaeJavaAPI#setNormalizationState
   * @see Util#saveCmdHistory
   */
  private static boolean setNormalizationState(CheckboxMenuItem item,
  String methodName)
  { /* setNormalizationState */
    boolean flag= (item!=null) ? item.getState() : false;
    
    /* [1] Turn off all states */
    if(mbf.miNRMhousekeepNorm!=null)
      mbf.miNRMhousekeepNorm.setState(false);
    mbf.miNRMgeneSetNorm.setState(false);
    if(mbf.miNRMCalDNAnorm!=null)
      mbf.miNRMCalDNAnorm.setState(false);
    mbf.miNRMZscoreNorm.setState(false);
    mbf.miNRMZscoreMeanStdDevLogNorm.setState(false);
    mbf.miNRMZscoreMeanAbsDevLogNorm.setState(false);
    mbf.miNRMmedianNorm.setState(false);
    mbf.miNRMlogMedianNorm.setState(false);
    mbf.miNRMscaleToMaxIntens.setState(false);
    mbf.miNRMnoNorm.setState(false);
    
    if(mae.DBUG_GENERIC_NORM_PLUGIN && mae.gnp!=null)
    { /* if debugging generic NormalizationPlugins using direct class */
       mbf.miNRMtestGenericNormPlugin.setState(false);
    }
    
   /* [1.1] Turn off all Checkbox menuitem state.
    * [NOTE] the Plugin normalization checkbox is turned ON in the
    * handleItemStateChanged() event handler code...
    *
    * [JE] do not remove the item condition below, it means that
    * this call was made by an activated Normalization plugin.
    */
    if(mae.mja!=null)
    { /* Handle call from Normalization MAEPlugin */
      if(mae.DBUG_NORM_PLUGIN)
        System.out.println("EM-SNSFP.1 methodName="+methodName);
      if(item==null)
      { /* if it isn't built-in normalization selected see if there is a plugin */
        mae.mja.setNormalizationState(true);
        if (!mae.mja.getNormalizationState())
        { /* if there is no Normalization plugin, turn on default noNorm */
          mbf.miNRMnoNorm.setState(true);
        }
      }
      else
        mae.mja.setNormalizationState(false);
    } /* Handle call from Normalization MAEPlugin */
    
    /* [2] Clear all flags */
    mae.normByHousekeepGenesFlag= false;
    mae.normByGeneSetFlag= false;
    mae.normByCalDNAflag= false;
    mae.normByZscoreFlag= false;
    mae.normByZscoreMeanStdDevLogFlag= false;
    mae.normByZscoreMeanAbsDevLogFlag= false;
    mae.normByMedianFlag= false;
    mae.normByLogMedianFlag= false;
    mae.scaleDataToMaxIntensFlag= false;
    mae.testGenericNormPluginFlag= false;
    
    /* [3] Turn on selected state*/
    if(mae.DBUG_NORM_PLUGIN)
      System.out.println("EM-SNSFP.2 methodName="+methodName);
    if(item!=null)
      item.setState(flag);
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
    
    /* [4] Update command history  */
    Util.saveCmdHistory("Normalization by "+methodName+" is ["+
                        ((flag) ? "ON" : "OFF") +"]");
    return(flag);
  } /* setNormalizationState */
  
  
  /**
   * changeDefSample() - switch to new msList[i] hybridized sample list
   * @param sn is the index in the snxxx[] list
   * @param rmvCmd remove sample else add sample
   * @return true if succeed
   * @see ArrayScroller#syncScrollerAccess
   * @see MaHybridSample
   * @see Util#showMsg3
   */
  synchronized static boolean changeDefSample(int sn, boolean rmvCmd)
  { /* changeDefSample */
    mae.mReady= false;
    mae.msPrev= mae.ms;          /* save for garbage collection */
    String snHPname= mae.snHPName[sn];
    int	i= 0;
    
    for(int j=1;j<=mae.hps.nHP;j++)
      if(mae.hps.msList[j].hpName.equals(snHPname))
      { /* found it in the existing list */
        i= j;
        break;
      }
    
    if(i==0)
    { /* oops, never loaded the sucker - go get it! */
      i= ++mae.hps.nHP;                /* add a new one */
      mae.ms= new MaHybridSample(mae,
                                 mae.snHPName[sn],
                                 mae.snHPSampleID[sn],
                                 mae.snHPDatabaseFileID[sn],
                                 mae.snImageFile[sn],
                                 mae.snHPMenuText[sn],
                                 mae.snHPFullStageText[sn],
                                 mae.snPrjName[sn],
                                 mae.snNeedLogin[sn],
                                 mae.snSwapCy5Cy3Data[sn],
                                 i                 /* index for msList[i] */
                                 );
      mae.mapHPtoMenuName[i]= sn;  /* fixup map */
      mae.hps.msList[i]= mae.ms;       /* set the new one */
      mae.iSampleName[i]= mae.snHPName[sn];
      mae.iImageFile[i]= mae.snImageFile[sn];
    } /* oops, never loaded the sucker - go get it! */
    
    mae.curHP= i;
    mae.ms= mae.hps.msList[i];   /* set the new one */
    
    mae.is.syncScrollerAccess(); /* update scroller & canvas access to rg,po,ms */
    
    mae.mReady= true;	           /* enable it */
    mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    Util.showMsg3("");
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
    
    return(mae.ms.dataAvailFromQuantFileFlag);
  } /* changeDefSample */
  
    
  /**
   * processRLO() - process the "RLO:RscriptName" actions to invoke the R script
   * @param actCmd is the action command to process
   * @return true if succeed
   * @see #makeMenuItem
   * @see #makeSubMenu
   */
  synchronized static boolean processRLO(String actCmd)
  { /* processRLO */
    /* Get the RLO entries and add the menu items */
    MJAReval mr= mae.mja.mjaReval;
    /* Get list of RLO menu names */
    int nRLOs= mbf.nRLOs;
    String
      s,
      rloRscriptNames[]= mbf.rloRscriptNames;
        
    /* Setup global state of Reval */ 
    mr.setupAllBasePaths(mae.fileBasePath);
    
    for(int i=0;i<nRLOs;i++)    
      if(actCmd.equals("RLO:"+rloRscriptNames[i]))
      { /* found it - get the RLO instance and eval it */
        int rloID= mr.getRLOidByRscriptName(rloRscriptNames[i]);
        if(rloID<0)
        { /* DRYROT - not in the RLO database */
          s= "Can't find RscriptName in RLO DB, actCmd=["+ actCmd + "]";
          mae.logDRYROTerr(s);
          Util.showMsg(s);
          return(false);
        }
        
        /* Go evaluate it */  
        mr.setCurrentRLO(rloID);                   /* set the RLO to eval */
        String rReport= mr.runRscriptWithR();
        if(rloPopup!=null)
        { /* update an existing popup */
          rloPopup.updateTitle(mbf.rloMenuNames[i]);
          rloPopup.updateText(rReport);
        }
        else
          rloPopup= new ShowStringPopup(mae, rReport, 20,70,
                                        mae.rptFontSize,
                                        mbf.rloMenuNames[i], 0, 0,
                                        "rloReport", pur.UNIQUE,
                                        "rloReport.txt");
        
        /* If requested, show plots generated if any with last R eval. */
        boolean
          showPlotsFlag= mr.isProcessBit(mr.lookupProcessBitsDBvalue("Post-display-plots"));
        if(showPlotsFlag)
          showCurrentRplots();
        
        return(true);
      } /* found it - get the RLO instance and eval it */
    
    /* Can't find script */
    s= "Can't find RscriptName in rloRscriptNames DB, actCmd=["+ actCmd + "]";
    mae.logDRYROTerr(s);
    Util.showMsg(s);
    return(false);
  } /* processRLO */
  
  
  /**
   * showCurrentRplots() - show plots generated if any with last R eval.
   * If there were not plots generated with the last Eval, then it is a no-op.
   * Just say so in the msg area
   * @see MJAReval#getCurrentRLO
   * @see MJAReval#getOutputDataFileNames
   * @see MJAReval#getOutputDataTypes
   * @see MJAReval#getReportBasePath
   * @see MJAUtil#displayPDF
   * @see MJAUtil#displayURL
   * @see #appendReport
   * @see #showGuiMsg
   */
  static void showCurrentRplots()
  { /* showCurrentRplots */
    MJAReval mr= mae.mja.mjaReval;
    String 
      outputDataFileNames[]= mr.getOutputDataFileNames(),
      jpgFile,
      pdfFile;
    int 
      nOutputFiles= outputDataFileNames.length,
      nFound= 0;
    
    if(nOutputFiles > 0)
    { /* find if there are any plot files to read and popup */
      int outputDataTypes[]= mr.getOutputDataTypes();
      
      for(int j=0;j<nOutputFiles;j++)
      { /* look for the first report file name */
        if(outputDataTypes[j]==mr.lookupOfileTypeDBvalue("JPEG"))
        { /* Found a JPG report file name - get it and popup a Java window */
          jpgFile= mr.getReportBasePath() + outputDataFileNames[j];
          nFound++;
          // [TODO] read it into an interactive Java window or Web browser
          util.displayURL("file://"+jpgFile); /* exec application browser */
        }
        
        else if(outputDataTypes[j]==mr.lookupOfileTypeDBvalue("PDF"))
        { /* Found a PDF report file name - get it and popup a Java window */
          pdfFile= mr.getReportBasePath() + outputDataFileNames[j];
          nFound++;
          
          /* check if pdf file present */
          boolean pdfFileExists= new File(pdfFile).isFile();
          
          if(pdfFileExists)
            util.displayPDF(pdfFile); /* exec Adobe Acrobat on file */
          else 
          {
            String errMsg= "Error, cannot find PDF file:["+pdfFile+"].";
            util.showMsg2(errMsg, Color.red, Color.white);
          }
        }
      } /* look for the first report file name */
    } /* find if there are any plot files to read and popup */
        
    if(nFound==0)
    {
      util.showMsg("There were no plots generated with the last R analysis");
    }
  } /* showCurrentRplots */
  
  
  /**
   * processSetHP() - process the "HP_xxx:sample" actions to modify the
   * HP-X, HP-Y, and HP-E lists and current sample.
   * Where xxx is either '', 'X+', 'X-',  'Y+', 'Y-',  'E+', 'E-'.
   * @param actCmd is the action command to process
   * @return true if succeed
   * @see Filter#computeWorkingGeneList
   * @see HPxyData#setupDataStruct
   * @see MenuBarFrame#setHP_XYlabels
   * @see MAExplorer#repaint
   * @see SampleSets#addHPtoXset
   * @see SampleSets#addHPtoYset
   * @see SampleSets#addHPtoElist
   * @see SampleSets#rmvHPfromXset
   * @see SampleSets#rmvHPfromYset
   * @see SampleSets#rmvHPfromElist
   * @see Util#showMsg
   * @see Util#saveCmdHistory
   * @see #changeDefSample
   */
  synchronized static boolean processSetHP(String actCmd)
  { /* processSetHP */
    MaHybridSample msCurrent= mae.ms;   /* save in case we need to do UNDO */
    
    if(!(actCmd.startsWith("HP:") ||
         actCmd.startsWith("HP_X:") || actCmd.startsWith("HP_Y:") ||
         actCmd.startsWith("HP_X+:") || actCmd.startsWith("HP_X-:") ||
         actCmd.startsWith("HP_Y+:") || actCmd.startsWith("HP_Y-:") ||
         actCmd.startsWith("HP_E+:") || actCmd.startsWith("HP_E-:") ))
      return(false);    /* Bogus command! */
    
    for(int sn=1; sn<=mae.snHPnbr; sn++)
    { /* check full hybridized sample name "H.P....:..." */
      String
        ithCmd= "HP:" + mae.snHPName[sn],
        ithCmdX= "HP_X:" + mae.snHPName[sn],
        ithCmdY= "HP_Y:" + mae.snHPName[sn],
        ithCmdXadd= "HP_X+:" + mae.snHPName[sn],
        ithCmdXrmv= "HP_X-:" + mae.snHPName[sn],
        ithCmdYadd= "HP_Y+:" + mae.snHPName[sn],
        ithCmdYrmv= "HP_Y-:" + mae.snHPName[sn],
        ithCmdEadd= "HP_E+:" + mae.snHPName[sn],
        ithCmdErmv= "HP_E-:" + mae.snHPName[sn];
      
      if(actCmd.equals(ithCmd) ||
         actCmd.equals(ithCmdX) || actCmd.equals(ithCmdY) ||
         actCmd.equals(ithCmdXadd) || actCmd.equals(ithCmdXrmv) ||
         actCmd.equals(ithCmdYadd) || actCmd.equals(ithCmdYrmv) ||
         actCmd.equals(ithCmdEadd) || actCmd.equals(ithCmdErmv))
      { /* found it */
        boolean rmvCmd= (actCmd.equals(ithCmdXrmv) ||
                         actCmd.equals(ithCmdYrmv) ||
                         actCmd.equals(ithCmdErmv));
        int i= sn;
        
           /* NOTE: following is synchronized so don't run
            * into problems with run() thread.
            */
        boolean didChange= changeDefSample(sn, rmvCmd);
        if(!didChange)
        {
          Util.showMsg("No database entry for '"+
                        mae.snHPFullStageText[sn] + "'");
          mae.ms= msCurrent;   /* do UNDO of current H.P. */
          mae.mReady= true;    /* don't hold things up */
          return(false);
        }
        else
          msCurrent= mae.ms;   /* **DEBUGGING ** */
        
        if(actCmd.equals(ithCmdX))
        {
          mae.msX= mae.ms;
          mae.curHP_X= i;
        }
        else if(actCmd.equals(ithCmdY))
        {
          mae.msY= mae.ms;
          mae.curHP_Y= i;
        }
        else if(actCmd.equals(ithCmdXadd))
          mae.hps.addHPtoXset(mae.ms);
        else if(actCmd.equals(ithCmdXrmv))
          mae.hps.rmvHPfromXset(mae.ms);
        else if(actCmd.equals(ithCmdYadd))
          mae.hps.addHPtoYset(mae.ms);
        else if(actCmd.equals(ithCmdYrmv))
          mae.hps.rmvHPfromYset(mae.ms);
        else if(actCmd.equals(ithCmdEadd))
          mae.hps.addHPtoElist(mae.ms);
        else if(actCmd.equals(ithCmdErmv))
          mae.hps.rmvHPfromElist(mae.ms);
        else return(false);
        
        /* Update the HP-X/-Y 'set' state */
        mae.cdb.hpXYdata.setupDataStruct(mae.useHPxySetDataFlag);
        
        mbf.setHP_XYlabels(); /* Set the GUI HP-X: and HP-Y: labels
                               * as singles or multiple (Class names) */
        
        Util.saveCmdHistory("Setting Current Sample to '"+
                            mae.snHPFullStageText[i] + "'");        
        mae.updatePseudoImgFlag= true;
        
        fc.computeWorkingGeneList();
        mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        mae.repaint();
        return(true);
      } /* found it */
    } /* check full hybridized sample "H.P....:..." */
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
    
    return(false);
  } /* processSetHP */
  
  
  /**
   * accept() - filter used to test if file should be in directory list.
   * NOTE: implements method for io.FilenameFilter.accept()
   * Checks if the file extension is promptExt
   * @param dir is the file directory
   * @param name is the name of the file
   * @return true if accept it
   */
  public boolean accept(File dir, String name)
  { /* accept */
    boolean flag= false;
    
    /* This method is verifies the validity of the dir and name */
    if(dir!=null && name!=null && dir.isDirectory() &&
       (name.lastIndexOf(promptExt)>0) &&
       ((new File(dir,name)).isFile()))
      flag= true;
    
    return(flag);
  } /* accept */
  
  
  /**
   * promptFileName() - dialog prompt for file name.
   * If useFileDialog is set use FileDialog else dialogQuery.
   * Set promptFile, and promptDir.
   * @param msg is the prompt msg
   * @param useDir is the opt. dir to use
   * @param initialFile is the opt. initial file
   * @param subDir is theopt. subdir to use
   * @param fileExt is the file extension
   * @param saveMode is the mode. Save if true else open
   * @param useFileDialog if true else query dialog
   * @return the file name.
   * @see PopupDialogQuery#dialogQuery
   */
  static String promptFileName(String msg, String useDir,
                               String initialFile, String subDir,
                               String fileExt, boolean saveMode,
                               boolean useFileDialog)
  { /* promptFileName */
    promptDir= (useDir!=null)
                 ? useDir : mae.startupDir;      /* defaults */
    promptFile= null;
    
    if(!useFileDialog)
    { /* type in full name */
      promptFile= mbf.pdq.dialogQuery(msg, "");
    } /* type in full name */
    
    else
    { /* File Dialog browser to get name  - may change dir */
      Frame fdFrame= new Frame("FileDialog");
      FileDialog fd= new FileDialog(fdFrame, msg,
                                    ((saveMode) 
                                      ? FileDialog.SAVE : FileDialog.LOAD)
                                   );
      
      fd.setDirectory(promptDir);
      if(initialFile!=null)
        fd.setFile(initialFile);
      
      /* Set filter to accept only files with fileExt extensions.
       * We implement
       *    java.io.FilenameFilter.accept(File dir, String name)
       */
      promptExt= fileExt;    /* used by accept() for filtering */
      if(promptExt!=null && mae.em!=null)
      {
        FilenameFilter
        filter= (FilenameFilter)mae.em;
        fd.setFilenameFilter(filter);
      }
      
      fd.setVisible(true);
      
      String newFile= fd.getFile();
      if(newFile==null)
        return(null);
      promptFile= newFile;
      promptDir= fd.getDirectory();
    } /* File Dialog browser to get name  - may change dir */
    
    String fullPath= promptDir + promptFile;
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("EM-PFN.2 - promptDir="+promptDir+
                       "\n promptFile="+promptFile+
                       "\n fullPath="+fullPath);
    */
    
    return(fullPath);
  } /* promptFileName */
  
  
  /**
   * notAvailableYet() - print "Not available yet".
   * Used as a placeholder for future functionality.
   * @return true
   * @see Util#showMsg
   */
  static boolean notAvailableYet()
  { /* notAvailableYet */
    Util.showMsg("Not available yet.");
    return(true);
  } /* notAvailableYet */
  
  
  /**
   * changeProjDir() - change the project name and path.
   * Called by PopupProjDirGuesser "Done" event handler
   * After user exits the popup window.
   * @param pName is the name of the project directory
   * @param pdir is the project directory
   * @see PopupProjDirGuesser#done
   * @see Util#saveCmdHistory
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see #openNewMaeDB
   * @see #openNewWebDB
   */
  void changeProjDir(String pName, String pDir)
  { /* changeProjDir */
    if(pName==null)
    { /* nothing selected */
      Util.showMsg("");
      Util.showMsg2("");
      Util.showMsg3("No project selected - no change made.",
                    Color.white, /* foreground */ Color.red /* background */);
      
      Util.sleepMsec(1500);
    }
    else
    { /* change it */
      promptProject= pName;
      cfg.database= pName;
      promptDir= pDir;
      mae.defDir= promptDir;
      Util.saveCmdHistory("Changing project DB to ["+pName+"]");
      Util.showMsg2("in ["+pDir+"].");
      Util.showMsg3("Do 'File | Open ... DB' to open sub-project.");
      if(!promptDir.startsWith("http:"))
        openNewMaeDB(null, null);      /* as if did "Open file DB" */
      else
        openNewWebDB(null);            /* as if did "Open Web DB" */
    } /* change it */
  } /* changeProjDir */
  
  
  /**
   * promptSaveAsMaeDB() - prompt SaveAs  current .mae DB
   * Optionally:
   *<PRE>
   *  1. prompt if should save
   *  2. prompt for SaveAs name.
   *</PRE>
   * @param alwaysSaveFlag always save and don't prompt first
   * @param saveAllsamplesFlag save all samples, else just save the samples
   *        in the HP-X, HP-Y 'sets' and HP-E 'list'
   * @see PopupDialogQuery#dialogQuery
   * @see UserState#writeMAEstartupFile
   */
  private static void promptSaveAsMaeDB(boolean alwaysSaveFlag,
                                        boolean saveAllsamplesFlag)
  { /* promptSaveAsMaeDB */
    /* kill current DB and start new database */
    String
      dbName= mae.defStartupFile,
      areYouSure= (useArgListFlag || alwaysSaveFlag)
                    ? "yes"
                    : mbf.pdq.dialogQuery("Save current DB ["+dbName+
                                          "]? [yes|no]",  "no");
    /* Save DB */
    if(areYouSure.equals("yes"))
    { /* save the current startup file */
      String
        sDir= mae.defDir,
        sStartupFile= mae.defStartupFile;
      mae.us.writeMAEstartupFile(sDir, sStartupFile,saveAllsamplesFlag);
      
      mae.madeChangesFlag= false;    /* took care of it... */
    } /* save the current startup file */
  } /* promptSaveAsMaeDB */
  
  
  /**
   * openNewMaeDB() - open new .mae file DB browser & select file.
   * If mae.defStartupFile.equals(mae.MAE_STARTUP_FILE)
   * then look in MAEprojects.txt for the last "Active-Project" entry.
   * Look in (mae.defDir, mae.defStartupFile) for the file.
   * @param dbMAEdir - full path of the new database MAE/ 'project' directory.
   *   E.g. "C:/user/test/MAE/" (with appropriate delimiters for the OS you are using).
   * @param maeFilename - name of the .mae startup file to be started
   *   E.g. "Start.mae"
   * @see MAExplorer#start
   * @see MAExplorer#init
   * @see MAExplorer#stop
   * @see Table#lookupFieldIdx
   * @see UserState#readMAEstartupFile
   * @see Util#showMsg
   * @see #promptSaveAsMaeDB
   * @see #promptFileName
   */
  public static void openNewMaeDB(String optDefDir, String optFileName)
  { /* openNewMaeDB */
    boolean useOptNameFlag= false;
    String newMAEdbname= "";
    
    if(optDefDir!=null && optFileName!=null)
    { /* use optional file names */
      mae.defDir= optDefDir;
      mae.defStartupFile= optFileName;
      promptDir= optDefDir;
      promptFile= optFileName;
      newMAEdbname= optDefDir + optFileName;
      useOptNameFlag= true;
    }
    
    /* [1] See if there was an previous "Active-Project". */
    if(!useOptNameFlag && mae.defStartupFile.equals(mae.MAE_STARTUP_FILE))
    { /* look in MAEprojects.txt for the default (dir,file) */
      int activePrj= -1;         /* set if found a TRUE entry */
      Table prjTbl= new Table(mae, mae.prjListFile, "Project List");
      if(prjTbl.tRows>0)
      { /* Project table exists - search for "Active-Project" */
        int
          idxPrjDir= prjTbl.lookupFieldIdx("Project-Directory"),
          idxPrjFile= prjTbl.lookupFieldIdx("Last-Project-File"),
          idxActPrj= prjTbl.lookupFieldIdx("Active-Project");
        String rowData[];
        
        for(int r=0;r<(prjTbl.tRows-1);r++)
        { /* look for last active file */
          rowData= prjTbl.tData[r];
          if(rowData[idxActPrj].equals("TRUE"))
          { /* found previous active file, use it */
            mae.defDir= rowData[idxPrjDir];
            mae.defStartupFile= rowData[idxPrjFile];
            activePrj= r;  /* save row index */
            break;
          }
        } /* look for last active file */
      } /* Project table exists - search for "Active-Project" */
    } /* look in MAEprojects.txt for the default (dir,file) */
    
    /* [2] Startup up the directory browser if not using optional names */
    if(!useOptNameFlag)
    {
      String
        useDir= (newDir!=null && newDir.equals(mae.defDir))
                   ? mae.defDir : newDir;
        newMAEdbname= promptFileName("Open disk DB file",
                                     mae.defDir,
                                     mae.defStartupFile,
                                     "MAE",        /* sub dir */
                                     ".mae",
                                     false,        /* saveMode*/
                                     true          /* useFileDialog */
                                     );
    }
    if(promptFile==null || promptFile.length()==0)
      return;              /* no-op */
    
    /* [3] Open the database in newMAEdbname File.
     * Make sure the file exists.
     */
    if(!mae.us.readMAEstartupFile(promptDir, promptFile))
    {
      Util.showMsg("Invalid .mae startup file ["+newMAEdbname+"]");
      Util.popupAlertMsg("Invalid .mae startup file", 
                         "Invalid .mae startup file ["+newMAEdbname+"]",
                         4, 60);
      return;
    }
    
    /* [3.1] kill current DB and start new database */
    if(mae.startupFileExistsFlag &&mae.hps.nHP>0 &&
       //!mae.defStartupFile.equals(mae.MAE_STARTUP_FILE) &&
       (!mae.defDir.equals(promptDir) ||
        !mae.defStartupFile.equals(promptFile)))
    { /* If file is different, ask if save state of current DB */
      /* prompt SaveAs  current .mae DB */
      promptSaveAsMaeDB(false /* alwaysSaveFlag */,
      true /* saveAllflag */);
    }
    
    /* [3.2] Restart database */
    mae.defDir= promptDir;               /* update */
    mae.defStartupFile= promptFile;
    mae.nFilesTotal= 0;
    Util.showMsg("Loading new DB ["+newMAEdbname+"]...");
    
    /* [3.3] Reload top level frame into the same instance.
     * We need to GC old data where possible. Other data
     * will replace pointers so this should make GC easier.
     */
    int idx= newMAEdbname.lastIndexOf(mae.fileSeparator);
    if(idx!=-1)
    { /* get just file name - not full path of file name */
      mae.defStartupFile= newMAEdbname.substring(idx+1);
      /* extract path directory from default startup file */
      mae.defDir= newMAEdbname.substring(0,idx+1);
    }
    else
      mae.defStartupFile= newMAEdbname;
    
    /* [4] Now go switch databases */
    mae.stop();
    mae.init();
    mae.start();
    
    System.runFinalization();         /* OK to GC Now! */
    System.gc();
  } /* openNewMaeDB */
  
  
  /**
   * openNewWebDB() - open new Web database after closing the old one
   * @param optURL - optional URL to use
   * @see PopupDialogQuery#dialogQuery
   * @see MenuBarFrame#enableMenus
   * @see MenuBarFrame#repaint
   * @see MAExplorer#start
   * @see MAExplorer#init
   * @see MAExplorer#stop
   * @see #notAvailableYet
   */
  private static void openNewWebDB(String optURL)
  { /* openNewWebDB */
    if(!mae.DBUG_STANDALONE)
    {
      notAvailableYet();
      return;
    }
    
    String
      curCodebase= (mae.saCodeBase.startsWith("http://"))
                      ? mae.saCodeBase : "",
      newSAcb= (optURL!=null)
                 ? optURL
                 : mbf.pdq.dialogQuery("Web MAE DB address",
                                       curCodebase);
    if(!mae.saCodeBase.startsWith("http://"))
    {
      Util.showMsg("Invalid MAE server Web DB address");
      Util.popupAlertMsg("Invalid MAE server Web DB address", 
                         "Invalid MAE server Web DB address",
                         4, 60);
      return;
    }
   /*
   if(mae.CONSOLE_FLAG)
     mae.fio.logMsgln("EM-ONWDB newSAcb= "+newSAcb+
                      " mae.saCodeBase="+mae.saCodeBase);
   */
    
    /* Restart the database and invoke reading new HPs */
    if(newSAcb!=null && newSAcb.length()>0)
    { /* switch database */
      /* [TODO] validate that newSAcb is a valid Web MAE database
       * server... Could have special CGI to validate it.
       */
      mae.saCodeBase= newSAcb;
      
      /* kill current DB and start new database */
      /* [TODO] ask if save state of current database */
      mbf.enableMenus("none");
      mbf.repaint();
      
      mae.stop();
      mae.init();
      mae.start();
    } /* switch database */
  } /* openNewWebDB */
  
  
  /**
   * invokeMenuEntryByName() - process the menu command by name.
   * This is designed to drive event processing from either client-server,
   * scripts or MAEPlugins.
   * @param cmd to be evaluated (exact spelling)
   * @param forceStateFlag (optional) is set to 1 for true, 0 for false if force
   *        a checkbox state otherwise the checkbox state is toggled.
   * @return true if suceed, false if can't find command in command list
   * @see #processActionEventByName
   * @see #processCheckboxMenuItemStateChangedByitem
   * @see #processCheckboxStateChangedByItem
   */
  static boolean invokeMenuEntryByName(String cmd, int forceStateFlag)
  { /* invokeMenuEntryByName */
    return(invokeMenuEntryByName(cmd, forceStateFlag, null));
  } /* invokeMenuEntryByName */
  
  
  /**
   * invokeMenuEntryByName() - process the menu command by name with extra argList.
   * This is designed to drive event processing from either client-server,
   * scripts or MAEPlugins.
   * @param cmd to be evaluated (exact spelling)
   * @param forceStateFlag (optional) is set to 1 for true, 0 for false if force
   *        a checkbox state otherwise the checkbox state is toggled.
   * @param argList is an optional (if not null) arglist that can be used to supply
   *        arguments to various commands (eg. file names or database names etc).
   * @return true if suceed, false if can't find command in command list
   * @see #processActionEventByName
   * @see #processCheckboxMenuItemStateChangedByitem
   * @see #processCheckboxStateChangedByItem
   */
  static boolean invokeMenuEntryByName(String cmd, int forceStateFlag, String argList[])
  { /* invokeMenuEntryByName */
    boolean
      isActionCmdFlag= false,
      isCheckBoxMenuFlag= false,
      isCheckBoxFlag= false,
      flag= false;
    int
      nCBcmds= mbf.nCBcmds,
      nCmds= mbf.nCmds;
    String
      actCmd= null,
      actName= null,
      cbName= null,
      cbmName= null,
      menuActionLabelList[]= mbf.menuActionLabelList,
      menuActionCmdList[]= mbf.menuActionCmdList,
      chkBoxMenuLabelList[]= mbf.chkBoxMenuLabelList;
    CheckboxMenuItem cbmItem= null;
    Checkbox cbItem= null;
    
   /* Lookup the command type ([TODO] replace with hash table - although
    * this is very infrequenctly called so may not be worth it.)
    */
    for(int i= 0;i<nCmds;i++)
      if(menuActionCmdList[i].equals(cmd) || menuActionLabelList[i].equals(cmd))
      { /* check action commands */
        actCmd= menuActionCmdList[i];
        actName= menuActionLabelList[i];
        isActionCmdFlag= true;
        break;
      }
    if(! isActionCmdFlag)
      for(int i= 0;i<nCBcmds;i++)
        if(chkBoxMenuLabelList[i].equals(cmd))
        { /* check CheckboxMenu commands */
          cbmName= cmd;
          cbmItem= mbf.chkBoxMenuItemList[i];
          boolean
            oldFlag= cbmItem.getState(),
            newFlag= !oldFlag;       /* toggle current state of the flag */
          if(forceStateFlag==1)
            oldFlag= true;          /* force flag to true */
          if(forceStateFlag==0)
            oldFlag= false;         /* force flag to false */
          cbmItem.setState(newFlag);
          isCheckBoxMenuFlag= true;
          break;
        }
    
    /* Process the command */
    if(isActionCmdFlag)
      flag= processActionEventByName(actCmd, argList);
    else if(isCheckBoxMenuFlag)
      flag= processCheckboxMenuItemStateChangedByitem(cbmItem, cbmName);
    else if(isCheckBoxFlag)
      flag= processCheckboxStateChangedByItem(cbItem, cbName);
    
    return(flag);
  } /* invokeMenuEntryByName */
  
  
  /**
   * handleActions() - handle menu item events (stateless command).
   * We split the code using @see #processActionEventByName for actCmd
   * so you can invoke commands via a client-server plugin.
   * @param e is menu item event
   * @see #processActionEventByName
   */
  public static boolean handleActions(ActionEvent e)
  { /* handleActions */
    String actCmd= e.getActionCommand();
    boolean flag= processActionEventByName(actCmd, null);
    return(flag);
  } /* handleActions */
  
  
  /**
   * handleCheckboxStateChanged() - handle check box item state changed events.
   * We split the code
   * @see #processCheckboxStateChangedByItem for (cbItem, cbName)
   * so you can invoke commands via a client-server plugin.
   * @param e is menu checkbox item event
   * @see #processCheckboxStateChangedByItem
   */
  public static void handleCheckboxStateChanged(ItemEvent e)
  { /* handleCheckboxStateChanged */
    Checkbox cbItem= (Checkbox)e.getSource();
    String cbName= cbItem.getLabel();
    processCheckboxStateChangedByItem(cbItem, cbName);
  } /* handleCheckboxStateChanged */
  
  
  /**
   * handleItemStateChanged() - handle checkbox menu item state changed events.
   * We split the code
   * @see #processCheckboxMenuItemStateChangedByitem
   */
  public static void handleItemStateChanged(ItemEvent e)
  { /* handleCheckboxStateChanged */
    CheckboxMenuItem cbmItem= (CheckboxMenuItem)e.getSource();
    String cbmName= cbmItem.getLabel();
    processCheckboxMenuItemStateChangedByitem(cbmItem, cbmName);
  } /* handleCheckboxStateChanged */
  
  
  /**
   * processActionEventByName() - process Menu item events only...
   * We split the code so you can invoke commands via a client-server plugin.
   * @param actCmd is the menu item action command to process
   * @param argList is an optional (if not null) arglist that can be used to supply
   *        arguments to various commands (eg. file names or database names etc).
   * @return true if succeed.
   *
   * @see CompositeDatabase#recalcNorms
   * @see Condition#addCondList
   * @see Condition#difference
   * @see Condition#getCondListLength
   * @see Condition#getConditionByName
   * @see Condition#getHPlist
   * @see Condition#intersection
   * @see Condition#listCondLists
   * @see Condition#rmvCondList
   * @see Condition#renameCondList
   * @see Condition#updateListCondLists
   * @see Condition#union
   * @see ExprProfilePopup
   * @see Filter#computeWorkingGeneList
   * @see GeneBitSet#assignCLtoUserBS
   * @see GeneBitSet#getGBSnames
   * @see GeneBitSet#lookupGeneSetByName
   * @see GeneBitSet#listGeneBitSets
   * @see GeneBitSet#removeUserBS
   * @see GeneBitSet#updateListGeneBitSets
   * @see GeneBitSet#useGeneSetBS
   * @see GeneClass#setCurrentGeneClassName
   * @see GeneList#clear
   * @see MAExplorer#logDRYROTerr
   * @see MAExplorer#resetImageAndState
   * @see MAExplorer#repaint
   * @see MenuBarFrame#setTitle
   * @see PopupDialogQuery#dialogQuery
   * @see PopupBinOprDialogQuery#dialogQuery
   * @see PopupHPChooser
   * @see PopupHPmenuGuesser
   * @see PopupProjDirGuesser
   * @see PopupRegistry#updateFilter
   * @see Report
   * @see SampleSets#showHP_XY_assignmentsPopup
   * @see SampleSets#showHP_E_assignmentsPopup
   * @see SampleSets#changeHPswapCy5Cy3Samples
   * @see ScrollableImageCanvas#drawGifFile
   * @see ShowExprProfilesPopup
   * @see ShowPlotPopup
   * @see StateScrollers#regenerateScrollers
   * @see Table
   * @see UserState#doLogin
   * @see UserState#openState
   * @see UserState#openOtherUserState
   * @see UserState#readUserGeneSet
   * @see UserState#saveStateFile
   * @see UserState#shareUserState
   * @see UserState#updateMAEprojectDB
   * @see UserState#writeMAEstartupFile
   * @see Util#popupViewer
   * @see Util#rmvFinalSubDirectory
   * @see Util#saveCmdHistory
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see Util#showFeatures
   * @see Util#chkDirFinalSeparator
   * @see #notAvailableYet
   * @see #openNewMaeDB
   * @see #openNewWebDB
   * @see #promptFileName
   * @see #promptSaveAsMaeDB
   * @see #processSetHP
   */
  static boolean processActionEventByName(String actCmd, String argList[])
  { /* processActionEventByName */
    cfg= mae.cfg;
    pur= mae.pur;
    stateScr= mae.stateScr;
    
    useArgListFlag= (argList!=null);  /* processing by eval method not GUI */
    int nArgs= (useArgListFlag) ? argList.length : 0;
    String
      sf1= null,
      sf2= null;
    
    if(mae.startupFileExistsFlag)
    { /* has active database */
      workingCL= fc.workingCL;
    wkCLbitSet= workingCL.bitSet;
      normCLbitSet= gct.normCL.bitSet;
      sf1= cfg.fluoresLbl1;
      sf2= cfg.fluoresLbl2;
    }
    
    Util.showMsg("");         /* clear info area */
    Util.showFeatures("",""); /* clear info area */
    
    if(mae.useCy5OverCy3Flag)
    { /* flip Cy3/Cy5 to Cy5/Cy3 */
      String sTmp= sf1;
      sf1= sf2;
      sf2= sTmp;
    }
    
    /*
    if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("EM:ActionEvent actCmd='"+actCmd+"'");
    */
    if(!mae.mReady)
    { /* let them exit if bad things are happening... */
      /*
      if(actCmd.equals("Quit"))
        mae.quit();
      Util.showMsg2("Wait until finished loading...");
      */
      return(false);   /* not ready to accept events */
    }
    
    mae.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    /* [1] ****** FILE pull down menu ************* */
    if(actCmd.equals("New project"))
    { /* Stand-alone - create new project */
      if(mae.NEVER && !mae.DBUG_STANDALONE)
        return(notAvailableYet());
      
    /* NOTE: project directory is NOT specified with /MAE/
     * at the end of the string. If it is there, remove it
     * in the prompt.
     */
      String fSep= mae.fileSeparator;
      int prjPathIdx= mae.defDir.lastIndexOf(fSep+"MAE"+fSep);
      String
        prjPath= (prjPathIdx==-1)
                   ? mae.defDir
                   : mae.defDir.substring(0,prjPathIdx),      
        prjName= (nArgs>=4)
                   ? argList[0]
                   : mbf.pdq.dialogQuery("New project name",
                                         "New project"),
        prjDir= (nArgs>=4)
                  ? argList[1]
                  : mbf.pdq.dialogQuery("New project directory",
                                        prjPath),
        prjFile= (nArgs>=4)
                   ? argList[2]
                   : mbf.pdq.dialogQuery("(Opt) startup file", ""),
        prjWebAddr= (nArgs>=4)
                      ? argList[3]
                      : mbf.pdq.dialogQuery("(Opt) Web address", "");
      
      if(prjName==null || prjName.length()==0 ||
         prjDir==null || prjDir.length()==0)
        return(true);      /* no-op */
      
      if(prjFile==null)
        prjFile= "";
      if(prjWebAddr==null)
        prjWebAddr= "";
      prjDir= mae.util.chkDirFinalSeparator(prjDir, fSep);
      mae.us.updateMAEprojectDB(prjName, prjDir, prjFile, prjWebAddr);
    } /* Stand-alone - create new project */
    
    else if(actCmd.equals("Set project"))
    { /* change the default MAE project directory  */
      Table prjTbl= new Table(mae, mae.prjListFile, "Project List");
      
      if(prjTbl==null)
      { /* There is no project list - error or create one */
        Util.showMsg("There is no project list");
        Util.popupAlertMsg("There is no project list", 
                           "There is no project list",
                           4, 60);
        return(true);
      }
      else
        Util.saveCmdHistory("Selecting a new project");
      
      /* Get from project name Guesser */
      mbf.projDirGuesserIsPoppedUp= true;
      mbf.projDirGuesser= new PopupProjDirGuesser(mae, '*',
                                                  mbf.useFontLbl,
                                                  prjTbl);
    } /* change the default MAE project directory  */
    
    else if(actCmd.equals("Open disk DB"))
    { /* Standalone - open existing disk DB */
    /* [TODO] possibly add functionality to:
     * read MAEprojects.txt DB and start that database?
     * We may NOT want to do this each time. Perhaps offer it
     * as the default startup when do an "Open file DB"?
     */
      String
        optDefDir= (useArgListFlag && argList.length>=2) ? argList[0] : null,
        optDefFile= (useArgListFlag && argList.length>=2) ? argList[1] : null;
      
      openNewMaeDB(optDefDir, optDefFile);           /* open new .mae DB */
      return(true);
    }  /* Standalone - open existing disk DB */
    
    else if(actCmd.equals("Open Web DB"))
    {  /* Stand-alone - open existing Web DB */
      String optURL= (nArgs>=1) ? argList[0] : null;
      
      openNewWebDB(optURL);     /* open new Web DB */
      return(true);
    } /* Standalone - open existing Web DB */
    
    else if(actCmd.equals("Save disk DB"))
    { /* Save database in original mae.defStartupFile */
        mae.us.writeMAEstartupFile(mae.defDir,
        mae.defStartupFile, true);
    } /* Save database in original mae.defStartupFile */
    
    else if(actCmd.equals("Save as disk DB"))
    { /* Standalone - save current disk DB */
      String newMAEdbname= promptFileName("Save as disk DB file",
                                          mae.defDir,
                                          mae.defStartupFile,
                                          "MAE",        /* sub dir */
                                          ".mae",
                                          true,         /* saveMode*/
                                          true          /* useFileDialog */
                                          );
      if(promptFile==null || promptFile.length()==0)
        return(true);              /* no-op */
      
      /* Save the database in new mae.defStartupFile. */
      /* switch database */
      if(!newMAEdbname.endsWith(".mae"))
        newMAEdbname += ".mae";
      if(mae.us.writeMAEstartupFile(promptDir, promptFile, true))
      { /* save it ok */
        mae.startupDir= promptDir;         /* update */
        mae.defStartupFile= promptFile;
      }
    } /* Standalone - save current disk DB */
    
    else if(actCmd.equals("State:reg"))
    {
      mae.util.popupViewer(mae.getCodeBase(),"maeRegister.html", "MaeAux");
    }
    
    else if(actCmd.equals("State:login"))
    { /* Login the user when required */
      mae.us.doLogin("", true);       /* get (uN,uPW) and validate */
    } /* Login the user when required */
    
    else if(actCmd.equals("State:open"))
    {
      String uSN= (nArgs>=1)
                    ? argList[0]
                    : mbf.pdq.dialogQuery("user-state name", mae.userStateName);
      mae.us.openState(uSN);
      notAvailableYet();       /* "Not available yet." */
    }
    
    else if(actCmd.equals("State:save"))
    {
      if(!mae.isLoggedInFlag)
      {
        Util.showMsg("Login first before save state");
        Util.popupAlertMsg("Login first before save state", 
                           "Login first before save state",
                           4, 60);
        return(true);
      }
      String stateName= (nArgs>=1)
                          ? argList[0]
                          : mbf.pdq.dialogQuery("new user-state name", "");
      if(stateName==null || stateName.length()==0)
      {
        Util.showMsg("Define a 'State Name' first");
        Util.popupAlertMsg("Define a 'State Name' first", 
                           "Define a 'State Name' first.",
                           4, 60);
        return(true);
      }
      
      boolean flag= mae.us.saveStateFile(mae.userName, mae.userPasswd,
                                         stateName);
      if(flag)
        Util.saveCmdHistory("Saved user-state ["+stateName+"]");
      else
      {
        Util.showMsg("Can't save user-state ["+stateName+"]");
        Util.popupAlertMsg("Can't save user-state", 
                           "Can't save user-state ["+stateName+"].\n"+
                           "Check the directory and file name",
                           4, 80);
      }
    }
    
    else if(actCmd.equals("State:dir"))
    {
      notAvailableYet();       /* "Not available yet." */
    }
    
    else if(actCmd.equals("State:del"))
    {
      notAvailableYet();       /* "Not available yet." */
    }
    
    else if(actCmd.equals("State:openOther"))
    {
    /* [TODO] popup dialog to get name of user to share and name
     * of state to be opened. Then check the state (on backend?)
     * to see if it is allowed.
     */
      String
        ouN= (nArgs>=2)
               ? argList[0]
               : mbf.pdq.dialogQuery("other user's login name, then press OK",
                                     mae.otherUserName),
        uSN= (nArgs>=2)
               ? argList[1]
               : mbf.pdq.dialogQuery("their state name you want to use",
                                     mae.userStateName);
      if(mae.us.openOtherUserState(ouN, uSN))
        Util.saveCmdHistory("User-state ["+uSN+"] opened for user "+ouN);
      notAvailableYet();       /* "Not available yet." */
    }
    
    else if(actCmd.equals("State:shr"))
    {
      String
        ouN= (nArgs>=2)
               ? argList[0]
               : mbf.pdq.dialogQuery("other user's login name, then press OK",
                                     mae.otherUserName),
        uSN= (nArgs>=2)
               ? argList[1]
               : mbf.pdq.dialogQuery("your user-state name to share, then press OK",
                                     mae.userStateName);
      if(mae.us.shareUserState(ouN, uSN, "share"))
        Util.saveCmdHistory("User-state ["+uSN+"] allowed for user "+ouN);
    }
    
    else if(actCmd.equals("State:unshr"))
    {
      Util.showMsg2(
         "To disallow sharing with all users, set \"user's login name\" to *NONE*");
      Util.showMsg3(
         "To disallow all user-states for specific user, set user-state name to *ALL*");
      String
        ouN= (nArgs>=2)
               ? argList[0]
               : mbf.pdq.dialogQuery("other user's login name",
      mae.otherUserName),
        uSN= (nArgs>=2)
                ? argList[1]
                : mbf.pdq.dialogQuery("your user-state name to disallow",
      mae.userStateName);
      if(mae.us.shareUserState(ouN, uSN, "unshare"))
        Util.saveCmdHistory("User-state ["+uSN+"] disallowed for user "+ouN);
    }
    
    /*
    else if(actCmd.equals("Reset:HP-X"))
    {
      notAvailableYet();
    }
    
    else if(actCmd.equals("Reset:HP-Y"))
    {
      notAvailableYet();
    }
    
    else if(actCmd.equals("Reset:HP-E"))
    {
      notAvailableYet();
    }
    
    else if(actCmd.equals("Reset:GeneClass"))
    {
      notAvailableYet();
    }
    
    else if(actCmd.equals("Reset:norm"))
    {
      notAvailableYet();
    }
   */
    
    else if(actCmd.equals("Reset:all"))
    { /* Reset all of the state */
      /* Clear all points*/
      /* [TODO] popup "ask - are you sure?" confirmation window. */
      mae.resetImageAndState();
      
      Util.saveCmdHistory("Reseting state");
      
      if ( mae.runT != null )
      { /*redo the thread */
        mae.runT.stop();
        mae.runT= new Thread(mae);
        mae.runT.start();
      }
    } /* Reset all of the state */
    
    else if(actCmd.equals("UpdateMAE"))
    { /* download a new MAExplorer.jar file into program install area */
      String
        maeJarURL= "http://maexplorer.sourceforge.net/MAExplorer/MAExplorer.jar",
        maeJarServer= "maexplorer.sourceforge.net";
      
      /* Get the version # of installed software */     
      String
        maeVersionStr= "",
        maeVersionURL= "http://maexplorer.sourceforge.net/MAExplorer/MaeJarVersion.txt";       
      byte bVersion[]= mae.fio.readBytesFromURL(maeVersionURL, null);
      if(bVersion!=null)        
      { /* display the current version # on the server if found */
        String s= new String(bVersion);
        int idxNULL= s.indexOf('\0');
        if(idxNULL!=-1)
          s= s.substring(0,idxNULL);
        int 
          idxLF= s.indexOf('\r'),
          idxCR= s.indexOf('\n'),          
          idx= (idxLF!=-1 && idxCR!=-1)
                  ? Math.min(idxLF,idxCR)
                  : Math.max(idxLF,idxCR); 
                   
        maeVersionStr= "[V." + ((idx==-1) ? s : s.substring(0,idx)) + "] ";   
      }
      String areYouSure= 
          mbf.pdq.dialogQuery("MAExplorer.jar "+maeVersionStr+"update from "+maeJarServer+
                              " Web site - are you sure?[yes|no]", "no");
      if(areYouSure.equals("yes"))
      { /* do it */
        boolean flag= mae.us.updateMAExplorerJarFile();
        if(!flag)
        {       
          Util.showMsg2("FAILED! Unable to update MAExplorer.jar file from "+
                        maeJarServer+".", Color.white, Color.red );
          Util.showMsg3("Make sure you are connected to the Internet and the "+
                        maeJarServer+" server is up.", Color.white, Color.red);
          Util.popupAlertMsg("FAILED! Unable to update MAExplorer.jar file", 
                             "FAILED! Unable to update MAExplorer.jar file from \n"+
                             maeJarServer+
                             ".\nMake sure you are connected to the Internet and the "+
                             " server is up.\n",
                             6, 70);
        }
        else
        {
          Util.showMsg2("Finished updating new MAExplorer.jar file from "+
                        maeJarServer+".", Color.black, Color.white );
          Util.showMsg3("You must exit and restart MAExplorer to use the new version.",
                        Color.black, Color.white );
          Util.popupAlertMsg("Finished updating new MAExplorer.jar file", 
                             "Finished updating new MAExplorer.jar file from "+
                             maeJarServer+ ".\n"+
                             "\nYou must exit and restart MAExplorer to use the new version.",
                             6, 70);         
          mae.pur.setWaitCursor(false);
          Util.saveCmdHistory("Update MAExplorer");
        }
        
        //Util.sleepMsec(5000);
      } /* do it */
    } /* download a new MAExplorer.jar file into program install area */
    
    else if(actCmd.equals("UpdateMAEPlugins"))
    { /* download a new MAEPlugins/jar/*.jar files into the program install Plugins/ area */
      boolean flag= mae.us.updateAllPluginsJarFiles();
      if(flag)
        {
          String 
            maePluginsJarDirURL= "http://maexplorer.sourceforge.net/MAEPlugins/jar/";
          Util.showMsg2("Finished updating new MAExplorer Plugin jar files from "+
                        maePluginsJarDirURL+".", Color.black, Color.white );
          Util.showMsg3("You must load a new plugin to use it.",
                        Color.black, Color.white );
          Util.popupAlertMsg("Finished updating new MAExplorer Plugin .jar files", 
                             "Finished updating new MAExplorer Plugin .jar files from "+
                             maePluginsJarDirURL+ ".\n"+
                             "\nYou must load a new plugin to use it.\n"+
                             "If it is already loaded, then unload it and then load it again."+
                             "Load a plugin in the (Plugins | Load plugin) menu. Then run it\n"+
                             "from the (Plugins | xxxx) menu where it now appears as a command.\n",
                             12, 80);          
          mae.pur.setWaitCursor(false);
          Util.saveCmdHistory("Update Plugins");
        }
        
      //Util.sleepMsec(5000);
    } /* download a new MAEPlugins/jar/*.jar files into the program install Plugins/ area */
        
    else if(actCmd.equals("UpdateRLOmethods"))
    { /* download MAExplorer/{R/,RLO/,lib/MAERlibr/} methods files into program install area */
      /* Invoked from "Update RLO methods from maexplorer.sourceforge.net" */
      boolean flag= mae.us.updateRmethodsFiles();
      if(flag)
        {
          String 
            maeRLOmethodsDirURL= "http://maexplorer.sourceforge.net/MAExplorer/";
          Util.showMsg2("Finished updating new RLO methods files from "+
                        maeRLOmethodsDirURL+".",Color.black, Color.white );
          Util.showMsg3("You must have installed R to use these methods.",
                        Color.black, Color.white );
          Util.popupAlertMsg("Finished updating new RLO methods files", 
                             "Finished updating new RLO methods files from "+
                             maeRLOmethodsDirURL+ ".\n"+
                             "NOTES:\n"+
                             "(1) You must first have installed R to use these RLO methods.\n"+
                             "(2) RLO methods are currently being tested and have not yet been validated.\n"+
                             "\n"+
                             "Use the (Plugins | RLO methods) submenu to run an RLO method.\n"+
                             "Alternatively, load the RtestPlugin in the (Plugins | Load plugin)\n"+
                             "menu, then run it by selecting (Plugins | R test) to run RtestPlugin.\n",
                             12, 80);          
          mae.pur.setWaitCursor(false);
          Util.saveCmdHistory("Update RLO methods");
        }
        
      //Util.sleepMsec(5000);
    } /* download MAExplorer/{R/,RLO/,lib/MAERlibr/} methods files into program install area */
    else if(actCmd.equals("UpdateMAERLIB"))
    { /* download and install MAExplorer/MAERlibr.zip zip file into program install area */
      /* Invoked from "Update MAE Lib via zip file from maexplorer.sourceforge.net"*/
      boolean flag= mae.us.updateMAERlibrFilesViaZip();
      if(flag)
        {
          String 
            maeRlibDirURL= "http://maexplorer.sourceforge.net/MAExplorer/";
          Util.showMsg2("Finished updating MAE R lib files from zip file located at: "+
                        maeRlibDirURL+".",Color.black, Color.white );
          Util.showMsg3("You must have installed R to use these methods.",
                        Color.black, Color.white );
          Util.popupAlertMsg("Finished updating MAE R lib", 
                             "Finished updating MAE R lib from zip file located at: "+
                             maeRlibDirURL+ ".\n"+
                             "NOTES:\n"+
                             "(1) You must first have installed R to use the library.\n"+
                             "(2) The MAE R lib is currently being tested and have not yet been validated.\n"+
                             "\n",
                             12, 80);          
          mae.pur.setWaitCursor(false);
          Util.saveCmdHistory("UpdateMAERLIB");
        }
      else
      {
       Util.popupAlertMsg("Can't update MAERlibr files","Can't update MAERlibr files"+
                        "Make sure you are connected\n to the Internet and the"+
                        " server is up.", 4, 60); 
      }
        
      //Util.sleepMsec(5000);
    } /* download MAExplorer/MAERlibr.zip/} zip file into program install area */
    
    else if(actCmd.equals("Quit"))
    { /* exit stand-alone version */
      if(mae.madeChangesFlag &&!mae.defStartupFile.equals(mae.MAE_STARTUP_FILE))
      { /* If file is different, ask if save state of current DB */
        /* prompt SaveAs  current .mae DB */
        promptSaveAsMaeDB(false /* alwaysSaveFlag */, true /* saveAllflag */);
      }
      mae.quit();
    } /* exit stand-alone version */
    
    /* [2] ****** Sample HP-X/-Y/-E pull down menus ********* */
    else if(actCmd.startsWith("List HP-X & HP-Y sample"))
    { /* list HP-X and HP-Y sets */
      mae.hps.showHP_XY_assignmentsPopup();
    } /* list HP-X/-Y  sets*/
    
    else if(actCmd.startsWith("List HP-E sample"))
    { /* list HP-E */
      mae.hps.showHP_E_assignmentsPopup();
    }
    
    else if(actCmd.equals("HP-X:setClass"))
    { /* get class name for HP-X */
      mae.classNameX= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery("Define HP-X 'set' class (condition) name",
      mae.classNameX);
      mae.updatePseudoImgFlag= true;
      mae.pur.updateLabels();
      mae.repaint();
    }
    
    else if(actCmd.equals("HP-Y:setClass"))
    { /* get class name for HP-Y */
      mae.classNameY= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery("Define HP-Y 'set' class (condition) name",
      mae.classNameY);
      mae.updatePseudoImgFlag= true;
      mae.pur.updateLabels();
      mae.repaint();
    }
    
    else if(actCmd.startsWith("HpGuesser"))
    { /* enter current HP menu name in popup guesser */
      if(!mbf.hpMenuGuesserIsPoppedUp)
      { /* Create guesser (separate window) */
        /* Add the name of the HP menu target action to take
         * AFTER we have guessed a sample with the Guesser list.
         */
        String hpAction= "";
        if(actCmd.endsWith(":HP"))
          hpAction= "HP:";
        else if(actCmd.endsWith(":HP_X"))
          hpAction= "HP_X:";
        else if(actCmd.endsWith(":HP_Y"))
          hpAction= "HP_Y:";
        else if(actCmd.endsWith(":HP_X+"))
          hpAction= "HP_X+:";
        else if(actCmd.endsWith(":HP_X-"))
          hpAction= "HP_X-:";
        else if(actCmd.endsWith(":HP_Y+"))
          hpAction= "HP_Y+:";
        else if(actCmd.endsWith(":HP_Y-"))
          hpAction= "HP_Y-:";
        else if(actCmd.endsWith(":HP_E+"))
          hpAction= "HP_E+:";
        else if(actCmd.endsWith(":HP_E-"))
          hpAction= "HP_E-:";
        
        mbf.hpMenuToUpdateFromGuesser= hpAction;
        /*
        if(mae.CONSOLE_FLAG)
          mae.fio.logMsgln("EM-HA actCmd="+actCmd+
                           " hpAction="+ hpAction);
         */
        mbf.hpMenuGuesserIsPoppedUp= true;
        mbf.hpMenuGuesser= new PopupHPmenuGuesser(mae, '\0',
        mbf.useFontLbl);
      } /* Create guesser (separate window) */
    } /* enter current HP menu name in popup guesser */
    
    else if(actCmd.startsWith("HpChooser"))
    { /* enter current HP menu name in popup chooser */
      PopupHPChooser phpc= new PopupHPChooser(mae,
                                              null, /* titleFont*/
                                              null, /* labelFont*/
                                              null, /* dataFont */
                                              7,    /* taRows */
                                              40    /* taCols (was 30, or 25) */
                                              );
    } /* enter current HP menu name in popup guesser */
    
    else if(actCmd.startsWith("CondChooser"))
    { /* "Choose named conditions of samples" */      
      PopupCondChooser pcc= new PopupCondChooser(mae.mja, oldPCC);
      oldPCC= pcc;            /* save for the next time */
    }
     else if(actCmd.startsWith("OrderedCondChooser"))
    { /* "Choose named conditions of samples" */      
      PopupOrderedCondChooser pocc= new PopupOrderedCondChooser(mae.mja, oldPOCC);
      oldPOCC= pocc;            /* save for the next time */
    }
    else if(actCmd.startsWith("HPswapCy53"))
    { /* Popup GUI to select HPs to swap Cy3/Cy5 to Cy5/Cy3 */
      /* Popup scrollable CB array to change
       * msList[0:nHP-1].swapCy5Cy3DataFlags.
       */
      mae.hps.changeHPswapCy5Cy3Samples();
    }
    
    else if(actCmd.startsWith("HP"))
    { /* check full hybridized sample name "HP...:..." */
      if(processSetHP(actCmd))
        mae.repaint();   /* only repaint if successufl! */
    }    
    
    /* [3] ****** GeneClass pull down menu ************* */
    else if(actCmd.startsWith("GC:"))
    { /* analyze gene class name */
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("EM actCmd='"+actCmd+"'");
       */
      for(int i=0; i<mae.gct.nbrGC; i++)
      { /* check full gene class name */
        String ithCmd= "GC:" + mae.gct.geneClassName[i];
        if(actCmd.equals(ithCmd))
        { /* found it */
          mae.gct.curGeneClass= i;
          Util.saveCmdHistory("Setting GeneClass to '"+
          mae.gct.geneClassName[i] + "'");
          
          /* sets the default GeneClass and fc.gcMemberCL */
          mae.updatePseudoImgFlag= true;
          mae.gct.setCurrentGeneClassName(mae.gct.geneClassName[i]);
          fc.computeWorkingGeneList();
          mae.repaint();
          break;
        }
      } /* check full gene class name */
    } /* analyze gene class name */
    
    else if(actCmd.equals("ListCurGC"))
    {
      Util.saveCmdHistory("Current Gene Class is '" +
      mae.gct.curGeneClassName+"'");
    }
    
    /* *** [4] Edit Menu **** */
    else if(actCmd.equals("Edit:ClearEGL"))
    {
      Util.saveCmdHistory("Clearing 'Edited Gene List'");
      gct.editedCL.clear();
      pur.updateFilter(mae.fc.workingCL);
      mae.repaint();
    }
    
    else if(actCmd.equals("Edit:CopyW2EGL"))
    {
      Util.saveCmdHistory("Setting 'Edited Gene List' to Filtered genes");
      gct.editedCL.copy(gct.editedCL, fc.workingCL);
      pur.updateFilter(mae.fc.workingCL);
      mae.repaint();
    }
    
    else if(actCmd.equals("GeneSet:list"))
    {
      Util.saveCmdHistory("Listing Gene Sets");
      wkCLbitSet.listGeneBitSets(null);
    }
    
    else if(actCmd.equals("GeneSet:assWorkCL"))
    {
      String
        userSetName= (nArgs>=1)
                       ? argList[0]
                       : mbf.pdq.dialogQuery(
              "gene set name from list or type a new one to save data filter, then press OK", 
                                             "", wkCLbitSet.getGBSnames(),
                                             wkCLbitSet.nUserBS);
      if(wkCLbitSet.assignCLtoUserBS(userSetName, workingCL)!=-1)
      {
        Util.saveCmdHistory("Assigned Filtered genes to Gene Set ["+
                            userSetName+"]");
        wkCLbitSet.updateListGeneBitSets();
      }
    }
    
    else if(actCmd.equals("GeneSet:assEGL"))
    {
      String
        userSetName= (nArgs>=1)
                       ? argList[0]
                       : mbf.pdq.dialogQuery(
               "gene set name from list or type a new one to save to E.G.L., then press OK", 
                                             "", wkCLbitSet.getGBSnames(),
                                             wkCLbitSet.nUserBS);
      if(wkCLbitSet.assignCLtoUserBS(userSetName, gct.editedCL)!=-1)
      {
        Util.saveCmdHistory("Assigning 'Edited Gene List' to ["+
                            userSetName+"]");
        wkCLbitSet.updateListGeneBitSets();
      }
    }
    
    else if(actCmd.equals("GeneSet:useCSfilter"))
    { /* Set the Filter Gene Set and then re-filter */
      String
        userSetName= (nArgs>=1)
                       ? argList[0]
                       : mbf.pdq.dialogQuery(
      "gene set name from list or type it for 'user filter gene set', then press OK", 
                                             "", wkCLbitSet.getGBSnames(),
                                             wkCLbitSet.nUserBS);
      if(wkCLbitSet.useGeneSetBS(mae.gct.userFilterGeneSetCL,
                                 "Filter", userSetName))
      {
        Util.saveCmdHistory("Assigning 'Use Filter gene set' to ["+
                            userSetName+"]");
        fc.computeWorkingGeneList();
        wkCLbitSet.updateListGeneBitSets();
        mae.repaint();
      }
    } /* Set the Filter Gene Set and then re-filter */
    
    else if(actCmd.equals("GeneSet:useCSnorm"))
    { /* set the Normalization Gene Set and renormalize */
      String
        userSetName= (nArgs>=1)
                       ? argList[0]
                       : mbf.pdq.dialogQuery(
       "gene set name from list or type it for user normalization gene set, then press OK", 
                                             "", wkCLbitSet.getGBSnames(),
                                             wkCLbitSet.nUserBS);
      if(normCLbitSet.useGeneSetBS(mae.gct.normCL, "Normalization",
                                   userSetName))
      { /* recompute normalization and then re-filter */
        if(mae.normByGeneSetFlag)
          mae.cdb.recalcNorms("gene set", mae.normByGeneSetFlag);
        Util.saveCmdHistory("Setting 'Use Normalization gene set' to Gene Set ["+
                            userSetName+"]");
        fc.computeWorkingGeneList();
        wkCLbitSet.updateListGeneBitSets();
        mae.repaint();
      }
    } /* set the Normalization Gene Set and renormalize */
    
    else if(actCmd.equals("GeneSet:union"))
    {
      boolean ok;
      if(nArgs>=3)
      { /* get from the argList[] options */
        mbf.pBOdq.data1= argList[0];
        mbf.pBOdq.data2= argList[1];
        mbf.pBOdq.data3= argList[2];
      }
      else
        ok= mbf.pBOdq.dialogQuery(" - (1st OR 2nd)",
                                  "Enter 1st gene set name (from list or type it)",
                                  "Enter 2nd gene set (from list or type it)",
                                  "Enter new gene set (from list or type it) to save result",
                                  "", "", "",
                                  wkCLbitSet.getGBSnames(),
                                  wkCLbitSet.nUserBS);
      String
        bsName1= mbf.pBOdq.data1,
        bsName2= mbf.pBOdq.data2,
        userSetName= mbf.pBOdq.data3;
      if(wkCLbitSet.assignCSbinOprToUserBS(bsName1, bsName2,
                                           userSetName,"union"))
      {
        Util.saveCmdHistory("Gene set ["+userSetName+"] is Union("+
                            bsName1+" or "+bsName2+")");
        wkCLbitSet.updateListGeneBitSets();
      }
    }
    
    else if(actCmd.equals("GeneSet:inter"))
    {
      boolean ok;
      if(nArgs>=3)
      { /* get from the argList[] options */
        mbf.pBOdq.data1= argList[0];
        mbf.pBOdq.data2= argList[1];
        mbf.pBOdq.data3= argList[2];
      }
      else
        ok= mbf.pBOdq.dialogQuery(" - (1st AND 2nd)",
                                 "Enter 1st gene set name (from list or type it)",
                                 "Enter 2nd gene set (from list or type it)",
                                 "Enter new gene set (from list or type it) to save result",
                                 "", "", "",
                                 wkCLbitSet.getGBSnames(),
                                 wkCLbitSet.nUserBS);
      String
        bsName1= mbf.pBOdq.data1,
        bsName2= mbf.pBOdq.data2,
        userSetName= mbf.pBOdq.data3;
      if(wkCLbitSet.assignCSbinOprToUserBS(bsName1, bsName2,
                                           userSetName,"intersect"))
      {
        Util.saveCmdHistory("Gene set ["+userSetName+"] is Intersection("+
                            bsName1+" and "+bsName2+")");
        wkCLbitSet.updateListGeneBitSets();
      }
    }
    
    else if(actCmd.equals("GeneSet:diff"))
    {
      boolean ok;
      if(nArgs>=3)
      { /* get from the argList[] options */
        mbf.pBOdq.data1= argList[0];
        mbf.pBOdq.data2= argList[1];
        mbf.pBOdq.data3= argList[2];
      }
      else
        ok= mbf.pBOdq.dialogQuery(" - DIFFERENCE (1st - 2nd)",
                                  "Enter 1st gene set name (from list or type it)",
                                  "Enter 2nd gene set (from list or type it) to subtract",
                                  "Enter new gene set (from list or type it) to save result",
                                  "", "", "",
                                  wkCLbitSet.getGBSnames(),
                                  wkCLbitSet.nUserBS);
      String
        bsName1= mbf.pBOdq.data1,
        bsName2= mbf.pBOdq.data2,
        userSetName= mbf.pBOdq.data3;
      if(wkCLbitSet.assignCSbinOprToUserBS(bsName1, bsName2,
                                           userSetName,"difference"))
      {
        Util.saveCmdHistory("Gene set ["+userSetName+"] is difference("+
                            bsName1+" - "+bsName2+")");
        wkCLbitSet.updateListGeneBitSets();
      }
    }
    
    else if(actCmd.equals("GeneSet:reName"))
    { /* Rename gene set */
      String
        userSetName= (nArgs>=2)
                       ? argList[0]
                       : mbf.pdq.dialogQuery(
             "'existing' gene set name (from list or type it) to 'rename', then press OK", 
                                             "", wkCLbitSet.getGBSnames(),
                                             wkCLbitSet.nUserBS),
      newSetName= (nArgs>=2)
                    ? argList[1]
                    : mbf.pdq.dialogQuery(
                           "'new' gene set name by typing it, then press OK", 
                                          "", wkCLbitSet.getGBSnames(),
      wkCLbitSet.nUserBS);
      int nRename= GeneBitSet.lookupGeneSetByName(userSetName, false);
      
      if(nRename!=-1 && newSetName!=null && newSetName.length()>0)
      {
        String
          gbsName= wkCLbitSet.userBS[nRename].bName,
          ans= (nArgs>=1)
                 ? argList[0]
                 : mbf.pdq.dialogQuery("- rename set '"+gbsName+
                                       "' - are you sure? [yes|no]","no");
        if(ans.equals("yes"))
        {
          wkCLbitSet.userBS[nRename].bName= newSetName;
          wkCLbitSet.updateListGeneBitSets();
        }
      }
    } /* Rename gene set */
    
    else if(actCmd.equals("GeneSet:rmv"))
    { /* Remove gene set */
      String
        userSetName= (nArgs>=1)
                       ? argList[0]
                       : mbf.pdq.dialogQuery(
                           "existing gene set name (from list or type it) to 'remove'",
                                             "", wkCLbitSet.getGBSnames(),
                                             wkCLbitSet.nUserBS);
      int nRemove= GeneBitSet.lookupGeneSetByName(userSetName, false);
      if(nRemove!=-1)
      {
        String
          gbsName= wkCLbitSet.userBS[nRemove].bName,
          ans= mbf.pdq.dialogQuery("- remove set '"+gbsName+
                                   "' - are you sure? [yes|no]","no");
        if(ans.equals("yes"))
        {
          wkCLbitSet.removeUserBS(userSetName);
          wkCLbitSet.updateListGeneBitSets();
        }
      }
    } /* Remove gene set */
    
    else if(actCmd.equals("GeneSet:Load"))
    {
      String
      cbsFileName= (nArgs>=1)
                     ? argList[0]
                     : promptFileName("Open disk gene set file",
                                      mae.defDir,
                                      "",
                                      "State",      /* sub dir */
                                      ".cbs",
                                      false,        /* saveMode*/
                                      true          /* useFileDialog */
                                      );
      if(mae.us.readUserGeneSet(cbsFileName, "@FULL-PATH")!=-1)
        wkCLbitSet.updateListGeneBitSets();
      return(true);
    }
    
    /* - - - - - - - - - - - - - - - - - - - - - - - */
    
    else if(actCmd.equals("CndLst:list"))
    {
      mae.cdList.listCondLists();
    }
    
    else if(actCmd.equals("CndLst:listOne"))
    {
      String
         condName= (nArgs>=1)
                      ? argList[0]
                      : mbf.pdq.dialogQuery(
                         "condition list of samples (from list or type it) to report",
                                            "", cdList.condNames, 
                                            cdList.nCondLists);
      if(!mae.cdList.listCondition(condName))
      {
        Util.showMsg("Can't find condition list ["+condName+"]");      
        Util.popupAlertMsg("Can't find condition list", 
                           "Can't find condition list ["+condName+"].\n"+
                           "Check the directory and file name",
                           4, 80);
      }
    }
    
    else if(actCmd.equals("CndLst:saveX"))
    {
      String
        userListName= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery(
       "name of condition list of samples (from list or type it) to save HP-X 'set',"+
                           " then press OK",
                                              "", cdList.condNames, 
                                              cdList.nCondLists);
      mae.cdList.addCondList(userListName, mae.hps.msListX, mae.hps.nHP_X, 1);
      mae.cdList.updateListCondLists();
    }
    
    else if(actCmd.equals("CndLst:saveY"))
    {
      String 
        userListName= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery(
         "name of condition list of samples (from list or type it) to save HP-Y 'set',"+
                           " then press OK",
                                              "", cdList.condNames, 
                                              cdList.nCondLists);
      mae.cdList.addCondList(userListName, mae.hps.msListY, mae.hps.nHP_Y, 1);
      mae.cdList.updateListCondLists();
    }
    
    else if(actCmd.equals("CndLst:saveE"))
    {
      String
        userListName= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery(
      "name of condition list of samples (from list or type it) to save HP-E 'list',"+
                           " then press OK",
                                              "", cdList.condNames,
                                              cdList.nCondLists);
      mae.cdList.addCondList(userListName, mae.hps.msListE, mae.hps.nHP_E, 1);
      mae.cdList.updateListCondLists();
    }
    
    else if(actCmd.equals("CndLst:useX"))
    {
      String 
        userListName= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery(
                           "HP list name (from list or type it) to use as HP-X 'set',"+
                           " then press OK",
                                              "", cdList.condNames,
                                              cdList.nCondLists);      
      Condition cond= mae.cdList.getConditionByName(userListName);
      if(cond!=null)
      { /* change it */
        mae.hps.msListX= cond.getHPlist(); 
        mae.hps.nHP_X= cond.getCondListLength();
        Util.saveCmdHistory("Setting HP-X 'set' to HP sample list ["+userListName+"]");        
      }
      else
      {
        Util.showMsg("Can't find HP sample list ["+userListName+"]");           
        Util.popupAlertMsg("Can't find condition list", 
                           "Can't find HP sample list ["+userListName+"].\n"+
                           "Check the directory and file name",
                           4, 80);
      }
    }
    
    else if(actCmd.equals("CndLst:useY"))
    {
      String
        userListName= (nArgs>=1)
                         ? argList[0]
                         : mbf.pdq.dialogQuery(
                             "HP list name (from list or type it) to use as HP-Y,"+
                             " then press OK",
                                               "", cdList.condNames, 
                                               cdList.nCondLists);      
      Condition cond= mae.cdList.getConditionByName(userListName);
      if(cond!=null)
      { /* change it */
        mae.hps.msListY= cond.getHPlist();
        mae.hps.nHP_Y= cond.getCondListLength();
        Util.saveCmdHistory("Setting HP-Y to HP list ["+userListName+"]");
      }
      else
      {
        Util.showMsg("Can't find HP sample list ["+userListName+"]");         
        Util.popupAlertMsg("Can't find condition list", 
                           "Can't find HP sample list ["+userListName+"].\n"+
                           "Check the directory and file name",
                           4, 80);
      }
    }
    
    else if(actCmd.equals("CndLst:useE"))
    {
      String
        userListName= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery(
                            "HP list name (from list or type it) to use as HP-E,"+
                            " then press OK",
                                              "", cdList.condNames,
                                              cdList.nCondLists);
      Condition cond= mae.cdList.getConditionByName(userListName);
      if(cond!=null)
      { /* change it */
        mae.hps.msListE= cond.getHPlist();
        mae.hps.nHP_E= cond.getCondListLength();
        Util.saveCmdHistory("Setting HP-E to HP list ["+userListName+"]");
      }
      else
      {
        Util.showMsg("Can't find HP list ["+userListName+"]");         
        Util.popupAlertMsg("Can't find condition list", 
                           "Can't find HP sample list ["+userListName+"].\n"+
                           "Check the directory and file name",
                           4, 80);
      }
    }
    
    else if(actCmd.equals("CndLst:union"))
    {
      boolean ok;
      if(nArgs>=3)
      { /* get from the argList[] options */
        mbf.pBOdq.data1= argList[0];
        mbf.pBOdq.data2= argList[1];
        mbf.pBOdq.data3= argList[2];
      }
      else
        ok= mbf.pBOdq.dialogQuery(" - (1st OR 2nd)",
                                  "Enter 1st condition list name (from list or type it)",
                                  "Enter 2nd condition list (from list or type it)",
                                  "Enter new condition list (from list or type it) to save result",
                                  "", "", "",
                                  cdList.condNames, cdList.nCondLists);
      String
        cdName1= mbf.pBOdq.data1,
        cdName2= mbf.pBOdq.data2,
        userListName= mbf.pBOdq.data3;
      Condition
        cdLst1= mae.cdList.getConditionByName(cdName1),
        cdLst2= mae.cdList.getConditionByName(cdName2);
      if(userListName!=null)
      {
        mae.cdList.union(cdLst1,cdLst2,userListName);
        Util.saveCmdHistory("Setting condition list ["+userListName+
                            "] to OR of ["+cdName1+"] and ["+ cdName2+"]");
        mae.cdList.updateListCondLists();
      }
    }
    
    else if(actCmd.equals("CndLst:inter"))
    {
      boolean  ok;
      if(nArgs>=3)
      { /* get from the argList[] options */
        mbf.pBOdq.data1= argList[0];
        mbf.pBOdq.data2= argList[1];
        mbf.pBOdq.data3= argList[2];
      }
      else
        ok= mbf.pBOdq.dialogQuery(" - (1st AND 2nd)",
                                  "Enter 1st condition list name (from list or type it)",
                                  "Enter 2nd condition list (from list or type it)",
                                  "Enter new condition list (from list or type it) to save result",
                                  "", "", "",
                                  cdList.condNames, cdList.nCondLists);
      String
        cdName1= mbf.pBOdq.data1,
        cdName2= mbf.pBOdq.data2,
        userListName= mbf.pBOdq.data3;
      Condition
        cdLst1= mae.cdList.getConditionByName(cdName1),
        cdLst2= mae.cdList.getConditionByName(cdName2);
      if(userListName!=null)
      {
        Util.saveCmdHistory("Setting condition list ["+userListName+
                          "] to AND of ["+cdName1+"] and ["+ cdName2+"]");
        mae.cdList.intersection(cdLst1,cdLst2,userListName);
        mae.cdList.updateListCondLists();
      }
    }
    
    else if(actCmd.equals("CndLst:diff"))
    {
      boolean ok;
      if(nArgs>=3)
      { /* get from the argList[] options */
        mbf.pBOdq.data1= argList[0];
        mbf.pBOdq.data2= argList[1];
        mbf.pBOdq.data3= argList[2];
      }
      else
        ok= mbf.pBOdq.dialogQuery(" - Difference (1st - 2nd)",
                                  "Enter 1st condition list name (from list or type it)",
                                  "Enter 2nd condition list (from list or type it) to subtract",
                                  "Enter new condition list (from list or type it) to save result",
                                  "", "", "",
                                  cdList.condNames, cdList.nCondLists);
      String
        cdName1= mbf.pBOdq.data1,
        cdName2= mbf.pBOdq.data2,
        userListName= mbf.pBOdq.data3;
      Condition
        cdLst1= mae.cdList.getConditionByName(cdName1),
        cdLst2= mae.cdList.getConditionByName(cdName2);
      if(userListName!=null)
      {
        Util.saveCmdHistory("Setting condition list ["+userListName+
                            "] to DIFFERENCE of ["+cdName1+"] and ["+cdName2+"]");
        mae.cdList.difference(cdLst1,cdLst2,userListName);
        mae.cdList.updateListCondLists();
      }
    }
    
    else if(actCmd.equals("CndLst:reName"))
    { /* Rename gene set */
      String 
        userListName= (nArgs>=2)
                         ? argList[0]
                         : mbf.pdq.dialogQuery(
       "'existing' condition list name (from list or type it) to rename, then press OK",
                                               "", cdList.condNames,
                                               cdList.nCondLists),
        newListName= (nArgs>=2)
                        ? argList[1]
                        : mbf.pdq.dialogQuery(
            "'new' condition list name (from list or type it) for this condition, then press OK",
                                              "", cdList.condNames, 
                                              cdList.nCondLists);
      if(userListName!=null && userListName.length()>0 &&
         newListName!=null && newListName.length()>0)
      {
        String ans= (useArgListFlag)
                      ? "yes"
                      : mbf.pdq.dialogQuery("- rename list '"+userListName+
                                            "'? [yes|no]", "no");
        if(ans.equals("yes"))
        {
          Util.saveCmdHistory("Renamed condition list ["+userListName+"] to ["+
                              newListName+"]");
          mae.cdList.renameCondList(userListName, newListName, true);
          mae.cdList.updateListCondLists();
        }
      }
    } /* Rename gene set */
    
    else if(actCmd.equals("CndLst:rmv"))
    { /* remove HP list */
      String
        userListName= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery("condition list name (from list or type it) to remove, then press OK",
                                              "", cdList.condNames,
                                              cdList.nCondLists);
      if(userListName!=null && userListName.length()>0)
      {
        String areYouSure= (useArgListFlag)
                             ? "yes"
                             : mbf.pdq.dialogQuery("- are you sure?[yes|no]", "no");
        if(areYouSure.equals("yes"))
        {
          boolean flag= mae.cdList.rmvCondList(userListName, false, true);
          mae.cdList.updateListCondLists();
          if(flag)
            Util.saveCmdHistory("Removed condition list ["+userListName+"]");
          else
          {
            Util.showMsg("Can't remove condition list ["+userListName+
                         "] - check spelling");         
            Util.popupAlertMsg("Can't remove condition list", 
                               "Can't remove condition list ["+userListName+
                                "] - check spelling.\n",
                               4, 80);
          }
        }
      }
    } /* remove HP list */
    
    else if(actCmd.equals("CndLst:Load"))
    {
      String hclbsFileName= (nArgs>=1)
                              ? argList[0]
                              : promptFileName("Open disk condition list file",
                                               mae.defDir,
                                               "",
                                               "State",      /* sub dir */
                                               ".hcl",
                                               false,        /* saveMode*/
                                               true          /* useFileDialog */
                                               );
      //mae.us.readUserHPcondList(hclFileName, "@FULL-PATH");
      return(true);
    }
    /* - - - - - - - - - - - - */
    
    else if(actCmd.equals("State:adjStateScr"))
    { /* make popup scrollers preferences window */
      Util.saveCmdHistory("Popup State Scrollers");
      stateScr.regenerateScrollers(false);
    }
    
    /* Preferences */
    else if(actCmd.equals("setWebDB"))
    { /* get database name */
      mae.saCodeBase= (nArgs>=1)
                        ? argList[0]
                        : mbf.pdq.dialogQuery("Define Web DB URL",
                                              mae.saCodeBase);
    }
    
    else if(actCmd.equals("setDBname"))
    { /* get database name */
      cfg.database= (nArgs>=1)
                      ? argList[0]
                      : mbf.pdq.dialogQuery("Define DB name", cfg.database);
      Util.saveCmdHistory("Assigning DB name to ["+cfg.database+"]");
    }
    
    else if(actCmd.equals("setDBtitle"))
    { /* get database title */
      cfg.dbSubset= (nArgs>=1)
                      ? argList[0]
                      : mbf.pdq.dialogQuery("Define DB title", cfg.dbSubset);
      /* [CHECK] update title of main window */
      mae.browserTitle= mae.maeTitleVer + " - " + cfg.dbSubset;
      Util.saveCmdHistory("Assigning DB title to ["+cfg.dbSubset+"]");
      mae.mbf.setTitle(mae.browserTitle);
      mae.repaint();
    }  
        
    else if(actCmd.equals("setGeoPlatformID"))
    { /* get database title */
      String arg= (nArgs>=1)
                    ? argList[0]
                    : mbf.pdq.dialogQuery("Define GEO Platform ID",
                                          cfg.geoPlatformID);
      if(arg==null || !arg.startsWith("GPI") ||
         (arg.length()>4 && !Character.isDigit(arg.charAt(4))))
      { /* bad platform ID */        
        Util.showMsg2("Illegal GEO Platform ID. Should be of the form:",
                    Color.white, Color.red );
        Util.showMsg3("'GPInnnn' where nnnn is a number. Try it again.",
                    Color.white, Color.red );
        Util.popupAlertMsg("Illegal GEO Platform ID", 
                           "Illegal GEO Platform ID. Should be of the form: 'GPInnnn'\n"+
                           "where nnnn is a number. Set this with (Edit | Preferences) menu\n"+
                           " and try it again.",
                           6, 60);
      }
      else
      { /* save valid GEO Platform ID */
        cfg.geoPlatformID= arg; 
        /* [CHECK] update title of main window */
        mae.browserTitle= mae.maeTitleVer + " - " + cfg.geoPlatformID;
        Util.saveCmdHistory("Assigning GEO Platform ID as ["+cfg.geoPlatformID+"]");
        mae.mbf.setTitle(mae.browserTitle);
        mae.repaint();
      }
    }
    
    else if(actCmd.startsWith("FontFamily:"))
    { /* Set Font family */
      int idx= (nArgs>=1) ? Util.cvs2i(argList[0]) : actCmd.indexOf(":");
      if(idx!=-1)
      { /* change the font family */
        String ffName= actCmd.substring(idx+1);
        cfg.fontFamily= ffName;
        mae.updatePseudoImgFlag= true;
        /* [TODO] update title of main window */
        Util.saveCmdHistory("Setting text font family to ["+ffName+"]");
        mae.repaint();
      }
    } /* Set Font family */
    
    else if(actCmd.startsWith("Scr:adjAll"))
    { /* Adjust all Filter thresholds */
      Util.saveCmdHistory("Popup and adjust all Filter threshold scrollers");
      stateScr.regenerateScrollers(true);
    }
    
    else if(actCmd.equals("ResizeMem"))
    { /* Resize LAX file data for MAExplorer. */
      /* This will adjust the startup process size.
       * The LAX file (e.g., C:/ProgramFiles/MAExplorer/MAExplorer.lax)
       * specifies the startup sizes for both the heap and stack.
       * It determines the current size, asks the user for a new size
       * then creates a new file if it succeeds in the Q and A.
       */    
    
      int newMemSize= mae.us.resizeLAXfileData();
      if(newMemSize>0)
        {
          Util.showMsg2("Changed MAExplorer memory size to  "+
                        newMemSize+" bytes.", Color.white, Color.blue );
          Util.showMsg3("You must restart MAExplorer to use the new memory size limits.",
                        Color.white, Color.blue );
          Util.popupAlertMsg("Changed MAExplorer memory size", 
                             "Changed MAExplorer memory size to  "+
                             newMemSize+" bytes. You must\n"+
                             "restart MAExplorer to use the new memory size limits.",
                             4, 60);
          Util.saveCmdHistory("Resize memory");        
          Util.sleepMsec(5000);
        }
      else
      {
        Util.showMsg2("FAILED! Unable to change startup memory size",
                      Color.white, Color.red );
        Util.popupAlertMsg("FAILED! Unable to change startup memory size ", 
                           "FAILED! Unable to change startup memory size - illegal size.",
                           4, 60);
      }
    } /* Resize LAX file data for MAExplorer. */
    
    /* [5] ****** PLOT pull down menu ************** */
    else if(actCmd.equals("Plot:origImg"))
    {
    /* [TODO] check to see if image exists in samples table.
     * If it does not, just print a msg instead of popping up
     * a browser.
     */
      Util.saveCmdHistory("Display original microarray image for ["+
      mae.ms.hpName+"]");
      /*
      if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("EM - imgURL='"+mae.getCodeBase()+mae.ms.oImageName+"'");
      */
      //if(mae.ms.oImageName!=null)
      //  mae.util.popupViewer(mae.getCodeBase(),mae.ms.oImageName,"MaeAux");
      
      /* Test if /Image directory exists for image and then create image */
      mae.util.popupImageOfHPsample(mae.ms);
    }
    
    else if(actCmd.equals("Plot:HP_XYscatPlot"))
    {
      //if(mae.hps.nHP<2 || mae.msX==mae.msY)
      if(mae.hps.nHP<2)
      {
        Util.showMsg("Can't plot HP-X vs HP-Y for same sample");
        Util.popupAlertMsg("Can't plot HP-X vs HP-Y for same sample", 
                           "Can't plot HP-X vs HP-Y for same sample.Change either\n"+
                           "HP-X or HP-Y to a different sample and try again.",
                           4, 60);
        return(true);
      }
      
      String
        changeStr= (mae.isZscoreFlag)
                     ? "("+sf1+"-"+sf2+") Zscores"
                     : "("+sf1+"/"+sf2+") ratios",
        ss= (mae.useRatioDataFlag) ? changeStr : "intensities",
        sTitle= "Scatter plot of HP-X vs HP-Y "+ss;
      Util.saveCmdHistory(sTitle);
      Util.showMsg2("Click on a point to set current gene.");
      Util.showMsg3("Scroll plot axes to zoom into a region.");
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_HP_XY_INTENS,
                                           sTitle, "ScatterPlotXY",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.LABEL |
                                           pur.UNIQUE),5);
    }
    
    else if(actCmd.equals("Plot:F1F2scatterPlot"))
    {
      String sTitle= "Scatter plot of "+sf1+" vs "+sf2+
                     " spot intensities for ["+ mae.ms.hpName+"]";
      Util.saveCmdHistory( sTitle);
      Util.showMsg2("Click on a point to set current gene.");
      Util.showMsg3("Scroll plot axes to zoom into a region.");
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_F1_F2_INTENS,
                                           sTitle,"ScatterPlotF1F2",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.LABEL |
                                           pur.UNIQUE),
                                           0);
    }
    
    else if(actCmd.equals("Plot:Xcy3VsYcy3ScatrPlot"))
    {
      String sTitle= "Scatter plot of HP-X("+sf1+") vs HP-Y("+sf1+
                     ") spot intensities";
      Util.saveCmdHistory( sTitle);
      Util.showMsg2("Click on a point to set current gene.");
      Util.showMsg3("Scroll plot axes to zoom into a region.");
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_F1_F2_INTENS,
                                           sTitle,"Xcy3VsYcy3ScatrPlot",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.LABEL |
                                           pur.UNIQUE),
                                           0);
    }
    
    else if(actCmd.equals("Plot:Xcy5VsYcy5ScatrPlot"))
    {
      String sTitle= "Scatter plot of HP-X("+sf2+") vs HP-Y("+sf2+
                     ") spot intensities";
      Util.saveCmdHistory( sTitle);
      Util.showMsg2("Click on a point to set current gene.");
      Util.showMsg3("Scroll plot axes to zoom into a region.");
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_F1_F2_INTENS,
                                           sTitle,"Xcy5VsYcy5ScatrPlot",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.LABEL |
                                           pur.UNIQUE),
                                           0);
    }
    
    else if(actCmd.equals("Plot:Xcy3VsYcy5ScatrPlot"))
    {
      String sTitle= "Scatter plot of HP-X("+sf1+") vs HP-Y("+sf2+
                     ") spot intensities";
      Util.saveCmdHistory( sTitle);
      Util.showMsg2("Click on a point to set current gene.");
      Util.showMsg3("Scroll plot axes to zoom into a region.");
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_F1_F2_INTENS,
                                           sTitle,"Xcy3VsYcy5ScatrPlot",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.LABEL |
                                           pur.UNIQUE),
                                           0);
    }
    
    else if(actCmd.equals("Plot:Xcy5VsYcy3ScatrPlot"))
    {
      String sTitle= "Scatter plot of HP-X("+sf2+") vs HP-Y("+sf1+
                     ") spot intensities";
      Util.saveCmdHistory( sTitle);
      Util.showMsg2("Click on a point to set current gene.");
      Util.showMsg3("Scroll plot axes to zoom into a region.");
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_F1_F2_INTENS,
                                           sTitle,"Xcy5VsYcy3ScatrPlot",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.LABEL |
                                           pur.UNIQUE),
                                           0);
    }
    
    else if(actCmd.equals("Plot:MvsA-F1F2ScatrPlot"))
    {
      String sTitle= "M vs A plot of current sample";
      Util.saveCmdHistory( sTitle);
      Util.showMsg2("Click on a point to set current gene.");
      Util.showMsg3("Scroll plot axes to zoom into a region.");
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_F1_F2_MVSA,
                                           sTitle,"MvsA-F1F2ScatrPlot",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.LABEL |
                                           pur.UNIQUE),
                                           0);
    }
    
    else if(actCmd.equals("Plot:F1F2ratioHist"))
    {
      String changeStr= (mae.isZscoreFlag)
                           ? "("+sf1+"-"+sf2+") Zscores"
                           : "("+sf1+"/"+sf2+") ratios";
      Util.saveCmdHistory("Display histogram of "+changeStr+
                          " ratios of current H.P.");
      Util.showMsg2("Click on a bin to filter by ratio range.");
      Util.showMsg3("Scroll plot axes to zoom into a region.");
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_HIST_F1F2_RATIO,
                                           "Histogram of "+changeStr,
                                           "RatioHistF1F2",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.UNIQUE),10);
    }
    
    else if(actCmd.equals("Plot:HP_XYratioHist"))
    {
      if(mae.hps.nHP<2 || mae.msX==mae.msY)
      {
        Util.showMsg("Can't plot. Set HP-X & HP-Y to different samples.");
        Util.popupAlertMsg("Can't plot HP-X vs HP-Y for same sample", 
                           "Can't plot HP-X vs HP-Y for same sample. Change either\n"+
                           "HP-X or HP-Y to a different sample and try again.",
                           4, 60);
      }
      else
      {
        String changeStr= (mae.isZscoreFlag)
                           ? "(HP-X - HP-Y) Zscores"
                           : "(HP-X/HP-Y) ratios";
        Util.saveCmdHistory("Display histogram of "+changeStr);
        Util.showMsg2("Click on a bin to filter by ratio range");
        ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_HIST_HP_XY_RATIO,
                                             "Histogram of gene "+changeStr,
                                             "RatioHistXY",
                                             (pur.CUR_GENE | pur.FILTER |
                                             pur.SLIDER | pur.UNIQUE),
                                             -10);
      }
    }
    
    else if(actCmd.equals("Plot:HP_XYsetsRatioHist"))
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Can't plot. Enable HP-XY 'set' mode.");
        Util.popupAlertMsg("Can't plot HP-X vs HP-Y sets", 
                           "Can't plot HP-X vs HP-Y sets. Enable HP-XY 'set' mode\n"+
                           "in the Samples menu.'",
                           4, 60);
      }
      else
      {
        String changeStr= (mae.isZscoreFlag)
                            ? "(HP-X - HP-Y) 'sets' Zscores"
                            : "(HP-X/HP-Y) 'sets' ratios";
        Util.saveCmdHistory("Display ratio histogram of "+changeStr);
        Util.showMsg2("Click on a bin to filter by ratio range");
        ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_HIST_HP_XY_SETS_RATIO,
                                             "Histogram of "+changeStr,
                                             "RatioHistXYsets",
                                             (pur.CUR_GENE | pur.FILTER |
                                             pur.SLIDER | pur.UNIQUE),
                                             -5);
      }
    }
    
    else if(actCmd.equals("Plot:intensHist"))
    {
      mae.ms.calcIntensHist();   /* recompute the histogram */
      String
        changeStr= (mae.isZscoreFlag)
                     ? "("+sf1+"-"+sf2+") Zscores"
                     : "("+sf1+"/"+sf2+") ratios",
        ss= (mae.useRatioDataFlag) ? changeStr : "Intensity",
        sTitle= ss+" histogram of Filtered genes for ["+mae.ms.hpName+"]";
      Util.saveCmdHistory(sTitle);
      ShowPlotPopup spp= new ShowPlotPopup(mae, mae.PLOT_INTENS_HIST,
                                           sTitle, "IntensityHist",
                                           (pur.CUR_GENE | pur.FILTER |
                                           pur.SLIDER | pur.UNIQUE),
                                           -15);
    }
    
    else if(actCmd.equals("Plot:geneEP"))
    {
      Util.saveCmdHistory("Display a gene's expression profile for HP-E");
      Util.showMsg2("Click on spot in array to set new current gene");
      mbf.exprProfilePopup= new ExprProfilePopup(mae, -1,
                                                 "Gene Expression Profile",
                                                 null, /* Font */
                                                 0,    /*preferredWidth */
                                                 0,    /*preferredHeight */
                                                 true /* drawLabelsFlag */   /* if true */
                                                 );
      mae.repaint();
    }
    
    else if(actCmd.equals("Plot:FltrGeneEP"))
    {
      String sTitle= "Expression Profiles of list of Filtered genes for HP-E";
      Util.saveCmdHistory(sTitle);
      ShowExprProfilesPopup fep= new ShowExprProfilesPopup(mae, fc.workingCL,
                                                           null, null, null, 0, 
                                                           false, sTitle, "EP-list",
                                                           (pur.CUR_GENE | pur.FILTER |
                                                           pur.SLIDER | pur.UNIQUE),
                                                           mae.useEPoverlayFlag,
                                                           false, /* useLargeFrameFlag */
                                                           false  /* just CloneID & name */
                                                           );
      mae.repaint();
    }
    
    else if(actCmd.equals("Plot:dispSOMclstr"))
    {
      notAvailableYet();       /* "Not available yet." */
    }
    
    else if(actCmd.equals("Plot:dispHPclstr"))
    {
      //mae.plotMode= mae.PLOT_CLUSTER_HYBSAMPLES;
      notAvailableYet();       /* "Not available yet." */
    }
    
    else if(actCmd.equals("Plot:SaveAs"))
    { /* Save Canvas as GIF image */
      String
        defGifDir= Util.rmvFinalSubDirectory(mae.defDir, "Report",true),
        oGifFileName= promptFileName("Enter GIF file name", defGifDir,
                                     "maeImagePlot.gif",
                                     null,   /* sub dir */
                                     ".gif",
                                     true,   /* saveMode*/
                                     true    /* useFileDialog */
                                     );
      if(oGifFileName!=null)
        mae.is.siCanvas.drawGifFile(oGifFileName);
    }
    
    /* [6] ****** VIEW pull down menu ************** */
    
    /* [7] ****** REPORT pull down menu ************* */
    else if(actCmd.equals("RPT:HPinfo"))
    {
      mae.reportMode= mae.RPT_TBL_SAMPLES_DB_INFO;
      String sTitle= "Hybridized samples information";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "HPinfo", pur.UNIQUE);
    }
    
    else if(actCmd.equals("RPT:HPwebLinks"))
    {
      mae.reportMode= mae.RPT_TBL_SAMPLES_WEB_LINKS;
      String sTitle= "Web links for hybridized samples";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.RPT_FMT_DYN, 
                             "HPwebLinks", pur.UNIQUE);
    }
    
    else if(actCmd.equals("RPT:xtraHPinfo"))
    {
      mae.reportMode= mae.RPT_TBL_HP_DB_INFO;
      String sTitle= "Extra information on hybridized sample.";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle,
      mae.reportMode, mae.tblFmtMode,
      "ExtraHPinfo", pur.UNIQUE);
    }
    
    else if(actCmd.equals("RPT:MAEprjDB"))
    {
      mae.reportMode= mae.RPT_TBL_MAE_PRJ_DB;
      String sTitle= "MAE Projects Database";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "MAEprjDB", pur.UNIQUE);
    }
    
    else if(actCmd.equals("RPT:allNamedC"))
    {
      mae.reportMode= mae.RPT_TBL_NAMED_GENES;
      String sTitle= "All named genes";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "AllNamedGenes", (pur.CUR_GENE | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:EGLgenes"))
    {
      mae.reportMode= mae.RPT_TBL_EDITED_GENE_LIST;
      String sTitle=  "Genes in 'Edited Gene List'";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "EGLgenes", (pur.CUR_GENE | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:NormGeneSet"))
    {
      mae.reportMode= mae.RPT_TBL_NORMALIZATION_GENE_LIST;
      String sTitle=  "Genes in 'Normalization Gene List'";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "NormGeneSet", (pur.CUR_GENE | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:GeneClassC"))
    {
      mae.reportMode= mae.RPT_TBL_GENE_CLASS;
      String sTitle= "Genes in current Gene Class ["+
                     mae.gct.curGeneClassName+"]";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "CurGeneClass", (pur.CUR_GENE | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:filterC"))
    {
      mae.reportMode= mae.RPT_TBL_FILTERED_GENES;
      String sTitle= "Filtered genes";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                            "FilteredGenes",
                            (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:highHP_XY"))
    {
      mae.reportMode= mae.RPT_TBL_HIGH_RATIO;
      String sTitle= "Genes with highest HP-X/HP-Y ratios";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "HighHP_X/Y",
                             (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:lowHP_XY"))
    {
      mae.reportMode= mae.RPT_TBL_LOW_RATIO;
      String sTitle= "Genes with lowest HP-X/HP-Y ratios";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "LowHP_X/Y", 
                             (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:highHP_F1F2"))
    {
      mae.reportMode= mae.RPT_TBL_HIGH_F1F2;
      String
        changeStr= (mae.isZscoreFlag)
                     ? "("+sf1+"-"+sf2+") Zscores"
                     : "("+sf1+"/"+sf2+") ratios",
        sTitle= "Genes with highest "+changeStr +" for sample "+
                mae.ms.fullStageText;
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "HighHP_F1/F2",
                             (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:lowHP_F1F2"))
    {
      mae.reportMode= mae.RPT_TBL_LOW_F1F2;
      String
        changeStr= (mae.isZscoreFlag)
                      ? "("+sf1+"-"+sf2+") Zscores"
                      : "("+sf1+"/"+sf2+") ratios",
        sTitle= "Genes with lowest "+changeStr +" for sample "+
                mae.ms.fullStageText;
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "LowHP_F1/F2",   
                             (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:EPFilter"))
    {
      mae.reportMode= mae.RPT_TBL_EXPR_PROFILE;
      String sTitle= "Expression profiles of Filtered genes for HP-E";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                            "EPFilter",    
                            (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:HP-XY-setStat"))
    {
      mae.reportMode= mae.RPT_TBL_HP_XY_SET_STAT;
      String sTitle= "HP-X/Y 'set' statistics of Filtered genes";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "HP-XY-setStat",    
                             (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:OCLstat"))
    {
      if(mae.cdList.curOCLidx==-1)
      {
        Util.showMsg("Can't report Ordered Condition List F-test filter data");
        Util.showMsg2("You must select 'current Ordered Conditions List' first!");
        Util.showMsg3("Then, select current OCL F-test");
        Util.popupAlertMsg("Can't report Ordered Condition List F-test filter data", 
                           "You must select current Ordered Conditions List first!\n"+
                           "1. Use (Samples menu | Choose named condition lists of samples)\n"+
                           "2. Use (Samples menu | Choose ordered lists of conditions)"+
                           "3. Enable (Filter menu | Filter by current Ordered Condition List\n"+
                           "   (OCL) F-Test [p-Value] slider [RB])"+
                           "4. Then, try generating this report again.",
                           10, 60);
        return(true);
      }
      mae.reportMode= mae.RPT_TBL_OCL_STAT;
      String sTitle= "Ordered Condition List statistics of Filtered genes";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "HP-OCL-Stat",    
                             (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:HP_HPcorr"))
    {
      mae.reportMode= mae.RPT_TBL_HP_HP_CORR;
      String sTitle= "Correlation coefficients for active samples";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "HP_HPcorr",
                             (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:CalDNAstat"))
    {
      mae.reportMode= mae.RPT_TBL_CALIB_DNA_STAT;
      String
      sTitle= "Calibration DNA Statistics for active samples";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "CalDNAstat", 
                             (pur.CUR_GENE | pur.FILTER |pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:HPmeanVarStat"))
    {
      mae.reportMode= mae.RPT_TBL_HP_MN_VAR_STAT;
      String sTitle= "Mean and variance statistics for active samples";
      Util.saveCmdHistory("Report of "+ sTitle);
      Report rpt= new Report(mae, sTitle, mae.reportMode, mae.tblFmtMode,
                             "HPmeanVarStat", 
                             (pur.CUR_GENE | pur.FILTER | pur.UNIQUE));
    }
    
    else if(actCmd.equals("RPT:maxGenesDialog"))
    { /* get database name */
      String sR= (nArgs>=1)
                    ? argList[0]
                    : mbf.pdq.dialogQuery("Set max # genes in highest/lowest report",
                                          (""+cfg.maxGenesToRpt));
      int val= Util.cvs2i(sR);
      if(val>0 && val<mae.mp.maxGenes)
        cfg.maxGenesToRpt= val;
      Util.saveCmdHistory("Set max # genes in highest/lowest report to "+
      cfg.maxGenesToRpt);
    }
    
    /* [8] ****** PLUGIN pull down menu ************* */    
    else if(actCmd.startsWith("RLO:"))
    { /* check for "RLO:RscriptName" and then eval the script */
      if(processRLO(actCmd))
        mae.repaint();   /* only repaint if successufl! */
    }
    
    else if(actCmd.equals("Plugin:TestCode"))
    { /* invoke TestPluginCode to test associated plugin code */
      /* This is enabled if mae.DBUG_TEST_PLUGIN_FLAG is set.
       * The class that the Plugin invokes should be linked
       * with MAExplorer and invoked by TestPlugin menu that
       * then invokes the class. This means that you need to
       * edit TestPluginCode.java to invoke the code you want to debug
       * with MAExplorer.
       */
      try
      { new TestPluginCode(mae); }
      catch(Exception eTPC)
      {
        Util.showMsg1("Can't Test Plugin Code - class not loaded with MAExplorer.",
                      Color.white, Color.red);
        Util.showMsg2(eTPC.toString(), Color.white, Color.red);
        eTPC.printStackTrace();
        Util.popupAlertMsg("Can't Test Plugin Code",
                           "Can't Test Plugin Code.\n"+eTPC.toString(),
                           10, 80);
      }
    } /* build Plugin code testing command */
    
    /* [9] ****** HELP pull down menu ************* */
    else if(actCmd.equals("DOC:Home"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"../index.html", "MaeAux");
    }
    
    else if(actCmd.equals("DOC:Intro"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"index.html", "MaeAux");
    }
    
    else if(actCmd.equals("DOC:Ovr"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeDoc.html#keyPoints",
                          "MaeAux");
    }
    else if(actCmd.equals("DOC:PDFs"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeDocPDF.html", "MaeAux");
    }
    
    else if(actCmd.equals("DOC:ShortTut"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeDocA.html", "MaeAux");
    }
    
    else if(actCmd.equals("DOC:AdvTut"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeDocB.html", "MaeAux");
    }
    
    else if(actCmd.equals("DOC:Mnu"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeDoc2.html#menuSummary",
                           "MaeAux");
    }
    
    else if(actCmd.equals("DOC:RefMan"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeHelp.html", "MaeAux");
    }
    
    else if(actCmd.equals("DOC:RefMan-file"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.1.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-samples"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.2.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-edit"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.3.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-GeneClass"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.4.1.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-Normalization"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.4.2.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-Filter"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.4.3.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-Plot"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.4.4.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-Cluster"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.4.5.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-view"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.5.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-plugins"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.6.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    else if(actCmd.equals("DOC:RefMan-help"))
    {
      String sURL= "http://maexplorer.sourceforge.net/MaeRefMan/hmaeDoc2.7.html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    
    else if(actCmd.equals("DOC:MAEPlugins"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"../MAEPlugins/index.html",
                          "MaeAux");
    }
    
    else if(actCmd.equals("DOC:RtestPlugins"))
    {
      String sURL= "http://cvs.sourceforge.net/viewcvs.py/*checkout*/maexplorer/maeplugins/RtestPlugin/RtestPlugin.html?rev=HEAD&content-type=text/html";
      mae.util.popupViewer(null, sURL, "MaeAux");
    }
    
    else if(actCmd.equals("DOC:dataExpl"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeDoc3.html", "MaeAux");
    }
    
    else if(actCmd.equals("DOC:Gloss"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeDocF.html", "MaeAux");
    }
    
    else if(actCmd.equals("DOC:Index"))
    {
      mae.util.popupViewer(null, mae.docCodeBase+"hmaeDoc.html#maeIndex",
                           "MaeAux");
    }
    
    else if(actCmd.startsWith("DOC:HelpMenu"))
    { /* look for "HelpMenu1", "HelpMenu2", etc. */
      if(cfg.sHelpURL!=null)
      { /* chk menus */
        cfg.nHelpMenus= cfg.sHelpURL.length;
        for(int h= 1;h<=cfg.nHelpMenus;h++)
        { /* find particular HelpUrl1, HelpURL2, etc */
          String
          name= "DOC:HelpMenu"+h;  /* 1, 2, etc */
          if(actCmd.equals(name))
          {
            mae.util.popupViewer(null, cfg.sHelpURL[h-1], "MaeAux");
            break;
          }
        }
      }
    } /* look for "HelpMenu1", "HelpMenu2", etc. */
    
    else if(actCmd.equals("DOC:About"))
    { /* show "About" info */
      String
        maeStr= mae.browserTitle + " (Beta)\n" + mae.verStr,
        authorStr= "Authors: P. Lemkin, G. Thornwall, " +
                   "LECB, NCI-Frederick, Frederick, MD 21702",
        contactStr= "Contact: mae@ncifcrf.gov",
        dbStr= "Current database: "+cfg.database,
        homePageStr= "Home page: "+mae.DEF_BASESERVER;
      Util.saveCmdHistory("About: "+maeStr);
      Util.showMsg2(authorStr+". "+contactStr);
      Util.showMsg3(dbStr);
      String sTbl= maeStr+"\n"+ authorStr+"\n"+contactStr+"\n"+
                   homePageStr+"\n\n"+dbStr+"\n";
      ShowStringPopup t= new ShowStringPopup(mae, sTbl,11,70,
                                             mae.rptFontSize,
                                             "About MAExplorer", 0, 0,
                                             "About", pur.UNIQUE, "maeAbout.txt");
    } /* show "About" info */
    
    else
    { /* see if changing the samples */
      String s= "[EM-HA]: ill-command["+ actCmd + "]";
      mae.logDRYROTerr(s);
      Util.showMsg(s);
    }
    
    mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    
    return(true);
  } /* processActionEventByName */
  
  
  /**
   * processCheckboxStateChangedByItem() - handle checkbox menu item state changed events.
   * NOTE: need to implement radio groups here since AWT only
   * implements radio groups for checkboxes, not for Checkbox MenuItems.
   * We split the code so you can invoke commands via a client-server plugin.
   * @param item is the check box item to process
   * @param itemName is the associated name for debugging purposes
   * @return true if succeed.
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see Util#showFeatures
   */
  static boolean processCheckboxStateChangedByItem(Checkbox cbItem, String cbItemName)
  { /* processCheckboxStateChangedByItem */
    Util.showMsg("");         /* clear info area */
    Util.showFeatures("",""); /* clear info area */
    if(!mae.mReady)
    {
      Util.showMsg2("Wait until finished loading before doing commands.");
      return(false);    /* not ready to accept events */
    }
    
    Util.showMsg("");         /* clear info area */
    Util.showFeatures("",""); /* clear info area */
    
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("EM:checkboxName='"+cbItemName+"'");
    */
    
    //mae.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    /* [1] handle separate checkboxes */
    //if(mbf.<future-checkbox>==cbItem)
    // {
    //   mae.<todo>Flag= mbf.<future-checkbox>.getState();
    //   return(true);
    // }
    
    //mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    return(false);
  } /* processCheckboxStateChangedByItem */
  
  
  
  /**
   * processCheckboxMenuItemStateChangedByitem() - process CheckboxMenuItem state
   * changed events by item.
   * NOTE: need to implement radio groups here since AWT only
   * implements radio groups for checkboxes, not for Checkbox MenuItems.
   * We split the code so you can invoke commands via a client-server plugin.
   * @param item is the check box menu item to process
   * @param itemName is the associated name for debugging purpose
   * @see ClusterGenes
   * @see CompositeDatabase#recalcNorms
   * @see CompositeDatabase#recalcGlobalStats
   * @see Filter#computeWorkingGeneList
   * @see HPxyData#setupDataStruct
   * @see MAExplorer#chkIfCache
   * @see MAExplorer#repaint
   * @see MenuBarFrame#setHP_XYlabels
   * @see PopupRegistry#updateCurGene
   * @see PopupRegistry#updateFilter
   * @see StateScrollers#regenerateScrollers
   * @see SamplesTable#lookupFieldIdxAndRemapFieldName
   * @see ShowStringPopup
   * @see Util#setLogMsgWindowVisible
   * @see Util#setLogHistoryWindowVisible
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see Util#saveCmdHistory
   * @see #setClusterDisplayState
   * @see #setCVmodeState
   * @see #setCy3Cy5RatioRangeState
   * @see #setEditState
   * @see #setGoodSpotModeState
   * @see #setIntensityRangeState
   * @see #setNormalizationState
   * @see #setPlotState
   * @see #setRatioRangeState
   * @see #setReportFontSizeState
   * @see #setReportFontSizeState
   * @see #setSIrangeState
   * @see #setSImodeState
   * @see #setSIcompareModeState
   * @see #setViewerDisplayState
   */
  static boolean processCheckboxMenuItemStateChangedByitem(CheckboxMenuItem item,
                                                           String itemName)
  { /* processCheckboxMenuItemStateChangedByitem */
    cfg= mae.cfg;
    pur= mae.pur;
    stateScr= mae.stateScr;
        
    if(!mae.mReady)
    {
      Util.showMsg2("Wait until finished loading before doing commands.");
      return(false);    /* not ready to accept events */
    }
    boolean
    groupFlag= false,  /* set for groups of menu items using for-loop */
    flag= true;
    
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("EM:checkboxMenuItemName='"+itemName+"'");
    */
    
    String
      sf1= cfg.fluoresLbl1,
      sf2= cfg.fluoresLbl2;
    
    if(mae.useCy5OverCy3Flag)
    { /* flip Cy3/Cy5 to Cy5/Cy3 */
      String sTmp= sf1;
      sf1= sf2;
      sf2= sTmp;
    }
    
    /* [2.1] ****** FILE pull down menu ****************** */
    
    /* [2.2] ****** SAMPLES pull down menu ****************** */
    if(mbf.miHPMuseHPxySets==item)
    {
      mae.useHPxySetDataFlag= item.getState();
      if(mae.useHPxySetDataFlag)
        Util.saveCmdHistory("Using HP-X & HP-Y 'sets' of multiple samples");
      else
        Util.saveCmdHistory("Using individual HP-X & HP-Y samples");
      
      mbf.setHP_XYlabels();    /* Set the GUI HP-X: and HP-Y: labels
                                * as singles or multiple (Class names)
                                */
      /* update data */
      mae.updatePseudoImgFlag= true;
      mae.cdb.hpXYdata.setupDataStruct(mae.useHPxySetDataFlag);
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    /* [2.3] ****** NORMALIZATION pull down menu ************* */
    
    else if(mbf.miNRMhousekeepNorm==item)
    {
      mae.normByHousekeepGenesFlag= setNormalizationState(item,"housekeeping genes");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("housekeeping genes", mae.normByHousekeepGenesFlag);
    }
    
    else if(mbf.miNRMgeneSetNorm==item)
    {
      mae.normByGeneSetFlag= setNormalizationState(item, "user gene set");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("user gene set", mae.normByGeneSetFlag);
    }
    
    else if(mbf.miNRMZscoreNorm==item)
    {
      mae.normByZscoreFlag= setNormalizationState(item, "Zscore");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("Zscore", mae.normByZscoreFlag);
    }
    
    else if(mbf.miNRMmedianNorm==item)
    {      
      mae.normByMedianFlag= setNormalizationState(item, "median");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("median", mae.normByMedianFlag);
    }
    
    else if(mbf.miNRMlogMedianNorm==item)
    {
      mae.normByLogMedianFlag= setNormalizationState(item, "log median");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("log median", mae.normByLogMedianFlag);
    }
    
    else if(mbf.miNRMZscoreMeanStdDevLogNorm==item)
    {
      mae.normByZscoreMeanStdDevLogFlag= setNormalizationState(item,"Z-score, stdDev, log");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("Z-score, stdDev, log",mae.normByZscoreMeanStdDevLogFlag);
    }
    
    else if(mbf.miNRMZscoreMeanAbsDevLogNorm==item)
    {
      mae.normByZscoreMeanAbsDevLogFlag= setNormalizationState(item,
                                                 "Z-score, mean abs.deviation, log");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("Z-score, mean abs.deviation, log",
                          mae.normByZscoreMeanAbsDevLogFlag);
    }
    
    else if(mbf.miNRMCalDNAnorm==item)
    {
      mae.normByCalDNAflag= setNormalizationState(item, "calibration DNA");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("calibration DNA", mae.normByCalDNAflag);
    }
    
    else if(mbf.miNRMscaleToMaxIntens==item)
    {
      mae.scaleDataToMaxIntensFlag= setNormalizationState(item, "scale to max. (65K)");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("scale to max. (65K)", mae.scaleDataToMaxIntensFlag);
    }
    
    else if(mbf.miNRMnoNorm==item)
    {
      setNormalizationState(item, "unnormalized");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("unnormalized", false);
    }
      
    else if(mae.DBUG_GENERIC_NORM_PLUGIN && mbf.miNRMtestGenericNormPlugin==item)
    { /* if debugging NormalizationPlugins using direct class mae.gnp */
      mae.testGenericNormPluginFlag= setNormalizationState(item, "Test Generic Norm Plugin");
      setZscoreNormalizationState(); /* Zscore norm state & adjust sliders if required */
      mae.cdb.recalcNorms("Test Gen. Norm Plugin ", mae.testGenericNormPluginFlag);
    }
    
    /* Continue event testing chain */
    if(mbf.miNRMbkgrdCorr==item)
    {
      mae.bkgdCorrectFlag= item.getState();
      Util.saveCmdHistory("Background correction is turned " +
                          (String)((mae.bkgdCorrectFlag) ? "ON" : "OFF"));
      mae.cdb.recalcNorms("background correction", mae.bkgdCorrectFlag);
    }
    
    else if(mbf.miNRMratioMedianCorrection==item)
    {
      mae.ratioMedianCorrectionFlag= item.getState();
      Util.saveCmdHistory("Ratio median correction is turned " +
                         (String)((mae.ratioMedianCorrectionFlag) ? "ON" : "OFF"));
      mae.cdb.recalcNorms("background correction", mae.bkgdCorrectFlag);
    }
    
    else if(mbf.miNRMgoodSpotsStats==item)
    {
      mae.useGoodSpotsForGlobalStatsFlag= item.getState();
      Util.saveCmdHistory("Using good spots data for global array statistics is turned " +
                          (String)((mae.useGoodSpotsForGlobalStatsFlag)
                                     ? "ON" : "OFF"));
      /* [TODO] recompute means, stddev, etc calibration stats
       * for all arrays as required. See where computed in first
       * place and either call that method with a data filter for
       * good spots or write a new method to do it. This could live in
       * MaHybridSample.
       */
      mae.cdb.recalcGlobalStats(mae.useGoodSpotsForGlobalStatsFlag);
    }
    
    /* [2.4] ****** EDIT pull down menu ************* */
    else if(mbf.miEMeditNop==item)
    {
      mae.editMode= mae.EDIT_NOP;
      setEditState(item);
      Util.saveCmdHistory(
           "Seting EGL mode off. Clicking on gene is a no-op (does nothing)");
      mae.repaint();
    }
    
    else if(mbf.miEMeditAdd==item)
    {
      mae.editMode= mae.EDIT_ADD;
      setEditState(item);
      Util.saveCmdHistory(
          "Setting EGL mode to add gene. Click on gene to add it to EGL (CTRL/Mouse)");
      mae.repaint();
    }
    
    else if(mbf.miEMeditRmv==item)
    {
      mae.editMode= mae.EDIT_RMV;
      setEditState(item);
      Util.saveCmdHistory(
          "Setting EGL mode to remove gene. Click on gene to remove it from EGL (SHIFT/Mouse)");
      mae.repaint();
    }
    
    else if(mbf.miEMautoScrollerPopup==item)
    {
      mae.autoStateScrPopupFlag= item.getState();
      String str= (mae.autoStateScrPopupFlag) ? "enabled." : "disabled.";
      Util.saveCmdHistory("Auto state-scroller popup window is "+ str);
    }
    
    else if(mbf.miEMclusterWorkingCL==item)
    {
      mae.clusterOnFilteredCLflag= item.getState();
      String str= (mae.clusterOnFilteredCLflag) ? "Filtered" : "all";
      Util.saveCmdHistory("Cluster on " + str + " genes");
    }
    
    /* Preferences toggles */
    else if(mbf.miFEMenableFIOcache==item)
    {
      mae.enableFIOcachingFlag= item.getState();
      mae.chkIfCache(); /* check if set up to cache from Web DB */
      Util.saveCmdHistory("Web DB caching is " +
                          (String)((mae.enableFIOcachingFlag) 
                                     ? "enabled" : "disabled"));
      if(mae.cacheFIOflag || !mae.saCodeBase.startsWith("http://"))
      {
        Util.showMsg2("No Mae Server Web DB defined, do 'Open Web DB'");
        Util.popupAlertMsg("No Mae Server Web DB defined", 
                           "No Mae Server Web DB defined, define it using\n"+
                           "the (File menu | Open Web DB) command.",
                           4, 60);
      }
    }
    
    else if(mbf.miFEMuseWebDB==item)
    {
      mae.useWebDBflag= item.getState();
      mae.chkIfCache(); /* check if set up to cache from Web DB */
      Util.saveCmdHistory(((String)((mae.enableFIOcachingFlag)
                                      ? "Use" : "Don't use")) + " Web DB");
      if(mae.cacheFIOflag || !mae.saCodeBase.startsWith("http://"))
      {
        Util.showMsg2("No Mae Server Web DB defined, do 'Open Web DB'");
        Util.popupAlertMsg("No Mae Server Web DB defined", 
                           "No Mae Server Web DB defined, define it using\n"+
                           "the (File menu | Open Web DB) command.",
                           4, 60);
      }
    }
    
    /* [2.3] ****** FILTER pull down menu ************* */
    
    else if(mbf.miFRMgeneClassMbrFilter==item)
    {
      mae.geneClassMbrFilterFlag= item.getState();
      if(mae.geneClassMbrFilterFlag)
        Util.saveCmdHistory( "Filtering genes by GeneClass membership");
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMuseGeneSetFilter==item)
    {
      mae.useGeneSetFilterFlag= item.getState();
      if(mae.useGeneSetFilterFlag)
        Util.saveCmdHistory( "Filter by genes in 'User Filter Gene Set'");
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMhistRatioRngFilter==item)
    {
      mae.useRatioHistCLflag= item.getState();
      if(mae.useRatioHistCLflag)
      {
        if(!pur.doesPopupExist("RatioHistF1/F2") &&
           !pur.doesPopupExist("RatioHistX/Y") &&
           !pur.doesPopupExist("RatioHistX/Ysets"))
        { /* no filtering until have a histogram!*/
          item.setState(false);
          mae.useRatioHistCLflag= false;
          Util.showMsg("NOTE: create a ratio histogram before setting this Filter option");
          Util.popupAlertMsg("No histogram was defined", 
                             "No histogram was defined. First create a ratio histogram"+ 
                             "before setting this Filter option",
                             4, 60); 
        }
        else
          Util.saveCmdHistory("Filter by genes in 'Ratio Histogram bins'");
      }
      else        
        Util.showMsg( "Not filtering by genes in ratio histogram bin");
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMhistIntensRngFilter==item)
    {
      mae.useIntensHistCLflag= item.getState();
      if(mae.useIntensHistCLflag)
      {
        if(!pur.doesPopupExist("IntensityHist") )
        { /* no filtering until have a histogram!*/
          item.setState(false);
          mae.useIntensHistCLflag= false;
          Util.showMsg(
             "NOTE: create intensity histogram before setting this Filter option");
          Util.popupAlertMsg("No histogram was defined", 
                             "No histogram was defined. First create ain intensity histogram"+ 
                             "before setting this Filter option",
                             4, 60);
        }
        else
          Util.saveCmdHistory("Filter by genes in 'Intensity Histogram bins'");
      }
      else
        Util.showMsg( "Not filtering by genes in intensity histogram bin");
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMusePosQuantDataFlag==item)
    { /* Filter by Positive intensity data if set */
      /* if not true, then allow negative intensity data */
      mae.usePosQuantDataFlag= item.getState();
      String sMsg= (mae.usePosQuantDataFlag) ? "" : "and negative ";
      Util.saveCmdHistory( "Filter by positive "+sMsg+"intensity data");
      fc.computeWorkingGeneList();
      mae.repaint();
    } /* Filter by Positive data if set */
    
    else if(mbf.miFRMposQuantModeMS==item)
    {
      mae.posQuantTestMode= mae.SS_MODE_MS;
      setPosQuantModeState(item);
      Util.saveCmdHistory("Filter postive data by current HP sample");
      mae.repaint();
    }
    
    else if(mbf.miFRMposQuantModeXY==item)
    {
      mae.posQuantTestMode= mae.SS_MODE_XY;
      setPosQuantModeState(item);
      Util.saveCmdHistory("Filter positive data by HP-X & HP-Y samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMposQuantModeXYsets==item)
    {
      mae.posQuantTestMode= mae.SS_MODE_XANDY_SETS;
      setPosQuantModeState(item);
      Util.saveCmdHistory("Filter positive data by HP-X & HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMposQuantModeE==item)
    {
      mae.posQuantTestMode= mae.SS_MODE_ELIST;
      setPosQuantModeState(item);
      Util.saveCmdHistory("Filter positive data by HP-E samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMuseGoodSpotDataFlag==item)
    {
      if(!mae.ms.hasQualChkFlag)
      { /* no QualCheck data  - must have for all samples */
        Util.showMsg2("Can't filter by 'Good Spot Data' - ignoring request.",
                      Color.white, Color.red);
        Util.showMsg3("There is no good spot (QualCheck) data in the database",
                      Color.white, Color.red);
        Util.popupAlertMsg("Can't filter by 'Good Spot Data'",
                           "Can't filter by 'Good Spot Data' - ignoring request.\n"+
                           "There is no good spot (QualCheck) data in the database.",
                           4, 60);
        mae.useGoodSpotDataFlag= false;
        mbf.miFRMusePosQuantDataFlag.setState(false);
      }
      else
      { /* do Good spot data filtering */
        mae.useGoodSpotDataFlag= item.getState();
        String sMsg= (mae.useGoodSpotDataFlag) ? "ON" : "OFF";
        Util.saveCmdHistory( "'Good Spot Filtering' is "+sMsg);
        stateScr.updateFilterScrollerUseCounter("Spot Quality", mae.useGoodSpotDataFlag);
        if(stateScr.isVisible || mae.autoStateScrPopupFlag)
          stateScr.regenerateScrollers(false);
        fc.computeWorkingGeneList();
        mae.repaint();
      }
    }
    
    else if(mbf.miFRMgoodSpotModeMS==item)
    {
      mae.goodSpotTestMode= mae.SS_MODE_MS;
      setGoodSpotModeState(item);
      Util.saveCmdHistory("Filter Good Spots by current HP samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMgoodSpotModeXY==item)
    {
      mae.goodSpotTestMode= mae.SS_MODE_XY;
      setGoodSpotModeState(item);
      Util.saveCmdHistory("Filter Good Spots by HP-X & HP-Y samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMgoodSpotModeXORYsets==item)
    {
      mae.goodSpotTestMode= mae.SS_MODE_XORY_SETS;
      setGoodSpotModeState(item);
      Util.saveCmdHistory("Filter Good Spots by HP-X or HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMgoodSpotModeXANDYsets==item)
    {
      mae.goodSpotTestMode= mae.SS_MODE_XANDY_SETS;
      setGoodSpotModeState(item);
      Util.saveCmdHistory("Filter Good Spots by HP-X and HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMgoodSpotModeE==item)
    {
      mae.goodSpotTestMode= mae.SS_MODE_ELIST;
      setGoodSpotModeState(item);
      Util.saveCmdHistory("Filter Good Spots by HP-E samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMuseDetValueSpotDataFlag==item)
    {
      if(!mae.ms.hasDetValueSpotDataFlag)
      { /* no Detection Value data  - must have for all samples */
        Util.showMsg2("Can't filter by 'Spot Detection Value Data' - ignoring request.",
                      Color.white, Color.red);
        Util.showMsg3("There is no good spot detection value data in the database",
                      Color.white, Color.red);
        Util.popupAlertMsg("Can't filter by Spot Detection Value Data'",
                           "Can't filter by 'Good Spot Data' - ignoring request.\n"+
                           "There is no good spot detection value data in the database.",
                           4, 60);
        mae.useDetValueSpotDataFlag= false;
        mbf.miFRMuseDetValueSpotDataFlag.setState(false);
      }
      else
      { /* do Detection value spot data filtering - toggle the switch */
        mae.useDetValueSpotDataFlag= item.getState();
        String sMsg= (mae.useDetValueSpotDataFlag) ? "ON" : "OFF";
        Util.saveCmdHistory( "'Spot Detection Value Filtering' is "+sMsg);
        stateScr.updateFilterScrollerUseCounter("Spot Detection Value",
                                                mae.useDetValueSpotDataFlag);
        if(stateScr.isVisible || mae.autoStateScrPopupFlag)
          stateScr.regenerateScrollers(false);
        fc.computeWorkingGeneList();
        mae.repaint();
      }
    }
     
    else if(mbf.miFRMOnlyGenesWithNonZeroDensityFlag==item)
    {
      mae.useOnlyGenesWithNonZeroDensityFlag= item.getState();
      fc.computeWorkingGeneList();
      Util.saveCmdHistory("Filter by genes with non-zero intensity");
      mae.repaint();
    }    
    
    else if(mbf.miFRMdetValueSpotModeMS==item)
    {
      mae.detValueSpotTestMode= mae.SS_MODE_MS;
      setDetValueSpotModeState(item);
      Util.saveCmdHistory("Filter Spots Corr. Coef. by current HP samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMdetValueSpotModeXY==item)
    {
      mae.detValueSpotTestMode= mae.SS_MODE_XY;
      setDetValueSpotModeState(item);
      Util.saveCmdHistory("Filter Spots Corr. Coef. by HP-X & HP-Y samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMdetValueSpotModeXANDYsets==item)
    {
      mae.detValueSpotTestMode= mae.SS_MODE_XANDY_SETS;
      setDetValueSpotModeState(item);
      Util.saveCmdHistory("Filter Spots Corr. Coef. by HP-X and HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMdetValueSpotModeE==item)
    {
      mae.detValueSpotTestMode= mae.SS_MODE_ELIST;
      setDetValueSpotModeState(item);
      Util.saveCmdHistory("Filter Spots Corr. Coef. by HP-E samples");
      mae.repaint();
    }
    
    else if(mbf.miFRM_EGLfilter==item)
    {
      mae.useEditedCLflag= item.getState();
      if(mae.useEditedCLflag)
        Util.saveCmdHistory("Filter spots by genes in 'Edited Genes List'");
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMgoodGeneListFilter==item)
    {
      int
      idxGipoQualCheck= mae.sampDB.lookupFieldIdxAndRemapFieldName("SamplesTable",
                                                                   "QualCheck");
      if(idxGipoQualCheck==-1)
      {
        Util.showMsg("Can't Filter genes by global 'Good Genes List'");
        Util.showMsg2("No Good Spot GIPO data is available in database.");
        Util.showMsg3("Try Filtering Good Spot data on a per-sample basis.");
        Util.popupAlertMsg("Can't Filter genes by global 'Good Genes List'",
                           "There is no Good Spot GIPO data is available in database.\n"+
                           "Can't Filter genes by global 'Good Genes List.\n"+
                           "Try Filtering Good Spot data on a per-sample basis.",
                           10, 80);
        return(true);
      }
      mae.useEditedCLflag= item.getState();
      if(mae.useEditedCLflag)
        Util.saveCmdHistory("Filter by genes in global 'Good Genes List'");
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMreplicateGenesFilter==item)
    {
      mae.useReplicateGenesFlag= item.getState();
      Util.saveCmdHistory("Filter by replicate genes");
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMtTestXYfilter==item)
    {/* single XY F1F2 t-test */
      mae.tTestXYfilterFlag= item.getState();
      if(mae.tTestXYsetsFilterFlag || mae.KS_TestXYsetsFilterFlag ||
         mae.F_TestOCLFilterFlag)
      { /* remove previous p-value associated test */
        stateScr.updateFilterScrollerUseCounter("p-Value",false);
        mbf.miFRMtTestXYsetsFilter.setState(false);
        mbf.miFRMksTestXYsetsFilter.setState(false);
        mbf.miFRMfTestOCLFilter.setState(false);
        mae.tTestXYsetsFilterFlag= false;
        mae.KS_TestXYsetsFilterFlag= false;
        mae.F_TestOCLFilterFlag= false;
      }
      stateScr.updateFilterScrollerUseCounter("p-Value",
                                              mae.tTestXYfilterFlag);      
      Util.saveCmdHistory("Filtering genes by HP-X,HP-Y t-Test filter");
      Util.showMsg2("");
      Util.showMsg3("");
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    } /* single XY F1F2 t-test */
    
    else if(mbf.miFRMtTestXYsetsFilter==item)
    { /* XYsets t-test */
      if(!mae.useHPxySetDataFlag)
      {
        mbf.miFRMtTestXYsetsFilter.setState(false);
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        Util.showMsg2("Use (Samples menu | Choose HP-X, HP-Y and HP-E samples");        
        Util.popupAlertMsg("Can't set HP-XY t-test filter", 
                           "Set 'Use HP-X and HP-Y sets' in Sample menu first!\n"+
                           "Use (Samples menu | Choose HP-X, HP-Y and HP-E samples",
                           20, 60);
        return(true);
      }
      mae.tTestXYsetsFilterFlag= item.getState();
      if(mae.tTestXYsetsFilterFlag || mae.KS_TestXYsetsFilterFlag ||
         mae.F_TestOCLFilterFlag)
      { /* remove previous p-value associated test */
        stateScr.updateFilterScrollerUseCounter("p-Value",false);
        if(mbf.miFRMtTestXYfilter!=null)
          mbf.miFRMtTestXYfilter.setState(false);
        mbf.miFRMksTestXYsetsFilter.setState(false);
        mbf.miFRMfTestOCLFilter.setState(false);
        mae.tTestXYfilterFlag= false;
        mae.KS_TestXYsetsFilterFlag= false;
        mae.F_TestOCLFilterFlag= false;
      }
      stateScr.updateFilterScrollerUseCounter("p-Value",
                                              mae.tTestXYsetsFilterFlag);      
      Util.saveCmdHistory("Filtering genes by HP-X,HP-Y 'sets' t-Test");
      Util.showMsg2("");
      Util.showMsg3("");
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    } /* XYsets t-test */
    
    else if(mbf.miFRMksTestXYsetsFilter==item)
    { /* XYsets KS-test */
      if(!mae.useHPxySetDataFlag)
      {
        mbf.miFRMksTestXYsetsFilter.setState(false);
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        Util.showMsg2("Use (Samples menu | Choose HP-X, HP-Y and HP-E samples");        
        Util.popupAlertMsg("Can't set HP-XY KS-test filter", 
                           "Set 'Use HP-X and HP-Y sets' in Sample menu first!\n"+
                           "Use (Samples menu | Choose HP-X, HP-Y and HP-E samples",
                           20, 60);
        return(true);
      }
      mae.KS_TestXYsetsFilterFlag= item.getState();
      if(mae.tTestXYfilterFlag || mae.tTestXYsetsFilterFlag || 
         mae.F_TestOCLFilterFlag)
      { /* remove previous p-value associated test */
        stateScr.updateFilterScrollerUseCounter("p-Value",false);
        if(mbf.miFRMtTestXYfilter!=null)
          mbf.miFRMtTestXYfilter.setState(false);
        mbf.miFRMtTestXYsetsFilter.setState(false);
        mbf.miFRMfTestOCLFilter.setState(false);        
        mae.tTestXYfilterFlag= false;
        mae.tTestXYsetsFilterFlag= false;
        mae.F_TestOCLFilterFlag= false;
      }
      stateScr.updateFilterScrollerUseCounter("p-Value",
                                              mae.KS_TestXYsetsFilterFlag);
      Util.saveCmdHistory("Filtering genes by HP-X,HP-Y 'sets' Kolmogorov-Smirnov Test");
      Util.showMsg2("");
      Util.showMsg3("");
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    } /* XYsets KS-test */
    
    else if(mbf.miFRMfTestOCLFilter==item)
    { /* OCL F-test */
      if(mae.cdList.curOCLidx==-1)
      {
        mbf.miFRMfTestOCLFilter.setState(false);
        Util.showMsg("You must select current Ordered Conditions List first!");
        Util.showMsg2("1. Use (Samples menu | Choose named condition lists of samples)");
        Util.showMsg3("2. Use (Samples menu | Choose ordered lists of conditions)");
        Util.popupAlertMsg("Can't set Ordered Condition List F-test filter", 
                           "You must select current Ordered Conditions List first!\n"+
                           "1. Use (Samples menu | Choose named condition lists of samples)\n"+
                           "2. Use (Samples menu | Choose ordered lists of conditions)",
                           10, 60);
        return(true);
      }
      mae.F_TestOCLFilterFlag= item.getState();
      if(mae.tTestXYfilterFlag || mae.tTestXYsetsFilterFlag || 
         mae.KS_TestXYsetsFilterFlag)
      { /* remove previous p-value associated test */
        stateScr.updateFilterScrollerUseCounter("p-Value",false);
        if(mbf.miFRMtTestXYfilter!=null)
          mbf.miFRMtTestXYfilter.setState(false);
        mbf.miFRMtTestXYsetsFilter.setState(false);
        mbf.miFRMksTestXYsetsFilter.setState(false);
        mae.tTestXYfilterFlag= false;
        mae.tTestXYsetsFilterFlag= false;
        mae.KS_TestXYsetsFilterFlag= false;
      }
      stateScr.updateFilterScrollerUseCounter("p-Value", mae.F_TestOCLFilterFlag);
      Util.saveCmdHistory("Filtering genes by current Ordered Conditions List (OCL) F-Test");
      Util.showMsg2("");
      Util.showMsg3("");
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    } /* OCL F-test */
    
    else if(mbf.miFRMspotCVfilter==item)
    { /* CV test */
      if(!mae.useHPxySetDataFlag &&
         (mae.cvTestMode==mae.SS_MODE_XSET ||
          mae.cvTestMode==mae.SS_MODE_YSET ||
          mae.cvTestMode==mae.SS_MODE_XORY_SETS ||
          mae.cvTestMode==mae.SS_MODE_XANDY_SETS))
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.useSpotCVfilterFlag= item.getState();
      if(mae.useSpotCVfilterFlag)
        Util.saveCmdHistory( "Filtering genes by Spot CV");
      stateScr.updateFilterScrollerUseCounter("Spot CV",mae.useSpotCVfilterFlag);
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    } /* CV test */
    
    else if(mbf.miFRMhighXYratioFilter==item)
    {
      mae.useHighRatiosFilterFlag= item.getState();
      if(mae.useHighRatiosFilterFlag)
      {
        mae.useLowRatiosFilterFlag= false;
        mbf.miFRMlowXYratioFilter.setState(false);
        Util.saveCmdHistory("Filtering genes by "+ cfg.maxGenesToRpt +
                            " with highest "+
                            ((mae.isZscoreFlag) ? "X-Y Zdiff" : "X/Y ratio"));
      }
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMlowXYratioFilter==item)
    {
      mae.useLowRatiosFilterFlag= item.getState();
      if(mae.useLowRatiosFilterFlag)
      {
        mae.useHighRatiosFilterFlag= false;
        mbf.miFRMhighXYratioFilter.setState(false);
        Util.saveCmdHistory("Filtering genes by "+ cfg.maxGenesToRpt +
                            " with lowest "+
                           ((mae.isZscoreFlag) ? "X-Y Zdiff" : "X/Y ratio"));
      }
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMthrSpotIntens==item)
    {
      if(!mae.useHPxySetDataFlag &&
         (mae.cvTestMode==mae.SS_MODE_XSET ||
          mae.cvTestMode==mae.SS_MODE_YSET ||
          mae.cvTestMode==mae.SS_MODE_XORY_SETS ||
          mae.cvTestMode==mae.SS_MODE_XANDY_SETS))
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.spotIntensFilterFlag= item.getState();
      Util.saveCmdHistory("Threshold Filter sliders set to [SI1:SI2].");
      stateScr.updateFilterScrollerUseCounter("Spot SI1",mae.spotIntensFilterFlag);
      stateScr.updateFilterScrollerUseCounter("Spot SI2",mae.spotIntensFilterFlag);
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensRngIn==item)
    {
      mae.spotIntensRangeMode= mae.RANGE_INSIDE;
      setSIrangeState(item);
      Util.saveCmdHistory(
          "Filtering by Spot Intensity. Include data inside [SI1:SI2] range");
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensRngOut==item)
    {
      mae.spotIntensRangeMode= mae.RANGE_OUTSIDE;
      setSIrangeState(item);
      Util.saveCmdHistory(
          "Filtering by Spot Intensity. Include data outside [SI1:SI2] range");
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensModeMS==item)
    {
      mae.spotIntensTestMode= mae.SS_MODE_MS;
      setSImodeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity of current HP sample");
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensModeXY==item)
    {
      mae.spotIntensTestMode= mae.SS_MODE_XY;
      setSImodeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity of HP-X or HP-Y samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensModeXsets==item)
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.spotIntensTestMode= mae.SS_MODE_XSET;
      setSImodeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity of HP-X or HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensModeYsets==item)
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.spotIntensTestMode= mae.SS_MODE_YSET;
      setSImodeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity of HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensModeXORYsets==item)
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.spotIntensTestMode= mae.SS_MODE_XORY_SETS;
      setSImodeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity of HP-X or HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensModeXANDYsets==item)
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.spotIntensTestMode= mae.SS_MODE_XANDY_SETS;
      setSImodeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity of HP-X and HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensModeE==item)
    {
      mae.spotIntensTestMode= mae.SS_MODE_ELIST;
      setSImodeState(item);
      Util.saveCmdHistory("Filter by Spot Iintensity of HP-E samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMspotIntensCompareModeALL==item)
    {
      setIntensityCompareModeSlider(mae.COMPARE_ALL);
      mae.spotIntensCompareMode= mae.COMPARE_ALL;
      setSIcompareModeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity for ALL channels meeting range");
      mae.repaint();
    }
    else if(mbf.miFRMspotIntensCompareModeANY==item)
    {
      setIntensityCompareModeSlider(mae.COMPARE_ANY);
      mae.spotIntensCompareMode= mae.COMPARE_ANY;
      setSIcompareModeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity for ANY channels meeting range");
      mae.repaint();
    }
    else if(mbf.miFRMspotIntensCompareModeAT_MOST==item)
    {
      setIntensityCompareModeSlider(mae.COMPARE_AT_MOST);
      mae.spotIntensCompareMode= mae.COMPARE_AT_MOST;
      setSIcompareModeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity for AT MOST channels meeting range");
      mae.repaint();
    }
    else if(mbf.miFRMspotIntensCompareModeAT_LEAST==item)
    {
      setIntensityCompareModeSlider(mae.COMPARE_AT_LEAST);
      mae.spotIntensCompareMode= mae.COMPARE_AT_LEAST;
      setSIcompareModeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity for AT LEAST channels meeting range");
      mae.repaint();
    }
    else if(mbf.miFRMspotIntensCompareModePRODUCT==item)
    {
      setIntensityCompareModeSlider(mae.COMPARE_PRODUCT);
      mae.spotIntensCompareMode= mae.COMPARE_PRODUCT;
      setSIcompareModeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity for PRODUCT of channels meeting range");
      mae.repaint();
    }
    else if(mbf.miFRMspotIntensCompareModeSUM==item)
    {
      setIntensityCompareModeSlider(mae.COMPARE_SUM);
      mae.spotIntensCompareMode= mae.COMPARE_SUM;
      setSIcompareModeState(item);
      Util.saveCmdHistory("Filter by Spot Intensity for SUM of channels meeting range");
      mae.repaint();
    }
    
    else if(mbf.miFRMthrGray==item)
    {
      mae.intensFilterFlag= item.getState();      
      String changeStr= (mae.useRatioDataFlag)
                          ? "("+sf1+"/"+sf2+")" : "Intensity";    
      Util.saveCmdHistory("Threshold Filter sliders set to "+changeStr+" [I1:I2]");
      
      stateScr.updateFilterScrollerUseCounter(changeStr+" I1", mae.intensFilterFlag);
      stateScr.updateFilterScrollerUseCounter(changeStr+" I2", mae.intensFilterFlag);
      
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMgrayRngIn==item)
    {
      mae.sampleIntensityRangeMode= mae.RANGE_INSIDE;
      setIntensityRangeState(item);
      Util.saveCmdHistory("Threshold Filter - include data inside intensity [I1:I2] range");
      mae.updatePseudoImgFlag= true;
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMgrayRngOut==item)
    {
      mae.sampleIntensityRangeMode= mae.RANGE_OUTSIDE;
      setIntensityRangeState(item);
      Util.saveCmdHistory("Threshold Filter - include data outside intensity [I1:I2] range");
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMthrRatio==item)
    {
      mae.ratioFilterFlag= item.getState();
      Util.saveCmdHistory("Ratio Filter sliders set to ratio [R1:R2]");
      if(!mae.isZscoreFlag)
      { /* ratio data */
        stateScr.updateFilterScrollerUseCounter("Ratio R1", mae.ratioFilterFlag);
        stateScr.updateFilterScrollerUseCounter("Ratio R2", mae.ratioFilterFlag);
      }
      else
      { /* Zdiff data */
        stateScr.updateFilterScrollerUseCounter("Zdiff Z1", mae.ratioFilterFlag);
        stateScr.updateFilterScrollerUseCounter("Zdiff Z2", mae.ratioFilterFlag);
      }      
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMratioRngIn==item)
    {
      mae.ratioRangeMode= mae.RANGE_INSIDE;
      setRatioRangeState(item);
      Util.saveCmdHistory("Ratio Filter - include data inside [R1:R2] range");
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMratioRngOut==item)
    {
      mae.ratioRangeMode= mae.RANGE_OUTSIDE;
      setRatioRangeState(item);
      Util.saveCmdHistory("Ratio Filter - include data outside [R1:R2] range");
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMthrCy3Cy5Ratio==item)
    {
      mae.ratioCy3Cy5FilterFlag= item.getState();
      String msg= (mae.ratioCy3Cy5FilterFlag) ? "on" : "off";
      if(! mae.isZscoreFlag)
      { /* Ratio data */
        Util.saveCmdHistory("Threshold Filter sliders set "+msg+
                            " for HP-X Cy3/Cy5 Ratio [CR1:CR2]");
        stateScr.updateFilterScrollerUseCounter("Ratio CR1", mae.ratioCy3Cy5FilterFlag);
        stateScr.updateFilterScrollerUseCounter("Ratio CR2", mae.ratioCy3Cy5FilterFlag);
      }
      else
      { /* Zdiff data */
        Util.saveCmdHistory("Threshold Filter sliders set "+msg+
                            " for HP-X Cy3/Cy5 Ratio [CZ1:CZ2]");
        stateScr.updateFilterScrollerUseCounter("Ratio CZ1", mae.ratioCy3Cy5FilterFlag);
        stateScr.updateFilterScrollerUseCounter("Ratio CZ2", mae.ratioCy3Cy5FilterFlag);
      }
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMCy3Cy5RatioRngIn==item)
    {
      mae.ratioCy3Cy5RangeMode= mae.RANGE_INSIDE;
      setCy3Cy5RatioRangeState(item);
      Util.saveCmdHistory("Cy3/Cyr Ratio Filter - include data inside [CR1:CR2] range");
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMCy3Cy5RatioRngOut==item)
    {
      mae.ratioCy3Cy5RangeMode= mae.RANGE_OUTSIDE;
      setCy3Cy5RatioRangeState(item);
      Util.saveCmdHistory("Cy3/Cyr Ratio Filter - include data outside [CR1:CR2] range");
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMcvModeMS==item)
    {
      mae.cvTestMode= mae.SS_MODE_MS;
      setCVmodeState(item);
      Util.saveCmdHistory("Filter by CV of current HP sample");
      mae.repaint();
    }
    
    else if(mbf.miFRMcvModeXY==item)
    {
      mae.cvTestMode= mae.SS_MODE_XY;
      setCVmodeState(item);
      Util.saveCmdHistory("Filter by CV of HP-X & HP-Y samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMcvModeXset==item)
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.cvTestMode= mae.SS_MODE_XSET;
      setCVmodeState(item);
      Util.saveCmdHistory("Filter by CV of HP-X 'set' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMcvModeYset==item)
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.cvTestMode= mae.SS_MODE_YSET;
      setCVmodeState(item);
      Util.saveCmdHistory("Filter by CV of HP-Y 'set' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMcvModeXORYsets==item)
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.cvTestMode= mae.SS_MODE_XORY_SETS;
      setCVmodeState(item);
      Util.saveCmdHistory("Filter by CV of HP-X or HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMcvModeXANDYsets==item)
    {
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Set 'Use HP-X and HP-Y sets' in Sample menu first!");
        return(true);
      }
      mae.cvTestMode= mae.SS_MODE_XANDY_SETS;
      setCVmodeState(item);
      Util.saveCmdHistory("Filter by CV of HP-X and HP-Y 'sets' samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMcvModeE==item)
    {
      mae.cvTestMode= mae.SS_MODE_ELIST;
      setCVmodeState(item);
      Util.saveCmdHistory("Filter by CV of HP-E samples");
      mae.repaint();
    }
    
    else if(mbf.miFRMuseCVmeanElseMax==item)
    { /* use mean of CV else use max */
      mae.useCVmeanElseMaxFlag= item.getState();
      Util.saveCmdHistory("Filter by CV - compute CV by "+
      ((mae.useCVmeanElseMaxFlag)  ? "mean" : "max") + " of HP CVs");
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMclusterHP_Efilter==item)
    {
      mae.clusterHP_EfilterFlag= item.getState();
      mae.useClusterDistFlag= mae.clusterHP_EfilterFlag;
      String status= (mae.clusterHP_EfilterFlag) ? "enabled." : "disabled.";
      Util.saveCmdHistory("Filtering by HP-E clustering is " + status);
      stateScr.updateFilterScrollerUseCounter("Cluster Distance",
                                              mae.clusterHP_EfilterFlag);
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    else if(mbf.miFRMuseDiffFilter==item)
    { /* Filter genes by Diff(HP-X,HP-Y)*/
      mae.useDiffFilterFlag= item.getState();
      String status= (mae.useDiffFilterFlag) ? "enabled." : "disabled.";
      Util.saveCmdHistory("Filtering by Diff(HP-X,HP-Y)" + status);
      stateScr.updateFilterScrollerUseCounter("Diff HP-XY",mae.useDiffFilterFlag);
      if(stateScr.isVisible || mae.autoStateScrPopupFlag)
        stateScr.regenerateScrollers(false);
      fc.computeWorkingGeneList();
      mae.repaint();
    }
    
    /* [2.5] ******* PLOT pull down menu ************* */
    else if(mbf.miPMplotPseudoImg==item)
    {
      Util.saveCmdHistory("Display array as pseudo-grayscale image ["+
                          mae.ms.hpName + "] sample");
      setPlotState(item);
      oldItem= item;
      mae.plotImageMode= mae.PLOT_PSEUDOIMG;
      mae.repaint();
    }
    
    else if(mbf.miPMplotPseudoHP_XYImg==item ||
    mbf.miPMplotPseudoHP_XY_RYGImg==item ||
    mbf.miPMplotPseudoHP_F1F2_RYGImg==item)
    { /* redraw pseudo image */
      if((mbf.miPMplotPseudoHP_XYImg==item ||
          mbf.miPMplotPseudoHP_XY_RYGImg==item) &&
         (mae.msX==null || mae.msY==null /* || mae.msX==mae.msY */))
      {
        Util.showMsg("Can't plot unless HP-X and HP-Y are defined.");        
        Util.popupAlertMsg("Can't plot unless HP-X and HP-Y are defined",
                           "Can't plot unless HP-X and HP-Y are defined.\n"+
                           "Define HP-X and HP-Y first.",
                           10, 80);
      }
      else if(mbf.miPMplotPseudoHP_F1F2_RYGImg==item && mae.msX==null)
      {
        Util.showMsg("Can't plot unless HP-X is defined.");       
        Util.popupAlertMsg("Can't plot unless HP-Xis defined",
                           "Can't plot unless HP-X is defined. Define HP-X.",
                           40, 60);
      }
      else
      {
        String
          sMsg= "Plotting array as pseudo-color image",
          sMsg1= "HP-X image ["+ mae.msX.hpName + "]",
          sMsg2= "HP-Y image ["+ mae.msY.hpName + "]";
        if(mbf.miPMplotPseudoHP_XYImg==item)
        {
          mae.plotImageMode= mae.PLOT_PSEUDO_HP_XY_IMG;
          sMsg += "[ratio of HP-X/HP-Y]";
        }
        else if(mbf.miPMplotPseudoHP_XY_RYGImg==item)
        {
          mae.plotImageMode= mae.PLOT_PSEUDO_HP_XY_RYG_IMG;
          sMsg += "[sum of HP-X and HP-Y]";
        }
        else if(mbf.miPMplotPseudoHP_F1F2_RYGImg==item)
        {
          mae.plotImageMode= mae.PLOT_PSEUDO_F1F2_RYG_IMG;
          sMsg += "[sum of "+sf1+","+sf2+"]";
          sMsg1= sf1+" image";
          sMsg2= sf2+" image";
        }
        setPlotState(item);
        oldItem= item;
        
        Util.saveCmdHistory(sMsg);
        Util.showMsg2(sMsg1);
        Util.showMsg3(sMsg2);
        mae.repaint();
      }
    }
    
    else if(mbf.miPMplotPseudoF1F2Img==item)
    {
      Util.saveCmdHistory("Plotting pseudocolor array "+sf1+", "+sf2);
      Util.showMsg2("image ["+ mae.ms.hpName + "]");
      Util.showMsg3("");
      setPlotState(item);
      oldItem= item;
      mae.plotImageMode= mae.PLOT_PSEUDO_F1F2_IMG;
      mae.repaint();
    }
    
    else if(mbf.miPMplotPseudoHP_XY_pValueImg==item)
    { /* use Pseudocolor (HP-X,HP-Y) 'sets' p-value */
      if(!mae.useHPxySetDataFlag)
      {
        Util.showMsg("Can't plot p-Values unless using HP-X and HP-Y 'sets'.");
        Util.showMsg2("Enable HP-X and HP-Y 'sets' and try again.");
        Util.showMsg3("");       
        Util.popupAlertMsg("Can't plot p-Values unless using HP-X and HP-Y 'sets'",
                           "plot p-Values unless using HP-X and HP-Y 'sets'.\n"+
                           "Enable HP-X and HP-Y 'sets' and try again.",
                           4, 80);
        mbf.miPMplotPseudoHP_XY_pValueImg.setState(false);
      }
      else if(mae.hps.nHP_X<2 || mae.hps.nHP_Y<2)
      {
        Util.showMsg("Can't plot p-Values unless HP-X and HP-Y 'sets' have at least 2 samples each.");
        Util.showMsg2("Choose new HP-X and HP-Y 'sets' that meet this criteria and try again,");
        Util.showMsg3("otherwise, you can't use this display.");       
        Util.popupAlertMsg("Can't plot p-Values unless HP-X and HP-Y 'sets' have at least 2 samples each",
                           "Can't plot p-Values unless HP-X and HP-Y 'sets' have at \n"+
                           "least 2 samples each. Choose new HP-X and HP-Y 'sets' that\n"+
                           "meet this criteria and try again. Enable HP-X and HP-Y \n"+
                           "'sets' and try again.",
                           10, 60);
        mbf.miPMplotPseudoHP_XY_pValueImg.setState(false);
      }
      else
      {
        Util.saveCmdHistory("Plotting pseudocolor array p-Values for HP-X and HP-Y sets");
        Util.showMsg2("");
        Util.showMsg3("");
        setPlotState(item);
        oldItem= item;
        mae.plotImageMode= mae.PLOT_PSEUDO_HP_XY_P_VALUE_IMG;
        mae.repaint();
      }
    } /* use Pseudocolor (HP-X,HP-Y) 'sets' p-value */
     
    else if(mbf.miPMplotPseudoHP_EP_CV_valueImg==item)
    { /* "Show Microarray) use Pseudocolor HP-EP 'list' CV-value" */
     if(mae.hps.nHP_E<2)
      {
        Util.showMsg("Can't plot CV-Values unless HP-EP 'list' has at least 2 samples.");
        Util.showMsg2("Choose new HP-EP 'list' that meets this criteria and try again,");
        Util.showMsg3("otherwise, you can't use this display.");       
        Util.popupAlertMsg("Can't plot CV-Values unless HP-EP 'list' has at least 2 samples",
                           "Can't plot CV-Values unless HP-EP 'list' has at \n"+
                           "least 2 samples. Choose new HP-EP 'list' that\n"+
                           "meets this criteria and try again.",
                           10, 60);
        mbf.miPMplotPseudoHP_EP_CV_valueImg.setState(false);
      }
      else
      {
        Util.saveCmdHistory("Plotting pseudocolor array CV-Values for HP-EP list");
        Util.showMsg2("");
        Util.showMsg3("");
        setPlotState(item);
        oldItem= item;
        mae.plotImageMode= mae.PLOT_PSEUDO_HP_EP_CV_VALUE_IMG;
        mae.cdb.hpXYdata.setCalcEPdataFlag(true);
        
        mae.repaint();
      }
    } /* "Show Microarray) use Pseudocolor HP-EP 'list' CV-value" */
    
    else if(mbf.miPMflickerXY==item)
    { /* toggle flicker and previous state */
      boolean stateVal= item.getState();
      
      if(stateVal)
      { /* flicker */
        oldPlotMode= mae.plotImageMode;
        oldMS= mae.ms;
        mae.plotImageMode= mae.PLOT_PSEUDOIMG;
        setPlotState(item);
        mae.runLoopDelayMsec= 600;
      }
      else
      { /* no flicker */
        mae.plotImageMode= oldPlotMode;
        mae.ms= oldMS;
        setPlotState(oldItem);
      }
      mae.flickerXYflag= stateVal;
      mae.repaint();
    }
    
    else if(mbf.miPEPlistType==item)
    { /* EP list type (overlay or grid) */
      mae.useEPoverlayFlag= item.getState();
      Util.saveCmdHistory("Plot EP list as " +
      ((mae.useEPoverlayFlag)  ? "overlay plot" : "scrollable grid"));
    }
    
    else if(mbf.miPMdualXYpseudoImg==item)
    { /* Dual HP-X & HP-Y pseudoimage */
      mae.dualXYpseudoPlotFlag= item.getState();
      Util.saveCmdHistory("Dual HP-X & HP-Y pseudoarray image is " +
      ((mae.dualXYpseudoPlotFlag) ? "enabled." : "disabled."));
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }    
    
    else if(mbf.miPMlowRangeZoom==item)
    { /* Scale pseudoarray image by 1/100 to zoom low-range values */
      mae.lowRangeZoomFlag= item.getState();
      Util.saveCmdHistory("Scale pseudoarray image " +
      ((mae.lowRangeZoomFlag) ? "by 1/100 to zoom low-range" : "is not scaled") );
      cfg.lowRangeScaleFactor= (mae.lowRangeZoomFlag) ? 0.01F : 1.0F;
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }
    
    else if(mbf.miCLMfindSimGenesDisp==item)
    { /* single gene clustering */
      mae.useSimGeneClusterDispFlag= setClusterDisplayState(item,false);
      stateScr.updateFilterScrollerUseCounter("Cluster Distance", 
                                              mae.useSimGeneClusterDispFlag);
      stateScr.regenerateScrollers(false);
      if(!mae.useSimGeneClusterDispFlag)
      { /* just shut THIS one off */
        mae.clg.removePreviousClusterMethod(0);       
        return(true);  
      }
      
      Util.saveCmdHistory("Cluster genes similar to current gene");
      Util.showMsg(
         "Click on a gene in array to find genes with similar HP-E expr. profiles.");      
          
      /* Setup popup text window */      
      mae.clg.createClusterMethod(fc.workingCL,
                                  cfg.nbrOfClustersThr,
                                  mae.hps.msListE, mae.hps.nHP_E,
                                  mae.clg.METHOD_SIMILAR_GENES_CLUSTERING,
                                  false /* resetFlag */);
      mae.repaint();
    } /* single gene clustering */
    
    else if(mbf.miCLMsimGeneCountsDisp==item)
    { /* cluster all genes */
      mae.useClusterCountsDispFlag= setClusterDisplayState(item,false);
      stateScr.updateFilterScrollerUseCounter("Cluster Distance", 
                                              mae.useClusterCountsDispFlag);
      stateScr.regenerateScrollers(false);
      if(!mae.useClusterCountsDispFlag)
      { /* just shut THIS one off */
        mae.clg.removePreviousClusterMethod(0);       
        return(true);  
      }
      
      Util.saveCmdHistory("Plotting clusters counts for all Filtered genes");
      /* Make sure have enough space
       * i.e. length is <=ClusterGenes.maxGenesInCurCluster
       */
      ClusterGenes.maxGenesInCurCluster= mae.mp.maxGenes;
      if((fc.workingCL.length > ClusterGenes.maxGenesInCurCluster) ||
         (fc.workingCL.length<=1))
      {
        String
          msg2= null,
          msg3= null;
        if(fc.workingCL.length<=1)
        {
          msg2= "Can't cluster - need at least 2 genes passing Filter";
          msg3= "Change the Filter settings and try again.";     
          Util.popupAlertMsg("Can't cluster - need at least 2 genes passing Filter'",
                             "cluster - need at least 2 genes passing Filter.\n"+
                             "Change the Filter settings and try again.",
                             4, 80);
        }
        ClusterGenes.showReducedFilteredGenesMsg(msg2,msg3,
                                                 ClusterGenes.maxGenesInCurCluster);
        mae.useClusterCountsDispFlag= false;
        mbf.miCLMsimGeneCountsDisp.setState(false);
        return(true);
      }
      
      /* Setup popup text window */      
      mae.clg.createClusterMethod(fc.workingCL,
                                  cfg.nbrOfClustersThr,
                                  mae.hps.msListE, mae.hps.nHP_E,
                                  mae.clg.METHOD_SIMILAR_GENE_COUNTS_CLUSTERING,
                                  false /* resetFlag */);
      //mae.repaint();      
    } /* cluster all genes */
    
    
    else if(mbf.miCLMhierClusterDisp==item)
    { /* hierarchical clustering */
      mae.useHierClusterDispFlag= setClusterDisplayState(item,false);
      stateScr.regenerateScrollers(false);
      if(!mae.useHierClusterDispFlag)
      { /* just shut THIS one off */
        mae.clg.removePreviousClusterMethod(0);       
        return(true);  
      }
      
      Util.saveCmdHistory(
        "Display 'Hierarchical clustering' of gene expression profiles for Filtered HP-E samples");
      
      mae.clg.createClusterMethod(fc.workingCL,
                                  cfg.nbrOfClustersThr,
                                  mae.hps.msListE, mae.hps.nHP_E,
                                  mae.clg.METHOD_HIERARCHICAL_CLUSTERING,
                                  false /* resetFlag */);
      mae.repaint();
    } /* hierarchical clustering */
    
    else if(mbf.miCLMdispKmeansNodes==item)
    { /* K-means clustering */
      mae.useKmeansClusterCntsDispFlag= setClusterDisplayState(item,false);
      stateScr.updateFilterScrollerUseCounter("# of Clusters", 
                                              mae.useKmeansClusterCntsDispFlag);
      stateScr.regenerateScrollers(false);
      if(!mae.useKmeansClusterCntsDispFlag)
      { /* just shut THIS one off */
        mae.clg.removePreviousClusterMethod(0);       
        return(true);  
      }
      
      Util.saveCmdHistory("Display 'K-"+
                          ((mae.useMedianForKmeansClusteringFlag)
                             ? "medians" : "means")+
                          "' gene expression profiles for Filtered HP-E");      
      mae.clg.createClusterMethod(fc.workingCL,
                                  cfg.nbrOfClustersThr,
                                  mae.hps.msListE, mae.hps.nHP_E,
                                  mae.clg.METHOD_CLUSTER_KMEANS,
                                  false /* resetFlag */);
      mae.repaint();
    } /* K-means clustering */
    
    else if(mbf.miCLHCMavgPGMALnk==item)
    {
      mae.hierClustMode= mae.HIER_CLUST_PGMA_LNKG;
      item.setState(true);
      mbf.miCLHCMavgPGMCLnk.setState(false);
      mbf.miCLHCMnextMinLnk.setState(false);
      Util.saveCmdHistory("Hierarchical cluster - use 'average-arithmetic-linkage'");
    }
    
    else if(mbf.miCLHCMavgPGMCLnk==item)
    {
      mae.hierClustMode= mae.HIER_CLUST_PGMC_LNKG;
      item.setState(true);
      mbf.miCLHCMavgPGMALnk.setState(false);
      mbf.miCLHCMnextMinLnk.setState(false);
      Util.saveCmdHistory("Hierarchical cluster - use 'average-centroid-linkage'");
    }
    
    else if(mbf.miCLHCMnextMinLnk==item)
    {
      mae.hierClustMode= mae.HIER_CLUST_NEXT_MIN_LNKG;
      item.setState(true);
      mbf.miCLHCMavgPGMALnk.setState(false);
      mbf.miCLHCMavgPGMCLnk.setState(false);
      Util.saveCmdHistory("Hierarchical cluster - use 'next-min-linkage'");
    }
    
    else if(mbf.miCLHCMunWtAvg==item)
    { /* toggle state */
      mae.hierClustUnWtAvgFlag= item.getState();
      String  sMsg= (mae.hierClustUnWtAvgFlag) ? "un" : "";
      Util.saveCmdHistory("Hierarchical cluster - use "+sMsg + "weighted averaging");
    }
    
    else if(mbf.miCLMnormHP==item)
    { /* toggle state */
      mae.normHCbyRatioHPflag= item.getState();
      String sMsg= (mae.normHCbyRatioHPflag)
                     ? "HP-X sample data/gene" 
                     : "HP's max intensities data";
      Util.saveCmdHistory("Hierarchical cluster - normalize data by "+sMsg);
    }
    
    else if(mbf.miCLMuseMedianKmeans==item)
    { /* toggle state */
      mae.useMedianForKmeansClusteringFlag= item.getState();
      String sMethod= (mae.useMedianForKmeansClusteringFlag) ? "median" : "mean";
      Util.saveCmdHistory("Using "+sMethod+
                          " for estimating cluster center in K-means clustering");
    }
    
    else if(mbf.miCLMuseCorrCoeffDist==item)
    { /* toggle corr-coeff else Euclid-distance of EP differences */
      mae.useCorrCoeffFlag= item.getState();
      String sMsg= (mae.useCorrCoeffFlag) ? "corr-coeffient" : "Euclidean-distance";
      Util.saveCmdHistory("For clustering - use "+sMsg+" gene difference metric");
    }
    
    else if(mbf.miCLMuseLSQmagNorm==item)
    { /* toggle expr. profile vector prior to clustering */
      mae.useLSQmagNormFlag= item.getState();
      String sMsg= (mae.useLSQmagNormFlag) ? "Don't normalize" : "Normalize";
      Util.saveCmdHistory(sMsg+" expression profile vector prior to clustering");
    }
    
    else if(mbf.miCLHCMuseClusterDistCache==item)
    { /* toggle clustering gene-gene distance matrix cache */
      mae.useClusterDistCacheFlag= item.getState();
      String sMsg= (mae.useClusterDistCacheFlag) ? "Not using" : "Using";
      Util.saveCmdHistory(sMsg+" gene-gene distance matrix cache when do clustering");
    }
    
    else if(mbf.miCLHCMuseShortClusterDistCache==item)
    { /* toggle short/float clustering gene-gene dist. matrix cache */
      mae.useShortClusterDistCacheFlag= item.getState();
      String sMsg= (mae.useShortClusterDistCacheFlag)
                      ? "(short 16-bit)" : "(float 32-bit)";
      Util.saveCmdHistory("Using "+sMsg+
                          " gene-gene distance matrix cache when clustering");
      if(mae.useShortClusterDistCacheFlag)
        Util.showMsg2("WARNING: scaling to 16-bits may lead to clustering errors.");
    }
    
    /* [2.6] ****** VIEW pull down menu ************* */
    else if(mbf.miVMshowEditedGenes==item || mbf.miEMshowEditedGenes==item)
    {
      mae.showEGLflag= item.getState();
      mbf.miVMshowEditedGenes.setState(mae.showEGLflag);  /* since dups... */
      mbf.miEMshowEditedGenes.setState(mae.showEGLflag);
      Util.saveCmdHistory(((mae.showEGLflag) ? "Showing" : "Not showing")+
                          " Edited Gene List");
      /* Update filter and repost new data. */
      //mae.updatePseudoImgFlag= true;
      pur.updateFilter(mae.fc.workingCL);
      mae.is.siCanvas.repaint();
      mae.repaint();
    }
    
    else if(mbf.miVMgenBankViewer==item)
    {
      mae.genBankViewerFlag= setViewerDisplayState(item,"GenBank");
      mae.repaint();
    }
    
    else if(mbf.miVMdbESTviewer==item)
    {
      mae.dbESTviewerFlag= setViewerDisplayState(item,"dbEst");
      mae.repaint();
    }
    
    else if(mbf.miVMuniGeneViewer==item)
    {
      mae.uniGeneViewerFlag= setViewerDisplayState(item,"UniGene");
      mae.repaint();
    }
    
    else if(mbf.miVMomimViewer==item)
    {
      mae.omimViewerFlag= setViewerDisplayState(item,"OMIM");
      mae.repaint();
    }
    
    else if(mbf.miVMmAdbViewer==item)
    {
      mae.mAdbViewerFlag= setViewerDisplayState(item,"mAdb");
      mae.repaint();
    }
    
    else if(mbf.miVMlocusLinkViewer==item)
    {
      mae.locusLinkViewerFlag= setViewerDisplayState(item,"locusLink");
      mae.repaint();
    }
    
    else if(mbf.miVMmedMinerViewer==item)
    {
      mae.medMinerViewerFlag= setViewerDisplayState(item,"medMiner");
      mae.repaint();
    }
    
    else if(mbf.miVMswissProtViewer==item)
    {
      mae.swissProtViewerFlag= setViewerDisplayState(item,"swissProt");
      mae.repaint();
    }
    
    else if(mbf.miVMpirViewer==item)
    {
      mae.pirViewerFlag= setViewerDisplayState(item,"PIR");
      mae.repaint();
    }
    
    else if(cfg.nGenomicMenus>0)
    { /* see if it is in the list of Genomic databases */
      for(int i=0;i<cfg.nGenomicMenus;i++)
        if(mbf.miVMgenomicViewer[i]==item)
        { /* found it - toggle generic Genomic menu entry */
          mae.genomicViewerFlag[i]= setViewerDisplayState(item,
          cfg.sGenomicMenu[i]);
          mae.repaint();
          groupFlag= true;
          break;
        }/* toggle generic Genomic menu entry */
    } /* see if it is in the list of Genomic databases */
    
    if(mbf.miVMshowFilteredSpots==item)
    {
      mae.viewFilteredSpotsFlag= item.getState();
      Util.saveCmdHistory(((mae.viewFilteredSpotsFlag) ? "Show" : "Don't show")+
                          "Filtered spots in array");
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }
    
    else if(mbf.miVMGang==item)
    {
      mae.gangSpotFlag= item.getState();
      Util.saveCmdHistory("Gang spot access ("+sf1+" and "+sf2+") is turned " +
                          (String)((mae.gangSpotFlag) ? "ON" : "OFF."));
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }
    
    else if(mbf.miVMuseMouseOver==item)
    {
      mae.useMouseOverFlag= item.getState();
      Util.saveCmdHistory("Mouse-over reporting is turned " +
                         (String)((mae.useMouseOverFlag) ? "ON" : "OFF."));
    }
    
    else if(mbf.miVMPresentView==item)
    {
      mae.presentViewFlag= item.getState();
      Util.saveCmdHistory("Presentation view size is turned " +
                          (String)((mae.presentViewFlag) ? "ON" : "OFF."));
      if(mae.presentViewFlag && !mae.rptFontSize.equals("12pt"))
      {
        mae.rptFontSize= "12pt";   /* force larger font */
        setReportFontSizeState(mbf.miTMtblFontSize12pt);
        mae.pur.updateLabels();
      }
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }
    
    else if(mbf.miVMuseDichromasy==item)
    {
      mae.useDichromasyFlag= item.getState();
      Util.saveCmdHistory("Setting color scheme to " +
                         (String)((mae.useDichromasyFlag) 
                                    ? "dichromasy" : "Red-green"));
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }
    
    else if(mbf.miVMshowMsgLog==item)
    {
      Util.msgIsVisibleFlag= item.getState();
      Util.saveCmdHistory("Show messages log is "+
                          ((Util.msgIsVisibleFlag)  ? "ON" : "OFF"));
      Util.setLogMsgWindowVisible(Util.msgIsVisibleFlag);
    }
    
    else if(mbf.miVMshowHistoryLog==item)
    {
      Util.historyIsVisibleFlag= item.getState();
      Util.saveCmdHistory("Showing command history log is "+
                          ((Util.historyIsVisibleFlag)  ? "ON" : "OFF"));
      Util.setLogHistoryWindowVisible(Util.historyIsVisibleFlag);
    }
    
    /* [2.7] ******* REPORT pull down menu ************* */
    else if(mbf.miTMtblFontSize8pt==item || mbf.miPRtblFontSize8pt==item)
    {
      mae.rptFontSize= "8pt";
      Util.saveCmdHistory( "Setting table font size to 8 pt.");
      setReportFontSizeState(item);
      mae.pur.updateLabels();
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }
    else if(mbf.miTMtblFontSize10pt==item || mbf.miPRtblFontSize10pt==item)
    {
      mae.rptFontSize= "10pt";
      Util.saveCmdHistory( "Setting table font size to 10 pt.");
      setReportFontSizeState(item);
      mae.pur.updateLabels();
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }
    else if(mbf.miTMtblFontSize12pt==item || mbf.miPRtblFontSize12pt==item)
    {
      mae.rptFontSize= "12pt";
      Util.saveCmdHistory( "Setting table font size to 12 pt.");
      setReportFontSizeState(item);
      mae.pur.updateLabels();
      mae.updatePseudoImgFlag= true;
      mae.repaint();
    }
    
    else if(mbf.miTMtblFmtTabDelim==item)
    {
      mae.tblFmtMode= mae.RPT_FMT_TAB_DELIM;
      Util.saveCmdHistory(
         "Setting table format to tab-delimited - suitable for export to spreadsheet.");
      setReportFormatState(item);
    }
    
    else if(mbf.miTMtblFmtClickable==item)
    {
      mae.tblFmtMode= mae.RPT_FMT_DYN;
      Util.saveCmdHistory( "Setting table format to clickable spreadsheet.");
      setReportFormatState(item);
    }
    
    else if(mbf.miTMtblFmtAddEPdata==item)
    {
      mae.addExprProfileFlag= item.getState();
      String sMsg= ((mae.addExprProfileFlag) ? "Adding " : "Not-adding ") +
                   "Expr-Profile data of Filtered genes to Gene-Reports.";
      Util.saveCmdHistory(sMsg);
    }
    
    else if(mbf.miTMtblFmtUseRawEPdata==item)
    {
      mae.useEPrawIntensValFlag= item.getState();
      String sMsg= "Using "+
                   ((mae.useEPrawIntensValFlag) ? "raw" : "normalized") +
                   "-intensity Expr-Profile data for Gene-Reports.";
      Util.saveCmdHistory(sMsg);
    }
    
    else if(mbf.miTMtblFmtAddHP_XYsetStat==item)
    {
      mae.addHP_XYstatFlag= item.getState();
      String sMsg= ((mae.addHP_XYstatFlag) ? "Adding " : "Not-adding ") +
                   "HP-X/Y 'set' statistics of Filtered genes to Gene-Reports.";
      Util.saveCmdHistory(sMsg);
    } 
    
    else if(mbf.miTMtblFmtAddOCLstatistics==item)
    { /*  "Add Ordered Condition List statistics data to Gene-Reports [CB]" */
      mae.addOCLstatFlag= item.getState();
      String sMsg= ((mae.addOCLstatFlag) ? "Adding " : "Not-adding ") +
                   "Ordered Condition List statistics of Filtered genes to Gene-Reports.";
      Util.saveCmdHistory(sMsg);
    }
    
    
    /* [2.8] ****** PLUGINS pull down menu ************* */
    /* [NOTE] Plugins are mostly handled by MAEPlugins their own event handlers */
    
    else if(mbf.miPLGMuseSPLUSasComputingEngine==item)
    { /* "Use S-PLUS, else R, as computing engine [CB]" */
      mae.useSPLUSasComputingEngineFlag= item.getState();
      String sMsg= ((mae.useSPLUSasComputingEngineFlag)
                     ? "Using S-PLUS" : "Using R ") +
                   " as the statistics and computing engine.";
      mae.mja.mjaReval.setUseSPLUSelseRengineFlag(mae.useSPLUSasComputingEngineFlag);
      Util.saveCmdHistory(sMsg);
    }
    
    else if(mbf.miPLGMuseTimeStampReports==item)
    { /* "Save RLO reports in time-stampled folder [CB]" */      
      mae.useRLOloggingFlag= item.getState();
      String sMsg= ((mae.useRLOloggingFlag) ? "Using " : "Not-using ") +
                   "time-stamped RLO Report folders.";
      /* Set RLO Report logging flag for creating subdirectory in 
       * {project}/Report/{RLOname}-yymmdd.hhmmss/ to store the RLO output 
       * files copied to Report/
       */
      mae.mja.mjaReval.setRLOreportLoggingFlag(mae.useRLOloggingFlag);
      Util.saveCmdHistory(sMsg);
    }
    
    /* [2.9] ****** HELP pull down menu ************* */
    
    /* [CHECK] check logic after add Plugin tests */
    flag= groupFlag;
    
    mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    
    return(flag);
  } /* processCheckboxMenuItemStateChangedByitem */
  
  
  /**
   * cleanup() - cleanup global static allocated variables in this class.
   * If statics are added later to this class, then set them to
   * null here.
   */
  void cleanup()
  { /* cleanup */
    fc= null;
    newDir= null;
    promptFile= null;
    promptDir= null;
    promptExt= null;
    workingCL= null;
    wkCLbitSet= null;
    normCLbitSet= null;
    oldPlotMode= 0;
    oldMS= null;
    oldItem= null;
  } /* cleanup */
  
} /* end of class EventMenu */
