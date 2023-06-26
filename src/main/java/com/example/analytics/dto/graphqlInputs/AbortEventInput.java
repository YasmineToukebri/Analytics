package com.example.analytics.dto.graphqlInputs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbortEventInput {

    private String userName;
    private UUID eventId;
}
