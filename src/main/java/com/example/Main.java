package com.example;

import com.example.bencode.BencodeDecoder;
import com.example.helpers.TorrentParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String []args) throws IOException {
        var bytes = Files.readAllBytes(Path.of("/home/aahil/Downloads/sample.torrent"));
//        var bytes = "d4:infod6:lengthi12345e4:name10:myfile.txt6:pieces40:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX12:piece lengthi16384ee8:announce29:http://tracker.com/announcee\n".getBytes(StandardCharsets.UTF_8);

        var torrent = TorrentParser.parseTorrent(bytes);
    }
}
