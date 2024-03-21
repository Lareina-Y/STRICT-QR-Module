package strict.ca.usask.cs.srlab.strict.test;

import strict.lucenecheck.LuceneSearcher;
//import strict.lucenecheck.MethodEntityUtil;
import org.junit.Test;
import strict.utility.MiscUtility;

import java.util.ArrayList;
import java.util.List;

public class LuceneSearcherTest {

	@Test
	public void testLuceneSearch() {
//		int bugID=217994;
//		String repository = "eclipse.jdt.debug";
//		String searchQuery = "args arg test JRE behavior Dvm bug launch configuration patch\tBug patch launching Debug honors JRE args Launcher args";

		int bugID = 303705;
		String repository = "eclipse.jdt.ui";
//		String searchQuery = "IJava Content Tree element IResource java Element Provider";
		String searchQuery = "element IJava IResource search java IJavaElement IJava Element Content check Tree Provider\tBug Custom hierarchically view";
		LuceneSearcher searcher = new LuceneSearcher(bugID, repository, searchQuery);
//		System.out.println("First found index:" + searcher.getFirstGoldRank());
		System.out.println("First found index:" + searcher.getFirstGoldRankClass());
	}

//	@Test
//	public void testMethodEntryAnalysis() {
//		String entry = "org.apache.commons.math3.geometry.euclidean.twod.Line$LineTransform:<init>(java.awt.geom.AffineTransform)";
//		MethodEntityUtil.analyseMethodEntity(entry);
//	}

}
