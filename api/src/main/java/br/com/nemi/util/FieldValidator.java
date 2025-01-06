package br.com.nemi.util;

import br.com.nemi.domain.participant.dto.PhoneNumberDTO;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.validator.routines.EmailValidator;

public class FieldValidator {

    public static boolean isNullOrBlank(String field) {
        return field == null || field.isBlank();
    }

    public static boolean isEmailValid(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isPhoneNumberValid(PhoneNumberDTO phoneNumber) {
        return PhoneNumberUtil.getInstance().isPossibleNumber(phoneNumber.number(), phoneNumber.countryCode());
    }

}
