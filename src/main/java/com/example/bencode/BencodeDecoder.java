package com.example.bencode;

import com.example.helpers.Pair;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class BencodeDecoder {

    public static Pair<Long,Integer> decodeLong(int index, byte[] input) {
        List<Byte> bytes = new ArrayList<>();

        index++;
        while (index < input.length && input[index] != 'e') {
            bytes.add(input[index]);
            index++;
        }
        index++;

        return new Pair<>(Long.parseLong(toUtf8String(bytes)), index);
    }

    public static Pair<String,Integer> decodeString(int index,  byte[] input) {
        Pair<byte[], Integer> pair = decodeStringAsBytes(index, input);

        return new Pair<>(new String(pair.first, StandardCharsets.UTF_8), pair.second);
    }

    public static Pair<byte[],Integer> decodeStringAsBytes(int index,  byte[] input) {
        List<Byte> bytes = new ArrayList<>();

        int colonIdx = index;
        while (index < input.length && input[colonIdx] != ':') {
            bytes.add(input[colonIdx]);
            colonIdx++;
        }

        int length = Integer.parseInt(toUtf8String(bytes));
        index = colonIdx+1;
        bytes.clear();

        while(length-- > 0) {
            bytes.add(input[index]);
            index++;
        }

        return new Pair<>(getBytesFromList(bytes), index);
    }

    public static Pair<List<Object>, Integer> decodeList(int index, byte[] input) {
        List<Object> list = new ArrayList<>();
        index++;

        while(index < input.length && input[index] != 'e') {
            if(input[index] == 'i') {
                Pair<Long,Integer> pair = decodeLong(index, input);
                list.add(pair.first);
                index = pair.second;
            } else if(input[index] == 'd') {
                Pair<Map<String,Object>, Integer> pair = decodeDictionary(index, input);
                list.add(pair.first);
                index =  pair.second;
            } else if(input[index] == 'l') {
                Pair<List<Object>,Integer> pair = decodeList(index, input);
                list.add(pair.first);
                index =  pair.second;
            } else {
                Pair<byte[],Integer> pair = decodeStringAsBytes(index, input);
                list.add(pair.first);
                index =  pair.second;
            }
        }

        index++;
        return new Pair<>(list, index);
    }

    public static Pair<Map<String,Object>,Integer> decodeDictionary(int index, byte[] input) {
        Map<String,Object> map = new HashMap<>();
        index++;

        while(index < input.length && input[index] != 'e') {
            Pair<String,Integer> pair1 = decodeString(index, input);
            index = pair1.second;

            if(input[index] == 'i') {
                Pair<Long, Integer> pair2 = decodeLong(index, input);
                index = pair2.second;
                map.put(pair1.first, pair2.first);
            } else if(input[index] == 'd') {
                Pair<Map<String, Object>, Integer> pair3 = decodeDictionary(index, input);
                index = pair3.second;
                map.put(pair1.first, pair3.first);
            } else if(input[index] == 'l') {
                Pair<List<Object>,Integer> pair4 = decodeList(index, input);
                index = pair4.second;
                map.put(pair1.first, pair4.first);
            } else {
                Pair<byte[],Integer> pair4 = decodeStringAsBytes(index, input);
                index = pair4.second;
                map.put(pair1.first, pair4.first);
            }
        }

        index++;
        return new Pair<>(map, index);
    }

    public static byte[] getInfoBytes(byte[] input) {
        int index = 1;

        while(index < input.length && input[index] != 'e') {
            Pair<String,Integer> pair1 = decodeString(index, input);
            index = pair1.second;

            if(input[index] == 'i') {
                Pair<Long, Integer> pair2 = decodeLong(index, input);
                index = pair2.second;
            } else if(input[index] == 'd') {
                if(pair1.first.equals("info")) {
                    return extractInfoBytes(index, input);
                } else {
                    Pair<Map<String, Object>, Integer> pair3 = decodeDictionary(index, input);
                    index = pair3.second;
                }
            } else if(input[index] == 'l') {
                Pair<List<Object>,Integer> pair4 = decodeList(index, input);
                index = pair4.second;
            } else {
                Pair<byte[],Integer> pair4 = decodeStringAsBytes(index, input);
                index = pair4.second;
            }
        }

        return null;
    }

    private static byte[] extractInfoBytes(int index, byte[] input) {
        List<Byte> bytes = new ArrayList<>();

        bytes.add((byte) 'd');
        index++;
        while(input[index] != 'e') {
            if (input[index] == 'i') {
                while (input[index] != 'e') {
                    bytes.add(input[index++]);
                }
                bytes.add(input[index++]);
            } else {
                Pair<byte[],Integer> pair = decodeStringAsBytes(index, input);
                for(byte b: Integer.toString(pair.first.length).getBytes()) {
                    bytes.add(b);
                }
                bytes.add((byte)':');
                for(byte b: pair.first) {
                    bytes.add(b);
                }
                index = pair.second;
            }
        }
        bytes.add((byte)'e');

        return getBytesFromList(bytes);
    }

    private static String toUtf8String(List<Byte> byteList) {
        return new String(getBytesFromList(byteList), StandardCharsets.UTF_8);
    }

    private static byte[] getBytesFromList(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        return bytes;
    }
}
