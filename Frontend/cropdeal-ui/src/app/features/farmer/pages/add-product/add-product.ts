import { Component, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {
  ActivatedRoute,
  Router,
  RouterLink
} from '@angular/router';

import { InventoryService } from '../../../../core/services/inventory';

@Component({
  selector: 'app-add-product',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './add-product.html',
  styleUrl: './add-product.scss'
})
export class AddProductComponent {

  loading = signal(false);
  errorMessage = signal('');

  selectedImage = signal<File | null>(null);
  previewUrl = signal<string | null>(null);

  readonly shopId: string;

  productForm;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private inventoryService: InventoryService
  ) {

    this.shopId =
      this.route.snapshot.paramMap.get('shopId') ?? '';

    this.productForm = this.fb.nonNullable.group({

      name: [
        '',
        [
          Validators.required,
          Validators.minLength(2)
        ]
      ],

      category: [
        '',
        Validators.required
      ],

      quantity: [
        1,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      price: [
        1,
        [
          Validators.required,
          Validators.min(1)
        ]
      ],

      description: [
        '',
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(500)
        ]
      ]

    });
  }


  selectImage(event: Event): void {

    const input =
      event.target as HTMLInputElement;

    const file =
      input.files?.[0];

    if (!file) {
      return;
    }

    if (!file.type.startsWith('image/')) {
      this.errorMessage.set(
        'Please select a valid image file.'
      );

      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      this.errorMessage.set(
        'Please select an image smaller than 5 MB.'
      );

      return;
    }

    const oldUrl = this.previewUrl();

    if (oldUrl) {
      URL.revokeObjectURL(oldUrl);
    }

    this.selectedImage.set(file);

    this.previewUrl.set(
      URL.createObjectURL(file)
    );

    this.errorMessage.set('');
  }


  addProduct(): void {

    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    if (!this.shopId) {
      this.errorMessage.set(
        'Shop information is missing.'
      );

      return;
    }

    const image =
      this.selectedImage();

    if (!image) {
      this.errorMessage.set(
        'Please upload a product image.'
      );

      return;
    }

    const value =
      this.productForm.getRawValue();

    this.loading.set(true);
    this.errorMessage.set('');

    this.inventoryService
      .addProduct(
        this.shopId,
        value.name.trim(),
        value.category,
        Number(value.quantity),
        Number(value.price),
        value.description.trim(),
        image
      )
      .subscribe({

        next: product => {

          console.log(
            'Product created:',
            product
          );

          this.loading.set(false);

          this.router.navigate([
            '/farmer/dashboard'
          ]);
        },

        error: error => {

          console.error(
            'Product creation failed:',
            error
          );

          this.loading.set(false);

          if (error.status === 409) {
            this.errorMessage.set(
              'A product with this name already exists in your shop.'
            );
          } else {
            this.errorMessage.set(
              'Unable to add the product right now. Please try again.'
            );
          }
        }

      });
  }
}