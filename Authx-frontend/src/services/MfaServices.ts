import apiClient from "@/config/ApiCient";
import type LoginResponseData from "@/models/LoginResponseData";
import type {
  BackupCodesResponse,
  MfaSetupResponse,
  MfaStatusResponse,
} from "@/models/MfaModel";
import axios from "axios";

const BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";

export const getMfaSetup = async () => {
  const response = await apiClient.get<MfaSetupResponse>(`/mfa/setup`);
  return response.data;
};

export const verifyMfaSetup = async (code: string) => {
  const response = await apiClient.post<BackupCodesResponse>(
    `mfa/verify-setup`,
    { code },
  );
  return response.data;
};
export const getMfaStatus = async () => {
  const response = await apiClient.post<MfaStatusResponse>(`/mfa/status`);
  return response.data;
};

export const disableMfa = async (code: string) => {
  const response = await apiClient.post(`/mfa/disable`, { code });
  return response.data;
};

export const regenerateBackupCodes = async (code: string) => {
  const response = await apiClient.post<BackupCodesResponse>(
    `/mfa/backup/regenerate`,
    { code },
  );
  return response.data;
};
// Pre-authentication (mfaToken only)

export const validateMfaCode = async (mfaToken: string, code: string) => {
  const response = await axios.post<LoginResponseData>(
    `${BASE_URL}/mfa/validate`,
    { code },
    {
      headers: { Authorization: `Bearer ${mfaToken}` },
      withCredentials: true,
    },
  );
  return response.data;
};

export const verifyBackupCode = async (mfaToken: string, code: string) => {
  const response = await axios.post<LoginResponseData>(
    `${BASE_URL}/mfa/backup/verify`,
    { code },
    {
      headers: { Authorization: `Bearer ${mfaToken}` },
      withCredentials: true,
    },
  );
  return response.data;
};
