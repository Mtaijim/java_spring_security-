import { motion } from "framer-motion";
import { Card, CardContent } from "@/components/ui/card";
import { ShieldCheck, Lock, UsersRound, Code } from "lucide-react";

const About = () => {
  return (
    <div className="min-h-screen bg-background text-foreground px-6 py-20">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
        className="max-w-3xl mx-auto text-center"
      >
        <h1 className="text-4xl md:text-5xl font-bold tracking-tight">
          About AuthX
        </h1>
        <p className="mt-6 text-lg text-muted-foreground leading-relaxed">
          AuthX is a generic, drop-in authentication and authorization
          service built on Spring Boot and Spring Security. It handles
          everything from JWT-based sessions and refresh token rotation to
          OAuth2 social login, TOTP multi-factor authentication, and
          role-based access control — so application teams can focus on
          their product instead of reinventing auth.
        </p>
      </motion.div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-5xl mx-auto mt-16">
        {[
          {
            icon: <Lock className="w-8 h-8" />,
            title: "Security First",
            desc: "Stateless JWTs, hashed backup codes, rotated refresh tokens — built with attacker-resistant defaults.",
          },
          {
            icon: <UsersRound className="w-8 h-8" />,
            title: "Built for Teams",
            desc: "Role-based access control out of the box, with admin tooling for managing users at scale.",
          },
          {
            icon: <Code className="w-8 h-8" />,
            title: "Easy Integration",
            desc: "A clean REST API any frontend or service can consume, regardless of stack.",
          },
        ].map((item) => (
          <Card key={item.title}>
            <CardContent className="p-6 text-center">
              <div className="flex justify-center mb-4 text-primary">
                {item.icon}
              </div>
              <h3 className="text-lg font-semibold mb-2">{item.title}</h3>
              <p className="text-sm text-muted-foreground">{item.desc}</p>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="flex justify-center mt-16">
        <div className="flex items-center gap-2 text-muted-foreground text-sm">
          <ShieldCheck className="w-4 h-4" />
          Built with Spring Boot, React, and a healthy respect for OWASP.
        </div>
      </div>
    </div>
  );
};

export default About;