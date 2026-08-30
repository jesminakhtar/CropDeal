import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';

import { AuthService } from '../../../core/services/auth-service';
import { InventoryService } from '../../../core/services/inventory-service';
import { OrderService } from '../../../core/services/order-service';
import { Order } from '../../../core/models/order.model';

interface FarmerOrderItem {
  productId: string;
  productName: string;
  quantity: number;
  price: number;
  image?: string;
}

interface FarmerOrder {
  orderId: string;
  dealerId: string;
  status: string;
  paymentStatus?: string;
  paymentMode?: string;
  transactionId?: string;
  orderTotal: number;
  items: FarmerOrderItem[];
}

@Component({
  selector: 'app-farmer-orders',
  imports: [
    DecimalPipe,
    RouterLink
  ],
  templateUrl: './orders.html',
  styleUrl: './orders.scss'
})
export class FarmerOrdersComponent implements OnInit {

  orders = signal<FarmerOrder[]>([]);
  loading = signal(true);
  errorMessage = signal('');

  constructor(
    private authService: AuthService,
    private inventoryService: InventoryService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();

    if (!user) {
      this.router.navigate([
        '/login'
      ]);

      return;
    }

    if (!user.username) {
      this.errorMessage.set(
        'Unable to identify the farmer account.'
      );

      this.loading.set(false);

      return;
    }

    this.loadFarmerOrders(
      user.username
    );
  }

  private loadFarmerOrders(
    farmerUsername: string
  ): void {

    this.loading.set(true);
    this.errorMessage.set('');

    this.inventoryService
      .getFarmerShops(farmerUsername)
      .pipe(
        switchMap((shops: any[]) => {

          if (!shops?.length) {
            return of({
              products: [],
              orders: []
            });
          }

          const productRequests =
            shops.map(shop => {

              const shopId =
                shop.shopId ??
                shop.id;

              return this.inventoryService
                .getShopProducts(
                  shopId
                );
            });

          return forkJoin(
            productRequests
          ).pipe(
            switchMap(productGroups => {

              const products =
                productGroups.flat();

              return this.orderService
                .getAllOrders()
                .pipe(
                  switchMap(orders =>
                    of({
                      products,
                      orders
                    })
                  )
                );
            })
          );
        })
      )
      .subscribe({

        next: result => {

          this.buildFarmerOrders(
            result.products,
            result.orders
          );

          this.loading.set(false);
        },

        error: error => {

          console.error(
            'Unable to load farmer orders:',
            error
          );

          this.errorMessage.set(
            'Unable to load your sales orders.'
          );

          this.loading.set(false);
        }

      });
  }

  private buildFarmerOrders(
    products: any[],
    orders: Order[]
  ): void {

    const productMap =
      new Map<string, any>();

    products.forEach(product => {

      const productId =
        product.productId ??
        product.id;

      if (productId) {
        productMap.set(
          productId,
          product
        );
      }
    });

    const farmerOrders:
      FarmerOrder[] = [];

    for (const order of orders ?? []) {

      if (
        order.status !== 'Placed' ||
        order.paymentStatus !== 'Done'
      ) {
        continue;
      }

      const items:
        FarmerOrderItem[] = [];

      for (
        const [productId, quantity]
        of Object.entries(
          order.orderItems ?? {}
        )
      ) {

        const product =
          productMap.get(
            productId
          );

        if (!product) {
          continue;
        }

        items.push({
          productId,
          productName:
            this.getProductName(
              product
            ),
          quantity,
          price:
            product.price ?? 0,
          image:
            this.getProductImage(
              product
            )
        });
      }

      if (!items.length) {
        continue;
      }

      farmerOrders.push({
        orderId:
          order.orderId,
        dealerId:
          order.dealerId,
        status:
          order.status,
        paymentStatus:
          order.paymentStatus,
        paymentMode:
          order.paymentMode,
        transactionId:
          order.transactionId,
        orderTotal:
          order.totalPrice,
        items
      });
    }

    farmerOrders.reverse();

    this.orders.set(
      farmerOrders
    );
  }

  private getProductName(
    product: any
  ): string {

    return product.productName ??
      product.name ??
      product.cropName ??
      'Crop product';
  }

  private getProductImage(
    product: any
  ): string | undefined {

    const image =
      product.imageUrl ??
      product.image ??
      product.productImage ??
      product.imageData ??
      product.imageBase64;

    if (!image) {
      return undefined;
    }

    if (
      typeof image !== 'string'
    ) {
      return undefined;
    }

    if (
      image.startsWith('http://') ||
      image.startsWith('https://') ||
      image.startsWith('data:') ||
      image.startsWith('blob:')
    ) {
      return image;
    }

    return `data:image/jpeg;base64,${image}`;
  }

  get totalConfirmedOrders(): number {
    return this.orders().length;
  }

  get totalUnitsSold(): number {
    return this.orders()
      .flatMap(
        order =>
          order.items
      )
      .reduce(
        (
          total,
          item
        ) =>
          total +
          item.quantity,
        0
      );
  }

  get estimatedSalesValue(): number {
    return this.orders()
      .flatMap(
        order =>
          order.items
      )
      .reduce(
        (
          total,
          item
        ) =>
          total +
          (
            item.price *
            item.quantity
          ),
        0
      );
  }

  getPaymentMethod(
    order: FarmerOrder
  ): string {

    return order.paymentMode
      ? order.paymentMode
          .replace(
            '_',
            ' '
          )
          .toUpperCase()
      : 'RAZORPAY';
  }
}