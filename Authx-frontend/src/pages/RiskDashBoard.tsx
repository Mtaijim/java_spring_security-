import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
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
import {
  AlertTriangle,
  Shield,
  ShieldAlert,
  ShieldCheck,
  Search,
  ChevronLeft,
  ChevronRight,
  Monitor,
  Smartphone,
} from "lucide-react";
import apiClient from "@/config/ApiCient";
import { useEffect, useState } from "react";

type RiskScore = {
  id: string;
  userId: string;
  email: string;
  level: "LOW" | "MEDIUM" | "HIGH";
  score: number;
  ipAddress: string;
  userAgent: string;
  reasons: string; // JSON string of risk reasons
  createdAt: string;
};

type PageResponse = {
  content: RiskScore[];
  totalPages: number;
  totalElements: number;
  number: number;
};

// risk level styles
const riskStyles = {
  LOW: {
    badge: "bg-green-100 text-green-700",
    icon: <ShieldCheck className="w-4 h-4 text-green-600" />,
    row: "",
  },
  MEDIUM: {
    badge: "bg-amber-100 text-amber-700",
    icon: <Shield className="w-4 h-4 text-amber-500" />,
    row: "bg-amber-50/30 dark:bg-amber-950/10",
  },
  HIGH: {
    badge: "bg-red-100 text-red-700",
    icon: <ShieldAlert className="w-4 h-4 text-red-500" />,
    row: "bg-red-50/30 dark:bg-red-950/10",
  },
};

const StatCard = ({
  title,
  value,
  icon,
  color,
}: {
  title: string;
  value: number;
  icon: React.ReactNode;
  color: string;
}) => (
  <Card className="rounded-2xl">
    <CardContent className="p-6">
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm text-muted-foreground">{title}</p>
          <p className="text-3xl font-bold mt-1">{value}</p>
        </div>
        <div
          className={`w-12 h-12 rounded-xl flex items-center justify-center ${color}`}
        >
          {icon}
        </div>
      </div>
    </CardContent>
  </Card>
);

const RiskDashboard = () => {
  const [risks, setRisks] = useState<RiskScore[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [search, setSearch] = useState("");
  const [levelFilter, setLevelFilter] = useState("ALL");

  //   stats

  const [stats, setStats] = useState({
    total: 0,
    high: 0,
    medium: 0,
    low: 0,
  });

  const PAGE_SIZE = 15;

  const fetchRisks = async (currentPage = 0) => {
    setLoading(true);
    try {
      const response = await apiClient.get<PageResponse>("/admin/risk", {
        params: { page: currentPage, size: PAGE_SIZE },
      });

      setRisks(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
      setPage(response.data.number);

      const all = response.data.content;
      setStats({
        total: response.data.totalElements,
        high: all.filter((r) => r.level === "HIGH").length,
        medium: all.filter((r) => r.level === "MEDIUM").length,
        low: all.filter((r) => r.level === "LOW").length,
      });
    } catch {
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRisks(0);
  }, []);

  const parseReasons = (reasons: string): string[] => {
    try {
      return JSON.parse(reasons) ?? [];
    } catch {
      return reasons ? [reasons] : [];
    }
  };

  const isMobile = (ua: string) => /mobile|android|iphone|ipad/i.test(ua);

  const formatTime = (dateStr: string) =>
    new Date(dateStr).toLocaleString("en-US", {
      day: "numeric",
      month: "short",
      hour: "2-digit",
      minute: "2-digit",
    });

  const filtered = risks.filter((r) => {
    const matchSearch =
      search === "" ||
      r.email?.toLowerCase().includes(search.toLowerCase()) ||
      r.ipAddress?.includes(search);

    const matchLevel = levelFilter === "ALL" || r.level === levelFilter;
    return matchSearch && matchLevel;
  });
  return (
    <div className="max-w-7xl mx-auto px-4 py-8 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-3">
        <AlertTriangle className="w-6 h-6 text-amber-500" />
        <div>
          <h1 className="text-2xl font-bold">Risk Dashboard</h1>
          <p className="text-sm text-muted-foreground">
            Monitor suspicious login activity
          </p>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Events"
          value={stats.total}
          icon={<Shield className="w-6 h-6 text-blue-600" />}
          color="bg-blue-50 dark:bg-blue-950/30"
        />
        <StatCard
          title="High Risk"
          value={stats.high}
          icon={<ShieldAlert className="w-6 h-6 text-red-500" />}
          color="bg-red-50 dark:bg-red-950/30"
        />
        <StatCard
          title="Medium Risk"
          value={stats.medium}
          icon={<Shield className="w-6 h-6 text-amber-500" />}
          color="bg-amber-50 dark:bg-amber-950/30"
        />
        <StatCard
          title="Low Risk"
          value={stats.low}
          icon={<ShieldCheck className="w-6 h-6 text-green-600" />}
          color="bg-green-50 dark:bg-green-950/30"
        />
      </div>

      {/* Table card */}
      <Card className="rounded-2xl">
        <CardHeader>
          <div className="flex items-center justify-between flex-wrap gap-4">
            <CardTitle className="text-base">
              Risk Events
              <Badge variant="secondary" className="ml-2">
                {totalElements}
              </Badge>
            </CardTitle>

            {/* Filters */}
            <div className="flex items-center gap-3 flex-wrap">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                <Input
                  placeholder="Search email or IP..."
                  className="pl-9 w-56"
                  value={search}
                  onChange={(e) => setSearch(e.target.value)}
                />
              </div>

              <Select value={levelFilter} onValueChange={setLevelFilter}>
                <SelectTrigger className="w-36">
                  <SelectValue placeholder="Risk level" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">All levels</SelectItem>
                  <SelectItem value="HIGH">High</SelectItem>
                  <SelectItem value="MEDIUM">Medium</SelectItem>
                  <SelectItem value="LOW">Low</SelectItem>
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
              No risk events found
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-border text-left">
                      <th className="pb-3 font-medium text-muted-foreground">
                        Risk
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        User
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        Score
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        Reasons
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        Device
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        IP
                      </th>
                      <th className="pb-3 font-medium text-muted-foreground">
                        Time
                      </th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-border">
                    {filtered.map((risk) => {
                      const style = riskStyles[risk.level];
                      const reasons = parseReasons(risk.reasons);

                      return (
                        <tr
                          key={risk.id}
                          className={`transition-colors ${style.row}`}
                        >
                          {/* Risk level */}
                          <td className="py-3 pr-4">
                            <span
                              className={`
                              inline-flex items-center gap-1.5
                              text-xs font-medium px-2.5 py-1
                              rounded-full
                              ${style.badge}
                            `}
                            >
                              {style.icon}
                              {risk.level}
                            </span>
                          </td>

                          {/* Email */}
                          <td className="py-3 pr-4">
                            <p className="font-medium truncate max-w-[160px]">
                              {risk.email || "—"}
                            </p>
                          </td>

                          {/* Score */}
                          <td className="py-3 pr-4">
                            <div className="flex items-center gap-2">
                              <div className="w-16 h-1.5 rounded-full bg-muted overflow-hidden">
                                <div
                                  className={`h-full rounded-full ${
                                    risk.level === "HIGH"
                                      ? "bg-red-500"
                                      : risk.level === "MEDIUM"
                                        ? "bg-amber-500"
                                        : "bg-green-500"
                                  }`}
                                  style={{
                                    width: `${Math.min(risk.score, 100)}%`,
                                  }}
                                />
                              </div>
                              <span className="text-xs text-muted-foreground">
                                {risk.score}
                              </span>
                            </div>
                          </td>

                          {/* Reasons */}
                          <td className="py-3 pr-4">
                            <div className="flex flex-wrap gap-1 max-w-[200px]">
                              {reasons.length > 0 ? (
                                reasons.map((r, i) => (
                                  <span
                                    key={i}
                                    className="text-xs bg-muted px-1.5 py-0.5 rounded"
                                  >
                                    {r}
                                  </span>
                                ))
                              ) : (
                                <span className="text-xs text-muted-foreground">
                                  —
                                </span>
                              )}
                            </div>
                          </td>

                          {/* Device */}
                          <td className="py-3 pr-4">
                            <span className="flex items-center gap-1.5 text-xs text-muted-foreground">
                              {isMobile(risk.userAgent) ? (
                                <Smartphone className="w-3.5 h-3.5" />
                              ) : (
                                <Monitor className="w-3.5 h-3.5" />
                              )}
                              {isMobile(risk.userAgent) ? "Mobile" : "Desktop"}
                            </span>
                          </td>

                          {/* IP */}
                          <td className="py-3 pr-4">
                            <p className="font-mono text-xs text-muted-foreground">
                              {risk.ipAddress || "—"}
                            </p>
                          </td>

                          {/* Time */}
                          <td className="py-3 text-xs text-muted-foreground whitespace-nowrap">
                            {formatTime(risk.createdAt)}
                          </td>
                        </tr>
                      );
                    })}
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
                    onClick={() => fetchRisks(page - 1)}
                  >
                    <ChevronLeft className="w-4 h-4" />
                    Previous
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={page >= totalPages - 1}
                    onClick={() => fetchRisks(page + 1)}
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
export default RiskDashboard;
