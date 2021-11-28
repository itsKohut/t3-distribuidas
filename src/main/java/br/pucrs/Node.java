package br.pucrs;

import br.pucrs.files.FileReader;

import java.net.UnknownHostException;
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
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSSS");
    public static final String SPACE_REGEX = " ";

    // atributos do nodo
    public String id;
    public String host;
    public Integer port;
    public LocalTime time;
    public Long delay;

    // coleção de nodos hosts
    public static ConcurrentMap<String, Node> connections = new ConcurrentHashMap<>();

    // construtor para o nodo atual
    public Node(final String fileName, final String indexNode) {
        final String[] content = findLineContentForIndexNode(fileName, indexNode);

        this.id = content[ID];
        this.host = content[HOST];
        this.port = Integer.valueOf(content[PORT]);
        this.time = LocalTime.parse(content[TIME], formatter);
        this.delay = Long.valueOf(content[DELAY]);

        System.out.println("MASTER");
        System.out.println(this);
        System.out.println("HOSTS");
        populateHosts(fileName, indexNode);
    }

    // construtor para os nodos hosts
    public Node(final String[] content) {
        this.id = content[ID];
        this.host = content[HOST];
        this.port = Integer.valueOf(content[PORT]);
        this.time = LocalTime.parse(content[TIME], formatter);
        this.delay = Long.valueOf(content[DELAY]);

        System.out.println(this);
    }

    public void execute() {
        try {
            new SocketService(this).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // método responsavel por popular o nodo atual
    private String[] findLineContentForIndexNode(final String fileName, final String indexNode) {
        final List<String> lines = FileReader.readFile(fileName);

        return lines.stream()
                .map(line -> line.split(SPACE_REGEX))
                .filter(lineContent -> lineContent[ID].equals(indexNode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format("Falha ao instanciar o nodo com index '%s' com arquivo de nome '%s'.", indexNode, fileName)));
    }


    // método responsavel por popular a coleção de nodos que são hosts, não incluindo o nodo atual
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

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", time='" + time + '\'' +
                ", delay='" + delay;
    }
}
