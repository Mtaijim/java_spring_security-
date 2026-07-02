import useAuthStore from "@/auth/store";
import { Alert, AlertTitle } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { validateMfaCode, verifyBackupCode } from "@/services/MfaServices";
import { KeyRound, ShieldCheck } from "lucide-react";
import { Label } from "@/components/ui/label";
import { useState } from "react";
import toast from "react-hot-toast";
import { useLocation, useNavigate } from "react-router";
import { Card, CardContent } from "@/components/ui/card";

const MfaChallenges = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const mfaToken: string | undefined = location.state?.mfaToken;
  const changeLocalLoginData = useAuthStore((s) => s.changeLocalLoginData);

  const [mode, setMode] = useState<"totp" | "backup">("totp");
  const [code, setCode] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  if (!mfaToken) {
    navigate("/login");
    return null;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!code.trim()) {
      setError("please enter a code");
      return;
    }
    console.log("Sending code:", JSON.stringify(code.trim()));
    setLoading(true);
    setError(null);

    try {
      const result =
        mode === "totp"
          ? await validateMfaCode(mfaToken, code.trim())
          : await verifyBackupCode(mfaToken, code.trim());

      changeLocalLoginData(result.accessToken, result.user, true);
      toast.success("Login success");
      navigate("/dashboard");
    } catch (err: any) {
      setError(
        err.response?.data?.message || "Invalid code. Please try again.",
      );
    } finally {
      setLoading(false);
    }
  };
  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <Card className="w-full max-w-md">
        <CardContent className="p-8 space-y-6">
          <div className="text-center space-y-2">
            <div className="flex justify-center">
              <div className="p-4 rounded-full bg-primary/10">
                <ShieldCheck className="w-8 h-8 text-primary" />
              </div>
            </div>
            <h1 className="text-2xl font-semibold">Two-factor verification</h1>
            <p className="text-muted-foreground text-sm">
              {mode === "totp"
                ? "Enter the 6-digit code from your authenticator app."
                : "Enter one of your unused backup codes."}
            </p>
          </div>

          {error && (
            <Alert variant="destructive">
              <AlertTitle>{error}</AlertTitle>
            </Alert>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="code">
                {mode === "totp" ? "Authentication code" : "Backup code"}
              </Label>
              <Input
                id="code"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                placeholder={mode === "totp" ? "123456" : "ABCD-1234"}
                autoFocus
              />
            </div>

            <Button type="submit" className="w-full" disabled={loading}>
              {loading ? "Verifying..." : "Verify"}
            </Button>
          </form>

          <button
            type="button"
            onClick={() => {
              setMode(mode === "totp" ? "backup" : "totp");
              setCode("");
              setError(null);
            }}
            className="w-full text-sm text-primary hover:underline flex items-center justify-center gap-1.5"
          >
            <KeyRound className="w-3.5 h-3.5" />
            {mode === "totp"
              ? "Use a backup code instead"
              : "Use authenticator app instead"}
          </button>
        </CardContent>
      </Card>
    </div>
  );
};

export default MfaChallenges;
