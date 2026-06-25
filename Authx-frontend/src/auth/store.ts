import type User from "@/models/User";
import type { LoginData } from "@/models/LoginData";
import { create } from "zustand";
import type LoginResponseData from "@/models/LoginResponseData";
import { loginUser, logoutUser } from "@/services/Authservice";
import { persist } from "zustand/middleware";

const Local_key = "auth_state";

type AuthState = {
  accessToken: string | null;
  user: User | null;
  authStatus: boolean;
  authLoading: boolean;
  login: (loginData: LoginData) => Promise<LoginResponseData>;
  logout: (silent?: boolean) => void;
  checkLogin: () => boolean | undefined;

  changeLocalLoginData: (
    accessToken: string,
    user: User,
    authStatus: boolean,
  ) => void;
};

const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      accessToken: null,
      user: null,
      authStatus: false,
      authLoading: false,

      login: async (loginData) => {
        try {
          console.log("login...");
          set({ authLoading: true });

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

      logout: async () => {
        try {
          set({
            authLoading: true,
          });
          await logoutUser();
        } catch (error) {
        } finally {
          set({
            authLoading: false,
          });
        }
        // await logoutUser();
        set({
          accessToken: null,
          user: null,
          authLoading: false,
          authStatus: false,
        });
      },

      checkLogin: () => {
        return !!(get().accessToken && get().authStatus);
      },

      changeLocalLoginData: (accessToken, user, authStatus) => {
        set({
          accessToken,
          user,
          authStatus,
        });
      },
    }),
    {
      name: Local_key,
    },
  ),
);

export default useAuthStore;
