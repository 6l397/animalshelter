package chnu.edu.pz.animalshelter.repository;

import chnu.edu.pz.animalshelter.model.Animal;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class AnimalRepositoryTest {

    @Autowired
    AnimalRepository animalRepository;

    @BeforeEach
    void setUp() {
        Animal a1 = new Animal("1", "Freddy", "dog", 5, "###test");
        Animal a2 = new Animal("2", "Mick", "cat", 3, "###test");
        Animal a3 = new Animal("3", "Paul", "dog", 2, "###test");
        animalRepository.saveAll(List.of(a1, a2, a3));
    }

    @AfterEach
    void tearDown() {
        List<Animal> toDelete = animalRepository.findAll().stream()
                .filter(a -> a.getDescription() != null &&
                        a.getDescription().contains("###test"))
                .toList();
        animalRepository.deleteAll(toDelete);
    }

    // 1. кількість початкових записів
    @Test
    void shouldContain3TestRecords() {
        List<Animal> testAnimals = animalRepository.findAll().stream()
                .filter(a -> a.getDescription() != null &&
                        a.getDescription().contains("###test"))
                .toList();

        assertEquals(3, testAnimals.size());
    }
    // 2. має згенерувати ID при збереженні без id
    @Test
    void shouldGenerateIdForNewAnimal() {
        Animal a = new Animal("Bobby", "dog", 4, "###test");

        animalRepository.save(a);

        Animal fromDb = animalRepository.findAll().stream()
                .filter(an -> "Bobby".equals(an.getName()))
                .findFirst().orElse(null);

        assertNotNull(fromDb);
        assertNotNull(fromDb.getId());
        assertEquals(24, fromDb.getId().length()); // стандартний ObjectId
    }

    // 3. якщо id задано – запис збережеться
    @Test
    void shouldSaveAnimalWithCustomId() {
        Animal a = new Animal("999", "Test", "cat", 1, "###test2");

        animalRepository.save(a);

        Animal fromDb = animalRepository.findById("999").orElse(null);

        assertNotNull(fromDb);
        assertEquals("999", fromDb.getId());
    }

    // 4. пошук за існуючим ID
    @Test
    void shouldFindAnimalById() {
        Optional<Animal> found = animalRepository.findById("1");

        assertTrue(found.isPresent());
        assertEquals("Freddy", found.get().getName());
    }

    // 5. пошук за неіснуючим ID = empty
    @Test
    void shouldReturnEmptyForNotExistingId() {
        Optional<Animal> found = animalRepository.findById("777");

        assertTrue(found.isEmpty());
    }

    // 6. оновлення запису
    @Test
    void shouldUpdateAnimal() {
        Animal a = animalRepository.findById("2").orElseThrow();
        a.setAge(10);

        animalRepository.save(a);

        Animal updated = animalRepository.findById("2").orElseThrow();
        assertEquals(10, updated.getAge());
    }

    // 7. видалення за id
    @Test
    void shouldDeleteById() {
        long before = animalRepository.count();

        animalRepository.deleteById("3");

        long after = animalRepository.count();

        assertEquals(before - 1, after);
        assertTrue(animalRepository.findById("3").isEmpty());
    }

    // 8. deleteAll видаляє список
    @Test
    void shouldDeleteAllTestAnimals() {
        List<Animal> testAnimals = animalRepository.findAll().stream()
                .filter(a -> a.getDescription().contains("###test"))
                .toList();

        animalRepository.deleteAll(testAnimals);

        List<Animal> stillExists = animalRepository.findAll().stream()
                .filter(a -> a.getDescription().contains("###test"))
                .toList();

        assertEquals(0, stillExists.size());
    }

    // 9. existsById повертає true
    @Test
    void existsShouldReturnTrueForExistingId() {
        assertTrue(animalRepository.existsById("1"));
    }

    // 10. count повертає правильну кількість
    @Test
    void countShouldReturnCorrectValue() {
        long testCount = animalRepository.findAll().stream()
                .filter(a -> a.getDescription() != null &&
                        a.getDescription().contains("###test"))
                .count();

        assertEquals(3, testCount);
    }
}

