# Certification Service

The **Certification Service** manages the uploading, validation, and retrieval of course certifications. It is integrated with AWS S3 for secure storage of certification files and supports user-specific and admin-level operations.

## Key Features

- **Certification Upload:** Allows uploading of certification files with associated metadata.
- **Validation:** Verifies the authenticity of a certification using a unique validation code.
- **User-Centric Operations:** Retrieves certifications based on user and course IDs.
- **Admin Operations:** Provides capabilities for managing certifications, including deletion.
- **Security:** Implements role-based access control with Spring Security.

## Technologies Used

- **Spring Boot**: Core framework for the service.
- **AWS S3**: Used for storing certification files securely.
- **Spring Security**: Handles authentication and authorization.
- **Jackson ObjectMapper**: Parses and processes JSON metadata.
- **RESTful API Design**: Provides a clean and structured API interface.

---

## Endpoints

### General Endpoints

#### `POST /certifications/upload`
**Description:** Uploads a certification file with associated metadata.

**Request Parts:**
- `metadata` (String): JSON string containing certification details.
- `file` (MultipartFile): Certification file to upload.

**Sample Metadata:**
```json
{
  "userId":1,
  "courseId":3
}
```

**Response:**
```json
{
  "message": "File uploaded successfully"
}
```

### Validation and Retrieval Endpoints

#### `GET /certifications/validate`
**Description:** Validates a certification using its unique validation code.

**Request Parameters:**
- `validationCode` (String): The validation code of the certification.

**Response:**
```json
{
  "id": 1,
  "userId": 123,
  "courseName": "Spring Boot Basics",
  "issuedDate": "2025-01-01",
  "validationCode": "LA9eJuUcVPP-NloA10rIKQ4ZAp7_5er3emXStqh4XfY"
}
```

#### `GET /certifications/id`
**Description:** Retrieves certification details by ID.

**Request Parameters:**
- `id` (int): The unique ID of the certification.

**Response:**
```json
{
  "id": 1,
  "userId": 123,
  "courseName": "Spring Boot Basics",
  "issuedDate": "2025-01-01",
  "validationCode": "LA9eJuUcVPP-NloA10rIKQ4ZAp7_5er3emXStqh4XfY"
}
```

#### `GET /certifications/user`
**Description:** Retrieves all certifications for a specific user by their user ID.

**Request Parameters:**
- `id` (int): The user ID.

**Response:**
```json
[
  {
    "id": 1,
    "courseName": "Spring Boot Basics",
    "issuedDate": "2025-01-01",
    "validationCode": "LA9eJuUcVPP-NloA10rIKQ4ZAp7_5er3emXStqh4XfY"
  }
]
```

#### `GET /certifications/usercourse`
**Description:** Retrieves a certification for a specific user and course.

**Request Parameters:**
- `userId` (int): The user ID.
- `courseId` (int): The course ID.

**Response:**
```json
{
  "id": 1,
  "courseName": "Spring Boot Basics",
  "issuedDate": "2025-01-01",
  "validationCode": "LA9eJuUcVPP-NloA10rIKQ4ZAp7_5er3emXStqh4XfY"
}
```

### Admin-Specific Endpoints

#### `DELETE /certifications/admin/delete`
**Description:** Deletes a certification by its ID (Admin only).

**Request Parameters:**
- `id` (int): The unique ID of the certification to delete.

**Response:**
```json
{
  "message": "Certification successfully deleted."
}
```

---

## Security Configuration

- **JWT-Based Authentication:** Secures endpoints and ensures role-based access.
- **Role-Based Access Control:** Limits admin-specific operations to users with the `ADMIN` role.
- **Stateless Session Management:** Ensures each request is independently authenticated.

---

## How to Run

1. Clone the repository.
2. Configure AWS S3 credentials in the application properties.
3. Build and run the application using Maven or your preferred IDE.
4. The service will be available at `http://localhost:8083`.

---

## Notes

- Ensure AWS S3 permissions are correctly set up for file upload and retrieval.
- The `validationCode` must be unique for each certification.
- Metadata is case-sensitive; ensure it aligns with the expected structure.
- File paths are stored in the database but managed on AWS S3.

---

## Future Enhancements

- Implement a feature for downloading certifications.
- Add support for updating certification details.
- Introduce expiration dates for certifications.
