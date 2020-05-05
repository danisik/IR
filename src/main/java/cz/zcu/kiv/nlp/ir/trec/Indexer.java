package cz.zcu.kiv.nlp.ir.trec;

import cz.zcu.kiv.nlp.ir.trec.data.Document;

import javax.print.Doc;
import java.util.List;

/**
 * Created by Tigi on 6.1.2015.
 *
 * Rozhraní, pro indexaci dokumentů.
 *
 * Pokud potřebujete/chcete můžete přidat další metody např. pro indexaci po jednotlivých dokumentech
 * a jiné potřebné metody (např. CRUD operace update, delete, ... dokumentů), ale zachovejte původní metodu.
 *
 * metodu index implementujte ve třídě {@link Index}
 */
public interface Indexer {

    /**
     * Metoda zaindexuje zadaný seznam dokumentů
     *
     * @param documents list dokumentů
     */
    void index(List<Document> documents);

    /**
     * Metoda zaindexuje jeden dokument.
     * @param document dokument.
     */
    void index(Document document);

    boolean loadIndexedData(String filename);

    boolean saveIndexedData(String filename);
}
