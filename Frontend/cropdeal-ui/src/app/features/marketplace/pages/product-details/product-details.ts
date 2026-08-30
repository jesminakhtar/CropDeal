import {
  Component,
  OnInit,
  signal
} from '@angular/core';

import { DecimalPipe } from '@angular/common';
import {
  ActivatedRoute,
  Router,
  RouterLink
} from '@angular/router';

import { InventoryService } from '../../../../core/services/inventory-service';
import { CartService } from '../../../../core/services/cart-service';
import { AuthService } from '../../../../core/services/auth';

import { Product } from '../../../../core/models/inventory.model';

@Component({
  selector: 'app-product-details',
  imports: [
    DecimalPipe,
    RouterLink
  ],
  templateUrl: './product-details.html',
  styleUrl: './product-details.scss'
})
export class ProductDetailsComponent implements OnInit {

  loading = signal(true);
  errorMessage = signal('');

  product = signal<Product | null>(null);

  quantity = signal(1);

  addingToCart = signal(false);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private inventoryService: InventoryService,
    private cartService: CartService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {

    const id =
      this.route.snapshot.paramMap.get('id');

    if (!id) {
      this.errorMessage.set(
        'Product information is missing.'
      );

      this.loading.set(false);

      return;
    }

    this.inventoryService
      .getProductById(id)
      .subscribe({

        next: product => {
          this.product.set(product);
          this.loading.set(false);
        },

        error: error => {
          console.error(error);

          this.errorMessage.set(
            'Unable to load this product.'
          );

          this.loading.set(false);
        }

      });
  }


  increaseQuantity(): void {

    const product =
      this.product();

    if (!product) {
      return;
    }

    if (
      this.quantity() <
      product.quantity
    ) {
      this.quantity.update(
        value => value + 1
      );
    }
  }


  decreaseQuantity(): void {

    if (this.quantity() > 1) {
      this.quantity.update(
        value => value - 1
      );
    }
  }


  addToCart(): void {

    const user =
      this.authService.getUser();

    const product =
      this.product();

    if (!product) {
      return;
    }

    if (!user) {

      this.router.navigate(
        ['/login']
      );

      return;
    }

    if (user.role !== 'DEALER') {

      this.errorMessage.set(
        'Only buyer accounts can add products to cart.'
      );

      return;
    }

    this.addingToCart.set(true);

    this.errorMessage.set('');

    this.cartService
      .addToCart(
        user.userId,
        product.id,
        this.quantity()
      )
      .subscribe({

        next: () => {

          this.addingToCart.set(false);

          this.router.navigate([
            '/cart'
          ]);
        },

        error: error => {

          console.error(
            'Add to cart failed:',
            error
          );

          this.addingToCart.set(false);

          this.errorMessage.set(
            'Unable to add this product to your cart.'
          );
        }

      });
  }
}