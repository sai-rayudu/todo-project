"use client";
import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { api, getUserRole } from "../../lib/api";
import Link from "next/link";
import TodoCard from "../components/TodoCard";
import TodoModal from "../components/TodoModal";
import ConfirmModal from "../components/ConfirmModal";

export default function AdminDashboard() {
    const router = useRouter();
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    // UI state
    const [selectedUser, setSelectedUser] = useState(null);
    const [userTodos, setUserTodos] = useState([]);
    const [viewTodosModal, setViewTodosModal] = useState(false);
    const [todosLoading, setTodosLoading] = useState(false);

    // Modals
    const [deleteModal, setDeleteModal] = useState({ open: false, user: null });
    const [todoModal, setTodoModal] = useState({ open: false, data: null });
    const [deleteTodoModal, setDeleteTodoModal] = useState({ open: false, todo: null });
    const [modalLoading, setModalLoading] = useState(false);
    const [toast, setToast] = useState({ show: false, message: "", type: "success" });
    const [roleModal, setRoleModal] = useState({ open: false, user: null });
    const [userInfoModal, setUserInfoModal] = useState({ open: false, user: null });

    const showToast = (message, type = "success") => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: "", type: "" }), 3000);
    };

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            router.replace("/login");
            return;
        }

        const role = getUserRole();
        if (role !== "ADMIN") {
            router.replace("/dashboard");
            return;
        }

        loadUsers();
    }, [router]);

    const loadUsers = async () => {
        setLoading(true);
        try {
            const data = await api.get("/admin/users");
            setUsers(data.filter(u => u.role !== 'ADMIN'));
        } catch (err) {
            setError(err.message || "Failed to load users");
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteUser = async () => {
        setModalLoading(true);
        try {
            await api.delete(`/admin/users/${deleteModal.user.id}`);
            setDeleteModal({ open: false, user: null });
            loadUsers();
        } catch (err) {
            alert(err.message);
        } finally {
            setModalLoading(false);
        }
    };

    const handleRoleChange = async (newRole) => {
        if (!roleModal.user) return;
        setModalLoading(true);
        try {
            await api.patch(`/admin/users/${roleModal.user.id}/role`, { role: newRole });
            setUsers(prev => prev.filter(u => u.id !== roleModal.user.id));
            setRoleModal({ open: false, user: null });
            showToast(`Role changed to ${newRole} for ${roleModal.user.username}`);
        } catch (err) {
            setRoleModal({ open: false, user: null });
            showToast(err.message, "error");
        } finally {
            setModalLoading(false);
        }
    };

    const handleViewTodos = async (user, silent = false) => {
        if (!silent) {
            setSelectedUser(user);
            setViewTodosModal(true);
            setTodosLoading(true);
        }
        try {
            const details = await api.get(`/admin/user/search?search=${user.username}`);
            setUserTodos(details.todos || []);
        } catch (err) {
            if (!silent) alert(err.message);
            if (!silent) setUserTodos([]);
        } finally {
            if (!silent) setTodosLoading(false);
        }
    };

    const handleCreateTodo = async (payload) => {
        setModalLoading(true);
        try {
            await api.post(`/admin/users/${selectedUser.id}/todos`, payload);
            setTodoModal({ open: false, data: null });
            handleViewTodos(selectedUser, true);
            showToast("Todo created successfully");
        } catch (err) {
            showToast(err.message, "error");
        } finally {
            setModalLoading(false);
        }
    };

    const handleUpdateTodo = async (payload) => {
        setModalLoading(true);
        try {
            await api.put(`/admin/todos/${todoModal.data.id}`, payload);
            setTodoModal({ open: false, data: null });
            handleViewTodos(selectedUser, true);
            showToast("Todo updated successfully");
        } catch (err) {
            showToast(err.message, "error");
        } finally {
            setModalLoading(false);
        }
    };

    const handleDeleteTodo = async () => {
        setModalLoading(true);
        try {
            await api.delete(`/admin/todos/${deleteTodoModal.todo.id}`);
            setDeleteTodoModal({ open: false, todo: null });
            handleViewTodos(selectedUser, true);
            showToast("Todo deleted successfully");
        } catch (err) {
            showToast(err.message, "error");
        } finally {
            setModalLoading(false);
        }
    };

    // Toggle completion for admin uses update endpoint
    const handleToggleComplete = async (todo) => {
        // Optimistic update
        const updatedStatus = !todo.completed;
        setUserTodos(prev => prev.map(t => t.id === todo.id ? { ...t, completed: updatedStatus } : t));
        try {
            await api.put(`/admin/todos/${todo.id}`, { ...todo, completed: updatedStatus });
        } catch (err) {
            // Revert on error
            setUserTodos(prev => prev.map(t => t.id === todo.id ? { ...t, completed: !updatedStatus } : t));
            showToast(err.message, "error");
        }
    };

    return (
        <div style={{ minHeight: "100vh", position: "relative", zIndex: 1 }}>
            {/* Navbar */}
            <nav
                style={{
                    position: "sticky",
                    top: 0,
                    zIndex: 50,
                    background: "rgba(15,12,41,0.85)",
                    backdropFilter: "blur(16px)",
                    borderBottom: "1px solid rgba(255,255,255,0.08)",
                    padding: "0 24px",
                    height: 64,
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                }}
            >
                <div style={{ display: "flex", alignItems: "center", gap: 10 }}>
                    <div
                        style={{
                            width: 34,
                            height: 34,
                            background: "linear-gradient(135deg, #ef4444, #b91c1c)",
                            borderRadius: 10,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            fontSize: 16,
                            boxShadow: "0 2px 12px rgba(239,68,68,0.4)",
                        }}
                    >
                        🛡️
                    </div>
                    <span style={{ fontWeight: 700, fontSize: 18, color: "#f1f1f1" }}>
                        Admin Portal
                    </span>
                </div>

                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                    <Link
                        href="/dashboard"
                        className="btn-ghost"
                        style={{ padding: "6px 12px", fontSize: 12, textDecoration: "none" }}
                    >
                        ← Back to Dashboard
                    </Link>
                </div>
            </nav>

            {/* Main content */}
            <main style={{ maxWidth: 860, margin: "0 auto", padding: "32px 16px 80px" }}>
                <div className="animate-fadeInUp" style={{ marginBottom: 28 }}>
                    <h1 style={{ fontSize: 28, fontWeight: 800, color: "#f1f1f1", marginBottom: 6 }}>
                        User Management
                    </h1>
                    <p style={{ fontSize: 14, color: "#a0a0c0", marginBottom: 20 }}>
                        Control user accounts, manage roles, and monitor assigned tasks.
                    </p>

                    <div style={{ display: "grid", gridTemplateColumns: "1fr", gap: 12, marginBottom: 24 }}>
                        <div
                            className="glass"
                            style={{ padding: "14px 16px", borderRadius: 12, textAlign: "center" }}
                        >
                            <div style={{ fontSize: 20, marginBottom: 4 }}>👥</div>
                            <div style={{ fontSize: 24, fontWeight: 800, color: "#ef4444" }}>
                                {users.length}
                            </div>
                            <div style={{ fontSize: 12, color: "#7070a0", marginTop: 2 }}>Total Users</div>
                        </div>
                    </div>
                </div>

                {loading ? (
                    <div style={{ display: "flex", justifyContent: "center", padding: "60px 0" }}>
                        <div
                            style={{
                                width: 40,
                                height: 40,
                                border: "3px solid rgba(239,68,68,0.2)",
                                borderTopColor: "#ef4444",
                                borderRadius: "50%",
                                animation: "spin 0.8s linear infinite",
                            }}
                        />
                        <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
                    </div>
                ) : (
                    <div className="glass animate-fadeInUp" style={{ borderRadius: 16, overflow: "hidden" }}>
                        <div style={{ padding: "20px", display: "grid", gridTemplateColumns: "1fr 2fr 1.5fr 1.5fr 1fr", gap: 10, borderBottom: "1px solid rgba(255,255,255,0.08)", background: "rgba(0,0,0,0.2)", fontSize: 13, fontWeight: 600, color: "#a0a0c0" }}>
                            <div>ID</div>
                            <div>Username</div>
                            <div>Email</div>
                            <div>Role</div>
                            <div style={{ textAlign: "right" }}>Actions</div>
                        </div>
                        {users.map((user, i) => (
                            <div
                                key={user.id}
                                style={{
                                    padding: "20px",
                                    display: "grid",
                                    gridTemplateColumns: "1fr 2fr 1.5fr 1.5fr 1fr",
                                    gap: 10,
                                    borderBottom: "1px solid rgba(255,255,255,0.04)",
                                    alignItems: "center",
                                    animationDelay: `${i * 40}ms`
                                }}
                                className="animate-fadeInUp"
                            >
                                <div style={{ fontSize: 13, color: "#7070a0" }}>#{user.id}</div>
                                <div
                                    style={{ fontSize: 14, fontWeight: 500, cursor: "pointer", color: "#3b82f6", textDecoration: "underline" }}
                                    onClick={() => setUserInfoModal({ open: true, user })}
                                >
                                    {user.username}
                                </div>
                                <div style={{ fontSize: 13, color: "#a0a0c0" }}>{user.email || "N/A"}</div>
                                <div>
                                    <button
                                        onClick={() => setRoleModal({ open: true, user })}
                                        className="btn-ghost"
                                        style={{ padding: "6px 10px", fontSize: 12, height: "auto", border: "1px solid rgba(255,255,255,0.1)" }}
                                    >
                                        Change Role
                                    </button>
                                </div>
                                <div style={{ display: "flex", gap: 8, justifyContent: "flex-end" }}>
                                    <button
                                        onClick={() => handleViewTodos(user)}
                                        className="btn-ghost"
                                        style={{ padding: "6px", fontSize: 14, height: 32, width: 32, display: "flex", alignItems: "center", justifyContent: "center" }}
                                        title="View User Todos"
                                    >
                                        📋
                                    </button>
                                    <button
                                        onClick={() => setDeleteModal({ open: true, user: user })}
                                        className="btn-ghost"
                                        style={{ padding: "6px", fontSize: 14, color: "#ef4444", border: "1px solid rgba(239, 68, 68, 0.2)", height: 32, width: 32, display: "flex", alignItems: "center", justifyContent: "center" }}
                                        title="Delete User"
                                    >
                                        🗑️
                                    </button>
                                </div>
                            </div>
                        ))}
                        {users.length === 0 && (
                            <div style={{ padding: 40, textAlign: "center", color: "#a0a0c0", fontSize: 14 }}>
                                No users found.
                            </div>
                        )}
                    </div>
                )}
            </main>

            {/* View Todos Modal / Overlay */}
            {viewTodosModal && selectedUser && (
                <div className="modal-overlay" style={{ zIndex: 100 }} onClick={(e) => e.target === e.currentTarget && setViewTodosModal(false)}>
                    <div className="glass animate-fadeInUp" style={{ width: "100%", maxWidth: 680, maxHeight: "85vh", display: "flex", flexDirection: "column", padding: 0 }}>

                        <div style={{ padding: "24px", borderBottom: "1px solid rgba(255,255,255,0.08)", display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                            <div>
                                <h3 style={{ fontSize: 18, fontWeight: 700, color: "#f1f1f1" }}>{selectedUser.username}&apos;s Todos</h3>
                                <p style={{ fontSize: 13, color: "#a0a0c0", marginTop: 4 }}>Manage tasks assigned to this user</p>
                            </div>
                            <div style={{ display: "flex", gap: 12 }}>
                                <button onClick={() => setTodoModal({ open: true, data: null })} className="btn-primary" style={{ padding: "8px 16px", fontSize: 13, width: "auto" }}>
                                    + Add Task
                                </button>
                                <button onClick={() => setViewTodosModal(false)} className="btn-ghost" style={{ padding: "8px 12px", border: "none" }}>✕</button>
                            </div>
                        </div>

                        <div style={{ padding: "24px", overflowY: "auto", flex: 1 }}>
                            {todosLoading ? (
                                <div style={{ textAlign: "center", padding: 40, color: "#a0a0c0" }}>Loading items...</div>
                            ) : userTodos.length === 0 ? (
                                <div style={{ textAlign: "center", padding: 40, color: "#7070a0" }}>This user has no todos.</div>
                            ) : (
                                <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                                    {userTodos.map((todo) => (
                                        <TodoCard
                                            key={todo.id}
                                            todo={todo}
                                            onEdit={(t) => setTodoModal({ open: true, data: t })}
                                            onDelete={(t) => setDeleteTodoModal({ open: true, todo: t })}
                                            onToggle={handleToggleComplete}
                                        />
                                    ))}
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {/* Reused Modals for Admin Actions */}
            <TodoModal
                isOpen={todoModal.open}
                initialData={todoModal.data}
                onClose={() => setTodoModal({ open: false, data: null })}
                onSave={todoModal.data ? handleUpdateTodo : handleCreateTodo}
                loading={modalLoading}
            />

            <ConfirmModal
                isOpen={deleteTodoModal.open}
                onClose={() => setDeleteTodoModal({ open: false, todo: null })}
                onConfirm={handleDeleteTodo}
                title="Delete User's Todo"
                message={`Are you sure you want to delete this task?`}
                loading={modalLoading}
            />

            <ConfirmModal
                isOpen={deleteModal.open}
                onClose={() => setDeleteModal({ open: false, user: null })}
                onConfirm={handleDeleteUser}
                title="Delete User"
                message={`Are you sure you want to permanently delete user "${deleteModal.user?.username}"? All their data will be lost.`}
                loading={modalLoading}
            />

            {/* Role Change Modal */}
            {roleModal.open && roleModal.user && (
                <div className="modal-overlay" style={{ zIndex: 200 }}>
                    <div className="glass animate-fadeInUp" style={{ width: "100%", maxWidth: 400, padding: 24, textAlign: "center" }}>
                        <h3 style={{ fontSize: 18, fontWeight: 700, marginBottom: 12 }}>Change Role</h3>
                        <p style={{ fontSize: 14, color: "#a0a0c0", marginBottom: 24 }}>
                            Select the new role for <strong>{roleModal.user.username}</strong>:
                        </p>
                        <div style={{ display: "flex", gap: 12, justifyContent: "center", marginBottom: 24 }}>
                            <button
                                onClick={() => handleRoleChange("USER")}
                                className="btn-primary"
                                style={{ flex: 1 }}
                                disabled={modalLoading}
                            >
                                Make User
                            </button>
                            <button
                                onClick={() => handleRoleChange("ADMIN")}
                                className="btn-primary"
                                style={{ flex: 1, background: "linear-gradient(135deg, #ef4444, #b91c1c)" }}
                                disabled={modalLoading}
                            >
                                Make Admin
                            </button>
                        </div>
                        <button
                            onClick={() => setRoleModal({ open: false, user: null })}
                            className="btn-ghost"
                            style={{ width: "100%" }}
                        >
                            Cancel
                        </button>
                    </div>
                </div>
            )}

            {/* User Info Modal */}
            {userInfoModal.open && userInfoModal.user && (
                <div className="modal-overlay" style={{ zIndex: 200 }} onClick={() => setUserInfoModal({ open: false, user: null })}>
                    <div className="glass animate-fadeInUp" style={{ width: "100%", maxWidth: 400, padding: 24 }} onClick={e => e.stopPropagation()}>
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
                            <h3 style={{ fontSize: 18, fontWeight: 700 }}>User Information</h3>
                            <button onClick={() => setUserInfoModal({ open: false, user: null })} className="btn-ghost" style={{ padding: 4 }}>✕</button>
                        </div>
                        <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                            <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid rgba(255,255,255,0.08)", paddingBottom: 8 }}>
                                <span style={{ color: "#a0a0c0", fontSize: 13 }}>ID</span>
                                <span style={{ fontWeight: 600 }}>{userInfoModal.user.id}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid rgba(255,255,255,0.08)", paddingBottom: 8 }}>
                                <span style={{ color: "#a0a0c0", fontSize: 13 }}>Username</span>
                                <span style={{ fontWeight: 600 }}>{userInfoModal.user.username}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid rgba(255,255,255,0.08)", paddingBottom: 8 }}>
                                <span style={{ color: "#a0a0c0", fontSize: 13 }}>Email</span>
                                <span style={{ fontWeight: 600 }}>{userInfoModal.user.email || "N/A"}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid rgba(255,255,255,0.08)", paddingBottom: 8 }}>
                                <span style={{ color: "#a0a0c0", fontSize: 13 }}>Role</span>
                                <span style={{ fontWeight: 600, color: userInfoModal.user.role === "ADMIN" ? "#ef4444" : "#10b981" }}>{userInfoModal.user.role}</span>
                            </div>
                        </div>
                    </div>
                </div>
            )}

            {/* Toast Notification */}
            {toast.show && (
                <div style={{
                    position: "fixed",
                    bottom: 24,
                    right: 24,
                    zIndex: 9999,
                    padding: "12px 24px",
                    background: toast.type === "error" ? "rgba(220, 38, 38, 0.9)" : "rgba(16, 185, 129, 0.9)",
                    color: "white",
                    borderRadius: 8,
                    boxShadow: "0 4px 12px rgba(0,0,0,0.3)",
                    fontWeight: 500,
                    fontSize: 14,
                    animation: "fadeInUp 0.3s ease-out" // Will need to define or use existing fadeInUp
                }}>
                    {toast.message}
                </div>
            )}
        </div>
    );
}
