import { useLocation, useNavigate } from "react-router";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Mail } from "lucide-react";

const CheckEmail = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const email = location.state?.email || "your email";
  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <Card className="w-full max-w-md">
        <CardContent className="p-8 text-center space-y-4">
          <div className="flex justify-center">
            <div className="p-4 rounded-full bg-primary/10">
              <Mail className="w-10 h-10 text-primary" />
            </div>
          </div>
          <h1 className="text-2xl font-semibold">Check your email</h1>
          <p className="text-muted-foreground">
            We sent a verification link to{" "}
            <span className="font-medium text-foreground">{email}</span>. Click
            the link in the email to activate your account.
          </p>
          <p className="text-sm text-muted-foreground">
            Didn't receive it? Check your spam folder.
          </p>
          <Button
            variant="outline"
            className="w-full"
            onClick={() => navigate("/login")}
          >
            Back to Login
          </Button>
        </CardContent>
      </Card>
    </div>
  );
};

export default CheckEmail;
