"use client";
import { useState, useEffect, useCallback } from "react";
import { useRouter } from "next/navigation";
import { api, getUserRole } from "../../lib/api";
import Link from "next/link";
import TodoCard from "../components/TodoCard";
import TodoModal from "../components/TodoModal";
import ConfirmModal from "../components/ConfirmModal";

const PAGE_SIZE = 5;

export default function Dashboard() {
    const router = useRouter();

    // Auth
    const [username, setUsername] = useState("");
    const [role, setRole] = useState(null);

    // Todos
    const [todos, setTodos] = useState([]);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [page, setPage] = useState(0);
    const [loading, setLoading] = useState(true);

    // Search / filter
    const [searchTitle, setSearchTitle] = useState("");
    const [searchPriority, setSearchPriority] = useState("");
    const [isSearching, setIsSearching] = useState(false);
    const [searchResults, setSearchResults] = useState(null);

    // Modals
    const [todoModal, setTodoModal] = useState({ open: false, data: null });
    const [deleteModal, setDeleteModal] = useState({ open: false, todo: null });
    const [deleteAccountModal, setDeleteAccountModal] = useState(false);
    const [modalLoading, setModalLoading] = useState(false);

    // UI Extras
    const [toast, setToast] = useState({ show: false, message: "", type: "success" });
    const [userInfoModalOpen, setUserInfoModalOpen] = useState(false);

    const showToast = (message, type = "success") => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: "", type: "" }), 3000);
    };

    // Sidebar
    const [sidebarOpen, setSidebarOpen] = useState(false);

    // Stats (from all todos)
    const [stats, setStats] = useState({ total: 0, completed: 0, pending: 0 });

    const loadStats = useCallback(async () => {
        try {
            const all = await api.get("/user-todos");
            const completed = all.filter((t) => t.completed).length;
            setStats({ total: all.length, completed, pending: all.length - completed });
        } catch {
            // silently fail
        }
    }, []);

    // Redirect if not authenticated
    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            router.replace("/login");
            return;
        }

        const userRole = getUserRole();
        if (userRole) {
            setRole(userRole);
        }

        api.get("/current-user").then(setUsername).catch(() => {
            localStorage.removeItem("token");
            router.replace("/login");
        });
        loadStats();
    }, [router, loadStats]);

    const loadTodos = useCallback(async (currentPage) => {
        setLoading(true);
        try {
            const data = await api.get(`/todos?page=${currentPage}&size=${PAGE_SIZE}`);
            setTodos(data.todos || []);
            setTotalPages(data.totalPages || 0);
            setTotalElements(data.totalItems || 0);
        } catch (err) {
            if (err.message?.includes("401")) {
                localStorage.removeItem("token");
                router.replace("/login");
            }
        } finally {
            setLoading(false);
        }
    }, [router]);

    useEffect(() => {
        if (!isSearching) {
            loadTodos(page);
        }
    }, [page, isSearching, loadTodos]);

    const handleSearch = async () => {
        if (!searchTitle && !searchPriority) {
            setIsSearching(false);
            setSearchResults(null);
            loadTodos(0);
            return;
        }
        setLoading(true);
        setIsSearching(true);
        try {
            const params = new URLSearchParams();
            if (searchTitle) params.append("title", searchTitle);
            if (searchPriority) params.append("priority", searchPriority);
            const results = await api.get(`/todos/search?${params}`);
            setSearchResults(results);
        } catch {
            setSearchResults([]);
        } finally {
            setLoading(false);
        }
    };

    const clearSearch = () => {
        setSearchTitle("");
        setSearchPriority("");
        setIsSearching(false);
        setSearchResults(null);
        setPage(0);
        loadTodos(0);
    };

    const handleCreateTodo = async (payload) => {
        setModalLoading(true);
        try {
            const newTodo = await api.post("/todos", payload);
            setTodos(prev => [newTodo, ...prev]);
            setTodoModal({ open: false, data: null });
            api.get("/user-todos").then(all => {
                const completed = all.filter((t) => t.completed).length;
                setStats({ total: all.length, completed, pending: all.length - completed });
            }).catch(() => { });
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
            const updatedTodo = await api.put(`/todos/${todoModal.data.id}`, payload);
            setTodos(prev => prev.map(t => t.id === todoModal.data.id ? updatedTodo : t));
            if (searchResults) {
                setSearchResults(prev => prev.map(t => t.id === todoModal.data.id ? updatedTodo : t));
            }
            setTodoModal({ open: false, data: null });
            api.get("/user-todos").then(all => {
                const completed = all.filter((t) => t.completed).length;
                setStats({ total: all.length, completed, pending: all.length - completed });
            }).catch(() => { });
            showToast("Todo updated successfully");
        } catch (err) {
            showToast(err.message, "error");
        } finally {
            setModalLoading(false);
        }
    };

    const handleToggleComplete = async (todo) => {
        const updatedStatus = !todo.completed;
        const previousTodos = [...todos];
        const previousSearchResults = searchResults ? [...searchResults] : null;

        setTodos(prev => prev.map(t => t.id === todo.id ? { ...t, completed: updatedStatus } : t));
        if (searchResults) {
            setSearchResults(prev => prev.map(t => t.id === todo.id ? { ...t, completed: updatedStatus } : t));
        }

        try {
            await api.put(`/todos/${todo.id}`, { ...todo, completed: updatedStatus });
            api.get("/user-todos").then(all => {
                const completed = all.filter((t) => t.completed).length;
                setStats({ total: all.length, completed, pending: all.length - completed });
            }).catch(() => { });
            showToast(`Todo marked as ${updatedStatus ? 'completed' : 'pending'}`);
        } catch (err) {
            setTodos(previousTodos);
            if (previousSearchResults) setSearchResults(previousSearchResults);
            showToast(err.message, "error");
        }
    };

    const handleDeleteTodo = async () => {
        setModalLoading(true);
        try {
            await api.delete(`/todos/${deleteModal.todo.id}`);
            const deletedId = deleteModal.todo.id;
            setTodos(prev => prev.filter(t => t.id !== deletedId));
            if (searchResults) {
                setSearchResults(prev => prev.filter(t => t.id !== deletedId));
            }
            setDeleteModal({ open: false, todo: null });
            api.get("/user-todos").then(all => {
                const completed = all.filter((t) => t.completed).length;
                setStats({ total: all.length, completed, pending: all.length - completed });
            }).catch(() => { });
            showToast("Todo deleted successfully");
        } catch (err) {
            showToast(err.message, "error");
        } finally {
            setModalLoading(false);
        }
    };

    const handleDeleteAccount = async (password) => {
        setModalLoading(true);
        try {
            await api.delete("/users/me", { currentPassword: password });
            localStorage.removeItem("token");
            router.replace("/login");
        } catch (err) {
            alert(err.message);
        } finally {
            setModalLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("token");
        router.replace("/login");
    };

    const displayedTodos = isSearching ? searchResults || [] : todos;

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
                            background: "var(--accent)",
                            borderRadius: 10,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            fontSize: 16,
                            boxShadow: "0 2px 12px var(--accent-glow)",
                        }}
                    >
                        ✓
                    </div>
                    <span style={{ fontWeight: 700, fontSize: 18, color: "#f1f1f1" }}>
                        TodoApp
                    </span>
                </div>

                <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                    {username && (
                        <div
                            onClick={() => setUserInfoModalOpen(true)}
                            style={{
                                fontSize: 13,
                                color: "#a0a0c0",
                                display: "flex",
                                alignItems: "center",
                                gap: 6,
                                cursor: "pointer",
                                padding: "4px 8px",
                                borderRadius: 8,
                                transition: "background 0.2s"
                            }}
                            onMouseEnter={e => e.currentTarget.style.background = "rgba(255,255,255,0.05)"}
                            onMouseLeave={e => e.currentTarget.style.background = "transparent"}
                        >
                            <span
                                style={{
                                    width: 28,
                                    height: 28,
                                    borderRadius: "50%",
                                    background: "rgba(16,185,129,0.1)",
                                    border: "1px solid rgba(16,185,129,0.3)",
                                    display: "inline-flex",
                                    alignItems: "center",
                                    justifyContent: "center",
                                    fontSize: 12,
                                    color: "var(--accent-light)",
                                    fontWeight: 700,
                                }}
                            >
                                {username[0]?.toUpperCase()}
                            </span>
                            <span style={{ display: "inline" }}>
                                {username}
                            </span>
                        </div>
                    )}

                    <button
                        onClick={() => setDeleteAccountModal(true)}
                        className="btn-ghost"
                        style={{ padding: "6px 12px", fontSize: 12 }}
                    >
                        ⚙️ Account
                    </button>

                    {role === "ADMIN" && (
                        <Link
                            href="/admin"
                            className="btn-ghost"
                            style={{ padding: "6px 12px", fontSize: 12, textDecoration: "none", display: "inline-block" }}
                        >
                            🛡️ Admin Panel
                        </Link>
                    )}

                    <button
                        onClick={handleLogout}
                        className="btn-ghost"
                        style={{ padding: "6px 12px", fontSize: 12 }}
                    >
                        Sign out
                    </button>
                </div>
            </nav>

            {/* Main content */}
            <main style={{ maxWidth: 760, margin: "0 auto", padding: "32px 16px 80px" }}>
                {/* Header + stats */}
                <div className="animate-fadeInUp" style={{ marginBottom: 28 }}>
                    <h1 style={{ fontSize: 28, fontWeight: 800, color: "#f1f1f1", marginBottom: 6 }}>
                        My Tasks
                    </h1>
                    <p style={{ fontSize: 14, color: "#a0a0c0", marginBottom: 20 }}>
                        {username ? `Welcome back, ${username}!` : "Here are your todos for today."}
                    </p>

                    {/* Stats row */}
                    <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: 12 }}>
                        {[
                            { label: "Total", value: stats.total, color: "var(--accent-light)", icon: "📋" },
                            { label: "Completed", value: stats.completed, color: "#4ade80", icon: "✅" },
                            { label: "Pending", value: stats.pending, color: "#fbbf24", icon: "⏳" },
                        ].map((s) => (
                            <div
                                key={s.label}
                                className="glass"
                                style={{
                                    padding: "14px 16px",
                                    borderRadius: 12,
                                    textAlign: "center",
                                }}
                            >
                                <div style={{ fontSize: 20, marginBottom: 4 }}>{s.icon}</div>
                                <div style={{ fontSize: 24, fontWeight: 800, color: s.color }}>
                                    {s.value}
                                </div>
                                <div style={{ fontSize: 12, color: "#7070a0", marginTop: 2 }}>{s.label}</div>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Search & Filter */}
                <div
                    className="glass animate-fadeInUp"
                    style={{ padding: "16px", marginBottom: 20, borderRadius: 12 }}
                >
                    <div style={{ display: "flex", gap: 10, flexWrap: "wrap" }}>
                        <input
                            className="input-field"
                            type="text"
                            placeholder="🔍 Search by title…"
                            value={searchTitle}
                            onChange={(e) => setSearchTitle(e.target.value)}
                            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                            style={{ flex: "2 1 180px" }}
                        />
                        <select
                            className="input-field"
                            value={searchPriority}
                            onChange={(e) => setSearchPriority(e.target.value)}
                            style={{ flex: "1 1 120px" }}
                        >
                            <option value="">All Priorities</option>
                            <option value="HIGH">🔴 HIGH</option>
                            <option value="MEDIUM">🟡 MEDIUM</option>
                            <option value="LOW">🟢 LOW</option>
                        </select>
                        <button
                            onClick={handleSearch}
                            style={{
                                padding: "10px 18px",
                                background: "var(--accent)",
                                color: "white",
                                border: "none",
                                borderRadius: 10,
                                cursor: "pointer",
                                fontWeight: 600,
                                fontSize: 13,
                                fontFamily: "inherit",
                                whiteSpace: "nowrap",
                                boxShadow: "0 2px 12px var(--accent-glow)",
                            }}
                        >
                            Search
                        </button>
                        {isSearching && (
                            <button onClick={clearSearch} className="btn-ghost" style={{ padding: "10px 14px" }}>
                                Clear
                            </button>
                        )}
                    </div>
                    {isSearching && searchResults && (
                        <p style={{ fontSize: 12, color: "#7070a0", marginTop: 10 }}>
                            Found {searchResults.length} result{searchResults.length !== 1 ? "s" : ""}
                        </p>
                    )}
                </div>

                {/* Todo list */}
                <div style={{ position: "relative", minHeight: "200px" }}>
                    {loading && (
                        <div style={{
                            position: "absolute",
                            top: 0, left: 0, right: 0, bottom: 0,
                            backgroundColor: "rgba(15, 17, 21, 0.4)",
                            backdropFilter: "blur(4px)",
                            zIndex: 10,
                            display: "flex",
                            justifyContent: "center",
                            alignItems: "center",
                            borderRadius: 16
                        }}>
                            <div style={{
                                width: 40,
                                height: 40,
                                border: "3px solid rgba(16,185,129,0.2)",
                                borderTopColor: "var(--accent-light)",
                                borderRadius: "50%",
                                animation: "spin 0.8s linear infinite",
                            }} />
                            <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
                        </div>
                    )}

                    <div style={{ opacity: loading ? 0.6 : 1, transition: "opacity 0.2s", pointerEvents: loading ? "none" : "auto" }}>
                        {displayedTodos.length === 0 ? (
                            <div
                                className="glass"
                                style={{ padding: "60px 24px", textAlign: "center", borderRadius: 16 }}
                            >
                                <div style={{ fontSize: 48, marginBottom: 16, opacity: 0.5 }}>📭</div>
                                <h3 style={{ fontSize: 18, fontWeight: 600, color: "#f1f1f1", marginBottom: 8 }}>
                                    {isSearching ? "No results found" : "No todos yet"}
                                </h3>
                                <p style={{ fontSize: 14, color: "#7070a0" }}>
                                    {isSearching
                                        ? "Try adjusting your search terms."
                                        : 'Click "+ New Todo" to create your first task!'}
                                </p>
                            </div>
                        ) : (() => {
                            const pendingTodos = displayedTodos.filter(t => !t.completed);
                            const completedTodos = displayedTodos.filter(t => t.completed);

                            return (
                                <div style={{ display: "flex", flexDirection: "column", gap: 24 }}>
                                    {/* Pending container */}
                                    {pendingTodos.length > 0 && (
                                        <div className="glass" style={{ padding: "16px 20px", borderRadius: 16 }}>
                                            <h3 style={{ marginBottom: 12, fontSize: 16, color: "#f1f1f1", fontWeight: 600, display: "flex", alignItems: "center", gap: 8 }}>
                                                <span style={{ fontSize: 20 }}>⏳</span> Pending Tasks
                                            </h3>
                                            <div style={{ display: "flex", flexDirection: "column", gap: 10, maxHeight: 520, overflowY: "auto", paddingRight: 4 }}>
                                                {pendingTodos.map((todo, i) => (
                                                    <div key={todo.id} style={{ animationDelay: `${i * 60}ms` }}>
                                                        <TodoCard
                                                            todo={todo}
                                                            onEdit={(t) => setTodoModal({ open: true, data: t })}
                                                            onDelete={(t) => setDeleteModal({ open: true, todo: t })}
                                                            onToggle={handleToggleComplete}
                                                        />
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    )}

                                    {/* Completed container */}
                                    {completedTodos.length > 0 && (
                                        <div className="glass" style={{ padding: "16px 20px", borderRadius: 16, background: "rgba(16,185,129,0.03)", borderColor: "rgba(16,185,129,0.15)" }}>
                                            <h3 style={{ marginBottom: 12, fontSize: 16, color: "#4ade80", fontWeight: 600, display: "flex", alignItems: "center", gap: 8 }}>
                                                <span style={{ fontSize: 20 }}>✅</span> Completed Tasks
                                            </h3>
                                            <div style={{ display: "flex", flexDirection: "column", gap: 10, maxHeight: 520, overflowY: "auto", paddingRight: 4 }}>
                                                {completedTodos.map((todo, i) => (
                                                    <div key={todo.id} style={{ animationDelay: `${i * 60}ms` }}>
                                                        <TodoCard
                                                            todo={todo}
                                                            onEdit={(t) => setTodoModal({ open: true, data: t })}
                                                            onDelete={(t) => setDeleteModal({ open: true, todo: t })}
                                                            onToggle={handleToggleComplete}
                                                        />
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    )}
                                </div>
                            );
                        })()}
                    </div>
                </div>

                {/* Pagination (only when not searching) */}
                {!isSearching && totalPages > 1 && (
                    <div
                        style={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            gap: 8,
                            marginTop: 28,
                        }}
                    >
                        <button
                            onClick={() => setPage((p) => Math.max(0, p - 1))}
                            disabled={page === 0}
                            className="btn-ghost"
                            style={{
                                padding: "8px 14px",
                                opacity: page === 0 ? 0.4 : 1,
                                cursor: page === 0 ? "not-allowed" : "pointer",
                            }}
                        >
                            ← Prev
                        </button>

                        <div style={{ display: "flex", gap: 6 }}>
                            {Array.from({ length: totalPages }, (_, i) => (
                                <button
                                    key={i}
                                    onClick={() => setPage(i)}
                                    style={{
                                        width: 34,
                                        height: 34,
                                        borderRadius: 8,
                                        border:
                                            i === page
                                                ? "none"
                                                : "1px solid rgba(255,255,255,0.1)",
                                        background:
                                            i === page
                                                ? "var(--accent)"
                                                : "transparent",
                                        color: i === page ? "white" : "#a0a0c0",
                                        cursor: "pointer",
                                        fontWeight: i === page ? 700 : 400,
                                        fontFamily: "inherit",
                                        fontSize: 13,
                                        boxShadow:
                                            i === page ? "0 2px 10px var(--accent-glow)" : "none",
                                        transition: "all 0.2s",
                                    }}
                                >
                                    {i + 1}
                                </button>
                            ))}
                        </div>

                        <button
                            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                            disabled={page >= totalPages - 1}
                            className="btn-ghost"
                            style={{
                                padding: "8px 14px",
                                opacity: page >= totalPages - 1 ? 0.4 : 1,
                                cursor: page >= totalPages - 1 ? "not-allowed" : "pointer",
                            }}
                        >
                            Next →
                        </button>
                    </div>
                )}

                {!isSearching && totalElements > 0 && (
                    <p style={{ textAlign: "center", fontSize: 12, color: "#5050a0", marginTop: 12 }}>
                        Showing {Math.min(page * PAGE_SIZE + 1, totalElements)}–
                        {Math.min((page + 1) * PAGE_SIZE, totalElements)} of {totalElements} todos
                    </p>
                )}
            </main>

            {/* Floating create button */}
            <button
                onClick={() => setTodoModal({ open: true, data: null })}
                title="Create new todo"
                style={{
                    position: "fixed",
                    bottom: 28,
                    right: 28,
                    width: 56,
                    height: 56,
                    borderRadius: "50%",
                    background: "var(--accent)",
                    color: "white",
                    border: "none",
                    fontSize: 26,
                    cursor: "pointer",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    boxShadow: "0 4px 24px var(--accent-glow)",
                    zIndex: 40,
                    transition: "transform 0.2s, box-shadow 0.2s",
                    animation: "pulse-glow 2.5s ease infinite",
                }}
                onMouseEnter={(e) => {
                    e.currentTarget.style.transform = "scale(1.1)";
                    e.currentTarget.style.boxShadow = "0 6px 30px var(--accent-glow)";
                }}
                onMouseLeave={(e) => {
                    e.currentTarget.style.transform = "scale(1)";
                    e.currentTarget.style.boxShadow = "0 4px 24px var(--accent-glow)";
                }}
            >
                +
            </button>

            {/* Modals */}
            <TodoModal
                isOpen={todoModal.open}
                initialData={todoModal.data}
                onClose={() => setTodoModal({ open: false, data: null })}
                onSave={todoModal.data ? handleUpdateTodo : handleCreateTodo}
                loading={modalLoading}
            />

            <ConfirmModal
                isOpen={deleteModal.open}
                onClose={() => setDeleteModal({ open: false, todo: null })}
                onConfirm={handleDeleteTodo}
                title="Delete Todo"
                message={`Are you sure you want to delete "${deleteModal.todo?.title}"? This cannot be undone.`}
                loading={modalLoading}
            />

            <ConfirmModal
                isOpen={deleteAccountModal}
                onClose={() => setDeleteAccountModal(false)}
                onConfirm={handleDeleteAccount}
                title="Delete Account"
                message="This will permanently delete your account and all your todos. This action is irreversible."
                loading={modalLoading}
                requirePassword
            />

            {/* User Info Modal */}
            {userInfoModalOpen && (
                <div className="modal-overlay" style={{ zIndex: 200 }} onClick={() => setUserInfoModalOpen(false)}>
                    <div className="glass animate-fadeInUp" style={{ width: "100%", maxWidth: 400, padding: 24 }} onClick={e => e.stopPropagation()}>
                        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
                            <h3 style={{ fontSize: 18, fontWeight: 700 }}>My Information</h3>
                            <button onClick={() => setUserInfoModalOpen(false)} className="btn-ghost" style={{ padding: 4 }}>✕</button>
                        </div>
                        <div style={{ display: "flex", flexDirection: "column", gap: 12 }}>
                            <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid rgba(255,255,255,0.08)", paddingBottom: 8 }}>
                                <span style={{ color: "#a0a0c0", fontSize: 13 }}>Username</span>
                                <span style={{ fontWeight: 600 }}>{username}</span>
                            </div>
                            <div style={{ display: "flex", justifyContent: "space-between", borderBottom: "1px solid rgba(255,255,255,0.08)", paddingBottom: 8 }}>
                                <span style={{ color: "#a0a0c0", fontSize: 13 }}>Role</span>
                                <span style={{ fontWeight: 600, color: role === "ADMIN" ? "#ef4444" : "#10b981" }}>{role || "USER"}</span>
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
                    animation: "fadeInUp 0.3s ease-out"
                }}>
                    {toast.message}
                </div>
            )}
        </div>
    );
}
