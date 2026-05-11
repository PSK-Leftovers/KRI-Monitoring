import { useState } from "react";
import { FaEnvelope, FaLock } from "react-icons/fa";
import { login } from "../services/authService";
import { useNavigate } from "react-router-dom";

function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);

  const navigate = useNavigate();

  const isDisabled = !username || !password;

  const submitLoginDetails = async (e) => {
    e.preventDefault();
    setError(null);

    try {
      const user_info = await login(username, password);

      localStorage.setItem("user", JSON.stringify({ email: user_info.email, role: user_info.role}));

      navigate("/indicators");
    } catch (err) {
      setError("Login failed");
    }
  };

  return (
    <main style={styles.main}>
      <h1 style={styles.title}>KRI Login</h1>

      <div style={styles.centerBox}>
        <form onSubmit={submitLoginDetails} style={styles.form}>

          <label style={styles.label}>
            Email
            <div style={styles.inputBox}>
              <FaEnvelope style={styles.icon} />
              <input
                style={styles.input}
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
          </label>

          <label style={styles.label}>
            Password
            <div style={styles.inputBox}>
              <FaLock style={styles.icon} />
              <input
                style={styles.input}
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </label>

          {error && <p style={styles.error}>{error}</p>}

          <button
            style={isDisabled ? styles.buttonDisabled : styles.button}
            disabled={isDisabled}
          >
            Login
          </button>
        </form>
      </div>
    </main>
  );
}

export default LoginPage;

const styles = {
  main: {
    fontFamily: "Arial, sans-serif",
    height: "100vh",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#f4f6f8",
  },

  title: {
    marginBottom: "20px",
  },

  centerBox: {
    background: "white",
    padding: "30px",
    borderRadius: "10px",
    boxShadow: "0 2px 10px rgba(0,0,0,0.1)",
    width: "300px",
  },

  form: {
    display: "flex",
    flexDirection: "column",
    gap: "15px",
  },

  label: {
    display: "flex",
    flexDirection: "column",
    fontSize: "14px",
    gap: "5px",
  },

  inputBox: {
    display: "flex",
    alignItems: "center",
    border: "1px solid #ccc",
    borderRadius: "6px",
    padding: "6px",
    background: "white",
  },

  input: {
    border: "none",
    outline: "none",
    flex: 1,
    padding: "6px",
  },

  icon: {
    marginRight: "8px",
    color: "#666",
  },

  button: {
    padding: "10px",
    background: "#007bff",
    color: "white",
    border: "none",
    borderRadius: "6px",
    cursor: "pointer",
  },

  buttonDisabled: {
    padding: "10px",
    background: "#ccc",
    color: "#666",
    border: "none",
    borderRadius: "6px",
    cursor: "not-allowed",
  },

  error: {
    color: "red",
    fontSize: "13px",
  },
};