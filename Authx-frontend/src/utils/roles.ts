import type User from "@/models/User";

export const isAdmin = (user: User | null | undefined): boolean => {
  if (!user || !user.roles) return false;
  return user.roles.some((r) => r.name === "ROLE_ADMIN");
};

export const hasRole = (
  user: User | null | undefined,
  role: "ROLE_USER" | "ROLE_ADMIN",
): boolean => {
  if (!user || !user.roles) return false;
  return user.roles.some((r) => r.name === role);
};
