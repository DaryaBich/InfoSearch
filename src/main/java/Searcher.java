import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;

import java.io.*;
import java.util.*;

public class Searcher {
    public Set<Series> search(String constant, String text, String indexDirectoryPath, Query inputQuery) throws IOException, ParseException {
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());
        IndexReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = null;
        if (inputQuery == null) {
            QueryParser queryParser = new QueryParser(constant, new StandardAnalyzer());
            query = queryParser.parse(text);
        } else {
            query = inputQuery;
        }
        System.out.println("\nSearch '" + query + "'");
        TopDocs docs = indexSearcher.search(query, 10000);
        long num = docs.totalHits;
        Map<String, Series> searchedResults = new HashMap<>();
        for(ScoreDoc scoreDoc : docs.scoreDocs){
            Document doc = indexSearcher.doc(scoreDoc.doc);
            String href = doc.get(Constants.HREF);
            searchedResults.put(href, new Series(doc.get(Constants.NAME), href, doc.get(Constants.DESCRIPTION),
                    scoreDoc.score));
        }
        return new HashSet(searchedResults.values());
    }

    public Set<Series> search(String constant, String lowerValue, String upperValue) throws IOException, ParseException {
        Integer lowerRating =  Integer.parseInt(lowerValue);
        Integer upperRating = Integer.parseInt(upperValue);
        Query query = IntPoint.newRangeQuery(constant, lowerRating, upperRating);
        Set<Series> series = search(null, null, "index", query);
        return series;
    }

    public Set<Series> searchBySynonym(String text) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser(Constants.NAME, new SynonymAnalyzer());
        Query query = queryParser.parse(text);
        Set<Series> series = search(null,null,"index", query);
        return series;
    }
}