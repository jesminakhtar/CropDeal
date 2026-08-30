import { Component } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../../core/services/auth';
import { InventoryService } from '../../../../core/services/inventory';

@Component({
  selector: 'app-create-shop',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './create-shop.html',
  styleUrl: './create-shop.scss'
})
export class CreateShopComponent {

  loading = false;
  errorMessage = '';

  selectedImage: File | null = null;
  previewUrl: string | null = null;

  shopForm;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private inventoryService: InventoryService,
    private router: Router
  ) {
    this.shopForm = this.fb.nonNullable.group({
      name: [
        '',
        [
          Validators.required,
          Validators.minLength(3)
        ]
      ]
    });
  }

  selectImage(event: Event): void {

    const input = event.target as HTMLInputElement;

    const file = input.files?.[0];

    if (!file) {
      return;
    }

    if (!file.type.startsWith('image/')) {
      this.errorMessage = 'Please select an image file.';
      return;
    }

    this.selectedImage = file;

    this.previewUrl = URL.createObjectURL(file);

    this.errorMessage = '';
  }


  createShop(): void {

    if (this.shopForm.invalid) {
      this.shopForm.markAllAsTouched();
      return;
    }

    if (!this.selectedImage) {
      this.errorMessage =
        'Please add an image for your shop.';
      return;
    }

    const user = this.authService.getUser();

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.inventoryService
      .createShop(
        this.shopForm.getRawValue().name,
        user.username,
        this.selectedImage
      )
      .subscribe({

        next: () => {
          this.loading = false;

          this.router.navigate([
            '/farmer/dashboard'
          ]);
        },

        error: error => {
          this.loading = false;

          console.error(
            'Shop creation failed',
            error
          );

          this.errorMessage =
            'Unable to create your shop right now.';
        }

      });
  }
}