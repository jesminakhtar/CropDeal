import { Component } from '@angular/core';
import { CropCardComponent } from '../../../crops/components/crop-card/crop-card';
import { Crop } from '../../../crops/models/crop.model';

@Component({
  selector: 'app-home',
  imports: [CropCardComponent],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent {

  crops: Crop[] = [
    {
      id: '1',
      name: 'Fresh Tomatoes',
      category: 'Vegetables',
      farmer: 'Green Valley Farms',
      location: 'West Bengal',
      price: 28,
      unit: 'kg',
      availableQuantity: 320,
      visualType: 'tomato'
    },
    {
      id: '2',
      name: 'Premium Wheat',
      category: 'Grains',
      farmer: 'Sharma Agro',
      location: 'Punjab',
      price: 34,
      unit: 'kg',
      availableQuantity: 840,
      visualType: 'wheat'
    },
    {
      id: '3',
      name: 'Organic Potatoes',
      category: 'Vegetables',
      farmer: 'Fresh Field Co.',
      location: 'Uttar Pradesh',
      price: 24,
      unit: 'kg',
      availableQuantity: 460,
      visualType: 'potato'
    },
    {
      id: '4',
      name: 'Basmati Rice',
      category: 'Grains',
      farmer: 'Harvest Farms',
      location: 'Haryana',
      price: 72,
      unit: 'kg',
      availableQuantity: 600,
      visualType: 'rice'
    }
  ];
}