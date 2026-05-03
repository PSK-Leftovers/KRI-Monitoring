import { Routes, Route, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import IndicatorsPage from "./pages/IndicatorsPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/indicators" element={<IndicatorsPage />} />
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}