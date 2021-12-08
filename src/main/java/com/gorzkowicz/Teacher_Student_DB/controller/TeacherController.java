package com.gorzkowicz.Teacher_Student_DB.controller;

import com.gorzkowicz.Teacher_Student_DB.model.Student;
import com.gorzkowicz.Teacher_Student_DB.model.Teacher;
import com.gorzkowicz.Teacher_Student_DB.repository.StudentRepository;
import com.gorzkowicz.Teacher_Student_DB.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/teachers")
    public ResponseEntity<Teacher> createTeacher(@Valid @RequestBody Teacher newTeacher) {
        try {
            Teacher teacher = teacherRepository.save(newTeacher);
            return new ResponseEntity<>(teacher, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/teachers")
    public ResponseEntity<Page<Teacher>> getAllTeachers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            @RequestParam(required = false, defaultValue = "lastName") String sort
    ) {
        try {
            Page<Teacher> teachers;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
            if (name == null) {
                teachers = teacherRepository.findAll(pageable);
            } else {
                teachers = teacherRepository.findByFirstOrLastName(name, pageable);
            }

            if (teachers.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(teachers, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/teachers/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable("id") long id) {
        try {
            Optional<Teacher> teacher = teacherRepository.findById(id);
            if (teacher.isPresent()) {
                return new ResponseEntity<>(teacher.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<HttpStatus> deleteTeacherById(@PathVariable long id) {
        try {
            Optional<Teacher> teacher = teacherRepository.findById(id);
            if (teacher.isPresent()) {
                teacherRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/teachers/{id}")
    public ResponseEntity<Teacher> updateTeacherById(@PathVariable("id") long id,
                                                     @Valid @RequestBody Teacher teacher) {
        try {
            Optional<Teacher> teacherToUpdate = teacherRepository.findById(id);
            if (teacherToUpdate.isPresent()) {
                Teacher _teacher = teacherToUpdate.get();
                _teacher.setFirstName(teacher.getFirstName());
                _teacher.setLastName(teacher.getLastName());
                _teacher.setAge(teacher.getAge());
                _teacher.setEmail(teacher.getEmail());
                _teacher.setCourse(teacher.getCourse());
                teacherRepository.save(_teacher);
                return new ResponseEntity<>(_teacher, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/teachers/{id}/students")
    public ResponseEntity<Set<Student>> getAllStudentsOfTeacher(@PathVariable("id") long id) {
        try {
            Optional<Teacher> teacher = teacherRepository.findById(id);
            if (teacher.isPresent()) {
                return new ResponseEntity<>(teacher.get().getStudents(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/teachers/{id}/students/{studentId}")
    public ResponseEntity<HttpStatus> addNewStudentToTeacher(@PathVariable("id") long id,
                                                             @PathVariable("studentId") long studentId) {
        try {
            Optional<Teacher> teacher = teacherRepository.findById(id);
            Optional<Student> student = studentRepository.findById(studentId);
            if (teacher.isPresent() && student.isPresent()) {
                teacher.get().addNewStudent(student.get());
                teacherRepository.save(teacher.get());
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/teachers/{id}/students/{studentId}")
    public ResponseEntity<HttpStatus> deleteStudentFromTeacher(@PathVariable("id") long id,
                                                               @PathVariable("studentId") long studentId) {
        try {
            Optional<Teacher> _teacher = teacherRepository.findById(id);
            Optional<Student> _student = studentRepository.findById(studentId);
            if (_teacher.isPresent() && _student.isPresent()) {
                Teacher teacher = _teacher.get();
                Student student = _student.get();
                if (teacher.getStudents().contains(student)) {
                    teacher.removeStudent(student);
                    teacherRepository.save(teacher);
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
