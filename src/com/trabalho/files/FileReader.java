package com.trabalho.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaria para leitura do arquivo txt
 */
public final class FileReader {

    public static List<String> readFile(final String fileName) {

        List<String> lines = new ArrayList<>();

        try {
            lines = Files.readAllLines(Paths.get(String.format("src/com/trabalho/files/%s.txt", fileName)));
        } catch (IOException e) {
            System.out.println("Ocorreu um erro na leitura de arquivo.");
            System.exit(1);
        }

        return lines;
    }
}
