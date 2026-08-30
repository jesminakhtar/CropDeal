import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

import { RegisterRequest } from '../models/auth.model';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthUser {
  token: string;
  userId: string;
  username: string;
  role: string;
  firstName: string;
  lastName: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl =
    'http://localhost:8080/users';

  private readonly USER_KEY =
    'cropdeal_user';

  private readonly TOKEN_KEY =
    'token';

  private readonly currentUserSignal =
    signal<AuthUser | null>(
      this.loadStoredUser()
    );

  readonly currentUser =
    this.currentUserSignal.asReadonly();

  constructor(
    private http: HttpClient
  ) {}

  login(
    credentials: LoginRequest
  ) {
    return this.http
      .post<AuthUser>(
        `${this.apiUrl}/login`,
        credentials
      )
      .pipe(
        tap(user => {
          this.setUser(user);
        })
      );
  }

  register(
    request: RegisterRequest
  ) {
    return this.http.post(
      `${this.apiUrl}/register`,
      request
    );
  }

  setUser(
    user: AuthUser
  ): void {

    localStorage.setItem(
      this.USER_KEY,
      JSON.stringify(user)
    );

    if (user.token) {
      localStorage.setItem(
        this.TOKEN_KEY,
        user.token
      );
    }

    this.currentUserSignal.set(
      user
    );
  }

  getUser(): AuthUser | null {
    return this.currentUserSignal();
  }

  getToken(): string | null {
    return localStorage.getItem(
      this.TOKEN_KEY
    );
  }

  isLoggedIn(): boolean {
    return this.currentUserSignal() !== null;
  }

  isFarmer(): boolean {
    return (
      this.currentUserSignal()?.role ===
      'FARMER'
    );
  }

  isDealer(): boolean {
    return (
      this.currentUserSignal()?.role ===
      'DEALER'
    );
  }

  logout(): void {

    localStorage.removeItem(
      this.USER_KEY
    );

    localStorage.removeItem(
      this.TOKEN_KEY
    );

    this.currentUserSignal.set(
      null
    );
  }

  private loadStoredUser(): AuthUser | null {

    const storedUser =
      localStorage.getItem(
        this.USER_KEY
      );

    if (!storedUser) {
      return null;
    }

    try {
      return JSON.parse(
        storedUser
      ) as AuthUser;
    } catch {

      localStorage.removeItem(
        this.USER_KEY
      );

      return null;
    }
  }
}