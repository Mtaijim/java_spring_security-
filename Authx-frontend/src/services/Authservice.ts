import apiClient from "@/config/ApiCient.ts";
import type { FormData as RegisterFormData } from "../models/FormData.ts";
import type { LoginData } from "@/models/LoginData.ts";
import type LoginResponseData from "@/models/LoginResponseData.ts";
import type User from "@/models/User.ts";

export const registerUser = async (formData: RegisterFormData) => {
  // api call to register user
  const response = await apiClient.post(`/auth/register`, formData);
  return response.data;
};

// login
export const loginUser = async (loginData: LoginData) => {
  const response = await apiClient.post<LoginResponseData>(
    `/auth/login`,
    loginData,
  );
  return response.data;
};
// logout

export const logoutUser = async () => {
  const response = await apiClient.post(`/auth/logout`);
  return response.data;
};
//get current login user
export const getCurrentUser = async (emailId: string | undefined) => {
  const response = await apiClient.get<User>(`/users/email/${emailId}`);
  return response.data;
};

//refresh token

export const refreshToken = async () => {
  const response = await apiClient.post<LoginResponseData>(`/auth/refresh`);
  return response.data;
};

// update user (JSON or FormData). If payload is FormData, it will be sent as multipart.
export const updateUser = async (userId: string, payload: any) => {
  if (payload instanceof FormData) {
    const response = await apiClient.put(`/users/${userId}`, payload);
    return response.data;
  }

  const response = await apiClient.put(`/users/${userId}`, payload);
  return response.data;
};
