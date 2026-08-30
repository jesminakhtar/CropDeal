import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Address } from '../../../../core/models/address.model';
import { Order, Receipt } from '../../../../core/models/order.model';
import { Cart } from '../../../../core/models/cart.model';
import { AuthService } from '../../../../core/services/auth-service';
import { AddressService } from '../../../../core/services/address-service';
import { CartService } from '../../../../core/services/cart-service';
import { OrderService } from '../../../../core/services/order-service';
import { PaymentService } from '../../../../core/services/payment-service';


declare const Razorpay: any;


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
  paymentProcessing = signal(false);
  paymentSuccessful = signal(false);
  errorMessage = signal('');
  addresses = signal<Address[]>([]);
  selectedAddressId = signal('');
  cart = signal<Cart | null>(null);
  showAddressForm = signal(false);
  placedOrder = signal<Order | null>(null);

  receipt = signal<Receipt | null>(null);
  addressForm: FormGroup;


  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private addressService: AddressService,
    private cartService: CartService,
    private orderService: OrderService,
    private paymentService: PaymentService,
    private router: Router
  ) {

    this.addressForm =
      this.fb.nonNullable.group({
        name: ['Home', Validators.required],
        houseNo: ['', Validators.required],
        roadName: ['', Validators.required],
        landmark: [''],
        pin: ['', [
            Validators.required,
            Validators.pattern(/^[0-9]{6}$/)]
          ],
        city: ['', Validators.required],
        state: ['', Validators.required],
        country: ['India', Validators.required],
        type: ['home', Validators.required]
      });
  }


  ngOnInit(): void {

    const user = this.authService.getUser();

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadCheckout(
      user.userId
    );
  }


  get productCount(): number {
    const currentCart = this.cart();

    if (!currentCart?.cartItems) {
      return 0;
    }

    return Object.keys(currentCart.cartItems).length;
  }


  private loadCheckout( userId: string ): void {
    this.loading.set(true);
    this.errorMessage.set('');
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
            'Checkout cart loading failed:',
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
      .getUserAddresses(
        userId
      )
      .subscribe({

        next: addresses => {

          const userAddresses =
            addresses ?? [];


          this.addresses.set(
            userAddresses
          );


          if (
            userAddresses.length > 0
          ) {

            this.selectedAddressId.set(
              userAddresses[0].id
            );

          } else {

            this.showAddressForm.set(
              true
            );
          }
        },


        error: error => {

          console.error(
            'Address loading failed:',
            error
          );


          if (
            error.status === 404
          ) {

            this.addresses.set(
              []
            );


            this.showAddressForm.set(
              true
            );


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


    this.errorMessage.set(
      ''
    );
  }


  toggleAddressForm(): void {

    this.showAddressForm.update(
      current =>
        !current
    );


    this.errorMessage.set(
      ''
    );
  }


  saveAddress(): void {

    if (
      this.addressForm.invalid
    ) {

      this.addressForm
        .markAllAsTouched();

      return;
    }


    const user =
      this.authService.getUser();


    if (!user) {

      this.router.navigate([
        '/login'
      ]);

      return;
    }


    const formValue =
      this.addressForm
        .getRawValue();


    this.submitting.set(
      true
    );


    this.errorMessage.set(
      ''
    );


    this.addressService
      .createAddress({

        userId:
          user.userId,

        name:
          formValue.name,

        houseNo:
          formValue.houseNo,

        roadName:
          formValue.roadName,

        landmark:
          formValue.landmark,

        pin:
          formValue.pin,

        city:
          formValue.city,

        state:
          formValue.state,

        country:
          formValue.country,

        type:
          formValue.type

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


          this.showAddressForm.set(
            false
          );


          this.addressForm.reset({

            name:
              'Home',

            houseNo:
              '',

            roadName:
              '',

            landmark:
              '',

            pin:
              '',

            city:
              '',

            state:
              '',

            country:
              'India',

            type:
              'home'

          });


          this.submitting.set(
            false
          );
        },


        error: error => {

          console.error(
            'Address creation failed:',
            error
          );


          this.submitting.set(
            false
          );


          this.errorMessage.set(
            'Unable to save this address.'
          );
        }

      });
  }


  placeOrder(): void {

    const user =
      this.authService.getUser();


    const addressId =
      this.selectedAddressId();


    if (!user) {

      this.router.navigate([
        '/login'
      ]);

      return;
    }


    if (!addressId) {

      this.errorMessage.set(
        'Please choose a delivery address.'
      );

      return;
    }


    const currentCart =
      this.cart();


    if (
      !currentCart ||
      !currentCart.cartItems ||
      Object.keys(
        currentCart.cartItems
      ).length === 0
    ) {

      this.errorMessage.set(
        'Your cart is empty.'
      );

      return;
    }


    this.submitting.set(
      true
    );


    this.errorMessage.set(
      ''
    );


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


          this.submitting.set(
            false
          );
        },


        error: error => {

          console.error(
            'Order placement failed:',
            error
          );


          this.submitting.set(
            false
          );


          this.errorMessage.set(
            'Unable to place your order.'
          );
        }

      });
  }


  /*
   * Starts Razorpay Standard Checkout.
   */
  payNow(): void {

    const order =
      this.placedOrder();


    if (!order) {

      this.errorMessage.set(
        'No pending order found.'
      );

      return;
    }


    const user =
      this.authService.getUser();


    if (!user) {

      this.router.navigate([
        '/login'
      ]);

      return;
    }


    this.paymentProcessing.set(
      true
    );


    this.errorMessage.set(
      ''
    );


    this.paymentService
      .initiatePayment(
        order.orderId
      )
      .subscribe({

        next: payment => {

          const options = {

            /*
             * Public Razorpay Test key.
             */
            key:
              payment.apikey,


            /*
             * Already in paise.
             */
            amount:
              payment.amount,


            currency:
              payment.currency,


            name:
              'CropDeal',


            description:
              `Payment for order ${order.orderId}`,


            /*
             * Razorpay order ID generated
             * by our Spring backend.
             */
            order_id:
              payment.orderId,


            prefill: {

              name:
                `${user.firstName ?? ''} ${user.lastName ?? ''}`
                  .trim(),

              email:
                user.email ?? ''

            },


            theme: {

              color:
                '#17633c'

            },


            /*
             * Razorpay calls this after
             * successful Test Mode payment.
             */
            handler:
              (response: any) => {

                this.verifyPayment(
                  order.orderId,
                  response
                );
              },


            modal: {

              ondismiss:
                () => {

                  this.paymentProcessing.set(
                    false
                  );
                }

            }

          };


          try {

            const razorpay =
              new Razorpay(
                options
              );


            razorpay.on(
              'payment.failed',
              (response: any) => {

                console.error(
                  'Razorpay payment failed:',
                  response.error
                );


                this.errorMessage.set(
                  response.error?.description ??
                  'Payment failed. Please try again.'
                );


                this.paymentProcessing.set(
                  false
                );
              }
            );


            razorpay.open();


          } catch (error) {

            console.error(
              'Could not open Razorpay:',
              error
            );


            this.paymentProcessing.set(
              false
            );


            this.errorMessage.set(
              'Unable to open Razorpay Checkout.'
            );
          }
        },


        error: error => {

          console.error(
            'Payment initiation failed:',
            error
          );


          this.paymentProcessing.set(
            false
          );


          this.errorMessage.set(
            error.error?.message ??
            'Unable to start payment.'
          );
        }

      });
  }


  /*
   * Send Razorpay's callback values to
   * Spring Boot.
   *
   * Backend verifies signature and,
   * when valid:
   *
   * - checks captured payment
   * - checks amount
   * - checks currency
   * - marks order Placed
   * - paymentStatus = Done
   * - saves payment method
   * - reduces inventory
   * - creates transaction
   * - creates receipt
   * - clears cart
   */
  private verifyPayment(
    cropDealOrderId: string,
    response: any
  ): void {

    if (
      !response?.razorpay_payment_id ||
      !response?.razorpay_order_id ||
      !response?.razorpay_signature
    ) {

      this.paymentProcessing.set(
        false
      );


      this.errorMessage.set(
        'Invalid response received from Razorpay.'
      );


      return;
    }


    this.paymentService
      .verifyPayment({

        cropDealOrderId:
          cropDealOrderId,

        razorpayPaymentId:
          response.razorpay_payment_id,

        razorpayOrderId:
          response.razorpay_order_id,

        razorpaySignature:
          response.razorpay_signature

      })
      .subscribe({

        next: receipt => {

          this.receipt.set(
            receipt
          );


          this.paymentProcessing.set(
            false
          );


          this.paymentSuccessful.set(
            true
          );


          /*
           * Reload actual order so the UI gets
           * paymentStatus, paymentMode and
           * transactionId saved by backend.
           */
          this.orderService
            .getOrderById(
              cropDealOrderId
            )
            .subscribe({

              next: updatedOrder => {

                this.placedOrder.set(
                  updatedOrder
                );
              },

              error: error => {

                console.error(
                  'Could not reload order:',
                  error
                );


                /*
                 * Payment already succeeded,
                 * so don't show payment failure
                 * just because refresh failed.
                 */
                this.placedOrder.update(
                  current => {

                    if (!current) {
                      return current;
                    }

                    return {
                      ...current,
                      status: 'Placed',
                      paymentStatus: 'Done',
                      transactionId:
                        receipt.transactionId
                    };
                  }
                );
              }

            });


          /*
           * Backend has cleared this cart.
           */
          this.cart.set(
            null
          );


          this.errorMessage.set(
            ''
          );
        },


        error: error => {

          console.error(
            'Payment verification failed:',
            error
          );


          this.paymentProcessing.set(
            false
          );


          this.errorMessage.set(
            error.error?.message ??
            'Payment could not be verified.'
          );
        }

      });
  }
}