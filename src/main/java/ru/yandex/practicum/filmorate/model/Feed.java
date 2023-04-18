package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed {
    private int eventId;
    private int userId;
    private long timestamp;
    private String eventType;
    private String operation;
    private int entityId;
}
