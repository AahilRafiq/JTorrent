package com.example;

import com.example.bencode.BencodeDecoder;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String []args) {
        var map = BencodeDecoder.decodeDictionary(0, "d4:infod6:lengthi12345e4:name10:myfile.txt6:pieces40:XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX12:piece lengthi16384ee8:announce29:http://tracker.com/announcee\n".getBytes(StandardCharsets.UTF_8)).first;
        System.out.println(BencodeDecoder.decodeLong(0, "i234e".getBytes(StandardCharsets.UTF_8)).first);
    }
}
