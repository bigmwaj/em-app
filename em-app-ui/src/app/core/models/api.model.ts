// API response interfaces
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface ApiError {
  error: string;
  message: string;
  status: number;
}

export interface Account {
  id?: number;
  name: string;
  type: string;
  status: string;
  createdAt?: Date;
}

export interface Contact {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  createdAt?: Date;
}
