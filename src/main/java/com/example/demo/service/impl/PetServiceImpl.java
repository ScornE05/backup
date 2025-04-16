package com.example.demo.service.impl;

import com.example.demo.DTO.PetDTO;
import com.example.demo.entity.Pet;
import com.example.demo.entity.PetImage;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PetImageRepository;
import com.example.demo.repository.PetRepository;
import com.example.demo.service.PetService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final PetImageRepository petImageRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PetServiceImpl(PetRepository petRepository,
                          PetImageRepository petImageRepository,
                          ModelMapper modelMapper) {
        this.petRepository = petRepository;
        this.petImageRepository = petImageRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Pet createPet(PetDTO petDTO) {
        Pet pet = new Pet();
        pet.setName(petDTO.getName());
        pet.setSpecies(petDTO.getSpecies());
        pet.setBreed(petDTO.getBreed());
        pet.setAge(petDTO.getAge());
        pet.setGender(petDTO.getGender());
        pet.setColor(petDTO.getColor());
        pet.setWeight(petDTO.getWeight());
        pet.setDescription(petDTO.getDescription());
        pet.setPrice(petDTO.getPrice());
        pet.setStatus(petDTO.getStatus() != null ? petDTO.getStatus() : "AVAILABLE");
        pet.setImageUrl(petDTO.getImageUrl());
        pet.setCreatedAt(LocalDateTime.now());
        pet.setUpdatedAt(LocalDateTime.now());

        pet = petRepository.save(pet);

        // Add pet images if provided
        if (petDTO.getImages() != null && !petDTO.getImages().isEmpty()) {
            for (int i = 0; i < petDTO.getImages().size(); i++) {
                PetImage image = new PetImage();
                image.setPet(pet);
                image.setImageUrl(petDTO.getImages().get(i));
                image.setIsPrimary(i == 0); // First image is primary
                petImageRepository.save(image);
            }
        }

        return pet;
    }

    @Override
    @Transactional
    public Pet updatePet(Long id, PetDTO petDTO) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        if (petDTO.getName() != null) {
            pet.setName(petDTO.getName());
        }

        if (petDTO.getSpecies() != null) {
            pet.setSpecies(petDTO.getSpecies());
        }

        if (petDTO.getBreed() != null) {
            pet.setBreed(petDTO.getBreed());
        }

        if (petDTO.getAge() != null) {
            pet.setAge(petDTO.getAge());
        }

        if (petDTO.getGender() != null) {
            pet.setGender(petDTO.getGender());
        }

        if (petDTO.getColor() != null) {
            pet.setColor(petDTO.getColor());
        }

        if (petDTO.getWeight() != null) {
            pet.setWeight(petDTO.getWeight());
        }

        if (petDTO.getDescription() != null) {
            pet.setDescription(petDTO.getDescription());
        }

        if (petDTO.getPrice() != null) {
            pet.setPrice(petDTO.getPrice());
        }

        if (petDTO.getStatus() != null) {
            pet.setStatus(petDTO.getStatus());
        }

        if (petDTO.getImageUrl() != null) {
            pet.setImageUrl(petDTO.getImageUrl());
        }

        pet.setUpdatedAt(LocalDateTime.now());

        pet = petRepository.save(pet);

        // Update pet images if provided
        if (petDTO.getImages() != null && !petDTO.getImages().isEmpty()) {
            // Delete existing images
            petImageRepository.deleteByPetId(id);

            // Add new images
            for (int i = 0; i < petDTO.getImages().size(); i++) {
                PetImage image = new PetImage();
                image.setPet(pet);
                image.setImageUrl(petDTO.getImages().get(i));
                image.setIsPrimary(i == 0); // First image is primary
                petImageRepository.save(image);
            }
        }

        return pet;
    }

    @Override
    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    @Override
    public Page<Pet> getAllPets(Pageable pageable) {
        return petRepository.findAll(pageable);
    }

    @Override
    public Page<Pet> getPetsBySpecies(String species, Pageable pageable) {
        return petRepository.findBySpecies(species, pageable);
    }

    @Override
    public Page<Pet> getPetsByBreed(String breed, Pageable pageable) {
        return petRepository.findByBreed(breed, pageable);
    }

    @Override
    public Page<Pet> getAvailablePets(Pageable pageable) {
        return petRepository.findAvailablePets(pageable);
    }

    @Override
    public Page<Pet> getAvailablePetsBySpecies(String species, Pageable pageable) {
        return petRepository.findAvailablePetsBySpecies(species, pageable);
    }

    @Override
    public Page<Pet> getAvailablePetsBySpeciesAndBreed(String species, String breed, Pageable pageable) {
        return petRepository.findAvailablePetsBySpeciesAndBreed(species, breed, pageable);
    }

    @Override
    public List<String> getAllSpecies() {
        return petRepository.findAllSpecies();
    }

    @Override
    public List<String> getBreedsBySpecies(String species) {
        return petRepository.findBreedsBySpecies(species);
    }

    @Override
    @Transactional
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updatePetStatus(Long id, String status) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        pet.setStatus(status);
        pet.setUpdatedAt(LocalDateTime.now());

        petRepository.save(pet);
    }
}