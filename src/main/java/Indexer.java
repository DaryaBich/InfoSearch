import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;

import java.io.*;
import java.util.ArrayList;

public class Indexer {
    private IndexWriter indexWriter;
    private IndexWriterConfig indexWriterConfig;

    public Indexer(String indexDirectoryPath) throws IOException {
        Directory index = FSDirectory.open(new File(indexDirectoryPath).toPath());
        indexWriterConfig = new IndexWriterConfig();
        indexWriter = new IndexWriter(index, indexWriterConfig);
    }

    public void close() throws IOException {
        indexWriter.close();
    }

    private static Document getDocument(Series series) {
        Document document = new Document();
        document.add(new TextField(Constants.NAME, series.name, Field.Store.YES));
        document.add(new StringField(Constants.HREF, series.href, Field.Store.YES));
        document.add(new IntPoint(Constants.RATING, series.rating));
        document.add(new TextField(Constants.DESCRIPTION, series.description, Field.Store.YES));
        return document;
    }

    public void createIndex (ArrayList<Series> list) throws IOException {
        for(Series series : list) {
            indexWriter.addDocument(getDocument(series));
        }
        close();
    }
}