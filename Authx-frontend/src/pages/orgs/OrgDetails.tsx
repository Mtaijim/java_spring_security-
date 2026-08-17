import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
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
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import {
  Crown,
  Shield,
  Users,
  Eye,
  UserPlus,
  Trash2,
  ArrowLeft,
} from "lucide-react";
import apiClient from "@/config/ApiCient";
import toast from "react-hot-toast";

type OrgRole = "OWNER" | "ADMIN" | "MEMBER" | "VIEWER";

type Member = {
  userId: string;
  name: string;
  email: string;
  image?: string;
  role: OrgRole;
  joinedAt: string;
};

type OrgDto = {
  id: string;
  name: string;
  slug: string;
  description: string;
  memberCount: number;
  myRole: OrgRole;
};

const roleBadge: Record<OrgRole, string> = {
  OWNER: "bg-amber-100 text-amber-700",
  ADMIN: "bg-blue-100 text-blue-700",
  MEMBER: "bg-green-100 text-green-700",
  VIEWER: "bg-gray-100 text-gray-600",
};

const RoleIcon = ({ role }: { role: OrgRole }) => {
  if (role === "OWNER") return <Crown className="w-3 h-3" />;
  if (role === "ADMIN") return <Shield className="w-3 h-3" />;
  if (role === "VIEWER") return <Eye className="w-3 h-3" />;
  return <Users className="w-3 h-3" />;
};

const OrgDetails = () => {
  const { orgId } = useParams();
  const navigate = useNavigate();
  const [org, setOrg] = useState<OrgDto | null>(null);
  const [members, setMembers] = useState<Member[]>([]);
  const [loading, setLoading] = useState(true);

  const [inviteEmail, setInviteEmail] = useState("");
  const [inviteRole, setInviteRole] = useState<OrgRole>("MEMBER");
  const [inviteOpen, setInviteOpen] = useState(false);
  const [inviting, setInviting] = useState(false);

  const fetchData = async () => {
    try {
      const [orgRes, membersRes] = await Promise.all([
        apiClient.get<OrgDto>(`/orgs/${orgId}`),
        apiClient.get<Member[]>(`/orgs/${orgId}/members`),
      ]);
      setOrg(orgRes.data);
      setMembers(membersRes.data);
    } catch {
      toast.error("Failed to load Organisation ");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [orgId]);

  const myRole = org?.myRole;
  const isOwner = myRole === "OWNER";
  const canManage = myRole === "OWNER" || myRole === "ADMIN";

  const inviteMember = async () => {
    if (!inviteEmail.trim()) {
      toast.error("Email is Required");
      return;
    }
    setInviting(true);

    try {
      await apiClient.post(`/orgs/${orgId}/invite`, {
        email: inviteEmail,
        role: inviteRole,
      });
      toast.success(`${inviteEmail} invited as ${inviteRole}`);
      setInviteEmail("");
      setInviteOpen(false);
      fetchData();
    } catch (err: any) {
      toast.error(err.response?.data?.message || "Failed to invite");
    } finally {
      setInviting(false);
    }
  };

  const changeRole = async (userId: string, role: OrgRole) => {
    try {
      await apiClient.put(`/orgs/${orgId}/members/${userId}`, { role });
      toast.success("Role updated");
      fetchData();
    } catch (err: any) {
      toast.error(err.response?.data?.message || "Failed to update role");
    }
  };

  const removeMember = async (userId: string) => {
    try {
      await apiClient.delete(`/orgs/${orgId}/members/${userId}`);
      toast.success("Member remove successfully");

      fetchData();
    } catch (err: any) {
      toast.error(err.response?.data?.message || "failed to remove");
    }
  };

  const deleteOrg = async () => {
    try {
      await apiClient.delete(`/orgs/${orgId}`);
      toast.success("Organization deleted");
      navigate("/orgs");
    } catch (err: any) {
      toast.error(err.response?.data?.message || "Failed to delete");
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center py-16">
        <div className="animate-spin rounded-full h-8 w-8 border-2 border-muted border-t-primary" />
      </div>
    );
  }
  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      {/* Back button */}
      <button
        onClick={() => navigate("/orgs")}
        className="flex items-center gap-1.5 text-sm text-muted-foreground hover:text-foreground mb-6 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        Back to organizations
      </button>

      {/* Org header */}
      <div className="flex items-start justify-between mb-6 flex-wrap gap-4">
        <div>
          <h1 className="text-2xl font-bold">{org?.name ?? "Organization"}</h1>
          {org?.description && (
            <p className="text-sm text-muted-foreground mt-1">
              {org.description}
            </p>
          )}
          <p className="text-xs text-muted-foreground font-mono mt-1">
            {org?.slug}
          </p>
        </div>

        <div className="flex items-center gap-2">
          {/* Invite member button */}
          {canManage && (
            <Dialog open={inviteOpen} onOpenChange={setInviteOpen}>
              <DialogTrigger asChild>
                <Button size="sm" className="gap-2">
                  <UserPlus className="w-4 h-4" />
                  Invite
                </Button>
              </DialogTrigger>
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>Invite Member</DialogTitle>
                </DialogHeader>
                <div className="space-y-4 mt-2">
                  <div className="space-y-1.5">
                    <label className="text-sm font-medium">Email address</label>
                    <Input
                      placeholder="member@example.com"
                      type="email"
                      value={inviteEmail}
                      onChange={(e) => setInviteEmail(e.target.value)}
                    />
                  </div>

                  <div className="space-y-1.5">
                    <label className="text-sm font-medium">Role</label>
                    <Select
                      value={inviteRole}
                      onValueChange={(v) => setInviteRole(v as OrgRole)}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="ADMIN">
                          Admin — manage members
                        </SelectItem>
                        <SelectItem value="MEMBER">
                          Member — normal access
                        </SelectItem>
                        <SelectItem value="VIEWER">
                          Viewer — read only
                        </SelectItem>
                      </SelectContent>
                    </Select>
                  </div>

                  <Button
                    className="w-full"
                    onClick={inviteMember}
                    disabled={inviting}
                  >
                    {inviting ? "Inviting..." : "Send Invite"}
                  </Button>
                </div>
              </DialogContent>
            </Dialog>
          )}

          {/* Delete org — OWNER only */}
          {isOwner && (
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button
                  size="sm"
                  variant="outline"
                  className="gap-2 text-red-600 hover:text-red-700 border-red-200"
                >
                  <Trash2 className="w-4 h-4" />
                  Delete
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Delete organization?</AlertDialogTitle>
                  <AlertDialogDescription>
                    This will permanently delete the organization and remove all
                    members. This cannot be undone.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancel</AlertDialogCancel>
                  <AlertDialogAction
                    onClick={deleteOrg}
                    className="bg-red-600 hover:bg-red-700"
                  >
                    Delete
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          )}
        </div>
      </div>

      {/* Members list */}
      <Card className="rounded-2xl">
        <CardHeader className="pb-3">
          <div className="flex items-center justify-between">
            <CardTitle className="text-base">Members</CardTitle>
            <Badge variant="secondary">
              {members.length} member{members.length !== 1 ? "s" : ""}
            </Badge>
          </div>
        </CardHeader>

        <CardContent className="space-y-2">
          {members.map((member) => (
            <div
              key={member.userId}
              className="flex items-center justify-between p-3 rounded-xl hover:bg-muted/30 transition-colors"
            >
              {/* Avatar + info */}
              <div className="flex items-center gap-3">
                <Avatar className="w-9 h-9">
                  <AvatarImage src={member.image} />
                  <AvatarFallback className="text-sm">
                    {(member.name || member.email)[0].toUpperCase()}
                  </AvatarFallback>
                </Avatar>
                <div>
                  <p className="text-sm font-medium">{member.name || "—"}</p>
                  <p className="text-xs text-muted-foreground">
                    {member.email}
                  </p>
                </div>
              </div>

              {/* Role + actions */}
              <div className="flex items-center gap-2">
                {/* Role badge or select */}
                {canManage && member.role !== "OWNER" ? (
                  <Select
                    value={member.role}
                    onValueChange={(v) =>
                      changeRole(member.userId, v as OrgRole)
                    }
                  >
                    <SelectTrigger className="h-7 text-xs w-28">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="ADMIN">Admin</SelectItem>
                      <SelectItem value="MEMBER">Member</SelectItem>
                      <SelectItem value="VIEWER">Viewer</SelectItem>
                    </SelectContent>
                  </Select>
                ) : (
                  <span
                    className={`
                    inline-flex items-center gap-1 text-xs
                    font-medium px-2.5 py-1 rounded-full
                    ${roleBadge[member.role]}
                  `}
                  >
                    <RoleIcon role={member.role} />
                    {member.role}
                  </span>
                )}

                {/* Remove button */}
                {canManage && member.role !== "OWNER" && (
                  <AlertDialog>
                    <AlertDialogTrigger asChild>
                      <Button
                        size="icon"
                        variant="ghost"
                        className="h-7 w-7 text-muted-foreground hover:text-red-500"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Remove member?</AlertDialogTitle>
                        <AlertDialogDescription>
                          Remove {member.name || member.email} from this
                          organization?
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <AlertDialogAction
                          onClick={() => removeMember(member.userId)}
                          className="bg-red-600 hover:bg-red-700"
                        >
                          Remove
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                )}
              </div>
            </div>
          ))}
        </CardContent>
      </Card>
    </div>
  );
};
export default OrgDetails;
