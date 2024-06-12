package com.example.javaspringbootservice;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import org.example.DatabaseConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import scala.collection.Seq;
import scala.collection.JavaConverters;
import scala.Tuple2;
import scala.Tuple3;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@RestController
public class Controller {
    private ObjectMapper mapper = new ObjectMapper();
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
    public List<String> getHistory() throws IOException {
        List<String> jsonStrings = JavaConverters.seqAsJavaListConverter(DatabaseConfig.getHistory()).asJava();
        System.out.println(jsonStrings);
//        List<Map<String, String>> result = new ArrayList<>();
//        System.out.println(jsonStrings);
//
//        for (String jsonString : jsonStrings) {
//            Map<String, String> map = mapper.readValue(jsonString, Map.class);
//            result.add(map);
//        }

        return jsonStrings;
    }

    @CrossOrigin(origins = "http://127.0.0.1:3000")
    @PutMapping("/history/{id}")
    public Map<String, String> updateHistory(@PathVariable Integer id, @RequestBody Map<String, String> entry) {
        // Logic to find the history item by id and update its summary
        // Save the updated item
        String summary = entry.get("summary");
        Integer done = DatabaseConfig.updateSummary(id, summary);
        Map<String, String> response = new HashMap<>();

        if(done == 1) {
            System.out.println("Updated!!!!");
            response.put("data", "Updated successfully");
        }
        else {
            System.out.println("Failed!!!!");
            response.put("data", "Update failed");
        }
        return response;
    }
}


