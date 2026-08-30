import {
  Component,
  computed,
  OnInit,
  signal
} from '@angular/core';

import { DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../../../core/services/auth-service';

import { Cart } from '../../../../core/models/cart.model';
import { Product } from '../../../../core/models/inventory.model';
import { CartService } from '../../../../core/services/cart-service';
import { InventoryService } from '../../../../core/services/inventory-service';

interface CartLine {
  product: Product;
  quantity: number;
}

@Component({
  selector: 'app-cart',
  imports: [
    DecimalPipe,
    RouterLink
  ],
  templateUrl: './cart.html',
  styleUrl: './cart.scss'
})
export class CartComponent implements OnInit {

  loading = signal(true);
  errorMessage = signal('');

  cart = signal<Cart | null>(null);

  items = signal<CartLine[]>([]);

  updatingProductId = signal<string | null>(null);


  totalItems = computed(() =>
    this.items().reduce(
      (total, item) =>
        total + item.quantity,
      0
    )
  );


  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private inventoryService: InventoryService,
    private router: Router
  ) {}


  ngOnInit(): void {
    this.loadCart();
  }


  loadCart(): void {

    const user =
      this.authService.getUser();

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    if (user.role !== 'DEALER') {

      this.errorMessage.set(
        'The shopping cart is available for buyer accounts.'
      );

      this.loading.set(false);

      return;
    }


    this.loading.set(true);
    this.errorMessage.set('');


    this.cartService
      .getCart(user.userId)
      .subscribe({

        next: cart => {

          this.cart.set(cart);

          const entries =
            Object.entries(
              cart.cartItems ?? {}
            );


          if (!entries.length) {

            this.items.set([]);

            this.loading.set(false);

            return;
          }


          const requests =
            entries.map(
              ([productId]) =>
                this.inventoryService
                  .getProductById(productId)
            );


          forkJoin(requests)
            .subscribe({

              next: products => {

                const lines: CartLine[] =
                  products.map(
                    (product, index) => ({
                      product,
                      quantity:
                        entries[index][1]
                    })
                  );


                this.items.set(lines);

                this.loading.set(false);
              },


              error: error => {

                console.error(
                  'Unable to load cart products:',
                  error
                );

                this.errorMessage.set(
                  'Some cart products could not be loaded.'
                );

                this.loading.set(false);
              }

            });

        },


        error: error => {

          console.error(
            'Unable to load cart:',
            error
          );


          /*
           * A buyer may not have created
           * a cart yet.
           */
          if (error.status === 404) {

            this.cart.set(null);

            this.items.set([]);

            this.loading.set(false);

            return;
          }


          this.errorMessage.set(
            'Unable to load your cart right now.'
          );

          this.loading.set(false);
        }

      });
  }


  increase(item: CartLine): void {

    if (
      item.quantity >=
      item.product.quantity
    ) {
      return;
    }

    this.updateQuantity(
      item,
      item.quantity + 1
    );
  }


  decrease(item: CartLine): void {

    if (item.quantity <= 1) {
      return;
    }

    this.updateQuantity(
      item,
      item.quantity - 1
    );
  }


  private updateQuantity(
    item: CartLine,
    quantity: number
  ): void {

    const user =
      this.authService.getUser();

    if (!user) {
      return;
    }


    this.updatingProductId.set(
      item.product.id
    );

    this.errorMessage.set('');


    this.cartService
      .updateQuantity(
        user.userId,
        item.product.id,
        quantity
      )
      .subscribe({

        next: () => {

          this.updatingProductId.set(null);

          this.loadCart();
        },


        error: error => {

          console.error(
            'Quantity update failed:',
            error
          );

          this.updatingProductId.set(null);

          this.errorMessage.set(
            'Unable to update the product quantity.'
          );
        }

      });
  }


  remove(item: CartLine): void {

    const user =
      this.authService.getUser();

    if (!user) {
      return;
    }


    this.updatingProductId.set(
      item.product.id
    );

    this.errorMessage.set('');


    this.cartService
      .removeItem(
        user.userId,
        item.product.id
      )
      .subscribe({

        next: () => {

          this.updatingProductId.set(null);

          this.loadCart();
        },


        error: error => {

          console.error(
            'Remove item failed:',
            error
          );

          this.updatingProductId.set(null);

          this.errorMessage.set(
            'Unable to remove this product.'
          );
        }

      });
  }
}