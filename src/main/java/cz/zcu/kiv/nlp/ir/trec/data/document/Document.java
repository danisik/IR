package cz.zcu.kiv.nlp.ir.trec.data.document;

import java.util.Date;
import java.util.Map;

/**
 * Created by Tigi on 8.1.2015.
 *
 * Rozhraní reprezentuje dokument, který je možné indexovat a vyhledávat.
 *
 * Implementujte toto rozhranní.
 *
 * Pokud potřebujete můžete do rozhranní přidat metody, ale signaturu stávajících metod neměnte.
 *
 */
public interface Document {

    /**
     * Text dokumentu
     * @return text
     */
    String getText();

    /**
     * Unikátní id dokumentu
     * @return id dokumentu
     */
    String getId();

    /**
     * Nastavení unikátního id dokumentu.
     * @param id - Id dokumentu.
     */
    void setId(String id);

    /**
     * Titulek dokumentu
     * @return titulek dokumentu
     */
    String getTitle();

    /**
     * Datum přidání dokumentu (tj. např. indexace nebo stažení nebo publikování
     *
     * @return datum vztažené k dokumentu
     */
    Date getDate();

    /**
     * Získání řádky textu, která bude použita pro preprocessing pro daný dokument.
     * @return Text.
     */
    String getDataForPreprocessing();

}
