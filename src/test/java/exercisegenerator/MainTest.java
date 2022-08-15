package exercisegenerator;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;
import exercisegenerator.util.*;

public class MainTest {

    private static class ComplementInput {
        private final int currentNodeNumber;
        private final BufferedReader exReader;
        private final BufferedReader solReader;
        private final ComplementTestCase test;

        private ComplementInput(
            final int currentNodeNumber,
            final ComplementTestCase test,
            final BufferedReader exReader,
            final BufferedReader solReader
        ) {
            this.currentNodeNumber = currentNodeNumber;
            this.test = test;
            this.exReader = exReader;
            this.solReader = solReader;
        }
    }

    private static class ComplementTestCase {
        private final int[] binaryNumber;
        private final int number;

        private ComplementTestCase(final int number, final int[] binaryNumber) {
            this.number = number;
            this.binaryNumber = binaryNumber;
        }
    }

    private static final String EX_FILE = "C:\\Daten\\Test\\exgen\\ex.tex";

    private static final String SOL_FILE = "C:\\Daten\\Test\\exgen\\sol.tex";

    private static void complement(
        final CheckedFunction<ComplementInput, Integer, IOException> algorithm,
        final ComplementTestCase[] cases,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        int currentNodeNumber = 0;
        BinaryNumbersTest.complementStart(exReader, solReader);
        boolean first = true;
        for (final ComplementTestCase test : cases) {
            if (first) {
                first = false;
            } else {
                BinaryNumbersTest.complementMiddle(exReader, solReader);
            }
            currentNodeNumber = algorithm.apply(new ComplementInput(currentNodeNumber, test, exReader, solReader));
        }
        BinaryNumbersTest.complementEnd(exReader, solReader);
    }

    private static void fromComplement(
        final ComplementTestCase[] cases,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        MainTest.complement(
            input -> BinaryNumbersTest.fromComplement(
                input.currentNodeNumber,
                input.test.number,
                input.test.binaryNumber,
                Arrays.stream(cases).mapToInt(test -> String.valueOf(test.number).length()).max().getAsInt(),
                input.exReader,
                input.solReader
            ),
            cases,
            exReader,
            solReader
        );
    }

    private static void toComplement(
        final ComplementTestCase[] cases,
        final BufferedReader exReader,
        final BufferedReader solReader
    ) throws IOException {
        MainTest.complement(
            input -> BinaryNumbersTest.toComplement(
                input.currentNodeNumber,
                input.test.number,
                input.test.binaryNumber,
                input.exReader,
                input.solReader
            ),
            cases,
            exReader,
            solReader
        );
    }

    private static String toInput(final ComplementTestCase[] cases) {
        return Arrays.stream(cases).map(c -> String.valueOf(c.number)).collect(Collectors.joining(","));
    }

    @AfterMethod
    public void cleanUp() {
        final File exFile = new File(MainTest.EX_FILE);
        final File solFile = new File(MainTest.SOL_FILE);
        if (exFile.exists()) {
            exFile.delete();
        }
        if (solFile.exists()) {
            solFile.delete();
        }
    }

    @Test
    public void fromOnesComplement() throws IOException {
        if (!Main.STUDENT_MODE) {
            final int bitLength = 8;
            final ComplementTestCase[] cases =
                new ComplementTestCase[] {
                    new ComplementTestCase(-3, new int[] {1,1,1,1,1,1,0,0}),
                    new ComplementTestCase(5, new int[] {0,0,0,0,0,1,0,1}),
                    new ComplementTestCase(-111, new int[] {1,0,0,1,0,0,0,0})
                };
            Main.main(
                new String[]{
                    "-a", "fromonescompl",
                    "-e", MainTest.EX_FILE,
                    "-t", MainTest.SOL_FILE,
                    "-c", String.valueOf(bitLength),
                    "-i", MainTest.toInput(cases)
                }
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), Patterns.fromOnes(bitLength));
                MainTest.fromComplement(cases, exReader, solReader);
            }
        }
    }

    @Test
    public void fromTwosComplement() throws IOException {
        if (!Main.STUDENT_MODE) {
            final int bitLength = 5;
            final ComplementTestCase[] cases =
                new ComplementTestCase[] {
                    new ComplementTestCase(-3, new int[] {1,1,1,0,1}),
                    new ComplementTestCase(1, new int[] {0,0,0,0,1}),
                    new ComplementTestCase(-13, new int[] {1,0,0,1,1})
                };
            Main.main(
                new String[]{
                    "-a", "fromtwoscompl",
                    "-e", MainTest.EX_FILE,
                    "-t", MainTest.SOL_FILE,
                    "-c", String.valueOf(bitLength),
                    "-i", MainTest.toInput(cases)
                }
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), Patterns.fromTwos(bitLength));
                MainTest.fromComplement(cases, exReader, solReader);
            }
        }
    }

    @Test
    public void insertionsort() throws IOException {
        if (!Main.STUDENT_MODE) {
            Main.main(
                new String[]{"-a", "insertionsort", "-e", MainTest.EX_FILE, "-t", MainTest.SOL_FILE, "-i", "3,5,1,4,2"}
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), Patterns.ARRAY_STYLE);
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n0) {\\phantom{0}3};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n1) [right=of n0] {\\phantom{0}5};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) [right=of n1] {\\phantom{0}1};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n3) [right=of n2] {\\phantom{0}4};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) [right=of n3] {\\phantom{0}2};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n30) [below=of n0] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n31) [right=of n30] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n32) [right=of n31] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n33) [right=of n32] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n34) [right=of n33] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n35) [below=of n30] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n36) [right=of n35] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n37) [right=of n36] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n38) [right=of n37] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n39) [right=of n38] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n40) [below=of n35] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n41) [right=of n40] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n42) [right=of n41] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n43) [right=of n42] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n44) [right=of n43] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n45) [below=of n40] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n46) [right=of n45] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n47) [right=of n46] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n48) [right=of n47] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n49) [right=of n48] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertNull(exReader.readLine());

                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), Patterns.ARRAY_STYLE);
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n5) {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n6) [right=of n5] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n7) [right=of n6] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n8) [right=of n7] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n9) [right=of n8] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n10) [below=of n5] {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n11) [right=of n10] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n12) [right=of n11] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n13) [right=of n12] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n14) [right=of n13] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n15) [below=of n10] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n16) [right=of n15] {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n17) [right=of n16] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n18) [right=of n17] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n19) [right=of n18] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n20) [below=of n15] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n21) [right=of n20] {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n22) [right=of n21] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n23) [right=of n22] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n24) [right=of n23] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n25) [below=of n20] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n26) [right=of n25] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n27) [right=of n26] {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n28) [right=of n27] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n29) [right=of n28] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertNull(solReader.readLine());
            }
        }
    }

    @BeforeMethod
    public void prepare() {
        TikZUtils.reset();
    }

    @Test
    public void toOnesComplement() throws IOException {
        if (!Main.STUDENT_MODE) {
            final int bitLength = 4;
            final ComplementTestCase[] cases =
                new ComplementTestCase[] {
                    new ComplementTestCase(3, new int[] {0,0,1,1}),
                    new ComplementTestCase(-1, new int[] {1,1,1,0}),
                    new ComplementTestCase(-2, new int[] {1,1,0,1})
                };
            Main.main(
                new String[]{
                    "-a", "toonescompl",
                    "-e", MainTest.EX_FILE,
                    "-t", MainTest.SOL_FILE,
                    "-c", String.valueOf(bitLength),
                    "-i", MainTest.toInput(cases)
                }
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), Patterns.toOnes(bitLength));
                MainTest.toComplement(cases, exReader, solReader);
            }
        }
    }

    @Test
    public void toTwosComplement() throws IOException {
        if (!Main.STUDENT_MODE) {
            final int bitLength = 6;
            final ComplementTestCase[] cases =
                new ComplementTestCase[] {
                    new ComplementTestCase(-3, new int[] {1,1,1,1,0,1}),
                    new ComplementTestCase(1, new int[] {0,0,0,0,0,1}),
                    new ComplementTestCase(-29, new int[] {1,0,0,0,1,1})
                };
            Main.main(
                new String[]{
                    "-a", "totwoscompl",
                    "-e", MainTest.EX_FILE,
                    "-t", MainTest.SOL_FILE,
                    "-c", String.valueOf(bitLength),
                    "-i", MainTest.toInput(cases)
                }
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), Patterns.toTwos(bitLength));
                MainTest.toComplement(cases, exReader, solReader);
            }
        }
    }

}