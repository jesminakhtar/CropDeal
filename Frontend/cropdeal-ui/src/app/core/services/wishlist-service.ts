import { Injectable, signal } from '@angular/core';
import { Product } from '../models/inventory.model';

@Injectable({
  providedIn: 'root'
})
export class WishlistService {

  private readonly STORAGE_KEY = 'cropdeal_wishlist';

  private readonly wishlistSignal = signal<Product[]>(this.loadWishlist());

  readonly wishlist = this.wishlistSignal.asReadonly();

  isWishlisted(productId: string): boolean {
    return this.wishlistSignal().some(product => product.id === productId);
  }

  toggle(product: Product): boolean {
    const products = this.wishlistSignal();
    const exists = products.some(item => item.id === product.id);

    let updated: Product[];

    if (exists) {
      updated = products.filter(item => item.id !== product.id);
    } else {
      updated = [...products, product];
    }

    this.wishlistSignal.set(updated);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(updated));

    return !exists;
  }

  remove(productId: string): void {
    const updated = this.wishlistSignal().filter(product => product.id !== productId);

    this.wishlistSignal.set(updated);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(updated));
  }

  clear(): void {
    this.wishlistSignal.set([]);
    localStorage.removeItem(this.STORAGE_KEY);
  }

  private loadWishlist(): Product[] {
    const stored = localStorage.getItem(this.STORAGE_KEY);

    if (!stored) {
      return [];
    }

    try {
      return JSON.parse(stored) as Product[];
    } catch {
      localStorage.removeItem(this.STORAGE_KEY);
      return [];
    }
  }
}