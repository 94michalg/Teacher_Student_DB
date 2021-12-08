package com.gorzkowicz.Teacher_Student_DB.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(name = "teachers")
@NoArgsConstructor
@Getter
@Setter
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 40, message = "Length of first name should be between 2 - 40")
    private String firstName;

    @NotNull
    private String lastName;

    @Min(value = 18, message = "Age should not be less than 18")
    private int age;

    @Email(message = "E-mail should be valid")
    private String email;

    private String course;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "teacher_student",
            joinColumns = {@JoinColumn(name = "teacher_id")},
            inverseJoinColumns = {@JoinColumn(name = "student_id")})
    private Set<Student> students = new HashSet<>();

    public void addNewStudent(Student newStudent) {
        this.students.add(newStudent);
        newStudent.getTeachers().add(this);
    }

    public void removeStudent(Student student) {
        this.students.remove(student);
        student.getTeachers().remove(this);
    }

    public Teacher(String firstName, String lastName, int age, String email, String course) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.email = email;
        this.course = course;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", course='" + course + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return age == teacher.age && id.equals(teacher.id) && firstName.equals(teacher.firstName) && lastName.equals(teacher.lastName) && Objects.equals(email, teacher.email) && Objects.equals(course, teacher.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, email, course);
    }
}
