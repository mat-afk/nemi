package br.com.nemi.util;

public class FieldValidator {

    public static boolean isNullOrBlank(String field) {
        return field == null || field.isBlank();
    }

}
