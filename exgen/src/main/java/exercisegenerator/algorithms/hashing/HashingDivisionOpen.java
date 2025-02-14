package exercisegenerator.algorithms.hashing;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.algorithms.hashing.Hashing.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.*;
import exercisegenerator.util.*;

public class HashingDivisionOpen implements AlgorithmImplementation {

    public static final HashingDivisionOpen INSTANCE = new HashingDivisionOpen();

    private static String toAdditionalHint(final int length) {
        return Main.TEXT_VERSION == TextVersion.ABRAHAM ? String.format(" ($f(n) = n \\mod %d$)", length) : "";
    }

    private HashingDivisionOpen() {}

    @Override
    public void executeAlgorithm(final AlgorithmInput input) throws IOException {
        final List<Integer> values = Hashing.parseOrGenerateValues(input.options);
        final HashList[] initialHashTable = Hashing.parseOrGenerateInitialArray(values.size(), input.options);
        try {
            final HashList[] result = Hashing.hashingWithDivisionMethod(values, initialHashTable, Optional.empty());
            Hashing.printHashingExerciseAndSolution(
                values,
                initialHashTable,
                result,
                new PrintOptions(
                    Hashing.DIVISION_METHOD
                    .concat(Hashing.NO_PROBING)
                    .concat(Hashing.GENERAL_HASHING_EXERCISE_TEXT_END)
                    .concat(HashingDivisionOpen.toAdditionalHint(initialHashTable.length)),
                    Hashing.toParameterString(initialHashTable.length),
                    false,
                    PreprintMode.parsePreprintMode(input.options)
                ),
                input.options,
                input.exerciseWriter,
                input.solutionWriter
            );
        } catch (final HashException e) {
            throw new IOException(e); //TODO is this possible at all?
        }
    }

    @Override
    public String[] generateTestParameters() {
        final String[] result = new String[2];
        result[0] = "-l";
        result[1] = "5";
        return result; //TODO
    }

}
