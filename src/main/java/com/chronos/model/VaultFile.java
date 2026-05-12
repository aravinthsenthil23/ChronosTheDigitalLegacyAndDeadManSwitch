package com.chronos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "vault_files")
public class VaultFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    private String fileName;
    private String fileType; // e.g., "application/pdf" or "image/png"

    // --- THE ENCRYPTED CONTENT ---

    @Lob // Tells Java this is a Large Object
    @Column(columnDefinition = "LONGBLOB") // Tells MySQL to allow huge files
    private byte[] fileData;

    // --- RELATIONSHIP ---
    // Many files belong to One User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Prevents infinite loops when converting to JSON
    private User user;

    // --- CONSTRUCTORS ---
    public VaultFile() {}

    public VaultFile(String fileName, String fileType, byte[] fileData, User user) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
        this.user = user;
    }

    // --- GETTERS AND SETTERS ---

    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}