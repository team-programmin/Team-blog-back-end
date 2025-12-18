package com.blog.eu.service;




import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.blog.eu.auth.repository.UserRepository;
import com.blog.eu.model.Photo;
import com.blog.eu.model.User;
import com.blog.eu.repo.PhotoRepository;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ProfileService {

    private final UserRepository userRepo;
    private final PhotoRepository photoRepo;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    public ProfileService(UserRepository userRepo, PhotoRepository photoRepo) {
        this.userRepo = userRepo;
        this.photoRepo = photoRepo;
    }

    public User getMe(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public User updateMe(Long userId, String displayName, String bio, String location, String website) {
        var u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (displayName != null) u.setDisplayName(displayName);
        if (bio != null) u.setBio(bio);
        if (location != null) u.setLocation(location);
        if (website != null) u.setWebsite(website);
        return userRepo.save(u);
    }

    public User uploadPhoto(Long userId, MultipartFile file, boolean isAvatar) throws IOException {
        var u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String ext = getExtension(file.getOriginalFilename());
        String key = UUID.randomUUID() + (ext != null ? "." + ext : "");
        File dest = new File(uploadDir, key);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        String url = baseUrl + "/uploads/" + key;

        Photo p = new Photo();
        p.setKey(key);
        p.setUrl(url);
        p.setAvatar(isAvatar);
        p.setUser(u);
        photoRepo.save(p);

        // substitui a foto anterior
        u.setPhotos(p);
        if (isAvatar) u.setAvatarUrl(url);

        return userRepo.save(u);
    }

    public User setAvatar(Long userId, String photoKey) {
        var u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        var photo = photoRepo.findByKey(photoKey).orElseThrow(() -> new RuntimeException("Foto não encontrada"));
        if (!photo.getUser().getId().equals(userId)) throw new RuntimeException("Foto não pertence ao usuário");

        photo.setAvatar(true);
        u.setPhotos(photo);
        u.setAvatarUrl(photo.getUrl());

        return userRepo.save(u);
    }

   public User deletePhoto(Long userId) {
    var u = userRepo.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    var photo = u.getPhotos();
    if (photo == null) throw new RuntimeException("Usuário não possui foto");

    boolean wasAvatar = photo.isAvatar();
    photoRepo.delete(photo);

    u.setPhotos(null);
    if (wasAvatar) u.setAvatarUrl(null);

    return userRepo.save(u);
}


    private String getExtension(String filename) {
        if (filename == null) return null;
        int idx = filename.lastIndexOf('.');
        if (idx < 0) return null;
        return filename.substring(idx + 1).toLowerCase();
    }
}

