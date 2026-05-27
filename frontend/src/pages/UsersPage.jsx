import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { listUsers } from "../services/authService";
import CreateUserModal from "../components/CreateUserModal";
import EditUserModal from "../components/EditUserModal";
import { deleteUser } from "../services/authService";

const ROLE_LABELS = {
  ANALYST: "Analitikas",
  DIRECTOR: "Direktorius",
  ADMIN: "Administratorius",
};

export default function UsersPage() {
  const [users, setUsers] = useState([]);
  const [roleFilter, setRoleFilter] = useState("ALL");
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const navigate = useNavigate();

  const currentUser = JSON.parse(localStorage.getItem("user") || "{}");

  useEffect(() => {
    if (currentUser.role !== "ADMIN") {
      navigate("/indicators");
      return;
    }
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const data = await listUsers();
      setUsers(data);
    } catch {
      navigate("/indicators");
    }
  };

  const filtered = roleFilter === "ALL" ? users : users.filter((u) => u.role === roleFilter);

  const formatDate = (dateStr) => {
    if (!dateStr) return "—";
    return new Date(dateStr).toLocaleDateString();
  };

  const formatDateTime = (dateStr) => {
    if (!dateStr) return "—";
    return new Date(dateStr).toLocaleString();
  };

  const openEditModal = (user) => {
    setEditingUser(user);
  };

  const handleDelete = async (user) => {
    const confirmed = window.confirm(`Ar tikrai norite ištrinti vartotoją ${user.name}?`);
    if (!confirmed) return;

    try {
      await deleteUser(user.id);
      await fetchUsers();
    } catch {
      window.alert("Nepavyko ištrinti vartotojo.");
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 px-6 py-8">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Vartotojai</h1>
            <p className="text-sm text-gray-500 mt-0.5">Valdykite sistemos vartotojus</p>
          </div>
          <div className="flex items-center gap-3">
            <button
              onClick={() => navigate("/indicators")}
              className="text-sm text-gray-500 hover:text-gray-700 cursor-pointer"
            >
              ← Indikatoriai
            </button>
            <button
              onClick={() => setShowModal(true)}
              className="inline-flex items-center gap-2 px-4 py-2 bg-gray-900 text-white text-sm font-medium rounded-lg hover:bg-gray-800 transition-colors cursor-pointer"
            >
              + Sukurti naują
            </button>
          </div>
        </div>

        <div className="mb-4">
          <select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            className="px-3 py-2 text-sm border border-gray-200 rounded-lg bg-white text-gray-700 cursor-pointer"
          >
            <option value="ALL">Visos rolės</option>
            <option value="ANALYST">Analitikas</option>
            <option value="DIRECTOR">Direktorius</option>
            <option value="ADMIN">Administratorius</option>
          </select>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Vartotojo vardas</th>
                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">El. paštas</th>
                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Rolė</th>
                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Sukūrimo data</th>
                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Pask. prisijungimas</th>
                <th className="text-left px-6 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Veiksmai</th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-6 py-16 text-center text-gray-400 text-sm">
                    Vartotojų nerasta.
                  </td>
                </tr>
              ) : (
                filtered.map((user) => (
                  <tr key={user.id} className="border-t border-gray-100 hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 font-medium text-gray-900">{user.name}</td>
                    <td className="px-6 py-4 text-gray-500">{user.email}</td>
                    <td className="px-6 py-4 text-gray-700">{ROLE_LABELS[user.role] ?? user.role}</td>
                    <td className="px-6 py-4 text-gray-500">{formatDate(user.createdAt)}</td>
                    <td className="px-6 py-4 text-gray-500">{formatDateTime(user.lastLogin)}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <button
                          onClick={() => openEditModal(user)}
                          className="text-xs text-brand-700 font-medium cursor-pointer hover:underline"
                        >
                          Redaguoti
                        </button>
                        <button
                          onClick={() => handleDelete(user)}
                          className="text-xs text-red-600 font-medium cursor-pointer hover:underline"
                        >
                          Ištrinti
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {showModal && (
        <CreateUserModal
          onClose={() => {
            setShowModal(false);
            fetchUsers();
          }}
        />
      )}

      {editingUser && (
        <EditUserModal
          user={editingUser}
          onClose={() => {
            setEditingUser(null);
            fetchUsers();
          }}
        />
      )}
    </div>
  );
}
