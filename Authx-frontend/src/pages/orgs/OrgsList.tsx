import apiClient from "@/config/ApiCient";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Building2, Plus, Users, Crown, Shield, Eye } from "lucide-react";
import PendingInvites from "../PendingInvites";

type OrgDto = {
  id: string;
  name: string;
  slug: string;
  description: string;
  createdAt: string;
  memberCount: number;
  myRole: "OWNER" | "ADMIN" | "MEMBER" | "VIEWER";
};

const roleBadge: Record<string, string> = {
  OWNER: "bg-amber-100 text-amber-700",
  ADMIN: "bg-blue-100 text-blue-700",
  MEMBER: "bg-green-100 text-green-700",
  VIEWER: "bg-gray-100 text-gray-600",
};

const RoleIcon = ({ role }: { role: string }) => {
  if (role === "OWNER") return <Crown className="w-3 h-3" />;
  if (role === "ADMIN") return <Shield className="w-3 h-3" />;
  if (role === "VIEWER") return <Eye className="w-3 h-3" />;
  return <Users className="w-3 h-3" />;
};
const OrgsList = () => {
  const [orgs, setOrgs] = useState<OrgDto[]>([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    apiClient
      .get<OrgDto[]>("/orgs/mine")
      .then((res) => setOrgs(res.data))
      .finally(() => setLoading(false));
  }, []);
  if (loading) {
    return (
      <div className="flex justify-center py-16">
        <div className="animate-spin rounded-full h-8 w-8 border-2 border-muted border-t-primary" />
      </div>
    );
  }
  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <PendingInvites />
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold">Organizations</h1>
          <p className="text-sm text-muted-foreground mt-1">
            Manage your teams and workspaces
          </p>
        </div>
        <Button onClick={() => navigate("/orgs/create")} className="gap-2">
          <Plus className="w-4 h-4" />
          New Organization
        </Button>
      </div>

      {/* Empty state */}
      {orgs.length === 0 ? (
        <Card className="rounded-2xl">
          <CardContent className="py-16 text-center">
            <Building2 className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
            <h2 className="text-lg font-semibold mb-2">No organizations yet</h2>
            <p className="text-sm text-muted-foreground mb-6">
              Create your first organization to start collaborating with your
              team.
            </p>
            <Button onClick={() => navigate("/orgs/create")}>
              Create Organization
            </Button>
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {orgs.map((org) => (
            <Card
              key={org.id}
              className="rounded-2xl hover:shadow-md transition-shadow cursor-pointer"
              onClick={() => navigate(`/orgs/${org.id}`)}
            >
              <CardContent className="p-6">
                <div className="flex items-start justify-between mb-4">
                  <div className="w-10 h-10 rounded-xl bg-primary/10 flex items-center justify-center">
                    <Building2 className="w-5 h-5 text-primary" />
                  </div>

                  {/* Role badge */}
                  <span
                    className={`
                    inline-flex items-center gap-1 text-xs
                    font-medium px-2.5 py-1 rounded-full
                    ${roleBadge[org.myRole]}
                  `}
                  >
                    <RoleIcon role={org.myRole} />
                    {org.myRole}
                  </span>
                </div>

                <h3 className="font-semibold text-base mb-1">{org.name}</h3>

                {org.description && (
                  <p className="text-sm text-muted-foreground mb-3 line-clamp-2">
                    {org.description}
                  </p>
                )}

                <div className="flex items-center justify-between pt-3 border-t border-border">
                  <span className="flex items-center gap-1.5 text-xs text-muted-foreground">
                    <Users className="w-3.5 h-3.5" />
                    {org.memberCount} member{org.memberCount !== 1 ? "s" : ""}
                  </span>
                  <span className="text-xs text-muted-foreground font-mono">
                    {org.slug}
                  </span>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default OrgsList;
