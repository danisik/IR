package cz.zcu.kiv.nlp.ir.trec.data;

import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.document.DocumentWordValues;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tigi on 8.1.2015.
 *
 * Ukázka implementace rozhraní {@link Document}
 *
 * Tuto třídu si můžete libovolně upravovat pokud vám nevyhovuje nebo můžete vytvořit vlastní třídu, která
 * implementuje rozhraní {@link Document}.
 *
 */
public class DocumentNew implements Document, Serializable {
    private String text;
    private String id;
    private String title;
    private Date date;

    private Map<String, DocumentWordValues> words;

    public DocumentNew() {
        words = new HashMap<>();
    }

    private final static long serialVersionUID = -5097715898427114007L;

    @Override
    public String toString() {
        return "DocumentNew{" +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                "text='" + text + '\'' +
                '}';
    }

    public String getDataForPreprocessing() {
        return title + " " + text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
