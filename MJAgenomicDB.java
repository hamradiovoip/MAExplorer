/** File: MJAgenomicDB.java */
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJAgenomicDB methods and data structures. 
 *
 * Access genomic databases on the Internet.
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * get_useGenBankViewerFlag() - access popup Web browser GenBank database
 * get_useUniGeneViewerFlag() - access popup Web browser UniGene database
 * get_useDbESTViewerFlag() - access popup Web browser dbEST database
 * get_useOMIMViewerFlag() - access popup Web browser OMIM database
 * get_useMadbViewerFlag() - access popup Web browser mAdb clone database
 * get_useLocksLinkViewerFlag() - access popup Web browser LocusLink database
 * get_useMedMinerViewerFlag() - access popup Web browser MedMiner database
 * get_useSwissProtViewerFlag() - access popup Web browser SwissProt database
 * get_usePIRViewerFlag() - access popup Web browser PIR database
 * ------- manipulate user-defined genomicDB URLs -------- 
 * get_genomicDB_URLs() - get user-defined genomic DB URLs 
 * set_genomicDB_URLs() - set user-defined genomic DB URLs 
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
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.9 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *<P>
 */
 
public class MJAgenomicDB extends MJAbase
{
  
  /**
   * MJAgenomicDB() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAgenomicDB()
  { /* MJAgenomicDB */
  } /* MJAgenomicDB */
  
  /* [TODO] add methods to access the generic genomicDB[]
   * structures in Config... */
  
  
  /* [TODO] add methods to get/set the URL structures in Config... */
  
  /**
   * get_useGenBankViewerFlag() - access popup Web browser GenBank database
   * @return value of flag
   */
  public final boolean get_useGenBankViewerFlag(boolean value)
  { return(mae.genBankViewerFlag); }
  
  
  /**
   * get_useUniGeneViewerFlag() - access popup Web browser UniGene database
   * @return value of flag
   */
  public final boolean get_useUniGeneViewerFlag(boolean value)
  { return(mae.uniGeneViewerFlag); }
  
  
  /**
   * get_useDbESTViewerFlag() - access popup Web browser dbEST database
   * @return value of flag
   */
  public final boolean get_useDbESTViewerFlag(boolean value)
  { return(mae.dbESTviewerFlag); } 
  
  /**
   * get_useOmimViewerFlag() - access popup Web browser OMIM database
   * @return value of flag
   */
  public final boolean get_useOmimViewerFlag(boolean value)
  { return(mae.omimViewerFlag); }
  
  
  /**
   * get_useMadbViewerFlag() - access popup Web browser mAdb clone
   * database
   * @return value of flag
   */
  public final boolean get_useMadbViewerFlag(boolean value)
  { return(mae.mAdbViewerFlag); }
  
  
  /**
   * get_useLocksLinkViewerFlag() - access popup Web browser LocusLink
   * database
   * @return value of flag
   */
  public final boolean get_useLocksLinkViewerFlag(boolean value)
  { return(mae.locusLinkViewerFlag); }
  
  
  /**
   * get_useMedMinerViewerFlag() - access popup Web browser MedMiner
   * database
   * @return value of flag
   */
  public final boolean get_useMedMinerViewerFlag(boolean value)
  { return(mae.medMinerViewerFlag); }
  
  
  /**
   * get_useSwissProtViewerFlag() - access popup Web browser SwissProt
   * database
   * @return value of flag
   */
  public final boolean get_useSwissProtViewerFlag(boolean value)
  { return(mae.swissProtViewerFlag); }
  
  
  /**
   * get_usePIRViewerFlag() - access popup Web browser PIR database
   * @return value of flag
   */
  public final boolean get_usePIRViewerFlag(boolean value)
  { return(mae.pirViewerFlag); }
  
  
  /* ------- manipulate user-defined genomicDB URLs -------- */
  
  
  /**
   * get_genomicDB_URLs() - get user-defined genomic DB URLs
   * <PRE>
   * The Hashtable list returned is defined as:
   * <B>Name               - Value</B>
   * "nGenomicDBs"        - int number of genomic databases
   * "genomicMenuEntires" - String[] list of MAExplorer View
   *                        menu entries for each DB
   * "genomicURL"         - String[] list of URL base addresses
   *                        for each DB
   * "genomicURLepilogue" - String[] optional list of URL epilogue
   *                        addresses for each DB
   * "genomicIDreqired"   - String[] list of "," separated lists
   *                        of identifiers required
   *                        Eg "Clone_ID,GenBankAcc,etc."
   * </PRE>
   *
   * @return hash table list of genomicDB URLs
   */
  public final Hashtable get_genomicDB_URLs()
  { /* get_genomicDB_URLs */
    Hashtable ht= new Hashtable();
    ht.put("nGenomicDBs", new Integer(cfg.nGenomicMenus));
    ht.put("genomicMenuEntires", cfg.sGenomicMenu);
    ht.put("genomicURL", cfg.sGenomicURL);
    ht.put("genomicURLepilogue", cfg.sGenomicURLepilogue);
    ht.put("genomicIDreqired", cfg.sGenomicIDreq);
    return(ht);
  } /* get_genomicDB_URLs */
  
  
  /**
   * set_genomicDB_URLs() - set user-defined genomic DB URLs
   * @param nGenomicDBs is number of genomic databases
   * @param genomicMenuEntires for MAExplorer View menu entries
   *                        for each database
   * @param genomicURL is list of URL base addresses for each DB
   * @param genomicURLepilogue is optional list of URL epilogue
   *                        addresses for each DB
   * @param genomicIDreqired is list of "," separated lists
   *                        of identifiers required
   *                        Eg "Clone_ID,GenBankAcc,etc."
   * @return true if succeed
   */
  public final boolean set_genomicDB_URLs(int nGenomicDBs,
                                          String genomicMenuEntires[],
                                          String genomicURL[],
                                          String genomicURLepilogue[],
                                          String genomicIDreqired[])
  { /* set_genomicDB_URLs */
    cfg.nGenomicMenus= nGenomicDBs;
    cfg.sGenomicMenu= genomicMenuEntires;
    cfg.sGenomicURL= genomicURL;
    cfg.sGenomicURLepilogue= genomicURLepilogue;
    cfg.sGenomicIDreq= genomicIDreqired;
    
    /* [TODO] update menus as well */
    
    return(true);
  } /* set_genomicDB_URLs */
  
  
} /* end of class MJAgenomicDB */

