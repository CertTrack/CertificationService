package com.certTrack.CertificationService.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.certTrack.CertificationService.Entity.Certification;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Integer> {

    Optional<Certification> findByValidationCode(String validationCode);

	List<Certification> findByUserId(int id);
}