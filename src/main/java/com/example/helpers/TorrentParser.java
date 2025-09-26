package com.example.helpers;

import com.example.bencode.BencodeDecoder;
import com.example.dto.TorrentDTO;

import java.util.Map;

public class TorrentParser {
    public static TorrentDTO parseTorrent(byte[] fileBytes) {
        Map<String, Object> map = BencodeDecoder.decodeDictionary(0, fileBytes).first;

        TorrentDTO torrentDTO = new TorrentDTO();

        if(map.containsKey("info") && map.get("info") instanceof Map) {
            Map<String, Object> infoMap = (Map<String, Object>) map.get("info");
            torrentDTO.setName((String) infoMap.get("name"));
            torrentDTO.setLength((long) infoMap.get("length"));
            torrentDTO.setPieces((byte[])  infoMap.get("pieces"));
            torrentDTO.setPieceLength((Long) infoMap.get("piece length"));
        }

        torrentDTO.setAnnounce((String) map.get("announce"));

        return  torrentDTO;
    }
}
