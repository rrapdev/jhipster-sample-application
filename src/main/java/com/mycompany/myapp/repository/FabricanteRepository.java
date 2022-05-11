package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Fabricante;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Fabricante entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FabricanteRepository extends JpaRepository<Fabricante, Long> {}
