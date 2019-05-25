/** File: EditedGeneList.java */

import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Class to edit the Edited Gene List.
 * The EGL is Filter.editedCL gene list. Operations include adding, removing,
 * setting it to Filter.workingCL, etc.
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
 * @version $Date: 2003/04/03 21:01:40 $   $Revision: 1.6 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see GeneClass
 * @see GeneList
 */

class EditedGeneList
{
  /** link to global MAExplorer instance */
  private MAExplorer
  mae;
  /** link to global Maps instance */
  private Maps
  map;
  /** link to global Filter instance */
  private Filter
  fc;
  /** link to global GeneClass instance */
  private GeneClass
  gct;
  
  /** temporary gene list for computations */
  private GeneList
  tmpCL;
  
  /** max # genes/gene list */
  int
  maxGenes;
  
  
  /**
   * EditedGeneList() - constructor
   * @param mae is the MAExplorer instance
   */
  EditedGeneList(MAExplorer mae)
  { /* EditedGeneList */
    this.mae= mae;
    map= mae.mp;
    fc= mae.fc;
    gct= mae.gct;
    
    maxGenes= 0;
  } /*EditedGeneList */
  
  
  /**
   * removeGeneFromEditedCL() - remove gene from the editedCL by mid.
   * @param mid is the Master Gene Index if not -1
   * @return true if removed it.
   * @see #removeGeneFromEditedCL
   */
  boolean removeGeneFromEditedCL(int mid)
  { /* removeGeneByPoint */
    if(mid==-1)
      return(false);         /* it does not exist */
    else
      return(removeGeneFromEditedCL(map.midStaticCL.mList[mid]));
  } /* removeGeneByPoint */
  
  
  /**
   * removeGeneFromEditedCL() - remove gene from the editedCL by gene.
   * @param gene is the Gene instance
   * @return true if removed it.
   * @see GeneBitSet#updateListGeneBitSets
   * @see GeneList#rmvGene
   */
  boolean removeGeneFromEditedCL(Gene gene)
  { /* removeGeneByPoint */
    if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
      return(false);            /* ignore bogus spots */
    
    gct.editedCL.rmvGene(gene);
    gct.editedCL.bitSet.updateListGeneBitSets();
    
    return(true);
  } /* removeGeneByPoint */
  
  
  /**
   * addGeneToEditedCL() - add gene to editedCL if not in CL by mid
   * @param mid is the Master Gene Index if not -1
   * @return true if aded it.
   */
  boolean addGeneToEditedCL(int mid)
  { /* addGeneToEditedCL */
    if(mid==-1)
      return(false);         /* it does not exist */
    return(addGeneToEditedCL(map.midStaticCL.mList[mid]));
  } /* addGeneToEditedCL */
  
  
  /**
   * addGeneToEditedCL() - add gene to editedCL if not in CL by gene
   * @param gene is the Gene instance
   * @return true if added it.
   * @see GeneList#addGene
   * @see GeneBitSet#updateListGeneBitSets
   */
  boolean addGeneToEditedCL(Gene gene)
  { /* addGeneToEditedCL */
    if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
      return(false);            /* ignore bogus spots */
    
    gct.editedCL.addGene(gene);
    gct.editedCL.bitSet.updateListGeneBitSets();
    
    return(true);
  } /* addGeneToEditedCL */
  
  
  /**
   * addGenesByRegion() - add genes to editedCL if they are in the region
   * in the pseudoarray image or scatterplot
   * [TODO]
   * @param gene is the Gene instance
   * @return true if added it.
   */
  boolean addGeneByRegion(Gene gene)
  { /* addGeneByRegion */
    return(false);
  } /* addGeneByRegion*/
  
  
  /**
   * processEditCommand() - do an 'edited gene list' add/remove command:
   *<PRE>
   *   CTRL/MOUSE to add genes to editedCL
   *   SHIFT/MOUSE to remove genes from editedCL
   *</PRE>
   * @param mouseKeyModes key modifiers
   * @return true if successful
   * @see Filter#computeWorkingGeneList
   * @see GeneList#isGeneInGeneList
   * @see MAExplorer#repaint
   * @see ScrollableImageCanvas#repaint
   * @see Util#showMsg
   * @see #addGeneToEditedCL
   * @see #removeGeneFromEditedCL
   */
  boolean processEditCommand(int mouseKeyMods)
  { /* processEditCommand */
    boolean
      ctrlMod= ((mouseKeyMods & InputEvent.CTRL_MASK) != 0),
      shiftMod= ((mouseKeyMods & InputEvent.SHIFT_MASK) != 0);
    
    /* CTRL/MOUSE to add genes to editedCL */
    if(mae.cdb.isValidObjFlag && (ctrlMod || mae.editMode==mae.EDIT_ADD))
    { /* add a point to editedCL */
      addGeneToEditedCL(mae.cdb.objMID);
      Util.showMsg(" Adding gene #" + gct.editedCL.length +
                   " " +mae.sf.getXYG(mae.is.siCanvas.xyObj, mae.ms) +
                   " to 'edited gene list'");
      if(mae.useEditedCLflag)
        fc.computeWorkingGeneList();
      /* Repaint since may have updated Filter or "E.G.L." */
      mae.is.siCanvas.repaint();
      mae.repaint();
    }
    
    /* SHIFT/MOUSE to remove genes from editedCL */
    else if(mae.cdb.isValidObjFlag &&
           (shiftMod || mae.editMode==mae.EDIT_RMV))
    { /* remove a point from editedCL */
      Gene gene= map.midStaticCL.mList[mae.cdb.objMID];
      if(gct.editedCL.isGeneInGeneList(gene) &&
         removeGeneFromEditedCL(mae.cdb.objMID))
        Util.showMsg("Removed gene #" + (gct.editedCL.length+1)+
                     " " +mae.sf.getXYG(mae.is.siCanvas.xyObj, mae.ms) +
                     " from 'edited gene list'");
      if(mae.useEditedCLflag)
        fc.computeWorkingGeneList();
      /* Repaint since may have updated Filter or "E.G.L." */
      mae.is.siCanvas.repaint();
      mae.repaint();
    }
    else
      return(false);
    
    return(true);
  }  /* processEditCommand */
  
  
  /**
   * setEGLfromGeneListStr() - set the EGL from the gene list string names.
   * This allows MULTIPLE instances of gene to be added to EGL.
   * @param geneNameList is string list of genes by Gene_Name
   * @return true if succeed.
   * @see GeneList#setGeneListFromGeneNameArray
   */
  boolean setEGLfromGeneListStr(String geneNameList[])
  { /* setEGLfromGeneListStr( */
    if(geneNameList==null || geneNameList.length==0)
      return(false);    
    boolean flag= gct.editedCL.setGeneListFromGeneNameArray(geneNameList);
    return(flag);
  } /* setEGLfromGeneListStr */
  
  
}   /* end of class EditedGeneList.java  */
