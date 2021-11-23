package com.trabalho;

public class Main {

    public static final String fileNameDefault = "config";

    public static void main(String[] args) {

        // verifica os argumentos passados, podendo ser 1 para o index apenas do nodo
        // e dois para o index e o nome do arquivo caso queira passar um outro arquivo
        if (args.length < 1 || args.length > 2) {
            System.out.println("Usage: java Node <file name> <node index>");
            System.exit(1);
        }

        // dependendo dos parametros passados, define os valores que serão utilizados para instanciar um nodo
        String fileName = args.length == 1 ? fileNameDefault : args[0];
        String nodeIndex = args.length == 1 ? args[0] : args[1];

        // execucao java Main <nodeIndex> ou java Main <fileName> <nodeIndex>
        Node node = new Node(fileName, nodeIndex);
        node.execute();
    }
}
