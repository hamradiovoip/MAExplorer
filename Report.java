/** File: Report.java */

import java.awt.*;
import java.awt.Color;
import java.util.*;
import java.net.*;
import java.lang.*;
import java.io.*;

/** 
 * The class generates a popup report window for either HP sample or Gene reports. 
 * There are many varients for these reports and these are specified 
 * by the reportMode argument. There may be multiple Report instances if the 
 * reportMode is different for different reports. 
 *<PRE> 
 *<B>List of report modes (defined in MAExplorer class)</B>
 *  RPT_TBL_SAMPLES_DB_INFO         - array report of SamplesDB  
 *  RPT_TBL_HP_DB_INFO              - array report of extra Info samples data (DEPRICATED)       
 *  RPT_TBL_SAMPLES_WEB_LINKS       - array report with active Web links 
 *  RPT_TBL_HP_XY_SET_STAT          - array samples set statistics 
 *  RPT_TBL_OCL_STAT                - array OCL statistics 
 *  RPT_TBL_HP_MN_VAR_STAT          - array samples mean and variance statistics 
 *  RPT_TBL_HP_HP_CORR              - HP vs HP correlation coefficient for only filtered genes      
 *  RPT_TBL_CALIB_DNA_STAT          - array samples Calibration DNA statistics   
 *  RPT_TBL_MAE_PRJ_DB              - project database  
 *
 *  RPT_TBL_HIGH_RATIO              - highest N HP-X/HP-Y ratio genes
 *  RPT_TBL_LOW_RATIO               - lowest N HP-X/HP-Y ratio genes
 *  RPT_TBL_HIGH_F1F2               - highest N current HP F1/F2 ratio genes 
 *  RPT_TBL_LOW_F1F2                - lowest N current HP F1/F2 ratio genes
 *  RPT_TBL_FILTERED_GENES          - data Filtered genes
 *  RPT_TBL_GENE_CLASS              - genes in current GencClass
 *  RPT_TBL_NAMED_GENES             - all named genes
 *  RPT_TBL_EXPR_PROFILE            - expression profiles of filtered genes 
 *  RPT_TBL_CUR_GENE_CLUSTER        - current gene clustering statistics
 *  RPT_TBL_ALL_GENES_CLUSTER       - all genes clustering statistics 
 *  RPT_TBL_KMEANS_CLUSTER          - K-means clustering statistics      
 *  RPT_TBL_MN_KMEANS_CLUSTER       - mean clusters for K-means clusters statistics       
 *  RPT_TBL_EDITED_GENE_LIST        - Edited Gene List       
 *  RPT_TBL_NORMALIZATION_GENE_LIST - Normalization gene list statistics  
 *  RPT_TBL_HIER_CLUSTER           -  hierarchical clusters gene statistics 
 *</PRE>
 *<P>
 * Reports may be either tab-delimited (using ShowStringPopup) or 
 * dynamic spreadsheets and this is specified the the state variable using the
 * tblFmtMode set to either RPT_FMT_TAB_DELIM or RPT_FMT_DYN.
 *
 *<P>
 * The font size and type used in the reports may also be specified.
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
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.14 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see ShowSpreadsheetPopup
 * @see SpreadSheet
 */
 
class Report
{
  /** link to global MAExplorer instance */
  private MAExplorer
    mae; 
  /** link to ClusterGenes instance */
  private ClusterGenes
    clg;        
  /** link to global Config instance */
  private Config
    cfg;
  /** link to global Filter instance */
  private Filter
    fc;                    
  /** link to global GipoTable instance */
  private GipoTable
    gipo;
  /** link to global SamplesTable instance */
  private SamplesTable
    sampDB;
  /** link to global Statistics instance */
  private Statistics
    stat;
    
  /** flag: Do not sort */
  final static int
    DONT_SORT= 0; 
  /** flag: sort rows by assending column data */
  final static int
    ASCENDING= 1;
  /** flag: sort rows by desending column data */
  final static int
    DESCENDING= 2;
    
  /* --- Spreadsheet Cell modes  --- */
  /** Spreadsheet Cell mode: cell has no associated action */
  static int    
    CELL_NOP= SpreadSheet.CELL_NOP;    
  static int    
  /** Spreadsheet Cell mode: cell has URL popup browser associated action */
    CELL_URL= SpreadSheet.CELL_URL;    
  static int        
  /** Spreadsheet Cell mode: cell has popup Expression Profile associated action */ 
    CELL_EXPR_PROFILE= SpreadSheet.CELL_EXPR_PROFILE;
 
  /** Report name */
  private String
    rName;
  /** Report mode */
  private int
    reportMode;
  /** ARG: name for for PopupRegistry */
  private String 
    popupName;             
  /** ARG: property bits for PopupRegistry */
  private int 
    popupPropertyBits;     
  
  /** Table format mode: spreadsheet or tab-delim */
  private int
    tblFmtMode;   
  /** Table used for updating a report */         
  private Table
    updateTable; 
  /** Sort direction: assending or descending */          
  private int
    sortDirection; 
            
  private boolean
    useEPrawIntensValFlag;
  private boolean
    addExprProfFlag;
  private boolean
    addHP_XYstatFlag;
  private boolean
    addOCLstatFlag;
  private boolean
    addHierClusterStatFlag;
  private boolean
    addKmeansClusterStatFlag;
  private boolean
    addKmeansMnAndSDstatFlag;


  /**
   * Report() - constructor for Report
   * @param mae is the MAExplorer instance
   * @param rName is the name of the report used for a title etc.
   * @param reportMode is the mae.RPT_xxxx state
   * @param tblFmtMode is table format mode mae.RPT_FMT_xxxx state
   * @param popupName is the name for the PopupRegistry
   * @param popupPropertyBits are the property bits for the PopupRegistry 
   * @see #makeReport
   */
  Report(MAExplorer mae, String rName, int reportMode, int tblFmtMode,
	 String popupName, int popupPropertyBits)
    { /* Report */
      this.mae= mae;
      clg= mae.clg;
      cfg= mae.cfg;
      fc= mae.fc;
      gipo= mae.gipo;
      sampDB= mae.sampDB;
      stat= mae.stat;
      
      this.rName= (rName==null) ? "Report" : rName;
      this.reportMode= reportMode;
      this.popupName= popupName; 
      this.popupPropertyBits= popupPropertyBits;
      
      if(mae.RPT_FMT_TAB_DELIM!=tblFmtMode &&
       	 mae.RPT_FMT_DYN!=tblFmtMode)
	      tblFmtMode= mae.RPT_FMT_TAB_DELIM;  /* default if none */
      this.tblFmtMode= tblFmtMode;
      updateTable= null;    /* none */
      
      makeReport();
    } /* Report */


  /**
   * setName() - set Report name
   * @param rname is the new report name
   */
  void setName(String rName)
    { /* setName */
      if(rName!=null)
	this.rName= rName;
    } /* setName */

  
  /**
   * makeReport() - make gene-report instance using reportMode to determine report
   * @see CompositeDatabase#getNormHP_XandYdata
   * @see Filter#computeWorkingGeneList
   * @see Gene#setGeneData
   * @see Gene#setGeneData1AndData2
   * @see GeneClass#getGeneListOfCurrentGeneClass
   * @see GeneClass#getGeneListOfGeneClass
   * @see MAExplorer#logDRYROTerr
   * @see MaHybridSample#getF1F2Data
   * @see SamplesTable#makeHPrestrictedTable
   * @see ShowSpreadsheetPopup
   * @see ShowStringPopup
   * @see Table
   * @see Table#getTableRowsSubset
   * @see Table#makeTabDelimReport
   * @see Util#showMsg
   * @see #showGeneReport
   */
  void makeReport()
  { /* makeReport */
    String
    sf1= mae.cfg.fluoresLbl1,
    sf2= mae.cfg.fluoresLbl2;
    if(mae.useCy5OverCy3Flag)
      { /* flip Cy3/Cy5 to Cy5/Cy3 */
        String
          sTmp= sf1;
        sf1= sf2;
        sf2= sTmp;
      }
    String
      dbName= (cfg.dbSubset.length()>0) ? ", " + cfg.dbSubset  : "";
    
    sortDirection= ASCENDING;
    useEPrawIntensValFlag= mae.useEPrawIntensValFlag;
    addExprProfFlag= mae.addExprProfileFlag;
    addHP_XYstatFlag= mae.addHP_XYstatFlag;
    addOCLstatFlag= mae.addOCLstatFlag;
    addHierClusterStatFlag= false;
    addKmeansClusterStatFlag= false; /* only set if K-means clustering*/
    addKmeansMnAndSDstatFlag= false; /* ditto */
    
    if(addExprProfFlag && mae.hps.nHP_E<1)
      addExprProfFlag= false;      /* disallow since no HP-E data */
    if(addHP_XYstatFlag && mae.useHPxySetDataFlag)
      addHP_XYstatFlag= false;  /* disallow since not in HP-XY set mode*/
    
    int
      maxGenesReported= cfg.maxGenesToRpt; /* max# of genes to report */
    String
      title= "GENE REPORT",
      sR= null;                    /* Report string */
    int
      nList= 0,
      eSize= mae.mp.maxGenes;
    float
      xList[]= new float[eSize],   /* [1:nList] quant. data for HP-X/-Y */
      yList[]= new float[eSize];
    Gene
      mList[]= new Gene[eSize];   /* [1:nMlist] genes meeting criteria */
    int
      nMlist= 0;
        
    if(reportMode==mae.RPT_TBL_SAMPLES_DB_INFO)
    { /* Make report on all Samples */
      title= "All Sample Hybridizations in the Database";
      Util.showMsg( "Reporting on " + title);
      Table
        rsTbl= new Table(sampDB.makeHPrestrictedTable(sampDB));
      
      
      if(mae.RPT_FMT_DYN==tblFmtMode)
      { /* display data within a clickable spreadsheet */
        int
          cellFormat[]= new int[rsTbl.tCols+1];
        for(int i=0;i<rsTbl.tCols;i++)
          cellFormat[i]= CELL_NOP;
        cellFormat[sampDB.idxGeneCard_URL]= CELL_URL;
        cellFormat[sampDB.idxHistology_URL]= CELL_URL;
        cellFormat[sampDB.idxModel_URL]= CELL_URL;
        
        /* add base address */
        String
        urlPrefix[]= new String[sampDB.tCols];
        urlPrefix[sampDB.idxGeneCard_URL]= cfg.geneCardURL;
        urlPrefix[sampDB.idxHistology_URL]= cfg.histologyURL;
        urlPrefix[sampDB.idxModel_URL]= cfg.modelsURL;
        
        SimpleTable memWL=
             rsTbl.getTableRowsSubset(rsTbl,       /* ARG: data source*/
                                      rsTbl.tFields, /* fields to use */
                                      null,        /* new names if ! null*/
                                      urlPrefix,   /* if entry ! null*/
                                      null,        /* urlSuffix if ! null*/
                                      rsTbl.tCols, /* # of new fields*/
                                      title);

	     /* [TODO] evaluate if we still need to DEBUG this... */
        //memWL= rsTbl.filterByPrjLogin(memWL);
        
        /* clickable spreadsheet */
        ShowSpreadsheetPopup
        ssp= new ShowSpreadsheetPopup(mae, this,
                                      memWL.tFields,
                                      memWL.tData,
                                      memWL.tRows, /* last row */
                                      0,           /* start row */
                                      memWL.tRows, /* max #rows&cols*/
                                      memWL.tCols,
                                      mae.rptFontSize,
                                      title,
                                      null,         /* altFieldNames[]*/
                                      cellFormat,
                                      null,         /* URL data */
                                      popupName,
                                      popupPropertyBits,
                                      false         /* urlViewerFlag*/
                                      );
      } /* display data within a clickable spreadsheet */
      
      else
        { /* string report */
          /* Restrict to entries which have valid logins */
          sR= rsTbl.makeTabDelimReport("All Samples in the Database\n");
          ShowStringPopup
            sRpt= new ShowStringPopup(mae,sR,25,80, mae.rptFontSize,
                                      null, 0, 0,
                                      popupName, popupPropertyBits,
                                      "maeSamplesRpt.txt");
        } /* string report */
      return;
    } /* Make report on all Samples */
    
    else if(reportMode==mae.RPT_TBL_SAMPLES_WEB_LINKS)
      { /* Make report on all Sample Web Links */
        Util.showMsg( "Reporting on Samples Web links.");
      
        if(mae.RPT_FMT_DYN!=tblFmtMode)
          {
            Util.showMsg("Sorry, only available in Spreadsheet format");
            return;
          }
        
       /* Make a new table of specified fields by fieldList[]
        * and in the order in which they appear in the fieldList[].
        * If an newFieldList[] entry is not null, then use a new field name.
        * If a urlPrefix[] (urlSuffix[]) entry is not null, then prepend
        * (append) it to the data item. This is how we generate clickable
        * URLs by attaching the URL base address and possible suffix.
        * Return the new table.
        */
        int nFields= 4;
        String
          wlTitle= "Array Samples Web Links",
          fieldList[]= {"Sample_ID", "GeneCard_URL",
                        "Histology_URL", "Model_URL"}, /* fields to use */
          newFieldList[]= {"Sample_ID", "GeneCard",
                           "Histology", "Model"};  /* new names if entries not null */
          String urlPrefix[]= new String[nFields];  /* if entry not null*/
          String urlSuffix[]= null;                 /* if entry not null*/
          int cellFormat[]= {CELL_NOP, CELL_URL, CELL_URL, CELL_URL};
          
          urlPrefix[1]= cfg.geneCardURL;            /* add base address */
          urlPrefix[2]= cfg.histologyURL;
          urlPrefix[3]= cfg.modelsURL;
          
          SimpleTable
            memWL= sampDB.getTableRowsSubset(mae.sampDB, /* ARG: data source*/
                                             fieldList,  /* fields to use */
                                             newFieldList, /* new names if entries not null */
                                             urlPrefix,  /* if entry not null*/
                                             urlSuffix,  /* if entry not null*/
                                             nFields,    /* # of new fields*/
                                             wlTitle);
	        if(memWL==null)
            { /* try using DEPRICATED "Membrane_ID" */
              fieldList[0]= "Membrane_ID";
              memWL= sampDB.getTableRowsSubset(mae.sampDB, /* ARG: data source*/
                                               fieldList, /* fields to use */
                                               newFieldList, /* new names if
                                                              * entries not null */
                                               urlPrefix,  /* if entry not null*/
                                               urlSuffix, /* if entry not null*/
                                               nFields,   /* # of new fields*/
                                               wlTitle);
            } /* try using DEPRICATED "Membrane_ID" */
          
          if(memWL==null)
            {
              mae.logDRYROTerr("[R-MR] - can't make Samples WebLinksTbl");
             return;
            }
          
          /* [TODO] evaluate if we still need to DEBUG this... */
          //memWL= sampDB.filterByPrjLogin(memWL);
          
          Table
            rsTbl= new Table(sampDB.makeHPrestrictedTable(new Table(memWL)));
          
          /* display ALL samples within a clickable spreadsheet */
          ShowSpreadsheetPopup
            ssp= new ShowSpreadsheetPopup(mae, this,
                                          rsTbl.tFields,
                                          rsTbl.tData,
                                          rsTbl.tRows,   /* last row */
                                          0,             /* start row */
                                          rsTbl.tRows,   /* max #rows&cols */
                                          rsTbl.tCols,
                                          mae.rptFontSize,
                                          "Samples Web Links",
                                          null,          /* altFieldNames */
                                          cellFormat,
                                          null,          /* URL data */
                                          popupName,
                                          popupPropertyBits,
                                          true           /*  urlViewerFlag */
                                         );
          return;
        } /* Make report on Samples Web links */
      
     else if(reportMode==mae.RPT_TBL_HP_DB_INFO )
     { /* Make report on extra Info for all microarrays */
       if(mae.infoDB==null)
         return;               /* no extra data */
       
       Table
         rsTbl= new Table(sampDB.makeHPrestrictedTable(mae.infoDB));
       
       title= "Extra Information on All Samples";
       Util.showMsg("Reporting on: " + title);
       
       if(mae.RPT_FMT_DYN==tblFmtMode)
         { /* display data within a clickable spreadsheet */
           ShowSpreadsheetPopup
            ssp= new ShowSpreadsheetPopup(mae, this,
                                          rsTbl.tFields,
                                          rsTbl.tData,
                                          rsTbl.tRows, /* last row */
                                          0,           /* start row */
                                          rsTbl.tRows, /* max #rows&cols*/
                                          rsTbl.tCols,
                                          mae.rptFontSize,
                                          title,
                                          null,        /* altFieldNames*/
                                          null,        /* cell type fields */
                                          null,        /* URL data */
                                          popupName,
                                          popupPropertyBits,
                                          false);
         } /* display data within a clickable spreadsheet */
	     
	    else
       { /* string report */
         sR= rsTbl.makeTabDelimReport(title+"\n");
         ShowStringPopup
           sRpt= new ShowStringPopup(mae,sR,25,80,mae.rptFontSize,
                                     null, 0, 0,
                                     popupName, popupPropertyBits,
                                     "maeSampleInfoRpt.txt");
       } /* string report */
	    
	     return;
	  } /* Make report on extra Info for all microarrays */
    
     else if(reportMode==mae.RPT_TBL_MAE_PRJ_DB)
     { /* Make report on MAEprojects.txt DB file */
       Table prjTbl= new Table(mae, mae.prjListFile, "Project List");
       if(prjTbl==null)
       {
         Util.showMsg("No MAE Projects DB exists - can't generate report.");
         Util.popupAlertMsg("No MAE Projects DB exists",
                            "No MAE Projects DB exists - can't generate report.",
                            4, 60);
         return;
       }
       
       title= "List of Local MAE Projects Databases";
       Util.showMsg("Reporting on: " + title);
       
       prjTbl.tRows--;           /* remove last NULL rows */
       int
         maxRows= ShowSpreadsheetPopup.MAX_ROWS_TO_SHOW,
         maxCols= ShowSpreadsheetPopup.DEF_COLS_TO_SHOW;
       
       if(prjTbl.tRows<maxRows)
       { /* make new larger table with stuff at top */
         int
           nRows= maxRows,
           nCols= prjTbl.tCols;
         
         prjTbl= new Table(prjTbl.insertTblInULHCtable(nRows, nCols,
         prjTbl.title));
       } /* make new larger table with stuff at top */
       
       if(mae.RPT_FMT_DYN==tblFmtMode)
       { /* display data within a clickable spreadsheet */
         ShowSpreadsheetPopup
         ssp= new ShowSpreadsheetPopup(mae, this,
                                       prjTbl.tFields,
                                       prjTbl.tData,
                                       prjTbl.tRows, /* last row */
                                       0,            /* start row */
                                       prjTbl.tRows, /* max #rows&cols*/
                                       prjTbl.tCols,
                                       mae.rptFontSize,
                                       title,
                                       null,         /* altFieldNames */
                                       null,         /* cell type fields*/
                                       null,         /* URL data */
                                       popupName,
                                       popupPropertyBits,
                                       false);
       } /* display data within a clickable spreadsheet */
       
       else
       { /* string report */
         sR= prjTbl.makeTabDelimReport(title+"\n");
         ShowStringPopup
           sRpt= new ShowStringPopup(mae,sR,25,80,mae.rptFontSize,
                                     null, 0, 0,
                                     popupName, popupPropertyBits,
                                     "maeProjectsRpt.txt");
       } /* string report */
       
       return;
     } /* Make report on MAEprojects.txt DB file */
    
     else if(reportMode==mae.RPT_TBL_HP_HP_CORR)
     { /* Make report on HP vs HP correlations */
       SimpleTable hphpCT= mae.cdb.calcHP_HPcorrelations();
       /* recalc & create table*/
       String
         sMsg=" HP vs. HP correlation coefficients table of Filtered genes";
       if(hphpCT==null)
       {
         Util.showMsg("Can't make"+ sMsg);
         Util.popupAlertMsg("Can't make HP vs HP correlation coefficients report",
                            "Can't make HP vs HP correlation coefficients report.",
                            4, 60);
         return;
       }
       else
         Util.showMsg( "Reporting on"+sMsg);
       title= hphpCT.title + dbName;
       if(mae.RPT_FMT_DYN==tblFmtMode)
       { /* Display ALL correlations in a clickable spreadsheet */
         ShowSpreadsheetPopup 
           ssp= new ShowSpreadsheetPopup(mae, this,
                                         hphpCT.tFields,
                                         hphpCT.tData,
                                         hphpCT.tRows, /* last row */
                                         0,            /* start row */
                                         hphpCT.tRows, /* max #rows&cols */
                                         hphpCT.tCols,
                                         mae.rptFontSize,
                                         title,
                                         null,         /* altFieldNames */
                                         null,         /* cell type fields */
                                         null,         /* URL data */
                                         popupName,
                                         popupPropertyBits,
                                         false);
       }
       else
       { /* string report */
         sR= (new Table(hphpCT)).makeTabDelimReport(title+"\n");
         ShowStringPopup
           sRpt= new ShowStringPopup(mae,sR,25,80,mae.rptFontSize,
                                    null, 0, 0,
                                    popupName, popupPropertyBits,
                                    "maeHPvsHPcorrelationRpt.txt");
       }
       return;
     } /* Make report on HP vs HP correlations */
    
    
     else if(reportMode==mae.RPT_TBL_CALIB_DNA_STAT)
     { /* Make report on Calibration DNA Statistics */
       SimpleTable calDNA= mae.cdb.calDNAhybSamplesTable(); /* recalculate*/
       String sMsg=" calibration DNA array statistics";
       if(calDNA==null)
       {
         Util.showMsg("Can't make"+sMsg+ " table");
         Util.popupAlertMsg("Can't make"+sMsg+ " table",
                            "Can't make"+sMsg+ " table",
                            4, 60);
         return;
       }
       else
         Util.showMsg( "Reporting"+sMsg);
       title= calDNA.title + dbName;
       if(mae.RPT_FMT_DYN==tblFmtMode)
       { /* Display Calibration CNA statistics in dyn. spreadsheet */
         ShowSpreadsheetPopup
         ssp= new ShowSpreadsheetPopup(mae, this,
                                       calDNA.tFields,
                                       calDNA.tData,
                                       calDNA.tRows, /* last row */
                                       0,            /* start row */
                                       calDNA.tRows, /* max #rows&cols */
                                       calDNA.tCols,
                                       mae.rptFontSize,
                                       title,
                                       null,          /* altFieldNames */
                                       null,          /* cell type fields*/
                                       null,          /* URL data */
                                       popupName,
                                       popupPropertyBits,
                                       false);
       }
       else
       { /* string report */
         sR= (new Table(calDNA)).makeTabDelimReport(title);
         ShowStringPopup
           sRpt= new ShowStringPopup(mae,sR,25,80,mae.rptFontSize,
                                     null, 0, 0,
                                     popupName, popupPropertyBits,
                                     "maeCalibDnaRpt.txt");
       }
       return;
     } /* Make report on Calibration DNA Statistics */
    
     else if(reportMode==mae.RPT_TBL_HP_MN_VAR_STAT)
     { /* Make report on H.P. mean, variance & median statistics */
       Table hpMnVar= mae.cdb.createHPmeanAndVarTable();
       String sMsg= " Sample HP mean and variance statistics";
       if(hpMnVar==null)
       {
         Util.showMsg("Can't make"+sMsg+" table");
         Util.popupAlertMsg("Can't make"+sMsg+ " table",
                            "Can't make"+sMsg+ " table",
                            4, 60);
         return;
       }
       else
         Util.showMsg( "Reporting"+sMsg);
       title= hpMnVar.title + dbName;
       if(mae.RPT_FMT_DYN==tblFmtMode)
       { /* Display Calibration CNA statistics in a dyn. spreadsheet */
         ShowSpreadsheetPopup
           ssp= new ShowSpreadsheetPopup(mae, this,
                                         hpMnVar.tFields,
                                         hpMnVar.tData,
                                         hpMnVar.tRows, /* last row */
                                         0,             /* start row */
                                         hpMnVar.tRows, /* max #rows&cols */
                                         hpMnVar.tCols,
                                         mae.rptFontSize,
                                         title,
                                         null,          /* altFieldNames */
                                         null,          /* cell type fields*/
                                         null,          /* URL data */
                                         popupName,
                                         popupPropertyBits,
                                         false);
       }
       else
       { /* string report */
         sR= hpMnVar.makeTabDelimReport(hpMnVar.title+"\n");
         ShowStringPopup
           sRpt= new ShowStringPopup(mae,sR,25,80,mae.rptFontSize,
                                     null, 0, 0,
                                     popupName, popupPropertyBits,
                                     "maeSampleStatisticsRpt.txt");
       }
       return;
     } /* Make report on H.P. mean, variance & median statistics */
    
     else if(reportMode==mae.RPT_TBL_NAMED_GENES ||
             reportMode==mae.RPT_TBL_EDITED_GENE_LIST ||
             reportMode==mae.RPT_TBL_NORMALIZATION_GENE_LIST ||
             reportMode==mae.RPT_TBL_KMEANS_CLUSTER ||
             reportMode==mae.RPT_TBL_MN_KMEANS_CLUSTER ||
             reportMode==mae.RPT_TBL_HIER_CLUSTER ||
             reportMode==mae.RPT_TBL_GENE_CLASS ||
             reportMode==mae.RPT_TBL_FILTERED_GENES ||
             reportMode==mae.RPT_TBL_EXPR_PROFILE ||
             reportMode==mae.RPT_TBL_HP_XY_SET_STAT ||
             reportMode==mae.RPT_TBL_OCL_STAT)
     { /* make report of named and filtered genes*/
       fc.computeWorkingGeneList(); /* Run the filter */
       
       String dataName= null;
       GeneList reportCL= null;     /* will set depending on the mode */
       
       if(reportMode==mae.RPT_TBL_GENE_CLASS)
       { /* Make report on current GeneClass */
         sortDirection= ASCENDING;
         sR= " of Genes in Current GeneClass '"+
             mae.gct.curGeneClassName + "'";
         reportCL= mae.gct.getGeneListOfCurrentGeneClass();
       }
       
       else if(reportMode==mae.RPT_TBL_EDITED_GENE_LIST)
       { /* Make report on edited genes */
         sortDirection= ASCENDING;
         sR= " of Genes in 'Edited Gene List'";
         reportCL= mae.gct.editedCL;
       }
       
       else if(reportMode==mae.RPT_TBL_NORMALIZATION_GENE_LIST)
       { /* Make report on edited genes */
         sortDirection= ASCENDING;
         sR= " of Genes in Normalization Gene List'";
         reportCL= mae.gct.normCL;
       }
       
       else if(reportMode==mae.RPT_TBL_HIER_CLUSTER)
       { /* Make report on all Hierarchical clustered genes */
         sortDirection= DONT_SORT;
         reportCL= ClusterGenes.curClusterCL;
         sR= " of Hierarchical Clusters of "+
             reportCL.length + " Genes";
        /* Note that itit reports the expression profile.
         * This should be put into the table */
         addHierClusterStatFlag= true;
       }
       
       else if(reportMode==mae.RPT_TBL_NAMED_GENES)
       { /* Make report on all named genes */
         sortDirection= ASCENDING;
         sR= " of Genes of All Named Genes";
         reportCL= mae.gct.getGeneListOfGeneClass("ALL NAMED GENES");
       }
       
       else if(reportMode==mae.RPT_TBL_KMEANS_CLUSTER ||
       reportMode==mae.RPT_TBL_MN_KMEANS_CLUSTER)
       { /* Make report on all K-means clustered genes */
         sortDirection= DONT_SORT;
         if(reportMode==mae.RPT_TBL_MN_KMEANS_CLUSTER)
         {
           reportCL= clg.mnClustersCL;
           addKmeansMnAndSDstatFlag= true;
         }
         else
           reportCL= fc.KmeansNodesCL;
         sR= " of K-means clusters ("+ clg.maxKmeansNodes +
             ") of "+reportCL.length + " genes clustered - initial gene [" +
             reportCL.mList[0].Master_ID+ "]";
        /* Note that it encodes node and count info
         * gene.clusterNodeNbr and gene.nGeneClustersCnt and
         * distance to K-means node in gene.data. This should
         * be put into the table */
         addKmeansClusterStatFlag= true;
       }
       
       else if(reportMode==mae.RPT_TBL_FILTERED_GENES ||
               reportMode==mae.RPT_TBL_EXPR_PROFILE ||
               reportMode==mae.RPT_TBL_HP_XY_SET_STAT||
               reportMode==mae.RPT_TBL_OCL_STAT)
       { /* Make report on filtered genes */
         sortDirection= ASCENDING;
         if(reportMode==mae.RPT_TBL_EXPR_PROFILE)
         {
           if(mae.hps.nHP_E<1)
           {
             Util.showMsg(
             "There are no samples in the HP-E list - aborting report.");
             return;
           }
           addExprProfFlag= true;
           sR= " of expression profiles of Filtered Genes";
         }
         else if(reportMode==mae.RPT_TBL_HP_XY_SET_STAT)
         {
           addHP_XYstatFlag= true;
           sR= " of HP-X/Y 'set' statistics of Filtered Genes";
         }
         else if(reportMode==mae.RPT_TBL_OCL_STAT)
         {
           addOCLstatFlag= true;
           sR= " of Ordered Condition List statistics of Filtered Genes";
         }
         else
           sR= " of Filtered Genes";
         reportCL= fc.workingCL;
       }
       
       int
         gid,
         nGenes= reportCL.length;
       Gene gene;
       nMlist= 0;
       
       /* Make a list of existing genes - just verify they exist... */
       title += " - " + sR + dbName;
       Util.showMsg(title);
       
       for(int k=0;k<nGenes;k++)
       { /* build gene list */
         gene= reportCL.mList[k];
         if(gene==null)
           continue;
         gid= gene.gid;
         
         /* Look for dryrot */
         if(gid!=-1 && gene.mid!=-1)
         { /* only look at existing genes */
           /* Report will be generated from this gene list */
           mList[nMlist++]= gene;
         }
       } /* build gene list */
       
       showGeneReport(mList, nMlist, nMlist, title, sortDirection,
                      dataName,   /* don't show dataName */
                      true,       /* show gene name as well */
                      tblFmtMode, reportMode);
       
       return;
     } /* make report of named and filtered genes*/
    
     else if(reportMode==mae.RPT_TBL_CUR_GENE_CLUSTER)
     { /* make cluster report for current gene */
       title= "Genes which cluster to gene ["+
              ClusterGenes.curGene.Master_ID+
              "] < cluster distance ["+ cfg.clusterDistThr+"]";
       sortDirection= ASCENDING;
       
       nList= ClusterGenes.curClusterCL.length;
       
       nMlist= 0;
       for(int i=1; i<=nList; i++)
       { /* add ratio data data */
         Gene gene= ClusterGenes.curClusterCL.mList[i];
         /* Report will be generated from this gene list */
         if(gene!=null)
           mList[nMlist++]= gene;
       } /* add ratio data data */
       
       int tfm= ((mae.RPT_FMT_DYN!=tblFmtMode)
                   ? mae.RPT_FMT_TAB_DELIM : tblFmtMode);
       
       showGeneReport(mList, nMlist, nMlist /* maxGenesReported*/,
                      title, sortDirection,
                      "Cluster Distance", true /* show gene name as well */,
                      tfm, reportMode);
                      /* false, false, false, false */
       return;
     } /* make cluster report for current gene */
    
     else if(reportMode==mae.RPT_TBL_ALL_GENES_CLUSTER)
     { /* make cluster report for all Filtered gene */
       title= "Number of genes in clusters of Filtered genes for"+
              " cluster distance < "+ cfg.clusterDistThr+"";
       sortDirection= DESCENDING;       
       nList= fc.workingCL.length;
       nMlist= 0;
       
       Gene  gene;
       for(int i=1; i<=nList; i++)
       { /* add #genes in cluster data data */
         gene= fc.workingCL.mList[i];
         /* Report will be generated from this gene list */
         if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
           continue;            /* ignore bogus spots */
         mList[nMlist++]= gene;
         gene.setGeneData((float)gene.nGeneClustersCnt);
       } /* add ratio data data */
       
       int tfm= ((mae.RPT_FMT_DYN!=tblFmtMode)
       ? mae.RPT_FMT_TAB_DELIM : tblFmtMode);
       
       showGeneReport(mList, nMlist, nMlist /* maxGenesReported*/,
                      title, sortDirection,
                      "# genes/cluster", true /* show gene name as well */,
                      tfm, reportMode);
                     /* , false, false, false, false); */
       return;
     } /* make cluster report for all Filtered gene */
    
     else if(reportMode==mae.RPT_TBL_HIGH_RATIO ||
             reportMode==mae.RPT_TBL_LOW_RATIO)
     { /* make ratio report of HP-X/HP-Y data */
       fc.computeWorkingGeneList();      /* Run the filter */
       
       if(!mae.useHPxySetDataFlag &&
          (mae.msX==mae.msY || mae.msY==null))
       {
         Util.showMsg("Set HP-X & HP-Y to different samples.");
         return;
       }
       
       else if(mae.useHPxySetDataFlag &&
       (mae.hps.nHP_X==0 || mae.hps.nHP_Y==0))
       {
         Util.showMsg("Add samples to HP-X and HP-Y sets first.");
         return;
       }
       
       String
         sXlbl= ((!mae.useHPxySetDataFlag)
                   ? mae.msX.hpName : mae.classNameX),
         sYlbl= ((!mae.useHPxySetDataFlag)
                   ? mae.msY.hpName : mae.classNameY),
         sMsg= "Filtered genes with "+cfg.maxGenesToRpt+" ";
                   
       if(reportMode==mae.RPT_TBL_LOW_RATIO)
       {
         sR= sMsg+"Lowest ";
         sortDirection= ASCENDING;
       }
       else
       {
         sR= sMsg+"Highest ";
         sortDirection= DESCENDING;
       }
       
       sR += ((mae.isZscoreFlag) ? "Zdiffs" : "ratios")+
              " HP-X["+ sXlbl+ "] "+
              ((mae.isZscoreFlag) ? "-" :"/")+ " HP-Y["+ sYlbl +"]";       
       title += " - " + sR;
       
       nList= mae.cdb.getNormHP_XandYdata(xList,yList, null,
                                          fc.displayCL, /* save results */
                                          mae.msX, mae.msY, false, false);
       String changeStr= (mae.isZscoreFlag)
                            ? "Zdiff (HP-X - HP-Y)"
                            : "Ratio HP-X/HP-Y";
       nMlist= 0;
       for(int i=0; i<nList; i++)
       { /* add ratio/Zscore data */
         Gene gene= fc.displayCL.mList[i];
         if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
           continue;            /* ignore bogus spots */
         float
           g1= xList[i],
           g2= yList[i],
           changeData= (mae.isZscoreFlag) ? (g1-g2) : g1/g2;           
         gene.setGeneData(changeData);
         gene.setGeneData1AndData2(g1,g2);
         /* Report will be generated from this gene list */
         mList[nMlist++]= gene;
       } /* add ratio/Zscore data */
       
       int tfm= ((mae.RPT_FMT_DYN!=tblFmtMode)
                    ? mae.RPT_FMT_TAB_DELIM : tblFmtMode);
       
       showGeneReport(mList, nMlist, maxGenesReported, title,
                      sortDirection, changeStr,
                      true /* show gene name as well */,
                      tfm, reportMode);
       return;
     } /* make ratio report of HP-X/HP-Y data  */
    
     else if(reportMode==mae.RPT_TBL_HIGH_F1F2 ||
             reportMode==mae.RPT_TBL_LOW_F1F2)
     { /* make F1/F2 report */
       String sMsg= "Filtered genes with "+cfg.maxGenesToRpt+" ";
       
       if(reportMode==mae.RPT_TBL_LOW_F1F2)
       {
         sR= sMsg+"lowest ";
         sortDirection= ASCENDING;
       }
       else
       {
         sR= sMsg+"highest ";
         sortDirection= DESCENDING;
       }
       
       sR += (mae.isZscoreFlag)
               ? "("+sf1+"-"+sf2+") Zdiffs"
               : sf1+"/"+sf2+" ratios";       
       title += " - " + sR+" for sample "+mae.ms.fullStageText;
       
       nList= mae.ms.getF1F2Data(xList, yList, null, fc.displayCL,
                                 false, false, false);       
       nMlist= 0;
       Gene gene;
       int mid;
       float
         g1, g2, changeData;
       
       for(int i=0; i<nList; i++)
       { /* add ratio/Zscore data */
         gene= fc.displayCL.mList[i];
         if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
           continue;            /* ignore bogus spots */
         mid= gene.mid;
         g1= xList[i];
         g2= yList[i];
         changeData= (mae.isZscoreFlag) ? (g1-g2) : g1/g2;
         gene.setGeneData(changeData);
         gene.setGeneData1AndData2(g1,g2);
         mList[nMlist++]= gene;
       } /* add ratio/Zscore data */
     } /* make F1/F2 report */
    
    int tfm= ((mae.RPT_FMT_DYN!=tblFmtMode)
                 ? mae.RPT_FMT_TAB_DELIM : tblFmtMode);
    String changeStr= (mae.isZscoreFlag)
                        ? "Zdiff ("+sf1+"-"+sf2+")"
                        : "Ratio "+sf1+"/"+sf2;
    showGeneReport(mList, nMlist, maxGenesReported, title, sortDirection,
                   changeStr, true /* show gene name as well */,
                   tfm, reportMode);
    
    return;
  } /* makeReport */
  
  
  /**
   * createTableOfExprProfiles() - make Table of expression profiles for mList[0:nC-1]
   * @param mList is the list of genes
   * @param nC is the number of genes
   * @param normByHP_Xflag is the normalize by HP-X sample flag
   * @return table of expression profiles
   * @see ExprProfile
   * @see ExprProfile#setupData
   * @see Table
   * @see Util#cvf2s
   */
  private Table createTableOfExprProfiles(Gene mList[], int nC,
                                          boolean normByHP_Xflag)
  { /* createTableOfExprProfiles */
    if(mList==null || nC==0)
      return(null);
    
    int
      nCols= mae.hps.nHP_E,
      nRows= nC;
    Table tR=  new Table(mae, nRows, nCols, "ExprProfile", "");
    
    for(int c=0;c<nCols;c++)
    {
      String hpStr= mae.hps.msListE[c+1].hpName; /* or full study str*/
      tR.tFields[c]= hpStr;
    }
    
    ExprProfile ep= new ExprProfile(mae,-1, false);
    float
      epVal,
      dataC,
      maxData= 0.0F;  /* (float)mae.MAX_INTENSITY */
    int
      mid,
      c;
    String sEPval;
    
    if(!useEPrawIntensValFlag && !normByHP_Xflag)
      for(int r=0;r<nRows;r++)
      { /* compute maxData for all data */
        mid= mList[r].mid;
        if(ep.setupData(mid))
          for(c=0;c<nCols;c++)
          {
            epVal= ep.hpData[c];
            if(maxData<epVal)
              maxData= epVal;
          }
      }
    
    for(int r=0;r<nRows;r++)
    {
      mid= mList[r].mid;
      if(ep.setupData(mid))
        if(normByHP_Xflag)
          maxData= ep.hpData[0];
      for(c=0;c<nCols;c++)
      {
        dataC= ep.hpData[c];
        if(useEPrawIntensValFlag)
          sEPval= (""+(int)dataC);
        else
          sEPval= Util.cvf2s((dataC/maxData),4);
        tR.tData[r][c]= sEPval;
      }
    }
    
    return(tR);
  } /* createTableOfExprProfiles */
    
  
  /**
   * createTableOfOCL_Fstat() - make Table of OCL F-test for mList[0:nC-1]
   * If the OCL F-Test filter is active, then report F-test statistics
   * @param mList is the list of genes
   * @param nC is the number of genes
   * @return table of X Y statistics
   * @see HPxyData#updateDataAndStat
   * @see Statistics#calcNCondFtestStat
   * @see Table
   * @see Util#cvf2s
   */
  private Table createTableOfOCL_Fstat(Gene[] mList, int nC)
  { /* createTableOfOCL_Fstat*/
    if(mList==null || nC==0)
      return(null);
    
    HPxyData hpxy= mae.cdb.hpXYdata;
    Condition cdList= mae.cdList;
    MJAsample mjaSample= mae.mja.mjaSample;
    boolean ok= false;    
  
    /* [1] Get lists of samples from the current Ordered Condition List */
    int curOCLidx= cdList.curOCLidx;
    if(cdList.curOCLidx==-1)
      return(null);       /* There is no current OCL */
     
    /* [1.1] Get the list of conditions in the OCL */
    Condition 
      cd,                 /* working condition */
      ocl[]= cdList.orderedCondList[cdList.curOCLidx];
    int
      nConditions= cdList.nOrderedCondList[curOCLidx],
      nCondData[]= new int[nConditions]; /* # of samples in each condition */ 
    String
      condName[]= new String[nConditions];
    
    /* [1.2] Get lists of samples from the current Ordered Condition List.
     * Current set of sample indexes for samples in the Conditions in the
     * Ordered Condition List to test. [nConditions][nsamples per condition]
     */
    int  
      allSamplesIndex[][]= new int[nConditions][],
      nCondSamples,        /* # of samples/condition */
      nSamples,            /* working # of samples */ 
      samplesIndex[],      /* Working sample indexes in working condition */  
      sampleNbrList[];     /* working list of samples/condition */ 
    MaHybridSample msList[];
     
    /* [1.3] Working current set of data for gene mid for samples in the 
     * Conditions in the Ordered Condition List to test.
     * [nConditions][nsamples per condition]
     */
    float condData[][]= new float[nConditions][];
     
    for(int c=0;c<nConditions;c++)
    { /* get sample indices for all conditions */
      cd= ocl[c];                /* get the working condition */
      msList= cd.getHPlist();    /* get list of samples */
      if(msList==null)
        return(null);           /* fatal error */
      nSamples= cd.nMScond;
      condName[c]= cd.cName;
      nCondData[c]= nSamples;
      sampleNbrList= new int[cd.nMScond];  /* make a new list */
      for(int i=1;i<=nSamples;i++)
        sampleNbrList[i-1]= MJAbase.lookupHPsampleNbr(msList[i]);
      allSamplesIndex[c]= sampleNbrList;
      condData[c]= new float[nSamples];
    } /* get sample indices for all conditions */
    
    /* Working normalized sample data for gene mid for working
     * data [nCondSamples]
     */
    float
      dataS[];
    int
      nExtra,
      nCols= 0,                     /* is computed */
      nRows= nC,
      mid;
    
    /* [1.5] Build the field names list */
    String 
      fBaseNames[]= {"p-Value", "f-stat", "dfWithin", "dfBetween",
                      "mnSqWithin", "mnSqBetween"
                    },           
      fNames[]= new String[fBaseNames.length+4*nConditions];  
     
    /* [1.5.1] Build field list */ 
    for(int i=0;i<fBaseNames.length;i++)
      fNames[nCols++]= fBaseNames[i];
    for(int c=0;c<nConditions;c++)
    {
      fNames[nCols++]= "c["+condName[c]+"]-Mean";
      fNames[nCols++]= "c["+condName[c]+"]-StdDev";
      fNames[nCols++]= "c["+condName[c]+"]-CV";
      fNames[nCols++]= "c["+condName[c]+"]-Nbr";
    }  
   
    Table tR= new Table(mae, nRows, nCols, "HP_OCL_Fstat", "");
    
    for(int i=0;i<nCols;i++)
      tR.tFields[i]= fNames[i];
    
    /* [2] Generate the rows data and write out into the table */
    for(int r=0;r<nRows;r++)
    { /* generate a row of data for this gene */
      mid= mList[r].mid;
      
      /* [2.1] Get the condData sample data[c][nCondData[c]] for
       * conditions c in [0:nCondition-1] for the mid and the
       * current normalization and ratio mode.
       */
      for(int c=0;c<nConditions;c++)
      { /* get the data for each set of samples in condition c */
        samplesIndex= allSamplesIndex[c];
       /* Build the working set of condition data for gene mid.
        * Lookup the data for samples in the list and save it in
        * the precomputed condData[] array of 1D vectors.
        */
        mjaSample.getSamplesDataVector(condData[c], samplesIndex, mid);
      } /* get the data for each set of samples in condition c */
      
      /* [2.2] Compute the F-test on the OCL data computes:
       *    pFnConds - p value
       *    fStatNconds - f statistic
       *    mnSqWithin - mean within class variance
       *    mnSqBetween - mean between class variance
       *    dfWithin - degrees of freedom Within conditions
       *    dfBetween - degrees of freedom Between conditions
       *
       * Returns false if any of the data is invalid or the beta fct fails.
       */
      ok= stat.calcNCondFtestStat(condData, nCondData, nConditions);
      
      /* [3] Test the p-value computed in the F-test against the pValue
       * slider threshold. This tests the Null-hypothesis that
       * there is no difference between the means as there is
       * significant different variance between the conditions
       */
      int k= 0;
      tR.tData[r][k++]= (ok) ? Util.cvf2s((float)stat.pFnConds,5) : "no F-test";
      tR.tData[r][k++]= Util.cvf2s((float)stat.fStatNconds,5);
      tR.tData[r][k++]= Util.cvf2s((float)stat.dfWithin,1);
      tR.tData[r][k++]= Util.cvf2s((float)stat.dfBetween,1);
      tR.tData[r][k++]= Util.cvf2s((float)stat.mnSqWithin,4);
      tR.tData[r][k++]= Util.cvf2s((float)stat.mnSqBetween,4);
      
      for(int c=0;c<nConditions;c++)
      {
        tR.tData[r][k++]= Util.cvf2s((float)stat.mean[c],4);
        tR.tData[r][k++]= Util.cvf2s((float)stat.variance[c],4);
        tR.tData[r][k++]= Util.cvf2s((float)(stat.variance[c]/stat.mean[c]),4);
        tR.tData[r][k++]= (""+stat.nCondData[c]);
      }
    } /* generate a row of data for this gene */
    
    return(tR);
  } /* createTableOfOCL_Fstat */  
  
  
  /**
   * createTableOfHP_XYstat() - make Table of HP_XYstatistics for mList[0:nC-1]
   * If the t-Test filter is active, then report t-test statistics
   * If the KS-Test filter is active, then report KS-test statistics
   * @param mList is the list of genes
   * @param nC is the number of genes
   * @return table of X Y statistics
   * @see HPxyData#updateDataAndStat
   * @see Statistics#calcTandPvalues
   * @see Table
   * @see Util#cvf2s
   */
  private Table createTableOfHP_XYstat(Gene[] mList, int nC)
  { /* createTableOfHP_XYstat*/
    if(mList==null || nC==0)
      return(null);
    
    HPxyData hpxy= mae.cdb.hpXYdata;
    
    boolean
      ok= false,
      hasAtLeast2SamplesFlag= (hpxy.nX>1 && hpxy.nY>1),
      do_t_TestFlag= (hasAtLeast2SamplesFlag && mae.tTestXYsetsFilterFlag),
      do_KS_TestFlag= (hasAtLeast2SamplesFlag && mae.KS_TestXYsetsFilterFlag);
    
    /* [1.1] Build the field names list */
    String 
      fBaseNames[]= {"p-Value",
                     ((mae.isZscoreFlag) ? "mnX-mnY" : "mnX/mnY"),
                     "HP-X:mn", "HP-Y:mn", "HP-X:S.D.", "HP-Y:S.D.",
                     "HP-X:CV", "HP-Y:CV", "HP-X:n", "HP-Y:n"
                    },
      f_tTestNames[]= {"t-Stat", "df", "pF-sameVar"},
      f_KS_testNames[]= {"D-Stat", "df"},      
      fNames[]= fBaseNames;     
       
    int
      s,
      k,
      nExtra,
      nCols= fNames.length,
      nRows= nC,
      mid,
      nX= hpxy.nX,
      nY= hpxy.nX;
    double
      dataX[]= null,
      dataY[]= null;
    
    if(do_t_TestFlag)
    { /* init for t-test */   
      nCols= 0;
      fNames= new String[fBaseNames.length+f_tTestNames.length];
      for(int i=0;i<fBaseNames.length;i++)
        fNames[nCols++]= fBaseNames[i];
      for(int i=0;i<f_tTestNames.length;i++)
        fNames[nCols++]= f_tTestNames[i];
    } /* init for t-test */     
    
    if(do_KS_TestFlag)
    { /* init for KS test */     
      nCols= 0;
      fNames= new String[fBaseNames.length+f_KS_testNames.length];
      for(int i=0;i<fBaseNames.length;i++)
        fNames[nCols++]= fBaseNames[i];
      for(int i=0;i<f_KS_testNames.length;i++)
        fNames[nCols++]= f_KS_testNames[i];
    } /* init for KS test */     
    
    /* [2] Build the table */
    Table tR= new Table(mae, nRows, nCols, "HP_XYstat", "");
    
    for(int i=0;i<nCols;i++)
      tR.tFields[i]= fNames[i];
    
    for(int r=0;r<nRows;r++)
    { /* generate a row of data for this gene */
      mid= mList[r].mid;
      boolean okXY= hpxy.updateDataAndStat(mid);
      float
        pValue= 0.0F,
        ratioXY= (mae.isZscoreFlag)
                   ? (hpxy.mnXdata - hpxy.mnYdata)
                   : ((hpxy.mnYdata==0.0F)
                       ? 0.0F : (hpxy.mnXdata/hpxy.mnYdata));
      if(!okXY)
      {
        ratioXY= 0.0F;
        hpxy.clearXYdata();     /* clear X and Y mn, cv, stdDev data */
      }
      if(do_t_TestFlag && okXY)
      { /* add t-Test statistics */
        /* Calc t and p-values given (n1,m1,s1) and (n2,m2,s2),
         * calc f, t, p, dF.
         * It computes:
         *    f - calculated f statistic
         *    t - t or t' statistic
         *    pT - t-test p-value w/NULL hypoth
         *    pF - f-test p-value w/NULL hypoth
         *    dF - degrees of freedom
         */
        ok= mae.stat.calcTandPvalues(hpxy.nX, hpxy.nY,
                                     hpxy.mnXdata, hpxy.mnYdata,
                                     hpxy.stdDevXdata, hpxy.stdDevYdata);
        pValue= (float)stat.pT;
      } /* add t-Test statistics */
                       
      if(do_KS_TestFlag && okXY)
      { /* calc KS-statistics */
        /* get (means, std-dev, n) data to compute p-value */
        if(!hpxy.updateDataAndStat(mid))
         continue;    
        if(dataX==null)
        { /* allocate once */
          nX= hpxy.nX;
          nY= hpxy.nY;
          dataX= new double[nX];          
          dataY= new double[nY];
        }
        /* Copy data from float to double */
        for(s=0;s<nX;s++)
          dataX[s]= (double)hpxy.hpDataX[s];
        for(s=0;s<nY;s++)
          dataY[s]= (double)hpxy.hpDataY[s];
              
        /* Calc KS statistics given (data1[],n1) (data2[],n2),
         * It computes:
         *    ksD - D statistic
         *    pKS - KS test p-value w/NULL hypoth
         *    dFks - degrees of freedom
         */
        ok= stat.calcKStestStat(dataX, hpxy.nX, dataY, hpxy.nY);
        pValue= (float)stat.pKS;
      } /* calc KS-statistics */
      
      k= 0;            /* reset index */
      tR.tData[r][k++]= (ok) ? Util.cvf2s(pValue,4) : "no t-test";
      tR.tData[r][k++]= Util.cvf2s(ratioXY,4);
      tR.tData[r][k++]= Util.cvf2s(hpxy.mnXdata,4);
      tR.tData[r][k++]= Util.cvf2s(hpxy.mnYdata,4);
      tR.tData[r][k++]= Util.cvf2s(hpxy.stdDevXdata,4);
      tR.tData[r][k++]= Util.cvf2s(hpxy.stdDevYdata,4);
      tR.tData[r][k++]= Util.cvf2s(hpxy.cvXdata,4);
      tR.tData[r][k++]= Util.cvf2s(hpxy.cvYdata,4);
      tR.tData[r][k++]= ""+hpxy.nX;
      tR.tData[r][k++]= ""+hpxy.nY;
      
      /* Add additional test-specific data */
      if(do_t_TestFlag)
      {       
        tR.tData[r][k++]= Util.cvf2s((float)stat.t,4);
        tR.tData[r][k++]= Util.cvf2s((float)stat.dF,1);
        tR.tData[r][k++]= Util.cvf2s((float)stat.pF,4);
      }
      if(do_KS_TestFlag)
      {      
        tR.tData[r][k++]= Util.cvf2s((float)stat.ksD,4);
        tR.tData[r][k++]= Util.cvf2s((float)stat.dFks,1);        
      }
    } /* generate a row of data for this gene */
    
    return(tR);
  } /* createTableOfHP_XYstat */
  
  
  /**
   * createTableKmeansClusterStats() - make Table of Kmeans clusters for mList[0:nC-1]
   * @param mList is the list of genes
   * @param nC is the number of genes
   * @return table of K-kmeans clustering statistics
   * @see Table
   * @see Util#cvf2s
   * @see Util#cvtValueToStars
   */
  private Table createTableKmeansClusterStats(Gene[] mList, int nC)
  { /* createTableKmeansClusterStats*/
    if(mList==null || nC==0)
      return(null);
    
    String fNames[]= {"Cluster #", "# nodes/Cluster", "Similarity",
                      "Mean dist to Cluster", "S.D. dist to Cluster",
                      "CV dist to Cluster"};
     int
       nCols= fNames.length,
       nRows= nC;
     Table tR=  new Table(mae, nRows, nCols, "KmeansClustStat", "");
     
     for(int i=0;i<nCols;i++)
       tR.tFields[i]= fNames[i];
     
     HPxyData hpxy= mae.cdb.hpXYdata;
     
     for(int r=0;r<nRows;r++)
     {
       Gene gene= mList[r];
       boolean isKmeansNode= (gene!=null) ? (gene.nGeneClustersCnt>0) : false;
       int 
         cKmeansNbr= (gene!=null) ? gene.clusterNodeNbr : 0,
         nGeneClustersCnt = (gene!=null) ? gene.nGeneClustersCnt : 0;
       float
         distKN= (gene!=null) ? gene.data : 0.0F,
         valMax= clg.kMeansMaxDist[cKmeansNbr],          // or cc.maxGlobalDist,
         val= (isKmeansNode) ? 0.0F : distKN;
       String
         sSimilarity,
         sdDist= "-",
         cvDist= "-",
         sDist= (isKmeansNode)
                  ? "Primary node ["+(int)distKN+"]"
                  : (""+(int)distKN);
                  
       sSimilarity= Util.cvtValueToStars(val, valMax,
                                        15, /*maxStars [1:30] */
                                        true /* right fill with spaces */);
       tR.tData[r][0]= (""+cKmeansNbr);
       tR.tData[r][1]= (""+nGeneClustersCnt);
       tR.tData[r][2]= sSimilarity;
       tR.tData[r][3]= sDist;
       if(isKmeansNode)
       {
         float
           mnWCD= clg.mnWithinClusterDist[cKmeansNbr],
           sdWCD= clg.sdWithinClusterDist[cKmeansNbr];
           sdDist= Util.cvf2s(sdWCD,4);
           cvDist= Util.cvf2s(sdWCD/mnWCD,4);
       }
       tR.tData[r][4]= sdDist;
       tR.tData[r][5]= cvDist;
     }
     
     return(tR);
  } /* createTableKmeansClusterStats */
  
     
  /**
   * createStrReportOfOCL_FstatData() - make String report of OCL F-test for mList[0:nC-1]
   * If the OCL F-Test filter is active, then report F-test statistics.
   * This report is meant to be used in an updateable String Popup
   *<P>
   * [TODO] extend report 
   * @param mList is the list of genes
   * @param nC is the number of genes
   * @return table of X Y statistics
   * @see HPxyData#updateDataAndStat
   * @see Statistics#calcNCondFtestStat
   * @see Table
   * @see Util#cvf2s
   */
  private String createStrReportOfOCL_FstatData(Gene[] mList, int nC)
  { /* createStrReportOfOCL_FstatData*/
    if(mList==null || nC==0)
      return(null);
    
    HPxyData hpxy= mae.cdb.hpXYdata;
    Condition cdList= mae.cdList;
  
    /* [1] Get lists of samples from the current Ordered Condition List */
    int curOCLidx= cdList.curOCLidx;
    if(cdList.curOCLidx==-1)
      return(null);       /* There is no current OCL */ 
    
    MJAsample mjaSample= mae.mja.mjaSample;
    MJAcondition mjaCondition= mae.mja.mjaCondition;
    boolean ok= false;    
    String     
      curOCLname= cdList.orderedCondListName[curOCLidx], 
      title= "Current OCL F-test report for selected genes\n",
      sR= title;                                  /* Result */
    
    /* [1.1] Get the list of conditions in the OCL */
    Condition 
      cd,                 /* working condition */
      ocl[]= cdList.orderedCondList[cdList.curOCLidx];
    int
      nConditions= cdList.nOrderedCondList[curOCLidx],
      nCondData[]= new int[nConditions];   /* # of samples in each condition */ 
    String
      sampleName[][]= new String[nConditions][],
      condName[]= new String[nConditions];
    
    /* [1.2] Get lists of samples from the current Ordered Condition List.
     * Current set of sample indexes for samples in the Conditions in the
     * Ordered Condition List to test. [nConditions][nsamples per condition]
     */
    int  
      allSamplesIndex[][]= new int[nConditions][],
      nCondSamples,        /* # of samples/condition */
      nSamples,            /* working # of samples */ 
      samplesIndex[],      /* Working sample indexes in working condition */  
      sampleNbrList[];     /* working list of samples/condition */ 
    MaHybridSample msList[];
     
    /* [1.3] Working current set of data for gene mid for samples in the 
     * Conditions in the Ordered Condition List to test.
     * [nConditions][nsamples per condition]
     */
    float condData[][]= new float[nConditions][];
     
    for(int c=0;c<nConditions;c++)
    { /* get sample indices for all conditions */
      cd= ocl[c];                /* get the working condition */
      msList= cd.getHPlist();    /* get list of samples */
      if(msList==null)
        return(null);           /* fatal error */
      nSamples= cd.nMScond;
      condName[c]= cd.cName;
      nCondData[c]= nSamples;
      sampleNbrList= new int[cd.nMScond];  /* make a new list */
      for(int i=1;i<=nSamples;i++)
        sampleNbrList[i-1]= MJAbase.lookupHPsampleNbr(msList[i]);
      sampleName[c]= mjaCondition.getSampleNamesInCondList(condName[c]);
      allSamplesIndex[c]= sampleNbrList;
      condData[c]= new float[nSamples];
    } /* get sample indices for all conditions */
    
    /* Working normalized sample data for gene mid for working
     * data [nCondSamples]
     */
    float
      dataS[];
    int
      nExtra,
      nCols= 0,                     /* is computed */
      nRows= nC,
      mid;
    Gene gene;
    
    /* [1.5] Print lists of samples for each condition */
    sR += "Current OCL='"+curOCLname+ "' has "+nConditions+" conditions\n";    
    for(int c=0;c<nConditions;c++)
    { /* print out the sample data */
      sR += "  Condition["+c+"]='"+ condName[c]+"' has # samples="+
            nCondData[c]+"\n";
      for(int s=0;s<nCondData[c];s++)
        sR += "   sample["+c+"]["+s+"]=('"+ sampleName[c][s]+"', "+
              allSamplesIndex[c][s]+"\n";
    } /* print out the sample data */
    
    /* [2] Generate the rows data and write out into the table */
    for(int r=0;r<nRows;r++)
    { /* generate a row of data for this gene */
      gene= mList[r];
      if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
        continue;            /* ignore bogus spots */
      mid= gene.mid;      
      
      /* [2.1] Get the condData sample data[c][nCondData[c]] for
       * conditions c in [0:nCondition-1] for the mid and the
       * current normalization and ratio mode.
       */
      for(int c=0;c<nConditions;c++)
      { /* get the data for each set of samples in condition c */
        samplesIndex= allSamplesIndex[c];
        /* Build the working set of condition data for gene mid.
         * Lookup the data for samples in the list and save it in
         * the precomputed condData[] array of 1D vectors.
        */
        mjaSample.getSamplesDataVector(condData[c], samplesIndex, mid);
      } /* get the data for each set of samples in condition c */
      
      /* [2.2] Compute the F-test on the OCL data computes:
       *    pFnConds - p value
       *    fStatNconds - f statistic
       *    mnSqWithin - mean within class variance
       *    mnSqBetween - mean between class variance
       *    dfWithin - degrees of freedom Within conditions
       *    dfBetween - degrees of freedom Between conditions
       *
       * Returns false if any of the data is invalid or the beta fct fails.
       */
      ok= stat.calcNCondFtestStat(condData, nCondData, nConditions);
      
      /* [3] Test the p-value computed in the F-test against the pValue
       * slider threshold. This tests the Null-hypothesis that
       * there is no difference between the means as there is
       * significant different variance between the conditions
       */
      String
        sData,
        sRow= "";
      sRow += " p=" + ((ok) ? Util.cvf2s((float)stat.pFnConds,5) : "no F-test");
      sRow += " f=" + Util.cvf2s((float)stat.fStatNconds,5);
      sRow += " dfWithin=" + Util.cvf2s((float)stat.dfWithin,1);
      sRow += " dfBetween=" + Util.cvf2s((float)stat.dfBetween,1);
      sRow += " mnSqWithin=" + Util.cvf2s((float)stat.mnSqWithin,4);
      sRow += " mnSqBetween=" + Util.cvf2s((float)stat.mnSqBetween,4);
      
      sR += sRow +"\n";
      for(int c=0;c<nConditions;c++)
      { /* get a subrow for each condition */  
        sRow= "  ";
        sRow += " m"+c+"=" + Util.cvf2s((float)stat.mean[c],4);
        sRow += " sd"+c+"=" + Util.cvf2s((float)stat.variance[c],4);
        sRow += " CV"+c+"=" + Util.cvf2s((float)(stat.variance[c]/stat.mean[c]),4);
        sRow += " n"+c+"=" + (""+stat.nCondData[c]);
        
        sData= " data"+c+"=(";
        for(int d=0;d<nCondData[c];d++)
          sData += Util.cvf2s((float)condData[c][d],4)+" ";
        sData += ")";
        
        sR += sRow + sData + "\n";
      } /* get a subrow for each condition */  
      
    } /* generate a row of data for this gene */
    
    return(sR);
  } /* createStrReportOfOCL_FstatData */
  
  
  /**
   * cvGene2TabDelimStr() - convert a gene to printable tab-delimited string
   * @param gene is the gene to convert
   * @param i is the id (row?) of the entry
   * @param epTbl is an opt. expr profile data
   * @param hpxyTbl is an opt. HP-X/-Y set stat. data
   * @return tab-delimited string of gene data
   * @see GridCoords#cvtFGRC2str
   * @see SpotFeatures#getPlate
   * @see Util#cvd2s
   */
  private String cvGene2TabDelimStr(Gene gene, int i, String dataName,
                                    boolean showGeneName, Table epTbl,
                                    Table hpxyTbl )
  { /* cvGene2TabDelimStr */
    GridCoords grcd= mae.mp.gid2fgrc[gene.gid];
    String
      Master_ID= gene.Master_ID,
      gcStr= mae.grcd.cvtFGRC2str(grcd.f, grcd.g, grcd.r, grcd.c, false),
      dbEST3= (gene.dbEST3==null) ? "" : gene.dbEST3,
      GenBankAcc= (gene.GenBankAcc!=null)
                    ? gene.GenBankAcc
                    : ((gene.GenBankAcc3!=null)
                         ? gene.GenBankAcc3
                         : ((gene.GenBankAcc3!=null)
                              ? gene.GenBankAcc3
                              : "")),
      MasterGeneName= (gene.MasterGeneName==null) 
                        ? "" : gene.MasterGeneName,
      plateName= mae.sf.getPlate(gene.gid),
      sR= "#"+ i + " " +  gcStr + "\t";
    
    if(dataName!=null)
      sR += Util.cvd2s(gene.data,4) + "\t";
    
    sR += Master_ID + "\t" + dbEST3 +"\t" + GenBankAcc;
    
    if(showGeneName)
      sR +=  "\t" + MasterGeneName + "\t" + plateName;
    
    /* Conditionally add on the expression profile data */
    if(epTbl!=null)
    {
      for(int c=0;c<epTbl.tCols;c++)
        sR += "\t" + epTbl.tData[i][c];
    }
    
    /* conditionally add on the HP-X/-Y set statistics data */
    if(hpxyTbl!=null)
    {
      for(int c=0;c<hpxyTbl.tCols;c++)
        sR += "\t" + hpxyTbl.tData[i][c];
    }
    
    return(sR);
  } /* cvGene2TabDelimStr */
  
  
  /**
   * getGeneTabDelimFieldsStr() - create tab delimited gene fields string
   * @param dataName is title for the data
   * @param showGeneName is flag to show the gene name as well
   * @param epTbl is an opt. expr profile data
   * @param hpxyTbl is an opt. HP-X/-Y set stat. data
   * @return tab-delimited string of gene data
   */
  private String getGeneTabDelimFieldsStr(String dataName, boolean showGeneName,
  Table epTbl,  Table hpxyTbl )
  { /* getGeneTabDelimFieldsStr */
    String sR= "#\tCoords\t" +
               ((dataName!=null) ? (dataName + "\t") : "") +
               mae.masterIDname+"\tdbEST3'\tGenBankAcc3'";
    
    if(showGeneName)
      sR += "\t"+mae.masterGeneName;
    
    /* Conditionally add on the expression profile data */
    if(epTbl!=null)
    {
      for(int c=0;c<epTbl.tCols;c++)
        sR += "\t" + epTbl.tFields[c];
    }
    
    /* conditionally add on the HP-X/-Y set statistics data */
    if(hpxyTbl!=null)
    {
      for(int c=0;c<hpxyTbl.tCols;c++)
        sR += "\t" + hpxyTbl.tFields[c];
    }
    
    sR += "\n";
    
    return(sR);
  } /* getGeneTabDelimFieldsStr */
  
  
  /**
   * showGeneReport() - show gene report with top maxGene's.
   * @param mListOrig is list of genes to display
   * @param nMlistOrig is number of genes
   * @param maxGenesReported is the max # of genes to show in the report
   * @param title of report
   * @param sortDirection as either ASCENDING, DESCENDING, DONT_SORT
   * @param dataName to show dataName if this is not null
   * @param showGeneName is flag to show Gene_Name of each gene
   * @param tblFmtMode is mode on how to present table
   * @param reportMode is the type of table
   * @see ShowStringPopup
   * @see Table
   * @see Table#makeTabDelimReport
   * @see #createTotGeneRptTbl
   * @see #createTotURLgeneRptTbl
   * @see #showPopupSpreadsheetGeneReport
   */
  private void showGeneReport(Gene[] mListOrig, int nMlistOrig,
                              int maxGenesReported, String title,
                              int sortDirection, String dataName,
                              boolean showGeneName, int tblFmtMode,
                              int reportMode)
  { /* showGeneReport */
    Table
      totCRtbl,
      kcTbl= new Table(createTotGeneRptTbl(mListOrig, nMlistOrig,
                                           maxGenesReported, title,
                                           sortDirection, dataName,
                                           showGeneName,reportMode));
    
    if(mae.RPT_FMT_DYN==tblFmtMode)
    { /* dynamic spreadsheet */
      int cellFormat[]= new int[kcTbl.tCols+1];
      
      totCRtbl= createTotURLgeneRptTbl(kcTbl,
                                       mListOrig,
                                       nMlistOrig,
                                       maxGenesReported,
                                       title,
                                       sortDirection,
                                       dataName,
                                       cellFormat,
                                       showGeneName);
      showPopupSpreadsheetGeneReport(kcTbl,
                                     totCRtbl, /* final table */
                                     mListOrig, nMlistOrig,
                                     maxGenesReported,
                                     title, sortDirection,
                                     dataName,
                                     cellFormat,
                                     showGeneName);
      return;
    } /* dynamic spreadsheet */
    else
      totCRtbl= kcTbl;          /* static tab delimited table */
    
    String sR= kcTbl.makeTabDelimReport(title + "\n");
    
    /* Show the report */
    ShowStringPopup  Rpt= new ShowStringPopup(mae, sR, 25,80, 
                                              mae.rptFontSize,
                                              title, 0, 0, popupName,
                                              popupPropertyBits, 
                                              "maeGeneRpt.txt");
  } /* showGeneReport */
  
  
  /**
   * createTotURLgeneRptTbl() - generate total URL gene report table
   * @param kcTbl is returned table (initial table is created prior to call)
   * @param mListOrig is list of genes to display
   * @param nMlistOrig is number of genes
   * @param maxGene is max# genes to show
   * @param title of report
   * @param sortDirection as either ASCENDING, DESCENDING, DONT_SORT
   * @param dataName to show dataName if this is not null
   * @param cellFormat of size [nCols+1]
   * @param showGeneName is flag to show Gene_Name
   * @see MAExplorer#logDRYROTerr
   * @see Table#getTableRowsSubset
   */
  private Table createTotURLgeneRptTbl(Table kcTbl, Gene[] mListOrig, 
                                       int nMlistOrig, int maxGene, 
                                       String title, int sortDirection,
                                       String dataName, int cellFormat[],
                                       boolean showGeneName )
  { /* createTotURLgeneRptTbl */
    int
      idxGridCoord= 0,          /* index for fieldsData[] table */
      idxMaster_ID= 1,          /* NOTE: synce with createTotGeneRptTbl() defs */
      idxGeneName= 2,
      idxmAdbCloneDB= 3,
      idxLocusLinkGBID= 4,
      idxUniGene= 5,
      idxGenBankAcc= 6,
      idxLocusID= 7,
      idxdbEST3= 8,
      idxPlateGRC= 9,
      nCols= kcTbl.tCols;       /* size of fieldsData[] */

    if(dataName!=null && dataName.length()>0)
    { /* shift to right by 1 */
      idxMaster_ID++;
      idxGeneName++;
      idxmAdbCloneDB++;
      idxLocusLinkGBID++;
      idxUniGene++;
      idxGenBankAcc++;
      idxLocusID++;
      idxdbEST3++;
      idxPlateGRC++;
    }
    
    /* add base address if dynamic */
    String
      urlPrefix[]= new String[nCols+1],
      urlSuffix[]= new String[nCols+1];
    
    for(int i=0;i<nCols;i++)
      cellFormat[i]= CELL_NOP;
    
    /* [TODO] redo this since may not have Clone_ID!!! */
    if(mae.RPT_FMT_DYN==tblFmtMode)
    { /* overide with URLs */
      if(gipo.cloneIdIdx!=-1)
        cellFormat[idxMaster_ID]= CELL_URL;  /* only if IMAGE clone else no */
      cellFormat[idxmAdbCloneDB]= CELL_URL;
      cellFormat[idxLocusLinkGBID]= CELL_URL;
      if(gipo.cloneIdIdx!=-1)
        cellFormat[idxUniGene]= CELL_URL;
      cellFormat[idxLocusID]= CELL_URL;
      cellFormat[idxGenBankAcc]= CELL_URL;
      cellFormat[idxdbEST3]= CELL_URL;
      
      if(gipo.cloneIdIdx!=-1)
        urlPrefix[idxMaster_ID]= cfg.genBankCloneURL;
      urlPrefix[idxmAdbCloneDB]= cfg.mAdbURL;
      urlPrefix[idxLocusLinkGBID]= cfg.gbid2LocusLinkURL;
      if(gipo.cloneIdIdx!=-1)
        urlPrefix[idxUniGene]= cfg.IMAGE2unigeneURL;
      //if(gipo.OmimIdIdx!=-1)
      //  urlPrefix[idxOMIM]= cfg.omimURL;
      urlPrefix[idxLocusID]= cfg.locusLinkURL;
      urlPrefix[idxGenBankAcc]= cfg.genBankAccURL;
      urlPrefix[idxdbEST3]= cfg.dbEstURL;
      
      if(gipo.cloneIdIdx!=-1)
        urlSuffix[idxMaster_ID]= "[clone]";
      urlSuffix[idxmAdbCloneDB]= null;      /* none */
      urlSuffix[idxLocusLinkGBID]= null;    /* none */
      if(gipo.cloneIdIdx!=-1)
        urlSuffix[idxUniGene]= null;        /* none */
      urlSuffix[idxLocusID]= null;          /* none */
      urlSuffix[idxGenBankAcc]= null;       /* none */
      urlSuffix[idxdbEST3]= null;           /* none */
    } /* overide with URLs */
    
    
    /* Show the report by displaying data within a clickable spreadsheet */
    SimpleTable st=kcTbl.getTableRowsSubset(kcTbl,         /* ARG: data source */
                                            kcTbl.tFields, /* fields to use */
                                            null,          /* new names if !null */
                                            urlPrefix,     /* if entry not null */
                                            urlSuffix,     /* if entry not null */
                                            nCols,         /* # of new fields*/
                                            kcTbl.title);
    Table kcUrlTbl= new Table(st);
    if(kcUrlTbl==null)
      mae.logDRYROTerr("[R-CTUCRT] - can't make kcUrlTbl");
    
    return(kcUrlTbl);
  } /* createTotURLgeneRptTbl */
  
  
  /**
   * createTotGeneRptTbl() - generate total gene report table.
   * @param mListOrig is list of genes to display
   * @param nMlistOrig is number of genes
   * @param maxGene is max# genes to show
   * @param title of report
   * @param sortDirection as either ASCENDING, DESCENDING, DONT_SORT
   * @param dataName to show dataName if this is not null
   * @param showGeneName is flag to show Gene_Name
   * @param reportMode is the type of report
   * @return table returned else null if problem.
   * @see Gene#sortGeneList
   * @see GridCoords#cvtGID2str
   * @see MAExplorer#logDRYROTerr
   * @see SimpleTable
   * @see SimpleTable#setRowData
   * @see SimpleTable#setFieldData
   * @see SpotFeatures#getPlate
   * @see Util#cvd2s
   * @see #createTableOfExprProfiles
   * @see #createTableOfHP_XYstat
   * @see #createTableKmeansClusterStats
   */
  private SimpleTable createTotGeneRptTbl(Gene[] mListOrig, int nMlistOrig,
                                          int maxGene, String title,
                                          int sortDirection, String dataName,
                                          boolean showGeneName, int reportMode)
  { /* createTotGeneRptTbl */
    String sR= title;
    boolean addHP_XYdataFlag= (reportMode==mae.RPT_TBL_HIGH_RATIO ||
                               reportMode==mae.RPT_TBL_LOW_RATIO ||
                               reportMode==mae.RPT_TBL_HIGH_F1F2 ||
                               reportMode==mae.RPT_TBL_LOW_F1F2);
    Gene[]  mList= new Gene[nMlistOrig];
    int nMlist= 0;
    String
      fields[],
      fieldsData[]= {"Grid-Coord", mae.masterIDname,
                     "Gene-Name", "mAdb CloneDB",
                     "LocusLink(GBID)", "UniGene", "GenBank",
                     "LocusID", "Plate-G,R,C"},
      fieldsDNdata[]= {"Grid-Coord", "**DATA-NAME**", mae.masterIDname,
                       "Gene-Name", "mAdb CloneDB",
                       "LocusLink(GBID)", "UniGene", "GenBank", "LocusID",
                       "Plate-G,R,C"};  /* has extra field */
     /* Note: may add on "EP[nameHP_E[1]]", ... "EP[nameHP_E[nHP_E]]" */
      int
        idxGridCoord= 0,             /* index for fieldsData[] table */
        idxMaster_ID= 1,
        idxGeneName= 2,
        idxmAdbCloneDB= 3,
        idxLocusLinkGBID= 4,
        idxUniGene= 5,
        idxGenBankAcc= 6,
        idxLocusID= 7,
        idxPlateGRC= 8,
        idxHP_Xdata= -1,
        idxHP_Ydata= -1,
        nCols= fieldsData.length;   /* size of fieldsData[] */
      
      if(dataName!=null && dataName.length()>0)
      { /* insert other 1 or 3 fields */
        fieldsDNdata[1]= dataName;  /* after Grid Coords */
        fields= fieldsDNdata;
        nCols++;
        idxMaster_ID++;
        idxGeneName++;
        idxmAdbCloneDB++;
        idxLocusLinkGBID++;
        idxUniGene++;
        idxGenBankAcc++;
        idxLocusID++;
        idxPlateGRC++;
        if(addHP_XYdataFlag)
        { /* insert "HP-X data" and "HP-Y data" */
          idxHP_Xdata= idxPlateGRC+1;
          idxHP_Ydata= idxHP_Xdata+1;
          nCols += 2;
          fields= new String[nCols];
          for(int i=0;i<fieldsDNdata.length;i++)
            fields[i]= fieldsDNdata[i];
          fields[idxHP_Xdata]= "HP-X data";
          fields[idxHP_Ydata]= "HP-Y data";
        }
      } /* /* insert other 1 or 3 fields */
      else
      { /* use the default size without dataName */
        fields= fieldsData;
      }
      
      int baseCols= nCols;
      
      /* Optionally extend the Gene Report table if reporting
       * additional data
       */
      if(addExprProfFlag || addHP_XYstatFlag || 
         addKmeansClusterStatFlag || addKmeansMnAndSDstatFlag)
      {
        int newNcols= nCols;
        if(addExprProfFlag)
          newNcols += mae.hps.nHP_E;
        if(addHP_XYstatFlag)
          newNcols += 10;      /* 2X (mn,SD,CV,n),pT, mnX/mnY */
        if(addKmeansClusterStatFlag)
          newNcols += 6;       /* "K-means node#, # nodes/Cluster,
                                * Similarity,
                                * MnDistToKmeans, SDdistToKmeans,
                                * CVdistToKmeans" */
        if(addKmeansMnAndSDstatFlag)
          newNcols += 3*mae.hps.nHP_E; /* mn1,sd1,cv1,mn2,sd2,cv2,...*/
        String newFields[]= new String[newNcols];
        
        for(int c=0;c<nCols;c++)
          newFields[c]= fields[c];   /* regrow the field list */
        
        fields= newFields;           /* add EP & redefine it */
        nCols= newNcols;             /* add EP & redefine it */
      }
            
      /* Remove null instances from gene list in case there are any. */
      for(int i=0;i<nMlistOrig;i++)
        if(mListOrig[i]!=null && mListOrig[i].Master_ID.length()!=0)
          mList[nMlist++]= mListOrig[i];  /* build a CLEAN list */
      
      if(sortDirection==ASCENDING)
        Gene.sortGeneList(mList, nMlist, true /* ascending */);
      else if(sortDirection==DESCENDING)
        Gene.sortGeneList(mList, nMlist, false /* descending */);      
      
      /* Extend the Gene Report table if reporting expression profile data*/
      Table epTbl= null;
      if(addExprProfFlag)
      { /* add field names "EP[<name[i]>]" */
        epTbl= createTableOfExprProfiles(mList,nMlist,
                                         addHierClusterStatFlag);
        for(int i=0;i<mae.hps.nHP_E;i++)
          fields[baseCols++]= "EP[" + epTbl.tFields[i] + "]";
      }
      
      /* Extend Gene Report table if reporting HP_XY set statistics data*/
      Table hpxyTbl= null;
      if(addHP_XYstatFlag)
      { /* add field names "XY statistics" */
        hpxyTbl= createTableOfHP_XYstat(mList,nMlist);
        int newCols= baseCols+hpxyTbl.tCols;   /* regrow the fields[] size */
        if(newCols>fields.length)
        { /* regrow the fields[] size */
          String newFields[]= new String[newCols];
          for(int i=0;i<baseCols;i++)
            newFields[i]= fields[i];
          fields= newFields;        
          nCols= newCols;
        } /* regrow the fields[] size */
        for(int i=0;i<hpxyTbl.tCols;i++)
          fields[baseCols++]= hpxyTbl.tFields[i];
      }
      
      /* Extend Gene Report table if reporting OCL F-test statistics data*/
      Table oclTbl= null;
      if(addOCLstatFlag)
      { /* add field names "OCL statistics" */
        oclTbl= createTableOfOCL_Fstat(mList,nMlist);
        int newCols= baseCols+oclTbl.tCols;   /* regrow the fields[] size */
        if(newCols>fields.length)
        { /* regrow the fields[] size */
          String newFields[]= new String[newCols];
          for(int i=0;i<baseCols;i++)
            newFields[i]= fields[i];
          fields= newFields;       
          nCols= newCols;
        } /* regrow the fields[] size */
        for(int i=0;i<oclTbl.tCols;i++)
          fields[baseCols++]= oclTbl.tFields[i];
      }
      
      /* Extend Gene Report table if reporting K-means cluster statistics data*/
      Table kMeansClusterTbl= null;
      if(addKmeansClusterStatFlag)
      { /* add field names "Kmeans node#, # nodes/Kmeans, MnDistToKmeans" */
        kMeansClusterTbl= createTableKmeansClusterStats(mList,nMlist);
        for(int i=0;i<kMeansClusterTbl.tCols;i++)
          fields[baseCols++]= kMeansClusterTbl.tFields[i];
      }
      
      if(addKmeansMnAndSDstatFlag)
      { /* add headings for mean and StdDev of K-means nodes */
        for(int i=1;i<=mae.hps.nHP_E;i++)
        {
          fields[baseCols++]= "Mean["+i+"]";
          fields[baseCols++]= "+-S.D.["+i+"]";
          fields[baseCols++]= "CV["+i+"]";
        }
      }
      
      String rowData[]= new String[nCols+1];
      
      /* Create new empty table and set the fields */
      maxGene= Math.min(maxGene,nMlist);
      
      /* Setup for popup SpreadSheet scroller*/
      int nRowsNeeded= Math.max(maxGene,
                                ShowSpreadsheetPopup.MAX_ROWS_TO_SHOW);
      nRowsNeeded += 1; /* add 1 */
      
      SimpleTable kcTbl= new SimpleTable("popupTable", title, null,
                                         nRowsNeeded,   /* # of rows */
                                         nCols          /* # columns */);
      kcTbl.setFieldData(fields,nCols);
      
      Gene gene;
      String
        Master_ID,
        Clone_ID,
        gene2UniGene,
        gene2mAdb,
        gbid2LocusLink,
        GenBankAcc,
        Gene_Name,
        LocusID,
        plateName;
      int
        c,
        h,
        m,
        gid;
      float
        mnVal,
        sdVal;
      
      for(int i=0; i<maxGene; i++)
      { /* make a Table for popup table */
        gene= mList[i];
        if(gene==null || (gene.properties & Gene.C_BAD_SPOT)!=0)
          continue;            /* ignore bogus spots */
        gid= gene.gid;
        
        /* Look for dryrot */
        if(gid!=-1 && gene.mid!=-1)
        { /* only look at existing geones */
          Master_ID= gene.Master_ID;
          plateName= mae.sf.getPlate(gene.gid);
          Clone_ID= gene.Clone_ID;
          gene2UniGene= Clone_ID;   /* used in lookup - no UID */
          gene2mAdb= Clone_ID;      /* used in lookup - no UID */
          GenBankAcc= gene.GenBankAcc;
          if(GenBankAcc==null || GenBankAcc.length()==0)
            GenBankAcc= gene.GenBankAcc3;
          if(GenBankAcc==null || GenBankAcc.length()==0)
            GenBankAcc= gene.GenBankAcc5;
          gbid2LocusLink= GenBankAcc;
          LocusID= gene.LocusID;
          Gene_Name= (gene.Gene_Name==null) ? "" : gene.Gene_Name;
          
        /* [TODO] redo so build report on what actually exists
         * including GenBank, GB3', GB5', SwissProt, dbEST3',dbEST5'
         * etc.
         */
        /*
        if(mae.CONSOLE_FLAG)
        {
          String
          msg= "R-CTCRT #"+ nMlist+ " " +
                GridCoords.cvtGID2str(gid, false) +
                "] Master_ID='"+Master_ID+"'" +
                ", dbEST3='"+dbEST3+
                ", gene2UniGene='"+gene2UniGene+
                ", Gene_Name='"+Gene_Name+"'"
               ", plateName='"+plateName+"'";
          mae.fio.logMsgln(msg);
          gene.prettyPrint(msg);
         }
         */
          
         /* Populate the Table by generating a row
          * and copying it to the actual table
          */
          m= 0;
          rowData[m++]= mae.grcd.cvtGID2str(gid,false);
          if(dataName!=null)
            rowData[m++]= Util.cvd2s(gene.data,4);
          rowData[m++]= Master_ID;
          rowData[m++]= Gene_Name;
          rowData[m++]= gene2mAdb;
          rowData[m++]= gbid2LocusLink;
          rowData[m++]= gene2UniGene;
          rowData[m++]= GenBankAcc;
          rowData[m++]= LocusID;
          rowData[m++]= plateName;
          if(addHP_XYdataFlag && dataName!=null)
          {
            rowData[m++]= Util.cvd2s(gene.data1,4);
            rowData[m++]= Util.cvd2s(gene.data2,4);
          }
          
         /* Optionally extend the Gene Report table if reporting
          * expression profile data or HP-XY set statistics data.
          */
          if(addExprProfFlag)
          { /* add expression profile data */
            for(c=0; c<mae.hps.nHP_E; c++)
              rowData[m++]= epTbl.tData[i][c];
          }
          
          if(addHP_XYstatFlag)
          { /* add mean and std deviation for X Y sets and statistics */
            for(c=0;c<hpxyTbl.tCols;c++)
              rowData[m++]= hpxyTbl.tData[i][c];
          }
          
          if(addOCLstatFlag)
          { /* add OCL statistics */
            for(c=0;c<oclTbl.tCols;c++)
              rowData[m++]= oclTbl.tData[i][c];
          }
          
          
          if(addKmeansClusterStatFlag)
          { /* add "K-means node#, # nodes/Cluster, distTKmeans" data */
            for(c=0;c<kMeansClusterTbl.tCols;c++)
              rowData[m++]= kMeansClusterTbl.tData[i][c];
          }
          
          if(addKmeansMnAndSDstatFlag)
          { /* add mean, StdDev, CoeffVar data for K-means nodes */
            for(h=0;h<mae.hps.nHP_E;h++)
            {
              mnVal= clg.hpDataMnA[i][h];
              sdVal= clg.hpDataSDA[i][h];
              rowData[m++]= Util.cvf2s(mnVal,4);
              rowData[m++]= Util.cvf2s(sdVal,4);
              rowData[m++]= Util.cvf2s(sdVal/mnVal,4);
            }
          } /* add mean and StdDev data for K-means nodes */
          
          kcTbl.setRowData(rowData,nCols,i);
        } /* only look at existing genes */
      } /* make a Table for popup table */
      
      if(maxGene<nRowsNeeded)
        for(int j= maxGene; j<nRowsNeeded; j++)
        { /* add blank entries if < size of SS */
          for(m= 0; m<nCols;m++)
            rowData[m]= "";
          kcTbl.setRowData(rowData,nCols,j);
        }
      
      /* Free the epTble and hpxyTbl since no longer needed */
      epTbl= null;
      hpxyTbl= null;
      
      if(kcTbl==null)
        mae.logDRYROTerr("[R-CTCRT] - can't make kcTbl");
      
      return(kcTbl);
  } /* createTotGeneRptTbl */
  
  
  /**
   * showPopupSpreadsheetGeneReport() - show gene report with top maxGene's.
   * @param kcTbl is returned table (initial table is created prior to call)
   * @param kcUrlTbl is returned table (initial table is created prior to call)
   * @param mListOrig is list of genes to display
   * @param nMlistOrig is number of genes
   * @param maxGene is max# genes to show
   * @param title of report
   * @param sortDirection as either ASCENDING, DESCENDING, DONT_SORT
   * @param dataName to show dataName if this is not null
   * @param cellFormat of size [nCols+1]
   * @param showGeneName is flag to show Gene_Name
   * @see MAExplorer#logDRYROTerr
   * @see ShowSpreadsheetPopup
   */
  private void showPopupSpreadsheetGeneReport(Table kcTbl, Table kcUrlTbl,
                                              Gene[] mListOrig, int nMlistOrig,
                                              int maxGene, String title,
                                              int sortDirection, String dataName,
                                              int cellFormat[], boolean showGeneName)
  { /* showPopupSpreadsheetGeneReport */    
    /* Show the report by displaying data within a clickable spreadsheet */
    if(kcTbl==null || kcUrlTbl==null)
    {
      mae.logDRYROTerr( "[R-SPSCR] - no GeneRpt Tbls");
    }
    
    /* Now show it in a spreadsheet */
    ShowSpreadsheetPopup ssp= new ShowSpreadsheetPopup(mae, this,
                                                       kcUrlTbl.tFields,
                                                       kcTbl.tData,    /* data without URLs */
                                                       kcUrlTbl.tRows, /* last row */
                                                       0,              /* start row */
                                                       kcUrlTbl.tRows, /* max #rows&cols */
                                                       kcUrlTbl.tCols,
                                                       mae.rptFontSize,
                                                       title,
                                                       null,           /* altFieldNames[] */
                                                       cellFormat,
                                                       kcUrlTbl.tData, /* data with URLs */
                                                       popupName,
                                                       popupPropertyBits,
                                                       true);
  } /* showPopupSpreadsheetGeneReport */
  
  
  /**
   * updateCurGene() - update report if current gene changed and this type of report requires it.
   * @param mid is the new Master Gene ID of the current gene
   */
  void updateCurGene(int mid)
  { /* updateCurGene */
  } /* updateCurGene */
  
  
  /**
   * updateFilter() - update report if Filter using new GeneList ml.
   * Use the Filter.workingCL if the specified list is null.
   * @param ml is the gene list to update now that filter has changed.
   */
  void updateFilter(GeneList ml)
  { /* updateFilter */
  } /* updateFilter */
  
  
  /**
   * updateSlider() - update report if Slider changed and this type of report requires it.
   */
  void updateSlider()
  { /* updateSlider */
  } /* updateSlider */
  
  
  /**
   * updateLabels() - update plot if labels changed and this type of report requires it.
   */
  void updateLabels()
  { /* updateLabels */
  } /* updateLabels */
  
  
  
  /**
   * close() - close this popup and reset flags if needed
   */
  void close()
  { /* close */
  } /* close */
  
  
} /* end of class Report */




