import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static org.testng.Assert.assertEquals;

public class Test_TestAssignment {
    private Matrix m1 = new Matrix(new int[][] {{1, 1}, {1, 1}});

    private Matrix m = new Matrix(new int[][] {{1, 1}, {1, 1}});

    private Matrix m2 = new Matrix(new int[][] {{-2, -2}, {0, 0}});
    private Matrix m3 = new Matrix(new int[][] {{0, 0}, {0, 0}});
    private Matrix m4 = new Matrix(new int[][] {{1, 2, 3}, {4, 5, 6}});
    private Matrix m5 = new Matrix(new int[][] {{1, 4, 7, 10}, {2, 5, 8, 11}, {3, 6, 9, 12}});


    @DataProvider
    public Object[][] positiveDataToFillMatrix() {
        return new Object[][]{
                {"A=[1 2 2; 0 3 1; -1 2 -4]", new int[][]{{1, 2, 2}, {0, 3, 1}, {-1, 2, -4}}},
                {"B=[2 1 0; -2 -1 -1; 1 1 2]", new int[][]{{2, 1, 0}, {-2, -1, -1}, {1, 1, 2}}},
                {"R=[-1 -7 6; -2 9 -4; 6 -10 2]", new int[][]{{-1, -7, 6}, {-2, 9, -4}, {6, -10, 2}}}
        };
    }

    @Test(dataProvider = "positiveDataToFillMatrix")
    public void fillMatrixPositiveTest(String matrixStr, int[][] matrix) {
        int[][] actualMatrix = new Matrix(matrixStr).getMatrix();
        assertEquals(actualMatrix, matrix);
    }

    @DataProvider
    public Object[][] negativeDataToFillMatrix() {
        return new Object[][]{
                {"M=[-9 5 9; -7 8x 7; 10 -3 3]`"},
                {"M=[-9 a 9; h 8x 7; 10 -3 3]"}
        };
    }

    @Test(dataProvider = "negativeDataToFillMatrix", expectedExceptions = NumberFormatException.class)
    public void fillMatrixNegativeTest(String matrixStr) {
        Matrix m = new Matrix(matrixStr);
    }

    @DataProvider
    public Object[][] additionPositiveData() {
        return new Object[][] {
                {Arrays.asList(m1, m2), new int[][] {{-1, -1}, {1, 1}}},
                {Arrays.asList(m1, m3), new int[][] {{1, 1}, {1, 1}}},
                {Arrays.asList(m2, m2), new int[][] {{-4, -4}, {0, 0}}}
        };
    }

    @DataProvider
    public Object[][] additionNegativeData() {
        return new Object[][] {
                {Arrays.asList(m1, m4)},
                {Arrays.asList(m4, m3)},
                {Arrays.asList(m5, m2)}
        };
    }

    @DataProvider
    public Object[][] subtractionPositiveData() {
        return new Object[][] {
                {Arrays.asList(m1, m2), new int[][] {{3, 3}, {1, 1}}},
                {Arrays.asList(m1, m3), new int[][] {{1, 1}, {1, 1}}},
                {Arrays.asList(m2, m2), new int[][] {{0, 0}, {0, 0}}}
        };
    }

    @DataProvider
    public Object[][] multiplicationPositiveData() {
        return new Object[][] {
                {Arrays.asList(m4, m5), new int[][] {{14, 32, 50, 68}, {32, 77, 122, 167}}},
                {Arrays.asList(m1, m2), new int[][]{{-2, -2}, {-2, -2}}},
                {Arrays.asList(m2, m3), new int[][]{{0, 0}, {0, 0}}}
        };
    }

    @DataProvider
    public Object[][] multiplicationNegativeData() {
        return new Object[][] {
                {Arrays.asList(m5, m4)},
                {Arrays.asList(m4, m2)},
                {Arrays.asList(m3, m5)}
        };
    }

    @Test(dataProvider = "additionPositiveData")
    public void additionMatrixTest(List<Matrix> matrices, int[][] resultMatrix) {
        Calculator calculator = new Calculator();
        assertEquals(calculator.addition(matrices.get(0), matrices.get(1)).getMatrix(), resultMatrix);
    }

    @Test(dataProvider = "subtractionPositiveData")
    public void subtractionMatrixTest(List<Matrix> matrices, int[][] resultMatrix) {
        Calculator calculator = new Calculator();
        assertEquals(calculator.subtraction(matrices.get(0), matrices.get(1)).getMatrix(), resultMatrix);
    }

    @Test(dataProvider = "multiplicationPositiveData")
    public void multiplicationMatrixTest(List<Matrix> matrices, int[][] resultMatrix) {
        Calculator calculator = new Calculator();
        assertEquals(calculator.multiplication(matrices.get(0), matrices.get(1)).getMatrix(), resultMatrix);
    }

    @Test(dataProvider = "additionNegativeData",
            expectedExceptions = IllegalArgumentException.class)
    public void additionMatrixNegativeTest(List<Matrix> matrices) {
        Calculator calculator = new Calculator();
        calculator.addition(matrices.get(0), matrices.get(1));
    }

    @Test(dataProvider = "additionNegativeData",
            expectedExceptions = IllegalArgumentException.class)
    public void subtractionMatrixNegativeTest(List<Matrix> matrices) {
        Calculator calculator = new Calculator();
        calculator.subtraction(matrices.get(0), matrices.get(1));
    }

    @Test(dataProvider = "multiplicationNegativeData",
            expectedExceptions = IllegalArgumentException.class)
    public void multiplicationMatrixNegativeTest(List<Matrix> matrices) {
        Calculator calculator = new Calculator();
        calculator.multiplication(matrices.get(0), matrices.get(1));
    }

    @DataProvider
    public Object[][] convertToRPNData() {
        return new Object[][] {
                {"R+E+B", "RE+B+"},
                {"D*K+M", "DK*M+"},
                {"K+K*R*K", "KKR*K*+"}
        };
    }

    @Test(dataProvider = "convertToRPNData")
    public void convertToRPNTest(String expression, String expected) {
        Calculator calculator = new Calculator();
        assertEquals(calculator.convertToRPN(expression), expected);
    }

    @DataProvider
    public Object[][] calculateData() {
        return new Object[][] {
                {"src/main/resources/pub01in.txt", "src/main/resources/pub01out.txt"},
                {"src/main/resources/pub02in.txt", "src/main/resources/pub02out.txt"},
                {"src/main/resources/pub03in.txt", "src/main/resources/pub03out.txt"}
        };
    }

    @Test(dataProvider = "calculateData")
    public void calculateExpressionTest(String input, String output) throws FileNotFoundException {
        File file = new File(input);
        Scanner scanner = new Scanner(file);
        Map<Character, Matrix> characterMatrixMap = new HashMap<>();
        Matrix result = null;
        while (scanner.hasNext()) {
            String str = scanner.nextLine().trim();
            if (str.isBlank()) {
                continue;
            }
            if (str.matches("[A-Z]\\s?[=].*$")) {
                characterMatrixMap.put(str.charAt(0), new Matrix(str));
            } else if (!characterMatrixMap.isEmpty()) {
                Calculator calculator = new Calculator();
                result = calculator.calculateExpression(characterMatrixMap, str);
                break;
            }
        }
        file = new File(output);
        scanner = new Scanner(file);
        if(scanner.hasNext() && result != null)
            assertEquals(scanner.nextLine(), result.toString());
    }

}
