package chnu.edu.pz.animalshelter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;


@Data
@ToString
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Animal {
    @Id
    private String id;

    private String name;
    private String species;
    private int age;
    private String description;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    // 4-аргументний конструктор (без id, без audit-полів)
    public Animal(String name, String species, int age, String description) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.description = description;
    }

    // 👇 ДОДАЙ ОЦЕ — 5-аргументний конструктор (з id)
    public Animal(String id, String name, String species, int age, String description) {
        this.id = id;
        this.name = name;
        this.species = species;
        this.age = age;
        this.description = description;
    }
}
