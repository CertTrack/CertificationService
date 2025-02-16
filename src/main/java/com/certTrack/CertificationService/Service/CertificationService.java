package com.certTrack.CertificationService.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.certTrack.CertificationService.DTO.ResponseMessage;
import com.certTrack.CertificationService.Entity.Certification;
import com.certTrack.CertificationService.Repository.CertificationRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class CertificationService {

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private CertificationRepository certificationRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${aws.s3.bucket}")
	private String bucket;

	public ResponseEntity<?> findByUserId(int id) {
    	List<Certification> certifications = certificationRepository.findByUserId(id);
        if (certifications.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Certifications not found. Or the user does not exist"));
        }
        return ResponseEntity.ok(certifications);
	}

	public ResponseEntity<?> findByUserIdAndCourseId(int userId, int courseId) {
	    Certification cert = certificationRepository.findByUserIdAndCourseId(userId, courseId).getFirst();
	    try {
	        amazonS3.getObjectMetadata(bucket, cert.getFilePath());

	        S3Object object = amazonS3.getObject(bucket, cert.getFilePath());
	        S3ObjectInputStream inputStream = object.getObjectContent();
	        byte[] content = IOUtils.toByteArray(inputStream);
	        ByteArrayResource arrayResource = new ByteArrayResource(content);

	        return ResponseEntity
	                .ok()
	                .contentLength(content.length)
	                .header("Content-type", "application/octet-stream")
	                .header("Content-disposition", "attachment; filename=\""+"certificate.pdf"+"\"")
	                .body(arrayResource);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body(new ResponseMessage("This certificate does not exist."));
	    } 
	}

	public ResponseEntity<?> findById(int id) {
        Certification certification = certificationRepository.findById(id).orElse(null);
        if (certification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Certification not found."));
        }
		return ResponseEntity.ok(certification);
	}

	public ResponseEntity<?> findByValidationCode(String validationCode) {
		Optional<Certification> certification = certificationRepository.findByValidationCode(validationCode);
		if (certification.isPresent()) {
			return ResponseEntity.ok(certification.get());
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ResponseMessage("Invalid certification code or certification not found."));
	}

	public ResponseMessage saveCertificate(Certification certification) {
		certification = certificationRepository.save(certification);

		byte[] pdfBytes = createCertificatePdf(certification);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfBytes);
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(pdfBytes.length);

		String fileName = certification.getId() + "_" + certification.getUserId() + "_" + certification.getIssuedDate()+".pdf";
		amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata));

		certification.setFilePath(fileName);
		certificationRepository.save(certification);
		return new ResponseMessage("Certificate uploaded successfully");
	}

	private byte[] createCertificatePdf(Certification certification) {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    Document document = new Document(PageSize.A4.rotate());

	    try {
	        PdfWriter.getInstance(document, outputStream);
	        document.open();

	        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA, 30);
	        Font defaultFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
	        Font grayFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.NORMAL, BaseColor.GRAY);
	        Font userNameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 34);
	        Font courseFont = FontFactory.getFont(FontFactory.HELVETICA, 30);

	        Paragraph title = new Paragraph("Course Certificate", titleFont);
	        title.setAlignment(Element.ALIGN_CENTER);
	        document.add(title);

	        document.add(new Paragraph("\n\n\n\n\n"));

	        Paragraph certifiesText = new Paragraph("This certifies that:", grayFont);
	        certifiesText.setAlignment(Element.ALIGN_CENTER);
	        document.add(certifiesText);

	        String query = "SELECT email FROM users WHERE id = ?";
	        String userName = jdbcTemplate.queryForObject(query, String.class, certification.getUserId());
	        Paragraph userNameParagraph = new Paragraph(userName, userNameFont);
	        userNameParagraph.setAlignment(Element.ALIGN_CENTER);
	        document.add(userNameParagraph);

	        document.add(new Paragraph("\n\n"));

	        


	        PdfPCell cell = new PdfPCell(new Phrase("has successfully completed the course by demonstrating theoretical and practical understanding of", grayFont));
	        cell.setBorder(Rectangle.NO_BORDER);
	        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell.setNoWrap(false);
	        PdfPTable table = new PdfPTable(1);
	        table.setWidthPercentage(40);
	        table.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(cell);
	        document.add(table);
//	        Paragraph hasCompletedText = new Paragraph("has successfully completed the course by demonstrating theoretical and practical understanding of", grayFont);
//	        hasCompletedText.setAlignment(Element.ALIGN_CENTER);
//	        document.add(hasCompletedText);

	        String queryOfCourseName = "SELECT name FROM course WHERE id = ?";
	        String courseName = jdbcTemplate.queryForObject(queryOfCourseName, String.class, certification.getCourseId());
	        Paragraph courseNameParagraph = new Paragraph(courseName, courseFont);
	        courseNameParagraph.setAlignment(Element.ALIGN_CENTER);
	        document.add(courseNameParagraph);

	        document.add(new Paragraph("\n\n\n\n\n"));

	        Paragraph details = new Paragraph("CertTrack corp. by Dmytro Trofimov\nDate: " + certification.getIssuedDate() + "\nCertification ID: " + certification.getId() + "\nValidation code: " + certification.getValidationCode(), defaultFont);
	        details.setAlignment(Element.ALIGN_RIGHT);
	        document.add(details);


	        Image image = Image.getInstance("src/certificateElement/photo_5269732161760651566_x.jpg");
	        image.setAbsolutePosition(36, 36);
	        image.scaleToFit(100, 100);
	        document.add(image);
	        document.close();
	    } catch (DocumentException e) {
	        e.printStackTrace();
	    } catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	    return outputStream.toByteArray();
	}

	public ResponseEntity<?> deleteCertificateById(int id) {
    	Certification certification = (Certification) this.findById(id).getBody();
        if (certification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("Certification not found."));
        }
		amazonS3.deleteObject(bucket, certification.getFilePath());
		certificationRepository.deleteById(id);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseMessage("Certification succesfuly deleted."));
	}
}