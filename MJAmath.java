/** File: MJAmath.java */
  
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Open Java class to access MJAmath methods and data structures.
 * This provides access to builtin math methods
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * get_okBetaCF() return true if nr_betacf() was ok
 * get_okBetaI() return true if if nr_betai() was ok
 * get_okProbKS() return true if nr_probks() was ok
 * get_dKS() return K-S statistic for Kolmogorov-Smirnov stat d & prob
 * get_probKS() return probability of null hypoth same distrib for K-S stat
 * get_nXY() return # of genes used in calcXYstat computation
 * get_rSq() return correlation coefficient 
 * get_mnX() return mean X
 * get_mnY() return mean Y
 * get_sdX() return standard deviation X
 * get_sdY() return standard deviation Y
 * nr_gammln() - Return ln(gamma()) of x by polynomial evaluation.
 * nr_betacf() - evaluation fct for the incomplete Beta function 'x(a,b).
 * nr_betai() - return the incomplete Beta function 'x(a,b).
 * nr_sort() - quick-sort of bin of data[1:n] in assend. numerical order.
 * nr_kstwo() - Kolmogorov-Smirnov stat d & prob of the null hypothesis of 
 * nr_probks() - Calc Kolmogorov-Smirnov probability Qks.
 * calcXYstat() - statistics string for display
 * euclideanDistance() - compute Euclidean distance of 2 vectors
 * cityBlockDistance() - compute city-block distance of 2 vectors
 * calcPearsonCorrCoef() - compute Pearson correlation coefficient
 * log2Zero() - compute log2((x==0.0 ? 0.0 : x) - avoids log2(0.0)!
 * log2() - compute log(x) base 2.
 * alog2() - compute alog(x) base 2.
 * logZero() - compute log10((x==0.0 ? 0.0 : x) - avoids log(0.0)!
 * log10() - compute log(x) base 10.
 * alog10() - compute alog(x) base 10.
 *</PRE>
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author P. Lemkin (NCI), J. Evans (CIT), C. Santos (CIT), 
 *         G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *<P>
 */
 
public class MJAmath extends MJAbase
{
  
  /**
   * MJAmath() - constructor for Open Java API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAmath()
  { /* MJAmath */
  } /* MJAmath */
  
  
  /**
   * get_okBetaCF() - test if nr_betaCF() was ok
   * @return true if nr_betacf() was ok
   * @see #nr_betacf
   */
  public final boolean get_okBetaCF()
  { return(MathMAE.okBetaCF); }
  
  
  /**
   * get_okBetaI() - test if nr_betai() was ok
   * @return true if nr_betai() was ok
   * @see #nr_betai
   */
  public final boolean get_okBetaI()
  { return(MathMAE.okBetaI); }
  
  
  /**
   * get_okProbKS() - test if Kolmogorov-Smirnov test was ok
   * @return true if nr_probks() was ok
   * @see #nr_kstwo
   * @see #nr_probks
   */
  public final boolean get_okProbKS()
  { return(MathMAE.okProbKS); }
  
  
  /**
   * get_dKS() - K-S statistic for Kolmogorov-Smirnov stat d & prob of the
   * null hypothesis of 2 bins of data.
   * @return value
   * @see #nr_kstwo
   */
  public final double get_dKS()
  { return(MathMAE.dKS); }
  
  
  /**
   * get_probKS() - probability of null hypoth same distrib for
   * previously computed Kolmogorov-Smirnov stat d & prob of the null
   * hypothesis of 2 bins of data.
   * @return value
   */
  public final double get_probKS()
  { return(MathMAE.probKS); }
  
  
  /**
   * get_nXY() - # of genes used in calcXYstat computation
   * @return value
   */
  public final int get_nXY()
  { return(MathMAE.nXY); }
  
  
  /**
   * get_rSq() - get correlation coefficient previously computed
   * @return value
   * @see #calcXYstat
   */
  public final double get_rSq()
  { return(MathMAE.rSq); }
  
  
  /**
   * get_mnX() - get mean X previously computed
   * @return value
   * @see #calcXYstat
   */
  public final double get_mnX()
  { return(MathMAE.mnX); }
  
  
  /**
   * get_mnY() - get mean Y previously computed
   * @return value
   * @see #calcXYstat
   */
  public final double get_mnY()
  { return(MathMAE.mnY); }
  
  
  /**
   * get_sdX() - get standard deviation X previously computed
   * @return value
   * @see #calcXYstat
   */
  public final double get_sdX()
  { return(MathMAE.sdX); }
  
  
  /**
   * get_sdY() - get standard deviation Y previously computed
   * @return value
   * @see #calcXYstat
   */
  public final double get_sdY()
  { return(MathMAE.sdY); }
  
  
  /**
   * nr_gammln() - Return ln(gamma()) of x by polynomial evaluation.
   * Pg 168, Section 6 N.R.C.
   * @param xx arg
   * @return value
   */
  public final double nr_gammln(double xx)
  {  return(MathMAE.nr_gammln( xx)); }
  
  
  /**
   * nr_betacf() - evaluation fct for the incomplete Beta function 'x(a,b).
   * Pg 180, Section 6.3 N.R.C.
   * Set the okBetaCF flag to false if there is a problem
   * @param a arg
   * @param b arg
   * @param x arg
   * @return value
   */
  public final double nr_betacf(double a, double b, double x)
  {  return(MathMAE.nr_betacf(a, b, x)); }
  
  
  /**
   * nr_betai() - return the incomplete Beta function 'x(a,b).
   * Pg 179, Section 6.3 N.R.C.
   * @param a - a parameter of 'x(a,b)
   * @param b - b parameter of 'x(a,b)
   * @param x - x parameter of 'x(a,b)
   * @return value if succeed else 0.0 if it fails and set the
   *         okBetaI flag to false.
   */
  public final double nr_betai(double a, double b, double x)
  { return(MathMAE.nr_betai(a, b, x)); }
  
  
  /**
   * nr_sort() - quick-sort of bin of data[1:n] in assend. numerical order.
   * Uses the partitioning-exchange sorting method.
   * @param n is the amount of data.
   * @param data is the set of data [1:n].
   * @return false if blow the stack or other problems.
   */
  public final boolean nr_sort(int n, double data[])
  { return(MathMAE.nr_sort(n, data)); }
  
  
  /**
   ** nr_kstwo() - Kolmogorov-Smirnov statistic d and the
   * probability of the null hypothesis of 2 bins of data.
   * Pg 625, Section 14.3 N.R.C. 2nd Edition.
   * @param data1 [1:n1] data1
   * @param n2 # of items in data 1
   * @param data1 [1:n2] data2
   * @param n2 # of items in data 2
   * @return false if any errors in the data or calcs. Data is returned
   * in the global class variables: returns data in global class variables:
   *<PRE>
   *    dKS     -  K-S statistic, and
   *    probKS  - probl of null hypoth same distribution
   *</PRE>
   */
  public final boolean nr_kstwo(double data1[], int n1,
                                double data2[], int n2)
  { return(MathMAE.nr_kstwo(data1, n1, data2, n2)); }
  
  
  /**
   * nr_probks() - Calc Kolmogorov-Smirnov probability Qks.
   * Pg 626, Section 14.3 N.R.C. 2nd Edition.
   * @param alam the value computed in kstwo().
   * @return the probability.Set okProbKS to false if there is a problem.
   */
  public final double nr_probks(double alam)
  {  return(MathMAE.nr_probks(alam)); }
  
  
  /**
   * calcXYstat() - compute correlation statistics string for display
   *  rSq=.., n=.., X(mn+-sd)=(..+-..), Y(mn+-sd)=(..+-..)
   * If propList[] is not null, then test if filtered using
   * IS_FILTERED property. The following data is generated:
   *<PRE>
   *  rSq - string "rSq=.., n=.., X(mn+-sd)=(..+-..), Y(mn+-sd)=(..+-..)"
   *  nXY - # of data points
   *  mnX - mean X value
   *  mnY - mean Y value
   *  sdX - std deviation of X values
   *  sdY - std deviation of Y values
   *</PRE>
   * @param n size of lists
   * @param xList is X class data
   * @param yList is Y class data
   * @param propList is properties data
   * @param xLbl is label for the X data
   * @param yLbl is label for the Y data
   * @return statistics string if succeed, else return null if problem.
   */
  public final String calcXYstat(int n, float xList[], float yList[],
                                 int propList[], String xLbl, String yLbl)
  { return(MathMAE.calcXYstat(n, xList, yList, propList, xLbl, yLbl)); }
  
  
  /**
   * euclideanDistance() - compute Euclidean distance of 2 vectors
   * as either the Euclidean distance or (sum dist**2).
   * The data vectors are data1[0:n-1] and data2[0:n-1].
   * @param data1 is vector [0:n-1] of object 1
   * @param data2 is vector [0:n-1] of object 2
   * @param n is size of vector
   * @param rtnDistSqFlag return (sum dist**2) else Euclidean distance.
   * @return euclidean distance or distSq, else -1.0 if an error
   */
  public final float euclideanDistance(float data1[], float data2[],
                                       int n, boolean rtnDistSqFlag)
  { return(MathMAE.euclidDist(data1, data2, n, rtnDistSqFlag)); }
  
  
  /**
   * cityBlockDistance() - compute city-block distance of 2 vectors.
   * The data vectors are data1[0:n-1] and data2[0:n-1].
   * @param data1 is vector [0:n-1] of object 1
   * @param data2 is vector [0:n-1] of object 2
   * @param n is size of vector
   * @return city-block distance. Return -1.0 if there is an error
   */
  public final float cityBlockDistance(float data1[],float data2[],
                                       int n, boolean rtnAbsSumFlag)
  { return(MathMAE.cityBlockDist(data1,data2,n,rtnAbsSumFlag)); }
  
  
  
  /**
   * calcPearsonCorrCoef() - compute Pearson correlation coefficient
   * The data is data1[0:n-1] and data2[0:n-1].
   * @param data1 is vector [0:n-1] of object 1
   * @param data2 is vector [0:n-1] of object 2
   * @param n is size of vector
   * @param usePopulationCovar flag to compute popuplation covariance
   *      (Weinstein) U. Scherf, Nat.Genetics (2000) 24:236-244, pg 243.
   *      else use version for large samples in Snedecore & Cochran 1st
   *      Edition page 175.
   * @return calcPearsonCorrCoef else 1000.0 if there is an error
   */
  public final float calcPearsonCorrCoef(float data1[], float data2[],
                                         int n, boolean usePopulationCovar)
  { return(MathMAE.calcPearsonCorrCoef(data1,data2,n,usePopulationCovar)); }
  
  
  /**
   * log2Zero() - compute log2((x==0.0 ? 0.0 : x) - avoids log2(0.0)!
   * This defaults log2(0.0) to log2(1.0).
   * @param x argument for log2
   * @return log2((x==0.0 ? 0.0 : x) - avoids log2(0.0)!
   */
  public final double log2Zero(double x)
  { return(MathMAE.log2Zero(x)); }
  
  
  /**
   * log2() - compute log(x) base 2.
   * Since log2(x)= log2(e)*ln(x).
   * @param x argument for log2
   * @return log(x) base 2
   */
  public final double log2(double x)
  { return(MathMAE.log2(x)); }
  
  
  /**
   * alog2() - compute alog(x) base 2.
   * Since ln(x)= log2(x)/log2(e) = log2(x) * ln2.
   * @param x argument for alog
   * @return alog(x) base 2
   */
  public final double alog2(double x)
  { return(MathMAE.alog2(x)); }
  
  
  
  /**
   * logZero() - compute log10((x==0.0 ? 0.0 : x) - avoids log(0.0)!
   * This defaults log10(0.0) to log(1.0).
   * @param x argument for log
   * @return log10((x==0.0 ? 0.0 : x) - avoids log(0.0)!
   */
  public final double logZero(double x)
  { return(MathMAE.logZero(x)); }
  
  
  /**
   * log10() - compute log(x) base 10.
   * Since log10(x)= log10e * ln(x).
   * @param x argument for log
   * @return log(x) base 10
   */
  public final double log10(double x)
  { return(MathMAE.log10(x)); }
  
  
  
  /**
   * alog10() - compute alog(x) base 10.
   * Since ln(x)= log10(x)/log10(e) = log10(x) * ln10.
   * @param x argument for alog
   * @return alog(x) base 10
   */
  public final double alog10(double x)
  { return(MathMAE.alog10(x)); }
  
  
} /* end of class MJAmath */

