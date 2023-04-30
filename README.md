# Search Keyword Identification for Concept Location using Graph-Based Term Weighting

**Abstract:** A study by the University of Cambridge pointed out that software errors cause economic losses of
$312 billion every year. In order to reduce this loss, software engineers spend 50% of their time on bug fixing. Engineers
often use different search techniques (e.g., regex, code search) to detect the location of a software concept (eg., software bugs),
which is time-consuming and difficult. STRICT was proposed in 2017 by Rahman and Roy. It reduces the cost of searching a
concept within the code by capturing suitable search terms from a change request. STRICT uses TextRank and POSRank to develop
two text graphs through co-occurrence and syntactic relationship. However, STRICT overlooks the semantic relationship among
words. In this project, I proposed to extend the STRICT and construct a third text graph based on the semantic relationship
between words. This algorithm will be called as SimRank below. After the experiments with 639 change requests from 6 subject
systems on the extended technique–STRICT++, approximately 10% of the requests can get better quality search terms than STRICT
in the First Glod Rank performance. But the current extended algorithm does not seem to be able to bring obvious improvements
to STRICT and still needs to be explored and verified. Four more metrics (Effectiveness, Mean Reciprocal Rank@K (MRR@K),
Mean Average Precision@K (MAP@K), and Top-K Accuracy) will also be used for evaluating the extended algorithm deeply.

Experimental Data
---------------------
Please check the  [**replication package**](https://github.com/Lareina-Y/STRICT-Replication-Package/tree/TPSR_v1) for more details.


Getting Started
---------------------

- `git clone` both [**"STRICT-QR-Module"**](https://github.com/Lareina-Y/STRICT-QR-Module/tree/TPSR_v1) and [**"STRICT-Replication-Package"**](https://github.com/Lareina-Y/STRICT-Replication-Package/tree/TPSR_v1) into the same folder
- Go to the branch "TPSR_v1"
- Update the `HOME_DIR` path in STRICT-QR-Module/src/strict/ca/usask/cs/srlab/strict/config/StaticData.java
- Download [**'glove.6B.100d.txt'**](https://nlp.stanford.edu/projects/glove/) and store under 'STRICT-Replication-Package/GloVe/glove.6B'
- Running the code in Main.java
- outputs are stored in the folder `output` based on the different systems
- Data analysis results are in 'Data_analysis.xlsx' under `output`

```java
// Main.java

// “i” represent the SIMILARITY_THRESHOLD which wants to be tested
for (double i = 0.2; i < 0.3; i += 0.1) { ... } 

// update the repoName to test different systems
String repoName = "eclipse.jdt.debug";
		
```
