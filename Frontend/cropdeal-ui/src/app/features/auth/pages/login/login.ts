import { Component } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import {
  ActivatedRoute,
  Router,
  RouterLink
} from '@angular/router';

import { AuthService } from '../../../../core/services/auth-service';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {

  loading = false;
  errorMessage = '';
  successMessage = '';
  showPassword = false;

  loginForm;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.loginForm = this.fb.nonNullable.group({
      username: [
        '',
        Validators.required
      ],
      password: [
        '',
        Validators.required
      ]
    });

    if (
      this.route.snapshot.queryParamMap.get('registered') === 'true'
    ) {
      this.successMessage =
        'Account created successfully. Sign in to continue.';
    }
  }

  login(): void {

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    const credentials =
      this.loginForm.getRawValue();

    this.authService
      .login({
        username: credentials.username.trim(),
        password: credentials.password
      })
      .subscribe({

        next: response => {

          this.loading = false;

          console.log(
            'Logged in:',
            response.username,
            response.role
          );

          /*
           * AuthService.login() already:
           * - stores the token
           * - stores the user
           * - updates currentUser signal
           *
           * So navbar updates immediately.
           */
          this.router.navigate(['/']);
        },

        error: (error: HttpErrorResponse) => {

          this.loading = false;

          console.error(
            'Login failed',
            error
          );

          if (
            error.status === 401 ||
            error.status === 403
          ) {
            this.errorMessage =
              'Incorrect username or password.';
            return;
          }

          if (error.status === 404) {
            this.errorMessage =
              'No account was found with this username.';
            return;
          }

          if (error.status === 0) {
            this.errorMessage =
              'Unable to connect to CropDeal. Please try again.';
            return;
          }

          this.errorMessage =
            'Unable to sign in right now. Please try again.';
        }
      });
  }

  togglePassword(): void {
    this.showPassword =
      !this.showPassword;
  }
}