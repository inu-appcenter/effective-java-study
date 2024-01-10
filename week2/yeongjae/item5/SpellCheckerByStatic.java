package org.items.item5;

public class SpellCheckerByStatic {

    public static final Lexicon lexicon = new KoreanDictionary();

    private SpellCheckerByStatic() {
    }

    public static boolean isValid(String word) {
        return word.equals("Lee");
    }
}
