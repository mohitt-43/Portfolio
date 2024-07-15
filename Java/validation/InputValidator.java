package validation;

public class InputValidator {

    // Validate that the input contains only alphabets and whitespaces
    public static void validateAlphabetOnly(String input) throws AlphabetOnlyException {
        input = input.trim(); 
        if (!input.matches("[a-zA-Z\\s]+")) {
            throw new AlphabetOnlyException("Input contains non-alphabetic characters.");
        }
    }

    // Validate that the input contains only numbers
    public static void validateNumberOnly(String input) throws NumberOnlyException {
        input = input.trim(); 
        if (!input.matches("\\d+")) {
            throw new NumberOnlyException("Input contains non-numeric characters.");
        }
    }

    // Validate that the input contains only alphabets, numbers, whitespaces, '-', and '_'
    public static void validateAlphanumeric(String input) throws AlphanumericException {
        input = input.trim(); 
        if (!input.matches("[a-zA-Z0-9\\s-_]+")) {
            throw new AlphanumericException("Input contains invalid characters.");
        }
    }
}
