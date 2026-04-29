export default function DeleteModal({ indicator, onConfirm, onCancel }) {
    return (
        <div style={overlay}>
            <div style={modal}>
                <div style={modalHeader}>
                    <strong>Delete indicator?</strong>
                </div>

                <div style={modalBody}>
                    Are you sure you want to delete{" "}
                    <strong>“{indicator.name}”</strong>?
                    <br />
                    This action cannot be undone.
                </div>

                <div style={modalFooter}>
                    <button onClick={onCancel}>Cancel</button>

                    <button onClick={onConfirm} style={deleteButton}>
                        Delete
                    </button>
                </div>
            </div>
        </div>
    );
}

// ✅ missing styles added here

const overlay = {
    position: "fixed",
    inset: 0,
    background: "rgba(0,0,0,0.4)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 1000,
};

const modal = {
    background: "#fff",
    borderRadius: "12px",
    width: "360px",
    overflow: "hidden",
};

const modalHeader = {
    padding: "16px",
    borderBottom: "1px solid #eee",
};

const modalBody = {
    padding: "16px",
    fontSize: "14px",
    color: "#555",
};

const modalFooter = {
    padding: "12px 16px",
    borderTop: "1px solid #eee",
    display: "flex",
    justifyContent: "flex-end",
    gap: "8px",
};

const deleteButton = {
    background: "#FCEBEB",
    color: "#A32D2D",
    border: "1px solid #F09595",
    padding: "6px 14px",
    borderRadius: "6px",
    cursor: "pointer",
};