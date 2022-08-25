package exercisegenerator.algorithms;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;

public abstract class CodingAlgorithms {

    public static final List<Character> BINARY_ALPHABET = Arrays.asList('0', '1');

    private static final String CODE_BOOK_FORMAT_ERROR_MESSAGE =
        "The specified code book does not match the expected format (entries of the form 'S':\"C\" for a symbol S and a code C, separated by commas)!";

    public static void decodeHuffman(final AlgorithmInput input) throws IOException {
        final Map<Character, String> codeBook = CodingAlgorithms.parseCodeBook(input.options);
        final String targetText = CodingAlgorithms.parseOrGenerateTargetText(codeBook, input.options);
        final String result = CodingAlgorithms.decodeHuffman(targetText, new HuffmanTree(codeBook));
        CodingAlgorithms.printExerciseAndSolutionForHuffmanDecoding(
            targetText,
            codeBook,
            result,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static String decodeHuffman(final String targetText, final HuffmanTree tree) {
        return tree.decode(targetText);
    }

    public static void encodeHuffman(final AlgorithmInput input) throws IOException {
        final String sourceText = CodingAlgorithms.parseOrGenerateSourceText(input.options);
        final List<Character> targetAlphabet = CodingAlgorithms.parseOrGenerateTargetAlphabet(input.options);
        final Pair<HuffmanTree, String> result = CodingAlgorithms.encodeHuffman(sourceText, targetAlphabet);
        CodingAlgorithms.printExerciseAndSolutionForHuffmanEncoding(
            sourceText,
            targetAlphabet,
            result,
            input.exerciseWriter,
            input.solutionWriter
        );
    }

    public static Pair<HuffmanTree, String> encodeHuffman(
        final String sourceText,
        final List<Character> targetAlphabet
    ) {
        final HuffmanTree tree = new HuffmanTree(sourceText, targetAlphabet);
        return new Pair<HuffmanTree, String>(tree, tree.toEncoder().encode(sourceText));
    }

    private static List<Character> generateAlphabet(final int alphabetSize, final Random gen) {
        final List<Character> biggestAlphabet = new ArrayList<Character>();
        for (int i = 32; i < 127; i++) {
            biggestAlphabet.add(Character.valueOf((char)i));
        }
        if (alphabetSize >= biggestAlphabet.size()) {
            return biggestAlphabet;
        }
        final List<Character> result = new ArrayList<Character>();
        for (int i = 0; i < alphabetSize; i++) {
            result.add(biggestAlphabet.remove(gen.nextInt(biggestAlphabet.size())));
        }
        return result;
    }

    private static String generateSourceText(final Map<Flag, String> options) {
        final Random gen = new Random();
        final int alphabetSize = CodingAlgorithms.parseOrGenerateAlphabetSize(options, gen);
        final int textLength = CodingAlgorithms.parseOrGenerateTextLength(options, gen);
        final List<Character> alphabet = CodingAlgorithms.generateAlphabet(alphabetSize, gen);
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            result.append(alphabet.get(gen.nextInt(alphabetSize)));
        }
        return result.toString();
    }

    private static String generateTargetText(final Map<Character, String> codeBook, final Map<Flag, String> options) {
        final Random gen = new Random();
        final int length = CodingAlgorithms.parseOrGenerateTextLength(options, gen);
        final StringBuilder result = new StringBuilder();
        final List<String> samples = new ArrayList<String>(codeBook.values());
        for (int i = 0; i < length; i++) {
            result.append(samples.get(gen.nextInt(samples.size())));
        }
        return result.toString();
    }

    private static Map<Character, String> parseCodeBook(final Map<Flag, String> options) {
        return Arrays.stream(options.get(Flag.OPERATIONS).split(","))
            .map(entry -> {
                final String[] assignment = entry.split(":");
                if (assignment.length != 2 || !assignment[0].matches("'.'") || !assignment[1].matches("\".+\"")) {
                    throw new IllegalArgumentException(CodingAlgorithms.CODE_BOOK_FORMAT_ERROR_MESSAGE);
                }
                return new Pair<Character, String>(
                    assignment[0].charAt(1),
                    assignment[1].substring(1, assignment[1].length() - 1)
                );
            }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private static String parseInputText(final BufferedReader reader, final Map<Flag, String> options)
    throws IOException {
        return reader.readLine();
    }

    private static int parseOrGenerateAlphabetSize(final Map<Flag, String> options, final Random gen) {
        if (options.containsKey(Flag.DEGREE)) {
            return Integer.parseInt(options.get(Flag.DEGREE));
        }
        return gen.nextInt(6) + 5;
    }

    private static String parseOrGenerateSourceText(final Map<Flag, String> options) throws IOException {
        return new ParserAndGenerator<String>(
            CodingAlgorithms::parseInputText,
            CodingAlgorithms::generateSourceText
        ).getResult(options);
    }

    private static List<Character> parseOrGenerateTargetAlphabet(final Map<Flag, String> options) throws IOException {
        if (options.containsKey(Flag.OPERATIONS)) {
            return options.get(Flag.OPERATIONS).chars().mapToObj(c -> (char)c).toList();
        }
        return CodingAlgorithms.BINARY_ALPHABET;
    }

    private static String parseOrGenerateTargetText(
        final Map<Character, String> codeBook,
        final Map<Flag, String> options
    ) throws IOException {
        return new ParserAndGenerator<String>(
            CodingAlgorithms::parseInputText,
            flags -> CodingAlgorithms.generateTargetText(codeBook, flags)
        ).getResult(options);
    }

    private static int parseOrGenerateTextLength(final Map<Flag, String> options, final Random gen) {
        if (options.containsKey(Flag.LENGTH)) {
            return Integer.parseInt(options.get(Flag.LENGTH));
        }
        return gen.nextInt(16) + 5;
    }

    private static void printCode(
        final String code,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write("\\textbf{Code:}\\\\");
        Main.newLine(exerciseWriter);
        solutionWriter.write("\\textbf{Code:}\\\\");
        Main.newLine(solutionWriter);
        solutionWriter.write(TikZUtils.code(code));
        Main.newLine(solutionWriter);
    }

    private static void printCodeBookForDecoding(final Map<Character, String> codeBook, final BufferedWriter writer)
    throws IOException {
        writer.write("\\textbf{Codebuch:}");
        Main.newLine(writer);
        TikZUtils.printBeginning("align*", writer);
        for (final Pair<Character, String> assignment : CodingAlgorithms.toSortedList(codeBook)) {
            writer.write(
                String.format(
                    "\\code{`%s'} &= \\code{\\textquotedbl{}%s\\textquotedbl{}}\\\\",
                    assignment.x,
                    assignment.y
                )
            );
            Main.newLine(writer);
        }
        TikZUtils.printEnd("align*", writer);
    }

    private static void printCodeBookForEncoding(
        final Map<Character, String> codeBook,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write("\\textbf{Codebuch:}\\\\[2ex]");
        Main.newLine(exerciseWriter);
        solutionWriter.write("\\textbf{Codebuch:}\\\\[2ex]");
        Main.newLine(solutionWriter);
        final int contentLength = codeBook.values().stream().mapToInt(String::length).max().getAsInt();
        for (final Pair<Character, String> assignment : CodingAlgorithms.toSortedList(codeBook)) {
            Algorithm.assignment(
                String.format("\\code{`%s'}", assignment.x),
                Collections.singletonList(
                    new ItemWithTikZInformation<String>(Optional.of(TikZUtils.code(assignment.y)))
                ),
                "\\code{`M'}",
                contentLength,
                exerciseWriter,
                solutionWriter
            );
        }
    }

    private static void printExerciseAndSolutionForHuffmanDecoding(
        final String targetText,
        final Map<Character, String> codeBook,
        final String result,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write(
            "Erzeugen Sie den Quelltext aus dem nachfolgenden Huffman Code mit dem angegebenen Codebuch:\\\\"
        );
        Main.newLine(exerciseWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, exerciseWriter);
        exerciseWriter.write(TikZUtils.code(targetText));
        Main.newLine(exerciseWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exerciseWriter);
        TikZUtils.printVerticalProtectedSpace(exerciseWriter);
        CodingAlgorithms.printCodeBookForDecoding(codeBook, exerciseWriter);
        TikZUtils.printVerticalProtectedSpace("-3ex", exerciseWriter);
        exerciseWriter.write("\\textbf{Quelltext:}\\\\[2ex]");
        Main.newLine(exerciseWriter);
        Main.newLine(exerciseWriter);

        solutionWriter.write(TikZUtils.code(result));
        Main.newLine(solutionWriter);
        Main.newLine(solutionWriter);
    }

    private static void printExerciseAndSolutionForHuffmanEncoding(
        final String sourceText,
        final List<Character> targetAlphabet,
        final Pair<HuffmanTree, String> result,
        final BufferedWriter exerciseWriter,
        final BufferedWriter solutionWriter
    ) throws IOException {
        exerciseWriter.write("Erzeugen Sie den Huffman Code f\\\"ur das Zielalphabet $\\{");
        exerciseWriter.write(targetAlphabet.stream().map(String::valueOf).collect(Collectors.joining(",")));
        exerciseWriter.write("\\}$ und den folgenden Eingabetext:\\\\");
        Main.newLine(exerciseWriter);
        TikZUtils.printBeginning(TikZUtils.CENTER, exerciseWriter);
        exerciseWriter.write(sourceText);
        Main.newLine(exerciseWriter);
        TikZUtils.printEnd(TikZUtils.CENTER, exerciseWriter);
        TikZUtils.printVerticalProtectedSpace(exerciseWriter);
        exerciseWriter.write("Geben Sie zus\\\"atzlich zu dem erstellten Code das erzeugte Codebuch an.\\\\[2ex]");
        Main.newLine(exerciseWriter);
        TikZUtils.printSolutionSpaceBeginning(exerciseWriter);
        CodingAlgorithms.printCodeBookForEncoding(result.x.toCodeBook(), exerciseWriter, solutionWriter);
        TikZUtils.printVerticalProtectedSpace(exerciseWriter);
        TikZUtils.printVerticalProtectedSpace(solutionWriter);
        CodingAlgorithms.printCode(result.y, exerciseWriter, solutionWriter);
        TikZUtils.printSolutionSpaceEnd(exerciseWriter);
        Main.newLine(exerciseWriter);
        Main.newLine(solutionWriter);
    }

    private static List<Pair<Character, String>> toSortedList(final Map<Character, String> codeBook) {
        return codeBook.entrySet()
            .stream()
            .map(entry -> new Pair<Character, String>(entry.getKey(), entry.getValue()))
            .sorted(
                new Comparator<Pair<Character, String>>() {

                    @Override
                    public int compare(final Pair<Character, String> pair1, final Pair<Character, String> pair2) {
                        return Character.compare(pair1.x, pair2.x);
                    }

                }
            ).toList();
    }

}
