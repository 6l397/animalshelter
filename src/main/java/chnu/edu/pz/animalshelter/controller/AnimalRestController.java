package chnu.edu.pz.animalshelter.controller;

import chnu.edu.pz.animalshelter.model.Animal;
import chnu.edu.pz.animalshelter.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/animals")
@RequiredArgsConstructor
public class AnimalRestController {

    private final AnimalService animalService;

    @RequestMapping("hello")
    public String hello() {
        return "Welcome to Animal Shelter API 🐕🐈";
    }

    @GetMapping("")
    public List<Animal> getAllAnimals() {
        return animalService.getAllAnimals();
    }

    @PostMapping
    public Animal createAnimal(@RequestBody Animal animal) {
        return animalService.createAnimal(animal);
    }

    @GetMapping("{id}")
    public Animal getAnimalById(@PathVariable String id) {
        return animalService.getAnimalById(id);
    }

    @DeleteMapping("{id}")
    public void deleteAnimal(@PathVariable String id) {
        animalService.deleteAnimal(id);
    }

    @PutMapping
    public Animal updateAnimal(@RequestBody Animal animal) {
        return animalService.updateAnimal(animal);
    }
}
