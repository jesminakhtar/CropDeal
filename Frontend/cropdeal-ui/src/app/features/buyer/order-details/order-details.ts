import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { OrderService } from '../../../core/services/order-service';
import { AddressService } from '../../../core/services/address-service';
import { InventoryService } from '../../../core/services/inventory-service';
import { AuthService } from '../../../core/services/auth-service';
import { Order } from '../../../core/models/order.model';
import { Address } from '../../../core/models/address.model';

interface OrderProduct {
  productId: string;
  name: string;
  quantity: number;
  price: number;
  image?: string;
}

@Component({
  selector: 'app-order-details',
  imports: [DecimalPipe, RouterLink],
  templateUrl: './order-details.html',
  styleUrl: './order-details.scss'
})
export class OrderDetailsComponent implements OnInit {

  order = signal<Order | null>(null);
  address = signal<Address | null>(null);
  products = signal<OrderProduct[]>([]);
  loading = signal(true);
  errorMessage = signal('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private addressService: AddressService,
    private inventoryService: InventoryService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    const orderId = this.route.snapshot.paramMap.get('orderId');

    if (!orderId) {
      this.errorMessage.set('Invalid order ID.');
      this.loading.set(false);
      return;
    }

    this.loadOrder(orderId, user.userId);
  }

  private loadOrder(orderId: string, userId: string): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.orderService.getOrderById(orderId).subscribe({
      next: order => {
        if (order.dealerId !== userId) {
          this.errorMessage.set('You do not have access to this order.');
          this.loading.set(false);
          return;
        }

        this.order.set(order);
        this.loadAdditionalData(order);
      },
      error: error => {
        console.error('Unable to load order:', error);
        this.errorMessage.set('Unable to load this order.');
        this.loading.set(false);
      }
    });
  }

  private loadAdditionalData(order: Order): void {
    this.loadAddress(order.deliveryAddressId);
    this.loadProducts(order);
  }

  private loadAddress(addressId: string): void {
    if (!addressId) {
      return;
    }

    this.addressService.getAddressById(addressId).subscribe({
      next: address => {
        this.address.set(address);
      },
      error: error => {
        console.error('Unable to load delivery address:', error);
      }
    });
  }

  private loadProducts(order: Order): void {
    const entries = Object.entries(order.orderItems ?? {});

    if (entries.length === 0) {
      this.products.set([]);
      this.loading.set(false);
      return;
    }

    const requests = entries.map(([productId]) =>
      this.inventoryService.getProductById(productId)
    );

    forkJoin(requests).subscribe({
      next: products => {
        const orderProducts: OrderProduct[] = products.map((product: any, index) => {
          const [productId, quantity] = entries[index];

          return {
            productId,
            name: this.getProductName(product),
            quantity,
            price: product.price ?? 0,
            image: this.getProductImage(product)
          };
        });

        this.products.set(orderProducts);
        this.loading.set(false);
      },
      error: error => {
        console.error('Unable to load order products:', error);

        const fallbackProducts: OrderProduct[] = entries.map(([productId, quantity]) => ({
          productId,
          name: 'Product',
          quantity,
          price: 0
        }));

        this.products.set(fallbackProducts);
        this.loading.set(false);
      }
    });
  }

  get paymentMethod(): string {
    const method = this.order()?.paymentMode;

    if (!method) {
      return 'Not available';
    }

    return method.replace('_', ' ').toUpperCase();
  }

  private getProductName(product: any): string {
    return product.productName ??
      product.name ??
      product.cropName ??
      'Product';
  }

  private getProductImage(product: any): string | undefined {
    const image =
      product.imageUrl ??
      product.image ??
      product.productImage ??
      product.imageData ??
      product.imageBase64;

    if (!image || typeof image !== 'string') {
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
}