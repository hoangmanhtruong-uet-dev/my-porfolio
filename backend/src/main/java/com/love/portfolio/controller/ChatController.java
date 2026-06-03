package com.love.portfolio.controller;

import com.love.portfolio.model.ChatMessage;
import com.love.portfolio.model.ChatUser;
import com.love.portfolio.repository.ChatMessageRepository;
import com.love.portfolio.repository.ChatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatUserRepository chatUserRepository;

    @GetMapping
    public List<ChatMessage> getAllMessages() {
        return chatMessageRepository.findAll();
    }

    @PostMapping
    public ChatMessage sendMessage(@RequestBody ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    @DeleteMapping
    public void clearHistory() {
        chatMessageRepository.deleteAll();
    }

    @GetMapping("/users/status")
    public Map<String, Boolean> getUserStatus() {
        Map<String, Boolean> status = new HashMap<>();
        status.put("male", chatUserRepository.findByRole("male").isPresent());
        status.put("female", chatUserRepository.findByRole("female").isPresent());
        return status;
    }

    @PostMapping("/users/auth")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> request) {
        String role = request.get("role");
        String password = request.get("password");

        if (role == null || (!role.equals("male") && !role.equals("female"))) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Vai trò không hợp lệ!"));
        }

        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Mật mã không được để trống!"));
        }

        Optional<ChatUser> existingUser = chatUserRepository.findByRole(role);
        if (existingUser.isEmpty()) {
            ChatUser newUser = new ChatUser();
            newUser.setRole(role);
            newUser.setPassword(password);
            chatUserRepository.save(newUser);
            return ResponseEntity.ok(Map.of("success", true, "status", "registered", "message", "Ghi nhận mật mã thành công!"));
        } else {
            if (existingUser.get().getPassword().equals(password)) {
                return ResponseEntity.ok(Map.of("success", true, "status", "authenticated", "message", "Xác thực thành công!"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Mật mã không chính xác!"));
            }
        }
    }

    @PutMapping("/{id}/reaction")
    public ResponseEntity<?> updateReaction(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String reaction = request.get("reaction");
        Optional<ChatMessage> optionalMessage = chatMessageRepository.findById(id);
        if (optionalMessage.isPresent()) {
            ChatMessage message = optionalMessage.get();
            message.setReaction(reaction);
            chatMessageRepository.save(message);
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
