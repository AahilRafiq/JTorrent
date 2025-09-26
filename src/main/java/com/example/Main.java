package com.example;

import com.example.bencode.BencodeDecoder;
import com.example.helpers.TorrentParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String []args) throws IOException {
        var bytes = Files.readAllBytes(Path.of("/home/aahil/Downloads/debian.torrent"));

        var torrent = TorrentParser.parseTorrent(bytes);
        System.out.println(BencodeDecoder.decodeLong(0, "i234e".getBytes(StandardCharsets.UTF_8)).first);
    }
}
