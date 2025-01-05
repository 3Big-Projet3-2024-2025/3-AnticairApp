package be.anticair.anticairapi.keycloak.service;

import be.anticair.anticairapi.Class.Listing;
import be.anticair.anticairapi.Class.PhotoAntiquity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PhotoAntiquityService {

    private PhotoAntiquityRepository photoAntiquityRepository;

    public PhotoAntiquityService(PhotoAntiquityRepository photoAntiquityRepository) {
        this.photoAntiquityRepository = photoAntiquityRepository;
    }

    /**
     * Get all the photos of an antiquity.
     *
     * @param antiquityId The id of the antiquity to get the photos of.
     * @return The list of photos of the antiquity.
     * @Author Neve Thierry
     */
    @Transactional
    public void updatePhotos(Integer antiquityId, List<MultipartFile> photos) throws IOException {
        try {
            // Delete the old images
            photoAntiquityRepository.deleteByIdAntiquity(antiquityId);

            // Add the new images
            for (MultipartFile file : photos) {
                // Verify if the files isn't null
                if (file.isEmpty()) {
                    throw new IOException("The file is empty : " + file.getOriginalFilename());
                }

                // Save the file on the HD
                String filePath = saveFile(file);

                PhotoAntiquity photo = new PhotoAntiquity();
                photo.setPathPhoto(filePath);
                photo.setIdAntiquity(antiquityId);

                // Save on the database
                photoAntiquityRepository.save(photo);
            }
        } catch (IOException e) {
            
            System.err.println("Error with the update of the pictures : " + e.getMessage());
            throw new IOException("Error with the update of the pictures : " + e.getMessage(), e);
        }
    }

    /**
     * Create a new photo for an antiquity.
     *
     * @param antiquity The antiquity to associate the photo with.
     * @param photoFile The photo file to save.
     * @return The created photo.
     * @throws IOException If an error occurs while saving the photo.
     * @author Blommaert Youry
     */
    public PhotoAntiquity createPhotoAntiquity(Listing antiquity, MultipartFile photoFile) throws IOException {
        // Save the photo file
        String photoPath = saveFile(photoFile);

        // Create the PhotoAntiquity object
        PhotoAntiquity photoAntiquity = new PhotoAntiquity();
        photoAntiquity.setPathPhoto(photoPath);
        photoAntiquity.setIdAntiquity(antiquity.getIdAntiquity());

        // Sauvegarder dans la base de données
        return photoAntiquityRepository.save(photoAntiquity);
    }

    /**
     * Create a list of photos for an antiquity.
     *
     * @param antiquity The antiquity to associate the photos with.
     * @param photoFiles The photo files to save.
     * @return The list of created photos.
     * @throws IOException If an error occurs while saving the photos.
     * @author Blommaert Youry
     */
    public List<PhotoAntiquity> createPhotoAntiquities(Listing antiquity, List<MultipartFile> photoFiles) throws IOException {
        List<PhotoAntiquity> savedPhotos = new ArrayList<>();

        for (MultipartFile photoFile : photoFiles) {
            PhotoAntiquity savedPhoto = createPhotoAntiquity(antiquity, photoFile);
            savedPhotos.add(savedPhoto);
        }

        return savedPhotos;
    }

    /**
     * Save a file on the server.
     *
     * @param file The file to save.
     * @return The path to the saved file.
     * @throws IOException If an error occurs while saving the file.
     * @author Blommaert Youry, Neve Thierry
     */
    public String saveFile(MultipartFile file) throws IOException {
        // Define the directory where the files will be saved
        String directory = System.getProperty("user.dir") + "/uploads/";

        // Create the directory if it doesn't exist
        File directoryPath = new File(directory);
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();  // Create the directory
        }

        String originalFileName = file.getOriginalFilename();

        // Replace spaces and special characters in the file name
        String fileName = originalFileName.replaceAll("\\s+", "_").replaceAll("[^a-zA-Z0-9._-]", "");

        // Export the file extension
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            extension = fileName.substring(dotIndex);  // Get the file extension
            fileName = fileName.substring(0, dotIndex);  // Delete the extension from the file name
        }

        // Create the file path
        String filePath = directory + fileName + extension;
        File dest = new File(filePath);

        // If the file already exists, add a timestamp to the file name
        while (dest.exists()) {
            fileName = originalFileName.substring(0, originalFileName.lastIndexOf('.')); // Remove the extension
            String newFileName = fileName + "_" + System.currentTimeMillis() + extension;
            dest = new File(directory + newFileName);
        }

        if (file.isEmpty()) {
            throw new IOException("The file is empty!");
        }

        // Save the file
        file.transferTo(dest);

        // Return the path to the saved file
        return "/uploads/" + dest.getName();
    }

    public List<PhotoAntiquity> findByIdAntiquity(Integer id) {
        // Récupérer les photos associées
        return photoAntiquityRepository.findByIdAntiquity(id);
    }

}
