package com.example.helpers;

import com.example.bencode.BencodeDecoder;
import com.example.dto.TorrentDTO;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TorrentParser {
    public static TorrentDTO parseTorrent(byte[] fileBytes) {
        Map<String, Object> map = BencodeDecoder.decodeDictionary(0, fileBytes).first;

        TorrentDTO torrentDTO = new TorrentDTO();

        if(map.containsKey("info") && map.get("info") instanceof Map) {
            Map<String, Object> infoMap = (Map<String, Object>) map.get("info");
            torrentDTO.setName(new String((byte[]) infoMap.get("name"), StandardCharsets.UTF_8));
            torrentDTO.setLength((long) infoMap.get("length"));
            torrentDTO.setPieces((byte[])  infoMap.get("pieces"));
            torrentDTO.setPieceLength((Long) infoMap.get("piece length"));
        }

        torrentDTO.setAnnounce(new String((byte[]) map.get("announce"), StandardCharsets.UTF_8));

        return  torrentDTO;
    }
}
