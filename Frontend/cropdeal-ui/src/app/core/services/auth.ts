import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

import {
  LoginRequest,
  LoginResponse,
  RegisterRequest
} from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/users';

  constructor(private http: HttpClient) {}

  login(request: LoginRequest) {
    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/login`,
        request
      )
      .pipe(
        tap(response => {
          this.saveSession(response);
        })
      );
  }

  register(request: RegisterRequest) {
    return this.http.post(
      `${this.apiUrl}/register`,
      request
    );
  }

  logout(): void {
    localStorage.removeItem('cropdeal_token');
    localStorage.removeItem('cropdeal_user');
  }

  getToken(): string | null {
    return localStorage.getItem('cropdeal_token');
  }

  getUser(): LoginResponse | null {
    const user = localStorage.getItem('cropdeal_user');

    if (!user) {
      return null;
    }

    return JSON.parse(user);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private saveSession(response: LoginResponse): void {
    localStorage.setItem(
      'cropdeal_token',
      response.token
    );

    localStorage.setItem(
      'cropdeal_user',
      JSON.stringify(response)
    );
  }
}