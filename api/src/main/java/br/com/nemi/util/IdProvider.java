package br.com.nemi.util;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import cool.graph.cuid.Cuid;

public class IdProvider {

    private static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String generateCUID() {
        return Cuid.createCuid();
    }

    public static String generateNanoId(int size) {
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                IdProvider.ALPHABET,
                size
        );
    }

}
