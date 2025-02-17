package com.chat.backend.controllers;

import com.chat.backend.dto.FileRequest;
import com.chat.backend.dto.requests.MessageReq;
import com.chat.backend.entities.Message;
import com.chat.backend.services.ActiveUserService;
import com.chat.backend.services.MessageService;
import org.springframework.core.io.Resource;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate brokerMessagingTemplate;
    private final ActiveUserService activeUserService;

    public MessageController(MessageService messageService, SimpMessagingTemplate brokerMessagingTemplate, ActiveUserService activeUserService) {
        this.messageService = messageService;
        this.brokerMessagingTemplate = brokerMessagingTemplate;
        this.activeUserService = activeUserService;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getMessages(@PathVariable String token){
        return new ResponseEntity<>( messageService.loadMessagesWithUser(token), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".") + 1) : "";

            String uniqueFileName = System.currentTimeMillis() + "." + extension;

            Path path = Paths.get("uploads/" + uniqueFileName);

            Files.createDirectories(path.getParent());

            Files.write(path, file.getBytes());

            Map<String, Object> response = new HashMap<>();
            response.put("file_url", uniqueFileName);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        System.out.println("-----------------file --------------" + filename);
        File file = new File("./uploads/" + filename); // Adjust the path to match your static location
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(file);

        String contentType = null;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }



}
