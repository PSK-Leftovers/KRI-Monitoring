import { Navigate } from "react-router-dom";

export default function ProtectedRoute({ children, allowedRoles }) {
  const userString = localStorage.getItem("user");

  if (!userString) {
    return <Navigate to="/" replace />;
  }

  const user = JSON.parse(userString);

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to="/indicators" replace />;
  }

  return children;
}