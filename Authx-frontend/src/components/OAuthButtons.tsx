import { Button } from "./ui/button";
import { FaGoogle, FaGithub } from "react-icons/fa";

const BASE_URL = import.meta.env.VITE_BASE_URL || "http://localhost:8080";

export default function OAuthButtons() {
  return (
    <div className="flex flex-col  gap-3 justify-center">
      <Button asChild variant="outline">
        <a href={`${BASE_URL}/oauth2/authorization/google`}>
          <FaGoogle className="mr-2 h-4 w-4" />
          Continue with Google
        </a>
      </Button>

      <Button asChild variant="outline">
        <a href={`${BASE_URL}/oauth2/authorization/github`}>
          <FaGithub className="mr-2 h-4 w-4" />
          Continue with GitHub
        </a>
      </Button>
    </div>
  );
}
