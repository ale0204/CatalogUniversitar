package com.catalog;

import com.catalog.exception.EntityNotFoundException;
import com.catalog.model.Student;
import com.catalog.repository.InMemoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRepositoryTest {
    private InMemoryRepository<Student> repo;

    @BeforeEach
    void setUp() {
        repo = new InMemoryRepository<>();
    }

    @Test
    void insert_siGetAll_returneazaStudentii() {
        Student s = new Student("Test", 20);
        repo.insert(s);
        List<Student> all = repo.getAll();
        assertEquals(1, all.size());
        assertEquals(s.getId(), all.get(0).getId());
    }

    @Test
    void getById_returneazaStudentulCorect() {
        Student s = new Student("Ion", 21);
        repo.insert(s);
        Student gasit = repo.getById(s.getId());
        assertNotNull(gasit);
        assertEquals("Ion", gasit.getNume());
    }

    @Test
    void getById_idInexistent_returneazaNull() {
        assertNull(repo.getById(9999));
    }

    @Test
    void delete_eliminaStudentul() {
        Student s = new Student("Maria", 22);
        repo.insert(s);
        repo.delete(s);
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void update_idInexistent_aruncarEntityNotFoundException() {
        Student s = new Student("Fictiv", 20);
        // s nu e in repo -> update trebuie sa arunce EntityNotFoundException
        assertThrows(EntityNotFoundException.class, () -> repo.update(s));
    }
}
