import { Component, OnInit } from '@angular/core';
import { ListingService } from '../../service/listing.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Antiquity } from '../../modele/DtoListing';
import { ImageServiceService } from '../../service/image-service.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-edit-listing',
  templateUrl: './edit-listing.component.html',
  styleUrl: './edit-listing.component.css'
})
export class EditListingComponent implements OnInit{
  antiquity!: Antiquity;
  imagePreviews: String[] = []; // Prévisualisations des images sélectionnées
  selectedFiles: File[] = []; // Fichiers sélectionnés pour upload

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private antiquityService: ListingService,
    private imageService: ImageServiceService
  ) {}

  ngOnInit(): void {
    // Récupérer l'ID de l'URL
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadAntiquityById(id);
    }
  }

  // Charger l'antiquité via l'API
  loadAntiquityById(id: string) {
    this.antiquityService.getAntiquityById(id).subscribe({
      next: (data) => {
        this.antiquity = data;

        const imageObservables = data.photos.map(photoPath =>
          this.imageService.getImageUrl(photoPath)
        );

        forkJoin(imageObservables).subscribe({
          next: urls => this.imagePreviews = urls,
          error: err => console.error('Erreur avec les images', err)
        });
      },
      error: err => console.error('Erreur avec l’antiquité', err)
    });
  }


  // Gestion de la sélection de fichiers
  onFileSelect(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      Array.from(input.files).forEach((file) => {
        if (this.imagePreviews.length < 10) {
          this.selectedFiles.push(file);

          // Prévisualisation des fichiers
          const reader = new FileReader();
          reader.onload = (e: ProgressEvent<FileReader>) => {
            if (e.target?.result) {
              this.imagePreviews.push(e.target.result as string);
             
            }
          };
          reader.readAsDataURL(file);
        }
      });
    }
  }

  // Supprimer une image de la prévisualisation
  removeImage(index: number): void {
    this.imagePreviews.splice(index, 1); // Supprimer l'image de l'aperçu
    this.selectedFiles.splice(index, 1); // Supprimer le fichier correspondant
  }

  // Soumission du formulaire
  onSubmit(): void {
    if (!this.antiquity.idAntiquity) {
      console.error('ID de l\'antiquité manquant');
      return;
    }
  
    // Envoyer l'antiquité avec les images sélectionnées
    this.antiquityService.updateAntiquityWithPhotos(
      this.antiquity.idAntiquity, 
      this.antiquity, 
      this.selectedFiles.length > 0 ? this.selectedFiles : undefined
    ).subscribe({
      next: () => {
        alert('Antiquité mise à jour avec succès');
        this.router.navigate(['/antiquities']); 
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour :', err);
        alert('Erreur lors de la mise à jour de l\'antiquité');
      }
    });
  }
  

}
