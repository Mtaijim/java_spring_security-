import useAuthStore from "@/auth/store";
import { Navigate, Outlet } from "react-router";

const Dashboard = () => {
  const checkLogin = useAuthStore((state) => state.checkLogin);

  if (checkLogin())
    return (
      <div>
        <Outlet />
      </div>
    );
  else return <Navigate to={"/login"} />;
};

export default Dashboard;
