import React, { useEffect, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

import { Pencil } from "lucide-react";
import useAuthStore from "@/auth/store";

import { updateUser } from "@/services/Authservice";
import { useNavigate } from "react-router";

const UserProfile: React.FC = () => {
  const user = useAuthStore((s) => s.user);
  const accessToken = useAuthStore((s) => s.accessToken);
  const changeLocalLoginData = useAuthStore((s) => s.changeLocalLoginData);
  const navigate = useNavigate();

  const [isEditing, setIsEditing] = useState(false);
  const [name, setName] = useState("");
  const [enabled, setEnabled] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (user) {
      setName(user.name ?? "");
      setEnabled(user.enable ?? false);
    }
  }, [user]);

  const onEdit = () => {
    // Reset to current saved values before opening edit mode
    setName(user?.name ?? "");
    setEnabled(user?.enable ?? false);
    setError(null);
    setIsEditing(true);
  };

  const onCancel = () => {
    setIsEditing(false);
    setError(null);
  };

  const onSave = async () => {
    if (!user) return;
    setIsSaving(true);
    setError(null);

    try {
      const serverUser = await updateUser(user.id, { name, enable: enabled });
      changeLocalLoginData(accessToken ?? "", serverUser, true);
      setIsEditing(false);
      navigate("/dashboard");
    } catch (err) {
      console.error("Failed to update user:", err);
      setError("Failed to save changes. Please try again.");
    } finally {
      setIsSaving(false);
    }
  };

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-background">
        <div className="animate-spin rounded-full h-10 w-10 border-2 border-muted border-t-primary" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background p-4 md:p-8">
      <div className="max-w-2xl mx-auto">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Profile</CardTitle>
            {!isEditing && (
              <Button variant="ghost" size="sm" onClick={onEdit}>
                <Pencil className="w-4 h-4 mr-2" />
                Change Name
              </Button>
            )}
          </CardHeader>

          <CardContent className="space-y-6">
            {/* Avatar */}
            <div className="flex items-center gap-4">
              <Avatar className="w-16 h-16">
                {user.image ? (
                  <AvatarImage src={user.image} alt={user.name} />
                ) : (
                  <AvatarFallback>
                    {(user.name || user.email)[0].toUpperCase()}
                  </AvatarFallback>
                )}
              </Avatar>
              <div>
                <p className="font-medium">{user.name}</p>
                <p className="text-sm text-muted-foreground">{user.email}</p>
              </div>
            </div>

            {/* Fields */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <Label htmlFor="name">Name</Label>
                <Input
                  id="name"
                  value={isEditing ? name : user.name}
                  onChange={(e) => setName(e.target.value)}
                  readOnly={!isEditing}
                  className={!isEditing ? "bg-muted cursor-default" : ""}
                />
              </div>

              <div className="flex flex-col gap-2">
                <Label htmlFor="email">Email</Label>
                <Input
                  id="email"
                  value={user.email}
                  readOnly
                  className="bg-muted cursor-default"
                />
              </div>

              <div className="flex flex-col gap-2">
                <Label htmlFor="provider">Provider</Label>
                <Input
                  id="provider"
                  value={user.provider}
                  readOnly
                  className="bg-muted cursor-default"
                />
              </div>

              <div className="flex flex-col gap-2">
                <Label htmlFor="role">Role</Label>
                <Input
                  id="role"
                  value={user.roles?.[0]?.name ?? "—"}
                  readOnly
                  className="bg-muted cursor-default"
                />
              </div>
            </div>

            {/* Error */}
            {error && <p className="text-sm text-destructive">{error}</p>}

            {/* Edit mode actions */}
            {isEditing && (
              <div className="flex justify-end gap-2">
                <Button variant="ghost" onClick={onCancel} disabled={isSaving}>
                  Cancel
                </Button>
                <Button onClick={onSave} disabled={isSaving}>
                  {isSaving ? "Saving…" : "Save"}
                </Button>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default UserProfile;
