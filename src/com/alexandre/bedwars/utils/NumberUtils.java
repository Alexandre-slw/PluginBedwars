package com.alexandre.bedwars.utils;

public class NumberUtils {

    public static String toRomainNumber(int number) {
        switch (number) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            case 40:
                return "XL";
            case 50:
                return "L";
            case 90:
                return "XC";
            case 100:
                return "C";
            case 400:
                return "CD";
            case 500:
                return "D";
            case 900:
                return "CM";
            case 1000:
                return "M";
        }

        StringBuilder result = new StringBuilder();
        while (number > 1000) {
            result.append(toRomainNumber(1000));
            number -= 1000;
        }
        number = append(number, 1000, result);
        number = append(number, 900, result);
        number = append(number, 500, result);
        number = append(number, 400, result);
        number = append(number, 100, result);
        number = append(number, 90, result);
        number = append(number, 50, result);
        number = append(number, 40, result);
        number = append(number, 10, result);

        if (number > 0) {
            result.append(toRomainNumber(number));
        }

        return result.toString();
    }

    private static int append(int number, int step, StringBuilder builder) {
        while (number > step) {
            builder.append(toRomainNumber(step));
            number -= step;
        }
        return number;
    }

}
