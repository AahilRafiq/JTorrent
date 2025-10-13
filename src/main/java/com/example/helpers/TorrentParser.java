package com.example.helpers;

import com.example.bencode.BencodeParser;
import com.example.dto.TorrentDTO;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public class TorrentParser {
    public static TorrentDTO parseTorrent(byte[] fileBytes) {
        var parser =  new BencodeParser();
        Map<String, Object> map = parser.getDecodedMap(fileBytes);

        TorrentDTO torrentDTO = new TorrentDTO();
        byte[] infoBytes = parser.getInfoBytes(fileBytes);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] mdBytes = md.digest(infoBytes);
            torrentDTO.setInfoHash(mdBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        if(map.containsKey("announce-list") && map.get("announce-list") instanceof List<?> tierList) {
            for(Object item: tierList) {
                if(item instanceof List<?> list) {
                    for(Object url: list) {
                        if(url instanceof byte[]) {
                            System.out.println(new String((byte[]) url, StandardCharsets.UTF_8));
                        }
                    }
                }
            }
        }

        if(map.containsKey("info") && map.get("info") instanceof Map<?, ?> infoMap) {
            torrentDTO.setName(new String((byte[]) infoMap.get("name"), StandardCharsets.UTF_8));
            torrentDTO.setLength((long) infoMap.get("length"));
            torrentDTO.setPieces(((byte[])  infoMap.get("pieces")));
            torrentDTO.setPieceLength((Long) infoMap.get("piece length"));
        }

        torrentDTO.setAnnounce(new String((byte[]) map.get("announce"), StandardCharsets.UTF_8));

        return  torrentDTO;
    }
}
