package com.chatroom.app.dto.message;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageResponseDTO {

    private String senderName;
    private String message;
    private LocalDateTime timestamp;
}