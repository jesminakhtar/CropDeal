export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: string;
  username: string;
  role: 'FARMER' | 'DEALER';
  firstName: string;
  lastName: string;
  email: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  role: 'FARMER' | 'DEALER';
  gender: string;
  firstName: string;
  lastName: string;
  phoneNumber: number;
  email: string;
}