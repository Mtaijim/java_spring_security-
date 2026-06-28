import { useEffect, useState } from "react";
import { useSearchParams, useNavigate } from "react-router";
import apiClient from "@/config/ApiCient";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

const VerifyEmail = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [status, setStatus] = useState<"loading" | "success" | "error">(
    "loading",
  );
  const [message, setMessage] = useState("");

  useEffect(() => {
    const token = searchParams.get("token");
    if (!token) {
      setStatus("error");
      setMessage("No verification token found in the link");
      return;
    }

    apiClient
      .get(`/auth/verify?token=${token}`)
      .then(() => {
        setStatus("success");
        setMessage("Your email has been verified ! you can now login");
      })
      .catch((err) => {
        setStatus("error");
        setMessage(
          err.response?.data?.message ||
            "Verification failed. The link may have expired or already been used.",
        );
      });
  }, []);
  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <Card className="w-full max-w-md">
        <CardContent className="p-8 text-center space-y-4">
          {status === "loading" && (
            <>
              <div className="animate-spin rounded-full h-10 w-10 border-2 border-muted border-t-primary mx-auto" />
              <p className="text-muted-foreground">Verifying your email...</p>
            </>
          )}
          {status === "success" && (
            <>
              <div className="text-5xl">✅</div>
              <h1 className="text-2xl font-semibold">Email Verified!</h1>
              <p className="text-muted-foreground">{message}</p>
              <Button className="w-full" onClick={() => navigate("/login")}>
                Go to Login
              </Button>
            </>
          )}

          {status === "error" && (
            <>
              <div className="text-5xl">❌</div>
              <h1 className="text-2xl font-semibold">Verification Failed</h1>
              <p className="text-muted-foreground">{message}</p>
              <Button
                className="w-full"
                variant="outline"
                onClick={() => navigate("/signup")}
              >
                Back to Sign Up
              </Button>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default VerifyEmail;
