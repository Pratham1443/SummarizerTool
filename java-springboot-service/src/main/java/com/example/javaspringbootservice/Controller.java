package com.example.javaspringbootservice;

import org.example.DatabaseConfig; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import scala.collection.Seq;
import scala.collection.JavaConverters;
import scala.Tuple2;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
public class Controller {
    @CrossOrigin(origins = "http://127.0.0.1:3000")
    @PostMapping("/summarize")
    public Map<String, String> summarize(@RequestBody Map<String, String> requestBody) {
        System.out.println("Received request body: " + requestBody);
        String url = requestBody.get("url");
        String summary = DatabaseConfig.summarizeUrl(url); 
        Map<String, String> response = new HashMap<>();
        response.put("summary", summary);
        return response;
    }

    @CrossOrigin(origins = "http://127.0.0.1:3000")
    @GetMapping("/history")
    public List<Map<String, String>> getHistory() {
        Seq<Tuple2<String, String>> scalaSeq = DatabaseConfig.getHistory();
        List<Map<String, String>> javaList = new ArrayList<>();
        for (Tuple2<String, String> tuple : JavaConverters.seqAsJavaList(scalaSeq)) {
            Map<String, String> entry = new HashMap<>();
            entry.put("url", tuple._1);
            entry.put("summary", tuple._2);
            javaList.add(entry);
        }
        return javaList;
    }
}


