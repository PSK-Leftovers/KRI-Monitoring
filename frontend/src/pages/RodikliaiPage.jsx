import { useEffect, useState } from "react";
import RodiklisForm from "../components/RodiklisForm";
import DeleteModal from "../components/DeleteModal";

const API = "http://localhost:8080/api/rodikliai";

export default function RodikliaiPage() {
    const [rodikliai, setRodikliai] = useState([]);
    const [editing, setEditing] = useState(null);
    const [deleting, setDeleting] = useState(null);

    useEffect(() => {
        fetchAll();
    }, []);

    const fetchAll = async () => {
        const response = await fetch(API);
        const data = await response.json();
        setRodikliai(data);
    };

    const handleSave = async (data) => {
        const method = data.id ? "PUT" : "POST";
        const url = data.id ? `${API}/${data.id}` : API;

        await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        });

        setEditing(null);
        fetchAll();
    };

    const handleDelete = async () => {
        await fetch(`${API}/${deleting.id}`, {
            method: "DELETE",
        });

        setDeleting(null);
        fetchAll();
    };

    if (editing !== null) {
        return (
            <RodiklisForm
                initial={editing}
                onSave={handleSave}
                onCancel={() => setEditing(null)}
            />
        );
    }

    return (
        <div style={page}>
            <div style={header}>
                <h1 style={title}>Rizikos rodikliai</h1>
                <button style={primaryButton} onClick={() => setEditing({})}>
                    + Naujas rodiklis
                </button>
            </div>

            <table style={table}>
                <thead>
                <tr style={{ background: "#f5f5f5" }}>
                    <th style={th}>Pavadinimas</th>
                    <th style={th}>Aprašymas</th>
                    <th style={th}>Ribos ž/g/r</th>
                    <th style={th}>Veiksmai</th>
                </tr>
                </thead>

                <tbody>
                {rodikliai.length === 0 ? (
                    <tr>
                        <td style={td} colSpan="4">
                            Rodiklių dar nėra.
                        </td>
                    </tr>
                ) : (
                    rodikliai.map((r) => (
                        <tr key={r.id}>
                            <td style={td}>
                                <strong>{r.pavadinimas}</strong>
                            </td>
                            <td style={td}>{r.aprasymas}</td>
                            <td style={td}>
                                <span style={{ color: "green" }}>{r.ribaZalia}</span> /{" "}
                                <span style={{ color: "orange" }}>{r.ribaGeltona}</span> /{" "}
                                <span style={{ color: "red" }}>{r.ribaRaudona}</span>
                            </td>
                            <td style={td}>
                                <button onClick={() => setEditing(r)}>Redaguoti</button>
                                &nbsp;
                                <button
                                    onClick={() => setDeleting(r)}
                                    style={{ color: "red" }}
                                >
                                    Ištrinti
                                </button>
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>

            {deleting && (
                <DeleteModal
                    rodiklis={deleting}
                    onConfirm={handleDelete}
                    onCancel={() => setDeleting(null)}
                />
            )}
        </div>
    );
}

const page = {
    padding: "1.5rem",
    fontFamily: "Arial, sans-serif",
};

const header = {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "1rem",
};

const title = {
    fontSize: "22px",
    fontWeight: 600,
};

const table = {
    width: "100%",
    borderCollapse: "collapse",
    fontSize: "14px",
    background: "#fff",
};

const th = {
    textAlign: "left",
    padding: "10px 12px",
    borderBottom: "1px solid #ddd",
};

const td = {
    padding: "10px 12px",
    borderBottom: "1px solid #eee",
};

const primaryButton = {
    background: "#3A2A6E",
    color: "#fff",
    border: "none",
    padding: "8px 14px",
    borderRadius: "6px",
    cursor: "pointer",
};