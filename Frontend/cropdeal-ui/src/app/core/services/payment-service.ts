import {
  Injectable
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Receipt
} from '../models/order.model';
import { API_BASE_URL } from '../config/api.config';


export interface PaymentInitResponse {
  apikey: string;
  currency: string;
  orderId: string;
  amount: number;
}


export interface PaymentVerificationRequest {
  cropDealOrderId: string;
  razorpayPaymentId: string;
  razorpayOrderId: string;
  razorpaySignature: string;
}


@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private readonly apiUrl = `${API_BASE_URL}/payments`;

  constructor(
    private http: HttpClient
  ) {}


  initiatePayment(
    cropDealOrderId: string
  ) {

    return this.http
      .post<PaymentInitResponse>(
        `${this.apiUrl}/initiate/${cropDealOrderId}`,
        null
      );
  }


  verifyPayment(
    request: PaymentVerificationRequest
  ) {

    return this.http.post<Receipt>(
      `${this.apiUrl}/verify`,
      request
    );
  }
}