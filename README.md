# Search Keyword Identification for Concept Location using Graph-Based Term Weighting

**Abstract:** During maintenance, software developers deal with numerous  change requests that are written in an unstructured fashion using natural language texts.
These texts illustrate the change requirements using various domain-related concepts. Software developers need to find appropriate keywords from these texts
so that they could identify the relevant locations in the source code using a search technique. Once such locations are identified, they can implement the requested changes there. Studies suggest that developers often perform poorly in choosing the right keywords from a change request. In this article, we propose a novel technique --STRICT-- that (1) identifies suitable keywords from a change request using three graph-based term weighting algorithms -- TextRank, POSRank and WK-Core, and (2) then delivers an appropriate query using query quality analysis and machine learning. Our approach determines a term's importance based on not only its co-occurrences with other important terms but also its syntactic relationships and cohesion with them. Experiments using 955 change requests from 22 Java-based subject systems show that STRICT can offer better search queries than 
the baseline queries (i.e., preprocessed version of a request text) from 44%--63% of all requests. Our queries also achieve 20% higher accuracy, 10% higher precision and 7% higher reciprocal rank than that of the baseline queries. Comparisons with six existing approaches from the literature demonstrate that our approach can outperform them in improving the baseline queries. Our approach also achieves 14% higher accuracy, 15% higher precision and 13% higher reciprocal rank than that of the six other existing approaches.


Experimental Data
---------------------
Please check the  [**replication package**](https://github.com/Lareina-Y/STRICT-Replication-Package/tree/TPSR_v1) for more details.


Getting Started
---------------------

- Update the HOME_DIR path in StaticData.java
- Download [**'glove.6B.100d.txt'**](https://nlp.stanford.edu/projects/glove/) and store under 'STRICT-Replication-Package/GloVe/glove.6B'
- Running the code in Main.java

```java
String repoName = "eclipse.jdt.debug";
		ArrayList<Integer> selectedBugs = SelectedBugs.loadSelectedBugs(repoName);
		String scoreKey = "TPR";
		StaticData.ADD_CODE_ELEM=false;
		StaticData.ADD_TITLE=true;
		ArrayList<String> queries = new SearchQueryProvider(repoName, scoreKey, selectedBugs).provideSearchQueries();
```
