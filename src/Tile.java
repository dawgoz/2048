import java.awt.*;

class Tile {
    private int power;

    Tile(boolean startsAsFour) {
        power = startsAsFour ? 2 : 1;
    }

    void increasePow() {
        power++;
    }

    int getPower() {
        return power;
    }

    int getValue() {
        return (int) Math.pow(2, power);
    }

    @Override
    public String toString() {
        return String.valueOf(this.getValue());
    }

    Color getColor() {
        return switch (power) {
            case 1 -> new Color(220, 214, 214);
            case 2 -> new Color(239, 225, 199);
            case 3 -> new Color(246, 181, 125);
            case 4 -> new Color(248, 147, 95);
            case 5 -> new Color(245, 114, 86);
            case 6 -> new Color(248, 81, 43);
            case 7 -> new Color(243, 212, 121);
            case 8 -> new Color(243, 210, 102);
            case 9 -> new Color(234, 198, 79);
            case 10 -> new Color(232, 193, 62);
            case 11 -> new Color(255, 203, 28);
            default ->
                    new Color(32, 32, 32);
        };

    }
}