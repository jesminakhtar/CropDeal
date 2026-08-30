import {
  Injectable
} from '@angular/core';

import {
  HttpClient
} from '@angular/common/http';

import {
  Receipt
} from '../models/order.model';


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

  private readonly apiUrl =
    'http://localhost:8080/payments';


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