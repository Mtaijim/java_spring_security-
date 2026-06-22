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

createRoot(document.getElementById("root")!).render(
  <BrowserRouter>
    <Routes>
      <Route path="/" element={<RootLayout />}>
        <Route index element={<App />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/login" element={<Login />} />
        <Route path="/about" element={<About />} />
        <Route path="/services" element={<Services />} />
      </Route>
    </Routes>
  </BrowserRouter>,
);
