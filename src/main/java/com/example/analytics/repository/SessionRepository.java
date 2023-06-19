package com.example.analytics.repository;

import com.example.analytics.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    Session findAllByUserName(String userName);
    Session findAllByRoomId(UUID roomId);

    Session findAllByUserNameAndRoomId(String userName, UUID roomId);
}