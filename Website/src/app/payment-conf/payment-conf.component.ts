import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { ThemeService } from '../../service/theme.service';
import { ListingService } from '../../service/listing.service';
import { Antiquity } from '../../modele/DtoListing';
import { forkJoin } from 'rxjs';
import { mergeMap } from 'rxjs/operators';
import { ImageServiceService } from '../../service/image-service.service';

@Component({
  selector: 'app-payment-conf',
  templateUrl: './payment-conf.component.html',
  styleUrls: ['./payment-conf.component.css']
})
export class PaymentConfComponent implements OnInit {

  currentTheme: 'dark' | 'light' = 'light';
  paymentStatus: 'success' | 'error' = 'error';
  invoiceNumber: string | null = null;
  paymentId: string | null = null;
  payerId: string | null = null;
  antiquity: Antiquity | null = null;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private listingService: ListingService,
    private themeService: ThemeService,
    private imageService: ImageServiceService
  ) {}

  ngOnInit(): void {
    // Subscribe to Theme event
    this.themeService.theme$.subscribe(theme => {
      this.currentTheme = theme;
    });

    // Extract URL parameters
    this.paymentId = this.route.snapshot.queryParamMap.get('paymentId');
    this.payerId = this.route.snapshot.queryParamMap.get('PayerID');

    if (this.paymentId && this.payerId) {
      // Call the API to execute the payment
      this.executePayment(this.paymentId, this.payerId);
    } else {
      this.paymentStatus = 'error';
    }
  }

  executePayment(paymentId: string, payerId: string): void {
    this.listingService.executePayment(paymentId, payerId).subscribe(
      (response: any) => {
        this.paymentStatus = 'success';
        console.log('Payment executed successfully', response);
        this.invoiceNumber = response.invoiceNumber;
        const listingId = response.listingId;
      },
      error => {
        if (error.message && error.error.message.includes("PAYMENT_ALREADY_DONE")) {
          this.paymentStatus = 'success';
        } else {
          this.paymentStatus = 'error';
          console.error('Payment execution failed', error);
        }
      }
    );
  }

  buyAntiquity(): void {
    // Call the service to buy the antiquity
    this.listingService.buyAntiquity(this.antiquity!.idAntiquity).subscribe(response => {
      // Open the paypal link in the current tab
      window.open(response, '_self');
    });
  }

  retryPayment(): void {
    this.router.navigate(['/sell']);
  }
}