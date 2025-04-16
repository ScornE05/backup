package com.example.demo.Controller;

import com.example.demo.DTO.ApiResponse;
import com.example.demo.DTO.PetDTO;
import com.example.demo.entity.Pet;
import com.example.demo.entity.PetImage;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.PetImageRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.service.PetService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;
    private final PetImageRepository petImageRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PetController(PetService petService,
                         PetImageRepository petImageRepository,
                         ReviewRepository reviewRepository,
                         ModelMapper modelMapper) {
        this.petService = petService;
        this.petImageRepository = petImageRepository;
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetDTO> createPet(@Valid @RequestBody PetDTO petDTO) {
        Pet pet = petService.createPet(petDTO);
        PetDTO responseDTO = convertToDto(pet);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDTO> getPetById(@PathVariable Long id) {
        Pet pet = petService.getPetById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with id: " + id));

        PetDTO petDTO = convertToDto(pet);

        // Get average rating and review count
        Double averageRating = reviewRepository.getAverageRatingByPetId(id);
        Long reviewCount = reviewRepository.countByPetId(id);

        petDTO.setAverageRating(averageRating);
        petDTO.setReviewCount(reviewCount);

        return ResponseEntity.ok(petDTO);
    }

    @GetMapping
    public ResponseEntity<Page<PetDTO>> getAllPets(Pageable pageable) {
        Page<Pet> pets = petService.getAllPets(pageable);
        Page<PetDTO> petDTOs = pets.map(this::convertToDto);

        return ResponseEntity.ok(petDTOs);
    }

    @GetMapping("/species/{species}")
    public ResponseEntity<Page<PetDTO>> getPetsBySpecies(@PathVariable String species, Pageable pageable) {
        Page<Pet> pets = petService.getPetsBySpecies(species, pageable);
        Page<PetDTO> petDTOs = pets.map(this::convertToDto);

        return ResponseEntity.ok(petDTOs);
    }

    @GetMapping("/breed/{breed}")
    public ResponseEntity<Page<PetDTO>> getPetsByBreed(@PathVariable String breed, Pageable pageable) {
        Page<Pet> pets = petService.getPetsByBreed(breed, pageable);
        Page<PetDTO> petDTOs = pets.map(this::convertToDto);

        return ResponseEntity.ok(petDTOs);
    }

    @GetMapping("/available")
    public ResponseEntity<Page<PetDTO>> getAvailablePets(Pageable pageable) {
        Page<Pet> pets = petService.getAvailablePets(pageable);
        Page<PetDTO> petDTOs = pets.map(this::convertToDto);

        return ResponseEntity.ok(petDTOs);
    }

    @GetMapping("/available/species/{species}")
    public ResponseEntity<Page<PetDTO>> getAvailablePetsBySpecies(@PathVariable String species, Pageable pageable) {
        Page<Pet> pets = petService.getAvailablePetsBySpecies(species, pageable);
        Page<PetDTO> petDTOs = pets.map(this::convertToDto);

        return ResponseEntity.ok(petDTOs);
    }

    @GetMapping("/available/species/{species}/breed/{breed}")
    public ResponseEntity<Page<PetDTO>> getAvailablePetsBySpeciesAndBreed(
            @PathVariable String species,
            @PathVariable String breed,
            Pageable pageable) {
        Page<Pet> pets = petService.getAvailablePetsBySpeciesAndBreed(species, breed, pageable);
        Page<PetDTO> petDTOs = pets.map(this::convertToDto);

        return ResponseEntity.ok(petDTOs);
    }

    @GetMapping("/species")
    public ResponseEntity<List<String>> getAllSpecies() {
        List<String> species = petService.getAllSpecies();
        return ResponseEntity.ok(species);
    }

    @GetMapping("/species/{species}/breeds")
    public ResponseEntity<List<String>> getBreedsBySpecies(@PathVariable String species) {
        List<String> breeds = petService.getBreedsBySpecies(species);
        return ResponseEntity.ok(breeds);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetDTO> updatePet(@PathVariable Long id, @Valid @RequestBody PetDTO petDTO) {
        Pet updatedPet = petService.updatePet(id, petDTO);
        PetDTO responseDTO = convertToDto(updatedPet);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deletePet(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.ok(new ApiResponse(true, "Pet deleted successfully"));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse> updatePetStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        petService.updatePetStatus(id, status);
        return ResponseEntity.ok(new ApiResponse(true, "Pet status updated successfully"));
    }

    private PetDTO convertToDto(Pet pet) {
        PetDTO petDTO = modelMapper.map(pet, PetDTO.class);

        // Set price values
        petDTO.setPrice(pet.getPrice());
        if (pet.getWeight() != null) {
            petDTO.setWeight(pet.getWeight());
        }

        // Set images
        List<PetImage> images = petImageRepository.findByPetId(pet.getId());
        if (images != null && !images.isEmpty()) {
            petDTO.setImages(images.stream()
                    .map(PetImage::getImageUrl)
                    .collect(Collectors.toList()));
        }

        return petDTO;
    }
}
