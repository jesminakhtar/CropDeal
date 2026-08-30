import {
  ChangeDetectorRef,
  Component,
  OnInit
} from '@angular/core';

import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../../core/services/auth';
import { InventoryService } from '../../../../core/services/inventory-service';

import {
  Product,
  Shop
} from '../../../../core/models/inventory.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    DecimalPipe,
    RouterLink
  ],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {

  loading = true;

  errorMessage = '';

  shop: Shop | null = null;

  products: Product[] = [];

  rating: number | null = null;


  constructor(
    private authService: AuthService,
    private inventoryService: InventoryService,
    private cdr: ChangeDetectorRef
  ) {}


  ngOnInit(): void {
    this.loadDashboard();
  }


  get currentUser() {
    return this.authService.getUser();
  }


  get totalProducts(): number {
    return this.products.length;
  }


  get totalStock(): number {
    return this.products.reduce(
      (total, product) =>
        total + product.quantity,
      0
    );
  }


  get inventoryValue(): number {
    return this.products.reduce(
      (total, product) =>
        total + (product.quantity * product.price),
      0
    );
  }


  loadDashboard(): void {

    const username =
      this.currentUser?.username;

    if (!username) {

      this.loading = false;

      this.errorMessage =
        'Unable to determine the logged-in farmer.';

      this.cdr.markForCheck();

      return;
    }


    this.loading = true;
    this.errorMessage = '';

    this.shop = null;
    this.products = [];
    this.rating = null;


    this.inventoryService
      .getFarmerShops(username)
      .subscribe({

        next: (shops) => {

          console.log(
            'Farmer shops response:',
            shops
          );


          /*
           * New farmer:
           * no shop exists yet.
           *
           * Backend now returns [].
           */
          if (!shops || shops.length === 0) {

            this.shop = null;

            this.products = [];

            this.rating = null;

            this.loading = false;

            this.cdr.markForCheck();

            return;
          }


          /*
           * For now CropDeal uses the
           * farmer's first shop.
           */
          this.shop = shops[0];


          if (!this.shop?.id) {

            this.loading = false;

            this.errorMessage =
              'Your shop information is incomplete.';

            this.cdr.markForCheck();

            return;
          }


          this.loadShopData(
            this.shop.id
          );
        },


        error: (error) => {

          console.error(
            'Failed to load farmer shops:',
            error
          );

          this.shop = null;

          this.products = [];

          this.rating = null;

          this.loading = false;


          if (error.status === 404) {

            /*
             * Treat missing shop as
             * an empty farmer dashboard.
             */
            this.errorMessage = '';

          } else {

            this.errorMessage =
              'We could not load your shop right now.';

          }


          this.cdr.markForCheck();
        }

      });
  }


  private loadShopData(
    shopId: string
  ): void {

    this.inventoryService
      .getShopProducts(shopId)
      .subscribe({

        next: (products) => {

          console.log(
            'Shop products response:',
            products
          );

          this.products =
            products ?? [];

          this.loadRating(shopId);
        },


        error: (error) => {

          console.error(
            'Failed to load products:',
            error
          );

          /*
           * A shop with no products should
           * still render successfully.
           */
          if (error.status === 404) {

            this.products = [];

            this.loadRating(shopId);

            return;
          }


          this.products = [];

          this.loading = false;

          this.errorMessage =
            'We could not load your products right now.';

          this.cdr.markForCheck();
        }

      });
  }


  private loadRating(
    shopId: string
  ): void {

    this.inventoryService
      .getShopRating(shopId)
      .subscribe({

        next: (rating) => {

          console.log(
            'Shop rating response:',
            rating
          );


          /*
           * Backend uses -1 when
           * no rating exists.
           */
          this.rating =
            rating === -1
              ? null
              : rating;


          this.loading = false;

          this.cdr.markForCheck();
        },


        error: (error) => {

          console.warn(
            'Unable to load shop rating:',
            error
          );

          /*
           * Rating is optional.
           * Don't fail the dashboard.
           */
          this.rating = null;

          this.loading = false;

          this.cdr.markForCheck();
        }

      });
  }
}