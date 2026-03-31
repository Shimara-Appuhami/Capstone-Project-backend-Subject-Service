package com.ijse.eca.subjectservice.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ijse.eca.subjectservice.domain.Subject;
import com.ijse.eca.subjectservice.service.SubjectService;
import com.ijse.eca.subjectservice.web.dto.CreateSubjectRequest;
import com.ijse.eca.subjectservice.web.dto.UpdateSubjectRequest;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping
    public List<Subject> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Subject findById(@PathVariable String id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Subject create(@Valid @RequestBody CreateSubjectRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public Subject update(@PathVariable String id, @Valid @RequestBody UpdateSubjectRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
