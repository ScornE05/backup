package com.example.demo.service;

import com.example.demo.DTO.PetDTO;
import com.example.demo.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PetService {

    Pet createPet(PetDTO petDTO);

    Pet updatePet(Long id, PetDTO petDTO);

    Optional<Pet> getPetById(Long id);

    Page<Pet> getAllPets(Pageable pageable);

    Page<Pet> getPetsBySpecies(String species, Pageable pageable);

    Page<Pet> getPetsByBreed(String breed, Pageable pageable);

    Page<Pet> getAvailablePets(Pageable pageable);

    Page<Pet> getAvailablePetsBySpecies(String species, Pageable pageable);

    Page<Pet> getAvailablePetsBySpeciesAndBreed(String species, String breed, Pageable pageable);

    List<String> getAllSpecies();

    List<String> getBreedsBySpecies(String species);

    void deletePet(Long id);

    void updatePetStatus(Long id, String status);
}
