import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.System.exit;

public class TestAssignment {
    private static Map<Character, Matrix> characterMatrixMap = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String str = scanner.nextLine().trim();
            if (str.isBlank()) {
                continue;
            }
            if (isMatrixDefinition(str)) {
                characterMatrixMap.put(str.charAt(0), new Matrix(str));
            } else if (!characterMatrixMap.isEmpty()) {
                Calculator calculator = new Calculator();
                Matrix result = calculator.calculateExpression(characterMatrixMap, str);
                System.out.println(result.toString());
                break;
            }
        }
    }

    private static boolean isMatrixDefinition(String str) {
        return str.indexOf('=') > 0;
    }

}

class Matrix {
    private int[][] matrix;

    public Matrix() {

    }

    public Matrix(int n, int m) {
        matrix = new int[n][m];
    }

    public Matrix(String matrixStr) {
        buildMatrix(matrixStr);
    }

    public Matrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public int getElement(int i, int j) {
        return matrix[i][j];
    }

    public void setElement(int i, int j, int value) {
        matrix[i][j] = value;
    }

    public int getNumberOfRows() {
        return matrix.length;
    }

    public int getNumberOfColumns() {
        return matrix[0].length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix1 = (Matrix) o;
        return Arrays.deepEquals(matrix, matrix1.matrix);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(matrix);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < matrix.length; i++) {
            int m = matrix[i].length;
            for (int j = 0; j < m; j++) {
                result.append(matrix[i][j]);
                result.append((j != m - 1) ? " " : (i != matrix.length - 1) ? "; " : "]");
            }
        }
        return result.toString();
    }

    private void buildMatrix(String matrixStr) {
        matrixStr = matrixStr.substring(matrixStr.indexOf('[') + 1, matrixStr.indexOf(']'));
        matrixStr = matrixStr.replaceAll("\\s+", " ");
        String[] splitStr = matrixStr.split(";");
        int n = splitStr.length;
        int m = (int) splitStr[0].trim().chars().filter(ch -> ch == ' ').count() + 1;

        matrix = new int[n][m];
        fillMatrix(splitStr);
    }

    private void fillMatrix(String[] splitStr) {
        for (int i = 0; i < splitStr.length; i++) {
            String[] elements = splitStr[i].trim().split(" ");
            for (int j = 0; j < elements.length; j++) {
                try {
                    matrix[i][j] = Integer.parseInt(elements[j]);
                } catch (NumberFormatException exception) {
                    System.err.println("Exception caught: " + exception.getClass() + " Can't read matrix.");
                    exit(1);
                }
            }
        }
    }
}

class Calculator {

    public Matrix addition(Matrix a, Matrix b) {
        if (a.getNumberOfColumns() != b.getNumberOfColumns() || a.getNumberOfRows() != b.getNumberOfRows()) {
            System.err.println("Exception caught: " + IllegalArgumentException.class + " Can't perform addition.");
            exit(1);
        }
        Matrix result = new Matrix(a.getNumberOfRows(), a.getNumberOfColumns());
        for (int i = 0; i < a.getNumberOfRows(); i++) {
            for (int j = 0; j < a.getNumberOfColumns(); j++) {
                result.setElement(i, j, a.getElement(i, j) + b.getElement(i, j));
            }
        }
        return result;
    }

    public Matrix subtraction(Matrix a, Matrix b) {
        if (a.getNumberOfColumns() != b.getNumberOfColumns() || a.getNumberOfRows() != b.getNumberOfRows()) {
            System.err.println("Exception caught: " + IllegalArgumentException.class + " Can't perform subtraction.");
            exit(1);
        }
        Matrix result = new Matrix(a.getNumberOfRows(), a.getNumberOfColumns());
        for (int i = 0; i < a.getNumberOfRows(); i++) {
            for (int j = 0; j < a.getNumberOfColumns(); j++) {
                result.setElement(i, j, a.getElement(i, j) - b.getElement(i, j));
            }
        }
        return result;
    }

    public Matrix multiplication(Matrix a, Matrix b) {
        if (a.getNumberOfColumns() != b.getNumberOfRows()) {
            System.err.println("Exception caught: " + IllegalArgumentException.class + " Can't perform multiplication.");
            exit(1);
        }
        Matrix result = new Matrix(a.getNumberOfRows(), b.getNumberOfColumns());
        for (int i = 0; i < a.getNumberOfRows(); i++) {
            for (int j = 0; j < b.getNumberOfColumns(); j++) {
                int elem = 0;
                for (int k = 0; k < a.getNumberOfColumns(); k++) {
                    elem += a.getElement(i, k) * b.getElement(k, j);
                }
                result.setElement(i, j, elem);
            }
        }
        return result;
    }

    public Matrix calculateExpression(Map<Character, Matrix> matrixMap, String expression) {
        char[] tokens = convertToRPN(expression).toCharArray();
        Deque<Matrix> deque = new ArrayDeque<>();
        for (char ch : tokens) {
            if (getPriority(ch) == 0) {
                deque.push(matrixMap.get(ch));
            }
            if (getPriority(ch) > 1) {
                Matrix a = deque.pop();
                Matrix b = deque.pop();
                if (ch == '+') {
                    deque.push(addition(b, a));
                }
                if (ch == '-') {
                    deque.push(subtraction(b, a));
                }
                if (ch == '*') {
                    deque.push(multiplication(b, a));
                }
            }
        }
        return deque.pop();
    }

    public String convertToRPN(String expression) {
        expression = expression.trim().replaceAll("\\s+", "");
        char[] tokens = expression.toCharArray();
        StringBuilder current = new StringBuilder();
        Deque<Character> deque = new ArrayDeque<>();
        for (char ch : tokens) {
            int priority = getPriority(ch);
            if (priority == 0)
                current.append(ch);
            if (priority == 1)
                deque.push(ch);
            if (priority > 1) {
                while (!deque.isEmpty()) {
                    if (getPriority(deque.peek()) >= priority) {
                        current.append(deque.pop());
                    } else {
                        break;
                    }
                }
                deque.push(ch);
            }
            if (priority == -1) {
                while (getPriority(deque.peek()) != 1) {
                    current.append(deque.pop());
                }
                deque.pop();
            }
        }
        while (!deque.isEmpty()) {
            current.append(deque.pop());
        }
        return current.toString();
    }

    private int getPriority(char token) {
        if (token == '*')
            return 3;
        if (token == '+' || token == '-')
            return 2;
        if (token == '(')
            return 1;
        if (token == ')')
            return -1;
        return 0;
    }
}