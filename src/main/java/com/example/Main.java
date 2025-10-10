package com.example;

import com.example.dto.TorrentDTO;
import com.example.helpers.TorrentParser;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.*;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class Main {
    public static void main(String []args) throws IOException, URISyntaxException {
        var bytes = Files.readAllBytes(Path.of("/home/aahilrafiq/Downloads/debian.torrent"));
        TorrentDTO torrent = TorrentParser.parseTorrent(bytes);



        String byteAsString = new String(torrent.getInfoHash(), StandardCharsets.UTF_8);
        String result = URLEncoder.encode(new String(torrent.getInfoHash(), StandardCharsets.US_ASCII), StandardCharsets.UTF_8);

        StringBuilder infoHashEncoded = new StringBuilder();
        for (byte b : torrent.getInfoHash()) {
            if ((b >= '0' && b <= '9') || (b >= 'a' && b <= 'z') || (b >= 'A' && b <= 'Z') || b == '.' || b == '-' || b == '_' || b == '~') {
                infoHashEncoded.append((char) b);
            } else {
                String hexString = String.format("%02x", b);
                infoHashEncoded.append("%");
                infoHashEncoded.append(hexString);
            }
        }
        System.out.println(infoHashEncoded.toString());


        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new PortInterceptor())
                .build();

        URL url = new URI(torrent.getAnnounce()).toURL();
        System.out.println(url.getProtocol());


//        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(torrent.getAnnounce())).newBuilder()
//                .addQueryParameter("d","sd")
//                .build();

        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme("https")
                .host(url.getHost())
                .port(80)
                .addPathSegment("announce")
                .addQueryParameter("info_hash", new String(torrent.getInfoHash(), StandardCharsets.ISO_8859_1))
                .addQueryParameter("downloaded", "0")
                .addQueryParameter("uploaded", "0")
                .addQueryParameter("left", torrent.getLength().toString())
                .addQueryParameter("peer_id", "47309681325840096130")
                .build();

        Request req = new Request.Builder()
                .url(httpUrl)
                .get()
                .build();

        try (Response response = client.newCall(req).execute()) {
            System.out.println(response.body().string());
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addQueryParam(StringBuilder builder, String key, String value) {
        builder.append("&").append(key).append("=").append(value);
    }
}

final class PortInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        int localPort = Objects.requireNonNull(chain.connection()).socket().getLocalPort();
        Request req = chain.request();
        Request modifiedReq = req.newBuilder()
                .url(
                        req.url().newBuilder()
                        .addQueryParameter("port", String.valueOf(localPort))
                                .build()
                ).build();

        return chain.proceed(modifiedReq);
    }
}
