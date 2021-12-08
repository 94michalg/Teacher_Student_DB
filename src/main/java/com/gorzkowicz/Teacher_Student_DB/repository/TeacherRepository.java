package com.gorzkowicz.Teacher_Student_DB.repository;

import com.gorzkowicz.Teacher_Student_DB.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    Page<Teacher> findAll (Pageable pageable);

    // Find teacher by firstName, lastName or fullName
    @Query("SELECT t FROM Teacher t WHERE CONCAT(first_Name, ' ', last_Name) LIKE %:s%")
    Page<Teacher> findByFirstOrLastName(String s, Pageable pageable);

//    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.students WHERE t.id = :id")
//    Optional<Teacher> findTeacherByIdAndEagerLoadAllStudents(Long id);

}
