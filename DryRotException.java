/** File: DryRotException.java */

/** 
 * Class to implement DRYROT messages for MAExplorer. 
 * DRYROT messages are fatal errors that indicates either that something 
 * is wrong with the program, bad or corrupted data structures etc. 
 * Since you can not continue, it passes the information to a Dryrot report 
 * method that pops up a Dialog box for the user and suggests they mail the code
 * author with the saved error message and MAExplorer state.
 *
 * <P>
 * At the point in a method when you want to throw a DryRotException,
 * use the syntax "throw new DryRotException(msg);" and specify in the parameter
 * a String of useful information.
 * <P>
 *
 * Any method that contains a throw must have a try block to catch this
 * exception or "Exception" itself or throw the exception to the next method.
 * <P>
 *
 * Thus for DryRotException,
 * <PRE>
 *   class Example
 *     {
 *       public void myMethod () throws DryRotException 
 *         {
 *           throw new DryRotException("Line 22 of Foo class had a bad var");
 *         }
 *
 *       public static void main (String [] args)
 *         {
 *          try 
 *            {
 *              Example o = new Example();
 *              o.myMethod();
 *            } 
 *          catch (DryRotException e)
 *            {
 *              System.out.print("Exception: "+e);
 *            }
 *        }
 *  }
 * </PRE>
 *
 * [JE Created on December 19, 2001, 2:15 PM, Version 1.0]
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author Jai Evans (DECA/CIT), P. Lemkin (NCI), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.9 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public class DryRotException extends java.lang.Exception
{
  
  /**
   * DryRotException() - constructor to create new <code>DryRotException</code> without
   * detail message.
   */
  public DryRotException()
  { /* DryRotException */
    super();
  } /* DryRotException */
  
  
  /**
   * Constructs an <code>DryRotException</code> with the specified detail message.
   * @param msg the detail message.
   */
  public DryRotException(String msg)
  { /* DryRotException */
    super(msg);
  } /* DryRotException */
  
} /* end of class DryRotException */


