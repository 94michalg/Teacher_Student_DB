package com.gorzkowicz.Teacher_Student_DB.repository;

import com.gorzkowicz.Teacher_Student_DB.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Page<Student> findAll (Pageable pageable);

    @Query("SELECT u FROM Student u WHERE CONCAT(first_Name, ' ', last_Name) LIKE %:s%")
    Page<Student> findByFirstOrLastName(String s, Pageable pageable);

}
