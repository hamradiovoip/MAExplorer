/** File: MJAstatistics.java */
  
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.image.*;
import java.util.*;

/**
 * MAExplorer Gather Scatter API class to access MJAstatistics methods and data structures. 
 * 
 * Access statistics methods
 *
 *<P>
 *<B>List of methods available to Plugin-writers</B>
 *<PRE>
 * get_f() - CALC: calculated f statistic 
 * get_t() - CALC: t or t' statistic previously computed 
 * get_pT() - CALC: t-test p-value w/NULL hypoth previously computed 
 * get_pF() - CALC: f-test p-value w/NULL hypoth previously computed 
 * get_fStat() - CALC: f-statistic previously computed  
 * get_dF() - CALC: degrees of freedom previously computed  
 *
 * get_useTest() - CALC: 'B' or 'T' - t-test to use computed 
 * get_title() - title for data used in histogram previously computed  
 * get_meanIdx() - index of mean in hist[] previously computed 
 * get_medianIdx() - index of median in hist[] previously computed 
 * get_modeIdx() - index of mode in hist[] previously computed  
 * get_nBinsH() - 0 if none. size of histogram previously computed  
 * get_hist() - histogram of size [0:nBinsH-1] previously computed  
 * get_medianH() - mode of data[] for histogram previously computed 
 * get_modeH() - mode of data[] for histogram previously computed   
 * get_meanH() - mean of data[] for histogram previously computed   
 * get_stdDevH() - standard deviation of data[] for histogram 
 * get_meanAbsDevH() - mean absolute deviation for histogram 
 * get_minDataH() - min value in data[] for histogram previously computed  
 * get_maxDataH() - max value in data[] for histogram previously computed  
 * get_deltaBinH() - width of the histogram bins
 *
 * get_nCond_pValue() - get p-value computed of N-condition F-test
 * get_nCondFstat() - get f-statistic computed in N-condition F-test
 * get_nCondMeanSqWithinVariance() - get mnSqWithin variance computed in N-condition F-test
 * get_nCondMeanSqBetweenVariance() - get mnSqBetween variance computed in N-condition F-test 
 * get_nCond_dfWithin() - get dfWithin (deg. of freedom) computed in N-condition F-test 
 * get_nCond_dfBetween() - get dfBetween (deg. of freedom) computed in N-condition F-test 
 * get_nConditions() - get nConditions used in computation of N-condition F-test
 * get_conditionsData() - get samples for each condition for N-cond F-test
 * get_nSamplesAllConditions() - # samples for each condition for N-cond F-test
 * get_meansAllConditions() - get means of conditions for N-cond F-test
 * get_varianceAllConditions() - get variances of conditions for N-cond F-test
 * --------------------------------------------------------
 * calcMeanAndVariance() - compute mean & var of dataS[] 
 * calcNCondFtestStat() - calc. F-test statistics of data[0:nConditions-1][samples]
 * calcFprobFromVariances() - calc 2-tailed f prob. that vars. are same.
 * calcTandPvalues() - given (n1,m1,s1) and (n2,m2,s2), calc f, t, p, dF.
 * calcHistStats() - compute and analyze histogram.
 * calcHistStats() - compute and analyze histogram.
 *</PRE>
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), J. Evans (CIT), C. Santos (CIT), 
 *         G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:03 $   $Revision: 1.10 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *<P>
 */
 
public class MJAstatistics extends MJAbase
{
  
  /**
   * MJAstatistics() - constructor for Gather Scatter API for MAExplorer to
   * access MAExplorer data in a uniform interface.  This Class lets us
   * access the underlying MAExplorer data structures in a uniform way that
   * hides the complexity. It is designed to be used with MAExplorer Plugins.
   */
  MJAstatistics()
  { /* MJAstatistics */
  } /* MJAstatistics */
  
  
  /**
   * get_f() - CALC: calculated f statistic previously computed
   * @see #calcFprobFromVariances
   * @see #calcTandPvalues
   */
  public final double get_f()
  { return(mae.stat.f); }
  
  
  /**
   * get_t() - CALC: t or t' statistic computed previously computed
   * @see #calcTandPvalues
   */
  public final double get_t()
  { return(mae.stat.t); }
  
  
  /**
   * get_pT() - CALC: t-test p-value w/NULL hypoth previously computed
   * @see #calcTandPvalues
   */
  public final double get_pT()
  { return(mae.stat.pT); }
  
  
  /**
   * get_pF() - CALC: f-test p-value w/NULL hypoth previously computed
   * @see #calcFprobFromVariances
   * @see #calcTandPvalues
   */
  public final double get_pF()
  { return(mae.stat.pF); }
  
  
  /**
   * get_fStat() - CALC: f-statistic previously computed
   * @see #calcFprobFromVariances
   * @see #calcTandPvalues
   */
  public final double get_fStat()
  { return(mae.stat.fStat); }
  
  
  /**
   * get_dF() - CALC: degrees of freedom previously computed
   * @see #calcTandPvalues
   */
  public final double get_dF()
  { return(mae.stat.dF); }
  
  
  /**
   * get_useTest() - CALC: 'B' or 'T' - t-test to use computed
   * using F-statistic previously computed
   * @see #calcTandPvalues
   */
  public final char get_useTest()
  { return(mae.stat.useTest); }
  
  
  /**
   * get_title() - title for data used in histogram previously computed
   */
  public final String get_title()
  { return(mae.stat.title); }
  
  
  /**
   * get_meanIdx() - index of mean in hist[] previously computed
   * @see #calcHistStats
   */
  public final int get_meanIdx()
  { return(mae.stat.meanIdx); }
  
  
  /**
   * get_medianIdx() - index of median in hist[] previously computed
   * @see #calcHistStats
   */
  public final int get_medianIdx()
  { return(mae.stat.medianIdx); }
  
  
  /**
   * get_modeIdx() - index of mode in hist[] previously computed
   * @see #calcHistStats
   */
  public final int get_modeIdx()
  { return(mae.stat.modeIdx); }
  
  
  /**
   * get_nBinsH() - 0 if none. size of histogram previously computed
   * @see #calcHistStats
   */
  public final int get_nBinsH()
  { return(mae.stat.nBinsH); }
  
  
  /**
   * get_hist() - histogram of size [0:nBinsH-1] previously computed
   * @see #calcHistStats
   */
  public final int[] get_hist()
  { return(mae.stat.hist); }
  
  
  /**
   * get_medianH() - mode of data[] for histogram previously computed
   * @see #calcHistStats
   */
  public final float get_medianH()
  { return(mae.stat.medianH); }
  
  
  /**
   * get_modeH() - mode of data[] for histogram previously computed
   * @see #calcHistStats
   */
  public final float get_modeH()
  { return(mae.stat.modeH); }
  
  
  /**
   * get_meanH() - mean of data[] for histogram previously computed
   * @see #calcHistStats
   */
  public final float get_meanH()
  { return(mae.stat.meanH); }
  
  
  /**
   * get_stdDevH() - standard deviation of data[] for histogram
   * previously computed
   * @see #calcHistStats
   */
  public final float get_stdDevH()
  { return(mae.stat.stdDevH); }
  
  
  /**
   * get_meanAbsDevH() - mean absolute deviation for histogram
   * previously computed
   * @see #calcHistStats
   */
  public final float get_meanAbsDevH()
  { return(mae.stat.meanAbsDevH); }
  
  
  /**
   * get_minDataH() - min value in data[] for histogram previously computed
   * @see #calcHistStats
   */
  public final float get_minDataH()
  { return(mae.stat.minDataH); }
  
  
  /**
   * get_maxDataH() - max value in data[] for histogram previously computed
   * @see #calcHistStats
   */
  public final float get_maxDataH()
  { return(mae.stat.maxDataH); }
  
  
  /**
   * get_deltaBinH() - width of the histogram bins
   * computed as: nBinsH/(maxDataH-minDataH) previously computed
   * @see #calcHistStats
   */
  public final float get_deltaBinH()
  { return(mae.stat.deltaBinH); }
  
  
  /**
   * get_nCond_pValue() - get p-value computed of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double get_nCond_pValue()
  { return(mae.stat.pFnConds); }
    
    
  /**
   * get_nCondFstat() - get f-statistic computed in N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double get_nCondFstat()
  { return(mae.stat.fStatNconds); }
  
  
  /**
   * get_nCondMeanSqWithinVariance() - get mnSqWithin variance used in computation
   * of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double get_nCondMeanSqWithinVariance()
  { return(mae.stat.mnSqWithin); }
  
  
  /**
   * get_nCondMeanSqBetweenVariance() - get mnSqBetween variance used in computation
   * of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double get_nCondMeanSqBetweenVariance()
  { return(mae.stat.mnSqBetween); }
  
  
  /**
   * get_nCond_dfWithin() - get dfWithin degrees of freedom used in computation
   * of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double get_nCond_dfWithin()
  { return(mae.stat.dfWithin); }
  
  
  /**
   * get_nCond_dfBetween() - get dfBetween degrees of freedom used in computation
   * of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double get_nCond_dfBetween()
  { return(mae.stat.dfBetween); }
  
  
  /**
   * get_nConditions() - get nConditions used in computation of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final int get_nConditions()
  { return(mae.stat.nConditions); }
  
  
  /**
   * get_conditionsData() - data[0:nConditions-1][sampleNbrInClass] for computations
   * of N-condition F-test 
   * @see #calcMeanAndVariance
   * @see #calcMeanAndVariance
   */
  public final float[][] get_conditionsData()
  { return(mae.stat.condData); }
  
  
  /**
   * get_nSamplesAllConditions() - # of samples in each of the nConditions
   * used in computation of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final int[] get_nSamplesAllConditions()
  { return(mae.stat.nCondData); }
  
  
  /**
   * get_meansAllConditions() - means of samples in each of the nConditions
   * used in computation of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double[] get_meansAllConditions()
  { return(mae.stat.mean); }
  
  
  /**
   * get_varianceAllConditions() - variance of samples in each of the nConditions
   * used in computation of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double[] get_varianceAllConditions()
  { return(mae.stat.variance); }
  
  
  /**
   * get_stdDevAllConditions() - stdDev of samples in each of the nConditions
   * used in computation of N-condition F-test 
   * @see #calcMeanAndVariance
   */
  public final double[] get_stdDevAllConditions()
  { return(mae.stat.stdDev); }
  
  
  /* ------------------------------------------------------------------- */
  
  /**
   * calcMeanAndVariance() - compute the mean and variance of dataS[] and save in
   * mean[classK] and variance[classK] arrays. Also save the
   * dataS in data[classK] and nSamples in nData[classK].
   * @param data is array of size [0:nSamples-1] of data
   * @param nSamples is size of data
   * @param classK is the class # associated with this data (start at 0)
   * @param calcStdDevFlag also compute stdDev[classK] as well
   * @return true if succeed
   */
  public boolean calcMeanAndVariance(float dataS[], int nSamples, int classK,
                                     boolean calcStdDevFlag)
  { return(mae.stat.calcMeanAndVariance(dataS, nSamples, classK, calcStdDevFlag)); }
   
   
   /**
    * calcNCondFtestStat() - calc. F-test statistics of data[0:nConditions-1][samples]
    * It computes:
    *<pre>
    *    pFnConds - p value
    *    fStatNconds - f statistic
    *    mnSqWithin - mean within class variance
    *    mnSqBetween - mean between class variance
    *    dF1 - degrees of freedom df1
    *    dF2 - degrees of freedom df2
    * </pre>
    * @param data sample data[nConditions][sampleNbrInCondition]
    * @param nData # samples in each [nConditions]
    * @param nConditions # of conditions
    * @return false if any of the data is invalid (need >1 sample/Condition)
    */
   public boolean calcNCondFtestStat(float data[][], int nData[], int nConditions)
   { return(mae.stat.calcNCondFtestStat(data, nData, nConditions)); }
 
  
  /**
   * calcFprobFromVariances() - calc 2-tailed f prob. that vars. are same.
   * It computes:
   *<PRE>
   *    fStat - the f-statistic
   *    pF    - probability tthat the two samples are the same
   *</PRE>
   * @param n1 # samples class 1
   * @param n2 # samples class 2
   * @param var1 variance of class 1
   * @param var2 variance of class 2
   * @return false if any problems.
   */
  public final boolean calcFprobFromVariances(int n1, int n2,
                                              double var1, double var2)
  { /* calcFprobFromVariances */
    return(mae.stat.calcFprobFromVariances(n1, n2, var1, var2));
  } /* calcFprobFromVariances */
  
  
  /**
   * calcTandPvalues() - given (n1,m1,s1) and (n2,m2,s2), calc f, t, p, dF.
   * Use Behrens-Fisher/Satterthwaite estimate for t and dF if f-stat
   * is < 0.05 p-value that variances are different. Otherwise use the
   * standard student t-statistic with DF= (n1+n2-2).
   * If you want to force the test the set useTest to 'B' or 'T', else it
   * will pick the test to use (ie. TB or TP).
   * It uses the algorithm described Numerical Recipes in C (1st Ed)
   * for estimating p-value given the t-statistic using the incomplete
   * beta function betai().
   * It computes:
   *<pre>
   *    f - calculated f statistic
   *    t - t or t' statistic
   *    pT - t-test p-value w/NULL hypoth
   *    pF - f-test p-value w/NULL hypoth
   *    dF - degrees of freedom
   *    useTest - is 'B' or'T' depending on f statistic
   * </pre>
   * @param n1 # samples in class 1
   * @param  n2 # samples in class 2
   * @param m1 sample mean class 1
   * @param m2 sample mean class 2
   * @param s1 sample std dev class 1
   * @param s2 sample std dev class 2
   * @return false if any of the data is invalid (need >= 2 samples/class)
   * or the beta fct fails.
   */
  public final boolean calcTandPvalues(int n1, int n2,
                                       double m1, double m2,
                                       double s1, double s2)
  { /* calcTandPvalues */
    return(mae.stat.calcTandPvalues(n1, n2, m1, m2, s1, s2));
  } /* calcTandPvalues */
  
  
  /**
   * calcHistStats() - compute and analyze histogram.
   * for whatever range of data is given.
   * This computes and returns the results in variables of this instance:
   * <PRE>
   *   hist[0:nBins-1], medianH, modeH, meanH,
   *   stdDevH, meanAbsDevH, minDataH, maxDataH, deltaBinH
   * </PRE>
   * @param title for data
   * @param nBins size of hist[]
   * @param data of size [0:nData-1]
   * @param nData size of data array
   * @return nBins if successful.
   */
  public final int calcHistStats(String title, int nBins, float data[], int nData)
  { /* calcHistStats */
    return(mae.stat.calcHistStats(title, nBins, data, nData));
  } /* calcHistStats */
  
  
  /**
   * calcHistStats() - compute and analyze histogram.
   * for whatever range of data is given.
   * This computes and returns the results in variables of this instance:
   *<PRE>
   *   hist[0:nBins-1], medianH, modeH, meanH,
   *   stdDevH, meanAbsDevH, minDataH, maxDataH, deltaBinH.
   * Note: if hist is shorintt[nBins+1], then it will not be allocated.
   * </PRE>
   * @param title for data
   * @param nBins size of hist[]
   * @param data of size [0:nData-1]
   * @param nData size of data array
   * @param hist opt. [nBins+1] else null in which case it will allocate
   *        it locally
   * @return nBins if successful else 0.
   */
  public final int calcHistStats(String title, int nBins, float data[],
                                 int nData, int hist[])
  { /* calcHistStats */
    return(mae.stat.calcHistStats(title, nBins, data, nData, hist));
  } /* calcHistStats */
  
  
  
} /* end of class MJAstatistics */

