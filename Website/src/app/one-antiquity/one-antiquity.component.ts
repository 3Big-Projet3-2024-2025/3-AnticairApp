import { Component, Input } from '@angular/core';
import { Antiquity } from '../../modele/DtoListing';

@Component({
  selector: 'app-one-antiquity',
  templateUrl: './one-antiquity.component.html',
  styleUrl: './one-antiquity.component.css'
})
export class OneAntiquityComponent {

  @Input() antiquity : Antiquity | undefined;
}
