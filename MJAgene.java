/** File: MJAgene.java */

 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJAgene methods and data structures. 
 *
 * Access single gene data. This class has several data structures
 * (1) the <B>Default Gene</B> that may be set to the Master Gene ID
 * or MID; (2) the Current Gene which is the current gene for all
 * samples; (3) the Access Sample used to access MID data from that
 * sample - this is not the Current Sample (i.e. HP) set from the
 * main menu. See MJAsample to manipulate that.
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE> 
 * --------------- Default Gene ------------------------
 * setDefaultGene() - define current gene by the Master Gene ID mid
 *
 * --------------- Get data from default Gene ------------------------
 * get_MasterGeneName() - get MasterGeneName for default gene
 * get_Gene_Name() - get Gene_Name for default gene
 * get_UGclusterName() - get UGclusterName for default gene, if exists
 * get_Master_ID() - get Master_ID for default gene
 * get_Generic_ID() - get Generic_ID for default gene, if exists
 * get_Clone_ID() - get Clone_ID for default gene, if exists
 * get_GenBankAcc3ID() - get GenBank Acc 3' for default gene, if exists
 * get_GenBankAcc5ID() - get GenBank Acc 5' for default gene, if exists
 * get_GenBankAccID() - get GenBankAcc for default gene, if exists
 * get_Unigene_ID() - get Unigene_ID for default gene, if exists
 * get_dbEST3ID() - get dbEST3 for default gene, if exists
 * get_dbEST5ID() - get dbEST5 for default gene, if exists
 * get_LocusID() - get LocusID for default gene, if exists
 * get_SwissProtID() - get SwissProt for default gene, if exists
 * get_plate() - get original plate for default gene, if exists
 * get_plate_row() - get original plate_row for default gene, if exists
 * get_plate_col) - get original plate_col for default gene, if exists
 * get_data() - get data for default gene, if exists
 * get_data1() - get data1 for default gene, if exists
 * get_data2() - get data2 for default gene, if exists
 * get_pValue() - get pValue for default gene, if exists
 * get_CV() - get Coefficient of Variation for default gene, if exists
 * get_clusterNodeNbr() - get clusterNodeNbr for default gene, if exists
 * get_nGeneClustersCnt() - get nGeneClustersCnt for default gene, if exists
 * get_properties() - get properties for default gene, if exists
 * get_nDuplGenes() - # duplicate genes in duplMIDlist[] for default gene
 * get_duplMIDlist() - get list of MIDs for duplicate genes for default gene
 *
 *
 * --------------- Set data for gene specified by mid ------------------------
 * set_MasterGeneName() - set MasterGeneName for specified gene
 * set_Gene_Name() - set Gene_Name for specified gene
 * set_UGclusterName() - set UGclusterName for specified gene, if exists
 * set_Master_ID() - set Master_ID for specified gene
 * set_Generic_ID() - set Generic_ID for specified gene, if exists
 * set_Clone_ID() - set Clone_ID for specified gene, if exists
 * set_GenBankAcc3ID() - set GenBank Acc 3' for specified gene, if exists
 * set_GenBankAcc5ID() - set GenBank Acc 5' for specified gene, if exists
 * set_GenBankAccID() - set GenBankAcc for specified gene, if exists
 * set_Unigene_ID() - set Unigene_ID for specified gene, if exists
 * set_dbEST3ID() - set dbEST3 for specified gene, if exists
 * set_dbEST5ID() - set dbEST5 for specified gene, if exists
 * set_LocusID() - set LocusID for specified gene, if exists
 * set_SwissProtID() - set SwissProt for specified gene, if exists
 * set_plate() - set original plate for specified gene, if exists
 * set_plate_row() - set original plate_row for specified gene, if exists
 * set_plate_col) - set original plate_col for specified gene, if exists
 * set_pValue() - set pValue for specified gene, if exists
 * set_clusterNodeNbr() - set clusterNodeNbr for specified gene, if exists
 * set_nGeneClustersCnt() - set nGeneClustersCnt for specified gene, if exists
 * set_properties() - set properties for specified gene, if exists
 * set_nDuplGenes() - # duplicate genes in duplMIDlist[] for specified gene
 * set_duplMIDlist() - get list of MIDs for duplicate genes for specified gene
 *
 * ----------------------- Current Gene ------------------------
 * setGeneMIDfromXYcoord() - get current gene MID from XY coordinates
 * setCurrentGeneFromMID() - set the Current Gene to the specified mid
 * isCurGeneValid() - test if mid is a valid gene for the current gene.
 * getCurGeneMID() - get MID (Master Gene ID) number for current gene
 * getGridIDnbr() - get GID (Grid layout ID) number for current gene
 * getCurGeneGIDG() - get GIDG (Ganged Grid layout ID) # for current gene
 * getCurGeneField() - get MID (Master Gene ID) number for current gene
 * getCurGeneGrid() - get the grid for current gene
 * getCurGeneRow() - get the row in the grid for current gene
 * getCurGeneCol() - get the column in the grid for current gene
 * getCurGeneXcoord() - get X coordinate in pseudoarray for current gene
 * getCurGeneYcoord() - get Y coordinate in pseudoarray for current gene
 *
 * -------- Data from gene or spot for specific sample ------------------
 * setCurrentSample() - set sample for subsequent gene or spot data access
 * getRawDataByGID() - return spot intensity data specified by GID  
 * getRawBackgroundDataByGID() - return spot raw background intensity by GID
 * getRawIntensData() - get spot (Intensity - Background) data by GID
 * getScaledSpotData() - get scaled spot (Intens - Bkgrd) data by GID
 * getScaledSpotData1() - get scaled spot (Intens - Bkgrd) data1 by GID
 * getScaledSpotData2() - get scaled spot (Intens - Bkgrd) data2 by GID
 * computeMinMaxF1F2Data() - compute F1 F2 min and max raw intensity extrema
 *
 * -------- Summary Data from gene or spot for specific sample ------------------
 * getGeneMeasurementSummary() - get spot quant. summary of (gene,sample).
 * getGeneFeatureLine() - get spot feature data summary of (gene,sample).
 * getGeneGenomicLine() - get spot genomic data summary of (gene,sample).
 *
 * ----- Lookup MID and MID lists by various genomic identifiers and names -----
 * lookupMIDfromGeneName() - lookup mid from Gene Name.
 * lookupMIDlistFromGeneName() - lookup mid list from Gene Name.
 * lookupMIDfromCloneID() - lookup mid from CloneID
 * lookupMIDlistFromCloneID() - lookup mid list from CloneID.
 * lookupMIDfromUniGeneID() - lookup mid from UniGene ID
 * lookupMIDlistFromUniGeneID() - lookup mid list from UniGene ID.
 * lookupMIDfromRefSeqID() - lookup mid from RefSeq ID
 * lookupMIDlistFromRefSeqID() - lookup mid list from RefSeq ID.
 * lookupMIDfromLocusID() - lookup mid from Locus ID
 * lookupMIDlistFromLocusID() - lookup mid list from Locus ID.
 * lookupMIDfromSwissProtID() - lookup mid from SwissProt ID
 * lookupMIDlistFromSwissProtID() - lookup mid list from SwissProt ID. 
 * lookupMIDfromGenBankACC() - lookup mid from GenBank ACC ID.
 * lookupMIDlistFromGenBankACC() - lookup mid list from GenBank ID.
 * lookupMIDfrom_dbEST() - lookup mid from dbEST 3' or 5' ID.
 * lookupMIDlistFrom_dbEST() - lookup mid list from dbEST 3' or 5' ID.
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
 * @version $Date: 2003/11/24 21:18:54 $   $Revision: 1.10 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *<P>
 */
 
public class MJAgene extends MJAbase
{  
  
   private Gene
     gene,
     defaultGene;
   private MaHybridSample          
     msW= null;
   
   
   /**
    * MJAgene() - constructor for Open Java API for MAExplorer to
    * access MAExplorer data in a uniform interface.  This Class lets us
    * access the underlying MAExplorer data structures in a uniform way that
    * hides the complexity. It is designed to be used with MAExplorer Plugins.
    */
   MJAgene()
   { /* MJAgene */
     defaultGene= new Gene(-1,-1);
     gene= defaultGene;
   } /* MJAgene */   
   
   
   /* --------------- Default Gene ------------------------ */
   
   
   /**
    * setDefaultGene() - define the default gene by the Master Gene ID mid
    * for interrogation by the get_XXXX() methods. Accessing genes features
    * this way is faster than passing the mid each time.
    * @param mid is the master gene ID
    * @return true if valid mid.
    */
   public final boolean setDefaultGene(int mid)
   { /* setDefaultGene */
     gene= (mid>=0) ? map.midStaticCL.mList[mid] : defaultGene;
     return((gene!=null));
   }  /* setDefaultGene */
   
   
  /* --------------- Get data from default Gene ------------------------ */
   
   /**
    * get_MasterGeneName() - get MasterGeneName for default gene
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_MasterGeneName()
   { return(gene.MasterGeneName); }
   
   
   /**
    * get_Gene_Name() - get Gene_Name for default gene
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_Gene_Name()
   { return(gene.Gene_Name); }
   
   
   /**
    * get_UGclusterName() - get UGclusterName for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_UGclusterName()
   { return(gene.UGclusterName); }
   
   
   /**
    * get_Master_ID() - get Master_ID for default gene
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_Master_ID()
   { return(gene.Master_ID); }
   
   
   /**
    * get_Generic_ID() - get Generic_ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_Generic_ID()
   { return(gene.Generic_ID); }
   
   
   /**
    * get_Clone_ID() - get Clone_ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_Clone_ID()
   { return(gene.Clone_ID); }
   
   
   /**
    * get_GenBankAcc3ID() - get GenBank Acc 3' ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_GenBankAcc3ID()
   { return(gene.GenBankAcc3); }
   
   
   /**
    * get_GenBankAcc5ID() - get GenBank Acc 5' ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_GenBankAcc5ID()
   { return(gene.GenBankAcc5); }
   
   
   /**
    * get_GenBankAccID() - get GenBankAcc ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_GenBankAccID()
   { return(gene.GenBankAcc); }
   
   
   /**
    * get_Unigene_ID() - get Unigene_ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_Unigene_ID()
   { return(gene.Unigene_ID); }
   
   
   /**
    * get_dbEST3ID() - get dbEST3 ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_dbEST3ID()
   { return(gene.dbEST3); }
   
   
   /**
    * get_dbEST5ID() - get dbEST5 ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_dbEST5ID()
   { return(gene.dbEST5); }
   
   
   /**
    * get_LocusID() - get LocusID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_LocusID()
   { return(gene.LocusID); }
   
   
   /**
    * get_SwissProtID() - get SwissProt ID for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_SwissProtID()
   { return(gene.SwissProt); }
   
   
   /**
    * get_plate() - get original plate for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_plate()
   { return(gene.plate); }
   
   
   /**
    * get_plate_row() - get original plate_row for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_plate_row()
   { return(gene.plate_row); }
   
   
   /**
    * get_plate_col) - get original plate_col for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final String get_plate_col()
   { return(gene.plate_col); }
   
   
   /**
    * get_data() - get data for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final float get_data()
   { return(gene.data); }
   
   
   /**
    * get_data1() - get data1 for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final float get_data1()
   { return(gene.data1); }
   
   
   /**
    * get_data2() - get data2 for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final float get_data2()
   { return(gene.data2); }
   
   
   /**
    * get_pValue() - get pValue for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final float get_pValue()
   { return(gene.pValue); }
   
   
   /**
    * get_CV() - get Coefficient of Variation for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final float get_CV()
   { return(gene.cv); }
   
   
   /**
    * get_clusterNodeNbr() - get clusterNodeNbr for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final int get_clusterNodeNbr()
   { return(gene.clusterNodeNbr); }
   
   
   /**
    * get_nGeneClustersCnt() - get nGeneClustersCnt for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final int get_nGeneClustersCnt()
   { return(gene.nGeneClustersCnt); }
   
   
   /**
    * get_properties() - get properties for default gene, if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final int get_properties()
   { return(gene.properties); }
   
   
   /**
    * get_nDuplGenes() - # duplicate genes in duplMIDlist[] for default gene,
    * if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final int get_nDuplGenes()
   { return(gene.nMid); }
   
   
   /**
    * get_duplMIDlist() - get list of MIDs for duplicate genes for default gene,
    * if exists
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final int[] get_duplMIDlist()
   { return(gene.midList); }
   
   
   /**
    * get_nGenomicID() - get # additional genomic IDs in the GenomicID[] list
    * for the default gene, if exists.
    * @return value, if not valid then return -1
    * @see #setDefaultGene
    */
   public final int[] get_nGenomicID()
   { return(gene.nGenomicID); }
   
   
   /**
    * get_GenomicID() - geta list of additional genomic IDs for the default
    * gene, if exists.
    * @return list, ifdoes not exist then return null
    * @see #setDefaultGene
    */
   public final String[] get_GenomicID()
   { return(gene.GenomicID); }
   
   
  /* --------------- Set data for gene specified by mid ---------------- */
   
   /**
    * set_MasterGeneName() - set MasterGeneName for specified gene
    * @param mid is master gene index of gene to modify
    * @param masterGeneName to set
    * @return true if succeed
    */
   public final boolean set_MasterGeneName(int mid, String masterGeneName)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].MasterGeneName= masterGeneName;
     return(true);
   }
   
   
   /**
    * set_Gene_Name() - set Gene_Name for specified gene
    * @param mid is master gene index of gene to modify
    * @param geneName to set
    * @return true if succeed
    */
   public final boolean set_Gene_Name(int mid, String geneName)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].Gene_Name= geneName;
     return(true);
   }
        
   
   /**
    * set_UGclusterName() - set UGclusterName for specified gene
    * @param mid is master gene index of gene to modify
    * @param uniGeneName to set
    * @return true if succeed
    */
   public final boolean set_UGclusterName(int mid, String uniGeneName)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].UGclusterName= uniGeneName;
     return(true);
   }
   
   
   /**
    * set_Master_ID() - set Master_ID for specified gene
    * @param mid is master gene index of gene to modify
    * @param masterID to set
    * @return true if succeed
    */
   public final boolean set_Master_ID(int mid, String masterID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].Master_ID= masterID;
     return(true);
   }
   
   
   /**
    * set_Generic_ID() - set Generic_ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param genericID to set
    * @return true if succeed
    */
   public final boolean set_Generic_ID(int mid, String genericID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].Generic_ID= genericID;
     return(true);
   }
   
   
   /**
    * set_Clone_ID() - set Clone_ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param cloneID to set
    * @return true if succeed
    */
   public final boolean set_Clone_ID(int mid, String cloneID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].Clone_ID= cloneID;
     return(true);
   }
   
   
   /**
    * set_GenBankAcc3ID() - set GenBank Acc 3' ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param genBankAcc3ID to set
    * @return true if succeed
    */
   public final boolean set_GenBankAcc3ID(int mid, String geneBankAcc3ID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].GenBankAcc3= geneBankAcc3ID;
     return(true);
   }
   
   
   /**
    * set_GenBankAcc5ID() - set GenBank Acc 5' ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param genBankAcc5ID to set
    * @return true if succeed
    */
   public final boolean set_GenBankAcc5ID(int mid, String geneBankAcc5ID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].GenBankAcc5= geneBankAcc5ID;
     return(true);
   }
   
   
   /**
    * set_GenBankAccID() - set GenBankAcc ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param genBankAccID to set
    * @return true if succeed
    */
   public final boolean set_GenBankAccID(int mid, String geneBankAccID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].GenBankAcc= geneBankAccID;
     return(true);
   }
   
   
   /**
    * set_Unigene_ID() - set Unigene_ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param unigeneID to set
    * @return true if succeed
    */
   public final boolean set_Unigene_ID(int mid, String unigeneID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].Unigene_ID= unigeneID;
     return(true);
   }
   
   
   /**
    * set_dbEST3ID() - set dbEST3 ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param dbEST3 ID to set
    * @return true if succeed
    */
   public final boolean set_dbEST3ID(int mid, String dbEST3)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].dbEST3= dbEST3;
     return(true);
   }
   
   
   /**
    * set_dbEST5ID() - set dbEST5 ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param dbEST5 ID to set
    * @return true if succeed
    */
   public final boolean set_dbEST5ID(int mid, String dbEST5)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].dbEST5= dbEST5;
     return(true);
   }
   
   
   /**
    * set_LocusID() - set LocusLinkID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param locusLinkID to set
    * @return true if succeed
    */
   public final boolean set_LocusID(int mid, String locusLinkID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].LocusID= locusLinkID;
     return(true);
   }
   
   
   /**
    * set_SwissProtID() - set SwissProt ID for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param swissProtID to set
    * @return true if succeed
    */
   public final boolean set_SwissProtID(int mid, String swissProtID)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].SwissProt= swissProtID;
     return(true);
   }
   
   
   /**
    * set_plate() - set original plate for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param plate to set
    * @return true if succeed
    */
   public final boolean set_plate(int mid, String plate)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].plate= plate;
     return(true);
   }
   
   
   /**
    * set_plate_row() - set original plate_row for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param plateRow to set
    * @return true if succeed
    */
   public final boolean set_plate_row(int mid, String plateRow)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].plate_row= plateRow;
     return(true);
   }
   
   
   /**
    * set_plate_col) - set original plate_col for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param plateGrid to set
    * @return true if succeed
    */
   public final boolean set_plate_col(int mid, String plateCol)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].plate_col= plateCol;
     return(true);
   }
   
    
   /**
    * set_pValue() - set pValue for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param pValue to set
    * @return true if succeed
    */
   public final boolean set_pValue(int mid, float pValue)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].pValue= pValue;
     return(true);
   }
   
   
   /**
    * set_clusterNodeNbr() - set clusterNodeNbr for specified gene, if exists
    * The cluster node # is the cluster number that a gene belongs to.
    * @param mid is master gene index of gene to modify
    * @param clusterNodeNbr to set
    * @return true if succeed
    */
   public final boolean set_clusterNodeNbr(int mid, int clusterNodeNbr)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].clusterNodeNbr= clusterNodeNbr;
     return(true);
   }
   
   
   /**
    * set_nGeneClustersCnt() - set nGeneClustersCnt for specified gene, if exists
    * The centroid gene of a cluster is flagged by containing the # of genes
    * in that cluster.
    * @param mid is master gene index of gene to modify
    * @param nGeneClustersCnt to set
    * @return true if succeed
    */
   public final boolean set_nGeneClustersCnt(int mid, int nGeneClustersCnt)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].nGeneClustersCnt= nGeneClustersCnt;
     return(true);
   }
   
   
   /**
    * set_properties() - set properties for specified gene, if exists
    * @param mid is master gene index of gene to modify
    * @param genePropertiesBits to set
    * @return true if succeed
    */
   public final boolean set_properties(int mid, int genePropertiesBits)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].properties= genePropertiesBits;
     return(true);
   }
   
   
   /**
    * set_nDuplGenes() - # duplicate genes in duplMIDlist[] for specified gene,
    * if exists
    * @param mid is master gene index of gene to modify
    * @param nbrDuplGenes to set
    * @return true if succeed
    */
   public final boolean set_nDuplGenes(int mid, int nbrDuplGenes)
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].nMid= nbrDuplGenes;
     return(true);
   }
   
   
   /**
    * set_duplMIDlist() - set list of MIDs for duplicate genes for specified gene,
    * if exists
    * @param mid is master gene index of gene to modify
    * @param midListOfDuplicates to set
    * @return true if succeed
    */
   public final boolean set_duplMIDlist(int mid, int midListOfDuplicates[])
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].midList= midListOfDuplicates;
     return(true);
   }
   
   
   /**
    * set_nGenomicID() - set # additional genomic IDs in the GenomicID[] list
    * for the specified gene, if exists.
    * @param mid is master gene index of gene to modify
    * @param nGenomicID list to set
    * @return true if succeed
    */
   public final boolean set_nGenomicID(int mid, int nGenomicID[])
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].nGenomicID= nGenomicID;
     return(true);
   }
   
   
   /**
    * set_GenomicID() - seta list of additional genomic IDs for the default
    * gene, if exists.
    * @param mid is master gene index of gene to modify
    * @param genomicIDs to set
    * @return true if succeed
    */
   public final boolean set_GenomicID(int mid, String genomicIDs[])
   { 
     if(mid<0 || mid>=map.maxGenes && map.midStaticCL.mList[mid]!=null)
       return(false);
     map.midStaticCL.mList[mid].GenomicID= genomicIDs;
     return(true);
   }  
   
      
   /* ----------------------- Get Current Gene ------------------------ */
   
   /**
    * setGeneMIDfromXYcoord() - set current gene MID from XY coordinates
    * for a spot in the pseudoarray image for that sample.
    * @param sampleNbr sample number data to access
    * @param xCoord x coordinate of putative gene.
    * @param yCoord y coordinate of putative gene
    * @return <code>mid</code> if found else <code>-1</code>
    * if not found or error.
    */
   public final int setGeneMIDfromXYcoord(int sampleNbr,
   int xCoord, int yCoord)
   { /* setGeneMIDfromXYcoord */
     if(sampleNbr<=0 || sampleNbr>hps.nHP)
       return(-1);
     
     MaHybridSample  msW= hps.msList[sampleNbr];
     if(msW==null)
       return(-1);
     Point xy= new Point(xCoord, yCoord);
     int mid= msW.lookupGeneFromXY(xy);
     
     return(mid);
   } /* setGeneMIDfromXYcoord */
   
   
   /**
    * setCurrentGeneFromMID() - set the Current Gene to the specified mid.
    * Note: this does not set the default gene that you use for getting gene
    * data with this MJAgene API.
    * @param mid master gene index
    */
   public final void setCurrentGeneFromMID(int mid)
   {  cdb.setObjCoordFromMID(mid, null); }
   
   
   /**
    * isCurGeneValid() - test if mid is a valid gene for the current gene.
    * @return true if it is a valid gene
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final boolean isCurGeneValid()
   { return(cdb.isValidObjFlag); }
   
   
   /**
    * getCurGeneMID() - get the MID (Master Gene ID) number for current gene
    * @return MID, if not valid then return -1
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getCurGeneMID()
   { return(cdb.objMID); }
   
   
   /**
    * getGridIDnbr() - get the GID (Grid layout ID) number for current gene
    * @return GID, if not valid then return -1
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getGridIDnbr()
   { return(cdb.objGID); }
   
   
   /**
    * getCurGeneGIDG() - get the GIDG (Ganged Grid layout ID) number for
    * current gene
    * @return GIDG, if not valid then return -1
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getCurGeneGIDG()
   { return(cdb.objGIDG); }
   
   
   /**
    * getCurGeneField() - get the MID (Master Gene ID) number for current gene
    * @return value, it is -1 if not valid
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getCurGeneField()
   { return(cdb.objField); }
   
   
   /**
    * getCurGeneGrid() - get the grid for current gene
    * @return value, it is -1 if not valid
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getCurGeneGrid()
   { return(cdb.objGrid); }
   
   
   /**
    * getCurGeneRow() - get the rowin the grid for current gene
    * @return value, it is -1 if not valid
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getCurGeneRow()
   { return(cdb.objRow); }
   
   
   /**
    * getCurGeneCol() - get the column in the grid for current gene
    * @return value, it is -1 if not valid
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getCurGeneCol()
   { return(cdb.objCol); }
   
   
   /**
    * getCurGeneXcoord() - get the X coordinate in the pseudoarray for
    * current gene
    * @return value, it is -1 if not valid
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getCurGeneXcoord()
   { return(cdb.objX); }
   
   
   /**
    * getCurGeneYcoord() - get the Y coordinate in the pseudoarray for
    * current gene
    * @return value, it is -1 if not valid
    * @see #setGeneMIDfromXYcoord
    * @see #setCurrentGeneFromMID
    */
   public final int getCurGeneYcoord()
   { return(cdb.objY); }
   
   
   /* -------- Data from gene or spot  for specific sample --------- */
   
   /**
    * setCurrentSample() - set the sample to be used for subsequent
    * gene or spot data accesses in that array
    * @param sampleNbr sample number data to access
    * @return true if sample exists
    */
   public final boolean setCurrentSample(int sampleNbr)
   { /* setCurrentSample */
     msW= chkGetHP(sampleNbr);
     if(msW==null)
       return(false);
     return(true);
   } /* setCurrentSample */
   
   
   /**
    * getRawDataByGID() - return spot intensity data specified by gid
    * (Grid Index ID) and data type.
    * Note: this may get the F1 and F2 data, its mean or its ratio.
    * Use the sample number last specified.  The current sample
    * is set by setCurrentSample().
    * @param gid is the GeneID GID for the spot
    * @param type is access method:
    *  <OL>
    *  <LI>DATA_MEAN_F1F2TOT - (g1+g2)/2
    *  <LI>DATA_RATIO_F1F2TOT -  g1/g2
    *  <LI>DATA_F1TOT - g1
    *  <LI>DATA_F2TOT - g2
    *</OL>
    * @return raw intensity value for this sample. If no sample is defined,
    * return 0.0F.
    * @see #setCurrentSample
    */
   public final float getRawDataByGID(int gid, int type)
   { /* getRawBackgroundDataByGID */
     if(msW==null)
       return(0.0F);
     float value= msW.getRawDataByGID(gid, false, type);
     return(value);
   } /* getRawDataByGID */
   
   
   /**
    * getRawBackgroundDataByGID() - return spot raw background intensity
    * data specified by gid (Grid Index ID) and data type.
    * Note: this may get the F1 and F2 data, its mean or its ratio.
    * Use the sample number last specified.  If no sample is defined,
    * return 0.0F.
    * @param gid is the GeneID GID for the spot
    * @param type is access method:
    *  <OL>
    *  <LI>DATA_MEAN_F1F2TOT - (g1Bkg+g2Bkg)/2
    *  <LI>DATA_RATIO_F1F2TOT -  g1/g2
    *  <LI>DATA_F1TOT - g1
    *  <LI>DATA_F2TOT - g2
    *</OL>
    * @return raw intensity value for this sample. If no sample is defined,
    * return 0.0F.
    */
   public final float getRawBackgroundDataByGID(int gid, int type)
   { /* getRawBackgroundDataByGID */
     if(msW==null)
       return(0.0F);
     float value= msW.getRawDataByGID(gid, false, type);
     return(value);
   } /* getRawBackgroundDataByGID */
   
   
   /**
    * getRawIntensData() - get spot raw (Intensity - Background) data
    * specified by gid (Grid Index ID) and data type.
    *<PRE>
    * If background subtraction is enabled then
    *   I1= (signal1 - bkgrdQ1)and I2= (signal2 - bkgrdQ2).
    * else
    *   I1= signal1, and I2= signal2.
    * If using ratio data, then return ratio (I1/I2) else I1.
    * If using ratioMedianScaling, then also multiply ratio data
    * by scaling factor (ratioMedianScale if not subtracting
    * background and by ratioMedianBkgdScale if we are subtracting
    * background).
    *</PRE>
    * @param gid is the GeneID GID for the spot
    * @param useF1F2ratioFlag then return ratio of F1/F2 else F1.
    * @return raw intensity value for this sample. If no sample is defined,
    *         return 0.0F.
    * @see #setCurrentSample
    */
   public final float getRawIntensData(int gid, boolean useF1F2ratioFlag)
   { /* getRawIntensData */
     if(msW==null)
       return(0.0F);
     float value= msW.getRawIntens(gid, useF1F2ratioFlag);
     return(value);
   } /* getRawIntensData */
   
   
   /**
    * scaleIntensData() - scale raw intensity data as fct of normalization mode.
    * This is called on a per-spot basis.
    * @param dataI is the raw intensity
    * @return the scaled intensity
    * @see #setCurrentSample
    */
   public final float scaleIntensData(float dataI)
   { /* scaleIntensData */
     if(msW==null)
       return(0.0F);
     float value= msW.scaleIntensData(dataI);
     return(value);
   } /* scaleIntensData */
   
   
   /**
    * scaleIntensData() - scale raw intensity data as fct of normalization mode.
    * This is called on a per-spot basis using the spot gid to aid in the normalization.
    * @param dataI is the raw intensity
    * @param gid is GID of the spot
    * @return the scaled intensity
    * @see #setCurrentSample
    */
   public final float scaleIntensData(float dataI, int gid)
   { /* scaleIntensData */
     if(msW==null)
       return(0.0F);
     float value= msW.scaleIntensData(dataI, gid);
     return(value);
   } /* scaleIntensData */
   
   
   /**
    * getScaledSpotData() - get scaled spot (Intensity - Background) data
    * specified by gid (Grid Index ID) and data type. for this gid.
    * This method first calls getRawIntensData() to get the raw data
    * and then scales it by calling scaleIntensData().
    * It also saves the data in MJAgene data as well.
    * @param dataI is the raw intensity
    * @param useF1F2ratioFlag then return ratio of F1/F2 else F1.
    * @return the scaled intensity,else 0.0F if there is a problem.
    * @see #setCurrentSample
    * @see #getRawIntensData
    * @see #scaleIntensData
    */
   public final float getScaledSpotData(int gid, boolean useF1F2ratioFlag)
   { /* getScaledSpotData */
     if(msW==null)
       return(0.0F);
     float
       dataBcRI= msW.getRawIntens(gid, useF1F2ratioFlag),
       data= msW.scaleIntensData(dataBcRI, gid);
     if(gene!=null)
       gene.data= data;
     return(data);
   } /* getScaledSpotData */
   
   
   /**
    * getScaledSpotData1() - get scaled spot (Intensity - Background) data1
    * specified by gid (Grid Index ID) and data type. for this gid.
    * This method first calls getRawIntensData() to get the raw data
    * and then scales it by calling scaleIntensData().
    * It also saves the data in MJAgene data1 as well.
    * @param dataI is the raw intensity
    * @param useF1F2ratioFlag then return ratio of F1/F2 else F1.
    * @return the scaled intensity,else 0.0F if there is a problem.
    * @see #setCurrentSample
    * @see #getRawIntensData
    * @see #scaleIntensData
    */
   public final float getScaledSpotData1(int gid, boolean useF1F2ratioFlag)
   { /* getScaledSpotData1 */
     if(msW==null)
       return(0.0F);
     float
       dataBcRI= msW.getRawIntens(gid, useF1F2ratioFlag),
       data1= msW.scaleIntensData(dataBcRI, gid);
     if(gene!=null)
       gene.data1= data1;
     return(data1);
   } /* getScaledSpotData1 */
   
   
   /**
    * getScaledSpotData2() - get scaled spot (Intensity - Background) data2
    * specified by gid (Grid Index ID) and data type. for this gid.
    * This method first calls getRawIntensData() to get the raw data
    * and then scales it by calling scaleIntensData().
    * It also saves the data in MJAgene data2 as well.
    * @param dataI is the raw intensity
    * @param useF1F2ratioFlag then return ratio of F1/F2 else F1.
    * @return the scaled intensity,else 0.0F if there is a problem.
    * @see #setCurrentSample
    * @see #getRawIntensData
    * @see #scaleIntensData
    */
   public final float getScaledSpotData2(int gid, boolean useF1F2ratioFlag)
   { /* getScaledSpotData2 */
     if(msW==null)
       return(0.0F);
     float
       dataBcRI= msW.getRawIntens(gid, useF1F2ratioFlag),
       data2= msW.scaleIntensData(dataBcRI, gid);
     gene.data2= data2;
     return(data2);
   } /* getScaledSpotData2 */
   
   
   /**
    * computeMinMaxF1F2Data() - compute F1 F2 min and max raw intensity extrema
    * returned as a float[] list for the current sample.
    * @param useF1F2ratioFlag then compute (minData,maxData) of the ratio F1/F2
    *        else compute (minDataF1, minDataF2, maxDataF1, maxDataF2) for
    *        the separate F1 F2 channels.
    * @param useAllGenesFlag compute it on all genes if true, else just
    *        on genes in the data Filtered working gene list.
    * @return list (minDataF1, minDataF2, maxDataF1, maxDataF2), else null if
    *        there is a problem.
    */
   public final float[] computeMinMaxF1F2Data(boolean useF1F2ratioFlag,
                                              boolean useAllGenesFlag)
   { /* computeMinMaxF1F2Data */
     if(msW==null ||
     msW.computeMinMaxF1F2Data(useF1F2ratioFlag, useAllGenesFlag))
       return(null);
     float minMaxF1F2List[] = { msW.minDataF1, msW.minDataF2, msW.maxDataF1,
                                msW.maxDataF2
                              };
     return(minMaxF1F2List);
   } /* computeMinMaxF1F2Data*/
   
   
   /**
    * getGeneMeasurementSummary() - get spot quantitation summary of (gene,sample).
    * This reports a 1-line measurement summary
    * The format depends on the normalization, single or multiple X, Y sets
    * pseudoarray image mode, clustering, etc.
    * See the Reference Manual for examples.
    * @param sampleNbr if 0, then use current HP sample
    * @param gid Grid Index (GID). If -1, use the current Gene if defined.
    * @return string report if current gene (spot) defined else ""
    */
   public final static String getGeneMeasurementSummary(int sampleNbr, int gid)
   { /* getGeneMeasurementSummary */
     if(sampleNbr<=0 || gid==-1)
       return(SpotFeatures.getCurGeneXYGdata());
     
     MaHybridSample ms= chkGetHP(sampleNbr);
     if(ms==null || gid>map.maxSpots)
       return("");
     String summary= mae.sf.getSpotMeasurementStr(gid, ms);
     return(summary);
   } /* getGeneMeasurementSummary */
   
   
   /**
    * getGeneFeatureLine() - get spot feature summary of gene.
    * The 1-line format format depends on GIPO and other data available
    * for the gene.
    * See the Reference Manual for examples.
    * @param gid Grid Index (GID). If -1, use the current Gene if defined.
    * @return string report if valid spot else ""
    */
   public final static String getGeneFeatureLine(int gid)
   { /* getGeneFeatureLine */
     if(gid==-1)
       return(SpotFeatures.getCurGeneFeaturedata());
     if(gid>map.maxSpots)
       return("");
     String featuresStr= mae.sf.getSpotFeaturesLine(gid);
     return(featuresStr);
   } /* getGeneFeatureLine */
   
   
   /**
    * getGeneGenomicLine() - get spot genomic data summary of gene.
    * The 1-line format depends on GIPO and other data available
    * for the gene. See Reference Manual for examples.
    * @param gid Grid Index (GID). If -1, use the current Gene if defined.
    * See the Reference Manual for examples.
    * @return string report if valid spot else ""
    */
   public final static String getGeneGenomicLine(int gid)
   { /* getGeneGenomicLine */
     if(gid==-1)
       return(SpotFeatures.getCurGeneGenomicdata());
     
     if(gid>map.maxSpots)
       return("");
     String genomicDataStr= mae.sf.getSpotGenomicLine(gid);
     return(genomicDataStr);
   } /* getGeneGenomicLine */
   
   
   /* ----- Lookup MID and MID lists by various genomic identifiers and names --- */
   
   /**
    * lookupMIDfromGeneName() - lookup mid from Gene Name.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   public final int lookupMIDfromGeneName(String name)
   { return(map.lookupMIDfromGeneName(name)); }
   
   
   /**
    * lookupMIDlistFromGeneName() - lookup mid list from Gene Name.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   public final int[] lookupMIDListFromGeneName(String name)
   { return(map.lookupMIDListFromGeneName(name)); }
   
   /**
    * lookupMIDfromCloneID() - lookup mid from CloneID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   public final int lookupMIDfromCloneID(String name)
   { return(map.lookupMIDfromCloneID(name)); }
   
   
   /**
    * lookupMIDlistFromCloneID() - lookup mid list from CloneID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   public final int[] lookupMIDListFromCloneID(String name)
   { return(map.lookupMIDListFromCloneID(name)); }
   
   
   /**
    * lookupMIDfromUniGeneID() - lookup mid from UniGene ID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   public final int lookupMIDfromUniGeneID(String name)
   { return(map.lookupMIDfromUniGeneID(name)); }
   
   
   /**
    * lookupMIDlistFromUniGeneID() - lookup mid list from UniGene ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   public final int[] lookupMIDListFromUniGeneID(String name)
   { return(map.lookupMIDListFromUniGeneID(name)); }
   
   
   /**
    * lookupMIDfromRefSeqID() - lookup mid from RefSeq ID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   public final int lookupMIDfromRefSeqID(String name)
   { return(map.lookupMIDfromRefSeqID(name)); }
   
   
   /**
    * lookupMIDlistFromRefSeqID() - lookup mid list from RefSeq ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   public final int[] lookupMIDListFromRefSeqID(String name)
   { return(map.lookupMIDListFromRefSeqID(name)); }
   
   
   /**
    * lookupMIDfromLocusID() - lookup mid from Locus ID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   public final int lookupMIDfromLocusID(String name)
   { return(map.lookupMIDfromLocusID(name)); }
   
   
   /**
    * lookupMIDlistFromLocusID() - lookup mid list from Locus ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDListFromLocusID(String name)
   { return(map.lookupMIDListFromLocusID(name)); }
   
   
   /**
    * lookupMIDfromSwissProtID() - lookup mid from SwissProt ID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   public final int lookupMIDfromSwissProtID(String name)
   { return(map.lookupMIDfromSwissProtID(name)); }
   
   
   /**
    * lookupMIDlistFromSwissProtID() - lookup mid list from SwissProt ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   public final int[] lookupMIDListFromSwissProtID(String name)
   { return(map.lookupMIDListFromSwissProtID(name)); }
   
   
   /**
    * lookupMIDfromGenBankACC() - lookup mid from GenBank ACC ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   public final int lookupMIDfromGenBankACC(String name)
   { return(map.lookupMIDfromGenBankACC(name)); }
   
   
   /**
    * lookupMIDlistFromGenBankACC() - lookup mid list from GenBank ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   public final int[] lookupMIDlistFromGenBankACC(String name)
   { return(map.lookupMIDlistFromGenBankACC(name)); }
   
   
   /**
    * lookupMIDfrom_dbEST() - lookup mid from dbEST 3' or 5' ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   public final int lookupMIDfrom_dbEST(String name)
   { return(map.lookupMIDfrom_dbEST(name)); }
   
   
   /**
    * lookupMIDlistFrom_dbEST() - lookup mid list from dbEST 3' or 5' ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   public final int[] lookupMIDlistFrom_dbEST(String name)
   { return(map.lookupMIDlistFrom_dbEST(name)); }
   
   
   
} /* end of class MJAgene */

