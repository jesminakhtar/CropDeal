import { Component, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';

import { WishlistService } from '../../core/services/wishlist-service';
import { Product } from '../../core/models/inventory.model';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [DecimalPipe, RouterLink],
  templateUrl: './wishlist.html',
  styleUrl: './wishlist.scss'
})
export class WishlistComponent {

  private readonly wishlistService = inject(WishlistService);

  readonly products = this.wishlistService.wishlist;

  removeProduct(product: Product): void {
    this.wishlistService.remove(product.id);
  }

  clearWishlist(): void {
    this.wishlistService.clear();
  }
}