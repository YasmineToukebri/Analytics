package com.example.Analytics.repository;

import com.example.Analytics.models.SessionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface sessionAction extends JpaRepository<SessionAction, UUID> {
}
