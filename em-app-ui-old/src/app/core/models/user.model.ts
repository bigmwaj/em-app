export interface User {
  email: string;
  name?: string;
  picture?: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  email: string;
  name?: string;
}
