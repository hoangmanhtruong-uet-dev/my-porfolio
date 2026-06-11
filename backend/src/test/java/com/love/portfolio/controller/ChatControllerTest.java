package com.love.portfolio.controller;

import com.love.portfolio.model.ChatMessage;
import com.love.portfolio.model.ChatUser;
import com.love.portfolio.repository.ChatMessageRepository;
import com.love.portfolio.repository.ChatUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageRepository chatMessageRepository;

    @MockBean
    private ChatUserRepository chatUserRepository;

    private ChatMessage testMessage;
    private ChatUser testUser;

    @BeforeEach
    void setUp() {
        testMessage = new ChatMessage();
        testMessage.setId(1L);
        testMessage.setSender("test_user");
        testMessage.setText("Hello World");

        testUser = new ChatUser();
        testUser.setId(1L);
        testUser.setRole("male");
        testUser.setPassword("hashedPassword");
    }

    @Test
    void testAuthenticateUserWithValidCredentials() throws Exception {
        when(chatUserRepository.findByRole("male")).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/chat/users/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"male\",\"password\":\"hashedPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testAuthenticateUserWithInvalidRole() throws Exception {
        mockMvc.perform(post("/api/chat/users/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"role\":\"invalid\",\"password\":\"password\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testSendMessage() throws Exception {
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(testMessage);

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sender\":\"test_user\",\"text\":\"Hello World\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUserStatus() throws Exception {
        when(chatUserRepository.findByRole("male")).thenReturn(Optional.of(testUser));
        when(chatUserRepository.findByRole("female")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/chat/users/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.male").value(true))
                .andExpect(jsonPath("$.female").value(false));
    }
}