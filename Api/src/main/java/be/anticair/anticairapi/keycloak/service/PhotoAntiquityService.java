package be.anticair.anticairapi.keycloak.service;

import be.anticair.anticairapi.Class.PhotoAntiquity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class PhotoAntiquityService {

    private PhotoAntiquityRepository photoAntiquityRepository;

    public PhotoAntiquityService(PhotoAntiquityRepository photoAntiquityRepository) {
        this.photoAntiquityRepository = photoAntiquityRepository;
    }

    // Method for update the Antiquity's images
    @Transactional
    public void updatePhotos(Integer antiquityId, List<MultipartFile> photos) throws IOException {
        try {
            // Delete the old images
            photoAntiquityRepository.deleteByIdAntiquity(antiquityId);

            // Add the new images
            for (MultipartFile file : photos) {
                // Verify if the files isn't null
                if (file.isEmpty()) {
                    throw new IOException("Le fichier est vide : " + file.getOriginalFilename());
                }

                // Save the file on the HD
                String filePath = saveFile(file);

                //
                PhotoAntiquity photo = new PhotoAntiquity();
                photo.setPathPhoto(filePath);
                photo.setIdAntiquity(antiquityId);

                // Save on the database
                photoAntiquityRepository.save(photo);
            }
        } catch (IOException e) {
            
            System.err.println("Erreur lors de la mise à jour des photos : " + e.getMessage());
            throw new IOException("Erreur lors de la mise à jour des photos : " + e.getMessage(), e);
        }
    }

    // Method for save the file on the HD (Hard Disk)
    private String saveFile(MultipartFile file) throws IOException {
        // Define the directory
        String directory = System.getProperty("user.dir") + "/uploads/";

        // Make the directory if necessary
        File directoryPath = new File(directory);
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();  // Crée les répertoires parents si nécessaires
        }

        String fileName = file.getOriginalFilename();
// Replace the mistakes characters
        fileName = fileName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._-]", "");
        String filePath = directory + fileName;
        File dest = new File(filePath);

        // Verify if the file already exist
        if (dest.exists()) {
            String newFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            dest = new File(directory + newFileName);
        }

        if (file.isEmpty()) {
            throw new IOException("Le fichier est vide !");
        }
        // Save the file on the HD
        file.transferTo(dest);

        return "/uploads/" + fileName;
    }
}
