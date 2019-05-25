/** File: SampleSets.java */

import java.awt.*;
import java.applet.*;
import java.awt.image.*;
import java.awt.event.*;

/** 
 * The SampleSets class maintains the set of all HP samples, the HP-X set,  HP-Y set and the HP-E list. 
 * <PRE>
 *<B> List of sets of MaHybridSample sample instances in this class </B> 
 *   msList[1:nHP]     is the set of all samples currently loaded in the database     
 *   msListX[1:nHP_X]  is the HP-X set samples  
 *   msListY[1:nHP_Y]  is the HP-Y set samples  
 *   msListE[1:nHP_E]  is the HP-E set samples 
 *</PRE>
 * The HPxyData class is used for computing means, stdDev, CVs for the HP-X
 * and HP-Y sets.
 *<P>
 * The PopupHPChooser and PopupHPmenuGuesser classes create user GUI popup 
 * windows that are used to change these data structures.
 *<P>
 * The HP-E list is used for defining expression profiles (@see ExprProfile)
 * that is used in expression profile plots and for computing cluster distances
 * used in clustering methods.
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ExprProfile
 * @see HPxyData
 * @see MaHybridSample
 * @see PopupHPChooser
 * @see PopupHPmenuGuesser
 */

class SampleSets 
{
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;                   
    
  /** The following are the order of the top level menus
   * in the menu tree. NOTE that the key words are in the
   * Samples Table "Source" or "Probe" fields.
   */
  String
    topSubMenuNames[];
  /** number of top level menus */
  int
    nTopSubMenus;
    
  /** X-axis [1:nHP_X] */
  static MaHybridSample
    msListX[];                 
  /** Y-axis [1:nHP_Y] */  
  static MaHybridSample
    msListY[];               
  /** Expression Profile [1:nHP_E]*/  
  static MaHybridSample
    msListE[];               
  /** total HP list [1:nHP] for all active samples */
  static MaHybridSample
    msList[]; 
      
  /** list (set) Samples for X-axis msListX[]*/
  static int
    nHP_X;		      
  /** list (set) Samples for Y-Axis msListY[]*/
  static int
    nHP_Y;		      
  /** list H.P. Samples for expr.profiles msListE[]*/
  static int
    nHP_E;		      
  /** number of Samples in msList[] */
  static int
    nHP;                      
  
  /** <PARAM NAME=RatioHP VALUE=4> 1/msRatioHP for ratio EP calcs.*/
  int
   idxRatioHP;               
   
  /** prefix title "" or " 'set'" */
  String
    sMod;                    
  /** HP-X epilog "", sHPsetCy53, sCy35, sCy53 */
  String
    sModX;                   
  /** HP-Y epilog "", sHPsetCy53, sCy35, sCy53 */
  String
    sModY;                   
  /** either sModX or sModY depending on mode*/
  String
    sModXY;                  
  /** "Cy3/Cy5" based on mae.cfg.fluoresLbl[12] */
  String
    sCy35;                   
  /** "Cy5/Cy3" based on mae.cfg.fluoresLbl[12] */
  String
    sCy53;                   
  /** may have " some HPs ("+sCy53+") swapped" */
  String
    sHPsetCy53;              
	    
  /** Names of samples for Cy5/Cy3 swap */
  private String
    nameList[];	
  /** list for Cy5/Cy3 swap */
  private boolean
    valueList[];	
  /** originbal list for Cy5/Cy3 swap if cancel */
  private boolean 
    oldValueList[]; 
    
      
 
  /**
   * SampleSets() - constructor to create the sample lists.
   * @param mae is MAExplorer instance
   * @see MaHybridSample
   */
  SampleSets(MAExplorer mae)
  { /* SampleSets */
    this.mae= mae;
    
    /* Define the hardwired HP-X, HP-Y and HP-E lists */
    msListX= new MaHybridSample[mae.MAX_HYB_SAMPLES+1];
    msListY= new MaHybridSample[mae.MAX_HYB_SAMPLES+1];
    msListE= new MaHybridSample[mae.MAX_HYB_SAMPLES+1];
    msList= new MaHybridSample[mae.MAX_HYB_SAMPLES+1];
    nHP_X= 0;
    nHP_Y= 0;
    nHP_E= 0;
    nHP= 0;
  } /* SampleSets */   
   
  
  /**
   * lookupHPsampleNbr() - lookup MaHybidSample's sampleNbr.
   * Make sure it is legal return sampleNbr.
   * @param ms is MaHybridSample. If null, return current HP
   * @return corresponding sampleNbr else -1 if not found.
   */
  static int lookupHPsampleNbr(MaHybridSample ms)
  { /* lookupHPsampleNbr */
    if(ms==null)
      return(MAExplorer.curHP);
    
    int sampleNbr= -1;
    for(int i=1;i<=nHP;i++)
      if(ms==msList[i])
      {
        sampleNbr= i;
        break;
      }
    
    return(sampleNbr);
  } /* lookupHPsampleNbr */
  
  
  /**
   * addHPtoDB() - set HP sample to active database entry i
   * @param ms is sample to replace at entry i
   * @param i is position of sample list to replace
   * @return true if succeed.
   */
  boolean addHPtoDB(MaHybridSample ms, int i)
  { /* addHPtoDB */
    if(ms.needLoginFlag)
      return(false);   /* bad data */
    
    if( msList==null || i<1 || i>mae.MAX_HYB_SAMPLES)
      return(false);
    
    msList[i]= ms;
    if(nHP<i)
      nHP= i;                   /* Math.max(i,nHP) */
    return(true);
  } /* addHPtoDB */
  
  
  /**
   * addNewHPtoDB() - add new HP sample to end of active database list
   * @param ms is sample to add
   * @return true if succeed.
   */
  boolean addNewHPtoDB(MaHybridSample ms)
  { /* addNewHPtoDB */
    if(ms.needLoginFlag)
      return(false);   /* bad data */
    
    boolean inListFlag= false;
    for(int i=1;i<=nHP;i++)
      if(msList[i]==ms)
      {
        inListFlag= true;
        break;
      }
    if(inListFlag)
    { /* add it to list */
      msList[++nHP]= ms;
    }
    return(inListFlag);
  } /* addNewHPtoDB */
  
  
  /**
   * addHPtoXset() - add HP sample to HP-X set
   * @param ms is sample to add to HP-X set
   * @return true if succeed.
   * @see MenuBarFrame#setHP_XYlabels
   * @see Util#showMsg
   * @see #assignHPtoAxis
   */
  boolean addHPtoXset(MaHybridSample ms)
  { /* addHPtoXset */
    if(ms.needLoginFlag)
      return(false);   /* bad data */
    
    boolean inListFlag= false;   /* is in the X list already */
    
    for(int i=1;i<=nHP;i++)
      if(msList[i]==ms)
      { /* is it loaded in total HP list? */
        mae.curHP_X= i;  /* just switch it */
        break;
      }
    for(int i=1;i<=nHP_X;i++)
      if(msListX[i]==ms)
      { /* is it in the HP-X set? */
        inListFlag= true;
        mae.curHP_X= i;  /* just switch it */
        break;
      }
    if(!inListFlag)
    { /* add it to list */
      msListX[++nHP_X]= ms;
      assignHPtoAxis(mae.curHP_X, mae.X_AXIS, ms);
      Util.showMsg("Adding '"+ ms.hpName + "' to HP-X");
    }
    mae.msX= ms;
    mae.mbf.setHP_XYlabels();     /* set GUI HP-X: & HP-Y: labels */
    return(inListFlag);
  } /* addHPtoXset */
  
  
  /**
   * addHPtoYset() - add HP sample to HP-Y set
   * @param ms is sample to add to HP-Y set
   * @return true if succeed.
   * @see MenuBarFrame#setHP_XYlabels
   * @see Util#showMsg
   * @see #assignHPtoAxis
   */
  boolean addHPtoYset(MaHybridSample ms)
  { /* addHPtoYset */
    if(ms.needLoginFlag)
      return(false);   /* bad data */
    
    boolean inListFlag= false;
    for(int i=1;i<=nHP;i++)
      if(msList[i]==ms)
      { /* is it loaded in total HP list? */
        mae.curHP_Y= i;
        break;
      }
    for(int i=1;i<=nHP_Y;i++)
      if(msListY[i]==ms)
      { /* is it in the HP-Y set? */
        inListFlag= true;
        mae.curHP_Y= i;
        break;
      }
    if(!inListFlag)
    { /* add it to list */
      msListY[++nHP_Y]= ms;
      assignHPtoAxis(mae.curHP_Y, mae.Y_AXIS, ms);
      Util.showMsg("Adding '"+ ms.hpName + "' to HP-Y");
    }
    mae.msY= ms;
    mae.mbf.setHP_XYlabels();            /* set GUI HP-X: & HP-Y: labels */
    return(inListFlag);
  } /* addHPtoYset */
  
  
  /**
   * addHPtoElist() - add HP sample to end of HP-E list
   * @param ms is sample to add to HP-E list
   * @return true if succeed.
   * @see Util#showMsg
   */
  boolean addHPtoElist(MaHybridSample ms)
  { /* addHPtoElist */
    if(ms.needLoginFlag)
      return(false);   /* bad data */
    
    boolean
    inListFlag= false;
    for(int i=1;i<=nHP_E;i++)
      if(msListE[i]==ms)
      {
        inListFlag= true;
        break;
      }
    if(!inListFlag)
    { /* add it to list */
      msListE[++nHP_E]= ms;
      Util.showMsg("Adding '"+ ms.hpName + "' to HP-E");
    }
    return(inListFlag);
  } /* addHPtoElist */
  
  
  /**
   * rmvHPfromXset() - remove HP sample from HP-X set
   * @param ms is sample to remove from HP-X set
   * @return true if succeed.
   * @see Util#showMsg
   */
  boolean rmvHPfromXset(MaHybridSample ms)
  { /* rmvHPfromYset */
    boolean flag= false;
    if(nHP_X>0)
    { /* remove from the list */
      int
      k,
      j= nHP_X;
      for(int i=1;i<=nHP_X;i++)
        if(msListX[i]==ms)
        { /* already in list */
          j= i;
          for(k=1;k<nHP_X;k++)
            msListX[k]= msListX[k+1];
          nHP_X--;
          flag= true;
          break;
        }
      if(flag)
        Util.showMsg("Removing '"+ ms.hpName + "' from HP-X");
    }
    return(flag);
  } /* rmvHPfromXset */
  
  
  /**
   * rmvHPfromYset() - remove HP sample from HP-Y set
   * @param ms is sample to remove from HP-Y set
   * @return true if succeed.
   * @see Util#showMsg
   */
  boolean rmvHPfromYset(MaHybridSample ms)
  { /* rmvHPfromYset */
    boolean flag= false;
    if(nHP_Y>0)
    { /* remove from the list */
      int
      k,
      j= nHP_Y;
      for(int i=1;i<=nHP_Y;i++)
        if(msListY[i]==ms)
        { /* already in list */
          j= i;
          for(k=1;k<nHP_Y;k++)
            msListY[k]= msListY[k+1];
          nHP_Y--;
          flag= true;
          break;
        }
      if(flag)
        Util.showMsg("Removing '"+ ms.hpName + "' from HP-Y");
    }
    return(flag);
  } /* rmvHPfromYset */
  
  
  /**
   * rmvHPfromElist() - remove HP sample from HP-E list
   * @param ms is sample to remove from HP-E list
   * @return true if succeed.
   * @see Util#showMsg
   */
  boolean rmvHPfromElist(MaHybridSample ms)
  { /* rmvHPfromElist */
    boolean flag= false;
    if(nHP_E>0)
    { /* remove from the list */
      int
      k,
      j= nHP_E;
      for(int i=1;i<=nHP_E;i++)
        if(msListE[i]==ms)
        { /* already in list */
          j= i;
          for(k=1;k<nHP_E;k++)
            msListE[k]= msListE[k+1];
          nHP_E--;
          flag= true;
          break;
        }
      if(flag)
        Util.showMsg("Removing '"+ ms.hpName + "' from HP-E");
    }
    return(flag);
  } /* rmvHPfromElist */
  
  
  /**
   * assignHPtoAxis() - assign  sample to current HP-X or HP-Y sample
   * This is the default.
   * @param n is the sampleNbr to set to mae.curHP
   * @param axis is either mae.X_AXIS or mae.Y_AXIS
   * @param ms is HP sample to assign
   */
  private void assignHPtoAxis(int n, int axis, MaHybridSample ms)
  { /* assignHPtoAxis */
    MaHybridSample oldms= ms;		/* for possible garbage collecting */
    
    mae.curHP= n;	/* default */
    mae.ms= msList[n];
    
    if(axis==mae.X_AXIS)
    {
      mae.curHP_X= n;
      mae.msX= msList[n];
    }
    else
      if(axis==mae.Y_AXIS)
      {
        mae.curHP_Y= n;
        mae.msY= msList[n];
      }
  } /* assignHPtoAxis */
  
  
  /**
   * setHPlistsFromPARAM() - set msList{X|Y|E}[] from PARAM cfg.{X|Y|E}list.
   * Also sets limits for msListX[1:nHP_X], msListY[1:nHP_Y] and
   * msListE[1:nHP_X].
   * Set the default current HP-X and HP-Y to the first entries
   * on the Xlist and Ylist PARAM lists (if any) using the map entries
   * idxListX[0] for curHP_X and idxListY[0] for curHP_Y.
   * Handles case where there is NO intial data!
   * @return true if succeed
   */
  boolean setHPlistsFromPARAM()
  { /* setHPlistsFromPARAM */
    
    /* [1] Default to no initial data! */
    mae.curHP_X= -1;
    mae.curHP_Y= -1;
    mae.msX= null;
    mae.msY= null;
    
    if(msList==null)
    { /* msList no good */
      idxRatioHP= -1;
      return(false);
    }
    
      /* [2] Converts Xlist, Ylist, Elist strings to int[]
       * for use as index below.
       */
    int
      maxSamples= mae.MAX_HYB_SAMPLES+1,
      idxListX[]= Util.cvs2iArray(mae.cfg.Xlist, maxSamples, ","),
      idxListY[]= Util.cvs2iArray(mae.cfg.Ylist, maxSamples, ","),
      idxListE[]= Util.cvs2iArray(mae.cfg.Elist, maxSamples, ",");
    
    idxRatioHP= (mae.cfg.RatioHP==null) ? 1 : Util.cvs2i(mae.cfg.RatioHP);
    
    /* [3] place lists in proper order based on index or
     * if no X,Y,E PARAM lists then default HP[1] for X,
     * HP[2] for Y and all samples in HP-E.
     * If there are no samples, then the lists will be null.
     */
    if(idxListX == null)
    { /* default msListX[1] */
      if(nHP>=1)
      {
        idxListX= new int[1];
        idxListX[0]= 1;
        msListX[++nHP_X]= msList[1];
      }
    }
    else
    { /* assign HP-X from Xlist PARAM */
      nHP_X= 0;
      for(int i=1;i<=idxListX.length;i++)
        if(msList[idxListX[i-1]]!=null)
          msListX[++nHP_X]= msList[idxListX[i-1]];
    }
    
    if(idxListY == null)
    { /* default msListY[1] */
      if(nHP>=2)
      {
        idxListY= new int[1];
        idxListY[0]= 2;
        msListY[++nHP_Y]= msList[2];
      }
      else if(nHP==1)
      {
        idxListY= new int[1];
        idxListY[0]= 1;
        msListY[++nHP_Y]= msList[1];
      }
    }
    else
    { /* assign HP-Y from Ylist PARAM */
      nHP_Y= 0;
      for(int i=1;i<=idxListY.length;i++)
        if(msList[idxListY[i-1]]!=null)
          msListY[++nHP_Y]= msList[idxListY[i-1]];
    }
    
    if(idxListE==null)
    { /* default msListE[1:nHP_E] to msList[1:nHP] */
      for(int i=1;i<=nHP;i++)
      {
        idxListE= new int[nHP+1];
        idxListE[i]= i;
        msListE[i]= msList[i];
      }
    }
    else
    { /* assign HP-E from Elist PARAM */
      nHP_E= 0;
      for(int i=1;i<=idxListE.length;i++)
        if(msList[idxListE[i-1]]!=null)
          msListE[++nHP_E]= msList[idxListE[i-1]];
    }
    
    /* [4] Setup default single X and Y samples if possible */
    if(nHP>=2)
    { /* start with 2 different samples */
      mae.curHP_X= (nHP_X==0) ? 1 : idxListX[0];
      mae.msX= msList[mae.curHP_X];
      if(mae.msX==null)
      {
        mae.curHP_X= 1;
        mae.msX= msList[mae.curHP_X];
      }
      mae.msX= msList[mae.curHP_X];
      mae.curHP_Y= (nHP_Y==0) ? 2 : idxListY[0];
      mae.msY= msList[mae.curHP_Y];
      if(mae.msY==null)
      {
        mae.curHP_Y= 2;
        mae.msY= msList[mae.curHP_Y];
      }
    }
    else if(nHP==1)
    { /* start with one sample, let HP-Y be same as HP-X */
      mae.curHP_X= 1;
      mae.msX= msList[1];
      mae.curHP_Y= mae.curHP_X;
      mae.msY= mae.msX;
    }
    
    return(true);
  } /* setHPlistsFromPARAM */
  
  
  /**
   * showHP_XY_assignmentsPopup() - make popup display of HP_X and HP-Y sample lists
   * @see ShowStringPopup
   * @see Util#showFeatures
   * @see Util#showMsg
   */
  void showHP_XY_assignmentsPopup()
  { /* showHP_XY_assignmentsPopup */
    String
      sListXmsg= "",
      sListYmsg= "",
      sListXreport= "",
      sListYreport= "",
      sX= "HP-X [" + mae.classNameX + "]:",
      sY= "HP-Y [" + mae.classNameY + "]:",
      s= "Hybridized samples assigned to HP-X and HP-Y 'sets'\n";
    MaHybridSample  ms;
    String sHP;
    
    for(int i=1;i<=nHP_X;i++)
    { /* build X list */
      ms= msListX[i];
      if(ms!=null)
      {
        sListXmsg += ((sListXmsg.length()==0)
        ? " " : ", ") + ms.hpName;
        sHP= (ms.sampleID.equals(ms.hpName))
                ? ms.fullStageText
                : ms.sampleID+ "  ["+ms.fullStageText+"]";
        sListXreport += " #" + i + "  " + sHP + "\n";
      }
    } /* build X list */
    
    for(int i=1;i<=nHP_Y;i++)
    { /* build Y list */
      ms= msListY[i];
      if(ms!=null)
      {
        sListYmsg += ((sListYmsg.length()==0) ? " " : ", ") + ms.hpName;
        sHP= (ms.sampleID.equals(ms.hpName))
               ? ms.fullStageText
               : ms.sampleID+ "  ["+ms.fullStageText+"]";
        sListYreport += " #" + i + "  " + sHP + "\n";
      }
    } /* build Y list */
    
    Util.showMsg(s);
    Util.showFeatures(sX+sListXmsg, sY+sListYmsg);
    String sTbl= s+"\n"+ sX+"\n"+sListXreport+ "\n"+ sY+"\n"+sListYreport;
    ShowStringPopup t= new ShowStringPopup(mae, sTbl,25,70, mae.rptFontSize,
                      "Current hybridized sample HP-X and -Y 'set' assignments",
                                           0, 0, "HP_XY_list",
                                           mae.pur.UNIQUE,
                                           "maeHP-XYassignments.txt");
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* showHP_XY_assignmentsPopup */
  
  
  /**
   * showHP_E_assignmentsPopup() - make popup display of HP_E list of samples.
   * @see ShowStringPopup
   * @see Util#showFeatures
   * @see Util#showMsg
   */
  void showHP_E_assignmentsPopup()
  { /* showHP_E_assignmentsPopup */
    String
      sHP,
      sListEreport= "",
      sListEmsg2= "",
      sListEmsg= "",
      s= "Current hybridized sample expression profile HP-E 'set' assignments -"+
      nHP_E + " samples\n";

    for(int i=1;i<=nHP_E;i++)
    {
      MaHybridSample ms= msListE[i];
      if(ms!=null)
      {
        if(sListEmsg.length()<=60)
          sListEmsg += "#" + i + "  " + ms.hpName +", ";
        else
          sListEmsg2 += "#" + i + "  " + ms.hpName +", ";
        
        sHP= (ms.sampleID.equals(ms.hpName))
                ? ms.fullStageText 
                : ms.sampleID+ "  ["+ms.fullStageText+"]";
        sListEreport += " #" + i + "  " + sHP + "\n";
      }
    }
    
    Util.showMsg(s);
    Util.showFeatures(sListEmsg,sListEmsg2);
    String sTbl= s+"\n"+sListEreport +"\n";
    ShowStringPopup t= new ShowStringPopup(mae, sTbl,30,70, mae.rptFontSize,
                                           "Current HP-E expression profile assignments",
                                           0, 0, "HP_E_assign", mae.pur.UNIQUE,
                                           "maeHP-Eassignement.txt");
    
    mae.madeChangesFlag= true;   /* made DB changes, so should SAVE DB*/
  } /* showHP_E_assignmentsPopup */
  
  
  /**
   * chkSwapCy5Cy3() - test if HP-X, HP-Y or HP-E list have  Cy3/Cy5 swap flag set.
   * If in ratio density mode, check to see if any HPs in the specified list
   * have the ms.swapCy5Cy3DataFlag set.
   * @return true if this is true, else false.
   */
  boolean chkSwapCy5Cy3(String msListToCheck /* "X", "Y", "E" */)
  { /* chkSwapCy5Cy3 */
    if(!mae.useRatioDataFlag)
      return(false);                   /* bad data */
    
    MaHybridSample msL[]= null;
    int nL= 0;
    
    if(msListToCheck.equals("X"))
    {
      msL= msListX;
      nL= nHP_X;
    }
    else if(msListToCheck.equals("Y"))
    {
      msL= msListY;
      nL= nHP_Y;
    }
    else if(msListToCheck.equals("E"))
    {
      msL= msListE;
      nL= nHP_E;
    }
    else
      return(false);
    
    for(int i=1;i<=nL;i++)
      if(msL[i].swapCy5Cy3DataFlag)
        return(true);
    
    return(false);
  } /* chkSwapCy5Cy3 */
  
  
  /**
   * setHPxyModStrings() - set up (sMod, sModX, sModY, sModXY) state strings.
   * The string reflects the ratio or intensity mode state as well as
   * whether swapCy5Cy3DataFlags is in effect.
   * @see SampleSets#chkSwapCy5Cy3
   */
  void setHPxyModStrings()
  { /* setHPxyModStrings */
    sMod= "";                             /* prefix title */
    sModX= "";                            /* epilog title */
    sModY= "";
    sModXY= "";
    sCy35= (mae.cfg.fluoresLbl1+"/"+ mae.cfg.fluoresLbl2);
    sCy53= (mae.cfg.fluoresLbl2+"/"+ mae.cfg.fluoresLbl1);
    sHPsetCy53= " some HPs ("+sCy53+") swapped";
    
    if(mae.useHPxySetDataFlag)
    { /* HP sets */
      sMod= " 'set'";
      sModX= (mae.hps.chkSwapCy5Cy3("X")) ? sHPsetCy53 : "";
      sModY= (mae.hps.chkSwapCy5Cy3("Y")) ? sHPsetCy53 : "";
    }
    else if(mae.useRatioDataFlag)
    { /* mark as swapped */
      sModX= " (" + ((mae.msX.swapCy5Cy3DataFlag) ? sCy53 : sCy35)+  ") ";
      sModY= " (" + ((mae.msY.swapCy5Cy3DataFlag) ? sCy53 : sCy35)+  ") ";
    }
    sModXY= (mae.msX==mae.ms) ? sModX : sModY;
  } /* setHPxyModStrings */
  
  
  /**
   * changeHPswapCy5Cy3Samples() - popup scrollable GUI array of checkbox to change cy3/Cy5 swap flags
   * msList[0:nHP-1].swapCy5Cy3DataFlags
   * @see ArrayScroller#repaint
   * @see Filter#computeWorkingGeneList
   * @see MAExplorer#repaint
   * @see MaHybridSample#swapCy5Cy3AndReCalcStats
   * @see PopupRegistry#updateCurGene
   * @see PopupScrollableSelector
   * @see #setHPxyModStrings
   */
  void changeHPswapCy5Cy3Samples()
  { /* changeHPswapCy5Cy3Samples */
    String
      title= "Select hybridized samples to swap (Cy3/Cy5) to (Cy5/Cy3) data",
      nameList[]= new String[nHP];
    
    boolean didSwapFlag= false;
    
    nameList= new String[nHP];
    valueList= new boolean[nHP];
    oldValueList= new boolean[nHP];
    
    for(int i=0;i<nHP;i++)
    {
      nameList[i]= msList[i+1].fullStageText;
      valueList[i]= msList[i+1].swapCy5Cy3DataFlag;
      oldValueList[i]= valueList[i];
    }
    
    Frame frame= new Frame();
    PopupScrollableSelector pss= new PopupScrollableSelector(mae, frame, title, 
                                                             nameList, valueList,
                                                             nHP);
    for(int i=0;i<nHP;i++)
      if(oldValueList[i]!=valueList[i])
      { /* swap F1 and F2 fields and toggle flag */
        didSwapFlag= true;
        msList[i+1].swapCy5Cy3AndReCalcStats(); /* swap stats. & data toggle 
                                                 * swapCy5Cy3DataFlag */
      }
    
    setHPxyModStrings();            /* setup (sMod, sModX, sModY, sModXY) */
    if(didSwapFlag)
    { /* update display and various other states */
      mae.updatePseudoImgFlag= true;
      mae.fc.computeWorkingGeneList();
      
      /* Fake it out to force refresh of all plots */
      mae.pur.updateCurGene(mae.cdb.objMID, 0,null);
      
      mae.is.repaint();
      mae.repaint();
    }
  } /* changeHPswapCy5Cy3Samples */
  
  
  /**
   * copyValuesToMlistCy3Cy5Flags() - copy nameListCB[] state to valueList[]
   */
  void copyValuesToMlistCy3Cy5Flags()
  { /* copyValuesToMlistCy3Cy5Flags */
    for(int i= 0; i<nHP;i++)
      msList[i+1].swapCy5Cy3DataFlag= valueList[i];
  } /* copyValuesToMlistCy3Cy5Flags */
  
  
} /* end of class SampleSets */




/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*          class PopupScrollableSelector                            */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
class PopupScrollableSelector extends Dialog implements
ActionListener, ItemListener, WindowListener
{
  private MAExplorer
    mae;
  private Frame
    frame;
  private ScrollPane
    checkBoxesSP;        /** to put checkBoxPanel in so can scroll */
  private Panel
    checkBoxPanel,       /** to put the checkBoxes in */
    controlPanel;        /** holds "Done" and "Cancel" buttons */
  private Checkbox
    nameListCB[];        /** [0:nList-1] checkboxes */
  private String
    nameList[];          /** [0:nList-1] values */
  private boolean
    valueList[];         /** [0:nList-1] values */
  private Button
    doneButton,
    cancelButton;
  private Label
    titleLbl;            /** holds title at top */
  private int
    nList;               /** # of elements in the list */
  private String
    title;
  
  
  /**
   * PopupScrollableSelector() - popup selector to select boolean values.
   * @param mae is MAExplorer instance
   * @param frame is the frame to put this popup selector in
   * @param title of the window
   * @param nameList of strings to put into selector
   * @param valueList of string values corresponding to the names
   * @param nList is the size of the list
   */
  PopupScrollableSelector(MAExplorer mae, Frame frame, String title,
                          String nameList[], boolean valueList[], int nList)
  { /* PopupScrollableSelector */
    super(frame,title,true);
    
    this.mae= mae;
    this.frame= frame;
    this.title= title;
    this.nameList= nameList;
    this.valueList= valueList;
    this.nList= nList;
    
    
    /* [TODO] Build scrollable array of nameList[] checkboxes */
    this.setLayout(new BorderLayout(1,1));
    this.addWindowListener(this);
    
    titleLbl= new Label(title);
    this.add("North", titleLbl);
    
    checkBoxesSP= new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    this.add("Center", checkBoxesSP);
    
    checkBoxPanel= new Panel();
    checkBoxPanel.setLayout(new GridLayout(nList,1,/*R,C*/ 1,1/*gap*/));
    checkBoxesSP.add(checkBoxPanel);
    
    controlPanel= new Panel();
    controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
    this.add("South", controlPanel);
    
    doneButton= new Button("Done");     /* close window button */
    doneButton.addActionListener(this);
    controlPanel.add(doneButton);
    
    cancelButton= new Button("Cancel");  /* cancel window button */
    cancelButton.addActionListener(this);
    controlPanel.add(cancelButton);
    
    /* Now add checkboxes */
    nameListCB= new Checkbox[nList];
    for(int i=0;i<nList;i++)
    {
      nameListCB[i]= new Checkbox(nameList[i]);
      checkBoxPanel.add(nameListCB[i]);
      nameListCB[i].addItemListener(this);
      nameListCB[i].setState(valueList[i]);
    }
    
    this.pack();
    
    /* Center frame on the screen, PC only */
    Dimension screen= Toolkit.getDefaultToolkit().getScreenSize(),
    popupSize= this.getSize();
    Point pos= new Point((screen.width - popupSize.width -100)/2,
                         (screen.height - popupSize.height+20)/2);
    this.setLocation(pos);
    
    this.setVisible(true);
  } /* PopupFamilyNameSelector */
  
  
  /**
   * copyStatesToValue() - copy nameListCB[] state to valueList[]
   */
  private void copyStatesToValue()
  { /* copyStatesToValue */
    for(int i= 0; i<nList;i++)
      valueList[i]= nameListCB[i].getState();
  } /* copyStatesToValue */
  
  
  /**
   * itemStateChanged() - handle item state checkbox selectors.
   * This computes the prp.useClassBits.
   * @param e is selection event
   * @see MAExplorer#repaint
   * @see Util#showMsg
   */
  public void itemStateChanged(ItemEvent e)
  { /* itemStateChanged */
    Object obj= e.getSource();
    Checkbox itemCB= (obj instanceof Checkbox) ? (Checkbox)obj : null;
    boolean cbFlag;
    
    /* Service the Family popup menu */
    for(int i= 0; i<nList;i++)
    { /* set corresponding value */
      cbFlag= nameListCB[i].getState();
      if(itemCB==nameListCB[i])
      { /* found one */
        String msg= (cbFlag) ? mae.hps.sCy53 : mae.hps.sCy35;
        Util.showMsg("Will swap (Cy3,Cy5) for "+nameList[i]+" to"+ msg);
        break;
      }
    } /* set corresponding value */
    
    mae.repaint();
  } /* itemStateChanged */
  
  
  /**
   * actionPerformed() - Handle button clicks
   * @param e is button press event
   * @see MAExplorer#repaint
   * @see SampleSets#copyValuesToMlistCy3Cy5Flags
   * @see Util#showMsg
   * @see #copyStatesToValue
   * @see #quit
   */
  public void actionPerformed(ActionEvent e)
  {
    String cmd= e.getActionCommand();
    Button item= (Button)e.getSource();
    
    if (cmd.equals("Done"))
    {
      copyStatesToValue();
      mae.hps.copyValuesToMlistCy3Cy5Flags();
      Util.showMsg("Swapped (Cy3,Cy5) values for selected HP that changed");
      quit();
    }
    else if (cmd.equals("Cancel"))
    {
      mae.util.showMsg("Canceling swapping (Cy3,Cy5) values");
      quit();
    }
  } /* actionPerformed */
  
  
  /**
   * quit() - kill this frame
   */
  void quit()
  { /* quit */
    this.dispose();
  } /* quit */
  
  
  /**
   * windowClosing() - close down the window.
   * @param e is window closing event
   * @see #quit
   */
  public void windowClosing(WindowEvent e)
  {
    quit();
  }
  

  /* Others not used at this time */
  public void windowClosed(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }
  public void windowActivated(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
 
} /* end of PopupScrollableSelector */




