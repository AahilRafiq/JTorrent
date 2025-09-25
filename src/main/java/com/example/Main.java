package com.example;

import com.example.bencode.BencodeDecoder;

public class Main {
    public static void main(String []args) {
        System.out.println(BencodeDecoder.decodeString(0, "11:hello world").first);
        System.out.println(BencodeDecoder.decodeInt(0, "i234e").first);
    }
}
