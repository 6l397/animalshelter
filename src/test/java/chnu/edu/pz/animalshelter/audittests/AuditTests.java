package chnu.edu.pz.animalshelter.audittests;

import chnu.edu.pz.animalshelter.model.Animal;
import chnu.edu.pz.animalshelter.service.AnimalService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class AuditTests {

    @Autowired
    private AnimalService animalService;

    @BeforeEach
    void setUp() {
        animalService.createAnimal(new Animal("1", "Freddy", "dog", 5, "###audit"));
        animalService.createAnimal(new Animal("2", "Mick", "cat", 3, "###audit"));
        animalService.createAnimal(new Animal("3", "Paul", "dog", 2, "###audit"));
    }

    @AfterEach
    void tearDown() {
        animalService.getAllAnimals().stream()
                .filter(a -> a.getDescription().contains("###audit"))
                .forEach(a -> animalService.deleteAnimal(a.getId()));
    }

    // 1. кількість тестових записів
    @Test
    void shouldContain3AuditRecords() {
        long count = animalService.getAllAnimals().stream()
                .filter(a -> a.getDescription().contains("###audit"))
                .count();
        assertEquals(3, count);
    }

    // 2. при створенні заповнюються audit-поля
    @Test
    void whenCreateAnimalThenAuditFieldsPresent() {
        Animal a = new Animal("10", "Rex", "dog", 4, "###audit");

        Animal created = animalService.createAnimal(a);

        assertNotNull(created);
        assertNotNull(created.getLastModifiedDate());

    }


    // 3. оновлення змінює lastModifiedDate
    @Test
    void whenUpdateAnimalThenLastModifiedChanges() throws InterruptedException {
        Animal a = new Animal("20", "Milo", "cat", 2, "###audit");
        Animal created = animalService.createAnimal(a);

        LocalDateTime beforeUpdateLastModified = created.getLastModifiedDate();
        assertNotNull(beforeUpdateLastModified);

        Thread.sleep(500);

        created.setAge(5);
        Animal updated = animalService.updateAnimal(created);

        assertNotNull(updated);
        assertTrue(updated.getLastModifiedDate().isAfter(beforeUpdateLastModified));
    }

    // 4. createdDate не змінюється після оновлення
    @Test
    void createdDateShouldNotChangeOnUpdate() throws InterruptedException {
        Animal a = new Animal("30", "Bella", "cat", 1, "###audit");
        Animal created = animalService.createAnimal(a);

        LocalDateTime createdDate = created.getCreatedDate();

        Thread.sleep(200);

        created.setName("Bella Updated");
        Animal updated = animalService.updateAnimal(created);

        assertEquals(createdDate, updated.getCreatedDate());
    }

    // 5. два різні оновлення дають різні lastModifiedDate
    @Test
    void auditShouldDifferentiateTwoUpdates() throws InterruptedException {
        Animal a = new Animal("40", "Tom", "dog", 3, "###audit");
        Animal created = animalService.createAnimal(a);

        LocalDateTime firstLastModified = created.getLastModifiedDate();
        assertNotNull(firstLastModified);

        Thread.sleep(500);

        created.setSpecies("wolf");
        Animal updated = animalService.updateAnimal(created);

        assertTrue(updated.getLastModifiedDate().isAfter(firstLastModified));
    }
}
