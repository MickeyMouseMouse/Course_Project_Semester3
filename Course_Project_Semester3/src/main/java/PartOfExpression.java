public class PartOfExpression implements Comparable {
    private byte numberOfSigns = 1; // 1 = default, 2 = +/-, 3 = +/-/*
    private byte sign; // -1 = no sign, 0 = +, 1 = -, 2 = *
    private int value; // >= 0 => number, -2 = opening bracket, -1 = closing bracket

    public PartOfExpression(byte sign, int value) {
        this.sign = sign;
        this.value = value;
    }

    public PartOfExpression(PartOfExpression part) {
        numberOfSigns = part.numberOfSigns;
        sign = part.sign;
        value = part.value;
    }

    public void setNumberOfSigns(byte number) { numberOfSigns = number; }

    public byte getNumberOfSigns() { return numberOfSigns; }

    public void setSign(byte sign) { this.sign = sign; }

    public byte getSign() { return sign; }

    public void invertSign() { if (sign == 0) sign = 1; else sign = 0; }

    public int getValue() { return value; }

    // -1 => this < obj; 0 => this == obj; 1 => this > obj
    @Override
    public int compareTo(Object obj) {
        if (this == obj) return 0;
        if (obj == null) throw new NullPointerException();

        PartOfExpression other = (PartOfExpression) obj;
        int first, second;
        if (sign == 0) first = value; else first = -value;
        if (other.sign == 0) second = other.value; else second = -other.value;

        return Integer.compare(first, second);
    }

    // true = overflow; false = no
    public boolean multiply(PartOfExpression other) {
        long product = (long) value * (long) other.value;

        if (product <= Integer.MAX_VALUE) {
            value = (int) product;
            return false;
        }

        return true;
    }

    // true = overflow; false = no
    public boolean plus(PartOfExpression other) {
        long first;
        long second;

        if (sign == 0) first = value; else first = -value;
        if (other.sign == 0) second = other.value; else second = -other.value;

        first += second;
        if (first <= Integer.MAX_VALUE) {
            value = Math.abs((int) first);
            if (first >= 0) sign = 0; else sign = 1;
            return false;
        }

        return true;
    }
}