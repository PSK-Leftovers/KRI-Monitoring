import { useState } from "react";
import { createUser } from "../services/authService";

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const ROLES = [
  { value: "ANALYST", label: "Analitikas" },
  { value: "DIRECTOR", label: "Direktorius" },
  { value: "ADMIN", label: "Administratorius" },
];

export default function CreateUserModal({ onClose }) {
  const [form, setForm] = useState({ name: "", email: "", password: "", role: "ANALYST" });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState(null);
  const [success, setSuccess] = useState(false);

  const validate = () => {
    const errs = {};
    if (!form.name.trim()) errs.name = "Vartotojo vardas yra privalomas";
    if (!form.email.trim()) errs.email = "El. pašto adresas yra privalomas";
    else if (!EMAIL_REGEX.test(form.email)) errs.email = "Neteisingas el. pašto formatas";
    if (!form.password.trim()) errs.password = "Slaptažodis yra privalomas";
    else if (form.password.length < 8) errs.password = "Slaptažodis turi būti bent 8 simbolių";
    return errs;
  };

  const handleSubmit = async (evt) => {
    evt.preventDefault();
    setServerError(null);
    const errs = validate();
    if (Object.keys(errs).length > 0) {
      setErrors(errs);
      return;
    }
    try {
      await createUser(form.name, form.email, form.password, form.role);
      setSuccess(true);
    } catch (err) {
      setServerError(
          err.message === "user_exists"
              ? "Vartotojas jau egzistuoja!"
              : err.message || "Įvyko klaida. Bandykite dar kartą."
      );
    }
  };

  const field = (key) => ({
    value: form[key],
    onChange: (e) => {
      setForm((f) => ({ ...f, [key]: e.target.value }));
      if (errors[key]) setErrors((errs) => ({ ...errs, [key]: undefined }));
    },
  });

  if (success) {
    return (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl p-10 w-full max-w-md text-center shadow-xl">
            <div className="flex justify-center mb-6">
              <svg className="w-24 h-24 text-gray-900" viewBox="0 0 100 100" fill="none" stroke="currentColor" strokeWidth="5">
                <circle cx="50" cy="50" r="45" />
                <polyline points="30,52 44,66 70,38" strokeLinecap="round" strokeLinejoin="round" />
              </svg>
            </div>
            <p className="text-xl font-bold text-gray-900 mb-8">Vartotojas sukurtas sėkmingai!</p>
            <button
                onClick={onClose}
                className="w-full py-3 bg-gray-900 text-white rounded-lg font-medium hover:bg-gray-800 transition-colors cursor-pointer"
            >
              Tęsti
            </button>
          </div>
        </div>
    );
  }

  return (
      <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
        <div className="bg-white rounded-2xl p-8 w-full max-w-md shadow-xl">
          <h2 className="text-xl font-bold text-gray-900 text-center mb-6">Vartotojo sukūrimas</h2>
          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Vartotojo vardas <span className="text-red-500">*</span>
              </label>
              <input
                  {...field("name")}
                  placeholder="Jonas Jonaitis"
                  className={`w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-1 ${
                      errors.name
                          ? "border-red-400 focus:ring-red-300"
                          : "border-gray-300 focus:ring-brand-700"
                  }`}
              />
              {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                El. pašto adresas <span className="text-red-500">*</span>
              </label>
              <input
                  {...field("email")}
                  type="email"
                  placeholder="jonas@gmail.com"
                  className={`w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-1 ${
                      errors.email
                          ? "border-red-400 focus:ring-red-300"
                          : "border-gray-300 focus:ring-brand-700"
                  }`}
              />
              {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Slaptažodis <span className="text-red-500">*</span>
              </label>
              <input
                  {...field("password")}
                  type="password"
                  className={`w-full px-3 py-2 border rounded-lg text-sm outline-none focus:ring-1 ${
                      errors.password
                          ? "border-red-400 focus:ring-red-300"
                          : "border-gray-300 focus:ring-brand-700"
                  }`}
              />
              {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Rolė</label>
              <select
                  value={form.role}
                  onChange={(e) => setForm((f) => ({ ...f, role: e.target.value }))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm bg-white outline-none focus:ring-1 focus:ring-brand-700 cursor-pointer"
              >
                {ROLES.map((r) => (
                    <option key={r.value} value={r.value}>{r.label}</option>
                ))}
              </select>
            </div>

            {/* Atnaujinta mygtukų sekcija */}
            <div className="flex gap-3 mt-2">
              <button
                  type="button"
                  onClick={onClose}
                  className="flex-1 py-3 border border-gray-300 text-gray-700 rounded-lg font-medium hover:bg-gray-50 transition-colors cursor-pointer text-center text-sm"
              >
                Grįžti
              </button>
              <button
                  type="submit"
                  className="flex-1 py-3 bg-gray-900 text-white rounded-lg font-medium hover:bg-gray-800 transition-colors cursor-pointer text-sm"
              >
                Sukurti
              </button>
            </div>

            {serverError && <p className="text-red-500 text-sm text-center">{serverError}</p>}
          </form>
        </div>
      </div>
  );
}