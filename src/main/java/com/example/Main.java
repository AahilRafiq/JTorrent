package com.example;

import com.example.dto.TorrentDTO;
import com.example.helpers.TorrentParser;

import com.example.tracker.HttpTracker;
import okhttp3.*;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String []args) throws IOException {
        var bytes = Files.readAllBytes(Path.of("/home/aahilrafiq/Downloads/suse.torrent"));
        TorrentDTO torrent = TorrentParser.parseTorrent(bytes);
        var peersMap = HttpTracker.getPeerList(torrent);
        System.out.println("Peers: " + peersMap.size());
    }
}

