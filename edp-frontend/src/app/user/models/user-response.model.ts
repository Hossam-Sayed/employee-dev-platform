export interface UserResponse {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  birthdate: string;
  phoneNumber: string;
  department: string;
  position: string;
  admin: boolean;
  reportsToId?: number;
}
