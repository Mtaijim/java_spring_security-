import useAuthStore from "@/auth/store";
import apiClient from "@/config/ApiCient";
import { useState } from "react";
import toast from "react-hot-toast";
import { useLocation, useNavigate } from "react-router";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Alert, AlertTitle } from "@/components/ui/alert";
import { Shield } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
const VerifyRisk = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const changeLocalLoginData = useAuthStore((s) => s.changeLocalLoginData);

  const email = location.state?.email as string;
  const [otp, SetOtp] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<String | null>(null);

  if (!email) {
    navigate("/login");
    return null;
  }

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);

    if (otp.length !== 6) {
      setError("please Enter 6 Digit-code.");
      return;
    }
    setLoading(true);

    try {
      const response = await apiClient.post("/auth/verify-risk", {
        email,
        otp,
      });

      changeLocalLoginData(response.data.accessToken, response.data.user, true);

      toast.success("Verified ! welcome back.");
      navigate("/dashboard");
    } catch (err: any) {
      const message = err.response?.data?.message;

      if (err.reponse?.status === 429) {
        setError("Too many Attempts . Please try again later");
        return;
      }
      setError(message || "Invalid or expired code.");
    } finally {
      setLoading(false);
    }
  };

  const resendOtp = async () => {
    try {
      toast.success("New code Sent to your email .");
    } catch {
      toast.error("Failed to resend code ");
    }
  };
  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <div className="w-full max-w-sm">
        <Card className="rounded-2xl shadow-lg">
          <CardContent className="p-8 space-y-6">
            {/* Icon + heading */}
            <div className="text-center space-y-2">
              <div className="w-14 h-14 rounded-2xl bg-amber-50 dark:bg-amber-950/30 flex items-center justify-center mx-auto">
                <Shield className="w-7 h-7 text-amber-500" />
              </div>
              <h1 className="text-xl font-bold">Security Verification</h1>
              <p className="text-sm text-muted-foreground leading-relaxed">
                We detected unusual activity on your account. A verification
                code has been sent to your email.
              </p>
            </div>

            {/* Email display */}
            <div className="bg-muted rounded-lg px-4 py-2 text-center">
              <p className="text-xs text-muted-foreground">Code sent to</p>
              <p className="text-sm font-medium mt-0.5">{email}</p>
            </div>

            {/* Error */}
            {error && (
              <Alert variant="destructive">
                <AlertTitle className="text-sm">{error}</AlertTitle>
              </Alert>
            )}

            {/* OTP form */}
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="otp">Verification code</Label>
                <Input
                  id="otp"
                  placeholder="000000"
                  maxLength={6}
                  value={otp}
                  onChange={(e) => SetOtp(e.target.value.replace(/\D/g, ""))}
                  className="text-center text-2xl tracking-widest font-mono"
                  autoFocus
                />
                <p className="text-xs text-muted-foreground text-center">
                  Code expires in 5 minutes
                </p>
              </div>

              <Button
                type="submit"
                className="w-full"
                disabled={loading || otp.length !== 6}
              >
                {loading ? "Verifying..." : "Verify & Login"}
              </Button>
            </form>

            {/* Resend + back */}
            <div className="flex items-center justify-between text-xs text-muted-foreground">
              <button
                onClick={resendOtp}
                className="hover:text-foreground transition-colors"
                type="button"
              >
                Resend code
              </button>
              <button
                onClick={() => navigate("/login")}
                className="hover:text-foreground transition-colors"
                type="button"
              >
                ← Back to login
              </button>
            </div>
          </CardContent>
        </Card>

        {/* Security notice */}
        <p className="text-center text-xs text-muted-foreground mt-4">
          🔒 This extra step keeps your account secure
        </p>
      </div>
    </div>
  );
};

export default VerifyRisk;
