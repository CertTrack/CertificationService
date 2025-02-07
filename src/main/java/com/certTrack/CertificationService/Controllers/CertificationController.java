package com.certTrack.CertificationService.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.certTrack.CertificationService.DTO.ResponseMessage;
import com.certTrack.CertificationService.Entity.Certification;
import com.certTrack.CertificationService.Service.CertificationService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/certifications")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;

    @PreAuthorize("hasAuthority('ROLE_SERVICE')")
    @PostMapping("/upload")
    public ResponseMessage uploadCertificateFile(
            @RequestParam int userId,
            @RequestParam int courseId
    		/*@RequestPart("metadata") String metadataJson,
            @RequestPart("file") MultipartFile file*/) throws JsonProcessingException {
        Certification metadata = new Certification();
        metadata.setUserId(userId);
        metadata.setCourseId(courseId);
        return certificationService.saveCertificate(metadata);
    }
    
    @GetMapping("/validate")
    public ResponseEntity<?> validateCertification(@RequestParam String validationCode) {
        return certificationService.findByValidationCode(validationCode);
    }
    
    
    
    @GetMapping("/id")
    public ResponseEntity<?> getCertificate(@RequestParam int id) {
        Certification certification = certificationService.findById(id);
        if (certification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Certification not found."));
        }
        return ResponseEntity.ok(certification);
    }

    
    @GetMapping("/user")
    public List<Certification> getCertificatesByUserId(@RequestParam int id) {
    	return certificationService.findByUserId(id);
    }
    
    @GetMapping("/usercourse")
    public Certification getCertificatesByUserIdAndCourseId(@RequestParam int userId, @RequestParam int courseId) {
    	return certificationService.findByUserIdAndCourseId(userId, courseId);
    }
    
    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> deleteCertificate(@RequestParam int id){
    	Certification certification = certificationService.findById(id);
        if (certification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Certification not found."));
        }
        certificationService.deleteCertificateById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseMessage("Certification succesfuly deleted."));
    }
}