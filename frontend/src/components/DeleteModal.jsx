export default function DeleteModal({ rodiklis, onConfirm, onCancel }) {
    return (
        <div style={overlay}>
            <div style={modal}>
                <div style={modalHeader}>
                    <strong>Ištrinti rodiklį?</strong>
                </div>

                <div style={modalBody}>
                    Ar tikrai norite ištrinti{" "}
                    <strong>„{rodiklis.pavadinimas}“</strong>?
                    <br />
                    Šio veiksmo negalima atšaukti.
                </div>

                <div style={modalFooter}>
                    <button onClick={onCancel}>Atšaukti</button>

                    <button onClick={onConfirm} style={deleteButton}>
                        Ištrinti
                    </button>
                </div>
            </div>
        </div>
    );
}

const overlay = {
    position: "fixed",
    inset: 0,
    background: "rgba(0,0,0,0.4)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    zIndex: 100,
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