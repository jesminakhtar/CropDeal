import { Component } from '@angular/core';
import {
  Router,
  RouterLink,
  RouterLinkActive
} from '@angular/router';
import { AuthService } from '../../core/services/auth-service';


@Component({
  selector: 'app-navbar',
  imports: [
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss'
})
export class NavbarComponent {

  constructor(
    public authService: AuthService,
    private router: Router
  ) {
    this.currentUser = this.authService.currentUser;
  }

  readonly currentUser: any;

  get displayRole(): string {

    const user =
      this.currentUser();

    if (!user) {
      return '';
    }

    if (user.role === 'FARMER') {
      return 'Farmer';
    }

    if (user.role === 'DEALER') {
      return 'Buyer';
    }

    return user.role;
  }

  logout(): void {

    this.authService.logout();

    this.router.navigate([
      '/'
    ]);
  }
}