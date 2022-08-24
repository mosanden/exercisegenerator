package exercisegenerator.structures;

import java.util.*;

public class HuffmanLeaf extends HuffmanNode {

    public final char sourceSymbol;

    public HuffmanLeaf(final int frequency, final char sourceSymbol) {
        super(frequency);
        this.sourceSymbol = sourceSymbol;
    }

    @Override
    public int getDepth() {
        return 0;
    }

    @Override
    public char getLeastSourceSymbol() {
        return this.sourceSymbol;
    }

    @Override
    Pair<Character, List<Character>> decode(final List<Character> targetText) {
        return new Pair<Character, List<Character>>(this.sourceSymbol, targetText);
    }

    @Override
    void fillCodeBook(final String prefix, final Map<Character, String> codeBook) {
        codeBook.put(this.sourceSymbol, prefix);
    }

}
