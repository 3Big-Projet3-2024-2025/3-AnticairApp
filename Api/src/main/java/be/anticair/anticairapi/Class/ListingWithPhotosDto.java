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
    private String mailAntiquarian;
    private List<String> photos;

    public ListingWithPhotosDto(Listing listing, List<PhotoAntiquity> photos) {
        this.idAntiquity = listing.getIdAntiquity();
        this.priceAntiquity = listing.getPriceAntiquity();
        this.descriptionAntiquity = listing.getDescriptionAntiquity();
        this.titleAntiquity = listing.getTitleAntiquity();
        this.mailMember = listing.getMailSeller();
        this.state = listing.getState();
        this.estAffiche = listing.getIsDisplay();
        this.mailAntiquarian = listing.getMailAntiquarian();
        this.photos = photos.stream()
                .map(PhotoAntiquity::getPathPhoto)
                .toList();
    }

}

