import { useEffect, useState } from "react";
import IndicatorForm from "../components/IndicatorForm.jsx";
import DeleteModal from "../components/DeleteModal";


const API = "http://localhost:8080/api/indicators";

export default function IndicatorsPage() {
    const [indicators, setIndicators] = useState([]);
    const [editing, setEditing] = useState(null);
    const [deleting, setDeleting] = useState(null);

    const fetchAll = async () => {
        const response = await fetch(API, {credentials: "include"});
        return response.json();
    };

    useEffect(() => {
        fetchAll().then(setIndicators);
    }, []);

    const handleSave = async (data) => {
        const method = data.id ? "PUT" : "POST";
        const url = data.id ? `${API}/${data.id}` : API;

        await fetch(url, {
            method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
            credentials: "include"
        });

        const updated = await fetchAll();
        setIndicators(updated);

        setEditing(null);
    };

    const handleDelete = async () => {
        await fetch(`${API}/${deleting.id}`, {
            method: "DELETE",
            credentials: "include"
            
        });

        const updated = await fetchAll();
        setIndicators(updated);

        setDeleting(null);
    };

    if (editing !== null) {
        return (
            <IndicatorForm
                initial={editing}
                onSave={handleSave}
                onCancel={() => setEditing(null)}
            />
        );
    }

    return (
        <div style={styles.page}>
            <div style={styles.header}>
                <h1 style={styles.title}>Risk indicators</h1>

                <button style={styles.primaryButton} onClick={() => setEditing({})}>
                    + New indicator
                </button>
            </div>

            <table style={styles.table}>
                <thead>
                <tr style={{ background: "#f5f5f5" }}>
                    <th style={styles.th}>Name</th>
                    <th style={styles.th}>Description</th>
                    <th style={styles.th}>Thresholds (G/Y/R)</th>
                    <th style={styles.th}>Actions</th>
                </tr>
                </thead>

                <tbody>
                {indicators.length === 0 ? (
                    <tr>
                        <td style={styles.td} colSpan="4">
                            No indicators found.
                        </td>
                    </tr>
                ) : (
                    indicators.map((i) => (
                        <tr key={i.id}>
                            <td style={styles.td}><strong>{i.name}</strong></td>
                            <td style={styles.td}>{i.description}</td>
                            <td style={styles.td}>
                                <span style={{ color: "green" }}>{i.greenThreshold}</span> /{" "}
                                <span style={{ color: "orange" }}>{i.yellowThreshold}</span> /{" "}
                                <span style={{ color: "red" }}>{i.redThreshold}</span>
                            </td>
                            <td style={styles.td}>
                                <button onClick={() => setEditing(i)}>Edit</button>
                                &nbsp;
                                <button
                                    onClick={() => setDeleting(i)}
                                    style={{ color: "red" }}
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))
                )}
                </tbody>
            </table>

            {deleting && (
                <DeleteModal
                    indicator={deleting}
                    onConfirm={handleDelete}
                    onCancel={() => setDeleting(null)}
                />
            )}
        </div>
    );
}

const styles = {
    page: {
        padding: "1.5rem",
        fontFamily: "Arial, sans-serif",
    },
    header: {
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: "1rem",
    },
    title: {
        margin: 0,
    },
    primaryButton: {
        padding: "0.5rem 1rem",
        cursor: "pointer",
    },
    table: {
        width: "100%",
        borderCollapse: "collapse",
    },
    th: {
        textAlign: "left",
        padding: "8px",
    },
    td: {
        padding: "8px",
        borderTop: "1px solid #ddd",
    },
};