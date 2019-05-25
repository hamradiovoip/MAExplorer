# MAExplorer

Microarray Explorer (MAExplorer) is a Java-based data-mining facility for cDNA or oligonucleiotide microarray databases. It may be downloaded and run as a stand-alone application on your computer. Its exploratory data analysis environment provides tools for the data-mining of quantitative expression profiles across multiple microarrays. 
 With MAExplorer, it is possible to: 1) analyze the expression of individual genes; 2) analyze the expression of gene families and clusters; 3) compare expression patterns and outliers; 4) directly access other genomic databases for genes of interest. Previously quantified array data is copied to your local computer where it is read by MAExplorer and intermediate results as well as the data mining session state may be saved between data mining sessions.

Microarray data may be viewed and directly manipulated in array pseudoimages, scatter plots, histograms, expression profile plots, cluster analyses (similar genes, K-means or K-median, hierarchical clustering, etc.), and reports. A key feature is the gene data filters for constraining a working set of genes to those passing a variety of user-specified tests and conditions. Reports may be generated with hypertext Web access to genomic databases such as UniGene, GenBank, dbEST, LocusLink, I.M.A.G.E., NCI/CIT mAdb microarray database and other Internet databases for sets of genes found to be of interest.

A major focus of this tool is interactive data mining with access to other supporting Web genomic databases. The emphasis on direct manipulation of genes and sets of genes in graphics and tables provides a high level of interaction with the data making it easier for investigators to test ideas when looking for patterns.

The MAExplorer Open Java API is available for creating your own Java MAEPlugins. This enables you to add your own analytic tools as well as those created by other researchers to extend MAExplorer functionalty. These could include adding new analysis methods to the base system such as statistical tests, normalization, clustering, client-server, etc.

MAExplorer was initially developed by the NCI Laboratory of Experimental and Computational Biology (LECB) in collaboration with the NIDDK Laboratory of Genetics and Physiology (LGP). MAExplorer was created to help analyze microarray data for the LGP's Mammary Genome Anatomy Program (MGAP) using mouse models designed to identify and understand genetic pathways operative during normal mammary gland development and tumorigenesis. Note that data for 50 hybridized samples from the MGAP database are included as a demonstration database when you download the stand-alone version of MAExplorer.


    Analyzes data (after arrays have been scanned and spots quantified)
    Handles multiple cDNA or oligo array samples with replicate spots
    Manages replicate samples, named condition sets of samples, and lists of condition sets
    Manages named subsets of genes
    Handles intensity or ratio (Cy3/Cy5) quantified array data
    Analyzes data for 2-conditions and N-condition expression profiles including ANOVA on any number of conditions of replicate samples
    Data-filters gene sets by statistics, clustering, gene set membership
    Provides direct data manipulation in graphics, spreadsheets and sample management
    Accesses genomic Web servers from plots and reports
    Converts your data using Cvt2Mae data conversion wizard
    Both MAExplorer and Cvt2Mae are written in Java for portability with download installers making it easy to run on any system
    Users may update these programs, once installed, by downloading just the Java jar file(s) using update commands.
    May be extended using new analytic methods written as either
        Java plug-ins using the MAEPlugin Open Java API, or
        R statistics and graphics language plug-ins methods called RLOs using the RtestPlugin tool 
