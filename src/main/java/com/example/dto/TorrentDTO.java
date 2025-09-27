package com.example.dto;

// Note: this is only for a single file torrent
public class TorrentDTO {
    private byte[] infoHash;
    private String announce;
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
}

