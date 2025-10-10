package com.example;

import com.example.dto.TorrentDTO;
import com.example.helpers.TorrentParser;

import java.io.*;
import java.net.*;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String []args) throws IOException, URISyntaxException {
        var bytes = Files.readAllBytes(Path.of("/home/aahilrafiq/Downloads/ubuntu.torrent"));
        TorrentDTO torrent = TorrentParser.parseTorrent(bytes);

        String byteAsString = new String(torrent.getInfoHash(), StandardCharsets.UTF_8);
        String result = URLEncoder.encode(new String(torrent.getInfoHash(), StandardCharsets.US_ASCII), StandardCharsets.UTF_8);

        StringBuilder infoHashEncoded = new StringBuilder();
        for(byte b: torrent.getInfoHash())  {
            if((b >= '0' && b <= '9') || (b >= 'a' && b <= 'z') || (b >= 'A' && b <= 'Z') || b == '.' || b == '-' || b == '_' || b == '~') {
                infoHashEncoded.append((char)b);
            } else {
                String hexString = String.format("%02x", b);
                infoHashEncoded.append("%");
                infoHashEncoded.append(hexString);
            }
        }
        System.out.println(infoHashEncoded.toString());

//        byte[] data = {'x12',x34,\x56,\x78,\x9a,\xbc,\xde,\xf1,\x23,\x45,\x67,\x89,\xab,\xcd,\xef,\x12,\x34,\x56,\x78\,x9a};

        byte[] data = torrent.getInfoHash();
        StringBuilder url = new StringBuilder("/announce");
        url.append("?this=that");
        addQueryParam(url, "info_hash", infoHashEncoded.toString());
        addQueryParam(url, "peer_id", "72847501847302749572");
        addQueryParam(url, "uploaded", "0");
        addQueryParam(url, "downloaded", "0");
        addQueryParam(url, "left", torrent.getLength().toString());
//        addQueryParam(url, "compact", "1");
//        addQueryParam(url, "event", "started");
//        addQueryParam(url, "no_peer_id", "1");
//        addQueryParam(url, "redundant", "0");
//        addQueryParam(url, "right", torrent.);
        System.out.println(torrent.getAnnounce().substring(7));
        System.out.println(url.toString());
        URL announceUrl = new URI(torrent.getAnnounce()).toURL();
        System.out.println(announceUrl.getHost());

        try  (Socket socket = new Socket(announceUrl.getHost(), 80)) {

            addQueryParam(url, "port", String.valueOf(socket.getLocalPort()));

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String sb = "GET " + url + " HTTP/1.1\r\n" +
                    "Host: " + announceUrl.getHost() + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

//            String message = """
//                    GET / HTTP/1.1\r
//                    Host: www.google.com\r
//                    Connection: close\r
//                    Accept: */*\r
//                    \r
//                    """;

            writer.write(sb);
            writer.flush();
            System.out.println("Message sent");

            String res = reader.readAllAsString();
            System.out.println(res);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void addQueryParam(StringBuilder builder, String key, String value) {
        builder.append("&").append(key).append("=").append(value);
    }
}
