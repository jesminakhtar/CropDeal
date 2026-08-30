import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Order } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private readonly apiUrl = 'http://localhost:8080/orders';

  constructor(private http: HttpClient) {}

  placeOrder(
    dealerId: string,
    addressId: string
  ) {
    return this.http.post<Order>(
      `${this.apiUrl}/place-order/${dealerId}/${addressId}`,
      null
    );
  }

  getUserOrders(dealerId: string) {
    return this.http.get<Order[]>(
      `${this.apiUrl}/user/${dealerId}`
    );
  }
}