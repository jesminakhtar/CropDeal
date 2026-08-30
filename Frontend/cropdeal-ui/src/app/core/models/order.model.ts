export interface Order {
  orderId: string;
  dealerId: string;
  totalPrice: number;
  orderItems: Record<string, number>;
  status: string;
  deliveryAddressId: string;
  paymentStatus?: string;
  paymentMode?: string;
  transactionId?: string;
  razorpayOrderId?: string;
}


export interface Receipt {
  id?: string;
  orderId: string;
  transactionId: string;
  dealerId: string;
  orderItems: Record<string, number>;
  totalPrice: number;
  status: string;
}