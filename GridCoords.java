/** File: GridCoords.java */

/**
 * The class contains data structures to map a spot on the array to a GRC coodinate.
 * The GRC stands for (grid,row, grid_row, grid_column) coordinate.
 * This lets us generate maps to convert gene microarray spot grid coordinates to 
 * and from GID/MID index values. 
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
 * @version $Date: 2004/01/13 16:45:02 $   $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public class GridCoords
  {
    /** link to global MAExplorer instance */
    private static MAExplorer
      mae;       
      
    /** field # */ 
    public int
      f;			 
    /** grid # */  
    public int
      g;			
    /** row # */  
    public int
      r;			
    /** col # */  
    public int
      c;			
    /** index cooresponding to ...[f][g][r][c] */
    public int
      gid;		
        
   
    /**
     * GridCoords() - constructor to build empty instance.
     */
    public GridCoords()
    { /* GridCoords */
    } /* GridCoords */
    
    
    /**
     * GridCoords() - constructor which builds (f,g,r,c) - (gid or cid) mapping tables.
     * Only called from MAExplorer init.
     * @param mae is instance of MAExplorer
     * @see #createIndexToGridCoordMap
     */
    GridCoords(MAExplorer mae)
    { /* GridCoords */
      this.mae= mae;
      
      createIndexToGridCoordMap(); /* once only map creation */
    } /* GridCoords */
    
    
    /**
     * GridCoords() - constructor of individual grid coordinates for a gid spot.
     * @param f is field #
     * @param g is grid #
     * @param r is grid-row #
     * @param c is grid-column #
     * @param gid is Grid Index ID GID value
     */
    GridCoords(int f, int g, int r, int c, int gid)
    { /* GridCoords */
      this.f= f;
      this.g= g;
      this.r= r;
      this.c= c;
      this.gid= gid;
    } /* GridCoords */
    
    
    /**
     * createIndexToGridCoordMap() - once only FGRC <--> gid map creation.
     * Note: if we are using Ratio data (Cy3/Cy5, etc) then
     * we store Cy3 in f1 and Cy5 in f2. This sets up:
     *   map.fgrc2gid[f][g][r][c]
     *   map.gid2fgrc[gid]
     *   map.gidToGangGid[gid]
     */
    void createIndexToGridCoordMap()
    { /* createIndexToGridCoordMap */
      int
        maxFIELDS= mae.cfg.maxFIELDS,
        maxGRIDS=  mae.cfg.maxGRIDS,
        maxGROWS=  mae.cfg.maxGROWS,
        maxGCOLS=  mae.cfg.maxGCOLS,
        fGang,
        f,g,r,c,
        nF= (mae.useRatioDataFlag) ? 2*maxFIELDS : maxFIELDS,
        gidGang,
        gid= 0;
      GridCoords p;
      
      /*
      if(mae.CONSOLE_FLAG)
          mae.fio.logMsgln("GC-CITGCM nF="+nF);
       */
      
      /* [1] Assign gid number, create FGRC - gid mapping tables.
       * note that fgrc2gid[][][][] counts from 1 whereas all of
       * the other mapping tables count from 0.
       */
      for(f=1; f<=nF; f++)
        for(g=1;g<=maxGRIDS;g++)
          for(r=1;r<=maxGROWS;r++)
            for(c=1;c<=maxGCOLS;c++)
            { /* Assign gid number, create FGRC - gid mapping tables */
              p= new GridCoords(f,g,r,c,gid);
              mae.mp.fgrc2gid[f][g][r][c]= gid;
              mae.mp.gid2fgrc[gid]= p;
              mae.mp.maxSpotsFound= gid;   /* NOTE: THIS IS WHERE WE SET IT */
              gid++;                       /* NOTE: count gid from 0 NOT from 1
                                            * so post increment it! */
            }
      
      /* [2] Now create the gang (i.e. f1 <--> f2) mapping table.*/
      for(f=1; f<=nF; f++)
        for(g=1;g<=maxGRIDS;g++)
          for(r=1;r<=maxGROWS;r++)
            for(c=1;c<=maxGCOLS;c++)
            { /* create gid to Gang gid mapping table */
              gid= mae.mp.fgrc2gid[f][g][r][c];
              if(nF==1)
                gidGang= gid;
              else
              {
                fGang= (f==1) ? 2 : 1;
                gidGang= mae.mp.fgrc2gid[fGang][g][r][c];
              }
              mae.mp.gidToGangGid[gid]= gidGang;
            }
    } /* createIndexToGridCoordMap */
    
    
    /**
     * toString() - convert grid coordinate instance to string name
     * @param useFieldNameFlag returns field name else field #
     * @return FGRC string "[f-g,r,c]"
     * @see #cvtFGRC2str
     */
    final public String toString(boolean useFieldNameFlag)
    { /* toString */
      return(cvtFGRC2str(f,g,r,c, useFieldNameFlag));
    } /* toString */
    
    
    /**
     * cvtFGRC2str() - convert grid coordinate f,g,r,c to string name
     * @param f is field #
     * @param g is grid #
     * @param r is grid-row #
     * @param c is grid-column #
     * @param useFieldNameFlag returns field name else field #
     * @return FGRC string "[f-g,r,c]"
     */
    final public String cvtFGRC2str(int f, int g, int r, int c,
    boolean useFieldNameFlag)
    { /* cvtFGRC2str */
      String maCoords;
      
      if(!useFieldNameFlag)
        maCoords= "[" + f + "-" + mae.mp.gName[g] + r + "," + c + "] ";
      else
        maCoords= "[" + mae.mp.fName[f] +"-" + mae.mp.gName[g] + r +"," +c +"] ";
      return(maCoords);
    } /* cvtFGRC2str */
    
    
    /**
     * cvtGID2str() - convert grid coord GridCoords(gid) to string name
     * @param gid is Grid Index ID GID value
     * @param useFieldNameFlag returns field name else field #
     * @return FGRC string "[f-g,r,c]"
     */
    final public String cvtGID2str(int gid, boolean useFieldNameFlag)
    { /* cvtGID2str */
      int
        f= mae.mp.gid2fgrc[gid].f,
        g= mae.mp.gid2fgrc[gid].g,
        r= mae.mp.gid2fgrc[gid].r,
        c= mae.mp.gid2fgrc[gid].c;
      String maCoords;
      
      if(!useFieldNameFlag)
        maCoords= "[" + f + "-" + mae.mp.gName[g] + r + "," + c + "] ";
      else
        maCoords= "[" + mae.mp.fName[f] +"-" + mae.mp.gName[g] + r +"," +c +"] ";
      return(maCoords);
    } /* cvtGID2str */
    
    
    /**
     * cvtNAME_GCR2FGRC() - parse Molecular Dynamics "GRID- 8-R12C11" coding to FGRC coords.
     * Contains finite state machine to parse it and save it in
     * the (f,g,r,c) for this object.
     * @param field is explicit field #
     * @param sGRC is the NAME_GRC data
     * @return grid coordinates object if ok, else null if an error.
     * @see Util#cvs2i
     */
    public GridCoords cvtNAME_GCR2FGRC(int field, String sGRC )
    { /* cvtNAME_GCR2FGRC */
      /* [TODO] add better error checking */
      if(field==0 || sGRC==null)
        return(null);
      
      String
        data[],
        sG,
        sG_R,
        sG_C;
      char
        ch,
        gridC[]= new char[5],  /* for parsing "GRID-   8-R12C11" coding */
        rowC[]= new char[5],
        colC[]= new char[5];
      
      /* Parse sRGC as "GRID-  <sG>-R<sG_R>C<SG_C>" */
      int
        j= 0,
        iG= 0,    /* length of substrings */
        iR= 0,
        iC= 0,
        len= sGRC.length();
      
      while(j<len)
      { /* look for start of Grid # */
        ch= sGRC.charAt(j++);
        if(Character.isDigit(ch))
        {
          gridC[iG++]= ch;  /* Save grid index */
          break;
        }
      }
      
      while(j<len)
      { /* save grid # and look for start of Row 'R' */
        ch= sGRC.charAt(j++);
        if(ch=='-')
          continue;      /* ignore the '-' before '-R' */
        if(ch=='R')
          break;
        gridC[iG++]= ch;  /* Save grid # */
      }
      
      while(j<len)
      { /* save row # and look for start of Col 'C' */
        ch= sGRC.charAt(j++);
        if(ch=='C')
          break;
        rowC[iR++]= ch;  /* Save row # */
      }
      
      while(j<len)
      { /* save col # */
        ch= sGRC.charAt(j++);
        colC[iC++]= ch;  /* Save column # */
      }
      
      if(iG==0 || iR==0 || iC==0)
        return(null);
      
      /* Convert to string before cvt to int # */
      sG= new String(gridC,0,iG);
      sG_R= new String(rowC,0,iR);
      sG_C= new String(colC,0,iC);
      
      /* Convert to integers */
      f= field;
      g= Util.cvs2i(sG);
      r= Util.cvs2i(sG_R);
      c= Util.cvs2i(sG_C);
      
      /*
      if(mae.CONSOLE_FLAG)
          mae.fio.logMsgln("GCrd-CNG2F.1 sGRC='"+sGRC+
                           "' sG='"+sG+"' g="+g+"' sG_R='"+
                           sG_R+"' sG_C='"+sG_C+"'");
       */
      
      return(this);
    } /* cvtNAME_GCR2FGRC */
    
    
} /* end of class GridCoords */




