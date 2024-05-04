import strict.ca.usask.cs.srlab.strict.config.StaticData;
import strict.query.SearchQueryProvider;
import strict.utility.BugReportLoader;
import strict.utility.MiscUtility;
import strict.utility.QueryLoader;
import strict.utility.SelectedBugs;

import java.io.IOException;
import java.util.*;

public class Demo {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        System.out.print("RepoName: ");
        String repoName = scanner.nextLine(); // eclipse.jdt.ui
        System.out.print("The Issue# in " + repoName + ": ");
        int issueID = scanner.nextInt(); // 303705

        ArrayList<Integer> selectedBugs = new ArrayList<>();
        selectedBugs.add(issueID);

        // STRICT++
        System.out.println("STRICT++: ");
        double[] parameters = {0, 9, 0.4, 1, 1}; // TR, PR, SR, BTR, PTR
        run(parameters, repoName, selectedBugs);

        // STRICT
        System.out.println("STRICT: ");
        parameters = new double[]{1, 1, 0, 0, 0}; // TR, PR, SR, BTR, PTR
        run(parameters, repoName, selectedBugs);

        // Baselines
        System.out.println("Title: ");
        String approachQueryFile = StaticData.HOME_DIR + "/Lareina/Baseline/query/query-title/" + repoName + ".txt";
        HashMap<Integer, String> baselineQuery = QueryLoader.loadQueryIncludeTab(approachQueryFile);
        System.out.println(issueID + "\t" + baselineQuery.get(issueID));
        System.out.println();

        System.out.println("Title-top10: ");
        String approachQueryFile2 = StaticData.HOME_DIR + "/Lareina/Baseline/query/query-title-top10/" + repoName + ".txt";
        HashMap<Integer, String> baselineQuery2 = QueryLoader.loadQueryIncludeTab(approachQueryFile2);
        System.out.println(issueID + "\t" + baselineQuery2.get(issueID));
        System.out.println();

        System.out.println("Description: ");
        String approachQueryFile3 = StaticData.HOME_DIR + "/Lareina/Baseline/query/query-desc/" + repoName + ".txt";
        HashMap<Integer, String> baselineQuery3 = QueryLoader.loadQueryIncludeTab(approachQueryFile3);
        System.out.println(issueID + "\t" + baselineQuery3.get(issueID));
        System.out.println();

        System.out.println("Title+Description: ");
        String approachQueryFile4 = StaticData.HOME_DIR + "/Baseline/query/query-whole/" + repoName + ".txt";
        HashMap<Integer, String> baselineQuery4 = QueryLoader.loadQueryIncludeTab(approachQueryFile4);
        System.out.println(baselineQuery4.get(issueID));
        System.out.println();

        // Change Request
        System.out.println("Change Request: ");
        String query = BugReportLoader.loadBugReport(repoName, issueID);
        System.out.println(query);
        System.out.println();
    }

    public static void run(double[] parameters, String repoName, ArrayList<Integer> selectedBugs) {

        List<String> scoreKeyList = Arrays.asList("TR", "PR", "SR", "BTR", "PTR");
        StaticData.SIMILARITY_THRESHOLD = 0.5;
        StaticData.ADD_TITLE=true;

        StaticData.TR_alpha = parameters[0];
        StaticData.PR_beta = parameters[1];
        StaticData.SR_gamma = parameters[2];
        StaticData.BTR_delta = parameters[3];
        StaticData.PTR_epsilon = parameters[4];

        ArrayList<String> queries = new SearchQueryProvider(
                repoName, selectedBugs, scoreKeyList).provideSearchQueriesByTokenScoreMap();
//        ArrayList<String> queries = new SearchQueryProvider(repoName, selectedBugs, scoreKeyList).provideSearchQueries();
        MiscUtility.showItems(queries);
        System.out.println();
    }
}