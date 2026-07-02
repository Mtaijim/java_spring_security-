import { useNavigate } from "react-router";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ShieldAlert } from "lucide-react";

const OAuthFailure = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex items-center justify-center bg-background px-4">
      <Card className="w-full max-w-md">
        <CardContent className="p-8 text-center space-y-4">
          <div className="flex justify-center">
            <div className="p-4 rounded-full bg-destructive/10">
              <ShieldAlert className="w-10 h-10 text-destructive" />
            </div>
          </div>
          <h1 className="text-2xl font-semibold">Sign-in failed</h1>
          <p className="text-muted-foreground">
            We couldn't complete sign-in with your provider. This can happen if
            access was denied or the session expired.
          </p>
          <div className="flex flex-col gap-2">
            <Button className="w-full" onClick={() => navigate("/login")}>
              Back to Login
            </Button>
            <Button
              variant="outline"
              className="w-full"
              onClick={() => navigate("/")}
            >
              Go Home
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default OAuthFailure;
