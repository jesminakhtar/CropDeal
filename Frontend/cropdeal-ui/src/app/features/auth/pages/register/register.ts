import { Component } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../../core/services/auth-service';
import { RegisterRequest } from '../../../../core/models/auth.model';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './register.html',
  styleUrl: './register.scss'
})
export class RegisterComponent {

  loading = false;
  errorMessage = '';
  showPassword = false;

  registerForm;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.nonNullable.group({
      role: ['FARMER' as 'FARMER' | 'DEALER', Validators.required],

      firstName: ['', Validators.required],
      lastName: ['', Validators.required],

      username: [
        '',
        [
          Validators.required,
          Validators.minLength(4)
        ]
      ],

      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],

      phoneNumber: [
        '',
        [
          Validators.required,
          Validators.pattern(/^[0-9]{10}$/)
        ]
      ],

      gender: ['', Validators.required],

      password: [
        '',
        [
          Validators.required,
          Validators.minLength(8)
        ]
      ],

      confirmPassword: ['', Validators.required]
    });
  }


  register(): void {

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const value = this.registerForm.getRawValue();

    if (value.password !== value.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request: RegisterRequest = {
      username: value.username,
      password: value.password,
      role: value.role,
      gender: value.gender,
      firstName: value.firstName,
      lastName: value.lastName,
      phoneNumber: Number(value.phoneNumber),
      email: value.email
    };

    this.authService.register(request).subscribe({
      next: () => {
        this.loading = false;

        this.router.navigate(
          ['/login'],
          {
            queryParams: {
              registered: 'true'
            }
          }
        );
      },

      error: error => {

        this.loading = false;

        console.error('Registration failed', error);

        if (error.status === 409) {
          this.errorMessage =
            'An account with these details already exists.';
        } else if (error.status === 400) {
          this.errorMessage =
            'Please check your details and try again.';
        } else {
          this.errorMessage =
            'Unable to create your account right now. Please try again.';
        }
      }
    });
  }


  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
}