package com.example.bencode;

import com.example.helpers.Pair;

public class BencodeDecoder {
    public static Pair<Integer,Integer> decodeInt(int index, String input) {
        StringBuilder sb = new StringBuilder();

        index++;
        while (input.charAt(index) != 'e') {
            sb.append(input.charAt(index));
            index++;
        }
        index++;

        return new Pair<>(Integer.parseInt(sb.toString()), index);
    }

    public static Pair<String,Integer> decodeString(int index, String input) {
        StringBuilder sb = new StringBuilder();

        int colonIdx = index;
        while (input.charAt(colonIdx) != ':') {
            sb.append(input.charAt(colonIdx));
            colonIdx++;
        }

        int length = Integer.parseInt(sb.toString());
        index = colonIdx+1;
        sb.setLength(0);
        while(length-- > 0) {
            sb.append(input.charAt(index));
            index++;
        }

        return new Pair<>(sb.toString(), index);
    }
}
