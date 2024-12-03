package be.anticair.anticairapi.keycloak.service;

import org.springframework.beans.factory.annotation.Autowired;

import be.anticair.anticairapi.Class.Listing;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for performing listing-related operations.
 *
 * @Author Blommaert Youry, Neve Thierry
 */

@Service
public class ListingService {

    public ListingService() {
    }

    @Autowired
    private ListingRepository ListingRepository;

    public Optional<Listing> getAntiquityById(Long id) {
        return ListingRepository.findById(id);
    }

    public Listing updateListing(Long id, Listing updatedListing) {
        return ListingRepository.findById(id).map(antiquity -> {
            antiquity.setPriceAntiquity(updatedListing.getPriceAntiquity());
            antiquity.setDescriptionAntiquity(updatedListing.getDescriptionAntiquity());
            antiquity.setTitleAntiquity(updatedListing.getTitleAntiquity());
            antiquity.setMailMember(updatedListing.getMailMember());
            antiquity.setState(updatedListing.getState());
            antiquity.setEstAffiche(updatedListing.getEstAffiche());
            return ListingRepository.save(antiquity);
        }).orElseThrow(() -> new RuntimeException("Antiquity not found with id: " + id));
    }
}



