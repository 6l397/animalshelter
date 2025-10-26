package chnu.edu.pz.animalshelter.repository;

import chnu.edu.pz.animalshelter.model.Animal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalRepository extends MongoRepository<Animal, String> {
}
