import { useEffect, useState } from "react";
import type User from "@/models/User";
import apiClient from "@/config/ApiCient";
import useAuthStore from "@/auth/store";
import toast from "react-hot-toast";
import { assignRole } from "@/services/Authservice";

const AdminUserList = () => {
  const [users, SetUsers] = useState<User[]>([]);
  const [loading, SetLoading] = useState(true);
  const currentUser = useAuthStore((s) => s.user);

  useEffect(() => {
    apiClient
      .get("/users")
      .then((r) => SetUsers(r.data))
      .catch(console.error)
      .finally(() => SetLoading(false));
  }, []);

  const handleDelete = async (id: String) => {
    if (!confirm("Delete this user?")) return;
    try {
      await apiClient.delete(`/users/${id}`);
      SetUsers((prev) => prev.filter((u) => u.id !== id));
      toast.success("User deleted");
    } catch {
      toast.error("You don't have permission or something went wrong");
    }
  };

  const handleRoleToggle = async (user: User) => {
    const currentRole = user.roles?.[0]?.name;
    const NewRole = currentRole === "ROLE_ADMIN" ? "ROLE_USER" : "ROLE_ADMIN";

    if (user.id === currentUser?.id && NewRole === "ROLE_USER") {
      toast.error("You can't demote Your self ");
      return;
    }
    try {
      const updated = await assignRole(user.id, NewRole);
      SetUsers((prev) => prev.map((u) => (u.id === user.id ? updated : u)));
      toast.success(`${user.email} is now ${NewRole}`);
    } catch {
      toast.error("Failed to update role");
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-10 w-10 border-2 border-muted border-t-primary" />
      </div>
    );
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-semibold mb-4">All users</h1>
      <table className="w-full text-sm border-collapse">
        <thead>
          <tr className="border-b text-left text-muted-foreground">
            <th className="px-4 py-3">Email</th>
            <th className="px-4 py-3">Name</th>
            <th className="px-4 py-3">Role</th>
            <th className="px-4 py-3">Enabled</th> {/* ← add this */}
            <th className="px-4 py-3">Actions</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u, i) => {
            const isCurrentAdmin = u.roles?.[0]?.name === "ROLE_ADMIN";
            const isSelf = u.id === currentUser?.id;
            return (
              <tr
                key={u.id}
                className={`border-t ${i % 2 === 0 ? "bg-background" : "bg-muted/30"}`}
              >
                <td className="px-4 py-3">{u.email}</td>
                <td className="px-4 py-3">{u.name ?? "—"}</td>
                <td className="px-4 py-3">
                  <span
                    className={`text-xs px-2 py-1 rounded-full font-medium ${
                      isCurrentAdmin
                        ? "bg-primary/10 text-primary"
                        : "bg-muted text-muted-foreground"
                    }`}
                  >
                    {u.roles?.[0]?.name ?? "—"}
                  </span>
                </td>
                <td className="px-4 py-3">{u.enable ? "✓" : "—"}</td>
                <td className="px-4 py-3 flex gap-2">
                  <button
                    onClick={() => handleRoleToggle(u)}
                    className="text-xs px-3 py-1 rounded border hover:bg-muted transition-colors"
                  >
                    {isCurrentAdmin ? "Make user" : "Make admin"}
                  </button>
                  {!isSelf && (
                    <button
                      onClick={() => handleDelete(u.id)}
                      className="text-xs px-3 py-1 rounded border border-destructive text-destructive hover:bg-destructive/10 transition-colors"
                    >
                      Delete
                    </button>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default AdminUserList;
