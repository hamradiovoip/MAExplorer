/** File: AnalysisPlugin.java */ 

package MAEPlugin.analysis;

/**
 * This class is the analysis MAEPlugin base class and is used to implement
 * specialized base classes based on CheckBoxMenuPlugin.
 * These include:
 * <PRE>
 * GeneClassPlugin -     manipulate Gene Sets
 * NormalizationPlugin - normalize spot data between array samples
 * FilterPlugin -        gene data filter for inclusion in data Filter chain
 * PlotPlugin -          plot method
 * ClusterPlugin -       cluster method
 * ReportPlugin -        report method
 * </PRE>
 * 
 * Created on September 5, 2001, 6:13 PM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/03 16:22:07 $ / $Revision: 1.4 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
abstract public class AnalysisPlugin extends MAEPlugin.CheckBoxMenuPlugin
 {    
    
   /**
    * AnalysisPlugin() - Constructor for new AnalysisPlugin with
    * default label "Analysis Plugin".
    */
   public AnalysisPlugin()
   { /* AnalysisPlugin */
     super("Analysis Plugin");
   } /* AnalysisPlugin */
   
   
   /**
    * AnalysisPlugin() - Constructor for new AnalysisPlugin with label.
    * @param menuLabel is the name of the plugin.
    */
   public AnalysisPlugin(String menuLabel)
   { /* AnalysisPlugin */
     super(menuLabel);
   } /* AnalysisPlugin */
   
   
   /**
    * AnalysisPlugin() - Constructor for new AnalysisPlugin with label and file name.
    * @param menuLabel is the name of the plugin.
    * @param pluginFileName name of the plugin without the ".jar"
    */
   public AnalysisPlugin(String menuLabel, String pluginFileName)
   { /* AnalysisPlugin */
     super(menuLabel, pluginFileName);
   } /* AnalysisPlugin */
   
   
} /* end of class AnalysisPlugin */

