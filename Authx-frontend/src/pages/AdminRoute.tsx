import useAuthStore from "@/auth/store";
import { isAdmin } from "@/utils/roles";
import { Navigate, Outlet } from "react-router";

const AdminRoute = () => {
  const user = useAuthStore((s) => s.user);
  if (!user) return <Navigate to={"/login"} />;
  if (!isAdmin(user)) return <Navigate to={"/dashboard"} />;
  return <Outlet />;
};

export default AdminRoute;
