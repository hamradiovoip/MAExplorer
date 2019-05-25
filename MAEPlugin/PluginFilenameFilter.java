/** File: PluginFilenameFilter.java */

package MAEPlugin;

import java.io.*;

/** 
 * Class Filename filter for class files.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/02/21 16:30:17 $ / $Revision: 1.4 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */


public class PluginFilenameFilter implements FilenameFilter
{
  /**
   * PluginFilenameFilter() - Filter for class files, may be modified by regedit style wild cards.
   */
  public PluginFilenameFilter()
  { /* PluginFilenameFilter */
  } /* PluginFilenameFilter */
  
  
  /**
   * accept() - Filter method for <I>accepting</I> a file.
   * @param dir Directory path
   * @param name Name of file to filter
   * @return if the file is acceptable.
   */
  public boolean accept(File dir, String name)
  { /* accept */
    return(name.endsWith(".class"));
  } /* accept */
  
} /* end of class PluginFilenameFilter */
