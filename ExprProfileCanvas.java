/** File: ExprProfileCanvas.java */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.lang.*;

/**
 * The ExprProfileCanvas is used to draw the expression profile. 
 * It also tracks locations of points drawn so they can be used with 
 * the event handler when the user clicks on a point or bar, etc.
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.11 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

class ExprProfileCanvas extends Canvas 
{
  /** instance of MAExplorer so we can clear flags indicating 
   * the user did a manual quit() */
  MAExplorer
    mae;
  /** expression profile for mid */  
  ExprProfile
    ep;                            
  
  /** draw vertical bars */
  final static int
    MODE_LINE= 0;                  
  /** draw circles for each entry */
  final static int
    MODE_CIRCLE= 1;                
  /** draw continuous curve */
  final static int
    MODE_CURVE= 2;                 
  /** default canvas width size */ 
  final static int
    MIN_CANVAS_WIDTH=  300;                 
  /** default canvas height size */        
  final static int       
    MIN_CANVAS_HEIGHT= 100; 
  /** target Size  for selecting a line */
  final static int      
    TARGET_SIZE= 2;		   
    
  /** preferred canvas height size */ 
  public int
    preferredHeight;
  /** preferred canvas width size */ 
  public int
    preferredWidth;
  private static Font
    font,
    titleFont;
  /** event handler mouse X cursor coordinates */
  int
    xCursor;  
  /** event handler mouse Y cursor coordinates */
  int
    yCursor;
   /** height font size of current font */
   private int
    fontHeight;
  /** leading font size of current font */
  private int
    fontLeading;
  /** ARG: opt. from user. If drawLabel */
  private String
    title;                         
  /** plot mode style: 0=line, 1=circle, 2=curve */
  int 
    plotStyleMode;                                          
  /** zoom magnification: by 1X, 2X, 5X, 10X, 20X mod */
  int 
    zoomBarsMag;                   
  /** report K-means data else just CloneID & name*/
  boolean
    showKmeansDataFlag;                               
  /** draw error bars for each line */
  boolean
    showErrBarsFlag;                                  
  /** mean else individual plots */ 
  boolean
    doMeanPlotsFlag;                                 
  /** set by setupData() if successful */
  boolean
    didDataSetupFlag;                                 
  /** ARG: draw labels for larger version */ 
  boolean
    drawLabelsFlag;                                  
  /** draw title & additional data if larger format. */ 
  boolean
    drawAdditionalDataFlag;          
  
  /** set if draw plot to GIF file*/
  boolean
    drawIntoImageFlag= false;  
  /** full Gif file path name */
  String
    oGifFileName;   
	     
	     
  /**
   * ExprProfileCanvas() - Constructor to display an expression profile in a canvas.
   *<PRE>
   * If mid >=0, then display gene info
   * If mid==-1, then display "request to click on a gene"
   * If mid==-2, then don't draw anything.
   *</PRE>
   * @param mae is the MAExplorer instance
   * @param mid is the Master Gene Index if not -1
   * @param title is the title for canvas label
   * @param font is the overide labels font if not null
   * @param preferredWidth is the canvas size, 0 to use defaults
   * @param preferredHeight
   * @param showKmeansDataFlag to draw K-means else gene data
   * @param drawLabelsFlag if true
   * @see ExprProfile#setupData
   */
  ExprProfileCanvas(MAExplorer mae, int mid, String title, Font font,
                    int preferredWidth,  int preferredHeight,
                    boolean showKmeansDataFlag, boolean drawLabelsFlag)
  { /* ExprProfileCanvas */
    this.mae= mae;
    
    ep= new ExprProfile(mae,mid,false);   /* where the data is kept */
    this.title= title;
    if(font!=null)
      this.font= font;
    else
      this.font= new Font(mae.cfg.fontFamily, Font.PLAIN, 10);
    if(titleFont==null)
      titleFont= new Font(mae.cfg.fontFamily, Font.PLAIN, 12);
    
    Dimension epcSize= getMinimumSize();
    this.preferredWidth= (preferredWidth!=0) ? preferredWidth : epcSize.width;
    this.preferredHeight= (preferredHeight!=0) ? preferredHeight : epcSize.height;
    
    this.showKmeansDataFlag= showKmeansDataFlag;
    this.drawLabelsFlag= drawLabelsFlag;
    
    /* Setup here to save space if multiple instances */
    ep.didDataSetupFlag= false;
    drawAdditionalDataFlag= false;
    doMeanPlotsFlag= false;   /* setup with separate call */
    showErrBarsFlag= true;    /* draw error bars for each line */
    zoomBarsMag= 1;           /* 1X magnification is default */
    doMeanPlotsFlag= false;   /* setup with separate call */
    
    if(ep.mid<=-1)
      return;
    
    ep.setupData(mid);
  } /* ExprProfileCanvas */
  
  
  /**
   * drawGifFile() - draw expression  plot into Gif image file if in standalone mode.
   * This sets it up and lets paint() to the heavy lifting...
   * @param oGifFileName is the full path GIF output file
   * @return false if unable to generate the image file.
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
   * getPreferredSize() - get the preferred size
   * @return window size
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(new Dimension(preferredWidth, preferredHeight));
  } /* getPreferredSize */
  
  
  /**
   * getMinimumSize() - get the minimum preferred size
   * @return window size
   */
  public Dimension getMinimumSize()
  { /* getMinimumSize */
    int
    width= (int)(MIN_CANVAS_WIDTH*(0.95+(mae.hps.nHP_E/50)));
    return(new Dimension(width, MIN_CANVAS_HEIGHT));
  } /* getMinimumSize */
  
  
  /**
   * updateData() - update expression profile plot with new gene data.
   * Only repaint if a legal gene MID.
   * @param mid is the Master Gene Index if not -1
   * @param title is the title for canvas label
   * @param drawLabelsFlag if true
   * @see ExprProfile#setupData
   * @see #repaint
   */
  boolean updateData(int mid, String title, boolean drawLabelsFlag )
  { /* updateData */
    ep.mid= mid;
    this.title= title;
    this.drawLabelsFlag= drawLabelsFlag;
    xCursor= 0;
    yCursor= 0;
    boolean flag= ep.setupData(mid);
    repaint();
    return(flag);
  } /* updateData */
  
  
  /**
   * setPlotEPmeansData() - set expression profile data so plot mean EP data.
   * @param mid is the Master Gene Index if not -1
   * @param hpDataNbr is the # HP-E members
   * @param maxHPdataMn is the max value in vector
   * @param hpDataMn is the Mean HP member quant data
   * @param hpDataSD is the S.D. HP member quant data
   * @param doMeanPlotsFlag if true
   * @see ExprProfile#updateData
   * @see #repaint
   */
  void setPlotEPmeansData(int mid, int hpDataNbr, float maxHPdataMn,
                          float hpDataMn[], float hpDataSD[],
                          boolean doMeanPlotsFlag)
  { /* setPlotEPmeansData */
    /* update ExprProfile object belonging to ExprProfileCanvas */
    if(mid==-1)
      mid= ep.mid;                /* use current mid */
    ep.updateData(mid, doMeanPlotsFlag, hpDataNbr,
    maxHPdataMn, hpDataMn, hpDataSD);
    repaint();
  } /* setPlotEPmeansData */
  
  
  /**
   * paint() - draw the expression profile in the canvas if a legal gene MID.
   * @param g is graphics context
   * @see CompositeDatabase#setObjCoordFromMID
   * @see PopupRegistry#chkOtherCurGeneEffects
   * @see SpotFeatures#showValidSpotInfo
   * @see Util#cvf2s
   * @see WriteGifEncoder
   * @see WriteGifEncoder#writeFile
   * @see #repaint
   */
  public void paint(Graphics g)
  { /* paint */
    FontMetrics
      fm= g.getFontMetrics(font),
      fmT= g.getFontMetrics(titleFont);
    float
      botRatio= (drawLabelsFlag) ? 0.65F  /* was 0.80F */ : 0.95F;
    int
      fontHeight= fm.getHeight(),
      fontWidth= (fm.getMaxAdvance()==-1) ? 5 : fm.getMaxAdvance(),
      fontLeading= fm.getLeading(),
      textPixels= fontHeight+fontLeading,
      titlePixels= fmT.getHeight()+fmT.getLeading(),
      yTop=   (int)(0.05*preferredHeight),
      yBot=   (int)(botRatio*preferredHeight),
      xRight= (int)(0.95*preferredWidth),
      xLeft=  (int)(0.05*preferredWidth),
      scaleMarkOverhang= 5,   /* scale markers size*/
      longOverhang= 6,        /* where HP #s on x-axis */
      shortOverhang= 3,       /* where no HP #s on x-axis */
      lineSpacing= 4,         /* pixels between vertical lines size*/
      xTopText= 0,
      yTopText= 0,
      xBotText= 0,
      yBotText= 0;
    
    if(drawLabelsFlag)
    { /* make sure space for text*/
      if(yTop<textPixels)
        yTop= textPixels;     /* Math.max(textPixels, yTop) */
      yBot= Math.min((yBot-textPixels+scaleMarkOverhang),
      preferredHeight);
      xTopText= 25;
      yTopText= textPixels+3;
      xBotText= xLeft;
      yBotText= Math.min((int)(yBot+2*textPixels), preferredHeight);
    }
    
    int
      xRange= (xRight-xLeft),
      yRange= (yBot-yTop),
      x1, y1,
      x2, y2;
    
    /* [1] If draw plot into GIF image file, setup new Graphics g. */
    Image gifImage= null;
    Graphics origG= g;         /* for debugging */
    if(drawIntoImageFlag)
    { /* draw into GIF file Image instead of canvas */
      int
        w= preferredWidth,
        h= preferredHeight;
      gifImage= createImage(w,h);
      g= gifImage.getGraphics();
    }
    
    /* [2] Always start fresh with a clean  background */
    setBackground(Color.white);
    g.clearRect(0,0, preferredWidth, preferredHeight);
    if(ep.mid==-2)
      return;                   /* No-op */
    
    /* [3] test if there is data to display yet */
    if(!ep.didDataSetupFlag)
    { /** no data yet */
      if(drawLabelsFlag)
      {
        g.setFont(titleFont);
        x1= 40;
        y1= (preferredHeight/2);
        g.drawString("Click on a gene, then drag the mouse", x1, y1);
        y1 += titlePixels;
        g.drawString("to a data line in profile plot.", x1, y1);
      }
      return;
    }
    
    /* [4] Draw axes and labels.
     * Clear the region each time.
     * (Top) Gene Title
     * (Top line 2)additional data
     * Vertical:    "Intensity" (or nothing)
     * Horizontal:
     *   compressed:     "# 1....5....10....15..." (or nothing)
     */
    g.setColor( Color.black );
    
    x1= Math.max(0, (xLeft-scaleMarkOverhang));
    y1= yBot;
    x2= xRight;
    g.drawLine( x1,y1, x2,y1);    /* X-axis */
    
    x1= xLeft;
    y1= Math.min(preferredHeight,(yBot+scaleMarkOverhang));
    y2= yTop-2;                   /* looks better if a little longer*/
    g.drawLine( x1,y1, x1,y2);    /* Y-axis */
    
    for(int i=0;i<=4;i++)
    { /* draw scale marks on y axis */
      x1= xLeft;
      x2= (x1-2);
      if((i&01)==0)
        x2 -= 2;                     /* make a little longer line */
      if(x2<0)
        x2= 0;                      /* Math.max(0,x2) */
      y1= yBot - (i*yRange)/4;
      g.drawLine( x1,y1, x2,y1);    /* Y-axis scale marks */
    }
    
    /* [5] Draw vertical bars centered over each of the labels. */
    int
      diff,
      dX,
      dYmax,                     /* mean + SD */
      dYmin,                     /* mean - SD */
      dY,                        /* i.e. [0:yRange]*/
      oldX1= -1,                 /* previous point for drawing lines */
      oldY2= -1,
      nHP_E= ep.nHP_E;
    float
      slope,
      baseline,
      maxIntensity,
      mean,          /* in EP space */
      stddev,        /* in EP space */
      hpDatum,       /* current data for ith line */
      hpSD,
      hpDatumMax,
      hpDatumMin;
    boolean iMod5flag;
    MaHybridSample
      msListE[]= mae.hps.msListE,
      ms;
    
    /* [5.1] Compute global transform for all samples to be plotted */
    for(int i=0;i<nHP_E;i++)
    { /* draw vertical bar for each HP */
      /* Map EP data to [0:1] */
      iMod5flag= (i==0 || (i>2 && ((i+1)%5)==0));
      dX= lineSpacing;
      
      if(ep.doMeanPlotsFlag)
      { /* mean plots */
        mean= ep.hpDataMn[i];
        stddev= ep.hpDataSD[i];
      }  /* mean plots */
      else
      { /* single gene plots */
        mean= ep.hpData[i]; /* individual plots */
        stddev= (mean*ep.cvData[i]);
      }
      
      /* [5.1.1] Scale norm intensity space to [0:1.0] for each HP */
      ms= msListE[i+1];
      maxIntensity= 1.0F;     /* range AFTER scale */
      slope= 1.0F/(ms.maxDataS - ms.minDataS);
      baseline= -ms.minDataS*slope;
      
      /* [5.1.2] scale points for HP[i]. */
      hpDatum= slope*mean + baseline;
      hpSD= slope*stddev + baseline;
      if(hpSD<0)
        hpSD= -hpSD;                  /* use abs value */
      
      hpDatumMax= hpDatum-hpSD;       /* plot reversed y */
      hpDatumMin= hpDatum+hpSD;
      
      /* [5.1.3] magnify the line for viewing low intensity data */
      hpDatum *= zoomBarsMag;
      hpDatumMax *= zoomBarsMag;
      hpDatumMin *= zoomBarsMag;
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("EPC-P.3 mid="+ep.mid+" i="+i+
                         " mn="+Util.cvf2s(mean,2)+
                         " sd="+Util.cvf2s(stddev,2)+
                         " minDS="+Util.cvf2s(ms.minDataS,2)+
                         " maxDS="+Util.cvf2s(ms.maxDataS,2)+
                         "\n maxInt="+Util.cvf2s(maxIntensity,2)+
                         " slope="+Util.cvf2s(slope,4)+
                         " bLine="+Util.cvf2s(baseline,2)+
                         "\n hpD="+Util.cvf2s(hpDatum,2)+
                         " hpSD="+Util.cvf2s(hpSD,2)+
                         " hpDMin="+Util.cvf2s(hpDatumMin,2)+
                         " hpDMax="+Util.cvf2s(hpDatumMax,2));
       */
      
      /* [5.1.4] Clip data to size of window */
      if(hpDatum>maxIntensity)
        hpDatum= maxIntensity;     /* clip it */
      else if(hpDatum<0)
        hpDatum= 0;
      if(hpDatumMax>maxIntensity)
        hpDatumMax= maxIntensity;  /* clip it */
      else if(hpDatumMax<0)
        hpDatumMax= 0;
      if(hpDatumMin>maxIntensity)
        hpDatumMin= maxIntensity;  /* clip it */
      else if(hpDatumMin<0)
        hpDatumMin= 0;
      
      /* [5.1.5] Map scaled HP data to drawing window */
      dY= (int)((hpDatum*yRange)/maxIntensity);   /*i.e. [0:yRange]*/
      dYmax= (int)((hpDatumMax*yRange)/maxIntensity);
      dYmin= (int)((hpDatumMin*yRange)/maxIntensity);
      
      scaleMarkOverhang= (iMod5flag) ? longOverhang : shortOverhang;
      x1= xLeft + lineSpacing + i*dX;
      y1= (iMod5flag) ? (yBot+shortOverhang) : yBot;
      y2= yBot - dY;
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("EPC-P.3.1 mid="+ep.mid+
                         " hpD="+Util.cvf2s(hpDatum,2)+
                         " hpSD="+Util.cvf2s(hpSD,2)+
                         " hpDMin="+Util.cvf2s(hpDatumMin,2)+
                         " hpDMax="+Util.cvf2s(hpDatumMax,2)+
                         "\n yRange="+yRange+
                         " dY="+dY+
                         " dYmin="+dYmin+
                         " dYmax="+dYmax+
                         "\n x1="+x1+" y1="+y1+" y2="+y2+
                         " oldX1"+oldX1+" oldY2="+oldY2+
                         "\n");
       */
      
      /* [5.1.6] Draw the vertical bar in green if selectd, else black*/
      diff= (xCursor - ep.xCoord[i]);
      if(diff<0)
        diff= -diff;    /* Math.abs(xCursor - ep.xCoord[i]) */
      if(diff < TARGET_SIZE)
        g.setColor( (mae.useDichromasyFlag) ? Color.blue : Color.green );
        
        if(plotStyleMode==MODE_LINE)
        { /* profile line for HP[i] */
          g.drawLine( x1, y1, x1, y2);
        }
        else if(plotStyleMode==MODE_CIRCLE)
        { /* draw a small circle */
          g.drawArc( x1-2, y2-2, 3, 3, 0, 360 );
        }
        else if(plotStyleMode==MODE_CURVE)
        { /* draw a line between circles */
          g.drawArc( x1-1, y2-1, 2, 2, 0, 360 );
          g.setColor( Color.black );
          if(oldY2!=-1)
            g.drawLine( oldX1, oldY2, x1, y2);
          oldY2= y2;   /* save for drawing next line */
          oldX1= x1;
        }
        g.setColor( Color.black );
        
        if(showErrBarsFlag)
        { /* mean plots - draw error bars*/
          g.setColor( (mae.useDichromasyFlag) ? Color.orange : Color.red);
          g.drawLine( x1, (y1-dYmax), x1, y2);  /* max line */
          g.drawLine( x1, (y1-dYmin), x1, y2);  /* min line */
          g.setColor( Color.black );
          g.drawLine( x1-2, y2, x1+2, y2);      /* mean marker */
        } /* mean plots - draw error bars*/
        
        ep.xCoord[i]= x1;                   /* save for use w/event handler*/
        ep.y1Coord= yBot;                   /* save for use w/event handler*/
        
        /* [5.1.7] draw labels */
        if(drawLabelsFlag)
        {
          y1= yBot + scaleMarkOverhang + (textPixels/2);
          x1 -= 3;
          g.setFont(font);
          if(iMod5flag)
            g.drawString((""+(i+1)), x1,y1);
        }
    } /* draw vertical bar for each HP */
    
   /* [6] If display X cursor is very close to vertical data bar,
    *   #i, hpName[], hpData[] and cvData[] value,
    * then do it.
    * [TODO] optimize placement and size of text...
    */
    if(drawLabelsFlag)
    { /* finish process event */
      g.setFont(titleFont);
      if(drawAdditionalDataFlag)
      { /* draw additional data */
        String msg= "";
        drawAdditionalDataFlag= false;
        if(yCursor > ep.y1Coord)
        { /* set the current gene and update Status */
          mae.cdb.setObjCoordFromMID(ep.mid,null);
          Point xyObj= new Point(mae.cdb.objX, mae.cdb.objY);
          /* when change current gene in ms.objXXX.
           * Then put things here to  do when change the gene which
           * are side effects. This includes modifying the
           * 'editable gene list'
          */
          mae.pur.chkOtherCurGeneEffects(ep.mid, 0 /*mouseKeyMods*/);
          mae.sf.showValidSpotInfo(xyObj,mae.ms);
        } /* set the current gene and update Status */
        
        else
        { /* draw additional information */
          for(int i=0;i<ep.nHP_E;i++)
          {
            diff= (xCursor - ep.xCoord[i]);
            if(diff<0)
              diff= -diff;    /* Math.abs(xCursor - ep.xCoord[i]) */
            if(diff < TARGET_SIZE)
            { /* draw additional info */
              drawAdditionalDataFlag= true;
              msg= ep.hpLabel[i];
            } /* draw additional info */
          }
        } /* draw additional information */
        g.setColor( (mae.useDichromasyFlag) ? Color.orange : Color.red );
        g.drawString(msg, xTopText, yTopText);
        g.setColor( Color.black );
      } /* draw additional data */
      
      /* Add draw "N-clusters=xxxx" if not 0 */
      String
        msg= title,
        msg2= null;
      int
        ncCnt= 0,
        nodeNbr= -1;
      Gene gene= mae.mp.midStaticCL.mList[ep.mid];
      if(gene!=null)
      {
        ncCnt= gene.nGeneClustersCnt;
        nodeNbr= gene.clusterNodeNbr;
      }
      
      if(showKmeansDataFlag && nodeNbr>0)
      { /* is member of a cluster */
        float dist= gene.data;
        String distStr= (dist<100.0) ? Util.cvf2s(dist,4) : (""+(int)dist);
        if(ncCnt>0)
        { /* is Kmeans medoid node */
          ClusterGenes cg= mae.clg;
          float
            mnWCD= cg.mnWithinClusterDist[nodeNbr],
            sdWCD= cg.sdWithinClusterDist[nodeNbr],
            cvWCD= sdWCD/mnWCD;
          msg= "Cluster #"+nodeNbr + " [" + ncCnt +
               " genes] [nearest cluster dist: "+distStr + "]" ;
          msg2= "Within cluster dist: mn+-sd=" +
                Util.cvf2s(mnWCD,3) + "+-" +Util.cvf2s(sdWCD,3)+
                " CV="+Util.cvf2s(cvWCD,3);
        } /* is Kmeans medoid node */
        else
        {
          msg += ", cluster#" + nodeNbr + ", distToClstr=" + distStr;
          msg2= gene.Gene_Name;
        }
      }	/* is member of a cluster */
      else
        msg2= gene.Gene_Name;
      
      if(msg!=null)
      { /* draw title */
        if(nodeNbr>0 && ncCnt>0)
          g.setColor( (mae.useDichromasyFlag) ? Color.orange : Color.red );
        else if(ep.mid==mae.cdb.objMID)
          g.setColor( (mae.useDichromasyFlag) ? Color.blue : Color.green );
        else
          g.setColor( (mae.useDichromasyFlag) ? Color.black : Color.blue );
          g.drawString(msg, xBotText, yBotText);
          if(msg2!=null)
            g.drawString(msg2, xBotText, yBotText+fontHeight);
          g.setColor( Color.black );
      } /* draw title */
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("EPCanvas-paint ep.mid="+ep.mid+
                         " title='"+title+"'\n"+ " msg='"+msg+"\n"+
                         " msg2='"+msg2);
       */
    } /* finish process event */
    
    /* [7] If drawing to a GIF file, then cvt Image to Gif stream
     * and write it out.
     */
    if(drawIntoImageFlag && gifImage!=null)
    { /* write it out */
      drawIntoImageFlag= false;
      WriteGifEncoder wge= new WriteGifEncoder(gifImage);
      gifImage= null;
      if(wge!=null)
        wge.writeFile(oGifFileName);
      
      repaint();   /* refresh the actual canvas */
    } /* write it out */
    
  } /* paint */
  
  
} /* end of ExprProfileCanvas class */

