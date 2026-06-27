import { useEffect, useState } from "react";
import type User from "@/models/User";
import apiClient from "@/config/ApiCient";

const AdminUserList = () => {
  const [users, SetUsers] = useState<User[]>([]);
  const [loading, SetLoading] = useState(true);

  useEffect(() => {
    apiClient
      .get("/users")
      .then((r) => SetUsers(r.data))
      .catch(console.error)
      .finally(() => SetLoading(false));
  }, []);

  const handleDelete = async (id: String) => {
    if (!confirm("Delete this user?")) return;
    await apiClient.delete(`/users/${id}`);
    SetUsers((prev) => prev.filter((u) => u.id !== id));
  };
  if (loading) return <p>Loading…</p>;

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-semibold mb-4">All users</h1>
      <table className="w-full text-sm border-collapse">
        <thead>
          <tr className="border-b text-left text-muted-foreground">
            <th className="pb-2">Email</th>
            <th className="pb-2">Name</th>
            <th className="pb-2">Role</th>
            <th className="pb-2">Active</th>
            <th className="pb-2"></th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id} className="border-b hover:bg-muted/30">
              <td className="py-2">{u.email}</td>
              <td className="py-2">{u.name ?? "—"}</td>
              <td className="py-2">
                {u.roles?.map((r) => r.name).join(", ") ?? "—"}
              </td>
              <td className="py-2">{u.enabled ? "✓" : "—"}</td>
              <td className="py-2">
                <button
                  onClick={() => handleDelete(u.id)}
                  className="text-destructive hover:underline text-xs"
                >
                  Delete
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminUserList;
