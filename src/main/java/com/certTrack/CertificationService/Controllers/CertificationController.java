package com.certTrack.CertificationService.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.certTrack.CertificationService.DTO.ResponseMessage;
import com.certTrack.CertificationService.Entity.Certification;
import com.certTrack.CertificationService.Service.CertificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/certifications")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;


    @PostMapping("/upload")
    public ResponseMessage uploadCertificateFile(
            @RequestPart("metadata") String metadataJson,
            @RequestPart("file") MultipartFile file) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Certification metadata = objectMapper.readValue(metadataJson, Certification.class);

        certificationService.saveCertificate(file, metadata);
        return new ResponseMessage("File uploaded successfully");
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