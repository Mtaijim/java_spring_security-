export interface Role {
  id: string;
  name: "ROLE_USER" | "ROLE_ADMIN";
}

export default interface User {
  id: string;
  email: string;
  name?: string;
  enable: boolean;
  image?: string;
  updatedAt?: string;
  createdAt?: string;
  provider: string;
  roles: Role[];
}
