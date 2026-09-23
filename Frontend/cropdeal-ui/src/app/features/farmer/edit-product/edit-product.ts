import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { InventoryService } from '../../../core/services/inventory-service';
import { Product } from '../../../core/models/inventory.model';

@Component({
  selector: 'app-edit-product',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './edit-product.html',
  styleUrl: './edit-product.scss'
})
export class EditProductComponent implements OnInit {

  loading = true;
  saving = false;
  errorMessage = '';

  productId = '';
  product: Product | null = null;
  imagePreview: string | null = null;

  productForm;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private inventoryService: InventoryService,
    private cdr: ChangeDetectorRef
  ) {
    this.productForm = this.fb.nonNullable.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      category: ['', Validators.required],
      quantity: [0, [Validators.required, Validators.min(0)]],
      price: [0, [Validators.required, Validators.min(0.01)]],
      description: ['', [Validators.required, Validators.minLength(5)]]
    });
  }

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('productId');

    if (!productId) {
      this.errorMessage = 'Unable to identify this product.';
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }

    this.productId = productId;
    this.loadProduct();
  }

  private loadProduct(): void {
    this.loading = true;
    this.errorMessage = '';

    this.inventoryService.getProductById(this.productId).subscribe({
      next: product => {
        this.product = product;

        this.productForm.patchValue({
          name: product.name,
          category: product.category,
          quantity: product.quantity,
          price: product.price,
          description: product.description
        });

        if (product.imageData) {
          this.imagePreview = `data:image/jpeg;base64,${product.imageData}`;
        }

        this.loading = false;
        this.cdr.markForCheck();
      },

      error: error => {
        console.error('Unable to load product:', error);

        this.errorMessage = 'Unable to load this product.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  save(): void {
    if (this.productForm.invalid) {
      this.productForm.markAllAsTouched();
      return;
    }

    if (!this.product) {
      this.errorMessage = 'Unable to identify this product.';
      return;
    }

    const values = this.productForm.getRawValue();

    const updatedProduct: Product = {
      ...this.product,
      name: values.name.trim(),
      category: values.category,
      quantity: Number(values.quantity),
      price: Number(values.price),
      description: values.description.trim()
    };

    this.saving = true;
    this.errorMessage = '';
    this.cdr.markForCheck();

    this.inventoryService.updateProduct(this.productId, updatedProduct).subscribe({
      next: () => {
        this.router.navigate(['/farmer/dashboard']);
      },

      error: (error: HttpErrorResponse) => {
        console.error('Unable to update product:', error);

        if (error.status === 403) {
          this.errorMessage = 'You are not authorized to update this product.';
        } else if (error.status === 404) {
          this.errorMessage = 'This product could not be found.';
        } else if (error.status === 400) {
          this.errorMessage = 'Please check the product details and try again.';
        } else {
          this.errorMessage = 'Unable to update this product right now.';
        }

        this.saving = false;
        this.cdr.markForCheck();
      }
    });
  }
}