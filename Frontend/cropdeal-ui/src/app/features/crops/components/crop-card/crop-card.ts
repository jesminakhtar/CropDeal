import { Component, Input } from '@angular/core';
import { Crop } from '../../models/crop.model';

@Component({
  selector: 'app-crop-card',
  imports: [],
  templateUrl: './crop-card.html',
  styleUrl: './crop-card.scss'
})
export class CropCardComponent {
  @Input({ required: true }) crop!: Crop;
}