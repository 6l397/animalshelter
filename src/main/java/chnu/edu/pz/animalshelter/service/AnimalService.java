package chnu.edu.pz.animalshelter.service;

import chnu.edu.pz.animalshelter.model.Animal;
import chnu.edu.pz.animalshelter.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public List<Animal> getAllAnimals() {
        return animalRepository.findAll();
    }

    public Animal createAnimal(Animal animal) {
        if (animal.getId() != null && animalRepository.existsById(animal.getId())) {
            return null;
        }
        return animalRepository.save(animal);
    }

    public Animal getAnimalById(String id) {
        return animalRepository.findById(id).orElse(null);
    }

    public void deleteAnimal(String id) {
        animalRepository.deleteById(id);
    }

    public Animal updateAnimal(Animal animal) {
        if (animal.getId() == null) {
            return null;
        }
        return animalRepository.save(animal);
    }
}
