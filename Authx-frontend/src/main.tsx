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
import Userhome from "./pages/users/Userhome.tsx";
import Userprofile from "./pages/users/Userprofile.tsx";
import OAuthScucess from "./pages/OAuthScucess.tsx";
import OAuthFailure from "./pages/OAuthFailure.tsx";
import AdminRoute from "./pages/AdminRoute.tsx";
import AdminUserList from "./pages/users/AdminUserList.tsx";
import VerifyEmail from "./pages/VerifyEmail.tsx";
import CheckEmail from "./pages/users/CheckEmail.tsx";
import { ThemeProvider } from "./components/ThemeProvider.tsx";
import ForgotPassword from "./pages/ForgotPassword.tsx";
import ResetPassword from "./pages/ResetPassword.tsx";
import MfaChallenges from "./pages/MfaChallenges.tsx";
import MfaSetup from "./pages/users/MfaSetup.tsx";
import { LoginHistory } from "./pages/LoginHistory.tsx";
import VerifyRisk from "./pages/VerifyRisk.tsx";
import AuditLogViewer from "./pages/AuditLogViewer.tsx";
import PermissionsManager from "./pages/PermissionsManager.tsx";
import OrgsList from "./pages/orgs/OrgsList.tsx";
import CreateOrg from "./pages/orgs/CreateOrg.tsx";
import OrgDetails from "./pages/orgs/OrgDetails.tsx";

createRoot(document.getElementById("root")!).render(
  <BrowserRouter>
    <ThemeProvider defaultTheme="system" storageKey="authx-theme">
      <Routes>
        <Route path="/" element={<RootLayout />}>
          <Route index element={<App />} />
          <Route path="/signup" element={<SignUp />} />
          <Route path="/login" element={<Login />} />
          <Route path="/about" element={<About />} />
          <Route path="/services" element={<Services />} />
          <Route path="/mfa/verify" element={<MfaChallenges />} />

          <Route path="/dashboard" element={<Dashboard />}>
            <Route path="/dashboard/history" element={<LoginHistory />} />
            <Route index element={<Userhome />} />
            <Route path="profile" element={<Userprofile />} />
            <Route path="mfa/setup" element={<MfaSetup />} />
            <Route element={<AdminRoute />}>
              <Route path="admin/users" element={<AdminUserList />} />
              <Route path="/admin/audit" element={<AuditLogViewer />} />
              <Route
                path="/admin/permissions"
                element={<PermissionsManager />}
              />
            </Route>
          </Route>
          <Route path="oauth/success" element={<OAuthScucess />} />
          <Route path="oauth/failure" element={<OAuthFailure />} />
          <Route path="/verify-email" element={<VerifyEmail />} />
          <Route path="/check-email" element={<CheckEmail />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/auth/verify-risk" element={<VerifyRisk />} />

          <Route path="/orgs" element={<OrgsList />} />
          <Route path="/orgs/create" element={<CreateOrg />} />
          <Route path="/orgs/:orgId" element={<OrgDetails />} />
        </Route>
      </Routes>
    </ThemeProvider>
  </BrowserRouter>,
);
