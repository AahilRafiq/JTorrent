package com.example.tracker;

import com.example.bencode.BencodeParser;
import com.example.dto.TorrentDTO;
import com.example.helpers.RandomString;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class HttpTracker {
    public static Map<String, Object> getPeerList(TorrentDTO torrent) {
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
            System.err.println("No HTTP(S) Tracker found");
            return null;
        }

        int port = uri.get().getScheme().equals("http") ? 80 : 443;
        if(uri.get().getPort() != -1) port = uri.get().getPort();
        String peerId = RandomString.getAlphaNumericString(20);
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(uri.get().getScheme())
                .host(uri.get().getHost())
                .port(port)
                .encodedPath(uri.get().getPath())
                .addEncodedQueryParameter("info_hash", torrent.getInfoHashUrlEncoded())
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
            BencodeParser parser = new BencodeParser();
            return parser.getDecodedMap(response.body().bytes());
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
