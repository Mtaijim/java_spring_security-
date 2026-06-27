import useAuthStore from "@/auth/store.ts";
import { Button } from "./ui/button.tsx";
import { NavLink, useNavigate } from "react-router";
import { isAdmin } from "@/utils/roles";

const Navbar = () => {
  const checkLogin = useAuthStore((state) => state.checkLogin);
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const navigate = useNavigate();

  return (
    <nav className="flex justify-between items-center px-6 py-3 border-b border-border bg-background text-foreground">
      {/* ── Logo ── */}
      <NavLink
        to="/"
        className={({ isActive }) =>
          `flex items-center gap-2 transition-colors ${
            isActive ? "text-primary" : ""
          }`
        }
      >
        <span className="rounded border border-border bg-card px-4 py-1.5 text-sm font-bold tracking-wide shadow-sm">
          Auth<span className="text-primary">X</span>
        </span>
      </NavLink>

      {/* ── Nav links ── */}
      <div className="flex items-center gap-4">
        {checkLogin() ? (
          <>
            {isAdmin(user) && (
              <NavLink
                to="/dashboard/admin/users"
                className="text-sm text-primary"
              >
                Admin panel
              </NavLink>
            )}
            <NavLink to={"/dashboard/profile"}>
              <div className="flex gap-2 justify-center items-center">
                <div>
                  {<img className="w-10 rounded-full" src={user?.image} />}
                </div>{" "}
                <div>{user?.name}</div>
              </div>
            </NavLink>

            <Button
              onClick={() => {
                logout();
                navigate("/");
              }}
              size={"sm"}
              className="cursor-pointer"
              variant={"outline"}
            >
              Logout
            </Button>
          </>
        ) : (
          <>
            <NavLink
              to="/"
              className={({ isActive }) =>
                `text-sm font-medium transition-colors hover:text-foreground ${
                  isActive ? "text-foreground" : "text-muted-foreground"
                }`
              }
            >
              Home
            </NavLink>

            <NavLink to="/login">
              <Button size="sm" variant="outline" className="cursor-pointer">
                Login
              </Button>
            </NavLink>

            <NavLink to="/signup">
              <Button size="sm" className="cursor-pointer">
                Sign up
              </Button>
            </NavLink>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
