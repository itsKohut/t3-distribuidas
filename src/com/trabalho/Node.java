package com.trabalho;

import com.trabalho.files.FileReader;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.lang.String.format;

public final class Node {

    // constantes
    public static final int ID = 0;
    public static final int HOST = 1;
    public static final int PORT = 2;
    public static final int TIME = 3;
    public static final int DELAY = 4;
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    public static final String SPACE_REGEX = " ";

    // coleção de nodos hosts
    public static ConcurrentMap<String, Node> connections = new ConcurrentHashMap<>();

    // atributos do nodo
    public String id;
    public String host;
    public Integer port;
    public volatile LocalTime time;
    public Long delay;

    // construtor para o nodo atual
    public Node(final String fileName, final String indexNode) {
        final String[] content = findLineContentForIndexNode(fileName, indexNode);

        this.id = content[ID];
        this.host = content[HOST];
        this.port = Integer.valueOf(content[PORT]);
        this.time = LocalTime.parse(content[TIME], formatter);
        this.delay = Long.valueOf(content[DELAY]);

        populateHosts(fileName, indexNode);

    }

    // construtor para os nodos hosts
    public Node(final String[] content) {
        this.id = content[ID];
        this.host = content[HOST];
        this.port = Integer.valueOf(content[PORT]);
        this.time = LocalTime.parse(content[TIME]);
        this.delay = Long.valueOf(content[DELAY]);
    }

    public void execute() {
        new SocketService(this).run();
    }

    // instancia um nodo baseado no seu index
    private String[] findLineContentForIndexNode(final String fileName, final String indexNode) {
        final List<String> lines = FileReader.readFile(fileName);

        return lines.stream()
                .map(line -> line.split(SPACE_REGEX))
                .filter(lineContent -> lineContent[ID].equals(indexNode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format("Falha ao instanciar o nodo com index '%s' com arquivo de nome '%s'.", indexNode, fileName)));
    }

    // popula todos os hosts do nodo master
    private void populateHosts(final String fileName, final String indexNode) {
        final List<String> lines = FileReader.readFile(fileName);

        final List<String[]> contentsArray = lines.stream()
                .map(line -> line.split(SPACE_REGEX))
                .filter(lineContent -> !(lineContent[ID].equals(indexNode)))
                .collect(Collectors.toList());

        for (String[] content : contentsArray) {
            final Node node = new Node(content);
            connections.put(node.id, node);
        }
    }

    // busca o tempo atual do nodo
    public synchronized LocalTime getTime() {
        return this.time;
    }

    // atualiza o tempo do nodo master
    public synchronized void adjustTime(final LocalTime localTime, final Long adjustTime) {
        final Long newNanosTime = localTime.toNanoOfDay() + adjustTime;
        final LocalTime newLocalTime = LocalTime.ofNanoOfDay(newNanosTime);

        System.out.printf("Tempo do nodo master atualizado de %s para %s\n", this.time, newLocalTime);

        this.time = newLocalTime;
    }

    // atualiza o tempo dos nodos slaves
    public synchronized void adjustTime(final Long newTime) {
        final LocalTime newLocalTime = LocalTime.ofNanoOfDay(newTime);

        System.out.printf("Tempo do nodo slave atualizado de %s para %s\n", this.time, newLocalTime);

        this.time = newLocalTime;

        driftTime();
    }

    private void driftTime() {

        // 15 para ter a change de cair no cenario de remoção de media da data
        int driftTime = (int) (Math.random() * 13);
        this.time = this.time.plusSeconds(driftTime);

        System.out.printf("Tempo recebeu um drift time de %s segundos para simulação", driftTime);
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", time='" + time + '\'' +
                ", delay='" + delay;
    }
}
