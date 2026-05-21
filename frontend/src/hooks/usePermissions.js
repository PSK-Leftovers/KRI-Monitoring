export function usePermissions() {
    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const role = user.role;

    const isAdmin = role === "ADMIN";

    return {
        role,
        isAdmin,
        canModify: isAdmin,
    };
}