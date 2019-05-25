package MAEPlugin;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * JarResources: JarResources maps all resources included in a
 * Zip or Jar file. Additionaly, it provides a method to extract 
 * one as a blob.
 * This is generic Java code taken from various sources and modified.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans(DECA/CIT), P. Lemkin (NCI), NCI-Frederick, Frederick, MD
 * @version $Date: 2003/02/21 16:30:16 $   $Revision: 1.4 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public final class JarResources
{
  /* hashtable to hold the jarContents */
  private Hashtable
    jarContents=new Hashtable();
  
  /* a jar file name*/
  private String
    jarFileName;
  
  /**
   * JarResources() - constructor for createing a JarResources.
   * It extracts all resources from a Jar
   * into an internal hashtable, keyed by resource names.
   * @param jarFileName a jar or zip file
   */
  public JarResources(String jarFileName)
  {
    this.jarFileName= jarFileName;
    loadJarContents();
  }
  
  
  /**
   * getResource() - Extracts a jar resource as a blob.
   * @param name a resource name.
   * @return bytes of resource
   */
  public byte[] getResource(String name)
  { return (byte[])jarContents.get(name); }
  
  
  /**
   * isResource() - checks to see if resource exists
   * @param name a resource name.
   * @return true if key exists in hash table resource
   */
  public boolean isResource(String name)
  { return (jarContents.containsKey(name)); }
  
  
  /**
   * loadJarContents() - initializes internal hash tables with jar file resources.
   * @return true if successful
   */
  private boolean loadJarContents()
  { /* loadJarContents */
    try
    { /* load the contents */
      ZipFile zFile= new ZipFile(jarFileName);
      Enumeration list= zFile.entries();
      BufferedInputStream is;
      ZipEntry ze= null;
      
      while (list!=null && list.hasMoreElements())
      { /* save Jar contents in the hash table */
        ze= (ZipEntry)list.nextElement();
        if (ze.isDirectory())
        {
          continue;
        }
        
        /* prepare repository */
        byte resource[]= null;
        if (ze.getSize()>0)
        {
          resource = new byte[(int)ze.getSize()];
        }
        
        is= new BufferedInputStream(zFile.getInputStream(ze));
        
        /* read in zip current zip entry*/
        is.read(resource, 0, resource.length);
        
        /* stow the resource full path */
        jarContents.put(ze.getName(), resource);
        
        /* stow the resource filename only */
        int i= ze.getName().lastIndexOf('/');
        if(i>0)
        {
          String key= ze.getName().substring(i+1, ze.getName().length());
          //System.out.println("raw "+ze.getName()+" key="+key);
          jarContents.put(key, resource);
        }
      } /* save Jar contents in the hash table */
    } /* load the contents */
    
    catch (NullPointerException e)
    {
      System.out.println("done.");
      return(false);
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
      return(false);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return(false);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return(false);
    }
    
    // if(MAEPlugin.DBUG_MAEP)
    //  System.out.println("JarResources-loadJarContents() jarFileName='"+
    //                      jarFileName+"'\n jarContents="+jarContents.toString());
    
    return(true);
  } /* loadJarContents */
  
}	/* End of JarResources class. */
