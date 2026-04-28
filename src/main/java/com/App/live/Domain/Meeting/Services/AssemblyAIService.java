package com.App.live.Domain.Meeting.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssemblyAIService {

    @Value("${assemblyai.api.key:34635686eaa548e58de03b116ed22e31}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String transcribeAudio(MultipartFile audioFile) {
        try {
            log.info("Step 1: Uploading audio to AssemblyAI...");
            String uploadUrl = uploadFileToAssemblyAI(audioFile);
            
            log.info("Step 2: Submitting transcription job to AssemblyAI...");
            String transcriptId = submitTranscriptionJob(uploadUrl);
            
            log.info("Step 3: Waiting for transcription to complete...");
            return pollForTranscription(transcriptId);
            
        } catch (Exception e) {
            log.error("Failed to transcribe audio via AssemblyAI", e);
            return "Transcription Failed: " + e.getMessage();
        }
    }

    private String uploadFileToAssemblyAI(MultipartFile file) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.assemblyai.com/v2/upload",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());
        return root.path("upload_url").asText();
    }

    private String submitTranscriptionJob(String audioUrl) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("audio_url", audioUrl);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.assemblyai.com/v2/transcript",
                requestEntity,
                String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());
        return root.path("id").asText();
    }

    private String pollForTranscription(String transcriptId) throws Exception {
        String pollingEndpoint = "https://api.assemblyai.com/v2/transcript/" + transcriptId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        while (true) {
            ResponseEntity<String> response = restTemplate.exchange(
                    pollingEndpoint,
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            JsonNode root = objectMapper.readTree(response.getBody());
            String status = root.path("status").asText();

            if ("completed".equalsIgnoreCase(status)) {
                return root.path("text").asText();
            } else if ("error".equalsIgnoreCase(status)) {
                throw new RuntimeException("AssemblyAI returned error status.");
            }

            log.info("AssemblyAI status is '{}'. Waiting 3 seconds...", status);
            Thread.sleep(3000); // Wait 3 seconds before polling again
        }
    }
}