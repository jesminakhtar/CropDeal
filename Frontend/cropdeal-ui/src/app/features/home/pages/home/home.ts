import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../../core/services/auth-service';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent {

  constructor(public authService: AuthService) {}

  get farmerCtaLabel(): string {
    const user = this.authService.currentUser();

    if (user?.role === 'FARMER') {
      return 'Go to farmer dashboard';
    }

    if (user?.role === 'DEALER') {
      return 'Browse marketplace';
    }

    return 'Start selling';
  }

  get farmerCtaRoute(): string {
    const user = this.authService.currentUser();

    if (user?.role === 'FARMER') {
      return '/farmer/dashboard';
    }

    if (user?.role === 'DEALER') {
      return '/marketplace';
    }

    return '/register';
  }
}