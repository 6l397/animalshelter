package chnu.edu.pz.animalshelter.service;

import chnu.edu.pz.animalshelter.model.Animal;
import chnu.edu.pz.animalshelter.repository.AnimalRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnimalServiceMockTests {

    @Mock
    private AnimalRepository mockRepository;

    @InjectMocks
    private AnimalService underTest;

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    // 1️⃣ Create animal: id exists → FAIL
    @DisplayName("Create animal: ID already exists → Fail")
    @Test
    void whenCreateAnimalAndIdExistsThenFail() {
        // given
        Animal a = new Animal("10", "Rex", "dog", 4, "test");
        given(mockRepository.existsById("10")).willReturn(true);

        // when
        Animal result = underTest.createAnimal(a);

        // then
        assertNull(result);
        verify(mockRepository, times(1)).existsById("10");
        verify(mockRepository, never()).save(any());
    }

    // 2️⃣ Create animal: id not exists → OK
    @DisplayName("Create animal: ID does not exist → OK")
    @Test
    void whenCreateAnimalAndIdNotExistsThenOk() {
        // given
        Animal a = new Animal("20", "Milo", "cat", 2, "good");
        given(mockRepository.existsById("20")).willReturn(false);
        given(mockRepository.save(a)).willReturn(a);

        // when
        Animal result = underTest.createAnimal(a);

        // then
        assertNotNull(result);
        assertEquals(a, result);
        verify(mockRepository).save(a);
        verify(mockRepository).existsById("20");
    }

    // 3️⃣ Update: animal.id = null → FAIL
    @DisplayName("Update animal: id == null → Fail")
    @Test
    void whenUpdateAnimalAndIdIsNullThenFail() {
        // given
        Animal a = new Animal(null, "Tom", "cat", 3, "smth");

        // when
        Animal result = underTest.updateAnimal(a);

        // then
        assertNull(result);
        verify(mockRepository, never()).save(any());
    }

    // 4️⃣ Update: id exists in DB → OK
    @DisplayName("Update animal: id exists in DB → OK")
    @Test
    void whenUpdateAnimalAndIdExistsThenOk() {
        // given
        Animal a = new Animal("40", "Lucky", "dog", 5, "desc");
        given(mockRepository.save(a)).willReturn(a);

        // when
        Animal result = underTest.updateAnimal(a);

        // then
        assertNotNull(result);
        assertEquals("Lucky", result.getName());
        verify(mockRepository).save(a);
    }

    // 5️⃣ getAnimalById: should return object
    @DisplayName("Get animal by ID → OK")
    @Test
    void whenGetAnimalByIdThenReturnAnimal() {
        // given
        Animal a = new Animal("55", "Bella", "cat", 1, "cute");
        given(mockRepository.findById("55")).willReturn(Optional.of(a));

        // when
        Animal result = underTest.getAnimalById("55");

        // then
        assertNotNull(result);
        assertEquals("Bella", result.getName());
        verify(mockRepository).findById("55");
    }
}
