export interface Cart {
  id: string;
  dealerId: string;
  totalPrice: number;
  cartItems: Record<string, number>;
}