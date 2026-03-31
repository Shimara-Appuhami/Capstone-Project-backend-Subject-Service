package com.ijse.eca.subjectservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ijse.eca.subjectservice.domain.Subject;

import java.util.List;

public interface SubjectRepository extends MongoRepository<Subject, String> {

    List<Subject> findAllByOrderBySubjectCodeAsc();

    boolean existsBySubjectCodeIgnoreCase(String subjectCode);

    boolean existsBySubjectCodeIgnoreCaseAndIdNot(String subjectCode, String id);
}