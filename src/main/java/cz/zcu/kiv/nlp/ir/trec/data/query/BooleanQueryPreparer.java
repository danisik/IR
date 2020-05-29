package cz.zcu.kiv.nlp.ir.trec.data.query;

import org.apache.lucene.queryparser.classic.ParseException;

import java.util.List;

/**
 * Třída, která má za úkol zpracovat BOOLEAN query.
 */
public class BooleanQueryPreparer {

    /**
     * Metoda připraví danou query (uzávorkuje NOT bloky).
     * @param tokens - Seznam / podseznam slov nebo znaků ze zpracovávané query.
     * @return Upravená (část) query.
     * @throws ParseException Pokud je jako první symbol roven NOT.
     */
    public static String prepareQuery(List<String> tokens) throws ParseException {
        return prepareQueryRecord(tokens, false).getQuery();
    }

    /**
     * Metoda analyzuje vloženou query jako seznam symbolů a
     * upraví tuto query tím způsobem, že upraví
     * např. AND NOT alfa -> AND (NOT alfa). Takto upravená query je pak správně
     * rozparsovaná Lucene QueryParser-em.
     * @param tokens - Seznam / podseznam slov nebo znaků ze zpracovávané query.
     * @param nested - True pokud tato metoda je volána vnořeně, false pokud ne.
     * @return Upravená (část) query.
     * @throws ParseException Pokud je jako první symbol roven NOT.
     */
    private static BooleanQueryPrepareRecord prepareQueryRecord(List<String> tokens, boolean nested) throws ParseException {
        String newQuery = "";

        // Projeď všechny slova v query.
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            // Pokud token je NOT, tak se ho snaž obalit v závorkách
            // -> Lucene QueryParser totiž nezvládá AND NOT / OR NOT.
            if (token.equals("NOT")) {

                // Pokud je NOT na první řádce, vyhoď parse exception.
                if (i == 0) {
                    throw new ParseException();
                }

                // Vložení levé závorky před NOT.
                newQuery += "(" + token;

                if ((i + 1) < tokens.size()) {
                    if (tokens.get(i + 1).contains("(")) {
                        // Pokud je NOT před závorkou, tak projeď ostatní tokeny dokud nedorazíš na první pravo závorku.
                        BooleanQueryPrepareRecord record = prepareQueryRecord(tokens.subList(i + 1, tokens.size()), true);
                        newQuery += " " + record.getQuery() + ")";
                        i += record.getOffset() + 1;

                        // Pokud je aktuální volání vnořené, vrať aktuálně zpracovaný NOT blok.
                        if (nested) {
                            return new BooleanQueryPrepareRecord(i, newQuery);
                        }
                    }
                    else {
                        // Pokud je po NOT jen text, tak vlož pravou závorku za ten text.
                        newQuery += " " + tokens.get(i + 1) + ")";
                        i++;

                        // Pokud je aktuální volání vnořené, vrať aktuálně zpracovaný NOT blok.
                        if (nested) {
                            return new BooleanQueryPrepareRecord(i, newQuery);
                        }
                    }
                }
            }
            else {
                // Pokud aktuální token není NOT, pouze přidej token do nové query.
                newQuery += token;
            }

            if ((i + 1) < tokens.size()) newQuery += " ";
        }

        return new BooleanQueryPrepareRecord(tokens.size(), newQuery);
    }
}
