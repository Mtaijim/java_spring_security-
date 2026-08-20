import apiClient from "@/config/ApiCient";
import { useEffect } from "react";
import { toast } from "react-hot-toast";
import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Building2, Check, X } from "lucide-react";

type OrgRole = "OWNER" | "ADMIN" | "MEMBER" | "VIEWER";

type PendingInvite = {
  OrgMembershipId: string;
  orgId: string;
  orgName: string;
  orgSlug: string;
  role: OrgRole;
  invitedByName: string | null;
  invitedByEmail: string | null;
  invitedAt: string;
};

const roleBadge: Record<OrgRole, string> = {
  OWNER: "bg-amber-100 text-amber-700",
  ADMIN: "bg-blue-100 text-blue-700",
  MEMBER: "bg-green-100 text-green-700",
  VIEWER: "bg-gray-100 text-gray-600",
};

const formatDate = (dateStr: string) =>
  new Date(dateStr).toLocaleDateString("en-US", {
    day: "numeric",
    month: "short",
    year: "numeric",
  });

const PendingInvites = () => {
  const [invites, setInvites] = useState<PendingInvite[]>([]);
  const [loading, setLoading] = useState(true);
  const [actingOn, setActingOn] = useState<string | null>(null);

  const fetchInvites = async () => {
    try {
      const response = await apiClient.get<PendingInvite[]>(
        "/orgs/invites/pending",
      );
      console.log("RAW pending invites response:", response.data);
      setInvites(response.data);
    } catch (error) {
      console.error("Error fetching pending invites:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInvites();
  }, []);
  const respond = async (
    invite: PendingInvite,
    action: "accept" | "decline",
  ) => {
    setActingOn(invite.OrgMembershipId);
    try {
      await apiClient.post(
        `/orgs/${invite.orgId}/invites/${invite.OrgMembershipId}/${action}`,
      );
      toast.success(
        action === "accept" ? "Invite accepted!" : "Invite declined!",
      );
      setInvites((prev) =>
        prev.filter((i) => i.OrgMembershipId !== invite.OrgMembershipId),
      );
    } catch (error) {
      toast.error("Error occurred while responding to invite.");
    } finally {
      setActingOn(null);
    }
  };
  if (loading) {
    return (
      <div className="flex justify-center py-16">
        <div className="animate-spin rounded-full h-8 w-8 border-2 border-muted border-t-primary" />
      </div>
    );
  }

  if (invites.length === 0) {
    return null;
  }
  return (
    <Card className="rounded-2xl">
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="text-base">Pending Invites</CardTitle>
          <Badge variant="secondary">{invites.length}</Badge>
        </div>
      </CardHeader>

      <CardContent className="space-y-2">
        {invites.map((invite) => (
          <div
            key={invite.OrgMembershipId}
            className="flex items-center justify-between p-3 rounded-xl border border-border"
          >
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 rounded-full bg-muted flex items-center justify-center">
                <Building2 className="w-4 h-4 text-muted-foreground" />
              </div>
              <div>
                <p className="text-sm font-medium">{invite.orgName}</p>
                <p className="text-xs text-muted-foreground">
                  Invited by{" "}
                  {invite.invitedByName || invite.invitedByEmail || "—"}
                  {" · "}
                  {formatDate(invite.invitedAt)}
                </p>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <span
                className={`text-xs font-medium px-2.5 py-1 rounded-full ${roleBadge[invite.role]}`}
              >
                {invite.role}
              </span>

              <Button
                size="icon"
                variant="ghost"
                className="h-7 w-7 text-muted-foreground hover:text-red-500"
                disabled={actingOn === invite.OrgMembershipId}
                onClick={() => respond(invite, "decline")}
              >
                <X className="w-3.5 h-3.5" />
              </Button>

              <Button
                size="icon"
                className="h-7 w-7"
                disabled={actingOn === invite.OrgMembershipId}
                onClick={() => respond(invite, "accept")}
              >
                <Check className="w-3.5 h-3.5" />
              </Button>
            </div>
          </div>
        ))}
      </CardContent>
    </Card>
  );
};
export default PendingInvites;
