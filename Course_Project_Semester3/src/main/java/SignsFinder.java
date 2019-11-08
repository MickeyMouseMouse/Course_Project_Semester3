import java.util.List;
import java.util.ArrayList;

public class SignsFinder {
    private String inputString;
    private List<PartOfExpression> leftPart;
    private PartOfExpression rightPart;

    private boolean done; // use for exit from main recursion
    private boolean solution; // true = there is solution, false = there is no solution
    private boolean isCanceled; // the process may be terminated
    private String error; // remember run time error

    // true = new string has been set; false = new string is duplicate of old one
    public boolean setInputString(String inputStr) {
        StringBuilder str = new StringBuilder();

        // remove extra spaces
        boolean space = true;
        for (char ch : inputStr.toCharArray())
            if (ch == ' ') {
                if (!space) space = true;
            } else {
                if (space) {
                    str.append(" ");
                    space = false;
                }
                str.append(ch);
            }

        // insert missing spaces before/after brackets and equals
        int i = 1;
        while (i < str.length() - 1) {
            if (str.charAt(i) == '(' || str.charAt(i) == ')' || str.charAt(i) == '=') {
                if (str.charAt(i - 1) != ' ') {
                    str.insert(i, ' ');
                    i++;
                }

                if (str.charAt(i + 1) != ' ') {
                    str.insert(i + 1, ' ');
                    i++;
                }
            }

            i++;
        }

        // compare new input string with old
        String result = str.toString();
        if (result.equals(inputString))
            return false;
        else {
            inputString = result;
            return true;
        }
    }

    // use for tests in Tests.java
    public String getInputString() { return inputString; }

    public boolean isValid() {
        if (!inputString.matches("( ([0-9]+|\\(|\\)))+( = [0-9]+)$")) {
            inputString = null;
            return false;
        }

        // extra check for brackets (right number of opening and closing brackets)
        int brackets = 0;
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.charAt(i) == '(') {
                brackets++;
                if (inputString.charAt(i + 2) == ')') {
                    brackets = -1;
                    break;
                }
            }
            if (inputString.charAt(i) == ')') brackets--;
            if (brackets < 0) break;
        }

        if (brackets == 0)
            return true;
        else {
            inputString = null;
            return false;
        }
    }

    // start finding a solution
    public String solve() {
        done = false;
        solution = false;
        isCanceled = false;
        error = null;

        split();
        if (isSucceeded()) findSolution(0);
        return makeAnswer();
    }

    private void split() {
        // left part = right part
        String[] parts = inputString.split("= ");

        // value of expression (right part)
        try {
            rightPart = new PartOfExpression((byte) 0, Integer.parseInt(parts[1]));
        } catch (NumberFormatException e) {
            cancel();
            error = parts[1];
            return;
        }

        // left part
        leftPart = new ArrayList<>();
        PartOfExpression part;
        String str = parts[0];
        for (int i = 0; i < str.length() - 1; i++) {
            if (str.charAt(i + 1) == ')') {
                leftPart.add(new PartOfExpression((byte) -1, -1));
                i++;
                continue;
            }

            byte numberOfSigns;
            if (i == 0 || str.charAt(i - 1) == '(')
                numberOfSigns = 2;
            else
                numberOfSigns = 3;

            if (str.charAt(i + 1) == '(') {
                part = new PartOfExpression((byte) -1, -2);
                i++;
            } else {
                int j = i + 1;
                while (str.charAt(j) != ' ')
                    j++;

                try {
                part = new PartOfExpression((byte) -1,
                        Integer.parseInt(str.substring(i + 1, j)));
                } catch (NumberFormatException e) {
                    cancel();
                    error = str.substring(i + 1, j);
                    return;
                }
                i = j - 1;
            }

            part.setNumberOfSigns(numberOfSigns);
            leftPart.add(part);
        }
    }

    // 0 = '+'; 1 = '-'; 2 = '*'
    private void findSolution(int index) {
        for (byte i = 0; i < leftPart.get(index).getNumberOfSigns(); i++) {
            if (done) break; // exit from the recursion
            leftPart.get(index).setSign(i);

            if (index != leftPart.size() - 1)
                findSolution(index + 1);
            else
                if (getResult(0, leftPart.size()).compareTo(rightPart) == 0) {
                    done = true;
                    solution = true;
                }
        }
    }

    private PartOfExpression getResult(int start, int stop) {
        List<PartOfExpression> expression = new ArrayList<>();
        for (int i = start; i < stop; i++)
            if (leftPart.get(i).getValue() != -2) { // -2 = '('; -1 = ')'
                expression.add(new PartOfExpression(leftPart.get(i)));
            } else {
                int j = i + 1;
                int brackets = 1;
                while (true) {
                    if (leftPart.get(j).getValue() == -2) brackets++;
                    if (leftPart.get(j).getValue() == -1)
                        if (--brackets == 0) break;
                    j++;
                }

                PartOfExpression part = getResult(i + 1, j);
                if (leftPart.get(i).getSign() == 1) part.invertSign();

                if (leftPart.get(i).getSign() == 2) { // 2 = '*'
                    if (part.getSign() == 1) // 1 = '-'
                        expression.get(expression.size() - 1).invertSign();
                    part.setSign((byte) 2);
                }

                expression.add(part);
                i = j;
            }

        return calculate(expression);
    }

    private PartOfExpression calculate(List<PartOfExpression> expression) {
        PartOfExpression result = new PartOfExpression((byte) 0, 0);
        for (int i = 0; i < expression.size(); i++) {
            if (i != expression.size() - 1 && expression.get(i + 1).getSign() == 2) { // 2 = '*'
                PartOfExpression productOfNumbers = expression.get(i);
                while (i != expression.size() - 1 && expression.get(i + 1).getSign() == 2)
                    if (productOfNumbers.multiply(expression.get(i++ + 1))) {
                        cancel();
                        error = "Overflow";
                        break;
                    }

                if (done) break;

                if (result.plus(productOfNumbers)) {
                    cancel();
                    error = "Overflow";
                    break;
                }
            } else
                if (result.plus(expression.get(i))) {
                    cancel();
                    error = "Overflow";
                    break;
                }
        }

        return result;
    }

    public void cancel() {
        isCanceled = true;
        done = true;
        inputString = null;
    }

    public boolean isSucceeded() { return error == null; }

    // make the required string from ArrayList<PartOfExpression>
    private String makeAnswer() {
        if (!isSucceeded()) return error;
        if (isCanceled) return "";
        if (!solution) return "There is no solution";

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < leftPart.size(); i++) {
            if (leftPart.get(i).getValue() == -1) {
                result.append(" )");
                continue;
            }

            switch (leftPart.get(i).getSign()) {
                case 0:
                    if (i != 0 && leftPart.get(i - 1).getValue() != -2)
                        result.append("+");
                    else
                        result.append(" ");
                    break;
                case 1:
                    result.append("-");
                    break;
                case 2:
                    result.append("*");
                    break;
            }

            if (leftPart.get(i).getValue() == -2)
                result.append("(");
            else
                result.append(leftPart.get(i).getValue());
        }

        result.append(" = ").append(rightPart.getValue());
        return result.toString();
    }
}