package com.example.bencode;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class BencodeParser {
    private int index;
    private byte[] fileBytes;

    public BencodeParser() {
        this.fileBytes = null;
        this.index = 0;
    }

    public Map<String,Object> getDecodedMap(byte[] input) {
        index = 0;
        fileBytes = input;
        return decodeDictionary();
    }

    private Object decode() {
        return switch (fileBytes[index]) {
            case 'i' -> decodeLong();
            case 'd' -> decodeDictionary();
            case 'l' -> decodeList();
            default -> decodeStringAsBytes();
        };
    }

    private Map<String,Object> decodeDictionary() {
        index++;
        Map<String,Object> map = new HashMap<>();

        while(index < fileBytes.length && fileBytes[index] != 'e') {
            String key = decodeString();
            Object value = decode();
            map.put(key,value);
        }

        index++;
        return map;
    }

    private String decodeString() {
        byte[] byteString = decodeStringAsBytes();
        return new String(byteString, StandardCharsets.UTF_8);
    }

    private byte[] decodeStringAsBytes() {
        List<Byte> buffer = new ArrayList<>();

        int colonIdx = index;
        while (index < fileBytes.length && fileBytes[colonIdx] != ':') {
            buffer.add(fileBytes[colonIdx]);
            colonIdx++;
        }

        int length = Integer.parseInt(toUtf8String(buffer));
        index = colonIdx+1;
        buffer.clear();

        while(length-- > 0) {
            buffer.add(fileBytes[index]);
            index++;
        }

        return getBytesFromList(buffer);
    }

    private Long decodeLong() {
        List<Byte> buffer = new ArrayList<>();
        index++;
        while (index < fileBytes.length && fileBytes[index] != 'e') {
            buffer.add(fileBytes[index]);
            index++;
        }
        index++;
        return Long.parseLong(toUtf8String(buffer));
    }

    private List<Object> decodeList() {
        var list = new ArrayList<>();
        index++;
        while(index < fileBytes.length &&  fileBytes[index] != 'e') {
            list.add(decode());
        }
        index++;
        return list;
    }

    /******************** Extract Info Bytes *************************/
    public byte[] getInfoBytes(byte[] input) {
        fileBytes = input;
        index = 1;

        while(index < input.length && input[index] != 'e') {
            String key = decodeString();

            if(key.equals("info")) {
                return extractInfoBytes();
            } else {
                decode();
            }
        }

        return null;
    }

    private byte[] extractInfoBytes() {
        int start = index;
        decodeDictionary(); // improvement: avoid unnecessary processing here
        return Arrays.copyOfRange(fileBytes, start, index);
    }

    /******************** HELPERS *************************/
    private String toUtf8String(List<Byte> byteList) {
        return new String(getBytesFromList(byteList), StandardCharsets.UTF_8);
    }

    private byte[] getBytesFromList(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        return bytes;
    }
}
