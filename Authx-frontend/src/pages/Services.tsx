import { Card, CardContent } from "@/components/ui/card";
import {
  Lock,
  UsersRound,
  ShieldCheck,
  KeyRound,
  Mail,
  UserCog,
} from "lucide-react";

export const Services = () => {
  const services = [
    {
      icon: <Lock className="w-8 h-8" />,
      title: "JWT Authentication",
      desc: "Stateless access + refresh tokens with automatic rotation and reuse detection.",
    },
    {
      icon: <UsersRound className="w-8 h-8" />,
      title: "OAuth2 Social Login",
      desc: "Sign in with Google or GitHub — zero password friction for end users.",
    },
    {
      icon: <KeyRound className="w-8 h-8" />,
      title: "Multi-Factor Authentication",
      desc: "TOTP authenticator app support with one-time backup codes as a fallback.",
    },
    {
      icon: <ShieldCheck className="w-8 h-8" />,
      title: "Role-Based Access Control",
      desc: "Fine-grained admin/user permissions enforced at the endpoint level.",
    },
    {
      icon: <Mail className="w-8 h-8" />,
      title: "Email Verification & Reset",
      desc: "Secure, time-limited tokens for account activation and password recovery.",
    },
    {
      icon: <UserCog className="w-8 h-8" />,
      title: "User Management",
      desc: "Admin tooling to view, edit, promote, or remove users from one dashboard.",
    },
  ];

  return (
    <div className="min-h-screen bg-background text-foreground px-6 py-20">
      <div className="max-w-3xl mx-auto text-center mb-16">
        <h1 className="text-4xl md:text-5xl font-bold tracking-tight">
          Services
        </h1>
        <p className="mt-4 text-lg text-muted-foreground">
          Everything AuthX provides out of the box.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-w-6xl mx-auto">
        {services.map((s) => (
          <Card key={s.title}>
            <CardContent className="p-6">
              <div className="mb-4 text-primary">{s.icon}</div>
              <h3 className="text-lg font-semibold mb-2">{s.title}</h3>
              <p className="text-sm text-muted-foreground">{s.desc}</p>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
};
