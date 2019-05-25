/** File: PopupRegistry.java */

import java.awt.*;
import java.util.*;

/**
 * The PopupRegistry class implements a call-back mechanism for active popup windows
 * It is also usable for any other objects that impelement MaeEventListeners 
 * that are dynamicaly created and need to be notified of state changes. Examples
 * of the latter are MAEPlugin instances.
 *<P>
 * It registers popup windows (plots, spreadsheets, string text areas, expr. profiles,
 * MAEPlugin Instance Objects etc.) So they can be checked for user event handling.
 * When an event gets registered, it supplies a list of events it wants to be notified
 * of which include: CurGene changed, data Filter changed, threshold Sliders changed,
 * sample labels or other lables changed, and timeout (not implemented yet).
 * Also, when a window registers it supplies a popup key (i.e. popup name) and 
 * this may be required to be UNIQUE or not. If unique, then only one instance of
 * that window may exist and the old windows must be removed (by the caller) prior to
 * to creating the new window.
 *
 *<PRE>
 * There are 6 types of popups that are able to be registered here.
 *  <B>Class                 variable prefix</B>   
 *  ShowPlotPopup           sppXXX[0:nSPP-1] active Plot popups   
 *  ShowSpreadsheetPopup    ssspXXX[0:nSSSP-1] active ShowSpreadsheetPopup popups 
 *  ShowStringPopup         sspXXX[0:nSSP-1] active String popups 
 *  ShowExprProfilesPopup   seppXXX[0:nSEPP-1] active ExprProfile's popups 
 *  MAEPluginUpdateListener maepXXX[0:nMAEP-1] active MAEPlugin Instance Object's Object popups [TODO]
 *</PRE>
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
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.14 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
class PopupRegistry
{
  /*
   * [CHECK] if we are updating things properly and at the right level for all 
   * plugins. We may need to recompute the data at a higher level (eg. if 
   * renormalize or reFilter or recompute a cluster, etc.) and send it down
   * the entire food chain again... Make a list of all failures
   * so we can check things better.
   */  
    
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;                        
  /** link to global MenuBarFrame instance */
  private MenuBarFrame
    mbf;                        
  /** link to global Maps instance */
  private Maps
    map;  
  /** link to global Config instance */
  private Config
    cfg; 
     
  /* --- PopupRegistry property bit types --- */
  /** Property bit type: Notify bit if CurGene changed */
  final static int
    CUR_GENE= 1;		
  /** Property bit type: Notify bit if Filter changed */
  final static int
    FILTER= 2;
  /** Property bit type: Notify bit if Slider changed */
  final static int
    SLIDER= 4;
  /** Property bit type: Notify bit if Labels changed */
  final static int
    LABEL= 8;
  /** Property bit type: set if this is a unique popup */
  final static int
    UNIQUE= 16;
  /** Property bit type: set if timeout and kill itself after 
   * TIMEOUT_DELAY milliseconds 
   */ 
  final static int
    TIMEOUT= 32;
    
  /** # msec. to wait before kill popup */  
  final static int
    TIMEOUT_DELAY= 5000;        
  /** maximum # of popups for each type of popup */
  final static int
    MAX_POPUPS= 15;                 
 
  /* --- Lists of various types of popup windows and associated data structures --- */
  /** [0:nSPP-1] list of active Plot popups */   
  private ShowPlotPopup
    sppList[];                  
  /** [0:nSSSP-1] list of active spreadsheet popup popups */
  private ShowSpreadsheetPopup
    ssspList[];           
  /** [0:nSSP-1] list of active String popups */
  private ShowStringPopup
    sspList[];                  
  /** [0:nSEPP-1] list of active Expr Profile popups */
  private ShowExprProfilesPopup
    seppList[];   
  /** [0:nMAEP-1] list of active MAEPlugin Objects */
  private MAEUpdateListener
    maepList[];        

  /** [0:nSPP-1] ShowPlotPopup key names */
  private String
    sppKey[]; 
  /** [0:nSSSP-1] ShowSpreadsheetPopup key names */
  private String                 
    ssspKey[];            
  /** [0:nSSP-1] ShowStringPopup key names*/
  private String
    sspKey[];
  /** [0:nSEPP-1] ExprProfilePopup key names*/
  private String
    seppKey[];      
  /** [0:nMAEP-1] MAEPlugin Object key names */
  private String
    maepKey[];  

 /** [0:nSPP-1] bits requesting notification of ShowPlotPopup of change */
  private int
    sppPropBits[];
  /** [0:nSSSP-1] bits requesting notification of ShowSpreadsheetPopup of change */
  private int	       
    ssspPropBits[];       
  /** [0:nSSP-1] bits requesting notification of ShowStringPopup of changes */
  private int
    sspPropBits[];	
  /** [0:nSEPP-1] bits requesting notification of ExprProfilePopup list of changes*/
  private int       
    seppPropBits[];
  /** [0:nMAEP-1] bits requesting notification of MAEPlugin Object list of changes */
  private int       
    maepPropBits[];         
  
  /** [0:nSPP-1] String Popup timeout seconds */
  private int
    sspTime[];	  
                      
  /** Plot popups counter */
  private int
    nSPP;                     
  /** ShowSpreadsheetPopup popups counter */
  private int
    nSSSP;		       
  /** String popups counter */
  private int
    nSSP;		       
  /** ShowExprProfilesPopup counter */
  private int
    nSEPP;
  /** MAEPlugin Instance Object counter */
  private int
    nMAEP;
    	       
  /** ShowPlotPopup popups currrent index */
  private int
    idxSPP;                    
  /** ShowSpreadsheetPopup popups current index */
  private int
    idxSSSP;		       
  /** ShowStringPopup popups current index */
  private int
    idxSSP;		       
  /** ShowExprProfilesPopup current index */
  private int
    idxSEPP;   	       
  /** MAEPlugin Object current index */
  private int
    idxMAEP;		           	       
   
  /** previous MID object used to see if switched current gene object */
  int 
    prevObjMID;                  
  /** last value of fc.workingCL.length to see if update clusters -
   * if filter changed.
   */
  private int
    oldWorkingCLlength;     
    

  /**
   * PopupRegistry() - constructor to popup registry
   * @param mae is instance of MAExplorer
   */
  PopupRegistry(MAExplorer mae)
  { /* PopupRegistry */
    this.mae= mae;
    mbf= mae.mbf;
    map= mae.mp;
    cfg= mae.cfg;
    
    nSPP= 0;
    nSSSP= 0;
    nSSP= 0;
    nSEPP= 0;
    nMAEP= 0;
    
    prevObjMID= -1;
    oldWorkingCLlength= 0;
    
    sppList= new ShowPlotPopup[MAX_POPUPS];
    ssspList= new ShowSpreadsheetPopup[MAX_POPUPS];
    sspList= new ShowStringPopup[MAX_POPUPS];
    seppList= new ShowExprProfilesPopup[MAX_POPUPS];
    maepList= new MAEUpdateListener[MAX_POPUPS];
    
    sppKey= new String[MAX_POPUPS];
    ssspKey= new String[MAX_POPUPS];
    sspKey= new String[MAX_POPUPS];
    seppKey= new String[MAX_POPUPS];
    maepKey= new String[MAX_POPUPS];
    
    sppPropBits= new int[MAX_POPUPS];
    ssspPropBits= new int[MAX_POPUPS];
    sspPropBits= new int[MAX_POPUPS];
    seppPropBits= new int[MAX_POPUPS];
    maepPropBits= new int[MAX_POPUPS];
    
    sspTime= new int[MAX_POPUPS];
  } /* PopupRegistry */
  
  
  /**
   * addPopupWindowToReg() - register ShowPlotPopup window in sppList[]
   * @param ssp instance of window to register
   * @param key is popup name for this window
   * @param propBits specify which updates are to be invoked for this window
   * @return the new count for this window type.
   */
  int addPopupWindowToReg(ShowPlotPopup spp, String key, int propBits)
  { /* addPopupWindowToReg */
    if(nSPP<MAX_POPUPS)
    {
      sppList[nSPP]= spp;
      if(key==null)
        key= "*UNDEFINED*";
      sppKey[nSPP]= key;
      sppPropBits[nSPP++]= propBits;
    }
    else return(-1);
    
    return(nSPP);
  } /* addPopupWindowToReg */
  
  
  /**
   * addPopupWindowToReg() - register ShowSpreadsheetPopup window in ssspList[]
   * @param sssp instance of window to register
   * @param key is popup name for this window
   * @param propBits specify which updates are to be invoked for this window
   * @return the new count for this window type.
   */
  int addPopupWindowToReg(ShowSpreadsheetPopup sssp, String key, int propBits)
  { /* addPopupWindowToReg */
    if(nSSSP<MAX_POPUPS)
    {
      ssspList[nSSSP]= sssp;
      if(key==null)
        key= "*UNDEFINED*";
      ssspKey[nSSSP]= key;
      ssspPropBits[nSSSP++]= propBits;
    }
    else return(-1);
    
    return(nSSSP);
  } /* addPopupWindowToReg */
  
  
  /**
   * addPopupWindowToReg() - register ShowStringPopup window in sspList[]
   * @param ssp instance of window to register
   * @param key is popup name for this window
   * @param propBits specify which updates are to be invoked for this window
   * @return the new count for this window type.
   */
  int addPopupWindowToReg(ShowStringPopup ssp, String key, int propBits)
  { /* addPopupWindowToReg */
    if(nSSP<MAX_POPUPS)
    {
      sspTime[nSSP]= 0;       /* init the timeout in case used */
      sspList[nSSP]= ssp;
      if(key==null)
        key= "*UNDEFINED*";
      sspKey[nSSP]= key;
      sspPropBits[nSSP++]= propBits;
    }
    else return(-1);
    
    return(nSSP);
  } /* addPopupWindowToReg */
  
  
  /**
   * addPopupWindowToReg() - register ShowExprProfilesPopup window in seppList[]
   * @param sepp instance of window to register
   * @param key is popup name for this window
   * @param propBits specify which updates are to be invoked for this window
   * @return the new count for this window type.
   */
  int addPopupWindowToReg(ShowExprProfilesPopup sepp, String key, int propBits)
  { /* addPopupWindowToReg */
    if(nSEPP<MAX_POPUPS)
    {
      seppList[nSEPP]= sepp;
      if(key==null)
        key= "*UNDEFINED*";
      seppKey[nSEPP]= key;
      seppPropBits[nSEPP++]= propBits;
    }
    else return(-1);
    
    return(nSEPP);
  } /* addPopupWindowToReg */
  
  
  /**
   * addPopupWindowToReg() - register MAEPlugin Instance Object window in maepList[]
   * @param maep instance of window to register
   * @param key is popup name for this window
   * @param propBits specify which updates are to be invoked for this window
   * @return the new count for this window type.
   */
  int addPopupWindowToReg(MAEUpdateListener maep, String key, int propBits)
  { /* addPopupWindowToReg */
    if(nMAEP<MAX_POPUPS)
    {
      maepList[nMAEP]= maep;
      if(key==null)
        key= "*UNDEFINED*";
      maepKey[nMAEP]= key;
      maepPropBits[nMAEP++]= propBits;
    }
    else return(-1);
    
    return(nMAEP);
  } /* addPopupWindowToReg */
  
  
  /**
   * rmvPopupFromReg() - remove popup instance from ShowPlotPopup sppList[].
   * @param spp is instance of previously registered window to be removed
   * @return -1 if not found or no objects in the list
   * else the new count.
   */
  synchronized int rmvPopupFromReg(ShowPlotPopup spp)
  { /* rmvPopupFromReg */
    if(nSPP==0)
      return(-1);
    
    for(int i=0;i<nSPP;i++)
      if(spp==sppList[i])
      { /* pop the list */
        for(int j=i; j<(nSPP-1);j++)
        { /* remove it from the list */
          sppList[j]= sppList[j+1];
          sppKey[j]= sppKey[j+1];
          sppPropBits[j]= sppPropBits[j+1];
        }
        nSPP--;
        return(nSPP);
      }
    return(-1);          /* bad object */
  } /* rmvPopupFromReg */
  
  
  /**
   * rmvPopupFromReg() - remove popup from ShowSpreadsheetPopup ssspList[].
   * @param sssp is instance of previously registered window to be removed
   * @return -1 if not found or no objects in the list
   *            else the new count.
   */
  synchronized int rmvPopupFromReg(ShowSpreadsheetPopup sssp)
  { /* rmvPopupFromReg */
    if(nSSSP==0)
      return(-1);
    
    for(int i=0;i<nSSSP;i++)
      if(sssp==ssspList[i])
      { /* pop the list */
        for(int j=i; j<(nSSSP-1);j++)
        { /* remove it from the list */
          ssspList[j]= ssspList[j+1];
          ssspKey[j]= ssspKey[j+1];
          ssspPropBits[j]= ssspPropBits[j+1];
        }
        nSSSP--;
        return(nSSSP);
      }
    return(-1);          /* bad object */
  } /* rmvPopupFromReg */
  
  
  /**
   * rmvPopupFromReg() - remove popup from ShowStringPopup ssptList[].
   * Note that only the String popup has a potential timeout.
   * @param spp is instance of previously registered window to be removed
   * @return -1 if not found or no objects in the list
   * else the new count.
   */
  synchronized int rmvPopupFromReg(ShowStringPopup ssp)
  { /* rmvPopupFromReg */
    if(nSSP==0)
      return(-1);
    
    for(int i=0;i<nSSP;i++)
      if(ssp==sspList[i])
      { /* pop the list */
        for(int j=i; j<(nSSP-1);j++)
        {
          sspTime[j]= sspTime[j+1];  /* remove it from the list */
          sspList[j]= sspList[j+1];
          sspKey[j]= sspKey[j+1];
          sspPropBits[j]= sspPropBits[j+1];
        }
        nSSP--;
        return(nSSP);
      }
    return(-1);          /* bad object */
  } /* rmvPopupFromReg */
  
  
  /**
   * rmvPopupFromReg() - remove popup from ShowExprProfilesPopup seppList[].
   * @param seep is instance of previously registered window to be removed
   * @return -1 if not found or no objects in the list
   * else the new count.
   */
  synchronized int rmvPopupFromReg(ShowExprProfilesPopup seep)
  { /* rmvPopupFromReg */
    if(nSEPP==0)
      return(-1);
    
    for(int i=0;i<nSEPP;i++)
      if(seep==seppList[i])
      { /* pop the list */
        for(int j=i; j<(nSEPP-1);j++)
        { /* remove it from the list */
          seppList[j]= seppList[j+1];
          seppKey[j]= seppKey[j+1];
          seppPropBits[j]= seppPropBits[j+1];
        }
        nSEPP--;
        return(nSEPP);
      }
    return(-1);          /* bad object */
  } /* rmvPopupFromReg */
  
  
  /**
   * rmvPopupFromReg() - remove popup from maepList[].
   * @param maep is instance of previously registered window to be removed
   * @return -1 if not found or no objects in the list
   * else the new count.
   */
  synchronized int rmvPopupFromReg(Object maep, int BOGUS)
  { /* rmvPopupFromReg */
    if(nMAEP==0)
      return(-1);
    
    for(int i=0;i<nMAEP;i++)
      if(maep==maepList[i])
      { /* pop the list */
        for(int j=i; j<(nMAEP-1);j++)
        { /* remove it from the list */
          maepList[j]= maepList[j+1];
          maepKey[j]= maepKey[j+1];
          maepPropBits[j]= maepPropBits[j+1];
        }
        nMAEP--;
        return(nMAEP);
      }
    return(-1);                            /* bad object */
  } /* rmvPopupFromReg */
  
  
  /**
   * chkOtherCurGeneEffects() - check other gene effects when change current gene in ms.objXXX.
   * Note: Put things here to do when change the gene which are side effects.
   * If the current gene is defined (i.e. mae.ms.isValidObjFlag),
   * then check and update the following if they are enabled:
   *<PRE>
   *    1. modifying the 'editable gene list',
   *    2. poping up a web browser on external GenBank etc. data,
   *    3. changing the gene used in the 'single' expression profile plot,
   *    4. update cluster algorithms and redraw overlays in pseudoarray image.
   *</PRE>
   * If mouseKeyMods==ClusterGenes.UPDATE, compute it again
   * even if midA==cdb.objMID.
   * @param midA specifying the gene MID
   * @param mouseKeyMods modifieers (i.e.CONTROL or SHIFT)
   * @return true if successful
   * @see ClusterGenes#copyCurClusterToEditedGeneList
   * @see ClusterGenes#findClustersOfGene
   * @see ClusterGenes#updateGeneClustersOfCurrentGeneReport
   * @see Gene#lookupIDbyName
   * @see EditedGeneList#processEditCommand
   * @see EventMenu#setClusterDisplayState
   * @see ExprProfilePopup#updateData
   * @see ScrollableImageCanvas#repaint
   * @see ShowStringPopup#changeGeneButton
   * @see StateScrollers#regenerateScrollers
   * @see Util#popupViewer
   * @see Util#rmvIMAGEstr
   * @see Util#saveCmdHistory
   */
  boolean chkOtherCurGeneEffects(int midA, int mouseKeyMods)
  { /* chkOtherCurGeneEffects */
    /* [CHECK] check if it works correctly and if not then DEBUG.
     * It seems there is a flaky 2D scatter-plot, clustergram and
     * EP overlay plot. See [TODO] note in front of this file.
     */
    
    if(midA==-1 ||
    (midA!=mae.cdb.objMID && mouseKeyMods!=ClusterGenes.UPDATE))
      return(false);         /* only check if midA is current gene! */
    
    int
      gid= mae.cdb.objGID,
      mid= mae.cdb.objMID;
    Gene gene= map.midStaticCL.mList[mid];
    if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
      return(false);            /* ignore bogus spots */
    
    /* [1] Check if we are doing an 'edited gene list' add/remove
     * command:
     *   CTRL/MOUSE to add genes to editedCL
     *   SHIFT/MOUSE to remove genes from editedCL
     */
    if(mae.cdb.isValidObjFlag
       /* && mouseKeyMods!=ClusterGenes.UPDATE */)
      mae.egl.processEditCommand(mouseKeyMods);
    
    /* [2] If enabled, then popup a Web browser with the GenBank data
     * via the NCI proxy server which can get data not on the NCI server.
     * [NOTE] only used the proxy server with MGAP and applet.
     */
    if(mae.cdb.isValidObjFlag)
    { /* get the popup genomic Web browser data if we can */
      String
        url= null,                          /* null means we failed */
        prefix= ((mae.codeBase.startsWith("http"))
                   ? mae.proxyServer : ""); /* use proxy server if applet */
      boolean
        hasCloneID= (mae.gipo.cloneIdIdx!=-1),
        hasUniGeneClusterID= (mae.gipo.Unigene_cluster_IDidx!=-1),
        hasLocusID= (mae.gipo.LocusLinkIdIdx!=-1),
        hasOmimID= (mae.gipo.OmimIdIdx!=-1),
        hasSwissProt= (mae.gipo.SwissProtIdx!=-1),
        has_dbEST= (mae.gipo.dbESTid3Idx!=-1 || mae.gipo.dbESTid5Idx!=-1),
        hasUniGeneID= (mae.gipo.Unigene_cluster_IDidx!=-1),
        hasGenBank= (mae.gipo.GenBankAccIdx!=-1 ||
                     mae.gipo.GenBankAcc3Idx!=-1 ||
                     mae.gipo.GenBankAcc5Idx!=-1 ||
                     mae.gipo.RefSeqIdIdx!=-1),
        genomicDBspecified= false;
      String
        cloneID= (gene.Clone_ID!=null && gene.Clone_ID.length()>0)
                    ? gene.Clone_ID : null,
        unigeneClusterID= (gene.Unigene_ID!=null && gene.Unigene_ID.length()>0)
                            ? gene.Unigene_ID : null,
        locusID= (gene.LocusID!=null && gene.LocusID.length()>0)
                    ? gene.LocusID : null,
        OmimID= (gene.OmimID!=null && gene.OmimID.length()>0)
                    ? gene.OmimID : null,
        RefSeqID= (gene.RefSeqID!=null && gene.RefSeqID.length()>0)
                    ? gene.RefSeqID : null,
        swissProt= (gene.SwissProt!=null && gene.SwissProt.length()>0)
                        ? gene.SwissProt : null,
        geneName= gene.Gene_Name,
        gb= gene.GenBankAcc,
        gb3= gene.GenBankAcc3,
        gb5= gene.GenBankAcc5,
        gbID= (RefSeqID!=null && RefSeqID.length()>0)
                ? RefSeqID                     /* use RefSeq if it exists */
                : ((gb!=null && gb.length()>0) 
                      ? gb 
                      : ((gb3!=null && gb3.length()>0)
                           ? gb3
                           : ((gb5!=null && gb5.length()>0)
                                ? gb5 : null))),
        dbEST3= gene.dbEST3,
        dbEST5= gene.dbEST5,
        dbEst= ((dbEST3!=null && dbEST3.length()>0)
                 ? dbEST3 : (dbEST5!=null && dbEST5.length()>0)
                               ? dbEST5 : null),
        geneID= ((cloneID!=null) 
                   ? cloneID : ((unigeneClusterID!=null)
                                  ? unigeneClusterID : ((locusID!=null) 
                                     ? locusID : gbID)));
      
      /* Note that changing the current gene. Because other data goes
       * into msg1 text area, disable showMsg() when save history.
       */
      String curGeneMsg= "Setting Current Gene to ["+geneName+"] "+geneID;
      if(mae.useKmeansClusterCntsDispFlag)
        curGeneMsg += " [Cluster #"+gene.clusterNodeNbr+"]";
      Util.saveCmdHistory(curGeneMsg, false);
      
      if(mae.genBankViewerFlag)
      { /* use GeneBank web server */
        /* Test which URL we should  be using. If have
         * GenBank IDs, then use NCBI.
         * If CloneIDs, use nciarray. etc.
         */        
        genomicDBspecified= true;
        if(hasGenBank && gbID!=null)
        { /* need since using GenBankAcc# */
          url= prefix + cfg.genBankAccURL + gbID;
        }
        else if(mae.NEVER && hasCloneID && cloneID!=null)
        { /* map GenBank from CloneID */
          //if(cfg.IMAGE2GenBankURL!=null)
          //  url= prefix + cfg.IMAGE2GenBankURL + cloneID;
          if(cfg.genBankCloneURL!=null)
            url= prefix + cfg.genBankCloneURL + cloneID +
            cfg.genBankCloneURLepilogue;
        }
      } /* use GeneBank web server */
      
      else if(mae.dbESTviewerFlag)
      { /* use dbEST web server */
        genomicDBspecified= true;
        if(dbEst!=null)
          url= prefix + cfg.dbEstURL + dbEst;
      }
      
      else if(mae.omimViewerFlag)
      { /* use OMIM web server */
        genomicDBspecified= true;
        if(OmimID!=null)
          url= prefix + cfg.omimURL + OmimID;
      }
      
      else if(mae.uniGeneViewerFlag)
      { /* use UniGene web server */
        genomicDBspecified= true;
        if(hasUniGeneClusterID && unigeneClusterID!=null)
        { /* Fix up the URL by inserting the species prefix */
          /* Map:
           * "http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=Rn&CID=Hs.1234"
           * to
           * "http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=Hs&CID=1234"
           */
          String 
            ugU= cfg.uniGeneClusterIdURL,
            ugId= unigeneClusterID;
          int 
            idxORG= ugU.indexOf("ORG="),
            idxUGID= ugId.indexOf(".");
          if(idxORG!=-1 && idxUGID!=-1)
          { /* patch it up */
            String 
              ugUprefix= ugU.substring(0,idxORG+4), 
              ugUpepilogue= ugU.substring(idxORG+6),
              speciesPrefix= ugId.substring(0,2),
              idEpilogue= ugId.substring(idxUGID+1);
            url= prefix + ugUprefix + speciesPrefix + ugUpepilogue + idEpilogue;  
          } /* patch it up */
          else 
            url= prefix + ugU + ugId;       
        } /* Fix up the URL by inserting the species prefix */
        else if(hasCloneID && cloneID!=null)
          url= prefix + cfg.IMAGE2unigeneURL + cloneID;
        //else if(hasGenBank && gbID!=null)
        //  url= prefix + cfg.gbid2unigeneURL + gbID;
      }
      
      else if(mae.mAdbViewerFlag)
      { /* use CIT's mAdb web server */
        genomicDBspecified= true;
        if(cloneID!=null)
          url= prefix + cfg.mAdbURL + cloneID;
      }
      
      else if(mae.locusLinkViewerFlag)
      { /* LocusLink */
        genomicDBspecified= true;
        if(hasLocusID && locusID!=null)
        { /* use LocusLink LocusID web server */
          url= prefix + cfg.locusLinkURL + locusID;
        }
        else if(hasGenBank && gbID!=null)
        { /* use GBID to LocusLink web server */
          url= prefix + cfg.gbid2LocusLinkURL + gbID;
        }
      } /* LocusLink */
      
      else if(mae.medMinerViewerFlag)
      { /* use discover.nci.nih.gov web server */
        genomicDBspecified= true;
        GeneList eCL= mae.gct.editedCL;  /* get gene2 from EGL */
        int eglLth= eCL.length;
        String
          gene1= cloneID,
          gene2= (eglLth>0 &&  eCL.mList[0]!=null)
                    ? eCL.mList[0].Clone_ID : null;
        
        //if(gene2!=null)
        //  {
        //    gene2= mae.util.rmvIMAGEstr(gene2);
        //  }
        gene1= mae.util.rmvIMAGEstr(gene1);
        if(gene1!=null && gene1.length()==0)
          gene1= null;
        if(gene2!=null && gene2.length()==0)
          gene2= null;
        
        if(mae.NEVER && gene1!=null && gene2!=null)
          url= prefix + cfg.medMinerURL + gene1 + "&g="+ gene2 + 
               cfg.medMinerURLepilogue;
        else if(gene1!=null)
          url= prefix + cfg.medMinerURL + gene1 + cfg.medMinerURLepilogue;
      } /* use discover.nci.nih.gov web server */
      
      else if(mae.swissProtViewerFlag)
      { /* use Expasy Swiss-Prott web server */
        genomicDBspecified= true;
        if(swissProt!=null)
          url= prefix + cfg.swissProtURL + swissProt;
      }
      
      else if(mae.pirViewerFlag)
      { /* use Expasy PIR ProClass web server */
        genomicDBspecified= true;
        if(swissProt!=null)
          url= prefix + cfg.pirURL + swissProt;
      }
      
      else if(cfg.nGenomicMenus>0)
      { /* see if it is in the list of Genomic databases */
        for(int i=0;i<cfg.nGenomicMenus;i++)
          if( mae.genomicViewerFlag[i])
          { /* found the active one build the URL */
            String sID= gene.lookupIDbyName(cfg.sGenomicIDreq[i]);
            if(sID!=null && sID.length()>0)
            {
              genomicDBspecified= true;
              url= cfg.sGenomicURL[i]+sID+cfg.sGenomicURLepilogue[i];
            }
            break;
          }
      } /* see if it is in the list of Genomic databases */
      
      /* FINALLY! if there is anything - go get it! */
      if(url!=null)
        mae.util.popupViewer(null, url, "MaeAux");
      else  if(genomicDBspecified && url==null)
      { /* no URL since ID not found */
        Util.showMsg3("No genomic ID was available for the Web database you selected.",
                      Color.white, /* foreground */ Color.red /* background */);        
        Util.sleepMsec(500);
      }
      
    } /* get the popup genomic Web browser data if we can */
    
    /* [3] If Expression Profile mode is on, then update the
     * the gene used in drawing the 'single' expression profile plot.
     */
    if(mae.cdb.isValidObjFlag && mbf.exprProfilePopup!=null)
    { /* draw expression profile for the geone */
      String msg= mae.masterIDname+" [" +
                  map.midStaticCL.mList[mid].Master_ID + "]";
      mbf.exprProfilePopup.updateData(mid, msg, true /* drawLabelsFlag */);
    } /* draw expression profile for the gene */
    
    /* [4] If clustering is enabled, then update cluster algorithms
     * and redraw the cluster overlays in the pseudo image.
     */
    if((mae.cdb.isValidObjFlag && mae.useSimGeneClusterDispFlag) ||
       mae.useClusterCountsDispFlag)
    { /* get the list of clusters for current gene */
      /* [DEPRICATED] switch from all genes to single gene mode */
      if(mae.useClusterCountsDispFlag && mouseKeyMods!=ClusterGenes.UPDATE)
      {
        mae.useSimGeneClusterDispFlag=
           EventMenu.setClusterDisplayState(mbf.miCLMfindSimGenesDisp, true);
        if(!mae.stateScr.isVisible || mae.autoStateScrPopupFlag)
          mae.stateScr.regenerateScrollers(false);
        prevObjMID= -1;          /* force refresh in counts mode */
        ClusterGenes.geneClustersPopup.changeGeneButton("Go 'Cluster gene counts'",
                                                        "allGenes",
                                                        mae.RPT_TBL_CUR_GENE_CLUSTER);
      }
      
      /* Get clusters of current gene if clusterDistThr changed */
      if(ClusterGenes.curGeneDistThr!=cfg.clusterDistThr ||
         mae.cdb.objMID!=prevObjMID ||
         oldWorkingCLlength!=mae.fc.workingCL.length)
      { /* find and display similar genes */
        prevObjMID= mae.cdb.objMID;
        oldWorkingCLlength= mae.fc.workingCL.length;
        /*
        if(mae.CONSOLE_FLAG)
          mae.fio.logMsgln("SIC-MR cfg.clusterDistThr="+cfg.clusterDistThr);
        */
        /* Re-cluster genes based on the current gene */
        if(mae.useSimGeneClusterDispFlag)
        { /* Update the gene report */
          ClusterGenes.findClustersOfGene(mae, gene,cfg.clusterDistThr, true);
          ClusterGenes.updateGeneClustersOfCurrentGeneReport();
          mae.is.siCanvas.repaint();
        }
        else if(mae.useClusterCountsDispFlag)
        {
          ClusterGenes.findAllGeneClusterCounts(mae, cfg.clusterDistThr);
          ClusterGenes.updateAllClustersGeneReport();
          mae.is.siCanvas.repaint();
        }
      } /* find and display similar genes */
    } /* get the list of clusters for current gene */
    
    /* [5] If K-means clustering, then copy curCluster genes to E.G.L.*/
    if(mae.cdb.isValidObjFlag && mae.useKmeansClusterCntsDispFlag)
      ClusterGenes.copyCurClusterToEditedGeneList();
    return(true);
  } /* chkOtherCurGeneEffects */
  
  
  /**
   * updateCurGene() - update (plot,report,string,MAEPlugin) popups with the current gene.
   * The popup objects will do whatever else is needed and also
   * handle side-effects.
   * @param mid specifying the gene MID
   * @param mouseKeyMods modifieers (i.e.CONTROL or SHIFT)
   * @param sObj object invoking the change if not null, don't refresh
   *       'self' object which called update.
   * @return true if successful
   * @see PopupRegistry#updateCurGene
   * @see #chkOtherCurGeneEffects
   */
  boolean updateCurGene(int mid, int mouseKeyMods, Object sObj)
  { /* updateCurGene */
    //if(mid<0 || sObj==null)
    //   return(false);
    
    /* [1] Update additional side-effects when change current gene
     * in ms.objXXX
     */
    chkOtherCurGeneEffects(mid, mouseKeyMods);
    
    /* [2] Update current gene in plots, reports, strings requiring it */
    for(int i=0;i<nSPP;i++)
      if((sppPropBits[i] & CUR_GENE)!=0 && (sObj!=sppList[i]))
        sppList[i].updateCurGene(mid,sObj);
    
    for(int i=0;i<nSSSP;i++)
      if((ssspPropBits[i] & CUR_GENE)!=0 && (sObj!=ssspList[i]))
        ssspList[i].updateCurGene(mid);
    
    for(int i=0;i<nSSP;i++)
      if((sspPropBits[i] & CUR_GENE)!=0 && (sObj!=sspList[i]))
        sspList[i].updateCurGene(mid);
    
    for(int i=0;i<nSEPP;i++)
      if((seppPropBits[i] & CUR_GENE)!=0 && (sObj!=seppList[i]))
        seppList[i].updateCurGene(mid);
    
    for(int i=0;i<nMAEP;i++)
      if((maepPropBits[i] & CUR_GENE)!=0 && (sObj!=maepList[i]))
      {
        maepList[i].updateCurGene(mid);
      }
    
    return(true);
  } /* updateCurGene */
  
  
  /**
   * updateFilter() - update popups using Filter workingCL changes for (plot,report,string,MAEPlugin) popups.
   * The popup objects  will do whatever else is needed.
   * @param ml is GeneList to be used when running the data Filter
   * @return true if successful
   * @see PopupRegistry#updateFilter
   * @see ScrollableImageCanvas#repaint
   */
  boolean updateFilter(GeneList ml)
  { /* updateFilter */
    for(int i=0;i<nSPP;i++)
      if((sppPropBits[i] & FILTER)!=0)
        sppList[i].updateFilter(ml);
    
    for(int i=0;i<nSSSP;i++)
      if((ssspPropBits[i] & FILTER)!=0)
        ssspList[i].updateFilter(ml);
    
    for(int i=0;i<nSSP;i++)
      if((sspPropBits[i] & FILTER)!=0)
        sspList[i].updateFilter(ml);
    
    for(int i=0;i<nSEPP;i++)
      if((seppPropBits[i] & FILTER)!=0)
        seppList[i].updateFilter(ml);
    
    for(int i=0;i<nMAEP;i++)
      if((maepPropBits[i] & FILTER)!=0)
        maepList[i].updateFilter();
    
    mae.is.siCanvas.repaint();  /* force a array image repaint */
    return(true);
  } /* updateFilter */
  
  
  /**
   * updateSlider() - update popups using Slider changes for (plot,report,string,MAEPlugin) popups.
   * The popup objects  will do whatever else is needed.
   * @return true if successful
   * @see PopupRegistry#updateSlider
   */
  boolean updateSlider()
  { /* updateSlider */
    for(int i=0;i<nSPP;i++)
      if((sppPropBits[i] & SLIDER)!=0)
        sppList[i].updateSlider();
    
    for(int i=0;i<nSSSP;i++)
      if((ssspPropBits[i] & SLIDER)!=0)
        ssspList[i].updateSlider();
    
    for(int i=0;i<nSSP;i++)
      if((sspPropBits[i] & SLIDER)!=0)
        sspList[i].updateSlider();
    
    for(int i=0;i<nSEPP;i++)
      if((seppPropBits[i] & SLIDER)!=0)
        seppList[i].updateSlider();
    
    for(int i=0;i<nMAEP;i++)
      if((maepPropBits[i] & SLIDER)!=0)
        maepList[i].updateSlider();
    
    return(true);
  } /* updateSlider */
  
  
  /**
   * updateLabels() - update popups using Label changes for (plot,report,string,MAEPlugin) popups.
   * The popup objects  will do whatever else is needed.
   * @return true if successful
   * @see PopupRegistry#updateLabels
   */
  boolean updateLabels()
  { /* updateLabels */
    for(int i=0;i<nSPP;i++)
      if((sppPropBits[i] & LABEL)!=0)
        sppList[i].updateLabels();
    
    for(int i=0;i<nSSSP;i++)
      if((ssspPropBits[i] & LABEL)!=0)
        ssspList[i].updateLabels();
    
    for(int i=0;i<nSSP;i++)
      if((sspPropBits[i] & LABEL)!=0)
        sspList[i].updateLabels();
    
    for(int i=0;i<nSEPP;i++)
      if((seppPropBits[i] & LABEL)!=0)
        seppList[i].updateLabels();
    
    for(int i=0;i<nMAEP;i++)
      if((maepPropBits[i] & SLIDER)!=0)
        maepList[i].updateLabels();
    
    return(true);
  } /* updateLabels */
  
  
  /**
   * doesPopupExist() - check to see if popup exists for this key.
   * @return true if the popup exists, else false if key is null or it fails
   */
  boolean doesPopupExist(String testKey)
  { /* doesPopupExist */
    if(testKey==null)
      return(false);
    
    for(int i=0;i<nSPP;i++)
      if(sppKey[i].equals(testKey))
        return(true);
    
    for(int i=0;i<nSSSP;i++)
      if(ssspKey[i].equals(testKey))
        return(true);
    
    for(int i=0;i<nSSP;i++)
      if(sspKey[i].equals(testKey))
        return(true);
    
    for(int i=0;i<nSEPP;i++)
      if(seppKey[i].equals(testKey))
        return(true);
    
    for(int i=0;i<nMAEP;i++)
      if(maepKey[i].equals(testKey))
        return(true);
    
    return(false);
  } /* doesPopupExist */
  
  
  /**
   * doesUniquePopupExist() - check to see if popup exists for this key
   * @param key is the popup name of the window to test for
   * @return true if key found, else false if key is null or it fails
   * @see #lookupMAEPluginInstanceObjectInstance
   * @see #lookupShowExprProfilesPopupInstance
   * @see #lookupShowPlotPopupInstance
   * @see #lookupShowSpreadsheetPopupInstance
   * @see #lookupShowStringPopupInstance
   */
  boolean doesUniquePopupExist(String testKey)
  { /* doesUniquePopupExist */
    if(testKey==null)
      return(false);
    
    if(lookupShowPlotPopupInstance(testKey)!=null ||
       lookupShowSpreadsheetPopupInstance(testKey)!=null ||
       lookupShowStringPopupInstance(testKey)!=null ||
       lookupShowExprProfilesPopupInstance(testKey)!=null ||
       lookupMAEPluginInstanceObjectInstance(testKey)!=null
       )
      return(true);  /* found an instance */
    
    return(false);
  } /* doesUniquePopupExist */
  
  
  /**
   * lookupShowPlotPopupInstance() - lookup ShowPlotPopup for unique key instance
   * @param key is the popup name of the window to test for
   * @return the instance if found, else null. Set idxSPP to index else -1.
   */
  ShowPlotPopup lookupShowPlotPopupInstance(String testKey)
  { /* lookupShowPlotPopupInstance */
    idxSPP= -1;
    if(testKey==null)
      return(null);
    
    for(int i=0;i<nSPP;i++)
      if(sppKey[i].equals(testKey) && (sppPropBits[i] & UNIQUE)!=0)
      {
        idxSPP= i;
        return(sppList[idxSPP]);
      }
    
    return(null);
  } /* lookupShowPlotPopupInstance */
  
  
  /**
   * lookupShowSpreadsheetPopupInstance() - lookup ShowSpreadsheetPopup for unique key instance
   * @param key is the popup name of the window to test for
   * @return the instance if found, else null. Set idxSSSP to index else -1.
   */
  ShowSpreadsheetPopup lookupShowSpreadsheetPopupInstance(String testKey)
  { /* lookupShowSpreadsheetPopupInstance */
    idxSSSP= -1;
    if(testKey==null)
      return(null);
    
    for(int i=0;i<nSSSP;i++)
      if(ssspKey[i].equals(testKey) && (ssspPropBits[i] & UNIQUE)!=0)
      {
        idxSSSP= i;
        return(ssspList[idxSSSP]);
      }
    
    return(null);
  } /* lookupShowSpreadsheetPopupInstance */
  
  
  /**
   * lookupShowStringPopupInstance() - lookup ShowStringPopup for unique key instance
   * @param key is the popup name of the window to test for
   * @return the instance if found, else null. Set idxSSP to index else -1.
   */
  ShowStringPopup lookupShowStringPopupInstance(String testKey)
  { /* lookupShowStringPopupInstance */
    idxSSP= -1;
    if(testKey==null)
      return(null);
    
    for(int i=0;i<nSSP;i++)
      if(sspKey[i].equals(testKey) && (sspPropBits[i] & UNIQUE)!=0)
      {
        idxSSP= i;
        return(sspList[idxSSP]);
      }
    
    return(null);
  } /* lookupShowStringPopupInstance */
  
  
  /**
   * lookupShowExprProfilesPopupInstance() - lookup ShowExprProfilesPopup for unique key instance
   * @param key is the popup name of the window to test for
   * @return the instance if found, else null. Set idxSEPP to index else -1.
   */
  ShowExprProfilesPopup lookupShowExprProfilesPopupInstance(String testKey)
  { /* lookupShowExprProfilesPopupInstance */
    idxSEPP= -1;
    if(testKey==null)
      return(null);
    
    for(int i=0;i<nSEPP;i++)
      if(seppKey[i].equals(testKey) && (seppPropBits[i] & UNIQUE)!=0)
      {
        idxSEPP= i;
        return(seppList[idxSEPP]);
      }
    
    return(null);
  } /* lookupShowExprProfilesPopupInstance */
  
  
  /**
   * lookupMAEPluginInstanceObjectInstance() - lookup MAEPlugin Object for unique key instance
   * @param key is the popup name of the window to test for
   * @return the instance if found, else null. Set idxMAEP to index else -1.
   */
  Object lookupMAEPluginInstanceObjectInstance(String testKey)
  { /* lookupMAEPluginInstanceObjectInstance */
    idxMAEP= -1;
    if(testKey==null)
      return(null);
    
    for(int i=0;i<nMAEP;i++)
      if(maepKey[i].equals(testKey) && (maepPropBits[i] & UNIQUE)!=0)
      {
        idxMAEP= i;
        return(maepList[idxMAEP]);
      }
    
    return(null);
  } /* lookupMAEPluginInstanceObjectInstance */
  
  
  /**
   * removePopupByKey() - remove popup by unique key if existing instance
   * @param key is the popup name of the window
   * @return the instance if found and return true, else No-OP if not found
   * and return false.
   * @see #lookupMAEPluginInstanceObjectInstance
   * @see #lookupShowExprProfilesPopupInstance
   * @see #lookupShowPlotPopupInstance
   * @see #lookupShowSpreadsheetPopupInstance
   * @see #lookupShowStringPopupInstance
   * @see #rmvPopupFromReg
   */
  boolean removePopupByKey(String testKey)
  { /* removePopupByKey */
    if(testKey==null)
      return(false);
    
    if(lookupShowPlotPopupInstance(testKey)!=null)
    {
      sppList[idxSPP].close();     /* close actual window */
      rmvPopupFromReg(sppList[idxSPP]);
      return(true);
    }
    
    if(lookupShowSpreadsheetPopupInstance(testKey)!=null)
    {
      ssspList[idxSSSP].close();   /* close actual window */
      rmvPopupFromReg(ssspList[idxSSSP]);
      return(true);
    }
    
    if(lookupShowStringPopupInstance(testKey)!=null)
    {
      sspList[idxSSP].close(false);   /* close actual window */
      rmvPopupFromReg(sspList[idxSSP]);
      return(true);
    }
    
    if(lookupShowExprProfilesPopupInstance(testKey)!=null)
    {
      seppList[idxSEPP].close(false);   /* close actual window */
      rmvPopupFromReg(seppList[idxSEPP]);
      return(true);
    }
    
    if(lookupMAEPluginInstanceObjectInstance(testKey)!=null)
    {
      maepList[idxMAEP].close(false);   /* close actual window */
      rmvPopupFromReg(seppList[idxMAEP]);
      return(true);
    }
    
    return(false);
  } /* removePopupByKey */
  
  
  /**
   * checkPopupTimeouts() - check timeouts for all active popups and kill any that exceed the time.
   * We increment the times here. This method is called from the run() infinite loop
   * once each time around.
   * [TODO] Debug...
   * @param runLoopDelayMsec is the timeout to test for
   * @see #rmvPopupFromReg
   */
  void checkStringPopupTimeouts(int runLoopDelayMsec)
  { /* checkPopupTimeouts */
    /* Note: check backwards in case shorten the list more than once */
    for(int i=(nSSP-1);i>=0;i--)
      if((sspPropBits[i] & TIMEOUT)!=0)
      { /* this popup has a timeout - decrement it and then check */
        sspTime[i] += runLoopDelayMsec;
        if(sspTime[i]>=TIMEOUT_DELAY)
        { /* kill it - it has timed out */
          sspList[i].close(false);   /* close actual window */
          rmvPopupFromReg(sspList[i]);
        }
      } /* this popup has a timeout - decrement it and then check */
  } /* checkPopupTimeouts */
  
  
  /**
   * setWaitCursor() - if true set wait cursor, else set default cursor.
   * [TODO] may want to save old cursor if using more than DEFAULT.
   * @param waitCursorFlag is the new cursor state
   */
  void setWaitCursor(boolean waitCursorFlag)
  { /* setWaitCursor */
    Cursor
      cursor= Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR),
      cursor2= cursor;
    
    if(waitCursorFlag)
      cursor= Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    else
      cursor2= Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    
    mbf= mae.mbf;                         /* update if need for first call */
    
    mbf.setCursor(cursor);                /* menu bar */
    mbf.HPlabelPanel.setCursor(cursor);   /* HP labels */
    mbf.HP_Xlabel.setCursor(cursor);
    mbf.HP_Ylabel.setCursor(cursor);
    mbf.curGeneTextButton.setCursor(cursor2);
    
    mae.is.setCursor(cursor);             /* array image */
    mae.is.txtField1.setCursor(cursor);   /* msg1 */
    mae.is.txtField2.setCursor(cursor);   /* msg2 */
    mae.is.txtField3.setCursor(cursor);   /* msg3 */
    
    /* [TODO] set cursor for all subwindows that are registered */
  } /* setWaitCursor */
  
  
  /**
   * updateCurGeneInImageAndReg() - update current gene in pseudoarray image and notify the PopupRegistry.
   * @param nameOrId to set as the new current gene
   * @return true if succeed
   * @see MAExplorer#repaint
   * @see ScrollableImageCanvas#repaint
   * @see SpotFeatures#getXYG
   * @see SpotFeatures#getSpotFeatures
   * @see SpotFeatures#getSpotGenomicData
   * @see Util#showMsg
   * @see Util#showFeatures
   * @see #updateCurGene
   */
  boolean updateCurGeneInImageAndReg(String nameOrId)
  { /* updateCurGeneInImageAndReg */
    String curGeneName= nameOrId;
    boolean hasLeadingDigit= Character.isDigit(nameOrId.charAt(0));
                           /* flag for if first char/digit is a digit */
    int mid= -1;	 /* default to fail */
    
    if(nameOrId!=null && nameOrId.length() > 0)
    { /* check nameOrId */
      for(int i=0; i < map.maxGenes; i++)
      { /* lookup the gene */
        Gene gene= map.midStaticCL.mList[i];
        if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
          continue;            /* ignore bogus spots */
        if(hasLeadingDigit && gene.Master_ID.equals(nameOrId))
        {
          mid= gene.mid;
          break;
        }
        else if(!hasLeadingDigit && gene.Gene_Name!=null &&
        gene.Gene_Name.equals(nameOrId))
        {
          mid= gene.mid;
          break;
        }
      } /* lookup the gene */
      
      if(mid!=-1)
      { /* found it, set current gene and do refresh */
        int gid= map.mid2gid[mid];
        MaHybridSample ms= mae.ms;
        CompositeDatabase cdb= mae.cdb;
        
        cdb.objField= map.gid2fgrc[gid].f;  /* save optimal spot */
        cdb.objGrid= map.gid2fgrc[gid].g;
        cdb.objRow= map.gid2fgrc[gid].r;
        cdb.objCol= map.gid2fgrc[gid].c;
        cdb.objMID= mid;
        cdb.objGID= gid;
        cdb.objGIDG= map.gidToGangGid[gid];
        
        /* Latch it to PseudoImage coordinante */
        cdb.objX= ms.xyCQ[gid].x;
        cdb.objY= ms.xyCQ[gid].y;
        mae.is.siCanvas.toPoint= new Point(cdb.objX,cdb.objY);
        mae.is.siCanvas.xyObj= mae.is.siCanvas.toPoint;
        
        String
          xygStr= mae.sf.getXYG(mae.is.siCanvas.xyObj, mae.ms),
          featuresStr= mae.sf.getSpotFeatures(mae.is.siCanvas.xyObj, mae.ms),
          genomicDataStr= mae.sf.getSpotGenomicData(mae.is.siCanvas.xyObj, mae.ms);
        Util.showMsg(xygStr);
        Util.showFeatures(featuresStr, genomicDataStr);
        
        /* Update the registry */
        updateCurGene(cdb.objMID, 0, null);
        
        mae.is.siCanvas.repaint();
        mae.repaint();         /* redraw the green circle! */
      } /* found it, set current gene and do refresh */
    } /* check nameOrId */
    
    return((mid!=-1));
  } /* updateCurGeneInImageAndReg */
  
  
} /* class PopupRegistry */


