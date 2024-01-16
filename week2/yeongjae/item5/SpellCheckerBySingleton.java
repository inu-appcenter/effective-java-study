package org.items.item5;


public class SpellCheckerBySingleton {

    private final Lexicon dictionary = new KoreanDictionary();

    private SpellCheckerBySingleton(Lexicon dictionary) {
    }

    public boolean isValid(String word) {
        return word.equals("lee");
    }
}