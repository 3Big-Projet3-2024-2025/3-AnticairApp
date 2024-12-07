package be.anticair.anticairapi.Class;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Photo_Antiquity")
public class PhotoAntiquity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_photo")
    private Integer idPhoto;

    @Column(name = "path_photo", unique = true, nullable = false)
    private String pathPhoto;

    @Column(name = "id_antiquity", nullable = false)
    private Integer idAntiquity;

    // Getters et setters
    public Integer getIdPhoto() {
        return idPhoto;
    }

    public void setIdPhoto(Integer idPhoto) {
        this.idPhoto = idPhoto;
    }

    public String getPathPhoto() {
        return pathPhoto;
    }

    public void setPathPhoto(String pathPhoto) {
        this.pathPhoto = pathPhoto;
    }

    public Integer getIdAntiquity() {
        return idAntiquity;
    }

    public void setIdAntiquity(Integer idAntiquity) {
        this.idAntiquity = idAntiquity;
    }
}
