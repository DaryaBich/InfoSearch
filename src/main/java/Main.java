import com.google.gson.Gson;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
// поиск синонимов \/
// ранжирование
// поиск в диапазоне рейтинга \/
public class Main {
    private static String indexPath = "index";
    private static String filePath = "D:\\InformationSearch\\MyShows\\data\\series.txt";
    public static Gson gson = new Gson();

    public static String findValue(String str, String start, char end) {
        String rez = "";
        int pos = str.indexOf(start);
        pos += start.length();
        while (str.charAt(pos) != end) {
            rez += str.charAt(pos);
            pos++;
        }
        return rez;
    }

    public static void loadData() throws InterruptedException, IOException {
        Document doc = null;
        ArrayList<Series> tempArray = new ArrayList<Series>();
        if (Files.notExists(Paths.get(filePath))) {
            Files.createFile(Paths.get(filePath));
        } else {
            Files.write(Paths.get(filePath), Collections.singleton(""));
        }
        System.out.println("=== Parsing start ===");
        try (FileWriter fileWriter = new FileWriter(filePath, true)) {
            for (int i = 0; i <= 5; i++) {
                doc = Jsoup.connect("https://myshows.me/search/all/?page=" + i).userAgent("Yandex").get();
                Elements currentpagedata = doc.select("body > div.wrapper > div.container.content > div > " +
                        "main > table > tbody > tr");
                currentpagedata.remove(0);
                for (Element el : currentpagedata) {
                    Document seriesDoc = null;
                    String href = findValue(el.toString(),
                            "href=\"https://myshows.me/view",
                            '\"');
                    String name = findValue(el.toString(),
                            "href=\"https://myshows.me/view" + href + "\">",
                            '<');
                    Integer rating = Integer.parseInt(findValue(el.toString(),
                            "span class=\"stars _",
                            '\"'));
                    seriesDoc = Jsoup.connect("https://myshows.me/view" + href).userAgent("Yandex").get();
                    Element mainSeriesInformation = seriesDoc.selectFirst("body > div.wrapper > div > div > main");
                    String description = mainSeriesInformation.select("div.col5").select("p").text();
                    Series series = new Series(name, "https://myshows.me/view" + href, rating, description);
                    tempArray.add(series);
                    System.out.println("\"" + name + "\" add in list");
                }
                Indexer indexer = new Indexer(indexPath);
                indexer.createIndex(tempArray);
                Reader.writeToFile(tempArray, "data/series.txt");
                System.out.println("--- " + (i + 1) + " page ready ---");
            }
        }
        System.out.println("=== Parsing finish ===\n");
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
//        loadData();    // Uncommit for load data

        System.out.println("=== Searching start ===");
        Searcher searcher = new Searcher();

        System.out.println("Введите слово для поиска:");
        Scanner in = new Scanner(System.in);
        String search_word = in.next();
//         Task 2 - Search by text field
        Set<Series> byName = searcher.search(Constants.NAME, search_word, indexPath, null);
        System.out.println("Results:");
        for (Series series : byName) {
            System.out.println(series.name + ":" + series.score+ ": "+series.href );
        }

        System.out.println("Ввведите левую границу рейтинга:");
        String left = in.next();
        System.out.println("Ввведите правую границу рейтинга:");
        String right = in.next();
        // Task 3 - Search in range
        Set<Series> byRange = searcher.search(Constants.RATING, left, right);
        System.out.println("Results:");
        for (Series series : byRange) {
            System.out.println(series.name + ": " + series.href + ": " + series.score);
        }

//         Task 3 - Synonyms
        WordWorker.getWords();    // Uncommit for get all words
//        Synonym.getSynonyms();    // Uncommit for get synonyms

        System.out.println("Ввведите слово для поиска синонима:");
        String forSynonymSearch = in.next();
        Set<Series> bySynonym = searcher.searchBySynonym(forSynonymSearch);
        System.out.println("Results:");
        for (Series series : bySynonym) {
            System.out.println(" " + series.name + ":"+series.href +":"+series.score);
        }

        System.out.println("=== Searching finish ===");
    }
}



