package com.leftovers.kri.controller;

import com.leftovers.kri.entity.Rodiklis;
import com.leftovers.kri.repository.RodiklisRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rodikliai")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
@RequiredArgsConstructor
public class RodiklisController {

    private final RodiklisRepository repo;

    @GetMapping
    public List<Rodiklis> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Rodiklis create(@RequestBody @Valid Rodiklis rodiklis) {
        return repo.save(rodiklis);
    }

    @PutMapping("/{id}")
    public Rodiklis update(@PathVariable Long id, @RequestBody @Valid Rodiklis rodiklis) {
        rodiklis.setId(id);
        return repo.save(rodiklis);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}