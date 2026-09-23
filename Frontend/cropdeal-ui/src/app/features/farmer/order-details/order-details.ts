import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { AuthService } from '../../../core/services/auth-service';
import { InventoryService } from '../../../core/services/inventory-service';
import { OrderService, FarmerOrderResponse } from '../../../core/services/order-service';
import { AddressService } from '../../../core/services/address-service';
import { Address } from '../../../core/models/address.model';

interface FarmerOrderItem {
  productId: string;
  productName: string;
  quantity: number;
  price: number;
  image?: string;
}

@Component({
  selector: 'app-farmer-order-details',
  imports: [DecimalPipe, RouterLink],
  templateUrl: './order-details.html',
  styleUrl: './order-details.scss'
})
export class FarmerOrderDetailsComponent implements OnInit {

  order = signal<FarmerOrderResponse | null>(null);
  address = signal<Address | null>(null);
  items = signal<FarmerOrderItem[]>([]);
  loading = signal(true);
  errorMessage = signal('');

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private orderService: OrderService,
    private inventoryService: InventoryService,
    private addressService: AddressService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    const orderId = this.route.snapshot.paramMap.get('orderId');

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    if (!user.username || !orderId) {
      this.errorMessage.set('Unable to load this order.');
      this.loading.set(false);
      return;
    }

    this.loadOrder(user.username, orderId);
  }

  private loadOrder(username: string, orderId: string): void {
    this.orderService.getFarmerOrder(username, orderId).subscribe({
      next: order => {
        this.order.set(order);

        if (order.deliveryAddressId) {
          this.loadAddress(order.deliveryAddressId);
        }

        this.loadProducts(order);
      },
      error: error => {
        console.error('Unable to load farmer order:', error);
        this.errorMessage.set('Unable to load this sales order.');
        this.loading.set(false);
      }
    });
  }

  private loadAddress(addressId: string): void {
    this.addressService.getAddressById(addressId).subscribe({
      next: address => this.address.set(address),
      error: error => console.error('Unable to load delivery address:', error)
    });
  }

  private loadProducts(order: FarmerOrderResponse): void {
    const entries = Object.entries(order.orderItems ?? {});

    if (!entries.length) {
      this.loading.set(false);
      return;
    }

    const requests = entries.map(([productId]) => this.inventoryService.getProductById(productId));

    forkJoin(requests).subscribe({
      next: products => {
        const mappedItems = products.map((product: any, index) => {
          const [productId, quantity] = entries[index];

          return {
            productId,
            productName: this.getProductName(product),
            quantity,
            price: product.price ?? 0,
            image: this.getProductImage(product)
          };
        });

        this.items.set(mappedItems);
        this.loading.set(false);
      },
      error: error => {
        console.error('Unable to load products:', error);
        this.loading.set(false);
      }
    });
  }

  private getProductName(product: any): string {
    return product.productName ?? product.name ?? product.cropName ?? 'Crop product';
  }

  private getProductImage(product: any): string | undefined {
    const image = product.imageUrl ?? product.image ?? product.productImage ?? product.imageData ?? product.imageBase64;

    if (!image || typeof image !== 'string') {
      return undefined;
    }

    if (image.startsWith('http://') || image.startsWith('https://') || image.startsWith('data:') || image.startsWith('blob:')) {
      return image;
    }

    return `data:image/jpeg;base64,${image}`;
  }

  get saleValue(): number {
    return this.items().reduce((total, item) => total + (item.price * item.quantity), 0);
  }

  get paymentMethod(): string {
    const method = this.order()?.paymentMode;
    return method ? method.replace('_', ' ').toUpperCase() : 'RAZORPAY';
  }
}