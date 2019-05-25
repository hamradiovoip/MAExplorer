/** File: SpotData.java */

import java.awt.*;
import java.awt.image.*;

/** 
 * The SpotData class is used as a static object to hold gene (HP-X,HP-Y), 
 * (f1,f2) spot quantified data and (x,y) coordiantes for a particular 
 * hybridized sample HP or pairs of samples (HP-X and HP-Y). 
 * Data may be assembled here from various sources but then used as a 
 * single object.
 *<P>
 * Note: This implements a type of "gather" method so that users of this 
 * data have what they need for further computations in one structure.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), G.Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

class SpotData
{
  /* [TODO] need to optimize this more since it is called a lot. */

  /** link to global MAExplorer instance */
  private static MAExplorer
    mae;           
  
  /** HP where this data came from */
  static MaHybridSample
    ms;            
 
  /* --- The following are STATIC versions so we don't have to keep 
   * reallocating it --- */

  /** X,Y Centroid of a spot */
  static Point
    xyS;
  /** X,Y Centroid of spot 1 */
  static Point
    xy1S;
  /** X,Y Centroid of spot 2 */
  static Point
    xy2S;
    

  /** GridCoords gid of a spot */
  static int
    gidS;
  /** GridCoords gid of spot 1 */
  static int
    gid1S;
  /** GridCoords gid of spot 2 */
  static int
    gid2S;          

  /** total bkgrd corrected intensity of a spot */
  static float
    totS;  
  /** total bkgrd corrected intensity of a spot */
  static float
    tot1S;        
  /** total bkgrd corrected intensity of a spot */
  static float
    tot2S;          
   
    
  /**
   * SpotData() - constructor to capture mae.
   * @param mae is instance of MAExplorer
   */
  SpotData(MAExplorer mae)
  { /*SpotData*/
    this.mae= mae;
  } /*SpotData*/
  
  
  /**
   * setSpotData() - set the default STATIC spot data by (x,y).
   * @param maS sample to use
   * @param x is X coordinate
   * @param y is Y coordinate
   * @param gid is Grid Index
   * @param tot is raw intensity
   */
  static void setSpotData(MaHybridSample msS, int x, int y, int gid, float tot)
  { /* setSpotData */
    ms= msS;
    mae= ms.mae;
    xyS.x= x;
    xyS.y= y;
    gidS= gid;
    totS= tot;
    
    /* COULD do some validation... */
  } /* setSpotData */
  
  
  /**
   * setSpotData() - set the default STATIC spot data by Point xy.
   * @param maS sample to use
   * @param xy is Point X,Y coordinate
   * @param gid is Grid Index
   * @param tot is raw intensity
   */
  static void setSpotData(MaHybridSample msS, Point xy, int gid, float tot)
  { /* setSpotData */
    ms= msS;
    mae= ms.mae;
    xyS= xy;
    gidS= gid;
    totS= tot;
    
    /* COULD do some validation... */
  } /*setSpotData*/
  
  
  /**
   * setSpotData1() - set the default STATIC spot data by Point xy.
   * @param maS sample to use
   * @param xy is Point X,Y coordinate
   * @param gid is Grid Index
   * @param tot is channel 1 raw intensity
   */
  static void setSpotData1(MaHybridSample msS, Point xy, int gid, float tot)
  { /* setSpotData1 */
    ms= msS;
    mae= ms.mae;
    xy1S= xy;
    gid1S= gid;
    tot1S= tot;    
    /* COULD do some validation... */
  } /* setSpotData1 */
  
  
  /**
   * setSpotData2() - set the default STATIC spot data by Point xy.
   * @param maS sample to use
   * @param xy is Point X,Y coordinate
   * @param gid is Grid Index
   * @param tot is channel 2 raw intensity
   */
  static void setSpotData2(MaHybridSample msS, Point xy, int gid, float tot)
  { /* setSpotData2 */
    ms= msS;
    mae= ms.mae;
    xy2S= xy;
    gid2S= gid;
    
    tot2S= tot;    
    /* COULD do some validation... */
  } /* setSpotData2 */
  
  
} /* end of class SpotData */
