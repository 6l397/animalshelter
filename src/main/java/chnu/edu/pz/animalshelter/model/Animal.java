package chnu.edu.pz.animalshelter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Animal {
    @Id
    private String id;
    private String name;
    private String species;      // наприклад: dog, cat
    private int age;
    private String description;

    public Animal(String name, String species, int age, String description) {
        this.name = name;
        this.species = species;
        this.age = age;
        this.description = description;
    }
}
