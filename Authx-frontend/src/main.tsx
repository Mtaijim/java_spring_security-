import { Route } from "react-router";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { BrowserRouter, Routes } from "react-router";
import SignUp from "./pages/SignUp.tsx";
import Login from "./pages/Login.tsx";
import About from "./pages/About.tsx";
import { Services } from "./pages/Services.tsx";
import RootLayout from "./pages/RootLayout.tsx";
import Dashboard from "./pages/users/Dashboard.tsx";
import { Userhome } from "./pages/users/Userhome.tsx";
import Userprofile from "./pages/users/Userprofile.tsx";
import OAuthScucess from "./pages/OAuthScucess.tsx";
import OAuthFailure from "./pages/OAuthFailure.tsx";
import AdminRoute from "./pages/AdminRoute.tsx";
import AdminUserList from "./pages/users/AdminUserList.tsx";

createRoot(document.getElementById("root")!).render(
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<RootLayout />}>
        <Route index element={<App />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/login" element={<Login />} />
        <Route path="/about" element={<About />} />
        <Route path="/services" element={<Services />} />
        <Route path="/dashboard" element={<Dashboard />}>
          <Route index element={<Userhome />} />
          <Route path="profile" element={<Userprofile />} />
          <Route element={<AdminRoute />}>
            <Route path="admin/users" element={<AdminUserList />} />
          </Route>
        </Route>
        <Route path="oauth/success" element={<OAuthScucess />} />
        <Route path="oauth/failure" element={<OAuthFailure />} />
      </Route>
    </Routes>
  </BrowserRouter>,
);
