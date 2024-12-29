package br.com.nemi.domain.user.exception;

import br.com.nemi.util.exception.ConflictException;

public class PhoneAlreadyInUseException extends ConflictException {
    public PhoneAlreadyInUseException(String message) {
        super(message);
    }
}
