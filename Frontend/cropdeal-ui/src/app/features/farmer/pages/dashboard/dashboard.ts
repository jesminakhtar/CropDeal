import { Component, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';

import { AuthService } from '../../../../core/services/auth';
import { InventoryService } from '../../../../core/services/inventory';

import {
  Product,
  Shop
} from '../../../../core/models/inventory.model';

@Component({
  selector: 'app-dashboard',
  imports: [
    DecimalPipe
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
    private inventoryService: InventoryService
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
      (total, product) => total + product.quantity,
      0
    );
  }


  get inventoryValue(): number {
    return this.products.reduce(
      (total, product) =>
        total + product.quantity * product.price,
      0
    );
  }


  loadDashboard(): void {

    const username = this.currentUser?.username;

    if (!username) {
      this.loading = false;
      return;
    }

    this.inventoryService
      .getFarmerShops(username)
      .subscribe({

        next: shops => {

          if (!shops.length) {
            this.loading = false;
            return;
          }

          this.shop = shops[0];

          this.loadShopData(this.shop.id);
        },

        error: error => {

          this.loading = false;

          /*
           * The current backend throws an exception when
           * a farmer has no shop yet.
           *
           * We treat that as the dashboard's empty state.
           */
          if (
            error.status === 404 ||
            error.status === 500
          ) {
            this.shop = null;
            return;
          }

          this.errorMessage =
            'We could not load your shop right now.';
        }

      });
  }


  private loadShopData(shopId: string): void {

    this.inventoryService
      .getShopProducts(shopId)
      .subscribe({

        next: products => {
          this.products = products;

          this.loadRating(shopId);
        },

        error: () => {
          this.loading = false;

          this.errorMessage =
            'We could not load your products.';
        }

      });
  }


  private loadRating(shopId: string): void {

    this.inventoryService
      .getShopRating(shopId)
      .subscribe({

        next: rating => {

          this.rating =
            rating === -1
              ? null
              : rating;

          this.loading = false;
        },

        error: () => {
          this.rating = null;
          this.loading = false;
        }

      });
  }
}