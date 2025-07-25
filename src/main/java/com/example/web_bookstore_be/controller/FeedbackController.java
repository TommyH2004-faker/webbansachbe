package com.example.web_bookstore_be.controller;
import com.example.web_bookstore_be.dao.FeedBackRepository;
import com.example.web_bookstore_be.dao.NguoiDungRepository;
import com.example.web_bookstore_be.entity.Feedbacks;
import com.example.web_bookstore_be.entity.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    private final ObjectMapper objectMapper;
    @Autowired
    private FeedBackRepository feedBackRepository;
    @Autowired
    private NguoiDungRepository userRepository;

    public FeedbackController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PutMapping("/update-feedback/{idFeedback}")
    public ResponseEntity<?> update(@PathVariable int idFeedback) {
        Optional<Feedbacks> feedback = feedBackRepository.findById(idFeedback);
        if (feedback.isPresent()) {
            feedback.get().setReaded(true);
            feedBackRepository.save(feedback.get());
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Thành công");
    }

    @PostMapping("/add-feedback")
    public ResponseEntity<?> add(@RequestBody JsonNode jsonData) {
        try {
            User user = userRepository.findByUsername(formatStringByJson(String.valueOf(jsonData.get("user"))));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Instant instant = Instant.from(formatter.parse(formatStringByJson(String.valueOf(jsonData.get("dateCreated")))));
            java.sql.Date dateCreated = new java.sql.Date(Date.from(instant).getTime());

            Feedbacks feedbacks = Feedbacks.builder()
                    .title(formatStringByJson(String.valueOf(jsonData.get("title"))))
                    .comment(formatStringByJson(String.valueOf(jsonData.get("comment"))))
                    .isReaded(false)
                    .dateCreated(dateCreated)
                    .user(user).build();

            feedBackRepository.save(feedbacks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok("Thành công");
    }
@GetMapping("/totalFeedbacks")
    public ResponseEntity<?> totalFeedbacks(){
        return ResponseEntity.ok(feedBackRepository.count());
    }
    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
