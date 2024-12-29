package br.com.nemi.util;

import cool.graph.cuid.Cuid;

public class TokenGenerator {

    public static String generateCUID() {
        return Cuid.createCuid();
    }

}
