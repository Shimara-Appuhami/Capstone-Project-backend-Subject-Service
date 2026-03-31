package com.ijse.eca.subjectservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ijse.eca.subjectservice.domain.Subject;
import com.ijse.eca.subjectservice.repository.SubjectRepository;

import java.util.List;

@Configuration
public class SubjectDataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(SubjectDataInitializer.class);

    @Bean
    CommandLineRunner seedSubjects(SubjectRepository repository) {
        return args -> {
            try {
                if (repository.count() > 0) {
                    return;
                }

                repository.saveAll(List.of(
                        createSubject("ECA-101", "Cloud Architecture Fundamentals", "Cloud Engineering", "Semester 1", 3, "Core patterns for distributed systems, resiliency, and service decomposition."),
                        createSubject("ECA-203", "Platform Operations", "Cloud Engineering", "Semester 2", 4, "Operational concerns for observability, deployment automation, scaling, and reliability."),
                        createSubject("ECA-305", "Enterprise Integration", "Software Engineering", "Semester 2", 3, "Gateway, messaging, and integration strategies for enterprise-grade systems.")
                ));
            } catch (DataAccessException exception) {
                logger.warn("Skipping subject seed because MongoDB is not reachable: {}", exception.getMessage());
            }
        };
    }

    private Subject createSubject(String subjectCode, String title, String department, String semester, Integer credits, String description) {
        Subject subject = new Subject();
        subject.setSubjectCode(subjectCode);
        subject.setTitle(title);
        subject.setDepartment(department);
        subject.setSemester(semester);
        subject.setCredits(credits);
        subject.setDescription(description);
        return subject;
    }
}
