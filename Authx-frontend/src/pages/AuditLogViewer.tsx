import apiClient from "@/config/ApiCient";
import { useEffect, useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Shield, Search, ChevronLeft, ChevronRight } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

type AuditLog = {
  id: string;
  userId: string;
  email: string;
  action: string;
  resourceType: string;
  resourceId: string | null;
  status: "SUCCESS" | "FAILURE";
  description: string;
  ipAddress: string;
  userAgent: string;
  createdAt: string;
  OrgId: string | null;
};

type PageResponse = {
  content: AuditLog[];
  totalPages: number;
  totalElements: number;
  number: number;
};

const actionColors: Record<string, string> = {
  LOGIN: "bg-green-100 text-green-700",
  LOGIN_FAILED: "bg-red-100 text-red-700",
  LOGOUT: "bg-gray-100 text-gray-600",
  REGISTER: "bg-blue-100 text-blue-700",
  PASSWORD_RESET: "bg-yellow-100 text-yellow-700",
  PASSWORD_CHANGED: "bg-orange-100 text-orange-700",
  MFA_ENABLED: "bg-purple-100 text-purple-700",
  MFA_DISABLED: "bg-pink-100 text-pink-700",
  ROLE_ASSIGNED: "bg-indigo-100 text-indigo-700",
  PERMISSION_ASSIGNED: "bg-cyan-100 text-cyan-700",
  ORG_CREATED: "bg-teal-100 text-teal-700",
  MEMBER_INVITED: "bg-lime-100 text-lime-700",
};

const AuditLogViewer = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [search, setSearch] = useState("");
  const [actionFilter, setActionFilter] = useState("ALL");

  const PAGE_SIZE = 15;

  const fetchLogs = async (currentPage = 0) => {
    setLoading(true);

    try {
      const response = await apiClient.get<PageResponse>("/admin/audit", {
        params: {
          page: currentPage,
          size: PAGE_SIZE,
        },
      });

      setLogs(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setPage(response.data.number);
    } catch {
    } finally {
      setLoading(true);
    }
  };

  useEffect(() => {
    fetchLogs(0);
  }, []);

  const filtered = logs.filter((log) => {
    const matchesSearch =
      search === "" ||
      log.email?.toLowerCase().includes(search.toLowerCase()) ||
      log.description?.toLowerCase().includes(search.toLowerCase());

    const matchesAction = actionFilter === "ALL" || log.action === actionFilter;

    return matchesAction && matchesSearch;
  });

  const formatTime = (dateStr: string) => {
    const date = new Date(dateStr);
    return date.toLocaleString("en-US", {
      day: "numeric",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const uniqueActions = [
    "ALL",
    ...Array.from(new Set(logs.map((l) => l.action))),
  ];

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <Card className="rounded-2xl">
        <CardHeader>
          <div className="flex items-center justify-between flex-wrap gap-4">
            <div className="flex items-center gap-2">
              <Shield className="w-5 h-5 text-primary" />
              <CardTitle>Audit Logs</CardTitle>
              <Badge variant="secondary" className="ml-2">
                {totalElements} total
              </Badge>
            </div>

            {/* Filters */}
            <div className="flex items-center gap-3 flex-wrap">
              {/* Search */}
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input
                  placeholder="Search email or description..."
                  className="pl-9 w-64"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>

              {/* Action filter */}
              <Select value={actionFilter} onValueChange={setActionFilter}>
                <SelectTrigger className="w-48">
                  <SelectValue placeholder="Filter by action" />
                </SelectTrigger>
                <SelectContent>
                  {uniqueActions.map((action) => (
                    <SelectItem key={action} value={action}>
                      {action}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardHeader>

        <CardContent>
          {loading ? (
            <div className="flex justify-center py-16">
              <div className="animate-spin rounded-full h-8 w-8 border-2 border-muted border-t-primary" />
            </div>
          ) : filtered.length === 0 ? (
            <div className="text-center py-16 text-muted-foreground">
              No audit logs found
            </div>
          ) : (
            <>
              {/* Table */}
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left">
                      <th className="pb-3 font-medium text-muted-foreground">
                        Action
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        User
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        Description
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        IP
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        Status
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        Time
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-border">
                    {filtered.map((log) => (
                      <tr
                        key={log.id}
                        className="hover:bg-muted/30 transition-colors"
                      >
                        {/* Action badge */}
                        <td className="py-3 pr-4">
                          <span
                            className={`
                            text-xs font-medium px-2.5 py-1
                            rounded-full whitespace-nowrap
                            ${
                              actionColors[log.action] ||
                              "bg-muted text-muted-foreground"
                            }
                          `}
                          >
                            {log.action}
                          </span>
                        </td>

                        {/* Email */}
                        <td className="py-3 pr-4">
                          <p className="font-medium truncate max-w-[160px]">
                            {log.email || "—"}
                          </p>
                        </td>

                        {/* Description */}
                        <td className="py-3 pr-4">
                          <p className="text-muted-foreground truncate max-w-[200px]">
                            {log.description || "—"}
                          </p>
                        </td>

                        {/* IP */}
                        <td className="py-3 pr-4">
                          <p className="font-mono text-xs text-muted-foreground">
                            {log.ipAddress || "—"}
                          </p>
                        </td>

                        {/* Status */}
                        <td className="py-3 pr-4">
                          <Badge
                            variant={
                              log.status === "SUCCESS"
                                ? "secondary"
                                : "destructive"
                            }
                            className="text-xs"
                          >
                            {log.status}
                          </Badge>
                        </td>

                        {/* Time */}
                        <td className="py-3 text-xs text-muted-foreground whitespace-nowrap">
                          {formatTime(log.createdAt)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Pagination */}
              <div className="flex items-center justify-between mt-4 pt-4 border-t border-border">
                <p className="text-sm text-muted-foreground">
                  Page {page + 1} of {totalPages}
                </p>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={page === 0}
                    onClick={() => fetchLogs(page - 1)}
                  >
                    <ChevronLeft className="w-4 h-4" />
                    Previous
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={page >= totalPages - 1}
                    onClick={() => fetchLogs(page + 1)}
                  >
                    Next
                    <ChevronRight className="w-4 h-4" />
                  </Button>
                </div>
              </div>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default AuditLogViewer;
