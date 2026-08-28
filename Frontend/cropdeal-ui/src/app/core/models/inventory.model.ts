export interface Product {
  id: string;
  name: string;
  category: string;
  quantity: number;
  price: number;
  description: string;
  shopId: string;
  imageData?: string;
  ratings?: Rating[];
}

export interface Rating {
  stars: number;
  dealerId?: string;
}

export interface Shop {
  id: string;
  name: string;
  farmerUsername: string;
  imageData?: string;
  products: Product[];
  ratings: Rating[];
}