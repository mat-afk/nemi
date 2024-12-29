package br.com.nemi.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import cool.graph.cuid.Cuid;

public class TokenGenerator {

    public static String generateCUID() {
        return Cuid.createCuid();
    }

    public static String generateVerificationToken() {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                NanoIdUtils.DEFAULT_ALPHABET,
                8
        );
    }
}
