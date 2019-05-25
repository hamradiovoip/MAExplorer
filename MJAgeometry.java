/** File: MJAgeometry.java */ 
 
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java API class to access MJAgeometry methods and data structures. 
 * Note: GID is the Grid coordinates ID, MID is Master gene ID,
 * FGRC is (Field,Grid,Row,Col) spot location.
 * Access geometry description of the arrays.
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<P>
 *<PRE> 
 * getMaxGenes() - get number of genes in the database
 * getMaps() - get Hashtablelist of a array geometry and MAExplorer maps
 * getNbrMasterIDs() - get the number of Master Gene IDs
 * getNbrGridIDs() - get the number of genes
 * getNbrSpots() - get the number of Nbr Spots in entire array
 * getMaxFields() - get the number of Max replicate grid Fields
 * getMaxGrids() - get the number of Max Grids in the array
 * getMaxGridRows() - get the number of rows/grid in the array
 * getMaxGridCols() - get the number of columns/grid in the array
 * getMasterGeneIDMap() - get the map of mid to gene.
 * getGridIndexMap() - get the map of mid to gene.
 * getFGRCtoGIDmap() - get map of [field][grid][grid_row][grid_col]'s to gids 
 * getGIDtoFGRCmap() - get the map of GIDs to FGRC 5-tuples
 * getMIDtoGIDmap() - get the map from MIDs to GIDs
 * getGIDtoMIDmap() - get the map from GIDs to MIDs 
 * getGIDtoGangGIDmap() - get the map from GIDs to Ganged GIDs
 * lookupMIDtoGID() - lookup MID given the GID
 * lookupGIDtoMID() - lookup GID given the MID
 * lookupGIDtoGangGID() - lookup ganged GID given the GID
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
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *<P>
 */
 
public class MJAgeometry extends MJAbase
{  

  /**
   * MJAgeometry() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAgeometry()
  { /* MJAgeometry */
  } /* MJAgeometry */
  
  
  
  /**
   * getMaxGenes() - get number of genes in the database
   * @return value else <code>null</code> if not found.
   */
  public final int getMaxGenes()
  { return(fc.maxGenes); }
  
  
  /**
   * getMaps() - get Hashtable list of a MAExplorer maps
   *
   * <PRE>
   * The list returned is defined as:
   * <B>Name                - Value</B>
   * "NbrMasterIDs"       - int # of MIDs (Master IDs) in DB  and
   *                        MasterGeneIndexMap
   * "NbrGridIDs"         - int # of GIDs (GridCoords Index) in DB
   * "NbrSpots"           - int # of spots in the database and GridIndexMap
   * "MaxFields"          - int number of microarray duplicate grid fields
   * "MaxGrids"           - int number of microarray grids
   * "MaxGridRows"        - int number of microarray grid rows
   * "MaxGridCols"        - int number of microarray grid columns
   * "MasterGeneIDMap"    - int[0:NbrMasterIDs]  Master gene ID (MID) list
   * "GridIndexMap"       - int[0:NbrGridIDs] master Grid Index (GID) list
   * "FGRCtoGIDmap"       - int [0:MaxFields][0:MaxGrids][0:MaxGridRows1]
   *                           [0:MaxGridCols] maps [f][g][r][c] to GID
   * "GIDtoFGRCmap"       - int[0:NbrGridIDs][0:4] maps GID to a
   *                           list (gid,f,g,r,c)
   * "MIDtoGIDmap"        - int[0:NbrMasterIDs] maps MID to GID
   * "GIDtoMIDmap"        - int[0:NbrGridIDs] maps GID to MID
   * "GIDtoGangGIDmap"    - int[0:NbrGridIDs] maps GID to Gang GID
   * </PRE>
   *
   * @return <code>null</code> if not found or error, else return  list.
   */
  public final Hashtable getMaps()
  { /* getMaps */
    Hashtable ht= new Hashtable(30);
    ht.put("NbrMasterIDs",new Integer(map.maxMIDs));
    ht.put("NbrGridIDs",new Integer(map.maxGenes));
    ht.put("NbrSpots",new Integer(map.maxSpots));
    ht.put("MaxFields",new Integer(cfg.maxFIELDS));
    ht.put("MaxGrids",new Integer(cfg.maxGRIDS));
    ht.put("MaxGridRows",new Integer(cfg.maxGROWS));
    ht.put("MaxGridCols",new Integer(cfg.maxGCOLS));
    ht.put("MasterGeneIDMap",cvtGeneList2GeneMIDlist(map.midStaticCL));
    ht.put("GridIndexMap",cvtGeneList2GeneMIDlist(map.gidStaticCL));
    ht.put("FGRCtoGIDmap",map.fgrc2gid);
    ht.put("GIDtoFGRCmap",getGIDtoFGRCmap());
    ht.put("MIDtoGIDmap",map.mid2gid);
    ht.put("GIDtoMIDmap",map.gid2mid);
    ht.put("GIDtoGangGIDmap",map.gidToGangGid);
    
    return(ht);
  } /* getMaps */
  
  
  /**
   * getNbrMasterIDs() - get the number of Master Gene IDs
   * @return value
   */
  public final int getNbrMasterIDs()
  { return(map.maxMIDs); }
  
  
  /**
   * getNbrGridIDs() - get the number of genes
   * @return value
   */
  public final int getNbrGridIDs()
  { return(map.maxGenes); }
  
  
  /**
   * getNbrSpots() - get the number of Nbr Spots in entire array
   * @return value
   */
  public final int getNbrSpots()
  { return(map.maxSpots); }
  
  
  /**
   * getMaxFields() - get the number of Max replicate grid Fields
   * @return value
   */
  public final int getMaxFields()
  { return(cfg.maxFIELDS); }
  
  
  /**
   * getMaxGrids() - get the number of Max Grids in the array
   * not counting replicate fields. The total # of grids
   * is (MaxGrids * MaxFields).
   * @return value
   */
  public final int getMaxGrids()
  { return(cfg.maxGRIDS); }
  
  
  /**
   * getMaxGridRows() - get the number of rows/grid in the array
   * @return value
   */
  public final int getMaxGridRows()
  { return(cfg.maxGROWS); }
  
  
  /**
   * getMaxGridCols() - get the number of columns/grid in the array
   * @return value
   */
  public final int getMaxGridCols()
  { return(cfg.maxGCOLS); }
  
  
  /**
   * getMasterGeneIDMap() - get the map of mid to gene. Entries with -1
   * indicate there is no gene at that Master Gene ID location.
   * @return value
   */
  public final int[] getMasterGeneIDMap()
  { return(cvtGeneList2GeneMIDlist(map.midStaticCL)); }
  
  
  /**
   * getGridIndexMap() - get the map of mid to gene. Entries with -1
   * indicate there is no spot at that Grid Index location.
   * @return value
   */
  public final int[] getGridIndexMap()
  { return(cvtGeneList2GeneMIDlist(map.gidStaticCL)); }
  
  
  /**
   * getFGRCtoGIDmap() - get map of [field][grid][grid_row][grid_col]
   * to gid (GridCoords index GID).
   * @return map [field][grid][grid_row][grid_col] to lookup gids
   */
  public final int[][][][] getFGRCtoGIDmap()
  { return(map.fgrc2gid); }
  
  
  /**
   * getGIDtoFGRCmap() - get the map of GIDs to FGRC 5-tuples
   * Each map entry is {gid, field, grid, row, column}
   * @return map array[0:maxSpots][0:4] of 5-tuples.
   */
  public final int[][] getGIDtoFGRCmap()
  { /* getGIDtoFGRCmap */
    int
      lthG2F= map.gid2fgrc.length,
      tupleGID2FGRC[][]= new int[lthG2F][5];
    for(int gid= 0; gid<lthG2F;gid++)
    {
      int gidFGRClist[]= {
                           gid,
                           map.gid2fgrc[gid].f, map.gid2fgrc[gid].g,
                           map.gid2fgrc[gid].r, map.gid2fgrc[gid].c
                         };
      tupleGID2FGRC[gid]= gidFGRClist;
    }
    return(tupleGID2FGRC);
  } /* getGIDtoFGRCmap */
  
  
  /**
   * getMIDtoGIDmap() - get the map from MIDs to GIDs
   * @return map
   */
  public final int[] getMIDtoGIDmap()
  { return(map.mid2gid); }
  
  
  /**
   * getGIDtoMIDmap() - get the map from GIDs to MIDs
   * @return map
   */
  public final int[] getGIDtoMIDmap()
  { return(map.gid2mid); }
  
  
  /**
   * getGIDtoGangGIDmap() - get the map from GIDs to Ganged GIDs
   * @return value
   */
  public final int[] getGIDtoGangGIDmap()
  { return(map.gidToGangGid); }
  
  
  /**
   * lookupMIDtoGID() - lookup MID given the GID
   * @return GID value or -1 if an error
   */
  public final int lookupMIDtoGID(int mid)
  {
    if(mid<0 || mid>map.maxGenes)
      return(-1);
    return(map.mid2gid[mid]);
  }
  
  
  /**
   * lookupGIDtoMID() - lookup GID given the MID
   * @return MID value or -1 if an error
   */
  public final int lookupGIDtoMID(int gid)
  {
    if(gid<0 || gid>map.maxSpots)
      return(-1);
    return(map.gid2mid[gid]);
  }
  
  
  /**
   * lookupGIDtoGangGID() - lookup ganged GID given the GID
   * @return value
   */
  public final int lookupGIDtoGangGID(int gid)
  {
    if(gid<0 || gid>map.maxSpots)
      return(-1);
    return(map.gidToGangGid[gid]);
  }
  
  
  
} /* end of class MJAgeometry */
