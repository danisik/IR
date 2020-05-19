package cz.zcu.kiv.nlp.ir.trec.data.query;

import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultImpl;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;

import java.util.ArrayList;
import java.util.List;

public class QueryParser {

    private static List<Document> documents;

    public static List<Result> getResults(TermQuery query) {
        List<Result> results = new ArrayList<>();

        for (Document document : documents) {
            if (document.getDataForPreprocessing().contains(query.getTerm().text())) {

            }
        }

        return results;
    }

    public static List<Result> getResults(BooleanQuery query) {
        List<Result> results = new ArrayList<>();

        return results;
    }
}
