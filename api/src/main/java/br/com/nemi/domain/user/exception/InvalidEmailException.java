package br.com.nemi.domain.user.exception;

import br.com.nemi.util.exception.BadRequestException;

public class InvalidEmailException extends BadRequestException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
