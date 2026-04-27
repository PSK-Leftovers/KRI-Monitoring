package com.leftovers.kri.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.leftovers.kri.entity.Rodiklis;

@Repository
public interface RodiklisRepository extends JpaRepository<Rodiklis, Long> {
}