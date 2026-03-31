export interface User {
  id?: number;
  name: string;
  email: string;
  role: string;
  password?: string;
  donViId?: number;
  tenDonVi?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
