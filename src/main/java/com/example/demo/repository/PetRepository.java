package com.example.demo.repository;

import com.example.demo.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Page<Pet> findBySpecies(String species, Pageable pageable);

    Page<Pet> findByBreed(String breed, Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.status = 'AVAILABLE'")
    Page<Pet> findAvailablePets(Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.status = 'AVAILABLE' AND p.species = :species")
    Page<Pet> findAvailablePetsBySpecies(@Param("species") String species, Pageable pageable);

    @Query("SELECT p FROM Pet p WHERE p.status = 'AVAILABLE' AND p.species = :species AND p.breed = :breed")
    Page<Pet> findAvailablePetsBySpeciesAndBreed(
            @Param("species") String species,
            @Param("breed") String breed,
            Pageable pageable);

    @Query("SELECT DISTINCT p.species FROM Pet p")
    List<String> findAllSpecies();

    @Query("SELECT DISTINCT p.breed FROM Pet p WHERE p.species = :species")
    List<String> findBreedsBySpecies(@Param("species") String species);
}