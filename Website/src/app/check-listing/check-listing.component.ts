import { Component, OnInit } from '@angular/core';
import { ListingService } from '../../service/listing.service';
import { Antiquity } from '../../modele/DtoListing';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-check-listing',
  templateUrl: './check-listing.component.html',
  styleUrl: './check-listing.component.css'
})
export class CheckListingComponent implements OnInit{

  currentTheme: 'dark' | 'light' = 'light'; // Actual theme, by default light
  antiquity!: Antiquity;
  isPopupVisible: boolean = false;
  note_title : string = "";
  note_description : string = "";
  note_price : string = "";
  note_photo : string = "";

  constructor( private route: ActivatedRoute, private listingService : ListingService, private router : Router){}

  ngOnInit(): void {
     // Retrieve the ID from the URL
     const id = this.route.snapshot.paramMap.get('id');
     if (id) {
       this.loadAntiquityById(id);
     }else{
      this.router.navigate(['/home']);
     }
  }

  // Load the antiquity from the API by its ID
    loadAntiquityById(id: string) {
      this.listingService.getAntiquityById(id).subscribe({
        next: (data) => {
          this.antiquity = data;
        }
      });
    }


    acceptAntiquity(){
     const isConfirm = confirm("Are you sure to accept this listing ?");
     if(isConfirm){
      this.listingService.acceptAntiquity(this.antiquity).subscribe({
        next: (data : Map<String, String>) =>{
          alert(data.get("message"));
        },
        error: (err : Map<String, String>)=>{
          console.error('Error accepting antiquity:', err.get("message"));
        }
      });
     }
    }

    openPopup(){
      this.isPopupVisible=true;
    }

    rejectAntiquity(){
      if(this.note_title=="" || this.note_description=="" || this.note_price=="" || this.note_photo==""){
        return alert("all fields must be completed.");
      } 
      const isConfirm = confirm("Are you sure to reject this listing ?");
     if(isConfirm){
      this.listingService.rejectAntiquity(this.antiquity,this.note_title,this.note_description,this.note_price,this.note_photo).subscribe({
        next: (data : Map<String, String>) =>{
          alert(data.get("message"));
        },
        error: (err : Map<String, String>)=>{
          console.error('Error rejecting antiquity:', err.get("message"));
        }
      });
     }
    }

    cancelReject(){
      let isConfirm = true;
      if(this.note_title!="" || this.note_description!="" || this.note_price!="" || this.note_photo!=""){
        isConfirm = confirm("Are you sure to cancel ?");
      } 
      if(isConfirm){
        this.isPopupVisible=false;
        this.note_description,this.note_photo,this.note_price,this.note_title="";
      }
    }

}
