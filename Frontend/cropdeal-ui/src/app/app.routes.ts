import { Routes } from '@angular/router';
import { farmerGuard } from './core/guards/farmer.guard';
import { buyerGuard } from './core/guards/buyer.guard';
import { OrdersComponent } from './features/buyer/orders/orders';
import { OrderDetailsComponent } from './features/buyer/order-details/order-details';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/pages/home/home')
        .then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login')
        .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/pages/register/register')
        .then(m => m.RegisterComponent)
  },
  {
    path: 'farmer/dashboard',
    canActivate: [farmerGuard],
    loadComponent: () =>
      import('./features/farmer/pages/dashboard/dashboard')
        .then(m => m.DashboardComponent)
  },
  {
    path: 'farmer/shop/new',
    canActivate: [farmerGuard],
    loadComponent: () =>
      import('./features/farmer/pages/create-shop/create-shop')
        .then(m => m.CreateShopComponent)
  },
  {
    path: 'farmer/products/new/:shopId',
    canActivate: [farmerGuard],
    loadComponent: () =>
      import('./features/farmer/pages/add-product/add-product')
        .then(m => m.AddProductComponent)
  },
  {
  path: 'marketplace',
    loadComponent: () =>
      import('./features/marketplace/pages/marketplace/marketplace')
        .then(m => m.MarketplaceComponent)
  },
  {
  path: 'products/:id',
    loadComponent: () =>
      import('./features/marketplace/pages/product-details/product-details')
        .then(m => m.ProductDetailsComponent)
  },
  {
    path: 'cart',
    loadComponent: () =>
      import('./features/cart/pages/cart/cart')
        .then(m => m.CartComponent)
  },
  {
  path: 'checkout',
    canActivate: [buyerGuard],
    loadComponent: () =>
      import('./features/checkout/pages/checkout/checkout')
        .then(m => m.CheckoutComponent)
  },
  {
  path: 'orders',
    component: OrdersComponent,
    canActivate: [buyerGuard]
  },
  {
  path: 'orders/:orderId',
    component: OrderDetailsComponent,
    canActivate: [buyerGuard]
  },
  {
    path: '**',
    redirectTo: ''
  },
];