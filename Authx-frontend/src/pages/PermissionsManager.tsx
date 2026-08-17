import apiClient from "@/config/ApiCient";
import { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Shield, Search, Plus, X, ChevronDown, ChevronUp } from "lucide-react";

type Permission = {
  id: string;
  name: string;
  description: string;
  category: string;
};

type User = {
  id: string;
  email: string;
  name: string;
  image?: string;
  permissions: Permission[];
  roles: { id: string; name: string }[];
};

const groupByCategory = (permissions: Permission[]) => {
  return permissions.reduce(
    (acc, permission) => {
      if (!acc[permission.category]) {
        acc[permission.category] = [];
      }
      acc[permission.category].push(permission);
      return acc;
    },
    {} as Record<string, Permission[]>,
  );
};

const categoryColors: Record<string, string> = {
  users: "bg-blue-50 text-blue-700 border-blue-200",
  reports: "bg-green-50 text-green-700 border-green-200",
  billing: "bg-amber-50 text-amber-700 border-amber-200",
  audit: "bg-purple-50 text-purple-700 border-purple-200",
  settings: "bg-gray-50 text-gray-700 border-gray-200",
};

const PermissionsManager = () => {
  const [allPermissions, setAllPermissions] = useState<Permission[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [expandedUser, setExpandedUser] = useState<string | null>(null);

  const [createOpen, setCreateOpen] = useState(false);
  const [newpermission, setNewPermission] = useState({
    name: "",
    description: "",
    category: "",
  });

  useEffect(() => {
    Promise.all([apiClient.get("/permissions"), apiClient.get("/users")])
      .then(([permissionsResponse, usersResponse]) => {
        setAllPermissions(permissionsResponse.data);
        setUsers(usersResponse.data);
      })
      .finally(() => setLoading(false));
  }, []);

  // assign permissions to user
  const assignPermission = async (userId: string, permissionName: string) => {
    try {
      await apiClient.post(`/permissions/users/${userId}`, {
        permission: permissionName,
      });
      toast.success("Permission assigned successfully");
      refreshUsers(userId);
    } catch (error) {
      toast.error("Failed to assign permission");
    }
  };

  const removePermission = async (userId: string, permissionName: string) => {
    try {
      await apiClient.delete(`/permissions/users/${userId}`, {
        data: { permission: permissionName },
      });
      toast.success("Permission removed successfully");
      refreshUsers(userId);
    } catch {
      toast.error("Failed to remove permission");
    }
  };

  const refreshUsers = async (userId: string) => {
    try {
      const response = await apiClient.get<User>(`/users/${userId}`);
      setUsers((prevUsers) =>
        prevUsers.map((user) => (user.id === userId ? response.data : user)),
      );

      if (selectedUser && selectedUser.id === userId) {
        setSelectedUser(response.data);
      }
    } catch (error) {
      toast.error("Failed to refresh user data");
    }
  };

  const createPermission = async () => {
    if (
      !newpermission.name ||
      !newpermission.description ||
      !newpermission.category
    ) {
      toast.error("Please fill all fields");
      return;
    }
    try {
      const response = await apiClient.post<Permission>(
        "/permissions",
        newpermission,
      );
      setAllPermissions((prev) => [...prev, response.data]);
      setNewPermission({ name: "", description: "", category: "" });
      setCreateOpen(false);
      toast.success("Permission created successfully");
    } catch {
      toast.error("Failed to create permission");
    }
  };

  const filteredUsers = users.filter(
    (user) =>
      user.email.toLowerCase().includes(search.toLowerCase()) ||
      user.name.toLowerCase().includes(search.toLowerCase()),
  );

  const groupedPermissions = groupByCategory(allPermissions);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-10 w-10 border-2 border-muted border-t-primary" />
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-6">
      {/* ── Header ── */}
      <div className="flex items-center justify-between flex-wrap gap-4">
        <div className="flex items-center gap-2">
          <Shield className="w-5 h-5 text-primary" />
          <h1 className="text-2xl font-bold">Permissions</h1>
        </div>

        {/* Create permission dialog */}
        <Dialog open={createOpen} onOpenChange={setCreateOpen}>
          <DialogTrigger asChild>
            <Button size="sm" className="gap-2">
              <Plus className="w-4 h-4" />
              New Permission
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Create Permission</DialogTitle>
            </DialogHeader>
            <div className="space-y-4 mt-2">
              <div className="space-y-1.5">
                <label className="text-sm font-medium">Name</label>
                <Input
                  placeholder="e.g. reports_export"
                  value={newpermission.name}
                  onChange={(e) =>
                    setNewPermission((p) => ({
                      ...p,
                      name: e.target.value.toLowerCase().replace(/\s+/g, "_"),
                    }))
                  }
                />
                <p className="text-xs text-muted-foreground">
                  Use format: resource_action
                </p>
              </div>

              <div className="space-y-1.5">
                <label className="text-sm font-medium">Description</label>
                <Input
                  placeholder="e.g. Export reports to CSV"
                  value={newpermission.description}
                  onChange={(e) =>
                    setNewPermission((p) => ({
                      ...p,
                      description: e.target.value,
                    }))
                  }
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-sm font-medium">Category</label>
                <Select
                  value={newpermission.category}
                  onValueChange={(v) =>
                    setNewPermission((p) => ({ ...p, category: v }))
                  }
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select category" />
                  </SelectTrigger>
                  <SelectContent>
                    {["users", "reports", "billing", "audit", "settings"].map(
                      (c) => (
                        <SelectItem key={c} value={c}>
                          {c}
                        </SelectItem>
                      ),
                    )}
                  </SelectContent>
                </Select>
              </div>

              <Button className="w-full" onClick={createPermission}>
                Create Permission
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* ── Left: All permissions by category ── */}
        <Card className="rounded-2xl lg:col-span-1">
          <CardHeader className="pb-3">
            <CardTitle className="text-base">All Permissions</CardTitle>
            <p className="text-xs text-muted-foreground">
              {allPermissions.length} permissions across{" "}
              {Object.keys(groupedPermissions).length} categories
            </p>
          </CardHeader>
          <CardContent className="space-y-4">
            {Object.entries(groupedPermissions).map(([category, perms]) => (
              <div key={category}>
                <p className="text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-2">
                  {category}
                </p>
                <div className="space-y-1.5">
                  {perms.map((p) => (
                    <div
                      key={p.id}
                      className={`
                        text-xs px-3 py-2 rounded-lg border
                        flex items-center justify-between
                        ${categoryColors[category] || "bg-muted border-border"}
                      `}
                    >
                      <span className="font-mono font-medium">{p.name}</span>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </CardContent>
        </Card>

        {/* ── Right: Users + their permissions ── */}
        <Card className="rounded-2xl lg:col-span-2">
          <CardHeader className="pb-3">
            <CardTitle className="text-base">User Permissions</CardTitle>
            {/* Search */}
            <div className="relative mt-2">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Search users..."
                className="pl-9"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
              />
            </div>
          </CardHeader>

          <CardContent className="space-y-3">
            {filteredUsers.map((user) => {
              const isExpanded = expandedUser === user.id;
              const userPermNames = new Set(
                user.permissions?.map((p) => p.name) ?? [],
              );

              return (
                <div
                  key={user.id}
                  className="border border-border rounded-xl overflow-hidden"
                >
                  {/* User row */}
                  <div
                    className="flex items-center justify-between p-4 cursor-pointer hover:bg-muted/30 transition-colors"
                    onClick={() => setExpandedUser(isExpanded ? null : user.id)}
                  >
                    <div className="flex items-center gap-3">
                      <Avatar className="w-8 h-8">
                        <AvatarImage src={user.image} />
                        <AvatarFallback className="text-xs">
                          {(user.name || user.email)[0].toUpperCase()}
                        </AvatarFallback>
                      </Avatar>
                      <div>
                        <p className="text-sm font-medium">
                          {user.name || "—"}
                        </p>
                        <p className="text-xs text-muted-foreground">
                          {user.email}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <Badge variant="secondary" className="text-xs">
                        {user.permissions?.length ?? 0} permissions
                      </Badge>
                      {isExpanded ? (
                        <ChevronUp className="w-4 h-4 text-muted-foreground" />
                      ) : (
                        <ChevronDown className="w-4 h-4 text-muted-foreground" />
                      )}
                    </div>
                  </div>

                  {/* Expanded permissions panel */}
                  {isExpanded && (
                    <div className="border-t border-border p-4 bg-muted/20 space-y-4">
                      {/* Current permissions */}
                      {user.permissions?.length > 0 && (
                        <div>
                          <p className="text-xs font-medium text-muted-foreground mb-2">
                            Current permissions
                          </p>
                          <div className="flex flex-wrap gap-2">
                            {user.permissions.map((p) => (
                              <span
                                key={p.id}
                                className="inline-flex items-center gap-1.5 text-xs font-mono bg-primary/10 text-primary px-2.5 py-1 rounded-full"
                              >
                                {p.name}
                                <button
                                  onClick={() =>
                                    removePermission(user.id, p.name)
                                  }
                                  className="hover:text-red-500 transition-colors"
                                >
                                  <X className="w-3 h-3" />
                                </button>
                              </span>
                            ))}
                          </div>
                        </div>
                      )}

                      {/* Assign permission */}
                      <div>
                        <p className="text-xs font-medium text-muted-foreground mb-2">
                          Assign permission
                        </p>
                        <div className="flex gap-2">
                          <Select
                            onValueChange={(perm) =>
                              assignPermission(user.id, perm)
                            }
                          >
                            <SelectTrigger className="flex-1 h-8 text-xs">
                              <SelectValue placeholder="Select permission to add" />
                            </SelectTrigger>
                            <SelectContent>
                              {allPermissions
                                // only show unassigned permissions
                                .filter((p) => !userPermNames.has(p.name))
                                .map((p) => (
                                  <SelectItem
                                    key={p.id}
                                    value={p.name}
                                    className="text-xs font-mono"
                                  >
                                    {p.name}
                                  </SelectItem>
                                ))}
                            </SelectContent>
                          </Select>
                        </div>
                      </div>
                    </div>
                  )}
                </div>
              );
            })}

            {filteredUsers.length === 0 && (
              <div className="text-center py-8 text-muted-foreground text-sm">
                No users found
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
export default PermissionsManager;
