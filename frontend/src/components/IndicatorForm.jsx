import { useState } from "react";

export default function IndicatorForm({ initial, onSave, onCancel }) {
    const [form, setForm] = useState({
        name: "",
        description: "",
        greenThreshold: "",
        yellowThreshold: "",
        redThreshold: "",
        ...initial,
    });

    const set = (key) => (event) => {
        setForm((current) => ({
            ...current,
            [key]: event.target.value,
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        onSave({
            ...form,
            greenThreshold: Number(form.greenThreshold),
            yellowThreshold: Number(form.yellowThreshold),
            redThreshold: Number(form.redThreshold),
        });
    };

    return (
        <div style={page}>
            <button onClick={onCancel} style={backButton}>
                ← Back
            </button>

            <h1 style={title}>
                {form.id ? "Edit indicator" : "New indicator"}
            </h1>

            <form onSubmit={handleSubmit} style={formStyle}>
                <div>
                    <label>Name *</label>
                    <input
                        required
                        value={form.name}
                        onChange={set("name")}
                        placeholder="Enter name"
                        style={input}
                    />
                </div>

                <div>
                    <label>Description</label>
                    <textarea
                        value={form.description || ""}
                        onChange={set("description")}
                        placeholder="Short description..."
                        style={textarea}
                    />
                </div>

                <div>
                    <label>Risk thresholds *</label>

                    <div style={limitsGrid}>
                        {[
                            ["greenThreshold", "Green threshold", "#27500A"],
                            ["yellowThreshold", "Yellow threshold", "#633806"],
                            ["redThreshold", "Red threshold", "#A32D2D"],
                        ].map(([key, label, color]) => (
                            <div key={key} style={limitBox(color)}>
                                <label style={{ fontSize: "12px", color, fontWeight: 500 }}>
                                    {label}
                                </label>
                                <input
                                    required
                                    type="number"
                                    value={form[key]}
                                    onChange={set(key)}
                                    placeholder="e.g. 100"
                                    style={input}
                                />
                            </div>
                        ))}
                    </div>
                </div>

                <div style={actions}>
                    <button type="button" onClick={onCancel}>
                        Cancel
                    </button>

                    <button type="submit" style={submitButton}>
                        {form.id ? "Save" : "Create indicator"}
                    </button>
                </div>
            </form>
        </div>
    );
}

const page = {
    padding: "1.5rem",
    maxWidth: "650px",
    fontFamily: "Arial, sans-serif",
};

const backButton = {
    marginBottom: "1rem",
};

const title = {
    fontSize: "22px",
    fontWeight: 600,
    marginBottom: "1.25rem",
};

const formStyle = {
    display: "flex",
    flexDirection: "column",
    gap: "14px",
};

const input = {
    display: "block",
    width: "100%",
    boxSizing: "border-box",
    marginTop: "4px",
    padding: "8px",
};

const textarea = {
    display: "block",
    width: "100%",
    boxSizing: "border-box",
    marginTop: "4px",
    height: "72px",
    padding: "8px",
};

const limitsGrid = {
    display: "grid",
    gridTemplateColumns: "1fr 1fr 1fr",
    gap: "10px",
    marginTop: "6px",
};

const limitBox = (color) => ({
    border: `1px solid ${color}33`,
    borderRadius: "6px",
    padding: "8px",
});

const actions = {
    display: "flex",
    justifyContent: "flex-end",
    gap: "8px",
    marginTop: "8px",
};

const submitButton = {
    background: "#3A2A6E",
    color: "#fff",
    border: "none",
    padding: "8px 16px",
    borderRadius: "6px",
    cursor: "pointer",
};