package com.chronos.controller;

import com.chronos.model.User;
import com.chronos.model.VaultFile;
import com.chronos.repository.UserRepository;
import com.chronos.repository.VaultFileRepository;
import com.chronos.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vault")
@CrossOrigin(origins = "*") // Allows HTML frontend access
public class VaultController {

    @Autowired
    private VaultFileRepository fileRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CryptoService cryptoService; // We will write this next!

    // ==========================================
    // 1. UPLOAD & ENCRYPT API
    // ==========================================
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("email") String email,
                                        @RequestParam("file") MultipartFile file) {
        try {
            Optional<User> userOpt = userRepo.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            // A. Get Raw Data
            byte[] originalBytes = file.getBytes();

            // B. ENCRYPT DATA (The Security Layer)
            // We use a secret key derived from the User's ID (or a fixed system key for simplicity)
            byte[] encryptedBytes = cryptoService.encrypt(originalBytes, "SecretKey_" + email);

            // C. Save to Database
            VaultFile vaultFile = new VaultFile();
            vaultFile.setFileName(file.getOriginalFilename());
            vaultFile.setFileType(file.getContentType());
            vaultFile.setFileData(encryptedBytes); // Storing ENCRYPTED data, not raw
            vaultFile.setUser(userOpt.get());

            fileRepo.save(vaultFile);

            return ResponseEntity.ok("File Encrypted & Secured in Vault!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Encryption Failed: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. LIST FILES API (For Dashboard)
    // ==========================================
    @GetMapping("/list")
    public ResponseEntity<?> listFiles(@RequestParam String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) return ResponseEntity.badRequest().body("User not found");

        // Return list of files belonging to this user
        List<VaultFile> files = fileRepo.findByUser(userOpt.get());
        return ResponseEntity.ok(files);
    }

    // ==========================================
    // 3. DOWNLOAD & DECRYPT API
    // ==========================================
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable Long fileId, @RequestParam String email) {
        Optional<VaultFile> fileOpt = fileRepo.findById(fileId);

        if (fileOpt.isPresent()) {
            VaultFile file = fileOpt.get();

            try {
                // A. DECRYPT DATA (On the fly)
                // The database has garbage text. We turn it back to a real file here.
                byte[] decryptedBytes = cryptoService.decrypt(file.getFileData(), "SecretKey_" + email);

                // B. Return as Downloadable Resource
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(file.getFileType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                        .body(new ByteArrayResource(decryptedBytes));

            } catch (Exception e) {
                return ResponseEntity.status(500).body(null);
            }
        }
        return ResponseEntity.notFound().build();
    }
}