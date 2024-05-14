package com.example.soupturboflex;

public class Server {
    private String name;
    private String host;
    private int port;
    private String protocol;

    // Constructeur, getters et setters
    public Server(String name, String host, int port, String protocol) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public String toString() {
        return "Server{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", protocol='" + protocol + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
// Getters et setters
    // ...
}