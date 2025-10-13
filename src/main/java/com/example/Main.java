package com.example;

import com.example.bencode.BencodeParser;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String []args) throws IOException, URISyntaxException {
        var bytes = Files.readAllBytes(Path.of("/home/aahilrafiq/Downloads/ubuntu.torrent"));
//        TorrentDTO torrent = TorrentParser.parseTorrent(bytes);
        var parser = new BencodeParser();
        var map = parser.getDecodedMap(bytes);
        var infoBytes = parser.getInfoBytes(bytes);

        System.out.println(map);


    }

    public static void addQueryParam(StringBuilder builder, String key, String value) {
        builder.append("&").append(key).append("=").append(value);
    }
}
