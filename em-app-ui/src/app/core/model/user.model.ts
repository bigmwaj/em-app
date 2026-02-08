export interface AuthUserInfo {
  id?: number;
  email: string;
  name: string;
  picture?: string;
  provider?: string;
}

export interface AuthResponse {
  token: string;
  user: AuthUserInfo;
}

export interface LoginRequest {
  provider: string;
}
