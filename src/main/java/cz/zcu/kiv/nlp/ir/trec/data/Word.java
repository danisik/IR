package cz.zcu.kiv.nlp.ir.trec.data;

public class Word {
    private String word;

    private Float idf;

    public Word(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public Float getIdf() {
        return idf;
    }

    public void setIdf(Float idf) {
        this.idf = idf;
    }

    @Override
    public int hashCode()
    {
        return 31 * word.hashCode() + word.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Word))
            return false;

        Word t = (Word)obj;
        return word.equals(t.word);
    }
}
