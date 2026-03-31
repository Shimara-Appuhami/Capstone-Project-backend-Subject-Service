package com.ijse.eca.subjectservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.ijse.eca.subjectservice.domain.Subject;
import com.ijse.eca.subjectservice.exception.DuplicateResourceException;
import com.ijse.eca.subjectservice.exception.PersistenceUnavailableException;
import com.ijse.eca.subjectservice.exception.ResourceNotFoundException;
import com.ijse.eca.subjectservice.repository.SubjectRepository;
import com.ijse.eca.subjectservice.web.dto.CreateSubjectRequest;
import com.ijse.eca.subjectservice.web.dto.UpdateSubjectRequest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SubjectService {

    private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);

    private final SubjectRepository repository;
    private final Map<String, Subject> fallbackStore = new ConcurrentHashMap<>();

    public SubjectService(SubjectRepository repository) {
        this.repository = repository;
        seedFallbackStore();
    }

    public List<Subject> findAll() {
        try {
            return repository.findAllByOrderBySubjectCodeAsc();
        } catch (DataAccessException exception) {
            logger.warn("MongoDB unavailable for findAll; using in-memory subject store: {}", exception.getMessage());
            return fallbackStore.values().stream()
                    .sorted(Comparator.comparing(Subject::getSubjectCode))
                    .toList();
        }
    }

    public Subject findById(String id) {
        try {
            return repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found: " + id));
        } catch (DataAccessException exception) {
            logger.warn("MongoDB unavailable for findById; using in-memory subject store: {}", exception.getMessage());
            Subject subject = fallbackStore.get(id);
            if (subject == null) {
                throw new ResourceNotFoundException("Subject not found: " + id);
            }
            return subject;
        }
    }

    public Subject create(CreateSubjectRequest request) {
        String subjectCode = normalizeCode(request.subjectCode());
        Subject subject = new Subject();
        apply(subject, subjectCode, normalize(request.title()), normalize(request.department()), normalize(request.semester()), request.credits(), normalize(request.description()));

        try {
            ensureUnique(subjectCode, null);
            return repository.save(subject);
        } catch (DataAccessException exception) {
            logger.warn("MongoDB unavailable for create; refusing in-memory fallback write: {}", exception.getMessage());
            throw new PersistenceUnavailableException("Subject database is unavailable. The subject was not saved.", exception);
        }
    }

    public Subject update(String id, UpdateSubjectRequest request) {
        String subjectCode = normalizeCode(request.subjectCode());

        try {
            Subject subject = findById(id);
            ensureUnique(subjectCode, id);
            apply(subject, subjectCode, normalize(request.title()), normalize(request.department()), normalize(request.semester()), request.credits(), normalize(request.description()));
            return repository.save(subject);
        } catch (DataAccessException exception) {
            logger.warn("MongoDB unavailable for update; refusing in-memory fallback write: {}", exception.getMessage());
            throw new PersistenceUnavailableException("Subject database is unavailable. The subject update was not saved.", exception);
        }
    }

    public void delete(String id) {
        try {
            Subject subject = findById(id);
            repository.delete(subject);
        } catch (DataAccessException exception) {
            logger.warn("MongoDB unavailable for delete; refusing in-memory fallback write: {}", exception.getMessage());
            throw new PersistenceUnavailableException("Subject database is unavailable. The subject was not deleted.", exception);
        }
    }

    private void ensureUnique(String subjectCode, String id) {
        boolean exists = id == null
                ? repository.existsBySubjectCodeIgnoreCase(subjectCode)
                : repository.existsBySubjectCodeIgnoreCaseAndIdNot(subjectCode, id);
        if (exists) {
            throw new DuplicateResourceException("Subject code already exists: " + subjectCode);
        }
    }

    private void apply(Subject subject, String subjectCode, String title, String department, String semester, Integer credits, String description) {
        subject.setSubjectCode(subjectCode);
        subject.setTitle(title);
        subject.setDepartment(department);
        subject.setSemester(semester);
        subject.setCredits(credits);
        subject.setDescription(description);
    }

    private String normalizeCode(String value) {
        return normalize(value).toUpperCase(Locale.ROOT);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private void seedFallbackStore() {
        if (!fallbackStore.isEmpty()) {
            return;
        }

        List<Subject> defaults = new ArrayList<>();
        defaults.add(createFallbackSubject("ECA-101", "Cloud Architecture Fundamentals", "Cloud Engineering", "Semester 1", 3, "Core patterns for distributed systems, resiliency, and service decomposition."));
        defaults.add(createFallbackSubject("ECA-203", "Platform Operations", "Cloud Engineering", "Semester 2", 4, "Operational concerns for observability, deployment automation, scaling, and reliability."));
        defaults.add(createFallbackSubject("ECA-305", "Enterprise Integration", "Software Engineering", "Semester 2", 3, "Gateway, messaging, and integration strategies for enterprise-grade systems."));

        defaults.forEach(subject -> fallbackStore.put(subject.getId(), subject));
    }

    private Subject createFallbackSubject(String subjectCode, String title, String department, String semester, Integer credits, String description) {
        Subject subject = new Subject();
        subject.setId(UUID.randomUUID().toString());
        subject.setSubjectCode(subjectCode);
        subject.setTitle(title);
        subject.setDepartment(department);
        subject.setSemester(semester);
        subject.setCredits(credits);
        subject.setDescription(description);
        return subject;
    }
}
