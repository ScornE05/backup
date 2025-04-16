package com.example.demo.repository;

import com.example.demo.entity.PetImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetImageRepository extends JpaRepository<PetImage, Long> {

    List<PetImage> findByPetId(Long petId);

    Optional<PetImage> findByPetIdAndIsPrimaryTrue(Long petId);

    @Modifying
    @Query("DELETE FROM PetImage pi WHERE pi.pet.id = :petId")
    void deleteByPetId(@Param("petId") Long petId);

    @Modifying
    @Query("UPDATE PetImage pi SET pi.isPrimary = false WHERE pi.pet.id = :petId")
    void resetPrimaryImageForPet(@Param("petId") Long petId);

    @Modifying
    @Query("UPDATE PetImage pi SET pi.isPrimary = true WHERE pi.id = :imageId")
    void setPrimaryImage(@Param("imageId") Long imageId);

    @Query("SELECT COUNT(pi) FROM PetImage pi WHERE pi.pet.id = :petId")
    Long countByPetId(@Param("petId") Long petId);
}
