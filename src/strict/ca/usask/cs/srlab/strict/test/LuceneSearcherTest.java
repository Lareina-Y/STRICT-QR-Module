package strict.ca.usask.cs.srlab.strict.test;

import strict.lucenecheck.LuceneSearcher;
import strict.lucenecheck.MethodEntityUtil;
import org.junit.Test;

public class LuceneSearcherTest {

	@Test
	public void testLuceneSearch() {
		int bugID = 303705;
		String repository = "eclipse.jdt.ui";
		String searchQuery = "element IJava IResource search java IJavaElement IJava Element Content check Tree Provider\tBug Custom hierarchically view";
		LuceneSearcher searcher = new LuceneSearcher(bugID, repository, searchQuery);
		System.out.println("First correct rank: " + searcher.getFirstGoldRank());
	}

	@Test
	public void testMethodEntryAnalysis() {
		String entry = "org.apache.commons.math3.geometry.euclidean.twod.Line$LineTransform:<init>(java.awt.geom.AffineTransform)";
		MethodEntityUtil.analyseMethodEntity(entry);
	}

}
