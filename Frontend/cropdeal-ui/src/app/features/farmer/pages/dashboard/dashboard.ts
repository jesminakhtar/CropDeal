import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../../core/services/auth-service';
import { InventoryService } from '../../../../core/services/inventory-service';
import { Product, Shop } from '../../../../core/models/inventory.model';

@Component({
  selector: 'app-dashboard',
  imports: [DecimalPipe, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class DashboardComponent implements OnInit {

  loading = true;
  deletingProduct = false;
  errorMessage = '';

  shop: Shop | null = null;
  products: Product[] = [];
  rating: number | null = null;

  productToDelete: Product | null = null;

  constructor(
    private authService: AuthService,
    private inventoryService: InventoryService,
    private router: Router,
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
    return this.products.reduce((total, product) => total + product.quantity, 0);
  }

  get inventoryValue(): number {
    return this.products.reduce((total, product) => total + (product.quantity * product.price), 0);
  }

  loadDashboard(): void {
    const username = this.currentUser?.username;

    if (!username) {
      this.loading = false;
      this.errorMessage = 'Unable to determine the logged-in farmer.';
      this.cdr.markForCheck();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.shop = null;
    this.products = [];
    this.rating = null;

    this.inventoryService.getFarmerShops(username).subscribe({
      next: shops => {
        if (!shops?.length) {
          this.shop = null;
          this.products = [];
          this.rating = null;
          this.loading = false;
          this.cdr.markForCheck();
          return;
        }

        this.shop = shops[0];

        if (!this.shop?.id) {
          this.loading = false;
          this.errorMessage = 'Your shop information is incomplete.';
          this.cdr.markForCheck();
          return;
        }

        this.loadShopData(this.shop.id);
      },

      error: error => {
        console.error('Failed to load farmer shops:', error);

        this.shop = null;
        this.products = [];
        this.rating = null;
        this.loading = false;

        this.errorMessage = error.status === 404
          ? ''
          : 'We could not load your shop right now.';

        this.cdr.markForCheck();
      }
    });
  }

  private loadShopData(shopId: string): void {
    this.inventoryService.getShopProducts(shopId).subscribe({
      next: products => {
        this.products = products ?? [];
        this.loadRating(shopId);
      },

      error: error => {
        console.error('Failed to load products:', error);

        if (error.status === 404) {
          this.products = [];
          this.loadRating(shopId);
          return;
        }

        this.products = [];
        this.loading = false;
        this.errorMessage = 'We could not load your products right now.';
        this.cdr.markForCheck();
      }
    });
  }

  private loadRating(shopId: string): void {
    this.inventoryService.getShopRating(shopId).subscribe({
      next: rating => {
        this.rating = rating === -1 ? null : rating;
        this.loading = false;
        this.cdr.markForCheck();
      },

      error: error => {
        console.warn('Unable to load shop rating:', error);
        this.rating = null;
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  editProduct(product: Product): void {
    const productId = this.getProductId(product);

    if (!productId) {
      console.error('Product ID is missing:', product);
      this.errorMessage = 'Unable to identify this product.';
      this.cdr.markForCheck();
      return;
    }

    console.log('Opening product:', productId);

    this.router.navigate(['/farmer/products', productId, 'edit']);
  }

  openDeleteModal(product: Product): void {
    this.productToDelete = product;
    this.cdr.markForCheck();
  }

  closeDeleteModal(): void {
    if (this.deletingProduct) {
      return;
    }

    this.productToDelete = null;
    this.cdr.markForCheck();
  }

  confirmDeleteProduct(): void {
    const product = this.productToDelete;

    if (!product) {
      return;
    }

    const productId = this.getProductId(product);

    if (!productId) {
      this.errorMessage = 'Unable to identify this product.';
      this.productToDelete = null;
      this.cdr.markForCheck();
      return;
    }

    this.deletingProduct = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    this.inventoryService.deleteProduct(productId).subscribe({
      next: () => {
        this.products = this.products.filter(item => this.getProductId(item) !== productId);
        this.productToDelete = null;
        this.deletingProduct = false;
        this.cdr.markForCheck();
      },

      error: error => {
        console.error('Unable to delete product:', error);

        this.productToDelete = null;
        this.deletingProduct = false;
        this.errorMessage = 'Unable to delete this product right now.';
        this.cdr.markForCheck();
      }
    });
  }

  private getProductId(product: Product): string {
    return product.id ?? (product as any).productId ?? '';
  }
}