export interface Crop {
  id: string;
  name: string;
  category: string;
  farmer: string;
  location: string;
  price: number;
  unit: string;
  availableQuantity: number;
  image?: string;
  visualType?: 'tomato' | 'wheat' | 'potato' | 'rice';
}