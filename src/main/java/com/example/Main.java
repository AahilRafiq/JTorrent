package com.example;

import com.example.dto.TorrentDTO;
import com.example.helpers.RandomString;
import com.example.helpers.TorrentParser;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String []args) throws IOException {
        var bytes = Files.readAllBytes(Path.of("/home/aahilrafiq/Downloads/suse.torrent"));
        TorrentDTO torrent = TorrentParser.parseTorrent(bytes);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(40, TimeUnit.SECONDS)
                .addNetworkInterceptor(new PortInterceptor())
                .build();

        Optional<URI> uri = torrent.getAnnounceList().stream()
                .map(announce -> {
                    try {
                        return new URI(announce);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .filter(announce -> announce.getScheme().equals("http") || announce.getScheme().equals("https"))
                .findFirst();

        if (uri.isEmpty()) {
            throw new IOException("No HTTP(S) Tracker found");
        }

        int port = uri.get().getScheme().equals("http") ? 80 : 443;
        if(uri.get().getPort() != -1) port = uri.get().getPort();
        String peerId = RandomString.getAlphaNumericString(20);
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(uri.get().getScheme())
                .host(uri.get().getHost())
                .port(port)
                .addPathSegment(uri.get().getPath())
                .addQueryParameter("info_hash", torrent.getInfoHashUrlEncoded())
                .addQueryParameter("downloaded", "0")
                .addQueryParameter("uploaded", "0")
                .addQueryParameter("left", torrent.getLength().toString())
                .addQueryParameter("peer_id", peerId)
                .build();

        Request req = new Request.Builder()
                .url(httpUrl)
                .header("Connection", "close")
                .get()
                .build();

        try (Response response = client.newCall(req).execute()) {
//            System.out.println(response.body().string());
            System.out.println(response.isSuccessful());
        }  catch (IOException e) {
            e.printStackTrace();
        }
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
