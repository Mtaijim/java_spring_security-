import { motion } from "framer-motion";
import { Card, CardContent } from "../ui/card";
import { Button } from "../ui/button";
import {
  ShieldCheck,
  Lock,
  UsersRound,
  Sparkles,
  ArrowRight,
  Bolt,
  UserRound,
  Key,
  Code,
} from "lucide-react";
const AuthHome = () => {
  return (
    <div className="min-h-screen bg-background text-foreground transition-colors overflow-hidden">
      {/* Hero Section */}
      <section className="relative py-28 px-6 text-center flex flex-col items-center justify-center">
        <motion.h1
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
          className="text-5xl md:text-7xl font-bold tracking-tight"
        >
          Ship Features. We'll Handle Security.
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3, duration: 0.8 }}
          className="mt-6 max-w-2xl text-lg md:text-xl text-muted-foreground"
        >
          Built for developers who demand seamless security, lightning-fast
          authentication, and enterprise-grade protection.
        </motion.p>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5, duration: 0.8 }}
          className="mt-10 flex gap-4"
        >
          <Button size="lg" className="rounded text-lg font-semibold px-6">
            {" "}
            Get Started ➜
          </Button>
          <Button
            variant="outline"
            size="lg"
            className="rounded text-lg font-semibold px-6 flex items-center gap-2"
          >
            Learn More
          </Button>
        </motion.div>
      </section>
      {/* Features Section */}
      <section>
        <h2 className="text-4xl font-bold text-center mb-16 px-10">
          Everything You Need for Secure Authentication
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 max-w-6xl mx-auto">
          {[
            {
              title: "JWT Security",
              desc: "Access and refresh token authentication built with Spring Security and JWT.",
              icon: <Lock className="w-10 h-10" />,
            },
            {
              title: "Social Authentication",
              desc: "One-click login using Google and GitHub OAuth2 providers.",
              icon: <UsersRound className="w-10 h-10" />,
            },
            {
              title: "RBAC Authorization",
              desc: "Admin and User roles with endpoint-level access control.",
              icon: <ShieldCheck className="w-10 h-10" />,
            },
          ].map((f, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6, delay: i * 0.2 }}
              viewport={{ once: true }}
            >
              <Card className="bg-card/70 backdrop-blur-xl border-border rounded-2xl shadow-lg">
                <CardContent className="p-10 text-center">
                  <div className="flex justify-center mb-6 text-primary">
                    {f.icon}
                  </div>
                  <h3 className="text-2xl font-semibold mb-3">{f.title}</h3>
                  <p className="text-muted-foreground">{f.desc}</p>
                </CardContent>
              </Card>
            </motion.div>
          ))}
        </div>
      </section>
      <section className="max-w-4xl mx-auto px-6 mb-20 mt-20">
        <div className="text-center mb-10">
          <h2 className="text-3xl font-bold mb-2">Why AuthX?</h2>
          <p className="text-muted-foreground">
            Built for developers, designed for scale.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {[
            {
              icon: <Bolt className="w-5 h-5 text-blue-600" />,
              title: "Stateless JWT sessions",
              desc: "No server-side session storage. Scales horizontally without extra infrastructure.",
            },
            {
              icon: <UserRound className="w-5 h-5 text-purple-600" />,
              title: "OAuth2 social login",
              desc: "Let users sign in with Google or GitHub — zero friction onboarding experience.",
            },
            {
              icon: <Key className="w-5 h-5 text-emerald-600" />,
              title: "Role-based access control",
              desc: "Protect any endpoint with granular admin and user role permissions.",
            },
            {
              icon: <Code className="w-5 h-5 text-amber-600" />,
              title: "Developer first",
              desc: "Clean Spring Boot integration. Drop it into your existing project in minutes.",
            },
          ].map((item) => (
            <div
              key={item.title}
              className="flex gap-4 p-5 bg-card border border-border rounded-xl hover:shadow-sm transition-shadow"
            >
              <div className="mt-0.5 flex-shrink-0">{item.icon}</div>
              <div>
                <h3 className="text-sm font-semibold mb-1">{item.title}</h3>
                <p className="text-sm text-muted-foreground leading-relaxed">
                  {item.desc}
                </p>
              </div>
            </div>
          ))}
        </div>
      </section>
      <section className="py-10 px-6 text-center relative overflow-hidden">
        <div className="absolute inset-0 bg-primary/3 pointer-events-none" />

        <motion.div
          initial={{ opacity: 0, y: 24 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.6 }}
          className="relative z-10 max-w-2xl mx-auto"
        >
          <div
            className="w-16 h-16 rounded-full border-2 border-primary/20
            flex items-center justify-center mx-auto mb-6 bg-primary/5"
          >
            <ShieldCheck className="w-7 h-7 text-primary" />
          </div>

          <h2 className="text-4xl md:text-5xl font-bold leading-tight">
            Start securing your app today
          </h2>
          <p className="mt-4 text-muted-foreground text-lg">
            Join thousands of developers already shipping with AuthX.
          </p>

          <div className="mt-8 flex flex-wrap gap-3 justify-center">
            <Button
              size="lg"
              className="rounded-xl px-8 text-base font-semibold gap-2 group"
            >
              Create free account
              <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
            </Button>
            <Button
              variant="outline"
              size="lg"
              className="rounded-xl px-8 text-base font-semibold gap-2"
            >
              <UsersRound className="w-4 h-4" />
              View on GitHub
            </Button>
          </div>
        </motion.div>
      </section>

      {/* Footer */}
      <footer className="py-10 text-center text-muted-foreground border-t border-border">
        © {new Date().getFullYear()} AuthX. All rights reserved.
      </footer>
    </div>
  );
};

export default AuthHome;
