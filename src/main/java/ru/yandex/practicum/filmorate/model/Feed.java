package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Feed {
    int eventId;
    int userId;
    int entityId;
    String eventType;
    String operation;
    LocalDate timestamp;
}
