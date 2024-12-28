import {Component, OnInit} from '@angular/core';
import {ListingService} from '../../service/listing.service';
import {ImageServiceService} from '../../service/image-service.service';
import {AuthService} from '../../service/auth.service';
import {Antiquity} from '../../modele/DtoListing';

@Component({
  selector: 'app-list-listing-verify',
  templateUrl: './list-listing-verify.component.html',
  styleUrl: './list-listing-verify.component.css'
})
export class ListListingVerifyComponent implements OnInit {
  antiquities: Antiquity[] = []


  constructor(private listingService: ListingService,private imageService:ImageServiceService,private authService: AuthService) { }

  ngOnInit(): void {

    let userinfo = this.authService.getUserDetails();

    this.listingService.getListingVerify(userinfo['email']).subscribe(res => {
      this.antiquities = res;
      this.antiquities.forEach(antiquity => {
        let photos: string[] = []
        antiquity.photos.forEach(photo => {
          this.imageService.getImageUrl(photo).subscribe(image => {
            photos.push(image.toString());
          })
        })
        antiquity.photos = photos;
      });
    })


  }

  viewDetails(id: number): void {
    // Action to view details (e.g., navigate to another route)
    console.log(`View details for antiquity with ID: ${id}`);
  }
}
