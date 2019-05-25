/** File: Maps.java*/

import java.util.*;

/**
 * The Maps class holds various gene maps for mapping: GID, MID, GCRF, gene names.
 * Specifically, gid, mid, gangGid, cloneID, gene_name mapping and hash lookup
 * tables. The mapping tables are allocated here but populated elsewhere, and
 * used throughout MAExplorer.
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
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.12 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see GridCoords
 * @see GeneList
 */

class Maps
{
  
  /* NOTE:
   * [NOTE]: as of V.0.90.01, "mid (MID) Clone_ID" in V.0.89.41
   * was renamed everywhere to "mid (MID)" "Master ID".
   */
  
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;
  /** link to global Config instance */
  private Config
    cfg;
  
  /** list of alphabetic names of Names of fields */
  final static String
    fName[]= {null, "left", "right"};
  /** list of alphabetic names of Names of grids */
  final static String
    gName[]= {"@", "A", "B", "C", "D", "E", "F", "G", "H",
              "I", "J", "K", "L", "M", "N", "O", "P", "Q",
              "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
              "a", "b", "c", "d", "e", "f", "g", "h",
              "i", "j", "k", "l", "m", "n", "o", "p", "q",
              "r", "s", "t", "u", "v", "w", "x", "y", "z",
              "1","2","3","4","5","6","7","8","9","10",
              "11","12","13","14","15","16","17","18","19","20",
              "21","22","23","24","25","26","27","28","29","30",
              "31","32","33","34","35","36","37","38","39","40",
              "41","42","43","44","45","46","47","48","49","50",
              "51","52","53","54","55","56","57","58","59","60",
              "61","62","63","64","65","66","67","68","69","70",
              "71","72","73","74","75","76","77","78","79","80",
              "81","82","83","84","85","86","87","88","89","90",
              "91","92","93","94","95","96","97","98","99","100"};
   
   /** size of gene hash table estimated as 1.5*maxGenes - only to 1st instance */
   int
     maxGeneHashTableSize= 0;
   /** maximum # of Master Gene IDs = size of gene (i.e clone,oligo, etc) list */
   int
     maxMIDs= 0;
   /** maximum number of genes (estimated as maxGRIDS*maxGROWS*maxGCOLS) */
   int
     maxGenes= 0;
   /** maximum # of spots estimated as maxFIELDS*maxGRIDS*maxGROWS*maxGCOL */
   int
     maxSpots= 0;
   /** max # spots found. Set by GridCoords.createIndexToGridCoordMap */
   int
     maxSpotsFound= 0;
   
    /* --- Note: maxMIDs is read from the GeneTable and should be the same
     * as  maxGenes which is computed from the # fields, grids, rows, cols. ---
     */
   
   /** map: gid (GridCoords index) to [f][g][r][c] values in GridCoords object.
    * This is created by:  GridCoords.createIndexToGridCoordMap()
    * It is searched by: cfg.lookupHPoords()
    */
   GridCoords
     gid2fgrc[]= null;
   
   /** map: [f][g][r][c] to gid (GridCoords index GID).
    * estimate as [maxFIELDS+1][maxGRIDS+1][maxGROWS+1][maxGCOLS+1]
    * This is created by: GridCoords.createIndexToGridCoordMap()
    * It is NOT searched - just used as lookup.
    */
   int
     fgrc2gid[][][][]= null;
   
   /** map: gid (GridCoords index) to the Gang gid.
    * This is created by: GridCoords.createIndexToGridCoordMap()
    * It is NOT searched - juse used as lookup.
    */
   int
     gidToGangGid[]= null;
   
   /** map: gid (gridCoords index) to mid (Master Gene index).
    * NOTE mid is a small number < maxGenes - it is NOT the numeric equivalent
    * of MasterID! This is created by: GeneTable.setupMIDlist().
    * It is searched by: ????  NOTYET!.
    */
   int
     gid2mid[]= null;
   
   /** map: mid (Master Gene index) to gid (gridCoords index)
    * This is created by: GeneTable.setupMIDlist()
    * It is used for lookup by:  GeneTable.getGeneListOfGeneClass()
    */
   int
     mid2gid[]= null;
   
   /** map: mid index to list of genes i.e. Maps.midStatCL.mList[mid]
    * This STATIC map is created by: GeneTable.setupMIDlist()
    */
   GeneList
     midStaticCL= null;
   
   /** map: GridCoords gid index to list of genes. i.e. Maps.gidStatCL.mList[gid]
    * This STATIC  map is created by: GeneTable.setupMIDlist()
    */
   GeneList
     gidStaticCL= null;
   
   /** hash table of Master Gene IDs (MID) of size maxGeneHashTableSize. The value
    * is an Integer value mid which is the index midStaticCL[].
    * This is created by: GeneTable.setupMIDlist().
    * It is searched by: GeneTable.getMIDfromMasterIDname(MasterID).
    */
   Hashtable
     mid2MasterIdHashtable= null;   
   
   
   /**
    * Maps() - constructor that allocates mapping tables calc size from F*G*R*C
    * These is populated @GipoTable.setupMapLookupTables().
    * @param mae is instance of MAExplorer
    * @see #allocateMapArrays
    */
   Maps(MAExplorer mae)
   { /* Maps */
     this.mae= mae;
     cfg= mae.cfg;
     
     if(!mae.startupFileExistsFlag)
     { /* There is no data */
       return;
     }
     
     maxGenes= cfg.maxGRIDS*cfg.maxGROWS*cfg.maxGCOLS;
     maxSpots= cfg.maxFIELDS*maxGenes;
     if(mae.useRatioDataFlag)
       maxSpots= 2*maxSpots;  /* allocate space for dual fluoresence */
     
     maxSpotsFound= 0;        /* will set by GridCoords.createIndexToGridCoordMap */
     
     /* [NOTE] only put ONE instance of a gene into the hash table.
      * So if want to find a list of all mids(gids) for the same gene
      * name, can not use the hash table method. Need to
      *   a) do a search of all names
      * or
      *   b) keep a list in the hash'ed Gene that points to the
      * other gids.
      */
     maxGeneHashTableSize= (int)(1.5*maxGenes);
     maxMIDs= 0;
     
     allocateMapArrays();     /* Allocate the maps */
   } /* Maps */
   
   
   /**
    * allocateMapArrays() - Allocate the map arrays and hash tables
    * @see FileIO#logMsgln
    * @see GeneList
    */
   private void allocateMapArrays()
   { /* allocateMapArrays */
     int
       f, g, r, c, i,
       maxFIELDS= cfg.maxFIELDS,
       nF= (mae.useRatioDataFlag) ? 2*maxFIELDS : maxFIELDS,
                 /* double up on the data since there
                  * are 2 channels/spot. NOTE: the
                  * GID space must allocate space
                  * for this. */
       maxGRIDS= cfg.maxGRIDS,
       maxGROWS= cfg.maxGROWS,
       maxGCOLS= cfg.maxGCOLS,
       maxFGRC= (maxFIELDS+1)*(maxGRIDS+1)*(maxGROWS+1)*(maxGCOLS+1);
                 /* add extra since index by  [1:maxXXXX]
                  * NOT by [0:maxXXXX-1] */
     
     if(mae.CONSOLE_FLAG)
     {
       mae.fio.logMsgln("MP-AMA maxSpots="+maxSpots+ " maxGenes="+maxGenes+
                        " maxFGRC="+maxFGRC);
     }
     
      /* Maps between gid (GridCoords index) and [f][g][r][c] values
       * in GridCoords object.
       */
     gid2fgrc= new GridCoords[maxSpots];
     fgrc2gid= new int[nF+1][maxGRIDS+1][maxGROWS+1][maxGCOLS+1];
     
      /* make sure all slots are initialized to illegal Java
       * bounds checker value i.e. -1
       */
     for(f=0; f<=maxFIELDS; f++)
       for(g=0;g<=maxGRIDS;g++)
         for(r=0;r<=maxGROWS;r++)
           for(c=0;c<=maxGCOLS;c++)
             fgrc2gid[f][g][r][c]= -1;
     
     /* Maps between gid (gridCoords index) & mid (Gene index) set to NULL */
     gid2mid= new int[maxSpots];
     gidToGangGid= new int[maxSpots];
     mid2gid= new int[maxGenes];
     for(i=0;i<maxSpots;i++)
     { /* set all slots to illegal value so Java bounds checker will catch it */
       gid2mid[i]= -1;
       gidToGangGid[i]= -1;
     }
     for(i=0;i<maxGenes;i++)
     { /* set all slots to illegal value so Java bounds checker will catch it */
       mid2gid[i]= -1;
     }
     
     /* Make the primary STATIC mapping gene lists */
     midStaticCL= new GeneList(mae, maxGenes, "midStaticCL",true);
     gidStaticCL= new GeneList(mae, maxSpots, "gidStaticCL",true);
     
     /* Make hash table of gene clone ids by gene names */
     mid2MasterIdHashtable= new Hashtable(maxGeneHashTableSize);
   } /* allocateMapArrays */
   
   
   /**
    * lookupMIDfromGeneName() - lookup mid from Gene Name.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   int lookupMIDfromGeneName(String name)
   { /* lookupMIDfromGeneName */
     int mid= -1;	 /* default to fail */
     
     if(name!=null && name.length()>0)
       for(int i=0; i < maxGenes; i++)
       { /* lookup the gene */
         Gene gene= midStaticCL.mList[i];
         if(gene==null)
           continue;
         if(gene.Gene_Name!=null &&  gene.Gene_Name.equalsIgnoreCase(name))
         {
           mid= gene.mid;
           break;
         }
       } /* lookup the gene */
     
     return(mid);
   } /* lookupMIDfromGeneName */
   
   
   /**
    * lookupMIDlistFromGeneName() - lookup mid list from Gene Name.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDListFromGeneName(String name)
   { /* lookupMIDListFromGeneName */
     int
       midList[]= null,
       mid= lookupMIDfromGeneName(name);
     if(mid!=-1)
       midList= midStaticCL.mList[mid].midList;
     return(midList);
   } /* lookupMIDlistFromGeneName */
   
   
   /**
    * lookupMIDfromCloneID() - lookup mid from CloneID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   int lookupMIDfromCloneID(String name)
   { /* lookupMIDfromCloneID */
     int mid= -1;	 /* default to fail */
     
     if(name!=null && name.length()>0)
       for(int i=0; i < maxGenes; i++)
       { /* lookup the gene */
         Gene gene= midStaticCL.mList[i];
         if(gene==null)
           continue;
         if(gene.Clone_ID!=null && gene.Clone_ID.equalsIgnoreCase(name))
         {
           mid= gene.mid;
           break;
         }
       } /* lookup the gene */
     
     return(mid);
   } /* lookupMIDfromCloneID */
   
   
   /**
    * lookupMIDlistFromCloneID() - lookup mid list from CloneID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDListFromCloneID(String name)
   { /* lookupMIDListFromCloneID */
     int
       midList[]= null,
       mid= lookupMIDfromCloneID(name);
     if(mid!=-1)
       midList= midStaticCL.mList[mid].midList;
     return(midList);
   } /* lookupMIDlistFromCloneID */
   
   
   /**
    * lookupMIDfromUniGeneID() - lookup mid from UniGene ID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   int lookupMIDfromUniGeneID(String name)
   { /* lookupMIDfromUniGeneID */
     int mid= -1;	 /* default to fail */
     
     if(name!=null && name.length()>0)
       for(int i=0; i < maxGenes; i++)
       { /* lookup the gene */
         Gene gene= midStaticCL.mList[i];
         if(gene==null)
           continue;
         if(gene.Unigene_ID!=null && gene.Unigene_ID.equalsIgnoreCase(name))
         {
           mid= gene.mid;
           break;
         }
       } /* lookup the gene */
     
     return(mid);
   } /* lookupMIDfromUniGeneID */
   
   
   /**
    * lookupMIDlistFromUniGeneID() - lookup mid list from UniGene ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDListFromUniGeneID(String name)
   { /* lookupMIDListFromUniGeneID */
     int
       midList[]= null,
       mid= lookupMIDfromUniGeneID(name);
     if(mid!=-1)
       midList= midStaticCL.mList[mid].midList;
     return(midList);
   } /* lookupMIDlistFromUniGeneID */
   
   
   /**
    * lookupMIDfromRefSeqID() - lookup mid from RefSeq ID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   int lookupMIDfromRefSeqID(String name)
   { /* lookupMIDfromRefSeqID */
     int mid= -1;	 /* default to fail */
     
     if(name!=null && name.length()>0)
       for(int i=0; i < maxGenes; i++)
       { /* lookup the gene */
         Gene gene= midStaticCL.mList[i];
         if(gene==null)
           continue;
         if(gene.RefSeqID!=null && gene.RefSeqID.equalsIgnoreCase(name))
         {
           mid= gene.mid;
           break;
         }
       } /* lookup the gene */
     
     return(mid);
   } /* lookupMIDfromRefSeqID */
   
   
   /**
    * lookupMIDlistFromRefSeqID() - lookup mid list from RefSeq ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDListFromRefSeqID(String name)
   { /* lookupMIDListFromRefSeqID */
     int
       midList[]= null,
       mid= lookupMIDfromRefSeqID(name);
     if(mid!=-1)
       midList= midStaticCL.mList[mid].midList;
     return(midList);
   } /* lookupMIDlistFromRefSeqID */
   
   
   /**
    * lookupMIDfromLocusID() - lookup mid from Locus ID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   int lookupMIDfromLocusID(String name)
   { /* lookupMIDfromLocusID */
     int mid= -1;	 /* default to fail */
     
     if(name!=null && name.length()>0)
       for(int i=0; i < maxGenes; i++)
       { /* lookup the gene */
         Gene gene= midStaticCL.mList[i];
         if(gene==null)
           continue;
         if(gene.LocusID!=null && gene.LocusID.equalsIgnoreCase(name))
         {
           mid= gene.mid;
           break;
         }
       } /* lookup the gene */
     
     return(mid);
   } /* lookupMIDfromLocusID */
   
   
   /**
    * lookupMIDlistFromLocusID() - lookup mid list from Locus ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDListFromLocusID(String name)
   { /* lookupMIDListFromLocusID */
     int
       midList[]= null,
       mid= lookupMIDfromLocusID(name);
     if(mid!=-1)
       midList= midStaticCL.mList[mid].midList;
     return(midList);
   } /* lookupMIDlistFromLocusID */
   
   
   /**
    * lookupMIDfromSwissProtID() - lookup mid from SwissProt ID
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   int lookupMIDfromSwissProtID(String name)
   { /* lookupMIDfromSwissProtID */
     int mid= -1;	 /* default to fail */
     
     if(name!=null && name.length()>0)
       for(int i=0; i < maxGenes; i++)
       { /* lookup the gene */
         Gene gene= midStaticCL.mList[i];
         if(gene==null)
           continue;
         if(gene.SwissProt!=null && gene.SwissProt.equalsIgnoreCase(name))
         {
           mid= gene.mid;
           break;
         }
       } /* lookup the gene */
     
     return(mid);
   } /* lookupMIDfromSwissProtID */
   
   
   /**
    * lookupMIDlistFromSwissProtID() - lookup mid list from SwissProt ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDListFromSwissProtID(String name)
   { /* lookupMIDListFromSwissProtID */
     int
       midList[]= null,
       mid= lookupMIDfromSwissProtID(name);
     if(mid!=-1)
       midList= midStaticCL.mList[mid].midList;
     return(midList);
   } /* lookupMIDlistFromSwissProtID */
   
   
   /**
    * lookupMIDfromGenBankACC() - lookup mid from GenBank ACC ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   int lookupMIDfromGenBankACC(String name)
   { /* lookupMIDfromGenBankACC */
     int mid= -1;	 /* default to fail */
     
     if(name!=null && name.length()>0)
       for(int i=0; i < maxGenes; i++)
       { /* lookup the gene */
         Gene gene= midStaticCL.mList[i];
         if(gene==null)
           continue;
         if((gene.GenBankAcc!=null && gene.GenBankAcc.equalsIgnoreCase(name)) ||
            (gene.GenBankAcc5!=null && gene.GenBankAcc5.equalsIgnoreCase(name)) ||
            (gene.GenBankAcc3!=null && gene.GenBankAcc3.equalsIgnoreCase(name)) ||
            (gene.RefSeqID!=null && gene.RefSeqID.equalsIgnoreCase(name)) )
         {
           mid= gene.mid;
           break;
         }
       } /* lookup the gene */
     
     return(mid);
   } /* lookupMIDfromGenBankACC */
   
   
   /**
    * lookupMIDlistFromGenBankACC() - lookup mid list from GenBank ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDlistFromGenBankACC(String name)
   { /* lookupMIDlistFromGenBankACC */
     int
       midList[]= null,
       mid= lookupMIDfromGenBankACC(name);
     if(mid!=-1)
       midList= midStaticCL.mList[mid].midList;
     return(midList);
   } /* lookupMIDlistFromGenBankACC */
   
   
   /**
    * lookupMIDfrom_dbEST() - lookup mid from dbEST 3' or 5' ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid if found else -1
    */
   int lookupMIDfrom_dbEST(String name)
   { /* lookupMIDfrom_dbEST */
     int mid= -1;            	 /* default to fail */
     
     if(name!=null && name.length()>0)
       for(int i=0; i < maxGenes; i++)
       { /* lookup the gene */
         Gene gene= midStaticCL.mList[i];
         if(gene==null)
           continue;
         if((gene.dbEST3!=null && gene.dbEST3.equalsIgnoreCase(name)) ||
            (gene.dbEST5!=null && gene.dbEST5.equalsIgnoreCase(name)) )
         {
           mid= gene.mid;
           break;
         }
       } /* lookup the gene */
     
     return(mid);
   } /* lookupMIDfrom_dbEST */
   
   
   /**
    * lookupMIDlistFrom_dbEST() - lookup mid list from dbEST 3' or 5' ID.
    * Note this returns the first instance of the gene found if there are duplicates.
    * @param name of gene to look up
    * @return mid list if found, else null
    */
   int[] lookupMIDlistFrom_dbEST(String name)
   { /* lookupMIDlistFrom_dbEST */
     int
       midList[]= null,
       mid= lookupMIDfrom_dbEST(name);
     if(mid!=-1)
       midList= midStaticCL.mList[mid].midList;
     return(midList);
   } /* lookupMIDlistFrom_dbEST */
   
   
   
} /* end of class Maps */




