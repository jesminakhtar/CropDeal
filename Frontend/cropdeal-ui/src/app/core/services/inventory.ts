import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import {
  Product,
  Shop
} from '../models/inventory.model';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {

  private readonly apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getFarmerShops(username: string) {
    return this.http.get<Shop[]>(
      `${this.apiUrl}/shops/farmerUsername/${username}`
    );
  }

  getShopProducts(shopId: string) {
    return this.http.get<Product[]>(
      `${this.apiUrl}/shops/getAllProducts/${shopId}`
    );
  }

  getShopRating(shopId: string) {
    return this.http.get<number>(
      `${this.apiUrl}/shops/rating/${shopId}`
    );
  }
}