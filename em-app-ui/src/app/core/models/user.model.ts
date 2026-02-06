// User model interfaces
export interface User {
  id?: number;
  email: string;
  name: string;
  picture?: string;
  provider?: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  provider: string;
}
