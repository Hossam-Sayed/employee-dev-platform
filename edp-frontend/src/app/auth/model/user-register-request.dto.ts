export interface UserRegisterRequestDto {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  password: string;
  birthdate: string; 
  phoneNumber?: string;
  department?: string;
  position?: string;
  admin: boolean;
  reportsToId?: number;
}
