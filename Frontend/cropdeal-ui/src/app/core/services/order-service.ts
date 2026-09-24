import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Order } from '../models/order.model';
import { API_BASE_URL } from '../config/api.config';

export interface FarmerOrderResponse {
  orderId: string;
  dealerId: string;
  status: string;
  paymentStatus?: string;
  paymentMode?: string;
  transactionId?: string;
  deliveryAddressId?: string;
  orderItems: Record<string, number>;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private readonly apiUrl = `${API_BASE_URL}/orders`;

  constructor(private http: HttpClient) {}

  placeOrder(dealerId: string, addressId: string) {
    return this.http.post<Order>(`${this.apiUrl}/place-order/${dealerId}/${addressId}`, null);
  }

  getUserOrders(dealerId: string) {
    return this.http.get<Order[]>(`${this.apiUrl}/user/${dealerId}`);
  }

  getOrderById(orderId: string) {
    return this.http.get<Order>(`${this.apiUrl}/${orderId}`);
  }

  getAllOrders() {
    return this.http.get<Order[]>(`${this.apiUrl}/all`);
  }

  getFarmerOrders(username: string) {
    return this.http.get<FarmerOrderResponse[]>(`${this.apiUrl}/farmer/${username}`);
  }

  getFarmerOrder(username: string, orderId: string) {
    return this.http.get<FarmerOrderResponse>(`${this.apiUrl}/farmer/${username}/${orderId}`);
  }
}