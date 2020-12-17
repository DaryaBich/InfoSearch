import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class WordWorker {
    public static void getWords() throws IOException {
        List<Series> series = Reader.readSeries("data/series.txt");
        Set<String> words = new HashSet<>();
        series.stream().forEach(oneSeries-> words.addAll(WordWorker.getWords(oneSeries)));
        Reader.writeToFileByLine(words, "data/words.txt");
    }

    private static Set<String> getWords(Series series) {
        if (series == null) {
            return new HashSet<>();
        }
        Set<String> words = getWords(series.name);
        return words;
    }

    private static Set<String> getWords(String phrase) {
        Set<String> words = Arrays.asList(phrase.split("\\s"))
                .stream()
                .map(x->x.trim()
                        .replaceAll("[.,!?;:\"()%№+-=*<>$&]", "")
                        .replaceAll("\\d+", ""))
                .filter(x->!x.isEmpty())
                .collect(Collectors.toSet());
        return words;
    }
}