import { Component } from '@angular/core';
import { ThemeService } from '../../service/theme.service';
import { ListingService } from '../../service/listing.service';
import { Antiquity } from '../../modele/DtoListing';
import { ImageServiceService } from '../../service/image-service.service';
import { forkJoin, mergeMap } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sell',
  templateUrl: './sell.component.html',
  styleUrl: './sell.component.css'
})
export class SellComponent {

  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light
  antiquities: Antiquity[] = [];
  pictures: String[] = [];
  filteredAntiquities: Antiquity[] = []; 
  searchText: string = ''; 
  sortCriteria: string = 'id'; // Default sort by ID

  constructor(private themeService: ThemeService, 
    private listingService: ListingService, private imageService: ImageServiceService, private router: Router) {}

    ngOnInit(): void {
      // Subscribe to Theme event
      this.themeService.theme$.subscribe(theme => {
        this.currentTheme = theme;
      });
    
      // Get all antiquities
      this.listingService.getAllAntiquitiesChecked().subscribe(antiquities => {
        this.antiquities = antiquities;
        this.filteredAntiquities = antiquities; 
        // Iterate over all antiquities
        this.antiquities.forEach(antiquity => {
          this.imageService.getImageFromAntiquity(antiquity.idAntiquity).pipe(
            mergeMap(photoPaths => {
              const urlRequests = photoPaths.map(photoPath =>
                this.imageService.getImageUrl(photoPath)
              );
              return forkJoin(urlRequests);  // Wait for all requests to finish
            })
          ).subscribe(pictures => {
            antiquity.photos = pictures;  // Assign pictures to antiquity
          });
        });
      });

    }
    
    onSearchChange(): void {
      if (this.searchText.trim() === '') {
        this.filteredAntiquities = this.antiquities; // If search is empty, show all antiquities
      } else {
        this.filteredAntiquities = this.antiquities.filter(antiquity =>
          antiquity.titleAntiquity.toLowerCase().includes(this.searchText.toLocaleLowerCase()) ||
          antiquity.descriptionAntiquity.toLowerCase().includes(this.searchText.toLocaleLowerCase())
        );
      }

      // Sort after filtering
      this.onSortChange();
    }

    onSortChange(): void {
      if (this.sortCriteria === 'id') {
        this.filteredAntiquities.sort((a, b) => a.idAntiquity - b.idAntiquity);
      } else if (this.sortCriteria === 'price') {
        this.filteredAntiquities.sort((a, b) => a.priceAntiquity - b.priceAntiquity);
      }
    }

    viewDetails(id: number): void {
      // Redirect to the details page
      this.router.navigate(['/see', id]);
    }
}