import type User from "@/models/User";
import type { LoginData } from "@/models/LoginData";
import { create } from "zustand";
import type LoginResponseData from "@/models/LoginResponseData";
import { loginUser, logoutUser } from "@/services/Authservice";
import { persist } from "zustand/middleware";

const LOCAL_KEY = "auth_state";

type AuthState = {
  accessToken: string | null;
  user: User | null;
  authStatus: boolean;
  authLoading: boolean;

  login: (loginData: LoginData) => Promise<LoginResponseData>;
  logout: (silent?: boolean) => Promise<void>;
  checkLogin: () => boolean;
  setAccessToken: (token: string) => void;
  clearAuth: () => void;
  changeLocalLoginData: (
    accessToken: string,
    user: User,
    authStatus: boolean,
  ) => void;
};

const CLEARED_AUTH = {
  accessToken: null,
  user: null,
  authStatus: false,
  authLoading: false,
} as const;

const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      accessToken: null,
      user: null,
      authStatus: false,
      authLoading: false,

      login: async (loginData) => {
        set({ authLoading: true });
        try {
          const loginResponseData = await loginUser(loginData);
          set({
            accessToken: loginResponseData.accessToken,
            user: loginResponseData.user,
            authStatus: true,
          });
          return loginResponseData;
        } finally {
          set({ authLoading: false });
        }
      },

      // silent=true: clears local state without calling the API.
      // Used by the axios interceptor when a token refresh fails.
      logout: async (silent = false) => {
        set({ authLoading: true });
        try {
          if (!silent) await logoutUser();
        } catch {
          // Server-side logout errors are non-fatal; always clear locally.
        } finally {
          set(CLEARED_AUTH);
        }
      },

      checkLogin: () => {
        return !!(get().accessToken && get().authStatus);
      },

      // Updates the in-memory access token after a silent refresh.
      setAccessToken: (token) => {
        set({ accessToken: token });
      },

      // Wipes all auth state locally without hitting the API.
      // Called by the axios interceptor when a refresh attempt fails.
      clearAuth: () => {
        set(CLEARED_AUTH);
      },

      changeLocalLoginData: (accessToken, user, authStatus) => {
        set({ accessToken, user, authStatus });
      },
    }),
    {
      name: LOCAL_KEY,
    },
  ),
);

export default useAuthStore;
