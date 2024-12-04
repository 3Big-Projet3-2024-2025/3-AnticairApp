package be.anticair.anticairapi.keycloak.service;

import be.anticair.anticairapi.Class.PhotoAntiquity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoAntiquityRepository extends JpaRepository<PhotoAntiquity, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM PhotoAntiquity p WHERE p.idAntiquity = ?1")
    void deleteByIdAntiquity(Integer idAntiquity);
}