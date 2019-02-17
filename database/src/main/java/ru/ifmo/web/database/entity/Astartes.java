package ru.ifmo.web.database.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Astartes {
    private Long id;
    private String name;
    private String title;
    private String position;
    private String planet;
    private Date birthdate;
}
