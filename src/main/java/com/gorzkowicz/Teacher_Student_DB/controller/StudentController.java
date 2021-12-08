package com.gorzkowicz.Teacher_Student_DB.controller;

import com.gorzkowicz.Teacher_Student_DB.model.Student;
import com.gorzkowicz.Teacher_Student_DB.model.Teacher;
import com.gorzkowicz.Teacher_Student_DB.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/students")
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student newStudent) {
        try {
            Student student = studentRepository.save(newStudent);
            return new ResponseEntity<>(student, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/students")
    public ResponseEntity<Page<Student>> getAllStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "lastName") String sort
    ) {
        try {
            Page<Student> students;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            if (name == null) {
                students = studentRepository.findAll(pageable);
            } else {
                students = studentRepository.findByFirstOrLastName(name, pageable);
            }

            if (students.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(students, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable("id") long id) {
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isPresent()) {
                return new ResponseEntity<>(student.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<HttpStatus> deleteStudentById(@PathVariable long id) {
        try {
            Optional<Student> _student = studentRepository.findById(id);
            if (_student.isPresent()) {
                Student student = _student.get();
                student.getTeachers().forEach(teacher ->
                        teacher.getStudents().remove(student));
                studentRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<Student> updateStudentById(@PathVariable("id") long id,
                                                     @Valid @RequestBody Student student) {
        try {
            Optional<Student> studentToUpdate = studentRepository.findById(id);
            if (studentToUpdate.isPresent()) {
                Student _student = studentToUpdate.get();
                _student.setFirstName(student.getFirstName());
                _student.setLastName(student.getLastName());
                _student.setAge(student.getAge());
                _student.setEmail(student.getEmail());
                _student.setSubject(student.getSubject());
                studentRepository.save(_student);
                return new ResponseEntity<>(_student, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/students/{id}/teachers")
    public ResponseEntity<Set<Teacher>> getAllTeachersOfStudent(@PathVariable("id") long id) {
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isPresent()) {
                return new ResponseEntity<>(student.get().getTeachers(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}