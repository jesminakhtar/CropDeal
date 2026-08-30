import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import {
  Address,
  CreateAddressRequest
} from '../models/address.model';

@Injectable({
  providedIn: 'root'
})
export class AddressService {

  private readonly apiUrl = 'http://localhost:8080/address';

  constructor(private http: HttpClient) {}

  getUserAddresses(userId: string) {
    return this.http.get<Address[]>(
      `${this.apiUrl}/user/${userId}`
    );
  }

  createAddress(request: CreateAddressRequest) {
    return this.http.post<Address>(
      `${this.apiUrl}/add`,
      request
    );
  }
}