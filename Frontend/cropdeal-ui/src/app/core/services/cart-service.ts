import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Cart } from '../models/cart.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getCart(dealerId: string) {
    return this.http.get<Cart>(
      `${this.apiUrl}/carts/${dealerId}`
    );
  }

  addToCart(
    dealerId: string,
    productId: string,
    quantity: number
  ) {
    return this.http.post(
      `${this.apiUrl}/carts/${dealerId}/items`,
      null,
      {
        params: {
          cropId: productId,
          quantity
        },
        responseType: 'text'
      }
    );
  }

  updateQuantity(
    dealerId: string,
    productId: string,
    quantity: number
  ) {
    return this.http.put(
      `${this.apiUrl}/carts/${dealerId}/items/${productId}`,
      null,
      {
        params: {
          quantity
        },
        responseType: 'text'
      }
    );
  }

  removeItem(
    dealerId: string,
    productId: string
  ) {
    return this.http.delete(
      `${this.apiUrl}/carts/${dealerId}/items/${productId}`,
      {
        responseType: 'text'
      }
    );
  }
}