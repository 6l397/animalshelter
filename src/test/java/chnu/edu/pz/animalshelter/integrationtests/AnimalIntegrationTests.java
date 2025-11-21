package chnu.edu.pz.animalshelter.integrationtests;

import chnu.edu.pz.animalshelter.model.Animal;
import chnu.edu.pz.animalshelter.repository.AnimalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AnimalIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnimalRepository animalRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String toJson(Object o) throws Exception {
        return objectMapper.writeValueAsString(o);
    }

    @BeforeEach
    void setUp() {
        animalRepository.deleteAll(); // чистий старт перед кожним тестом

        Animal a1 = new Animal("1", "Freddy", "dog", 5, "###test");
        Animal a2 = new Animal("2", "Mick", "cat", 3, "###test");
        Animal a3 = new Animal("3", "Paul", "dog", 2, "###test");
        animalRepository.saveAll(List.of(a1, a2, a3));
    }

    @AfterEach
    void tearDown() {
        animalRepository.deleteAll();
    }

    // 1. hello endpoint
    @Test
    @DisplayName("Hello endpoint should return greeting string")
    void helloEndpointShouldReturnGreeting() throws Exception {
        mockMvc.perform(get("/api/v1/animals/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Animal Shelter API")));
    }

    // 2. get all animals
    @Test
    @DisplayName("GET /animals should return 3 animals")
    void getAllAnimalsShouldReturn3Records() throws Exception {
        mockMvc.perform(get("/api/v1/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));
    }

    // 3. create animal – happy path
    @Test
    @DisplayName("POST /animals should create new animal")
    void whenCreateAnimalThenItIsPersisted() throws Exception {
        Animal request = new Animal("10", "Rex", "dog", 4, "###test");

        mockMvc.perform(post("/api/v1/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk());

        Animal fromDb = animalRepository.findById("10").orElse(null);
        assertNotNull(fromDb);
        assertEquals("Rex", fromDb.getName());
        assertEquals("dog", fromDb.getSpecies());
        assertEquals(4, fromDb.getAge());
        assertEquals("###test", fromDb.getDescription());
    }

    // 4. create + then GET all => 4 елементи
    @Test
    @DisplayName("After create, GET /animals should return 4 animals")
    void afterCreateGetAllShouldReturn4Animals() throws Exception {
        Animal request = new Animal("20", "Milo", "cat", 2, "###test");

        mockMvc.perform(post("/api/v1/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/animals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));
    }

    // 5. get by id – positive
    @Test
    @DisplayName("GET /animals/{id} should return existing animal")
    void getAnimalByIdShouldReturnAnimal() throws Exception {
        mockMvc.perform(get("/api/v1/animals/{id}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Freddy"))
                .andExpect(jsonPath("$.species").value("dog"));
    }

    // 6. get by id – negative (немає такого id)
    @Test
    @DisplayName("GET /animals/{id} with unknown id should return empty body")
    void getAnimalByUnknownIdShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/v1/animals/{id}", "999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));  // service повертає null
    }

    // 7. delete – happy path
    @Test
    @DisplayName("DELETE /animals/{id} should remove animal from DB")
    void deleteAnimalShouldRemoveRecord() throws Exception {
        assertTrue(animalRepository.existsById("2"));

        mockMvc.perform(delete("/api/v1/animals/{id}", "2"))
                .andExpect(status().isOk());

        assertFalse(animalRepository.existsById("2"));
        assertEquals(2, animalRepository.count());
    }

    // 8. delete – negative (unknown id)
    @Test
    @DisplayName("DELETE /animals/{id} with unknown id should not change count")
    void deleteUnknownIdShouldNotChangeCount() throws Exception {
        long before = animalRepository.count();

        mockMvc.perform(delete("/api/v1/animals/{id}", "999"))
                .andExpect(status().isOk());

        long after = animalRepository.count();
        assertEquals(before, after);
    }

    // 9. update – happy path
    @Test
    @DisplayName("PUT /animals should update existing animal")
    void updateAnimalShouldChangeFields() throws Exception {
        Animal update = new Animal("1", "Freddy Updated", "dog", 6, "###updated");

        mockMvc.perform(put("/api/v1/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(update)))
                .andExpect(status().isOk());

        Animal fromDb = animalRepository.findById("1").orElseThrow();
        assertEquals("Freddy Updated", fromDb.getName());
        assertEquals(6, fromDb.getAge());
        assertEquals("###updated", fromDb.getDescription());
    }

    // 10. update – id == null → нічого не змінюється
    @Test
    @DisplayName("PUT /animals with null id should not save new animal")
    void updateAnimalWithNullIdShouldFailSilently() throws Exception {
        long before = animalRepository.count();

        Animal update = new Animal(null, "NoId", "cat", 1, "###test-null");

        mockMvc.perform(put("/api/v1/animals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(update)))
                .andExpect(status().isOk());

        long after = animalRepository.count();
        assertEquals(before, after);

        boolean exists = animalRepository.findAll().stream()
                .anyMatch(a -> "NoId".equals(a.getName()));
        assertFalse(exists);
    }
}
