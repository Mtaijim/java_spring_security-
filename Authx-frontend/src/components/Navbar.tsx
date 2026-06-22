import { Button } from "./ui/button.tsx";
import { NavLink } from "react-router";

const Navbar = () => {
  return (
    <nav className="flex justify-between items-center px-6 py-3 border-b border-border bg-background text-foreground">
      {/* ── Logo ── */}
      <NavLink to="/" className="flex items-center gap-2">
        <span className="rounded border border-border bg-muted px-4 py-1.5 text-sm font-bold tracking-wide">
          Auth<span className="text-primary">X</span>
        </span>
      </NavLink>

      {/* ── Nav links ── */}
      <div className="flex items-center gap-4">
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
      </div>
    </nav>
  );
};

export default Navbar;
