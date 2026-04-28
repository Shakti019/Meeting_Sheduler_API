package com.App.live.Domain.Meeting.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/assemblyai")
@RequiredArgsConstructor
public class AssemblyAIController {

    // Store this in application.properties as assemblyai.api.key
    @Value("${assemblyai.api.key:34635686eaa548e58de03b116ed22e31}")
    private String apiKey;

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> getTemporaryToken() {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Integer> body = new HashMap<>();
        body.put("expires_in", 3600); // Token expires in 1 hour

        HttpEntity<Map<String, Integer>> request = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.assemblyai.com/v2/realtime/token", 
                request, 
                Map.class
            );
            
            String token = (String) response.getBody().get("token");
            
            Map<String, String> res = new HashMap<>();
            res.put("token", token);
            return ResponseEntity.ok(res);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}