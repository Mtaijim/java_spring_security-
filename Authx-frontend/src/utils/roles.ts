import type User from "@/models/User";

export const isAdmin = (user: User | null): boolean =>
  !!user?.roles?.some((r) => r.name === "ROLE_ADMIN");

export const isSelf = (user: User | null, targetId: string): boolean =>
  user?.id === targetId;
