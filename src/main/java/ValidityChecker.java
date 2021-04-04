import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

public class ValidityChecker {
    /**
     Checks validity of a Swedish personal or organisation number (including coordinated personal numbers).
     @param number number to validate.
     @return boolean returns true if it is a valid personal or organisation number.
     */
    public boolean validate(String number) {
        // Immediately return false if any requirement is not met by throwing an exception
        try {
            ascertain(number != null, "Null input");
            // Check if number matches any valid pattern (disregarding digit validity)
            Pattern p_personal = Pattern.compile("^(\\d{6}|\\d{8})[-+]?\\d{4}$");
            Matcher personal = p_personal.matcher(number);
            ascertain(personal.matches(), "Incorrect syntax");

            boolean organisation = false;
            // A full YYYY means 12 or 13 characters
            boolean full = number.length() > 11;

            if (full) {
                if (number.startsWith("16")) {
                    // Per definition
                    organisation = true;
                }
                // Discard the unnecessary century digits: YYYYMM... --> YYMM...
                number = number.substring(2);
            }

            // Check middle digit pair (in the YYMMDD part)
            int middle_pair = Integer.parseInt(number.substring(2,4));
            if (middle_pair >= 20) {
                organisation = true;
            }

            // Numbers can contain a separator (+ or -) between the birthdate and birth numbers
            boolean has_sep = number.length() == 11 || number.length() == 13;

            // Check for coordination offset in the first day digit
            int day_1 = Character.getNumericValue(number.charAt(4));
            boolean coordination = ((day_1 >= 6) && (day_1 <=9));

            // A number can't be BOTH an organisation and a coordination number
            ascertain(!(organisation && coordination), "Both organisation and coordination");

            // An organisation number does not need to adhere date rules
            if (!organisation) {
                // Check that the birthdate is valid
                String date = number.substring(0, 6);
                if (coordination) {
                    // offset 61-91 to regular date 01-31
                    date = date.substring(0, 4) + (day_1 - 6) + date.substring(5);
                }
                // Throws exception if false
                valid_date(date);
            }

            // Validate control digit (last digit of the number)
            String clean_number = number.substring(0, number.length() - 1);
            if (has_sep) {
                // remove separator (that is now always) at index 6
                clean_number = clean_number.substring(0, 6) + clean_number.substring(7);
            }
            String control_digit = luhns_algorithm(clean_number);
            String last_digit = number.substring(number.length() - 1);

            ascertain(control_digit.equals(last_digit), "Control digit does not match");

            return true;
        } catch (AssertionError e) {
            return false;
        }

    }

    /**
     * Checks if a date is valid by trying to parse it as one.
     * @param date to validate, no poor rhyme intended.
     */
    private void valid_date(String date) {
        DateTimeFormatter format;
        if (date.length() == 8) {
            //YYYYMMDD
            format = DateTimeFormatter.ofPattern("uuuuMMdd");
        } else {
            //YYMMDD
            format = DateTimeFormatter.ofPattern("uuMMdd");
        }
        try {
            // Strict ResolverStyle invalidates any incorrect date, e.g. non-leap year 02/29
            LocalDate.parse(date, format.withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException e) {
            throw new AssertionError(e);
//            e.printStackTrace();
        }
    }

    /**
     * Throws exception if the input is false.
     * @param bool statement to evaluate.
     */
    private void ascertain(boolean bool, String error_message) {
        if (!bool) {
//            System.out.println(error_message);
            throw new AssertionError();
        }
    }
    private int digit_sum(int number) {
        int num = number;
        int sum = 0;
        while (num > 0) {
            sum = sum + num % 10;
            num = num / 10;
        }

        return sum;
    }

    /**
     * Luhn's algorithm:
     * Uses digit sum and modulo to calculate a single control digit given a sequence of digits.
     * n u m b e r ...
     * * * * * * * (multiplication)
     * 2 1 2 1 2 1 ...
     * =
     * 2n + u + 2m + b + 2e + r ...
     * Apply digit sum where individual results (2n etc.) overflow a single digit (i.e. >9).
     * Digit_sum(29) --> 2+9 = 11 --> 1+1 = 2.
     * Final step includes modulo.
     */
    private String luhns_algorithm(String number) {
        int control_digit = 0;
        char[] digits = number.toCharArray();

        for (int i = 0; i < digits.length; i++) {
            int digit = Character.getNumericValue(digits[i]);
            int addition = 0;
            if ((i % 2) == 0) {
                addition += digit * 2;
            } else {
                addition += digit; // * 1
            }
            control_digit += digit_sum(addition);
        }
        control_digit = (10 - (control_digit % 10)) % 10;

        return Integer.toString(control_digit);
    }

    /**
     * For testing during the development of this program. No purpose in final product.
     */
    public static void main(String[] args) {
        ValidityChecker validityChecker = new ValidityChecker();
        String[] numbers = new String[]{"201701102384", "141206-2380", "20080903-2386", "7101169295", "198107249289",
                "19021214-9819", "190910199827", "191006089807", "192109099180", "4607137454", "194510168885",
                "900118+9811", "189102279800", "189912299816", "201701272394", "190302299813", "190910799824",
                "556614-3185", "16556601-6399", "262000-1111", "857202-7566"};
        for (String number : numbers) {
            boolean valid = validityChecker.validate(number);
            System.out.println(number + " " + valid+"\n");
        }
    }
}