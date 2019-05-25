/** File: Guesser.java */

import java.awt.*;
import java.awt.Cursor;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.awt.TextField;
import java.awt.TextArea;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.*;

/**
 * The Guesser class to used to create popup string guessers from list of strings. 
 * It is given a list of strings and the upper case version of that list. It then
 * lets the user type in the beginning letters of the substring to be matched.
 * It then dynamically finds the matching substring (ignoring case) and presents
 * that in the scrollable text area. If you use a leading "*" wildcard
 * Guesser is used as a base class for building data specific guessers. 
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
 * @version $Date: 2004/01/13 16:45:02 $   $Revision: 1.10 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see PopupGeneGuesser 
 * @see PopupHPmenuGuesser 
 * @see PopupProjDirGuesser
 */

class Guesser extends Frame implements ActionListener, 
	     WindowListener, FocusListener
{ /* class Guesser */
  /** link to global instance */
  MAExplorer 
    mae;                   
  /** link to global Maps instance */
  Maps
    map;                   
  /** link to global MenuBarFrame instance */
  MenuBarFrame
    mbf;                  
  /** link to global GipoTable instance */
  GipoTable
    gipo;                  
    
  /** used to set iMode */
  private final static int
    BAD_MODE= 0;
  /** used to set iMode */
  private final static int                
    MODE_MASTER_GENE_NAME= 1;
  /** used to set iMode */
  private final static int
    MODE_MASTER_ID= 2;
  /** used to set iMode */
  private final static int
    MODE_GENBANK= 3;
  /** used to set iMode */
  private final static int
    MODE_GENBANK3= 4;
  /** used to set iMode */
  private final static int
    MODE_GENBANK5= 5;
  /** used to set iMode */
  private final static int
    MODE_DB_EST3= 6;
  /** used to set iMode */
  private final static int
    MODE_DB_EST5= 7;
  /** used to set iMode */
  private final static int
    MODE_SWISSPROT= 8;
  /** used to set iMode */
  private final static int
    MODE_UNIGENE= 9;
  /** used to set iMode */
  private final static int
    MODE_LOCUSLINK= 10;

  /** max items to display in ta area */
  final static int
    DEF_GUESSER_DISP_SIZE= 50000;  
  /** default # rows size of text area */
  final static int
    DEF_TA_ROWS= 15;             
  /** default # columns size of text area */
  final static int
    DEF_TA_COLS= 25;           
  /** flag for single gene finish */
  final static int
    SINGLE_GENE= 1;             
  /** flag for multiple gene finish */
  final static int
    MULTIPLE_GENE= 2;           
 
  /** frame where guesser is built */
  Frame
    frame;                      
  /** TextArea list of best guesses */
  private TextArea
    ta;				
  /** Text field for User input */
  private TextField
    tf;				
  /** place for buttons */	
  Panel 
    buttonPanel;			
  /** place for title */
  Panel 
    titlePanel;			
  /** title label */
  Label
    titleLabel;	    
  /** toggle: "Master ID", "GeneName", "GenBank", "dbEst", "UniGene", "LocusID" */		
  Button
    geneDBnamesButton;              
  /** handle events for text field */
  KeyEventHandler 
    kev;			
  /** handle events for text area */
  MouseEventHandler
    meh;			
  /** return user results from guesser */
  String
    resultStr;			    		
  /** title of window */
  String
    title;			
  /** "GeneName", "GenBank", "dbEst" etc */
  String
    alphaListMode;                		
  /** if passing results back as sublist */	
  String 
    resultSubList[];            	
  /** char text to be searched (GeneName)*/
  String
    alphaList[];				
  /** concatenated string for sorting */
  String
    sortedAlphaListStr;		
  /** size of alphaList array */
  int
    alphaListSize;		
  /** sub strings for guesser */
  String
    subAlphaList[];		
  /** global setup flag */	
  boolean
    onceOnlyFlag;               	
  /** if true, don't put Clone opt. button */	
  boolean
    genericFlag;                	
  /** if set then group copy to EGL */
  boolean
    setEGLfromListFlag;         
  /** button labels */
  String
    geneIDname[];               
  /** one of the above MODE_xxxx values */
  int
    iMode;                                     
  /** what type of finish was requested: either SINGLE_GENE or MULTIPLE_GENE */
  int
    resultMode;                 
  /** local map.maxGenes */  
  int
    maxGenes;                  
  /** size of TextArea */
  int
    taRows;			
  /** size of TextArea */
  int
    taCols;			
  /** first char typed */
  char
    chFirstTyped;   	        
  
  /** Font for guesser list */
  Font
    font;
  
  /* --- Only ONE of the following parent class instances is not null ---*/
  /** parent class instance if it is not null */
  PopupGeneGuesser
    pgcg;
  /** parent class instance if it is not null */
  PopupHPmenuGuesser
    phpmg; 
  /** parent class instance if it is not null */
  PopupProjDirGuesser
    pPDg;
   

  /**
   * Guesser() - constructor for Guesser.
   * @param mae is instance of MAExplorer
   * @param genericFlag to use the generic IDs
   * @param setEGLfromListFlag if enable setting the EGL list from Guesser
   */
  Guesser(MAExplorer mae, boolean genericFlag,
          boolean setEGLfromListFlag)
  { /* Guesser */
    this.mae= mae;
    map= mae.mp;
    mbf= mae.mbf;
    gipo= mae.gipo;
    
    this.maxGenes= map.maxGenes;
    this.genericFlag= genericFlag;
    this.setEGLfromListFlag= setEGLfromListFlag;
    
    String sDefs[]= {"BAD", mae.masterGeneName, mae.masterIDname,
                     "GenBank", "GenBank 3'", "GenBank 5'",
                     "dbEST 3'", "dbEST 5'", "SwissProt", "UniGene",
                     "LocusID"};
    geneIDname= sDefs;
     
     pgcg= null;
     phpmg= null;
     pPDg= null;
     resultMode= 0;
     onceOnlyFlag= false;
  } /* Guesser */
  
  
  /**
   * setData() - for single list. It should be a presorted single list.
   * @param chFirst is preface character if not null to do initial search
   * @param sortedAlphaListStr is list sorted alphabetically as string
   * @param alphaList is alphabetic list
   * @param alphaListSize is size of list
   * @param taRows is table rows
   * @param taCols is table columns
   * @param title is the title of guesser
   * @param font to use
   */
  void setData(char chFirst, String sortedAlphaListStr, String alphaList[],
               int alphaListSize, int taRows, int taCols, String title,
               Font font)
  { /* setData */
    this.maxGenes= map.maxGenes;
    
    this.chFirstTyped= chFirst;
    this.sortedAlphaListStr= sortedAlphaListStr;
    this.alphaList= alphaList;
    this.alphaListSize= alphaListSize;
    this.taRows= (taRows==0) ? DEF_TA_ROWS : taRows;
    this.taCols= (taCols==0) ? DEF_TA_COLS : taCols;
    this.title= title;
    this.font= font;
  } /* setData*/
  
  
  /**
   * switchAlphaList() - switch alphaList to alphaListMode (for gene guesser).
   * The legal modes are:
   *  "Master ID", "Gene Name", "GenBank/3'/5'", "dbEst 3'/5'",
   * "SwissProt", "Unigene_ID", "LocusID"
   * @param iMode is one of the following
   *   MODE_MASTER_GENE_NAME, MODE_MASTER_ID, MODE_GENBANK, MODE_GENBANK3,
   *   MODE_GENBANK5, MODE_DB_EST3, MODE_DB_EST5, MODE_SWISSPROT,
   *   MODE_UNIGENE, MODE_LOCUSLINK
   * @see SortMAE#uniqueInsert
   * @see SortMAE#quickSortMultLists
   * @see Util#cvtStrArrayToStr
   * @see #clearData
   * @see KeyEventHandler#resetKeyEventHandler
   * @see MouseEventHandler#resetMouseEventHandler
   */
  void switchAlphaList(int iMode)
  { /* switchAlphaList */
    
    /* [1] Setup Identifier specific changes */
    if(iMode!=BAD_MODE)
    { /* valid data type */
      clearData();
      
      String
        nameList[],
        nameList0[]= new String[map.maxGenes],   /* shorten later */
        nameList0UC[]= new String[map.maxGenes], /* upper case for sorting*/
        name= null;
      int nameListSize= 0;
      Gene
        mList[]= map.midStaticCL.mList,
        gene;      
      Hashtable nameListHT= new Hashtable((int)(1.5*maxGenes));
      /* used by uniqueInsert() to speed things up */
      
      /* [1.1] Copy data to working guesser lists */
      for(int k= 0; k < maxGenes; k++)
      { /* process gene mid */
        gene= mList[k];
        if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
          continue;            /* ignore bogus spots */
      
        switch(iMode)
        { /* add gene to list */
          case MODE_MASTER_GENE_NAME:
            name= gene.MasterGeneName;
            if(name==null || name.length()==0 ||
            name.equals("empty"))
              continue;
            break;
          case MODE_MASTER_ID:
            name= gene.Master_ID;
            break;
          case MODE_GENBANK:
            name= gene.GenBankAcc;
            break;
          case MODE_GENBANK3:
            name= gene.GenBankAcc3;
            break;
          case MODE_GENBANK5:
            name= gene.GenBankAcc5;
            break;
          case MODE_DB_EST3:
            name= gene.dbEST3;
            break;
          case MODE_DB_EST5:
            name= gene.dbEST5;
            break;
          case MODE_SWISSPROT:
            name= gene.SwissProt;
            break;
          case MODE_UNIGENE:
            name= gene.Unigene_ID;
            break;
          case MODE_LOCUSLINK:
            name= gene.LocusID;
            break;
        } /* add gene to list */
        
        /* Add only if unique */
        if(name!=null && name.length() > 0 )
        { /* build sorted list */
          /* build unsorted list that will Quicksort later */
          nameListSize= SortMAE.uniqueInsert(nameList0,nameList0UC,
                                             nameListSize,name, nameListHT);
        } /* build sorted list */
      } /* process gene mid */
      
     /* [1.1.1] Now do the QuickSort after inserted the data
      * on the unsorted lists.
      */
      SortMAE.quickSortMultLists(nameList0,nameList0UC,
                                 0, nameListSize-1);
      
      /* [1.2] Shorten arrays to the exact length */
      nameList0UC= null;             /* G.C. */
      nameList= new String[nameListSize];
      System.arraycopy(nameList0,0,nameList,0,nameListSize);
      nameList0= null;             /* G.C. */
      
      /* [1.3] Sort lists then convert to string with "\n" delimiters */
      sortedAlphaListStr= Util.cvtStrArrayToStr(nameList);
      alphaList= nameList;
      alphaListSize= nameListSize;
      
      kev.resetKeyEventHandler(alphaList,alphaListSize);
      meh.resetMouseEventHandler(alphaList);
    } /* valid data type */    
  } /* switchAlphaList */
  
  
  /**
   * clearData() - clear TextField tf and TextArea ta data for switching to another list.
   * @see PopupGeneGuesser#clear
   * @see PopupHPmenuGuesser#clear
   * @see PopupProjDirGuesser#clear
   */
  void clearData()
  { /* clearData */
    tf.setText("");
    ta.setText("");
    
    if(pgcg!=null)
      pgcg.clear();
    else if(phpmg!=null)
      phpmg.clear();
    else if(pPDg!=null)
      pPDg.clear();
  } /* clearData */
  
  
  /**
   * buildAndRunGuesser() - create the Guesser window GUI
   * @param guesserTitle to put on window
   * @see KeyEventHandler
   * @see MouseEventHandler
   */
  void buildAndRunGuesser(String guesserTitle)
  { /* buildAndRunGuesser */    
    Button b;  
      
    /* [1] create buttons */
    guesserTitle= (guesserTitle!=null) ? guesserTitle : "Guesser";
    buttonPanel= new Panel();
    
    if(setEGLfromListFlag)
    {
      b= new Button("Set E.G.L.");
      b.addActionListener(this);
      b.setFont(font);
      buttonPanel.add("Center", b);
    }
    
    b= new Button("Done");
    b.addActionListener(this);
    b.setFont(font);
    buttonPanel.add("Center",b);
    
   /* Toggle: "Master ID", "GeneName", "GenBank/3'/5", "dbEst3'/5'",
    * "SwissProt", "Unigene_ID", LocusID" etc.
    */
    iMode= MODE_MASTER_GENE_NAME;
    alphaListMode= "Gene Name";
    geneDBnamesButton= new Button("   Gene Name   ");
    geneDBnamesButton.addActionListener(this);
    geneDBnamesButton.setFont(font);
    if(!genericFlag)
      buttonPanel.add("Center", geneDBnamesButton);
    
    b= new Button("Cancel");
    b.addActionListener(this);
    b.setFont(font);
    buttonPanel.add("West", b);
    
    b= new Button("Clear");
    b.addActionListener(this);
    b.setFont(font);
    buttonPanel.add("East", b);
    buttonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
    /* [2] title */
    titlePanel= new Panel();
    titlePanel.setLayout(new GridLayout(1,2));
    titleLabel= new Label(title, Label.CENTER);
    titleLabel.setFont(font);
    
    /* [3] create TextField */
    tf= new TextField();
    tf.requestFocus();/* force tf to have primary focus */
    tf.setEditable(true);
    Character character= new Character(chFirstTyped);
    String tmpStr= character.toString(); /* convert single char to Str */
    
    tf.setText(tmpStr);
    tf.setFont(font);
    tf.setBackground(Color.cyan);
    
    /* [4] Guesser list apears here in the TextArea */
    ta= new TextArea("Type text into blue field above", taRows, taCols);
    ta.setEditable(false);	/* make sure user can not change text */
    ta.setFont(font);
    ta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
    /* [5] create mouse listener for TextArea */
    meh= new MouseEventHandler(mae,this,tf, ta, alphaList);
    ta.addMouseListener(meh);
    
    /* [6] create key listener for text field */
    kev= new KeyEventHandler(mae, this, tf, ta, alphaList,
    alphaListSize, meh);
    tf.addKeyListener(kev);
    tf.addFocusListener(this);
    
    /* [7] setup frame and add stuff to frame */
    titlePanel.add(tf);
    titlePanel.add(titleLabel);
    
    frame= new Frame(guesserTitle);
    frame.setBackground(Color.white);	/* white background */
    frame.addWindowListener(this); /* listener for window events */
    frame.setLayout(new BorderLayout());
    frame.setLayout(new BorderLayout());
    frame.add("South" , buttonPanel);
    frame.add("Center", ta);
    frame.add("North", titlePanel);
    frame.pack();
    
    /* [8] Center frame on the screen, PC only */
    Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
    Point framePos= new Point((screen.width-frame.getSize().width)/2,
                              (screen.height-frame.getSize().height)/2);
    frame.setLocation(framePos);
    frame.setVisible(true);
    
    kev.handleKeyTyped(chFirstTyped);	/* implement first key typed */
    tf.setVisible(true);
    tf.requestFocus(); /* force tf to have primary focus */
  } /* buildAndRunGuesser */
  
  
  /**
   * updateAssocTextField() - update the associated text field
   * @param str to update text field
   * @see PopupGeneGuesser#updateAssocTextField
   */
  void updateAssocTextField(String str)
  { /* updateAssocTextField */
    if(pgcg!=null)
      pgcg.updateAssocTextField(str);
  } /* updateAssocTextField */
  
  
  /**
   * updateAssocTextFieldCaretPos() - update associated text field caret position
   * @param pos is field caret position
   * @see PopupGeneGuesser#updateAssocTextFieldCaretPos
   */
  void updateAssocTextFieldCaretPos(int pos)
  { /* updateAssocTextFieldCaretPos */
    if(pgcg!=null)
      updateAssocTextFieldCaretPos(pos);
  } /* updateAssocTextFieldCaretPos */
  
  
  /**
   * copyArrayToSizedArray() - resize copy tempList to outList [0:outLen-1].
   * Also copy the outList to the global resultSublist[].
   * @param tempList is temporary list
   * @param outLen is length of output list
   * @return resized list
   */
  String[] copyArrayToSizedArray(String[] tempList, int outLen)
  { /* copyArrayToSizedArray */
    /* make array with exact size */    
    if(outLen > 0)
    { /* make array with exact size */
      String outList[]= new String[outLen];
      
    /* Make copy, otherwise errors w/ .length().
     * Copy tempList to outList
     */
      System.arraycopy((Object) tempList, 0,
                       (Object) outList, 0, outLen);
      
      resultSubList= outList;   /* save in case need to pass back */
      return(outList);
    }
    else
    {
      resultSubList= null;   /* save in case need to pass back */
      return(null);
    }
  } /* copyArrayToSizedArray */
  
  
  /**
   * wildCard() - search for *<letters> and return the sublist
   * @param searchFor is string to search for
   * @param data is list to search
   * @param size is size of list to search
   * @return sublist the subset matching wildcards
   * @see #copyArrayToSizedArray
   */
  String[] wildCard(String searchFor, String data[], int size)
  { /* wildCard */
    String tempList[]= new String[size];
    int outLen= 0;
    
    for(int x=0; x < size; x++)
      if(data[x].lastIndexOf(searchFor) > 0)
      { /* found so copy str for output */
        tempList[outLen++]= data[x]; /* save it */
      }
    
    /* Make array with exact size */
    return(copyArrayToSizedArray(tempList,outLen));
  } /* wildCard */
  
  
  /**
   * actionPerformed() - Handle Control button clicks.
   * @see PopupGeneGuesser#done
   * @see PopupHPmenuGuesser#done
   * @see PopupProjDirGuesser#done
   * @see PopupGeneGuesser#clear
   * @see PopupHPmenuGuesser#clear
   * @see PopupProjDirGuesser#clear
   * @see PopupGeneGuesser#cancel
   * @see PopupHPmenuGuesser#cancel
   * @see PopupProjDirGuesser#cancel
   * @see #close
   * @see #switchAlphaList
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    Button item= (Button)e.getSource();
    
    if (cmd.equals("Cancel"))
    {
      mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      close();
    }
    
    else if (cmd.equals("Set E.G.L."))
    { /* finished: pass back String[] of data  in text area */
      String
        geneListStr[]= resultSubList, /* get LIST of strings */
        resultStr= tf.getText();      /* get single string in case list
                                       * is empty */
      if(pgcg!=null)
      { /* finish processing in EditedGeneList */
        pgcg.setEGL(geneListStr, resultStr);
        resultMode= MULTIPLE_GENE;
        mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    } /* finished: pass back String[] of data  in text area */
    
    else if (cmd.equals("Done"))
    { /* finished: pass back data in text field */
      String resultStr= tf.getText();  /* get & store results */
      if(genericFlag && resultStr!=null &&
         (phpmg!=null || pPDg!=null))
      { /* generic guesser for any type of data - single list */
        if(phpmg!=null)
          phpmg.done(resultStr);
        else if(pPDg!=null)
          pPDg.done(resultStr);
      }
      
      else if(pgcg!=null)
      { /* Gene Clone guesser */
        Gene
          mList[]= map.midStaticCL.mList,
          gene;
        
        if(iMode==MODE_MASTER_GENE_NAME)
          pgcg.done(resultStr);
        
        else if(iMode==MODE_MASTER_ID)
          pgcg.done(resultStr);
        
        else
        { /* convert to other types of IDs */
          boolean foundIt= false;
          for(int i=0; i < maxGenes; i++)
          { /* lookup the gene */
            gene= mList[i];
            if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
              continue;          /* ignore bogus spots */      
            
            if(iMode==MODE_GENBANK &&
               gene.GenBankAcc.length()>0 &&
               gene.GenBankAcc.equals(resultStr))
            {
              foundIt= true;
            }
            else if(iMode==MODE_GENBANK3 &&
                    gene.GenBankAcc3.length()>0 &&
                    gene.GenBankAcc3.equals(resultStr))
            {
              foundIt= true;
            }
            else if(iMode==MODE_GENBANK5 &&
                     gene.GenBankAcc5.length()>0 &&
                     gene.GenBankAcc5.equals(resultStr))
            {
              foundIt= true;
            }
            else if(iMode==MODE_DB_EST3 &&
                    gene.dbEST3.length()>0 &&
                    gene.dbEST3.equals(resultStr))
            {
              foundIt= true;
            }
            else if(iMode==MODE_DB_EST5 &&
                    gene.dbEST5.length()>0 &&
                    gene.dbEST5.equals(resultStr))
            {
              foundIt= true;
            }
            else if(iMode==MODE_SWISSPROT &&
                    gene.SwissProt.length()>0 &&
                    gene.SwissProt.equals(resultStr))
            {
              foundIt= true;
            }
            else if(iMode==MODE_UNIGENE &&
                    gene.Unigene_ID.length()>0 &&
                    gene.Unigene_ID.equals(resultStr))
            {
              foundIt= true;
            }
            
            if(foundIt)
            {
              pgcg.done(gene.Master_ID);
              break;
            }
          } /* lookup the gene */
        } /* convert to other types of IDs */
        
        resultMode= SINGLE_GENE;
      }
      else if(phpmg!=null)
        phpmg.done(resultStr);
      else if(pPDg!=null)
        pPDg.done(resultStr);
    } /* Gene Clone guesser */
    
    else if (cmd.equals("Clear"))      /* clear textfield and textarea */
    { /* clear text area and text fields */
      tf.setText("");
      ta.setText("");
      
      if(pgcg!=null)
        pgcg.clear();
      else if(phpmg!=null)
        phpmg.clear();
      else if(pPDg!=null)
        pPDg.clear();
    } /* clear text area and text fields */
    
    else if(!genericFlag && geneDBnamesButton==item)
    { /* Switch alphaListMode between "GeneName","GenBank","dbEst" etc. */
     /* This skips instances where the geneIDname is not in the GIPO db.
      * search for next ID type which actually exists.
      */
      while(true)
      { /* only used IDs which actually exist - chk GIPO */
        if(iMode==MODE_MASTER_GENE_NAME)
        {
          iMode= MODE_MASTER_ID;
          if(gipo.masterIdIdx!=-1)
            break;
        }
        else if(iMode==MODE_MASTER_ID)
        {
          iMode= MODE_GENBANK;
          if(gipo.GenBankAccIdx!=-1)
            break;
        }
        else if(iMode==MODE_GENBANK)
        {
          iMode= MODE_GENBANK3;
          if(gipo.GenBankAcc3Idx!=-1)
            break;
        }
        else if(iMode==MODE_GENBANK3)
        {
          iMode= MODE_GENBANK5;
          if(gipo.GenBankAcc5Idx!=-1)
            break;
        }
        else if(iMode==MODE_GENBANK5)
        {
          iMode= MODE_DB_EST3;
          if(gipo.dbESTid3Idx!=-1)
            break;
        }
        else if(iMode==MODE_DB_EST3)
        {
          iMode= MODE_DB_EST5;
          if(gipo.dbESTid5Idx!=-1)
            break;
        }
        else if(iMode==MODE_DB_EST5)
        {
          iMode= MODE_UNIGENE;
          if(gipo.Unigene_cluster_IDidx!=-1)
            break;
        }
        else if(iMode==MODE_UNIGENE)
        {
          iMode= MODE_LOCUSLINK;
          if(gipo.LocusLinkIdIdx!=-1)
            break;
        }
        else if(iMode==MODE_LOCUSLINK)
        {
          iMode= MODE_SWISSPROT;
          if(gipo.SwissProtIdx!=-1)
            break;
        }
        else if(iMode==MODE_SWISSPROT)
        {
          iMode= MODE_MASTER_GENE_NAME;
          if(gipo.masterNameIdx!=-1)
            break;
        }
      } /* only used IDs which actually exist - chk GIPO */
      
      alphaListMode= geneIDname[iMode];
      geneDBnamesButton.setLabel(alphaListMode);
      
      /* Set the alpha list to the new type */
      switchAlphaList(iMode);
    } /* Switch alphaListMode between "GeneName","GenBank","dbEst" etc. */
  } /* actionPerformed */
  
  
  /**
   * close() - close the window.  This calls the close() of the parent.
   */
  void close()
  { /* close */
    mae.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    if(pgcg!=null)
      pgcg.cancel();
    else if(phpmg!=null)
      phpmg.cancel();
    else if(pPDg!=null)
      pPDg.cancel();
  } /* close */
  
  
  /**
   * handleKeyTyped() - handle key Typed/Released in textfield of guesser
   * @see KeyEventHandler#handleKeyTyped
   */
  public void handleKeyTyped(char ch)
  {
    kev.handleKeyTyped(ch);
  }
  
  
  /**
   * windowClosing() - close down the window.
   * @see #close
   */
  public void windowClosing(WindowEvent e)
  {
    close();
  }
  
  
  public void windowActivated(WindowEvent e)
  {}
  public void windowClosed(WindowEvent e)
  {}
  public void windowDeactivated(WindowEvent e)
  {}
  public void windowDeiconified(WindowEvent e)
  {}
  public void windowIconified(WindowEvent e)
  {}
  public void windowOpened(WindowEvent e)
  {}
  
  public void focusGained( FocusEvent e )
  {} /* not used yet */
  public void focusLost( FocusEvent e )
  {}   /* not used yet */
  
} /* class Guesser */




/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*                 CLASS KeyEventHandler                          */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
class KeyEventHandler extends KeyAdapter
{ /* class KeyEventHandler */
  /** instance of MAExplorer */
  MAExplorer
    mae;
  
  /** instance of Guesser */
  Guesser
    gsr;
  /** TextArea list of best guesses */
  TextArea
    ta;
  /** User types in here */
  TextField
    tf;
  /** alpha list */
  String
    alphaList[];
  /** for passing flag */
  MouseEventHandler
    meh;
  /** typed in tf */
  String
    searchStr;
  /** displayed in ta */
  String
    displayStr;
  int
    previousSearchIndex= 0;
  /** size of alphaList array */
  int
    alphaListSize;
  
  /**
   * KeyEventHandler() - constructor for key events.
   * @param mae is instance of MAExplorer
   * @param gsr is instance of Guesser
   * @param tf is TextField
   * @param ta is TextArea
   * @param alphaList list of data
   * @param alphaListSize size of data list
   * @param meh is MouseEventHandler
   */
  KeyEventHandler(MAExplorer mae, Guesser gsr, TextField tf, TextArea ta,
                  String alphaList[], int alphaListSize, MouseEventHandler meh)
  { /* KeyEventHandler */
    this.mae= mae;
    this.gsr= gsr;
    this.ta= ta;
    this.tf= tf;
    this.alphaList= alphaList;
    this.alphaListSize= alphaListSize;
    
    /*
    if(mae.CONSOLE_FLAG)
    {
      mae.fio.logMsgln("alphaListSize= "+alphaListSize);
      for(int x=0;x<alphaListSize;x++)
        mae.fio.logMsgln("GU-KEH alphaList[]= "+alphaList[x]);
    }
    */
    
    this.meh= meh;
    displayStr= "";
    searchStr= "";
  } /* KeyEventHandler */
  
  
  /**
   * resetKeyEventHandler() - Reset data within KeyEventHandler.
   * @param alphaList list of data
   * @param alphaListSize size of data list
   */
  public void resetKeyEventHandler(String alphaList[], int alphaListSize)
  { /* KeyEventHandler */
    this.alphaList= alphaList;
    this.alphaListSize= alphaListSize;
    
    displayStr= "";
    searchStr= "";
  } /* KeyEventHandler */
  
  
  /**
   * keyReleased() - handle keyboard inputs from TextField tf.
   * Note: do not use keyPressed - does not work on the PC, a Bug.
   * @param evt is key released event
   * @see #handleKeyTyped
   */
  public void keyReleased(KeyEvent evt)
  { /* keyReleased */
    char ch= evt.getKeyChar();
    handleKeyTyped(ch);
  } /* keyReleased */
  
  
  /**
   * handleKeyTyped() - handle key Typed/Released in textfield of guesser
   * @param evt is key typed event
   * @see Guesser#updateAssocTextField
   * @see Guesser#updateAssocTextFieldCaretPos
   * @see MAExplorer#logDRYROTerr
   * @see Util#cvtStrArrayToStr
   * @see #containsWildCard
   * @see #findStrLoc
   * @see #makeSearchList
   */
  public void handleKeyTyped(char ch)
  {
    int
      searchStrSize= 0,
      index= 0,		/* index for str in text area */
      bufLoc= -1,	/* location of found str in ta */
      searchIndex= 0;		/* size of search string in text field */
    boolean wildCardFlag= false;
    
    /* [1] initialize */
    gsr.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    gsr.buttonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    gsr.titlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    ta.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    tf.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    if(searchIndex == 0)
      ta.select(0,0);		  /* select nothing or reset ta */
    
    searchStr= tf.getText();    /* get str to search for */
    gsr.subAlphaList= alphaList;
    
    gsr.updateAssocTextField(searchStr);
    gsr.updateAssocTextFieldCaretPos(searchStr.length());
    
    searchIndex= searchStr.length();
    searchStrSize= searchStr.length(); /* size of str to find */
    bufLoc= -1;	/* initialize to -1 */
    
    if(searchStrSize > 0)
    { /* something to search for */
      /* [2] check if wildcard present and set flag */
      wildCardFlag= (containsWildCard(searchStr) >=0);
      
      if(gsr.subAlphaList != null)
      { /* alpha */
        if(wildCardFlag)
          gsr.subAlphaList= makeSearchList(1, alphaList, alphaListSize,
                                           searchStr, gsr.DEF_GUESSER_DISP_SIZE);
        else
          gsr.subAlphaList= makeSearchList(alphaList, alphaListSize,
                                           searchStr,gsr.DEF_GUESSER_DISP_SIZE);
        
        displayStr= Util.cvtStrArrayToStr(gsr.subAlphaList);
        if(displayStr!=null)
          ta.setText(displayStr);
        else
          ta.setText("");
      } /* alpha */
      else
      { /* TROUBLE! there is NO string array */
        mae.logDRYROTerr("[GSR-HKT] no string[] data");
      }
      
     /* [3] Find first occurance of search string starting at the
      * left column, find the location within nonarray/string
      * version.
      */
      index= displayStr.toUpperCase().indexOf(searchStr.toUpperCase());
      if(index >= 0)
      { /* index in ta */
        bufLoc= findStrLoc(gsr.subAlphaList,searchStr);
        
        if(bufLoc >= 0 && ((bufLoc + searchStrSize) < displayStr.length()))
          ta.select(bufLoc, (bufLoc + searchStrSize));
        
        /* [4] If back at begining then blank textArea */
        if(searchIndex == 0)
        {
          bufLoc= -1;
          ta.setText("");
          ta.select(0, 0); /* select nothing, reset ta */
        }
        previousSearchIndex= searchIndex;
      } /* index in ta */
      
    } /* something to search for */
    else
      ta.setText("");
    
    gsr.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    gsr.buttonPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    gsr.titlePanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    tf.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    ta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  } /* handleKeyTyped */
  
  
  /**
   * containsWildCard() - return location of wildcard '*',
   * -1 if not found. (0 - (1-n))
   * @param iStr is string to search for
   * @return value -1 if not found. (0 - (1-n))
   */
  public int containsWildCard(String iStr)
  { /* containsWildCard */
    char ch[]= iStr.toCharArray();
    int len= iStr.length();
    
    for(int x= 0; x<len; x++)
      if('*' == ch[x])
        return(x);            /* found */
    return(-1);		/* not found */
  } /* containsWildCard */
  
  
  /**
   * howManyWildCards() - return number of wildcards present
   * 0 if none found.
   * @param iStr is string to search for
   * @return # of wildcards else 0 if none
   */
  public int howManyWildCards(String iStr)
  { /* howManyWildCards */
    char ch[]= iStr.toCharArray();
    int len= iStr.length(),
    
    count=0;
    for(int x= 0; x<len; x++)
      if('*' == ch[x])
        count++;		/* keep track of how many wildcards */
    return(count);		/* # found */
  } /* howManyWildCards */
  
  
  /**
   * makeSearchList() - Find all strings in list that have the same begining.
   * Note: this method will limit size to be displayed in Guesser.
   * Take first sizeToDisplay to display.
   * @param data[] is list of data to search through
   * @param dataLen is size of data list
   * @param searchStr is str to look for
   * @param sizeToDisplay is limit size to display size
   * @see Guesser#copyArrayToSizedArray
   */
  public String[] makeSearchList(String data[], int dataLen, String searchStr,
                                 int sizeToDisplay)
  { /* makeSearchList */
    int
      totalLenDisplayed,	/* limit number shown in guesser ta */
      outLen= 0;		/* size of returned str */
    String tempList[]= new String[dataLen];
    
    /* [1] Limit number shown  */
    if(dataLen <= sizeToDisplay)
      totalLenDisplayed= dataLen;
    else
      totalLenDisplayed= sizeToDisplay;
    
    /* [2] go thru list and save ones that match*/
    if(data != null && searchStr != null)
      for(int x= 0; x < dataLen; x++)
        if(data[x].toUpperCase().startsWith(searchStr.toUpperCase()) &&
        totalLenDisplayed > outLen)
          tempList[outLen++]= data[x]; /* save it */
    
    /* [3] make array with exact size */
    return(gsr.copyArrayToSizedArray(tempList,outLen));
  } /* makeSearchList */
  
  
  /**
   * makeSearchList() - Find all strings and make a list, handle wild cards.
   * Note: this method will limit size to be displayed in Guesser.
   * Take first sizeToDisplay to display.
   * Different ways of specifing the search criteria:
   *<PRE>
   *     <chars>*<chars>
   *     *<chars>
   *     <chars>*
   *</PRE>
   * @param wildCard [future] # of wildcards
   * @param data is the list of data to search
   * @param dataLen is the size of the data list
   * @param searchStr is the string to look for
   * @param sizeToDisplay is limit size to display size
   * @return a String array of the exact size.
   * @see Guesser#copyArrayToSizedArray
   * @see #containsWildCard
   * @see #findMatch
   * @see #howManyWildCards
   */
  public String[] makeSearchList(int wildCard, String data[], int dataLen,
                                 String searchStr, int sizeToDisplay)
  { /* makeSearchList */
    int
      wildcards= howManyWildCards(searchStr),
      wildcarLoc= containsWildCard(searchStr), /* location of '*' */
      totalLenDisplayed,	/* limit number shown in guesser ta */
      outLen= 0;		/* size of returned str */
    String tempList[]= new String[dataLen]; /* list to return */
    
    /* [1] Limit number shown */
    if(dataLen <= sizeToDisplay)
      totalLenDisplayed= dataLen;
    else
      totalLenDisplayed= sizeToDisplay;
    
    if(wildcards > 1)
    { /* multiple wildcards */
      int
       searchStrLen= searchStr.length(),
        numberWildCards= 0,
        wildcarLocs[]= new int[3];
      char tempBuf[]= new char[searchStr.length()];
      
      tempBuf= searchStr.toCharArray();
      
      /* get locations of wildcards */
      for(int x= 0; x < searchStrLen; x++)
        if(tempBuf[x] == '*')
          wildcarLocs[numberWildCards++]= x;
      int
        buf1Len= wildcarLocs[0],
        buf2Len= wildcarLocs[1] - wildcarLocs[0] - 1,
        buf3Len= searchStrLen - wildcarLocs[1] - 1;
      char
        buf1[]= new char[buf1Len],
        buf2[]= new char[buf2Len],
        buf3[]= new char[buf3Len];      
      
      /* get rid of wildcard from begining */
      searchStr.getChars(0, wildcarLocs[0], buf1, 0);
      searchStr.getChars(wildcarLocs[0]+1, wildcarLocs[1],buf2, 0);
      searchStr.getChars(wildcarLocs[1]+1, searchStrLen, buf3, 0);
      
      String
        tempStr1= new String(buf1),
        tempStr2= new String(buf2),
        tempStr3= new String(buf3);
      
      /* [] go thru list and save ones that match */
      char tempData[];
      if(data != null && tempStr1 != null &&
         tempStr2 != null && tempStr3 != null)
        for(int x= 0; x < dataLen; x++)
        { /* create list */
          tempData= data[x].toCharArray();
          
          if(findMatch(tempData, data[x].length(),
                       buf1, buf1Len, buf2, buf2Len, buf3, buf3Len) &&
                       totalLenDisplayed > outLen)
          { /* save it */
            tempList[outLen++]= data[x]; 
          }
        } /* create list */
    } /* multiple wildcards */
    
    else
    { /* single wildcards */
      if(wildcarLoc != -1 && data != null && searchStr != null)
      {
        if(wildcarLoc == 0)
        { /* wildcard is first char */
          int newLen= searchStr.length() - 1;
          char buf[]= new char[newLen];
          
          searchStr.getChars(1, newLen+1, buf, 0);/* get rid of wildcard
           * from str begining */
          String tempStr= new String(buf);
          
          if(data != null && tempStr != null)
            for(int x= 0; x < dataLen; x++)
            { /* go thru list and save ones that match */
              if(data[x].toUpperCase().endsWith(tempStr.toUpperCase()) &&
              totalLenDisplayed > outLen)
                tempList[outLen++]= data[x]; /* save it */
            }
        } /* wildcard is first char */
        else
          if(wildcarLoc == (searchStr.length()-1))
          { /* wildcard at end */
            int newLen= searchStr.length()-1;
            char buf[]= new char[newLen];
            
            /* get rid of wildcard from begining */
            searchStr.getChars(0,newLen,buf,0);
            String tempStr= new String(buf);
            
            /* go thru list and save ones that match */
            if(data != null && tempStr != null)
              for(int x= 0; x < dataLen; x++)
                if(data[x].toUpperCase().startsWith(tempStr.toUpperCase()) &&
                   totalLenDisplayed > outLen)
                { /* save it */
                  tempList[outLen++]= data[x]; 
                }
          } /* wildcard at end */
          else
          { /* wildcard in middle */
            int
              newLen= searchStr.length(), /* with wildcard */
              lastLen= newLen - wildcarLoc - 1,
              firstLen= newLen - lastLen - 1;
            char
              bufFirst[]= new char[firstLen],
              bufLast[]= new char[lastLen];
            
            /* get rid of wildcard from middle, seperate into 2 strs */
            searchStr.getChars(0, wildcarLoc, bufFirst,0);
            searchStr.getChars(wildcarLoc+1, newLen,bufLast,0);
            
            String
              tempStrFirst= new String(bufFirst),
              tempStrLast= new String(bufLast);
            
            /* go thru list and save ones that match */
            if(data != null && tempStrFirst != null && tempStrLast != null)
            {
              for(int x= 0; x < dataLen; x++)
                if(data[x].toUpperCase().startsWith(tempStrFirst.toUpperCase()) &&
                   data[x].toUpperCase().endsWith(tempStrLast.toUpperCase()) &&
                   totalLenDisplayed > outLen)
                { /* save it */
                  tempList[outLen++]= data[x]; 
                }
            }
          } /* wildcard in middle */
      }
    } /* single wildcard */
    
    /* [3] make array with exact size */
    return(gsr.copyArrayToSizedArray(tempList,outLen));
  } /* makeSearchList */
  
  
  /**
   * findMatch() - find match of 3 strings in order (2 wild cards) of the form "*abc*".
   * @param searchMainStr is the main search string to test
   * @param mainSize is size of searchMainStr
   * @param buf1 is first buffer
   * @param buf1Size is size of first buffer
   * @param buf2 is first buffer
   * @param buf2Size is size of 2nd buffer
   * @param buf3 is first buffer
   * @param buf3Size is size of third buffer)
   * @return true if found match
   */
  public boolean findMatch(char searchMainStr[], int mainSize,
                           char buf1[], int buf1Size,
                           char buf2[], int buf2Size,
                           char buf3[], int buf3Size)
  { /* findMatch */
    boolean
     foundBuf1Flag= false,
     foundBuf2Flag= false,
     foundBuf3Flag= false,
     foundFlag= true;
    int
      bufCounter= 0,
      mainCounter= 0;
    char
      ch1,
      ch2;
    
    if(buf1Size !=0)
    { /* first buffer > 0 */
      while(mainCounter < mainSize && foundFlag && bufCounter < buf1Size)
      { /* find first str */
        ch1= Character.toUpperCase(searchMainStr[mainCounter++]);
        ch2= Character.toUpperCase(buf1[bufCounter]);
        
        if(ch1 == ch2)
        {
          bufCounter++;
          if(bufCounter >= buf1Size)
          {
            foundFlag= false; /* found first one or at end */
            foundBuf1Flag= true;
          }
        }
        else
          bufCounter= 0; /* reset since not consecutive */
      } /* find first str */
      
      if(!foundBuf1Flag)
        return(false);
    } /* first buffer > 0 */
    
    foundFlag= true;		/* reset foundflag to get into next loop */
    bufCounter= 0;
    
    while(mainCounter < mainSize && foundFlag && bufCounter < buf2Size)
    { /* look for 2nd str */
      ch1= Character.toUpperCase(searchMainStr[mainCounter++]);
      ch2= Character.toUpperCase(buf2[bufCounter]);
      
      if(ch1 == ch2)
      {
        bufCounter++;
        if(bufCounter >= buf2Size)
        {
          foundFlag= false; /* found it */
          
          if(buf3Size == 0)
            return(true); /* found 2 since 3rd is empty */
          
          foundBuf2Flag= true;
        }
      }
      else
        bufCounter= 0;	/* reset since not consecutive */
    } /* look for 2nd str */
    
    if(!foundBuf2Flag)
      return(false);
    
    foundFlag= true;		/* reset foundflag to get into next loop */
    bufCounter= 0;
    
    while(mainCounter < mainSize && foundFlag && bufCounter < buf3Size)
    { /* find 3rd str */
      ch1= Character.toUpperCase(searchMainStr[mainCounter++]);
      ch2= Character.toUpperCase(buf3[bufCounter]);
      
      if(ch1 == ch2)
      {
        bufCounter++;
        if(bufCounter >= buf3Size)
          return(true);	/* found all 3 */
      }
      else
        bufCounter= 0;
    } /* find 3rd str */
    
    return(false);		/* not found */
  } /* findMatch */
  
  
  /**
   * findStrLoc() - Find location within an array of Strings of searchStr.
   * Must be at begining of String[x] not in the middle of the string.
   * @param data is list of strings to search
   * @param searchStr is the search string.
   * @return index of data[] if matched
   */
  int findStrLoc(String data[], String searchStr)
  { /* findStrLoc */
    int
      strSize= 0,		/* length of string in the row */
      total= 0,		  /* keep track of total size of str */
      i= 0;		   	  /* counter */
    
    if(data!=null)
      for(int x= 0; x < data.length; x++)
      {
        if(data[x]!=null)
        {
          strSize= data[x].length();
          
          /* look at each seperate row to find the string */
          i= data[x].toUpperCase().indexOf(searchStr.toUpperCase());
          if(i == 0)
            return(total); /* found, the first character */
          else
            total += (strSize + 1);/* not found or not first occurance */
        }
        else
          return(-1);	/* data[x] null, error */
      }
    return(-1);		/* never found */
  } /* findStrLoc */
  
  
} /* class KeyEventHandler */




/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*                 CLASS MouseEventHandler                        */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

class MouseEventHandler extends MouseAdapter
{ /* class MouseEventHandler */
  /** instance of MAExplorer */
  MAExplorer
    mae;
  /** instance of Guesser */
  Guesser
    gsr;
  /** TextArea list of best guesses */
  TextArea
    ta;
  /** User types in here */
  TextField
    tf;
  /** alpha list */
  String
    alphaList[];
  
  
  /**
   * MouseEventHandler() - constructor
   * @param mae is instance of MAExplorer
   * @param gsr is instance of Guesser
   * @param tf is TextField
   * @param ta is TextArea
   * @param alphaList list of data
   */
  public MouseEventHandler(MAExplorer mae, Guesser gsr, TextField tf,
                           TextArea ta,  String alphaList[])
  { /* MouseEventHandler */
    this.mae= mae;
    this.gsr= gsr;
    this.tf= tf;
    this.ta= ta;
    this.alphaList= alphaList;
  } /* MouseEventHandler */
  
  
  /**
   * resetMouseEventHandler() - reset the alphaList
   * @param alphaList list of data
   */
  public void resetMouseEventHandler(String alphaList[])
  
  { /* resetMouseEventHandler */
    this.alphaList= alphaList;
  } /* resetMouseEventHandler */
  
  
  /**
   * MouseReleased() - handle mouse clicks for ta text area
   * @param evt is mouse released event
   * @see Guesser#updateAssocTextField
   */
  public void mouseReleased(MouseEvent evt)
  { /* MouseReleased */
    int
      caretIndex=0,		/* for PC, seperate index */
      listSize=0,		/* number of rows */
      total= 0,		/* keep track of location */
      strSize= 0,
      taLocation= -1;		/* location of mouse clicked in ta */
    String
      foundStr= null,		/* set text field if found */
      listToSearch[];		/* store numericList/alphaList list */
    
    /* [1] Use alphaList */
    listToSearch= gsr.subAlphaList;
    listSize= gsr.subAlphaList.length;
    
    /* [2] get location of mouse click using getCaretPosition() */
    taLocation= ta.getCaretPosition(); /* cursor location */
    
    /* [3] get str value for that row */
    for(int r= 0; r < listSize; r++)
    { /* search thru rows */
      strSize= listToSearch[r].length(); /* get str size */
      
      if(total <= taLocation && taLocation <= (total + strSize))
      {
        foundStr= listToSearch[r];
        if(mae.isWinPCflag)  /* fixes PC counting problem */
          ta.select(caretIndex, (caretIndex + strSize));
        else
          ta.select(total, (total + strSize));
        break;
      }
      
      if(mae.isWinPCflag)
      { /* fixes PC counting problem */
        caretIndex += strSize + 1;
        total += strSize + 2; /* keep count, +2 for \0\n */
      }
      else
        total += strSize+1;      /* keep count, +1 for \0 */
    } /* search thru rows */
    
    
    /* [4] if found, stuff value into TextField */
    if(foundStr!=null && tf.getText().length() > 0)
    {
      tf.setText(foundStr);
      gsr.updateAssocTextField(foundStr);
    }
  } /* MouseReleased */
  
} /* class MouseEventHandler */

/* end of file Guesser.java */
