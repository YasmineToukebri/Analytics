package com.example.Analytics.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
public class JoinRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String userName;
    private UUID roomId;
    private LocalDateTime joinedAt;
}
