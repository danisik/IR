package cz.zcu.kiv.nlp.ir.trec.indexer;

import cz.zcu.kiv.nlp.ir.trec.data.document.Document;

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
     * Metoda zaindexuje jeden dokument
     *
     * @param document - Dokument k zaindexování.
     */
    void index(Document document);

    /**
     * Metoda načte indexovaná data ze souboru.
     * @param filename - Název souboru.
     * @return True pokud načítání proběhlo úspěšně.
     */
    boolean loadIndexedData(String filename);

    /**
     * Metoda uloží indexovaná data do souboru.
     * @param filename - Název souboru.
     * @return True pokud ukládání proběhlo úspěšně.
     */
    boolean saveIndexedData(String filename);
}
