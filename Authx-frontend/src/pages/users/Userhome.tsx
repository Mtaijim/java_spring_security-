import { NavLink } from "react-router";
import useAuthStore from "@/auth/store";
import { isAdmin } from "@/utils/roles";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

import { Clock3, ShieldCheck, Users, UserPen } from "lucide-react";

const Userhome = () => {
  const user = useAuthStore((state) => state.user);

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        Loading...
      </div>
    );
  }

  const adminUser = isAdmin(user);

  const initials = (user.name || user.email || "U")
    .split(" ")
    .map((word) => word[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);

  return (
    <div className="container max-w-5xl mx-auto px-4 py-8 space-y-6">
      {/* Welcome Card */}
      <Card>
        <CardContent className="p-6">
          <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
            <div className="flex items-center gap-4">
              <Avatar className="h-14 w-14">
                <AvatarImage src={user.image} />
                <AvatarFallback>{initials}</AvatarFallback>
              </Avatar>

              <div>
                <h1 className="text-2xl font-bold">
                  Welcome back, {user.name}
                </h1>

                <p className="text-muted-foreground text-sm">
                  Manage your AuthX account and security settings.
                </p>
              </div>
            </div>

            <Button asChild>
              <NavLink to="/dashboard/profile">
                <UserPen className="mr-2 h-4 w-4" />
                Edit Profile
              </NavLink>
            </Button>
          </div>
        </CardContent>
      </Card>

      {/* Last Login */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Clock3 className="h-5 w-5" />
            Last Login
          </CardTitle>
        </CardHeader>

        <CardContent>
          <p className="font-medium">{new Date().toLocaleString()}</p>

          <p className="text-sm text-muted-foreground mt-1 capitalize">
            Signed in via {user.provider}
          </p>
        </CardContent>
      </Card>

      {/* About AuthX */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <ShieldCheck className="h-5 w-5" />
            About AuthX
          </CardTitle>
        </CardHeader>

        <CardContent className="flex justify-center items-center">
          <p className="text-sm text-muted-foreground leading-relaxed">
            AuthX is a secure authentication platform built with React, Spring
            Boot, JWT, OAuth2, RBAC, email verification, password reset and
            profile management.
          </p>
          <Button> About</Button>
        </CardContent>
      </Card>

      {/* Admin Features */}
      {adminUser && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Users className="h-5 w-5" />
              Admin Features
            </CardTitle>
          </CardHeader>

          <CardContent>
            <ul className="space-y-2 text-sm text-muted-foreground">
              <li>• User Management</li>
              <li>• CRUD Operations</li>
              <li>• Role Management</li>
              <li>• Permission Control</li>
              <li>• Account Status Management</li>
            </ul>
          </CardContent>
        </Card>
      )}
    </div>
  );
};

export default Userhome;
