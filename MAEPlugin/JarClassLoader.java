package MAEPlugin;

import java.util.*;

/**
 * JarClassLoader provides a minimalistic ClassLoader which shows how to
 * instantiate a class which resides in a .jar file.
 * <P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author	John D. Mitchell, Non, Inc., Mar  3, 1999
 * @author Jai Evans 2002
 * @version 1.0
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public class JarClassLoader extends ClassLoader 
{
  /** utility object that loads resources from a Jar */
  private JarResources
    jarResources;
  
  /** Contains the loaded classes */
  private Hashtable
    classes= new Hashtable();
  
  /** Utility Char */
  private char
    classNameReplacementChar;
  
  ///protected boolean   sourceMonitorOn = true;
  
  
  /**
   * JarClassLoader() - default constructor that loads jar from the jarPath.
   * @param jarPath is the path of the Jar file
   */
  public JarClassLoader(String jarPath)
  {
    /* Create the JarResource and suck in the .jar file. */
    jarResources = new JarResources(jarPath);
  }
  
  
  /**
   * JarClassLoader() - constructor2 that loads jar from the path that is derived from the jarFile.
   * [constructor 2]
   * @param jarFile is the Jar file.
   */
  public JarClassLoader(java.io.File jarFile)
  {
    jarResources = new JarResources(jarFile.getPath());
  }
  
  
  /**
   * loadClassBytes() loads bytes for class files.
   * Note: the class may be buried inside of a directory
   * E.g. Plugins/<I>className</i>, so we need to test that first.
   * @param className of class to load bytes from
   * @return byte array of data from the class
   */
  protected byte[] loadClassBytes(String className)
  { /* loadClassBytes */
    className = formatClassName(className);
    /* Attempt to get the class data from the JarResource.*/
    String
    qualifiedClassName= "Plugins/"+className;
    if(jarResources.isResource(qualifiedClassName))
      return (jarResources.getResource(qualifiedClassName));
    else
      return (jarResources.getResource(className));
  } /* loadClassBytes */
  
  
  /**
   * loadClass() - load specified class and let it resolve the class if necessary.
   * This is a simple version for external clients since they
   * will always want the class resolved before it is returned
   * to them.
   * @param className name of the class to load
   * @return class that was loaded or null if error
   * @throws ClassNotFoundException
   */
  public Class loadClass(String className) throws ClassNotFoundException
  { return (loadClass(className, true)); }
  
  
  /**
   * loadClass() - load specified class
   * @param className name of the class to load
   * @param resolveIt lets it resolve the class if necessary
   * @return class that was loaded or null if error
   * @throws ClassNotFoundException
   */
  public synchronized Class loadClass(String className, boolean resolveIt)
  throws ClassNotFoundException
  { /* loadClass */
    Class c;
    byte classBytes[]= loadClassBytes(className);
    
    if (classBytes == null)
    { /* no data means that couldn't find the class in the jar file */
      /* Check our local cache of classes.
       * Note: if load, unload and reload want new version - so may NOT
       * want to use the cache!
       */
      //c = (Class)classes.get(className);
      //if (c != null)
      //  return(c);
      
      /* Check with the primordial class loader */
      try
      { /* Try to load plugin from cache or existing jar file in path */
        c = super.findSystemClass(className);
        return(c);
      }
      catch (ClassNotFoundException e)
      { /* failed, fall through and load it ourselves */
        throw new ClassNotFoundException();
      }
    }/* no data means that couldn't find the class in the jar file */
    
    /* Define Class */
    c = defineClass(className, classBytes, 0, classBytes.length);
    if (c == null)
    {
      throw new ClassFormatError();
    }
    
    /* Resolve Class if necessary */
    if (resolveIt)
      resolveClass(c);
    
    /* Complete */
    classes.put(className, c);
    return(c);
  } /* loadClass */
  
  
  /**
   * setClassNameReplacementChar() - remap class name.
   * This optional call allows a class name such as
   * "COM.test.Hello" to be changed to "COM_test_Hello",
   * which is useful for storing classes from different
   * packages in the same retrival directory.
   * In the above example the char would be '_'.
   * @param replacement character to assign.
   */
  public void setClassNameReplacementChar(char replacement)
  { classNameReplacementChar = replacement; }
  
  
  /**
   * formatClassName() - generate a name with ".class" extension
   * @param className class name
   * @return formated class name
   */
  protected String formatClassName(String className)
  { /* formatClassName */
    if (classNameReplacementChar == '\u0000')
    { /* '/' is used to map the package to the path */
      return (className.replace('.', '/') + ".class");
    }
    else
    { /* Replace '.' with custom char, such as '_' */
      return (className.replace('.', classNameReplacementChar) + ".class");
    }
  } /* formatClassName */
  
  
    /*
     * JarResources() - retreives jar resource which gives access to the jar contents
     * @return Jar resources
     */
  public JarResources getJarResources()
  { return (this.jarResources); }
  
}  /* end of class JarClassLoader */
