package com.certTrack.CertificationService.Controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.certTrack.CertificationService.DTO.ResponseMessage;
import com.certTrack.CertificationService.Entity.Certification;
import com.certTrack.CertificationService.Service.CertificationService;
import com.fasterxml.jackson.databind.ObjectMapper;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CertificationControllerTest {

	
	@Autowired
	MockMvc api;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@Autowired
	CertificationService certificationService;

	@Test
	public void NotAuthorizedUserCanNotSeeAnyEndpoint() throws Exception {
		api.perform(get("/certifications/")).andExpect(status().is4xxClientError());
		api.perform(get("/certifications/id?id=1")).andExpect(status().is4xxClientError());
		api.perform(get("/certifications/validate?validationCode=ABC123DEF")).andExpect(status().is4xxClientError());
		api.perform(delete("/certifications/admin/delete?id=1")).andExpect(status().is4xxClientError());
	}
	@WithMockUser
	@Test
	public void AuthorizedUserCanSeeCertificationsById() throws Exception{
		Certification certification = new Certification(16,7,"2025-01-14 09:11", "ts5XBCr1oLUNLVGoxv54DxYz7FhHSIFSlc1dYnmauFs", "7_2025-01-14_09-04.png"); 
		 String responseJson = objectMapper.writeValueAsString(certification);
		 api.perform(get("/certifications/id?id=7")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(content().json(responseJson));
	}
	
	@WithMockUser
	@Test
	public void AuthorizedUserCanSeeCertificationsByUserId() throws Exception{
		 List<Certification> list = List.of(new Certification(16,7,"2025-01-14 09:11", "ts5XBCr1oLUNLVGoxv54DxYz7FhHSIFSlc1dYnmauFs", "7_2025-01-14_09-04.png")); 
		 String responseJson = objectMapper.writeValueAsString(list);
		 api.perform(get("/certifications/user?id=16")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(content().json(responseJson));
	}
	
	@WithMockUser
	@Test
	public void AuthorizedUserCanSeeCertificationsByUserIdAndCourseId() throws Exception{
		Certification certification = new Certification(16,7,"2025-01-14 09:11", "ts5XBCr1oLUNLVGoxv54DxYz7FhHSIFSlc1dYnmauFs", "7_2025-01-14_09-04.png"); 
		 String responseJson = objectMapper.writeValueAsString(certification);
		 api.perform(get("/certifications/usercourse?userId=16&courseId=7")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(content().json(responseJson));
	}
	@WithMockUser
	@Test
	public void AuthorizedUserCanValidateFAKECertification() throws Exception{
		ResponseEntity<?> message = certificationService.findByValidationCode("ABC123DEFasd");

		String responseJson = objectMapper.writeValueAsString(message.getBody());
		api.perform(get("/certifications/validate?validationCode=ABC123DEFasd")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isNotFound())
	            .andExpect(content().json(responseJson));
	}
	@WithMockUser
	@Test
	public void AuthorizedUserCanValidateREALCertification() throws Exception{
		ResponseEntity<?> message = certificationService.findByValidationCode("ts5XBCr1oLUNLVGoxv54DxYz7FhHSIFSlc1dYnmauFs");
		String responseJson = objectMapper.writeValueAsString(message.getBody());
		api.perform(get("/certifications/validate?validationCode=ts5XBCr1oLUNLVGoxv54DxYz7FhHSIFSlc1dYnmauFs")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(content().json(responseJson));
		
	}
	@WithMockUser(auth = "ROLE_ADMIN")
	@Test
	public void AdminCanDeleteCourses() throws Exception{
		ResponseEntity<ResponseMessage> message = ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Certification not found."));
		 String responseJson = objectMapper.writeValueAsString(message.getBody());
		 api.perform(delete("/certifications/admin/delete?id=100")
	            .contentType(MediaType.APPLICATION_JSON))
	            .andExpect(status().is4xxClientError())
	            .andExpect(content().json(responseJson));
	}
}