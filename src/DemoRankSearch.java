import strict.lucenecheck.LuceneSearcher;

import java.io.IOException;
import java.util.Scanner;

public class DemoRankSearch {

    public static void main(String[] args) throws IOException {
//        int issueID = 303705;
//        String repository = "eclipse.jdt.ui";

        Scanner scanner = new Scanner(System.in);

        System.out.print("RepoName: ");
        String repoName = scanner.nextLine(); // eclipse.jdt.ui
        System.out.print("The Issue# in " + repoName + ": ");
        int issueID = scanner.nextInt(); // 303705
        scanner.nextLine();

        System.out.print("searchQuery: ");
        String searchQuery = scanner.nextLine(); // eclipse.jdt.ui
        while (!searchQuery.equals("stop")) {
            LuceneSearcher searcher = new LuceneSearcher(issueID, repoName, searchQuery);
            System.out.println("First correct rank: " + searcher.getFirstGoldRank() + "\n");
            System.out.print("searchQuery: ");
            searchQuery = scanner.nextLine();
        }
    }
}
