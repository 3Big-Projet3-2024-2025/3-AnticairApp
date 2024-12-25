import { Component, Input } from '@angular/core';
import { Antiquity } from '../../modele/DtoListing';
import { ThemeService } from '../../service/theme.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ListingService } from '../../service/listing.service';
import { ImageServiceService } from '../../service/image-service.service';
import { forkJoin, mergeMap } from 'rxjs';

@Component({
  selector: 'app-one-antiquity',
  templateUrl: './one-antiquity.component.html',
  styleUrl: './one-antiquity.component.css'
})
export class OneAntiquityComponent {

  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light
  @Input() id!: number; // Input property to accept the id
  antiquity!: Antiquity; // Antiquity object

  constructor(private themeService: ThemeService, private route : ActivatedRoute, private router : Router, private listingService : ListingService, private imageService: ImageServiceService) {}

  ngOnInit(): void {
    // Subscribe to Theme event
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });

     // Get the ID from the route and fetch the antiquity details
    this.route.paramMap.subscribe(params => {
      const id = +params.get('id')!;
      if (id) {
        this.listingService.getAntiquityById(id.toString()).subscribe(antiquity => {
          this.antiquity = antiquity;
          this.loadAntiquityImages(antiquity);
        });
      } else {
        this.router.navigate(['/sell']);
      }
    });
  }

  buyAntiquity(): void {
    // Call the service to buy the antiquity
    this.listingService.buyAntiquity(this.antiquity.idAntiquity).subscribe(response => {
      // Open the paypal link in a new tab
      window.open(response, '_blank');
      this.router.navigate(['/sell']);
    });
  }

  goBack(): void {
    this.router.navigate(['/sell']);
  }

  private loadAntiquityImages(antiquity: Antiquity): void {
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
  }

}
