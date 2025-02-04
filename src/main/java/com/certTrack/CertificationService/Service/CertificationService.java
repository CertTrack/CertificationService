package com.certTrack.CertificationService.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.certTrack.CertificationService.DTO.ResponseMessage;
import com.certTrack.CertificationService.Entity.Certification;
import com.certTrack.CertificationService.Repository.CertificationRepository;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class CertificationService {

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private CertificationRepository certificationRepository;

	@Value("${aws.s3.bucket}")
	private String bucket;

	public List<Certification> findByUserId(int id) {
		return certificationRepository.findByUserId(id);
	}

	public Certification findByUserIdAndCourseId(int userId, int courseId) {
		return certificationRepository.findByUserIdAndCourseId(userId, courseId).getFirst();
	}

	public Certification findById(int id) {
		Certification cert = certificationRepository.findById(id).orElse(null);
		return cert;
	}

	public ResponseEntity<?> findByValidationCode(String validationCode) {
		Optional<Certification> certification = certificationRepository.findByValidationCode(validationCode);
		if (certification.isPresent()) {
			return ResponseEntity.ok(certification.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ResponseMessage("Invalid certification code or certification not found."));
	}

	public void saveCertificate(Certification certification) {
		certification = certificationRepository.save(certification);

		byte[] pdfBytes = createCertificatePdf(certification);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(pdfBytes.length);

		String fileName = certification.getId() + "_" + certification.getUserId() + "_" + certification.getIssuedDate()+".pdf";
		amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));

		certification.setFilePath(fileName);
		certificationRepository.save(certification);
	}

	private byte[] createCertificatePdf(Certification certification) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4.rotate()); // горизонтальна орієнтація

		try {
			PdfWriter.getInstance(document, outputStream);
			document.open();

			// Додаємо заголовок
			Paragraph title = new Paragraph("Course Certificate");
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);

			// Додаємо дані з об'єкту Certification
			document.add(new Paragraph("\n\n")); // пропуск кількох рядків
			document.add(new Cell("This certifies that:"));
			document.add(new Paragraph(certification.getUserId()));
			document.add(new Paragraph("\n\n")); // пропуск кількох рядків
			document.add(new Paragraph("has successfully completed the course by demonstrating theoretical and practical understanding of"));
			document.add(new Paragraph(certification.getCourseId()));
			document.add(new Paragraph("\n\n")); // пропуск кількох рядків
			document.add(new Paragraph("Date: " + certification.getIssuedDate()));
			document.add(new Paragraph("Certification ID: " + certification.getId()));
			document.add(new Paragraph("Validation code: " + certification.getValidationCode()));

			document.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		return outputStream.toByteArray();
	}

//    public void saveCertificate(MultipartFile file, Certification certification) {
//    	certification = certificationRepository.save(certification);
//    	File fileObj = convertMultipartFileToFile(file);
//    	String fileName = certification.getId() + "_" + file.getOriginalFilename();
//        amazonS3.putObject(new PutObjectRequest(bucket, fileName, fileObj));
//        	    /*.withCannedAcl(CannedAccessControlList.PublicRead)*/
//        fileObj.delete();
//        certification.setFilePath(fileName);
//        certificationRepository.save(certification);
// 
//    }

//	private File convertMultipartFileToFile(MultipartFile file) {
//		File convertedFile = new File(file.getOriginalFilename());
//		try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
//			fos.write(file.getBytes());
//		} catch (IOException e) {
//			System.out.println("Error converting multipartfile to file");
//
//		}
//		return convertedFile;
//	}

	public void deleteCertificateById(int id) {
		Certification certification = certificationRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Certification not found."));
		amazonS3.deleteObject(bucket, certification.getFilePath());
		certificationRepository.deleteById(id);
	}
}