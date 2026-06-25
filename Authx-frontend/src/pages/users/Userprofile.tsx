import React, { useEffect, useRef, useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import useAuthStore from "@/auth/store";
import type User from "@/models/User";
import { updateUser } from "@/services/Authservice";
import { Switch } from "@/components/ui/switch";
import { useNavigate } from "react-router";

const Userprofile: React.FC = () => {
  const user = useAuthStore((s) => s.user);
  const accessToken = useAuthStore((s) => s.accessToken);
  const authStatus = useAuthStore((s) => s.authStatus);
  const changeLocalLoginData = useAuthStore((s) => s.changeLocalLoginData);
  const navigate = useNavigate();

  const [form, setForm] = useState<Partial<User>>({});
  const [preview, setPreview] = useState<string | undefined>(undefined);
  const fileRef = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    if (user) {
      setForm(user);
      setPreview(user.image);
    }
  }, [user]);

  const onFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const url = URL.createObjectURL(file);
    setPreview(url);
    // store temporary url in form.image so it persists on save
    setForm((p) => ({ ...p, image: url }));
  };

  const onSave = async () => {
    console.log("TOKEN:", useAuthStore.getState().accessToken);
    if (!user) return; // ← guard first

    try {
      const file = fileRef.current?.files?.[0];

      if (file) {
        const fd = new FormData();
        fd.append("name", form.name ?? user.name ?? "");
        fd.append("enabled", String(form.enabled ?? user.enabled));
        fd.append("file", file);

        const serverUser = await updateUser(user.id, fd);
        changeLocalLoginData(accessToken ?? "", serverUser, true);
        setPreview(serverUser.image);
      } else {
        const serverUser = await updateUser(user.id, {
          name: form.name ?? user.name,
          enabled:
            typeof form.enabled === "boolean" ? form.enabled : user.enabled,
          image: form.image ?? user.image,
        });
        changeLocalLoginData(accessToken ?? "", serverUser, true);
        setPreview(serverUser.image);
      }

      navigate("/dashboard"); // ← navigate AFTER DB save succeeds
    } catch (err) {
      console.error("Failed to update user:", err);
      // fallback local update
      const updated: User = {
        ...user,
        name: form.name ?? user.name,
        image: form.image ?? user.image,
        enabled:
          typeof form.enabled === "boolean" ? form.enabled : user.enabled,
      };
      changeLocalLoginData(accessToken ?? "", updated, authStatus ?? false);
      // don't navigate — let user retry
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
      <div className="max-w-3xl mx-auto">
        <Card>
          <CardHeader>
            <CardTitle>Profile</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center gap-4">
              <Avatar className="w-20 h-20">
                {preview ? (
                  <AvatarImage src={preview} alt={form.name || user.name} />
                ) : (
                  <AvatarFallback>
                    {(user.name || user.email)[0].toUpperCase()}
                  </AvatarFallback>
                )}
              </Avatar>

              <div className="space-y-2">
                <div className="flex gap-2">
                  <Button onClick={() => fileRef.current?.click()} size="sm">
                    Change Photo
                  </Button>
                  <input
                    ref={fileRef}
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={onFileChange}
                  />
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => {
                      setPreview(undefined);
                      setForm((p) => ({ ...p, image: undefined }));
                    }}
                  >
                    Remove
                  </Button>
                </div>
                <div className="text-sm text-muted-foreground">
                  Recommended size: 256x256 — JPG, PNG, or GIF.
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <Label className="ml-2">Name</Label>
                <Input
                  value={form.name ?? ""}
                  onChange={(e) =>
                    setForm((p) => ({ ...p, name: e.target.value }))
                  }
                />
              </div>

              <div className="flex flex-col gap-2">
                <Label className="ml-2">Email</Label>
                <Input value={user.email} readOnly />
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="flex flex-col gap-2">
                <Label className="ml-2">Provider</Label>
                <Input value={user.provider} readOnly />
              </div>

              <div className="flex items-center justify-between rounded-lg border p-4">
                <div className="space-y-1">
                  <Label htmlFor="account-active">Account Active</Label>

                  <p className="text-sm text-muted-foreground">
                    Toggle account accessibility.
                  </p>
                </div>

                <Switch
                  id="account-active"
                  checked={!!form.enabled}
                  onCheckedChange={(checked) =>
                    setForm((p) => ({
                      ...p,
                      enabled: checked,
                    }))
                  }
                />
              </div>
            </div>

            <div className="flex justify-end gap-2">
              <Button
                variant="ghost"
                onClick={() => {
                  setForm(user);
                  setPreview(user.image);
                }}
              >
                Cancel
              </Button>
              <Button onClick={onSave}>Save</Button>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default Userprofile;
