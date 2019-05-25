/** File: DrawScatterPlot.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;

/**
 * Class to draw scatter plot in an extended Draw2Dplot canvas.
 * It is updated by the PopupRegistry when the normalization, current gene,
 * Filter.workingCL, or other state values change.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), G.g Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2003/07/07 21:40:41 $   $Revision: 1.11 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see Draw2Dplot
 * @see ShowPlotPopup
 */

class DrawScatterPlot extends Draw2Dplot
{
  /** link to global instance */
  private MAExplorer 
    mae;                   
  /** link to global instance */
  private MaHybridSample
    ms;                   
  /** link to global instance */
  private Maps
    map;           
  /** link to global instance */
  private CompositeDatabase
    cdb;                   
  
  /** plot mode of scatter plot */
  private int 
    plotMode;              
  /** parent window */
  private ShowPlotPopup
    spp;                   
  /** MID closest to point click on */
  private int
    prevBestMID;     
  

  /**
   * DrawScatterPlot() - constructor to create scatter plot
   * @param mae is the MAExplorer instance
   * @param spp is the ShowPlotPopup instance
   * @param plotMode is the specific plot mode to implement
   * @see #drawScatterPlot
   */
  DrawScatterPlot(MAExplorer mae, ShowPlotPopup spp, int plotMode)
  { /* DrawScatterPlot */
    super(mae, plotMode, spp, null, "ScatterPlot", -1, 1, false);
    
    this.mae= mae;
    ms= mae.ms;
    map= mae.mp;
    cdb= mae.cdb;
    this.spp= spp;
    this.plotMode= plotMode;
    
    drawScatterPlot();
  } /* DrawScatterPlot */
  
  
  /**
   * updateScatterPlot() - refresh scatter plot
   * Note: prevBestMID could be -1 or -2.
   * @see #drawScatterPlot
   * @see #showGeneMsgsAndPlot
   */
  void updateScatterPlot()
  { /* updateScatterPlot */
    if(prevBestMID>=0)
      showGeneMsgsAndPlot(prevBestMID); /* show quant&genomic data
       * messages in MSGs & in plot */
    drawScatterPlot();
  } /* updateScatterPlot */
  
  
  /**
   * drawGifFile() - draw plot into Gif image file if in standalone mode.
   * This sets it up and lets paint() to the heavy lifting...
   * Returns false if unable to generate the image file.
   * @param oGifFileName is the full path GIF output file
   * @return true if successful
   * @see #repaint
   */
  boolean drawGifFile(String oGifFileName)
  { /* drawGifFile */
    if(mae.isAppletFlag || oGifFileName==null)
      return(false);
    
    drawIntoImageFlag= true;
    this.oGifFileName= oGifFileName;
    repaint();          /* will start the process */
    
    return(true);
  } /* drawGifFile */
  
  
  /**
   * drawScatterPlot() - draw scatter plot (repeatedly) of Filter data
   * @see CompositeDatabase#getNormHP_XandYdata
   * @see MathMAE#calcXYstat
   * @see MaHybridSample#getF1F2Data
   * @see MenuBarFrame#setHP_XYlabels
   * @see SampleSets#setHPxyModStrings
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see #update2Dplot
   */
  void drawScatterPlot()
  { /* drawScatterPlot */
   /* Setup mae.hps.(sMod, sModX, sModY) based on whether using
    * ratio mode or swapCy5Cy3DataFlags
    */
    mae.hps.setHPxyModStrings();
    eventHandlerCL= null;
    nPointsPlotted= 0;               /* # points plotted */
    prevBestMID= -2;                 /* starts things up */
    
    String
      topTitle= null,                /* Opt. other titling method */
      title1= null,
      title2= null,
      title= "",
      aText= null;
    int
      nList= 0,
      xText= 0,
      yText= 0;
    float
      xList[]= new float[map.maxGenes],    /* coordinates */
      yList[]= new float[map.maxGenes];
    int
      propList[]= new int[map.maxGenes];
    
    if(plotMode==mae.PLOT_HP_XY_INTENS)
    { /* scatter plot of HybridSample-X vs -Y */
      String
        xCaption= "HP-X" + mae.hps.sMod,
        yCaption= "HP-Y" + mae.hps.sMod;
      
      mae.mbf.setHP_XYlabels(); /* set GUI HP-X: and HP-Y: labels */
      title1= "  " + xCaption + ": " + cdb.hpXYdata.hpNameX + mae.hps.sModX;
      title2= "  " + yCaption + ": " + cdb.hpXYdata.hpNameY + mae.hps.sModY;
      topTitle=  "";
      
      if(mae.hps.nHP<2 || mae.msX==mae.msY)
      {
        Util.showMsg("Need different samples for HP-X vs HP-Y scatter plot");
        return;
      }
      nList= cdb.getNormHP_XandYdata(xList,yList, propList, mae.fc.displayCL,
                                     mae.msX, mae.msY, true, false);
      
      if(nList>0)
      { /* Get XY statistics on ONLY those which passed data Filter */
        aText= MathMAE.calcXYstat(nList,xList,yList,propList,"X","Y");
        Util.showMsg2(aText);
      }
      
      update2Dplot(yCaption, xCaption, /* i.e. "HP-Y", "HP-X", */
                   title, topTitle, title1, title2,
                   "  ["+mae.normNameDisp+"]",
                   aText, xText, yText,
                   1.0, 1.0,
                   cdb.minDataHP_X, cdb.maxDataHP_X /* X-axis */,
                   cdb.minDataHP_Y, cdb.maxDataHP_Y /* Y-axis */,
                   true       /* draw 45 line */,
                   false      /* draw 180 line */,
                   xList, yList, propList,
                   mae.fc.displayCL, nList, null);
    } /* scatter plot of HybridSample-X vs -Y */
    
    else if(plotMode==mae.PLOT_F1_F2_INTENS || plotMode==mae.PLOT_F1_F2_MVSA)
    { /* scatter plot of F1 vs F2 */
      /* NOTE: There are 5 submodes indicated by spp.popupName
       * The extra modes are ONLY available in ratio mode.
       *  spp.popupName                Semantics
       *  ==================           ==========
       *  "ScatterPlotF1F2"            HP cy3 vs cy5
       *  "Xcy3VsYcy3ScatrPlot"        HP-X cy3 vs HP-Y cy3
       *  "Xcy5VsYcy5ScatrPlot"        HP-X cy5 vs HP-Y cy5
       *  "Xcy3VsYcy5ScatrPlot"        HP-X cy3 vs HP-Y cy5
       *  "Xcy5VsYcy3ScatrPlot"        HP-X cy5 vs HP-Y cy3
       *  "MvsA-F1F2ScatrPlot"         M (log2 Cy5/Cy3) vs A (log 2 Cy5*Cy3)
       */
      ms= (mae.ms==mae.msX) ? mae.msX : mae.msY;
      String
        sf1= mae.cfg.fluoresLbl1,
        sf2= mae.cfg.fluoresLbl2,
        sMod= (ms==mae.msX) ? mae.hps.sModX : mae.hps.sModY,
        xCaption= "HP-X" + mae.hps.sMod,
        yCaption= "HP-Y" + mae.hps.sMod,
        vCaption,
        hCaption;
      
      if(mae.useCy5OverCy3Flag)
      { /* Flip Cy3/Cy5 to Cy5/Cy3 */
        String sTmp= sf1;
        sf1= sf2;
        sf2= sTmp;
      }
      
      mae.mbf.setHP_XYlabels(); /* set GUI HP-X: and HP-Y: labels */
      
     /* Get data There are 5 submodes indicated by spp.popupName
      * The extra modes are ONLY available in ratio mode.
      *  spp.popupName                Semantics
      *  ==================           ==========
      *  "ScatterPlotF1F2"            HP cy3 vs cy5
      *  "Xcy3VsYcy3ScatrPlot"        HP-X cy3 vs HP-Y cy3
      *  "Xcy5VsYcy5ScatrPlot"        HP-X cy5 vs HP-Y cy5
      *  "Xcy3VsYcy5ScatrPlot"        HP-X cy3 vs HP-Y cy5
      *  "Xcy5VsYcy3ScatrPlot"        HP-X cy5 vs HP-Y cy3
      */
      if(spp.popupName.equals("Xcy3VsYcy3ScatrPlot"))
      { /* HP-X cy3 vs HP-Y cy3 */
        vCaption= "HP-Y "+sf1;
        hCaption= "HP-X "+sf1;
        title1= "  " + xCaption + ": " + cdb.hpXYdata.hpNameX + "(Cy3)";
        title2= "  " + yCaption + ": " + cdb.hpXYdata.hpNameY + "(Cy3)";
        topTitle=  "";
        float trashList[]= new float[map.maxGenes];
        nList= mae.msX.getF1F2Data(xList, trashList, propList,
                                   mae.fc.displayCL, true, false, false);
        nList= mae.msY.getF1F2Data(yList, trashList, propList,
                                   mae.fc.displayCL, true, false, false);
      }
      else if(spp.popupName.equals("Xcy5VsYcy5ScatrPlot"))
      { /* HP-X cy5 vs HP-Y cy5 */
        vCaption= "HP-Y "+sf2;
        hCaption= "HP-X "+sf2;
        title1= "  " + xCaption + ": " + cdb.hpXYdata.hpNameX + "(Cy5)";
        title2= "  " + yCaption + ": " + cdb.hpXYdata.hpNameY + "(Cy5)";
        topTitle=  "";
        float trashList[]= new float[map.maxGenes];
        nList= mae.msX.getF1F2Data(trashList, xList, propList,
                                   mae.fc.displayCL, true, false, false);
        nList= mae.msY.getF1F2Data(trashList, yList, propList,
                                   mae.fc.displayCL, true, false, false);
      }
      else if(spp.popupName.equals("Xcy3VsYcy5ScatrPlot"))
      { /* HP-X cy3 vs HP-Y cy5 */
        vCaption= "HP-Y "+sf2;
        hCaption= "HP-X "+sf1;
        title1= "  " + xCaption + ": " + cdb.hpXYdata.hpNameX + "(Cy3)";
        title2= "  " + yCaption + ": " + cdb.hpXYdata.hpNameY + "(Cy5)";
        topTitle=  "";
        float trashList[]= new float[map.maxGenes];
        nList= mae.msX.getF1F2Data(xList, trashList, propList,
                                   mae.fc.displayCL, true, false, false);
        nList= mae.msY.getF1F2Data(trashList, yList, propList,
                                   mae.fc.displayCL, true, false, false);
      }
      else if(spp.popupName.equals("Xcy5VsYcy3ScatrPlot"))
      { /* HP-X cy5 vs HP-Y cy3 */
        vCaption= "HP-Y "+sf1;
        hCaption= "HP-X "+sf2;
        title1= "  " + xCaption + ": " + cdb.hpXYdata.hpNameX + "(Cy5)";
        title2= "  " + yCaption + ": " + cdb.hpXYdata.hpNameY + "(Cy3)";
        topTitle=  "";
        float trashList[]= new float[map.maxGenes];
        nList= mae.msX.getF1F2Data(trashList, xList, propList,
                                   mae.fc.displayCL, true, false, false);
        nList= mae.msY.getF1F2Data(yList, trashList, propList,
                                   mae.fc.displayCL, true, false, false);
      }
      else if(spp.popupName.equals("MvsA-F1F2ScatrPlot"))
      { /* M vs A plot of current sample */
        String sampleName= mae.hps.msList[mae.curHP].fullStageText;
        vCaption= "M (log2 "+sf1+"/"+sf2+")";
        hCaption= "A (log2 "+sf1+"*"+sf2+")";
        title1= "  " + vCaption + ": " + sampleName;
        title2= "  " + hCaption + ": " + sampleName;
        topTitle=  "";
        float
          trashList[]= new float[map.maxGenes],
          f1List[]= new float[map.maxGenes],
          f2List[]= new float[map.maxGenes];
        int
          fPropList[]= new int[map.maxGenes],
          fMidList[]= new int[map.maxGenes],
          nFound= mae.ms.getF1F2Data(trashList, f2List, fPropList,
                                      mae.fc.displayCL, true, false, false);
        nFound= mae.ms.getF1F2Data(f1List, trashList, fPropList,
                                    mae.fc.displayCL, true, false, false);
        /* Compute the M vs A data.
         * xList[] X axis [0:nFilteredGenes-1] data computed data A= log2(R*G)/2
         * A= [(Log10(R)/Log10(2) + log10(G)/log10(2) ]/2.
         *
         * yData[] axis [0:nFilteredGenes-1] data computed data M= log2(R/G)
         * M= [(Log10(R)/Log10(2) - log10(G)/log10(2) ]/2.
         * log10base2 is a constant
         */
        double
          R, G, A, M, 
          minA= 100000000000.0F, /* For computing max, min extrema for A and M */
          minM= 100000000000.0F,
          maxA= -100000000000.0F,
          maxM= -100000000000.0F,
          sumA= 0.0,
          sumM= 0.0,
          meanA,
          meanM,
          log10base2=  (float)MathMAE.log10(2.0);; /* log10(2.0) value */
          nList= 0;    /* build the list with non-zero entries */
        for(int i=0;i<nFound;i++)
        { /* Compute A and M */
          G= f1List[i];            /* F1 is Cy3 */
          R= f2List[i];            /* F2 is Cy5 */
          if(R==0.0 || G==0.0)
            continue;              /* ignore zero data */
          
          A= ((MathMAE.log10(R)/log10base2) + (MathMAE.log10(G)/log10base2))/2.0;
          M= ((MathMAE.log10(R)/log10base2) - (MathMAE.log10(G)/log10base2))/2.0;
          xList[nList]= (float)A;
          yList[nList]= (float)M;
          propList[nList]= fPropList[i];
          Gene gene= mae.fc.displayCL.mList[i];
          if(gene==null)
            continue;
          fMidList[nList++]= gene.gid;
          /* compute extrema and sums */
          if(maxA<A)
            maxA= (float)A;
          if(minA>A)
            minA= (float)A;
          sumA += A;
          if(maxM<M)
            maxM= (float)M;
          if(minM>M)
            minM= (float)M;
          sumM += M;
        } /* Compute A and M */
          
        /* Compute the means */
        meanA= (nList>0) ? (sumA/nList) : 0;
        meanM= (nList>0) ? (sumM/nList) : 0;   
        if(mae.CONSOLE_FLAG)
        { 
          /* e.g. DSP-DSP mn(A,M)=(0.708,0.107)
           * (minA:maxA)=(-3.376,7.246) (minM:maxM)=(-0.512,0.952)
           */
          System.out.println("DSP-DSP mn(A,M)=("+
                             Util.cvd2s(meanA,3)+","+Util.cvd2s(meanM,3)+")"+
                             " (minA:maxA)=("+
                             Util.cvd2s(minA,3)+","+Util.cvd2s(maxA,3)+")"+
                             " (minM:maxM)=("+
                             Util.cvd2s(minM,3)+","+Util.cvd2s(maxM,3)+")");
        }
        
        /* Move the A and M ranges up by (minA,minM) so min is 0 not negative for now
         * and fixup the display list.
         */
        mae.fc.displayCL.clear();
        int mid;
        Gene gene;
        for(int i=0;i<nList;i++)
        {
          if(minA<0)
            xList[i] += -minA;
          if(minM<0)
            yList[i] += -minM;
        
          /* Fixup the mae.fc.displayCL.mList[] - maybe we need a separate display list
           * so as not to interfer with pseudoarray plot
           */
          /* add only genes that are OK gene to displayCL gene list */
          mid= fMidList[i];
          gene= mae.mp.midStaticCL.mList[mid];
          if(gene!=null)
            mae.fc.displayCL.addGene(gene);
        }  /* Fix up the lists */
      } /* M vs A plot of current sample */
      
      else
      { /* i.e. "ScatterPlotF1F2" current HP cy3 vs cy5 */
        vCaption= sf1;
        hCaption= sf2;
        title= "Intensity values of "+sf1+ " vs "+sf2+" for same genes";
        topTitle= title;
        title1= " HP: " + ms.hpName + sMod;
        if(!ms.hpName.equals(ms.sampleID))
          title2= " " + ms.sampleID;
        nList= ms.getF1F2Data(xList, yList, propList,
                              mae.fc.displayCL, true, false, false);
      }
      
      if(mae.useCy5OverCy3Flag)
      { /* Flip Cy3/Cy5 to Cy5/Cy3 */
        float listTmp[]= yList;
        yList= xList;
        xList= listTmp;
      }
      
      if(nList>0)
      { /* compute statistics only on genes passing Filter */
        aText= MathMAE.calcXYstat(nList,xList,yList,propList, sf1,sf2);
        Util.showMsg2(aText);
      }
      
      update2Dplot(vCaption, hCaption, title,  topTitle, title1, title2,
                   " ["+mae.normNameDisp+"]", aText, xText, yText,
                   1.0, 1.0,
                   ms.minDataF1, ms.maxDataF1 /* X-axis */,
                   ms.minDataF2, ms.maxDataF2 /* Y-axis */,
                   true    /* draw 45 line */,
                   false   /* draw 180 line */,
                   xList, yList, propList,
                   mae.fc.displayCL, nList, null);
    } /* scatter plot of F1 vs F2 */
  } /* drawScatterPlot */
  
  
  /**
   * setCurGene() - set current gene and update data in displays
   * @param mid is the new Master Gene ID of the current gene
   * @see CompositeDatabase#setObjCoordFromMID
   * @see #showGeneMsgsAndPlot
   */
  void setCurGene(int mid)
  { /* stCurGene */
    if(mid==-1)
      return;
    int gid1= map.mid2gid[mid];
    if(gid1==-1)
      return;
    
    /* If Expression Profile mode is on, then draw it */
    cdb.setObjCoordFromMID(mid,spp);
    showGeneMsgsAndPlot(mid);
  } /* setCurGene */
  
  
  /**
   * showGeneMsgsAndPlot() - show quant&NCBI data messages in MSG and in plot
   * @param mid is the Master Gene Index if not -1
   * @see GridCoords#cvtGID2str
   * @see MaHybridSample#getSpotDataStatic
   * @see SpotFeatures#getSpotFeatures
   * @see SpotFeatures#getSpotGenomicData
   * @see Util#cvf2s
   * @see Util#showMsg
   * @see Util#showFeatures
   * @see #set2DplotSubtitles
   */
  void showGeneMsgsAndPlot(int mid)
  { /* showGeneMsgsAndPlot */
    if(mid<0)
      return;
    int gid1= map.mid2gid[mid];
    if(gid1==-1)
      return;
    
    ms.getSpotDataStatic(gid1,mae.useRatioDataFlag);
    /* lookup the XY coords of spot */
    Point xyGid1= SpotData.xyS;
    if(xyGid1==null)
      return;
    int bestI= 0;
    Gene
      gene,
      mList[]= eventHandlerCL.mList;
    
    for(int i=0;i<nPointsPlotted;i++)
    { /* find closest point */
      gene= mList[i];
      if(gene!=null && gene.mid==mid)
      { /* latch index */
        bestI= i;
        break;
      }
    } /* find closest point */
    
    boolean
      saveGangFlag= mae.gangSpotFlag;
    float
      fx= xList[bestI],                /* actual data */
      fy= yList[bestI],
      changeData= (mae.isZscoreFlag) ? (fx-fy) : (fx/fy);    
    int cngStrPrecision= (Math.abs(changeData)<0.0001) ? 7 : 5;
    
    /* If possible, force it to be true since need both data  */
    mae.gangSpotFlag= (mae.cfg.maxFIELDS>1);
    
    String
      sBkgrdTick= (!mae.bkgdCorrectFlag)
                      ? "" : "'",
    maCoords= mae.grcd.cvtGID2str(gid1,false),
    xygStr= maCoords +
            "X"+sBkgrdTick+"="+Util.cvf2s(fx,cvPrecision) + ", "+
            "Y"+sBkgrdTick+"="+Util.cvf2s(fy,cvPrecision);
    
    String
      sRatio= (mae.isZscoreFlag) ? ", (X-Y)=" : ", (X/Y)=",
      featuresStr= mae.sf.getSpotFeatures(xyGid1, mae.ms),
      genomicDataStr= mae.sf.getSpotGenomicData(xyGid1, mae.ms),
      numericDataPlot= xygStr + sRatio + Util.cvf2s(changeData,cngStrPrecision),
      numericData= numericDataPlot + ", ("+mae.normNameDisp+")";

    mae.gangSpotFlag= saveGangFlag; /* restore flag */
    
    Util.showMsg(numericData);
    Util.showFeatures(featuresStr, genomicDataStr);
    
    set2DplotSubtitles(numericDataPlot, featuresStr, genomicDataStr, null);
  } /* showGeneMsgsAndPlot */
  
  
  /**
   * showGeneOfInterest() - show quant&NCBI data if click on a point in plot
   * @param mid is the Master Gene Index if not -1
   * @param mouseKeyMods is Shift/Control key modifiers
   * @param setCurGeneFlag if true
   * @see PopupRegistry#updateCurGene
   * @see #repaint
   * @see #setCurGene
   * @see #showGeneMsgsAndPlot
   */
  void showGeneOfInterest(int mid, int mouseKeyMods, boolean setCurGeneFlag)
  { /* showGeneOfInterest*/
    if(mid==-1)
      return;
    
    if(setCurGeneFlag)
      setCurGene(mid);          /* set the current gene */
    
    showGeneMsgsAndPlot(mid);   /* show quant&NCBI data messages in MSGs
     * and in the plot */
    
      /* When change current gene in ms.objXXX. Then put things here to
       * do when change the gene which are side effects.
       * This includes modifying the 'editable gene list'
       */
    if(setCurGeneFlag)
      mae.pur.updateCurGene(mid, mouseKeyMods, (Object)spp);
    
    repaint();                  /* redraw this plot */
  } /* showGeneOfInterest*/
  
  
  /**
   * mouseHandler() - interact with current plot if a graphic plot.
   * If get a hit, then display data for the gene.
   * Note: because the plot lives in the ScrollerImageCanvas, there
   * is only ONE plot so can't get into trouble with multiple plots.
   * @param x is mouse x coordinate
   * @param y is mouse y coordinate
   * @param mouseKeyMods is Shift/Control key modifiers
   * @param setCurGeneFlag if true
   * @see #repaint
   * @see #set2DplotSubtitles
   * @see #showGeneOfInterest
   */
  public void mouseHandler(int x, int y, int mouseKeyMods,
  boolean setCurGeneFlag)
  { /* mouseHandler */
    if(eventHandlerCL==null || nPointsPlotted==0)
      return;
    
    int
      threshold= 3,                /* # pixels max can click away */
      thrSQ= threshold*threshold,
      bestI= -1,
      bestLSQerr= 100000000,
      bestMid= -1;
    Gene
      gene= null,
     mList[]= eventHandlerCL.mList;
    
    for(int i=0;i<nPointsPlotted;i++)
    { /* find closest point */
      int
        dx= (xPlotted[i]-x),
        dy= (yPlotted[i]-y),
        lsqErr= (dx*dx + dy*dy);
      
      if(bestLSQerr>lsqErr && mList[i]!=null && mList[i].mid!=-1)
      { /* latch onto better one */
        bestLSQerr= lsqErr;
        bestI= i;
        bestMid= mList[i].mid;
        gene= mList[i];
      }
    } /* find closest point */
    
    if(bestLSQerr<=thrSQ)
    { /* Found it!! - Go process */
      showGeneOfInterest(bestMid, mouseKeyMods,
      setCurGeneFlag);
      prevBestMID= bestMid;
    }
    else if(prevBestMID!=-1)
    { /* clear it out */
      set2DplotSubtitles(null,null,null,null);
      repaint();
      prevBestMID= -1;
    }
    return;
  } /* mouseHandler */
  
} /* end of class DrawScatterPlot */



