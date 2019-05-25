/* File: Gene.java */

/**
 * Gene is the base class used to define a single gene (clone or oligo) data structure.
 * It consists of sample-specific data fields and sample independent
 * genomic identifiers and name fields. The latter is represented by the 
 * Master Gene ID (MID) which is unique for any number of spots for that gene
 * and the Grid coordinate ID (GID) which corresponds to a particular spot for that
 * gene.
 *<P>
 * Genomic IDs include: Clone_ID, GenBankAcc, GenBankAcc3, GenBankAcc5,
 *   Unigene_ID, dbEST3, dbEST5, SwissProt, RefSeqID, LocusID.<BR>
 * The Master_ID is set to one of these. The arrays GenomicID[] and nGenomicID[]
 * may be used for specifying external identifiers for particular user databases.
 *<P>
 * Gene names include: Gene_Name, UGclusterName.<BR>
 * The MasterGeneName is set to one of these.
 *<P>
 * Quantified data can be temporarily stored in the gene in the
 * (data, data1, data2, pValue, geneDist, etc.) variable. Generally, F1 (Cy3) is data1,
 * F2 (Cy5) is data2 and F1/F2 or Cy3/Cy5 is data.
 *<P>
 * Additional identifiers include: Gene_Class, plate, plate_row, plate_col
 *<P>
 * Each gene has various properties indicated the inclusive or of the C_xxxx constants.
 * These include: C_IS_KMEANS, C_GOOD_MID, C_BAD_MID, C_DUP_SPOT, C_BAD_SPOT, C_IMAGE_ID,
 *  C_ATCC_ID, C_USE_GBID_FOR_CLONEID, C_MARGINAL_SPOT, C_BAD_SPOT_GEOMETRY,
 *  C_BAD_LOCAL_SPOT_BKGRD, C_LOW_SPOT_REF_SIGNAL, C_IS_EGL_GENE, C_IS_NOT_FILTERED, 
 *  C_IS_CUR_GENE
 *<P>
 * The midList[0:nMid-1] is the list of other Genes pointed to by the 
 * mae.mp.midStaticCL[].mList[] that are in fact the same gene in the 
 * database and can treated as replicates for various computations.
 *
 *<P>
 * Note: because we may have duplicate spots F1 and F2, there may be 2 
 * spots that represent the same Gene instance. However, they BOTH have 
 * the same mid. Therefore, we provide data slots (data1, data2) for both 
 * fields 1 and 2. We can call GridCoords.getGICcomplement(gid) to lookup the
 * other gidGang index.
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
 * @version $Date: 2003/11/24 21:18:54 $   $Revision: 1.11 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

import java.util.Vector;

class Gene
{ 
    
 /* [TODO] generalize this for variable number of GIDs/gene.
 * Note: maybe have Gene list which maps genes with the same string
 * name to different Gene objects, etc. 
 */
    
  /** link to global MAExplorer instance */
  private static MAExplorer
    mae;                   
  /** link to global Maps instance */
  private static Maps
    map;                      
  /** link to global Config instance */
  private static Config
    cfg;                 
    
  /* --- Special bit pattern features of a gene can have 32 bits.
   * Note: spot quality is binary (i.e. good or not good) ---
   */
  /** Gene property: Is a K-means node gene */ 
  final static int
    C_IS_KMEANS= 1;                
  /** Gene property: the gene is good, good gene name. NCI/CIT mAdb: "Good" or NULL */ 
  final static int
    C_GOOD_MID= 2;
  /** Gene property: the gene is bad, good gene name */ 
  final static int
    C_BAD_MID= 4;
  /** Gene property: is duplicate of another gene on array */   
  final static int
    C_DUP_SPOT= 16;                
  /** Gene property: non-analyzable gene (eg. marker, etc). NCI/CIT mAdb: "Not Found", "Empty" */ 
  final static int
    C_BAD_SPOT= 8;
  /** Gene property: Clone is an I.M.A.G.E. or numbered clone */ 
  final static int
    C_IMAGE_ID= 32;                
  /** Gene property: Clone is an ATCC numbered clone */
  final static int   
    C_ATCC_ID= 64;                
  /** Gene property: use GenBank ID for Clone ID */
  final static int
    C_USE_GBID_FOR_CLONEID= 128;   
  /** Gene property: Marginal spot */
  final static int
    C_MARGINAL_SPOT= 256;          
  /** Gene property: bad spot geometry */
  final static int
    C_BAD_SPOT_GEOMETRY= 512;      
  /** Gene property: bad local spot background */
  final static int
    C_BAD_LOCAL_SPOT_BKGRD= 1024;  
  /** Gene property: low spot reference signal */
  final static int
    C_LOW_SPOT_REF_SIGNAL= 2048;    
  /** Gene property: is an E.G. L. gene (not normally used) */
  final static int
    C_IS_EGL_GENE= 4096;      
  /** Gene property: is a gene that did pass data Filter  */
  final static int
    C_IS_FILTERED= 8192; 
  /** Gene property: is a gene that did not pass data Filter  */
  final static int
    C_IS_NOT_FILTERED= 16384;         
  /** Gene property: is Current Gene (not normally used) */
  final static int
    C_IS_CUR_GENE= 32768;  
  /** property: gene has bad data */
  final static int
    C_BAD_DATA= 65536;    
   
  /** Master gene ID index # (MID). It isvalid for BOTH F1 & F2 spots.
   * [NOTE: this used to be cid (MID)] in v0.89.41
   */
  int
    mid= -1;
    
  /** a Grid Identifier (GID) index index corresponding to MasterID. A value of -1
   * indicates the gid is invalid. Note: the gids are created when the 
   * GIPO database is read, but may be wrong if the mae.mp.gid2mid[] is not setup yet!
  */
  int            
    gid= -1;              
  
    // /** # of GIDs for this gene */
    // int
    //   nGid;    
    // /** all gids for this duplicated Gene [0:nGid-1] */   
    // int      
    //  gidList[];       
  /** # of genes in midList[] for this gene */
  int 
    nMid= 0;
  /** list of all mids for duplicats of this Gene [0:nMid-1] that occurs more than 2X in
   * the in GIPO
   */
  int            
    midList[]= null;      
  
  /* --- The use of Master Gene Identifier and Master Gene Name based on what data is 
   * available in the GIPO file ([NOTE] as of V.0.90.01) . Previously the GID was used
   * and Gene_Name was required and cludge code was used to define these entities. ---
   */
  /** Master Gene Identifier (i.e. MID) will be one of IDs below */
  String
    Master_ID= null;
  /** Master Gene Name will be one of the gene names below ([NOTE] as of V.0.90.01) */
  String
    MasterGeneName= null; 
    
  /** opt. gene name if it exists */
  String
    Gene_Name= null;      
  /** opt. UniGene cluster name if it exists */
  String
    UGclusterName= null;  
 
  /** Generic Identifier  - "if all else fails" the use may use some arbitrary unique ID */
  String
    Generic_ID= null;
  /** I.M.A.G.E clone id #, or "your plate", etc. if it exists */
  String
    Clone_ID= null;
  /** NCBI GenBankAcc id if it exists */
  String
    GenBankAcc= null;
  /** NCBI GenBankAcc 3' id if it exists */
  String
    GenBankAcc3= null;
  /** NCBI GenBankAcc 5' id if it exists */
  String
    GenBankAcc5= null;
  /** NCBI Unigene cluster  if it exists */
  String
    Unigene_ID= null;
  /** NCBI OMIM ID  if it exists */
  String
    OmimID= null;
  /** NCBI dbEST database 3' id */
  String
    dbEST3= null;
  /** NCBI dbEST database 5' id */
  String
    dbEST5= null;
  /** Swiss-Prot ID if it exists */
  String
    SwissProt= null;
  /** NCBI RefSeq ID if it exists */
  String
    RefSeqID= null;
  /** NCBI LocusLink LocusID if it exists */
  String
    LocusID= null;
  
  /** (Optional)
   * External Genomic IDs values if specified via the
   * MaeConfig.txt or .mae startup files.
   * These values IDs are only for genomic database
   * NOT found in the default Gene ID names.
   * The ID value data for each Gene object is obtained from 
   * the GIPO file data for this gene. 
   * The corresponding names of the Genomic databases are
   * for the i'throws enty is cfg.sGenomicMenu[nGenomicID[i]].
   */
  String
    GenomicID[]= null;
  /**  (Optional)
   * If generic IDs are used this will contain the INDEX
   * of the cfg.sGenomicMenu[] entry. THAT is the name of the
   * external Genomic database.
   */
  int
    nGenomicID[]= null;    

  
  /** --- options  may or may not be used --- */
  String
    comments= null;
  /** opt. current Gene_Class (may be in several...) 
   * [TODO] may want to replace with byte[] or short[] list of GeneClass indices...
   */
  String
    Gene_Class= null;

  /** plate where clone was from */
  String
    plate= null;
  /** plate where clone was from */
  String
    plate_row= null;
  /** plate where clone was from */
  String
    plate_col= null; 
  
  ///* [Future] data structures for use with Protein Arrays*/
  ///** [Future] opt. Swiss-Prot id [polypeptideNbr] */ 
  //String
  //  swissProtID[]= null; 
  // /** [Future] opt. polypeptide id [polypeptideNbr] */     
  //String
  //  polypeptideID[]= null;
  // /** [Future] opt. epitope map id [polypeptideNbr][epitopeNbr] */
  //String
  //  epitopeID[][]= null;      
 
  // /** [FUTURE] #of polypeptides # */
  //int
  //  polypeptideNbr= 0;
  // /** [FUTURE]  # of epitopdes for each polypeptide. */
  //int
  //  epitopeNbr[]= null;        
    
  //  /** original data [0:nHP-1][0:nGID-1] */
  //String
  //  origDataV[][]= null;    
  // /** normalized data [0:nHP-1][0:nGID-1] */
  //String  
  //  normDataV[][]= null;     
  // /** Good-spot (i.e. C_BAD_SPOT data from the .quant files on a per spot basis [0:nHP-1][0:nGID-1] */ 
  //boolean
  //  spotFilterFlag[][]= null; 
  
  /* [FUTURE] */
  /** [0:nHP-1][0:nGID-1] for use on per spot GOOD gene filter. eg. C_BAD_SPOT etc. */ 
  //boolean
  //  spotFilter[][];    
  
  /** dynamic generic data such as ratio, etc associated with this gene */  
  float
    data= 0.0F;         
  /** dynamic generic data for Field 1 associated with this gene */
  float
    data1= 0.0F;          
  /** dynamic generic data for Field 2 associated with this gene */
  float
    data2= 0.0F;          
  /** optional computed p-value if doing statistical test */   
  float
    pValue= 0.0F;       
  /** optional computed coefficient of variation (CV) if doing statistical test */   
  float
    cv= 0.0F;            
  /** Opt. distance from this gene to current Gene */
  float
    geneDist= 0.0F;       
  /** if >0, then this the cluster node in K-means clustering */
  int
    clusterNodeNbr= 0;    
  /** # of genes in cluster at cluster distance threshold. This is 0
   * for genes which are NOT cluster nodes.
   */ 
  int
    nGeneClustersCnt= 0; 
  /** properties of the gene defined by one or more C_xxxx bits  */
  int
    properties= 0;        
  
  	
		      
  /**
   * Gene() - constructor to set up global mae instance - MUST BE CALLED FIRST!!!
   * Note: we only need the mae instance to setup the map instance.
   * @param maE is instance of MAExplorer
   */
  Gene(MAExplorer maE)
  { /* Gene */
    mae= maE;      /* set up static */
    map= mae.mp;   /* setup map */
    cfg= mae.cfg;
  } /* Gene */
  
  
  /**
   * Gene() - constructor with gid, mid and no quantified data.
   * Data will be stuffed later with "gene.XXXX= XXXX" etc.
   */
  Gene(int gid, int mid)
  { /* Gene */
    this.gid= gid;
    this.mid= mid;
  } /*Gene */
  
  
  /**
   * assignMasterID() - assign Master_ID using masterIDmode using existing data.
   * @param masterIDmode is current mode of the genomic ID assigned as master ID
   */
  void assignMasterID(int masterIDmode)
  { /* assignMasterID */
    if(masterIDmode==mae.MASTER_CLONE_ID)
      Master_ID= Clone_ID;
    else if(masterIDmode==mae.MASTER_GENBANK)
      Master_ID= GenBankAcc;
    else if(masterIDmode==mae.MASTER_GENBANK5)
      Master_ID= GenBankAcc5;
    else if(masterIDmode==mae.MASTER_GENBANK3)
      Master_ID= GenBankAcc3;
    else if(masterIDmode==mae.MASTER_UG_ID)
      Master_ID= Unigene_ID;
    else if(masterIDmode==mae.MASTER_DBEST5)
      Master_ID= dbEST5;
    else if(masterIDmode==mae.MASTER_DBEST3)
      Master_ID= dbEST3;
    else if(masterIDmode==mae.MASTER_SWISS_PROT)
      Master_ID= SwissProt;
    else if(masterIDmode==mae.MASTER_LOCUSLINK)
      Master_ID= LocusID;
    else if(masterIDmode==mae.MASTER_OMIM)
      Master_ID= OmimID;
    else if(masterIDmode==mae.MASTER_GENERIC_ID)
      Master_ID= Generic_ID;
    else Master_ID= "";          /* NONE! */
    
    /* [TODO] do we want to be able to assign the Master-ID
     * to external GenomicMenu[] ids?
     */
  } /* assignMasterID */
  
  
  /**
   * lookupIDbyName() - lookup the genomic gene id value given the ID name.
   * ID names include: "Clone ID", "GenBank", "GenBank 5'", "GenBank 3'",
   * "UniGene ID", "dbEST 5'", "dbEST 3'", "SwissProt", "LocusID". In addition
   * the GenomicID[] list can be used.
   * @param idName is the gene name used to lookup the ID value
   * @return the ID value or null if bad ID name.
   * @see GipoTable#lookupFieldIdx
   */
  String lookupIDbyName(String idName)
  { /* lookupIDbyName */
    String id= null;                 /* failed */
    
    if(idName.equalsIgnoreCase("Clone ID"))
      id= Clone_ID;
    else if(idName.equalsIgnoreCase("GenBank"))
      id= GenBankAcc;
    else if(idName.equalsIgnoreCase("GenBank 5'"))
      id= GenBankAcc5;
    else if(idName.equalsIgnoreCase("GenBank 3'"))
      id= GenBankAcc3;
    else if(idName.equalsIgnoreCase("UniGene ID"))
      id= Unigene_ID;
    else if(idName.equalsIgnoreCase("dbEST 5'"))
      id= dbEST5;
    else if(idName.equalsIgnoreCase("dbEST 3'"))
      id= dbEST3;
    else if(idName.equalsIgnoreCase("SwissProt"))
      id= SwissProt;
    else if(idName.equalsIgnoreCase("LocusID"))
      id= LocusID;
    else if(idName.equalsIgnoreCase("OmimID"))
      id= OmimID;
    
    else if(GenomicID!=null)
    { /* Search for external DB names */
      for(int i=0; i<cfg.nGenomicMenus;i++)
        if(idName.equalsIgnoreCase(cfg.sGenomicMenu[nGenomicID[i]]))
        { /* found it */
          String GenomicIDname= GenomicID[i];
                 /* Now used this to lookup the data for this id in
                  * the GIPO table.
                  */
          int
            idxGipoIDcol= mae.gipo.lookupFieldIdx(GenomicIDname),
            gipoRow= mae.gipo.gid2GipoTableRow[gid];
                 /* Lookup data from idxGipoID column for
                  * row corresponding to this gene.
                  */
          if(gipoRow!=-1 && idxGipoIDcol!=-1)
            id= mae.gipo.tData[gipoRow][idxGipoIDcol];
          else
            id= "";               /* No data */
          break;
        }
    } /* Search for external DB names */
    
    return(id);
  }/* lookupIDbyName */
  
  
  /**
   * assignMasterGeneName() - assign MasterGeneName using masterGeneNameMode
   * for current database configuration.
   * @param masterGeneNameMode for this gene
   */
  void assignMasterGeneName(int masterGeneNameMode)
  { /* assignMasterGeneName */
    if(masterGeneNameMode==mae.MASTER_GENE_NAME)
      MasterGeneName= Gene_Name;
    else if(masterGeneNameMode==mae.MASTER_UG_NAME)
      MasterGeneName= UGclusterName;
  } /* assignMasterGeneName */
  
  
  /**
   * setGeneData() - set gene quantified data
   * @param data is computed data for this gene
   */
  void setGeneData(float data)
  { /* setGeneData */
    this.data= data;
  } /* setGeneData */
  
  
  /**
   * setGeneData() - set quantified gene data, data1 and data2
   * @param data is computed data for this gene
   * @param data1 is data for channel 1 for this gene
   * @param data2 is data for channel 2 for this gene
   */
  void setGeneData(float data, float data1, float data2)
  { /* setGeneData */
    this.data= data;
    this.data1= data1;
    this.data2= data2;
  } /* setGeneData */
  
  
  /**
   * setGeneData() - set quantified gene data, data1 and data2
   * @param data is computed data for this gene
   * @param data1 is data for channel 1 for this gene
   * @param data2 is data for channel 2 for this gene
   * @param pValue data for this gene
   */
  void setGeneData(float data, float data1, float data2, float pValue)
  { /* setGeneData */
    this.data= data;
    this.data1= data1;
    this.data2= data2;
    this.pValue= pValue;
  } /* setGeneData */
  
  
  /**
   * setGeneData1AndData2() - set quantified gene data1 and data2
   * @param data1 is data for channel 1 for this gene
   * @param data2 is data for channel 2 for this gene
   */
  void setGeneData1AndData2(float data1, float data2)
  { /* setGeneData1AndData2( */
    this.data1= data1;
    this.data2= data2;
  } /* setGeneData1AndData2 */
  
  
  /**
   * setGeneDist() - set gene cluster distance (only used if clustering).
   * @param geneDist cluster distance for this gene to center of cluster
   */
  void setGeneDist(float geneDist)
  { /* setGeneDist */
    this.geneDist= geneDist;
  } /* setGeneDist */
  
  
  /**
   * setplateData() - set gene plate data (plate, plate_row, plate_col)
   * @param plate source plate for this gene's cDNA
   * @param plate_row source plate row for this gene's cDNA
   * @param plate_col source plate column for this gene's cDNA
   */
  void setplateData(String plate, String plate_row, String plate_col)
  { /* setplateData */
    this.plate= plate;
    this.plate_row= plate_row;
    this.plate_col= plate_col;
  } /* setplateData */
  
  
  /**
   * setGeneProperty() - set C_xxxx gene properties.
   * @param propToTest bit or bits to test set for this gene
   * @return the new properties bits.
   */
  int setGeneProperty(int propToTest)
  { /* setGeneProperty */
    properties |= propToTest;
    return(properties);
  } /* setGeneProperty */
  
  
  /**
   * clearGeneProperty() - clear C_xxxx gene properties.
   * @param propToTest bit or bits to test set for this gene
   * @return the new properties bits.
   */
  int clearGeneProperty(int propToTest)
  { /* clearGeneProperty */
    int bit= (properties & propToTest);
    properties -= bit;
    return(properties);
  } /* clearGeneProperty */
  
  
  /**
   * isGeneProperty() - test if C_xxxx gene property is set.
   * @param propToTest bit or bits to test if any are present for this gene
   */
  boolean isGeneProperty(int propToTest)
  { /* isGeneProperty */
    return((properties & propToTest)!=0);
  } /* isGeneProperty */
  
  
  /**
   * sortGeneList() - bubble sort gene list by associated gene.data field
   * @param cList list of genes to be sorted [0:nClist-1]
   * @param nClist size of gene list
   * @param sortAssendingFlag flag to specifry assending or descending sort
   */
  static void sortGeneList(Gene cList[],  int nClist, boolean sortAssendingFlag)
  { /* sortGeneList */
    int
      jMin= 0,
      n,
      j;
    float
      cJdata,
      cJP1data;
    Gene tmp;
    for(int i=0; i<nClist; i++)
    { /* bubble sort */
      n= jMin;
      for(j=(nClist-2); j>=n; j--)
      { /* check each (j,j+1) pair */
        cJdata= cList[j].data;
        cJP1data= cList[j+1].data;
        
        if((!sortAssendingFlag && (cJdata < cJP1data)) ||
            (sortAssendingFlag &&  (cJdata > cJP1data)))
        { /* swap j and j+1 index */
          tmp= cList[j];
          cList[j]= cList[j+1];
          cList[j+1]= tmp;
          jMin= j;                  /* reset min n found */
        } /* swap j and j+1 index */
      } /* bubble sort */
    } /* check each (j,j+1) pair */
  } /* sortGeneList */
  
  
  /**
   * toString() - generate prettyprint gene data string
   * @return prettyprint string of gene data.
   */
  public String toString()
  { /* prettyPrint */
    GridCoords gc= mae.mp.gid2fgrc[gid];
    String
    sR= "{mid=" + mid +" gid="+gid +
        " ["+ gc.f +mae.mp.gName[gc.g] + gc.r + "," + gc.c+
        "] "+mae.masterIDname+"='"+ Master_ID+
        "', "+mae.masterGeneName+"='" + MasterGeneName + "'";
    
    sR += "\n nMid="+nMid+" midList[]=[ ";
    for(int i=0;i<nMid;i++)
      sR += +midList[i]+" ";
    sR += "]";
    
    //sR += "\n dbEST3='" + dbEST3 + "', dbEST5='" + dbEST5 + "'"+
    //      "\n  GenBankAcc='" + GenBankAcc +
    //      ", GenBankAcc3='" + GenBankAcc3 +
    //      "', GenBankAcc5='" + GenBankAcc5 + "'";
    //      "', SwissProt='" + SwissProt + "'";
    //sR += "\n comments='"+comments+"'";
    //sR += "\n data="+data + " data1="+data1+" data2="+data2;
    sR += "}";
    
    return(sR);
  } /* prettyPrint */
  
  
  
} /* end of class Gene */




