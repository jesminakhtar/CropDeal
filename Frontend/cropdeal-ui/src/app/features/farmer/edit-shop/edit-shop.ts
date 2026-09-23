import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth-service';
import { InventoryService } from '../../../core/services/inventory-service';

@Component({
  selector: 'app-edit-shop',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './edit-shop.html',
  styleUrl: './edit-shop.scss'
})
export class EditShopComponent implements OnInit {

  loading = true;
  saving = false;
  errorMessage = '';
  shopId = '';
  selectedImage: File | null = null;
  imagePreview: string | null = null;

  shopForm;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private inventoryService: InventoryService,
    private cdr: ChangeDetectorRef
  ) {
    this.shopForm = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  ngOnInit(): void {
    const user = this.authService.getUser();
    const shopId = this.route.snapshot.paramMap.get('shopId');

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    if (!shopId) {
      this.errorMessage = 'Unable to identify this shop.';
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }

    this.shopId = shopId;

    this.inventoryService.getShopById(shopId).subscribe({
      next: shop => {
        console.log('Loaded shop:', shop);

        if (shop.farmerUsername !== user.username) {
          this.router.navigate(['/farmer/dashboard']);
          return;
        }

        this.shopForm.patchValue({
          name: shop.name
        });

        if (shop.imageData) {
          this.imagePreview = `data:image/jpeg;base64,${shop.imageData}`;
        }

        this.loading = false;
        this.cdr.markForCheck();
      },

      error: error => {
        console.error('Unable to load shop:', error);

        this.errorMessage = 'Unable to load your shop.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    this.selectedImage = file;

    const reader = new FileReader();

    reader.onload = () => {
      this.imagePreview = reader.result as string;
      this.cdr.markForCheck();
    };

    reader.readAsDataURL(file);
  }

  save(): void {
    if (this.shopForm.invalid) {
      this.shopForm.markAllAsTouched();
      return;
    }

    const user = this.authService.getUser();

    if (!user?.username) {
      this.errorMessage = 'Unable to identify the farmer account.';
      this.cdr.markForCheck();
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    this.inventoryService.updateShop(
      this.shopId,
      this.shopForm.getRawValue().name.trim(),
      user.username,
      this.selectedImage
    ).subscribe({
      next: updatedShop => {
        console.log('Updated shop:', updatedShop);
        this.router.navigate(['/farmer/dashboard']);
      },

      error: (error: HttpErrorResponse) => {
        console.error('Unable to update shop:', error);

        this.errorMessage = 'Unable to update your shop right now.';
        this.saving = false;
        this.cdr.markForCheck();
      }
    });
  }
}