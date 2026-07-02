export interface MfaSetupResponse {
  secret: string;
  Qrcode: string;
}

export interface BackupCodesResponse {
  codes: string[];
}

export interface MfaStatusResponse {
  mfaEnabled: boolean;
  backUpCodesRemaining: number;
}
