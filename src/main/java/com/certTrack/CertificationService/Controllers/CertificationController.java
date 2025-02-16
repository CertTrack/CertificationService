package com.certTrack.CertificationService.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.certTrack.CertificationService.DTO.ResponseMessage;
import com.certTrack.CertificationService.Entity.Certification;
import com.certTrack.CertificationService.Security.UserPrincipal;
import com.certTrack.CertificationService.Service.CertificationService;

@RestController
@RequestMapping("/certifications")
public class CertificationController {

    @Autowired
    private CertificationService certificationService;

    @PostMapping("/upload")
    public ResponseMessage uploadCertificateFile(
            @RequestParam int userId,
            @RequestParam int courseId) {
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
        return certificationService.findById(id);
    }

    
    @GetMapping("/user")
    public ResponseEntity<?> getCertificatesByUserId(@RequestParam int id) {
        return certificationService.findByUserId(id);
    }
    
    @GetMapping("/usercourse")
    public ResponseEntity<?> getCertificatesByUserIdAndCourseId(@AuthenticationPrincipal UserPrincipal user, @RequestParam int courseId) {
    	return certificationService.findByUserIdAndCourseId(user.getUserId(), courseId);
    } 
    
    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> deleteCertificate(@RequestParam int id){
        return certificationService.deleteCertificateById(id);
    }
}