import { useEffect, useState } from "react";
import { motion } from "framer-motion";
import {
  BarChart3,
  Users,
  MessageSquare,
  Heart,
  Settings,
  LogOut,
  Mail,
  Calendar,
  ArrowRight,
  TrendingUp,
} from "lucide-react";

// shadcn/ui imports — all tokens resolve automatically in light & dark
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import useAuthStore from "@/auth/store";

// ─── Stat Card ───────────────────────────────────────────────────────────────

const StatCard = ({
  icon: Icon,
  label,
  value,
  trend,
  delay,
}: {
  icon: React.ComponentType<{ className?: string }>;
  label: string;
  value: string | number;
  trend?: string;
  delay: number;
}) => (
  <motion.div
    initial={{ opacity: 0, y: 16 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ delay, duration: 0.4, ease: "easeOut" }}
  >
    <Card className="hover:shadow-md transition-shadow duration-200">
      <CardContent className="p-6">
        <div className="flex items-start justify-between">
          <div className="space-y-1">
            <p className="text-sm text-muted-foreground">{label}</p>
            <p className="text-3xl font-semibold tracking-tight">{value}</p>
            {trend && (
              <p className="text-xs text-emerald-600 dark:text-emerald-400 flex items-center gap-1">
                <TrendingUp className="w-3 h-3" />
                {trend}
              </p>
            )}
          </div>
          <div className="p-2.5 rounded-lg bg-primary/10">
            <Icon className="w-5 h-5 text-primary" />
          </div>
        </div>
      </CardContent>
    </Card>
  </motion.div>
);

// ─── Activity Item ────────────────────────────────────────────────────────────

const ActivityItem = ({
  title,
  description,
  time,
  index,
}: {
  title: string;
  description: string;
  time: string;
  index: number;
}) => (
  <motion.div
    initial={{ opacity: 0, x: -16 }}
    animate={{ opacity: 1, x: 0 }}
    transition={{ delay: 0.3 + index * 0.08, duration: 0.3 }}
    className="flex gap-3 group"
  >
    <div className="flex flex-col items-center gap-1 pt-1">
      <div className="w-2 h-2 rounded-full bg-primary flex-shrink-0" />
      {index < 3 && <div className="w-px flex-1 bg-border" />}
    </div>
    <div className="pb-4 flex-1">
      <p className="text-sm font-medium leading-none mb-1">{title}</p>
      <p className="text-sm text-muted-foreground">{description}</p>
      <p className="text-xs text-muted-foreground/70 mt-1.5">{time}</p>
    </div>
  </motion.div>
);

// ─── Quick Link Button ────────────────────────────────────────────────────────

const QuickLink = ({
  label,
  icon: Icon,
  index,
}: {
  label: string;
  icon: React.ComponentType<{ className?: string }>;
  index: number;
}) => (
  <motion.div
    initial={{ opacity: 0, x: -8 }}
    animate={{ opacity: 1, x: 0 }}
    transition={{ delay: 0.4 + index * 0.07 }}
  >
    <Button
      variant="ghost"
      className="w-full justify-between px-3 h-10 font-normal group"
    >
      <span className="flex items-center gap-3">
        <Icon className="w-4 h-4 text-muted-foreground group-hover:text-foreground transition-colors" />
        <span>{label}</span>
      </span>
      <ArrowRight className="w-3.5 h-3.5 text-muted-foreground opacity-0 group-hover:opacity-100 transition-opacity -translate-x-1 group-hover:translate-x-0 duration-150" />
    </Button>
  </motion.div>
);

// ─── Main Component ───────────────────────────────────────────────────────────

export const Userhome = () => {
  const user = useAuthStore((state) => state.user);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted || !user) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-background">
        <div className="animate-spin rounded-full h-10 w-10 border-2 border-muted border-t-primary" />
      </div>
    );
  }

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.08, delayChildren: 0.1 },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 16 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.4, ease: [0.22, 1, 0.36, 1] as const },
    },
  };

  const stats = [
    {
      icon: BarChart3,
      label: "Total Views",
      value: "2,847",
      trend: "+12% this week",
      delay: 0.1,
    },
    {
      icon: Users,
      label: "Followers",
      value: "342",
      trend: "+5 today",
      delay: 0.15,
    },
    {
      icon: MessageSquare,
      label: "Messages",
      value: "28",
      trend: "4 unread",
      delay: 0.2,
    },
    {
      icon: Heart,
      label: "Likes",
      value: "1,293",
      trend: "+89 this week",
      delay: 0.25,
    },
  ];

  const quickLinks = [
    { label: "Edit Profile", icon: Settings },
    { label: "View Analytics", icon: BarChart3 },
    { label: "Messages", icon: MessageSquare },
    { label: "Settings", icon: Settings },
  ];

  const activities = [
    {
      title: "Profile Updated",
      description: "You updated your profile picture and bio",
      time: "2 hours ago",
    },
    {
      title: "New Follower",
      description: "sarah.dev started following you",
      time: "5 hours ago",
    },
    {
      title: "Post Liked",
      description: "Your latest post received 32 likes",
      time: "1 day ago",
    },
    {
      title: "Comment Received",
      description: "john_smith commented on your post",
      time: "2 days ago",
    },
  ];

  return (
    <TooltipProvider>
      <motion.div
        className="min-h-screen bg-background p-4 md:p-8"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.3 }}
      >
        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="max-w-6xl mx-auto space-y-8"
        >
          {/* Welcome Header */}
          <motion.div variants={itemVariants}>
            <h1 className="text-3xl font-semibold tracking-tight">
              Welcome back, {user.name?.split(" ")[0] || "there"} 👋
            </h1>
            <p className="text-muted-foreground mt-1">
              Here's what's happening with your account today.
            </p>
          </motion.div>

          {/* Profile Card */}
          <motion.div variants={itemVariants}>
            <Card>
              <CardContent className="p-6 md:p-8">
                <div className="flex flex-col sm:flex-row items-start sm:items-center gap-5">
                  {/* Avatar with online dot */}
                  <div className="relative">
                    <Avatar className="w-20 h-20 ring-2 ring-border">
                      <AvatarImage src={user.image} alt={user.name} />
                      <AvatarFallback className="text-2xl font-semibold bg-primary text-primary-foreground">
                        {(user.name || user.email)[0].toUpperCase()}
                      </AvatarFallback>
                    </Avatar>
                    <motion.span
                      className="absolute bottom-0.5 right-0.5 w-4 h-4 rounded-full bg-emerald-500 ring-2 ring-background"
                      animate={{ scale: [1, 1.25, 1] }}
                      transition={{
                        duration: 2.5,
                        repeat: Infinity,
                        ease: "easeInOut",
                      }}
                    />
                  </div>

                  {/* Info */}
                  <div className="flex-1 min-w-0">
                    <div className="flex flex-wrap items-center gap-2 mb-2">
                      <h2 className="text-xl font-semibold">
                        {user.name || "User"}
                      </h2>
                      {user.enabled && (
                        <Badge
                          variant="secondary"
                          className="text-emerald-600 dark:text-emerald-400 bg-emerald-500/10"
                        >
                          Active
                        </Badge>
                      )}
                      <Badge variant="outline" className="capitalize">
                        {user.provider}
                      </Badge>
                    </div>

                    <div className="flex flex-col sm:flex-row sm:items-center gap-1.5 text-sm text-muted-foreground">
                      <span className="flex items-center gap-1.5">
                        <Mail className="w-3.5 h-3.5" />
                        {user.email}
                      </span>
                      {user.createdAt && (
                        <>
                          <span className="hidden sm:block text-border">·</span>
                          <span className="flex items-center gap-1.5">
                            <Calendar className="w-3.5 h-3.5" />
                            Joined{" "}
                            {new Date(user.createdAt).toLocaleDateString(
                              "en-US",
                              { month: "long", year: "numeric" },
                            )}
                          </span>
                        </>
                      )}
                    </div>
                  </div>

                  {/* Actions */}
                  <div className="flex items-center gap-2 self-start sm:self-center">
                    <Button size="sm" className="gap-2">
                      <Settings className="w-3.5 h-3.5" />
                      Settings
                    </Button>
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <Button size="sm" variant="outline" className="px-2.5">
                          <LogOut className="w-4 h-4" />
                        </Button>
                      </TooltipTrigger>
                      <TooltipContent>Sign out</TooltipContent>
                    </Tooltip>
                  </div>
                </div>
              </CardContent>
            </Card>
          </motion.div>

          {/* Stats Grid */}
          <motion.div
            variants={itemVariants}
            className="grid grid-cols-2 lg:grid-cols-4 gap-4"
          >
            {stats.map((s) => (
              <StatCard key={s.label} {...s} />
            ))}
          </motion.div>

          {/* Main Content */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Recent Activity */}
            <motion.div variants={itemVariants} className="lg:col-span-2">
              <Card className="h-full">
                <CardHeader className="pb-2">
                  <div className="flex items-center justify-between">
                    <CardTitle className="text-base font-semibold">
                      Recent Activity
                    </CardTitle>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="text-muted-foreground h-8 px-2 gap-1"
                    >
                      View all
                      <ArrowRight className="w-3.5 h-3.5" />
                    </Button>
                  </div>
                </CardHeader>
                <CardContent className="pt-4">
                  {activities.map((a, i) => (
                    <ActivityItem key={a.title} {...a} index={i} />
                  ))}
                </CardContent>
              </Card>
            </motion.div>

            {/* Quick Links + Upgrade */}
            <motion.div variants={itemVariants} className="space-y-4">
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-base font-semibold">
                    Quick Links
                  </CardTitle>
                </CardHeader>
                <CardContent className="pt-0 space-y-1">
                  {quickLinks.map((l, i) => (
                    <QuickLink key={l.label} {...l} index={i} />
                  ))}
                </CardContent>
              </Card>

              {/* Upgrade Banner */}
              <motion.div
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.7 }}
              >
                <Card className="border-primary/20 bg-primary/5">
                  <CardContent className="p-5">
                    <p className="font-semibold text-sm mb-1">
                      Upgrade your plan
                    </p>
                    <p className="text-xs text-muted-foreground mb-4 leading-relaxed">
                      Unlock advanced analytics, priority support, and unlimited
                      exports.
                    </p>
                    <Button size="sm" className="w-full">
                      See plans
                    </Button>
                  </CardContent>
                </Card>
              </motion.div>
            </motion.div>
          </div>
        </motion.div>
      </motion.div>
    </TooltipProvider>
  );
};
