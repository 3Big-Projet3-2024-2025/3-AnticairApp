import { Component, Input } from '@angular/core';
import { Antiquity } from '../../modele/DtoListing';

@Component({
  selector: 'app-check-listing',
  templateUrl: './check-listing.component.html',
  styleUrl: './check-listing.component.css'
})
export class CheckListingComponent {

  @Input() antiquity : Antiquity | undefined;

}
