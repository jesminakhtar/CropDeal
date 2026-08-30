import {
  Component,
  OnInit,
  signal
} from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  DecimalPipe
} from '@angular/common';

import {
  Router,
  RouterLink
} from '@angular/router';

import { AuthService } from '../../../../core/services/auth-service';
import { AddressService } from '../../../../core/services/address-service';
import { CartService } from '../../../../core/services/cart-service';
import { OrderService } from '../../../../core/services/order-service';

import { Address } from '../../../../core/models/address.model';
import { Cart } from '../../../../core/models/cart.model';
import { Order } from '../../../../core/models/order.model';

@Component({
  selector: 'app-checkout',
  imports: [
    ReactiveFormsModule,
    DecimalPipe,
    RouterLink
  ],
  templateUrl: './checkout.html',
  styleUrl: './checkout.scss'
})
export class CheckoutComponent implements OnInit {

  loading = signal(true);

  submitting = signal(false);

  errorMessage = signal('');

  addresses = signal<Address[]>([]);

  selectedAddressId = signal('');

  cart = signal<Cart | null>(null);

  showAddressForm = signal(false);

  placedOrder = signal<Order | null>(null);


  addressForm;


  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private addressService: AddressService,
    private cartService: CartService,
    private orderService: OrderService,
    private router: Router
  ) {

    this.addressForm =
      this.fb.nonNullable.group({

        name: [
          'Home',
          Validators.required
        ],

        houseNo: [
          '',
          Validators.required
        ],

        roadName: [
          '',
          Validators.required
        ],

        landmark: [''],

        pin: [
          '',
          [
            Validators.required,
            Validators.pattern(/^[0-9]{6}$/)
          ]
        ],

        city: [
          '',
          Validators.required
        ],

        state: [
          '',
          Validators.required
        ],

        country: [
          'India',
          Validators.required
        ],

        type: [
          'home',
          Validators.required
        ]

      });
  }


  ngOnInit(): void {

    const user =
      this.authService.getUser();

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadCheckout(
      user.userId
    );
  }


  private loadCheckout(
    userId: string
  ): void {

    this.loading.set(true);

    this.loadAddresses(userId);

    this.cartService
      .getCart(userId)
      .subscribe({

        next: cart => {

          this.cart.set(cart);

          this.loading.set(false);
        },

        error: error => {

          console.error(
            'Checkout cart failed:',
            error
          );

          this.errorMessage.set(
            'Your cart could not be loaded.'
          );

          this.loading.set(false);
        }

      });
  }


  private loadAddresses(
    userId: string
  ): void {

    this.addressService
      .getUserAddresses(userId)
      .subscribe({

        next: addresses => {

          this.addresses.set(
            addresses ?? []
          );

          if (addresses?.length) {

            this.selectedAddressId.set(
              addresses[0].id
            );

          } else {

            this.showAddressForm.set(true);

          }
        },

        error: error => {

          console.error(
            'Address loading failed:',
            error
          );

          /*
           * A buyer with no address should
           * simply see the create form.
           */
          if (error.status === 404) {

            this.addresses.set([]);

            this.showAddressForm.set(true);

            return;
          }

          this.errorMessage.set(
            'Unable to load your delivery addresses.'
          );
        }

      });
  }


  selectAddress(
    addressId: string
  ): void {

    this.selectedAddressId.set(
      addressId
    );
  }


  toggleAddressForm(): void {

    this.showAddressForm.update(
      value => !value
    );

    this.errorMessage.set('');
  }


  saveAddress(): void {

    if (this.addressForm.invalid) {

      this.addressForm.markAllAsTouched();

      return;
    }


    const user =
      this.authService.getUser();

    if (!user) {
      return;
    }


    this.submitting.set(true);

    this.errorMessage.set('');


    this.addressService
      .createAddress({
        userId: user.userId,
        ...this.addressForm.getRawValue()
      })
      .subscribe({

        next: address => {

          this.addresses.update(
            current => [
              ...current,
              address
            ]
          );

          this.selectedAddressId.set(
            address.id
          );

          this.showAddressForm.set(false);

          this.addressForm.reset({
            name: 'Home',
            houseNo: '',
            roadName: '',
            landmark: '',
            pin: '',
            city: '',
            state: '',
            country: 'India',
            type: 'home'
          });

          this.submitting.set(false);
        },

        error: error => {

          console.error(
            'Address creation failed:',
            error
          );

          this.submitting.set(false);

          this.errorMessage.set(
            'Unable to save this address.'
          );
        }

      });
  }

  get productCount(): number {

    const currentCart = this.cart();

    if (!currentCart?.cartItems) {
      return 0;
    }

    return Object.keys(
      currentCart.cartItems
    ).length;
  }

  placeOrder(): void {

    const user =
      this.authService.getUser();

    const addressId =
      this.selectedAddressId();

    if (!user) {
      return;
    }

    if (!addressId) {

      this.errorMessage.set(
        'Please choose a delivery address.'
      );

      return;
    }


    this.submitting.set(true);

    this.errorMessage.set('');


    this.orderService
      .placeOrder(
        user.userId,
        addressId
      )
      .subscribe({

        next: order => {

          this.placedOrder.set(
            order
          );

          this.submitting.set(false);
        },

        error: error => {

          console.error(
            'Order placement failed:',
            error
          );

          this.submitting.set(false);

          this.errorMessage.set(
            'Unable to place your order right now.'
          );
        }

      });
  }
}