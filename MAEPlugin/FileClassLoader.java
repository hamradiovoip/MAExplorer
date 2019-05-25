/** File: FileClassLoader.java */

package MAEPlugin;

import java.io.*;
import java.net.*;

import java.util.zip.*;
import java.util.*;


/** 
 * Class to dynamic classes from local disks. This allows plugins to be loaded.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans. Modified from Chan, P. et.al. <I>The JavaClass 
 *    Libraries, v.1</I>. Addison Wesley, 1998 pp.416-418.); 
 *     C. Santos (DECA/CIT), P. Lemkin (NCI-Frederick
 * @version $Date: 2003/02/21 16:30:15 $ / $Revision: 1.4 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see java.lang.ClassLoader
 */

public class FileClassLoader extends ClassLoader
{
  
  /** String path */
  String 
    dir;
  
  /** zip path flag */
  boolean
   isZip= false;
  
  
  /**
   * FileClassLoader() - Constructor to load path.
   * @param path class to be loaded
   */
  public FileClassLoader(String path)
  { /* FileClassLoader */
    this(new File(path));
  } /* FileClassLoader */
  
  
  /**
   * FileClassLoader() - Constructor to load .classfile.
   * @param File itself, directory, class file or jar archive of class to be loaded
   */
  public FileClassLoader(File file)
  { /* FileClassLoader */
    if ( file == null)
      this.dir= null;
    else if ( file.isDirectory() )
      this.dir= file.getPath();
    else if ( file.isFile() )
      if (file.getName().endsWith(".jar") || (file.getName().endsWith(".zip")))
      { /* We use JarClassLoader if .jar */
        this.isZip = true;
        this.dir= file.getPath();
      }
      else
        this.dir= file.getParent();
  } /* FileClassLoader */
  
  
  
  /**
   * loadClass() - Implementation of ClassLoader.loadClass()
   **
   * From javadoc v. 1.3
   * Loads the class with the specified name. The default
   * implementation of this method searches for classes in the
   * following order:<p>
   * <ol>
   * <li> Call <CODE>findLoadedClass(String)</CODE> to check if class
   *      hasalready been loaded. <p>
   * <li> Call the <code>loadClass</code> method on the parent class
   *      loader.  If the parent is <code>null</code> the class loader
   *      built-in to the virtual machine is used, instead. <p>
   * <li> Call the <CODE>findClass(String)</CODE> method to find class.
   * <p>
   * </ol>
   *
   * If the class was found using the above steps, and the
   * <code>resolve</code> flag is true, this method will then call the
   * <CODE>resolveClass(Class)</CODE> method on the resulting class object.
   * <p>
   * From the Java 2 SDK, v1.2, subclasses of ClassLoader are
   * encouraged to override
   * <CODE>findClass(String)</CODE>, rather than this method.<p><DD><DL>
   *<DT><B>Parameters:</B>
   * <DD><CODE>name</CODE> - the name of the class
   * <DD><CODE>resolve</CODE> - if <code>true</code> then resolve class
   * <DT><B>Returns:</B>
   * <DD>the resulting <code>Class</code> object
   * <DT><B>Throws:</B>
   * <DD><CODE>ClassNotFoundException</CODE> - if class couldn't be found
   * </DL>
   *</DD>
   *</DL>
   * @param className name of the class to load
   * @param resolve called by Classloader savvy code to force resolution
   *               of the class.
   * @throws ClassNotFoundException thrown whenever class can not be loaded.
   * @return Return the Class object or null if class not found.
   */
  protected synchronized Class loadClass(String name, boolean resolve)
  throws ClassNotFoundException
  { /* loadClass */
    if (name == null)
      return null;
    
    /* [1] try to load Class from jre cache */
    String className;
    
    /* [1.1] make sure that the classname suffix is clean. */
    if (name.endsWith(".class"))
    { /* you have to use the '+' operator, otherwise substring() doesn't
       * produce a viable string. */
      className = ""+name.substring(0,name.lastIndexOf(".class"));
    }
    else if (name.endsWith(".jar"))
    { /* you have to use the '+' operator, otherwise substring() doesn't
       * produce a viable string. */
      className = ""+name.substring(0,name.lastIndexOf(".jar"));
     /* For 1.1 compatability only zip is implemented
      * however jar's are just zip's
      */
    }
    else if (name.endsWith(".zip"))
    { /* you have to use the '+' operator, otherwise substring() doesn't
      * produce a viable string. */
      className = ""+name.substring(0,name.lastIndexOf(".zip"));
     /* For 1.1 compatability only zip is implemented however jar's 
      * are just zip's 
      */
    }
    else
      className = name;
    
    Class c= null;
    //c = findLoadedClass(className);  /* get from JVM */
    
    /* [2] not in cache attempt to load */
    if (c == null)
    { /* [2.1] attempt to load via conventional ClassLoader */
      try
      {
        /* We do not look in the system cache since may want to reload
         * a different version.
         */
        //c = findSystemClass(className);
        //if (c == null)
        c = findLoadedClass(className);
        if (c == null)
          c = super.loadClass(className, resolve);
        if (c == null)
          c = findClass(className);
        if (c == null)
        { /* see if in "Plugins/" folder */
          String qualifiedClassName= "Plugins/"+className;
          c = super.loadClass(className, resolve);
          if (c == null)
            c = findClass(qualifiedClassName);
        } /* see if in "Plugins/" folder */
        ///
        super.loadClass(className, resolve);
      }
      
      catch (ClassNotFoundException e)
      {
        try
        {
          if (c == null)
            c = findClass(className);
        }
        catch (ClassNotFoundException er)
        {
          try
          {
            if(!isZip)
              c = findFileClass(className);
            else
              c = loadClassFromZip(this.dir,className);
            
           /* [From JavaDoc for LinkageError]
            * Subclasses of LinkageError indicate that a class
            * has some dependency on another class; however,
            * the latter class has incompatibly changed after
            * the compilation of the former class.
            *
            * java.lang.Object
            *  |
            *  +--java.lang.Throwable
            *        |
            *        +--java.lang.Error
            *             |
            *             +--java.lang.LinkageError
            *                  |
            *                  +--java.lang.ClassFormatError
            *                  |
            *                  +--java.lang.NoClassDefFoundError
            */
          }
          catch (NoClassDefFoundError err)
          {
            /*  [Thrown when a ClassLoader or VM] tries to load
             *  in the definition of a class
             *  and no definition of the class could be found.
             *
             *  The searched-for class definition existed when
             *  the currently executing class was compiled, but
             *  the definition can no longer be found.
             */
            System.out.println("*"+err);
          }
          catch (ClassFormatError err)
          { /* thrown when file is malformed or otherwise cannot
            * be interpreted as a class file.
            */
            System.out.println("*"+err);
          }
          catch (LinkageError err)
          { /* if duplicate, move on false alert */
            //if (err.toString().indexOf("duplicate")<0)
            System.out.println("*"+err);
          }
        }
      }
      
      if (c != null && resolve)
      {
        resolveClass(c);
      }
      else if (isZip)
      {
        c = loadClassFromZip(this.dir, className);
      }
    }
    
    return (c);
  } /* loadClass */
  
  
  /**
   * loadClass() - Implementation of ClassLoader.loadClass() by name of class.
   * @param className Name of the class to load
   * equivalent of loadClass(name, false);
   * @throws ClassNotFoundException Thrown whenever class can't be loaded.
   * @return Return the Class object or null if class not found.
   */
  public synchronized Class loadClass(String name)
  throws ClassNotFoundException
  { /* loadClass */
    /* call loadClass(name, false); */
    return(this.loadClass(name, true));
  } /* loadClass */
  
  
  /**
   * loadClass() - Implementation of ClassLoader.loadClass() by name of File pointer.
   * File pointer version. @see #loadClass(String)
   * @param file file to be loaded
   * @return Class instance
   */
  public synchronized Class loadClass(File file)
  throws ClassNotFoundException
  { /* loadClass */
    /* Handed a Filehandle, so we must resolve it to a class file */
    return (this.loadClass(file,true));
  } /* loadClass */
  
  
  /**
   * * loadClass() - Implementation of ClassLoader.loadClass() by name of File pointer and user option to resolve.
   * File pointer version. @see #loadClass
   * @param file file to be loaded
   * @param resolve try to resolve classes if true
   * @return Class instance
   */
  protected synchronized Class loadClass(File file, boolean resolve)
  throws ClassNotFoundException
  { /* loadClass */
    /* call loadClass(name, false); */
    if (( file != null) && (!file.isDirectory()))
    {
      return (loadClass( file.getName(),resolve));
    }
    else
    {
      throw (new ClassNotFoundException());
    }
  } /* loadClass */
  
  
  /**
   * findFileClass() - find file of class by name.
   * @return Returns the Class from the file.
   * @param name Name of the Class
   * @throws ClassNotFoundException If any error occurs,
   *         a ClassNotFoundException is thrown.
   * @return Class instance
   */
  public Class findFileClass(String name) throws ClassNotFoundException
  { /* findFileClass */    
    String
      fileName= name,
      className;
    
    /* [1] make sure that the file ends in .class */
    if (!fileName.endsWith(".class"))
      fileName += ".class";
    
    /* [2] remove any package name */
    while (fileName.indexOf(".")!=fileName.lastIndexOf(".class"))
    {
      fileName = fileName.substring(fileName.indexOf(".")+1);
    }
    
    /* [2.1] make sure that the class name is clean */
    if (fileName.lastIndexOf(".class")>=0)
    {
      /* you have to use the '+' operator, otherwise substring() doesn't
       * produce a viable string. 
       */
      className= ""+fileName.substring(0,fileName.lastIndexOf(".class"));
    }
    else
      className= fileName;
    
    /* [3] attempt to get file and read in byte codes */
    File file = new File(this.dir, fileName);
    try
    {
      InputStream in = new FileInputStream(file);
      int bufferSize = (int)file.length();
      byte buffer[] = new byte[bufferSize];
      in.read(buffer, 0, bufferSize);
      in.close();
      
      /* [4] define the class */
      return(defineClass(className, buffer, 0, buffer.length));
    }
    catch (Exception e)
    { /* [5] failer, pass along exception */
      throw new ClassNotFoundException(name);
    }
  }  /* findFileClass */
  
  
  /**
   * getResource() - Returns a URL containing the location of the  named resource.
   * @param name Name of the resource
   * @return URL of the named resource.
   */
  public URL getResource(String name)
  { /* getResource */
    try
    {
      File file = new File(dir,name);
      
      /* Cleanup for HTML, slashes etc */
      String absPath= file.getAbsolutePath().replace(file.separatorChar,'/');
      if (absPath.charAt(0) != '/')
        absPath= '/' + absPath;               /* add directory prefix */
      return(new URL("file:" + absPath));
    }
    catch (MalformedURLException e)
    {
      return(null);
    }
  } /* getResource */
  
  
  /**
   * getResourceAsStream() - returns an input stream to the named source.
   * @param name Name of the resource
   * @return input stream to the resource
   */
  public InputStream getResourceAsStream(String name)
  { /* getResourceAsStream */
    try
    {
      return(new FileInputStream(new File(dir,name)));
    }
    catch (FileNotFoundException e)
    {
      return(null);
    }
  } /* getResourceAsStream */
  
  
  /**
   * loadClassFromZip() - loads classes and resources of a zip or jar file
   * and loads the one class that has the same name as the archive.
   * @ param zipPath The path to the archive(zip of jar)
   * @ param className The name of the class
   * @ @return
   */
  protected Class loadClassFromZip(String zipPath, String className)
  { /* loadClassFromZip */
    Class c = null;
    
    /* set up a store for classes that we find */
    Vector classes = new Vector();
    
    ZipFile zip=null;
    try
    { /* open access to Zip file */
      zip = new ZipFile(zipPath);
      
      /* get list of contents of zip */
      Enumeration entries = zip.entries();
      for (; entries.hasMoreElements() ;)
      { /* check each element */
        ZipEntry entry= (ZipEntry)entries.nextElement();
        if(!entry.isDirectory() && (entry.getName().endsWith(".class")))
        { /* [1] get input stream to zip entry */
          InputStream in = zip.getInputStream(entry);
          try
          { /* [2] get buffer ready & load bytes from stream */
            int bufferSize = (int)entry.getSize();
            byte buffer[] = new byte[bufferSize];
            in.read(buffer, 0, bufferSize);
            in.close();
            
            /* [3] define the class */
            classes.add(defineClass(className,buffer,0, buffer.length));
          }
          catch (Exception e)
          {}
        }
      } /* check each element */
      
      /* Go through these and load resources into memory */
      /* grab the named class */
      for (Enumeration n = classes.elements(); n.hasMoreElements();)
      {
        Class cla = (Class)n.nextElement();
        if(cla.toString().compareTo(className)!=-1)
          c = cla;
        else
        {
        }
        System.out.println("class: "+cla);
      }
    } /* open access to Zip file */
    
    catch (ZipException e)
    { /* - if a ZIP format error has occurred */
    }
    catch (IOException e)
    { /* - if an I/O error has occurred */
    }
    catch (SecurityException e)
    { /* - if a security manager exists and its checkpoint
     * method doesn't allow read access to the file.
     */
    }
    finally
    {
      try
      { /* always close the zip */
        if (zip != null)
          zip.close();
      }
      catch (Exception e)
      {}
    }
    
    return(c);
  }  /* loadClassFromZip */
  
  
} /* end of class FileClassLoader */

