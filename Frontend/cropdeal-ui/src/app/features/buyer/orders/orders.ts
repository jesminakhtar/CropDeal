import { Component, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth-service';
import { OrderService } from '../../../core/services/order-service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-orders',
  imports: [DecimalPipe, RouterLink],
  templateUrl: './orders.html',
  styleUrl: './orders.scss'
})
export class OrdersComponent implements OnInit {

  orders = signal<Order[]>([]);
  loading = signal(true);
  errorMessage = signal('');

  constructor(
    private authService: AuthService,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();

    if (!user) {
      this.router.navigate(['/login']);
      return;
    }

    this.loadOrders(user.userId);
  }

  private loadOrders(userId: string): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.orderService.getUserOrders(userId).subscribe({
      next: orders => {
        const sortedOrders = [...(orders ?? [])].reverse();
        this.orders.set(sortedOrders);
        this.loading.set(false);
      },
      error: error => {
        console.error('Unable to load buyer orders:', error);
        this.errorMessage.set('Unable to load your orders.');
        this.loading.set(false);
      }
    });
  }

  getItemCount(order: Order): number {
    if (!order.orderItems) {
      return 0;
    }

    return Object.values(order.orderItems)
      .reduce((total, quantity) => total + quantity, 0);
  }

  getPaymentMethod(order: Order): string {
    if (!order.paymentMode) {
      return 'Not paid';
    }

    return order.paymentMode
      .replace('_', ' ')
      .toUpperCase();
  }
}