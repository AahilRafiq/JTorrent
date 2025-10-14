package com.example.dto;

import java.util.List;

// Note: this is only for a single file torrent
public class TorrentDTO {
    private byte[] infoHash;
    private String announce;
    private List<String> announceList;
    private String name;
    private Long length;
    private Long pieceLength;
    private byte[] pieces;

    // Getters and setters
    public byte[] getInfoHash() { return infoHash; }
    public void setInfoHash(byte[] infoHash) { this.infoHash = infoHash; }

    public String getAnnounce() { return announce; }
    public void setAnnounce(String announce) { this.announce = announce; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getLength() { return length; }
    public void setLength(Long length) { this.length = length; }

    public Long getPieceLength() { return pieceLength; }
    public void setPieceLength(Long pieceLength) { this.pieceLength = pieceLength; }

    public byte[] getPieces() { return pieces; }
    public void setPieces(byte[] pieces) { this.pieces = pieces; }

    public List<String> getAnnounceList() { return announceList; };
    public void setAnnounceList(List<String> announceList) { this.announceList = announceList; }

    public String getInfoHashUrlEncoded() {
        StringBuilder infoHashEncoded = new StringBuilder();
        for (byte b : infoHash) {
            if ((b >= '0' && b <= '9') || (b >= 'a' && b <= 'z') || (b >= 'A' && b <= 'Z') || b == '.' || b == '-' || b == '_' || b == '~') {
                infoHashEncoded.append((char) b);
            } else {
                String hexString = String.format("%02x", b);
                infoHashEncoded.append("%");
                infoHashEncoded.append(hexString);
            }
        }

        return infoHashEncoded.toString();
    }
}

