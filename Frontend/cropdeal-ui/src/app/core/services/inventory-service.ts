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

  createShop(
    name: string,
    farmerUsername: string,
    image: File
  ) {
    const formData = new FormData();

    formData.append('file', image);
    formData.append('name', name);
    formData.append('farmerUsername', farmerUsername);

    return this.http.post<Shop>(
      `${this.apiUrl}/shops/add`,
      formData
    );
  }

  addProduct(
    shopId: string,
    name: string,
    category: string,
    quantity: number,
    price: number,
    description: string,
    image: File
  ) {
    const formData = new FormData();

    formData.append('file', image);
    formData.append('name', name);
    formData.append('shopId', shopId);
    formData.append('category', category);
    formData.append('quantity', quantity.toString());
    formData.append('price', price.toString());
    formData.append('description', description);

    return this.http.post<Product>(
      `${this.apiUrl}/products/add`,
      formData
    );
  }

  getAllProducts() {
    return this.http.get<Product[]>(
      `${this.apiUrl}/products`
    );
  }

  getProductById(productId: string) {
    return this.http.get<Product>(
      `${this.apiUrl}/products/findById/${productId}`
    );
  }

  getShopById(shopId: string) {
    return this.http.get<Shop>(`${this.apiUrl}/shops/${shopId}`);
  }

  updateShop(shopId: string, name: string, farmerUsername: string, image?: File | null) {
    const formData = new FormData();

    formData.append('name', name);
    formData.append('farmerUsername', farmerUsername);

    if (image) {
      formData.append('file', image);
    }

    return this.http.put<Shop>(`${this.apiUrl}/shops/${shopId}`, formData);
  }

  updateProduct(productId: string, product: Product) {
    return this.http.put(
      `${this.apiUrl}/products/${productId}`,
      product,
      { responseType: 'text' }
    );
  }

  deleteProduct(productId: string) {
    return this.http.delete(
      `${this.apiUrl}/products/${productId}`,
      { responseType: 'text' }
    );
  }
}