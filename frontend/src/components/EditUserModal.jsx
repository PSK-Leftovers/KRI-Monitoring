import { useEffect, useState } from "react";
import { updateUser } from "../services/authService";

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const ROLES = [
  { value: "ANALYST", label: "Analitikas" },
  { value: "DIRECTOR", label: "Direktorius" },
  { value: "ADMIN", label: "Administratorius" },
];

export default function EditUserModal({ user, onClose }) {
  const [form, setForm] = useState({
    name: "",
    email: "",
    password: "",
    role: "ANALYST",
  });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (user) {
      setForm({
        name: user.name ?? "",
        email: user.email ?? "",
        password: "",
        role: user.role ?? "ANALYST",
      });
    }
  }, [user]);

  const validate = () => {
    const errs = {};
    if (!form.name.trim()) errs.name = "Vartotojo vardas yra privalomas";
    if (!form.email.trim()) errs.email = "El. pašto adresas yra privalomas";
    else if (!EMAIL_REGEX.test(form.email)) errs.email = "Neteisingas el. pašto formatas";
    if (form.password && form.password.length < 8) errs.password = "Slaptažodis turi būti bent 8 simbolių";
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
      await updateUser(user.id, form.name, form.email, form.password, form.role);
      window.alert("Vartotojas atnaujintas sėkmingai!");
      setSuccess(true);
    } catch (err) {
      setServerError(
        err.message === "user_exists"
          ? "Vartotojas su tokiu el. paštu jau egzistuoja!"
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
          <p className="text-xl font-bold text-gray-900 mb-8">Vartotojas atnaujintas sėkmingai!</p>
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
        <h2 className="text-xl font-bold text-gray-900 text-center mb-6">Vartotojo redagavimas</h2>
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
              Slaptažodis <span className="text-gray-400 text-xs">(nebūtina)</span>
            </label>
            <input
              {...field("password")}
              type="password"
              placeholder="Palikite tuščią, jei nekeičiate"
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

          <button
            type="submit"
            className="w-full py-3 bg-gray-900 text-white rounded-lg font-medium hover:bg-gray-800 transition-colors cursor-pointer mt-2"
          >
            Išsaugoti
          </button>

          {serverError && <p className="text-red-500 text-sm text-center">{serverError}</p>}
        </form>
      </div>
    </div>
  );
}