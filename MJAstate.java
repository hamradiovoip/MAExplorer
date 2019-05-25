/** File: MJAstate.java */

import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJAstate methods and data structures.
 *
 * Access get and save state, get additional state info
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * ------------------- GET FLAGS ------------------------
 * get_useDichromasyDisplayFlag() - get "useDichromasyDisplay" flag status
 * get_gangSpotDuplicatesFlag) - get "gangSpotDuplicates" flag status
 * get_presentationViewFlag() - get "presentationView" flag status
 * get_viewFilteredSpotsFlag() - get "viewFilteredSpots" flag status
 * get_useCy5OverCy3Flag() - get "useCy5OverCy3Flag" flag status
 * get_useMouseOverFlag() - get "useMouseOver" flag status
 * get_isZscoreNormFlag() - get "isZscoreNorm" flag status
 * get_useExprProfileOverlayFlag() - get "useExprProfileOverlay" flag status
 * get_updatePseudoArrayImageFlag() - get "updatePseudoArrayImage" flag status
 * get_useRatioDataFlag() - get "useRatioData" flag status
 * get_allowNegQuantDataFlag() - get "allowNegQuantData" flag status
 * get_useHPxySetsDataFlag() - get "useHPxySetsData" flag status
 * get_useHP_E_ListDataFlag() - get "useMeanHP_EdataFlag" flag status
 * get_abortFlag() - get "abortFlag" flag status
 * get_doGCflag() - get "doGCflag" flag status
 * --------------------- SET FLAGS --------------------------------
 * set_useRatioDataFlag() - set "useRatioData" flag status
 * set_updatePseudoArrayImageFlag() - set "updatePseudoArrayImage" flag status
 * set_isZscoreNormFlag() - set "isZscoreNorm" flag status
 * set_abortFlag() - set "abortFlag" flag status
 * set_doGCflag() - set "doGCflag" flag status
 * --------------------- Change Database ---------------------------
 * openNewFileDB() - open a new .mae database file
 * --------------------- MAEPlugins hash state storage -------------
 * getPluginHashState() - get state object value from MAEPlugin hash state storage.
 * setPluginHashState() - set state object value from MAEPlugin hash state storage.
 * flushPluginHashState() - flush the MAEPlugin hash state storage to the disk.
 *</PRE>
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), J. Evans (CIT), C. Santos (CIT),
 *         G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.12 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *<P>
 */

public class MJAstate extends MJAbase
{
  /** Hashtable used for the MAExplorer MAEPlugins (pluginName+varName)
   * hash storage.
   */
  private static Hashtable
    piHT;
  /** Hashtable size of piHT. */
  private static int
    piHTsize;
  /** flag indicating that hash table is active */
  private static boolean
    hasPluginHashTableInMemoryFlag;
  
  /**
   * MJAstate() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAstate()
  { /* MJAstate */
    piHTsize= 101;
    piHT= new Hashtable(piHTsize);   /* will regrow as needed */    
    hasPluginHashTableInMemoryFlag= false;
  } /* MJAstate */


  /**
   * get_useDichromasyDisplayFlag() - get "useDichromasyDisplay" flag status
   * enable Dichromasy color-blindness display options
   * @return value
   */
  public final boolean get_useDichromasyDisplayFlag()
  { return(mae.useDichromasyFlag); }


  /**
   * get_gangSpotDuplicatesFlag() - get "gangSpotDuplicates" flag status
   * display and report ganged (F1,F2) data else just F1 data
   * @return value
   */
  public final boolean get_gangSpotDuplicatesFlag()
  { return(mae.gangSpotFlag); }



  /**
   * get_presentationViewFlag() - get "presentationView" flag status
   * use larger fonts and graphics for presentations
   * @return value
   */
  public final boolean get_presentationViewFlag()
  { return(mae.presentViewFlag); }


  /**
   * get_viewFilteredSpotsFlag() - get "viewFilteredSpots" flag status
   * in PseudoArray image show Filtered spots else don't show overlays
   * @return value
   */
  public final boolean get_viewFilteredSpotsFlag()
  { return(mae.viewFilteredSpotsFlag); }


  /**
   * get_useCy5OverCy3Flag() - get "useCy5OverCy3Flag" flag status
   * Swap Cy3/Cy to Cy5/Cy3 for reporting purposes
   * @return value
   */
  public final boolean get_useCy5OverCy3Flag()
  { return(mae.useCy5OverCy3Flag); }


  /**
   * get_useMouseOverFlag() - get "useMouseOver" flag status
   */
  public final boolean get_useMouseOverFlag()
  { return(mae.useMouseOverFlag); }


  /**
   * get_isZscoreNormFlag() - get "isZscoreNorm" flag status
   * using one of the Zscore normalizations
   * @return value
   */
  public final boolean get_isZscoreNormFlag()
  { return(mae.isZscoreFlag); }


  /**
   * get_useExprProfileOverlayFlag() - get "useExprProfileOverlay" flag status
   * Plot EP list as overlay 2Dplot else scrollable EP list
   * @return value
   */
  public final boolean get_useExprProfileOverlayFlag()
  { return(mae.useEPoverlayFlag); }


  /**
   * get_useRatioDataFlag() - get "useRatioData" flag status
   * data is Cy3/Cy5 ratio else Intensity
   * @return value
   */
  public final boolean get_useRatioDataFlag()
  { return(mae.useRatioDataFlag); }


  /**
   * get_allowNegQuantDataFlag() - get "allowNegQuantData" flag status
   * allow Negative Quantified intensity data
   * @return value
   */
  public final boolean get_allowNegQuantDataFlag()
  { return(mae.allowNegQuantDataFlag); }


  /**
   * get_useHPxySetsDataFlag() - get "useHPxySetsData" flag status
   * use mean HP-X,Y 'sets' data else HP-X,HP-Y single samples data
   * @return value
   */
  public final boolean get_useHPxySetsDataFlag()
  { return(mae.useHPxySetDataFlag); }

  
  /**
   * get_useHP_E_ListDataFlag() - get "useMeanHPeListDataFlag" flag status
   * use mean HP-E 'list' data else single current HP sample data
   * @return value
   */
  public final boolean get_useHP_E_ListDataFlag()
  { return(mae.useMeanHPeListDataFlag); }


  /**
   * get_updatePseudoArrayImageFlag() - get "updatePseudoArrayImage" flag
   * status. If true, it causes an update of pseudoarray image
   * @return value
   */
  public final boolean get_updatePseudoImgFlag()
  { return(mae.updatePseudoImgFlag); }


  /**
   * get_abortFlag() - get "abortFlag" flag status
   * @return value
   */
  public final boolean get_abortFlag()
  { return(mae.abortFlag); }


  /**
   * get_doGCFlag() - get "doGCflag" flag status
   * invoke the Garbage Collector when have time as determined by
   * the main idle loop
   * @return value
   */
  public final boolean get_doGCFlag()
  { return(mae.doGCflag); }


  /* --------------------- SET FLAGS --------------------------- */

  /**
   * set_updatePseudoArrayImageFlag() - set "updatePseudoArrayImage"
   * flag status. This causes an update of the pseudoarray image
   */
  public final void set_updatePseudoImgFlag(boolean flag)
  { mae.updatePseudoImgFlag= flag; }


  /**
   * set_isZscoreNormFlag() - set "isZscoreNorm" flag status
   * using one of the Zscore normalizations.
   * [DEPRICATED] there are various side-effects since this may
   * make the state inconsistent. So this method should be avoided.
   * @return value
   */
  public final void set_isZscoreNorm(boolean flag)
  { /* set_isZscoreNorm */
    /* [TODO] handle other side effects */
    mae.isZscoreFlag= flag;
    
    /* Adjust any sliders that depend on being in Zscore mode.*/
    //mae.stateScr.adjustRatioOrZscoreCounters();   
  } /* set_isZscoreNorm */


  /**
   * set_abortFlag() - set "abortFlag" flag status
   * @return value
   */
  public final void set_abortFlag(boolean flag)
  { mae.abortFlag= flag; }


  /**
   * set_doGCFlag() - set "doGCflag" flag status to invoke the
   * Garbage Collector when have time as determined by main idle loop.
   * @return value
   */
  public final void set_doGCflag(boolean flag)
  { mae.doGCflag= flag; }

  /* --------------------- Change Database --------------------------- */

  /**
   * openNewFileDB() - open a new .mae database file.
   * @param dbMAEdir - full path of the new database MAE/ 'project' directory.
   *   E.g. "C:/user/test/MAE/" (with appropriate delimiters for the OS you are using).
   * @param maeFilename - name of the .mae startup file to be started
   *   E.g. "Start.mae"
   * @param saveOldDBfileFlag - if set, then save the old DB first
   * @return true if succeed
   */
  public final boolean openNewFileDB(String dbMAEdir, String maeFilename,
                                     boolean saveOldDBfileFlag)
  { /* openNewFileDB */
    String testFileName= dbMAEdir+maeFilename;
    try
    {
      File f= new File(testFileName);
      if(!f.canRead() || !f.exists())
        return(false);
    }
    catch(Exception e)
    { return(false); }

    mae.em.openNewMaeDB(dbMAEdir, maeFilename);           /* open new .mae DB */
    return(true);
  } /* openNewFileDB */

  
  /**
   * setGenBankAccURL() - set the url
   * @param url to set
   */
  public final void setGenBankAccURL( String url )
  { mae.cfg.genBankAccURL = url; }

  
  /**
   * getGenBankAccURL() - get the current url value
   * @return the current url value
   */
  public final String getGeneBankAccURL()
  { return mae.cfg.genBankAccURL; }

  
  /**
   * setDbEstURL() - set the url
   * @param url to set
   */
  public final void setDbEstURL( String url )
  { mae.cfg.dbEstURL= url; }

  
  /**
   * getDbEstURL() - get the current url value
   * @return the current url value
   */
  public final String getDbEstURL()
  { return mae.cfg.dbEstURL;}

  
  /**
   * setGenBankCloneURL() - set the url
   * @param url to set
   */
  public final void setGenBankCloneURL( String url )
  { mae.cfg.genBankCloneURL = url; }
  
  
  /**
   * getGenBankCloneURL() - get the current url value
   * @return the current url value
   */
  public final String getGenBankCloneURL()
  { return mae.cfg.genBankCloneURL; }

  
  /**
   * setGenBankCloneURLepilogue() - set the url
   * @param url to set
   */
  public final void setGenBankCloneURLepilogue( String url )
  { mae.cfg.genBankCloneURLepilogue= url; }

  
  /**
   * getGenBankCloneURLepilogue() - get the current url value
   * @return the current url value
   */
  public final String getGenBankCloneURLepilogue()
  { return mae.cfg.genBankCloneURLepilogue; }

  
  /**
   * setIMAGE2GenBankURL() - set the url
   * @param url to set
   */
  public final void setIMAGE2GenBankURL( String url )
  { mae.cfg.IMAGE2GenBankURL= url; }

  
  /**
   * getIMAGE2GenBankURL() - get the current url value
   * @return the current url value
   */
  public final String getIMAGE2GenBankURL()
  { return mae.cfg.IMAGE2GenBankURL; }

  
  /**
   * setIMAGE2unigeneURL() - set the url
   * @param url to set
   */
  public final void setIMAGE2unigeneURL( String url )
  { mae.cfg.IMAGE2unigeneURL= url; }

  
  /**
   * getIMAGE2unigeneURL() - get the current url value
   * @return the current url value
   */
  public final String getIMAGE2unigeneURL()
  { return mae.cfg.IMAGE2unigeneURL; }

  
  /**
   * setMAdbURL() - set the url
   * @param url to set
   */
  public final void setMAdbURL( String url )
  { mae.cfg.mAdbURL= url; }

  
  /**
   * getMAdbURL() - get the current url value
   * @return the current url value
   */
  public final String getMAdbURL()
  { return mae.cfg.mAdbURL; }

  
  /**
   * setUniGeneURL() - set the url
   * @param url to set
   */
  public final void setUniGeneURL( String url )
  { mae.cfg.uniGeneURL= url; }

  
  /**
   * getUniGeneURL() - get the current url value
   * @return the current url value
   */
  public final String getUniGeneURL()
  { return mae.cfg.uniGeneURL; }

  
  /**
   * setUniGeneClusterIdURL() - set the url
   * @param url to set
   */
  public final void setUniGeneClusterIdURL( String url )
  { mae.cfg.uniGeneClusterIdURL= url; }

  
  /**
   * getUniGeneClusterIdURL() - get the current url value
   * @return the current url value
   */
  public final String getUniGeneClusterIdURL()
  { return mae.cfg.uniGeneClusterIdURL; }

  
  /**
   * setGeneCardURL() - set the url
   * @param url to set
   */
  public final void setGeneCardURL( String url )
  { mae.cfg.geneCardURL= url; }

  
  /**
   * getGeneCardURL() - get the current url value
   * @return the current url value
   */
  public final String getGeneCardURL()
  { return mae.cfg.geneCardURL; }

  
  /**
   * setGbid2LocusLinkURL() - set the url
   * @param url to set
   */
  public final void setGbid2LocusLinkURL( String url )
  { mae.cfg.gbid2LocusLinkURL= url;}

  
  /**
   * getGbid2LocusLinkURL() - get the current url value
   * @return the current url value
   */
  public final String getGbid2LocusLinkURL()
  { return mae.cfg.gbid2LocusLinkURL; }

  
  /**
   * setLocusLinkURL() - set the url
   * @param url to set
   */
  public final void setLocusLinkURL( String url )
  { mae.cfg.locusLinkURL= url; }

  
  /**
   * getLocusLinkURL() - get the current url value
   * @return the current url value
   */
  public final String getLocusLinkURL()
  { return mae.cfg.locusLinkURL; }

  
  /**
   * setSwissProtURL() - set the url
   * @param url to set
   */
  public final void setSwissProtURL( String url )
  { mae.cfg.swissProtURL= url; }

  
  /**
   * getSwissProtURL() - get the current url value
   * @return the current url value
   */
  public final String getSwissProtURL()
  { return mae.cfg.swissProtURL; }

  
  /**
   * setPirURL() - set the url
   * @param url to set
   */
  public final void setPirURL( String url )
  { mae.cfg.pirURL= url; }

  
  /**
   * getPirURL() - get the current url value
   * @return the current url value
   */
  public final String getPirURL()
  { return mae.cfg.pirURL; }

  
  /**
   * setMedMinerURL() - set the url
   * @param url to set
   */
  public final void setMedMinerURL( String url )
  { mae.cfg.medMinerURL= url; }

  
  /**
   * getMedMinerURL() - get the current url value
   * @return the current url value
   */
  public final String getMedMinerURL()
  { return mae.cfg.medMinerURL; }

  
  /**
   * setMedMinerURLepilogue() - set the url
   * @param url to set
   */
  public final void setMedMinerURLepilogue( String url )
  { mae.cfg.medMinerURLepilogue= url; }

  
  /**
   * getMedMinerURLepilogue() - get the current url value
   * @return the current url value
   */
  public final String getMedMinerURLepilogue()
  { return mae.cfg.medMinerURLepilogue; }
  
  
  /* ---------------- MAEPlugins hash state storage --------- */
  
  /** 
   * getPluginHashState() - get int state value from MAEPlugin hash state storage
   * This looks for a symbol with the name (pluginName + "." + varName) in the
   * global hash store.
   * @param pluginName of plugin
   * @param varName of variable in the plugin
   * @param defValue is the default value of variable if not found in hash store
   * @return Object for the variable if it exists, else return null
   */
   public int getPluginHashState(String pluginName, String varName, int defValue)
   { /* getPluginState */
     Object obj= getPluginHashState(pluginName, varName);
     if(obj==null || !(obj instanceof Integer))
       return(defValue);     
     int value= ((Integer)obj).intValue();     
     return(value);
   } /* getPluginHashState */
   
  
  /** 
   * getPluginHashState() - get byte state value from MAEPlugin hash state storage
   * This looks for a symbol with the name (pluginName + "." + varName) in the
   * global hash store.
   * @param pluginName of plugin
   * @param varName of variable in the plugin
   * @param defValue is the default value of variable if not found in hash store
   * @return Object for the variable if it exists, else return null
   */
   public byte getPluginHashState(String pluginName, String varName, byte defValue)
   { /* getPluginState */
     Object obj= getPluginHashState(pluginName, varName);
     if(obj==null || !(obj instanceof Byte))
       return(defValue);     
     byte value= ((Byte)obj).byteValue();     
     return(value);
   } /* getPluginHashState */
   
  
  /** 
   * getPluginHashState() - get boolean state value from MAEPlugin hash state storage
   * This looks for a symbol with the name (pluginName + "." + varName) in the
   * global hash store.
   * @param pluginName of plugin
   * @param varName of variable in the plugin
   * @param defValue is the default value of variable if not found in hash store
   * @return Object for the variable if it exists, else return null
   */
   public boolean getPluginHashState(String pluginName, String varName, boolean defValue)
   { /* getPluginState */
     Object obj= getPluginHashState(pluginName, varName);
     if(obj==null || !(obj instanceof Boolean))
       return(defValue);     
     boolean value= ((Boolean)obj).booleanValue();     
     return(value);
   } /* getPluginHashState */
   
  
  /** 
   * getPluginHashState() - get long state value from MAEPlugin hash state storage
   * This looks for a symbol with the name (pluginName + "." + varName) in the
   * global hash store.
   * @param pluginName of plugin
   * @param varName of variable in the plugin
   * @param defValue is the default value of variable if not found in hash store
   * @return Object for the variable if it exists, else return null
   */
   public long getPluginHashState(String pluginName, String varName, long defValue)
   { /* getPluginState */
     Object obj= getPluginHashState(pluginName, varName);
     if(obj==null || !(obj instanceof Long))
       return(defValue);     
     long value= ((Long)obj).longValue();     
     return(value);
   } /* getPluginHashState */
   
  
  /** 
   * getPluginHashState() - get float state value from MAEPlugin hash state storage
   * This looks for a symbol with the name (pluginName + "." + varName) in the
   * global hash store.
   * @param pluginName of plugin
   * @param varName of variable in the plugin
   * @param defValue is the default value of variable if not found in hash store
   * @return Object for the variable if it exists, else return null
   */
   public float getPluginHashState(String pluginName, String varName, float defValue)
   { /* getPluginState */
     Object obj= getPluginHashState(pluginName, varName);
     if(obj==null || !(obj instanceof Float))
       return(defValue);     
     float value= ((Float)obj).floatValue();     
     return(value);
   } /* getPluginHashState */
   
  
  /** 
   * getPluginHashState() - get double state value from MAEPlugin hash state storage
   * This looks for a symbol with the name (pluginName + "." + varName) in the
   * global hash store.
   * @param pluginName of plugin
   * @param varName of variable in the plugin
   * @param defValue is the default value of variable if not found in hash store
   * @return Object for the variable if it exists, else return null
   */
   public double getPluginHashState(String pluginName, String varName, double defValue)
   { /* getPluginState */
     Object obj= getPluginHashState(pluginName, varName);
     if(obj==null || !(obj instanceof Double))
       return(defValue);     
     double value= ((Float)obj).floatValue();     
     return(value);
   } /* getPluginHashState */
   
   
  /** 
   * getPluginHashState() - get state object value from MAEPlugin hash state storage.
   * This looks for a symbol with the name (pluginName + "." + varName) in the
   * global hash store.
   * @param pluginName of plugin
   * @param varName of variable in the plugin
   * @return Object for the variable if it exists, else return null
   */
   public Object getPluginHashState(String pluginName, String varName)
   { /* getPluginState */
     if(!hasPluginHashTableInMemoryFlag)
        readPluginHashState();
     if(piHT==null || pluginName==null || varName==null)
       return(null);
     
     String name= pluginName + "." + varName;
     Object obj= piHT.get(name);
     
     return(obj);
   } /* getPluginHashState */
  
    
  /** 
   * setPluginHashState() - set state object value from MAEPlugin hash state storage.
   * This looks for a symbol with the name (pluginName + "." + varName) in the
   * global hash store.
   * @param pluginName of plugin
   * @param varName of variable in the plugin
   * @param value of object to save
   * @return true if succeeds
   */
   public boolean setPluginHashState(String pluginName, String varName, Object value)
   { /* setPluginState */
     if(piHT==null || pluginName==null || varName==null)
       return(false);     
     String name= pluginName + "." + varName;
     Object obj= piHT.put(name,value);     
     return(true);     
   } /* setPluginState */
  
  
  /** 
   * getPluginHashStateFileName() - get the MAEPlugin hash state disk file name.
   * The MJAPlugins hash storage database name is based on the MAEPlugins DB name.
   * It is saved in State/dbStartupName + ".hash"   
   * <BR>
   * [TODO]
   * @return file name if succeeds, else null
   */
   public String getPluginHashStateFileName()
   { /* getPluginHashStateFileName */
     int idx= mae.defStartupFile.lastIndexOf(".mae");
     if(idx==-1)
       return(null);
     String 
       dbName= mae.defStartupFile.substring(0,idx),
       hashFileName= dbName + ".hash",
       stateDir= Util.rmvFinalSubDirectory(mae.defDir,"State", true),
       fileName= stateDir + hashFileName;
       
     return(fileName);
   } /* getPluginHashStateFileName */
  
  
  /** 
   * flushPluginHashState() - flush the MAEPlugin hash state storage to the disk.
   * The MJAPlugins hash storage database name is based on the MAEPlugins DB name.
   * It is saved in State/dbStartupName + ".hash" 
   * <BR>
   * [TODO]
   * @return true if succeeds
   */
   public boolean flushPluginHashState()
   { /* flushPluginHashState */
     boolean flag= false;      
     String fileName= getPluginHashStateFileName();
       
     /* [TODO] open the hash file and write out the hash table piHT
      * as serialized data then set the flag to true.
      */
     
     return(flag);
   } /* flushPluginHashState */
  
    
  /** 
   * readPluginHashState() - read the MAEPlugin hash state storage from the disk.
   * The MJAPlugins hash storage database name is based on the MAEPlugins DB name.
   * It is saved in State/dbStartupName + ".hash" 
   * <BR>
   * [TODO]
   * @return true if succeeds or the table was previously read
   */
   boolean readPluginHashState()
   { /* readPluginHashState */
     if(hasPluginHashTableInMemoryFlag)
       return(true);
     
     boolean flag= false;      
     String fileName= getPluginHashStateFileName();
       
     /* [TODO] open the hash file and read the hash table piHT
      * as serialized data then set the flag to true.
      */
     if(flag)
       hasPluginHashTableInMemoryFlag= true;
     
     return(flag);
   } /* readPluginHashState */
   
} /* end of class MJAstate */

