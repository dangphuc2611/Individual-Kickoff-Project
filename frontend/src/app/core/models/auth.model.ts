export interface AuthResponse {
  token: string;
}

export interface UserPayload {
  userId: number;
  email: string;
  role: string;
  donViId: number;
  exp: number;
  iat: number;
}
