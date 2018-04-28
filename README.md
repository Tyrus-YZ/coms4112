COMS 4112 Project 2

[Files]
1. Algo.java: Code for taking in input, calculating the optimal plan and printing the plan as well as optimal cost in the required format.
2. Subset.java: Code of wrapped class of a subset. The class contains all information of the subset during the algorithm, as well as some functions to calculate basic properties of the subset such as selectivity, fcost and so on.
3. config.txt: File containing cofiguration information.
4. query.txt: File containing queries that will be calculated by Algo.java.
5. output.txt: File storing the optimal plans calcualted.
6. Makefile: Containing instructions to compile programs.
7. stage2.sh: Containing instructiions to execute the program.

[Execution]
1. execute "make"
2. execute "sh stage2.sh"

[Description]

In Algo.java, we first read in the configuration information and queries. Then for every query, we use a dfs way with varied lengths to generate all possible 2_(k-1) subsets and represent every subset with our defined Subset class as well as keep them in a increasing-order. Algorithm 4.11 is then executed to come up with the optimal plan of this query. At last, we make use of the dynamic programming table as well as the tree structure of the optimal plan to print out the optimal plan in the required format.

In Subset.java, we created a class that contains all useful information of a subset as well as defined a series of functions to calculate basic properties of this subset such as combined selectivity, fcost and so on. Also, some other functions that are useful during the process of Algorithm 4.11 are also defined, e.g. functions to get the leftmost and rightmost &-term. A specific toString() function is defined in order to print the optimal plan correctly in teh required format.







