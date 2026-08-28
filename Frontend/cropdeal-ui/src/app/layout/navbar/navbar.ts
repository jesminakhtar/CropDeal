import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/services/auth';
import { LoginResponse } from '../../core/models/auth.model';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
})
export class NavbarComponent {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  get currentUser(): LoginResponse | null {
    return this.authService.getUser();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  get displayRole(): string {
    if (this.currentUser?.role === 'FARMER') {
      return 'Farmer';
    }

    if (this.currentUser?.role === 'DEALER') {
      return 'Buyer';
    }

    return '';
  }
}