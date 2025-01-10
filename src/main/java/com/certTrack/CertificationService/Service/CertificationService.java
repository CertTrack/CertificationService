package com.certTrack.CertificationService.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.certTrack.CertificationService.DTO.CourseDTO;
import com.certTrack.CertificationService.DTO.ResponseMessage;
import com.certTrack.CertificationService.Entity.Certification;
import com.certTrack.CertificationService.Repository.CertificationRepository;

@Service
public class CertificationService {

    @Autowired
    private RestTemplate restTemplate;
   
    @Autowired 
    private AmazonS3 amazonS3;

    @Autowired
    private CertificationRepository certificationRepository;

    
    @Value("${aws.s3.bucket}")
    private String bucket;

    
    

    
    
    public String getCourseNameById(int courseId) {
        String url = "http://localhost/courses/id/" + courseId;
        CourseDTO course = restTemplate.getForObject(url, CourseDTO.class);
        return course != null ? course.getName() : null;
    }
    

	public List<Certification> findByUserId(int id) {
		return certificationRepository.findByUserId(id);
	}
	
	
    public Certification findById(int id) {
    	Certification cert =  certificationRepository.findById(id).orElse(null);
    	return cert;
    }

    
    
    
    public ResponseEntity<?> findByValidationCode(String validationCode) {
        Optional<Certification> certification = certificationRepository.findByValidationCode(validationCode);
        if (certification.isPresent()) {
        	return ResponseEntity.ok(certification.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Invalid certification code or certification not found."));
    }

    
    
    public void saveCertificate(MultipartFile file, Certification certification) {
    	certification = certificationRepository.save(certification);
    	File fileObj = convertMultipartFileToFile(file);
    	String fileName = certification.getId() + "_" + file.getOriginalFilename();
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, fileObj));
        	    /*.withCannedAcl(CannedAccessControlList.PublicRead)*/
        fileObj.delete();
        certification.setFilePath(fileName);
        certificationRepository.save(certification);
 
    }
    
    
    private File convertMultipartFileToFile(MultipartFile file) {
    	File convertedFile = new File(file.getOriginalFilename());
    	try(FileOutputStream fos = new FileOutputStream(convertedFile)) {
    		fos.write(file.getBytes());
    	}catch(IOException e){
    		System.out.println("Error converting multipartfile to file");
    		
    	}
    	return convertedFile;
    }


    public void deleteCertificateById(int id) {
        Certification certification = certificationRepository.findById(id).orElseThrow(() -> 
            new RuntimeException("Certification not found."));
        amazonS3.deleteObject(bucket, certification.getFilePath());
        certificationRepository.deleteById(id);
    }
}