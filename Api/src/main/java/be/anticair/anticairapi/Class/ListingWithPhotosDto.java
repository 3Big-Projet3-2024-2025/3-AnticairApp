package be.anticair.anticairapi.Class;

import lombok.*;


import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListingWithPhotosDto {

    private Integer idAntiquity;
    private Double priceAntiquity;
    private String descriptionAntiquity;
    private String titleAntiquity;
    private String mailMember;
    private Integer state;
    private Boolean estAffiche;
    private Integer idMember;
    private List<String> photos;

    public ListingWithPhotosDto(Listing listing, List<PhotoAntiquity> photos) {
        this.idAntiquity = listing.getIdAntiquity();
        this.priceAntiquity = listing.getPriceAntiquity();
        this.descriptionAntiquity = listing.getDescriptionAntiquity();
        this.titleAntiquity = listing.getTitleAntiquity();
        this.mailMember = listing.getMailMember();
        this.state = listing.getState();
        this.estAffiche = listing.getEstAffiche();
        this.idMember = listing.getIdMember();
        this.photos = photos.stream()
                .map(PhotoAntiquity::getPathPhoto)
                .toList();
    }

    // Getters and setters
}

