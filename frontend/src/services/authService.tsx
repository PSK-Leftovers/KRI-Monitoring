const AUTH_URL = "http://localhost:8080/auth";
const ADMIN_URL = "http://localhost:8080/admin";

export async function login(email: string, password: string) {
  const res = await fetch(`${AUTH_URL}/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ email, password }),
  });

  if (!res.ok) throw new Error("Invalid credentials");
  return res.json();
}

export async function logout() {
  await fetch(`${AUTH_URL}/logout`, {
    method: "POST",
    credentials: "include",
  });
}

export async function listUsers() {
  const res = await fetch(`${ADMIN_URL}/users`, { credentials: "include" });
  if (!res.ok) throw new Error("fetch_failed");
  return res.json();
}

export async function createUser(
  name: string,
  email: string,
  password: string,
  role: string
) {
  const res = await fetch(`${ADMIN_URL}/users`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ name, email, password, role }),
  });

  if (res.status === 409) throw new Error("user_exists");
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || "create_failed");
  }
  return res.json();
}

export async function updateUser(
  id: number,
  name: string,
  email: string,
  password: string,
  role: string
) {
  const payload = { name, email, role, ...(password ? { password } : {}) };

  const res = await fetch(`${ADMIN_URL}/users/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify(payload),
  });

  if (res.status === 409) throw new Error("user_exists");
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || "update_failed");
  }
  return res.json();
}

export async function deleteUser(id: number) {
  const res = await fetch(`${ADMIN_URL}/users/${id}`, {
    method: "DELETE",
    credentials: "include",
  });

  if (!res.ok && res.status !== 204) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || "delete_failed");
  }
}
