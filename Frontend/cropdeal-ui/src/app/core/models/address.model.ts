export interface Address {
  id: string;
  name: string;
  userId: string;
  houseNo: string;
  roadName: string;
  landmark: string;
  pin: string;
  city: string;
  state: string;
  country: string;
  type: string;
}

export interface CreateAddressRequest {
  name: string;
  userId: string;
  houseNo: string;
  roadName: string;
  landmark: string;
  pin: string;
  city: string;
  state: string;
  country: string;
  type: string;
}