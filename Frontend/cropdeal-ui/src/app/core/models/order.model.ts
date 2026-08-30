export interface Order {
  orderId: string;
  dealerId: string;
  totalPrice: number;
  orderItems: Record<string, number>;
  status: string;
  deliveryAddressId: string;
}