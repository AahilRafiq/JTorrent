package com.example.bencode;

import com.example.helpers.Pair;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        System.out.println(toUtf8String(bytes));

        return new Pair<>(toUtf8String(bytes), index);
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
            } else if(input[index] == 'd'){
                Pair<Map<String, Object>, Integer> pair3 = decodeDictionary(index, input);
                index = pair3.second;
                map.put(pair1.first, pair3.first);
            } else {
                Pair<String,Integer> pair2 = decodeString(index, input);
                index = pair2.second;
                map.put(pair1.first, pair2.first);
            }
        }

        index++;
        return new Pair<>(map, index);
    }

    private static String toUtf8String(List<Byte> byteList) {
        byte[] bytes = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            bytes[i] = byteList.get(i);
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
